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

package org.apache.xalan.xsltc.compiler;

import javax.xml.parsers.*;

import org.xml.sax.*;

import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.ReferenceType;
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class CopyOf extends Instruction {
    private Expression _select;
	
    public void display(int indent) {
	indent(indent);
	Util.println("CopyOf");
	indent(indent + IndentIncrement);
	Util.println("select " + _select.toString());
    }

    public void parseContents(Parser parser) {
	_select = parser.parseExpression(this, "select", null);
        // make sure required attribute(s) have been set
        if (_select.isDummy()) {
	    reportError(this, parser, ErrorMsg.REQUIRED_ATTR_ERR, "select");
	    return;
        }
    }
	
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	final Type tselect = _select.typeCheck(stable);
	if (tselect instanceof NodeType ||
	    tselect instanceof NodeSetType ||
	    tselect instanceof ReferenceType ||
	    tselect instanceof ResultTreeType) {
	    // falls through 
	}
	else {
	    _select = new CastExpr(_select, Type.String);
	}
	return Type.Void;
    }
	
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	final Type tselect = _select.getType();

	final String CPY1_SIG = "("+NODE_ITERATOR_SIG+TRANSLET_OUTPUT_SIG+")V";
	final int cpy1 = cpg.addInterfaceMethodref(DOM_INTF, "copy", CPY1_SIG);

	final String CPY2_SIG = "("+NODE_SIG+TRANSLET_OUTPUT_SIG+")V";
	final int cpy2 = cpg.addInterfaceMethodref(DOM_INTF, "copy", CPY2_SIG);

	if (tselect instanceof NodeSetType) {
	    il.append(methodGen.loadDOM());

	    // push NodeIterator
	    _select.translate(classGen, methodGen);	
	    _select.startResetIterator(classGen, methodGen);

	    // call copy from the DOM 'library'
	    il.append(methodGen.loadHandler());
	    il.append(new INVOKEINTERFACE(cpy1, 3));
	}
	else if (tselect instanceof NodeType) {
	    il.append(methodGen.loadDOM());
	    _select.translate(classGen, methodGen);	
	    il.append(methodGen.loadHandler());
	    il.append(new INVOKEINTERFACE(cpy2, 3));
	}
	else if (tselect instanceof ResultTreeType) {
	    _select.translate(classGen, methodGen);	
	    // We want the whole tree, so we start with the root node
	    il.append(ICONST_1);
	    il.append(methodGen.loadHandler());
	    il.append(new INVOKEINTERFACE(cpy2, 3));
	}
	else if (tselect instanceof ReferenceType) {
	    _select.translate(classGen, methodGen);
	    il.append(methodGen.loadHandler());
	    il.append(methodGen.loadCurrentNode());
	    il.append(methodGen.loadDOM());
	    final int copy = cpg.addMethodref(BASIS_LIBRARY_CLASS, "copy",
					      "(" 
					      + OBJECT_SIG  
					      + TRANSLET_OUTPUT_SIG 
					      + NODE_SIG
					      + DOM_INTF_SIG
					      + ")V");
	    il.append(new INVOKESTATIC(copy));
	}
	else {
	    il.append(classGen.loadTranslet());
	    _select.translate(classGen, methodGen);
	    il.append(methodGen.loadHandler());
	    il.append(new INVOKEVIRTUAL(cpg.addMethodref(TRANSLET_CLASS,
							 CHARACTERSW,
							 CHARACTERSW_SIG)));
	}

    }
}
