package cn.t.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity //用于标识当前配是实体对象映射类
@Table(name="stu") // 指定对应的表名。如果不存在，则会自动创建
public class Student {
	// 类中的属性会对应表中的列名
	private Integer id;
	private String name;
	private Integer age;

	public Student() {}
	public Student(int id, String name, int age) {
		this.id=id;
		this.name=name;
		this.age=age;
	}
	// 快捷键shift+alt+s，生成getter和setter
	@Id // 标识此列是主键列
	@GeneratedValue // 使主键自动递增
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	
	@Override
	public String toString() {
		return "Student [id=" + id + ", name=" + name + ", age=" + age + "]";
	}
	
	
	
	
	

}
