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
package org.apache.xpath.functions.math;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionOneArg;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSNumericType;

/**
 * Implementation of the math:atan() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncMathAtan extends FunctionOneArg
{

    private static final long serialVersionUID = 7279664663962212944L;

    /**
     * Execute the function. The function must return a valid object.
     * 
     * @param xctxt The current execution context.
     * @return A valid XObject.
     *
     * @throws javax.xml.transform.TransformerException
     */
    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
    {
       XObject result = null;
       
       SourceLocator srcLocator = xctxt.getSAXLocator();
       
       Expression arg0 = getArg0();
       
       if (arg0 == null || isArgCountErr()) {
          ResultSequence resultSeq = new ResultSequence();
          return resultSeq;
       }
          
       XObject arg0Result = getEffectiveFuncArgValue(arg0, xctxt);
       
       if (arg0Result instanceof XNumber) {
          double resultVal = Math.atan(((XNumber)arg0Result).num());
          result = new XSDouble(resultVal);
       }
       else if (arg0Result instanceof XSNumericType) {
          String strVal = ((XSNumericType)arg0Result).stringValue();
          double resultVal = Math.atan((new XSDouble(strVal)).doubleValue());
          result = new XSDouble(resultVal);
       }
       else if (arg0Result instanceof XMLNodeCursorImpl) {
          XMLNodeCursorImpl xNodeSet = (XMLNodeCursorImpl)arg0Result;
          if (xNodeSet.getLength() != 1) {
             throw new javax.xml.transform.TransformerException("XPTY0004 : The argument to math:atan "
                                                                     + "function must be a sequence of length one.", srcLocator);    
          }
          else {
             String strVal = xNodeSet.str();
             
             double arg = 0.0;             
             try {
                arg = (new XSDouble(strVal)).doubleValue();
             }
             catch (Exception ex) {
                throw new javax.xml.transform.TransformerException("FORG0001 : Cannot convert the string \"" + strVal + "\" to "
                                                                                                       + "a double value.", srcLocator);
             }
             
             result = new XSDouble(Math.atan(arg));
          }
       }
       else if (arg0Result instanceof ResultSequence) {
           ResultSequence resultSeq = (ResultSequence)arg0Result;
           if (resultSeq.size() != 1) {
              throw new javax.xml.transform.TransformerException("XPTY0004 : The argument to math:atan "
                                                                      + "function must be a sequence of length one.", srcLocator);    
           }
           else {
              XObject val = resultSeq.item(0);
              String strVal = XslTransformEvaluationHelper.getStrVal(val);
              
              double arg = 0.0;             
              try {
                 arg = (new XSDouble(strVal)).doubleValue();
              }
              catch (Exception ex) {
                 throw new javax.xml.transform.TransformerException("FORG0001 : Cannot convert the string \"" + strVal + "\" to "
                                                                                                        + "a double value.", srcLocator);
              }
              
              result = new XSDouble(Math.atan(arg));
           }
       }
       else {
           throw new javax.xml.transform.TransformerException("XPTY0004 : The item type of first argument to function math:atan is not "
                                                                                                      + "xs:double.", srcLocator); 
       }
       
       return result;
    }
}
