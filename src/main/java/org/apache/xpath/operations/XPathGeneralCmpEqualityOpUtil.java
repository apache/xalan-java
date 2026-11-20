/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xpath.operations;

import java.math.BigDecimal;

import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSUntypedAtomic;

/**
 * A class definition, providing utility methods, for 
 * XPath general comparison operator =, != evaluation.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 */
public class XPathGeneralCmpEqualityOpUtil {
	
	
	/**
	 * Method definition, to evaluate XPath operators =, !=, when first 
     * operand for these operators is a value of type XSNumericType, and 
     * the second operator is an xdm sequence.
	 * 
	 * @param obj1                               An XPath operator's first operand
	 * @param rSeq                               An XPath operator's second operand
	 * @param notEquals                          Boolean value, to denote whether this
	 *                                           is an equals, or a not equals check.
	 * @return                                   Boolean value, true or false
	 * @throws TransformerException
	 */
	public static boolean equals(XSNumericType obj1, ResultSequence rSeq, boolean notEquals) throws TransformerException {	   
	   
		boolean result = false; 
		
		BigDecimal bigDecimal1 = new BigDecimal(obj1.stringValue());
		BigDecimal bigDecimal2 = null;
		
		int size1 = rSeq.size();
		
		for (int idx = 0; idx < size1; idx++) {
			XObject xObj2 = rSeq.item(idx);			

			if (xObj2 instanceof XNumber) {
				bigDecimal2 = getBigdecimalFromXNumber((XNumber)xObj2); 

				result = !notEquals ? (bigDecimal1.compareTo(bigDecimal2) == 0) : 
					                                                           (bigDecimal1.compareTo(bigDecimal2) != 0);								
			}
			else if (xObj2 instanceof XSNumericType) {
				java.lang.String str2 = ((XSNumericType)xObj2).stringValue();

				bigDecimal2 = new BigDecimal(str2);

				result = !notEquals ? (bigDecimal1.compareTo(bigDecimal2) == 0) : 
					                                                           (bigDecimal1.compareTo(bigDecimal2) != 0);				
			}
			else if ((xObj2 instanceof XMLNodeCursorImpl) || (xObj2 instanceof XSUntypedAtomic)) {
				java.lang.String str2 = XslTransformEvaluationHelper.getStrVal(xObj2);

				try {
					bigDecimal2 = new BigDecimal(str2);
				}
				catch (NumberFormatException ex) {
					throw new TransformerException("XPTY0004 : An XPath 3.1 operator =, or != second operand cannot be transformed to a number.");
				}

				result = !notEquals ? (bigDecimal1.compareTo(bigDecimal2) == 0) : 
					                                                           (bigDecimal1.compareTo(bigDecimal2) != 0);
			}

			if (result) {
				break; 
			}
		}

		return result;
	}
	
	/**
	 * Method definition, to evaluate XPath operators =, !=, when first 
     * operand for these operators is a value of type XNumber, and 
     * the second operator is an xdm sequence.
	 * 
	 * @param xNum                               An XPath operator's first operand
	 * @param rSeq                               An XPath operator's second operand
	 * @param notEquals                          Boolean value, to denote whether this
	 *                                           is an equals, or a not equals check.
	 * @return                                   Boolean value, true or false
	 * @throws TransformerException
	 */
	public static boolean equals(XNumber xNum, ResultSequence rSeq, boolean notEquals) throws TransformerException {
		
		boolean result = false; 
		
		BigDecimal bigDecimal1 = getBigdecimalFromXNumber(xNum);
		
		int size1 = rSeq.size();
		
		for (int idx = 0; idx < size1; idx++) {
			XObject xObj2 = rSeq.item(idx);
			
			BigDecimal bigDecimal2 = null;
			
			if (xObj2 instanceof XNumber) {
				bigDecimal2 = getBigdecimalFromXNumber((XNumber)xObj2); 
				
				result = !notEquals ? (bigDecimal1.compareTo(bigDecimal2) == 0) : (bigDecimal1.compareTo(bigDecimal2) != 0);								
			}
			else if (xObj2 instanceof XSNumericType) {
				java.lang.String str2 = ((XSNumericType)xObj2).stringValue();
				
				bigDecimal2 = new BigDecimal(str2);
				
				result = !notEquals ? (bigDecimal1.compareTo(bigDecimal2) == 0) : (bigDecimal1.compareTo(bigDecimal2) != 0);
			}
			else if ((xObj2 instanceof XMLNodeCursorImpl) || (xObj2 instanceof XSUntypedAtomic)) {
				java.lang.String str2 = XslTransformEvaluationHelper.getStrVal(xObj2);
				
				try {
				   bigDecimal2 = new BigDecimal(str2);
				}
				catch (NumberFormatException ex) {
				   throw new TransformerException("XPTY0004 : An XPath 3.1 operator =, or != second operand is "
				   		                                                                    + "a node whose string value "
				   		                                                                    + "is not castable to a number.");
				}
				
				result = !notEquals ? (bigDecimal1.compareTo(bigDecimal2) == 0) : (bigDecimal1.compareTo(bigDecimal2) != 0);
			}
			
			if (result) {
			   break;	
			}
		}

		return result; 
	}
	
	/**
	 * Method definition, to evaluate XPath operators =, !=, 
     * when first operand for these operators is value with 
     * type XSString, and second operand is an xdm sequence.
     * 
	 * @param obj1                              An XPath operator's first
	 *                                          operand.
	 * @param rSeq                              An XPath operator's second
	 *                                          operand.
	 * @param notEquals                         A boolean value, indicating whether
	 *                                          this method does XPath operator evaluation
	 *                                          '=', or '!='.
	 * @return
	 * @throws TransformerException
	 */
	public static boolean equals(XSString obj1, ResultSequence rSeq, boolean notEquals) throws TransformerException {
		
		boolean result = false;

		java.lang.String lStrVal = obj1.stringValue();
		
		for (int idx = 0; idx < rSeq.size(); idx++) {
			XObject xObj1 = rSeq.item(idx);
			
			java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj1);
			
			boolean result1 = !notEquals ? lStrVal.equals(str1) : !lStrVal.equals(str1);
			
			if (result1) {
				result = true;
				
				break; 
			}
		}

		return result;
    }
	
	/**
	 * Method definition, to evaluate XPath operators =, !=, 
     * when first operand for these operators is value with 
     * type XString, and second operand is an xdm sequence.
     * 
	 * @param obj1                              An XPath operator's first
	 *                                          operand.
	 * @param rSeq                              An XPath operator's second
	 *                                          operand.
	 * @param notEquals                         A boolean value, indicating whether
	 *                                          this method does XPath operator evaluation
	 *                                          '=', or '!='.
	 * @return
	 * @throws TransformerException
	 */
	public static boolean equals(XString obj1, ResultSequence rSeq, boolean notEquals) throws TransformerException {
		
		boolean result = false;

		java.lang.String lStrVal = obj1.str();
		
		for (int idx = 0; idx < rSeq.size(); idx++) {
			XObject xObj1 = rSeq.item(idx);
			
			java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj1);
			
			boolean result1 = !notEquals ? lStrVal.equals(str1) : !lStrVal.equals(str1);
			
			if (result1) {
				result = true;
				
				break; 
			}
		}

		return result;
    }
	
	/**
	 * Method definition, to evaluate XPath operators =, !=, when 
     * XPath operator's first and second operands are xdm sequences.
     * 
	 * @param rSeq1                                   XPath operator first operand
	 * @param rSeq2                                   XPath operator second operand
	 * @param notEquals                               When boolean true, this method
	 *                                                implements != comparison, otherwise
	 *                                                = comparison.
	 * @return                                        Boolean value true or false 
	 * @throws TransformerException
	 */
    public static boolean equals(ResultSequence rSeq1, ResultSequence rSeq2, boolean notEquals) throws TransformerException {
		
		boolean result = false;
		
		int size1 = rSeq1.size();
		
		int size2 = rSeq2.size();
		
		// Check, possible xdm string & untypedAtomic value comparisons
		
		boolean isIncompatibleTypes = false;
		
		for (int idx = 0; idx < size1; idx++) {
			XObject xObj1 = rSeq1.item(idx);
			for (int idx2 = 0; idx2 < size2; idx2++) {
				XObject xObj2 = rSeq2.item(idx2);
				
				if ((xObj1 instanceof XSString || xObj1 instanceof XString) &&
					                                                       (xObj2 instanceof XSString || xObj2 instanceof XString)) {
					java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj1);
					java.lang.String str2 = XslTransformEvaluationHelper.getStrVal(xObj2);
					
					int cmp1 = str1.compareTo(str2);
					
					if (!notEquals && (cmp1 == 0)) {
					   result = true;
					}
					else if (notEquals && (cmp1 != 0)) {
					   result = true;
					}
				}
				else if ((xObj1 instanceof XSUntypedAtomic) && (xObj2 instanceof XSUntypedAtomic)) {
					java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj1);
					java.lang.String str2 = XslTransformEvaluationHelper.getStrVal(xObj2);
					
					int cmp1 = str1.compareTo(str2);
					
					if (!notEquals && (cmp1 == 0)) {
					   result = true;
					}
					else if (notEquals && (cmp1 != 0)) {
					   result = true;
					}
				}
				else {
					isIncompatibleTypes = true;
					
					break;
				}
			}
			
			if (isIncompatibleTypes) {
			   break;	
			}
		}
		
		if (!isIncompatibleTypes) {
		   return result;
		}
		
		for (int idx = 0; idx < size1; idx++) {
			XObject xObj1 = rSeq1.item(idx);

			if (xObj1 instanceof XNumber) {
				XNumber xNumber = (XNumber)xObj1;			   

				xObj1 = XslTransformEvaluationHelper.getXNumberNormalizedValue(xNumber);
			}

			if (xObj1 instanceof XNumber) {
				result = equals((XNumber)xObj1, rSeq2, notEquals);												
			}
			else if (xObj1 instanceof XSNumericType) {
				result = equals((XSNumericType)xObj1, rSeq2, notEquals);				
			}
			else if ((xObj1 instanceof XMLNodeCursorImpl) || (xObj1 instanceof XSUntypedAtomic)) {
				java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj1);

				try {										
					result = equals(new XSDecimal(str1), rSeq2, notEquals);
				}
				catch (NumberFormatException ex) {
					throw new TransformerException("FORG0001 : The string value '" + str1 + "' cannot be converted to a number.");
				}
			}

			if (result) {
				break;	
			}
		}
		
		return result;
    }
	
	/**
	 * Method definition, to get java.math.BigDecimal value from the
	 * supplied XNumber object instance.
	 * 
	 * @param xNum                        The supplied XNumber object 
	 *                                    instance.
	 * 
	 * @return                            Java java.math.BigDecimal object
	 *                                    instance.
	 */
    public static BigDecimal getBigdecimalFromXNumber(XNumber xNum) {
		
    	BigDecimal result = null;		    	
    	
    	XObject xobj = XslTransformEvaluationHelper.getXNumberNormalizedValue(xNum);
    	
    	java.lang.String str1 = null;

    	if (xobj instanceof XNumber) {
    		str1 = (((XNumber)xNum).num()) + ""; 
    	}
    	else {
    		str1 = XslTransformEvaluationHelper.getStrVal(xobj); 
    	}

    	result = new BigDecimal(str1);

    	return result;
	}
    
    /**
	 * Method definition, to get java.math.BigDecimal value from an
	 * XSNumericType object instance.
	 * 
	 * @param xNum                        The supplied XSNumericType object 
	 *                                    instance.
	 * 
	 * @return                            Java java.math.BigDecimal object
	 *                                    instance.
	 */
    public static BigDecimal getBigdecimalFromXSNumericType(XSNumericType xsNumeric) {
		
    	BigDecimal result = null;
    	
    	java.lang.String str1 = xsNumeric.stringValue();
    	
    	result = new BigDecimal(str1); 
    	
    	return result;
    }

}
