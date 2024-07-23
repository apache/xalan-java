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
package org.apache.xpath;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import javax.xml.transform.TransformerException;

import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.operations.Operation;

import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSInteger;

/**
 * A base class for arithmetic operations +, -, div & mod.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 */
public class ArithmeticOperation extends Operation {
	
	private static final long serialVersionUID = 7567257906066639674L;

	protected static final String CARDINALITY_ERR_MESG = "{0} : A sequence of more than one item is not allowed as an operand of operator {1}.";

	protected static final String DIV_BY_ZERO_ERR_MESG = "{0} : An integer division by zero error.";

	protected static final String OPERAND_NOT_NUMERIC_ERR_MESG = "{0} : One or both of the operands of operator {1} are not numeric.";
	
	protected static final String OP_SYMBOL_PLUS = "+";
	
	protected static final String OP_SYMBOL_MINUS = "-";
	
	protected static final String OP_SYMBOL_DIV = "div";
	
	protected static final String OP_SYMBOL_MOD = "mod";
	
    private static final String NON_TERMINATING_DECIMAL_EXPANSION = "Non-terminating decimal expansion";
    
    private static final String DIVISION_BY_ZERO = "Division by zero";
    
    private static final int DEFAULT_DIV_SCALE = 18;
	
	/**
	 * This method does an arithmetic operation on two XNumber object values.
	 *  
	 * @throws TransformerException 
	 */
	protected XObject arithmeticOpOnXNumberValues(XNumber lNumber, XNumber rNumber, 
			                                      String opSymbol) throws TransformerException {

		XObject result = null;

		if (lNumber.isXsInteger() && rNumber.isXsInteger()) {
			String lXsIntegerStr = (lNumber.getXsInteger()).stringValue();
			BigInteger lBigInteger = new BigInteger(lXsIntegerStr);
			String rXsIntegerStr = (rNumber.getXsInteger()).stringValue();
			BigInteger rBigInteger = new BigInteger(rXsIntegerStr);
			if (opSymbol.equals(OP_SYMBOL_PLUS)) {
			   result = new XSInteger(lBigInteger.add(rBigInteger));
			}
			else if (opSymbol.equals(OP_SYMBOL_MINUS)) {
			   result = new XSInteger(lBigInteger.subtract(rBigInteger));				
			}
			else if (opSymbol.equals(OP_SYMBOL_DIV)) {
			   BigDecimal lBigDecimal = new BigDecimal(lXsIntegerStr); 
			   BigDecimal rBigDecimal = new BigDecimal(rXsIntegerStr);			   
			   try {				  
			      result = new XSDecimal(lBigDecimal.divide(rBigDecimal));
			   }
			   catch (ArithmeticException ex) {
				   java.lang.String exceptionMesg = ex.getMessage();
	     		   result = divOpArithmeticExceptionAction(lBigDecimal, rBigDecimal, exceptionMesg);
			   }
			}
			else if (opSymbol.equals(OP_SYMBOL_MOD)) {
			   try {
				  BigDecimal lBigDecimal = new BigDecimal(lXsIntegerStr); 
				  BigDecimal rBigDecimal = new BigDecimal(rXsIntegerStr);
				  result = new XSDecimal(lBigDecimal.remainder(rBigDecimal));
			   }
			   catch (ArithmeticException ex) {				  
				  error(DIV_BY_ZERO_ERR_MESG, new String[] {"FOAR0001"});
			   }
			}
		}
		else if (lNumber.isXsDecimal() && rNumber.isXsDecimal()) {
			BigDecimal lBigDecimal = new BigDecimal((lNumber.getXsDecimal()).stringValue());
			BigDecimal rBigDecimal = new BigDecimal((rNumber.getXsDecimal()).stringValue());
			result = arithmeticOpOnBigDecimalValues(lBigDecimal, rBigDecimal, opSymbol);
		}
		else if (lNumber.isXsDouble() && rNumber.isXsDouble()) {
			Double lDbl = Double.valueOf((lNumber.getXsDouble()).stringValue());
			Double rDbl = Double.valueOf((rNumber.getXsDouble()).stringValue());
			if (opSymbol.equals(OP_SYMBOL_PLUS)) {
			   result = new XSDouble(lDbl + rDbl);
			}
			else if (opSymbol.equals(OP_SYMBOL_MINUS)) {
			   result = new XSDouble(lDbl - rDbl);				
			}
			else if (opSymbol.equals(OP_SYMBOL_DIV)) {
			   result = new XSDecimal(BigDecimal.valueOf(lDbl.doubleValue() / rDbl.doubleValue()));
			}
			else if (opSymbol.equals(OP_SYMBOL_MOD)) {
			   result = new XSDecimal(BigDecimal.valueOf(lDbl.doubleValue() % rDbl.doubleValue()));	
			}			
		}
		else if (lNumber.isXsInteger() && rNumber.isXsDecimal()) {
			BigDecimal lBigDecimal = new BigDecimal((lNumber.getXsInteger()).stringValue());
			BigDecimal rBigDecimal = new BigDecimal((rNumber.getXsDecimal()).stringValue());
			result = arithmeticOpOnBigDecimalValues(lBigDecimal, rBigDecimal, opSymbol);
		}
		else if (lNumber.isXsInteger() && rNumber.isXsDouble()) {
			BigDecimal lBigDecimal = new BigDecimal((lNumber.getXsInteger()).stringValue());
			BigDecimal rBigDecimal = new BigDecimal((rNumber.getXsDouble()).stringValue());
			result = arithmeticOpOnBigDecimalValues(lBigDecimal, rBigDecimal, opSymbol);
		}
		else if (lNumber.isXsDecimal() && rNumber.isXsInteger()) {
			BigDecimal lBigDecimal = new BigDecimal((lNumber.getXsDecimal()).stringValue());
			BigDecimal rBigDecimal = new BigDecimal((rNumber.getXsInteger()).stringValue()); 		 
			result = arithmeticOpOnBigDecimalValues(lBigDecimal, rBigDecimal, opSymbol);
		}
		else if (lNumber.isXsDecimal() && rNumber.isXsDouble()) {
			BigDecimal lBigDecimal = new BigDecimal((lNumber.getXsDecimal()).stringValue());
			BigDecimal rBigDecimal = new BigDecimal((rNumber.getXsDouble()).stringValue());
			result = arithmeticOpOnBigDecimalValues(lBigDecimal, rBigDecimal, opSymbol);
		}
		else if (lNumber.isXsDouble() && rNumber.isXsInteger()) {
			BigDecimal lBigDecimal = new BigDecimal((lNumber.getXsDouble()).stringValue());
			BigDecimal rBigDecimal = new BigDecimal((rNumber.getXsInteger()).stringValue());
			result = arithmeticOpOnBigDecimalValues(lBigDecimal, rBigDecimal, opSymbol);
		}      
		else if (lNumber.isXsDouble() && rNumber.isXsDecimal()) {
			BigDecimal lBigDecimal = new BigDecimal((lNumber.getXsDouble()).stringValue());
			BigDecimal rBigDecimal = new BigDecimal((rNumber.getXsDecimal()).stringValue());			
			result = arithmeticOpOnBigDecimalValues(lBigDecimal, rBigDecimal, opSymbol);
		}      
		else {
			double lDouble = lNumber.num();
			double rDouble = rNumber.num();
			
			if (opSymbol.equals(OP_SYMBOL_PLUS)) {
			   result = new XSDouble(lDouble + rDouble);
			}
			else if (opSymbol.equals(OP_SYMBOL_MINUS)) {
			   result = new XSDouble(lDouble - rDouble);				
			}
			else if (opSymbol.equals(OP_SYMBOL_DIV)) {
			   result = new XSDouble(lDouble / rDouble);	
			}
			else if (opSymbol.equals(OP_SYMBOL_MOD)) {
			   result = new XSDouble(lDouble % rDouble);	
			}
		}

		return result;
	}
	
	/**
	 * This method specifies the processing that takes place, when ArithmeticException occurs
	 * on 'div' operator's evaluation.  
	 */
	protected XObject divOpArithmeticExceptionAction(BigDecimal lBigDecimal, BigDecimal rBigDecimal,
			                                         java.lang.String exceptionMesg) throws TransformerException {
		XObject result = null;

		if (exceptionMesg.startsWith(NON_TERMINATING_DECIMAL_EXPANSION)) {
			BigDecimal resultBigDecimal = lBigDecimal.divide(rBigDecimal, DEFAULT_DIV_SCALE, RoundingMode.HALF_EVEN);
			result = new XSDecimal(resultBigDecimal);
		}
		else if (exceptionMesg.startsWith(DIVISION_BY_ZERO)) {
			error(DIV_BY_ZERO_ERR_MESG, new String[] {"FOAR0001"}); 
		}

		return result;
	}
	
	/**
	 * Method to construct a concrete error message string using information 
	 * supplied as arguments, to be emitted as an javax.xml.transform.TransformerException 
	 * object. 
	 */
	protected void error(String errMesg, String[] args) throws javax.xml.transform.TransformerException {
		for (int idx = 0; idx < args.length; idx++) {		 
			errMesg = errMesg.replace("{"+idx+"}", args[idx]);
		}

		throw new javax.xml.transform.TransformerException(errMesg); 
	}
	
	/**
	 * This method does an arithmetic operation on two BigDecimal values. 
	 */
	private XObject arithmeticOpOnBigDecimalValues(BigDecimal lBigDecimal, BigDecimal rBigDecimal, 
			                                       String opSymbol) throws TransformerException {
		XObject result = null;

		if (opSymbol.equals(OP_SYMBOL_PLUS)) {
			result = new XSDecimal(lBigDecimal.add(rBigDecimal));
		}
		else if (opSymbol.equals(OP_SYMBOL_MINUS)) {
			result = new XSDecimal(lBigDecimal.subtract(rBigDecimal));				
		}
		else if (opSymbol.equals(OP_SYMBOL_DIV)) {
			try {
				result = new XSDecimal(lBigDecimal.divide(rBigDecimal));
			}
			catch (ArithmeticException ex) {
				java.lang.String exceptionMesg = ex.getMessage();
				result = divOpArithmeticExceptionAction(lBigDecimal, rBigDecimal, exceptionMesg);
			}
		}
		else if (opSymbol.equals(OP_SYMBOL_MOD)) {
			try {
				result = new XSDecimal(lBigDecimal.remainder(rBigDecimal));
			}
			catch (ArithmeticException ex) {				  
				error(DIV_BY_ZERO_ERR_MESG, new String[] {"FOAR0001"});
			}
		}

		return result;
	}

}
