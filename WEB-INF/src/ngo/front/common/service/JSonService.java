package ngo.front.common.service;


import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Service;


@Service
public class JSonService {

	private final ObjectMapper mapper = new ObjectMapper();
	
	public String toJson(Object obj)
	{
		try {
    		return mapper.writeValueAsString(obj);	
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
        return null;
	}
}
