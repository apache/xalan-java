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

import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.DOMHelper;

import org.w3c.dom.Node;

/**
 * Walker for the 'preceding' axes.
 * <p>The walk is done from the root node, in depth-first order,
 * testing to see if a given vertice is an ancestor of the step
 * context, in which case that vertice, and all following vertices,
 * are skipped.</p>
 * @see <a href="http://www.w3.org/TR/xpath#axes">XPath axes descriptions</a>
 */
public class PrecedingWalker extends ReverseAxesWalker
{

  /**
   * Construct a PrecedingWalker using a LocPathIterator.
   *
   * @param locPathIterator The location path iterator that 'owns' this walker.
   */
  public PrecedingWalker(LocPathIterator locPathIterator)
  {
    super(locPathIterator);
  }

  /**
   *  Set the root node of the TreeWalker.  If we follow an attribute:: or namespace::
   *  axis, we operate relative to the parent node.
   *
   * @param root The context node of this step.
   */
  public void setRoot(Node root)
  {

    if (Node.ATTRIBUTE_NODE == root.getNodeType())
    {
      root = m_lpi.getDOMHelper().getParentOfNode(root);
    }

    super.setRoot(root);

    m_doc = m_lpi.getDOMHelper().getRootNode(root);
    m_currentNode = m_doc;
    m_nextLevelAmount = root.hasChildNodes() ? 1 : 0;

    super.resetProximityPositions();
  }

  /**
   * Reset the proximity positions counts.
   */
  public void resetProximityPositions(){}

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

    Node next = m_currentNode.getParentNode();

    // If we're at the root of the tree, we're done.
    if ((null == next) || m_doc.equals(next))
      return null;

    // If the parent vertice is an ancestor of the step 
    // context, then all siblings are to the left of the 
    // ancestor path, so we're done.
    if (isAncestorOfRootContext(next))
      return null;

    m_nextLevelAmount = 0;

//    if(null != next)
//    {
//      Node attrNode = next.getAttributes().getNamedItem("id");
//      if(null != attrNode)
//        System.out.println("parentNode: "+attrNode.getNodeValue());
//      else
//        System.out.println("parentNode: no id value");
//    }
//    else
//      System.out.println("parentNode: null");

    return setCurrentIfNotNull(next);
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

    // Walk down the left edge of the current sub-tree.
    // Get the next child on the 'preceding' axes. This will 
    // skip any children nodes that are ancestors of the step context,
    // but will return children of those nodes.
    if (m_root.equals(m_currentNode))
      return null;

    Node nextNode = m_currentNode;

    while (null != nextNode)
    {
      Node n = nextNode.getFirstChild();

      if (null != n)
      {
        nextNode = n;
      }
      else
      {

        // If the next sibling is an ancestor, then continue 
        // on to get it's first child, otherwise nextSibling() 
        // will get it.
        nextNode = nextNode.getNextSibling();

        if (null != nextNode)
        {
          if (m_root.equals(nextNode) ||!isAncestorOfRootContext(nextNode))
            return null;
          else
            continue;
        }
      }

      if (null != nextNode)
      {
        if (m_root.equals(nextNode))
          return null;
        else if (!isAncestorOfRootContext(nextNode))
          break;
      }
    }

    m_nextLevelAmount = (null == nextNode)
                        ? 0 : (nextNode.hasChildNodes() ? 1 : 0);

//    if(null != nextNode)
//    {
//      Node attrNode = nextNode.getAttributes().getNamedItem("id");
//      if(null != attrNode)
//        System.out.println("firstChild: "+attrNode.getNodeValue());
//      else
//        System.out.println("firstChild: no id value");
//    }
//    else
//      System.out.println("firstChild: null");

    return setCurrentIfNotNull(nextNode);
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

    if (m_root.equals(m_currentNode))
      return null;

    Node next = m_currentNode.getNextSibling();
    
    if ((null == next) || m_root.equals(next)
            /* || isAncestorOfRootContext(next) */)
      return null;
      
    if(isAncestorOfRootContext(next))
    {
      next = next.getFirstChild();
      
      if ((null == next) || m_root.equals(next)
              /* || isAncestorOfRootContext(next) */)
        return null;
    }

    m_nextLevelAmount = (null == next) ? 0 : (next.hasChildNodes() ? 1 : 0);

//    if(null != next)
//    {
//      Node attrNode = next.getAttributes().getNamedItem("id");
//      if(null != attrNode)
//        System.out.println("nextSibling: "+attrNode.getNodeValue());
//      else
//        System.out.println("nextSibling: no id value");
//    }
//    else
//      System.out.println("nextSibling: null");

    return setCurrentIfNotNull(next);
  }

  /** The document owner node.  */
  transient Node m_doc;

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
