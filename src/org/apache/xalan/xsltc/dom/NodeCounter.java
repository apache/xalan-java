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
import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.Translet;
import org.apache.xalan.xsltc.NodeIterator;
import org.apache.xalan.xsltc.dom.Axis;

public abstract class NodeCounter implements Axis {
    public static final int END = DOM.NULL;

    protected int _node = END;
    protected int _nodeType = DOM.FIRST_TYPE - 1;
    protected int _value = Integer.MIN_VALUE;

    public final DOM          _document;
    public final NodeIterator _iterator;
    public final Translet     _translet;

    protected String _format;
    protected String _lang;
    protected String _letterValue;
    protected String _groupSep;
    protected int    _groupSize;

    private boolean separFirst = true;
    private boolean separLast = false;
    private Vector separToks = null;
    private Vector formatToks = null;
    private int nSepars  = 0;
    private int nFormats = 0;

    private static String[] Thousands = 
        {"", "m", "mm", "mmm" };
    private static String[] Hundreds = 
	{"", "c", "cc", "ccc", "cd", "d", "dc", "dcc", "dccc", "cm"};
    private static String[] Tens = 
	{"", "x", "xx", "xxx", "xl", "l", "lx", "lxx", "lxxx", "xc"};
    private static String[] Ones = 
	{"", "i", "ii", "iii", "iv", "v", "vi", "vii", "viii", "ix"};

    protected NodeCounter(Translet translet,
			  DOM document, NodeIterator iterator) {
	_translet = translet;
	_document = document;
	_iterator = iterator;
    }

    /** 
     * Set the start node for this counter. The same <tt>NodeCounter</tt>
     * object can be used multiple times by resetting the starting node.
     */
    abstract public NodeCounter setStartNode(int node);

    /** 
     * If the user specified a value attribute, use this instead of 
     * counting nodes.
     */
    public NodeCounter setValue(int value) {
	_value = value;
	return this;
    }

    /**
     * Sets formatting fields before calling formatNumbers().
     */
    protected void setFormatting(String format, String lang, String letterValue,
				 String groupSep, String groupSize) {
	_lang = lang;
	_format = format;
	_groupSep = groupSep;
	_letterValue = letterValue;

	try {
	    _groupSize = Integer.parseInt(groupSize);
	}
	catch (NumberFormatException e) {
	    _groupSize = 0;
	}

	final int length = _format.length();
	boolean isFirst = true;
	separFirst = true;
	separLast = false;

        separToks = new Vector();
        formatToks = new Vector();

	/* 
	 * Tokenize the format string into alphanumeric and non-alphanumeric
	 * tokens as described in M. Kay page 241.
	 */
	for (int j = 0, i = 0; i < length;) {
            char c = _format.charAt(i);
            for (j = i; Character.isLetterOrDigit(c);) {
                if (++i == length) break;
		c = _format.charAt(i);
            }
            if (i > j) {
                if (isFirst) {
                    separToks.addElement(".");
                    isFirst = separFirst = false;
                }
                formatToks.addElement(_format.substring(j, i));
            }

            if (i == length) break;

            c = _format.charAt(i);
            for (j = i; !Character.isLetterOrDigit(c);) {
                if (++i == length) break;
                c = _format.charAt(i);
                isFirst = false;
            }
            if (i > j) {
                separToks.addElement(_format.substring(j, i));
            }
        }

	nSepars = separToks.size();
	nFormats = formatToks.size(); 
	if (nSepars > nFormats) separLast = true;

	if (separFirst) nSepars--;
	if (separLast) nSepars--;
	if (nSepars == 0) {
	    separToks.insertElementAt(".", 1);
 	    nSepars++;
	}
	if (separFirst) nSepars ++;
    }

    /**
     * Sets formatting fields to their default values.
     */
    public NodeCounter setDefaultFormatting() {
	setFormatting("1", "en", "alphabetic", null, null);
	return this;
    }

    /**
     * Returns the position of <tt>node</tt> according to the level and 
     * the from and count patterns.
     */
    abstract public String getCounter();

    /**
     * Returns the position of <tt>node</tt> according to the level and 
     * the from and count patterns. This position is converted into a
     * string based on the arguments passed.
     */
    public String getCounter(String format, String lang, String letterValue,
			     String groupSep, String groupSize) {
	setFormatting(format, lang, letterValue, groupSep, groupSize);
	return getCounter();
    }

    /**
     * Returns true if <tt>node</tt> matches the count pattern. By
     * default a node matches the count patterns if it is of the 
     * same type as the starting node.
     */
    public boolean matchesCount(int node) {
	return _nodeType == _document.getType(node);
    }

    /**
     * Returns true if <tt>node</tt> matches the from pattern. By default, 
     * no node matches the from pattern.
     */
    public boolean matchesFrom(int node) {
	return false;
    }

    /**
     * Format a single value according to the format parameters.
     */
    protected String formatNumbers(int value) {
	return formatNumbers(new int[] { value });
    }

    /**
     * Format a sequence of values according to the format paramaters
     * set by calling setFormatting().
     */
    protected String formatNumbers(int[] values) {
	final int nValues = values.length;
	final int length = _format.length();

	boolean isEmpty = true;
	for (int i = 0; i < nValues; i++)
	    if (values[i] != Integer.MIN_VALUE)
		isEmpty = false;
	if (isEmpty) return("");

	// Format the output string using the values array and the fmt. tokens
	boolean isFirst = true;
	int t = 0, n = 0, s = 1;
	final StringBuffer buffer = new StringBuffer();

	// Append separation token before first digit/letter/numeral
	if (separFirst) buffer.append((String)separToks.elementAt(0));

	// Append next digit/letter/numeral and separation token
	while (n < nValues) {
	    final int value = values[n];
	    if (value != Integer.MIN_VALUE) {
		if (!isFirst) buffer.append((String) separToks.elementAt(s++));
		formatValue(value, (String)formatToks.elementAt(t++), buffer);
		if (t == nFormats) t--;
		if (s >= nSepars) s--;
		isFirst = false;
	    }
	    n++;
	}

	// Append separation token after last digit/letter/numeral
	if (separLast) buffer.append((String)separToks.lastElement());
	return buffer.toString();
    }

    /**
     * Format a single value based on the appropriate formatting token. 
     * This method is based on saxon (Michael Kay) and only implements
     * lang="en".
     */
    private void formatValue(int value, String format, StringBuffer buffer) {

        char c = format.charAt(0);
        if (Character.isDigit(c)) {
            char zero = (char)(c - Character.getNumericValue(c));

            StringBuffer temp = buffer;
            if (_groupSize > 0) {
                temp = new StringBuffer();
            }
            String s = "";
            int n = value;
            while (n > 0) {
                s = (char) ((int) zero + (n % 10)) + s;
                n = n / 10;
            }
                
            for (int i = 0; i < format.length() - s.length(); i++) {
                temp.append(zero);
            }
            temp.append(s);
            
            if (_groupSize > 0) {
                for (int i = 0; i < temp.length(); i++) {
                    if (i != 0 && ((temp.length() - i) % _groupSize) == 0) {
                        buffer.append(_groupSep);
                    }
                    buffer.append(temp.charAt(i));
                }
            }
        } 
	else if (c == 'i' && !_letterValue.equals("alphabetic")) {
            buffer.append(romanValue(value));
        } 
	else if (c == 'I' && !_letterValue.equals("alphabetic")) {
            buffer.append(romanValue(value).toUpperCase());
        } 
	else {
            int min = (int) c;
            int max = (int) c;
            while (Character.isLetterOrDigit((char) (max+1))) {
		max++;
	    }
            buffer.append(alphaValue(value, min, max));
        }
    }

    private String alphaValue(int value, int min, int max) {
        if (value <= 0) {
	    return "" + value;
	}

        int range = max - min + 1;
        char last = (char)(((value-1) % range) + min);
        if (value > range) {
            return alphaValue((value-1) / range, min, max) + last;
        } 
	else {
            return "" + last;
        }
    }

    private String romanValue(int n) {
        if (n <= 0 || n > 4000) {
	    return "" + n;
	}
        return
	    Thousands[n / 1000] +
	    Hundreds[(n / 100) % 10] +
	    Tens[(n/10) % 10] +
	    Ones[n % 10];
    }
}

