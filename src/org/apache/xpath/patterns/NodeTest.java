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
package org.apache.xpath.patterns;

import org.w3c.dom.traversal.NodeFilter;

import org.apache.xpath.compiler.OpCodes;
import org.apache.xpath.XPath;
import org.apache.xpath.DOMHelper;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;

/**
 * <meta name="usage" content="internal"/>
 * NEEDSDOC Class NodeTest <needs-comment/>
 */
public class NodeTest extends Expression
{

  /** NEEDSDOC Field WILD          */
  public static final String WILD = "*";

  /**
   * This attribute determines which node types are accepted.
   */
  protected int m_whatToShow;

  /** NEEDSDOC Field SHOW_NAMESPACE          */
  public static final int SHOW_NAMESPACE = 0x00001000;

  /**
   * Special bitmap for match patterns starting with a function.
   * Make sure this does not conflict with dom.traversal.NodeFilter
   */
  public static final int SHOW_BYFUNCTION = 0x00010000;

  /**
   * This attribute determines which node types are accepted.
   * These constants are defined in the <code>NodeFilter</code>
   * interface.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public int getWhatToShow()
  {
    return m_whatToShow;
  }

  /** NEEDSDOC Field m_namespace          */
  String m_namespace;

  /**
   * Return the namespace to be tested.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getNamespace()
  {
    return m_namespace;
  }

  /** NEEDSDOC Field m_name          */
  String m_name;

  /**
   * Return the local namespace to be tested.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getLocalName()
  {
    return m_name;
  }

  /** NEEDSDOC Field m_score          */
  XNumber m_score;

  /** NEEDSDOC Field SCORE_NODETEST          */
  static final XNumber SCORE_NODETEST =
    new XNumber(XPath.MATCH_SCORE_NODETEST);

  /** NEEDSDOC Field SCORE_NSWILD          */
  static final XNumber SCORE_NSWILD = new XNumber(XPath.MATCH_SCORE_NSWILD);

  /** NEEDSDOC Field SCORE_QNAME          */
  static final XNumber SCORE_QNAME = new XNumber(XPath.MATCH_SCORE_QNAME);

  /** NEEDSDOC Field SCORE_OTHER          */
  static final XNumber SCORE_OTHER = new XNumber(XPath.MATCH_SCORE_OTHER);

  /** NEEDSDOC Field SCORE_NONE          */
  public static final XNumber SCORE_NONE =
    new XNumber(XPath.MATCH_SCORE_NONE);

  /**
   * Constructor NodeTest
   *
   *
   * NEEDSDOC @param whatToShow
   * NEEDSDOC @param namespace
   * NEEDSDOC @param name
   */
  public NodeTest(int whatToShow, String namespace, String name)
  {
    initNodeTest(whatToShow, namespace, name);
  }

  /**
   * Constructor NodeTest
   *
   *
   * NEEDSDOC @param whatToShow
   */
  public NodeTest(int whatToShow)
  {
    initNodeTest(whatToShow);
  }

  /**
   * Constructor NodeTest
   *
   */
  public NodeTest(){}

  /**
   * NEEDSDOC Method initNodeTest 
   *
   *
   * NEEDSDOC @param whatToShow
   */
  public void initNodeTest(int whatToShow)
  {

    m_whatToShow = whatToShow;

    calcScore();
  }

  /**
   * NEEDSDOC Method initNodeTest 
   *
   *
   * NEEDSDOC @param whatToShow
   * NEEDSDOC @param namespace
   * NEEDSDOC @param name
   */
  public void initNodeTest(int whatToShow, String namespace, String name)
  {

    m_whatToShow = whatToShow;
    m_namespace = namespace;
    m_name = name;

    calcScore();
  }

  /** NEEDSDOC Field m_isTotallyWild          */
  private boolean m_isTotallyWild;

  /**
   * Static calc of match score.
   */
  protected void calcScore()
  {

    if ((m_namespace == null) && (m_name == null))
      m_score = SCORE_NODETEST;
    else if (((m_namespace == WILD) || (m_namespace == null))
             && (m_name == WILD))
      m_score = SCORE_NODETEST;
    else if ((m_namespace != WILD) && (m_name == WILD))
      m_score = SCORE_NSWILD;
    else
      m_score = SCORE_QNAME;

    m_isTotallyWild = (m_namespace == null && m_name == WILD);
  }

  /**
   * NEEDSDOC Method getDefaultScore 
   *
   *
   * NEEDSDOC (getDefaultScore) @return
   */
  public double getDefaultScore()
  {
    return m_score.num();
  }

  /**
   * NEEDSDOC Method debugWhatToShow 
   *
   *
   * NEEDSDOC @param whatToShow
   */
  public static void debugWhatToShow(int whatToShow)
  {

    java.util.Vector v = new java.util.Vector();

    if (0 != (whatToShow & NodeFilter.SHOW_ATTRIBUTE))
      v.addElement("SHOW_ATTRIBUTE");

    if (0 != (whatToShow & NodeFilter.SHOW_CDATA_SECTION))
      v.addElement("SHOW_CDATA_SECTION");

    if (0 != (whatToShow & NodeFilter.SHOW_COMMENT))
      v.addElement("SHOW_COMMENT");

    if (0 != (whatToShow & NodeFilter.SHOW_DOCUMENT))
      v.addElement("SHOW_DOCUMENT");

    if (0 != (whatToShow & NodeFilter.SHOW_DOCUMENT_FRAGMENT))
      v.addElement("SHOW_DOCUMENT_FRAGMENT");

    if (0 != (whatToShow & NodeFilter.SHOW_DOCUMENT_TYPE))
      v.addElement("SHOW_DOCUMENT_TYPE");

    if (0 != (whatToShow & NodeFilter.SHOW_ELEMENT))
      v.addElement("SHOW_ELEMENT");

    if (0 != (whatToShow & NodeFilter.SHOW_ENTITY))
      v.addElement("SHOW_ENTITY");

    if (0 != (whatToShow & NodeFilter.SHOW_ENTITY_REFERENCE))
      v.addElement("SHOW_ENTITY_REFERENCE");

    if (0 != (whatToShow & NodeFilter.SHOW_NOTATION))
      v.addElement("SHOW_NOTATION");

    if (0 != (whatToShow & NodeFilter.SHOW_PROCESSING_INSTRUCTION))
      v.addElement("SHOW_PROCESSING_INSTRUCTION");

    if (0 != (whatToShow & NodeFilter.SHOW_TEXT))
      v.addElement("SHOW_TEXT");

    int n = v.size();

    for (int i = 0; i < n; i++)
    {
      if (i > 0)
        System.out.print(" | ");

      System.out.print(v.elementAt(i));
    }

    if (0 == n)
      System.out.print("empty whatToShow: " + whatToShow);

    System.out.println();
  }

  /**
   * Two names are equal if they and either both are null or
   * the name t is wild and the name p is non-null, or the two
   * strings are equal.
   *
   * NEEDSDOC @param p
   * NEEDSDOC @param t
   *
   * NEEDSDOC ($objectName$) @return
   */
  private static final boolean subPartMatch(String p, String t)
  {

    // boolean b = (p == t) || ((null != p) && ((t == WILD) || p.equals(t)));
    // System.out.println("subPartMatch - p: "+p+", t: "+t+", result: "+b);
    return (p == t) || ((null != p) && ((t == WILD) || p.equals(t)));
  }

  /**
   * Test a node to see if it matches the given node test.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param len The length of the argument.
   * @param len The type of the step.
   * @returns score in an XNumber, one of MATCH_SCORE_NODETEST,
   * MATCH_SCORE_NONE, MATCH_SCORE_OTHER, MATCH_SCORE_QNAME.
   *
   * NEEDSDOC @param xctxt
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws org.xml.sax.SAXException
   */
  public XObject execute(XPathContext xctxt) throws org.xml.sax.SAXException
  {

    if (m_whatToShow == NodeFilter.SHOW_ALL)
      return m_score;

    Node context = xctxt.getCurrentNode();
    short nodeType = context.getNodeType();
    int nodeBit = (m_whatToShow & (0x00000001 << (nodeType - 1)));

    switch (nodeBit)
    {
    case NodeFilter.SHOW_DOCUMENT :
      return SCORE_OTHER;
    case NodeFilter.SHOW_COMMENT :
      return m_score;
    case NodeFilter.SHOW_CDATA_SECTION :
    case NodeFilter.SHOW_TEXT :
      return (!xctxt.getDOMHelper().shouldStripSourceNode(context))
             ? m_score : SCORE_NONE;
    case NodeFilter.SHOW_PROCESSING_INSTRUCTION :
      return subPartMatch(context.getNodeName(), m_name)
             ? m_score : SCORE_NONE;

    // From the draft: "Two expanded names are equal if they 
    // have the same local part, and either both have no URI or 
    // both have the same URI."
    // "A node test * is true for any node of the principal node type. 
    // For example, child::* will select all element children of the 
    // context node, and attribute::* will select all attributes of 
    // the context node."
    // "A node test can have the form NCName:*. In this case, the prefix 
    // is expanded in the same way as with a QName using the context 
    // namespace declarations. The node test will be true for any node 
    // of the principal type whose expanded name has the URI to which 
    // the prefix expands, regardless of the local part of the name."
    case NodeFilter.SHOW_ATTRIBUTE :
    {
      int isNamespace = (m_whatToShow & SHOW_NAMESPACE);

      if (0 == isNamespace)
      {
        DOMHelper dh = xctxt.getDOMHelper();

        if (!dh.isNamespaceNode(context))
          return (m_isTotallyWild || (subPartMatch(dh.getNamespaceOfNode(context), m_namespace) && subPartMatch(dh.getLocalNameOfNode(context), m_name)))
                 ? m_score : SCORE_NONE;
        else
          return SCORE_NONE;
      }
      else
      {
        if (xctxt.getDOMHelper().isNamespaceNode(context))
        {
          String ns = context.getNodeValue();

          return (subPartMatch(ns, m_name)) ? m_score : SCORE_NONE;
        }
        else
          return SCORE_NONE;
      }
    }
    case NodeFilter.SHOW_ELEMENT :
    {
      DOMHelper dh = xctxt.getDOMHelper();

      return (m_isTotallyWild || (subPartMatch(dh.getNamespaceOfNode(context), m_namespace) && subPartMatch(dh.getLocalNameOfNode(context), m_name)))
             ? m_score : SCORE_NONE;
    }
    default :
      return SCORE_NONE;
    }  // end switch(testType)
  }
}
