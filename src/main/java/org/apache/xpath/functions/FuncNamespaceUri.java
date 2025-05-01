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
/*
 * $Id$
 */
package org.apache.xpath.functions;

import javax.xml.transform.SourceLocator;

import org.apache.xml.dtm.DTM;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSAnyURI;

/**
 * Implementation of XPath 3.1 fn:namespace-uri function.
 * 
 * @xsl.usage advanced
 */
public class FuncNamespaceUri extends FunctionDef1Arg
{

   private static final long serialVersionUID = 6358386272825912243L;

  /**
   * Implementation of the function. The function must return
   * a valid object.
   * 
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {
	 
	 int context = DTM.NULL;
	 
	 SourceLocator srcLocator = xctxt.getSAXLocator();
	 
	 try {
	    context = getArg0AsNode(xctxt);
	 }
	 catch (javax.xml.transform.TransformerException ex) {
		throw new javax.xml.transform.TransformerException("XPTY0004: Type error occured while evaluating function "
				                                                  + "fn:namespace-uri. Please ensute that, context item is a node.", srcLocator); 
	 }	 	 
	    
	 String uriStrVal;
	    
	 if (context != DTM.NULL) {
		DTM dtm = xctxt.getDTM(context);
		int nodeType = dtm.getNodeType(context);
		if (nodeType == DTM.ELEMENT_NODE) {
		   uriStrVal = dtm.getNamespaceURI(context);
		}
		else if (nodeType == DTM.ATTRIBUTE_NODE) {
		   // This function always returns an empty string for namespace nodes.
		   // We check for those here. Fix inspired by Davanum Srinivas.
			
		   uriStrVal = dtm.getNodeName(context);
		   if (uriStrVal.startsWith("xmlns:") || uriStrVal.equals("xmlns")) {
			  return new XSAnyURI("");
		   }
			
		   uriStrVal = dtm.getNamespaceURI(context);
		}
		else {
		   return new XSAnyURI("");
		}
	 }
	 else {
	    throw new javax.xml.transform.TransformerException("XPDY0002: While evaluating function call fn:namespace-uri, "
	    		                                                  + "context item cannot be absent.", srcLocator); 
	 }
	    
	 return ((uriStrVal == null) ? new XSAnyURI("") : new XSAnyURI(uriStrVal));
  }
}
