/*
 * @(#)$Id$
 *
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 * @author Santiago Pericas-Geertsen
 *
 */

package org.apache.xalan.xsltc.trax;

import java.io.InputStream;
import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.xalan.xsltc.compiler.XSLTC;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;

import org.w3c.dom.Document;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public final class Util {

    public static String baseName(String name) {
	return org.apache.xalan.xsltc.compiler.util.Util.baseName(name);
    }
    
    public static String noExtName(String name) {
	return org.apache.xalan.xsltc.compiler.util.Util.noExtName(name);
    }

    public static String toJavaName(String name) {
	return org.apache.xalan.xsltc.compiler.util.Util.toJavaName(name);
    }

     

    
    /**
     * Creates a SAX2 InputSource object from a TrAX Source object
     */
    public static InputSource getInputSource(XSLTC xsltc, Source source)
	throws TransformerConfigurationException 
    {
	InputSource input = null;

	String systemId = source.getSystemId();         

	try {
	    // Try to get InputSource from SAXSource input
	    if (source instanceof SAXSource) {
		final SAXSource sax = (SAXSource)source;
		input = sax.getInputSource();
		// Pass the SAX parser to the compiler
                try {
                    XMLReader reader = sax.getXMLReader();

                     /*
                      * Fix for bug 24695
                      * According to JAXP 1.2 specification if a SAXSource
                      * is created using a SAX InputSource the Transformer or
                      * TransformerFactory creates a reader via the
                      * XMLReaderFactory if setXMLReader is not used
                      */

                    if (reader == null) {
                       try {
                           reader= XMLReaderFactory.createXMLReader();
                       } catch (Exception e ) {
                           try {

                               //Incase there is an exception thrown 
                               // resort to JAXP 
                               SAXParserFactory parserFactory = 
                                      SAXParserFactory.newInstance();
                               parserFactory.setNamespaceAware(true);
                               reader = parserFactory.newSAXParser()
                                     .getXMLReader();

                               
                           } catch (ParserConfigurationException pce ) {
                               throw new TransformerConfigurationException
                                 ("ParserConfigurationException" ,pce);
                           }
                       }
                    }
                    reader.setFeature
                        ("http://xml.org/sax/features/namespaces",true);
                    reader.setFeature
                        ("http://xml.org/sax/features/namespace-prefixes",false);

                    xsltc.setXMLReader(reader);
                }catch (SAXNotRecognizedException snre ) {
                  throw new TransformerConfigurationException
                       ("SAXNotRecognizedException ",snre);
                }catch (SAXNotSupportedException snse ) {
                  throw new TransformerConfigurationException
                       ("SAXNotSupportedException ",snse);
                }catch (SAXException se ) {
                  throw new TransformerConfigurationException
                       ("SAXException ",se);
                }

	    }
	    // handle  DOMSource  
	    else if (source instanceof DOMSource) {
		final DOMSource domsrc = (DOMSource)source;
		final Document dom = (Document)domsrc.getNode();
		final DOM2SAX dom2sax = new DOM2SAX(dom);
		xsltc.setXMLReader(dom2sax);  

	        // Try to get SAX InputSource from DOM Source.
		input = SAXSource.sourceToInputSource(source);
		if (input == null){
		    input = new InputSource(domsrc.getSystemId());
		}
	    }
	    // Try to get InputStream or Reader from StreamSource
	    else if (source instanceof StreamSource) {
		final StreamSource stream = (StreamSource)source;
		final InputStream istream = stream.getInputStream();
		final Reader reader = stream.getReader();

		// Create InputSource from Reader or InputStream in Source
		if (istream != null) {
		    input = new InputSource(istream);
		}
		else if (reader != null) {
		    input = new InputSource(reader);
		}
		else {
		    input = new InputSource(systemId);
		}
	    }
	    else {
		ErrorMsg err = new ErrorMsg(ErrorMsg.JAXP_UNKNOWN_SOURCE_ERR);
		throw new TransformerConfigurationException(err.toString());
	    }
	    input.setSystemId(systemId);
	}
	catch (NullPointerException e) {
 	    ErrorMsg err = new ErrorMsg(ErrorMsg.JAXP_NO_SOURCE_ERR,
					"TransformerFactory.newTemplates()");
	    throw new TransformerConfigurationException(err.toString());
	}
	catch (SecurityException e) {
 	    ErrorMsg err = new ErrorMsg(ErrorMsg.FILE_ACCESS_ERR, systemId);
	    throw new TransformerConfigurationException(err.toString());
	}
	return input;
    }

}

