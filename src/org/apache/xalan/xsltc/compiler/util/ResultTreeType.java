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

package org.apache.xalan.xsltc.compiler.util;

import org.apache.xalan.xsltc.DOM;

import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.FlowList;
import org.apache.xalan.xsltc.compiler.Constants;

public final class ResultTreeType extends Type {
    private final String _methodName;

    protected ResultTreeType() {
	_methodName = null;
    }

    public ResultTreeType(String methodName) {
	_methodName = methodName;
    }

    public String toString() {
	return "result-tree";
    }

    public boolean identicalTo(Type other) {
	return (other instanceof ResultTreeType);
    }

    public String toSignature() {
	return DOM_INTF_SIG;
    }

    public org.apache.bcel.generic.Type toJCType() {
	return Util.getJCRefType(toSignature());
    }

    public String getMethodName() {
	return _methodName;
    }

    public boolean implementedAsMethod() {
	return _methodName != null;
    }

    /**
     * Translates a result tree to object of internal type <code>type</code>. 
     * The translation to int is undefined since result trees
     * are always converted to reals in arithmetic expressions.
     *
     * @param classGen A BCEL class generator
     * @param methodGen A BCEL method generator
     * @param type An instance of the type to translate the result tree to
     * @see org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    Type type) {
	if (type == Type.String) {
	    translateTo(classGen, methodGen, (StringType)type);
	}
	else if (type == Type.Boolean) {
	    translateTo(classGen, methodGen, (BooleanType)type);
	}
	else if (type == Type.Real) {
	    translateTo(classGen, methodGen, (RealType)type);
	}
	else if (type == Type.NodeSet) {
	    translateTo(classGen, methodGen, (NodeSetType)type);
	}
	else if (type == Type.Reference) {
	    translateTo(classGen, methodGen, (ReferenceType)type);
	}
	else if (type == Type.Object) {
	    translateTo(classGen, methodGen, (ObjectType) type);
	}
	else {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.DATA_CONVERSION_ERR,
					toString(), type.toString());
	    classGen.getParser().reportError(Constants.FATAL, err);
	}
    }

    /**
     * Expects an result tree on the stack and pushes a boolean.
     * Translates a result tree to a boolean by first converting it to string.
     *
     * @param classGen A BCEL class generator
     * @param methodGen A BCEL method generator
     * @param type An instance of BooleanType (any)
     * @see org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    BooleanType type) {
	// A result tree is always 'true' when converted to a boolean value,
	// since the tree always has at least one node (the root).
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	il.append(POP);      // don't need the DOM reference
	il.append(ICONST_1); // push 'true' on the stack
    }

    /**
     * Expects an result tree on the stack and pushes a string.
     *
     * @param classGen A BCEL class generator
     * @param methodGen A BCEL method generator
     * @param type An instance of StringType (any)
     * @see org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    StringType type) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	
	if (_methodName == null) {
	    int index = cpg.addInterfaceMethodref(DOM_INTF,
						  "getStringValue", 
						  "()"+STRING_SIG);
	    il.append(new INVOKEINTERFACE(index, 1));
	}
	else {
	    final String className = classGen.getClassName();
	    final int current = methodGen.getLocalIndex("current");
	    
	    // Push required parameters 
	    il.append(classGen.loadTranslet());
	    if (classGen.isExternal()) {
		il.append(new CHECKCAST(cpg.addClass(className)));
	    }
	    il.append(DUP);
	    il.append(new GETFIELD(cpg.addFieldref(className, "_dom",
						   DOM_INTF_SIG)));

	    // Create a new instance of a StringValueHandler
	    int index = cpg.addMethodref(STRING_VALUE_HANDLER, "<init>", "()V");
	    il.append(new NEW(cpg.addClass(STRING_VALUE_HANDLER)));
	    il.append(DUP);
	    il.append(DUP);
	    il.append(new INVOKESPECIAL(index));
	    
	    // Store new Handler into a local variable
	    final LocalVariableGen handler =
		methodGen.addLocalVariable("rt_to_string_handler", 
					   Util.getJCRefType(STRING_VALUE_HANDLER_SIG),
					   null, null);
	    il.append(new ASTORE(handler.getIndex()));

	    // Call the method that implements this result tree
	    index = cpg.addMethodref(className, _methodName,
				     "("+DOM_INTF_SIG+TRANSLET_OUTPUT_SIG+")V");
	    il.append(new INVOKEVIRTUAL(index));
	    
	    // Restore new handler and call getValue()
	    il.append(new ALOAD(handler.getIndex()));
	    index = cpg.addMethodref(STRING_VALUE_HANDLER,
				     "getValue",
				     "()" + STRING_SIG);
	    il.append(new INVOKEVIRTUAL(index));
	}
    }

    /**
     * Expects an result tree on the stack and pushes a real.
     * Translates a result tree into a real by first converting it to string.
     *
     * @param classGen A BCEL class generator
     * @param methodGen A BCEL method generator
     * @param type An instance of RealType (any)
     * @see org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    RealType type) {
	translateTo(classGen, methodGen, Type.String);
	Type.String.translateTo(classGen, methodGen, Type.Real);	
    }

    /**
     * Expects a result tree on the stack and pushes a boxed result tree.
     * Result trees are already boxed so the translation is just a NOP.
     *
     * @param classGen A BCEL class generator
     * @param methodGen A BCEL method generator
     * @param type An instance of ReferenceType (any)
     * @see org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    ReferenceType type) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	if (_methodName == null) {
	    il.append(NOP);
	}
	else {
	    LocalVariableGen domBuilder, newDom;
	    final String className = classGen.getClassName();
	    final int current = methodGen.getLocalIndex("current");

	    // Push required parameters 
	    il.append(classGen.loadTranslet());
	    if (classGen.isExternal()) {
		il.append(new CHECKCAST(cpg.addClass(className)));
	    }
	    il.append(methodGen.loadDOM());

	    // Create new instance of DOM class (with 64 nodes)
	    int index = cpg.addMethodref(DOM_IMPL, "<init>", "(I)V");
	    il.append(new NEW(cpg.addClass(DOM_IMPL)));
	    il.append(DUP);
	    il.append(DUP);
	    il.append(new PUSH(cpg, 64));
	    il.append(new INVOKESPECIAL(index));
	    
	    // Store new DOM into a local variable
	    newDom = methodGen.addLocalVariable("rt_to_reference_dom", 
						Util.getJCRefType(DOM_INTF_SIG),
						null, null);
	    il.append(new CHECKCAST(cpg.addClass(DOM_INTF_SIG)));
	    il.append(new ASTORE(newDom.getIndex()));

	    // Overwrite old handler with DOM handler
	    index = cpg.addMethodref(DOM_IMPL,
				     "getOutputDomBuilder", 
				     "()" + TRANSLET_OUTPUT_SIG);
	    il.append(new INVOKEVIRTUAL(index));
	    il.append(DUP);
	    il.append(DUP);

	    // Store DOM handler in a local in order to call endDocument()
	    domBuilder =
		methodGen.addLocalVariable("rt_to_reference_handler", 
					   Util.getJCRefType(TRANSLET_OUTPUT_SIG),
					   null, null);
	    il.append(new ASTORE(domBuilder.getIndex()));

	    // Call startDocument on the new handler
	    index = cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE, 
					      "startDocument", "()V");
	    il.append(new INVOKEINTERFACE(index, 1));

	    // Call the method that implements this result tree
	    index = cpg.addMethodref(className,
				     _methodName,
				     "("
				     + DOM_INTF_SIG
				     + TRANSLET_OUTPUT_SIG
				     +")V");
	    il.append(new INVOKEVIRTUAL(index));

	    // Call endDocument on the DOM handler
	    il.append(new ALOAD(domBuilder.getIndex()));
	    index = cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE, 
					      "endDocument", "()V");
	    il.append(new INVOKEINTERFACE(index, 1));

	    // Push the new DOM on the stack
	    il.append(new ALOAD(newDom.getIndex()));
	}
    }

    /**
     * Expects a result tree on the stack and pushes a node-set (iterator).
     * Note that the produced iterator is an iterator for the DOM that
     * contains the result tree, and not the DOM that is currently in use.
     * This conversion here will therefore not directly work with elements
     * such as <xsl:apply-templates> and <xsl:for-each> without the DOM
     * parameter/variable being updates as well.
     *
     * @param classGen A BCEL class generator
     * @param methodGen A BCEL method generator
     * @param type An instance of NodeSetType (any)
     * @see org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    NodeSetType type) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	// Put an extra copy of the result tree (DOM) on the stack
	il.append(DUP);

	// DOM adapters containing a result tree are not initialised with
	// translet-type to DOM-type mapping. This must be done now for
	// XPath expressions and patterns to work for the iterator we create.
	il.append(classGen.loadTranslet()); // get names array
	il.append(new GETFIELD(cpg.addFieldref(TRANSLET_CLASS,
					       NAMES_INDEX,
					       NAMES_INDEX_SIG)));
	il.append(classGen.loadTranslet()); // get namespaces array
	il.append(new GETFIELD(cpg.addFieldref(TRANSLET_CLASS,
					       NAMESPACE_INDEX,
					       NAMESPACE_INDEX_SIG)));
	// Pass the type mappings to the DOM adapter
	final int mapping = cpg.addInterfaceMethodref(DOM_INTF,
						      "setupMapping",
						      "(["+STRING_SIG+
						      "["+STRING_SIG+")V");
	il.append(new INVOKEINTERFACE(mapping, 3));
	il.append(DUP);

	// Create an iterator with all the nodes in the DOM adapter
	final int iter = cpg.addInterfaceMethodref(DOM_INTF,
						   "getChildren",
						   "(I)"+NODE_ITERATOR_SIG);
	il.append(new PUSH(cpg, DOM.ROOTNODE));
	il.append(new INVOKEINTERFACE(iter, 2));
    }

    /**
     * Subsume result tree into ObjectType.
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    ObjectType type) {
	methodGen.getInstructionList().append(NOP);	
    }

    /**
     * Translates a result tree into a non-synthesized boolean.
     * It does not push a 0 or a 1 but instead returns branchhandle list
     * to be appended to the false list.
     *
     * @param classGen A BCEL class generator
     * @param methodGen A BCEL method generator
     * @param type An instance of BooleanType (any)
     * @see org.apache.xalan.xsltc.compiler.util.Type#translateToDesynthesized
     */
    public FlowList translateToDesynthesized(ClassGenerator classGen, 
					     MethodGenerator methodGen, 
					     BooleanType type) {
	final InstructionList il = methodGen.getInstructionList();
	translateTo(classGen, methodGen, Type.Boolean);
	return new FlowList(il.append(new IFEQ(null)));
    }

    /**
     * Translates a result tree to a Java type denoted by <code>clazz</code>. 
     * Expects a result tree on the stack and pushes an object
     * of the appropriate type after coercion. Result trees are translated
     * to W3C Node or W3C NodeList and the translation is done
     * via node-set type.
     *
     * @param classGen A BCEL class generator
     * @param methodGen A BCEL method generator
     * @param type An reference to the Class to translate to
     * @see org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    Class clazz) {
	final String className = clazz.getName();
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	if (className.equals("org.w3c.dom.Node")) {
	    translateTo(classGen, methodGen, Type.NodeSet);
	    int index = cpg.addInterfaceMethodref(DOM_INTF,
						  MAKE_NODE,
						  MAKE_NODE_SIG2);
	    il.append(new INVOKEINTERFACE(index, 2));
	}
	else if (className.equals("org.w3c.dom.NodeList")) {
	    translateTo(classGen, methodGen, Type.NodeSet);
	    int index = cpg.addInterfaceMethodref(DOM_INTF,
						  MAKE_NODE_LIST,
						  MAKE_NODE_LIST_SIG2);
	    il.append(new INVOKEINTERFACE(index, 2));
	}
	else if (className.equals("java.lang.Object")) {
	    il.append(NOP);
	}
	else {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.DATA_CONVERSION_ERR,
					toString(), className);
	    classGen.getParser().reportError(Constants.FATAL, err);
	}
    }

    /**
     * Translates an object of this type to its boxed representation.
     */ 
    public void translateBox(ClassGenerator classGen,
			     MethodGenerator methodGen) {
	translateTo(classGen, methodGen, Type.Reference);
    }

    /**
     * Translates an object of this type to its unboxed representation.
     */ 
    public void translateUnBox(ClassGenerator classGen,
			       MethodGenerator methodGen) {
	methodGen.getInstructionList().append(NOP);
    }

    /**
     * Returns the class name of an internal type's external representation.
     */
    public String getClassName() {
	return(DOM_INTF);
    }

    public Instruction LOAD(int slot) {
	return new ALOAD(slot);
    }
	
    public Instruction STORE(int slot) {
	return new ASTORE(slot);
    }
}
