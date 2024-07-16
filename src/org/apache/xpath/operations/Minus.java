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

import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
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
 * The binary '-' operation expression executer.
 */
public class Minus extends Operation
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
  public XObject operate(XObject left, XObject right)
                                           throws javax.xml.transform.TransformerException {
      XObject result = null;
      
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
      else if ((left instanceof XNumber) && (right instanceof XNumber)) {
          double lDouble = ((XNumber)left).num();
          double rDouble = ((XNumber)right).num();
          
          result = new XNumber(lDouble - rDouble);
      }
      else if ((left instanceof XNumber) && (right instanceof XSNumericType)) {
          BigDecimal lBigDecimal = BigDecimal.valueOf(((XNumber)left).num());          
          BigDecimal rBigDecimal = new BigDecimal(((XSNumericType)right).stringValue());
          
          BigDecimal resultBigDecimal = lBigDecimal.subtract(rBigDecimal);
          if (resultBigDecimal.compareTo(new BigDecimal(resultBigDecimal.toBigInteger())) == 0) {
     		 result = new XSInteger(resultBigDecimal.toBigInteger()); 
     	  }
     	  else {
              result = new XSDecimal(resultBigDecimal);
     	  }
      }
      else if ((left instanceof XSNumericType) && (right instanceof XNumber)) {
    	  BigDecimal lBigDecimal = new BigDecimal(((XSNumericType)left).stringValue());
    	  BigDecimal rBigDecimal = BigDecimal.valueOf(((XNumber)right).num());
    	  
    	  BigDecimal resultBigDecimal = lBigDecimal.subtract(rBigDecimal);    	  
    	  if (resultBigDecimal.compareTo(new BigDecimal(resultBigDecimal.toBigInteger())) == 0) {
    		 result = new XSInteger(resultBigDecimal.toBigInteger()); 
    	  }
    	  else {
             result = new XSDecimal(resultBigDecimal);
    	  }
      }      
      else if ((left instanceof XSNumericType) && (right instanceof XSNumericType)) {
    	  BigDecimal lBigDecimal = new BigDecimal(((XSNumericType)left).stringValue());
    	  BigDecimal rBigDecimal = new BigDecimal(((XSNumericType)right).stringValue());
    	  
    	  BigDecimal resultBigDecimal = lBigDecimal.subtract(rBigDecimal);    	  
    	  if (resultBigDecimal.compareTo(new BigDecimal(resultBigDecimal.toBigInteger())) == 0) {
     		 result = new XSInteger(resultBigDecimal.toBigInteger()); 
     	  }
     	  else {
             result = new XSDecimal(resultBigDecimal);
     	  }
      }
      else if ((left instanceof XNumber) && (right instanceof XNodeSet)) {          
          XNodeSet rNodeSet = (XNodeSet)right;
          if (rNodeSet.getLength() > 1) {
             throw new javax.xml.transform.TransformerException("XPTY0004 : A sequence of more "
                                                                                   + "than one item is not allowed as the 2nd "
                                                                                   + "operand of operator '-'.");  
          }
          else {
        	  BigDecimal lBigDecimal = BigDecimal.valueOf(((XNumber)left).num());
        	  BigDecimal rBigDecimal = null;
        	  try {
        	     rBigDecimal = new BigDecimal(rNodeSet.str());
        	  }
        	  catch (NumberFormatException ex) {
        		 throw new javax.xml.transform.TransformerException("XPTY0004 : The 2nd operand of operator '-' is not a numeric value or "
        		 		                                                          + "cannot be converted to a numeric value."); 
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
      else if ((left instanceof XNodeSet) && (right instanceof XNumber)) {
    	  XNodeSet lNodeSet = (XNodeSet)left;
          if (lNodeSet.getLength() > 1) {
             throw new javax.xml.transform.TransformerException("XPTY0004 : A sequence of more "
                                                                                   + "than one item is not allowed as the 1st "
                                                                                   + "operand of operator '-'.");  
          }
          else {        	  
        	  BigDecimal lBigDecimal = null;
        	  try {
        		 lBigDecimal = new BigDecimal(lNodeSet.str());
        	  }
        	  catch (NumberFormatException ex) {
        		 throw new javax.xml.transform.TransformerException("XPTY0004 : The 1st operand of operator '-' is not a numeric value or "
        		 		                                                          + "cannot be converted to a numeric value."); 
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
      else if ((left instanceof XSNumericType) && (right instanceof XNodeSet)) {
    	  XNodeSet rNodeSet = (XNodeSet)right;
          if (rNodeSet.getLength() > 1) {
             throw new javax.xml.transform.TransformerException("XPTY0004 : A sequence of more "
                                                                                   + "than one item is not allowed as the 2nd "
                                                                                   + "operand of operator '-'.");  
          }
          else {
        	  BigDecimal lBigDecimal = new BigDecimal(((XSNumericType)left).stringValue());
        	  BigDecimal rBigDecimal = null;
        	  try {
        	     rBigDecimal = new BigDecimal(rNodeSet.str());
        	  }
        	  catch (NumberFormatException ex) {
        		 throw new javax.xml.transform.TransformerException("XPTY0004 : The 2nd operand of operator '-' is not a numeric value or "
        		 		                                                          + "cannot be converted to a numeric value."); 
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
      else if ((left instanceof XNodeSet) && (right instanceof XSNumericType)) {
    	  XNodeSet lNodeSet = (XNodeSet)left;
          if (lNodeSet.getLength() > 1) {
             throw new javax.xml.transform.TransformerException("XPTY0004 : A sequence of more "
                                                                                   + "than one item is not allowed as the 1st "
                                                                                   + "operand of operator '-'.");  
          }
          else {        	  
        	  BigDecimal lBigDecimal = null;
        	  try {
        		 lBigDecimal = new BigDecimal(lNodeSet.str());
        	  }
        	  catch (NumberFormatException ex) {
        		 throw new javax.xml.transform.TransformerException("XPTY0004 : The 1st operand of operator '-' is not a numeric value or "
        		 		                                                          + "cannot be converted to a numeric value."); 
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
      else if ((left instanceof XNodeSet) && (right instanceof XNodeSet)) {          
          XNodeSet lNodeSet = (XNodeSet)left;
          if (lNodeSet.getLength() > 1) {
             throw new javax.xml.transform.TransformerException("XPTY0004 : A sequence of more "
                                                                                   + "than one item is not allowed as the 1st "
                                                                                   + "operand of operator '-'.");  
          }
          
          XNodeSet rNodeSet = (XNodeSet)right;
          if (rNodeSet.getLength() > 1) {
             throw new javax.xml.transform.TransformerException("XPTY0004 : A sequence of more "
                                                                                   + "than one item is not allowed as the 2nd "
                                                                                   + "operand of operator '-'.");  
          }
          
          BigDecimal lBigDecimal = null;
    	  try {
    		 lBigDecimal = new BigDecimal(lNodeSet.str());
    	  }
    	  catch (NumberFormatException ex) {
    		 throw new javax.xml.transform.TransformerException("XPTY0004 : The 1st operand of operator '-' is not a numeric value or "
    		 		                                                          + "cannot be converted to a numeric value."); 
    	  }
    	  
    	  BigDecimal rBigDecimal = null;
    	  try {
    	     rBigDecimal = new BigDecimal(rNodeSet.str());
    	  }
    	  catch (NumberFormatException ex) {
    		 throw new javax.xml.transform.TransformerException("XPTY0004 : The 2nd operand of operator '-' is not a numeric value or "
    		 		                                                          + "cannot be converted to a numeric value."); 
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
             throw new javax.xml.transform.TransformerException("XPTY0004 : A sequence of more "
                                                                                   + "than one item is not allowed as the 1st "
                                                                                   + "operand of operator '-'.");  
          }
          else {        	  
        	  BigDecimal lBigDecimal = null;
        	  try {
        		 lBigDecimal = new BigDecimal(XslTransformEvaluationHelper.getStrVal(lSeq.item(0)));
        	  }
        	  catch (NumberFormatException ex) {
        		 throw new javax.xml.transform.TransformerException("XPTY0004 : The 1st operand of operator '-' is not a numeric value or "
        		 		                                                          + "cannot be converted to a numeric value."); 
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
             throw new javax.xml.transform.TransformerException("XPTY0004 : A sequence of more "
                                                                                   + "than one item is not allowed as the 2nd "
                                                                                   + "operand of operator '-'.");  
          }
          else {
        	  BigDecimal lBigDecimal = BigDecimal.valueOf(((XNumber)left).num());
        	  BigDecimal rBigDecimal = null;
        	  try {
        	     rBigDecimal = new BigDecimal(XslTransformEvaluationHelper.getStrVal(rSeq.item(0)));
        	  }
        	  catch (NumberFormatException ex) {
        		 throw new javax.xml.transform.TransformerException("XPTY0004 : The 2nd operand of operator '-' is not a numeric value or "
        		 		                                                          + "cannot be converted to a numeric value."); 
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
             throw new javax.xml.transform.TransformerException("XPTY0004 : A sequence of more "
                                                                                   + "than one item is not allowed as the 1st "
                                                                                   + "operand of operator '-'.");  
          }
          else {        	  
        	  BigDecimal lBigDecimal = null;
        	  try {
        		 lBigDecimal = new BigDecimal(XslTransformEvaluationHelper.getStrVal(lSeq.item(0)));
        	  }
        	  catch (NumberFormatException ex) {
        		 throw new javax.xml.transform.TransformerException("XPTY0004 : The 1st operand of operator '-' is not a numeric value or "
        		 		                                                          + "cannot be converted to a numeric value."); 
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
             throw new javax.xml.transform.TransformerException("XPTY0004 : A sequence of more "
                                                                                   + "than one item is not allowed as the 2nd "
                                                                                   + "operand of operator '-'.");  
          }
          else {
        	  BigDecimal lBigDecimal = new BigDecimal(((XSNumericType)left).stringValue());
        	  BigDecimal rBigDecimal = null;
        	  try {
        	     rBigDecimal = new BigDecimal(XslTransformEvaluationHelper.getStrVal(rSeq.item(0)));
        	  }
        	  catch (NumberFormatException ex) {
        		 throw new javax.xml.transform.TransformerException("XPTY0004 : The 2nd operand of operator '-' is not a numeric value or "
        		 		                                                          + "cannot be converted to a numeric value."); 
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
              throw new javax.xml.transform.TransformerException("XPTY0004 : A sequence of more "
                                                                                   + "than one item is not allowed as the 1st "
                                                                                   + "operand of operator '-'.");  
          }
          
          ResultSequence rSeq = (ResultSequence)right;          
          if (rSeq.size() > 1) {
              throw new javax.xml.transform.TransformerException("XPTY0004 : A sequence of more "
                                                                                   + "than one item is not allowed as the 2nd "
                                                                                   + "operand of operator '-'.");  
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
        		  throw new javax.xml.transform.TransformerException("XPTY0004 : 1st or the 2nd operand of operator '-', "
        		  		                                                           + "is not numeric or cannot be converted to numeric.");
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
              throw new javax.xml.transform.TransformerException("XPTY0004 : A sequence of more "
                                                                                   + "than one item is not allowed as the 1st "
                                                                                   + "operand of operator '-'.");  
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
        		  throw new javax.xml.transform.TransformerException("XPTY0004 : 1st or the 2nd operand of operator '-', "
        		  		                                                           + "is not numeric or cannot be converted to numeric.");
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
      else if (left instanceof XNodeSet) {
    	  XNodeSet lNodeSet = (XNodeSet)left;
          if (lNodeSet.getLength() > 1) {
             throw new javax.xml.transform.TransformerException("XPTY0004 : A sequence of more "
                                                                                   + "than one item is not allowed as the 1st "
                                                                                   + "operand of operator '-'.");  
          }
          
          BigDecimal lBigDecimal = null;
          BigDecimal rBigDecimal = null;
    	  try {
    		 lBigDecimal = new BigDecimal(lNodeSet.str());
             rBigDecimal = new BigDecimal(XslTransformEvaluationHelper.getStrVal(right));
    	  }
    	  catch (NumberFormatException ex) {
    		 throw new javax.xml.transform.TransformerException("XPTY0004 : 1st or the 2nd operand of operator '-', "
                                                                               + "is not numeric or cannot be converted to numeric."); 
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
             throw new javax.xml.transform.TransformerException("XPTY0004 : Could not evaluate the "
                                                                                                + "operator '-', due to incorrectly "
                                                                                                + "typed operand(s)."); 
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
