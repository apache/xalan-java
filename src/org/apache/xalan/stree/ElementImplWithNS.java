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

import org.xml.sax.Attributes;

/**
 * <meta name="usage" content="internal"/>
 * This class represents an element in an HTML or XML document associated 
 * with a given namespace.
 * Elements may have attributes associated with them as well as children nodes.
 */
public class ElementImplWithNS extends ElementImpl
{

  /** This element's localName          */
  private String m_localName;

  /** This element's namespace URI          */
  private String m_uri;

  /**
   * Constructor ElementImplWithNS
   *
   *
   * @param doc Document object
   * @param ns Namespace URI 
   * @param name Element's name
   */
  ElementImplWithNS(DocumentImpl doc, String ns, String name)
  {

    super(doc, name);

    int index = name.indexOf(':');

    if (index > 0)
      m_localName = name.substring(index + 1);
    else
      m_localName = name;

    m_uri = ns;
  }

  /**
   * Constructor ElementImplWithNS
   *
   *
   * @param doc Document Object
   * @param ns Element's Namespace URI
   * @param localName Element's localName
   * @param name Element's name
   * @param atts List of attributes associated with this element 
   */
  ElementImplWithNS(DocumentImpl doc, String ns, String localName,
                    String name, Attributes atts)
  {

    super(doc, name, atts);

    m_localName = localName;
    m_uri = ns;
  }

  /**
   * The namespace URI of this node, or <code>null</code> if it is
   * unspecified.
   *
   * @return The element's namespace URI 
   */
  public String getNamespaceURI()
  {
    return m_uri;
  }

  /**
   * The namespace prefix of this node, or <code>null</code> if it is
   * unspecified.
   * @since DOM Level 2
   *
   * @return The element's namespace prefix
   */
  public String getPrefix()
  {

    String rawName = getNodeName();
    int indexOfNSSep = rawName.indexOf(':');

    return (indexOfNSSep >= 0) ? rawName.substring(0, indexOfNSSep) : null;
  }

  /**
   * Returns the local part of the qualified name of this node.
   * <br>For nodes created with a DOM Level 1 method, such as
   * <code>createElement</code> from the <code>Document</code> interface,
   * it is <code>null</code>.
   * @since DOM Level 2
   *
   * @return the local part of the qualified name of this node
   */
  public String getLocalName()
  {
    return m_localName;
  }
}
