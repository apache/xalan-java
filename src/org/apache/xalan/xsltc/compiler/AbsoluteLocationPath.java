/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id$
 */

package org.apache.xalan.xsltc.compiler;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.NEW;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.NodeType;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

/**
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 */
final class AbsoluteLocationPath extends Expression {
    private Expression _path;	// may be null 

    public AbsoluteLocationPath() {
	_path = null;
    }

    public AbsoluteLocationPath(Expression path) {
	_path = path;
	if (path != null) {
	    _path.setParent(this);
	}
    }

    public void setParser(Parser parser) {
	super.setParser(parser);
	if (_path != null) {
	    _path.setParser(parser);
	}
    }

    public Expression getPath() {
	return(_path);
    }
    
    public String toString() {
	return "AbsoluteLocationPath(" +
	    (_path != null ? _path.toString() : "null") + ')';
    }
	
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	if (_path != null) {
	    final Type ptype = _path.typeCheck(stable);
	    if (ptype instanceof NodeType) {		// promote to node-set
		_path = new CastExpr(_path, Type.NodeSet);
	    }
	}
	return _type = Type.NodeSet;	
    }
	
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	if (_path != null) {
	    final int initAI = cpg.addMethodref(ABSOLUTE_ITERATOR,
						"<init>",
						"("
						+ NODE_ITERATOR_SIG
						+ ")V");
	    // Create new AbsoluteIterator
	    il.append(new NEW(cpg.addClass(ABSOLUTE_ITERATOR)));
	    il.append(DUP);

	    // Compile relative path iterator(s)
	    _path.translate(classGen, methodGen);

	    // Initialize AbsoluteIterator with iterator from the stack
	    il.append(new INVOKESPECIAL(initAI));
	}
	else {
	    final int gitr = cpg.addInterfaceMethodref(DOM_INTF,
						       "getIterator",
						       "()"+NODE_ITERATOR_SIG);
	    il.append(methodGen.loadDOM());
	    il.append(new INVOKEINTERFACE(gitr, 1));
	}
    }
}
