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

import java.util.Vector;

import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.XType;
import org.apache.xml.utils.IntVector;
import org.apache.xml.utils.WrappedRuntimeException;

/** This class provides an API representation for the XPath 2 Data Model's "Sequences"
 * -- which are the basic representation for typed values. Only built-in types, 
 * types derived from built-ins, and sets thereof are returned directly by the XPath2
 * DM; complex schema-types must be accessed through the document tree.
 * */
public class XSequenceImpl extends XObject
  implements XSequenceMutable
{
	// %OPT% Four vectors is not exactly compact.
	// Do we want to try to improve this?
	private Vector m_values=new Vector();
	protected Vector m_typeNamespaces=new Vector();
	protected Vector m_typeNames=new Vector();
  
  // We really shouldn't need xTypes, since the XObject should be cabable of 
  // storing the type.
	// private IntVector xTypes=new IntVector();

	protected int m_pos = -1;
	protected boolean m_unlocked=true;
	
	protected int m_homogtype=XType.EMPTYSEQ;
  
  /** Create a sequence, initially empty.
   * */
  public XSequenceImpl()
  {
    setValues(new Vector());
  }

	
	/** Create a sequence, initially empty.
	 * */
	public XSequenceImpl(boolean shouldCache)
	{
    if(shouldCache)
      setValues(new Vector());
	}

	/**
	 * %REVIEW% If the type is in the XMLSchema or XMLSchema-datatype
	 * namespaces, should we also assign a primitive XType?
	 *
	 * @param value Item value to add to the iteration.
	 * @param typeNamespace String containing namespace URI of schema type
	 * @param typeNamespace String containing local name of schema type
	 * 
	 * @return Sequence containing old plus new data. IT MAY BE A NEW
	 * OBJECT; user is responsible for always invoking this as
	 * <code>myseq=myseq.concat(val,ns,typename);</code>. However, IT MAY
	 * NOT BE A NEW OBJECT; don't assume that the old sequence will
	 * still be available after this operation returns.
	 * 
	 * @see XSequenceMutable.append(Object value,String typeNamespace,String typeName)
	 * */
	public XSequenceMutable concat(Object value,String typeNamespace,String typeName)
	{
		if(!m_unlocked)
		    throw new RuntimeException("Sequence is locked, hence not muteable!");
		
		int xtype=0;
		if(XType.XMLSCHEMA_DATATYPE_NAMESPACE.equals(typeNamespace)
			|| XType.XMLSCHEMA_NAMESPACE.equals(typeNamespace))
			xtype=XType.getTypeFromLocalName(typeName);
			
    XObject xvalue = XObjectFactory.create(value);
    getValues().addElement(xvalue);
		
		// If it's an XType, we may save some space by not
		// storing the strings and instead retrieving them
		// from the manefest-constant tables.
		// (Though storing them would simplify retrieval...)
		// %REVIEW%
		m_typeNames.addElement(xtype!=0 ? null : typeName);
		m_typeNamespaces.addElement(xtype!=0 ? null : typeNamespace);

    // Ignore xtype for right now.  -sb
		// xTypes.addElement(xtype);
		
		if(m_homogtype==XType.EMPTYSEQ)
			m_homogtype=xtype;
		else if(m_homogtype!=xtype)
			m_homogtype=XType.NOTHOMOGENOUS;
			
		return this;
	}
  
	/** Add an object to the sequence, along with its
	 * primitive datatype as defined in XType
	 * @param value Item value to add to the iteration.
	 * @param xtype Primitive type number, as defined in XType.
	 * 
	 * @return Sequence containing old plus new data. IT MAY BE A NEW
	 * OBJECT; user is responsible for always invoking this as
	 * <code>myseq=myseq.concat(val,type);</code>. However, IT MAY
	 * NOT BE A NEW OBJECT; don't assume that the old sequence will
	 * still be available after this operation returns.
	 * 
	 * @see XSequenceMutable.concat(Object value,int xtype)
	 * */
	public XSequenceMutable concat(Object value,int xtype)
	{
		if(!m_unlocked)
		    throw new RuntimeException("Sequence is locked, hence not mutable!");
		
    XObject xvalue = XObjectFactory.create(value);
		getValues().addElement(xvalue);

		// Could store the strings. But given that I'm suppressing
		// them for efficiency reasons when type was set by strings,
		// I'll suppress them here for consistancy.
		// %REVIEW%
		m_typeNames.addElement(null);
		m_typeNamespaces.addElement(null);
		
    // Ignore xTypes for right now.  See comment at commented out member var. -sb
		// xTypes.addElement(xtype);
		
		if(m_homogtype==XType.EMPTYSEQ)
			m_homogtype=xtype;
		else if(m_homogtype!=xtype)
			m_homogtype=XType.NOTHOMOGENOUS;
			
		return this;
	}
  
  /**
   * @see org.apache.xpath.objects.XSequenceMutable#concat(XObject)
   */
  public XSequenceMutable concat(XObject xvalue)
  {
    if(!m_unlocked)
        throw new RuntimeException("Sequence is locked, hence not muteable!");
    
    getValues().addElement(xvalue);

    // Could store the strings. But given that I'm suppressing
    // them for efficiency reasons when type was set by strings,
    // I'll suppress them here for consistancy.
    // %REVIEW%
    m_typeNames.addElement(null);
    m_typeNamespaces.addElement(null);
    
    // Ignore xTypes for right now.  See comment at commented out member var. -sb
    // xTypes.addElement(xvalue.getType());
    
    return this;
  }
	
	
	/** Append complete contents of another XSequence
	 * 
	 * @return Sequence containing old plus new data. IT MAY BE A NEW
	 * OBJECT; user is responsible for always invoking this as
	 * <code>myseq=myseq.concat(otherseq);</code>. However, IT MAY
	 * NOT BE A NEW OBJECT; don't assume that the old sequence will
	 * still be available after this operation returns.
	 * 
	 * @see XSequenceMutable.append(XSequence)
	 * */
	public XSequenceMutable concat(XSequence other)
	{
		if(!m_unlocked)
		    throw new RuntimeException("Sequence is locked, hence not muteable!");

		for(Object value=other.next();
			value!=null;
			value=other.next())
		{
			getValues().addElement(value);
					
			int xtype=other.getType();
      
      // Ignore xTypes for right now.  See comment at commented out member var. -sb
			// xTypes.addElement(xtype);
	
			m_typeNames.addElement(xtype==0 ? null : other.getTypeLocalName());
			m_typeNamespaces.addElement(xtype==0 ? null : other.getTypeNS());

			if(m_homogtype==XType.EMPTYSEQ)
				m_homogtype=xtype;
			else if(m_homogtype!=xtype)
				m_homogtype=XType.NOTHOMOGENOUS;
		}
		
		return this;
	}
	  	

	/** Prevent further mutation of this sequence. After this has
	 * been called, isMutable should return false.
	 * @see XSequence.isMutable()
	 * */
	public void lock()
	{
		m_unlocked=false;
	}

	/** @return the number of members in this sequence. */
	public int getLength()
	{
		return getValues().size();
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
	public XObject next()
	{
    if(m_pos >= getValues().size()-1)
      return null;
		return (XObject)getValues().elementAt(++m_pos);
	}
  
	/**
	 * @see org.apache.xml.dtm.XSequence#previous()
	 */
	public XObject previous()
	{
    if(m_pos <= 0)
      return null;
		return (XObject)getValues().elementAt(--m_pos);
	}
	
	/** %REVIEW% Format not yet firmly settled. Primarily for debugging purposes!
	 * */
	public String toString()
	{
		StringBuffer b=new StringBuffer("Sequence[");
		String start="";
		
		for(int i=0;i<getValues().size();++i)
		{
			XObject xvalue=(XObject)getValues().elementAt(i);
			String tns,tn;
			// int xtype=xTypes.elementAt(i);
      int xtype=xvalue.getType();
			if(xtype!=XType.ANYTYPE)
			{
				tns=XType.XMLSCHEMA_NAMESPACE;
				tn=XType.getLocalNameFromType(xtype);
			}
			else
			{
				// %OPT% Is it faster to cast to String, or
				// to call .toString()? The latter is _safer_...
				// (Opinion from Sun: Depends on your JVM.)
				tns=(String)m_typeNamespaces.elementAt(i);
				tn=(String)m_typeNames.elementAt(i);
				
			}
			b.append(start)
			    .append(tns).append(':').append(tn);
			if(xtype!=0)
				b.append(" (").append(xtype).append(')');
			b.append(" = ")
				.append(xvalue);
			if(xvalue!=null)
			    b.append(" {"+xvalue.getClass().getName()+"}");
			start=", ";
		}
		return b.append(']').toString();
	}
  
  /**
   * @see org.apache.xml.dtm.XSequence#allowDetachToRelease(boolean)
   */
  public void allowDetachToRelease(boolean allowRelease)
  {
  }

  /**
   * @see org.apache.xml.dtm.XSequence#detach()
   */
  public void detach()
  {
    // Do nothing
  }

  /**
   * @see org.apache.xml.dtm.XSequence#getCurrent()
   */
  public XObject getCurrent()
  {
    return (XObject)getValues().elementAt(m_pos);
  }

  /**
   * @see org.apache.xml.dtm.XSequence#getCurrentPos()
   */
  public int getCurrentPos()
  {
    return m_pos;
  }

  /**
   * @see org.apache.xml.dtm.XSequence#getTypeLocalName()
   */
  public String getTypeLocalName()
  {
    XObject xobj = getCurrent();
    int xtype = xobj.getType();
    if (xtype != XType.ANYTYPE)
      return XType.getLocalNameFromType(xtype);
    else
      return (String) m_typeNames.elementAt(m_pos);
  }

  /**
   * @see org.apache.xml.dtm.XSequence#getTypeNS()
   */
  public String getTypeNS()
  {
    XObject xobj = getCurrent();
    int xtype = xobj.getType();
    if (xtype != XType.ANYTYPE)
      return XType.XMLSCHEMA_NAMESPACE;
    else
      return (String) m_typeNamespaces.elementAt(m_pos);
  }

  /**
   * If this iterator holds homogenous primitive types, return that type ID.
   * 
   * @return The homogenous type, or NOTHOMOGENOUS, or EMPTYSEQ.
   */
  public int getTypes()
  {
    return m_homogtype;
  }
  
  /**
   * @see org.apache.xml.dtm.XSequence#getType()
   */
  public int getType()
  {
    XObject xobj = getCurrent();
    if(null == xobj)
      return XType.SEQ; // %REVIEW% Not sure what this should return.
    int xtype = xobj.getType();
    return xtype;
  }

  /** @return false, since I don't think we're ever going to put
   * nodes into an XSequenceImpl (they aren't objects, unless we
   * are handling DOM nodes...)
   * %REVIEW%
   * */
  public boolean isPureNodeSequence()
  {
  	return false;	
  }

  /**
   * @see org.apache.xml.dtm.XSequence#isFresh()
   */
  public boolean isFresh()
  {
    return (m_pos == -1) ? true : false;
  }

  /**
   * @see org.apache.xml.dtm.XSequence#isMutable()
   */
  public boolean isMutable()
  {
    return m_unlocked;
  }

  /** %BUG% Problem here -- to recognize inheritance we need to
   * reach into the schemas. Currently that can be done by using
   * the XPathContext (which we ain't got) to query the AbstractSchema
   * for an XPath2Type (both of which are Xerces-specific and buried 
   * in XNI2DTM) and asking it... We need a higher-level API for that
   * test.
   * 
   * %REVIEW% As a stopgap, match literal type name directly.
   * 
   * @see org.apache.xml.dtm.XSequence#isSchemaType(String, String)
   */
  public boolean isSchemaType(String namespace, String localname)
  {
  	String tns=getTypeNS();
  	String tn=getTypeLocalName();  	
  	return 
  		((tns==null) ? namespace==null : tns.equals(namespace))
  		&&
  		((tn==null) ? localname==null : tn.equals(localname))
  		;
  }

  /**
   * @see org.apache.xml.dtm.XSequence#isSingletonOrEmpty()
   */
  public boolean isSingletonOrEmpty()
  {
    return getLength() <= 1;
  }

  /**
   * @see org.apache.xml.dtm.XSequence#setCurrentPos(int)
   */
  public void setCurrentPos(int i)
  {
    m_pos = i-1;
  }

  /**
   * This method performs no actions.
   * @see org.apache.xml.dtm.XSequence#setShouldCache(boolean)
   */
  public void setShouldCache(boolean b)
  {
    // Ignore.  We always cache.
  }

  /**
   * @return true -- setCurrentPos() works for this object
   * @see org.apache.xml.dtm.XSequence#getIsRandomAccess()
   */
  public boolean getIsRandomAccess()
  {
    return true;
  }

  /**
   * @see org.apache.xml.dtm.XSequence#reset()
   */
  public void reset()
  {
    m_pos = -1;
  }


  /**
   * @see java.lang.Object#clone()
   */
  public Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }

  /**
   * Return a (fresh) sequence representing this object.
   * @return XSequence
   */
  public XSequence xseq()
  {
    if(isFresh())
      return this;
    try
    {
      XSequence xseq = (XSequence)this.clone();
      xseq.reset();
      return xseq;
    }
    catch (CloneNotSupportedException e)
    {
      // will never happen
      throw new WrappedRuntimeException(e);
    }
  }

  /**
   * Returns the values.
   * @return Vector
   */
  public Vector getValues()
  {
    return m_values;
  }

  /**
   * Returns the values.
   * @return Vector
   */
  public Object object()
  {
    return m_values;
  }

  /**
   * Sets the values.
   * @param values The values to set
   */
  public void setValues(Vector values)
  {
    this.m_values = values;
  }
  

  /**
   * @see org.apache.xpath.objects.XSequenceMutable#getItem(int)
   */
  public XObject getItem(int pos)
  {
    return (XObject)getValues().elementAt(pos);
  }

  /**
   * @see org.apache.xpath.objects.XSequenceMutable#insertItemAt(XObject, int)
   */
  public void insertItemAt(XObject value, int pos)
  {
    // TBD: Update homogtype?
    getValues().insertElementAt(value, pos);
  }

  /**
   * @see org.apache.xpath.objects.XSequenceMutable#setItem(XObject, int)
   */
  public void setItem(XObject value, int pos)
  {
    // TBD: Update homogtype?
    getValues().setElementAt(value, pos);
  }

  /**
   * @see org.apache.xpath.objects.XObject#isSequenceProper()
   */
  public boolean isSequenceProper()
  {
    return true;
  }

}

