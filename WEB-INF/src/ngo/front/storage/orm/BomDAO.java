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
		List<Bom> list =  sqlSession.selectList("ngo.front.storage.orm.BomMapper.getAllBom");
		return list;
	}
}
