package com.qmruan.aicodemother.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.qmruan.aicodemother.model.dto.chathistory.ChatHistoryQueryRequest;
import com.qmruan.aicodemother.model.entity.ChatHistory;
import com.qmruan.aicodemother.model.entity.User;

import java.time.LocalDateTime;

/**
 * 对话历史 服务层。
 *
 * @author qmruan
 */
public interface ChatHistoryService extends IService<ChatHistory> {

    /**
     * 添加对话历史
     * @param appId
     * @param message
     * @param messageType
     * @param userId
     * @return
     */
    boolean addChatMessage(Long appId, String message, String messageType, Long userId);

    /**
     * 根据应用id删除记录
     * @param appId
     * @return
     */
    boolean deleteByAppId(Long appId);

    /**
     * 获取查询包装类
     * @param chatHistoryQueryRequest
     * @return
     */
    QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest);

    Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize,
                                               LocalDateTime lastCreateTime,
                                               User loginUser);
}
