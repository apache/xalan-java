package org.apache.xalan.xslt.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Stack;

/**
 * A class definition, defining few utility methods for 
 * string information handling.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage general
 */
public class StringUtil {
	
	/**
     * Method definition to check, whether the supplied string value 
     * has balanced parentheses pairs.
     */
    public static boolean isStrHasBalancedParentheses(String strValue, char lParenChar, char rParenChar) {
       
       boolean result = true;
       
       Stack<Character> charStack = new Stack<Character>();
       
       int strLen = strValue.length();
       
       for(int idx = 0; idx < strLen; idx++) {
           char ch = strValue.charAt(idx);
           if (ch == lParenChar) {
              charStack.push(ch); 
           }
           else if (ch == rParenChar){
              if (charStack.isEmpty() || (charStack.pop() != lParenChar)) {
                 // Unbalanced parentheses
                 result = false;
                 break;
              }   
           }
       }
       
       if (!charStack.isEmpty()) {
          result = false;
       }
       
       return result; 
    }
    
    /**
     * Method definition to check, whether the supplied string value 
     * has balanced XPath comment delimiters. XPath comments have
     * lexical form (: comment text :)
     */
    public static boolean isStrHasBalancedXPathCommentDelim(String strValue) {
        
        boolean result = true;
        
        // Sufficiently random character strings, that're unlikely 
        // to occur within an XPath expression.        
        String lDelimStr = new String(new int[] { 2 }, 0, 1);
        String rDelimStr = new String(new int[] { 3 }, 0, 1);
        
        char lDelimChar = lDelimStr.charAt(0);
        char rDelimChar = rDelimStr.charAt(0);
        
        strValue = strValue.replace("(:", lDelimStr);        
        strValue = strValue.replace(":)", rDelimStr);
        
        Stack<Character> charStack = new Stack<Character>();
        
        int strLen = strValue.length();
        
        for(int idx = 0; idx < strLen; idx++) {
            char ch = strValue.charAt(idx);
            if (ch == lDelimChar) {
               charStack.push(ch); 
            }
            else if (ch == rDelimChar){
               if (charStack.isEmpty() || (charStack.pop() != lDelimChar)) {
                  // Unbalanced comment string
                  result = false;
                  break;
               }   
            }
        }
        
        if (!charStack.isEmpty()) {
           result = false;
        }
        
        return result; 
    }
    
    /**
     * Method definition, to get the string contents from a URL 
     * resource.
     */
    public static String getStringContentFromUrl(URL url) throws IOException {            	
    	
    	StringBuilder strBuilder = new StringBuilder();
        
        InputStream inpStream = url.openStream();        
        try {                    
            BufferedReader buffReader = new BufferedReader(new InputStreamReader(inpStream));
            int c;
            while ((c = buffReader.read()) != -1) {
               strBuilder.append((char)c);
            }
        } 
        finally {
            inpStream.close();
        }
     
        return strBuilder.toString();
    }

}
