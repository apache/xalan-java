/*
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
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.apache.xalan.xsltc.trax.TransformerFactoryImpl;

public class Transform {

    public static void main(String[] args){
        Transform app = new Transform();
        app.run(args);
    }

    /**
     * Reads a Templates object from a file, the Templates object creates
     * a translet and wraps it in a Transformer. The translet performs the
     * transformation on behalf of the Transformer.transform() method.
     */
    public void run(String[] args){
        String xml = args[0];
        String translet = args[1];

        try {
	    StreamSource document = new StreamSource(xml);
	    StreamResult result = new StreamResult(System.out);
	    Templates templates = readTemplates(translet);
	    Transformer transformer = templates.newTransformer();
            transformer.transform(document, result);
        }
	catch (Exception e) {
            System.err.println("Exception: " + e); 
	    e.printStackTrace();
        }
        System.exit(0);
    }

    /**
     * Reads a Templates object from a file
     */
    private Templates readTemplates(String file) {
	try {
	    FileInputStream ostream = new FileInputStream(file);
	    ObjectInputStream p = new ObjectInputStream(ostream);
	    Templates templates = (Templates)p.readObject();
	    ostream.close();
	    return(templates);
	}
	catch (Exception e) {
	    System.err.println(e);
	    e.printStackTrace();
	    System.err.println("Could not write file "+file);
	    return null;
	}
    }

    /**
     * Returns the base-name of a file/url
     */
    private String getBaseName(String filename) {
	int start = filename.lastIndexOf(File.separatorChar);
	int stop  = filename.lastIndexOf('.');
	if (stop <= start) stop = filename.length() - 1;
	return filename.substring(start+1, stop);
    }

    public void usage() {
        System.err.println("Usage: run <xml_file> <xsl_file>");
        System.exit(1);
    }

}
