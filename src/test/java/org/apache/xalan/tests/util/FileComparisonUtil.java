/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xalan.tests.util;

import org.json.JSONObject;

/**
 * A class definition, having utility methods to compare various 
 * types of file contents.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FileComparisonUtil {
	
	/**
	 * Class constructor.
	 */
	public FileComparisonUtil() {
	   // NO OP	
	}
	
	/**
	 * Method definition to find whether, contents of two json files 
	 * represented as string values, are equal.
	 * 
	 * @param jsonStr1			first json string
	 * @param jsonStr2			second json string	
	 * @return					boolean value indicating the result of
	 * 							file comparison.
	 */
	public boolean isJsonFileContentsEqual(String jsonStr1, String jsonStr2) {
		boolean result = false;
		
		JSONObject jsonObj1 = new JSONObject(jsonStr1);
		JSONObject jsonObj2 = new JSONObject(jsonStr2);
		
		result = (jsonObj1.toString()).equals(jsonObj2.toString());
		
		return result;
	}

}
