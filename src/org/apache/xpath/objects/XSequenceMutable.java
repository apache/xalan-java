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



/** Interface for constructable version of XSequence.
 * 
 * Defined in DTM because XSequence is defined her; if the superclass
 * is moved, this should be moved with it.
 * 
 * @author keshlam
 * @since 7/18/02
 */
public interface XSequenceMutable extends XSequence
{
	/** Add an object to the sequence, along with its Schema-based
	 * datatype.  Within the sequence, wrap the object in an XObject. 
	 *
	 * @param value Item value to add to the iteration.
	 * @param typeNamespace String containing namespace URI of schema type
	 * @param typeNamespace String containing local name of schema type
	 * 
	 * @return Sequence containing old plus new data. IT MAY BE A NEW
	 * OBJECT; user is responsible for always invoking this as
	 * <code>myseq=myseq.concat(newval,...);</code>. However, IT MAY
	 * NOT BE A NEW OBJECT; don't assume that the old sequence will
	 * still be available after this operation returns.
	 * */
	public XSequenceMutable concat(Object value,String typeNamespace,String typeName);

	/** Add an object to the sequence, along with its
	 * primitive datatype as defined in XType.  Within the sequence, 
   * wrap the object in an XObject.
	 * @param value Item value to add to the iteration.
	 * @param xtype Primitive type number, as defined in XType.
	 * 
	 * @return Sequence containing old plus new data. IT MAY BE A NEW
	 * OBJECT; user is responsible for always invoking this as
	 * <code>myseq=myseq.concat(newval,...);</code>. However, IT MAY
	 * NOT BE A NEW OBJECT; don't assume that the old sequence will
	 * still be available after this operation returns.
	 * */
	public XSequenceMutable concat(Object value,int xtype);
  
  /** Add an XObject to the sequence.
   * 
   * @return Sequence containing old plus new data. IT MAY BE A NEW
   * OBJECT; user is responsible for always invoking this as
   * <code>myseq=myseq.concat(newval,...);</code>. However, IT MAY
   * NOT BE A NEW OBJECT; don't assume that the old sequence will
   * still be available after this operation returns.
   * */
  public XSequenceMutable concat(XObject value);

  /**
   * Set the item at the position.  For sorting type operations.
   * @param value An XObject that should not be a sequence with 
   *               multiple values.
   * @param pos The position to set the value, which must be valid.
   */
  public void setItem(XObject value, int pos);

  /**
   * Insert the item at the position.  For sorting type operations.
   * @param value An XObject that should not be a sequence with 
   *               multiple values.
   * @param pos The position to set the value, which must be valid.
   */
  public void insertItemAt(XObject value, int pos);
  
  /**
   * Convenience method to get the current item.
   * @param pos
   */
  public XObject getItem(int pos);
	
	/** Append complete contents of another XSequence
	 * 
	 * @return Sequence containing old plus new data. IT MAY BE A NEW
	 * OBJECT; user is responsible for always invoking this as
	 * <code>myseq=myseq.concat(newval,...);</code>. However, IT MAY
	 * NOT BE A NEW OBJECT; don't assume that the old sequence will
	 * still be available after this operation returns.
	 * */
	public XSequenceMutable concat(XSequence other);
	  	

	/** Prevent further mutation of this sequence. After this has
	 * been called, isMutable should return false.
   * 
	 * @see XSequence.isMutable()
	 * */
	public void lock();
}
