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

import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSAnyURI;
import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSFloat;
import xml.xpath31.processor.types.XSInteger;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSUntypedAtomic;

/**
 * An XPath 3.1 'and' expression evaluator.
 */
public class And extends XPathOperator
{
  static final long serialVersionUID = 392330077126534022L;

  /**
   * AND two expressions and return the boolean result. Override
   * superclass method for optimization purposes.
   *
   * @param xctxt					An XPath context object
   *
   * @return {@link org.apache.xpath.objects.XBoolean#S_TRUE} or 
   * {@link org.apache.xpath.objects.XBoolean#S_FALSE}.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {

	  XObject xObj0 = null;	
	  
	  try {
	     xObj0 = m_left.execute(xctxt);
	  }
	  catch (Exception ex) {
		 // No op
	  }
	  
	  if (xObj0 instanceof XSString) {		  
		  xObj0 = new XString(((XSString)xObj0).stringValue()); 
	  }
	  
	  boolean a1 = false;
	  
	  try {
		  if (xObj0 != null) {
			  a1 = xObj0.bool();
			  
			  if (!a1) {
				  return XBoolean.S_FALSE;  
			  }
		  }		  
	  }
	  catch (Exception ex) {
		  // No op
	  }
	  	  
	  if (xObj0 != null) {
		  if ((xObj0 instanceof ResultSequence) && (((ResultSequence)xObj0).size() == 0)) {
			  return XBoolean.S_FALSE;
		  }
		  else if (xObj0 instanceof ResultSequence) {
			  XObject xObj = ((ResultSequence)xObj0).item(0);
			  
			  if ((xObj instanceof XSBoolean) || (xObj instanceof XBoolean) || (xObj instanceof XBooleanStatic)) {
				  if (!xObj.bool()) {
					  return XBoolean.S_FALSE; 
				  }
			  }
		  }
		  else if ((xObj0 instanceof XMLNodeCursorImpl) && (((XMLNodeCursorImpl)xObj0).getLength() == 0)) {
			  return XBoolean.S_FALSE;
		  }
		  else if ((xObj0 instanceof XSBoolean) || (xObj0 instanceof XBoolean) || (xObj0 instanceof XBooleanStatic)) {			  
			  if (!xObj0.bool()) {
				  return XBoolean.S_FALSE; 
			  }
		  }
		  else if ((xObj0 instanceof XSString) || (xObj0 instanceof XString)) {
			  java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj0);
			  
			  if (str1.length() == 0) {
				  return XBoolean.S_FALSE; 
			  }
		  }
		  else if ((xObj0 instanceof XSAnyURI) || (xObj0 instanceof XSUntypedAtomic)) {
			  java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj0);
			  
			  if (str1.length() == 0) {
				  return XBoolean.S_FALSE; 
			  }
		  }
		  else if (xObj0 instanceof XSFloat) {
			  XSFloat xsFloat = (XSFloat)xObj0;
			  
			  if (xsFloat.nan()) {
				  return XBoolean.S_FALSE; 
			  }
		  }
		  else if (xObj0 instanceof XSDouble) {
			  XSDouble xsDouble = (XSDouble)xObj0;
			  
			  if (xsDouble.nan()) {
				  return XBoolean.S_FALSE;
			  }
		  }	  
		  else if (xObj0 instanceof XNumber) {
			  XNumber xNumber = (XNumber)xObj0;
			  Double dbl = xNumber.num();
			  
			  if (dbl.isNaN()) {
				  return XBoolean.S_FALSE; 
			  }
		  }
		  else if (xObj0 instanceof XSDecimal) {
			  XSDecimal xsDecimal = (XSDecimal)xObj0;
			  BigDecimal bigDecimal = xsDecimal.getValue();
			  BigDecimal bigDecimalZero = BigDecimal.valueOf(0); 
			  
			  if (bigDecimal.compareTo(bigDecimalZero) == 0) {
				 return XBoolean.S_FALSE; 
			  }
		  }
		  else if (xObj0 instanceof XSInteger) {
			  XSInteger xsInteger = (XSInteger)xObj0;
			  BigInteger bigInt = xsInteger.intValue();
			  BigInteger bigIntZero = BigInteger.valueOf(0);
			  
			  if (bigInt.compareTo(bigIntZero) == 0) {
				  return XBoolean.S_FALSE; 
			  }
		  }
		  else if (!((xObj0 instanceof ResultSequence) || (xObj0 instanceof XSBoolean) || (xObj0 instanceof XBoolean) || (xObj0 instanceof XBooleanStatic) || 
				     (xObj0 instanceof XSString) || (xObj0 instanceof XString) || (xObj0 instanceof XNumber) || (xObj0 instanceof XSNumericType) ||
				     (xObj0 instanceof XSAnyURI) || (xObj0 instanceof XMLNodeCursorImpl))) {
			  throw new TransformerException("FORG0006 : XPath 3.1 specifies effective boolean value only for schema types boolean, string, "
					  																							          + "number, uri and node. An "
					  																							          + "XPath operator 'and' first operand has wrong type.");
		  }
      }
	  
	  boolean lInf = false;	  
	  boolean lNumericOk = false;
	  
      XObject xObj1 = null;
	  
	  try {
	     xObj1 = m_right.execute(xctxt);
	  }
	  catch (Exception ex) {
		 // No op 
	  }
	  
	  if (xObj0 != null) {
		  if (xObj0 instanceof XSNumericType) {
			  java.lang.String str1 = ((XSNumericType)xObj0).stringValue();
			  
			  if (!("INF".equals(str1) || "-INF".equals(str1))) {
				  double dbl = Double.valueOf(str1);
				  
				  if (dbl == 0) {
					  return XBoolean.S_FALSE;
				  }
				  else {
					  lNumericOk = true;
				  }
			  }
			  else {
				  lInf = true;
			  }
		  }
		  else if (xObj0 instanceof XNumber) {
			  XNumber xNum = (XNumber)xObj0;
			  Double dbl = xNum.num();			
			  
			  if (!dbl.isInfinite()) {
				  if (dbl == 0) {
					  return XBoolean.S_FALSE;
				  }
				  else {
					  lNumericOk = true;
				  }
			  }
			  else {
				  lInf = true;
			  }
		  }
		  else if ((xObj1 instanceof XSAnyURI) || (xObj1 instanceof XSUntypedAtomic)) {
              java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj1);
			  
			  if (str1.length() > 0) {
				  return XBoolean.S_TRUE; 
			  } 
		  }
      }	  	  

	  if ((xObj0 == null) || a1 || lInf || lNumericOk)
	  {		  		  		  
		  
		  if (xObj1 instanceof XSString) {		  
			  xObj1 = new XString(((XSString)xObj1).stringValue()); 
		  }
		  
		  boolean a2 = false;
		  
		  try {
			  a2 = xObj1.bool();			  
			  
			  if (!a2) {
				  return XBoolean.S_FALSE;  
			  }
		  }
		  catch (Exception ex) {
			  // No op
		  }
		  
		  if (xObj1 != null) {
			  if ((xObj1 instanceof ResultSequence) && (((ResultSequence)xObj1).size() == 0)) {
				  return XBoolean.S_FALSE;
			  }
			  else if (xObj1 instanceof ResultSequence) {
				  XObject xObj = ((ResultSequence)xObj1).item(0);
				  
				  if ((xObj instanceof XSBoolean) || (xObj instanceof XBoolean) || (xObj instanceof XBooleanStatic)) {
					  if (!xObj.bool()) {
						  return XBoolean.S_FALSE; 
					  }
				  }
			  }
			  else if ((xObj1 instanceof XMLNodeCursorImpl) && (((XMLNodeCursorImpl)xObj1).getLength() == 0)) {
				  return XBoolean.S_FALSE;
			  }
			  else if ((xObj1 instanceof XSBoolean) || (xObj1 instanceof XBoolean) || (xObj1 instanceof XBooleanStatic)) {
				  if (!xObj1.bool()) {
					  return XBoolean.S_FALSE; 
				  }
			  }
			  else if ((xObj1 instanceof XSString) || (xObj1 instanceof XString)) {
				  java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj1);
				  
				  if (str1.length() == 0) {
					  return XBoolean.S_FALSE; 
				  }
			  }
			  else if ((xObj1 instanceof XSAnyURI) || (xObj1 instanceof XSUntypedAtomic)) {
				  java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj1);
				  
				  if (str1.length() == 0) {
					  return XBoolean.S_FALSE; 
				  }
				  
				  if ((xObj0 instanceof XSAnyURI) || (xObj0 instanceof XSUntypedAtomic)) {
					  return XBoolean.S_TRUE; 
				  }
			  }
			  else if (xObj1 instanceof XSFloat) {
				  XSFloat xsFloat = (XSFloat)xObj1;
				  
				  if (xsFloat.nan()) {
					  return XBoolean.S_FALSE; 
				  }
			  }
			  else if (xObj1 instanceof XSDouble) {
				  XSDouble xsDouble = (XSDouble)xObj1;
				  
				  if (xsDouble.nan()) {
					  return XBoolean.S_FALSE;
				  }
			  }	  
			  else if (xObj1 instanceof XNumber) {
				  XNumber xNumber = (XNumber)xObj1;
				  Double dbl = xNumber.num();
				  
				  if (dbl.isNaN()) {
					  return XBoolean.S_FALSE; 
				  }
			  }
			  else if (xObj1 instanceof XSDecimal) {
				  XSDecimal xsDecimal = (XSDecimal)xObj1;
				  BigDecimal bigDecimal = xsDecimal.getValue();
				  BigDecimal bigDecimalZero = BigDecimal.valueOf(0); 
				  
				  if (bigDecimal.compareTo(bigDecimalZero) == 0) {
					 return XBoolean.S_FALSE; 
				  }
			  }
			  else if (xObj1 instanceof XSInteger) {
				  XSInteger xsInteger = (XSInteger)xObj1;
				  BigInteger bigInt = xsInteger.intValue();
				  BigInteger bigIntZero = BigInteger.valueOf(0);
				  
				  if (bigInt.compareTo(bigIntZero) == 0) {
					  return XBoolean.S_FALSE; 
				  }
			  }
			  else if (!((xObj1 instanceof ResultSequence) || (xObj1 instanceof XSBoolean) || (xObj1 instanceof XBoolean) || (xObj1 instanceof XBooleanStatic) || 
					     (xObj1 instanceof XSString) || (xObj1 instanceof XString) || (xObj1 instanceof XNumber) || (xObj1 instanceof XSNumericType) ||
					     (xObj1 instanceof XSAnyURI) || (xObj1 instanceof XMLNodeCursorImpl))) {
				  throw new TransformerException("FORG0006 : XPath 3.1 specifies effective boolean value only for schema types boolean, string, "
					          																								  + "number, uri and node. An "
					          																								  + "XPath operator 'and' second operand has wrong type.");
			  }
	      }
		  
		  boolean rInf = false;
		  boolean rNumericOk = false;

		  if (xObj1 != null) {
			  if (xObj1 instanceof XSNumericType) {
				  java.lang.String str1 = ((XSNumericType)xObj1).stringValue();
				  
				  if (!("INF".equals(str1) || "-INF".equals(str1))) {
					  double dbl = Double.valueOf(str1);
					  
					  if (dbl == 0) {
						  return XBoolean.S_FALSE;
					  }
					  else {
						  rNumericOk = true;
					  }
				  }
				  else {
					  rInf = true;
				  }
			  }

			  if (xObj1 instanceof XNumber) {
				  XNumber xNum = (XNumber)xObj1;
				  Double dbl = xNum.num();			
				  
				  if (!dbl.isInfinite()) {
					  if (dbl == 0) {
						  return XBoolean.S_FALSE;
					  }
					  else {
						  rNumericOk = true;
					  }
				  }
				  else {
					  rInf = true;
				  }
			  }
	      }

		  return (a2 || rInf || rNumericOk) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
	  }	  
	  else
		  return XBoolean.S_FALSE;
  }
  
  /**
   * Evaluate XPath 3.1 'and' operator directly to a boolean.
   *
   * @param xctxt                  An XPath context object
   *
   * @return                       The result of evaluation as a 
   *                               boolean.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean bool(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {      	  
	  boolean result = false;
	  
	  XObject xObj =  execute(xctxt);	  
	  result = xObj.bool(); 
	  
	  return result;
  }

}
