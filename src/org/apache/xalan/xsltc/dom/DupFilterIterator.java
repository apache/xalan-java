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
 * @author G. Todd Miller 
 *
 */

package org.apache.xalan.xsltc.dom;

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.NodeIterator;
import org.apache.xalan.xsltc.runtime.BasisLibrary;
import org.apache.xalan.xsltc.util.IntegerArray;

/**
 * Removes duplicates and sorts a source iterator. The nodes from the 
 * source are collected in an array upon calling setStartNode(). This
 * array is later sorted and duplicates are ignored in next().
 */
public final class DupFilterIterator extends NodeIteratorBase {

    /**
     * Reference to source iterator.
     */
    private NodeIterator _source;

    /**
     * Array to cache all nodes from source.
     */
    private IntegerArray _nodes = new IntegerArray();

    /**
     * Index in _nodes array to current node.
     */
    private int _current = 0;

    /**
     * Cardinality of _nodes array.
     */
    private int _nodesSize = 0; 

    /**
     * Last value returned by next().
     */
    private int _lastNext = END;

    public DupFilterIterator(NodeIterator source) {
	_source = source;
// System.out.println("DFI source = " + source + " this = " + this);

	// Cache contents of id() or key() index right away. Necessary for
	// union expressions containing multiple calls to the same index, and
	// correct as well since start-node is irrelevant for id()/key() exrp.
	if (source instanceof KeyIndex) {
	    setStartNode(DOM.ROOTNODE);
	}
    }

    public NodeIterator setStartNode(int node) {
	if (_isRestartable) {
	    // KeyIndex iterators are always relative to the root node, so there
	    // is never any point in re-reading the iterator (and we SHOULD NOT).
	    if (_source instanceof KeyIndex && _startNode == DOM.ROOTNODE) {
		return this;
	    }

	    if (node != _startNode) {
		_source.setStartNode(_startNode = node);

		_nodes.clear();
		while ((node = _source.next()) != END) {
		    _nodes.add(node);
		}
		_nodes.sort();
		_nodesSize = _nodes.cardinality();
		_current = 0;
		_lastNext = END;
		resetPosition();
	    }
	}
	return this;
    }


    public int next() {
	while (_current < _nodesSize) {
	    final int next = _nodes.at(_current++);
	    if (next != _lastNext) {
		return returnNode(_lastNext = next);
	    }
	}
	return END;
    }

    public NodeIterator cloneIterator() {
	try {
	    final DupFilterIterator clone =
		(DupFilterIterator) super.clone();
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

    public void setRestartable(boolean isRestartable) {
	_isRestartable = isRestartable;
	_source.setRestartable(isRestartable);
    }

    public void setMark() {
	_markedNode = _current;
    }

    public void gotoMark() {
	_current = _markedNode;
    }

    public NodeIterator reset() {
	_current = 0;
	_lastNext = END;
	return resetPosition();
    }
}
