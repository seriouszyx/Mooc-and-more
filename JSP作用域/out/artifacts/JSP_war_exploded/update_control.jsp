<%@ page import="java.util.Map" %>
<%@ page import="me.seriouszyx.bean.Emp" %>
<%@ page import="me.seriouszyx.db.DBUtil" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <%
        Map<String, Emp> map = DBUtil.map;
        Emp emp = map.get(request.getParameter("account"));
        emp.setName(request.getParameter("name"));
        emp.setEmail(request.getParameter("email"));
    %>
    <h3 align="center">修改员工信息成功</h3>
</body>
</html>
