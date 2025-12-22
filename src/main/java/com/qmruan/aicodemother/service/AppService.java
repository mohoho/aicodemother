package com.qmruan.aicodemother.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.qmruan.aicodemother.model.dto.app.AppAddRequest;
import com.qmruan.aicodemother.model.dto.app.AppQueryRequest;
import com.qmruan.aicodemother.model.entity.App;
import com.qmruan.aicodemother.model.entity.User;
import com.qmruan.aicodemother.model.vo.AppVO;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author qmruan
 */
public interface AppService extends IService<App> {

    Long createApp(AppAddRequest appAddRequest, User loginUser);

    void generateAppScreenshotAsync(Long appId, String appUrl);

    AppVO getAppVO(App app);

    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

    List<AppVO> getAppVOList(List<App> appList);

    /**
     * 通过对话生成应用代码
     * @param appId
     * @param message
     * @param loginUser
     * @return
     */
    Flux<String> chatToGenCode(Long appId, String message, User loginUser);

    /**
     * 应用部署
     * @param appId
     * @param loginUser
     * @return 可访问地址
     */
    String deployApp(Long appId, User loginUser);


}
