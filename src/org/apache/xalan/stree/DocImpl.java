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
package org.apache.xalan.stree;

import org.apache.xml.utils.FastStringBuffer;
import org.apache.xpath.XPathContext;

/**
 * <meta name="usage" content="internal"/>
 * Contains extended functionality that Xalan requires that is
 * common in both the DocumentImpl and DocumentFragmentImpl
 * classes.  This leaves the DocumentImpl class free to simply implement
 * the Document interface plus items peculiar to it.
 */
public abstract class DocImpl extends Parent
{

//  /** Aid to assigning a unique ID to the tree. */
//  static int m_idCount = 0;
//  
//  /** The unique ID of this tree. */
//  int m_id;

  /** This holds all the characters used, copied from the 
   * characters events.  This allows us to not have to allocate 
   * a million little arrays.  */
  FastStringBuffer m_chars;
  
  /** Contains exception thrown from transformation thread, 
   * if one occured. */
  public Exception m_exceptionThrown = null;
  
  /**
   * For execution of whitespace matching.
   */
   XPathContext m_xpathContext = null;

  /**
   * Constructor DocImpl
   */
  public DocImpl()
  {
    super(null);
    // Just an initial guess at reasonable tuning parameters
    m_chars = new FastStringBuffer(13,13);
//    m_id = m_idCount++;
  }
  
  /**
   * Constructor DocImpl
   */
  public DocImpl(int charBufSize)
  {
    super(null);

    /*
     * (Note that this takes an initial text buffer size, in characters,
     * as its input parameter. FastStringBuffer's constructor expects the
     * number of bits (power of two) needed to span that size. We need to
     * convert.)
     */

    // Find highest bit
    int bitCheck=charBufSize, bitCount=0;
    while(bitCheck!=0)
      {
	++bitCount;
	bitCheck>>>=1;
      }
    //If any lower bits are set, bump bitCount up one 
    int mask=( 1<<(bitCount-1) -1 );
    if ((charBufSize & mask) != 0)
      ++bitCount;

    m_chars = new FastStringBuffer(bitCount);

//    m_id = m_idCount++;
  }


  /** A reference back to the source tree 
   * handler that is creating this tree.    */
  SourceTreeHandler m_sourceTreeHandler;

  /**
   * Get a reference back to the source tree 
   * handler that is creating this tree.  
   *
   * @return SourceTreeHandler reference, could 
   * be null (though maybe this should change.  -sb).
   */
  SourceTreeHandler getSourceTreeHandler()
  {
    return m_sourceTreeHandler;
  }

  /**
   * Set a reference back to the source tree 
   * handler that is creating this tree. 
   *
   * @param h Should be a non-null reference to 
   * the SourceTreeHandler that is creating this 
   * tree.
   */
  void setSourceTreeHandler(SourceTreeHandler h)
  {
    m_sourceTreeHandler = h;
  }

  /** This tells how many children are in the tree.  */
  int m_docOrderCount = 1;

  /**
   * Increment the document order count.  Needs to be called
   * when a child is added.
   */
  protected void incrementDocOrderCount()
  {
    m_docOrderCount++;
  }

  /**
   * Get the number of nodes in the tree.  Needs to be called
   * when a child is added.
   *
   * @return The number of children in the tree.
   */
  protected int getDocOrderCount()
  {
    return m_docOrderCount;
  }

  /** If this is true, the transformation is working off of 
   * a secondary thread from the incoming SAX events, and 
   * the secondary thread may have to wait for nodes be produced.  */
  boolean m_useMultiThreading = false;

  /**
   * Set whether or not the tree being built should handle
   * transformation while the parse is still going on.
   *
   * @param b true if the transformation is working off of a 
   * secondary thread, false otherwise.
   */
  public void setUseMultiThreading(boolean b)
  {
    m_useMultiThreading = b;
  }

  /**
   * Tell whether or not the tree being built should handle
   * transformation while the parse is still going on.
   *
   * @return true if the transformation is working off of a 
   * secondary thread, false otherwise.
   */
  public boolean getUseMultiThreading()
  {
    return m_useMultiThreading;
  }
}
