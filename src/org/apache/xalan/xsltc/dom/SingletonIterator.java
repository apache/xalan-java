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

import org.apache.xalan.xsltc.NodeIterator;

public class SingletonIterator extends NodeIteratorBase {
    private int _node;
    private final boolean _isConstant;

    public SingletonIterator() {
	this(Integer.MIN_VALUE, false);
    }

    public SingletonIterator(int node) {
	this(node, false);
    }

    public SingletonIterator(int node, boolean constant) {
	_node = _startNode = node;
	_isConstant = constant;
    }
    
    /**
     * Override the value of <tt>_node</tt> only when this
     * object was constructed using the empty constructor.
     */
    public NodeIterator setStartNode(int node) {
	if (_isConstant) {
	    _node = _startNode;
	    return resetPosition();
	}
	else if (_isRestartable) {
	    if (_node <= 0)
		_node = _startNode = node;
	    return resetPosition();
	}
	return this;
    }
	
    public NodeIterator reset() {
	if (_isConstant) {
	    _node = _startNode;
	    return resetPosition();
	}
	else {
	    final boolean temp = _isRestartable;
	    _isRestartable = true;
	    setStartNode(_startNode);
	    _isRestartable = temp;
	}
	return this;
    }
    
    public int next() {
	final int result = _node;
	_node = NodeIterator.END;
	return returnNode(result);
    }

    public void setMark() {
	_markedNode = _node;
    }

    public void gotoMark() {
	_node = _markedNode;
    }
}
