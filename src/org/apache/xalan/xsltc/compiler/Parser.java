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
import java.util.Stack;
import java.net.MalformedURLException;

import javax.xml.parsers.*;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import org.w3c.dom.*;
import org.xml.sax.*;

import java_cup.runtime.Symbol;

import org.apache.xalan.xsltc.compiler.util.*;
import org.apache.xalan.xsltc.runtime.AttributeList;

public final class Parser implements Constants, ContentHandler {

    private static final String XSL = "xsl";            // standard prefix
    private static final String TRANSLET = "translet"; // extension prefix
    
    private XSLTC _xsltc;             // Reference to the compiler object.
    private XPathParser _xpathParser; // Reference to the XPath parser.
    private Vector _errors;           // Contains all compilation errors
    private Vector _warnings;        // Contains all compilation errors

    private ErrorListener _errorListener = null;

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

    private SyntaxTreeNode _root = null;

    private String _target;

    private int _currentImportPrecedence = 1;

    private final static String CLASS_NOT_FOUND =
	"Internal XSLTC class not in classpath: ";
    private final static String INTERNAL_ERROR =
	"Unrecoverable XSLTC compilation error: ";
    private final static String UNSUPPORTED_XSL_ERROR =
	"Unsupported XSL element: ";
    private final static String INVALID_EXT_ERROR =
	"Invalid XSLTC extension: ";
    private final static String UNSUPPORTED_EXT_ERROR =
	"Unsupported XSLT extension: ";
    private final static String TEXT_NODE_ERROR =
	"Text data outside of top-level <xsl:stylesheet> element.";
    private final static String MISSING_HREF_ERROR =
	"Processing instruction <?xml-stylesheet ... ?> is missing href data.";

    public Parser(XSLTC xsltc) {
	_xsltc = xsltc;
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
	
	_useAttributeSets =
	    getQName(XSLT_URI, XSL, "use-attribute-sets");
	_excludeResultPrefixes =
	    getQName(XSLT_URI, XSL, "exclude-result-prefixes");
	_extensionElementPrefixes =
	    getQName(XSLT_URI, XSL, "extension-element-prefixes");
    }

    public void setOutput(Output output) {
	if (_output == null)
	    _output = output;
	else
	    output.disable();
    }

    public void setErrorListener(ErrorListener listener) {
	_errorListener = listener;
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
		if (namespace == null) namespace = Constants.EMPTYSTRING;
	    }
	    return getQName(namespace, prefix, localname);
	}
	else {
	    return getQName(_symbolTable.lookupNamespace(Constants.EMPTYSTRING), null, stringRep);
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
		    reportError(Constants.ERROR,
			new ErrorMsg(ErrorMsg.NSPUNDEF_ERR, prefix)); 
		    Exception e = new Exception();
		    e.printStackTrace();
		}
	    }
	    return getQName(namespace, prefix, localname);
	}
	else {
	    return getQName(_symbolTable.lookupNamespace(Constants.EMPTYSTRING), null, stringRep);
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
    public Stylesheet makeStylesheet(SyntaxTreeNode element) 
	throws CompilerException {
	try {
	    Stylesheet stylesheet;

	    if (element instanceof Stylesheet) {
		stylesheet = (Stylesheet)element;
	    }
	    else {
		stylesheet = new Stylesheet();
		stylesheet.setSimplified();
		stylesheet.addElement(element);
	    }
	    stylesheet.setParser(this);
	    return stylesheet;
	}
	catch (ClassCastException e) {
	    throw new CompilerException("The input document does not "+
					"contain an XSL stylesheet.");
	}
    }
    
    /**
     * Instanciates a SAX2 parser and generate the AST from the input.
     */
    public void createAST(Stylesheet stylesheet) {
	try {
	    if (stylesheet != null) {
		stylesheet.parseContents(this);
		final int precedence = stylesheet.getImportPrecedence();
		final Enumeration elements = stylesheet.elements();
		while (elements.hasMoreElements()) {
		    Object child = elements.nextElement();
		    if (child instanceof Text) {
			reportError(Constants.ERROR,
			    new ErrorMsg(TEXT_NODE_ERROR));
		    }
		}
		if (!errorsFound()) {
		    stylesheet.typeCheck(_symbolTable);
		}
	    }
	}
	catch (TypeCheckError e) {
	    reportError(Constants.ERROR, new ErrorMsg(e.toString()));
	}
    }

    // GTM prototype:
    public SyntaxTreeNode parse(InputStream input){
	try {
	    // Create a SAX parser and get the XMLReader object it uses
	    final SAXParserFactory factory = SAXParserFactory.newInstance();
	    try {
		factory.setFeature(Constants.NAMESPACE_FEATURE,true);
	    }
	    catch (Exception e) {
		factory.setNamespaceAware(true);
	    }
	    final SAXParser parser = factory.newSAXParser();
	    final XMLReader reader = parser.getXMLReader();

	    // Parse the input document and build the abstract syntax tree
	    reader.setContentHandler(this);
	    InputSource is = new InputSource(input);
	    reader.parse(new InputSource(input));

	    // Find the start of the stylesheet within the tree
	    return (SyntaxTreeNode)getStylesheet(_root);
	}
	catch (ParserConfigurationException e) {
	    reportError(Constants.ERROR,
		new ErrorMsg("JAXP parser not configured correctly"));
	}
	catch (IOException e) {
	    reportError(Constants.ERROR,
		new ErrorMsg(e.getMessage()));
	}
	catch (SAXParseException e){
	    reportError(Constants.ERROR,
		new ErrorMsg(e.getMessage(),e.getLineNumber()));
	}
	catch (SAXException e) {
	    reportError(Constants.ERROR, new ErrorMsg(e.getMessage()));
	}
	catch (CompilerException e) {
	    reportError(Constants.ERROR, new ErrorMsg(e.getMessage()));
	}
	return null;
    }

    /**
     * Instanciates a SAX2 parser and generate the AST from the input.
     */
    public SyntaxTreeNode parse(URL url) {
	return(parse(url.toString(), true));
    }

    /**
     * Instanciates a SAX2 parser and generate the AST from the input.
     */
    public SyntaxTreeNode parse(String location, boolean isURL) {
	try {
	    // Create a SAX parser and get the XMLReader object it uses
	    final SAXParserFactory factory = SAXParserFactory.newInstance();
	    try {
		factory.setFeature(Constants.NAMESPACE_FEATURE,true);
	    }
	    catch (Exception e) {
		factory.setNamespaceAware(true);
	    }
	    final SAXParser parser = factory.newSAXParser();
	    final XMLReader reader = parser.getXMLReader();

	    // Parse the input document and build the abstract syntax tree
	    reader.setContentHandler(this);
	    if (isURL)
		reader.parse(location);
	    else
		reader.parse("file:"+(new File(location).getAbsolutePath()));

	    // Find the start of the stylesheet within the tree
	    return (SyntaxTreeNode)getStylesheet(_root);
	}
	catch (ParserConfigurationException e) {
	    reportError(Constants.ERROR,
		new ErrorMsg("JAXP parser not configured correctly"));
	}
	catch (IOException e) {
	    reportError(Constants.ERROR,
		new ErrorMsg(ErrorMsg.FILECANT_ERR, location));
	}
	catch (SAXParseException e){
	    reportError(Constants.ERROR,
		new ErrorMsg(e.getMessage(),e.getLineNumber()));
	}
	catch (SAXException e) {
	    reportError(Constants.ERROR, new ErrorMsg(e.getMessage()));
	}
	catch (CompilerException e) {
	    reportError(Constants.ERROR, new ErrorMsg(e.getMessage()));
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
    private SyntaxTreeNode getStylesheet(SyntaxTreeNode root)
	throws CompilerException {

	// Assume that this is a pure XSL stylesheet if there is not
	// <?xml-stylesheet ....?> processing instruction
	if (_target == null) return(root);

	// Find the xsl:stylesheet or xsl:transform with this reference
	if (_target.charAt(0) == '#') {
	    SyntaxTreeNode element = findStylesheet(root, _target.substring(1));
	    return(element);
	}
	else {
	    return(loadExternalStylesheet(_target));
	}
    }

    /**
     * Find a Stylesheet element with a specific ID attribute value.
     * This method is used to find a Stylesheet node that is referred
     * in a <?xml-stylesheet ... ?> processing instruction.
     */
    private SyntaxTreeNode findStylesheet(SyntaxTreeNode root, String href) {

	if (root == null) return null;

	if (root instanceof Stylesheet) {
	    String id = root.getAttribute("id");
	    if (id.equals(href)) return root;
	}
	Vector children = root.getContents();
	if (children != null) {
	    final int count = children.size();
	    for (int i = 0; i < count; i++) {
		SyntaxTreeNode child = (SyntaxTreeNode)children.elementAt(i);
		SyntaxTreeNode node = findStylesheet(child, href);
		if (node != null) return node;
	    }
	}
	return null;	
    }

    /**
     * For embedded stylesheets: Load an external file with stylesheet
     */
    private SyntaxTreeNode loadExternalStylesheet(String url)
	throws CompilerException {

	// Check if the URL is a local file
	if ((new File(url)).exists()) url = "file:"+url;

	SyntaxTreeNode external = (SyntaxTreeNode)parse(url, true);

	return(external);
    }

    /**
     * Initialize the _instructionClasses Hashtable, which maps XSL element
     * names to Java classes in this package.
     */
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
	MethodType I_D  = new MethodType(Type.Int, Type.NodeSetDTM);
	MethodType R_I  = new MethodType(Type.Real, Type.Int);
	MethodType R_V  = new MethodType(Type.Real, Type.Void);
	MethodType R_R  = new MethodType(Type.Real, Type.Real);
	MethodType R_D  = new MethodType(Type.Real, Type.NodeSetDTM);
	MethodType R_O  = new MethodType(Type.Real, Type.Reference);
	MethodType I_I  = new MethodType(Type.Int, Type.Int);
	MethodType D_O  = new MethodType(Type.NodeSetDTM, Type.Reference);
	MethodType D_SS = new MethodType(Type.NodeSetDTM,
					 Type.String, Type.String);
	MethodType D_SD = new MethodType(Type.NodeSetDTM,
					 Type.String, Type.NodeSetDTM);
	MethodType D_S  = new MethodType(Type.NodeSetDTM, Type.String);
	MethodType D_D  = new MethodType(Type.NodeSetDTM, Type.NodeSetDTM);
	MethodType A_V  = new MethodType(Type.Node, Type.Void);
	MethodType S_V  = new MethodType(Type.String, Type.Void);
	MethodType S_S  = new MethodType(Type.String, Type.String);
	MethodType S_A  = new MethodType(Type.String, Type.Node);
	MethodType S_D  = new MethodType(Type.String, Type.NodeSetDTM);
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
					 Type.NodeSetDTM);
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

	_symbolTable.addPrimop("current", A_V);
	_symbolTable.addPrimop("last", I_V);
	_symbolTable.addPrimop("position", I_V);
	_symbolTable.addPrimop("true", B_V);
	_symbolTable.addPrimop("false", B_V);
	_symbolTable.addPrimop("not", B_B);
	_symbolTable.addPrimop("name", S_V);
	_symbolTable.addPrimop("name", S_A);
	_symbolTable.addPrimop("generate-id", S_V);
	_symbolTable.addPrimop("generate-id", S_A);
	_symbolTable.addPrimop("ceiling", R_R);
	_symbolTable.addPrimop("floor", R_R);
	_symbolTable.addPrimop("round", I_R);
	_symbolTable.addPrimop("contains", B_SS);
	_symbolTable.addPrimop("number", R_O);
	_symbolTable.addPrimop("number", R_V);
	_symbolTable.addPrimop("boolean", B_O);
	_symbolTable.addPrimop("string", S_O);
	_symbolTable.addPrimop("string", S_V);
	_symbolTable.addPrimop("translate", S_SSS);
	_symbolTable.addPrimop("string-length", I_V);
	_symbolTable.addPrimop("string-length", I_S);
	_symbolTable.addPrimop("starts-with", B_SS);
	_symbolTable.addPrimop("format-number", S_DS);
	_symbolTable.addPrimop("format-number", S_DSS);
	_symbolTable.addPrimop("unparsed-entity-uri", S_S);
	_symbolTable.addPrimop("key", D_SS);
	_symbolTable.addPrimop("key", D_SD);
	_symbolTable.addPrimop("id", D_S);
	_symbolTable.addPrimop("id", D_D);
	_symbolTable.addPrimop("namespace-uri", S_V);

	// The following functions are implemented in the basis library
	_symbolTable.addPrimop("count", I_D);
	_symbolTable.addPrimop("sum", R_D);
	_symbolTable.addPrimop("local-name", S_V);
	_symbolTable.addPrimop("local-name", S_D);
	_symbolTable.addPrimop("namespace-uri", S_V);
	_symbolTable.addPrimop("namespace-uri", S_D);
	_symbolTable.addPrimop("substring", S_SR);
	_symbolTable.addPrimop("substring", S_SRR);
	_symbolTable.addPrimop("substring-after", S_SS);
	_symbolTable.addPrimop("substring-before", S_SS);
	_symbolTable.addPrimop("normalize-space", S_V);
	_symbolTable.addPrimop("normalize-space", S_S);
	_symbolTable.addPrimop("function-available", B_S);
	_symbolTable.addPrimop("system-property", S_S);

	// Operators +, -, *, /, % defined on real types.
	_symbolTable.addPrimop("+", R_RR);	
	_symbolTable.addPrimop("-", R_RR);	
	_symbolTable.addPrimop("*", R_RR);	
	_symbolTable.addPrimop("/", R_RR);	
	_symbolTable.addPrimop("%", R_RR);	

	// Operators +, -, * defined on integer types.
	// Operators / and % are not  defined on integers (may cause exception)
	_symbolTable.addPrimop("+", I_II);	
	_symbolTable.addPrimop("-", I_II);	
	_symbolTable.addPrimop("*", I_II);	

	 // Operators <, <= >, >= defined on real types.
	_symbolTable.addPrimop("<",  B_RR);	
	_symbolTable.addPrimop("<=", B_RR);	
	_symbolTable.addPrimop(">",  B_RR);	
	_symbolTable.addPrimop(">=", B_RR);	

	// Operators <, <= >, >= defined on int types.
	_symbolTable.addPrimop("<",  B_II);	
	_symbolTable.addPrimop("<=", B_II);	
	_symbolTable.addPrimop(">",  B_II);	
	_symbolTable.addPrimop(">=", B_II);	

	// Operators <, <= >, >= defined on boolean types.
	_symbolTable.addPrimop("<",  B_BB);	
	_symbolTable.addPrimop("<=", B_BB);	
	_symbolTable.addPrimop(">",  B_BB);	
	_symbolTable.addPrimop(">=", B_BB);	

	// Operators 'and' and 'or'.
	_symbolTable.addPrimop("or", B_BB);	
	_symbolTable.addPrimop("and", B_BB);	

	// Unary minus.
	_symbolTable.addPrimop("u-", R_R);	
	_symbolTable.addPrimop("u-", I_I);	
    }

    public SymbolTable getSymbolTable() {
	return _symbolTable;
    }

    public Template getTemplate() {
	return _template;
    }

    public void setTemplate(Template template) {
	_template = template;
    }

    private int _templateIndex = 0;

    public int getTemplateIndex() {
	return(_templateIndex++);
    }

    /**
     * Creates a new node in the abstract syntax tree. This node can be
     *  o) a supported XSLT 1.0 element
     *  o) an unsupported XSLT element (post 1.0)
     *  o) a supported XSLT extension
     *  o) an unsupported XSLT extension
     *  o) a literal result element (not an XSLT element and not an extension)
     * Unsupported elements do not directly generate an error. We have to wait
     * until we have received all child elements of an unsupported element to
     * see if any <xsl:fallback> elements exist.
     */
    public SyntaxTreeNode makeInstance(String uri, String prefix, String local){
	QName  qname = getQName(uri, prefix, local);
	String className = (String)_instructionClasses.get(qname);
	SyntaxTreeNode node = null;

	if (className != null) {
	    try {
		final Class clazz = Class.forName(className);
		node = (SyntaxTreeNode)clazz.newInstance();
		node.setQName(qname);
		node.setParser(this);
		if (node instanceof Stylesheet) {
		    _xsltc.setStylesheet((Stylesheet)node);
		}
	    }
	    catch (ClassNotFoundException e) {
		reportError(Constants.ERROR,
		    new ErrorMsg(CLASS_NOT_FOUND+className));
	    }
	    catch (Exception e) {
		reportError(Constants.ERROR,
		    new ErrorMsg(INTERNAL_ERROR+e.getMessage()));
	    }
	}
	else {
	    if (uri != null) {
		// Check if the element belongs in our namespace
		if (uri.equals(XSLT_URI)) {
		    node = new UnsupportedElement(uri, prefix, local);
		    UnsupportedElement element = (UnsupportedElement)node;
		    element.setErrorMessage(UNSUPPORTED_XSL_ERROR+local);
		}
		// Check if this is an XSLTC extension element
		else if (uri.equals(TRANSLET_URI)) {
		    node = new UnsupportedElement(uri, prefix, local);
		    UnsupportedElement element = (UnsupportedElement)node;
		    element.setErrorMessage(INVALID_EXT_ERROR+local);
		}
		// Check if this is an extension of some other XSLT processor
		else if ((_xsltc.getStylesheet() != null) &&
			 (_xsltc.getStylesheet().isExtension(uri))) {
		    node = new UnsupportedElement(uri, prefix, local);
		    UnsupportedElement element = (UnsupportedElement)node;
		    element.setErrorMessage(UNSUPPORTED_EXT_ERROR+
					    prefix+":"+local);
		}
	    }
	    if (node == null) node = new LiteralElement();
	}
	if ((node != null) && (node instanceof LiteralElement)) {
	    ((LiteralElement)node).setQName(qname);
	}
	return(node);
    }

    /**
     * Parse an XPath expression:
     *  @parent - XSL element where the expression occured
     *  @exp    - textual representation of the expression
     */
    public Expression parseExpression(SyntaxTreeNode parent, String exp) {
	return (Expression)parseTopLevel(parent, "<EXPRESSION>"+exp, 0, null);
    }

    /**
     * Parse an XPath expression:
     *  @parent - XSL element where the expression occured
     *  @attr   - name of this element's attribute to get expression from
     *  @def    - default expression (if the attribute was not found)
     */
    public Expression parseExpression(SyntaxTreeNode parent,
				      String attr, String def) {
	// Get the textual representation of the expression (if any)
        String exp = parent.getAttribute(attr);
	// Use the default expression if none was found
        if ((exp.length() == 0) && (def != null)) exp = def;
	// Invoke the XPath parser
        return (Expression)parseTopLevel(parent, "<EXPRESSION>"+exp, 0, exp);
    }

    /**
     * Parse an XPath pattern:
     *  @parent - XSL element where the pattern occured
     *  @exp    - textual representation of the pattern
     */
    public Pattern parsePattern(SyntaxTreeNode parent, String pattern) {
	return (Pattern)parseTopLevel(parent, "<PATTERN>"+pattern, 0, pattern);
    }

    /**
     * Parse an XPath pattern:
     *  @parent - XSL element where the pattern occured
     *  @attr   - name of this element's attribute to get pattern from
     *  @def    - default pattern (if the attribute was not found)
     */
    public Pattern parsePattern(SyntaxTreeNode parent,
				String attr, String def) {
	// Get the textual representation of the pattern (if any)
        String pattern = parent.getAttribute(attr);
	// Use the default pattern if none was found
	if ((pattern.length() == 0) && (def != null)) pattern = def;
	// Invoke the XPath parser
        return (Pattern)parseTopLevel(parent, "<PATTERN>"+pattern, 0, pattern);
    }

    /**
     * Parse an XPath expression or pattern using the generated XPathParser
     * The method will return a Dummy node if the XPath parser fails.
     */
    private SyntaxTreeNode parseTopLevel(SyntaxTreeNode parent,
					 String text, int line,
					 String expression) {
	try {
	    _xpathParser.setScanner(new XPathLexer(new StringReader(text)));
	    Symbol result = _xpathParser.parse(line);
	    if (result != null) {
		final SyntaxTreeNode node = (SyntaxTreeNode)result.value;
		if (node != null) {
		    node.setParser(this);
		    node.setParent(parent);
		    return node;
		}
	    } 
	    reportError(Constants.ERROR,
		new ErrorMsg(ErrorMsg.XPATHPAR_ERR, line, expression));
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


    public void internalError() {
	Exception e = new Exception();
	e.printStackTrace();
	reportError(Constants.INTERNAL,
	    new ErrorMsg("Internal compiler error.\n"+
                "Please report to xalan-dev@xml.apache.org\n"+
                "(include stack trace)"));
    }

    public void notYetImplemented(String message) {
	reportError(Constants.UNSUPPORTED, new ErrorMsg(message));
    }


    /**
     * Prints all compile-time errors
     */
    public void printErrors() {
	if (_errorListener != null) return;   //support for TrAX Error Listener
	System.err.println("Compile errors:");
	final int size = _errors.size();
	for (int i = 0; i < size; i++) {
	    System.err.println("  " + _errors.elementAt(i));
	}
    }

    /**
     * Prints all compile-time warnings
     */
    public void printWarnings() {
	if (_errorListener != null) return;  //support for TrAX Error Listener 
	if (_warnings.size() > 0) {
	    System.err.println("Warning:");
	    final int size = _warnings.size();
	    for (int i = 0; i < size; i++) {
		System.err.println("  " + _warnings.elementAt(i));
	    }
	}
    }

    /**
     * Suggested common error handler - not in use yet!!!
     */
// JUMP
    public void reportError(final int category, final ErrorMsg error) {
	try {
	    switch (category) {
	    case Constants.INTERNAL:
		// Unexpected internal errors, such as null-ptr exceptions, etc.
		// Immediately terminates compilation, no translet produced
		_errors.addElement(error);
		if (_errorListener != null) {
		    _errorListener.fatalError(new TransformerException(
			error.toString()));
		}
		break;
	    case Constants.UNSUPPORTED:
		// XSLT elements that are not implemented and unsupported ext.
		// Immediately terminates compilation, no translet produced
		_errors.addElement(error);
		if (_errorListener != null) {
		    final String msg = error.toString();
                    _errorListener.fatalError(new TransformerException(
                        "Not Implemented, Unsupported Extension: " +
			msg));
                }

		break;
	    case Constants.FATAL:
		// Fatal error in the stylesheet input (parsing or content)
		// Immediately terminates compilation, no translet produced

        	_errors.addElement(error);
        	if (_errorListener != null ) {
		    final String msg = error.toString();
		    _errorListener.fatalError(new TransformerException(
                        "Stylesheet Parsing Fatal Error: " + msg));
        	}
		break;
	    case Constants.ERROR:
		// Other error in the stylesheet input (parsing or content)
		// Does not terminate compilation, no translet produced
		_errors.addElement(error);
		if (_errorListener != null) {
		    final String msg = error.toString();
		    _errorListener.error(new TransformerException(
		        "Stylesheet Parsing Error: " + msg));
		}
		break;
	    case Constants.WARNING:
		// Other error in the stylesheet input (content errors only)
		// Does not terminate compilation, a translet is produced
		_warnings.addElement(error);
		if (_errorListener != null) {
		    final String msg = error.toString();
		    _errorListener.warning(new TransformerException(
		        "Stylesheet Parsing Warning: " + msg));
		}
		break;
	    }
	}
	catch (TransformerException e) {
	    // If the error listener does not handle the exception then
	    // we're certainly not doing anything about it....
	}
    }

    /************************ SAX2 ContentHandler INTERFACE *****************/

    private Stack _parentStack = null;
    private Hashtable _prefixMapping = null;

    /**
     * SAX2: Receive notification of the beginning of a document.
     */
    public void startDocument() {
	_root = null;
	_target = null;
	_prefixMapping = null;
	_parentStack = new Stack();
    }

    /**
     * SAX2: Receive notification of the end of a document.
     */
    public void endDocument() { }


    /**
     * SAX2: Begin the scope of a prefix-URI Namespace mapping.
     *       This has to be passed on to the symbol table!
     */
    public void startPrefixMapping(String prefix, String uri) {
	if (_prefixMapping == null) _prefixMapping = new Hashtable();
	_prefixMapping.put(prefix, uri);
	//System.err.println("starting mapping for "+prefix+"="+uri);
    }

    /**
     * SAX2: End the scope of a prefix-URI Namespace mapping.
     *       This has to be passed on to the symbol table!
     */
    public void endPrefixMapping(String prefix) {
	//System.err.println("ending mapping for "+prefix);
    }

    /**
     * SAX2: Receive notification of the beginning of an element.
     *       The parser may re-use the attribute list that we're passed so
     *       we clone the attributes in our own Attributes implementation
     */
    public void startElement(String uri, String localname,
			     String qname, Attributes attributes) 
	throws SAXException {

	final int col = qname.lastIndexOf(':');
	final String prefix;
	if (col == -1)
	    prefix = null;
	else
	    prefix = qname.substring(0, col);

	SyntaxTreeNode element = makeInstance(uri, prefix, localname);
	if (element == null) {
	    throw new SAXException("Error while parsing stylesheet.");
	}

	if (_root == null) {
	    _root = element;
	}
	else {
	    SyntaxTreeNode parent = (SyntaxTreeNode)_parentStack.peek();
	    parent.addElement(element);
	    element.setParent(parent);
	}
	element.setAttributes((Attributes)new AttributeList(attributes));
	element.setPrefixMapping(_prefixMapping);
	
	if (element instanceof Stylesheet) {
	    // Extension elements and excluded elements have to be
	    // handled at this point in order to correctly generate
	    // Fallback elements from <xsl:fallback>s.
	    getSymbolTable().setCurrentNode(element);
	    ((Stylesheet)element).excludeExtensionPrefixes(this);
	}

	_prefixMapping = null;
	_parentStack.push(element);
    }

    /**
     * SAX2: Receive notification of the end of an element.
     */
    public void endElement(String uri, String localname, String qname) {
	_parentStack.pop();
    }

    /**
     * SAX2: Receive notification of character data.
     */
    public void characters(char[] ch, int start, int length) {
	String string = new String(ch, start, length);
	SyntaxTreeNode parent = (SyntaxTreeNode)_parentStack.peek();

	// If this text occurs within an <xsl:text> element we append it
	// as-is to the existing text element
	if (parent instanceof Text) {
	    if (string.length() > 0) {
		((Text)parent).setText(string);
	    }
	}
	// Ignore text nodes that occur directly under <xsl:stylesheet>
	else if (parent instanceof Stylesheet) {

	}
	// Add it as a regular text node otherwise
	else {
	    if (string.trim().length() > 0) {
		parent.addElement(new Text(string));
	    }
	}
    }

    /**
     * SAX2: Receive notification of a processing instruction.
     *       These require special handling for stylesheet PIs.
     */
    public void processingInstruction(String name, String value) {
	if ((_target == null) && (name.equals("xml-stylesheet"))) {
	    StringTokenizer tokens = new StringTokenizer(value);
	    while (tokens.hasMoreElements()) {
		String token = (String)tokens.nextElement();
		if (token.startsWith("href=")) {
		    _target = token.substring(5);
		    final int start = _target.indexOf('"');
		    final int stop = _target.lastIndexOf('"');
		    _target = _target.substring(start+1,stop);
		    return;
		}
	    }
	}
    }

    /**
     * IGNORED - all ignorable whitespace is ignored
     */
    public void ignorableWhitespace(char[] ch, int start, int length) { }

    /**
     * IGNORED - we do not have to do anything with skipped entities
     */
    public void skippedEntity(String name) { }

    /**
     * IGNORED - we already know what the origin of the document is
     */
    public void setDocumentLocator(Locator locator) { }

}
