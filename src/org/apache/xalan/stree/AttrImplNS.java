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
package org.apache.xalan.stree;

import org.w3c.dom.Node;

import org.xml.sax.Attributes;

/**
 * <meta name="usage" content="internal"/>
 * Class to hold information about an attribute node with a name space
 */
public class AttrImplNS extends AttrImpl
{

  /** Attribute local name          */
  private String m_localName;

  /** Attribute namespace URI          */
  private String m_namespaceURI;  // attribute index

  /**
   * Constructor AttrImplNS
   *
   *
   * @param doc Document object
   * @param uri Attribute namespace URI
   * @param name attribute name
   * @param value Attribute value   
   */
  AttrImplNS(DocumentImpl doc, String uri, String name, String value)
  {

    super(doc, name, value);

    // System.out.println("AttrImplNS - name: "+name);
    // System.out.println("uri: "+uri+", "+name);
    m_namespaceURI = uri;

    int index = name.indexOf(':');

    if (index > 0)
      m_localName = name.substring(index + 1);
    else
      m_localName = name;
  }

  /**
   * The namespace URI of this node, or <code>null</code> if it is
   * unspecified.
   * <br>This is not a computed value that is the result of a namespace
   * lookup based on an examination of the namespace declarations in scope.
   * It is merely the namespace URI given at creation time.
   * <br>For nodes of any type other than <code>ELEMENT_NODE</code> and
   * <code>ATTRIBUTE_NODE</code> and nodes created with a DOM Level 1
   * method, such as <code>createElement</code> from the
   * <code>Document</code> interface, this is always <code>null</code>.Per
   * the Namespaces in XML Specification  an attribute does not inherit its
   * namespace from the element it is attached to. If an attribute is not
   * explicitly given a namespace, it simply has no namespace.
   *
   * @return namespace URI of this node
   */
  public String getNamespaceURI()
  {
    return m_namespaceURI;
  }

  /**
   * The namespace prefix of this node, or <code>null</code> if it is
   * unspecified.
   * @since DOM Level 2
   *
   * @return namespace prefix of this node
   */
  public String getPrefix()
  {

    String m_name = getNodeName();
    int indexOfNSSep = m_name.indexOf(':');

    return (indexOfNSSep >= 0) ? m_name.substring(0, indexOfNSSep) : null;
  }

  /**
   * Returns the local part of the qualified name of this node.
   * <br>For nodes created with a DOM Level 1 method, such as
   * <code>createElement</code> from the <code>Document</code> interface,
   * it is <code>null</code>.
   * @since DOM Level 2
   *
   * @return local part of the qualified name of this node
   */
  public String getLocalName()
  {
    return m_localName;
  }
}
