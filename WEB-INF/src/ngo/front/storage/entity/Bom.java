package ngo.front.storage.entity;

import org.codehaus.jackson.annotate.JsonIgnore;

public class Bom {
	
	@JsonIgnore
	private String grade;
	@JsonIgnore
	private String module;
	@JsonIgnore
	private String category;
	
	private String name;
	private String ver;
	private String url;
	
	@JsonIgnore
	private String md5;
	
	public Bom()
	{
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVer() {
		return ver;
	}
	public void setVer(String version) {
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
		return new StringBuffer(this.name).append(",").append(this.ver).append(",").append(url).toString();
	}
	
}
