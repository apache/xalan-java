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
 *
 */

package org.apache.xalan.xsltc.runtime.output;

import java.io.Writer;
import java.io.IOException;
import java.io.OutputStream;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;
import org.apache.xalan.xsltc.runtime.*;
import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.TransletOutputHandler;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xalan.xsltc.trax.SAX2DOM;

public class TransletOutputHandlerFactory {

    public static final int STREAM = 0;
    public static final int SAX    = 1;
    public static final int DOM    = 2;

    private String _encoding       = "utf-8";
    private String _method         = null;
    private int    _outputType     = STREAM;
    private OutputStream _ostream  = System.out;
    private Writer _writer         = null;
    private Node           _node   = null;
    private int _indentNumber      = -1;
    private ContentHandler _handler    = null;
    private LexicalHandler _lexHandler = null;

    static public TransletOutputHandlerFactory newInstance() {
	return new TransletOutputHandlerFactory();
    }

    public void setOutputType(int outputType) {
	_outputType = outputType;
    }

    public void setEncoding(String encoding) {
	if (encoding != null) {
	    _encoding = encoding;
	}
    }

    public void setOutputMethod(String method) {
	_method = method;
    }

    public void setOutputStream(OutputStream ostream) {
	_ostream = ostream;
    }

    public void setWriter(Writer writer) {
	_writer = writer;
    }

    public void setHandler(ContentHandler handler) {
        _handler = handler;
    }

    public void setLexicalHandler(LexicalHandler lex) {
	_lexHandler = lex;
    }

    public void setNode(Node node) {
	_node = node;
    }

    public Node getNode() {
	return (_handler instanceof SAX2DOM) ? ((SAX2DOM)_handler).getDOM() 
	   : null;
    }

    public void setIndentNumber(int value) {
	_indentNumber = value;
    }

    public TransletOutputHandler getTransletOutputHandler() 
	throws IOException, ParserConfigurationException 
    {
	switch (_outputType) {
	    case STREAM:
		StreamOutput result = null;

		if (_method == null) {
		    result = (_writer == null) ? 
			new StreamUnknownOutput(_ostream, _encoding) :
			new StreamUnknownOutput(_writer, _encoding);
		}
		else if (_method.equalsIgnoreCase("xml")) {
		    result = (_writer == null) ? 
			new StreamXMLOutput(_ostream, _encoding) :
			new StreamXMLOutput(_writer, _encoding);
		}
		else if (_method.equalsIgnoreCase("html")) {
		    result = (_writer == null) ? 
			new StreamHTMLOutput(_ostream, _encoding) :
			new StreamHTMLOutput(_writer, _encoding);
		}
		else if (_method.equalsIgnoreCase("text")) {
		    result = (_writer == null) ? 
			new StreamTextOutput(_ostream, _encoding) :
			new StreamTextOutput(_writer, _encoding);
		}

		if (result != null && _indentNumber >= 0) {
		    result.setIndentNumber(_indentNumber);
		}
		return result;
	    case DOM:
		_handler = (_node != null) ? new SAX2DOM(_node) : 
					     new SAX2DOM();
		_lexHandler = (LexicalHandler)_handler;
		// falls through
	    case SAX:
		if (_method == null) {
		    _method = "xml";    // default case
		}

		if (_method.equalsIgnoreCase("xml")) {
		    return (_lexHandler == null) ? 
			new SAXXMLOutput(_handler, _encoding) :
			new SAXXMLOutput(_handler, _lexHandler, _encoding);
		}
		else if (_method.equalsIgnoreCase("html")) {
		    return (_lexHandler == null) ? 
			new SAXHTMLOutput(_handler, _encoding) :
			new SAXHTMLOutput(_handler, _lexHandler, _encoding);
		}
		else if (_method.equalsIgnoreCase("text")) {
		    return (_lexHandler == null) ? 
			new SAXTextOutput(_handler, _encoding) :
			new SAXTextOutput(_handler, _lexHandler, _encoding);
		}
	    break;
	}
	return null;
    }

    // Temporary - returns an instance of TextOutput
    public TransletOutputHandler getOldTransletOutputHandler() throws IOException {
	DefaultSAXOutputHandler saxHandler =
	    new DefaultSAXOutputHandler(_ostream, _encoding);
	return new TextOutput((ContentHandler)saxHandler,
			      (LexicalHandler)saxHandler, _encoding);
    }
}
