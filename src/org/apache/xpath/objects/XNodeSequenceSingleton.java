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

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.XType;
import org.apache.xml.dtm.ref.DTMNodeList;
import org.apache.xml.utils.DateTimeObj;
import org.apache.xml.utils.WrappedRuntimeException;
import org.apache.xml.utils.XMLString;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

/**
 * The responsibility of enclosing_type is to .
 * 
 * Created Jul 20, 2002
 * @author sboag
 */
public class XNodeSequenceSingleton extends XObject
  implements XSequence
{	
  DTM m_dtm;
  int m_nodeHandle;
  int m_pos = -1;
  
  /**
   * Constructor for XNodeSequenceSingleton.
   */
  public XNodeSequenceSingleton(int nodeHandle, XNodeSet owningNodeSet)
  {
  	this(nodeHandle,owningNodeSet.getDTM(nodeHandle));
  }
  
  /**
   * Constructor for XNodeSequenceSingleton.
   */
  public XNodeSequenceSingleton(int nodeHandle, DTM dtm)
  {
    m_dtm=dtm;
    m_nodeHandle = nodeHandle;
  }
  
  
  /**
   * Tell what kind of class this is.
   *
   * @return CLASS_UNKNOWN
   * @see org.apache.xpath.objects.XObject@getType()
   */
  /*
  public int getType()
  {
  	// %REVIEW% May not be the right thing. Makes extensions work,
  	// but currently causes InstanceofExpr to cast it incorrectly
  	// (to XNodeSet, not compatable). 
  	// Should we be subclassing XNodeSet rather than XObject? 
  	// Should we change this type?
  	// Should we change InstanceOfExpr to cast to XSequence,
  	//		which is a shared superclass?
  	return CLASS_NODESET; 
  }
  */

  /**
   * Given a request type, return the equivalent string.
   * For diagnostic purposes.
   *
   * @return type string "#UNKNOWN" + object class name
   * @see org.apache.xpath.objects.XObject@getTypeString()
   */
  /*
  public String getTypeString()
  {
    return "#NODESET";
  }
  */

  /**
   * @see org.apache.xpath.objects.XSequence#getTypes()
   */
  public int getTypes()
  {
    DTM dtm = getDTM();
    return XType.getTypeID(dtm, m_nodeHandle);
  }

  /**
   * @see org.apache.xpath.objects.XSequence#next()
   */
  public XObject next()
  {
      if(m_pos == -1)
      {
        m_pos++;
        return this;
      }
      else
        return null;
///    try
//    {
//      if(m_pos == -1)
//      {
//        XObject next = (XObject)this.clone();
//        next.reset();
//        m_pos++;
//        return next;
//      }
//      else
//        return null;
//    }
//    catch (CloneNotSupportedException e)
//    {
//      throw new WrappedRuntimeException(e);
//    }
  }

  /**
   * @see org.apache.xpath.objects.XSequence#previous()
   */
  public XObject previous()
  {
    if(m_pos == 0)
    {
      m_pos--;
      return this;
    }
    else
      return null;
  }

  /**
   * @see org.apache.xpath.objects.XSequence#getCurrent()
   */
  public XObject getCurrent()
  {
    if(m_pos == 0)
    {
      return this;
    }
    else
      return null;
  }

  /**
   * @see org.apache.xpath.objects.XSequence#isFresh()
   */
  public boolean isFresh()
  {
    return m_pos == -1;
  }

  /**
   * @see org.apache.xpath.objects.XSequence#getTypeNS()
   */
  public String getTypeNS()
  {
    DTM dtm = getDTM();
    return dtm.getSchemaTypeNamespace(m_nodeHandle);
  }

  /**
   * @see org.apache.xpath.objects.XSequence#getTypeLocalName()
   */
  public String getTypeLocalName()
  {
    DTM dtm = getDTM();
    return dtm.getSchemaTypeLocalName(m_nodeHandle);
  }

  /**
   * @see org.apache.xpath.objects.XSequence#isSchemaType(String, String)
   */
  public boolean isSchemaType(String namespace, String localname)
  {
    DTM dtm = getDTM();
    return dtm.isNodeSchemaType(m_nodeHandle, namespace, localname);
  }

  /**
   * @see org.apache.xpath.objects.XSequence#isPureNodeSequence()
   */
  public boolean isPureNodeSequence()
  {
    return true;
  }

  /**
   * @see org.apache.xpath.objects.XSequence#setShouldCache(boolean)
   */
  public void setShouldCacheNodes(boolean b)
  {
  }

  /**
   * @see org.apache.xpath.objects.XSequence#getIsRandomAccess()
   */
  public boolean getIsRandomAccess()
  {
    return true;
  }

  /**
   * @see org.apache.xpath.objects.XSequence#isMutable()
   */
  public boolean isMutable()
  {
    return false;
  }

  /**
   * @see org.apache.xpath.objects.XSequence#getCurrentPos()
   */
  public int getCurrentPos()
  {
    return m_pos;
  }

  /**
   * @see org.apache.xpath.objects.XSequence#setCurrentPos(int)
   */
  public void setCurrentPos(int i)
  {
    m_pos = i;
  }

  /**
   * @see org.apache.xpath.objects.XSequence#getLength()
   */
  public int getLength()
  {
    return 1;
  }

  /**
   * @see org.apache.xpath.objects.XSequence#isSingletonOrEmpty()
   */
  public boolean isSingletonOrEmpty()
  {
    return true;
  }
  
  /**
   * @see java.lang.Object#clone()
   */
  public Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }

  /**
   * Get numeric value of the string conversion from a single node.
   *
   * @param n Node to convert
   *
   * @return numeric value of the string conversion from a single node.
   */
  public double getNumberFromNode(int n)
  {
    XMLString xstr = getDTM().getStringValue(n);
    // String str = xstr.toString();
    return xstr.toDouble();
  }

  /**
   * Cast result object to a number.
   *
   * @return numeric value of the string conversion from the 
   * next node in the NodeSetDTM, or NAN if no node was found
   */
  public double num()
  {

    int node = m_nodeHandle;
    return (node != DTM.NULL) ? getNumberFromNode(node) : Double.NaN;
  }
  
  /**
   * @see org.apache.xpath.objects.XObject#floatVal()
   */
  public float floatVal() throws TransformerException
  {
    int node = m_nodeHandle;
    return (node != DTM.NULL) ? (float)getNumberFromNode(node) : Float.NaN;
  }

  /**
   * @see org.apache.xpath.objects.XObject#integer()
   */
  public int integer() throws TransformerException
  {
    int node = m_nodeHandle;
    return (node != DTM.NULL) ? (int)getNumberFromNode(node) : 0;
  }

  /**
   * @see org.apache.xpath.objects.XObject#numWithSideEffects()
   */
  public double numWithSideEffects() throws TransformerException
  {
    return super.numWithSideEffects();
  }

  
  /**
   * Cast result object to a boolean.
   *
   * @return True if there is a next node in the nodeset
   */
  public boolean bool()
  {
    // Check the new rules about this.  -sb
    return (m_nodeHandle != DTM.NULL);
  }
  
  /**
   * Cast result object to a boolean, but allow side effects, such as the 
   * incrementing of an iterator.
   *
   * @return True if there is a next node in the nodeset
   */
  public boolean boolWithSideEffects()
  {
    return (m_nodeHandle != DTM.NULL);
  }

  
  /**
   * Get the string conversion from a single node.
   *
   * @param n Node to convert
   *
   * @return the string conversion from a single node.
   */
  public XMLString getStringFromNode(int n)
  {
    // %OPT%
    // I guess we'll have to get a static instance of the DTM manager...
    if(DTM.NULL != n)
    {
      return getDTM().getStringValue(n);
    }
    else
    {
      return org.apache.xpath.objects.XString.EMPTYSTRING;
    }
  }
  
  /**
   * Directly call the
   * characters method on the passed ContentHandler for the
   * string-value. Multiple calls to the
   * ContentHandler's characters methods may well occur for a single call to
   * this method.
   *
   * @param ch A non-null reference to a ContentHandler.
   *
   * @throws org.xml.sax.SAXException
   */
  public void dispatchCharactersEvents(org.xml.sax.ContentHandler ch)
          throws org.xml.sax.SAXException
  {
    int node = m_nodeHandle;
  
    if(node != DTM.NULL)
    {
      getDTM().dispatchCharactersEvents(node, ch, false);
    }
    
  }
  
  /**
   * Cast result object to an XMLString.
   *
   * @return The document fragment node data or the empty string. 
   */
  public XMLString xstr()
  {
    int node = m_nodeHandle;
    return (node != DTM.NULL) ? getStringFromNode(node) : XString.EMPTYSTRING;
  }
  
  /**
   * Cast result object to a string.
   *
   * @return The string this wraps or the empty string if null
   */
  public void appendToFsb(org.apache.xml.utils.FastStringBuffer fsb)
  {
    XString xstring = (XString)xstr();
    xstring.appendToFsb(fsb);
  }
  

  /**
   * Cast result object to a string.
   *
   * @return the string conversion from the next node in the nodeset
   * or "" if there is no next node
   */
  public String str()
  {
    int node = m_nodeHandle;
    return (node != DTM.NULL) ? getStringFromNode(node).toString() : "";   
  }
  
  /**
   * Return a java object that's closest to the representation
   * that should be handed to an extension.
   *
   * @return The object that this class wraps
   */
  public Object object()
  {
    if(null == m_dtm)	// Should this case ever arise???
      return this;	// And is this response useful? %REVIEW% 
    else
      //return m_dtm;
      return m_dtm.getNode(m_nodeHandle);
  }

  /**
   * Returns the dtm.
   * @return DTM
   */
  public DTM getDTM()
  {
    return m_dtm;
  }

  /**
   * Returns the nodeHandle.
   * @return int
   */
  public int getNodeHandle()
  {
    return m_nodeHandle;
  }

  /**
   * Sets the dtm.
   * @param dtm The dtm to set
   */
  public void setDTM(DTM dtm)
  {
    m_dtm = dtm;
  }

  /**
   * Sets the nodeHandle.
   * @param nodeHandle The nodeHandle to set
   */
  public void setNodeHandle(int nodeHandle)
  {
    m_nodeHandle = nodeHandle;
  }

  /**
   * Tell if one object is less than the other.
   *
   * @param obj2 object to compare this nodeset to
   *
   * @return see this.compare(...) 
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean lessThan(XObject obj2) throws javax.xml.transform.TransformerException
  {
    return compare(obj2, XNodeSet.S_LT);
  }

  /**
   * Tell if one object is less than or equal to the other.
   *
   * @param obj2 object to compare this nodeset to
   *
   * @return see this.compare(...) 
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean lessThanOrEqual(XObject obj2) throws javax.xml.transform.TransformerException
  {
    return compare(obj2, XNodeSet.S_LTE);
  }

  /**
   * Tell if one object is less than the other.
   *
   * @param obj2 object to compare this nodeset to
   *
   * @return see this.compare(...) 
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean greaterThan(XObject obj2) throws javax.xml.transform.TransformerException
  {
    return compare(obj2, XNodeSet.S_GT);
  }

  /**
   * Tell if one object is less than the other.
   *
   * @param obj2 object to compare this nodeset to
   *
   * @return see this.compare(...) 
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean greaterThanOrEqual(XObject obj2)
          throws javax.xml.transform.TransformerException
  {
    return compare(obj2, XNodeSet.S_GTE);
  }

  /**
   * Tell if two objects are functionally equal.
   *
   * @param obj2 object to compare this nodeset to
   *
   * @return see this.compare(...) 
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean equalsExistential(XObject obj2)
  {
    try
    {
      return compare(obj2, XNodeSet.S_EQ);
    }
    catch(javax.xml.transform.TransformerException te)
    {
      throw new org.apache.xml.utils.WrappedRuntimeException(te);
    }
  }

  /**
   * Tell if two objects are functionally not equal.
   *
   * @param obj2 object to compare this nodeset to
   *
   * @return see this.compare(...) 
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean notEquals(XObject obj2) throws javax.xml.transform.TransformerException
  {
    return compare(obj2, XNodeSet.S_NEQ);
  }
  
  /**
   * @see java.lang.Object#equals(Object)
   */
  public boolean equals(XObject arg0)
  {
    int nodeHandle = ((XObject) arg0).getNodeHandle();
    return nodeHandle == m_nodeHandle;
  }


  /**
   * @see java.lang.Object#equals(Object)
   */
  public boolean equals(Object arg0)
  {    
    return super.equals(arg0);
  }
  
  /**
   * Tell if one object is less than the other.
   *
   * @param obj2 Object to compare this nodeset to
   * @param comparator Comparator to use
   *
   * @return See the comments below for each object type comparison 
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean compare(XObject obj2, Comparator comparator)
    throws javax.xml.transform.TransformerException
  {

    boolean result = false;
    int type = obj2.getType();

    if (obj2.isNodesetExpr())
    {
      // %OPT% This should be XMLString based instead of string based...

      // From http://www.w3.org/TR/xpath: 
      // If both objects to be compared are node-sets, then the comparison 
      // will be true if and only if there is a node in the first node-set 
      // and a node in the second node-set such that the result of performing 
      // the comparison on the string-values of the two nodes is true.
      // Note this little gem from the draft:
      // NOTE: If $x is bound to a node-set, then $x="foo" 
      // does not mean the same as not($x!="foo"): the former 
      // is true if and only if some node in $x has the string-value 
      // foo; the latter is true if and only if all nodes in $x have 
      // the string-value foo.
      XSequence list2 = obj2.xseq();
      int node1 = m_nodeHandle;
      java.util.Vector node2Strings = null;

      XMLString s1 = getStringFromNode(node1);

      if (null == node2Strings)
      {
        
        XObject next;
        while (null != (next = list2.next()))
        {
           XMLString s2 = next.xstr();

          if (comparator.compareStrings(s1, s2))
          {
            result = true;

            break;
          }

          if (null == node2Strings)
            node2Strings = new java.util.Vector();

          node2Strings.addElement(s2);
        }
      }
      else
      {
        int n = node2Strings.size();

        for (int i = 0; i < n; i++)
        {
          if (comparator
            .compareStrings(s1, (XMLString) node2Strings.elementAt(i)))
          {
            result = true;

            break;
          }
        }
      }
      list2.reset();
    }
    else
      if (XObject.CLASS_BOOLEAN == type)
      {

        // From http://www.w3.org/TR/xpath: 
        // If one object to be compared is a node-set and the other is a boolean, 
        // then the comparison will be true if and only if the result of 
        // performing the comparison on the boolean and on the result of 
        // converting the node-set to a boolean using the boolean function 
        // is true.
        double num1 = bool() ? 1.0 : 0.0;
        double num2 = obj2.num();

        result = comparator.compareNumbers(num1, num2);
      }
      else
        if (XObject.CLASS_NUMBER == type)
        {

          // From http://www.w3.org/TR/xpath: 
          // If one object to be compared is a node-set and the other is a number, 
          // then the comparison will be true if and only if there is a 
          // node in the node-set such that the result of performing the 
          // comparison on the number to be compared and on the result of 
          // converting the string-value of that node to a number using 
          // the number function is true. 
          double num2 = obj2.num();
          int node = m_nodeHandle;

          double num1 = getNumberFromNode(node);

          if (comparator.compareNumbers(num1, num2))
          {
            result = true;
          }
        }
        else
          if (XObject.CLASS_RTREEFRAG == type)
          {
            XMLString s2 = obj2.xstr();
            int node = m_nodeHandle;

            XMLString s1 = getStringFromNode(node);

            if (comparator.compareStrings(s1, s2))
            {
              result = true;
            }
          }
          else
            if (XObject.CLASS_STRING == type)
            {

              // From http://www.w3.org/TR/xpath: 
              // If one object to be compared is a node-set and the other is a 
              // string, then the comparison will be true if and only if there 
              // is a node in the node-set such that the result of performing 
              // the comparison on the string-value of the node and the other 
              // string is true. 
              XMLString s2 = obj2.xstr();
              int node = m_nodeHandle;

              XMLString s1 = getStringFromNode(node);
              if (comparator.compareStrings(s1, s2))
              {
                result = true;
              }
            }
            else
            {
              result = comparator.compareNumbers(this.num(), obj2.num());
            }

    return result;
  }


  /**
   * @see org.apache.xpath.Expression#isNodesetExpr()
   */
  public boolean isNodesetExpr()
  {
    return true;
  }

  /**
   * @see org.apache.xpath.objects.XObject#xseq()
   */
  public XSequence xseq()
  {
    try
    {
      XSequence newXSeq = (XSequence)clone();
      // newXSeq.reset();
      return newXSeq;
    }
    catch (CloneNotSupportedException e)
    {
      throw new WrappedRuntimeException(e);
    }
  }

  /**
   * @see org.apache.xpath.objects.XObject#reset()
   */
  public void reset()
  {
    super.reset();
    m_pos = -1;
  }

  /**
   * @see org.apache.xpath.objects.XObject#nodelist()
   */
  public NodeList nodelist() throws TransformerException
  {
    return new DTMNodeList(getDTM(),getNodeHandle(), false);
  }

  /**
   * @see org.apache.xpath.objects.XObject#nodeset()
   */
  public NodeIterator nodeset() throws TransformerException
  {
    DTM dtm = getDTM();
    DTMIterator iter = new XNodeSet(getNodeHandle(), dtm.getManager());
    return new org.apache.xml.dtm.ref.DTMNodeIterator(iter);
  }
  
  /**
   * Cast result object to a nodelist. Always issues an error.
   *
   * @return null
   *
   * @throws javax.xml.transform.TransformerException
   */
  public DTMIterator iter() throws javax.xml.transform.TransformerException
  {

    DTM dtm = getDTM();
    DTMIterator iter = new XNodeSet(getNodeHandle(), dtm.getManager());

    return iter;
  }

  /**
   * @see org.apache.xpath.objects.XObject#getType()
   */
  public int getType()
  {
    return XType.NODE;
  }

  /**
   * @see org.apache.xpath.objects.XObject#getValueType()
   */
  public int getValueType()
  {
    int nodeHandle = getNodeHandle();
    if (DTM.NULL != nodeHandle)
    {
      DTM dtm = getDTM();
      
      return XType.getTypeID(dtm, nodeHandle);
    }
    else
      return XType.ANYTYPE;
  }
  
//  /**
//   * @see org.apache.xpath.objects.XObject#date()
//   */
//  public DateTimeObj date() throws TransformerException
//  {
//    DTM dtm = getDTM();
//    dtm.getN
//    return super.date();
//  }

}
