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
 *
 */

package org.apache.xalan.xsltc.compiler;

import java.util.Vector;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;
import java.text.Collator;

import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.ReferenceType;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;
import org.apache.bcel.Constants;

import org.apache.xalan.xsltc.dom.*;
import org.apache.xalan.xsltc.compiler.util.*;


final class Sort extends Instruction implements Closure {

    private Expression     _select;
    private AttributeValue _order;
    private AttributeValue _caseOrder;
    private AttributeValue _dataType;

    private String         _data = null;
    public  String         _lang;
    public  String         _country;

    private String _className = null;
    private ArrayList _closureVars = null;
    private boolean _needsSortRecordFactory = false;

    // -- Begin Closure interface --------------------

    /**
     * Returns true if this closure is compiled in an inner class (i.e.
     * if this is a real closure).
     */
    public boolean inInnerClass() {
	return (_className != null);
    }

    /**
     * Returns a reference to its parent closure or null if outermost.
     */
    public Closure getParentClosure() {
	return null;
    }

    /**
     * Returns the name of the auxiliary class or null if this predicate 
     * is compiled inside the Translet.
     */
    public String getInnerClassName() {
	return _className;
    }

    /**
     * Add new variable to the closure.
     */
    public void addVariable(VariableRefBase variableRef) {
	if (_closureVars == null) {
	    _closureVars = new ArrayList();
	}

	// Only one reference per variable
	if (!_closureVars.contains(variableRef)) {
	    _closureVars.add(variableRef);
	    _needsSortRecordFactory = true;
	}
    }

    // -- End Closure interface ----------------------

    private void setInnerClassName(String className) {
	_className = className;
    }

    /**
     * Parse the attributes of the xsl:sort element
     */
    public void parseContents(Parser parser) {

	final SyntaxTreeNode parent = getParent();
	if (!(parent instanceof ApplyTemplates) &&
	    !(parent instanceof ForEach)) {
	    reportError(this, parser, ErrorMsg.STRAY_SORT_ERR, null);
	    return;
	}

	// Parse the select expression (node string value if no expression)
	_select = parser.parseExpression(this, "select", "string(.)");

	// Get the sort order; default is 'ascending'
	String val = getAttribute("order");
	if (val.length() == 0) val = "ascending";
	_order = AttributeValue.create(this, val, parser);

	// Get the case order; default is language dependant
	val = getAttribute("case-order");
	if (val.length() == 0) val = "upper-first";
	_caseOrder = AttributeValue.create(this, val, parser);

	// Get the sort data type; default is text
	val = getAttribute("data-type");
	if (val.length() == 0) {
	    try {
		final Type type = _select.typeCheck(parser.getSymbolTable());
		if (type instanceof IntType)
		    val = "number";
		else
		    val = "text";
	    }
	    catch (TypeCheckError e) {
		val = "text";
	    }
	}
	_dataType = AttributeValue.create(this, val, parser);

	// Get the language whose sort rules we will use; default is env.dep.
	if ((val = getAttribute("lang")) != null) {
	    try {
		StringTokenizer st = new StringTokenizer(val,"-",false);
		_lang = st.nextToken();
		_country = st.nextToken();
	    }
	    catch (NoSuchElementException e) { // ignore
	    }
	}
    }
    
    /**
     * Run type checks on the attributes; expression must return a string
     * which we will use as a sort key
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	final Type tselect = _select.typeCheck(stable);

	// If the sort data-type is not set we use the natural data-type
	// of the data we will sort
	if (!(tselect instanceof StringType)) {
	    _select = new CastExpr(_select, Type.String);
	}

	_order.typeCheck(stable);
	_caseOrder.typeCheck(stable);
	_dataType.typeCheck(stable);
	return Type.Void;
    }

    /**
     * These two methods are needed in the static methods that compile the
     * overloaded NodeSortRecord.compareType() and NodeSortRecord.sortOrder()
     */
    public void translateSortType(ClassGenerator classGen,
				  MethodGenerator methodGen) {
	_dataType.translate(classGen, methodGen);
    }
    
    public void translateSortOrder(ClassGenerator classGen,
				   MethodGenerator methodGen) {
	_order.translate(classGen, methodGen);
    }
    
    /**
     * This method compiles code for the select expression for this
     * xsl:sort element. The method is called from the static code-generating
     * methods in this class.
     */
    public void translateSelect(ClassGenerator classGen,
				MethodGenerator methodGen) {
	_select.translate(classGen,methodGen);
    }

    /**
     * This method should not produce any code
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	// empty
    }

    /**
     * Compiles code that instantiates a SortingIterator object.
     * This object's constructor needs referencdes to the current iterator
     * and a node sort record producing objects as its parameters.
     */
    public static void translateSortIterator(ClassGenerator classGen,
				      MethodGenerator methodGen,
				      Expression nodeSet,
				      Vector sortObjects) 
    {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	// SortingIterator.SortingIterator(NodeIterator,NodeSortRecordFactory);
	final int init = cpg.addMethodref(SORT_ITERATOR, "<init>",
					  "("
					  + NODE_ITERATOR_SIG
					  + NODE_SORT_FACTORY_SIG
					  + ")V");	

	il.append(new NEW(cpg.addClass(SORT_ITERATOR)));
	il.append(DUP);

	// Get the current node iterator
	if (nodeSet == null) {	// apply-templates default
	    final int children = cpg.addInterfaceMethodref(DOM_INTF,
							   "getAxisIterator",
							   "(I)"+
							   NODE_ITERATOR_SIG);
	    il.append(methodGen.loadDOM());
	    il.append(new PUSH(cpg, Axis.CHILD));
	    il.append(new INVOKEINTERFACE(children, 2));
	}
	else {
	    nodeSet.translate(classGen, methodGen);
	}
	
	// Compile the code for the NodeSortRecord producing class and pass
	// that as the last argument to the SortingIterator constructor.
	compileSortRecordFactory(sortObjects, classGen, methodGen);
	il.append(new INVOKESPECIAL(init));
    }


    /**
     * Compiles code that instantiates a NodeSortRecordFactory object which
     * will produce NodeSortRecord objects of a specific type.
     */
    public static void compileSortRecordFactory(Vector sortObjects,
	ClassGenerator classGen, MethodGenerator methodGen) 
    {
	String sortRecordClass = 
	    compileSortRecord(sortObjects, classGen, methodGen);

	boolean needsSortRecordFactory = false;
	final int nsorts = sortObjects.size();
	for (int i = 0; i < nsorts; i++) {
	    final Sort sort = (Sort) sortObjects.elementAt(i);
	    needsSortRecordFactory |= sort._needsSortRecordFactory;
	}

	String sortRecordFactoryClass = NODE_SORT_FACTORY;
	if (needsSortRecordFactory) {
	    sortRecordFactoryClass = 
		compileSortRecordFactory(sortObjects, classGen, methodGen, 
		    sortRecordClass);
	}

	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	
	il.append(new NEW(cpg.addClass(sortRecordFactoryClass)));
	il.append(DUP);
	il.append(methodGen.loadDOM());
	il.append(new PUSH(cpg, sortRecordClass));
	il.append(classGen.loadTranslet());

	// Compile code that initializes the static _sortOrder
	il.append(new PUSH(cpg, nsorts));
	il.append(new ANEWARRAY(cpg.addClass(STRING)));
	for (int level = 0; level < nsorts; level++) {
	    final Sort sort = (Sort)sortObjects.elementAt(level);
	    il.append(DUP);
	    il.append(new PUSH(cpg, level));
	    sort.translateSortOrder(classGen, methodGen);
	    il.append(AASTORE);
	}

	il.append(new PUSH(cpg, nsorts));
	il.append(new ANEWARRAY(cpg.addClass(STRING)));
	for (int level = 0; level < nsorts; level++) {
	    final Sort sort = (Sort)sortObjects.elementAt(level);
	    il.append(DUP);
	    il.append(new PUSH(cpg, level));
	    sort.translateSortType(classGen, methodGen);
	    il.append(AASTORE);
	}

	il.append(new INVOKESPECIAL(
	    cpg.addMethodref(sortRecordFactoryClass, "<init>", 
		"(" + DOM_INTF_SIG 
		    + STRING_SIG
		    + TRANSLET_INTF_SIG
		    + "[" + STRING_SIG
		    + "[" + STRING_SIG + ")V")));

	// Initialize closure variables in sortRecordFactory
	final ArrayList dups = new ArrayList();

	for (int j = 0; j < nsorts; j++) {
	    final Sort sort = (Sort) sortObjects.get(j);
	    final int length = (sort._closureVars == null) ? 0 : 
		sort._closureVars.size();

	    for (int i = 0; i < length; i++) {
		VariableRefBase varRef = (VariableRefBase) sort._closureVars.get(i);

		// Discard duplicate variable references
		if (dups.contains(varRef)) continue;

		final VariableBase var = varRef.getVariable();

		// Store variable in new closure
		il.append(DUP);
		il.append(var.loadInstruction());
		il.append(new PUTFIELD(
			cpg.addFieldref(sortRecordFactoryClass, var.getVariable(), 
			    var.getType().toSignature())));
		dups.add(varRef);
	    }
	}
    }

    public static String compileSortRecordFactory(Vector sortObjects,
	ClassGenerator classGen, MethodGenerator methodGen, 
	String sortRecordClass)
    {
	final XSLTC  xsltc = ((Sort)sortObjects.firstElement()).getXSLTC();
	final String className = xsltc.getHelperClassName();

	final NodeSortRecordFactGenerator sortRecordFactory =
	    new NodeSortRecordFactGenerator(className,
					NODE_SORT_FACTORY,
					className + ".java",
					ACC_PUBLIC | ACC_SUPER | ACC_FINAL,
					new String[] {},
					classGen.getStylesheet());

	ConstantPoolGen cpg = sortRecordFactory.getConstantPool();

	// Add a new instance variable for each var in closure
	final int nsorts = sortObjects.size();
	final ArrayList dups = new ArrayList();

	for (int j = 0; j < nsorts; j++) {
	    final Sort sort = (Sort) sortObjects.get(j);
	    final int length = (sort._closureVars == null) ? 0 : 
		sort._closureVars.size();

	    for (int i = 0; i < length; i++) {
		final VariableRef varRef = (VariableRef) sort._closureVars.get(i);

		// Discard duplicate variable references
		if (dups.contains(varRef)) continue;

		final VariableBase var = varRef.getVariable();
		sortRecordFactory.addField(new Field(ACC_PUBLIC, 
					   cpg.addUtf8(var.getVariable()),
					   cpg.addUtf8(var.getType().toSignature()),
					   null, cpg.getConstantPool()));
		dups.add(varRef);
	    }
	}

	// Define a constructor for this class
	final org.apache.bcel.generic.Type[] argTypes = 
	    new org.apache.bcel.generic.Type[5];
	argTypes[0] = Util.getJCRefType(DOM_INTF_SIG);
	argTypes[1] = Util.getJCRefType(STRING_SIG);
	argTypes[2] = Util.getJCRefType(TRANSLET_INTF_SIG);
	argTypes[3] = Util.getJCRefType("[" + STRING_SIG);
	argTypes[4] = Util.getJCRefType("[" + STRING_SIG);

	final String[] argNames = new String[5];
	argNames[0] = DOCUMENT_PNAME;
	argNames[1] = "className";
	argNames[2] = TRANSLET_PNAME;
	argNames[3] = "order";
	argNames[4] = "type";

	InstructionList il = new InstructionList();
	final MethodGenerator constructor =
	    new MethodGenerator(ACC_PUBLIC,
				org.apache.bcel.generic.Type.VOID, 
				argTypes, argNames, "<init>", 
				className, il, cpg);

	// Push all parameters onto the stack and called super.<init>()
	il.append(ALOAD_0);
	il.append(ALOAD_1);
	il.append(ALOAD_2);
	il.append(new ALOAD(3));
	il.append(new ALOAD(4));
	il.append(new ALOAD(5));
	il.append(new INVOKESPECIAL(cpg.addMethodref(NODE_SORT_FACTORY,
	    "<init>", 
	    "(" + DOM_INTF_SIG 
		+ STRING_SIG 
		+ TRANSLET_INTF_SIG 
		+ "[" + STRING_SIG
		+ "[" + STRING_SIG + ")V")));
	il.append(RETURN);

	// Override the definition of makeNodeSortRecord()
	il = new InstructionList(); 
	final MethodGenerator makeNodeSortRecord =
	    new MethodGenerator(ACC_PUBLIC,
		Util.getJCRefType(NODE_SORT_RECORD_SIG), 
		new org.apache.bcel.generic.Type[] { 
		    org.apache.bcel.generic.Type.INT,
		    org.apache.bcel.generic.Type.INT },
		new String[] { "node", "last" }, "makeNodeSortRecord",
		className, il, cpg);

	il.append(ALOAD_0);
	il.append(ILOAD_1);
	il.append(ILOAD_2);
	il.append(new INVOKESPECIAL(cpg.addMethodref(NODE_SORT_FACTORY,
	    "makeNodeSortRecord", "(II)" + NODE_SORT_RECORD_SIG)));
	il.append(DUP);
	il.append(new CHECKCAST(cpg.addClass(sortRecordClass)));

	// Initialize closure in record class
	final int ndups = dups.size();
	for (int i = 0; i < ndups; i++) {
	    final VariableRef varRef = (VariableRef) dups.get(i);
	    final VariableBase var = varRef.getVariable();
	    final Type varType = var.getType();
	    
	    il.append(DUP);

	    // Get field from factory class
	    il.append(ALOAD_0);
	    il.append(new GETFIELD(
		cpg.addFieldref(className,
		    var.getVariable(), varType.toSignature())));

	    // Put field in record class
	    il.append(new PUTFIELD(
		cpg.addFieldref(sortRecordClass,
		    var.getVariable(), varType.toSignature())));
	}
	il.append(POP);
	il.append(ARETURN);

	constructor.setMaxLocals();
	constructor.setMaxStack();
	sortRecordFactory.addMethod(constructor.getMethod());
	makeNodeSortRecord.setMaxLocals();
	makeNodeSortRecord.setMaxStack();
	sortRecordFactory.addMethod(makeNodeSortRecord.getMethod());
	xsltc.dumpClass(sortRecordFactory.getJavaClass());

	return className;
    }

    /**
     * Create a new auxillary class extending NodeSortRecord.
     */
    private static String compileSortRecord(Vector sortObjects,
					    ClassGenerator classGen,
					    MethodGenerator methodGen) {
	final XSLTC  xsltc = ((Sort)sortObjects.firstElement()).getXSLTC();
	final String className = xsltc.getHelperClassName();

	// This generates a new class for handling this specific sort
	final NodeSortRecordGenerator sortRecord =
	    new NodeSortRecordGenerator(className,
					NODE_SORT_RECORD,
					"sort$0.java",
					ACC_PUBLIC | ACC_SUPER | ACC_FINAL,
					new String[] {},
					classGen.getStylesheet());
	
	final ConstantPoolGen cpg = sortRecord.getConstantPool();	

	// Add a new instance variable for each var in closure
	final int nsorts = sortObjects.size();
	final ArrayList dups = new ArrayList();

	for (int j = 0; j < nsorts; j++) {
	    final Sort sort = (Sort) sortObjects.get(j);

	    // Set the name of the inner class in this sort object
	    sort.setInnerClassName(className);	

	    final int length = (sort._closureVars == null) ? 0 : 
		sort._closureVars.size();
	    for (int i = 0; i < length; i++) {
		final VariableRef varRef = (VariableRef) sort._closureVars.get(i);

		// Discard duplicate variable references
		if (dups.contains(varRef)) continue;

		final VariableBase var = varRef.getVariable();
		sortRecord.addField(new Field(ACC_PUBLIC, 
				    cpg.addUtf8(var.getVariable()),
				    cpg.addUtf8(var.getType().toSignature()),
				    null, cpg.getConstantPool()));
		dups.add(varRef);
	    }
	}

	Method clinit = compileClassInit(sortObjects, sortRecord,
					 cpg, className);
	Method extract = compileExtract(sortObjects, sortRecord,
					cpg, className);
	sortRecord.addMethod(clinit);
	sortRecord.addEmptyConstructor(ACC_PUBLIC);
	sortRecord.addMethod(extract);

	// Overload NodeSortRecord.getCollator() only if needed
	for (int i = 0; i < sortObjects.size(); i++) {
	    if (((Sort)(sortObjects.elementAt(i)))._lang != null) {
		sortRecord.addMethod(compileGetCollator(sortObjects,
							sortRecord,
							cpg,
							className));
		i = sortObjects.size();
	    }
	}
	
	xsltc.dumpClass(sortRecord.getJavaClass());
	return className;
    }

    /**
     * Create a class constructor for the new class. All this constructor does
     * is to initialize a couple of tables that contain information on sort
     * order and sort type. These static tables cannot be in the parent class.
     */
    private static Method compileClassInit(Vector sortObjects,
					   NodeSortRecordGenerator sortRecord,
					   ConstantPoolGen cpg,
					   String className) {
	// Class initializer - void NodeSortRecord.<clinit>();
	final InstructionList il = new InstructionList();
	final CompareGenerator classInit =
	    new CompareGenerator(ACC_PUBLIC | ACC_STATIC,
				 org.apache.bcel.generic.Type.VOID, 
				 new org.apache.bcel.generic.Type[] { },
				 new String[] { },
				 "<clinit>", className, il, cpg);

	final int initLocale =  cpg.addMethodref("java/util/Locale",
						 "<init>",
						 "(Ljava/lang/String;"+
						 "Ljava/lang/String;)V");
	
	final int getCollator = cpg.addMethodref(COLLATOR_CLASS,
						 "getInstance",
						 "(Ljava/util/Locale;)"+
						 COLLATOR_SIG);

	final int setStrength = cpg.addMethodref(COLLATOR_CLASS,
						 "setStrength", "(I)V");

	final int levels = sortObjects.size();

	/*
	final int levelsField = cpg.addFieldref(className, "_levels", "I");
	il.append(new PUSH(cpg, levels));
	il.append(new PUTSTATIC(levelsField));
	*/

	// Compile code that initializes the locale
	String language = null;
	String country = null;
	Sort sort = (Sort)sortObjects.elementAt(0);

	for (int level = 0; level < levels; level++) {
	    if (language == null && sort._lang != null)
		language = sort._lang;
	    if (country == null && sort._country != null)
		country = sort._country;
	}

	// Get index to private static reference in NodeSortRecrd
	final int collator =
	    cpg.addFieldref(className, "_collator", COLLATOR_SIG);

	if (language != null) {
	    // Create new Locale object on stack
	    il.append(new NEW(cpg.addClass("java/util/Locale")));
	    il.append(DUP);
	    il.append(new PUSH(cpg, language));
	    il.append(new PUSH(cpg, (country != null ? country : EMPTYSTRING)));
	    il.append(new INVOKESPECIAL(initLocale));
	    
	    // Use that Locale object to get the required Collator object
	    il.append(new INVOKESTATIC(getCollator));
	    il.append(new PUTSTATIC(collator));
	}

	il.append(new GETSTATIC(collator));
	il.append(new ICONST(Collator.TERTIARY));
	il.append(new INVOKEVIRTUAL(setStrength));

	il.append(RETURN);

	classInit.stripAttributes(true);
	classInit.setMaxLocals();
	classInit.setMaxStack();
	classInit.removeNOPs();

	return classInit.getMethod();
    }


    /**
     * Compiles a method that overloads NodeSortRecord.extractValueFromDOM()
     */
    private static Method compileExtract(Vector sortObjects,
					 NodeSortRecordGenerator sortRecord,
					 ConstantPoolGen cpg,
					 String className) {
	final InstructionList il = new InstructionList();
	
	// String NodeSortRecord.extractValueFromDOM(dom,node,level);
	final CompareGenerator extractMethod =
	    new CompareGenerator(ACC_PUBLIC | ACC_FINAL,
				 org.apache.bcel.generic.Type.STRING, 
				 new org.apache.bcel.generic.Type[] {
		                     Util.getJCRefType(DOM_INTF_SIG),
				     org.apache.bcel.generic.Type.INT,
				     org.apache.bcel.generic.Type.INT,
				     Util.getJCRefType(TRANSLET_SIG),
				     org.apache.bcel.generic.Type.INT
				 },
				 new String[] { "dom",
						"current",
						"level",
						"translet",
						"last"
				 },
				 "extractValueFromDOM", className, il, cpg);

	// Values needed for the switch statement
	final int levels = sortObjects.size();
	final int match[] = new int[levels];
	final InstructionHandle target[] = new InstructionHandle[levels];
	InstructionHandle tblswitch = null;

	// Compile switch statement only if the key has multiple levels
	if (levels > 1) {
	    // Put the parameter to the swtich statement on the stack
	    il.append(new ILOAD(extractMethod.getLocalIndex("level")));
	    // Append the switch statement here later on
	    tblswitch = il.append(new NOP());
	}

	// Append all the cases for the switch statment
	for (int level = 0; level < levels; level++) {
	    match[level] = level;
	    final Sort sort = (Sort)sortObjects.elementAt(level);
	    target[level] = il.append(NOP);
	    sort.translateSelect(sortRecord, extractMethod);
	    il.append(ARETURN);
	}
	
	// Compile def. target for switch statement if key has multiple levels
	if (levels > 1) {
	    // Append the default target - it will _NEVER_ be reached
	    InstructionHandle defaultTarget =
		il.append(new PUSH(cpg, EMPTYSTRING));
	    il.insert(tblswitch,new TABLESWITCH(match, target, defaultTarget));
	    il.append(ARETURN);
	}

	extractMethod.stripAttributes(true);
	extractMethod.setMaxLocals();
	extractMethod.setMaxStack();
	extractMethod.removeNOPs();

	return extractMethod.getMethod();
    }

    /**
     * Compiles a method that overloads NodeSortRecord.getCollator()
     * This method is only compiled if the "lang" attribute is used.
     */
    private static Method compileGetCollator(Vector sortObjects,
					     NodeSortRecordGenerator sortRecord,
					     ConstantPoolGen cpg,
					     String className) {
	final InstructionList il = new InstructionList();
	// Collator NodeSortRecord.getCollator();
	final MethodGenerator getCollator =
	    new MethodGenerator(ACC_PUBLIC | ACC_FINAL,
				Util.getJCRefType(COLLATOR_SIG),
				new org.apache.bcel.generic.Type[] {},
				new String[] { },
				"getCollator", className, il, cpg);

	// Get index to private static reference in NodeSortRecrd
	final int collator =
	    cpg.addFieldref(className, "collator", COLLATOR_SIG);
	// Feck the Collator object on the stack and return it
	il.append(new GETSTATIC(collator));
	il.append(ARETURN);

	getCollator.stripAttributes(true);
	getCollator.setMaxLocals();
	getCollator.setMaxStack();
	getCollator.removeNOPs();

	return getCollator.getMethod();
    }
}
