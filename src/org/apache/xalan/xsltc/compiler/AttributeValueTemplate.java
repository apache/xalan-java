/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id$
 */

package org.apache.xalan.xsltc.compiler;

import java.util.Enumeration;
import java.util.Vector;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.NEW;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

/**
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 */
final class AttributeValueTemplate extends AttributeValue {

    public AttributeValueTemplate(String value, Parser parser, 
	SyntaxTreeNode parent) 
    {
	setParent(parent);
	setParser(parser);
	if (check(value, parser)) {
	    parseAVTemplate(0, value, parser);
	}
    }

    private void parseAVTemplate(final int start, String text, Parser parser) {
	String str;

	if (text == null) return;

	// Get first single opening braces
	int open = start - 2;
	do {
	    open = text.indexOf('{', open+2);
	} while ((open != -1) && 
		 (open < (text.length()-1)) && 
		 (text.charAt(open+1) == '{'));

	if (open != -1) {
	    // Get first single closing braces
	    int close = open - 2;
	    do {
		close = text.indexOf('}', close+2);
	    } while ((close != -1) && 
		     (close < (text.length()-1)) && 
		     (text.charAt(close+1) == '}'));
	    
	    // Add literal expressiong before AVT
	    if (open > start) {
		str = removeDuplicateBraces(text.substring(start, open));
		addElement(new LiteralExpr(str));
	    }
	    // Add the AVT itself
	    if (close > open + 1) {
		str = text.substring(open + 1, close);
		str = removeDuplicateBraces(text.substring(open+1,close));
		addElement(parser.parseExpression(this, str));
	    }
	    // Parse rest of string
	    parseAVTemplate(close + 1, text, parser);
	    
	}
	else if (start < text.length()) {
	    // Add literal expression following AVT
	    str = removeDuplicateBraces(text.substring(start));
	    addElement(new LiteralExpr(str));
	}
    }

    public String removeDuplicateBraces(String orig) {
	String result = orig;
	int index;

	while ((index = result.indexOf("{{")) != -1) {
	    result = result.substring(0,index) + 
		result.substring(index+1,result.length());
	}

	while ((index = result.indexOf("}}")) != -1) {
	    result = result.substring(0,index) + 
		result.substring(index+1,result.length());
	}

	return(result);
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	final Vector contents = getContents();
	final int n = contents.size();
	for (int i = 0; i < n; i++) {
	    final Expression exp = (Expression)contents.elementAt(i);
	    if (!exp.typeCheck(stable).identicalTo(Type.String)) {
		contents.setElementAt(new CastExpr(exp, Type.String), i);
	    }
	}
	return _type = Type.String;
    }

    public String toString() {
	final StringBuffer buffer = new StringBuffer("AVT:[");
	final int count = elementCount();
	for (int i = 0; i < count; i++) {
	    buffer.append(elementAt(i).toString());
	    if (i < count - 1)
		buffer.append(' ');
	}
	return buffer.append(']').toString();
    }
		
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	if (elementCount() == 1) {
	    final Expression exp = (Expression)elementAt(0);
	    exp.translate(classGen, methodGen);
	}
	else {
	    final ConstantPoolGen cpg = classGen.getConstantPool();
	    final InstructionList il = methodGen.getInstructionList();
	    final int initBuffer = cpg.addMethodref(STRING_BUFFER_CLASS,
						    "<init>", "()V");
	    final Instruction append =
		new INVOKEVIRTUAL(cpg.addMethodref(STRING_BUFFER_CLASS,
						   "append",
						   "(" + STRING_SIG + ")"
						   + STRING_BUFFER_SIG));
	    
	    final int toString = cpg.addMethodref(STRING_BUFFER_CLASS,
						  "toString",
						  "()"+STRING_SIG);
	    il.append(new NEW(cpg.addClass(STRING_BUFFER_CLASS)));
	    il.append(DUP);
	    il.append(new INVOKESPECIAL(initBuffer));
	    // StringBuffer is on the stack
	    final Enumeration elements = elements();
	    while (elements.hasMoreElements()) {
		final Expression exp = (Expression)elements.nextElement();
		exp.translate(classGen, methodGen);
		il.append(append);
	    }
	    il.append(new INVOKEVIRTUAL(toString));
	}
    }

    private boolean check(String value, Parser parser) {
	// !!! how about quoted/escaped braces?
	if (value == null) return true;

	final char[] chars = value.toCharArray();
	int level = 0;
	for (int i = 0; i < chars.length; i++) {
	    switch (chars[i]) {
	    case '{':
		if (((i+1) == (chars.length)) || (chars[i+1] != '{'))
		    ++level;
		else
		    i++;
		break;
	    case '}':	
		if (((i+1) == (chars.length)) || (chars[i+1] != '}'))
		    --level;
		else
		    i++;
		break;
	    default:
		continue;
	    }
	    switch (level) {
	    case 0:
	    case 1:
		continue;
	    default:
		reportError(getParent(), parser,
			    ErrorMsg.ATTR_VAL_TEMPLATE_ERR, value);
		return false;
	    }
	}
	if (level != 0) {
	    reportError(getParent(), parser,
			ErrorMsg.ATTR_VAL_TEMPLATE_ERR, value);
	    return false;
	}
	return true;
    }
}
