/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights 
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
package org.apache.xalan.utils;

import java.util.Vector;

public class Heap extends Vector
{
  private HeapObject m_a[] = new HeapObject[50];
  private int m_n;
  
  public Heap()
  {
  }
  
  public void insert(HeapObject obj)
  {
    m_n++;
    m_a[m_n] = obj;
    upheap(m_n);
  }
    
  private void upheap(int k)
  {
    HeapObject v = m_a[k]; 
    m_a[0] = m_sentinel;
    while (m_a[k/2].getHeapValue() < v.getHeapValue()) 
    {
      m_a[k] = m_a[k/2];
      k = k/2;
    }
    m_a[k] = v;
  }
  
  public HeapObject remove()
  {
    HeapObject v = m_a[1];
    m_a[1] = m_a[m_n--];
    downheap(1);
    return v;
  }
  
  private void downheap(int k)
  {
    HeapObject v = m_a[k];
    while (k <= m_n/2) 
    {
      int j = k+k;
      if (j < m_n && m_a[j].getHeapValue() < m_a[j+1].getHeapValue()) 
        j++;
      if (v.getHeapValue() >= m_a[j].getHeapValue()) 
        break;
      m_a[k] = m_a[j]; k = j;
    }
    m_a[k] = v;
  }
   
  HeapObject replace(HeapObject v)
  {
    m_a[0] = v;
    downheap(0);
    return m_a[0];
  }
  
  void change()
  {
  }
  
  void delete()
  {
  }
  
  class Sentinel implements HeapObject
  {
    public int getHeapValue()
    {
      return Short.MAX_VALUE;
    }
  }
  
  final Sentinel m_sentinel = new Sentinel();
}
