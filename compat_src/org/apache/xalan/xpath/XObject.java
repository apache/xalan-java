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
import java.io.Serializable;
import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xalan.res.XSLMessages;


/**
 * <meta name="usage" content="general"/>
 * This class represents an XPath object, and is capable of 
 * converting the object to various types, such as a string.
 * This class acts as the base class to other XPath type objects, 
 * such as XString, and provides polymorphic casting capabilities.
 * @deprecated This compatibility layer will be removed in later releases. 
 */
public class XObject extends Object implements Serializable    
{
  org.apache.xpath.objects.XObject m_xObject;
  
  /**
   * Create an XObject.
   */
  public XObject()
  {
    m_xObject = new org.apache.xpath.objects.XObject() ;
  }

  /**
   * Create an XObject.
   */
  public XObject(Object obj)
  {
    m_xObject = new org.apache.xpath.objects.XObject(obj) ;
  }
  
    /**
   * Tell what kind of class this is.
   */
  public int getType()
  {
    return m_xObject.getType();
  }

  /**
   * Given a request type, return the equivalent string. 
   * For diagnostic purposes.
   */
  protected String getTypeString() // PR:DMAN4MBJ4D Submitted by:<garyp@firstech.com> change to protected
  {
    return "#UNKNOWN";
  }
  
  /**
   * Cast result object to a number.
   */
  public double num()
    throws org.xml.sax.SAXException, javax.xml.transform.TransformerException
  {
	  return m_xObject.num();
  }

  /**
   * Cast result object to a boolean.
   */
  public boolean bool()
    throws org.xml.sax.SAXException, javax.xml.transform.TransformerException
  {
    return m_xObject.bool();
  }

  /**
   * Cast result object to a string.
   */
  public String str()
  {
    return m_xObject.str();
  }
  
  public String toString()
  {
    return m_xObject.toString();
  }
  
  /**
   * Cast result object to a result tree fragment.
   */
  public DocumentFragment rtree(XPathSupport support)
  {
    org.apache.xpath.XPathContext context = (org.apache.xpath.XPathContext)support;
    return m_xObject.rtree(context);
    //return  (DocumentFragment)context.getDTMManager().getDTM(result).getNode(result);         
  }
  
  /**
   * For functions to override.
   */
  public DocumentFragment rtree()
  {
    return null;
  }
  
  /**
   * Return a java object that's closes to the represenation 
   * that should be handed to an extension.
   */
  public Object object()
  {
    return m_xObject.object();
  }

  /**
   * Cast result object to a nodelist.
   */
  public NodeList nodeset()
    throws org.xml.sax.SAXException, javax.xml.transform.TransformerException
  {
    error(XPATHErrorResources.ER_CANT_CONVERT_TO_NODELIST, new Object[] {getTypeString()}); //"Can not convert "+getTypeString()+" to a NodeList!");
    return null;
  }  
  
  /**
   * Cast result object to a nodelist.
   */
  public NodeList mutableNodeset()
    throws org.xml.sax.SAXException, javax.xml.transform.TransformerException
  {
    return new org.apache.xml.dtm.ref.DTMNodeList(m_xObject.mutableNodeset());
  }  
 
  /**
   * Cast object to type t.
   */
  public Object castToType(int t, XPathSupport support)
    throws org.xml.sax.SAXException, javax.xml.transform.TransformerException
  {   
    return m_xObject.castToType(t, (org.apache.xpath.XPathContext) support);
  }

  /**
   * Tell if one object is less than the other.
   */
  public boolean lessThan(XObject obj2)
    throws org.xml.sax.SAXException, javax.xml.transform.TransformerException
  {
   
    return m_xObject.lessThan(obj2.m_xObject);
  }

  /**
   * Tell if one object is less than or equal to the other.
   */
  public boolean lessThanOrEqual(XObject obj2)
    throws org.xml.sax.SAXException, javax.xml.transform.TransformerException
  {
    return m_xObject.lessThanOrEqual(obj2.m_xObject);
  }

  /**
   * Tell if one object is less than the other.
   */
  public boolean greaterThan(XObject obj2)
    throws org.xml.sax.SAXException, javax.xml.transform.TransformerException
  {
    return m_xObject.greaterThan(obj2.m_xObject);
  }

  /**
   * Tell if one object is less than the other.
   */
  public boolean greaterThanOrEqual(XObject obj2)
    throws org.xml.sax.SAXException, javax.xml.transform.TransformerException
  {
    return m_xObject.greaterThanOrEqual(obj2.m_xObject);
  }

  /**
   * Tell if two objects are functionally equal.
   */
  public boolean equals(XObject obj2)
    throws org.xml.sax.SAXException
  {
    return m_xObject.equals(obj2.m_xObject);
  }
  
  /**
   * Tell if two objects are functionally not equal.
   */
  public boolean notEquals(XObject obj2)
    throws org.xml.sax.SAXException, javax.xml.transform.TransformerException
  {
    return m_xObject.notEquals(obj2.m_xObject);
  }

  /**
   * Tell the user of an error, and probably throw an 
   * exception.
   */
  protected void error(int msg)
    throws org.xml.sax.SAXException, javax.xml.transform.TransformerException
  {
	  error (msg, null);
  }	   

  /**
   * Tell the user of an error, and probably throw an 
   * exception.
   */
  protected void error(int msg, Object[] args)
    throws org.xml.sax.SAXException, javax.xml.transform.TransformerException
  {
    String fmsg = XSLMessages.createXPATHMessage(msg, args);

    
    throw new org.xml.sax.SAXException(fmsg);
  }

}
