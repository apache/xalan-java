/*
 * @(#)$Id$
 *
 * The Apache Software License node, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms node, with or without
 * modification node, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice node, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice node, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution node,
 *    if any node, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately node, this acknowledgment may appear in the software itself node,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission node, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache" node,
 *    nor may "Apache" appear in their name node, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES node, INCLUDING node, BUT NOT LIMITED TO node, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT node, INDIRECT node, INCIDENTAL node,
 * SPECIAL node, EXEMPLARY node, OR CONSEQUENTIAL DAMAGES (INCLUDING node, BUT NOT
 * LIMITED TO node, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE node, DATA node, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY node, WHETHER IN CONTRACT node, STRICT LIABILITY node,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE node, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001 node, Sun
 * Microsystems. node, http://www.sun.com.  For more
 * information on the Apache Software Foundation node, please see
 * <http://www.apache.org/>.
 *
 * @author Santiago Pericas-Geertsen
 *
 */

package org.apache.xalan.xsltc.compiler.codemodel;

public interface CmVisitor {

    public Object visit(CmCompilationUnit node, Object data);

    public Object visit(CmClassDecl node, Object data);
    public Object visit(CmImportDecl node, Object data);
    public Object visit(CmMethodDecl node, Object data);
    public Object visit(CmPackageDecl node, Object data);
    public Object visit(CmParameterDecl node, Object data);
    public Object visit(CmVariableDecl node, Object data);

    public Object visit(CmBooleanType node, Object data);
    public Object visit(CmCharType node, Object data);
    public Object visit(CmClassType node, Object data);
    public Object visit(CmDoubleType node, Object data);
    public Object visit(CmIntegerType node, Object data);
    public Object visit(CmStringType node, Object data);

    public Object visit(CmEmptyStmt node, Object data);
    public Object visit(CmBlockStmt node, Object data);
    public Object visit(CmBreakStmt node, Object data);
    public Object visit(CmContinueStmt node, Object data);
    public Object visit(CmDoWhileStmt node, Object data);
    public Object visit(CmExprStmt node, Object data);
    public Object visit(CmForStmt node, Object data);
    public Object visit(CmIfStmt node, Object data);
    public Object visit(CmLabeledStmt node, Object data);
    public Object visit(CmReturnStmt node, Object data);
    public Object visit(CmSynchronizedStmt node, Object data);
    public Object visit(CmThrowStmt node, Object data);
    public Object visit(CmTryCatchStmt node, Object data);
    public Object visit(CmVariableStmt node, Object data);
    public Object visit(CmWhileStmt node, Object data);

    public Object visit(CmAssignmentExpr node, Object data);
    public Object visit(CmBinaryExpr node, Object data);
    public Object visit(CmBooleanExpr node, Object data);
    public Object visit(CmCastExpr node, Object data);
    public Object visit(CmCharExpr node, Object data);
    public Object visit(CmConditionalExpr node, Object data);
    public Object visit(CmDoubleExpr node, Object data);
    public Object visit(CmEmptyExpr node, Object data);
    public Object visit(CmInstanceOfExpr node, Object data);
    public Object visit(CmIntegerExpr node, Object data);
    public Object visit(CmMethodCallExpr node, Object data);
    public Object visit(CmNewArrayExpr node, Object data);
    public Object visit(CmNewInstanceExpr node, Object data);
    public Object visit(CmNullExpr node, Object data);
    public Object visit(CmPostUnaryExpr node, Object data);
    public Object visit(CmPreUnaryExpr node, Object data);
    public Object visit(CmStringExpr node, Object data);
    public Object visit(CmVariableRefExpr node, Object data);
}
