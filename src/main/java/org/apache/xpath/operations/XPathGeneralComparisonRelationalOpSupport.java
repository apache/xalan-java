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

import xml.xpath31.processor.types.XSNumericType;

/**
 * A class providing implementation methods, to support 
 * evaluation of XPath general comparison operators <, <=, >, >=.
 * 
 * Ref : https://www.w3.org/TR/xpath-31/#id-general-comparisons
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 */
public class XPathGeneralComparisonRelationalOpSupport {
	
	/**
     * Method to evaluate XPath operators <, <=, when its LHS is value of an 
     * XML Schema numeric type, and RHS is a sequence.
	 */
	public static boolean lessThan(XSNumericType obj1, ResultSequence rSeq, boolean isEqualsOption) throws TransformerException {	   
	   boolean result = false;
	   
	   java.lang.String lStrVal = obj1.stringValue();
	   double lhsDbl = (Double.valueOf(lStrVal)).doubleValue();
	   
	   for (int idx = 0; idx < rSeq.size(); idx++) {
		  XObject rhsItem = rSeq.item(idx);
		  double rhsDbl;
		  if (rhsItem instanceof XNumber) {
			  rhsDbl = rhsItem.num();
			  boolean resultHere = !isEqualsOption ? (lhsDbl < rhsDbl) : (lhsDbl <= rhsDbl);  
			  if (resultHere) {
				  result = true;
				  break; 
			  }
		  }
		  else if (rhsItem instanceof XSNumericType) {
			  java.lang.String rStrVal = ((XSNumericType)rhsItem).stringValue();
			  rhsDbl = (Double.valueOf(rStrVal)).doubleValue();
			  boolean resultHere = !isEqualsOption ? (lhsDbl < rhsDbl) : (lhsDbl <= rhsDbl);
			  if (resultHere) {
				  result = true;
				  break; 
			  }
		  }
	   }
	   
	   return result;
	}
	
	/**
     * Method to evaluate XPath operators <, <=, when its LHS is of type XNumber,
     * and RHS is a sequence.
	 */
	public static boolean lessThan(XNumber xNum, ResultSequence rSeq, boolean isEqualsOption) throws TransformerException {
		boolean result = false; 

		double lhsDbl = xNum.num();
		
		for (int idx = 0; idx < rSeq.size(); idx++) {
			XObject rhsItem = rSeq.item(idx);
			double rhsDbl;
			if (rhsItem instanceof XNumber) {
				rhsDbl = rhsItem.num();
				boolean resultHere = !isEqualsOption ? (lhsDbl < rhsDbl) : (lhsDbl <= rhsDbl);
				if (resultHere) {
					result = true;
					break; 
				}
			}
			else if (rhsItem instanceof XSNumericType) {
				java.lang.String rStrVal = ((XSNumericType)rhsItem).stringValue();
				rhsDbl = (Double.valueOf(rStrVal)).doubleValue();
				boolean resultHere = !isEqualsOption ? (lhsDbl < rhsDbl) : (lhsDbl <= rhsDbl);
				if (resultHere) {
					result = true;
					break; 
				}
			}
		}

		return result; 
	}
	
	/**
     * Method to evaluate XPath operators >, >=, when its LHS is value of an 
     * XML Schema numeric type, and RHS is a sequence.
	 */
	public static boolean greaterThan(XSNumericType obj1, ResultSequence rSeq, boolean isEqualsOption) throws TransformerException {	   
	   boolean result = false;
	   
	   java.lang.String lStrVal = obj1.stringValue();
	   double lhsDbl = (Double.valueOf(lStrVal)).doubleValue();
	   
	   for (int idx = 0; idx < rSeq.size(); idx++) {
		  XObject rhsItem = rSeq.item(idx);
		  double rhsDbl;
		  if (rhsItem instanceof XNumber) {
			  rhsDbl = rhsItem.num();
			  boolean resultHere = !isEqualsOption ? (lhsDbl > rhsDbl) : (lhsDbl >= rhsDbl);  
			  if (resultHere) {
				  result = true;
				  break; 
			  }
		  }
		  else if (rhsItem instanceof XSNumericType) {
			  java.lang.String rStrVal = ((XSNumericType)rhsItem).stringValue();
			  rhsDbl = (Double.valueOf(rStrVal)).doubleValue();
			  boolean resultHere = !isEqualsOption ? (lhsDbl > rhsDbl) : (lhsDbl >= rhsDbl);
			  if (resultHere) {
				  result = true;
				  break; 
			  }
		  }
	   }
	   
	   return result;
	}
	
	/**
     * Method to evaluate XPath operators >, >=, when its LHS is of type XNumber,
     * and RHS is a sequence.
	 */
	public static boolean greaterThan(XNumber xNum, ResultSequence rSeq, boolean isEqualsOption) throws TransformerException {
		boolean result = false; 

		double lhsDbl = xNum.num();
		
		for (int idx = 0; idx < rSeq.size(); idx++) {
			XObject rhsItem = rSeq.item(idx);
			double rhsDbl;
			if (rhsItem instanceof XNumber) {
				rhsDbl = rhsItem.num();
				boolean resultHere = !isEqualsOption ? (lhsDbl > rhsDbl) : (lhsDbl >= rhsDbl);
				if (resultHere) {
					result = true;
					break; 
				}
			}
			else if (rhsItem instanceof XSNumericType) {
				java.lang.String rStrVal = ((XSNumericType)rhsItem).stringValue();
				rhsDbl = (Double.valueOf(rStrVal)).doubleValue();
				boolean resultHere = !isEqualsOption ? (lhsDbl > rhsDbl) : (lhsDbl >= rhsDbl);
				if (resultHere) {
					result = true;
					break; 
				}
			}
		}

		return result; 
	}

}
