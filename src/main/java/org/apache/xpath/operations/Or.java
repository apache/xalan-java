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
 * An XPath 'or' expression evaluator.
 */
public class Or extends XPathOperator
{
  static final long serialVersionUID = -644107191353853079L;
  
  /**
   * Class field, representing an XPath 'or' expression's
   * first operand. This value may be null.
   */
  private java.lang.String m_l_str = null;
  
  /**
   * Class field, representing an XPath 'or' expression's
   * second operand. This value may be null.
   */
  private java.lang.String m_r_str = null;
  
  /**
   * Default constructor.
   */
  public Or() {
	 // No op 
  }
  
  /**
   * Class constructor.
   */
  public Or(java.lang.String str1, java.lang.String str2) {
	  m_l_str = str1;
	  m_r_str = str2;
  }

  /**
   * OR two expressions and return the boolean result. Override
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

	  XObject xObj0 = m_left.execute(xctxt);

	  boolean bool_0 = false;

	  if ((xObj0 instanceof ResultSequence) && (((ResultSequence)xObj0).size() > 0)) {
		  XObject xObj = ((ResultSequence)xObj0).item(0);
		  
		  if ((xObj instanceof XMLNodeCursorImpl) && (((XMLNodeCursorImpl)xObj).getLength() > 0)) {
			  return XBoolean.S_TRUE; 
		  }
		  else if ((xObj instanceof XSBoolean) || (xObj instanceof XBoolean) || (xObj instanceof XBooleanStatic)) {
			  if (xObj.bool()) {
				  return XBoolean.S_TRUE; 
			  }
		  }
	  }
	  else if ((xObj0 instanceof XMLNodeCursorImpl) && (((XMLNodeCursorImpl)xObj0).getLength() > 0)) {
		  return XBoolean.S_TRUE;
	  }
	  else if ((xObj0 instanceof XSBoolean) || (xObj0 instanceof XBoolean) || (xObj0 instanceof XBooleanStatic)) {
		  if (xObj0.bool()) {
			  return XBoolean.S_TRUE; 
		  }
	  }
	  else if ((xObj0 instanceof XSString) || (xObj0 instanceof XString)) {
		  java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj0);
		  
		  if (str1.length() > 0) {
			  return XBoolean.S_TRUE; 
		  }
	  }
	  else if ((xObj0 instanceof XSAnyURI) || (xObj0 instanceof XSUntypedAtomic)) {
		  java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj0);
		  
		  if (str1.length() > 0) {
			  return XBoolean.S_TRUE; 
		  }
	  }
	  else if (xObj0 instanceof XSFloat) {
		  XSFloat xsFloat = (XSFloat)xObj0;
		  
		  if (!xsFloat.nan()) {
			  float flt1 = xsFloat.floatValue();
			  
			  if (flt1 != 0) {
				  return XBoolean.S_TRUE; 
			  }
		  }
	  }
	  else if (xObj0 instanceof XSDouble) {
		  XSDouble xsDouble = (XSDouble)xObj0;
		  
		  if (!xsDouble.nan()) {
			  double dbl1 = xsDouble.doubleValue();
			  
			  if (dbl1 != 0) {
				  return XBoolean.S_TRUE; 
			  }
		  }
	  }	  
	  else if (xObj0 instanceof XNumber) {
		  XNumber xNumber = (XNumber)xObj0;
		  Double dbl1 = xNumber.num();
		  
		  if (!(dbl1.isNaN() || (dbl1 == 0))) {
			  return XBoolean.S_TRUE; 
		  }
	  }
	  else if (xObj0 instanceof XSDecimal) {
		  XSDecimal xsDecimal = (XSDecimal)xObj0;
		  BigDecimal bigDecimal = xsDecimal.getValue();
		  BigDecimal bigDecimalZero = BigDecimal.valueOf(0); 
		  
		  if (!(bigDecimal.compareTo(bigDecimalZero) == 0)) {
			 return XBoolean.S_TRUE; 
		  }
	  }
	  else if (xObj0 instanceof XSInteger) {
		  XSInteger xsInteger = (XSInteger)xObj0;
		  BigInteger bigInt = xsInteger.intValue();
		  BigInteger bigIntZero = BigInteger.valueOf(0);
		  
		  if (!(bigInt.compareTo(bigIntZero) == 0)) {
			  return XBoolean.S_TRUE; 
		  }
	  }
	  else if (!((xObj0 instanceof ResultSequence) || (xObj0 instanceof XSBoolean) || (xObj0 instanceof XBoolean) || (xObj0 instanceof XBooleanStatic) || 
			     (xObj0 instanceof XSString) || (xObj0 instanceof XString) || (xObj0 instanceof XNumber) || (xObj0 instanceof XSNumericType) ||
			     (xObj0 instanceof XSAnyURI) || (xObj0 instanceof XMLNodeCursorImpl))) {
		  throw new TransformerException("FORG0006 : XPath 3.1 specifies effective boolean value only for schema types boolean, string, "
		  		                                                                                                      + "number, uri and node. An "
		  		                                                                                                      + "XPath operator 'or' first operand has wrong type.");
	  }
	  else {
		  bool_0 = xObj0.bool(); 
	  }

	  if (!bool_0)
	  {
		  XObject xObj1 = m_right.execute(xctxt);
		  
		  boolean bool_1 = false;

		  if ((xObj1 instanceof ResultSequence) && (((ResultSequence)xObj1).size() > 0)) {
			  XObject xObj = ((ResultSequence)xObj1).item(0);
			  
			  if ((xObj instanceof XMLNodeCursorImpl) && (((XMLNodeCursorImpl)xObj).getLength() > 0)) {
				 return XBoolean.S_TRUE; 
			  }
			  else if ((xObj instanceof XSBoolean) || (xObj instanceof XBoolean) || (xObj instanceof XBooleanStatic)) {
				  if (xObj.bool()) {
					  return XBoolean.S_TRUE; 
				  }
			  }
		  }
		  else if ((xObj1 instanceof XMLNodeCursorImpl) && (((XMLNodeCursorImpl)xObj1).getLength() > 0)) {
			  return XBoolean.S_TRUE;
		  }
		  else if ((xObj1 instanceof XSBoolean) || (xObj1 instanceof XBoolean) || (xObj1 instanceof XBooleanStatic)) {
			  if (xObj1.bool()) {
				 return XBoolean.S_TRUE; 
			  }
		  }
		  else if ((xObj1 instanceof XSString) || (xObj1 instanceof XString)) {
			  java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj1);
			  
			  if (str1.length() > 0) {
				 return XBoolean.S_TRUE; 
			  }
		  }
		  else if ((xObj1 instanceof XSAnyURI) || (xObj1 instanceof XSUntypedAtomic)) {
			  java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj1);
			  
			  if (str1.length() > 0) {
				 return XBoolean.S_TRUE; 
			  }
		  }
		  else if (xObj1 instanceof XSFloat) {
			  XSFloat xsFloat = (XSFloat)xObj1;
			  
			  if (!xsFloat.nan()) {
				  float flt1 = xsFloat.floatValue();
				  
				  if (flt1 != 0) {
					  return XBoolean.S_TRUE; 
				  }
			  }
		  }
		  else if (xObj1 instanceof XSDouble) {
			  XSDouble xsDouble = (XSDouble)xObj1;
			  
			  if (!xsDouble.nan()) {
				  double dbl1 = xsDouble.doubleValue();
				  
				  if (dbl1 != 0) {
					  return XBoolean.S_TRUE; 
				  }
			  }
		  }	  
		  else if (xObj1 instanceof XNumber) {
			  XNumber xNumber = (XNumber)xObj1;
			  Double dbl1 = xNumber.num();
			  
			  if (!(dbl1.isNaN() || (dbl1 == 0))) {
				  return XBoolean.S_TRUE; 
			  }
		  }
		  else if (xObj1 instanceof XSDecimal) {
			  XSDecimal xsDecimal = (XSDecimal)xObj1;
			  BigDecimal bigDecimal = xsDecimal.getValue();
			  BigDecimal bigDecimalZero = BigDecimal.valueOf(0); 
			  
			  if (!(bigDecimal.compareTo(bigDecimalZero) == 0)) {
				 return XBoolean.S_TRUE; 
			  }
		  }
		  else if (xObj1 instanceof XSInteger) {
			  XSInteger xsInteger = (XSInteger)xObj1;
			  BigInteger bigInt = xsInteger.intValue();
			  BigInteger bigIntZero = BigInteger.valueOf(0);
			  
			  if (!(bigInt.compareTo(bigIntZero) == 0)) {
				  return XBoolean.S_TRUE; 
			  }
		  }
		  else if (!((xObj1 instanceof ResultSequence) || (xObj1 instanceof XSBoolean) || (xObj1 instanceof XBoolean) || (xObj1 instanceof XBooleanStatic) || 
				     (xObj1 instanceof XSString) || (xObj1 instanceof XString) || (xObj1 instanceof XNumber) || (xObj1 instanceof XSNumericType) ||
				     (xObj1 instanceof XSAnyURI) || (xObj1 instanceof XMLNodeCursorImpl))) {
			  throw new TransformerException("FORG0006 : XPath 3.1 specifies effective boolean value only for schema types boolean, string, "
                        																								  + "number, uri and node. An "
                        																								  + "XPath operator 'or' second operand has wrong type.");
		  }
		  else {
			  bool_1 = xObj1.bool(); 
		  }

		  return bool_1 ? XBoolean.S_TRUE : XBoolean.S_FALSE;
	  }
	  else
		  return XBoolean.S_TRUE;
	  
  }
  
  /**
   * Method definition, to evaluate XPath 3.1 'or' operator 
   * directly to a boolean.
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

  public java.lang.String getLStr() {
	  return m_l_str;
  }

  public void setLStr(java.lang.String l_str) {
	  this.m_l_str = l_str;
  }

  public java.lang.String getRStr() {
	  return m_r_str;
  }

  public void setRStr(java.lang.String r_str) {
	  this.m_r_str = r_str;
  }

}
