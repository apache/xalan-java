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
package org.apache.xpath.objects;

import javax.xml.transform.TransformerException;

import org.apache.xml.dtm.XType;
import org.apache.xml.utils.DateTimeObj;
import org.apache.xml.utils.Duration;
import org.apache.xml.utils.WrappedRuntimeException;
import org.apache.xml.utils.XMLString;
import org.apache.xml.xdm.XDMSequence;

/** This class provides an API representation for the XPath 2 Data Model's "Sequences"
 * -- which are the basic representation for typed values. Only built-in types, 
 * types derived from built-ins, and sets thereof are returned directly by the XPath2
 * DM; complex schema-types must be accessed through the document tree.
 * 
 * This implementation exists as a wrapper/proxy for the primitive
 * XDMSequence object. DTM is not aware of XPath, and XDMSequence
 * returns its values as primitive Java objects; we need to re-express
 * that in XPath objects.
 * */
public class XDTMSequence extends XObject implements XSequence
{
	XDMSequence m_dtmseq;

	protected int m_pos = -1;
	protected int m_homogtype = -1; // cache getTypes()
  
  /** Create a sequence, initially empty.
   * */
  public XDTMSequence(XDMSequence seq)
  {
  	m_dtmseq=seq;
  }

	
	/** @return the number of members in this sequence. */
	public int getLength()
	{
		return m_dtmseq.getLength();
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
	    if(m_pos >= m_dtmseq.getLength()-1)
    	  return null;
	  	return XObjectFactory.create(m_dtmseq.getValue(++m_pos));
	}
  
	/**
	 * @see org.apache.xml.dtm.XSequence#previous()
	 */
	public XObject previous()
	{
	    if(m_pos <= 0)
    	  return null;
	  	return XObjectFactory.create(m_dtmseq.getValue(--m_pos));
	}
	
	/** %REVIEW% Format not yet firmly settled. Primarily for debugging purposes!
	 * */
	public String toString()
	{
		return m_dtmseq.toString();
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
    int pos = m_pos;
    
  	return XObjectFactory.create(m_dtmseq.getValue(m_pos));
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
  	return m_dtmseq.getTypeLocalName(m_pos);
  }

  /**
   * @see org.apache.xml.dtm.XSequence#getTypeNS()
   */
  public String getTypeNS()
  {
  	return m_dtmseq.getTypeNS(m_pos);
  }

  /**
   * If this iterator holds homogenous primitive types, return that type ID.
   * 
   * @return The homogenous type, or NOTHOMOGENOUS, or EMPTYSEQ.
   */
  public int getTypes()
  {
  	if(m_homogtype==-1) // Return cached value if available
  	{
		if(m_dtmseq.getLength()==0)
  			m_homogtype=XType.EMPTYSEQ;
  		else
  		{
		  	// AT THIS TIME, XDMSequences are always homogenous
		  	// collections of primitives,
  			// so I can just check the current element. If that 
  			// changes, we'll have to loop and check for conflicts.
	  		//
		  	// %REVIEW% Should we do so now, as safety net?
		  	// Probably safer...
			m_homogtype=getType();
  		}
  	}  	
  	return m_homogtype;
  }
  
  
  /**
   * @see org.apache.xml.dtm.XSequence#getType()
   */
  public int getType()
  {
  	if(m_dtmseq.getLength()==0)
  		return m_homogtype=XType.EMPTYSEQ;

  	String[] names=XType.getNames();

  	// Need to check for derivation, rather than just doing lookup
  	// against the name table as in XType.getTypeFromLocalName()
  	//
  	// %REVIEW% What if m_pos is still -1?
  	// ... XSequenceImpl doesn't deal either. Ignored for now.
  	for(int whichtype=names.length-1;whichtype>=0;--whichtype)
  	{
  		if(m_dtmseq.isSchemaType(m_pos,XType.XMLSCHEMA_NAMESPACE,names[whichtype]))
  			return m_homogtype=whichtype;
  	}
  	
    return XType.SEQ; // ANYTYPE? NOTHOMOGENOUS? Shouldn't arise...
  }
  
  /**
   * @see org.apache.xml.dtm.XSequence#getType()
   */
  public int getValueType(int pos)
  {
    String ns = m_dtmseq.getTypeNS(pos);
    String localName = m_dtmseq.getTypeLocalName(pos);
    int typeID = XType.getTypeID(ns, localName);
    return typeID;
  }
  
  /**
   * @see org.apache.xml.dtm.XSequence#getType()
   */
  public int getValueType()
  {m_dtmseq.getTypeLocalName(m_pos);
    String ns = getTypeNS();
    String localName = getTypeLocalName();
    int typeID = XType.getTypeID(ns, localName);
    return typeID;
  }


  /** @return false -- this type is not used for nodes
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
    return false;
  }

  /**
   * @see org.apache.xml.dtm.XSequence#isSchemaType(String, String)
   */
  public boolean isSchemaType(String namespace, String localname)
  {
  	return m_dtmseq.isSchemaType(m_pos,namespace,localname);
  }

  /**
   * @see org.apache.xml.dtm.XSequence#isSingletonOrEmpty()
   */
  public boolean isSingletonOrEmpty()
  {
    return m_dtmseq.getLength() <= 1;
  }

  /**
   * @see org.apache.xml.dtm.XSequence#setCurrentPos(int)
   */
  public void setCurrentPos(int i)
  {
  	// %REVIEW% What if i is out of range?
    m_pos = i-1;
  }

  /**
   * This method performs no actions.
   * @see org.apache.xml.dtm.XSequence#setShouldCache(boolean)
   */
  public void setShouldCacheNodes(boolean b)
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
  
  public Object object()
  {
  	return m_dtmseq;
  }

  /**
   * @see org.apache.xpath.objects.XSequenceMutable#getItem(int)
   */
  public XObject getItem(int pos)
  {
  	Object value=m_dtmseq.getValue(pos);

  	// %TODO% At the low level, date/time are being returned as
  	// int[8] (because Xerces hasn't yet defined its own class
  	// to represent these). DateTimeObj isn't currently 
  	// set up to take this array and a type hint, and DTM is
  	// not currently set up to return the original strings.
  	// We could change either end of that equation. Improving
  	// DateTimeObj would be more efficient but bind us
  	// explicitly to Xerces' current stopgap...
  	//
  	// Durations are treated similarly. Note that we have to deal with
  	// the derived duration types defined by the XPath/XSLT/XQuery group.
  	if(value instanceof int[])
  	{
  		String typeNS=getTypeNS(),typeLocalName=getTypeLocalName();
  		if("duration".equals(typeLocalName) &&
  			XType.XMLSCHEMA_NAMESPACE.equals(typeNS) ||
  			("http://www.w3.org/2002/10/xquery-functions".equals(typeNS) &&
  			  ("yearMonthDuration".equals(typeLocalName) ||
  			  "dayTimeDuration".equals(typeLocalName)))
  			)
  			value=new Duration((int[])value);
  	
	  	else // It's a date of some flavor
      {
        DateTimeObj dt=new DateTimeObj((int[])value,typeLocalName);

        // The following is only good enough for now.  Needs more work. -sb
        int t = getValueType(pos);
        if(XType.DATE == t)
          return new XDate(dt);
        else if(XType.TIME == t)
          return new XTime(dt);
          
        value = dt;
          
      }
  	}

  	return XObjectFactory.create(value);
  }

  /**
   * @see org.apache.xpath.objects.XObject#isSequenceProper()
   */
  public boolean isSequenceProper()
  {
    return true;
  }

  /**
   * @see org.apache.xpath.objects.XObject#bool()
   */
  public boolean bool() throws TransformerException
  {
  	try
  	{
	    return getItem(0).bool();
  	}
  	catch(ArrayIndexOutOfBoundsException e)
  	{
  		return false; // Boolean has no NAN
  	}
  }

  /**
   * @see org.apache.xpath.objects.XObject#boolWithSideEffects()
   */
  public boolean boolWithSideEffects() throws TransformerException
  {
  	try
  	{
	    return getItem(0).boolWithSideEffects();
  	}
  	catch(ArrayIndexOutOfBoundsException e)
  	{
  		return false; // Boolean has no NAN
  	}
  }

  /**
   * @see org.apache.xpath.objects.XObject#date()
   */
  public DateTimeObj date() throws TransformerException
  {
    return getItem(0).date();
  }

  /**
   * @see org.apache.xpath.objects.XObject#datetime()
   */
  public DateTimeObj datetime() throws TransformerException
  {
    return getItem(0).datetime();
  }

  /**
   * @see org.apache.xpath.objects.XObject#daytimeDuration()
   */
  public Duration daytimeDuration() throws TransformerException
  {
    return getItem(0).daytimeDuration();
  }

  /**
   * @see org.apache.xpath.objects.XObject#duration()
   */
  public Duration duration() throws TransformerException
  {
    return getItem(0).duration();
  }

  /**
   * @see org.apache.xpath.objects.XObject#floatVal()
   */
  public float floatVal() throws TransformerException
  {
  	try
  	{
	    return getItem(0).floatVal();
  	}
  	catch(ArrayIndexOutOfBoundsException e)
  	{
  		return Float.NaN;
  	}
  }

  /**
   * @see org.apache.xpath.objects.XObject#integer()
   */
  public int integer() throws TransformerException
  {
  	try
  	{
	    return getItem(0).integer();
  	}
  	catch(ArrayIndexOutOfBoundsException e)
  	{
  		return 0; // Integer has no NAN
  	}
  }

  /**
   * @see org.apache.xpath.objects.XObject#num()
   */
  public double num() throws TransformerException
  {
  	try
  	{
	    return getItem(0).num();
  	}
  	catch(ArrayIndexOutOfBoundsException e)
  	{
  		return Double.NaN;
  	}
  }

  /**
   * @see org.apache.xpath.objects.XObject#str()
   */
  public String str()
  {
  	try
  	{
	    return getItem(0).str();
  	}
  	catch(ArrayIndexOutOfBoundsException e)
  	{
  		return "";
  	}
  }

  /**
   * @see org.apache.xpath.objects.XObject#xstr()
   */
  public XMLString xstr()
  {
  	try
  	{
	    return getItem(0).xstr();
  	}
  	catch(ArrayIndexOutOfBoundsException e)
  	{
  		return XString.EMPTYSTRING;
  	}
  }

  /**
   * @see org.apache.xpath.objects.XObject#yearmonthDuration()
   */
  public Duration yearmonthDuration() throws TransformerException
  {
    return getItem(0).yearmonthDuration();
  }

}

