/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights 
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
package org.apache.xml.dtm.ref;

import org.apache.xml.dtm.*;
import org.apache.xml.utils.IntVector;

import org.w3c.dom.Node;
import org.w3c.dom.DOMException;

import java.util.Vector;

/**
 * <code>DTMAxisNodeList</code> gives us an implementation of the DOM's
 * NodeList interface wrapped around a DTM Iterator. The author
 * considers this something of an abominations, since NodeList was not
 * intended to be a general purpose "list of nodes" API and is
 * generally considered by the DOM WG to have be a mistake... but I'm
 * told that some of the XPath/XSLT folks say they must have this
 * solution.
 *
 * Please note that this is not necessarily equivlaent to a DOM
 * NodeList operating over the same document. In particular:
 * <ul>
 *
 * <li>If there are several Text nodes in logical succession (ie,
 * across CDATASection and EntityReference boundaries), we will return
 * only the first; the caller is responsible for stepping through
 * them.
 * (%REVIEW% Provide a convenience routine here to assist, pending
 * proposed DOM Level 3 getAdjacentText() operation?) </li>
 *
 * <li>Since the whole XPath/XSLT architecture assumes that the source
 * document is not altered while we're working with it, we do not
 * promise to implement the DOM NodeList's "live view" response to
 * document mutation. </li>
 *
 * </ul>
 *
 * <p>State: In progress!!</p>
 * */
public class DTMAxisIterNodeList extends DTMNodeListBase {
    private DTM m_dtm;
    private DTMAxisIterator m_iter;
    private IntVector m_cachedNodes;
    private int m_last = -1;
    //================================================================
    // Methods unique to this class
    private DTMAxisIterNodeList() {
    }

    /**
     * Public constructor: Wrap a DTMNodeList around an existing
     * and preconfigured DTMAxisIterator
     */
    public DTMAxisIterNodeList(DTM dtm, DTMAxisIterator dtmAxisIterator) {
        if (dtmAxisIterator == null) {
            m_last = 0;
        } else {
            m_cachedNodes = new IntVector();
            m_dtm = dtm;
        }
        m_iter = dtmAxisIterator;
    }

    /**
     * Access the wrapped DTMIterator. I'm not sure whether anyone will
     * need this or not, but let's write it and think about it.
     *
     */
    public DTMAxisIterator getDTMAxisIterator() {
        return m_iter;
    }
  

    //================================================================
    // org.w3c.dom.NodeList API follows

    /**
     * Returns the <code>index</code>th item in the collection. If 
     * <code>index</code> is greater than or equal to the number of nodes in 
     * the list, this returns <code>null</code>.
     * @param indexIndex into the collection.
     * @return The node at the <code>index</code>th position in the 
     *   <code>NodeList</code>, or <code>null</code> if that is not a valid 
     *   index.
     */
    public Node item(int index) {
        if (m_iter != null) {
            int node;
            int count = m_cachedNodes.size();

            if (count > index) {
                node = m_cachedNodes.elementAt(index);
                return m_dtm.getNode(node);
            } else if (m_last == -1) {
                while (((node = m_iter.next()) != DTMAxisIterator.END)
                           && count <= index) {
                    m_cachedNodes.addElement(node);
                    count++;
                }
                if (node == DTMAxisIterator.END) {
                    m_last = count;
                } else {
                    return m_dtm.getNode(node);
                }
            }
        }
        return null;
    }

    /**
     * The number of nodes in the list. The range of valid child node indices 
     * is 0 to <code>length-1</code> inclusive. 
     */
    public int getLength() {
        if (m_last == -1) {
            int node;
            while ((node = m_iter.next()) != DTMAxisIterator.END) {
                m_cachedNodes.addElement(node);
            }
            m_last = m_cachedNodes.size();
        }
        return m_last;
    }
}
