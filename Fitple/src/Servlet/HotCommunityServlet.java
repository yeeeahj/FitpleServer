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


// 31,30
public class HotCommunityServlet extends HttpServlet{
	

	@Override
	protected void doPost(
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		ServletContext sc = this.getServletContext();
		Statement stmt = null;
		ResultSet rs = null;
		
		PreparedStatement prepare=null;
		String CookieId=null;
		
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
				CookieId=cookies[i].getValue();
			}
			
			
			List<Map<String,Object>> list = new ArrayList(); 
			Map<String,Object> j_map = new HashMap(); 


			 
			ArrayList<Integer> grouplists=new ArrayList<Integer>(); //hot

			
			

			//YJ:쿼리문 생성
			prepare = conn.prepareStatement("select group_seq,avg(statistics) from Fitple.Statistics group by group_seq order by avg(statistics)  DESC;");
			//prepare.setString(1,CookieId);
			
			rs=prepare.executeQuery();
			while(rs.next()){
				
				grouplists.add(Integer.parseInt(rs.getString("group_seq")));
				
				
			}
			
			
			
		
			
		
				prepare = conn.prepareStatement("select group_seq,groupname,group_info,group_image from fitple.group g1 where g1.group_seq in( select group_seq from (select group_seq,avg(statistics) from Fitple.Statistics group by group_seq order by avg(statistics)  DESC) g2);");
				rs=prepare.executeQuery();
				while(rs.next()){
					Map<String,Object> map = new HashMap(); 
					map.put("group_seq", rs.getString("group_seq"));
					map.put("groupname", rs.getString("groupname"));
					map.put("group_info", rs.getString("group_info"));
					map.put("group_image", rs.getString("group_image"));
					
					//나중에 통계하면 여기다 붙이면 
					list.add(map);
				}
			
			

			
		

		
			j_map.put("responseCode", 31);
			j_map.put("Group", list);

			
			
			String json = new Gson().toJson(j_map);
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
