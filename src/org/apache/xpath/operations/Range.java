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

import java.math.BigInteger;

import javax.xml.XMLConstants;

import org.apache.xalan.templates.XSConstructorFunctionUtil;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FuncExtFunction;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.xml.sax.SAXException;

import xml.xpath31.processor.types.XSInteger;
import xml.xpath31.processor.types.XSNumericType;

/**
 * The XPath 3.1 range "to" operation.
 * 
 * An XPath range "to" expression can be used to construct a sequence of 
 * consecutive integers. Each of the operands of the XPath range "to" 
 * operator is converted as though it was an argument of a function 
 * with the expected parameter type xs:integer.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class Range extends Operation
{
    
   private static final long serialVersionUID = 7722428363208837859L;

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
    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {
        
      ResultSequence result = new ResultSequence();
      
      XObject expr1 = null;
      
      XObject expr2 = null;
      
          if (m_left instanceof FuncExtFunction) {
             FuncExtFunction extFunction = (FuncExtFunction)m_left;
             if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(extFunction.getNamespace())) {
                 expr1 = XSConstructorFunctionUtil.processFuncExtFunctionOrXPathOpn(xctxt, m_left, null); 
             }
             else {
                 expr1 = m_left.execute(xctxt, true);  
             }
          }
          else {
              expr1 = m_left.execute(xctxt, true); 
          }
          
          if (m_right instanceof FuncExtFunction) {
             FuncExtFunction extFunction = (FuncExtFunction)m_right;
             if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(extFunction.getNamespace())) {
                 expr2 = XSConstructorFunctionUtil.processFuncExtFunctionOrXPathOpn(xctxt, m_right, null); 
             }
             else {
                 expr2 = m_right.execute(xctxt, true);  
             }
          }
          else {
              expr2 = m_right.execute(xctxt, true); 
          }
      
      double firstArg = (expr1 instanceof XSNumericType) ?  (Double.valueOf((
                                                                       (XSNumericType)expr1).stringValue())).doubleValue() : expr1.num();  
      double secondArg = (expr2 instanceof XSNumericType) ?  (Double.valueOf((
                                                                       (XSNumericType)expr2).stringValue())).doubleValue() : expr2.num();
              
      if (firstArg > (long)firstArg) {
         throw new javax.xml.transform.TransformerException("XPTY0004 : The required item type of the first operand of "
                                                     + "'to' is an xs:integer. The supplied value is of type xs:double.", 
                                                                                                                   xctxt.getSAXLocator());  
      }
      
      if (secondArg > (long)secondArg) {
         throw new javax.xml.transform.TransformerException("XPTY0004 : The required item type of the second operand of "
                                                     + "'to' is an xs:integer. The supplied value is of type xs:double.", 
                                                                                                                   xctxt.getSAXLocator());  
      }
      
      long fromIdx = (long)firstArg;
      long toIdx = (long)secondArg;
      
      for (long idx = fromIdx; idx <= toIdx; idx++) {
         result.add(new XSInteger(BigInteger.valueOf(idx)));    
      }
      
      return result;      
    }

}
