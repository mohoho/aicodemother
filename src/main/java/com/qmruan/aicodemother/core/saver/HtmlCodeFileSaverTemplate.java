package com.qmruan.aicodemother.core.saver;

import cn.hutool.core.util.StrUtil;
import com.qmruan.aicodemother.ai.model.HtmlCodeResult;
import com.qmruan.aicodemother.exception.BusinessException;
import com.qmruan.aicodemother.exception.ErrorCode;
import com.qmruan.aicodemother.exception.ThrowUtils;
import com.qmruan.aicodemother.model.enums.CodeGenTypeEnum;

/**
 * @Author: qmruan@trip.com
 * @Date: 2025/10/6 16:31
 */

public class HtmlCodeFileSaverTemplate extends CodeFileSaverTemplate<HtmlCodeResult>{

    @Override
    CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.HTML;
    }

    @Override
    void saveFiles(HtmlCodeResult result, String baseDirPath) {
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
    }

    @Override
    protected void validateInput(HtmlCodeResult result) {
        super.validateInput(result);

        ThrowUtils.throwIf(StrUtil.isBlank(result.getHtmlCode()), new BusinessException(ErrorCode.SYSTEM_ERROR, "HTML代码不能为空"));

    }
}
