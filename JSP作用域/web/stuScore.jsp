<%@ page import="java.util.Map" %>
<%@ page import="me.seriouszyx.bean.Student" %>
<%@ page import="java.util.HashMap" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>学生成绩管理</title>
</head>
<body>
    <h3 align="center">学生成绩管理页面</h3>
    <hr />
    <%
        Map<String, Student> map = new HashMap<>();
        map.put("10", new Student("11071010", "赵明", 78));
        map.put("11", new Student("11071011", "李雷", 100));
        map.put("12", new Student("11071012", "韩梅梅", 90));
        map.put("13", new Student("11071013", "大熊", 80));
        map.put("14", new Student("11071014", "李静香", 92));
        map.put("15", new Student("11071015", "胖虎", 42));
    %>
    <table align="center" border="1" width="500px">
        <tr>
            <td>学号</td>
            <td>姓名</td>
            <td>成绩</td>
        </tr>
        <%
            for (String s : map.keySet()) {
                Student stu = map.get(s);
        %>
            <tr>
                <td><%=stu.getId() %></td>
                <td><%=stu.getName() %></td>
                <td><%=stu.getGrade() %></td>
            </tr>
        <%
            }
        %>
    </table>
</body>
</html>
