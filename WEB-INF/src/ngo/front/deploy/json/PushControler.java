package ngo.front.deploy.json;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

import ngo.front.deploy.service.PushService;
import ngo.front.storage.entity.Bom;
import ngo.front.storage.entity.Module;
import ngo.front.web.service.BomService;
 
/*
	Ngo km remote push interface	 
 **/
@Controller
public class PushControler {
 
	@Autowired
    private PushService pushService;
	
	@Autowired 
	private ServletContext servletContext;
	
	private final Logger logger = Logger.getLogger(this.getClass());
    
    @RequestMapping(value = "/ants/push", method = RequestMethod.GET)
    public ResponseEntity<String> deploy(@RequestParam("type") String type, @RequestParam("path") String outFilePath, @RequestParam("token") String token) {
    	
    	//TODO: validate token here
    	
    	if (type.equals("bom"))
    		return handleBom(outFilePath);
    	else if (type.equals("module"))
    		return handleModule(outFilePath);
    		
    	return ResponseEntity.ok("NA");    	
    }

    private ResponseEntity<String> handleBom(String path)
    {
    	// close this will also close wrapped file reader 
    	//https://stackoverflow.com/questions/9541614/how-does-this-filereader-get-closed
    	BufferedReader br = null;

        try {     	     	
        	//0 - helpers
        	Map<String, Bom> uploadedBoms = new HashMap<String, Bom>();
        	Map<String, Bom> storedBoms = new HashMap<String, Bom>();
        	Map<String, Bom> toBeInsertBoms = new HashMap<String, Bom>();
        	Map<String, Bom> toBeUpdateBoms = new HashMap<String, Bom>();
        	
        	//1. load the bom file from webapp folder
        	String filePath = servletContext.getRealPath(path);
        	FileReader fr = new FileReader(filePath);
        	br = new BufferedReader(fr);
        	String line, row[], name, url, md5, temp[], grade, module, lorder, category;
        	line = br.readLine();
            while (line!=null && line.length() > 0) {
        		row = line.split("\\|");
        		name = row[0];
            	url = row[1];
            	temp = row[2].split("/");
            	grade = temp.length == 2 ? temp[0] : "";
            	module = temp.length == 2 ? temp[1] : temp[0];
            	md5 = row[3];
            	lorder = row[4].isEmpty() ? "0" : row[4];
            	category = row[5];
            	uploadedBoms.put(name, new Bom(name, url, grade, module, md5, Integer.parseInt(lorder),category));
            	line = br.readLine();
            } 
            
        	//2. load the bom info from db
        	List<Bom> dbBoms = pushService.getAllBomObjects();
        	for (Bom b : dbBoms)
        		storedBoms.put(b.getName(), b);
        	
        	//3. find bom to be inserted or updated
        	uploadedBoms.entrySet().stream().forEach(s -> {
        		Bom entry = storedBoms.get(s.getKey());
        		if (entry == null) {
        			s.getValue().setVer(1);
        			toBeInsertBoms.put(s.getKey(), s.getValue());
        		}
        		else 
        		{
        			Bom u = s.getValue();
        			boolean isMd5 = u.getMd5().equalsIgnoreCase(entry.getMd5());
        			boolean isModule = u.getModule().equals(entry.getModule());
        			boolean isGrade = u.getGrade().equals(entry.getGrade());
        			boolean isLoader = u.getLorder() == entry.getLorder();
        			if (!isMd5 || !isModule || !isGrade || !isLoader )
        			{
        				//update version by increase 1
            			entry.setMd5(u.getMd5());
            			entry.setModule(u.getModule());
            			entry.setGrade(u.getGrade());
            			entry.setLorder(u.getLorder());
            			if (!isMd5)
            				entry.setVer(entry.getVer() + 1);
            			toBeUpdateBoms.put(s.getKey(), entry);	
        			}
        		}
        	});
        	
        	//4. do insertion and updating
        	toBeUpdateBoms.entrySet().stream().forEach(s -> {
        		pushService.updateBomObject(s.getValue());
            });
        	toBeInsertBoms.entrySet().stream().forEach(s -> {
        		pushService.addBomObject(s.getValue());
            });
        	
        	return ResponseEntity.ok("["+new java.util.Date()+"] Bom entry pushed, Inserted:"+toBeInsertBoms.size()+", Updated:"+toBeUpdateBoms.size());
        } catch (Exception e) {
        	logger.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Push error: "+e.getMessage());
        } finally {
        	try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private ResponseEntity<String> handleModule(String path)
    {
    	// close this will also close wrapped file reader 
    	//https://stackoverflow.com/questions/9541614/how-does-this-filereader-get-closed
    	BufferedReader br = null;

        try {     	     	
        	//0 - helpers
        	Map<String, Module> uploadedModules = new HashMap<String, Module>();
        	Map<String, Module> storedModules = new HashMap<String, Module>();
        	Map<String, Module> toBeInsertModules = new HashMap<String, Module>();
        	Map<String, Module> toBeUpdateModules = new HashMap<String, Module>();
        	
        	//1. load the bom file from webapp root/bom folder
        	String filePath = servletContext.getRealPath(path);
        	FileReader fr = new FileReader(filePath);
        	br = new BufferedReader(fr);
        	String line, row[], name, gid, mid, description;
        	line = br.readLine();
            while (line!=null && line.length() > 0) {
        		row = line.split("\\|");
        		gid = row[0];
        		mid = row[1];
        		name = row[2];
        		description = row[3];      	
        		uploadedModules.put(mid, new Module(gid, mid, name, description));
            	line = br.readLine();
            } 
            
        	//2. load the module info from db
        	List<Module> dbModules = pushService.getAllModuleObjects();
        	for (Module m : dbModules)
        		storedModules.put(m.getMid(), m);
        	
        	//3. find module to be inserted or updated
        	uploadedModules.entrySet().stream().forEach(s -> {
        		Module entry = storedModules.get(s.getKey());
        		if (entry == null) {
        			toBeInsertModules.put(s.getKey(), s.getValue());
        		}
        		else 
        		{
        			Module m = s.getValue();
        			boolean isGid = m.getGid().equalsIgnoreCase(entry.getGid());
        			boolean isName = m.getName().equals(entry.getName());
        			boolean isDesc = m.getDescription().equals(entry.getDescription());
        			if (!isGid || !isName || !isDesc)
        			{
        				//update version by increase 1
            			entry.setGid(m.getGid());
            			entry.setName(m.getName());
            			entry.setDescription(m.getDescription());		
            			toBeUpdateModules.put(s.getKey(), entry);	
        			}
        		}
        	});
        	
        	//4. do insertion and updating
        	toBeUpdateModules.entrySet().stream().forEach(s -> {
        		pushService.updateModuleObject(s.getValue());
            });
        	toBeInsertModules.entrySet().stream().forEach(s -> {
        		pushService.addModuleObject(s.getValue());
            });
        	
        	return ResponseEntity.ok("["+new java.util.Date()+"] Module entry pushed, Inserted:"+toBeInsertModules.size()+", Updated:"+toBeUpdateModules.size());
        } catch (Exception e) {
        	logger.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Push error: "+e.getMessage());
        } finally {
        	try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

