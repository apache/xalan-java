/*
 * The Apache Software License, Version 1.1
 * 
 * Copyright (c) 2002-2003 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowledgment: "This product includes software developed by the Apache
 * Software Foundation (http://www.apache.org/)." Alternately, this
 * acknowledgment may appear in the software itself, if and wherever such
 * third-party acknowledgments normally appear. 4. The names "Xalan" and
 * "Apache Software Foundation" must not be used to endorse or promote products
 * derived from this software without prior written permission. For written
 * permission, please contact apache@apache.org. 5. Products derived from this
 * software may not be called "Apache", nor may "Apache" appear in their name,
 * without prior written permission of the Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the Apache Software Foundation and was originally based on
 * software copyright (c) 2002, International Business Machines Corporation.,
 * http://www.ibm.com. For more information on the Apache Software Foundation,
 * please see <http://www.apache.org/> .
 */
package org.apache.xpath.expression;

import org.apache.xpath.datamodel.SingleType;

/**
 * Represents <em>cast as</em> and <em>castable as</em> expressions. Use
 * the method {@link #getExprType()}to differentiate <em>castable as</em>
 * from <em>cast as</em> expressions.
 * 
 * @see <a target="_top" href="http://www.w3.org/TR/xpath20/#id-cast">XPath
 *      2.0 specification</a>
 * @author <a href="mailto:villard@us.ibm.com>Lionel Villard</a>
 * @version $Id$
 */
public interface CastableOrCastExpr extends Expr
{

	/**
	 * Gets the expression to <em>cast as</em> or <em>castable as</em>
	 * 
	 * @return An expression
	 */
	Expr getExpr();

	/**
	 * Replace the current expression to <em>cast as</em> or <em>castable as</em>
	 * by the specified one.
	 * <p>
	 * If the specified expression belongs to another expression, it is
	 * duplicated by following the same rules as {@link Expr#cloneExpression()}
	 * before its insertion.
	 * </p>
	 * 
	 * @param expr
	 *            The new expression to cast as or castable as
	 * @return The expression that has just been added or a clone.
	 */
	Expr replaceExpr(Expr expr);

	/**
	 * Gets the type to <em>cast as</em> or <em>castable as</em>
	 * 
	 * @return A single type
	 */
	SingleType getSingleType();

	/**
	 * Replace the current single type which <em>cast as</em> or <em>castable as</em>
	 * is applied on by the specified one.
	 * <p>
	 * If the specified type belongs to another expression, it is
	 * duplicated by following the same rules as {@link Expr#cloneExpression()}
	 * before its insertion.
	 * </p>
	 * 
	 * @param type
	 *            The type to replace with
	 * @return The type that has just been added or a clone.
	 */
	SingleType replaceSingleType(SingleType type);

}
