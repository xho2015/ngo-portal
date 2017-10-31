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

	public List<Bom> getAllBom() {
		List<Bom> list =  sqlSession.selectList("ngo.front.storage.orm.BomMapper.getAll");
		return list;
	}
	
	public List<Bom> getBomByModule(String moduleId) {
		List<Bom> list =  sqlSession.selectList("ngo.front.storage.orm.BomMapper.getByModuleId", moduleId);
		return list;
	}
	
	public List<Bom> getBomByGrade(String gradeId) {
		List<Bom> list =  sqlSession.selectList("ngo.front.storage.orm.BomMapper.getByGradeId", gradeId);
		return list;
	}

	public int updateBom(Bom bom) {
		return sqlSession.update("ngo.front.storage.orm.BomMapper.update", bom);		
	}
	
	public int insertBom(Bom bom) {
		return sqlSession.insert("ngo.front.storage.orm.BomMapper.insert", bom);		
	}
}
