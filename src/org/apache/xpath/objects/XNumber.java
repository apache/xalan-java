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
package org.apache.xpath.objects;

import org.w3c.dom.*;

/**
 * <meta name="usage" content="general"/>
 * This class represents an XPath number, and is capable of 
 * converting the number to other types, such as a string.
 */
public class XNumber extends XObject
{
  double m_val;
  
  /**
   * Construct a XNodeSet object.
   */
  public XNumber(double d)
  {
    super();
    m_val = d;
  }
  
  /**
   * Tell that this is a CLASS_NUMBER.
   */
  public int getType()
  {
    return CLASS_NUMBER;
  }
  
  /**
   * Given a request type, return the equivalent string. 
   * For diagnostic purposes.
   */
  private String getTypeString()
  {
    return "#NUMBER";
  }
  
  /**
   * Cast result object to a number.
   */
  public double num()
  {
    return m_val;
  }

  /**
   * Cast result object to a boolean.
   */
  public boolean bool()
  {
    return (Double.isNaN(m_val) || (m_val == 0.0)) ? false : true;
  }
  
  /**
   * Cast result object to a string.
   */
  public String str()
  {
    if(Double.isNaN(m_val))
    {
      return "NaN";
    }
    else if(Double.isInfinite(m_val))
    {
      if(m_val > 0)
        return "Infinity";
      else
        return "-Infinity";
    }
    
    // java.text.NumberFormat formatter 
    //         = java.text.NumberFormat.getNumberInstance();
    java.text.DecimalFormat formatter = new java.text.DecimalFormat("0.##################");
  
    String s = formatter.format(m_val);
    
    // Hack to get around some VMs tendency to format integers to '1.0' 
    // instead of '1'.  I would rather do that than muck with the pattern 
    // handed in to NumberFormat, since that is locale specific.
    // (not that this is locale sensitive... but at least it should be 
    // non-harmful in the case of a different locale...).
    if(s.endsWith(".0"))
    {
      s = s.substring(0, s.length() - 2);
    }
    return s;
  }
  
  /**
   * Return a java object that's closes to the represenation 
   * that should be handed to an extension.
   */
  public Object object()
  {
    return new Double(m_val);
  }

  /**
   * Tell if two objects are functionally equal.
   */
  public boolean equals(XObject obj2)
    throws org.xml.sax.SAXException
  {
    // In order to handle the 'all' semantics of 
    // nodeset comparisons, we always call the 
    // nodeset function.
    if(obj2.getType() == XObject.CLASS_NODESET)
      return obj2.equals(this);

    return m_val == obj2.num();
  }

}

