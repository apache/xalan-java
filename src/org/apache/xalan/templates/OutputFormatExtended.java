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
package org.apache.xalan.templates;

import org.apache.xalan.serialize.OutputFormat;

import java.util.Vector;

import org.w3c.dom.Document;

import org.apache.xml.utils.QName;

/**
 * This class extends OutputFormat to act as a bean that
 * has methods that match the algorithmically generated
 * method signatures of the processor.  It also has flags
 * that tell if a given method has been set or not by the
 * stylesheet.
 */
public class OutputFormatExtended extends OutputFormat
	implements java.io.Serializable, RecomposableBase
{

  // Flag to tell us when to record that an attribute 
  // has been set.

  /** NEEDSDOC Field m_shouldRecordHasBeenSet          */
  private boolean m_shouldRecordHasBeenSet;

  /** NEEDSDOC Field m_methodHasBeenSet          */
  private boolean m_methodHasBeenSet = false;

  /**
   * NEEDSDOC Method methodHasBeenSet 
   *
   *
   * NEEDSDOC (methodHasBeenSet) @return
   */
  public boolean methodHasBeenSet()
  {
    return m_methodHasBeenSet;
  }

  /** NEEDSDOC Field m_versionHasBeenSet          */
  private boolean m_versionHasBeenSet = false;

  /**
   * NEEDSDOC Method versionHasBeenSet 
   *
   *
   * NEEDSDOC (versionHasBeenSet) @return
   */
  public boolean versionHasBeenSet()
  {
    return m_versionHasBeenSet;
  }

  /** NEEDSDOC Field m_indentHasBeenSet          */
  private boolean m_indentHasBeenSet = false;

  /**
   * NEEDSDOC Method indentHasBeenSet 
   *
   *
   * NEEDSDOC (indentHasBeenSet) @return
   */
  public boolean indentHasBeenSet()
  {
    return m_indentHasBeenSet;
  }

  /** NEEDSDOC Field m_encodingHasBeenSet          */
  private boolean m_encodingHasBeenSet = false;

  /**
   * NEEDSDOC Method encodingHasBeenSet 
   *
   *
   * NEEDSDOC (encodingHasBeenSet) @return
   */
  public boolean encodingHasBeenSet()
  {
    return m_encodingHasBeenSet;
  }

  /** NEEDSDOC Field m_mediaTypeHasBeenSet          */
  private boolean m_mediaTypeHasBeenSet;

  /**
   * NEEDSDOC Method mediaTypeHasBeenSet 
   *
   *
   * NEEDSDOC (mediaTypeHasBeenSet) @return
   */
  public boolean mediaTypeHasBeenSet()
  {
    return m_mediaTypeHasBeenSet;
  }

  /** NEEDSDOC Field m_doctypeSystemHasBeenSet          */
  private boolean m_doctypeSystemHasBeenSet;

  /**
   * NEEDSDOC Method doctypeSystemHasBeenSet 
   *
   *
   * NEEDSDOC (doctypeSystemHasBeenSet) @return
   */
  public boolean doctypeSystemHasBeenSet()
  {
    return m_doctypeSystemHasBeenSet;
  }

  /** NEEDSDOC Field m_doctypePublicHasBeenSet          */
  private boolean m_doctypePublicHasBeenSet;

  /**
   * NEEDSDOC Method doctypePublicHasBeenSet 
   *
   *
   * NEEDSDOC (doctypePublicHasBeenSet) @return
   */
  public boolean doctypePublicHasBeenSet()
  {
    return m_doctypePublicHasBeenSet;
  }

  /** NEEDSDOC Field m_omitXmlDeclarationHasBeenSet          */
  private boolean m_omitXmlDeclarationHasBeenSet = false;

  /**
   * NEEDSDOC Method omitXmlDeclarationHasBeenSet 
   *
   *
   * NEEDSDOC (omitXmlDeclarationHasBeenSet) @return
   */
  public boolean omitXmlDeclarationHasBeenSet()
  {
    return m_omitXmlDeclarationHasBeenSet;
  }

  /** NEEDSDOC Field m_standaloneHasBeenSet          */
  private boolean m_standaloneHasBeenSet = false;

  /**
   * NEEDSDOC Method standaloneHasBeenSet 
   *
   *
   * NEEDSDOC (standaloneHasBeenSet) @return
   */
  public boolean standaloneHasBeenSet()
  {
    return m_standaloneHasBeenSet;
  }

  /** NEEDSDOC Field m_cdataElementsHasBeenSet          */
  private boolean m_cdataElementsHasBeenSet = false;

  /**
   * NEEDSDOC Method cdataElementsHasBeenSet 
   *
   *
   * NEEDSDOC (cdataElementsHasBeenSet) @return
   */
  public boolean cdataElementsHasBeenSet()
  {
    return m_cdataElementsHasBeenSet;
  }

  /** NEEDSDOC Field m_nonEscapingElementsHasBeenSet          */
  private boolean m_nonEscapingElementsHasBeenSet = false;

  /**
   * NEEDSDOC Method nonEscapingElementsHasBeenSet 
   *
   *
   * NEEDSDOC (nonEscapingElementsHasBeenSet) @return
   */
  public boolean nonEscapingElementsHasBeenSet()
  {
    return m_nonEscapingElementsHasBeenSet;
  }

  /**
   * Constructs a new output format with the default values.
   */
  public OutputFormatExtended(int docOrderNumber)
  {

    super();

    m_shouldRecordHasBeenSet = true;

    setPreserveSpace(true);

    m_docOrderNumber = docOrderNumber;
  }

  /**
   *  The document order number, analogous to the same field in an ElemTemplateElement.
   */
  protected int m_docOrderNumber;

  /**
   * Get the UID (document order index).
   *
   * @return Index of this child
   */
  public int getUid()
  {
    return m_docOrderNumber;
  }

  /**
   * NEEDSDOC Method copyFrom 
   *
   *
   * NEEDSDOC @param of
   */
  void copyFrom(OutputFormatExtended of)
  {

    setPreserveSpace(true);

    if (of.methodHasBeenSet())
      setMethod(of.getMethod());
    else
      super.setMethod(of.getMethod());

    if (of.cdataElementsHasBeenSet())
      setCDataElements(of.getCDataElements());
    else
      super.setCDataElements(of.getCDataElements());

    if (of.doctypePublicHasBeenSet())
      setDoctypePublicId(of.getDoctypePublicId());
    else
      super.setDoctypePublicId(of.getDoctypePublicId());

    if (of.doctypeSystemHasBeenSet())
      setDoctypeSystemId(of.getDoctypeSystemId());
    else
      super.setDoctypeSystemId(of.getDoctypeSystemId());

    if (of.encodingHasBeenSet())
      setEncoding(of.getEncoding());
    else
      super.setEncoding(of.getEncoding());

    boolean indent = of.getIndent();

    if (of.indentHasBeenSet())
    {
      setIndent(indent);
      setPreserveSpace(!indent);
    }
    else
    {
      super.setIndent(indent);
      super.setPreserveSpace(!indent);
    }

    if (of.mediaTypeHasBeenSet())
      setMediaType(of.getMediaType());
    else
      super.setMediaType(of.getMediaType());

    if (of.nonEscapingElementsHasBeenSet())
      setNonEscapingElements(of.getNonEscapingElements());
    else
      super.setNonEscapingElements(of.getNonEscapingElements());

    if (of.omitXmlDeclarationHasBeenSet())
      setOmitXMLDeclaration(of.getOmitXMLDeclaration());
    else
      super.setOmitXMLDeclaration(of.getOmitXMLDeclaration());

    if (of.standaloneHasBeenSet())
      setStandalone(of.getStandalone());
    else
      super.setStandalone(of.getStandalone());

    if (of.versionHasBeenSet())
      setVersion(of.getVersion());
    else
      super.setVersion(of.getVersion());
  }

  /**
   * NEEDSDOC Method copyFrom 
   *
   *
   * NEEDSDOC @param of
   */
  void copyFrom(OutputFormat of)
  {

    setPreserveSpace(true);
    setCDataElements(of.getCDataElements());
    setDoctypePublicId(of.getDoctypePublicId());

    // setDoctype(of.getDoctypePublic(), of.getDoctypeSystem());
    setEncoding(of.getEncoding());

    // System.out.println("getOutputFormat - of.getIndent(): "+ of.getIndent());
    // setIndent(of.getIndent());
    setIndent(of.getIndent());

    // setLineSeparator(of.getLineSeparator());
    // setLineWidth(of.getLineWidth());
    setMediaType(of.getMediaType());
    setMethod(of.getMethod());
    setNonEscapingElements(of.getNonEscapingElements());
    setOmitXMLDeclaration(of.getOmitXMLDeclaration());
    setPreserveSpace(of.getPreserveSpace());
    setStandalone(of.getStandalone());
    setVersion(of.getVersion());
  }

  /**
   * The doctype-public attribute.
   *
   * NEEDSDOC @param publicId
   */
  public void setDoctypePublic(String publicId)
  {

    if (m_shouldRecordHasBeenSet)
      m_doctypePublicHasBeenSet = true;

    super.setDoctypePublicId(publicId);

    // super.setDoctype(publicId, this.getDoctypeSystem());
  }

  /**
   * The doctype-system attribute.
   *
   * NEEDSDOC @param systemId
   */
  public void setDoctypeSystem(String systemId)
  {

    if (m_shouldRecordHasBeenSet)
      m_doctypeSystemHasBeenSet = true;

    // super.setDoctype(this.getDoctypePublic(), systemId);
    super.setDoctypeSystemId(systemId);
  }

  /**
   * The omit-xml-declaration attribute.
   *
   * NEEDSDOC @param omit
   */
  public void setOmitXmlDeclaration(boolean omit)
  {

    if (m_shouldRecordHasBeenSet)
      m_omitXmlDeclarationHasBeenSet = true;

    super.setOmitXMLDeclaration(omit);
  }

  /**
   * The cdata-section-elements attribute.
   *
   * NEEDSDOC @param elements
   */
  public void setCdataSectionElements(Vector elements)
  {

    if (m_shouldRecordHasBeenSet)
      m_cdataElementsHasBeenSet = true;

    int n = elements.size();
    org.apache.xml.utils.QName[] qnames = new QName[n];

    for (int i = 0; i < n; i++)
    {
      qnames[i] = (QName) elements.elementAt(i);
    }

    super.setCDataElements(qnames);
  }

  /**
   * The cdata-section-elements attribute.
   *
   * NEEDSDOC @param elements
   */
  public void setCdataSectionElements(org.apache.xml.utils.QName[] elements)
  {

    if (m_shouldRecordHasBeenSet)
      m_cdataElementsHasBeenSet = true;

    super.setCDataElements(elements);
  }

  /**
   * Sets the method for this output format.
   *
   * @see #getMethod
   * @param method The output method, or null
   */
  public void setMethod(String method)
  {

    // System.out.println("Setting the method to: "+method);
    if (m_shouldRecordHasBeenSet)
      m_methodHasBeenSet = true;

    super.setMethod(method);

    if ((null != method) && method.equalsIgnoreCase("html"))
    {

      // System.out.println("m_indentHasBeenSet: "+m_indentHasBeenSet);
      if (!this.m_indentHasBeenSet)
      {

        // System.out.println("Setting indent to true");
        setIndent(true);

        this.m_indentHasBeenSet = false;
      }
    }
  }

  /**
   * Sets the method for this output format.
   *
   * @see #getMethod
   * @param method The output method, or null
   */
  public void setMethod(QName method)
  {

    if (m_shouldRecordHasBeenSet)
      m_methodHasBeenSet = true;

    // TODO: Work with Assaf on this.
    String meth = method.getLocalPart();

    super.setMethod(meth);
    ;

    if ((null != method) && (method.getNamespaceURI() == null)
            && method.getLocalName().equalsIgnoreCase("html"))
    {

      // System.out.println("m_indentHasBeenSet: "+m_indentHasBeenSet);
      if (!this.m_indentHasBeenSet)
      {

        // System.out.println("Setting indent to true");
        setIndent(true);

        this.m_indentHasBeenSet = false;
      }
    }
  }

  /**
   * Sets the version for this output method.
   * For XML the value would be "1.0", for HTML
   * it would be "4.0".
   *
   * @see #getVersion
   * @param version The output method version, or null
   */
  public void setVersion(String version)
  {

    if (m_shouldRecordHasBeenSet)
      m_versionHasBeenSet = true;

    super.setVersion(version);
    ;
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
   *
   * NEEDSDOC @param indent
   */
  public void setIndent(boolean indent)
  {

    if (m_shouldRecordHasBeenSet)
      m_indentHasBeenSet = true;

    // System.out.println("setIndent( "+indent+" )");
    setPreserveSpace(!indent);
    super.setIndent(indent);
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
  public void setEncoding(String encoding)
  {

    if (m_shouldRecordHasBeenSet)
      m_encodingHasBeenSet = true;

    super.setEncoding(encoding);
  }

  /**
   * Sets the media type.
   *
   * @see #getMediaType
   * @param mediaType The specified media type
   */
  public void setMediaType(String mediaType)
  {

    if (m_shouldRecordHasBeenSet)
      m_mediaTypeHasBeenSet = true;

    super.setMediaType(mediaType);
  }

  /**
   * Sets XML declaration omitting on and off.
   *
   * @param omit True if XML declaration should be ommited
   */
  public void setOmitXMLDeclaration(boolean omit)
  {

    if (m_shouldRecordHasBeenSet)
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
  public void setStandalone(boolean standalone)
  {

    if (m_shouldRecordHasBeenSet)
      m_standaloneHasBeenSet = true;

    super.setStandalone(standalone);
  }

  /**
   * Sets the list of elements for which text node children
   * should be output unescaped (no character references).
   *
   * @param nonEscapingElements List of unescaped element tag names
   */
  public void setNonEscapingElements(
          org.apache.xml.utils.QName[] nonEscapingElements)
  {

    // TODO: Need to work on this.
    if (m_shouldRecordHasBeenSet)
      m_nonEscapingElementsHasBeenSet = true;

    super.setNonEscapingElements(nonEscapingElements);
  }

  /**
   * This function is called to recompose all of the output format extended elements.
   */
  public void recompose(StylesheetRoot root)
  {
    root.recomposeOutput(this);
  }

}
