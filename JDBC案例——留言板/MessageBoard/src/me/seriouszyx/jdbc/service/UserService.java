package me.seriouszyx.jdbc.service;

import me.seriouszyx.jdbc.bean.User;
import me.seriouszyx.jdbc.dao.UserDAO;

/**
 * @ClassName UserService
 * @Description 用户Service
 * @Author Yixiang Zhao
 * @Date 2018/9/7 21:18
 * @Version 1.0
 */
public class UserService {

    private UserDAO userDAO;

    public UserService() {
        userDAO = new UserDAO();
    }

    public User login(String username, String password) {
        return userDAO.login(username, password);
    }

    public User getUserById(Long id) {
        return userDAO.getUserById(id);
    }

    public boolean updateUser(User user) {
        return userDAO.updateUser(user);
    }
}
