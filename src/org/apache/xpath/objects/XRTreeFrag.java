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

import javax.xml.transform.TransformerException;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.utils.FastStringBuffer;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.XPathContext;
import org.w3c.dom.NodeList;

/**
 * <meta name="usage" content="general"/>
 * This class represents an XPath result tree fragment object, and is capable of
 * converting the RTF to other types, such as a string.
 */
public class XRTreeFrag extends XNodeSet implements Cloneable
{
  DTM m_dtm=null; // For possible storage management during finalize/destruct
  boolean m_allowRelease;

  /**
   * Create an XRTreeFrag Object.
   *
   * @param frag Document fragment this will wrap
   */
  public XRTreeFrag(int root, XPathContext xctxt, ExpressionNode parent)
  {
    super(root,xctxt);
    
    exprSetParent(parent);

    m_dtm = xctxt.getDTM(root);
    
    m_iter=xctxt.createDTMIterator(root);
  }
  
  /**
   * Create an XRTreeFrag Object.
   *
   * @param frag Document fragment this will wrap
   */
  public XRTreeFrag(int root, XPathContext xctxt)
  {
    super(root,xctxt);

    m_dtm = xctxt.getDTM(root);
    m_iter=xctxt.createDTMIterator(root);
  }

  /**
   * Create an XRTreeFrag Object.
   *
   * @param frag Document fragment this will wrap
   */
  /*
  private XRTreeFrag(Expression expr)
  {
    super(expr);
    
    // Can't retrieve m_iter until the expression has been executed.
    // Can't execute without an XCTXT.
    // Can't win.
    // Should this constructor exist at all? It seems to be here to support
    // XRTreeFragSelectWrapper... but I'm not sure that should be considered
    // an RTF rather than an XNodeSet!!!
  }
  */

  /**
   * Release any resources this object may have by calling destruct().
   * 
   * STRONG WARNING: This release will occur asynchronously. Resources it 
   * manipulates MUST be thread-safe!
   *
   * @throws Throwable
   */
  protected void finalize() throws Throwable
  {
    try
    {
      destruct();
    }
    finally
    {
      super.finalize();  // Always use this.
    }
  }
  
  /**
   * Specify if it's OK for detach to release the iterator for reuse.
   * 
   * @param allowRelease true if it is OK for detach to release this iterator 
   * for pooling.
   */
  public void allowDetachToRelease(boolean allowRelease)
  {
    m_allowRelease = allowRelease;
  }

  /**
   * Detaches the <code>DTMIterator</code> from the set which it iterated
   * over, releasing any computational resources and placing the iterator
   * in the INVALID state. After <code>detach</code> has been invoked,
   * calls to <code>nextNode</code> or <code>previousNode</code> will
   * raise a runtime exception.
   * 
   * In general, detach should only be called once on the object.
   */
  public void detach()
  {
    if(m_allowRelease)
    {
      // See #destruct() for a comment about this next check.
      int ident = m_dtmMgr.getDTMIdentity(m_dtm);
      DTM foundDTM = m_dtmMgr.getDTM(ident);      
      if(foundDTM == m_dtm)
      {
        m_dtmMgr.release(m_dtm, true);
        m_dtm = null;
        m_dtmMgr = null;
      }
      m_obj = null;
    }
  }
  
  /**
   * Forces the object to release it's resources.  This is more harsh than 
   * detach().  You can call destruct as many times as you want.
   */
  public void destruct()
  {
    if(null != m_dtm)
    {
      // For this next check, see http://nagoya.apache.org/bugzilla/show_bug.cgi?id=7622.
      // What happens if you don't do this this check:
      // 1) Transform#1 creates an XRTreeFrag.  This has a reference to a DTM, that in turn 
      //    is registered with a DTMManager.  The DTM will need to be deleted from the 
      //    DTMManager when the XRTreeFrag is deleted.  The XRTreeFrag  also contains a 
      //    reference to the XPathContext.
      // 2) Transform#1 completes.  The XPathContext is reset... namely the a bunch 
      //    of structures are reset or rebuilt, including DTMManagerDefault#m_dtms.  
      //    BUT, the XRTreeFrags are still hanging around, waiting to unregister themselves.
      // 3) Transform#2 starts humming along.  It builds a XRTreeFrag and installs that 
      //    RTF DTM into DTMManagerDefault#m_dtms[2].
      // 4) The finalizer thread wakes and decides to delete some of those old XRTreeFrags 
      //    from Transform#1.
      // 5) The XRTreeFrag#finalize() method references through the XPathContext, and 
      //    deletes what it thinks is it's DTM from  DTMManagerDefault#m_dtms[2] (via 
      //    getDTMIdentity(dtm)).
      // 6) Transform#2 tries to reference DTMManagerDefault#m_dtms[2], finds it is 
      //    null, and chaos results.
      int ident = m_dtmMgr.getDTMIdentity(m_dtm);
      DTM foundDTM = m_dtmMgr.getDTM(ident);      
      if(foundDTM == m_dtm)
      {
        m_dtmMgr.release(m_dtm, true);
        m_dtm = null;
        m_dtmMgr = null;
      }
    }
    m_obj = null;
 }

  /**
   * Tell what kind of class this is.
   * %REVIEW% Should we really be distinguishing RTF from other NodeSets?
   *
   * @return type CLASS_RTREEFRAG 
   */
  public int getType()
  {
    return CLASS_RTREEFRAG;
  }

  /**
   * Given a request type, return the equivalent string.
   * For diagnostic purposes.
   * %REVIEW% Should we really be distinguishing RTF from other NodeSets?
   *
   * @return type string "#RTREEFRAG"
   */
  public String getTypeString()
  {
    return "#RTREEFRAG";
  }

  /**
   * Cast result object to a number.
   * Note that this is different from XNodeSet's definition; we convert
   * the entire string content, not just item(0).
   *
   * @return The result tree fragment as a number or NaN
   */
  public double num()
  {
    XMLString s = xstr();

    return s.toDouble();
  }

  /**
   * Cast result object to a boolean.  This always returns true for a RTreeFrag
   * because it is treated like a node-set with a single root node.
   *
   * @return true
   */
  public boolean bool()
  {
    return true;
  }
  
  private XMLString m_xmlStr = null;
  
  /**
   * Cast result object to an XMLString.
   * Note that this is different from XNodeSet's definition; we convert
   * the entire string content, not just item(0).
   *
   * @return The document fragment node data or the empty string. 
   */
  public XMLString xstr()
  {
    if(null == m_xmlStr)
      m_xmlStr = m_dtm.getStringValue(item(0));
    
    return m_xmlStr;
  }
  
  /**
   * Append result object's content to a FastStringBuffer.
   * Note that this is different from XNodeSet's definition; we convert
   * the entire string content, not just item(0).
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
   * Note that this is different from XNodeSet's definition; we convert
   * the entire string content, not just item(0).
   *
   * @return The document fragment node data or the empty string. 
   */
  public String str()
  {
    String str = m_dtm.getStringValue(item(0)).toString();

    return (null == str) ? "" : str;
  }

  /**
   * Cast result object to a result tree fragment.
   *
   * @return The DTM Node Handle of the root node of the 
   * document fragment this wraps
   */
  public int rtf()
  {
    return item(0);
  }

  /**
   * Cast result object to a DTMIterator. Standard XObject method,
   * replaces special-case asNodeIterator()
   * @return The document fragment as a DTMIterator
   */
  public DTMIterator iter()
  {
    //return m_dtmMgr.createDTMIterator(item(0));
    return super.iter();
  }
  
  /** @deprecated Unnecessarily nonstandard.
   * @see iter()
   * */
  public DTMIterator asNodeIterator()
  {
    return iter();
  }
  

  /**
   * Cast result object to a DOM nodelist, primarily
   * for use as an extension function argument.
   * (tandard XObject function, replaces special-case convertToNodeset()
   *
   * @return The document fragment as a nodelist
   */
  public NodeList nodelist() throws javax.xml.transform.TransformerException
  {
  	return super.nodelist();
	/*
    if (m_obj instanceof NodeList)
      return (NodeList) m_obj;
    else
      return new org.apache.xml.dtm.ref.DTMNodeList(iter());
     */
  }

  /** @deprecated Unnecessarily nonstandard.
   * @see nodeList()
   * */
  public NodeList convertToNodeset() throws javax.xml.transform.TransformerException
  {
  	return nodelist();
  }


  /**
   * Tell if two objects are functionally equal.
   *
   * @param obj2 Object to compare this to
   *
   * @return True if the two objects are equal
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean equals(XObject obj2)
  {

    try
    {
      if (XObject.CLASS_NODESET == obj2.getType())
      {
  
        // In order to handle the 'all' semantics of 
        // nodeset comparisons, we always call the 
        // nodeset function.
        return ((XNodeSet)obj2).equalsExistential(this);
      }
      else if (XObject.CLASS_BOOLEAN == obj2.getType())
      {
        return bool() == obj2.bool();
      }
      else if (XObject.CLASS_NUMBER == obj2.getType())
      {
        return num() == obj2.num();
      }
      else if (XObject.CLASS_NODESET == obj2.getType())
      {
        return xstr().equals(obj2.xstr());
      }
      else if (XObject.CLASS_STRING == obj2.getType())
      {
        return xstr().equals(obj2.xstr());
      }
      else if (XObject.CLASS_RTREEFRAG == obj2.getType())
      {
  
        // Probably not so good.  Think about this.
        return xstr().equals(obj2.xstr());
      }
      else
      {
        return super.equals(obj2);
      }
    }
    catch(javax.xml.transform.TransformerException te)
    {
      throw new org.apache.xml.utils.WrappedRuntimeException(te);
    }
  }

}
