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

import com.sun.xml.internal.bind.v2.model.core.ID;

import ngo.front.web.service.BomService;
import ngo.front.web.service.GradeService;
import ngo.front.web.service.ModuleService;
 
/*
		 
 **/
@Controller
public class GradeControler {
 
	@Autowired
    private GradeService gradeService;
	
	private final Logger logger = Logger.getLogger(this.getClass());
    
    @RequestMapping(value = "/json/grade", method = RequestMethod.GET)
    public ResponseEntity<String> bom(@RequestParam("id") String id, @RequestParam("token") String token) {
        try {
        	if (id.equals("all"))	
        		return ResponseEntity.ok(gradeService.getAll());
        	else
        		return ResponseEntity.ok(gradeService.getById(id));      	
        } catch (Exception e) {
        	logger.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

