package com.qmruan.aicodemother.aop;


import com.qmruan.aicodemother.annotation.AuthCheck;
import com.qmruan.aicodemother.exception.BusinessException;
import com.qmruan.aicodemother.exception.ErrorCode;
import com.qmruan.aicodemother.exception.ThrowUtils;
import com.qmruan.aicodemother.model.entity.User;
import com.qmruan.aicodemother.model.enums.UserRoleEnum;
import com.qmruan.aicodemother.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;

@Aspect
@Component
public class AuthInterceptor {

    @Resource
    UserService userService;

    @Around("@annotation(authCheck)")
    public Object checkAuth(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        if (mustRoleEnum == null) {
            return joinPoint.proceed();
        }

        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        // 获取当前登录用户
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        User loginUser = userService.getLoginUser(request);

        UserRoleEnum roleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        ThrowUtils.throwIf(roleEnum == null, ErrorCode.NO_AUTH_ERROR);
        ThrowUtils.throwIf(UserRoleEnum.ADMIN.equals(mustRoleEnum) && !roleEnum.equals(UserRoleEnum.ADMIN), ErrorCode.NO_AUTH_ERROR);
        return joinPoint.proceed();
    }

}
