/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
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
/*
 * $Id$
 */
package org.apache.xpath.functions;

import org.apache.xpath.regex.Matcher;
import org.apache.xpath.regex.Pattern;

/**
 * This class provides supporting implementation, common to all
 * XPath 3.1 functions and the XSLT instruction xsl:analyze-string 
 * that require regex functionality.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class RegexEvaluationSupport {
	
	/**
	 * Variable indicating that, XPath regex valid flag 
	 * characters are : s, m, i, x, q.
	 */
    private static final String VALID_REGEX_FLAG_CHARS = "smixq";
	
	/*
	 * Transform regex pattern input string, to resolve differences between, 
	 * XML Schema regex subtraction operator and Java regex subtraction operator. 
	 */
	public static String transformRegexStrForSubtractionOp(String pattern) {
		String transformedPatternStr = pattern;
		
		int indx1 = transformedPatternStr.indexOf("-[");
		if (indx1 != -1) {
			String subsPrev = transformedPatternStr.substring(0, indx1);
			String subsAfter = transformedPatternStr.substring(indx1 + 2);
			if ((subsPrev.indexOf("[") != -1) && (subsAfter.indexOf("]]") != -1)) {
				transformedPatternStr = transformedPatternStr.replaceAll("\\-\\[", 
						                                                    "&&[^");	
			}
		}
		
		return transformedPatternStr;
	}
	
	public static Matcher regex(String pattern, String flags, String src) {
		Matcher matcher = compileAndExecute(pattern, flags, src);
		return matcher;
	}
	
	public static boolean isFlagStrValid(String flags) {
       boolean flagStrValid = true;
       
       if (flags.length() > 0) {
    	  for (int idx = 0; idx < flags.length(); idx++) {
    		 if (VALID_REGEX_FLAG_CHARS.indexOf(flags.charAt(idx)) == -1) {
    			flagStrValid = false;
    			break;
    		 }
    	  }
       }
       
       return flagStrValid; 
	}
	
	public static Matcher compileAndExecute(String regexStr, String regexFlags, 
	                                                                  String inputStr) {
		int flag = Pattern.UNIX_LINES;
		
		if (regexFlags != null) {			
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
		}
		
		Pattern pattern = Pattern.compile(regexStr, flag);
		
		return pattern.matcher(inputStr);
	}

}
