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
 * @author Erwin Bolwidt <ejb@klomp.org>
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
import org.apache.xalan.xsltc.runtime.Hashtable;
import org.apache.xalan.xsltc.runtime.BasisLibrary;

public final class MultiDOM implements DOM {

    private static final int NO_TYPE = DOM.FIRST_TYPE - 2;
    private static final int INITIAL_SIZE = 4;
    private static final int CLR = 0x00FFFFFF;
    private static final int SET = 0xFF000000;
    
    private DOM[] _adapters;
    private int _free;
    private int _size;

    private Hashtable _documents = new Hashtable();

    private final class AxisIterator implements NodeIterator {
	private final int _axis;
	private final int _type;

	private int _mask;
	private NodeIterator _source = null;
	
	public AxisIterator(final int axis, final int type) {
	    _axis = axis;
	    _type = type;
	}
	
	public int next() {
	    if (_source == null) return(END);
	    if (_mask == 0) return _source.next();
	    final int node = _source.next();
	    return node != END ? (node | _mask) : END;
	}
	
	public void setRestartable(boolean flag) {
	    _source.setRestartable(flag);
	}

	public NodeIterator setStartNode(final int node) {
	    final int dom = node >>> 24;
	    final int mask = node & SET;

	    // Get a new source first time and when mask changes
	    if (_source == null || _mask != mask) {
		if (_type == NO_TYPE) {
		    _source = _adapters[dom].getAxisIterator(_axis);
		}
		else if (_axis == Axis.CHILD && _type != ELEMENT) {
		    _source = _adapters[dom].getTypedChildren(_type);
		}
		else {
		    _source = _adapters[dom].getTypedAxisIterator(_axis, _type);
		}
	    }

	    _mask = mask;
	    _source.setStartNode(node & CLR);
	    return this;
	}

	public NodeIterator reset() {
	    if (_source != null) _source.reset();
	    return this;
	}
    
	public int getLast() {
	    return _source.getLast();
	}
    
	public int getPosition() {
	    return _source.getPosition();
	}
    
	public boolean isReverse() {
	    return (_source == null) ? false : _source.isReverse();
	}
    
	public void setMark() {
	    _source.setMark();
	}
    
	public void gotoMark() {
	    _source.gotoMark();
	}
    
	public NodeIterator cloneIterator() {
	    final AxisIterator clone = new AxisIterator(_axis, _type);
	    clone._source = _source.cloneIterator();
	    clone._mask = _mask;
	    return clone;
	}

    } // end of AxisIterator


    /**************************************************************
     * This is a specialised iterator for predicates comparing node or
     * attribute values to variable or parameter values.
     */
    private final class NodeValueIterator extends NodeIteratorBase {

	private NodeIterator _source;
	private String _value;
	private boolean _op;
	private final boolean _isReverse;
	private int _returnType = RETURN_PARENT;

	public NodeValueIterator(NodeIterator source, int returnType,
				 String value, boolean op) {
	    _source = source;
	    _returnType = returnType;
	    _value = value;
	    _op = op;
	    _isReverse = source.isReverse();
	}

	public boolean isReverse() {
	    return _isReverse;
	}
    
	public NodeIterator cloneIterator() {
	    try {
		NodeValueIterator clone = (NodeValueIterator)super.clone();
		clone._source = _source.cloneIterator();
		clone.setRestartable(false);
		return clone.reset();
	    }
	    catch (CloneNotSupportedException e) {
		BasisLibrary.runTimeError(BasisLibrary.ITERATOR_CLONE_ERR,
					  e.toString());
		return null;
	    }
	}

	public void setRestartable(boolean isRestartable) {
	    _isRestartable = isRestartable;
	    _source.setRestartable(isRestartable);
	}

	public NodeIterator reset() {
	    _source.reset();
	    return resetPosition();
	}

	public int next() {

	    int node;
	    while ((node = _source.next()) != END) {
		String val = getNodeValue(node);
		if (_value.equals(val) == _op) {
		    if (_returnType == RETURN_CURRENT)
			return returnNode(node);
		    else
			return returnNode(getParent(node));
		}
	    }
	    return END;
	}

	public NodeIterator setStartNode(int node) {
	    if (_isRestartable) {
		_source.setStartNode(_startNode = node); 
		return resetPosition();
	    }
	    return this;
	}

	public void setMark() {
	    _source.setMark();
	}

	public void gotoMark() {
	    _source.gotoMark();
	}
    }                       

    public MultiDOM(DOM main) {
	_size = INITIAL_SIZE;
	_free = 1;
	_adapters = new DOM[INITIAL_SIZE];
	_adapters[0] = main;
    }

    public int nextMask() {
	return(_free << 24);
    }

    public void setupMapping(String[] names, String[] namespaces) {
	// This method only has a function in DOM adapters
    }

    public int addDOMAdapter(DOMAdapter dom) {
	// Add the DOM adapter to the array of DOMs
	final int domNo = _free++;
	if (domNo == _size) {
	    final DOMAdapter[] newArray = new DOMAdapter[_size *= 2];
	    System.arraycopy(_adapters, 0, newArray, 0, domNo);
	    _adapters = newArray;
	}
	_adapters[domNo] = dom;

	// Store reference to document (URI) in hashtable
	String uri = dom.getDocumentURI(0);
	_documents.put(uri, new Integer(domNo));
	
	// Store mask in DOMAdapter
	dom.setMultiDOMMask(domNo << 24);
	return (domNo << 24);
    }
    
    public int getDocumentMask(String uri) {
	Integer domIdx = (Integer)_documents.get(uri);
	if (domIdx == null)
	    return(-1);
	else
	    return((domIdx.intValue() << 24));
    }

    /** 
      * Returns singleton iterator containg the document root 
      */
    public NodeIterator getIterator() {
	// main source document @ 0
	return _adapters[0].getIterator();
    }
    
    public String getStringValue() {
	return _adapters[0].getStringValue();
    }

    public String getTreeString() {
	return _adapters[0].getTreeString();
    }
    
    public NodeIterator getChildren(final int node) {
	return (node & SET) == 0
	    ? _adapters[0].getChildren(node)
	    : getAxisIterator(Axis.CHILD).setStartNode(node);
    }
    
    public NodeIterator getTypedChildren(final int type) {
	return new AxisIterator(Axis.CHILD, type);
    }
    
    public NodeIterator getAxisIterator(final int axis) {
	return new AxisIterator(axis, NO_TYPE);
    }
    
    public NodeIterator getTypedAxisIterator(final int axis, final int type) {
	return new AxisIterator(axis, type);
    }

    public NodeIterator getNthDescendant(int node, int n, boolean includeself) {
	return _adapters[node>>>24].getNthDescendant(node & CLR,n,includeself);
    }

    public NodeIterator getNodeValueIterator(NodeIterator iterator, int type,
					     String value, boolean op) {
	return(new NodeValueIterator(iterator, type, value, op));
    }

    public NodeIterator getNamespaceAxisIterator(final int axis, final int ns) {
	NodeIterator iterator = _adapters[0].getNamespaceAxisIterator(axis,ns);
	return(iterator);	
    }

    public NodeIterator orderNodes(NodeIterator source, int node) {
	return _adapters[node>>>24].orderNodes(source, node & CLR);
    }

    public int getType(final int node) {
	return _adapters[node>>>24].getType(node & CLR);
    }

    public int getNamespaceType(final int node) {
	return _adapters[node>>>24].getNamespaceType(node & CLR);
    }
    
    public int getParent(final int node) {
	return _adapters[node>>>24].getParent(node & CLR) | node&SET;
    }
    
    public int getTypedPosition(int type, int node) {
	return _adapters[node>>>24].getTypedPosition(type, node&CLR);
    }

    public int getTypedLast(int type, int node) {
	return _adapters[node>>>24].getTypedLast(type, node&CLR);
    }

    public int getAttributeNode(final int type, final int el) {
	return _adapters[el>>>24].getAttributeNode(type, el&CLR) | el&SET;
    }
    
    public String getNodeName(final int node) {
	return _adapters[node>>>24].getNodeName(node & CLR);
    }

    public String getNamespaceName(final int node) {
	return _adapters[node>>>24].getNamespaceName(node & CLR);
    }
    
    public String getNodeValue(final int node) {
	return _adapters[node>>>24].getNodeValue(node & CLR);
    }
    
    public void copy(final int node, TransletOutputHandler handler)
	throws TransletException {
	_adapters[node>>>24].copy(node & CLR, handler);
    }
    
    public void copy(NodeIterator nodes, TransletOutputHandler handler)
	throws TransletException {
	int node;
	while ((node = nodes.next()) != DOM.NULL) {
	    _adapters[node>>>24].copy(node & CLR, handler);
	}
    }


    public String shallowCopy(final int node, TransletOutputHandler handler)
	throws TransletException {
	return _adapters[node>>>24].shallowCopy(node & CLR, handler);
    }
    
    public boolean lessThan(final int node1, final int node2) {
	final int dom1 = node1>>>24;
	final int dom2 = node2>>>24;
	return dom1 == dom2
	    ? _adapters[dom1].lessThan(node1 & CLR, node2 & CLR)
	    : dom1 < dom2;
    }
    
    public void characters(final int textNode, TransletOutputHandler handler)
	throws TransletException {
	    _adapters[textNode>>>24].characters(textNode & CLR, handler);
    }

    public void setFilter(StripFilter filter) {
	for (int dom=0; dom<_free; dom++) {
	    _adapters[dom].setFilter(filter);
	}
    }

    public Node makeNode(int index) {
	return _adapters[index>>>24].makeNode(index & CLR);
    }

    public Node makeNode(NodeIterator iter) {
	// TODO: gather nodes from all DOMs ?
	return _adapters[0].makeNode(iter);
    }

    public NodeList makeNodeList(int index) {
	return _adapters[index>>>24].makeNodeList(index & CLR);
    }

    public NodeList makeNodeList(NodeIterator iter) {
	// TODO: gather nodes from all DOMs ?
	return _adapters[0].makeNodeList(iter);
    }

    public String getLanguage(int node) {
	return _adapters[node>>>24].getLanguage(node & CLR);
    }

    public int getSize() {
	int size = 0;
	for (int i=0; i<_size; i++)
	    size += _adapters[i].getSize();
	return(size);
    }

    public String getDocumentURI(int node) {
	return _adapters[node>>>24].getDocumentURI(0);
    }

    public boolean isElement(final int node) {
	return(_adapters[node>>>24].isElement(node & CLR));
    }

    public boolean isAttribute(final int node) {
	return(_adapters[node>>>24].isAttribute(node & CLR));
    }

    public String lookupNamespace(int node, String prefix) 
	throws TransletException
    {
	return _adapters[node>>>24].lookupNamespace(node, prefix);
    }
}
