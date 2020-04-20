package cn.t.service;

import org.springframework.data.repository.Repository;

import cn.t.domain.Student;

/**
 * 通过调用此接口中的JPA规范方法，实现对持久层的CRUD操作
 * @author yyq
 *
 */
public interface StuRepository extends Repository<Student, Integer> {
	void save(Student s1);
}
