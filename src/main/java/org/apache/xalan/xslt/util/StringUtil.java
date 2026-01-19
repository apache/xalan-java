/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xalan.xslt.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Stack;

/**
 * This class definition, specifies few utility methods for 
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
     * Method definition, to check whether the supplied string value 
     * has balanced XPath comment delimiters. XPath comments have
     * lexical form (: comment text :)
     */
    public static boolean isStrHasXPathBalancedCommentDelim(String strValue) {
        
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
     * Method definition, to remove XPath comments from the
     * supplied string value.
     * 
     * This method handles occurrences of XPath nested comments,
     * as well, as required by XPath 3.1 spec.
     * 
     * @param strValue				  The supplied string value
     * @return                        String value, after the XPath 
     *                                comments have been removed.  
     */
    public static String removeXPathComments(String strValue) {
    	
    	String result = null;

    	String str1 = strValue;

    	str1 = str1.replaceAll("\\(:", "\u0002");
    	str1 = str1.replaceAll(":\\)", "\u0003");

    	StringBuilder strBuilder = new StringBuilder();
    	int level = 0;
    	int strLength = str1.length();
    	for (int idx = 0; idx < strLength; idx++) {
    		char chr = str1.charAt(idx);
    		if (chr == '\u0002') {
    			level++;  
    		}
    		else if (chr == '\u0003') {
    			if (level > 0) {
    				level--;  
    			}
    		}
    		else if (level == 0) {
    			strBuilder.append(chr); 
    		}
    	}

    	if (level <= 0) {
    		str1 = strBuilder.toString(); 
    	}

    	result = str1;

    	return result;
    }
    
    /**
     * Method definition, to get the string contents from a URL 
     * resource.
     */
    public static String getStringContentFromUrl(URL url) throws IOException {            	
    	
    	String result = null;
    	
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
        
        result = strBuilder.toString(); 
     
        return result;
    }
    
    /**
     * Method definition, to trim whitespace characters from  
     * RHS of an input string, and returning resulting string.
     */
    public static String strRtrim(String str) {
 	 
    	String result = null;

    	if (str.length() == 0) {
    		result = "";  
    	}
    	else if (str.length() == 1) {
    		if (Character.isWhitespace(str.charAt(0))) {
    			result = ""; 
    		}
    		else {
    			result = str;
    		}
    	}
    	else {
    		char chr = str.charAt(str.length() - 1);
    		if (Character.isWhitespace(chr)) {
    			result = str.substring(0, str.length() - 1);
    			result = strRtrim(result); 
    		}
    		else {
    			result = str; 
    		}
    	}

    	return result; 	  
    }

}
