package Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
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

public class GroupListServlet extends HttpServlet implements pName{
	

	@Override
	protected void doPost(
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		ServletContext sc = this.getServletContext();
		Statement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		PreparedStatement prepare=null;
		PreparedStatement prepare2=null;
		PreparedStatement prepare3=null;
		String CookieId=null;
		
		
		try{
			//YJ:DB�뿰寃�
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
			System.out.println(request.getCookies());
			
				for(int i = 0; i<cookies.length;i++){
					CookieId=cookies[i].getValue();
				}
			
			
			
			List<Map<String,Object>> list = new ArrayList();
			Map<String,Object> j_map = new HashMap(); 
			
			ArrayList<Integer> grouplists=new ArrayList<Integer>();
			

			Date d = new Date();
			String s = d.toString();
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		       
			prepare = conn.prepareStatement("select group_seq from groupmember where groupmember.member_seq=?");
			prepare.setString(1,CookieId);
			
			rs=prepare.executeQuery();
			while(rs.next()){
				
				grouplists.add(Integer.parseInt(rs.getString("group_seq")));
				
				
			}
			
			for(int j=0;j<grouplists.size();j++){
				prepare = conn.prepareStatement("select group_seq,groupname from fitple.group where group_seq=?");
				prepare.setString(1,grouplists.get(j)+"");
				
				rs=prepare.executeQuery();
				while(rs.next()){
					
				 	float feed=0;
				 	float member=0;
					
					Map<String,Object> map = new HashMap(); 
					String aaa = sdf.format(d)+"%";
					
					map.put("group_seq", rs.getString("group_seq"));
					map.put("groupname", rs.getString("groupname"));
					
					prepare2 = conn.prepareStatement("select FEED_SEQ from fitple.FEED where GROUP_SEQ=? and DATE like ?");
					//prepare2 = conn.prepareStatement("select FEED_SEQ from fitple.FEED where GROUP_SEQ=?");
					prepare2.setString(1,grouplists.get(j)+"");
					prepare2.setString(2, aaa);
					rs2=prepare2.executeQuery();
					
					
					while(rs2.next()){
						feed++;
						
					}
					
					prepare3 = conn.prepareStatement("SELECT MEMBER_SEQ FROM Fitple.GroupMember WHERE GROUP_SEQ =? ");
					prepare3.setString(1,grouplists.get(j)+"");
					rs3=prepare3.executeQuery();
					
					while(rs3.next()){
						member++;
					}
					
					map.put("statistics", Math.round(feed/member*100)+"");
					
					
					list.add(map);
				}
			}
			
		
		
			j_map.put("responseCode", pName.LISTGROUPSUCCESS);
			j_map.put("group", list);
			
			
			String json = new Gson().toJson(j_map);
			PrintWriter out = response.getWriter();
			out.println(json);

			
		} catch (Exception e) {
			e.printStackTrace();
			Map<String,Object> j_map = new HashMap(); 
			PrintWriter out = response.getWriter();
			j_map.put("responseCode",  pName.LISTGROUPFAIL);
			String json = new Gson().toJson(j_map);
			out.println(json);
			
		} finally {

			try {if (rs != null) rs.close();} catch(Exception e) {}
			try {if (stmt != null) stmt.close();} catch(Exception e) {}

		}

	}

}
