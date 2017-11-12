package ngo.deploy.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;



import ngo.deploy.tools.YuiCompressor.Options;
import sun.misc.BASE64Encoder;

/**
 * NGO Customized task for picture BASE64 encoding, it's based on ant 1.9.9
 * 
 * @author Administrator
 *
 */
public class Base64Task extends org.apache.tools.ant.Task {
	
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
		
		if (!match(include, fileName) || match(exclude, fileName))
			return;

		System.out.println("BASE64 encoding for: " + fileName);
		
		String extension = "";
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			extension = fileName.substring(i + 1);
		}
		
		String inputFilename = fileName;
		String outputFilename = fileName.replace("." + extension, "." + this.fileExtension);

		try {
			InputStream in = null;
			byte[] data = null;
			in = new FileInputStream(fileName);
			data = new byte[in.available()];
			in.read(data);
			in.close();
			BASE64Encoder encoder = new BASE64Encoder();
			String png = encoder.encode(data);
			OutputStream out = new FileOutputStream(outputFilename);
			out.write(png.getBytes());
			out.flush();
			out.close();
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
		File[] files = root.listFiles();
		iterateFiles(files);
	}
}
