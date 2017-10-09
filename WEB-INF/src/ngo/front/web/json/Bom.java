package ngo.front.web.json;

public class Bom {
	private String name;
	private String version;
	private String url;
	
	public Bom(String n, String v, String u)
	{
		this.name = n; this.version = v; this.url = u;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String toString() {
		return new StringBuffer(this.name).append(",").append(this.version).append(",").append(url).toString();
	}
	
}
