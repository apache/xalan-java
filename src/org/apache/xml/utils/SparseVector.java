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

package org.apache.xml.utils;

// %REVIEW% Should this be based on SuballocatedIntVector instead?
// (Unclear. Pools will rarely be huge. But if they ever are...)
import org.apache.xml.utils.IntVector;
import java.util.Vector;

/** <p>SparseVector can be thought of as a replacement for
 * Hashtable, using int-value hashcodes/keys directly rather than requiring
 * that they be objectified as Integers.
 * (A standard Hashtable is relatively
 * inefficient at manipulating primitives as either keys or values.)</p>
 *
 * Note similarity to SparseVector, though that assigns its own keys
 * (allowing it to use internal position as an implied additional column)
 * and puts heavier emphasis on bidirectional mapping.
 *
 * <p>Design Priorities:
 * <li>Index-to-Object lookup speed is critical.</li>
 * <li>Object-to-index lookup speed is not.</li>
 * <li>Threadsafety is not guaranteed at this level.
 * Enforce that in the application if needed.</li>
 * <li>Storage efficiency is a major issue; that's why we're using
 * a sparse vector rather than a standard one.</li>
 * <li>I'm assuming a relatively small number of values will be stored;
 * the current primary use for this is xsi:type overrides in XNI2DTM,
 * and it is hoped these will be infrequent. To make this more general
 * it would need to be adaptive to size demands, as is Java's Hashtable.
 * </li>
 * </ul>
 * </p>
 *
 * Status: Not unit-tested.
 * */
public class SparseVector
{
  Vector m_values;
  IntVector m_keys;
  static final int HASHPRIME=101;
  int[] m_hashStart=new int[HASHPRIME];
  IntVector m_hashChain;
  static final int NULL=-1;

  public SparseVector()
    {
      m_values=new Vector();
      m_keys=new IntVector();
      m_hashChain=new IntVector(512);
      removeAllElements();
    }
  
  public void removeAllElements()
    {
      m_values.removeAllElements();
      m_keys.removeAllElements();
      for(int i=0;i<HASHPRIME;++i)
        m_hashStart[i]=NULL;
      m_hashChain.removeAllElements();
    }

  /** 
   * @param value to be stored
   * @param index to store it at
   * @return previous value at that index (may be null), or null if none.
   * */    
  public Object setElementAt(Object value,int index)
  {
  	return maybeSetElementAt(value,index,true);
  }

  /** 
   * @param index to retrieve
   * @return current value at that index (may be null), or null if none.
   * */    
  public Object elementAt(int index)
  {
  	return maybeSetElementAt(null,index,false);
  }

  protected Object maybeSetElementAt(Object value, int index,boolean set)
    {
      int hashslot=index%HASHPRIME;
      if(hashslot<0) hashslot=-hashslot;

      // Is it one we already know?
      int hashlast=m_hashStart[hashslot];
      int hashcandidate=hashlast;
      while(hashcandidate!=NULL)
        {
          if(m_keys.elementAt(hashcandidate)==index)
          {
          	// Found existing; update it
          	Object ret=m_values.elementAt(hashcandidate);
          	if(set)
	          	m_values.setElementAt(value,hashcandidate);
			return ret;          
          }
          hashlast=hashcandidate;
          hashcandidate=m_hashChain.elementAt(hashcandidate);
        }
        
      if(set)
      {
      	// New value. Add to tables.
        int newIndex=m_values.size();
        m_values.addElement(value);
        m_keys.addElement(index);
        m_hashChain.addElement(NULL);	// Initialize to no-following-same-hash
        if(hashlast==NULL)  // First for this hash
          m_hashStart[hashslot]=newIndex;
        else // Link from previous with same hash
          m_hashChain.setElementAt(newIndex,hashlast);
      }
      return null;
    }
}
