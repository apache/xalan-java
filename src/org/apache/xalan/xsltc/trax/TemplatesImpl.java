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
 * @author Morten Jorgensen
 * @author G. Todd Millerj
 * @author Jochen Cordes <Jochen.Cordes@t-online.de>
 * @author Santiago Pericas-Geertsen 
 *
 */

package org.apache.xalan.xsltc.trax;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Properties;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.URIResolver;

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.Translet;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.runtime.AbstractTranslet;
import org.apache.xalan.xsltc.runtime.Hashtable;
import org.apache.xml.utils.ObjectFactory;

public final class TemplatesImpl implements Templates, Serializable {

    /**
     * Name of the superclass of all translets. This is needed to
     * determine which, among all classes comprising a translet, 
     * is the main one.
     */
    private static String ABSTRACT_TRANSLET 
	= "org.apache.xalan.xsltc.runtime.AbstractTranslet";

    /**
     * Name of the main class or default name if unknown.
     */
    private String _name = null;

    /**
     * Contains the actual class definition for the translet class and
     * any auxiliary classes.
     */
    private byte[][] _bytecodes = null;
    
    /**
     * Contains the translet class definition(s). These are created when 
     * this Templates is created or when it is read back from disk.
     */
    private Class[] _class = null;

    /**
     * The index of the main translet class in the arrays _class[] and
     * _bytecodes.
     */
    private int _transletIndex = -1;
    
    /**
     * Contains the list of auxiliary class definitions.
     */
    private Hashtable _auxClasses = null;
    
    /**
     * Output properties of this translet.
     */
    private Properties _outputProperties; 

    /**
     * Number of spaces to add for output indentation.
     */
    private int _indentNumber;

    /**
     * This URIResolver is passed to all Transformers.
     * Declaring it transient to fix bug 22438 
     */
    private transient URIResolver _uriResolver = null;

    /**
     * Cache the DTM for the stylesheet in a thread local variable,
     * which is used by the document('') function.
     * Use ThreadLocal because a DTM cannot be shared between
     * multiple threads. 
     * Declaring it transient to fix bug 22438 
     */
    private transient ThreadLocal _sdom = new ThreadLocal();
    
    /**
     * A reference to the transformer factory that this templates
     * object belongs to.
     */
    private transient TransformerFactoryImpl _tfactory = null;

    static final class TransletClassLoader extends ClassLoader {
	TransletClassLoader(ClassLoader parent) {
	    super(parent);
	}

        /**
         * Access to final protected superclass member from outer class.
         */
	Class defineClass(byte[] b) {
	    return defineClass(null, b, 0, b.length);
	}
    }


    /**
     * Create an XSLTC template object from the bytecodes.
     * The bytecodes for the translet and auxiliary classes, plus the name of
     * the main translet class, must be supplied.
     */
    protected TemplatesImpl(byte[][] bytecodes, String transletName,
	Properties outputProperties, int indentNumber,
	TransformerFactoryImpl tfactory) 
    {
	_bytecodes = bytecodes;
	_name      = transletName;
	_outputProperties = outputProperties;
	_indentNumber = indentNumber;
	_tfactory = tfactory;
    }
    
    /**
     * Create an XSLTC template object from the translet class definition(s).
     */
    protected TemplatesImpl(Class[] transletClasses, String transletName,
	Properties outputProperties, int indentNumber,
	TransformerFactoryImpl tfactory) 
    {
	_class     = transletClasses;
	_name      = transletName;
	_transletIndex = 0;
	_outputProperties = outputProperties;
	_indentNumber = indentNumber;
	_tfactory = tfactory;
    }
    

    /**
     * Need for de-serialization, see readObject().
     */
    public TemplatesImpl() { }

    /**
     *  Overrides the default readObject implementation since we decided
     *  it would be cleaner not to serialize the entire tranformer
     *  factory.  [ ref bugzilla 12317 ]
     *  We need to check if the user defined class for URIResolver also
     *  implemented Serializable
     *  if yes then we need to deserialize the URIResolver
     *  Fix for bugzilla bug 22438
     */
    private void  readObject(ObjectInputStream is) 
      throws IOException, ClassNotFoundException 
    {
	is.defaultReadObject();
        if (is.readBoolean()) {
            _uriResolver = (URIResolver) is.readObject();
        }

	_tfactory = new TransformerFactoryImpl();
    } 


    /**
     *  This is to fix bugzilla bug 22438
     *  If the user defined class implements URIResolver and Serializable
     *  then we want it to get serialized
     */
    private void writeObject(ObjectOutputStream os)
        throws IOException, ClassNotFoundException {
        os.defaultWriteObject();
        if (_uriResolver instanceof Serializable) {
            os.writeBoolean(true);
            os.writeObject((Serializable) _uriResolver);
        }
        else {
            os.writeBoolean(false);
        }
    }


     /**
     * Store URIResolver needed for Transformers.
     */
    public synchronized void setURIResolver(URIResolver resolver) {
	_uriResolver = resolver;
    }

    /**
     * The TransformerFactory must pass us the translet bytecodes using this
     * method before we can create any translet instances
     */
    protected synchronized void setTransletBytecodes(byte[][] bytecodes) {
	_bytecodes = bytecodes;
    }

    /**
     * Returns the translet bytecodes stored in this template
     */
    public synchronized byte[][] getTransletBytecodes() {
	return _bytecodes;
    }

    /**
     * Returns the translet bytecodes stored in this template
     */
    public synchronized Class[] getTransletClasses() {
	try {
	    if (_class == null) defineTransletClasses();
	}
	catch (TransformerConfigurationException e) {
	    // Falls through
	}
	return _class;
    }

    /**
     * Returns the index of the main class in array of bytecodes
     */
    public synchronized int getTransletIndex() {
	try {
	    if (_class == null) defineTransletClasses();
	}
	catch (TransformerConfigurationException e) {
	    // Falls through
	}
	return _transletIndex;
    }

    /**
     * The TransformerFactory should call this method to set the translet name
     */
    protected synchronized void setTransletName(String name) {
	_name = name;
    }

    /**
     * Returns the name of the main translet class stored in this template
     */
    protected synchronized String getTransletName() {
	return _name;
    }

    /**
     * Defines the translet class and auxiliary classes.
     * Returns a reference to the Class object that defines the main class
     */
    private void defineTransletClasses()
	throws TransformerConfigurationException {

	if (_bytecodes == null) {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.NO_TRANSLET_CLASS_ERR);
	    throw new TransformerConfigurationException(err.toString());
	}

        TransletClassLoader loader =
            new TransletClassLoader(ObjectFactory.findClassLoader());

	try {
	    final int classCount = _bytecodes.length;
	    _class = new Class[classCount];

	    if (classCount > 1) {
	        _auxClasses = new Hashtable();
	    }

	    for (int i = 0; i < classCount; i++) {
		_class[i] = loader.defineClass(_bytecodes[i]);
		final Class superClass = _class[i].getSuperclass();

		// Check if this is the main class
		if (superClass.getName().equals(ABSTRACT_TRANSLET)) {
		    _transletIndex = i;
		}
		else {
		    _auxClasses.put(_class[i].getName(), _class[i]);
		}
	    }

	    if (_transletIndex < 0) {
		ErrorMsg err= new ErrorMsg(ErrorMsg.NO_MAIN_TRANSLET_ERR, _name);
		throw new TransformerConfigurationException(err.toString());
	    }
	}
	catch (ClassFormatError e) {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.TRANSLET_CLASS_ERR, _name);
	    throw new TransformerConfigurationException(err.toString());
	}
	catch (LinkageError e) {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.TRANSLET_OBJECT_ERR, _name);
	    throw new TransformerConfigurationException(err.toString());
	}
    }

    /**
     * This method generates an instance of the translet class that is
     * wrapped inside this Template. The translet instance will later
     * be wrapped inside a Transformer object.
     */
    private Translet getTransletInstance()
	throws TransformerConfigurationException {
	try {
	    if (_name == null) return null;

	    if (_class == null) defineTransletClasses();

	    // The translet needs to keep a reference to all its auxiliary 
	    // class to prevent the GC from collecting them
	    AbstractTranslet translet = (AbstractTranslet) _class[_transletIndex].newInstance();
            translet.postInitialization();
	    translet.setTemplates(this);
	    if (_auxClasses != null) {
	        translet.setAuxiliaryClasses(_auxClasses);
	    }
	    
	    return translet;
	}
	catch (InstantiationException e) {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.TRANSLET_OBJECT_ERR, _name);
	    throw new TransformerConfigurationException(err.toString());
	}
	catch (IllegalAccessException e) {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.TRANSLET_OBJECT_ERR, _name);
	    throw new TransformerConfigurationException(err.toString());
	}
    }

    /**
     * Implements JAXP's Templates.newTransformer()
     *
     * @throws TransformerConfigurationException
     */
    public synchronized Transformer newTransformer()
	throws TransformerConfigurationException 
    {
	TransformerImpl transformer;

	transformer = new TransformerImpl(getTransletInstance(), _outputProperties,
	    _indentNumber, _tfactory);
	
	if (_uriResolver != null) {
	    transformer.setURIResolver(_uriResolver);
	}
	return transformer;
    }

    /**
     * Implements JAXP's Templates.getOutputProperties(). We need to 
     * instanciate a translet to get the output settings, so
     * we might as well just instanciate a Transformer and use its
     * implementation of this method.
     */
    public synchronized Properties getOutputProperties() { 
	try {
	    return newTransformer().getOutputProperties();
	}
	catch (TransformerConfigurationException e) {
	    return null;
	}
    }

    /**
     * Return the thread local copy of the stylesheet DOM.
     */
    public DOM getStylesheetDOM() {
    	return (DOM)_sdom.get();
    }
    
    /**
     * Set the thread local copy of the stylesheet DOM.
     */
    public void setStylesheetDOM(DOM sdom) {
    	_sdom.set(sdom);
    }
}
