package ngo.front.storage.orm;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ngo.front.storage.entity.Bom;

@Repository
public class BomDAO {

	@Autowired
	private SqlSessionTemplate sqlSession;

	public List<Bom> getAll() {
		List<Bom> list =  sqlSession.selectList("ngo.front.storage.orm.BomMapper.getAll");
		return list;
	}
	
	public List<Bom> getByModule(String moduleId) {
		List<Bom> list =  sqlSession.selectList("ngo.front.storage.orm.BomMapper.getByModule", moduleId);
		return list;
	}
	
	public List<Bom> getByGrade(String gradeId) {
		List<Bom> list =  sqlSession.selectList("ngo.front.storage.orm.BomMapper.getByGrade", gradeId);
		return list;
	}

	public int update(Bom bom) {
		return sqlSession.update("ngo.front.storage.orm.BomMapper.update", bom);		
	}
	
	public int insert(Bom bom) {
		return sqlSession.insert("ngo.front.storage.orm.BomMapper.insert", bom);		
	}

	public List<Bom> getVersions() {
		return sqlSession.selectList("ngo.front.storage.orm.BomMapper.getVersions");
	}
}
