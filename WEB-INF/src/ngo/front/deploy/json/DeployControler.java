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
	
	@Autowired 
	private ServletContext servletContext;
	
	private final Logger logger = Logger.getLogger(this.getClass());
    
    @RequestMapping(value = "/json/deploy", method = RequestMethod.GET)
    public ResponseEntity<String> deploy(@RequestParam("path") String bomPath, @RequestParam("token") String token) {
        try {
        	//TODO: validate token here
        	     	
        	//0 - helpers
        	Map<String, Bom> uploadedBoms = new HashMap<String, Bom>();
        	Map<String, Bom> storedBoms = new HashMap<String, Bom>();
        	Map<String, Bom> toBeInsertBoms = new HashMap<String, Bom>();
        	Map<String, Bom> toBeUpdateBoms = new HashMap<String, Bom>();
        	
        	//1. load the bom file from webapp root/bom folder
        	String filePath = servletContext.getRealPath(bomPath);
        	BufferedReader br = new BufferedReader(new FileReader(filePath));
        	String line, row[], fileId, url, md5, temp[], grade, module;
        	line = br.readLine();
            while (line!=null && line.length() > 0) {
        		row = line.split("\\|");
        		fileId = row[0];
            	url = row[1];
            	temp = row[2].split("/");
            	grade = temp.length == 2 ? temp[0] : "";
            	module = temp.length == 2 ? temp[1] : "";
            	md5 = row[3];
            	uploadedBoms.put(url, new Bom(fileId, url, grade, module, md5));
            	line = br.readLine();
            } 
            
        	//2. load the bom info from db
        	List<Bom> dbBoms = bomService.getAllBomObjects();
        	for (Bom b : dbBoms)
        		storedBoms.put(b.getUrl(), b);
        	
        	//3. find bom to be inserted or updated
        	uploadedBoms.entrySet().stream().forEach(s -> {
        		Bom entry = storedBoms.get(s.getKey());
        		if (entry == null)
        		{
        			s.getValue().setVer("1");
        			toBeInsertBoms.put(s.getKey(), s.getValue());
        		}
        		else 
        		{
        			Bom u = s.getValue(); 
        			if (!u.getMd5().equalsIgnoreCase(entry.getMd5()))
        			{
        				//update version by increase 1
            			entry.setMd5(u.getMd5());
            			entry.setVer(String.valueOf(Integer.parseInt(entry.getVer()) + 1));
            			toBeUpdateBoms.put(s.getKey(), entry);	
        			}
        		}
        	});
        	
        	//4. do insertion and updating
        	toBeUpdateBoms.entrySet().stream().forEach(s -> {
        		bomService.updateBomObject(s.getValue());
            });
        	toBeInsertBoms.entrySet().stream().forEach(s -> {
        		bomService.addBomObject(s.getValue());
            });
        	
        	return ResponseEntity.ok("["+new java.util.Date()+"] Bom daploy on ["+ servletContext.getServerInfo() +"], Inserted:"+toBeInsertBoms.size()+", Updated:"+toBeUpdateBoms.size());
        } catch (Exception e) {
        	logger.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

