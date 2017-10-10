package ngo.front.storage.orm;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class BomDAO {

	@Autowired
	private SqlSessionTemplate sqlSession;

	public List<Object> getAllBom() {
		return sqlSession.selectList("ngo.front.storage.orm.BomMapper.getAllBom");
	}
}
