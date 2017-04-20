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
public class MainCommunityServlet extends HttpServlet{
	

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
			
			List<Map<String,Object>> main_list = new ArrayList();  //main

			
			ArrayList<Integer> grouplists=new ArrayList<Integer>(); //hot
			ArrayList<Integer> main_grouplists=new ArrayList<Integer>();//main
			
			


			
			//main project 지정 (2개 ) 일단 2개 나중에  main 그룹을 위한 데이터베이스 만들어서 그안에서 2개 랜덤값으로 뽑아내야 겟지?
			main_grouplists.add(14);
			main_grouplists.add(19);
			
			for(int j=0;j<main_grouplists.size();j++){
				prepare = conn.prepareStatement("select group_seq,groupname,group_info,group_image from fitple.group where group_seq=? ");
				
				prepare.setString(1,main_grouplists.get(j)+"");
				rs2=prepare.executeQuery();
				while(rs2.next()){
					Map<String,Object> main_map = new HashMap(); 
					main_map.put("group_seq", rs2.getString("group_seq"));
					main_map.put("groupname", rs2.getString("groupname"));
					main_map.put("group_info", rs2.getString("group_info"));
					main_map.put("group_image", rs2.getString("group_image"));
					
					//나중에 통계하면 여기다 붙이면 
					main_list.add(main_map);
				}
			}
			
			
		

		
			j_map.put("responseCode", 31);

			j_map.put("Main_group", main_list);
			
			
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
