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
package org.apache.xalan.transformer;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.xalan.xpath.NodeSet;
import org.apache.xalan.xpath.XPathContext;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.res.XSLMessages;

/**
 * <meta name="usage" content="internal"/>
 * Container of a result tree fragment.
 */
public class ResultTreeFrag implements DocumentFragment
{
  Document m_docFactory;
  NodeSet m_children;
  XPathContext m_xsupport;

  public ResultTreeFrag(Document docFactory, XPathContext support)
  {
    m_xsupport = support;
    m_docFactory = docFactory;
    m_children = new NodeSet();
  }

  public ResultTreeFrag(Document docFactory, NodeSet children,
                        XPathContext support)
  {
    m_xsupport = support;
    m_docFactory = docFactory;
    m_children = children;
  }

  /**
   * Throw an error.
   */
  void error(int msg)
  {
		   throw new RuntimeException(XSLMessages.createMessage(msg, null));
  }

  /**
   * The name of this node, depending on its type; see the table above.
   */
  public final String getNodeName()
  {
    return "#document-fragment";
  }

  /**
   * The value of this node, depending on its type; see the table above.
   * @exception DOMException
   *   NO_MODIFICATION_ALLOWED_ERR: Raised when the node is readonly.
   * @exception DOMException
   *   DOMSTRING_SIZE_ERR: Raised when it would return more characters than
   *   fit in a <code>DOMString</code> variable on the implementation
   *   platform.
   */
    public String             getNodeValue()
      throws DOMException
    {
      return "";
    }

    /**
     * DocumentFragments never have a nodeValue.
     * @throws DOMException(NO_MODIFICATION_ALLOWED_ERR)
     */
    public void setNodeValue(String x)
      throws DOMException
    {
      // No action.
    }

  /**
   * A code representing the type of the underlying object, as defined above.
   */
  public final short getNodeType()
  {
    return Node.DOCUMENT_FRAGMENT_NODE;
  }

  /**
   * The parent of this node. All nodes, except <code>Document</code>,
   * <code>DocumentFragment</code>, and <code>Attr</code> may have a parent.
   * However, if a node has just been created and not yet added to the tree,
   * or if it has been removed from the tree, this is <code>null</code>.
   */
  public Node               getParentNode()
  {
    return null;
  }

  /**
   * A <code>NodeList</code> that contains all children of this node. If there
   * are no children, this is a <code>NodeList</code> containing no nodes.
   * The content of the returned <code>NodeList</code> is "live" in the sense
   * that, for instance, changes to the children of the node object that
   * it	was created from are immediately reflected in the nodes returned by
   * the <code>NodeList</code> accessors; it is not a static snapshot of the
   * content of the node. This is true for every <code>NodeList</code>,
   * including the ones returned by the <code>getElementsByTagName</code>
   * method.
   */
  public NodeList           getChildNodes()
  {
    return m_children;
  }

  /**
   * The first child of this node. If there is no such node, this returns
   * <code>null</code>.
   */
  public Node               getFirstChild()
  {
    int nChildren = m_children.getLength();
    return (nChildren > 0) ? m_children.item(0) : null;
  }

  /**
   * The last child of this node. If there is no such node, this returns
   * <code>null</code>.
   */
  public Node               getLastChild()
  {
    int nChildren = m_children.getLength();
    return (nChildren > 0) ? m_children.item(nChildren-1) : null;
  }

  /**
   * The node immediately preceding this node. If there is no such node, this
   * returns <code>null</code>.
   */
  public Node               getPreviousSibling()
  {
    return null;
  }

  /**
   * The node immediately following this node. If there is no such node, this
   * returns <code>null</code>.
   */
  public Node               getNextSibling()
  {
    return null;
  }

  /**
   * A <code>NamedNodeMap</code> containing the attributes of this node (if it
   * is an <code>Element</code>) or <code>null</code> otherwise.
   */
  public NamedNodeMap       getAttributes()
  {
    return null;
  }

  /**
   * The <code>Document</code> object associated with this node. This is also
   * the <code>Document</code> object used to create new nodes. When this
   * node is a <code>Document</code> this is <code>null</code>.
   */
  public Document getOwnerDocument()
  {
    return m_docFactory;
  }

  /**
   * Inserts the node <code>newChild</code> before the existing child node
   * <code>refChild</code>. If <code>refChild</code> is <code>null</code>,
   * insert <code>newChild</code> at the end of the list of children.
   * <br>If <code>newChild</code> is a <code>DocumentFragment</code> object,
   * all of its children are inserted, in the same order, before
   * <code>refChild</code>. If the <code>newChild</code> is already in the
   * tree, it is first removed.
   * @param newChild The node to insert.
   * @param refChild The reference node, i.e., the node before which the new
   *   node must be inserted.
   * @return The node being inserted.
   * @exception DOMException
   *   HIERARCHY_REQUEST_ERR: Raised if this node is of a type that does not
   *   allow children of the type of the <code>newChild</code> node, or if
   *   the node to insert is one of this node's ancestors.
   *   <br>WRONG_DOCUMENT_ERR: Raised if <code>newChild</code> was created
   *   from a different document than the one that created this node.
   *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
   *   <br>NOT_FOUND_ERR: Raised if <code>refChild</code> is not a child of
   *   this node.
   */
  public Node insertBefore(Node newChild,
                           Node refChild)
    throws DOMException
  {
    // NodeSet mnl = (NodeSet)m_children;
    // int refIndex = (null == refChild)
    //               ? mnl.getLength() : mnl.indexOf(refChild);
    return newChild;
  }

  /**
   * Replaces the child node <code>oldChild</code> with <code>newChild</code>
   * in the list of children, and returns the <code>oldChild</code> node. If
   * the <code>newChild</code> is already in the tree, it is first removed.
   * @param newChild The new node to put in the child list.
   * @param oldChild The node being replaced in the list.
   * @return The node replaced.
   * @exception DOMException
   *   HIERARCHY_REQUEST_ERR: Raised if this node is of a type that does not
   *   allow children of the type of the <code>newChild</code> node, or it
   *   the node to put in is one of this node's ancestors.
   *   <br>WRONG_DOCUMENT_ERR: Raised if <code>newChild</code> was created
   *   from a different document than the one that created this node.
   *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
   *   <br>NOT_FOUND_ERR: Raised if <code>oldChild</code> is not a child of
   *   this node.
   */
  public Node               replaceChild(Node newChild,
                                         Node oldChild)
    throws DOMException
  {
    NodeSet mnl = (NodeSet)m_children;
    int newChildIndex = mnl.indexOf(newChild);
    if(newChildIndex > -1)
    {
      mnl.removeElementAt(newChildIndex);
    }
    else
    {
      // throw exception.
    }
    int refIndex = (null == oldChild)
                   ? -1 : mnl.indexOf(oldChild);
    if(refIndex > -1)
    {
      mnl.removeElement(oldChild);
      mnl.setElementAt(newChild, refIndex);
    }
    else
    {
      // Throw exception.
    }
    return oldChild;
  }

  /**
   * Removes the child node indicated by <code>oldChild</code> from the list
   * of children, and returns it.
   * @param oldChild The node being removed.
   * @return The node removed.
   * @exception DOMException
   *   NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
   *   <br>NOT_FOUND_ERR: Raised if <code>oldChild</code> is not a child of
   *   this node.
   */
  public Node removeChild(Node oldChild)
    throws DOMException
  {
    NodeSet mnl = (NodeSet)m_children;
    mnl.removeElement(oldChild);
    return oldChild;
  }

  /**
   * Adds the node <code>newChild</code> to the end of the list of children of
   * this node. If the <code>newChild</code> is already in the tree, it is
   * first removed.
   * @param newChild The node to add.If it is a  <code>DocumentFragment</code>
   *   object, the entire contents of the document fragment are moved into
   *   the child list of this node
   * @return The node added.
   * @exception DOMException
   *   HIERARCHY_REQUEST_ERR: Raised if this node is of a type that does not
   *   allow children of the type of the <code>newChild</code> node, or if
   *   the node to append is one of this node's ancestors.
   *   <br>WRONG_DOCUMENT_ERR: Raised if <code>newChild</code> was created
   *   from a different document than the one that created this node.
   *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
   */
  public Node appendChild(Node newChild)
    throws DOMException
  {
    NodeSet mnl = (NodeSet)m_children;
    mnl.addElement(newChild);
    return newChild;
  }

  /**
   *  This is a convenience method to allow easy determination of whether a
   * node has any children.
   * @return  <code>true</code> if the node has any children,
   *   <code>false</code> if the node has no children.
   */
  public boolean hasChildNodes()
  {
    return m_children.getLength() > 0;
  }

  /**
   * Returns a duplicate of this node, i.e., serves as a generic copy
   * constructor for nodes. The duplicate node has no parent (
   * <code>parentNode</code> returns <code>null</code>.).
   * <br>Cloning an <code>Element</code> copies all attributes and their
   * values, including those generated by the  XML processor to represent
   * defaulted attributes, but this method does not copy any text it contains
   * unless it is a deep clone, since the text is contained in a child
   * <code>Text</code> node. Cloning any other type of node simply returns a
   * copy of this node.
   * @param deep If <code>true</code>, recursively clone the subtree under the
   *   specified node; if <code>false</code>, clone only the node itself (and
   *   its attributes, if it is an <code>Element</code>).
   * @return The duplicate node.
   */
  public Node cloneNode(boolean deep)
  {
    ResultTreeFrag newFrag = new ResultTreeFrag(m_docFactory, m_xsupport);
    if(deep)
    {
      int n = m_children.getLength();
      for(int i = 0; i < n; i++)
      {
        newFrag.appendChild(m_children.item(i).cloneNode(deep));
      }
    }
    return newFrag;
  }

  /** Unimplemented. */
  public void               normalize()
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED); //"normalize not supported!");
  }

  /** Unimplemented. */
  public boolean            supports(String feature,
                                     String version)
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED); //"supports not supported!");
    return false;
  }

  /** Unimplemented. */
  public String             getNamespaceURI()
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED); //"getNamespaceURI not supported!");
    return null;
  }

  /** Unimplemented. */
  public String             getPrefix()
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED); //"getPrefix not supported!");
    return null;
  }

  /** Unimplemented. */
  public void               setPrefix(String prefix)
    throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED); //"setPrefix not supported!");
  }

  /** Unimplemented. */
  public String       getLocalName()
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED); //"getLocalName not supported!");
    return null;
  }

}
