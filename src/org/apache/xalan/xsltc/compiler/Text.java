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

import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class Text extends Instruction {

    private String _text;
    private boolean _escaping = true;
    private boolean _ignore = false;
    private boolean _textElement = false;

    /**
     * Create a blank Text syntax tree node.
     */
    public Text() {
	_textElement = true;
    }

    /**
     * Create text syntax tree node.
     * @param text is the text to put in the node.
     */
    public Text(String text) {
	_text = text;
    }

    /**
     * Returns the text wrapped inside this node
     * @return The text wrapped inside this node
     */
    protected String getText() {
	return _text;
    }

    /**
     * Set the text for this node. Appends the given text to any already
     * existing text (using string concatenation, so use only when needed).
     * @param text is the text to wrap inside this node.
     */
    protected void setText(String text) {
	if (_text == null)
	    _text = text;
	else
	    _text = _text + text;
    }

    public void display(int indent) {
	indent(indent);
	Util.println("Text");
	indent(indent + IndentIncrement);
	Util.println(_text);
    }
		
    public void parseContents(Parser parser) {
        final String str = getAttribute("disable-output-escaping");
	if ((str != null) && (str.equals("yes"))) _escaping = false;

	parseChildren(parser);

	if (_text == null) {
	    if (_textElement) {
		_text = EMPTYSTRING;
	    }
	    else {
		_ignore = true;
	    }
	}
	else if (_textElement) {
	    if (_text.length() == 0) _ignore = true;
	}
	else if (getParent() instanceof LiteralElement) {
	    LiteralElement element = (LiteralElement)getParent();
	    String space = element.getAttribute("xml:space");
	    if ((space == null) || (!space.equals("preserve")))
		if (_text.trim().length() == 0) _ignore = true;
	}
	else {
	    if (_text.trim().length() == 0) _ignore = true;
	}
    }

    public void ignore() {
	_ignore = true;
    }

    public boolean isTextElement() {
	return _textElement;
    }

    protected boolean contextDependent() {
	return false;
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	if (!_ignore) {
	    // Turn off character escaping if so is wanted.
	    final int esc = cpg.addInterfaceMethodref(OUTPUT_HANDLER,
						      "setEscaping", "(Z)Z");
	    if (!_escaping) {
		il.append(methodGen.loadHandler());
		il.append(new PUSH(cpg, false));
		il.append(new INVOKEINTERFACE(esc, 2));
	    }

	    final int characters = cpg.addInterfaceMethodref(OUTPUT_HANDLER,
							     "characters",
							     "(" + STRING_SIG + ")V");
	    il.append(methodGen.loadHandler());
	    il.append(new PUSH(cpg, _text));
	    il.append(new INVOKEINTERFACE(characters, 2));

	    // Restore character escaping setting to whatever it was.
	    // Note: setEscaping(bool) returns the original (old) value
	    if (!_escaping) {
		il.append(methodGen.loadHandler());
		il.append(SWAP);
		il.append(new INVOKEINTERFACE(esc, 2));
		il.append(POP);
	    }
	}
	translateContents(classGen, methodGen);
    }
}
