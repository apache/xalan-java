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
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES{} LOSS OF
 * USE, DATA, OR PROFITS{} OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
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
 * @author Santiago Pericas-Geertsen
 * @author G. Todd Miller 
 *
 */

package org.apache.xalan.xsltc.runtime.output;

import java.util.Stack;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.runtime.Constants;

abstract class SAXOutput extends OutputBase implements Constants { 

    protected ContentHandler _saxHandler;
    protected LexicalHandler _lexHandler = null;
    protected AttributesImpl _attributes = new AttributesImpl();
    protected String	     _elementName = null;
    protected String         _encoding = null; 

    public SAXOutput(ContentHandler handler, String encoding) {
	_saxHandler = handler;
	_encoding = encoding;	
    } 

    public SAXOutput(ContentHandler hdler, LexicalHandler lex, String encoding) {
	_saxHandler = hdler;
	_lexHandler = lex;
	_encoding = encoding;
    }

    public void startDocument() throws TransletException {
	try {
	    _saxHandler.startDocument();
	} 
	catch (SAXException e) {
	    throw new TransletException(e);
	}
    }

    public void characters(String  characters)
       throws TransletException
    {
	characters(characters.toCharArray(), 0, characters.length());	
    }

    public void comment(String comment) throws TransletException {
	try {
	    // Close any open element before emitting comment
            if (_startTagOpen) {
		closeStartTag();
	    }
	    else if (_cdataTagOpen) {
		closeCDATA();
	    }

	    // Ignore if a lexical handler has not been set
	    if (_lexHandler != null) {
		_lexHandler.comment(comment.toCharArray(), 0, comment.length());
	    }
	}
	catch (SAXException e) {
	    throw new TransletException(e);
	}
    }

    public void processingInstruction(String target, String data) 
	throws TransletException 
    {
        // Redefined in SAXXMLOutput
    }

    protected void closeStartTag() throws TransletException {
    }

    protected void closeCDATA() throws SAXException {
        // Redefined in SAXXMLOutput
    }
}
