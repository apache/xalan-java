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

import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class UnresolvedRef extends VariableRefBase {

    private QName           _variableName = null;
    private VariableRefBase _ref = null;
    private VariableBase    _var = null;
    private Stylesheet      _sheet = null;

    public UnresolvedRef(QName name) {
	super();
	_variableName = name;
	_sheet = getStylesheet();
    }

    public QName getName() {
	return(_variableName);
    }

    private ErrorMsg reportError() {
	ErrorMsg err = new ErrorMsg(ErrorMsg.VARIABLE_UNDEF_ERR,
				    _variableName, this);
	getParser().reportError(Constants.ERROR, err);
	return(err);
    }

    private VariableRefBase resolve(Parser parser, SymbolTable stable) {
	// At this point the AST is already built and we should be able to
	// find any declared global variable or parameter
	VariableBase ref = parser.lookupVariable(_variableName);
	if (ref == null) ref = (VariableBase)stable.lookupName(_variableName);
	if (ref == null) {
	    reportError();
	    return null;
	}
	
	// Insert the referenced variable as something the parent variable
	// is dependent of (this class should only be used under variables)
	if ((_var = findParentVariable()) != null) _var.addDependency(ref);

	// Instanciate a true variable/parameter ref
	if (ref instanceof Variable)
	    return(new VariableRef((Variable)ref));
	else if (ref instanceof Param)
	    return(new ParameterRef((Param)ref));
	else
	    return null;
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	if (_ref != null) {
	    final String name = _variableName.toString();
	    ErrorMsg err = new ErrorMsg(ErrorMsg.CIRCULAR_VARIABLE_ERR,
					name, this);
	}
	if ((_ref = resolve(getParser(), stable)) != null) {
	    return (_type = _ref.typeCheck(stable));
	}
	throw new TypeCheckError(reportError());
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	if (_ref != null)
	    _ref.translate(classGen, methodGen);
	else
	    reportError();
    }

    public String toString() {
	return "unresolved-ref()";
    }

}
