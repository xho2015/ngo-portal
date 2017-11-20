package ngo.front.web.service;

import org.springframework.stereotype.Service;

import ngo.front.common.service.JSonService;
import ngo.front.common.service.LocalCache;
import ngo.front.storage.entity.Bom;
import ngo.front.storage.entity.Grade;
import ngo.front.storage.entity.Module;
import ngo.front.storage.entity.Resource;
import ngo.front.storage.orm.BomDAO;
import ngo.front.storage.orm.GradeDAO;
import ngo.front.storage.orm.ModuleDAO;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.annotate.JsonView;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class GradeService implements LocalCache.CachingLoader{
	
	private final Logger logger = Logger.getLogger(this.getClass());
	
	private static final String CACHE_KEY = "grade";
	
	@Autowired
	private LocalCache localCache;
	
	@Autowired
	private GradeDAO gradeDAO;
	
	@Autowired
	private JSonService jsonService;
	
	@PostConstruct
	public void init()
	{
		localCache.register(CACHE_KEY, this);
		logger.info("GradeService registered as caching loader for ["+CACHE_KEY+"]");				
	}
	
	public String getById(String gradeId)
	{
    	return (String)localCache.getObject(gradeId);	
	}	

	@Override
	public Object loadCacheObject(String key)  {
		if (key.startsWith(CACHE_KEY+".")) {
			String [] keys = key.split("\\.");			
			List<Grade> grades = keys[1].equals("all") ? gradeDAO.getAll() : gradeDAO.getById(keys[1]);					
			Resource resource = new Resource(grades);
			localCache.entryVerUp(key, resource.getVersion());
			String json = jsonService.toJson(resource);			
			logger.info("Grade key ["+key+"] loaded from database");			
			return (Object)json;	 
		}
		return null;
	}

	public int updateGrade(Grade grade) {
		if (grade == null)
			throw new IllegalStateException("grade can not be null for updating");
		return gradeDAO.update(grade);
	}
	
	public int addGrade(Grade grade) {
		if (grade == null)
			throw new IllegalStateException("grade can not be null for adding");
		return gradeDAO.insert(grade);
	}

	public String getAll() {
		return (String)localCache.getObject(CACHE_KEY+".all");
	}
}
