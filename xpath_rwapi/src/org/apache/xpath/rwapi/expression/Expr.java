/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights 
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
package org.apache.xpath.rwapi.expression;


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
     * Path expression type
     */
    short PATH_EXPR = 0;
    
    /**
     * Logical expression type. 
     * Modelise 'or' and 'and' expressions
     */
    short LOGICAL_EXPR = 1;
    
    /**
     * Conditionnal expression type. 
     * Modelise 'if' expressions
     */
    short CONDITIONAL_EXPR = 2;
    
    /**
     * Iteration expression type.
     * Modelise 'for' expressions
     */
    short ITERATION_EXPR = 3;
    
    /**
     * Quantified expression type.
     * Modelise 'every' and 'some' expressions
     */
    short QUANTIFIED_EXPR = 4;
    
    /**
     * Comparator expression type.
     * Modelise value comparisons, general comparisons, node comparisons, and order comparisons.
     */
    short COMPARATOR_EXPR = 5;
    
    /**
     * Arithmetic expression type.
     * Modelise arithmetic operators for addition, subtraction, multiplication, division, and modulus
     */
    short ARITHMETIC_EXPR = 6;
    
    /**
     * Sequence of expressions type.
     */
    short SEQUENCE_EXPR = 7;
    
    /**
     * Union and Intersection
     */
    short COMBINE_EXPR = 8;
    
    /**
     * 
     */
    short PRIMARY_EXPR = 9;
    
    /**
     * 
     */
    short VALIDATE_EXPR = 10;
    
    /**
     * 
     */
    short NAMETEST_EXPR = 11;
    
     /**
     * 
     */
    short KINDTEST_EXPR = 12;
    

     /**
     * Literal primary expression type constant
     */
    short LITERAL_EXPR = 13;

    /**
     * Function call primary expression type constant
     */
    short FUNCTION_CALL_EXPR = 14;

    /**
     * Variable reference primary expression type constant
     */
    short VARIABLE_REF_EXPR = 15;

    /**
     * Parenthesized primary expression type constant
     */
    //short PARENTHESIZED_EXPR = 16;
    
    /**
     * Step
     */
    short STEP = 17;
    
    /**
     * Instance of
     */
    short INSTANCE_OF_EXPR = 18;
    
     
    /**
     * Unary expression
     */
    short UNARY_EXPR = 19;
    
    /**
     * Cast expression
     */
    short CAST_EXPR = 20;
    
    /**
     * Castable expression
     */
    short CASTABLE_EXPR = 21;

    /**
     * Gets the expression or expression component type
     * @return The type of this expression: one of the constants define in this class
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


