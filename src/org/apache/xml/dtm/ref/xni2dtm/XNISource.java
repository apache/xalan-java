/*
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
 * 4. The name "Apache Software Foundation" must not be used to endorse or
 *    promote products derived from this software without prior written
 *    permission. For written permission, please contact apache@apache.org.
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
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xml.dtm.ref.xni2dtm;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.sax.SAXSource;

import java.lang.String;

import java.io.OutputStream;
import java.io.Writer;

import org.apache.xerces.xni.parser.XMLDocumentScanner;
import org.apache.xerces.xni.parser.XMLInputSource;

/**
 * Acts as an holder for XNI-style Source.
 * Should be made more compatable with JAXP Source
 *   (eg, accept JAXP InputSource and convert to XNI?)
 * Should this be moved to javax.xml.transform?
 */
public class XNISource implements Source {
	XMLDocumentScanner docSource;
	XMLInputSource xniInputSource;

    /**
     * If {@link javax.xml.transform.TransformerFactory#getFeature}
     * returns true when passed this value as an argument,
     * the Transformer supports Source input of this type.
     */
    public static final String FEATURE =
        "http://org.apache.xml.dtm.ref.xni2dtm.XNISource/feature";

    /**
     * Zero-argument default constructor.  If this constructor
     * is used, and no other method is called, the
     * {@link javax.xml.transform.Transformer}
     * assumes an empty input tree, with a default root node.
     */
    public XNISource() {}

    /**
     * Create a <code>XNISource</code>, using an {@link org.xml.XNI.XMLReader}
     * and a XNI InputSource. The {@link javax.xml.transform.Transformer}
     * or {@link javax.xml.transform.XNI.XNITransformerFactory} will set itself
     * to be the reader's {@link org.xml.XNI.ContentHandler}, and then will call
     * reader.parse(inputSource).
     *
     * @param reader An XMLReader to be used for the parse.
     * @param inputSource A XNI input source reference that must be non-null
     * and that will be passed to the reader parse method.
     */
    public XNISource(XMLDocumentScanner reader, XMLInputSource inputSource) {
        this.docSource      = reader;
        this.xniInputSource = inputSource;
    }

    /**
     * Create a <code>XNISource</code>, using a XNI <code>InputSource</code>.
     * The {@link javax.xml.transform.Transformer} or
     * {@link javax.xml.transform.XNI.XNITransformerFactory} creates a
     * reader via {@link org.xml.XNI.helpers.XMLReaderFactory}
     * (if setXMLReader is not used), sets itself as
     * the reader's {@link org.xml.XNI.ContentHandler}, and calls
     * reader.parse(inputSource).
     *
     * @param inputSource An input source reference that must be non-null
     * and that will be passed to the parse method of the reader.
     */
    public XNISource(XMLInputSource inputSource) {
        this.xniInputSource = inputSource;
    }

    /**
	 * Convert SAX source to XNI source (by swiping its input)
     *
     * @param inputSource An input source reference that must be non-null
     * and that will be passed to the parse method of the reader.
     */
    public XNISource(SAXSource saxSource) {
        setInputSource(saxSource.getInputSource());
    }
    
    /**
	 * Read from SAX-style InputSource
     *
     * @param inputSource An input source reference that must be non-null
     * and that will be passed to the parse method of the reader.
     */
    public void setInputSource(org.xml.sax.InputSource saxInputSource) {
    	if(saxInputSource.getCharacterStream()!=null)
			this.xniInputSource=new XMLInputSource(
				saxInputSource.getPublicId(),
				saxInputSource.getSystemId(),
				null,
				saxInputSource.getCharacterStream(),
				saxInputSource.getEncoding()
				);
    	else if(saxInputSource.getByteStream()!=null)
			this.xniInputSource=new XMLInputSource(
				saxInputSource.getPublicId(),
				saxInputSource.getSystemId(),
				null,
				saxInputSource.getByteStream(),
				saxInputSource.getEncoding()
				);
    	else 
			this.xniInputSource=new XMLInputSource(
				saxInputSource.getPublicId(),
				saxInputSource.getSystemId(),
				null);
    }

    /**
     * Set the XMLReader to be used for the Source.
     *
     * @param reader A valid XMLReader or XMLFilter reference.
     */
    public void setXMLReader(org.apache.xerces.xni.parser.XMLDocumentScanner reader) {
        this.docSource = reader;
    }

    /**
     * Get the XMLReader to be used for the Source.
     *
     * @return A valid XMLReader or XMLFilter reference, or null.
     */
    public XMLDocumentScanner getXMLReader() {
        return docSource;
    }

    /**
     * Set the XNI InputSource to be used for the Source.
     *
     * @param inputSource A valid InputSource reference.
     */
    public void setInputSource(XMLInputSource inputSource) {
        this.xniInputSource = inputSource;
    }

    /**
     * Get the XNI InputSource to be used for the Source.
     *
     * @return A valid InputSource reference, or null.
     */
    public XMLInputSource getInputSource() {
        return xniInputSource;
    }

    /**
     * Set the system identifier for this Source.  If an input source
     * has already been set, it will set the system ID or that
     * input source, otherwise it will create a new input source.
     *
     * <p>The system identifier is optional if there is a byte stream
     * or a character stream, but it is still useful to provide one,
     * since the application can use it to resolve relative URIs
     * and can include it in error messages and warnings (the parser
     * will attempt to open a connection to the URI only if
     * no byte stream or character stream is specified).</p>
     *
     * @param systemId The system identifier as a URI string.
     */
    public void setSystemId(String systemId) {

        if (null == xniInputSource) {
            xniInputSource = new XMLInputSource(null,systemId,null);
        } else {
            xniInputSource.setSystemId(systemId);
        }
    }

    /**
     * Get the base ID (URI or system ID) from where URIs
     * will be resolved.
     *
     * @return Base URL for the Source, or null.
     */
    public String getSystemId() {

        return (null != xniInputSource)
               ? xniInputSource.getSystemId()
               : null;
    }
}

