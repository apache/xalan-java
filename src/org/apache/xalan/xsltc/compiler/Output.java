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

import de.fub.bytecode.generic.*;
import de.fub.bytecode.classfile.JavaClass;

import org.apache.xalan.xsltc.compiler.util.*;
import org.apache.xalan.xsltc.runtime.TextOutput;

final class Output extends TopLevelElement {

    // These attributes are extracted from the xsl:output element. They also
    // appear as fields (with the same type, only public) in the translet
    private String  _version;
    private String  _method;
    private String  _encoding;
    private boolean _omitHeader = false;
    private String  _standalone;
    private String  _doctypePublic;
    private String  _doctypeSystem;
    private String  _cdata;
    private boolean _indent = false;
    private String  _mediaType;

    // Disables this output element (when other element has higher precedence)
    private boolean _disabled = false;

    // Some global constants
    private final static String STRING_SIG = "Ljava/lang/String;";
    private final static String ONE_DOT_ZERO_STRING = "1.0";
    private final static String OUTPUT_VERSION_ERROR =
	"Output XML document type should be 1.0";

    /**
     * Displays the contents of this element (for debugging)
     */
    public void display(int indent) {
	indent(indent);
	Util.println("Output " + _method);
    }

    /**
     * Disables this <xsl:output> element in case where there are some other
     * <xsl:output> element (from a different imported/included stylesheet)
     * with higher precedence.
     */
    public void disable() {
	_disabled = true;
    }

    /**
     * Scans the attribute list for the xsl:output instruction
     */
    public void parseContents(Parser parser) {

	// Do nothing if other <xsl:output> element has higher precedence
	if (_disabled) return;

	String attrib = null;

	// Get the output XML version - only version "1.0" should be used
	_version = getAttribute("version");
	if ((_version == null) || (_version.equals(Constants.EMPTYSTRING))) {
	    _version = ONE_DOT_ZERO_STRING;
	}
	if (!_version.equals(ONE_DOT_ZERO_STRING)) {
	    ErrorMsg msg = new ErrorMsg(OUTPUT_VERSION_ERROR);
	    parser.reportError(Constants.WARNING, msg);
	}

	// Get the output method - "xml", "html", "text" or <qname>
	_method = getAttribute("method");
	if (_method.equals(Constants.EMPTYSTRING)) _method = null;
	if (_method != null) _method = _method.toLowerCase();

	// Get the output encoding - any value accepted here
	_encoding = getAttribute("encoding");
	if (_encoding.equals(Constants.EMPTYSTRING)) _encoding = null;

	// Should the XML header be omitted - translate to true/false
	attrib = getAttribute("omit-xml-declaration");
	if ((attrib != null) && (attrib.equals("yes"))) _omitHeader = true;

	// Add 'standalone' decaration to output - use text as is
	_standalone = getAttribute("standalone");
	if (_standalone.equals(Constants.EMPTYSTRING)) _standalone = null;

	// Get system/public identifiers for output DOCTYPE declaration
	_doctypeSystem = getAttribute("doctype-system");
	if (_doctypeSystem.equals(Constants.EMPTYSTRING)) _doctypeSystem = null;
	_doctypePublic = getAttribute("doctype-public");
	if (_doctypePublic.equals(Constants.EMPTYSTRING)) _doctypePublic = null;

	// Names the elements of whose text contents should be output as CDATA
	_cdata = getAttribute("cdata-section-elements");
	if ((_cdata != null) && (_cdata.equals(Constants.EMPTYSTRING)))
	    _cdata = null;

	// Get the indent setting - only has effect for xml and html output
	attrib = getAttribute("indent");
	if ((attrib != null) && (attrib.equals("yes"))) _indent = true;

	// Get the MIME type for the output file - we don't do anythign with it,
	// but our client may use it to specify a data transport type, etc.
	_mediaType = getAttribute("media-type");
	if (_mediaType.equals(Constants.EMPTYSTRING)) _mediaType = null;

	// parseChildren(parser); - the element is always empty

	parser.setOutput(this);
    }

    /**
     * Compile code that passes the information in this <xsl:output> element
     * to the appropriate fields in the translet
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {

	// Do nothing if other <xsl:output> element has higher precedence
	if (_disabled) return;

	ConstantPoolGen cpg = classGen.getConstantPool();
	InstructionList il = methodGen.getInstructionList();

	int field = 0;
        il.append(classGen.loadTranslet());

	// Only update _version field if set and different from default
	if ((_version != null) && (!_version.equals(ONE_DOT_ZERO_STRING))) {
	    field = cpg.addFieldref(TRANSLET_CLASS, "_version", STRING_SIG);
	    il.append(DUP);
	    il.append(new PUSH(cpg, _version));
	    il.append(new PUTFIELD(field));
	}

	// Only update _method field if "method" attribute used
	if (_method != null) {
	    field = cpg.addFieldref(TRANSLET_CLASS, "_method", STRING_SIG);
	    il.append(DUP);
	    il.append(new PUSH(cpg, _method));
	    il.append(new PUTFIELD(field));
	}

	// Only update if _encoding field is "encoding" attribute used
	if (_encoding != null) {
	    field = cpg.addFieldref(TRANSLET_CLASS, "_encoding", STRING_SIG);
	    il.append(DUP);
	    il.append(new PUSH(cpg, _encoding));
	    il.append(new PUTFIELD(field));
	}

	// Only update if "omit-xml-declaration" used and set to 'yes'
	if (_omitHeader) {
	    field = cpg.addFieldref(TRANSLET_CLASS, "_omitHeader", "Z");
	    il.append(DUP);
	    il.append(new PUSH(cpg, _omitHeader));
	    il.append(new PUTFIELD(field));
	}

	// Add 'standalone' decaration to output - use text as is
	if (_standalone != null) {
	    field = cpg.addFieldref(TRANSLET_CLASS, "_standalone", STRING_SIG);
	    il.append(DUP);
	    il.append(new PUSH(cpg, _standalone));
	    il.append(new PUTFIELD(field));
	}

	// Set system/public doctype only if both are set
	field = cpg.addFieldref(TRANSLET_CLASS,"_doctypeSystem",STRING_SIG);
	il.append(DUP);
	il.append(new PUSH(cpg, _doctypeSystem));
	il.append(new PUTFIELD(field));
	field = cpg.addFieldref(TRANSLET_CLASS,"_doctypePublic",STRING_SIG);
	il.append(DUP);
	il.append(new PUSH(cpg, _doctypePublic));
	il.append(new PUTFIELD(field));
	
	// Add 'medye-type' decaration to output - if used
	if (_mediaType != null) {
	    field = cpg.addFieldref(TRANSLET_CLASS, "_mediaType", STRING_SIG);
	    il.append(DUP);
	    il.append(new PUSH(cpg, _mediaType));
	    il.append(new PUTFIELD(field));
	}

	// Compile code to set output indentation on/off
	if (_indent ) {
	    field = cpg.addFieldref(TRANSLET_CLASS, "_indent", "Z");
	    il.append(DUP);
	    il.append(new PUSH(cpg, _indent));
	    il.append(new PUTFIELD(field));
	}

	// Forward to the translet any elements that should be output as CDATA
	if (_cdata != null) {
	    int index = cpg.addMethodref(TRANSLET_CLASS,
					 "addCdataElement",
					 "(Ljava/lang/String;)V");
	    StringTokenizer tokens = new StringTokenizer(_cdata);
	    while (tokens.hasMoreTokens()) {
		il.append(DUP);
		il.append(new PUSH(cpg, tokens.nextToken()));
		il.append(new INVOKEVIRTUAL(index));
	    }
	}
	il.append(POP); // Cleanup - pop last translet reference off stack
    }

}
