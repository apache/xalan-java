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
 * @author Morten Jorgensen
 *
 */

package org.apache.xalan.xsltc.dom;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.StripFilter;
import org.apache.xalan.xsltc.NodeIterator;
import org.apache.xalan.xsltc.TransletOutputHandler;
import org.apache.xalan.xsltc.TransletException;

public final class DOMAdapter implements DOM {

    private final DOMImpl _domImpl;
    private short[] _mapping;
    private short[] _reverse;
    private short[] _NSmapping;
    private short[] _NSreverse;

    private StripFilter _filter = null;

    private int _multiDOMMask;
    
    public DOMAdapter(DOMImpl dom,
		      String[] namesArray,
		      String[] namespaceArray) 
    {
	_domImpl = dom;
	_mapping = dom.getMapping(namesArray);
	_reverse = dom.getReverseMapping(namesArray);
	_NSmapping = dom.getNamespaceMapping(namespaceArray);
	_NSreverse = dom.getReverseNamespaceMapping(namespaceArray);
    }

    public void setupMapping(String[] names, String[] namespaces) {
	_mapping = _domImpl.getMapping(names);
	_reverse = _domImpl.getReverseMapping(names);
	_NSmapping = _domImpl.getNamespaceMapping(namespaces);
	_NSreverse = _domImpl.getReverseNamespaceMapping(namespaces);
    }

    /** 
      * Returns singleton iterator containg the document root 
      */
    public NodeIterator getIterator() {
	return _domImpl.getIterator();
    }
    
    public String getStringValue() {
	return _domImpl.getStringValue();
    }

    public String getTreeString() {
	return _domImpl.getTreeString();
    }
    
    public int getMultiDOMMask() {
	return _multiDOMMask;
    }

    public void setMultiDOMMask(int mask) {
	_multiDOMMask = mask;
    }

    public NodeIterator getChildren(final int node) {
	NodeIterator iterator = _domImpl.getChildren(node);
	if (_filter == null) {
	    return iterator.setStartNode(node);
	}
	else {
	    iterator = _domImpl.strippingIterator(iterator, _mapping, _filter);
	    return iterator.setStartNode(node);
	}
    }

    public void setFilter(StripFilter filter) {
	_filter = filter;
    }
    
    public NodeIterator getTypedChildren(final int type) {
	NodeIterator iterator = _domImpl.getTypedChildren(_reverse[type]);
	if (_reverse[type] == DOM.TEXT && _filter != null) {
	    return _domImpl.strippingIterator(iterator,_mapping,_filter);
	}
	return iterator;
    }

    public NodeIterator getNamespaceAxisIterator(final int axis, final int ns) {
	return _domImpl.getNamespaceAxisIterator(axis,_NSreverse[ns]);
    }

    public NodeIterator getAxisIterator(final int axis) {
	NodeIterator iterator = _domImpl.getAxisIterator(axis);
	if (_filter != null) {
	    return _domImpl.strippingIterator(iterator, _mapping, _filter);
	}
	return iterator;
    }
    
    public NodeIterator getTypedAxisIterator(final int axis, final int type) {
	NodeIterator iterator;

	if (axis == Axis.NAMESPACE) {
	    iterator = (type == NO_TYPE || type > _NSreverse.length) ?
		_domImpl.getAxisIterator(axis) :
		_domImpl.getTypedAxisIterator(axis,_NSreverse[type]);
	}
	else {
	    iterator = _domImpl.getTypedAxisIterator(axis, _reverse[type]);
	}
	
	if (_reverse[type] == DOM.TEXT && _filter != null) {
	    iterator = _domImpl.strippingIterator(iterator, _mapping, _filter);
	}
	return iterator;
    }

    public NodeIterator getNthDescendant(int type, int n, boolean includeself) {
	return _domImpl.getNthDescendant(_reverse[type], n, includeself);
    }

    public NodeIterator getNodeValueIterator(NodeIterator iterator, int type,
					     String value, boolean op) 
    {
	return _domImpl.getNodeValueIterator(iterator, type, value, op);
    }

    public NodeIterator orderNodes(NodeIterator source, int node) {
	return _domImpl.orderNodes(source, node);
    }
        
    public int getType(final int node) {
	return _mapping[_domImpl.getType(node)];
    }

    public int getNamespaceType(final int node) {
	return _NSmapping[_domImpl.getNamespaceType(node)];
    }
    
    public int getParent(final int node) {
	return _domImpl.getParent(node);
    }

    public int getTypedPosition(int type, int node) {
	return _domImpl.getTypedPosition(_reverse[type], node);
    }

    public int getTypedLast(int type, int node) {
	return _domImpl.getTypedLast(_reverse[type], node);
    }

    public int getAttributeNode(final int type, final int element) {
	return _domImpl.getAttributeNode(_reverse[type], element);
    }
    
    public String getNodeName(final int node) {
	return _domImpl.getNodeName(node);
    }

    public String getNamespaceName(final int node) {
	return _domImpl.getNamespaceName(node);
    }
    
    public String getNodeValue(final int node) {
	return _domImpl.getNodeValue(node);
    }
    
    public void copy(final int node, TransletOutputHandler handler)
	throws TransletException {
	    _domImpl.copy(node, handler);
    }
    
    public void copy(NodeIterator nodes, TransletOutputHandler handler)
	throws TransletException {
	    _domImpl.copy(nodes, handler);
    }

    public String shallowCopy(final int node, TransletOutputHandler handler)
	throws TransletException {
	    return _domImpl.shallowCopy(node, handler);
    }
    
    public boolean lessThan(final int node1, final int node2) {
	return _domImpl.lessThan(node1, node2);
    }
    
    public void characters(final int textNode, TransletOutputHandler handler)
	throws TransletException {
	_domImpl.characters(textNode, handler);
    }

    public Node makeNode(int index) {
	return _domImpl.makeNode(index);
    }

    public Node makeNode(NodeIterator iter) {
	return _domImpl.makeNode(iter);
    }

    public NodeList makeNodeList(int index) {
	return _domImpl.makeNodeList(index);
    }

    public NodeList makeNodeList(NodeIterator iter) {
	return _domImpl.makeNodeList(iter);
    }

    public String getLanguage(int node) {
	return _domImpl.getLanguage(node);
    }

    public int getSize() {
	return _domImpl.getSize();
    }

    public void setDocumentURI(String uri) {
	_domImpl.setDocumentURI(uri);
    }

    public String getDocumentURI() {
	return(_domImpl.getDocumentURI());
    }

    public String getDocumentURI(int node) {
	return(_domImpl.getDocumentURI());
    }

    public boolean isElement(final int node) {
	return(_domImpl.isElement(node));
    }

    public boolean isAttribute(final int node) {
	return(_domImpl.isAttribute(node));
    }

    public String lookupNamespace(int node, String prefix) 
	throws TransletException 
    {
	return _domImpl.lookupNamespace(node, prefix);
    }
}
