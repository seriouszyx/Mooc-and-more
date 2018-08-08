<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>人事管理系统</title>
</head>
<body>
    <%
        // request.setAttribute("name", "zyx");
        // request.getRequestDispatcher("result.jsp").forward(request, response);
    %>

    <%
        // pageContext.setAttribute("name", "zyx");
    %>

    config对象：<%=config.getServletName() %>
    <br />
    config初始化参数：<%=config.getInitParameter("age") %>

    <h3 align = "center">人事管理系统登陆页面</h3>
    <hr />
    <form action="control.jsp" method="post">
        <table align="center">
            <tr>
                <td>账号：</td>
                <td><input type="text" name="account" /></td>
            </tr>
            <tr>
                <td>密码：</td>
                <td><input type="password" name="password" /></td>
            </tr>
            <tr>
                <td><input type="submit" value="登陆" /></td>
            </tr>
        </table>
    </form>
</body>
</html>
