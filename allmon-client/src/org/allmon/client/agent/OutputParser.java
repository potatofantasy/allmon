package org.allmon.client.agent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.allmon.common.AllmonLoggerConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OutputParser {

	private static final Log logger = LogFactory.getLog(OutputParser.class);
    
    // Charset and decoder for ISO-8859-15
    private static Charset charset = Charset.forName("ISO-8859-15");
    private static CharsetDecoder decoder = charset.newDecoder();

    // Pattern used to parse lines
    private Pattern linePattern = Pattern.compile(".*\r?\n");

    // The input pattern that we're looking for
    private Pattern pattern;

    // Compile the pattern from the command line
    private void compile(String pat) {
        try {
            pattern = Pattern.compile(pat);
        } catch (PatternSyntaxException ex) {
            System.err.println(ex.getMessage());
            //System.exit(1);
        }
    }

    // Use the linePattern to break the given CharBuffer into lines, applying
    // the input pattern to each line to see if we have a match
    private void grep(File f, CharBuffer cb) {
        Matcher lm = linePattern.matcher(cb); // Line matcher
        Matcher pm = null; // Pattern matcher
        int lines = 0;
        while (lm.find()) {
            lines++;
            CharSequence cs = lm.group(); // The current line
            if (pm == null)
                pm = pattern.matcher(cs);
            else
                pm.reset(cs);
            if (pm.find())
                System.out.print(f + ":" + lines + ":" + cs);
            if (lm.end() == cb.limit())
                break;
        }
    }

    // Search for occurrences of the input pattern in the given file
    private void grep(File f) throws IOException {

        // Open the file and then get a channel from the stream
        FileInputStream fis = new FileInputStream(f);
        FileChannel fc = fis.getChannel();

        // Get the file's size and then map it into memory
        int sz = (int) fc.size();
        MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, sz);

        // Decode the file into a char buffer
        CharBuffer cb = decoder.decode(bb);

        // Perform the search
        grep(f, cb);

        // Close the channel and the stream
        fc.close();
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java Grep pattern file...");
            return;
        }
        
        OutputParser op = new OutputParser();
        
        op.compile(args[0]);
        for (int i = 1; i < args.length; i++) {
            File f = new File(args[i]);
            try {
                op.grep(f);
            } catch (IOException x) {
                System.err.println(f + ": " + x);
            }
        }
    }

    
    public static String findFirst(BufferedReader br, String searchPhrase) {
    	logger.debug(AllmonLoggerConstants.ENTERED);
        StringBuffer fullSearchResults = new StringBuffer();
        String metric = "";
    	String inputLine;
        int i = 0;
        try {
			while ((inputLine = br.readLine()) != null) {
			    logger.debug(inputLine);
			    Pattern p = Pattern.compile(searchPhrase);
			    Matcher m = p.matcher(inputLine);
			    while (m.find()) {
			        CharSequence cs = m.group();
			        fullSearchResults.append(cs);
			        fullSearchResults.append(" ");
			        if (i == 0) {
			            metric = cs.toString();
			            logger.debug(AllmonLoggerConstants.EXITED);
			            //return metric; // XXX code this properly later
			        }
			        i++;
			    }
			    fullSearchResults.append("\n");
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
        logger.debug("Found " + i + " phrases " + fullSearchResults.toString().trim()); // XXX send the message
    	logger.debug(AllmonLoggerConstants.EXITED);
        return metric;
    }
    
}
