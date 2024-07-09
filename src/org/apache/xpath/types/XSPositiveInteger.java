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

/**
 * Implementation of XML Schema data type xs:positiveInteger.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 */
public class XSPositiveInteger extends XSNonNegativeInteger {

	private static final long serialVersionUID = 8197349050098829466L;

	/**
	 * Class constructor.
	 */
	public XSPositiveInteger() {
	   // NO OP	
	}
	
	/**
	 * Class constructor.
	 */
	public XSPositiveInteger(BigInteger val) throws TransformerException {
	   super(val);
	   int cmprResult = val.compareTo(BigInteger.valueOf(1));
	   if (cmprResult == -1) {
		  throw new TransformerException("FOCA0003 : An xs:positiveInteger value cannot be less than 1.");  
	   }			   	
	}
	
	/**
	 * Class constructor.
	 */
	public XSPositiveInteger(String val) throws TransformerException {
	   super(val);
	   BigInteger bigInt = (getValue()).toBigInteger(); 
	   int cmprResult = bigInt.compareTo(BigInteger.valueOf(1));
	   if (cmprResult == -1) {
		  throw new TransformerException("FOCA0003 : An xs:positiveInteger value cannot be less than 1.");  
	   }
	}
	
	public ResultSequence constructor(ResultSequence seq) {
		ResultSequence result = new ResultSequence();
		
		XObject xObj = seq.item(0);
		String strVal = XslTransformEvaluationHelper.getStrVal(xObj);
		try {
			XSPositiveInteger xsPositiveInteger = new XSPositiveInteger(strVal);
			result.add(xsPositiveInteger);
		} catch (TransformerException ex) {
			// NO OP
		}
		
		return result;
	}	
	
	public int getType() {
	   return CLASS_POSITIVE_INTEGER;
	}

}
