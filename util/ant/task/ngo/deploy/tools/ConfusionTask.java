package ngo.deploy.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;



import ngo.deploy.tools.YuiCompressor.Options;

/**
 * NGO Customized task for javascript compression, it's based on ant 1.9.9
 * 
 * @author Administrator
 *
 */
public class ConfusionTask extends org.apache.tools.ant.Task {
	
	private List<String> ignores = new ArrayList<String>();
	

	private String path;

	private String include;
	
	private String ignore;

	private String exclude;

	private String fileExtension;

	
	public String getIgnore() {
		return ignore;
	}

	public void setIgnore(String ignore) {
		this.ignore = ignore;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
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

	private static boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	private void handleFile(File file) {

		String fileName = file.getAbsolutePath();
		
		if (!match(include, fileName) || match(exclude, fileName) || ignores.contains(fileName))
			return;

		System.out.println("YUI compressor for: " + fileName);
		
		String extension = "";
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			extension = fileName.substring(i + 1);
		}
		
		String inputFilename = fileName;
		String outputFilename = fileName.replace("." + extension, "." + this.fileExtension);

		try {
			YuiCompressor.compressJavaScript(inputFilename, outputFilename, new Options());	
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw new BuildException(e.getMessage());
		}
	}

	private void iterateFiles(File[] files) {
		for (File file : files) {
			if (file.isDirectory()) {
				//traverse the folder tree
				iterateFiles(file.listFiles());
			} else {
				handleFile(file);
			}
		}
	}

	@Override
	public void execute() throws BuildException {
		File root = new File(this.path);
		for (String f : this.ignore.split("\\|"))
		{
			String af = root.getAbsolutePath() + f;
			System.out.println("DEBUG: ignore="+af);		
			ignores.add(af);
		}
		
		File[] files = root.listFiles();
		iterateFiles(files);
	}
}
