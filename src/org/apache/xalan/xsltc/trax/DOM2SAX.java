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
 * @author G. Todd Miller 
 *
 */


package org.apache.xalan.xsltc.trax;

import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import java.io.IOException;

/**
 * skeleton extension of XMLFilterImpl for now.  
 */
public class DOM2SAX implements XMLReader {
    public DOM2SAX(Node root) {
    }

    public ContentHandler getContentHandler() { 
	return null;
    }
    public DTDHandler getDTDHandler() { 
	return null;
    }
    public ErrorHandler getErrorHandler() {
	return null;
    }
    public boolean getFeature(String name) throws SAXNotRecognizedException,
	SAXNotSupportedException
    {
	return false;
    }
    public void setFeature(String name, boolean value) throws 
	SAXNotRecognizedException, SAXNotSupportedException 
    {
    }
    public void parse(InputSource input) throws IOException, SAXException {
    }
    public void parse(String sysId) throws IOException, SAXException {
    }
    public void setContentHandler(ContentHandler handler) throws 
	NullPointerException 
    {
	if (handler == null ) throw new NullPointerException();
    }
    public void setDTDHandler(DTDHandler handler) throws NullPointerException {
	if (handler == null )  throw new NullPointerException();
    }
    public void setEntityResolver(EntityResolver resolver) throws 
	NullPointerException 
    {
	if (resolver == null )  throw new NullPointerException();
    }
    public EntityResolver getEntityResolver() {
	return null;
    }
    public void setErrorHandler(ErrorHandler handler) throws 
	NullPointerException
    {
	if (handler == null )  throw new NullPointerException();
    }
    public void setProperty(String name, Object value) throws
	SAXNotRecognizedException, SAXNotSupportedException {
    }
    public Object getProperty(String name) throws SAXNotRecognizedException,
	SAXNotSupportedException
    {
	return null;
    }
}
