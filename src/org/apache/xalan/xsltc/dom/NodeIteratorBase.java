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
import org.apache.xalan.xsltc.runtime.BasisLibrary;

public abstract class NodeIteratorBase implements NodeIterator {

    /**
     * Cached computed value of last().
     */
    protected int _last = -1;

    /**
     * Value of position() in this iterator. Incremented in
     * returnNode().
     */
    protected int _position = 0;

    /**
     * Store node in call to setMark().
     */
    protected int _markedNode;

    /**
     * Store node in call to setStartNode().
     */
    protected int _startNode = NodeIterator.END;

    /** 
     * Flag indicating if "self" should be returned.
     */
    protected boolean _includeSelf = false;

    /**
     * Flag indicating if iterator can be restarted.
     */
    protected boolean _isRestartable = true;

    /**
     * Setter for _isRestartable flag. 
     */
    public void setRestartable(boolean isRestartable) {
	_isRestartable = isRestartable;
    }

    /**
     * Initialize iterator using a node. If iterator is not
     * restartable, then do nothing. If node is equal to END then
     * subsequent calls to next() must return END.
     */
    abstract public NodeIterator setStartNode(int node);

    /**
     * Reset this iterator using state from last call to
     * setStartNode().
     */
    public NodeIterator reset() {
	final boolean temp = _isRestartable;
	_isRestartable = true;
	// Must adjust _startNode if self is included
	setStartNode(_includeSelf ? _startNode + 1 : _startNode);
	_isRestartable = temp;
	return this;
    }

    /**
     * Setter for _includeSelf flag.
     */
    public NodeIterator includeSelf() {
	_includeSelf = true;
	return this;
    }

    /**
     * Default implementation of getLast(). Stores current position
     * and current node, resets the iterator, counts all nodes and
     * restores iterator to original state.
     */
    public int getLast() {
	if (_last == -1) {
	    final int temp = _position;
	    setMark();
	    reset();
	    do {
		_last++;
	    } while (next() != END);
	    gotoMark();
	    _position = temp;
	}
	return _last;
    }

    /**
     * Returns the position() in this iterator.
     */
    public int getPosition() {
	return _position == 0 ? 1 : _position;
    }

    /**
     * Indicates if position in this iterator is computed in reverse
     * document order. Note that nodes are always returned in document
     * order.
     */
    public boolean isReverse() {
	return false;
    }
    
    /**
     * Clones and resets this iterator. Note that the cloned iterator is 
     * not restartable. This is because cloning is needed for variable 
     * references, and the context node of the original variable 
     * declaration must be preserved.
     */
    public NodeIterator cloneIterator() {
	try {
	    final NodeIteratorBase clone = (NodeIteratorBase)super.clone();
	    clone._isRestartable = false;
	    return clone.reset();
	}
	catch (CloneNotSupportedException e) {
	    BasisLibrary.runTimeError(BasisLibrary.ITERATOR_CLONE_ERR,
				      e.toString());
	    return null;
	}
    }
    
    /**
     * Utility method that increments position and returns its
     * argument.
     */
    protected final int returnNode(final int node) {
	_position++;
	return node;
    }
    
    /**
     * Reset the position in this iterator.
     */
    protected final NodeIterator resetPosition() {
	_position = 0;
	return this;
    }
}
