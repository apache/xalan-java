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

import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSFloat;
import xml.xpath31.processor.types.XSInteger;
import xml.xpath31.processor.types.XSNumericType;

/**
 * An XPath 3.1 unary minus operator expression evaluator.
 */
public class Neg extends XPath3UnaryOperator
{
  
  private static final long serialVersionUID = -6280607702375702291L;

  /**
   * Apply XPath unary operator to an operand, and return the result.
   *
   * @param right non-null reference to the evaluated right operand.
   *
   * @return non-null reference to the XObject that represents the result of the operation.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject operate(XObject right) throws javax.xml.transform.TransformerException
  {
    
	  XObject result = null;

	  if (right instanceof XSNumericType) {
		  java.lang.String str1 = ((XSNumericType)right).stringValue();
		  
		  if (right instanceof XSInteger) {
			  BigInteger bigInt = new BigInteger(str1);
			  bigInt = bigInt.multiply(BigInteger.valueOf(-1));

			  result = new XSInteger(bigInt);
		  }
		  else if (right instanceof XSDecimal) {
			  BigDecimal bigdecimal = new BigDecimal(str1);
			  bigdecimal = bigdecimal.multiply(BigDecimal.valueOf(-1));
			  
			  result = new XSDecimal(bigdecimal); 
		  }
		  else if (right instanceof XSDouble) {
			  Double dbl1 = ((XSDouble)right).doubleValue();
			  
			  if (dbl1 == Double.POSITIVE_INFINITY) {
				  result = new XSDouble(Double.NEGATIVE_INFINITY); 
			  }
			  else if (dbl1 == Double.NEGATIVE_INFINITY) {
				  result = new XSDouble(Double.POSITIVE_INFINITY); 
			  }
			  else if (!dbl1.isNaN()) {
				  result = new XSDouble(dbl1 * -1);
			  }
			  else {
				  result = new XSDouble(Double.NaN);
			  }
		  }
          else if (right instanceof XSFloat) {
        	  Float flt1 = ((XSFloat)right).floatValue();

        	  if (flt1 == Float.POSITIVE_INFINITY) {
        		  result = new XSFloat(Float.NEGATIVE_INFINITY); 
        	  }
        	  else if (flt1 == Float.NEGATIVE_INFINITY) {
        		  result = new XSFloat(Float.POSITIVE_INFINITY); 
        	  }
        	  else if (!flt1.isNaN()) {
        		  result = new XSFloat(flt1 * -1);
        	  }
        	  else {
        		  result = new XSFloat(Float.NaN);
        	  }
		  }
	  }
	  else {
		  XNumber xNumber = (XNumber)right;
		  
		  XNumber xNumNew = new XNumber(xNumber.num() * -1);

		  if (xNumber.getXsDecimal() != null) {
			  XSDecimal xsDecimal = xNumber.getXsDecimal();

			  BigDecimal bigdecimal = xsDecimal.getValue();    	  
			  bigdecimal = bigdecimal.multiply(BigDecimal.valueOf(-1));

			  xsDecimal = new XSDecimal(bigdecimal);    	  
			  xNumNew.setXsDecimal(xsDecimal);
		  }
		  else if (xNumber.getXsDouble() != null) {
			  XSDouble xsDouble = xNumber.getXsDouble();

			  double dbl1 = xsDouble.doubleValue() * -1;

			  xsDouble = new XSDouble(dbl1);
			  xNumNew.setXsDouble(xsDouble);
		  }
		  else if (xNumber.getXsInteger() != null) {
			  XSInteger xsInteger = xNumber.getXsInteger();

			  BigInteger bigInt = xsInteger.intValue();
			  bigInt = bigInt.multiply(BigInteger.valueOf(-1));

			  xsInteger = new XSInteger(bigInt);
			  xNumNew.setXsInteger(xsInteger);
		  }

		  result = xNumNew;  
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

    return -(m_right.num(xctxt));
  }

}
