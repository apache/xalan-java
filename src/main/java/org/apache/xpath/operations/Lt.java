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

import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSAnyURI;
import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSUntypedAtomic;

/**
 * An XPath 3.1 operator '<' evaluator.
 */
public class Lt extends XPathOperator
{
   static final long serialVersionUID = 3388420509289359422L;

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
  public XObject operate(XObject left, XObject right) throws javax.xml.transform.TransformerException
  {	  
	  XObject result = null;
	  
      XPathContext xctxt = getXPathContext();
      
      left = XObject.xdmAtomize(left, xctxt);
	  
	  right = XObject.xdmAtomize(right, xctxt);
	  
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
	  
	  if ((left instanceof XNumber) && (right instanceof XSUntypedAtomic)) {
		  java.lang.String str2 = ((XSUntypedAtomic)right).stringValue();
		  
		  try {			 
			 BigDecimal bigDecimal1 = XPathGeneralCmpEqualityOpUtil.getBigdecimalFromXNumber((XNumber)left);
			 
			 BigDecimal bigDecimal2 = new BigDecimal(str2); 
			 
			 if (bigDecimal1.compareTo(bigDecimal2) < 0) {
				result = XBoolean.S_TRUE; 
			 }
			 else {
				result = XBoolean.S_FALSE;
			 }
			 
			 return result;
		  }
		  catch (NumberFormatException ex) {
			 throw new TransformerException("FORG0001 : While evaluating XPath operator <, couldn't transform string '" 
		                                                                                                             + str2 + "' to a numeric value."); 
		  }
	  }
	  
	  if ((left instanceof XSUntypedAtomic) && (right instanceof XSUntypedAtomic)) {
		  java.lang.String str1 = ((XSUntypedAtomic)left).stringValue();
		  java.lang.String str2 = ((XSUntypedAtomic)right).stringValue();

		  try {			 
			  BigDecimal bigDecimal1 = new BigDecimal(str1); 

			  BigDecimal bigDecimal2 = new BigDecimal(str2); 

			  if (bigDecimal1.compareTo(bigDecimal2) < 0) {
				  result = XBoolean.S_TRUE; 
			  }
			  else {
				  result = XBoolean.S_FALSE;
			  }

			  return result;
		  }
		  catch (NumberFormatException ex) {
			  throw new TransformerException("FORG0001 : While evaluating XPath operator <, couldn't transform supplied XPath operand to a number."); 
		  }
	  }
	  
	  // xs:anyURI values are compared as strings
	  
	  if (left instanceof XSAnyURI) {
		  left = new XSString(((XSAnyURI)left).stringValue());  
	  }
	  
	  if (right instanceof XSAnyURI) {
		  right = new XSString(((XSAnyURI)right).stringValue());  
	  }
	  
	  if (left instanceof ResultSequence) {
		  ResultSequence rSeq = (ResultSequence)left;

		  ResultSequence rSeq2 = new ResultSequence(); 

		  int size1 = rSeq.size();
		  
		  boolean isSeqContainsXsAnyUri = false;

		  for (int idx = 0; idx < size1; idx++) {
			  XObject xObj = rSeq.item(idx);

			  if (xObj instanceof XSAnyURI) {
				  XSString xsString = new XSString(((XSAnyURI)xObj).stringValue());
				  rSeq2.add(xsString);
				  
				  isSeqContainsXsAnyUri = true;
			  }
			  else {
				  rSeq2.add(xObj);
			  }
		  }

		  if (isSeqContainsXsAnyUri) {
			  if (size1 > 1) {
				  left = rSeq2;
			  }
			  else if (size1 == 1) {
				  left = rSeq2.item(0); 
			  }
		  }
	  }
	  
	  if (right instanceof ResultSequence) {
		  ResultSequence rSeq = (ResultSequence)right;

		  ResultSequence rSeq2 = new ResultSequence(); 

		  int size1 = rSeq.size();
		  
		  boolean isSeqContainsXsAnyUri = false;

		  for (int idx = 0; idx < size1; idx++) {
			  XObject xObj = rSeq.item(idx);

			  if (xObj instanceof XSAnyURI) {
				  XSString xsString = new XSString(((XSAnyURI)xObj).stringValue());
				  rSeq2.add(xsString);
				  
				  isSeqContainsXsAnyUri = true;
			  }
			  else {
				  rSeq2.add(xObj);
			  }
		  }

		  if (isSeqContainsXsAnyUri) {
			  if (size1 > 1) {
				  right = rSeq2;
			  }
			  else if (size1 == 1) {
				  right = rSeq2.item(0); 
			  }
		  }
	  }
	  
	  if ((left instanceof XSString) && (right instanceof XSString)) {
		 java.lang.String str1 = ((XSString)left).stringValue();
		 java.lang.String str2 = ((XSString)right).stringValue();
		 
		 if (str1.compareTo(str2) < 0) {
			return XBoolean.S_TRUE; 
		 }
		 else {
			return XBoolean.S_FALSE; 
		 }
	  }
	  
	  if ((left instanceof XNumber) && (right instanceof XSUntypedAtomic)) {
		  java.lang.String str2 = ((XSUntypedAtomic)right).stringValue();
		  
		  try {			 
			 BigDecimal bigDecimal1 = XPathGeneralCmpEqualityOpUtil.getBigdecimalFromXNumber((XNumber)left);
			 
			 BigDecimal bigDecimal2 = new BigDecimal(str2); 
			 
			 if (bigDecimal1.compareTo(bigDecimal2) < 0) {
				result = XBoolean.S_TRUE; 
			 }
			 else {
				result = XBoolean.S_FALSE;
			 }
			 
			 return result;
		  }
		  catch (NumberFormatException ex) {
			 throw new TransformerException("FORG0001 : While evaluating XPath operator <, couldn't transform string '" 
		                                                                                                             + str2 + "' to a numeric value."); 
		  }
	  }
	  
	  if ((left instanceof XSUntypedAtomic) && (right instanceof XNumber)) {
		  java.lang.String str1 = ((XSUntypedAtomic)left).stringValue();
		  
		  try {			 			 			 
			 BigDecimal bigDecimal1 = new BigDecimal(str1);
			 
			 BigDecimal bigDecimal2 = XPathGeneralCmpEqualityOpUtil.getBigdecimalFromXNumber((XNumber)right);
			 
			 if (bigDecimal1.compareTo(bigDecimal2) < 0) {
				result = XBoolean.S_TRUE; 
			 }
			 else {
				result = XBoolean.S_FALSE;
			 }
			 
			 return result;
		  }
		  catch (NumberFormatException ex) {
			 throw new TransformerException("FORG0001 : While evaluating XPath operator <, couldn't transform string '" 
		                                                                                                             + str1 + "' to a numeric value."); 
		  }
	  }	  
	  
	  if ((left instanceof XSBoolean) || (left instanceof XBoolean) || (left instanceof XBooleanStatic)) {
		  if ((right instanceof XSBoolean) || (right instanceof XBoolean) || (right instanceof XBooleanStatic)) {
			  boolean bool1 = left.bool(); 
			  boolean bool2 = right.bool();

			  int a = bool1 ? 1 : 0;
			  int b = bool2 ? 1 : 0;

			  if (a < b) {
				  return XBoolean.S_TRUE;
			  }
			  else {
				  return XBoolean.S_FALSE;
			  }
		  }
	  }

	  if ((left instanceof ResultSequence) && (right instanceof ResultSequence)) {		  		  
		  boolean bool = XPathGeneralCmpOpSupport.lessThan((ResultSequence)left, (ResultSequence)right, false);
			 
		  result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE);
	  }
	  else if ((left instanceof XSNumericType) && (right instanceof XNumber)) {
		  java.lang.String lStr = ((XSNumericType)left).stringValue();
		  double dbl1 = (Double.valueOf(lStr)).doubleValue();
		  double dbl2 = ((XNumber)right).num();
		  
		  result = ((dbl1 < dbl2) ? XBoolean.S_TRUE : XBoolean.S_FALSE);
	  }
      else if ((left instanceof XNumber) && (right instanceof XSNumericType)) {
    	  double dbl1 = ((XNumber)left).num();
    	  java.lang.String rStr = ((XSNumericType)right).stringValue();
		  double dbl2 = (Double.valueOf(rStr)).doubleValue();		  
		  
		  result = ((dbl1 < dbl2) ? XBoolean.S_TRUE : XBoolean.S_FALSE); 
	  }
      else if ((left instanceof XSNumericType) && (right instanceof XSNumericType)) {
    	  java.lang.String lStr = ((XSNumericType)left).stringValue();
		  double dbl1 = (Double.valueOf(lStr)).doubleValue();
		  java.lang.String rStr = ((XSNumericType)right).stringValue();
		  double dbl2 = (Double.valueOf(rStr)).doubleValue();
		  
		  result = ((dbl1 < dbl2) ? XBoolean.S_TRUE : XBoolean.S_FALSE);
      }
      else if ((left instanceof XNumber) && (right instanceof XNumber)) {
    	  double dbl1 = ((XNumber)left).num();
    	  double dbl2 = ((XNumber)right).num();
    	  
    	  result = ((dbl1 < dbl2) ? XBoolean.S_TRUE : XBoolean.S_FALSE);
      }
	  
	  if (result != null) {
		  return result;
	  }
	  
	  if (right instanceof ResultSequence) {
		  if (left instanceof XNumber) {
			  boolean bool = XPathGeneralCmpOpSupport.lessThan((XNumber)left, (ResultSequence)right, false);

			  result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE); 
		  }
		  else if (left instanceof XSNumericType) {
			  boolean bool = XPathGeneralCmpOpSupport.lessThan((XSNumericType)left, (ResultSequence)right, false);

			  result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE);
		  }
		  else if (left instanceof ResultSequence) {
			  boolean bool = XPathGeneralCmpOpSupport.lessThan((ResultSequence)left, (ResultSequence)right, false);

			  result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE);
		  }
		  else if ((left instanceof XMLNodeCursorImpl) || (left instanceof XSUntypedAtomic)) {
			  java.lang.String lStrVal = XslTransformEvaluationHelper.getStrVal(left);
			  
			  try {
				  double dbl1 = Double.valueOf(lStrVal);
				  
				  boolean bool = XPathGeneralCmpOpSupport.lessThan(new XNumber(dbl1), (ResultSequence)right, false);

				  result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE);
			  }
			  catch (NumberFormatException ex) {
				  throw new TransformerException("FORG0001 : The string value '" + lStrVal + "' cannot be converted to double.");
			  }
		  }
	  }
	  else if (left instanceof ResultSequence) {
		  if (right instanceof XNumber) {
			  boolean bool = XPathGeneralCmpOpSupport.lessThan((ResultSequence)left, (XNumber)right, false);

			  result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE);  
		  }
		  else if (right instanceof XSNumericType) {
			  boolean bool = XPathGeneralCmpOpSupport.lessThan((ResultSequence)left, (XSNumericType)right, false);

			  result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE);
		  }
		  else if ((right instanceof XMLNodeCursorImpl) || (right instanceof XSUntypedAtomic)) {
			  java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(right);
			  
			  try {
				  double dbl = Double.valueOf(str1);
				  
				  boolean bool = XPathGeneralCmpOpSupport.lessThan((ResultSequence)left, new XNumber(dbl), false);

				  result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE);
			  }
			  catch (NumberFormatException ex) {
				  throw new TransformerException("FORG0001 : The string value '" + str1 + "' cannot be converted to a number.");
			  }
		  }
		  else if ((right instanceof XSString) || (right instanceof XString)) {
			  ResultSequence rSeq = (ResultSequence)left;
			  
			  int size1 = rSeq.size();
			  
			  java.lang.String str2 = XslTransformEvaluationHelper.getStrVal(right);
			  
			  boolean result1 = false;
			  
			  boolean isDataTypeCompatible = true;
			  
			  for (int idx = 0; idx < size1; idx++) {
				 XObject xObj = rSeq.item(idx);
				 
				 if (xObj instanceof XSUntypedAtomic) {
					XSUntypedAtomic xsUntypedAtomic = (XSUntypedAtomic)xObj;
					java.lang.String str1 = xsUntypedAtomic.stringValue();
					
					if (str1.compareTo(str2) < 0) {
					   result1 = true;
					}
				 }
				 else {
					isDataTypeCompatible = false;
				 }
			  }
			  
			  if (isDataTypeCompatible) {
				  if (result1) {
					  result = XBoolean.S_TRUE;  
				  }
				  else {
					  result = XBoolean.S_FALSE; 
				  }
				  
				  return result;
			  }
		  }
	  }
	  else if (left instanceof XPathMap) {
		  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 map cannot be atomized. An xdm map is provided as "
		  																															+ "operator '<' lhs operand.");
	  }
      else if (right instanceof XPathMap) {
    	  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 map cannot be atomized. An xdm map is provided as "
    	  																															+ "operator '<' rhs operand."); 
	  }
	  else if (isXPathOperandXdmFunctionItem(left)) {
		  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 function item cannot be atomized. An XPath function "
		  		                                                                                                                    + "item is provided as operator '<' lhs operand.");
	  }
      else if (isXPathOperandXdmFunctionItem(right)) {
    	  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 function item cannot be atomized. An XPath function "
                                                                                                                                    + "item is provided as operator '<' rhs operand."); 
	  }
	  else {
		  result = left.lessThan(right) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
	  }
	  
	  return result;
  }
}
