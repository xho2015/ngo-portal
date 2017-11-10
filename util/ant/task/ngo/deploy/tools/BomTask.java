package ngo.deploy.tools;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tools.ant.BuildException;


/**
 * NGO Customized task for Bom, it includes
 * 
 * 	1. generate boms.txt which includes ngo resource meta info. (e.g. name, path, desc, md5, module, grade .etc.)
 * 	2. generate module meta file.
 * 	3. generate grade meta file.
 * 	4. validate the meta.txt in bom path
 * 	5. merge CDN info to url of ngo resource
 * 
 * 
 * This task is based on ant 1.9.9
 * 
 * @author Administrator
 *
 */
public class BomTask extends org.apache.tools.ant.Task {

	private List<String> ngoBom = new ArrayList<String>();
	private List<String> ngoModuleBom = new ArrayList<String>();
	private List<String> ngoGradeBom = new ArrayList<String>();
	private Map<String,String> ngoCDN = new HashMap<String,String>();
	private Map<String,String> ngoMod = new HashMap<String,String>();
	private Map<String,String> ngoCategory = new HashMap<String,String>();
	
	
	

	private String validate;
	private String categoryMapping;
	private String path;
	private String include;
	private String exclude;
	private String bomOutFile;
	private String moduleOutput;
	private String gradeOutput;
	private String spliter;	
	private String bomRoot;
	private String appRoot;
	private String metaName;
	private String cdnName;
	private String modName;

	
	public String getCategoryMapping() {
		return categoryMapping;
	}

	public void setCategoryMapping(String categoryMapping) {
		this.categoryMapping = categoryMapping;
	}

	public String getMetaName() {
		return metaName;
	}

	public void setMetaName(String metaName) {
		this.metaName = metaName;
	}

	public String getCdnName() {
		return cdnName;
	}

	public void setCdnName(String cdnName) {
		this.cdnName = cdnName;
	}

	public String getModName() {
		return modName;
	}

	public void setModName(String orderName) {
		this.modName = orderName;
	}

	public String getValidate() {
		return validate;
	}

	public void setValidate(String validate) {
		this.validate = validate;
	}

	public String getModuleOutput() {
		return moduleOutput;
	}

	public void setModuleOutput(String moduleOutput) {
		this.moduleOutput = moduleOutput;
	}

	public String getGradeOutput() {
		return gradeOutput;
	}

	public void setGradeOutput(String gradeOutput) {
		this.gradeOutput = gradeOutput;
	}

	public String getAppRoot() {
		return appRoot;
	}

	public void setAppRoot(String appRoot) {
		this.appRoot = appRoot;
	}

	public String getBomRoot() {
		return bomRoot;
	}

	public void setBomRoot(String bomRoot) {
		this.bomRoot = bomRoot;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getInclude() {
		return include;
	}

	public void setInclude(String include) {
		this.include = include;
	}

	public String getExclude() {
		return exclude;
	}

	public void setExclude(String exclude) {
		this.exclude = exclude;
	}

	public String getBomOutFile() {
		return bomOutFile;
	}

	public void setBomOutFile(String outFile) {
		this.bomOutFile = outFile;
	}

	public String getSpliter() {
		return spliter;
	}

	public void setSpliter(String spliter) {
		this.spliter = spliter;
	}

	private static boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	private void dumpOutFile()
	{
		String outputFilename = this.path + "/" + this.bomOutFile;
		
		BufferedWriter writer = null;
	    try {
	        writer = new BufferedWriter(new FileWriter(outputFilename));
	        for(String line : ngoBom){
	        	writer.write(line);
	        	writer.write(System.getProperty("line.separator"));     	
	        }
	    } catch (IOException ioe) {
	    	ioe.printStackTrace(System.out);
			throw new BuildException(ioe.getMessage());
	    } finally {
	        if (writer != null) {
	            try {
	                writer.close();
	            } catch (IOException e) {
	            	e.printStackTrace(System.out);
	            }
	        }
	    }
		System.out.println("Bom File: "+ outputFilename +" generated.");
	}
	
	private void dumpGradeOutFile()
	{
		String outputFilename = this.path + "/" + this.gradeOutput;
		
		BufferedWriter writer = null;
	    try {
	        writer = new BufferedWriter(new FileWriter(outputFilename));
	        for(String line : ngoGradeBom){
	        	writer.write(line);
	        	writer.write(System.getProperty("line.separator"));     	
	        }
	    } catch (IOException ioe) {
	    	ioe.printStackTrace(System.out);
			throw new BuildException(ioe.getMessage());
	    } finally {
	        if (writer != null) {
	            try {
	                writer.close();
	            } catch (IOException e) {
	            	e.printStackTrace(System.out);
	            }
	        }
	    }
		System.out.println("Grade File: "+ outputFilename +" is generated!");
	}
	
	private void dumpModuleOutFile()
	{
		String outputFilename = this.path + "/" + this.moduleOutput;
		
		BufferedWriter writer = null;
	    try {
	        writer = new BufferedWriter(new FileWriter(outputFilename));
	        for(String line : ngoModuleBom){
	        	writer.write(line);
	        	writer.write(System.getProperty("line.separator"));     	
	        }
	    } catch (IOException ioe) {
	    	ioe.printStackTrace(System.out);
			throw new BuildException(ioe.getMessage());
	    } finally {
	        if (writer != null) {
	            try {
	                writer.close();
	            } catch (IOException e) {
	            	e.printStackTrace(System.out);
	            }
	        }
	    }
		System.out.println("Module File: "+ outputFilename +" is generated");
	}
	
	private void handleFileByAPI(File file, String folderName) {

		String fileName = file.getAbsolutePath();
		if (!match(include, fileName) || match(exclude, fileName) || fileName.endsWith("meta.txt"))
			return;
		
		String relativeFilename = fileName.replace("\\","/").replace(this.path, "");
		String fileId = relativeFilename.replace(".", "-").replace("/", ".").substring(1);
		String bomPath = (relativeFilename.startsWith(bomRoot) ? folderName : "").replaceAll(bomRoot, "");
		String appPath = (relativeFilename.startsWith(appRoot) ? folderName : "").replaceAll(appRoot, "");

		// try to calculate md5 for file
		try {
			FileInputStream fis = new FileInputStream(new File(fileName));
			String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
			String cdn = relativeFilename;
			String temp[], mod, order = "", module;
			fis.close();			
			//try merge CDN
			if (ngoCDN.containsKey(relativeFilename)) {
				cdn = ngoCDN.get(relativeFilename);
				System.out.println("CDN merged: "+ cdn);
			}
			
			//set default module
			module = (bomPath.length() > 0 ? bomPath : appPath);
			
			//parse mod
			mod = ngoMod.get(relativeFilename);
			if (mod !=null && mod.length() > 0){
				temp = mod.split("#");
				module = temp[0];
				order = temp[1];
			}
			
			//parse file category base on file extension
			String fileTemp[] = file.getName().split("\\.");
			String category= this.ngoCategory.get(fileTemp[fileTemp.length-1]); 
			//pub content in temp array
			ngoBom.add(fileId + this.spliter + cdn + this.spliter + module + this.spliter + md5 + this.spliter + order+ this.spliter + category);
			System.out.println("File: category="+category + ", "+ relativeFilename +", md5=" + md5) ;
		} catch (IOException ioe) {
			ioe.printStackTrace(System.out);
			throw new BuildException(ioe.getMessage());
		}
	}
	
	private void handleMetaFile(File file, String folderName) throws IOException
	{
		//process meta.txt
		File meta = new File(file.getAbsolutePath() + this.metaName);
		if (meta.exists()) 
		{
			System.out.println("meta.txt: "+file.getAbsolutePath());
			BufferedReader br = new BufferedReader(new FileReader(meta));
        	String line, row[], temp = "", type=null;
        	
        	line = br.readLine();
        	row = line.split("=");
        	List<String> out = null;         
    		if (row[0].equals("type") && row[1].equalsIgnoreCase("G")){	
    			out = this.ngoGradeBom;  type = "G";     			
    		} else if (row[0].equals("type") && row[1].equalsIgnoreCase("M")) {
    			out = this.ngoModuleBom;  type = "M";  
    		}
    		line = br.readLine();
        	while (line!=null && line.length() > 0) {       		
        		row = line.split("=");
        		if (row[0].equals("id")) {
        			if (type.equals("G")) {
        				temp += row[1]+"|";
        			} else if (type.equals("M")){
        				temp += row[1].split("/")[0]+"|"+row[1].split("/")[1]+"|";            				
        			}
        			//validate
            		if (this.validate.equals("true")){
            			String currentFolder = meta.getAbsolutePath().replace("\\","/").replace(this.path, "");
            			currentFolder = currentFolder.replace(bomRoot, "").replace("/meta.txt", "");
            			if (!row[1].equalsIgnoreCase(currentFolder))
            				throw new IllegalStateException("id ["+ row[1]+"] is not valid in "+meta.getAbsolutePath());
            		}
        		} else {
        			temp += row[1]+"|";
        		}
        		line = br.readLine();
            }        	
        	out.add(temp.substring(0, temp.length()-1));
        	br.close();
		} 
		else
		{
			if (this.validate.equalsIgnoreCase("true"))
			{
				String relativeFilename = file.getAbsolutePath().replace("\\","/").replace(this.path, "");
				String bomPath = (relativeFilename.startsWith(bomRoot) ? folderName : "").replaceAll(bomRoot, "");
				if (bomPath.length() > 0)
					throw new IllegalStateException(relativeFilename + " don't have "+this.metaName);
			}
		}
		
	}
	
	private void handleCDNFile(File file, String folderName) throws IOException
	{
		//process cdn.txt
		File cdn = new File(file.getAbsolutePath() + this.cdnName);
		if (cdn.exists()) 
		{
			System.out.println("cdn.txt: "+file.getAbsolutePath());
			BufferedReader br = new BufferedReader(new FileReader(cdn));
        	String line, row[];        	
        	line = br.readLine();     	
        	while (line!=null && line.length() > 0) {       		
        		row = line.split("=");   		
        		ngoCDN.put(row[0], row[1]);
        		line = br.readLine(); 
        	}
        	br.close();
		}	
	}
	
	private void handleModFile(File file, String folderName) throws IOException
	{
		//process cdn.txt
		File order = new File(file.getAbsolutePath() + this.modName);
		if (order.exists()) 
		{
			System.out.println("mod.txt: "+file.getAbsolutePath());
			BufferedReader br = new BufferedReader(new FileReader(order));
        	String line, row[];        	
        	line = br.readLine();     	
        	while (line!=null && line.length() > 0) {       		
        		row = line.split("=");   		
        		ngoMod.put(row[0], row[1]);
        		line = br.readLine(); 
        	}
        	br.close();
		}	
	}
	
	private void iterateFiles(File[] files, String folderName) throws IOException  {
		for (File file : files) {
			if (file.isDirectory()) {
				//process with meta.txt in bom path and CDN, order file in app path
				handleCDNFile(file, folderName);
				handleModFile(file, folderName);
				handleMetaFile(file, folderName);				
				// traverse the folder tree
				iterateFiles(file.listFiles(), folderName + "/"+ file.getName());
			} else {
				handleFileByAPI(file, folderName);
			}
		}
	}
	
	@Override
	public void execute() throws BuildException {
		//parse categoryMapping
		String categorys[] = this.categoryMapping.split("#");
		for (String c : categorys)
			this.ngoCategory.put(c.split("=")[0], c.split("=")[1]);
			
		File[] files = new File(this.path).listFiles();
		try {
			iterateFiles(files, "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		//dump file + md5 into out file
		dumpOutFile();
		dumpGradeOutFile();
		dumpModuleOutFile();
	}
}
