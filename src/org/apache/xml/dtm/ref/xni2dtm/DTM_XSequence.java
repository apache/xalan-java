/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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
package org.apache.xml.dtm.ref.xni2dtm;

import org.apache.xml.dtm.DTMSequence;

/** This class provides an API representation for the XPath 2 Data Model's "Sequences"
 * -- which are the basic representation for typed values. Only built-in types, 
 * types derived from built-ins, and sets thereof are returned directly by the XPath2
 * DM; complex schema-types must be accessed through the document tree.
 * 
 * %REVIEW% Should we have a more compact empty-sequence? (Probably not worth
 * the effort, since XNI2DTM will use a stored instance.)
 * */
public class DTM_XSequence 
implements DTMSequence
{
  Object[] values;
  
  // We currently expect all elements in schema-produced sequencs to have
  // the same type. This may need to be enhanced at some point in the future.
  XPath2Type type;

  /** Create a sequence.
   * 
   * @param value Object representation of the primitive value. May be array.
   * @param type Datatype being represented. May be list-of type.
   * */
  public DTM_XSequence(Object value,XPath2Type type)
  {   
    // If it's a list-of type, we want to unwrap that and resport
    // the type of the individual element...

    XPath2Type itemType=type.getItemType();
    
    if(itemType==null)
    {
      // Creating the array is a bit wasteful. Special-case this?
      this.values=new Object[1];
      this.values[0]=value;
      this.type=type;
    }
    else
    {
      this.values=(Object[])value;
      this.type=itemType;
    }
  }
  
  /** @return the number of members in this sequence. */
  public int getLength()
  {
    return values.length;
  }
  
  /** Retrieve the value for this member of the sequence. Since values may be
   * a heterogenous mix, and their type may not be known until they're examined,
   * they're returned as Objects. This is storage-inefficient if the value(s)
   * is/are builtins that map to Java primitives. Tough.
   * 
   * @param index 0-based index into the sequence.
   * @return the specified value
   * @throws exception if index <0 or >=length   
   * */
  public Object getValue(int index)
  {
    return values[index];
  }
  
  /** Retrieve the datatype namespace URI for this member of the sequence.
   * 
   * @param index 0-based index into the sequence.
   * @return the namespace for the specified value's type
   * @throws exception if index <0 or >=length   
   * */
  public String getTypeNS(int index)
  {
    return type.getTargetNamespace();
  }
  
  /** Retrieve the datatype namespace URI for this member of the sequence.
   * 
   * %REVIEW% Do we really need type -- 
   * or can we just tell folks to do instanceOf on the Java value objects?  
   * 
   * @param index 0-based index into the sequence.
   * @return the localname of the specified value's type
   * @throws exception if index <0 or >=length   
   * */
  public String getTypeLocalName(int index)
  {
    return type.getTypeName();
  }
  
  /** Ask whether this member's datatype equals or is derived from a specified
   * schema NSURI/localname pair.
   * 
   * @param index 0-based index into the sequence.
   * @return true if the type is an instance of this schema datatype,
   * false if it isn't.
   * @throws exception if index <0 or >=length   
   * */
  public boolean isSchemaType(int index, String namespace, String localname)
  {
    return type.derivedFrom(namespace,localname);
  }
  
  /** %REVIEW% Format not yet firmly settled. Primarily for debugging purposes!
   * */
  public String toString()
  {
    StringBuffer b=new StringBuffer("Sequence[");
    String start="";
    
    for(int i=0;i<values.length;++i)
    {
      b.append(start)
          .append(type.getTargetNamespace()).append(':').append(type.getTypeName())
          .append(" = ");
          
      if(values[i] instanceof int[])
      {
      	int[] v=(int[])values[i];
      	b.append('[');
      	if(v.length>0)
      		b.append(v[0]);
      	for(int j=1;j<v.length;++j)
	      	b.append(',').append(v[j]);
      	b.append(']');
      }
      else
        b.append(values[i]);
        
      if(values[i]!=null)
          b.append(" {"+values[i].getClass().getName()+"}");
      start=", ";
    }
    return b.append(']').toString();
  }
}

