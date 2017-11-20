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
	
	//TODO: change the refresh interval to 10 MINs when go PROD
	private static final int REFRESH_INTERVAL = 1;
	
	private final static Map<String,CacheEntry> ENTRY_MAP = new HashMap<String,CacheEntry>();
	private final static Map<String,ReloadPolicy> RELOAD_MAP = new HashMap<String,ReloadPolicy>();
	private final static Map<String, CachingLoader> REGISTRA_MAP = new HashMap<String, CachingLoader>();
		
	@PostConstruct
	public void init() {
		logger.info("CacheBuilder refresh interval is :  ["+REFRESH_INTERVAL+"] minutes.");				
	}

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
	
	private RemovalListener<String, Object> removalListener = new RemovalListener<String, Object>() {
		  public void onRemoval(RemovalNotification<String, Object> removal) {
			  int size = removal.getValue().toString().length();
			  logger.debug("key ["+removal.getKey()+"] is removed, "+size+" released.");	
		  }
		};
	
	private LoadingCache<String, Object> cache = CacheBuilder.newBuilder().recordStats().refreshAfterWrite(REFRESH_INTERVAL, TimeUnit.MINUTES).removalListener(removalListener).build(loader);

	private Object loadObject(String key) {
		//retrieve the root key
		String temp[] = key.split("\\.");
		String root = temp[0];
		CachingLoader loader = REGISTRA_MAP.get(root);
		if (loader==null)
			logger.error("key ["+key+"] don't have a caching loader");			
		else{
			Object newValue =  loader.loadCacheObject(key);
			//reset size map
			entryResize(key, newValue.toString().length());
			//reset reload status
			updateEntryPolicy(root);
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
	
	public void register(String key, CachingLoader loader) {
		this.REGISTRA_MAP.put(key, loader);
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

	private void updateEntryPolicy(String root)	{
		ReloadPolicy policy = RELOAD_MAP.get(root);
		if (policy != null)
			policy.load(System.currentTimeMillis()).update();
	}
	
	private void entryResize(String key, int newSize) {
		CacheEntry entry = ENTRY_MAP.get(key);
		if (entry == null) {
			entry = new CacheEntry(newSize, "");
			ENTRY_MAP.put(key, entry);
		} else
			entry.size = newSize;
	}	
	
	public void entryVerUp(String key, String version) {
		CacheEntry entry = ENTRY_MAP.get(key);
		if (entry == null) {
			entry = new CacheEntry(0, version);
			ENTRY_MAP.put(key, entry);
		} else
			entry.digest = version;
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
	
	public String getCacheState() {
		logger.info("cache states");		
		return cache.stats().toString();
	}
	
	public String getCacheSize() {
		logger.info("cache size");		
		int size = 0;
		for (Map.Entry<String, CacheEntry> entry : ENTRY_MAP.entrySet()){
		    size+=entry.getValue().size;
		}
		return String.valueOf(size);
	}
	
	public String dumpCacheEntries() {
		StringBuffer dump = new StringBuffer("[");
		for (Map.Entry<String, CacheEntry> entry : ENTRY_MAP.entrySet()){
			dump.append("{\"name\":\"").append(entry.getKey()).append("\",\"ver\":\"").append(entry.getValue().digest).append("\"},");
		}
		return dump.deleteCharAt(dump.length()-1).append("]").toString();
	}

	public void refreshObject(String key) {
		cache.refresh(key);
		logger.info("key [" +key+"] refreshed");		
	}
	
	public void invalidateObject(String key) {
		cache.invalidate(key);
		ENTRY_MAP.remove(key);
		logger.info("key [" +key+"] invalidated");		
	}
	
	public void invalidateAllObject() {
		cache.invalidateAll();
		ENTRY_MAP.clear();
		logger.info("all keys invalidated");		
	}
	
	private class CacheEntry {
		int size;
		String digest;
		CacheEntry(int size, String digest) {
			this.size = size;
			this.digest = digest;
		}
		@Override
		public String toString() {
			return size+","+digest;
		}
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
