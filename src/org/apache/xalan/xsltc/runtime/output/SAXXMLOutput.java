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
import org.xml.sax.ext.LexicalHandler;
import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.runtime.Hashtable;
import org.xml.sax.SAXException;
import org.apache.xalan.xsltc.runtime.BasisLibrary;
import org.apache.xalan.xsltc.runtime.DefaultSAXOutputHandler;

public class SAXXMLOutput extends SAXOutput  {

    // Each entry (prefix) in this hashtable points to a Stack of URIs
    private Hashtable _namespaces;
    // The top of this stack contains an id of the element that last declared
    // a namespace. Used to ensure prefix/uri map scopes are closed correctly
    private Stack     _nodeStack;
    // The top of this stack is the prefix that was last mapped to an URI
    private Stack     _prefixStack;


    // Contains all elements that should be output as CDATA sections
    private Hashtable _cdata = null;

    private boolean   _cdataTagOpen = false;
    // The top of this stack contains the element id of the last element whose
    // contents should be output as CDATA sections.
    private Stack     _cdataStack;

    // Holds the current tree depth (see startElement() and endElement()).
    private int _depth = 0;

    // The top of this stack contains the QName of the currently open element
    private Stack     _qnameStack;

    private static final char[] BEGCDATA = "<![CDATA[".toCharArray();
    private static final char[] ENDCDATA = "]]>".toCharArray();
    private static final char[] CNTCDATA = "]]]]><![CDATA[>".toCharArray();
    

    public SAXXMLOutput(ContentHandler handler, String encoding) {
    	super(handler, encoding);
    }

    public SAXXMLOutput(ContentHandler handler, LexicalHandler lex, 
        String encoding)
    {
        super(handler, lex, encoding);
    }

    public void endDocument() throws TransletException {
	try {
	    // Close any open start tag
            if (_startTagOpen) closeStartTag();
            if (_cdataTagOpen) closeCDATA();

            // Close output document
            _saxHandler.endDocument();
        } catch (SAXException e) {
            throw new TransletException(e);
        }
    }

    /**
     * This method is called when all the data needed for a call to the
     * SAX handler's startElement() method has been gathered.
     */
    public void closeStartTag() throws TransletException {
        try {
            _startTagOpen = false;

            // Now is time to send the startElement event
            _saxHandler.startElement(getNamespaceURI(_elementName, true),
                getLocalName(_elementName), _elementName, _attributes);
        }
        catch (SAXException e) {
            throw new TransletException(e);
        }
    }

    /**
     * Returns the URI of an element or attribute. Note that default namespaces
     * do not apply directly to attributes.
     */
    private String getNamespaceURI(String qname, boolean isElement)
        throws TransletException
    {
        String uri = EMPTYSTRING;
        int col = qname.lastIndexOf(':');
        final String prefix = (col > 0) ? qname.substring(0, col) : EMPTYSTRING;

        if (prefix != EMPTYSTRING || isElement) {
            uri = lookupNamespace(prefix);
            if (uri == null && !prefix.equals(XMLNS_PREFIX)) {
                BasisLibrary.runTimeError(BasisLibrary.NAMESPACE_PREFIX_ERR,
                                          qname.substring(0, col));
            }
        }
        return uri;
    }

    /**
     * Use a namespace prefix to lookup a namespace URI
     */
    private String lookupNamespace(String prefix) {
        final Stack stack = (Stack)_namespaces.get(prefix);
        return stack != null && !stack.isEmpty() ? (String)stack.peek() : null;
    }

    
    private void closeCDATA() throws SAXException {
        // Output closing bracket - "]]>"
        _saxHandler.characters(ENDCDATA, 0, ENDCDATA.length);
        _cdataTagOpen = false;
    }

    /**
     * Put an attribute and its value in the start tag of an element.
     * Signal an exception if this is attempted done outside a start tag.
     */
    public void attribute(String name, final String value)
        throws TransletException 
    {
	final String patchedName = patchQName(name);
        final String localName = getLocalName(patchedName);
        final String uri = getNamespaceURI(patchedName, false);
        final int index = (localName == null) ?
                _attributes.getIndex(name) :    /* don't use patchedName */
                _attributes.getIndex(uri, localName);
        if (!_startTagOpen) {
            BasisLibrary.runTimeError(BasisLibrary.STRAY_ATTRIBUTE_ERR,
                patchedName);
        }

	// Output as namespace declaration
        if (name.startsWith(XMLNS_PREFIX)) {
            namespace(name.length() > 6 ? name.substring(6) : EMPTYSTRING,
                value);
        }
        else {
            if (index >= 0) {       // Duplicate attribute?
                _attributes.setAttribute(index, uri, localName,
                    patchedName, "CDATA", value);
            }
            else {
                _attributes.addAttribute(uri, localName, patchedName,
                    "CDATA", value);
            }
        }
    }

    public void characters(char[] ch, int off, int len)
        throws TransletException 
    {
	try {
            // Close any open start tag
            if (_startTagOpen) closeStartTag();

            // Take special precautions if within a CDATA section. If we
            // encounter the sequence ']]>' within the CDATA, we need to
            // break the section in two and leave the ']]' at the end of
            // the first CDATA and '>' at the beginning of the next. Other
            // special characters/sequences are _NOT_ escaped within CDATA.
            Integer I = (Integer)_cdataStack.peek();
            if ((I.intValue() == _depth) && (!_cdataTagOpen)) {
                startCDATA(ch, off, len);
            }
            else {
                _saxHandler.characters(ch, off, len);
            }
	}
        catch (SAXException e) {
            throw new TransletException(e);
        }
    }

    public void endElement(String elementName) throws TransletException {
        try {
            // Close any open element
            if (_startTagOpen) closeStartTag();
            if (_cdataTagOpen) closeCDATA();

            final String qname = (String) _qnameStack.pop();
            _saxHandler.endElement(getNamespaceURI(qname, true),
                getLocalName(qname), qname);

            popNamespaces();
            if (((Integer)_cdataStack.peek()).intValue() == _depth){
                _cdataStack.pop();
	    }
            _depth--;

    	} catch (SAXException e) {
            throw new TransletException(e);
    	}
    }

    /**
     * The <xsl:output method="xml"/> instruction can specify that certain
     * XML elements should be output as CDATA sections. This methods allows
     * the translet to insert these elements into a hashtable of strings.
     * Every output element is looked up in this hashtable before it is
     * output.
     */
    public void setCdataElements(Hashtable elements) {
        _cdata = elements;
    }

    /**
     * Set the XML output document version - should be 1.0
     */
    public void setVersion(String version) {
        if (_saxHandler instanceof DefaultSAXOutputHandler) {
            ((DefaultSAXOutputHandler)_saxHandler).setVersion(version);
        }
    }

    /**
     * Start an element in the output document. This might be an XML
     * element (<elem>data</elem> type) or a CDATA section.
     */
    public void startElement(String elementName) throws TransletException {
	try {
            // Close any open start tag
            if (_startTagOpen) closeStartTag();
            if (_cdataTagOpen) closeCDATA();

            // Handle document type declaration (for first element only)
            if (_lexHandler != null) {
                if (_doctypeSystem != null) {
                    _lexHandler.startDTD(elementName,
                         _doctypePublic,_doctypeSystem);
		}
                _lexHandler = null;
            }

            _depth++;
            _elementName = elementName;
            _attributes.clear();
            _startTagOpen = true;
            _qnameStack.push(elementName);

            if ((_cdata != null) && (_cdata.get(elementName) != null)) {
                _cdataStack.push(new Integer(_depth));
	    }
	}
        catch (SAXException e) {
            throw new TransletException(e);
        }
    }

    /**
     * Initialize namespace stacks
     */
    private void initNamespaces() {
        _namespaces = new Hashtable();
        _nodeStack = new Stack();
        _prefixStack = new Stack();

        // Define the default namespace (initially maps to "" uri)
        Stack stack;
        _namespaces.put(EMPTYSTRING, stack = new Stack());
        stack.push(EMPTYSTRING);
        _prefixStack.push(EMPTYSTRING);

        _namespaces.put(XML_PREFIX, stack = new Stack());
        stack.push("http://www.w3.org/XML/1998/namespace");
        _prefixStack.push(XML_PREFIX);

        _nodeStack.push(new Integer(-1));
        _depth = 0;
    }

    /**
     * Pop all namespace definitions that were delcared by the current element
     */
    private void popNamespaces() throws TransletException {
        try {
            while (true) {
                if (_nodeStack.isEmpty()) return;
                Integer i = (Integer)(_nodeStack.peek());
                if (i.intValue() != _depth) return;
                _nodeStack.pop();
                popNamespace((String)_prefixStack.pop());
            }
        }
        catch (SAXException e) {
            throw new TransletException(e);
        }
    }


    /**
     * Undeclare the namespace that is currently pointed to by a given prefix
     */
    private void popNamespace(String prefix) throws SAXException {
        // Prefixes "xml" and "xmlns" cannot be redefined
        if (prefix.equals(XML_PREFIX)) return;

        Stack stack;
        if ((stack = (Stack)_namespaces.get(prefix)) != null) {
            stack.pop();
            _saxHandler.endPrefixMapping(prefix);
        }
    }




    /**
     * Utility method - pass a whole charactes as CDATA to SAX handler
     */
    private void startCDATA(char[] ch, int off, int len) throws SAXException {
        final int limit = off + len;
        int offset = off;

        // Output start bracket - "<![CDATA["
        _saxHandler.characters(BEGCDATA, 0, BEGCDATA.length);

        // Detect any occurence of "]]>" in the character array
        for (int i = offset; i < limit-2; i++) {
            if (ch[i] == ']' && ch[i+1] == ']' && ch[i+2] == '>') {
                _saxHandler.characters(ch, offset, i - offset);
                _saxHandler.characters(CNTCDATA, 0, CNTCDATA.length);
                offset = i+3;
                i=i+2; // Skip next chars ']' and '>'.
            }
        }

        // Output the remaining characters
        if (offset < limit) _saxHandler.characters(ch, offset, limit - offset);

        _cdataTagOpen = true;
    }


}

