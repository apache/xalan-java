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

import org.apache.xalan.templates.ElemFunction;
import org.apache.xpath.functions.Function;

/**
 * A class definition, encapsulating either xsl:function reference, 
 * or XPath 3.1 built-in function reference.
 * 
 * There are other types of XPath 3.1 function items as well, 
 * that're represented by their own classes.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPath3FunctionItem extends XObject {
	
	private static final long serialVersionUID = 1324050607867572996L;
	
	/**
	 * An xsl:function reference, funtion item.
	 */
	private ElemFunction m_elemFunction = null;
	
	/**
	 * An XPath 3.1 built-in function reference, function item.
	 */
	private Function m_function = null;
	
	/**
	 * Class constructor.
	 * 
	 * @param elemFunction
	 */
	public XPath3FunctionItem(ElemFunction elemFunction) {
		m_elemFunction = elemFunction;
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param function
	 */
	public XPath3FunctionItem(Function function) {
		m_function = function;
	}
	
	public ElemFunction getElemFunction() {
		return m_elemFunction;
	}
	
	public void setElemFunction(ElemFunction elemFunction) {
		m_elemFunction = elemFunction; 
	}
	
	public Function getFunction() {
		return m_function;
	}
	
	public void setFunction(Function function) {
		m_function = function; 
	}

}
