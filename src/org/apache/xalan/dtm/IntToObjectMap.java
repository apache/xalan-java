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
package org.apache.xalan.dtm;

/**
 * <meta name="usage" content="internal"/>
 * Map simple ints to objects.
 */
class IntToObjectMap
{
  static final int BLOCKSIZE = 64;
  int m_map[] = new int[BLOCKSIZE];
  Object m_objects[] = new Object[BLOCKSIZE];
  int m_firstFree = 0;
  int m_mapSize = BLOCKSIZE;
  
  IntToObjectMap()
  {
  }
  
  void put(int key, Object value)
  {
    if(m_firstFree >= m_mapSize)
    {
      m_mapSize+=BLOCKSIZE;
      int newMap[] = new int[m_mapSize];
      System.arraycopy(m_map, 0, newMap, 0, m_firstFree);
      m_map = newMap;
      Object newObjects[] = new Object[m_mapSize];
      System.arraycopy(m_objects, 0, newObjects, 0, m_firstFree);
      m_objects = newObjects;
    }
    // For now, just do a simple append.  A sorted insert only 
    // makes sense if we're doing an binary search or some such.
    m_map[m_firstFree] = key;
    m_objects[m_firstFree] = value;
    m_firstFree++;
  }
  
  Object get(int key)
  {
    for(int i = 0; i < m_firstFree; i++)
    {
      if(m_map[i] == key)
      {
        return m_objects[i];
      }
    }
    return null;
  }
}
