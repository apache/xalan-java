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
import java.util.Hashtable;
import java.util.Enumeration;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import org.apache.xalan.xsltc.*;

import org.apache.xalan.xsltc.util.IntegerArray;
import org.apache.xalan.xsltc.dom.DOMAdapter;
import org.apache.xalan.xsltc.dom.DOMImpl;
import org.apache.xalan.xsltc.dom.StripWhitespaceFilter;
import org.apache.xalan.xsltc.dom.KeyIndex;

// GTM added all these
import javax.xml.transform.Transformer;	
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.ErrorListener;
import java.lang.IllegalArgumentException;
import java.util.Properties;
import java.lang.IllegalArgumentException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.xml.sax.XMLReader;
import org.apache.xalan.xsltc.dom.DTDMonitor;
import java.io.File;
import java.io.OutputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
// END.

public abstract class AbstractTranslet extends Transformer implements Translet {

    protected String _encoding = "utf-8";

    //!!! to be added only as needed
    protected StringValueHandler stringValueHandler = new StringValueHandler();

    //!! different handshake needed
    protected String[] namesArray;
    protected String[] namespaceArray;

    //!!! Temporary approach. To be added to translet only when needed. !!!
    public Hashtable _formatSymbols = new Hashtable();
    public Hashtable _unparsedEntities = null;

    // Container for all indexes for xsl:key elements
    private Hashtable _keyIndexes = new Hashtable();
    private KeyIndex  _emptyKeyIndex = new KeyIndex(1);
    private int _indexSize = 0;

    private MessageHandler _msgHandler = null;

    private DOMCache _domCache = null;

    private final static String EMPTYSTRING = "";


    public final DOMAdapter makeDOMAdapter(DOM dom)
	throws TransletException {
	if (dom instanceof DOMImpl) {
	    return new DOMAdapter((DOMImpl)dom, namesArray, namespaceArray);
	}
	else {
	    throw new TransletException("wrong type of source DOM");
	}
    }

    public final void setMessageHandler(MessageHandler handler) {
	_msgHandler = handler;
    }

    public final void displayMessage(String msg) {
	if (_msgHandler == null) {
            System.err.println(msg);
	}
	else {
	    _msgHandler.displayMessage(msg);
	}
    }

    /**
     * Variable's stack: <tt>vbase</tt> and <tt>vframe</tt> are used 
     * to denote the current variable frame.
     */
    protected int vbase = 0, vframe = 0;
    protected Vector varsStack = new Vector();

    /**
     * Parameter's stack: <tt>pbase</tt> and <tt>pframe</tt> are used 
     * to denote the current parameter frame.
     */
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
     * Add a new parameter if not already in the current frame.
     * Returns the old value if already defined and the new value
     * if added.
     */
    public final Object addParameter(String name, Object value) {
	for (int i = pframe - 1; i >= pbase; i--) {
	    final Parameter param = (Parameter) paramsStack.elementAt(i);
	    if (param.name.equals(name)) {
		return param.value;
	    }
	}
	paramsStack.insertElementAt(new Parameter(name, value), pframe++);
	return value;
    }

    /**
     * Get the value of a parameter from the current frame or
     * <tt>null</tt> if undefined.
     */
    public final Object getParameter(String name) {
	for (int i = pframe - 1; i >= pbase; i--) {
	    final Parameter param = (Parameter)paramsStack.elementAt(i);
	    if (param.name.equals(name)) {
		return param.value;
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

    /**
     * Adds a DecimalFormat object to the _formatSymbols hashtable.
     * The entry is created with the input DecimalFormatSymbols.
     */
    public void addDecimalFormat(String name, DecimalFormatSymbols symbols) {
	// Remove any existing entries with the same name.
	if (name == null) name = EMPTYSTRING;
	_formatSymbols.remove(name);
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
	if (name == null) name = EMPTYSTRING;
	final DecimalFormat df = (DecimalFormat)_formatSymbols.get(name);
	if (df == null) {
	    // This should really result in an error
	    return((DecimalFormat)_formatSymbols.get(EMPTYSTRING));
	}
	else {
	    return df;
	}
    }

    public final void addUnparsedEntity(String name, String uri) {
	if (_unparsedEntities == null)
	    _unparsedEntities = new Hashtable();
	if (_unparsedEntities.containsKey(name) == false)
	    _unparsedEntities.put(name, uri);
    }
    
    public final String getUnparsedEntity(String name) {
	final String uri = (String)_unparsedEntities.get(name);
	return uri == null ? EMPTYSTRING : uri;
    }

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

    public abstract void transform(DOM document, NodeIterator iterator,
				   TransletOutputHandler handler)
	throws TransletException;
	
    protected TransletOutputHandler[] handlers;

    protected final TransletOutputHandler getOutputHandler(double port) {
	try {
	    return handlers[(int) port];
	}
	catch (IndexOutOfBoundsException e) {
	    BasisLibrary.runTimeError("Output port " + (int)port 
				      + " out of range.");     
	    return null;
	}
    }

    public final void transform(DOM document, TransletOutputHandler handler) 
	throws TransletException {
	handlers = new TransletOutputHandler[] { handler };
	transform(document, document.getIterator(), handler);
    }
	
    public final void transform(DOM document,
				TransletOutputHandler[] handlers) 
	throws TransletException {
	this.handlers = handlers;
	transform(document, document.getIterator(), handlers[0]);
    }
    
    private char[] _characterArray = new char[32];

    public final void characters(final String string,
				 TransletOutputHandler handler) 
	throws TransletException {
	if (string == null) return;
	final int length = string.length();
	if (length > _characterArray.length) {
	    _characterArray = new char[length];
	}
	string.getChars(0, length, _characterArray, 0);
	handler.characters(_characterArray, 0, length);
    }

    /**
     * Pass the output encoding setting to the output handler.
     */
    public String getOutputEncoding() {
      	return _encoding; 
    } 

    public void setIndexSize(int size) {
	if (size > _indexSize)
	    _indexSize = size;
    }

    public void buildKeyIndex(String name, int node, String value) {
	KeyIndex index = (KeyIndex)_keyIndexes.get(name);
	if (index == null) {
	    _keyIndexes.put(name, index = new KeyIndex(_indexSize));
	}
	index.add(value, node);
    }

    public KeyIndex createKeyIndex() {
	return(new KeyIndex(_indexSize));
    }

    public KeyIndex getKeyIndex(String name) {
	final KeyIndex index = (KeyIndex)_keyIndexes.get(name);
	return index != null ? index : _emptyKeyIndex;
    }

    public void buildKeys(DOM document, NodeIterator iterator,
			  TransletOutputHandler handler,
			  int root) throws TransletException {
    }

    public void setDOMCache(DOMCache cache) {
	_domCache = cache;
    }

    public DOMCache getDOMCache() {
	return(_domCache);
    }

    private String _transletName;

    public void setTransletName(String name) {
	_transletName = name;
    }

    public String getTransletName() {
	return _transletName;
    }

    /*********************************************************
     *  Transformer methods
     *********************************************************/

    public void clearParameters() {  
	paramsStack.clear();
    }

    public ErrorListener getErrorListener() {  
	/* TBD */
	return null; 
    }

    public void setErrorListener(ErrorListener listener) throws 
        IllegalArgumentException {  /* TBD */ }

    public Properties getOutputProperties() throws IllegalArgumentException { 
        /*TBD*/ 
    	return null; 
    }

    public String getOutputProperty(String name) throws 
	IllegalArgumentException
    { 
        /*TBD*/ 
	return ""; 
    }

    public void setOutputProperties(Properties props) throws 
	IllegalArgumentException 
    { 
	/*TBD */ 
    }

    public void setOutputProperty(String name, String value) throws 
	IllegalArgumentException 
    {
	/*TBD*/ 
    }

    public URIResolver getURIResolver() { /*TBD*/ return null; }

    public void setParameter(String name, Object value) { 
	addParameter(name, value);
    }
    public void setURIResolver(URIResolver resolver) { /*TBD*/}

    public void transform(Source xmlsrc, Result outputTarget)
        throws TransformerException 
    {
	doTransform( xmlsrc.getSystemId(), 
		     ((StreamResult)outputTarget).getOutputStream() ); 
    }

    private void doTransform(String xmlDocName, OutputStream ostream) {
        try {
            final Translet translet = (Translet)this; // GTM added

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
        /****************
            if (_uri)
                reader.parse(xmlDocName);
            else
        *******************/
            reader.parse("file:"+(new File(xmlDocName).getAbsolutePath()));
           
            // Set size of key/id indices
            setIndexSize(dom.getSize());
            // If there are any elements with ID attributes, build an index
            dtdMonitor.buildIdIndex(dom, 0, this);

            setUnparsedEntityURIs(dtdMonitor.getUnparsedEntityURIs());

            // Transform the document
            String encoding = translet.getOutputEncoding();
            if (encoding == null) encoding = "UTF-8";

            //TextOutput textOutput = new TextOutput(System.out, encoding);
            DefaultSAXOutputHandler saxHandler = new
                DefaultSAXOutputHandler(ostream, encoding);
            TextOutput textOutput = new TextOutput(saxHandler, encoding);
            translet.transform(dom, textOutput);
            textOutput.flush();

        }
        catch (TransletException e) {
            System.err.println("\nTranslet Error: " + e.getMessage());
            System.exit(1);
        }
        catch (RuntimeException e) {
            System.err.println("\nRuntime Error: " + e.getMessage());
            System.exit(1);
        }
        catch (FileNotFoundException e) {
           //System.err.println("Error:File or URI '"+_fileName+"' not found.");
            System.exit(1);
        }
        catch (MalformedURLException e) {
            //System.err.println("Error: Invalid URI '"+_fileName+"'.");
            System.exit(1);
        }
        catch (UnknownHostException e) {
            //System.err.println("Error: Can't resolve URI specification '"+
                               //_fileName+"'.");
            System.exit(1);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error: internal error.");
            System.exit(1);
        }
    }
}
