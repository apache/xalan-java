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

import java.text.DecimalFormat;

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

/**
 * Implementation of the round() function.
 * 
 * @xsl.usage advanced
 */
public class FuncRound extends Function2Args
{
      static final long serialVersionUID = -7970583902573826611L;
    
      /**
       * Execute the function. The function must return a valid object.
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
                                                                                                          + "fn:round() is empty.", srcLocator);
          }
          
          if (m_arg1 == null) {
             try {
                result = new XNumber(Math.round(Double.valueOf(strValueOfArg0)));
                
                return result;
             }
             catch (Exception ex) {
                throw new javax.xml.transform.TransformerException("FORG0006 : An error occured, while evaluating one argument "
                                                                                                               + "version of function fn:round(). "
                                                                                                               + "Please verify the function call with respect to function signature.", 
                                                                                                                       srcLocator); 
             }
          }
          else {
              try {
                  String strValueOfArg1 = (getArgAsString(m_arg1, xctxt)).toString();
    
                  int arg1AsInt = (Integer.valueOf(strValueOfArg1)).intValue();
                     
                  if (arg1AsInt >= 0) {
                     DecimalFormat decimalFormat = new DecimalFormat("#." + getStrForZeros(arg1AsInt));
                     double valAfterRounding = (Double.valueOf(decimalFormat.format(Double.valueOf(
                                                                                                    strValueOfArg0)))).doubleValue();
                     result = new XNumber(valAfterRounding);
                  }
                  else {
                     throw new javax.xml.transform.TransformerException("FORG0006 : A negative integer value of second argument to "
                                                                                                                     + "function fn:round() is not supported.", 
                                                                                                                     srcLocator); 
                  }
              }
              catch (Exception ex) {
                  throw new javax.xml.transform.TransformerException("FORG0006 : An error occured, while evaluating two argument "
                                                                                                 + "version of function fn:round(). Please verify the function call "
                                                                                                 + "with respect to function signature.", srcLocator);
              }
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
       * @param expr    Function argument's XPath expression object
       * @param xctxt   Runtime XPath context
       *
       * @return The string value of the first argument, or the string value of the
       *         current context node if the first argument is null.
       *
       * @throws javax.xml.transform.TransformerException if an error occurs while
       *                                   executing the argument expression.
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
      
      /*
       * Given a non-negative integer value, return a string comprising those many 
       * characters '0'. We use the string value returned by this method, to construct 
       * a java.text.DecimalFormat object instance.
       */
      private String getStrForZeros(int strSize) {
         String strVal = "";
         
         for (int idx = 0; idx < strSize; idx++) {
            strVal = strVal + "0";  
         }
         
         return strVal;
      }
}
