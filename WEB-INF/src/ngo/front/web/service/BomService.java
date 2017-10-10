package ngo.front.web.service;

import org.springframework.stereotype.Service;

import ngo.front.storage.cache.LocalCache;
import ngo.front.storage.entity.Bom;
import ngo.front.storage.entity.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class BomService {
	
	private final ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	private LocalCache cache;
	
	public String getModuleBom(String moduleId, String token)
	{
		/*
		Resource resource = new Resource();
    	List<Bom> links = new ArrayList<Bom>();
    	resource.setLinks(links);
    	links.add(new Bom("three-min-js","1","/app/lib/three.min.ngjs"));
    	links.add(new Bom("dat-gui","1","/app/lib/dat.gui.min.ngjs"));
    	links.add(new Bom("common1","1","/app/lib/common.min.ngjs"));
    	links.add(new Bom("demo1","1","demo1.min.ngjs"));
    	*/
    	try {
    		Map bom = (HashMap)cache.getObject("bom");
    		return mapper.writeValueAsString(bom);	
    	} 
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
        return null;
	}
}
