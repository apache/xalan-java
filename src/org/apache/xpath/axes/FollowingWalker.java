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
package org.apache.xpath.axes;

import java.util.Stack;

import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.DOMHelper;

import org.w3c.dom.Node;

/**
 * Walker for the 'following' axes.
 * @see <a href="http://www.w3.org/TR/xpath#axes">XPath axes descriptions</a>
 */
public class FollowingWalker extends AxesWalker
{

  /**
   * Construct a FollowingWalker using a LocPathIterator.
   *
   * @param locPathIterator The location path iterator that 'owns' this walker.
   */
  public FollowingWalker(LocPathIterator locPathIterator)
  {
    super(locPathIterator);
  }

  /**
   *  Set the root node of the TreeWalker.
   *
   * @param root The context node of this step.
   */
  public void setRoot(Node root)
  {      

    super.setRoot(root);
    
    if(root.getNodeType() == Node.ATTRIBUTE_NODE)
    {
      // The current node could be an attribute node, so getNextSibling() will 
      // always return null.  In that case, we want to continue the search 
      // with the first child of the owner element, as if the attribute nodes 
      // are children which are always _before_ the first child element.  We 
      // don't have to consider following attributes, since they never match 
      // the following axes.
      /*
      Node e = m_lpi.getDOMHelper().getParentOfNode(root);
      root = e.getLastChild();
      if(null == root)
        root = e;
      m_currentAncestor = e.getParentNode();
      */
      Node e = m_lpi.getDOMHelper().getParentOfNode(root);
      m_currentNode = e;
      m_currentAncestor = e.getOwnerDocument(); // Not totally sure why
    } 
    else
      m_currentAncestor = root;

    // Following is always moving up the tree, 
    // so I think this should be OK.
    m_nextLevelAmount = 0;
  }

  /** Stack of ancestors of the root/context node.  */
  transient protected Stack m_ancestors = new Stack();

  /**
   *  Moves to and returns the closest visible ancestor node of the current
   * node. If the search for parentNode attempts to step upward from the
   * TreeWalker's root node, or if it fails to find a visible ancestor
   * node, this method retains the current position and returns null.
   * @return  The new parent node, or null if the current node has no parent
   *   in the TreeWalker's logical view.
   */
  public Node parentNode()
  {

    Node n;
//    Node nextAncestor = (null != m_currentAncestor)
//                        ? m_currentAncestor.getParentNode() : null;
    Node nextParent = m_currentNode.getParentNode();

//    if (nextParent == nextAncestor)
//    {
//      n = null;
//
//      Node ancestor = m_currentAncestor;
//
//      while ((null != ancestor)
//             && (null != (ancestor = (Node) ancestor.getParentNode())))
//      {
//        n = ancestor.getNextSibling();
//
//        if ((null != n) || (null == ancestor))
//          break;
//      }
//
//      m_currentAncestor = ancestor;
//    }
//    else
    {
      n = nextParent;
    }

//    if(null != n)
//    {
//      Node attrNode = n.getAttributes().getNamedItem("id");
//      if(null != attrNode)
//        System.out.println("parentNode: "+attrNode.getNodeValue());
//      else
//        System.out.println("parentNode: no id value");
//    }
//    else
//      System.out.println("parentNode: null");

    return setCurrentIfNotNull(n);
  }

  /**
   *  Moves the <code>TreeWalker</code> to the first visible child of the
   * current node, and returns the new node. If the current node has no
   * visible children, returns <code>null</code> , and retains the current
   * node.
   * @return  The new node, or <code>null</code> if the current node has no
   *   visible children in the TreeWalker's logical view.
   */
  public Node firstChild()
  {

    Node n;
    if(m_currentAncestor == m_currentNode)
    {
//      if(m_currentNode.getNodeType() == Node.ATTRIBUTE_NODE)
//      {
//        // The current node could be an attribute node, so getNextSibling() will 
//        // always return null.  In that case, we want to continue the search 
//        // with the first child of the owner element, as if the attribute nodes 
//        // are children which are always _before_ the first child element.  We 
//        // don't have to consider following attributes, since they never match 
//        // the following axes.
//        n = m_lpi.getDOMHelper().getParentOfNode(m_currentNode).getFirstChild();
//      } 
//      else
        n = m_currentNode.getNextSibling();
    }
    else
    {
      n = m_currentNode.getFirstChild();
    }

    m_nextLevelAmount = (null == n) ? 0 : (n.hasChildNodes() ? 1 : 0);
    
//    if(null != n)
//    {
//      Node attrNode = n.getAttributes().getNamedItem("id");
//      if(null != attrNode)
//        System.out.println("firstChild: "+attrNode.getNodeValue());
//      else
//        System.out.println("firstChild: no id value");
//    }
//    else
//      System.out.println("firstChild: null");
      
    return setCurrentIfNotNull(n);
  }

  /**
   *  Moves the <code>TreeWalker</code> to the next sibling of the current
   * node, and returns the new node. If the current node has no visible
   * next sibling, returns <code>null</code> , and retains the current node.
   * @return  The new node, or <code>null</code> if the current node has no
   *   next sibling in the TreeWalker's logical view.
   */
  public Node nextSibling()
  {

    Node n;    
    n = m_currentNode.getNextSibling();

    m_nextLevelAmount = (null == n) ? 0 : (n.hasChildNodes() ? 1 : 0);

//    if(null != n)
//    {
//      Node attrNode = n.getAttributes().getNamedItem("id");
//      if(null != attrNode)
//        System.out.println("nextSibling: "+attrNode.getNodeValue());
//      else
//        System.out.println("nextSibling: no id value");
//    }
//    else
//      System.out.println("nextSibling: null");

    return setCurrentIfNotNull(n);
  }

  /** What this is is frankly a little unclear.  It is used in getParent 
   *  to see if we should continue to climb the tree. */
  transient Node m_currentAncestor;

  /**
   * Tell what's the maximum level this axes can descend to.
   *
   * @return Short.MAX_VALUE.
   */
  protected int getLevelMax()
  {
    return Short.MAX_VALUE;
  }
}
