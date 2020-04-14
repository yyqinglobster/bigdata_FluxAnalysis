package dataview;

import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;

/**
 * 日志流量数据可视化
 * 返回数据格式：data:['pv','uv','vv','newip','newcust']
 * 
 */
@WebServlet("/DataView")
public class DataView extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String result = "[";
		
		for(int i=0;i<4;i++){
			int num = new Random().nextInt(100);
			result = result+num+',';
		}
		result = result+ new Random().nextInt(100) + "]";
		response.getWriter().write(result);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
