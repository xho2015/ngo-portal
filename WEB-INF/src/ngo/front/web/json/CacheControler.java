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
 
/*
		 
 **/
@Controller
public class CacheControler {
 
	@Autowired
    private LocalCache localCache;
	
	private final Logger logger = Logger.getLogger(this.getClass());
    
    @RequestMapping(value = "/json/cache", method = RequestMethod.GET)
    public ResponseEntity<String> cache(@RequestParam("token") String token, @RequestParam("action") String action, @RequestParam("key") String key) {
        try {
        	//TODO: validate token here
        	if (action.equalsIgnoreCase("refresh"))
        	{	
        		localCache.refreshObject(key);
        		return ResponseEntity.ok("refresh cache key ["+key+"] done.");
        	}
        	else if (action.equalsIgnoreCase("remove"))
        	{	localCache.removeObject(key);
        		return ResponseEntity.ok("remove cache key ["+key+"] done.");
        	}
        	else if (action.equalsIgnoreCase("removeAll"))
        	{	localCache.removeAllObject();
        		return ResponseEntity.ok("removed All cache key done.");
        	}
        	else if (action.equalsIgnoreCase("state"))
        	{	String state = localCache.cacheState();
        		return ResponseEntity.ok(state);
        	}
        	return ResponseEntity.ok("N/A");
        } catch (Exception e) {
        	logger.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    } 
}

