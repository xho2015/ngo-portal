package ngo.front.deploy.json;


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
    
    @RequestMapping(value = "/ants/cache", method = RequestMethod.GET)
    public ResponseEntity<String> cache(@RequestParam("token") String token, @RequestParam("action") String action, @RequestParam("key") String key) {
        try {
        	//TODO: validate token here
        	
        	String timestamp = "["+new java.util.Date().toString()+"] ";
        	
        	if (action.equalsIgnoreCase("refresh"))	{	
        		localCache.refreshObject(key);
        		return ResponseEntity.ok(timestamp+"refresh cache key ["+key+"] done.");
        	}
        	else if (action.equalsIgnoreCase("remove"))
        	{	localCache.invalidateObject(key);
        		return ResponseEntity.ok(timestamp+"remove cache key ["+key+"] done.");
        	}
        	else if (action.equalsIgnoreCase("removeAll"))
        	{	localCache.invalidateAllObject();
        		return ResponseEntity.ok(timestamp+"removed All cache key done.");
        	}
        	else if (action.equalsIgnoreCase("state"))
        	{	String state = localCache.getCacheState();
        		return ResponseEntity.ok(timestamp+state);
        	}
        	else if (action.equalsIgnoreCase("size"))
        	{	String size = localCache.getCacheSize();
        		return ResponseEntity.ok(timestamp+size);
        	}
        	else if (action.equalsIgnoreCase("dump"))
        	{	String dump = localCache.dumpCacheEntries();
    			return ResponseEntity.ok(timestamp+dump);
        	}
        	else if (action.equalsIgnoreCase("setPolicy"))
        	{	String result = localCache.setReloadPolicy(key);
    			return ResponseEntity.ok(timestamp+" set reload policy, " + result);
        	}
        	else if (action.equalsIgnoreCase("getPolicy"))
        	{	String policy = localCache.getReloadPolicy();
    			return ResponseEntity.ok(timestamp+policy);
        	}
        	else if (action.equalsIgnoreCase("preload"))
        	{	String keys[] = key.split("!");
        		for(String k : keys)
        			localCache.getObject(k);
    			return ResponseEntity.ok(timestamp+" local cache preload done.");
        	}
        	return ResponseEntity.ok("N/A");
        } catch (Exception e) {
        	logger.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    } 
}

