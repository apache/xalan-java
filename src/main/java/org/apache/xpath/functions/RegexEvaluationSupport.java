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
package org.apache.xpath.functions;

import org.apache.xpath.regex.Matcher;
import org.apache.xpath.regex.Pattern;

/**
 * Class definition, providing support for implementing regex 
 * features available within XPath 3.1 F&O and XSLT 3.0 
 * specifications.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class RegexEvaluationSupport {
	
	
	// Class field, denoting allowable XPath regex flag characters.
    private static final String VALID_REGEX_FLAG_CHARS = "smixq";
	
    
    /**
     * Method definition, to transform the supplied regex string, to 
     * resolve differences between, XML Schema regex subtraction operator 
     * and Java regex subtraction operator.
     * 
     * @param regexStr                         The supplied regex string
     * @return                                 The transformed regex string
     */
	public static String transformRegexStrForSubtrOp(String regexStr) {
		
		String result = regexStr;
		
		int indx1 = result.indexOf("-[");
		if (indx1 != -1) {
			String subsPrev = result.substring(0, indx1);
			String subsAfter = result.substring(indx1 + 2);
			if ((subsPrev.indexOf("[") != -1) && (subsAfter.indexOf("]]") != -1)) {
				result = result.replaceAll("\\-\\[", "&&[^");	
			}
		}
		
		return result;
	}
	
	/**
	 * Method definition, to build regex matcher object, using the
	 * supplied regex string, regex flags and input string to be 
	 * matched.
	 * 
	 * @param regexStr                       The supplied regex string
	 * @param regexFlags                     The supplied regex flags
	 * @param str1                           An input string value to be 
	 *                                       matched by regex.
	 * @return                               Regex matcher object
	 */
	public static Matcher getRegexMatcher(String regexStr, String regexFlags, String str1) {
		Matcher matcher = compileAndExecute(regexStr, regexFlags, str1);
		
		return matcher;
	}
	
	/**
	 * Method definition, to check whether the supplied input
	 * string contains allowable regex flag characters.
	 * 
	 * @param regexFlags                    The supplied regex flags
	 * @return                              Boolean value true or false
	 */
	public static boolean isRegexFlagStrValid(String regexFlags) {
       
	   boolean result = true;
       
	   int size1 = regexFlags.length();
	   
       if (size1 > 0) {
    	  for (int idx = 0; idx < size1; idx++) {
    		 if (VALID_REGEX_FLAG_CHARS.indexOf(regexFlags.charAt(idx)) == -1) {
    			result = false;
    			
    			break;
    		 }
    	  }
       }
       
       return result; 
	}
	
	/**
	 * Method definition, to compile a regex string, and build regex
	 * matcher object.
	 * 
	 * @param regexStr                       The supplied regex string
	 * @param regexFlags                     The supplied regex flags
	 * @param str1                           An input string value to be 
	 *                                       matched by regex.
	 * @return                               Regex matcher object
	 */
	public static Matcher compileAndExecute(String regexStr, String regexFlags, 
	                                                                  String str1) {	
				
		Matcher result = null;
		
		int flag = 0;
		
		String osNameStr = System.getProperty("os.name");
		if (!osNameStr.startsWith("Windows")) {
		   flag = Pattern.UNIX_LINES;
		}
		
		Pattern pattern = null;
		
		if ((regexFlags != null) && !"".equals(regexFlags)) {			
			if (regexFlags.indexOf("s") >= 0) {
				flag = flag | Pattern.DOTALL;
			}
			if (regexFlags.indexOf("m") >= 0) {
                flag = flag | Pattern.MULTILINE;
            }
			if (regexFlags.indexOf("i") >= 0) {
				flag = flag | Pattern.CASE_INSENSITIVE;
			}			
			if (regexFlags.indexOf("x") >= 0) {
				flag = flag | Pattern.IGNORE_WHITESPACE;
			}
			if (regexFlags.indexOf("q") >= 0) {
                flag = flag | Pattern.LITERAL;
            }
			
			pattern = Pattern.compile(regexStr, flag);
		}
		else {
		    pattern = Pattern.compile(regexStr);
		}
		
		result = pattern.matcher(str1); 
		
		return result;
	}

}
