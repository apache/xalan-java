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
import java.util.Iterator;
import java.util.Enumeration;
import java.util.StringTokenizer;

import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class UseAttributeSets extends Instruction {

    // Only error that can occur:
    private final static String ATTR_SET_NOT_FOUND =
	"";

    // Contains the names of all references attribute sets
    private final Vector _sets = new Vector(2);

    /**
     * Constructur - define initial attribute sets to use
     */
    public UseAttributeSets(String setNames, Parser parser) {
	setParser(parser);
	addAttributeSets(setNames);
    }

    /**
     * This method is made public to enable an AttributeSet object to merge
     * itself with another AttributeSet (including any other AttributeSets
     * the two may inherit from).
     */
    public void addAttributeSets(String setNames) {
	if ((setNames != null) && (!setNames.equals(Constants.EMPTYSTRING))) {
	    final StringTokenizer tokens = new StringTokenizer(setNames);
	    while (tokens.hasMoreTokens()) {
		final QName qname = 
		    getParser().getQNameIgnoreDefaultNs(tokens.nextToken());
		_sets.add(qname);
	    }
	}
    }

    /**
     * Do nada.
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	return Type.Void;
    }

    /**
     * Generate a call to the method compiled for this attribute set
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {

	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	final SymbolTable symbolTable = getParser().getSymbolTable();

	// Go through each attribute set and generate a method call
	for (int i=0; i<_sets.size(); i++) {
	    // Get the attribute set name
	    final QName name = (QName)_sets.elementAt(i);
	    // Get the AttributeSet reference from the symbol table
	    final AttributeSet attrs = symbolTable.lookupAttributeSet(name);
	    // Compile the call to the set's method if the set exists
	    if (attrs != null) {
		final String methodName = attrs.getMethodName();
		il.append(classGen.loadTranslet());
		il.append(methodGen.loadHandler());
		il.append(methodGen.loadIterator());
		final int method = cpg.addMethodref(classGen.getClassName(),
						    methodName, ATTR_SET_SIG);
		il.append(new INVOKESPECIAL(method));
	    }
	    // Generate an error if the attribute set does not exist
	    else {
		final Parser parser = getParser();
		final String atrs = name.toString();
		reportError(this, parser, ErrorMsg.ATTRIBSET_UNDEF_ERR, atrs);
	    }
	}
    }
}
