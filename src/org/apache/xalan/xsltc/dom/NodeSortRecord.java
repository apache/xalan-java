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

import java.util.Vector;
import java.text.Collator;
import java.text.CollationKey;

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.runtime.AbstractTranslet;

/**
 * Base class for sort records containing application specific sort keys 
 */
public abstract class NodeSortRecord {
    public static int COMPARE_STRING     = 0;
    public static int COMPARE_NUMERIC    = 1;

    public static int COMPARE_ASCENDING  = 0;
    public static int COMPARE_DESCENDING = 1;

    protected static Collator _collator = Collator.getInstance();

    protected int   _levels = 1;
    protected int[] _compareType;
    protected int[] _sortOrder;

    private AbstractTranslet _translet = null;

    private DOM    _dom = null;
    private int    _node;           // The position in the current iterator
    private int    _last = 0;       // Number of nodes in the current iterator
    private int    _scanned = 0;    // Number of key levels extracted from DOM

    private Object[] _values; // Contains either CollationKey or Double

    /**
     * This constructor is run by a call to Class.forName() in the
     * makeNodeSortRecord method in the NodeSortRecordFactory class. Since we
     * cannot pass any parameters to the constructor in that case we just set
     * the default values here and wait for new values through initialize().
     */ 
    public NodeSortRecord(int node) {
	_node = node;
    }

    public NodeSortRecord() {
        this(0);
    }

    /**
     * This method allows the caller to set the values that could not be passed
     * to the default constructor.
     */
    public final void initialize(int node, int last, DOM dom,
				 AbstractTranslet translet,
				 int[] order, int[] type) {
	_dom = dom;
	_node = node;
	_last = last;
	_translet = translet;
	_scanned = 0;

	_levels = order.length;
	_sortOrder = order;
	_compareType = type;

	_values = new Object[_levels];
    }

    /**
     * Returns the node for this sort object
     */
    public final int getNode() {
	return _node;
    }

    /**
     *
     */
    public final int compareDocOrder(NodeSortRecord other) {
	return _node - other._node;
    }

    /**
     * Get the string or numeric value of a specific level key for this sort
     * element. The value is extracted from the DOM if it is not already in
     * our sort key vector.
     */
    private final CollationKey stringValue(int level) {
	// Get value from our array if possible
	if (_scanned <= level) {
	    // Get value from DOM if accessed for the first time
	    final String str = extractValueFromDOM(_dom, _node, level,
						   _translet, _last);
	    final CollationKey key = _collator.getCollationKey(str);
	    _values[_scanned++] = key;
	    return(key);
	}
	return((CollationKey)_values[level]);
    }
    
    private final Double numericValue(int level) {
	// Get value from our vector if possible
	if (_scanned <= level) {
	    // Get value from DOM if accessed for the first time
	    final String str = extractValueFromDOM(_dom, _node, level,
						   _translet, _last);
	    Double num;
	    try {
		num = new Double(str);
	    }
	    // Treat number as NaN if it cannot be parsed as a double
	    catch (NumberFormatException e) {
		num = new Double(Double.NEGATIVE_INFINITY);
	    }
	    _values[_scanned++] = num;
	    return(num);
	}
	return((Double)_values[level]);
    }

    /**
     * Compare this sort element to another. The first level is checked first,
     * and we proceed to the next level only if the first level keys are
     * identical (and so the key values may not even be extracted from the DOM)
     *
     * !!!!MUST OPTIMISE - THIS IS REALLY, REALLY SLOW!!!!
     */
    public int compareTo(NodeSortRecord other) {
	int cmp, level;
	for (level = 0; level < _levels; level++) {
	    // Compare the two nodes either as numeric or text values
	    if (_compareType[level] == COMPARE_NUMERIC) {
		final Double our = numericValue(level);
		final Double their = other.numericValue(level);
		cmp = our.compareTo(their);
	    }
	    else {
		final CollationKey our = stringValue(level);
		final CollationKey their = other.stringValue(level);
		cmp = our.compareTo(their);
	    }
	    
	    // Return inverse compare value if inverse sort order
	    if (cmp != 0) {
		return _sortOrder[level] == COMPARE_DESCENDING ? 0 - cmp : cmp;
	    }
	}
	// Compare based on document order if all sort keys are equal
	return(_node - other._node);
    }

    /**
     * Returns the Collator used for text comparisons in this object.
     * May be overridden by inheriting classes
     */
    public Collator getCollator() {
	return _collator;
    }

    /**
     * Extract the sort value for a level of this key.
     */
    public abstract String extractValueFromDOM(DOM dom, int current, int level,
					       AbstractTranslet translet,
					       int last);

}
