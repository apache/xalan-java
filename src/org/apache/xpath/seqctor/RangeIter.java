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
package org.apache.xpath.seqctor;

import org.apache.xml.dtm.XType;
import org.apache.xpath.objects.XInteger;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XSequence;

/**
 * The responsibility of enclosing_type is to .
 * 
 * Created Jul 23, 2002
 * @author sboag
 */
public class RangeIter extends XObject implements XSequence
{
  private int m_from;
  private int m_to;
  private int m_pos;
  private XObject m_current;
  private Object m_obj;

  /**
   * Constructor for RangeIter.
   */
  public RangeIter(int from, int to)
  {
    super();
    m_to = to;
    m_from = from;
    m_pos = from-1;
  }

  /**
   * Constructor for RangeIter.
   * @param obj
   */
  public RangeIter(Object obj)
  {
    //super(obj);
    m_obj=obj;
  }
  
  public Object object()
  {
  	return m_obj;
  }

  /**
   * @see org.apache.xpath.objects.XSequence#getTypes()
   */
  public int getTypes()
  {
    return 0;
  }

  /**
   * @see org.apache.xpath.objects.XSequence#next()
   */
  public XObject next()
  {
    if (m_pos <= m_to)
    {
      m_pos++;
      if (m_pos <= m_to)
        return m_current = new XInteger(m_pos);
      else
        return m_current = null;
    }
    return m_current = null;
  }

  /**
   * @see org.apache.xpath.objects.XSequence#previous()
   */
  public XObject previous()
  {
    if (m_pos >= m_from)
    {
      m_pos--;
      if (m_pos >= m_from)
        return m_current = new XInteger(m_pos);
      else
        return m_current = null;
    }
    else
      return m_current = null;
  }

  /**
   * @see org.apache.xpath.objects.XSequence#getCurrent()
   */
  public XObject getCurrent()
  {
    return m_current;
  }

  /**
   * @see org.apache.xpath.objects.XSequence#isFresh()
   */
  public boolean isFresh()
  {
    return m_pos == m_from-1;
  }

  /**
   * @see org.apache.xpath.objects.XSequence#getTypeNS()
   */
  public String getTypeNS()
  {
    return XType.XMLSCHEMA_DATATYPE_NAMESPACE;
  }

  /**
   * @see org.apache.xpath.objects.XSequence#getTypeLocalName()
   */
  public String getTypeLocalName()
  {
    return XType.getLocalNameFromType(XType.INTEGER);
  }

  /**
   * @see org.apache.xpath.objects.XSequence#isSchemaType(String, String)
   */
  public boolean isSchemaType(String namespace, String localname)
  {
    int type = XType.getTypeID(namespace, localname);
    return type == XType.INTEGER;
  }

  /**
   * @see org.apache.xpath.objects.XSequence#isPureNodeSequence()
   */
  public boolean isPureNodeSequence()
  {
    return false;
  }

  /**
   * @see org.apache.xpath.objects.XSequence#setShouldCache(boolean)
   */
  public void setShouldCacheNodes(boolean b)
  {
  }

  /**
   * @see org.apache.xpath.objects.XSequence#getIsRandomAccess()
   */
  public boolean getIsRandomAccess()
  {
    return true;
  }

  /**
   * @see org.apache.xpath.objects.XSequence#isMutable()
   */
  public boolean isMutable()
  {
    return false;
  }

  /**
   * @see org.apache.xpath.objects.XSequence#getCurrentPos()
   */
  public int getCurrentPos()
  {
    return m_pos-m_from;
  }

  /**
   * @see org.apache.xpath.objects.XSequence#setCurrentPos(int)
   */
  public void setCurrentPos(int i)
  {
    m_pos = m_from+i;
  }

  /**
   * @see org.apache.xpath.objects.XSequence#getLength()
   */
  public int getLength()
  {
    return m_to-m_from;
  }

  /**
   * @see org.apache.xpath.objects.XSequence#isSingletonOrEmpty()
   */
  public boolean isSingletonOrEmpty()
  {
    return getLength() <= 1;
  }
  
  /**
   * @see java.lang.Object#clone()
   */
  public Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }


  /**
   * Returns the from.
   * @return int
   */
  public int getFrom()
  {
    return m_from;
  }

  /**
   * Returns the to.
   * @return int
   */
  public int getTo()
  {
    return m_to;
  }

  /**
   * Sets the current.
   * @param current The current to set
   */
  public void setCurrent(XObject current)
  {
    m_current = current;
  }

  /**
   * Sets the from.
   * @param from The from to set
   */
  public void setFrom(int from)
  {
    m_from = from;
  }

  /**
   * Sets the to.
   * @param to The to to set
   */
  public void setTo(int to)
  {
    m_to = to;
  }

  /**
   * @see org.apache.xpath.objects.XObject#isSequenceProper()
   */
  public boolean isSequenceProper()
  {
    return true;
  }

  /**
   * @see org.apache.xpath.objects.XObject#xseq()
   */
  public XSequence xseq()
  {
    return this;
  }

}
