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
 *
 */

package org.apache.xalan.xsltc.compiler;

import java.util.Vector;
import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.xml.parsers.*;

import org.xml.sax.*;

import org.apache.xalan.xsltc.compiler.util.Type;
import de.fub.bytecode.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class Copy extends Instruction {
    private UseAttributeSets _useSets;
    
    public void parseContents(Parser parser) {
	final String useSets = getAttribute("use-attribute-sets");
	if (useSets.length() > 0) {
	    _useSets = new UseAttributeSets(useSets, parser);
	}
	parseChildren(parser);
    }
    
    public void display(int indent) {
	indent(indent);
	Util.println("Copy");
	indent(indent + IndentIncrement);
	displayContents(indent + IndentIncrement);
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	if (_useSets != null) {
	    _useSets.typeCheck(stable);
	}
	typeCheckContents(stable);
	return Type.Void;
    }
	
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	final String DOM_CLASS = classGen.getDOMClass();
	final LocalVariableGen name =
	    methodGen.addLocalVariable2("name",
					Util.getJCRefType(STRING_SIG),
					il.getEnd());
	final LocalVariableGen length =
	    methodGen.addLocalVariable2("length",
					Util.getJCRefType("I"),
					il.getEnd());
	
	il.append(methodGen.loadDOM());
	il.append(methodGen.loadCurrentNode());
	il.append(methodGen.loadHandler());
	final int shallowCopy = cpg.addMethodref(DOM_CLASS,
						 "shallowCopy",
						 "("
						 + NODE_SIG
						 + TRANSLET_OUTPUT_SIG
						 + ")" + STRING_SIG); 
	il.append(new INVOKEVIRTUAL(shallowCopy));
	il.append(DUP);
	il.append(new ASTORE(name.getIndex()));
	final BranchHandle ifBlock1 = il.append(new IFNULL(null));
	il.append(new ALOAD(name.getIndex()));
	final int lengthMethod = cpg.addMethodref(STRING_CLASS,"length","()I");
	il.append(new INVOKEVIRTUAL(lengthMethod));
	// save length for later tests
	il.append(new ISTORE(length.getIndex()));
	// only if use-attribute-sets specified compile this seq
	if (_useSets != null) {
	    // check if element; if not skip to translate body
	    il.append(new ILOAD(length.getIndex()));
	    final BranchHandle ifBlock2 = il.append(new IFEQ(null));
	    // length != 0 -> element -> do attribute sets
	    _useSets.translate(classGen, methodGen);
	    // not an element; root
	    ifBlock2.setTarget(il.append(NOP));
	}
	// instantiate body of xsl:copy
	translateContents(classGen, methodGen);
	// check if element
	il.append(new ILOAD(length.getIndex()));
	final BranchHandle ifBlock3 = il.append(new IFEQ(null));
	// emit endElement
	il.append(methodGen.loadHandler());
	il.append(new ALOAD(name.getIndex()));
	il.append(methodGen.endElement());
	
	final InstructionHandle end = il.append(NOP);
	ifBlock1.setTarget(end);
	ifBlock3.setTarget(end);
	methodGen.removeLocalVariable(name);
	methodGen.removeLocalVariable(length);
    }
}
