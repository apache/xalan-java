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

package org.apache.xalan.xsltc.compiler;

import java.util.Vector;

import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class KeyCall extends FunctionCall {

    private Expression _name;      // The name of this key
    private Expression _value;     // The value to look up in the key/index
    private Type       _valueType; // The value's data type
    private QName      _resolvedQName = null;

    /**
     * Get the parameters passed to function:
     *   key(String name, String value)
     *   key(String name, NodeSet value)
     * The 'arguments' vector should contain two parameters for key() calls,
     * one holding the key name and one holding the value(s) to look up. The
     * vector has only one parameter for id() calls (the key name is always
     * "##id" for id() calls).
     *
     * @param fname The function name (should be 'key' or 'id')
     * @param arguments A vector containing the arguments the the function
     */
    public KeyCall(QName fname, Vector arguments) {
	super(fname, arguments);
	switch(argumentCount()) {
	case 1:
	    _name = null;
	    _value = argument(0);
	    break;
	case 2:
	    _name = argument(0);
	    _value = argument(1);
	    break;
	default:
	    _name = _value = null;
	    break;
	}
    }

    /**
     * Type check the parameters for the id() or key() function.
     * The index name (for key() call only) must be a string or convertable
     * to a string, and the lookup-value must be a string or a node-set.
     * @param stable The parser's symbol table
     * @throws TypeCheckError When the parameters have illegal type
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	final Type returnType = super.typeCheck(stable);

	// Run type check on the key name (first argument) - must be a string,
	// and if it is not it must be converted to one using string() rules.
	if (_name != null) {
	    final Type nameType = _name.typeCheck(stable); 

	    if (_name instanceof LiteralExpr) {
		final LiteralExpr literal = (LiteralExpr) _name;
		_resolvedQName = 
		    getParser().getQNameIgnoreDefaultNs(literal.getValue());
	    }
	    else if (nameType instanceof StringType == false) {
		_name = new CastExpr(_name, Type.String);
	    }
	}

	// Run type check on the value for this key. This value can be of
	// any data type, so this should never cause any type-check errors.
	// If the value is not a node-set then it should be converted to a
	// string before the lookup is done. If the value is a node-set then
	// this process (convert to string, then do lookup) should be applied
	// to every node in the set, and the result from all lookups should
	// be added to the resulting node-set.
	_valueType = _value.typeCheck(stable);

	if ((_valueType != Type.NodeSet) &&
	    (_valueType != Type.ResultTree) &&
	    (_valueType != Type.String) &&
	    (_valueType != Type.Real) &&
	    (_valueType != Type.Int)) {
	    _value = new CastExpr(_value, Type.String);
	}

	return returnType;
    }

    /**
     * This method is called when the constructor is compiled in
     * Stylesheet.compileConstructor() and not as the syntax tree is traversed.
     * This method is a wrapper for the real translation method, which is
     * the private method translateCall() below. All this method does is to
     * wrap the KeyIndex that this function returns inside a duplicate filter.
     * The duplicate filter is used both to eliminate duplicates and to
     * cache the nodes in the index.
     * @param classGen The Java class generator
     * @param methodGen The method generator
     */
    public void translate(ClassGenerator classGen,
			  MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	// Wrap the KeyIndex (iterator) inside a duplicate filter iterator
	// to pre-read the indexed nodes and cache them.
	final int dupInit = cpg.addMethodref(DUP_FILTERED_ITERATOR,
					     "<init>",
					     "("+NODE_ITERATOR_SIG+")V");
	il.append(new NEW(cpg.addClass(DUP_FILTERED_ITERATOR)));
	il.append(DUP);
	translateCall(classGen, methodGen);
	il.append(new INVOKESPECIAL(dupInit));
    }

    /**
     * Translate the actual index lookup - leaves KeyIndex (iterator) on stack
     * @param classGen The Java class generator
     * @param methodGen The method generator
     */
    private void translateCall(ClassGenerator classGen,
			      MethodGenerator methodGen) {

	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	// Returns the string value for a node in the DOM
	final int getNodeValue = cpg.addInterfaceMethodref(DOM_INTF,
							   "getNodeValue",
							   "(I)"+STRING_SIG);

	// Returns the KeyIndex object of a given name
	final int getKeyIndex = cpg.addMethodref(TRANSLET_CLASS,
						 "getKeyIndex",
						 "(Ljava/lang/String;)"+
						 KEY_INDEX_SIG);

	// Initialises a KeyIndex to return nodes with specific values
	final int lookupId = cpg.addMethodref(KEY_INDEX_CLASS,
					      "lookupId",
					      "(Ljava/lang/Object;)V");
	final int lookupKey = cpg.addMethodref(KEY_INDEX_CLASS,
					       "lookupKey",
					       "(Ljava/lang/Object;)V");

	// Merges the nodes in two KeyIndex objects
	final int merge = cpg.addMethodref(KEY_INDEX_CLASS,
					   "merge",
					   "("+KEY_INDEX_SIG+")V");

	// Constructor for KeyIndex class
	final int indexConstructor = cpg.addMethodref(TRANSLET_CLASS,
						      "createKeyIndex",
						      "()"+KEY_INDEX_SIG);
	
	// This local variable holds the index/iterator we will return
	final LocalVariableGen returnIndex =
	    methodGen.addLocalVariable("returnIndex",
				       Util.getJCRefType(KEY_INDEX_SIG),
				       il.getEnd(), null);

	// This local variable holds the index we're using for search
	final LocalVariableGen searchIndex =
	    methodGen.addLocalVariable("searchIndex",
				       Util.getJCRefType(KEY_INDEX_SIG),
				       il.getEnd(), null);

	// If the second paramter is a node-set we need to go through each
	// node in the set, convert each one to a string and do a look up in
	// the named index, and then merge all the resulting node sets.
	if (_valueType == Type.NodeSet || _valueType == Type.ResultTree) {
	    // Save current node and current iterator on the stack
	    il.append(methodGen.loadCurrentNode());
	    il.append(methodGen.loadIterator());

	    // Get new iterator from 2nd parameter node-set & store in variable
	    _value.translate(classGen, methodGen);
	    _value.startResetIterator(classGen, methodGen);
	    il.append(methodGen.storeIterator());

	    // Create the KeyIndex object (the iterator) we'll return
	    il.append(classGen.loadTranslet());
	    il.append(new INVOKEVIRTUAL(indexConstructor));
	    il.append(new ASTORE(returnIndex.getIndex()));

	    // Initialise the index specified in the first parameter of key()
	    il.append(classGen.loadTranslet());
	    if (_name == null) {
		il.append(new PUSH(cpg,"##id"));
	    }
	    else if (_resolvedQName != null) {
		il.append(new PUSH(cpg, _resolvedQName.toString()));
	    }
	    else {
		_name.translate(classGen, methodGen);
	    }

	    il.append(new INVOKEVIRTUAL(getKeyIndex));
	    il.append(new ASTORE(searchIndex.getIndex()));

	    // LOOP STARTS HERE

	    // Now we're ready to start traversing the node-set given in
	    // the key() function's second argument....
	    final BranchHandle nextNode = il.append(new GOTO(null));
	    final InstructionHandle loop = il.append(NOP);

	    // Push returnIndex on stack to prepare for call to merge()
	    il.append(new ALOAD(returnIndex.getIndex()));
	    
	    // Lookup index using the string value from the current node
	    il.append(new ALOAD(searchIndex.getIndex()));
	    il.append(DUP);
	    il.append(methodGen.loadDOM());
	    il.append(methodGen.loadCurrentNode());
	    il.append(new INVOKEINTERFACE(getNodeValue, 2));
	    if (_name == null) {
		il.append(new INVOKEVIRTUAL(lookupId));
	    }
	    else {
		il.append(new INVOKEVIRTUAL(lookupKey));
	    }

	    // Call to returnIndex.merge(searchIndex);
	    il.append(new INVOKEVIRTUAL(merge));
		
	    // Go on with next node in the 2nd parameter node-set
	    nextNode.setTarget(il.append(methodGen.loadIterator()));
	    il.append(methodGen.nextNode());
	    il.append(DUP);
	    il.append(methodGen.storeCurrentNode());
	    il.append(new IFNE(loop));

	    // LOOP ENDS HERE

	    // Restore current node and current iterator from the stack
	    il.append(methodGen.storeIterator());
	    il.append(methodGen.storeCurrentNode());

	    // Return with the an iterator for all resulting nodes
	    il.append(new ALOAD(returnIndex.getIndex()));
	}
	// If the second parameter is a single value we just lookup the named
	// index and initialise the iterator to return nodes with this value.
	else {
	    // Call getKeyIndex in AbstractTranslet with the name of the key
	    // to get the index for this key (which is also a node iterator).
	    il.append(classGen.loadTranslet());
	    if (_name == null) {
		il.append(new PUSH(cpg,"##id"));
	    }
	    else if (_resolvedQName != null) {
		il.append(new PUSH(cpg, _resolvedQName.toString()));
	    }
	    else {
		_name.translate(classGen, methodGen);
	    }
	    il.append(new INVOKEVIRTUAL(getKeyIndex));

	    // Now use the value in the second argument to determine what nodes
	    // the iterator should return.
	    il.append(DUP);

	    if (_valueType == Type.Int || _valueType == Type.Real) {
		final int dbl = cpg.addMethodref(DOUBLE_CLASS,"<init>", "(D)V");
		il.append(new NEW(cpg.addClass(DOUBLE_CLASS)));
		il.append(DUP);
		_value.translate(classGen, methodGen);
		if (_valueType == Type.Int)
		    il.append(new I2D());
		il.append(new INVOKESPECIAL(dbl));
	    }
	    else {
		_value.translate(classGen, methodGen);
	    }

	    if (_name == null) {
		il.append(new INVOKEVIRTUAL(lookupId));
	    }
	    else {
		il.append(new INVOKEVIRTUAL(lookupKey));
	    }
	}
    }
}
