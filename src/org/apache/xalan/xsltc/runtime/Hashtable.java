/*
 * @(#)$Id$
 *
 * The Apache Software License, Version 1.1
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

import java.io.*;
import java.util.Enumeration;

/**
 * IMPORTANT NOTE:
 * This code was taken from Sun's Java1.1 JDK java.util.HashTable.java
 * All "synchronized" keywords and some methods we do not need have been 
 * all been removed.
 */

/**
 * Object that wraps entries in the hash-table
 */
class HashtableEntry {
    int hash;
    Object key;
    Object value;
    HashtableEntry next;

    protected Object clone() {
	HashtableEntry entry = new HashtableEntry();
	entry.hash = hash;
	entry.key = key;
	entry.value = value;
	entry.next = (next != null) ? (HashtableEntry)next.clone() : null;
	return entry;
    }
}

/**
 * The main hash-table implementation
 */
public class Hashtable {

    private transient HashtableEntry table[]; // hash-table entries
    private transient int count;              // number of entries
    private int threshold;                    // current size of hash-tabke
    private float loadFactor;                 // load factor

    /**
     * Constructs a new, empty hashtable with the specified initial 
     * capacity and the specified load factor. 
     */
    public Hashtable(int initialCapacity, float loadFactor) {
	if (initialCapacity <= 0) initialCapacity = 11;
	if (loadFactor <= 0.0) loadFactor = 0.75f;
	this.loadFactor = loadFactor;
	table = new HashtableEntry[initialCapacity];
	threshold = (int)(initialCapacity * loadFactor);
    }

    /**
     * Constructs a new, empty hashtable with the specified initial capacity
     * and default load factor.
     */
    public Hashtable(int initialCapacity) {
	this(initialCapacity, 0.75f);
    }

    /**
     * Constructs a new, empty hashtable with a default capacity and load
     * factor. 
     */
    public Hashtable() {
	this(101, 0.75f);
    }

    /**
     * Returns the number of keys in this hashtable.
     */
    public int size() {
	return count;
    }

    /**
     * Tests if this hashtable maps no keys to values.
     */
    public boolean isEmpty() {
	return count == 0;
    }

    /**
     * Returns an enumeration of the keys in this hashtable.
     */
    public Enumeration keys() {
	return new HashtableEnumerator(table, true);
    }

    /**
     * Returns an enumeration of the values in this hashtable.
     * Use the Enumeration methods on the returned object to fetch the elements
     * sequentially.
     */
    public Enumeration elements() {
	return new HashtableEnumerator(table, false);
    }

    /**
     * Tests if some key maps into the specified value in this hashtable.
     * This operation is more expensive than the <code>containsKey</code>
     * method.
     */
    public boolean contains(Object value) {

	if (value == null) throw new NullPointerException();

	int i;
	HashtableEntry e;
	HashtableEntry tab[] = table;

	for (i = tab.length ; i-- > 0 ;) {
	    for (e = tab[i] ; e != null ; e = e.next) {
		if (e.value.equals(value)) {
		    return true;
		}
	    }
	}
	return false;
    }

    /**
     * Tests if the specified object is a key in this hashtable.
     */
    public boolean containsKey(Object key) {
	HashtableEntry e;
	HashtableEntry tab[] = table;
	int hash = key.hashCode();
	int index = (hash & 0x7FFFFFFF) % tab.length;

	for (e = tab[index] ; e != null ; e = e.next)
	    if ((e.hash == hash) && e.key.equals(key))
		return true;

	return false;
    }

    /**
     * Returns the value to which the specified key is mapped in this hashtable.
     */
    public Object get(Object key) {
	HashtableEntry e;
	HashtableEntry tab[] = table;
	int hash = key.hashCode();
	int index = (hash & 0x7FFFFFFF) % tab.length;

	for (e = tab[index] ; e != null ; e = e.next) 
	    if ((e.hash == hash) && e.key.equals(key)) 
		return e.value;

	return null;
    }

    /**
     * Rehashes the contents of the hashtable into a hashtable with a 
     * larger capacity. This method is called automatically when the 
     * number of keys in the hashtable exceeds this hashtable's capacity 
     * and load factor. 
     */
    protected void rehash() {
	HashtableEntry e, old;
	int i, index;
	int oldCapacity = table.length;
	HashtableEntry oldTable[] = table;

	int newCapacity = oldCapacity * 2 + 1;
	HashtableEntry newTable[] = new HashtableEntry[newCapacity];

	threshold = (int)(newCapacity * loadFactor);
	table = newTable;

	for (i = oldCapacity ; i-- > 0 ;) {
	    for (old = oldTable[i] ; old != null ; ) {
		e = old;
		old = old.next;
		index = (e.hash & 0x7FFFFFFF) % newCapacity;
		e.next = newTable[index];
		newTable[index] = e;
	    }
	}
    }

    /**
     * Maps the specified <code>key</code> to the specified 
     * <code>value</code> in this hashtable. Neither the key nor the 
     * value can be <code>null</code>. 
     * <p>
     * The value can be retrieved by calling the <code>get</code> method 
     * with a key that is equal to the original key. 
     */
    public Object put(Object key, Object value) {
	// Make sure the value is not null
	if (value == null) throw new NullPointerException();

	// Makes sure the key is not already in the hashtable.
	HashtableEntry e;
	HashtableEntry tab[] = table;
	int hash = key.hashCode();
	int index = (hash & 0x7FFFFFFF) % tab.length;

	for (e = tab[index] ; e != null ; e = e.next) {
	    if ((e.hash == hash) && e.key.equals(key)) {
		Object old = e.value;
		e.value = value;
		return old;
	    }
	}

	// Rehash the table if the threshold is exceeded
	if (count >= threshold) {
	    rehash();
	    return put(key, value);
	} 

	// Creates the new entry.
	e = new HashtableEntry();
	e.hash = hash;
	e.key = key;
	e.value = value;
	e.next = tab[index];
	tab[index] = e;
	count++;
	return null;
    }

    /**
     * Removes the key (and its corresponding value) from this 
     * hashtable. This method does nothing if the key is not in the hashtable.
     */
    public Object remove(Object key) {
	HashtableEntry e, prev;
	HashtableEntry tab[] = table;
	int hash = key.hashCode();
	int index = (hash & 0x7FFFFFFF) % tab.length;
	for (e = tab[index], prev = null ; e != null ; prev = e, e = e.next) {
	    if ((e.hash == hash) && e.key.equals(key)) {
		if (prev != null)
		    prev.next = e.next;
		else
		    tab[index] = e.next;
		count--;
		return e.value;
	    }
	}
	return null;
    }

    /**
     * Clears this hashtable so that it contains no keys. 
     */
    public void clear() {
	HashtableEntry tab[] = table;
	for (int index = tab.length; --index >= 0; )
	    tab[index] = null;
	count = 0;
    }

    /**
     * Returns a rather long string representation of this hashtable.
     * Handy for debugging - leave it here!!!
     */
    public String toString() {
	int i;
	int max = size() - 1;
	StringBuffer buf = new StringBuffer();
	Enumeration k = keys();
	Enumeration e = elements();
	buf.append("{");

	for (i = 0; i <= max; i++) {
	    String s1 = k.nextElement().toString();
	    String s2 = e.nextElement().toString();
	    buf.append(s1 + "=" + s2);
	    if (i < max) buf.append(", ");
	}
	buf.append("}");
	return buf.toString();
    }

    /**
     * A hashtable enumerator class.  This class should remain opaque 
     * to the client. It will use the Enumeration interface.
     */
    class HashtableEnumerator implements Enumeration {
	boolean keys;
	int index;
	HashtableEntry table[];
	HashtableEntry entry;

	HashtableEnumerator(HashtableEntry table[], boolean keys) {
	    this.table = table;
	    this.keys = keys;
	    this.index = table.length;
	}
	
	public boolean hasMoreElements() {
	    if (entry != null) {
		return true;
	    }
	    while (index-- > 0) {
		if ((entry = table[index]) != null) {
		    return true;
		}
	    }
	    return false;
	}

	public Object nextElement() {
	    if (entry == null) {
		while ((index-- > 0) && ((entry = table[index]) == null));
	    }
	    if (entry != null) {
		HashtableEntry e = entry;
		entry = e.next;
		return keys ? e.key : e.value;
	    }
	    return null;
	}
    }

}
