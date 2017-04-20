package Servlet;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class LoginServlet extends HttpServlet implements pName{
		int responseCode;

			@Override
			protected void doPost(
					HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {
				request.setCharacterEncoding("UTF-8");
				
				ServletContext sc = this.getServletContext();
				Statement stmt = null;
				ResultSet rs = null;
				PreparedStatement prepare=null;
				List<Map<String,Object>> list = new ArrayList();
				Map<String,Object> j_map = new HashMap(); 
				Map<String,Object> map = new HashMap(); 
				String CookieId=null;
				try{
					
					Class.forName("com.mysql.jdbc.Driver");
					Connection conn = DriverManager.getConnection(
							sc.getInitParameter("url"),
							sc.getInitParameter("username"),
							sc.getInitParameter("password"));
				
					sc.setAttribute("conn", conn);
					stmt = conn.createStatement();

					
					response.setContentType("text/html; charset=UTF-8");
					prepare = conn.prepareStatement("SELECT * FROM Fitple.Member WHERE Member.email = ? and Member.password = ?" );
					
					
					prepare.setString(1,request.getParameter("email"));
					prepare.setString(2,request.getParameter("password"));
					

					rs=prepare.executeQuery();
				
					while(rs.next()){
						
						
						String member_seq=rs.getString("member_seq");
						map.put("member_seq",member_seq);
						map.put("email", rs.getString("email"));
						map.put("nickname", rs.getString("nickname"));
						map.put("gender", rs.getString("gender"));
						map.put("passwd", rs.getString("password"));
						
						Cookie cookie = new Cookie("email",URLEncoder.encode(member_seq,"euc-kr"));
						cookie.setMaxAge(60*60*24);
						response.addCookie(cookie);
					
						
					}
					j_map.put("responseCode", pName.LOGINSUCCESS);
					j_map.put("myInfo", map);
					
					
					String json = new Gson().toJson(j_map);
					PrintWriter out = response.getWriter();
					
					out.println(json);

					
				} catch (NullPointerException e) {
					j_map.put("responseCode", pName.LOGINFAIL);
					
					
				}catch (Exception e) {
					j_map.put("responseCode", pName.LOGINFAIL);
					
				} finally {

					try {if (rs != null) rs.close();} catch(Exception e) {}
					try {if (stmt != null) stmt.close();} catch(Exception e) {}

				}

			}
			

	


}
