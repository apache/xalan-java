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

package org.apache.xalan.xsltc.compiler;

import java.util.Vector;
import java.util.Enumeration;

import javax.xml.parsers.*;

import org.xml.sax.*;

import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.ReferenceType;
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class ForEach extends Instruction {

    private Expression _select;
    private Type       _type;

    public void display(int indent) {
	indent(indent);
	Util.println("ForEach");
	indent(indent + IndentIncrement);
	Util.println("select " + _select.toString());
	displayContents(indent + IndentIncrement);
    }
		
    public void parseContents(Parser parser) {
	_select = parser.parseExpression(this, "select", null);

	parseChildren(parser);

        // make sure required attribute(s) have been set
        if (_select.isDummy()) {
	    reportError(this, parser, ErrorMsg.REQUIRED_ATTR_ERR, "select");
	    return;
        }
    }
	
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	_type = _select.typeCheck(stable);

	if (_type instanceof ReferenceType || _type instanceof NodeType) {
	    _select = new CastExpr(_select, Type.NodeSet);
	    typeCheckContents(stable);
	    return Type.Void;
	}
	if (_type instanceof NodeSetType||_type instanceof ResultTreeType) {
	    typeCheckContents(stable);
	    return Type.Void;
	}
	throw new TypeCheckError(this);
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	// Save current node and current iterator on the stack
	il.append(methodGen.loadCurrentNode());
	il.append(methodGen.loadIterator());
		
	// Collect sort objects associated with this instruction
	final Vector sortObjects = new Vector();
	Enumeration children = elements();
	while (children.hasMoreElements()) {
	    final Object child = children.nextElement();
	    if (child instanceof Sort) {
		sortObjects.addElement(child);
	    }
	}

	if ((_type != null) && (_type instanceof ResultTreeType)) {
	    // Store existing DOM on stack - must be restored when loop is done
	    il.append(methodGen.loadDOM());

	    // <xsl:sort> cannot be applied to a result tree - issue warning
	    if (sortObjects.size() > 0) {
		ErrorMsg msg = new ErrorMsg(ErrorMsg.RESULT_TREE_SORT_ERR,this);
		getParser().reportError(WARNING, msg);
	    }

	    // Put the result tree on the stack (DOM)
	    _select.translate(classGen, methodGen);
	    // Get an iterator for the whole DOM - excluding the root node
	    _type.translateTo(classGen, methodGen, Type.NodeSet);
	    // Store the result tree as the default DOM
	    il.append(SWAP);
	    il.append(methodGen.storeDOM());
	}
	else {
	    // Compile node iterator
	    if (sortObjects.size() > 0) {
		Sort.translateSortIterator(classGen, methodGen,
					   _select, sortObjects);
	    }
	    else {
		_select.translate(classGen, methodGen);
	    }
	    if (!(_type instanceof ReferenceType)) {
		_select.startResetIterator(classGen, methodGen);
	    }
	}


	// Overwrite current iterator
	il.append(methodGen.storeIterator());

	// Give local variables (if any) default values before starting loop
	initializeVariables(classGen, methodGen);

	final BranchHandle nextNode = il.append(new GOTO(null));
	final InstructionHandle loop = il.append(NOP);

	translateContents(classGen, methodGen);
		    
	nextNode.setTarget(il.append(methodGen.loadIterator()));
	il.append(methodGen.nextNode());
	il.append(DUP);
	il.append(methodGen.storeCurrentNode());
	il.append(new IFNE(loop));

	// Restore current DOM (if result tree was used instead for this loop)
	if ((_type != null) && (_type instanceof ResultTreeType)) {
	    il.append(methodGen.storeDOM());	    
	}

	// Restore current node and current iterator from the stack
	il.append(methodGen.storeIterator());
	il.append(methodGen.storeCurrentNode());
    }

    /**
     * The code that is generated by nested for-each loops can appear to some
     * JVMs as if it is accessing un-initialized variables. We must add some
     * code that pushes the default variable value on the stack and pops it
     * into the variable slot. This is done by the Variable.initialize()
     * method. The code that we compile for this loop looks like this:
     *
     *           initialize iterator
     *           initialize variables <-- HERE!!!
     *           goto   Iterate
     *  Loop:    :
     *           : (code for <xsl:for-each> contents)
     *           :
     *  Iterate: node = iterator.next();
     *           if (node != END) goto Loop
     */
    public void initializeVariables(ClassGenerator classGen,
				   MethodGenerator methodGen) {
	final int n = elementCount();
	for (int i = 0; i < n; i++) {
	    final Object child = getContents().elementAt(i);
	    if (child instanceof Variable) {
		Variable var = (Variable)child;
		var.initialize(classGen, methodGen);
	    }
	}
    }

}
