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

public class CmClassDecl extends CmDeclaration {

    /**
     * Class modifiers such as public, abstract, etc.
     */
    private int _modifiers;

    /**
     * Name of this class.
     */
    private String _name;

    /**
     * Name of super class.
     */
    private String _extendsName = null;

    /**
     * List of interface names (strings) implemented by this class.
     */
    private List _implementsNames = null;

    /**
     * List of inner classes.
     */
    private List _innerCmClassDecls = null;

    /**
     * List of variable declarations.
     */
    private List _variableDecls = null;

    /**
     * List of method declarations.
     */
    private List _methodDecls = null;

    public CmClassDecl(int modifiers, String name, String extendsName,
        List implementsNames)
    {
        _modifiers = modifiers;
	_name = name;
        _extendsName = extendsName;
        _implementsNames = implementsNames;
    }

    public int getCmModifiers() {
	return _modifiers;
    }

    /**
     * Method to set all class modifiers at once.
     */
    public void setCmModifiers(int modifiers) {
	_modifiers = modifiers;
    }

    /**
     * Method to add one modifier at a time.
     */
    public CmClassDecl addCmModifiers(int flag) {
	_modifiers |= flag;
	return this;
    }

    public String getName() {
	return _name;
    }

    public String getExtendsName() {
	return _extendsName;
    }

    public void setExtendsName(String extendsName) {
	_extendsName = extendsName;
    }

    public List getImplementsNames() {
	return _implementsNames;
    }

    public CmClassDecl addImplementsName(String implementName) {
	if (_implementsNames == null) {
	    _implementsNames = new ArrayList();
	}
	_implementsNames.add(implementName);
	return this;
    }

    public List getInnerCmClassDecls() {
	return _innerCmClassDecls;
    }

    public CmClassDecl addInnerCmClassDecl(CmDeclaration innerCmClassDecl) {
	if (_innerCmClassDecls == null) {
	    _innerCmClassDecls = new ArrayList();
	}
	_innerCmClassDecls.add(innerCmClassDecl);
	return this;
    }

    public List getCmVariableDecls() {
	return _variableDecls;
    }

    public CmClassDecl addCmDeclaration(CmDeclaration declaration) {
        if (declaration instanceof CmVariableDecl) {
            addCmVariableDecl(declaration);
        }
        else if (declaration instanceof CmMethodDecl) {
            addCmMethodDecl(declaration);
        }
        else if (declaration instanceof CmClassDecl) {
            addInnerCmClassDecl(declaration);
        }
        return this;
    }

    public CmClassDecl addCmVariableDecl(CmDeclaration variableDecl) {
	if (_variableDecls == null) {
	    _variableDecls = new ArrayList();
	}
	_variableDecls.add(variableDecl);
	return this;
    }

    public List getCmMethodDecls() {
	return _methodDecls;
    }

    public CmClassDecl addCmMethodDecl(CmDeclaration methodDecl) {
	if (_methodDecls == null) {
	    _methodDecls = new ArrayList();
	}
	_methodDecls.add(methodDecl);
	return this;
    }

    public Object accept(CmVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public Object childrenAccept(CmVisitor visitor, Object data) {
	data = listAccept(_innerCmClassDecls, visitor, data);
	data = listAccept(_variableDecls, visitor, data);
	return listAccept(_methodDecls, visitor, data);
    }
}
