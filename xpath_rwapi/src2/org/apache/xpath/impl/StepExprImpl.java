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
import org.apache.xpath.expression.NodeTest;
import org.apache.xpath.expression.StepExpr;
import org.apache.xpath.expression.Visitor;
import org.apache.xpath.impl.parser.Axis;
import org.apache.xpath.impl.parser.Node;
import org.apache.xpath.impl.parser.SimpleNode;
import org.apache.xpath.impl.parser.XPath;
import org.apache.xpath.impl.parser.XPathTreeConstants;


/**
 * Default implementation of step. 
 * %review% step is not an expression by itself: it's always ebbeded in path expression. 
 * So make it inherit of SimpleNode?
 */
public class StepExprImpl extends ExprImpl implements StepExpr
{
    final static boolean[] AXIS_FORWARD = 
                                          {
                                              false, true, true, false, true,
                                              true, true, false, true, false,
                                              true, false, true, false
                                          };

    /** Used when no axis apparent */
    private final short NO_AXIS_TYPE = -2;
    
    /** Used when this is a primary expr.  */
    private final short STEP_IS_PRIMARYEXPR = -1;

    /**
     * Axis type. STEP_IS_PRIMARYEXPR whenever this step expr is a primary expr
     */
    short m_axisType = NO_AXIS_TYPE;

    /**
     * Constructor for StepExprImpl.
     * @param i
     */
    public StepExprImpl(int i)
    {
        super(i);
    }

    /**
     * Constructor for StepExprImpl.
     * @param p
     * @param i
     */
    public StepExprImpl(XPath p, int i)
    {
        super(p, i);
    }

    /**
     * Constructor for StepExprImpl.
     * @param axisType
     * @param NodeTest
     */
    public StepExprImpl(short axisType, NodeTest nodeTest)
    {
        super(XPathTreeConstants.JJTSTEPEXPR);

        m_axisType = axisType;
        super.jjtAddChild((Node) nodeTest, 0);

        //  super.jjtAddChild(new OperatorImpl(XPathTreeConstants.JJTPREDICATES), 1);
    }
    
    /**
     * Constructor for cloning     
     */
    private StepExprImpl(StepExprImpl step)
    {
    	
    }

    /**
     * @see org.apache.xpath.expression.StepExpr#getPredicateAt(int)
     */
    public Expr getPredicateAt(int i)
    {
        return (Expr) children[i + 1];
    }

    /**
     * @see org.apache.xpath.expression.StepExpr#getPredicateCount()
     */
    public int getPredicateCount()
    {
        return children.length - 1;
    }

    /**
     * @see org.apache.xpath.expression.StepExpr#appendPredicate(Expr)
     */
    public void appendPredicate(Expr predicate)
    {
        super.jjtAddChild((Node) predicate, children.length);
    }

    /**
     * @see org.apache.xpath.expression.StepExpr#removePredicate(Expr)
     */
    public void removePredicate(Expr predicate) {}

    /**
     * @see org.apache.xpath.expression.Visitable#visit(Visitor)
     */
    public void visit(Visitor visitor)
    {
        visitor.visitStep(this);
    }

    /**
     * @see org.apache.xpath.expression.StepExpr#isForwardStep()
     */
    public boolean isForwardStep()
    {
        return (m_axisType != STEP_IS_PRIMARYEXPR) && AXIS_FORWARD[m_axisType];
    }

    /**
     * @see org.apache.xpath.expression.StepExpr#isReversedStep()
     */
    public boolean isReversedStep()
    {
        return (m_axisType != STEP_IS_PRIMARYEXPR) && !AXIS_FORWARD[m_axisType];
    }

    /**
     * @see org.apache.xpath.expression.StepExpr#isPrimaryExpr()
     */
    public boolean isPrimaryExpr()
    {
        return m_axisType == STEP_IS_PRIMARYEXPR;
    }

    /**
     * @see org.apache.xpath.expression.StepExpr#getAxisType()
     */
    public short getAxisType() throws XPathException
    {
        if (m_axisType == STEP_IS_PRIMARYEXPR)
        {
            throw new XPathException("Invalid call of this method on primary expression");
        }

        return m_axisType;
    }

    /**
     * @see org.apache.xpath.expression.StepExpr#setAxisType(short)
     */
    public void setAxisType(short newType) throws XPathException
    {
        if (m_axisType == STEP_IS_PRIMARYEXPR)
        {
            throw new XPathException("Invalid call of this method on primary expression");
        }

        m_axisType = newType;
    }

    /**
     * @see org.apache.xpath.expression.StepExpr#getAxisName()
     */
    public String getAxisName() throws XPathException
    {
        if (m_axisType == STEP_IS_PRIMARYEXPR)
        {
            throw new XPathException("Invalid call of this method on primary expression");
        }

        return StepExprImpl.FULL_AXIS_NAME[m_axisType];
    }

    /**
     * @see org.apache.xpath.expression.StepExpr#getStepNodeTest()
     */
    public NodeTest getNodeTest() throws XPathException
    {
        if (m_axisType == STEP_IS_PRIMARYEXPR)
        {
            throw new XPathException("Invalid call of this method on step compose of primary expression");
        }

        return (NodeTest) children[0];
    }

    /**
     * @see org.apache.xpath.expression.StepExpr#getPrimaryExpr()
     */
    public Expr getPrimaryExpr() throws XPathException
    {
        if (m_axisType != STEP_IS_PRIMARYEXPR)
        {
            throw new XPathException("Invalid call of this method on step compose of node test");
        }

        return (Expr) children[0];
    }

    /**
     * @see org.apache.xpath.expression.StepExpr#cloneStep()
     */
    public StepExpr cloneStep()
    {
        return null;
    }

    /**
     * @see org.apache.xpath.expression.Expr#getExprType()
     */
    public short getExprType()
    {
        return StepExprImpl.STEP;
    }

    /**
     * @see org.apache.xpath.expression.Expr#cloneExpression()
     */
    public Expr cloneExpression()
    {
        return null;
    }

    /**
     * @see org.apache.xpath.impl.parser.Node#jjtAddChild(Node, int)
     */
    final public void jjtAddChild(Node n, int i)
    {
        switch (n.getId())
        {
            case XPathTreeConstants.JJTAT:
                m_axisType = AXIS_ATTRIBUTE;

                break;
			case XPathTreeConstants.JJTDOT:
				m_axisType = AXIS_SELF;
				super.jjtAddChild(n, 0);
			break;
            case XPathTreeConstants.JJTAXISCHILD:
            case XPathTreeConstants.JJTAXISDESCENDANT:
            case XPathTreeConstants.JJTAXISANCESTOR:
            case XPathTreeConstants.JJTAXISSELF:
            case XPathTreeConstants.JJTAXISDESCENDANTORSELF:
            case XPathTreeConstants.JJTAXISFOLLOWINGSIBLING:
            case XPathTreeConstants.JJTAXISFOLLOWING:
            case XPathTreeConstants.JJTAXISNAMESPACE:
            case XPathTreeConstants.JJTAXISPARENT:
            case XPathTreeConstants.JJTAXISPRECEDINGSIBLING:
            case XPathTreeConstants.JJTAXISPRECEDING:
            case XPathTreeConstants.JJTAXISANCESTORORSELF:
			case XPathTreeConstants.JJTAXISATTRIBUTE:

                m_axisType = ((Axis) n).getAxis();

                break;

            case XPathTreeConstants.JJTNODETEST:

                if (m_axisType == NO_AXIS_TYPE)
                {
                    // NodeTest production
                    m_axisType = AXIS_CHILD;
                    super.jjtAddChild(n.jjtGetChild(0), 0);
                }
                else
                {
                    // reduce
                    super.jjtAddChild(n.jjtGetChild(0), 0);
                }

                break;

            case XPathTreeConstants.JJTDOTDOT:
                m_axisType = AXIS_PARENT;

                KindTestImpl node = new KindTestImpl();
                node.m_kindTest = NodeTest.ANY_KIND_TEST;
                super.jjtAddChild(node, 0);

                break;

            case XPathTreeConstants.JJTINTEGERLITERAL:
            case XPathTreeConstants.JJTSTRINGLITERAL:
            case XPathTreeConstants.JJTDECIMALLITERAL:
            case XPathTreeConstants.JJTDOUBLELITERAL:
            case XPathTreeConstants.JJTFUNCTIONCALL:
            case XPathTreeConstants.JJTVARNAME:
                m_axisType = STEP_IS_PRIMARYEXPR;
                super.jjtAddChild(n, 0);

                break;

            case XPathTreeConstants.JJTEXPRSEQUENCE:
                m_axisType = STEP_IS_PRIMARYEXPR;

			
                if (((SimpleNode) n).canBeReduced())
                {
                    super.jjtAddChild(n.jjtGetChild(0), 0);
                }
                else
                {
                    super.jjtAddChild(n, 0);
                }

                break;

            case XPathTreeConstants.JJTPREDICATES:				
                int size = n.jjtGetNumChildren();

                for (int j = 0; j < size; j++)
                {
                    super.jjtAddChild(n.jjtGetChild(j), size - j);
                }

                break;
                
			case XPathTreeConstants.JJTNAMETEST:				
            default:
				super.jjtAddChild(n, 0);
                //System.out.println("not implemented yet " + n.getId());
        }
    }

    /**
     * @see org.apache.xpath.impl.ExprImpl#getString(StringBuffer, boolean)
     */
    public void getString(StringBuffer expr, boolean abbreviate)
    {
        try
        {
            if (m_axisType == STEP_IS_PRIMARYEXPR)
            {
                ExprImpl p = (ExprImpl) getPrimaryExpr();

                if ((p.getExprType() == SEQUENCE_EXPR)
                        || (p.getExprType() == COMBINE_EXPR)
                        || (p.getExprType() == RANGE_EXPR))
                {
                    expr.append('(');
                    p.getString(expr, abbreviate);
                    expr.append(')');
                }
                else
                {
                    p.getString(expr, abbreviate);
                }
            }
            else
            {
                if (abbreviate && (m_axisType == AXIS_CHILD))
                {
                    ((SimpleNode) getNodeTest()).getString(expr, abbreviate);
                }
                else if (abbreviate && (m_axisType == AXIS_ATTRIBUTE))
                {
                    expr.append("@");
                    ((SimpleNode) getNodeTest()).getString(expr, abbreviate);
                }
                else if (abbreviate && (m_axisType == AXIS_PARENT)
                             && getNodeTest().isKindTest()
                             && (getNodeTest().getKindTest() == NodeTest.ANY_KIND_TEST))
                {
                    expr.append("..");
                }
                else if (abbreviate && (m_axisType == AXIS_DESCENDANT_OR_SELF)
                             && getNodeTest().isKindTest()
                             && (getNodeTest().getKindTest() == NodeTest.ANY_KIND_TEST))
                {
                    // empty step
                }
                else
                {
                    expr.append(getAxisName()).append("::");
                    ((SimpleNode) getNodeTest()).getString(expr, abbreviate);
                }
            }

            // Predicates
            int size = getPredicateCount();

            for (int i = 0; i < size; i++)
            {
                expr.append('[');
                ((ExprImpl) getPredicateAt(i)).getString(expr, abbreviate);
                expr.append(']');
            }
        }
        catch (XPathException e)
        {
            // never
        }
    }

    /**
     * Override to print out useful instance data.  
     * @see org.apache.xpath.impl.parser.SimpleNode#toString()
     */
    public String toString()
    {
        return XPathTreeConstants.jjtNodeName[id] + " " 
                + getClass() + " " 
                + ((m_axisType == STEP_IS_PRIMARYEXPR) ? "InvalidAxis" : StepExprImpl.FULL_AXIS_NAME[m_axisType]);
    }
}
