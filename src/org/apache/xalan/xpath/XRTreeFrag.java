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
package org.apache.xalan.xpath;

import org.w3c.dom.*;
import org.apache.xalan.xpath.DOMHelper;

/**
 * <meta name="usage" content="general"/>
 * This class represents an XPath result tree fragment object, and is capable of 
 * converting the RTF to other types, such as a string.
 */
public class XRTreeFrag extends XObject
{  
  /**
   * Create an XObject.
   */
  public XRTreeFrag(DocumentFragment frag)
  {
    super(frag);
  }
  
  /**
   * Tell what kind of class this is.
   */
  public int getType()
  {
    return CLASS_RTREEFRAG;
  }

  /**
   * Given a request type, return the equivalent string. 
   * For diagnostic purposes.
   */
  private String getTypeString()
  {
    return "#RTREEFRAG";
  }
  
  /**
   * Cast result object to a number.
   */
  public double num()
  {
    java.text.NumberFormat m_formatter = java.text.NumberFormat.getNumberInstance();
    double result;
    String s = DOMHelper.getNodeData((DocumentFragment)m_obj);
    if(null != s)
    {
      try
      {
        // result = Double.valueOf(s).doubleValue();
        Number n = m_formatter.parse(s.trim());
        result = n.doubleValue();
      }
      // catch(NumberFormatException nfe)
      catch(java.text.ParseException nfe)
      {
        result = Double.NaN;
      }
    }
    else
    {
      result = Double.NaN;
    }

    return result;
  }

  /**
   * Cast result object to a boolean.
   */
  public boolean bool()
  {
    boolean result = false;
    NodeList nl = ((DocumentFragment)m_obj).getChildNodes();
    int nChildren = nl.getLength();
    for(int i = 0; i < nChildren; i++)
    {
      Node n = nl.item(i);
      if((Node.TEXT_NODE == n.getNodeType()) &&
          (((Text)n).getData().trim().length() == 0))
      {
        continue;
      }
      
      result = true;
      break;
    }

    return result;
  }

  /**
   * Cast result object to a string.
   */
  public String str()
  {
    String str = DOMHelper.getNodeData((DocumentFragment)m_obj);
    return (null == str) ? "" : str;
  }
  
  /**
   * Cast result object to a result tree fragment.
   */
  public DocumentFragment rtree()
  {
    return (DocumentFragment)m_obj;
  }
  
  /**
   * Cast result object to a nodelist. (special function).
   */
  public NodeList convertToNodeset()
  {
    return ((DocumentFragment)m_obj).getChildNodes();
  }  
  
  /**
   * Tell if two objects are functionally equal.
   */
  public boolean equals(XObject obj2)
    throws org.xml.sax.SAXException
  {
    if(XObject.CLASS_NODESET == obj2.getType())
    {
      // In order to handle the 'all' semantics of 
      // nodeset comparisons, we always call the 
      // nodeset function.
      return obj2.equals(this);
    }
    else if(XObject.CLASS_BOOLEAN == obj2.getType())
    {
      return bool() == obj2.bool();
    }
    else if(XObject.CLASS_NUMBER == obj2.getType())
    {
      return num() == obj2.num();
    }
    else if(XObject.CLASS_NODESET == obj2.getType())
    {
      return str().equals(obj2.str());
    }
    else if(XObject.CLASS_STRING == obj2.getType())
    {
      return str().equals(obj2.str());
    }
    else if(XObject.CLASS_RTREEFRAG == obj2.getType())
    {
      // Probably not so good.  Think about this.
      return str().equals(obj2.str());
    }
    else
    {
      return super.equals(obj2);
    }
  }

}
