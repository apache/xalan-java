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

import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

class VariableRefBase extends Expression {

    /**
     * A reference to the associated variable.
     */
    protected final VariableBase _variable; 

    /**
     * A reference to the enclosing expression/instruction for which a
     * closure is needed (Predicate, Number or Sort).
     */
    protected Closure _closure = null;

    public VariableRefBase(VariableBase variable) {
	_variable = variable;
	variable.addReference(this);
    }

    public VariableRefBase() {
	_variable = null;
    }

    /**
     * Returns a reference to the associated variable
     */
    public VariableBase getVariable() {
	return _variable;
    }

    /**
     * Returns a reference to any parent variable
     */
    public VariableBase findParentVariable() {
	SyntaxTreeNode node = this;
	while (node != null && !(node instanceof VariableBase)) {
	    node = node.getParent();
	}
	return (VariableBase) node;
    }

    /**
     * Two variable references are deemed equal if they refer to the 
     * same variable.
     */
    public boolean equals(Object obj) {
	try {
	    return (_variable == ((VariableRefBase) obj)._variable);
	} 
	catch (ClassCastException e) {
	    return false;
	}
    }

    /**
     * Returns a string representation of this variable reference on the
     * format 'variable-ref(<var-name>)'.
     * @return Variable reference description
     */
    public String toString() {
	return "variable-ref("+_variable.getName()+'/'+_variable.getType()+')';
    }

    public Type typeCheck(SymbolTable stable) 
	throws TypeCheckError 
    {
	// Returned cached type if available
	if (_type != null) return _type;

	// Find nearest closure to add a variable reference
	if (_variable.isLocal()) {
	    SyntaxTreeNode node = getParent();
	    do {
		if (node instanceof Closure) {
		    _closure = (Closure) node;
		    break;
		}
		if (node instanceof TopLevelElement) {
		    break;	// way up in the tree
		}
		node = node.getParent();
	    } while (node != null);

	    if (_closure != null) {
		_closure.addVariable(this);
	    }
	}

	// Insert a dependency link from one variable to another
	VariableBase parent = findParentVariable();
	if (parent != null) parent.addDependency(_variable);

        // Attempt to get the cached variable type
        _type = _variable.getType();

        // If that does not work we must force a type-check (this is normally
        // only needed for globals in included/imported stylesheets
        if (_type == null) {
            _variable.typeCheck(stable);
            _type = _variable.getType();
        }

        // Return the type of the referenced variable
        return _type;
    }

}
