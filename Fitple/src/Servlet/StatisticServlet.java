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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StatisticServlet extends HttpServlet{

	protected void doPost(
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		ServletContext sc = this.getServletContext();
		Statement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;
		ResultSet rs5 = null;
		PreparedStatement prepare=null;
		String CookieId=null;
		int tatalMem = 0;
		int participants =0;
		
		List<Map<String,Object>> list = new ArrayList(); 
		List<Map<String,Object>> list2 = new ArrayList(); 
		List<Map<String,Object>> list3 = new ArrayList(); 
		List<Map<String,Object>> list4 = new ArrayList();
		Map<String,Object> a_map = new HashMap(); 
		
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
			 Date d = new Date();
			 String s = d.toString();
		     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		     String dateString = sdf.format(d)+"%";


			//전체 사용자 수 
			prepare = conn.prepareStatement("select count(*) from fitple.groupmember where fitple.groupmember.group_Seq =?");
			prepare.setString(1,request.getParameter("group_seq"));
			
			rs=prepare.executeQuery();
			while(rs.next()){
			//	a_map.put("responseCode", 50);
				tatalMem = Integer.parseInt(rs.getString("count(*)"));
				a_map.put("total_member",tatalMem+"");
				
			
			}
			// 인증한 회원 수 
			prepare = conn.prepareStatement("select count(member_seq) cnt from (select DISTINCT  member_seq from fitple.FEED where GROUP_SEQ=? and DATE like ?) cnt");
			prepare.setString(1,request.getParameter("group_seq"));
			prepare.setString(2,dateString);
			rs2=prepare.executeQuery();
			while(rs2.next()){
				participants= Integer.parseInt(rs2.getString("cnt"));
				a_map.put("participants", participants);
				
			
			}
			
			// 인증 한 멤버의 닉네임과 이미지 
			prepare = conn.prepareStatement("select nickname, image from Fitple.Member where member_seq in  (select member_seq from fitple.FEED where GROUP_SEQ=? and DATE like ?)");
			prepare.setString(1,request.getParameter("group_seq"));
			prepare.setString(2,dateString);
			rs3=prepare.executeQuery();
			while(rs3.next()){
			//	a_map.put("responseCode", 50);
				rs3.getString("nickname");
				rs3.getString("image");
				
				Map<String,Object> map = new HashMap(); 
				map.put("nickname", rs3.getString("nickname"));
				map.put("image", rs3.getString("image"));
				list.add(map);
			}
			a_map.put("paticipants_info",list);
			
			//인증x 한 멤버의 닉네임과 이미지 
			prepare = conn.prepareStatement("select nickname, image from Fitple.Member where member_seq NOT in  (select member_seq from fitple.FEED where GROUP_SEQ=? and DATE like ?)");
			prepare.setString(1,request.getParameter("group_seq"));
			prepare.setString(2,dateString);
			rs3=prepare.executeQuery();
			while(rs3.next()){
			//	a_map.put("responseCode", 50);
				rs3.getString("nickname");
				rs3.getString("image");
				
				Map<String,Object> map = new HashMap(); 
				map.put("nickname", rs3.getString("nickname"));
				map.put("image", rs3.getString("image"));
				list4.add(map);
			}
			a_map.put("unparticipant_info",list4);
			
			
			// 내가 운동 한 날짜 
			prepare = conn.prepareStatement("select fitple.feed.DATE from fitple.feed where fitple.feed.group_seq=? and fitple.feed.member_Seq=?;");
			prepare.setString(1,request.getParameter("group_seq"));
			prepare.setString(2,CookieId);
			rs4=prepare.executeQuery();
			while(rs4.next()){
						//	a_map.put("responseCode", 50);
							rs4.getString("DATE");
							Map<String,Object> map = new HashMap(); 
							map.put("date", rs4.getString("DATE"));
							list2.add(map);
							
						};
			a_map.put("totalData",list2);
			
			prepare = conn.prepareStatement("select fitple.member.MEMBER_SEQ, member.nickname, fitple.member.image ,count(feed_Seq) from Fitple.Member , Fitple.GroupMember ,Fitple.feed where Fitple.GroupMember.group_seq=? and Fitple.GroupMember.member_seq = Fitple.Member.member_seq  and Fitple.feed.group_seq = Fitple.GroupMember.group_seq and  Fitple.feed.member_seq = Fitple.Member.member_seq  group by fitple.member.MEMBER_SEQ order by count(feed_seq) DESC; ");
			prepare.setString(1,request.getParameter("group_seq"));
			
			rs5=prepare.executeQuery();
			while(rs5.next()){
							Map<String,Object> map = new HashMap(); 
							map.put("nickname", rs5.getString("nickname"));
							map.put("image", rs5.getString("image"));
							map.put("count", rs5.getString("count(feed_Seq)"));
							list3.add(map);
							
						};
						a_map.put("totalAuth",list3);
						
			a_map.put("statistics", ((participants*100)/(tatalMem)));
			System.out.println(((participants*100)/(tatalMem)));
			
			Gson gson = new GsonBuilder().serializeNulls().create();
			String json = gson.toJson(a_map);
			PrintWriter out = response.getWriter();
			out.println(json);

			
		} catch (Exception e) {
			e.printStackTrace();
			a_map.put("responseCode", 16);
			String json = new Gson().toJson(a_map);
			PrintWriter out = response.getWriter();
			out.println(json);
			
		} finally {

			try {if (rs != null) rs.close();
			if (rs2 != null) rs.close();
			if (rs3 != null) rs.close();
			if (rs4 != null) rs.close();
			if (rs5 != null) rs.close();} catch(Exception e) {}
			try {if (stmt != null) stmt.close();} catch(Exception e) {}

		}

	}
}
