/*
 * @(#)$Id$
 *
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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
 * originally based on software copyright (c) 2001, Sun
 * Microsystems., http://www.sun.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.xalan.xsltc.dom;

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.StripFilter;
import org.apache.xalan.xsltc.runtime.AbstractTranslet;
import org.apache.xalan.xsltc.runtime.Hashtable;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMWSFilter;

/**
 * A wrapper class that adapts the
 * {@link org.apache.xml.dtm.DTMWSFilter DTMWSFilter} interface to the XSLTC
 * DOM {@link org.apache.xsltc.StripFilter StripFilter} interface.
 */
public class DOMWSFilter implements DTMWSFilter {

    private AbstractTranslet m_translet;
    private StripFilter m_filter;
    
    // The Hashtable for DTM to mapping array
    private Hashtable m_mappings;
    
    // Cache the DTM and mapping that are used last time
    private DTM m_currentDTM;
    private short[] m_currentMapping;

    /**
     * Construct an adapter connecting the <code>DTMWSFilter</code> interface
     * to the <code>StripFilter</code> interface.
     *
     * @param translet A translet that also implements the StripFilter
     * interface.
     *
     * @see org.apache.xml.dtm.DTMWSFilter
     * @see org.apache.xsltc.StripFilter
     */
    public DOMWSFilter(AbstractTranslet translet) {
        m_translet = translet;
        m_mappings = new Hashtable();

        if (translet instanceof StripFilter) {
            m_filter = (StripFilter) translet;
        }
    }

    /**
     * Test whether whitespace-only text nodes are visible in the logical
     * view of <code>DTM</code>. Normally, this function
     * will be called by the implementation of <code>DTM</code>;
     * it is not normally called directly from
     * user code.
     *
     * @param node int handle of the node.
     * @param dtm the DTM that owns this node
     * @return one of <code>NOTSTRIP</code>, <code>STRIP</code> or
     * <code>INHERIT</code>.
     */
    public short getShouldStripSpace(int node, DTM dtm) {
        if (m_filter != null && dtm instanceof DOM) {
            DOM dom = (DOM)dtm;
            int type = 0;

            if (dtm instanceof SAXImpl) {
                SAXImpl saxImpl = (SAXImpl)dtm;
                
                short[] mapping;
                if (dtm == m_currentDTM) {
                    mapping = m_currentMapping;
                }
                else {  
                    mapping = (short[])m_mappings.get(dtm);
                    if (mapping == null) {
                        mapping = saxImpl.getMapping(m_translet.getNamesArray());
                        m_mappings.put(dtm, mapping);
                        m_currentDTM = saxImpl;
                        m_currentMapping = mapping;
                    }
                }
                
                int expType = saxImpl.getExpandedTypeID(node);
                
                // %OPT% The mapping array does not have information about all the
                // exptypes. However it does contain enough information about all names
                // in the translet's namesArray. If the expType does not fall into the
                // range of the mapping array, it means that the expType is not for one
                // of the recognized names. In this case we can just set the type to -1.
                if (expType >= 0 && expType < mapping.length)
                  type = mapping[expType];
                else
                  type = -1;
                
            } 
            else {
                return INHERIT;
            }

            if (m_filter.stripSpace(dom, node, type)) {
                return STRIP;
            } else {
                return NOTSTRIP;
            }
        } else {
            return NOTSTRIP;
        }
    }
}
