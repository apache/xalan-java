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
 *
 */

package org.apache.xalan.xsltc.util;

public final class IntegerArray {
    private static final int InitialSize = 32;
    
    private int[] _array;
    private int   _size;
    private int   _free = 0;
  
    public IntegerArray() {
	this(InitialSize);
    }
  
    public IntegerArray(int size) {
	_array = new int[_size = size];
    }

    public IntegerArray(int[] array) {
	this(array.length);
	System.arraycopy(array, 0, _array, 0, _free = _size);
    }

    public void clear() {
	_free = 0;
    }

    public Object clone() {
	final IntegerArray clone = new IntegerArray(_free);
	System.arraycopy(_array, 0, clone._array, 0, _free);
	clone._free = _free;
	return clone;
    }

    public int[] toIntArray() {
	final int[] result = new int[cardinality()];
	System.arraycopy(_array, 0, result, 0, cardinality());
	return result;
    }

    public final int at(int index) {
	return _array[index];
    }

    public final void set(int index, int value) {
	_array[index] = value;
    }

    public int indexOf(int n) {
	for (int i = 0; i < _free; i++) {
	    if (n == _array[i]) return i;
	}
	return -1;
    }

    public final void add(int value) {
	if (_free == _size) {
	    growArray(_size * 2);
	}
	_array[_free++] = value;
    }
  
    /** 
     * Adds new int at the end if not already present.
     */
    public void addNew(int value) {
	for (int i = 0; i < _free; i++) {
	    if (_array[i] == value) return;  // already in array
	}
	add(value);
    }

    public void reverse() {
	int left = 0; 
	int right = _free - 1;

	while (left < right) {
	    int temp = _array[left];
	    _array[left++] = _array[right];
	    _array[right--] = temp;
	}
    }

    /**
     * Merge two sorted arrays and eliminate duplicates. 
     */
    public void merge(IntegerArray other) {
	final int newSize = _free + other._free;
// System.out.println("IntegerArray.merge() begin newSize = " + newSize);
	int[] newArray = new int[newSize];

	// Merge the two arrays
	int i = 0, j = 0, k;
	for (k = 0; i < _free && j < other._free; k++) {
	    int x = _array[i];
	    int y = other._array[j];

	    if (x < y) {
		newArray[k] = x;
		i++;
	    }
	    else if (x > y) {
		newArray[k] = y;
		j++;
	    }
	    else {
		newArray[k] = x;
		i++; j++;
	    }
	}

	// Copy the rest if of different lengths
	if (i >= _free) {
	    while (j < other._free) {
		newArray[k++] = other._array[j++];
	    }
	}
	else {
	    while (i < _free) {
		newArray[k++] = _array[i++];
	    }
	}

	// Update reference to this array
	_array = newArray;
	_free = _size = newSize;
// System.out.println("IntegerArray.merge() end");
    }

    public void sort() {
	quicksort(_array, 0, _free - 1);
    }

    private static void quicksort(int[] array, int p, int r) {
	if (p < r) {
	    final int q = partition(array, p, r);
	    quicksort(array, p, q);
	    quicksort(array, q + 1, r);
	}
    }
    
    private static int partition(int[] array, int p, int r) {
	final int x = array[(p + r) >>> 1];
	int i = p - 1; int j = r + 1;

	while (true) {
	    while (x < array[--j]);
	    while (x > array[++i]);
	    if (i < j) {
		int temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	    }
	    else {
		return j;
	    }
	}
    }

    private void growArray(int size) {
	final int[] newArray = new int[_size = size];
	System.arraycopy(_array, 0, newArray, 0, _free);
	_array = newArray;
    }

    public int popLast() {
	return _array[--_free];
    }

    public int last() {
	return _array[_free - 1];
    }

    public void setLast(int n) {
	_array[_free - 1] = n;
    }

    public void pop() {
	_free--;
    }

    public void pop(int n) {
	_free -= n;
    }
  
    public final int cardinality() {
	return _free;
    }

    public void print(java.io.PrintStream out) {
	if (_free > 0) {
	    for (int i = 0; i < _free - 1; i++) {
		out.print(_array[i]);
		out.print(' ');
	    }
	    out.println(_array[_free - 1]);
	}
	else {
	    out.println("IntegerArray: empty");
	}
    }
}
