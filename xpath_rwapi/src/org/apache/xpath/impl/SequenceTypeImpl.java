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
import org.apache.xpath.datamodel.SequenceType;
import org.apache.xpath.impl.parser.Node;
import org.apache.xpath.impl.parser.SimpleNode;
import org.apache.xpath.impl.parser.Token;
import org.apache.xpath.impl.parser.XPath;
import org.apache.xpath.impl.parser.XPathTreeConstants;

/**
 * JavaCC-based {@link SequenceType} implementation.
 * @author <a href="mailto:villard@us.ibm.com">Lionel Villard</a>
 * @version $Id$
 */
public class SequenceTypeImpl extends KindTestImpl implements SequenceType
{
	short m_occInd;

	// Constructors

	public SequenceTypeImpl(int i)
	{
		super(i);

		m_occInd = ONE;
	}

	public SequenceTypeImpl(XPath p, int i)
	{
		super(p, i);

		m_occInd = ONE;
	}
	
	/**
	 * Creates an atomic type. 
	 */
	protected SequenceTypeImpl(QName typeName)
	{
		super(XPathTreeConstants.JJTQNAMEFORSEQUENCETYPE);
		
		m_occInd = ONE;
		try
		{
			setTypeName(typeName);
		}
		catch (XPath20Exception e)
		{
			throw new RuntimeException();
		}	
	}

	// Methods

	protected void setKindTestFromJJTID(int id)
	{
		switch (id)
		{
			case XPathTreeConstants.JJTEMPTY :
				m_kindTest = EMPTY_SEQ;
				break;

			case XPathTreeConstants.JJTITEM :
				m_kindTest = ITEM_TYPE;
				break;
			case XPathTreeConstants.JJTQNAMEFORSEQUENCETYPE :
				m_kindTest = ATOMIC_TYPE;
				break;

			default :
				super.setKindTestFromJJTID(id);
		}
	}

	// Implements SequenceType

	public short getItemType()
	{
		return super.getKindTestType();
	}

	public short getOccurrenceIndicator()
	{
		return m_occInd;
	}

	public void setOccurrenceIndicator(short occ) throws XPath20Exception
	{
		// TODO :check
		m_occInd = occ;
	}

	// Methods

	public void getString(StringBuffer expr, boolean abbreviate)
	{
		switch (m_kindTest)
		{
			case EMPTY_SEQ :
				expr.append("empty()");
				break;

			case ITEM_TYPE :
				expr.append("item()");
				break;

			case ATOMIC_TYPE :
				try
				{
					expr.append(getTypeName().toString());
				}
				catch (XPath20Exception e)
				{
					throw new RuntimeException();
				}

				break;
			default :
				super.getString(expr, abbreviate);
		}

		if (m_kindTest != EMPTY_SEQ)
		{
			switch (m_occInd)
			{
				case ZERO_OR_MORE :
					expr.append("*");

					break;

				case ONE_OR_MORE :
					expr.append("+");

					break;

				case ZERO_OR_ONE :
					expr.append("?");

					break;

				default : // ONE
					// nothing
			}
		}
	}

	// Parser

	public void jjtAddChild(Node n, int i)
	{
		super.jjtAddChild(n, i);
	}

	public void processToken(Token t)
	{
		super.processToken(t);

		switch (id)
		{

			case XPathTreeConstants.JJTQNAMEFORSEQUENCETYPE :
				String qname = t.image;
				int parenIndex = qname.lastIndexOf("(");
				if (parenIndex > 0)
				{
					qname = qname.substring(0, parenIndex);
				}
				qname = qname.trim();
				int colonIdx = qname.indexOf(":");
				if (colonIdx == -1)
				{
					m_typeName =
						SimpleNode.getExpressionFactory().createQName(
							null,
							qname,
							null);
				}
				else
				{
					m_typeName =
						SimpleNode.getExpressionFactory().createQName(
							null,
							qname.substring(colonIdx + 1),
							qname.substring(0, colonIdx));
				}
				break;

			default :
			
		}
	}

}
