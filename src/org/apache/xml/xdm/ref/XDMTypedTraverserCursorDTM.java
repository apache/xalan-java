package org.apache.xml.xdm.ref;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.xdm.XDMCursor;
import org.apache.xml.xdm.Axis;

/**
 * @author keshlam
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class XDMTypedTraverserCursorDTM extends XDMTraverserCursorDTM {

	/**
	 * Constructor for XDMTypedTraverserCursorDTM.
	 * @param dtmManager
	 * @param nodeHandle
	 * @param axis
	 * @param extendedType
	 */
	public XDMTypedTraverserCursorDTM(
		DTMManager dtmManager,
		int nodeHandle,
		int axis,
		int extendedType) {
		super(dtmManager, nodeHandle, axis, extendedType);
	}

	/**
	 * Constructor for XDMTypedTraverserCursorDTM.
	 * @param dtm
	 * @param nodeHandle
	 * @param axis
	 * @param extendedType
	 */
	public XDMTypedTraverserCursorDTM(
		DTM dtm,
		int nodeHandle,
		int axis,
		int extendedType) {
		super(dtm, nodeHandle, axis, extendedType);
	}

	/**
	 * Constructor for XDMTypedTraverserCursorDTM.
	 * @param other
	 * @param axis
	 * @param extendedType
	 */
	public XDMTypedTraverserCursorDTM(
		XDMCursorDTM other,
		int axis,
		int extendedType) {
		super(other, axis, extendedType);
	}

	/** Subclassed to introduce type senstitivity.
	 * 
	 * @see org.apache.xml.xdm.XDMCursor#resetIteration()
	 */
	public void resetIteration() {
		if(null==m_traverser)
			m_traverser=this.m_currentDTM.getAxisTraverser(m_axis);
		if(null==m_cache)
			m_currentHandle=m_traverser.first(m_startHandle,m_extendedType);
		else
			m_currentHandle=m_cache.elementAt(0);
		m_position=0;
		if(m_currentHandle==DTM.NULL)
			m_length=0;

		switch(m_axis)
		{
			case Axis.ANCESTOR:
			case Axis.ANCESTORORSELF:
			case Axis.PRECEDINGSIBLING:
			case Axis.PRECEDING:
				preloadReverseAxis();
		}
	}

	/** Subclassed to introduce type senstitivity.
	 * 
	 * @see org.apache.xml.xdm.XDMCursor#nextNode()
	 */
	public boolean nextNode() {
		if(m_cache!=null && m_position<m_cache.size())
		{
			m_currentHandle=m_cache.elementAt(++m_position);
		}
		else
		{
			// %BUG% Tries repeatedly from last-found.
			// We need a no-more flag.
			int next=m_traverser.next(m_startHandle,m_currentHandle,m_extendedType);
			if(next!=DTM.NULL)
			{
				m_currentHandle=next;
				++m_position;
				if(m_cache!=null)
					m_cache.addElement(next);
			}
			else
			{
				m_length=m_position+1;
				return false;
			}
		}
		return true;
	}
	
}
