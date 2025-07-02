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
package org.apache.xpath.composite;

import java.util.List;
import java.util.Vector;

import javax.xml.transform.TransformerException;

import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.XObject;

/**
 * A class definition providing support for XPath parse of 
 * function call syntax, like prefix:func1(args1)(args2). The 
 * list of string values within an object of this class, stores 
 * XPath expression strings for the function arguments args2. 
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathExprFuncCallExtendedArg extends Expression {

	private static final long serialVersionUID = -5784911363958937383L;
	
	private List<String> m_functionArgXPathExprStrList = null;

	@Override
	public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
		// NO OP
	}

	@Override
	public XObject execute(XPathContext xctxt) throws TransformerException {
		// NO OP
		return null;
	}

	@Override
	public void fixupVariables(Vector vars, int globalsSize) {
		// NO OP
	}

	@Override
	public boolean deepEquals(Expression expr) {
		// NO OP
		return false;
	}

	public List<String> getFunctionArgXPathExprStrList() {
		return m_functionArgXPathExprStrList;
	}

	public void setFunctionArgXPathExprStrList(List<String> functionArgXPathExprStrList) {
		this.m_functionArgXPathExprStrList = functionArgXPathExprStrList;
	}

}
