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

/**
 * The responsibility of XSequence is to provide an interface to XPath 2.0
 * sequences.
 * 
 * Created Jul 16, 2002
 * @author sboag
 * @author jkesselman
 */
public interface XSequence extends Cloneable
{  
  /** MANAFEST CONSTANT: An empty XSequence, which can be returned when no
   * typed value exists.
   * */ 
  public static final XSequenceEmpty EMPTY = new XSequenceEmpty();
  
  //========= Type methods on the sequence as a whole ==========

  /**
   * If this iterator holds homogenous types, return that type ID.
   * 
   * @return The homogenous type, or NOTHOMOGENOUS, or EMPTYSEQ.
   */
  public int getTypes();
  
  
  //========= Itteration ==========

  /**
   * Returns the next object in the set and advances the position of the
   * iterator in the set.
   * 
   * @return The next item in the set being iterated over, or
   *  <code>null</code> if there are no more members in that set.
   */
  public XObject next();

  /**
   * Returns the previous object in the set and moves the position of the
   * <code>XSequence</code> backwards in the set.
   * 
   * @return The previous item in the set being iterated over,
   *   or <code>null</code> if there are no more members in that set.
   */
  public XObject previous();

  /**
   * Detaches the <code>XSequence</code> from the set which it iterated
   * over, releasing any computational resources and placing the iterator
   * in the INVALID state. After <code>detach</code> has been invoked,
   * calls to <code>next</code> or <code>previous</code> will
   * raise a runtime exception.
   */
  public void detach();
  
  /**
   * Specify if it's OK for detach to release the iterator for reuse.
   * 
   * @param allowRelease true if it is OK for detach to release this iterator 
   * for pooling.
   */
  public void allowDetachToRelease(boolean allowRelease);

  /**
   * Get the current item in the iterator.
   *
   * @return The item or null if there is no current item.
   */
  public XObject getCurrent();

  /**
   * Tells if this iterator is "fresh", in other words, if
   * the first next() that is called will return the
   * first item in the set.
   *
   * @return true if the iteration of this list has not yet begun.
   */
  public boolean isFresh();
  
  /** 
   * Retrieve the current item's datatype namespace URI.
   * 
   * @return the namespace for the current value's type
   * @throws exception if there is no current item.
   */
  public String getTypeNS();

  /** 
   * Retrieve current item's datatype namespace URI.
   * 
   * @return the localname of the current value's type
   * @throws exception if there is no current item.
   */
  public String getTypeLocalName();

  /** 
   * Ask whether the current item's datatype equals or is derived from a specified
   * schema NSURI/localname pair.
   * 
   * @return true if the type is an instance of this schema datatype,
   * false if it isn't.
   * @throws exception if there is no current item.
   */
  public boolean isSchemaType(String namespace, String localname);
  
  /**
   * Retrieve the built-in atomic type of the current item
   * 
   * @return NODE, ANYTYPE, etc. as defined in XType
   * @throws exception if there is no current item.
   */
  public int getType();
  
  /** @return true if the sequence is known to contain only NODEs.
   * (%REVIEW%: If false, that may not mean it doesn't, just that
   * we don't know this a priori and can't optimize.)
   * */
  public boolean isPureNodeSequence();
  
  /**
   * Reset the iterator to the beginning of the itteration.  After this 
   * is called, isFresh will return true, and there will be no current 
   * node.
   */
  public void reset();


  //========= Random Access ==========

  /**
   * If setShouldCache(true) is called, then items will
   * be cached, enabling random access, and giving the ability to do 
   * sorts and the like.  They are not cached by default.
   *
   * %REVIEW% Shouldn't the other random-access methods throw an exception
   * if they're called with this flag set false?
   * 
   * %REVIEW% Is this method still needed, given that we *removed*
   * most of the random-access methods (except setCurrentPos)?
   *
   * @param b true if the items should be cached.
   * @throws exception if XSequence can't cache.
   */
  public void setShouldCacheNodes(boolean b);
  
  /**
   * Tell if the random access methods (i.e. those methods 
   * that take an index) can be used.  This is the normally the  
   * same value set with setShouldCache(boolean b).
   * 
   * %REVIEW% Is this method still needed, given that we *removed*
   * most of the random-access methods (except setCurrentPos)?
   *
   * @return true if the random access methods can be used.
   */
  public boolean getIsRandomAccess();
  
  /**
   * Tells if this iterator can have items added to it.
   * 
   * @return True if the XSequence can be mutated -- in which case
   * it presumably implements XSequenceMutable. (However, not all
   * sequences which implement that interface can be mutated; they
   * may have been locked.)
   * 
   * @see XSequenceMutable.lock()
   */
  public boolean isMutable();

  /**
   * Get the current position within the cached list, which is one
   * less than the next next() call will retrieve.  i.e. if you
   * call getCurrentPos() and the return is 0, the next fetch will
   * take place at index 1.  If the itterator is fresh, this will return 
   * -1.
   *
   * @return The position of the iteration.
   */
  public int getCurrentPos();

  /**
   * Set the current position in the sequence.  If the index is 
   * at the end of the sequence, the return value will be null.
   * This is mainly to support setting the index to zero without 
   * caring if the sequence contains a value.
   * 
   * @param i Must be a valid index.
   * @return the object at the given index, or null if 
   * index == length.
   * @throws exception if index &lt; 0 or &gt;= length. 
   * @throws exception if the sequence implementation does not 
   * support random access.
   */
  public void setCurrentPos(int i);
    
  /**
   * The number of items in the list. The range of valid item indices
   * is 0 to <code>length-1</code> inclusive. Note that this requires running
   * the iterator to completion, and presumably filling the cache.
   * <i>Warning: Use of this function can be expensive and detrimental to 
   * performance.</i> 
   *
   * @return The number of items in the list.
   */
  public int getLength();
  
  /**
   * Tell if this item is a singleton.  This method will also 
   * return true for an empty sequence.
   * @return true if this sequence is a singleton, or an empty sequence.
   */
  public boolean isSingletonOrEmpty();
  
  /**
   * @return XSequence object.
   * @see java.lang.Object#clone()
   */
  public Object clone() throws CloneNotSupportedException;
        
}
