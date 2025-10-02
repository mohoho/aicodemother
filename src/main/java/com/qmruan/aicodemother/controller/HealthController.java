package com.qmruan.aicodemother.controller;

import com.qmruan.aicodemother.common.BaseResponse;
import com.qmruan.aicodemother.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: qmruan@trip.com
 * @Date: 2025/9/25 21:58
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping("/")
    public BaseResponse<String> healthCheck(){
        return ResultUtils.success("ok");
    }

}
