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
 * @author Morten Jorgensen
 *
 */

package org.apache.xalan.xsltc.compiler.util;

import org.apache.xalan.xsltc.compiler.Stylesheet;
import org.apache.xalan.xsltc.compiler.SyntaxTreeNode;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.text.MessageFormat;

public final class ErrorMsg {

    private int _code;
    private int _line;
    private String _message = null;
    private String _url = null;
    Object[] _params = null;

    // Compiler error messages
    public static final int MULTIPLE_STYLESHEET_ERR = 0;
    public static final int TEMPLATE_REDEF_ERR      = 1;
    public static final int TEMPLATE_UNDEF_ERR      = 2;
    public static final int VARIABLE_REDEF_ERR      = 3;
    public static final int VARIABLE_UNDEF_ERR      = 4;
    public static final int CLASS_NOT_FOUND_ERR     = 5;
    public static final int METHOD_NOT_FOUND_ERR    = 6;
    public static final int ARGUMENT_CONVERSION_ERR = 7;
    public static final int FILE_NOT_FOUND_ERR      = 8;
    public static final int INVALID_URI_ERR         = 9;
    public static final int FILE_ACCESS_ERR         = 10;
    public static final int MISSING_ROOT_ERR        = 11;
    public static final int NAMESPACE_UNDEF_ERR     = 12;
    public static final int FUNCTION_RESOLVE_ERR    = 13;
    public static final int NEED_LITERAL_ERR        = 14;
    public static final int XPATH_PARSER_ERR        = 15;
    public static final int REQUIRED_ATTR_ERR       = 16;
    public static final int ILLEGAL_CHAR_ERR        = 17;
    public static final int ILLEGAL_PI_ERR          = 18;
    public static final int STRAY_ATTRIBUTE_ERR     = 19;
    public static final int ILLEGAL_ATTRIBUTE_ERR   = 20;
    public static final int CIRCULAR_INCLUDE_ERR    = 21;
    public static final int RESULT_TREE_SORT_ERR    = 22;
    public static final int SYMBOLS_REDEF_ERR       = 23;
    public static final int XSL_VERSION_ERR         = 24;
    public static final int CIRCULAR_VARIABLE_ERR   = 25;
    public static final int ILLEGAL_BINARY_OP_ERR   = 26;
    public static final int ILLEGAL_ARG_ERR         = 27;
    public static final int DOCUMENT_ARG_ERR        = 28;
    public static final int MISSING_WHEN_ERR        = 29;
    public static final int MULTIPLE_OTHERWISE_ERR  = 30;
    public static final int STRAY_OTHERWISE_ERR     = 31;
    public static final int STRAY_WHEN_ERR          = 32;
    public static final int WHEN_ELEMENT_ERR        = 33;
    public static final int UNNAMED_ATTRIBSET_ERR   = 34;
    public static final int ILLEGAL_CHILD_ERR       = 35;
    public static final int ILLEGAL_ELEM_NAME_ERR   = 36;
    public static final int ILLEGAL_ATTR_NAME_ERR   = 37;
    public static final int ILLEGAL_TEXT_NODE_ERR   = 38;
    public static final int SAX_PARSER_CONFIG_ERR   = 39;
    public static final int INTERNAL_ERR            = 40;
    public static final int UNSUPPORTED_XSL_ERR     = 41;
    public static final int UNSUPPORTED_EXT_ERR     = 42;
    public static final int MISSING_XSLT_URI_ERR    = 43;
    public static final int MISSING_XSLT_TARGET_ERR = 44;
    public static final int NOT_IMPLEMENTED_ERR     = 45;
    public static final int NOT_STYLESHEET_ERR      = 46;
    public static final int ELEMENT_PARSE_ERR       = 47;
    public static final int KEY_USE_ATTR_ERR        = 48;
    public static final int OUTPUT_VERSION_ERR      = 49;
    public static final int ILLEGAL_RELAT_OP_ERR    = 50;
    public static final int ATTRIBSET_UNDEF_ERR     = 51;
    public static final int ATTR_VAL_TEMPLATE_ERR   = 52;
    public static final int UNKNOWN_SIG_TYPE_ERR    = 53;
    public static final int DATA_CONVERSION_ERR     = 54;

    // JAXP/TrAX error messages
    public static final int NO_TRANSLET_CLASS_ERR   = 55;
    public static final int NO_MAIN_TRANSLET_ERR    = 56;
    public static final int TRANSLET_CLASS_ERR      = 57;
    public static final int TRANSLET_OBJECT_ERR     = 58;
    public static final int ERROR_LISTENER_NULL_ERR = 59;
    public static final int JAXP_UNKNOWN_SOURCE_ERR = 60;
    public static final int JAXP_NO_SOURCE_ERR      = 61;
    public static final int JAXP_COMPILE_ERR        = 62;
    public static final int JAXP_INVALID_ATTR_ERR   = 63;
    public static final int JAXP_SET_RESULT_ERR     = 64;
    public static final int JAXP_NO_TRANSLET_ERR    = 65;
    public static final int JAXP_NO_HANDLER_ERR     = 66;
    public static final int JAXP_NO_RESULT_ERR      = 67;
    public static final int JAXP_UNKNOWN_PROP_ERR   = 68;
    public static final int SAX2DOM_ADAPTER_ERR     = 69;
    public static final int XSLTC_SOURCE_ERR        = 70;

    // Command-line error messages
    public static final int COMPILE_STDIN_ERR       = 71;
    public static final int COMPILE_USAGE_STR       = 72;
    public static final int TRANSFORM_USAGE_STR     = 73;

    // Recently added error messages
    public static final int STRAY_SORT_ERR          = 74;
    public static final int UNSUPPORTED_ENCODING    = 75;
    public static final int SYNTAX_ERR              = 76;
    public static final int CONSTRUCTOR_NOT_FOUND   = 77;
    public static final int NO_JAVA_FUNCT_THIS_REF  = 78;

    // All error messages are localized and are stored in resource bundles.
    // This array and the following 4 strings are read from that bundle.
    private static String[] _errorMessages;
    private static String   _compileError;
    private static String   _compileWarning;
    private static String   _runtimeError;
    
    public final static String ERROR_MESSAGES_KEY   = "error-messages";
    public final static String COMPILER_ERROR_KEY   = "compile-error";
    public final static String COMPILER_WARNING_KEY = "compile-warning";
    public final static String RUNTIME_ERROR_KEY    = "runtime-error";

    static {
	ResourceBundle bundle = ResourceBundle.getBundle("org.apache.xalan.xsltc.compiler.util.ErrorMessages", Locale.getDefault());
	_errorMessages  = bundle.getStringArray(ERROR_MESSAGES_KEY);
	_compileError   = bundle.getString(COMPILER_ERROR_KEY);
	_compileWarning = bundle.getString(COMPILER_WARNING_KEY);
	_runtimeError   = bundle.getString(RUNTIME_ERROR_KEY);
    }

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

    public static String getCompileErrorMessage() {
	return _compileError;
    }

    public static String getCompileWarningMessage() {
	return _compileWarning;
    }

    public static String getTransletErrorMessage() {
	return _runtimeError;
    }

    private String getFileName(SyntaxTreeNode node) {
	Stylesheet stylesheet = node.getStylesheet();
	if (stylesheet != null)
	    return stylesheet.getSystemId();
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
	    result.append("line ");
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
	    (_code >= 0 ? new String(_errorMessages[_code]) : _message)
	    : MessageFormat.format(_errorMessages[_code], _params);
	return formatLine() + suffix;
    }
	
    public String toString(Object obj) {
	Object params[] = new Object[1];
	params[0] = obj.toString();
	String suffix = MessageFormat.format(_errorMessages[_code], params);
	return formatLine() + suffix;
    }
	
    public String toString(Object obj0, Object obj1) {
	Object params[] = new Object[2];
	params[0] = obj0.toString();
	params[1] = obj1.toString();
	String suffix = MessageFormat.format(_errorMessages[_code], params);
	return formatLine() + suffix;
    }
}

