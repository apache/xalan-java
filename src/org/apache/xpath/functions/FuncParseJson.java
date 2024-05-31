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
package org.apache.xpath.functions;

import java.util.Iterator;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.res.XPATHErrorResources;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSString;

/**
 * Implementation of the fn:parse-json function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncParseJson extends FunctionMultiArgs {

	private static final long serialVersionUID = 8542161858023543436L;
	
	/**
     * The number of arguments passed to the fn:parse-json function 
     * call.
     */
    private int numOfArgs = 0;
    
    /**
     * Implementation of the function. The function must return a valid object.
     * 
     * @param xctxt The current execution context.
     * @return A valid XObject.
     *
     * @throws javax.xml.transform.TransformerException
     */
    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {
        
        XObject result = null;
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        Expression arg0 = m_arg0;        
        Expression arg1 = m_arg1;
        
        if (numOfArgs == 1) {
           // One argument version of the function was called
           XObject arg0Value = arg0.execute(xctxt);
           String arg0StrValue = XslTransformEvaluationHelper.getStrVal(arg0Value);
           Object jsonObj = null;
           try {
        	  if (arg0StrValue.charAt(0) == '{') {
        	     jsonObj = new JSONObject(arg0StrValue);
        	  }
        	  else if (arg0StrValue.charAt(0) == '[') {
        		 jsonObj = new JSONArray(arg0StrValue);  
        	  }
        	  else {
        		 throw new javax.xml.transform.TransformerException("FOUT1190 : The 1st argument provided with function call "
                            												+ "fn:parse-json is not a valid json string. A json string can begin "
                            												+ "only with '{' or '[' characters.", srcLocator); 
        	  }
           }
           catch (JSONException ex) {
        	  throw new javax.xml.transform.TransformerException("FOUT1190 : The 1st argument provided with function call "
        	  		                                                        + "fn:parse-json is not a valid json string.", srcLocator); 
           }
                      
           result = getXdmValueFromNativeJson(jsonObj);           
        }
    
        
        return result;
    }
    
    /**
     * Given a native json object, convert that into 'xdm map' or 
     * an 'xdm array'.
     */
    private XObject getXdmValueFromNativeJson(Object jsonObj) {
    	
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
	      		  XObject value1 = getXdmValueFromNativeJson(value);
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
	      				xObj = getXdmValueFromNativeJson(arrItem);
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
    		      xObj = getXdmValueFromNativeJson(arrItem);
    		   }
    			 
    		   xpathArr.add(xObj);
    		}
    		  
    		result = xpathArr;
    	}
    	
    	return result;
    }
    
    /**
     * Check that the number of arguments passed to this function is correct.
     *
     * @param argNum The number of arguments that is being passed to the function.
     *
     * @throws WrongNumberArgsException
     */
    public void checkNumberArgs(int argNum) throws WrongNumberArgsException
    {
       if (!((argNum == 1) || (argNum == 2))) {
          reportWrongNumberArgs();
       }
       else {
          numOfArgs = argNum;   
       }
    }
    
    /**
     * Constructs and throws a WrongNumberArgException with the appropriate
     * message for this function object.
     *
     * @throws WrongNumberArgsException
     */
    protected void reportWrongNumberArgs() throws WrongNumberArgsException {
        throw new WrongNumberArgsException(XSLMessages.createXPATHMessage(
                                                                     XPATHErrorResources.ER_ONE_OR_TWO, null));
    }

}
