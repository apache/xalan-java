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

// Java imports
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import java.text.DecimalFormatSymbols;

import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

// Xalan imports
import org.apache.xml.utils.SystemIDResolver;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.StringVector;
import org.apache.xpath.XPath;

// DOM Imports
import org.w3c.dom.Node;
import org.w3c.dom.Document;

// SAX2 Imports
import javax.xml.transform.TransformerException;
import org.xml.sax.Locator;

import javax.xml.transform.SourceLocator;

/**
 * Represents a stylesheet element.
 * <p>All properties in this class have a fixed form of bean-style property
 * accessors for all properties that represent XSL attributes or elements.
 * These properties have setter method names accessed generically by the
 * processor, and so these names must be fixed according to the system
 * defined in the <a href="XSLTAttributeDef#getSetterMethodName">getSetterMethodName</a>
 * function.</p>
 * <p><pre>
 * <!ENTITY % top-level "
 *  (xsl:import*,
 *   (xsl:include
 *   | xsl:strip-space
 *   | xsl:preserve-space
 *   | xsl:output
 *   | xsl:key
 *   | xsl:decimal-format
 *   | xsl:attribute-set
 *   | xsl:variable
 *   | xsl:param
 *   | xsl:template
 *   | xsl:namespace-alias
 *   %non-xsl-top-level;)*)
 * ">
 *
 * <!ENTITY % top-level-atts '
 *   extension-element-prefixes CDATA #IMPLIED
 *   exclude-result-prefixes CDATA #IMPLIED
 *   id ID #IMPLIED
 *   version NMTOKEN #REQUIRED
 *   xmlns:xsl CDATA #FIXED "http://www.w3.org/1999/XSL/Transform"
 *   %space-att;
 * '>
 *
 * <!ELEMENT xsl:stylesheet %top-level;>
 * <!ATTLIST xsl:stylesheet %top-level-atts;>
 *
 * <!ELEMENT xsl:transform %top-level;>
 * <!ATTLIST xsl:transform %top-level-atts;>
 *
 * </p></pre>
 * @see <a href="http://www.w3.org/TR/xslt#section-Stylesheet-Structure">section-Stylesheet-Structure in XSLT Specification</a>
 */
public class Stylesheet extends ElemTemplateElement
        implements java.io.Serializable, Document
{

  /**
   * Constructor for a Stylesheet.
   * @param parent  The including or importing stylesheet.
   */
  public Stylesheet(Stylesheet parent)
  {

    if (null != parent)
    {
      m_stylesheetParent = parent;
      m_stylesheetRoot = parent.getStylesheetRoot();
    }
  }

  /**
   * Get the owning stylesheet.  This looks up the
   * inheritance chain until it calls getStylesheet
   * on a Stylesheet object, which will return itself.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Stylesheet getStylesheet()
  {
    return this;
  }

  /**
   * Tell if this can be cast to a StylesheetComposed, meaning, you
   * can ask questions from getXXXComposed functions.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public boolean isAggregatedType()
  {
    return false;
  }

  /**
   * Tell if this is the root of the stylesheet tree.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public boolean isRoot()
  {
    return false;
  }

  /**
   * Extension to be used when serializing to disk.
   */
  public static final String STYLESHEET_EXT = ".lxc";

  /**
   * Read the stylesheet from a serialization stream.
   *
   * NEEDSDOC @param stream
   *
   * @throws IOException
   * @throws TransformerException
   */
  private void readObject(ObjectInputStream stream)
          throws IOException, TransformerException
  {

    // System.out.println("Reading Stylesheet");
    try
    {
      stream.defaultReadObject();
    }
    catch (ClassNotFoundException cnfe)
    {
      throw new TransformerException(cnfe);
    }

    // System.out.println("Done reading Stylesheet");
  }

  /**
   * NEEDSDOC Method writeObject 
   *
   *
   * NEEDSDOC @param stream
   *
   * @throws IOException
   */
  private void writeObject(ObjectOutputStream stream) throws IOException
  {

    // System.out.println("Writing Stylesheet");
    stream.defaultWriteObject();

    // System.out.println("Done writing Stylesheet");
  }

  //============== XSLT Properties =================

  /**
   * The "xmlns:xsl" property.
   */
  private String m_XmlnsXsl;

  /**
   * Set the "xmlns:xsl" property.
   * @see <a href="http://www.w3.org/TR/xslt#xslt-namespace">xslt-namespace in XSLT Specification</a>
   *
   * NEEDSDOC @param v
   */
  public void setXmlnsXsl(String v)
  {
    m_XmlnsXsl = v;
  }

  /**
   * Get the "xmlns:xsl" property.
   * @see <a href="http://www.w3.org/TR/xslt#xslt-namespace">xslt-namespace in XSLT Specification</a>
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getXmlnsXsl()
  {
    return m_XmlnsXsl;
  }

  /**
   * The "extension-element-prefixes" property, actually contains URIs.
   */
  private StringVector m_ExtensionElementURIs;

  /**
   * Set the "extension-element-prefixes" property.
   * @see <a href="http://www.w3.org/TR/xslt#extension-element">extension-element in XSLT Specification</a>
   *
   * NEEDSDOC @param v
   */
  public void setExtensionElementPrefixes(StringVector v)
  {
    m_ExtensionElementURIs = v;
  }

  /**
   * Get and "extension-element-prefix" property.
   * @see <a href="http://www.w3.org/TR/xslt#extension-element">extension-element in XSLT Specification</a>
   *
   * NEEDSDOC @param i
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws ArrayIndexOutOfBoundsException
   */
  public String getExtensionElementPrefix(int i)
          throws ArrayIndexOutOfBoundsException
  {

    if (null == m_ExtensionElementURIs)
      throw new ArrayIndexOutOfBoundsException();

    return m_ExtensionElementURIs.elementAt(i);
  }

  /**
   * Get the number of "extension-element-prefixes" Strings.
   * @see <a href="http://www.w3.org/TR/xslt#extension-element">extension-element in XSLT Specification</a>
   *
   * NEEDSDOC ($objectName$) @return
   */
  public int getExtensionElementPrefixCount()
  {
    return (null != m_ExtensionElementURIs)
           ? m_ExtensionElementURIs.size() : 0;
  }

  /**
   * Get and "extension-element-prefix" property.
   * @see <a href="http://www.w3.org/TR/xslt#extension-element">extension-element in XSLT Specification</a>
   *
   * NEEDSDOC @param uri
   *
   * NEEDSDOC ($objectName$) @return
   */
  public boolean containsExtensionElementURI(String uri)
  {

    if (null == m_ExtensionElementURIs)
      return false;

    return m_ExtensionElementURIs.contains(uri);
  }

  /**
   * The "exclude-result-prefixes" property.
   */
  private StringVector m_ExcludeResultPrefixs;

  /**
   * Set the "exclude-result-prefixes" property.
   * The designation of a namespace as an excluded namespace is
   * effective within the subtree of the stylesheet rooted at
   * the element bearing the exclude-result-prefixes or
   * xsl:exclude-result-prefixes attribute; a subtree rooted
   * at an xsl:stylesheet element does not include any stylesheets
   * imported or included by children of that xsl:stylesheet element.
   * @see <a href="http://www.w3.org/TR/xslt#literal-result-element">literal-result-element in XSLT Specification</a>
   *
   * NEEDSDOC @param v
   */
  public void setExcludeResultPrefixes(StringVector v)
  {
    m_ExcludeResultPrefixs = v;
  }

  /**
   * Get an "exclude-result-prefix" property.
   * The designation of a namespace as an excluded namespace is
   * effective within the subtree of the stylesheet rooted at
   * the element bearing the exclude-result-prefixes or
   * xsl:exclude-result-prefixes attribute; a subtree rooted
   * at an xsl:stylesheet element does not include any stylesheets
   * imported or included by children of that xsl:stylesheet element.
   * @see <a href="http://www.w3.org/TR/xslt#literal-result-element">literal-result-element in XSLT Specification</a>
   *
   * NEEDSDOC @param i
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws ArrayIndexOutOfBoundsException
   */
  public String getExcludeResultPrefix(int i)
          throws ArrayIndexOutOfBoundsException
  {

    if (null == m_ExcludeResultPrefixs)
      throw new ArrayIndexOutOfBoundsException();

    return m_ExcludeResultPrefixs.elementAt(i);
  }

  /**
   * Get the number of "extension-element-prefixes" Strings.
   * @see <a href="http://www.w3.org/TR/xslt#extension-element">extension-element in XSLT Specification</a>
   *
   * NEEDSDOC ($objectName$) @return
   */
  public int getExcludeResultPrefixCount()
  {
    return (null != m_ExcludeResultPrefixs)
           ? m_ExcludeResultPrefixs.size() : 0;
  }

  /**
   * Get whether or not the passed URL is contained flagged by
   * the "extension-element-prefixes" property.
   * @see <a href="http://www.w3.org/TR/xslt#extension-element">extension-element in XSLT Specification</a>
   *
   * @param prefix non-null reference to prefix that might be excluded.
   *
   * @return true if the prefix should normally be excluded.
   */
  public boolean containsExcludeResultPrefix(String prefix)
  {

    if (null == m_ExcludeResultPrefixs)
      return false;

    if (prefix.length() == 0)
      prefix = Constants.ATTRVAL_DEFAULT_PREFIX;

    return m_ExcludeResultPrefixs.contains(prefix);
  }

  /**
   * The "id" property.
   */
  private String m_Id;

  /**
   * Set the "id" property.
   * @see <a href="http://www.w3.org/TR/xslt#section-Embedding-Stylesheets">section-Embedding-Stylesheets in XSLT Specification</a>
   *
   * NEEDSDOC @param v
   */
  public void setId(String v)
  {
    m_Id = v;
  }

  /**
   * Get the "id" property.
   * @see <a href="http://www.w3.org/TR/xslt#section-Embedding-Stylesheets">section-Embedding-Stylesheets in XSLT Specification</a>
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getId()
  {
    return m_Id;
  }

  /**
   * The "version" property.
   */
  private String m_Version;

  /**
   * Set the "version" property.
   * @see <a href="http://www.w3.org/TR/xslt#forwards">forwards in XSLT Specification</a>
   *
   * NEEDSDOC @param v
   */
  public void setVersion(String v)
  {
    m_Version = v;
  }

  /**
   * Get the "version" property.
   * @see <a href="http://www.w3.org/TR/xslt#forwards">forwards in XSLT Specification</a>
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getVersion()
  {
    return m_Version;
  }

  /**
   * The "xsl:import" list.
   */
  private Vector m_imports;

  /**
   * Add a stylesheet to the "import" list.
   * @see <a href="http://www.w3.org/TR/xslt#import">import in XSLT Specification</a>
   *
   * NEEDSDOC @param v
   */
  public void setImport(StylesheetComposed v)
  {

    if (null == m_imports)
      m_imports = new Vector();

    // I'm going to insert the elements in backwards order,
    // so I can walk them 0 to n.
    m_imports.addElement(v);
  }

  /**
   * Get a stylesheet from the "import" list.
   * @see <a href="http://www.w3.org/TR/xslt#import">import in XSLT Specification</a>
   *
   * NEEDSDOC @param i
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws ArrayIndexOutOfBoundsException
   */
  public StylesheetComposed getImport(int i)
          throws ArrayIndexOutOfBoundsException
  {

    if (null == m_imports)
      throw new ArrayIndexOutOfBoundsException();

    return (StylesheetComposed) m_imports.elementAt(i);
  }

  /**
   * Get the number of imported stylesheets.
   * @see <a href="http://www.w3.org/TR/xslt#import">import in XSLT Specification</a>
   *
   * NEEDSDOC ($objectName$) @return
   */
  public int getImportCount()
  {
    return (null != m_imports) ? m_imports.size() : 0;
  }

  /**
   * The "xsl:include" properties.
   */
  private Vector m_includes;

  /**
   * Set a "xsl:include" property.
   * @see <a href="http://www.w3.org/TR/xslt#include">include in XSLT Specification</a>
   *
   * NEEDSDOC @param v
   */
  public void setInclude(Stylesheet v)
  {

    if (null == m_includes)
      m_includes = new Vector();

    m_includes.addElement(v);
  }

  /**
   * Get an "xsl:include" property.
   * @see <a href="http://www.w3.org/TR/xslt#include">include in XSLT Specification</a>
   *
   * NEEDSDOC @param i
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws ArrayIndexOutOfBoundsException
   */
  public Stylesheet getInclude(int i) throws ArrayIndexOutOfBoundsException
  {

    if (null == m_includes)
      throw new ArrayIndexOutOfBoundsException();

    return (Stylesheet) m_includes.elementAt(i);
  }

  /**
   * Get the number of included stylesheets.
   * @see <a href="http://www.w3.org/TR/xslt#import">import in XSLT Specification</a>
   *
   * NEEDSDOC ($objectName$) @return
   */
  public int getIncludeCount()
  {
    return (null != m_includes) ? m_includes.size() : 0;
  }

  /**
   * Table of tables of element decimal-format.
   * @see ElemDecimalFormat.
   */
  Stack m_DecimalFormatDeclarations;

  /**
   * Process the xsl:decimal-format element.
   *
   * NEEDSDOC @param edf
   */
  public void setDecimalFormat(DecimalFormatProperties edf)
  {

    if (null == m_DecimalFormatDeclarations)
      m_DecimalFormatDeclarations = new Stack();

    // Elements are pushed in by order of importance
    // so that when recomposed, they get overiden properly.
    m_DecimalFormatDeclarations.push(edf);
  }

  /**
   * Get an "xsl:decimal-format" property.
   * 
   * @see DecimalFormatProperties
   * @see <a href="http://www.w3.org/TR/xslt#format-number">format-number in XSLT Specification</a>
   *
   * @param name The qualified name of the decimal format property.
   * @return null if not found, otherwise a DecimalFormatProperties
   * object, from which you can get a DecimalFormatSymbols object.
   */
  public DecimalFormatProperties getDecimalFormat(QName name)
  {

    if (null == m_DecimalFormatDeclarations)
      return null;

    int n = getDecimalFormatCount();

    for (int i = (n - 1); i >= 0; i++)
    {
      DecimalFormatProperties dfp = getDecimalFormat(i);

      if (dfp.getName().equals(name))
        return dfp;
    }

    return null;
  }

  /**
   * Get an "xsl:decimal-format" property.
   * @see <a href="http://www.w3.org/TR/xslt#format-number">format-number in XSLT Specification</a>
   * @see ElemDecimalFormat.
   *
   * NEEDSDOC @param i
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws ArrayIndexOutOfBoundsException
   */
  public DecimalFormatProperties getDecimalFormat(int i)
          throws ArrayIndexOutOfBoundsException
  {

    if (null == m_DecimalFormatDeclarations)
      throw new ArrayIndexOutOfBoundsException();

    return (DecimalFormatProperties) m_DecimalFormatDeclarations.elementAt(i);
  }

  /**
   * Get the number of xsl:decimal-format declarations.
   * @see ElemDecimalFormat.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public int getDecimalFormatCount()
  {
    return (null != m_DecimalFormatDeclarations)
           ? m_DecimalFormatDeclarations.size() : 0;
  }

  /**
   * The "xsl:strip-space" properties,
   * A lookup table of all space stripping elements.
   */
  private Vector m_whitespaceStrippingElements;

  /**
   * Set the "xsl:strip-space" properties.
   * @see <a href="http://www.w3.org/TR/xslt#strip">strip in XSLT Specification</a>
   *
   * NEEDSDOC @param v
   */
  public void setStripSpaces(WhiteSpaceInfo wsi)
  {

    if (null == m_whitespaceStrippingElements)
    {
      m_whitespaceStrippingElements = new Vector();
    }

    m_whitespaceStrippingElements.addElement(wsi);
  }

  /**
   * Get an "xsl:strip-space" property.
   * @see <a href="http://www.w3.org/TR/xslt#strip">strip in XSLT Specification</a>
   *
   * NEEDSDOC @param i
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws ArrayIndexOutOfBoundsException
   */
  public WhiteSpaceInfo getStripSpace(int i) throws ArrayIndexOutOfBoundsException
  {

    if (null == m_whitespaceStrippingElements)
      throw new ArrayIndexOutOfBoundsException();

    return (WhiteSpaceInfo) m_whitespaceStrippingElements.elementAt(i);
  }

  /**
   * Get the number of "xsl:strip-space" properties.
   * @see <a href="http://www.w3.org/TR/xslt#strip">strip in XSLT Specification</a>
   *
   * NEEDSDOC ($objectName$) @return
   */
  public int getStripSpaceCount()
  {
    return (null != m_whitespaceStrippingElements)
           ? m_whitespaceStrippingElements.size() : 0;
  }

  /**
   * The "xsl:preserve-space" property,
   * A lookup table of all space preserving elements.
   */
  private Vector m_whitespacePreservingElements;

  /**
   * Set the "xsl:preserve-space" property.
   * @see <a href="http://www.w3.org/TR/xslt#strip">strip in XSLT Specification</a>
   *
   * NEEDSDOC @param v
   */
  public void setPreserveSpaces(WhiteSpaceInfo wsi)
  {

    if (null == m_whitespacePreservingElements)
    {
      m_whitespacePreservingElements = new Vector();
    }

    m_whitespacePreservingElements.addElement(wsi);
  }

  /**
   * Get a "xsl:preserve-space" property.
   * @see <a href="http://www.w3.org/TR/xslt#strip">strip in XSLT Specification</a>
   *
   * NEEDSDOC @param i
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws ArrayIndexOutOfBoundsException
   */
  public WhiteSpaceInfo getPreserveSpace(int i) throws ArrayIndexOutOfBoundsException
  {

    if (null == m_whitespacePreservingElements)
      throw new ArrayIndexOutOfBoundsException();

    return (WhiteSpaceInfo) m_whitespacePreservingElements.elementAt(i);
  }

  /**
   * Get the number of "xsl:preserve-space" properties.
   * @see <a href="http://www.w3.org/TR/xslt#strip">strip in XSLT Specification</a>
   *
   * NEEDSDOC ($objectName$) @return
   */
  public int getPreserveSpaceCount()
  {
    return (null != m_whitespacePreservingElements)
           ? m_whitespacePreservingElements.size() : 0;
  }

  /**
   * The "xsl:output" properties.  This is a vector of OutputFormatExtended objects.
   */
  private Vector m_output;

  /**
   * Set the "xsl:output" property.
   * @see <a href="http://www.w3.org/TR/xslt#output">output in XSLT Specification</a>
   *
   * NEEDSDOC @param v
   */
  public void setOutput(OutputFormatExtended v)
  {
    if (null == m_output)
    {
      m_output = new Vector();
    }

    m_output.addElement(v);
  }

  /**
   * Get an "xsl:output" property.
   * @see <a href="http://www.w3.org/TR/xslt#output">output in XSLT Specification</a>
   *
   * NEEDSDOC @param i
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws ArrayIndexOutOfBoundsException
   */
  public OutputFormatExtended getOutput(int i) throws ArrayIndexOutOfBoundsException
  {

    if (null == m_output)
      throw new ArrayIndexOutOfBoundsException();

    return (OutputFormatExtended) m_output.elementAt(i);
  }

  /**
   * Get the number of "xsl:output" properties.
   * @see <a href="http://www.w3.org/TR/xslt#output">output in XSLT Specification</a>
   *
   * NEEDSDOC ($objectName$) @return
   */
  public int getOutputCount()
  {
    return (null != m_output)
           ? m_output.size() : 0;
  }

  /**
   * The "xsl:key" property.
   */
  private Vector m_keyDeclarations;

  /**
   * Set the "xsl:key" property.
   * @see <a href="http://www.w3.org/TR/xslt#key">key in XSLT Specification</a>
   *
   * NEEDSDOC @param v
   */
  public void setKey(KeyDeclaration v)
  {

    if (null == m_keyDeclarations)
      m_keyDeclarations = new Vector();

    m_keyDeclarations.addElement(v);
  }

  /**
   * Get an "xsl:key" property.
   * @see <a href="http://www.w3.org/TR/xslt#key">key in XSLT Specification</a>
   *
   * NEEDSDOC @param i
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws ArrayIndexOutOfBoundsException
   */
  public KeyDeclaration getKey(int i) throws ArrayIndexOutOfBoundsException
  {

    if (null == m_keyDeclarations)
      throw new ArrayIndexOutOfBoundsException();

    return (KeyDeclaration) m_keyDeclarations.elementAt(i);
  }

  /**
   * Get the number of "xsl:key" properties.
   * @see <a href="http://www.w3.org/TR/xslt#key">key in XSLT Specification</a>
   *
   * NEEDSDOC ($objectName$) @return
   */
  public int getKeyCount()
  {
    return (null != m_keyDeclarations) ? m_keyDeclarations.size() : 0;
  }

  /**
   * The "xsl:attribute-set" property.
   */
  private Vector m_attributeSets;

  /**
   * Set the "xsl:attribute-set" property.
   * @see <a href="http://www.w3.org/TR/xslt#attribute-sets">attribute-sets in XSLT Specification</a>
   *
   * NEEDSDOC @param attrSet
   */
  public void setAttributeSet(ElemAttributeSet attrSet)
  {

    if (null == m_attributeSets)
    {
      m_attributeSets = new Vector();
    }

    m_attributeSets.addElement(attrSet);
  }

  /**
   * Get an "xsl:attribute-set" property.
   * @see <a href="http://www.w3.org/TR/xslt#attribute-sets">attribute-sets in XSLT Specification</a>
   *
   * NEEDSDOC @param i
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws ArrayIndexOutOfBoundsException
   */
  public ElemAttributeSet getAttributeSet(int i)
          throws ArrayIndexOutOfBoundsException
  {

    if (null == m_attributeSets)
      throw new ArrayIndexOutOfBoundsException();

    return (ElemAttributeSet) m_attributeSets.elementAt(i);
  }

  /**
   * Get the number of "xsl:attribute-set" properties.
   * @see <a href="http://www.w3.org/TR/xslt#attribute-sets">attribute-sets in XSLT Specification</a>
   *
   * NEEDSDOC ($objectName$) @return
   */
  public int getAttributeSetCount()
  {
    return (null != m_attributeSets) ? m_attributeSets.size() : 0;
  }

  /**
   * The "xsl:variable" and "xsl:param" properties.
   */
  private Vector m_topLevelVariables;

  /**
   * Set the "xsl:variable" property.
   * @see <a href="http://www.w3.org/TR/xslt#top-level-variables">top-level-variables in XSLT Specification</a>
   *
   * NEEDSDOC @param v
   */
  public void setVariable(ElemVariable v)
  {

    if (null == m_topLevelVariables)
      m_topLevelVariables = new Vector();

    m_topLevelVariables.addElement(v);
  }
  
  /**
   * Get an "xsl:variable" or "xsl:param" property.
   * @see <a href="http://www.w3.org/TR/xslt#top-level-variables">top-level-variables in XSLT Specification</a>
   *
   * NEEDSDOC @param qname
   *
   * NEEDSDOC ($objectName$) @return
   */
  public ElemVariable getVariableOrParam(QName qname)
  {

    if (null != m_topLevelVariables)
    {
      int n = getVariableOrParamCount();

      for (int i = 0; i < n; i++)
      {
        ElemVariable var = (ElemVariable) getVariableOrParam(i);

        if (var.getName().equals(qname))
          return var;
      }
    }

    return null;
  }


  /**
   * Get an "xsl:variable" property.
   * @see <a href="http://www.w3.org/TR/xslt#top-level-variables">top-level-variables in XSLT Specification</a>
   *
   * NEEDSDOC @param qname
   *
   * NEEDSDOC ($objectName$) @return
   */
  public ElemVariable getVariable(QName qname)
  {

    if (null != m_topLevelVariables)
    {
      int n = getVariableOrParamCount();

      for (int i = 0; i < n; i++)
      {
        ElemVariable var = getVariableOrParam(i);
        if((var.getXSLToken() == Constants.ELEMNAME_VARIABLE) &&
           (var.getName().equals(qname)))
          return var;
      }
    }

    return null;
  }

  /**
   * Get an "xsl:variable" property.
   * @see <a href="http://www.w3.org/TR/xslt#top-level-variables">top-level-variables in XSLT Specification</a>
   *
   * NEEDSDOC @param i
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws ArrayIndexOutOfBoundsException
   */
  public ElemVariable getVariableOrParam(int i) throws ArrayIndexOutOfBoundsException
  {

    if (null == m_topLevelVariables)
      throw new ArrayIndexOutOfBoundsException();

    return (ElemVariable) m_topLevelVariables.elementAt(i);
  }

  /**
   * Get the number of "xsl:variable" properties.
   * @see <a href="http://www.w3.org/TR/xslt#top-level-variables">top-level-variables in XSLT Specification</a>
   *
   * NEEDSDOC ($objectName$) @return
   */
  public int getVariableOrParamCount()
  {
    return (null != m_topLevelVariables) ? m_topLevelVariables.size() : 0;
  }

  /**
   * Set an "xsl:param" property.
   * @see <a href="http://www.w3.org/TR/xslt#top-level-variables">top-level-variables in XSLT Specification</a>
   *
   * @param v A non-null ElemParam reference.
   */
  public void setParam(ElemParam v)
  {
    setVariable(v);
  }

  /**
   * Get an "xsl:param" property.
   * @see <a href="http://www.w3.org/TR/xslt#top-level-variables">top-level-variables in XSLT Specification</a>
   *
   * NEEDSDOC @param qname
   *
   * NEEDSDOC ($objectName$) @return
   */
  public ElemParam getParam(QName qname)
  {

    if (null != m_topLevelVariables)
    {
      int n = getVariableOrParamCount();

      for (int i = 0; i < n; i++)
      {
        ElemVariable var = getVariableOrParam(i);
        if((var.getXSLToken() == Constants.ELEMNAME_PARAMVARIABLE) &&
           (var.getName().equals(qname)))
          return (ElemParam)var;
      }
    }

    return null;
  }

  /**
   * The "xsl:template" properties.
   */
  private Vector m_templates;

  /**
   * Set an "xsl:template" property.
   * @see <a href="http://www.w3.org/TR/xslt#section-Defining-Template-Rules">section-Defining-Template-Rules in XSLT Specification</a>
   *
   * NEEDSDOC @param v
   */
  public void setTemplate(ElemTemplate v)
  {

    if (null == m_templates)
      m_templates = new Vector();

    m_templates.addElement(v);
    v.setStylesheet(this);
  }

  /**
   * Get an "xsl:template" property.
   * @see <a href="http://www.w3.org/TR/xslt#section-Defining-Template-Rules">section-Defining-Template-Rules in XSLT Specification</a>
   *
   * NEEDSDOC @param i
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws TransformerException
   */
  public ElemTemplate getTemplate(int i) throws TransformerException
  {

    if (null == m_templates)
      throw new ArrayIndexOutOfBoundsException();

    return (ElemTemplate) m_templates.elementAt(i);
  }

  /**
   * Get the number of "xsl:template" properties.
   * @see <a href="http://www.w3.org/TR/xslt#section-Defining-Template-Rules">section-Defining-Template-Rules in XSLT Specification</a>
   *
   * NEEDSDOC ($objectName$) @return
   */
  public int getTemplateCount()
  {
    return (null != m_templates) ? m_templates.size() : 0;
  }

  /**
   * The "xsl:namespace-alias" properties.
   */
  private Vector m_prefix_aliases;

  /**
   * Set the "xsl:namespace-alias" property.
   * @see <a href="http://www.w3.org/TR/xslt#literal-result-element">literal-result-element in XSLT Specification</a>
   *
   * NEEDSDOC @param na
   */
  public void setNamespaceAlias(NamespaceAlias na)
  {

    if (m_prefix_aliases == null)
      m_prefix_aliases = new Vector();

    m_prefix_aliases.addElement(na);
  }

  /**
   * Get an "xsl:variable" property.
   * @see <a href="http://www.w3.org/TR/xslt#top-level-variables">top-level-variables in XSLT Specification</a>
   *
   * NEEDSDOC @param i
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws ArrayIndexOutOfBoundsException
   */
  public NamespaceAlias getNamespaceAlias(int i)
          throws ArrayIndexOutOfBoundsException
  {

    if (null == m_prefix_aliases)
      throw new ArrayIndexOutOfBoundsException();

    return (NamespaceAlias) m_prefix_aliases.elementAt(i);
  }

  /**
   * Get the number of "xsl:variable" properties.
   * @see <a href="http://www.w3.org/TR/xslt#top-level-variables">top-level-variables in XSLT Specification</a>
   *
   * NEEDSDOC ($objectName$) @return
   */
  public int getNamespaceAliasCount()
  {
    return (null != m_prefix_aliases) ? m_prefix_aliases.size() : 0;
  }

  /**
   * The "non-xsl-top-level" properties.
   */
  private Hashtable m_NonXslTopLevel;

  /**
   * Set a found non-xslt element.
   * @see <a href="http://www.w3.org/TR/xslt#stylesheet-element">stylesheet-element in XSLT Specification</a>
   *
   * NEEDSDOC @param name
   * NEEDSDOC @param obj
   */
  public void setNonXslTopLevel(QName name, Object obj)
  {

    if (null == m_NonXslTopLevel)
      m_NonXslTopLevel = new Hashtable();

    m_NonXslTopLevel.put(name, obj);
  }

  /**
   * Get a non-xslt element.
   * @see <a href="http://www.w3.org/TR/xslt#stylesheet-element">stylesheet-element in XSLT Specification</a>
   *
   * NEEDSDOC @param name
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Object getNonXslTopLevel(QName name)
  {
    return (null != m_NonXslTopLevel) ? m_NonXslTopLevel.get(name) : null;
  }

  // =========== End top-level XSLT properties ===========

  /**
   * The base URL of the XSL document.
   * @serial
   */
  private String m_href = null;

  /** NEEDSDOC Field m_publicId          */
  private String m_publicId;

  /** NEEDSDOC Field m_systemId          */
  private String m_systemId;

  /**
   * Get the base identifier with which this stylesheet is associated.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getHref()
  {
    return m_href;
  }

  /**
   * Get the base identifier with which this stylesheet is associated.
   *
   * NEEDSDOC @param baseIdent
   */
  public void setHref(String baseIdent)
  {
    m_href = baseIdent;
  }

  /**
   * Set the location information for this element.
   *
   * NEEDSDOC @param locator
   */
  public void setLocaterInfo(SourceLocator locator)
  {

    if (null != locator)
    {
      m_publicId = locator.getPublicId();
      m_systemId = locator.getSystemId();

      if (null != m_systemId)
      {
        try
        {
          m_href = SystemIDResolver.getAbsoluteURI(m_systemId, null);
        }
        catch (TransformerException se)
        {

          // Ignore this for right now
        }
      }

      super.setLocaterInfo(locator);
    }
  }

  /**
   * The root of the stylesheet, where all the tables common
   * to all stylesheets are kept.
   * @serial
   */
  private StylesheetRoot m_stylesheetRoot;

  /**
   * Get the root of the stylesheet, where all the tables common
   * to all stylesheets are kept.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public StylesheetRoot getStylesheetRoot()
  {
    return m_stylesheetRoot;
  }

  /**
   * Set the root of the stylesheet, where all the tables common
   * to all stylesheets are kept.
   *
   * NEEDSDOC @param v
   */
  public void setStylesheetRoot(StylesheetRoot v)
  {
    m_stylesheetRoot = v;
  }

  /**
   * The parent of the stylesheet.  This will be null if this
   * is the root stylesheet.
   * @serial
   */
  private Stylesheet m_stylesheetParent;

  /**
   * Get the parent of the stylesheet.  This will be null if this
   * is the root stylesheet.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Stylesheet getStylesheetParent()
  {
    return m_stylesheetParent;
  }

  /**
   * Set the parent of the stylesheet.  This should be null if this
   * is the root stylesheet.
   *
   * NEEDSDOC @param v
   */
  public void setStylesheetParent(Stylesheet v)
  {
    m_stylesheetParent = v;
  }

  /**
   * Get the owning aggregated stylesheet, or this
   * stylesheet if it is aggregated.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public StylesheetComposed getStylesheetComposed()
  {

    Stylesheet sheet = this;

    while (!sheet.isAggregatedType())
    {
      sheet = sheet.getStylesheetParent();
    }

    return (StylesheetComposed) sheet;
  }

  /**
   * Get the type of the node.  We'll pretend we're a Document.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public short getNodeType()
  {
    return Node.DOCUMENT_NODE;
  }

  /**
   * Get an integer representation of the element type.
   *
   * @return An integer representation of the element, defined in the
   *     Constants class.
   * @see org.apache.xalan.templates.Constants
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_STYLESHEET;
  }

  /**
   * Return the node name.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_STYLESHEET_STRING;
  }

  /**
   * Replace an "xsl:template" property.
   * This is a hook for CompilingStylesheetHandler, to allow
   * us to access a template, compile it, instantiate it,
   * and replace the original with the compiled instance.
   * ADDED 9/5/2000 to support compilation experiment
   *
   * NEEDSDOC @param v
   * NEEDSDOC @param i
   *
   * @throws TransformerException
   */
  public void replaceTemplate(ElemTemplate v, int i) throws TransformerException
  {

    if (null == m_templates)
      throw new ArrayIndexOutOfBoundsException();

    replaceChild(v, (Node) m_templates.elementAt(i));
    m_templates.setElementAt(v, i);
    v.setStylesheet(this);
  }
}
