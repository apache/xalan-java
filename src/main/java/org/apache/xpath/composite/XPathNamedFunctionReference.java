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
package org.apache.xpath.composite;

import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.ElemFunction;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.XObject;

/**
 * An XPath parser, constructs an object instance of this class
 * to represent a named function reference expression.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathNamedFunctionReference extends XObject {

	private static final long serialVersionUID = -2448144139309343329L;
	
	/**
	 * Function's name.
	 */
	private String m_funcName = null;
	
	/**
	 * Namespace of the function.
	 */
	private String m_funcNamespace = null;
	
	/**
	 * Function's arity value.
	 */
	private Short m_arity;
	
	/**
	 * An XSL stylesheet function object reference.
	 */
	private ElemFunction m_elemFunction = null;
	
	/**
	 * An XSL StylesheetRoot object reference.
	 */
	private StylesheetRoot m_stylesheetRoot = null;
	

	@Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
		// NO OP
		return null;
    }

	public String getFuncNamespace() {
		return m_funcNamespace;
	}

	public void setFuncNamespace(String funcNamespace) {
		this.m_funcNamespace = funcNamespace;
	}

	public String getFuncName() {
		return m_funcName;
	}

	public void setFuncName(String funcName) {
		this.m_funcName = funcName;
	}

	public Short getArity() {
		return m_arity;
	}

	public void setArity(Short arity) {
		this.m_arity = arity;
	}
	
	public ElemFunction getXslStylesheetFunction() {
		return m_elemFunction; 
	}
	
	public void setXslStylesheetFunction(ElemFunction elemFunction, StylesheetRoot stylesheetRoot) {
		m_elemFunction = elemFunction;
		m_stylesheetRoot = stylesheetRoot;
	}
	
	public StylesheetRoot getXslStylesheetRoot() {
		return m_stylesheetRoot; 
	}
	
	public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
        // NO OP
    }
    
    public int getType() {
        return CLASS_FUNCTION_ITEM;
    }

}
