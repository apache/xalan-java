/*
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
package org.apache.xml.serializer;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.LexicalHandler;

abstract public class ToSAXHandler extends SerializerBase 
{
    public ToSAXHandler()
    {
    }

    public ToSAXHandler(
        ContentHandler hdlr,
        LexicalHandler lex,
        String encoding)
    {
        setContentHandler(hdlr);
        setLexHandler(lex);
        setEncoding(encoding);
    }
    public ToSAXHandler(ContentHandler handler, String encoding)
    {
        setContentHandler(handler);
        setEncoding(encoding);
    }

    /**
     * Underlying SAX handler. Taken from XSLTC
     */
    protected ContentHandler m_saxHandler;

    /**
     * Underlying LexicalHandler. Taken from XSLTC
     */
    protected LexicalHandler m_lexHandler;

    
    /** If this is true, then the content handler wrapped by this
     * serializer implements the TransformState interface which
     * will give the content handler access to the state of
     * the transform. */
    protected TransformStateSetter m_state = null;

    /**
     * Pass callback to the SAX Handler
     */
    protected void startDocumentInternal() throws SAXException
    {
        if (m_needToCallStartDocument)  
        {
            super.startDocumentInternal();

            m_saxHandler.startDocument();
            m_needToCallStartDocument = false;
        }
    }
    /**
     * Do nothing.
     * @see org.xml.sax.ext.LexicalHandler#startDTD(String, String, String)
     */
    public void startDTD(String arg0, String arg1, String arg2)
        throws SAXException
    {
        // do nothing for now
    }

    /**
     * Receive notification of character data.
     *
     * @param chars The string of characters to process.
     *
     * @throws org.xml.sax.SAXException
     *
     * @see org.apache.xml.serializer.ExtendedContentHandler#characters(String)
     */
    public void characters(String characters) throws SAXException
    {
        final int len = characters.length();
        if (len > m_charsBuff.length)
        {
           m_charsBuff = new char[len*2 + 1];             
        }
        characters.getChars(0,len, m_charsBuff, 0);   
        characters(m_charsBuff, 0, len);
    }

    /**
     * Receive notification of a comment.
     *
     * @see org.apache.xml.serializer.ExtendedLexicalHandler#comment(String)
     */
    public void comment(String comment) throws SAXException
    {

        // Close any open element before emitting comment
        if (m_elemContext.m_startTagOpen)
        {
            closeStartTag();
        }
        else if (m_cdataTagOpen)
        {
            closeCDATA();
        }

        // Ignore if a lexical handler has not been set
        if (m_lexHandler != null)
        {
            final int len = comment.length();
            if (len > m_charsBuff.length)
            {
               m_charsBuff = new char[len*2 + 1];              
            }
            comment.getChars(0,len, m_charsBuff, 0);            
            m_lexHandler.comment(m_charsBuff, 0, len);
            // time to fire off comment event
            if (m_tracer != null)
                super.fireCommentEvent(m_charsBuff, 0, len);
        }

    }

    /**
     * Do nothing as this is an abstract class. All subclasses will need to
     * define their behavior if it is different.
     * @see org.xml.sax.ContentHandler#processingInstruction(String, String)
     */
    public void processingInstruction(String target, String data)
        throws SAXException
    {
        // Redefined in SAXXMLOutput
    }

    protected void closeStartTag() throws SAXException
    {
    }

    protected void closeCDATA() throws SAXException
    {
        // Redefined in SAXXMLOutput
    }
    
    /**
     * Receive notification of the beginning of an element, although this is a
     * SAX method additional namespace or attribute information can occur before
     * or after this call, that is associated with this element.
     *
     *
     * @param namespaceURI The Namespace URI, or the empty string if the
     *        element has no Namespace URI or if Namespace
     *        processing is not being performed.
     * @param localName The local name (without prefix), or the
     *        empty string if Namespace processing is not being
     *        performed.
     * @param name The element type name.
     * @param atts The attributes attached to the element, if any.
     * @throws org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see org.xml.sax.ContentHandler#startElement
     * @see org.xml.sax.ContentHandler#endElement
     * @see org.xml.sax.AttributeList
     *
     * @throws org.xml.sax.SAXException
     *
     * @see org.xml.sax.ContentHandler#startElement(String,String,String,Attributes)
     */
    public void startElement(
        String arg0,
        String arg1,
        String arg2,
        Attributes arg3)
        throws SAXException
    {
        if (m_state != null) {
            m_state.resetState(getTransformer());
        }

        // fire off the start element event
        if (m_tracer != null)
            super.fireStartElem(arg2);
    }

    /**
     * Sets the LexicalHandler.
     * @param _lexHandler The LexicalHandler to set
     */
    public void setLexHandler(LexicalHandler _lexHandler)
    {
        this.m_lexHandler = _lexHandler;
    }

    /**
     * Sets the SAX ContentHandler.
     * @param m_saxHandler The ContentHandler to set
     */
    public void setContentHandler(ContentHandler _saxHandler)
    {
        this.m_saxHandler = _saxHandler;
        if (m_lexHandler == null && _saxHandler instanceof LexicalHandler)
        {
            // we are not overwriting an existing LexicalHandler, and _saxHandler
            // is also implements LexicalHandler, so lets use it
            m_lexHandler = (LexicalHandler) _saxHandler;
        }
    }

    /**
     * Does nothing. The setting of CDATA section elements has an impact on
     * stream serializers.
     * @see org.apache.xml.serializer.SerializationHandler#setCdataSectionElements(java.util.Vector)
     */
    public void setCdataSectionElements(Vector URI_and_localNames)
    {
        // do nothing
    }

    /**
     * This method flushes any pending events, which can be startDocument()
     * closing the opening tag of an element, or closing an open CDATA section.
     */
    public void flushPending() throws SAXException
    {
    
            if (m_needToCallStartDocument)
            {
                startDocumentInternal();
                m_needToCallStartDocument = false;
            }

            if (m_elemContext.m_startTagOpen)
            {
                closeStartTag();
                m_elemContext.m_startTagOpen = false;
            }
            
            if (m_cdataTagOpen)
            {
                closeCDATA();
                m_cdataTagOpen = false;
            }

    }

    /**
     * Pass in a reference to a TransformState object, which
     * can be used during SAX ContentHandler events to obtain
     * information about he state of the transformation. This
     * method will be called  before each startDocument event.
     *
     * @param ts A reference to a TransformState object
     */
    public void setTransformState(TransformStateSetter ts) {
        this.m_state = ts;
    }
    
    /**
     * Receives notification that an element starts, but attributes are not
     * fully known yet.
     *
     * @param uri the URI of the namespace of the element (optional)
     * @param localName the element name, but without prefix (optional)
     * @param qName the element name, with prefix, if any (required)
     *
     * @see org.apache.xml.serializer.ExtendedContentHandler#startElement(String, String, String)
     */
    public void startElement(String uri, String localName, String qName)
        throws SAXException {
            
        if (m_state != null) {
            m_state.resetState(getTransformer());
        }

        // fire off the start element event
        if (m_tracer != null)
            super.fireStartElem(qName);         
    }

    /**
     * An element starts, but attributes are not fully known yet.
     *
     * @param elementName the element name, with prefix (if any).

     * @see org.apache.xml.serializer.ExtendedContentHandler#startElement(String)
     */
    public void startElement(String qName) throws SAXException {
        if (m_state != null) {
            m_state.resetState(getTransformer());
        }        
        // fire off the start element event
        if (m_tracer != null)
            super.fireStartElem(qName);              
    }
    
    /**
     * This method gets the nodes value as a String and uses that String as if
     * it were an input character notification.
     * @param node the Node to serialize
     * @throws org.xml.sax.SAXException
     */    
    public void characters(org.w3c.dom.Node node)
        throws org.xml.sax.SAXException
    {
        // remember the current node
        if (m_state != null)
        {
            m_state.setCurrentNode(node);
        }
        
        // do what the stream serializers do
        super.characters(node);
       }    

    /**
     * @see org.xml.sax.ErrorHandler#fatalError(SAXParseException)
     */
    public void fatalError(SAXParseException exc) throws SAXException {
        super.fatalError(exc);
        
        m_needToCallStartDocument = false;
        
        if (m_saxHandler instanceof ErrorHandler) {
            ((ErrorHandler)m_saxHandler).fatalError(exc);            
        }
    }

    /**
     * @see org.xml.sax.ErrorHandler#error(SAXParseException)
     */
    public void error(SAXParseException exc) throws SAXException {
        super.error(exc);
        
        if (m_saxHandler instanceof ErrorHandler)
            ((ErrorHandler)m_saxHandler).error(exc);        
        
    }

    /**
     * @see org.xml.sax.ErrorHandler#warning(SAXParseException)
     */
    public void warning(SAXParseException exc) throws SAXException {
        super.warning(exc);
        
        if (m_saxHandler instanceof ErrorHandler)
            ((ErrorHandler)m_saxHandler).warning(exc);        
    }
    
       
    /**
     * Try's to reset the super class and reset this class for 
     * re-use, so that you don't need to create a new serializer 
     * (mostly for performance reasons).
     * 
     * @return true if the class was successfuly reset.
     * @see org.apache.xml.serializer.Serializer#reset()
     */
    public boolean reset()
    {
        boolean wasReset = false;
        if (super.reset())
        {
            resetToSAXHandler();
            wasReset = true;
        }
        return wasReset;
    }
    
    /**
     * Reset all of the fields owned by ToSAXHandler class
     *
     */
    private void resetToSAXHandler()
    {
        this.m_lexHandler = null;
        this.m_saxHandler = null;
        this.m_state = null;
    }  

    /**
     * Add a unique attribute
     */
    public void addUniqueAttribute(String qName, String value, int flags)
        throws SAXException
    {
        addAttribute(qName, value); 
    }
}
