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

package org.apache.xalan.xsltc.compiler.util;

import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.Constants;
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.Stylesheet;

/**
 * The class that implements any class that inherits from 
 * <tt>AbstractTranslet</tt>, i.e. any translet. Methods in this 
 * class may be of the following kinds: 
 *
 * 1. Main method: applyTemplates, implemented by intances of 
 * <tt>MethodGenerator</tt>.
 *
 * 2. Named methods: for named templates, implemented by instances 
 * of <tt>NamedMethodGenerator</tt>.
 *
 * 3. Rt methods: for result tree fragments, implemented by 
 * instances of <tt>RtMethodGenerator</tt>.
 */
public class ClassGenerator extends ClassGen {
    protected static int TRANSLET_INDEX = 0;
    protected static int INVALID_INDEX  = -1;

    private Stylesheet _stylesheet;
    private final Parser _parser;		// --> can be moved to XSLT
    // a  single instance cached here
    private final Instruction _aloadTranslet;
    private final String _domClass;
    private final String _domClassSig;
    private final String _applyTemplatesSig;

    public ClassGenerator(String class_name, String super_class_name,
			  String file_name,
			  int access_flags, String[] interfaces,
			  Stylesheet stylesheet) {
	super(class_name, super_class_name, file_name,
	      access_flags, interfaces);
	_stylesheet = stylesheet;
	_parser = stylesheet.getParser();
	_aloadTranslet = new ALOAD(TRANSLET_INDEX);
	
	if (stylesheet.isMultiDocument()) {
	    _domClass = "org.apache.xalan.xsltc.dom.MultiDOM";
	    _domClassSig = "Lorg/apache/xalan/xsltc/dom/MultiDOM;";
	}
	else {
	    _domClass = "org.apache.xalan.xsltc.dom.DOMAdapter";
	    _domClassSig = "Lorg/apache/xalan/xsltc/dom/DOMAdapter;";
	}
	_applyTemplatesSig = "(" 
	    + Constants.DOM_INTF_SIG
	    + Constants.NODE_ITERATOR_SIG
	    + Constants.TRANSLET_OUTPUT_SIG
	    + ")V"; 
    }

    public final Parser getParser() {
	return _parser;
    }

    public final Stylesheet getStylesheet() {
	return _stylesheet;
    }

    /**
     * Pretend this is the stylesheet class. Useful when compiling 
     * references to global variables inside a predicate.
     */
    public final String getClassName() {
	return _stylesheet.getClassName();
    }

    public Instruction loadTranslet() {
	return _aloadTranslet;
    }

    public final String getDOMClass() {
	return _domClass;
    }

    public final String getDOMClassSig() {
	return _domClassSig;
    }

    public final String getApplyTemplatesSig() {
	return _applyTemplatesSig;
    }

    /**
     * Returns <tt>true</tt> or <tt>false</tt> depending on whether
     * this class inherits from <tt>AbstractTranslet</tt> or not.
     */
    public boolean isExternal() {
	return false;
    }
}
