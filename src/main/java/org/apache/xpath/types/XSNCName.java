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

/**
 * Implementation of XML Schema data type xs:NCName.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 */
public class XSNCName extends XSName {
	
	private static final long serialVersionUID = 576149921912815294L;
	
    private String m_ncName = null;
	
	private static final String XS_NCNAME = "xs:NCName";
	
	/**
	 * Default constructor.
	 */
	public XSNCName() {
	   // no op	
	}
	
	/**
	 * Class constructor.
	 */
	public XSNCName(String strValue) throws TransformerException {		
		if (XML11Char.isXML11ValidNCName(strValue)) {
			m_ncName = strValue;
		}
		else {
			throw new TransformerException("XTTE0570 : The string value '" + strValue + "' is not a valid XML 1.1 NCName.");
		}
	}
	
	@Override
	public String stringType() {
		return XS_NCNAME;
	}
	
	public ResultSequence constructor(ResultSequence seq) {
		ResultSequence result = new ResultSequence();
		
		XObject xObj = seq.item(0);
		String strVal = XslTransformEvaluationHelper.getStrVal(xObj);
		try {
			XSNCName xsNcName = new XSNCName(strVal);
			result.add(xsNcName);
		} catch (TransformerException ex) {
			// no op
		}
		
		return result;
	}
	
	public String stringValue() {
		return m_ncName;
	}
	
	public int getType() {
		return CLASS_NCNAME;
	}
	
	/**
	 * Implementation of operation equals, for the type xs:NCName.
	 */
	public boolean eq(XSNCName obj2) {
		boolean result = false;

		result = m_ncName.equals(obj2.stringValue());

		return result;
	}
	
	/**
	 * Implementation of operation not equals, for the type xs:NCName.
	 */
	public boolean ne(XSNCName obj2) {
        boolean result = false;
		
		result = !eq(obj2);
		
		return result;
	}
	
	@Override
	public String typeName() {
		return "NCName";
	}

}
