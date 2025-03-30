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
package org.apache.xpath.functions;

import javax.xml.transform.SourceLocator;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Node;

import xml.xpath31.processor.types.XSString;

/**
 * Implementation of XPath 3.1 fn:local-name function.
 * 
 * @xsl.usage advanced
 */
public class FuncLocalName extends FunctionDef1Arg {
	
	private static final long serialVersionUID = -1472285426859105850L;

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
		 
		 XSString result = null;
		 
		 SourceLocator srcLocator = xctxt.getSAXLocator();
		 
		 int nodeHandle = DTM.NULL;
		 
		 if (m_arg0 == null) {
		    nodeHandle = xctxt.getCurrentNode();
		 }
		 else if (m_arg0 instanceof SelfIteratorNoPredicate) {
			 XObject contextItem = xctxt.getXPath3ContextItem();
			 if ((contextItem != null) && (contextItem instanceof XMLNodeCursorImpl)) {
				 nodeHandle = getNodeHandle((XMLNodeCursorImpl)contextItem); 
			 }
			 else {
				 XObject xObject = m_arg0.execute(xctxt);
			     if (xObject instanceof XMLNodeCursorImpl) {
			        nodeHandle = getNodeHandle((XMLNodeCursorImpl)xObject);
			     }
			 }
		 }
		 else {			 
			 XObject xObject = m_arg0.execute(xctxt);
		     if (xObject instanceof XMLNodeCursorImpl) {
		        nodeHandle = getNodeHandle((XMLNodeCursorImpl)xObject);
		     }
		 }
		 
		 if (nodeHandle == DTM.NULL) {
			 throw new javax.xml.transform.TransformerException("XPTY0004: The 1st argument of XPath function "
			 		                                                                    + "fn:local-name is not a node, or there's no context node.", srcLocator); 
		 }
	     
	     String localNameStrVal = getLocalName(nodeHandle, xctxt);
	     
	     result = new XSString(localNameStrVal);
		
		 return result;
	 }

	 /**
	  * Get dtm node handle of a node.
	  */
	 private int getNodeHandle(XMLNodeCursorImpl nodeSet) {
		 int nodeHandle;
		 
		 DTMCursorIterator dtmIter = nodeSet.iterRaw();
		 nodeHandle = dtmIter.nextNode();
		 
		 return nodeHandle;
	 }
	 
	 /**
	  *  Get local-name string value of xpath node.
	  */
	 private String getLocalName(int nodeHandle, XPathContext xctxt) {
		 String localName = null;
		 
		 DTMManager dtmMgr = xctxt.getDTMManager();
         DTM dtm = dtmMgr.getDTM(nodeHandle);
         Node node = dtm.getNode(nodeHandle);
         
         localName = node.getLocalName();
         
         return localName;
	 }
}
