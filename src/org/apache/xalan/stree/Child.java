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

import org.apache.xml.utils.UnImplNode;
import org.apache.xpath.DOMOrder;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;

import org.xml.sax.ContentHandler;

import org.apache.xalan.transformer.TransformerImpl;

/**
 * <meta name="usage" content="internal"/>
 * Class representing a child node
 */
public class Child extends UnImplNode implements DOMOrder, SaxEventDispatch
{

  /** Document Object          */
  protected DocumentImpl m_doc;

  /** This child's parent node           */
  protected Parent m_parent;

  /** This child next sibling          */
  Child m_next;

  /** This child previous sibling          */
  Child m_prev;

  /** This child's level in the source tree   */
  short m_level;

  /** This child's unique ID          */
  int m_uid;

  /**
   * Set the document object for this child 
   *
   *
   * @param doc document object
   */
  protected void setDoc(DocumentImpl doc)
  {
    m_doc = doc;
  }

  /**
   * Constructor Child
   *
   *
   * @param doc document object
   */
  public Child(DocumentImpl doc)
  {
    m_doc = doc;
  }

  /**
   * Set the parent of the node.
   *
   * @param parent this node's parent
   */
  protected void setParent(Parent parent)
  {
    m_parent = parent;
  }

  /**
   * Return if this node has had all it's children added, i.e.
   * if a endElement event has occured.  An atomic node always
   * returns true.
   *
   * @return true.
   */
  public boolean isComplete()
  {

    // Atomic nodes are always complete.
    return true;
  }

  /**
   * Set that this node's child list is complete, i.e.
   * an endElement event has occured. At this level, this 
   * method does nothing.
   *
   * @param isComplete true if this node has had all it's 
   * children added.
   */
  public void setComplete(boolean isComplete){}

  /**
   * Get the Transformer object for this source tree. 
   *
   *
   * @return transformer object for this source tree
   */
  protected TransformerImpl getTransformer()
  {
    return m_doc.m_sourceTreeHandler.m_transformer;
  }

  /**
   * Throw a Parse Error exception 
   *
   *
   * @param e original exception
   */
  protected void throwParseError(Exception e)
  {
    throw new org.apache.xml.utils.WrappedRuntimeException(e);
  }

  /**
   * Throw a Parse Error exception if no exception was thrown yet 
   *
   */
  protected void throwIfParseError()
  {
    if (null != m_doc.m_exceptionThrown)
      throwParseError(m_doc.m_exceptionThrown);
  }

  /**
   * Set the UID (document order index).
   *
   * @param kIndex Index of this child.
   */
  protected void setUid(int kIndex)
  {
    m_uid = kIndex;
  }

  /**
   * Get the UID (document order index).
   *
   * @return Index of this child
   */
  public int getUid()
  {
    return m_uid;
  }

  /**
   * <meta name="usage" content="internal"/>
   * Get the depth level of this node in the tree.
   *
   * @return This child's level in the source tree
   */
  public short getLevel()
  {
    return m_level;
  }

  /**
   * <meta name="usage" content="internal"/>
   * Get the depth level of this node in the tree.
   *
   * @param level This child's level in the source tree
   */
  public void setLevel(short level)
  {
    m_level = level;
  }

  /**
   * Get the root Document Implementation.
   *
   * @return document object
   */
  DocumentImpl getDocumentImpl()
  {
    return m_doc;
  }

  // ================ Node interface implementation ==============

  /**
   * The parent of this node. All nodes, except <code>Attr</code>,
   * <code>Document</code>, <code>DocumentFragment</code>,
   * <code>Entity</code>, and <code>Notation</code> may have a parent.
   * However, if a      node has just been created and not yet added to the
   * tree, or if it has been removed from the tree, this is
   * <code>null</code>.
   *
   * @return This node's parent node. 
   */
  public Node getParentNode()
  {

    // if(null != m_parent)
    //  m_parent.waitForHaveParent();
    return this.m_parent;
  }

  /**
   * The first child of this node. If there is no such node, this returns
   * <code>null</code>.
   *
   * @return This node's first child.
   */
  public Node getFirstChild()
  {
    return null;
  }

  /**
   * The last child of this node. If there is no such node, this returns
   * <code>null</code>.
   *
   * @return This node's last child.
   */
  public Node getLastChild()
  {
    return null;
  }

  /**
   * The node immediately preceding this node. If there is no such node,
   * this returns <code>null</code>.
   *
   * @return This node's previous sibling
   */
  public Node getPreviousSibling()
  {
    return m_prev;
  }

  /**
   * The node immediately following this node. If there is no such node,
   * this returns <code>null</code>.
   *
   * @return This node's next sibling.
   */
  public Node getNextSibling()
  {

//    synchronized (m_doc)
//    {
    if (null != m_next)
      return m_next;
    else if (!m_parent.m_isComplete)
    {
      synchronized (m_doc)
      {
        try
        {

          // System.out.println("Waiting... getChild " + i + " " + getNodeName());
          while (!m_parent.isComplete())
          {
            m_doc.wait(100);
            throwIfParseError();

            if (null != m_next)
              return m_next;
          }
        }
        catch (InterruptedException e)
        {
          throwIfParseError();
        }
      }
    }
//    }

    return m_next;
  }

  /**
   * The <code>Document</code> object associated with this node. This is
   * also the <code>Document</code> object used to create new nodes. When
   * this node is a <code>Document</code> or a <code>DocumentType</code>
   * which is not used with any <code>Document</code> yet, this is
   * <code>null</code>.
   * @version DOM Level 2
   *
   * @return document object
   */
  public Document getOwnerDocument()
  {
    return m_doc;
  }

  /**
   *  This is a convenience method to allow easy determination of whether a
   * node has any children.
   * @return  <code>true</code> if the node has any children,
   *   <code>false</code> if the node has no children.
   */
  public boolean hasChildNodes()
  {
    return false;
  }

  /**
   * Tests whether the DOM implementation implements a specific feature and
   * that feature is supported by this node.
   * @since DOM Level 2
   * @param feature The string of the feature to test. This is the same name
   *   that which can be passed to the method <code>hasFeature</code> on
   *   <code>DOMImplementation</code>.
   * @param version This is the version number of the feature to test. In
   *   Level 2, version 1, this is the string "2.0". If the version is not
   *   specified, supporting any version of the feature will cause the
   *   method to return <code>true</code>.
   * @return Returns <code>true</code> if the specified feature is supported
   *   on this node, <code>false</code> otherwise.
   */
  public boolean isSupported(String feature, String version)
  {
    if (feature == SaxEventDispatch.SUPPORTSINTERFACE ||
        feature == org.apache.xpath.patterns.NodeTest.SUPPORTS_PRE_STRIPPING)
      return true;
    else
      return false;
  }

  /**
   * The namespace URI of this node, or <code>null</code> if it is
   * unspecified.
   *
   * @return This node's namespace URI 
   */
  public String getNamespaceURI()
  {
    return null;
  }

  /**
   * The namespace prefix of this node, or <code>null</code> if it is
   * unspecified.
   * @since DOM Level 2
   *
   * @return This node's namespace prefix
   */
  public String getPrefix()
  {
    return null;
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
    return null;
  }

  /**
   * UnImplemented. 
   *
   * @return null
   */
  public String getTagName()
  {
    return null;
  }

  /**
   * Unimplemented. 
   *
   * @return null
   */
  public NamedNodeMap getAttributes()
  {
    return null;
  }

  /**
   * Unimplemented. 
   *
   * @param name Attribute name 
   * @param value Attribute value
   *
   * @throws DOMException
   */
  public void setAttribute(String name, String value) throws DOMException{}

  /**
   * Tell if the given node is a namespace decl node.
   *
   * @return whether this node is a namespace decl node
   */
  public boolean isNamespaceNode()
  {
    return false;
  }
  
  /**
   * Handle a Characters event 
   *
   *
   * @param ch Content handler to handle SAX events
   *
   * @throws SAXException if the content handler characters event throws a SAXException.
   */
  public void dispatchCharactersEvent(ContentHandler ch) 
    throws org.xml.sax.SAXException
  {
  }

}
