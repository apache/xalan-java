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

import org.apache.xml.dtm.ref.DTMAxisIteratorBase;
import org.apache.xml.dtm.DTMAxisIterator;

public final class CurrentNodeListIterator extends DTMAxisIteratorBase {
    private DTMAxisIterator _source;
    private boolean _docOrder;
    private final CurrentNodeListFilter _filter;
    private IntegerArray _nodes = new IntegerArray();
	
    private int _current;	// index in _nodes of the next node to try
	
    private AbstractTranslet _translet;
    private final int _currentNode;
    private int _last;		

    public CurrentNodeListIterator(DTMAxisIterator source, 
				   CurrentNodeListFilter filter,
				   int currentNode,
				   AbstractTranslet translet) {
	this(source, !source.isReverse(), filter, currentNode, translet);
    }

    public CurrentNodeListIterator(DTMAxisIterator source, boolean docOrder,
				   CurrentNodeListFilter filter,
				   int currentNode,
				   AbstractTranslet translet) {
	_source = source;
	_filter = filter;
	_translet = translet;
	_docOrder = docOrder;
	_currentNode = currentNode;
    }

    public DTMAxisIterator forceNaturalOrder() {
	_docOrder = true;
	return this;
    }

    public void setRestartable(boolean isRestartable) {
	_isRestartable = isRestartable;
	_source.setRestartable(isRestartable);
    }

    public boolean isReverse() {
	return !_docOrder;
    }

    public DTMAxisIterator cloneIterator() {
	try {
	    final CurrentNodeListIterator clone =
		(CurrentNodeListIterator)super.clone();
	    clone._nodes = (IntegerArray)_nodes.clone();
	    clone.setRestartable(false);
	    return clone.reset();
	}
	catch (CloneNotSupportedException e) {
	    BasisLibrary.runTimeError(BasisLibrary.ITERATOR_CLONE_ERR,
				      e.toString());
	    return null;
	}
    }
    
    public DTMAxisIterator reset() {
	_current = 0;
	return resetPosition();
    }

    public int next() {
	final boolean docOrder = _docOrder;
	final int last = _nodes.cardinality();
	final int currentNode = _currentNode;

	for (int index = _current; index < last; ) {
	    final int node = _nodes.at(index++); 	// note increment
	    if (_filter.test(node, index, last, currentNode, _translet, this)) {
		_current = index;
		return returnNode(node);
	    }
	}
	return END;
    }

    private int computePositionOfLast() {
	int lastPosition = 0;
	final boolean docOrder = _docOrder;
        final int last = _nodes.cardinality();
        final int currNode = _currentNode;

	for (int index = _current; index < last; ) {
            int nodeIndex = _nodes.at(index++); 	// note increment
            if (_filter.test(nodeIndex, index, last, currNode, _translet, this)) {
                lastPosition++;
            }
        }
	return lastPosition;
    }

    public DTMAxisIterator setStartNode(int node) {
	DTMAxisIterator retval = this;
	
	if (_isRestartable) {
	    // iterator is not a clone
	    _source.setStartNode(_startNode = node);
	    // including ROOT
	    _nodes.clear();
	    while ((node = _source.next()) != END) {
		_nodes.add(node);
	    }
	    _current = 0;
	    retval = resetPosition();
	}
	// compute position of _last
  	_last = computePositionOfLast();	
	return retval;
    }
	
    public int getLast() {
	return (_last == -1) ? computePositionOfLast() : _last;
    }

    public void setMark() {
	_source.setMark();
	_markedNode = _current;
    }

    public void gotoMark() {
	_source.gotoMark();
	_current = _markedNode;
    }
}
