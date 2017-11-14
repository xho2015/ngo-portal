package ngo.front.storage.entity;

import org.codehaus.jackson.map.annotate.JsonView;



public class Resource {
	
	public interface Dafault {}
	public interface Version extends Dafault{}
	public interface Expire extends Dafault{}
	public interface MD5 extends Dafault{}
	
	@JsonView(Resource.Dafault.class)
	private Object content;

	@JsonView(Resource.Version.class)
	private int version;
	
	@JsonView(Resource.Expire.class)
	private int expire;

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getExpire() {
		return expire;
	}

	public void setExpire(int expire) {
		this.expire = expire;
	}
	
	
	
	
	

}
