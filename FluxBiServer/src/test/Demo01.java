package test;

import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Demo01
 */
@WebServlet("/Demo01")
public class Demo01 extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 允许跨域请求访问
		response.setHeader("Access-Control-allow-Origin","*");
		response.setHeader("Cache-Control","no-cache");
		
		String result="[";
		for(int i=0;i<6;i++){
			int number = new Random().nextInt(100);
			result=result+number+",";
		}
		result=result+new Random().nextInt(100)+"]";
		
		response.getWriter().write(result);
		
		/*String info = "hello 1910";
		response.getWriter().write(info);*/
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
