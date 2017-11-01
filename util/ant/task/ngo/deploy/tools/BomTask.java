package ngo.deploy.tools;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.tools.ant.BuildException;

import ngo.front.storage.entity.Bom;

/**
 * NGO Customized task for 1. calculate md5 checksum for all NGO resource.
 * (ngjs, ngcss, ngdom etc.) 2. gather filename + "|" + md5 of all NGO resource
 * and write into /ngo.bom file
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

	private String validate;
	private String path;
	private String include;
	private String exclude;
	private String outFile;
	private String moduleOutput;
	private String gradeOutput;
	private String spliter;	
	private String bomRoot;
	private String appRoot;
	
	
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

	public String getOutFile() {
		return outFile;
	}

	public void setOutFile(String outFile) {
		this.outFile = outFile;
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
		String outputFilename = this.path + "/" + this.outFile;
		
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
		String fileId = file.getName().replaceAll("\\.", "-");
		String bomPath = (relativeFilename.startsWith(bomRoot) ? folderName : "").replaceAll(bomRoot, "");
		String appPath = (relativeFilename.startsWith(appRoot) ? folderName : "").replaceAll(appRoot, "");

		// try to calculate md5 for file
		try {
			FileInputStream fis = new FileInputStream(new File(fileName));
			String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
			fis.close();
			ngoBom.add(fileId + this.spliter + relativeFilename + this.spliter + (bomPath.length() > 0 ? bomPath : appPath) + this.spliter + md5);
			System.out.println("File: "+ relativeFilename +", md5=" + md5) ;
		} catch (IOException ioe) {
			ioe.printStackTrace(System.out);
			throw new BuildException(ioe.getMessage());
		}
	}
	
	private void handleMetaFile(File file, String folderName) throws IOException
	{
		//process meta.txt
		File meta = new File(file.getAbsolutePath() + "/meta.txt");
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
		} 
		else
		{
			if (this.validate.equalsIgnoreCase("true"))
			{
				String relativeFilename = file.getAbsolutePath().replace("\\","/").replace(this.path, "");
				String bomPath = (relativeFilename.startsWith(bomRoot) ? folderName : "").replaceAll(bomRoot, "");
				if (bomPath.length() > 0)
					throw new IllegalStateException(relativeFilename + " don't have a meta.txt");
			}
		}
		
	}

	private void iterateFiles(File[] files, String folderName) throws IOException  {
		for (File file : files) {
			if (file.isDirectory()) {
				//process with meta.txt
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
		File[] files = new File(this.path).listFiles();
		try {
			iterateFiles(files, "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//dump file + md5 into out file
		dumpOutFile();
		dumpGradeOutFile();
		dumpModuleOutFile();
	}
}
