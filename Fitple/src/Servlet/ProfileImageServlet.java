package Servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import com.oreilly.servlet.*;

@SuppressWarnings("serial")

// success:27 fail:28
public class ProfileImageServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		ServletContext sc = this.getServletContext();
		Statement stmt = null;
		ResultSet rs = null;
		PreparedStatement prepare = null;
		String CookieId=null;

		try {
			
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(sc.getInitParameter("url"), sc.getInitParameter("username"),
					sc.getInitParameter("password"));

			sc.setAttribute("conn", conn);
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			
			
			Cookie[] cookies = request.getCookies();
			for(int i = 0; i<cookies.length;i++){
				CookieId=cookies[i].getValue();
				
			}

			int group_result;
			

			String saveFolder = sc.getRealPath("image");
			//String saveFolder = "/Users/oosl1/Documents/apache-tomcat-7.0.62/webapps/manager/images";
			String imageURL= "http://203.252.219.238:8080/Fitple/image";
			
			//실제 저장경로 /Users/oosl1/Documents/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/Fitple/image
			
			int sizeLimit=1024*1024*5;

			MultipartRequest multi = new MultipartRequest(request, saveFolder,sizeLimit, "UTF-8",new DefaultFileRenamePolicy());
			String fileName=multi.getFilesystemName("photo");
			imageURL=imageURL+"/"+fileName;
			//System.out.println("imageurl"+imageURL);
			if(fileName == null) { // 파일이 업로드 되지 않았을때
	            System.out.print("파일 업로드 되지 않았음");
	        } else { // 파일이 업로드 되었을때
	            fileName=new String(fileName.getBytes("8859_1"),"euc-kr"); // 한글인코딩 - 브라우져에 출력
	        } 
			
			// YJ:add group
			prepare = conn.prepareStatement("UPDATE `Fitple`.`Member` SET `IMAGE`=? WHERE `MEMBER_SEQ`=?");

			prepare.setString(1, imageURL);
			prepare.setString(2, CookieId);
			
			group_result = prepare.executeUpdate();
			Map<String, Object> j_map = new HashMap();

			if (group_result == 0) {
				j_map.put("responseCode", 28);

			} else {
				j_map.put("responseCode", 27);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("error", e);
			RequestDispatcher rd = request.getRequestDispatcher("/Error.jsp");
			rd.forward(request, response);

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
