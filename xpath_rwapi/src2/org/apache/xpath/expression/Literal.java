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
package org.apache.xpath.expression;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.xpath.XPathException;


/**
 * Represents literal expression type.
 * <pre>
 * [59]   Literal   ::=   NumericLiteral |  StringLiteral 
 * [58]   NumericLiteral   ::=   IntegerLiteral |  DecimalLiteral |  DoubleLiteral 
 * [1]   IntegerLiteral   ::=   Digits 
 * [2]   DecimalLiteral   ::=   ("." Digits) |  (Digits "." [0-9]*) 
 * [3]   DoubleLiteral   ::=   (("." Digits) |  (Digits ("." [0-9]*)?)) ("e" | "E") ("+" | "-")? Digits 
 * [4]   StringLiteral ::=   ('"' (('"' '"') |  [^"])* '"') |  ("'" (("'" "'") |  [^'])* "'")
 * </pre>
 * 
 * @see <a href="http://www.w3.org/TR/xpath20#id-literals">XPath 2.0
 *      Specification</a>
 */
public interface Literal extends Expr
{
    /**
     * This expression is an integer literal
     */
    short INTEGER_LITERAL = 0;

    /**
     * This expression is a decimal literal
     */
    short DECIMAL_LITERAL = 1;

    /**
     * This expression is an string literal
     */
    short STRING_LITERAL = 2;

    /**
     * This expression is an double literal
     */
    short DOUBLE_LITERAL = 3;

    /**
     * Gets the literal type
     *
     * @return short One of the four following literal type:
     *         <code>INTEGER_LITERAL</code>, <code>DECIMAL_LITERAL</code>,
     *         <code>STRING_LITERAL</code>, <code>DOUBLE_LITERAL</code>.
     */
    short getLiteralType();

    /**
     * Gets the integer literal
     *
     * @return DOCUMENT ME!
     *
     * @throws XPathException when the literal isn't an integer or cannot be
     *         represented by the primitive int type (in case of big integer)
     */
    int getIntegerLiteralAsInt() throws XPathException;

    /**
     * Gets the integer literal
     *
     * @return DOCUMENT ME!
     *
     * @throws XPathException when the literal isn't an integer
     */
    BigInteger getIntegerLiteral() throws XPathException;

    /**
     * Gets the decimal literal
     *
     * @return DOCUMENT ME!
     *
     * @throws XPathException when the literal isn't a decimal
     */
    BigDecimal getDecimalLiteral() throws XPathException;

    /**
     * Gets the decimal literal as a double
     *
     * @return DOCUMENT ME!
     *
     * @throws XPathException when the literal isn't a decimal
     */
    double getDecimalLiteralAsDouble() throws XPathException;

    /**
     * Gets the double literal
     *
     * @return DOCUMENT ME!
     *
     * @throws XPathException when the literal isn't a double
     */
    double getDoubleLiteral() throws XPathException;

    /**
     * Gets the string literal
     *
     * @return DOCUMENT ME!
     *
     * @throws XPathException when the literal isn't a string
     */
    String getStringLiteral() throws XPathException;
}
