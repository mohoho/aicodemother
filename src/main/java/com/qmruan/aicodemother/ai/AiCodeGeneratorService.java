package com.qmruan.aicodemother.ai;

import com.qmruan.aicodemother.ai.model.HtmlCodeResult;
import com.qmruan.aicodemother.ai.model.MultiFileCodeResult;
import dev.langchain4j.service.SystemMessage;

public interface AiCodeGeneratorService {

    /**
     * 生成单文件代码
     * @param userMessage 用户提示词
     * @return 单文件代码
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    HtmlCodeResult generateHtmlCode(String userMessage);

    /**
     * 生成多文件代码
     * @param userMessage 用户提示词
     * @return 多文件代码
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    MultiFileCodeResult generateMultiFileCode(String userMessage);
}