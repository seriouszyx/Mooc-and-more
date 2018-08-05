<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>qrcode</title>
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.qrcode.min.js"></script>
</head>
<body>
    生成的二维码如下：<br>
    <div id="qrcode"></div>

<script>
    jQuery('#qrcode').qrcode('https://github.com/seriouszyx')
</script>
</body>
</html>
