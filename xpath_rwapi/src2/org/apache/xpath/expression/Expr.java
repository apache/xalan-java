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
 * This interface represents an XPath Expression.
 * <pre>
 * [1]   XPath   ::=   ExprSequence? 
 * [3]   Expr    ::=   OrExpr
 * [7]   QuantifiedExpr   ::=   ((<"some" "$"> |  <"every" "$">) VarName "in" Expr ("," "$" VarName "in" Expr)* "satisfies")* IfExpr
 * [10]  ComparisonExpr   ::=   RangeExpr ( (ValueComp | GeneralComp |  NodeComp | OrderComp)  RangeExpr )?
 * [18]  PathExpr   ::=   ("/" RelativePathExpr?) |  ("//" RelativePathExpr) |  RelativePathExpr
 * </pre>
 * @see <a href="http://www.w3.org/TR/2002/WD-xpath20-20020816/#id-expressions">XPath 2.0 Specification</a>
 */ 
public interface Expr extends Visitable {

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
	static final  short COMPARISON_EXPR = 5;
    
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
     * The expression is a combine expression (union and intersection)
     */
	static final short COMBINE_EXPR = 8;
   
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
     * The expression is a range expression
     */
	static final short RANGE_EXPR = 16;
    
    /**
     * Step
     * %review% to remove since a step can't exist outside of path
     */
	static final short STEP = 17;
    
    /**
     * The expression is an instance of expression
     */
	static final short INSTANCE_OF_EXPR = 18;    
     
    /**
     * The expression is an unary expression
     */
	static final short UNARY_EXPR = 19;
    
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
     * Gets the global expression type. 
     * @return The type of this expression: one of the constants defined in this
     * interface
     */
    short getExprType();

    /**
     * Clone the expression
     * @return A clone of this expression
     */
    Expr cloneExpression();  
    
    /**
     * Gets the expression as a string (external form)
     * @param abbreviate Gets the string as an abbreviate form or not
     * @return The external form of this expression
     */
    String getString(boolean abbreviate);  
    
}


