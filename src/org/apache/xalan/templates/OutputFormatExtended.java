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
 *     the documentation and/or other materials provided with the
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
package org.apache.xalan.templates;

import serialize.OutputFormat;
import java.util.Vector;
import org.w3c.dom.Document;

import org.apache.xalan.utils.QName;

/**
 * This class extends OutputFormat to act as a bean that 
 * has methods that match the algorithmically generated 
 * method signatures of the processor.  It also has flags 
 * that tell if a given method has been set or not by the 
 * stylesheet.
 */
public class OutputFormatExtended extends OutputFormat
{
  // Flag to tell us when to record that an attribute 
  // has been set.
  private boolean m_shouldRecordHasBeenSet;
  
  private boolean m_methodHasBeenSet = false;
  public boolean methodHasBeenSet() { return m_methodHasBeenSet; }
  private boolean m_versionHasBeenSet = false;
  public boolean versionHasBeenSet() { return m_versionHasBeenSet; }
  private boolean m_indentingHasBeenSet = false;
  public boolean indentingHasBeenSet() { return m_indentingHasBeenSet; }
  private boolean m_indentHasBeenSet = false;
  public boolean indentHasBeenSet() { return m_indentHasBeenSet; }
  private boolean m_encodingHasBeenSet = false;
  public boolean encodingHasBeenSet() { return m_encodingHasBeenSet; }
  private boolean m_mediaTypeHasBeenSet;
  public boolean mediaTypeHasBeenSet() { return m_mediaTypeHasBeenSet; }
  private boolean m_doctypeSystemHasBeenSet;
  public boolean doctypeSystemHasBeenSet() { return m_doctypeSystemHasBeenSet; }
  private boolean m_doctypePublicHasBeenSet;
  public boolean doctypePublicHasBeenSet() { return m_doctypePublicHasBeenSet; }
  private boolean m_omitXmlDeclarationHasBeenSet = false;
  public boolean omitXmlDeclarationHasBeenSet() { return m_omitXmlDeclarationHasBeenSet; }
  private boolean m_standaloneHasBeenSet = false;
  public boolean standaloneHasBeenSet() { return m_standaloneHasBeenSet; }
  private boolean m_cdataElementsHasBeenSet = false;
  public boolean cdataElementsHasBeenSet() { return m_cdataElementsHasBeenSet; }
  private boolean m_nonEscapingElementsHasBeenSet = false;
  public boolean nonEscapingElementsHasBeenSet() { return m_nonEscapingElementsHasBeenSet; }

  /**
   * Constructs a new output format with the default values.
   */
  public OutputFormatExtended()
  {
    super();
    m_shouldRecordHasBeenSet = true;
    setPreserveSpace(true);
  }

  /**
   * Constructs a new output format with the default values for
   * the specified method and encoding. If <tt>indent</tt>
   * is true, the document will be pretty printed with the default
   * indentation level and default line wrapping.
   *
   * @param method The specified output method
   * @param encoding The specified encoding
   * @param indenting True for pretty printing
   * @see #setEncoding
   * @see #setIndenting
   * @see #setMethod
   */
  public OutputFormatExtended( String method, String encoding, boolean indenting )
  {
    // super(method, encoding, indenting);
    if(null != method)
      super.setMethod(method);
    if(null != encoding)
      super.setEncoding(encoding);
    super.setIndent(indenting);
    m_shouldRecordHasBeenSet = true;
  }

  /*
   * Constructs a new output format with the proper method,
   * document type identifiers and media type for the specified
   * document.
   *
   * @param doc The document to output
   * @see #whichMethod
   */
  // public OutputFormatExtended( Document doc )
  // {
    // super(doc);
    // super();
    // m_shouldRecordHasBeenSet = true;
  // }
  
  /**
   * Constructs a new output format with the proper method,
   * document type identifiers and media type for the specified
   * document, and with the specified encoding. If <tt>indent</tt>
   * is true, the document will be pretty printed with the default
   * indentation level and default line wrapping.
   *
   * @param doc The document to output
   * @param encoding The specified encoding
   * @param indenting True for pretty printing
   * @see #setEncoding
   * @see #setIndenting
   * @see #whichMethod
   */
  // public OutputFormatExtended( Document doc, String encoding, boolean indenting )
  // {
    // super(doc, encoding, indenting);
    // m_shouldRecordHasBeenSet = true;
  // }
  
  /**
   * The doctype-public attribute.
   */
  public void setDoctypePublic ( String publicId )
  {
    if(m_shouldRecordHasBeenSet)
      m_doctypePublicHasBeenSet = true;
    super.setDoctypePublicId(publicId);
    // super.setDoctype(publicId, this.getDoctypeSystem());
  }

  /**
   * The doctype-system attribute.
   */
  public void setDoctypeSystem  ( String systemId )
  {
    if(m_shouldRecordHasBeenSet)
      m_doctypeSystemHasBeenSet = true;
    // super.setDoctype(this.getDoctypePublic(), systemId);
    super.setDoctypeSystemId(systemId);
  }
  
  /**
   * The omit-xml-declaration attribute.
   */
  public void setOmitXmlDeclaration( boolean omit )
  {
    if(m_shouldRecordHasBeenSet)
      m_omitXmlDeclarationHasBeenSet = true;
    super.setOmitXMLDeclaration(omit);
  }
  
  /**
   * The cdata-section-elements attribute.
   */
  public void setCdataSectionElements(Vector elements)
  {
    if(m_shouldRecordHasBeenSet)
      m_cdataElementsHasBeenSet = true;
    int n = elements.size();
    serialize.QName[] qnames = new QName[n];
    for(int i = 0; i < n; i++)
    {
      qnames[i] = (QName)elements.elementAt(i);
    }
    super.setCDataElements(qnames);
  }
  
  /**
   * The cdata-section-elements attribute.
   */
  public void setCdataSectionElements(serialize.QName[] elements)
  {
    if(m_shouldRecordHasBeenSet)
      m_cdataElementsHasBeenSet = true;
    super.setCDataElements(elements);
  }
  
  /**
   * Sets the method for this output format.
   *
   * @see #getMethod
   * @param method The output method, or null
   */
  public void setMethod( String method )
  {
    if(m_shouldRecordHasBeenSet)
      m_methodHasBeenSet = true;
    super.setMethod(method);;
  }
  
  /**
   * Sets the method for this output format.
   *
   * @see #getMethod
   * @param method The output method, or null
   */
  public void setMethod( QName method )
  {
    if(m_shouldRecordHasBeenSet)
      m_methodHasBeenSet = true;
    // TODO: Work with Assaf on this.
    String meth = method.getLocalPart();
    super.setMethod(meth);;
  }

  /**
   * Sets the version for this output method.
   * For XML the value would be "1.0", for HTML
   * it would be "4.0".
   *
   * @see #getVersion
   * @param version The output method version, or null
   */
  public void setVersion( String version )
  {
    if(m_shouldRecordHasBeenSet)
      m_versionHasBeenSet = true;
    super.setVersion(version);;
  }

  /**
   * Sets the indentation on and off. When set on, the default
   * indentation level and default line wrapping is used
   * (see {@link #DEFAULT_INDENT} and {@link #DEFAULT_LINE_WIDTH}).
   * To specify a different indentation level or line wrapping,
   * use {@link #setIndent} and {@link #setLineWidth}.
   * 
   * <p>This method signature is required by the
   * {@link org.apache.xalan.processor.XSLTAttributeDef#getSetterMethodName() 
   * getSetterMethodName} 
   * method.  See also 
   * the {@link org.apache.xalan.processor.XSLTSchema 
   * XSLTSchema} class.</p>
   *
   * @param on True if indentation should be on
   */
  public void setIndent( boolean indent )
  {
    // System.out.println("setIndent( "+indent+" )");
    // setPreserveSpace(false);
    setIndent(indent);
  }

  /**
   * Sets the indentation amount. The document will not be
   * indented if the indentation is set to zero.
   * Calling {@link #setIndenting} will reset this
   * value to zero (off) or the default (on).
   *
   * @param indent The indentation, or zero
   */
  public void setIndent( int indent )
  {
    // System.out.println("setIndent( int indent )");
    if(m_shouldRecordHasBeenSet)
      m_indentHasBeenSet = true;
    // For the moment, there doesn't seem to be a way 
    // to set the indenting amount.
    // super.setIndent(indent);
  }

  /**
   * Sets the indentation on and off. When set on, the default
   * indentation level and default line wrapping is used
   * (see {@link #DEFAULT_INDENT} and {@link #DEFAULT_LINE_WIDTH}).
   * To specify a different indentation level or line wrapping,
   * use {@link #setIndent} and {@link #setLineWidth}.
   *
   * @param on True if indentation should be on
   */
  public void setIndenting( boolean on )
  {
    // System.out.println("setIndenting( "+on+" ), m_shouldRecordHasBeenSet: "+m_shouldRecordHasBeenSet);
    if(m_shouldRecordHasBeenSet)
      m_indentingHasBeenSet = true;
    setPreserveSpace(!on);
    super.setIndent(on);
  }

  /**
   * Sets the encoding for this output method. If no
   * encoding was specified, the default is always "UTF-8".
   * Make sure the encoding is compatible with the one
   * used by the {@link java.io.Writer}.
   *
   * @see #getEncoding
   * @param encoding The encoding, or null
   */
  public void setEncoding( String encoding )
  {
    if(m_shouldRecordHasBeenSet)
      m_encodingHasBeenSet = true;
    super.setEncoding(encoding);
  }


  /**
   * Sets the media type.
   *
   * @see #getMediaType
   * @param mediaType The specified media type
   */
  public void setMediaType( String mediaType )
  {
    if(m_shouldRecordHasBeenSet)
      m_mediaTypeHasBeenSet = true;
    super.setMediaType(mediaType);
  }

  /**
   * Sets XML declaration omitting on and off.
   *
   * @param omit True if XML declaration should be ommited
   */
  public void setOmitXMLDeclaration( boolean omit )
  {
    if(m_shouldRecordHasBeenSet)
      m_omitXmlDeclarationHasBeenSet = true;
    super.setOmitXMLDeclaration(omit);
  }

  /**
   * Sets document DTD standalone. The public and system
   * identifiers must be null for the document to be
   * serialized as standalone.
   *
   * @param standalone True if document DTD is standalone
   */
  public void setStandalone( boolean standalone )
  {
    if(m_shouldRecordHasBeenSet)
      m_standaloneHasBeenSet = true;
    super.setStandalone(standalone);
  }

  /**
   * Sets the list of elements for which text node children
   * should be output unescaped (no character references).
   *
   * @param nonEscapingElements List of unescaped element tag names
   */
  public void setNonEscapingElements( serialize.QName[] nonEscapingElements )
  {
    // TODO: Need to work on this.
    if(m_shouldRecordHasBeenSet)
      m_nonEscapingElementsHasBeenSet = true;
    super.setNonEscapingElements(nonEscapingElements);
  }

}
