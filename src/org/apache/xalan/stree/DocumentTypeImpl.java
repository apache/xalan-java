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
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;

/**
 * <meta name="usage" content="internal"/>
 * NEEDSDOC Class DocumentTypeImpl <needs-comment/>
 */
public class DocumentTypeImpl extends Child implements DocumentType
{

  /**
   * Constructor DocumentTypeImpl
   *
   *
   * NEEDSDOC @param doc
   * NEEDSDOC @param name
   */
  DocumentTypeImpl(DocumentImpl doc, String name)
  {

    super(doc);

    m_name = name;
  }

  /** NEEDSDOC Field m_name          */
  private String m_name;

  /** NEEDSDOC Field m_publicID          */
  private String m_publicID;

  /** NEEDSDOC Field m_systemID          */
  private String m_systemID;

  /** NEEDSDOC Field m_internalSubset          */
  private String m_internalSubset;

  /**
   * A short integer indicating what type of node this is. The named
   * constants for this value are defined in the org.w3c.dom.Node interface.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public short getNodeType()
  {
    return Node.DOCUMENT_TYPE_NODE;
  }

  /**
   * Returns the node name. 
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getNodeName()
  {
    return m_name;  // I guess I need the name of the document type
  }

  /**
   * The name of DTD; i.e., the name immediately following the
   * <code>DOCTYPE</code> keyword.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getName()
  {
    return m_name;
  }

  /**
   * Returns the local part of the qualified name of this node.
   * <br>For nodes created with a DOM Level 1 method, such as
   * <code>createElement</code> from the <code>Document</code> interface,
   * it is <code>null</code>.
   * @since DOM Level 2
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getLocalName()
  {
    return m_name;
  }

  /**
   * A <code>NamedNodeMap</code> containing the general entities, both
   * external and internal, declared in the DTD. Parameter entities are not
   *  contained. Duplicates are discarded. For example in:
   * <pre>
   * &lt;!DOCTYPE ex SYSTEM "ex.dtd" [
   *   &lt;!ENTITY foo "foo"&gt;
   *   &lt;!ENTITY bar "bar"&gt;
   *   &lt;!ENTITY bar "bar2"&gt;
   *   &lt;!ENTITY % baz "baz"&gt;
   * ]&gt;
   * &lt;ex/&gt;</pre>
   *   the interface
   * provides access to <code>foo</code> and the first declaration of
   * <code>bar</code> but not the second declaration of  <code>bar</code>
   * or <code>baz</code>. Every node in this map also implements the
   * <code>Entity</code> interface.
   * <br>The DOM Level 2 does not support editing entities, therefore
   * <code>entities</code> cannot be altered in any way.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public NamedNodeMap getEntities()
  {
    return null;
  }

  /**
   * A <code>NamedNodeMap</code> containing  the notations declared in the
   * DTD. Duplicates are discarded. Every node in this map also implements
   * the <code>Notation</code> interface.
   * <br>The DOM Level 2 does not support editing notations, therefore
   * <code>notations</code> cannot be altered in any way.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public NamedNodeMap getNotations()
  {
    return null;
  }

  /**
   * The public identifier of the external subset.
   * @since DOM Level 2
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getPublicId()
  {
    return m_publicID;
  }

  /**
   * The system identifier of the external subset.
   * @since DOM Level 2
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getSystemId()
  {
    return m_systemID;
  }

  /**
   * The internal subset as a string.
   * @since DOM Level 2
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getInternalSubset()
  {
    return m_internalSubset;
  }
}
