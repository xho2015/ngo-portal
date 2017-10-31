package ngo.front.web.service;

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
public class BomService implements LocalCache.CachingLoader{
	
	private final Logger logger = Logger.getLogger(this.getClass());
	
	private static final String CACHE_KEY = "bom";
	private static final String SUB_KEY_MODULE = "module";
	private static final String SUB_KEY_GRADE = "grade";
	
	@Autowired
	private LocalCache localCache;
	
	@Autowired
	private BomDAO bomDAO;
	
	@Autowired
	private JSonService jsonService;
	
	@PostConstruct
	public void init()
	{
		localCache.register(CACHE_KEY, this);
		logger.info("BomService registered as caching loader for ["+CACHE_KEY+"]");				
	}
	
	/**
	 * retrive all Bom data from DAO
	 * @return list of Bom object
	 */
	public List<Bom> getAllBomObjects()
	{
		return bomDAO.getAllBom();
	}
	
	public String getModuleBom(String moduleId, String token)
	{
    	return (String)localCache.getObject(CACHE_KEY+"."+ SUB_KEY_MODULE + "."+ moduleId);	
	}
	
	public String getGradeBom(String gradeId, String token)
	{
    	return (String)localCache.getObject(CACHE_KEY+"."+ SUB_KEY_GRADE + "."+ gradeId);	
	}

	@Override
	public Object loadCacheObject(String key)  {
		if (key.startsWith(CACHE_KEY))
		{
			String [] keys = key.split("\\.");
			if (keys[1].equals(SUB_KEY_MODULE))
			{
				Resource resource = new Resource();
				resource.setLinks(bomDAO.getBomByModule(keys[2]));			
				String json = jsonService.toJson(resource);			
				logger.info("Localcache: Module key ["+key+"] loaded from database");			
				return (Object)json;	
			} else if (keys[1].equals(SUB_KEY_GRADE)){
				Resource resource = new Resource();
				resource.setLinks(bomDAO.getBomByGrade(keys[2]));			
				String json = jsonService.toJson(resource);			
				logger.info("Localcache: Grade key ["+key+"] loaded from database");			
				return (Object)json;
			}
			
		}
		return null;
	}

	public int updateBomObject(Bom bom) {
		if (bom == null)
			throw new IllegalStateException("Bom can not be null for updating");
		if (bomDAO.updateBom(bom) != 1)
			throw new IllegalStateException("Bom not updated successfully");
		return 1;
	}
	
	public int addBomObject(Bom bom) {
		if (bom == null)
			throw new IllegalStateException("Bom can not be null for adding");
		if (bomDAO.insertBom(bom) != 1)
			throw new IllegalStateException("Bom not added successfully");
		return 1;
	}
}
