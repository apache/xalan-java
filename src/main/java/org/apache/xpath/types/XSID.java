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
 * Implementation of XML Schema data type xs:ID.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 */
public class XSID extends XSNCName {
	
	private static final long serialVersionUID = 8985003138755990758L;

	private String m_id = null;
	
	private static final String XS_ID = "xs:ID";
	
	/**
	 * Default constructor.
	 */
	public XSID() {
	   // NO OP	
	}
	
	/**
	 * Class constructor.
	 */
	public XSID(String strValue) throws TransformerException {		
		if (XML11Char.isXML11ValidNCName(strValue)) {
			m_id = strValue;
		}
		else {
			throw new TransformerException("XTTE0570 : The string value '" + strValue + "' is not a valid xs:ID value.");
		}
	}
	
	@Override
	public String stringType() {
		return XS_ID;
	}
	
	public ResultSequence constructor(ResultSequence seq) {
		ResultSequence result = new ResultSequence();
		
		XObject xObj = seq.item(0);
		String strVal = XslTransformEvaluationHelper.getStrVal(xObj);
		try {
			XSID xsId = new XSID(strVal);
			result.add(xsId);
		} catch (TransformerException ex) {
			// NO OP
		}
		
		return result;
	}
	
	public String stringValue() {
		return m_id;
	}
	
	public int getType() {
		return CLASS_ID;
	}
	
	/**
	 * Implementation of operation equals, for the type xs:ID.
	 */
	public boolean eq(XSID obj2) {
		boolean result = false;

		result = m_id.equals(obj2.stringValue());

		return result;
	}
	
	/**
	 * Implementation of operation not equals, for the type xs:ID.
	 */
	public boolean ne(XSID obj2) {
        boolean result = false;
		
		result = !eq(obj2);
		
		return result;
	}
	
	@Override
	public String typeName() {
		return "ID";
	}

}
