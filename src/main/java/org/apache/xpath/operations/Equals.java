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
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSBoolean;
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
	  
	  if (left instanceof XPathArray) {
		 left = ((XPathArray)left).atomize();
	  }
	  
	  if (right instanceof XPathArray) {
		 right = ((XPathArray)right).atomize(); 
	  }
	  
	  XObject lObj = null;
	  XObject rObj = null;
	  
	  if ((left instanceof XMLNodeCursorImpl) && ((right instanceof XString) || (right instanceof XSString))) {
		  lObj = left;
		  rObj = right;
	  }
	  else if ((right instanceof XMLNodeCursorImpl) && ((left instanceof XString) || (left instanceof XSString))) {
		  lObj = right;
		  rObj = left;
	  }
	  
	  if (lObj != null) {		  
		  if (rObj instanceof XString) {
			  java.lang.String strL = lObj.str();
			  java.lang.String strR = rObj.str();			
			  if (strL.equals(strR)) {
				  result = XBoolean.S_TRUE; 
			  }
			  else {
				  result = XBoolean.S_FALSE;
			  }

			  return result;
		  }
		  else if (rObj instanceof XSString) {
			  java.lang.String strL = lObj.str();
			  java.lang.String strR = ((XSString)rObj).stringValue();
			  if (strL.equals(strR)) {
				  result = XBoolean.S_TRUE; 
			  }
			  else {
				  result = XBoolean.S_FALSE;
			  }

			  return result;
		  }
      }
	  	  
	  if (right instanceof ResultSequence) {
		 if (left instanceof XNumber) {
			 boolean bool = XPathGeneralComparisonEqualityOpSupport.equals((XNumber)left, (ResultSequence)right, false);
			 result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE); 
		 }
		 else if (left instanceof XSNumericType) {
			 boolean bool = XPathGeneralComparisonEqualityOpSupport.equals((XSNumericType)left, (ResultSequence)right, false);
			 result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE);
		 }
		 else if (left instanceof XSString) {
			 boolean bool = XPathGeneralComparisonEqualityOpSupport.equals((XSString)left, (ResultSequence)right, false);
			 result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE);
		 }
		 else if (left instanceof XString) {
			 boolean bool = XPathGeneralComparisonEqualityOpSupport.equals((XString)left, (ResultSequence)right, false);
			 result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE);
		 }
		 else if (left instanceof ResultSequence) {
			 ResultSequence resultSeqLhs = (ResultSequence)left;
			 ResultSequence resultSeqRhs = (ResultSequence)right;
			 boolean isEqual = false;
			 for (int i = 0; i < resultSeqLhs.size(); i++) {
				 XObject xObj1 = resultSeqLhs.item(i); 
				 for (int j = 0; j < resultSeqRhs.size(); j++) {
					XObject xObj2 = resultSeqRhs.item(j);
					if (xObj2 instanceof ResultSequence) {
					   XObject r1 = operate(xObj1, xObj2);
					   isEqual = r1.bool();
					}					
					else if (xObj1.equals(xObj2)) {
					   isEqual = true;						
					}
					
					if (isEqual) {
					   break;	
					}
				 }
				 
				 if (isEqual) {
					break; 
				 }
			 }
			 
			 result = (isEqual ? XBoolean.S_TRUE : XBoolean.S_FALSE);
		 }
		 else {
			 java.lang.String lStrVal = XslTransformEvaluationHelper.getStrVal(left);			 
			 boolean bool = XPathGeneralComparisonEqualityOpSupport.equals(new XSString(lStrVal), (ResultSequence)right, false);
			 result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE);
		 }
	  }
	  else if ((left instanceof XMLNodeCursorImpl) && (right instanceof XMLNodeCursorImpl)) {
		 result = (left.equals(right) ? XBoolean.S_TRUE : XBoolean.S_FALSE);  
	  }
	  else if (left instanceof XMLNodeCursorImpl) {
		 XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)left;
		 DTMCursorIterator iter1 = xmlNodeCursorImpl.iterRaw();
		 int nextNode;
		 DTMManager dtmManager = iter1.getDTMManager();
		 while ((nextNode = iter1.nextNode()) != DTM.NULL) {
			XMLNodeCursorImpl xmlNodeCursorImpl1 = new XMLNodeCursorImpl(nextNode, dtmManager);
			java.lang.String lStrValue = xmlNodeCursorImpl1.str();
			try {
				if (right instanceof XNumber) {
					double lDbl = Double.valueOf(lStrValue);
					XNumber rXNumber = (XNumber)right;
					double rDbl = rXNumber.num();
					if (lDbl == rDbl) {
						return XBoolean.S_TRUE;
					}
				}
				else if (right instanceof XSNumericType) {
					double lDbl = Double.valueOf(lStrValue);
					XSNumericType xsNumericType = (XSNumericType)right;
					java.lang.String rStrValue = xsNumericType.stringValue();
					double rDbl = Double.valueOf(rStrValue);
					if (lDbl == rDbl) {
					   return XBoolean.S_TRUE;
					}
				}
				else if (right instanceof XSBoolean) {
					java.lang.String rStrValue = ((XSBoolean)right).stringValue();
					if (!"".equals(lStrValue) && ("1".equals(rStrValue) || "true".equals(rStrValue))) {
						return XBoolean.S_TRUE;
					}
					else if ("".equals(lStrValue) && ("0".equals(rStrValue) || "false".equals(rStrValue))) {
						return XBoolean.S_TRUE;
					}
				}
				else if ((right instanceof XBoolean) || (right instanceof XBooleanStatic)) {
					java.lang.String rStrValue = right.str();
					if (!"".equals(lStrValue) && ("1".equals(rStrValue) || "true".equals(rStrValue))) {
						return XBoolean.S_TRUE;
					}
					else if ("".equals(lStrValue) && ("0".equals(rStrValue) || "false".equals(rStrValue))) {
						return XBoolean.S_TRUE;
					}
				}
				else {
					java.lang.String rStrValue = XslTransformEvaluationHelper.getStrVal(right);
					if (lStrValue.equals(rStrValue)) {
					   return XBoolean.S_TRUE;
					}
				}
			}
			catch (NumberFormatException ex) {
				// NO OP
			}
		 }
		 
		 result = XBoolean.S_FALSE;
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
