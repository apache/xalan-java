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

import org.apache.xpath.XPathException;


/**
 * Represents any expressions with one, two or more operands. This includes the following XPath features:
 * <ul>
 *  <li>combining sequences</li>
 *  <li>unary and binary arithmetics expressions</li>
 *  <li>comparison expressions</li>
 *  <li>logical expressions</li>
 *  <li>range expressions</li>
 *  <li>path expressions (see <code>PathExpr</code>)</li>
 *  <li>Parenthesized expression</li>
 * </ul>
 * <pre>
 * [14]   UnionExpr   ::=   IntersectExceptExpr ( ("union" |  "|")  IntersectExceptExpr )*
 * [15]   IntersectExceptExpr   ::=   UnaryExpr ( ("intersect" |  "except")  UnaryExpr )*
 * [12]   AdditiveExpr   ::=   MultiplicativeExpr ( ("+" |  "-")  MultiplicativeExpr )*
 * [13]   MultiplicativeExpr   ::=   UnionExpr ( ("*" |  "div" |  "idiv" |  "mod")  UnionExpr )*
 * [10]   ComparisonExpr   ::=   RangeExpr ( (ValueComp|  GeneralComp|  NodeComp|  OrderComp)  RangeExpr )?
 * [25]   ValueComp   ::=   "eq" |  "ne" |  "lt" |  "le" |  "gt" |  "ge"
 * [24]   GeneralComp   ::=   "=" |  "!=" |  "<" |  "<=" |  ">" |  ">="
 * [26]   NodeComp   ::=   "is" |  "isnot"
 * [27]   OrderComp   ::=   "<<" |  ">>"
 * [4]    OrExpr   ::=   AndExpr ( "or"  AndExpr )*
 * [5]    AndExpr   ::=   ForExpr ( "and"  ForExpr )*
 * [11]   RangeExpr   ::=   AdditiveExpr ( "to"  AdditiveExpr )*
 * [18]   PathExpr   ::=   ("/" RelativePathExpr?) |  ("//" RelativePathExpr) |  RelativePathExpr
 * [19]   RelativePathExpr   ::=   StepExpr (("/" |  "//") StepExpr)*
 * [46]   ParenthesizedExpr   ::=   "(" ExprSequence? ")"
 * [25]   ExprSequence   ::=   Expr ("," Expr)*
 * </pre>
 * @see <a href="http://www.w3.org/TR/xpath20">XPath 2.0 Specification</href>
 */
public interface OperatorExpr extends Expr
{
    /**
     *
     */
    short UNION_COMBINE = 0;

    /**
     *
     */
    short INTERSECT_COMBINE = 1;

    /**
     *
     */
    short EXCEPT_COMBINE = 2;

    /**
     *
     */
    short PLUS_ADDITIVE = 3;

    /**
     *
     */
    short MINUS_ADDITIVE = 4;

    /**
    *
    */
    short RANGE = 5;

    /**
    *
    */
    short EQUAL_VALUE_COMPARISON = 6;

    /**
    *
    */
    short NOTEQUAL_VALUE_COMPARISON = 7;

    /**
    *
    */
    short LESSTHAN_VALUE_COMPARISON = 8;

    /**
    *
    */
    short LESSOREQUALTHAN_VALUE_COMPARISON = 9;

    /**
    *
    */
    short GREATTHAN_VALUE_COMPARISON = 10;

    /**
    *
    */
    short GREATOREQUALTHAN_VALUE_COMPARISON = 11;

    /**
    *
    */
    short EQUAL_GENERAL_COMPARISON = 12;

    /**
    *
    */
    short NOTEQUAL_GENERAL_COMPARISON = 13;

    /**
    *
    */
    short LESSTHAN_GENERAL_COMPARISON = 14;

    /**
    *
    */
    short LESSOREQUALTHAN_GENERAL_COMPARISON = 15;

    /**
    *
    */
    short GREATTHAN_GENERAL_COMPARISON = 16;

    /**
    *
    */
    short GREATOREQUALTHAN_GENERAL_COMPARISON = 17;

    /**
    *
    */
    short IS_NODE_COMPARISON = 18;

    /**
    *
    */
    short ISNOT_NODE_COMPARISON = 19;

    /**
    *
    */
    short EARLIERTHAN_ORDER_COMPARISON = 20;

    /**
    *
    */
    short LATERTHAN_ORDER_COMPARISON = 21;

    /**
    *
    */
    short AND_LOGICAL = 22;

    /**
    *
    */
    short OR_LOGICAL = 23;

    /**
     *
     */
    short PLUS_UNARY = 24;

    /**
     *
     */
    short MINUS_UNARY = 25;

    /**
     *
     */
    short SLASH_STEP = 26;

    /**
     *
     */
    short SLASHSLASH_STEP = 27;

    /**
     *
     */
    short COMMA = 28;

    /**
     *
     */
    short MULT_PRODUCT = 29;

    /**
     *
     */
    short MULT_DIV = 30;

    /**
     *
     */
    short MULT_IDIV = 31;

    /**
     *
     */
    short MULT_MOD = 32;

    /**
     * Gets the operator type
     * @return
     */
    short getOperatorType();

    /**
     * Gets the operand at the ith position.
     */
    Expr getOperand(int i);

    /**
     * Gets the operand count
     */
    int getOperandCount();

    /**
     * Append an operand
     */
    void addOperand(Expr operand) throws XPathException;

    /**
     * Remove an operand
     */
    void removeOperand(Expr operand) throws XPathException;
    
    /**
     * Append the specified expr to the end of this expression. <br>
     * The specified expression has to be of the same type of this expression,
     * otherwise an exception will be raised
     * @param expr The expression to append
     */    
    void append(OperatorExpr expr) throws XPathException;
    
}
