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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSInteger;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;

/**
 * An XPath 3.1 operator '>=' evaluator.
 */
public class Gte extends XPathOperator
{
  static final long serialVersionUID = 9142945909906680220L;

  /**
   * Apply an XPath operator to its two operands, and return the result.
   *
   * @param left  non-null reference to an XPath operator's evaluated 
   *              first operand.              
   * @param right non-null reference to an XPath operator's evaluated 
   *              second operand.
   *
   * @return non-null reference to an XObject object instance, that 
   *         represents the result of XPath operator evaluation. 
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
	  
	  if ((left instanceof XSString || left instanceof XString) && 
			  												(right instanceof XSNumericType || right instanceof XNumber)) {
		  java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(left);
		  if (str1.startsWith("0")) { 
			  throw new javax.xml.transform.TransformerException("XPTY0004 : An xdm string value '" + str1 + "' cannot be compared to an integer.");
		  }
	  }

	  if ((right instanceof XSString || right instanceof XString) && 
			  												(left instanceof XSNumericType || left instanceof XNumber)) {
		  java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(right);
		  if (str1.startsWith("0")) { 
			  throw new javax.xml.transform.TransformerException("XPTY0004 : An xdm string value '" + str1 + "' cannot be compared to an integer.");
		  }
	  }
	  
	  if ((left instanceof ResultSequence) && (((ResultSequence)left).size() == 0)) {
		 result = XBoolean.S_FALSE;
		 
		 return result;
	  }
	  
	  if ((right instanceof ResultSequence) && (((ResultSequence)right).size() == 0)) {
		  result = XBoolean.S_FALSE;

		  return result;
	  }
	  
	  BigInteger bigInt1 = null;
	  BigInteger bigInt2 = null;
	  
	  BigDecimal bigDecimal1 = null;
	  BigDecimal bigDecimal2 = null;
	  
	  if ((left instanceof XNumber) && !(right instanceof ResultSequence)) {
		  XNumber xNumber = (XNumber)left;
		  
		  if (xNumber.getXsDecimal() != null) {
			  left = xNumber.getXsDecimal();
			  XSDecimal xsDecimal =(XSDecimal)left;
			  
			  bigDecimal1 = xsDecimal.getValue(); 
		  }
		  else if (xNumber.getXsDouble() != null) {
			  left = xNumber.getXsDouble();  
		  }
		  else if (xNumber.getXsInteger() != null) {			  			  
			  left = xNumber.getXsInteger();
			  XSInteger xsInteger =(XSInteger)left;
			  
			  bigInt1 = xsInteger.intValue();
		  }
		  
		  if (right instanceof XMLNodeCursorImpl) {
			  right = right.getFresh();
			  
			  XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)right;			  			  
			  DTMCursorIterator dtmCursorIterator = xmlNodeCursorImpl.iter();
			  
			  int nextNode = DTM.NULL;
			  while ((nextNode = dtmCursorIterator.nextNode()) != DTM.NULL) {
				 XMLNodeCursorImpl node1 = new XMLNodeCursorImpl(nextNode, m_xctxt); 
				 java.lang.String str1 = node1.str();
				 
				 BigDecimal bigDecimalVal = null;
				 
				 try {
				    bigDecimalVal = new BigDecimal(str1);
				 }
				 catch (NumberFormatException ex) {
					throw new TransformerException("FORG0001 : A string value '" + str1 + "' cannot be converted to double."); 
				 }
				 
				 if (bigInt1 != null) {
					bigDecimal1 = new BigDecimal(bigInt1);
				 }
				 
				 if (bigDecimal1 != null) {
					if (bigDecimal1.compareTo(bigDecimalVal) >= 0) {
					   return XBoolean.S_TRUE;	
					}					
				 }
			  }
			  
			  return XBoolean.S_FALSE;
		  }
	  }
	  
	  if ((right instanceof XNumber) && !(left instanceof ResultSequence)) {
		  XNumber xNumber = (XNumber)right;
		  
		  if (xNumber.getXsDecimal() != null) {
			  right = xNumber.getXsDecimal();
			  XSDecimal xsDecimal =(XSDecimal)right;
			  
			  bigDecimal2 = xsDecimal.getValue(); 
		  }
		  else if (xNumber.getXsDouble() != null) {
			  right = xNumber.getXsDouble();  
		  }
		  else if (xNumber.getXsInteger() != null) {
			  right = xNumber.getXsInteger();
			  XSInteger xsInteger =(XSInteger)right;
			  
			  bigInt2 = xsInteger.intValue();  
		  }
		  
		  if (left instanceof XMLNodeCursorImpl) {
			  left = left.getFresh();
			  
			  XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)left;			  			  
			  DTMCursorIterator dtmCursorIterator = xmlNodeCursorImpl.iter();
			  
			  int nextNode = DTM.NULL;
			  while ((nextNode = dtmCursorIterator.nextNode()) != DTM.NULL) {
				 XMLNodeCursorImpl node1 = new XMLNodeCursorImpl(nextNode, m_xctxt); 
				 java.lang.String str1 = node1.str();
				 
				 BigDecimal bigDecimalVal = null;
				 
				 try {
				    bigDecimalVal = new BigDecimal(str1);
				 }
				 catch (NumberFormatException ex) {
					throw new TransformerException("FORG0001 : A string value '" + str1 + "' cannot be converted to double."); 
				 }
				 
				 if (bigInt2 != null) {
					bigDecimal2 = new BigDecimal(bigInt2);
				 }
				 
				 if (bigDecimal2 != null) {
					if (bigDecimalVal.compareTo(bigDecimal2) >= 0) {
					   return XBoolean.S_TRUE;	
					}					
				 }
			  }
			  
			  return XBoolean.S_FALSE;
		  }
	  }
	  
	  if ((bigInt1 != null) && (bigInt2 != null)) {
		  if (bigInt1.compareTo(bigInt2) >= 0) {
			  return XBoolean.S_TRUE; 
		  }
		  else {
			  return XBoolean.S_FALSE;
		  }
	  }
	  else if ((bigDecimal1 != null) && (bigDecimal2 != null)) {
		  if (bigDecimal1.compareTo(bigDecimal2) >= 0) {
			  return XBoolean.S_TRUE; 
		  }
		  else {
			  return XBoolean.S_FALSE;
		  }
	  }
	  
	  XObject lObj = null;
	  XObject rObj = null;
	  
	  List<java.lang.String> strList = new ArrayList<java.lang.String>();
	  
	  if (left instanceof XSDouble) {
		  XSDouble lXsDouble = (XSDouble)left;
		  if (lXsDouble.nan()) {
			 if (right instanceof XNumber) {
				double rDbl = ((XNumber)right).num();
				if (!((new Double(rDbl)).isNaN() || (rDbl == Double.POSITIVE_INFINITY) || (rDbl == Double.NEGATIVE_INFINITY))) {
				   return XBoolean.S_FALSE;
				}
			 }
		  }
	  }
	  
	  if ((left instanceof XMLNodeCursorImpl) && ((right instanceof XString) || (right instanceof XSString))) {
		  lObj = left;
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
		  
		  lObj = lObj.getFresh();
		  left = left.getFresh();
	  }
	  else if ((right instanceof XMLNodeCursorImpl) && ((left instanceof XString) || (left instanceof XSString))) {
		  lObj = right;
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
		  
		  lObj = lObj.getFresh();
		  right = right.getFresh();
	  }
	  else if ((left instanceof XSNumericType) && (right instanceof XNumber)) {
		  java.lang.String lStr = ((XSNumericType)left).stringValue();
		  double dbl1 = (Double.valueOf(lStr)).doubleValue();
		  double dbl2 = ((XNumber)right).num();
		  
		  result = ((dbl1 >= dbl2) ? XBoolean.S_TRUE : XBoolean.S_FALSE);
	  }
      else if ((left instanceof XNumber) && (right instanceof XSNumericType)) {
    	  double dbl1 = ((XNumber)left).num();
    	  java.lang.String rStr = ((XSNumericType)right).stringValue();
		  double dbl2 = (Double.valueOf(rStr)).doubleValue();		  
		  
		  result = ((dbl1 >= dbl2) ? XBoolean.S_TRUE : XBoolean.S_FALSE); 
	  }
      else if ((left instanceof XSNumericType) && (right instanceof XSNumericType)) {
    	  java.lang.String lStr = ((XSNumericType)left).stringValue();
		  double dbl1 = (Double.valueOf(lStr)).doubleValue();
		  java.lang.String rStr = ((XSNumericType)right).stringValue();
		  double dbl2 = (Double.valueOf(rStr)).doubleValue();
		  
		  result = ((dbl1 >= dbl2) ? XBoolean.S_TRUE : XBoolean.S_FALSE);
      }
      else if ((left instanceof XNumber) && (right instanceof XNumber)) {
    	  double dbl1 = ((XNumber)left).num();
    	  double dbl2 = ((XNumber)right).num();
    	  
    	  result = ((dbl1 >= dbl2) ? XBoolean.S_TRUE : XBoolean.S_FALSE);
      }
	  
	  if (result != null) {
		  return result;
	  }
	  
	  if (strList.size() > 0) {		  
		  if (rObj instanceof XString) {
			  java.lang.String strR = rObj.str();			
			  for (int i = 0; i < strList.size(); i++) {
				 java.lang.String str2 = strList.get(i);
				 if ((str2.compareTo(strR) > 0) || (str2.compareTo(strR) == 0)) {
					return XBoolean.S_TRUE; 
				 }
			  }
			  
			  return XBoolean.S_FALSE;
		  }
		  else if (rObj instanceof XSString) {
			  java.lang.String strR = ((XSString)rObj).stringValue();
			  for (int i = 0; i < strList.size(); i++) {
				  java.lang.String str2 = strList.get(i);
				  if ((str2.compareTo(strR) > 0) || (str2.compareTo(strR) == 0)) {
					  return XBoolean.S_TRUE; 
				  }
			  }

			  return XBoolean.S_FALSE;
		  }
      }
	  else if ((lObj != null) && (strList.size() == 0)) {
		  result = XBoolean.S_FALSE;
		  
		  return result;
	  }
  	  
	  if (right instanceof ResultSequence) {
		 if (left instanceof XNumber) {
			 boolean bool = XPathGeneralCmpOpSupport.greaterThan((XNumber)left, (ResultSequence)right, true);
			 
			 result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE); 
		 }
		 else if (left instanceof XSNumericType) {
			 boolean bool = XPathGeneralCmpOpSupport.greaterThan((XSNumericType)left, (ResultSequence)right, true);
			 
			 result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE);
		 }
		 else if (left instanceof ResultSequence) {
			 boolean bool = XPathGeneralCmpOpSupport.greaterThan((ResultSequence)left, (ResultSequence)right, true);
			 
			 result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE);
		 }
		 else if (left instanceof XMLNodeCursorImpl) {
			 java.lang.String lStrVal = ((XMLNodeCursorImpl)left).str();
			 try {
				 double lhsDbl = Double.valueOf(lStrVal);
				 boolean bool = XPathGeneralCmpOpSupport.greaterThan(new XNumber(lhsDbl), (ResultSequence)right, true);

				 result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE);
			 }
			 catch (NumberFormatException ex) {
				 throw new TransformerException("FORG0001 : The string value '" + lStrVal + "' cannot be converted to double.");
			 }
		 }
	  }
	  else if (left instanceof ResultSequence) {
		  if (right instanceof XNumber) {
			  boolean bool = XPathGeneralCmpOpSupport.greaterThan((ResultSequence)left, (XNumber)right, true);
				 
			  result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE);  
		  }
		  else if (right instanceof XSNumericType) {
			  boolean bool = XPathGeneralCmpOpSupport.greaterThan((ResultSequence)left, (XSNumericType)right, true);
				 
			  result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE);
		  }
		  else if (right instanceof XMLNodeCursorImpl) {
			  java.lang.String rStrVal = ((XMLNodeCursorImpl)right).str();
			  try {
				  double rhsDbl = Double.valueOf(rStrVal);
				  boolean bool = XPathGeneralCmpOpSupport.greaterThan((ResultSequence)left, new XNumber(rhsDbl), true);
					 
				  result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE);
			  }
			  catch (NumberFormatException ex) {
				  throw new TransformerException("FORG0001 : The string value '" + rStrVal + "' cannot be converted to double.");
			  }
		  }
	  }
	  else if (left instanceof XPathMap) {
		  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 map cannot be atomized. An xdm map is provided as "
		  																															+ "operator '>=' lhs operand.");
	  }
      else if (right instanceof XPathMap) {
    	  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 map cannot be atomized. An xdm map is provided as "
    	  																															+ "operator '>=' rhs operand."); 
	  }
	  else if (isXPathOperandXdmFunctionItem(left)) {
		  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 function item cannot be atomized. An XPath function "
		  		                                                                                                                    + "item is provided as operator '>=' lhs operand.");
	  }
      else if (isXPathOperandXdmFunctionItem(right)) {
    	  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 function item cannot be atomized. An XPath function "
                                                                                                                                    + "item is provided as operator '>=' rhs operand."); 
	  }
	  else {
		  result = left.greaterThanOrEqual(right) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
	  }
	  
	  return result;
  }
}
