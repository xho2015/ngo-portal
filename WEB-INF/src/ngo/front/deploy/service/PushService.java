package ngo.front.deploy.service;

import org.springframework.stereotype.Service;

import ngo.front.common.service.JSonService;
import ngo.front.common.service.LocalCache;
import ngo.front.storage.entity.Bom;
import ngo.front.storage.entity.Module;
import ngo.front.storage.entity.Resource;
import ngo.front.storage.orm.BomDAO;
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
	
	public List<Bom> getAllBomObjects()
	{
		return bomDAO.getAllBom();
	}	
	
	public int updateBomObject(Bom bom) {
		return bomDAO.updateBom(bom);
	}
	
	public int addBomObject(Bom bom) {
		return bomDAO.insertBom(bom);
	}

	public List<Module> getAllModuleObjects() {
		return moduleDAO.getAll();
	}

	public void updateModuleObject(Module module) {
		moduleDAO.update(module);	
	}

	public void addModuleObject(Module module) {
		moduleDAO.insert(module);
	}
}
