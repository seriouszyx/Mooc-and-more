package me.seriouszyx.dao;

import me.seriouszyx.mybatis.Users;
import me.seriouszyx.utils.SqlSessionFactoryUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

/**
 * @ClassName UsersDAO
 * @Description TODO
 * @Author Yixiang Zhao
 * @Date 2018/9/9 14:58
 * @Version 1.0
 */
public class UsersDAO {
    private SqlSession sqlSession;
    private Users user;
    private List<Users> list;

    private SqlSession getSession() {
        sqlSession = SqlSessionFactoryUtils.getSqlSessionFactory().openSession();
        return sqlSession;
    }

    /**
     * @Author Yixiang Zhao
     * @Description 查询全部用户
     * @Date 17:59 2018/9/9
     * @Param []
     * @return java.util.List<me.seriouszyx.mybatis.Users>
     **/
    public List<Users> findAll() {
        try {
            list = getSession().selectList("findUsers");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
        return list;
    }

    /**
     * @Author Yixiang Zhao
     * @Description 根据编号查询用户
     * @Date 18:01 2018/9/9
     * @Param [id]
     * @return me.seriouszyx.mybatis.Users
     **/
    public Users findById(Integer id) {
        try {
            user = getSession().selectOne("findUsers", new Users(id));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
        return user;
    }

    /**
     * @Author Yixiang Zhao
     * @Description 添加一个新用户到数据库中
     * @Date 19:10 2018/9/9
     * @Param [user]
     * @return me.seriouszyx.mybatis.Users
     **/
    public Users addUser(Users user) {
        try {
            getSession().insert("addUser", user);
            sqlSession.commit();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }

        return user;
    }

    /**
     * @Author Yixiang Zhao
     * @Description 用于修改用户资料
     * @Date 19:55 2018/9/9
     * @Param [user]
     * @return me.seriouszyx.mybatis.Users
     **/
    public Users updateUsers(Users user) {
        try {
            getSession().update("updateUser", user);
            sqlSession.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }

        return user;
    }

    public void delUsers(Integer id) {
        try {
            getSession().delete("delUser", id);
            sqlSession.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }

    }
}
