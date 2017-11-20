package ngo.front.web.service;

import org.springframework.stereotype.Service;

import ngo.front.common.service.JSonService;
import ngo.front.common.service.LocalCache;
import ngo.front.storage.entity.Bom;
import ngo.front.storage.entity.Module;
import ngo.front.storage.entity.Resource;
import ngo.front.storage.orm.BomDAO;
import ngo.front.storage.orm.ModuleDAO;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.annotate.JsonView;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class ModuleService implements LocalCache.CachingLoader{
	
	private final Logger logger = Logger.getLogger(this.getClass());
	
	private static final String CACHE_KEY = "module";
	private static final String SUBKEY_GRADE = "grade";
	
	@Autowired
	private LocalCache localCache;
	
	@Autowired
	private ModuleDAO moduleDAO;
	
	@Autowired
	private JSonService jsonService;
	
	@PostConstruct
	public void init()
	{
		localCache.register(CACHE_KEY, this);
		logger.info("ModuleService registered as caching loader for ["+CACHE_KEY+"]");				
	}
	
	public String getByGrade(String gradeId)
	{
    	return (String)localCache.getObject(gradeId);	
	}	

	@Override
	public Object loadCacheObject(String key)  {
		if (key.startsWith(CACHE_KEY+"."))
		{
			String [] keys = key.split("\\.");
			if (keys[1].equals(SUBKEY_GRADE)){
				List<Module> modules = keys[2].equals("all") ? moduleDAO.getAll() : moduleDAO.getByGrade(keys[2]);						
				Resource resource = new Resource(modules);
				localCache.entryVerUp(key, resource.getVersion());
				String json = jsonService.toJson(resource);			
				logger.info("Grade key ["+key+"] loaded from database");			
				return (Object)json;	
			} 
		}
		return null;
	}

	public int updateModule(Module module) {
		if (module == null)
			throw new IllegalStateException("module can not be null for updating");
		return moduleDAO.update(module);
	}
	
	public int addModule(Module module) {
		if (module == null)
			throw new IllegalStateException("module can not be null for adding");
		return moduleDAO.insert(module);
	}

	public String getAll() {
		return (String)localCache.getObject(CACHE_KEY+"."+ SUBKEY_GRADE + ".all");
	}
}
