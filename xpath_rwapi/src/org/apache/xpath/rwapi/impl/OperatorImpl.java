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
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xpath.rwapi.impl;

import org.apache.xpath.rwapi.XPathException;
import org.apache.xpath.rwapi.expression.Expr;
import org.apache.xpath.rwapi.expression.OperatorExpr;
import org.apache.xpath.rwapi.expression.Visitor;
import org.apache.xpath.rwapi.impl.parser.Node;
import org.apache.xpath.rwapi.impl.parser.SimpleNode;
import org.apache.xpath.rwapi.impl.parser.Token;
import org.apache.xpath.rwapi.impl.parser.XPath;
import org.apache.xpath.rwapi.impl.parser.XPathConstants;
import org.apache.xpath.rwapi.impl.parser.XPathTreeConstants;


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
    short m_exprType;
    short m_opType;

    /**
     * Constructor for OperatorImpl.
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

            case XPathTreeConstants.JJTPATHEXPR:
                m_exprType = PATH_EXPR;
                m_opType = SLASH_STEP;

                break;

            case XPathTreeConstants.JJTUNIONEXPR:
                m_exprType = COMBINE_EXPR;

                // opType is not known yet
                break;

            case XPathTreeConstants.JJTFUNCTIONCALL:

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

                //			opType is not known yet
                break;

            case XPathTreeConstants.JJTRANGEEXPR:
                m_exprType = RANGE_EXPR;
                m_opType = RANGE;

                break;

            default:
                System.out.println("OperatorImpl: not implemented yet:" + i);
        }
    }

    /**
     * Constructor for OperatorImpl.
     *
     * @param p
     * @param i
     */
    public OperatorImpl(XPath p, int i)
    {
        super(p, i);
    }

    /**
     * @see org.apache.xpath.rwapi.expression.Expr#getExprType()
     */
    public short getExprType()
    {
        return m_exprType;
    }

    /**
     * @see org.apache.xpath.rwapi.expression.Expr#cloneExpression()
     */
    public Expr cloneExpression()
    {
        return null;
    }

    /**
     * @see org.apache.xpath.rwapi.expression.Visitable#visit(Visitor)
     */
    public void visit(Visitor visitor)
    {
        int count = getOperandCount();

        for (int i = 0; i < count; i++)
        {
            getOperand(i).visit(visitor);
        }
    }

    /**
     * @see org.apache.xpath.rwapi.expression.OperatorExpr#addOperand(Expr)
     */
    public void addOperand(Expr operand) throws XPathException
    {
        super.jjtAddChild((Node) operand,
            (children == null) ? 0 : children.length);
    }

    /**
     * @see org.apache.xpath.rwapi.expression.OperatorExpr#getOperand(int)
     */
    public Expr getOperand(int i)
    {
        if (children == null)
        {
            throw new ArrayIndexOutOfBoundsException();
        }

        return (Expr) children[i];
    }

    /**
     * @see org.apache.xpath.rwapi.expression.OperatorExpr#getOperandCount()
     */
    public int getOperandCount()
    {
        return (children == null) ? 0 : children.length;
    }

    /**
     * @see org.apache.xpath.rwapi.expression.OperatorExpr#getOperatorType()
     */
    public short getOperatorType()
    {
        return m_opType;
    }

    /**
     * @see org.apache.xpath.rwapi.expression.OperatorExpr#removeOperand(Expr)
     */
    public void removeOperand(Expr operand) throws XPathException
    {
    }

    /**
     * @see org.apache.xpath.rwapi.impl.parser.Node#jjtAddChild(Node, int)
     */
    public void jjtAddChild(Node n, int i)
    {
        // Filter operator
        // if (n.getId() == XPathTreeConstants.JJTSLASH)
        // {
        // Filter
        // }
        //else 
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
                if ((m_exprType == SEQUENCE_EXPR)
                        && (n.jjtGetNumChildren() > 0)
                        && (n.jjtGetChild(0).getId() == XPathTreeConstants.JJTEXPRSEQUENCE))
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
     * @see org.apache.xpath.rwapi.impl.parser.SimpleNode#canBeReduced()
     */
    public boolean canBeReduced()
    {
        if (m_exprType == SEQUENCE_EXPR)
        {
            return (children == null) || (children.length <= 1);
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
    protected void getString(StringBuffer expr, boolean abbreviate)
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

            if (op.getExprType() == ARITHMETIC_EXPR || op.getExprType() == COMBINE_EXPR)
            {
                expr.append('(');
            }

            op.getString(expr, abbreviate);

            if (op.getExprType() == ARITHMETIC_EXPR || op.getExprType() == COMBINE_EXPR)
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
     * @see org.apache.xpath.rwapi.impl.parser.SimpleNode#processToken(Token)
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
            // Bug.. report
        }
    }
}
