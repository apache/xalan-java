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

package org.apache.xalan.xsltc.compiler.util;

import java.util.Vector;

public final class MethodType extends Type {
    private final Type _resultType;	
    private final Vector _argsType;
	
    public MethodType(Type resultType) {
	_argsType = null;
	_resultType = resultType;
    }

    public MethodType(Type resultType, Type arg1) {
	if (arg1 != Type.Void) {
	    _argsType = new Vector();
	    _argsType.addElement(arg1);
	}
	else {
	    _argsType = null;
	}
	_resultType = resultType;
    }

    public MethodType(Type resultType, Type arg1, Type arg2) {
	_argsType = new Vector(2);
	_argsType.addElement(arg1);
	_argsType.addElement(arg2);
	_resultType = resultType;
    }

    public MethodType(Type resultType, Type arg1, Type arg2, Type arg3) {
	_argsType = new Vector(3);
	_argsType.addElement(arg1);
	_argsType.addElement(arg2);
	_argsType.addElement(arg3);
	_resultType = resultType;
    }

    public MethodType(Type resultType, Vector argsType) {
	_resultType = resultType;
	_argsType = argsType.size() > 0 ? argsType : null;
    }

    public String toString() {
	StringBuffer result = new StringBuffer("method{");
	if (_argsType != null) {
	    final int count = _argsType.size();
	    for (int i=0; i<count; i++) {
		result.append(_argsType.elementAt(i));
		if (i != (count-1)) result.append(',');
	    }
	}
	else {
	    result.append("void");
	}
	result.append('}');
	return result.toString();
    }

    public String toSignature() {
	return toSignature("");
    }

    /**
     * Returns the signature of this method that results by adding
     * <code>lastArgSig</code> to the end of the argument list.
     */
    public String toSignature(String lastArgSig) {
	final StringBuffer buffer = new StringBuffer();
	buffer.append('(');
	if (_argsType != null) {
	    final int n = _argsType.size();
	    for (int i = 0; i < n; i++) {
		buffer.append(((Type)_argsType.elementAt(i)).toSignature());
	    }
	}
	return buffer
	    .append(lastArgSig)
	    .append(')')
	    .append(_resultType.toSignature())
	    .toString();
    }

    public org.apache.bcel.generic.Type toJCType() {
	return null;	// should never be called
    }

    public boolean identicalTo(Type other) {
	boolean result = false;
	if (other instanceof MethodType) {
	    final MethodType temp = (MethodType) other;
	    if (_resultType.identicalTo(temp._resultType)) {
		final int len = argsCount();
		result = len == temp.argsCount();
		for (int i = 0; i < len && result; i++) {
		    final Type arg1 = (Type)_argsType.elementAt(i);
		    final Type arg2 = (Type)temp._argsType.elementAt(i);
		    result = arg1.identicalTo(arg2);
		}
	    }
	}
	return result;	
    }
	
    public int distanceTo(Type other) {
	int result = Integer.MAX_VALUE;
	if (other instanceof MethodType) {
	    final MethodType mtype = (MethodType) other;
	    if (_argsType != null) {
		final int len = _argsType.size();
		if (len == mtype._argsType.size()) {
		    result = 0;
		    for (int i = 0; i < len; i++) {
			Type arg1 = (Type) _argsType.elementAt(i);
			Type arg2 = (Type) mtype._argsType.elementAt(i);
			final int temp = arg1.distanceTo(arg2);
			if (temp == Integer.MAX_VALUE) {
			    result = temp;  // return MAX_VALUE
			    break;
			}
			else {
			    result += arg1.distanceTo(arg2);
			}
		    }
		}
	    }
	    else if (mtype._argsType == null) {
		result = 0;   // both methods have no args
	    }
	}
	return result;
    }
		
    public Type resultType() {
	return _resultType;
    }
		
    public Vector argsType() {
	return _argsType;
    }

    public int argsCount() {
	return _argsType == null ? 0 : _argsType.size();
    }
}
