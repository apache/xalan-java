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
import org.apache.xpath.datamodel.SequenceType;


/**
 * Allows the creation of XPath expressions either from string representation
 * or directly using the object-oriented representation.
 */
public interface ExpressionFactory {

	
    /**
     * Create a expression from a string representation
     * @throws XPathException whenever the given expression isn't an XPath expression
     */
    public Expr createExpr(String expr) throws XPathException;

	/**
	 * Create a relative or absolute path expression 
	 */
	public PathExpr createPathExpr(boolean isAbsolute);

	/**
	 * Create a step expression without qualifiers
	 */
	public StepExpr createStepExpr(short axisType, NodeTest nodeTest);

	/**
	 * Create a name test
	 */
	public NodeTest createNameTest(String namespace, String name);

	/**
 	 * Create a predicate qualifier
	 * @param expr
	 */
	public Expr createPredicate(Expr expr);

	/**
	 * Create a combining expression
	 */
	public OperatorExpr createCombineExpr(short type);
    
    /**
     * Create a quantifier expression of type 'some' with one clause
     */
    public ForAndQuantifiedExpr createSomeExpr(Expr clause);

    /**
     * Create a quantifier expression of type 'every'  with one clause
     */
    public ForAndQuantifiedExpr createEveryExpr(Expr clause);

    /**
     * Create an logical expression of type 'and' with at least two operands
     */
    public OperatorExpr createAndExpr(Expr firstOperand, Expr secondOperand);
    
    /**
     * Create an logical expression of type 'or' with at least two operands
     */
    public OperatorExpr createOrExpr(Expr firstOperand, Expr secondOperand);
    
    /**
     * Create an if expression
     */
    public ConditionalExpr createIfExpr(Expr test, Expr thenExpr, Expr elseExpr);
    
    /**
     * Create a for expression
     */
    public ForAndQuantifiedExpr createForExpr(String varName, Expr clauseExpr, Expr quantifiedExpr);
    
    /**
     * Create a cast as expression 
     */
    public CastOrTreatAsExpr createCastExpr(SequenceType seqType, OperatorExpr parExpr);
    
    /**
     * Create an integer expression 
     */
    public Literal createIntegerLiteralExpr(int value);
    
    /**
     * Create a decimal expression 
     */
    public Literal createDecimalLiteralExpr(float value);
    
    /**
     * Create a string expression 
     */
    public Literal createStringLiteralExpr(String value);
    
    /**
     * Create a double expression 
     */
    public Literal createDoubleLiteralExpr(double value);
}
