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

package org.apache.xalan.xsltc.compiler.util;

import java.util.Vector;
import de.fub.bytecode.generic.Type;
import de.fub.bytecode.generic.*;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.Template;

public final class AttributeSetMethodGenerator extends MethodGenerator {
    private static int HANDLER_INDEX = 1;

    private static final de.fub.bytecode.generic.Type[] argTypes =
	new de.fub.bytecode.generic.Type[1];
    private static final String[] argNames = new String[1];
    
    static {
	argTypes[0] = Util.getJCRefType(TRANSLET_OUTPUT_SIG);
	argNames[0] = TRANSLET_OUTPUT_PNAME;
    }

    private final Instruction _astoreHandler;
    private final Instruction _aloadHandler;
    
    public AttributeSetMethodGenerator(String methodName, ClassGen classGen) {
	super(de.fub.bytecode.Constants.ACC_PRIVATE,
	      de.fub.bytecode.generic.Type.VOID,
	      argTypes, argNames, methodName, 
	      classGen.getClassName(),
	      new InstructionList(),
	      classGen.getConstantPool());
	
	_astoreHandler  = new ASTORE(HANDLER_INDEX);
	_aloadHandler   = new ALOAD(HANDLER_INDEX);
    }

    public int getIteratorIndex() {
	return INVALID_INDEX;		// not available
    }

    public Instruction storeHandler() {
	return _astoreHandler;
    }

    public Instruction loadHandler() {
	return _aloadHandler;
    }

    public int getLocalIndex(String name) {
	return INVALID_INDEX;		// not available
    }
}
