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

final class TransletOutput extends Instruction {

    private Expression _filename;

    private final static String MISSING_FILE_ATTR =
	"The <xsltc:output> element requires a 'file' attribute.";

    /**
     * Displays the contents of this <xsltc:output> element.
     */
    public void display(int indent) {
	indent(indent);
	Util.println("TransletOutput: " + _filename);
    }
		
    /**
     * Parse the contents of this <xsltc:output> element. The only attribute
     * we recognise is the 'file' attribute that contains teh output filename.
     */
    public void parseContents(Parser parser) {
	// Get the output filename from the 'file' attribute
	String filename = getAttribute("file");

	// Verify that the filename is in fact set
	if ((filename == null) || (filename.equals(EMPTYSTRING))) {
	    reportError(this, parser, ErrorMsg.REQUIRED_ATTR_ERR, "file");
	}

	// Save filename as an attribute value template
	_filename = AttributeValue.create(this, filename, parser);
	parseChildren(parser);
    }
    
    /**
     * Type checks the 'file' attribute (must be able to convert it to a str).
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	final Type type = _filename.typeCheck(stable);
	if (type instanceof StringType == false) {
	    _filename = new CastExpr(_filename, Type.String);
	}
	typeCheckContents(stable);
	return Type.Void;
    }
    
    /**
     * Compile code that opens the give file for output, dumps the contents of
     * the element to the file, then closes the file.
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	// Save the current output handler on the stack
	il.append(methodGen.loadHandler());
	
	final int open =  cpg.addMethodref(TRANSLET_CLASS,
					   "openOutputHandler",
					   "("+STRING_SIG+")"+
					   TRANSLET_OUTPUT_SIG);

	final int close =  cpg.addMethodref(TRANSLET_CLASS,
					    "closeOutputHandler",
					    "("+TRANSLET_OUTPUT_SIG+")V");

	// Create the new output handler (leave it on stack)
	il.append(classGen.loadTranslet());
	_filename.translate(classGen, methodGen);
	il.append(new INVOKEVIRTUAL(open));

	// Overwrite current handler
	il.append(methodGen.storeHandler());
	
	// Translate contents with substituted handler
	translateContents(classGen, methodGen);

	// Close the output handler (close file)
	il.append(classGen.loadTranslet());
	il.append(methodGen.loadHandler());
	il.append(new INVOKEVIRTUAL(close));

	// Restore old output handler from stack
	il.append(methodGen.storeHandler());
    }
}

