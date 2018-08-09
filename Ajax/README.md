#   Ajax

>	与慕课网[Ajax入门](https://class.imooc.com/sc/31/series)搭配使用效果更佳。

<!-- TOC -->


- [Ajax 局部刷新（jQuery）](#ajax-局部刷新jquery)
    - [1.  导入页面](#1--导入页面)
    - [2.  代码实现](#2--代码实现)
- [Ajax 局部刷新（js）](#ajax-局部刷新js)

<!-- /TOC -->

##  Ajax 局部刷新（jQuery）

### 1.  导入页面

将 JavaPage 下的文件复制到 web 目录下，将 `jar包` 导入工程中，启动服务器，即可进入登陆页面。

### 2.  代码实现

在执行登陆请求的过程中，如果用户名和密码正确，则跳转到主页面；如果用户名或密码错误，则没有必要再次刷新页面，可以使用 Ajax 局部刷新提供错误信息。

在 index.jsp 中加入下面代码

```jsp
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
```

其中，

*   url 代表发送请求的地址
*   type 代表发送数据的传递方式，分 get 和 post
*   data 代表客户端发送的数据（建议使用 json 传递）
*   dataType 代表接受服务端数据的格式（建议使用 json）
*   success 代表客户端成功发送信息，并接收到服务端的信息，回调函数中是对页面的的响应

下面是 LoginServlet 的内容

```java
public class LoginServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//1、首先获取jsp页面传递过来的参数信息
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		//2、如果username="15912345678"，password="12345678"则登录成功，否则登录失败
		JSONObject jsonObject = null;
		if("15912345678".equals(username) && "12345678".equals(password)){
			System.out.println("username="+username);
			System.out.println("password="+password);
			jsonObject = new JSONObject("{flag:true}");
		}else{
			//如果登录失败，则给ajax返回数据
			jsonObject = new JSONObject("{flag:false}");
		}
		response.getOutputStream().write(jsonObject.toString().getBytes("utf-8"));
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
```

首先获取到前端发送过来的参数信息，在这里我们假定用户名为“15912345678”，密码为“12345678”登陆成功，构建 JSON 对象并赋正确的值，否则将 JSON 赋错误的值，最后 JSON 对象转换成字节形式，通过 response 对象发送到前端。

##  Ajax 局部刷新（js）

在 listCourse.jsp 中，不同的按钮会调用不同的函数，我们以其中的一个函数为例

```jsp
	function showJava(){
		//1、创建一个 xmlhttpRequest对象
		var xmlhttp = new XMLHttpRequest();
		//2、规定请求的类型、URL 以及是否异步处理请求。
		xmlhttp.open("GET","<%=basePath%>/ListCourseServlet?flag=1",true);
		//3、将请求发送到服务器。
		xmlhttp.send();
		//4、接收服务器端的响应(readyState=4表示请求已完成且响应已就绪    status=200表示请求响应一切正常)
		xmlhttp.onreadystatechange=function(){
			if (xmlhttp.readyState==4 && xmlhttp.status==200){
				//responseText：表示的是服务器返回给ajax的数据
		    	document.getElementById("div1").innerHTML=xmlhttp.responseText;
		    	//alert(xmlhttp.responseText);
		    }
		}
	}
```

代码中均有详细的注释，下面是与之响应的 ListCourseServlet

```java

public class ListCouseServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//1、获取ajax传递过来的参数信息
		String flag = request.getParameter("flag");
		//2、需要返回的数据信息
		String data = "";
		if("1".equals(flag)){//Java课程
			data = "Java从入门到精通<br>java框架";
		}else if("2".equals(flag)){//C课程
			data = "C程序设计<br>C项目实战";
		}
		//3、将数据信息回写给ajax
		response.getOutputStream().write(data.getBytes("utf-8"));
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
```

与 jQuery 的思路相同，只不过代码看起来比较麻烦。