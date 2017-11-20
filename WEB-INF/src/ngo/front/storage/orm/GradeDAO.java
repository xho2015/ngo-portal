package ngo.front.storage.orm;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import ngo.front.storage.entity.Grade;


@Repository
public class GradeDAO {

	@Autowired
	private SqlSessionTemplate sqlSession;

	public List<Grade> getAll() {
		List<Grade> list =  sqlSession.selectList("ngo.front.storage.orm.GradeMapper.getAll");
		return list;
	}

	public int update(Grade grade) {
		return sqlSession.update("ngo.front.storage.orm.GradeMapper.update", grade);		
	}
	
	public int insert(Grade grade) {
		return sqlSession.insert("ngo.front.storage.orm.GradeMapper.insert", grade);		
	}

	public List<Grade> getById(String gid) {
		List<Grade> list =  sqlSession.selectList("ngo.front.storage.orm.GradeMapper.getById", gid);
		return list;
	}
}
