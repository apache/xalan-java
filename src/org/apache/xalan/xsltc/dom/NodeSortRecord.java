/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id$
 */

package org.apache.xalan.xsltc.dom;

import java.text.CollationKey;
import java.text.Collator;
import java.util.Locale;

import org.apache.xalan.xsltc.CollatorFactory;
import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.runtime.AbstractTranslet;
import org.apache.xml.utils.ObjectFactory;
import org.apache.xml.utils.StringComparable;

/**
 * Base class for sort records containing application specific sort keys 
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 * @author Morten Jorgensen
 * @author W. Eliot Kimber (eliot@isogen.com)
 */
public abstract class NodeSortRecord {
    public static final int COMPARE_STRING     = 0;
    public static final int COMPARE_NUMERIC    = 1;

    public static final int COMPARE_ASCENDING  = 0;
    public static final int COMPARE_DESCENDING = 1;

    /**
     * A reference to a locales. 
     */
    protected Locale[] _locale;

    /**
     * A reference to a collators. 
     */
    protected Collator[] _collator ;
    protected CollatorFactory _collatorFactory;

    protected int   _levels = 1;
    protected int[] _compareType;
    protected int[] _sortOrder;
    protected String[] _case_order;

    private AbstractTranslet _translet = null;

    private DOM    _dom = null;
    private int    _node;           // The position in the current iterator
    private int    _last = 0;       // Number of nodes in the current iterator
    private int    _scanned = 0;    // Number of key levels extracted from DOM

    private Object[] _values; // Contains Comparable  objects

    /**
     * This constructor is run by a call to ClassLoader in the
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
	 AbstractTranslet translet, int[] order, int[] type, final Locale[] locale, final Collator[] collator,
     final String[] case_order, NodeSortRecordFactory nsrFactory) throws TransletException
    {
	_dom = dom;
	_node = node;
	_last = last;
	_translet = translet;
	_scanned = 0;

	_levels = order.length;
	_sortOrder = order;
	_compareType = type;

	_values = new Object[_levels];
   _locale = locale;
  
	// -- W. Eliot Kimber (eliot@isogen.com)
        String colFactClassname = 
	    System.getProperty("org.apache.xalan.xsltc.COLLATOR_FACTORY");

        if (colFactClassname != null) {
            try {
                Object candObj = ObjectFactory.findProviderClass(
                    colFactClassname, ObjectFactory.findClassLoader(), true);
                _collatorFactory = (CollatorFactory)candObj;
            } 
	          catch (ClassNotFoundException e) {
		          throw new TransletException(e);
            }
             for(int i = 0; i< _levels; i++){
                _collator[i] = _collatorFactory.getCollator(_locale[i]);
             }
        }else {
    	    _collator = collator;
        }
     
     
     _case_order = case_order;
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
    private final Comparable stringValue(int level) {
    	// Get value from our array if possible
    	if (_scanned <= level) {
    	    // Get value from DOM if accessed for the first time
    	    final String str = extractValueFromDOM(_dom, _node, level,
    						   _translet, _last);
    	    final Comparable key = StringComparable.getComparator(str, _locale[level], _collator[level], _case_order[level]);
    	    _values[_scanned++] = key;
    	    return(key);
    	}
    	return((Comparable)_values[level]);
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
		final Comparable our = stringValue(level);
		final Comparable their = other.stringValue(level);
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
    public Collator[] getCollator() {
	return _collator;
    }

    /**
     * Extract the sort value for a level of this key.
     */
    public abstract String extractValueFromDOM(DOM dom, int current, int level,
					       AbstractTranslet translet,
					       int last);

}
