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

import org.apache.bcel.generic.Type;
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.Template;

public final class MatchGenerator extends MethodGenerator {
    private static int CURRENT_INDEX = 1;

    private int _iteratorIndex = INVALID_INDEX;

    private final Instruction _iloadCurrent;
    private final Instruction _istoreCurrent;
    private Instruction _aloadDom;
    
    public MatchGenerator(int access_flags, Type return_type, 
			  Type[] arg_types, String[] arg_names, 
			  String method_name, String class_name,
			  InstructionList il, ConstantPoolGen cp) {
	super(access_flags, return_type, arg_types, arg_names, method_name, 
	      class_name, il, cp);
	
	_iloadCurrent = new ILOAD(CURRENT_INDEX);
	_istoreCurrent = new ISTORE(CURRENT_INDEX);
    }

    public Instruction loadCurrentNode() {
	return _iloadCurrent;
    }

    public Instruction storeCurrentNode() {
	return _istoreCurrent;
    }
    
    public int getHandlerIndex() {
	return INVALID_INDEX;		// not available
    }

    /**
     * Get index of the register where the DOM is stored.
     */
    public Instruction loadDOM() {
	return _aloadDom;
    }

    /**
     * Set index where the reference to the DOM is stored.
     */
    public void setDomIndex(int domIndex) {
	_aloadDom = new ALOAD(domIndex);
    }

    /**
     * Get index of the register where the current iterator is stored.
     */
    public int getIteratorIndex() {
	return _iteratorIndex;
    }

    /**
     * Set index of the register where the current iterator is stored.
     */
    public void setIteratorIndex(int iteratorIndex) {
	_iteratorIndex = iteratorIndex;
    }

    public int getLocalIndex(String name) {
	if (name.equals("current")) {
	    return CURRENT_INDEX;
	}
	return super.getLocalIndex(name);
    }
}
