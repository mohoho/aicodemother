package com.qmruan.aicodemother.utils;

import jakarta.validation.constraints.AssertTrue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author: qmruan@trip.com
 * @Date: 2025/11/26 20:04
 */

class WebScreenshotUtilsTest {

    @Test
    void saveWebPageScreenshot() {
        String testUrl = "https://www.google.com";
        String s = WebScreenshotUtils.saveWebPageScreenshot(testUrl);
        Assertions.assertNotNull(s);
    }
}