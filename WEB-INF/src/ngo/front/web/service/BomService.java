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
    	return (String)localCache.getObject(CACHE_KEY);	
	}

	@Override
	public Object loadCacheObject(String key)  {
		if (key.equals(CACHE_KEY))
		{
			Resource resource = new Resource();
			resource.setLinks(bomDAO.getAllBom());			
			String json = jsonService.toJson(resource);			
			logger.info("Localcache: key ["+key+"] loaded from database");			
			return (Object)json;
		}
		return null;
	}
}
