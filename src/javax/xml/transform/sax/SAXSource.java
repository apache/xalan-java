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
package javax.xml.transform.sax;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import java.lang.String;

import java.io.OutputStream;
import java.io.Writer;

import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.InputSource;


/**
 * Acts as an holder for SAX-style Source.
 */
public class SAXSource implements Source {

    /**
     * If {@link javax.xml.transform.TransformerFactory#getFeature}
     * returns true when passed this value as an argument,
     * the Transformer supports Source input of this type.
     */
    public static final String FEATURE =
        "http://javax.xml.transform.sax.SAXSource/feature";

    /**
     * Zero-argument default constructor.  If this constructor
     * is used, and no other method is called, the
     * {@link javax.xml.transform.Transformer}
     * assumes an empty input tree, with a default root node.
     */
    public SAXSource() {}

    /**
     * Create a <code>SAXSource</code>, using an {@link org.xml.sax.XMLReader}
     * and a SAX InputSource. The {@link javax.xml.transform.Transformer}
     * or {@link javax.xml.transform.sax.SAXTransformerFactory} will set itself
     * to be the reader's {@link org.xml.sax.ContentHandler}, and then will call
     * reader.parse(inputSource).
     *
     * @param reader An XMLReader to be used for the parse.
     * @param inputSource A SAX input source reference that must be non-null
     * and that will be passed to the reader parse method.
     */
    public SAXSource(XMLReader reader, InputSource inputSource) {
        this.reader      = reader;
        this.inputSource = inputSource;
    }

    /**
     * Create a <code>SAXSource</code>, using a SAX <code>InputSource</code>.
     * The {@link javax.xml.transform.Transformer} or
     * {@link javax.xml.transform.sax.SAXTransformerFactory} creates a
     * reader via {@link org.xml.sax.helpers.XMLReaderFactory}
     * (if setXMLReader is not used), sets itself as
     * the reader's {@link org.xml.sax.ContentHandler}, and calls
     * reader.parse(inputSource).
     *
     * @param inputSource An input source reference that must be non-null
     * and that will be passed to the parse method of the reader.
     */
    public SAXSource(InputSource inputSource) {
        this.inputSource = inputSource;
    }

    /**
     * Set the XMLReader to be used for the Source.
     *
     * @param reader A valid XMLReader or XMLFilter reference.
     */
    public void setXMLReader(XMLReader reader) {
        this.reader = reader;
    }

    /**
     * Get the XMLReader to be used for the Source.
     *
     * @return A valid XMLReader or XMLFilter reference, or null.
     */
    public XMLReader getXMLReader() {
        return reader;
    }

    /**
     * Set the SAX InputSource to be used for the Source.
     *
     * @param inputSource A valid InputSource reference.
     */
    public void setInputSource(InputSource inputSource) {
        this.inputSource = inputSource;
    }

    /**
     * Get the SAX InputSource to be used for the Source.
     *
     * @return A valid InputSource reference, or null.
     */
    public InputSource getInputSource() {
        return inputSource;
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

        if (null == inputSource) {
            inputSource = new InputSource(systemId);
        } else {
            inputSource.setSystemId(systemId);
        }
    }

    /**
     * Get the base ID (URI or system ID) from where URIs
     * will be resolved.
     *
     * @return Base URL for the Source, or null.
     */
    public String getSystemId() {

        return (null != inputSource)
               ? inputSource.getSystemId()
               : null;
    }

    /** The XMLReader to be used for the source tree input. May be null.        */
    private XMLReader reader;

    /** The SAX InputSource to be used for the source tree input.  Should not be null. */
    private InputSource inputSource;

    /**
     * Attempt to obtain a SAX InputSource object from a TrAX Source
     * object.
     *
     * @param source Must be a non-null Source reference.
     *
     * @return An InputSource, or null if Source can not be converted.
     */
    public static InputSource sourceToInputSource(Source source) {

        if (source instanceof SAXSource) {
            return ((SAXSource) source).getInputSource();
        } else if (source instanceof StreamSource) {
            StreamSource ss      = (StreamSource) source;
            InputSource  isource = new InputSource(ss.getSystemId());

            isource.setByteStream(ss.getInputStream());
            isource.setCharacterStream(ss.getReader());
            isource.setPublicId(ss.getPublicId());

            return isource;
        } else {
            return null;
        }
    }
}

