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
 * @author Morten Jorgensen
 *
 */

package org.apache.xalan.xsltc.compiler;

import java.util.Vector;
import java.util.Enumeration;
import java.util.StringTokenizer;

import org.w3c.dom.*;

import de.fub.bytecode.generic.*;
import de.fub.bytecode.classfile.JavaClass;

import org.apache.xalan.xsltc.compiler.util.*;
import org.apache.xalan.xsltc.runtime.TextOutput;

final class Output extends TopLevelElement {

    // These attributes are extracted from the xsl:output element
    private String  _method;
    private String  _version;
    private String  _encoding;
    private boolean _omitXmlDeclaration = false;
    private String  _standalone;
    private String  _doctypePublic;	// IGNORED!!!
    private String  _doctypeSystem;	// IGNORED!!!
    private String  _cdataElements;
    private boolean _indent = false;
    private String  _mediaType;

    private boolean _disabled = false;

    // This is generated from the above data.
    private String _header;

    /**
     * Displays the contents of this element (for debugging)
     */
    public void display(int indent) {
	indent(indent);
	Util.println("Output " + _method);
    }

    public void disable() {
	_disabled = true;
    }

    /**
     * Generates the XML output document header
     */
    private String generateXmlHeader() {
        // No header if user doesn't want one.
        if (_omitXmlDeclaration)  {
	    return("");
	}

	// Start off XML header
        final StringBuffer hdr = new StringBuffer("<?xml ");
	
	if ((_version != null) && (!_version.equals("")))
	    hdr.append("version=\"" + _version + "\" ");
	else
	    hdr.append("version=\"1.0\" ");

	if ((_encoding != null) && (!_encoding.equals("")))
	    hdr.append("encoding=\"" + _encoding + "\" ");
	else
	    hdr.append("encoding=\"utf-8\" ");

	if ((_standalone != null) && (!_standalone.equals("")))
	    hdr.append("standalone=\"" + _standalone + "\" ");

	// Finish off XML header and return string.
	hdr.append("?>");
	return(hdr.toString());
    }

    /**
     * Scans the attribute list for the xsl:output instruction
     */
    public void parseContents(Element element, Parser parser) {

	_method        = element.getAttribute("method");
	_version       = element.getAttribute("version");
	_encoding      = element.getAttribute("encoding");
	_doctypeSystem = element.getAttribute("doctype-system");
	_doctypePublic = element.getAttribute("doctype-public");
	_cdataElements = element.getAttribute("cdata-section-elements");
	_mediaType     = element.getAttribute("media-type");
	_standalone    = element.getAttribute("standalone");

	if ((_method == null) || (_method.equals("")))
	    _method = "xml";

	String attrib = element.getAttribute("omit-xml-declaration");
	if ((attrib != null) && (attrib.equals("yes")))
	    _omitXmlDeclaration = true;

	if (_method.equals("xml") || _method.equals("html")) {
	    attrib = element.getAttribute("indent");
	    if ((attrib != null) && (attrib.equals("yes")))
	        _indent = true;
	}

	if (_method.equals("xml"))
	    _header = generateXmlHeader();
	else
	    _header = null;

	parseChildren(element, parser);

	parser.setOutput(this);
    }

    /**
     * Compile code to set the appropriate output type and 
     * output header (if any).
     */
    public void translate(ClassGenerator classGen,
			  MethodGenerator methodGen) {
	
	if (_disabled) return;

	ConstantPoolGen cpg = classGen.getConstantPool();
	InstructionList il = methodGen.getInstructionList();

	// bug fix # 1406, Compile code to set xml header on/off
	if ( _omitXmlDeclaration ) {
	    final int omitXmlDecl = cpg.addInterfaceMethodref(OUTPUT_HANDLER,
						 "omitXmlDecl","(Z)V");
	    il.append(methodGen.loadHandler());
	    il.append(new PUSH(cpg, true));
	    il.append(new INVOKEINTERFACE(omitXmlDecl,2));
	}

	// Compile code to set the appropriate output type.
	final int type = cpg.addInterfaceMethodref(OUTPUT_HANDLER,
						   "setType", "(I)V");
	il.append(methodGen.loadHandler());
	if (_method.equals("text")) {
  	    il.append(new PUSH(cpg, org.apache.xalan.xsltc.runtime.TextOutput.TEXT));
	}
	else if (_method.equals("xml")) {
	    // Handle any XML elements that should be turned into
	    // CDATA elements in the XML output document.
	    if ((_cdataElements != null) && (_cdataElements != "")) {
	        StringTokenizer st = new StringTokenizer(_cdataElements,",");
		final int cdata = cpg.addInterfaceMethodref(OUTPUT_HANDLER,
						   "insertCdataElement",
						   "("+STRING_SIG+")V");
		while (st.hasMoreElements()) {
		    il.append(DUP); // Reference to handler on stack
		    il.append(new PUSH(cpg,st.nextToken()));
		    il.append(new INVOKEINTERFACE(cdata,2));
		}
	    }
	    il.append(new PUSH(cpg,org.apache.xalan.xsltc.runtime.TextOutput.XML));
	}
	else if (_method.equals("html")) {
	    il.append(new PUSH(cpg,org.apache.xalan.xsltc.runtime.TextOutput.HTML));
	}
	else {
	    il.append(new PUSH(cpg, org.apache.xalan.xsltc.runtime.TextOutput.QNAME));
	}
	il.append(new INVOKEINTERFACE(type,2));

	// Compile code to set output indentation on/off
	if ( _indent ) {
	    final int indent = cpg.addInterfaceMethodref(OUTPUT_HANDLER,
							 "setIndent","(Z)V");
	    il.append(methodGen.loadHandler());
	    il.append(new PUSH(cpg, true));
	    il.append(new INVOKEINTERFACE(indent,2));
	}
    }

    /**
     *  Sets the character encoding in the translet. This method is   
     *  called from org.apache.xalan.xsltc.compiler.Stylesheet in order to
     *  have the value of the encoding  that was specified in the
     *  stylesheet (<xsl:output> element) available in the translets
     *  constructor.  
     */
    public void translateEncoding(ClassGenerator classGen, InstructionList il) {
	ConstantPoolGen cpg = classGen.getConstantPool();
        il.append(classGen.loadTranslet());
        il.append(new PUSH(cpg, _encoding));
        il.append(new PUTFIELD(cpg.addFieldref(TRANSLET_CLASS,
					       "_encoding",
					       "Ljava/lang/String;")));
    }
}
