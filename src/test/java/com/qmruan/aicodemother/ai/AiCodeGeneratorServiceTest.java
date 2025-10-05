package com.qmruan.aicodemother.ai;

import com.qmruan.aicodemother.ai.model.HtmlCodeResult;
import com.qmruan.aicodemother.ai.model.MultiFileCodeResult;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class AiCodeGeneratorServiceTest {

    @Resource
    AiCodeGeneratorService aiCodeGeneratorService;

    @Test
    void generateHtmlCode() {
        HtmlCodeResult res = aiCodeGeneratorService.generateHtmlCode("做个20行的博客");
        Assertions.assertNotNull(res);
    }

    @Test
    void generateMultiFileCode() {
        MultiFileCodeResult res = aiCodeGeneratorService.generateMultiFileCode("做个20行的博客");
        Assertions.assertNotNull(res);
    }
}