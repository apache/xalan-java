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

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.utils.Constants;
import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.XPathContext;
import org.apache.xpath.composite.XPathNamedFunctionReference;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import xml.xpath31.processor.types.XSAnyAtomicType;
import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSString;

/**
 * Implementation of an XPath 3.1 function fn:deep-equal.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncDeepEqual extends FunctionMultiArgs {

  private static final long serialVersionUID = -7233896041672168880L;
  
  private XPathCollationSupport m_xpathCollationSupport = null;
  
  /**
   * Default constructor.
   */
  public FuncDeepEqual() {
	  m_arity = new Short[] {2, 3}; 
  }
  
  /**
   * Class constructor.
   */
  public FuncDeepEqual(XPathCollationSupport xPathCollationSupport) {
	 m_xpathCollationSupport = xPathCollationSupport; 
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
	  
	  if ((m_arg0 == null) || (m_arg1 == null)) {
		 throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath 3.1 function call 'deep-equal' requires "
		 		                                                                                                         + "two or three arguments.", srcLocator);  
	  }
	  
	  try {	  
		  XObject arg0Val = getFunctionArgEffectiveValue(m_arg0, xctxt);
		  
		  XObject arg1Val = getFunctionArgEffectiveValue(m_arg1, xctxt);
	      
	      m_xpathCollationSupport = xctxt.getXPathCollationSupport();
		  
	      String collationUri = null;
	      
		  if (m_arg2 != null) {
			 // A collation uri was, explicitly provided during the function call fn:deep-equal
		     XObject collationXObj = getFunctionArgEffectiveValue(m_arg2, xctxt);
		     
		     collationUri = XslTransformEvaluationHelper.getStrVal(collationXObj); 			 			 
		  }
		  else {
			 collationUri = xctxt.getDefaultCollation(); 
		  }
		  
		  ResultSequence resultSeq0 = XslTransformEvaluationHelper.getResultSequenceFromXObject(arg0Val, xctxt);
		  
		  int size1 = resultSeq0.size();
		  for (int idx = 0; idx < size1; idx++) {
			 XObject xObj1 = resultSeq0.item(idx);
			 if (xObj1 instanceof XPathNamedFunctionReference) {
				 throw new javax.xml.transform.TransformerException("FOTY0015 : An XPath 3.1 function call 'deep-equal' has an argument containing a function item.", srcLocator);   
			 }
		  }
		  
		  ResultSequence resultSeq1 = XslTransformEvaluationHelper.getResultSequenceFromXObject(arg1Val, xctxt);
		  
		  int size2 = resultSeq1.size();
		  for (int idx = 0; idx < size2; idx++) {
			 XObject xObj1 = resultSeq1.item(idx);
			 if (xObj1 instanceof XPathNamedFunctionReference) {
				 throw new javax.xml.transform.TransformerException("FOTY0015 : An XPath 3.1 function call 'deep-equal' has an argument containing a function item.", srcLocator);   
			 }
		  }
		  
		  boolean isDeepEqual = false;
		  
		  if (size1 == size2) {		 
			  isDeepEqual = isTwoSequenceDeepEqual(resultSeq0, resultSeq1, xctxt, collationUri);
		  }

		  if (isDeepEqual) {
			  result = new XSBoolean(true); 
		  }
		  else {
			  result = new XSBoolean(false); 
		  }
	 }
	 catch (Exception ex) {
		 throw new TransformerException(ex.getMessage(), srcLocator); 
	 }
	
	 return result;
  }

  /**
   * Method definition, to check whether two xdm sequences of same size
   * are deep equal.
   * 
   * @param resultSeq0                         The first, supplied result sequence
   * @param resultSeq1                         The second, supplied result sequence
   * @param xctxt                              An XPath context object
   * @param collationUri                       The collation uri
   * @return                                   Boolean value true or false
   * @throws TransformerException
   * @throws Exception
   */
  public boolean isTwoSequenceDeepEqual(ResultSequence resultSeq0, ResultSequence resultSeq1,
		                                                                         XPathContext xctxt, String collationUri) throws TransformerException, Exception {
	
	  boolean result = true;

	  int size1 = resultSeq0.size();
	  for (int idx1 = 0; idx1 < size1; idx1++) {
		  int size2 = resultSeq1.size();
		  for (int idx2 = 0; idx2 < size2; idx2++) {
			  if (idx1 == idx2) {
				  XObject item1 = resultSeq0.item(idx1);
				  XObject item2 = resultSeq1.item(idx2);

				  if ((item1 instanceof XSString) && (item2 instanceof XSString)) {
					  String str1 = ((((XSString)item1))).stringValue();
					  String str2 = ((((XSString)item2))).stringValue();
					  int strComparisonResult = m_xpathCollationSupport.compareStringsUsingCollation(str1, str2, collationUri);
					  if (strComparisonResult != 0) {
						  result = false;
						  break;
					  }
				  }
				  else if ((item1 instanceof XSString) && (item2 instanceof XString)) {
					  String str1 = ((((XSString)item1))).stringValue();
					  String str2 = (((XString)item2)).str();
					  int strComparisonResult = m_xpathCollationSupport.compareStringsUsingCollation(str1, str2, collationUri);
					  if (strComparisonResult != 0) {
						  result = false;
						  break;
					  }
				  }
				  else if ((item1 instanceof XString) && (item2 instanceof XSString)) {
					  String str1 = (((XString)item1)).str();
					  String str2 = ((((XSString)item2))).stringValue();
					  int strComparisonResult = m_xpathCollationSupport.compareStringsUsingCollation(str1, str2, collationUri);
					  if (strComparisonResult != 0) {
						  result = false;
						  break;
					  }
				  }
				  else if ((item1 instanceof XString) && (item2 instanceof XString)) {
					  String str1 = (((XString)item1)).str();
					  String str2 = (((XString)item2)).str();
					  int strComparisonResult = m_xpathCollationSupport.compareStringsUsingCollation(str1, str2, collationUri);
					  if (strComparisonResult != 0) {
						  result = false;
						  break;
					  }
				  }
				  else if ((item1 instanceof XSAnyAtomicType) && (item2 instanceof XMLNodeCursorImpl)) {
					  result = false;
					  break; 
				  }
				  else if ((item1 instanceof XMLNodeCursorImpl) && (item2 instanceof XSAnyAtomicType)) {
					  result = false;
					  break; 
				  }
				  else if ((item1 instanceof XMLNodeCursorImpl) && (item2 instanceof XMLNodeCursorImpl)) {
					  item1 = ((XMLNodeCursorImpl)item1).getFresh();
					  item2 = ((XMLNodeCursorImpl)item2).getFresh();
					  DTMCursorIterator dtmIter1 = ((XMLNodeCursorImpl)item1).iterRaw();
					  DTMCursorIterator dtmIter2 = ((XMLNodeCursorImpl)item2).iterRaw();
					  int nodeHandle1 = dtmIter1.nextNode();
					  int nodeHandle2 = dtmIter2.nextNode();
					  DTM dtm1 = xctxt.getDTM(nodeHandle1);
					  Node node1 = dtm1.getNode(nodeHandle1);
					  DTM dtm2 = xctxt.getDTM(nodeHandle2);
					  Node node2 = dtm2.getNode(nodeHandle2);
					  if (!isTwoXmlDomNodesEqual(node1, node2, collationUri)) {
						  result = false;
						  break; 
					  }	 
				  }
				  else if (!item1.vcEquals(item2, null, null, true)) {
					  result = false;
					  break;
				  }
			  }			 			 
		  }

		  if (!result) {
			  break; 
		  }
	  }

	  return result;
  }
  
  /**
   * Method definition, to check whether two XML dom nodes 
   * are equal.
   * 
   * @param node1							The first XML dom node
   * @param node2                           The second XML dom node
   * @param collationUri                    The collation uri
   * @return                                Boolean value true or false
   * @throws Exception
   */
  private boolean isTwoXmlDomNodesEqual(Node node1, Node node2, String collationUri) throws Exception {
	 
	 boolean isTwoXmlDomNodesEqual = true;
	 
	 if ((node1.getNodeType() == Node.ELEMENT_NODE) && 
			                                     (node2.getNodeType() == Node.ELEMENT_NODE)) {
		String xmlStr1 = XslTransformEvaluationHelper.serializeXmlDomElementNode(node1);
		String xmlStr2 = XslTransformEvaluationHelper.serializeXmlDomElementNode(node2);
		isTwoXmlDomNodesEqual = isTwoXmlDocumentStrEqual(xmlStr1, xmlStr2, collationUri);
	 }
	 else if ((node1.getNodeType() == Node.ATTRIBUTE_NODE) && 
			                                     (node2.getNodeType() == Node.ATTRIBUTE_NODE)) {
		String localName1 = node1.getLocalName();
		String nsUri1 = node1.getNamespaceURI();		
		String localName2 = node2.getLocalName();
		String nsUri2 = node2.getNamespaceURI();		
		if (localName1.equals(localName2)) {
		   if ((nsUri1 != null) && (nsUri2 != null) && !nsUri1.equals(nsUri2)) {
			  isTwoXmlDomNodesEqual = false;  
		   }
		   else if (((nsUri1 != null) && (nsUri2 == null)) || 
				                                ((nsUri1 == null) && (nsUri2 != null))) {
			  isTwoXmlDomNodesEqual = false; 
		   }
		}
		else {
		   isTwoXmlDomNodesEqual = false;
		}
		
		if (isTwoXmlDomNodesEqual) {
		   String node1StrVal = node1.getNodeValue();
		   String node2StrVal = node2.getNodeValue();
		   int strComparisonResult = m_xpathCollationSupport.compareStringsUsingCollation(node1StrVal, node2StrVal, collationUri);
		   if (strComparisonResult != 0) {
			  isTwoXmlDomNodesEqual = false;  
		   }
		}		
	 }
	 else if ((node1.getNodeType() == Node.TEXT_NODE) && 
			                                       (node2.getNodeType() == Node.TEXT_NODE)) {
		String node1StrVal = node1.getNodeValue();
		String node2StrVal = node2.getNodeValue();
		int strComparisonResult = m_xpathCollationSupport.compareStringsUsingCollation(node1StrVal, node2StrVal, collationUri);
		if (strComparisonResult != 0) {
		   isTwoXmlDomNodesEqual = false;  
		}
	 }
	 else if ((node1.getNodeType() == Node.COMMENT_NODE) && 
			                                          (node2.getNodeType() == Node.COMMENT_NODE)) {
		String node1StrVal = node1.getNodeValue();
		String node2StrVal = node2.getNodeValue();
		int strComparisonResult = m_xpathCollationSupport.compareStringsUsingCollation(node1StrVal, node2StrVal, collationUri);
		if (strComparisonResult != 0) {
		   isTwoXmlDomNodesEqual = false;  
		}
	 }
	 
	 return isTwoXmlDomNodesEqual; 
  }
  
  /**
   * Method definition, to check whether two XML documents represented
   * by the supplied XML document strings are equals.
   * 
   * @param xmlStr1                       XML document string one
   * @param xmlStr2                       XML document string two
   * @param collationUri                  The collation uri
   * @return                              Boolean value true or false
   * @throws Exception
   */
  private boolean isTwoXmlDocumentStrEqual(String xmlStr1, String xmlStr2, String collationUri) 
		                                                               throws Exception {
	 boolean isTwoXmlDomElementNodesEqual = true;
	 
	 System.setProperty(Constants.XML_DOCUMENT_BUILDER_FACTORY_KEY, Constants.XML_DOCUMENT_BUILDER_FACTORY_VALUE);
	 
	 DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	 dbf.setNamespaceAware(true);	 
	 DocumentBuilder dBuilder = dbf.newDocumentBuilder();
	 
	 Document document1 = dBuilder.parse(new ByteArrayInputStream(xmlStr1.getBytes()));
	 ElementImpl elem1 = (ElementImpl)(document1.getDocumentElement());
	 
	 Document document2 = dBuilder.parse(new ByteArrayInputStream(xmlStr2.getBytes()));
	 ElementImpl elem2 = (ElementImpl)(document2.getDocumentElement());
	 
	 // The method 'isEqualNodeWithQName' used here, has been newly implemented
	 // within Xerces-J's class org.apache.xerces.dom.ElementImpl to support
	 // few of the use cases of XPath 3.1 fn:deep-equal function. The method
	 // 'isEqualNodeWithQName' used here is very similar to the standard
	 // XML DOM method 'isEqualNode', except for few enhancements to compare
	 // XML namespace declarations on element nodes as specified for XPath 3.1 
	 // fn:deep-equal function. 
	 isTwoXmlDomElementNodesEqual = elem1.isEqualNodeWithQName(elem2, collationUri);
	 
	 return isTwoXmlDomElementNodesEqual;
  }

  public XPathCollationSupport getXPathCollationSupport() {
	  return m_xpathCollationSupport;
  }

  public void setXPathCollationSupport(XPathCollationSupport xPathCollationSupport) {
	  this.m_xpathCollationSupport = xPathCollationSupport;
  }
  
}
