/*
 * Created on Aug 8, 2003
 */
package org.apache.xpath.expression;

import org.apache.xml.QName;
import org.apache.xpath.XPath20Exception;

/**
 * Represents <em>KindTest</em>.
 * <p>The method {@link #getKindTestType()} returns the kind
 * test type as following:
 * <ul>
 * <li><b>{@link #TEXT_TEST}</b> for text()</li>
 * <li><b>{@link #PROCESSING_INSTRUCTION_TEST}</b> for processing-instruction()</b>. 
 * Use {@link #getPITarget()} to get the target when specified.</li>
 * <li><b>{@link #COMMENT_TEST}</b> for comment()</li>
 * <li><b>{@link #ANY_KIND_TEST}</b> for node()</li>
 * <li><b>{@link #CONTEXT_ITEM_TEST}</b> for '.'</li>
 * <li><b>{@link #ELEMENT_TEST}</b> for element tests. Use {@link #getKindTestSubtype()} to get which one:
 * <ul>
 * <li><b>{@link #WILDCARD}</b> for element(), or element(*), or element(*,*)</b>
 * <li><b>{@link #NODE_TYPE}</b> for element(N, T)</li>
 * <li><b>{@link #NODE_TYPE_NILLABLE}</b> for element(N, T nillable)</li> 
 * <li><b>{@link #NODE_WILDCARD}</b> for element(N, *)</li>
 * <li><b>{@link #WILDCARD_TYPE}</b> for element(*, T)</li>
 * <li><b>{@link #SCHEMA_PATH}</b> for element(P)</li>
 * </ul> 
 * </li>
 * <li><b>{@link #ATTRIBUTE_TEST}</b> for attribute test.  Use {@link #getKindTestSubtype()} to get which one:
 * <ul>
 * <li><b>{@link #WILDCARD}</b> for attribute(), or attribute(*), or attribute(*,*)</b>
 * <li><b>{@link #NODE_TYPE}</b> for attribute(@N, T)</li>
 * <li><b>{@link #NODE}</b> for attribute(@N)</li>
 * <li><b>{@link #NODE_WILDCARD}</b> for attribute(@N, *)</li>
 * <li><b>{@link #WILDCARD_TYPE}</b> for attribute(@*, T)</li>
 * <li><b>{@link #SCHEMA_PATH}</b> for attribute(P)</li>
 * </ul>
 * </ul> 
 * @author <a href="mailto:villard@us.ibm.com">Lionel Villard</a>
 * @version $Id$
 */
public interface KindTest extends NodeTest
{
	
	/**
	 * Processing instruction test (kind test) 
	 */
	static final short PROCESSING_INSTRUCTION_TEST = 0;
	
	/**
	 * Comment test (kind test)
	 */
	static final short COMMENT_TEST = 1;
	
	/**	 
	 * Any kind test (except context item test)
	 */
	static final short ANY_KIND_TEST = 2;
	
	/**
	 * Text test (kind test)
	 */
	static final short TEXT_TEST = 3;
	
	/**
	 * Document test (kind test)
	 */
	static final short DOCUMENT_TEST = 4;
	
	/**
	 * Element test (kind test)
	 */
	static final short ELEMENT_TEST = 5;

	/**
	 * Attribute test (kind test)
	 */
	static final short ATTRIBUTE_TEST = 6;    
	
	/**
	 * The node test is a context item test (belong to the kind test group)
	 */
	static final short CONTEXT_ITEM_TEST = 7;

	// Subtypes

	static final short WILDCARD = 0;
	static final short NODE_TYPE = 1;
	static final short NODE = 6;
	static final short NODE_TYPE_NILLABLE = 2;
	static final short NODE_WILDCARD = 3;
	static final short WILDCARD_TYPE = 4;
	static final short SCHEMA_PATH = 5;
	
	/**
	 * Gets the kind test code.
	 * @return short One of the kind test constant value
	 */
	short getKindTestType(); 

	/**
	 * Gets the subtype kind test code.
	 * @return short One of the subkind test constant value
	 */
	short getKindTestSubtype(); 

	/**
	 * Gets the local name used to match either an element,
	 * an attribute or a document node. 
	 * See class description to see when to use this method.
	 * @return The local name or null
	 * @throws XPath20Exception whenever the kind test type of is not
	 * {@link #ELEMENT_TEST} or {@link #ATTRIBUTE_TEST}
	 * or {@link #DOCUMENT_TEST}.
	 */
	QName getLocalName() throws XPath20Exception;
	
	/**
	 * 
	 * @return
	 * @throws XPath20Exception
	 */
	QName getNodeName() throws XPath20Exception;
	
	/**
	 * Gets type name. See class description to see when to use this method.
	 * @return QName
	 * @throws XPath20Exception
	 */
	QName getTypeName() throws XPath20Exception;
	
	/**
	 * Gets schema context path. See class description to see when to use this method.
	 * @return String (?)
	 * @throws XPath20Exception
	 */
	String getSchemaContext() throws XPath20Exception;

	/**
	 * Gets the PITarget test or null if not specified.
	 * @return
	 * @throws XPath20Exception whenever the kind test is not
	 * {@link #PROCESSING_INSTRUCTION_TEST}.
	 */
	String getPITarget() throws XPath20Exception;
}
