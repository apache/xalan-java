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

package org.apache.xalan.xsltc.dom;

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.NodeIterator;
import org.apache.xalan.xsltc.runtime.BasisLibrary;

/**
 * UnionIterator takes a set of NodeIterators and produces
 * a merged NodeSet in document order with duplicates removed
 * The individual iterators are supposed to generate nodes
 * in document order
 */
public final class UnionIterator extends NodeIteratorBase {
    /** wrapper for NodeIterators to support iterator
	comparison on the value of their next() method
    */
    final private DOM _dom;

    private final static class LookAheadIterator {
	public int node, markedNode;
	public final NodeIterator iterator;
		
	public LookAheadIterator(NodeIterator iterator) {
	    this.iterator = iterator;
	}
		
	public int step() {
	    node = iterator.next();
	    return node;
	}

	public void setMark() {
	    markedNode = node;
	    iterator.setMark();
	}

	public void gotoMark() {
	    node = markedNode;
	    iterator.gotoMark();
	}

    } // end of LookAheadIterator

    private static final int InitSize = 8;
  
    private int            _heapSize = 0;
    private int            _size = InitSize;
    private LookAheadIterator[] _heap = new LookAheadIterator[InitSize];
    private int            _free = 0;
  
    // last node returned by this UnionIterator to the caller of next
    // used to prune duplicates
    private int _returnedLast;

    public UnionIterator(DOM dom) {
	_dom = dom;
    }

    public NodeIterator cloneIterator() {
	final LookAheadIterator[] heapCopy = 
	    new LookAheadIterator[_heap.length];
	try {
	    final UnionIterator clone = (UnionIterator)super.clone();
	    System.arraycopy(_heap, 0, heapCopy, 0, _heap.length);
	    clone.setRestartable(false);
	    clone._heap = heapCopy;
	    return clone.reset();
	} 
	catch (CloneNotSupportedException e) {
	    BasisLibrary.runTimeError(BasisLibrary.ITERATOR_CLONE_ERR,
				      e.toString());
	    return null;
	}
    }
    
    public UnionIterator addIterator(NodeIterator iterator) {
	if (_free == _size) {
	    LookAheadIterator[] newArray = new LookAheadIterator[_size *= 2];
	    System.arraycopy(_heap, 0, newArray, 0, _free);
	    _heap = newArray;
	}
	_heapSize++;
	_heap[_free++] = new LookAheadIterator(iterator);
	return this;
    }
  
    public int next() {
	while (_heapSize > 0) {
	    final int smallest = _heap[0].node;
	    if (smallest == END) { // iterator _heap[0] is done
		if (_heapSize > 1) {
		    // Swap first and last (iterator must be restartable)
		    final LookAheadIterator temp = _heap[0];
		    _heap[0] = _heap[--_heapSize];
		    _heap[_heapSize] = temp;
		}
		else {
		    return END;
		}
	    }
	    else if (smallest == _returnedLast) {	// duplicate
		_heap[0].step(); // value consumed
	    }
	    else {
		_heap[0].step(); // value consumed
		heapify(0);
		return returnNode(_returnedLast = smallest);
	    }
	    // fallthrough if not returned above
	    heapify(0);
	}
	return END;
    }
  
    public NodeIterator setStartNode(int node) {
	if (_isRestartable) {
	    _startNode = node;
	    for (int i = 0; i < _free; i++) {
		_heap[i].iterator.setStartNode(node);
		_heap[i].step();	// to get the first node
	    }
	    // build heap
	    for (int i = (_heapSize = _free)/2; i >= 0; i--) {
		heapify(i);
	    }
	    _returnedLast = END;
	    return resetPosition();
	}
	return this;
    }
	
    private void heapify(int i) {
	for (int r, l, smallest;;) {
	    r = (i + 1) << 1; l = r - 1;
	    smallest = l < _heapSize 
		&& _dom.lessThan(_heap[l].node, _heap[i].node) ? l : i;
	    if (r < _heapSize && _dom.lessThan(_heap[r].node,
					       _heap[smallest].node)) {
		smallest = r;
	    }
	    if (smallest != i) {
		final LookAheadIterator temp = _heap[smallest];
		_heap[smallest] = _heap[i];
		_heap[i] = temp;
		i = smallest;
	    }
	    else
		break;
	}
    }

    public void setMark() {
	for (int i = 0; i < _free; i++) {
	    _heap[i].setMark();
	}
    }

    public void gotoMark() {
	for (int i = 0; i < _free; i++) {
	    _heap[i].gotoMark();
	}
    }

    public NodeIterator reset() {
	super.reset();
	for (int i = 0; i < _free; i++) {
	    _heap[i].iterator.reset();
	}
	return resetPosition();
    }

}
