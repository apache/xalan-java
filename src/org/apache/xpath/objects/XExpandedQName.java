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

import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.XPathContext;
import org.apache.xml.dtm.DTMIterator;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;
import org.apache.xpath.NodeSetDTM;

/** XObject which represents an Expanded Qualified Name.
 * 
 * Note that at this writing, Expanded QNames have no lexical
 * representation; they may be compared for equality, or they may
 * have their values retreived via xf:get-*-from-QName(), but
 * no other XPath/XSLT 2.0 operations are valid.
 * 
 * @author keshlam
 * @since Aug 26, 2002
 */
public class XExpandedQName extends XObject
{
	QName m_qname;
	
	public XExpandedQName(QName qname)
	{
		m_qname=qname;
	}
	
	public XExpandedQName(String namespace,String localname)
	{
		m_qname=new QName(namespace,localname);
	}
	
  /**
   * Tell what kind of class this is.
   *
   * @return CLASS_UNKNOWN
   */
  public int getType()
  {
    return CLASS_QNAME;
  }
  
  /**
   * Given a request type, return the equivalent string.
   * For diagnostic purposes.
   *
   * @return type string "#EXPANDED_QNAME"
   */
  public String getTypeString()
  {
    return "#EXPANDED_QNAME";
  }

	public String getNamespace()
	{
		return m_qname.getNamespace();
	}
	
	public String getLocalName()
	{
		return m_qname.getLocalName();
	}

	/**
	 * @see org.apache.xpath.objects.XObject#object()
	 */
	public Object object()
	{
		return m_qname;
	}

  /**
   * Cast result object to an XString. Always issues an error.
   *
   * @return null
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XMLString xstr()
  {
  	try
  	{
	    error(XPATHErrorResources.ER_CANT_CONVERT_TO_TYPE,
    	      new Object[]{ getTypeString(), "XMLString" });
  	}
  	catch(Exception e)
  	{
  		// Necessary because XObject.xstr isn't normally expected
  		// to throw exceptions.
  		throw new org.apache.xml.utils.WrappedRuntimeException(e); 
  	}

    return null;
  }
  /**
   * Cast result object to a String. Always issues an error.
   *
   * @return null
   */
  public String str() 
  {
  	try
  	{
	    error(XPATHErrorResources.ER_CANT_CONVERT_TO_TYPE,
    	      new Object[]{ getTypeString(), "java.lang.String" });
  	}
  	catch(Exception e)
  	{
  		// Necessary because XObject.str isn't normally expected
  		// to throw exceptions.
  		throw new org.apache.xml.utils.WrappedRuntimeException(e); 
  	}

    return "";
  }

	/** For debugging purposes, NOT for expression evaluation)
	 * */
  public String toString()
  {
  	return m_qname.toString();
  }	
}
