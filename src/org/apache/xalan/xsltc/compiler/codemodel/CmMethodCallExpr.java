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
 * @author Santiago Pericas-Geertsen
 *
 */

package org.apache.xalan.xsltc.compiler.codemodel;

import java.util.List;
import java.util.ArrayList;

public class CmMethodCallExpr extends CmExpression {

    /**
     * CmExpression that evaluates to "this". Set to null if
     * method is static.
     */
    private CmExpression _that = null;

    /**
     * Name of method - includes className if method is static.
     */
    private String _methodName;

    /**
     * List of actual parameter expressions.
     */
    private List _parameters = null;

    public CmMethodCallExpr(String methodName, CmExpression param1) {
        _methodName = methodName;
        _parameters = new ArrayList();
        _parameters.add(param1);
    }

    public CmMethodCallExpr(String methodName, CmExpression param1,
        CmExpression param2)
    {
        this(methodName, param1);
        _parameters.add(param2);
    }

    public CmMethodCallExpr(String methodName, CmExpression param1,
        CmExpression param2, CmExpression param3)
    {
        this(methodName, param1, param2);
        _parameters.add(param3);
    }

    public CmMethodCallExpr(CmExpression that, String methodName,
	List parameters)
    {
	_that = that;
	_methodName = methodName;
	_parameters = parameters;
    }

    public CmExpression getThat() {
	return _that;
    }

    public String getMethodName() {
	return _methodName;
    }

    public List getParameters() {
	return _parameters;
    }

    public Object accept(CmVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public Object childrenAccept(CmVisitor visitor, Object data) {
	data = _that.accept(visitor, data);
	return listAccept(_parameters, visitor, data);
    }

}
