package com.office_management.auth_service.feign;


import com.office_management.auth_service.exception.errors.FeignServiceException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

@Component
public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        String message = "Remote service call failed: " + methodKey + " | HTTP " + response.status();
        return new FeignServiceException("Remote service unavailable: " + methodKey + " returned HTTP " + response.status());
    }
}