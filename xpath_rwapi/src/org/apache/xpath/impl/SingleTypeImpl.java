/*
 * Created on Sep 10, 2003
 */
package org.apache.xpath.impl;

import org.apache.xml.QName;
import org.apache.xpath.datamodel.SingleType;
import org.apache.xpath.expression.Visitor;
import org.apache.xpath.impl.parser.Node;
import org.apache.xpath.impl.parser.IQNameWrapper;
import org.apache.xpath.impl.parser.SimpleNode;
import org.apache.xpath.impl.parser.XPathTreeConstants;

/**
 * JavaCC-based {@link SingleType} implementation.
 * @author <a href="mailto:villard@us.ibm.com">Lionel Villard</a>
 * @version $Id$
 */
public class SingleTypeImpl extends ExprImpl implements SingleType
{

	QName m_qname;
	boolean m_emptySeq;

	// Constructors 

	public SingleTypeImpl(int i)
	{
		super(i);

	}

	protected SingleTypeImpl(QName typeName)
	{
		super(XPathTreeConstants.JJTSINGLETYPE);

		m_qname = typeName;
	}

	// Methods

	public void getString(StringBuffer expr, boolean abbreviate)
	{
		expr.append(m_qname.toString());
		if (m_emptySeq)
		{
			expr.append("?");
		}
	}

	protected int initialChildNumber()
	{
		return -1;
	}

	// Implements SingleType

	public boolean allowEmptySequence()
	{
		return m_emptySeq;
	}

	public QName getAtomicType()
	{
		return m_qname;
	}

	// Implements Expr

	public short getExprType()
	{
		return SINGLE_TYPE_EXPR;
	}

	public boolean visit(Visitor visitor)
	{
		return visitor.visitSingleType(this);
	}

	// Parser

	public void jjtAddChild(Node n, int i)
	{
		switch (n.getId())
		{
			case XPathTreeConstants.JJTQNAMEFORSEQUENCETYPE :

				m_qname = ((IQNameWrapper) n).getQName();
				break;
			case XPathTreeConstants.JJTOCCURRENCEZEROORONE :
				m_emptySeq = true;
				break;
			default :
				throw new RuntimeException();
		}
	}

	public void jjtClose()
	{
		SimpleNode.inSingleType = false;
	}

	public void jjtOpen()
	{
		SimpleNode.inSingleType = true;
	}

}
