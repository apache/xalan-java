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

import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.ReferenceType;
import de.fub.bytecode.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class WithParam extends Instruction {
    private QName _name;
    private Expression _select;

    public void display(int indent) {
	indent(indent);
	Util.println("with-param " + _name);
	if (_select != null) {
	    indent(indent + IndentIncrement);
	    Util.println("select " + _select.toString());
	}
	displayContents(indent + IndentIncrement);
    }

    public void parseContents(Parser parser) {
	final String name = getAttribute("name");
	if (name.length() > 0) {
	    _name = parser.getQName(name);
	}
        else {
	    reportError(this, parser, ErrorMsg.NREQATTR_ERR, "name");
        }
	
	final String select = getAttribute("select");
	if (select.length() > 0) {
	    _select = parser.parseExpression(this, "select", null);
	}
	
	parseChildren(parser);
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	if (_select != null) {
	    final Type tselect = _select.typeCheck(stable);
	    if (tselect instanceof ReferenceType == false) {
		_select = new CastExpr(_select, Type.Reference);
	    }
	}
	else {
	    typeCheckContents(stable);
	}
	return Type.Void;
    }

    public void compileResultTree(ClassGenerator classGen,
				  MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	// Save the current handler base on the stack
	il.append(methodGen.loadHandler());

	// Create new instance of DOM class (with 64 nodes)
	int index = cpg.addMethodref(DOM_IMPL, "<init>", "(I)V");
	il.append(new NEW(cpg.addClass(DOM_IMPL)));
	il.append(DUP);
	il.append(DUP);
	il.append(new PUSH(cpg, 64));
	il.append(new INVOKESPECIAL(index));

	// Overwrite old handler with DOM handler
	index = cpg.addMethodref(DOM_IMPL,
				 "getOutputDomBuilder",
				 "()" + TRANSLET_OUTPUT_SIG);
	il.append(new INVOKEVIRTUAL(index));
	il.append(DUP);
	il.append(methodGen.storeHandler());

	// Call startDocument on the new handler
	il.append(methodGen.startDocument());

	// Instantiate result tree fragment
	translateContents(classGen, methodGen);

	// Call endDocument on the new handler
	il.append(methodGen.loadHandler());
	il.append(methodGen.endDocument());

	// Restore old handler base from stack
	il.append(SWAP);
	il.append(methodGen.storeHandler());
    }
    
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	il.append(classGen.loadTranslet());
	il.append(new PUSH(cpg, _name.getLocalPart()));	// TODO: namespace ?

	if (_select == null) {
	    if (hasContents()) {
		compileResultTree(classGen, methodGen);
	    }
	    else {
		il.append(new PUSH(cpg,""));
	    }
	}
	else {
	    _select.translate(classGen, methodGen);
	    _select.startResetIterator(classGen, methodGen);
	}
	il.append(new INVOKEVIRTUAL(cpg.addMethodref(TRANSLET_CLASS,
						     ADD_PARAMETER,
						     ADD_PARAMETER_SIG)));
	il.append(POP);
    }
}
