package com.office_management.comment_service.feign;


import com.office_management.comment_service.config.FeignConfig;
import com.office_management.comment_service.dto.feign.TaskSummaryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "task-service" , contextId = "commentTaskFeignClient" , configuration = FeignConfig.class)
public interface TaskFeignClient {

    @GetMapping("/api/tasks/{taskId}")
    TaskSummaryDTO getTaskById(@PathVariable("taskId") Long taskId);

}