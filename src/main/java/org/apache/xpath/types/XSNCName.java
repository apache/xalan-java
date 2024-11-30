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

import org.apache.xerces.util.XMLChar;

/**
 * Implementation of XML Schema data type xs:NCName.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 */
public class XSNCName extends XSName {
	
	private static final long serialVersionUID = 576149921912815294L;
	
	private String fNcNameStr = null;
	
	public XSNCName() {
	   // NO OP	
	}
	
	public void setStrVal(String str) throws TransformerException {
		if (isValidNCName(str)) {
		   fNcNameStr = str;
		}
		else {
		   throw new TransformerException("The string value '" + str + "' is not a valid XML NCName according "
		   		                             + "to 'XML namespaces 1.0 recommendation'.");
		}
	}
	
	public String stringValue() {
		return fNcNameStr;
	}

	/*
	 * This method determines whether, a string represents a valid XML NCName 
	 * according to 'XML namespaces 1.0 recommendation'.
	 */
	private boolean isValidNCName(String str) {
	   boolean isValidXMLNCName = XMLChar.isValidNCName(str);	   
	   return isValidXMLNCName;
	}

}
