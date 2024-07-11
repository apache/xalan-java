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
package org.apache.xpath.operations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xpath.composite.SequenceTypeArrayTest;
import org.apache.xpath.composite.SequenceTypeData;
import org.apache.xpath.composite.SequenceTypeKindTest;
import org.apache.xpath.composite.SequenceTypeMapTest;
import org.apache.xpath.composite.SequenceTypeSupport;
import org.apache.xpath.composite.SequenceTypeSupport.OccurenceIndicator;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;
import org.apache.xpath.types.XSByte;
import org.apache.xpath.types.XSNegativeInteger;
import org.apache.xpath.types.XSNonNegativeInteger;
import org.apache.xpath.types.XSNonPositiveInteger;
import org.apache.xpath.types.XSPositiveInteger;
import org.apache.xpath.types.XSShort;
import org.apache.xpath.types.XSUnsignedByte;
import org.apache.xpath.types.XSUnsignedInt;
import org.apache.xpath.types.XSUnsignedLong;
import org.apache.xpath.types.XSUnsignedShort;

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
      boolean isInstanceOf = false;
      
      SequenceTypeData seqTypedData = (SequenceTypeData)right;
      
      int seqType = seqTypedData.getSequenceType();
      if ((left instanceof ResultSequence) && ((ResultSequence)left).size() == 0) {
    	 if ((seqType != SequenceTypeSupport.EMPTY_SEQUENCE) && (seqType > 0)) {
    	    return XBoolean.S_FALSE; 
    	 }
      }
      
      isInstanceOf = isInstanceOf(left, seqTypedData);
      
      return isInstanceOf ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }

  /**
   * This method checks whether, an xdm value is an instance of 
   * a specific type.
   */
  private boolean isInstanceOf(XObject xdmValue, SequenceTypeData seqTypeData) {
    
    boolean isInstanceOf = false;
      
    if ((xdmValue instanceof XSUntypedAtomic) && 
          (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_UNTYPED_ATOMIC)) {
          isInstanceOf = true;  
      }
      else if ((xdmValue instanceof XSUntyped) && 
              (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_UNTYPED)) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XString || xdmValue instanceof XSString) && 
              ((seqTypeData.getSequenceType() == SequenceTypeSupport.STRING) || (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSNormalizedString) && ((seqTypeData.getSequenceType() == SequenceTypeSupport.XS_NORMALIZED_STRING) || 
    		                                                (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSToken) && ((seqTypeData.getSequenceType() == SequenceTypeSupport.XS_TOKEN) || 
    		                                     (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSAnyURI) && ((seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_URI) || 
    		                                      (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSQName) && ((seqTypeData.getSequenceType() == SequenceTypeSupport.XS_QNAME) || 
                                                 (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if (((xdmValue instanceof XBoolean) || (xdmValue instanceof XSBoolean)) && ((seqTypeData.getSequenceType() == SequenceTypeSupport.BOOLEAN) || 
    		                                                                           (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if (xdmValue instanceof XNumber) {          
          if ((seqTypeData.getSequenceType() == SequenceTypeSupport.XS_DOUBLE) || 
        	  (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_DECIMAL) || 
        	  (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE)) {
             isInstanceOf = true; 
          }
          else {
             double doubleVal = ((XNumber)xdmValue).num();
             if ((doubleVal == (int)doubleVal) && ((seqTypeData.getSequenceType() == SequenceTypeSupport.XS_INTEGER) ||            		                               
                                                   (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {            	 
                 // Revisit
            	 isInstanceOf = true; 
             }
          }
      }
      else if ((xdmValue instanceof XSDouble) && ((seqTypeData.getSequenceType() == SequenceTypeSupport.XS_DOUBLE) || 
    		                                      (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
         isInstanceOf = true; 
      }
      else if ((xdmValue instanceof XSFloat) && ((seqTypeData.getSequenceType() == SequenceTypeSupport.XS_FLOAT) || 
    		                                     (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if (((xdmValue instanceof XSInteger) || (xdmValue instanceof XSNonNegativeInteger) || 
    		    (xdmValue instanceof XSPositiveInteger) || (xdmValue instanceof XSNonPositiveInteger) || 
    		    (xdmValue instanceof XSNegativeInteger) || (xdmValue instanceof XSLong) || 
    		    (xdmValue instanceof XSInt) || (xdmValue instanceof XSShort)) && 
    		     ((seqTypeData.getSequenceType() == SequenceTypeSupport.XS_INTEGER) ||                                                   
                  (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {          
    	  isInstanceOf = true;                    
      }
      else if (((xdmValue instanceof XSLong) || (xdmValue instanceof XSInt) || (xdmValue instanceof XSShort)) && 
    		                                      ((seqTypeData.getSequenceType() == SequenceTypeSupport.XS_LONG) || 
                                                   (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {          
    	  isInstanceOf = true;
      }
      else if (((xdmValue instanceof XSInt) || (xdmValue instanceof XSShort)) && 
    		                                     ((seqTypeData.getSequenceType() == SequenceTypeSupport.XS_INT) || 
    		                                      (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {    	  
    	  isInstanceOf = true;
      }      
      else if ((xdmValue instanceof XSShort) && ((seqTypeData.getSequenceType() == SequenceTypeSupport.XS_SHORT) || 
                                                 (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSByte) && ((seqTypeData.getSequenceType() == SequenceTypeSupport.XS_BYTE) || 
                                                (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSUnsignedLong) && ((seqTypeData.getSequenceType() == SequenceTypeSupport.XS_UNSIGNED_LONG) || 
                                                        (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSUnsignedInt) && ((seqTypeData.getSequenceType() == SequenceTypeSupport.XS_UNSIGNED_INT) || 
                                                       (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSUnsignedShort) && ((seqTypeData.getSequenceType() == SequenceTypeSupport.XS_UNSIGNED_SHORT) || 
                                                         (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSUnsignedByte) && ((seqTypeData.getSequenceType() == SequenceTypeSupport.XS_UNSIGNED_BYTE) || 
                                                        (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if (((xdmValue instanceof XSNonNegativeInteger) || (xdmValue instanceof XSPositiveInteger)) && 
    		                                                   ((seqTypeData.getSequenceType() == SequenceTypeSupport.XS_NON_NEGATIVE_INTEGER) || 
                                                                (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {    	  
    	  isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSPositiveInteger) && ((seqTypeData.getSequenceType() == SequenceTypeSupport.XS_POSITIVE_INTEGER) || 
                                                           (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if (((xdmValue instanceof XSNonPositiveInteger) || (xdmValue instanceof XSNegativeInteger)) && 
    		   ((seqTypeData.getSequenceType() == SequenceTypeSupport.XS_NON_POSITIVE_INTEGER) || 
                (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSNegativeInteger) && ((seqTypeData.getSequenceType() == SequenceTypeSupport.XS_NEGATIVE_INTEGER) || 
                                                           (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }      
      else if ((xdmValue instanceof XSDecimal) && ((seqTypeData.getSequenceType() == SequenceTypeSupport.XS_DECIMAL) || 
    		                                       (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSDate) && ((seqTypeData.getSequenceType() == SequenceTypeSupport.XS_DATE) || 
    		                                    (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSDateTime) && ((seqTypeData.getSequenceType() == SequenceTypeSupport.XS_DATETIME) || 
    		                                        (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSTime) && ((seqTypeData.getSequenceType() == SequenceTypeSupport.XS_TIME) || 
    		                                    (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSDuration) && ((seqTypeData.getSequenceType() == SequenceTypeSupport.XS_DURATION) || 
    		                                        (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSDayTimeDuration) && ((seqTypeData.getSequenceType() == SequenceTypeSupport.XS_DAYTIME_DURATION) || 
    		                                               (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSYearMonthDuration) && ((seqTypeData.getSequenceType() == SequenceTypeSupport.XS_YEARMONTH_DURATION) || 
    		                                                 (seqTypeData.getSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if (xdmValue instanceof XNodeSet) {
          isInstanceOf = checkXdmNodesetWithType(xdmValue, seqTypeData);
      }
      else if (xdmValue instanceof ResultSequence) {
          isInstanceOf = checkXdmSequenceWithType(xdmValue, seqTypeData); 
      }
      else if (xdmValue instanceof XPathMap) {
    	  isInstanceOf = isInstanceOfMap(xdmValue, seqTypeData);
      }
      else if (xdmValue instanceof XPathArray) {
    	  isInstanceOf = isInstanceOfArray(xdmValue, seqTypeData);
      }
    
     return isInstanceOf;
  }

  /**
   * Check whether, an xdm node set conforms to xpath sequence type information.
   */
  private boolean checkXdmNodesetWithType(XObject xdmValue, SequenceTypeData seqTypeData) {
	
	  boolean isInstanceOf = false;

	  XNodeSet xdmNodeSet = (XNodeSet)xdmValue;          
	  int nodeSetLen = xdmNodeSet.getLength();          
	  int itemTypeOccurenceIndicator = seqTypeData.getItemTypeOccurrenceIndicator();
	  SequenceTypeKindTest seqTypeKindTest = seqTypeData.getSequenceTypeKindTest();

	  if ((nodeSetLen > 1) && ((itemTypeOccurenceIndicator == 0) || (itemTypeOccurenceIndicator == OccurenceIndicator.ZERO_OR_ONE))) {
		  isInstanceOf = false; 
	  }
	  else {
		  DTMIterator dtmIter = xdmNodeSet.iterRaw();

		  List<Boolean> nodeSetSequenceTypeKindTestResultList = new ArrayList<Boolean>();

		  int nextNodeDtmHandle;
		  while ((nextNodeDtmHandle = dtmIter.nextNode()) != DTM.NULL) {                 
			  DTM dtm = dtmIter.getDTM(nextNodeDtmHandle);
			  java.lang.String nodeName = dtm.getNodeName(nextNodeDtmHandle);
			  java.lang.String nodeNsUri = dtm.getNamespaceURI(nextNodeDtmHandle);

			  if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ELEMENT_NODE) {
				  if (seqTypeKindTest != null) {
					  java.lang.String elemNodeKindTestNodeName = seqTypeKindTest.getNodeLocalName();
					  if (elemNodeKindTestNodeName == null || "".equals(elemNodeKindTestNodeName) || 
							  SequenceTypeSupport.STAR.equals(elemNodeKindTestNodeName)) {
						  elemNodeKindTestNodeName = nodeName;  
					  }

					  if ((seqTypeKindTest.getKindVal() == SequenceTypeSupport.ELEMENT_KIND) && (nodeName.equals(elemNodeKindTestNodeName)) 
							  && (SequenceTypeSupport.isTwoXmlNamespacesEqual(nodeNsUri, seqTypeKindTest.getNodeNsUri()))) {
						  nodeSetSequenceTypeKindTestResultList.add(Boolean.valueOf(true));  
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
				  if (seqTypeKindTest != null) {
					  java.lang.String attrNodeKindTestNodeName = seqTypeKindTest.getNodeLocalName();
					  if (attrNodeKindTestNodeName == null || "".equals(attrNodeKindTestNodeName) || 
							  SequenceTypeSupport.STAR.equals(attrNodeKindTestNodeName)) {
						  attrNodeKindTestNodeName = nodeName;  
					  }

					  if ((seqTypeKindTest.getKindVal() == SequenceTypeSupport.ATTRIBUTE_KIND) && (nodeName.equals(attrNodeKindTestNodeName)) 
							  && (SequenceTypeSupport.isTwoXmlNamespacesEqual(
									  nodeNsUri, seqTypeKindTest.getNodeNsUri()))) {
						  nodeSetSequenceTypeKindTestResultList.add(Boolean.valueOf(true));    
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
				  if (seqTypeKindTest.getKindVal() == SequenceTypeSupport.TEXT_KIND) {
					  nodeSetSequenceTypeKindTestResultList.add(Boolean.valueOf(true)); 
				  }
			  }
			  else if (dtm.getNodeType(nextNodeDtmHandle) == DTM.NAMESPACE_NODE) {
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
   * Check whether, an xdm sequence conforms to xpath sequence type information.
   */
  private boolean checkXdmSequenceWithType(XObject xdmValue, SequenceTypeData seqTypeData) {
	  
	  boolean isInstanceOf = false;
	  
	  ResultSequence srcResultSeq = (ResultSequence)xdmValue;

	  int seqLen = srcResultSeq.size();

	  if ((seqLen == 0) && (seqTypeData.getItemTypeOccurrenceIndicator() == OccurenceIndicator.ONE_OR_MANY)) {
		  isInstanceOf = false;  
	  }
	  else if ((seqLen > 0) && (seqTypeData.getSequenceType() == SequenceTypeSupport.EMPTY_SEQUENCE)) {
		  isInstanceOf = false;  
	  }
	  else if ((seqLen > 1) && (seqTypeData.getItemTypeOccurrenceIndicator() == OccurenceIndicator.ZERO_OR_ONE)) {
		  isInstanceOf = false;
	  }

	  SequenceTypeData sequenceTypeDataNew = new SequenceTypeData();          
	  if (seqTypeData.getSequenceTypeKindTest() != null) {
		  sequenceTypeDataNew.setSequenceTypeKindTest(seqTypeData.getSequenceTypeKindTest()); 
	  }
	  else {
		  sequenceTypeDataNew.setSequenceType(seqTypeData.getSequenceType()); 
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
   * Check whether, an xdm value is an instance of xpath map.
   */
  private boolean isInstanceOfMap(XObject xdmValue, SequenceTypeData seqTypeData) {
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
   * Check whether, an xdm value is an instance of xpath array.
   */
  private boolean isInstanceOfArray(XObject xdmValue, SequenceTypeData seqTypeData) {
	  
	  boolean isInstanceOf = false;
	  
	  SequenceTypeArrayTest sequenceTypeArrayTest = seqTypeData.getSequenceTypeArrayTest();
	  if (sequenceTypeArrayTest != null) {
		  if (sequenceTypeArrayTest.isAnyArrayTest()) {
			  isInstanceOf = true;
		  }
		  else {
			  XPathArray xpathArr = (XPathArray)xdmValue;
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
					  XObject arrayItemTypeCheckResult = SequenceTypeSupport.convertXdmValueToAnotherType(arrItem, null, arrayItemTypeInfo, null);
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
