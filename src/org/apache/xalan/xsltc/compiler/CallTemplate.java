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
 * @author Erwin Bolwidt <ejb@klomp.org>
 *
 */

package org.apache.xalan.xsltc.compiler;

import javax.xml.parsers.*;

import org.xml.sax.*;

import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class CallTemplate extends Instruction {
    private QName _name;

    public void display(int indent) {
	indent(indent);
	System.out.print("CallTemplate");
	Util.println(" name " + _name);
	displayContents(indent + IndentIncrement);
    }
		
    public boolean hasWithParams() {
	return elementCount() > 0;
    }

    public void parseContents(Parser parser) {
	_name = parser.getQNameIgnoreDefaultNs(getAttribute("name"));
	parseChildren(parser);
    }
		
    /**
     * Verify that a template with this name exists.
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	final Template template = stable.lookupTemplate(_name);
	if (template != null) {
	    typeCheckContents(stable);
	}
	else {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.TEMPLATE_UNDEF_ERR,_name,this);
	    throw new TypeCheckError(err);
	}
	return Type.Void;
    }

    /**
     * Translate call-template.
     * A parameter frame is pushed only if some template in the stylesheet
     * uses parameters.
     * TODO: optimize by checking if the callee has parameters.
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final Stylesheet stylesheet = classGen.getStylesheet();
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();


	if (stylesheet.hasLocalParams() || hasContents()) {
	    // Push parameter frame
	    final int push = cpg.addMethodref(TRANSLET_CLASS, 
					      PUSH_PARAM_FRAME,
					      PUSH_PARAM_FRAME_SIG);
	    il.append(classGen.loadTranslet());
	    il.append(new INVOKEVIRTUAL(push));
	    // Translate with-params
	    translateContents(classGen, methodGen);
	}

	final String className = stylesheet.getClassName();
	// Generate a valid Java method name
	String methodName = Util.escape(_name.toString());

	il.append(classGen.loadTranslet());
	il.append(methodGen.loadDOM());
	il.append(methodGen.loadIterator());
	il.append(methodGen.loadHandler());
	il.append(methodGen.loadCurrentNode());
	il.append(new INVOKEVIRTUAL(cpg.addMethodref(className,
						     methodName,
						     "("
						     + DOM_INTF_SIG
						     + NODE_ITERATOR_SIG
						     + TRANSLET_OUTPUT_SIG
						     + NODE_SIG
						     +")V")));
	

	if (stylesheet.hasLocalParams() || hasContents()) {
	    // Pop parameter frame
	    final int pop = cpg.addMethodref(TRANSLET_CLASS,
					     POP_PARAM_FRAME,
					     POP_PARAM_FRAME_SIG);
	    il.append(classGen.loadTranslet());
	    il.append(new INVOKEVIRTUAL(pop));
	}
    }
} 
