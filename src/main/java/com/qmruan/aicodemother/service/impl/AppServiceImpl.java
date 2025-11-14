package com.qmruan.aicodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.qmruan.aicodemother.constant.AppConstant;
import com.qmruan.aicodemother.core.AiCodeGeneratorFacade;
import com.qmruan.aicodemother.core.builder.VueProjectBuilder;
import com.qmruan.aicodemother.core.handler.StreamHandlerExecutor;
import com.qmruan.aicodemother.core.parser.CodeParserExecutor;
import com.qmruan.aicodemother.core.saver.CodeFileSaverExecutor;
import com.qmruan.aicodemother.exception.BusinessException;
import com.qmruan.aicodemother.exception.ErrorCode;
import com.qmruan.aicodemother.exception.ThrowUtils;
import com.qmruan.aicodemother.model.dto.app.AppQueryRequest;
import com.qmruan.aicodemother.model.entity.App;
import com.qmruan.aicodemother.mapper.AppMapper;
import com.qmruan.aicodemother.model.entity.User;
import com.qmruan.aicodemother.model.enums.ChatHistoryMessageTypeEnum;
import com.qmruan.aicodemother.model.enums.CodeGenTypeEnum;
import com.qmruan.aicodemother.model.vo.AppVO;
import com.qmruan.aicodemother.model.vo.UserVO;
import com.qmruan.aicodemother.service.AppService;
import com.qmruan.aicodemother.service.ChatHistoryService;
import com.qmruan.aicodemother.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用 服务层实现。
 *
 * @author qmruan
 */
@Service
@Slf4j
public class AppServiceImpl extends ServiceImpl<AppMapper, App>  implements AppService{

    @Resource
    private UserService userService;

    @Resource
    private ChatHistoryService chatHistoryService;

    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Resource
    private StreamHandlerExecutor streamHandlerExecutor;

    @Resource
    private VueProjectBuilder vueProjectBuilder;

    @Override
    public AppVO getAppVO(App app) {
        if (app == null) {
            return null;
        }
        AppVO appVO = new AppVO();
        BeanUtil.copyProperties(app, appVO);
        // 关联查询用户信息
        Long userId = app.getUserId();
        if (userId != null) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            appVO.setUser(userVO);
        }
        return appVO;
    }

    @Override
    public QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = appQueryRequest.getId();
        String appName = appQueryRequest.getAppName();
        String cover = appQueryRequest.getCover();
        String initPrompt = appQueryRequest.getInitPrompt();
        String codeGenType = appQueryRequest.getCodeGenType();
        String deployKey = appQueryRequest.getDeployKey();
        Integer priority = appQueryRequest.getPriority();
        Long userId = appQueryRequest.getUserId();
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id)
                .like("appName", appName)
                .like("cover", cover)
                .like("initPrompt", initPrompt)
                .eq("codeGenType", codeGenType)
                .eq("deployKey", deployKey)
                .eq("priority", priority)
                .eq("userId", userId)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }

    @Override
    public List<AppVO> getAppVOList(List<App> appList) {
        if (CollUtil.isEmpty(appList)) {
            return new ArrayList<>();
        }
        // 批量获取用户信息，避免 N+1 查询问题
        Set<Long> userIds = appList.stream()
                .map(App::getUserId)
                .collect(Collectors.toSet());
        Map<Long, UserVO> userVOMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, userService::getUserVO));
        return appList.stream().map(app -> {
            AppVO appVO = getAppVO(app);
            UserVO userVO = userVOMap.get(app.getUserId());
            appVO.setUser(userVO);
            return appVO;
        }).collect(Collectors.toList());
    }

    @Override
    public Flux<String> chatToGenCode(Long appId, String message, User loginUser) {
        // 1. 参数校验
        ThrowUtils.throwIf(appId == null || appId < 0, new BusinessException(ErrorCode.PARAMS_ERROR, "appId不能为空"));
        ThrowUtils.throwIf(loginUser == null || loginUser.getId() == null || loginUser.getId() < 0, new BusinessException(ErrorCode.PARAMS_ERROR, "用户不能为空"));
        ThrowUtils.throwIf(StrUtil.isBlank(message), new BusinessException(ErrorCode.PARAMS_ERROR, "提示词不能为空"));

        // 2. 查询应用信息
        App app = getById(appId);
        ThrowUtils.throwIf(app == null, new BusinessException(ErrorCode.SYSTEM_ERROR, "应用不存在"));
        // 3. 权限校验，仅本人可以和自己的应用对话
        ThrowUtils.throwIf(!app.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR);
        // 4. 获取应用的代码生成类型
        String codeGenType = app.getCodeGenType();
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenType);
        ThrowUtils.throwIf(codeGenTypeEnum == null, new BusinessException(ErrorCode.PARAMS_ERROR, "代码生成类型错误"));
        // 5. 在调用AI前先保存用户消息到数据库中
        chatHistoryService.addChatMessage(appId, message, ChatHistoryMessageTypeEnum.USER.getValue(), loginUser.getId());
        // 6. 调用 AI生成代码
        Flux<String> contentFlux = aiCodeGeneratorFacade.generateAndSaveCodeStream(message, codeGenTypeEnum, appId);

        return streamHandlerExecutor.doExecute(contentFlux, chatHistoryService, appId, loginUser, codeGenTypeEnum);
    }

    @Override
    public String deployApp(Long appId, User loginUser) {
        // 1. 参数校验
        ThrowUtils.throwIf(appId == null || appId < 0, ErrorCode.PARAMS_ERROR, "appId错误");
        ThrowUtils.throwIf(loginUser == null || loginUser.getId() == null || loginUser.getId() < 0, ErrorCode.NO_AUTH_ERROR);
        // 2. 查询应用信息
        App app = getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.SYSTEM_ERROR, "应用不存在");
        // 3. 权限校验，仅本人可部署应用
        ThrowUtils.throwIf(!app.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR);
        // 4. 检查是否已有deploy key没有则生成（雪花算法）
        String deployKey = app.getDeployKey();
        if (StrUtil.isBlank(deployKey)) {
            deployKey = IdUtil.getSnowflakeNextIdStr();
        }
        // 5. 获取代码生成类型，获取原始代码生成路径
        String codeGenType = app.getCodeGenType();
        String sourceDirName = codeGenType + "_" + appId;
        String sourceDirPath = AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + sourceDirName;

        // 6. 检查源目录是否存在
        File sourceDir = new File(sourceDirPath);
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用代码不存在，请先生成代码");
        }
        // 7. Vue 项目特殊处理：执行构建
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenType);
        if (codeGenTypeEnum == CodeGenTypeEnum.VUE_PROJECT) {
            // Vue 项目需要构建
            boolean buildSuccess = vueProjectBuilder.buildProject(sourceDirPath);
            ThrowUtils.throwIf(!buildSuccess, ErrorCode.SYSTEM_ERROR, "Vue 项目构建失败，请检查代码和依赖");
            // 检查 dist 目录是否存在
            File distDir = new File(sourceDirPath, "dist");
            ThrowUtils.throwIf(!distDir.exists(), ErrorCode.SYSTEM_ERROR, "Vue 项目构建完成但未生成 dist 目录");
            // 将 dist 目录作为部署源
            sourceDir = distDir;
            log.info("Vue 项目构建成功，将部署 dist 目录: {}", distDir.getAbsolutePath());
        }

        // 8. 复制文件到部署目录
        String deployDirName = AppConstant.CODE_DEPLOY_ROOT_DIR + File.separator + deployKey;
        try {
            FileUtil.copyContent(sourceDir, new File(deployDirName), true);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "部署失败，请稍后再试");
        }
        // 9. 更新数据库
        App updateApp = new App();
        updateApp.setId(appId);
        updateApp.setDeployKey(deployKey);
        updateApp.setDeployedTime(LocalDateTime.now());
        boolean res = this.updateById(updateApp);
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR, "更新应用部署信息失败");
        // 10. 返回可访问的URL地址
        return String.format("%s/%s/", AppConstant.CODE_DEPLOY_HOST, deployKey);
    }

    /**
     * 删除应用时，关联删除对话历史
     *
     * @param id
     * @return
     */

    @Override
    public boolean removeById(Serializable id) {
        if (id == null) {
            return false;
        }

        long appId = Long.parseLong(id.toString());
        if (appId <= 0) {
            return false;
        }
        // 先关联删除对话历史
        try {
            chatHistoryService.deleteByAppId(appId);
        } catch (Exception e) {
            log.error("删除应用关联的对话历史失败：{}", e.getMessage());
        }
        // 删除应用
        return super.removeById(id);
    }


}
