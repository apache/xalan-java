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
package org.apache.xpath.test;

//import org.apache.xpath.XPath20Exception;
//import org.apache.xpath.expression.Expr;
//import org.apache.xpath.expression.ExprContext;
//import org.apache.xpath.impl.ExprContextImpl;

/**
 * Sample Evaluation wrapper for using old Xalan data model.  
 * Placed in test package because DynamicContext design not finished.
 */
public class XalanEvaluator {

//	/**
//     * Just return a default ExprContextImpl.
//	 */
//	public ExprContextImpl createExprContext() {
//		return new ExprContextImpl();
//	}
//
//	/**
//	 * Evaluate an XPath using Xalan's old XPathAPI.
//     * //@TODO change to use ctx.getDynamicContext() once designed.
//	 */
//	public Object evaluate(ExprContextImpl ctx, Expr expr) throws XPath20Exception {
////		try {
////			XObject xobj = XPathAPI.eval((org.w3c.dom.Node) ctx.getContextItem(), expr.getString(true));
////			return xobj;
////		} catch (TransformerException e) {
////			throw new XPathException(e);
////		}
//		return null;
//	}
//
//	/**
//	 * Evaluate an match pattern using Xalan's old XPathAPI.
//     * //@TODO not implemented
//	 */
//	public boolean match(ExprContext ctx, Expr expr, Object node) throws XPath20Exception {
//		return false;
//	}

}
