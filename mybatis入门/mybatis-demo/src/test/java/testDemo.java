import me.seriouszyx.app.Users;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @ClassName testDemo
 * @Description TODO
 * @Author Yixiang Zhao
 * @Date 2018/9/9 10:23
 * @Version 1.0
 */
public class testDemo {

    @Test
    public void testDemo1() throws IOException {
        // 初始化mybatis配置环境
        String resource = "mybatis.xml";
        InputStream is = Resources.getResourceAsStream(resource);
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);

        // 打开和数据库之间的会话
        SqlSession session = factory.openSession();

        // 进行数据处理
        List<Users> usersList = session.selectList("usersList");

        for (Users user : usersList
             ) {
            System.out.println(user);
        }

        // 关闭资源
        session.close();
    }
}
