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
 * @author G. Todd Miller 
 *
 */
package org.apache.xalan.xsltc.runtime;

import javax.xml.transform.Templates;
import javax.xml.transform.Source; 
import javax.xml.transform.Transformer; 
import javax.xml.transform.TransformerConfigurationException; 
import javax.xml.transform.sax.SAXTransformerFactory; 

import org.apache.xalan.xsltc.runtime.AbstractTranslet;
import org.apache.xalan.xsltc.compiler.XSLTC;
import org.apache.xalan.xsltc.Translet;
import java.util.Properties;

import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Implementation of a JAXP1.1 Templates object for Translets.
 */ 
public class TransletTemplates implements Templates {
    private Source _stylesheet;

    public TransletTemplates(Source stylesheet) {
	_stylesheet = stylesheet;
    }

    public Transformer newTransformer() throws 
	TransformerConfigurationException
    {
        XSLTC xsltc = new XSLTC();
        xsltc.init();
        String stylesheetName = _stylesheet.getSystemId();
        int index = stylesheetName.indexOf('.');
        String transletName = stylesheetName.substring(0,index);
        boolean isSuccessful = true;
        try {
            File file = new File(stylesheetName);
            URL url = file.toURL();
            isSuccessful = xsltc.compile(url);
        } catch (MalformedURLException e) {
            throw new TransformerConfigurationException(
                "URL for stylesheet '" + stylesheetName +
                "' can not be formed.");
        }

        if (!isSuccessful) {
            throw new TransformerConfigurationException(
                "Compilation of stylesheet '" + stylesheetName + "' failed.");
        }

        Translet translet = null;
        try {
            Class clazz = Class.forName(transletName);
            translet = (Translet)clazz.newInstance();
            ((AbstractTranslet)translet).setTransletName(transletName);
        } catch (ClassNotFoundException e) {
            throw new TransformerConfigurationException(
                "Translet class '" + transletName + "' not found.");
        } catch (InstantiationException e) {
            throw new TransformerConfigurationException(
                "Translet class '" + transletName +
                "' could not be instantiated");
        } catch (IllegalAccessException  e) {
            throw new TransformerConfigurationException(
                "Translet class '" + transletName + "' could not be accessed.");
        }
        return (AbstractTranslet)translet;
    }

    public Properties getOutputProperties() { 
	/*TBD*/ 
	return new Properties(); 
    }

}
