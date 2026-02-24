package com.qmruan.aicodemother.ai;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.qmruan.aicodemother.ai.tools.*;
import com.qmruan.aicodemother.exception.BusinessException;
import com.qmruan.aicodemother.exception.ErrorCode;
import com.qmruan.aicodemother.model.enums.CodeGenTypeEnum;
import com.qmruan.aicodemother.service.ChatHistoryService;
import com.qmruan.aicodemother.utils.SpringContextUtil;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * AI服务创建工厂
 * 通过 ApplicationContext 按名称获取 prototype 的 StreamingChatModel，确保每次创建服务时拿到新实例，避免多线程并发时共用一个 model 导致只有单线程响应。
 */
@Configuration
@Slf4j
public class AiCodeGeneratorServiceFactory {

    private static final String BEAN_STREAMING_CHAT_MODEL = "streamingChatModelPrototype";
    private static final String BEAN_REASONING_STREAMING_CHAT_MODEL = "reasoningStreamingChatModelPrototype";

    @Resource(name = "openAiChatModel")
    private ChatModel chatModel;

    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;

    @Resource
    private ChatHistoryService chatHistoryService;

    @Resource
    private ToolManager toolManager;

    /**
     * AI 服务实例缓存
     * 缓存策略：
     * - 最大缓存 1000 个实例
     * - 写入后 30 分钟过期
     * - 访问后 10 分钟过期
     */
    private final Cache<String, AiCodeGeneratorService> serviceCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .removalListener((key, value, cause) -> {
                log.debug("AI 服务实例被移除，key: {}, 原因: {}", key, cause);
            })
            .build();

    /**
     * 根据 appId 获取服务（带缓存）
     */
    public AiCodeGeneratorService getAiCodeGeneratorService(long appId) {
        return getAiCodeGeneratorService(appId, CodeGenTypeEnum.HTML);
    }

    /**
     * 根据 appId 获取服务（带缓存）
     */
    public AiCodeGeneratorService getAiCodeGeneratorService(long appId, CodeGenTypeEnum codeGenTypeEnum) {
        String cacheKey = buildCacheKey(appId, codeGenTypeEnum);
        return serviceCache.get(cacheKey, key -> createAiCodeGeneratorService(appId, codeGenTypeEnum));
    }

    /**
     * 创建新的 AI 服务实例
     */
    private AiCodeGeneratorService createAiCodeGeneratorService(long appId, CodeGenTypeEnum codeGenTypeEnum) {
        log.info("为 appId: {} 创建新的 AI 服务实例", appId);
        // 根据 appId 构建独立的对话记忆
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory
                .builder()
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .maxMessages(50)
                .build();
        chatHistoryService.loadChatHistoryToMemory(appId, chatMemory, 30);
        StreamingChatModel streamingModel = SpringContextUtil.getBean(BEAN_STREAMING_CHAT_MODEL, StreamingChatModel.class);
        StreamingChatModel reasoningModel = SpringContextUtil.getBean(BEAN_REASONING_STREAMING_CHAT_MODEL, StreamingChatModel.class);
        return switch (codeGenTypeEnum) {
            case VUE_PROJECT -> AiServices.builder(AiCodeGeneratorService.class)
                    .streamingChatModel(reasoningModel)
                    .chatMemoryProvider(memoryId -> chatMemory)
                    .tools(toolManager.getAllTools())
                    .hallucinatedToolNameStrategy(toolExecutionRequest -> ToolExecutionResultMessage.from(
                            toolExecutionRequest, "Error: there is no tool called " + toolExecutionRequest.name()
                    ))
                    .build();
            case MULTI_FILE, HTML -> AiServices.builder(AiCodeGeneratorService.class)
                        .chatModel(chatModel)
                        .streamingChatModel(streamingModel)
                        .chatMemoryProvider(memoryId -> chatMemory)
                        .build();
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的生成类型" + codeGenTypeEnum.getValue());
        };
    }

    /**
     * 构造缓存键
     * @param appId
     * @param codeGenTypeEnum
     * @return
     */
    private String buildCacheKey(long appId, CodeGenTypeEnum codeGenTypeEnum) {
        return appId + "_"  + codeGenTypeEnum.getValue();
    }


}
