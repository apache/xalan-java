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
 * @author Gopal Sharma
 */

package org.apache.xalan.xsltc.compiler.codemodel;

import java.io.Writer;
import java.io.IOException;
import java.util.List;

public class JavaCmVisitor implements CmVisitor {

    /**
     * Indentation level.
     */
    private int _indentLevel = 0;

    /**
     * Number of indent spaces.
     */
    private int _indentSpaces = 4;

    /**
     * Output stream.
     */
    private Writer _writer;

    public JavaCmVisitor(Writer writer) {
	_writer = writer;
    }

    public JavaCmVisitor(Writer writer, int indentLevel,
	int indentSpaces)
    {
	this(writer);
	_indentLevel = indentLevel;
	_indentSpaces = indentSpaces;
    }

    public void flush() {
        try {
            _writer.flush();
        }
        catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
        }
    }

    // -- Compilation Unit -----------------------------------------

    public Object visit(CmCompilationUnit node, Object object) {
	return node.childrenAccept(this, object);
    }

    // -- CmDeclarations ---------------------------------------------

    public Object visit(CmClassDecl node, Object object) {
	try {
	    indent(_indentLevel++, _indentSpaces);

	    object = visitCmModifiers(node.getCmModifiers(), object);
	    _writer.write("class " + node.getName());

	    String extendsName = node.getExtendsName();
	    if (extendsName != null) {
		_writer.write(" extends " + extendsName);
	    }

	    List implementsNames = node.getImplementsNames();
	    if (implementsNames != null) {
		_writer.write(" implements ");
		object = visitList(implementsNames, object, ", ");
	    }
	    _writer.write(" {\n");

	    List declList = node.getInnerCmClassDecls();
	    if (declList != null) {
		object = visitList(declList, object, "\n");
		_writer.write("\n");
	    }

	    declList = node.getCmVariableDecls();
	    if (declList != null) {
		object = visitList(declList, object, "\n");
		_writer.write("\n");
	    }

	    declList = node.getCmMethodDecls();
	    if (declList != null) {
		object = visitList(declList, object, "\n");
		_writer.write("}\n");
	    }
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmImportDecl node, Object object) {
	try {
	    _writer.write("import " + node.getImportName() + ";\n");
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmPackageDecl node, Object object) {
	try {
	    _writer.write("package " + node.getPackageName() + ";\n");
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
        }
    }

    public Object visit(CmMethodDecl node, Object object) {
	try {
	    indent(_indentLevel, _indentSpaces);
	    object = visitCmModifiers(node.getCmModifiers(), object);

	    CmType returnCmType = node.getCmType();
	    if (returnCmType != null) {
		object = node.getCmType().accept(this, object);
		_writer.write(' ');
	    }

	    _writer.write(node.getName() + '(');
	    object = visitList(node.getParamDecls(), object, ", ");
	    _writer.write(')');
	    return node.getBody().accept(this, object);
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
        }
    }

    public Object visit(CmParameterDecl node, Object object) {
	try {
	    object = node.getCmType().accept(this, object);
	    _writer.write(' ' + node.getName());
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmVariableDecl node, Object object) {
	try {
	    indent(_indentLevel, _indentSpaces);
	    object = visitCmModifiers(node.getCmModifiers(), object);
	    object = node.getCmType().accept(this, object);
	    _writer.write(' ' + node.getName());

	    CmExpression initializer = node.getInitializer();
	    if (initializer != null) {
		_writer.write(" = ");
		object = initializer.accept(this, object);
	    }

	    _writer.write(";\n");
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    // -- CmTypes --------------------------------------------------

    public Object visit(CmBooleanType node, Object object) {
	try {
	    _writer.write("boolean");
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmCharType node, Object object) {
	try {
	    _writer.write("char");
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmClassType node, Object object) {
	try {
	    _writer.write(node.getName());
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmDoubleType node, Object object) {
	try {
	    _writer.write("double");
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmIntegerType node, Object object) {
	try {
	    _writer.write("int");
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmStringType node, Object object) {
	try {
	    _writer.write("String");
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmVoidType node, Object object) {
         try {
             _writer.write("void");
             return object;
         }
         catch (IOException e) {
             throw new RuntimeException(e.getMessage());
         }
    }


    // -- CmStatements ---------------------------------------------

    public Object visit(CmEmptyStmt node, Object object) {
	try {
	    _writer.write(';');
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmBlockStmt node, Object object) {
	try {
	    _writer.write(" {\n");
	    ++_indentLevel;
	    object = visitList(node.getCmStatements(), object, "\n");
	    indent(--_indentLevel, _indentSpaces);
	    _writer.write("}\n");
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmBreakStmt node, Object object) {
	try {
	    indent(_indentLevel, _indentSpaces);
	    _writer.write("break");
	    final String label = node.getLabel();
	    if (label != null) {
		_writer.write(' ' + label);
	    }
	    _writer.write(";\n");
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmContinueStmt node, Object object) {
	try {
	    indent(_indentLevel, _indentSpaces);
	    _writer.write("continue");
	    final String label = node.getLabel();
	    if (label != null) {
		_writer.write(' ' + label);
	    }
	    _writer.write(";\n");
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmDoWhileStmt node, Object object) {
	try {
	    indent(_indentLevel++, _indentSpaces);
	    _writer.write("do { ");
	    object = node.childrenAccept(this, object);
	    indent(--_indentLevel, _indentSpaces);
	    _writer.write("}\n");
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmExprStmt node, Object object) {
	try {
	    indent(_indentLevel++, _indentSpaces);
	    object = node.childrenAccept(this, object);
	    _indentLevel--;
	    _writer.write(";\n");
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmForStmt node, Object object) {
	try {
	    indent(_indentLevel++, _indentSpaces);
	    _writer.write("for (");
	    object = node.getInit().accept(this, object);
	    _writer.write("; ");
	    object = node.getCondition().accept(this, object);
	    _writer.write("; ");
	    object = node.getUpdate().accept(this, object);
	    _writer.write(")\n");
	    object = node.getBody().accept(this, object);
	    _indentLevel--;
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmIfStmt node, Object object) {
	try {
	    indent(_indentLevel++, _indentSpaces);
	    _writer.write("if (");
	    object = node.getCondition().accept(this, object);
	    _writer.write(")\n");
	    object = node.getTrueCase().accept(this, object);
	    indent(_indentLevel - 1, _indentSpaces);
	    _writer.write("else\n");
	    object = node.getFalseCase().accept(this, object);
	    _indentLevel--;
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmLabeledStmt node, Object object) {
	try {
	    indent(_indentLevel++, _indentSpaces);
	    _writer.write(node.getLabel() + ":\n");
	    object = node.getCmStatement().accept(this, object);
	    _indentLevel--;
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmReturnStmt node, Object object) {
	try {
	    indent(_indentLevel, _indentSpaces);
            _writer.write("return ");
	    object = node.childrenAccept(this, object);
	    _writer.write(";\n");
            return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmSynchronizedStmt node, Object object) {
	try {
	    indent(_indentLevel++, _indentSpaces);
	    _writer.write("synchronized (");
	    object = node.getCmExpression().accept(this, object);
	    _writer.write(") {\n");
	    object = node.getCmStatement().accept(this, object);
	    indent(--_indentLevel, _indentSpaces);
	    _writer.write("}\n");
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmThrowStmt node, Object object) {
	try {
	    indent(_indentLevel, _indentSpaces);
	    _writer.write("throw ");
	    object = node.childrenAccept(this, object);
	    _writer.write(";\n");
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmTryCatchStmt node, Object object) {
	try {
	    indent(_indentLevel++, _indentSpaces);
	    _writer.write("try\n");
	    node.getTry().accept(this, object);

	    List paramDecls = node.getParamDecls();
	    List statements = node.getCmStatements();
	    final int n = paramDecls != null ? paramDecls.size() : 0;
	    for (int i = 0; i < n; i++) {
		indent(_indentLevel - 1, _indentSpaces);
		_writer.write("catch (");
		object = ((CmNode) paramDecls.get(i)).accept(this, object);
		_writer.write(")\n");
		object = ((CmNode) statements.get(i)).accept(this, object);
	    }

	    CmStatement finallyStmt = node.getFinally();
	    return finallyStmt != null ? finallyStmt.accept(this, object)
		    		       : object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmVariableStmt node, Object object) {
	return node.childrenAccept(this, object);
    }

    public Object visit(CmWhileStmt node, Object object) {
	try {
	    indent(_indentLevel++, _indentSpaces);
	    _writer.write("while (");
	    object = node.getCondition().accept(this, object);
	    _writer.write(")\n");
	    object = node.getBody().accept(this, object);
	    _indentLevel--;
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

   public Object visit(CmSwitchStmt node, Object object){
     try {
            indent(_indentLevel++, _indentSpaces);
            _writer.write("switch (");
            object = node.getCondition().accept(this, object);
            _writer.write(")\n");
            object = node.getBody().accept(this, object);
            _indentLevel--;
            return object;
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage());
	}
   }

    // -- CmExpressions --------------------------------------------

    public Object visit(CmEmptyExpr node, Object object) {
	return object;
    }

    public Object visit(CmPreUnaryExpr node, Object object) {
	try {
	    _writer.write(node.getCmOperator().toString());
	    return node.childrenAccept(this, object);
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmPostUnaryExpr node, Object object) {
	try {
	    object = node.childrenAccept(this, object);
	    _writer.write(node.getCmOperator().toString());
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmBinaryExpr node, Object object) {
	try {
	    _writer.write('(');
	    node.getLeft().accept(this, object);
	    _writer.write(' ');
	    _writer.write(node.getCmOperator().toString());
	    _writer.write(' ');
	    object = node.getRight().accept(this, object);
	    _writer.write(')');
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmAssignmentExpr node, Object object) {
	try {
	    _writer.write('(');
	    node.getLeft().accept(this, object);
	    _writer.write(node.getCmOperator().toString());
	    object = node.getRight().accept(this, object);
	    _writer.write(')');
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmConditionalExpr node, Object object) {
	try {
	    node.getCondition().accept(this, object);
	    _writer.write(" ? ");
	    node.getTrueCase().accept(this, object);
	    _writer.write(" : ");
	    return node.getFalseCase().accept(this, object);
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmInstanceOfExpr node, Object object) {
	try {
	    node.getCmExpression().accept(this, object);
	    _writer.write(" instanceof ");
	    return node.getCmType().accept(this, object);
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmCastExpr node, Object object) {
	try {
	    _writer.write("((");
	    node.getCmType().accept(this, object);
	    _writer.write(") ");
	    return node.getCmExpression().accept(this, object);
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmMethodCallExpr node, Object object) {
	try {
	    CmExpression that = node.getThat();
	    if (that != null) {
		that.accept(this, object);
		_writer.write(".");
	    }
	    _writer.write(node.getMethodName() + "(");
	    object = visitList(node.getParameters(), object, ", ");
	    _writer.write(')');
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmNewArrayExpr node, Object object) {
	try {
	    _writer.write("new ");
	    object = node.getCmType().accept(this, object);
            List dimensionSizes = node.getDimensionSizes();
	    if (dimensionSizes != null) {
		_writer.write("[");
		object = visitList(dimensionSizes, object, "][");
		_writer.write("]");
	    }
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmNewInstanceExpr node, Object object) {
	try {
	    _writer.write("new ");
	    _writer.write(node.getClassName() + "(");
	    object = visitList(node.getParameters(), object, ",");
	    _writer.write(')');
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmVariableRefExpr node, Object object) {
	try {
	    _writer.write(node.toString());
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmIntegerExpr node, Object object) {
	try {
	    _writer.write(Integer.toString(node.getValue()));
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmDoubleExpr node, Object object) {
	try {
	    _writer.write(node.toString());
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmCharExpr node, Object object) {
	try {
	    _writer.write("'" + node.toString() + "'");
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmStringExpr node, Object object) {
	try {
	    _writer.write(node.toString());
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    public Object visit(CmBooleanExpr node, Object object) {
        try {
            _writer.write(node == CmBooleanExpr.trueExpr ? "true" : "false");
	    return object;
        }
        catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
        }
    }

    public Object visit(CmNullExpr node, Object object) {
	try {
	    _writer.write("null");
	    return object;
	}
	catch (IOException e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    // -- Utility methods ------------------------------------------------

    private Object visitCmModifiers(int modifiers, Object object)
	throws IOException
    {
	if ((modifiers & CmModifier.STATIC) > 0) {
	    _writer.write("static ");
	}
	if ((modifiers & CmModifier.PUBLIC) > 0) {
	    _writer.write("public ");
	}
	if ((modifiers & CmModifier.PRIVATE) > 0) {
	    _writer.write("private ");
	}
	if ((modifiers & CmModifier.PROTECTED) > 0) {
	    _writer.write("protected ");
	}
	if ((modifiers & CmModifier.FINAL) > 0) {
	    _writer.write("final ");
	}
	if ((modifiers & CmModifier.ABSTRACT) > 0) {
	    _writer.write("abstract ");
	}
	return object;
    }

    private Object visitList(List list, Object object, String separ)
	throws IOException
    {
	if (list != null) {
	    final int n = list.size();
	    for (int i = 0; i < n; i++) {
		object = ((CmNode) list.get(i)).accept(this, object);
		if (i < n - 1) _writer.write(separ);
	    }
	}
	return object;
    }

    private void indent(int indentLevel, int indentSpaces)
	throws IOException
    {
	int n = indentLevel * indentSpaces;
	while (n-- > 0) {
	    _writer.write(' ');
	}
    }
}
