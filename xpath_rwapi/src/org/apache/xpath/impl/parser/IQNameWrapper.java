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
package org.apache.xpath.impl.parser;

import org.apache.xml.QName;
import org.apache.xpath.expression.NameTest;
import org.apache.xpath.expression.StaticContext;

/**
 * QNameNode wrappers a 'real' QName object.
 * @author <a href="mailto:villard@us.ibm.com>Lionel Villard</a>
 * @version $Id$
 */
public class IQNameWrapper extends SimpleNode
{

	/**
	 * The wrapped QName
	 */
	QName m_qname;

	public short m_type;
	public String m_ncname;

	/**
	 * Constructor for QName.
	 * @param i
	 */
	public IQNameWrapper(int i)
	{
		super(i);
	}


	public void processToken(Token t)
	{
		super.processToken(t);

		switch (id)
		{
			case XPathTreeConstants.JJTSTAR :
				m_type = NameTest.WILDCARD;
				break;
			case XPathTreeConstants.JJTSTARCOLONNCNAME :
				m_ncname = t.image.trim();
				m_ncname = m_ncname.substring(m_ncname.indexOf(":") + 1);
				m_type = NameTest.WILDCARD_NCNAME;

				break;
			case XPathTreeConstants.JJTNCNAMECOLONSTAR :
				{
					StaticContext staticCtx = SimpleNode.getStaticContext();

					m_ncname = t.image.trim();
					m_ncname = m_ncname.substring(0, m_ncname.indexOf(":"));

					String ns =
						staticCtx.getNamespaces().getNamespaceURI(m_ncname);

					if (ns == null)
					{
						// TODO: report error
						throw new RuntimeException(
							"Namespace prefix undefined: " + m_ncname);
					}

					m_type = NameTest.NCNAME_WILDCARD;
				}
				break;

			case XPathTreeConstants.JJTKEYLPAR :
				m_qname =
					SimpleNode.getExpressionFactory().createQName(
						null,
						"key",
						null);
				m_type = NameTest.QNAME;
				break;

			case XPathTreeConstants.JJTIDLPAR :
				m_qname =
					SimpleNode.getExpressionFactory().createQName(
						null,
						"id",
						null);
				m_type = NameTest.QNAME;
				break;

			case XPathTreeConstants.JJTQNAME :
			case XPathTreeConstants.JJTQNAMELPAR :
			case XPathTreeConstants.JJTQNAMEFORSEQUENCETYPE :
			case XPathTreeConstants.JJTQNAMEFORITEMTYPE :
				{
					StaticContext staticCtx = SimpleNode.getStaticContext();
					String qname = t.image;
					int parenIndex = qname.lastIndexOf("(");
					if (parenIndex > 0)
					{
						qname = qname.substring(0, parenIndex);
					}
					qname = qname.trim();
					m_qname = resolveQName(staticCtx, qname);
					m_type = NameTest.QNAME;
				}
				break;

			default :
				throw new RuntimeException(
					"Invalid jjtree id: doesn't match a QName id=" + id);
		}
	}

	public org.apache.xml.QName getQName()
	{
		return m_qname;
	}

	public static QName resolveQName(StaticContext staticCtx, String image)
	{
		QName qname;
		int colonIdx = image.indexOf(":");
		if (colonIdx == -1)
		{
			qname =
				SimpleNode.getExpressionFactory().createQName(
					null,
					image,
					null);
		}
		else
		{
			String prefix = image.substring(0, colonIdx);
			String ns = staticCtx.getNamespaces().getNamespaceURI(prefix);

			if (ns == null)
			{
				// TODO: report error
				throw new RuntimeException(
					"Namespace prefix undefined: " + prefix);
			}

			qname =
				SimpleNode.getExpressionFactory().createQName(
					ns,
					image.substring(colonIdx + 1),
					prefix);
		}
		return qname;

	}

}
