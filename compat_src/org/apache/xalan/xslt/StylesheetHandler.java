/*
 * The Apache Software License, Version 1.1  
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
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
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xalan.xslt;

import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.ParserAdapter;
import org.w3c.dom.*;
import java.util.Vector;
import java.io.*;
import java.net.*;

import org.apache.xalan.templates.Stylesheet; 

/**
 * <meta name="usage" content="advanced"/>
 * Initializes and processes a stylesheet via SAX events.
 * If you need to alter the code in here,
 * it is not for the faint-of-heart, due to the state tracking
 * that has to be done due to the SAX event model.
 * @deprecated This compatibility layer will be removed in later releases. 
 */
public class StylesheetHandler extends ParserAdapter
  
{
  
  /**
   * Instance constructor... 
   * @param processor  The XSLTProcessor implementation.
   * @param stylesheet The root stylesheet.
   *    
   */
  public StylesheetHandler(XSLTEngineImpl processor, Stylesheet stylesheetTree)    
    throws javax.xml.transform.TransformerConfigurationException, SAXException 
  {
    
      super(new org.apache.xerces.parsers.SAXParser());
      //try{
      org.apache.xalan.processor.StylesheetHandler handler = 
            new org.apache.xalan.processor.StylesheetHandler(processor.getTransformerFactory());
      handler.pushStylesheet(stylesheetTree);
      this.setContentHandler(handler);
    //}
    //catch (javax.xml.transform.TransformerConfigurationException tce)
    //{}
  } 
  
  /**
   * Instance constructor... 
   * @param processor  The XSLTProcessor implementation.
   * @param stylesheet The root stylesheet. 
   * @exception TransformerConfigurationException  
   */
  public StylesheetHandler(XSLTEngineImpl processor, StylesheetRoot stylesheetTree)    
    throws javax.xml.transform.TransformerConfigurationException, SAXException 
  {
    //try{
      super(new org.apache.xerces.parsers.SAXParser());
      org.apache.xalan.processor.StylesheetHandler handler = 
            new org.apache.xalan.processor.StylesheetHandler(processor.getTransformerFactory());
      handler.pushStylesheet(stylesheetTree.getObject());
      this.setContentHandler(handler);
    //}
    //catch (javax.xml.transform.TransformerConfigurationException tce)
    //{}
  } 
  
  
}
