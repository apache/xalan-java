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
package org.apache.xalan.transformer;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.XType;
import org.apache.xml.utils.NodeVector;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XSequence;
import org.apache.xpath.objects.XSequenceMutable;
import org.apache.xpath.objects.XNodeSequenceSingleton;
import java.util.Vector;
import org.apache.xml.utils.QName;
import org.apache.xalan.templates.ElemForEachGroup;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;

/**
 * <meta name="usage" content="internal"/>
 * This class filters nodes from a key iterator, according to 
 * whether or not the use value matches the ref value.  
 */
public class GroupingIterator extends LocPathIterator implements XSequenceMutable
{
  /**
   * Constructor GroupingIterator
   *
   *
   * @param ref Key value to match
   * @param ki The main key iterator used to walk the source tree 
   */
  public GroupingIterator(Vector groups)
  {
    super(null);
    m_groups = groups;
  }
  
  XSequence m_group;
  Vector m_groups = new Vector();
  int m_next = 0;
  
  /**
   * Get the next node via getNextXXX.  Bottlenecked for derived class override.
   * @return The next node on the axis, or DTM.NULL.
   */
  protected int getNextNode()
  {                  
  	int next = DTM.NULL;
    if (m_next < m_groups.size())
    {
      next = ((ElemForEachGroup.Group)m_groups.elementAt(m_next++)).m_initItem;
      m_pos = m_next-1;
    }
    m_lastFetched = next;
    if (next == DTM.NULL)
      m_foundLast = true;
    
    return next;
  }
  
  /**
   *  Returns the next node in the set and advances the position of the
   * iterator in the set. After a NodeIterator is created, the first call
   * to nextNode() returns the first node in the set.
   *
   * @return  The next <code>Node</code> in the set being iterated over, or
   *   <code>null</code> if there are no more members in that set.
   */
  public int nextNode()
  {
    return getNextNode();
  }
  
  /**
   * Get the sequence matching the given node.
   * 
   * @return The sequence matching the given node .
   */
  public XSequence getSequence(int node)
  { 
    int size = m_groups.size();
    for (int i = 0; i < size; i++)
      {
        ElemForEachGroup.Group group = (ElemForEachGroup.Group)m_groups.elementAt(i);
        if (group.m_initItem == node)
        {
          return group.getGroupSeq();
        }
      }
  	return null;
  }
  
  /**
   * Reset the iterator.
   */
  public void reset()
  {
  	super.reset();
  	m_next = 0;
  }


  /**
   * Get the length of the cached nodes.
   *
   * <p>Note: for the moment at least, this only returns
   * the size of the nodes that have been fetched to date,
   * it doesn't attempt to run to the end to make sure we
   * have found everything.  This should be reviewed.</p>
   *
   * @return The size of the current cache list.
   */
  public int size()
  {
	return m_groups.size();
  }
  
  /**
   *  The number of nodes in the list. The range of valid child node indices
   * is 0 to <code>length-1</code> inclusive.
   *
   * @return The number of nodes in the list, always greater or equal to zero.
   */
  public int getLength()
  {
    return m_groups.size();
  }
  
  /**
   *  Returns the <code>index</code> th item in the collection. If
   * <code>index</code> is greater than or equal to the number of nodes in
   * the list, this returns <code>null</code> .
   * @param index  Index into the collection.
   * @return  The node at the <code>index</code> th position in the
   *   <code>NodeList</code> , or <code>null</code> if that is not a valid
   *   index.
   */
  public int item(int index)
  {
	if (index < m_groups.size())
    {
    	
      return ((ElemForEachGroup.Group)m_groups.elementAt(index)).m_initItem;
      
    }
      
    return DTM.NULL;
  }
  
  /**
   * Sets the node at the specified index of this vector to be the
   * specified node. The previous component at that position is discarded.
   *
   * <p>The index must be a value greater than or equal to 0 and less
   * than the current size of the vector.  
   * The iterator must be in cached mode.</p>
   * 
   * <p>Meant to be used for sorted iterators.</p>
   *
   * @param node Node to set
   * @param index Index of where to set the node
   */
  public void setItem(int node, int index)
  {
    int size = m_groups.size();
    if (index < size)
    {
      for (int i = 0; i < size; i++)
      {
        ElemForEachGroup.Group group = (ElemForEachGroup.Group)m_groups.elementAt(i);
        if (group.m_initItem == node)
        {
          ElemForEachGroup.Group temp = (ElemForEachGroup.Group)m_groups.elementAt(index);
          m_groups.setElementAt(group, index);
          m_groups.setElementAt(temp, i);
          break;
        }
      }
    }
  } 
  
  /**
   * Set the current position in the node set.
   *
   * @param i Must be a valid index greater
   * than or equal to zero and less than m_cachedNodes.size().
   */
  public void setCurrentPos(int i)
  {
  	m_pos = m_next = i;
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
		int node = nextNode();
		return new XNodeSequenceSingleton(node, this.getDTM(node));
	}
	
	/**
   * Returns the previous object in the set and moves the position of the
   * <code>XSequence</code> backwards in the set.
   * 
   * @return The previous item in the set being iterated over,
   *   or <code>null</code> if there are no more members in that set.
   */
  public XObject previous()
  {
  	int node = item(m_next - 1);
  	return new XNodeSequenceSingleton(node, this.getDTM(node));
  }
  
  /**
   * @see org.apache.xml.dtm.XSequence#detach()
   */
  public void detach()
  {
    // Do nothing
  }
  
  
  /**
   * @see org.apache.xml.dtm.XSequence#allowDetachToRelease(boolean)
   */
  public void allowDetachToRelease(boolean allowRelease)
  {
  }
  
  /**
   * @see org.apache.xml.dtm.XSequence#getCurrent()
   */
  public XObject getCurrent()
  {
  	int node = item(m_next);
    return new XNodeSequenceSingleton(node, this.getDTM(node));
  }
  
  
  
  /** 
   * Retrieve the current item's datatype namespace URI.
   * 
   * @return the namespace for the current value's type
   * @throws exception if there is no current item.
   */
  public String getTypeNS()
  {
  	return XType.XMLSCHEMA_NAMESPACE;
  }

  /** 
   * Retrieve current item's datatype namespace URI.
   * 
   * @return the localname of the current value's type
   * @throws exception if there is no current item.
   */
  public String getTypeLocalName()
  {
  	int xtype = getType();
    return XType.getLocalNameFromType(xtype);
  }

  /** 
   * Ask whether the current item's datatype equals or is derived from a specified
   * schema NSURI/localname pair.
   * 
   * @return true if the type is an instance of this schema datatype,
   * false if it isn't.
   * @throws exception if there is no current item.
   */
  public boolean isSchemaType(String namespace, String localname)
  {
  	return false;
  }
  
  /**
   * Retrieve the built-in atomic type of the current item
   * 
   * @return NODE, ANYTYPE, etc. as defined in XType
   * @throws exception if there is no current item.
   */
  public int getType()
  {
  	return XType.NODE;
  }
  
  /** @return true if the sequence is known to contain only NODEs.
   * (%REVIEW%: If false, that may not mean it doesn't, just that
   * we don't know this a priori and can't optimize.)
   * */
  public boolean isPureNodeSequence()
  {
  	return true;
  }
  
  
  
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
  public boolean getIsRandomAccess()
  {
  	return true;
  }
  
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
  public boolean isMutable()
  {
  	return true;
  }
  

	
	/**
   * If this iterator holds homogenous primitive types, return that type ID.
   * 
   * @return The homogenous type, or NOTHOMOGENOUS, or EMPTYSEQ.
   */
  public int getTypes()
  {
    return XType.SEQ;
  }
  
  
  
  /**
   * Tell if this item is a singleton.  This method will also 
   * return true for an empty sequence.
   * @return true if this sequence is a singleton, or an empty sequence.
   */
  public boolean isSingletonOrEmpty()
  {
  	 return getLength() <= 1;
  }
  
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
	public XSequenceMutable concat(Object value,String typeNamespace,String typeName)
	{
		return this;
	}

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
	public XSequenceMutable concat(Object value,int xtype)
	{
		return this;
	}
  
  /** Add an XObject to the sequence.
   * 
   * @return Sequence containing old plus new data. IT MAY BE A NEW
   * OBJECT; user is responsible for always invoking this as
   * <code>myseq=myseq.concat(newval,...);</code>. However, IT MAY
   * NOT BE A NEW OBJECT; don't assume that the old sequence will
   * still be available after this operation returns.
   * */
  public XSequenceMutable concat(XObject value)
  {
  	return this;
  } 

  /**
   * Set the item at the position.  For sorting type operations.
   * @param value An XObject that should not be a sequence with 
   *               multiple values.
   * @param pos The position to set the value, which must be valid.
   */
  public void setItem(XObject value, int pos)
  {
  	setItem(((XNodeSequenceSingleton)value).getNodeHandle(), pos);
  }

  /**
   * Insert the item at the position.  For sorting type operations.
   * @param value An XObject that should not be a sequence with 
   *               multiple values.
   * @param pos The position to set the value, which must be valid.
   */
  public void insertItemAt(XObject value, int pos)
  {
  	setItem(((XNodeSequenceSingleton)value).getNodeHandle(), pos);
  }
  
  /**
   * Convenience method to get the current item.
   * @param pos
   */
  public XObject getItem(int pos)
  {
  	int node = item(pos);
  	return new XNodeSequenceSingleton(node, getDTM(node));
  }
	
	/** Append complete contents of another XSequence
	 * 
	 * @return Sequence containing old plus new data. IT MAY BE A NEW
	 * OBJECT; user is responsible for always invoking this as
	 * <code>myseq=myseq.concat(newval,...);</code>. However, IT MAY
	 * NOT BE A NEW OBJECT; don't assume that the old sequence will
	 * still be available after this operation returns.
	 * */
	public XSequenceMutable concat(XSequence other)
	{
		return this;
	}
	  	

	/** Prevent further mutation of this sequence. After this has
	 * been called, isMutable should return false.
     * 
	 * @see XSequence.isMutable()
	 * */
	public void lock()
	{
	}
	
	
	
  
}