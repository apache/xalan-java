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
package org.apache.xalan.processor;

import org.xml.sax.ContentHandler;

import org.apache.xalan.templates.Constants;
import org.apache.xml.utils.QName;

/**
 * This class defines the allowed structure for an element in a XSLT stylesheet,
 * is meant to reflect the structure defined in http://www.w3.org/TR/xslt#dtd, and the
 * mapping between Xalan classes and the markup elements in the XSLT instance.
 * This actually represents both text nodes and elements.
 */
public class XSLTElementDef
{

  /**
   * Construct an instance of XSLTElementDef.  This must be followed by a
   * call to build().
   */
  XSLTElementDef(){}

  /**
   * Construct an instance of XSLTElementDef.
   *
   * @param namespace  The Namespace URI, "*", or null.
   * @param name The local name (without prefix), "*", or null.
   * @param nameAlias A potential alias for the name, or null.
   * @param elements An array of allowed child element defs, or null.
   * @param attributes An array of allowed attribute defs, or null.
   * @param contentHandler The element processor for this element.
   * @param classObject The class of the object that this element def should produce.
   */
  XSLTElementDef(XSLTSchema schema, String namespace, String name, String nameAlias,
                 XSLTElementDef[] elements, XSLTAttributeDef[] attributes,
                 XSLTElementProcessor contentHandler, Class classObject)
  {
    build(namespace, name, nameAlias, elements, attributes, contentHandler,
          classObject);
    if ( (null != namespace)
    &&  (namespace.equals(Constants.S_XSLNAMESPACEURL)
        || namespace.equals(Constants.S_BUILTIN_EXTENSIONS_URL)) )
    {
      schema.addAvailableElement(new QName(namespace, name));
      if(null != nameAlias)
        schema.addAvailableElement(new QName(namespace, nameAlias));
    } 
  }

  /**
   * Construct an instance of XSLTElementDef that represents text.
   *
   * @param classObject The class of the object that this element def should produce.
   * @param contentHandler The element processor for this element.
   * @param type Content type, one of T_ELEMENT, T_PCDATA, or T_ANY.
   */
  XSLTElementDef(Class classObject, XSLTElementProcessor contentHandler,
                 int type)
  {

    this.m_classObject = classObject;
    this.m_type = type;

    setElementProcessor(contentHandler);
  }

  /**
   * Construct an instance of XSLTElementDef.
   *
   * @param namespace  The Namespace URI, "*", or null.
   * @param name The local name (without prefix), "*", or null.
   * @param nameAlias A potential alias for the name, or null.
   * @param elements An array of allowed child element defs, or null.
   * @param attributes An array of allowed attribute defs, or null.
   * @param contentHandler The element processor for this element.
   * @param classObject The class of the object that this element def should produce.
   */
  void build(String namespace, String name, String nameAlias,
             XSLTElementDef[] elements, XSLTAttributeDef[] attributes,
             XSLTElementProcessor contentHandler, Class classObject)
  {

    this.m_namespace = namespace;
    this.m_name = name;
    this.m_nameAlias = nameAlias;
    this.m_elements = elements;
    this.m_attributes = attributes;

    setElementProcessor(contentHandler);

    this.m_classObject = classObject;
  }

  /**
   * Tell if two objects are equal, when either one may be null.
   * If both are null, they are considered equal.
   *
   * @param obj1 A reference to the first object, or null.
   * @param obj2 A reference to the second object, or null.
   *
   * @return true if the to objects are equal by both being null or 
   * because obj2.equals(obj1) returns true.
   */
  private static boolean equalsMayBeNull(Object obj1, Object obj2)
  {
    return (obj2 == obj1)
           || ((null != obj1) && (null != obj2) && obj2.equals(obj1));
  }

  /**
   * Tell if the two string refs are equal,
   * equality being defined as:
   * 1) Both strings are null.
   * 2) One string is null and the other is empty.
   * 3) Both strings are non-null, and equal.
   *
   * @param s1 A reference to the first string, or null.
   * @param s2 A reference to the second string, or null.
   *
   * @return true if Both strings are null, or if 
   * one string is null and the other is empty, or if 
   * both strings are non-null, and equal because 
   * s1.equals(s2) returns true.
   */
  private static boolean equalsMayBeNullOrZeroLen(String s1, String s2)
  {

    int len1 = (s1 == null) ? 0 : s1.length();
    int len2 = (s2 == null) ? 0 : s2.length();

    return (len1 != len2) ? false : (len1 == 0) ? true : s1.equals(s2);
  }

  /** Content type enumerations    */
  static final int T_ELEMENT = 1, T_PCDATA = 2, T_ANY = 3;

  /**
   * The type of this element.
   */
  private int m_type = T_ELEMENT;

  /**
   * Get the type of this element.
   *
   * @return Content type, one of T_ELEMENT, T_PCDATA, or T_ANY.
   */
  int getType()
  {
    return m_type;
  }

  /**
   * Set the type of this element.
   *
   * @param t Content type, one of T_ELEMENT, T_PCDATA, or T_ANY.
   */
  void setType(int t)
  {
    m_type = t;
  }

  /**
   * The allowed namespace for this element.
   */
  private String m_namespace;

  /**
   * Get the allowed namespace for this element.
   *
   * @return The Namespace URI, "*", or null.
   */
  String getNamespace()
  {
    return m_namespace;
  }

  /**
   * The name of this element.
   */
  private String m_name;

  /**
   * Get the local name of this element.
   *
   * @return The local name of this element, "*", or null.
   */
  String getName()
  {
    return m_name;
  }

  /**
   * The name of this element.
   */
  private String m_nameAlias;

  /**
   * Get the name of this element.
   *
   * @return A potential alias for the name, or null.
   */
  String getNameAlias()
  {
    return m_nameAlias;
  }

  /**
   * The allowed elements for this type.
   */
  private XSLTElementDef[] m_elements;

  /**
   * Get the allowed elements for this type.
   *
   * @return An array of allowed child element defs, or null.
   */
  XSLTElementDef[] getElements()
  {
    return m_elements;
  }

  /**
   * Set the allowed elements for this type.
   *
   * @param defs An array of allowed child element defs, or null.
   */
  void setElements(XSLTElementDef[] defs)
  {
    m_elements = defs;
  }

  /**
   * Tell if the namespace URI and local name match this
   * element.
   * @param uri The namespace uri, which may be null.
   * @param localName The local name of an element, which may be null.
   *
   * @return true if the uri and local name arguments are considered 
   * to match the uri and local name of this element def.
   */
  private boolean QNameEquals(String uri, String localName)
  {

    return (equalsMayBeNullOrZeroLen(m_namespace, uri)
            && (equalsMayBeNullOrZeroLen(m_name, localName)
                || equalsMayBeNullOrZeroLen(m_nameAlias, localName)));
  }

  /**
   * Given a namespace URI, and a local name, get the processor
   * for the element, or return null if not allowed.
   *
   * @param uri The Namespace URI, or an empty string.
   * @param localName The local name (without prefix), or empty string if not namespace processing.
   *
   * @return The element processor that matches the arguments, or null.
   */
  XSLTElementProcessor getProcessorFor(String uri, String localName)
  {

    XSLTElementProcessor lreDef = null;  // return value

    if (null == m_elements)
      return null;

    int n = m_elements.length;

    for (int i = 0; i < n; i++)
    {
      XSLTElementDef def = m_elements[i];

      // A "*" signals that the element allows literal result
      // elements, so just assign the def, and continue to  
      // see if anything else matches.
      if (def.m_name.equals("*"))
      {

        // Don't allow xsl elements
        if (!equalsMayBeNullOrZeroLen(uri, Constants.S_XSLNAMESPACEURL))
          lreDef = def.m_elementProcessor;
      }
      else if (def.QNameEquals(uri, localName))
        return def.m_elementProcessor;
    }

    return lreDef;
  }

  /**
   * Given an unknown element, get the processor
   * for the element.
   *
   * @param uri The Namespace URI, or an empty string.
   * @param localName The local name (without prefix), or empty string if not namespace processing.
   *
   * @return normally a {@link ProcessorUnknown} reference.
   * @see ProcessorUnknown
   */
  XSLTElementProcessor getProcessorForUnknown(String uri, String localName)
  {

    // XSLTElementProcessor lreDef = null; // return value
    if (null == m_elements)
      return null;

    int n = m_elements.length;

    for (int i = 0; i < n; i++)
    {
      XSLTElementDef def = m_elements[i];

      if (def.m_name.equals("unknown") && uri.length() > 0)
      {
        return def.m_elementProcessor;
      }
    }

    return null;
  }

  /**
   * The allowed attributes for this type.
   */
  private XSLTAttributeDef[] m_attributes;

  /**
   * Get the allowed attributes for this type.
   *
   * @return An array of allowed attribute defs, or null.
   */
  XSLTAttributeDef[] getAttributes()
  {
    return m_attributes;
  }

  /**
   * Given a namespace URI, and a local name, return the element's
   * attribute definition, if it has one.
   *
   * @param uri The Namespace URI, or an empty string.
   * @param localName The local name (without prefix), or empty string if not namespace processing.
   *
   * @return The attribute def that matches the arguments, or null.
   */
  XSLTAttributeDef getAttributeDef(String uri, String localName)
  {

    XSLTAttributeDef defaultDef = null;
    XSLTAttributeDef[] attrDefs = getAttributes();
    int nAttrDefs = attrDefs.length;

    for (int k = 0; k < nAttrDefs; k++)
    {
      XSLTAttributeDef attrDef = attrDefs[k];
      String uriDef = attrDef.getNamespace();
      String nameDef = attrDef.getName();
      
      if (nameDef.equals("*") && (equalsMayBeNullOrZeroLen(uri, uriDef) || 
          (uriDef != null && uri.length() > 0 && uriDef.equals("*"))))
      {
        return attrDef;
      }
      else if (nameDef.equals("*") && (uriDef == null))
      {

        // In this case, all attributes are legal, so return 
        // this as the last resort.
        defaultDef = attrDef;
      }
      else if (equalsMayBeNullOrZeroLen(uri, uriDef)
               && localName.equals(nameDef))
      {
        return attrDef;
      }
    }

    if (null == defaultDef)
    {
      if (uri.length() > 0 && !equalsMayBeNullOrZeroLen(uri, Constants.S_XSLNAMESPACEURL))
      {
        return XSLTAttributeDef.m_foreignAttr;
      }
    }

    return defaultDef;
  }

  /**
   * If non-null, the ContentHandler/TransformerFactory for this element.
   */
  private XSLTElementProcessor m_elementProcessor;

  /**
   * Return the XSLTElementProcessor for this element.
   *
   * @return The element processor for this element.
   */
  XSLTElementProcessor getElementProcessor()
  {
    return m_elementProcessor;
  }

  /**
   * Set the XSLTElementProcessor for this element.
   *
   * @param handler The element processor for this element.
   */
  void setElementProcessor(XSLTElementProcessor handler)
  {

    if (handler != null)
    {
      m_elementProcessor = handler;

      m_elementProcessor.setElemDef(this);
    }
  }

  /**
   * If non-null, the class object that should in instantiated for
   * a Xalan instance of this element.
   */
  private Class m_classObject;

  /**
   * Return the class object that should in instantiated for
   * a Xalan instance of this element.
   *
   * @return The class of the object that this element def should produce, or null.
   */
  Class getClassObject()
  {
    return m_classObject;
  }
}
