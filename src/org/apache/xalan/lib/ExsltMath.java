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
 * This class contains EXSLT math extension functions.
 * It is accessed by specifying a namespace URI as follows:
 * <pre>
 *    xmlns:math="http://exslt.org/math"
 * </pre>
 * 
 * The documentation for each function has been copied from the relevant
 * EXSLT Implementer page.
 * 
 * @see <a href="http://www.exslt.org/">EXSLT</a>

 */
public class ExsltMath
{
  /**
   * The math:max function returns the maximum value of the nodes passed as the argument. 
   * The maximum value is defined as follows. The node set passed as an argument is sorted 
   * in descending order as it would be by xsl:sort with a data type of number. The maximum 
   * is the result of converting the string value of the first node in this sorted list to 
   * a number using the number function. 
   * 
   * If the node set is empty, or if the result of converting the string values of any of the 
   * nodes to a number is NaN, then NaN is returned.
   * 
   * @param expCon is passed in by the Xalan extension processor
   * @param ni The NodeIterator for the node-set to be evaluated.
   * 
   * @return String representation of the maximum value found, NaN if any node cannot be 
   * converted to a number.
   * 
   * @see <a href="http://www.exslt.org/">EXSLT</a>
   */
  public static String max (ExpressionContext expCon, NodeIterator ni)
  {
    NodeSet ns = new NodeSet(ni);
    Node maxNode = null;
    double m = Double.MIN_VALUE;
    for (int i = 0; i < ns.getLength(); i++)
    {
      Node n = ns.elementAt(i);
      double d = expCon.toNumber(n);
      if (Double.isNaN(d))
        return "NaN";
      else if (d > m)
      {
        m = d;
        maxNode = n;
      }
    }
    return expCon.toString(maxNode);      
  }

  /**
   * The math:min function returns the minimum value of the nodes passed as the argument. 
   * The minimum value is defined as follows. The node set passed as an argument is sorted 
   * in ascending order as it would be by xsl:sort with a data type of number. The minimum 
   * is the result of converting the string value of the first node in this sorted list to 
   * a number using the number function. 
   * 
   * If the node set is empty, or if the result of converting the string values of any of 
   * the nodes to a number is NaN, then NaN is returned.
   * 
   * @param expCon is passed in by the Xalan extension processor
   * @param ni The NodeIterator for the node-set to be evaluated.
   * 
   * @return String representation of the minimum value found, NaN if any node cannot be 
   * converted to a number.
   * 
   * @see <a href="http://www.exslt.org/">EXSLT</a>
   */
  public static String min (ExpressionContext expCon, NodeIterator ni)
  {
    NodeSet ns = new NodeSet(ni);
    Node minNode = null;
    double m = Double.MAX_VALUE;
    for (int i = 0; i < ns.getLength(); i++)
    {
      Node n = ns.elementAt(i);
      double d = expCon.toNumber(n);
      if (Double.isNaN(d))
        return "NaN";
      else if (d < m)
      {
        m = d;
        minNode = n;
      }
    }
    return expCon.toString(minNode);
  }
  
  /**
   * The math:highest function returns the nodes in the node set whose value is the maximum 
   * value for the node set. The maximum value for the node set is the same as the value as 
   * calculated by math:max. A node has this maximum value if the result of converting its 
   * string value to a number as if by the number function is equal to the maximum value, 
   * where the equality comparison is defined as a numerical comparison using the = operator.
   * 
   * If any of the nodes in the node set has a non-numeric value, the math:max function will 
   * return NaN. The definition numeric comparisons entails that NaN != NaN. Therefore if any 
   * of the nodes in the node set has a non-numeric value, math:highest will return an empty 
   * node set. 
   * 
   * @param expCon is passed in by the Xalan extension processor
   * @param ni The NodeIterator for the node-set to be evaluated.
   * 
   * @return node-set with nodes containing the maximum value found, an empty node-set
   * if any node cannot be converted to a number.
   */
  public static NodeSet highest (ExpressionContext expCon, NodeIterator ni)
    throws java.lang.CloneNotSupportedException
  {    
    NodeSet ns = new NodeSet(ni);
    NodeIterator niClone = ns.cloneWithReset();
    double high = new Double(max(expCon, niClone)).doubleValue();
    NodeSet highNodes = new NodeSet();
    highNodes.setShouldCacheNodes(true);
    
    if (Double.isNaN(high))
      return highNodes;  // empty Nodeset
    
     for (int i = 0; i < ns.getLength(); i++)
    {
      Node n = ns.elementAt(i);
      double d = expCon.toNumber(n); 
      if (d == high)
      highNodes.addElement(n);
    }
    return highNodes;
  }
  
  /**
   * The math:lowest function returns the nodes in the node set whose value is the minimum value 
   * for the node set. The minimum value for the node set is the same as the value as calculated 
   * by math:min. A node has this minimum value if the result of converting its string value to 
   * a number as if by the number function is equal to the minimum value, where the equality 
   * comparison is defined as a numerical comparison using the = operator.
   * 
   * If any of the nodes in the node set has a non-numeric value, the math:min function will return 
   * NaN. The definition numeric comparisons entails that NaN != NaN. Therefore if any of the nodes 
   * in the node set has a non-numeric value, math:lowest will return an empty node set.
   * 
   * @param expCon is passed in by the Xalan extension processor
   * @param ni The NodeIterator for the node-set to be evaluated.
   * 
   * @return node-set with nodes containing the minimum value found, an empty node-set
   * if any node cannot be converted to a number.
   * 
   */
  public static NodeSet lowest (ExpressionContext expCon, NodeIterator ni)
    throws java.lang.CloneNotSupportedException
  {
    NodeSet ns = new NodeSet(ni);
    NodeIterator niClone = ns.cloneWithReset();
    double low = new Double(min(expCon, niClone)).doubleValue();

    NodeSet lowNodes = new NodeSet();
    lowNodes.setShouldCacheNodes(true);
    
    if (Double.isNaN(low))
      return lowNodes;  // empty Nodeset
    
     for (int i = 0; i < ns.getLength(); i++)
    {
      Node n = ns.elementAt(i);
      double d = expCon.toNumber(n); 
      if (d == low)
      lowNodes.addElement(n);
    }
    return lowNodes;
  }  
}