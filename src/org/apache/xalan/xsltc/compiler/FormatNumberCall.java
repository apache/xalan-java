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

import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class FormatNumberCall extends FunctionCall {
    private Expression _value;
    private Expression _format;
    private Expression _name;
    private QName      _resolvedQName = null;

    public FormatNumberCall(QName fname, Vector arguments) {
	super(fname, arguments);
	_value = argument(0);
	_format = argument(1);
	_name = argumentCount() == 3 ? argument(2) : null;
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {

	// Inform stylesheet to instantiate a DecimalFormat object
	getStylesheet().numberFormattingUsed();

	final Type tvalue = _value.typeCheck(stable);
	if (tvalue instanceof RealType == false) {
	    _value = new CastExpr(_value, Type.Real);
	}
	final Type tformat = _format.typeCheck(stable);
	if (tformat instanceof StringType == false) {
	    _format = new CastExpr(_format, Type.String);
	}
	if (argumentCount() == 3) {
	    final Type tname = _name.typeCheck(stable);

	    if (_name instanceof LiteralExpr) {
		final LiteralExpr literal = (LiteralExpr) _name;
		_resolvedQName = 
		    getParser().getQNameIgnoreDefaultNs(literal.getValue());
	    }
	    else if (tname instanceof StringType == false) {
		_name = new CastExpr(_name, Type.String);
	    }
	}
	return _type = Type.String;
    }
    
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	_value.translate(classGen, methodGen);
	_format.translate(classGen, methodGen);

	final int fn3arg = cpg.addMethodref(BASIS_LIBRARY_CLASS,
					    "formatNumber",
					    "(DLjava/lang/String;"+
					    "Ljava/text/DecimalFormat;)"+
					    "Ljava/lang/String;");
	final int get = cpg.addMethodref(TRANSLET_CLASS,
					 "getDecimalFormat",
					 "(Ljava/lang/String;)"+
					 "Ljava/text/DecimalFormat;");
	
	il.append(classGen.loadTranslet());
	if (_name == null) {
	    il.append(new PUSH(cpg, EMPTYSTRING));
	}
	else if (_resolvedQName != null) {
	    il.append(new PUSH(cpg, _resolvedQName.toString()));
	}
	else {
	    _name.translate(classGen, methodGen);
	}
	il.append(new INVOKEVIRTUAL(get));
	il.append(new INVOKESTATIC(fn3arg));
    }
}
