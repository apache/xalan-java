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

import org.apache.xpath.expression.Expr;
import org.apache.xpath.expression.ForAndQuantifiedExpr;
import org.apache.xpath.expression.Variable;
import org.apache.xpath.expression.Visitor;
import org.apache.xpath.impl.parser.Node;
import org.apache.xpath.impl.parser.SimpleNode;
import org.apache.xpath.impl.parser.XPath;
import org.apache.xpath.impl.parser.XPathTreeConstants;


/**
 * AST node for 'for' and quantified (some, every) expressions
 */
public class ForAndQuantifiedExprImpl extends ExprImpl
    implements ForAndQuantifiedExpr
{
    /**
     * The expression of for/quantified clauses
     */
    ExprImpl[] m_exprs;

    /**
     * The variable name of for/quantified clauses
     */
    VariableImpl[] m_varNames;

    /**
     * The return/satisfies expr
     */
    ExprImpl m_resExpr;

    /**
     * Constructor for ForAndQuantifiedExprImpl.
     * @param i
     */
    public ForAndQuantifiedExprImpl(int i)
    {
        super(i);
    }

    /**
     * Constructor for ForAndQuantifiedExprImpl.
     * @param p
     * @param i
     */
    public ForAndQuantifiedExprImpl(XPath p, int i)
    {
        super(p, i);
    }

    /**
     * @see org.apache.xpath.expression.ForAndQuantifiedExpr#getClauseVarName(int)
     */
    public Variable getClauseVarName(int i) throws IndexOutOfBoundsException
    {
        return m_varNames[i];
    }

    /**
     * @see org.apache.xpath.expression.ForAndQuantifiedExpr#getClauseExpr(int)
     */
    public Expr getClauseExpr(int i) throws IndexOutOfBoundsException
    {
        return m_exprs[i];
    }

    /**
     * @see org.apache.xpath.expression.ForAndQuantifiedExpr#getClauseCount()
     */
    public int getClauseCount()
    {
        return (m_varNames == null) ? 0 : m_varNames.length;
    }

    /**
     * @see org.apache.xpath.expression.ForAndQuantifiedExpr#getResultingExpr()
     */
    public Expr getResultingExpr()
    {
        return m_resExpr;
    }

    /**
     * @see org.apache.xpath.expression.ForAndQuantifiedExpr#addClause(String, Expr)
     */
    public void addClause(String varName, Expr expr)
    {
        // TODO
    }

    /**
     * @see org.apache.xpath.expression.ForAndQuantifiedExpr#removeClause(String)
     */
    public void removeClause(String varName)
    {
        // TODO
    }

    /**
     * @see org.apache.xpath.expression.Expr#getExprType()
     */
    public short getExprType()
    {
        switch (id)
        {
            case XPathTreeConstants.JJTFLWREXPR:
                return ITERATION_EXPR;

            case XPathTreeConstants.JJTEVERY:
                return EVERY_EXPR;

            case XPathTreeConstants.JJTSOME:
                return SOME_EXPR;
        }

        // bug
        throw new RuntimeException("Invalid object state");
    }

    /**
     * @see org.apache.xpath.expression.Expr#cloneExpression()
     */
    public Expr cloneExpression()
    {
        // TODO
        return null;
    }

    /**
    * @see org.apache.xpath.impl.parser.Node#jjtAddChild(Node, int)
    */
    final public void jjtAddChild(Node n, int i)
    {
    	
        if (((SimpleNode) n).canBeReduced())
        {
            super.jjtAddChild(n.jjtGetChild(0), i);
        }
        else
        {
            super.jjtAddChild(n, i);
        }
    }

    /**
     * @see org.apache.xpath.impl.parser.SimpleNode#canBeReduced()
     */
    public boolean canBeReduced()
    {
        return children.length == 1; // means that there is no???
    }

    /**
     * @see org.apache.xpath.expression.Visitable#visit(Visitor)
     */
    public void visit(Visitor visitor)
    {
        // TODO:
    }

    /* (non-Javadoc)
     * @see org.apache.xpath.impl.ExprImpl#getString(java.lang.StringBuffer, boolean)
     */
    public void getString(StringBuffer expr, boolean abbreviate)
    {
        switch (getExprType())
        {
            case ITERATION_EXPR:
                expr.append("for ");

                break;

            case EVERY_EXPR:
                expr.append("every ");

                break;

            case SOME_EXPR:
                expr.append("some ");

                break;

            default:
                throw new RuntimeException("Invalid object state");
        }

        for (int i = 0; i < m_varNames.length; i++)
        {
            expr.append(m_varNames[i].getVariableName()).append(" in ");
            m_exprs[i].getString(expr, abbreviate);
        }

        switch (getExprType())
        {
            case ITERATION_EXPR:
                expr.append(" return ");

                break;

            case EVERY_EXPR:
            case SOME_EXPR:
                expr.append(" satifies ");

                break;

            default:
                throw new RuntimeException("Invalid object state");
        }

        m_resExpr.getString(expr, abbreviate);
    }
	

}
