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
import org.apache.xpath.WhitespaceStrippingElementMatcher;
import org.apache.xml.utils.PrefixResolver;

import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;

/**
 * <meta name="usage" content="advanced"/>
 * This is the basic node test class for both match patterns and location path 
 * steps.
 */
public class NodeTest extends Expression
{

  /** The namespace or local name for node tests with a wildcard.
   *  @see <a href="http://www.w3.org/TR/xpath#NT-NameTest">the XPath NameTest production.</a> */
  public static final String WILD = "*";
  
  /** The URL to pass to the Node#supports method, to see if the 
   * DOM has already been stripped of whitespace nodes. */
  public static final String SUPPORTS_PRE_STRIPPING =
    "http://xml.apache.org/xpath/features/whitespace-pre-stripping";

  /**
   * This attribute determines which node types are accepted.
   * @serial
   */
  protected int m_whatToShow;

  /** This bit specifies a namespace, and extends the SHOW_XXX stuff 
   *  in {@link org.w3c.dom.traversal.NodeFilter}. */
  public static final int SHOW_NAMESPACE = 0x00001000;

  /**
   * Special bitmap for match patterns starting with a function.
   * Make sure this does not conflict with {@link org.w3c.dom.traversal.NodeFilter}.
   */
  public static final int SHOW_BYFUNCTION = 0x00010000;

  /**
   * This attribute determines which node types are accepted.
   * These constants are defined in the {@link org.w3c.dom.traversal.NodeFilter}
   * interface.
   *
   * @return bitset mainly defined in {@link org.w3c.dom.traversal.NodeFilter}.
   */
  public int getWhatToShow()
  {
    return m_whatToShow;
  }

  /** The namespace to be tested for, which may be null.
   *  @serial */
  String m_namespace;

  /**
   * Return the namespace to be tested.
   *
   * @return The namespace to be tested for, or {@link #WILD}, or null.
   */
  public String getNamespace()
  {
    return m_namespace;
  }

  /** The local name to be tested for.
   *  @serial */
  String m_name;

  /**
   * Return the local namespace to be tested.
   *
   * @return the local namespace to be tested, or {@link #WILD}, or an empty string.
   */
  public String getLocalName()
  {
    return (null == m_name) ? "" : m_name;
  }

  /** Statically calculated score for this test.  One of
   *  {@link #SCORE_NODETEST}, 
   *  {@link #SCORE_NONE}, 
   *  {@link #SCORE_NSWILD}, 
   *  {@link #SCORE_QNAME}, or
   *  {@link #SCORE_OTHER}.
   *  @serial
   */
  XNumber m_score;

  /** 
   * The match score if the pattern consists of just a NodeTest.
   *  @see <a href="http://www.w3.org/TR/xslt#conflict">XSLT Specification - 5.5 Conflict Resolution for Template Rules</a> */
  public static final XNumber SCORE_NODETEST =
    new XNumber(XPath.MATCH_SCORE_NODETEST);

  /** 
   * The match score if the pattern pattern has the form NCName:*.
   *  @see <a href="http://www.w3.org/TR/xslt#conflict">XSLT Specification - 5.5 Conflict Resolution for Template Rules</a> */
  public static final XNumber SCORE_NSWILD = new XNumber(XPath.MATCH_SCORE_NSWILD);

  /** 
   * The match score if the pattern has the form
   * of a QName optionally preceded by an @ character.
   *  @see <a href="http://www.w3.org/TR/xslt#conflict">XSLT Specification - 5.5 Conflict Resolution for Template Rules</a> */
  public static final XNumber SCORE_QNAME = new XNumber(XPath.MATCH_SCORE_QNAME);

  /** 
   * The match score if the pattern consists of something
   * other than just a NodeTest or just a qname.
   *  @see <a href="http://www.w3.org/TR/xslt#conflict">XSLT Specification - 5.5 Conflict Resolution for Template Rules</a> */
  public static final XNumber SCORE_OTHER = new XNumber(XPath.MATCH_SCORE_OTHER);

  /** 
   * The match score if no match is made.
   *  @see <a href="http://www.w3.org/TR/xslt#conflict">XSLT Specification - 5.5 Conflict Resolution for Template Rules</a> */
  public static final XNumber SCORE_NONE =
    new XNumber(XPath.MATCH_SCORE_NONE);

  /**
   * Construct an NodeTest that tests for namespaces and node names.
   *
   *
   * @param whatToShow Bit set defined mainly by {@link org.w3c.dom.traversal.NodeFilter}.
   * @param namespace The namespace to be tested.
   * @param name The local name to be tested.
   */
  public NodeTest(int whatToShow, String namespace, String name)
  {
    initNodeTest(whatToShow, namespace, name);
  }

  /**
   * Construct an NodeTest that doesn't test for node names.
   *
   *
   * @param whatToShow Bit set defined mainly by {@link org.w3c.dom.traversal.NodeFilter}.
   */
  public NodeTest(int whatToShow)
  {
    initNodeTest(whatToShow);
  }

  /**
   * Null argument constructor.
   */
  public NodeTest(){}

  /**
   * Initialize this node test by setting the whatToShow property, and 
   * calculating the score that this test will return if a test succeeds.
   *
   *
   * @param whatToShow Bit set defined mainly by {@link org.w3c.dom.traversal.NodeFilter}.
   */
  public void initNodeTest(int whatToShow)
  {

    m_whatToShow = whatToShow;

    calcScore();
  }

  /**
   * Initialize this node test by setting the whatToShow property and the 
   * namespace and local name, and 
   * calculating the score that this test will return if a test succeeds.
   *
   *
   * @param whatToShow Bit set defined mainly by {@link org.w3c.dom.traversal.NodeFilter}.
   * @param namespace The namespace to be tested.
   * @param name The local name to be tested.
   */
  public void initNodeTest(int whatToShow, String namespace, String name)
  {

    m_whatToShow = whatToShow;
    m_namespace = namespace;
    m_name = name;

    calcScore();
  }

  /** True if this test has a null namespace and a local name of {@link #WILD}.
   *  @serial */
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
   * Get the score that this test will return if a test succeeds.
   *
   *
   * @return the score that this test will return if a test succeeds.
   */
  public double getDefaultScore()
  {
    return m_score.num();
  }

  /**
   * Do a diagnostics dump of a whatToShow bit set.
   *
   *
   * @param whatToShow Bit set defined mainly by {@link org.w3c.dom.traversal.NodeFilter}.
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
   * @param p part string from the node.
   * @param t target string, which may be {@link #WILD}.
   *
   * @return true if the strings match according to the rules of this method.
   */
  private static final boolean subPartMatch(String p, String t)
  {

    // boolean b = (p == t) || ((null != p) && ((t == WILD) || p.equals(t)));
    // System.out.println("subPartMatch - p: "+p+", t: "+t+", result: "+b);
    return (p == t) || ((null != p) && ((t == WILD) || p.equals(t)));
  }
  
  /**
   * This is temporary to patch over Xerces issue with representing DOM 
   * namespaces as "".
   *
   * @param p part string from the node, which may represent the null namespace 
   *        as null or as "".
   * @param t target string, which may be {@link #WILD}.
   *
   * @return true if the strings match according to the rules of this method.
   */
  private static final boolean subPartMatchNS(String p, String t)
  {

    return (p == t) || ((null != p) && ((p.length() > 0) ? ((t == WILD) || p.equals(t)) : null == t));
  }


  /**
   * Tell what the test score is for the given node.
   *
   *
   * @param xctxt XPath runtime context.
   * @param context The node being tested.
   *
   * @return {@link org.apache.xpath.patterns.NodeTest#SCORE_NODETEST}, 
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_NONE}, 
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_NSWILD}, 
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_QNAME}, or
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_OTHER}.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt, Node context)
          throws javax.xml.transform.TransformerException
  {
    short nodeType = context.getNodeType();
    
    // Yuck!  Blech!  -sb
    if((Node.TEXT_NODE == nodeType || Node.CDATA_SECTION_NODE == nodeType) && !context.isSupported(SUPPORTS_PRE_STRIPPING, null))
    {
      Node parent = context.getParentNode();
      if(null != parent && Node.ELEMENT_NODE == parent.getNodeType())
      {
        String data = context.getNodeValue();
        if(org.apache.xml.utils.XMLCharacterRecognizer.isWhiteSpace(data))
        {
          // Ugly trick for now.
          PrefixResolver resolver = xctxt.getNamespaceContext();
          if(resolver instanceof WhitespaceStrippingElementMatcher)
          {
            WhitespaceStrippingElementMatcher wsem = 
               (WhitespaceStrippingElementMatcher)resolver;
            if(wsem.shouldStripWhiteSpace(xctxt, (org.w3c.dom.Element)parent))
            {
              return SCORE_NONE;
            }
          }
        }
      }
    }

    if (m_whatToShow == NodeFilter.SHOW_ALL)
      return m_score;

    int nodeBit = (m_whatToShow & (0x00000001 << (nodeType - 1)));

    switch (nodeBit)
    {
    case NodeFilter.SHOW_DOCUMENT_FRAGMENT :
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
          return (m_isTotallyWild || (subPartMatchNS(dh.getNamespaceOfNode(context), m_namespace) && subPartMatch(dh.getLocalNameOfNode(context), m_name)))
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

      return (m_isTotallyWild || (subPartMatchNS(dh.getNamespaceOfNode(context), m_namespace) && subPartMatch(dh.getLocalNameOfNode(context), m_name)))
             ? m_score : SCORE_NONE;
    }
    default :
      return SCORE_NONE;
    }  // end switch(testType)
  }

  /**
   * Test the current node to see if it matches the given node test.
   *
   * @param xctxt XPath runtime context.
   *
   * @return {@link org.apache.xpath.patterns.NodeTest#SCORE_NODETEST},
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_NONE},
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_NSWILD},
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_QNAME}, or
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_OTHER}.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {

    return execute(xctxt, xctxt.getCurrentNode());
  }
}
