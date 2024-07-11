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
import org.apache.xpath.composite.SequenceTypeSupport.XmlSchemaBuiltinNumericType;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

/**
 * Implementation of XML Schema data type xs:unsignedInt.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 */
public class XSUnsignedInt extends XSUnsignedLong {

	private static final long serialVersionUID = -5195115298409745404L;

	/**
	 * Class constructor.
	 */
	public XSUnsignedInt() {
	   // NO OP	
	}
	
	/**
	 * Class constructor.
	 */
	public XSUnsignedInt(BigInteger val) throws TransformerException {
	   super(val);
	   int cmprResult1 = val.compareTo(BigInteger.valueOf(XmlSchemaBuiltinNumericType.UnsignedInt.MIN_INCLUSIVE));
	   int cmprResult2 = val.compareTo(BigInteger.valueOf(XmlSchemaBuiltinNumericType.UnsignedInt.MAX_INCLUSIVE));
	   if ((cmprResult1 == -1) || (cmprResult2 == 1)) {
		  throw new TransformerException("FOCA0003 : An xs:unsignedInt value's numeric range is [" + XmlSchemaBuiltinNumericType.UnsignedInt.MIN_INCLUSIVE + 
				                                                   ", " + XmlSchemaBuiltinNumericType.UnsignedInt.MAX_INCLUSIVE + "] with both inclusive.");  
	   }			   	
	}
	
	/**
	 * Class constructor.
	 */
	public XSUnsignedInt(String val) throws TransformerException {
	   super(val);
	   BigInteger bigIntArg = new BigInteger(val);
	   int cmprResult1 = bigIntArg.compareTo(BigInteger.valueOf(XmlSchemaBuiltinNumericType.UnsignedInt.MIN_INCLUSIVE));
	   int cmprResult2 = bigIntArg.compareTo(BigInteger.valueOf(XmlSchemaBuiltinNumericType.UnsignedInt.MAX_INCLUSIVE));
	   if ((cmprResult1 == -1) || (cmprResult2 == 1)) {
		  throw new TransformerException("FOCA0003 : An xs:unsignedInt value's numeric range is [" + XmlSchemaBuiltinNumericType.UnsignedInt.MIN_INCLUSIVE + 
					                                              ", " + XmlSchemaBuiltinNumericType.UnsignedInt.MAX_INCLUSIVE + "] with both inclusive.");  
	   }
	}
	
	public ResultSequence constructor(ResultSequence seq) {
		ResultSequence result = new ResultSequence();
		
		XObject xObj = seq.item(0);
		String strVal = XslTransformEvaluationHelper.getStrVal(xObj);
		try {
			XSUnsignedInt xsUnsignedInt = new XSUnsignedInt(strVal);
			result.add(xsUnsignedInt);
		} catch (TransformerException ex) {
			// NO OP
		}
		
		return result;
	}	
	
	public int getType() {
	   return CLASS_UNSIGNED_INT;
	}

}
