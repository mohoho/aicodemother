package com.qmruan.aicodemother.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.qmruan.aicodemother.model.entity.ChatHistory;
import com.qmruan.aicodemother.mapper.ChatHistoryMapper;
import com.qmruan.aicodemother.service.ChatHistoryService;
import org.springframework.stereotype.Service;

/**
 * 对话历史 服务层实现。
 *
 * @author qmruan
 */
@Service
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory>  implements ChatHistoryService{

}
