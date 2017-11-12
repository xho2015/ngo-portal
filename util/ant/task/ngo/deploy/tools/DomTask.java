package ngo.deploy.tools;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.BuildException;

import org.codehaus.jackson.map.ObjectMapper;

import ngo.front.storage.entity.Bom;

/**
 * NGO Customized task for converting htm into ngdom file
 * This task is based on ant 1.9.9
 * 
 * @author Administrator
 *
 */
public class DomTask extends org.apache.tools.ant.Task {

	private String path;
	
	private String compact;
	
	private String verFile;
	
	private String include;

	private String exclude;

	private String executable;

	private String fileExtension;
	
	private String verSeed;
	
	private String verSeedName;
	
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public String getCompact() {
		return compact;
	}

	public void setCompact(String compact) {
		this.compact = compact;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
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

	public String getExecutable() {
		return executable;
	}

	public void setExecutable(String executable) {
		this.executable = executable;
	}
	
	public String getVerFile() {
		return verFile;
	}

	public void setVerFile(String verFile) {
		this.verFile = verFile;
	}

	public String getVerSeed() {
		return verSeed;
	}

	public void setVerSeed(String verSeed) {
		this.verSeed = verSeed;
	}

	public String getVerSeedName() {
		return verSeedName;
	}

	public void setVerSeedName(String verSeedName) {
		this.verSeedName = verSeedName;
	}

	private static boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	private void handleFileByAPI(File file) {

		String fileName = file.getAbsolutePath();
		if (!match(include, fileName) || match(exclude, fileName))
			return;

		String extension = "";
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			extension = fileName.substring(i + 1);
		}

		String inputFilename = fileName;
		String outputFilename = fileName.replace("." + extension, "." + this.fileExtension);
		String relativeFilename = fileName.replace("\\","/").replace(this.path, "");
		String fileId = relativeFilename.replace(".", "-").replace("/", ".").substring(1);
		boolean isReplaced = false;

		try {
			//1. read content of dom file
			@SuppressWarnings("deprecation")
			String dom = FileUtils.readFileToString(file, "utf-8");
			
			//2. loop in ver mapping entries
			for (Map.Entry<String,Bom> entry : bomVerMap.entrySet()){
			    if (dom.contains("@"+entry.getValue().getName()+"@")){
			    	
			    	//2.a generate local md5
			    	String fileUrl =  entry.getValue().getUrl().contains("#") ? entry.getValue().getUrl().split("#")[1] : entry.getValue().getUrl();
			    	FileInputStream fis = new FileInputStream(new File(this.path + fileUrl));
					String localMD5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
					fis.close();
					
					//2.b validate md5 between local & server version
					String serverMD5 = entry.getValue().getMd5();
					if (!localMD5.equals(serverMD5))	{
						throw new IllegalStateException(fileId+ " has different MD5 value between local & server version.");
					}
					
					//2.c do url+ver tag replacement
					dom = dom.replaceAll("@"+entry.getValue().getName()+"@", entry.getValue().getUrl()+"?"+this.verSeed+entry.getValue().getVer());
					isReplaced = true;
			    }
			}
			
			//3. replace 'verSeedName' tag
			dom = dom.replaceAll(this.verSeedName, verSeed);
			
			//3.1 replace all line endings if compact is set to true
			if (this.compact.equalsIgnoreCase("true")){
				dom = dom.replaceAll("\r\n", "");
				dom = dom.replaceAll("\t", "");
			}
			
			//4.dump replaced dom into file
			final BufferedWriter bo = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFilename), "utf-8"));
			try {
			    bo.write(dom);
			} finally {
			    bo.close();
			}
					
			if (isReplaced)
				System.out.println("DOM ver tag replaced for: " + fileName);		
			
		} catch (IOException ioe) {
			ioe.printStackTrace(System.out);
			throw new BuildException(ioe.getMessage());
		}
	}

	private void iterateFiles(File[] files) {
		for (File file : files) {
			if (file.isDirectory()) {
				// traverse the folder tree
				iterateFiles(file.listFiles());
			} else {
				handleFileByAPI(file);
			}
		}
	}
	
	private Map<String, Bom> bomVerMap = new HashMap<String, Bom>(); 
	
	private void loadBomVer()
	{
		String vpath = this.path + "/"+ this.verFile;
		System.out.println("DEBUG: ver file path is:"+vpath);
		File verFile = new File(vpath);
		if (verFile.exists()) 
		{
			try {
				@SuppressWarnings("deprecation")
				String json = FileUtils.readFileToString(verFile);
				ObjectMapper mapper = new ObjectMapper();
				Bom[] bomVers = mapper.readValue(json, Bom[].class);
				for(Bom b : bomVers){
					//System.out.println("DEBUG: Bom -"+b.toString());
					bomVerMap.put(b.getName(), b);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		}	
	}

	@Override
	public void execute() throws BuildException {
		loadBomVer();
		File[] files = new File(this.path).listFiles();
		iterateFiles(files);
	}
}
