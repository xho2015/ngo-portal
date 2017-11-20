package ngo.front.web.json;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ngo.front.common.service.LocalCache;
import ngo.front.web.service.BomService;
 
/*
		 
 **/
@Controller
public class ResourceControler {
 
	@Autowired
    private LocalCache localCache;
	
	private final Logger logger = Logger.getLogger(this.getClass());
    
    @RequestMapping(value = "/json/resource", method = RequestMethod.GET)
    public ResponseEntity<String> resource(@RequestParam("key") String moduleId, @RequestParam("token") String token) {
        try {
        	//validate token here
        	String json = localCache.dumpCacheEntries();
        	return ResponseEntity.ok(json);
        } catch (Exception e) {
        	logger.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

