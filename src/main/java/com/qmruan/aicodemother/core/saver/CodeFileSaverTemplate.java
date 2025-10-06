package com.qmruan.aicodemother.core.saver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.qmruan.aicodemother.exception.ErrorCode;
import com.qmruan.aicodemother.exception.ThrowUtils;
import com.qmruan.aicodemother.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * @Author: qmruan@trip.com
 * @Date: 2025/10/6 16:17
 */

public abstract class CodeFileSaverTemplate<T> {

    // 文件保存根目录
    public static final String FILE_SAVE_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_output";

    /**
     * 模板方法，保存代码的基本流程
     * @param result
     * @return
     */
    public final File saveCode(T result) {
        // 1. 验证输入
        validateInput(result);
        // 2. 构建唯一目录
        String baseDirPath = buildUniqueDir();
        // 3. 保存文件（具体实现交给子类）
        saveFiles(result, baseDirPath);
        // 4. 返回文件目录对象
        return new File(baseDirPath);
    }

    protected void validateInput(T result) {
        ThrowUtils.throwIf(result == null, ErrorCode.PARAMS_ERROR);
    }

    /**
     * 构建文件的唯一路径(tmp/code_output/bizType_雪花 ID)
     * @param
     * @return
     */
    protected String buildUniqueDir() {
        String bizType = getCodeType().getValue();
        String uniqueDir = FILE_SAVE_ROOT_DIR + File.separator + bizType + "_" + IdUtil.getSnowflakeNextIdStr();
        FileUtil.mkdir(uniqueDir);
        return uniqueDir;
    }

    /**
     * 保存单个文件
     */
    public final void writeToFile(String dirPath, String fileName, String content) {
        if (StrUtil.isNotBlank(content)) {
            String filePath = dirPath + File.separator + fileName;
            FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
        }
    }


    abstract CodeGenTypeEnum getCodeType();

    abstract void saveFiles(T result, String baseDirPath);

}
