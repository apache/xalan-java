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
 * @author G. Todd Miller
 */

package org.apache.xalan.xsltc.compiler;

import java.util.Iterator;
import java.util.HashSet;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.bcel.generic.*;
import org.apache.bcel.util.*;
import org.apache.xalan.xsltc.compiler.util.*;
import org.apache.xalan.xsltc.DOM;

/**
 * Mode gathers all the templates belonging to a given mode; 
 * it is responsible for generating an appropriate 
 * applyTemplates + (mode name) method in the translet.
 */
final class Mode implements Constants {

    /**
     * The name of this mode as defined in the stylesheet.
     */
    private final QName _name;

    /**
     * A reference to the stylesheet object that owns this mode.
     */
    private final Stylesheet _stylesheet; 

    /**
     * The name of the method in which this mode is compiled.
     */
    private final String _methodName;

    /**
     * A vector of all the templates in this mode.
     */
    private Vector _templates; 

    /**
     * Group for patterns with node()-type kernel.
     */
    private Vector _nodeGroup = null;

    /**
     * Test sequence for patterns with node()-type kernel.
     */
    private TestSeq _nodeTestSeq = null;

    /**
     * Group for patterns with id() or key()-type kernel.
     */
    private Vector _idxGroup = null;

    /**
     * Test sequence for patterns with id() or key()-type kernel.
     */
    private TestSeq _idxTestSeq = null;

    /**
     * Group for patterns with any other kernel type.
     */
    private Vector[] _patternGroups;

    /**
     * Test sequence for patterns with any other kernel type.
     */
    private TestSeq[] _testSeq;

    /**
     * A mapping between patterns and instruction lists used by 
     * test sequences to avoid compiling the same pattern multiple 
     * times. Note that patterns whose kernels are "*", "node()" 
     * and "@*" can between shared by test sequences.
     */
    private Hashtable _preCompiled = new Hashtable();

    /**
     * A mapping between templates and test sequences.
     */
    private Hashtable _neededTemplates = new Hashtable();

    /**
     * A mapping between named templates and Mode objects.
     */
    private Hashtable _namedTemplates = new Hashtable();

    /**
     * A mapping between templates and instruction handles.
     */
    private Hashtable _templateIHs = new Hashtable();

    /**
     * A mapping between templates and instruction lists.
     */
    private Hashtable _templateILs = new Hashtable();

    /**
     * A reference to the pattern matching the root node.
     */
    private LocationPathPattern _rootPattern = null;

    /**
     * Stores ranges of template precendences for the compilation 
     * of apply-imports (a Hashtable for historical reasons).
     */
    private Hashtable _importLevels = null;

    /**
     * A mapping between key names and keys.
     */
    private Hashtable _keys = null;

    /**
     * Variable index for the current node used in code generation.
     */
    private int _currentIndex;

    /**
     * Creates a new Mode.
     *
     * @param name A textual representation of the mode's QName
     * @param stylesheet The Stylesheet in which the mode occured
     * @param suffix A suffix to append to the method name for this mode
     *               (normally a sequence number - still in a String).
     */
    public Mode(QName name, Stylesheet stylesheet, String suffix) {
	_name = name;
	_stylesheet = stylesheet;
	_methodName = APPLY_TEMPLATES + suffix;
	_templates = new Vector();
	_patternGroups = new Vector[32];
    }

    /**
     * Returns the name of the method (_not_ function) that will be 
     * compiled for this mode. Normally takes the form 'applyTemplates()' 
     * or * 'applyTemplates2()'.
     *
     * @return Method name for this mode
     */
    public String functionName() {
	return _methodName;
    }

    public String functionName(int min, int max) {
	if (_importLevels == null) {
	    _importLevels = new Hashtable();
	}
	_importLevels.put(new Integer(max), new Integer(min));
	return _methodName + '_' + max;
    }

    /**
     * Add a pre-compiled pattern to this mode. 
     */
    public void addInstructionList(Pattern pattern, 
	InstructionList ilist) 
    {
	_preCompiled.put(pattern, ilist);
    }

    /**
     * Get the instruction list for a pre-compiled pattern. Used by 
     * test sequences to avoid compiling patterns more than once.
     */
    public InstructionList getInstructionList(Pattern pattern) {
	return (InstructionList) _preCompiled.get(pattern);
    }

    /**
     * Shortcut to get the class compiled for this mode (will be inlined).
     */
    private String getClassName() {
	return _stylesheet.getClassName();
    }

    public Stylesheet getStylesheet() {
	return _stylesheet;
    }

    public void addTemplate(Template template) {
	_templates.addElement(template);
    }

    private Vector quicksort(Vector templates, int p, int r) {
	if (p < r) {
	    final int q = partition(templates, p, r);
	    quicksort(templates, p, q);
	    quicksort(templates, q + 1, r);
	}
	return templates;
    }
    
    private int partition(Vector templates, int p, int r) {
	final Template x = (Template)templates.elementAt(p);
	int i = p - 1;
	int j = r + 1;
	while (true) {
	    while (x.compareTo((Template)templates.elementAt(--j)) > 0);
	    while (x.compareTo((Template)templates.elementAt(++i)) < 0);
	    if (i < j) {
		templates.set(j, templates.set(i, templates.elementAt(j)));
	    }
	    else {
		return j;
	    }
	}
    }

    /**
     * Process all the test patterns in this mode
     */
    public void processPatterns(Hashtable keys) {
	_keys = keys;

/*
System.out.println("Before Sort " + _name);
for (int i = 0; i < _templates.size(); i++) {
    System.out.println("name = " + ((Template)_templates.elementAt(i)).getName());
    System.out.println("pattern = " + ((Template)_templates.elementAt(i)).getPattern());
    System.out.println("priority = " + ((Template)_templates.elementAt(i)).getPriority());
    System.out.println("position = " + ((Template)_templates.elementAt(i)).getPosition());
}
*/

	_templates = quicksort(_templates, 0, _templates.size() - 1);

/*
System.out.println("\n After Sort " + _name);
for (int i = 0; i < _templates.size(); i++) {
    System.out.println("name = " + ((Template)_templates.elementAt(i)).getName());
    System.out.println("pattern = " + ((Template)_templates.elementAt(i)).getPattern());
    System.out.println("priority = " + ((Template)_templates.elementAt(i)).getPriority());
    System.out.println("position = " + ((Template)_templates.elementAt(i)).getPosition());
}
*/

	// Traverse all templates
	final Enumeration templates = _templates.elements();
	while (templates.hasMoreElements()) {
	    // Get the next template
	    final Template template = (Template)templates.nextElement();

	    /* 
	     * Add this template to a table of named templates if it has a name.
	     * If there are multiple templates with the same name, all but one
	     * (the one with highest priority) will be disabled.
	     */
	    if (template.isNamed() && !template.disabled()) {
		_namedTemplates.put(template, this);
	    }

	    // Add this template to a test sequence if it has a pattern
	    final Pattern pattern = template.getPattern();
	    if (pattern != null) {
		flattenAlternative(pattern, template, keys);
	    }
	}
	prepareTestSequences();
    }

    /**
     * This method will break up alternative patterns (ie. unions of patterns,
     * such as match="A/B | C/B") and add the basic patterns to their
     * respective pattern groups.
     */
    private void flattenAlternative(Pattern pattern,
				    Template template,
				    Hashtable keys) {
	// Patterns on type id() and key() are special since they do not have
	// any kernel node type (it can be anything as long as the node is in
	// the id's or key's index).
	if (pattern instanceof IdKeyPattern) {
	    final IdKeyPattern idkey = (IdKeyPattern)pattern;
	    idkey.setTemplate(template);
	    if (_idxGroup == null) _idxGroup = new Vector();
	    _idxGroup.add(pattern);
	}
	// Alternative patterns are broken up and re-processed recursively
	else if (pattern instanceof AlternativePattern) {
	    final AlternativePattern alt = (AlternativePattern)pattern;
	    flattenAlternative(alt.getLeft(), template, keys);
	    flattenAlternative(alt.getRight(), template, keys);
	}
	// Finally we have a pattern that can be added to a test sequence!
	else if (pattern instanceof LocationPathPattern) {
	    final LocationPathPattern lpp = (LocationPathPattern)pattern;
	    lpp.setTemplate(template);
	    addPatternToGroup(lpp);
	}
    }

    /**
     * Group patterns by NodeTests of their last Step
     * Keep them sorted by priority within group
     */
    private void addPatternToGroup(final LocationPathPattern lpp) {
	// id() and key()-type patterns do not have a kernel type
	if (lpp instanceof IdKeyPattern) {
	    addPattern(-1, lpp);
	}
	// Otherwise get the kernel pattern from the LPP
	else {
	    // kernel pattern is the last (maybe only) Step
	    final StepPattern kernel = lpp.getKernelPattern();
	    if (kernel != null) {
		addPattern(kernel.getNodeType(), lpp);
	    }
	    else if (_rootPattern == null ||
		     lpp.noSmallerThan(_rootPattern)) {
		_rootPattern = lpp;
	    }
	}
    }

    /**
     * Adds a pattern to a pattern group
     */
    private void addPattern(int kernelType, LocationPathPattern pattern) {
	// Make sure the array of pattern groups is long enough
	final int oldLength = _patternGroups.length;
	if (kernelType >= oldLength) {
	    Vector[] newGroups = new Vector[kernelType * 2];
	    System.arraycopy(_patternGroups, 0, newGroups, 0, oldLength);
	    _patternGroups = newGroups;
	}
	
	// Find the vector to put this pattern into
	Vector patterns;

	// Use the vector for id()/key()/node() patterns if no kernel type
	patterns = (kernelType == -1) ? _nodeGroup : _patternGroups[kernelType];

	// Create a new vector if needed and insert the very first pattern
	if (patterns == null) {
	    patterns = new Vector(2);
	    patterns.addElement(pattern);

	    if (kernelType == -1) {
		_nodeGroup = patterns;
	    }
	    else {
		_patternGroups[kernelType] = patterns;
	    }
	}
	// Otherwise make sure patterns are ordered by precedence/priorities
	else {
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
    }
    
    /**
     * Build test sequences. The first step is to complete the test sequences 
     * by including patterns of "*" and "node()" kernel to all element test 
     * sequences, and of "@*" to all attribute test sequences.
     */
    private void prepareTestSequences() {
	final Vector names = _stylesheet.getXSLTC().getNamesIndex();

	final Vector starGroup = _patternGroups[DOM.ELEMENT];
	final Vector atStarGroup = _patternGroups[DOM.ATTRIBUTE];

	// Complete test sequences with "*", "@*" and "node()"
	if (starGroup != null || atStarGroup != null || _nodeGroup != null) {
	    final int n = _patternGroups.length;

	    for (int m, i = DOM.NTYPES; i < n; i++) {
		if (_patternGroups[i] == null) continue;

		final String name = (String) names.elementAt(i - DOM.NTYPES);

		if (isAttributeName(name)) {
		    // If an attribute then copy "@*" to its test sequence
		    m = (atStarGroup != null) ? atStarGroup.size() : 0;
		    for (int j = 0; j < m; j++) {
			addPattern(i, 
			    (LocationPathPattern) atStarGroup.elementAt(j));
		    }
		}
		else {
		    // If an element then copy "*" to its test sequence
		    m = (starGroup != null) ? starGroup.size() : 0;
		    for (int j = 0; j < m; j++) {
			addPattern(i, 
			    (LocationPathPattern) starGroup.elementAt(j));
		    }

		    // And also copy "node()" to its test sequence
		    m = (_nodeGroup != null) ? _nodeGroup.size() : 0;
		    for (int j = 0; j < m; j++) {
			addPattern(i, 
			    (LocationPathPattern) _nodeGroup.elementAt(j));
		    }
		}
	    }
	}

	_testSeq = new TestSeq[DOM.NTYPES + names.size()];
	
	final int n = _patternGroups.length;
	for (int i = 0; i < n; i++) {
	    final Vector patterns = _patternGroups[i];
	    if (patterns != null) {
		final TestSeq testSeq = new TestSeq(patterns, i, this);
		testSeq.reduce();
		_testSeq[i] = testSeq;
		testSeq.findTemplates(_neededTemplates);
	    }
	}

	if ((_nodeGroup != null) && (_nodeGroup.size() > 0)) {
	    _nodeTestSeq = new TestSeq(_nodeGroup, -1, this);
	    _nodeTestSeq.reduce();
	    _nodeTestSeq.findTemplates(_neededTemplates);
	}

	if ((_idxGroup != null) && (_idxGroup.size() > 0)) {
	    _idxTestSeq = new TestSeq(_idxGroup, this);
	    _idxTestSeq.reduce();
	    _idxTestSeq.findTemplates(_neededTemplates);
	}
	
	if (_rootPattern != null) {
	    // doesn't matter what is 'put', only key matters
	    _neededTemplates.put(_rootPattern.getTemplate(), this);
	}
    }

    private void compileNamedTemplate(Template template,
				      ClassGenerator classGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = new InstructionList();
	String methodName = Util.escape(template.getName().toString());

	final NamedMethodGenerator methodGen =
	    new NamedMethodGenerator(ACC_PUBLIC,
				     org.apache.bcel.generic.Type.VOID,
				     new org.apache.bcel.generic.Type[] {
					 Util.getJCRefType(DOM_INTF_SIG),
					 Util.getJCRefType(NODE_ITERATOR_SIG),
					 Util.getJCRefType(TRANSLET_OUTPUT_SIG),
					 org.apache.bcel.generic.Type.INT
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
				  InstructionHandle next) 
    {
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
		_templateILs.put(template, til);
		_templateIHs.put(template, til.getStart());
	    }
	    else {
		// empty template
		_templateIHs.put(template, next);
	    }
	}
    }
	
    private void appendTemplateCode(InstructionList body) {
	final Enumeration templates = _neededTemplates.keys();
	while (templates.hasMoreElements()) {
	    final Object iList =
		_templateILs.get(templates.nextElement());
	    if (iList != null) {
		body.append((InstructionList)iList);
	    }
	}
    }

    private void appendTestSequences(InstructionList body) {
	final int n = _testSeq.length;
	for (int i = 0; i < n; i++) {
	    final TestSeq testSeq = _testSeq[i];
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
	final int git = cpg.addInterfaceMethodref(DOM_INTF,
						  GET_CHILDREN,
						  GET_CHILDREN_SIG);
	il.append(methodGen.loadDOM());
	il.append(new ILOAD(node));
	il.append(new INVOKEINTERFACE(git, 2));
    }

    /**
     * Compiles the default handling for DOM elements: traverse all children
     */
    private InstructionList compileDefaultRecursion(ClassGenerator classGen,
						    MethodGenerator methodGen,
						    InstructionHandle next) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = new InstructionList();
	final String applyTemplatesSig = classGen.getApplyTemplatesSig();
	final int git = cpg.addInterfaceMethodref(DOM_INTF,
						  GET_CHILDREN,
						  GET_CHILDREN_SIG);
	final int applyTemplates = cpg.addMethodref(getClassName(),
						    functionName(),
						    applyTemplatesSig);
	il.append(classGen.loadTranslet());
	il.append(methodGen.loadDOM());
	
	il.append(methodGen.loadDOM());
	il.append(new ILOAD(_currentIndex));
	il.append(new INVOKEINTERFACE(git, 2));
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
					       InstructionHandle next) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = new InstructionList();

	final int chars = cpg.addInterfaceMethodref(DOM_INTF,
						    CHARACTERS,
						    CHARACTERS_SIG);
	il.append(methodGen.loadDOM());
	il.append(new ILOAD(_currentIndex));
	il.append(methodGen.loadHandler());
	il.append(new INVOKEINTERFACE(chars, 3));
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
		    
		    if ((i < _testSeq.length) &&
			(_testSeq[i] != null)) {
			targets[type] =
			    (_testSeq[i]).compile(classGen,
						       methodGen,
						       defaultTarget);
			compiled = true;
		    }
		}
	    }

	    // Return "null" if no test sequences were compiled
	    if (!compiled) return(null);
		
	    // Append first code in applyTemplates() - get type of current node
	    final int getNS = cpg.addInterfaceMethodref(DOM_INTF,
							"getNamespaceType",
							"(I)I");
	    il.append(methodGen.loadDOM());
	    il.append(new ILOAD(_currentIndex));
	    il.append(new INVOKEINTERFACE(getNS, 2));
	    il.append(new SWITCH(types, targets, defaultTarget));
	    return(il);
	}
	else {
	    return(null);
	}
    }

   /**
     * Compiles the applyTemplates() method and adds it to the translet.
     * This is the main dispatch method.
     */
    public void compileApplyTemplates(ClassGenerator classGen) {
	final XSLTC xsltc = classGen.getParser().getXSLTC();
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final Vector names = xsltc.getNamesIndex();

	// Create the applyTemplates() method
	final org.apache.bcel.generic.Type[] argTypes =
	    new org.apache.bcel.generic.Type[3];
	argTypes[0] = Util.getJCRefType(DOM_INTF_SIG);
	argTypes[1] = Util.getJCRefType(NODE_ITERATOR_SIG);
	argTypes[2] = Util.getJCRefType(TRANSLET_OUTPUT_SIG);

	final String[] argNames = new String[3];
	argNames[0] = DOCUMENT_PNAME;
	argNames[1] = ITERATOR_PNAME;
	argNames[2] = TRANSLET_OUTPUT_PNAME;

	final InstructionList mainIL = new InstructionList();
	final MethodGenerator methodGen =
	    new MethodGenerator(ACC_PUBLIC | ACC_FINAL, 
				org.apache.bcel.generic.Type.VOID,
				argTypes, argNames, functionName(),
				getClassName(), mainIL,
				classGen.getConstantPool());
	methodGen.addException("org.apache.xalan.xsltc.TransletException");

	// Create a local variable to hold the current node
	final LocalVariableGen current;
	current = methodGen.addLocalVariable2("current",
					      org.apache.bcel.generic.Type.INT,
					      mainIL.getEnd());
	_currentIndex = current.getIndex();

	// Create the "body" instruction list that will eventually hold the
	// code for the entire method (other ILs will be appended).
	final InstructionList body = new InstructionList();
	body.append(NOP);

	// Create an instruction list that contains the default next-node
	// iteration
	final InstructionList ilLoop = new InstructionList();
	ilLoop.append(methodGen.loadIterator());
	ilLoop.append(methodGen.nextNode());
	ilLoop.append(DUP);
	ilLoop.append(new ISTORE(_currentIndex));

	// The body of this code can get very large - large than can be handled
	// by a single IFNE(body.getStart()) instruction - need workaround:
        final BranchHandle ifeq = ilLoop.append(new IFEQ(null));
	final BranchHandle loop = ilLoop.append(new GOTO_W(null));
	ifeq.setTarget(ilLoop.append(RETURN)); 	// applyTemplates() ends here!
	final InstructionHandle ihLoop = ilLoop.getStart();

	// Compile default handling of elements (traverse children)
	InstructionList ilRecurse =
	    compileDefaultRecursion(classGen, methodGen, ihLoop);
	InstructionHandle ihRecurse = ilRecurse.getStart();

	// Compile default handling of text/attribute nodes (output text)
	InstructionList ilText =
	    compileDefaultText(classGen, methodGen, ihLoop);
	InstructionHandle ihText = ilText.getStart();

	// Distinguish attribute/element/namespace tests for further processing
	final int[] types = new int[DOM.NTYPES + names.size()];
	for (int i = 0; i < types.length; i++) {
	    types[i] = i;
	}

	// Initialize isAttribute[] and isNamespace[] arrays
	final boolean[] isAttribute = new boolean[types.length];
	final boolean[] isNamespace = new boolean[types.length];
	for (int i = 0; i < names.size(); i++) {
	    final String name = (String)names.elementAt(i);
	    isAttribute[i + DOM.NTYPES] = isAttributeName(name);
	    isNamespace[i + DOM.NTYPES] = isNamespaceName(name);
	}

	// Compile all templates - regardless of pattern type
	compileTemplates(classGen, methodGen, ihLoop);

	// Handle template with explicit "*" pattern
	final TestSeq elemTest = _testSeq[DOM.ELEMENT];
	InstructionHandle ihElem = ihRecurse;
	if (elemTest != null)
	    ihElem = elemTest.compile(classGen, methodGen, ihRecurse);

	// Handle template with explicit "@*" pattern
	final TestSeq attrTest = _testSeq[DOM.ATTRIBUTE];
	InstructionHandle ihAttr = ihText;
	if (attrTest != null)
	    ihAttr = attrTest.compile(classGen, methodGen, ihAttr);

	// Do tests for id() and key() patterns first
	InstructionList ilKey = null;
	if (_idxTestSeq != null) {
	    loop.setTarget(_idxTestSeq.compile(classGen, methodGen, body.getStart()));
	    ilKey = _idxTestSeq.getInstructionList();
	}
	else {
	    loop.setTarget(body.getStart());
	}

	// If there is a match on node() we need to replace ihElem
	// and ihText if the priority of node() is higher
	if (_nodeTestSeq != null) {
	    // Compare priorities of node() and "*"
	    double nodePrio = _nodeTestSeq.getPriority();
	    int    nodePos  = _nodeTestSeq.getPosition();
	    double elemPrio = (0 - Double.MAX_VALUE);
	    int    elemPos  = Integer.MIN_VALUE;

	    if (elemTest != null) {
		elemPrio = elemTest.getPriority();
		elemPos  = elemTest.getPosition();
	    }
	    if (elemPrio == Double.NaN || elemPrio < nodePrio || 
		(elemPrio == nodePrio && elemPos < nodePos)) 
	    {
		ihElem = _nodeTestSeq.compile(classGen, methodGen, ihLoop);
	    }

	    // Compare priorities of node() and text()
	    final TestSeq textTest = _testSeq[DOM.TEXT];
	    double textPrio = (0 - Double.MAX_VALUE);
	    int    textPos  = Integer.MIN_VALUE;

	    if (textTest != null) {
		textPrio = textTest.getPriority();
		textPos  = textTest.getPosition();
	    }
	    if (textPrio == Double.NaN || textPrio < nodePrio ||
	        (textPrio == nodePrio && textPos < nodePos)) 
	    {
		ihText = _nodeTestSeq.compile(classGen, methodGen, ihLoop);
		_testSeq[DOM.TEXT] = _nodeTestSeq;
	    }
	}

	// Handle templates with "ns:*" pattern
	InstructionHandle elemNamespaceHandle = ihElem;
	InstructionList nsElem = compileNamespaces(classGen, methodGen,
						   isNamespace, isAttribute,
						   false, ihElem);
	if (nsElem != null) elemNamespaceHandle = nsElem.getStart();

	// Handle templates with "ns:@*" pattern
	InstructionHandle attrNamespaceHandle = ihAttr;
	InstructionList nsAttr = compileNamespaces(classGen, methodGen,
						   isNamespace, isAttribute,
						   true, ihAttr);
	if (nsAttr != null) attrNamespaceHandle = nsAttr.getStart();

	// Handle templates with "ns:elem" or "ns:@attr" pattern
	final InstructionHandle[] targets = new InstructionHandle[types.length];
	for (int i = DOM.NTYPES; i < targets.length; i++) {
	    final TestSeq testSeq = _testSeq[i];
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
	targets[DOM.ROOT] = _rootPattern != null
	    ? getTemplateInstructionHandle(_rootPattern.getTemplate())
	    : ihRecurse;
	
	// Handle any pattern with match on text nodes - default: output text
	targets[DOM.TEXT] = _testSeq[DOM.TEXT] != null
	    ? _testSeq[DOM.TEXT].compile(classGen, methodGen, ihText)
	    : ihText;

	// This DOM-type is not in use - default: process next node
	targets[DOM.NAMESPACE] = ihLoop;

	// Match unknown element in DOM - default: check for namespace match
	targets[DOM.ELEMENT] = elemNamespaceHandle;

	// Match unknown attribute in DOM - default: check for namespace match
	targets[DOM.ATTRIBUTE] = attrNamespaceHandle;

	// Match on processing instruction - default: process next node
	InstructionHandle ihPI = ihLoop;
	if (_nodeTestSeq != null) ihPI = ihElem;
	if (_testSeq[DOM.PROCESSING_INSTRUCTION] != null)
	    targets[DOM.PROCESSING_INSTRUCTION] =
		_testSeq[DOM.PROCESSING_INSTRUCTION].
		compile(classGen, methodGen, ihPI);
	else
	    targets[DOM.PROCESSING_INSTRUCTION] = ihPI;
	
	// Match on comments - default: process next node
	InstructionHandle ihComment = ihLoop;
	if (_nodeTestSeq != null) ihComment = ihElem;
	targets[DOM.COMMENT] = _testSeq[DOM.COMMENT] != null
	    ? _testSeq[DOM.COMMENT].compile(classGen, methodGen, ihComment)
	    : ihComment;

	// Now compile test sequences for various match patterns:
	for (int i = DOM.NTYPES; i < targets.length; i++) {
	    final TestSeq testSeq = _testSeq[i];
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

	if (ilKey != null) body.insert(ilKey);

	// Append first code in applyTemplates() - get type of current node
	final int getType = cpg.addInterfaceMethodref(DOM_INTF,
						      "getType", "(I)I");
	body.append(methodGen.loadDOM());
	body.append(new ILOAD(_currentIndex));
	body.append(new INVOKEINTERFACE(getType, 2));

	// Append switch() statement - main dispatch loop in applyTemplates()
	InstructionHandle disp = body.append(new SWITCH(types, targets, ihLoop));

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

	peepHoleOptimization(methodGen);
	methodGen.stripAttributes(true);
	
	methodGen.setMaxLocals();
	methodGen.setMaxStack();
	methodGen.removeNOPs();
	classGen.addMethod(methodGen.getMethod());

	// Compile method(s) for <xsl:apply-imports/> for this mode
	if (_importLevels != null) {
	    Enumeration levels = _importLevels.keys();
	    while (levels.hasMoreElements()) {
		Integer max = (Integer)levels.nextElement();
		Integer min = (Integer)_importLevels.get(max);
		compileApplyImports(classGen, min.intValue(), max.intValue());
	    }
	}
    }

    private void compileTemplateCalls(ClassGenerator classGen,
				      MethodGenerator methodGen,
				      InstructionHandle next, int min, int max){
        Enumeration templates = _neededTemplates.keys();
	while (templates.hasMoreElements()) {
	    final Template template = (Template)templates.nextElement();
	    final int prec = template.getImportPrecedence();
	    if ((prec >= min) && (prec < max)) {
		if (template.hasContents()) {
		    InstructionList til = template.compile(classGen, methodGen);
		    til.append(new GOTO_W(next));
		    _templateILs.put(template, til);
		    _templateIHs.put(template, til.getStart());
		}
		else {
		    // empty template
		    _templateIHs.put(template, next);
		}
	    }
	}
    }


    public void compileApplyImports(ClassGenerator classGen, int min, int max) {
	final XSLTC xsltc = classGen.getParser().getXSLTC();
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final Vector names      = xsltc.getNamesIndex();

	// Clear some datastructures
	_namedTemplates = new Hashtable();
	_neededTemplates = new Hashtable();
	_templateIHs = new Hashtable();
	_templateILs = new Hashtable();
	_patternGroups = new Vector[32];
	_rootPattern = null;

	// IMPORTANT: Save orignal & complete set of templates!!!!
	Vector oldTemplates = _templates;

	// Gather templates that are within the scope of this import
	_templates = new Vector();
	final Enumeration templates = oldTemplates.elements();
	while (templates.hasMoreElements()) {
	    final Template template = (Template)templates.nextElement();
	    final int prec = template.getImportPrecedence();
	    if ((prec >= min) && (prec < max)) addTemplate(template);
	}

	// Process all patterns from those templates
	processPatterns(_keys);

	// Create the applyTemplates() method
	final org.apache.bcel.generic.Type[] argTypes =
	    new org.apache.bcel.generic.Type[3];
	argTypes[0] = Util.getJCRefType(DOM_INTF_SIG);
	argTypes[1] = Util.getJCRefType(NODE_ITERATOR_SIG);
	argTypes[2] = Util.getJCRefType(TRANSLET_OUTPUT_SIG);

	final String[] argNames = new String[3];
	argNames[0] = DOCUMENT_PNAME;
	argNames[1] = ITERATOR_PNAME;
	argNames[2] = TRANSLET_OUTPUT_PNAME;

	final InstructionList mainIL = new InstructionList();
	final MethodGenerator methodGen =
	    new MethodGenerator(ACC_PUBLIC | ACC_FINAL, 
				org.apache.bcel.generic.Type.VOID,
				argTypes, argNames, functionName()+'_'+max,
				getClassName(), mainIL,
				classGen.getConstantPool());
	methodGen.addException("org.apache.xalan.xsltc.TransletException");

	// Create the local variable to hold the current node
	final LocalVariableGen current;
	current = methodGen.addLocalVariable2("current",
					      org.apache.bcel.generic.Type.INT,
					      mainIL.getEnd());
	_currentIndex = current.getIndex();

	// Create the "body" instruction list that will eventually hold the
	// code for the entire method (other ILs will be appended).
	final InstructionList body = new InstructionList();
	body.append(NOP);

	// Create an instruction list that contains the default next-node
	// iteration
	final InstructionList ilLoop = new InstructionList();
	ilLoop.append(methodGen.loadIterator());
	ilLoop.append(methodGen.nextNode());
	ilLoop.append(DUP);
	ilLoop.append(new ISTORE(_currentIndex));

	// The body of this code can get very large - large than can be handled
	// by a single IFNE(body.getStart()) instruction - need workaround:
        final BranchHandle ifeq = ilLoop.append(new IFEQ(null));
	final BranchHandle loop = ilLoop.append(new GOTO_W(null));
	ifeq.setTarget(ilLoop.append(RETURN)); // applyTemplates() ends here!
	final InstructionHandle ihLoop = ilLoop.getStart();

	// Compile default handling of elements (traverse children)
	InstructionList ilRecurse =
	    compileDefaultRecursion(classGen, methodGen, ihLoop);
	InstructionHandle ihRecurse = ilRecurse.getStart();

	// Compile default handling of text/attribute nodes (output text)
	InstructionList ilText =
	    compileDefaultText(classGen, methodGen, ihLoop);
	InstructionHandle ihText = ilText.getStart();

	// Distinguish attribute/element/namespace tests for further processing
	final int[] types = new int[DOM.NTYPES + names.size()];
	for (int i = 0; i < types.length; i++) {
	    types[i] = i;
	}

	final boolean[] isAttribute = new boolean[types.length];
	final boolean[] isNamespace = new boolean[types.length];
	for (int i = 0; i < names.size(); i++) {
	    final String name = (String)names.elementAt(i);
	    isAttribute[i+DOM.NTYPES] = isAttributeName(name);
	    isNamespace[i+DOM.NTYPES] = isNamespaceName(name);
	}

	// Compile all templates - regardless of pattern type
	compileTemplateCalls(classGen, methodGen, ihLoop, min, max);

	// Handle template with explicit "*" pattern
	final TestSeq elemTest = _testSeq[DOM.ELEMENT];
	InstructionHandle ihElem = ihRecurse;
	if (elemTest != null) {
	    ihElem = elemTest.compile(classGen, methodGen, ihLoop);
	}

	// Handle template with explicit "@*" pattern
	final TestSeq attrTest = _testSeq[DOM.ATTRIBUTE];
	InstructionHandle ihAttr = ihLoop;
	if (attrTest != null) {
	    ihAttr = attrTest.compile(classGen, methodGen, ihAttr);
	}

	// Do tests for id() and key() patterns first
	InstructionList ilKey = null;
	if (_idxTestSeq != null) {
	    loop.setTarget(_idxTestSeq.compile(classGen, methodGen, body.getStart()));
	    ilKey = _idxTestSeq.getInstructionList();
	}
	else {
	    loop.setTarget(body.getStart());
	}

	// If there is a match on node() we need to replace ihElem
	// and ihText if the priority of node() is higher
	if (_nodeTestSeq != null) {
	    // Compare priorities of node() and "*"
	    double nodePrio = _nodeTestSeq.getPriority();
	    int    nodePos  = _nodeTestSeq.getPosition();
	    double elemPrio = (0 - Double.MAX_VALUE);
	    int    elemPos  = Integer.MIN_VALUE;

	    if (elemTest != null) {
		elemPrio = elemTest.getPriority();
		elemPos  = elemTest.getPosition();
	    }

	    if (elemPrio == Double.NaN || elemPrio < nodePrio || 
		(elemPrio == nodePrio && elemPos < nodePos)) 
	    {
		ihElem = _nodeTestSeq.compile(classGen, methodGen, ihLoop);
	    }

	    // Compare priorities of node() and text()
	    final TestSeq textTest = _testSeq[DOM.TEXT];
	    double textPrio = (0 - Double.MAX_VALUE);
	    int    textPos  = Integer.MIN_VALUE;

	    if (textTest != null) {
		textPrio = textTest.getPriority();
		textPos  = textTest.getPosition();
	    }

	    if (textPrio == Double.NaN || textPrio < nodePrio ||
	        (textPrio == nodePrio && textPos < nodePos)) 
	    {
		ihText = _nodeTestSeq.compile(classGen, methodGen, ihLoop);
		_testSeq[DOM.TEXT] = _nodeTestSeq;
	    }
	}

	// Handle templates with "ns:*" pattern
	InstructionHandle elemNamespaceHandle = ihElem;
	InstructionList nsElem = compileNamespaces(classGen, methodGen,
						   isNamespace, isAttribute,
						   false, ihElem);
	if (nsElem != null) elemNamespaceHandle = nsElem.getStart();

	// Handle templates with "ns:@*" pattern
	InstructionList nsAttr = compileNamespaces(classGen, methodGen,
						   isNamespace, isAttribute,
						   true, ihAttr);
	InstructionHandle attrNamespaceHandle = ihAttr;
	if (nsAttr != null) attrNamespaceHandle = nsAttr.getStart();

	// Handle templates with "ns:elem" or "ns:@attr" pattern
	final InstructionHandle[] targets = new InstructionHandle[types.length];
	for (int i = DOM.NTYPES; i < targets.length; i++) {
	    final TestSeq testSeq = _testSeq[i];
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
	targets[DOM.ROOT] = _rootPattern != null
	    ? getTemplateInstructionHandle(_rootPattern.getTemplate())
	    : ihRecurse;
	
	// Handle any pattern with match on text nodes - default: loop
	targets[DOM.TEXT] = _testSeq[DOM.TEXT] != null
	    ? _testSeq[DOM.TEXT].compile(classGen, methodGen, ihText)
	    : ihText;

	// This DOM-type is not in use - default: process next node
	targets[DOM.NAMESPACE] = ihLoop;

	// Match unknown element in DOM - default: check for namespace match
	targets[DOM.ELEMENT] = elemNamespaceHandle;

	// Match unknown attribute in DOM - default: check for namespace match
	targets[DOM.ATTRIBUTE] = attrNamespaceHandle;

	// Match on processing instruction - default: loop
	InstructionHandle ihPI = ihLoop;
	if (_nodeTestSeq != null) ihPI = ihElem;
	if (_testSeq[DOM.PROCESSING_INSTRUCTION] != null) {
	    targets[DOM.PROCESSING_INSTRUCTION] =
		_testSeq[DOM.PROCESSING_INSTRUCTION].
		compile(classGen, methodGen, ihPI);
	}
	else {
	    targets[DOM.PROCESSING_INSTRUCTION] = ihPI;
	}
	
	// Match on comments - default: process next node
	InstructionHandle ihComment = ihLoop;
	if (_nodeTestSeq != null) ihComment = ihElem;
	targets[DOM.COMMENT] = _testSeq[DOM.COMMENT] != null
	    ? _testSeq[DOM.COMMENT].compile(classGen, methodGen, ihComment)
	    : ihComment;

	// Now compile test sequences for various match patterns:
	for (int i = DOM.NTYPES; i < targets.length; i++) {
	    final TestSeq testSeq = _testSeq[i];
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

	if (ilKey != null) body.insert(ilKey);

	// Append first code in applyTemplates() - get type of current node
	final int getType = cpg.addInterfaceMethodref(DOM_INTF,
						      "getType", "(I)I");
	body.append(methodGen.loadDOM());
	body.append(new ILOAD(_currentIndex));
	body.append(new INVOKEINTERFACE(getType, 2));

	// Append switch() statement - main dispatch loop in applyTemplates()
	InstructionHandle disp = body.append(new SWITCH(types,targets,ihLoop));

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

	peepHoleOptimization(methodGen);
	methodGen.stripAttributes(true);
	
	methodGen.setMaxLocals();
	methodGen.setMaxStack();
	methodGen.removeNOPs();
	classGen.addMethod(methodGen.getMethod());

	// Restore original (complete) set of templates for this transformation
	_templates = oldTemplates;
    }

    /**
     * Peephole optimization: Remove sequences of [ALOAD, POP].
     */
    private void peepHoleOptimization(MethodGenerator methodGen) {
	InstructionList il = methodGen.getInstructionList();
	InstructionFinder find = new InstructionFinder(il);
	InstructionHandle ih;
	String pattern;

	// Remove seqences of ALOAD, POP (GTM)
	pattern = "`ALOAD'`POP'`Instruction'";
	for(Iterator iter=find.search(pattern); iter.hasNext();){
	    InstructionHandle[] match = (InstructionHandle[])iter.next();
	    try {
		if ((!match[0].hasTargeters()) && (!match[1].hasTargeters())) {
                    il.delete(match[0], match[1]);
                }
	    }
	    catch (TargetLostException e) {
                // TODO: move target down into the list
            }
	}
	// Replace sequences of ILOAD_?, ALOAD_?, SWAP with ALOAD_?, ILOAD_?
	pattern = "`ILOAD'`ALOAD'`SWAP'`Instruction'";
	for(Iterator iter=find.search(pattern); iter.hasNext();){
            InstructionHandle[] match = (InstructionHandle[])iter.next();
            try {
                org.apache.bcel.generic.Instruction iload;
                org.apache.bcel.generic.Instruction aload;
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
        }

        // Replace sequences of ALOAD_1, ALOAD_1 with ALOAD_1, DUP 
	pattern = "`ALOAD_1'`ALOAD_1'`Instruction'";
        for(Iterator iter=find.search(pattern); iter.hasNext();){
            InstructionHandle[] match = (InstructionHandle[])iter.next();
            try {
	        org.apache.bcel.generic.Instruction iload;
                org.apache.bcel.generic.Instruction aload;
                if ((!match[0].hasTargeters()) && (!match[1].hasTargeters())) {
                    il.insert(match[1], new DUP());
                    il.delete(match[1]);
                }
            }
            catch (TargetLostException e) {
                // TODO: move target down into the list
            }
        }

    }

    public InstructionHandle getTemplateInstructionHandle(Template template) {
	return (InstructionHandle)_templateIHs.get(template);
    }

    /**
     * Auxiliary method to determine if a qname is an attribute.
     */
    private static boolean isAttributeName(String qname) {
	final int col = qname.lastIndexOf(':') + 1;
	return (qname.charAt(col) == '@');
    }

    /**
     * Auxiliary method to determine if a qname is a namespace 
     * qualified "*".
     */
    private static boolean isNamespaceName(String qname) {
	final int col = qname.lastIndexOf(':');
	return (col > -1 && qname.charAt(qname.length()-1) == '*');
    }
}
