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
import org.apache.xalan.xsltc.Translet;
import org.apache.xalan.xsltc.NodeIterator;
import org.apache.xalan.xsltc.util.IntegerArray;

public abstract class MultipleNodeCounter extends NodeCounter {
    private NodeIterator _precSiblings = null;

    public MultipleNodeCounter(Translet translet,
			       DOM document, NodeIterator iterator) {
	super(translet, document, iterator);
    }
	
    public NodeCounter setStartNode(int node) {
	_node = node;
	_nodeType = _document.getType(node);
	_precSiblings = _document.getAxisIterator(PRECEDINGSIBLING);
	return this;
    }

    public String getCounter() {
	if (_value != Integer.MIN_VALUE) {
	    return formatNumbers(_value);
	}

	IntegerArray ancestors = new IntegerArray();

	// Gather all ancestors that do not match from pattern
	int next = _node;
	ancestors.add(next);		// include self
	while ((next = _document.getParent(next)) > END && 
	       !matchesFrom(next)) {
	    ancestors.add(next);
	}

	// Create an array of counters
	final int nAncestors = ancestors.cardinality();
	final int[] counters = new int[nAncestors]; 
	for (int i = 0; i < nAncestors; i++) {
	    counters[i] = Integer.MIN_VALUE;
	}

	// Increment array of counters according to semantics
	for (int j = 0, i = nAncestors - 1; i >= 0 ; i--, j++) {
	    final int counter = counters[j];
	    final int ancestor = ancestors.at(i);

	    if (matchesCount(ancestor)) {
		_precSiblings.setStartNode(ancestor);
		while ((next = _precSiblings.next()) != END) {
		    if (matchesCount(next)) {
			counters[j] = (counters[j] == Integer.MIN_VALUE) ? 1 
			    : counters[j] + 1;		
		    }
		}
		// Count the node itself
		counters[j] = counters[j] == Integer.MIN_VALUE
		    ? 1 
		    : counters[j] + 1;	
	    }
	}
	return formatNumbers(counters);
    }

    public static NodeCounter getDefaultNodeCounter(Translet translet,
						    DOM document,
						    NodeIterator iterator) {
	return new DefaultMultipleNodeCounter(translet, document, iterator);
    }

    static class DefaultMultipleNodeCounter extends MultipleNodeCounter {
	public DefaultMultipleNodeCounter(Translet translet,
					  DOM document,
					  NodeIterator iterator) {
	    super(translet, document, iterator);
	}
    }
}
