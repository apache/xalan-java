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
 * modification, are permitted provided that the following expressions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of expressions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of expressions and the following disclaimer in
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

package org.apache.xalan.xsltc.compiler.codemodel;

import java.util.List;
import java.util.ArrayList;

public class CmTryCatchStmt extends CmStatement {

    /**
     * The statement of this synchronized.
     */
    private CmStatement _try;

    /**
     * List of parameter declarations for each catch.
     */
    private List _paramDecls = null;

    /**
     * List of statements for each catch (same length as
     * _paramDecls).
     */
    private List _statements = null;

    /**
     * The statement associated with the finally clause.
     */
    private CmStatement _finally = null;

    public CmTryCatchStmt(CmStatement tryStmt, CmParameterDecl paramDecl,
	CmStatement statement)
    {
	_try = tryStmt;
	_paramDecls = new ArrayList();
	_paramDecls.add(paramDecl);
	_statements = new ArrayList();
	_statements.add(statement);
    }

    public CmTryCatchStmt(CmStatement tryStmt, List paramDecls,
	List statements, CmStatement finallyStmt)
    {
	_try = tryStmt;
	_paramDecls = paramDecls;
	_statements = statements;
	_finally = finallyStmt;
    }

    public CmStatement getTry() {
	return _try;
    }

    public List getParamDecls() {
	return _paramDecls;
    }

    public List getCmStatements() {
	return _statements;
    }

    public CmStatement getFinally() {
	return _finally;
    }

    public Object accept(CmVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public Object childrenAccept(CmVisitor visitor, Object data) {
	data = _try.accept(visitor, data);
	final int n = _paramDecls != null ? _paramDecls.size() : 0;
	for (int i = 0; i < n; i++) {
	    data = ((CmNode) _paramDecls.get(i)).accept(visitor, data);
	    data = ((CmNode) _statements.get(i)).accept(visitor, data);
	}
	return _finally != null ? _finally.accept(visitor, data) : data;
    }
}
