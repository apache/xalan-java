package org.apache.xpath.parser;

import org.apache.xml.dtm.DTMFilter;
import org.apache.xpath.objects.XString;

/**
 * This is an expression node that only exists for construction 
 * purposes. 
 */
public class NameTest extends NonExecutableExpression
{  
  public NameTest(XPath parser)
  {
  	super(parser);
  }
  
  void processKindTest(KindTest kind)
  {
    if (kind instanceof AnyKindTest)
    {
      m_isTotallyWild = true;
      // m_name = org.apache.xpath.patterns.NodeTest.WILD;
      m_name = org.apache.xpath.patterns.StepPattern.PSEUDONAME_ANY;
      m_namespace = null;
      if (m_parser.m_predLevel == 0 && m_parser.m_isMatchPattern)
        m_whatToShow =
          ~DTMFilter.SHOW_ATTRIBUTE
            & ~DTMFilter.SHOW_DOCUMENT
            & ~DTMFilter.SHOW_DOCUMENT_FRAGMENT;
      else
        m_whatToShow = DTMFilter.SHOW_ALL;
    }
    else
      if (kind instanceof TextTest)
      {
        m_isTotallyWild = false;
        m_name = org.apache.xpath.patterns.StepPattern.PSEUDONAME_TEXT;
        m_namespace = null;
        m_whatToShow = DTMFilter.SHOW_TEXT;
      }
      else
        if (kind instanceof CommentTest)
        {
          m_isTotallyWild = false;
          m_name = org.apache.xpath.patterns.StepPattern.PSEUDONAME_COMMENT;
          m_namespace = null;
          m_whatToShow = DTMFilter.SHOW_COMMENT;
        }
        else
          if (kind instanceof ProcessingInstructionTest)
          {
            m_isTotallyWild = false;
            m_name = ((ProcessingInstructionTest)kind).getLocalName();
            m_namespace = null;
            m_whatToShow = DTMFilter.SHOW_PROCESSING_INSTRUCTION;
          }
  }

  public void jjtAddChild(Node n, int i) 
  {
    if(n instanceof QName)
    {
    	QName qname = ((QName)n);
    	m_namespace = qname.getNamespaceURI();
    	m_name = qname.getLocalName();
    	m_whatToShow = DTMFilter.SHOW_ELEMENT;
    }
    else if(n instanceof Star)
    {
    	m_isTotallyWild = true;
    	m_name = org.apache.xpath.patterns.NodeTest.WILD;
    	// m_name = null;
    	m_namespace = null;
    	m_whatToShow = DTMFilter.SHOW_ELEMENT;
    }
    else if(n instanceof KindTest)
    {
    	if(jjtGetNumChildren() > 0)
    	{
     	  processKindTest((KindTest)n.jjtGetChild(0));
    	}
    	else
    	{
    	  processKindTest((KindTest)n);
    	}
    	super.jjtAddChild(n, i);
    }
    else
    {
    	// Probably this is a CommentTest, TextTest, etc.
    	// super.jjtAddChild(n, i);
    	// Assertion, should never happen.
    	throw new RuntimeException("node can only be a QName, Wildcard, or KindTest!");
    }
  }
  
  private boolean m_isTotallyWild = false;
  
  public boolean isTotallyWild()
  {
  	return m_isTotallyWild;
  }

  public void setIsTotallyWild(boolean b)
  {
  	m_isTotallyWild = b;
  }

  
  /**
   * The namespace to be tested for, which may be null.
   *  @serial 
   */
  String m_namespace;

  /**
   * The local name to be tested for.
   *  @serial 
   */
  String m_name;
  
  public String getNamespaceURI()
  {
  	return m_namespace;
  }

  public String getLocalName()
  {
  	return m_name;
  }
  
  int m_whatToShow = DTMFilter.SHOW_ALL;
  
  public int getWhatToShow()
  {
      return m_whatToShow; // ??
  }



}

