/*
 * @(#)$Id$
 *
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 *
 */

package org.apache.xalan.xsltc;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.apache.xml.dtm.*;
import org.apache.xml.dtm.ref.*;
import org.apache.xalan.xsltc.runtime.Hashtable;

public interface DOM {
    public final static int  FIRST_TYPE             = 0;

    public final static int  NO_TYPE                = -1;
    
    // 0 is reserved for NodeIterator.END
    public final static int NULL     = 0;

    // used by some node iterators to know which node to return
    public final static int RETURN_CURRENT = 0;
    public final static int RETURN_PARENT  = 1;
    
    /** returns singleton iterator containg the document root */
    public DTMAxisIterator getIterator();
    public String getStringValue();
    public String getTreeString(); // this one includes PIs and comments...
	
    public DTMAxisIterator getChildren(final int node);
    public DTMAxisIterator getTypedChildren(final int type);
    public DTMAxisIterator getAxisIterator(final int axis);
    public DTMAxisIterator getTypedAxisIterator(final int axis, final int type);
    public DTMAxisIterator getNthDescendant(int node, int n, boolean includeself);
    public DTMAxisIterator getNamespaceAxisIterator(final int axis, final int ns);
    public DTMAxisIterator getNodeValueIterator(DTMAxisIterator iter, int returnType,
					     String value, boolean op);
    public DTMAxisIterator orderNodes(DTMAxisIterator source, int node);
    public String getNodeName(final int node);
    public String getNodeNameX(final int node);
    public String getNamespaceName(final int node);
    public int getType(final int node);
    public int getNamespaceType(final int node);
    public int getParent(final int node);
    public int getAttributeNode(final int gType, final int element);
    public String getNodeValue(final int node);
    public void copy(final int node, TransletOutputHandler handler)
	throws TransletException;
    public void copy(DTMAxisIterator nodes, TransletOutputHandler handler)
	throws TransletException;
    public String shallowCopy(final int node, TransletOutputHandler handler)
	throws TransletException;
    public boolean lessThan(final int node1, final int node2);
    public void characters(final int textNode, TransletOutputHandler handler)
	throws TransletException;
    public Node makeNode(int index);
    public Node makeNode(DTMAxisIterator iter);
    public NodeList makeNodeList(int index);
    public NodeList makeNodeList(DTMIterator iter);
    public String getLanguage(int node);
    public int getSize();
    public String getDocumentURI(int node);
    public void setFilter(StripFilter filter);
    public void setupMapping(String[] names, String[] namespaces);
    public boolean isElement(final int node);
    public boolean isAttribute(final int node);
    public String lookupNamespace(int node, String prefix)
	throws TransletException;
    public int getNodeIdent(final int nodehandle);
    public int getNodeHandle(final int nodeId);
    public DOM getResultTreeFrag(int initialSize);
    public TransletOutputHandler getOutputDomBuilder();
    public int getNSType(int node);
    public int getDocument();
    public String getUnparsedEntityURI(String name);
    public Hashtable getElementsWithIDs();
}
