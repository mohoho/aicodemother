package com.qmruan.aicodemother.manage;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectResult;
import com.qmruan.aicodemother.config.OssClientConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * COS对象存储管理器
 *
 * @author qmruan
 */
@Component
@Slf4j
public class OssManager {

    @Resource
    private OssClientConfig ossClientConfig;

    @Resource
    private OSS ossClient;

    /**
     * 上传对象
     *
     * @param key  唯一键
     * @param file 文件
     * @return 上传结果
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectResult putObjectResult = null;
        try {
            putObjectResult = ossClient.putObject(ossClientConfig.getBucket(), key, new FileInputStream(file));
        } catch (FileNotFoundException e) {
            log.error("file not found {}", key);
        }
        return putObjectResult;
    }

    /**
     * 上传文件到 COS 并返回访问 URL
     *
     * @param key  COS对象键（完整路径）
     * @param file 要上传的文件
     * @return 文件的访问URL，失败返回null
     */
    public String uploadFile(String key, File file) {
        // 上传文件
        PutObjectResult result = putObject(key, file);
        if (result != null) {
            // 构建访问URL
            String url = String.format("https://%s.%s/%s", ossClientConfig.getBucket(), ossClientConfig.getHost(), key);
            log.info("文件上传COS成功: {} -> {}", file.getName(), url);
            return url;
        } else {
            log.error("文件上传COS失败，返回结果为空");
            return null;
        }
    }
}
