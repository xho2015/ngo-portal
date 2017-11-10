package ngo.front.common.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;


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
	
	private static final int REFRESH_INTERVAL = 10;
	
	private static Map<String,Integer> SIZE_MAP = new HashMap<String,Integer>();
	
	
	@PostConstruct
	public void init()
	{
		logger.info("LocalCache builder refresh interval is :  ["+REFRESH_INTERVAL+"] minutes.");				
	}

	//TODO: change the refresh interval to 10 MINs when go PROD
	private LoadingCache<String, Object> cache = CacheBuilder.newBuilder().recordStats().refreshAfterWrite(REFRESH_INTERVAL, TimeUnit.MINUTES)
			.build(loader);

	
	public void register(String key, CachingLoader loader)
	{
		this.registra.put(key, loader);
	}

	private Object loadObject(String key) {
		
		//retrieve the root key
		String temp[] = key.split("\\.");
		String root = temp.length > 0 ? temp[0] : "";
		CachingLoader loader = registra.get(root);
		if (loader==null)
			logger.error("Localcache: key ["+key+"] don't have a caching loader");			
		else{
			Object newObj =  loader.loadCacheObject(key);
			SIZE_MAP.put(key, newObj.toString().length());
			return newObj;
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
	
	public String cacheState() {
		logger.info("Localcache: cache states");		
		return cache.stats().toString();
	}
	
	public String getCacheSize() {
		logger.info("Localcache: cache size");		
		int size = 0;
		for (Map.Entry<String,Integer> entry : SIZE_MAP.entrySet()){
		    size+=entry.getValue();
		}
		return String.valueOf(size);
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
