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


public class JoinServlet extends HttpServlet implements pName{
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
			System.out.println("post");
					
			try{
				Class.forName("com.mysql.jdbc.Driver");
				Connection conn = DriverManager.getConnection(
						sc.getInitParameter("url"),
						sc.getInitParameter("username"),
						sc.getInitParameter("password"));
			
				sc.setAttribute("conn", conn);
				
				   
				stmt = conn.createStatement();
				
				
				List<Map<String,String>> list = new ArrayList();
				Map<String,Object> j_map = new HashMap(); 
				response.setContentType("text/html; charset=UTF-8");
				if(request.getParameter("email").equals("")||request.getParameter("nickname").equals(null)){

					j_map.put("responseCode", 2);
				}else{

				prepare = conn.prepareStatement("INSERT INTO Member(nickname,email,password,gender)"+
						"values(?,?,?,?)");
				
				prepare.setString(1,request.getParameter("nickname"));
				prepare.setString(2,request.getParameter("email"));
				prepare.setString(3,request.getParameter("password"));
				prepare.setString(4,request.getParameter("gender"));

				responseCode=prepare.executeUpdate();
			
				j_map.put("responseCode", responseCode);
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
