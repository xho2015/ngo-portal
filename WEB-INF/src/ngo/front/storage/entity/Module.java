package ngo.front.storage.entity;

public class Module {
	
	private String mid;
	
	private String gid;
		
	private String name;
	
	private String description;

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Module()
	{
	}
	
	public Module(String gid, String mid, String name, String description)
	{
		this.mid = mid;
		this.gid = gid;
		this.name = name;
		this.description = description;
	}
	
	@Override
	public String toString() {
		return new StringBuffer(this.mid).append("#").append(this.gid).append("#").append(this.name).append("#").append(this.description).toString();
	}	
}
