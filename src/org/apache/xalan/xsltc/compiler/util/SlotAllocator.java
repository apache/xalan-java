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
 *
 */

package org.apache.xalan.xsltc.compiler.util;

import de.fub.bytecode.generic.Type;
import de.fub.bytecode.generic.*;

final class SlotAllocator {
    private int   _firstAvailableSlot;
    private int   _size = 8;
    private int   _free = 0;
    private int[] _slotsTaken = new int[_size];

    /*
    private static int Serial = 0;
    private final int _serial = Serial++;
    

    private void printState(String msg) {
	System.out.println("=========== " + _serial + " =========== " + msg);
	System.out.println("firstAvailableSlot = " + _firstAvailableSlot);
	
	for (int i = 0; i < _free; i++) {
	    System.out.println("\tslotsTaken = " + _slotsTaken[i]);
	}
	
	System.out.println("========================");
    }
    */
    
    public void initialize(LocalVariableGen[] vars) {
	final int length = vars.length;
	//System.out.println(_serial + " initialize " + length);
	int slot = 0;
	for (int i = 0; i < length; i++) {
	    //System.out.println("index " + vars[i].getIndex());
	    //System.out.println("all " + allocateSlot(vars[i].getType()));
	    //allocateSlot(vars[i].getType());
	    slot = Math.max(slot,
			    vars[i].getIndex() + vars[i].getType().getSize());
	}
	_firstAvailableSlot = slot;
	//System.out.println("firstAvailableSlot = " + _firstAvailableSlot);
    }

    public int allocateSlot(Type type) {
	final int size = type.getSize();
	final int limit = _free;
	int slot = _firstAvailableSlot, where = 0;

	//printState("allocating");

	if (_free + size > _size) {
	    final int[] array = new int[_size *= 2];
	    for (int j = 0; j < limit; j++) {
		array[j] = _slotsTaken[j];
	    }
	    _slotsTaken = array;
	}

	while (where < limit) {
	    if (slot + size <= _slotsTaken[where]) {
		// insert
		for (int j = limit - 1; j >= where; j--) {
		    _slotsTaken[j + size] = _slotsTaken[j];
		}
		break;
	    }
	    else {
		slot = _slotsTaken[where++] + 1;
	    }
	}
	
	for (int j = 0; j < size; j++) {
	    _slotsTaken[where + j] = slot + j;
	}
	
	_free += size;
	//System.out.println("allocated slot " + slot);
	//printState("done");
	return slot;
    }

    public void releaseSlot(LocalVariableGen lvg) {
	final int size = lvg.getType().getSize();
	final int slot = lvg.getIndex();
	final int limit = _free;
	
	//printState("releasing " + slot);
	for (int i = 0; i < limit; i++) {
	    if (_slotsTaken[i] == slot) {
		int j = i + size;
		while (j < limit) {
		    _slotsTaken[i++] = _slotsTaken[j++];
		}
		_free -= size;
		//System.out.println("released slot " + slot);
		
		//printState("done");
		return;
	    }
	}
	System.err.println("size = " + size);
	System.err.println("slot = " + slot);
	System.err.println("limit = " + limit);
	//printState("error");
	throw new Error("releaseSlot");
    }
}
