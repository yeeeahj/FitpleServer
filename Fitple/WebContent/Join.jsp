<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<title>Insert title here</title>
</head>
<body>
<form action="/Fitple/Servlet/JoinServlet" method=post characterEncoding=UTF-8>
이메일 : <input type="text" name="email" size=5><br>
닉네임 : <input type="text" name="nickname" size=5><br>
비밀번호 <input type="text" name="password" size=5><br>
성 <input type="text" name="gender" size=5><br>
<input type="submit" value="검색">
</form>
</body>
</html>