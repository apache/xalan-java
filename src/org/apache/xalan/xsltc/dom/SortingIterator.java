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
import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.runtime.BasisLibrary;

public final class SortingIterator extends NodeIteratorBase {
    private final static int INIT_DATA_SIZE = 16;
    private NodeIterator _source;
    private NodeSortRecordFactory _factory;
    private NodeSortRecord[] _data;
    private int _free = 0;
    private int _current;	// index in _nodes of the next node to try

    public SortingIterator(NodeIterator source, 
			   NodeSortRecordFactory factory) {
	_source = source;
	_factory = factory;
    }

    public int next() {
	return _current < _free ? _data[_current++].getNode() : END;
    }
	
    public NodeIterator setStartNode(int node) {
	try {
	    _source.setStartNode(_startNode = node);
	    _data = new NodeSortRecord[INIT_DATA_SIZE];
	    _free = 0;

	    // gather all nodes from the source iterator
	    while ((node = _source.next()) != END) {
		addRecord(_factory.makeNodeSortRecord(node,_free));
	    }
	    // now sort the records
	    quicksort(0, _free - 1);

	    _current = 0;
	    return this;
	}
	catch (Exception e) {
	    return this;
	}
    }
	
    public int getPosition() {
	return _current == 0 ? 1 : _current;
    }

    public int getLast() {
	return _free;
    }

    public void setMark() {
	_source.setMark();
	_markedNode = _current;
    }

    public void gotoMark() {
	_source.gotoMark();
	_current = _markedNode;
    }
    
    /**
     * Clone a <code>SortingIterator</code> by cloning its source
     * iterator and then sharing the factory and the array of
     * <code>NodeSortRecords</code>.
     */
    public NodeIterator cloneIterator() {
	try {
	    final SortingIterator clone = (SortingIterator) super.clone();
	    clone._source = _source.cloneIterator();  
	    clone._factory = _factory;		// shared between clones
	    clone._data = _data;		// shared between clones
	    clone._free = _free;
	    clone._current = _current;
	    clone.setRestartable(false);
	    return clone.reset();
	}
	catch (CloneNotSupportedException e) {
	    BasisLibrary.runTimeError(BasisLibrary.ITERATOR_CLONE_ERR,
				      e.toString());
	    return null;
	}
    }

    private void addRecord(NodeSortRecord record) {
	if (_free == _data.length) {
	    NodeSortRecord[] newArray = new NodeSortRecord[_data.length * 2];
	    System.arraycopy(_data, 0, newArray, 0, _free);
	    _data = newArray;
	}
	_data[_free++] = record;
    }

    private void quicksort(int p, int r) {
	while (p < r) {
	    final int q = partition(p, r);
	    quicksort(p, q);
	    p = q + 1;
	}
    }
    
    private int partition(int p, int r) {
	final NodeSortRecord x = _data[(p + r) >>> 1];
	int i = p - 1;
	int j = r + 1;
	while (true) {
	    while (x.compareTo(_data[--j]) < 0);
	    while (x.compareTo(_data[++i]) > 0);
	    if (i < j) {
		final NodeSortRecord t = _data[i];
		_data[i] = _data[j];
		_data[j] = t;
	    }
	    else {
		return(j);
	    }
	}
    }
}
