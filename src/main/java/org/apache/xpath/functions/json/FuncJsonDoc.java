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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.xslt.util.StringUtil;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.res.XPATHErrorResources;
import org.json.JSONException;

import xml.xpath31.processor.types.XSBoolean;

/**
 * Implementation of an XPath 3.1 function fn:json-doc.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncJsonDoc extends JsonFunction {
	
	private static final long serialVersionUID = 4521998080023197937L;

	private static final List<String> OPTION_SUPPORTED_LIST = new ArrayList<String>();
	
	/**
     * The number of arguments passed to the fn:json-doc function call.
     */
    private int fNumOfArgs = 0;
    
    /**
     * Class constructor.
     */
    public FuncJsonDoc() {
       OPTION_SUPPORTED_LIST.add(XSLJsonConstants.LIBERAL);
       OPTION_SUPPORTED_LIST.add(XSLJsonConstants.DUPLICATES);
       
       m_arity = new Short[] { 1, 2 };
    }
    
    /**
	 * Evaluate the function. The function must return a valid object.
	 * 
	 * @param xctxt                        An XPath context object
	 * @return                             A valid XObject
	 *
	 * @throws javax.xml.transform.TransformerException
	 */
    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {
        
        XObject result = null;
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        if ((m_arg0 == null) && (m_arg1 == null)) {
           throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath 3.1 function 'json-doc' may have either one or two arguments.", srcLocator);
        }
        else if (m_arg2 != null) {
           throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath 3.1 function 'json-doc' may have either one or two arguments.", srcLocator);
        }
        
        boolean optionIsLiberalVal = false;
        
        String optionDuplicatesValStr = XSLJsonConstants.DUPLICATES_USE_FIRST; 
        
        if (fNumOfArgs == 1) {
           result = getFnJsonDocResult(m_arg0, xctxt, optionIsLiberalVal, optionDuplicatesValStr);           
        }
        else {
           // An XPath 3.1 function fn:json-doc, has been called 
           // with two arguments.
        	
           XObject arg1Value = getFunctionArgEffectiveValue(m_arg1, xctxt);
           
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
         			throw new javax.xml.transform.TransformerException("FOUT1190 : An XPath 3.1 function call 'json-doc' option '" + keyStr + "' is not "
         					                                                                                                       + "supported. This implementation supports "
         					                                                                                                       + "function 'json-doc' options 'liberal', 'duplicates'.", srcLocator);
         		 }
         		 
         		 if (XSLJsonConstants.LIBERAL.equals(keyStr)) {
         			if ((optionValue instanceof XSBoolean) || (optionValue instanceof XBooleanStatic)) {
         			   optionIsLiberalVal = optionValue.bool();        			   
         			}
         			else {
         			   throw new javax.xml.transform.TransformerException("FOUT1190 : An XPath 3.1 function call 'json-doc' option \"liberal\" value is not of an XML schema type boolean.", srcLocator);
         			}
         		 }
         		 else if (XSLJsonConstants.DUPLICATES.equals(keyStr)) {
         			optionDuplicatesValStr = XslTransformEvaluationHelper.getStrVal(optionValue);
         			
         			if (!(XSLJsonConstants.DUPLICATES_REJECT.equals(optionDuplicatesValStr) ||
         				  XSLJsonConstants.DUPLICATES_USE_FIRST.equals(optionDuplicatesValStr) ||
         				  XSLJsonConstants.DUPLICATES_USE_LAST.equals(optionDuplicatesValStr))) {
         				throw new javax.xml.transform.TransformerException("FOUT1190 : An XPath 3.1 function call 'json-doc' option \"duplicates\" value "
         						                                                                                                                  + "is not one of following : 'reject', "
         						                                                                                                                  + "'use-first', 'use-last'.", srcLocator);
         			}        			
         		 }
        	  }
        	  
        	  result = getFnJsonDocResult(m_arg0, xctxt, optionIsLiberalVal, optionDuplicatesValStr);
           }
           else {
        	   throw new javax.xml.transform.TransformerException("FOUT1190 : An XPath 3.1 function call 'json-doc' 'options' argument is not an xdm map.", srcLocator);  
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
       fNumOfArgs = argNum;
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
     * @param xpathExpr                          Represents first argument provided to 
     *                                           function fn:parse-json.
     * @param xctxt                              XPath context object
     * @param optionIsLiberal                    Function call fn:json-doc option liberal's value
     * @param optionDuplicatesValStr             Function call fn:json-doc option duplicates's value
     * 
     * @return                                   An xdm value of type XPathMap, XPathArray, XSDouble, XSBoolean, 
     *                                           ResultSequence (representing json null values), XSString. These 
     *                                           are the possible xdm values to which a JSON value can translate 
     *                                           to. 
     *  
     * @throws javax.xml.transform.TransformerException
     */
	private XObject getFnJsonDocResult(Expression xpathExpr, XPathContext xctxt, boolean optionIsLiberal, String optionDuplicatesValStr) 
			                                                                                             throws javax.xml.transform.TransformerException {
		
		XObject result = null;
		
		SourceLocator srcLocator = xctxt.getSAXLocator();
		
		XObject arg0Value = getFunctionArgEffectiveValue(xpathExpr, xctxt);
		
		String hrefStrVal = XslTransformEvaluationHelper.getStrVal(arg0Value);
		
		URL resolvedArg0Url = null;

		try {
			URI arg0Uri = new URI(hrefStrVal);
			
			// XSL stylesheet base uri if available			
			String stylesheetSystemId = srcLocator.getSystemId();

			if (!arg0Uri.isAbsolute() && (stylesheetSystemId != null)) {
				// If the first argument is a relative uri reference, then 
				// resolve the relative uri using base uri of the stylesheet.
				
				URI resolvedUriArg = (new URI(stylesheetSystemId)).resolve(hrefStrVal);
				
				resolvedArg0Url = resolvedUriArg.toURL(); 
			}

			if (resolvedArg0Url == null) {
				resolvedArg0Url = new URL(hrefStrVal);   
			}

			String urlStrContents = StringUtil.getStringContentFromUrl(resolvedArg0Url);
			
			result = getJsonXdmValueFromStr(urlStrContents, optionIsLiberal, optionDuplicatesValStr);

		}
		catch (URISyntaxException ex) {
			throw new javax.xml.transform.TransformerException("FODC0005 : An XPath 3.1 function call 'json-doc' error has occured. The uri '" + hrefStrVal + "' is not a "
					                                                                                                                           + "valid absolute uri, or cannot be resolved "
					                                                                                                                           + "to an absolute uri.", srcLocator);  
		}
		catch (MalformedURLException ex) {
			throw new javax.xml.transform.TransformerException("FODC0005 : An XPath 3.1 function call 'json-doc' error has occured. The uri '" + hrefStrVal + "' is not a "
					                                                                                                                           + "valid absolute uri, or cannot be resolved "
					                                                                                                                           + "to an absolute uri.", srcLocator);
		}
		catch (IOException ex) {
			throw new javax.xml.transform.TransformerException("FODC0002 : An XPath 3.1 function call 'json-doc' error has occured. The information from uri '" + hrefStrVal + "' cannot be retrieved.", srcLocator);
		}
		catch (JSONException ex) {
			throw new javax.xml.transform.TransformerException("FOUT1190 : An XPath 3.1 function call 'parse-json' first argument is not a correct json lexical string. The json parse options used were : liberal: " + optionIsLiberal + ", duplicates: " 
																                                                                                                 	                                                  + optionDuplicatesValStr + ". The json parse "
																                                                                                                 	                                                  + "resulted in following errors : " + ex.getMessage() + ".", srcLocator); 
		}
		
		return result;
	}

}
