/*
 * The Apache Software License, Version 1.1
 * 
 * Copyright (c) 2002-2003 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  3. The end-user documentation included with the redistribution, if any,
 * must include the following acknowledgment: "This product includes software
 * developed by the Apache Software Foundation (http://www.apache.org/)."
 * Alternately, this acknowledgment may appear in the software itself, if and
 * wherever such third-party acknowledgments normally appear.
 *  4. The names "Xalan" and "Apache Software Foundation" must not be used to
 * endorse or promote products derived from this software without prior written
 * permission. For written permission, please contact apache@apache.org.
 *  5. Products derived from this software may not be called "Apache", nor may
 * "Apache" appear in their name, without prior written permission of the
 * Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the Apache Software Foundation and was originally based on
 * software copyright (c) 2002, International Business Machines Corporation.,
 * http://www.ibm.com. For more information on the Apache Software Foundation,
 * please see <http://www.apache.org/> .
 */
package org.apache.xpath.impl;

import org.apache.xpath.XPath20Exception;
import org.apache.xpath.expression.Expr;
import org.apache.xpath.expression.KindTest;
import org.apache.xpath.expression.NodeTest;
import org.apache.xpath.expression.StepExpr;
import org.apache.xpath.expression.Visitor;
import org.apache.xpath.impl.parser.IAxis;
import org.apache.xpath.impl.parser.Node;
import org.apache.xpath.impl.parser.SimpleNode;
import org.apache.xpath.impl.parser.XPath;
import org.apache.xpath.impl.parser.XPathTreeConstants;

/**
 * JavaCC-based {@link org.apache.xpath.expression.StepExpr}implementation.
 * 
 * @author <a href="mailto:villard@us.ibm.com>Lionel Villard</a>
 * @version $Id$
 */
public class StepExprImpl extends ExprImpl implements StepExpr
{
	// Static

	final static boolean[] AXIS_FORWARD =
		{
			false,
			true,
			true,
			false,
			true,
			true,
			true,
			false,
			true,
			false,
			true,
			false,
			true,
			false };

	/** Used when the axis type has not been set yet */
	private static final short NO_AXIS_TYPE = -2;

	/** Used when this step is a filter step. */
	private static final short FILTER_STEP = -1;

	/**
	 * Creates // (slash slash) step
	 */
	final public static StepExprImpl createSlashSlashStep(boolean pattern)
	{
		KindTestImpl kt = new KindTestImpl();
		kt.setKindTest(KindTest.ANY_KIND_TEST);
		return new StepExprImpl(StepExpr.AXIS_DESCENDANT_OR_SELF, kt, pattern);
	}

	// State

	/**
	 * Axis type. Hold FILTER_STEP whenever this step expr is a filter step
	 */
	short m_axisType = NO_AXIS_TYPE;

	// Constructors

	/**
	 * Constructor for StepExprImpl. Internal uses only
	 * 
	 * @param i
	 */
	public StepExprImpl(int i)
	{
		super(i);
	}

	/**
	 * Constructor for StepExprImpl. Internal uses only
	 * 
	 * @param p
	 * @param i
	 */
	public StepExprImpl(XPath p, int i)
	{
		super(p, i);
	}

	/**
	 * Constructor for factory. Internal uses only
	 * 
	 * @param axisType
	 * @param NodeTest
	 */
	protected StepExprImpl(short axisType, NodeTest nodeTest, boolean pattern)
	{
		super(
			pattern
				? XPathTreeConstants.JJTPATTERNSTEP
				: XPathTreeConstants.JJTSTEPEXPR);

		m_axisType = axisType;
		super.jjtAddChild((Node) nodeTest, 0);
	}

	/**
	 * Constructor for factory. Internal uses only
	 * 
	 * @param axisType
	 * @param NodeTest
	 */
	public StepExprImpl(Expr primaryExpr, boolean pattern)
	{
		super(
			pattern
				? XPathTreeConstants.JJTPATTERNSTEP
				: XPathTreeConstants.JJTSTEPEXPR);

		m_axisType = FILTER_STEP;
		super.jjtAddChild((Node) primaryExpr, 0);
	}

	// Methods

	public void getString(StringBuffer expr, boolean abbreviate)
	{
		try
		{
			if (abbreviate
				&& isRootOnSelfNode()
				&& (getParentExpr() == null
					|| getParentExpr().getExprType() != Expr.PATH_EXPR))
			{
				// Can abbreviate if it's not part of a step other than the
				// first one.
				// TODO: not sure that I need this test...
				expr.append('/');
			}
			else if (m_axisType == FILTER_STEP)
			{
				ExprImpl p = (ExprImpl) getPrimaryExpr();

				if ((p.getExprType() == SEQUENCE_EXPR))
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
				else if (
					abbreviate
						&& (m_axisType == AXIS_PARENT)
						&& getNodeTest().isKindTest()
						&& (((KindTest) getNodeTest()).getKindTestType()
							== KindTest.ANY_KIND_TEST))
				{
					expr.append("..");
				}
				else if (
					abbreviate
						&& (m_axisType == AXIS_DESCENDANT_OR_SELF)
						&& getNodeTest().isKindTest()
						&& (((KindTest) getNodeTest()).getKindTestType()
							== KindTest.ANY_KIND_TEST))
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
		catch (XPath20Exception e)
		{
			// never
		}
	}

	/**
	 * Tells whether this step is fn:root(self::node())
	 */
	public boolean isRootOnSelfNode()
	{
		try
		{
			return isFilterStep()
				&& ((ExprImpl) getPrimaryExpr()).isRootOnSelfNode();

		}
		catch (XPath20Exception e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * Tells whether this step is // (descendant-or-self::node())
	 */
	public boolean isSlashSlash()
	{
		try
		{
			return !isFilterStep()
				&& getAxisType() == AXIS_DESCENDANT
				&& getNodeTest().isKindTest()
				&& ((KindTestImpl) getNodeTest()).getKindTestType()
					== KindTest.ANY_KIND_TEST;
		}
		catch (XPath20Exception e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	// Implements StepExpr

	public Expr getPredicateAt(int i)
	{
		return (Expr) m_children.get(i + 1);
	}

	public int getPredicateCount()
	{
		return m_children.size() - 1;
	}

	public void appendPredicate(Expr predicate)
	{
		predicate = selfOrclone(predicate);

		super.jjtAddChild((Node) predicate, m_children.size());
	}

	public void removePredicate(Expr predicate)
	{
		super.jjtRemoveChild((Node) predicate);
	}

	public boolean visit(Visitor visitor)
	{
		// TODO: visit nodetest
		return visitor.visitStep(this);
	}

	public boolean isForwardStep()
	{
		return (m_axisType != FILTER_STEP) && AXIS_FORWARD[m_axisType];
	}

	public boolean isReversedStep()
	{
		return (m_axisType != FILTER_STEP) && !AXIS_FORWARD[m_axisType];
	}

	public boolean isFilterStep()
	{
		return m_axisType == FILTER_STEP;
	}

	public short getAxisType() throws XPath20Exception
	{
		if (m_axisType == FILTER_STEP)
		{
			throw new XPath20Exception("Invalid call of this method on primary expression");
		}

		return m_axisType;
	}

	public void setAxisType(short newType) throws XPath20Exception
	{
		if (m_axisType == FILTER_STEP)
		{
			throw new XPath20Exception("Invalid call of this method on primary expression");
		}

		m_axisType = newType;
	}

	public String getAxisName() throws XPath20Exception
	{
		if (m_axisType == FILTER_STEP)
		{
			throw new XPath20Exception("Invalid call of this method on primary expression");
		}

		if (m_axisType < 0)
			return "!AXES NOT SET!";
		else
			return StepExprImpl.FULL_AXIS_NAME[m_axisType];
	}

	public NodeTest getNodeTest() throws XPath20Exception
	{
		if (m_axisType == FILTER_STEP)
		{
			throw new XPath20Exception("Invalid call of this method on step compose of primary expression");
		}

		return (NodeTest) m_children.get(0);
	}

	public void setNodeTest(NodeTest test) throws XPath20Exception
	{
		if (m_axisType == FILTER_STEP)
		{
			throw new XPath20Exception("Invalid call of this method on step compose of primary expression");
		}

		Expr nt = selfOrclone(test);
		super.jjtAddChild((Node) nt, 0);
	}

	public Expr getPrimaryExpr() throws XPath20Exception
	{
		if (m_axisType != FILTER_STEP)
		{
			throw new XPath20Exception("Invalid call of this method on step compose of node test");
		}

		return (Expr) m_children.get(0);
	}

	// Implements Expr

	public short getExprType()
	{
		return StepExprImpl.STEP_EXPR;
	}

	// Parser

	final public void jjtAddChild(Node n, int i)
	{
		switch (n.getId())
		{
			case XPathTreeConstants.JJTAT :
				m_axisType = AXIS_ATTRIBUTE;

				break;
			case XPathTreeConstants.JJTDOT :
				m_axisType = FILTER_STEP;
				super.jjtAddChild(n, 0);
				break;

			case XPathTreeConstants.JJTAXISCHILD :
			case XPathTreeConstants.JJTAXISDESCENDANT :
			case XPathTreeConstants.JJTAXISANCESTOR :
			case XPathTreeConstants.JJTAXISSELF :
			case XPathTreeConstants.JJTAXISDESCENDANTORSELF :
			case XPathTreeConstants.JJTAXISFOLLOWINGSIBLING :
			case XPathTreeConstants.JJTAXISFOLLOWING :
			case XPathTreeConstants.JJTAXISNAMESPACE :
			case XPathTreeConstants.JJTAXISPARENT :
			case XPathTreeConstants.JJTAXISPRECEDINGSIBLING :
			case XPathTreeConstants.JJTAXISPRECEDING :
			case XPathTreeConstants.JJTAXISANCESTORORSELF :
			case XPathTreeConstants.JJTAXISATTRIBUTE :

				m_axisType = ((IAxis) n).getAxis();

				break;

			case XPathTreeConstants.JJTNODETEST :

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

			case XPathTreeConstants.JJTDOTDOT :
				m_axisType = AXIS_PARENT;

				KindTestImpl node = new KindTestImpl();
				node.m_kindTest = KindTest.ANY_KIND_TEST;
				super.jjtAddChild(node, 0);

				break;

			case XPathTreeConstants.JJTINTEGERLITERAL :
			case XPathTreeConstants.JJTSTRINGLITERAL :
			case XPathTreeConstants.JJTDECIMALLITERAL :
			case XPathTreeConstants.JJTDOUBLELITERAL :
			case XPathTreeConstants.JJTFUNCTIONCALL :
			case XPathTreeConstants.JJTVARNAME :
				m_axisType = FILTER_STEP;
				super.jjtAddChild(n, 0);

				break;

				// [Aug22Draft]
			case XPathTreeConstants.JJTEXPR :
				m_axisType = FILTER_STEP;

				if (((SimpleNode) n).canBeReduced())
				{
					super.jjtAddChild(n.jjtGetChild(0), 0);
				}
				else
				{
					super.jjtAddChild(n, 0);
				}

				break;

			case XPathTreeConstants.JJTPREDICATES :
				int size = n.jjtGetNumChildren();

				for (int j = 0; j < size; j++)
				{
					super.jjtAddChild(n.jjtGetChild(j), size - j);
				}

				break;

			case XPathTreeConstants.JJTNAMETEST :
			default :
				super.jjtAddChild(n, 0);
				//System.out.println("not implemented yet " + n.getId());
		}
	}

	/**
	 * Reduce when: - there is no predicate - and it's a filter step
	 */
	public boolean canBeReduced()
	{
		return isFilterStep() && getPredicateCount() == 0;
	}

	// Debugging

	/**
	 * Override to print out useful instance data.
	 * 
	 * @see org.apache.xpath.impl.parser.SimpleNode#toString()
	 */
	public String toString()
	{
		return XPathTreeConstants.jjtNodeName[id]
			+ " "
			+ getClass()
			+ " "
			+ ((m_axisType == FILTER_STEP)
				? "InvalidAxis"
				: StepExprImpl.FULL_AXIS_NAME[m_axisType]);
	}

}
