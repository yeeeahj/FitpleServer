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
import com.google.gson.GsonBuilder;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import com.oreilly.servlet.*;

@SuppressWarnings("serial")

// ����� 5/ ���н� 6
public class GroupAddTestServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("euc-kr");

		ServletContext sc = this.getServletContext();
		Statement stmt = null;
		ResultSet rs = null;
		PreparedStatement prepare = null;
		String CookieId=null;

		try {
			// YJ:DB����
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(sc.getInitParameter("url"), sc.getInitParameter("username"),
					sc.getInitParameter("password"));

			sc.setAttribute("conn", conn);
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			
			//get Cookie
			Cookie[] cookies = request.getCookies();
			System.out.println(request.getCookies());
			System.out.println("도");

			
			 
			
			
			

			int group_result;
			int groupmember_result;
			

			String saveFolder = sc.getRealPath("image");
			//String saveFolder = "/Users/oosl1/Documents/apache-tomcat-7.0.62/webapps/manager/images";
			String imageURL= "http://203.252.219.238:8080/Fitple/image";
			
			//실제 저장경로 /Users/oosl1/Documents/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/Fitple/image
			
			int sizeLimit=1024*1024*5;

			MultipartRequest multi = new MultipartRequest(request, saveFolder,sizeLimit, "UTF-8",new DefaultFileRenamePolicy());
			String fileName=multi.getFilesystemName("photo");
			imageURL=imageURL+"/"+fileName;
			System.out.println("imageurl"+imageURL+multi.getParameter("groupname"));
			String qqqqq = new String(multi.getParameter("group_info").getBytes("UTF-8"),"euc-kr");
			System.out.println(qqqqq);
			if(fileName == null) { // 파일이 업로드 되지 않았을때
	            System.out.print("파일 업로드 되지 않았음");
	        } else { // 파일이 업로드 되었을때
	            fileName=new String(fileName.getBytes("8859_1"),"euc-kr"); // 한글인코딩 - 브라우져에 출력
	        } 
			
			// YJ:add group
			prepare = conn.prepareStatement(
					"INSERT INTO Fitple.GROUP(groupname,group_info,host,group_image,public_seq,permission_seq)"
							+ "values(?,?,?,?,?,?)");

			prepare.setString(1, multi.getParameter("groupname"));
			prepare.setString(2, multi.getParameter("group_info"));
			prepare.setString(3, multi.getParameter("member_seq"));
			prepare.setString(4, imageURL);
			prepare.setString(5, multi.getParameter("public_seq"));
			prepare.setString(6, multi.getParameter("permission_seq"));

			group_result = prepare.executeUpdate();


			Map<String, String> map = new HashMap();
			List<Map<String, String>> list = new ArrayList();
			Map<String, String> list_map = new HashMap();
			Map<String, Object> j_map = new HashMap();

			


			
			//select group_seq
			prepare = conn.prepareStatement(
					"SELECT GROUP_SEQ FROM Fitple.Group WHERE groupname=?");
			String group_seq="";

			prepare.setString(1, multi.getParameter("groupname"));
			rs=prepare.executeQuery();
			while(rs.next()){
				group_seq=rs.getString("group_seq");
			}
			
			//add groupmember
			prepare = conn.prepareStatement(
					"INSERT INTO Fitple.GROUPMEMBER(group_seq,member_seq)"
							+ "values(?,?)");

			prepare.setString(1, group_seq);
			prepare.setString(2, multi.getParameter("member_seq"));
			groupmember_result = prepare.executeUpdate();

			
			if (group_result == 0 || groupmember_result==0) {
				j_map.put("responseCode", 6);

			} else {
				j_map.put("responseCode", 5);

				list_map.put("groupname", multi.getParameter("groupname"));
				list_map.put("group_info", multi.getParameter("group_info"));
				list_map.put("host",  multi.getParameter("member_seq"));
				list_map.put("group_image", imageURL);
				list_map.put("public_seq", multi.getParameter("public_seq"));
				list_map.put("permission_seq", multi.getParameter("permission_seq"));
				j_map.put("GroupData", list_map);
			}
			String json = new Gson().toJson(j_map);
			out.println(json);
			System.out.println(json);

		} catch (Exception e) {
			e.printStackTrace();
			PrintWriter out = response.getWriter();
			Map<String,Object> map = new HashMap();
			map.put("responseCode", 6);
			Gson gson = new GsonBuilder().serializeNulls().create();
			String json = gson.toJson(map);
			//PrintWriter out = response.getWriter();
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
