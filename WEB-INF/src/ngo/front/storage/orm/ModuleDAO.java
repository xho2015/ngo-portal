package ngo.front.storage.orm;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ngo.front.storage.entity.Bom;
import ngo.front.storage.entity.Module;

@Repository
public class ModuleDAO {

	@Autowired
	private SqlSessionTemplate sqlSession;

	public List<Module> getAll() {
		List<Module> list =  sqlSession.selectList("ngo.front.storage.orm.ModuleMapper.getAll");
		return list;
	}
	
	public List<Module> getByGrade(String gradeId) {
		List<Module> list =  sqlSession.selectList("ngo.front.storage.orm.ModuleMapper.getByGrade", gradeId);
		return list;
	}

	public int update(Module module) {
		return sqlSession.update("ngo.front.storage.orm.ModuleMapper.update", module);		
	}
	
	public int insert(Module module) {
		return sqlSession.insert("ngo.front.storage.orm.ModuleMapper.insert", module);		
	}
}
