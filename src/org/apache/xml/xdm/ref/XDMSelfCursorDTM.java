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

import org.apache.xml.xdm.XDMCursor;
import org.apache.xml.xdm.Axis;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.DTM;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.res.XSLMessages;

/** Single-node XDMCursorDTM.
 * 
 * %OPT% This is slightly inefficient because the XDMCursorDTM
 * core currently implements both m_startHandle and m_currentHandle,
 * whereas we really need only a single handle. It's
 * tempting to rework that dependency in order to avoid initializing
 * and cloning the startHandle field. Downside is the maintainability hit.
 * 
 * %OPT% Should we have a separate XDMEmptyCursorDTM static-final
 * instance? Would have to avoid dependencies on m_currentDTM.
 * 
 * @author keshlam
 */
public class XDMSelfCursorDTM 
extends XDMCursorDTM implements Cloneable {
	
	/** Internal constructor */
	public XDMSelfCursorDTM(DTMManager dtmmgr, int handle)
	{
		super(dtmmgr,handle);
		m_currentHandle=handle;
	}

	/** Internal constructor */
	public XDMSelfCursorDTM(DTM dtm, int handle)
	{
		super(dtm,handle);
		m_currentHandle=handle;
	}

	/** Internal constructor.
	 * Very minor efficiency hack when we know that the
	 * other cursor is of the right type. */
	public XDMSelfCursorDTM(XDMCursorDTM other)
	{
		super(other);
		m_currentHandle=m_startHandle;
	}

	/** Internal constructor.
	 * The cast will fail if "other" is not an XDMCursorDTM instance.
	 * That's OK; I think we want it to do so.
	 * */
	public XDMSelfCursorDTM(XDMCursor other)
	{
		super((XDMCursorDTM) other);
		m_currentHandle=m_startHandle;
	}

	public boolean isEmpty()
	{
		return DTM.NULL == m_currentHandle;
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#cloneXDMCursor()
	 */
	public XDMCursor cloneXDMCursor() {
		//return new XDMSelfCursorDTM(this);
		return (XDMCursor)clone();
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#nextNode()
	 */
	public boolean nextNode() {
		return false;
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#setShouldCacheNodes(boolean)
	 */
	public void setShouldCacheNodes() {
		// no-op
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#isMutable()
	 */
	public boolean isMutable() {
		return false;
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getCurrentPos()
	 */
	public int getCurrentPos() {
		return 0;
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#setCurrentPos(int)
	 */
	public boolean setCurrentPos(int i) {
		return (i==0 && DTM.NULL!=m_currentHandle) ? true : false;
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#item(int)
	 */
	public XDMCursor item(int index) {
		return (index==0 && DTM.NULL!=m_currentHandle) ? this : null;
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getLength()
	 */
	public int getLength() {
		return (DTM.NULL!=m_currentHandle) ? 1 : 0;
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#cloneWithReset()
	 */
	public XDMCursor cloneWithReset() {
		// see cloneXDMCursor for issues, but since this one resets:
		return new XDMSelfCursorDTM(m_currentDTM,m_currentHandle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#isDocOrdered()
	 */
	public boolean isDocOrdered() {
		return true;
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#getAxis()
	 */
	public int getAxis() {
		return Axis.SELF;
	}
	
	/** %REVIEW% Alternatives to throwing cast exception would be
	 * returning null, or calling the root's factory method to
	 * make a *new* iterator in its own document space. But
	 * I think crossing docs should be considered an error
	 * at this level.
	 * 
	 * @throws cast exception if the root isn't an XDMCursorDTM.
	 * @see org.apache.xml.xdm.XDMCursor#setIterationRoot()
	 */
	public XDMCursor setIterationRoot(XDMCursor root, Object environment)
	{
		XDMCursorDTM r=(XDMCursorDTM)root;
		this.m_currentDTM = r.m_currentDTM;
		this.m_startHandle=this.m_currentHandle = r.m_currentHandle;
		return this;
	}
	
	/**
	 * @see org.apache.xml.xdm.XDMCursor#getIterationRoot()
	 */
	public XDMCursor getIterationRoot()
	{
		return cloneWithReset();
	}

	/**
	 * @see org.apache.xml.xdm.XDMCursor#resetIteration()
	 */
	public void resetIteration()
	{
		// no-op
	}

	/** Release any resources used by this cursor, and
	 * -- possibly -- return it to a reuse pool.
	 * 
	 * We used pooling in LocPathIterators. And XRTreeFrag,
	 * which implemented DTMIterator, used this as a hint 
	 * that it was now time to discard the Temporary/ResultFragment
	 * DTM. It appears to have been a no-op for other iterators.
	 * 
	 * @see org.apache.xml.xdm.XDMCursor#detachIteration()
	 * @see org.apache.xml.dtm.DTMIterator#detach()
	 */
	public void detach()
	{ // no-op
	}

	/** %REVIEW% Are we pooling these?
	 * 
	 * @see org.apache.xml.xdm.XDMCursor#allowDetachToRelease(boolean)
	 */
	public void allowDetachToRelease(boolean allowRelease)
	{ // no-op
	}
  }
