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
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;

/**
 * The '=' operation expression executer.
 */
public class Equals extends Operation
{
    static final long serialVersionUID = -2658315633903426134L;

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
	  	  
	  if (right instanceof ResultSequence) {
		 if (left instanceof XNumber) {
			 XNumber lNum = (XNumber)left;
			 boolean bool = XPathGeneralComparisonSupport.equals(lNum, (ResultSequence)right);
			 result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE); 
		 }
		 else if (left instanceof XSNumericType) {
			 boolean bool = XPathGeneralComparisonSupport.equals((XSNumericType)left, (ResultSequence)right);
			 result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE);
		 }
		 else if (left instanceof XSString) {
			 boolean bool = XPathGeneralComparisonSupport.equals((XSString)left, (ResultSequence)right);
			 result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE);
		 }
		 else if (left instanceof XString) {
			 boolean bool = XPathGeneralComparisonSupport.equals((XString)left, (ResultSequence)right);
			 result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE);
		 }
		 else {
			 XSString xsStrLeft = new XSString(left.str());
			 boolean bool = XPathGeneralComparisonSupport.equals(xsStrLeft, (ResultSequence)right);
			 result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE);
		 }
	  }
	  else {
		 result = (left.equals(right) ? XBoolean.S_TRUE : XBoolean.S_FALSE);
	  }
	  
	  return result;
  }
  
  /**
   * Execute a binary operation by calling execute on each of the operands,
   * and then calling the operate method on the derived class.
   *
   *
   * @param xctxt The runtime execution context.
   *
   * @return The XObject result of the operation.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean bool(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {
    XObject left = m_left.execute(xctxt, true);
    XObject right = m_right.execute(xctxt, true);

    boolean result = left.equals(right) ? true : false;
	left.detach();
	right.detach();
    return result;
  }

}
