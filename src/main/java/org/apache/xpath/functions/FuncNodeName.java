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
import javax.xml.transform.TransformerException;

import org.apache.xml.dtm.DTM;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Node;

import xml.xpath31.processor.types.XSQName;

/**
 * Implementation of XPath 3.1 function fn:node-name.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncNodeName extends FunctionDef1Arg {

   private static final long serialVersionUID = -6613114399818352830L;
   
   /**
    * Class constructor.
    */
   public FuncNodeName() {
	   m_defined_arity = new Short[] { 0, 1 };
   }

   /**
   * Evaluate the function. The function must return a valid object.
   * 
   * @param xctxt                             An XPath context object
   * @return                                  A valid XObject
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {

	  XObject result = null;
	  
	  SourceLocator srcLocator = xctxt.getSAXLocator();
	  
	  int nodeHandle = DTM.NULL;
	  
	  if (m_arg0 == null) {
		  XObject xObj = xctxt.getXPath3ContextItem();
		  if (xObj != null) {
			 if (xObj instanceof XMLNodeCursorImpl) {
				XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj;
				nodeHandle = xmlNodeCursorImpl.asNode(xctxt);
			 }
			 else {
				throw new TransformerException("XPTY0004 : While evaluating XPath function node-name without an argument, XPath context "
						                                                                                     + "item didn't evaluate to a valid node.");
			 }
		  }
		  else {
			 nodeHandle = xctxt.getCurrentNode(); 
		  }
	  }
	  else {
		  try {
		     nodeHandle = m_arg0.asNode(xctxt);
		  }
		  catch (TransformerException ex) {
			 throw new TransformerException("XPDY0002 : While evaluating XPath function node-name, function's argument didn't evaluate to a valid node."); 
		  }
	  }
	  
	  if (nodeHandle != DTM.NULL) {
		  DTM dtm = xctxt.getDTM(nodeHandle);
		  Node node = dtm.getNode(nodeHandle);
		  String nodeLocalName = node.getLocalName();
		  String namespaceUri = node.getNamespaceURI();
		  String nodeNamePrefix = node.getPrefix();
		  result = new XSQName(nodeNamePrefix, nodeLocalName, namespaceUri);
	  }
	  else {
		  result = new ResultSequence();
	  }

	  return result;
  }
  
}
