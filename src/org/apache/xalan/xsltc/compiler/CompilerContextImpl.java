/*
 * @(#)$Id$
 *
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
 * @author Santiago Pericas-Geertsen
 *
 */

package org.apache.xalan.xsltc.compiler;

import org.apache.xalan.xsltc.compiler.codemodel.CmClassDecl;
import org.apache.xalan.xsltc.compiler.codemodel.CmMethodDecl;

class CompilerContextImpl implements CompilerContext {

    /**
     * A thread local variable that holds the compiler context. Multiple
     * calls to getInstance() will return the same variable.
     */
    static private ThreadLocal _compilerContext = new ThreadLocal();

    /**
     * A reference to the XSLTC object.
     */
    private XSLTC _xsltc;

    /**
     * A reference to the parser object.
     */
    private Parser _parser;

    /**
     * A reference to the "current" codemodel class object.
     */
    private CmClassDecl _currentClass;

    /**
     * A refernece to the "current" codemodel method object.
     */
    private CmMethodDecl _currentMethod;

    /**
     * The method getInstance() should be used instead.
     */
    private CompilerContextImpl() {
    }

    /**
     * This method must be called the first time an instance of the
     * compiler context is created to ensure that a reference to the
     * parser is provided.
     */
    static CompilerContextImpl getInstance(XSLTC xsltc) {
        CompilerContextImpl result =
            (CompilerContextImpl) _compilerContext.get();
        if (result == null) {
            _compilerContext.set(result = new CompilerContextImpl());
        }
        result._xsltc = xsltc;
        result._parser = xsltc.getParser();
        return result;
    }

    /**
     * This method can be called to obtain a instance to the compiler
     * context after getInstance(Parser) has been invoked (otherwise
     * it will return null).
     */
    static CompilerContextImpl getInstance() {
        return (CompilerContextImpl) _compilerContext.get();
    }

    /**
     * Returns a reference to the XSLTC object.
     */
    public XSLTC getXSLTC() {
        return _xsltc;
    }

    /**
     * Returns a reference to the parser object.
     */
    public Parser getParser() {
        return _parser;
    }

    /**
     * Returns a reference to the "current" class object from the
     * codemodel package.
     */
    public CmClassDecl getCurrentClass() {
        return _currentClass;
    }

    /**
     * Set the current codemodel class object. This method must be
     * called whenever a new class is generated.
     */
    public void setCurrentClass(CmClassDecl currentClass) {
        _currentClass = currentClass;
    }

    /**
     * Returns a reference to the "current" method object from the
     * codemodel package.
     */
    public CmMethodDecl getCurrentMethod() {
        return _currentMethod;
    }

    /**
     * Set the current codemodel method object. This method must be
     * called whenever a new method is generated.
     */
    public void setCurrentMethod(CmMethodDecl currentMethod) {
        _currentMethod = currentMethod;
    }
}

