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

/**
 * Base class for Codemodel hierarchy. Abstract grammar:
 *
 * CmCompilationUnit ::= CmCompilationUnit CmPackageDecl? CmImportDecl* CmClassDecl*
 *
 * CmPackageDecl ::= CmPackageDecl Name
 *
 * CmImportDecl ::= CmImportDecl Name
 *
 * CmClassDecl ::=
 *      CmClassDecl Flags* Name Name? Name* CmClassDecl* CmVariableDecl* CmMethodDecl*
 *
 * CmVariableDecl ::= CmVariableDecl Flags* CmType Name Expr?
 *
 * CmMethodDecl ::= CmMethodDecl Flags* CmType Name CmParameterDecl* Stmt
 *
 * CmParameterDecl ::= CmParameterDecl CmType Name
 *
 * Stmt ::=
 *      CmEmptyStmt
 *      | CmExprStmt Expr
 *      | CmWhileStmt Expr Stmt
 *      | CmDoWhileStmt Stmt Expr
 *      | CmBreakStmt Name
 *      | CmContinueStmt Name
 *      | CmReturnStmt Expr
 *      | CmThrowStmt Expr
 *      | CmBlockStmt Stmt*
 *      | CmLabeledStmt Name Stmt
 *      | CmIfStmt Expr Stmt Stmt?
 *      | CmForStmt Expr Expr Expr Expr
 *      | CmSynchronizedStmt Expr Stmt
 *      | CmTryCatchStmt Stmt (CmParameterDecl Stmt)* Stmt?
 *      | CmVariableStmt CmVariableDecl
 *
 * Expr ::=
 *      CmEmptyExpr
 *      | CmPreUnaryExpr CmOperator Expr
 *      | CmPostUnaryExpr Expr CmOperator
 *      | CmBinaryExpr Expr CmOperator Expr
 *      | CmAssignmentExpr Expr CmOperator Expr
 *      | CmConditionalExpr Expr Expr Expr
 *      | CmInstanceOfExpr Expr CmType
 *      | CmCastExpr CmType Expr
 *      | CmMethodCallExpr Expr? Name Expr*
 *      | CmNewArrayExpr CmType Expr*
 *      | CmNewInstanceExpr Name Expr*
 *      | CmVariableRefExpr Name	// includes this & super
 *      | CmIntegerExpr int
 *      | CmDoubleExpr double
 *      | CmCharExpr char
 *      | CmStringExpr string
 *      | CmBooleanExpr
 *      | CmNullExpr
 *
 * CmType ::=
 *      CmIntegerType
 *      | CmDoubleType
 *      | CmCharType
 *      | CmStringType
 *      | CmBooleanType
 *      | CmClassType Name
 *
 * CmOperator ::=
 *      "-" | "+" | "!" | "~" | "++" | "--" | "++" | "--" |
 *      "+" | "-" | "*" | "/" | "%" | "<" | "<=" | ">" | ">=" |
 *      "&&" | "||" | "<<" | ">>" | "&" | "|"  | "==" | "!=" |
 *      "=" | "*=" | "/=" | "%=" | "+=" | "-=" | "<<=" | ">>=" |
 *      "&=" | "^=" | "|="
 *
 * Flags ::=
 *      "static" | "public" | "private" | "protected" | "final" | "abstract"
 *
 */
public abstract class CmNode {

    public Object accept(CmVisitor visitor, Object data) {
	return data;
    }

    public Object childrenAccept(CmVisitor visitor, Object data) {
	return data;
    }

    protected  Object listAccept(List list, CmVisitor visitor, Object object) {
	if (list != null) {
	    final int n = list.size();
	    for (int i = 0; i < n; i++) {
		object = ((CmNode) list.get(i)).accept(visitor, object);
	    }
	}
	return object;
    }
}
