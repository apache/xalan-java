/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xpath.objects;

import javax.xml.transform.TransformerException;

import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.XML11Char;

/**
 * Class definition, to represent an XPath 3.1 EQName,
 * object instance.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathEQname extends QName {

	private static final long serialVersionUID = 661967149708493549L;
	
	/**
	 * The string value representation, for XPath 3.1 EQName
	 * that represents the names of XML elements, attributes, 
	 * functions, variables, types.
	 */
	private String m_eqNameStr = null;
	
	/**
	 * Class constructor.
	 */
	public XPathEQname(String eqNameStr) {
	   m_eqNameStr = eqNameStr; 
	}
	
	/**
	 * Method definition, to parse the supplied XPath 3.1 EQName 
	 * string value, to the org.apache.xml.utils.QName object 
	 * instance. 
	 * 
	 * @param eqNameStr                         The supplied XPath 3.1 EQName 
	 *                                          string value. 
	 * @param prefixResolver                    An object instance that resolves,
	 *                                          prefixes to namespaces within XPath
	 *                                          expressions.
	 * @return
	 * @throws TransformerException
	 */
	public static QName parseEQname(String eqNameStr, PrefixResolver prefixResolver) 
																				   throws TransformerException {
	   
	   QName result = null;
	   
	   try {	   
		   if (eqNameStr != null) {
			   if (eqNameStr.startsWith("Q")) {
				   int idx1 = eqNameStr.indexOf('{');  
				   int idx2 = eqNameStr.lastIndexOf('}');

				   if (idx2 > idx1) {
					   String ncNameStr = eqNameStr.substring(idx2 + 1);
					   
					   if (XML11Char.isXML11ValidNCName(ncNameStr)) {                                                    
                          String localName = ncNameStr;
                          
                          String nsUri = null;
                          
                          if (idx2 > (idx1 + 1)) {
                        	 nsUri = eqNameStr.substring(idx1 + 1, idx2);
                          }
                          
                          result = new QName(nsUri, localName);
					   }
					   else {
						   throw new TransformerException("XPST0003 : An XPath 3.1 parse error has occured, while parse for the supplied EQName "
																									       + "string value '" + eqNameStr + "'. "
																									       + "An invalid NCName string value '" + ncNameStr + "' "
																									       + "occurs after the substring Q{...}."); 
					   }
				   }
				   else {
					   throw new TransformerException("XPST0003 : An XPath 3.1 parse error has occured, while "
																										   + "parse for the supplied EQName "
																										   + "string value '" + eqNameStr + "'.");
				   }
			   }
			   else {				   
				   result = new QName(eqNameStr, prefixResolver); 
			   }
		   }
	   }
	   catch (Exception ex) {
		   throw new TransformerException("XPST0003 : An XPath 3.1 parse error has occured, while parse for the supplied EQName "
		   		                                                                                                        + "string value '" + eqNameStr + "'."); 
	   }
	   
	   return result;
	}

}
