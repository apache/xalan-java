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
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.InstructionList;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

/**
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 */
final class ProcessingInstruction extends Instruction {

    private AttributeValue _name; // name treated as AVT (7.1.3)
    
    public void parseContents(Parser parser) {
	final String name  = getAttribute("name");
	_name = AttributeValue.create(this, name, parser);
	if (name.equals("xml")) {
	    reportError(this, parser, ErrorMsg.ILLEGAL_PI_ERR, "xml");
	}
	parseChildren(parser);
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	_name.typeCheck(stable);
	typeCheckContents(stable);
	return Type.Void;
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	// Save the current handler base on the stack
	il.append(methodGen.loadHandler());
	il.append(DUP);		// first arg to "attributes" call
	
	// push attribute name
	_name.translate(classGen, methodGen);// 2nd arg

	il.append(classGen.loadTranslet());
	il.append(new GETFIELD(cpg.addFieldref(TRANSLET_CLASS,
					       "stringValueHandler",
					       STRING_VALUE_HANDLER_SIG)));
	il.append(DUP);
	il.append(methodGen.storeHandler());

	// translate contents with substituted handler
	translateContents(classGen, methodGen);

	// get String out of the handler
	il.append(new INVOKEVIRTUAL(cpg.addMethodref(STRING_VALUE_HANDLER,
						     "getValueOfPI",
						     "()" + STRING_SIG)));
	// call "processingInstruction"
	final int processingInstruction =
	    cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE,
				      "processingInstruction", 
				      "(" + STRING_SIG + STRING_SIG + ")V");
	il.append(new INVOKEINTERFACE(processingInstruction, 3));
	// Restore old handler base from stack
	il.append(methodGen.storeHandler());
    }
}
