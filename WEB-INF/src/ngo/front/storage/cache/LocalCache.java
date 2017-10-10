package ngo.front.storage.cache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import ngo.front.storage.orm.BomDAO;

@Service
@Scope("singleton")
public class LocalCache {

	private CacheLoader<String, Object> loader = new CacheLoader<String, Object>() {
		@Override
		public Object load(String key) {
			return loadDBObject(key);
		}
	};

	private LoadingCache<String, Object> cache = CacheBuilder.newBuilder().refreshAfterWrite(10, TimeUnit.MINUTES)
			.build(loader);

	@Autowired
	private BomDAO bomDAO;

	private Object loadDBObject(String key) {
		return bomDAO.getAllBom();
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
