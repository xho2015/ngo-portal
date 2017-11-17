package ngo.front.storage.entity;

public class Grade {
	
	private String gid;
		
	private String name;
	
	private String description;


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

	public Grade()
	{
	}
	
	public Grade(String gid, String name, String description)
	{
		this.gid = gid;
		this.name = name;
		this.description = description;
	}
	
	@Override
	public String toString() {
		return new StringBuffer(this.gid).append("#").append(this.name).append("#").append(this.description).toString();
	}	
}
