/*
 * @(#)$Id$
 *
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import org.apache.xalan.xsltc.runtime.BasisLibrary;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.dtm.ref.DTMAxisIteratorBase;

/**
 * This is a special kind of iterator that takes a source iterator and a 
 * node N. If initialized with a node M (the parent of N) it computes the 
 * position of N amongst the children of M. This position can be obtained 
 * by calling getPosition().
 * It is an iterator even though next() will never be called. It is used to
 * match patterns with a single predicate like:
 *
 *    BOOK[position() = last()]
 *
 * In this example, the source iterator will return elements of type BOOK, 
 * a call to position() will return the position of N. Notice that because 
 * of the way the pattern matching is implemented, N will always be a node 
 * in the source since (i) it is a BOOK or the test sequence would not be 
 * considered and (ii) the source iterator is initialized with M which is 
 * the parent of N. Also, and still in this example, a call to last() will 
 * return the number of elements in the source (i.e. the number of BOOKs).
 */
public final class MatchingIterator extends DTMAxisIteratorBase {

    /**
     * A reference to a source iterator.
     */
    private DTMAxisIterator _source;

    /**
     * The node to match.
     */
    private final int _match;

    public MatchingIterator(int match, DTMAxisIterator source) {
	_source = source;
	_match = match;
    }


    public void setRestartable(boolean isRestartable) {
	_isRestartable = isRestartable;
	_source.setRestartable(isRestartable);
    }

    public DTMAxisIterator cloneIterator() {

	try {
	    final MatchingIterator clone = (MatchingIterator) super.clone();
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
    
    public DTMAxisIterator setStartNode(int node) {
	if (_isRestartable) {
	    // iterator is not a clone
	    _source.setStartNode(node);

	    // Calculate the position of the node in the set
	    _position = 1;
	    while ((node = _source.next()) != END && node != _match) {
		_position++;
	    }
	}
	return this;
    }

    public DTMAxisIterator reset() {
	_source.reset();
	return resetPosition();
    }
    
    public int next() {
	return _source.next();
    }
	
    public int getLast() {
        if (_last == -1) {
            _last = _source.getLast();
        }
        return _last;
    }

    public int getPosition() {
	return _position;
    }

    public void setMark() {
	_source.setMark();
    }

    public void gotoMark() {
	_source.gotoMark();
    }
}
