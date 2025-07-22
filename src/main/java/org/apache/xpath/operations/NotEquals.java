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
package org.apache.xpath.operations;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;

/**
 * The '!=' operation expression executer.
 */
public class NotEquals extends Operation
{
    static final long serialVersionUID = -7869072863070586900L;

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
			  boolean bool = XPathGeneralComparisonEqualityOpSupport.equals((XNumber)left, (ResultSequence)right, true);
			  result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE); 
		  }
		  else if (left instanceof XSNumericType) {
			  boolean bool = XPathGeneralComparisonEqualityOpSupport.equals((XSNumericType)left, (ResultSequence)right, true);
			  result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE);
		  }
		  else if (left instanceof XSString) {
			  boolean bool = XPathGeneralComparisonEqualityOpSupport.equals((XSString)left, (ResultSequence)right, true);
			  result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE);
		  }
		  else if (left instanceof XString) {
			  boolean bool = XPathGeneralComparisonEqualityOpSupport.equals((XString)left, (ResultSequence)right, true);
			  result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE);
		  }
		  else if (left instanceof ResultSequence) {
			  ResultSequence resultSeqLhs = (ResultSequence)left;
			  ResultSequence resultSeqRhs = (ResultSequence)right;
			  boolean isResult = false;
			  for (int i = 0; i < resultSeqLhs.size(); i++) {
				  XObject xObj1 = resultSeqLhs.item(i); 
				  for (int j = 0; j < resultSeqRhs.size(); j++) {
					  XObject xObj2 = resultSeqRhs.item(j);
					  if (!xObj1.equals(xObj2)) {
						  isResult = true;
						  break;
					  }
				  }

				  if (isResult) {
					  break; 
				  }
			  }

			  result = (isResult ? XBoolean.S_TRUE : XBoolean.S_FALSE);
		  }
		  else {
			  java.lang.String lStrVal = XslTransformEvaluationHelper.getStrVal(left);
			  boolean bool = XPathGeneralComparisonEqualityOpSupport.equals(new XSString(lStrVal), (ResultSequence)right, true);
			  result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE);
		  }
	  }
	  else {
		  result = (left.notEquals(right)) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
	  }
      
	  return result;
  }
}
