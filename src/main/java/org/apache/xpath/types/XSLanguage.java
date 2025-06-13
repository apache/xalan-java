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
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.regex.Matcher;
import org.apache.xpath.regex.Pattern;

import xml.xpath31.processor.types.XSToken;

/**
 * Implementation of XML Schema data type xs:language.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 */
public class XSLanguage extends XSToken {

	private static final long serialVersionUID = 1905351155203382706L;
	
    private String m_languageStrValue;
	
	private static final String XS_LANGUAGE = "xs:language";
	
	/**
	 * Default constructor.
	 */
	public XSLanguage() {
		// NO OP
	}
	
	/**
	 * Class constructor.
	 * 
	 * (The regex for xs:language's string value is specified within XML Schema 
	 *  datatypes specification)
	 */
	public XSLanguage(String xsLanguageStrValue) throws TransformerException {
		Pattern pattern = Pattern.compile("[a-zA-Z]{1,8}(-[a-zA-Z0-9]{1,8})*");
		Matcher matcher = pattern.matcher(xsLanguageStrValue);
		if (!matcher.matches()) {
			throw new TransformerException("FORG0001 : A string value " + xsLanguageStrValue + " doesn't conform "
					                                                                         + "to xs:language's value and lexical space.");	
		}
		m_languageStrValue = xsLanguageStrValue;
	}
	
	@Override
	public String stringType() {
		return XS_LANGUAGE;
	}
	
	public ResultSequence constructor(ResultSequence seq) {
		ResultSequence result = new ResultSequence();
		
		XObject xObj = seq.item(0);
		String strVal = XslTransformEvaluationHelper.getStrVal(xObj);
		try {
			XSLanguage xsLanguage = new XSLanguage(strVal);
			result.add(xsLanguage);
		} catch (TransformerException ex) {
			// NO OP
		}
		
		return result;
	}
	
	@Override
	public String stringValue() {
		return m_languageStrValue;
	}
	
	public int getType() {
		return CLASS_LANGUAGE;
	}
	
	/**
	 * Implementation of operation equals, for the type xs:language.
	 */
	public boolean eq(XSLanguage obj2) {		
		boolean result = false;
		
		result = (this.m_languageStrValue).equals(obj2.stringValue()); 
		
		return result;
	}
	
	/**
	 * Implementation of operation not equals, for the type xs:language.
	 */
	public boolean ne(XSLanguage obj2) {
		boolean result = false;
		
		result = !eq(obj2);
		
		return result;
	}
	
	@Override
	public String typeName() {
		return "language";
	}

}
