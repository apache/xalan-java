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
 * @author Erwin Bolwidt <ejb@klomp.org>
 *
 */

package org.apache.xalan.xsltc.compiler;

import org.apache.xalan.xsltc.compiler.util.Type;
import de.fub.bytecode.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class VariableRef extends VariableRefBase {

    private boolean _escaped;
	
    public VariableRef(Variable variable) {
	super(variable);
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	if ( (_variable.isLocal()) && (_escaped = isEscaped()) )
	    ((Variable)_variable).setEscapes();
	return super.typeCheck(stable);
    }

    private boolean isEscaped() {
	final SyntaxTreeNode limit = _variable.getParent();
	SyntaxTreeNode parent = getParent();
	do {
	    if (parent.isClosureBoundary()) {
		return true;
	    }
	    else {
		parent = parent.getParent();
	    }
	}
	while (parent != limit);
	return limit.isClosureBoundary();
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	// Fall-through for variables that are implemented as methods
	if (_type.implementedAsMethod()) return;

	final String name = _variable.getVariable();

	if (_variable.isLocal()) {
	    if (classGen.isExternal() || _escaped) {
		il.append(classGen.loadTranslet());
		final int sindex = ((Variable)_variable).getStackIndex();
		il.append(new PUSH(cpg, sindex));
		final int getVar = cpg.addMethodref(TRANSLET_CLASS, 
						    GET_VARIABLE,
						    GET_VARIABLE_SIG);
		il.append(new INVOKEVIRTUAL(getVar));
		_type.translateUnBox(classGen, methodGen);
	    }
	    else {
		il.append(_variable.loadInstruction());
		_variable.removeReference(this);
	    }
	}
	else {
	    final String signature = _type.toSignature();
	    final String className = classGen.getClassName();
	    il.append(classGen.loadTranslet());
	    // If inside a predicate we must cast this ref down
	    if (classGen.isExternal()) {
		il.append(new CHECKCAST(cpg.addClass(className)));
	    }
	    il.append(new GETFIELD(cpg.addFieldref(className,name,signature)));
	}

	if (_variable.getType() instanceof NodeSetType) {
	    final int reset = cpg.addInterfaceMethodref(NODE_ITERATOR,
							"reset",
							"()"+NODE_ITERATOR_SIG);
	    il.append(new INVOKEINTERFACE(reset,1));	    
	}

    }
}
