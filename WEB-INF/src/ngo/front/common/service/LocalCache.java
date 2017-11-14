package ngo.front.common.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;

/**
 * https://github.com/google/guava/wiki/CachesExplained
 * 
 * @author Administrator
 *
 */
@Service
@Scope("singleton")
public class LocalCache {

	private final Logger logger = Logger.getLogger(this.getClass());
	
	private final Map<String, CachingLoader> registra = new HashMap<String, CachingLoader>();
	
	private CacheLoader<String, Object> loader = new CacheLoader<String, Object>() {
		@Override
		public Object load(String key) {
			logger.debug("LocalCache builder load for key ["+key+"]");	
			return loadObject(key);
		}
		
		@Override
		public ListenableFuture<Object> reload(String key, Object oldValue){
			logger.debug("LocalCache builder reload for key ["+key+"]");	
			return reloadObject(key, oldValue);
		}
	};
	
	//TODO: change the refresh interval to 10 MINs when go PROD
	private static final int REFRESH_INTERVAL = 1;
	
	private static Map<String,Integer> SIZE_MAP = new HashMap<String,Integer>();
	private static Map<String,ReloadPolicy> RELOAD_MAP = new HashMap<String,ReloadPolicy>();
	
	
	@PostConstruct
	public void init()
	{
		logger.info("LocalCache builder refresh interval is :  ["+REFRESH_INTERVAL+"] minutes.");				
	}

	private LoadingCache<String, Object> cache = CacheBuilder.newBuilder().recordStats().refreshAfterWrite(REFRESH_INTERVAL, TimeUnit.MINUTES)
			.build(loader);

	
	public void register(String key, CachingLoader loader) {
		this.registra.put(key, loader);
	}
	
	public void setReloadPolicy(String setting){
		logger.info("Localcache: set reload policy: "+setting);
		String temp[] = setting.split("\\!");
		for (String p : temp) {
			String kv[] = p.split(":");
			ReloadPolicy policy = RELOAD_MAP.get(kv[0]);
			if (policy == null)
				RELOAD_MAP.put(kv[0], new ReloadPolicy(Long.parseLong(kv[1]) * 1000, 0));
			else {
				policy.interval = Long.parseLong(kv[1]) * 1000; //interval unit is in second
				policy.nextExecTime += policy.interval;
			}
		}
	}
	
	public String getReloadPolicy(){
		logger.info("Localcache: get reload policy");		
		StringBuffer policy = new StringBuffer();
		for (Map.Entry<String,ReloadPolicy> entry : RELOAD_MAP.entrySet()){
			policy.append(entry.getKey()).append("=").append(entry.getValue().toString()).append(";");
		}
		return policy.toString();
	}

	private Object loadObject(String key) {
		//retrieve the root key
		String temp[] = key.split("\\.");
		String root = temp.length > 0 ? temp[0] : "";
		CachingLoader loader = registra.get(root);
		if (loader==null)
			logger.error("Localcache: key ["+key+"] don't have a caching loader");			
		else{
			logger.debug("Localcache: key ["+key+"] to be loaded from database");	
			Object newValue =  loader.loadCacheObject(key);
			//reset size map
			SIZE_MAP.put(key, newValue.toString().length());
			//reset reload policy
			ReloadPolicy policy = RELOAD_MAP.get(root);
			if (policy == null)
				RELOAD_MAP.put(root, new ReloadPolicy(REFRESH_INTERVAL * 60 * 1000, System.currentTimeMillis()));
			else
				policy.nextExecTime += policy.nextExecTime== 0 ? System.currentTimeMillis()+policy.interval : policy.interval;			
			//return back to new value to cache store
			return newValue;
		}
		return null;
	}
	
	private ListenableFuture<Object> reloadObject(String key, Object oldValue) {
		//check if immediate value is required based on reload policy
		String temp[] = key.split("\\.");
		long next = RELOAD_MAP.get(temp[0]).nextExecTime;
		boolean isImmediate = System.currentTimeMillis() < next;
		
		if (isImmediate) {
            return Futures.immediateFuture(oldValue);
	    } else {
	        // asynchronous!
	        ListenableFutureTask<Object> task = ListenableFutureTask.create(new Callable<Object>() {
	          public Object call() {
	            return loadObject(key);
	          }
	        });
	        Executors.newSingleThreadExecutor().execute(task);
	        return task;
	    }
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
	
	public String dmpCacheEntries() {
		logger.info("Localcache: dump entries");		
		StringBuffer dump = new StringBuffer();
		for (Map.Entry<String,Integer> entry : SIZE_MAP.entrySet()){
			dump.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
		}
		return dump.toString();
	}

	public void refreshObject(String key) {
		cache.refresh(key);
		logger.info("Localcache: key [" +key+"] refreshed");		
	}
	
	public void removeObject(String key) {
		cache.invalidate(key);
		SIZE_MAP.remove(key);
		logger.info("Localcache: key [" +key+"] removed");		
	}
	
	public void removeAllObject() {
		cache.invalidateAll();
		SIZE_MAP.clear();
		logger.info("Localcache: all keys removed");		
	}
	
	private class ReloadPolicy {
		long interval = REFRESH_INTERVAL * 60 * 1000; 
		long nextExecTime = 0;
		ReloadPolicy(long interval, long next){
			this.interval = interval;
			this.nextExecTime = next;
		}
		@Override
		public String toString(){
			return interval+","+new java.util.Date(nextExecTime).toString();
		}
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
