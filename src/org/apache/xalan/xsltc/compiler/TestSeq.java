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
 *
 */

package org.apache.xalan.xsltc.compiler;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Dictionary;
import java.util.Enumeration;

import de.fub.bytecode.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class TestSeq {
    private Vector _patterns;
    private final Mode _mode;
    // template to instantiate when all (reduced) tests fail
    // and the sequence is not extended
    // when the _template is null, control should go to 'NextNode'
    private Template _template = null;
    private InstructionList _instructionList;
	
    public TestSeq(Vector patterns, Mode mode) {
	_patterns = patterns;
	_mode = mode;
    }

    public double getPriority() {
	if (_template == null)
	    return(Double.NaN);
	else
	    return(_template.getPriority());
    }

    public int getPosition() {
	if (_template == null)
	    return(0);
	else
	    return(_template.getPosition());
    }
	
    // careful with test ordering
    public void reduce() {
	final int nPatterns = _patterns.size();
	for (int i = 0; i < nPatterns; i++)
	    Util.println(_patterns.elementAt(i).toString());
	final Vector newPatterns = new Vector();
	for (int i = 0; i < nPatterns; i++) {
	    final LocationPathPattern pattern =
		(LocationPathPattern)_patterns.elementAt(i);
	    pattern.reduceKernelPattern();
			
	    // we only retain nontrivial reduced patterns
	    // also, anything that could follow a trivial pattern
	    // can be omitted
	    if (!pattern.isWildcard()) {
		newPatterns.addElement(pattern);
	    }
	    else {
		// failure of the previous test falls through to this fully
		// reduced test, succeeding by definition and branching
		// unconditionally to the test's template
		_template = pattern.getTemplate();
		break;
	    }
	}
	_patterns = newPatterns;
	//Util.println("patterns after reduction");
	for (int i = 0; i < _patterns.size(); i++)
	    Util.println(_patterns.elementAt(i).toString());
    }
	
    public void findTemplates(Dictionary templates) {
	if (_template != null)
	    templates.put(_template, this);
	for (int i = 0; i < _patterns.size(); i++) {
	    final LocationPathPattern pattern =
		(LocationPathPattern)_patterns.elementAt(i);
	    templates.put(pattern.getTemplate(), this);
	}
    }
	
    private InstructionHandle getTemplateHandle(Template template) {
	return (InstructionHandle)_mode.getTemplateInstructionHandle(template);
    }

    private LocationPathPattern getPattern(int n) {
	return (LocationPathPattern)_patterns.elementAt(n);
    }

    public InstructionHandle compile(ClassGenerator classGen,
				     MethodGenerator methodGen,
				     InstructionHandle continuation) {
	if (_patterns.size() == 0) {
	    //Util.println("compiling trivial testseq " + _template);
	    return getTemplateHandle(_template);
	}
	else {
	    // 'fail' represents a branch to go to when test fails
	    // it is updated in each iteration so that the tests
	    // are linked together in the 
	    // if elseif elseif ... else fashion
	    InstructionHandle fail = _template == null
		? continuation
		: getTemplateHandle(_template);
			
	    for (int n = _patterns.size() - 1; n >= 0; n--) {
		final LocationPathPattern pattern = getPattern(n);
		final InstructionList il = new InstructionList();
		// patterns expect current node on top of stack
		il.append(methodGen.loadCurrentNode());
		// apply the actual pattern
		il.append(pattern.compile(classGen, methodGen));
		// on success (fallthrough) goto template code
		final InstructionHandle success =
		    il.append(new GOTO(getTemplateHandle(pattern.getTemplate())));
		pattern.backPatchTrueList(success);
		pattern.backPatchFalseList(fail);
		// the failure of the preceding test will lead to this test
		fail = il.getStart();
		if (_instructionList != null) {
		    il.append(_instructionList);
		}
		_instructionList = il;
	    }
	    return fail;
	}
    }
	
    public InstructionList getInstructionList() {
	return _instructionList;
    }
}
