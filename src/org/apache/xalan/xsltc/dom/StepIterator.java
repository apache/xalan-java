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
 * @author Erwin Bolwidt <ejb@klomp.org>
 * @author Morten Jorgensen
 *
 */

package org.apache.xalan.xsltc.dom;

import org.apache.xalan.xsltc.NodeIterator;
import org.apache.xalan.xsltc.runtime.BasisLibrary;

public class StepIterator extends NodeIteratorBase {

    protected NodeIterator _source;
    protected NodeIterator _iterator;
    private int _pos = -1;

    public StepIterator(NodeIterator source, NodeIterator iterator) {
	_source = source;
	_iterator = iterator;
    }

    public void setRestartable(boolean isRestartable) {
	_isRestartable = isRestartable;
	_source.setRestartable(isRestartable);
	_iterator.setRestartable(true); // must _always_ be restartable
    }

    public NodeIterator cloneIterator() {
	try {
	    final StepIterator clone = (StepIterator)super.clone();
	    clone._source = _source.cloneIterator();
	    clone._iterator = _iterator.cloneIterator();
	    clone.setRestartable(false);
	    return clone.reset();
	}
	catch (CloneNotSupportedException e) {
	    BasisLibrary.runTimeError(BasisLibrary.ITERATOR_CLONE_ERR,
				      e.toString());
	    return null;
	}
    }
    
    public NodeIterator setStartNode(int node) {
	if (_isRestartable) {
	    // Set start node for left-hand iterator...
	    _source.setStartNode(_startNode = node);

	    // ... and get start node for right-hand iterator from left-hand,
	    // with special case for //* path - see ParentLocationPath
	    _iterator.setStartNode(_includeSelf ? _startNode : _source.next());
	    return resetPosition();
	}
	return this;
    }

    public NodeIterator reset() {
	_source.reset();
	// Special case for //* path - see ParentLocationPath
	_iterator.setStartNode(_includeSelf ? _startNode : _source.next());
	return resetPosition();
    }
    
    public int next() {
	for (int node;;) {
	    // Try to get another node from the right-hand iterator
	    if ((node = _iterator.next()) != END) {
		return returnNode(node);
	    }
	    // If not, get the next starting point from left-hand iterator...
	    else if ((node = _source.next()) == END) {
		return END;
	    }
	    // ...and pass it on to the right-hand iterator
	    else {
		_iterator.setStartNode(node);
	    }
	}
    }

    public void setMark() {
	_source.setMark();
	_iterator.setMark();
	_pos = _position;
    }

    public void gotoMark() {
	_source.gotoMark();
	_iterator.gotoMark();
	_position = _pos;
    }
}
