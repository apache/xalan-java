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

package org.apache.xalan.xsltc.cmdline;

import java.io.*;
import java.util.Vector;
import java.net.URL;

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.Translet;
import org.apache.xalan.xsltc.TransletOutputHandler;

import org.apache.xalan.xsltc.compiler.XSLTC;
import org.apache.xalan.xsltc.compiler.util.Util;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.cmdline.getopt.*;

public final class Compile {

    // Versioning numbers  for the compiler -v option output
    private static int VERSION_MAJOR = 1;
    private static int VERSION_MINOR = 2;
    private static int VERSION_DELTA = 0;
 

    // This variable should be set to false to prevent any methods in this 
    // class from calling System.exit(). As this is a command-line tool,
    // calling System.exit() is normally OK, but we also want to allow for
    // this class being used in other ways as well.
    private static boolean _allowExit = true;

    public static void printUsage() {
        StringBuffer vers = new StringBuffer("XSLTC version " + 
	    VERSION_MAJOR + "." + VERSION_MINOR + 
	    ((VERSION_DELTA > 0) ? ("."+VERSION_DELTA) : ("")));
	System.err.println(vers + "\n" + 
		new ErrorMsg(ErrorMsg.COMPILE_USAGE_STR));
	if (_allowExit) System.exit(-1);
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
	    boolean useStdIn = false;
	    boolean classNameSet = false;
	    final GetOpt getopt = new GetOpt(args, "o:d:j:p:uxhsinv");
	    if (args.length < 1) printUsage();

	    final XSLTC xsltc = new XSLTC();
	    xsltc.init();

	    int c;
	    while ((c = getopt.getNextOption()) != -1) {
		switch(c) {
		case 'i':
		    useStdIn = true;
		    break;
		case 'o':
		    xsltc.setClassName(getopt.getOptionArg());
		    classNameSet = true;
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
		    _allowExit = false;
		    break;
		case 'n':
		    xsltc.setTemplateInlining(false);
		    break;
		case 'v':
		    // fall through to case h
		case 'h':
		default:
		    printUsage();
		    break; 
		}
	    }

	    boolean compileOK;

	    if (useStdIn) {
		if (!classNameSet) {
		    System.err.println(new ErrorMsg(ErrorMsg.COMPILE_STDIN_ERR));
		    if (_allowExit) System.exit(-1);
		}
		compileOK = xsltc.compile(System.in, xsltc.getClassName());
	    }
	    else {
		// Generate a vector containg URLs for all stylesheets specified
		final String[] stylesheetNames = getopt.getCmdArgs();
		final Vector   stylesheetVector = new Vector();
		for (int i = 0; i < stylesheetNames.length; i++) {
		    final String name = stylesheetNames[i];
		    URL url;
		    if (inputIsURL)
			url = new URL(name);
		    else
			url = (new File(name)).toURL();
		    stylesheetVector.addElement(url);
		}
		compileOK = xsltc.compile(stylesheetVector);
	    }

	    // Compile the stylesheet and output class/jar file(s)
	    if (compileOK) {
		xsltc.printWarnings();
		if (xsltc.getJarFileName() != null) xsltc.outputToJar();
		if (_allowExit) System.exit(0);
	    }
	    else {
		xsltc.printWarnings();
		xsltc.printErrors();
		if (_allowExit) System.exit(-1);
	    }
	}
	catch (GetOptsException ex) {
	    System.err.println(ex);
	    printUsage(); // exits with code '-1'
	}
	catch (Exception e) {
	    e.printStackTrace();
	    if (_allowExit) System.exit(-1);
	}
    }

}
