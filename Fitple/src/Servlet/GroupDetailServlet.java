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

public class GroupDetailServlet extends HttpServlet{
	@Override
	protected void doPost(
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		ServletContext sc = this.getServletContext();
		Statement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		PreparedStatement prepare=null;
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
			List<Map<String,Object>> m_list = new ArrayList();
			Map<String,Object> j_map = new HashMap(); 
			
			ArrayList<Integer> grouplists=new ArrayList<Integer>();
			
			Map<String,Object> map = new HashMap();
			
			
			//YJ:쿼리문 생성
			prepare = conn.prepareStatement(" select fitple.groupmember.group_seq, groupname, group_info, group_image , PUBLIC_SEQ,  PERMISSION_SEQ, count(member_seq) as num from fitple.Group , fitple.groupmember  where fitple.group.group_seq=? and fitple.group.group_seq = fitple.groupmember.group_seq group by fitple.groupmember.group_seq;");
			prepare.setString(1,request.getParameter("group_seq"));
			rs=prepare.executeQuery();
			while(rs.next()){

				map.put("group_info", rs.getString("group_info"));
				map.put("group_seq", rs.getString("group_seq"));
				map.put("groupname", rs.getString("groupname"));
				map.put("group_image", rs.getString("group_image"));
				map.put("public_seq", rs.getString("public_seq"));
				map.put("permission_seq", rs.getString("permission_seq"));
				map.put("member_num", rs.getString("num"));
				list.add(map);
			
			}
			
			prepare2 = conn.prepareStatement(" select fitple.groupmember.member_seq , nickname,member.image from fitple.groupmember, fitple.member where group_seq =? and fitple.member.member_seq = fitple.groupmember.member_seq;");
			prepare2.setString(1,request.getParameter("group_seq"));
			rs2=prepare2.executeQuery();
			while(rs2.next()){
				Map<String,Object> m_map = new HashMap();
				m_map.put("member_seq", rs2.getString("member_seq"));
				m_map.put("nickname", rs2.getString("nickname"));
				m_map.put("image", rs2.getString("image"));
				m_list.add(m_map);
			}
			

			
			j_map.put("responseCode", 11);
			j_map.put("Group", list);
			j_map.put("Member", m_list);
			
			
			
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
