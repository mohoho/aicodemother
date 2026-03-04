package com.qmruan.aicodemother.ai;

import com.qmruan.aicodemother.ai.guardrail.PromptSafetyInputGuardrail;
import com.qmruan.aicodemother.ai.guardrail.RetryOutputGuardrail;
import com.qmruan.aicodemother.utils.SpringContextUtil;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI代码生成类型路由服务工厂
 *
 * @author yupi
 */
@Slf4j
@Configuration
public class AiCodeGenTypeRoutingServiceFactory {
    /**
     * 创建AI代码生成类型路由服务实例（每次调用都会创建新实例，适用于并发场景）
     * 注意：此方法未加 @Bean，避免被 Configuration 代理拦截后总是返回同一实例
     */
    public AiCodeGenTypeRoutingService createAiCodeGenTypeRoutingService() {
        ChatModel chatModel = SpringContextUtil.getBean("routingChatModelPrototype", ChatModel.class);
        return AiServices.builder(AiCodeGenTypeRoutingService.class)
                .chatModel(chatModel)
                .build();
    }

    /**
     * 默认提供一个单例 Bean（供只需单个实例的场景注入使用）
     */
    @Bean
    public AiCodeGenTypeRoutingService aiCodeGenTypeRoutingService() {
        return createAiCodeGenTypeRoutingService();
    }
}
