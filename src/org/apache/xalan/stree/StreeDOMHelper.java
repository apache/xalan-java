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

import org.apache.xpath.DOM2Helper;

import org.w3c.dom.Node;
import org.w3c.dom.Document;

/**
 * <meta name="usage" content="internal"/>
 * Provides XSLTProcessor an interface to the Xerces XML parser.  This
 * liaison should be used if Xerces DOM nodes are being process as
 * the source tree or as the result tree.
 */
public class StreeDOMHelper extends DOM2Helper
{

  /**
   * Create an empty DOM Document.  Mainly used for RTFs.
   *
   *
   * @return an empty DOM Document.
   *

  // public Document createDocument()
  // {
  //  return new DocumentImpl();
  // }*/
  
  /**
   * Get the specified node's position in the document
   *
   * @param node A node in the document tree
   *
   * @return The position of the node in the document
   */
  public String getUniqueID(Node node)
  {

    try
    {
      int index = ((Child) node).getUid();

      return "N" + Integer.toHexString(index).toUpperCase();
    }
    catch (ClassCastException cce)
    {
      return super.getUniqueID(node);
    }
  }

  /**
   * <meta name="usage" content="internal"/>
   * Get the depth level of this node in the tree.
   *
   * @param node1 A node in the document tree
   *
   * @return The depth level of this node in the tree
   */
  public short getLevel(Node node1)
  {

    try
    {
      return ((Child) node1).getLevel();
    }
    catch (ClassCastException cce)
    {
      return super.getLevel(node1);
    }
  }

  /**
   * Tell if the given node is a namespace decl node.
   *
   * @param n A node in the document tree
   *
   * @return true if the node is a namespace decl node
   */
  public boolean isNamespaceNode(Node n)
  {

    try
    {
      return ((Child) n).isNamespaceNode();
    }
    catch (ClassCastException cce)
    {
      return super.isNamespaceNode(n);
    }
  }
  
  /**
   * Overload DOM2Helper#isNodeAfter, making the assumption that both nodes 
   * implement DOMOrder, and handling things if this is not the case by 
   * catching a cast exception.
   *
   * @param node1 DOM Node to perform position comparison on.
   * @param node2 DOM Node to perform position comparison on .
   * 
   * @return false if node2 comes before node1, otherwise return true.
   * You can think of this as 
   * <code>(node1.documentOrderPosition &lt;= node2.documentOrderPosition)</code>.
   */
  public boolean isNodeAfter(Node node1, Node node2)
  {

    // Assume first that the nodes are DTM nodes, since discovering node 
    // order is massivly faster for the DTM.
    try
    {
      int index1 = ((org.apache.xpath.DOMOrder) node1).getUid();
      int index2 = ((org.apache.xpath.DOMOrder) node2).getUid();

      return index1 <= index2;
    }
    catch (ClassCastException cce)
    {

      // isNodeAfter will return true if node is after countedNode 
      // in document order. The base isNodeAfter is sloooow (relatively)
      return super.isNodeAfter(node1, node2);
    }
  }

}
