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
package org.apache.xml.utils;

/**
 * Bare-bones, unsafe, fast string buffer. No thread-safety, no
 * parameter range checking, exposed fields.
 */
public class FastStringBuffer
{

  /** Field m_blocksize establishes the allocation granularity -- the
   * initial size of m_map[] and the minimum increment by which it grows
   * when necessary.  */
  public int m_blocksize;

  /** Field m_map[] is a character array holding the string buffer's
   * contents. Note that this array will be reallocated when necessary
   * in order to allow the buffer to grow, so references to this
   * object may become (indedetectably) invalid  after any edit is
   * made to the FastStringBuffer. DO NOT retain such references!
   * (Note that this imposes a multithreading hazard; its the user's
   * responsibility to manage access to FastStringBuffer to prevent
   * the problem from arising.) */
  public char m_map[];

  /** Field m_firstFree is an index into m_map[], pointing to the first
   * character in the array which is not part of the FastStringBuffer's
   * current content. Since m_map[] is zero-based, m_firstFree is also 
   * equal to the length of that content. */
  public int m_firstFree = 0;

  /** Field m_mapSize is a cached copy of m_map.length -- or,
   * transiently, the new length that m_map will grow to. */
  public int m_mapSize;

  /**
   * Construct a IntVector, using the default block size.
   */
  public FastStringBuffer()
  {

    m_blocksize = 1024;
    m_mapSize = 1024;
    m_map = new char[1024];
  }

  /**
   * Construct a IntVector, using the given block size.
   *
   * @param blocksize Desired value for m_blocksize, establishes both
   * the initial storage allocation and the minimum growth increment.
   */
  public FastStringBuffer(int blocksize)
  {

    m_blocksize = blocksize;
    m_mapSize = blocksize;
    m_map = new char[blocksize];
  }

  /**
   * Get the length of the list. Synonym for length().
   *
   * @return the number of characters in the FastStringBuffer's content.
   */
  public final int size()
  {
    return m_firstFree;
  }

  /**
   * Get the length of the list. Synonym for size().
   *
   * @return the number of characters in the FastStringBuffer's content.
   */
  public final int length()
  {
    return m_firstFree;
  }

  /**
   * Discard the content of the FastStringBuffer. Does _not_ release
   * any of the storage space.
   */
  public final void reset()
  {
    m_firstFree = 0;
  }

  /**
   * Directly set how much of the FastStringBuffer's storage is to be
   * considered part of its content. This is a fast but hazardous
   * operation. It is not protected against negative values, or values
   * greater than the amount of storage currently available... and even
   * if additional storage does exist, its contents are unpredictable.
   * The only safe use for setLength() is to truncate the FastStringBuffer
   * to a shorter string.
   *
   * @param l New length. If l<0 or l>=m_mapSize, this operation will
   * not report an error but future operations will almost certainly fail.
   */
  public final void setLength(int l)
  {
    m_firstFree = l;
  }

  /**
   * @return the contents of the FastStringBuffer as a standard Java string
   */
  public final String toString()
  {
    return new String(m_map, 0, m_firstFree);
  }

  /**
   * Ensure that the FastStringBuffer has at least the specified amount
   * of unused space in m_map[]. If necessary, this operation will
   * enlarge the buffer, overallocating by blocksize so we're less
   * likely to immediately need to grow again.
   * <p>
   * NOTE THAT after calling ensureFreeSpace(), previously obtained
   * references to m_map[] may no longer be valid.
   *
   * @param newSize the required amount of "free space".
   */
  private final void ensureFreeSpace(int newSize)
  {

    if ((m_firstFree + newSize) >= m_mapSize)
    {
      m_mapSize += (newSize + m_blocksize);

      char newMap[] = new char[m_mapSize];

      System.arraycopy(m_map, 0, newMap, 0, m_firstFree + 1);

      m_map = newMap;
    }
  }

  /**
   * Append a single character onto the FastStringBuffer, growing the 
   * storage if necessary.
   * <p>
   * NOTE THAT after calling append(), previously obtained
   * references to m_map[] may no longer be valid.
   *
   * @param value character to be appended.
   */
  public final void append(char value)
  {

    ensureFreeSpace(1);

    m_map[m_firstFree] = value;

    m_firstFree++;
  }

  /**
   * Append the contents of a String onto the FastStringBuffer, 
   * growing the storage if necessary.
   * <p>
   * NOTE THAT after calling append(), previously obtained
   * references to m_map[] may no longer be valid.
   *
   * @param value String whose contents are to be appended.
   */
  public final void append(String value)
  {

    int len = value.length();

    ensureFreeSpace(len);
    value.getChars(0, len, m_map, m_firstFree);

    m_firstFree += len;
  }

  /**
   * Append the contents of a StringBuffer onto the FastStringBuffer,
   * growing the storage if necessary.
   * <p>
   * NOTE THAT after calling append(), previously obtained
   * references to m_map[] may no longer be valid.
   *
   * @param value StringBuffer whose contents are to be appended.
   */
  public final void append(StringBuffer value)
  {

    int len = value.length();

    ensureFreeSpace(len);
    value.getChars(0, len, m_map, m_firstFree);

    m_firstFree += len;
  }

  /**
   * Append part of the contents of a Character Array onto the 
   * FastStringBuffer,  growing the storage if necessary.
   * <p>
   * NOTE THAT after calling append(), previously obtained
   * references to m_map[] may no longer be valid.
   *
   * @param chars character array from which data is to be copied
   * @param start offset in chars of first character to be copied,
   * zero-based.
   * @param length number of characters to be copied
   */
  public final void append(char[] chars, int start, int length)
  {

    ensureFreeSpace(length);
    System.arraycopy(chars, start, m_map, m_firstFree, length);

    m_firstFree += length;
  }

  /**
   * Append the contents of another FastStringBuffer onto 
   * this FastStringBuffer, growing the storage if necessary.
   * <p>
   * NOTE THAT after calling append(), previously obtained
   * references to m_map[] may no longer be valid.
   *
   * @param value FastStringBuffer whose contents are
   * to be appended.
   */
  public final void append(FastStringBuffer value)
  {

    int length = value.m_firstFree;

    ensureFreeSpace(length);
    System.arraycopy(value.m_map, 0, m_map, m_firstFree, length);

    m_firstFree += length;
  }
}
