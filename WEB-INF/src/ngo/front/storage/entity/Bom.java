package ngo.front.storage.entity;

import org.codehaus.jackson.annotate.JsonIgnore;

public class Bom {
	
	@JsonIgnore
	private String grade;
	@JsonIgnore
	private String module;
	@JsonIgnore
	private String md5;
	
	private String name;
	private int ver;
	private String url;
	private int lorder;
	private String category;
	
	public Bom()
	{
	}
	
	public Bom(String fid, String url, String grade, String module, String md5, int lorder,String category)
	{
		this.name = fid;
		this.url = url;
		this.grade = grade;
		this.module = module;
		this.md5 = md5;
		this.lorder = lorder;
		this.category = category;
	}
	
	
	
	public int getLorder() {
		return lorder;
	}

	public void setLorder(int lorder) {
		this.lorder = lorder;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getVer() {
		return ver;
	}
	public void setVer(int version) {
		this.ver = version;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	
	@Override
	public String toString() {
		return new StringBuffer(this.name).append("#").append(this.ver).append("#").append(url).append("#").append(grade).append("#").append(module).append("#").append(md5).append("#").append(lorder).toString();
	}
	
}
