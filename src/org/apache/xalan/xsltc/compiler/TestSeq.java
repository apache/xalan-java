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
 * @author Erwin Bolwidt <ejb@klomp.org>
 * @author Morten Jorgensen <morten.jorgensen@sun.com>
 *
 */

package org.apache.xalan.xsltc.compiler;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Dictionary;
import java.util.Enumeration;

import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

/**
 * A test sequence is a sequence of patterns that
 *
 *  (1) occured in templates in the same mode
 *  (2) share the same kernel node type (such as A/B and C/C/B).
 *
 * A test sequence may have a default template, which will be run if
 * none of the patterns do not match. This template is always a template
 * that matches solely on the shared kernel node type.
 */
final class TestSeq {

    private Vector   _patterns = null; // all patterns
    private Mode     _mode     = null; // the shared mode
    private Template _default  = null; // the default template

    private InstructionList _instructionList;

    /**
     * Creates a new test sequence, given a set of patterns and a mode.
     */
    public TestSeq(Vector patterns, Mode mode) {
	_patterns = patterns;
	_mode = mode;
    }

    /**
     * The priority is only calculated if the test sequence has a default
     * template. This is bad, bad, bad. We should get the priority from the
     * other templates that make up the test sequence.
     */
    public double getPriority() {
	double prio = (0 - Double.MAX_VALUE);
	final int count = _patterns.size();

	for (int i = 0; i < count; i++) {
	    final Pattern pattern = (Pattern)_patterns.elementAt(i);
	    final Template template = pattern.getTemplate();
	    final double tp = template.getPriority();
	    if (tp > prio) prio = tp;
	}
	if (_default != null) {
	    final double tp = _default.getPriority();
	    if (tp > prio) prio = tp;
	}
	return prio;
    }

    /**
     * This method should return the last position of any template included
     * in this test sequence.
     */
    public int getPosition() {
	int pos = Integer.MIN_VALUE;
	final int count = _patterns.size();

	for (int i = 0; i < count; i++) {
	    final Pattern pattern = (Pattern)_patterns.elementAt(i);
	    final Template template = pattern.getTemplate();
	    final int tp = template.getPosition();
	    if (tp > pos) pos = tp;
	}
	if (_default != null) {
	    final int tp = _default.getPosition();
	    if (tp > pos) pos = tp;
	}
	return pos;
    }
	
    /**
     * Reduce the patterns in this test sequence to exclude the shared
     * kernel node type. After the switch() in the translet's applyTemplates()
     * we already know that we have a hit for the kernel node type, we only
     * have the check the rest of the pattern.
     */
    public void reduce() {
	final Vector newPatterns = new Vector();
	final int count = _patterns.size();

	// Traverse the existing set of patterns (they are in prioritised order)
	for (int i = 0; i < count; i++) {
	    final LocationPathPattern pattern =
		(LocationPathPattern)_patterns.elementAt(i);
	    // Reduce this pattern (get rid of kernel node type)
	    pattern.reduceKernelPattern();
			
	    // Add this pattern to the new vector of patterns.
	    if (!pattern.isWildcard()) {
		newPatterns.addElement(pattern);
	    }
	    // Set template as default if its pattern matches purely on kernel
	    else {
		_default = pattern.getTemplate();
		// Following patterns can be ignored since default has priority
		break;
	    }
	}
	_patterns = newPatterns;
    }

    /**
     * Returns, by reference, the templates that are included in this test
     * sequence. Remember that a single template can occur in several test
     * sequences if its pattern is a union (ex. match="A/B | A/C").
     */
    public void findTemplates(Dictionary templates) {
	if (_default != null)
	    templates.put(_default, this);
	for (int i = 0; i < _patterns.size(); i++) {
	    final LocationPathPattern pattern =
		(LocationPathPattern)_patterns.elementAt(i);
	    templates.put(pattern.getTemplate(), this);
	}
    }

    /**
     * Get the instruction handle to a template's code. This is used when
     * a single template occurs in several test sequences; that is, if its
     * pattern is a union of patterns (ex. match="A/B | A/C").
     */
    private InstructionHandle getTemplateHandle(Template template) {
	return (InstructionHandle)_mode.getTemplateInstructionHandle(template);
    }

    /**
     * Returns pattern n in this test sequence
     */
    private LocationPathPattern getPattern(int n) {
	return (LocationPathPattern)_patterns.elementAt(n);
    }


    private InstructionHandle _start = null;

    /**
     * Copile the code for this test sequence. The code will first test for
     * the pattern with the highest priority, then go on to the next ones,
     * until it hits or finds the default template.
     */
    public InstructionHandle compile(ClassGenerator classGen,
				     MethodGenerator methodGen,
				     InstructionHandle continuation) {

	final int count = _patterns.size();
	
	if (_start != null) return(_start);

	// EZ DC if there is only one (default) pattern
	if (count == 0) getTemplateHandle(_default);

	// The 'fail' instruction handle represents a branch to go to when
	// test fails. It is updated in each iteration, so that the tests
	// are linked together in the  if-elseif-elseif-else fashion.
	InstructionHandle fail;
	
	// Initialize 'fail' to either the code for the default template
	if (_default != null)
	    fail = getTemplateHandle(_default);
	// ..or if that does not exist, to a location set by the caller.
	else
	    fail = continuation;

	for (int n = (count - 1); n >= 0; n--) {
	    final LocationPathPattern pattern = getPattern(n);
	    final Template template = pattern.getTemplate();
	    final InstructionList il = new InstructionList();

	    // Patterns expect current node on top of stack
	    il.append(methodGen.loadCurrentNode());
	    // Apply the test-code compiled for the pattern
	    il.append(pattern.compile(classGen, methodGen));

	    // On success branch to the template code
	    final InstructionHandle gtmpl = getTemplateHandle(template);
	    final InstructionHandle success = il.append(new GOTO_W(gtmpl));
	    pattern.backPatchTrueList(success);
	    pattern.backPatchFalseList(fail);

	    // We're working backwards here. The next pattern's 'fail' target
	    // is this pattern's first instruction
	    fail = il.getStart();

	    // Append existing instruction list to the end of this one
	    if (_instructionList != null) il.append(_instructionList);

	    // Set current instruction list to be this one.
	    _instructionList = il;
	}
	return(_start = fail);
    }

    /**
     * Returns the instruction list for this test sequence
     */
    public InstructionList getInstructionList() {
	return _instructionList;
    }

}
