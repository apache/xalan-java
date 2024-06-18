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

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import xml.xpath31.processor.types.XSAnyAtomicType;
import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSString;

/**
 * Implementation of an XPath 3.1 fn:deep-equal function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncDeepEqual extends FunctionMultiArgs {

  private static final long serialVersionUID = -7233896041672168880L;    
  
  private static final String JAXP_XML_DOCUMENT_BUILDER_FACTORY_NAME = "javax.xml.parsers.DocumentBuilderFactory";
  
  private static final String JAXP_XML_DOCUMENT_BUILDER_FACTORY_IMPL = "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl";
  
  private XPathCollationSupport fXPathCollationSupport = null;
  

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
		  
	      Expression arg2 = getArg2();
	      
	      fXPathCollationSupport = xctxt.getXPathCollationSupport();
		  
	      String collationUri = null;
	      
		  if (arg2 != null) {
			 // A collation uri was, explicitly provided during the function call fn:deep-equal
		     XObject collationXObj = arg2.execute(xctxt);
		     collationUri = XslTransformEvaluationHelper.getStrVal(collationXObj); 			 			 
		  }
		  else {
			 collationUri = xctxt.getDefaultCollation(); 
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
					 
					 if ((item1 instanceof XSString) && (item2 instanceof XSString)) {
						 String str1 = ((((XSString)item1))).stringValue();
						 String str2 = ((((XSString)item2))).stringValue();
						 int strComparisonResult = fXPathCollationSupport.compareStringsUsingCollation(str1, 
                                                                                                       str2, collationUri);
                         if (strComparisonResult != 0) {
                            isDeepEqual = false;
                            break;
                         }
				     }
					 else if ((item1 instanceof XSString) && (item2 instanceof XString)) {
						String str1 = ((((XSString)item1))).stringValue();
						String str2 = (((XString)item2)).str();
					    int strComparisonResult = fXPathCollationSupport.compareStringsUsingCollation(str1, 
                                                                                                      str2, collationUri);
                        if (strComparisonResult != 0) {
                           isDeepEqual = false;
                           break;
                        }
					}
                    else if ((item1 instanceof XString) && (item2 instanceof XSString)) {
                    	String str1 = (((XString)item1)).str();
                    	String str2 = ((((XSString)item2))).stringValue();
					    int strComparisonResult = fXPathCollationSupport.compareStringsUsingCollation(str1, 
                                                                                                      str2, collationUri);
                        if (strComparisonResult != 0) {
                           isDeepEqual = false;
                           break;
                        }
					}
                    else if ((item1 instanceof XString) && (item2 instanceof XString)) {
                    	String str1 = (((XString)item1)).str();
                    	String str2 = (((XString)item2)).str();
					    int strComparisonResult = fXPathCollationSupport.compareStringsUsingCollation(str1, 
                                                                                                      str2, collationUri);
                        if (strComparisonResult != 0) {
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
			     	     if (!isTwoXmlDomNodesEqual(node1, node2, collationUri)) {
			     	        isDeepEqual = false;
		                 	break; 
			     	     }	 
					}
	                else if (!item1.vcEquals(item2, null, true)) {
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
  private boolean isTwoXmlDomNodesEqual(Node node1, Node node2, String collationUri) throws Exception {
	 boolean isTwoDomNodesEqual = true;
	 
	 if ((node1.getNodeType() == Node.ELEMENT_NODE) && 
			                                     (node2.getNodeType() == Node.ELEMENT_NODE)) {
		String xmlStr1 = XslTransformEvaluationHelper.serializeXmlDomElementNode(node1);
		String xmlStr2 = XslTransformEvaluationHelper.serializeXmlDomElementNode(node2);
		isTwoDomNodesEqual = isTwoXmlDocumentStrEqual(xmlStr1, xmlStr2, collationUri);
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
		   int strComparisonResult = fXPathCollationSupport.compareStringsUsingCollation(node1StrVal, 
					                                                                     node2StrVal, collationUri);
		   if (strComparisonResult != 0) {
			  isTwoDomNodesEqual = false;  
		   }
		}		
	 }
	 else if ((node1.getNodeType() == Node.TEXT_NODE) && 
			                                       (node2.getNodeType() == Node.TEXT_NODE)) {
		String node1StrVal = node1.getNodeValue();
		String node2StrVal = node2.getNodeValue();
		int strComparisonResult = fXPathCollationSupport.compareStringsUsingCollation(node1StrVal, 
					                                                                  node2StrVal, collationUri);
		if (strComparisonResult != 0) {
		   isTwoDomNodesEqual = false;  
		}
	 }
	 else if ((node1.getNodeType() == Node.COMMENT_NODE) && 
			                                          (node2.getNodeType() == Node.COMMENT_NODE)) {
		String node1StrVal = node1.getNodeValue();
		String node2StrVal = node2.getNodeValue();
		int strComparisonResult = fXPathCollationSupport.compareStringsUsingCollation(node1StrVal, 
						                                                              node2StrVal, collationUri);
		if (strComparisonResult != 0) {
		   isTwoDomNodesEqual = false;  
		}
	 }
	 
	 return isTwoDomNodesEqual; 
  }
  
  /*
   * Check whether two XML document strings are equal.
   */
  private boolean isTwoXmlDocumentStrEqual(String xmlStr1, String xmlStr2, String collationUri) 
		                                                               throws Exception {
	 boolean isTwoXmlDomElementNodesEqual = true;
	 
	 // Using Xerces-J DOM parser api, to construct an XML document object 
	 // from an XML string value.
	 System.setProperty(JAXP_XML_DOCUMENT_BUILDER_FACTORY_NAME, JAXP_XML_DOCUMENT_BUILDER_FACTORY_IMPL);
	 
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
  
}
