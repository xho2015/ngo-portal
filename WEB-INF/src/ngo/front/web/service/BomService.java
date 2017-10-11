package ngo.front.web.service;

import org.springframework.stereotype.Service;

import ngo.front.storage.entity.Bom;
import ngo.front.storage.entity.Resource;
import ngo.front.storage.service.LocalCache;


import org.springframework.beans.factory.annotation.Autowired;

@Service
public class BomService {
	
	
	@Autowired
	private LocalCache localCache;
	
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

    	return (String)localCache.getObject("bom");	
	}
	
	public void refreshBom(){
		localCache.refreshObject("bom");
	}
	
}
