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
 * originally based on software copyright (c) 2002, International
 * Business Machines Corporation., http://www.ibm.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xpath;

import org.apache.xpath.expression.ExpressionFactory;
import org.apache.xpath.impl.XPathFactoryImpl;

/**
 * Factory model for XPath expression manipulation.
 * Use <code>newExpressionFactory</code> in order to get a factory 
 * for creating and updating XPath expressions.
 * 
 * @author villard@us.ibm.com
 * @author shane_curcuru@lotus.com
 */
public abstract class XPathFactory {

	public static final String XPATH_FACTORY_KEY = "org.apache.xpath.XPathFactory";

    /** 
     * Get an XPathFactory, using a lookup or a fallback.  
     * 
     * TODO: investigate using JAXP FactoryFinder-like mechanisim 
     * or something more sophisticated than System.getProperty().
     */ 
	public static XPathFactory newInstance() {
		// Basic implementation finder: use system properties
		String className = System.getProperty(XPATH_FACTORY_KEY);

		if (className != null) {
			try {
				Class factory = Class.forName(className);

				return (XPathFactory) factory.newInstance();
			} catch (ClassNotFoundException e) {
				System.err.println("DEBUG-FillInErrorHandling: " + e.toString());
			} catch (InstantiationException e) {
				System.err.println("DEBUG-FillInErrorHandling: " + e.toString());
			} catch (IllegalAccessException e) {
				System.err.println("DEBUG-FillInErrorHandling: " + e.toString());
			}
		}

		return new XPathFactoryImpl();
	}

	/**
	 * Create a new expression factory.
	 * @see org.apache.xpath.expression.ExpressionFactory
	 */
	public abstract ExpressionFactory newExpressionFactory();

	/* public abstract ItemFactory newItemFactory(); */

	/* public abstract EvaluatorFactory newEvaluatorFactory(); */

}
