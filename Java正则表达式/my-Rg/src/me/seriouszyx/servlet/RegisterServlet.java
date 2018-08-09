package me.seriouszyx.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet(name = "RegisterServlet")
public class RegisterServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//首先获取参数
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String phone = request.getParameter("phone");
		String email = request.getParameter("email");
		//对获取的参数信息进行校验
		//		用户名只能为字母，长度为6-12位：[a-zA-Z]{6,12}
		String usernameRegex = "[a-zA-Z]{6,12}";
		//matches方法的含义是将获取过来的username和usernameRegex这个规则进行比对，如果满足要求则返回true，否则返回false
		boolean flag1 = username.matches(usernameRegex);
		//		密码只能为数字，长度至少为6位：[0-9]{6,}，\\d{6}
		String passRegex = "[0-9]{6,}";
		boolean flag2 = password.matches(passRegex);
		//		手机号校验：[1][3578]\\d{9}
		String phoneRegex ="[1][3578][0-9]{9}";
		boolean flag3 = phone.matches(phoneRegex);
		//		邮箱校验：[a-zA-Z_0-9]{3,}@([a-zA-Z]+|\\d+)(\\.[a-zA-Z]+)+
		String emialRegex ="[a-zA-Z_0-9]{3,}@([a-zA-Z]+|\\d+)(\\.[a-zA-Z]+)+";
		boolean flag4 = email.matches(emialRegex);
		//如果username、password、email、phone同时满足格式要求的话才打印数据,否则提示数据不满足格式要求
		if(flag1&&flag2&&flag3&&flag4){
			//然后打印参数
			System.out.println("username="+username);
			System.out.println("password="+password);
			System.out.println("phone="+phone);
			System.out.println("email="+email);
		}else{
			System.out.println("亲，您输入的注册信息数据不满足格式要求，请检查仔细后在输入");
		}
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
