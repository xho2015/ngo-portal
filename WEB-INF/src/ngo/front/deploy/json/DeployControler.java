package ngo.front.deploy.json;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ngo.front.storage.entity.Bom;
import ngo.front.web.service.BomService;
 
/*
	Ngo km remote deploy interface	 
 **/
@Controller
public class DeployControler {
 
	@Autowired
    private BomService bomService;
	
	//@Autowired 
	//private ServletContext servletContext;
	
	private final Logger logger = Logger.getLogger(this.getClass());
    
    @RequestMapping(value = "/deploy/bom", method = RequestMethod.GET)
    public ResponseEntity<String> bom(HttpServletRequest request, @RequestParam("path") String bomPath, @RequestParam("token") String token) {
        try {
        	//0 - helpers
        	Map<String, String> uploadBoms = new HashMap<String, String>();
        	Map<String, Bom> storedBoms = new HashMap<String, Bom>();
        	Map<String, Bom> toBeInsertBoms = new HashMap<String, Bom>();
        	Map<String, Bom> toBeUpdateBoms = new HashMap<String, Bom>();
        	
        	
        	//1. load the bom file from webapp root/bom folder
        	ServletContext servletContext = request.getServletContext();
        	String filePath = servletContext.getRealPath(bomPath);
        	FileReader fr =new FileReader(filePath);
        	BufferedReader br = new BufferedReader(fr);
        	String line[], fileId, url, md5, grade, module;
        	while(br.read()!=-1)
            {
            	line=br.readLine().split("\\|");
            	fileId = line[0];
            	url = line[1];
            	grade = line[2].split("/")[0];
            	module = line[2].split("/")[1];
            	md5 = line[3];
            	uploadBoms.put(url, new Bom());
            }
            
        	//2. load the bom info from db
        	List<Bom> dbBoms = bomService.getAllBomObjects();
        	for (Bom b : dbBoms)
        		storedBoms.put(b.getUrl(), b);
        	
        	//3. insertion or updating
        	uploadBoms.entrySet().stream().forEach(s -> {
        		Bom entry = storedBoms.get(s.getKey());
        		if (entry == null)
        		{
        			entry = new Bom();
        			entry.setName("");
        			
        			entry.setMd5(s.getValue());
        			entry.setUrl(s.getKey());
        			
        			toBeInsertBoms.put(s.getKey(), entry);
        		}
        		else if (entry != null && !entry.getMd5().equalsIgnoreCase(s.getValue()))
        		{
        			//update version by increase 1
        			entry.setMd5(s.getValue());
        			entry.setVer(String.valueOf(Integer.parseInt(entry.getVer()) + 1));
        			toBeUpdateBoms.put(s.getKey(), entry);
        		} 
        				
        		
        	});
        	
        	return ResponseEntity.ok("");
        } catch (Exception e) {
        	logger.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

