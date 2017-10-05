package ngo.deploy.tools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.tools.ant.BuildException;


/**
 * NGO Customized task for Gzip static resources e.g. js, css, dom etc. this
 * task is based on ant 1.9.9
 * 
 * @author Administrator
 *
 */
public class GzipTask extends org.apache.tools.ant.Task {

	private String path;

	private String include;

	private String exclude;

	private String executable;

	private String fileExtension;

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

	public String getExecutable() {
		return executable;
	}

	public void setExecutable(String executable) {
		this.executable = executable;
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

		// try to GZip file by using common compressor
		try {
			InputStream in = new FileInputStream(inputFilename);
			OutputStream fout = new FileOutputStream(outputFilename);
			BufferedOutputStream out = new BufferedOutputStream(fout);
			GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(out);
			final byte[] buffer = new byte[1024];
			int n = 0;
			while (-1 != (n = in.read(buffer))) {
				gzOut.write(buffer, 0, n);
			}
			gzOut.close();
			in.close();

			System.out.println("File GZip:" + outputFilename);

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

	@Override
	public void execute() throws BuildException {
		File[] files = new File(this.path).listFiles();
		iterateFiles(files);
	}
}
