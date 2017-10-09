package ngo.front.web.json;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
 
/*
 * 
 {
	"version" : "1",
	"name" : "threeminjs",
	"url" : "/app/lib/three.min.ngjs"
}, {
	"version" : "1",
	"name" : "datgui",
	"url" : "/app/lib/dat.gui.min.ngjs"
}, {
	"version" : "1",
	"name" : "common1",
	"url" : "/app/lib/common.min.ngjs"
}, {
	"version" : "1",
	"name" : "demo1",
	"url" : "demo1.min.ngjs"
} 
		 
 **/
@Controller
public class BomControler {
 
	private ObjectMapper mapper = new ObjectMapper();
	
	private final Logger logger = Logger.getLogger(this.getClass());
    
    @RequestMapping(value = "/json/bom", method = RequestMethod.GET)
    public @ResponseBody String bom() {
        try {
        	Resource resource = new Resource();
        	List<Bom> links = new ArrayList<Bom>();
        	resource.setLinks(links);
        	links.add(new Bom("threeminjs","1","/app/lib/three.min.ngjs"));
        	links.add(new Bom("datgui","1","/app/lib/dat.gui.min.ngjs"));
        	links.add(new Bom("common1","1","/app/lib/common.min.ngjs"));
        	links.add(new Bom("demo1","1","demo1.min.ngjs"));
            return mapper.writeValueAsString(resource);
        } catch (Exception e) {
        	logger.error(e.getMessage());
        }
        return null;
    }   
}

