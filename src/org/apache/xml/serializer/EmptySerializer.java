/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights reserved.
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
 * originally based on software copyright (c) 2003, International Business
 * Machines, Inc., http://www.ibm.com.  For more information on the Apache
 * Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xml.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.Transformer;

import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This class is an adapter class. Its only purpose is to be extended and
 * for that extended class to over-ride all methods that are to be used. 
 */
public class EmptySerializer implements SerializationHandler
{
    protected static final String ERR = "EmptySerializer method not over-ridden";
    /**
     * @see org.apache.xml.serializer.SerializationHandler#asContentHandler()
     */

    private static void throwUnimplementedException()
    {
        /* TODO: throw this exception for real.
         * Some users of this class do not over-ride all methods that 
         * they use, which is a violation of the intended use of this
         * class. Those tests used to end in error, but fail when this
         * exception is enabled.  Perhaps that is an indication of what
         * the true problem is.  Such tests include copy56,58,59,60 for 
         * both Xalan-J interpretive and for XSLTC. - bjm
         */
        // throw new RuntimeException(err);
        return;
    }
    /**
     * @see org.apache.xml.serializer.SerializationHandler#asContentHandler()
     */
    public ContentHandler asContentHandler() throws IOException
    {
        throwUnimplementedException();
        return null;
    }
    /**
     * @see org.apache.xml.serializer.SerializationHandler#setContentHandler(org.xml.sax.ContentHandler)
     */
    public void setContentHandler(ContentHandler ch)
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.SerializationHandler#close()
     */
    public void close()
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.SerializationHandler#getOutputFormat()
     */
    public Properties getOutputFormat()
    {
        throwUnimplementedException();
        return null;
    }
    /**
     * @see org.apache.xml.serializer.SerializationHandler#getOutputStream()
     */
    public OutputStream getOutputStream()
    {
        throwUnimplementedException();
        return null;
    }
    /**
     * @see org.apache.xml.serializer.SerializationHandler#getWriter()
     */
    public Writer getWriter()
    {
        throwUnimplementedException();
        return null;
    }
    /**
     * @see org.apache.xml.serializer.SerializationHandler#reset()
     */
    public boolean reset()
    {
        throwUnimplementedException();
        return false;
    }
    /**
     * @see org.apache.xml.serializer.SerializationHandler#serialize(org.w3c.dom.Node)
     */
    public void serialize(Node node) throws IOException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.SerializationHandler#setCdataSectionElements(java.util.Vector)
     */
    public void setCdataSectionElements(Vector URI_and_localNames)
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.SerializationHandler#setEscaping(boolean)
     */
    public boolean setEscaping(boolean escape) throws SAXException
    {
        throwUnimplementedException();
        return false;
    }
    /**
     * @see org.apache.xml.serializer.SerializationHandler#setIndent(boolean)
     */
    public void setIndent(boolean indent)
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.SerializationHandler#setIndentAmount(int)
     */
    public void setIndentAmount(int spaces)
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.SerializationHandler#setOutputFormat(java.util.Properties)
     */
    public void setOutputFormat(Properties format)
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.SerializationHandler#setOutputStream(java.io.OutputStream)
     */
    public void setOutputStream(OutputStream output)
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.SerializationHandler#setVersion(java.lang.String)
     */
    public void setVersion(String version)
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.SerializationHandler#setWriter(java.io.Writer)
     */
    public void setWriter(Writer writer)
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.SerializationHandler#setTransformer(javax.xml.transform.Transformer)
     */
    public void setTransformer(Transformer transformer)
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.SerializationHandler#getTransformer()
     */
    public Transformer getTransformer()
    {
        throwUnimplementedException();
        return null;
    }
    /**
     * @see org.apache.xml.serializer.SerializationHandler#flushPending()
     */
    public void flushPending()
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.ExtendedContentHandler#addAttribute(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void addAttribute(
        String uri,
        String localName,
        String rawName,
        String type,
        String value)
        throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.ExtendedContentHandler#addAttributes(org.xml.sax.Attributes)
     */
    public void addAttributes(Attributes atts) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.ExtendedContentHandler#addAttribute(java.lang.String, java.lang.String)
     */
    public void addAttribute(String name, String value)
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.ExtendedContentHandler#characters(java.lang.String)
     */
    public void characters(String chars) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.ExtendedContentHandler#endElement(java.lang.String)
     */
    public void endElement(String elemName) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.ExtendedContentHandler#startDocument()
     */
    public void startDocument() throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.ExtendedContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void startElement(String uri, String localName, String qName)
        throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.ExtendedContentHandler#startElement(java.lang.String)
     */
    public void startElement(String qName) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.ExtendedContentHandler#namespaceAfterStartElement(java.lang.String, java.lang.String)
     */
    public void namespaceAfterStartElement(String uri, String prefix)
        throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.ExtendedContentHandler#startPrefixMapping(java.lang.String, java.lang.String, boolean)
     */
    public boolean startPrefixMapping(
        String prefix,
        String uri,
        boolean shouldFlush)
        throws SAXException
    {
        throwUnimplementedException();
        return false;
    }
    /**
     * @see org.apache.xml.serializer.ExtendedContentHandler#entityReference(java.lang.String)
     */
    public void entityReference(String entityName) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.ExtendedContentHandler#getNamespaceMappings()
     */
    public NamespaceMappings getNamespaceMappings()
    {
        throwUnimplementedException();
        return null;
    }
    /**
     * @see org.apache.xml.serializer.ExtendedContentHandler#getPrefix(java.lang.String)
     */
    public String getPrefix(String uri)
    {
        throwUnimplementedException();
        return null;
    }
    /**
     * @see org.apache.xml.serializer.ExtendedContentHandler#getNamespaceURI(java.lang.String, boolean)
     */
    public String getNamespaceURI(String name, boolean isElement)
    {
        throwUnimplementedException();
        return null;
    }
    /**
     * @see org.apache.xml.serializer.ExtendedContentHandler#getNamespaceURIFromPrefix(java.lang.String)
     */
    public String getNamespaceURIFromPrefix(String prefix)
    {
        throwUnimplementedException();
        return null;
    }
    /**
     * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
     */
    public void setDocumentLocator(Locator arg0)
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    public void endDocument() throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
     */
    public void startPrefixMapping(String arg0, String arg1)
        throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
     */
    public void endPrefixMapping(String arg0) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(
        String arg0,
        String arg1,
        String arg2,
        Attributes arg3)
        throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String arg0, String arg1, String arg2)
        throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    public void characters(char[] arg0, int arg1, int arg2) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
     */
    public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
        throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
     */
    public void processingInstruction(String arg0, String arg1)
        throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
     */
    public void skippedEntity(String arg0) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.ExtendedLexicalHandler#comment(java.lang.String)
     */
    public void comment(String comment) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ext.LexicalHandler#startDTD(java.lang.String, java.lang.String, java.lang.String)
     */
    public void startDTD(String arg0, String arg1, String arg2)
        throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ext.LexicalHandler#endDTD()
     */
    public void endDTD() throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ext.LexicalHandler#startEntity(java.lang.String)
     */
    public void startEntity(String arg0) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ext.LexicalHandler#endEntity(java.lang.String)
     */
    public void endEntity(String arg0) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ext.LexicalHandler#startCDATA()
     */
    public void startCDATA() throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ext.LexicalHandler#endCDATA()
     */
    public void endCDATA() throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ext.LexicalHandler#comment(char[], int, int)
     */
    public void comment(char[] arg0, int arg1, int arg2) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.XSLOutputAttributes#getDoctypePublic()
     */
    public String getDoctypePublic()
    {
        throwUnimplementedException();
        return null;
    }
    /**
     * @see org.apache.xml.serializer.XSLOutputAttributes#getDoctypeSystem()
     */
    public String getDoctypeSystem()
    {
        throwUnimplementedException();
        return null;
    }
    /**
     * @see org.apache.xml.serializer.XSLOutputAttributes#getEncoding()
     */
    public String getEncoding()
    {
        throwUnimplementedException();
        return null;
    }
    /**
     * @see org.apache.xml.serializer.XSLOutputAttributes#getIndent()
     */
    public boolean getIndent()
    {
        throwUnimplementedException();
        return false;
    }
    /**
     * @see org.apache.xml.serializer.XSLOutputAttributes#getIndentAmount()
     */
    public int getIndentAmount()
    {
        throwUnimplementedException();
        return 0;
    }
    /**
     * @see org.apache.xml.serializer.XSLOutputAttributes#getMediaType()
     */
    public String getMediaType()
    {
        throwUnimplementedException();
        return null;
    }
    /**
     * @see org.apache.xml.serializer.XSLOutputAttributes#getOmitXMLDeclaration()
     */
    public boolean getOmitXMLDeclaration()
    {
        throwUnimplementedException();
        return false;
    }
    /**
     * @see org.apache.xml.serializer.XSLOutputAttributes#getStandalone()
     */
    public String getStandalone()
    {
        throwUnimplementedException();
        return null;
    }
    /**
     * @see org.apache.xml.serializer.XSLOutputAttributes#getVersion()
     */
    public String getVersion()
    {
        throwUnimplementedException();
        return null;
    }
    /**
     * @see org.apache.xml.serializer.XSLOutputAttributes#setCdataSectionElements(java.util.Hashtable)
     */
    public void setCdataSectionElements(Hashtable h) throws Exception
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.XSLOutputAttributes#setDoctype(java.lang.String, java.lang.String)
     */
    public void setDoctype(String system, String pub)
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.XSLOutputAttributes#setDoctypePublic(java.lang.String)
     */
    public void setDoctypePublic(String doctype)
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.XSLOutputAttributes#setDoctypeSystem(java.lang.String)
     */
    public void setDoctypeSystem(String doctype)
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.XSLOutputAttributes#setEncoding(java.lang.String)
     */
    public void setEncoding(String encoding)
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.XSLOutputAttributes#setMediaType(java.lang.String)
     */
    public void setMediaType(String mediatype)
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.XSLOutputAttributes#setOmitXMLDeclaration(boolean)
     */
    public void setOmitXMLDeclaration(boolean b)
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.XSLOutputAttributes#setStandalone(java.lang.String)
     */
    public void setStandalone(String standalone)
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ext.DeclHandler#elementDecl(java.lang.String, java.lang.String)
     */
    public void elementDecl(String arg0, String arg1) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ext.DeclHandler#attributeDecl(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void attributeDecl(
        String arg0,
        String arg1,
        String arg2,
        String arg3,
        String arg4)
        throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ext.DeclHandler#internalEntityDecl(java.lang.String, java.lang.String)
     */
    public void internalEntityDecl(String arg0, String arg1)
        throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ext.DeclHandler#externalEntityDecl(java.lang.String, java.lang.String, java.lang.String)
     */
    public void externalEntityDecl(String arg0, String arg1, String arg2)
        throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
     */
    public void warning(SAXParseException arg0) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
     */
    public void error(SAXParseException arg0) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public void fatalError(SAXParseException arg0) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.Serializer#asDOMSerializer()
     */
    public DOMSerializer asDOMSerializer() throws IOException
    {
        throwUnimplementedException();
        return null;
    }

    /**
     * @see org.apache.xml.serializer.SerializationHandler#setNamespaceMappings(NamespaceMappings)
     */
    public void setNamespaceMappings(NamespaceMappings mappings) {
        throwUnimplementedException();
    }
    /**
     * @see org.apache.xml.serializer.ExtendedContentHandler#setSourceLocator(javax.xml.transform.SourceLocator)
     */
    public void setSourceLocator(SourceLocator locator)
    {
        throwUnimplementedException();
    }

}
