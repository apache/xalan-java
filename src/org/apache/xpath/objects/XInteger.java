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
package org.apache.xpath.objects;

import org.apache.xml.dtm.XType;
import org.apache.xpath.XPathContext;
import org.apache.xpath.parser.Token;

public class XInteger extends XNumber
{
  int m_val;
  //Integer m_intobj; // Cache; current guess is we don't need it

  /**
   * Constructor for XInteger
   * 
   * This one's actually being used, unlike most XObject empty ctors,
   * because it may be created and _then_ set (by processToken)
   * during stylesheet parsing.
   */
  public XInteger()
  {
  }

  /**
   * Constructor for XInteger
   */
  public XInteger(int num)
  {
    m_val = num;
  }
  
  /**
   * Constructor for XInteger
   */
  public XInteger(Integer num)
  {
  	//m_intobj=num;
    m_val = num.intValue();
  }
  
  /**
   * Cast result object to a number.
   *
   * @return the value of the XNumber object
   */
  public double num()
  {
    return (double)m_val;
  }
  
  /**
   * Evaluate expression to a number.
   *
   * @return 0.0
   *
   * @throws javax.xml.transform.TransformerException
   */
  public double num(XPathContext xctxt) 
    throws javax.xml.transform.TransformerException
  {
    return (double)m_val;
  }
  
  /**
   * Get result object as a integer.
   *
   * @return At this level, the num() value cast to an integer.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public int integer() throws javax.xml.transform.TransformerException
  {

    return m_val;
  }


  /**
   * Cast result object to a boolean.
   *
   * @return false if the value is NaN or equal to 0.0
   */
  public boolean bool()
  {
    return (m_val == 0) ? false : true;
  }


  public void processToken(Token t) 
  { 
  	String strNum = t.image;
  	try
    {
      m_val = Integer.parseInt(strNum);
      //m_intobj=null; // Reset/defer
    }
    catch (java.lang.NumberFormatException e)
    {
      // TBD: Use error listener. -sb
      throw e;
    }
  }
  
  /**
   * Return the sequence representing this object.
   * @return XSequence
   */
  public XSequence xseq()
  {
    return new XSequenceSingleton(this);
  }


  public Object object()
  {
  	//if(m_intobj==null)
  	//	m_intobj=new Integer(m_val);
  	//return m_intobj;
  	return new Integer(m_val);
  }
}

