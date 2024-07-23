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
	
	/**
	 * This method does an arithmetic operation on two XNumber object values. 
	 */
	protected XObject arithmeticOpOnXNumberValues(XNumber lNumber, XNumber rNumber, String opSymbol) {

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
		}
		else if (lNumber.isXsDecimal() && rNumber.isXsDecimal()) {
			String lXsDecimalStr = (lNumber.getXsDecimal()).stringValue();
			BigDecimal lBigDecimal = new BigDecimal(lXsDecimalStr);
			String rXsDecimalStr = (rNumber.getXsDecimal()).stringValue();
			BigDecimal rBigDecimal = new BigDecimal(rXsDecimalStr);
			if (opSymbol.equals(OP_SYMBOL_PLUS)) {
			   result = new XSDecimal(lBigDecimal.add(rBigDecimal));
			}
			else if (opSymbol.equals(OP_SYMBOL_MINUS)) {
			   result = new XSDecimal(lBigDecimal.subtract(rBigDecimal));				
			}
		}
		else if (lNumber.isXsDouble() && rNumber.isXsDouble()) {
			String lXsDoubleStr = (lNumber.getXsDouble()).stringValue();
			Double lDbl = Double.valueOf(lXsDoubleStr);
			String rXsDoubleStr = (rNumber.getXsDouble()).stringValue();
			Double rDbl = Double.valueOf(rXsDoubleStr);
			if (opSymbol.equals(OP_SYMBOL_PLUS)) {
			   result = new XSDouble(lDbl + rDbl);
			}
			else if (opSymbol.equals(OP_SYMBOL_MINUS)) {
			   result = new XSDouble(lDbl - rDbl);				
			}
		}
		else if (lNumber.isXsInteger() && rNumber.isXsDecimal()) {
			String lXsIntegerStr = (lNumber.getXsInteger()).stringValue();
			BigDecimal lBigDecimal = new BigDecimal(lXsIntegerStr);
			String rXsDecimalStr = (rNumber.getXsDecimal()).stringValue();
			BigDecimal rBigDecimal = new BigDecimal(rXsDecimalStr);
			if (opSymbol.equals(OP_SYMBOL_PLUS)) {
			   result = new XSDecimal(lBigDecimal.add(rBigDecimal));
			}
			else if (opSymbol.equals(OP_SYMBOL_MINUS)) {
			   result = new XSDecimal(lBigDecimal.subtract(rBigDecimal));				
			} 
		}
		else if (lNumber.isXsInteger() && rNumber.isXsDouble()) {
			String lXsIntegerStr = (lNumber.getXsInteger()).stringValue();
			BigDecimal lBigDecimal = new BigDecimal(lXsIntegerStr);
			String rXsDoubleStr = (rNumber.getXsDouble()).stringValue();
			BigDecimal rBigDecimal = new BigDecimal(rXsDoubleStr);
			if (opSymbol.equals(OP_SYMBOL_PLUS)) {
			   result = new XSDecimal(lBigDecimal.add(rBigDecimal));
			}
			else if (opSymbol.equals(OP_SYMBOL_MINUS)) {
			   result = new XSDecimal(lBigDecimal.subtract(rBigDecimal));				
			}
		}
		else if (lNumber.isXsDecimal() && rNumber.isXsInteger()) {
			String lXsDecimalStr = (lNumber.getXsDecimal()).stringValue();
			BigDecimal lBigDecimal = new BigDecimal(lXsDecimalStr);
			String rXsIntegerStr = (rNumber.getXsInteger()).stringValue();
			BigDecimal rBigDecimal = new BigDecimal(rXsIntegerStr); 		 
			if (opSymbol.equals(OP_SYMBOL_PLUS)) {
			   result = new XSDecimal(lBigDecimal.add(rBigDecimal));
			}
			else if (opSymbol.equals(OP_SYMBOL_MINUS)) {
			   result = new XSDecimal(lBigDecimal.subtract(rBigDecimal));				
			} 
		}
		else if (lNumber.isXsDecimal() && rNumber.isXsDouble()) {
			String lXsDecimalStr = (lNumber.getXsDecimal()).stringValue();
			BigDecimal lBigDecimal = new BigDecimal(lXsDecimalStr);
			String rXsDoubleStr = (rNumber.getXsDouble()).stringValue();
			BigDecimal rBigDecimal = new BigDecimal(rXsDoubleStr);
			if (opSymbol.equals(OP_SYMBOL_PLUS)) {
			   result = new XSDecimal(lBigDecimal.add(rBigDecimal));
			}
			else if (opSymbol.equals(OP_SYMBOL_MINUS)) {
			   result = new XSDecimal(lBigDecimal.subtract(rBigDecimal));				
			}
		}
		else if (lNumber.isXsDouble() && rNumber.isXsInteger()) {
			String lXsDoubleStr = (lNumber.getXsDouble()).stringValue();
			BigDecimal lBigDecimal = new BigDecimal(lXsDoubleStr);
			String rXsIntegerStr = (rNumber.getXsInteger()).stringValue();
			BigDecimal rBigDecimal = new BigDecimal(rXsIntegerStr);
			if (opSymbol.equals(OP_SYMBOL_PLUS)) {
			   result = new XSDecimal(lBigDecimal.add(rBigDecimal));
			}
			else if (opSymbol.equals(OP_SYMBOL_MINUS)) {
			   result = new XSDecimal(lBigDecimal.subtract(rBigDecimal));				
			}
		}      
		else if (lNumber.isXsDouble() && rNumber.isXsDecimal()) {
			String lXsDoubleStr = (lNumber.getXsDouble()).stringValue();
			BigDecimal lBigDecimal = new BigDecimal(lXsDoubleStr);
			String rXsDecimalStr = (rNumber.getXsDecimal()).stringValue();
			BigDecimal rBigDecimal = new BigDecimal(rXsDecimalStr);			
			if (opSymbol.equals(OP_SYMBOL_PLUS)) {
			   result = new XSDecimal(lBigDecimal.add(rBigDecimal));
			}
			else if (opSymbol.equals(OP_SYMBOL_MINUS)) {
			   result = new XSDecimal(lBigDecimal.subtract(rBigDecimal));				
			}
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

}
