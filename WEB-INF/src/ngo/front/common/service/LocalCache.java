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
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
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
			logger.debug("load for key ["+key+"]");	
			return loadObject(key);
		}
		
		@Override
		public ListenableFuture<Object> reload(String key, Object oldValue){
			logger.debug("reload for key ["+key+"]");	
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
		logger.info("CacheBuilder refresh interval is :  ["+REFRESH_INTERVAL+"] minutes.");				
	}

	
	private RemovalListener<String, Object> removalListener = new RemovalListener<String, Object>() {
		  public void onRemoval(RemovalNotification<String, Object> removal) {
			  int size = removal.getValue().toString().length();
			  logger.debug("key ["+removal.getKey()+"] is removed, "+size+" released.");	
		  }
		};
	
	private LoadingCache<String, Object> cache = CacheBuilder.newBuilder().recordStats().refreshAfterWrite(REFRESH_INTERVAL, TimeUnit.MINUTES)
				.removalListener(removalListener).build(loader);

	public void register(String key, CachingLoader loader) {
		this.registra.put(key, loader);
	}
	
	public String setReloadPolicy(String setting){
		logger.info("set reload policy: "+setting);
		String temp[] = setting.split("\\!");
		for (String p : temp) {
			String kv[] = p.split(":");
			ReloadPolicy policy = RELOAD_MAP.get(kv[0]);
			if (policy == null){
				policy = new ReloadPolicy();
				RELOAD_MAP.put(kv[0], policy);
			}
			long newInterval = Long.parseLong(kv[1]) * 1000;
			if ((newInterval % (REFRESH_INTERVAL * 60 * 100)) > 0 || newInterval <= (REFRESH_INTERVAL * 60 * 100))
				return "interval ["+kv[0]+"] not valid";
			policy.update(Long.parseLong(kv[1]) * 1000); //interval in setting is in second unit
		}
		return "Done";
	}
	
	public String getReloadPolicy(){
		logger.info("get reload policy");		
		StringBuffer policy = new StringBuffer();
		for (Map.Entry<String,ReloadPolicy> entry : RELOAD_MAP.entrySet()){
			policy.append(entry.getKey()).append("=").append(entry.getValue().toString()).append(";");
		}
		return policy.toString();
	}

	private Object loadObject(String key) {
		//retrieve the root key
		String temp[] = key.split("\\.");
		String root = temp[0];
		CachingLoader loader = registra.get(root);
		if (loader==null)
			logger.error("key ["+key+"] don't have a caching loader");			
		else{
			logger.info("query from database for key ["+key+"]");	
			Object newValue =  loader.loadCacheObject(key);
			//reset size map
			SIZE_MAP.put(key, newValue.toString().length());
			//reset reload status
			ReloadPolicy policy = RELOAD_MAP.get(root);
			if (policy != null)
				policy.load(System.currentTimeMillis()).update();
			//return back to new value to cache store
			return newValue;
		}
		return null;
	}
	
	private ListenableFuture<Object> reloadObject(String key, Object oldValue) {
		//check if immediate value is required based on reload policy
		String temp[] = key.split("\\.");
		boolean isReload = true;
		ReloadPolicy policy = RELOAD_MAP.get(temp[0]);
		if (policy != null)	
			isReload = policy.isExpired();
		if (!isReload) {
			return Futures.immediateFuture(oldValue);
	    } else {
	        Object newValue =  loadObject(key);
	        return Futures.immediateFuture(newValue);
	    }
	}
	
	public Object getObject(String key) {
		try {
			return cache.get(key);
		} catch (ExecutionException e) {
			e.printStackTrace();
			logger.info("getObject error :" + e.getMessage());	
			return null;
		}
	}
	
	public String cacheState() {
		logger.info("cache states");		
		return cache.stats().toString();
	}
	
	public String getCacheSize() {
		logger.info("cache size");		
		int size = 0;
		for (Map.Entry<String,Integer> entry : SIZE_MAP.entrySet()){
		    size+=entry.getValue();
		}
		return String.valueOf(size);
	}
	
	public String dmpCacheEntries() {
		logger.info("dump entries");		
		StringBuffer dump = new StringBuffer();
		for (Map.Entry<String,Integer> entry : SIZE_MAP.entrySet()){
			dump.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
		}
		return dump.toString();
	}

	public void refreshObject(String key) {
		cache.refresh(key);
		logger.info("key [" +key+"] refreshed");		
	}
	
	public void invalidateObject(String key) {
		cache.invalidate(key);
		SIZE_MAP.remove(key);
		logger.info("key [" +key+"] invalidated");		
	}
	
	public void invalidateAllObject() {
		cache.invalidateAll();
		SIZE_MAP.clear();
		logger.info("all keys invalidated");		
	}
	
	private class ReloadPolicy {
		long load = -1;
		long interval = -1; 
		long nextExecTime = -1;
		
		ReloadPolicy(){}
		
		@Override
		public String toString(){
			return interval+","+new java.util.Date(nextExecTime).toString();
		}
		
		public ReloadPolicy load(long timestamp) {
			this.load = timestamp;
			return this;
		}
		
		public ReloadPolicy update(long newInterval) {
			interval = newInterval;
			return this;
		}
		
		public ReloadPolicy update() {
			if (interval > 0 && load > 0) {
				nextExecTime = load + interval;
			}
			return this;
		}
		
		public boolean isExpired() {
			long current = System.currentTimeMillis();
			if (current > nextExecTime)
				return true;
			return false;
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
