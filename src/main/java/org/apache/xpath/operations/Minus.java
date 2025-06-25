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
 * An XPath '-' operation implementation.
 * 
 * @author Scott Boag <scott_boag@us.ibm.com>
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 *         (XSLT 3 specific changes, to this class)
 */
public class Minus extends ArithmeticOperation
{
   static final long serialVersionUID = -5297672838170871043L;

  /**
   * Apply the operation to two operands, and return the result.
   *
   * @param left non-null reference to the evaluated left operand.
   * @param right non-null reference to the evaluated right operand.
   *
   * @return non-null reference to the XObject that represents the 
   *         result of the operation.
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
      
      if ((left instanceof XSUntyped) && (right instanceof XSUntyped)) {
          java.lang.String lStrVal = ((XSUntyped)left).stringValue();
          double lDouble = (Double.valueOf(lStrVal)).doubleValue();
          
          java.lang.String rStrVal = ((XSUntyped)right).stringValue();
          double rDouble = (Double.valueOf(rStrVal)).doubleValue();
          
          result = new XSDouble(lDouble - rDouble);
      }
      else if ((left instanceof XSUntypedAtomic) && (right instanceof XSUntypedAtomic)) {
          java.lang.String lStrVal = ((XSUntypedAtomic)left).stringValue();
          double lDouble = (Double.valueOf(lStrVal)).doubleValue();
          
          java.lang.String rStrVal = ((XSUntypedAtomic)right).stringValue();
          double rDouble = (Double.valueOf(rStrVal)).doubleValue();
          
          result = new XSDouble(lDouble - rDouble);
      }
      else if ((left instanceof XSUntyped) && (right instanceof XSUntypedAtomic)) {
          java.lang.String lStrVal = ((XSUntyped)left).stringValue();
          double lDouble = (Double.valueOf(lStrVal)).doubleValue();
          
          java.lang.String rStrVal = ((XSUntypedAtomic)right).stringValue();
          double rDouble = (Double.valueOf(rStrVal)).doubleValue();
          
          result = new XSDouble(lDouble - rDouble);
      }
      else if ((left instanceof XSUntypedAtomic) && (right instanceof XSUntyped)) {
          java.lang.String lStrVal = ((XSUntypedAtomic)left).stringValue();
          double lDouble = (Double.valueOf(lStrVal)).doubleValue();
          
          java.lang.String rStrVal = ((XSUntyped)right).stringValue();
          double rDouble = (Double.valueOf(rStrVal)).doubleValue();
          
          result = new XSDouble(lDouble - rDouble);
      }      
      else if ((left instanceof XNumber) && (right instanceof XSNumericType)) {
    	  XNumber rightXNumber = getXNumberFromXSNumericType((XSNumericType)right);
    	  result = arithmeticOpOnXNumberValues((XNumber)left, rightXNumber, OP_SYMBOL_MINUS);    	  
      }
      else if ((left instanceof XSNumericType) && (right instanceof XNumber)) {
    	  XNumber leftXNumber = getXNumberFromXSNumericType((XSNumericType)left);
    	  result = arithmeticOpOnXNumberValues(leftXNumber, (XNumber)right, OP_SYMBOL_MINUS);
      }      
      else if ((left instanceof XSNumericType) && (right instanceof XSNumericType)) {
    	  XNumber leftXNumber = getXNumberFromXSNumericType((XSNumericType)left);
    	  XNumber rightXNumber = getXNumberFromXSNumericType((XSNumericType)right);
    	  result = arithmeticOpOnXNumberValues(leftXNumber, rightXNumber, OP_SYMBOL_MINUS);
      }
      else if ((left instanceof XNumber) && (right instanceof XNumber)) {
    	  XNumber lNumber = (XNumber)left;
    	  XNumber rNumber = (XNumber)right;
    	  result = arithmeticOpOnXNumberValues(lNumber, rNumber, OP_SYMBOL_MINUS);
      }
      else if ((left instanceof XNumber) && (right instanceof XMLNodeCursorImpl)) {          
          XMLNodeCursorImpl rNodeSet = (XMLNodeCursorImpl)right;
          if (rNodeSet.getLength() > 1) {
        	  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MINUS});  
          }
          else {
        	  BigDecimal lBigDecimal = BigDecimal.valueOf(((XNumber)left).num());
        	  BigDecimal rBigDecimal = null;
        	  try {
        	     rBigDecimal = new BigDecimal(rNodeSet.str());
        	  }
        	  catch (NumberFormatException ex) {
        		 error(OPERAND_NOT_NUMERIC_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MINUS}); 
        	  }
        	  
        	  BigDecimal resultBigDecimal = lBigDecimal.subtract(rBigDecimal);
        	  if (resultBigDecimal.compareTo(new BigDecimal(resultBigDecimal.toBigInteger())) == 0) {
          		 result = new XSInteger(resultBigDecimal.toBigInteger()); 
          	  }
          	  else {
                 result = new XSDecimal(resultBigDecimal);
          	  }
          }
      }
      else if ((left instanceof XMLNodeCursorImpl) && (right instanceof XNumber)) {
    	  XMLNodeCursorImpl lNodeSet = (XMLNodeCursorImpl)left;
          if (lNodeSet.getLength() > 1) {
        	  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MINUS});  
          }
          else {        	  
        	  BigDecimal lBigDecimal = null;
        	  try {
        		 lBigDecimal = new BigDecimal(lNodeSet.str());
        	  }
        	  catch (NumberFormatException ex) {
        		 error(OPERAND_NOT_NUMERIC_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MINUS}); 
        	  }
        	  BigDecimal rBigDecimal = BigDecimal.valueOf(((XNumber)right).num());
        	  
        	  BigDecimal resultBigDecimal = lBigDecimal.subtract(rBigDecimal);
        	  if (resultBigDecimal.compareTo(new BigDecimal(resultBigDecimal.toBigInteger())) == 0) {
          		 result = new XSInteger(resultBigDecimal.toBigInteger()); 
          	  }
          	  else {
                 result = new XSDecimal(resultBigDecimal);
          	  }
          }
      }
      else if ((left instanceof XSNumericType) && (right instanceof XMLNodeCursorImpl)) {
    	  XMLNodeCursorImpl rNodeSet = (XMLNodeCursorImpl)right;
          if (rNodeSet.getLength() > 1) {
        	  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MINUS});  
          }
          else {
        	  BigDecimal lBigDecimal = new BigDecimal(((XSNumericType)left).stringValue());
        	  BigDecimal rBigDecimal = null;
        	  try {
        	     rBigDecimal = new BigDecimal(rNodeSet.str());
        	  }
        	  catch (NumberFormatException ex) {
        		 error(OPERAND_NOT_NUMERIC_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MINUS}); 
        	  }
        	  
        	  BigDecimal resultBigDecimal = lBigDecimal.subtract(rBigDecimal);
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
        	  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MINUS});  
          }
          else {        	  
        	  BigDecimal lBigDecimal = null;
        	  try {
        		 lBigDecimal = new BigDecimal(lNodeSet.str());
        	  }
        	  catch (NumberFormatException ex) {
        		 error(OPERAND_NOT_NUMERIC_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MINUS}); 
        	  }
        	  BigDecimal rBigDecimal = new BigDecimal(((XSNumericType)right).stringValue());
        	  
        	  BigDecimal resultBigDecimal = lBigDecimal.subtract(rBigDecimal);
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
        	  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MINUS});  
          }
          
          XMLNodeCursorImpl rNodeSet = (XMLNodeCursorImpl)right;
          if (rNodeSet.getLength() > 1) {
        	  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MINUS});  
          }
          
          BigDecimal lBigDecimal = null;
    	  try {
    		 lBigDecimal = new BigDecimal(lNodeSet.str());
    	  }
    	  catch (NumberFormatException ex) {
    		 error(OPERAND_NOT_NUMERIC_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MINUS}); 
    	  }
    	  
    	  BigDecimal rBigDecimal = null;
    	  try {
    	     rBigDecimal = new BigDecimal(rNodeSet.str());
    	  }
    	  catch (NumberFormatException ex) {
    		 error(OPERAND_NOT_NUMERIC_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MINUS}); 
    	  }
    	  
    	  BigDecimal resultBigDecimal = lBigDecimal.subtract(rBigDecimal);
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
        	  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MINUS});  
          }
          else {        	  
        	  BigDecimal lBigDecimal = null;
        	  try {
        		 lBigDecimal = new BigDecimal(XslTransformEvaluationHelper.getStrVal(lSeq.item(0)));
        	  }
        	  catch (NumberFormatException ex) {
        		 error(OPERAND_NOT_NUMERIC_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MINUS}); 
        	  }
        	  BigDecimal rBigDecimal = BigDecimal.valueOf(((XNumber)right).num());
        	  
        	  BigDecimal resultBigDecimal = lBigDecimal.subtract(rBigDecimal);
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
        	  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MINUS});  
          }
          else {
        	  BigDecimal lBigDecimal = BigDecimal.valueOf(((XNumber)left).num());
        	  BigDecimal rBigDecimal = null;
        	  try {
        	     rBigDecimal = new BigDecimal(XslTransformEvaluationHelper.getStrVal(rSeq.item(0)));
        	  }
        	  catch (NumberFormatException ex) {
        		 error(OPERAND_NOT_NUMERIC_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MINUS}); 
        	  }
        	  
        	  BigDecimal resultBigDecimal = lBigDecimal.subtract(rBigDecimal);
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
        	  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MINUS});  
          }
          else {        	  
        	  BigDecimal lBigDecimal = null;
        	  try {
        		 lBigDecimal = new BigDecimal(XslTransformEvaluationHelper.getStrVal(lSeq.item(0)));
        	  }
        	  catch (NumberFormatException ex) {
        		 error(OPERAND_NOT_NUMERIC_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MINUS}); 
        	  }
        	  BigDecimal rBigDecimal = new BigDecimal(((XSNumericType)right).stringValue());
        	  
        	  BigDecimal resultBigDecimal = lBigDecimal.subtract(rBigDecimal);
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
        	  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MINUS});  
          }
          else {
        	  BigDecimal lBigDecimal = new BigDecimal(((XSNumericType)left).stringValue());
        	  BigDecimal rBigDecimal = null;
        	  try {
        	     rBigDecimal = new BigDecimal(XslTransformEvaluationHelper.getStrVal(rSeq.item(0)));
        	  }
        	  catch (NumberFormatException ex) {
        		 error(OPERAND_NOT_NUMERIC_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MINUS}); 
        	  }
        	  
        	  BigDecimal resultBigDecimal = lBigDecimal.subtract(rBigDecimal);
        	  if (resultBigDecimal.compareTo(new BigDecimal(resultBigDecimal.toBigInteger())) == 0) {
          		 result = new XSInteger(resultBigDecimal.toBigInteger()); 
          	  }
          	  else {
                 result = new XSDecimal(resultBigDecimal);
          	  }
          } 
      }
      else if ((left instanceof XSYearMonthDuration) && (right instanceof XSYearMonthDuration)) {
          result = ((XSYearMonthDuration)left).subtract((XSYearMonthDuration)right);
          
          return result;
      }
      else if (left instanceof XSDate) {
          result = ((XSDate)left).subtract(right);  
      }
      else if (left instanceof XSDateTime) {
          result = ((XSDateTime)left).subtract(right);  
      }
      else if (left instanceof XSTime) {
          result = ((XSTime)left).subtract(right);  
      }
      else if ((left instanceof ResultSequence) && (right instanceof ResultSequence)) {
          ResultSequence lSeq = (ResultSequence)left;          
          if (lSeq.size() > 1) {
        	  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MINUS});  
          }
          
          ResultSequence rSeq = (ResultSequence)right;          
          if (rSeq.size() > 1) {
        	  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MINUS}); 
          }
          
          XObject lXObj = ((ResultSequence)left).item(0);
          XObject rXObj = ((ResultSequence)right).item(0);
          
          if (lXObj instanceof XSDate) {
              result = ((XSDate)lXObj).subtract(rXObj); 
          }
          else if (lXObj instanceof XSDateTime) {
              result = ((XSDateTime)lXObj).subtract(rXObj); 
          }
          else if (lXObj instanceof XSTime) {
              result = ((XSTime)lXObj).subtract(rXObj); 
          }
          else {
        	  BigDecimal lBigDecimal = null;
        	  BigDecimal rBigDecimal = null;        	  
        	  try {
	              java.lang.String lStr = XslTransformEvaluationHelper.getStrVal(lXObj);
	              lBigDecimal = new BigDecimal(lStr);	              
	              java.lang.String rStr = XslTransformEvaluationHelper.getStrVal(rXObj);
	              rBigDecimal = new BigDecimal(rStr);
        	  }
        	  catch (NumberFormatException ex) {
        		  error(OPERAND_NOT_NUMERIC_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MINUS});
        	  }
        	  
        	  BigDecimal resultBigDecimal = lBigDecimal.subtract(rBigDecimal);
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
        	  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MINUS});  
          }
          
          XObject lXObj = ((ResultSequence)left).item(0);
          if (lXObj instanceof XSDate) {
              result = ((XSDate)lXObj).subtract(right); 
          }
          else if (lXObj instanceof XSDateTime) {
              result = ((XSDateTime)lXObj).subtract(right); 
          }
          else if (lXObj instanceof XSTime) {
              result = ((XSTime)lXObj).subtract(right); 
          }
          else {
        	  BigDecimal lBigDecimal = null;
        	  BigDecimal rBigDecimal = null;        	  
        	  try {
	              java.lang.String lStr = XslTransformEvaluationHelper.getStrVal(lXObj);
	              lBigDecimal = new BigDecimal(lStr);	              
	              java.lang.String rStr = XslTransformEvaluationHelper.getStrVal(right);
	              rBigDecimal = new BigDecimal(rStr);
        	  }
        	  catch (NumberFormatException ex) {
        		  error(OPERAND_NOT_NUMERIC_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MINUS});
        	  }
        	  
        	  BigDecimal resultBigDecimal = lBigDecimal.subtract(rBigDecimal);
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
        	  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MINUS});  
          }
          
          BigDecimal lBigDecimal = null;
          BigDecimal rBigDecimal = null;
    	  try {
    		 lBigDecimal = new BigDecimal(lNodeSet.str());
             rBigDecimal = new BigDecimal(XslTransformEvaluationHelper.getStrVal(right));
    	  }
    	  catch (NumberFormatException ex) {
    		 error(OPERAND_NOT_NUMERIC_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MINUS}); 
    	  }
        	  
          BigDecimal resultBigDecimal = lBigDecimal.subtract(rBigDecimal);
          if (resultBigDecimal.compareTo(new BigDecimal(resultBigDecimal.toBigInteger())) == 0) {
          	 result = new XSInteger(resultBigDecimal.toBigInteger()); 
          }
          else {
             result = new XSDecimal(resultBigDecimal);
          }
      }
      else {          
    	  try {
          	 java.lang.String lStrVal = XslTransformEvaluationHelper.getStrVal(left);
          	 java.lang.String rStrVal = XslTransformEvaluationHelper.getStrVal(right);
             result = new XNumber(Double.valueOf(lStrVal) - Double.valueOf(rStrVal));
          }
          catch (NumberFormatException ex) {
        	 error(OPERAND_NOT_NUMERIC_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MINUS}); 
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

    return (m_left.num(xctxt) - m_right.num(xctxt));
  }

}
