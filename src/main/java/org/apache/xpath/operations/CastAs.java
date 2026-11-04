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
import java.math.RoundingMode;

import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.xslt.util.XslTransformData;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.XPathContext;
import org.apache.xpath.compiler.OpCodes;
import org.apache.xpath.composite.XPathSequenceType;
import org.apache.xpath.composite.XPathSequenceTypeSupport;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.types.XSGYearMonth;

import xml.xpath31.processor.types.XSAnyType;
import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSDate;
import xml.xpath31.processor.types.XSDateTime;
import xml.xpath31.processor.types.XSDayTimeDuration;
import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSDuration;
import xml.xpath31.processor.types.XSFloat;
import xml.xpath31.processor.types.XSInteger;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSQName;
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSTime;
import xml.xpath31.processor.types.XSUntypedAtomic;
import xml.xpath31.processor.types.XSYearMonthDuration;

/**
 * An implementation of XPath 3.1 "cast as" expression 
 * evaluation.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class CastAs extends XPathOperator
{

   private static final long serialVersionUID = -4194858144694864568L;
   
   /**
    * Class field, to denote whether an XPath 3.1 "cast as" expression,
    * as evaluated by this class object, is for an XPath 3.1 function
    * item parameter & result type checks.
    */
   boolean m_func_param_result_type_check = false;
   
   /**
    * Class constructor.
    */
   public CastAs() {
	  // No op 
   }

   /**
    * Apply an XPath operator to its two operands, and return the result.
    *
    * @param left  non-null reference to an XPath operator's evaluated 
    *              first operand.              
    * @param right non-null reference to an XPath operator's evaluated 
    *              second operand.
    *
    * @return non-null reference to an XObject object instance, that 
    *         represents the result of XPath operator evaluation. 
    *
    * @throws javax.xml.transform.TransformerException
    */
  public XObject operate(XObject left, XObject right) 
                                                 throws javax.xml.transform.TransformerException
  {
	  XObject result = null;
      
      XPathSequenceType seqTypedData = (XPathSequenceType)right;
      
      ExpressionNode exprNode = getExpressionOwner();
      XPathContext xpathContext = null;
      
      if (exprNode instanceof ElemTemplateElement) {
    	 xpathContext = ((ElemTemplateElement)exprNode).getXPathContext();
      }
            
      if (left instanceof XNumber) {
    	  left = XslTransformEvaluationHelper.getXNumberNormalizedValue((XNumber)left);  
      }

      if (!m_func_param_result_type_check) {
    	  try {
    		  if (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.STRING) {
    			  result = castToXsString(left);

    			  if (result != null) {
    				  return result; 
    			  }
    		  }
    		  else if (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_UNTYPED_ATOMIC) {
    			  result = castToXsString(left);

    			  if (result != null) {
    				  java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(result);    	    

    				  result = new XSUntypedAtomic(str1);

    				  return result;
    			  }
    		  }
    		  else if (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_FLOAT) {
    			  result = castToXsFloat(left);

    			  if (result != null) {
    				  return result;
    			  }
    		  }
    		  else if (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_DOUBLE) {
    			  result = castToXsDouble(left);

    			  if (result != null) {
    				  return result;
    			  }
    		  }
    		  else if (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_DECIMAL) {
    			  result = castToXsDecimal(left);

    			  if (result != null) {
    				  return result;
    			  }
    		  }
    		  else if (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_INTEGER) {
    			  result = castToXsInteger(left);

    			  if (result != null) {
    				  return result;
    			  }
    		  } 
    		  else if (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.BOOLEAN) {
    			  result = castToXsBoolean(left);

    			  if (result != null) {
    				  return result;
    			  }
    		  }
    		  else if (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_YEARMONTH_DURATION) {
    			  result = castToXsYearMonthDuration(left);

    			  if (result != null) {
    				  return result;
    			  }
    		  }
    		  else if (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_DAYTIME_DURATION) {
    			  result = castToXsDaytimeDuration(left);

    			  if (result != null) {
    				  return result;
    			  }
    		  }
    		  else if (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_DURATION) {
    			  result = castToXsDuration(left);

    			  if (result != null) {
    				  return result;
    			  }
    		  }
    		  else if (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_DATETIME) {
    			  result = castToXsDateTime(left);

    			  if (result != null) {
    				  return result; 
    			  }
    		  }
    		  else if (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_TIME) {
    			  result = castToXsTime(left);

    			  if (result != null) {
    				  return result; 
    			  }
    		  }
    		  else if (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_DATE) {
    			  result = castToXsDate(left);

    			  if (result != null) {
    				  return result; 
    			  }
    		  }
    		  else if (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_GYEAR_MONTH) {
    			  result = castToXsGYearMonth(left);

    			  if (result != null) {
    				  return result; 
    			  }
    		  }
    	  }
    	  catch (TransformerException ex) {
    		  java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(left);
    		  
    		  java.lang.String dataTypeName = XPathSequenceTypeSupport.getDataTypeNameFromIntValue(seqTypedData.getBuiltInSequenceType());
    		  
    		  if (dataTypeName == null) {
    			 dataTypeName = ""; 
    		  }
    		  else {
    			 dataTypeName = " " + dataTypeName;  
    		  }
    		  
    		  throw new TransformerException("FORG0001 : The supplied value '" + str1 + "' couldn't be cast, to the specified type" + dataTypeName + ".");  
    	  }
      }
      
      if (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_NUMERIC) {
		  result = castToXsNumeric(left);

		  if (result != null) {
			  return result;
		  }
	  }
      
      try {
    	  if (XslTransformData.m_xpathCallingOpCode == OpCodes.XPath3OpCodes.OP_IDIV) {
    		  if (left instanceof XSDecimal) {
    			  XSDecimal xsDecimal = (XSDecimal)left;
    			  double dblValue = (xsDecimal.getValue()).doubleValue();
    			  
    			  if (dblValue >= 0) {
    				  int intValue = (int)(Math.floor(dblValue));
    				  java.lang.String intStrValue = java.lang.String.valueOf(intValue);

    				  result = new XSInteger(intStrValue);
    			  }
    			  else {
    				  int intValue = (int)(Math.ceil(dblValue));
    				  java.lang.String intStrValue = java.lang.String.valueOf(intValue);

    				  result = new XSInteger(intStrValue);
    			  }
    			  
    			  return result;
    		  }
    		  else if (left instanceof XSDouble) {
    			  double dblValue = ((XSDouble)left).doubleValue();    			  
    			  
    			  if (dblValue >= 0) {
    				  int intValue = (int)(Math.floor(dblValue));
    				  java.lang.String intStrValue = java.lang.String.valueOf(intValue);

    				  result = new XSInteger(intStrValue);
    			  }
    			  else {
    				  int intValue = (int)(Math.ceil(dblValue));
    				  java.lang.String intStrValue = java.lang.String.valueOf(intValue);

    				  result = new XSInteger(intStrValue);
    			  }
    			  
    			  return result;
    		  }
    	  }
      }
      finally {
    	  // Reset the value of variable XslTransformData.m_xpathCallingOpCode 
    	  XslTransformData.m_xpathCallingOpCode = Integer.MIN_VALUE;
      }
      
      if (left instanceof XSDecimal) {
    	  if (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.STRING) {
              java.lang.String str1 = ((XSDecimal)left).stringValue();
              
              return new XSString(str1);
    	  }
      }
      
      // Evaluate an XPath 3.1 "cast as" expression, when XPath "idiv" expression 
      // evaluation has not been requested within original XPath expression input.
      result = XPathSequenceTypeSupport.castXdmValueToAnotherType(left, null, seqTypedData, xpathContext);
      
      result.setCastAsType(seqTypedData);
      
      return result;
  }

  /**
   * Method definition, to cast the supplied xdm value
   * to an xdm xs:decimal value.
   * 
   * @param xObj							The supplied xdm value 
   *                                        object instance.
   * @return                                An xdm value that is the 
   *                                        result for XPath 3.1 cast 
   *                                        expression.
   * @throws TransformerException
   */
  private XObject castToXsDecimal(XObject left) throws TransformerException {
	
	  XObject result = null;

	  if (left instanceof XSDecimal) {
		  result = left;  
	  }
	  else if (left instanceof XSInteger) {
		  XSInteger xsInteger = (XSInteger)left;
		  BigInteger bigInt1 = xsInteger.intValue();

		  result = new XSDecimal(new BigDecimal(bigInt1)); 
	  }
	  else if (left instanceof XSFloat) {
		  XSFloat xsFloat = (XSFloat)left;

		  if (!(xsFloat.nan() || xsFloat.infinite())) {
			  try {
				  result = new XSDecimal(xsFloat.stringValue());
			  }
			  catch (Exception ex) {
				  throw new TransformerException("FOCA0001 : The supplied xdm float value " + xsFloat.toString() + " could'nt be "
						                                                                                         + "cast to an xdm type 'decimal'."); 
			  }
		  }
		  else {
			  throw new TransformerException("FOCA0002 : The supplied xdm float value " + xsFloat.toString() + " could'nt be "
					                                                                                         + "cast to an xdm type 'decimal'."); 
		  }
	  }
	  else if (left instanceof XSDouble) {
		  XSDouble xsDouble = (XSDouble)left;

		  if (!(xsDouble.nan() || xsDouble.infinite())) {
			  try {
				  result = new XSDecimal(xsDouble.stringValue());
			  }
			  catch (Exception ex) {
				  throw new TransformerException("FOCA0001 : The supplied xdm double value " + xsDouble.toString() + " could'nt be "
						                                                                                           + "cast to an xdm type 'decimal'."); 
			  }
		  }
		  else {
			  throw new TransformerException("FOCA0002 : The supplied xdm double value " + xsDouble.toString() + " could'nt be "
					                                                                                           + "cast to an xdm type 'decimal'."); 
		  }
	  }
	  else if ((left instanceof XSBoolean) || (left instanceof XBoolean) || (left instanceof XBooleanStatic)) {
		  boolean value1 = left.bool();

		  result = value1 ? new XSDecimal("1.0") : new XSDecimal("0.0");
	  }
	  else if ((left instanceof XSString) || (left instanceof XString) || (left instanceof XSUntypedAtomic)) {
		  java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(left);
		  
		  if (str1.contains("E") || str1.contains("e")) {
			 throw new TransformerException("FORG0001 : The supplied xdm string value '" + str1 + "' could'nt be "
                                                                                                + "cast to an xdm type 'decimal'.");
		  }

		  try {
			  result = new XSDecimal(str1);
		  }
		  catch (Exception ex) {
			  throw new TransformerException("FORG0001 : The supplied xdm string value '" + str1 + "' could'nt be "
					                                                                             + "cast to an xdm type 'decimal'."); 
		  }
	  }

	  return result;
  }
  
  /**
   * Method definition, to cast the supplied xdm value
   * to an xdm xs:integer value.
   * 
   * @param xObj							The supplied xdm value 
   *                                        object instance.
   * @return                                An xdm value that is the 
   *                                        result for XPath 3.1 cast 
   *                                        expression.
   * @throws TransformerException
   */
  private XObject castToXsInteger(XObject left) throws TransformerException {
	  
	  XObject result = null;

	  if (left instanceof XSInteger) {
		  result = left; 
	  }
	  else if (left instanceof XSDecimal) {
		  XSDecimal xsDecimal = (XSDecimal)left;    		 
		  BigDecimal bigdecimal = xsDecimal.getValue();

		  BigDecimal bigDecimal1 = null;

		  if (bigdecimal.compareTo(BigDecimal.valueOf(0)) > 0) {
			  bigDecimal1 = bigdecimal.setScale(0, RoundingMode.FLOOR); 
		  }
		  else if (bigdecimal.compareTo(BigDecimal.valueOf(0)) < 0) {
			  bigDecimal1 = bigdecimal.setScale(0, RoundingMode.CEILING);
		  }
		  else {
			  bigDecimal1 = bigdecimal;  
		  }

		  result = new XSInteger(bigDecimal1.toBigInteger());
	  }
	  else if ((left instanceof XSFloat) || (left instanceof XSDouble)) {
		  java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(left);

		  if (!("NaN".equals(str1) || "INF".equals(str1) || "-INF".equals(str1))) {
			  int idx = str1.indexOf('.');

			  if (idx != -1) {
				  // An xs:float, xs:double value fractional part is truncated,
				  // and the resulting value is cast to an xs:integer value.

				  str1 = str1.substring(0, idx);
				  long longValue1 = 0;

				  if (!str1.startsWith("-")) {
					  longValue1 = Integer.valueOf(str1);  
				  }
				  else {
					  longValue1 = Integer.valueOf(str1);
				  }

				  double dbl = 0.0;

				  if (longValue1 > 0) {
					  dbl = Math.floor(longValue1); 
				  }
				  else {
					  dbl = Math.ceil(longValue1);
				  }

				  result = new XSInteger(dbl + "");
			  }
			  else {
				  result = new XSInteger(str1);
			  }
		  }
		  else {
			  throw new TransformerException("FOCA0002 : An xdm float/double value '" + str1 + "' could'nt be cast "
					                                                                         + "to, an XML Schema type 'integer'"); 
		  }
	  }
	  else if ((left instanceof XSBoolean) || (left instanceof XBoolean) || (left instanceof XBooleanStatic)) {
		  if (left.bool()) {
			  result = new XSInteger("1"); 
		  }
		  else {
			  result = new XSInteger("0");
		  }
	  }
	  else if ((left instanceof XSString) || (left instanceof XSUntypedAtomic)) {
		  java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(left);

		  try {
			  result = new XSInteger(str1); 
		  }
		  catch (Exception ex) {
			  throw new TransformerException("FOCA0002 : An xdm string/untypedAtomic value '" + str1 + "' could'nt be cast "
					                                                                                 + "to, an XML Schema type 'integer'"); 
		  }
	  }

	  return result;
  }

  /**
   * Method definition, to cast the supplied xdm value
   * to an xdm xs:numeric value.
   * 
   * @param xObj							The supplied xdm value 
   *                                        object instance.
   * @return                                An xdm value that is the 
   *                                        result for XPath 3.1 cast 
   *                                        expression.
   * @throws TransformerException
   */
  private XObject castToXsNumeric(XObject xObj) throws TransformerException {
	  
	  XObject result = null;

	  if (xObj instanceof XSNumericType) {
		  result = xObj; 
	  }
	  else if (xObj instanceof XNumber) {
		  result = new XSDouble(((XNumber)xObj).num()); 
	  }
	  else if ((xObj instanceof XSBoolean) || (xObj instanceof XBoolean) || (xObj instanceof XBooleanStatic)) {
		  boolean value1 = xObj.bool();

		  result = value1 ? new XSDouble(1) : new XSDouble(0);
	  }

	  return result; 
   }
  
  /**
   * Method definition, to cast the supplied xdm value
   * to an xdm xs:boolean value.
   * 
   * @param xObj							The supplied xdm value 
   *                                        object instance.
   * @return                                An xdm value that is the 
   *                                        result for XPath 3.1 cast 
   *                                        expression.
   * @throws TransformerException
   */
   private XObject castToXsBoolean(XObject xObj) throws TransformerException {

	  XObject result = null;

	  if ((xObj instanceof XSBoolean) || (xObj instanceof XBoolean) || (xObj instanceof XBooleanStatic)) {
		  result = new XSBoolean(xObj.bool());	    	    
	  }	 
	  else if (xObj instanceof XSFloat) {
		  XSFloat xsFloat = (XSFloat)xObj;

		  if (xsFloat.zero() || xsFloat.negativeZero() || xsFloat.nan()) {
			  result = new XSBoolean(false);
		  }
		  else {
			  result = new XSBoolean(true);
		  }
	  }    	 
	  else if (xObj instanceof XSDouble) {
		  XSDouble xsDouble = (XSDouble)xObj;

		  if (xsDouble.zero() || xsDouble.negativeZero() || xsDouble.nan()) {
			  result = new XSBoolean(false);
		  }
		  else {
			  result = new XSBoolean(true);
		  }
	  }
	  else if (xObj instanceof XSDecimal) {
		  XSDecimal xsDecimal = (XSDecimal)xObj;
		  BigDecimal bigDecimal = xsDecimal.getValue();

		  if (bigDecimal.compareTo(new BigDecimal(0)) == 0) {
			  result = new XSBoolean(false);
		  }
		  else {
			  result = new XSBoolean(true);
		  }
	  }
	  else if (xObj instanceof XSInteger) {
		  XSInteger xsInteger = (XSInteger)xObj;
		  BigDecimal bigDecimal = xsInteger.getValue();

		  if (bigDecimal.compareTo(new BigDecimal(0)) == 0) {
			  result = new XSBoolean(false);
		  }
		  else {
			  result = new XSBoolean(true);
		  }
	  }

	  return result;
   }
   
   /**
    * Method definition, to cast the supplied xdm value
    * to an xdm xs:string value.
    * 
    * @param xObj							 The supplied xdm value 
    *                                        object instance.
    * @return                                An xdm value that is the 
    *                                        result for XPath 3.1 cast 
    *                                        expression.
    */
   private XObject castToXsString(XObject xObj) {
	   
	   XObject result = null;

	   if ((xObj instanceof XSString) || (xObj instanceof XString)) {
		   java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj);

		   result = new XSString(str1);
	   }
	   else if (xObj instanceof XSQName) {
		   XSQName xsQName = (XSQName)xObj;

		   java.lang.String prefix = xsQName.getPrefix();
		   java.lang.String localName = xsQName.getLocalPart();

		   java.lang.String str1 = null;

		   if ((prefix != null) && !"".equals(prefix)) {
			   str1 = prefix + ":" + localName; 
		   }
		   else {
			   str1 = localName; 	
		   }

		   result = new XSString(str1);
	   }
	   else if (xObj instanceof XSInteger) {
		   result = new XSString(((XSInteger)xObj).stringValue()); 
	   }
	   else if (xObj instanceof XSDecimal) {
		   XSDecimal xsDecimal = (XSDecimal)xObj;
		   BigDecimal bigDecimal = xsDecimal.getValue();

		   boolean isXSInteger = true;

		   try {
			   XSInteger xsInteger = new XSInteger(bigDecimal.toString());    		   

			   result = new XSString(xsInteger.stringValue());
		   }
		   catch (Exception ex) {
			   isXSInteger = false;
		   }

		   if (!isXSInteger) {
			   result = new XSString(xsDecimal.stringValue());
		   }
	   }
	   else if (xObj instanceof XSAnyType) {
		   java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj);

		   result = new XSString(str1);
	   }
	   else if ((xObj instanceof XBoolean) || (xObj instanceof XBooleanStatic)) {
		   result = new XSString(xObj.str());
	   }

	   return result; 
	}
   
   /**
    * Method definition, to cast the supplied xdm value
    * to an xdm xs:float value.
    * 
    * @param xObj							 The supplied xdm value 
    *                                        object instance.
    * @return                                An xdm value that is the 
    *                                        result for XPath 3.1 cast 
    *                                        expression.
    */
   private XObject castToXsFloat(XObject left) throws TransformerException {

	   XObject result = null;

	   if (left instanceof XSFloat) {
		   result = left; 
	   }
	   else if (left instanceof XSDouble) {
		   XSDouble xsDouble = (XSDouble)left;
		   Double dbl = (Double)(xsDouble.doubleValue());

		   if (dbl == Double.POSITIVE_INFINITY) {
			   result = new XSFloat(Float.POSITIVE_INFINITY); 
		   }
		   else if (dbl == Double.NEGATIVE_INFINITY) {
			   result = new XSFloat(Float.NEGATIVE_INFINITY);
		   }
		   else if (xsDouble.nan()) {
			   result = new XSFloat(Float.NaN); 
		   }
		   else if (xsDouble.zero()) {
			   result = new XSFloat("0"); 
		   }
		   else if (xsDouble.negativeZero()) {
			   result = new XSFloat("-0"); 
		   }
		   else {
			   float val1 = (float)(dbl.doubleValue());

			   result = new XSFloat(val1);
		   }
	   }
	   else if ((left instanceof XSInteger) || (left instanceof XSDecimal)) {
		   XObject xObj = castToXsString(left);

		   result = new XSFloat(((XSString)xObj).stringValue());
	   }
	   else if ((left instanceof XSBoolean) || (left instanceof XBoolean) 
			                                || (left instanceof XBooleanStatic)) {
		   boolean value1 = left.bool();

		   result = value1 ? new XSFloat(1.0E0f) : new XSFloat(0.0E0f);
	   }
	   else if ((left instanceof XSString) || (left instanceof XString) || (left instanceof XSUntypedAtomic)) {
		   java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(left);

		   result = new XSFloat(str1);
	   }
	   
	   return result;
    }
   
   /**
    * Method definition, to cast the supplied xdm value
    * to an xdm xs:double value.
    * 
    * @param xObj							 The supplied xdm value 
    *                                        object instance.
    * @return                                An xdm value that is the 
    *                                        result for XPath 3.1 cast 
    *                                        expression.
    */
   private XObject castToXsDouble(XObject left) throws TransformerException {

	   XObject result = null;

	   if (left instanceof XSDouble) {
		   result = left; 
	   }
	   else if (left instanceof XSFloat) {
		   XSFloat xsFloat = (XSFloat)left;
		   Float flt = (Float)(xsFloat.floatValue());

		   if (flt == Float.POSITIVE_INFINITY) {
			   result = new XSDouble(Double.POSITIVE_INFINITY); 
		   }
		   else if (flt == Float.NEGATIVE_INFINITY) {
			   result = new XSDouble(Double.NEGATIVE_INFINITY);
		   }
		   else if (xsFloat.nan()) {
			   result = new XSDouble(Double.NaN); 
		   }
		   else if (xsFloat.zero()) {
			   result = new XSDouble("0"); 
		   }
		   else if (xsFloat.negativeZero()) {
			   result = new XSDouble("-0"); 
		   }
		   else {
			   double val1 = flt.doubleValue();

			   result = new XSDouble(val1);
		   }
	   }
	   else if ((left instanceof XSInteger) || (left instanceof XSDecimal)) {
		   XObject xObj = castToXsString(left);

		   result = new XSDouble(((XSString)xObj).stringValue());
	   }
	   else if ((left instanceof XSBoolean) || (left instanceof XBoolean) 
			   || (left instanceof XBooleanStatic)) {
		   boolean value1 = left.bool();

		   result = value1 ? new XSDouble(1.0E0f) : new XSDouble(0.0E0f);
	   }
	   else if ((left instanceof XSString) || (left instanceof XString) || (left instanceof XSUntypedAtomic)) {
		   java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(left);

		   result = new XSDouble(str1);
	   }

	   return result;
    }
   
   /**
    * Method definition, to cast the supplied xdm value
    * to an xdm xs:yearMonthDuration value.
    * 
    * @param xObj							 The supplied xdm value 
    *                                        object instance.
    * @return                                An xdm value that is the 
    *                                        result for XPath 3.1 cast 
    *                                        expression.
    */
    private XObject castToXsYearMonthDuration(XObject xObj) throws TransformerException {
    	
    	XObject result = null;

    	if (xObj instanceof XSYearMonthDuration) {
    		result = xObj;  
    	}
    	else if ((xObj instanceof XSDuration) && !(xObj instanceof XSDayTimeDuration)) {
    		result = XSYearMonthDuration.castToYearMonthDuration((XSDuration)xObj);
    	}
    	else if (xObj instanceof XSDayTimeDuration) {
    		result = new XSYearMonthDuration(0);  
    	}
    	else if ((xObj instanceof XSString) || (xObj instanceof XString) || (xObj instanceof XSUntypedAtomic)) {
    		java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj);

    		result = (XSYearMonthDuration)(XSYearMonthDuration.parseYearMonthDuration(str1));
    	}

    	return result;
	}
    
    /**
     * Method definition, to cast the supplied xdm value
     * to an xdm xs:dayTimeDuration value.
     * 
     * @param xObj							  The supplied xdm value 
     *                                        object instance.
     * @return                                An xdm value that is the 
     *                                        result for XPath 3.1 cast 
     *                                        expression.
     */
    private XObject castToXsDaytimeDuration(XObject xObj) throws TransformerException {
  	  
    	XObject result = null;

    	if (xObj instanceof XSDayTimeDuration) {
    		result = xObj;  
    	}
    	else if ((xObj instanceof XSDuration) && !(xObj instanceof XSYearMonthDuration)) {
    		result = XSDayTimeDuration.castToDayTimeDuration((XSDuration)xObj);
    	}
    	else if (xObj instanceof XSYearMonthDuration) {
    		result = new XSDayTimeDuration(0.0);  
    	}
    	else if ((xObj instanceof XSString) || (xObj instanceof XString) || (xObj instanceof XSUntypedAtomic)) {
    		java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj);

    		result = (XSDayTimeDuration)(XSDayTimeDuration.parseDayTimeDuration(str1));
    	}

    	return result;
    }
    
    /**
     * Method definition, to cast the supplied xdm value
     * to an xdm xs:duration value.
     * 
     * @param xObj							  The supplied xdm value 
     *                                        object instance.
     * @return                                An xdm value that is the 
     *                                        result for XPath 3.1 cast 
     *                                        expression.
     */
    private XObject castToXsDuration(XObject xObj) throws TransformerException {
    	
    	XObject result = null;

    	if (xObj instanceof XSYearMonthDuration) {
    		XSYearMonthDuration xsYearMonthDuration = (XSYearMonthDuration)xObj;

    		result = new XSDuration(xsYearMonthDuration.year(), xsYearMonthDuration.month(), 0, 0, 
    				                                                                        0, 0.0, xsYearMonthDuration.negative()); 
    	}
    	else if (xObj instanceof XSDayTimeDuration) {
    		XSDayTimeDuration xsDayTimeDuration = (XSDayTimeDuration)xObj;

    		result = new XSDuration(0, 0, xsDayTimeDuration.days(), xsDayTimeDuration.hours(), xsDayTimeDuration.minutes(), 
																			    				xsDayTimeDuration.seconds(), 
																			    				xsDayTimeDuration.negative());  
    	}
    	else if ((xObj instanceof XSString) || (xObj instanceof XString) || (xObj instanceof XSUntypedAtomic)) {
    		java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj);

    		result = XSDuration.parseDuration(str1); 
    	}

    	return result;
    }
    
    /**
     * Method definition, to cast the supplied xdm value
     * to an xdm xs:dateTime value.
     * 
     * @param xObj							  The supplied xdm value 
     *                                        object instance.
     * @return                                An xdm value that is the 
     *                                        result for XPath 3.1 cast 
     *                                        expression.
     */
    private XObject castToXsDateTime(XObject xObj) throws TransformerException {
    	
    	XObject result = null;

    	if (xObj instanceof XSDateTime) {
    		result = xObj;  
    	}
    	else if (xObj instanceof XSDate) {
    		result = XSDateTime.castToDateTime((XSDate)xObj);
    	}
    	else if ((xObj instanceof XSString) || (xObj instanceof XString) || (xObj instanceof XSUntypedAtomic)) {
    		java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj);

    		result = XSDateTime.parseDateTime(str1);
    	}

    	return result;
    }
    
    /**
     * Method definition, to cast the supplied xdm value
     * to an xdm xs:time value.
     * 
     * @param xObj							  The supplied xdm value 
     *                                        object instance.
     * @return                                An xdm value that is the 
     *                                        result for XPath 3.1 cast 
     *                                        expression.
     */
    private XObject castToXsTime(XObject xObj) throws TransformerException {
    	
    	XObject result = null;

    	if (xObj instanceof XSTime) {
    		result = xObj; 
    	}
    	else if (xObj instanceof XSDateTime) {
    		XSDateTime xsDateTime = (XSDateTime)xObj;    			 

    		result = XSTime.castToTime(xsDateTime);
    	}
    	else if ((xObj instanceof XSString) || (xObj instanceof XString) || (xObj instanceof XSUntypedAtomic)) {
    		java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj);

    		result = XSTime.parseTime(str1);
    	}

    	return result;
    }
    
    /**
     * Method definition, to cast the supplied xdm value
     * to an xdm xs:date value.
     * 
     * @param xObj							  The supplied xdm value 
     *                                        object instance.
     * @return                                An xdm value that is the 
     *                                        result for XPath 3.1 cast 
     *                                        expression.
     */
    private XObject castToXsDate(XObject xObj) throws TransformerException {
    	
    	XObject result = null;

    	if (xObj instanceof XSDate) {
    		result = xObj; 
    	}
    	else if (xObj instanceof XSDateTime) {
    		XSDateTime xsDateTime = (XSDateTime)xObj;    			 

    		result = XSDate.castToDate(xsDateTime);
    	}
    	else if ((xObj instanceof XSString) || (xObj instanceof XString) || (xObj instanceof XSUntypedAtomic)) {
    		java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj);

    		result = XSDate.parseDate(str1);
    	}

    	return result;
    }
    
    /**
     * Method definition, to cast the supplied xdm value
     * to an xdm xs:gYearMonth value.
     * 
     * @param xObj							  The supplied xdm value 
     *                                        object instance.
     * @return                                An xdm value that is the 
     *                                        result for XPath 3.1 cast 
     *                                        expression.
     */ 
    private XObject castToXsGYearMonth(XObject xObj) throws TransformerException {
    	
	   XObject result = null;

	   if (xObj instanceof XSGYearMonth) {
		   result = xObj; 
	   }
	   else if (xObj instanceof XSDateTime) {
		   XSDateTime xsDateTime = (XSDateTime)xObj;    			 

		   int year = xsDateTime.year();
		   int month = xsDateTime.month();

		   java.lang.String tzStr = null;

		   if (xsDateTime.isDateTimeTimezoned()) {
			   XSDuration tz = xsDateTime.getTimezone();    		
			   tzStr = tz.getTimezoneAsString(); 
		   }
		   
		   java.lang.String yearStr = year + "";		   
		   int size1 = yearStr.length();
		   
		   if (size1 == 1) {
			  yearStr = "000" + yearStr; 
		   }
		   else if (size1 == 2) {
			  yearStr = "00" + yearStr; 
		   }
		   else if (size1 == 3) {
			  yearStr = "0" + yearStr; 
		   }
		   
		   java.lang.String monthStr = month + "";
		   size1 = monthStr.length();
		   
		   if (size1 == 1) {
			  monthStr = "0" + monthStr; 
		   }

		   java.lang.String str1 = yearStr + "-" + monthStr;
		   str1 = (tzStr != null) ? (str1 + tzStr) : str1;

		   result = new XSGYearMonth(str1);
	   }
	   else if (xObj instanceof XSDate) {
		   XSDate xsDate = (XSDate)xObj;    			 

		   int year = xsDate.year();
		   int month = xsDate.month();

		   java.lang.String tzStr = null;

		   if (xsDate.isDateTimezoned()) {
			   XSDuration tz = xsDate.getTimezone();    		
			   tzStr = tz.getTimezoneAsString();
		   }
		   
		   java.lang.String yearStr = year + "";		   
		   int size1 = yearStr.length();
		   
		   if (size1 == 1) {
			  yearStr = "000" + yearStr; 
		   }
		   else if (size1 == 2) {
			  yearStr = "00" + yearStr; 
		   }
		   else if (size1 == 3) {
			  yearStr = "0" + yearStr; 
		   }
		   
		   java.lang.String monthStr = month + "";
		   size1 = monthStr.length();
		   
		   if (size1 == 1) {
			  monthStr = "0" + monthStr; 
		   }

		   java.lang.String str1 = yearStr + "-" + monthStr;
		   str1 = (tzStr != null) ? (str1 + tzStr) : str1;

		   result = new XSGYearMonth(str1);
	   }
	   else if ((xObj instanceof XSString) || (xObj instanceof XString) || (xObj instanceof XSUntypedAtomic)) {
		   java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj);
		   
		   result = new XSGYearMonth(str1);
	   }

	   return result;
    }
   
    public boolean getFuncParamResultTypeCheck() {
       return m_func_param_result_type_check;
    }

    public void setFuncParamResultTypeCheck(boolean funcParamResultTypeCheck) {
       m_func_param_result_type_check = funcParamResultTypeCheck; 	
    }    
  
}
