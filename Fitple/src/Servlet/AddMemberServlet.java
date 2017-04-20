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
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
//success 11 fail12
public class AddMemberServlet extends HttpServlet{
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		ServletContext sc = this.getServletContext();
		Statement stmt = null;
		ResultSet rs = null;
		PreparedStatement prepare = null;
		String CookieId=null;
		Map<String,Object> m_map = new HashMap(); 
		PrintWriter out = response.getWriter();

		try {
			
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(sc.getInitParameter("url"), sc.getInitParameter("username"),
					sc.getInitParameter("password"));

			sc.setAttribute("conn", conn);
			response.setContentType("text/html; charset=UTF-8");
			
			
			//get Cookie
			Cookie[] cookies = request.getCookies();
			for(int i = 0; i<cookies.length;i++){
				CookieId=cookies[i].getValue();
				
			}
			int result;
			
			//add groupmember =>나중에 public check !!!!!!
			prepare = conn.prepareStatement(
					"INSERT INTO Fitple.GROUPMEMBER(group_seq,member_seq)"
							+ "values(?,?)");

			prepare.setString(1, request.getParameter("group_seq"));
			prepare.setString(2, request.getParameter("member_seq"));
			result = prepare.executeUpdate();

			
			if (result == 0) {
				m_map.put("responseCode", 12);

			} else {
				m_map.put("responseCode", 11);

			}
			

			String json = new Gson().toJson(m_map);

			out.println(json);
			System.out.println(json);

		} catch (Exception e) {
			e.printStackTrace();
			m_map.put("responseCode", 12);
			String json = new Gson().toJson(m_map);
			out.println(json);

		} finally {

			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
			}

		}

	}
}
