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

import javax.xml.transform.SourceLocator;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSBoolean;

/**
 * Implementation of fn:lang function (available in both 
 * XPath 1.0 and 3.1 versions).
 * 
 * XPath 3.1 has introduced an optional second argument 
 * for this function, representing an explicit XPath node. The
 * meaning of first argument of this function is same, within
 * both XPath 1.0 and 3.1 versions.
 * 
 * @xsl.usage advanced
 */
public class FuncLang extends FunctionMultiArgs {
	
	   private static final long serialVersionUID = -7868705139354872185L;

	   /**
	   * Implementation of the function. The function must return
	   * a valid object.
	   * 
	   * @param xctxt 	            the current evaluation context
	   * @return                    a valid XObject
	   *
	   * @throws javax.xml.transform.TransformerException
	   */
	   public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {
	    
			SourceLocator srcLocator = xctxt.getSAXLocator();
		    
		    if (m_arg0 == null) {
			    throw new javax.xml.transform.TransformerException("XPDY0002: The function call fn:lang needs to have a mandatory "
			    		                                                                              + "first argument.", srcLocator);
			}
		    
		    if (m_arg2 != null) {
			    throw new javax.xml.transform.TransformerException("XPDY0002: The function call fn:lang cannot have more "
			    		                                                                 + "than two arguments.", srcLocator);
			}
			
		    String langStrToBeTested = m_arg0.execute(xctxt).str();
		    
		    int nodeHandle = DTM.NULL;
		    
		    if (m_arg1 != null) {
		       XObject secondArgEvalResult = m_arg1.execute(xctxt);
		       if (secondArgEvalResult instanceof XNodeSet) {
		    	   XNodeSet xObject = (XNodeSet)secondArgEvalResult;
		    	   if (xObject.getLength() == 1) {
		    		   XNodeSet nodeSet = (XNodeSet)xObject;
				       DTMIterator dtmIter = nodeSet.iterRaw();
				       nodeHandle = dtmIter.nextNode(); 
		    	   }
		    	   else {
		    		  throw new javax.xml.transform.TransformerException("XPDY0002: The second argument of function call "
		    		  		                                        + "fn:lang, is not a nodeset with length one.", srcLocator); 
		    	   }
		       }
		       else {
		    	   throw new javax.xml.transform.TransformerException("XPTY0004: A type error has occured. The "
		    	   		                                            + "second argument of function call fn:lang is not a node.", srcLocator);  
		       }
		    }
		    else {
		       nodeHandle = xctxt.getCurrentNode();
		    }
		    
		    if (nodeHandle == DTM.NULL) {
		       throw new javax.xml.transform.TransformerException("XPDY0002: An XPath node argument (either as an explicit second "
		       		                                                      + "argument, or an implicit context node if second argument is absent) for "
		       		                                                      + "function call fn:lang cannot be absent.", srcLocator);
		    }
		    
		    boolean isLangEquals = false;
		    
		    DTM dtm = xctxt.getDTM(nodeHandle);
		
		    while (DTM.NULL != nodeHandle) {
		        if (DTM.ELEMENT_NODE == dtm.getNodeType(nodeHandle)) {        
		    	   int langAttrNodeHandle = dtm.getAttributeNode(nodeHandle, "http://www.w3.org/XML/1998/namespace", "lang");
		
		           if (DTM.NULL != langAttrNodeHandle) {
		              String langVal = dtm.getNodeValue(langAttrNodeHandle);
		              if ((langVal.toLowerCase()).startsWith(langStrToBeTested.toLowerCase())) {
		                 int valLen = langStrToBeTested.length();
		                 if ((langVal.length() == valLen) || (langVal.charAt(valLen) == '-')) {
		                    isLangEquals = true;
		                 }
		              }
		
		              break;
		           }
		        }
		        
		        nodeHandle = dtm.getParent(nodeHandle);
		    }
		
		    return isLangEquals ? new XSBoolean(true) : new XSBoolean(false);
	   }
	   
}
