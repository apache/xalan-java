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
package org.apache.xalan.lib;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.traversal.NodeIterator;

import org.apache.xpath.NodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XRTreeFrag;

import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.DOMHelper;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.ref.DTMNodeIterator;
import org.apache.xml.utils.XMLString;

import org.xml.sax.SAXNotSupportedException;

import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.xslt.EnvironmentCheck;

import javax.xml.parsers.*;

/**
 * <meta name="usage" content="general"/>
 * This class contains EXSLT set extension functions.
 * It is accessed by specifying a namespace URI as follows:
 * <pre>
 *    xmlns:set="http://exslt.org/sets"
 * </pre>
 * 
 * The documentation for each function has been copied from the relevant
 * EXSLT Implementer page. 
 * 
 * @see <a href="http://www.exslt.org/">EXSLT</a>
 */
public class ExsltSets
{   
  /**
   * The set:leading function returns the nodes in the node set passed as the first argument that
   * precede, in document order, the first node in the node set passed as the second argument. If
   * the first node in the second node set is not contained in the first node set, then an empty
   * node set is returned. If the second node set is empty, then the first node set is returned.
   * 
   * @param ni1 NodeIterator for first node-set.
   * @param ni2 NodeIterator for second node-set.
   * @return a node-set containing the nodes in ni1 that precede in document order the first
   * node in ni2; an empty node-set if the first node in ni2 is not in ni1; all of ni1 if n12
   * is empty.
   * 
   * @see <a href="http://www.exslt.org/">EXSLT</a>
   */
  public static NodeSet leading (NodeIterator ni1, NodeIterator ni2)
  {
    NodeSet ns1 = new NodeSet(ni1);
    NodeSet ns2 = new NodeSet(ni2);
    NodeSet leadNodes = new NodeSet();
    if (ns2.getLength() == 0)
      return ns1;
    Node endNode = ns2.elementAt(0);
    if (!ns1.contains(endNode))
      return leadNodes; // empty NodeSet
    for (int i = 0; i < ns1.getLength(); i++)
    {
      Node testNode = ns1.elementAt(i);
      if (DOMHelper.isNodeAfter(testNode, endNode) 
          && !(DOMHelper.isNodeTheSame(testNode, endNode)))
        leadNodes.addElement(testNode);
    }
    return leadNodes;
  }
  
  /**
   * The set:trailing function returns the nodes in the node set passed as the first argument that 
   * follow, in document order, the first node in the node set passed as the second argument. If 
   * the first node in the second node set is not contained in the first node set, then an empty 
   * node set is returned. If the second node set is empty, then the first node set is returned. 
   * 
   * @param ni1 NodeIterator for first node-set.
   * @param ni2 NodeIterator for second node-set.
   * @return a node-set containing the nodes in ni1 that follow in document order the first
   * node in ni2; an empty node-set if the first node in ni2 is not in ni1; all of ni1 if ni2
   * is empty.
   * 
   * @see <a href="http://www.exslt.org/">EXSLT</a>
   */
  public static NodeSet trailing (NodeIterator ni1, NodeIterator ni2)
  {
    NodeSet ns1 = new NodeSet(ni1);
    NodeSet ns2 = new NodeSet(ni2);
    NodeSet trailNodes = new NodeSet();
    if (ns2.getLength() == 0)
      return ns1;
    Node startNode = ns2.elementAt(0);
    if (!ns1.contains(startNode))
      return trailNodes; // empty NodeSet
    for (int i = 0; i < ns1.getLength(); i++)
    {
      Node testNode = ns1.elementAt(i);
      if (DOMHelper.isNodeAfter(startNode, testNode) 
          && !(DOMHelper.isNodeTheSame(startNode, testNode)))
        trailNodes.addElement(testNode);          
    }
    return trailNodes;
  }
  
  /**
   * The set:intersection function returns a node set comprising the nodes that are within 
   * both the node sets passed as arguments to it.
   * 
   * @param ni1 NodeIterator for first node-set.
   * @param ni2 NodeIterator for second node-set.
   * @return a NodeSet containing the nodes in ni1 that are also
   * in ni2.
   * 
   * Note: Already implemented in the xalan namespace.
   * 
   * @see <a href="http://www.exslt.org/">EXSLT</a>
   */
  public static NodeSet intersection(NodeIterator ni1, NodeIterator ni2)
          throws javax.xml.transform.TransformerException
  {
    return Extensions.intersection(ni1, ni2);
  }
  
  /**
   * The set:difference function returns the difference between two node sets - those nodes that 
   * are in the node set passed as the first argument that are not in the node set passed as the 
   * second argument.
   * 
   * @param ni1 NodeIterator for first node-set.
   * @param ni2 NodeIterator for second node-set.
   * @return a NodeSet containing the nodes in ni1 that are not
   * in ni2.
   * 
   * Note: Already implemented in the xalan namespace.
   * 
   * @see <a href="http://www.exslt.org/">EXSLT</a>
   */
  public static NodeSet difference(NodeIterator ni1, NodeIterator ni2)
          throws javax.xml.transform.TransformerException
  {
    return Extensions.difference(ni1, ni2);
  }
  
  /**
   * The set:distinct function returns a subset of the nodes contained in the node-set NS passed 
   * as the first argument. Specifically, it selects a node N if there is no node in NS that has 
   * the same string value as N, and that precedes N in document order. 
   * 
   * @param myContext Passed in by Xalan extension processor.
   * @param ni NodeIterator for node-set.
   * @return a NodeSet with nodes from ni containing distinct string values.
   * In other words, if more than one node in ni contains the same string value,
   * only include the first such node found.
   * 
   * Note: Already implemented in the xalan namespace.
   * 
   * @see <a href="http://www.exslt.org/">EXSLT</a>
   */
  public static NodeSet distinct(ExpressionContext myContext, NodeIterator ni)
          throws javax.xml.transform.TransformerException
  {
    return Extensions.distinct(myContext, ni);
  }
  
  /**
   * The set:has-same-node function returns true if the node set passed as the first argument shares 
   * any nodes with the node set passed as the second argument. If there are no nodes that are in both
   * node sets, then it returns false. 
   * 
   * The Xalan extensions MethodResolver converts 'has-same-node' to 'hasSameNode'.
   * 
   * Note: Not to be confused with hasSameNodes in the Xalan namespace, which returns true if
   * the two node sets contain the exactly the same nodes (perhaps in a different order), 
   * otherwise false.
   * 
   * @see <a href="http://www.exslt.org/">EXSLT</a>
   */
  public static boolean hasSameNode(NodeIterator ni1, NodeIterator ni2)
  {
    
    NodeSet ns1 = new NodeSet(ni1);
    NodeSet ns2 = new NodeSet(ni2);

    for (int i = 0; i < ns1.getLength(); i++)
    {
      if (ns2.contains(ns1.elementAt(i)))
        return true;
    }
    return false;
  }
  
}