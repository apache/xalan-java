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
import java.util.Hashtable;
import java.util.Enumeration;
import org.apache.xalan.xsltc.compiler.util.Type;
import de.fub.bytecode.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;
import org.apache.xalan.xsltc.DOM;

/**
 * Mode gathers all the templates belonging to a given mode; it is responsible
 * for generating an appropriate applyTemplates + (mode name) function
 */
final class Mode implements Constants {
    private final QName _name;
    
    // the owning stylesheet
    private final Stylesheet _stylesheet;

    private final String _functionName;
    
    // all the templates in this mode
    private final Vector _templates = new Vector();
    
    // Patterns/test sequences for the stylesheet's templates
    private Vector[]  _patternGroups = new Vector[32];
    private TestSeq[] _code2TestSeq;
    // Pattern/test sequence for pattern with 'node()' kernel
    private Vector    _nodeGroup = null;
    private TestSeq   _nodeTestSeq = null;

    private int _currentIndex;

    private final Hashtable _neededTemplates = new Hashtable();
    private final Hashtable _namedTemplates = new Hashtable();
    private final Hashtable _templateInstructionHandles = new Hashtable();
    private final Hashtable _templateInstructionLists = new Hashtable();
    private LocationPathPattern _explicitRootPattern = null;
	
    public Mode(QName name, Stylesheet stylesheet, String suffix) {
	_name = name;
	_stylesheet = stylesheet;
	_functionName = APPLY_TEMPLATES + suffix;
    }

    public String functionName() {
	return _functionName;
    }

    private String getClassName() {
	return _stylesheet.getClassName();
    }

    public void addTemplate(Template template) {
	_templates.addElement(template);
	Util.println("added template, pattern: "+ template.getPattern());
    }

    public void processPatterns(Hashtable keys) {
	// Traverse all templates
	final Enumeration templates = _templates.elements();
	while (templates.hasMoreElements()) {
	    final Template template = (Template)templates.nextElement();
	    // Is this a named template?
	    if (template.isNamed()) {
		// Only process template with highest priority when there
		// are multiple templates with the sanme name
		if (!template.disabled()) _namedTemplates.put(template, this);
	    }
	    processTemplatePattern(template,keys);
	}
	prepareTestSequences();
    }
		
    private void processTemplatePattern(Template template, Hashtable keys) {
	final Pattern matchPattern = template.getPattern();
	if (matchPattern != null)
	    flattenAlternative(matchPattern, template, keys);
    }
		
    private void flattenAlternative(Pattern pattern,
				    Template template,
				    Hashtable keys) {

	if (pattern instanceof IdKeyPattern) {
	    // TODO: Cannot handle this kind of core pattern yet!!!
	}
	else if (pattern instanceof AlternativePattern) {
	    final AlternativePattern alt = (AlternativePattern)pattern;
	    flattenAlternative(alt.getLeft(), template, keys);
	    flattenAlternative(alt.getRight(), template, keys);
	}
	else if (pattern instanceof LocationPathPattern) {
	    final LocationPathPattern lpp = (LocationPathPattern)pattern;
	    lpp.setTemplate(template);
	    addPatternToGroup(lpp);
	}
	else
	    Util.println("Bad pattern: " + pattern);
    }
    
    private void addPattern(int code, LocationPathPattern pattern) {
	if (code >= _patternGroups.length) {
	    Vector[] newGroups = new Vector[code*2];
	    System.arraycopy(_patternGroups, 0, newGroups, 0,
			     _patternGroups.length);
	    _patternGroups = newGroups;
	}
	
	Vector patterns = code == -1
	    ? _nodeGroup	// node()
	    : _patternGroups[code];
	
	if (patterns == null) {
	    patterns = new Vector(2);
	    patterns.addElement(pattern);
	}
	else {	// keep patterns ordered by diminishing precedence/priorities
	    boolean inserted = false;
	    for (int i = 0; i < patterns.size(); i++) {
		final LocationPathPattern lppToCompare =
		    (LocationPathPattern)patterns.elementAt(i);
		if (pattern.noSmallerThan(lppToCompare)) {
		    inserted = true;
		    patterns.insertElementAt(pattern, i);
		    break;
		}
	    }
	    if (inserted == false) {
		patterns.addElement(pattern);
	    }
	}
	if (code == -1) {
	    _nodeGroup = patterns;
	}
	else {
	    _patternGroups[code] = patterns;
	}
    }
    
    /**
     * Group patterns by NodeTests of their last Step
     * Keep them sorted by priority within group
     */
    private void addPatternToGroup(final LocationPathPattern lpp) {
	// kernel pattern is the last (maybe only) Step
	final StepPattern kernel = lpp.getKernelPattern();
	if (kernel != null) {
	    addPattern(kernel.getNodeType(), lpp);
	}
	else if (_explicitRootPattern == null ||
		 lpp.noSmallerThan(_explicitRootPattern)) {
	    _explicitRootPattern = lpp;
	}
    }

    /**
     * Build test sequences
     */
    private void prepareTestSequences() {
	final Vector names = _stylesheet.getXSLTC().getNamesIndex();
	_code2TestSeq = new TestSeq[DOM.NTYPES + names.size()];
	
	final int n = _patternGroups.length;
	for (int i = 0; i < n; i++) {
	    final Vector patterns = _patternGroups[i];
	    if (patterns != null) {
		final TestSeq testSeq = new TestSeq(patterns, this);
		testSeq.reduce();
		_code2TestSeq[i] = testSeq;
		testSeq.findTemplates(_neededTemplates);
	    }
	}

	if ((_nodeGroup != null) && (_nodeGroup.size() > 0)) {
	    _nodeTestSeq = new TestSeq(_nodeGroup, this);
	    _nodeTestSeq.reduce();
	    _nodeTestSeq.findTemplates(_neededTemplates);
	}
	
	if (_explicitRootPattern != null) {
	    // doesn't matter what is 'put', only key matters
	    _neededTemplates.put(_explicitRootPattern.getTemplate(), this);
	}
    }

    private void compileNamedTemplate(Template template,
				      ClassGenerator classGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = new InstructionList();
	final String DOM_CLASS_SIG = classGen.getDOMClassSig();

	String methodName = template.getName().toString();
	methodName = methodName.replace('.', '$');
	methodName = methodName.replace('-', '$');

	final NamedMethodGenerator methodGen =
	    new NamedMethodGenerator(ACC_PUBLIC,
				     de.fub.bytecode.generic.Type.VOID,
				     new de.fub.bytecode.generic.Type[] {
					 Util.getJCRefType(DOM_CLASS_SIG),
					 Util.getJCRefType(NODE_ITERATOR_SIG),
					 Util.getJCRefType(TRANSLET_OUTPUT_SIG),
					 de.fub.bytecode.generic.Type.INT
				     },
				     new String[] {
					 DOCUMENT_PNAME,
					 ITERATOR_PNAME,
					 TRANSLET_OUTPUT_PNAME,
					 NODE_PNAME
				     },
				     methodName,
				     getClassName(),
				     il, cpg);
	
	il.append(template.compile(classGen, methodGen));
	il.append(RETURN);
	
	methodGen.stripAttributes(true);
	methodGen.setMaxLocals();
	methodGen.setMaxStack();
	methodGen.removeNOPs();
	classGen.addMethod(methodGen.getMethod());
    }

    private void compileTemplates(ClassGenerator classGen,
				  MethodGenerator methodGen,
				  InstructionHandle next) {
        Enumeration templates = _namedTemplates.keys();
        while (templates.hasMoreElements()) {
            final Template template = (Template)templates.nextElement();
            compileNamedTemplate(template, classGen);
        }

	templates = _neededTemplates.keys();
	while (templates.hasMoreElements()) {
	    final Template template = (Template)templates.nextElement();
	    if (template.hasContents()) {
		// !!! TODO templates both named and matched
		InstructionList til = template.compile(classGen, methodGen);
		til.append(new GOTO_W(next));
		_templateInstructionLists.put(template, til);
		_templateInstructionHandles.put(template, til.getStart());
	    }
	    else {
		// empty template
		_templateInstructionHandles.put(template, next);
	    }
	}
    }
	
    private void appendTemplateCode(InstructionList body) {
	final Enumeration templates = _neededTemplates.keys();
	while (templates.hasMoreElements()) {
	    final Object iList =
		_templateInstructionLists.get(templates.nextElement());
	    if (iList != null) {
		body.append((InstructionList)iList);
	    }
	}
    }

    private void appendTestSequences(InstructionList body) {
	final int n = _code2TestSeq.length;
	for (int i = 0; i < n; i++) {
	    final TestSeq testSeq = _code2TestSeq[i];
	    if (testSeq != null) {
		InstructionList il = testSeq.getInstructionList();
		if (il != null)
		    body.append(il);
		// else trivial TestSeq
	    }
	}
    }

    public static void compileGetChildren(ClassGenerator classGen,
					  MethodGenerator methodGen,
					  int node) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	final String DOM_CLASS = classGen.getDOMClass();
	il.append(methodGen.loadDOM());
	il.append(new ILOAD(node));
	il.append(new INVOKEVIRTUAL(cpg.addMethodref(DOM_CLASS,
						     GET_CHILDREN,
						     GET_CHILDREN_SIG)));
    }

    /**
     * Compiles the default handling for DOM elements: traverse all children
     */
    private InstructionList compileDefaultRecursion(ClassGenerator classGen,
						    MethodGenerator methodGen,
						    int node,
						    InstructionHandle next) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = new InstructionList();
	final String DOM_CLASS = classGen.getDOMClass();
	final String applyTemplatesSig = classGen.getApplyTemplatesSig();
	final int getChildren = cpg.addMethodref(DOM_CLASS,
						 GET_CHILDREN,
						 GET_CHILDREN_SIG);
	final int applyTemplates = cpg.addMethodref(getClassName(),
						    functionName(),
						    applyTemplatesSig);
	il.append(classGen.loadTranslet());
	il.append(methodGen.loadDOM());
	
	il.append(methodGen.loadDOM());
	il.append(new ILOAD(node));
	il.append(new INVOKEVIRTUAL(getChildren));
	il.append(methodGen.loadHandler());
	il.append(new INVOKEVIRTUAL(applyTemplates));
	il.append(new GOTO_W(next));
	return il;
    }

    /**
     * Compiles the default action for DOM text nodes and attribute nodes:
     * output the node's text value
     */
    private InstructionList compileDefaultText(ClassGenerator classGen,
					       MethodGenerator methodGen,
					       int node,
					       InstructionHandle next) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = new InstructionList();
	final String DOM_CLASS = classGen.getDOMClass();
	il.append(methodGen.loadDOM());
	il.append(new ILOAD(node));
	il.append(methodGen.loadHandler());
	il.append(new INVOKEVIRTUAL(cpg.addMethodref(DOM_CLASS,
						     CHARACTERS,
						     CHARACTERS_SIG)));
	il.append(new GOTO_W(next));
	return il;
    }

    private InstructionList compileNamespaces(ClassGenerator classGen,
					      MethodGenerator methodGen,
					      boolean[] isNamespace,
					      boolean[] isAttribute,
					      boolean attrFlag,
					      InstructionHandle defaultTarget) {
	final XSLTC xsltc = classGen.getParser().getXSLTC();
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final String DOM_CLASS = classGen.getDOMClass();

	// Append switch() statement - namespace test dispatch loop
	final Vector namespaces = xsltc.getNamespaceIndex();
	final Vector names = xsltc.getNamesIndex();
	final int namespaceCount = namespaces.size() + 1;
	final int namesCount = names.size();

	final InstructionList il = new InstructionList();
	final int[] types = new int[namespaceCount];
	final InstructionHandle[] targets = new InstructionHandle[types.length];

	if (namespaceCount > 0) {
	    boolean compiled = false;

	    // Initialize targets for namespace() switch statement
	    for (int i = 0; i < namespaceCount; i++) {
		targets[i] = defaultTarget;
		types[i] = i;
	    }

	    // Add test sequences for known namespace types
	    for (int i = DOM.NTYPES; i < (DOM.NTYPES+namesCount); i++) {
		if ((isNamespace[i]) && (isAttribute[i] == attrFlag)) {
		    String name = (String)names.elementAt(i-DOM.NTYPES);
		    String namespace = name.substring(0,name.lastIndexOf(':'));
		    final int type = xsltc.registerNamespace(namespace);
		    
		    if ((i < _code2TestSeq.length) &&
			(_code2TestSeq[i] != null)) {
			targets[type] =
			    (_code2TestSeq[i]).compile(classGen,
						       methodGen,
						       defaultTarget);
			compiled = true;
		    }
		}
	    }

	    // Return "null" if no test sequences were compiled
	    if (!compiled) return(null);
		
	    // Append first code in applyTemplates() - get type of current node
	    il.append(methodGen.loadDOM());
	    il.append(new ILOAD(_currentIndex));
	    il.append(new INVOKEVIRTUAL(cpg.addMethodref(DOM_CLASS,
							     "getNamespaceType",
							     "(I)I")));
	    il.append(new SWITCH(types, targets, defaultTarget));
	    return(il);
	}
	else {
	    return(null);
	}
    }

    /**
     * Auxillary method to determine if a qname describes an attribute/element
     */
    private static boolean isAttributeName(String qname) {
	final int col = qname.indexOf(':') + 1;
	if (qname.charAt(col) == '@')
	    return(true);
	else
	    return(false);
    }

    private static boolean isNamespaceName(String qname) {
	final int col = qname.lastIndexOf(':');
	if ((col > -1) && (qname.charAt(qname.length()-1) == '*'))
	    return(true);
	else
	    return(false);
    }

    /**
     * Compiles the applyTemplates() method and adds it to the translet.
     * This is the main dispatch method.
     */
    public void compileApplyTemplates(ClassGenerator classGen) {
	final XSLTC xsltc = classGen.getParser().getXSLTC();
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final Vector names      = xsltc.getNamesIndex();
	final String DOM_CLASS = classGen.getDOMClass();

	// (*) Create the applyTemplates() method
	final de.fub.bytecode.generic.Type[] argTypes =
	    new de.fub.bytecode.generic.Type[3];
	argTypes[0] = Util.getJCRefType(classGen.getDOMClassSig());
	argTypes[1] = Util.getJCRefType(NODE_ITERATOR_SIG);
	argTypes[2] = Util.getJCRefType(TRANSLET_OUTPUT_SIG);

	final String[] argNames = new String[3];
	argNames[0] = DOCUMENT_PNAME;
	argNames[1] = ITERATOR_PNAME;
	argNames[2] = TRANSLET_OUTPUT_PNAME;

	final InstructionList mainIL = new InstructionList();
	final MethodGenerator methodGen =
	    new MethodGenerator(ACC_PUBLIC | ACC_FINAL, 
				de.fub.bytecode.generic.Type.VOID,
				argTypes, argNames, functionName(),
				getClassName(), mainIL,
				classGen.getConstantPool());
	methodGen.addException("org.apache.xalan.xsltc.TransletException");

	// (*) Create the local variablea
	final LocalVariableGen current;
	current = methodGen.addLocalVariable2("current",
					      de.fub.bytecode.generic.Type.INT,
					      mainIL.getEnd());
	_currentIndex = current.getIndex();

	// (*) Create the "body" instruction list that will eventually hold the
	//     code for the entire method (other ILs will be appended).
	final InstructionList body = new InstructionList();
	body.append(NOP);

	// (*) Create an instruction list that contains the default next-node
	//     iteration
	final InstructionList ilLoop = new InstructionList();
	ilLoop.append(methodGen.loadIterator());
	ilLoop.append(methodGen.nextNode());
	ilLoop.append(DUP);
	ilLoop.append(new ISTORE(_currentIndex));
	// The body of this code can get very large - large than can be handled
	// by a single IFNE(body.getStart()) instruction - need workaround:
        final BranchHandle ifeq = ilLoop.append(new IFEQ(null));
	ilLoop.append(new GOTO_W(body.getStart()));
	ifeq.setTarget(ilLoop.append(NOP));

	final InstructionHandle ihLoop = ilLoop.getStart();

	// (*) Compile default handling of elements (traverse children)
	InstructionList ilRecurse =
	  compileDefaultRecursion(classGen, methodGen, _currentIndex, ihLoop);
	InstructionHandle ihRecurse = ilRecurse.getStart();

	// (*) Compile default handling of text/attribute nodes (output text)
	InstructionList ilText = compileDefaultText(classGen, methodGen,
						    _currentIndex, ihLoop);
	InstructionHandle ihText = ilText.getStart();

	// Distinguish attribute/element/namespace tests for further processing
	final int[] types = new int[DOM.NTYPES + names.size()];
	for (int i = 0; i < types.length; i++) types[i] = i;

	final boolean[] isAttribute = new boolean[types.length];
	final boolean[] isNamespace = new boolean[types.length];
	for (int i = 0; i < names.size(); i++) {
	    final String name = (String)names.elementAt(i);
	    isAttribute[i+DOM.NTYPES] = isAttributeName(name);
	    isNamespace[i+DOM.NTYPES] = isNamespaceName(name);
	}

	// (*) Compile all templates - regardless of pattern type
	compileTemplates(classGen, methodGen, ihLoop);

	// (*) Handle template with explicit "*" pattern
	final TestSeq elemTest = _code2TestSeq[DOM.ELEMENT];
	InstructionHandle ihElem = ihRecurse;
	if (elemTest != null)
	    ihElem = elemTest.compile(classGen, methodGen, ihRecurse);

	// (*) Handle template with explicit "@*" pattern
	final TestSeq attrTest = _code2TestSeq[DOM.ATTRIBUTE];
	InstructionHandle ihAttr = ihText;
	if (attrTest != null)
	    ihAttr = attrTest.compile(classGen, methodGen, ihText);

	// (*) If there is a match on node() we need to replace ihElem
	//     and ihText (default behaviour for elements & text).
	if (_nodeTestSeq != null) {
	    double nodePrio = -0.5;// _nodeTestSeq.getPriority();
	    int    nodePos  = _nodeTestSeq.getPosition();
	    if ((elemTest == null) ||
		(elemTest.getPriority() == Double.NaN) ||
		(elemTest.getPriority() < nodePrio) ||
		((elemTest.getPriority() == nodePrio) &&
		 (elemTest.getPosition() < nodePos))) {
		ihElem = _nodeTestSeq.compile(classGen, methodGen, ihLoop);
		ihText = ihElem;
	    }
	}

	// (*) Handle templates with "ns:*" pattern
	InstructionHandle elemNamespaceHandle = ihElem;
	InstructionList nsElem = compileNamespaces(classGen, methodGen,
						   isNamespace, isAttribute,
						   false, ihElem);
	if (nsElem != null) elemNamespaceHandle = nsElem.getStart();

	// (*) Handle templates with "ns:@*" pattern
	InstructionList nsAttr = compileNamespaces(classGen, methodGen,
						   isNamespace, isAttribute,
						   true, ihAttr);
	InstructionHandle attrNamespaceHandle = ihAttr;
	if (nsAttr != null) attrNamespaceHandle = nsAttr.getStart();

	// (*) Handle templates with "ns:elem" or "ns:@attr" pattern
	final InstructionHandle[] targets = new InstructionHandle[types.length];
	for (int i = DOM.NTYPES; i < targets.length; i++) {
	    final TestSeq testSeq = _code2TestSeq[i];
	    // Jump straight to namespace tests ?
	    if (isNamespace[i]) {
		if (isAttribute[i])
		    targets[i] = attrNamespaceHandle;
		else
		    targets[i] = elemNamespaceHandle;
	    }
	    // Test first, then jump to namespace tests
	    else if (testSeq != null) {
		if (isAttribute[i])
		    targets[i] = testSeq.compile(classGen, methodGen,
						 attrNamespaceHandle);
		else
		    targets[i] = testSeq.compile(classGen, methodGen,
						 elemNamespaceHandle);
	    }
	    else {
		targets[i] = ihLoop;
	    }
	}

	// Handle pattern with match on root node - default: traverse children
	targets[DOM.ROOT] = _explicitRootPattern != null
	    ? getTemplateInstructionHandle(_explicitRootPattern.getTemplate())
	    : ihRecurse;
	
	// Handle any pattern with match on text nodes - default: output text
	targets[DOM.TEXT] = _code2TestSeq[DOM.TEXT] != null
	    ? _code2TestSeq[DOM.TEXT].compile(classGen, methodGen, ihText)
	    : ihText;

	// This DOM-type is not in use - default: process next node
	targets[DOM.UNUSED] = ihLoop;

	// Match unknown element in DOM - default: check for namespace match
	targets[DOM.ELEMENT] = elemNamespaceHandle;

	// Match unknown attribute in DOM - default: check for namespace match
	targets[DOM.ATTRIBUTE] = attrNamespaceHandle;

	// Match on processing instruction - default: process next node
	targets[DOM.PROCESSING_INSTRUCTION] =
	    _code2TestSeq[DOM.PROCESSING_INSTRUCTION] != null
	    ? _code2TestSeq[DOM.PROCESSING_INSTRUCTION]
	    .compile(classGen, methodGen, ihLoop)
	    : ihLoop;
	
	// Match on comments - default: process next node
	targets[DOM.COMMENT] = _code2TestSeq[DOM.COMMENT] != null
	    ? _code2TestSeq[DOM.COMMENT].compile(classGen, methodGen, ihLoop)
	    : ihLoop;

	// Now compile test sequences for various match patterns:
	for (int i = DOM.NTYPES; i < targets.length; i++) {
	    final TestSeq testSeq = _code2TestSeq[i];
	    // Jump straight to namespace tests ?
	    if ((testSeq == null) || (isNamespace[i])) {
		if (isAttribute[i])
		    targets[i] = attrNamespaceHandle;
		else
		    targets[i] = elemNamespaceHandle;
	    }
	    // Match on node type
	    else {
		if (isAttribute[i])
		    targets[i] = testSeq.compile(classGen, methodGen,
						 attrNamespaceHandle);
		else
		    targets[i] = testSeq.compile(classGen, methodGen,
						 elemNamespaceHandle);
	    }
	}

	// Append first code in applyTemplates() - get type of current node
	body.append(methodGen.loadDOM());
	body.append(new ILOAD(_currentIndex));
	body.append(new INVOKEVIRTUAL(cpg.addMethodref(DOM_CLASS,
						       "getType", "(I)I")));
	
	// Append switch() statement - main dispatch loop in applyTemplates()
	body.append(new SWITCH(types, targets, ihLoop));
	// Append all the "case:" statements
	appendTestSequences(body);
	// Append the actual template code
	appendTemplateCode(body);

	// Append NS:* node tests (if any)
	if (nsElem != null) body.append(nsElem);
	// Append NS:@* node tests (if any)
	if (nsAttr != null) body.append(nsAttr);

	// Append default action for element and root nodes
	body.append(ilRecurse);
	// Append default action for text and attribute nodes
	body.append(ilText);

	// putting together constituent instruction lists
	mainIL.append(new GOTO_W(ihLoop));
	mainIL.append(body);
	// fall through to ilLoop
	mainIL.append(ilLoop);
	mainIL.append(RETURN);

	peepHoleOptimization(methodGen);
	methodGen.stripAttributes(true);
	
	methodGen.setMaxLocals();
	methodGen.setMaxStack();
	methodGen.removeNOPs();
	classGen.addMethod(methodGen.getMethod());
    }

    /**
     * Peephole optimization: Remove sequences of [ALOAD, POP].
     */
    private void peepHoleOptimization(MethodGenerator methodGen) {
	InstructionList il = methodGen.getInstructionList();
	FindPattern find = new FindPattern(il);
	InstructionHandle ih;
	String pattern;

	// Remove sequences of ALOAD, POP
	pattern = "`ALOAD'`POP'`Instruction'";
	ih = find.search(pattern);
	while (ih != null) {
	    final InstructionHandle[] match = find.getMatch();
	    try {
		if ((!match[0].hasTargeters()) && (!match[1].hasTargeters())) {
		    il.delete(match[0], match[1]);
		}
	    }
	    catch (TargetLostException e) {
		// TODO: move target down into the list
	    }
	    ih = find.search(pattern, match[2]);
	}

	// Replace sequences of ILOAD_?, ALOAD_?, SWAP with ALOAD_?, ILOAD_?
	pattern = "`ILOAD__'`ALOAD__'`SWAP'`Instruction'";
	ih = find.search(pattern);
	while (ih != null) {
	    final InstructionHandle[] match = find.getMatch();
	    try {
		de.fub.bytecode.generic.Instruction iload;
		de.fub.bytecode.generic.Instruction aload;
		if ((!match[0].hasTargeters()) &&
		    (!match[1].hasTargeters()) &&
		    (!match[2].hasTargeters())) {
		    iload = match[0].getInstruction();
		    aload = match[1].getInstruction();
		    il.insert(match[0], aload);
		    il.insert(match[0], iload);
		    il.delete(match[0], match[2]);
		}
	    }
	    catch (TargetLostException e) {
		// TODO: move target down into the list
	    }
	    ih = find.search(pattern, match[3]);
	}

	// Replaces sequences of ALOAD_1, ALOAD_1 with ALOAD_1, DUP
	pattern = "`ALOAD_1'`ALOAD_1'`Instruction'";
	ih = find.search(pattern);
	while (ih != null) {
	    final InstructionHandle[] match = find.getMatch();
	    try {
		de.fub.bytecode.generic.Instruction iload;
		de.fub.bytecode.generic.Instruction aload;
		if ((!match[0].hasTargeters()) && (!match[1].hasTargeters())) {
		    il.insert(match[1], new DUP());
		    il.delete(match[1]);
		}
	    }
	    catch (TargetLostException e) {
		// TODO: move target down into the list
	    }
	    ih = find.search(pattern, match[2]);
	}

	// Removes uncessecary GOTOs
	pattern = "`GOTO'`GOTO'`Instruction'";
	ih = find.search(pattern);
	while (ih != null) {
	    final InstructionHandle[] match = find.getMatch();
	    try {
		de.fub.bytecode.generic.Instruction iload;
		de.fub.bytecode.generic.Instruction aload;
		InstructionTargeter tgtrs[] = match[1].getTargeters();
		if (tgtrs != null) {
		    InstructionHandle newTarget =
			((BranchHandle)match[1]).getTarget();
		    for (int i=0; i<tgtrs.length; i++)
			tgtrs[i].updateTarget(match[1],newTarget);
		}
		il.delete(match[1]);
	    }
	    catch (TargetLostException e) {
		// TODO: move target down into the list
	    }
	    ih = find.search(pattern, match[2]);
	}
	
	
    }

    public InstructionHandle getTemplateInstructionHandle(Template template) {
	return (InstructionHandle)_templateInstructionHandles.get(template);
    }
}
