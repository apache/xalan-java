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
 * Class definition, providing implementation methods, supporting 
 * evaluation of XPath general comparison operators <, <=, >, >=.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 */
public class XPathGeneralCmpOpSupport {
	
	/**
	 * Method definition, to evaluate XPath operators <, <=, when 
     * lhs value is an XML Schema numeric type, and rhs is an 
     * xdm sequence.
	 * 
	 * @param obj1                                    XPath operator lhs operand
	 * @param rSeq                                    XPath operator rhs operand
	 * @param isEqualsOption                          When boolean true, this method
	 *                                                implements <= comparison, otherwise
	 *                                                < comparison.
	 * @return                                        Boolean value true or false 
	 * @throws TransformerException
	 */
	public static boolean lessThan(XSNumericType obj1, ResultSequence rSeq, boolean isEqualsOption) throws TransformerException {	   
	   
		boolean result = false;

		java.lang.String lStrVal = obj1.stringValue();

		BigDecimal bigDecimal1 = new BigDecimal(lStrVal); 

		int size1 = rSeq.size();

		for (int idx = 0; idx < size1; idx++) {
			XObject xObj2 = rSeq.item(idx);

			BigDecimal bigDecimal2 = null;

			if (xObj2 instanceof XNumber) {			  			  
				xObj2 = XslTransformEvaluationHelper.getXNumberNormalizedValue((XNumber)xObj2);

				java.lang.String str1 = null;

				if (xObj2 instanceof XNumber) {
					str1 = (((XNumber)xObj2).num()) + ""; 
				}
				else {
					str1 = XslTransformEvaluationHelper.getStrVal(xObj2); 
				}

				bigDecimal2 = new BigDecimal(str1);
			}
			else if (xObj2 instanceof XSNumericType) {
				java.lang.String str1 = ((XSNumericType)xObj2).stringValue();			  

				bigDecimal2 = new BigDecimal(str1);
			}
			else if ((xObj2 instanceof XMLNodeCursorImpl) || (xObj2 instanceof XSUntypedAtomic)) {
				java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj2);

				try {
					bigDecimal2 = new BigDecimal(str1);
				}
				catch (NumberFormatException ex) {
					throw new TransformerException("FORG0001 : The string value '" + str1 + "' cannot be converted to a number.");
				}
			}

			if (isEqualsOption) {
				result = ((bigDecimal1.compareTo(bigDecimal2) < 0) || (bigDecimal1.compareTo(bigDecimal2) == 0)); 
			}
			else {
				result = (bigDecimal1.compareTo(bigDecimal2) < 0);
			}

			if (result) {
				break;
			}
		}

		return result;
	}
	
	/**
	 * Method definition, to evaluate XPath operators <, <=, when lhs 
     * value is an object with type XNumber, and rhs is an xdm sequence.
	 * 
	 * @param xNum                                    XPath operator lhs operand
	 * @param rSeq                                    XPath operator rhs operand
	 * @param isEqualsOption                          When boolean true, this method
	 *                                                implements <= comparison, otherwise
	 *                                                < comparison.
	 * @return                                        Boolean value true or false 
	 * @throws TransformerException
	 */
	public static boolean lessThan(XNumber xNum, ResultSequence rSeq, boolean isEqualsOption) throws TransformerException {
		
		boolean result = false; 
		
		XObject xObj = XslTransformEvaluationHelper.getXNumberNormalizedValue(xNum);		
				
		java.lang.String str1 = null;
		
		if (xObj instanceof XNumber) {
		   str1 = (((XNumber)xObj).num()) + ""; 
		}
		else {
		   str1 = XslTransformEvaluationHelper.getStrVal(xObj); 
		}
		
		BigDecimal bigDecimal1 = new BigDecimal(str1);
		
		int size1 = rSeq.size();
		
		for (int idx = 0; idx < size1; idx++) {
			XObject xObj2 = rSeq.item(idx);
			
			BigDecimal bigDecimal2 = null;
			
			if (xObj2 instanceof XNumber) {								
				xObj2 = XslTransformEvaluationHelper.getXNumberNormalizedValue((XNumber)xObj2);
				
				if (xObj2 instanceof XNumber) {
					str1 = (((XNumber)xObj2).num()) + ""; 
				}
				else {
					str1 = XslTransformEvaluationHelper.getStrVal(xObj2); 
				}
				
				bigDecimal2 = new BigDecimal(str1);
				
				if (isEqualsOption) {
				   result = ((bigDecimal1.compareTo(bigDecimal2) < 0) || (bigDecimal1.compareTo(bigDecimal2) == 0)); 
				}
				else {
				   result = (bigDecimal1.compareTo(bigDecimal2) < 0);
				}
				
				if (result) {
				   break;
				}
			}
			else if (xObj2 instanceof XSNumericType) {
				str1 = ((XSNumericType)xObj2).stringValue();				
				
                bigDecimal2 = new BigDecimal(str1);
				
                if (isEqualsOption) {
                	result = ((bigDecimal1.compareTo(bigDecimal2) < 0) || (bigDecimal1.compareTo(bigDecimal2) == 0));
                }
                else {
                	result = (bigDecimal1.compareTo(bigDecimal2) < 0);
                }
				
				if (result) {
					break;
				}
			}
			else if ((xObj2 instanceof XMLNodeCursorImpl) || (xObj2 instanceof XSUntypedAtomic)) {
				str1 = XslTransformEvaluationHelper.getStrVal(xObj2); 

				try {
					bigDecimal2 = new BigDecimal(str1);

					if (isEqualsOption) {
	                	result = ((bigDecimal1.compareTo(bigDecimal2) < 0) || (bigDecimal1.compareTo(bigDecimal2) == 0));
	                }
	                else {
	                	result = (bigDecimal1.compareTo(bigDecimal2) < 0);
	                }
					
					if (result) {
						break;
					}
				}
				catch (NumberFormatException ex) {
					throw new TransformerException("XPTY0004 : A numeric value cannot be compared with '" + str1 + "'.");
				}
			}
		}

		return result;
	}
	
	/**
	 * Method definition, to evaluate XPath operator <, <=, where the first 
	 * operand is an xdm sequence, and the second operand is an object instance 
	 * of type XNumber. 
	 * 
	 * @param rSeq                                    XPath operator's first operand
	 * @param xNum                                    XPath operator's second operand
	 * @param isEqualsOption                          When boolean true, this method
	 *                                                implements <= comparison, otherwise
	 *                                                < comparison.
	 * @return                                        Boolean value true or false 
	 * @throws TransformerException
	 */
	public static boolean lessThan(ResultSequence rSeq, XNumber xNum, boolean isEqualsOption) throws TransformerException {
		
		boolean result = false; 
		
		XObject xObj = XslTransformEvaluationHelper.getXNumberNormalizedValue(xNum);		
		
		java.lang.String str1 = null;
		
		if (xObj instanceof XNumber) {
		   str1 = (((XNumber)xObj).num()) + ""; 
		}
		else {
		   str1 = XslTransformEvaluationHelper.getStrVal(xObj); 
		}
		
		BigDecimal bigDecimal1 = new BigDecimal(str1);
		
		int size1 = rSeq.size();
		
		for (int idx = 0; idx < size1; idx++) {
			XObject xObj2 = rSeq.item(idx);
			
			BigDecimal bigDecimal2 = null;
			
			if (xObj2 instanceof XNumber) {								
				xObj2 = XslTransformEvaluationHelper.getXNumberNormalizedValue((XNumber)xObj2);
				
				if (xObj2 instanceof XNumber) {
					str1 = (((XNumber)xObj2).num()) + ""; 
				}
				else {
					str1 = XslTransformEvaluationHelper.getStrVal(xObj2); 
				}
				
				bigDecimal2 = new BigDecimal(str1);
				
				if (isEqualsOption) {
				   result = ((bigDecimal2.compareTo(bigDecimal1) < 0) || (bigDecimal2.compareTo(bigDecimal1) == 0)); 
				}
				else {
				   result = (bigDecimal2.compareTo(bigDecimal1) < 0);
				}
				
				if (result) {
				   break;
				}
			}
			else if (xObj2 instanceof XSNumericType) {
				str1 = ((XSNumericType)xObj2).stringValue();				
				
                bigDecimal2 = new BigDecimal(str1);
				
                if (isEqualsOption) {
                	result = ((bigDecimal2.compareTo(bigDecimal1) < 0) || (bigDecimal2.compareTo(bigDecimal1) == 0));
                }
                else {
                	result = (bigDecimal2.compareTo(bigDecimal1) < 0);
                }
				
				if (result) {
					break;
				}
			}
			else if ((xObj2 instanceof XMLNodeCursorImpl) || (xObj2 instanceof XSUntypedAtomic)) {
				str1 = XslTransformEvaluationHelper.getStrVal(xObj2); 

				try {
					bigDecimal2 = new BigDecimal(str1);

					if (isEqualsOption) {
	                	result = ((bigDecimal2.compareTo(bigDecimal1) < 0) || (bigDecimal2.compareTo(bigDecimal1) == 0));
	                }
	                else {
	                	result = (bigDecimal2.compareTo(bigDecimal1) < 0);
	                }
					
					if (result) {
						break;
					}
				}
				catch (NumberFormatException ex) {
					throw new TransformerException("XPTY0004 : A numeric value cannot be compared with '" + str1 + "'.");
				}
			}
		}

		return result; 
	}
	
	/**
	 * Method definition, to evaluate XPath operators <, <=, when lhs is 
     * an xdm sequence, and rhs is an object with type XSNumericType.
	 * 
	 * @param rSeq                                    XPath operator lhs operand
	 * @param obj1                                    XPath operator rhs operand
	 * @param isEqualsOption                          When boolean true, this method
	 *                                                implements <= comparison, otherwise
	 *                                                < comparison.
	 * @return                                        Boolean value true or false 
	 * @throws TransformerException
	 */
	public static boolean lessThan(ResultSequence rSeq, XSNumericType obj1, boolean isEqualsOption) throws TransformerException {
		
		boolean result = false; 
		
		java.lang.String rStrVal = obj1.stringValue();
		double rhsDbl = (Double.valueOf(rStrVal)).doubleValue();
		
		int size1 = rSeq.size();
		for (int idx = 0; idx < size1; idx++) {
			XObject xObj2 = rSeq.item(idx);
			double lhsDbl;
			if (xObj2 instanceof XNumber) {
				lhsDbl = xObj2.num();
				boolean a1 = !isEqualsOption ? (lhsDbl < rhsDbl) : (lhsDbl <= rhsDbl);
				if (a1) {
					result = true;
					
					break; 
				}
			}
			else if (xObj2 instanceof XSNumericType) {
				java.lang.String lStrVal = ((XSNumericType)xObj2).stringValue();
				lhsDbl = (Double.valueOf(lStrVal)).doubleValue();
				boolean a1 = !isEqualsOption ? (lhsDbl < rhsDbl) : (lhsDbl <= rhsDbl);
				if (a1) {
					result = true;
					
					break; 
				}
			}
			else if (xObj2 instanceof XMLNodeCursorImpl) {
				java.lang.String lStrVal = ((XMLNodeCursorImpl)xObj2).str();
				try {
					lhsDbl = Double.valueOf(lStrVal);
					boolean a1 = !isEqualsOption ? (lhsDbl < rhsDbl) : (lhsDbl <= rhsDbl);
					if (a1) {
						result = true;

						break; 
					}
				}
				catch (NumberFormatException ex) {
					throw new TransformerException("FORG0001 : The string value '" + lStrVal + "' cannot be converted to double.");
				}
			}
		}

		return result; 
	}
	
	/**
	 * Method definition, to evaluate XPath operators <, <=, when 
     * XPath operator's lhs and rhs operands are xdm sequences.
     * 
	 * @param lSeq                                    XPath operator lhs operand
	 * @param rSeq                                    XPath operator rhs operand
	 * @param isEqualsOption                          When boolean true, this method
	 *                                                implements <= comparison, otherwise
	 *                                                < comparison.
	 * @return                                        Boolean value true or false 
	 * @throws TransformerException
	 */
    public static boolean lessThan(ResultSequence lSeq, ResultSequence rSeq, boolean isEqualsOption) throws TransformerException {
		
		boolean result = false;
		
		int size1 = lSeq.size();
		
		int size2 = rSeq.size(); 
		
		// Check, possible xdm string & untypedAtomic value comparisons

		boolean isIncompatibleTypes = false;

		for (int idx = 0; idx < size1; idx++) {
			XObject xObj1 = lSeq.item(idx);
			for (int idx2 = 0; idx2 < size2; idx2++) {
				XObject xObj2 = rSeq.item(idx2);

				if ((xObj1 instanceof XSString || xObj1 instanceof XString) &&
						                                                   (xObj2 instanceof XSString || xObj2 instanceof XString)) {
					java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj1);
					java.lang.String str2 = XslTransformEvaluationHelper.getStrVal(xObj2);

					int cmp1 = str1.compareTo(str2);

					if (!isEqualsOption && (cmp1 < 0)) {
						result = true;
					}
					else if (isEqualsOption && (cmp1 <= 0)) {
						result = true;
					}
				}
				else if ((xObj1 instanceof XSUntypedAtomic) && (xObj2 instanceof XSUntypedAtomic)) {
					java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj1);
					java.lang.String str2 = XslTransformEvaluationHelper.getStrVal(xObj2);

					int cmp1 = str1.compareTo(str2);

					if (!isEqualsOption && (cmp1 < 0)) {
						result = true;
					}
					else if (isEqualsOption && (cmp1 <= 0)) {
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
			XObject xObj1 = lSeq.item(idx);

			if (xObj1 instanceof XNumber) {
				XNumber xNumber = (XNumber)xObj1;			   

				xObj1 = XslTransformEvaluationHelper.getXNumberNormalizedValue(xNumber);
			}

			if (xObj1 instanceof XNumber) {
				result = lessThan((XNumber)xObj1, rSeq, isEqualsOption);												
			}
			else if (xObj1 instanceof XSNumericType) {
				result = lessThan((XSNumericType)xObj1, rSeq, isEqualsOption);				
			}
			else if ((xObj1 instanceof XMLNodeCursorImpl) || (xObj1 instanceof XSUntypedAtomic)) {
				java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj1);

				try {										
					result = lessThan(new XSDecimal(str1), rSeq, isEqualsOption);
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
	 * Method definition, to evaluate XPath operator >, >=, where the first 
	 * operand is an object instance of type XSNumericType, and second operand 
	 * is an xdm sequence.
	 * 
	 * @param obj1                                    XPath operator's first operand
	 * @param rSeq                                    XPath operator's second operand
	 * @param isEqualsOption                          When boolean true, this method
	 *                                                implements >= comparison, otherwise
	 *                                                > comparison.
	 * @return                                        Boolean value true or false 
	 * @throws TransformerException
	 */
	public static boolean greaterThan(XSNumericType obj1, ResultSequence rSeq, boolean isEqualsOption) throws TransformerException {	   
	   
	   boolean result = false;
	   
	   java.lang.String lStrVal = obj1.stringValue();
	   
	   BigDecimal bigDecimal1 = new BigDecimal(lStrVal); 
	   
	   int size1 = rSeq.size();
	   
	   for (int idx = 0; idx < size1; idx++) {
		  XObject xObj2 = rSeq.item(idx);
		  
		  BigDecimal bigDecimal2 = null;
		  
		  if (xObj2 instanceof XNumber) {			  			  
			  xObj2 = XslTransformEvaluationHelper.getXNumberNormalizedValue((XNumber)xObj2);

			  java.lang.String str1 = null;

			  if (xObj2 instanceof XNumber) {
				  str1 = (((XNumber)xObj2).num()) + ""; 
			  }
			  else {
				  str1 = XslTransformEvaluationHelper.getStrVal(xObj2); 
			  }
			  
			  bigDecimal2 = new BigDecimal(str1);
		  }
		  else if (xObj2 instanceof XSNumericType) {
			  java.lang.String str1 = ((XSNumericType)xObj2).stringValue();			  
			  
			  bigDecimal2 = new BigDecimal(str1);
		  }
		  else if ((xObj2 instanceof XMLNodeCursorImpl) || (xObj2 instanceof XSUntypedAtomic)) {
			  java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj2);
			  
			  try {
				  bigDecimal2 = new BigDecimal(str1);
			  }
			  catch (NumberFormatException ex) {
				  throw new TransformerException("FORG0001 : The string value '" + str1 + "' cannot be converted to a number.");
			  }
		  }
		  
		  if (isEqualsOption) {
			  result = ((bigDecimal1.compareTo(bigDecimal2) > 0) || (bigDecimal1.compareTo(bigDecimal2) == 0)); 
		  }
		  else {
			  result = (bigDecimal1.compareTo(bigDecimal2) > 0);
		  }

		  if (result) {
			  break;
		  }
	   }
	   
	   return result;
	}
	
	/**
	 * Method definition, to evaluate XPath operator >, >=, where the first 
	 * operand is an object instance of type XNumber, and second operand 
	 * is an xdm sequence.
	 * 
	 * @param xNum                                    XPath operator's first operand
	 * @param rSeq                                    XPath operator's second operand
	 * @param isEqualsOption                          When boolean true, this method
	 *                                                implements >= comparison, otherwise
	 *                                                > comparison.
	 * @return                                        Boolean value true or false 
	 * @throws TransformerException
	 */
	public static boolean greaterThan(XNumber xNum, ResultSequence rSeq, boolean isEqualsOption) throws TransformerException {
		
		boolean result = false; 
		
		XObject xObj = XslTransformEvaluationHelper.getXNumberNormalizedValue(xNum);		
				
		java.lang.String str1 = null;
		
		if (xObj instanceof XNumber) {
		   str1 = (((XNumber)xObj).num()) + ""; 
		}
		else {
		   str1 = XslTransformEvaluationHelper.getStrVal(xObj); 
		}
		
		BigDecimal bigDecimal1 = new BigDecimal(str1);
		
		int size1 = rSeq.size();
		
		for (int idx = 0; idx < size1; idx++) {
			XObject xObj2 = rSeq.item(idx);
			
			BigDecimal bigDecimal2 = null;
			
			if (xObj2 instanceof XNumber) {								
				xObj2 = XslTransformEvaluationHelper.getXNumberNormalizedValue((XNumber)xObj2);
				
				if (xObj2 instanceof XNumber) {
					str1 = (((XNumber)xObj2).num()) + ""; 
				}
				else {
					str1 = XslTransformEvaluationHelper.getStrVal(xObj2); 
				}
				
				bigDecimal2 = new BigDecimal(str1);
				
				if (isEqualsOption) {
				   result = ((bigDecimal1.compareTo(bigDecimal2) > 0) || (bigDecimal1.compareTo(bigDecimal2) == 0)); 
				}
				else {
				   result = (bigDecimal1.compareTo(bigDecimal2) > 0);
				}
				
				if (result) {
				   break;
				}
			}
			else if (xObj2 instanceof XSNumericType) {
				str1 = ((XSNumericType)xObj2).stringValue();				
				
                bigDecimal2 = new BigDecimal(str1);
				
                if (isEqualsOption) {
                	result = ((bigDecimal1.compareTo(bigDecimal2) > 0) || (bigDecimal1.compareTo(bigDecimal2) == 0));
                }
                else {
                	result = (bigDecimal1.compareTo(bigDecimal2) > 0);
                }
				
				if (result) {
					break;
				}
			}
			else if ((xObj2 instanceof XMLNodeCursorImpl) || (xObj2 instanceof XSUntypedAtomic)) {
				str1 = XslTransformEvaluationHelper.getStrVal(xObj2); 

				try {
					bigDecimal2 = new BigDecimal(str1);

					if (isEqualsOption) {
	                	result = ((bigDecimal1.compareTo(bigDecimal2) > 0) || (bigDecimal1.compareTo(bigDecimal2) == 0));
	                }
	                else {
	                	result = (bigDecimal1.compareTo(bigDecimal2) > 0);
	                }
					
					if (result) {
						break;
					}
				}
				catch (NumberFormatException ex) {
					throw new TransformerException("XPTY0004 : A numeric value cannot be compared with '" + str1 + "'.");
				}
			}
		}

		return result; 
	}		
	
	/**
	 * Method definition, to evaluate XPath operator >, >=, where the first 
	 * operand is an xdm sequence, and the second operand is an object instance 
	 * of type XNumber. 
	 * 
	 * @param rSeq                                    XPath operator's first operand
	 * @param xNum                                    XPath operator's second operand
	 * @param isEqualsOption                          When boolean true, this method
	 *                                                implements >= comparison, otherwise
	 *                                                > comparison.
	 * @return                                        Boolean value true or false 
	 * @throws TransformerException
	 */
	public static boolean greaterThan(ResultSequence rSeq, XNumber xNum, boolean isEqualsOption) throws TransformerException {
		
		boolean result = false; 
		
		XObject xObj = XslTransformEvaluationHelper.getXNumberNormalizedValue(xNum);		
		
		java.lang.String str1 = null;
		
		if (xObj instanceof XNumber) {
		   str1 = (((XNumber)xObj).num()) + ""; 
		}
		else {
		   str1 = XslTransformEvaluationHelper.getStrVal(xObj); 
		}
		
		BigDecimal bigDecimal1 = new BigDecimal(str1);
		
		int size1 = rSeq.size();
		
		for (int idx = 0; idx < size1; idx++) {
			XObject xObj2 = rSeq.item(idx);
			
			BigDecimal bigDecimal2 = null;
			
			if (xObj2 instanceof XNumber) {								
				xObj2 = XslTransformEvaluationHelper.getXNumberNormalizedValue((XNumber)xObj2);
				
				if (xObj2 instanceof XNumber) {
					str1 = (((XNumber)xObj2).num()) + ""; 
				}
				else {
					str1 = XslTransformEvaluationHelper.getStrVal(xObj2); 
				}
				
				bigDecimal2 = new BigDecimal(str1);
				
				if (isEqualsOption) {
				   result = ((bigDecimal2.compareTo(bigDecimal1) > 0) || (bigDecimal2.compareTo(bigDecimal1) == 0)); 
				}
				else {
				   result = (bigDecimal2.compareTo(bigDecimal1) > 0);
				}
				
				if (result) {
				   break;
				}
			}
			else if (xObj2 instanceof XSNumericType) {
				str1 = ((XSNumericType)xObj2).stringValue();				
				
                bigDecimal2 = new BigDecimal(str1);
				
                if (isEqualsOption) {
                	result = ((bigDecimal2.compareTo(bigDecimal1) > 0) || (bigDecimal2.compareTo(bigDecimal1) == 0));
                }
                else {
                	result = (bigDecimal2.compareTo(bigDecimal1) > 0);
                }
				
				if (result) {
					break;
				}
			}
			else if ((xObj2 instanceof XMLNodeCursorImpl) || (xObj2 instanceof XSUntypedAtomic)) {
				str1 = XslTransformEvaluationHelper.getStrVal(xObj2); 

				try {
					bigDecimal2 = new BigDecimal(str1);

					if (isEqualsOption) {
	                	result = ((bigDecimal2.compareTo(bigDecimal1) > 0) || (bigDecimal2.compareTo(bigDecimal1) == 0));
	                }
	                else {
	                	result = (bigDecimal2.compareTo(bigDecimal1) > 0);
	                }
					
					if (result) {
						break;
					}
				}
				catch (NumberFormatException ex) {
					throw new TransformerException("XPTY0004 : A numeric value cannot be compared with '" + str1 + "'.");
				}
			}
		}

		return result; 
	}
	
	/**
	 * Method definition, to evaluate XPath operators >, >=, when lhs is 
     * an xdm sequence, and rhs is an object with type XNumber.
	 * 
	 * @param rSeq                                    XPath operator lhs operand
	 * @param obj1                                    XPath operator rhs operand
	 * @param isEqualsOption                          When boolean true, this method
	 *                                                implements >= comparison, otherwise
	 *                                                > comparison.
	 * @return                                        Boolean value true or false 
	 * @throws TransformerException
	 */
	public static boolean greaterThan(ResultSequence rSeq, XSNumericType obj1, boolean isEqualsOption) throws TransformerException {
		
		boolean result = false;
		
		// REVISIT
		
		java.lang.String rStrVal = obj1.stringValue();
		double rhsDbl = (Double.valueOf(rStrVal)).doubleValue();
		
		int size1 = rSeq.size();
		for (int idx = 0; idx < size1; idx++) {
			XObject xObj2 = rSeq.item(idx);
			double lhsDbl;
			if (xObj2 instanceof XNumber) {
				lhsDbl = xObj2.num();
				boolean a1 = !isEqualsOption ? (lhsDbl > rhsDbl) : (lhsDbl >= rhsDbl);
				if (a1) {
					result = true;
					
					break; 
				}
			}
			else if (xObj2 instanceof XSNumericType) {
				java.lang.String lStrVal = ((XSNumericType)xObj2).stringValue();
				lhsDbl = (Double.valueOf(lStrVal)).doubleValue();
				boolean a1 = !isEqualsOption ? (lhsDbl > rhsDbl) : (lhsDbl >= rhsDbl);
				if (a1) {
					result = true;
					
					break; 
				}
			}
			else if (xObj2 instanceof XMLNodeCursorImpl) {
				java.lang.String lStrVal = ((XMLNodeCursorImpl)xObj2).str();
				try {
					lhsDbl = Double.valueOf(lStrVal);
					boolean a1 = !isEqualsOption ? (lhsDbl > rhsDbl) : (lhsDbl >= rhsDbl);
					if (a1) {
						result = true;

						break; 
					}
				}
				catch (NumberFormatException ex) {
					throw new TransformerException("FORG0001 : The string value '" + lStrVal + "' cannot be converted to double.");
				}
			}
		}

		return result; 
	}
	
	/**
	 * Method definition, to evaluate XPath operators >, >=, when 
     * XPath operator's first and second operands are xdm sequences.
	 * 
	 * @param rSeq1                                   XPath operator's first operand
	 * @param rSeq2                                   XPath operator's second operand
	 * @param isEqualsOption                          When boolean true, this method
	 *                                                implements >= comparison, otherwise
	 *                                                > comparison.
	 * @return                                        Boolean value true or false 
	 * @throws TransformerException
	 */
    public static boolean greaterThan(ResultSequence rSeq1, ResultSequence rSeq2, boolean isEqualsOption) throws TransformerException {
		
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

					if (!isEqualsOption && (cmp1 > 0)) {
						result = true;
					}
					else if (isEqualsOption && (cmp1 >= 0)) {
						result = true;
					}
				}
				else if ((xObj1 instanceof XSUntypedAtomic) && (xObj2 instanceof XSUntypedAtomic)) {
					java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj1);
					java.lang.String str2 = XslTransformEvaluationHelper.getStrVal(xObj2);

					int cmp1 = str1.compareTo(str2);

					if (!isEqualsOption && (cmp1 > 0)) {
						result = true;
					}
					else if (isEqualsOption && (cmp1 >= 0)) {
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
				result = greaterThan((XNumber)xObj1, rSeq2, isEqualsOption);												
			}
			else if (xObj1 instanceof XSNumericType) {
				result = greaterThan((XSNumericType)xObj1, rSeq2, isEqualsOption);				
			}
			else if ((xObj1 instanceof XMLNodeCursorImpl) || (xObj1 instanceof XSUntypedAtomic)) {
				java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(xObj1);

				try {										
					result = greaterThan(new XSDecimal(str1), rSeq2, isEqualsOption);
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

}
