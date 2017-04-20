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

public class FeedServlet extends HttpServlet implements pName{
	protected void doPost(
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		ServletContext sc = this.getServletContext();
		Statement stmt = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;
		
		PreparedStatement prepare=null;
		PreparedStatement prepare1=null;
		PreparedStatement prepare2=null;
		PreparedStatement prepare3=null;
		PreparedStatement prepare4=null;
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
			
			
			//get Cookie
			Cookie[] cookies = request.getCookies();
			for(int i = 0; i<cookies.length;i++){
				System.out.println(cookies[i].getName()+cookies[i].getValue());
				CookieId=cookies[i].getValue();
			}
			
			
			List<Map<String,Object>> list = new ArrayList();
			Map<String,Object> f_map = new HashMap(); 
			Map<String,Object> g_map = new HashMap(); 
			ArrayList<Integer> grouplists=new ArrayList<Integer>();
			

			prepare = conn.prepareStatement("select feed_seq, group_seq,member_seq,content,date,image from Feed where group_seq=? order by date DESC");
			prepare.setString(1,request.getParameter("group_seq"));
			rs=prepare.executeQuery();
			while(rs.next()){
				Map<String,Object> c_map = new HashMap(); 
				
				String feed_seq = rs.getString("feed_seq");
				String member_seq = rs.getString("member_seq");
				
				int count = 0;
				int like = 0;
			
				c_map.put("feed_seq", feed_seq);
				c_map.put("member_seq", member_seq);
				c_map.put("content", rs.getString("content"));
				c_map.put("date", rs.getString("date"));
				c_map.put("image", rs.getString("image"));
				
				prepare1 = conn.prepareStatement("select nickname from Fitple.Member where member_seq=? ");
				prepare1.setString(1,member_seq);
				rs1=prepare1.executeQuery();
				
				while(rs1.next()){
					c_map.put("nickname", rs1.getString("nickname"));
				}
							
				
				prepare2 = conn.prepareStatement("select COMMENT_SEQ from Fitple.Comment where feed_seq=? ");
				prepare2.setString(1,feed_seq);
				rs2=prepare2.executeQuery();
				
				while(rs2.next()){
					count++;
				}
				
				c_map.put("comment_num", count+"");
				
				
				prepare3 = conn.prepareStatement("select member_seq from Fitple.LIKE where feed_seq=? ");
				prepare3.setString(1,feed_seq);
				rs3=prepare3.executeQuery();
				
				while(rs3.next()){
					if(rs3.getString("member_seq").equals(CookieId)){
						like = 1;
					}
				}
				
				c_map.put("like_st", like+"");
				
			
				prepare4 = conn.prepareStatement("SELECT count(*) as num FROM Fitple.LIKE where feed_seq=? ");
				prepare4.setString(1,feed_seq);
				rs4=prepare4.executeQuery();
				
				while(rs4.next()){
					c_map.put("like_num", rs4.getString("num"));
					
				}
				
				
				
				list.add(c_map);
				
				
			}
			
			prepare = conn.prepareStatement("select group_seq,groupname,group_image from Fitple.Group where group_seq=?");
			prepare.setString(1,request.getParameter("group_seq"));
			rs=prepare.executeQuery();
			while(rs.next()){
				g_map.put("group_seq", rs.getString("group_seq"));
				g_map.put("groupname", rs.getString("groupname"));
				g_map.put("group_image", rs.getString("group_image"));
				
			}
			
			
			
			f_map.put("responseCode", pName.LISTFEEDSUCCESS);
			f_map.put("group", g_map);
			f_map.put("feed", list);
			
			
			String json = new Gson().toJson(f_map);
			PrintWriter out = response.getWriter();
			out.println(json);

			
		} catch (Exception e) {
			Map<String,Object> j_map = new HashMap(); 
			j_map.put("responseCode", pName.LISTFEEDFAIL);
			String json = new Gson().toJson(j_map);
			PrintWriter out = response.getWriter();
			out.println(json);
			
		} finally {

			try {if (rs != null) rs.close();} catch(Exception e) {}
			try {if (stmt != null) stmt.close();} catch(Exception e) {}

		}

	}
}
