/*
 * @(#)$Id$
 *
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
 * @author Erwin Bolwidt <ejb@klomp.org>
 *
 */

package org.apache.xalan.xsltc.compiler;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Stack;
import java.net.MalformedURLException;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.helpers.AttributesImpl;

import java_cup.runtime.Symbol;
import org.apache.xalan.xsltc.compiler.util.*;

public class Parser implements Constants, ContentHandler {

    /**
     * Standard prefix bound to XSL uri.
     */
    private static final String XSL = "xsl";

    /**
     * Extension prefix.
     */
    private static final String TRANSLET = "translet";

    /**
     * SAX locator object. Used to obtain the location of any content
     * handler event in the XML source document.
     */
    private Locator _locator = null;

    /**
     * Mapping between an instruction name (e.g. template) to the
     * name of the class implementing that instruction. Java
     * reflection is used to load the class from its name.
     */
    private HashMap _instructionClasses;

    /**
     * Mapping between an instruction name (e.g. template) to an
     * array of strings contaning all the attributes allowed for
     * that instruction. Used for error reporting.
     */
    private HashMap _instructionAttrs;

    /**
     * A reference to the XSLTC object.
     */
    private XSLTC _xsltc;

    /**
     * A list of all the errors encountered during compilation.
     */
    private ArrayList _errors;

    /**
     * A list of all the warnings encountered during compilation.
     */
    private ArrayList _warnings;

    /**
     * A reference to the XPath parser generated using Javacup/JLex.
     */
    private XPathParser _xpathParser;

    /**
     * A reference to the static context.
     */
    private StaticContext _staticContext;

    /**
     * A reference to the parsed xsl:output object. Multiple output
     * declarations are merged as defined in the XSLT spec.
     */
    private Output _output = null;

    /**
     * A reference to the template being parsed.
     */
    private Template _template;

    /**
     * A reference to the stylesheet's root element.
     */
    private SyntaxTreeNode _root = null;

    /**
     * Set to true if the XSL namespace is defined in the stylesheet.
     * Used to handle stylsheets specified via the xml-stylesheet PI.
     */
    private boolean  _rootNamespaceDef = false;

    /**
     * Target of xml-stylesheet PI (if present).
     */
    private String _target;

    /**
     * Current import precendence. This is incremented every time a
     * stylesheet is imported.
     */
    private int _currentImportPrecedence = 1;

    public Parser(XSLTC xsltc) {
	_xsltc = xsltc;
    }

    public void init() {
	_instructionClasses  = new HashMap();
	_instructionAttrs    = new HashMap();
	_template            = null;
	_errors              = new ArrayList();
	_warnings            = new ArrayList();
	_currentImportPrecedence = 1;
	_staticContext       = StaticContext.getInstance(null);
        _xpathParser         =
            new XPathParser(CompilerContext.getInstance(), _staticContext);

	initStdClasses();
	initInstructionAttrs();
	initExtClasses();
	initStaticContextPrimops();
    }

    public void setOutput(Output output) {
	if (_output != null) {
	    if (_output.getImportPrecedence() <= output.getImportPrecedence()) {
		String cdata = _output.getCdata();
		output.mergeCdata(cdata);
		_output.disable();
		_output = output;
	    }
	    else {
		output.disable();
	    }
	}
	else {
	    _output = output;
	}
    }

    public Output getOutput() {
	return _output;
    }

    public Properties getOutputProperties() {
	return getTopLevelStylesheet().getOutputProperties();
    }

    /**
     * Store the document locator to later retrieve line numbers of all
     * elements from the stylesheet
     */
    public void setDocumentLocator(Locator locator) {
        _locator = locator;
    }

    public void setXSLTC(XSLTC xsltc) {
	_xsltc = xsltc;
    }

    public XSLTC getXSLTC() {
	return _xsltc;
    }

    public Stylesheet getTopLevelStylesheet() {
        return _xsltc.getStylesheet();
    }

    public int getCurrentImportPrecedence() {
	return _currentImportPrecedence;
    }

    public int getNextImportPrecedence() {
	return ++_currentImportPrecedence;
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
		stylesheet.add(element);
		stylesheet.setAttributes(element.getAttributes());

		// Map the default NS if not already defined
		if (element.lookupNamespace(EMPTYSTRING) == null) {
		    element.addPrefixMapping(EMPTYSTRING, EMPTYSTRING);
		}
	    }
	    return stylesheet;
	}
	catch (ClassCastException e) {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.NOT_STYLESHEET_ERR, element);
	    throw new CompilerException(err.toString());
	}
    }

    /**
     * This method performs two passes on the AST. The first pass is used
     * to parse attributes and XPath expression. The second pass is used
     * to type check the tree.
     */
    public void createAST(Stylesheet stylesheet) {
        CompilerContext ccontext = CompilerContext.getInstance();

	try {
	    if (stylesheet != null) {
                // Initiate parsing phase by calling parse()
		stylesheet.parse(ccontext);

		final int precedence = stylesheet.getImportPrecedence();
		final Iterator elements = stylesheet.iterator();
		while (elements.hasNext()) {
		    Object child = elements.next();
		    if (child instanceof Text) {
			final int l = _locator.getLineNumber();
			ErrorMsg err = new ErrorMsg(
                            ErrorMsg.ILLEGAL_TEXT_NODE_ERR, l, null);
			reportError(ERROR, err);
		    }
		}
		if (!errorsFound()) {
                    // Initiate type checking phase by calling typeCheck()
		    stylesheet.typeCheck(ccontext);
		}
	    }
	}
	catch (TypeCheckError e) {
	    reportError(ERROR, new ErrorMsg(e.toString()));
	}
    }

    /**
     * Parses a stylesheet and builds the internal abstract syntax tree
     * @param reader A SAX2 SAXReader (parser)
     * @param input A SAX2 InputSource can be passed to a SAX reader
     * @return The root of the abstract syntax tree
     */
    public SyntaxTreeNode parse(XMLReader reader, InputSource input) {
	try {
	    // Parse the input document and build the abstract syntax tree
	    reader.setContentHandler(this);
	    reader.parse(input);
	    // Find the start of the stylesheet within the tree
	    return (SyntaxTreeNode)getStylesheet(_root);
	}
	catch (IOException e) {
	    if (_xsltc.getDebug()) e.printStackTrace();
	    reportError(ERROR,new ErrorMsg(e.getMessage()));
	}
	catch (SAXException e) {
	    Throwable ex = e.getException();
	    if (_xsltc.getDebug()) {
		e.printStackTrace();
		if (ex != null) ex.printStackTrace();
	    }
	    reportError(ERROR, new ErrorMsg(e.getMessage()));
	}
	catch (CompilerException e) {
	    if (_xsltc.getDebug()) e.printStackTrace();
	    reportError(ERROR, new ErrorMsg(e.getMessage()));
	}
	catch (Exception e) {
	    if (_xsltc.getDebug()) e.printStackTrace();
	    reportError(ERROR, new ErrorMsg(e.getMessage()));
	}
	return null;
    }

    /**
     * Parses a stylesheet and builds the internal abstract syntax tree
     * @param input A SAX2 InputSource can be passed to a SAX reader
     * @return The root of the abstract syntax tree
     */
    public SyntaxTreeNode parse(InputSource input) {
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
	    return(parse(reader, input));
	}
	catch (ParserConfigurationException e) {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.SAX_PARSER_CONFIG_ERR);
	    reportError(ERROR, err);
	}
	catch (SAXParseException e){
	    reportError(ERROR, new ErrorMsg(e.getMessage(),e.getLineNumber()));
	}
	catch (SAXException e) {
	    reportError(ERROR, new ErrorMsg(e.getMessage()));
	}
	return null;
    }

    public SyntaxTreeNode getDocumentRoot() {
	return _root;
    }

    private String _PImedia = null;
    private String _PItitle = null;
    private String _PIcharset = null;

    /**
     * Set the parameters to use to locate the correct <?xml-stylesheet ...?>
     * processing instruction in the case where the input document is an
     * XML document with one or more references to a stylesheet.
     * @param media The media attribute to be matched. May be null, in which
     * case the prefered templates will be used (i.e. alternate = no).
     * @param title The value of the title attribute to match. May be null.
     * @param charset The value of the charset attribute to match. May be null.
     */
    protected void setPIParameters(String media, String title, String charset) {
	_PImedia = media;
	_PItitle = title;
	_PIcharset = charset;
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
	if (_target == null) {
	    if (!_rootNamespaceDef) {
		ErrorMsg msg = new ErrorMsg(ErrorMsg.MISSING_XSLT_URI_ERR);
		throw new CompilerException(msg.toString());
	    }
	    return(root);
	}

	// Find the xsl:stylesheet or xsl:transform with this reference
	if (_target.charAt(0) == '#') {
	    SyntaxTreeNode element = findStylesheet(root, _target.substring(1));
	    if (element == null) {
		ErrorMsg msg = new ErrorMsg(ErrorMsg.MISSING_XSLT_TARGET_ERR,
					    _target, root);
		throw new CompilerException(msg.toString());
	    }
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
	ArrayList children = root.getContents();
	if (children != null) {
	    final int count = children.size();
	    for (int i = 0; i < count; i++) {
		SyntaxTreeNode child = (SyntaxTreeNode)children.get(i);
		SyntaxTreeNode node = findStylesheet(child, href);
		if (node != null) return node;
	    }
	}
	return null;
    }

    /**
     * For embedded stylesheets: Load an external file with stylesheet
     */
    private SyntaxTreeNode loadExternalStylesheet(String location)
	throws CompilerException {

	InputSource source;

	// Check if the location is URL or a local file
	if ((new File(location)).exists())
	    source = new InputSource("file:"+location);
	else
	    source = new InputSource(location);

	SyntaxTreeNode external = (SyntaxTreeNode)parse(source);
	return(external);
    }

    private void initAttrTable(String elementName, String[] attrs) {
	_instructionAttrs.put(getQName(XSLT_URI, XSL, elementName),
				attrs);
    }

    /**
     * Initializes a mapping between XSL element and a list of attributes
     * that may appear as part of the element. If a mandatory attribute is
     * missing, this we'll be reported by each instruction independently.
     * Attributes that are not listed here are allowed only if their
     * expanded name has a non-null URI.
     */
    private void initInstructionAttrs() {
	initAttrTable("template",
	    new String[] {"match", "name", "priority", "mode"});
	initAttrTable("stylesheet",
	    new String[] {"id", "version", "extension-element-prefixes",
		"exclude-result-prefixes"});
	initAttrTable("transform",
	    new String[] {"id", "version", "extension-element-prefixes",
		"exclude-result-prefixes"});
	initAttrTable("text", new String[] {"disable-output-escaping"});
	initAttrTable("if", new String[] {"test"});
	initAttrTable("choose", null);
	initAttrTable("when", new String[] {"test"});
	initAttrTable("otherwise", null);
	initAttrTable("for-each", new String[] {"select"});
	initAttrTable("message", new String[] {"terminate"});
	initAttrTable("number",
	    new String[] {"level", "count", "from", "value", "format", "lang",
		"letter-value", "grouping-separator", "grouping-size"});
	initAttrTable("comment", null);
	initAttrTable("copy", new String[] {"use-attribute-sets"});
	initAttrTable("copy-of", new String[] {"select"});
	initAttrTable("param", new String[] {"name", "select"});
	initAttrTable("with-param", new String[] {"name", "select"});
	initAttrTable("variable", new String[] {"name", "select"});
	initAttrTable("output",
	    new String[] {"method", "version", "encoding",
		"omit-xml-declaration", "standalone", "doctype-public",
		"doctype-system", "cdata-section-elements", "indent",
		"media-type"});
	initAttrTable("sort",
	   new String[] {"select", "order", "case-order", "lang", "data-type"});
	initAttrTable("key", new String[] {"name", "match", "use"});
	initAttrTable("fallback", null);
	initAttrTable("attribute", new String[] {"name", "namespace"});
	initAttrTable("attribute-set",
	    new String[] {"name", "use-attribute-sets"});
	initAttrTable("value-of",
	    new String[] {"select", "disable-output-escaping"});
	initAttrTable("element",
	    new String[] {"name", "namespace", "use-attribute-sets"});
	initAttrTable("call-template", new String[] {"name"});
	initAttrTable("apply-templates", new String[] {"select", "mode"});
	initAttrTable("apply-imports", null);
	initAttrTable("decimal-format",
	    new String[] {"name", "decimal-separator", "grouping-separator",
		"infinity", "minus-sign", "NaN", "percent", "per-mille",
		"zero-digit", "digit", "pattern-separator"});
	initAttrTable("import", new String[] {"href"});
	initAttrTable("include", new String[] {"href"});
	initAttrTable("strip-space", new String[] {"elements"});
	initAttrTable("preserve-space", new String[] {"elements"});
	initAttrTable("processing-instruction", new String[] {"name"});
	initAttrTable("namespace-alias",
	   new String[] {"stylesheet-prefix", "result-prefix"});

        // XSLT 2.0
        initAttrTable("analyze-string",
           new String[] {"select", "regex", "flags"});
        initAttrTable("import-schema",
           new String[] {"namespace", "schema-location"});
        initAttrTable("for-each-group",
           new String[] {"select", "group-by", "group-adjacent",
               "group-starting-with", "group-ending-with",
               "collation", "as"});
        initAttrTable("function",
              new String[] {"name", "override"});
        initAttrTable("matching-substring", null);
        initAttrTable("namespace", new String[] {"name"});
        initAttrTable("non-matching-substring", null);
        initAttrTable("result",
              new String[] {"select", "as", "type-information"});
        initAttrTable("result-document",
              new String[] {"format", "href", "type-information"});
        initAttrTable("sort-key",
              new String[] {"name"});
    }

    /**
     * Initialize the _instructionClasses HashMap, which maps XSL element
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

        // XSLT 2.0
        initStdClass("analyze-string", "AnalyzeString");
        initStdClass("import-schema", "ImportSchema");
        initStdClass("for-each-group", "ForEachGroup");
        initStdClass("function", "Function");
        initStdClass("matching-substring", "MatchingSubstring");
        initStdClass("namespace", "Namespace");
        initStdClass("non-matching-substring", "NonMatchingSubstring");
        initStdClass("result", "Result");
        initStdClass("result-document", "ResultDocument");
        initStdClass("sort-key", "SortKey");
    }

    private void initStdClass(String elementName, String className) {
	_instructionClasses.put(getQName(XSLT_URI, XSL, elementName),
				COMPILER_PACKAGE + '.' + className);
    }

    public boolean elementSupported(String namespace, String localName) {
	return(_instructionClasses.get(getQName(namespace, XSL, localName))
               != null);
    }

    public boolean functionSupported(String fname) {
	return (_staticContext.getPrimop(fname) != null);
    }

    private void initExtClasses() {
	initExtClass("output", "TransletOutput");
        initExtClass(REDIRECT_URI, "write", "TransletOutput");
    }

    private void initExtClass(String elementName, String className) {
	_instructionClasses.put(getQName(TRANSLET_URI, TRANSLET, elementName),
				COMPILER_PACKAGE + '.' + className);
    }

    private void initExtClass(String namespace, String elementName, String className) {
        _instructionClasses.put(getQName(namespace, TRANSLET, elementName),
                                COMPILER_PACKAGE + '.' + className);
    }

    /**
     * Add primops and base functions to the static context.
     */
    private void initStaticContextPrimops() {
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
	MethodType D_V  = new MethodType(Type.NodeSet, Type.Void);
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
	MethodType D_X  = new MethodType(Type.NodeSet, Type.Object);
	MethodType R_RR = new MethodType(Type.Real, Type.Real, Type.Real);
	MethodType I_II = new MethodType(Type.Int, Type.Int, Type.Int);
	MethodType B_RR = new MethodType(Type.Boolean, Type.Real, Type.Real);
	MethodType B_II = new MethodType(Type.Boolean, Type.Int, Type.Int);
	MethodType S_SS = new MethodType(Type.String, Type.String, Type.String);
	MethodType S_DS = new MethodType(Type.String, Type.Real, Type.String);
	MethodType S_SR = new MethodType(Type.String, Type.String, Type.Real);

	MethodType D_SS =
	    new MethodType(Type.NodeSet, Type.String, Type.String);
	MethodType D_SD =
	    new MethodType(Type.NodeSet, Type.String, Type.NodeSet);
	MethodType B_BB =
	    new MethodType(Type.Boolean, Type.Boolean, Type.Boolean);
	MethodType B_SS =
	    new MethodType(Type.Boolean, Type.String, Type.String);
	MethodType S_SD =
	    new MethodType(Type.String, Type.String, Type.NodeSet);
	MethodType S_DSS =
	    new MethodType(Type.String, Type.Real, Type.String, Type.String);
	MethodType S_SRR =
	    new MethodType(Type.String, Type.String, Type.Real, Type.Real);
	MethodType S_SSS =
	    new MethodType(Type.String, Type.String, Type.String, Type.String);

	/*
	 * Standard functions: implemented but not in this table concat().
	 * When adding a new function make sure to uncomment
	 * the corresponding line in <tt>FunctionAvailableCall</tt>.
	 */

	// The following functions are inlined

	_staticContext.addPrimop("current", A_V);
	_staticContext.addPrimop("last", I_V);
	_staticContext.addPrimop("position", I_V);
	_staticContext.addPrimop("true", B_V);
	_staticContext.addPrimop("false", B_V);
	_staticContext.addPrimop("not", B_B);
	_staticContext.addPrimop("name", S_V);
	_staticContext.addPrimop("name", S_A);
	_staticContext.addPrimop("generate-id", S_V);
	_staticContext.addPrimop("generate-id", S_A);
	_staticContext.addPrimop("ceiling", R_R);
	_staticContext.addPrimop("floor", R_R);
	_staticContext.addPrimop("round", R_R);
	_staticContext.addPrimop("contains", B_SS);
	_staticContext.addPrimop("number", R_O);
	_staticContext.addPrimop("number", R_V);
	_staticContext.addPrimop("boolean", B_O);
	_staticContext.addPrimop("string", S_O);
	_staticContext.addPrimop("string", S_V);
	_staticContext.addPrimop("translate", S_SSS);
	_staticContext.addPrimop("string-length", I_V);
	_staticContext.addPrimop("string-length", I_S);
	_staticContext.addPrimop("starts-with", B_SS);
	_staticContext.addPrimop("format-number", S_DS);
	_staticContext.addPrimop("format-number", S_DSS);
	_staticContext.addPrimop("unparsed-entity-uri", S_S);
	_staticContext.addPrimop("key", D_SS);
	_staticContext.addPrimop("key", D_SD);
	_staticContext.addPrimop("id", D_S);
	_staticContext.addPrimop("id", D_D);
	_staticContext.addPrimop("namespace-uri", S_V);
	_staticContext.addPrimop("function-available", B_S);
	_staticContext.addPrimop("element-available", B_S);
	_staticContext.addPrimop("document", D_S);
	_staticContext.addPrimop("document", D_V);

	// The following functions are implemented in the basis library
	_staticContext.addPrimop("count", I_D);
	_staticContext.addPrimop("sum", R_D);
	_staticContext.addPrimop("local-name", S_V);
	_staticContext.addPrimop("local-name", S_D);
	_staticContext.addPrimop("namespace-uri", S_V);
	_staticContext.addPrimop("namespace-uri", S_D);
	_staticContext.addPrimop("substring", S_SR);
	_staticContext.addPrimop("substring", S_SRR);
	_staticContext.addPrimop("substring-after", S_SS);
	_staticContext.addPrimop("substring-before", S_SS);
	_staticContext.addPrimop("normalize-space", S_V);
	_staticContext.addPrimop("normalize-space", S_S);
	_staticContext.addPrimop("system-property", S_S);

	// Extensions
        _staticContext.addPrimop("nodeset", D_O);
        _staticContext.addPrimop("objectType", S_O);

	// Operators +, -, *, /, % defined on real types.
	_staticContext.addPrimop("+", R_RR);
	_staticContext.addPrimop("-", R_RR);
	_staticContext.addPrimop("*", R_RR);
	_staticContext.addPrimop("/", R_RR);
	_staticContext.addPrimop("%", R_RR);

	// Operators +, -, * defined on integer types.
	// Operators / and % are not  defined on integers (may cause exception)
	_staticContext.addPrimop("+", I_II);
	_staticContext.addPrimop("-", I_II);
	_staticContext.addPrimop("*", I_II);

	 // Operators <, <= >, >= defined on real types.
	_staticContext.addPrimop("<",  B_RR);
	_staticContext.addPrimop("<=", B_RR);
	_staticContext.addPrimop(">",  B_RR);
	_staticContext.addPrimop(">=", B_RR);

	// Operators <, <= >, >= defined on int types.
	_staticContext.addPrimop("<",  B_II);
	_staticContext.addPrimop("<=", B_II);
	_staticContext.addPrimop(">",  B_II);
	_staticContext.addPrimop(">=", B_II);

	// Operators <, <= >, >= defined on boolean types.
	_staticContext.addPrimop("<",  B_BB);
	_staticContext.addPrimop("<=", B_BB);
	_staticContext.addPrimop(">",  B_BB);
	_staticContext.addPrimop(">=", B_BB);

	// Operators 'and' and 'or'.
	_staticContext.addPrimop("or", B_BB);
	_staticContext.addPrimop("and", B_BB);

	// Unary minus.
	_staticContext.addPrimop("u-", R_R);
	_staticContext.addPrimop("u-", I_I);
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

    private boolean versionIsOne = true;

    public SyntaxTreeNode makeInstance(String uri, String prefix,
	String local, Attributes attributes)
    {
	SyntaxTreeNode node = null;
	QName qname = getQName(uri, prefix, local);
	String className = (String)_instructionClasses.get(qname);

	if (className != null) {
	    try {
		final Class clazz = Class.forName(className);
		node = (SyntaxTreeNode)clazz.newInstance();
		node.setQName(qname);
		if (_locator != null) {
		    node.setLineNumber(_locator.getLineNumber());
		}
		if (node instanceof Stylesheet) {
		    _xsltc.setStylesheet((Stylesheet)node);
		}
		checkForSuperfluousAttributes(node, attributes);
	    }
	    catch (ClassNotFoundException e) {
		ErrorMsg err = new ErrorMsg(ErrorMsg.CLASS_NOT_FOUND_ERR, node);
		reportError(ERROR, err);
	    }
	    catch (Exception e) {
		ErrorMsg err = new ErrorMsg(ErrorMsg.INTERNAL_ERR,
					    e.getMessage(), node);
		reportError(FATAL, err);
	    }
	}
	else {
	    // Check if the element belongs in our namespace
	    if (uri.equals(XSLT_URI)) {
		node = new UnsupportedElement(uri, prefix, local);
		UnsupportedElement element = (UnsupportedElement)node;
		ErrorMsg msg = new ErrorMsg(ErrorMsg.UNSUPPORTED_XSL_ERR,
					    _locator.getLineNumber(),local);
		element.setErrorMessage(msg);
	    }
	    // Check if this is an XSLTC extension element
	    else if (uri.equals(TRANSLET_URI)) {
		node = new UnsupportedElement(uri, prefix, local);
		UnsupportedElement element = (UnsupportedElement)node;
		ErrorMsg msg = new ErrorMsg(ErrorMsg.UNSUPPORTED_EXT_ERR,
					    _locator.getLineNumber(),local);
		element.setErrorMessage(msg);
	    }
	    // Check if this is an extension of some other XSLT processor
	    else {
		// TODO: check if stack is empty
                SyntaxTreeNode parent = (SyntaxTreeNode)_parentStack.peek();
		StaticContext scontext = parent.getStaticContext();

		// Check parent's static context for extension URI
		if (scontext.getExtensionElementURI(uri)) {
		    // TODO
		    throw new ToDoException("do something with extension!");
		}
	    }

            // If unrecognized then assume literal
	    if (node == null) {
                node = new LiteralElement();
                ((LiteralElement) node).setQName(qname);
	    }
	}

	return node;
    }

    /**
     * checks the list of attributes against a list of allowed attributes
     * for a particular element node.
     * TODO: change the implementation of this method to use the version
     * stored in each SyntaxTreeNode.
     */
    private void checkForSuperfluousAttributes(SyntaxTreeNode node,
	Attributes attrs)
    {
	QName qname = node.getQName();
	boolean isStylesheet = (node instanceof Stylesheet);
        String[] legal = (String[]) _instructionAttrs.get(qname);

	if (versionIsOne && legal != null) {
	    int j;
	    final int n = attrs.getLength();

	    for (int i = 0; i < n; i++) {
	        final String attrQName = attrs.getQName(i);

	        if (isStylesheet && attrQName.equals("version")) {
	            versionIsOne = attrs.getValue(i).equals("1.0");
	        }

		// Ignore if special or if it has a prefix
	        if (attrQName.startsWith("xml") ||
		    attrQName.indexOf(':') > 0) continue;

	        for (j = 0; j < legal.length; j++) {
	            if (attrQName.equalsIgnoreCase(legal[j])) {
		        break;
		    }
	        }
	        if (j == legal.length) {
	            final ErrorMsg err =
		        new ErrorMsg(ErrorMsg.ILLEGAL_ATTRIBUTE_ERR,
				attrQName, node);
		    reportError(WARNING, err);
	        }
	    }
        }
    }

    /**
     * Parse an XPath expression:
     *  @parent - XSL element where the expression occured
     *  @exp    - textual representation of the expression
     */
    public Expression parseExpression(SyntaxTreeNode parent, String exp) {
	return (Expression)parseTopLevel(parent, "<EXPRESSION>"+exp, null);
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
        return (Expression)parseTopLevel(parent, "<EXPRESSION>"+exp, exp);
    }

    /**
     * Parse an XPath pattern:
     *  @parent - XSL element where the pattern occured
     *  @exp    - textual representation of the pattern
     */
    public Pattern parsePattern(SyntaxTreeNode parent, String pattern) {
	return (Pattern)parseTopLevel(parent, "<PATTERN>"+pattern, pattern);
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
        return (Pattern)parseTopLevel(parent, "<PATTERN>"+pattern, pattern);
    }

    /**
     * Parse an XPath expression or pattern using the generated XPathParser
     * The method will return a Dummy node if the XPath parser fails.
     */
    private SyntaxTreeNode parseTopLevel(SyntaxTreeNode parent, String text,
					 String expression) {
	int line = 0;
	if (_locator != null) line = _locator.getLineNumber();

	try {
	    _xpathParser.setScanner(new XPathLexer(new StringReader(text)));
	    Symbol result = _xpathParser.parse(expression, line);
	    if (result != null) {
		final SyntaxTreeNode node = (SyntaxTreeNode)result.value;
		if (node != null) {
		    node.setParent(parent);
		    node.setLineNumber(line);
// System.out.println("e = " + text + " " + node);
		    return node;
		}
	    }
	    reportError(ERROR, new ErrorMsg(ErrorMsg.XPATH_PARSER_ERR,
					    expression, parent));
	}
	catch (Exception e) {
	    if (_xsltc.getDebug()) e.printStackTrace();
	    reportError(ERROR, new ErrorMsg(ErrorMsg.XPATH_PARSER_ERR,
					    expression, parent));
	}

	// Return a dummy pattern (which is an expression)
        return SyntaxTreeNode.Dummy;
    }

    // -- Error Handling -------------------------------------------------

    /**
     * Returns true if there were any errors during compilation
     */
    public boolean errorsFound() {
	return _errors.size() > 0;
    }

    /**
     * Prints all compile-time errors
     */
    public void printErrors() {
	final int size = _errors.size();
	if (size > 0) {
	    System.err.println(ErrorMsg.getCompileErrorMessage());
	    for (int i = 0; i < size; i++) {
		System.err.println("  " + _errors.get(i));
	    }
	}
    }

    /**
     * Prints all compile-time warnings
     */
    public void printWarnings() {
	final int size = _warnings.size();
	if (size > 0) {
	    System.err.println(ErrorMsg.getCompileWarningMessage());
	    for (int i = 0; i < size; i++) {
		System.err.println("  " + _warnings.get(i));
	    }
	}
    }

    /**
     * Common error/warning message handler
     */
    public void reportError(final int category, final ErrorMsg error) {
	switch (category) {
	case Constants.INTERNAL:
	    // Unexpected internal errors, such as null-ptr exceptions, etc.
	    // Immediately terminates compilation, no translet produced
	    _errors.add(error);
	    break;
	case Constants.UNSUPPORTED:
	    // XSLT elements that are not implemented and unsupported ext.
	    // Immediately terminates compilation, no translet produced
	    _errors.add(error);
	    break;
	case Constants.FATAL:
	    // Fatal error in the stylesheet input (parsing or content)
	    // Immediately terminates compilation, no translet produced
	    _errors.add(error);
	    break;
	case Constants.ERROR:
	    // Other error in the stylesheet input (parsing or content)
	    // Does not terminate compilation, no translet produced
	    _errors.add(error);
	    break;
	case Constants.WARNING:
	    // Other error in the stylesheet input (content errors only)
	    // Does not terminate compilation, a translet is produced
	    _warnings.add(error);
	    break;
	}
    }

    public ArrayList getErrors() {
	return _errors;
    }

    public ArrayList getWarnings() {
	return _warnings;
    }

    // -- QName creation -------------------------------------------------

    public QName getQName(final String stringRep) {
        return getQName(stringRep, true, false);
    }

    public QName getQNameIgnoreDefaultNs(final String stringRep) {
        return getQName(stringRep, true, true);
    }

    public QName getQName(final String stringRep, boolean reportError) {
        return getQName(stringRep, reportError, false);
    }

    private QName getQName(final String stringRep, boolean reportError,
        boolean ignoreDefaultNs)
    {
        // parse and retrieve namespace
        final int colon = stringRep.lastIndexOf(':');
        if (colon != -1) {
            String namespace = "";
            String prefix = stringRep.substring(0, colon);
            String localname = stringRep.substring(colon + 1);

            // Get the namespace uri from the symbol table
            if (!prefix.equals(XMLNS_PREFIX)) {
                namespace = _staticContext.getNamespace(prefix);
                if (namespace == null && reportError) {
                    final int line = _locator.getLineNumber();
                    ErrorMsg err = new ErrorMsg(ErrorMsg.NAMESPACE_UNDEF_ERR,
                                                line, prefix);
                    reportError(ERROR, err);
                }
            }
            return getQName(namespace, prefix, localname);
        }
        else {
            if (stringRep.equals(XMLNS_PREFIX)) {
                ignoreDefaultNs = true;
            }
            return getQName(ignoreDefaultNs ? "" :
                _staticContext.getNamespace(""), "", stringRep);
        }
    }

    public QName getQName(String namespace, String prefix, String localname) {
        return new QName(namespace, prefix, localname);
    }

    // -- SAX2 ContentHandler implementation -----------------------------

    /**
     * Stack of ancestors in reverse document order.
     */
    private Stack _parentStack = null;

    /**
     * Prefix mapping for each element node defined. This mapping is
     * set by calling SyntaxTreeNode.setPrefixMapping().
     */
    private HashMap _prefixMapping = null;

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
    public void endDocument() {
    }

    /**
     * SAX2: Begin the scope of a prefix-URI Namespace mapping.
     *       This has to be passed on to the symbol table!
     */
    public void startPrefixMapping(String prefix, String uri) {
	if (_prefixMapping == null) {
	    _prefixMapping = new HashMap();
	}
	_prefixMapping.put(prefix, uri);
    }

    /**
     * SAX2: End the scope of a prefix-URI Namespace mapping.
     *       This has to be passed on to the symbol table!
     */
    public void endPrefixMapping(String prefix) {
    }

    /**
     * SAX2: Receive notification of the beginning of an element.
     *       The parser may re-use the attribute list that we're passed so
     *       we clone the attributes in our own Attributes implementation
     */
    public void startElement(String uri, String localname,
	String qname, Attributes attributes) throws SAXException
    {
	final int col = qname.lastIndexOf(':');
	final String prefix = (col == -1) ? "" : qname.substring(0, col);
	SyntaxTreeNode parent = (SyntaxTreeNode)_parentStack.peek();

	// Unqualified names cannot be global
	if (uri.length() == 0 && parent instanceof Stylesheet) {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.ELEMENT_PARSE_ERR, qname);
	    throw new SAXException(err.toString());
	}

	// Create AST node based on element qname
	SyntaxTreeNode element = makeInstance(uri, prefix, localname,
            attributes);
	if (element == null) {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.ELEMENT_PARSE_ERR, qname);
	    throw new SAXException(err.toString());
	}

	// If this is the root element of the XML document we need to
        // make sure that it contains a definition of the XSL namespace URI
	if (_root == null) {
	    if (_prefixMapping == null ||
		_prefixMapping.containsValue(Constants.XSLT_URI) == false) {
		_rootNamespaceDef = false;
	    }
	    else {
		_rootNamespaceDef = true;
	    }
	    _root = element;
	}
	else {
	    parent.add(element);
	    element.setParent(parent);
	}

        // Set NS declaration - must be set before attributes
        element.setPrefixMapping(_prefixMapping);

	// Set attributes (processes standard attributes too)
	element.setAttributes(new AttributesImpl(attributes));

        // Reset NS mapping
	_prefixMapping = null;

        // Push element to the parent's stack
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

	if (string.length() == 0) return;

	// If this text occurs within an <xsl:text> element we append it
	// as-is to the existing text element
	if (parent instanceof Text) {
	    ((Text)parent).setText(string);
	    return;
	}

	// Ignore text nodes that occur directly under <xsl:stylesheet>
        // TODO: report error if this text node contains non-whitespace
	if (parent instanceof Stylesheet) return;

        // Get last element of parent
        int n = parent.elementCount();
	SyntaxTreeNode bro = (n > 0) ? parent.get(n - 1) : null;
	if (bro != null && bro instanceof Text) {
	    Text text = (Text) bro;
	    if (!text.isTextElement()) {
		if (length > 1 || ((int) ch[0]) < 0x100) {
		    text.setText(string);
		    return;
		}
	    }
	}

	// Add it as a regular text node otherwise
	parent.add(new Text(string));
    }

    private String getTokenValue(String token) {
	final int start = token.indexOf('"');
	final int stop = token.lastIndexOf('"');
	return token.substring(start+1, stop);
    }

    /**
     * SAX2: Receive notification of a processing instruction.
     *       These require special handling for stylesheet PIs.
     */
    public void processingInstruction(String name, String value) {
	// We only handle the <?xml-stylesheet ...?> PI
	if (_target == null && name.equals("xml-stylesheet")) {
	    String href = null;    // URI of stylesheet found
	    String media = null;   // Media of stylesheet found
	    String title = null;   // Title of stylesheet found
	    String charset = null; // Charset of stylesheet found

	    // Get the attributes from the processing instruction
	    StringTokenizer tokens = new StringTokenizer(value);
	    while (tokens.hasMoreTokens()) {
		String token = (String)tokens.nextToken();
		if (token.startsWith("href")) {
		    href = getTokenValue(token);
		}
		else if (token.startsWith("media")) {
		    media = getTokenValue(token);
		}
		else if (token.startsWith("title")) {
		    title = getTokenValue(token);
		}
		else if (token.startsWith("charset")) {
		    charset = getTokenValue(token);
		}
	    }

	    // Set the target to this PI's href if the parameters are
	    // null or match the corresponding attributes of this PI.
	    if ((_PImedia == null || _PImedia.equals(media)) &&
		(_PItitle == null || _PImedia.equals(title)) &&
		(_PIcharset == null || _PImedia.equals(charset)))
            {
		_target = href;
	    }
	}
    }

    /**
     * IGNORED - all ignorable whitespace is ignored
     */
    public void ignorableWhitespace(char[] ch, int start, int length) {
    }

    /**
     * IGNORED - we do not have to do anything with skipped entities
     */
    public void skippedEntity(String name) {
    }
}
