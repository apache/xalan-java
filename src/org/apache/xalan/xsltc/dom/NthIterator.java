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

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.NodeIterator;
import org.apache.xalan.xsltc.runtime.BasisLibrary;

public final class NthIterator extends NodeIteratorBase {
    // ...[N]
    private NodeIterator _source;
    private final int _position;
    private boolean _ready;

    public NthIterator(NodeIterator source, int n) {
	_source = source;
	_position = n;
    }

    public void setRestartable(boolean isRestartable) {
	_isRestartable = isRestartable;
	_source.setRestartable(isRestartable);
    }
    
    public int next() {
	if (_ready && _position > 0) {
	    _ready = false;
	    // skip N-1 nodes
	    final int pos = _position;
	    for (int n = pos - 1; n-- > 0;) {
		if (_source.next() == NodeIterator.END) {
		    return NodeIterator.END;
		}
	    }
	    return _source.next();
	}
	return NodeIterator.END;
    }
	
    public NodeIterator setStartNode(final int node) {
	if (_isRestartable) {
	    _source.setStartNode(node);
	    _ready = true;
	}
	return this;
    }
	
    public NodeIterator reset() {
	_source.reset();
	_ready = true;
	return this;
    }
    
    public int getLast() {
	return 1;
    }
    
    public int getPosition() {
	return 1;
    }
    
    public boolean isReverse() {
	return _source.isReverse();
    }
    
    public void setMark() {
	_source.setMark();
    }
    
    public void gotoMark() {
	_source.gotoMark();
    }

    public NodeIterator cloneIterator() {
	NodeIterator clone = _source.cloneIterator();
	NthIterator other = new NthIterator(clone, _position);
	other.setRestartable(false);
	return other.reset();
    }
}
