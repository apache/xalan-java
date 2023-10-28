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
import java.util.List;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xpath.composite.SequenceTypeData;
import org.apache.xpath.composite.SequenceTypeKindTest;
import org.apache.xpath.composite.SequenceTypeSupport;
import org.apache.xpath.composite.SequenceTypeSupport.OccurenceIndicator;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

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
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSTime;
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
      
      isInstanceOf = isInstanceOf(left, seqTypedData);
      
      return isInstanceOf ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }

  /**
   * This method checks whether, an xdm value is an instance of 
   * a specific type.
   */
  private boolean isInstanceOf(XObject xdmValue, SequenceTypeData seqTypedData) {
    
    boolean isInstanceOf = false;
      
    if ((xdmValue instanceof XSUntypedAtomic) && 
          (seqTypedData.getSequenceType() == SequenceTypeSupport.XS_UNTYPED_ATOMIC) && 
          ((seqTypedData.getItemTypeOccurrenceIndicator() == 0) || 
                                                     (seqTypedData.getItemTypeOccurrenceIndicator() == SequenceTypeSupport.OccurenceIndicator.ZERO_OR_ONE))) {
          isInstanceOf = true;  
      }
      else if ((xdmValue instanceof XSUntyped) && 
              (seqTypedData.getSequenceType() == SequenceTypeSupport.XS_UNTYPED) && 
              ((seqTypedData.getItemTypeOccurrenceIndicator() == 0) || 
                                                         (seqTypedData.getItemTypeOccurrenceIndicator() == SequenceTypeSupport.OccurenceIndicator.ZERO_OR_ONE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XString || xdmValue instanceof XSString) && 
              (seqTypedData.getSequenceType() == SequenceTypeSupport.STRING) && 
              ((seqTypedData.getItemTypeOccurrenceIndicator() == 0) || 
                                                         (seqTypedData.getItemTypeOccurrenceIndicator() == SequenceTypeSupport.OccurenceIndicator.ZERO_OR_ONE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XBoolean || xdmValue instanceof XSBoolean) && 
              (seqTypedData.getSequenceType() == SequenceTypeSupport.BOOLEAN) && 
              ((seqTypedData.getItemTypeOccurrenceIndicator() == 0) || 
                                                         (seqTypedData.getItemTypeOccurrenceIndicator() == SequenceTypeSupport.OccurenceIndicator.ZERO_OR_ONE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XNumber) && ((seqTypedData.getItemTypeOccurrenceIndicator() == 0) || 
                                             (seqTypedData.getItemTypeOccurrenceIndicator() == SequenceTypeSupport.OccurenceIndicator.ZERO_OR_ONE))) {          
          if ((seqTypedData.getSequenceType() == SequenceTypeSupport.XS_DOUBLE) || (seqTypedData.getSequenceType() == SequenceTypeSupport.XS_DECIMAL)) {
             isInstanceOf = true; 
          }
          else {
             double doubleVal = ((XNumber)xdmValue).num();
             if ((doubleVal == (int)doubleVal) && ((seqTypedData.getSequenceType() == SequenceTypeSupport.XS_INTEGER) || 
                                                   (seqTypedData.getSequenceType() == SequenceTypeSupport.XS_LONG) ||
                                                   (seqTypedData.getSequenceType() == SequenceTypeSupport.XS_INT))) {
                 isInstanceOf = true; 
             }
          }
      }
      else if ((xdmValue instanceof XSDouble) && (seqTypedData.getSequenceType() == SequenceTypeSupport.XS_DOUBLE) && 
              ((seqTypedData.getItemTypeOccurrenceIndicator() == 0) || 
                      (seqTypedData.getItemTypeOccurrenceIndicator() == SequenceTypeSupport.OccurenceIndicator.ZERO_OR_ONE))) {
         isInstanceOf = true; 
      }
      else if ((xdmValue instanceof XSFloat) && (seqTypedData.getSequenceType() == SequenceTypeSupport.XS_FLOAT) && 
              ((seqTypedData.getItemTypeOccurrenceIndicator() == 0) || 
                                                         (seqTypedData.getItemTypeOccurrenceIndicator() == SequenceTypeSupport.OccurenceIndicator.ZERO_OR_ONE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSInt) && (seqTypedData.getSequenceType() == SequenceTypeSupport.XS_INT) && 
              ((seqTypedData.getItemTypeOccurrenceIndicator() == 0) || 
                                                         (seqTypedData.getItemTypeOccurrenceIndicator() == SequenceTypeSupport.OccurenceIndicator.ZERO_OR_ONE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSLong) && (seqTypedData.getSequenceType() == SequenceTypeSupport.XS_LONG) && 
              ((seqTypedData.getItemTypeOccurrenceIndicator() == 0) || 
                                                         (seqTypedData.getItemTypeOccurrenceIndicator() == SequenceTypeSupport.OccurenceIndicator.ZERO_OR_ONE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSInteger) && (seqTypedData.getSequenceType() == SequenceTypeSupport.XS_INTEGER) && 
              ((seqTypedData.getItemTypeOccurrenceIndicator() == 0) || 
                                                         (seqTypedData.getItemTypeOccurrenceIndicator() == SequenceTypeSupport.OccurenceIndicator.ZERO_OR_ONE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSDecimal) && (seqTypedData.getSequenceType() == SequenceTypeSupport.XS_DECIMAL) && 
              ((seqTypedData.getItemTypeOccurrenceIndicator() == 0) || 
                                                         (seqTypedData.getItemTypeOccurrenceIndicator() == SequenceTypeSupport.OccurenceIndicator.ZERO_OR_ONE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSDate) && (seqTypedData.getSequenceType() == SequenceTypeSupport.XS_DATE) && 
              ((seqTypedData.getItemTypeOccurrenceIndicator() == 0) || 
                                                         (seqTypedData.getItemTypeOccurrenceIndicator() == SequenceTypeSupport.OccurenceIndicator.ZERO_OR_ONE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSDateTime) && (seqTypedData.getSequenceType() == SequenceTypeSupport.XS_DATETIME) && 
              ((seqTypedData.getItemTypeOccurrenceIndicator() == 0) || 
                                                         (seqTypedData.getItemTypeOccurrenceIndicator() == SequenceTypeSupport.OccurenceIndicator.ZERO_OR_ONE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSTime) && (seqTypedData.getSequenceType() == SequenceTypeSupport.XS_TIME) && 
              ((seqTypedData.getItemTypeOccurrenceIndicator() == 0) || 
                                                         (seqTypedData.getItemTypeOccurrenceIndicator() == SequenceTypeSupport.OccurenceIndicator.ZERO_OR_ONE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSDuration) && (seqTypedData.getSequenceType() == SequenceTypeSupport.XS_DURATION) && 
              ((seqTypedData.getItemTypeOccurrenceIndicator() == 0) || 
                                                         (seqTypedData.getItemTypeOccurrenceIndicator() == SequenceTypeSupport.OccurenceIndicator.ZERO_OR_ONE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSDayTimeDuration) && (seqTypedData.getSequenceType() == SequenceTypeSupport.XS_DAYTIME_DURATION) && 
              ((seqTypedData.getItemTypeOccurrenceIndicator() == 0) || 
                                                         (seqTypedData.getItemTypeOccurrenceIndicator() == SequenceTypeSupport.OccurenceIndicator.ZERO_OR_ONE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSYearMonthDuration) && (seqTypedData.getSequenceType() == SequenceTypeSupport.XS_YEARMONTH_DURATION) && 
              ((seqTypedData.getItemTypeOccurrenceIndicator() == 0) || 
                                                         (seqTypedData.getItemTypeOccurrenceIndicator() == SequenceTypeSupport.OccurenceIndicator.ZERO_OR_ONE))) {
          isInstanceOf = true;
      }
      else if (xdmValue instanceof XNodeSet) {
          XNodeSet xdmNodeSet = (XNodeSet)xdmValue;          
          int nodeSetLen = xdmNodeSet.getLength();          
          int itemTypeOccurenceIndicator = seqTypedData.getItemTypeOccurrenceIndicator();
          SequenceTypeKindTest seqTypeKindTest = seqTypedData.getSequenceTypeKindTest();
          
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
                                                                              && (SequenceTypeSupport.isTwoXmlNamespacesEqual(nodeNsUri, seqTypeKindTest.getNodeNsUri()))) {
                           nodeSetSequenceTypeKindTestResultList.add(Boolean.valueOf(true));    
                       }
                       else if ((seqTypeKindTest.getKindVal() == SequenceTypeSupport.NODE_KIND) || (seqTypeKindTest.getKindVal() == SequenceTypeSupport.ITEM_KIND)) {
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
      }
      else if (xdmValue instanceof ResultSequence) {
          ResultSequence srcResultSeq = (ResultSequence)xdmValue;
          
          int seqLen = srcResultSeq.size();
          
          if ((seqLen == 0) && (seqTypedData.getItemTypeOccurrenceIndicator() == OccurenceIndicator.ONE_OR_MANY)) {
             isInstanceOf = false;  
          }
          else if ((seqLen > 0) && (seqTypedData.getSequenceType() == SequenceTypeSupport.EMPTY_SEQUENCE)) {
             isInstanceOf = false;  
          }
          else if ((seqLen > 1) && (seqTypedData.getItemTypeOccurrenceIndicator() == OccurenceIndicator.ZERO_OR_ONE)) {
             isInstanceOf = false;
          }
          
          SequenceTypeData sequenceTypeDataNew = new SequenceTypeData();          
          if (seqTypedData.getSequenceTypeKindTest() != null) {
             sequenceTypeDataNew.setSequenceTypeKindTest(seqTypedData.getSequenceTypeKindTest()); 
          }
          else {
             sequenceTypeDataNew.setSequenceType(seqTypedData.getSequenceType()); 
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
      }
    
      return isInstanceOf;
  }
}
