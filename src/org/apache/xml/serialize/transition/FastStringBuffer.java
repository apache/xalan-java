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
 * 4. The names "Xerces" and "Apache Software Foundation" must
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
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xml.serialize.transition;

/**
 * Bare-bones, unsafe, fast string buffer.
 */
public class FastStringBuffer
{
  public int m_blocksize;
  public char m_map[];  // don't hold on to a reference!
  public int m_firstFree = 0;
  public int m_mapSize;
 
  /**
   * Construct a IntVector, using the given block size.
   */
  public FastStringBuffer(int blocksize)
  {
    m_blocksize = blocksize;
    m_mapSize = blocksize;
    m_map = new char[blocksize]; 
  }
  
  /**
   * Get the length of the list.
   */
  public final int size()
  {
    return m_firstFree;
  }
  
  public final int length()
  {
    return m_firstFree;
  }
  
  public final void reset()
  {
    m_firstFree = 0;
  }
  
  public final void setLength(int l)
  {
    m_firstFree = l;
  }

    
  private final void ensureSize(int newSize)
  {
    if((m_firstFree+newSize) >= m_mapSize)
    {
      if(m_blocksize > newSize)
        m_mapSize+=m_blocksize;
      else
        m_mapSize+=(newSize+m_blocksize);
      char newMap[] = new char[m_mapSize];
      System.arraycopy(m_map, 0, newMap, 0, m_firstFree+1);
      m_map = newMap;
    }
  }

  /**
   * Append a int onto the vector.
   */
  public final void append(char value)
  {
    int ff = m_firstFree;
    if((ff+1) >= m_mapSize)
    {
      m_mapSize+=m_blocksize+1;
      char newMap[] = new char[m_mapSize];
      System.arraycopy(m_map, 0, newMap, 0, ff);
      m_map = newMap;
    }
    m_map[ff] = value;
    m_firstFree = ff+1;
  }
  

  /**
   * Append a int onto the vector.
   */
  public final void append(String value)
  {
    int len = value.length();
    ensureSize(len);
    value.getChars(0, len, m_map, m_firstFree);
    m_firstFree+=len;
  }
  
  /**
   * Append a int onto the vector.
   */
  public final void append(StringBuffer value)
  {
    int len = value.length();
    ensureSize(len);
    value.getChars(0, len, m_map, m_firstFree);
    m_firstFree+=len;
  }
  
  public final void append( char[] chars, int start, int length )
  {
    ensureSize(length);
    System.arraycopy(chars, start, m_map, m_firstFree, length);
    m_firstFree+=length;
  }
  
  public final void append( FastStringBuffer value )
  {
    int length = value.m_firstFree;
    ensureSize(length);
    System.arraycopy(value.m_map, 0, m_map, m_firstFree, length);
    m_firstFree+=length;
  }
}
