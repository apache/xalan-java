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
 * @author G. Todd Miller
 *
 */

package org.apache.xalan.xsltc.trax;

import java.io.Serializable;

import javax.xml.transform.*;

import org.apache.xalan.xsltc.Translet;
import org.apache.xalan.xsltc.compiler.*;
import org.apache.xalan.xsltc.runtime.*;

import java.util.Properties;

public final class TemplatesImpl implements Templates, Serializable {
    
    // Contains the name of the main translet class
    private String   _transletName = null;

    // Contains the actual class definition for the translet class and
    // any auxiliary classes (representing node sort records, predicates, etc.)
    private byte[][] _bytecodes = null;

    // This error could occur when a compilation inside the TransformerFactory
    // failed and when a template has been loaded from stable storage.
    private final static String TRANSLET_ERR_MSG =
	"This template does not contain a valid translet class definition.";
    
    // Our own private class loader - builds Class definitions from bytecodes
    private class TransletClassLoader extends ClassLoader {
	public Class defineClass(byte[] b) {
	    return super.defineClass(null, b, 0, b.length);
	}
    }

    /**
     * The only way to create an XSLTC emplate object
     * The bytecodes for the translet and auxiliary classes, plus the name of
     * the main translet class, must be supplied
     */
    protected TemplatesImpl(byte[][] bytecodes, String transletName) {
	_bytecodes = bytecodes;
	_transletName = transletName;
    }

    /**
     * The TransformerFactory must pass us the translet bytecodes using this
     * method before we can create any translet instances
     */
    protected void setTransletBytecodes(byte[][] bytecodes) {
	_bytecodes = bytecodes;
    }

    /**
     * Returns the translet bytecodes stored in this template
     */
    protected byte[][] getTransletBytecodes() {
	return(_bytecodes);
    }

    /**
     * The TransformerFactory should call this method to set the translet name
     */
    protected void setTransletName(String name) {
	_transletName = name;
    }

    /**
     * Returns the name of the main translet class stored in this template
     */
    protected String getTransletName() {
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
	    if (transletClass == null) return null;
	    return((Translet)transletClass.newInstance());
	}
	catch (LinkageError e) { return(null); }
	catch (InstantiationException e) { return(null); }
	catch (IllegalAccessException e) { return(null); }
    }

    /**
     * Implements JAXP's Templates.newTransformer()
     *
     * @throws TransformerConfigurationException
     */
    public Transformer newTransformer()
	throws TransformerConfigurationException {
	Translet translet = getTransletInstance();
	if (translet == null)
	    throw new TransformerConfigurationException(TRANSLET_ERR_MSG);
	TransformerImpl transformer = new TransformerImpl(translet);
        return(transformer);
    }

    /**
     * Implements JAXP's Templates.getOutputProperties()
     */
    public Properties getOutputProperties() { 
	// We need to instanciate a translet to get the output settings, so
	// we might as well just instanciate a Transformer and use its
	// implementation of this method
	try {
	    Transformer transformer = newTransformer();
	    return transformer.getOutputProperties();
	}
	catch (TransformerConfigurationException e) {
	    return null;
	}
    }

}

