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
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 * @author Morten Jorgensen
 * @author G. Todd Miller
 * @author John Howard, JohnH@schemasoft.com 
 */

package org.apache.xalan.xsltc.runtime;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.apache.xalan.xsltc.*;
import org.apache.xalan.xsltc.dom.DOMAdapter;
import org.apache.xalan.xsltc.dom.DOMImpl;
import org.apache.xalan.xsltc.dom.KeyIndex;
import org.apache.xalan.xsltc.dom.DTDMonitor;
import org.apache.xalan.xsltc.util.IntegerArray;
import org.apache.xalan.xsltc.runtime.output.*;

public abstract class AbstractTranslet implements Translet {

    // These attributes are extracted from the xsl:output element. They also
    // appear as fields (with the same type, only public) in Output.java
    public String  _version = "1.0";
    public String  _method = null;
    public String  _encoding = "UTF-8";
    public boolean _omitHeader = false;
    public String  _standalone = null;
    public String  _doctypePublic = null;
    public String  _doctypeSystem = null;
    public boolean _indent = false;
    public String  _mediaType = null;
    public Hashtable _cdata = null;

    // DOM/translet handshaking - the arrays are set by the compiled translet
    protected String[] namesArray;
    protected String[] namespaceArray;

    // TODO - these should only be instanciated when needed
    protected StringValueHandler stringValueHandler = new StringValueHandler();

    // Use one empty string instead of constantly instanciating String("");
    private final static String EMPTYSTRING = "";

    
    /************************************************************************
     * Debugging
     ************************************************************************/
    public void printInternalState() {
	System.out.println("-------------------------------------");
	System.out.println("AbstractTranslet this = " + this);
	System.out.println("pbase = " + pbase);
	System.out.println("vframe = " + pframe);
	System.out.println("paramsStack.size() = " + paramsStack.size());
	System.out.println("namesArray.size = " + namesArray.length);
	System.out.println("namespaceArray.size = " + namespaceArray.length);
	System.out.println("");
	System.out.println("Total memory = " + Runtime.getRuntime().totalMemory());
    }

    /**
     * Wrap the initial input DOM in a dom adapter. This adapter is wrapped in
     * a DOM multiplexer if the document() function is used (handled by compiled
     * code in the translet - see compiler/Stylesheet.compileTransform()).
     */
    public final DOMAdapter makeDOMAdapter(DOM dom)
	throws TransletException {
	if (dom instanceof DOMImpl)
	    return new DOMAdapter((DOMImpl)dom, namesArray, namespaceArray);
	BasisLibrary.runTimeError(BasisLibrary.DOM_ADAPTER_INIT_ERR);
	return null;
    }

    /************************************************************************
     * Parameter handling
     ************************************************************************/

    // Parameter's stack: <tt>pbase</tt> and <tt>pframe</tt> are used 
    // to denote the current parameter frame.
    protected int pbase = 0, pframe = 0;
    protected ArrayList paramsStack = new ArrayList();

    /**
     * Push a new parameter frame.
     */
    public final void pushParamFrame() {
	paramsStack.add(pframe, new Integer(pbase));
	pbase = ++pframe;
    }

    /**
     * Pop the topmost parameter frame.
     */
    public final void popParamFrame() {
	if (pbase > 0) {
	    final int oldpbase = ((Integer)paramsStack.get(--pbase)).intValue();
	    for (int i = pframe - 1; i >= pbase; i--) {
		paramsStack.remove(i);
	    }
	    pframe = pbase; pbase = oldpbase;
	}
    }

    /**
     * Add a new global parameter if not already in the current frame.
     */
    public final Object addParameter(String name, Object value) {
	name = BasisLibrary.replace(name, ".-", 
				    new String[] { "$dot$", "$dash$" });
	return addParameter(name, value, false);
    }

    /**
     * Add a new global or local parameter if not already in the current frame.
     * The 'isDefault' parameter is set to true if the value passed is the
     * default value from the <xsl:parameter> element's select attribute or
     * element body.
     */
    public final Object addParameter(String name, Object value, 
	boolean isDefault) 
    {
	// Local parameters need to be re-evaluated for each iteration
	for (int i = pframe - 1; i >= pbase; i--) {
	    final Parameter param = (Parameter) paramsStack.get(i);

	    if (param._name.equals(name)) {
		// Only overwrite if current value is the default value and
		// the new value is _NOT_ the default value.
		if (param._isDefault || !isDefault) {
		    param._value = value;
		    param._isDefault = isDefault;
		    return value;
		}
		return param._value;
	    }
	}

	// Add new parameter to parameter stack
	paramsStack.add(pframe++, new Parameter(name, value, isDefault));
	return value;
    }

    /**
     * Clears the parameter stack.
     */
    public void clearParameters() {  
	pbase = pframe = 0;
	paramsStack.clear();
    }

    /**
     * Get the value of a parameter from the current frame or
     * <tt>null</tt> if undefined.
     */
    public final Object getParameter(String name) {
	for (int i = pframe - 1; i >= pbase; i--) {
	    final Parameter param = (Parameter)paramsStack.get(i);
	    if (param._name.equals(name)) return param._value;
	}
	return null;
    }

    /************************************************************************
     * Message handling - implementation of <xsl:message>
     ************************************************************************/

    // Holds the translet's message handler - used for <xsl:message>.
    // The deault message handler dumps a string stdout, but anything can be
    // used, such as a dialog box for applets, etc.
    private MessageHandler _msgHandler = null;

    /**
     * Set the translet's message handler - must implement MessageHandler
     */
    public final void setMessageHandler(MessageHandler handler) {
	_msgHandler = handler;
    }

    /**
     * Pass a message to the message handler - used by Message class.
     */
    public final void displayMessage(String msg) {
	if (_msgHandler == null) {
            System.err.println(msg);
	}
	else {
	    _msgHandler.displayMessage(msg);
	}
    }

    /************************************************************************
     * Decimal number format symbol handling
     ************************************************************************/

    // Contains decimal number formatting symbols used by FormatNumberCall
    public Hashtable _formatSymbols = null;

    /**
     * Adds a DecimalFormat object to the _formatSymbols hashtable.
     * The entry is created with the input DecimalFormatSymbols.
     */
    public void addDecimalFormat(String name, DecimalFormatSymbols symbols) {
	// Instanciate hashtable for formatting symbols if needed
	if (_formatSymbols == null) _formatSymbols = new Hashtable();

	// The name cannot be null - use empty string instead
	if (name == null) name = EMPTYSTRING;

	// Construct a DecimalFormat object containing the symbols we got
	final DecimalFormat df = new DecimalFormat();
	if (symbols != null) {
	    df.setDecimalFormatSymbols(symbols);
	}
	_formatSymbols.put(name, df);
    }

    /**
     * Retrieves a named DecimalFormat object from _formatSymbols hashtable.
     */
    public final DecimalFormat getDecimalFormat(String name) {

	if (_formatSymbols != null) {
	    // The name cannot be null - use empty string instead
	    if (name == null) name = EMPTYSTRING;

	    DecimalFormat df = (DecimalFormat)_formatSymbols.get(name);
	    if (df == null) df = (DecimalFormat)_formatSymbols.get(EMPTYSTRING);
	    return df;
	}
	return(null);
    }

    /************************************************************************
     * Unparsed entity URI handling - implements unparsed-entity-uri()
     ************************************************************************/

    // Keeps all unparsed entity URIs specified in the XML input
    public Hashtable _unparsedEntities = null;

    /**
     * Get the value of an unparsed entity URI.
     * This method is used by the compiler/UnparsedEntityUriCall class.
     */
    public final String getUnparsedEntity(String name) {
	final String uri = (String)_unparsedEntities.get(name);
	return uri == null ? EMPTYSTRING : uri;
    }

    /**
     * Add an unparsed entity URI. The URI/value pairs are passed from the
     * DOM builder to the translet.
     */
    public final void addUnparsedEntity(String name, String uri) {
	if (_unparsedEntities == null)
	    _unparsedEntities = new Hashtable();
	if (_unparsedEntities.containsKey(name) == false)
	    _unparsedEntities.put(name, uri);
    }
    
    /**
     * Add an unparsed entity URI. The URI/value pairs are passed from the
     * DOM builder to the translet.
     */
    public final void setUnparsedEntityURIs(Hashtable table) {
	if (_unparsedEntities == null)
	    _unparsedEntities = table;
	else {
	    Enumeration keys = table.keys();
	    while (keys.hasMoreElements()) {
		String name = (String)keys.nextElement();
		_unparsedEntities.put(name,table.get(name));
	    }
	}
    }

    /**
     * The DTD monitor used by the DOM builder scans the input document DTD
     * for unparsed entity URIs. These are passed to the translet using
     * this method.
     */
    public final void setDTDMonitor(DTDMonitor monitor) {
	setUnparsedEntityURIs(monitor.getUnparsedEntityURIs());
    }

    /************************************************************************
     * Index(es) for <xsl:key> / key() / id()
     ************************************************************************/

    // Container for all indexes for xsl:key elements
    private Hashtable _keyIndexes = null;
    private KeyIndex  _emptyKeyIndex = new KeyIndex(1);
    private int       _indexSize = 0;

    /**
     * This method is used to pass the largest DOM size to the translet.
     * Needed to make sure that the translet can index the whole DOM.
     */
    public void setIndexSize(int size) {
	if (size > _indexSize) _indexSize = size;
    }

    /**
     * Creates a KeyIndex object of the desired size - don't want to resize!!!
     */
    public KeyIndex createKeyIndex() {
	return(new KeyIndex(_indexSize));
    }

    /**
     * Adds a value to a key/id index
     *   @name is the name of the index (the key or ##id)
     *   @node is the node id of the node to insert
     *   @value is the value that will look up the node in the given index
     */
    public void buildKeyIndex(String name, int node, Object value) {
	if (_keyIndexes == null) _keyIndexes = new Hashtable();
	
	KeyIndex index = (KeyIndex)_keyIndexes.get(name);
	if (index == null) {
	    _keyIndexes.put(name, index = new KeyIndex(_indexSize));
	}
	index.add(value, node);
    }

    /**
     * Returns the index for a given key (or id).
     * The index implements our internal iterator interface
     */
    public KeyIndex getKeyIndex(String name) {
	// Return an empty key index iterator if none are defined
	if (_keyIndexes == null) return(_emptyKeyIndex);

	// Look up the requested key index
	final KeyIndex index = (KeyIndex)_keyIndexes.get(name);

	// Return an empty key index iterator if the requested index not found
	if (index == null) return(_emptyKeyIndex);

	return(index);
    }

    /**
     * This method builds key indexes - it is overridden in the compiled
     * translet in cases where the <xsl:key> element is used
     */
    public void buildKeys(DOM document, NodeIterator iterator,
			  TransletOutputHandler handler,
			  int root) throws TransletException {
    }

    /************************************************************************
     * DOM cache handling
     ************************************************************************/

    // Hold the DOM cache (if any) used with this translet
    private DOMCache _domCache = null;

    /**
     * Sets the DOM cache used for additional documents loaded using the
     * document() function.
     */
    public void setDOMCache(DOMCache cache) {
	_domCache = cache;
    }

    /**
     * Returns the DOM cache used for this translet. Used by the LoadDocument
     * class (if present) when the document() function is used.
     */
    public DOMCache getDOMCache() {
	return(_domCache);
    }

    /************************************************************************
     * Multiple output document extension.
     * See compiler/TransletOutput for actual implementation.
     ************************************************************************/

    public TransletOutputHandler openOutputHandler(String filename) 
	throws TransletException 
    {
	try {
	    final TransletOutputHandlerFactory factory 
		= TransletOutputHandlerFactory.newInstance();

	    factory.setEncoding(_encoding);
	    factory.setOutputMethod(_method);
	    factory.setWriter(new FileWriter(filename));
	    factory.setOutputType(TransletOutputHandlerFactory.STREAM);

	    final TransletOutputHandler handler 
		= factory.getTransletOutputHandler();

	    transferOutputSettings(handler);
	    handler.startDocument();
	    return handler;
	}
	catch (Exception e) {
	    throw new TransletException(e);
	}
    }

    public void closeOutputHandler(TransletOutputHandler handler) {
	try {
	    handler.endDocument();
	    handler.close();
	}
	catch (Exception e) {
	    // what can you do?
	}
    }

    /************************************************************************
     * Native API transformation methods - _NOT_ JAXP/TrAX
     ************************************************************************/

    /**
     * Main transform() method - this is overridden by the compiled translet
     */
    public abstract void transform(DOM document, NodeIterator iterator,
				   TransletOutputHandler handler)
	throws TransletException;

    /**
     * Calls transform() with a given output handler
     */
    public final void transform(DOM document, TransletOutputHandler handler) 
	throws TransletException {
	transform(document, document.getIterator(), handler);
    }
	
    /**
     * Used by some compiled code as a shortcut for passing strings to the
     * output handler
     */
    public final void characters(final String string,
				 TransletOutputHandler handler) 
	throws TransletException {
	final int length = string.length();
	handler.characters(string.toCharArray(), 0, length);
    }

    /**
     * Add's a name of an element whose text contents should be output as CDATA
     */
    public void addCdataElement(String name) {
	if (_cdata == null) _cdata = new Hashtable();
	_cdata.put(name, name);
    }

    /**
     * Transfer the output settings to the output post-processor
     */
    protected void transferOutputSettings(TransletOutputHandler handler) {
	if (_method != null) {
	    if (_method.equals("xml")) {
	        if (_standalone != null) {
		    handler.setStandalone(_standalone);
		}
		if (_omitHeader) {
		    handler.omitHeader(true);
		}
		handler.setCdataElements(_cdata);
		if (_version != null) {
		    handler.setVersion(_version);
		}
		handler.setIndent(_indent);
		if (_doctypeSystem != null) {
		    handler.setDoctype(_doctypeSystem, _doctypePublic);
		}
	    }
	    else if (_method.equals("html")) {
		handler.setIndent(_indent);
		handler.setDoctype(_doctypeSystem, _doctypePublic);
		if (_mediaType != null) {
		    handler.setMediaType(_mediaType);
		}
	    }
	}
	else {
	    handler.setCdataElements(_cdata);
	    if (_version != null) {
		handler.setVersion(_version);
	    }
	    if (_standalone != null) {
		handler.setStandalone(_standalone);
	    }
	    if (_omitHeader) {
		handler.omitHeader(true);
	    }
	    handler.setIndent(_indent);
	    handler.setDoctype(_doctypeSystem, _doctypePublic);
	}
    }

    private Hashtable _auxClasses = null;

    public void addAuxiliaryClass(Class auxClass) {
	if (_auxClasses == null) _auxClasses = new Hashtable();
	_auxClasses.put(auxClass.getName(), auxClass);
    }

    public Class getAuxiliaryClass(String className) {
	if (_auxClasses == null) return null;
	return((Class)_auxClasses.get(className));
    }

    // GTM added (see pg 110)
    public String[] getNamesArray() {
	return namesArray;
    }
    public String[] getNamespaceArray() {
	return namespaceArray;
    }
}
