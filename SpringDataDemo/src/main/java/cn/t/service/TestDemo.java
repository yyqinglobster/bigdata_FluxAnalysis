package cn.t.service;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.t.domain.Student;

public class TestDemo {
	
	/*连接数据库*/
	@Test
	public void connect(){
		// 通过配置文件初始化spring容器
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		// 获取代理对象，通过此对象执行CURD操作
		StuRepository proxy = context.getBean(StuRepository.class);
	}
	
	/*将数据插入到表，方法名固定为save()
	 * save()方法：用于新增数据和修改数据。
	 * 	1.如果主键不存在，则新增
	 *  2.如果主键存在，则修改	*/
	@Test
	public void save(){
		// 通过配置文件初始化spring容器
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		// 获取代理对象，通过此对象执行CURD操作
		StuRepository proxy = context.getBean(StuRepository.class);
		Student s1 = new Student(1,"tom",23);
		Student s2 = new Student(2,"rose",18);
		Student s3 = new Student(3,"jim",30);
		Student s4 = new Student(4,"jim",30);
		
		proxy.save(s1);
		proxy.save(s2);
		proxy.save(s3);
		proxy.save(s4);
	}

}
