/*
 * Created on 8 sept. 2003 
 */
package org.apache.xpath.impl.parser;

import org.apache.xml.QName;

/**
 * Intermediate AST node representing a node name: 
 * <pre>[75]     	NodeName  	   ::=     	QName | "*"</pre> 
 * Intermediate AST node (not present in final representation).
 * @author <a href="mailto:villard@us.ibm.com>Lionel Villard</a>
 * @version $Id$
 */
public class INodeName extends SimpleNode
{

	private QName m_qname;

	// Constructors

	public INodeName(int i)
	{
		super(i);
	}

	// Methods

	final public boolean isWildCard()
	{
		return m_qname == null;
	}

	final public QName getQName()
	{
		return m_qname;
	}

	// Parser

	public void jjtAddChild(Node n, int i)
	{
		switch (n.getId())
		{
			case XPathTreeConstants.JJTANYNAME :
				// nothing
				break;
			case XPathTreeConstants.JJTQNAME :
			case XPathTreeConstants.JJTQNAMEFORITEMTYPE:
				m_qname = ((IQNameWrapper) n).getQName();
				break;
			default :
				throw new RuntimeException("Invalid child node id: " + n.getId());
		}
	}

}
