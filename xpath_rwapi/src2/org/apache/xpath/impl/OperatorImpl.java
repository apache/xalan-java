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

import org.apache.xpath.XPathException;
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
 *
 */
public class OperatorImpl extends ExprImpl implements OperatorExpr
{
    /**
     * Mapping between operation type and it's external representation
     */
    final private static String[] OPTYPE2STRING = 
    {
        "|", "intersect", "except", "+", "-", "to", "eq", "ne", "lt", "le", "gt",
        "ge", "=", "!=", "<", "<=", ">", ">=", "is", "isnot", "<<", ">>", "and",
        "or", "+", "-", "/", "//", ",", "*", "div", "idiv", "mod"
    };

    /**
     * Indicate whether space is needed around the operator
     */
    final private static boolean[] SPACE_NEEDED = 
    {
        false, true, true, false, false, true, true, true, true, true, true,
        true, false, false, false, false, false, false, true, true, false, false,
        true, true, false, false, false, false, false, false, true, true, true
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
     *
     * @param i
     */
    public OperatorImpl(int i)
    {
        super(i);

        switch (i)
        {
            case XPathTreeConstants.JJTEXPRSEQUENCE:
                m_exprType = SEQUENCE_EXPR;
                m_opType = COMMA;

                break;

            case XPathTreeConstants.JJTUNARYEXPR:
                m_exprType = UNARY_EXPR;

                break;

            case XPathTreeConstants.JJTUNIONEXPR:
			case XPathTreeConstants.JJTINTERSECTEXCEPTEXPR:
            case XPathTreeConstants.JJTPATTERN:
                m_exprType = COMBINE_EXPR;

                // opType is not known yet
                break;

            case XPathTreeConstants.JJTFUNCTIONCALL:
            case XPathTreeConstants.JJTIDKEYPATTERN:

                // ignore : see FunctionCallImpl subclass
                break;

            case XPathTreeConstants.JJTADDITIVEEXPR:
            case XPathTreeConstants.JJTMULTIPLICATIVEEXPR:
                m_exprType = ARITHMETIC_EXPR;

                // opType is not known yet
                break;

            case XPathTreeConstants.JJTOREXPR:
            case XPathTreeConstants.JJTANDEXPR:
                m_exprType = LOGICAL_EXPR;

                //	opType is not known yet
                break;

            case XPathTreeConstants.JJTCOMPARISONEXPR:
                m_exprType = COMPARISON_EXPR;

                // opType is not known yet
                break;

            case XPathTreeConstants.JJTRANGEEXPR:
                m_exprType = RANGE_EXPR;
                m_opType = RANGE;

                break;

            default:

                // Invalid parameter
                throw new IllegalArgumentException(
                    "The parameter value does not correspond to an operator identifier"); // I16
        }
    }

    /**
     * Constructor for OperatorImpl. Internal uses only.
     *
     * @param p
     * @param i
     */
    public OperatorImpl(XPath p, int i)
    {
        super(p, i);
    }

    /**
     * Create a clone     
     */
    protected OperatorImpl(OperatorImpl expr)
    {
        super(expr.id);

        m_exprType = expr.m_exprType;
        m_opType = expr.m_opType;

        m_children = expr.cloneChildren();
    }
    

    /**
     * Constructor for OperatorImpl.
     *
     * @param id
     * @param exprType DOCUMENT ME!
     * @param opType DOCUMENT ME!
     */
    protected OperatorImpl(short exprType, short opType)
    {
        super();

        m_exprType = exprType;
        m_opType = opType;
    }

    /**
     * @see org.apache.xpath.expression.Expr#getExprType()
     */
    public short getExprType()
    {
        return m_exprType;
    }

    /**
     * @see org.apache.xpath.expression.Expr#cloneExpression()
     */
    public Expr cloneExpression()
    {
        return new OperatorImpl(this);
    }

    /**
     * @see org.apache.xpath.expression.Visitable#visit(Visitor)
     */
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
     * @see org.apache.xpath.expression.OperatorExpr#addOperand(Expr)
     */
    public void addOperand(Expr operand) throws XPathException
    {
    	// do not performed the reduction during edition
        super.jjtAddChild((Node) operand,
            (m_children == null) ? 0 : m_children.length);
    }

	/* (non-Javadoc)
	 * @see org.apache.xpath.expression.OperatorExpr#append(org.apache.xpath.expression.OperatorExpr)
	 */
	public void append(OperatorExpr expr) throws XPathException {
		if (expr.getExprType() == m_exprType && expr.getOperatorType() == m_opType ) {
			int size = expr.getOperandCount();
			for (int i = 0; i < size ; i ++ ) {
				addOperand(expr.getOperand(i));			
			}
		} else {
			throw new XPathException("Mismatched operator expressions"); // I16 + better msg
		}
	}

    /**
     * @see org.apache.xpath.expression.OperatorExpr#getOperand(int)
     */
    public Expr getOperand(int i)
    {
        if (m_children == null)
        {
            throw new ArrayIndexOutOfBoundsException();
        }

        return (Expr) m_children[i];
    }

    /**
     * @see org.apache.xpath.expression.OperatorExpr#getOperandCount()
     */
    public int getOperandCount()
    {
        return (m_children == null) ? 0 : m_children.length;
    }

    /**
     * @see org.apache.xpath.expression.OperatorExpr#getOperatorType()
     */
    public short getOperatorType()
    {
        return m_opType;
    }

    /**
     * @see org.apache.xpath.expression.OperatorExpr#removeOperand(Expr)
     */
    public void removeOperand(Expr operand) throws XPathException
    {
        super.jjtRemoveChild((Node) operand);
    }

    /**
     * @see org.apache.xpath.impl.parser.Node#jjtAddChild(Node, int)
     */
    public void jjtAddChild(Node n, int i)
    {
        if (n.getId() == XPathTreeConstants.JJTMINUS)
        {
            // Minus expression            
            m_opType = MINUS_UNARY;
        }
        else if (n.getId() == XPathTreeConstants.JJTPLUS)
        {
            // Plus expression
            m_opType = PLUS_UNARY;
        }
        else
        {
            if (((SimpleNode) n).canBeReduced())
            {
                if (((m_exprType == SEQUENCE_EXPR)
                        && (n.jjtGetNumChildren() > 0)
                        && (n.jjtGetChild(0).getId() == XPathTreeConstants.JJTEXPRSEQUENCE))
                        || ((id == XPathTreeConstants.JJTPATTERN)
                        && (n.jjtGetNumChildren() > 0)
                        && (n.jjtGetChild(0).getId() == XPathTreeConstants.JJTPATTERN)))
                {
                    super.jjtInsertNodeChildren(n.jjtGetChild(0));
                }
                else
                {
                    super.jjtInsertChild(n.jjtGetChild(0));
                }
            }
            else
            {
                super.jjtInsertChild(n);
            }
        }
    }

    /**
     * @see org.apache.xpath.impl.parser.SimpleNode#canBeReduced()
     */
    public boolean canBeReduced()
    {
        if (m_exprType == SEQUENCE_EXPR || id == XPathTreeConstants.JJTPATTERN)
        {
            return (m_children == null) || (m_children.length <= 1);
        }
        		

        return super.canBeReduced();
    }

    /**
     * Gets operator as a char
     *
     * @return DOCUMENT ME!
     */
    protected String getOperatorChar()
    {
        return OPTYPE2STRING[m_opType];
    }

    /**
     * Tell is spaces are needed around the operator
     *
     * @return DOCUMENT ME!
     */
    protected boolean isSpaceNeeded()
    {
        return SPACE_NEEDED[m_opType];
    }

    /**
     * Gets expression as external string representation
     *
     * @param expr DOCUMENT ME!
     * @param abbreviate DOCUMENT ME!
     */
    public void getString(StringBuffer expr, boolean abbreviate)
    {
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

            if ((op.getExprType() == ARITHMETIC_EXPR)
                    || (op.getExprType() == COMBINE_EXPR))
            {
                expr.append('(');
            }

            op.getString(expr, abbreviate);

            if ((op.getExprType() == ARITHMETIC_EXPR)
                    || (op.getExprType() == COMBINE_EXPR))
            {
                expr.append(')');
            }

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
    }

    /**
     * @see org.apache.xpath.impl.parser.SimpleNode#processToken(Token)
     */
    public void processToken(Token token)
    {
        switch (token.kind)
        {
            case XPathConstants.Plus:
                m_opType = PLUS_ADDITIVE;

                break;

            case XPathConstants.Minus:
                m_opType = MINUS_ADDITIVE;

                break;

            case XPathConstants.Multiply:
                m_opType = MULT_PRODUCT;

                break;

            case XPathConstants.Div:
                m_opType = MULT_DIV;

                break;

            case XPathConstants.Idiv:
                m_opType = MULT_IDIV;

                break;

            case XPathConstants.Mod:
                m_opType = MULT_MOD;

                break;

            case XPathConstants.Union:
            case XPathConstants.Vbar:
                m_opType = UNION_COMBINE;

                break;

            case XPathConstants.Intersect:
                m_opType = INTERSECT_COMBINE;

                break;

            case XPathConstants.Except:
                m_opType = EXCEPT_COMBINE;

                break;

            case XPathConstants.And:
                m_opType = AND_LOGICAL;

                break;

            case XPathConstants.Or:
                m_opType = OR_LOGICAL;

                break;

            case XPathConstants.Equals:
                m_opType = EQUAL_GENERAL_COMPARISON;

                break;

            case XPathConstants.NotEquals:
                m_opType = NOTEQUAL_GENERAL_COMPARISON;

                break;

            case XPathConstants.Lt:
                m_opType = LESSTHAN_GENERAL_COMPARISON;

                break;

            case XPathConstants.LtEquals:
                m_opType = LESSOREQUALTHAN_GENERAL_COMPARISON;

                break;

            case XPathConstants.Gt:
                m_opType = GREATTHAN_GENERAL_COMPARISON;

                break;

            case XPathConstants.GtEquals:
                m_opType = GREATOREQUALTHAN_GENERAL_COMPARISON;

                break;

            case XPathConstants.FortranEq:
                m_opType = EQUAL_VALUE_COMPARISON;

                break;

            case XPathConstants.FortranNe:
                m_opType = NOTEQUAL_VALUE_COMPARISON;

                break;

            case XPathConstants.FortranLt:
                m_opType = LESSTHAN_VALUE_COMPARISON;

                break;

            case XPathConstants.FortranLe:
                m_opType = LESSOREQUALTHAN_VALUE_COMPARISON;

                break;

            case XPathConstants.FortranGt:
                m_opType = GREATTHAN_VALUE_COMPARISON;

                break;

            case XPathConstants.FortranGe:
                m_opType = GREATOREQUALTHAN_VALUE_COMPARISON;

                break;

            case XPathConstants.Is:
                m_opType = IS_NODE_COMPARISON;

                break;

            case XPathConstants.IsNot:
                m_opType = ISNOT_NODE_COMPARISON;

                break;

            case XPathConstants.GtGt:
                m_opType = LATERTHAN_ORDER_COMPARISON;

                break;

            case XPathConstants.LtLt:
                m_opType = EARLIERTHAN_ORDER_COMPARISON;

                break;

            default:
            // never
        }
    }
}
