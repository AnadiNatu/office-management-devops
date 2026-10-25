package com.office_management.comment_service.feign;

import com.office_management.comment_service.excption.errors.FeignServiceException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {

        int status = response.status();
        String downstreamMessage = extractResponseBody(response);

        log.error("Feign downstream failure | method={} | status={} | response={}", methodKey, status, downstreamMessage);

        return switch (status) {

            case 401 -> new FeignServiceException("Unauthorized request to downstream service" + buildDetails(downstreamMessage));

            case 403 -> new FeignServiceException("Forbidden request to downstream service" + buildDetails(downstreamMessage));

            case 404 -> new FeignServiceException("Requested resource was not found in downstream service" + buildDetails(downstreamMessage));

            case 408 -> new FeignServiceException("Downstream service request timed out" + buildDetails(downstreamMessage));

            case 429 -> new FeignServiceException("Downstream service rate limit exceeded" + buildDetails(downstreamMessage));

            default -> {
                if (status >= 500) {
                    yield new FeignServiceException("Downstream service is currently unavailable" + buildDetails(downstreamMessage));
                }

                yield new FeignServiceException("Downstream service request failed with HTTP " + status + buildDetails(downstreamMessage));
            }
        };
    }

    private String extractResponseBody(Response response) {

        if (response.body() == null) {
            return "";
        }
        try (InputStream inputStream = response.body().asInputStream()) {

            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8).trim();
            if (body.isBlank()) {
                return "";
            }

            /*
             * Prevent huge downstream error responses from
             * flooding the application logs.
             */
            if (body.length() > 1000) {
                body = body.substring(0, 1000);
            }
            return body;
        } catch (IOException ex) {
            log.warn("Unable to read Feign error response body", ex);
            return "";
        }
    }

    private String buildDetails(String body) {

        if (body == null || body.isBlank()) {
            return "";
        }

        return " | downstreamResponse=" + body;
    }
}