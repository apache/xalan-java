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

/**
 * Represents a XPath expression.
 * <p>Use {@link #getExprType()} to query the expression type. 
 * More information about expression are obtained by casting it
 * to its corresponding java type. Here the XPath expression type to Java type mapping table:
 * </p>
 * <table cellpadding="2" cellspacing="2" border="1">
 * <thead>
 *    <tr>
 * 	    <td>Expression type</td>
 * 	    <td>Java type</td>
 *    </tr>
 * </thead>
 * <tbody>
 * 	  <tr>
 *      <td>{@link #PATH_EXPR}</td>
 *      <td>{@link PathExpr}</td>
 *    </tr>
 *    <tr>
 *      <td>{@link #LOGICAL_EXPR}</td>
 *       <td>{@link OperatorExpr}</td>
 *    </tr>
 *    <tr>
 *      <td>{@link #CONDITIONAL_EXPR}</td>
 *      <td>{@link ConditionalExpr}</td>
 *    </tr>
 *    <tr>
 *      <td>{@link #ITERATION_EXPR}</td>
 *      <td>{@link ForAndQuantifiedExpr}</td>
 *    </tr>
 *    <tr>
 *      <td>{@link #EVERY_EXPR}</td>
 *      <td>{@link ForAndQuantifiedExpr}</td>
 *    </tr>
 *    <tr>
 *      <td>{@link #SOME_EXPR}</td>
 *      <td>{@link ForAndQuantifiedExpr}</td>
 *    </tr>
 *    <tr>
 *      <td>{@link #COMPARISON_EXPR}</td>
 *      <td>{@link OperatorExpr}</td>
 *    </tr>
 *    <tr>
 *      <td>{@link #ARITHMETIC_EXPR}</td>
 *      <td>{@link OperatorExpr}</td>
 *    </tr>
 *    <tr>
 *      <td>{@link #SEQUENCE_EXPR}</td>
 *      <td>{@link OperatorExpr}</td>
 *    </tr>
 *    <tr>
 *      <td>{@link #LITERAL_EXPR}</td>
 *      <td>{@link Literal}</td>
 *    </tr>
 *    <tr>
 *      <td>{@link #FUNCTION_CALL_EXPR}</td>
 *      <td>{@link FunctionCall}</td>
 *    </tr>
 *    <tr>
 *      <td>{@link #VARIABLE_REF_EXPR}</td>
 *      <td>{@link Variable}</td>
 *    </tr>
 *    <tr>
 *      <td>{@link #STEP}</td>
 *      <td>{@link StepExpr}</td>
 *    </tr>
 *    <tr>
 *      <td>{@link #INSTANCE_OF_EXPR}</td>
 *      <td>{@link InstanceOfExpr}</td>
 *    </tr>
 *    <tr>
 *      <td>{@link #UNARY_EXPR}</td>
 *      <td>{@link OperatorExpr}</td>
 *    </tr>
 *    <tr>
 *      <td>{@link #CAST_AS_EXPR}</td>
 *      <td>{@link CastOrTreatAsExpr}</td>
 *    </tr>
 *    <tr>
 *      <td>{@link #TREAT_AS_EXPR}</td>
 *      <td>{@link CastOrTreatAsExpr}</td>
 *    </tr>
 *    <tr>
 *      <td>{@link #CASTABLE_EXPR}</td>
 *      <td>{@link CastableAsExpr}</td>
 *    </tr>
 *    <tr>
 *      <td>{@link #VALIDATE_EXPR}</td>
 *      <td>{@link ?}</td>
 *    </tr>
 *  </tbody>
 * </table> 
 * <p>For example the following code snippet may be used to get the items of 
 * the expression e = (1, call(),(toto[/user], $author)):</p>
 * <pre>
 * void printItem(Expr e)
 * {
 * 		if (e.getExprType() ==  Expr.SEQUENCE_EXPR)
 * 		{
 * 			OperatorExpr expr = (OperatorExpr) e;
 * 			for (int i = 0; i %lt; expr.getOperandCount(); i ++ )
 * 			{
 * 				printItem(expr.getOperand(i));
 * 			}
 * 		} else 
 * 		{
 * 		  System.out.println(e.getString(true) + " ");
 * 		}
 * }
 * </pre> 
 * <p>Should produced the following result:</p>
 * <pre>
 * 1 call() toto[/user] $author
 * </pre> 
 * <p>XPath expressions are always fully expanded. For example, the expression
 * /a//b is expanded to fn:root(self::node())/descendant-or-self::node()/b.
 * The number of steps is 3 (and not 2). 
 * </p> 
 * <p>
 * An {@link Expr} object may be not a valid XPath expression but only a fragment.
 * For example a {@link StepExpr} expression is a fragment but can't be executed by
 * itself. To be valid, the top level expression must be an expression sequence.
 * </p>
 * @see <a href="http://www.w3.org/TR/2002/WD-xpath20-20020816/#id-expressions">XPath 2.0 Specification</a>
 * @author <a href="mailto:villard@us.ibm.com">Lionel Villard</a>
 * @version $Id$
 */
public interface Expr 
{

	/**
	 * The expression is a path expression
	 */
	static final short PATH_EXPR = 0;

	/**
	 * The expression is a logical expression. 
	 * Represents 'or' and 'and' expressions.
	 */
	static final short LOGICAL_EXPR = 1;

	/**
	 * The expression is a conditionnal expression (if) 
	 */
	static final short CONDITIONAL_EXPR = 2;

	/**
	 * The expression is an iteration expression (for)   
	 */
	static final short ITERATION_EXPR = 3;

	/**
	 * The expression is a quantified expression of type every     
	 */
	static final short EVERY_EXPR = 4;

	/**
	 * The expression is a quantified expression of type some     
	 */
	static final short SOME_EXPR = 22;

	/**
	 * The expression is a comparison expression type. 
	 * Includes value comparisons, general comparisons, node comparisons
	 * and order comparisons.
	 */
	static final short COMPARISON_EXPR = 5;

	/**
	 * The expression is an arithmetic expression.
	 * Includes arithmetic operators for addition, subtraction, multiplication, division and modulus
	 */
	static final short ARITHMETIC_EXPR = 6;

	/**
	 * The expression is a sequence.
	 */
	static final short SEQUENCE_EXPR = 7;

	/**
	 * The expression is a validate expression 
	 */
	static final short VALIDATE_EXPR = 10;

	/**
	 * The expression is a literal expression
	 */
	static final short LITERAL_EXPR = 13;

	/**
	 * The expression is a function call
	 */
	static final short FUNCTION_CALL_EXPR = 14;

	/**
	 * The expression is a variable reference
	 */
	static final short VARIABLE_REF_EXPR = 15;

	/**
	 * The expression is a step
	 */
	static final short STEP = 17;

	/**
	 * The expression is an instance of expression
	 */
	static final short INSTANCE_OF_EXPR = 18;

	/**
	 * The expression is a cast as expression
	 */
	static final short CAST_AS_EXPR = 20;

	/**
	 * The expression is a castable expression
	 */
	static final short CASTABLE_EXPR = 21;

	/**
	 * The expression is a treat as expression
	 */
	static final short TREAT_AS_EXPR = 22;

	/**
	 * The expression is a let expression
	 */
	static final short LET_EXPR = 23;


	/**
	 * Gets the expression type. 
	 * @return The type of this expression: one of the constants defined in this
	 * interface
	 */
	short getExprType();

	/**
	 * Clone expression
	 * @return A clone of this expression
	 */
	Expr cloneExpression();

	/**
	 * Gets the expression as a string (external form)
	 * @param abbreviate Gets the string as an abbreviate form or not
	 * @return The external form of this expression
	 */
	String getString(boolean abbreviate);

	/**
	 * Visit the expression. Pass through the expression hierarchy and
	 * invoke corresponding {@link Visitor callbacks}.
	 */
    boolean visit(Visitor visitor);
}
