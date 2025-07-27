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
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.res.XPATHErrorResources;
import org.json.JSONException;

import xml.xpath31.processor.types.XSBoolean;

/**
 * Implementation of an XPath 3.1 function, fn:parse-json.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncParseJson extends JsonFunction {

	private static final long serialVersionUID = 8542161858023543436L;
	
	private static final List<String> OPTION_SUPPORTED_LIST = new ArrayList<String>();
	
	/**
     * The number of arguments passed to the fn:json-doc function call.
     */
    private int fNumOfArgs = 0;
    
    /**
     * Class constructor.
     */
    public FuncParseJson() {
       OPTION_SUPPORTED_LIST.add(XSLJsonConstants.LIBERAL);
       OPTION_SUPPORTED_LIST.add(XSLJsonConstants.DUPLICATES);
       
       m_defined_arity = new Short[] { 1, 2 };
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
            		                                                                                    + "have either one or two arguments.", srcLocator);
        }
        
        boolean optionIsLiberalVal = false;
        
        String optionDuplicatesValStr = XSLJsonConstants.DUPLICATES_USE_FIRST;  
        
        if (fNumOfArgs == 1) {
           result = getJsonXdmValue(arg0, xctxt, optionIsLiberalVal, optionDuplicatesValStr);           
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
        	  
        	  while (optionsIter.hasNext()) {
        		 Entry<XObject,XObject> mapEntry = optionsIter.next();
        		 String keyStr = XslTransformEvaluationHelper.getStrVal(mapEntry.getKey());
        		 XObject optionValue = mapEntry.getValue();
        		 if (!OPTION_SUPPORTED_LIST.contains(keyStr)) {
        			throw new javax.xml.transform.TransformerException("FOUT1190 : An option '" + keyStr + "' used with function call "
        					                                                                    + "fn:parse-json, is not supported. This implementation "
        					                                                                    + "supports, fn:parse-json options 'liberal', 'duplicates'.", srcLocator); 
        		 }
        		 
        		 if (XSLJsonConstants.LIBERAL.equals(keyStr)) {
        			if ((optionValue instanceof XSBoolean) || (optionValue instanceof XBooleanStatic)) {
        			   optionIsLiberalVal = optionValue.bool();        			   
        			}
        			else {
        			   throw new javax.xml.transform.TransformerException("FOUT1190 : The function fn:parse-json option "
					        			   		                                               + "\"liberal\"'s value is not of type xs:boolean.", srcLocator);
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
        		 }
        	  }
        	  
        	  result = getJsonXdmValue(arg0, xctxt, optionIsLiberalVal, optionDuplicatesValStr);
           }
           else {
        	  throw new javax.xml.transform.TransformerException("FOUT1190 : The second argument provided for function call "
        	  		                                                                           + "fn:parse-json to represent 'options' is not a map.", srcLocator);  
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
     * Method definition to JSON parse an input string, and return a corresponding 
     * xdm value.
     * 
     * @param xpath                              Represents first argument provided to function fn:parse-json.
     * @param xctxt                              XPath context object
     * @param optionIsLiberal                    Function call fn:parse-json option liberal's value.
     * @param optionDuplicatesValStr             Function call fn:parse-json option duplicates's value.
     * 
     * @return                                   An xdm value of type XPathMap, XPathArray, XSDouble,
     *                                           XSBoolean, ResultSequence, XSString (these are the possible
     *                                           XPath values to which a JSON value can translate to). 
     *  
     * @throws javax.xml.transform.TransformerException
     */
	private XObject getJsonXdmValue(Expression xpath, XPathContext xctxt, boolean optionIsLiberal, 
			                                                              String optionDuplicatesValStr) 
			                                                            		      throws javax.xml.transform.TransformerException {
		
		XObject result = null;
		
		SourceLocator srcLocator = xctxt.getSAXLocator();
		
		XObject arg0Value = xpath.execute(xctxt);
		String arg0StrValue = XslTransformEvaluationHelper.getStrVal(arg0Value);
		
		try {			
			result = getJsonXdmValueFromStr(arg0StrValue, optionIsLiberal, optionDuplicatesValStr);
		}
		catch (JSONException ex) {			
		    throw new javax.xml.transform.TransformerException("FOUT1190 : The function call fn:parse-json's first argument is not a "
		    		                                                                 + "correct JSON lexical string. The XPath JSON parse options used were : "
		    		                                                                 + "liberal: " + optionIsLiberal + ", duplicates: " + 
		    		                                                                 optionDuplicatesValStr + ". The JSON parser emitted following error message: " + 
		    		                                                                 ex.getMessage() + ".", srcLocator);
		}
		
		return result;
	}

}
