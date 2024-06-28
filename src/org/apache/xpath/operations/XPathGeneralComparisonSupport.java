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
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;

/**
 * A class providing implementation methods, to support 
 * evaluation of XPath general comparison operators =, !=.
 * 
 * Ref : https://www.w3.org/TR/xpath-31/#id-general-comparisons
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 */
public class XPathGeneralComparisonSupport {
	
	/**
     * Method to evaluate an XPath operator =, when its LHS is value of an 
     * XML Schema numeric type, and RHS is a sequence.
	 */
	public static boolean equals(XSNumericType obj1, ResultSequence rSeq) throws TransformerException {	   
	   boolean result = false;
	   
	   java.lang.String lStrVal = obj1.stringValue();
	   double lhsDbl = (Double.valueOf(lStrVal)).doubleValue();
	   for (int idx = 0; idx < rSeq.size(); idx++) {
		  XObject rhsItem = rSeq.item(idx);
		  double rhsDbl;
		  if (rhsItem instanceof XNumber) {
			  rhsDbl = rhsItem.num();
			  if (lhsDbl == rhsDbl) {
				  result = true;
				  break; 
			  }
		  }
		  else if (rhsItem instanceof XSNumericType) {
			  java.lang.String rStrVal = ((XSNumericType)rhsItem).stringValue();
			  rhsDbl = (Double.valueOf(rStrVal)).doubleValue();
			  if (lhsDbl == rhsDbl) {
				  result = true;
				  break; 
			  }
		  }
	   }
	   
	   return result;
	}
	
	/**
     * Method to evaluate an XPath operator =, when its LHS is of type XNumber,
     * and RHS is a sequence.
	 */
	public static boolean equals(XNumber xNum, ResultSequence rSeq) throws TransformerException {
		boolean result = false; 

		for (int idx = 0; idx < rSeq.size(); idx++) {
			XObject rhsItem = rSeq.item(idx);
			double rhsDbl;
			if (rhsItem instanceof XNumber) {
				rhsDbl = rhsItem.num();
				if (xNum.num() == rhsDbl) {
					result = true;
					break; 
				}
			}
			else if (rhsItem instanceof XSNumericType) {
				java.lang.String rStrVal = ((XSNumericType)rhsItem).stringValue();
				rhsDbl = (Double.valueOf(rStrVal)).doubleValue();
				if (xNum.num() == rhsDbl) {
					result = true;
					break; 
				}
			}
		}

		return result; 
	}
	
	/**
     * Method to evaluate an XPath operator =, when its LHS is value of an 
     * XML Schema type xs:string, and RHS is a sequence.
	 */
	public static boolean equals(XSString obj1, ResultSequence rSeq) throws TransformerException {
		boolean result = false;

		java.lang.String lStrVal = obj1.stringValue();
		for (int idx = 0; idx < rSeq.size(); idx++) {
			XObject rhsItem = rSeq.item(idx);
			if (rhsItem instanceof XSString) {
				java.lang.String rStrVal = ((XSString)rhsItem).stringValue();
				if (lStrVal.equals(rStrVal)) {
					result = true;
					break; 
				}
			}
			else if (rhsItem instanceof XString) {
				java.lang.String rStrVal = ((XString)rhsItem).str();
				if (lStrVal.equals(rStrVal)) {
					result = true;
					break; 
				}
			}
			else {
				java.lang.String rStrVal = rhsItem.str();
				if (lStrVal.equals(rStrVal)) {
					result = true;
					break; 
				}
		    }
		}

		return result;
    }
	
	/**
     * Method to evaluate an XPath operator =, when its LHS is of type XString,
     * and RHS is a sequence.
	 */
	public static boolean equals(XString obj1, ResultSequence rSeq) throws TransformerException {
		boolean result = false;

		java.lang.String lStrVal = obj1.str();
		for (int idx = 0; idx < rSeq.size(); idx++) {
			XObject rhsItem = rSeq.item(idx);
			if (rhsItem instanceof XSString) {
				java.lang.String rStrVal = ((XSString)rhsItem).stringValue();
				if (lStrVal.equals(rStrVal)) {
					result = true;
					break; 
				}
			}
			else if (rhsItem instanceof XString) {
				java.lang.String rStrVal = ((XString)rhsItem).str();
				if (lStrVal.equals(rStrVal)) {
					result = true;
					break; 
				}
			}
			else {
				java.lang.String rStrVal = rhsItem.str();
				if (lStrVal.equals(rStrVal)) {
					result = true;
					break; 
				}
		    }
		}

		return result;
    }
	
	/**
     * Method to evaluate an XPath operator !=, when its LHS is value of an 
     * XML Schema numeric type, and RHS is a sequence.
	 */
	public static boolean notEquals(XSNumericType obj1, ResultSequence rSeq) throws TransformerException {	   
		boolean result = false;

		java.lang.String lStrVal = obj1.stringValue();
		double lhsDbl = (Double.valueOf(lStrVal)).doubleValue();
		for (int idx = 0; idx < rSeq.size(); idx++) {
			XObject rhsItem = rSeq.item(idx);
			double rhsDbl;
			if (rhsItem instanceof XNumber) {
				rhsDbl = rhsItem.num();
				if (lhsDbl != rhsDbl) {
					result = true;
					break; 
				}
			}
			else if (rhsItem instanceof XSNumericType) {
				java.lang.String rStrVal = ((XSNumericType)rhsItem).stringValue();
				rhsDbl = (Double.valueOf(rStrVal)).doubleValue();
				if (lhsDbl != rhsDbl) {
					result = true;
					break; 
				}
			}
		}

		return result;
	}

	/**
     * Method to evaluate an XPath operator !=, when its LHS is of type XNumber, 
     * and RHS is a sequence.
	 */
	public static boolean notEquals(XNumber xNum, ResultSequence rSeq) throws TransformerException {
		boolean result = false; 

		for (int idx = 0; idx < rSeq.size(); idx++) {
			XObject rhsItem = rSeq.item(idx);
			double rhsDbl;
			if (rhsItem instanceof XNumber) {
				rhsDbl = rhsItem.num();
				if (xNum.num() != rhsDbl) {
					result = true;
					break; 
				}
			}
			else if (rhsItem instanceof XSNumericType) {
				java.lang.String rStrVal = ((XSNumericType)rhsItem).stringValue();
				rhsDbl = (Double.valueOf(rStrVal)).doubleValue();
				if (xNum.num() != rhsDbl) {
					result = true;
					break; 
				}
			}
		}

		return result; 
	}

	/**
     * Method to evaluate an XPath operator !=, when its LHS is value of an 
     * XML Schema type xs:string, and RHS is a sequence.
	 */
	public static boolean notEquals(XSString obj1, ResultSequence rSeq) throws TransformerException {
		boolean result = false;

		java.lang.String lStrVal = obj1.stringValue();
		for (int idx = 0; idx < rSeq.size(); idx++) {
			XObject rhsItem = rSeq.item(idx);
			if (rhsItem instanceof XSString) {
				java.lang.String rStrVal = ((XSString)rhsItem).stringValue();
				if (!lStrVal.equals(rStrVal)) {
					result = true;
					break; 
				}
			}
			else if (rhsItem instanceof XString) {
				java.lang.String rStrVal = ((XString)rhsItem).str();
				if (!lStrVal.equals(rStrVal)) {
					result = true;
					break; 
				}
			}
			else {
				java.lang.String rStrVal = rhsItem.str();
				if (!lStrVal.equals(rStrVal)) {
					result = true;
					break; 
				}
			}
		}

		return result;
	}

	/**
     * Method to evaluate an XPath operator !=, when its LHS is of type XString, 
     * and RHS is a sequence.
	 */
	public static boolean notEquals(XString obj1, ResultSequence rSeq) throws TransformerException {
		boolean result = false;

		java.lang.String lStrVal = obj1.str();
		for (int idx = 0; idx < rSeq.size(); idx++) {
			XObject rhsItem = rSeq.item(idx);
			if (rhsItem instanceof XSString) {
				java.lang.String rStrVal = ((XSString)rhsItem).stringValue();
				if (!lStrVal.equals(rStrVal)) {
					result = true;
					break; 
				}
			}
			else if (rhsItem instanceof XString) {
				java.lang.String rStrVal = ((XString)rhsItem).str();
				if (!lStrVal.equals(rStrVal)) {
					result = true;
					break; 
				}
			}
			else {
				java.lang.String rStrVal = rhsItem.str();
				if (!lStrVal.equals(rStrVal)) {
					result = true;
					break; 
				}
			}
		}

		return result;
	}

}
