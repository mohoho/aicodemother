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
import com.qmruan.aicodemother.exception.BusinessException;
import com.qmruan.aicodemother.exception.ErrorCode;
import com.qmruan.aicodemother.exception.ThrowUtils;
import com.qmruan.aicodemother.model.dto.app.AppQueryRequest;
import com.qmruan.aicodemother.model.entity.App;
import com.qmruan.aicodemother.mapper.AppMapper;
import com.qmruan.aicodemother.model.entity.User;
import com.qmruan.aicodemother.model.enums.CodeGenTypeEnum;
import com.qmruan.aicodemother.model.vo.AppVO;
import com.qmruan.aicodemother.model.vo.UserVO;
import com.qmruan.aicodemother.service.AppService;
import com.qmruan.aicodemother.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
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
public class AppServiceImpl extends ServiceImpl<AppMapper, App>  implements AppService{

    @Resource
    private UserService userService;

    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

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
        // 5. 调用 AI生成代码
        Flux<String> flux = aiCodeGeneratorFacade.generateAndSaveCodeStream(message, codeGenTypeEnum, appId);
        return flux;
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
        // 6. 检查路径是否存在
        File sourceFile = new File(sourceDirPath);
        ThrowUtils.throwIf(!sourceFile.exists(), ErrorCode.SYSTEM_ERROR, "应用代码不存在！");
        // 7. 复制文件到目录
        String deployDirName = AppConstant.CODE_DEPLOY_ROOT_DIR + File.separator + deployKey;
        try {
            FileUtil.copyContent(sourceFile, new File(deployDirName), true);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "部署失败，请稍后再试");
        }
        // 8. 更新数据库
        App updateApp = new App();
        updateApp.setId(appId);
        updateApp.setDeployKey(deployKey);
        updateApp.setDeployedTime(LocalDateTime.now());
        boolean res = this.updateById(updateApp);
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR, "更新应用部署信息失败");
        // 9. 返回可访问的URL地址
        return String.format("%s/%s/", AppConstant.CODE_DEPLOY_HOST, deployKey);
    }


}
