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
 * @author Morten Jorgensen
 *
 */

package org.apache.xalan.xsltc.trax;

import javax.xml.transform.*;

import org.apache.xalan.xsltc.Translet;
import org.apache.xalan.xsltc.compiler.*;
import org.apache.xalan.xsltc.runtime.*;

public abstract class TemplatesImpl implements Templates {
    
    private String   _transletName = null;
    private byte[][] _bytecodes = null;
    
    // Our own private class loader - builds Class definitions from bytecodes
    private class TransletClassLoader extends ClassLoader {
	public Class defineClass(byte[] b) {
	    return super.defineClass(null, b, 0, b.length);
	}
    }

    /**
     * The TransformerFactory must pass us the translet bytecodes using this
     * method before we can create any translet instances
     */
    public void setTransletBytecodes(byte[][] bytecodes) {
	_bytecodes = bytecodes;
    }

    public byte[][] getTransletBytecodes() {
	return(_bytecodes);
    }

    /**
     * The TransformerFactory should call this method to set the translet name
     */
    public void setTransletName(String name) {
	_transletName = name;
    }

    public String getTransletName() {
	return _transletName;
    }

    /**
     * Defines the translet class and auxiliary classes.
     * Returns a reference to the Class object that defines the main class
     */
    private Class defineTransletClasses() {
	if (_bytecodes == null) return null;

	TransletClassLoader loader = new TransletClassLoader();

	try {
	    Class transletClass = null;
	    final int classCount = _bytecodes.length;
	    for (int i = 0; i < classCount; i++) {
		Class clazz = loader.defineClass(_bytecodes[i]);
		if (clazz.getName().equals(_transletName))
		    transletClass = clazz;
	    }
	    return transletClass; // Could still be 'null'
	}
	catch (ClassFormatError e) {
	    return null;
	}
    }

    /**
     * This method generates an instance of the translet class that is
     * wrapped inside this Template. The translet instance will later
     * be wrapped inside a Transformer object.
     */
    private Translet getTransletInstance() {
	if (_transletName == null) return null;

	// First assume that the JVM already had the class definition
	try {
	    Class transletClass = Class.forName(_transletName);
	    return((Translet)transletClass.newInstance());
	}
	// Ignore these for now, try to define translet class again
	catch (LinkageError e) { }
	catch (InstantiationException e) { }
	catch (ClassNotFoundException e) { }
	catch (IllegalAccessException e) { }

	// Create the class definition from the bytecodes if failed
	try {
	    Class transletClass = defineTransletClasses();
	    return((Translet)transletClass.newInstance());
	}
	catch (LinkageError e) { return(null); }
	catch (InstantiationException e) { return(null); }
	catch (IllegalAccessException e) { return(null); }
    }

}

