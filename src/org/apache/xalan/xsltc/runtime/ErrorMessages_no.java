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

public class ErrorMessages_no extends ErrorMessages {

    // Disse feilmeldingene maa korrespondere med konstantene some er definert
    // nederst i kildekoden til BasisLibrary.
    private static final String errorMessages[] = {
	// RUN_TIME_INTERNAL_ERR
	"Intern programfeil i ''{0}''",
	// RUN_TIME_COPY_ERR
	"Programfeil under utf\u00f8ing av <xsl:copy>.",
	// DATA_CONVERSION_ERR
	"Ugyldig konvertering av ''{0}'' fra ''{1}''.",
	// EXTERNAL_FUNC_ERR
	"Ekstern funksjon ''{0}'' er ikke st\u00f8ttet av XSLTC.",
	// EQUALITY_EXPR_ERR
	"Ugyldig argument i EQUALITY uttrykk.",
	// INVALID_ARGUMENT_ERR
	"Ugyldig argument ''{0}'' i kall til ''{1}''",
	// FORMAT_NUMBER_ERR
	"Fors\u00f8k p\u00e5 \u00e5 formattere nummer ''{0}'' med ''{1}''.",
	// ITERATOR_CLONE_ERR
	"Kan ikke klone iterator ''{0}''.",
	// AXIS_SUPPORT_ERR
	"Iterator for axis ''{0}'' er ikke st\u00e8ttet.",
	// TYPED_AXIS_SUPPORT_ERR
	"Iterator for typet axis ''{0}'' er ikke st\u00e8ttet.",
	// STRAY_ATTRIBUTE_ERR
	"Attributt ''{0}'' utenfor element.",
	// STRAY_NAMESPACE_ERR
	"Navnedeklarasjon ''{0}''=''{1}'' utenfor element.",
	// NAMESPACE_PREFIX_ERR
	"Prefix ''{0}'' er ikke deklartert.",
	// DOM_ADAPTER_INIT_ERR
	"Fors\u00f8k p\u00e5 \u00e5 instansiere DOMAdapter med feil type DOM."
    };

    public Object handleGetObject(String key) {
	if (key == null) return null;
	if (key.equals(BasisLibrary.ERROR_MESSAGES_KEY)) return errorMessages;
	return(null);
    }

}
