package com.qmruan.aicodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.qmruan.aicodemother.exception.BusinessException;
import com.qmruan.aicodemother.exception.ErrorCode;
import com.qmruan.aicodemother.exception.ThrowUtils;
import com.qmruan.aicodemother.model.dto.user.UserQueryRequest;
import com.qmruan.aicodemother.model.entity.User;
import com.qmruan.aicodemother.mapper.UserMapper;
import com.qmruan.aicodemother.model.enums.UserRoleEnum;
import com.qmruan.aicodemother.model.vo.LoginUserVO;
import com.qmruan.aicodemother.model.vo.UserVO;
import com.qmruan.aicodemother.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.qmruan.aicodemother.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户 服务层实现。
 *
 * @author qmruan
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService{

    @Override
    public QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id)
                .eq("userRole", userRole)
                .like("userAccount", userAccount)
                .like("userName", userName)
                .like("userProfile", userProfile)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }


    @Override
    public boolean userLogout(HttpServletRequest req) {
        Object userObj = req.getSession().getAttribute(USER_LOGIN_STATE);
        ThrowUtils.throwIf(userObj == null, ErrorCode.NOT_LOGIN_ERROR);
        req.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }

        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user,userVO);

        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<User> users) {
        if (users == null) {
            return new ArrayList<>();
        }

        return users.stream().map(this::getUserVO).toList();
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {

        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User)userObj;

        ThrowUtils.throwIf(user == null, ErrorCode.NOT_LOGIN_ERROR);

        Long id = user.getId();
        User currentUser = this.getById(id);

        ThrowUtils.throwIf(currentUser == null, ErrorCode.NOT_LOGIN_ERROR);

        return currentUser;
    }

    @Override
    public LoginUserVO userLogin(String username, String password, HttpServletRequest request) {
        // 校验参数
        ThrowUtils.throwIf(StrUtil.hasBlank(username, password), ErrorCode.PARAMS_ERROR);

        // 加密
        String encryptPassword = getEncryptPassword(password);

        // 查询用户是否存在
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", username);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.mapper.selectOneByQuery(queryWrapper);
        ThrowUtils.throwIf(user == null, new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误"));

        // 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);

        return getLoginUserVO(user);
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }

        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {

        ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword, checkPassword), ErrorCode.PARAMS_ERROR);

        ThrowUtils.throwIf(!userPassword.equals(checkPassword), new BusinessException(ErrorCode.PARAMS_ERROR, "确认密码不一致"));

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.mapper.selectCountByQuery(queryWrapper);
        ThrowUtils.throwIf(count > 0, new BusinessException(ErrorCode.PARAMS_ERROR, "用户已存在"));

        String encryptPassword = getEncryptPassword(userPassword);

        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("无名");
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean save = this.save(user);
        ThrowUtils.throwIf(!save, new BusinessException(ErrorCode.OPERATION_ERROR, "注册失败，数据库报错"));

        return user.getId();
    }

    @Override
    public String getEncryptPassword(String password) {
        final String salt = "qmruan";
        return DigestUtils.md5DigestAsHex((password + salt).getBytes(StandardCharsets.UTF_8));
    }
}
