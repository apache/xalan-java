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
 * @author Morten Jorgensen
 *
 */

package org.apache.xalan.xsltc.dom;

import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.IOException;

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.NodeIterator;


public class BitArray implements Externalizable {

    private int[] _bits;
    private int   _bitSize;
    private int   _intSize;
    private int   _mask;

    // This table is used to prevent expensive shift operations
    // (These operations are inexpensive on CPUs but very expensive on JVMs.)
    private final static int[] _masks = {
	0x80000000, 0x40000000, 0x20000000, 0x10000000,
	0x08000000, 0x04000000, 0x02000000, 0x01000000,
	0x00800000, 0x00400000, 0x00200000, 0x00100000,
	0x00080000, 0x00040000, 0x00020000, 0x00010000,
	0x00008000, 0x00004000, 0x00002000, 0x00001000,
	0x00000800, 0x00000400, 0x00000200, 0x00000100,
	0x00000080, 0x00000040, 0x00000020, 0x00000010,
	0x00000008, 0x00000004, 0x00000002, 0x00000001 };

    /**
     * Constructor. Defines the initial size of the bit array (in bits).
     */
    public BitArray() {
	this(32);
    }

    public BitArray(int size) {
	if (size < 32) size = 32;
	_bitSize = size;
	_intSize = (_bitSize >>> 5) + 1;
	_bits = new int[_intSize + 1];
    }

    public BitArray(int size, int[] bits) {
	if (size < 32) size = 32;
	_bitSize = size;
	_intSize = (_bitSize >>> 5) + 1;
	_bits = bits;
    }

    /**
     * Set the mask for this bit array. The upper 8 bits of this mask
     * indicate the DOM in which the nodes in this array belong.
     */    
    public void setMask(int mask) {
	_mask = mask;
    }

    /**
     * See setMask()
     */
    public int getMask() {
	return(_mask);
    }

    /**
     * Returns the size of this bit array (in bits).
     */
    public final int size() {
	return(_bitSize);
    }

    /**
     * Returns true if the given bit is set
     */
    public final boolean getBit(int bit) {
	return((_bits[bit>>>5] & _masks[bit%32]) != 0);
    }

    /**
     * Returns the next set bit from a given position
     */
    public final int getNextBit(int startBit) {
	for (int i = (startBit >>> 5) ; i<=_intSize; i++) {
	    int bits = _bits[i];
	    if (bits != 0) {
		for (int b = (startBit % 32); b<32; b++) {
		    if ((bits & _masks[b]) != 0) {
			return((i << 5) + b);
		    }
		}
	    }
	    startBit = 0;
	}
	return(NodeIterator.END);
    }

    /**
     * This method returns the Nth bit that is set in the bit array. The
     * current position is cached in the following 4 variables and will
     * help speed up a sequence of next() call in an index iterator. This
     * method is a mess, but it is fast and it works, so don't fuck with it.
     */
    private int _pos = Integer.MAX_VALUE;
    private int _node = 0;
    private int _int = 0;
    private int _bit = 0;

    public final int getBitNumber(int pos) {

	// Return last node if position we're looking for is the same
	if (pos == _pos) return(_node);
	
	// Start from beginning of position we're looking for is before
	// the point where we left off the last time.
	if (pos < _pos) {
	    _int = _bit = _pos = 0;
	}

	// Scan through the bit array - skip integers that have no bits set
	for ( ; _int <= _intSize; _int++) {
	    int bits = _bits[_int];
	    if (bits != 0) { // Any bits set?
		for ( ; _bit < 32; _bit++) {
		    if ((bits & _masks[_bit]) != 0) {
			if (++_pos == pos) {
			    _node = ((_int << 5) + _bit) - 1;
			    return (_node);
			}
		    }
		}
		_bit = 0;
	    }
	}
	return(0);
    }

    /**
     * Returns the integer array in which the bit array is contained
     */
    public final int[] data() {
	return(_bits);
    }

    int _first = Integer.MAX_VALUE; // The index where first set bit is
    int _last  = Integer.MIN_VALUE; // The _INTEGER INDEX_ where last set bit is

    /**
     * Sets a given bit
     */
    public final void setBit(int bit) {
	if (bit >= _bitSize) return;
	final int i = (bit >>> 5);
	if (i < _first) _first = i;
	if (i > _last) _last = i;
	_bits[i] |= _masks[bit % 32];
    }

    /**
     * Merge two bit arrays. This currently only works for nodes from
     * a single DOM (because there is only one _mask per array).
     */
    public final BitArray merge(BitArray other) {
	// Take other array's bits if we have node set
	if (_last == -1) {
	    _bits = other._bits;
	}
	// Only merge if other array has any bits set
	else if (other._last != -1) {
	    int start = (_first < other._first) ? _first : other._first;
	    int stop  = (_last > other._last) ? _last : other._last;

	    // Merge these bits into other array if other array is larger
	    if (other._intSize > _intSize) {
		if (stop > _intSize) stop = _intSize;
		for (int i=start; i<=stop; i++)
		    other._bits[i] |= _bits[i];
		_bits = other._bits;
	    }
	    // Merge other bits into this array if this arrai is large/equal.
	    else {
		if (stop > other._intSize) stop = other._intSize;
		for (int i=start; i<=stop; i++)
		    _bits[i] |= other._bits[i];
	    }
	}
	return(this);
    }

    /**
     * Resizes the bit array - try to avoid using this method!!!
     */
    public final void resize(int newSize) {
	if (newSize > _bitSize) {
	    _intSize = (newSize >>> 5) + 1;
	    final int[] newBits = new int[_intSize + 1];
	    System.arraycopy(_bits, 0, newBits, 0, (_bitSize>>>5) + 1);
	    _bits = newBits;
	    _bitSize = newSize;
	}
    }

    public BitArray cloneArray() {
	return(new BitArray(_intSize, _bits));
    }

    public void writeExternal(ObjectOutput out) throws IOException {
	out.writeInt(_bitSize);
	out.writeInt(_mask);
	out.writeObject(_bits);
	out.flush();
    }

    /**
     * Read the whole tree from a file (serialized)
     */
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	_bitSize = in.readInt();
	_intSize = (_bitSize >>> 5) + 1;
	_mask    = in.readInt();
	_bits    = (int[])in.readObject();
    }

}


    
