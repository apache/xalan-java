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

import javax.xml.parsers.*;

import org.xml.sax.*;

import de.fub.bytecode.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;
import org.apache.xalan.xsltc.compiler.util.Type;

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.dom.Axis;

final class Key extends TopLevelElement {
    private QName      _name;
    private Pattern    _match;
    private Expression _use;
    private Type       _useType;

    public void parseContents(Parser parser) {
	// make sure values are provided
	_name = parser.getQName(getAttribute("name"));
	_match = parser.parsePattern(this, "match", null);
	_use = parser.parseExpression(this, "use", null);

        // make sure required attribute(s) have been set
        if (_match.isDummy()) {
	    reportError(this, parser, ErrorMsg.NREQATTR_ERR, "match");
	    return;
        }
        if (_use.isDummy()) {
	    reportError(this, parser, ErrorMsg.NREQATTR_ERR, "use");
	    return;
        }
    }

    public Pattern getPattern() {
	return(_match);
    }

    public StepPattern getKernelPattern() {
	return(((LocationPathPattern)_match).getKernelPattern());
    }

    /**
     *
     */
    public String getName() {
	String name;
	if (_name.getPrefix() == null)
	    name = _name.getLocalPart();
	else
	    name = _name.getPrefix()+":"+_name.getLocalPart();
	return(name);
    }

    /**
     * Run type check on the "use" attribute and make sure it is something
     * we can use to extract some value from nodes.
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	_match.typeCheck(stable);
	_useType = _use.typeCheck(stable);
	// If the 'use' attribute is not a string...
	if (!(_useType instanceof StringType)) {

	    // ...it must hold an expression for a node...
	    if (_useType instanceof NodeType) {
		_use = new CastExpr(_use, Type.String);
	    }
	    // ...or a node-set.
	    else if (!(_useType instanceof NodeSetDTMType)) {
		throw new TypeCheckError(this);
	    }
	}
	return Type.Void;
    }

    /**
     * This method is called if the "use" attribute of the key contains a
     * node set. In this case we must traverse all nodes in the set and
     * create one entry in this key's index for each node in the set.
     */
    public void traverseNodeSetDTM(ClassGenerator classGen,
				MethodGenerator methodGen,
				int buildKeyIndex) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	// DOM.getNodeValue(nodeIndex) => String
	final int getNodeValue = cpg.addMethodref(classGen.getDOMClass(),
						  "getNodeValue",
						  "(I)"+STRING_SIG);

	// This variable holds the id of the node we found with the "match"
	// attribute of xsl:key. This is the id we store, with the value we
	// get from the nodes we find here, in the index for this key.
	final LocalVariableGen parentNode =
	    methodGen.addLocalVariable("parentNode",
				       Util.getJCRefType("I"),
				       il.getEnd(), null);

	// Get the 'parameter' from the stack and store it in a local var.
	il.append(new ISTORE(parentNode.getIndex()));

	// Save current node and current iterator on the stack
	il.append(methodGen.loadCurrentNode());
	il.append(methodGen.loadIterator());

	// Overwrite current iterator with one that gives us only what we want
	_use.translate(classGen, methodGen);
	_use.startResetIterator(classGen, methodGen);
	il.append(methodGen.storeIterator());

	final BranchHandle nextNode = il.append(new GOTO(null));
	final InstructionHandle loop = il.append(NOP);

	// Prepare to call buildKeyIndex(String name, int node, String value);
	il.append(classGen.loadTranslet());
	il.append(new PUSH(cpg, getName()));
	il.append(new ILOAD(parentNode.getIndex()));

	// Now get the node value and feck it on the parameter stack
	il.append(methodGen.loadDOM());
	il.append(methodGen.loadCurrentNode());
	il.append(new INVOKEVIRTUAL(getNodeValue));

	// Finally do the call to add an entry in the index for this key.
	il.append(new INVOKEVIRTUAL(buildKeyIndex));

	nextNode.setTarget(il.append(methodGen.loadIterator()));
	il.append(methodGen.nextNode());

	il.append(DUP);
	il.append(methodGen.storeCurrentNode());
	il.append(new IFNE(loop)); // Go on to next matching node....

	// Restore current node and current iterator from the stack
	il.append(methodGen.storeIterator());
	il.append(methodGen.storeCurrentNode());
    }

    /**
     * Gather all nodes that match the expression in the attribute "match"
     * and add one (or more) entries in this key's index.
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {

	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	final int current = methodGen.getLocalIndex("current");

	// AbstractTranslet.buildKeyIndex(name,node_id,value) => void
	final int key = cpg.addMethodref(TRANSLET_CLASS,
					 "buildKeyIndex",
					 "("+STRING_SIG+"I"+STRING_SIG+")V");

	// DOM.getAxisIterator(root) => NodeIterator
	final int git = cpg.addMethodref(classGen.getDOMClass(),
					 "getAxisIterator",
					 "(I)"+NODE_ITERATOR_SIG);

	// Save current node and current iterator on the stack
	il.append(methodGen.loadCurrentNode());
	il.append(methodGen.loadIterator());

	// Get an iterator for all nodes in the DOM
	il.append(methodGen.loadDOM());	
	il.append(new PUSH(cpg,Axis.DESCENDANT));
	il.append(new INVOKEVIRTUAL(git));

	// Reset the iterator to start with the root node
	il.append(methodGen.loadCurrentNode());
	il.append(methodGen.setStartNode());
	il.append(methodGen.storeIterator());

	// Loop for traversing all nodes in the DOM
	final BranchHandle nextNode = il.append(new GOTO(null));
	final InstructionHandle loop = il.append(NOP);

	// Check if the current node matches the pattern in "match"
	il.append(methodGen.loadCurrentNode());
	_match.translate(classGen, methodGen);
	_match.synthesize(classGen, methodGen); // Leaves 0 or 1 on stack
	final BranchHandle skipNode = il.append(new IFEQ(null));
	
	// If this is just a single node we should convert that to a string
	// and use that string as the value in the index for this key.
	if ((_useType instanceof NodeType) || (_useType instanceof StringType)) {
	    il.append(classGen.loadTranslet());
	    il.append(new PUSH(cpg, _name.toString()));
	    il.append(methodGen.loadCurrentNode());
	    _use.translate(classGen,methodGen);
	    il.append(new INVOKEVIRTUAL(key));
	}
	// If this is a node-set we must go through each node in the set
	// and create one entry in the key index for each node in the set.
	else {
	    // Pass current node as parameter (we're indexing on that node)
	    il.append(methodGen.loadCurrentNode());
	    traverseNodeSetDTM(classGen,methodGen,key);
	}
	
	// Get the next node from the iterator and do loop again...
	final InstructionHandle skip = il.append(NOP);
	
	il.append(methodGen.loadIterator());
	il.append(methodGen.nextNode());
	il.append(DUP);
	il.append(methodGen.storeCurrentNode());
	il.append(new IFNE(loop));

	// Restore current node and current iterator from the stack
	il.append(methodGen.storeIterator());
	il.append(methodGen.storeCurrentNode());
	
	nextNode.setTarget(skip);
	skipNode.setTarget(skip);
    }
}
