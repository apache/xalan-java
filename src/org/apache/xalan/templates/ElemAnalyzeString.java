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
package org.apache.xalan.templates;

//import org.w3c.dom.*;
//import org.w3c.dom.traversal.NodeIterator;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMManager;

import org.apache.xml.utils.XMLString;

// Experemental
import org.apache.xml.dtm.ref.ExpandedNameTable;

import org.xml.sax.*;

import org.apache.xpath.*;
import org.apache.xpath.Expression;
import org.apache.xpath.axes.ContextNodeList;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XSequence;
import org.apache.xpath.objects.XSequenceImpl;
import org.apache.xpath.parser.regexp.*;

import java.util.Vector;

import org.apache.xml.utils.QName;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.ResultTreeHandler;
import org.apache.xalan.transformer.StackGuard;
import org.apache.xalan.transformer.ClonerToResultTree;
import org.apache.xalan.res.XSLMessages;

import org.apache.xpath.objects.XSequenceImpl;
import org.apache.xpath.objects.XNodeSequenceSingleton;
import org.apache.xpath.objects.XInteger;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import org.apache.xpath.ExpressionOwner;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:analyze-string.
 * <pre>
 * <!ELEMENT xsl:analyze-string
 *  (#PCDATA
 *   xsl:matching-substring
 *   | xsl:non-matching-substring)
 * >
 *
 * <!ATTLIST xsl:analyze-string
 *   select %expr; #REQUIRED
 *   regex %expr; #REQUIRED
 *   flags %expr;
 * >
 * </pre>
 * @see <a href="http://www.w3.org/TR/xslt#analyze-string">analyze-string in XSLT Specification</a>
 */
public class ElemAnalyzeString extends ElemTemplateElement implements ExpressionOwner
{
  /** Set true to request some basic status reports */
  static final boolean DEBUG = false;
  
    
  /**
   * Construct a element representing xsl:analyze-string.
   */
  public ElemAnalyzeString()
  {
  	super();
  }

  /**
   * The "select" expression.
   * @serial
   */
  protected Expression m_selectExpression = null;
  /**
   * The "regex" string.
   * @serial
   */
  protected AVT m_regex = null;
  /**
   * The "flags" string.
   * @serial
   */
  protected AVT m_flags = null;
  /**
   * The "matching-substring" element.
   * @serial
   */
  protected ElemMatchingSubstring m_matchingSubstring = null;
  /**
   * The "non-matching-substring" element.
   * @serial
   */
  protected ElemNonMatchingSubstring m_nonMatchingSubstring = null;
  
  /**
   * Used to store the the type of grouping used
   */
  private int m_groupType = 0;
  /**
   * Values for the types of grouping
   */
  private static final int TYPE_GROUP_BY = 1;
  private static final int TYPE_GROUP_ADJACENT = 2;
  private static final int TYPE_GROUP_STARTING_WITH = 3;
  private static final int TYPE_GROUP_ENDING_WITH = 4;

  /**
   * Set the "select" attribute.
   *
   * @param xpath The XPath expression for the "select" attribute.
   */
  public void setSelect(XPath xpath)
  {
    m_selectExpression = xpath.getExpression();
  }

  /**
   * Get the "select" attribute.
   *
   * @return The XPath expression for the "select" attribute.
   */
  public Expression getSelect()
  {
    return m_selectExpression;
  }
  
  
  /**
   * Set the "regex" attribute.
   *
   * @param regex The string to set for the "regex" attribute.
   */
  public void setRegex(AVT regex)
  {
    m_regex = regex;
  }

  /**
   * Get the "regex" attribute.
   *
   * @return The string to set for the "regex" attribute.
   */
  public AVT getRegex()
  {
    return m_regex;
  }
  
  /**
   * Set the "flags" attribute.
   *
   * @param regex The string to set for the "flags" attribute.
   */
  public void setFlags(AVT flags)
  {
    m_flags = flags;
  }

  /**
   * Get the "flags" attribute.
   *
   * @return The string to set for the "flags" attribute.
   */
  public AVT getFlags()
  {
    return m_flags;
  }
  
  /**
   * Set the "matching-substring" element.
   *
   * @param matchingSubstring The matching-substring element to set.
   */
  public void setMatchingSubstringElem(ElemMatchingSubstring matchingSubstring)
  {
    m_matchingSubstring = matchingSubstring;
  }

  /**
   * Get the "matchingSubstring" element.
   *
   * @return The matchingSubstring element.
   */
  public ElemTemplateElement getMatchingSubstringElem()
  {
    return m_matchingSubstring;
  }
  
  /**
   * Set the "non-matching-substring" element.
   *
   * @param nonMatchingSubstring The non-matching-substring element to set.
   */
  public void setNonMatchingSubstringElem(ElemNonMatchingSubstring nonMatchingSubstring)
  {
    m_nonMatchingSubstring = nonMatchingSubstring;
  }

  /**
   * Get the "nonMatchingSubstring" element.
   *
   * @return The nonMatchingSubstring element.
   */
  public ElemTemplateElement getNonMatchingSubstringElem()
  {
    return m_nonMatchingSubstring;
  }

  
  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for this element
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_ANALYZESTRING;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_ANALYZESTRING_STRING;
  }

  /**
   * Execute the xsl:analyze-string transformation
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {

    transformer.pushCurrentTemplateRuleIsNull(true);    
    if (TransformerImpl.S_DEBUG)
      transformer.getTraceManager().fireTraceEvent(this);

    try
    {
     XPathContext xctxt = transformer.getXPathContext();
    int contextNode = xctxt.getContextNode();
      
    String regexValue = m_regex.evaluate(xctxt, contextNode, this);
    int patLen = 0;
    if (regexValue == null || !((patLen = regexValue.length()) > 0))
    this.error(XSLTErrorResources.ER_ILLEGAL_ATTRIBUTE_VALUE, new Object[]{Constants.ATTRNAME_REGEX, regexValue});
    
    String flagsValue = "";
    if (null != m_flags)
    {
      flagsValue = m_flags.evaluate(xctxt, contextNode, this);
    }
    
    String selectResult = m_selectExpression.execute(xctxt).str();
    XSequenceImpl matchSeq = new XSequenceImpl();
    XSequenceImpl noMatchSeq = new XSequenceImpl();;
    
     
    RegularExpression regexp = new RegularExpression(regexValue, flagsValue);
    int index = 0;
    int length = selectResult.length();
    int i=0, j=0;    	
    while (index < length)
    {
    	int[] range = regexp.matchString(selectResult, index, length);
    	int start = range[0];
    	int end = range[1];
    	if (end >0)
    	{
    		matchSeq.insertItemAt(new XString(selectResult.substring(start, end)), i++);
    	    noMatchSeq.insertItemAt(new XString(selectResult.substring(index, start)), j++); 
    	    index = end;
    	}
    	else
    	{
    	   noMatchSeq.insertItemAt(new XString(selectResult.substring(index)), j++);     	    	
    	   index = length;
    	}
    	   if (m_nonMatchingSubstring != null)
    	   {
    		   m_nonMatchingSubstring.setRegexGroup(noMatchSeq);
    		   xctxt.setSAXLocator(m_nonMatchingSubstring);
               transformer.setCurrentElement(m_nonMatchingSubstring);
    	       m_nonMatchingSubstring.execute(transformer);
    	   }
    	   if (m_matchingSubstring != null)
    	   {
    	       m_matchingSubstring.setRegexGroup(matchSeq);
    	       xctxt.setSAXLocator(m_matchingSubstring);
               transformer.setCurrentElement(m_matchingSubstring);
    	       m_matchingSubstring.execute(transformer);
    	   }
      }
    }
    finally
    {
      if (TransformerImpl.S_DEBUG)
	    transformer.getTraceManager().fireTraceEndEvent(this); 
      transformer.popCurrentTemplateRuleIsNull();
    }
  }

  /**
   * Get template element associated with this
   *
   *
   * @return template element associated with this (itself)
   */
  protected ElemTemplateElement getTemplateMatch()
  {
    return this;
  }
  
   

  /**
   * Add a child to the child list.
   * <!ELEMENT xsl:matching-string|xsl:non-matching-string>
   * 
   *
   * @param newChild Child to add to child list
   *
   * @return Child just added to child list
   */
  public ElemTemplateElement appendChild(ElemTemplateElement newChild)
  {

    int type = ((ElemTemplateElement) newChild).getXSLToken();

    if (Constants.ELEMNAME_MATCHINGSUBSTRING == type)
    {
      setMatchingSubstringElem((ElemMatchingSubstring) newChild);

      //return newChild;
    }
    else
    //if (Constants.ELEMNAME_NONMATCHINGSTRING == type)
    {
      setNonMatchingSubstringElem((ElemNonMatchingSubstring) newChild);

      //return newChild;
    }
    //should we throw an error if anything else or does 
    // schema already take care of that??
    return super.appendChild(newChild); 
  }
  
  /**
   * Call the children visitors.
   * @param visitor The visitor whose appropriate method will be called.
   */
  public void callChildVisitors(XSLTVisitor visitor, boolean callAttributes)
  {
  	if(callAttributes)
  	{ 
  		if(null != m_selectExpression)
  		  m_selectExpression.callVisitors(this, visitor);
  		if(null != m_regex)
  		  m_regex.callVisitors(visitor);
  		if(null != m_flags)
  		  m_flags.callVisitors(visitor);
  	}
  		
    getMatchingSubstringElem().callVisitors(visitor);
    getNonMatchingSubstringElem().callVisitors(visitor);

    super.callChildVisitors(visitor, callAttributes);
  }

  /**
   * @see ExpressionOwner#getExpression()
   */
  public Expression getExpression()
  {
    return m_selectExpression;
  }

  /**
   * @see ExpressionOwner#setExpression(Expression)
   */
  public void setExpression(Expression exp)
  {
  	exp.exprSetParent(this);
  	m_selectExpression = exp;
  }
  
  

}
