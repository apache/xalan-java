/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights 
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
 * Implement xsl:non-matching-substring.
 * <pre>
 * <!ELEMENT xsl:non-matching-substring
 *  (#PCDATA)
 * >
 *
 * 
 * </pre>
 * @see <a href="http://www.w3.org/TR/xslt#non-matching-substring">non-matching-substring in XSLT Specification</a>
 */
public class ElemNonMatchingSubstring extends ElemMatchingSubstring
{
  /** Set true to request some basic status reports */
  static final boolean DEBUG = false;
  
    
  /**
   * Construct a element representing xsl:analyze-string.
   */
  public ElemNonMatchingSubstring()
  {
  	super();
  }
  
  
  
  
  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for this element
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_NONMATCHINGSUBSTRING;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_NONMATCHINGSUBSTRING_STRING;
  }


  /**
   * Get the substring sequence.
   *
   * @return The sequence of substrings.
   */
  public XSequence getRegexGroup()
  {
    return XSequence.EMPTY;
  }
  

}
