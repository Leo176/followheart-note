<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>welcome page</title>
</head>
<body>
<p>欢迎来到随心云笔记</p>
<a href="./note/getAllNotebooks" id="getAllNotebooks">获取所有笔记本</a>
<a href="./note/addNotebook/newnotebook" id="addNotebook">新建笔记本</a>
<a href="./note//addNote/newnote" id="addNote">新建笔记</a>
<%session.setAttribute("user_info","test");%>
<%session.setAttribute("nbRowkey","test_133256");%>
</body>
</html>