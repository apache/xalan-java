/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xpath.rwapi.impl;

import java.util.Set;

import org.apache.xpath.rwapi.eval.DynamicContext;
import org.apache.xpath.rwapi.eval.NamespaceContext;
import org.apache.xpath.rwapi.expression.ExprContext;
import org.apache.xpath.rwapi.expression.StaticContext;

/**
 *
 */
public class ExprContextImpl implements ExprContext, DynamicContext, StaticContext {

    Object m_contextItem;

	/**
	 * @see org.apache.xpath.rwapi.expression.ExprContext#getDynamicContext()
	 */
	public DynamicContext getDynamicContext() {
		return this;
	}

	/**
	 * @see org.apache.xpath.rwapi.expression.ExprContext#getStaticContext()
	 */
	public StaticContext getStaticContext() {
		return this;
	}

	/**
	 * @see org.apache.xpath.rwapi.eval.DynamicContext#getContextItem()
	 */
	public Object getContextItem() {
		return m_contextItem;
	}

	/**
	 * @see org.apache.xpath.rwapi.eval.DynamicContext#setContextItem(Object)
	 */
	public void setContextItem(Object item) {
        m_contextItem = item;
	}

	/**
	 * @see org.apache.xpath.rwapi.expression.StaticContext#getExceptionPolicy()
	 */
	public short getExceptionPolicy() {
		return 0;
	}

	/**
	 * @see org.apache.xpath.rwapi.expression.StaticContext#getNamespaces()
	 */
	public NamespaceContext getNamespaces() {
		return null;
	}

	/**
	 * @see org.apache.xpath.rwapi.expression.StaticContext#getDefaultNamepaceForElement()
	 */
	public String getDefaultNamepaceForElement() {
		return null;
	}

	/**
	 * @see org.apache.xpath.rwapi.expression.StaticContext#getDefaultNamepaceforFunction()
	 */
	public String getDefaultNamepaceforFunction() {
		return null;
	}

	/**
	 * @see org.apache.xpath.rwapi.expression.StaticContext#getSchemaDefs()
	 */
	public void getSchemaDefs() {
	}

	/**
	 * @see org.apache.xpath.rwapi.expression.StaticContext#getVariables()
	 */
	public Set getVariables() {
		return null;
	}

	/**
	 * @see org.apache.xpath.rwapi.expression.StaticContext#getFunctions()
	 */
	public void getFunctions() {
	}

	/**
	 * @see org.apache.xpath.rwapi.expression.StaticContext#getCollations()
	 */
	public void getCollations() {
	}

	/**
	 * @see org.apache.xpath.rwapi.expression.StaticContext#getDefaultCollation()
	 */
	public void getDefaultCollation() {
	}

	/**
	 * @see org.apache.xpath.rwapi.expression.StaticContext#getBaseURI()
	 */
	public String getBaseURI() {
		return null;
	}

}
