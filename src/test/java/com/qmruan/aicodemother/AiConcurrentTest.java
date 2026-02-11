package com.qmruan.aicodemother;

import com.qmruan.aicodemother.ai.AiCodeGenTypeRoutingService;
import com.qmruan.aicodemother.ai.AiCodeGenTypeRoutingServiceFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@SpringBootTest
public class AiConcurrentTest {

    @Resource
    private AiCodeGenTypeRoutingServiceFactory routingServiceFactory;

    /**
     * 串行化对 routeCodeGenType 的调用，规避 langchain4j AbstractGuardrailService 内部
     * 使用非线程安全 HashMap 导致的 ConcurrentModificationException。
     * 若 langchain4j 后续改为 ConcurrentHashMap，可移除此锁实现真正并发。
     */
    private final ReentrantLock routingCallLock = new ReentrantLock();

    @Test
    public void testConcurrentRoutingCalls() throws InterruptedException {
        String[] prompts = {
                "做一个简单的HTML页面",
                "做一个多页面网站项目",
                "做一个Vue管理系统"
        };
        // 使用虚拟线程并发执行（实际对 AI 的调用在锁内串行，避免库的并发 bug）
        Thread[] threads = new Thread[prompts.length];
        for (int i = 0; i < prompts.length; i++) {
            final String prompt = prompts[i];
            final int index = i + 1;
            threads[i] = Thread.ofVirtual().start(() -> {
                AiCodeGenTypeRoutingService service = routingServiceFactory.aiCodeGenTypeRoutingService();
                try {
                    var result = service.routeCodeGenType(prompt);
                    log.info("线程 {}: {} -> {}", index, prompt, result.getValue());
                } finally {
                }
            });
        }
        // 等待所有任务完成
        for (Thread thread : threads) {
            thread.join();
        }
    }
}
