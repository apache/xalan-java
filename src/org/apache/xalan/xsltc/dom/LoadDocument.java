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
 * @author Morten Jorgensen
 *
 */

package org.apache.xalan.xsltc.dom;

import java.io.FileNotFoundException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.DOMCache;
import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.runtime.AbstractTranslet;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.dtm.ref.DTMDefaultBase;
import org.apache.xml.dtm.ref.EmptyIterator;
import org.apache.xml.utils.SystemIDResolver;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public final class LoadDocument {

    private static final String NAMESPACE_FEATURE =
       "http://xml.org/sax/features/namespaces";

    /**
     * Interprets the arguments passed from the document() function (see
     * org/apache/xalan/xsltc/compiler/DocumentCall.java) and returns an
     * iterator containing the requested nodes. Builds a union-iterator if
     * several documents are requested.
     * 2 arguments arg1 and arg2.  document(Obj, node-set) call 
     */
    public static DTMAxisIterator documentF(Object arg1, DTMAxisIterator arg2,
                            String xslURI, AbstractTranslet translet, DOM dom)
    throws TransletException {
        String baseURI = null;
        final int arg2FirstNode = arg2.next();
        if (arg2FirstNode == DTMAxisIterator.END) {
            //  the second argument node-set is empty
            return EmptyIterator.getInstance();
        } else {
            //System.err.println("arg2FirstNode name: "
            //                   + dom.getNodeName(arg2FirstNode )+"["
            //                   +Integer.toHexString(arg2FirstNode )+"]");
            baseURI = getBaseFromURI(dom.getDocumentURI(arg2FirstNode)); 
        }
      
        try {
            if (arg1 instanceof String) {
                if (((String)arg1).length() == 0) {
                    return document(xslURI, "", translet, dom);
                } else {
                    return document((String)arg1, baseURI, translet, dom);
                }
            } else if (arg1 instanceof DTMAxisIterator) {
                return document((DTMAxisIterator)arg1, baseURI, translet, dom);
            } else {
                final String err = "document("+arg1.toString()+")";
                throw new IllegalArgumentException(err);
            }      
        } catch (Exception e) {
            throw new TransletException(e);
        }
    }
    /**
     * Interprets the arguments passed from the document() function (see
     * org/apache/xalan/xsltc/compiler/DocumentCall.java) and returns an
     * iterator containing the requested nodes. Builds a union-iterator if
     * several documents are requested.
     * 1 arguments arg.  document(Obj) call
     */
    public static DTMAxisIterator documentF(Object arg, String xslURI,
                    AbstractTranslet translet, DOM dom)
    throws TransletException {
        try {
            if (arg instanceof String) {
                String baseURI = xslURI;
                if (!SystemIDResolver.isAbsoluteURI(xslURI))
                   baseURI = SystemIDResolver.getAbsoluteURIFromRelative(xslURI);
                
                String href = (String)arg;
                if (href.length() == 0) 
                    href = "";   
                return document(href, baseURI, translet, dom);
            } else if (arg instanceof DTMAxisIterator) {
                return document((DTMAxisIterator)arg, null, translet, dom);
            } else {
                final String err = "document("+arg.toString()+")";
                throw new IllegalArgumentException(err);
            }      
        } catch (Exception e) {
            throw new TransletException(e);
        }
    }
 
 
    private static DTMAxisIterator document(String uri, String base,
                    AbstractTranslet translet, DOM dom)
    throws Exception 
    {
        try {
        final String originalUri = uri;
        MultiDOM multiplexer = (MultiDOM)dom;

        // Prepend URI base to URI (from context)
        if (base != null && !base.equals("")) {
            uri = SystemIDResolver.getAbsoluteURI(uri, base);
        }

        // Return an empty iterator if the URI is clearly invalid
        // (to prevent some unncessary MalformedURL exceptions).
        if (uri == null || uri.equals("")) {
            return(EmptyIterator.getInstance());
        }
        
        // Check if this DOM has already been added to the multiplexer
        int mask = multiplexer.getDocumentMask(uri);
        if (mask != -1) {
            DOM newDom = ((DOMAdapter)multiplexer.getDOMAdapter(uri))
                                       .getDOMImpl();
            if (newDom instanceof SAXImpl) {
                return new SingletonIterator(((SAXImpl)newDom).getDocument(),
                                             true);
            } 
        }

        // Check if we can get the DOM from a DOMCache
        DOMCache cache = translet.getDOMCache();
        DOM newdom;

        mask = multiplexer.nextMask(); // peek

        if (cache != null) {
            newdom = cache.retrieveDocument(base, originalUri, translet);
            if (newdom == null) {
                final Exception e = new FileNotFoundException(originalUri);
                throw new TransletException(e);
            }
        } else {
            // Parse the input document and construct DOM object
            // Create a SAX parser and get the XMLReader object it uses
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            final SAXParser parser = factory.newSAXParser();
            final XMLReader reader = parser.getXMLReader();
            try {
                reader.setFeature(NAMESPACE_FEATURE,true);
            }
            catch (Exception e) {
                throw new TransletException(e);
            }

            // Set the DOM's DOM builder as the XMLReader's SAX2 content handler
            XSLTCDTMManager dtmManager = (XSLTCDTMManager)
                        ((DTMDefaultBase)((DOMAdapter)multiplexer.getMain())
                                               .getDOMImpl()).m_mgr;
            newdom = (SAXImpl)dtmManager.getDTM(
                                new SAXSource(reader, new InputSource(uri)),
                                false, null, true, false, translet.hasIdCall());

            translet.prepassDocument(newdom);

            ((SAXImpl)newdom).setDocumentURI(uri);
        }

        // Wrap the DOM object in a DOM adapter and add to multiplexer
        final DOMAdapter domAdapter = translet.makeDOMAdapter(newdom);
        multiplexer.addDOMAdapter(domAdapter);

        // Create index for any key elements
        translet.buildKeys(domAdapter, null, null,
                           ((SAXImpl)newdom).getDocument());

        // Return a singleton iterator containing the root node
        return new SingletonIterator(((SAXImpl)newdom).getDocument(), true);
        } catch (Exception e) {
            throw e;
        }
    }


    private static DTMAxisIterator document(DTMAxisIterator arg1,
                                            String baseURI,
                                            AbstractTranslet translet, DOM dom)
    throws Exception
    {
        UnionIterator union = new UnionIterator(dom);
        int node = DTM.NULL;

        while ((node = arg1.next()) != DTM.NULL) {
            String uri = dom.getStringValueX(node);
            //document(node-set) if true;  document(node-set,node-set) if false
            if (baseURI  == null) {
               baseURI = dom.getDocumentURI(node);
               if (!SystemIDResolver.isAbsoluteURI(baseURI))
                    baseURI = SystemIDResolver.getAbsoluteURIFromRelative(baseURI);
            }
            union.addIterator(document(uri, baseURI, translet, dom));
        }
        return(union);
    }
 
    private static String getBaseFromURI( String uri){
        final int backwardSep = uri.lastIndexOf('\\') + 1;
        final int forwardSep = uri.lastIndexOf('/') + 1;

        return uri.substring(0, Math.max(backwardSep, forwardSep));
    }

}
