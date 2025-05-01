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
package org.apache.xpath.functions;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import xml.xpath31.processor.types.XSAnyURI;

/**
 * Implementation of XPath 3.1 fn:namespace-uri-for-prefix function.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncNamespaceUriForPrefix extends Function2Args {

	private static final long serialVersionUID = 6212323137442859818L;

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

		  XObject result = null;
	    
	      SourceLocator srcLocator = xctxt.getSAXLocator();
		  
		  Expression arg0 = getArg0();
		  Expression arg1 = getArg1();	  
		  
		  XObject arg0Value = arg0.execute(xctxt);	  
		  String nsPrefixStr = XslTransformEvaluationHelper.getStrVal(arg0Value);
		  
		  XObject arg1Value = arg1.execute(xctxt);
		  if (arg1Value instanceof XMLNodeCursorImpl) {
			 XMLNodeCursorImpl nodeSet = (XMLNodeCursorImpl)arg1Value;
			 if (nodeSet.getLength() == 1) {
				 DTMCursorIterator dtmIter = nodeSet.iterRaw();
		         int nodeHandle = dtmIter.nextNode();
		         DTMManager dtmMgr = nodeSet.getDTMManager();
		         DTM dtm = dtmMgr.getDTM(nodeHandle);
		         Node node = dtm.getNode(nodeHandle);
		         if (node.getNodeType() == Node.ELEMENT_NODE) {
		        	result = getNamespaceUriForPrefix(nsPrefixStr, node);	 
		         }
		         else {
		        	throw new javax.xml.transform.TransformerException("FOAP0001: The second argument within function call "
                                                                               + "fn:namespace-uri-for-prefix, needs to be an element node.", srcLocator); 
		         }
			 }
			 else {
				throw new javax.xml.transform.TransformerException("FOAP0001: The second argument within function call "
	                                                                            + "fn:namespace-uri-for-prefix, needs to be an element nodeset of size one.", srcLocator); 
			 }
		  }
		  else {
			 throw new javax.xml.transform.TransformerException("FOAP0001: The second argument within function call "
			 		                                                         + "fn:namespace-uri-for-prefix, is not a node.", srcLocator);  
		  }
	
	      return result;
    }
   
   /**
    * Get namespace uri for prefix, searching within this function's 'node' argument 
    * and 'node' argument's ancestor nodes.
    */
   private XSAnyURI getNamespaceUriForPrefix(String nsPrefixStr, Node node) {
	   XSAnyURI xsAnyUri = null;
	   
	   NamedNodeMap elemAttributes = node.getAttributes();
	   int attrListSize = elemAttributes.getLength();
	   for (int idx = 0; idx < attrListSize; idx++) {
		   Attr attr = (Attr)elemAttributes.item(idx);
		   String attrName = attr.getName();
		   if ("xmlns".equals(attrName) && ((nsPrefixStr == null) || ("".equals(nsPrefixStr)))) {
			   xsAnyUri = new XSAnyURI(attr.getValue());  
		   }
		   else if (attrName.startsWith("xmlns:")) {
			  String nsPrefixOfAttr = attrName.substring(6);
			  if (nsPrefixStr.equals(nsPrefixOfAttr)) {
				 xsAnyUri = new XSAnyURI(attr.getValue()); 
			  }
		   }
	   }
	   
	   if (xsAnyUri != null) {
		  return xsAnyUri;  
	   }
	   else {
		  // Recursive call to this function
		  xsAnyUri = getNamespaceUriForPrefix(nsPrefixStr, node.getParentNode()); 
	   }
	   
	   return xsAnyUri;
   }
}
