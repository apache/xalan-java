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
 * @author Morten Jorgensen
 *
 */

package org.apache.xalan.xsltc.compiler;

import java.util.Vector;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

final class DocumentCall extends FunctionCall {

    private Expression _arg1 = null;
    private Expression _arg2 = null;
    private Type       _arg1Type;

    /**
     * Default function call constructor
     */
    public DocumentCall(QName fname, Vector arguments) {
        super(fname, arguments);
    }

    /**
     * Type checks the arguments passed to the document() function. The first
     * argument can be any type (we must cast it to a string) and contains the
     * URI of the document
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        // At least one argument - two at most
        final int ac = argumentCount();
        if ((ac < 1) || (ac > 2)) {
            ErrorMsg msg = new ErrorMsg(ErrorMsg.ILLEGAL_ARG_ERR, this);
            throw new TypeCheckError(msg);
        }
        if (getStylesheet() == null) {
            ErrorMsg msg = new ErrorMsg(ErrorMsg.ILLEGAL_ARG_ERR, this);
            throw new TypeCheckError(msg);
        }

        // Parse the first argument 
        _arg1 = argument(0);

        if (_arg1 == null) {// should not happened 
            ErrorMsg msg = new ErrorMsg(ErrorMsg.DOCUMENT_ARG_ERR, this);
            throw new TypeCheckError(msg);
        }

        _arg1Type = _arg1.typeCheck(stable);
        if ((_arg1Type != Type.NodeSet) && (_arg1Type != Type.String)) {
            _arg1 = new CastExpr(_arg1, Type.String);
        }

        // Parse the second argument 
        if (ac == 2) {
            _arg2 = argument(1);

            if (_arg2 == null) {// should not happened 
                ErrorMsg msg = new ErrorMsg(ErrorMsg.DOCUMENT_ARG_ERR, this);
                throw new TypeCheckError(msg);
            }

            final Type arg2Type = _arg2.typeCheck(stable);

            if (arg2Type.identicalTo(Type.Node)) {
                _arg2 = new CastExpr(_arg2, Type.NodeSet);
            } else if (arg2Type.identicalTo(Type.NodeSet)) {
                // falls through
            } else {
                ErrorMsg msg = new ErrorMsg(ErrorMsg.DOCUMENT_ARG_ERR, this);
                throw new TypeCheckError(msg);
            }
        }

        return _type = Type.NodeSet;
    }
    
    /**
     * Translates the document() function call to a call to LoadDocument()'s
     * static method document().
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final int ac = argumentCount();

        final int domField = cpg.addFieldref(classGen.getClassName(),
                                             DOM_FIELD,
                                             DOM_INTF_SIG);
          
        String docParamList = null;
        if (ac == 1) {
           // documentF(Object,String,AbstractTranslet,DOM)
           docParamList = "("+OBJECT_SIG+STRING_SIG+TRANSLET_SIG+DOM_INTF_SIG
                         +")"+NODE_ITERATOR_SIG;
        } else { //ac == 2; ac < 1 or as >2  was tested in typeChec()
           // documentF(Object,DTMAxisIterator,String,AbstractTranslet,DOM)
           docParamList = "("+OBJECT_SIG+NODE_ITERATOR_SIG+STRING_SIG
                         +TRANSLET_SIG+DOM_INTF_SIG+")"+NODE_ITERATOR_SIG;  
        }
        final int docIdx = cpg.addMethodref(LOAD_DOCUMENT_CLASS, "documentF",
                                            docParamList);


        // The URI can be either a node-set or something else cast to a string
        _arg1.translate(classGen, methodGen);
        if (_arg1Type == Type.NodeSet) {
            _arg1.startIterator(classGen, methodGen);
        }

        if (ac == 2) {
            //_arg2 == null was tested in typeChec()
            _arg2.translate(classGen, methodGen);
            _arg2.startIterator(classGen, methodGen);       
        }
    
        // Feck the rest of the parameters on the stack
        il.append(new PUSH(cpg, getStylesheet().getSystemId()));
        il.append(classGen.loadTranslet());
        il.append(DUP);
        il.append(new GETFIELD(domField));
        il.append(new INVOKESTATIC(docIdx));
    }

}
