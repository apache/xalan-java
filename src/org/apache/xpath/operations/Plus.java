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

import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.xs.types.XSDecimal;
import org.apache.xpath.xs.types.XSFloat;
import org.apache.xpath.xs.types.XSInt;
import org.apache.xpath.xs.types.XSInteger;
import org.apache.xpath.xs.types.XSLong;
import org.apache.xpath.xs.types.XSYearMonthDuration;

/**
 * The '+' operation expression executer.
 */
public class Plus extends Operation
{
    static final long serialVersionUID = -4492072861616504256L;

  /**
   * Apply the operation to two operands, and return the result.
   *
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
      
      if ((left instanceof XSInteger) && (right instanceof XSInteger)) {
         BigInteger bigintLeft = ((XSInteger)left).intValue();
         BigInteger bigintRight = ((XSInteger)right).intValue();
         
         return new XSInteger(bigintLeft.add(bigintRight));    
      }      
      
      if ((left instanceof XSLong) && (right instanceof XSLong)) {
          BigInteger bigintLeft = ((XSLong)left).intValue();
          BigInteger bigintRight = ((XSLong)right).intValue();
          
          // its possible that, result after addition is, not within value space of xs:long.
          // handle this error. revisit
          return new XSLong(bigintLeft.add(bigintRight));    
      }      
      
      if ((left instanceof XSInt) && (right instanceof XSInt)) {
          BigInteger bigintLeft = ((XSInt)left).intValue();
          BigInteger bigintRight = ((XSInt)right).intValue();
          
          // its possible that, result after addition is, not within value space of xs:int.
          // handle this error. revisit
          return new XSInt(bigintLeft.add(bigintRight));    
      }
      
      if ((left instanceof XSYearMonthDuration) && 
                                         (right instanceof XSYearMonthDuration)) {
          return ((XSYearMonthDuration)left).add((XSYearMonthDuration)right);  
      }
      
      double leftArg = 0.0;
      double rightArg = 0.0;
      
      float lArg = (float)0.0;
      float rArg = (float)0.0;
      
      boolean isFloatLarg = false;
      boolean isFloatRarg = false;
      
      if (left instanceof XSDecimal) {
          leftArg = ((XSDecimal)left).doubleValue();    
      }
      else if (left instanceof XSFloat) {
          isFloatLarg = true;
          lArg = ((XSFloat)left).floatValue();    
      }
      else {
          leftArg = left.num();  
      }
      
      if (right instanceof XSDecimal) {
          rightArg = ((XSDecimal)right).doubleValue();    
      }
      else if (right instanceof XSFloat) {
          isFloatRarg = true;
          rArg = ((XSFloat)right).floatValue();    
      }
      else {
          rightArg = right.num();  
      }
      
      if (isFloatLarg && isFloatRarg) {
         // currently, supporting XSFloat typed result, when both operands 
         // are of type XSFloat.  
         result = new XSFloat(lArg + rArg);
         
         return result;
      }
      
      result = new XNumber(leftArg + rightArg);
      
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

}
