package ngo.front.storage.entity;

import org.codehaus.jackson.map.annotate.JsonView;
import org.apache.commons.codec.digest.DigestUtils;



public class Resource {
	
	public interface Dafault {}
	public interface Version extends Dafault{}
	public interface Expire extends Dafault{}
	public interface MD5 extends Dafault{}
	
	@JsonView(Resource.Dafault.class)
	private Object payload;

	@JsonView(Resource.Version.class)
	private String version;
	
	@JsonView(Resource.Expire.class)
	private int expire;

	public Resource(Object object) {
		this.setPayload(object);
		this.setVersion(DigestUtils.md2Hex(object.toString()));
	}

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object content) {
		this.payload = content;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getExpire() {
		return expire;
	}

	public void setExpire(int expire) {
		this.expire = expire;
	}
}
