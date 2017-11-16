package ngo.front.deploy.json;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ngo.front.web.service.BomService;
 
/*
	Ngo km remote query interface	 
 **/
@Controller
public class QueryControler {
 
	@Autowired
    private BomService bomService;
	
	private final Logger logger = Logger.getLogger(this.getClass());
    
    @RequestMapping(value = "/ants/query", method = RequestMethod.GET)
    public ResponseEntity<String> deploy(@RequestParam("key") String key, @RequestParam("token") String token) {
    	
    	try {
    		//validate token here
        	if (key.equals("bomver")) {     		
            	return ResponseEntity.ok(bomService.getBomVersions());
        	}
        	return ResponseEntity.ok("N/A");
        } catch (Exception e) {
        	logger.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Query error: "+e.getMessage());
        } finally {
        	
        }
    }
}

