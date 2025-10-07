package com.qmruan.aicodemother.core;

import com.qmruan.aicodemother.ai.AiCodeGeneratorService;
import com.qmruan.aicodemother.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiCodeGeneratorFacadeTest {

    @Resource
    AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Test
    void generateAndSaveCode() {
        File file = aiCodeGeneratorFacade.generateAndSaveCode("生成一个20行博客页面", CodeGenTypeEnum.HTML, 1111L);
        Assertions.assertNotNull(file);
    }
    
    @Test
    void generateAndSaveCodeStream() {
        Flux<String> codeStream = aiCodeGeneratorFacade.generateAndSaveCodeStream("生成一个20行的博客", CodeGenTypeEnum.MULTI_FILE, 1111L);
        List<String> result = codeStream.collectList().block();
        //等待数据收集完成
        Assertions.assertNotNull(result);
        String res = String.join("", result);
        System.out.println(res);
    }
}