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
 * @author Morten Jorgensen
 *
 */

package org.apache.xalan.xsltc.compiler;

import java.util.Vector;
import java.util.Enumeration;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class DocumentCall extends FunctionCall {

    private Expression _uri = null;
    private Expression _base = null;
    private Type       _uriType;

    /**
     * Default function call constructor
     */
    public DocumentCall(QName fname, Vector arguments) {
	super(fname, arguments);
    }

    /**
     * Type checks the arguments passed to the document() function. The first
     * argument can be any type (we must cast it to a string) and contains the
     * URI of the document
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	// At least one argument - two at most
	final int ac = argumentCount();
	if ((ac < 1) || (ac > 2)) {
	    ErrorMsg msg = new ErrorMsg(ErrorMsg.ILLEGAL_ARG_ERR, this);
	    throw new TypeCheckError(msg);
	}

	// Parse the first argument - the document URI
	_uri = argument(0);
	if (_uri instanceof LiteralExpr) {
	    LiteralExpr expr = (LiteralExpr)_uri;
	    if (expr.getValue().equals(EMPTYSTRING)) {
		Stylesheet stylesheet = getStylesheet();
		if (stylesheet == null) {
		    ErrorMsg msg = new ErrorMsg(ErrorMsg.ILLEGAL_ARG_ERR, this);
		    throw new TypeCheckError(msg);
		}
		_uri = new LiteralExpr(stylesheet.getSystemId(), EMPTYSTRING);
	    }
	}

	_uriType = _uri.typeCheck(stable);
	if ((_uriType != Type.NodeSet) && (_uriType != Type.String)) {
	    _uri = new CastExpr(_uri, Type.String);
	}

	// Parse the second argument - the document URI base
	if (ac == 2) {
	    _base = argument(1);
	    final Type baseType = _base.typeCheck(stable);
	    
	    if (baseType.identicalTo(Type.Node)) {
		_base = new CastExpr(_base, Type.NodeSet);
	    }
	    else if (baseType.identicalTo(Type.NodeSet)) {
		// falls through
	    }
	    else {
		ErrorMsg msg = new ErrorMsg(ErrorMsg.DOCUMENT_ARG_ERR, this);
		throw new TypeCheckError(msg);
	    }
	}

	return _type = Type.NodeSet;
    }
	
    /**
     * Translates the document() function call to a call to LoadDocument()'s
     * static method document().
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	final int domField = cpg.addFieldref(classGen.getClassName(),
					     DOM_FIELD,
					     DOM_INTF_SIG);
	final String docParamList =
	    "("+OBJECT_SIG+STRING_SIG+STRING_SIG+TRANSLET_SIG+DOM_INTF_SIG+")"+
	    NODE_ITERATOR_SIG;
	final int docIdx = cpg.addMethodref(LOAD_DOCUMENT_CLASS,
					    "document", docParamList);

	final int uriIdx = cpg.addInterfaceMethodref(DOM_INTF,
						     "getDocumentURI",
						     "(I)"+STRING_SIG);

	final int nextIdx = cpg.addInterfaceMethodref(NODE_ITERATOR,
						      NEXT, NEXT_SIG);

	// The URI can be either a node-set or something else cast to a string
	_uri.translate(classGen, methodGen);
	if (_uriType == Type.NodeSet)
	    _uri.startResetIterator(classGen, methodGen);

	// The base of the URI may be given as a second argument (a node-set)
	il.append(methodGen.loadDOM());
	if (_base != null) {
	    _base.translate(classGen, methodGen);
	    il.append(new INVOKEINTERFACE(nextIdx, 1));
	}
	else {
	     il.append(methodGen.loadContextNode());
	}
	il.append(new INVOKEINTERFACE(uriIdx, 2));
	il.append(new PUSH(cpg, getStylesheet().getSystemId()));

	// Feck the rest of the parameters on the stack
	il.append(classGen.loadTranslet());
	il.append(DUP);
	il.append(new GETFIELD(domField));
	il.append(new INVOKESTATIC(docIdx));
    }

}
