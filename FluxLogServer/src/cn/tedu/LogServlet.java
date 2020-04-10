package cn.tedu;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonFormat.Value;

public class LogServlet extends HttpServlet {
	
	private Logger logger = Logger.getLogger(LogServlet.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*1.获取埋点信息
		 * ①按&切分，获取属性对
		 * ②获取属性值，用|拼接
		 * url=http://localhost:8080/FluxAppServer/a.jsp&urlname=a.jsp&title=页面A&chset=UTF-8&scr=1440x960&col=24-bit&lg=zh-cn&je=0&ce=1&fv=&cnv=0.8130548035843617&ref=&uagent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36&stat_uv=34553081984501874499&stat_ss=7027847835_0_1586500098506	
		 * url=http://localhost:8080/FluxAppServer/a.jsp|a.jsp|页面A|UTF-8|&scr=1440x960&col=24-bit&lg=zh-cn&je=0&ce=1&fv=&cnv=0.8130548035843617&ref=&uagent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36&stat_uv=34553081984501874499&stat_ss=7027847835_0_1586500098506
		 * */
		String info = URLDecoder.decode(request.getQueryString(),"utf-8");
		System.out.println(info);
		String[] kvs = info.split("\\&");
		StringBuffer buffer = new StringBuffer();
		for(String kv : kvs){
			/*获取value时，可能出现数组越界问题
			 * 如ref(来路页面)属性,可能没有value	*/			
			String value = kv.split("=").length == 2 ? kv.split("=")[1] : "";
			buffer.append(value+"|");
		}
		//2.拼接用户IP
		buffer.append(request.getRemoteAddr());	
		System.out.println(buffer);
		
		/*Logger记录的级别从低到高依次为debug >> info >> warn >> error >> fatal
		 *使用log4j还需要有log4j.properties*/
		logger.info(buffer);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
