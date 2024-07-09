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
package org.apache.xpath.types;

import java.math.BigInteger;

import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSInt;
import xml.xpath31.processor.types.XSNumericType;

/**
 * Implementation of XML Schema data type xs:short.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 */
public class XSShort extends XSInt {

	private static final long serialVersionUID = -4155842894636269334L;
	
	private static final int MIN_INCLUSIVE = -32768;
	
	private static final int MAX_INCLUSIVE = 32767;

	/**
	 * Class constructor.
	 */
	public XSShort() {
	   // NO OP	
	}
	
	/**
	 * Class constructor.
	 */
	public XSShort(BigInteger val) throws TransformerException {
	   super(val);
	   int cmprResult1 = val.compareTo(BigInteger.valueOf(MIN_INCLUSIVE));
	   int cmprResult2 = val.compareTo(BigInteger.valueOf(MAX_INCLUSIVE));
	   if ((cmprResult1 == -1) || (cmprResult2 == 1)) {
		  throw new TransformerException("FOCA0003 : An xs:short value's numeric range is [" + MIN_INCLUSIVE + 
				                                                     ", " + MAX_INCLUSIVE + "] with both inclusive.");  
	   }			   	
	}
	
	/**
	 * Class constructor.
	 */
	public XSShort(String val) throws TransformerException {
	   super(val);
	   BigInteger bigInt = (getValue()).toBigInteger(); 
	   int cmprResult1 = bigInt.compareTo(BigInteger.valueOf(MIN_INCLUSIVE));
	   int cmprResult2 = bigInt.compareTo(BigInteger.valueOf(MAX_INCLUSIVE));
	   if ((cmprResult1 == -1) || (cmprResult2 == 1)) {
		  throw new TransformerException("FOCA0003 : An xs:short value's numeric range is [" + MIN_INCLUSIVE + 
				                                                     ", " + MAX_INCLUSIVE + "] with both inclusive.");  
	   }
	}
	
	public ResultSequence constructor(ResultSequence seq) {
		ResultSequence result = new ResultSequence();
		
		XObject xObj = seq.item(0);
		String strVal = XslTransformEvaluationHelper.getStrVal(xObj);
		try {
			XSShort xsShort = new XSShort(strVal);
			result.add(xsShort);
		} catch (TransformerException ex) {
			// NO OP
		}
		
		return result;
	}
	
	public String stringValue() {
		return super.stringValue();
	}
	
	public BigInteger intValue() {
		return super.intValue();
	}
		
	public boolean equals(XSNumericType val) {
		boolean result = false;
		   
		result = ((intValue()).longValue() == getDoubleFromXsNumericType(val));
		   
		return result;
	}
		
	public boolean lt(XSNumericType val) {
		boolean result = false;
			   
		result = ((intValue()).longValue() < getDoubleFromXsNumericType(val));
			   
		return result;
	}
		
	public boolean gt(XSNumericType val) {
		boolean result = false;
				   
		result = ((intValue()).longValue() > getDoubleFromXsNumericType(val));
				   
		return result;
	}
		
	public XSDouble multiply(XSNumericType val) {
		XSDouble result = null;
					   
		double resultDbl = ((intValue()).longValue() * getDoubleFromXsNumericType(val));	   
		result = new XSDouble(resultDbl);
					   
		return result;
	}
		
    public int getType() {
		return CLASS_SHORT;
	}
		
    /**
	 * Get double primitive value from XSNumericType.
	*/
	private double getDoubleFromXsNumericType(XSNumericType val) {
		double result = 0.0;
		   
		String argStrVal = val.stringValue();
		result = (Double.valueOf(argStrVal)).doubleValue();
		   
		return result;
	}
	
}
