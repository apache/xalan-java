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

import de.fub.bytecode.generic.Type;
import de.fub.bytecode.generic.*;
import org.apache.xalan.xsltc.compiler.Template;

public class MethodGenerator extends MethodGen
    implements org.apache.xalan.xsltc.compiler.Constants {
    protected static final int INVALID_INDEX   = -1;
    
    private static final String START_ELEMENT_SIG   
	= "(" + STRING_SIG + ")V";
    private static final String END_ELEMENT_SIG     
	= START_ELEMENT_SIG;
    
    private InstructionList _mapTypeSub;
	
    private static final int DOM_INDEX       = 1;
    private static final int ITERATOR_INDEX  = 2;
    private static final int HANDLER_INDEX   = 3;

    private Instruction       _iloadCurrent;
    private Instruction       _istoreCurrent;
    private final Instruction _astoreHandler;
    private final Instruction _aloadHandler;
    private final Instruction _astoreIterator;
    private final Instruction _aloadIterator;
    private final Instruction _aloadDom;
    
    private final Instruction _startElement;
    private final Instruction _endElement;
    private final Instruction _startDocument;
    private final Instruction _endDocument;
    private final Instruction _attribute;

    private final Instruction _setStartNode;
    private final Instruction _nextNode;

    private SlotAllocator _slotAllocator;
    
    public MethodGenerator(int access_flags, Type return_type,
			   Type[] arg_types, String[] arg_names,
			   String method_name, String class_name,
			   InstructionList il, ConstantPoolGen cpg) {
	super(access_flags, return_type, arg_types, arg_names, method_name, 
	      class_name, il, cpg);
	
	_astoreHandler  = new ASTORE(HANDLER_INDEX);
	_aloadHandler   = new ALOAD(HANDLER_INDEX);
	_astoreIterator = new ASTORE(ITERATOR_INDEX);
	_aloadIterator  = new ALOAD(ITERATOR_INDEX);
	_aloadDom       = new ALOAD(DOM_INDEX);
	
	final int startElement =
	    cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE,
				      "startElement",
				      START_ELEMENT_SIG);
	_startElement = new INVOKEINTERFACE(startElement, 2);
	
	final int endElement =
	    cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE,
				      "endElement",
				      END_ELEMENT_SIG);
	_endElement = new INVOKEINTERFACE(endElement, 2);

	final int attribute =
	    cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE,
				      "attribute", 
				      "("
				      + STRING_SIG
				      + STRING_SIG
				      + ")V");
	_attribute = new INVOKEINTERFACE(attribute, 3);
	
	int index = cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE,
					      "startDocument",
					      "()V");
	_startDocument = new INVOKEINTERFACE(index, 1);
	
	index = cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE,
					  "endDocument",
					  "()V");
	_endDocument = new INVOKEINTERFACE(index, 1);
	
	
	index = cpg.addInterfaceMethodref(NODE_ITERATOR,
					  SET_START_NODE,
					  SET_START_NODE_SIG);
	_setStartNode = new INVOKEINTERFACE(index, 2);
	
	index = cpg.addInterfaceMethodref(NODE_ITERATOR, NEXT, NEXT_SIG);
	_nextNode = new INVOKEINTERFACE(index, 1);
	
	_slotAllocator = new SlotAllocator();
	_slotAllocator.initialize(getLocalVariables());
    }

    public LocalVariableGen addLocalVariable(String name, Type type,
					     InstructionHandle start,
					     InstructionHandle end) {
	
	//System.out.println("OLD addLocalVariable " + name);
	return super.addLocalVariable(name, type, start, end);
    }
    
    public LocalVariableGen addLocalVariable2(String name, Type type,
					      InstructionHandle start) {
	
	//System.out.println("addLocalVariable " + name);
	
	return super.addLocalVariable(name, type,
				      _slotAllocator.allocateSlot(type),
				      start, null);
    }

    public void removeLocalVariable(LocalVariableGen lvg) {
	//final int slot = lvg.getIndex();
	//System.out.println("removing local " + slot);
	_slotAllocator.releaseSlot(lvg);
	super.removeLocalVariable(lvg);
    }

    /**
     * Returns an instruction list that defines the map type subroutine. 
     * This subroutine is added to a method only when needed.
     */
    /*
    private InstructionList compileMapTypeSub(ClassGenerator classGen) {
	final InstructionList il = new InstructionList();
	final ConstantPoolGen cpg = classGen.getConstantPool();

	final LocalVariableGen returnAddress = 
	    addLocalVariable2("returnAddress", 
			      de.fub.bytecode.generic.Type.OBJECT,
			      il.getEnd());
	final LocalVariableGen node = 
	    addLocalVariable2("node",
			      de.fub.bytecode.generic.Type.INT,
			      il.getEnd());
	il.append(new ASTORE(returnAddress.getIndex()));
	il.append(new ISTORE(node.getIndex()));
	il.append(classGen.aloadTranslet());
	il.append(new GETFIELD(cpg.addFieldref(TRANSLET_CLASS,
					       MAP_FIELD,
					       MAP_FIELD_SIG)));
	il.append(classGen.aloadTranslet());
	il.append(new GETFIELD(cpg.addFieldref(TRANSLET_CLASS,
					       TYPE_FIELD,
					       TYPE_FIELD_SIG)));
	il.append(new ILOAD(node.getIndex()));
	il.append(InstructionConstants.SALOAD);
	il.append(InstructionConstants.SALOAD);
	il.append(new RET(returnAddress.getIndex()));
	//!!! until more sophisticated allocator
	//removeLocalVariable(returnAddress);
	//removeLocalVariable(node);
	return il;
    }
    */

    /**
     * Add a map type subroutine if needed. Must be called before
     * adding this method to a class.
     */
    /*
    public void addMapTypeSub() {
	if (_mapTypeSub != null) {
	    getInstructionList().append(_mapTypeSub);
	}
    }
    */

    /**
     * Returns a JSR instruction that jumps to the map type subroutine. 
     * This subroutine is compiled if needed but not added to the inst 
     * stream until addMapTypeSub() is called.
     */
    /*
    public JSR getMapTypeSub(ClassGenerator classGen) {
	if (_mapTypeSub == null) {
	    _mapTypeSub = compileMapTypeSub(classGen);
	}
	return new JSR(_mapTypeSub.getStart());
    }
    */

    public Instruction loadDOM() {
	return _aloadDom;
    }
    
    public Instruction storeHandler() {
	return _astoreHandler;
    }

    public Instruction loadHandler() {
	return _aloadHandler;
    }

    public Instruction storeIterator() {
	return _astoreIterator;
    }
    
    public Instruction loadIterator() {
	return _aloadIterator;
    }
    
    public final Instruction setStartNode() {
	return _setStartNode;
    }
    
    public final Instruction nextNode() {
	return _nextNode;
    }
    
    public final Instruction startElement() {
	return _startElement;
    }

    public final Instruction endElement() {
	return _endElement;
    }

    public final Instruction startDocument() {
	return _startDocument;
    }

    public final Instruction endDocument() {
	return _endDocument;
    }

    public final Instruction attribute() {
	return _attribute;
    }

    public Instruction loadCurrentNode() {
	return _iloadCurrent != null
	    ? _iloadCurrent
	    : (_iloadCurrent = new ILOAD(getLocalIndex("current")));
    }

    public Instruction storeCurrentNode() {
	return _istoreCurrent != null
	    ? _istoreCurrent
	    : (_istoreCurrent = new ISTORE(getLocalIndex("current")));
    }

    /** by default context node is the same as current node. MK437 */
    public Instruction loadContextNode() {
	return loadCurrentNode();
    }

    public int getLocalIndex(String name) {
	return getLocalVariable(name).getIndex();
    }

    public LocalVariableGen getLocalVariable(String name) {
	//	System.out.println("getLocalVariable " + name);
	
	final LocalVariableGen[] vars = getLocalVariables();
	for (int i = 0; i < vars.length; i++)
	    if (vars[i].getName().equals(name))
		return vars[i];
	return null;	
    }

    public void setMaxLocals() {
	
	// Get the current number of local variable slots
	int maxLocals = super.getMaxLocals();
	int prevLocals = maxLocals;

	// Get numer of actual variables
	final LocalVariableGen[] localVars = super.getLocalVariables();
	if (localVars != null) {
	    if (localVars.length > maxLocals)
		maxLocals = localVars.length;
	}

	// We want at least 5 local variable slots (for parameters)
	if (maxLocals < 5) maxLocals = 5;

	super.setMaxLocals(maxLocals);
    }
}
