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
import java.util.Vector;
import java.util.Hashtable;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Date;
import java.util.Map;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.jar.*;

import org.xml.sax.*;

import javax.xml.parsers.*;
import javax.xml.transform.ErrorListener;

import org.apache.xalan.xsltc.compiler.util.*;
import org.apache.xalan.xsltc.cmdline.getopt.*;
import org.apache.xalan.xsltc.DOM;
import de.fub.bytecode.classfile.JavaClass;

public final class XSLTC {

    // A reference to the main parsergobject.
    private final Parser _parser;
    
    // A reference to the stylesheet being compiled.
    private Stylesheet _stylesheet = null;

    // Counters used by various classes to generate unique names.
    private int _variableSerial     = 1;
    private int _modeSerial         = 1;
    private int _stylesheetSerial   = 1;
    private int _stepPatternSerial  = 1;
    private int _helperClassSerial  = 0;
    private int _attributeSetSerial = 0;
    
    private int[] _numberFieldIndexes;
    
    // Name index tables
    private int       _nextGType;  // Next available element type
    private Vector    _namesIndex; // Index of all registered QNames
    private Hashtable _elements;   // Hashtable of all registered elements
    private Hashtable _attributes; // Hashtable of all registered attributes

    // Namespace index tables
    private int       _nextNSType; // Next available namespace type
    private Vector    _namespaceIndex; // Index of all registered namespaces
    private Hashtable _namespaces; // Hashtable of all registered namespaces

    // These define the various methods for outputting the translet
    private static final int FILE_OUTPUT        = 0;
    private static final int JAR_OUTPUT         = 1;
    private static final int BYTEARRAY_OUTPUT   = 2;
    private static final int CLASSLOADER_OUTPUT = 3;

    // Compiler options (passed from command line or XSLTC client)
    private boolean _debug = false;      // -x
    private String  _jarFileName = null; // -j <jar-file-name>
    private String  _className = null;   // -o <class-name>
    private String  _packageName = null; // -p <package-name>
    private File    _destDir = null;     // -d <directory-name>
    private int     _outputType = FILE_OUTPUT; // by default

    private Vector  _classes;
    private boolean _multiDocument = false;
    
    /**
     * XSLTC compiler constructor
     */
    public XSLTC() {
	_parser = new Parser(this);
    }

    /**
     * Initializes the compiler to compile a new stylesheet
     */
    public void init() {
	reset();
	_classes = new Vector();
    }
    
    /**
     * Initializes the compiler to produce a new translet
     */
    private void reset() {
	_nextGType      = DOM.NTYPES;
	_elements       = new Hashtable();
	_attributes     = new Hashtable();
	_namespaces     = new Hashtable();
	_namespaces.put("",new Integer(_nextNSType));
	_namesIndex     = new Vector(128);
	_namespaceIndex = new Vector(32);
	_parser.init();
	_variableSerial     = 1;
	_modeSerial         = 1;
	_stylesheetSerial   = 1;
	_stepPatternSerial  = 1;
	_helperClassSerial  = 0;
	_attributeSetSerial = 0;
	_multiDocument      = false;
	_numberFieldIndexes = new int[] {
	    -1, 	// LEVEL_SINGLE
	    -1, 	// LEVEL_MULTIPLE
	    -1		// LEVEL_ANY
	};
    }
    
    /**
     * Compiles an XSL stylesheet pointed to by a URL
     */
    public boolean compile(URL url) { 
	return compile(url, (ErrorListener)null);
    }

    /**
     * Compiles an XSL stylesheet pointed to by a URL
     *   @listener parameter may be null
     */
    public boolean compile(URL url, ErrorListener listener) {
	try {
	    // Open input stream from URL
	    final InputStream input = url.openStream();
	    // Get class name from URL if not explicitly set
	    if (_className == null) {
		final String name = Util.baseName(url.getFile());
		return compile(input, name, url, listener);
	    }
	    else {
		return compile(input, _className, url, listener);
	    }
	}
	catch (MalformedURLException e) {
	    _parser.reportError(Constants.FATAL, new ErrorMsg(e.getMessage()));
	    _parser.printErrors();
	    return false;
	}
	catch (IOException e) {
	    _parser.reportError(Constants.FATAL, new ErrorMsg(e.getMessage()));
	    _parser.printErrors();
	    return false;
	}
    }

    /**
     * Compiles an XSL stylesheet passed in through an InputStream
     *   @transletName parameter must be set since the compiler cannot get
     *   the XSL stylesheet name from the input stream
     */
    public boolean compile(InputStream input, String transletName) {
	return compile(input, transletName, null, null);
    }

    /**
     * Compiles an XSL stylesheet passed in through an InputStream
     *   @input passes the stylesheet to the compiler
     *   @transletName may be set, name taken from URL if this param is null
     *   @url parameter must be set
     *   @listener parameter may be null
     */
    public boolean compile(InputStream input,
			   String transletName,
			   URL url,
			   ErrorListener listener) {

	// Set the parser's error listener if defined
	if (listener != null) _parser.setErrorListener(listener);

	try {
	    // Reset globals in case we're called by compile(Vector v);
	    reset();

	    // Set the translet's class name
	    if (transletName == null) 
		transletName = Util.baseName(url.getFile());
	    setClassName(transletName);

	    // Get the root node of the abstract syntax tree
	    final SyntaxTreeNode element = _parser.parse(input);

	    // Compile the translet - this is where the work is done!
	    if ((!_parser.errorsFound()) && (element != null)) {
		// Create a Stylesheet element from the root node
		_stylesheet = _parser.makeStylesheet(element);
		_stylesheet.setURL(url);
		_stylesheet.setParentStylesheet(null);
		_parser.setCurrentStylesheet(_stylesheet);
		// Create AST under the Stylesheet element (parse & type-check)
		_parser.createAST(_stylesheet);
	    }
	    // Generate the bytecodes and output the translet class(es)
	    if ((!_parser.errorsFound()) && (_stylesheet != null)) {
		_stylesheet.setMultiDocument(_multiDocument);
		_stylesheet.translate();
	    }
	}
	catch (CompilerException e) {
	    _parser.reportError(Constants.FATAL, new ErrorMsg(e.getMessage()));
	}
	finally {
	    _parser.printErrors();
	    _parser.printWarnings();
	    return !_parser.errorsFound();
	}
    }

    /**
     * Compiles a set of stylesheets pointed to by a Vector of URLs
     */
    public boolean compile(Vector stylesheets) {
	// Get the number of stylesheets (ie. URLs) in the vector
	final int count = stylesheets.size();
	
	// Return straight away if the vector is empty
	if (count == 0) return true;

	// Special handling needed if the URL count is one, becuase the
	// _className global must not be reset if it was set explicitly
	if (count == 1) {
	    final Object url = stylesheets.firstElement();
	    if (url instanceof URL)
		return compile((URL)url);
	    else
		return false;
	}

	// Traverse all elements in the vector and compile
	final Enumeration urls = stylesheets.elements();
	while (urls.hasMoreElements()) {
	    _className = null; // reset, so that new name will be computed 
	    final Object url = urls.nextElement();
	    if (url instanceof URL) {
		if (!compile((URL)url)) return false;
	    }
	}
	return true;
    }

    /**
     * Compiles a stylesheet pointed to by a URL. The result is put in a
     * set of byte arrays. One byte array for each generated class.
     */
    public byte[][] compile(URL stylesheetURL, String className) {
	_outputType = BYTEARRAY_OUTPUT;
	setClassName(className);
	if (compile(stylesheetURL)) {
	    final int count = _classes.size();
	    final byte[][] result = new byte[1][count];
	    for (int i = 0; i < count; i++)
		result[i] = (byte[])_classes.elementAt(i);
	    return result;
	}
	return null;
    }

    /**
     * Compiles a stylesheet pointed to by a URL. The result is put in a
     * set of byte arrays. One byte array for each generated class.
     */
    public byte[][] compile(InputStream source, String className, int dummy) {
	_outputType = BYTEARRAY_OUTPUT;
	setClassName(className);
	if (compile(source, className)) {
	    final int count = _classes.size();
	    final byte[][] result = new byte[1][count];
	    for (int i = 0; i < count; i++)
		result[i] = (byte[])_classes.elementAt(i);
	    return result;
	}
	return null;
    }

    /**
     * This method is called by the XPathParser when it encounters a call
     * to the document() function. Affects the DOM used by the translet.
     */
    public void setMultiDocument(boolean flag) {
	_multiDocument = flag;
    }

    /**
     * Set the class name for the generated translet. This class name is
     * overridden if multiple stylesheets are compiled in one go using the
     * compile(Vector urls) method.
     */
    public void setClassName(String className) {
	final String base  = Util.baseName(className);
	final String noext = Util.noExtName(base); 
	final String name  = Util.toJavaName(noext);
	if (_packageName == null)
	    _className = name;
	else
	    _className = _packageName + '.' + name;
    }
    
    /**
     * Get the class name for the generated translet.
     */
    public String getClassName() {
	return _className;
    }

    /**
     * Convert for Java class name of local system file name.
     * (Replace '.' with '/' on UNIX and replace '.' by '\' on Windows/DOS.)
     */
    private String classFileName(final String className) {
	return className.replace('.', File.separatorChar) + ".class";
    }
    
    /**
     * Generate an output File object to send the translet to
     */
    private File getOutputFile(String className) {
	if (_destDir != null)
	    return new File(_destDir, classFileName(className));
	else
	    return new File(classFileName(className));
    }

    /**
     * Set the destination directory for the translet.
     * The current working directory will be used by default.
     */
    public boolean setDestDirectory(String dstDirName) {
	final File dir = new File(dstDirName);
	if (dir.exists() || dir.mkdirs()) {
	    _destDir = dir;
	    return true;
	}
	else {
	    _destDir = null;
	    return false;
	}
    }

    /**
     * Set an optional package name for the translet and auxiliary classes
     */
    public void setPackageName(String packageName) {
	_packageName = packageName;
    }

    /**
     * Set the name of an optional JAR-file to dump the translet and
     * auxiliary classes to
     */
    public void setJarFileName(String jarFileName) {
	final String JAR_EXT = ".jar";
	if (jarFileName.endsWith(JAR_EXT))
	    _jarFileName = jarFileName;
	else
	    _jarFileName = jarFileName + JAR_EXT;
	_outputType = JAR_OUTPUT;
    }

    public String getJarFileName() {
	return _jarFileName;
    }

    /**
     *
     */
    public Stylesheet getStylesheet() {
	return _stylesheet;
    }

    /**
     *
     */
    public void setStylesheet(Stylesheet stylesheet) {
	if (_stylesheet == null)
	    _stylesheet = stylesheet;
    }
   
    /**
     * Registers an attribute and gives it a type so that it can be mapped to
     * DOM attribute types at run-time.
     */
    public int registerAttribute(QName name) {
	Integer code = (Integer)_attributes.get(name);
	if (code == null) {
	    _attributes.put(name, code = new Integer(_nextGType++));
	    final String uri = name.getNamespace();
	    final String local = "@"+name.getLocalPart();
	    if ((uri != null) && (!uri.equals("")))
		_namesIndex.addElement(uri+":"+local);
	    else
		_namesIndex.addElement(local);
	    if (name.getLocalPart().equals("*")) {
		registerNamespace(name.getNamespace());
	    }
	}
	return code.intValue();
    }

    /**
     * Registers an element and gives it a type so that it can be mapped to
     * DOM element types at run-time.
     */
    public int registerElement(QName name) {
	// Register element (full QName)
	Integer code = (Integer)_elements.get(name.toString());
	if (code == null) {
	    _elements.put(name.toString(), code = new Integer(_nextGType++));
	    _namesIndex.addElement(name.toString());
	}
	if (name.getLocalPart().equals("*")) {
	    registerNamespace(name.getNamespace());
	}
	return code.intValue();
    }

    /**
     * Registers a namespace and gives it a type so that it can be mapped to
     * DOM namespace types at run-time.
     */
    public int registerNamespace(String namespaceURI) {
	Integer code = (Integer)_namespaces.get(namespaceURI);
	if (code == null) {
	    code = new Integer(_nextNSType++);
	    _namespaces.put(namespaceURI,code);
	    _namespaceIndex.addElement(namespaceURI);
	}
	return code.intValue();
    }
    
    public int nextVariableSerial() {
	return _variableSerial++;
    }
    
    public int nextModeSerial() {
	return _modeSerial++;
    }

    public int nextStylesheetSerial() {
	return _stylesheetSerial++;
    }

    public int nextStepPatternSerial() {
	return _stepPatternSerial++;
    }

    public int[] getNumberFieldIndexes() {
	return _numberFieldIndexes;
    }

    public int nextHelperClassSerial() {
	return _helperClassSerial++;
    }
    
    public int nextAttributeSetSerial() {
	return _attributeSetSerial++;
    }

    public Vector getNamesIndex() {
	return _namesIndex;
    }

    public Vector getNamespaceIndex() {
	return _namespaceIndex;
    }
    
    /**
     * Returns a unique name for every helper class needed to
     * execute a translet.
     */
    public String getHelperClassName() {
	return getClassName() + '$' + _helperClassSerial++;
    }
   
    public void dumpClass(JavaClass clazz) {
	try {
	    switch (_outputType) {
	    case FILE_OUTPUT:
		clazz.dump(getOutputFile(clazz.getClassName()));
		break;
	    case JAR_OUTPUT:
		_classes.addElement(clazz);	 
		break;
	    case BYTEARRAY_OUTPUT:
	    case CLASSLOADER_OUTPUT:
		ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
		clazz.dump(out);
		_classes.addElement(out.toByteArray());
		break;
	    }
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * File separators are converted to forward slashes for ZIP files.
     */
    private String entryName( File f ) throws IOException {
	return f.getName().replace(File.separatorChar, '/');
    }
    
    /**
     * Generate output JAR-file and packages
     */
    public void outputToJar() throws IOException {
	// create the manifest
	final Manifest manifest = new Manifest();
	manifest.getMainAttributes()
	    .put(java.util.jar.Attributes.Name.MANIFEST_VERSION, "1.0");

	final Map map = manifest.getEntries();
	// create manifest
	Enumeration classes = _classes.elements();
	final String now = (new Date()).toString();
	final java.util.jar.Attributes.Name dateAttr = 
	    new java.util.jar.Attributes.Name("Date");
	while (classes.hasMoreElements()) {
	    final JavaClass clazz = (JavaClass)classes.nextElement();
	    final java.util.jar.Attributes attr = 
		new java.util.jar.Attributes();
	    attr.put(dateAttr, now);
	    map.put(classFileName(clazz.getClassName()), attr);
	}

	final File jarFile = new File(_destDir, _jarFileName);
	final JarOutputStream jos =
	    new JarOutputStream(new FileOutputStream(jarFile), manifest);
	classes = _classes.elements();
	while (classes.hasMoreElements()) {
	    final JavaClass cl = (JavaClass)classes.nextElement();
	    jos.putNextEntry(new JarEntry(classFileName(cl.getClassName())));
	    final ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
	    cl.dump(out);	// dump() closes it's output stream
	    out.writeTo(jos);
	}
	jos.close();
    }

    /**
     * Turn debugging messages on/off
     */
    public void setDebug(boolean debug) {
	_debug = debug;
    }

    /**
     * Get current debugging message setting
     */
    public boolean debug() {
	return _debug;
    }
        
    /** 
     * This method implements the command line compiler. See the USAGE_STRING
     * constant for a description. It may make sense to move the command-line
     * handling to a separate package (ie. make one xsltc.cmdline.Compiler
     * class that contains this main() method and one xsltc.cmdline.Transform
     * class that contains the DefaultRun stuff).
     */
    public static void main(String[] args) {
	try {
	    boolean inputIsURL = false;

	    final GetOpt getopt = new GetOpt(args, "o:d:j:p:uxhs");
	    if (args.length < 1) {
		printUsage();
		return;
	    }

	    final XSLTC xsltc = new XSLTC();
	    xsltc.init();

	    int c;
	    while ((c = getopt.getNextOption()) != -1) {
		switch(c) {
		case 'o':
		    xsltc.setClassName(getopt.getOptionArg());
		    break;
		case 'd':
		    xsltc.setDestDirectory(getopt.getOptionArg());
		    break;
		case 'p':
		    xsltc.setPackageName(getopt.getOptionArg());
		    break;
		case 'j':  
		    xsltc.setJarFileName(getopt.getOptionArg());
		    break;
		case 'x':
		    xsltc.setDebug(true);
		    break;
		case 'u':
		    inputIsURL = true;
		    break;
		case 's':
		    allowSystemExit = false;
		    break;
		case 'h':
		default:
		    printUsage();
		    break; 
		}
	    }

	    // Generate a vector containg URLs for all stylesheets specified
	    final String[] stylesheetNames = getopt.getCmdArgs();
	    final Vector   stylesheetVector = new Vector();
	    for (int i = 0; i < stylesheetNames.length; i++) {
		final String stylesheetName = stylesheetNames[i];
		final URL    stylesheetURL;
		if (inputIsURL) {
		    stylesheetVector.addElement(new URL(stylesheetName));
		}
		else {
		    stylesheetVector.addElement((new File(stylesheetName)).toURL());
		}

	    }

	    // Compile the stylesheet and output class/jar file(s)
	    if (xsltc.compile(stylesheetVector)) {
		if (xsltc.getJarFileName() != null) xsltc.outputToJar();
	    }
	    else {
		Util.println("compilation failed");
		doSystemExit(1); return;
	    }
	}
	catch (GetOptsException ex) {
	    System.err.println(ex);
	    printUsage();
	}
	catch (Exception e) {
	    e.printStackTrace();
	    doSystemExit(1);
	}
    }

    /** If we should call System.exit or not */
    protected static boolean allowSystemExit = true;

    /** Worker method to call System.exit or not */
    protected static void doSystemExit(int retVal) {
        if (allowSystemExit)
            System.exit(retVal);
    }

    private final static String USAGE_STRING =
	"Usage:\n" + 
	"   xsltc [-o <output>] [-d <directory>] [-j <jarfile>]\n"+
	"         [-p <package name>] [-x] [-s] [-u] <stylesheet>... \n\n"+
	"   Where <output> is the name to give the the generated translet.\n"+
	"         <stylesheet> is one or more stylesheet file names, or if,\n"+
	"         the -u options is specified, one or more stylesheet URLs.\n"+
	"         <directory> is the output directory.\n"+
	"         <jarfile> is the name of a JAR-file to put all generated classes in.\n"+
	"         <package-name> is a package name to prefix all class names with.\n\n"+
	"   Notes:\n"+
	"         The -o option is ignored when multiple stylesheets are specified.\n"+
	"         The -x option switched on debug messages.\n"+
	"         The -s option prevents the compiler from exiting\n";
    
    public static void printUsage() {
	System.err.println(USAGE_STRING);
	doSystemExit(1);
	return;
    }
}
