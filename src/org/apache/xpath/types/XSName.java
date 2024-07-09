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

import org.apache.xml.serializer.utils.XML11Char;

import xml.xpath31.processor.types.XSToken;

/**
 * Implementation of XML Schema data type xs:Name.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 */
public class XSName extends XSToken {

	private static final long serialVersionUID = 6658600221262283990L;
	
	private String fNameStr = null;
	
	public XSName() {
	   // NO OP	
	}
	
	public void setStrVal(String str) throws TransformerException {
		if (isValidXMLName(str)) {
		   fNameStr = str;
		}
		else {
		   throw new TransformerException("The string value '" + str + "' is not a valid XML 1.1 name.");
		}
	}
	
	public String stringValue() {
		return fNameStr;
	}

	/*
	 * This method determines whether, a string represents a valid 
	 * XML 1.1 name.
	 */
	private boolean isValidXMLName(String str) {
	   boolean isValidXMLName = true;
	   
	   for (int idx = 0; idx < str.length(); idx++) {
		  if (!XML11Char.isXML11Valid(str.indexOf(idx))) {
			 isValidXMLName = false;
			 break;
		  }
	   }
	   
	   return isValidXMLName;
	}

}
