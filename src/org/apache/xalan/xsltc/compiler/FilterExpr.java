/*
 * @(#)$Id$
 *
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.NEW;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.NodeSetType;
import org.apache.xalan.xsltc.compiler.util.ReferenceType;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

class FilterExpr extends Expression {
    
    /**
     * Primary expression of this filter. I.e., 'e' in '(e)[p1]...[pn]'.
     */
    private Expression   _primary;
    
    /**
     * Array of predicates in '(e)[p1]...[pn]'.
     */
    private final Vector _predicates;

    public FilterExpr(Expression primary, Vector predicates) {
	_primary = primary;
	_predicates = predicates;
	primary.setParent(this);
    }

    protected Expression getExpr() {
	if (_primary instanceof CastExpr)
	    return ((CastExpr)_primary).getExpr();
	else
	    return _primary;
    }

    public void setParser(Parser parser) {
	super.setParser(parser);
	_primary.setParser(parser);
	if (_predicates != null) {
	    final int n = _predicates.size();
	    for (int i = 0; i < n; i++) {
		final Expression exp = (Expression)_predicates.elementAt(i);
		exp.setParser(parser);
		exp.setParent(this);
	    }
	}
    }
    
    public String toString() {
	return "filter-expr(" + _primary + ", " + _predicates + ")";
    }

    /**
     * Type check a FilterParentPath. If the filter is not a node-set add a 
     * cast to node-set only if it is of reference type. This type coercion 
     * is needed for expressions like $x where $x is a parameter reference.
     * All optimizations are turned off before type checking underlying
     * predicates.
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	Type ptype = _primary.typeCheck(stable);

	if (ptype instanceof NodeSetType == false) {
	    if (ptype instanceof ReferenceType)  {
		_primary = new CastExpr(_primary, Type.NodeSet);
	    }
	    else {
		throw new TypeCheckError(this);
	    }
	}

        // Type check predicates and turn all optimizations off
	int n = _predicates.size();
	for (int i = 0; i < n; i++) {
	    Predicate pred = (Predicate) _predicates.elementAt(i);
            pred.dontOptimize();
	    pred.typeCheck(stable);
	}
	return _type = Type.NodeSet;	
    }
	
    /**
     * Translate a filter expression by pushing the appropriate iterator
     * onto the stack.
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	if (_predicates.size() > 0) {
	    translatePredicates(classGen, methodGen);
	}
	else {
	    _primary.translate(classGen, methodGen);
	}
    }

    /**
     * Translate a sequence of predicates. Each predicate is translated 
     * by constructing an instance of <code>CurrentNodeListIterator</code> 
     * which is initialized from another iterator (recursive call), a 
     * filter and a closure (call to translate on the predicate) and "this". 
     */
    public void translatePredicates(ClassGenerator classGen,
				    MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

        // If not predicates left, translate primary expression
	if (_predicates.size() == 0) {
	    translate(classGen, methodGen);
	}
	else {
            // Translate predicates from right to left
	    final int initCNLI = cpg.addMethodref(CURRENT_NODE_LIST_ITERATOR,
						  "<init>",
						  "("+NODE_ITERATOR_SIG+"Z"+
						  CURRENT_NODE_LIST_FILTER_SIG +
						  NODE_SIG+TRANSLET_SIG+")V");

	    Predicate predicate = (Predicate)_predicates.lastElement();
	    _predicates.remove(predicate);

            // Create a CurrentNodeListIterator
            il.append(new NEW(cpg.addClass(CURRENT_NODE_LIST_ITERATOR)));
            il.append(DUP);
            
            // Translate the rest of the predicates from right to left
            translatePredicates(classGen, methodGen);
            
            // Initialize CurrentNodeListIterator
            il.append(ICONST_1);
            predicate.translate(classGen, methodGen);
            il.append(methodGen.loadCurrentNode());
            il.append(classGen.loadTranslet());
            il.append(new INVOKESPECIAL(initCNLI));
	}
    }
}
