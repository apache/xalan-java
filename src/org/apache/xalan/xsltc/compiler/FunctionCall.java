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
 * @author Morten Jorgensen
 * @author Erwin Bolwidt <ejb@klomp.org>
 *
 */

package org.apache.xalan.xsltc.compiler;

import java.util.Vector;
import java.util.Enumeration;
import java.util.Hashtable;

import java.lang.reflect.*;

import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;
import org.apache.xalan.xsltc.runtime.TransletLoader;

class FunctionCall extends Expression {

    // Name of this function call
    private QName  _fname;
    // Arguments to this function call (might not be any)
    private final Vector _arguments;
    // Empty argument list, used for certain functions
    private final static Vector EMPTY_ARG_LIST = new Vector(0);

    // Valid namespaces for Java function-call extension
    protected final static String EXT_XSLTC = 
	TRANSLET_URI;

    protected final static String JAVA_EXT_XSLTC = 
	EXT_XSLTC + "/java";

    protected final static String EXT_XALAN =
	"http://xml.apache.org/xalan";

    protected final static String JAVA_EXT_XALAN =
	"http://xml.apache.org/xslt/java";


    // External Java function's class/method/signature
    private String     _className;
    private Method     _chosenMethod;
    private MethodType _chosenMethodType;

    // Encapsulates all unsupported external function calls
    private boolean    unresolvedExternal;

    // Legal conversions between internal and Java types.
    private static final MultiHashtable _internal2Java = new MultiHashtable();

    // Legal conversions between Java and internal types.
    private static final Hashtable _java2Internal = new Hashtable();

    /**
     * Defines 2 conversion tables:
     * 1. From internal types to Java types and
     * 2. From Java types to internal types.
     * These two tables are used when calling external (Java) functions.
     */
    static {
	try {
	    final Class objectClass   = Class.forName("java.lang.Object");
	    final Class stringClass   = Class.forName("java.lang.String");
	    final Class nodeClass     = Class.forName("org.w3c.dom.Node");
	    final Class nodeListClass = Class.forName("org.w3c.dom.NodeList");

	    // Possible conversions between internal and Java types
	    _internal2Java.put(Type.Boolean, Boolean.TYPE);

	    _internal2Java.put(Type.Int, Character.TYPE);
	    _internal2Java.put(Type.Int, Byte.TYPE);
	    _internal2Java.put(Type.Int, Short.TYPE);
	    _internal2Java.put(Type.Int, Integer.TYPE);
	    _internal2Java.put(Type.Int, Long.TYPE);
	    _internal2Java.put(Type.Int, Float.TYPE);
	    _internal2Java.put(Type.Int, Double.TYPE);

	    _internal2Java.put(Type.Real, Character.TYPE);
	    _internal2Java.put(Type.Real, Byte.TYPE);
	    _internal2Java.put(Type.Real, Short.TYPE);
	    _internal2Java.put(Type.Real, Integer.TYPE);
	    _internal2Java.put(Type.Real, Long.TYPE);
	    _internal2Java.put(Type.Real, Float.TYPE);
	    _internal2Java.put(Type.Real, Double.TYPE);

	    _internal2Java.put(Type.String, stringClass);

	    _internal2Java.put(Type.Node, nodeClass);
	    _internal2Java.put(Type.Node, nodeListClass);

	    _internal2Java.put(Type.NodeSet, Integer.TYPE);
	    _internal2Java.put(Type.NodeSet, nodeClass);
	    _internal2Java.put(Type.NodeSet, nodeListClass);

	    _internal2Java.put(Type.ResultTree, nodeClass);
	    _internal2Java.put(Type.ResultTree, nodeListClass);

	    _internal2Java.put(Type.Reference, objectClass);

	    // Possible conversions between Java and internal types
	    _java2Internal.put(Boolean.TYPE, Type.Boolean);

	    _java2Internal.put(Character.TYPE, Type.Real);
	    _java2Internal.put(Byte.TYPE, Type.Real);
	    _java2Internal.put(Short.TYPE, Type.Real);
	    _java2Internal.put(Integer.TYPE, Type.Real);
	    _java2Internal.put(Long.TYPE, Type.Real);
	    _java2Internal.put(Float.TYPE, Type.Real);
	    _java2Internal.put(Double.TYPE, Type.Real);

	    _java2Internal.put(stringClass, Type.String);

	    _java2Internal.put(objectClass, Type.Reference);

	    // Conversions from org.w3c.dom.Node/NodeList are not supported
	}
	catch (ClassNotFoundException e) {
	    System.err.println(e);
	}
    }
		
    public FunctionCall(QName fname, Vector arguments) {
	_fname = fname;
	_arguments = arguments;
	_type = null;
    }

    public FunctionCall(QName fname) {
	this(fname, EMPTY_ARG_LIST);
    }

    public String getName() {
	return(_fname.toString());
    }

    public void setParser(Parser parser) {
	super.setParser(parser);
	if (_arguments != null) {
	    final int n = _arguments.size();
	    for (int i = 0; i < n; i++) {
		final Expression exp = (Expression)_arguments.elementAt(i);
		exp.setParser(parser);
		exp.setParent(this);
	    }
	}
    }

    public String getClassNameFromUri(String uri) 
	throws TypeCheckError
    {
	final int length = 
	    uri.startsWith(JAVA_EXT_XSLTC) ? JAVA_EXT_XSLTC.length() + 1 :
	    uri.startsWith(JAVA_EXT_XALAN) ? JAVA_EXT_XALAN.length() + 1 : 0;

	if (length == 0) {
	    throw new TypeCheckError(this);
	}
	return (uri.length() > length) ? uri.substring(length) : EMPTYSTRING;
    }

    /**
     * Type check a function call. Since different type conversions apply,
     * type checking is different for standard and external (Java) functions.
     */
    public Type typeCheck(SymbolTable stable) 
	throws TypeCheckError 
    {
	if (_type != null) return _type;

	final String namespace = _fname.getNamespace();
	final String local = _fname.getLocalPart();

	if (isExtension()) {
	    _fname = new QName(null, null, local);
	    return typeCheckStandard(stable);
	}
	else if (isStandard()) {
	    return typeCheckStandard(stable);
	}
	// Handle extension functions (they all have a namespace)
	else {
	    try {
		_className = getClassNameFromUri(namespace);

		final int pos = local.lastIndexOf('.');
		if (pos > 0) {
		    _className = _className + local.substring(0, pos);
		    _fname = new QName(namespace, null, local.substring(pos + 1));
		}
		else {
		    _fname = new QName(namespace, null, local);
		}
		if (_className.length() > 0) {
		    return typeCheckExternal(stable);
		}
	    } 
	    catch (TypeCheckError e) {
		ErrorMsg errorMsg = e.getErrorMsg();
		if (errorMsg == null) {
		    final String name = _fname.getLocalPart();
		    errorMsg = new ErrorMsg(ErrorMsg.METHOD_NOT_FOUND_ERR, name);
		}
		getParser().reportError(ERROR, errorMsg);
		return _type = Type.Void;
	    }

	    /*
	     * Warn user if external function could not be resolved.
	     * Warning will _NOT_ be issued is the call is properly
	     * wrapped in an <xsl:if> or <xsl:when> element. For details
	     * see If.parserContents() and When.parserContents()
	     */
	    final Parser parser = getParser();
	    if (parser != null) {
		reportWarning(this, parser, ErrorMsg.FUNCTION_RESOLVE_ERR,
			      _fname.toString());
	    }
	    unresolvedExternal = true;
	    return _type = Type.Int;	// use "Int" as "unknown"
	}
    }

    /**
     * Type check a call to a standard function. Insert CastExprs when needed.
     * If as a result of the insertion of a CastExpr a type check error is 
     * thrown, then catch it and re-throw it with a new "this".
     */
    public Type typeCheckStandard(SymbolTable stable) throws TypeCheckError {
	_fname.clearNamespace(); 	// HACK!!!

	final int n = _arguments.size();
	final Vector argsType = typeCheckArgs(stable);
	final MethodType args = new MethodType(Type.Void, argsType);
	final MethodType ptype =
	    lookupPrimop(stable, _fname.getLocalPart(), args);

	if (ptype != null) {
	    for (int i = 0; i < n; i++) {
		final Type argType = (Type) ptype.argsType().elementAt(i);
		final Expression exp = (Expression)_arguments.elementAt(i);
		if (!argType.identicalTo(exp.getType())) {
		    try {
			_arguments.setElementAt(new CastExpr(exp, argType), i);
		    }
		    catch (TypeCheckError e) {
			throw new TypeCheckError(this);	// invalid conversion
		    }
		}
	    }
	    _chosenMethodType = ptype;
	    return _type = ptype.resultType();
	}
	throw new TypeCheckError(this);
    }

    /**
     * Type check a call to an external (Java) method.
     * The method must be static an public, and a legal type conversion
     * must exist for all its arguments and its return type.
     * Every method of name <code>_fname</code> is inspected
     * as a possible candidate.
     */
    public Type typeCheckExternal(SymbolTable stable) throws TypeCheckError {
	final Vector methods = findMethods();
	
	if (methods == null) {
	    // Method not found in this class
	    final String name = _fname.getLocalPart();
	    throw new TypeCheckError(ErrorMsg.METHOD_NOT_FOUND_ERR, name);
	}

	Class extType = null;
	final int nMethods = methods.size();
	final int nArgs = _arguments.size();
	final Vector argsType = typeCheckArgs(stable);

	// Try all methods with the same name as this function
	for (int j, i = 0; i < nMethods; i++) {

	    // Check if all paramteters to this method can be converted
	    final Method method = (Method)methods.elementAt(i);
	    final Class[] paramTypes = method.getParameterTypes();

	    for (j = 0; j < nArgs; j++) {
		// Convert from internal (translet) type to external (Java) type
		extType = paramTypes[j];
		final Type intType = (Type)argsType.elementAt(j);
		if (!_internal2Java.maps(intType, extType)) break;
	    }

	    if (j == nArgs) {
		// Check if the return type can be converted
		extType = method.getReturnType();
		_type = extType.getName().equals("void") ? Type.Void
		    : (Type) _java2Internal.get(extType);

		// Use this method if all parameters & return type match
		if (_type != null) {
		    _chosenMethod = method;
		    return _type;
		}
	    }
	}

	final StringBuffer buf = new StringBuffer(_className);
	buf.append('.').append(_fname.getLocalPart()).append('(');
	for (int i = 0; i < nArgs; i++) {
	    final Type intType = (Type)argsType.elementAt(i);
	    buf.append(intType.toString());
	    if (i < nArgs - 1) buf.append(", ");
	}
	buf.append(')');
	throw new TypeCheckError(ErrorMsg.ARGUMENT_CONVERSION_ERR, 
	    buf.toString());
    }

    /**
     * Type check the actual arguments of this function call.
     */
    public Vector typeCheckArgs(SymbolTable stable) throws TypeCheckError {
	final Vector result = new Vector();
	final Enumeration e = _arguments.elements();	
	while (e.hasMoreElements()) {
	    final Expression exp = (Expression)e.nextElement();
	    result.addElement(exp.typeCheck(stable));
	}
	return result;
    }

    protected final Expression argument(int i) {
	return (Expression)_arguments.elementAt(i);
    }

    protected final Expression argument() {
	return argument(0);
    }
    
    protected final int argumentCount() {
	return _arguments.size();
    }

    protected final void setArgument(int i, Expression exp) {
	_arguments.setElementAt(exp, i);
    }

    /**
     * Compile the function call and treat as an expression
     * Update true/false-lists.
     */
    public void translateDesynthesized(ClassGenerator classGen,
				       MethodGenerator methodGen) 
    {
	Type type = Type.Boolean;
	if (_chosenMethodType != null)
	    type = _chosenMethodType.resultType();

	final InstructionList il = methodGen.getInstructionList();
	translate(classGen, methodGen);

	if ((type instanceof BooleanType) || (type instanceof IntType)) {
	    _falseList.add(il.append(new IFEQ(null)));
	}
    }


    /**
     * Translate a function call. The compiled code will leave the function's
     * return value on the JVM's stack.
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final int n = argumentCount();
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	int index;

	// Translate calls to methods in the BasisLibrary
	if (isStandard() || isExtension()) {
	    for (int i = 0; i < n; i++) {
		final Expression exp = argument(i);
		exp.translate(classGen, methodGen);
		exp.startResetIterator(classGen, methodGen);
	    }

	    // append "F" to the function's name
	    final String name = _fname.toString().replace('-', '_') + "F";
	    String args = Constants.EMPTYSTRING;

	    // Special precautions for some method calls
	    if (name.equals("sumF")) {
		args = DOM_INTF_SIG;
		il.append(methodGen.loadDOM());
	    }
	    else if (name.equals("normalize_spaceF")) {
		if (_chosenMethodType.toSignature(args).
		    equals("()Ljava/lang/String;")) {
		    args = "I"+DOM_INTF_SIG;
		    il.append(methodGen.loadContextNode());
		    il.append(methodGen.loadDOM());
		}
	    }

	    // Invoke the method in the basis library
	    index = cpg.addMethodref(BASIS_LIBRARY_CLASS, name,
				     _chosenMethodType.toSignature(args));
	    il.append(new INVOKESTATIC(index));
	}
	// Add call to BasisLibrary.unresolved_externalF() to generate
	// run-time error message for unsupported external functions
	else if (unresolvedExternal) {
	    index = cpg.addMethodref(BASIS_LIBRARY_CLASS,
				     "unresolved_externalF",
				     "(Ljava/lang/String;)V");
	    il.append(new PUSH(cpg, _fname.toString()));
	    il.append(new INVOKESTATIC(index));
	}
	// Invoke function calls that are handled in separate classes
	else {
	    final String clazz = _chosenMethod.getDeclaringClass().getName();
	    Class[] paramTypes = _chosenMethod.getParameterTypes();

	    for (int i = 0; i < n; i++) {
		final Expression exp = argument(i);
		exp.translate(classGen, methodGen);
		// Convert the argument to its Java type
		exp.startResetIterator(classGen, methodGen);
		exp.getType().translateTo(classGen, methodGen, paramTypes[i]);
	    }

	    final StringBuffer buffer = new StringBuffer();
	    buffer.append('(');
	    for (int i = 0; i < paramTypes.length; i++) {
		buffer.append(getSignature(paramTypes[i]));
	    }
	    buffer.append(')');
	    buffer.append(getSignature(_chosenMethod.getReturnType()));

	    index = cpg.addMethodref(clazz,
				     _fname.getLocalPart(),
				     buffer.toString());
	    il.append(new INVOKESTATIC(index));

	    // Convert the return type back to our internal type
	    _type.translateFrom(classGen, methodGen,
				_chosenMethod.getReturnType());
	}
    }

    public String toString() {
	return "funcall(" + _fname + ", " + _arguments + ')';
    }

    public boolean isStandard() {
	final String namespace = _fname.getNamespace();
	return (namespace == null) || (namespace.equals(Constants.EMPTYSTRING));
    }

    public boolean isExtension() {
	final String namespace = _fname.getNamespace();
	return (namespace != null) && (namespace.equals(EXT_XSLTC) 
	    || namespace.equals(EXT_XALAN));
    }

    /**
     * Returns a vector with all methods named <code>_fname</code>
     * after stripping its namespace or <code>null</code>
     * if no such methods exist.
     */
    private Vector findMethods() {
	Vector result = null;
	final String namespace = _fname.getNamespace();

	if (namespace.startsWith(JAVA_EXT_XSLTC) ||
	    namespace.startsWith(JAVA_EXT_XALAN)) {
	    final int nArgs = _arguments.size();
	    try {
		TransletLoader loader = new TransletLoader();
		final Class clazz = loader.loadClass(_className);

		if (clazz == null) {
		    final ErrorMsg msg =
			new ErrorMsg(ErrorMsg.CLASS_NOT_FOUND_ERR, _className);
		    getParser().reportError(Constants.ERROR, msg);
		}
		else {
		    final String methodName = _fname.getLocalPart();
		    final Method[] methods = clazz.getDeclaredMethods();

		    for (int i = 0; i < methods.length; i++) {
			final int mods = methods[i].getModifiers();

			// Is it public, static and same number of args ?
			if (Modifier.isPublic(mods)
			    && Modifier.isStatic(mods)
			    && methods[i].getName().equals(methodName)
			    && methods[i].getParameterTypes().length == nArgs)
			    {
				if (result == null) {
				    result = new Vector();
				}
				result.addElement(methods[i]);
			    }
		    }
		}
	    }
	    catch (ClassNotFoundException e) {
		final ErrorMsg msg =
		    new ErrorMsg(ErrorMsg.CLASS_NOT_FOUND_ERR, _className);
		getParser().reportError(Constants.ERROR, msg);
	    }
	}
	return result;
    }

    /**
     * Compute the JVM signature for the class.
     */
    static final String getSignature(Class clazz) {
	if (clazz.isArray()) {
	    final StringBuffer sb = new StringBuffer();
	    Class cl = clazz;
	    while (cl.isArray()) {
		sb.append("[");
		cl = cl.getComponentType();
	    }
	    sb.append(getSignature(cl));
	    return sb.toString();
	}
	else if (clazz.isPrimitive()) {
	    if (clazz == Integer.TYPE) {
		return "I";
	    }
	    else if (clazz == Byte.TYPE) {
		return "B";
	    }
	    else if (clazz == Long.TYPE) {
		return "J";
	    }
	    else if (clazz == Float.TYPE) {
		return "F";
	    }
	    else if (clazz == Double.TYPE) {
		return "D";
	    }
	    else if (clazz == Short.TYPE) {
		return "S";
	    }
	    else if (clazz == Character.TYPE) {
		return "C";
	    }
	    else if (clazz == Boolean.TYPE) {
		return "Z";
	    }
	    else if (clazz == Void.TYPE) {
		return "V";
	    }
	    else {
		final String name = clazz.toString();
		ErrorMsg err = new ErrorMsg(ErrorMsg.UNKNOWN_SIG_TYPE_ERR,name);
		throw new Error(err.toString());
	    }
	}
	else {
	    return "L" + clazz.getName().replace('.', '/') + ';';
	}
    }

    /**
     * Compute the JVM method descriptor for the method.
     */
    static final String getSignature(Method meth) {
	final StringBuffer sb = new StringBuffer();
	sb.append('(');
	final Class[] params = meth.getParameterTypes(); // avoid clone
	for (int j = 0; j < params.length; j++) {
	    sb.append(getSignature(params[j]));
	}
	return sb.append(')').append(getSignature(meth.getReturnType()))
	    .toString();
    }

    /**
     * Compute the JVM constructor descriptor for the constructor.
     */
    static final String getSignature(Constructor cons) {
	final StringBuffer sb = new StringBuffer();
	sb.append('(');
	final Class[] params = cons.getParameterTypes(); // avoid clone
	for (int j = 0; j < params.length; j++) {
	    sb.append(getSignature(params[j]));
	}
	return sb.append(")V").toString();
    }
}
