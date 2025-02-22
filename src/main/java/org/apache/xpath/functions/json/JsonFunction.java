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
package org.apache.xpath.functions.json;

import java.util.Iterator;

import org.apache.xpath.functions.FunctionMultiArgs;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSString;

/**
 * A class providing common methods and code, that're used
 * by XPath 3.1 implementation for functions fn:parse-json 
 * and fn:json-doc.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class JsonFunction extends FunctionMultiArgs {
	
	private static final long serialVersionUID = 1094611901554413886L;
	
	/**
	 * Given an input string as argument to this function, the function does 
	 * JSON parse of the string, and returns an XDM map or array object instance 
	 * corresponding to the input JSON string.
	 * 
	 * @param strValue            an input string value
	 * @return                    an XDM object instance of one of following types : XPathMap, 
	 *                            XPathArray, XSDouble, XSBoolean, ResultSequence, XSString.  
	 * @throws JSONException
	 */
	protected XObject getJsonXdmValueFromStr(String strValue) throws JSONException {
		
		XObject result = null;
		
		if ((strValue.trim()).charAt(0) == '{') {
			JSONObject jsonObj = new JSONObject(strValue);
			result = getXdmMapFromJSONObject(jsonObj);
		}
		else if ((strValue.trim()).charAt(0) == '[') {
			JSONArray jsonArr = new JSONArray(strValue);
			result = getXdmArrayFromJSONArray(jsonArr);
		}
		else {
			try {
				Double dbl = Double.valueOf(strValue);
				result = new XSDouble(dbl);
			}
			catch (NumberFormatException ex) {
				// NO OP	
			}

			if (result == null) {
				if ("false".equals(strValue) || "0".equals(strValue) 
						                 || "true".equals(strValue) || "1".equals(strValue)) {
					result = new XSBoolean(Boolean.valueOf(strValue));
				}
			}

			if (result == null) {
				if ("null".equals(strValue)) {
					// return an, empty sequence
					result = new ResultSequence();
				}
			}

			if (result == null) {
				if (strValue.startsWith("\"") && strValue.endsWith("\"")) {
					strValue = strValue.substring(1, strValue.length()-1);
					if (strValue.length() == 0) {
						strValue = ""; 
					}
					result = new XSString(strValue);
				}
				else if (strValue.startsWith("'") && strValue.endsWith("'")) {
					strValue = strValue.substring(1, strValue.length()-1);
					if (strValue.length() == 0) {
						strValue = ""; 
					}
					result = new XSString(strValue); 
				}
				else {
					if (strValue.length() == 0) {
						strValue = ""; 
					}
					result = new XSString(strValue);
				}
			}
		}
		
		return result;
	}

	/**
	 * Given a JSONObject object instance as argument to this function, the 
	 * function returns an XDM map object. 
	 * 
	 * @param jsonObj     a JSONObject object instance
	 * @return            an XDM map object
	 */
	private XPathMap getXdmMapFromJSONObject(JSONObject jsonObj) {
    	
		XPathMap xpathMapResult = new XPathMap();

		Iterator<String> jsonKeys = jsonObj.keys();

		while (jsonKeys.hasNext()) {
			String key = jsonKeys.next();
			Object value = jsonObj.get(key);

			if (value instanceof String) {
				xpathMapResult.put(new XSString(key), new XSString(String.valueOf(value)));  
			}
			else if (value instanceof Number) {
				double doubleVal = ((Number)value).doubleValue();
				xpathMapResult.put(new XSString(key), new XSDecimal(String.valueOf(doubleVal)));
			}
			else if (value instanceof Boolean) {
				xpathMapResult.put(new XSString(key), new XSBoolean(new Boolean(value.toString()))); 
			}	      	   
			else if (value instanceof JSONObject) {
				XObject value1 = getXdmMapFromJSONObject((JSONObject)value);
				xpathMapResult.put(new XSString(key), value1);
			}
			else if (value instanceof JSONArray) {
				XPathArray xpathArr = new XPathArray();

				JSONArray jsonArr = (JSONArray)value;	      		  
				int arrLen = jsonArr.length();
				
				for (int idx = 0; idx < arrLen; idx++) {
					Object arrItem = jsonArr.get(idx);
					XObject xObj = null;
					if (arrItem instanceof String) {
						xObj = new XSString(arrItem.toString());	 
					}
					else if (arrItem instanceof Number) {
						double doubleVal = ((Number)arrItem).doubleValue();
						xObj = new XSDecimal(String.valueOf(doubleVal)); 
					}
					else if (arrItem instanceof Boolean) {
						xObj = new XSBoolean(new Boolean(arrItem.toString())); 
					}
					else if (arrItem instanceof JSONObject) {
						xObj = getXdmMapFromJSONObject((JSONObject)arrItem);
					}
					else if (arrItem instanceof JSONArray) {
						xObj = getXdmArrayFromJSONArray((JSONArray)arrItem);
					}

					xpathArr.add(xObj);
				}

				xpathMapResult.put(new XSString(key), xpathArr);
			}
		}

		return xpathMapResult;
    }
    
	/**
	 * Given a JSONArray object instance as argument to this function, the 
	 * function returns an XDM array object. 
	 * 
	 * @param jsonArr     a JSONArray object instance
	 * @return            an XDM array object
	 */
    private XPathArray getXdmArrayFromJSONArray(JSONArray jsonArr) {
    	
    	XPathArray xpathArrResult = new XPathArray();

    	int arrLen = jsonArr.length();
    	
    	for (int idx = 0; idx < arrLen; idx++) {
    		Object arrItem = jsonArr.get(idx);
    		XObject xObj = null;

    		if (arrItem instanceof String) {
    			xObj = new XSString(arrItem.toString());	 
    		}
    		else if (arrItem instanceof Number) {
    			double doubleVal = ((Number)arrItem).doubleValue();
    			xObj = new XSDecimal(String.valueOf(doubleVal)); 
    		}
    		else if (arrItem instanceof Boolean) {
    			xObj = new XSBoolean(new Boolean(arrItem.toString())); 
    		}
    		else if (arrItem instanceof JSONObject) {
    			xObj = getXdmMapFromJSONObject((JSONObject)arrItem);  
    		}
    		else if (arrItem instanceof JSONArray) {
    			xObj = getXdmArrayFromJSONArray((JSONArray)arrItem);
    		}

    		xpathArrResult.add(xObj);
    	}

    	return xpathArrResult;
    }

}
