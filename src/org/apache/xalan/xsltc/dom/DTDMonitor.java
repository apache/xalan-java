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
 * @author Morten Jorgensen
 *
 */

package org.apache.xalan.xsltc.dom;

import java.util.Enumeration;

import org.xml.sax.XMLReader;
import org.xml.sax.DTDHandler;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import org.apache.xalan.xsltc.*;
import org.apache.xalan.xsltc.runtime.AbstractTranslet;
import org.apache.xalan.xsltc.runtime.Hashtable;

final public class DTDMonitor implements DTDHandler, DeclHandler {

    private final static String EMPTYSTRING = "";

    // This is the name of the index used for ID attributes
    private final static String ID_INDEX_NAME = "##id";
    // Stores the names of the ID attributes for the various element types
    private Hashtable _idAttributes = new Hashtable(); 
    // Stores names of all unparsed entities
    private Hashtable _unparsedEntities = new Hashtable();

    // Name of DTD declaration handler property of an XMLReader object
    private final static String DECL_HANDLER_PROP =
	"http://xml.org/sax/properties/declaration-handler";

    // Error message used when the SAX parser does not generate DTD events
    private final static String NO_DTD_SUPPORT_STR =
	"Your SAX parser does not handle DTD declarations";

    /**
     * Constructor - does nothing
     */
    public DTDMonitor() { }

    /**
     * Constructor
     */
    public DTDMonitor(XMLReader reader) throws RuntimeException {
	handleDTD(reader);
    }

    /**
     * Set an instance of this class as the DTD declaration handler for
     * an XMLReader object (using the setProperty() method).
     */
    public void handleDTD(XMLReader reader) throws RuntimeException {
	try {
	    reader.setProperty(DECL_HANDLER_PROP, this);
	    reader.setDTDHandler(this);
	}
	catch (SAXNotRecognizedException e) {
	    throw(new RuntimeException(NO_DTD_SUPPORT_STR));
	}
	catch (SAXNotSupportedException e) {
	    throw(new RuntimeException(NO_DTD_SUPPORT_STR));
	}
    }

    /**
     * SAX2: Receive notification of a notation declaration event.
     */
    public void notationDecl(String name, String publicId, String systemId)
	throws SAXException { }

    /**
     * SAX2: Receive notification of an unparsed entity declaration event.
     * The only method here that does not have to do with ID attributes.
     * Passes names of unparsed entities to the translet.
     */
    public void unparsedEntityDecl(String name, String publicId,
				   String systemId, String notation)
	throws SAXException {
	if (_unparsedEntities.containsKey(name) == false) {
	    _unparsedEntities.put(name, systemId);
	}
    }

    public Hashtable getUnparsedEntityURIs() {
	return(_unparsedEntities);
    }

    /**
     * Stores the association between the name of an ID attribute and the
     * name of element that may contain it  Such an association would be
     * represented in a DTD as in:
     *              <!ATTLIST Person SSN ID #REQUIRED>
     * where 'Person' would be elemtName and 'SSN' would be the ID attribute
     */
    public void attributeDecl(String element, String attribute, String type,
			      String[] options, String defaultValue,
			      boolean fixed, boolean required) {
	_idAttributes.put(element, "@"+attribute);
    }

    /**
     * SAX2 extension handler for DTD declaration events
     * Report an attribute type declaration
     */
    public void attributeDecl(String element, String attribute, 
			      String type, String defaultValue, String value) {
	// Stores the association between the name of an ID attribute and the
	// name of element that may contain it  Such an association would be
	// represented in a DTD as in:
	//           <!ATTLIST Person SSN ID #REQUIRED>
	// where 'Person' would be elemtName and 'SSN' would be the ID attribute
	if (type.equals("ID") || (type.equals("IDREF")))
	    _idAttributes.put(element, "@"+attribute);
    }
    
    /**
     * SAX2 extension handler for DTD declaration events
     * Report an element type declaration.
     */
    public void elementDecl(String element, String model) { }
                 
    /**
     * SAX2 extension handler for DTD declaration events
     * Report a parsed external entity declaration.
     */
    public void externalEntityDecl(String name, String pid, String sid) { }
                 

    /**
     * SAX2 extension handler for DTD declaration events
     * Report an internal entity declaration.
     */
    public void internalEntityDecl(String name, String value) { }

    /**
     * Retrieves the name of the ID attribute associated with an element type
     */
    private final String getIdAttrName(String elemtName) {
        final String idAttrName = (String)_idAttributes.get(elemtName);
        return ((idAttrName == null) ? "" : idAttrName);
    }

    /**
     * Leverages the Key Class to implement the XSLT id() function.
     * buildIdIndex creates the index (##id) that Key Class uses. 
     * The index contains the node index (int) and the id value (String).
     */
    public final void buildIdIndex(DOMImpl dom, int mask,
				   AbstractTranslet translet) {

	// These variables are put up here for speed
	int node, attr, type, typeCache;

	// Set size of key/id indices
	translet.setIndexSize(dom.getSize());

	// Do nothing if there were no ID declarations in the DTD
	if ((_idAttributes == null) || (_idAttributes.isEmpty())) return;

	// Convert the _idAttribute Hashtable so that instead of containing
	// an element name (String) mapping to an ID attribute name (String),
	// it contains pairs of element types (Integer) mapping to ID attribute
	// types (Integer). This eliminates string comparisons, and makes it
	// possible for us to traverse the input DOM just once.
	Enumeration elements = _idAttributes.keys();
	if (elements.nextElement() instanceof String) {
	    Hashtable newAttributes = new Hashtable();
	    elements = _idAttributes.keys();
	    while (elements.hasMoreElements()) {
		String element = (String)elements.nextElement();
		String attribute = (String)_idAttributes.get(element);
		int elemType = dom.getGeneralizedType(element);
		int attrType = dom.getGeneralizedType(attribute);
		newAttributes.put(new Integer(elemType), new Integer(attrType));
	    }
	    _idAttributes = newAttributes;
	}

	// Get all nodes in the DOM
	final NodeIterator iter = dom.getAxisIterator(Axis.DESCENDANT);
	iter.setStartNode(DOM.ROOTNODE);

	Integer E = new Integer(typeCache = 0);
	Integer A = null;

	while ((node = iter.next()) != NodeIterator.END) {
	    // Get the node type of this node
	    type = dom.getType(node);
	    if (type != typeCache) {
		E = new Integer(typeCache = type);
		A = (Integer)_idAttributes.get(E);
	    }

	    // See if it has a defined ID attribute type
	    if (A != null) {
		// Only store the attribute value if the element has this attr.
		if ((attr = dom.getAttributeNode(A.intValue(), node)) != 0) {
		    final String value = dom.getNodeValue(attr);
		    translet.buildKeyIndex(ID_INDEX_NAME, mask|node, value);
		}
	    }
	}
    }

}

