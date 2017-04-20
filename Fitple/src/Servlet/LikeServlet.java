package Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

//import org.apache.jasper.tagplugins.jstl.core.Out;
//import org.apache.tomcat.jdbc.pool.DataSource;

import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

//import net.sf.json.JSON;
//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject; 
//import net.sf.json.xml.XMLSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings("serial")

//b25 p26
public class LikeServlet extends HttpServlet{
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
			String CookieId=null;
			String korean=null;
					
			try{
			
				Class.forName("com.mysql.jdbc.Driver");
				Connection conn = DriverManager.getConnection(
						sc.getInitParameter("url"),
						sc.getInitParameter("username"),
						sc.getInitParameter("password"));
			
				sc.setAttribute("conn", 
						conn);
				
				   
				stmt = conn.createStatement();
				Cookie[] cookies = request.getCookies();
				for(int i = 0; i<cookies.length;i++){
					CookieId=cookies[i].getValue();
					
				}
				
				List<Map<String,String>> list = new ArrayList();
				Map<String,Object> j_map = new HashMap(); 
				response.setContentType("text/html; charset=UTF-8");
		
				
				if(request.getParameter("like_st").equals("0")){
				
					prepare = conn.prepareStatement("INSERT INTO Fitple.Like(feed_seq,member_seq)"+"values(?,?)");
					prepare.setString(1,request.getParameter("feed_seq"));
					prepare.setString(2,CookieId);
					
				}else{
					prepare = conn.prepareStatement("DELETE FROM `Fitple`.`Like` WHERE `FEED_SEQ`=?");
					prepare.setString(1,request.getParameter("feed_seq"));
													
				}
				responseCode=prepare.executeUpdate();
			
				
				if(responseCode==0){

					j_map.put("responseCode", "26");
						
				}else{

					j_map.put("responseCode", "25");
					
				}
				
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
