package com.qmruan.aicodemother.core.parser;

/**
 * @Author: qmruan@trip.com
 * @Date: 2025/10/6 16:01
 */

public interface CodeParser<T> {

    /**
     * 解析代码内容
     *
     * @param code
     * @return
     */
    T parseCode(String code);

}
