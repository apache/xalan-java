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
package org.apache.xalan.xpath.axes;

import org.apache.xalan.xpath.LocPathIterator;
import org.apache.xalan.xpath.XPath;
import org.apache.xalan.xpath.SimpleNodeLocator;
import org.apache.xalan.xpath.XPathContext;
import org.apache.xalan.xpath.XObject;
import org.apache.xalan.xpath.DOMHelper;
import org.apache.xalan.utils.PrefixResolver;
import java.util.Vector;

import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.traversal.NodeFilter;

/**
 * Walker for the OP_VARIABLE, or OP_EXTFUNCTION, or OP_FUNCTION, or OP_GROUP,
 * op codes.
 * @see <a href="http://www.w3.org/TR/xpath#NT-FilterExpr">XPath FilterExpr descriptions</a>
 */
public class FilterExprWalker extends AxesWalker
{
  /**
   * Construct a FilterExprWalker using a LocPathIterator.
   */
  public FilterExprWalker(LocPathIterator locPathIterator)
  {
    super(locPathIterator);
  }
    
  /**
   *  Set the root node of the TreeWalker.
   */
  public void setRoot(Node root)
  {
    XPathContext xctxt = m_lpi.getXPathContext();
    PrefixResolver savedResolver = xctxt.getNamespaceContext();
    int savedStackframeIndex = xctxt.getVarStack().getCurrentStackFrameIndex();
    try
    {
      xctxt.getVarStack().setCurrentStackFrameIndex(m_lpi.getStackFrameIndex());
      xctxt.setNamespaceContext(m_lpi.getPrefixResolver());
      XObject obj = m_lpi.getXPath().execute(m_lpi.getXPathContext(), 
                                             root, getOpPos());
      if(null != obj)
        m_nodeSet = obj.nodeset();
      else
        m_nodeSet = null;
      
      m_peek = null;
    }
    catch(org.xml.sax.SAXException se)
    {
      // TODO: Fix...
      throw new RuntimeException(se.getMessage());
    }
    finally
    {
      xctxt.getVarStack().setCurrentStackFrameIndex(savedStackframeIndex);
      xctxt.setNamespaceContext(savedResolver);
    }
    
    super.setRoot(root);
  }
  
  /**
   * Get a cloned FilterExprWalker.
   */
  public Object clone()
    throws CloneNotSupportedException
  {
    FilterExprWalker clone = (FilterExprWalker)super.clone();
    clone.m_nodeSet = (NodeIterator)((LocPathIterator)m_nodeSet).clone();
    return clone;
  }
  
  /**
   * This method needs to override AxesWalker.acceptNode because FilterExprWalkers 
   * don't need to, and shouldn't, do a node test.
   * @param n  The node to check to see if it passes the filter or not.
   * @return  a constant to determine whether the node is accepted, 
   *   rejected, or skipped, as defined  above .
   */
  public short acceptNode(Node n)
  {
    try
    {
      if(m_predicateCount > 0)
      {
        countProximityPosition(0);
        
        if(!predicate(n, m_lpi.getXPath(), m_lpi.getXPathContext(), 
                      m_posOfPredicate))
          return NodeFilter.FILTER_SKIP;
      }
      
      return NodeFilter.FILTER_ACCEPT;
    }
    catch(org.xml.sax.SAXException se)
    {
      // TODO: Fix this.
      throw new RuntimeException(se.getMessage());
    }
  }
  
  /**
   *  Moves the <code>TreeWalker</code> to the next visible node in document 
   * order relative to the current node, and returns the new node. If the 
   * current node has no next node,  or if the search for nextNode attempts 
   * to step upward from the TreeWalker's root node, returns 
   * <code>null</code> , and retains the current node.
   * @return  The new node, or <code>null</code> if the current node has no 
   *   next node  in the TreeWalker's logical view.
   */
  public Node getNextNode()
  {
    Node next;
    if(null != m_peek)
    {
      next = m_peek;
      m_peek = null;
    }
    else
    {
      next = (null != m_nodeSet) ? m_nodeSet.nextNode() : null;
    }
    
    // Bogus, I think, but probably OK for right now since a filterExpr 
    // can only occur at the head of a location path.
    if(null == next)
    {
      m_nextLevelAmount = 0;
    }
    else
    {
      m_nextLevelAmount = (next.hasChildNodes() ? 1 : 0);
      /* ...WAIT TO SEE IF WE REALLY NEED THIS...
      m_peek = m_nodeSet.nextNode();
      if(null == m_peek)
        m_nextLevelAmount = 0;
      else
      {
        DOMHelper dh = m_lpi.getDOMHelper();
        m_nextLevelAmount = dh.getLevel(m_peek) - dh.getLevel(next);
      }
      */
    }
    
    return setCurrentIfNotNull(next);
  }
  
  NodeIterator m_nodeSet;
  Node m_peek = null;
    
  /**
   * Tell what's the maximum level this axes can descend to.
   */
  protected int getLevelMax()
  {
    // TODO: Oh, this is going to be a hell of a lot of fun...
    // return Short.MAX_VALUE;
    return 1; // bogus, will probably screw things up.
    // return m_lpi.getDOMHelper().getLevel(this.m_currentNode)+1;
  }

}
