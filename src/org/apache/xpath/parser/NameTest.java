/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights 
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

