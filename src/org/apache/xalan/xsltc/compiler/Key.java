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
 * @author Santiago Pericas-Geertsen
 *
 */

package org.apache.xalan.xsltc.compiler;

import javax.xml.parsers.*;

import org.xml.sax.*;

import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;
import org.apache.xalan.xsltc.compiler.util.Type;

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.dom.Axis;

final class Key extends TopLevelElement {

    /**
     * The name of this key as defined in xsl:key.
     */
    private QName _name;

    /**
     * The pattern to match starting at the root node.
     */
    private Pattern _match;

    /**
     * The expression that generates the values for this key.
     */
    private Expression _use;

    /**
     * The type of the _use expression.
     */
    private Type _useType;

    /**
     * Parse the <xsl:key> element and attributes
     * @param parser A reference to the stylesheet parser
     */
    public void parse(CompilerContext ccontext) {
        final Parser parser = ccontext.getParser();

	// Get the required attributes and parser XPath expressions
	_name = parser.getQNameIgnoreDefaultNs(getAttribute("name"));
	_match = parser.parsePattern(this, "match", null);
	_use = parser.parseExpression(this, "use", null);

        // Make sure required attribute(s) have been set
        if (_name == null) {
	    reportError(this, parser, ErrorMsg.REQUIRED_ATTR_ERR, "name");
	    return;
        }
        if (_match.isDummy()) {
	    reportError(this, parser, ErrorMsg.REQUIRED_ATTR_ERR, "match");
	    return;
        }
        if (_use.isDummy()) {
	    reportError(this, parser, ErrorMsg.REQUIRED_ATTR_ERR, "use");
	    return;
        }
    }

    /**
     * Returns a String-representation of this key's name
     * @return The key's name (from the <xsl:key> elements 'name' attribute).
     */
    public String getName() {
	return _name.toString();
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	// Type check match pattern
	_match.typeCheck(stable);

	// Cast node values to string values (except for nodesets)
	_useType = _use.typeCheck(stable);
	if (_useType instanceof StringType == false &&
	    _useType instanceof NodeSetType == false)
	{
	    _use = new CastExpr(_use, Type.String);
	}

	return Type.Void;
    }

    /**
     * This method is called if the "use" attribute of the key contains a
     * node set. In this case we must traverse all nodes in the set and
     * create one entry in this key's index for each node in the set.
     */
    public void traverseNodeSet(ClassGenerator classGen,
				MethodGenerator methodGen,
				int buildKeyIndex) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	// DOM.getNodeValue(nodeIndex) => String
	final int getNodeValue = cpg.addInterfaceMethodref(DOM_INTF,
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
	il.append(new PUSH(cpg, _name.toString()));
	il.append(new ILOAD(parentNode.getIndex()));

	// Now get the node value and feck it on the parameter stack
	il.append(methodGen.loadDOM());
	il.append(methodGen.loadCurrentNode());
	il.append(new INVOKEINTERFACE(getNodeValue, 2));

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
					 "("+STRING_SIG+"I"+OBJECT_SIG+")V");

	// DOM.getAxisIterator(root) => NodeIterator
	final int git = cpg.addInterfaceMethodref(DOM_INTF,
						  "getAxisIterator",
						  "(I)"+NODE_ITERATOR_SIG);

	il.append(methodGen.loadCurrentNode());
	il.append(methodGen.loadIterator());

	// Get an iterator for all nodes in the DOM
	il.append(methodGen.loadDOM());
	il.append(new PUSH(cpg,Axis.DESCENDANT));
	il.append(new INVOKEINTERFACE(git, 2));

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

	// If this is a node-set we must go through each node in the set
	if (_useType instanceof NodeSetType) {
	    // Pass current node as parameter (we're indexing on that node)
	    il.append(methodGen.loadCurrentNode());
	    traverseNodeSet(classGen, methodGen, key);
	}
	else {
	    il.append(classGen.loadTranslet());
	    il.append(new PUSH(cpg, _name.toString()));
	    il.append(methodGen.loadCurrentNode());
	    _use.translate(classGen, methodGen);
	    il.append(new INVOKEVIRTUAL(key));
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
