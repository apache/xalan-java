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

import org.w3c.dom.*;

import org.apache.xalan.xsltc.compiler.util.Type;
import de.fub.bytecode.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class Text extends Instruction {
    private String _text;
    private boolean _escaping = true;

    public Text() {}

    public Text(String text) {
	_text = text;
    }

    public String getText() {
	return _text;
    }

    public void display(int indent) {
	indent(indent);
	Util.println("Text");
	indent(indent + IndentIncrement);
	Util.println(_text);
    }
		
    public void parseContents(Element element, Parser parser) {
        final String str = element.getAttribute("disable-output-escaping");
	if ((str != null) && (str.equals("yes"))) {
	    _escaping = false;
	}
	final NodeList nl = element.getChildNodes();
	for (int i = 0; i < nl.getLength(); i++) {
	    final Node node = nl.item(i);
	    if (node.getNodeType() == Node.TEXT_NODE) {
		_text = node.getNodeValue();
	    }
	}
    }
	
    public boolean contextDependent() {
	return false;
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	if (_text != null && _text.length() > 0) {
	    // Turn off character escaping if so is wanted.
	    final int esc = cpg.addInterfaceMethodref(OUTPUT_HANDLER,
						      "setEscaping", "(Z)Z");
	    // set escaping value in output handler 
	    if (_escaping) {
		il.append(methodGen.loadHandler());
		il.append(new PUSH(cpg,true));
		il.append(new INVOKEINTERFACE(esc, 2));
	    } else {
		il.append(methodGen.loadHandler());
		il.append(new PUSH(cpg, false));
		il.append(new INVOKEINTERFACE(esc, 2));
	    }

	    il.append(classGen.loadTranslet());
	    il.append(new PUSH(cpg, _text));
	    il.append(methodGen.loadHandler());
	    il.append(new INVOKEVIRTUAL(cpg.addMethodref(TRANSLET_CLASS,
							 CHARACTERSW,
							 CHARACTERSW_SIG)));

	    // Restore character escaping setting to whatever it was.
	    // Note: setEscaping(bool) returns the original (old) value
	    il.append(methodGen.loadHandler());
	    il.append(SWAP);
	    il.append(new INVOKEINTERFACE(esc, 2));
	    il.append(POP);
	}

    }
}
