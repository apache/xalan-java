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
import java.util.HashSet;
import org.apache.xalan.xsltc.compiler.util.Type;
import de.fub.bytecode.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class FunctionAvailableCall extends FunctionCall {
    static HashSet AvailableFunctions = new HashSet();

    static {
	AvailableFunctions.add("boolean");
	AvailableFunctions.add("ceiling");
	AvailableFunctions.add("concat");
	AvailableFunctions.add("contains");
	AvailableFunctions.add("count");
	AvailableFunctions.add("current");
	AvailableFunctions.add("document");
	AvailableFunctions.add("element-available");
	AvailableFunctions.add("false");
	AvailableFunctions.add("floor");
	AvailableFunctions.add("format-number");
	AvailableFunctions.add("function-available");
	AvailableFunctions.add("generate-id");
	AvailableFunctions.add("id");
	AvailableFunctions.add("key");
	AvailableFunctions.add("lang");
	AvailableFunctions.add("last");
	AvailableFunctions.add("local-name");
	AvailableFunctions.add("name");
	AvailableFunctions.add("namespace-uri");
	AvailableFunctions.add("normalize-space");
	AvailableFunctions.add("not");
	AvailableFunctions.add("number");
	AvailableFunctions.add("position");
	AvailableFunctions.add("round");
	AvailableFunctions.add("starts-with");
	AvailableFunctions.add("string");
	AvailableFunctions.add("string-length");
	AvailableFunctions.add("substring");
	AvailableFunctions.add("substring-after");
	AvailableFunctions.add("substring-before");
	AvailableFunctions.add("sum");
	AvailableFunctions.add("system-property");
	AvailableFunctions.add("translate");
	AvailableFunctions.add("true");
	AvailableFunctions.add("unparsed-entity-uri");
    }

    public FunctionAvailableCall(QName fname, Vector arguments) {
	super(fname, arguments);
    }

    /**
     * Force the argument to this function to be a literal string.
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	if (argument() instanceof LiteralExpr) {
	    return _type = Type.Boolean;
	}
	throw new TypeCheckError(ErrorMsg.LITERALS_ERR, "function-available");
    }

    /**
     * Calls to 'function-available' are resolved at compile time since 
     * the namespaces declared in the stylsheet are not available at run
     * time. Consequently, arguments to this function must be literals.
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final InstructionList il = methodGen.getInstructionList();
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final LiteralExpr arg = (LiteralExpr)argument();
	final String namespace = arg.getNamespace();
	boolean result = false;

	if (namespace == null) {
	    result = AvailableFunctions.contains(arg.getValue());
	}
	il.append(new PUSH(cpg, result));
    }
}
