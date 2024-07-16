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
import java.math.BigInteger;

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
import xml.xpath31.processor.types.XSLong;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSTime;
import xml.xpath31.processor.types.XSUntyped;
import xml.xpath31.processor.types.XSUntypedAtomic;
import xml.xpath31.processor.types.XSYearMonthDuration;

/**
 * The '+' operation expression executer.
 */
public class Plus extends Operation
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
  public XObject operate(XObject left, XObject right)
                                           throws javax.xml.transform.TransformerException {
      XObject result = null;
   
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
    	  result = addXNumberToXsNumericType((XNumber)left, (XSNumericType)right);
      }
      else if ((left instanceof XSNumericType) && (right instanceof XNumber)) {
    	  result = addXNumberToXsNumericType((XNumber)right, (XSNumericType)left);    	
      }      
      else if ((left instanceof XSNumericType) && (right instanceof XSNumericType)) {          
          result = addXSNumericTypeToXsNumericType((XSNumericType)left, (XSNumericType)right);
      }
      else if ((left instanceof XNumber) && (right instanceof XNumber)) {
          double lDouble = ((XNumber)left).num();
          double rDouble = ((XNumber)right).num();
          
          result = new XNumber(lDouble + rDouble);                    
      }
      else if ((left instanceof XNumber) && (right instanceof XNodeSet)) {
          double lDouble = ((XNumber)left).num();
          
          XNodeSet rNodeSet = (XNodeSet)right;
          if (rNodeSet.getLength() > 1) {
             throw new javax.xml.transform.TransformerException("XPTY0004 : A sequence of more "
                                                                                   + "than one item is not allowed as the 2nd "
                                                                                   + "operand of operator '+'.");  
          }
          else {
             java.lang.String rStrVal = rNodeSet.str();
             double rDouble = (Double.valueOf(rStrVal)).doubleValue();
             
             result = new XNumber(lDouble + rDouble);
          }
      }
      else if ((left instanceof XNodeSet) && (right instanceof XNumber)) {
          double rDouble = ((XNumber)right).num();
          
          XNodeSet lNodeSet = (XNodeSet)left;
          if (lNodeSet.getLength() > 1) {
             throw new javax.xml.transform.TransformerException("XPTY0004 : A sequence of more "
                                                                                   + "than one item is not allowed as the 1st "
                                                                                   + "operand of operator '+'.");  
          }
          else {
             java.lang.String lStrVal = lNodeSet.str();
             double lDouble = (Double.valueOf(lStrVal)).doubleValue();
             
             result = new XNumber(lDouble + rDouble);
          }
      }
      else if ((left instanceof XSNumericType) && (right instanceof XNodeSet)) {
    	  XNodeSet rNodeSet = (XNodeSet)right;
          if (rNodeSet.getLength() > 1) {
             throw new javax.xml.transform.TransformerException("XPTY0004 : A sequence of more "
                                                                                   + "than one item is not allowed as the 2nd "
                                                                                   + "operand of operator '+'.");  
          }
          else {
        	  BigDecimal lBigDecimal = new BigDecimal(((XSNumericType)left).stringValue());
        	  BigDecimal rBigDecimal = null;
        	  try {
        	     rBigDecimal = new BigDecimal(rNodeSet.str());
        	  }
        	  catch (NumberFormatException ex) {
        		 throw new javax.xml.transform.TransformerException("XPTY0004 : The 2nd operand of operator '+' is not a numeric value or "
        		 		                                                          + "cannot be converted to a numeric value."); 
        	  }
        	  
        	  BigDecimal resultBigDecimal = lBigDecimal.add(rBigDecimal);
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
                                                                                   + "operand of operator '+'.");  
          }
          else {        	  
        	  BigDecimal lBigDecimal = null;
        	  try {
        		 lBigDecimal = new BigDecimal(lNodeSet.str());
        	  }
        	  catch (NumberFormatException ex) {
        		 throw new javax.xml.transform.TransformerException("XPTY0004 : The 1st operand of operator '+' is not a numeric value or "
        		 		                                                          + "cannot be converted to a numeric value."); 
        	  }
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
      else if ((left instanceof XNodeSet) && (right instanceof XNodeSet)) {
    	  XNodeSet lNodeSet = (XNodeSet)left;
          if (lNodeSet.getLength() > 1) {
             throw new javax.xml.transform.TransformerException("XPTY0004 : A sequence of more "
                                                                                   + "than one item is not allowed as the 1st "
                                                                                   + "operand of operator '+'.");  
          }
          
          XNodeSet rNodeSet = (XNodeSet)right;
          if (rNodeSet.getLength() > 1) {
             throw new javax.xml.transform.TransformerException("XPTY0004 : A sequence of more "
                                                                                   + "than one item is not allowed as the 2nd "
                                                                                   + "operand of operator '+'.");  
          }
          
          BigDecimal lBigDecimal = null;
    	  try {
    		 lBigDecimal = new BigDecimal(lNodeSet.str());
    	  }
    	  catch (NumberFormatException ex) {
    		 throw new javax.xml.transform.TransformerException("XPTY0004 : The 1st operand of operator '+' is not a numeric value or "
    		 		                                                          + "cannot be converted to a numeric value."); 
    	  }
    	  
    	  BigDecimal rBigDecimal = null;
    	  try {
    	     rBigDecimal = new BigDecimal(rNodeSet.str());
    	  }
    	  catch (NumberFormatException ex) {
    		 throw new javax.xml.transform.TransformerException("XPTY0004 : The 2nd operand of operator '+' is not a numeric value or "
    		 		                                                          + "cannot be converted to a numeric value."); 
    	  }
    	  
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
             throw new javax.xml.transform.TransformerException("XPTY0004 : A sequence of more "
                                                                                   + "than one item is not allowed as the 1st "
                                                                                   + "operand of operator '+'.");  
          }
          else {        	  
        	  BigDecimal lBigDecimal = null;
        	  try {
        		 lBigDecimal = new BigDecimal(XslTransformEvaluationHelper.getStrVal(lSeq.item(0)));
        	  }
        	  catch (NumberFormatException ex) {
        		 throw new javax.xml.transform.TransformerException("XPTY0004 : The 1st operand of operator '+' is not a numeric value or "
        		 		                                                          + "cannot be converted to a numeric value."); 
        	  }
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
             throw new javax.xml.transform.TransformerException("XPTY0004 : A sequence of more "
                                                                                   + "than one item is not allowed as the 2nd "
                                                                                   + "operand of operator '+'.");  
          }
          else {
        	  BigDecimal lBigDecimal = BigDecimal.valueOf(((XNumber)left).num());
        	  BigDecimal rBigDecimal = null;
        	  try {
        	     rBigDecimal = new BigDecimal(XslTransformEvaluationHelper.getStrVal(rSeq.item(0)));
        	  }
        	  catch (NumberFormatException ex) {
        		 throw new javax.xml.transform.TransformerException("XPTY0004 : The 2nd operand of operator '+' is not a numeric value or "
        		 		                                                          + "cannot be converted to a numeric value."); 
        	  }
        	  
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
             throw new javax.xml.transform.TransformerException("XPTY0004 : A sequence of more "
                                                                                   + "than one item is not allowed as the 1st "
                                                                                   + "operand of operator '+'.");  
          }
          else {        	  
        	  BigDecimal lBigDecimal = null;
        	  try {
        		 lBigDecimal = new BigDecimal(XslTransformEvaluationHelper.getStrVal(lSeq.item(0)));
        	  }
        	  catch (NumberFormatException ex) {
        		 throw new javax.xml.transform.TransformerException("XPTY0004 : The 1st operand of operator '+' is not a numeric value or "
        		 		                                                          + "cannot be converted to a numeric value."); 
        	  }
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
             throw new javax.xml.transform.TransformerException("XPTY0004 : A sequence of more "
                                                                                   + "than one item is not allowed as the 2nd "
                                                                                   + "operand of operator '+'.");  
          }
          else {
        	  BigDecimal lBigDecimal = new BigDecimal(((XSNumericType)left).stringValue());
        	  BigDecimal rBigDecimal = null;
        	  try {
        	     rBigDecimal = new BigDecimal(XslTransformEvaluationHelper.getStrVal(rSeq.item(0)));
        	  }
        	  catch (NumberFormatException ex) {
        		 throw new javax.xml.transform.TransformerException("XPTY0004 : The 2nd operand of operator '+' is not a numeric value or "
        		 		                                                          + "cannot be converted to a numeric value."); 
        	  }
        	  
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
              throw new javax.xml.transform.TransformerException("XPTY0004 : A sequence of more "
                                                                                   + "than one item is not allowed as the 1st "
                                                                                   + "operand of operator '+'.");  
          }
          
          ResultSequence rSeq = (ResultSequence)right;
          if (rSeq.size() > 1) {
              throw new javax.xml.transform.TransformerException("XPTY0004 : A sequence of more "
                                                                                   + "than one item is not allowed as the 2nd "
                                                                                   + "operand of operator '+'.");  
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
        	  BigDecimal lBigDecimal = null;
        	  BigDecimal rBigDecimal = null;        	  
        	  try {
	              java.lang.String lStr = XslTransformEvaluationHelper.getStrVal(lXObj);
	              lBigDecimal = new BigDecimal(lStr);	              
	              java.lang.String rStr = XslTransformEvaluationHelper.getStrVal(rXObj);
	              rBigDecimal = new BigDecimal(rStr);
        	  }
        	  catch (NumberFormatException ex) {
        		  throw new javax.xml.transform.TransformerException("XPTY0004 : 1st or the 2nd operand of operator '+', "
        		  		                                                           + "is not numeric or cannot be converted to numeric.");
        	  }
        	  
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
              throw new javax.xml.transform.TransformerException("XPTY0004 : A sequence of more "
                                                                                   + "than one item is not allowed as the 1st "
                                                                                   + "operand of operator '+'.");  
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
        	  BigDecimal lBigDecimal = null;
        	  BigDecimal rBigDecimal = null;        	  
        	  try {
	              java.lang.String lStr = XslTransformEvaluationHelper.getStrVal(lXObj);
	              lBigDecimal = new BigDecimal(lStr);	              
	              java.lang.String rStr = XslTransformEvaluationHelper.getStrVal(right);
	              rBigDecimal = new BigDecimal(rStr);
        	  }
        	  catch (NumberFormatException ex) {
        		  throw new javax.xml.transform.TransformerException("XPTY0004 : 1st or the 2nd operand of operator '+', "
        		  		                                                           + "is not numeric or cannot be converted to numeric.");
        	  }
        	  
        	  BigDecimal resultBigDecimal = lBigDecimal.add(rBigDecimal);
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
                                                                                   + "operand of operator '+'.");  
          }
          
          BigDecimal lBigDecimal = null;
          BigDecimal rBigDecimal = null;
    	  try {
    		 lBigDecimal = new BigDecimal(lNodeSet.str());
             rBigDecimal = new BigDecimal(XslTransformEvaluationHelper.getStrVal(right));
    	  }
    	  catch (NumberFormatException ex) {
    		 throw new javax.xml.transform.TransformerException("XPTY0004 : 1st or the 2nd operand of operator '+', "
                                                                               + "is not numeric or cannot be converted to numeric."); 
    	  }
        	  
          BigDecimal resultBigDecimal = lBigDecimal.add(rBigDecimal);
          if (resultBigDecimal.compareTo(new BigDecimal(resultBigDecimal.toBigInteger())) == 0) {
          	 result = new XSInteger(resultBigDecimal.toBigInteger()); 
          }
          else {
             result = new XSDecimal(resultBigDecimal);
          }
      }
      else {
          try {
             result = new XNumber(left.num() + right.num());
          }
          catch (TransformerException ex) {
             throw new javax.xml.transform.TransformerException("XPTY0004 : Could not evaluate the "
                                                                                              + "operator '+', due to incorrectly "
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

    return (m_right.num(xctxt) + m_left.num(xctxt));
  }
  
  /**
   * Add a value of type XSNumericType, to another value of type 
   * XSNumericType. 
   * 
   * Whereever possible, we try to return a value of type derived 
   * from XSNumericType (i.e, an XML Schema type), and don't incur
   * loss of precision. 
   */
  private XObject addXSNumericTypeToXsNumericType(XSNumericType leftVal, XSNumericType rightVal) {

	  XObject result = null;

	  if (leftVal instanceof XSInteger) {
		  if (rightVal instanceof XSInteger) {
			  XSInteger lXsInteger = (XSInteger)leftVal;
			  BigDecimal lBigDecimal = lXsInteger.getValue();

			  XSInteger rXsInteger = (XSInteger)rightVal;
			  BigDecimal rBigDecimal = rXsInteger.getValue();

			  BigDecimal resultBigDecimal = lBigDecimal.add(rBigDecimal);
			  result = new XSInteger(resultBigDecimal.toString());
		  }
		  else if (rightVal instanceof XSLong) {
			  XSInteger lXsInteger = (XSInteger)leftVal;
			  BigDecimal lBigDecimal = lXsInteger.getValue();

			  XSLong rXsLong = (XSLong)rightVal;
			  BigDecimal rBigDecimal = rXsLong.getValue();

			  BigDecimal resultBigDecimal = lBigDecimal.add(rBigDecimal);
			  result = new XSInteger(resultBigDecimal.toString()); 
		  }
		  else if (rightVal instanceof XSDecimal) {
			  XSDecimal rXsDecimal = (XSDecimal)rightVal;
			  BigDecimal rBigDecimal = rXsDecimal.getValue();
			  if (rBigDecimal.equals(rBigDecimal.toBigInteger())) {
				  XSInteger lXsInteger = (XSInteger)leftVal;
				  BigDecimal lBigDecimal = lXsInteger.getValue();
				  result = new XSInteger((lBigDecimal.add(rBigDecimal)).toString());
			  }
			  else {
				  XSInteger lXsInteger = (XSInteger)leftVal;
				  BigDecimal lBigDecimal = lXsInteger.getValue();
				  result = new XSDecimal((lBigDecimal.add(rBigDecimal)).toString());
			  }
		  }
		  else {
			  XSInteger lXsInteger = (XSInteger)leftVal;
			  BigDecimal lBigDecimal = lXsInteger.getValue();

			  java.lang.String rStrVal = rightVal.stringValue();
			  BigDecimal rBigDecimal = new BigDecimal(rStrVal);

			  result = new XSDecimal((lBigDecimal.add(rBigDecimal)).toString());
		  }
	  }
	  else if (leftVal instanceof XSLong) {
		  java.lang.String lStrVal = leftVal.stringValue();
		  java.lang.String rStrVal = rightVal.stringValue();
		  if (rightVal instanceof XSInteger) {
			  BigDecimal lBigDecimal = new BigDecimal(lStrVal);
			  BigDecimal rBigDecimal = new BigDecimal(rStrVal);
			  result = new XSInteger((lBigDecimal.add(rBigDecimal)).toString());
		  }
		  else if (rightVal instanceof XSLong) {
			  BigDecimal lBigDecimal = new BigDecimal(lStrVal);
			  BigDecimal rBigDecimal = new BigDecimal(rStrVal);
			  result = new XSInteger((lBigDecimal.add(rBigDecimal)).toString()); 
		  }
		  else if (rightVal instanceof XSDecimal) {
			  BigDecimal lBigDecimal = new BigDecimal(lStrVal);
			  BigDecimal rBigDecimal = new BigDecimal(rStrVal);
			  result = new XSDecimal((lBigDecimal.add(rBigDecimal)).toString());  
		  }
		  else {
			  XSLong lXsLong = (XSLong)leftVal;
			  BigDecimal lBigDecimal = lXsLong.getValue();

			  BigDecimal rBigDecimal = new BigDecimal(rStrVal);       		    
			  result = new XSDecimal((lBigDecimal.add(rBigDecimal)).toString()); 
		  } 
	  }
	  else {
		  java.lang.String lStrVal = leftVal.stringValue();
		  java.lang.String rStrVal = rightVal.stringValue();

		  BigDecimal lBigDecimal = new BigDecimal(lStrVal);
		  BigDecimal rBigDecimal = new BigDecimal(rStrVal);

		  result = new XSDecimal((lBigDecimal.add(rBigDecimal)).toString());  
	  }
	  
	  return result;
  }

  /**
   * Add XNumber value to an XSNumericType value.
   * 
   * Whereever possible, we try to return a value of type derived 
   * from XSNumericType (i.e, an XML Schema type), and don't incur
   * loss of precision. 
   */
  private XObject addXNumberToXsNumericType(XNumber leftVal, XSNumericType rightVal) {
	  
	  XObject result = null;
	  
	  if (rightVal instanceof XSInteger) {
		  java.lang.String rStrVal = rightVal.stringValue();
		  BigInteger rBigInt = new BigInteger(rStrVal);
		  double lDouble = leftVal.num();
		  if (lDouble == (long)lDouble) {
			  BigInteger resultBigInt = rBigInt.add(new BigInteger((long)lDouble + ""));
			  result = new XSInteger(resultBigInt);
		  }
		  else {
			  java.math.BigDecimal lBigDecimal = new java.math.BigDecimal(rStrVal); 
			  java.math.BigDecimal resultBigDecimal = lBigDecimal.add(new java.math.BigDecimal(lDouble));
			  result = new XSDecimal(resultBigDecimal);
		  }
	  }
	  else if (rightVal instanceof XSLong) {
		  java.lang.String rStrVal = rightVal.stringValue();
		  BigInteger rBigInt = new BigInteger(rStrVal);
		  double lDouble = leftVal.num();
		  if (lDouble == (long)lDouble) {
			  BigInteger resultBigInt = rBigInt.add(new BigInteger((long)lDouble + ""));
			  result = new XSLong(resultBigInt);
		  }
		  else {
			  java.math.BigDecimal rBigDecimal = new java.math.BigDecimal(rStrVal); 
			  java.math.BigDecimal resultBigDecimal = rBigDecimal.add(new java.math.BigDecimal(lDouble));
			  result = new XSDecimal(resultBigDecimal);
		  }
	  }    	  
	  else {
		  java.lang.String rStrVal = rightVal.stringValue();
		  double rDouble = (Double.valueOf(rStrVal)).doubleValue();
		  double lDouble = leftVal.num();
		  result = new XNumber(lDouble + rDouble);
	  }
	  
	  return result;
  }

}
