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
import java.util.Calendar;
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
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
//success 21 fail 22
public class CommentWriteServlet extends HttpServlet{
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
		

		try {
			request.setCharacterEncoding("UTF-8");
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
		
			System.out.println(request.getParameter("feed_seq"));
			
			Date d = new Date();
			String s = d.toString();
		       // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		       
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			
			//add groupmember =>나중에 public check !!!!!!
			prepare = conn.prepareStatement(
					"INSERT INTO Fitple.COMMENT(feed_seq,member_seq,date,comment)"
							+ "values(?,?,?,?)");

			prepare.setString(1, request.getParameter("feed_seq"));
			prepare.setString(2, CookieId);
			prepare.setString(3, dateFormat.format(d));
			prepare.setString(4, request.getParameter("comment"));
			
			result = prepare.executeUpdate();

			
			if (result == 0) {
				m_map.put("responseCode", 22);

			} else {
				m_map.put("responseCode", 21);

			}
			

			String json = new Gson().toJson(m_map);
			PrintWriter out = response.getWriter();
			out.println(json);
			System.out.println(json);

		} catch (Exception e) {
			e.printStackTrace();
			m_map.put("responseCode", 22);
			String json = new Gson().toJson(m_map);
			PrintWriter out = response.getWriter();
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
