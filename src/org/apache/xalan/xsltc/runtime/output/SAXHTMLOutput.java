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
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES{} LOSS OF
 * USE, DATA, OR PROFITS{} OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
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
 * @author Santiago Pericas-Geertsen
 * @author G. Todd Miller 
 *
 */

package org.apache.xalan.xsltc.runtime.output;

import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;
import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.runtime.BasisLibrary;


public class SAXHTMLOutput extends SAXOutput  { 
   private boolean   _headTagOpen = false;

   public SAXHTMLOutput(ContentHandler handler, String encoding) {
	super(handler, encoding);
   }

   public SAXHTMLOutput(ContentHandler handler, LexicalHandler lex, 
	String encoding)
   {
	super(handler, lex, encoding);
   }


   public void startElement(String elementName) throws TransletException {
   }

   public void attribute(String name, final String value) 
	throws TransletException
   {
	final String patchedName = patchQName(name);
	final String localName = getLocalName(patchedName);
	final int index = _attributes.getIndex(name); 

	if (!_startTagOpen) {
            BasisLibrary.runTimeError(BasisLibrary.STRAY_ATTRIBUTE_ERR,name);
        }
	/*
         * The following is an attempt to escape an URL stored in a href
         * attribute of HTML output. Normally URLs should be encoded at
         * the time they are created, since escaping or unescaping a
         * completed URI might change its semantics. We limit or escaping
         * to include space characters only - and nothing else. This is for
         * two reasons: (1) performance and (2) we want to make sure that
         * we do not change the meaning of the URL.
         */
        final String tmp = name.toLowerCase();
        if (tmp.equals("href") || tmp.equals("src") || 
            tmp.equals("cite")) 
        {
		/************
            if (index >= 0) {
                _attributes.setAttribute(index, EMPTYSTRING, EMPTYSTRING, 
                    name, "CDATA", quickAndDirtyUrlEncode(escapeAttr(value)));
            }
            else {
                _attributes.addAttribute(EMPTYSTRING, EMPTYSTRING, name, 
                    "CDATA", quickAndDirtyUrlEncode(escapeAttr(value)));
            }
		**************/
        }
        else {
            if (index >= 0) {
                _attributes.setAttribute(index, EMPTYSTRING, EMPTYSTRING,
                    name, "CDATA", value);
            }
            else {
                _attributes.addAttribute(EMPTYSTRING, EMPTYSTRING,
                    name, "CDATA", value);
            }
        }
   }

   

}
