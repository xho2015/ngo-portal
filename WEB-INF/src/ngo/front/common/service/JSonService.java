package ngo.front.common.service;


import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


@Service
@Scope("singleton")
public class JSonService {

	private final ObjectMapper mapper = new ObjectMapper();
	
	public String toJson(Object obj, Class view)
	{
		try {
    		return mapper.writerWithView(view).writeValueAsString(obj);	
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
        return null;
	}
	
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
