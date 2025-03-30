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

import java.math.BigDecimal;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.ArithmeticOperation;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathException;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSUntyped;
import xml.xpath31.processor.types.XSUntypedAtomic;
import xml.xpath31.processor.types.XSYearMonthDuration;

import java.lang.String;

/**
 * An XPath 'div' operation implementation.
 * 
 * @author Scott Boag <scott_boag@us.ibm.com>
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 *         (XSLT 3 specific changes, to this class)
 */
public class Div extends ArithmeticOperation
{
   static final long serialVersionUID = 6220756595959798135L;

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
  public XObject operate(XObject left, XObject right) throws javax.xml.transform.TransformerException
  {  
     XObject result = null;
     
     if ((left instanceof XSUntyped) && (right instanceof XSUntyped)) {
         java.lang.String lStrVal = ((XSUntyped)left).stringValue();
         double lDouble = (Double.valueOf(lStrVal)).doubleValue();
         
         java.lang.String rStrVal = ((XSUntyped)right).stringValue();
         double rDouble = (Double.valueOf(rStrVal)).doubleValue();
         
         result = new XSDouble(lDouble / rDouble);
     }
     else if ((left instanceof XSUntypedAtomic) && (right instanceof XSUntypedAtomic)) {
         java.lang.String lStrVal = ((XSUntypedAtomic)left).stringValue();
         double lDouble = (Double.valueOf(lStrVal)).doubleValue();
         
         java.lang.String rStrVal = ((XSUntypedAtomic)right).stringValue();
         double rDouble = (Double.valueOf(rStrVal)).doubleValue();
         
         result = new XSDouble(lDouble / rDouble);
     }
     else if ((left instanceof XSUntyped) && (right instanceof XSUntypedAtomic)) {
         java.lang.String lStrVal = ((XSUntyped)left).stringValue();
         double lDouble = (Double.valueOf(lStrVal)).doubleValue();
         
         java.lang.String rStrVal = ((XSUntypedAtomic)right).stringValue();
         double rDouble = (Double.valueOf(rStrVal)).doubleValue();
         
         result = new XSDouble(lDouble / rDouble);
     }
     else if ((left instanceof XSUntypedAtomic) && (right instanceof XSUntyped)) {
         java.lang.String lStrVal = ((XSUntypedAtomic)left).stringValue();
         double lDouble = (Double.valueOf(lStrVal)).doubleValue();
         
         java.lang.String rStrVal = ((XSUntyped)right).stringValue();
         double rDouble = (Double.valueOf(rStrVal)).doubleValue();
         
         result = new XSDouble(lDouble / rDouble);
     }
     else if ((left instanceof XNumber) && (right instanceof XSNumericType)) {
    	XNumber rightXNumber = getXNumberFromXSNumericType((XSNumericType)right);
   	    result = arithmeticOpOnXNumberValues((XNumber)left, rightXNumber, OP_SYMBOL_DIV);
     }
     else if ((left instanceof XSNumericType) && (right instanceof XNumber)) {
    	XNumber leftXNumber = getXNumberFromXSNumericType((XSNumericType)left);
   	    result = arithmeticOpOnXNumberValues(leftXNumber, (XNumber)right, OP_SYMBOL_DIV);
     }     
     else if ((left instanceof XSNumericType) && (right instanceof XSNumericType)) {
    	XNumber leftXNumber = getXNumberFromXSNumericType((XSNumericType)left);
   	    XNumber rightXNumber = getXNumberFromXSNumericType((XSNumericType)right);
   	    result = arithmeticOpOnXNumberValues(leftXNumber, rightXNumber, OP_SYMBOL_DIV);
     }
     else if ((left instanceof XNumber) && (right instanceof XNumber)) {         
    	XNumber lNumber = (XNumber)left;
   	    XNumber rNumber = (XNumber)right;
   	    result = arithmeticOpOnXNumberValues(lNumber, rNumber, OP_SYMBOL_DIV);    	 
     }
     else if ((left instanceof XNumber) && (right instanceof XMLNodeCursorImpl)) {
         double lDouble = ((XNumber)left).num();
         
         XMLNodeCursorImpl rNodeSet = (XMLNodeCursorImpl)right;
         if (rNodeSet.getLength() > 1) {
        	 error(CARDINALITY_ERR_MESG, new String[] {"XPTY0004", OP_SYMBOL_DIV});  
         }
         else {
            java.lang.String rStrVal = rNodeSet.str();
            double rDouble = (Double.valueOf(rStrVal)).doubleValue();
            
            result = new XSDecimal(BigDecimal.valueOf(lDouble / rDouble));
         }
     }
     else if ((left instanceof XMLNodeCursorImpl) && (right instanceof XNumber)) {
         double rDouble = ((XNumber)right).num();
         
         XMLNodeCursorImpl lNodeSet = (XMLNodeCursorImpl)left;
         if (lNodeSet.getLength() > 1) {
        	 error(CARDINALITY_ERR_MESG, new String[] {"XPTY0004", OP_SYMBOL_DIV});  
         }
         else {
            java.lang.String lStrVal = lNodeSet.str();
            double lDouble = (Double.valueOf(lStrVal)).doubleValue();
            
            result = new XSDecimal(BigDecimal.valueOf(lDouble / rDouble));
         }
     }
     else if ((left instanceof XSNumericType) && (right instanceof XMLNodeCursorImpl)) {
         java.lang.String lStrVal = ((XSNumericType)left).stringValue();
         double lDouble = (Double.valueOf(lStrVal)).doubleValue();
         
         XMLNodeCursorImpl rNodeSet = (XMLNodeCursorImpl)right;
         if (rNodeSet.getLength() > 1) {
        	 error(CARDINALITY_ERR_MESG, new String[] {"XPTY0004", OP_SYMBOL_DIV});  
         }
         else {
            java.lang.String rStrVal = rNodeSet.str();
            double rDouble = (Double.valueOf(rStrVal)).doubleValue();
            
            result = new XSDecimal(BigDecimal.valueOf(lDouble / rDouble));
         }
     }
     else if ((left instanceof XMLNodeCursorImpl) && (right instanceof XSNumericType)) {
         java.lang.String rStrVal = ((XSNumericType)right).stringValue();
         double rDouble = (Double.valueOf(rStrVal)).doubleValue();
         
         XMLNodeCursorImpl lNodeSet = (XMLNodeCursorImpl)left;
         if (lNodeSet.getLength() > 1) {
        	 error(CARDINALITY_ERR_MESG, new String[] {"XPTY0004", OP_SYMBOL_DIV});  
         }
         else {
            java.lang.String lStrVal = lNodeSet.str();
            double lDouble = (Double.valueOf(lStrVal)).doubleValue();
            
            result = new XSDecimal(BigDecimal.valueOf(lDouble / rDouble));
         }
     }
     else if ((left instanceof XMLNodeCursorImpl) && (right instanceof XMLNodeCursorImpl)) {
         double lDouble = 0.0d;
         double rDouble = 0.0d;
         
         XMLNodeCursorImpl lNodeSet = (XMLNodeCursorImpl)left;
         if (lNodeSet.getLength() > 1) {
        	 error(CARDINALITY_ERR_MESG, new String[] {"XPTY0004", OP_SYMBOL_DIV});  
         }
         else {
            java.lang.String lStrVal = lNodeSet.str();
            lDouble = (Double.valueOf(lStrVal)).doubleValue();
         }
         
         XMLNodeCursorImpl rNodeSet = (XMLNodeCursorImpl)right;
         if (rNodeSet.getLength() > 1) {
        	 error(CARDINALITY_ERR_MESG, new String[] {"XPTY0004", OP_SYMBOL_DIV});  
         }
         else {
            java.lang.String rStrVal = rNodeSet.str();
            rDouble = (Double.valueOf(rStrVal)).doubleValue();
         }
         
         result = new XSDecimal(BigDecimal.valueOf(lDouble / rDouble));
     }     
     else if ((left instanceof ResultSequence) && (right instanceof XNumber)) {
         ResultSequence rsLeft = (ResultSequence)left;          
         if (rsLeft.size() > 1) {
        	 error(CARDINALITY_ERR_MESG, new String[] {"XPTY0004", OP_SYMBOL_DIV});  
         }
         else {
            java.lang.String lStr = XslTransformEvaluationHelper.getStrVal(rsLeft.item(0));
            double lDouble = (Double.valueOf(lStr)).doubleValue();
            
            double rDouble = ((XNumber)right).num();
            
            result = new XSDecimal(BigDecimal.valueOf(lDouble / rDouble));
         }
     }
     else if ((left instanceof XNumber) && (right instanceof ResultSequence)) {
         ResultSequence rsRight = (ResultSequence)right;          
         if (rsRight.size() > 1) {
        	 error(CARDINALITY_ERR_MESG, new String[] {"XPTY0004", OP_SYMBOL_DIV});  
         }
         else {             
            double lDouble = ((XNumber)left).num();
            
            java.lang.String rStr = XslTransformEvaluationHelper.getStrVal(rsRight.item(0));
            double rDouble = (Double.valueOf(rStr)).doubleValue();
            
            result = new XSDecimal(BigDecimal.valueOf(lDouble / rDouble));
         }
     }
     else if ((left instanceof ResultSequence) && (right instanceof XSNumericType)) {
         ResultSequence rsLeft = (ResultSequence)left;          
         if (rsLeft.size() > 1) {
        	 error(CARDINALITY_ERR_MESG, new String[] {"XPTY0004", OP_SYMBOL_DIV});  
         }
         else {
            java.lang.String lStr = XslTransformEvaluationHelper.getStrVal(rsLeft.item(0));
            double lDouble = (Double.valueOf(lStr)).doubleValue();
            
            java.lang.String rStrVal = ((XSNumericType)right).stringValue();
            double rDouble = (Double.valueOf(rStrVal)).doubleValue();
            
            result = new XSDecimal(BigDecimal.valueOf(lDouble / rDouble));
         } 
     }
     else if ((left instanceof XSNumericType) && (right instanceof ResultSequence)) {
         ResultSequence rsRight = (ResultSequence)right;          
         if (rsRight.size() > 1) {
        	 error(CARDINALITY_ERR_MESG, new String[] {"XPTY0004", OP_SYMBOL_DIV});  
         }
         else {                          
            java.lang.String lStrVal = ((XSNumericType)left).stringValue();
            double lDouble = (Double.valueOf(lStrVal)).doubleValue();
            
            java.lang.String rStr = XslTransformEvaluationHelper.getStrVal(rsRight.item(0));
            double rDouble = (Double.valueOf(rStr)).doubleValue();
            
            result = new XSDecimal(BigDecimal.valueOf(lDouble / rDouble));
         }
     }
     else if ((left instanceof ResultSequence) && (right instanceof ResultSequence)) {
         ResultSequence rsLeft = (ResultSequence)left;          
         if (rsLeft.size() > 1) {
        	 error(CARDINALITY_ERR_MESG, new String[] {"XPTY0004", OP_SYMBOL_DIV});  
         }
         
         ResultSequence rsRight = (ResultSequence)right;          
         if (rsRight.size() > 1) {
        	 error(CARDINALITY_ERR_MESG, new String[] {"XPTY0004", OP_SYMBOL_DIV});  
         }
         
         java.lang.String lStr = XslTransformEvaluationHelper.getStrVal(rsLeft.item(0));
         double lDouble = (Double.valueOf(lStr)).doubleValue();
         
         java.lang.String rStr = XslTransformEvaluationHelper.getStrVal(rsRight.item(0));
         double rDouble = (Double.valueOf(rStr)).doubleValue();
         
         result = new XSDecimal(BigDecimal.valueOf(lDouble / rDouble));
     }
     else if (left instanceof ResultSequence) {
    	 ResultSequence rSeq = (ResultSequence)left;
         if (rSeq.size() > 1) {
        	 error(CARDINALITY_ERR_MESG, new String[] {"XPTY0004", OP_SYMBOL_DIV});  
         }                  
    	 
         BigDecimal lBigDecimal = null;
         BigDecimal rBigDecimal = null;
         
    	 try {
    		 java.lang.String lStrVal = XslTransformEvaluationHelper.getStrVal(rSeq.item(0));
             java.lang.String rStrVal = XslTransformEvaluationHelper.getStrVal(right);
    		 lBigDecimal = new BigDecimal(lStrVal); 
        	 rBigDecimal = new BigDecimal(rStrVal);
    		 result = new XSDecimal(lBigDecimal.divide(rBigDecimal));
    	 }
    	 catch (NumberFormatException ex) {
    		 error(OPERAND_NOT_NUMERIC_ERR_MESG, new String[] {"XPTY0004", OP_SYMBOL_DIV});
    	 }
    	 catch (ArithmeticException ex) {
    		 java.lang.String exceptionMesg = ex.getMessage();
    		 result = divOpArithmeticExceptionAction(lBigDecimal, rBigDecimal, exceptionMesg);
    	 }
     }
     else if (left instanceof XMLNodeCursorImpl) {
    	 XMLNodeCursorImpl lNodeSet = (XMLNodeCursorImpl)left;
         if (lNodeSet.getLength() > 1) {
        	 error(CARDINALITY_ERR_MESG, new String[] {"XPTY0004", OP_SYMBOL_DIV}); 
         }                  
    	 
         BigDecimal lBigDecimal = null;
         BigDecimal rBigDecimal = null;
         
    	 try {
    		 java.lang.String lStrVal = lNodeSet.str();
             java.lang.String rStrVal = XslTransformEvaluationHelper.getStrVal(right);
    		 lBigDecimal = new BigDecimal(lStrVal); 
        	 rBigDecimal = new BigDecimal(rStrVal);
    		 result = new XSDecimal(lBigDecimal.divide(rBigDecimal));
    	 }
    	 catch (NumberFormatException ex) {
    		 error(OPERAND_NOT_NUMERIC_ERR_MESG, new String[] {"XPTY0004", OP_SYMBOL_DIV});
    	 }
    	 catch (ArithmeticException ex) {
    		 java.lang.String exceptionMesg = ex.getMessage();
    		 result = divOpArithmeticExceptionAction(lBigDecimal, rBigDecimal, exceptionMesg);
    	 }         
     }
     else if (left instanceof XSYearMonthDuration) {
         try {
            java.lang.String rStrVal = XslTransformEvaluationHelper.getStrVal(right);
            result = ((XSYearMonthDuration)left).div(new XSDouble(rStrVal));
         }
         catch (XPathException ex) {
            throw new javax.xml.transform.TransformerException(ex.getMessage());  
         }
     }     
     else {
    	 try {
            java.lang.String lStrVal = XslTransformEvaluationHelper.getStrVal(left);
        	java.lang.String rStrVal = XslTransformEvaluationHelper.getStrVal(right);            
            result = new XSDecimal(BigDecimal.valueOf(Double.valueOf(lStrVal) / Double.valueOf(rStrVal)));
         }
         catch (NumberFormatException ex) {
        	error(OPERAND_NOT_NUMERIC_ERR_MESG, new String[] {"XPTY0004", OP_SYMBOL_DIV}); 
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

    return (m_left.num(xctxt) / m_right.num(xctxt));
  }

}
