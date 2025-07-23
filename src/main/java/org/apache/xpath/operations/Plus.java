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

import java.math.BigDecimal;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.ArithmeticOperation;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSDate;
import xml.xpath31.processor.types.XSDateTime;
import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSInteger;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSTime;
import xml.xpath31.processor.types.XSUntyped;
import xml.xpath31.processor.types.XSUntypedAtomic;
import xml.xpath31.processor.types.XSYearMonthDuration;

/**
 * An XPath '+' operation implementation.
 * 
 * @author Scott Boag <scott_boag@us.ibm.com>
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 *         (XPath 3.1 specific changes, to this class)
 */
public class Plus extends ArithmeticOperation
{
   static final long serialVersionUID = -4492072861616504256L;

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
  public XObject operate(XObject left, XObject right) throws javax.xml.transform.TransformerException {
      
	  XObject result = null;
	  
	  Expression leftOperandExpr = getLeftOperand();	  
	  if (leftOperandExpr instanceof SelfIteratorNoPredicate) {
		 left = getModifiedOperandValue(left, (SelfIteratorNoPredicate)leftOperandExpr);
	  }
	  
      Expression rightOperandExpr = getRightOperand();	  
	  if (rightOperandExpr instanceof SelfIteratorNoPredicate) {
		 right = getModifiedOperandValue(right, (SelfIteratorNoPredicate)rightOperandExpr);
	  }
   
	  try {
		  if ((left instanceof XSUntyped) && (right instanceof XSUntyped)) {
			  java.lang.String lStrVal = ((XSUntyped)left).stringValue();
			  double lDouble = (Double.valueOf(lStrVal)).doubleValue();

			  java.lang.String rStrVal = ((XSUntyped)right).stringValue();
			  double rDouble = (Double.valueOf(rStrVal)).doubleValue();

			  result = new XSDouble(lDouble + rDouble);
		  }
		  else if ((left instanceof XSUntypedAtomic) && (right instanceof XSUntypedAtomic)) {
			  java.lang.String lStrVal = ((XSUntypedAtomic)left).stringValue();
			  double lDouble = (Double.valueOf(lStrVal)).doubleValue();

			  java.lang.String rStrVal = ((XSUntypedAtomic)right).stringValue();
			  double rDouble = (Double.valueOf(rStrVal)).doubleValue();

			  result = new XSDouble(lDouble + rDouble);
		  }
		  else if ((left instanceof XSUntyped) && (right instanceof XSUntypedAtomic)) {
			  java.lang.String lStrVal = ((XSUntyped)left).stringValue();
			  double lDouble = (Double.valueOf(lStrVal)).doubleValue();

			  java.lang.String rStrVal = ((XSUntypedAtomic)right).stringValue();
			  double rDouble = (Double.valueOf(rStrVal)).doubleValue();

			  result = new XSDouble(lDouble + rDouble);
		  }
		  else if ((left instanceof XSUntypedAtomic) && (right instanceof XSUntyped)) {
			  java.lang.String lStrVal = ((XSUntypedAtomic)left).stringValue();
			  double lDouble = (Double.valueOf(lStrVal)).doubleValue();

			  java.lang.String rStrVal = ((XSUntyped)right).stringValue();
			  double rDouble = (Double.valueOf(rStrVal)).doubleValue();

			  result = new XSDouble(lDouble + rDouble);
		  }
		  else if ((left instanceof XNumber) && (right instanceof XSNumericType)) {
			  XNumber rightXNumber = getXNumberFromXSNumericType((XSNumericType)right);
			  
			  result = arithmeticOpOnXNumberValues((XNumber)left, rightXNumber, OP_SYMBOL_PLUS);
		  }
		  else if ((left instanceof XSNumericType) && (right instanceof XNumber)) {
			  XNumber leftXNumber = getXNumberFromXSNumericType((XSNumericType)left);
			  
			  result = arithmeticOpOnXNumberValues(leftXNumber, (XNumber)right, OP_SYMBOL_PLUS);
		  }      
		  else if ((left instanceof XSNumericType) && (right instanceof XSNumericType)) { 
			  XNumber leftXNumber = getXNumberFromXSNumericType((XSNumericType)left);
			  XNumber rightXNumber = getXNumberFromXSNumericType((XSNumericType)right);
			  
			  result = arithmeticOpOnXNumberValues(leftXNumber, rightXNumber, OP_SYMBOL_PLUS);
		  }
		  else if ((left instanceof XNumber) && (right instanceof XNumber)) {          
			  XNumber lNumber = (XNumber)left;
			  XNumber rNumber = (XNumber)right;
			  
			  result = arithmeticOpOnXNumberValues(lNumber, rNumber, OP_SYMBOL_PLUS);
		  }
		  else if ((left instanceof XNumber) && (right instanceof XMLNodeCursorImpl)) {
			  double lDouble = ((XNumber)left).num();

			  XMLNodeCursorImpl rNodeSet = (XMLNodeCursorImpl)right;
			  if (rNodeSet.getLength() > 1) {
				  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_PLUS});  
			  }
			  else {
				  java.lang.String rStrVal = rNodeSet.str();
				  double rDouble = (Double.valueOf(rStrVal)).doubleValue();

				  result = new XNumber(lDouble + rDouble);
			  }
		  }
		  else if ((left instanceof XMLNodeCursorImpl) && (right instanceof XNumber)) {
			  double rDouble = ((XNumber)right).num();

			  XMLNodeCursorImpl lNodeSet = (XMLNodeCursorImpl)left;
			  if (lNodeSet.getLength() > 1) {
				  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_PLUS});  
			  }
			  else {
				  java.lang.String lStrVal = lNodeSet.str();
				  double lDouble = (Double.valueOf(lStrVal)).doubleValue();

				  result = new XNumber(lDouble + rDouble);
			  }
		  }
		  else if ((left instanceof XSNumericType) && (right instanceof XMLNodeCursorImpl)) {
			  XMLNodeCursorImpl rNodeSet = (XMLNodeCursorImpl)right;
			  if (rNodeSet.getLength() > 1) {
				  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_PLUS});  
			  }
			  else {
				  BigDecimal lBigDecimal = new BigDecimal(((XSNumericType)left).stringValue());
				  BigDecimal rBigDecimal = new BigDecimal(rNodeSet.str());				  
				  
				  BigDecimal resultBigDecimal = lBigDecimal.add(rBigDecimal);
				  if (resultBigDecimal.compareTo(new BigDecimal(resultBigDecimal.toBigInteger())) == 0) {
					  result = new XSInteger(resultBigDecimal.toBigInteger()); 
				  }
				  else {
					  result = new XSDecimal(resultBigDecimal);
				  }
			  }
		  }
		  else if ((left instanceof XMLNodeCursorImpl) && (right instanceof XSNumericType)) {
			  XMLNodeCursorImpl lNodeSet = (XMLNodeCursorImpl)left;
			  if (lNodeSet.getLength() > 1) {
				  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_PLUS});  
			  }
			  else {        	  
				  BigDecimal lBigDecimal = new BigDecimal(lNodeSet.str());				  
				  BigDecimal rBigDecimal = new BigDecimal(((XSNumericType)right).stringValue());
				  
				  BigDecimal resultBigDecimal = lBigDecimal.add(rBigDecimal);
				  if (resultBigDecimal.compareTo(new BigDecimal(resultBigDecimal.toBigInteger())) == 0) {
					  result = new XSInteger(resultBigDecimal.toBigInteger()); 
				  }
				  else {
					  result = new XSDecimal(resultBigDecimal);
				  }
			  }
		  }
		  else if ((left instanceof XMLNodeCursorImpl) && (right instanceof XMLNodeCursorImpl)) {
			  XMLNodeCursorImpl lNodeSet = (XMLNodeCursorImpl)left;
			  if (lNodeSet.getLength() > 1) {
				  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_PLUS});  
			  }

			  XMLNodeCursorImpl rNodeSet = (XMLNodeCursorImpl)right;
			  if (rNodeSet.getLength() > 1) {
				  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_PLUS});  
			  }
			  BigDecimal lBigDecimal = new BigDecimal(lNodeSet.str());			  
			  BigDecimal rBigDecimal = new BigDecimal(rNodeSet.str());
			  
			  BigDecimal resultBigDecimal = lBigDecimal.add(rBigDecimal);
			  if (resultBigDecimal.compareTo(new BigDecimal(resultBigDecimal.toBigInteger())) == 0) {
				  result = new XSInteger(resultBigDecimal.toBigInteger()); 
			  }
			  else {
				  result = new XSDecimal(resultBigDecimal);
			  }
		  }      
		  else if ((left instanceof ResultSequence) && (right instanceof XNumber)) {
			  ResultSequence lSeq = (ResultSequence)left;
			  if (lSeq.size() > 1) {
				  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_PLUS});  
			  }
			  else {        	  
				  BigDecimal lBigDecimal = new BigDecimal(XslTransformEvaluationHelper.getStrVal(lSeq.item(0)));				  
				  BigDecimal rBigDecimal = BigDecimal.valueOf(((XNumber)right).num());
				  
				  BigDecimal resultBigDecimal = lBigDecimal.add(rBigDecimal);
				  if (resultBigDecimal.compareTo(new BigDecimal(resultBigDecimal.toBigInteger())) == 0) {
					  result = new XSInteger(resultBigDecimal.toBigInteger()); 
				  }
				  else {
					  result = new XSDecimal(resultBigDecimal);
				  }
			  }
		  }
		  else if ((left instanceof XNumber) && (right instanceof ResultSequence)) {
			  ResultSequence rSeq = (ResultSequence)right;
			  if (rSeq.size() > 1) {
				  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_PLUS});  
			  }
			  else {
				  BigDecimal lBigDecimal = BigDecimal.valueOf(((XNumber)left).num());
				  BigDecimal rBigDecimal = new BigDecimal(XslTransformEvaluationHelper.getStrVal(rSeq.item(0)));				  
				  
				  BigDecimal resultBigDecimal = lBigDecimal.add(rBigDecimal);
				  if (resultBigDecimal.compareTo(new BigDecimal(resultBigDecimal.toBigInteger())) == 0) {
					  result = new XSInteger(resultBigDecimal.toBigInteger()); 
				  }
				  else {
					  result = new XSDecimal(resultBigDecimal);
				  }
			  }
		  }
		  else if ((left instanceof ResultSequence) && (right instanceof XSNumericType)) {
			  ResultSequence lSeq = (ResultSequence)left;
			  if (lSeq.size() > 1) {
				  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_PLUS});  
			  }
			  else {        	  
				  BigDecimal lBigDecimal = new BigDecimal(XslTransformEvaluationHelper.getStrVal(lSeq.item(0)));				  
				  BigDecimal rBigDecimal = new BigDecimal(((XSNumericType)right).stringValue());

				  BigDecimal resultBigDecimal = lBigDecimal.add(rBigDecimal);
				  if (resultBigDecimal.compareTo(new BigDecimal(resultBigDecimal.toBigInteger())) == 0) {
					  result = new XSInteger(resultBigDecimal.toBigInteger()); 
				  }
				  else {
					  result = new XSDecimal(resultBigDecimal);
				  }
			  }
		  }
		  else if ((left instanceof XSNumericType) && (right instanceof ResultSequence)) {
			  ResultSequence rSeq = (ResultSequence)right;
			  if (rSeq.size() > 1) {
				  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_PLUS});  
			  }
			  else {
				  BigDecimal lBigDecimal = new BigDecimal(((XSNumericType)left).stringValue());
				  BigDecimal rBigDecimal = new BigDecimal(XslTransformEvaluationHelper.getStrVal(rSeq.item(0)));				  

				  BigDecimal resultBigDecimal = lBigDecimal.add(rBigDecimal);
				  if (resultBigDecimal.compareTo(new BigDecimal(resultBigDecimal.toBigInteger())) == 0) {
					  result = new XSInteger(resultBigDecimal.toBigInteger()); 
				  }
				  else {
					  result = new XSDecimal(resultBigDecimal);
				  }
			  }
		  }
		  else if ((left instanceof XSYearMonthDuration) && (right instanceof XSYearMonthDuration)) {
			  result = ((XSYearMonthDuration)left).add((XSYearMonthDuration)right);  
		  }
		  else if (left instanceof XSDate) {
			  result = ((XSDate)left).add(right);  
		  }
		  else if (left instanceof XSDateTime) {
			  result = ((XSDateTime)left).add(right);  
		  }
		  else if (left instanceof XSTime) {
			  result = ((XSTime)left).add(right);  
		  }
		  else if ((left instanceof ResultSequence) && (right instanceof ResultSequence)) {
			  ResultSequence lSeq = (ResultSequence)left;          
			  if (lSeq.size() > 1) {
				  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_PLUS});  
			  }

			  ResultSequence rSeq = (ResultSequence)right;
			  if (rSeq.size() > 1) {
				  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_PLUS}); 
			  }

			  XObject lXObj = ((ResultSequence)left).item(0);
			  XObject rXObj = ((ResultSequence)right).item(0);

			  if (lXObj instanceof XSDate) {
				  result = ((XSDate)lXObj).add(rXObj); 
			  }
			  else if (lXObj instanceof XSDateTime) {
				  result = ((XSDateTime)lXObj).add(rXObj); 
			  }
			  else if (lXObj instanceof XSTime) {
				  result = ((XSTime)lXObj).add(rXObj); 
			  }
			  else {				  
				  java.lang.String lStr = XslTransformEvaluationHelper.getStrVal(lXObj);
				  BigDecimal lBigDecimal = new BigDecimal(lStr);	              
				  java.lang.String rStr = XslTransformEvaluationHelper.getStrVal(rXObj);
				  BigDecimal rBigDecimal = new BigDecimal(rStr);

				  BigDecimal resultBigDecimal = lBigDecimal.add(rBigDecimal);
				  if (resultBigDecimal.compareTo(new BigDecimal(resultBigDecimal.toBigInteger())) == 0) {
					  result = new XSInteger(resultBigDecimal.toBigInteger()); 
				  }
				  else {
					  result = new XSDecimal(resultBigDecimal);
				  }
			  }
		  }
		  else if (left instanceof ResultSequence) {
			  ResultSequence lSeq = (ResultSequence)left;          
			  if (lSeq.size() > 1) {
				  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_PLUS});  
			  }

			  XObject lXObj = ((ResultSequence)left).item(0);
			  if (lXObj instanceof XSDate) {
				  result = ((XSDate)lXObj).add(right); 
			  }
			  else if (lXObj instanceof XSDateTime) {
				  result = ((XSDateTime)lXObj).add(right); 
			  }
			  else if (lXObj instanceof XSTime) {
				  result = ((XSTime)lXObj).add(right); 
			  }
			  else {        	  
				  java.lang.String lStr = XslTransformEvaluationHelper.getStrVal(lXObj);
				  BigDecimal lBigDecimal = new BigDecimal(lStr);	              
				  java.lang.String rStr = XslTransformEvaluationHelper.getStrVal(right);
				  BigDecimal rBigDecimal = new BigDecimal(rStr);

				  BigDecimal resultBigDecimal = lBigDecimal.add(rBigDecimal);
				  if (resultBigDecimal.compareTo(new BigDecimal(resultBigDecimal.toBigInteger())) == 0) {
					  result = new XSInteger(resultBigDecimal.toBigInteger()); 
				  }
				  else {
					  result = new XSDecimal(resultBigDecimal);
				  }
			  }
		  }
		  else if (left instanceof XMLNodeCursorImpl) {
			  XMLNodeCursorImpl lNodeSet = (XMLNodeCursorImpl)left;
			  if (lNodeSet.getLength() > 1) {
				  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_PLUS}); 
			  }

			  BigDecimal lBigDecimal = new BigDecimal(lNodeSet.str());
			  BigDecimal rBigDecimal = new BigDecimal(XslTransformEvaluationHelper.getStrVal(right));			  

			  BigDecimal resultBigDecimal = lBigDecimal.add(rBigDecimal);
			  if (resultBigDecimal.compareTo(new BigDecimal(resultBigDecimal.toBigInteger())) == 0) {
				  result = new XSInteger(resultBigDecimal.toBigInteger()); 
			  }
			  else {
				  result = new XSDecimal(resultBigDecimal);
			  }
		  }
		  else {
			  java.lang.String lStrVal = XslTransformEvaluationHelper.getStrVal(left);
			  java.lang.String rStrVal = XslTransformEvaluationHelper.getStrVal(right);
			  
			  result = new XNumber(Double.valueOf(lStrVal) + Double.valueOf(rStrVal));			 
		  }
	  }
	  catch (NumberFormatException ex) {
		  java.lang.String lStrValue = null;
		  if (left instanceof XMLNodeCursorImpl) {
			  lStrValue = ((XMLNodeCursorImpl)left).str(); 
		  }
		  java.lang.String rStrValue = null;
		  if (right instanceof XMLNodeCursorImpl) {
			  rStrValue = ((XMLNodeCursorImpl)right).str(); 
		  }
		  
		  if ("".equals(lStrValue) || "".equals(rStrValue)) {
		     result = new ResultSequence();
		  }
		  else {
			 throw new javax.xml.transform.TransformerException("XPTY0004 : One or both of the operands of operator + are not numeric."); 
		  }
	  }
      
      return result;
  }

  /**
   * Evaluate this operation directly to a double.
   *
   * @param xctxt The runtime execution context.
   *
   * @return The result of the operation as a double.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public double num(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {

    return (m_right.num(xctxt) + m_left.num(xctxt));
  }
  
  /**
   * Add a value of type XSNumericType, to another value of type 
   * XSNumericType. 
   */
  private XObject addXSNumericTypeToXsNumericType(XSNumericType leftVal, XSNumericType rightVal) {

	  XObject result = null;

	  if ((leftVal instanceof XSDecimal) || (rightVal instanceof XSDecimal)) {
		  BigDecimal lBigDecimal = new BigDecimal(leftVal.stringValue());
		  BigDecimal rBigDecimal = new BigDecimal(rightVal.stringValue());

		  result = new XSDecimal((lBigDecimal.add(rBigDecimal)).toString());
	  }
	  else {
		  Double lDouble = Double.valueOf(leftVal.stringValue());
		  Double rDouble = Double.valueOf(rightVal.stringValue());

		  result = new XSDouble(lDouble.doubleValue() + rDouble.doubleValue());  
	  }
	  
	  return result;
  }

  /**
   * Add XNumber value to an XSNumericType value.
   */
  private XObject addXNumberToXsNumericType(XNumber leftVal, XSNumericType rightVal) {
	  
      XObject result = null;
	  
	  if (rightVal instanceof XSDecimal) {
	      java.lang.String rStrVal = rightVal.stringValue();		  		  
	      java.math.BigDecimal rBigDecimal = new java.math.BigDecimal(rStrVal);
	      double lDouble = leftVal.num();
		  java.math.BigDecimal resultVal = rBigDecimal.add(new java.math.BigDecimal(lDouble));
		  result = new XSDecimal(resultVal);
	  }	      	  
	  else {
		  double lDouble = leftVal.num();
		  java.lang.String rStrVal = rightVal.stringValue();
		  double rDouble = (Double.valueOf(rStrVal)).doubleValue();		  
		  result = new XSDouble(lDouble + rDouble);
	  }
	  
	  return result;
  }

}
