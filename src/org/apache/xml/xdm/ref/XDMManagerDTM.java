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

import javax.xml.transform.Source;
import org.apache.xml.xdm.XDMCursor;
import org.apache.xml.xdm.XDMManager;
import org.apache.xml.xdm.XDMWSFilter;

import org.w3c.dom.Node;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMWSFilter;

import org.apache.xml.utils.XMLStringFactory;

/** Prototype implementation of XDMManager. 
 * 
 * Currently this operates as a thin wrapper around the 
 * DTM package. As it evolves, the DTM interfaces will
 * be simplified to make XDM the primary public API, 
 * DTM-specific code here will move down into the DTM
 * package, and the DTM Node Handle code will be eliminated.
 * 
 * @author keshlam
 */
public class XDMManagerDTM extends XDMManager {
	/** Factory for DTMs to be accessed through XDM.
	 * Note that this can't be set until the string manager
	 * has been set... and probably shouldn't be set until
	 * first used.
	 *  */
	DTMManager m_dtmmgr=null;

	/**
	 * Constructor for XDMManagerDTM. Public, unlike that of
	 * the abstract superclass.
	 * 
	 * Note that the string factory will be set after the object
	 * is instantiated.
	 */
	public XDMManagerDTM() {
		super();
		// Can't set m_dtmmgr until we have the string manager.
	}
	
	/**
	 * @see org.apache.xml.xdm.XDMManager#getXDM(Source, boolean, XDMWSFilter, boolean, boolean)
	 */
	public XDMCursor getXDM(
		Source source,
		boolean unique,
		XDMWSFilter whiteSpaceFilter,
		boolean incremental,
		boolean doIndexing) 
	{
		// PROTOTYPE IMPLEMENTATION: Delegate to DTM.
		// We'll be adding other behaviors later.
				    	
    	// Get the manager if necessary
    	if(null == m_dtmmgr)
    		m_dtmmgr=DTMManager.newInstance(m_xsf);
    		
		// Get a DTM instance. Note the adapter classes.
		DTM dtm=m_dtmmgr.getDTM(source,unique,
			new XDMWSFilterAdapter(whiteSpaceFilter),
			incremental,doIndexing);
		int doc_handle=dtm.getDocument();

		return new XDMSelfCursorDTM(m_dtmmgr,doc_handle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMManager#getXDMFromNode(Node)
	 */
	public XDMCursor getXDMCursorFromNode(Node node) {
		if(null != m_dtmmgr)
		{
			int node_handle=m_dtmmgr.getDTMHandleFromNode(node);
			if(node_handle!=DTM.NULL)
			{
				return new XDMSelfCursorDTM(m_dtmmgr,node_handle);				
			}
		}

		// Fallthru: Not one we know about.
		// %REVIEW%: Should we manufacture a mapping?
		return null; 
	}

	/**
	 * @see org.apache.xml.xdm.XDMManager#createDocumentFragment()
	 */
	public XDMCursor createTextFragment(String text) {
		DTM frag=m_dtmmgr.createDocumentFragment();
		int fraghandle=frag.getDocument();
		frag.appendTextChild(text);
		return new XDMSelfCursorDTM(frag,fraghandle);
	}

	/**
	 * @see org.apache.xml.xdm.XDMManager#release(XDMCursor, boolean)
	 */
	public boolean release(XDMCursor xdm, boolean shouldHardDelete) {
		if(xdm instanceof XDMCursorDTM && null!=m_dtmmgr)
		{
			XDMCursorDTM xdmdtm=(XDMCursorDTM)xdm;
			return m_dtmmgr.release(xdmdtm.getCurrentDTM(),shouldHardDelete);
		}
		return false;
	}
	
	/** Glue class: Adapt an XDM whitespace filter for invocation
	 * as a DTM whitespace filter.
	 * 
	 * %REVIEW% TEMPORARY until we can push this farther down...?
	 * */
	class XDMWSFilterAdapter implements org.apache.xml.dtm.DTMWSFilter
	{
		XDMWSFilter xdmfilter;
		
		public XDMWSFilterAdapter(XDMWSFilter xdmfilter)
		{
			this.xdmfilter = xdmfilter;
		}
		
		public short getShouldStripSpace(int elementHandle, DTM dtm)
		{
			// Massively inefficient. Reuse a mutable Self Cursor?
			return xdmfilter.getShouldStripSpace(
				new XDMSelfCursorDTM(dtm,elementHandle));
		}
	}

}
