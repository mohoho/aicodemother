package com.qmruan.aicodemother.ai.model.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: qmruan@trip.com
 * @Date: 2025/11/13 17:26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StreamMessage {

    /**
     * 消息类型
     */
    private String type;
}
