/*
 * Created on 8 sept. 2003
 */
package org.apache.xpath.impl.parser;

/**
 * Represents a string literal. 
 * Intermediate AST node (not present in final representation).
 * Usually stored as a Java string.
 * @author <a href="mailto:villard@us.ibm.com>Lionel Villard</a>
 * @version $Id$
 */
public class IStringLiteral extends SimpleNode
{
	String m_str;

	// Constructors

	public IStringLiteral(int i)
	{
		super(i);

	}

	// Methods

	public void processToken(Token token)
	{
		super.processToken(token);

		switch (id)
		{
			case XPathTreeConstants.JJTSTRINGLITERALFORKINDTEST :
				m_str = token.image;
				m_str = m_str.substring(1);
				m_str = m_str.substring(0, m_str.length() - 1);
				break;

			case XPathTreeConstants.JJTNCNAMEFORPI:
				m_str = token.image;
				break;
			default :
				throw new RuntimeException("Invalid JJTree id:" + id);

		}
	}

	/**
	 * @return
	 */
	public String getStr()
	{
		return m_str;
	}
}
