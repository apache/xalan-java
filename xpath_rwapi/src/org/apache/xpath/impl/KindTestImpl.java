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
import org.apache.xpath.expression.KindTest;
import org.apache.xpath.expression.Visitor;
import org.apache.xpath.impl.parser.INodeName;
import org.apache.xpath.impl.parser.Node;
import org.apache.xpath.impl.parser.IStringLiteral;
import org.apache.xpath.impl.parser.XPath;
import org.apache.xpath.impl.parser.XPathTreeConstants;

/**
 * JavaCC-based {@link org.apache.xpath.expression.KindTest} implementation.
 * 
 * @author <a href="mailto:villard@us.ibm.com>Lionel Villard</a>
 * @version $Id$
 */
public class KindTestImpl extends ExprImpl implements KindTest
{

	/**
	 * The type of kindtest
	 */
	protected short m_kindTest;

	/**
	 * The subtype of kindtest
	 */
	protected short m_subkindTest;

	/**
	 * Node name or local name
	 */
	protected QName m_nodeName;

	/**
	 * Type Name
	 */
	protected QName m_typeName;

	/**
	 * PI target
	 */
	protected String m_pitarget;

	/**
	 * Element test
	 */
	protected KindTestImpl m_elementTest;

	// Constructors

	/**
	 * Creates an any kind test node. Internal uses only
	 */
	public KindTestImpl()
	{
		super(XPathTreeConstants.JJTANYKINDTEST);
		//		default subkindtype: WILDCARD
	}

	/**
	 * Constructor for KindTestImpl. Internal uses only     
	 * @param i
	 */
	public KindTestImpl(int i)
	{
		super(i);

		setKindTestFromJJTID(i);
		// default subkindtype: WILDCARD
	}

	/**
	 * Constructor for KindTestImpl. Internal uses only
	 *
	 * @param p
	 * @param i
	 */
	public KindTestImpl(XPath p, int i)
	{
		super(p, i);

		setKindTestFromJJTID(i);
		//		default subkindtype: WILDCARD
	}

	// Methods

	public void getString(StringBuffer expr, boolean abbreviate)
	{
		switch (m_kindTest)
		{
			case TEXT_TEST :
			case COMMENT_TEST :
			case ANY_KIND_TEST :
				expr.append(KIND_TEST_NAME[m_kindTest]);
				break;
			case PROCESSING_INSTRUCTION_TEST :
				expr.append("processing-instruction(");
				if (m_pitarget != null)
				{
					expr.append(m_pitarget);
					// XPath 2.0 compatibility				
				}
				expr.append(')');
				break;
			case DOCUMENT_TEST :
				expr.append("document-node(");
				if (m_elementTest != null)
				{
					m_elementTest.getString(expr, abbreviate);
				}
				expr.append(')');
				break;
			case ELEMENT_TEST :
			case ATTRIBUTE_TEST :
				expr.append(
					m_kindTest == ELEMENT_TEST ? "element(" : "attribute(");

				switch (m_subkindTest)
				{
					case WILDCARD :
						// normal form: ()
						//expr.append("*, *");
						break;
					case NODE_TYPE :
					case NODE_TYPE_NILLABLE :
					case WILDCARD_TYPE_NILLABLE :
						expr.append(m_kindTest == ELEMENT_TEST ? "" : "@");
						if (m_subkindTest == WILDCARD_TYPE_NILLABLE)
						{
							expr.append("*, ");
						}
						else
						{
							expr.append(m_nodeName).append(", ");
						}
						expr.append(m_typeName);
						if (m_subkindTest != NODE_TYPE)
						{
							expr.append(" nillable");
						}
						break;
					case NODE_WILDCARD :
						expr.append(m_kindTest == ELEMENT_TEST ? "" : "@");
						expr.append(m_nodeName);// .append(", ").append('*');
						break;
					case NODE :
						expr.append('@').append(m_nodeName);
						break;
					case WILDCARD_TYPE :
						expr.append(m_kindTest == ELEMENT_TEST ? "*," : "@*,");
						expr.append(m_typeName);
						break;
					case SCHEMA_PATH :
						// TODO:
						break;
					default :
						throw new RuntimeException(
							"Invalid kind test subtype: " + m_subkindTest);
				}
				expr.append(')');
				break;
			default :
				throw new RuntimeException(
					"Invalid kind test type: " + m_kindTest);

		}
	}

	/**
	 * If the m_kindTest is non-null, use this function to 
	 * get the kind test name plus the name of the PI.  For example:
	 * processing-instruction('a-pi')
	 * @param sb String buffer that must be non-null.
	 */
	private void getStringForNamedPI(StringBuffer sb)
	{
		String s = KIND_TEST_NAME[m_kindTest];

		sb.append(s.substring(0, s.length() - 1));
		sb.append("'");
		sb.append(m_pitarget);
		sb.append("')");
	}

	/**
	 * Sets the kindTest.
	 *
	 * @param kindTest The kindTest to set
	 */
	public void setKindTest(short kindTest)
	{
		m_kindTest = kindTest;
	}

	//	Implements KindTest

	public short getKindTestSubtype()
	{
		return m_subkindTest;
	}

	public short getKindTestType()
	{
		return m_kindTest;
	}

	public QName getNodeName() throws XPath20Exception
	{
		//TODO:check

		return m_nodeName;
	}

	public String getPITarget() throws XPath20Exception
	{
		//		TODO:check
		return m_pitarget;
	}

	public String getSchemaContext() throws XPath20Exception
	{
		//		TODO:check
		// TODO Auto-generated method stub
		return null;
	}

	public QName getTypeName() throws XPath20Exception
	{
		//		TODO:check
		return m_typeName;
	}

	public KindTest getElementTest() throws XPath20Exception
	{
		return m_elementTest;
	}

	// Implements NodeTest

	final public boolean isNameTest()
	{
		return false;
	}

	final public boolean isKindTest()
	{
		return true;
	}

	// soon in the API
	public void setTypeName(QName typeName) throws XPath20Exception
	{
		//		TODO:check
		m_typeName = typeName;
	}

	public void setNodeName(QName nodeName) throws XPath20Exception
	{
		//		TODO:check
		m_nodeName = nodeName;
	}

	//	Implements Expr

	public short getExprType()
	{
		return Expr.NODE_TEST_EXPR;
	}

	public boolean visit(Visitor visitor)
	{
		return visitor.visitKindTest(this);
	}

	// Parser

	public void jjtAddChild(Node n, int i)
	{
		// Reminder: node are received in reversed order.

		switch (n.getId())
		{
			case XPathTreeConstants.JJTSTRINGLITERALFORKINDTEST :
			case XPathTreeConstants.JJTNCNAMEFORPI :
				//assert m_kindTest == PROCESSING_INSTRUCTION_TEST;
				m_pitarget = ((IStringLiteral) n).getStr();
				break;

			case XPathTreeConstants.JJTNODENAME :
				{
					//	subkindtest may have been previously set (nillable and typename) 
					INodeName nn = (INodeName) n;
					if (nn.isWildCard())
					{
						// all subtype are correct.
					}
					else
					{
						switch (m_subkindTest)
						{
							case WILDCARD :
								m_subkindTest = NODE_WILDCARD;
								break;
							case WILDCARD_TYPE_NILLABLE :
								m_subkindTest = NODE_TYPE_NILLABLE;
								break;
							case WILDCARD_TYPE :
								m_subkindTest = NODE_TYPE;
								break;
							default :
								throw new RuntimeException(
									"Invalid state: " + m_subkindTest);

						}
						try
						{
							setNodeName(nn.getQName());
						}
						catch (XPath20Exception e)
						{
							throw new RuntimeException(e.getMessage());
						}
					}
				}
				break;

			case XPathTreeConstants.JJTTYPENAME :
				{
					INodeName nn = (INodeName) n;
					if (nn.isWildCard())
					{
						// either WILDCARD or NODE_WILDCARD					
						m_subkindTest = WILDCARD;
						// may becomes a NODE_TYPE if a NodeName child is added
					}
					else
					{
						// subkindtest may have been previously set 
						if (m_subkindTest != WILDCARD_TYPE_NILLABLE)
						{
							// either NODE_TYPE or WILDCARD_TYPE
							m_subkindTest = WILDCARD_TYPE;
							// may becomes a NODE_TYPE if a NodeName child is added
						}

						try
						{
							setTypeName(nn.getQName());
						}
						catch (XPath20Exception e)
						{
							throw new RuntimeException(e.getMessage());
						}
					}
				}
				break;
			case XPathTreeConstants.JJTNILLABLE :
				//assert m_kindTest == ELEMENT_TEST

				// NODE_TYPE_NILLABLE or WILDCARD_TYPE_NILLABLE
				m_subkindTest = WILDCARD_TYPE_NILLABLE;
				// may becomes a NODE_TYPE_NILLABLE if a NodeName child is added
				break;
			case XPathTreeConstants.JJTELEMENTTEST :
				m_elementTest = (KindTestImpl) n;
				break;
			case XPathTreeConstants.JJTELEMENTTYPE :
			case XPathTreeConstants.JJTATTRIBUTETYPE :
			case XPathTreeConstants.JJTAT :
			case XPathTreeConstants.JJTELEMENTTYPEFORDOCUMENTTEST :
				break;
			default :
				throw new RuntimeException("Invalid JJTree id: " + n.getId());

		}
	}

	/**
	 * Sets the kindtest type from JJT id
	 */
	protected void setKindTestFromJJTID(int id)
	{
		switch (id)
		{
			case XPathTreeConstants.JJTPITEST :
				m_kindTest = KindTest.PROCESSING_INSTRUCTION_TEST;
				break;

			case XPathTreeConstants.JJTCOMMENTTEST :
				m_kindTest = KindTest.COMMENT_TEST;
				break;

			case XPathTreeConstants.JJTTEXTTEST :
				m_kindTest = KindTest.TEXT_TEST;
				break;

			case XPathTreeConstants.JJTANYKINDTEST :
				m_kindTest = KindTest.ANY_KIND_TEST;
				break;
			case XPathTreeConstants.JJTELEMENTTEST :
				m_kindTest = KindTest.ELEMENT_TEST;
				break;
			case XPathTreeConstants.JJTATTRIBUTETEST :
				m_kindTest = KindTest.ATTRIBUTE_TEST;
				break;
			case XPathTreeConstants.JJTDOCUMENTTEST :
				m_kindTest = KindTest.DOCUMENT_TEST;
				break;

			default :
				throw new RuntimeException("Invalid JJTree id: " + id);
		}
	}

	// Debugging

	public String toString()
	{
		return XPathTreeConstants.jjtNodeName[id]
			+ " "
			+ getClass()
			+ " "
			+ getString(true);
	}

}
