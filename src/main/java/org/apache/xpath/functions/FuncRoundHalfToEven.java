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
package org.apache.xpath.functions;

import java.math.BigDecimal;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.res.XPATHErrorResources;

import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSFloat;
import xml.xpath31.processor.types.XSInteger;
import xml.xpath31.processor.types.XSNumericType;

/**
 * Implementation of XPath 3.1 function fn:round-half-to-even.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncRoundHalfToEven extends Function2Args
{
      
	 private static final long serialVersionUID = 4199285731813500143L;

	 /**
      * Class constructor.
      */
      public FuncRoundHalfToEven() {
    	  m_defined_arity = new Short[] { 1, 2 };  
      }
    
      /**
       * Implementation of the function. The function must return a valid object.
       * 
       * @param xctxt The current execution context.
       * @return A valid XObject.
       *
       * @throws javax.xml.transform.TransformerException
       */
      public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {          
          
          XObject result = null;
          
          SourceLocator srcLocator = xctxt.getSAXLocator();
          
          String strValueOfArg0 = (getArgAsString(m_arg0, xctxt)).toString();
          
          if ((strValueOfArg0 == null) || "".equals(strValueOfArg0.trim())) {
              throw new javax.xml.transform.TransformerException("FORG0006 : The first argument to function "
                                                                                                          + "fn:round-half-to-even is empty.", srcLocator);
          }
          
          int precision = 0;
          
          if (m_arg1 != null) {
        	  try {
        	     String strValueOfArg1 = (getArgAsString(m_arg1, xctxt)).toString();        	     
        	     precision = (Integer.valueOf(strValueOfArg1)).intValue();
        	  }
        	  catch (Exception ex) {
        		  throw new javax.xml.transform.TransformerException("FORG0006 : The second argument to function fn:round-half-to-even, "
        		  		                                                            + "is not a valid value for precision which must be "
        		  		                                                            + "an xs:integer value.", srcLocator); 
        	  }
          }
          
          if (!((m_arg0 instanceof XSNumericType) || (m_arg0 instanceof XNumber))) {
        	  m_arg0 = m_arg0.execute(xctxt); 
          }
          
          if (m_arg0 instanceof XSFloat) {
        	  XSFloat arg0XsFloat = (XSFloat)m_arg0;
        	  
        	  if (arg0XsFloat.nan() || arg0XsFloat.zero() || arg0XsFloat.negativeZero() || arg0XsFloat.infinite()){
        		  result = arg0XsFloat; 
        	  }
        	  else {
        		  BigDecimal arg0Decimal = new BigDecimal(arg0XsFloat.stringValue());        		  
        		  BigDecimal roundValue = arg0Decimal.setScale(precision, BigDecimal.ROUND_HALF_EVEN);
        		  
        		  result = new XSFloat(roundValue.floatValue());
        	  }
          }
          else if (m_arg0 instanceof XSDouble) {
        	  XSDouble arg0XsDouble = (XSDouble)m_arg0;
        	  
        	  if (arg0XsDouble.nan() || arg0XsDouble.zero() || arg0XsDouble.negativeZero() || arg0XsDouble.infinite()) {
        		 result = arg0XsDouble;  
        	  }
        	  else {
        		  BigDecimal arg0Decimal = new BigDecimal(arg0XsDouble.stringValue());        		  
        		  BigDecimal roundValue = arg0Decimal.setScale(precision, BigDecimal.ROUND_HALF_EVEN);
        		  
        		  result = new XSDouble(roundValue.floatValue()); 
        	  }
          }
          else if (m_arg0 instanceof XSDecimal) {
        	  XSDecimal arg0XsDecimal = (XSDecimal)m_arg0;        	  
        	  
        	  BigDecimal arg0Decimal = new BigDecimal(arg0XsDecimal.stringValue());
        	  BigDecimal roundValue = arg0Decimal.setScale(precision, BigDecimal.ROUND_HALF_EVEN);
    		  
    		  result = new XSDecimal(roundValue);
          }
          else if (m_arg0 instanceof XSInteger) {
        	  XSInteger arg0XsInteger = (XSInteger)m_arg0;        	  
        	  
        	  BigDecimal arg0Decimal = new BigDecimal(arg0XsInteger.stringValue());
        	  BigDecimal roundValue = arg0Decimal.setScale(precision, BigDecimal.ROUND_HALF_EVEN);
    		  
    		  result = new XSInteger(roundValue.toBigInteger());
          }
          else if (m_arg0 instanceof XSNumericType) {
        	  String arg0StrValue = ((XSNumericType)m_arg0).stringValue();
        	  XSDecimal arg0XsDecimal = new XSDecimal(arg0StrValue);
        	  
        	  BigDecimal arg0Decimal = new BigDecimal(arg0XsDecimal.stringValue());
        	  BigDecimal roundValue = arg0Decimal.setScale(precision, BigDecimal.ROUND_HALF_EVEN);
    		  
    		  result = new XSDecimal(roundValue);
          }
          else if (m_arg0 instanceof XNumber) {
        	  XNumber xNumber = (XNumber)m_arg0;        	  
        	  
        	  XSDecimal xsDecimal = xNumber.getXsDecimal();
        	  
        	  BigDecimal arg0Decimal = null;
        	  if (xsDecimal != null) {
        		 arg0Decimal = xsDecimal.getValue(); 
        	  }
        	  else {
        	     arg0Decimal = new BigDecimal(xNumber.num());
        	  }
        	  
        	  BigDecimal roundValue = arg0Decimal.setScale(precision, BigDecimal.ROUND_HALF_EVEN);       	  
    		  
    		  result = new XSDecimal(roundValue);    		  
          }
          else {
        	  throw new javax.xml.transform.TransformerException("FORG0006 : The first argument to function fn:round-half-to-even "
        	  		                                                                                                       + "is not numeric.", srcLocator);
          }
          
          return result;
      }
      
      /**
       * Check that the number of arguments passed to this function is correct.
       *
       * @param argNum The number of arguments that is being passed to the function.
       *
       * @throws WrongNumberArgsException
       */
      public void checkNumberArgs(int argNum) throws WrongNumberArgsException
      {
         if (argNum > 2) {
            reportWrongNumberArgs();
         }
      }

      /**
       * Constructs and throws a WrongNumberArgException with the appropriate
       * message for this function object.
       *
       * @throws WrongNumberArgsException
       */
      protected void reportWrongNumberArgs() throws WrongNumberArgsException {
          throw new WrongNumberArgsException(XSLMessages.createXPATHMessage(
                                                  XPATHErrorResources.ER_ONE_OR_TWO, null)); //"1 or 2"
      }
      
      /**
       * Execute the first argument expression that is expected to return a
       * string. If the argument is null, then get the string value from the
       * current context node.
       *
       * @param expr    								Function argument's XPath expression object
       * @param xctxt   								Runtime XPath context
       *
       * @return The string value of the first argument, or the string value of the
       *         current context node if the first argument is null.
       *
       * @throws javax.xml.transform.TransformerException       If an error occurs while
       *                                                        evaluating the argument expression.
       */
      private XMLString getArgAsString(Expression expr, XPathContext xctxt) throws javax.xml.transform.TransformerException {
            
          XMLString resultVal = null;
              
          if (expr == null) {
             int currentNode = xctxt.getCurrentNode();
             if (currentNode == DTM.NULL) {
                resultVal = XString.EMPTYSTRING;
             }
             else {
                DTM dtm = xctxt.getDTM(currentNode);
                resultVal = dtm.getStringValue(currentNode);
             }      
          }
          else if (expr instanceof SelfIteratorNoPredicate) {
             XObject xpath3ContextItem = xctxt.getXPath3ContextItem();
             if (xpath3ContextItem != null) {
                resultVal = new XString(XslTransformEvaluationHelper.getStrVal(xpath3ContextItem));
             }
             else {
                XObject arg0XObject = expr.execute(xctxt);
                   
                resultVal = new XString(XslTransformEvaluationHelper.getStrVal(arg0XObject));
             }
          }
          else {
             XObject arg0XObject = expr.execute(xctxt);
                
             resultVal = new XString(XslTransformEvaluationHelper.getStrVal(arg0XObject));  
          }
            
          return resultVal;
      }
}
