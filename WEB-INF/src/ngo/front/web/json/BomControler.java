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

import ngo.front.web.service.BomService;
 
/*
		 
 **/
@Controller
public class BomControler {
 
	@Autowired
    private BomService bomService;
	
	private final Logger logger = Logger.getLogger(this.getClass());
    
    @RequestMapping(value = "/json/bom/list", method = RequestMethod.GET)
    public ResponseEntity<String> bom(@RequestParam("module") String moduleId, @RequestParam("token") String token) {
        try {
        	//it's not necessary do request param validity as spring framework will handle it
        	String json = bomService.getModuleBom(moduleId, token);
        	return ResponseEntity.ok(json);
        } catch (Exception e) {
        	logger.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

