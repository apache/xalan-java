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

import org.apache.xml.dtm.XType;

/**
 * The responsibility of enclosing_type is to .
 * 
 * Created Jul 15, 2002
 * @author sboag
 */
class XSequenceEmpty extends XObject implements XSequence, Cloneable
{
  /**
   * Calling this method doesn't do anything.
   * @see org.apache.xml.dtm.XSequence#allowDetachToRelease(boolean)
   */
  public void allowDetachToRelease(boolean allowRelease)
  {
  }

  /**
   * Calling this method doesn't do anything.
   * @see org.apache.xml.dtm.XSequence#detach()
   */
  public void detach()
  {
  }

  /**
   * Always return null.
   * @see org.apache.xml.dtm.XSequence#getCurrent()
   */
  public XObject getCurrent()
  {
    return null;
  }
  
  public Object object()
  {
  	return null;
  }

  /**
   * Always returns -1.
   * @see org.apache.xml.dtm.XSequence#getCurrentPos()
   */
  public int getCurrentPos()
  {
    return 0;
  }

  /**
   * Always returns 0.
   * @see org.apache.xml.dtm.XSequence#getLength()
   */
  public int getLength()
  {
    return 0;
  }

  /**
   * Always throws IndexOutOfBoundsException.
   * @see org.apache.xml.dtm.XSequence#getType()
   */
  public int getType()
  {
    throw new IndexOutOfBoundsException("No current item!");
  }

  /** @return true if the sequence is known to contain only NODEs.
   * %REVIEW% I can argue for either calling this an empty sequence
   * of nodes, or a sequence which does not contain any nodes
   * (or we could throw an exception, though I don't think that's
   * useful). Which response is most useful?
   * */
  public boolean isPureNodeSequence()
  {
  	return false;	
  }

  /**
   * Always throws IndexOutOfBoundsException.
   * @see org.apache.xml.dtm.XSequence#getTypeLocalName()
   */
  public String getTypeLocalName()
  {
    throw new IndexOutOfBoundsException("No current item!");
  }

  /**
   * Always throws IndexOutOfBoundsException.
   * @see org.apache.xml.dtm.XSequence#getTypeNS()
   */
  public String getTypeNS()
  {
    throw new IndexOutOfBoundsException("No current item!");
  }

  /**
   * Always return XType.EMPTYSEQ.
   * @see org.apache.xml.dtm.XSequence#getTypes()
   */
  public int getTypes()
  {
    return XType.EMPTYSEQ;
  }

  /**
   * Always return true.
   * @see org.apache.xml.dtm.XSequence#isFresh()
   */
  public boolean isFresh()
  {
    return true;
  }

  /**
   * Always return false.
   * @see org.apache.xml.dtm.XSequence#isMutable()
   */
  public boolean isMutable()
  {
    return false;
  }

  /**
   * Always throw IndexOutOfBoundsException.
   * @see org.apache.xml.dtm.XSequence#isSchemaType(String, String)
   */
  public boolean isSchemaType(String namespace, String localname)
  {
    throw new IndexOutOfBoundsException("No current item!");
  }

  /**
   * Always return true;
   * @see org.apache.xml.dtm.XSequence#isSingleton()
   */
  public boolean isSingletonOrEmpty()
  {
    return true;
  }

  /**
   * Always return null.
   * @see org.apache.xml.dtm.XSequence#next()
   */
  public XObject next()
  {
    return null;
  }

  /**
   * Always return null.
   * @see org.apache.xml.dtm.XSequence#previous()
   */
  public XObject previous()
  {
    return null;
  }

  /**
   * @see org.apache.xml.dtm.XSequence#setCurrentPos(int)
   */
  public void setCurrentPos(int i)
  {
    if(0 != i)
      return;
    else
      throw new IndexOutOfBoundsException("No item at that index!");
  }

  /**
   * Calling this method doesn't do anything.
   * @see org.apache.xml.dtm.XSequence#setShouldCache(boolean)
   */
  public void setShouldCacheNodes(boolean b)
  {
  }

  /**
   * %REVIEW% Format not yet firmly settled. Primarily for debugging purposes!
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    return super.toString();
  }

  /**
   * @see java.lang.Object#clone()
   */
  public Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }

  /**
   * Always return true.
   * @see org.apache.xml.dtm.XSequence#getIsRandomAccess()
   */
  public boolean getIsRandomAccess()
  {
    return true; // as far as it goes.
  }

  /**
   * Calling this method doesn't do anything.
   * @see org.apache.xml.dtm.XSequence#reset()
   */
  public void reset()
  {
    // do nothing.
  }

}
