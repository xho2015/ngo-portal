package ngo.front.web.json;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


import ngo.front.common.service.LocalCache;
import ngo.front.web.service.BomService;
import ngo.front.web.service.GradeService;
import ngo.front.web.service.ModuleService;
 
/**
	NGO Kids Math Resource request controller	 
 */
@Controller
public class ResourceControler {
 
	@Autowired
    private LocalCache localCache;
	
	@Autowired
    private BomService bomService;
	
	@Autowired
    private ModuleService moduleService;
	
	@Autowired
    private GradeService gradeService;
	
	private final Logger logger = Logger.getLogger(this.getClass());
    
    @RequestMapping(value = "/json/resource", method = RequestMethod.GET)
    public ResponseEntity<String> request(@RequestParam("key") String key, @RequestParam("token") String token) {
        try {
        	//validate token here
        	
        	if (key.equals("dump")) {
        		return ResponseEntity.ok(localCache.dumpCacheEntries());
        	} else if (key.startsWith("bom.")) {
        		return bom(key, token);
        	} else if (key.startsWith("module.")) {
        		return module(key, token);
        	} else if (key.startsWith("grade.")) {
        		return grade(key, token);
        	}
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("invalid request");
        } catch (Exception e) {
        	logger.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    public ResponseEntity<String> bom(String moduleId, String token) {
        try {
        	return ResponseEntity.ok(bomService.getModuleBom(moduleId, token));
        } catch (Exception e) {
        	logger.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    public ResponseEntity<String> module(String gradeId, String token) {
        try {
        	if (gradeId.endsWith(".all"))
        		return ResponseEntity.ok(moduleService.getAll());
        	else
        		return ResponseEntity.ok(moduleService.getByGrade(gradeId));
        } catch (Exception e) {
        	logger.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    public ResponseEntity<String> grade(String gid, String token) {
        try {
        	if (gid.endsWith(".all"))	
        		return ResponseEntity.ok(gradeService.getAll());
        	else
        		return ResponseEntity.ok(gradeService.getById(gid));      	
        } catch (Exception e) {
        	logger.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

