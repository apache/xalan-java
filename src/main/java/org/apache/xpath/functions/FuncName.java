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
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.objects.ElemFunctionItem;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XdmAttributeItem;
import org.apache.xpath.objects.XdmCommentItem;
import org.apache.xpath.objects.XdmProcessingInstructionItem;
import org.w3c.dom.Node;

import xml.xpath31.processor.types.XSAnyAtomicType;
import xml.xpath31.processor.types.XSString;

/**
 * Implementation of XPath 3.1 function fn:name.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncName extends FunctionMultiArgs {

  private static final long serialVersionUID = -3263417937216412681L;
  
  /**
   * Class constructor.
   */
  public FuncName() {
	  m_defined_arity = new Short[] { 0, 1 };
  }

  /**
   * Implementation of the function. The function must return
   * a valid object.
   * 
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {

	  XSString result = null;    
      	  
	  Expression arg0 = getArg0();
	  
	  SourceLocator srcLocator = xctxt.getSAXLocator();
	  
	  int nodeHandle = DTM.NULL;
	  
	  String nodeNameStr = null;
	  
	  if (arg0 == null) {
		 XObject contextItem = xctxt.getXPath3ContextItem();
		 if (contextItem != null) {
			if ((contextItem instanceof XSAnyAtomicType) || (contextItem instanceof XBoolean) 
					                                                || (contextItem instanceof XBooleanStatic) || (contextItem instanceof XNumber) 
					                                                || (contextItem instanceof XPathMap) || (contextItem instanceof XPathArray) 
					                                                || (contextItem instanceof XPathInlineFunction) || (contextItem instanceof ElemFunctionItem)) {
				result = new XSString("");
				   
				return result;
			}
			else if (contextItem instanceof XdmAttributeItem) {
			   XdmAttributeItem xdmAttributeItem = (XdmAttributeItem)contextItem;
			   String localName = xdmAttributeItem.getAttrLocalName();
			   String namespace = xdmAttributeItem.getAttrNodeNs();
			   if (namespace != null) {
				  nodeNameStr = "Q{" + namespace + "}" + localName; 
			   }
			   else {
				  nodeNameStr = localName;  
			   }
			   
			   result = new XSString(nodeNameStr);
			   
			   return result;
			}
			else if (contextItem instanceof XdmCommentItem) {
			   result = new XSString("");
				   
			   return result;
			}
            else if (contextItem instanceof XdmProcessingInstructionItem) {
            	XdmProcessingInstructionItem xdmProcessingInstructionItem = (XdmProcessingInstructionItem)contextItem;
            	nodeNameStr = xdmProcessingInstructionItem.getName();
            	
            	result = new XSString(nodeNameStr);
				   
 			    return result;
			}
		 }
		 
		 nodeHandle = xctxt.getCurrentNode();		 
	  }
	  else if (arg0 instanceof SelfIteratorNoPredicate) {
		  XObject contextItem = xctxt.getXPath3ContextItem();
		  
		  if ((contextItem != null) && (contextItem instanceof XMLNodeCursorImpl)) {
			  contextItem = contextItem.getFresh();
			  nodeHandle = getNodeHandle((XMLNodeCursorImpl)contextItem, xctxt);
		  }
		  else {
			  XObject xObject = m_arg0.execute(xctxt);
			  if (xObject instanceof XMLNodeCursorImpl) {
				  xObject = xObject.getFresh();
				  nodeHandle = getNodeHandle((XMLNodeCursorImpl)xObject, xctxt);
			  }
		  }
	  }
	  else {
		 XObject xObject = arg0.execute(xctxt);		 
		 if (xObject instanceof XMLNodeCursorImpl) {
			xObject = xObject.getFresh();			
			XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObject;			
			nodeHandle = getNodeHandle(xmlNodeCursorImpl, xctxt);
		 }		 
	  }	  	  
	  
	  if (nodeHandle != DTM.NULL) {
		 nodeNameStr = getNodeNameStrFromNodeHandle(nodeHandle, xctxt); 
	  }	  
	  else {
		 throw new javax.xml.transform.TransformerException("XPTY0004 : The first argument of XPath function "
		 		                                                                    + "fn:name is not a node, or there's no context node.", srcLocator); 
	  }
	  
	  result = new XSString(nodeNameStr);

      return result;
  }
  
  /**
   * Method definition, to get node handle of an 
   * xdm node. 
   * 
   * @param XMLNodeCursorImpl				 The supplied XMLNodeCursorImpl 
   *                                         object.
   * @param xctxt                            XPathContext object instance
   * @return								 Node handle value
   * @throws TransformerException
   */
  private int getNodeHandle(XMLNodeCursorImpl XMLNodeCursorImpl, XPathContext xctxt) throws TransformerException {	 	 	 	 	 
	 
	 int result = DTM.NULL;
	 
	 result = XMLNodeCursorImpl.asNode(xctxt);
	 
	 return result;
  }

  /**
   * Method definition, to get an xdm node name's string value,
   * from the supplied xdm node's integer handle.
   * 
   * @param nodeHandle					An XPath node's integer handle
   * @param xctxt                       An XPath context object						
   * @return                            An XPath node name's string value 
   */
  private String getNodeNameStrFromNodeHandle(int nodeHandle, XPathContext xctxt) {	  
	  
	  String result = null;

	  DTM dtm = xctxt.getDTM(nodeHandle);
	  if ((dtm.getNodeType(nodeHandle) == DTM.DOCUMENT_NODE) || (dtm.getNodeType(nodeHandle) == DTM.COMMENT_NODE) 
			                                                                                  || (dtm.getNodeType(nodeHandle) == DTM.TEXT_NODE)) {
		  result = "";
	  }
	  else if (dtm.getNodeType(nodeHandle) == DTM.NAMESPACE_NODE) {
		  Node node = dtm.getNode(nodeHandle);
		  String nsNodeName = node.getNodeName();
		  if ((nsNodeName == null) || ("".equals(nsNodeName))) {
			  result = ""; 
		  }
		  else if (nsNodeName.contains(":")) {
			  int idx = nsNodeName.indexOf(':');
			  result = nsNodeName.substring(idx + 1);   
		  }
		  else {
			  result = nsNodeName; 
		  }
	  }
	  else {
		  Node node = dtm.getNode(nodeHandle);
		  result = node.getNodeName();
	  }

	  return result;
  }
  
}
