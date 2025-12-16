package com.qmruan.aicodemother.service.impl;

import com.qmruan.aicodemother.service.ScreenshotService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author: qmruan@trip.com
 * @Date: 2025/12/16 20:25
 */

@SpringBootTest
class ScreenshotServiceImplTest {

    @Resource
    ScreenshotService screenshotService;

    @Test
    void generateAndUploadScreenshot() {
        String s = screenshotService.generateAndUploadScreenshot("https://localhost/2000905882252537856/#/");
        System.out.println(s);
        Assertions.assertNotNull(s);
    }
}