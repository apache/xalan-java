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

import java.util.Vector;

import org.apache.xpath.XPathContext;

/**
 * XSequenceCachedBase is an abstract superclass for classes that
 * want to use XSequenceImpl as a base for caching but are otherwise
 * incremental.
 * 
 * Created Jul 20, 2002
 * @author sboag
 */
public abstract class XSequenceCachedBase extends XSequenceImpl implements XSequence
{
  protected boolean m_cached = false;
  protected XObject m_current;
  protected XPathContext m_xctxt; 
  protected boolean m_foundLast = false;
  
  /**
   * Constructor for XSequenceCachedBase.
   */
  public XSequenceCachedBase(XPathContext xctxt)
  {
    m_xctxt = xctxt;
  }


  /**
   * Return the next object in the iteration from a next() method.
   * @param xobj the next Object in the itterator, or null.
   * @return XObject The same reference that was passed in.
   */
  public XObject next()
  {
  	// Start by asking the cache
    XObject next = super.next();
    
    // If not in cache, and not done iterating, request
    // new value from getNext() (provided by subclass)
    if(null == next && !m_foundLast)
    {
      next = getNext();
      if(null != next)
      {
      	// Add it to the cache.
        m_pos++;
        if(m_cached)
        {
          // Note: The first case arises only if the cache
          // actually contained a null. May happen if a
          // subclass knows the expected count and preallocates
          // the cache; unlikely otherwise.
          if(m_pos < getValues().size()-1)
            insertItemAt(next, m_pos);
          else
            concat(next);
        }
      }
      else
      {
        m_foundLast = true; // Off the end...
        m_pos++; // go off the end.
      }
    }
    m_current = next;
    return next;
  }

  /**
   * Return the previous object in the iteration from a previous() method.
   * @param xobj the next Object in the iterator, or null.
   * @return XObject The same reference that was passed in.
   */
  public XObject previous()
  {
  	// Start by asking the cache
    XObject prev = super.previous();
    
    // If not in cache, and not done iterating, request
    // new value from getNext() (provided by subclass)
    if(null == prev)
    {
      m_pos--;
      prev = getPrevious();
      if(null != prev)
      {
      	// Add it to the cache.
        // Note: This case arises only if the cache
        // actually contained a null. May happen if a
        // subclass knows the expected count and preallocates
        // the cache; unlikely otherwise.
        if(m_cached)
          super.insertItemAt(prev, m_pos);
      }
    }
    m_current = prev;
    return prev;
  }
  
  /**
   * This method must be implemented by the derived class to return the 
   * next item in the iteration, and is called by the next() method.  The 
   * item returned will be inserted into the cache if the cache property 
   * is true.
   * @return XObject The next item, or null.
   */
  protected abstract XObject getNext();
  
  /**
   * This method must be implemented by the derived class to return the 
   * previous item in the iteration, and is called by the next() method.  The 
   * item returned will be inserted into the cache if the cache property 
   * is true.
   * @return XObject The previous item, or null.
   * @throws RuntimeException if this iterator doesn't support non-cached 
   * previous operations.
   */
  protected abstract XObject getPrevious();


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
    return (null == m_current) && super.isFresh();
  }
  
  /**
   * @see org.apache.xpath.objects.XSequence#getTypeNS()
   */
  public String getTypeNS()
  {
    assertion((null != m_current), "Current node can not be null for getTypeNS()!");
    return m_current.xseq().getTypeNS();
  }

  /**
   * @see org.apache.xpath.objects.XSequence#getTypeLocalName()
   */
  public String getTypeLocalName()
  {
    assertion((null != m_current), "Current node can not be null for getTypeLocalName()!");
    return m_current.xseq().getTypeLocalName();
  }

  /**
   * @see org.apache.xpath.objects.XSequence#isSchemaType(String, String)
   */
  public boolean isSchemaType(String namespace, String localname)
  {
    assertion((null != m_current), "Current node can not be null for isSchemaType()!");
    return m_current.xseq().isSchemaType(namespace, localname);
  }

  /**
   * @see org.apache.xpath.objects.XSequence#isPureNodeSequence()
   */
  public boolean isPureNodeSequence()
  {
    // Not totally sure what to do about this yet.
    return false;
  }

  /**
   * @see org.apache.xpath.objects.XSequence#setShouldCache(boolean)
   */
  public void setShouldCache(boolean b)
  {
    if(!isFresh())
     throw new RuntimeException(this.getClass().getName()+" cache can not be changed after it has started!");
    if(b)
    {
      if(null == getValues())
        setValues(new Vector());
    }
    m_cached = b;
  }

  /**
   * @see org.apache.xpath.objects.XSequence#getIsRandomAccess()
   */
  public boolean getIsRandomAccess()
  {
    return m_cached;
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
    return m_pos;
  }

  /**
   * @see org.apache.xpath.objects.XSequence#setCurrentPos(int)
   */
  public void setCurrentPos(int i)
  {
    if (!m_cached)
      throw new RuntimeException("This "+this.getClass().getName()+" is not set for random access!");

    if (i < 0)
    {
      m_current = null;
      m_pos = -1;
    }
    else
      if (i > m_pos)
      {
        while (i > m_pos)
        {
          XObject xobj = next(); 	// Sets m_current implicitly
          if (null == xobj)
            break;
        }
      }
      else
      {
        m_pos = i;
        m_current = (XObject) getValues().elementAt(i);
      }
  }

  /**
   * @see org.apache.xpath.objects.XSequence#getLength()
   */
  public int getLength()
  {
    if(isFresh())
      setShouldCache(true);
    else if(!getIsRandomAccess())
      throw new RuntimeException("This "+this.getClass().getName()+" is not set for random access!");
    
    if(m_foundLast)
      return getValues().size();
      
    int origPos = m_pos;
    while(next() != null);
    int len = m_pos;
    m_pos = origPos;
    return len;
  }

  /**
   * @see org.apache.xpath.objects.XSequence#isSingletonOrEmpty()
   */
  public boolean isSingletonOrEmpty()
  {
    return false;  // hard to tell at this point.
  }



  /**
   * @return XSequence object.
   * @see java.lang.Object#clone()
   */
  public Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }

  /**
   * @see org.apache.xpath.objects.XObject#reset()
   */
  public void reset()
  {
    super.reset();
    m_current = null;
  }

}
