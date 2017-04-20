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
import com.google.gson.GsonBuilder;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
//1516
public class FeedAddServlet extends HttpServlet{
	protected void doPost(
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		ServletContext sc = this.getServletContext();
		Statement stmt = null;
		ResultSet rs = null;
		PreparedStatement prepare=null;
		String CookieId=null;
		int result;
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
			

			String saveFolder = sc.getRealPath("image");
			String imageURL= "http://203.252.219.238:8080/Fitple/image";
			
			//실제 저장경로 /Users/oosl1/Documents/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/Fitple/image
			
			int sizeLimit=1024*1024*5;

			MultipartRequest multi = new MultipartRequest(request, saveFolder,sizeLimit, "UTF-8",new DefaultFileRenamePolicy());
			String fileName=multi.getFilesystemName("photo");
			imageURL=imageURL+"/"+fileName;
			
			if(fileName == null) { // 파일이 업로드 되지 않았을때
	            System.out.print("파일 업로드 되지 않았음");
	        } else { // 파일이 업로드 되었을때
	            fileName=new String(fileName.getBytes("8859_1"),"euc-kr"); // 한글인코딩 - 브라우져에 출력
	        } 
			
			 Date d = new Date();
		    String s = d.toString();
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	       
			
			
			

			//YJ:쿼리문 생성
			prepare = conn.prepareStatement("insert into feed(group_seq,content,member_seq,image,date) values(?,?,?,?,?)");
			prepare.setString(1,multi.getParameter("group_seq"));
			prepare.setString(2,multi.getParameter("content"));
			prepare.setString(3,CookieId);
			prepare.setString(4,imageURL);
			prepare.setString(5,sdf.format(d));
			result = prepare.executeUpdate();
			if (result == 0 ) {
				a_map.put("responseCode", 16);

			} else {
				a_map.put("responseCode", 15);
			}
			
			
			
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

			try {if (rs != null) rs.close();} catch(Exception e) {}
			try {if (stmt != null) stmt.close();} catch(Exception e) {}

		}

	}
}
