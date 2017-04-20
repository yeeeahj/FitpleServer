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

public class GroupAddServlet extends HttpServlet implements pName{
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
			
			//get Cookie
			Cookie[] cookies = request.getCookies();
			System.out.println(request.getCookies());
			for(int i = 0; i<cookies.length;i++){
				CookieId=cookies[i].getValue();
				
			}
			 
			
			
			

			int group_result;
			int groupmember_result;
			

			String saveFolder = sc.getRealPath("image");
			//String saveFolder = "/Users/oosl1/Documents/apache-tomcat-7.0.62/webapps/manager/images";
			String imageURL= "http://182.162.104.185:8080/Fitple/image";
			
			//占쎈뼄占쎌젫 占쏙옙占쎌삢野껋럥以� /Users/oosl1/Documents/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/Fitple/image
			
			int sizeLimit=1024*1024*5;

			MultipartRequest multi = new MultipartRequest(request, saveFolder,sizeLimit, "UTF-8",new DefaultFileRenamePolicy());
			String fileName=multi.getFilesystemName("photo");
			imageURL=imageURL+"/"+fileName;
			//System.out.println("imageurl"+imageURL);
			if(fileName == null) { 
	            out.print("11111111");
	        } else { 
	            fileName=new String(fileName.getBytes("8859_1"),"euc-kr"); 
	        } 
			
			// YJ:add group
			prepare = conn.prepareStatement(
					"INSERT INTO Fitple.Group(groupname,group_info,host,group_image,public_seq,permission_seq)"
							+ "values(?,?,?,?,?,?)");

			prepare.setString(1, multi.getParameter("groupname"));
			prepare.setString(2, multi.getParameter("group_info"));
			prepare.setString(3, CookieId);
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
					"INSERT INTO Fitple.GroupMember(group_seq,member_seq)"
							+ "values(?,?)");

			prepare.setString(1, group_seq);
			prepare.setString(2, CookieId);
			groupmember_result = prepare.executeUpdate();

			
			if (group_result == 0 || groupmember_result==0) {
				j_map.put("responseCode", pName.ADDGROUPFAIL);

			} else {
				j_map.put("responseCode", pName.ADDGROUPSUCCESS);

				list_map.put("groupname", multi.getParameter("groupname"));
				list_map.put("group_info", multi.getParameter("group_info"));
				list_map.put("host", CookieId);
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
			map.put("responseCode", pName.ADDGROUPFAIL);
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
