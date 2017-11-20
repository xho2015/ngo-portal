package ngo.front.deploy.service;

import org.springframework.stereotype.Service;

import ngo.front.common.service.JSonService;
import ngo.front.common.service.LocalCache;
import ngo.front.storage.entity.Bom;
import ngo.front.storage.entity.Grade;
import ngo.front.storage.entity.Module;
import ngo.front.storage.entity.Resource;
import ngo.front.storage.orm.BomDAO;
import ngo.front.storage.orm.GradeDAO;
import ngo.front.storage.orm.ModuleDAO;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class PushService {
	
	private final Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private BomDAO bomDAO;
	
	@Autowired
	private ModuleDAO moduleDAO;
	
	@Autowired
	private GradeDAO gradeDAO;
	
	public List<Bom> getAllBomObjects()
	{
		return bomDAO.getAll();
	}	
	
	public int updateBomObject(Bom bom) {
		return bomDAO.update(bom);
	}
	
	public int addBomObject(Bom bom) {
		return bomDAO.insert(bom);
	}

	public List<Module> getAllModuleObjects() {
		return moduleDAO.getAll();
	}

	public int updateModuleObject(Module module) {
		return moduleDAO.update(module);	
	}

	public int addModuleObject(Module module) {
		return moduleDAO.insert(module);
	}

	public List<Grade> getAllGradeObjects() {
		return gradeDAO.getAll();
	}

	public int updateGradeObject(Grade grade) {
		return gradeDAO.update(grade);
	}

	public int addModuleObject(Grade grade) {
		return gradeDAO.insert(grade);
	}
}
