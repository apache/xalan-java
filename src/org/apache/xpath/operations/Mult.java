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

import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathException;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.xs.types.XSDouble;
import org.apache.xpath.xs.types.XSNumericType;
import org.apache.xpath.xs.types.XSYearMonthDuration;

/**
 * The '*' operation expression executer.
 */
public class Mult extends Operation
{
    static final long serialVersionUID = -4956770147013414675L;

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
                                           throws javax.xml.transform.TransformerException
  {
      XObject result = null;
      
      if ((left instanceof XNumber) && (right instanceof XSNumericType)) {
          double lDouble = ((XNumber)left).num();
          
          java.lang.String rStrVal = ((XSNumericType)right).stringValue();
          double rDouble = (Double.valueOf(rStrVal)).doubleValue();
          
          result = new XNumber(lDouble * rDouble);
      }
      else if ((left instanceof XSNumericType) && (right instanceof XNumber)) {
          java.lang.String lStrVal = ((XSNumericType)left).stringValue();
          double lDouble = (Double.valueOf(lStrVal)).doubleValue();
          
          double rDouble = ((XNumber)right).num();
          
          result = new XNumber(lDouble * rDouble);
      }
      else if ((left instanceof XNumber) && (right instanceof XNumber)) {
          double lDouble = ((XNumber)left).num();
          double rDouble = ((XNumber)right).num();
          
          result = new XNumber(lDouble * rDouble);
      }
      else if ((left instanceof XSNumericType) && (right instanceof XSNumericType)) {
          java.lang.String lStrVal = ((XSNumericType)left).stringValue();
          double lDouble = (Double.valueOf(lStrVal)).doubleValue();
          
          java.lang.String rStrVal = ((XSNumericType)right).stringValue();
          double rDouble = (Double.valueOf(rStrVal)).doubleValue();
          
          result = new XNumber(lDouble * rDouble);
      }
      else if ((left instanceof XNumber) && (right instanceof XNodeSet)) {
          double lDouble = ((XNumber)left).num();
          
          XNodeSet rNodeSet = (XNodeSet)right;
          if (rNodeSet.getLength() > 1) {
             throw new javax.xml.transform.TransformerException("XPTY0004 : a sequence of more "
                                                                                   + "than one item is not allowed as the second "
                                                                                   + "operand of multiplication operator '*'.");  
          }
          else {
             java.lang.String rStrVal = rNodeSet.str();
             double rDouble = (Double.valueOf(rStrVal)).doubleValue();
             
             result = new XNumber(lDouble * rDouble);
          }
      }
      else if ((left instanceof XNodeSet) && (right instanceof XNumber)) {
          double rDouble = ((XNumber)right).num();
          
          XNodeSet lNodeSet = (XNodeSet)left;
          if (lNodeSet.getLength() > 1) {
             throw new javax.xml.transform.TransformerException("XPTY0004 : a sequence of more "
                                                                                   + "than one item is not allowed as the first "
                                                                                   + "operand of multiplication operator '*'.");  
          }
          else {
             java.lang.String lStrVal = lNodeSet.str();
             double lDouble = (Double.valueOf(lStrVal)).doubleValue();
             
             result = new XNumber(lDouble * rDouble);
          }
      }
      else if ((left instanceof XSNumericType) && (right instanceof XNodeSet)) {
          java.lang.String lStrVal = ((XSNumericType)left).stringValue();
          double lDouble = (Double.valueOf(lStrVal)).doubleValue();
          
          XNodeSet rNodeSet = (XNodeSet)right;
          if (rNodeSet.getLength() > 1) {
             throw new javax.xml.transform.TransformerException("XPTY0004 : a sequence of more "
                                                                                   + "than one item is not allowed as the second "
                                                                                   + "operand of multiplication operator '*'.");  
          }
          else {
             java.lang.String rStrVal = rNodeSet.str();
             double rDouble = (Double.valueOf(rStrVal)).doubleValue();
             
             result = new XNumber(lDouble * rDouble);
          }
      }
      else if ((left instanceof XNodeSet) && (right instanceof XSNumericType)) {
          java.lang.String rStrVal = ((XSNumericType)right).stringValue();
          double rDouble = (Double.valueOf(rStrVal)).doubleValue();
          
          XNodeSet lNodeSet = (XNodeSet)left;
          if (lNodeSet.getLength() > 1) {
             throw new javax.xml.transform.TransformerException("XPTY0004 : a sequence of more "
                                                                                   + "than one item is not allowed as the first "
                                                                                   + "operand of multiplication operator '*'.");  
          }
          else {
             java.lang.String lStrVal = lNodeSet.str();
             double lDouble = (Double.valueOf(lStrVal)).doubleValue();
             
             result = new XNumber(lDouble * rDouble);
          }
      }
      else if ((left instanceof XNodeSet) && (right instanceof XNodeSet)) {
          double lDouble = 0.0d;
          double rDouble = 0.0d;
          
          XNodeSet lNodeSet = (XNodeSet)left;
          if (lNodeSet.getLength() > 1) {
             throw new javax.xml.transform.TransformerException("XPTY0004 : a sequence of more "
                                                                                   + "than one item is not allowed as the first "
                                                                                   + "operand of multiplication operator '*'.");  
          }
          else {
             java.lang.String lStrVal = lNodeSet.str();
             lDouble = (Double.valueOf(lStrVal)).doubleValue();
          }
          
          XNodeSet rNodeSet = (XNodeSet)right;
          if (rNodeSet.getLength() > 1) {
             throw new javax.xml.transform.TransformerException("XPTY0004 : a sequence of more "
                                                                                   + "than one item is not allowed as the second "
                                                                                   + "operand of multiplication operator '*'.");  
          }
          else {
             java.lang.String rStrVal = rNodeSet.str();
             rDouble = (Double.valueOf(rStrVal)).doubleValue();
          }
          
          result = new XNumber(lDouble * rDouble);
      }
      else if ((left instanceof XSYearMonthDuration) && 
                                                 (right instanceof XNumber)) {
          try {
             double rDouble = ((XNumber)right).num();
             result = ((XSYearMonthDuration)left).mult(new XSDouble(rDouble));
          }
          catch (XPathException ex) {
             throw new javax.xml.transform.TransformerException(ex.getMessage());  
          }
      }
      else if ((left instanceof XSYearMonthDuration) && 
                                                 (right instanceof XSNumericType)) {
          try {
             java.lang.String rStrVal = ((XSNumericType)right).stringValue();
             result = ((XSYearMonthDuration)left).mult(new XSDouble(rStrVal));
          }
          catch (XPathException ex) {
             throw new javax.xml.transform.TransformerException(ex.getMessage());  
          }
      }
      else if ((left instanceof XSYearMonthDuration) && (right instanceof XNodeSet)) {
          XNodeSet rNodeSet = (XNodeSet)right;
          if (rNodeSet.getLength() > 1) {
             throw new javax.xml.transform.TransformerException("XPTY0004 : a sequence of more "
                                                                                 + "than one item is not allowed as the second "
                                                                                 + "operand of multiplication operator '*'.");  
          }
          else {
             java.lang.String rStrVal = rNodeSet.str();
             result = ((XSYearMonthDuration)left).mult(new XSDouble(rStrVal));
          }
      }
      else {
          try {
             result = new XNumber(left.num() * right.num());
          }
          catch (Exception ex) {
             throw new javax.xml.transform.TransformerException("XPTY0004 : could not apply the "
                                                                                   + "multiplication operator '*', due to incorrectly "
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
    return (m_left.num(xctxt) * m_right.num(xctxt));
  }

}
