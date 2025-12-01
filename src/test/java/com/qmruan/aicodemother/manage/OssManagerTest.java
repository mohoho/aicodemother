package com.qmruan.aicodemother.manage;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author: qmruan@trip.com
 * @Date: 2025/11/26 21:18
 */

@SpringBootTest
class OssManagerTest {

    @Resource
    OssManager ossManager;

    @Test
    void uploadFile() {
        String s = ossManager.uploadFile("123", new File("D:\\Users\\qmruan\\Desktop\\item\\aicodemother\\tmp\\screenshots\\2450179b\\71983_compressed.jpg"));
        Assertions.assertNotNull(s);
    }
}