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

import org.apache.xml.QName;
import org.apache.xpath.XPath20Exception;
import org.apache.xpath.expression.Expr;
import org.apache.xpath.expression.FunctionCall;
import org.apache.xpath.expression.KindTest;
import org.apache.xpath.expression.StepExpr;
import org.apache.xpath.impl.parser.Node;
import org.apache.xpath.impl.parser.SimpleNode;
import org.apache.xpath.impl.parser.XPath;
import org.apache.xpath.impl.parser.XPathTreeConstants;

/**
 * JavaCC-based {@link org.apache.xpath.expression.FunctionCall} implementation. 
 * @author <a href="mailto:villard@us.ibm.com">Lionel Villard</a>
 * @version $Id$
 */
public class FunctionCallImpl extends OperatorImpl implements FunctionCall
{
	/**
	 * Name of the function
	 */
	QName m_qname;

	/**
	 * Is fn:root(self::node()) ?
	 * -1: unset
	 *  0: false
	 *  1: true		 
	 */
	private short m_isRootOnSelfNode = -1;

	// Constructors

	/**
	 * Constructor for FunctionCallImpl.
	 * @param i
	 */
	public FunctionCallImpl(int i)
	{
		super(i);
	}

	/**
	 * Constructor for FunctionCallImpl.
	 * @param p
	 * @param i
	 */
	public FunctionCallImpl(XPath p, int i)
	{
		super(p, i);
	}

	/**
	 * Constructor for FunctionCallImpl with specified name
	 */
	protected FunctionCallImpl(QName name)
	{
		super(XPathTreeConstants.JJTFUNCTIONCALL);

		m_qname = name;
	}

	// Methods

	protected boolean isFirstStep()
	{
		// This test is valid only in compact mode.
		return !withinExpr(Expr.PATH_EXPR);
	}
	
	public void getString(StringBuffer expr, boolean abbreviate)
	{
		if (abbreviate
			&& isRootOnSelfNode()
			&& isFirstStep())
		{
			expr.append('/');
		}
		else
		{
			expr.append(m_qname.toString());
			expr.append('(');
			int size = getOperandCount();
			for (int i = 0; i < size; i++)
			{
				((ExprImpl) getOperand(i)).getString(expr, abbreviate);
				if (i < size - 1)
				{
					expr.append(',');
				}
			}
			expr.append(')');
		}

	}

	public String getOperatorChar()
	{
		return ",";
	}

	public boolean isRootOnSelfNode()
	{
		if (m_isRootOnSelfNode == -1)
		{
			try
			{
				if (m_qname.getLocalPart().equals("root")
					&& m_qname.getNamespaceURI().equals(
						DefaultStaticContext.XPATH_FUNCTIONS_URI)
					&& getOperandCount() == 1
					&& getOperand(0).getExprType() == Expr.STEP_EXPR)
				{
					StepExpr s = (StepExpr) getOperand(0);
					if (s.isForwardStep()
						&& s.getAxisType() == StepExpr.AXIS_SELF
						&& s.getNodeTest().isKindTest())
					{
						m_isRootOnSelfNode =
							(short) (((KindTest) s.getNodeTest())
								.getKindTestType()
								== KindTest.ANY_KIND_TEST
								? 1
								: 0);
					}
				}
			}
			catch (XPath20Exception e)
			{
				throw new RuntimeException(e.getMessage());
			}
		}
		return m_isRootOnSelfNode == 1;
	}

	// Implements FunctionCall

	public QName getFunctionQName()
	{
		return m_qname;
	}

	// Implements OperatorExpr

	public Expr getOperand(int i)
	{
		return (Expr) m_children.get(i);
	}

	public int getOperandCount()
	{
		return (m_children == null) ? 0 : m_children.size();
	}

	public short getOperatorType()
	{
		return COMMA;
	}

	// Implements Expr

	final public short getExprType()
	{
		return FUNCTION_CALL_EXPR;
	}

	// Parser

	final public void jjtAddChild(Node n, int i)
	{

		if (n.getId() == XPathTreeConstants.JJTQNAMELPAR)
		{
			m_qname =
				((org.apache.xpath.impl.parser.IQNameWrapper) n).getQName();
		}
		else
		{
			// parameter
			// -1 because of QNAMELPAR 
			if (((SimpleNode) n).canBeReduced())
			{
				super.jjtAddChild(n.jjtGetChild(0), i - 1);
			}
			else
			{
				super.jjtAddChild(n, i - 1);
			}
		}

	}

	/**
	 * Never reduce a function call.
	 */
	public boolean canBeReduced()
	{
		return false;
	}

}
