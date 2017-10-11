package ngo.front.storage.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import ngo.front.common.service.JSonService;
import ngo.front.storage.entity.Resource;
import ngo.front.storage.orm.BomDAO;

@Service
@Scope("singleton")
public class LocalCache {

	private final Logger logger = Logger.getLogger(this.getClass());
	
	private CacheLoader<String, Object> loader = new CacheLoader<String, Object>() {
		@Override
		public Object load(String key) {
			return loadDBObject(key);
		}
	};

	//TODO: change the refresh interval
	private LoadingCache<String, Object> cache = CacheBuilder.newBuilder().refreshAfterWrite(1, TimeUnit.MINUTES)
			.build(loader);

	@Autowired
	private BomDAO bomDAO;
	
	@Autowired
	private JSonService jsonService;

	private Object loadDBObject(String key) {
		
		if (key.equals("bom"))
		{
			Resource resource = new Resource();
			resource.setLinks(bomDAO.getAllBom());			
			String json = jsonService.toJson(resource);			
			logger.info("Localcache: key ["+key+"] loaded from database");			
			return json;
		}
		
		return null;
	}

	public Object getObject(String key) {
		try {
			return cache.get(key);
		} catch (ExecutionException e) {
			e.printStackTrace();
			logger.info("Localcache: getObject error :" + e.getMessage());	
			return null;
		}
	}

	public void refreshObject(String key) {
		cache.refresh(key);
		logger.info("Localcache: key [" +key+"] refreshed");		
	}
}
