/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.xml.xdm.ref;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.DTMAxisTraverser;
import org.apache.xml.xdm.XDMCursor;
import org.apache.xml.xdm.Axis;
import org.apache.xml.utils.IntVector;

/** Generic XDMCursorDTM along a specified axis,
 * leveraging the DTM Traversers. Optimized versions
 * may follow for some axes.
 * 
 * Note that when traversal order doesn't match document
 * order, we are forced into prescan-and-cache mode. That's
 * done automagically.
 * 
 * Note too that in order to support a typed-traversal
 * subclass, we need to accept and store the type here
 * (otherwise, it isn't available when we need it during
 * constructor execution). "Somewhat vexing" -- we may want
 * to clean that up as we move toward the optimized
 * implementation.
 * 
 * @author keshlam
 */
public class XDMTraverserCursorDTM extends XDMCursorDTM {
	/** DTM Traverser which implements this axis */
	protected DTMAxisTraverser m_traverser;
	/** Cached nodes -- if cache is enabled for this cursor.
	 * Note that caching may only be set before first call to
	 * nextNode(), and that it may be set automagically if
	 * we need to remap traversal order to yield results
	 * in document order.
	 * */
	protected IntVector m_cache;
	/** Position within sequence. Zero based! */
	protected int m_position;
	/** Axis of this cursor, as defined in xdm.Axis. */
	protected int m_axis;
	/** Length of sequence. Note that computing this is EXPENSIVE,
	 * though at least it does set the cache.
	 * */
	protected int m_length=-1; // -1 indicates Not Yet Known.
	/** UNUSED in untyped cursors, but we need to record it here
	 * so typed cursors have it available when they override
	 * the initialization and nextNode methods.
	 * */
	protected int m_extendedType;

	/**
	 * Constructor for XDMTraverserCursorDTM.
	 * @param dtmManager
	 * @param nodeHandle
	 * @param axis
	 * @param extendedType UNUSED in untyped cursors; see typed.
	 */
	public XDMTraverserCursorDTM(DTMManager dtmManager, int nodeHandle,
			int axis, int extendedType) {
		super(dtmManager, nodeHandle);
		m_axis=axis;
		m_extendedType=extendedType;
		resetIteration();
	}

	/**
	 * Constructor for XDMTraverserCursorDTM.
	 * @param dtm
	 * @param nodeHandle
	 * @param axis
	 * @param extendedType UNUSED in untyped cursors; see typed.
	 */
	public XDMTraverserCursorDTM(DTM dtm, int nodeHandle,
			int axis, int extendedType) {
		super(dtm, nodeHandle);
		m_axis=axis;
		m_extendedType=extendedType;
		resetIteration();
	}

	/**
	 * Constructor for XDMTraverserCursorDTM.
	 * @param other
	 * @param axis
	 * @param extendedType UNUSED in untyped cursors; see typed.
	 */
	public XDMTraverserCursorDTM(XDMCursorDTM other,
			int axis, int extendedType) {
		super(other);
		m_axis=axis;
		m_extendedType=extendedType;
		resetIteration();
	}
	
	/** (Subclass this to introduce type senstitivity.)
	 * 
	 * @see org.apache.xml.xdm.XDMCursor#resetIteration()
	 */
	public void resetIteration() {
		if(null==m_traverser)
			m_traverser=this.m_currentDTM.getAxisTraverser(m_axis);
		if(null==m_cache)
			m_currentHandle=m_traverser.first(m_startHandle);
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
	
	/** Internal routine: If called on a reverse axis, we want to
	 * put it back into document order. This routine will do so,
	 * and the cache code should then make the rest of the cursor
	 * Do The Right Thing.
	 * */
	protected void preloadReverseAxis()
	{
		if(m_length<0) // If length known, cache is already set.
		{
			// If not empty, we need to reverse the set
			setShouldCacheNodes();
			while(nextNode())
				; // loop does all the work
				
			IntVector reverseCache=new IntVector(m_cache.size());
			for(int i=m_cache.size()-1;
				i>=0;
				--i)
				reverseCache.addElement(m_cache.elementAt(i));
			m_cache=reverseCache;
			m_position=0;
			
			// m_currentHandle and m_length should already
			// be set correctly!
		}
	}


	/**
	 * @see org.apache.xml.xdm.XDMCursor#isEmpty()
	 */
	public boolean isEmpty() {
		return m_length==0;
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#cloneXDMCursor()
	 */
	public XDMCursor cloneXDMCursor() {
		try
		{
			// This handles the scalar values... and we can share
			// the traverser since it's stateless.
			XDMTraverserCursorDTM newcursor=(XDMTraverserCursorDTM)clone();

			// But m_cache has state, hence must be deep-cloned
			// rather than being shared.
			if(m_cache!=null)
				newcursor.m_cache=(IntVector)m_cache.clone();

			return newcursor;
		}
		catch(Exception e)
		{
			// Should never happen!
			throw new InternalError(e.toString());
		}
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getIterationRoot()
	 */
	public XDMCursor getIterationRoot() {
		return new XDMSelfCursorDTM(m_currentDTM,m_startHandle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#setIterationRoot(XDMCursor, Object)
	 */
	public XDMCursor setIterationRoot(XDMCursor root, Object environment) {
		m_length=-1;
		XDMCursorDTM rd=(XDMCursorDTM)root;
		m_currentDTM=rd.m_currentDTM;
		m_startHandle=rd.m_currentHandle;
		resetIteration();
		if(m_cache!=null)
		{
			m_cache=null;
			setShouldCacheNodes();
		}
		return this;		
	}

	/** (Subclass this to introduce type senstitivity.)
	 * 
	 * @see org.apache.xml.xdm.XDMCursor#nextNode()
	 */
	public boolean nextNode() {
		// Position is 0-based, size is 1-based
		if(m_cache!=null && m_position<m_cache.size()-1)
		{
			m_currentHandle=m_cache.elementAt(++m_position);
		}
		else if(m_length>=0)
		{
			return false; // off end of _completed_ cache
		}
		else
		{
			// %BUG% Tries repeatedly from last-found.
			// We need a no-more flag.
			int next=m_traverser.next(m_startHandle,m_currentHandle);
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

	/**
	 * @see org.apache.xml.xdm.XDMCursor#detach()
	 */
	public void detach() {
		// no-op
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#allowDetachToRelease(boolean)
	 */
	public void allowDetachToRelease(boolean allowRelease) {
		// no-op
	}

	/** Note that this has no effect if we aren't at the
	 * initial state.
	 * 
	 * @see org.apache.xml.xdm.XDMCursor#setShouldCacheNodes(boolean)
	 */
	public void setShouldCacheNodes() {
		if(m_position==0 && m_cache==null)
		{
			m_cache=new IntVector();
			m_cache.addElement(m_currentHandle);
		}
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getCurrentPos()
	 */
	public int getCurrentPos() {
		return m_position;
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#setCurrentPos(int)
	 */
	public boolean setCurrentPos(int i) {
		if(i<0)
			return false;

		if(i>m_position)
		{	
			if(m_cache!=null && i<m_cache.size())
			{
				m_position=i;
				m_currentHandle=m_cache.elementAt(i);			
			}
			else do 
			{
				// %OPT% More expensive than I really like. Inline?
				// Note that position changes even if we stop short
				// -- should it?
				if(!nextNode())
					return false;
			} while(i>m_position);
		}
		
		if(i<m_position)
		{
			if(null==m_cache)
				return false; // "I'm sorry, Dave. I can't do that."
			m_position=i;
			m_currentHandle=m_cache.elementAt(i);
		}
		return true;
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#item(int)
	 */
	public XDMCursor item(int index) {
		if(index==m_position)
			return new XDMSelfCursorDTM(m_currentDTM,m_currentHandle);
		else if(null==m_cache)
			return null;
		else if(index>=m_cache.size())
		{			
			// Save our position, check the other position,
			// and restore our position. NOT threadsafe, both
			// because it changes position temporarily and because
			// it (indirectly) calls nextNode().
			//
			// %REVIEW% Sloppy leveraging of existing code.
			// Should we do a more explicit cache pre-read?
			int pos=m_position;
			XDMCursor search=(setCurrentPos(index)) 
				? new XDMSelfCursorDTM(this)
				: new XDMSelfCursorDTM(m_currentDTM,DTM.NULL);
			m_position=pos;
			m_currentHandle=m_cache.elementAt(pos);
			return search;
		}
		else	
			return new XDMSelfCursorDTM(m_currentDTM,m_cache.elementAt(index));			
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getLength()
	 */
	public int getLength() {
		if(m_length==-1) //unknown
		{
			int pos=m_position;
			int current=m_currentHandle;
		
			while(nextNode())
				; // iteration does all the work
		
			m_position=pos;
			m_currentHandle=current;
		}
		return m_length;
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#cloneWithReset()
	 */
	public XDMCursor cloneWithReset() {
		// %OPT% Could speed up slightly by inlining
		XDMCursor newcursor=cloneXDMCursor();
		newcursor.resetIteration();
		return newcursor;		
	}

	/** All axis cursors should be doc-ordered.
	 * Note that the underlying traversal code may not be.
	 * If it isn't, we'll need to pre-cache and adjust
	 * appropriately.
	 * 
	 * @see org.apache.xml.xdm.XDMCursor#isDocOrdered()
	 */
	public boolean isDocOrdered() {
		return true; 
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getAxis()
	 */
	public int getAxis() {
		return m_axis;
	}

}
