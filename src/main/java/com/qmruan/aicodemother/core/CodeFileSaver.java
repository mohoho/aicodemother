package com.qmruan.aicodemother.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.qmruan.aicodemother.ai.model.HtmlCodeResult;
import com.qmruan.aicodemother.ai.model.MultiFileCodeResult;
import com.qmruan.aicodemother.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

@Deprecated
public class CodeFileSaver {

    // 文件保存根目录
    public static final String FILE_SAVE_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_output";

    /**
     *     保存HTML网页代码
      */

    public static File saveHtmlCodeResult(HtmlCodeResult htmlCodeResult) {
        String buildUniqueDir = buildUniqueDir(CodeGenTypeEnum.HTML.getValue());
        writeToFile(buildUniqueDir, "index.html", htmlCodeResult.getHtmlCode());
        return new File(buildUniqueDir);
    }

    /**
     * 保存多文件代码
     * @param multiFileCodeResult
     * @return
     */
    public static File saveMultiFileCodeResult(MultiFileCodeResult multiFileCodeResult) {
        String buildUniqueDir = buildUniqueDir(CodeGenTypeEnum.HTML.getValue());
        writeToFile(buildUniqueDir, "index.html", multiFileCodeResult.getHtmlCode());
        writeToFile(buildUniqueDir, "style.css", multiFileCodeResult.getCssCode());
        writeToFile(buildUniqueDir, "script.js", multiFileCodeResult.getJsCode());
        return new File(buildUniqueDir);
    }

    /**
     * 保存单个文件
     */
    public static void writeToFile(String dirPath, String fileName, String content) {
        String filePath = dirPath + File.separator + fileName;
        FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
    }

    /**
     * 构建文件的唯一路径(tmp/code_output/bizType_雪花 ID)
     * @param bizType
     * @return
     */
    public static String buildUniqueDir(String bizType) {
        String uniqueDir = FILE_SAVE_ROOT_DIR + File.separator + bizType + "_" + IdUtil.getSnowflakeNextIdStr();
        FileUtil.mkdir(uniqueDir);
        return uniqueDir;
    }

}
