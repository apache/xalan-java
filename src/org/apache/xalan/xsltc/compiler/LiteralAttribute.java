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
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 * @author Morten Jorgensen
 *
 */

package org.apache.xalan.xsltc.compiler;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xalan.xsltc.compiler.util.Util;

import org.apache.xml.serializer.ElemDesc;
import org.apache.xml.serializer.SerializationHandler;

final class LiteralAttribute extends Instruction {

    private final String  _name;         // Attribute name (incl. prefix)
    private final AttributeValue _value; // Attribute value

    /**
     * Creates a new literal attribute (but does not insert it into the AST).
     * @param name the attribute name (incl. prefix) as a String.
     * @param value the attribute value.
     * @param parser the XSLT parser (wraps XPath parser).
     */
    public LiteralAttribute(String name, String value, Parser parser) {
	_name = name;
	_value = AttributeValue.create(this, value, parser);
    }

    public void display(int indent) {
	indent(indent);
	Util.println("LiteralAttribute name=" + _name + " value=" + _value);
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	_value.typeCheck(stable);
	typeCheckContents(stable);
	return Type.Void;
    }

    protected boolean contextDependent() {
	return _value.contextDependent();
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	// push handler
	il.append(methodGen.loadHandler());
	// push attribute name - namespace prefix set by parent node
	il.append(new PUSH(cpg, _name));
	// push attribute value
	_value.translate(classGen, methodGen);
	
	// Generate code that calls SerializationHandler.addUniqueAttribute()
	// if all attributes are unique.
	SyntaxTreeNode parent = getParent();
	if (parent instanceof LiteralElement
	    && ((LiteralElement)parent).allAttributesUnique()) {	    
	    
	    int flags = 0;
	    boolean isHTMLAttrEmpty = false;
	    ElemDesc elemDesc = ((LiteralElement)parent).getElemDesc();
	    
	    // Set the HTML flags
	    if (elemDesc != null) {
	    	if (elemDesc.isAttrFlagSet(_name, ElemDesc.ATTREMPTY)) {
	    	    flags = flags | SerializationHandler.HTML_ATTREMPTY;
	    	    isHTMLAttrEmpty = true;
	    	}
	    	else if (elemDesc.isAttrFlagSet(_name, ElemDesc.ATTRURL)) {
	    	    flags = flags | SerializationHandler.HTML_ATTRURL;
	    	}
	    }
	    
	    if (_value instanceof SimpleAttributeValue) {
	        String attrValue = ((SimpleAttributeValue)_value).toString();
	        
	        if (!hasBadChars(attrValue) && !isHTMLAttrEmpty) {
	            flags = flags | SerializationHandler.NO_BAD_CHARS;
	        }
	    }
	        
	    il.append(new PUSH(cpg, flags));
	    il.append(methodGen.uniqueAttribute());
	}
	else {
	    // call attribute
	    il.append(methodGen.attribute());
	}
    }
    
    /**
     * Return true if at least one character in the String is considered to
     * be a "bad" character. A bad character is one whose code is:
     * less than 32 (a space),
     * or greater than 126,
     * or it is one of '<', '>', '&' or '\"'. 
     * This helps the serializer to decide whether the String needs to be escaped.
     */
    private boolean hasBadChars(String value) {
    	char[] chars = value.toCharArray();
    	int size = chars.length;
    	for (int i = 0; i < size; i++) {
    	    char ch = chars[i];
    	    if (ch < 32 || 126 < ch || ch == '<' || ch == '>' || ch == '&' || ch == '\"')
                return true;    	        
    	}
    	return false;
    }
    
    /**
     * Return the name of the attribute
     */
    public String getName() {
        return _name;
    }
    
    /**
     * Return the value of the attribute
     */
    public AttributeValue getValue() {
        return _value;
    }
    
}
