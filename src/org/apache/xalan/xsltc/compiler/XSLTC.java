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

import com.sun.xml.tree.*;

import org.apache.xalan.xsltc.compiler.util.*;
import org.apache.xalan.xsltc.util.getopt.*;
import org.apache.xalan.xsltc.DOM;
import de.fub.bytecode.classfile.JavaClass;

public final class XSLTC {

    // A reference to the main parsergobject.
    private final Parser _parser;
    
    // A reference to the stylesheet being compiled.
    private Stylesheet _stylesheet = null;

    // Command line options/args
    private boolean _debug;
    private boolean _isJarFileSpecified;
    private String  _className;			// -o effects this
    private String  _packageName;
    private File    _destDir;
    private File    _dumpDir;

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

    private static final int FILE_OUTPUT        = 0;
    private static final int JAR_OUTPUT         = 1;
    private static final int BYTEARRAY_OUTPUT   = 2;
    private static final int CLASSLOADER_OUTPUT = 3;

    private int _outputType = FILE_OUTPUT; // by default
    private Vector _classes;
    private boolean _multiDocument = false;
    
    public XSLTC() {
	_parser = new Parser(this);
    }

    public void init() {
	reset();
	_classes = new Vector();
    }
    
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
    
    public void setMultiDocument(boolean flag) {
	_multiDocument = flag;
    }
    
    /**
     * Compiles the stylesheet into Java bytecode. Returns 'true' if the
     * compilation was successful - 'false' otherwise.
     */
    public boolean compile(URL stylesheet) {
	try {
	    reset();
	    final String name = stylesheet.getFile();
	    if (_className == null) {
		final String baseName = Util.baseName(name);
		setClassName(Util.toJavaName(Util.noExtName(baseName)));
	    }
	    
	    final ElementEx stylesheetElement = _parser.parse(stylesheet);
	    if (_parser.errorsFound()) {
		_parser.printErrors();
		return false;
	    }
	
	    _parser.printWarnings();

	    /* REMOVED: The first element is not necessarily in the XSL
	                namespace if we're compiling a simplified stylesheet
	    final String namespace = stylesheetElement.getNamespace();
	    // Does it start with an element of the xsl namespace ?
	    if (namespace != null && !namespace.equals(Constants.XSLT_URI)) {
	    	_parser.addError(new ErrorMsg(ErrorMsg.STYORTRA_ERR));
	    }
	    */
	    if ((!_parser.errorsFound()) && (stylesheetElement != null)) {
		_stylesheet = _parser.makeStylesheet(stylesheetElement);
		_stylesheet.setURL(stylesheet);
		// This is the top level stylesheet - it has no parent
		_stylesheet.setParentStylesheet(null);
		_parser.setCurrentStylesheet(_stylesheet);
		_parser.createAST(stylesheetElement, _stylesheet);
		if (_stylesheet != null && _parser.errorsFound() == false) {
		    _stylesheet.setMultiDocument(_multiDocument);
		    _stylesheet.translate();
		}
		else {
		    _parser.printErrors();
		}		
	    }
	    else {
		_parser.printErrors();
	    }
	    _parser.printWarnings();
	    return !_parser.errorsFound();
	}
	catch (CompilerException e) {
	    _parser.addError(new ErrorMsg(e.getMessage()));
	    _parser.printErrors();
	    return false;	    
	}
    }
    
    private boolean compile(Vector stylesheets) {
	final int nStylesheets = stylesheets.size();
	/*
	 * Note: if there are multiple stylesheets, then '_className' must
	 *       be reset to null each time; otherwise it should not be
	 *       reset because user might have specified a new name with the
	 *       -o <name> option.
	 */
	if (nStylesheets > 1) {
	    final Enumeration urls = stylesheets.elements();
	    while (urls.hasMoreElements()) {
		final URL stylesheetURL = (URL)urls.nextElement();
		_className = null; // reset, so that new name will be computed 
		if (!compile(stylesheetURL)) {
		    return false;
		}
	    }
	    return true;
	}
	else {
	    final URL stylesheetURL = (URL)stylesheets.firstElement();
	    // do not reset _className to null in case -o option was used.
	    return compile(stylesheetURL);
	}
    }
    
    private void setClassName(String className)  {
	final String name =
	    Util.toJavaName(Util.noExtName(Util.baseName(className)));
	_className = (_packageName != null) ? _packageName + '.' + name : name;
    }
    
    public String getClassName() {
	return _className;
    }

    private void setDebug(boolean debug) {
	_debug = debug;
    }
    
    private void setDestDirectory(String dstDirName) throws CompilerException {
	final File dir = new File(dstDirName);
	if (dir.exists() || dir.mkdirs()) {
	    _destDir = dir;
	}
	else {
	    throw new CompilerException("Could not create output directory");
	}
    }

    private void setPackageName(String packageName) {
	_packageName = packageName;
    }

    private void setJarFileSpecified(boolean value) {
	if (_isJarFileSpecified = value) {
	    _outputType = JAR_OUTPUT;
	}
    }
    
    public Stylesheet getStylesheet() {
	return _stylesheet;
    }

    private File getOutputFile(String className) {
	return new File(_dumpDir != null ? _dumpDir : _destDir,
			classFileName(className));
    }
   
    private String classFileName(final String className) {
	return className.replace('.', File.separatorChar) + ".class";
    }

    public boolean debug() {
	return _debug;
    }

    public Vector getNamesIndex() {
	return _namesIndex;
    }

    public Vector getNamespaceIndex() {
	return _namespaceIndex;
    }

    /**
     * Registers an attribute and gives it a type so that it can be mapped to
     * DOM attribute types at run-time.
     */
    public int registerAttribute(QName name) {
	Integer code = (Integer)_attributes.get(name);
	if (code == null) {
	    _attributes.put(name, code = new Integer(_nextGType++));
	    //_namesIndex.addElement("@" + name.toString());
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
    
    /**
     *	Aborts the execution of the compiler as a result of an 
     *  unrecoverable error.
     */
    public void internalError() {
	System.err.println("Internal error");
	if (debug()) {
	    _parser.errorsFound(); // print stack
	}
	doSystemExit(1); throw new RuntimeException("System.exit(1) here!");
    }

    /**
     * Aborts the execution of the compiler if something found in the source 
     * file can't be compiled. It also prints which feature is not implemented 
     * if specified.
     */
    public void notYetImplemented(String feature) {
	System.err.println("'"+feature+"' is not supported by XSLTC.");
	if (debug()) {
	    _parser.errorsFound(); // print stack
	}
	doSystemExit(1); throw new RuntimeException("System.exit(1) here!");
    }

    /**
     * Aborts the execution of the compiler if something found in the source 
     * file can't be compiled. It also prints which feature is not implemented 
     * if specified.
     */
    public void extensionNotSupported(String feature) {
	System.err.println("Extension element '"+feature+
			   "' is not supported by XSLTC.");
	if (debug()) {
	    _parser.errorsFound(); // print stack
	}
	doSystemExit(1); throw new RuntimeException("System.exit(1) here!");
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

    private byte[][] outputToArrays() {
	final int nClasses = _classes.size();
	final byte[][] result = new byte[1][nClasses];
	for (int i = 0; i < nClasses; i++) {
	    result[i] = (byte[])_classes.elementAt(i);
	}
	return result;
    }

    /**
     * File separators are converted to forward slashes for ZIP files.
     */
    private String entryName( File f ) throws IOException {
	return f.getName().replace(File.separatorChar, '/');
    }
    
    /**
     * Jar and packages
     */
    private void outputToJar(String jarFileName) throws IOException {
	// create the manifest
	final Manifest manifest = new Manifest();
	manifest.getMainAttributes()
	    .put(Attributes.Name.MANIFEST_VERSION, "1.0");

	final Map map = manifest.getEntries();
	// create manifest
	Enumeration classes = _classes.elements();
	final String now = (new Date()).toString();
	final Attributes.Name dateAttr = new Attributes.Name("Date");
	while (classes.hasMoreElements()) {
	    final JavaClass clazz = (JavaClass)classes.nextElement();
	    final Attributes attr = new Attributes();
	    attr.put(dateAttr, now);
	    map.put(classFileName(clazz.getClassName()), attr);
	}

	final File jarFile = new File(_destDir, jarFileName+".jar");
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
    
    public byte[][] compileStylesheet(URL stylesheetURL, String className) {
	_outputType = BYTEARRAY_OUTPUT;
	setClassName(className);
	return compile(stylesheetURL) ? outputToArrays() : null;
    }

    /** 
     * Command line runnability.
     * o className
     * d destDirectory
     * p packageName
     * j jarFileName
     * u (isUriSpecified)
     * x (isDebugSpecified)
     * h printUsage()
     * s (don't allow System.exit)
     */
    public static void main(String[] args) {
	try {
	    final GetOpt getopt = new GetOpt(args, "o:d:j:p:uxhs");
	    if (args.length < 1) {
		printUsage();
		doSystemExit(1); return;
	    } 
	    boolean isUriSpecified = false;
	    boolean isDebugSpecified = false;
	    boolean isJarFileSpecified = false;
	    String jarFileName = null;
	    String destDirectory = "."; // cwd by default
	    String packageName = null;
	    String className = null;
	    int c;
	    while ((c = getopt.getNextOption()) != -1) {
		switch(c) {
		case 'o':
		    className = getopt.getOptionArg();
		    break;
		case 'd':
		    destDirectory = getopt.getOptionArg();
		    break;
		case 'p':
		    packageName = getopt.getOptionArg(); 
		    break;
		case 'j':  
		    jarFileName = getopt.getOptionArg();
		    isJarFileSpecified = true;
		    break;
		case 'u':  
		    isUriSpecified = true;
		    break;
		case 'x':
		    isDebugSpecified = true;
		    break;
		case 's':
		    allowSystemExit = false;
		    break;
		case 'h':
		    printUsage();
		    break;
		default:
		    printUsage();
		    break; 
		}
	    }

	    final String[] stylesheets = getopt.getCmdArgs();
	    final int nStyleSheets = stylesheets.length;

	    File dir = new File(destDirectory);

	    if (!dir.isDirectory() || (className != null && nStyleSheets > 1)) {
		printUsage();
		doSystemExit(1); return;
	    }

	    dir = null;
	
	    final XSLTC xsltc = new XSLTC();
	    xsltc.init();
	    xsltc.setDebug(isDebugSpecified);
	    xsltc.setPackageName(packageName);
	    xsltc.setDestDirectory(destDirectory);
	    xsltc.setJarFileSpecified(isJarFileSpecified);
	    if (className != null) {
		xsltc.setClassName(className);
	    }
	    final Vector stylesheetVector = new Vector();
	    for (int i = 0; i < nStyleSheets; i++) {
		final String currStyleSheetName = stylesheets[i];
		final URL stylesheetURL;
		if (isUriSpecified) {
		    stylesheetURL = new URL(currStyleSheetName);
		}
		else {
		    File stylesheetFile = new File(currStyleSheetName);
		    stylesheetURL = stylesheetFile.toURL();
		}
		stylesheetVector.addElement(stylesheetURL);
	    }
	    final long startTime = System.currentTimeMillis();
	    if (xsltc.compile(stylesheetVector)) {
		if (isJarFileSpecified) {
		    xsltc.outputToJar(jarFileName);
		}
		if (isDebugSpecified) {
		    Util.println("compile time " +
				 (System.currentTimeMillis() - startTime) +
				 " msec");
		}
	    }
	    else {
		Util.println("compilation failed");
		doSystemExit(1); return;
	    }
	}
	catch (GetOptsException ex) {
	    System.err.println(ex);
	    printUsage();
	    doSystemExit(1); return;
	}
	catch (Exception e) {
	    e.printStackTrace();
	    doSystemExit(1); return;
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
	"   xsltc [-o <output>] [-d <directory>] [-j <jarfile>]\n" +
	"         [-p <package name>] \n" +
	"         [-u]  <stylesheet>... \n\n" +
	"   where <stylesheet> is a file or if -u option is used, \n" +
	"         is a URL such as http://myserver/stylesheet1.xsl.\n"+
	"         <jarfile> is the name of jar file, do not specify \n" +
	"         the .jar extension. Example: -j MyJar \n"+
	"   Note: the -o option should not be used when processing\n"+
	"         multiple stylesheets. \n"+
	"   also: [-x] (debug), [-s] (don't allow System.exit)";
    
    public static void printUsage() {
	System.err.println(USAGE_STRING);
    }
}
