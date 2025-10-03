package com.qmruan.aicodemother.service;

import com.mybatisflex.core.service.IService;
import com.qmruan.aicodemother.model.entity.User;
import com.qmruan.aicodemother.model.vo.LoginUserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 用户 服务层。
 *
 * @author qmruan
 */
public interface UserService extends IService<User> {

    boolean userLogout(HttpServletRequest req);

    User getLoginUser(HttpServletRequest request);

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

    String getEncryptPassword(String password);
}
