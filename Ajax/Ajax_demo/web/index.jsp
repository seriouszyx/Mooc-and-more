<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>登录</title>
<style type="text/css">
	input{
		width:250px;
		height:25px;
	}
	#login{
		width:255px;
		height:35px;
		background-color:#FF2611;
		border:0px;
		cursor:pointer;
		color:white
	}
	.c1{
		font-size:24px;
		color:black;
		font-weight:bolder
	}
	.c2{
		font-size:14px;
		color:#666;
	}
	.c3{
		font-size:14px;
		color:red;
	}
</style>
<script type="text/javascript" src="resources/js/jquery-1.4.2.js"></script>
</head>
<body style="text-align:center;">
			<%-- <form action="<%=basePath%>/LoginServlet"  method="post"> --%>
				<table>
					<tr>
						<td>
							<span class="c1">欢迎登录</span>&nbsp;
							<span class="c2">没有帐号？</span>
							<span class="c3">立即注册</span>
						</td>
					</tr>
					<tr>
						<td><input type="text" name="username" placeholder="请输入登录邮箱/手机号"><span class="tip" style="color:red;font-size:12px"></span></td>
					</tr>
					<tr>
						<td><input type="password" name="password" placeholder="6-16位密码，区分大小写，不能空格"></td>
					</tr>
					<tr>
						<td>
							<!-- <input type="submit" value="登录"  id="login"> -->
							<input type="button" value="登录"  id="login">
						</td>
					</tr>
				</table>
			<!-- </form> -->
</body>
<script type="text/javascript">
$("#login").click(function(){
    //单击登录按钮的时候触发ajax事件
    $.ajax({
        url:"<%=basePath%>/LoginServlet",
        type:"post",
        data:{
            username:$("input[name=username]").val(),
            password:$("input[name=password]").val()
        },
        dataType:"json",
        success:function(result){
            var flag = result.flag;
            if(flag==true){
                //如果登录成功则跳转到成功页面
                window.location.href="<%=basePath%>/pages/front/success.jsp";
            }else{
                //跳回到Index.jsp登录页面，同时在登录页面给用户一个友好的提示
                $(".tip").text("您输入的用户名或者密码不正确");
            }
        }

    });
});
</script>
</html>