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
import java.util.Hashtable;

import org.xml.sax.*;

import com.sun.xml.parser.Resolver;
import com.sun.xml.parser.DtdEventListener;
import com.sun.xml.parser.Parser;

import org.apache.xalan.xsltc.*;
import org.apache.xalan.xsltc.runtime.AbstractTranslet;


final public class DTDMonitor implements DtdEventListener {

    // This is the name of the index used for ID attributes
    private final static String ID_INDEX_NAME = "##id";
    // Stores the names of the ID attributes for the various element types
    private Hashtable _idAttributes = new Hashtable(); 
    // Stores names of all unparsed entities
    private Hashtable _unparsedEntities = new Hashtable();

    /**
     * Constructor
     */
    public DTDMonitor() { }

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
	// Do nothing if there were no ID declarations in the DTD
	if ((_idAttributes == null) || (_idAttributes.isEmpty())) return;
	
	Enumeration elementNames = _idAttributes.keys();
	while (elementNames.hasMoreElements()) {
	    String elementName = (String)elementNames.nextElement();
	    String attrName = getIdAttrName(elementName);
	    int nodeType = dom.getGeneralizedType(elementName);
	    NodeIterator niter = dom.getTypedDescendantIterator(nodeType);
	    
	    int attributeType = dom.getGeneralizedType(attrName);
	    int node;

	    while (( node = niter.next()) != NodeIterator.END) {
		// get id value for the node
		String idValue = dom.getAttributeValue(attributeType, node);
		// add entry into ##id index for KeyCall to handle
		translet.buildKeyIndex(ID_INDEX_NAME, mask|node, idValue);
	    }
	} 
    }

    /**
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

    public void notationDecl(String name, String publicId, String systemId)
	throws SAXException { }

    public void elementDecl(String elemtName, String contentModel) { }

    public void endDtd() { }

    public void externalDtdDecl(String publicId, String systemId) { }

    public void externalEntityDecl(String name, String publicId,
				   String systemId) { }
	
    public void internalDtdDecl(String internalSubset) { }

    public void internalEntityDecl(String name, String value) { }
    
    public void startDtd(String rootName) { }
}
