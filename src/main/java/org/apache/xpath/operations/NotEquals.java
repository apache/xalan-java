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
/*
 * $Id$
 */
package org.apache.xpath.operations;

import java.util.ArrayList;
import java.util.List;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;

/**
 * The '!=' operation expression executer.
 */
public class NotEquals extends Operation
{
   static final long serialVersionUID = -7869072863070586900L;

  /**
   * Apply the operation to two operands, and return the result.
   *
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
	  
      XObject result = null;
	  
	  if (left instanceof XPathArray) {
		 left = ((XPathArray)left).atomize();
	  }
	  
	  if (right instanceof XPathArray) {
		 right = ((XPathArray)right).atomize(); 
	  }
	  
	  XObject lObj = null;
	  XObject rObj = null;
	  
	  List<java.lang.String> strList = new ArrayList<java.lang.String>();
	  
	  if ((left instanceof XMLNodeCursorImpl) && ((right instanceof XString) || (right instanceof XSString))) {
		  lObj = left.getFresh();
		  rObj = right;
		  
		  XMLNodeCursorImpl nodeRef = (XMLNodeCursorImpl)lObj;		  
		  DTMManager dtmManager = nodeRef.getDTMManager();
		  DTMCursorIterator iter = nodeRef.iterRaw();
		  int nextNode = DTM.NULL;		  
		  while ((nextNode = iter.nextNode()) != DTM.NULL) {
			  XMLNodeCursorImpl nodeRef1 = new XMLNodeCursorImpl(nextNode, dtmManager);
			  java.lang.String nodeStrValue = nodeRef1.str();
			  strList.add(nodeStrValue);
		  }
	  }
	  else if ((right instanceof XMLNodeCursorImpl) && ((left instanceof XString) || (left instanceof XSString))) {
		  lObj = right.getFresh();
		  rObj = left;
		  
		  XMLNodeCursorImpl nodeRef = (XMLNodeCursorImpl)lObj;		  
		  DTMManager dtmManager = nodeRef.getDTMManager();
		  DTMCursorIterator iter = nodeRef.iterRaw();
		  int nextNode = DTM.NULL;
		  while ((nextNode = iter.nextNode()) != DTM.NULL) {
			  XMLNodeCursorImpl nodeRef1 = new XMLNodeCursorImpl(nextNode, dtmManager);
			  java.lang.String nodeStrValue = nodeRef1.str();
			  strList.add(nodeStrValue);
		  }
	  }
	  
	  if ((lObj instanceof XMLNodeCursorImpl) && ((rObj instanceof XString) || (rObj instanceof XSString))) {
		  if (strList.size() > 0) {			  
			  java.lang.String strR = ((rObj instanceof XString) ? rObj.str() : ((XSString)rObj).stringValue());
			  int strListSize = strList.size();
			  for (int idx = 0; idx < strListSize; idx++) {
				  java.lang.String str1 = strList.get(idx);
				  if (!str1.equals(strR)) {
					  return XBoolean.S_TRUE;
				  }
			  }
			  
			  return XBoolean.S_FALSE;
		  }
		  
		  // An lObj object instance represents an empty sequence
		  return XBoolean.S_FALSE;	
	  }
	  
	  if ((lObj != null) && (strList.size() == 0)) {
		  result = XBoolean.S_TRUE;
		  
		  return result;
	  }
	  	  
	  if (right instanceof ResultSequence) {
		 if (left instanceof XNumber) {
			 boolean bool = XPathGeneralComparisonEqualityOpSupport.equals((XNumber)left, (ResultSequence)right, true);
			 result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE); 
		 }
		 else if (left instanceof XSNumericType) {
			 boolean bool = XPathGeneralComparisonEqualityOpSupport.equals((XSNumericType)left, (ResultSequence)right, true);
			 result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE);
		 }
		 else if (left instanceof XSString) {
			 boolean bool = XPathGeneralComparisonEqualityOpSupport.equals((XSString)left, (ResultSequence)right, true);
			 result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE);
		 }
		 else if (left instanceof XString) {
			 boolean bool = XPathGeneralComparisonEqualityOpSupport.equals((XString)left, (ResultSequence)right, true);
			 result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE);
		 }
		 else if (left instanceof ResultSequence) {
			 ResultSequence resultSeqLhs = (ResultSequence)left;
			 ResultSequence resultSeqRhs = (ResultSequence)right;
			 boolean isNotEqual = false;
			 for (int i = 0; i < resultSeqLhs.size(); i++) {
				 XObject xObj1 = resultSeqLhs.item(i); 
				 for (int j = 0; j < resultSeqRhs.size(); j++) {
					XObject xObj2 = resultSeqRhs.item(j);
					if (xObj2 instanceof ResultSequence) {
					   isNotEqual = xObj1.vcEquals(xObj2, null, XPathCollationSupport.UNICODE_CODEPOINT_COLLATION_URI, false);
					}					
					else if (xObj1.vcEquals(xObj2, null, XPathCollationSupport.UNICODE_CODEPOINT_COLLATION_URI, false)) {
					   isNotEqual = true;						
					}
					
					if (isNotEqual) {
					   break;	
					}
				 }
				 
				 if (isNotEqual) {
					break; 
				 }
			 }
			 
			 result = (isNotEqual ? XBoolean.S_TRUE : XBoolean.S_FALSE);
		 }
		 else {
			 java.lang.String lStrVal = XslTransformEvaluationHelper.getStrVal(left);			 
			 boolean bool = XPathGeneralComparisonEqualityOpSupport.equals(new XSString(lStrVal), (ResultSequence)right, true);
			 result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE);
		 }
	  }
	  else if ((left instanceof XMLNodeCursorImpl) && (right instanceof XMLNodeCursorImpl)) {
		  lObj = left.getFresh();
		  rObj = right.getFresh();
		  
		  List<java.lang.String> strList1 = new ArrayList<java.lang.String>();
		  XMLNodeCursorImpl nodeRef = (XMLNodeCursorImpl)lObj;		  
		  DTMManager dtmManager = nodeRef.getDTMManager();
		  DTMCursorIterator iter = nodeRef.iterRaw();
		  int nextNode = DTM.NULL;		  
		  while ((nextNode = iter.nextNode()) != DTM.NULL) {
			  XMLNodeCursorImpl nodeRef1 = new XMLNodeCursorImpl(nextNode, dtmManager);
			  java.lang.String nodeStrValue = nodeRef1.str();
			  strList1.add(nodeStrValue);
		  }
		  
		  List<java.lang.String> strList2 = new ArrayList<java.lang.String>();
		  nodeRef = (XMLNodeCursorImpl)rObj;		  
		  dtmManager = nodeRef.getDTMManager();
		  iter = nodeRef.iterRaw();
		  nextNode = DTM.NULL;		  
		  while ((nextNode = iter.nextNode()) != DTM.NULL) {
			  XMLNodeCursorImpl nodeRef2 = new XMLNodeCursorImpl(nextNode, dtmManager);
			  java.lang.String nodeStrValue = nodeRef2.str();
			  strList2.add(nodeStrValue);
		  }
		  
		  int size1 = strList1.size();
		  int size2 = strList2.size();		  
		  for (int idx = 0; idx < size1; idx++) {
			  java.lang.String str1 = strList1.get(idx);
			  for (int idx2 = 0; idx2 < size2; idx2++) {
				 java.lang.String str2 = strList2.get(idx2);
				 if (!str1.equals(str2)) {
					return XBoolean.S_TRUE; 
				 }
			  }  
		  }
		  
		  return XBoolean.S_FALSE;
	  }
	  else if (left instanceof XMLNodeCursorImpl) {
		 left = left.getFresh();
		 
		 XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)left;
		 DTMCursorIterator iter1 = xmlNodeCursorImpl.iterRaw();
		 int nextNode;
		 DTMManager dtmManager = iter1.getDTMManager();
		 while ((nextNode = iter1.nextNode()) != DTM.NULL) {
			XMLNodeCursorImpl xmlNodeCursorImpl1 = new XMLNodeCursorImpl(nextNode, dtmManager);
			java.lang.String lStrValue = xmlNodeCursorImpl1.str();
			try {
				if (right instanceof XNumber) {
					double lDbl = Double.valueOf(lStrValue);
					XNumber rXNumber = (XNumber)right;
					double rDbl = rXNumber.num();
					if (lDbl != rDbl) {
						return XBoolean.S_TRUE;
					}
				}
				else if (right instanceof XSNumericType) {
					double lDbl = Double.valueOf(lStrValue);
					XSNumericType xsNumericType = (XSNumericType)right;
					java.lang.String rStrValue = xsNumericType.stringValue();
					double rDbl = Double.valueOf(rStrValue);
					if (lDbl != rDbl) {
					   return XBoolean.S_TRUE;
					}
				}
				else if (right instanceof XSBoolean) {
					java.lang.String rStrValue = ((XSBoolean)right).stringValue();
					if (!"".equals(lStrValue) && (!"1".equals(rStrValue) || !"true".equals(rStrValue))) {
						return XBoolean.S_TRUE;
					}
					else if ("".equals(lStrValue) && (!"0".equals(rStrValue) || !"false".equals(rStrValue))) {
						return XBoolean.S_TRUE;
					}
				}
				else if ((right instanceof XBoolean) || (right instanceof XBooleanStatic)) {
					java.lang.String rStrValue = right.str();
					if (!"".equals(lStrValue) && (!"1".equals(rStrValue) || !"true".equals(rStrValue))) {
						return XBoolean.S_TRUE;
					}
					else if ("".equals(lStrValue) && (!"0".equals(rStrValue) || !"false".equals(rStrValue))) {
						return XBoolean.S_TRUE;
					}
				}
				else {
					java.lang.String rStrValue = XslTransformEvaluationHelper.getStrVal(right);
					if (!lStrValue.equals(rStrValue)) {
					   return XBoolean.S_TRUE;
					}
				}
			}
			catch (NumberFormatException ex) {
				// no op
			}
		 }
		 
		 result = XBoolean.S_FALSE;
	  }
	  else if ((left instanceof XString) && ((right instanceof XSNumericType) || (right instanceof XNumber))) {
		  XString lStr = (XString)left;
		  if (lStr.isXrTreeFragSelectWrapperResult()) {
			  java.lang.String strVal1 = lStr.str();
			  double dbl1 = 0;
			  try {
				  dbl1 = (Double.valueOf(strVal1)).doubleValue();
			  }
			  catch (NumberFormatException ex) {
				  throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath numeric comparison with = operator, has LHS operand value as non-numeric.");
			  }
			  
			  java.lang.String strVal2 = XslTransformEvaluationHelper.getStrVal(right);
			  double dbl2 = (Double.valueOf(strVal2)).doubleValue();
			  
			  result = ((dbl1 != dbl2) ? XBoolean.S_TRUE : XBoolean.S_FALSE);  
		  }
		  else {
			  throw new javax.xml.transform.TransformerException("XPTY0004 : Within an XPath expression, number cannot be "
					  																							+ "compared to a string value.");  
		  }
	  }
	  else if (((left instanceof XSNumericType) || (left instanceof XNumber)) && (right instanceof XString)) {
		  XString rStr = (XString)right;
		  if (rStr.isXrTreeFragSelectWrapperResult()) {			  			  			  
			  java.lang.String strVal1 = rStr.str();
			  double dbl1 = 0;
			  try {
				  dbl1 = (Double.valueOf(strVal1)).doubleValue();
			  }
			  catch (NumberFormatException ex) {
				  throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath numeric comparison with = operator, has RHS operand value as non-numeric.");
			  }
			  
			  java.lang.String strVal2 = XslTransformEvaluationHelper.getStrVal(left);
			  double dbl2 = (Double.valueOf(strVal2)).doubleValue();
			  
			  result = ((dbl1 != dbl2) ? XBoolean.S_TRUE : XBoolean.S_FALSE);
		  }
		  else {
			  throw new javax.xml.transform.TransformerException("XPTY0004 : Within an XPath expression, number cannot be "
					  																							+ "compared to a string value.");  
		  }
	  }
      else if ((left instanceof XSString) && ((right instanceof XSNumericType) || (right instanceof XNumber))) {
		 throw new javax.xml.transform.TransformerException("XPTY0004 : Within an XPath expression, number cannot be "
		 		                                                                                                + "compared to a string value."); 
	  }
	  else if (((left instanceof XSNumericType) || (left instanceof XNumber)) && ((right instanceof XString) || (right instanceof XSString))) {
		  throw new javax.xml.transform.TransformerException("XPTY0004 : Within an XPath expression, number cannot be "
		  		                                                                                                 + "compared to a string value."); 
	  }
	  else if (((left instanceof XBooleanStatic) || (left instanceof XBoolean) || (left instanceof XSBoolean)) && ((right instanceof XSNumericType) 
			                                                                                                                       || (right instanceof XNumber))) {
		  throw new javax.xml.transform.TransformerException("XPTY0004 : Within an XPath expression, number cannot be "
				  																								 + "compared to a boolean value."); 
	  }
	  else if (((left instanceof XSNumericType) || (left instanceof XNumber)) && ((right instanceof XBooleanStatic) || (right instanceof XBoolean) || 
			                                                                                                                           (right instanceof XSBoolean))) {
		  throw new javax.xml.transform.TransformerException("XPTY0004 : Within an XPath expression, number cannot be compared to a boolean value."); 
	  }
	  else {
		 result = (left.notEquals(right) ? XBoolean.S_TRUE : XBoolean.S_FALSE);
	  }
	  
	  return result;
  }
}
