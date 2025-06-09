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
package org.apache.xpath.operations;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xalan.xslt.util.XslTransformSharedDatastore;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xpath.XPath;
import org.apache.xpath.composite.SequenceTypeArrayTest;
import org.apache.xpath.composite.SequenceTypeData;
import org.apache.xpath.composite.SequenceTypeKindTest;
import org.apache.xpath.composite.SequenceTypeMapTest;
import org.apache.xpath.composite.SequenceTypeSupport;
import org.apache.xpath.composite.SequenceTypeSupport.OccurrenceIndicator;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;
import org.apache.xpath.types.XSByte;
import org.apache.xpath.types.XSGDay;
import org.apache.xpath.types.XSGMonth;
import org.apache.xpath.types.XSGMonthDay;
import org.apache.xpath.types.XSGYear;
import org.apache.xpath.types.XSGYearMonth;
import org.apache.xpath.types.XSNegativeInteger;
import org.apache.xpath.types.XSNonNegativeInteger;
import org.apache.xpath.types.XSNonPositiveInteger;
import org.apache.xpath.types.XSPositiveInteger;
import org.apache.xpath.types.XSShort;
import org.apache.xpath.types.XSUnsignedByte;
import org.apache.xpath.types.XSUnsignedInt;
import org.apache.xpath.types.XSUnsignedLong;
import org.apache.xpath.types.XSUnsignedShort;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import xml.xpath31.processor.types.XSAnyURI;
import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSDate;
import xml.xpath31.processor.types.XSDateTime;
import xml.xpath31.processor.types.XSDayTimeDuration;
import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSDuration;
import xml.xpath31.processor.types.XSFloat;
import xml.xpath31.processor.types.XSInt;
import xml.xpath31.processor.types.XSInteger;
import xml.xpath31.processor.types.XSLong;
import xml.xpath31.processor.types.XSNormalizedString;
import xml.xpath31.processor.types.XSQName;
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSTime;
import xml.xpath31.processor.types.XSToken;
import xml.xpath31.processor.types.XSUntyped;
import xml.xpath31.processor.types.XSUntypedAtomic;
import xml.xpath31.processor.types.XSYearMonthDuration;

/**
 * The XPath 3.1 "instance of" operation.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class InstanceOf extends Operation
{

   private static final long serialVersionUID = -5941900193967481806L;

   /**
   * Apply the operation to two operands, and return the result.
   *
   * @param left non-null reference to the evaluated left operand.
   * @param right non-null reference to the evaluated right operand.
   *
   * @return non-null reference to the XObject that represents the result of the operation.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject operate(XObject left, XObject right) 
                                                 throws javax.xml.transform.TransformerException
  {
      boolean result = false;
      
      SequenceTypeData seqTypedData = (SequenceTypeData)right;
      
      int builtInSeqType = seqTypedData.getBuiltInSequenceType();
      
      if ((left instanceof ResultSequence) && ((ResultSequence)left).size() == 0) {
    	 if ((seqTypedData.getItemTypeOccurrenceIndicator() == SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_ONE) || 
    		 (seqTypedData.getItemTypeOccurrenceIndicator() == SequenceTypeSupport.OccurrenceIndicator.ZERO_OR_MANY)) {
    		 return XBoolean.S_TRUE; 
    	 }
    	 else if (builtInSeqType == SequenceTypeSupport.EMPTY_SEQUENCE) {
    		 return XBoolean.S_TRUE;
    	 }
    	 else if (seqTypedData.getItemTypeOccurrenceIndicator() == SequenceTypeSupport.OccurrenceIndicator.ABSENT) {
    		 return XBoolean.S_FALSE;
    	 }
      }
      
      try {
         result = isInstanceOf(left, seqTypedData);
      }
      catch (Exception ex) {    	 
    	 result = false; 
      }
      
      return ((result == true) ? XBoolean.S_TRUE : XBoolean.S_FALSE);
  }

  /**
   * This method checks whether, an xdm value is an instance of 
   * a specific type.
   */
  private boolean isInstanceOf(XObject xdmValue, SequenceTypeData seqTypeData) throws ParserConfigurationException, SAXException, 
                                                                                                             IOException, TransformerException, Exception {
    
      boolean isInstanceOf = false;
      
      if ((xdmValue instanceof XSUntypedAtomic) && 
          (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_UNTYPED_ATOMIC)) {
          isInstanceOf = true;  
      }
      else if ((xdmValue instanceof XSUntyped) && 
              (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_UNTYPED)) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XString || xdmValue instanceof XSString) && 
              ((seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.STRING) || (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSNormalizedString) && ((seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_NORMALIZED_STRING) || 
    		                                                (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSToken) && ((seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_TOKEN) || 
    		                                     (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSAnyURI) && ((seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_URI) || 
    		                                      (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSQName) && ((seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_QNAME) || 
                                                 (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if (((xdmValue instanceof XBoolean) || (xdmValue instanceof XSBoolean)) && ((seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.BOOLEAN) || 
    		                                                                           (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if (xdmValue instanceof XNumber) {          
    	  if (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE) {
    		 isInstanceOf = true;
    	  }
    	  else if ((seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_DECIMAL) ||
    			   (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_DOUBLE)) {
             isInstanceOf = true; 
          }
          else {
             double doubleVal = ((XNumber)xdmValue).num();
             if ((doubleVal == (int)doubleVal) && (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_INTEGER)) {
            	 isInstanceOf = true; 
             }
             else if ((doubleVal == (float)doubleVal) && (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_FLOAT)) {
            	 isInstanceOf = true; 
             }
          }    	  
      }
      else if ((xdmValue instanceof XSDouble) && ((seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_DOUBLE) || 
    		                                      (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
         isInstanceOf = true; 
      }
      else if ((xdmValue instanceof XSFloat) && ((seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_FLOAT) || 
    		                                     (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if (((xdmValue instanceof XSInteger) || (xdmValue instanceof XSNonNegativeInteger) || 
    		    (xdmValue instanceof XSPositiveInteger) || (xdmValue instanceof XSNonPositiveInteger) || 
    		    (xdmValue instanceof XSNegativeInteger) || (xdmValue instanceof XSLong) || 
    		    (xdmValue instanceof XSInt) || (xdmValue instanceof XSShort)) && 
    		     ((seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_INTEGER) ||                                                   
                  (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {          
    	  isInstanceOf = true;                    
      }
      else if (((xdmValue instanceof XSLong) || (xdmValue instanceof XSInt) || (xdmValue instanceof XSShort)) && 
    		                                      ((seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_LONG) || 
                                                   (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {          
    	  isInstanceOf = true;
      }
      else if (((xdmValue instanceof XSInt) || (xdmValue instanceof XSShort)) && 
    		                                     ((seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_INT) || 
    		                                      (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {    	  
    	  isInstanceOf = true;
      }      
      else if ((xdmValue instanceof XSShort) && ((seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_SHORT) || 
                                                 (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSByte) && ((seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_BYTE) || 
                                                (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSUnsignedLong) && ((seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_UNSIGNED_LONG) || 
                                                        (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSUnsignedInt) && ((seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_UNSIGNED_INT) || 
                                                       (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSUnsignedShort) && ((seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_UNSIGNED_SHORT) || 
                                                         (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSUnsignedByte) && ((seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_UNSIGNED_BYTE) || 
                                                        (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if (((xdmValue instanceof XSNonNegativeInteger) || (xdmValue instanceof XSPositiveInteger)) && 
    		                                                   ((seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_NON_NEGATIVE_INTEGER) || 
                                                                (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {    	  
    	  isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSPositiveInteger) && ((seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_POSITIVE_INTEGER) || 
                                                           (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if (((xdmValue instanceof XSNonPositiveInteger) || (xdmValue instanceof XSNegativeInteger)) && 
    		   ((seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_NON_POSITIVE_INTEGER) || 
                (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSNegativeInteger) && ((seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_NEGATIVE_INTEGER) || 
                                                           (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }      
      else if ((xdmValue instanceof XSDecimal) && ((seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_DECIMAL) || 
    		                                       (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSDate) && ((seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_DATE) || 
    		                                    (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSDateTime) && ((seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_DATETIME) || 
    		                                        (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSTime) && ((seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_TIME) || 
    		                                    (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSDuration) && ((seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_DURATION) || 
    		                                        (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSDayTimeDuration) && ((seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_DAYTIME_DURATION) || 
    		                                               (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSYearMonthDuration) && ((seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_YEARMONTH_DURATION) || 
    		                                                 (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSGYearMonth) && (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_GYEAR_MONTH)) {
          isInstanceOf = true;  
      }
      else if ((xdmValue instanceof XSGYear) && (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_GYEAR)) {
          isInstanceOf = true;  
      }
      else if ((xdmValue instanceof XSGMonthDay) && (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_GMONTH_DAY)) {
          isInstanceOf = true;  
      }
      else if ((xdmValue instanceof XSGDay) && (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_GDAY)) {
          isInstanceOf = true;  
      }
      else if ((xdmValue instanceof XSGMonth) && (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_GMONTH)) {
          isInstanceOf = true;  
      }
      else if (xdmValue instanceof XMLNodeCursorImpl) {
    	  XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xdmValue;
    	  if (xmlNodeCursorImpl.m_is_for_each_group) {
    		  // This XMLNodeCursorImpl object is constructed via xsl:for-each-group 
    		  // instruction, to group a sequence of atomic values.
    		  
    		  DTMCursorIterator iter = xmlNodeCursorImpl.getContainedIter();    	  
    		  int node = iter.nextNode();
    		  DTM dtm = iter.getDTM(node);
    		  short nodeType = dtm.getNodeType(node);
    		  if (nodeType == DTM.TEXT_NODE) {
    			  Node nodeObj = dtm.getNode(node);
    			  java.lang.String nodeStrValue = nodeObj.getNodeValue();
    			  Double dblValue = null;
    			  XObject xObj = null;
    			  try {
    				  dblValue = Double.valueOf(nodeStrValue);
    			  }
    			  catch (NumberFormatException ex) {
    				  // NO OP 
    			  }

    			  if (dblValue != null) {
    				  xObj = new XSDecimal(BigDecimal.valueOf(dblValue));
    			  }
    			  else {
    				  xObj = new XSString(nodeStrValue);;  
    			  }

    			  XObject result = SequenceTypeSupport.castXdmValueToAnotherType(xObj, seqTypeData, true);
    			  if (result != null) {
    				  isInstanceOf = true; 
    			  }
    		  }
    	  }
    	  else {
             isInstanceOf = isNodesetInstanceOfType((XMLNodeCursorImpl)xdmValue, seqTypeData);
    	  }
      }
      else if (xdmValue instanceof ResultSequence) {
          isInstanceOf = isSequenceInstanceOfType((ResultSequence)xdmValue, seqTypeData); 
      }
      else if (xdmValue instanceof XPathMap) {
    	  isInstanceOf = isXdmMapConformsWithSeqType((XPathMap)xdmValue, seqTypeData);
      }
      else if (xdmValue instanceof XPathArray) {
    	  isInstanceOf = isXdmArrayConformsWithSeqType((XPathArray)xdmValue, seqTypeData);
      }
    
      return isInstanceOf;
  }

  /**
   * This method checks whether, an xdm nodeset is an instance of 
   * a specific type.
   */
  private boolean isNodesetInstanceOfType(XMLNodeCursorImpl nodeSet, SequenceTypeData seqTypeData) throws 
                                                                         ParserConfigurationException, SAXException, 
                                                                         IOException, TransformerException, Exception {
	  
	  boolean isInstanceOf = false;
          
	  int nodeSetLen = nodeSet.getLength();          
	  int itemTypeOccurenceIndicator = seqTypeData.getItemTypeOccurrenceIndicator();	  

	  if ((nodeSetLen > 1) && ((itemTypeOccurenceIndicator == 0) || (itemTypeOccurenceIndicator == OccurrenceIndicator.ZERO_OR_ONE))) {
		  isInstanceOf = false; 
	  }
	  else {
		  DTMCursorIterator dtmIter = nodeSet.iterRaw();

		  List<Boolean> nodeSetSequenceTypeKindTestResultList = new ArrayList<Boolean>();

		  int nextNodeDtmHandle;
		  while ((nextNodeDtmHandle = dtmIter.nextNode()) != DTM.NULL) {                 
			  DTM dtm = dtmIter.getDTM(nextNodeDtmHandle);
			  java.lang.String nodeName = dtm.getNodeName(nextNodeDtmHandle);
			  java.lang.String nodeNsUri = dtm.getNamespaceURI(nextNodeDtmHandle);

			  if (dtm.getNodeType(nextNodeDtmHandle) == DTM.DOCUMENT_NODE) {
				  SequenceTypeKindTest seqTypeKindTest = seqTypeData.getSequenceTypeKindTest();				  
				  if ((seqTypeKindTest != null) && (seqTypeKindTest.getKindVal() == SequenceTypeSupport.DOCUMENT_KIND)) {
					  nodeSetSequenceTypeKindTestResultList.add(Boolean.valueOf(true)); 
				  }
				  else {
					  isInstanceOf = false;
					  break;
				  }
			  }
			  else if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ELEMENT_NODE) {
				  SequenceTypeKindTest seqTypeKindTest = seqTypeData.getSequenceTypeKindTest();				  
				  if (seqTypeKindTest != null) {
					  java.lang.String elemNodeKindTestNodeName = seqTypeKindTest.getNodeLocalName();
					  if (elemNodeKindTestNodeName == null || "".equals(elemNodeKindTestNodeName) || 
							  																SequenceTypeSupport.STAR.equals(elemNodeKindTestNodeName)) {
						  elemNodeKindTestNodeName = nodeName;  
					  }

					  if ((seqTypeKindTest.getKindVal() == SequenceTypeSupport.ELEMENT_KIND) && (nodeName.equals(elemNodeKindTestNodeName)) 
							                                                       && (SequenceTypeSupport.isTwoXmlNamespaceValuesEqual(nodeNsUri, 
							                                                    		                                       seqTypeKindTest.getNodeNsUri()))) {
						  XSTypeDefinition xsTypeDefn = seqTypeData.getXsTypeDefinition();
						  if (xsTypeDefn != null) {
							  XMLNodeCursorImpl node = new XMLNodeCursorImpl(nextNodeDtmHandle, dtmIter.getDTMManager());
							  if (SequenceTypeSupport.isXdmElemNodeValidWithSchemaType(node, m_xctxt, xsTypeDefn)) {
								  nodeSetSequenceTypeKindTestResultList.add(Boolean.valueOf(true));
							  }
							  else {
								  isInstanceOf = false;
								  break;
							  }
						  }
						  else if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(seqTypeKindTest.getDataTypeUri())) {
							  XMLNodeCursorImpl node = new XMLNodeCursorImpl(nextNodeDtmHandle, dtmIter.getDTMManager());
							  
							  // Check whether this element node has complexContent (i.e, presence of 
							  // child element or attribute). If 'yes' then instance of check will 
							  // be false for this case.
							  node = (XMLNodeCursorImpl)(node.getFresh());
							  DTMCursorIterator dtmIter1 = ((XMLNodeCursorImpl)node).iterRaw();
							  int nodeHandle = dtmIter1.nextNode();
							  DTM dtm1 = m_xctxt.getDTM(nodeHandle);
							  Node node1 = dtm1.getNode(nodeHandle);
							  							  
							  NodeList childNodes = node1.getChildNodes();
							  boolean isComplexContent = false;
							  for (int idx = 0; idx < childNodes.getLength(); idx++) {
								  Node childNode = childNodes.item(idx);
								  if (childNode.getNodeType() == Node.ELEMENT_NODE) {
									  isComplexContent = true;
									  break;
								  }
							  }
							  
							  if (!isComplexContent) {
								  NamedNodeMap attrNodes = node1.getAttributes();							  
								  for (int idx = 0; idx < attrNodes.getLength(); idx++) {
									  Node attrNode = attrNodes.item(idx);
									  java.lang.String nodeNameStr = attrNode.getNodeName();									
									  if (!"xmlns".equals(nodeNameStr)) {
										  isComplexContent = true;
										  break;
									  }
								  }
							  }
							  
							  if (isComplexContent) {
								  isInstanceOf = false;
								  break;
							  }
							  else {
								  dtmIter1.reset();
							  }
							  
							  java.lang.String dataTypeLocalName = seqTypeKindTest.getDataTypeLocalName();
							  
							  ElemTemplateElement elemTemplateElement = (ElemTemplateElement)m_xctxt.getNamespaceContext();
							  List<XMLNSDecl> prefixTable = null;
							  if (elemTemplateElement != null) {
								  prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
							  }
							  java.lang.String xmlSchemaNsPrefix = XslTransformEvaluationHelper.getPrefixFromNsUri(XMLConstants.
									                                                                                   W3C_XML_SCHEMA_NS_URI, prefixTable);
				              
							  java.lang.String xpathConstructorFuncExprStr = null;
							  if (xmlSchemaNsPrefix != null) {
								  xpathConstructorFuncExprStr = xmlSchemaNsPrefix + ":" + dataTypeLocalName + "('" + node.str() + "')";
								  xpathConstructorFuncExprStr += " instance of " + xmlSchemaNsPrefix + ":" + dataTypeLocalName;
							  }
							  else {
								  xpathConstructorFuncExprStr = "xs:" + dataTypeLocalName + "('" + node.str() + "')";
								  xpathConstructorFuncExprStr += " instance of xs:" + dataTypeLocalName; 
							  }
							  
							  XPath xpath = new XPath(xpathConstructorFuncExprStr, m_xctxt.getSAXLocator(), m_xctxt.getNamespaceContext(), 
                                      																 XPath.SELECT, null);
							  XObject xObj = null;
							  try {
							     xObj = xpath.executeInstanceOf(m_xctxt, DTM.NULL, null);
							     isInstanceOf = ((xObj.bool() == true) ? true : false);
							  }
							  catch (TransformerException ex) {
								  isInstanceOf = false;
								  break;
							  }
							  if (isInstanceOf) {
								 nodeSetSequenceTypeKindTestResultList.add(Boolean.valueOf(true)); 
							  }
							  else {
								 break; 
							  }
						  }
						  else {
							  nodeSetSequenceTypeKindTestResultList.add(Boolean.valueOf(true)); 
						  }
					  }
					  else if ((seqTypeKindTest.getKindVal() == SequenceTypeSupport.SCHEMA_ELEMENT_KIND) && (nodeName.equals(elemNodeKindTestNodeName)) 
                              																	&& (SequenceTypeSupport.isTwoXmlNamespaceValuesEqual(nodeNsUri, 
                              																				seqTypeKindTest.getNodeNsUri()))) {
						  StylesheetRoot stylesheetRoot = XslTransformSharedDatastore.stylesheetRoot;
						  XSModel xsModel = stylesheetRoot.getXsModel();
						  if (xsModel != null) {
							  XSElementDeclaration elemDecl = xsModel.getElementDeclaration(elemNodeKindTestNodeName, seqTypeKindTest.getNodeNsUri());
							  if (elemDecl != null) {
								 nodeSetSequenceTypeKindTestResultList.add(Boolean.valueOf(true)); 
							  }
							  else {
								 // When an XML input document has been validated with a schema but the schema 
								 // doesn't have a global element declaration for this element node, we 
								 // produce 'instance of' result as false, instead of emitting an XPath 
								 // dynamic error. 
								 isInstanceOf = false;
								 break; 
							  }
						  }
						  else {
							  // When an XML input document has not been validated with a schema, we produce 
							  // 'instance of' result as false, instead of emitting an XPath dynamic error.
							  isInstanceOf = false;
							  break; 
						  }
					  }
					  else if ((seqTypeKindTest.getKindVal() == SequenceTypeSupport.NODE_KIND) || 
							  (seqTypeKindTest.getKindVal() == SequenceTypeSupport.ITEM_KIND)) {
						  nodeSetSequenceTypeKindTestResultList.add(Boolean.valueOf(true)); 
					  }
				  }
				  else {
					  isInstanceOf = false;
					  break;
				  }
			  }
			  else if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ATTRIBUTE_NODE) {
				  SequenceTypeKindTest seqTypeKindTest = seqTypeData.getSequenceTypeKindTest();				  
				  if (seqTypeKindTest != null) {
					  java.lang.String attrNodeKindTestNodeName = seqTypeKindTest.getNodeLocalName();
					  if (attrNodeKindTestNodeName == null || "".equals(attrNodeKindTestNodeName) || 
							  SequenceTypeSupport.STAR.equals(attrNodeKindTestNodeName)) {
						  attrNodeKindTestNodeName = nodeName;  
					  }

					  if ((seqTypeKindTest.getKindVal() == SequenceTypeSupport.ATTRIBUTE_KIND) && (nodeName.equals(attrNodeKindTestNodeName)) 
																						  && (SequenceTypeSupport.isTwoXmlNamespaceValuesEqual(
																								  nodeNsUri, seqTypeKindTest.getNodeNsUri()))) {
						  XSTypeDefinition xsTypeDefn = seqTypeData.getXsTypeDefinition();
						  if (xsTypeDefn != null) {
							  if (xsTypeDefn instanceof XSSimpleType) {
								  XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)xsTypeDefn;
								  XMLNodeCursorImpl node = new XMLNodeCursorImpl(nextNodeDtmHandle, dtmIter.getDTMManager());
								  try {
								      xsSimpleTypeDecl.validate(node.str(), null, null);
								      nodeSetSequenceTypeKindTestResultList.add(Boolean.valueOf(true));
								  }
								  catch (InvalidDatatypeValueException ex) {
									  isInstanceOf = false;
									  break;
								  }
							  }
							  else {
								  isInstanceOf = false;
								  break; 
							  }
						  }
						  else if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(seqTypeKindTest.getDataTypeUri())) {
                              XMLNodeCursorImpl node = new XMLNodeCursorImpl(nextNodeDtmHandle, dtmIter.getDTMManager());							  
							  java.lang.String dataTypeLocalName = seqTypeKindTest.getDataTypeLocalName();
							  java.lang.String xpathConstructorFuncExprStr = "xs:" + dataTypeLocalName + "('" + node.str() + "')";
							  xpathConstructorFuncExprStr += " instance of xs:" + dataTypeLocalName; 							  
							  XPath xpath = new XPath(xpathConstructorFuncExprStr, m_xctxt.getSAXLocator(), m_xctxt.getNamespaceContext(), 
                                      																 XPath.SELECT, null);
							  XObject xObj = null;
							  try {
							     xObj = xpath.executeInstanceOf(m_xctxt, DTM.NULL, null);
							     isInstanceOf = ((xObj.bool() == true) ? true : false);
							  }
							  catch (TransformerException ex) {
								  isInstanceOf = false;
								  break;
							  }
							  if (isInstanceOf) {
								 nodeSetSequenceTypeKindTestResultList.add(Boolean.valueOf(true)); 
							  }
							  else {
								 break; 
							  } 
						  }
						  else {
						      nodeSetSequenceTypeKindTestResultList.add(Boolean.valueOf(true));
						  }
					  }
					  else if ((seqTypeKindTest.getKindVal() == SequenceTypeSupport.SCHEMA_ATTRIBUTE_KIND) && (nodeName.equals(attrNodeKindTestNodeName)) 
																								  && (SequenceTypeSupport.isTwoXmlNamespaceValuesEqual(
																										  nodeNsUri, seqTypeKindTest.getNodeNsUri()))) {
						  StylesheetRoot stylesheetRoot = XslTransformSharedDatastore.stylesheetRoot;
						  XSModel xsModel = stylesheetRoot.getXsModel();
						  if (xsModel != null) {
							  XSAttributeDeclaration attrDecl = xsModel.getAttributeDeclaration(attrNodeKindTestNodeName, seqTypeKindTest.getNodeNsUri());
							  if (attrDecl != null) {
								 nodeSetSequenceTypeKindTestResultList.add(Boolean.valueOf(true)); 
							  }
							  else {
								 // When an XML input document has been validated with a schema but the schema 
								 // doesn't have a global attribute declaration for this attribute node, we 
								 // produce 'instance of' result as false, instead of emitting an XPath 
								 // dynamic error. 
								 isInstanceOf = false;
								 break; 
							  }
						  }
						  else {
							  // When an XML input document has not been validated with a schema, we produce 
							  // 'instance of' result as false, instead of emitting an XPath dynamic error.   
							  isInstanceOf = false;
							  break; 
						  }
					  }
					  else if ((seqTypeKindTest.getKindVal() == SequenceTypeSupport.NODE_KIND) || 
							  (seqTypeKindTest.getKindVal() == SequenceTypeSupport.ITEM_KIND)) {
						  nodeSetSequenceTypeKindTestResultList.add(Boolean.valueOf(true));   
					  }   
				  }
				  else {
					  isInstanceOf = false;
					  break;
				  } 
			  }
			  else if (dtm.getNodeType(nextNodeDtmHandle) == DTM.TEXT_NODE) {
				  SequenceTypeKindTest seqTypeKindTest = seqTypeData.getSequenceTypeKindTest();				  
				  if (seqTypeKindTest.getKindVal() == SequenceTypeSupport.TEXT_KIND) {
					  nodeSetSequenceTypeKindTestResultList.add(Boolean.valueOf(true)); 
				  }
			  }
			  else if (dtm.getNodeType(nextNodeDtmHandle) == DTM.NAMESPACE_NODE) {
				  SequenceTypeKindTest seqTypeKindTest = seqTypeData.getSequenceTypeKindTest();				  
				  if (seqTypeKindTest.getKindVal() == SequenceTypeSupport.NAMESPACE_NODE_KIND) {
					  nodeSetSequenceTypeKindTestResultList.add(Boolean.valueOf(true)); 
				  }
			  }
		  }

		  if (nodeSetSequenceTypeKindTestResultList.size() > 0 && (nodeSetSequenceTypeKindTestResultList.size() == nodeSetLen)) {
			  isInstanceOf = true; 
		  }
	  }
	  
	  return isInstanceOf;
  }

  /**
   * This method checks whether, an xdm sequence is an instance of 
   * a specific type.
   */
  private boolean isSequenceInstanceOfType(ResultSequence srcResultSeq, SequenceTypeData seqTypeData) throws ParserConfigurationException, 
                                                                                                 SAXException, IOException, 
                                                                                                 TransformerException, Exception {
	  
	  boolean isInstanceOf = false;

	  int seqLen = srcResultSeq.size();

	  if ((seqLen == 0) && (seqTypeData.getItemTypeOccurrenceIndicator() == OccurrenceIndicator.ONE_OR_MANY)) {
		  isInstanceOf = false;  
	  }
	  else if ((seqLen > 0) && (seqTypeData.getBuiltInSequenceType() == SequenceTypeSupport.EMPTY_SEQUENCE)) {
		  isInstanceOf = false;  
	  }
	  else if ((seqLen > 1) && (seqTypeData.getItemTypeOccurrenceIndicator() == OccurrenceIndicator.ZERO_OR_ONE)) {
		  isInstanceOf = false;
	  }

	  SequenceTypeData sequenceTypeDataNew = new SequenceTypeData();          
	  if (seqTypeData.getSequenceTypeKindTest() != null) {
		  sequenceTypeDataNew.setSequenceTypeKindTest(seqTypeData.getSequenceTypeKindTest()); 
	  }
	  else {
		  sequenceTypeDataNew.setBuiltInSequenceType(seqTypeData.getBuiltInSequenceType()); 
	  }

	  boolean isInstanceOfOnSeqItem = true;

	  for (int idx = 0; idx < srcResultSeq.size(); idx++) {
		  XObject seqItem = (XObject)(srcResultSeq.item(idx));
		  // Recursive call to this function
		  if (!isInstanceOf(seqItem, sequenceTypeDataNew)) {
			  isInstanceOfOnSeqItem = false;
			  break;
		  }
	  }

	  isInstanceOf = isInstanceOfOnSeqItem;
	  
	  return isInstanceOf;
  }
  
  /**
   * This method checks whether, an xdm map conforms with the specified sequence type.
   */
  private boolean isXdmMapConformsWithSeqType(XPathMap map, SequenceTypeData seqTypeData) {
	  boolean isInstanceOf = false;
	  
	  SequenceTypeMapTest sequenceTypeMapTest = seqTypeData.getSequenceTypeMapTest();
	  if (sequenceTypeMapTest != null) {
		 if (sequenceTypeMapTest.isAnyMapTest()) {
		    isInstanceOf = true;
		 }
	  }
	  
	  return isInstanceOf; 
  }

  /**
   * This method checks whether, an xdm array conforms with the specified sequence type.
   */
  private boolean isXdmArrayConformsWithSeqType(XPathArray xpathArr, SequenceTypeData seqTypeData) {
	  
	  boolean isInstanceOf = false;
	  
	  SequenceTypeArrayTest sequenceTypeArrayTest = seqTypeData.getSequenceTypeArrayTest();
	  if (sequenceTypeArrayTest != null) {
		  if (sequenceTypeArrayTest.isAnyArrayTest()) {
			  isInstanceOf = true;
		  }
		  else {
			  List<XObject> nativeArr = xpathArr.getNativeArray();
			  Iterator<XObject> arrIter = nativeArr.iterator();
			  // We check below each of array items, with an expected type
			  isInstanceOf = true; 
			  while (arrIter.hasNext()) {
				  XObject arrItem = arrIter.next();
				  if (arrItem instanceof ResultSequence) {
					  arrItem = ((ResultSequence)arrItem).item(0);
				  }
				  SequenceTypeData arrayItemTypeInfo = sequenceTypeArrayTest.getArrayItemTypeInfo();
				  try {
					  XObject arrayItemTypeCheckResult = SequenceTypeSupport.castXdmValueToAnotherType(arrItem, null, arrayItemTypeInfo, null);
					  if (arrayItemTypeCheckResult == null) {             				
						  isInstanceOf = false;
						  break;
					  }
				  }
				  catch (TransformerException ex) {
					  isInstanceOf = false;
					  break; 
				  }
			  } 	
		  }
	  }
	  
	  return isInstanceOf;
  }
  
}
