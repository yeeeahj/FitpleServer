package Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
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
import com.google.gson.GsonBuilder;

//success 23 fail 24

public class CommentListServlet extends HttpServlet{
	@Override
	protected void doPost(
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		ServletContext sc = this.getServletContext();
		Statement stmt = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		PreparedStatement prepare=null;
		PreparedStatement prepare1=null;
		PreparedStatement prepare2=null;
		String CookieId=null;
		int count = 0;
		try{
			//YJ:DB연결
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(
					sc.getInitParameter("url"),
					sc.getInitParameter("username"),
					sc.getInitParameter("password"));
			sc.setAttribute("conn", conn);
			stmt = conn.createStatement();
			response.setContentType("text/html; charset=UTF-8");
			
			
			//get Cookie
			Cookie[] cookies = request.getCookies();
			for(int i = 0; i<cookies.length;i++){
				System.out.println(cookies[i].getName()+cookies[i].getValue());
				CookieId=cookies[i].getValue();
			}
			
			
			List<Map<String,Object>> list = new ArrayList();
			Map<String,Object> j_map = new HashMap(); 
			
			ArrayList<Integer> grouplists=new ArrayList<Integer>();
			

			prepare = conn.prepareStatement("SELECT COMMENT_SEQ, MEMBER_SEQ, Comment.COMMENT, DATE from Fitple.Comment where FEED_SEQ=?");
			prepare.setString(1,request.getParameter("feed_seq"));
			rs=prepare.executeQuery();
			while(rs.next()){
				j_map.put("responseCode", 23);
				
				Map<String,Object> map = new HashMap(); 
				String member_seq = rs.getString("member_seq");
								
				map.put("comment_seq", rs.getString("comment_seq"));
				map.put("member_seq", member_seq);
				map.put("comment", rs.getString("comment"));
				map.put("date", rs.getString("date"));
				
				prepare1 = conn.prepareStatement("select NICKNAME, IMAGE from fitple.MEMBER where member_seq=? ");
				prepare1.setString(1,member_seq);
				rs1=prepare1.executeQuery();
				
				while(rs1.next()){
					map.put("nickname", rs1.getString("nickname"));
					map.put("image", rs1.getString("image"));					
				}
				list.add(map);
			
			}
			
			
			j_map.put("responseCode", 23);
			j_map.put("comment", list);
			
			
			
			
			//String json = new Gson().toJson(j_map);
			Gson gson = new GsonBuilder().serializeNulls().create();
			String json = gson.toJson(j_map);
			PrintWriter out = response.getWriter();
			out.println(json);

			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("error", e);
			RequestDispatcher rd = request.getRequestDispatcher("/Error.jsp");
			rd.forward(request, response);
			
		} finally {

			try {if (rs != null) rs.close();} catch(Exception e) {}
			try {if (stmt != null) stmt.close();} catch(Exception e) {}

		}

	}
}
