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
 * @author G. Todd Miller
 * @author Erwin Bolwidt <ejb@klomp.org>
 *
 */

package org.apache.xalan.xsltc.compiler.util;

import org.apache.xalan.xsltc.compiler.SyntaxTreeNode;

import java.net.URL;
import java.text.MessageFormat;

public final class ErrorMsg {
    private int _code;
    private int _line;
    private String _message = null;
    private String _url = null;
    Object[] _params = null;
	
    public static final int STLREDEF_ERR = 0;
    public static final int TMPREDEF_ERR = 1;
    public static final int VARREDEF_ERR = 2;
    public static final int VARUNDEF_ERR = 3;
    public static final int CLSUNDEF_ERR = 4;
    public static final int METUNDEF_ERR = 5;
    public static final int TMPUNDEF_ERR = 6;
    public static final int CANNOTCV_ERR = 7;
    public static final int FILENOTF_ERR = 8;
    public static final int INVALURI_ERR = 9;
    public static final int FILECANT_ERR = 10;
    public static final int STYORTRA_ERR = 11;
    public static final int NSPUNDEF_ERR = 12;
    public static final int FUNRESOL_ERR = 13;
    public static final int LITERALS_ERR = 14;
    public static final int XPATHPAR_ERR = 15;
    public static final int NREQATTR_ERR = 16;
    public static final int FUNC_USE_ERR = 17;
    public static final int ILLEG_PI_ERR = 18;
    public static final int ATTROUTS_ERR = 19;
    public static final int ILL_ATTR_ERR = 20;

    static final String messages_d[] = { 
	"More than one stylesheet defined in the same file.",
	"Template ''{0}'' already defined in this stylesheet.",
	"Variable ''{0}'' is multiply defined in the same scope.",
	"Variable or parameter ''{0}'' is undefined.",
	"Cannot find external class ''{0}''.",
	"Cannot find external method ''{0}'' (It must be static and public).",
	"Template ''{0}'' not defined in this stylesheet.",
	"Cannot convert argument/return type in call to Method ''{0}'' of class ''{1}''.",
	"File or URI ''{0}'' not found.",
	"Invalid URI ''{0}''.",
	"Cannot open file ''{0}''.",
	"'stylesheet' or 'transform' element expected.",
	"Element prefix ''{0}'' is undeclared.",
	"Unable to resolve call to function ''{0}''.",
	"Argument to ''{0}'' must be a literal string.",
	"Error parsing XPath expression ''{0}''.",
	"Required attribute ''{0}'' is missing.",
	"Illegal use of function ''{0}''.",
	"Illegal name ''{0}'' for processing instruction.",
	"Attribute ''{0}'' outside of element.",
	"Illegal attribute name ''{0}''."
    };

    public ErrorMsg(int code) {
	_code = code;
	_line = 0;
    }
	
    public ErrorMsg(String message) {
	_code = -1;
	_message = message;
	_line = 0;
    }

    public ErrorMsg(String message, int line) {
	_code = -1;
	_message = message;
	_line = line;
    }

    public ErrorMsg(int code, int line, Object param) {
	_code = code;
	_line = line;
	_params = new Object[] { param };
    }

    public ErrorMsg(int code, Object param) {
	this(code);
	_params = new Object[1];
	_params[0] = param;
    }

    public ErrorMsg(int code, Object param1, Object param2) {
	this(code);
	_params = new Object[2];
	_params[0] = param1;
	_params[1] = param2;
    }

    public ErrorMsg(int code, SyntaxTreeNode node) {
	_code = code;
	_url  = getFileName(node);
	_line = node.getLineNumber();
    }

    public ErrorMsg(int code, Object param1, SyntaxTreeNode node) {
	_code = code;
	_url  = getFileName(node);
	_line = node.getLineNumber();
	_params = new Object[1];
	_params[0] = param1;
    }

    public ErrorMsg(int code, Object param1, Object param2,
		    SyntaxTreeNode node) {
	_code = code;
	_url  = getFileName(node);
	_line = node.getLineNumber();
	_params = new Object[2];
	_params[0] = param1;
	_params[1] = param2;
    }

    private String getFileName(SyntaxTreeNode node) {
	final URL url = node.getStylesheet().getURL();
	if (url != null)
	    return url.toString();
	else
	    return null;
    }

    private String formatLine() {
	StringBuffer result = new StringBuffer();
	if (_url != null) {
	    result.append(_url);
	    result.append(": ");
	}
	if (_line > 0) {
	    result.append("Line ");
	    result.append(Integer.toString(_line));
	    result.append(": ");
	}
	return result.toString();
    }
	
    /**
     * This version of toString() uses the _params instance variable
     * to format the message. If the <code>_code</code> is negative
     * the use _message as the error string.
     */
    public String toString() {
	String suffix = (_params == null) ? 
	    (_code >= 0 ? new String(messages_d[_code]) : _message)
	    : MessageFormat.format(messages_d[_code], _params);
	return formatLine() + suffix;
    }
	
    public String toString(Object obj) {
	Object params[] = new Object[1];
	params[0] = obj.toString();
	String suffix = MessageFormat.format(messages_d[_code], params);
	return formatLine() + suffix;
    }
	
    public String toString(Object obj0, Object obj1) {
	Object params[] = new Object[2];
	params[0] = obj0.toString();
	params[1] = obj1.toString();
	String suffix = MessageFormat.format(messages_d[_code], params);
	return formatLine() + suffix;
    }
}

