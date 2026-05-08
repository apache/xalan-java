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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class definition, specifies few utility methods for 
 * XSL regex processing.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage general
 */
public class RegexUtil {
	
	/**
	 * Method definition, to get a list of regex match boundaries
	 * for the supplied regex and the string value to be matched 
	 * with regex.
	 * 
	 * @param regex                    The supplied regex string
	 * @param str                      The supplied string value to
	 *                                 be matched with regex.
	 * @return                         A list of regex match boundaries
	 */
	public static List<RegexMatchInfo> getRegexMatchInfoList(String regex, String str) {
		
		List<RegexMatchInfo> regexMatchInfoList = new ArrayList<RegexMatchInfo>();

		Pattern pattern = Pattern.compile(regex);
		Matcher regexMatcher = pattern.matcher(str);   	 

		while (regexMatcher.find()) {
			int idx1 = regexMatcher.start();
			int idx2 = regexMatcher.end();
			RegexMatchInfo regexMatchInfo = new RegexMatchInfo();
			regexMatchInfo.setStartIdx(idx1);
			regexMatchInfo.setEndIdx(idx2);
			regexMatchInfoList.add(regexMatchInfo);
		}

		regexMatcher.reset();

		return regexMatchInfoList; 
	}
	
	/**
	 * Method definition, to get a list of regex match boundaries
	 * for the supplied regex matcher object.
	 * 
	 * @param regexMatcher             The supplied regex matcher object
	 * @return                         A list of regex match boundaries
	 */
	public static List<RegexMatchInfo> getRegexMatchInfoList(org.apache.xpath.regex.Matcher regexMatcher) {
		
		List<RegexMatchInfo> regexMatchInfoList = new ArrayList<RegexMatchInfo>();
		
		while (regexMatcher.find()) {
			int idx1 = regexMatcher.start();
			int idx2 = regexMatcher.end();
			RegexMatchInfo regexMatchInfo = new RegexMatchInfo();
			regexMatchInfo.setStartIdx(idx1);
			regexMatchInfo.setEndIdx(idx2);
			regexMatchInfoList.add(regexMatchInfo);
		}

		regexMatcher.reset();
		
		return regexMatchInfoList;
	}

}
