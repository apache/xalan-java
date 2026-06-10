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
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSFloat;
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSUntypedAtomic;

/**
 * An XPath 3.1 'or' expression evaluator.
 */
public class Or extends Operation
{
  static final long serialVersionUID = -644107191353853079L;

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

	  XObject expr1 = m_left.execute(xctxt);

	  boolean lBool = false;

	  if ((expr1 instanceof ResultSequence) && (((ResultSequence)expr1).size() > 0)) {
		  XObject xObj = ((ResultSequence)expr1).item(0);
		  if ((xObj instanceof XMLNodeCursorImpl) && (((XMLNodeCursorImpl)xObj).getLength() > 0)) {
			  return XBoolean.S_TRUE; 
		  }
		  else if ((xObj instanceof XSBoolean) || (xObj instanceof XBoolean) || (xObj instanceof XBooleanStatic)) {
			  if (xObj.bool()) {
				  return XBoolean.S_TRUE; 
			  }
		  }
	  }
	  else if ((expr1 instanceof XMLNodeCursorImpl) && (((XMLNodeCursorImpl)expr1).getLength() > 0)) {
		  return XBoolean.S_TRUE;
	  }
	  else if ((expr1 instanceof XSBoolean) || (expr1 instanceof XBoolean) || (expr1 instanceof XBooleanStatic)) {
		  if (expr1.bool()) {
			  return XBoolean.S_TRUE; 
		  }
	  }
	  else if ((expr1 instanceof XSString) || (expr1 instanceof XString)) {
		  java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(expr1);
		  if (str1.length() > 0) {
			  return XBoolean.S_TRUE; 
		  }
	  }
	  else if ((expr1 instanceof XSAnyURI) || (expr1 instanceof XSUntypedAtomic)) {
		  java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(expr1);
		  if (str1.length() > 0) {
			  return XBoolean.S_TRUE; 
		  }
	  }
	  else if (expr1 instanceof XSFloat) {
		  XSFloat xsFloat = (XSFloat)expr1;
		  if (!xsFloat.nan()) {
			  float flt1 = xsFloat.floatValue();
			  if (flt1 != 0) {
				  return XBoolean.S_TRUE; 
			  }
		  }
	  }
	  else if (expr1 instanceof XSDouble) {
		  XSDouble xsDouble = (XSDouble)expr1;
		  if (!xsDouble.nan()) {
			  double dbl1 = xsDouble.doubleValue();
			  if (dbl1 != 0) {
				  return XBoolean.S_TRUE; 
			  }
		  }
	  }	  
	  else if (expr1 instanceof XNumber) {
		  XNumber xNumber = (XNumber)expr1;
		  Double dbl1 = xNumber.num();
		  if (!(dbl1.isNaN() || (dbl1 == 0))) {
			  return XBoolean.S_TRUE; 
		  }
	  }
	  else {
		  lBool = expr1.bool(); 
	  }

	  if (!lBool)
	  {
		  XObject expr2 = m_right.execute(xctxt);
		  
		  boolean rBool = false;

		  if ((expr2 instanceof ResultSequence) && (((ResultSequence)expr2).size() > 0)) {
			  XObject xObj = ((ResultSequence)expr2).item(0);
			  if ((xObj instanceof XMLNodeCursorImpl) && (((XMLNodeCursorImpl)xObj).getLength() > 0)) {
				 return XBoolean.S_TRUE; 
			  }
			  else if ((xObj instanceof XSBoolean) || (xObj instanceof XBoolean) || (xObj instanceof XBooleanStatic)) {
				  if (xObj.bool()) {
					  return XBoolean.S_TRUE; 
				  }
			  }
		  }
		  else if ((expr2 instanceof XMLNodeCursorImpl) && (((XMLNodeCursorImpl)expr2).getLength() > 0)) {
			  return XBoolean.S_TRUE;
		  }
		  else if ((expr2 instanceof XSBoolean) || (expr2 instanceof XBoolean) || (expr2 instanceof XBooleanStatic)) {
			  if (expr2.bool()) {
				 return XBoolean.S_TRUE; 
			  }
		  }
		  else if ((expr2 instanceof XSString) || (expr2 instanceof XString)) {
			  java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(expr2);
			  if (str1.length() > 0) {
				 return XBoolean.S_TRUE; 
			  }
		  }
		  else if ((expr2 instanceof XSAnyURI) || (expr2 instanceof XSUntypedAtomic)) {
			  java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(expr2);
			  if (str1.length() > 0) {
				 return XBoolean.S_TRUE; 
			  }
		  }
		  else if (expr2 instanceof XSFloat) {
			  XSFloat xsFloat = (XSFloat)expr2;
			  if (!xsFloat.nan()) {
				  float flt1 = xsFloat.floatValue();
				  if (flt1 != 0) {
					  return XBoolean.S_TRUE; 
				  }
			  }
		  }
		  else if (expr2 instanceof XSDouble) {
			  XSDouble xsDouble = (XSDouble)expr2;
			  if (!xsDouble.nan()) {
				  double dbl1 = xsDouble.doubleValue();
				  if (dbl1 != 0) {
					  return XBoolean.S_TRUE; 
				  }
			  }
		  }	  
		  else if (expr2 instanceof XNumber) {
			  XNumber xNumber = (XNumber)expr2;
			  Double dbl1 = xNumber.num();
			  if (!(dbl1.isNaN() || (dbl1 == 0))) {
				  return XBoolean.S_TRUE; 
			  }
		  }
		  else {
			  rBool = expr2.bool(); 
		  }

		  return rBool ? XBoolean.S_TRUE : XBoolean.S_FALSE;
	  }
	  else
		  return XBoolean.S_TRUE;
  }
  
  /**
   * Evaluate XPath 3.1 'or' operator directly to a boolean.
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
