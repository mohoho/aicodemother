package com.qmruan.aicodemother.service;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @Author: qmruan@trip.com
 * @Date: 2025/12/19 17:14
 */

public interface ProjectDownloadService {
    void downloadProjectAsZip(String projectPath, String downloadFileName, HttpServletResponse response);
}
