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
 */

package org.apache.xalan.xsltc.runtime;

import java.util.Vector;
import java.util.Enumeration;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import org.apache.xalan.xsltc.*;

import org.apache.xalan.xsltc.util.IntegerArray;
import org.apache.xalan.xsltc.dom.DOMAdapter;
import org.apache.xalan.xsltc.dom.DOMImpl;
import org.apache.xalan.xsltc.dom.StripWhitespaceFilter;
import org.apache.xalan.xsltc.dom.KeyIndex;
import org.apache.xalan.xsltc.dom.DTDMonitor;

// GTM added all these
import org.apache.xalan.xsltc.runtime.DefaultSAXOutputHandler;
import javax.xml.transform.Transformer;	
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.OutputKeys;
import java.lang.IllegalArgumentException;
import java.util.Properties;
import java.lang.IllegalArgumentException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;
import java.io.File;
import java.io.Writer;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
// END.

public abstract class AbstractTranslet extends Transformer implements Translet {

    // DOM/translet handshaking - the arrays are set by the compiled translet
    protected String[] namesArray;
    protected String[] namespaceArray;

    // TODO - these should only be instanciated when needed
    protected StringValueHandler stringValueHandler = new StringValueHandler();

    // Use one empty string instead of constantly instanciating String("");
    private final static String EMPTYSTRING = "";

    /**
     * Wrap the initial input DOM in a dom adapter. This adapter is wrapped in
     * a DOM multiplexer if the document() function is used (handled by compiled
     * code in the translet - see compiler/Stylesheet.compileTransform()).
     */
    public final DOMAdapter makeDOMAdapter(DOM dom)
	throws TransletException {
	if (dom instanceof DOMImpl) {
	    return new DOMAdapter((DOMImpl)dom, namesArray, namespaceArray);
	}
	else {
	    throw new TransletException("wrong type of source DOM");
	}
    }

    /************************************************************************
     * Variable and parameter handling
     ************************************************************************/

    // Variable's stack: <tt>vbase</tt> and <tt>vframe</tt> are used 
    // to denote the current variable frame.
    protected int vbase = 0, vframe = 0;
    protected Vector varsStack = new Vector();

    // Parameter's stack: <tt>pbase</tt> and <tt>pframe</tt> are used 
    // to denote the current parameter frame.
    protected int pbase = 0, pframe = 0;
    protected Vector paramsStack = new Vector();

    /**
     * Push a new parameter frame.
     */
    public final void pushParamFrame() {
	paramsStack.insertElementAt(new Integer(pbase), pframe);
	pbase = ++pframe;
    }

    /**
     * Pop the topmost parameter frame.
     */
    public final void popParamFrame() {
	if (pbase > 0) {
	    pframe = pbase - 1;
	    pbase = ((Integer) paramsStack.elementAt(pframe)).intValue();
	}
    }

    /**
     * Add a new global parameter if not already in the current frame.
     */
    public final Object addParameter(String name, Object value) {
	return addParameter(name, value, false);
    }

    /**
     * Add a new global or local parameter if not already in the current frame.
     * The 'isDefault' parameter is set to true if the value passed is the
     * default value from the <xsl:parameter> element's select attribute or
     * element body.
     */
    public final Object addParameter(String name, Object value,
				     boolean isDefault) {
	// Local parameters need to be re-evaluated for each iteration
	for (int i = pframe - 1; i >= pbase; i--) {
	    final Parameter param = (Parameter) paramsStack.elementAt(i);
	    if (param._name.equals(name)) {
		// Only overwrite if current value is the default value and
		// the new value is _NOT_ the default value.
		if ((param._isDefault == true) || (!isDefault)) {
		    param._value = value;
		    param._isDefault = isDefault;
		    return value;
		}
		return param._value;
	    }
	}

	// Add new parameter to parameter stack
	final Parameter param = new Parameter(name, value, isDefault);
	paramsStack.insertElementAt(param, pframe++);
	return value;
    }

    /**
     * Get the value of a parameter from the current frame or
     * <tt>null</tt> if undefined.
     */
    public final Object getParameter(String name) {
	for (int i = pframe - 1; i >= pbase; i--) {
	    final Parameter param = (Parameter)paramsStack.elementAt(i);
	    if (param._name.equals(name)) {
		return param._value;
	    }
	}
	return null;
    }

    /**
     * Push a new variable frame.
     */
    public final void pushVarFrame(int frameSize) {
	varsStack.insertElementAt(new Integer(vbase), vframe);
	vbase = ++vframe;
	vframe += frameSize;
    }

    /**
     * Pop the topmost variable frame.
     */
    public final void popVarFrame() {
	if (vbase > 0) {
	    vframe = vbase - 1;
	    vbase = ((Integer)varsStack.elementAt(vframe)).intValue();
	}
    }

    /**
     * Get the value of a variable given its index.
     */
    public final Object getVariable(int vindex) {
	return varsStack.elementAt(vbase + vindex);
    }

    /**
     * Set the value of a variable in the current frame.
     */
    public final void addVariable(int vindex, Object value) {
	varsStack.insertElementAt(value, vbase + vindex);
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

	    final DecimalFormat df = (DecimalFormat)_formatSymbols.get(name);
	    if (df != null)
		return df;
	    else
		return((DecimalFormat)_formatSymbols.get(EMPTYSTRING));
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
    public void buildKeyIndex(String name, int node, String value) {
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
     * Start of an implementation of a multiple output extension.
     * See compiler/TransletOutput for actual implementation.
     ************************************************************************/

    protected TransletOutputHandler[] _handlers;

    protected final TransletOutputHandler getOutputHandler(double port) {
	try {
	    return _handlers[(int) port];
	}
	catch (IndexOutOfBoundsException e) {
	    BasisLibrary.runTimeError("Output port " + (int)port 
				      + " out of range.");     
	    return null;
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
	_handlers = new TransletOutputHandler[] { handler };
	transform(document, document.getIterator(), handler);
    }
	
    /**
     * Calls transform() with a set of given output handlers
     */
    public final void transform(DOM document,
				TransletOutputHandler[] handlers) 
	throws TransletException {
	_handlers = handlers;
	transform(document, document.getIterator(), handlers[0]);
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

    // Holds output encoding - set by code compiled by compiler/Output
    protected String _encoding = "utf-8";

    /**
     * Pass the output encoding setting to the output handler.
     */
    public String getOutputEncoding() {
      	return _encoding; 
    } 

    // Holds the translet's name - God only knows what it is used for
    private String _transletName;

    /**
     * Set the translet's name (default is class name)
     */
    public void setTransletName(String name) {
	_transletName = name;
    }

    /**
     * Get the translet's name (default is class name)
     */
    public String getTransletName() {
	return _transletName;
    }

    /************************************************************************
     * JAXP/TrAX Transformer interface implementation + etc. TrAX/JAXP stuff
     * This will be moved to org/apache/xalan/xsltc/trax/Transformer
     ************************************************************************/

    public void transform(Source xmlsrc, Result outputTarget)
	throws TransformerException {

	// try to get the encoding from Translet
	final Translet translet = (Translet)this;
	String encoding = translet.getOutputEncoding();
	if (encoding == null) encoding = "UTF-8";

	// create a DefaultSAXOutputHandler
	DefaultSAXOutputHandler saxHandler = null;
	StreamResult target = (StreamResult)outputTarget;
	java.io.Writer writer = target.getWriter();
	java.io.OutputStream os = target.getOutputStream();
	String systemid = target.getSystemId();
	if (writer != null) {
	    // no constructor that takes encoding yet...
	    try {
		saxHandler = new DefaultSAXOutputHandler(writer); 
	    } catch (java.io.IOException e) {
		throw new TransformerException(
		"IOException creating DefaultSAXOutputHandler");
	    }
	} else if (os != null) {
	    try {
		saxHandler = new DefaultSAXOutputHandler(os, encoding); 
	    } catch (java.io.IOException e) {
		throw new TransformerException(
                     "IOException creating DefaultSAXOutputHandler");
	    }
	} else if (systemid != null) {
	    String filePrefix = new String("file:///");
	    if (systemid.startsWith(filePrefix)) {
		systemid = systemid.substring(filePrefix.length());
	    }
	    try {
		saxHandler = new DefaultSAXOutputHandler(
			((OutputStream)new FileOutputStream(systemid)), 
			encoding);
	    } catch (java.io.FileNotFoundException e) {
		throw new TransformerException(
			"Transform output target could not be opened.");
	    } catch (java.io.IOException e) {
		throw new TransformerException(
                   "Transform output target could not be opened.");
	    }
	}
 
	// finally do the transformation...
	doTransform(xmlsrc.getSystemId(), saxHandler, encoding);
    }
 
    private void doTransform(String xmlDocName, 
			     ContentHandler saxHandler,
			     String encoding) {
	try {
	    // Create a SAX parser and get the XMLReader object it uses
	    final SAXParserFactory factory = SAXParserFactory.newInstance();
	    final SAXParser parser = factory.newSAXParser();
	    final XMLReader reader = parser.getXMLReader();
 
	    // Set the DOM's DOM builder as the XMLReader's SAX2 content handler
	    final DOMImpl dom = new DOMImpl();
	    reader.setContentHandler(dom.getBuilder());
	    // Create a DTD monitor and pass it to the XMLReader object
	    final DTDMonitor dtdMonitor = new DTDMonitor();
	    dtdMonitor.handleDTD(reader);
 
	    dom.setDocumentURI(xmlDocName);
	    if (xmlDocName.startsWith("file:/")) {   
		reader.parse(xmlDocName);            
	    } else {                                
	        reader.parse("file:"+(new File(xmlDocName).getAbsolutePath()));
	    }

	    // Set size of key/id indices
	    setIndexSize(dom.getSize());
	    // If there are any elements with ID attributes, build an index
	    dtdMonitor.buildIdIndex(dom, 0, this);
	    // Pass unparsed entity URIs to the translet (this)
	    setDTDMonitor(dtdMonitor);
 
	    // Transform the document
	    TextOutput textOutput = new TextOutput(saxHandler, encoding);
	    transform(dom, textOutput);
	}
	catch (TransletException e) {
	    if (_errorListener != null) {
		postErrorToListener(e.getMessage());
	    } else {
	        System.err.println("\nTranslet Error: " + e.getMessage());
	    }
	    System.exit(1);
	}
	catch (RuntimeException e) {
	    if (_errorListener != null) {
		postErrorToListener("Runtime Error: " + e.getMessage());
	    } else {
	        System.err.println("\nRuntime Error: " + e.getMessage());
	    }
	    System.exit(1);
	}
	catch (FileNotFoundException e) {
	    if (_errorListener != null) {
		postErrorToListener("File not found: " + e.getMessage());
	    } else {
		System.err.println("Error: File not found:"+e.getMessage());
	    }
	    System.exit(1);
	}
	catch (MalformedURLException e) {
	    if (_errorListener != null) {
		postErrorToListener("Malformed URL: " + e.getMessage());
	    } else {
	        System.err.println("Error: Malformed URL: "+e.getMessage());
	    }
	    System.exit(1);
	}
	catch (UnknownHostException e) {
	    if (_errorListener != null) {
		postErrorToListener("Cannot resolve URI: " + e.getMessage());
	    } else {
	        System.err.println("Error: Cannot resolve URI: "+
				   e.getMessage());
	    }
	    System.exit(1);
	}
	catch (Exception e) {
	    if (_errorListener != null) {
		postErrorToListener("Internal error: " + e.getMessage()); 
	    } else {
	        System.err.println("Internal error: "+e.getMessage());
	        e.printStackTrace();
	    }
	    System.exit(1);
	}
    }

    // TrAX support methods, get/setErrorListener
    private ErrorListener _errorListener = null;

    /**
     * Get the TrAX error listener
     */
    public ErrorListener getErrorListener() {  
	return _errorListener; 
    }

    /**
     * Set the TrAX error listener
     */
    public void setErrorListener(ErrorListener listener)
	throws IllegalArgumentException {
        if (listener == null) {
            throw new IllegalArgumentException(
               "Error: setErrorListener() call where ErrorListener is null");
        }
        _errorListener = listener;
    }

    /**
     * Inform TrAX error listener of an error
     */
    private void postErrorToListener(String msg) {
        try {
            _errorListener.error(new TransformerException(
                "Translet Error: " + msg));
        } catch (TransformerException e) {
            // TBD
        }
    }

    /**
     * Inform TrAX error listener of a warning
     */
    private void postWarningToListener(String msg) {
        try {
            _errorListener.warning(new TransformerException(
                "Translet Warning: " + msg));
        } catch (TransformerException e) {
            // TBD
        }
    }

    /**
     * Implements JAXP's Transformer.getOutputProperties().
     * Returns a copy of the output properties for the transformation.
     */
    public Properties getOutputProperties() throws IllegalArgumentException { 
	// TODO
	return(null);
    }

    /**
     * Implements JAXP's Transformer.getOutputProperty().
     * Set an output property that will be in effect for the transformation.
     */
    public String getOutputProperty(String name)
	throws IllegalArgumentException { 
	// TODO
	return(null);
    }

    /**
     * Implements JAXP's Transformer.setOutputProperties().
     * Set the output properties for the transformation. These properties
     * will override properties set in the Templates with xsl:output.
     */
    public void setOutputProperties(Properties props)
	throws IllegalArgumentException {
	// TODO
    }

    /**
     * Implements JAXP's Transformer.setOutputProperty().
     * Get an output property that is in effect for the transformation. The
     * property specified may be a property that was set with
     * setOutputProperty(), or it may be a property specified in the stylesheet.
     */
    public void setOutputProperty(String name, String value)
	throws 	IllegalArgumentException  {
	// TODO
    }

    /**
     * Implements JAXP's Transformer.setParameter()
     * Add a parameter for the transformation.
     */
    public void setParameter(String name, Object value) { 
	addParameter(name, value, false);
    }

    /**
     * Implements JAXP's Transformer.clearParameters()
     * Clears the parameter stack.
     */
    public void clearParameters() {  
	paramsStack.clear();
    }

    /**
     * These two methods need to pass the URI resolver to the dom/LoadDocument
     * class, which again must use the URI resolver if present.
     */
    public URIResolver getURIResolver() {
	// TBD
	return null;
    }

    public void setURIResolver(URIResolver resolver) { 
	// TBD
    }

}
