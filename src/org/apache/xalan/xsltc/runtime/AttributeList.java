/*
 * $Id$
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
 * @author Morten Jorgensen
 *
 */

package org.apache.xalan.xsltc.runtime;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

import java.util.Vector;
import java.util.Enumeration;

public class AttributeList implements org.xml.sax.Attributes {

    private final static String EMPTYSTRING = "";
    private final static String CDATASTRING = "CDATA";

    private Hashtable _attributes;
    private Vector    _names;
    private Vector    _qnames;
    private Vector    _values;
    private Vector    _uris;
    private int       _length;

    /**
     * AttributeList constructor
     */
    public AttributeList() {
	_attributes = new Hashtable();
	_names  = new Vector();
	_values = new Vector();
	_qnames = new Vector();
	_uris   = new Vector();
	_length = 0;
    }

    /**
     * Attributes clone constructor
     */
    public AttributeList(org.xml.sax.Attributes attributes) {
	this();
	if (attributes != null) {
	    final int count = attributes.getLength();
	    for (int i = 0; i < count; i++) {
		add(attributes.getQName(i),attributes.getValue(i));
	    }
	}
    }

    /**
     * SAX2: Return the number of attributes in the list. 
     */
    public int getLength() {
	return(_length);
    }

    /**
     * SAX2: Look up an attribute's Namespace URI by index.
     */
    public String getURI(int index) {
	if (index < _length)
	    return((String)_uris.elementAt(index));
	else
	    return(null);
    }

    /**
     * SAX2: Look up an attribute's local name by index.
     */
    public String getLocalName(int index) {
	if (index < _length)
	    return((String)_names.elementAt(index));
	else
	    return(null);
    }

    /**
     * Return the name of an attribute in this list (by position).
     */
    public String getQName(int pos) {
	if (pos < _length)
	    return((String)_qnames.elementAt(pos));
	else
	    return(null);
    }

    /**
     * SAX2: Look up an attribute's type by index.
     */
    public String getType(int index) {
	return(CDATASTRING);
    }

    /**
     * SAX2: Look up the index of an attribute by Namespace name.
     */
    public int getIndex(String namespaceURI, String localPart) {
	return(0);
    }

    /**
     * SAX2: Look up the index of an attribute by XML 1.0 qualified name.
     */
    public int getIndex(String qname) {
	return(0);
    }

    /**
     * SAX2: Look up an attribute's type by Namespace name.
     */
    public String getType(String uri, String localName) {
	return(CDATASTRING);
    }

    /**
     * SAX2: Look up an attribute's type by qname.
     */
    public String getType(String qname) {
	return(CDATASTRING);
    }

    /**
     * SAX2: Look up an attribute's value by index.
     */
    public String getValue(int pos) {
	if (pos < _length)
	    return((String)_values.elementAt(pos));
	else
	    return(null);
    }

    /**
     * SAX2: Look up an attribute's value by qname.
     */
    public String getValue(String qname) {
	final Integer obj = (Integer)_attributes.get(qname);
	if (obj == null) return null;
	return(getValue(obj.intValue()));
    }

    /**
     * SAX2: Look up an attribute's value by Namespace name - SLOW!
     */
    public String getValue(String uri, String localName) {
	return(getValue(uri+':'+localName));
    }

    /**
     * Adds an attribute to the list
     */
    public void add(String qname, String value) {
	// Stuff the QName into the names vector & hashtable
	Integer obj = (Integer)_attributes.get(qname);
	if (obj == null) {
	    _attributes.put(qname, obj = new Integer(_length++));
	    _qnames.addElement(qname);
	    _values.addElement(value);
	    int col = qname.lastIndexOf(':');
	    if (col > -1) {
		_uris.addElement(qname.substring(0,col));
		_names.addElement(qname.substring(col+1));
	    }
	    else {
		_uris.addElement(EMPTYSTRING);
		_names.addElement(qname);
	    }
	}
	else {
	    final int index = obj.intValue();
	    _values.set(index, value);
	}
    }

    /**
     * Clears the attribute list
     */
    public void clear() {
	_length = 0;
	_attributes.clear();
	_names.removeAllElements();
	_values.removeAllElements();
	_qnames.removeAllElements();
	_uris.removeAllElements();
    }
    
}
