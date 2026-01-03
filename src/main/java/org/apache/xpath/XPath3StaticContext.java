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
package org.apache.xpath;

import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.functions.XSL3FunctionService;
import org.apache.xpath.functions.XSLFunctionBuilder;

/**
 * A class definition, that implements XPath 3.1 expression evaluation's 
 * static context.
 * 
 * Please refer to, XPath 3.1 recommendation section, '2.1.1 Static Context'.
 * 
 * @author Scott Boag <scott_boag@us.ibm.com>
 * @author Gary L Peskin <garyp@apache.org>
 * @author Myriam Midy <mmidy@apache.org>
 * 
 * @author Joseph Kesselman <keshlam@alum.mit.edu>
 * @author Morris Kwan <mkwan@apache.org>, Ilene Seelemann <ilene@apache.org>, Henry Zongaro <zongaro@ca.ibm.com>,
 *         Brian Minchau <minchau@apache.org>, Sarah McNamara <mcnamara@apache.org>
 *         
 * @author Mukul Gandhi <mukulg@apache.org>
 *         (XPath 3.1 specific changes, to this class)
 * 
 * @xsl.usage general
 */
public class XPath3StaticContext extends XPath3DynamicContext {
	
	/**
	 * XPath 3.1 built-in functions namespace uri, for most of the functions
	 * available to XPath 3.1 language users. The XPath functions available within
	 * this namespace, may be used without binding the function name with an XML 
	 * namespace, or binding with a non-null XML namespace (the commonly used XML 
	 * namespace prefix for this namespace uri is "fn", as specified by
	 * XPath 3.1 spec).
	 */
	public static final String XPATH_BUILT_IN_FUNCS_NS_URI = "http://www.w3.org/2005/xpath-functions";
	
	/**
	 * XPath 3.1 built-in functions namespace uri, for maths trigonometric and exponential 
	 * functions. The XPath functions available within this namespace, must be used by 
	 * qualifying the function name with an XML namespace bound to this uri (the commonly 
	 * used XML namespace prefix for this namespace uri is "math", as specified by 
	 * XPath 3.1 spec).
	 */
	public static final String XPATH_BUILT_IN_MATH_FUNCS_NS_URI = "http://www.w3.org/2005/xpath-functions/math";
	
	/**
	 * XPath 3.1 built-in functions namespace uri, for map related functions. The XPath functions available 
	 * within this namespace, must be used by qualifying the function name with an XML namespace bound to this 
	 * uri (the commonly used XML namespace prefix for this namespace uri is "map", as specified by 
	 * XPath 3.1 spec).
	 */
	public static final String XPATH_BUILT_IN_MAP_FUNCS_NS_URI = "http://www.w3.org/2005/xpath-functions/map";
	
	/**
	 * XPath 3.1 built-in functions namespace uri, for array related functions. The XPath functions available 
	 * within this namespace, must be used by qualifying the function name with an XML namespace bound to this 
	 * uri (the commonly used XML namespace prefix for this namespace uri is "array", as specified by 
	 * XPath 3.1 spec).
	 */
	public static final String XPATH_BUILT_IN_ARRAY_FUNCS_NS_URI = "http://www.w3.org/2005/xpath-functions/array";
	
	/**
	 * The default collation uri (the default collation for Xalan-J's XSL 3 support 
	 * is, "Unicode Codepoint Collation").
	 */
	private String m_default_collation = XPathCollationSupport.UNICODE_CODEPOINT_COLLATION_URI;

	/**
	 * An XPathCollationSupport object instance to support, collations within XPath implementation.
	 */
	private XPathCollationSupport m_collationSupport = new XPathCollationSupport(m_default_collation);

	public XPathCollationSupport getXPathCollationSupport() {
		return m_collationSupport;
	}

	public String getDefaultCollation() {
		return m_default_collation;
	}
	
	public FunctionTable getFunctionTable() {
		return new FunctionTable();
	}

	public XSL3FunctionService getXSLFunctionService() {
		return XSLFunctionBuilder.getXSLFunctionService(); 
	}

}
