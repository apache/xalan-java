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

import javax.xml.transform.TransformerException;

import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSNumericType;

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
	   double lhsDbl = (Double.valueOf(lStrVal)).doubleValue();
	   
	   int size1 = rSeq.size();
	   for (int idx = 0; idx < size1; idx++) {
		  XObject xObj2 = rSeq.item(idx);
		  double rhsDbl;
		  if (xObj2 instanceof XNumber) {
			  rhsDbl = xObj2.num();
			  boolean a1 = !isEqualsOption ? (lhsDbl < rhsDbl) : (lhsDbl <= rhsDbl);  
			  if (a1) {
				  result = true;
				  
				  break; 
			  }
		  }
		  else if (xObj2 instanceof XSNumericType) {
			  java.lang.String rStrVal = ((XSNumericType)xObj2).stringValue();
			  rhsDbl = (Double.valueOf(rStrVal)).doubleValue();
			  boolean a1 = !isEqualsOption ? (lhsDbl < rhsDbl) : (lhsDbl <= rhsDbl);
			  if (a1) {
				  result = true;
				  
				  break; 
			  }
		  }
		  else if (xObj2 instanceof XMLNodeCursorImpl) {
			  java.lang.String rStrVal = ((XMLNodeCursorImpl)xObj2).str();
			  try {
				  rhsDbl = Double.valueOf(rStrVal);
				  boolean a1 = !isEqualsOption ? (lhsDbl < rhsDbl) : (lhsDbl <= rhsDbl);
				  if (a1) {
					  result = true;

					  break; 
				  }
			  }
			  catch (NumberFormatException ex) {
				  throw new TransformerException("FORG0001 : The string value '" + rStrVal + "' cannot be converted to double.");
			  }
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

		double lhsDbl = xNum.num();
		
		int size1 = rSeq.size();
		for (int idx = 0; idx < size1; idx++) {
			XObject xObj2 = rSeq.item(idx);
			double rhsDbl;
			if (xObj2 instanceof XNumber) {
				rhsDbl = xObj2.num();
				boolean a1 = !isEqualsOption ? (lhsDbl < rhsDbl) : (lhsDbl <= rhsDbl);
				if (a1) {
					result = true;
					
					break; 
				}
			}
			else if (xObj2 instanceof XSNumericType) {
				java.lang.String rStrVal = ((XSNumericType)xObj2).stringValue();
				rhsDbl = (Double.valueOf(rStrVal)).doubleValue();
				boolean a1 = !isEqualsOption ? (lhsDbl < rhsDbl) : (lhsDbl <= rhsDbl);
				if (a1) {
					result = true;
					
					break; 
				}
			}
			else if (xObj2 instanceof XMLNodeCursorImpl) {
				java.lang.String rStrVal = ((XMLNodeCursorImpl)xObj2).str();
				try {
					rhsDbl = Double.valueOf(rStrVal);
					boolean a1 = !isEqualsOption ? (lhsDbl < rhsDbl) : (lhsDbl <= rhsDbl);
					if (a1) {
						result = true;

						break; 
					}
				}
				catch (NumberFormatException ex) {
					throw new TransformerException("FORG0001 : The string value '" + rStrVal + "' cannot be converted to double.");
				}
			}
		}

		return result; 
	}
	
	/**
	 * Method definition, to evaluate XPath operators <, <=, when lhs is 
     * an xdm sequence, and rhs is an object with type XNumber.
	 * 
	 * @param rSeq                                    XPath operator lhs operand
	 * @param xNum                                    XPath operator rhs operand
	 * @param isEqualsOption                          When boolean true, this method
	 *                                                implements <= comparison, otherwise
	 *                                                < comparison.
	 * @return                                        Boolean value true or false 
	 * @throws TransformerException
	 */
	public static boolean lessThan(ResultSequence rSeq, XNumber xNum, boolean isEqualsOption) throws TransformerException {
		
		boolean result = false; 

		double rhsDbl = xNum.num();
		
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
		for (int idx = 0; idx < size1; idx++) {
			XObject xObj1 = lSeq.item(idx);
			if (xObj1 instanceof XNumber) {
				result = lessThan((XNumber)xObj1, rSeq, isEqualsOption);				
				if (result) {
				   break;	
				}
			}
			else if (xObj1 instanceof XSNumericType) {
				result = lessThan((XSNumericType)xObj1, rSeq, isEqualsOption);
				if (result) {
					break;	
				}
			}
            else if (xObj1 instanceof XMLNodeCursorImpl) {
               XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj1;
               java.lang.String lStrVal = xmlNodeCursorImpl.str();
               try {
					double lhsDbl = Double.valueOf(lStrVal);
					xObj1 = new XNumber(lhsDbl);
					
					result = lessThan((XSNumericType)xObj1, rSeq, isEqualsOption);
					if (result) {
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
     * lhs is value with XML Schema numeric type, and rhs is a 
     * sequence.
     * 
     * @param obj1                                    XPath operator lhs operand
	 * @param rSeq                                    XPath operator rhs operand
	 * @param isEqualsOption                          When boolean true, this method
	 *                                                implements >= comparison, otherwise
	 *                                                > comparison.
	 * @return                                        Boolean value true or false 
	 * @throws TransformerException
     */
	public static boolean greaterThan(XSNumericType obj1, ResultSequence rSeq, boolean isEqualsOption) throws TransformerException {	   
	   
	   boolean result = false;
	   
	   java.lang.String lStrVal = obj1.stringValue();
	   double lhsDbl = (Double.valueOf(lStrVal)).doubleValue();
	   
	   int size1 = rSeq.size();
	   for (int idx = 0; idx < size1; idx++) {
		  XObject xObj2 = rSeq.item(idx);
		  double rhsDbl;
		  if (xObj2 instanceof XNumber) {
			  rhsDbl = xObj2.num();
			  boolean a1 = !isEqualsOption ? (lhsDbl > rhsDbl) : (lhsDbl >= rhsDbl);  
			  if (a1) {
				  result = true;
				  
				  break; 
			  }
		  }
		  else if (xObj2 instanceof XSNumericType) {
			  java.lang.String rStrVal = ((XSNumericType)xObj2).stringValue();
			  rhsDbl = (Double.valueOf(rStrVal)).doubleValue();
			  boolean a1 = !isEqualsOption ? (lhsDbl > rhsDbl) : (lhsDbl >= rhsDbl);
			  if (a1) {
				  result = true;
				  
				  break; 
			  }
		  }
		  else if (xObj2 instanceof XMLNodeCursorImpl) {
			  java.lang.String rStrVal = ((XMLNodeCursorImpl)xObj2).str();
			  try {
				  rhsDbl = Double.valueOf(rStrVal);
				  boolean a1 = !isEqualsOption ? (lhsDbl > rhsDbl) : (lhsDbl >= rhsDbl);
				  if (a1) {
					  result = true;

					  break; 
				  }
			  }
			  catch (NumberFormatException ex) {
				  throw new TransformerException("FORG0001 : The string value '" + rStrVal + "' cannot be converted to double.");
			  }
		  }
	   }
	   
	   return result;
	}
	
	/**
	 * Method definition, to evaluate XPath operators >, >=, when lhs is 
     * an object with type XNumber, and rhs is an xdm sequence.
	 * 
	 * @param xNum                                    XPath operator lhs operand
	 * @param rSeq                                    XPath operator rhs operand
	 * @param isEqualsOption                          When boolean true, this method
	 *                                                implements >= comparison, otherwise
	 *                                                > comparison.
	 * @return                                        Boolean value true or false 
	 * @throws TransformerException
	 */
	public static boolean greaterThan(XNumber xNum, ResultSequence rSeq, boolean isEqualsOption) throws TransformerException {
		
		boolean result = false; 

		double lhsDbl = xNum.num();
		
		int size1 = rSeq.size();
		for (int idx = 0; idx < size1; idx++) {
			XObject xObj2 = rSeq.item(idx);
			double rhsDbl;
			if (xObj2 instanceof XNumber) {
				rhsDbl = xObj2.num();
				boolean a1 = !isEqualsOption ? (lhsDbl > rhsDbl) : (lhsDbl >= rhsDbl);
				if (a1) {
					result = true;
					
					break; 
				}
			}
			else if (xObj2 instanceof XSNumericType) {
				java.lang.String rStrVal = ((XSNumericType)xObj2).stringValue();
				rhsDbl = (Double.valueOf(rStrVal)).doubleValue();
				boolean a1 = !isEqualsOption ? (lhsDbl > rhsDbl) : (lhsDbl >= rhsDbl);
				if (a1) {
					result = true;
					
					break; 
				}
			}
			else if (xObj2 instanceof XMLNodeCursorImpl) {
				java.lang.String rStrVal = ((XMLNodeCursorImpl)xObj2).str();
				try {
					rhsDbl = Double.valueOf(rStrVal);
					boolean a1 = !isEqualsOption ? (lhsDbl > rhsDbl) : (lhsDbl >= rhsDbl);
					if (a1) {
						result = true;

						break; 
					}
				}
				catch (NumberFormatException ex) {
					throw new TransformerException("FORG0001 : The string value '" + rStrVal + "' cannot be converted to double.");
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
	 * @param xNum                                    XPath operator rhs operand
	 * @param isEqualsOption                          When boolean true, this method
	 *                                                implements >= comparison, otherwise
	 *                                                > comparison.
	 * @return                                        Boolean value true or false 
	 * @throws TransformerException
	 */
	public static boolean greaterThan(ResultSequence rSeq, XNumber xNum, boolean isEqualsOption) throws TransformerException {
		
		boolean result = false; 

		double rhsDbl = xNum.num();
		
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
     * XPath operator's lhs and rhs operands are xdm sequences.
	 * 
	 * @param lSeq                                    XPath operator lhs operand
	 * @param rSeq                                    XPath operator rhs operand
	 * @param isEqualsOption                          When boolean true, this method
	 *                                                implements >= comparison, otherwise
	 *                                                > comparison.
	 * @return                                        Boolean value true or false 
	 * @throws TransformerException
	 */
    public static boolean greaterThan(ResultSequence lSeq, ResultSequence rSeq, boolean isEqualsOption) throws TransformerException {
		
		boolean result = false;
		
		int size1 = lSeq.size();
		for (int idx = 0; idx < size1; idx++) {
			XObject xObj1 = lSeq.item(idx);
			if (xObj1 instanceof XNumber) {
				result = greaterThan((XNumber)xObj1, rSeq, isEqualsOption);				
				if (result) {
				   break;	
				}
			}
			else if (xObj1 instanceof XSNumericType) {
				result = greaterThan((XSNumericType)xObj1, rSeq, isEqualsOption);
				if (result) {
					break;	
				}
			}
            else if (xObj1 instanceof XMLNodeCursorImpl) {
               XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj1;
               java.lang.String lStrVal = xmlNodeCursorImpl.str();
               try {
					double lhsDbl = Double.valueOf(lStrVal);
					xObj1 = new XNumber(lhsDbl);
					
					result = greaterThan((XSNumericType)xObj1, rSeq, isEqualsOption);
					if (result) {
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

}
