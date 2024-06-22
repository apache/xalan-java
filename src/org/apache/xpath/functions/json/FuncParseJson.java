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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionMultiArgs;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.res.XPATHErrorResources;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xml.xpath31.processor.types.XSBoolean;

/**
 * Implementation of an XPath 3.1 function, fn:parse-json.
 * 
 * This function accepts as an argument a json document 
 * string, and returns an XDM 'map', or an 'array'.
 * 
 * (It's also useful to refer, to following json RFC : 
 * https://datatracker.ietf.org/doc/html/rfc7159)
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncParseJson extends FunctionMultiArgs {

	private static final long serialVersionUID = 8542161858023543436L;
	
	private static final List<String> OPTIONS_SUPPORTED_LIST = new ArrayList<String>();
	
	/**
     * The number of arguments passed to the fn:parse-json function 
     * call.
     */
    private int fNumOfArgs = 0;
    
    /**
     * Class constructor.
     */
    public FuncParseJson() {
       OPTIONS_SUPPORTED_LIST.add(XSLJsonConstants.LIBERAL);
       OPTIONS_SUPPORTED_LIST.add(XSLJsonConstants.DUPLICATES);
    }
    
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
        
        if ((arg0 == null) && (arg1 == null)) {
           throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath function fn:parse-json needs to have "
            		                                                      + "at-least one argument.", srcLocator);
        }
        else if (m_arg2 != null) {
           throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath function fn:parse-json can "
            		                                                      + "have either 1 or two arguments.", srcLocator);
        }
        
        if (fNumOfArgs == 1) {
           result = parseJson(arg0, xctxt);           
        }
        else {
           // fn:parse-json function was called, with two arguments
           XObject arg1Value = null;
           if (arg1 instanceof Variable) {
        	  arg1Value = ((Variable)arg1).execute(xctxt);
           }
           else {
        	  arg1Value = arg1.execute(xctxt); 
           }
           
           if (arg1Value instanceof XPathMap) {
        	  XPathMap optionsMap = (XPathMap)arg1Value;
        	  
        	  Map<XObject, XObject> optionsNativeMap = optionsMap.getNativeMap();
        	  Set<Entry<XObject,XObject>> optionEntries = optionsNativeMap.entrySet();
        	  Iterator<Entry<XObject,XObject>> optionsIter = optionEntries.iterator();
        	  String optionDuplicatesValStr = null;
        	  while (optionsIter.hasNext()) {
        		 Entry<XObject,XObject> mapEntry = optionsIter.next();
        		 String keyStr = XslTransformEvaluationHelper.getStrVal(mapEntry.getKey());
        		 XObject optionValue = mapEntry.getValue();
        		 if (!OPTIONS_SUPPORTED_LIST.contains(keyStr)) {
        			throw new javax.xml.transform.TransformerException("FOUT1190 : An option '" + keyStr + "' used during "
        					                                       + "function call fn:parse-json, is not supported. "
        					                                       + "This implementation supports, only the options 'liberal' & 'duplicates' "
        					                                       + "for the function fn:parse-json.", srcLocator); 
        		 }
        		 else if (XSLJsonConstants.LIBERAL.equals(keyStr)) {
        			if ((optionValue instanceof XSBoolean) || (optionValue instanceof XBooleanStatic)) {
        			   boolean liberalVal = optionValue.bool();
        			   if (liberalVal) {
        				  throw new javax.xml.transform.TransformerException("FOUT1190 : An implementation for function fn:parse-json, doesn't "
        				  		                                         + "support an option liberal=true. An input JSON string to function "
        				  		                                         + "fn:parse-json, should strictly conform to the JSON syntax rules, "
        				  		                                         + "as specified by RFC 7159.", srcLocator);  
        			   }
        			}
        			else {
        			   throw new javax.xml.transform.TransformerException("FOUT1190 : The function fn:parse-json option "
        			   		                                           + "\"liberal\"'s value is not of type xs:boolean.", 
        			   		                                           srcLocator);
        			}
        		 }
        		 else if (XSLJsonConstants.DUPLICATES.equals(keyStr)) {
        			optionDuplicatesValStr = XslTransformEvaluationHelper.getStrVal(optionValue);
        			if (!(XSLJsonConstants.DUPLICATES_REJECT.equals(optionDuplicatesValStr) || 
        				  XSLJsonConstants.DUPLICATES_USE_FIRST.equals(optionDuplicatesValStr) || 
        				  XSLJsonConstants.DUPLICATES_USE_LAST.equals(optionDuplicatesValStr))) {
        				throw new javax.xml.transform.TransformerException("FOUT1190 : The function fn:parse-json option "
                                                                        + "\"duplicates\"'s value is not one of following : 'reject', 'use-first', "
                                                                        + "'use-last'.", srcLocator);
        			}
        			else if (XSLJsonConstants.DUPLICATES_USE_FIRST.equals(optionDuplicatesValStr) || 
        					XSLJsonConstants.DUPLICATES_USE_LAST.equals(optionDuplicatesValStr)) {
        				throw new javax.xml.transform.TransformerException("FOUT1190 : The function fn:parse-json option \"duplicates\"'s value "
        						                                        + "'use-first', or 'use-last' is not supported. There should be no duplicate "
        						                                        + "keys within an input JSON document.", srcLocator);
        			}
        		 }
        	  }
        	  
        	  result = parseJson(arg0, xctxt);
           }
           else {
        	  throw new javax.xml.transform.TransformerException("FOUT1190 : The 2nd argument passed to function call "
        	  		                                                              + "fn:parse-json is not a map.", srcLocator);  
           }
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
          fNumOfArgs = argNum;   
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

    /**
     * This method parses a string value, to XDM object representations for 
     * JSON documents.
     * 
     * @param xpath      1st argument provided to function fn:parse-json
     * @param xctxt      XPath context object
     * @return           an xdm object of type XPathMap, or XPathArray
     *  
     * @throws javax.xml.transform.TransformerException
     */
	private XObject parseJson(Expression xpath, XPathContext xctxt) throws javax.xml.transform.TransformerException {
		
		XObject result;
		
		SourceLocator srcLocator = xctxt.getSAXLocator();
		
		XObject arg0Value = xpath.execute(xctxt);
		String arg0StrValue = XslTransformEvaluationHelper.getStrVal(arg0Value);
		arg0StrValue = arg0StrValue.trim();
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
						                                                 + "fn:parse-json is not a correct json lexical string. A json "
						                                                 + "string can begin only with '{' or '[' characters.", srcLocator); 
			}
		}
		catch (JSONException ex) {
			throw new javax.xml.transform.TransformerException("FOUT1190 : The 1st argument provided with function call "
					                                                 + "fn:parse-json is not a correct json lexical string. The JSON "
					                                                 + "parser produced following error : " + ex.getMessage() + ".", 
					                                                 srcLocator); 
		}
		
		result = getXdmMapOrArrayFromNativeJson(jsonObj);
		
		return result;
	}

}
