/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights 
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
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xml.utils;

import java.util.Hashtable;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.SAXException;

/**
 * Creates XMLReader objects and caches them for re-use.
 * This class follows the singleton pattern.
 */
public class XMLReaderManager {

    private static final String NAMESPACES_FEATURE =
                             "http://xml.org/sax/features/namespaces";
    private static final String NAMESPACE_PREFIXES_FEATURE =
                             "http://xml.org/sax/features/namespace-prefixes";
    private static final XMLReaderManager m_singletonManager =
                                                     new XMLReaderManager();

    /**
     * Parser factory to be used to construct XMLReader objects
     */
    private static SAXParserFactory m_parserFactory;

    /**
     * Cache of XMLReader objects
     */
    private ThreadLocal m_readers;

    /**
     * Keeps track of whether an XMLReader object is in use.
     */
    private Hashtable m_inUse;

    /**
     * Hidden constructor
     */
    private XMLReaderManager() {
    }

    /**
     * Retrieves the singleton reader manager
     */
    public static XMLReaderManager getInstance() {
        return m_singletonManager;
    }

    /**
     * Retrieves a cached XMLReader for this thread, or creates a new
     * XMLReader, if the existing reader is in use.  When the caller no
     * longer needs the reader, it must release it with a call to
     * {@link releaseXMLReader}.
     */
    public synchronized XMLReader getXMLReader() throws SAXException {
        XMLReader reader;
        boolean readerInUse;

        if (m_readers == null) {
            // When the m_readers.get() method is called for the first time
            // on a thread, a new XMLReader will automatically be created.
            m_readers = new ThreadLocal();
        }

        if (m_inUse == null) {
            m_inUse = new Hashtable();
        }

        // If the cached reader for this thread is in use, construct a new
        // one; otherwise, return the cached reader.
        reader = (XMLReader) m_readers.get();
        boolean threadHasReader = (reader != null);
        if (!threadHasReader || m_inUse.get(reader) == Boolean.TRUE) {
            try {
                try {
                    // According to JAXP 1.2 specification, if a SAXSource
                    // is created using a SAX InputSource the Transformer or
                    // TransformerFactory creates a reader via the
                    // XMLReaderFactory if setXMLReader is not used
                    reader = XMLReaderFactory.createXMLReader();
                } catch (Exception e) {
                   try {
                        // If unable to create an instance, let's try to use
                        // the XMLReader from JAXP
                        if (m_parserFactory == null) {
                            m_parserFactory = SAXParserFactory.newInstance();
                            m_parserFactory.setNamespaceAware(true);
                        }

                        reader = m_parserFactory.newSAXParser().getXMLReader();
                   } catch (ParserConfigurationException pce) {
                       throw pce;   // pass along pce
                   }
                }
                try {
                    reader.setFeature(NAMESPACES_FEATURE, true);
                    reader.setFeature(NAMESPACE_PREFIXES_FEATURE, false);
                } catch (SAXException se) {
                    // Try to carry on if we've got a parser that
                    // doesn't know about namespace prefixes.
                }
            } catch (ParserConfigurationException ex) {
                throw new SAXException(ex);
            } catch (FactoryConfigurationError ex1) {
                throw new SAXException(ex1.toString());
            } catch (NoSuchMethodError ex2) {
            } catch (AbstractMethodError ame) {
            }

            // Cache the XMLReader if this is the first time we've created
            // a reader for this thread.
            if (!threadHasReader) {
                m_readers.set(reader);
                m_inUse.put(reader, Boolean.TRUE);
            }
        } else {
            m_inUse.put(reader, Boolean.TRUE);
        }

        return reader;
    }

    /**
     * Mark the cached XMLReader as available.  If the reader was not
     * actually in the cache, do nothing.
     *
     * @param reader The XMLReader that's being released.
     */
    public synchronized void releaseXMLReader(XMLReader reader) {
        // If the reader that's being released is the cached reader
        // for this thread, mark it as no longer being in use.
        if (m_readers.get() == reader) {
            m_inUse.put(reader, Boolean.FALSE);
        }
    }
}
