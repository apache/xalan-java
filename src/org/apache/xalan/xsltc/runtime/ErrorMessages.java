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
 * @author Morten Jorgensen
 *
 */

package org.apache.xalan.xsltc.runtime;

import java.util.Vector;
import java.util.Enumeration;
import java.util.ResourceBundle;

public class ErrorMessages extends ResourceBundle {

    // These message should be read from a locale-specific resource bundle
    private static final String errorMessages[] = {
	// RUN_TIME_INTERNAL_ERR
	"Run-time internal error in ''{0}''",
	// RUN_TIME_COPY_ERR
	"Run-time error when executing <xsl:copy>.",
	// DATA_CONVERSION_ERR
	"Invalid conversion from ''{0}'' to ''{1}''.",
	// EXTERNAL_FUNC_ERR
	"External function ''{0}'' not supported by XSLTC.",
	// EQUALITY_EXPR_ERR
	"Unknown argument type in equality expression.",
	// INVALID_ARGUMENT_ERR
	"Invalid argument type ''{0}'' in call to ''{1}''",
	// FORMAT_NUMBER_ERR
	"Attempting to format number ''{0}'' using pattern ''{1}''.",
	// ITERATOR_CLONE_ERR
	"Cannot clone iterator ''{0}''.",
	// AXIS_SUPPORT_ERR
	"Iterator for axis ''{0}'' not supported.",
	// TYPED_AXIS_SUPPORT_ERR
	"Iterator for typed axis ''{0}'' not supported.",
	// STRAY_ATTRIBUTE_ERR
	"Attribute ''{0}'' outside of element.",
	// STRAY_NAMESPACE_ERR
	"Namespace declaration ''{0}''=''{1}'' outside of element.",
	// NAMESPACE_PREFIX_ERR
	"Namespace for prefix ''{0}'' has not been declared.",
	// DOM_ADAPTER_INIT_ERR
	"DOMAdapter created using wrong type of source DOM."
    };

    private static Vector _keys;

    static {
	_keys = new Vector();
	_keys.addElement(BasisLibrary.ERROR_MESSAGES_KEY);
    }

    public Enumeration getKeys() {
	return _keys.elements();
    }

    public Object handleGetObject(String key) {
	if (key == null) return null;
	if (key.equals(BasisLibrary.ERROR_MESSAGES_KEY)) return errorMessages;
	return(null);
    }

}
