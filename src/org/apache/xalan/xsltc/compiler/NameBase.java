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
 * @author Erwin Bolwidt <ejb@klomp.org>
 *
 */

package org.apache.xalan.xsltc.compiler;

import java.util.Vector;

import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

class NameBase extends FunctionCall {

    private Expression _param = null;
    private Type       _paramType = Type.Node;

    /**
     * Handles calls with no parameter (current node is implicit parameter).
     */
    public NameBase(QName fname) {
	super(fname);
    }

    /**
     * Handles calls with one parameter (either node or node-set).
     */
    public NameBase(QName fname, Vector arguments) {
	super(fname, arguments);
	_param = argument(0);
    }


    /**
     * Check that we either have no parameters or one parameter that is
     * either a node or a node-set.
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {

	// Check the argument type (if any)
	switch(argumentCount()) {
	case 0:
	    _paramType = Type.Node;
	    break;
	case 1:
	    _paramType = _param.typeCheck(stable);
	    break;
	default:
	    throw new TypeCheckError(this);
	}

	// The argument has to be a node, a node-set or a node reference
	if ((_paramType != Type.NodeSet) &&
	    (_paramType != Type.Node) &&
	    (_paramType != Type.Reference)) {
	    throw new TypeCheckError(this);
	}

	return (_type = Type.String);
    }

    public Type getType() {
	return _type;
    }

    /**
     * Translate the code required for getting the node for which the
     * QName, local-name or namespace URI should be extracted.
     */
    public void translate(ClassGenerator classGen,
			  MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	il.append(methodGen.loadDOM());
	
	// Function was called with no parameters
	if (argumentCount() == 0) {
	    il.append(methodGen.loadContextNode());
	}
	// Function was called with node parameter
	else if (_paramType == Type.Node) {
	    _param.translate(classGen, methodGen);
	}
	else if (_paramType == Type.Reference) {
	    _param.translate(classGen, methodGen);
	    il.append(new INVOKESTATIC(cpg.addMethodref
				       (BASIS_LIBRARY_CLASS,
					"referenceToNodeSet",
					"(Ljava/lang/Object;)" +
					"Lorg/apache/xalan/xsltc/" +
					"NodeIterator;")));
	    il.append(methodGen.nextNode());
	}
	// Function was called with node-set parameter
	else {
	    _param.translate(classGen, methodGen);
	    _param.startResetIterator(classGen, methodGen);
	    il.append(methodGen.nextNode());
	}
    }
}
