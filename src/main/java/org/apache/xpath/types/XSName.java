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

import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.serializer.utils.XML11Char;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSToken;

/**
 * Implementation of XML Schema data type xs:Name.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 */
public class XSName extends XSToken {

	private static final long serialVersionUID = 6658600221262283990L;
	
	private String m_name = null;
	
	private static final String XS_NAME = "xs:Name";
	
	/**
	 * Default constructor.
	 */
	public XSName() {
	   // NO OP	
	}
	
	/**
	 * Class constructor.
	 */
	public XSName(String strValue) throws TransformerException {		
		if (XML11Char.isXML11ValidName(strValue)) {
			m_name = strValue;
		}
		else {
			throw new TransformerException("XTTE0570 : The string value '" + strValue + "' is not a valid XML 1.1 name.");
		}
	}
	
	@Override
	public String stringType() {
		return XS_NAME;
	}
	
	public ResultSequence constructor(ResultSequence seq) {
		ResultSequence result = new ResultSequence();
		
		XObject xObj = seq.item(0);
		String strVal = XslTransformEvaluationHelper.getStrVal(xObj);
		try {
			XSName xsName = new XSName(strVal);
			result.add(xsName);
		} catch (TransformerException ex) {
			// NO OP
		}
		
		return result;
	}
	
	public String stringValue() {
		return m_name;
	}
	
	public int getType() {
		return CLASS_NAME;
	}
	
	/**
	 * Implementation of operation equals, for the type xs:Name.
	 */
	public boolean eq(XSName obj2) {
		boolean result = false;

		result = m_name.equals(obj2.stringValue());

		return result;
	}
	
	/**
	 * Implementation of operation not equals, for the type xs:Name.
	 */
	public boolean ne(XSName obj2) {
        boolean result = false;
		
		result = !eq(obj2);
		
		return result;
	}
	
	@Override
	public String typeName() {
		return "Name";
	}

}
