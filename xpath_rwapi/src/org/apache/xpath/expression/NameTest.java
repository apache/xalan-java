/*
 * Created on Aug 6, 2003
 */
package org.apache.xpath.expression;

import org.apache.xml.QName;
import org.apache.xpath.XPath20Exception;

/**
 * <em>NameTest</em> expression type.
 * Dependings on the value returned by {@link #getNameTestType()},
 * a name test have the following forms:
 * <ul>
 * <li>{@link #WILDCARD}: *</li>
 * <li>{@link #NCNAME_WILDCARD}: ns:*</li>
 * <li>{@link #WILDCARD_NCNAME}: *:ln</li>
 * <li>{@link #QNAME}: (prefix?):localName</li>
 * </ul> 
 * @author <a href="mailto:villard@us.ibm.com">Lionel Villard</a>
 * @version $Id$
 */
public interface NameTest extends NodeTest
{
	final short WILDCARD = 0;
	final short NCNAME_WILDCARD = 1;
	final short WILDCARD_NCNAME = 2;
	final short QNAME = 3;

	/**
	 * Gets the name test type
	 * @return One of the constants defined above
	 */
	short getNameTestType();

	/**
	 * Gets the qualified name of the name test.
	 * @return Qualified name
	 * @throws XPath20Exception when the name test type isn't
	 * {@link #QNAME} 
	 */
	QName getName() throws XPath20Exception;
	
	/**
	 * Gets the namespace or the local part of the name test.
	 * @return NCName
	 * @throws XPath20Exception when the name test type isn't
	 * {@link #NCNAME_WILDCARD} or {@link #WILDCARD_NCNAME}
	 */
	String getNCName() throws XPath20Exception;
}
