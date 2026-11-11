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
import java.util.Base64;
import java.util.Base64.Encoder;

import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.xslt.util.XslTransformData;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xerces.impl.dv.util.HexBin;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.XPathContext;
import org.apache.xpath.compiler.OpCodes;
import org.apache.xpath.composite.XPathSequenceType;
import org.apache.xpath.composite.XPathSequenceTypeSupport;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.types.XSBase64Binary;
import org.apache.xpath.types.XSGDay;
import org.apache.xpath.types.XSGMonth;
import org.apache.xpath.types.XSGMonthDay;
import org.apache.xpath.types.XSGYear;
import org.apache.xpath.types.XSGYearMonth;
import org.apache.xpath.types.XSHexBinary;
import org.xml.sax.SAXException;

import xml.xpath31.processor.types.XSAnyType;
import xml.xpath31.processor.types.XSAnyURI;
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
    		  else if (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_URI) {    			      			  
    			  result = castToXsAnyURI(left);
    			  
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
    		  else if (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_GYEAR) {
    			  result = castToXsGYear(left);

    			  if (result != null) {
    				  return result; 
    			  }    			  
    		  }
    		  else if (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_GMONTH_DAY) {
    			  result = castToXsGMonthDay(left);

    			  if (result != null) {
    				  return result; 
    			  }    			  
    		  }
    		  else if (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_GDAY) {
    			  result = castToXsGDay(left);

    			  if (result != null) {
    				  return result; 
    			  }    			  
    		  }
    		  else if (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_GMONTH) {
    			  result = castToXsGMonth(left);

    			  if (result != null) {
    				  return result; 
    			  }    			  
    		  }
    		  else if (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_BASE64BINARY) {
    			  result = castToXsBase64Binary(left);
    			  
    			  if (result != null) {
    				 return result;  
    			  }    			  
    		  }
    		  else if (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_HEXBINARY) {
    			  result = castToXsHexBinary(left);

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
    	  catch (SAXException ex) {
    		  java.lang.String errMesg = ex.getMessage();
    		  
    		  throw new TransformerException(errMesg);
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
   * @throws TransformerException, SAXException
   */
  private XObject castToXsDecimal(XObject xObj) throws TransformerException, SAXException {
	
	  XObject result = null;

	  if (xObj instanceof XSDecimal) {
		  result = xObj;  
	  }
	  else if (xObj instanceof XSInteger) {
		  XSInteger xsInteger = (XSInteger)xObj;
		  BigInteger bigInt1 = xsInteger.intValue();

		  result = new XSDecimal(new BigDecimal(bigInt1)); 
	  }
	  else if (xObj instanceof XSFloat) {
		  XSFloat xsFloat = (XSFloat)xObj;

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
	  else if (xObj instanceof XSDouble) {
		  XSDouble xsDouble = (XSDouble)xObj;

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
	  else if ((xObj instanceof XSBoolean) || (xObj instanceof XBoolean) || (xObj instanceof XBooleanStatic)) {
		  boolean value1 = xObj.bool();

		  result = value1 ? new XSDecimal("1.0") : new XSDecimal("0.0");
	  }
	  else if ((xObj instanceof XSString) || (xObj instanceof XString) || (xObj instanceof XSUntypedAtomic)) {
		  java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj);
		  
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
	  else if (xObj instanceof XMLNodeCursorImpl) {
		  java.lang.String str1 = ((XMLNodeCursorImpl)xObj).str();
		  
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
	  else {
  	      throw new SAXException("XPTY0004 : The supplied xdm type couldn't be cast to schema type 'decimal'.");	
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
   * @throws TransformerException, SAXException
   */
  private XObject castToXsInteger(XObject xObj) throws TransformerException, SAXException {
	  
	  XObject result = null;

	  if (xObj instanceof XSInteger) {
		  result = xObj; 
	  }
	  else if (xObj instanceof XSDecimal) {
		  XSDecimal xsDecimal = (XSDecimal)xObj;    		 
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
	  else if ((xObj instanceof XSFloat) || (xObj instanceof XSDouble)) {
		  java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj);

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
					  
					  result = new XSInteger(dbl + "");
				  }
				  else if (longValue1 < 0) {
					  dbl = Math.ceil(longValue1);
					  
					  result = new XSInteger(dbl + "");
				  }
				  else {
				      result = new XSInteger("0");
				  }
			  }
			  else {
				  result = new XSInteger(str1);
			  }
		  }
		  else {
			  throw new SAXException("FOCA0002 : An xdm float/double value '" + str1 + "' could'nt be cast "
					                                                                 + "to, an XML Schema type 'integer'"); 
		  }
	  }
	  else if ((xObj instanceof XSBoolean) || (xObj instanceof XBoolean) || (xObj instanceof XBooleanStatic)) {
		  if (xObj.bool()) {
			  result = new XSInteger("1"); 
		  }
		  else {
			  result = new XSInteger("0");
		  }
	  }
	  else if ((xObj instanceof XSString) || (xObj instanceof XString) || (xObj instanceof XSUntypedAtomic)) {
		  java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj);

		  try {
			  result = new XSInteger(str1); 
		  }
		  catch (Exception ex) {
			  throw new TransformerException("FOCA0002 : An xdm string/untypedAtomic value '" + str1 + "' could'nt be cast "
					                                                                                 + "to, an XML Schema type 'integer'"); 
		  }
	  }
	  else if (xObj instanceof XMLNodeCursorImpl) {
		  java.lang.String str1 = ((XMLNodeCursorImpl)xObj).str();
		  
		  try {
			  result = new XSInteger(str1); 
		  }
		  catch (Exception ex) {
			  throw new TransformerException("FOCA0002 : An xdm node string value '" + str1 + "' could'nt be cast "
					                                                                        + "to, an XML Schema type 'integer'"); 
		  }
	  }
	  else {
  	      throw new SAXException("XPTY0004 : The supplied xdm type couldn't be cast to schema type 'integer'.");	
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
   * @throws TransformerException, SAXException
   */
   private XObject castToXsBoolean(XObject xObj) throws TransformerException, SAXException {

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
	  else if ((xObj instanceof XSString) || (xObj instanceof XString) || (xObj instanceof XSUntypedAtomic)) {
		  java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj);
		  
		  if ("true".equals(str1) || "1".equals(str1)) {
			 result = new XSBoolean(true); 
		  }
		  else if ("false".equals(str1) || "0".equals(str1)) {
			 result = new XSBoolean(false); 
		  }
		  else {
			 throw new TransformerException("FORG0001 : The supplied xdm item with string value '" + str1 + "' couldn't be cast "
			 		                                                                                      + "to XML Schema type boolean."); 
		  }
	  }
	  else if (xObj instanceof XMLNodeCursorImpl) {
		  java.lang.String str1 = ((XMLNodeCursorImpl)xObj).str();
		  
		  if ("true".equals(str1) || "1".equals(str1)) {
			 result = new XSBoolean(true); 
		  }
		  else if ("false".equals(str1) || "0".equals(str1)) {
			 result = new XSBoolean(false); 
		  }
		  else {
			 throw new TransformerException("FORG0001 : The supplied xdm item with string value '" + str1 + "' couldn't be cast "
                                                                                                          + "to XML Schema type boolean."); 
		  }
		  
		  return result;
	  }
	  else {
  	      throw new SAXException("XPTY0004 : The supplied xdm type couldn't be cast to schema type 'boolean'.");	
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
	   else if (xObj instanceof XMLNodeCursorImpl) {
		   java.lang.String str1 = ((XMLNodeCursorImpl)xObj).str();
		   
		   result = new XSString(str1);
	   }

	   return result; 
	}
   
   /**
    * Method definition, to cast the supplied xdm value
    * to an xdm xs:anyURI value.
    * 
    * @param xObj							 The supplied xdm value 
    *                                        object instance.
    * @return                                An xdm value that is the 
    *                                        result for XPath 3.1 cast 
    *                                        expression.
    * @throws TransformerException, SAXException                                        
    */
    private XObject castToXsAnyURI(XObject xObj) throws TransformerException, SAXException {
	   
    	XObject result = null;

    	if (xObj instanceof XSAnyURI) {
    		result = xObj; 
    	}
    	else if ((xObj instanceof XSUntypedAtomic) || (xObj instanceof XSString) || (xObj instanceof XString)) {
    		java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj);

    		result = new XSAnyURI(str1);
    	}
    	else if (xObj instanceof XMLNodeCursorImpl) {
  		    java.lang.String str1 = ((XMLNodeCursorImpl)xObj).str();
  		  
  		    result = new XSAnyURI(str1); 
  	    }
    	else {
    		throw new SAXException("XPTY0004 : The supplied xdm type couldn't be cast to schema type 'anyURI'."); 
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
    * @throws TransformerException, SAXException                                        
    */
   private XObject castToXsFloat(XObject xObj) throws TransformerException, SAXException {

	   XObject result = null;

	   if (xObj instanceof XSFloat) {
		   result = xObj; 
	   }
	   else if (xObj instanceof XSDouble) {
		   XSDouble xsDouble = (XSDouble)xObj;
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
	   else if ((xObj instanceof XSInteger) || (xObj instanceof XSDecimal)) {
		   XObject xObj1 = castToXsString(xObj);

		   result = new XSFloat(((XSString)xObj1).stringValue());
	   }
	   else if ((xObj instanceof XSBoolean) || (xObj instanceof XBoolean) 
			                                || (xObj instanceof XBooleanStatic)) {
		   boolean value1 = xObj.bool();

		   result = value1 ? new XSFloat(1.0E0f) : new XSFloat(0.0E0f);
	   }
	   else if ((xObj instanceof XSString) || (xObj instanceof XString) || (xObj instanceof XSUntypedAtomic)) {
		   java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj);

		   result = new XSFloat(str1);
	   }
	   else if (xObj instanceof XMLNodeCursorImpl) {
		   java.lang.String str1 = ((XMLNodeCursorImpl)xObj).str();

		   result = new XSFloat(str1);
	   }
	   else {
   	       throw new SAXException("XPTY0004 : The supplied xdm type couldn't be cast to schema type 'float'.");	
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
    * @throws TransformerException, SAXException                                        
    */
   private XObject castToXsDouble(XObject xObj) throws TransformerException, SAXException {

	   XObject result = null;

	   if (xObj instanceof XSDouble) {
		   result = xObj; 
	   }
	   else if (xObj instanceof XSFloat) {
		   XSFloat xsFloat = (XSFloat)xObj;
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
	   else if ((xObj instanceof XSInteger) || (xObj instanceof XSDecimal)) {
		   XObject xObj1 = castToXsString(xObj);

		   result = new XSDouble(((XSString)xObj1).stringValue());
	   }
	   else if ((xObj instanceof XSBoolean) || (xObj instanceof XBoolean) 
			                                || (xObj instanceof XBooleanStatic)) {
		   boolean value1 = xObj.bool();

		   result = value1 ? new XSDouble(1.0E0f) : new XSDouble(0.0E0f);
	   }
	   else if ((xObj instanceof XSString) || (xObj instanceof XString) || (xObj instanceof XSUntypedAtomic)) {
		   java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj);

		   result = new XSDouble(str1);
	   }
	   else if (xObj instanceof XMLNodeCursorImpl) {
		   java.lang.String str1 = ((XMLNodeCursorImpl)xObj).str();

		   result = new XSDouble(str1);
	   }
	   else {
   	       throw new SAXException("XPTY0004 : The supplied xdm type couldn't be cast to schema type 'double'.");	
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
    * @throws TransformerException, SAXException
    */
    private XObject castToXsYearMonthDuration(XObject xObj) throws TransformerException, SAXException {
    	
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
    	else if (xObj instanceof XMLNodeCursorImpl) {
 		   java.lang.String str1 = ((XMLNodeCursorImpl)xObj).str();

 		   result = (XSYearMonthDuration)(XSYearMonthDuration.parseYearMonthDuration(str1));
 	    }
    	else {
    	    throw new SAXException("XPTY0004 : The supplied xdm type couldn't be cast to schema type 'yearMonthDuration'.");	
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
     * @throws TransformerException, SAXException                                        
     */
    private XObject castToXsDaytimeDuration(XObject xObj) throws TransformerException, SAXException {
  	  
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
    	else if (xObj instanceof XMLNodeCursorImpl) {
  		   java.lang.String str1 = ((XMLNodeCursorImpl)xObj).str();

  		   result = (XSDayTimeDuration)(XSDayTimeDuration.parseDayTimeDuration(str1));
  	    }
    	else {
    	    throw new SAXException("XPTY0004 : The supplied xdm type couldn't be cast to schema type 'dayTimeDuration'.");	
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
     * @throws TransformerException, SAXException                                        
     */
    private XObject castToXsDuration(XObject xObj) throws TransformerException, SAXException {
    	
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
    	else if (xObj instanceof XSDuration) {
    		result = xObj;
    	}
    	else if ((xObj instanceof XSString) || (xObj instanceof XString) || (xObj instanceof XSUntypedAtomic)) {
    		java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj);

    		result = XSDuration.parseDuration(str1); 
    	}
    	else if (xObj instanceof XMLNodeCursorImpl) {
   		   java.lang.String str1 = ((XMLNodeCursorImpl)xObj).str();

   		   result = XSDuration.parseDuration(str1); 
   	    }
    	else {
    	    throw new SAXException("XPTY0004 : The supplied xdm type couldn't be cast to schema type 'duration'.");	
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
     * @throws TransformerException, SAXException                                        
     */
    private XObject castToXsDateTime(XObject xObj) throws TransformerException, SAXException {
    	
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
    	else if (xObj instanceof XMLNodeCursorImpl) {
    		java.lang.String str1 = ((XMLNodeCursorImpl)xObj).str();

    		result = XSDateTime.parseDateTime(str1); 
    	}
    	else {
    	    throw new SAXException("XPTY0004 : The supplied xdm type couldn't be cast to schema type 'dateTime'.");	
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
     * @throws TransformerException, SAXException                                        
     */
    private XObject castToXsTime(XObject xObj) throws TransformerException, SAXException {
    	
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
    	else if (xObj instanceof XMLNodeCursorImpl) {
    		java.lang.String str1 = ((XMLNodeCursorImpl)xObj).str();

    		result = XSTime.parseTime(str1);
    	}
    	else {
    	    throw new SAXException("XPTY0004 : The supplied xdm type couldn't be cast to schema type 'time'.");	
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
     * @throws TransformerException, SAXException                                        
     */
    private XObject castToXsDate(XObject xObj) throws TransformerException, SAXException {
    	
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
    	else if (xObj instanceof XMLNodeCursorImpl) {
    		java.lang.String str1 = ((XMLNodeCursorImpl)xObj).str();

    		result = XSDate.parseDate(str1);
    	}
    	else {
    	    throw new SAXException("XPTY0004 : The supplied xdm type couldn't be cast to schema type 'date'.");	
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
     * @throws TransformerException, SAXException                                        
     */ 
    private XObject castToXsGYearMonth(XObject xObj) throws TransformerException, SAXException {
    	
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
	   else if (xObj instanceof XMLNodeCursorImpl) {
		   java.lang.String str1 = ((XMLNodeCursorImpl)xObj).str();

		   result = new XSGYearMonth(str1);
   	   }
	   else {
   	       throw new SAXException("XPTY0004 : The supplied xdm type couldn't be cast to schema type 'gYearMonth'.");	
       }

	   return result;
    }
    
    /**
     * Method definition, to cast the supplied xdm value
     * to an xdm xs:gYear value.
     * 
     * @param xObj							  The supplied xdm value 
     *                                        object instance.
     * @return                                An xdm value that is the 
     *                                        result for XPath 3.1 cast 
     *                                        expression.
     * @throws TransformerException, SAXException                                        
     */ 
    private XObject castToXsGYear(XObject xObj) throws TransformerException, SAXException {
    	
	   XObject result = null;
	   
	   if (xObj instanceof XSGYear) {
		   result = xObj;  
	   }
	   else if (xObj instanceof XSDateTime) {
		   XSDateTime xsDateTime = (XSDateTime)xObj;

		   int year = xsDateTime.year();

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
           
		   java.lang.String str1 = (tzStr != null) ? (yearStr + tzStr) : yearStr;
		   
		   result = new XSGYear(str1);
	   }
	   else if (xObj instanceof XSDate) {
		   XSDate xsDate = (XSDate)xObj;    			 

		   int year = xsDate.year();

		   java.lang.String tzStr = null;

		   if (xsDate.isDateTimezoned()) {
			   XSDuration tz = xsDate.getTimezone();    		
			   tzStr = tz.getTimezoneAsString();
		   }
		   
		   java.lang.String yearStr = year + "";
		   
		   boolean isNegativeYearValue = false;
		   
		   if (yearStr.startsWith("-")) {
			  isNegativeYearValue = true;
			  
			  yearStr = yearStr.substring(1); 
		   }
		   
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
		   
		   if (isNegativeYearValue) {
			  yearStr = "-" + yearStr; 
		   }
		   
           java.lang.String str1 = (tzStr != null) ? (yearStr + tzStr) : yearStr;
		   
		   result = new XSGYear(str1);
	   }
	   else if ((xObj instanceof XSString) || (xObj instanceof XString) || (xObj instanceof XSUntypedAtomic)) {
           java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj);
		   
		   result = new XSGYear(str1);
	   }
	   else if (xObj instanceof XMLNodeCursorImpl) {
		   java.lang.String str1 = ((XMLNodeCursorImpl)xObj).str();

		   result = new XSGYear(str1);
   	   }
	   else {
   	       throw new SAXException("XPTY0004 : The supplied xdm type couldn't be cast to schema type 'gYear'.");	
       }
	   
	   return result;
    }
    
    /**
     * Method definition, to cast the supplied xdm value
     * to an xdm xs:gMonthDay value.
     * 
     * @param xObj							  The supplied xdm value 
     *                                        object instance.
     * @return                                An xdm value that is the 
     *                                        result for XPath 3.1 cast 
     *                                        expression.
     * @throws TransformerException, SAXException                                        
     */ 
    private XObject castToXsGMonthDay(XObject xObj) throws TransformerException, SAXException {
    	
	   XObject result = null;

	   if (xObj instanceof XSGMonthDay) {
		   result = xObj; 
	   }
	   else if (xObj instanceof XSDateTime) {
		   XSDateTime xsDateTime = (XSDateTime)xObj;    			 
		   
		   int month = xsDateTime.month();
		   int day = xsDateTime.day();

		   java.lang.String tzStr = null;

		   if (xsDateTime.isDateTimeTimezoned()) {
			   XSDuration tz = xsDateTime.getTimezone();    		
			   tzStr = tz.getTimezoneAsString(); 
		   }		   		   
		   
		   java.lang.String monthStr = month + "";
		   int size1 = monthStr.length();
		   
		   if (size1 == 1) {
			  monthStr = "0" + monthStr; 
		   }
		   
		   java.lang.String dayStr = day + "";
		   size1 = dayStr.length();
		   
		   if (size1 == 1) {
			  dayStr = "0" + dayStr; 
		   }
		   
		   java.lang.String str1 = "--" + monthStr + "-" + dayStr;
		   str1 = (tzStr != null) ? (str1 + tzStr) : str1;
		   
		   result = new XSGMonthDay(str1);
	   }
	   else if (xObj instanceof XSDate) {
		   XSDate xsDate = (XSDate)xObj;    			 
		   
		   int month = xsDate.month();
		   int day = xsDate.day();

		   java.lang.String tzStr = null;

		   if (xsDate.isDateTimezoned()) {
			   XSDuration tz = xsDate.getTimezone();    		
			   tzStr = tz.getTimezoneAsString(); 
		   }		   		   
		   
		   java.lang.String monthStr = month + "";
		   int size1 = monthStr.length();
		   
		   if (size1 == 1) {
			  monthStr = "0" + monthStr; 
		   }
		   
		   java.lang.String dayStr = day + "";
		   size1 = dayStr.length();
		   
		   if (size1 == 1) {
			  dayStr = "0" + dayStr; 
		   }
		   
		   java.lang.String str1 = "--" + monthStr + "-" + dayStr;
		   str1 = (tzStr != null) ? (str1 + tzStr) : str1;
		   
		   result = new XSGMonthDay(str1);
	   }
	   else if ((xObj instanceof XSString) || (xObj instanceof XString) || (xObj instanceof XSUntypedAtomic)) {
		   java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj);
		   
		   result = new XSGMonthDay(str1);
	   }
	   else if (xObj instanceof XMLNodeCursorImpl) {
		   java.lang.String str1 = ((XMLNodeCursorImpl)xObj).str();

		   result = new XSGMonthDay(str1);
   	   }
	   else {
   	       throw new SAXException("XPTY0004 : The supplied xdm type couldn't be cast to schema type 'gMonthDay'.");	
       }

	   return result;
    }
    
    /**
     * Method definition, to cast the supplied xdm value
     * to an xdm xs:gDay value.
     * 
     * @param xObj							  The supplied xdm value 
     *                                        object instance.
     * @return                                An xdm value that is the 
     *                                        result for XPath 3.1 cast 
     *                                        expression.
     * @throws TransformerException, SAXException                                        
     */ 
    private XObject castToXsGDay(XObject xObj) throws TransformerException, SAXException {
    	
	   XObject result = null;
	   
	   if (xObj instanceof XSGDay) {
		   result = xObj;  
	   }
	   else if (xObj instanceof XSDateTime) {
		   XSDateTime xsDateTime = (XSDateTime)xObj;

		   int day = xsDateTime.day();

		   java.lang.String tzStr = null;

		   if (xsDateTime.isDateTimeTimezoned()) {
			   XSDuration tz = xsDateTime.getTimezone();    		
			   tzStr = tz.getTimezoneAsString(); 
		   }
		   
		   java.lang.String dayStr = day + "";		   
		   int size1 = dayStr.length();
		   
		   if (size1 == 1) {
			  dayStr = "0" + dayStr; 
		   }
           
		   java.lang.String str1 = (tzStr != null) ? ("---" + dayStr + tzStr) : "---" + dayStr;
		   
		   result = new XSGDay(str1);
	   }
	   else if (xObj instanceof XSDate) {
		   XSDate xsDate = (XSDate)xObj;

		   int day = xsDate.day();

		   java.lang.String tzStr = null;

		   if (xsDate.isDateTimezoned()) {
			   XSDuration tz = xsDate.getTimezone();    		
			   tzStr = tz.getTimezoneAsString(); 
		   }
		   
		   java.lang.String dayStr = day + "";		   
		   int size1 = dayStr.length();
		   
		   if (size1 == 1) {
			  dayStr = "0" + dayStr; 
		   }
           
		   java.lang.String str1 = (tzStr != null) ? ("---" + dayStr + tzStr) : "---" + dayStr;
		   
		   result = new XSGDay(str1);
	   }
	   else if ((xObj instanceof XSString) || (xObj instanceof XString) || (xObj instanceof XSUntypedAtomic)) {
           java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj);
		   
		   result = new XSGDay(str1);
	   }
	   else if (xObj instanceof XMLNodeCursorImpl) {
		   java.lang.String str1 = ((XMLNodeCursorImpl)xObj).str();

		   result = new XSGDay(str1);
   	   }
	   else {
   	       throw new SAXException("XPTY0004 : The supplied xdm type couldn't be cast to schema type 'gDay'.");	
       }
	   
	   return result;
    }
    
    /**
     * Method definition, to cast the supplied xdm value
     * to an xdm xs:gMonth value.
     * 
     * @param xObj							  The supplied xdm value 
     *                                        object instance.
     * @return                                An xdm value that is the 
     *                                        result for XPath 3.1 cast 
     *                                        expression.
     * @throws TransformerException, SAXException                                        
     */ 
    private XObject castToXsGMonth(XObject xObj) throws TransformerException, SAXException {
    	
	   XObject result = null;
	   
	   if (xObj instanceof XSGMonth) {
		   result = xObj;  
	   }
	   else if (xObj instanceof XSDateTime) {
		   XSDateTime xsDateTime = (XSDateTime)xObj;

		   int month = xsDateTime.month();

		   java.lang.String tzStr = null;

		   if (xsDateTime.isDateTimeTimezoned()) {
			   XSDuration tz = xsDateTime.getTimezone();    		
			   tzStr = tz.getTimezoneAsString(); 
		   }
		   
		   java.lang.String monthStr = month + "";		   
		   int size1 = monthStr.length();
		   
		   if (size1 == 1) {
			  monthStr = "0" + monthStr; 
		   }
           
		   java.lang.String str1 = (tzStr != null) ? ("--" + monthStr + tzStr) : "--" + monthStr;
		   
		   result = new XSGMonth(str1);
	   }
	   else if (xObj instanceof XSDate) {
		   XSDate xsDate = (XSDate)xObj;

		   int month = xsDate.month();

		   java.lang.String tzStr = null;

		   if (xsDate.isDateTimezoned()) {
			   XSDuration tz = xsDate.getTimezone();    		
			   tzStr = tz.getTimezoneAsString(); 
		   }
		   
		   java.lang.String monthStr = month + "";		   
		   int size1 = monthStr.length();
		   
		   if (size1 == 1) {
			  monthStr = "0" + monthStr; 
		   }
           
		   java.lang.String str1 = (tzStr != null) ? ("--" + monthStr + tzStr) : "--" + monthStr;
		   
		   result = new XSGMonth(str1);
	   }
	   else if ((xObj instanceof XSString) || (xObj instanceof XString) || (xObj instanceof XSUntypedAtomic)) {
           java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj);
		   
		   result = new XSGMonth(str1);
	   }
	   else if (xObj instanceof XMLNodeCursorImpl) {
		   java.lang.String str1 = ((XMLNodeCursorImpl)xObj).str();

		   result = new XSGMonth(str1);
   	   }
	   else {
   	       throw new SAXException("XPTY0004 : The supplied xdm type couldn't be cast to schema type 'gMonth'.");	
       }
	   
	   return result;
    }
    
    /**
     * Method definition, to cast the supplied xdm value
     * to an xdm xs:base64Binary value.
     * 
     * @param xObj							  The supplied xdm value 
     *                                        object instance.
     * @return                                An xdm value that is the 
     *                                        result for XPath 3.1 cast 
     *                                        expression.
     * @throws TransformerException, SAXException                                       
     */ 
    private XObject castToXsBase64Binary(XObject xObj) throws TransformerException, SAXException {
    	
    	XObject result = null;

    	if (xObj instanceof XSBase64Binary) {
    		result = xObj; 
    	}
    	else if (xObj instanceof XSHexBinary) {
    		java.lang.String str1 = ((XSHexBinary)xObj).stringValue();
    		byte[] decoded = HexBin.decode(str1);

    		Encoder base64Encoder = Base64.getEncoder();
    		str1 = base64Encoder.encodeToString(decoded);

    		result = new XSBase64Binary(str1);
    	}
    	else if ((xObj instanceof XSUntypedAtomic) || (xObj instanceof XSString) || (xObj instanceof XString)) {
    		java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj);

    		result = new XSBase64Binary(str1);
    	}
    	else if (xObj instanceof XMLNodeCursorImpl) {
    		java.lang.String str1 = ((XMLNodeCursorImpl)xObj).str();

    		result = new XSBase64Binary(str1);
    	}
    	else {
    	    throw new SAXException("XPTY0004 : The supplied xdm type couldn't be cast to schema type 'base64Binary'.");	
        }

    	return result;
    }
    
    /**
     * Method definition, to cast the supplied xdm value
     * to an xdm xs:hexBinary value.
     * 
     * @param xObj							  The supplied xdm value 
     *                                        object instance.
     * @return                                An xdm value that is the 
     *                                        result for XPath 3.1 cast 
     *                                        expression.
     * @throws TransformerException, SAXException                                        
     */ 
    private XObject castToXsHexBinary(XObject xObj) throws TransformerException, SAXException {
    	
    	XObject result = null;

    	if (xObj instanceof XSHexBinary) {
    		result = xObj; 
    	}
    	else if (xObj instanceof XSBase64Binary) {    		    		
    		java.lang.String str1 = ((XSBase64Binary)xObj).stringValue();

    		byte[] decodedBytes = Base64.getDecoder().decode(str1.getBytes());

    		StringBuffer strBuff = new StringBuffer();                        

    		for (int idx = 0; idx < decodedBytes.length; idx++) {
    			strBuff.append(java.lang.String.format("%02X", decodedBytes[idx]));
    		}

    		str1 = strBuff.toString();

    		result = new XSHexBinary(str1);
    	}
    	else if ((xObj instanceof XSUntypedAtomic) || (xObj instanceof XSString) || (xObj instanceof XString)) {
    		java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj);

    		result = new XSHexBinary(str1);
    	}
    	else if (xObj instanceof XMLNodeCursorImpl) {
    		java.lang.String str1 = ((XMLNodeCursorImpl)xObj).str();

    		result = new XSHexBinary(str1);
    	}
    	else {
    	    throw new SAXException("XPTY0004 : The supplied xdm type couldn't be cast to schema type 'hexBinary'.");	
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
