package me.seriouszyx.servlet;

import me.seriouszyx.domain.User;
import me.seriouszyx.utils.UpdateUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "RegistServlet")
public class RegistServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 数据的接收
        /**
         *  文件上传基本流程
         *      1. 创建一个磁盘项工厂对象
         *      2. 创建核心解析类
         *      3. 解析request请求，返回List集合，集合中存放的是FileItem对象
         *      4. 遍历集合，获得每个FileItem，判断表单项还是文件上传项
         **/

        try {
            Map<String, String> map = new HashMap<>();
            // 1. 创建一个磁盘项工厂对象
            DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
            // 2. 创建核心解析类
            ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);
            // 3. 解析request请求，返回List集合，集合中存放的是FileItem对象
            List<FileItem> list = servletFileUpload.parseRequest(request);
            List<String> hobby = new ArrayList<>();
            String url = null;
            // 4. 遍历集合，获得每个FileItem，判断表单项还是文件上传项
            for (FileItem fileItem : list) {
                if (fileItem.isFormField()) {
                    // 普通表单项
                    String name = fileItem.getFieldName();
                    String value = fileItem.getString("UTF-8");
                    // 复选框数据
                    if ("hobby".equals(name)) {
                        String hobbyValue =  fileItem.getString("UTF-8");
                        hobby.add(hobbyValue);
                        hobbyValue = hobby.toString().substring(1,hobby.toString().length()-1 );
                        map.put(name, hobbyValue);
                    } else {
                        map.put(name, value);
                    }
                } else {
                    // 文件上传项
                    // 获得文件上传名称
                    String fileName = fileItem.getName();
                    // 通过工具类获得唯一文件名
                    String uuidFileName = UpdateUtils.getUUIDFileName(fileName);
                    // 获得文件上传数据
                    InputStream is = fileItem.getInputStream();
                    // 获得文上传路径
                    String path = this.getServletContext().getRealPath("/upload");
                    // 将输入流对接到输出流
                    url = path + "\\" + uuidFileName;
                    OutputStream os = new FileOutputStream(url);
                    int len = 0;
                    byte[] b = new byte[1024];
                    while ((len = is.read(b)) != -1) {
                        os.write(b, 0, len);
                    }
                    is.close();
                    os.close();
                }
            }
            // 封装数据到User当中
            User user = new User();
            user.setUsername(map.get("username"));
            user.setPassword(map.get("password"));
            user.setSex(map.get("sex"));
            user.setNickname(map.get("nickname"));
            user.setHobby(map.get("hobby"));
            user.setPath(url);

            List<User> userList = (List<User>) this.getServletContext().getAttribute("list");
            userList.add(user);
            this.getServletContext().setAttribute("list", userList);
            // 注册成功，登陆跳转页面
            request.getSession().setAttribute("username", user.getUsername());
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        } catch (FileUploadException e) {
            e.printStackTrace();
        }


    }
}
