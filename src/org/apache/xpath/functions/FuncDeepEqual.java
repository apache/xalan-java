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

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.XSConstructorFunctionUtil;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import xml.xpath31.processor.types.XSAnyAtomicType;
import xml.xpath31.processor.types.XSBoolean;

/**
 * Implementation of an XPath 3.1 fn:deep-equal function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncDeepEqual extends FunctionMultiArgs {

  private static final long serialVersionUID = -7233896041672168880L;

  /**
   * Execute the function. The function must return a valid object.
   * 
   * @param xctxt The current execution context
   * @return A valid XObject
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {
	    
	  XObject result = new XSBoolean(true);
	  
	  SourceLocator srcLocator = xctxt.getSAXLocator();
	  
	  Expression arg0 = getArg0();
	  Expression arg1 = getArg1();
	  
	  if ((arg0 == null) || (arg1 == null)) {
		 throw new javax.xml.transform.TransformerException("FOAP0001 : The number of arguments specified while "
		 		                                                   + "calling deep-equal() function is wrong. Expected "
		 		                                                   + "number of arguments for deep-equal() function is two "
		 		                                                   + "or three.", srcLocator);  
	  }
	  
	  try {	  
		  XObject arg0Val = arg0.execute(xctxt);
		  XObject arg1Val = arg1.execute(xctxt);
		  
	      Expression arg2 = getArg2();  // an optional collation argument. REVISIT
		  
		  XObject collationVal = null;
		  
		  if (arg2 != null) {
			 collationVal = arg2.execute(xctxt);   
		  }
		  
		  ResultSequence resultSeq0 = XslTransformEvaluationHelper.getResultSequenceFromXObject(arg0Val, xctxt);
		  
		  ResultSequence resultSeq1 = XslTransformEvaluationHelper.getResultSequenceFromXObject(arg1Val, xctxt);
		  
		  boolean isDeepEqual = true;
		  
		  if (resultSeq0.size() == resultSeq1.size()) {		 
			 for (int idx1 = 0; idx1 < resultSeq0.size(); idx1++) {
				for (int idx2 = 0; idx2 < resultSeq1.size(); idx2++) {
				  if (idx1 == idx2) {
					 XObject item1 = resultSeq0.item(idx1);
					 XObject item2 = resultSeq1.item(idx2);
					 
					 if ((item1 instanceof XSAnyAtomicType) && (item2 instanceof XSAnyAtomicType)) {
						if (!item1.vcEquals(item2, null, true)) {
						   isDeepEqual = false;
		                   break;
						}
					 }
					 else if ((item1 instanceof XSAnyAtomicType) && (item2 instanceof XNodeSet)) {
						 isDeepEqual = false;
		                 break; 
					 }
	                 else if ((item1 instanceof XNodeSet) && (item2 instanceof XSAnyAtomicType)) {
	                	 isDeepEqual = false;
	                 	 break; 
					 }
	                 else if ((item1 instanceof XNodeSet) && (item2 instanceof XNodeSet)) {
	                	 item1 = ((XNodeSet)item1).getFresh();
	                	 item2 = ((XNodeSet)item2).getFresh();
	                	 DTMIterator dtmIter1 = ((XNodeSet)item1).iterRaw();
	                	 DTMIterator dtmIter2 = ((XNodeSet)item2).iterRaw();
	                	 int nodeHandle1 = dtmIter1.nextNode();
	                	 int nodeHandle2 = dtmIter2.nextNode();
	                	 DTM dtm1 = xctxt.getDTM(nodeHandle1);
			     	     Node node1 = dtm1.getNode(nodeHandle1);
			     	     DTM dtm2 = xctxt.getDTM(nodeHandle2);
			     	     Node node2 = dtm2.getNode(nodeHandle2);
			     	     if (!isTwoXmlDomNodesEqual(node1, node2)) {
			     	        isDeepEqual = false;
		                 	break; 
			     	     }	 
					 }
	                 else {
	                	isDeepEqual = false;
	                	break;
	                 }
				  }			 			 
			  }
				 
			  if (!isDeepEqual) {
				 break; 
			  }
		   }
		 }
		 else {
		    result = new XSBoolean(false);  
		 }
		  
		 if (!isDeepEqual) {
			result = new XSBoolean(false); 
		 }
	 }
	 catch (Exception ex) {
		 throw new TransformerException(ex.getMessage(), srcLocator); 
	 }
	
	 return result;
  }
  
  /*
   * Check whether two XML DOM nodes are equal.
   */
  private boolean isTwoXmlDomNodesEqual(Node node1, Node node2) throws Exception {
	 boolean isTwoDomNodesEqual = true;
	 
	 if ((node1.getNodeType() == Node.ELEMENT_NODE) && 
			                                     (node2.getNodeType() == Node.ELEMENT_NODE)) {
		String xmlStr1 = serializeXmlDomElementNode(node1);
		String xmlStr2 = serializeXmlDomElementNode(node2);
		isTwoDomNodesEqual = isTwoXmlDocumentStrEqual(xmlStr1, xmlStr2);
	 }
	 else if ((node1.getNodeType() == Node.ATTRIBUTE_NODE) && 
			                                     (node2.getNodeType() == Node.ATTRIBUTE_NODE)) {
		String localName1 = node1.getLocalName();
		String nsUri1 = node1.getNamespaceURI();		
		String localName2 = node2.getLocalName();
		String nsUri2 = node2.getNamespaceURI();		
		if (localName1.equals(localName2)) {
		   if ((nsUri1 != null) && (nsUri2 != null) && !nsUri1.equals(nsUri2)) {
			  isTwoDomNodesEqual = false;  
		   }
		   else if (((nsUri1 != null) && (nsUri2 == null)) || 
				                                ((nsUri1 == null) && (nsUri2 != null))) {
			  isTwoDomNodesEqual = false; 
		   }
		}
		else {
		   isTwoDomNodesEqual = false;
		}
		
		if (isTwoDomNodesEqual) {
		   String node1StrVal = node1.getNodeValue();
		   String node2StrVal = node2.getNodeValue();
		   if (!node1StrVal.equals(node2StrVal)) {
			  isTwoDomNodesEqual = false;  
		   }
		}		
	 }
	 else if ((node1.getNodeType() == Node.TEXT_NODE) && 
			                                       (node2.getNodeType() == Node.TEXT_NODE)) {
		String node1StrVal = node1.getNodeValue();
		String node2StrVal = node2.getNodeValue();
		if (!node1StrVal.equals(node2StrVal)) {
		   isTwoDomNodesEqual = false;
		}
	 }
	 else if ((node1.getNodeType() == Node.COMMENT_NODE) && 
			                                          (node2.getNodeType() == Node.COMMENT_NODE)) {
		String node1StrVal = node1.getNodeValue();
		String node2StrVal = node2.getNodeValue();
		if (!node1StrVal.equals(node2StrVal)) {
		   isTwoDomNodesEqual = false;
		}
	 }
	 
	 return isTwoDomNodesEqual; 
  }
  
  /*
   * Check whether two XML document strings are equal.
   */
  private boolean isTwoXmlDocumentStrEqual(String xmlStr1, String xmlStr2) 
		                                                               throws Exception {
	 boolean isTwoXmlDomElementNodesEqual = true;
	 
	 DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	 dbf.setNamespaceAware(true);
	 
	 DocumentBuilder dBuilder = dbf.newDocumentBuilder();
	 
	 Document document1 = dBuilder.parse(new ByteArrayInputStream(xmlStr1.getBytes()));
	 
	 Document document2 = dBuilder.parse(new ByteArrayInputStream(xmlStr2.getBytes()));
	 
	 isTwoXmlDomElementNodesEqual = document1.isEqualNode(document2);
	 
	 return isTwoXmlDomElementNodesEqual;
  }
  
  /*
   * Serialize an XML DOM element node, to string value.
   */
  private String serializeXmlDomElementNode(Node node) throws Exception {
	 String resultStr = null;
	 
	 DOMImplementationLS domImplLS = (DOMImplementationLS)((DOMImplementationRegistry.
			                                                                newInstance()).getDOMImplementation("LS"));
     LSSerializer lsSerializer = domImplLS.createLSSerializer();
     DOMConfiguration domConfig = lsSerializer.getDomConfig();
     domConfig.setParameter(XSConstructorFunctionUtil.DOM_FORMAT_PRETTY_PRINT, Boolean.TRUE);
     resultStr = lsSerializer.writeToString(node);
     resultStr = resultStr.replaceFirst(XSConstructorFunctionUtil.UTF_16, XSConstructorFunctionUtil.UTF_8);
	 
	 return resultStr;
  }
  
}
