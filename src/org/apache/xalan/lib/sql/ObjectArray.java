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
package org.apache.xalan.lib.sql;

import java.util.Vector;

/**
 * Provide a simple Array storage mechinsim where  native Arrays will be use as
 * the basic storage mechinism but the Arrays will be stored as blocks.
 * The size of the Array blocks is determine during object construction.
 * This is intended to be a simple storage mechinsim where the storage only
 * can grow. Array elements can not be removed, only added to.
 */
public class ObjectArray
{
  /**
   */
  private int m_minArraySize = 10;
  /**
   * The container of all the sub arrays
   */
  private Vector m_Arrays = new Vector(200);

  /**
   * An index that porvides the Vector entry for the current Array that is
   * being appended to.
   */
  private _ObjectArray m_currentArray;


  /**
   * The next offset in the current Array to append a new object
   */
  private int m_nextSlot;


  /**
   */
  public ObjectArray( )
  {
    //
    // Default constructor will work with a minimal fixed size
    //
    init(10);
  }

  /**
   * @param minArraySize The size of the Arrays stored in the Vector
   */
  public ObjectArray( final int minArraySize )
  {
    init(minArraySize);
  }

  /**
   * @param size
   *
   */
  private void init( int size )
  {
    m_minArraySize = size;
    m_currentArray = new _ObjectArray(m_minArraySize);
  }

  /**
   * @param idx Index of the Object in the Array
   *
   */
  public Object getAt( final int idx )
  {
    int arrayIndx = idx / m_minArraySize;
    int arrayOffset = idx - (arrayIndx * m_minArraySize);

    //
    // If the array has been off loaded to the Vector Storage them
    // grab it from there.
    if (arrayIndx < m_Arrays.size())
    {
      _ObjectArray a = (_ObjectArray)m_Arrays.elementAt(arrayIndx);
      return a.objects[arrayOffset];
    }
    else
    {
      // We must be in the current array, so pull it from there

      // %REVIEW% We may want to check to see if arrayIndx is only
      // one freater that the m_Arrays.size(); This code is safe but
      // will repete if the index is greater than the array size.
      return m_currentArray.objects[arrayOffset];
    }
  }

  /**
   * @param idx Index of the Object in the Array
   * @param obj , The value to set in the Array
   *
   */
  public void setAt( final int idx, final Object obj )
  {
    int arrayIndx = idx / m_minArraySize;
    int arrayOffset = idx - (arrayIndx * m_minArraySize);

    //
    // If the array has been off loaded to the Vector Storage them
    // grab it from there.
    if (arrayIndx < m_Arrays.size())
    {
      _ObjectArray a = (_ObjectArray)m_Arrays.elementAt(arrayIndx);
      a.objects[arrayOffset] = obj;
    }
    else
    {
      // We must be in the current array, so pull it from there

      // %REVIEW% We may want to check to see if arrayIndx is only
      // one freater that the m_Arrays.size(); This code is safe but
      // will repete if the index is greater than the array size.
      m_currentArray.objects[arrayOffset] = obj;
    }
  }



  /**
   * @param o Object to be appended to the Array
   *
   */
  public int append( Object o )
  {
    if (m_nextSlot >= m_minArraySize)
    {
      m_Arrays.addElement(m_currentArray);
      m_nextSlot = 0;
      m_currentArray = new _ObjectArray(m_minArraySize);
    }

    m_currentArray.objects[m_nextSlot] = o;

    int pos = (m_Arrays.size() * m_minArraySize) + m_nextSlot;

    m_nextSlot++;

    return pos;
  }


  /**
   */
  class _ObjectArray
  {
    /**
     */
    public Object[] objects;
    /**
     * @param size
     */
    public _ObjectArray( int size )
    {
      objects = new Object[size];
    }
  }

  /**
   * @param args
   *
   */
  public static void main( String[] args )
  {
    String[] word={
      "Zero","One","Two","Three","Four","Five",
      "Six","Seven","Eight","Nine","Ten",
      "Eleven","Twelve","Thirteen","Fourteen","Fifteen",
      "Sixteen","Seventeen","Eighteen","Nineteen","Twenty",
      "Twenty-One","Twenty-Two","Twenty-Three","Twenty-Four",
      "Twenty-Five","Twenty-Six","Twenty-Seven","Twenty-Eight",
      "Twenty-Nine","Thirty","Thirty-One","Thirty-Two",
      "Thirty-Three","Thirty-Four","Thirty-Five","Thirty-Six",
      "Thirty-Seven","Thirty-Eight","Thirty-Nine"};

    ObjectArray m_ObjectArray = new ObjectArray();
    // Add them in, using the default block size
    for (int x =0; x< word.length; x++)
    {
      System.out.print(" - " + m_ObjectArray.append(word[x]));
    }

    System.out.println("\n");
    // Now let's read them out sequentally
    for (int x =0; x< word.length; x++)
    {
      String s = (String) m_ObjectArray.getAt(x);
      System.out.println(s);
    }

    // Some Random Access
    System.out.println((String) m_ObjectArray.getAt(5));
    System.out.println((String) m_ObjectArray.getAt(10));
    System.out.println((String) m_ObjectArray.getAt(20));
    System.out.println((String) m_ObjectArray.getAt(2));
    System.out.println((String) m_ObjectArray.getAt(15));
    System.out.println((String) m_ObjectArray.getAt(30));
    System.out.println((String) m_ObjectArray.getAt(6));
    System.out.println((String) m_ObjectArray.getAt(8));

    // Out of bounds
    System.out.println((String) m_ObjectArray.getAt(40));

  }
}
