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

import org.apache.xpath.XPath20Exception;
import org.apache.xpath.expression.Expr;
import org.apache.xpath.expression.OperatorExpr;
import org.apache.xpath.expression.Visitor;
import org.apache.xpath.impl.parser.Node;
import org.apache.xpath.impl.parser.SimpleNode;
import org.apache.xpath.impl.parser.Token;
import org.apache.xpath.impl.parser.XPath;
import org.apache.xpath.impl.parser.XPathConstants;
import org.apache.xpath.impl.parser.XPathTreeConstants;

/**
 * Basic implementation of {@link OperatorExpr}. 
 * @author <a href="mailto:villard@us.ibm.com>Lionel Villard</a>
 * @version $Id$
 */
public class OperatorImpl extends ExprImpl implements OperatorExpr
{
	/**
	 * Mapping between operation type and its expression type 
	 */
	final private static short[] OPTYPE2EXPRTYPE =
		{
			Expr.SEQUENCE_EXPR,
			Expr.SEQUENCE_EXPR,
			Expr.SEQUENCE_EXPR,
			Expr.ARITHMETIC_EXPR,
			Expr.ARITHMETIC_EXPR,
			Expr.SEQUENCE_EXPR,
			Expr.COMPARISON_EXPR,
			Expr.COMPARISON_EXPR,
			Expr.COMPARISON_EXPR,
			Expr.COMPARISON_EXPR,
			Expr.COMPARISON_EXPR,
			Expr.COMPARISON_EXPR,
			Expr.COMPARISON_EXPR,
			Expr.COMPARISON_EXPR,
			Expr.COMPARISON_EXPR,
			Expr.COMPARISON_EXPR,
			Expr.COMPARISON_EXPR,
			Expr.COMPARISON_EXPR,
			Expr.COMPARISON_EXPR,
			Expr.COMPARISON_EXPR,
			Expr.COMPARISON_EXPR,
			Expr.COMPARISON_EXPR,
			Expr.LOGICAL_EXPR,
			Expr.LOGICAL_EXPR,
			Expr.ARITHMETIC_EXPR,
			Expr.ARITHMETIC_EXPR,
			Expr.PATH_EXPR,
			-1,
			Expr.SEQUENCE_EXPR,
			Expr.ARITHMETIC_EXPR,
			Expr.ARITHMETIC_EXPR,
			Expr.ARITHMETIC_EXPR,
			Expr.ARITHMETIC_EXPR };

	/**
	 * Mapping between operation type and it's external representation
	 */
	final private static String[] OPTYPE2STRING =
		{
			"|",
			"intersect",
			"except",
			"+",
			"-",
			"to",
			"eq",
			"ne",
			"lt",
			"le",
			"gt",
			"ge",
			"=",
			"!=",
			"<",
			"<=",
			">",
			">=",
			"is",
			"isnot",
			"<<",
			">>",
			"and",
			"or",
			"+",
			"-",
			"/",
			"//",
			",",
			"*",
			"div",
			"idiv",
			"mod" };

	/**
	 * Indicate whether space is needed around the operator
	 */
	final private static boolean[] SPACE_NEEDED =
		{
			false,
			true,
			true,
			false,
			false,
			true,
			true,
			true,
			true,
			true,
			true,
			true,
			false,
			false,
			false,
			false,
			false,
			false,
			true,
			true,
			false,
			false,
			true,
			true,
			false,
			false,
			false,
			false,
			false,
			false,
			true,
			true,
			true };

	/**
	 *  Operator precedence
	 */
	final private static short[] OPERATOR_PRECEDENCE = { 13, // "|",
		14, // "intersect",
		14, // "except",
		10, // "+",
		10, // "-",
		9, // "to",
		8, // "eq",
		8, // "ne",
		8, // "lt",
		8, // "le",
		8, // "gt",
		8, // "ge",
		8, // "=",
		8, // "!=",
		8, // "<",
		8, // "<=",
		8, // ">",
		8, // ">=",
		8, // "is",
		8, // "isnot",
		8, // "<<",
		8, // ">>",
		3, // "and",
		2, // "or",
		12, // "+", (unary)
		12, // "-", (unary)
		15, // "/",
		15, // "//",
		1, // ",",
		11, // "*",
		11, // "div",
		11, // "idiv",
		11, // "mod" 
	};

	/**
	 * Type of the expression
	 */
	short m_exprType;

	/**
	 * Type of the operator
	 */
	short m_opType;

	/**
	 * Internal use only
	 */
	protected OperatorImpl()
	{
	}

	/**
	 * Constructor for OperatorImpl. Internal uses only.	
	 * @param i
	 */
	public OperatorImpl(int i)
	{
		super(i);

		switch (i)
		{
			// [Aug22Draft] Changed from JJTEXPRSEQUENCE
			case XPathTreeConstants.JJTEXPR :
				m_exprType = SEQUENCE_EXPR;
				m_opType = COMMA;

				break;

			case XPathTreeConstants.JJTUNARYEXPR :
				m_exprType = ARITHMETIC_EXPR;
				break;

			case XPathTreeConstants.JJTUNIONEXPR :
			case XPathTreeConstants.JJTINTERSECTEXCEPTEXPR :
			case XPathTreeConstants.JJTPATTERN :
				m_exprType = SEQUENCE_EXPR;

				// opType is not known yet
				break;

			case XPathTreeConstants.JJTFUNCTIONCALL :
			case XPathTreeConstants.JJTIDKEYPATTERN :

				// ignore : see FunctionCallImpl subclass
				break;

			case XPathTreeConstants.JJTADDITIVEEXPR :
			case XPathTreeConstants.JJTMULTIPLICATIVEEXPR :
				m_exprType = ARITHMETIC_EXPR;

				// opType is not known yet
				break;

			case XPathTreeConstants.JJTOREXPR :
			case XPathTreeConstants.JJTANDEXPR :
				m_exprType = LOGICAL_EXPR;

				//	opType is not known yet
				break;

			case XPathTreeConstants.JJTCOMPARISONEXPR :
				m_exprType = COMPARISON_EXPR;

				// opType is not known yet
				break;

			case XPathTreeConstants.JJTRANGEEXPR :
				m_exprType = SEQUENCE_EXPR;
				m_opType = TO;

				break;

			default :

				// Invalid parameter
				throw new IllegalArgumentException("The parameter value does not correspond to an operator identifier");
				// I16
		}
	}

	/**
	 * Constructor for OperatorImpl. Internal uses only.	 
	 * @param p
	 * @param i
	 */
	public OperatorImpl(XPath p, int i)
	{
		super(p, i);
	}

	protected OperatorImpl(short exprType, short opType)
	{
		super();

		m_exprType = exprType;
		m_opType = opType;
	}

	// Methods

	/**
	 * Tell is spaces are needed around the operator
	 */
	final protected boolean isSpaceNeeded()
	{
		return SPACE_NEEDED[m_opType];
	}

	protected short getOperatorPrecedence()
	{
		return OPERATOR_PRECEDENCE[m_opType];
	}

	/**
	 * Tells whether this expression is an unary expression	 
	 */
	protected boolean isUnary()
	{
		return m_opType == PLUS_UNARY || m_opType == MINUS_UNARY;
	}

	/**
	 * Tells whether the specified expression can be flatten.
	 * 
	 */
	protected boolean canBeFlatten(ExprImpl expr)
	{
		//		return (m_exprType == SEQUENCE_EXPR
		//							&& (expr.jjtGetNumChildren() > 0)
		//							&& (((Expr) expr.jjtGetChild(0)).getExprType() == SEQUENCE_EXPR)
		//							&& (((OperatorExpr) expr.jjtGetChild(0)).getOperatorType()
		//								== m_opType))
		//							|| ((id == XPathTreeConstants.JJTPATTERN)
		//								&& (expr.jjtGetNumChildren() > 0)
		//								&& (expr.jjtGetChild(0).getId()
		//									== XPathTreeConstants.JJTPATTERN));
		return expr.getExprType() == m_exprType
			&& ((OperatorExpr) expr).getOperatorType() == m_opType;
	}

	// Implements Expr

	public short getExprType()
	{
		return m_exprType;
	}

	public boolean visit(Visitor visitor)
	{
		if (visitor.visitOperator(this))
		{
			int count = getOperandCount();

			for (int i = 0; i < count; i++)
			{
				if (!getOperand(i).visit(visitor))
				{
					return false;
				}
			}

			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Gets the operator as a char
	 */
	public String getOperatorChar()
	{
		return OPTYPE2STRING[m_opType];
	}

	/**
	 * Gets expression as external string representation
	 *
	 * @param expr 
	 * @param abbreviate 
	 */
	public void getString(StringBuffer expr, boolean abbreviate)
	{
		boolean pred = lowerPrecedence();
		if (pred)
		{
			expr.append('(');
		}

		int size = getOperandCount();
		String oper = getOperatorChar();
		ExprImpl op;

		if ((m_opType == MINUS_UNARY) || (m_opType == PLUS_UNARY))
		{
			expr.append(oper);
		}

		for (int i = 0; i < size; i++)
		{
			op = (ExprImpl) getOperand(i);

			// [Aug22Draft] temp, defensive
			if (null == op)
			{
				expr.append("!NULL!");
				return;
			}

			op.getString(expr, abbreviate);

			if (i < (size - 1))
			{
				if (isSpaceNeeded())
				{
					expr.append(' ');
				}

				expr.append(oper);

				if (isSpaceNeeded())
				{
					expr.append(' ');
				}
			}
		}
		if (pred)
		{
			expr.append(')');
		}
	}

	// Implements OperatorExpr    

	public Expr addOperand(Expr operand) throws XPath20Exception
	{
		operand = selfOrclone(operand);

		super.jjtAddChild(
			(Node) operand,
			(m_children == null) ? 0 : m_children.size());

		return operand;
	}

	public Expr insertOperand(int i, Expr operand)
	{
		operand = selfOrclone(operand);

		super.jjtInsertChild((Node) operand, i);
		return operand;
	}

	public Expr replaceOperand(int i, Expr operand) throws XPath20Exception
	{
		// TODO: check operand compatibility
		operand = selfOrclone(operand);

		ExprImpl old = (ExprImpl) getOperand(i);
		old.jjtSetParent(null);

		super.jjtAddChild((Node) operand, i);
		return operand;
	}

	public void append(OperatorExpr expr) throws XPath20Exception
	{
		if (expr.getExprType() == m_exprType
			&& expr.getOperatorType() == m_opType)
		{
			int size = expr.getOperandCount();
			for (int i = 0; i < size; i++)
			{
				addOperand(expr.getOperand(i));
			}
		}
		else
		{
			throw new XPath20Exception("Mismatched operator expressions");
			// I16 + better msg
		}
	}

	public Expr getOperand(int i)
	{
		if (m_children == null)
		{
			throw new ArrayIndexOutOfBoundsException();
		}

		try
		{
			return (Expr) m_children.get(i);
		}
		catch (java.lang.ClassCastException e)
		{
			System.out.println();
			System.out.flush();
			System.err.println("\nYikes!" + e.getMessage());
			return null;
		}
	}

	public int getOperandCount()
	{
		return (m_children == null) ? 0 : m_children.size();
	}

	public short getOperatorType()
	{
		return m_opType;
	}

	public void removeOperand(Expr operand) throws XPath20Exception
	{
		super.jjtRemoveChild((Node) operand);
	}

	// Parser

	public void processToken(Token token)
	{
		switch (token.kind)
		{
			case XPathConstants.Plus :
				m_opType = PLUS_ADDITIVE;

				break;

			case XPathConstants.Minus :
				m_opType = MINUS_ADDITIVE;

				break;

			case XPathConstants.Multiply :
				m_opType = MULT_PRODUCT;

				break;

			case XPathConstants.Div :
				m_opType = MULT_DIV;

				break;

			case XPathConstants.Idiv :
				m_opType = MULT_IDIV;

				break;

			case XPathConstants.Mod :
				m_opType = MULT_MOD;

				break;

			case XPathConstants.Union :
			case XPathConstants.Vbar :
				m_opType = UNION_COMBINE;

				break;

			case XPathConstants.Intersect :
				m_opType = INTERSECT_COMBINE;

				break;

			case XPathConstants.Except :
				m_opType = EXCEPT_COMBINE;

				break;

			case XPathConstants.And :
				m_opType = AND_LOGICAL;

				break;

			case XPathConstants.Or :
				m_opType = OR_LOGICAL;

				break;

			case XPathConstants.Equals :
				m_opType = EQUAL_GENERAL_COMPARISON;

				break;

			case XPathConstants.NotEquals :
				m_opType = NOTEQUAL_GENERAL_COMPARISON;

				break;

			case XPathConstants.Lt :
				m_opType = LESSTHAN_GENERAL_COMPARISON;

				break;

			case XPathConstants.LtEquals :
				m_opType = LESSOREQUALTHAN_GENERAL_COMPARISON;

				break;

			case XPathConstants.Gt :
				m_opType = GREATTHAN_GENERAL_COMPARISON;

				break;

			case XPathConstants.GtEquals :
				m_opType = GREATOREQUALTHAN_GENERAL_COMPARISON;

				break;

			case XPathConstants.FortranEq :
				m_opType = EQUAL_VALUE_COMPARISON;

				break;

			case XPathConstants.FortranNe :
				m_opType = NOTEQUAL_VALUE_COMPARISON;

				break;

			case XPathConstants.FortranLt :
				m_opType = LESSTHAN_VALUE_COMPARISON;

				break;

			case XPathConstants.FortranLe :
				m_opType = LESSOREQUALTHAN_VALUE_COMPARISON;

				break;

			case XPathConstants.FortranGt :
				m_opType = GREATTHAN_VALUE_COMPARISON;

				break;

			case XPathConstants.FortranGe :
				m_opType = GREATOREQUALTHAN_VALUE_COMPARISON;

				break;

			case XPathConstants.Is :
				m_opType = IS_NODE_COMPARISON;

				break;

			case XPathConstants.IsNot :
				m_opType = ISNOT_NODE_COMPARISON;

				break;

			case XPathConstants.GtGt :
				m_opType = LATERTHAN_ORDER_COMPARISON;

				break;

			case XPathConstants.LtLt :
				m_opType = EARLIERTHAN_ORDER_COMPARISON;

				break;

			default :
				// never
		}
	}

	public void jjtAddChild(Node n, int i)
	{
		// [Aug22Draft][NR]
		if (n.getId() == XPathTreeConstants.JJTUNARYMINUS)
		{
			// Minus expression            
			m_opType = MINUS_UNARY;

		}
		else if (n.getId() == XPathTreeConstants.JJTUNARYPLUS)
		{
			// Plus expression
			m_opType = PLUS_UNARY;
		}
		else
		{
			if (((SimpleNode) n).canBeReduced())
			{
				if (canBeFlatten((ExprImpl) n.jjtGetChild(0)))
				{
					super.jjtInsertNodeChildren(n.jjtGetChild(0), 0);
				}
				else
				{
					super.jjtInsertChild(n.jjtGetChild(0), 0);
				}
			}
			else 
			{
				super.jjtInsertChild(n, 0);
			}
		}
	}

	/**
	 * Can be reduced when there is only one operand (except for unary operator)
	 */
	public boolean canBeReduced()
	{
		return !isUnary() && getOperandCount() <= 1;
	}
}
