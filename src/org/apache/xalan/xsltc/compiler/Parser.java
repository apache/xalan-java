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
 * @author G. Todd Miller
 * @author Morten Jorgensen
 *
 */

package org.apache.xalan.xsltc.compiler;

import java.io.*;
import java.net.URL;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.net.MalformedURLException;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import java_cup.runtime.Symbol;

import org.apache.xalan.xsltc.compiler.util.*;

public final class Parser implements Constants {

    private static final String XSL = "xsl";            // standard prefix
    private static final String TRANSLET = "translet"; // extension prefix
    
    private XSLTC _xsltc;             // Reference to the compiler object.
    private XPathParser _xpathParser; // Reference to the XPath parser.
    private Vector _errors;           // Contains all compilation errors
    private Vector _warnings;        // Contains all compilation errors

    private Hashtable   _instructionClasses; // Maps instructions to classes
    private Hashtable   _qNames;
    private Hashtable   _namespaces;
    private QName       _useAttributeSets;
    private QName       _excludeResultPrefixes;
    private QName       _extensionElementPrefixes;
    private Hashtable   _variableScope;
    private Stylesheet  _currentStylesheet;
    private SymbolTable _symbolTable; // Maps QNames to syntax-tree nodes
    private Output      _output = null;
    private Template    _template;    // Reference to the template being parsed.

    private int _currentImportPrecedence = 1;

    public Parser(XSLTC xsltc) {
	_xsltc = xsltc;
    }

    public void setOutput(Output output) {
	if (_output == null)
	    _output = output;
	else
	    output.disable();
    }

    public void init() {
	_qNames              = new Hashtable(512);
	_namespaces          = new Hashtable();
	_instructionClasses  = new Hashtable();
	_variableScope       = new Hashtable();
	_template            = null;
	_errors              = new Vector();
	_warnings            = new Vector();
	_symbolTable         = new SymbolTable();
	_xpathParser         = new XPathParser(this);
	_currentStylesheet   = null;
	_currentImportPrecedence = 1;
	
	initStdClasses();
	initExtClasses();
	initSymbolTable();
	
	_useAttributeSets = getQName(XSLT_URI, XSL, "use-attribute-sets");
	_excludeResultPrefixes
	    = getQName(XSLT_URI, XSL, "exclude-result-prefixes");
	_extensionElementPrefixes
	    = getQName(XSLT_URI, XSL, "extension-element-prefixes");
    }
    
    public void addVariable(Variable var) {
	_variableScope.put(var.getName(), var);
    }

    public void addParameter(Param param) {
	_variableScope.put(param.getName(), param);
    }

    public void removeVariable(QName name) {
	_variableScope.remove(name);
    }

    public SyntaxTreeNode lookupVariable(QName name) {
	return (SyntaxTreeNode)_variableScope.get(name);
    }

    public XSLTC getXSLTC() {
	return _xsltc;
    }

    public int getCurrentImportPrecedence() {
	return _currentImportPrecedence;
    }
    
    public int getNextImportPrecedence() {
	return ++_currentImportPrecedence;
    }

    public void setCurrentStylesheet(Stylesheet stylesheet) {
	_currentStylesheet = stylesheet;
    }

    public Stylesheet getCurrentStylesheet() {
	return _currentStylesheet;
    }
    
    public Stylesheet getTopLevelStylesheet() {
	return _xsltc.getStylesheet();
    }

    public QName getQNameSafe(final String stringRep) {
	// parse and retrieve namespace
	final int colon = stringRep.lastIndexOf(':');
	if (colon != -1) {
	    final String prefix = stringRep.substring(0, colon);
	    final String localname = stringRep.substring(colon + 1);
	    String namespace = null;
	    
	    // Get the namespace uri from the symbol table
	    if (prefix.equals("xmlns") == false) {
		namespace = _symbolTable.lookupNamespace(prefix);
		if (namespace == null) namespace = "";
	    }
	    return getQName(namespace, prefix, localname);
	}
	else {
	    return getQName(_symbolTable.lookupNamespace(""), null, stringRep);
	}
    }
    
    public QName getQName(final String stringRep) {
	// parse and retrieve namespace
	final int colon = stringRep.lastIndexOf(':');
	if (colon != -1) {
	    final String prefix = stringRep.substring(0, colon);
	    final String localname = stringRep.substring(colon + 1);
	    String namespace = null;
	    
	    // Get the namespace uri from the symbol table
	    if (prefix.equals("xmlns") == false) {
		namespace = _symbolTable.lookupNamespace(prefix);
		if (namespace == null) {
		    addError(new ErrorMsg(ErrorMsg.NSPUNDEF_ERR, prefix));
		}
	    }
	    return getQName(namespace, prefix, localname);
	}
	else {
	    return getQName(_symbolTable.lookupNamespace(""), null, stringRep);
	}
    }

    public QName getQName(String namespace, String prefix, String localname) {
	if (namespace == null) {
	    QName name = (QName)_qNames.get(localname);
	    if (name == null) {
		_qNames.put(localname,
			    name = new QName(null, prefix, localname));
	    }
	    return name;
	}
	else {
	    Dictionary space = (Dictionary)_namespaces.get(namespace);
	    if (space == null) {
		final QName name = new QName(namespace, prefix, localname);
		_namespaces.put(namespace, space = new Hashtable());
		space.put(localname, name);
		return name;
	    }
	    else {
		QName name = (QName)space.get(localname);
		if (name == null) {
		    space.put(localname,
			      name = new QName(namespace, prefix, localname));
		}
		return name;
	    }
	}
    }
    
    public QName getQName(String scope, String name) {
	return getQName(scope + name);
    }

    public QName getQName(QName scope, QName name) {
	return getQName(scope.toString() + name.toString());
    }

    public QName getUseAttributeSets() {
	return _useAttributeSets;
    }

    public QName getExtensionElementPrefixes() {
	return _extensionElementPrefixes;
    }

    public QName getExcludeResultPrefixes() {
	return _excludeResultPrefixes;
    }
    
    /**	
     * Create an instance of the <code>Stylesheet</code> class,
     * and then parse, typecheck and compile the instance.
     * Must be called after <code>parse()</code>.
     */
    public Stylesheet makeStylesheet(Element element) 
	throws CompilerException {
	try {
	    Stylesheet stylesheet;

	    // Push any namespaces declared in the root element
	    pushNamespaces(element);

	    // Get the name of this element and try to map it to a class
	    final QName qName = getQName(element.getTagName());
	    final String namespace = element.getNamespaceURI();
	    final String classname = (String)_instructionClasses.get(qName);

	    // Make a Stylesheet object from the root node if it is an
	    // xsl:stylesheet element
	    if (classname != null) {
		stylesheet = (Stylesheet)makeInstance(element);
	    }
	    // Otherwise we create an empty Stylesheet object, and tag it
	    // as a simplified stylesheet (a simplified stylesheet will have
	    // to insert a <xsl:template match="/"/> as its only child).
	    else {
		stylesheet = new Stylesheet();
		stylesheet.setSimplified();
	    }

	    stylesheet.setParser(this);
	    popNamespaces(element);
	    return stylesheet;
	}
	catch (ClassCastException e) {
	    throw new CompilerException("The input document does not "+
					"contain an XSL stylesheet.");
	}
    }
    
    /**
     * Update the symbol table with the namespace declarations defined in
     * <tt>element</tt>.
     */
    public void pushNamespaces(Element element) {
	pushPopNamespaces(element, true);
    }

    /**
     * Update the symbol table with the namespace declarations defined in
     * <tt>element</tt>.
     */
    public void popNamespaces(Element element) {
	pushPopNamespaces(element, false);
    }

    /**
     * Update the symbol table with the namespace declarations defined in
     * <tt>element</tt>. The declarations are added or removed form the
     * symbol table depending on the value of <tt>push</tt>.
     */
    private void pushPopNamespaces(Element element, boolean push) {
	final NamedNodeMap map = element.getAttributes();
	final int n = map.getLength();
	for (int i = 0; i < n; i++) {
	    final Node node = map.item(i);
	    final String name = node.getNodeName();
	    String prefix;

	    if (name.equals("xmlns")) {
		prefix = "";		// default namespace
	    }
	    else {
		final int index = name.indexOf(':');
		if (index >= 0 && name.substring(0, index).equals("xmlns")) {
		    prefix = name.substring(index + 1);
		}
		else {
		    continue;
		}
	    }

	    if (push)
		_symbolTable.pushNamespace(prefix, node.getNodeValue());
	    else
		_symbolTable.popNamespace(prefix);
	}
    }

    public void createAST(Element element, Stylesheet stylesheet) {
	try {
	    if (stylesheet != null) {
		stylesheet.parseContents(element, this);
		final int precedence = stylesheet.getImportPrecedence();
		final Enumeration elements = stylesheet.elements();
		while (elements.hasMoreElements()) {
		    Object child = elements.nextElement();
		    // GTM: fixed bug # 4415344 
		    if (child instanceof Text){
			throw new TypeCheckError(new ErrorMsg(
			    "character data '" + ((Text)child).getText() +
			    "' found outside top level stylesheet element."));
		    }
		}
		if (!errorsFound()) {
		    stylesheet.typeCheck(_symbolTable);
		}
	    }
	}
	catch (TypeCheckError e) {
	    //e.printStackTrace();
	    addError(new ErrorMsg(e.toString())); // TODO
	}
    }

    public Element parse(URL url) {
	return(parse(url.toString()));
    }

    public Element parse(String stylesheetURL) {
	try {
	    // Get an instance of the document builder factory
	    final DocumentBuilderFactory factory =
		DocumentBuilderFactory.newInstance();
	    factory.setNamespaceAware(true);

	    // Then make an instance of the actual document builder
	    final DocumentBuilder builder = factory.newDocumentBuilder();
	    if (!builder.isNamespaceAware()) { // Must be namespace aware
		addError(new ErrorMsg("SAX parser is not namespace aware"));
	    }

	    // Parse the stylesheet document and return the root element
	    Document document = builder.parse(stylesheetURL);
	    return document.getDocumentElement();
	}
	catch (ParserConfigurationException e) {
	    addError(new ErrorMsg("JAXP parser not configured correctly"));
	}
	catch (IOException e) {
	    addError(new ErrorMsg(ErrorMsg.FILECANT_ERR, stylesheetURL));
	}
	catch (SAXParseException e){
	    addError(new ErrorMsg(e.getMessage(),e.getLineNumber()));
	}
	catch (SAXException e) {
	    addError(new ErrorMsg(e.getMessage()));
	}
	return null;
    }

    /**
     * Extracts the DOM for the stylesheet. In the case of an embedded
     * stylesheet, it extracts the DOM subtree corresponding to the 
     * embedded stylesheet that has an 'id' attribute whose value is the
     * same as the value declared in the <?xml-stylesheet...?> processing 
     * instruction (P.I.). In the xml-stylesheet P.I. the value is labeled
     * as the 'href' data of the P.I. The extracted DOM representing the
     * stylesheet is returned as an Element object.
     */
    private Element getStylesheet(Document doc) throws CompilerException {

	// Get the xml-stylesheet processing instruction (P.I.)
	org.w3c.dom.ProcessingInstruction stylesheetPI = getStylesheetPI(doc);
	// If there is none we assume this is a pure XSL file and return root.
	if (stylesheetPI == null ) return (Element)doc.getDocumentElement();

	// Get href value from P.I. to identify the correct stylesheet
	String href = getXmlStylesheetPIHrefValue(stylesheetPI);
	if (href == null) {
	    throw new CompilerException(
		"Processing instruction <?xml-stylesheet ... ?> is " +
		"missing the href data.");
	}

	// Create list of all xsl:stylesheet (or alt. xsl:transform) elements
	NodeList elements = doc.getElementsByTagName("xsl:stylesheet");
	if (elements.getLength() == 0)
	    elements = doc.getElementsByTagName("xsl:transform");

	// Scan all elements to find the one that matches the href in the PI
	for(int i=0; i<elements.getLength(); i++) {
	    Node curr = elements.item(i);
	    NamedNodeMap map = curr.getAttributes();
	    if (map.getLength() != 0) {
		Node attr = map.getNamedItem("id");
		String attrValue = attr.getNodeValue();
		if ((href == null) || (href.equals(attrValue))) {
		    return (Element)curr;
		}
	    }
	}

	// If there is none we assume this is a pure XSL file and return root.
	// GTM Note: removed this line because it was causing failures
	//   in test suite, the presence of a xml-stylesheet P.I. AND
	//   the absence of an href should be an error. See 'href == null'
	//   above.... we will remove this comment and associated line
	//   after test suite confirms it has not damaged anything else. 
	// if (href == null) return (Element)doc.getDocumentElement();

	// If we did not find the references stylesheet in the current XML
	// file we assume it is an external file and load that...
	return(loadExternalStylesheet(doc, href));
    }

    /**
     * For embedded stylesheets: Load an external file with stylesheet
     */
    private Element loadExternalStylesheet(Document doc, String url)
	throws CompilerException {
	try {
	    // Check if the URL is a local file
	    if ((new File(url)).exists()) url = "file:"+url;

	    // Get an instance of the document builder factory
	    final DocumentBuilderFactory factory =
		DocumentBuilderFactory.newInstance();
	    factory.setNamespaceAware(true);

	    // Then make an instance of the actual document builder
	    final DocumentBuilder builder = factory.newDocumentBuilder();
	    if (!builder.isNamespaceAware()) { // Must be namespace aware
		addError(new ErrorMsg("SAX parser is not namespace aware"));
	    }

	    // Parse the stylesheet document and return the root element
	    Document document = builder.parse(url);
	    return document.getDocumentElement();
	}
	catch (ParserConfigurationException e) {
	    throw new CompilerException("JAXP parser not configured "+
					"correctly");
	}
	catch (IOException e) {
	    throw new CompilerException("Could not find stylesheet - '"+url+
					"' is not a named element in the "+
					"current file nor a local file.");
	}
	catch (SAXException e) {
	    throw new CompilerException("Could not parse stylesheet with id='"+
					url+"' (referenced in stylesheet PI)");
	}
    }

    /**
     * Returns the first xml-stylesheet processing instruction found in the DOM.
     */
    private org.w3c.dom.ProcessingInstruction getStylesheetPI(Document doc) {

        Node node = doc;

	while (node != null) {
	    switch (node.getNodeType ()) {
	    case Node.PROCESSING_INSTRUCTION_NODE:
		org.w3c.dom.ProcessingInstruction pi =
		    (org.w3c.dom.ProcessingInstruction)node;
		if (pi.getTarget().equals("xml-stylesheet")) return pi;
		// FALLTHROUGH
	    case Node.DOCUMENT_FRAGMENT_NODE:
	    case Node.DOCUMENT_NODE:
	    case Node.ELEMENT_NODE:
		// First try to traverse any children of the node
		Node child = node.getFirstChild();
		if (child != null) {
		    node = child;
		    break;
		}
		// FALLTHROUGH
	    default:
		// Then try the siblings
		Node next = node.getNextSibling();
		if (next == null) {
		    // Then step up to the parent node
		    Node parent = node.getParentNode();
		    if ((parent == null) || (node == doc)) return null;
		    node = parent;
		}
		else {
		    node = next;
		}
	    }
	}
	return null;
    }

    /**
     * Extracts the value of the 'href' in an xml-stylesheet processing
     * instruction. The value of the P.I. href is prefixed with the '#'
     * character, this method removes the prefix before returning it to 
     * caller. Returns: String (without '#' prefix) or null if none found.
     */
    private String getXmlStylesheetPIHrefValue(
        org.w3c.dom.ProcessingInstruction pi)
    {
	String data = pi.getData();
	int start = -1;
	if ((start = data.indexOf("href")) < 0) {
	    return null;
	}
	String hrefportion = data.substring(start);
	StringTokenizer tok = new StringTokenizer(hrefportion, "\"");
	tok.nextToken();  	// throw away 'href='
	String retval = tok.nextToken();
	return (retval.startsWith("#")) ? retval.substring(1):retval; 
    }
    

    private void initStdClasses() {
	initStdClass("template", "Template");
	initStdClass("stylesheet", "Stylesheet");
	initStdClass("transform", "Stylesheet");
	initStdClass("text", "Text");
	initStdClass("if", "If");
	initStdClass("choose", "Choose");
	initStdClass("when", "When");
	initStdClass("otherwise", "Otherwise");
	initStdClass("for-each", "ForEach");
	initStdClass("message", "Message");
	initStdClass("number", "Number");
	initStdClass("comment", "Comment");
	initStdClass("copy", "Copy");
	initStdClass("copy-of", "CopyOf");
	initStdClass("param", "Param");
	initStdClass("with-param", "WithParam");
	initStdClass("variable", "Variable");
	initStdClass("output", "Output");
	initStdClass("sort", "Sort");
	initStdClass("key", "Key");
	initStdClass("fallback", "Fallback");
	initStdClass("attribute", "XslAttribute");
	initStdClass("attribute-set", "AttributeSet");
	initStdClass("value-of", "ValueOf");
	initStdClass("element", "XslElement");
	initStdClass("call-template", "CallTemplate");
	initStdClass("apply-templates", "ApplyTemplates");
	initStdClass("apply-imports", "ApplyImports");
	initStdClass("decimal-format", "DecimalFormatting");
	initStdClass("import", "Import");
	initStdClass("include", "Include");
	initStdClass("strip-space", "Whitespace");
	initStdClass("preserve-space", "Whitespace");
	initStdClass("processing-instruction", "ProcessingInstruction");
	initStdClass("namespace-alias", "NamespaceAlias");
    }
    
    private void initStdClass(String elementName, String className) {
	_instructionClasses.put(getQName(XSLT_URI, XSL, elementName),
				COMPILER_PACKAGE + '.' + className);
    }

    private void initExtClasses() {
	initExtClass("output", "TransletOutput");
    }

    private void initExtClass(String elementName, String className) {
	_instructionClasses.put(getQName(TRANSLET_URI, TRANSLET, elementName),
				COMPILER_PACKAGE + '.' + className);
    }

    /**
     * Add primops and base functions to the symbol table.
     */
    private void initSymbolTable() {
	MethodType I_V  = new MethodType(Type.Int, Type.Void);
	MethodType I_R  = new MethodType(Type.Int, Type.Real);
	MethodType I_S  = new MethodType(Type.Int, Type.String);
	MethodType I_D  = new MethodType(Type.Int, Type.NodeSet);
	MethodType R_I  = new MethodType(Type.Real, Type.Int);
	MethodType R_V  = new MethodType(Type.Real, Type.Void);
	MethodType R_R  = new MethodType(Type.Real, Type.Real);
	MethodType R_D  = new MethodType(Type.Real, Type.NodeSet);
	MethodType R_O  = new MethodType(Type.Real, Type.Reference);
	MethodType I_I  = new MethodType(Type.Int, Type.Int);
	MethodType D_O  = new MethodType(Type.NodeSet, Type.Reference);
	MethodType D_SS = new MethodType(Type.NodeSet,
					 Type.String, Type.String);
	MethodType D_SD = new MethodType(Type.NodeSet,
					 Type.String, Type.NodeSet);
	MethodType D_S  = new MethodType(Type.NodeSet, Type.String);
	MethodType D_D  = new MethodType(Type.NodeSet, Type.NodeSet);
	MethodType A_V  = new MethodType(Type.Node, Type.Void);
	MethodType S_V  = new MethodType(Type.String, Type.Void);
	MethodType S_S  = new MethodType(Type.String, Type.String);
	MethodType S_A  = new MethodType(Type.String, Type.Node);
	MethodType S_D  = new MethodType(Type.String, Type.NodeSet);
	MethodType S_O  = new MethodType(Type.String, Type.Reference);
	MethodType B_O  = new MethodType(Type.Boolean, Type.Reference);
	MethodType B_V  = new MethodType(Type.Boolean, Type.Void);
	MethodType B_B  = new MethodType(Type.Boolean, Type.Boolean);
	MethodType B_S  = new MethodType(Type.Boolean, Type.String);
	MethodType R_RR = new MethodType(Type.Real, Type.Real, Type.Real);
	MethodType I_II = new MethodType(Type.Int, Type.Int, Type.Int);
	MethodType B_RR = new MethodType(Type.Boolean, Type.Real, Type.Real);
	MethodType B_II = new MethodType(Type.Boolean, Type.Int, Type.Int);
	MethodType B_BB = new MethodType(Type.Boolean, Type.Boolean,
					 Type.Boolean);
	MethodType B_SS = new MethodType(Type.Boolean, Type.String,
					 Type.String);
	MethodType S_SS = new MethodType(Type.String, Type.String,
					 Type.String);
	MethodType S_SD = new MethodType(Type.String, Type.String,
					 Type.NodeSet);
	MethodType S_DS  = new MethodType(Type.String, Type.Real, Type.String);
	MethodType S_DSS = new MethodType(Type.String, Type.Real, Type.String,
					  Type.String);
	MethodType S_SR  = new MethodType(Type.String, Type.String, Type.Real);
	MethodType S_SRR = new MethodType(Type.String, Type.String, Type.Real,
					  Type.Real);
	MethodType S_SSS = new MethodType(Type.String, Type.String,
					  Type.String, Type.String);

	/*
	 * Standard functions: implemented but not in this table concat().
	 * When adding a new function make sure to uncomment
	 * the corresponding line in <tt>FunctionAvailableCall</tt>.
	 */

	// The following functions are inlined
	_symbolTable.addPrimop(getQName("current"), A_V);
	_symbolTable.addPrimop(getQName("last"), I_V);
	_symbolTable.addPrimop(getQName("position"), I_V);
	_symbolTable.addPrimop(getQName("true"), B_V);
	_symbolTable.addPrimop(getQName("false"), B_V);
	_symbolTable.addPrimop(getQName("not"), B_B);
	_symbolTable.addPrimop(getQName("name"), S_V);
	_symbolTable.addPrimop(getQName("name"), S_A);
	_symbolTable.addPrimop(getQName("generate-id"), S_V);
	_symbolTable.addPrimop(getQName("generate-id"), S_A);
	_symbolTable.addPrimop(getQName("ceiling"), R_R);
	_symbolTable.addPrimop(getQName("floor"), R_R);
	_symbolTable.addPrimop(getQName("round"), I_R);
	_symbolTable.addPrimop(getQName("contains"), B_SS);
	_symbolTable.addPrimop(getQName("number"), R_O);
	_symbolTable.addPrimop(getQName("number"), R_V);
	_symbolTable.addPrimop(getQName("boolean"), B_O);
	_symbolTable.addPrimop(getQName("string"), S_O);
	_symbolTable.addPrimop(getQName("string"), S_V);
	_symbolTable.addPrimop(getQName("translate"), S_SSS);
	_symbolTable.addPrimop(getQName("string-length"), I_V);
	_symbolTable.addPrimop(getQName("string-length"), I_S);
	_symbolTable.addPrimop(getQName("starts-with"), B_SS);
	_symbolTable.addPrimop(getQName("format-number"), S_DS);
	_symbolTable.addPrimop(getQName("format-number"), S_DSS);
	_symbolTable.addPrimop(getQName("unparsed-entity-uri"), S_S);
	_symbolTable.addPrimop(getQName("key"), D_SS);
	_symbolTable.addPrimop(getQName("key"), D_SD);
	_symbolTable.addPrimop(getQName("id"), D_S);
	_symbolTable.addPrimop(getQName("id"), D_D);
	_symbolTable.addPrimop(getQName("namespace-uri"), S_V);

	// The following functions are implemented in the basis library
	_symbolTable.addPrimop(getQName("count"), I_D);
	_symbolTable.addPrimop(getQName("sum"), R_D);
	_symbolTable.addPrimop(getQName("local-name"), S_V);
	_symbolTable.addPrimop(getQName("local-name"), S_D);
	_symbolTable.addPrimop(getQName("namespace-uri"), S_V);
	_symbolTable.addPrimop(getQName("namespace-uri"), S_D);
	_symbolTable.addPrimop(getQName("substring"), S_SR);
	_symbolTable.addPrimop(getQName("substring"), S_SRR);
	_symbolTable.addPrimop(getQName("substring-after"), S_SS);
	_symbolTable.addPrimop(getQName("substring-before"), S_SS);
	_symbolTable.addPrimop(getQName("normalize-space"), S_V);
	_symbolTable.addPrimop(getQName("normalize-space"), S_S);
	_symbolTable.addPrimop(getQName("function-available"), B_S);
	_symbolTable.addPrimop(getQName("system-property"), S_S);

	// Operators +, -, *, /, % defined on real types.
	_symbolTable.addPrimop(getQName("+"), R_RR);	
	_symbolTable.addPrimop(getQName("-"), R_RR);	
	_symbolTable.addPrimop(getQName("*"), R_RR);	
	_symbolTable.addPrimop(getQName("/"), R_RR);	
	_symbolTable.addPrimop(getQName("%"), R_RR);	

	// Operators +, -, * defined on integer types.
	// Operators / and % are not  defined on integers (may cause exception)
	_symbolTable.addPrimop(getQName("+"), I_II);	
	_symbolTable.addPrimop(getQName("-"), I_II);	
	_symbolTable.addPrimop(getQName("*"), I_II);	

	 // Operators <, <= >, >= defined on real types.
	_symbolTable.addPrimop(getQName("<"),  B_RR);	
	_symbolTable.addPrimop(getQName("<="), B_RR);	
	_symbolTable.addPrimop(getQName(">"),  B_RR);	
	_symbolTable.addPrimop(getQName(">="), B_RR);	

	// Operators <, <= >, >= defined on int types.
	_symbolTable.addPrimop(getQName("<"),  B_II);	
	_symbolTable.addPrimop(getQName("<="), B_II);	
	_symbolTable.addPrimop(getQName(">"),  B_II);	
	_symbolTable.addPrimop(getQName(">="), B_II);	

	// Operators <, <= >, >= defined on boolean types.
	_symbolTable.addPrimop(getQName("<"),  B_BB);	
	_symbolTable.addPrimop(getQName("<="), B_BB);	
	_symbolTable.addPrimop(getQName(">"),  B_BB);	
	_symbolTable.addPrimop(getQName(">="), B_BB);	

	// Operators 'and' and 'or'.
	_symbolTable.addPrimop(getQName("or"), B_BB);	
	_symbolTable.addPrimop(getQName("and"), B_BB);	

	// Unary minus.
	_symbolTable.addPrimop(getQName("u-"), R_R);	
	_symbolTable.addPrimop(getQName("u-"), I_I);	
	_symbolTable.pushNamespace("xml", "xml");
    }

    public SymbolTable getSymbolTable() {
	return _symbolTable;
    }

    public Template getTemplate() {
	return _template;
    }

    int _templateIndex = 0;

    public int getTemplateIndex() {
	return(_templateIndex++);
    }

    //!!! temporarily here
    public void internalError() {
	_xsltc.internalError();
    }
    
    public void notYetImplemented(String feature) {
	_xsltc.notYetImplemented(feature);
    }
    
    public void setTemplate(Template template) {
	_template = template;
    }

    private Element findFallback(Element root) {
	final NodeList nodes = root.getChildNodes();
	final int length = (nodes != null) ? nodes.getLength() : 0;

	for (int i = 0; i < length; i++) {
	    Node node = nodes.item(i);
	    if (node.getNodeType() == Node.ELEMENT_NODE) {
		Element child = (Element)node;
		String namespace = child.getNamespaceURI();
		String localname = child.getLocalName();
		if (namespace.equals(XSLT_URI) &&
		    localname.equals("fallback")) {
		    return child;
		}
		else {
		    Element result = findFallback(child);
		    if (result != null) return result;
		}
	    }
	}
	return null;
    }

    public Fallback makeFallback(Element root) {
	Element element = findFallback(root);
	if (element != null) {
	    Fallback fallback = (Fallback)makeInstance(FALLBACK_CLASS, element);
	    fallback.activate();
	    fallback.parseContents(element, this);
	    return(fallback);
	}
	else {
	    return(null);
	}
    }

    /**
     * Creates an object that is of a sub-class of SyntaxTreeNode 
     * from the node in the DOM (if possible).
     */
    public SyntaxTreeNode makeInstance(Element element) {
	QName qName = getQName(element.getTagName());
	String className = (String)_instructionClasses.get(qName);
	if (className != null) {
	    return makeInstance(className, element);
	}
	else {
	    final String namespace = element.getNamespaceURI();
	    if (namespace != null) {
		// Check if the element belongs in our namespace
		if (namespace.equals(XSLT_URI)) {
		    Fallback fallback = makeFallback(element);
		    if (fallback == null)
			_xsltc.notYetImplemented(element.getTagName());
		    return(fallback);
		}
		// Check if this is an XSLTC extension element
		else if (namespace.equals(TRANSLET_URI)) {
		    Fallback fallback = makeFallback(element);
		    if (fallback == null)
			_xsltc.extensionNotSupported(element.getTagName());
		    return(fallback);
		}
		// Check if this is an extension of some other XSLT processor
		else if (_xsltc.getStylesheet().isExtension(namespace)) {
		    Fallback fallback = makeFallback(element);
		    if (fallback == null)
			_xsltc.extensionNotSupported(element.getTagName());
		    return(fallback);
		}
	    }
	    return new LiteralElement();
	}
    }
	
    private SyntaxTreeNode makeInstance(String className, Element element) {
	if (className != null) {
	    try {
		final Class clazz = Class.forName(className);
		SyntaxTreeNode node = (SyntaxTreeNode)clazz.newInstance();
		node.setParser(this);
		return node;
	    }
	    catch (ClassNotFoundException e) {
		e.printStackTrace();
	    }
	    catch (Exception e) {
		e.printStackTrace();
		_xsltc.internalError();
	    }
	}
	else {
	    _xsltc.notYetImplemented(element.getTagName());
	}
	return null;
    }

    public Expression parseExpression(SyntaxTreeNode parent,
				      String expression) {
	return (Expression)parseTopLevel(parent, "<EXPRESSION>"+expression,
					 0, null);
    }

    public Expression parseExpression(SyntaxTreeNode parent,
				      Element element, String attrName) {
	return parseExpression(parent, element, attrName, null);
    }

    public Expression parseExpression(SyntaxTreeNode parent,
				      Element element, String attrName,
				      String defaultValue) {
        String expression = element.getAttribute(attrName);
        if (expression.length() == 0 && defaultValue != null) {
            expression = defaultValue;
        }
        //final int line = ((Integer)element.getUserObject()).intValue();
        return (Expression)parseTopLevel(parent, "<EXPRESSION>" + expression, 
					 -1 /*line*/, expression);
    }

    public Pattern parsePattern(SyntaxTreeNode parent, String pattern) {
	return (Pattern)parseTopLevel(parent, "<PATTERN>"+pattern, 0, pattern);
    }

    public Pattern parsePattern(SyntaxTreeNode parent,
				Element element, String attrName) {
        final String pattern = element.getAttribute(attrName);
        //final int line = ((Integer)element.getUserObject()).intValue();
        return (Pattern)parseTopLevel(parent, "<PATTERN>" + pattern, 
				      -1 /*line*/, pattern);
    }

    private SyntaxTreeNode parseTopLevel(SyntaxTreeNode parent,
					 String text, int lineNumber,
					 String expression) {
	try {
	    _xpathParser.setScanner(new XPathLexer(new StringReader(text)));
	    Symbol result = _xpathParser.parse(lineNumber);
	    if (result != null) {
		final SyntaxTreeNode node = (SyntaxTreeNode)result.value;
		node.setParser(this);
		node.setParent(parent);
		return node;
	    } 
            else {
                addError(new ErrorMsg(ErrorMsg.XPATHPAR_ERR,
				      lineNumber, expression));
            }
	}
	catch (Exception e) {
	    if (_xsltc.debug()) {
		e.printStackTrace();
	    }
	    // Intentional fall through
	}
	// Return a dummy pattern (which is an expression)
	SyntaxTreeNode.Dummy.setParser(this);
        return SyntaxTreeNode.Dummy; 
    }

    /**
     * Returns true if there were any errors during compilation
     */
    public boolean errorsFound() {
	return _errors.size() > 0;
    }

    public boolean warnings() {
	return _warnings.size() > 0;
    }

    /**
     * Adds an error to the vector containing compile-time errors.
     */
    public void addError(ErrorMsg error) {
	_errors.addElement(error);
    }

    public void addWarning(ErrorMsg msg) {
	_warnings.addElement(msg);
    }

    /**
     * Prints all compile-time errors
     */
    public void printErrors() {
	System.err.println("Compile errors:");
	final int size = _errors.size();
	for (int i = 0; i < size; i++) {
	    System.err.println("  " + _errors.elementAt(i));
	}
    }

    public void printWarnings() {
	if (_warnings.size() > 0) {
	    System.err.println("Warning:");
	    final int size = _warnings.size();
	    for (int i = 0; i < size; i++) {
		System.err.println("  " + _warnings.elementAt(i));
	    }
	}
    }
}
