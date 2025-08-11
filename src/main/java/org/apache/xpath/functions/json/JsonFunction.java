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
import org.json.JSONParserConfiguration;

import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSString;

/**
 * A class providing common implementation code, that is used by Xalan-J's
 * implementation of XPath 3.1 functions fn:parse-json & fn:json-doc.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class JsonFunction extends FunctionMultiArgs {
	
	private static final long serialVersionUID = 1094611901554413886L;
	
	/**
	 * Given a supplied string value as an argument to this function, the function 
	 * does JSON parse of the string, and returns an equivalent xdm value.
	 * 
	 * @param jsonStrVal                       Supplied JSON document string value
	 * @param optionIsLiberal                  Function call JSON option liberal's value
	 * @param optionDuplicatesValStr           Function call JSON option duplicates's value
	 *  
	 * @return                                 An xdm value of type XPathMap, XPathArray, XSDouble, XSBoolean, 
     *                                         ResultSequence (representing json null values), XSString. These 
     *                                         are the possible xdm values to which a JSON value can translate 
     *                                         to. 
	 * @throws JSONException
	 */
	protected XObject getJsonXdmValueFromStr(String jsonStrVal, boolean optionIsLiberal, String optionDuplicatesValStr) throws JSONException {
				
		XObject result = null;
		
		JSONParserConfiguration jsonParserConf = getJsonParserConfiguration(optionIsLiberal, optionDuplicatesValStr);
		
		if ((jsonStrVal.trim()).charAt(0) == '{') {
			JSONObject jsonObj = new JSONObject(jsonStrVal, jsonParserConf);
			result = getXdmMapFromJSONObject(jsonObj);
		}
		else if ((jsonStrVal.trim()).charAt(0) == '[') {
			JSONArray jsonArr = new JSONArray(jsonStrVal, jsonParserConf);
			result = getXdmArrayFromJSONArray(jsonArr);
		}
		else {
			try {
				Double dbl = Double.valueOf(jsonStrVal);
				result = new XSDouble(dbl);
			}
			catch (NumberFormatException ex) {
				// NO OP	
			}

			if (result == null) {
				if ("false".equals(jsonStrVal) || "0".equals(jsonStrVal) 
						                                         || "true".equals(jsonStrVal) 
						                                         || "1".equals(jsonStrVal)) {
					result = new XSBoolean(Boolean.valueOf(jsonStrVal));
				}
			}

			if (result == null) {
				if ("null".equals(jsonStrVal)) {
					// Return an, empty sequence
					result = new ResultSequence();
				}
			}

			if (result == null) {
				if (jsonStrVal.startsWith("\"") && jsonStrVal.endsWith("\"")) {
					jsonStrVal = jsonStrVal.substring(1, jsonStrVal.length()-1);
					if (jsonStrVal.length() == 0) {
						jsonStrVal = ""; 
					}
					result = new XSString(jsonStrVal);
				}
				else if (jsonStrVal.startsWith("'") && jsonStrVal.endsWith("'")) {
					jsonStrVal = jsonStrVal.substring(1, jsonStrVal.length()-1);
					if (jsonStrVal.length() == 0) {
						jsonStrVal = ""; 
					}
					result = new XSString(jsonStrVal); 
				}
				else {
					if (jsonStrVal.length() == 0) {
						jsonStrVal = ""; 
					}
					result = new XSString(jsonStrVal);
				}
			}
		}
		
		return result;
	}

	/**
	 * Given a JSONObject object instance as argument to this function, the 
	 * function returns an xdm map object. 
	 * 
	 * @param jsonObj                 A JSONObject object instance
	 * @return                        An xdm map object
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
	 * function returns an xdm array object. 
	 * 
	 * @param jsonArr                A JSONArray object instance
	 * @return                       An xdm array object
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
    
    /**
	 * Method definition, to get org.json library's JSONParserConfiguration object for 
	 * the supplied XPath 3.1 option 'liberal' & 'duplicates' values to parse JSON 
	 * string values.
	 * 
	 * @param optionIsLiberal                        Value of JSON parse 'liberal' option
	 * @param optionDuplicatesValStr                 Value of JSON parse 'duplicates' option
	 * @return                                       org.json.JSONParserConfiguration object
	 */
	public JSONParserConfiguration getJsonParserConfiguration(boolean optionIsLiberal, String optionDuplicatesValStr) {
		
		JSONParserConfiguration jsonParserConf = new JSONParserConfiguration();
		
		if (optionIsLiberal) {
			jsonParserConf = jsonParserConf.withStrictMode(false);
		}

		if (XSLJsonConstants.DUPLICATES_REJECT.equals(optionDuplicatesValStr)) {
			jsonParserConf = jsonParserConf.withOverwriteDuplicateKey(false);
			jsonParserConf = jsonParserConf.useFirstDuplicateKey(false); 
			jsonParserConf = jsonParserConf.retainAllDuplicateKeys(false);
		}
		else if (XSLJsonConstants.DUPLICATES_USE_FIRST.equals(optionDuplicatesValStr)) {
			/**
			 * org.json library classes JSONObject & JSONParserConfiguration are modified
			 * slightly (and, included within Xalan-J's XPath 3.1 implementation code) to 
			 * implement this XPath feature.
			 */
			jsonParserConf = jsonParserConf.useFirstDuplicateKey(true);
			jsonParserConf = jsonParserConf.withOverwriteDuplicateKey(false);
			jsonParserConf = jsonParserConf.retainAllDuplicateKeys(false);
		}
		else if (XSLJsonConstants.DUPLICATES_USE_LAST.equals(optionDuplicatesValStr)) {
			jsonParserConf = jsonParserConf.withOverwriteDuplicateKey(true);
			jsonParserConf = jsonParserConf.useFirstDuplicateKey(false);
			jsonParserConf = jsonParserConf.retainAllDuplicateKeys(false);			 	
		}
		else if (XSLJsonConstants.DUPLICATES_RETAIN.equals(optionDuplicatesValStr)) {
			jsonParserConf = jsonParserConf.retainAllDuplicateKeys(true);
			jsonParserConf = jsonParserConf.withOverwriteDuplicateKey(false);
			jsonParserConf = jsonParserConf.useFirstDuplicateKey(false);						 	
		}
		
		return jsonParserConf;
	}

}
