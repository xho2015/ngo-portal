package ngo.front.deploy.service;

import org.springframework.stereotype.Service;

import ngo.front.common.service.JSonService;
import ngo.front.common.service.LocalCache;
import ngo.front.storage.entity.Bom;
import ngo.front.storage.entity.Resource;
import ngo.front.storage.orm.BomDAO;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class DeployService {
	
	private final Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private BomDAO bomDAO;
	
	
	/**
	 * retrive all Bom data from DAO
	 * @return list of Bom object
	 */
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
}
