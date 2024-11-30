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
 * by XPath 3.1 implementation for functions fn:parse-json, 
 * fn:json-doc.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class JsonFunction extends FunctionMultiArgs {
	
	private static final long serialVersionUID = 1094611901554413886L;
	
	/**
	 * Given an input string, the method does JSON parse of the string,
	 * and returns an xdm value corresponding to JSON string.
	 * 
	 * @param jsonStr             an input JSON string
	 * @return                    an xdm object of type XPathMap, XPathArray, XSDouble,
	 *                            XSBoolean, ResultSequence, XSString.  
	 * @throws JSONException
	 */
	protected XObject getJsonXdmValueFromStr(String jsonStr) throws JSONException {
		
		XObject result = null;
		
		if ((jsonStr.trim()).charAt(0) == '{') {
			JSONObject jsonObj = new JSONObject(jsonStr);
			result = getXdmMapOrArrayFromParsedJson(jsonObj);
		}
		else if ((jsonStr.trim()).charAt(0) == '[') {
			JSONArray jsonArr = new JSONArray(jsonStr);
			result = getXdmMapOrArrayFromParsedJson(jsonArr);
		}
		else {
			try {
				Double dbl = Double.valueOf(jsonStr);
				result = new XSDouble(dbl);
			}
			catch (NumberFormatException ex) {
				// NO OP	
			}

			if (result == null) {
				if ("false".equals(jsonStr) || "0".equals(jsonStr) 
						                 || "true".equals(jsonStr) || "1".equals(jsonStr)) {
					result = new XSBoolean(Boolean.valueOf(jsonStr));
				}
			}

			if (result == null) {
				if ("null".equals(jsonStr)) {
					// return an, empty sequence
					result = new ResultSequence();
				}
			}

			if (result == null) {
				if (jsonStr.startsWith("\"") && jsonStr.endsWith("\"")) {
					jsonStr = jsonStr.substring(1, jsonStr.length()-1);
					if (jsonStr.length() == 0) {
						jsonStr = ""; 
					}
					result = new XSString(jsonStr);
				}
				else if (jsonStr.startsWith("'") && jsonStr.endsWith("'")) {
					jsonStr = jsonStr.substring(1, jsonStr.length()-1);
					if (jsonStr.length() == 0) {
						jsonStr = ""; 
					}
					result = new XSString(jsonStr); 
				}
				else {
					if (jsonStr.length() == 0) {
						jsonStr = ""; 
					}
					result = new XSString(jsonStr);
				}
			}
		}
		
		return result;
	}

	/**
     * Given an object of type org.json.JSONObject or org.json.JSONArray,
     * construct an xdm 'map', or an 'array' from these objects.   
     * 
     * @param jsonObj   an input argument of type org.json.JSONObject, or 
     *                  org.json.JSONArray.  
     * @return          an xdm object of type XPathMap, or XPathArray 
     */
    private XObject getXdmMapOrArrayFromParsedJson(Object jsonObj) {
    	
    	XObject result = null;
    	
    	if (jsonObj instanceof JSONObject) {
    		result = new XPathMap();
	    	Iterator<String> jsonKeys = ((JSONObject)jsonObj).keys();
	    	
	    	while (jsonKeys.hasNext()) {
	      	   String key = jsonKeys.next();
	      	   Object value = ((JSONObject)jsonObj).get(key);        	  
	      	   if (value instanceof String) {
	      		 ((XPathMap)result).put(new XSString(key), new XSString(String.valueOf(value)));  
	      	   }
	      	   else if (value instanceof Number) {
	      		  double doubleVal = ((Number)value).doubleValue();
	      		  ((XPathMap)result).put(new XSString(key), new XSDecimal(String.valueOf(doubleVal)));
	      	   }
	      	   else if (value instanceof Boolean) {
	      		  ((XPathMap)result).put(new XSString(key), new XSBoolean(new Boolean(value.toString()))); 
	      	   }	      	   
	      	   else if (value instanceof JSONObject) {
	      		  // Recursive call to this function
	      		  XObject value1 = getXdmMapOrArrayFromParsedJson(value);
	      		  ((XPathMap)result).put(new XSString(key), value1);
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
	      			 else if ((arrItem instanceof JSONObject) || (arrItem instanceof JSONArray)) {
	      			   // Recursive call to this function
	      				xObj = getXdmMapOrArrayFromParsedJson(arrItem);
	      			 }
	      			 
	      			xpathArr.add(xObj);
	      		  }
	      		  
	      		  ((XPathMap)result).put(new XSString(key), xpathArr);
	      	   }
	        }
    	}
    	else if (jsonObj instanceof JSONArray) {
    		XPathArray xpathArr = new XPathArray();
    		
    		JSONArray jsonArr = (JSONArray)jsonObj;	      		  
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
    		   else if ((arrItem instanceof JSONObject) || (arrItem instanceof JSONArray)) {
    			  // Recursive call to this function
    		      xObj = getXdmMapOrArrayFromParsedJson(arrItem);
    		   }
    			 
    		   xpathArr.add(xObj);
    		}
    		  
    		result = xpathArr;
    	}
    	
    	return result;
    }

}
