/*
 * Created on 10 sept. 2003
 */
package org.apache.xpath.impl.parser;

import org.apache.xpath.XPath20Exception;
import org.apache.xpath.datamodel.SequenceType;
import org.apache.xpath.impl.SequenceTypeImpl;

/**
 * Intermediate AST node representing a sequence type
 * @author <a href="mailto:villard@us.ibm.com>Lionel Villard</a>
 * @version $Id$
 */
public class ISequenceType extends SimpleNode
{

	// Static

	private static ISequenceType m_instance = new ISequenceType();

	public static ISequenceType getSingleton()
	{
		m_instance.reset();
		return m_instance;
	}

	// State

	short m_occInd;
	SequenceTypeImpl m_realSeqType;

	// Constructor	

	private ISequenceType()
	{
		super(XPathTreeConstants.JJTSEQUENCETYPE);
	}

	// Methods

	private void reset()
	{
		m_realSeqType = null;
		m_occInd = -2;
	}

	private void updateOccurence()
	{
		if (m_realSeqType != null)
		{
			try
			{
				m_realSeqType.setOccurrenceIndicator(m_occInd);
			}
			catch (XPath20Exception e)
			{
				throw new RuntimeException();
			}
		}
	}
	public void jjtAddChild(Node n, int i)
	{
		switch (n.getId())
		{
			// [Aug22Draft]
			case XPathTreeConstants.JJTOCCURRENCEZEROORMORE :
				m_occInd = SequenceType.ZERO_OR_MORE;
				updateOccurence();
				break;

				// [Aug22Draft]
			case XPathTreeConstants.JJTOCCURRENCEZEROORONE :
				m_occInd = SequenceType.ZERO_OR_ONE;
				updateOccurence();
				break;

				// [Aug22Draft]
			case XPathTreeConstants.JJTOCCURRENCEONEORMORE :
				m_occInd = SequenceType.ONE_OR_MORE;
				updateOccurence();
				break;			
			default :
				m_realSeqType = (SequenceTypeImpl) n;
				super.jjtAddChild(n, 0);
				updateOccurence();

		}
	}

	public boolean canBeReduced()
	{
		return true;
	}

	protected int initialChildNumber()
	{
		return 1;
	}

	public void jjtOpen()
	{
		SimpleNode.inSequenceType = true;
	}

	public void jjtClose()
	{
		SimpleNode.inSequenceType = false;
	}

	
}
