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
 * @author Santiago Pericas-Geertsen
 *
 */

package org.apache.xalan.xsltc.runtime;

import java.lang.Class;
import java.lang.ClassLoader;
import java.lang.Thread;

import java.net.*;	// temporary

/**
 * This class is intended used when the default Class.forName() method fails.
 * This method will fail if XSLTC is installed in a jar-file under the
 * $JAVA_HOME/jre/lib/ext directory. This is because the extensions class
 * loader is used instead of the bootstrap class loader, and that the
 * extensions class loader does not load classes for the default class path.
 * But, if the extensions class loader is being used, then we know two things:
 *  (1) XSLTC is running on Java 1.2 or later (when extensions were introduced)
 *  (2) XSLTC has access to the ClassLoader.getSystemClassLoader() method
 * This class takes advantage of this and uses a privileged call to this
 * method to get a reference to the bootstrap class loader. It then uses this
 * class loader to load the desired class.
 *
 * Note that this class should only be _instanciated_ if Class.forName() fails.
 * And, YES, I do mean _instanciated_, and not called. By instanciating this
 * class on Java 1.1 you'll get a NoSuchMethodException.
 */
final public class TransletLoader {
    
    ClassLoader _loader = null; // Reference to class loader

    /**
     * Create a translet loader.
     * Get a handle to the system class loader
     */
    public TransletLoader() {
	// Get the loader for the current thread (not the current class)
	ClassLoader loader = Thread.currentThread().getContextClassLoader();

	// Avoid using the extensions class loader (see comment above)
	final String loaderName = loader.getClass().getName();
	if (loaderName.equals("sun.misc.Launcher$ExtClassLoader")) {
	    loader = ClassLoader.getSystemClassLoader();
	}
	_loader = loader;
    }

    /**
     * Loads a Class definition, but does not run static initializers
     */
    public Class loadClass(String name) throws ClassNotFoundException {
	return(Class.forName(name, false, _loader));
    }

    /**
     * Loads a Class definition and runs static initializers.
     */
    public Class loadTranslet(String name) throws ClassNotFoundException {
	return(Class.forName(name, true, _loader));
    }
}
