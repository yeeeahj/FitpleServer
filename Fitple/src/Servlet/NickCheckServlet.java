package Servlet;

import java.beans.Statement;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class NickCheckServlet extends HttpServlet implements pName {


	@Override
	protected void doPost(
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		Statement stmt = null;
		ResultSet rs = null;
		PreparedStatement prepare=null;
		
		PrintWriter out = response.getWriter();
		Map<String,Object> map = new HashMap();
	
		try{
			ServletContext sc = this.getServletContext();
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(
					sc.getInitParameter("url"),
					sc.getInitParameter("username"),
					sc.getInitParameter("password"));
			sc.setAttribute("conn", conn);
			

			response.setContentType("text/html; charset=UTF-8");
			
			
			prepare = conn.prepareStatement("SELECT count(*) AS nickcount  FROM Fitple.Member WHERE Fitple.Member.nickname = ?");
			
			prepare.setString(1, request.getParameter("nickname"));
			rs=prepare.executeQuery();

			while(rs.next()){
				if(Integer.parseInt(rs.getString("nickcount")) == 0){
					map.put("responseCode",pName.NICKSUCCESS);
				}else{
					map.put("responseCode",pName.NICKFAIL);
				}
				
			}
			String json = new Gson().toJson(map);
			out.println(json);


			
		} catch (Exception e) {
			e.printStackTrace();
			map.put("responseCode",pName.NICKFAIL);
			String json = new Gson().toJson(map);
			out.println(json);
			
		}finally {

			try {if (rs != null) rs.close();} catch(Exception e) {}
			try {if (stmt != null) rs.close();} catch(Exception e) {}

		}

	}
}
