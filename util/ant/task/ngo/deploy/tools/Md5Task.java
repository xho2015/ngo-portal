package ngo.deploy.tools;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
public class Md5Task extends org.apache.tools.ant.Task {

	private List<String> ngoBom = new ArrayList<String>();

	private String path;

	private String include;

	private String exclude;

	private String outFile;

	private String spliter;
	
	private String bomRoot;
	
	private String appRoot;

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
	        	//note:
	        	//  windows - \r\n
	        	//  unix - \r
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
		System.out.println("NGO bom File: "+ outputFilename +" is generated!");
	}
	
	private void handleFileByAPI(File file, String folderName) {

		String fileName = file.getAbsolutePath();
		if (!match(include, fileName) || match(exclude, fileName))
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

	private void iterateFiles(File[] files, String folderName) {
		for (File file : files) {
			if (file.isDirectory()) {
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
		iterateFiles(files, "");
		//dump file + md5 into out file
		dumpOutFile();
	}
}
