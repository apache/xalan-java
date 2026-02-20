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

import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathArithmeticOp;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathException;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.functions.FuncArgPlaceholder;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSDayTimeDuration;
import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSUntyped;
import xml.xpath31.processor.types.XSUntypedAtomic;
import xml.xpath31.processor.types.XSYearMonthDuration;

/**
 * An XPath 'div' operation implementation.
 * 
 * @author Scott Boag <scott_boag@us.ibm.com>
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 *         (XPath 3.1 specific changes, to this class)
 */
public class Div extends XPathArithmeticOp
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
     
     try {
    	 Object lObj = left.object();
    	 Object rObj = right.object();

    	 ElemTemplateElement elemTemplateElement = (ElemTemplateElement)getExpressionOwner();	  
    	 StylesheetRoot stylesheetRoot = XslTransformEvaluationHelper.getXslStylesheetRootFromXslElementRef(
    			 elemTemplateElement);
    	 TransformerImpl transformerImpl = stylesheetRoot.getTransformerImpl();
    	 XPathContext xctxt = transformerImpl.getXPathContext(); 

    	 if ((lObj instanceof FuncArgPlaceholder) && (rObj instanceof FuncArgPlaceholder)) {
    		 java.lang.String xpathInlineFuncExprStr = "function($arg0, $arg1) { $arg0 div $arg1 }";
    		 XPath xpathObj = new XPath(xpathInlineFuncExprStr, null, null, XPath.SELECT, null);
    		 result = xpathObj.execute(xctxt, DTM.NULL, null);

    		 return result;
    	 }
    	 else if ((lObj instanceof FuncArgPlaceholder) && !(rObj instanceof FuncArgPlaceholder)) {
    		 java.lang.String rStr = XslTransformEvaluationHelper.getStrVal(right);
    		 java.lang.String xpathInlineFuncExprStr = "function($arg0) { $arg0 div " + rStr + " }";
    		 XPath xpathObj = new XPath(xpathInlineFuncExprStr, null, null, XPath.SELECT, null);
    		 result = xpathObj.execute(xctxt, DTM.NULL, null);

    		 return result;
    	 }
    	 else if (!(lObj instanceof FuncArgPlaceholder) && (rObj instanceof FuncArgPlaceholder)) {
    		 java.lang.String lStr = XslTransformEvaluationHelper.getStrVal(left);
    		 java.lang.String xpathInlineFuncExprStr = "function($arg1) { " + lStr + " div $arg1 }";
    		 XPath xpathObj = new XPath(xpathInlineFuncExprStr, null, null, XPath.SELECT, null);
    		 result = xpathObj.execute(xctxt, DTM.NULL, null);

    		 return result;
    	 }

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

    		 result = doubleDiv(lDouble, rDouble);
    	 }
    	 else if ((left instanceof XSUntypedAtomic) && (right instanceof XSUntypedAtomic)) {
    		 java.lang.String lStrVal = ((XSUntypedAtomic)left).stringValue();
    		 double lDouble = (Double.valueOf(lStrVal)).doubleValue();

    		 java.lang.String rStrVal = ((XSUntypedAtomic)right).stringValue();
    		 double rDouble = (Double.valueOf(rStrVal)).doubleValue();

    		 result = doubleDiv(lDouble, rDouble);
    	 }
    	 else if ((left instanceof XSUntyped) && (right instanceof XSUntypedAtomic)) {
    		 java.lang.String lStrVal = ((XSUntyped)left).stringValue();
    		 double lDouble = (Double.valueOf(lStrVal)).doubleValue();

    		 java.lang.String rStrVal = ((XSUntypedAtomic)right).stringValue();
    		 double rDouble = (Double.valueOf(rStrVal)).doubleValue();

    		 result = doubleDiv(lDouble, rDouble);
    	 }
    	 else if ((left instanceof XSUntypedAtomic) && (right instanceof XSUntyped)) {
    		 java.lang.String lStrVal = ((XSUntypedAtomic)left).stringValue();
    		 double lDouble = (Double.valueOf(lStrVal)).doubleValue();

    		 java.lang.String rStrVal = ((XSUntyped)right).stringValue();
    		 double rDouble = (Double.valueOf(rStrVal)).doubleValue();

    		 result = doubleDiv(lDouble, rDouble);
    	 }
    	 else if ((left instanceof XNumber) && (right instanceof XSNumericType)) {
    		 XNumber rightXNumber = getXNumberFromXSNumericType((XSNumericType)right);
    		 result = arithmeticOpOnXNumberValues((XNumber)left, rightXNumber, OP_SYMBOL_DIV, elemTemplateElement);
    	 }
    	 else if ((left instanceof XSNumericType) && (right instanceof XNumber)) {
    		 XNumber leftXNumber = getXNumberFromXSNumericType((XSNumericType)left);
    		 result = arithmeticOpOnXNumberValues(leftXNumber, (XNumber)right, OP_SYMBOL_DIV, elemTemplateElement);
    	 }     
    	 else if ((left instanceof XSNumericType) && (right instanceof XSNumericType)) {
    		 XNumber leftXNumber = getXNumberFromXSNumericType((XSNumericType)left);
    		 XNumber rightXNumber = getXNumberFromXSNumericType((XSNumericType)right);
    		 result = arithmeticOpOnXNumberValues(leftXNumber, rightXNumber, OP_SYMBOL_DIV, elemTemplateElement);
    	 }
    	 else if ((left instanceof XNumber) && (right instanceof XNumber)) {         
    		 XNumber lNumber = (XNumber)left;
    		 XNumber rNumber = (XNumber)right;
    		 result = arithmeticOpOnXNumberValues(lNumber, rNumber, OP_SYMBOL_DIV, elemTemplateElement);
    		 if (result == null) {
    			result = new XSDouble(Double.NaN); 
    		 }
    	 }
    	 else if ((left instanceof XNumber) && (right instanceof XMLNodeCursorImpl)) {
    		 double lDouble = ((XNumber)left).num();

    		 XMLNodeCursorImpl rNodeSet = (XMLNodeCursorImpl)right;
    		 if (rNodeSet.getLength() > 1) {
    			 error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_DIV}, elemTemplateElement);  
    		 }
    		 else {            
    			 double rDouble = getDoubleFromXdmNode(rNodeSet, xctxt);

    			 result = doubleDiv(lDouble, rDouble);
    		 }
    	 }
    	 else if ((left instanceof XMLNodeCursorImpl) && (right instanceof XNumber)) {
    		 double rDouble = ((XNumber)right).num();

    		 XMLNodeCursorImpl lNodeSet = (XMLNodeCursorImpl)left;
    		 if (lNodeSet.getLength() > 1) {
    			 error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_DIV}, elemTemplateElement);  
    		 }
    		 else {        	         	 
    			 double lDouble = getDoubleFromXdmNode(lNodeSet, xctxt);

    			 result = doubleDiv(lDouble, rDouble);
    		 }
    	 }
    	 else if ((left instanceof XSNumericType) && (right instanceof XMLNodeCursorImpl)) {
    		 java.lang.String lStrVal = ((XSNumericType)left).stringValue();
    		 double lDouble = (Double.valueOf(lStrVal)).doubleValue();

    		 XMLNodeCursorImpl rNodeSet = (XMLNodeCursorImpl)right;
    		 if (rNodeSet.getLength() > 1) {
    			 error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_DIV}, elemTemplateElement);  
    		 }
    		 else {        	 
    			 double rDouble = getDoubleFromXdmNode(rNodeSet, xctxt); 

    			 result = doubleDiv(lDouble, rDouble);
    		 }
    	 }
    	 else if ((left instanceof XMLNodeCursorImpl) && (right instanceof XSNumericType)) {
    		 java.lang.String rStrVal = ((XSNumericType)right).stringValue();
    		 double rDouble = (Double.valueOf(rStrVal)).doubleValue();

    		 XMLNodeCursorImpl lNodeSet = (XMLNodeCursorImpl)left;
    		 if (lNodeSet.getLength() > 1) {
    			 error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_DIV}, elemTemplateElement);  
    		 }
    		 else {
    			 double lDouble = getDoubleFromXdmNode(lNodeSet, xctxt);

    			 result = doubleDiv(lDouble, rDouble);
    		 }
    	 }
    	 else if ((left instanceof XMLNodeCursorImpl) && (right instanceof XMLNodeCursorImpl)) {
    		 double lDouble = 0.0d;
    		 double rDouble = 0.0d;

    		 XMLNodeCursorImpl lNodeSet = (XMLNodeCursorImpl)left;
    		 if (lNodeSet.getLength() > 1) {
    			 error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_DIV}, elemTemplateElement);  
    		 }
    		 else {
    			 lDouble = getDoubleFromXdmNode(lNodeSet, xctxt);
    		 }

    		 XMLNodeCursorImpl rNodeSet = (XMLNodeCursorImpl)right;
    		 if (rNodeSet.getLength() > 1) {
    			 error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_DIV}, elemTemplateElement);  
    		 }
    		 else {
    			 rDouble = getDoubleFromXdmNode(rNodeSet, xctxt); 
    		 }

    		 result = doubleDiv(lDouble, rDouble);
    	 }     
    	 else if ((left instanceof ResultSequence) && (right instanceof XNumber)) {
    		 ResultSequence rsLeft = (ResultSequence)left;          
    		 if (rsLeft.size() > 1) {
    			 error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_DIV}, elemTemplateElement);  
    		 }
    		 else {
    			 java.lang.String lStr = XslTransformEvaluationHelper.getStrVal(rsLeft.item(0));
    			 double lDouble = (Double.valueOf(lStr)).doubleValue();

    			 double rDouble = ((XNumber)right).num();

    			 result = doubleDiv(lDouble, rDouble);
    		 }
    	 }
    	 else if ((left instanceof XNumber) && (right instanceof ResultSequence)) {
    		 ResultSequence rsRight = (ResultSequence)right;          
    		 if (rsRight.size() > 1) {
    			 error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_DIV}, elemTemplateElement);  
    		 }
    		 else {             
    			 double lDouble = ((XNumber)left).num();

    			 java.lang.String rStr = XslTransformEvaluationHelper.getStrVal(rsRight.item(0));
    			 double rDouble = (Double.valueOf(rStr)).doubleValue();

    			 result = doubleDiv(lDouble, rDouble);
    		 }
    	 }
    	 else if ((left instanceof ResultSequence) && (right instanceof XSNumericType)) {
    		 ResultSequence rsLeft = (ResultSequence)left;          
    		 if (rsLeft.size() > 1) {
    			 error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_DIV}, elemTemplateElement);  
    		 }
    		 else {
    			 java.lang.String lStr = XslTransformEvaluationHelper.getStrVal(rsLeft.item(0));
    			 double lDouble = (Double.valueOf(lStr)).doubleValue();

    			 java.lang.String rStrVal = ((XSNumericType)right).stringValue();
    			 double rDouble = (Double.valueOf(rStrVal)).doubleValue();

    			 result = doubleDiv(lDouble, rDouble);
    		 } 
    	 }
    	 else if ((left instanceof XSNumericType) && (right instanceof ResultSequence)) {
    		 ResultSequence rsRight = (ResultSequence)right;          
    		 if (rsRight.size() > 1) {
    			 error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_DIV}, elemTemplateElement);  
    		 }
    		 else {                          
    			 java.lang.String lStrVal = ((XSNumericType)left).stringValue();
    			 double lDouble = (Double.valueOf(lStrVal)).doubleValue();

    			 java.lang.String rStr = XslTransformEvaluationHelper.getStrVal(rsRight.item(0));
    			 double rDouble = (Double.valueOf(rStr)).doubleValue();

    			 result = doubleDiv(lDouble, rDouble);
    		 }
    	 }
    	 else if ((left instanceof ResultSequence) && (right instanceof ResultSequence)) {
    		 ResultSequence rsLeft = (ResultSequence)left;          
    		 if (rsLeft.size() > 1) {
    			 error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_DIV}, elemTemplateElement);  
    		 }

    		 ResultSequence rsRight = (ResultSequence)right;          
    		 if (rsRight.size() > 1) {
    			 error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_DIV}, elemTemplateElement);  
    		 }

    		 java.lang.String lStr = XslTransformEvaluationHelper.getStrVal(rsLeft.item(0));
    		 double lDouble = (Double.valueOf(lStr)).doubleValue();

    		 java.lang.String rStr = XslTransformEvaluationHelper.getStrVal(rsRight.item(0));
    		 double rDouble = (Double.valueOf(rStr)).doubleValue();

    		 result = doubleDiv(lDouble, rDouble);
    	 }
    	 else if (left instanceof ResultSequence) {
    		 ResultSequence rSeq = (ResultSequence)left;
    		 if (rSeq.size() > 1) {
    			 error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_DIV}, elemTemplateElement);  
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
    			 error(OPERAND_NOT_NUMERIC_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_DIV}, elemTemplateElement);
    		 }
    		 catch (ArithmeticException ex) {
    			 java.lang.String exceptionMesg = ex.getMessage();
    			 result = divOpArithmeticExceptionAction(lBigDecimal, rBigDecimal, exceptionMesg, elemTemplateElement);
    		 }
    	 }
    	 else if (left instanceof XMLNodeCursorImpl) {
    		 XMLNodeCursorImpl lNodeSet = (XMLNodeCursorImpl)left;
    		 if (lNodeSet.getLength() > 1) {
    			 error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_DIV}, elemTemplateElement); 
    		 }                  

    		 BigDecimal lBigDecimal = null;
    		 BigDecimal rBigDecimal = null;

    		 try {
    			 int nodeHandle = (lNodeSet.iter()).nextNode();
    			 DTM dtm = xctxt.getDTM(nodeHandle);

    			 XMLString xmlString = dtm.getStringValue(nodeHandle);
    			 java.lang.String lStrVal = xmlString.toString(); 

    			 java.lang.String rStrVal = XslTransformEvaluationHelper.getStrVal(right);
    			 lBigDecimal = new BigDecimal(lStrVal); 
    			 rBigDecimal = new BigDecimal(rStrVal);
    			 result = new XSDecimal(lBigDecimal.divide(rBigDecimal));
    		 }
    		 catch (NumberFormatException ex) {
    			 error(OPERAND_NOT_NUMERIC_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_DIV}, elemTemplateElement);
    		 }
    		 catch (ArithmeticException ex) {
    			 java.lang.String exceptionMesg = ex.getMessage();
    			 result = divOpArithmeticExceptionAction(lBigDecimal, rBigDecimal, exceptionMesg, elemTemplateElement);
    		 }         
    	 }
    	 else if (left instanceof XSYearMonthDuration) {
    		 try {
    			 if (right instanceof XSYearMonthDuration) {
    				 XSYearMonthDuration lValue = (XSYearMonthDuration)left;        		         		 
    				 XSYearMonthDuration rValue = (XSYearMonthDuration)right;

    				 boolean isNegativeValue = ((lValue.negative() && !rValue.negative()) || (!lValue.negative() && rValue.negative()));

    				 int lMonthValue = (lValue.year() * 12) + lValue.month();
    				 int rMonthValue = (rValue.year() * 12) + rValue.month();        		 
    				 double rDouble = (lMonthValue / (double)rMonthValue);

    				 rDouble = (isNegativeValue ? -1 * rDouble : rDouble); 

    				 result = new XSDecimal(java.lang.String.valueOf(rDouble));
    			 }
    			 else {
    				 java.lang.String rStrVal = XslTransformEvaluationHelper.getStrVal(right);
    				 result = ((XSYearMonthDuration)left).div(new XSDouble(rStrVal));
    			 }
    		 }
    		 catch (XPathException ex) {
    			 throw new javax.xml.transform.TransformerException(ex.getMessage());  
    		 }
    	 }
    	 else if (left instanceof XSDayTimeDuration) {
    		 try {
    			 if (right instanceof XSDayTimeDuration) {
    				 int days1 = ((XSDayTimeDuration)left).days();
    				 int days2 = ((XSDayTimeDuration)right).days();
    				 int resultInt = (days1 / days2);        		
    				 result = new XSDecimal(java.lang.String.valueOf(resultInt));
    			 }
    			 else {
    				 java.lang.String rStrVal = XslTransformEvaluationHelper.getStrVal(right);
    				 result = ((XSDayTimeDuration)left).div(new XSDouble(rStrVal));
    			 }
    		 }
    		 catch (XPathException ex) {
    			 throw new javax.xml.transform.TransformerException(ex.getMessage());  
    		 }
    	 }
    	 else {
    		 try {
    			 java.lang.String lStrVal = XslTransformEvaluationHelper.getStrVal(left);
    			 java.lang.String rStrVal = XslTransformEvaluationHelper.getStrVal(right);

    			 double lDouble = (Double.valueOf(lStrVal)).doubleValue();
    			 double rDouble = (Double.valueOf(rStrVal)).doubleValue();

    			 result = doubleDiv(lDouble, rDouble);
    		 }
    		 catch (NumberFormatException ex) {
    			 error(OPERAND_NOT_NUMERIC_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_DIV}, elemTemplateElement); 
    		 }
    	 }
     }
     catch (javax.xml.transform.TransformerException ex) {
         java.lang.String errMesg = ex.getMessage();
         if (errMesg.contains("FOAR0001 : An integer division by zero error")) {
        	if (right instanceof XSDouble) {
        		XSDouble xsDouble = (XSDouble)right;
        		if (xsDouble.negativeZero()) {
        		   result = new XSDouble(Double.NEGATIVE_INFINITY);	
        		}
        		else {
        		   result = new XSDouble(Double.POSITIVE_INFINITY);
        		}
        	}
        	else if (right instanceof XSDecimal) {
        		XSDecimal xsDecimal = (XSDecimal)right;
        		XSDouble xsDouble = new XSDouble(xsDecimal.doubleValue());
        		if (xsDouble.negativeZero()) {
         		   result = new XSDouble(Double.NEGATIVE_INFINITY);	
         		}
         		else {
         		   result = new XSDouble(Double.POSITIVE_INFINITY);
         		}
        	}
        	else {
        		throw ex;
        	}
         }
         else {
        	throw ex;
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
