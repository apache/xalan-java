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
package org.apache.xpath.impl;

import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.xpath.XPathException;
import org.apache.xpath.datamodel.SequenceType;
import org.apache.xpath.expression.CastOrTreatAsExpr;
import org.apache.xpath.expression.ConditionalExpr;
import org.apache.xpath.expression.Expr;
import org.apache.xpath.expression.ExpressionFactory;
import org.apache.xpath.expression.ForAndQuantifiedExpr;
import org.apache.xpath.expression.Literal;
import org.apache.xpath.expression.NodeTest;
import org.apache.xpath.expression.OperatorExpr;
import org.apache.xpath.expression.PathExpr;
import org.apache.xpath.expression.StepExpr;
import org.apache.xpath.impl.parser.NodeFactory;
import org.apache.xpath.impl.parser.ParseException;
import org.apache.xpath.impl.parser.XPath;

/**
 * Default implementation expression factory to create XPath AST nodes.
 * //@TODO not fully implemented! 13-Mar-03 -sc
 */
public class ExpressionFactoryImpl implements ExpressionFactory {

	/**
	 * Node factory
	 */
	protected NodeFactory m_nodeFactory = null;

	/* (non-Javadoc)
	 * @see org.apache.xpath.expression.ExpressionFactory#setNodeFactory(org.apache.xpath.impl.parser.NodeFactory)
	 */
	public void setNodeFactory(NodeFactory factory) {
		m_nodeFactory = factory;
	}

	/**
	 * @see org.apache.xpath.expression.ExpressionFactory#createExpr(java.lang.String)
	 */
	public Expr createExpr(String expr) throws XPathException {
		XPath parser = new XPath(new StringReader(expr));
		if ( m_nodeFactory != null )  {
			parser.setNodeFactory(m_nodeFactory);
		}
		try {
			return (Expr) parser.XPath2().jjtGetChild(0);
		} catch (ParseException e) {
            throw new XPathException(e);
		}
	}

	/**
	 * @see org.apache.xpath.expression.ExpressionFactory#createPathExpr(boolean)
	 */
	public PathExpr createPathExpr(boolean isAbsolute) {
		PathExprImpl e = new PathExprImpl();
		e.m_isAbsolute = isAbsolute;
		return e;
	}

	/**
	 * @see org.apache.xpath.expression.ExpressionFactory#createStepExpr(short, org.apache.xpath.expression.NodeTest)
	 */
	public StepExpr createStepExpr(short axisType, NodeTest nodeTest) {
		return new StepExprImpl(axisType, nodeTest);
	}

	/**
	 * @see org.apache.xpath.expression.ExpressionFactory#createNameTest(java.lang.String, java.lang.String)
	 */
	public NodeTest createNameTest(String namespace, String name) {
		return new NameTestImpl(namespace, name);
	}

	/**
	 * @see org.apache.xpath.expression.ExpressionFactory#createCombineExpr(short)
	 */
	public OperatorExpr createCombineExpr(short type) {
		return new OperatorImpl(Expr.COMBINE_EXPR, type);
	}

	/**
	 * @see org.apache.xpath.expression.ExpressionFactory#createSomeExpr(org.apache.xpath.expression.Expr)
	 */
	public ForAndQuantifiedExpr createSomeExpr(Expr clause) {
		return null;
	}

	/**
	 * @see org.apache.xpath.expression.ExpressionFactory#createEveryExpr(org.apache.xpath.expression.Expr)
	 */
	public ForAndQuantifiedExpr createEveryExpr(Expr clause) {
		return null;
	}

	/**
	 * @see org.apache.xpath.expression.ExpressionFactory#createAndExpr(org.apache.xpath.expression.Expr, org.apache.xpath.expression.Expr)
	 */
	public OperatorExpr createAndExpr(Expr firstOperand, Expr secondOperand) {
		return new OperatorImpl(Expr.LOGICAL_EXPR, OperatorExpr.AND_LOGICAL);
	}

	/**
	 * @see org.apache.xpath.expression.ExpressionFactory#createOrExpr(org.apache.xpath.expression.Expr, org.apache.xpath.expression.Expr)
	 */
	public OperatorExpr createOrExpr(Expr firstOperand, Expr secondOperand) {
		return new OperatorImpl(Expr.LOGICAL_EXPR, OperatorExpr.OR_LOGICAL);
	}

	/**
	 * @see org.apache.xpath.expression.ExpressionFactory#createIfExpr(org.apache.xpath.expression.Expr, org.apache.xpath.expression.Expr, org.apache.xpath.expression.Expr)
	 */
	public ConditionalExpr createIfExpr(Expr test, Expr thenExpr, Expr elseExpr) {
		return new ConditionalExprImpl(test, thenExpr, elseExpr);
	}

	/**
	 * @see org.apache.xpath.expression.ExpressionFactory#createForExpr(java.lang.String, org.apache.xpath.expression.Expr, org.apache.xpath.expression.Expr)
	 */
	public ForAndQuantifiedExpr createForExpr(String varName, Expr clauseExpr, Expr quantifiedExpr) {
		return null;
	}

	/**
	 * @see org.apache.xpath.expression.ExpressionFactory#createCastExpr(org.apache.xpath.datamodel.SequenceType, org.apache.xpath.expression.OperatorExpr)
	 */
	public CastOrTreatAsExpr createCastAsExpr(SequenceType seqType, OperatorExpr parExpr) {
		return new CastOrTreatAsExprImpl(seqType, parExpr, true);
	}

	/* (non-Javadoc)
	 * @see org.apache.xpath.expression.ExpressionFactory#createTreatAsExpr(org.apache.xpath.datamodel.SequenceType, org.apache.xpath.expression.OperatorExpr)
	 */
	public CastOrTreatAsExpr createTreatAsExpr(
		SequenceType seqType,
		OperatorExpr parExpr) {
			return new CastOrTreatAsExprImpl(seqType, parExpr, false);

	}

	/**
	 * @see org.apache.xpath.expression.ExpressionFactory#createIntegerLiteralExpr(int)
	 */
	public Literal createIntegerLiteralExpr(BigInteger value) {
		LiteralImpl lit = new LiteralImpl();
		lit.setIntValue(value);
		return lit;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.xpath.expression.ExpressionFactory#createIntegerLiteralExpr(int)
	 */
	public Literal createIntegerLiteralExpr(int value) {
		return createIntegerLiteralExpr(BigInteger.valueOf(value));
	}


	/**
	 * @see org.apache.xpath.expression.ExpressionFactory#createDecimalLiteralExpr(float)
	 */
	public Literal createDecimalLiteralExpr(double value) {
		return createDecimalLiteralExpr(new BigDecimal(value));
	}
	
	/* (non-Javadoc)
	 * @see org.apache.xpath.expression.ExpressionFactory#createDecimalLiteralExpr(java.math.BigDecimal)
	 */
	public Literal createDecimalLiteralExpr(BigDecimal value) {
		LiteralImpl lit = new LiteralImpl();
		lit.setDecimalValue(value);
		return lit;

	}

	/**
	 * @see org.apache.xpath.expression.ExpressionFactory#createStringLiteralExpr(java.lang.String)
	 */
	public Literal createStringLiteralExpr(String value) {
		LiteralImpl lit = new LiteralImpl();
		lit.setStringValue(value);
		return lit;
	}

	/**
	 * @see org.apache.xpath.expression.ExpressionFactory#createDoubleLiteralExpr(double)
	 */
	public Literal createDoubleLiteralExpr(double value) {
		LiteralImpl lit = new LiteralImpl();
		lit.setDoubleValue(value);
		return lit;
	}



}
