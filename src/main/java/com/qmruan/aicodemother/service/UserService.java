package com.qmruan.aicodemother.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.qmruan.aicodemother.model.dto.UserQueryRequest;
import com.qmruan.aicodemother.model.entity.User;
import com.qmruan.aicodemother.model.vo.LoginUserVO;
import com.qmruan.aicodemother.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 用户 服务层。
 *
 * @author qmruan
 */
public interface UserService extends IService<User> {

    QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 用户注销
     * @param req
     * @return
     */
    boolean userLogout(HttpServletRequest req);

    /**
     * 获取脱敏用户信息
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏用户列表
     * @param users
     * @return
     */
    List<UserVO> getUserVOList(List<User> users);

    /**
     * 获取未脱敏用户信息
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户登录
     * @param username
     * @param password
     * @param request
     * @return
     */
    LoginUserVO userLogin(String username, String password, HttpServletRequest request);


    /**
     * 获取已经脱敏的用户信息
     * @param user
     * @return
     */
    LoginUserVO getLoginUserVO(User user);


    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 获取加密后的密码
     * @param password
     * @return
     */
    String getEncryptPassword(String password);
}
