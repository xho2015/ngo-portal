package ngo.front.common.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import ngo.front.storage.entity.Resource;
import ngo.front.storage.orm.BomDAO;

@Service
@Scope("singleton")
public class LocalCache {

	private final Logger logger = Logger.getLogger(this.getClass());
	
	private final Map<String, CachingLoader> registra = new HashMap<String, CachingLoader>();
	
	private CacheLoader<String, Object> loader = new CacheLoader<String, Object>() {
		@Override
		public Object load(String key) {
			return loadObject(key);
		}
	};

	//TODO: change the refresh interval
	private LoadingCache<String, Object> cache = CacheBuilder.newBuilder().recordStats().refreshAfterWrite(1, TimeUnit.MINUTES)
			.build(loader);

	
	public void register(String key, CachingLoader loader)
	{
		this.registra.put(key, loader);
	}

	private Object loadObject(String key) {
		
		CachingLoader loader = registra.get(key);
		if (loader==null)
			logger.error("Localcache: key ["+key+"] don't have a caching loader");			
		else
			return loader.loadCacheObject(key);
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
	
	public String cacheState() {
		logger.info("Localcache: cache states");		
		return cache.stats().toString();
	}

	public void refreshObject(String key) {
		cache.refresh(key);
		logger.info("Localcache: key [" +key+"] refreshed");		
	}
	
	public void removeObject(String key) {
		cache.invalidate(key);
		logger.info("Localcache: key [" +key+"] removed");		
	}
	
	public void removeAllObject() {
		cache.invalidateAll();
		logger.info("Localcache: all keys removed");		
	}
	
	/**
	 * the class implements this interface hold the implementation of loading the specified caching entry (by key)
	 * from the storage somewhere, e.g. database. 
	 * @author Administrator
	 */
	public interface CachingLoader {
		public Object loadCacheObject(String key);
	}
}
