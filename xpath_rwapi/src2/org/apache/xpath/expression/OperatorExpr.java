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
 * Represents any expressions which combine one or many operands under
 * a single operator. 
 * The following XPath constructors are represented by this interface:
 * <ul>
 *  <li><strong>Sequence expression</strong>:
 *    <ul>
 * 	    <li><strong>By constructing</strong>:
 * 		  <ul>
 *          <li>Expression type: {@link Expr#SEQUENCE_EXPR}</li>
 * 	        <li>Operator type: {@link OperatorExpr#COMMA} or {@link OperatorExpr#TO}</li>
 *          <li>Example: <code>(10, 1, 2, 3, 4)</code>, <code>1 to 10</code></li>
 *        </ul>
 *      </li>
 *      
 * 	    <li><strong>By combining</strong>:
 *        <ul>
 *          <li>Expression type: {@link Expr#SEQUENCE_EXPR}</li>
 * 	        <li>Operator type: {@link OperatorExpr#UNION_COMBINE} or
 * 				{@link OperatorExpr#INTERSECT_COMBINE} or 
 * 				{@link OperatorExpr#EXCEPT_COMBINE}</li>
 *          <li>Example: <code>$seq2 union $seq3</code></li>
 *        </ul> 
 *      </li>
 *    </ul>
 *  </li>
 *  <li><strong>Arithmetic expression</strong>:
 *    <ul>
 *      <li><strong>Unary expression</strong>:
 * 		  <ul>
 *          <li>Expression type: {@link Expr#ARITHMETIC_EXPR}</li>
 * 	        <li>Operator type: {@link OperatorExpr#PLUS_UNARY} or {@link OperatorExpr#MINUS_UNARY}</li>
 *          <li>Example: <code>-20</code></li>
 *        </ul>
 *      </li>
 * 		<li><strong>Additive expression</strong>:
 * 		  <ul>
 *          <li>Expression type: {@link Expr#ARITHMETIC_EXPR}</li>
 * 	        <li>Operator type: {@link OperatorExpr#PLUS_ADDITIVE} or {@link OperatorExpr#MINUS_ADDITIVE}</li>
 *          <li>Example: <code>+20</code></li>
 *        </ul>
 *      </li>
 * 		<li><strong>Multiplicative expression</strong>:
 * 		  <ul>
 *          <li>Expression type: {@link Expr#ARITHMETIC_EXPR}</li>
 * 	        <li>Operator type: {@link OperatorExpr#MULT_DIV} or 
 * 				{@link OperatorExpr#MULT_IDIV} or
 *    			{@link OperatorExpr#MULT_MOD} or
 *    			{@link OperatorExpr#MULT_PRODUCT} </li>
 *          <li>Example: <code>10 * 20</code></li>
 *        </ul>
 *      </li>
 * 	  </ul>
 *  </li>
 *  <li><strong>Comparison expression</strong>:
 * 	  <ul>
 *      <li>Expression type: {@link Expr#COMPARISON_EXPR}</li>
 * 	    <li>Operator type: XXX_COMPARISON</li>
 *      <li>Example: <code>$book1/author eq "Kennedy"</code></li>
 *    </ul>
 * 	</li>
 *  <li><strong>Logical expressions</strong>:
 * 	  <ul>
 *      <li>Expression type: {@link Expr#LOGICAL_EXPR}</li>
 * 	    <li>Operator type: {@link OperatorExpr#AND_LOGICAL} or
 * 			{@link OperatorExpr#OR_LOGICAL}</li>
 *      <li>Example: <code>1 eq 1 and 2 eq 2</code></li>
 *    </ul>
 *  </li>
 *  <li><strong>Path expression</strong>: see {@link PathExpr}</li>
 *  <li><strong>Function call</strong>: see {@link FunctionCall}</li>
 * </ul>
 * @see <a href="http://www.w3.org/TR/xpath20">XPath 2.0 Specification</href>
 */
public interface OperatorExpr extends Expr
{
	/**
	 * <code>|</code> or <code>union</code>
	 */
    short UNION_COMBINE = 0;

	/**
	 * <code>intersect</code>
	 */
    short INTERSECT_COMBINE = 1;

    /**
     * <code>except</code>
     */
    short EXCEPT_COMBINE = 2;

    /**
     * <code>+</code>
     */
    short PLUS_ADDITIVE = 3;

    /**
     * <code>-</code>
     */
    short MINUS_ADDITIVE = 4;

    /**
     * <code>to</code>
     */
    short TO = 5;

    /**
     * <code>eq</code>
     */
    short EQUAL_VALUE_COMPARISON = 6;

    /**
     * <code>neq</code>
     */
    short NOTEQUAL_VALUE_COMPARISON = 7;

    /**
     * <code>lt</code>
     */
    short LESSTHAN_VALUE_COMPARISON = 8;

	/**
	 * <code>le</code>
	 */
    short LESSOREQUALTHAN_VALUE_COMPARISON = 9;

	/**
	 * <code>gt</code>
	 */
    short GREATTHAN_VALUE_COMPARISON = 10;

	/**
	 * <code>ge</code>
	 */
    short GREATOREQUALTHAN_VALUE_COMPARISON = 11;

	/**
	 * <code>=</code>
	 */
     short EQUAL_GENERAL_COMPARISON = 12;

    /**
     * <code>!=</code>
     */
    short NOTEQUAL_GENERAL_COMPARISON = 13;

	/**
	 * <code>&lt;</code>
	 */
    short LESSTHAN_GENERAL_COMPARISON = 14;

    /**
	 * <code>&lt;=</code>
	 */
    short LESSOREQUALTHAN_GENERAL_COMPARISON = 15;

    /**
	 * <code>&gt;</code>
	 */
    short GREATTHAN_GENERAL_COMPARISON = 16;

    /**
	 * <code>&gt;=</code>
	 */
    short GREATOREQUALTHAN_GENERAL_COMPARISON = 17;

    /**
	 * <code>is</code>
	 */
    short IS_NODE_COMPARISON = 18;

    /**
	 * <code>isnot</code>
	 */
    short ISNOT_NODE_COMPARISON = 19;

    /**
	 * <code>&lt;</code>
	 */
    short EARLIERTHAN_ORDER_COMPARISON = 20;

    /**
	 * <code>&gt;&gt;</code>
	 */
    short LATERTHAN_ORDER_COMPARISON = 21;

    /**
	 * <code>and</code>
	 */
    short AND_LOGICAL = 22;

    /**
	 * <code>or</code>
	 */
    short OR_LOGICAL = 23;

    /**
	 * <code>+ expr</code>
	 */
    short PLUS_UNARY = 24;

    /**
	 * <code>- expr</code>
	 */
    short MINUS_UNARY = 25;

    /**
	 * <code>/</code>
	 */
    short SLASH_STEP = 26;

    /**
	 * <code>,</code>
	 */
    short COMMA = 28;

    /**
	 * <code>*</code>
	 */
    short MULT_PRODUCT = 29;

    /**
	 * <code>div</code>
	 */
    short MULT_DIV = 30;

    /**
	 * <code>idiv</code>
	 */
    short MULT_IDIV = 31;

    /**
	 * <code>mod</code>
	 */
    short MULT_MOD = 32;

    /**
     * Gets the operator type
     * @return One one the operator type constants defined in this class
     */
    short getOperatorType();

    /**
     * Gets the operand at the ith position.
     * @throws IndexOutOfBoundsException
     */
    Expr getOperand(int i);

    /**
     * Gets the operand count
     */
    int getOperandCount();

    /**
     * Append an operand at the end of this expression.
     */
    void addOperand(Expr operand) throws XPathException;

    /**
     * Remove an operand
     */
    void removeOperand(Expr operand) throws XPathException;

	// TODO
	// void setOperand(Expr operand)
	// void insertOperand(Expr operant, int i)	
    
    /**
     * Append the specified expr at the end of this expression. 
     * The specified expression has to be of the same type of this expression,
     * otherwise an exception will be raised.
     * <p>For example, the result of appending <code>c/d</code> 
     * in the expression <code>a/b</code> is <code>a/b/c/d</code></p>
     * @param expr The expression to append
     */    
    void append(OperatorExpr expr) throws XPathException;
    
}
