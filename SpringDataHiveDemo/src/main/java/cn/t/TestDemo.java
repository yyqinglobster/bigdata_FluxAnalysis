package cn.t;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.hadoop.hive.HiveClient;
import org.springframework.data.hadoop.hive.HiveClientCallback;
import org.springframework.data.hadoop.hive.HiveTemplate;

public class TestDemo {
	
	@Test
	public void selectAll(){
		// 通过配置文件初始化spring容器
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		// 通过注入的代理对象
		HiveTemplate proxy = (HiveTemplate) context.getBean("hiveTemplate");
		// 先切换到指定的数据库
		proxy.query("use springdata_test");
		// query做单列的简单查询
		List<String> results = proxy.query("select name from tb1");
		System.out.println(results);
	}

	@Test
	public void execute(){
		// 通过配置文件初始化spring容器
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		// 通过注入的代理对象
		HiveTemplate proxy = (HiveTemplate) context.getBean("hiveTemplate");
		// 先切换到指定的数据库
		proxy.query("use springdata_test");
		// 通过execute方法，底层通过jdbc操作hive，并返回结果
		People result = proxy.execute(new HiveClientCallback<People>() {
			public People doInHive(HiveClient hiveClient) throws Exception {
				Connection conn = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					conn = hiveClient.getConnection();
					ps = conn.prepareStatement("select * from tb1 where name=?");
					ps.setString(1, "tom");
					rs = ps.executeQuery();
					
					if(rs.next()){
						People result = new People();
						int id = rs.getInt("id");
						String name = rs.getString("name");
						result.setId(id);
						result.setName(name);
						return result;
					}else{
						return null;
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}finally{
					if(rs!=null)conn.close();
					if(ps!=null)conn.close();
					if(conn!=null)conn.close();
				}

			}	
		});
		System.out.println(result);
	}
	

}
