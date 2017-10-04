package ngo.deploy.tools;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
 
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
 
public class YuiCompressor {
    public static void main(String args[]) {
        try {
 
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
 
    private static Logger logger = Logger.getLogger(YuiCompressor.class.getName());
    
    
    private static class YuiCompressorErrorReporter implements ErrorReporter {
        public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
            if (line < 0) {
                logger.log(Level.WARNING, message);
            } else {
                logger.log(Level.WARNING, line + ':' + lineOffset + ':' + message);
            }
        }
     
        public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
            if (line < 0) {
                logger.log(Level.SEVERE, message);
            } else {
                logger.log(Level.SEVERE, line + ':' + lineOffset + ':' + message);
            }
        }
     
        public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
            error(message, sourceName, line, lineSource, lineOffset);
            return new EvaluatorException(message);
        }
    }
    
    public static class Options {
        public String charset = "UTF-8";
        public int lineBreakPos = -1;
        public boolean munge = true;
        public boolean verbose = false;
        public boolean preserveAllSemiColons = false;
        public boolean disableOptimizations = false;
    }
    
    public static void compressJavaScript(String inputFilename, String outputFilename, Options o) throws IOException {
        Reader in = null;
        Writer out = null;
        try {
            in = new InputStreamReader(new FileInputStream(inputFilename), o.charset);
     
            JavaScriptCompressor compressor = new JavaScriptCompressor(in, new YuiCompressorErrorReporter());
            in.close(); in = null;
     
            out = new OutputStreamWriter(new FileOutputStream(outputFilename), o.charset);
            compressor.compress(out, o.lineBreakPos, o.munge, o.verbose, o.preserveAllSemiColons, o.disableOptimizations);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }
}

