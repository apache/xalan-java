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

public class NodeTest extends Expression
{
  public static final String WILD = "*";

  /**
   * This attribute determines which node types are accepted.
   */
  protected int m_whatToShow;
  
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
   */
  public int getWhatToShow()
  {
    return m_whatToShow;
  }
  
  String m_namespace;
  
  /**
   * Return the namespace to be tested.
   */
  public String getNamespace()
  {
    return m_namespace;
  }
  
  String m_name;
  
  /**
   * Return the local namespace to be tested.
   */
  public String getLocalName()
  {
    return m_name;
  }
  
  XNumber m_score;
  
  static final XNumber SCORE_NODETEST 
    = new XNumber( XPath.MATCH_SCORE_NODETEST );
  static final XNumber SCORE_NSWILD 
    = new XNumber( XPath.MATCH_SCORE_NSWILD );
  static final XNumber SCORE_QNAME 
    = new XNumber( XPath.MATCH_SCORE_QNAME );
  static final XNumber SCORE_OTHER 
    = new XNumber( XPath.MATCH_SCORE_OTHER );
  public static final XNumber SCORE_NONE 
    = new XNumber( XPath.MATCH_SCORE_NONE );
  
  public NodeTest(int whatToShow, String namespace, String name)
  {
    initNodeTest(whatToShow, namespace, name);
  }
  
  public NodeTest(int whatToShow)
  {
    initNodeTest(whatToShow);
  }
  
  public NodeTest()
  {
  }
  
  public void initNodeTest(int whatToShow)
  {
    m_whatToShow = whatToShow;
    calcScore();
  }
  
  public void initNodeTest(int whatToShow, String namespace, String name)
  {
    m_whatToShow = whatToShow;
    m_namespace = namespace;
    m_name = name;
    calcScore();
  }
  
  private boolean m_isTotallyWild;
  
  /**
   * Static calc of match score.
   */
  protected void calcScore()
  {
    if((m_namespace == null) && (m_name == null))
      m_score = SCORE_NODETEST;
    else if(((m_namespace == WILD) || (m_namespace == null)) && (m_name == WILD))
      m_score = SCORE_NODETEST;
    else if((m_namespace != WILD) && (m_name == WILD))
      m_score = SCORE_NSWILD;
    else
      m_score = SCORE_QNAME;
    
    m_isTotallyWild = ( m_namespace == null && m_name == WILD);
  }
  
  public double getDefaultScore()
  {
    return m_score.num();
  }  
  
  public static void debugWhatToShow(int whatToShow)
  {
    java.util.Vector v = new java.util.Vector();
    if(0 != (whatToShow & NodeFilter.SHOW_ATTRIBUTE))
      v.addElement("SHOW_ATTRIBUTE");
    if(0 != (whatToShow & NodeFilter.SHOW_CDATA_SECTION))
      v.addElement("SHOW_CDATA_SECTION");
    if(0 != (whatToShow & NodeFilter.SHOW_COMMENT))
      v.addElement("SHOW_COMMENT");
    if(0 != (whatToShow & NodeFilter.SHOW_DOCUMENT))
      v.addElement("SHOW_DOCUMENT");
    if(0 != (whatToShow & NodeFilter.SHOW_DOCUMENT_FRAGMENT))
      v.addElement("SHOW_DOCUMENT_FRAGMENT");
    if(0 != (whatToShow & NodeFilter.SHOW_DOCUMENT_TYPE))
      v.addElement("SHOW_DOCUMENT_TYPE");
    if(0 != (whatToShow & NodeFilter.SHOW_ELEMENT))
      v.addElement("SHOW_ELEMENT");
    if(0 != (whatToShow & NodeFilter.SHOW_ENTITY))
      v.addElement("SHOW_ENTITY");
    if(0 != (whatToShow & NodeFilter.SHOW_ENTITY_REFERENCE))
      v.addElement("SHOW_ENTITY_REFERENCE");
    if(0 != (whatToShow & NodeFilter.SHOW_NOTATION))
      v.addElement("SHOW_NOTATION");
    if(0 != (whatToShow & NodeFilter.SHOW_PROCESSING_INSTRUCTION))
      v.addElement("SHOW_PROCESSING_INSTRUCTION");
    if(0 != (whatToShow & NodeFilter.SHOW_TEXT))
      v.addElement("SHOW_TEXT");
    int n = v.size();
    for(int i = 0; i < n; i++)
    {
      if(i > 0)
        System.out.print(" | ");
      System.out.print(v.elementAt(i));
    }
    if(0 == n)
      System.out.print("empty whatToShow: "+whatToShow);
    System.out.println();
  }

  /**
   * Two names are equal if they and either both are null or 
   * the name t is wild and the name p is non-null, or the two 
   * strings are equal.
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
   */
  public XObject execute(XPathContext xctxt)
    throws org.xml.sax.SAXException
  {
    int whatToShow = getWhatToShow();
    // debugWhatToShow(whatToShow);
    if(whatToShow == NodeFilter.SHOW_ALL)
      return m_score;
    
    Node context = xctxt.getCurrentNode();
    int nodeType = context.getNodeType();

    int nodeBit = (whatToShow & (0x00000001 << (nodeType-1)));

    switch(nodeBit)
    {
    case NodeFilter.SHOW_DOCUMENT:
      return SCORE_OTHER;
    case NodeFilter.SHOW_COMMENT:
      return m_score;
      
    case NodeFilter.SHOW_CDATA_SECTION:
    case NodeFilter.SHOW_TEXT:
      return (!xctxt.getDOMHelper().shouldStripSourceNode(context))
             ? m_score : SCORE_NONE;

    case NodeFilter.SHOW_PROCESSING_INSTRUCTION:
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

    case NodeFilter.SHOW_ATTRIBUTE:
      {
        int isNamespace = (whatToShow & SHOW_NAMESPACE);
        if(0 == isNamespace)
        {
          if(!xctxt.getDOMHelper().isNamespaceNode(context))
            return (m_isTotallyWild ||
                    (subPartMatch(context.getNamespaceURI(), m_namespace) 
                    && subPartMatch(context.getLocalName(), m_name))) ?
                   m_score : SCORE_NONE;
          else
            return SCORE_NONE;
        }
        else
        {
          if(xctxt.getDOMHelper().isNamespaceNode(context))
          {
            String ns = context.getNodeValue();
            return (subPartMatch(ns, m_name)) ?
                   m_score : SCORE_NONE;
          }
          else
            return SCORE_NONE;
        }
      }
      
    case NodeFilter.SHOW_ELEMENT:
      {
        return (m_isTotallyWild ||
                (subPartMatch(context.getNamespaceURI(), m_namespace) 
                && subPartMatch(context.getLocalName(), m_name))) ?
               m_score : SCORE_NONE;
      }
      
    default:
      return SCORE_NONE;
    } // end switch(testType)
    
  }
  
}
