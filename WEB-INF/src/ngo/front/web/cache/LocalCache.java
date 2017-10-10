package ngo.front.web.cache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/*
 *https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
 **/
public class LocalCache {

	private CacheLoader<String, Object> loader;

	private LoadingCache<String, Object> cache;

	private LocalCache() {
		loader = new CacheLoader<String, Object>() {
			@Override
			public Object load(String key) {
				return loadDBObject(key);
			}
		};

		//TODO: configure the refresh interval
		cache = CacheBuilder.newBuilder().refreshAfterWrite(10, TimeUnit.MINUTES).build(loader);
	}

	private Object loadDBObject(String key) {
		return null;
	}

	private static class LazyHolder {
		static final LocalCache INSTANCE = new LocalCache();
	}

	public static LocalCache getInstance() {
		return LazyHolder.INSTANCE;
	}

	public Object getObject(String key) {
		try {
			return cache.get(key);
		} catch (ExecutionException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void refreshObject(String key) {
		cache.refresh(key);
	}
}
