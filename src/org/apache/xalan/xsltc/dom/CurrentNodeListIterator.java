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
 * @author Morten Jorgensen
 *
 */

package org.apache.xalan.xsltc.dom;

import org.apache.xalan.xsltc.NodeIterator;
import org.apache.xalan.xsltc.runtime.AbstractTranslet;
import org.apache.xalan.xsltc.util.IntegerArray;
import org.apache.xalan.xsltc.runtime.BasisLibrary;

/**
 * Iterators of this kind use a CurrentNodeListFilter to filter a subset of 
 * nodes from a source iterator. For each node from the source, the boolean 
 * method CurrentNodeListFilter.test() is called. 
 *
 * All nodes from the source are read into an array upon calling setStartNode() 
 * (this is needed to determine the value of last, a parameter to 
 * CurrentNodeListFilter.test()). The method getLast() returns the last element 
 * after applying the filter.
 */
public final class CurrentNodeListIterator extends NodeIteratorBase {

    /**
     * A flag indicating if nodes are returned in document order.
     */
    private boolean _docOrder;

    /**
     * The source for this iterator.
     */
    private NodeIterator _source;

    /**
     * A reference to a filter object.
     */
    private final CurrentNodeListFilter _filter;

    /**
     * An integer array to store nodes from source iterator.
     */
    private IntegerArray _nodes = new IntegerArray();
	
    /**
     * Index in _nodes of the next node to filter.
     */
    private int _currentIndex;
	
    /**
     * The current node in the stylesheet at the time of evaluation.
     */
    private final int _currentNode;

    /**
     * A reference to the translet.
     */
    private AbstractTranslet _translet;

    public CurrentNodeListIterator(NodeIterator source, 
				   CurrentNodeListFilter filter,
				   int currentNode,
				   AbstractTranslet translet) 
    {
	this(source, !source.isReverse(), filter, currentNode, translet);
    }

    public CurrentNodeListIterator(NodeIterator source, boolean docOrder,
				   CurrentNodeListFilter filter,
				   int currentNode,
				   AbstractTranslet translet) 
    {
	_source = source;
	_filter = filter;
	_translet = translet;
	_docOrder = docOrder;
	_currentNode = currentNode;
    }

    public void setRestartable(boolean isRestartable) {
	_isRestartable = isRestartable;
	_source.setRestartable(isRestartable);
    }

    public boolean isReverse() {
	return !_docOrder;
    }

    public NodeIterator cloneIterator() {
	try {
	    final CurrentNodeListIterator clone =
		(CurrentNodeListIterator) super.clone();
	    clone._nodes = (IntegerArray) _nodes.clone();
	    clone._source = _source.cloneIterator();
	    clone._isRestartable = false;
	    return clone.reset();
	}
	catch (CloneNotSupportedException e) {
	    BasisLibrary.runTimeError(BasisLibrary.ITERATOR_CLONE_ERR,
				      e.toString());
	    return null;
	}
    }
    
    public NodeIterator reset() {
	_currentIndex = 0;
	return resetPosition();
    }

    public int next() {
	final int last = _nodes.cardinality();
	final int currentNode = _currentNode;
	final AbstractTranslet translet = _translet;

	for (int index = _currentIndex; index < last; ) {
	    final int position = _docOrder ? index + 1 : last - index;
	    final int node = _nodes.at(index++); 	// note increment

	    if (_filter.test(node, position, last, currentNode, translet, this)) {
		_currentIndex = index;
		return returnNode(node);
	    }
	}
	return END;
    }

    public NodeIterator setStartNode(int node) {
	if (_isRestartable) {
	    _source.setStartNode(_startNode = node);

	    _nodes.clear();
	    while ((node = _source.next()) != END) {
		_nodes.add(node);
	    }
	    _currentIndex = 0;
	    resetPosition();
	}
	return this;
    }
	
    public int getPosition() {
	if (_last == -1) {
	    _last = computePositionOfLast();
	}
	return _docOrder ? _position : _last - _position + 1;
    }

    public int getLast() {
	if (_last == -1) {
	    _last = computePositionOfLast();
	}
	return _last;
    }

    public void setMark() {
	_markedNode = _currentIndex;
    }

    public void gotoMark() {
	_currentIndex = _markedNode;
    }

    private int computePositionOfLast() {
        final int last = _nodes.cardinality();
        final int currNode = _currentNode;
	final AbstractTranslet translet = _translet;

	int lastPosition = _position;
	for (int index = _currentIndex; index < last; ) {
	    final int position = _docOrder ? index + 1 : last - index;
            int nodeIndex = _nodes.at(index++); 	// note increment

            if (_filter.test(nodeIndex, position, last, currNode, translet, this)) {
                lastPosition++;
            }
        }
	return lastPosition;
    }
}

