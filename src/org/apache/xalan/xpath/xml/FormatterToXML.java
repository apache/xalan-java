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
package org.apache.xalan.xpath.xml;

import java.io.Writer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.apache.xml.serialize.transition.XMLSerializer;


/**
 * <meta name="usage" content="general"/>
 * FormatterToXML formats SAX-style events into XML.
 * Warning: this class will be replaced by the Xerces Serializer classes.
 */
public class FormatterToXML //extends BaseMarkupSerializer 
{
  XMLSerializer m_serializer;
  public FormatterToXML()
  {
    m_serializer = new XMLSerializer();
    //super( new OutputFormat( Method.XML, null, false ) );     
  }
  
  /**
   * Constructor using a writer.
   * @param writer        The character output stream to use.
   */
  public FormatterToXML(Writer writer) 
  {
    m_serializer = new XMLSerializer(writer, null);
   // super( format != null ? format : new OutputFormat( Method.XML, null, false ) );
    //_format.setMethod( Method.XML );
    //setOutputCharStream( writer );
  }
  
  /**
   * Constructor using an output stream, and a simple OutputFormat.
   * @param writer        The character output stream to use.
   */
  public FormatterToXML(java.io.OutputStream os) 
    throws UnsupportedEncodingException
  {
    m_serializer = new XMLSerializer(os, null);
    //super( format != null ? format : new OutputFormat( Method.XML, null, false ) );
    //_format.setMethod( Method.XML );
    //setOutputByteStream( os );
  }
  
  /**
   * Constructor using a writer.
   * @param writer        The character output stream to use.
   */
  public FormatterToXML(FormatterToXML xmlListener) 
  {
    m_serializer = new XMLSerializer();
    //super( new OutputFormat( Method.XML, null, false ) );  
  }
  
  
  
  

}  //ToXMLStringVisitor
