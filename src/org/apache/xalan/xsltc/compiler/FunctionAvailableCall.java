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
 * @author G. Todd Miller 
 * @author Santiago Pericas-Geertsen
 *
 */

package org.apache.xalan.xsltc.compiler;

import java.util.ArrayList;
import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;
import org.apache.xalan.xsltc.runtime.TransletLoader;

final class FunctionAvailableCall extends FunctionCall {

    private Expression _arg; 
    private String     _nameOfFunct = null; 
    private String     _namespaceOfFunct = null; 	
    private boolean    _isFunctionAvailable = false; 

    /**
     * Constructs a FunctionAvailableCall FunctionCall. Takes the
     * function name qname, for example, 'function-available', and 
     * a list of arguments where the arguments must be instances of 
     * LiteralExpression. 
     */
    public FunctionAvailableCall(QName fname, ArrayList arguments) {
	super(fname, arguments);
	_arg = (Expression)arguments.get(0);
	_type = null; 

        if (_arg instanceof LiteralExpr) {
	    LiteralExpr arg = (LiteralExpr) _arg;
            _namespaceOfFunct = arg.getNamespace();
            _nameOfFunct = arg.getValue();

            if (!isInternalNamespace()) {
              _isFunctionAvailable = hasMethods();
            }
        }
    }

    /**
     * Argument of function-available call must be literal, typecheck
     * returns the type of function-available to be boolean.  
     */
    public Type typeCheck(CompilerContext ccontext) throws TypeCheckError {
	if (_type != null) {
	   return _type;
	}
	if (_arg instanceof LiteralExpr) {
	    return _type = Type.Boolean;
	}
	ErrorMsg err = new ErrorMsg(ErrorMsg.NEED_LITERAL_ERR,
			"function-available", this);
	throw new TypeCheckError(err);
    }

    /**
     * Returns an object representing the compile-time evaluation 
     * of an expression. We are only using this for function-available
     * and element-available at this time.
     */
    public Object evaluateAtCompileTime() {
	return getResult() ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * for external java functions only: reports on whether or not
     * the specified method is found in the specifed class. 
     */
    private boolean hasMethods() {
	LiteralExpr arg = (LiteralExpr)_arg;
	
	// Get the class name from the namespace uri
	String className = getClassNameFromUri(_namespaceOfFunct);
	
	// Get the method name from the argument to function-available
	String methodName = null;
	int colonIndex = _nameOfFunct.indexOf(":");
	if (colonIndex > 0) {
	  String functionName = _nameOfFunct.substring(colonIndex+1);
	  int lastDotIndex = functionName.lastIndexOf('.');
	  if (lastDotIndex > 0) {
	    methodName = functionName.substring(lastDotIndex+1);
	    if (className != null && !className.equals(""))
	      className = className + "." + functionName.substring(0, lastDotIndex);
	    else
	      className = functionName.substring(0, lastDotIndex);
	  }
	  else
	    methodName = functionName;
	}
	else
	  methodName = _nameOfFunct;
	  
	if (className == null || methodName == null) {
	    return false;
	}
	
	// Replace the '-' characters in the method name
	if (methodName.indexOf('-') > 0)
	  methodName = replaceDash(methodName);

	try {
	    TransletLoader loader = new TransletLoader();
	    final Class clazz = loader.loadClass(className);

	    if (clazz == null) {
	    	return false;
	    }
	    else {
		final Method[] methods = clazz.getMethods();

		for (int i = 0; i < methods.length; i++) {
		    final int mods = methods[i].getModifiers();

		    if (Modifier.isPublic(mods)
			&& Modifier.isStatic(mods)
			&& methods[i].getName().equals(methodName))
		    {
			return true;
		    }
		}
	    }
	}
	catch (ClassNotFoundException e) {
	  return false;
	}
        return false;   
    }

    /**
     * Reports on whether the function specified in the argument to
     * xslt function 'function-available' was found.
     */
    public boolean getResult() {
	if (_nameOfFunct == null) { 
	    return false;
	}

        if (isInternalNamespace()) {
            final Parser parser = getParser();
            _isFunctionAvailable = 
		parser.functionSupported(Util.getLocalName(_nameOfFunct));
        }
 	return _isFunctionAvailable;
    }
    
    /**
     * Return true if the namespace uri is null or it is the XSLTC translet uri.
     */
    private boolean isInternalNamespace() {
    	return (_namespaceOfFunct == null ||
            _namespaceOfFunct.equals(EMPTYSTRING) ||
	    _namespaceOfFunct.equals(TRANSLET_URI));
    }

    /**
     * Calls to 'function-available' are resolved at compile time since 
     * the namespaces declared in the stylsheet are not available at run
     * time. Consequently, arguments to this function must be literals.
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	methodGen.getInstructionList().append(new PUSH(cpg, getResult()));
    }

}
