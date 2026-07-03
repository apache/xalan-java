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
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSUntypedAtomic;

/**
 * An XPath 3.1 'and' expression evaluator.
 */
public class And extends Operation
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

	  XObject xObjL = null;	
	  
	  try {
	     xObjL = m_left.execute(xctxt);
	  }
	  catch (Exception ex) {
		 // no op
	  }
	  
	  if (xObjL instanceof XSString) {		  
		  xObjL = new XString(((XSString)xObjL).stringValue()); 
	  }
	  
	  boolean a1 = false;
	  
	  try {
		  if (xObjL != null) {
			  a1 = xObjL.bool();
			  if (!a1) {
				  return XBoolean.S_FALSE;  
			  }
		  }		  
	  }
	  catch (Exception ex) {
		  // no op
	  }
	  	  
	  if (xObjL != null) {
		  if ((xObjL instanceof ResultSequence) && (((ResultSequence)xObjL).size() == 0)) {
			  return XBoolean.S_FALSE;
		  }
		  else if ((xObjL instanceof ResultSequence) && (((ResultSequence)xObjL).size() == 1)) {
			  XObject xObj = ((ResultSequence)xObjL).item(0);
			  if ((xObj instanceof XSBoolean) || (xObj instanceof XBoolean) || (xObj instanceof XBooleanStatic)) {
				  if (!xObj.bool()) {
					  return XBoolean.S_FALSE; 
				  }
			  }
		  }
		  else if ((xObjL instanceof XMLNodeCursorImpl) && (((XMLNodeCursorImpl)xObjL).getLength() == 0)) {
			  return XBoolean.S_FALSE;
		  }
		  else if ((xObjL instanceof XSBoolean) || (xObjL instanceof XBoolean) || (xObjL instanceof XBooleanStatic)) {
			  if (!xObjL.bool()) {
				  return XBoolean.S_FALSE; 
			  }
		  }
		  else if ((xObjL instanceof XSString) || (xObjL instanceof XString)) {
			  java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObjL);
			  if (str1.length() == 0) {
				  return XBoolean.S_FALSE; 
			  }
		  }
		  else if ((xObjL instanceof XSAnyURI) || (xObjL instanceof XSUntypedAtomic)) {
			  java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObjL);
			  if (str1.length() == 0) {
				  return XBoolean.S_FALSE; 
			  }
		  }
		  else if (xObjL instanceof XSFloat) {
			  XSFloat xsFloat = (XSFloat)xObjL;
			  if (xsFloat.nan()) {
				  return XBoolean.S_FALSE; 
			  }
		  }
		  else if (xObjL instanceof XSDouble) {
			  XSDouble xsDouble = (XSDouble)xObjL;
			  if (xsDouble.nan()) {
				  return XBoolean.S_FALSE;
			  }
		  }	  
		  else if (xObjL instanceof XNumber) {
			  XNumber xNumber = (XNumber)xObjL;
			  Double dbl = xNumber.num();
			  if (dbl.isNaN()) {
				  return XBoolean.S_FALSE; 
			  }
		  }		  		  
      }
	  
	  boolean lInf = false;	  
	  boolean lNumericOk = false;
	  
	  if (xObjL != null) {
		  if (xObjL instanceof XSNumericType) {
			  java.lang.String str1 = ((XSNumericType)xObjL).stringValue();
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
		  else if (xObjL instanceof XNumber) {
			  XNumber xNum = (XNumber)xObjL;
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
      }	  	  

	  if ((xObjL == null) || a1 || lInf || lNumericOk)
	  {		  
		  XObject xObjR = null;
		  
		  try {
		     xObjR = m_right.execute(xctxt);
		  }
		  catch (Exception ex) {
			 // no op 
		  }
		  
		  if (xObjR instanceof XSString) {		  
			  xObjR = new XString(((XSString)xObjR).stringValue()); 
		  }
		  
		  boolean a2 = false;
		  
		  try {
			  a2 = xObjR.bool();			  
			  if (!a2) {
				  return XBoolean.S_FALSE;  
			  }
		  }
		  catch (Exception ex) {
			  // no op
		  }
		  
		  if (xObjR != null) {
			  if ((xObjR instanceof ResultSequence) && (((ResultSequence)xObjR).size() == 0)) {
				  return XBoolean.S_FALSE;
			  }
			  else if ((xObjR instanceof ResultSequence) && (((ResultSequence)xObjR).size() == 1)) {
				  XObject xObj = ((ResultSequence)xObjR).item(0);
				  if ((xObj instanceof XSBoolean) || (xObj instanceof XBoolean) || (xObj instanceof XBooleanStatic)) {
					  if (!xObj.bool()) {
						  return XBoolean.S_FALSE; 
					  }
				  }
			  }
			  else if ((xObjR instanceof XMLNodeCursorImpl) && (((XMLNodeCursorImpl)xObjR).getLength() == 0)) {
				  return XBoolean.S_FALSE;
			  }
			  else if ((xObjR instanceof XSBoolean) || (xObjR instanceof XBoolean) || (xObjR instanceof XBooleanStatic)) {
				  if (!xObjR.bool()) {
					  return XBoolean.S_FALSE; 
				  }
			  }
			  else if ((xObjR instanceof XSString) || (xObjR instanceof XString)) {
				  java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObjR);
				  if (str1.length() == 0) {
					  return XBoolean.S_FALSE; 
				  }
			  }
			  else if ((xObjR instanceof XSAnyURI) || (xObjR instanceof XSUntypedAtomic)) {
				  java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObjR);
				  if (str1.length() == 0) {
					  return XBoolean.S_FALSE; 
				  }
			  }
			  else if (xObjR instanceof XSFloat) {
				  XSFloat xsFloat = (XSFloat)xObjR;
				  if (xsFloat.nan()) {
					  return XBoolean.S_FALSE; 
				  }
			  }
			  else if (xObjR instanceof XSDouble) {
				  XSDouble xsDouble = (XSDouble)xObjR;
				  if (xsDouble.nan()) {
					  return XBoolean.S_FALSE;
				  }
			  }	  
			  else if (xObjR instanceof XNumber) {
				  XNumber xNumber = (XNumber)xObjR;
				  Double dbl = xNumber.num();
				  if (dbl.isNaN()) {
					  return XBoolean.S_FALSE; 
				  }
			  }
	      }
		  
		  boolean rInf = false;
		  boolean rNumericOk = false;

		  if (xObjR != null) {
			  if (xObjR instanceof XSNumericType) {
				  java.lang.String str1 = ((XSNumericType)xObjR).stringValue();
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

			  if (xObjR instanceof XNumber) {
				  XNumber xNum = (XNumber)xObjR;
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
