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
package org.apache.xalan.stree;

import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.WhiteSpaceInfo;

import org.xml.sax.SAXException;

/**
 * <meta name="usage" content="internal"/>
 * NEEDSDOC Class Parent <needs-comment/>
 */
public class Parent extends Child
{

  /**
   * Constructor Parent
   *
   *
   * NEEDSDOC @param doc
   */
  public Parent(DocumentImpl doc)
  {
    super(doc);
  }

  /** NEEDSDOC Field m_posInChildList          */
  protected int m_posInChildList;

  /** NEEDSDOC Field m_childCount          */
  protected int m_childCount = 0;  // includes attributes, elements

  /** NEEDSDOC Field m_isComplete          */
  boolean m_isComplete = false;

  /** NEEDSDOC Field m_last          */
  Child m_last;

  /** NEEDSDOC Field m_first          */
  Child m_first;

  /**
   * Get the number of children this node currently contains.
   * Note that this will only return the number of children
   * added so far.  If the isComplete property is false,
   * it is likely that more children will be added.
   * DON'T CALL THIS FUNCTION IF YOU CAN HELP IT!!!
   *
   * NEEDSDOC ($objectName$) @return
   */
  public int getChildCount()
  {

    if (!isComplete())
    {
      synchronized (m_doc)
      {
        try
        {

          // Here we have to wait until the element is complete
          while (!isComplete())
          {
            m_doc.wait();
            throwIfParseError();
          }
        }
        catch (InterruptedException e)
        {
          throwIfParseError();
        }

        //System.out.println("... getcount " );
      }
    }

    //System.out.println("Waiting... Done "+ this.getNodeName() );          
    return m_childCount;
  }

  /**
   *  This is a convenience method to allow easy determination of whether a
   * node has any children.
   * @return  <code>true</code> if the node has any children,
   *   <code>false</code> if the node has no children.
   */
  public boolean hasChildNodes()
  {

    if (0 != m_childCount)
      return true;
    else
    {
      if (!isComplete())
      {
        synchronized (m_doc)
        {
          try
          {

            // Only wait until the first child comes, or we are complete.
            while (!isComplete())
            {
              m_doc.wait();
              throwIfParseError();

              if (0 != m_childCount)
                break;
            }
          }
          catch (InterruptedException e)
          {
            throwIfParseError();
          }
        }
      }

      return (0 == m_childCount) ? false : true;
    }
  }

  /**
   * <meta name="usage" content="internal"/>
   * Get the position of the child of an element in the document.
   * Note that this is assuming an index starting at 1
   *
   * NEEDSDOC @param pos
   *
   * NEEDSDOC ($objectName$) @return
   */
  public int getChildUID(int pos)
  {

    Child child = getChild(pos);

    return (null != child) ? child.getUid() : -1;
  }

  /**
   * Get the nth child.
   * @param i the index of the child.
   *
   * NEEDSDOC ($objectName$) @return
   * @exception ArrayIndexOutOfBoundsException if the index is out of bounds.
   * @exception NullPointerException if there are no children.
   */
  public Child getChild(int i)
          throws ArrayIndexOutOfBoundsException, NullPointerException
  {

    if (i < 0)
      return null;
    else if ((i >= m_childCount) &&!isComplete())
    {
      synchronized (m_doc)
      {
        try
        {

          // System.out.println("Waiting... getChild " + i + " " + getNodeName());
          while (!isComplete())
          {
            m_doc.wait();
            throwIfParseError();

            if (i < m_childCount)
              break;
          }
        }
        catch (InterruptedException e)
        {
          throwIfParseError();
        }
      }
    }

    if (i < m_childCount)
    {
      Child child = m_first;
      int pos = 0;

      while (null != child)
      {
        if (pos == i)
        {
          return child;
        }

        child = child.m_next;

        pos++;
      }
    }

    return null;
  }

  /**
   * The first child of this node. If there is no such node, this returns
   * <code>null</code>.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Node getFirstChild()
  {

    if (null != m_first)
      return m_first;
    else if (!m_isComplete)
    {
      synchronized (m_doc)
      {
        try
        {

          // System.out.println("Waiting... getChild " + i + " " + getNodeName());
          while (!isComplete())
          {
            m_doc.wait();
            throwIfParseError();

            if (null != m_first)
              return m_first;
          }
        }
        catch (InterruptedException e)
        {
          throwIfParseError();
        }
      }
    }

    return m_first;
  }

  /**
   * The last child of this node. If there is no such node, this returns
   * <code>null</code>.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Node getLastChild()
  {

    try
    {
      return getChild(getChildCount() - 1);
    }
    catch (Exception e)
    {
      throw new org.apache.xalan.utils.WrappedRuntimeException(e);
    }
  }

  /**
   * Append a child to the child list.
   * @param newChild Must be a org.apache.xalan.stree.Child.
   *
   * NEEDSDOC ($objectName$) @return
   * @exception ClassCastException if the newChild isn't a org.apache.xalan.stree.Child.
   *
   * @throws DOMException
   */
  public Node appendChild(Node newChild) throws DOMException
  {

    Child child = (Child) newChild;
    DocumentImpl doc = m_doc;

    child.m_parent = this;

    m_childCount++;

    child.m_uid = ++m_doc.m_docOrderCount;
    child.m_level = (short) (m_level + 1);

    if (null == m_first)
    {
      m_first = child;
    }
    else
    {
      m_last.m_next = child;
      child.m_prev = m_last;
    }

    m_last = child;

    // getDocumentImpl().getLevelIndexer().insertNode(child);
    if (Node.ELEMENT_NODE == child.getNodeType())
    {
      SourceTreeHandler sh = doc.getSourceTreeHandler();

      if ((null != sh) && sh.m_shouldCheckWhitespace)
      {
        TransformerImpl transformer = sh.getTransformerImpl();

        if (null != transformer)
        {
          StylesheetRoot stylesheet = transformer.getStylesheet();

          try
          {
            ElementImpl elem = (ElementImpl) child;
            WhiteSpaceInfo info =
              stylesheet.getWhiteSpaceInfo(transformer.getXPathContext(),
                                           elem);
            boolean shouldStrip;

            if (null == info)
            {
              shouldStrip = sh.getShouldStripWhitespace();
            }
            else
            {
              shouldStrip = info.getShouldStripSpace();
            }

            sh.setShouldStripWhitespace(shouldStrip);
          }
          catch (SAXException se)
          {

            // TODO: Diagnostics
          }
        }
      }
    }

    return newChild;
  }

  /**
   * Return if this node has had all it's children added, i.e.
   * if a endElement event has occured.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public boolean isComplete()
  {

    if (!m_isComplete && (null != m_doc.m_exceptionThrown))
      throwParseError(m_doc.m_exceptionThrown);

    return m_isComplete;
  }

  /**
   * Set that this node's child list is complete, i.e.
   * an endElement event has occured.
   *
   * NEEDSDOC @param isComplete
   */
  public void setComplete(boolean isComplete)
  {
    m_isComplete = isComplete;
  }

  /**
   * NEEDSDOC Method throwParseError 
   *
   *
   * NEEDSDOC @param e
   */
  protected void throwParseError(Exception e)
  {

    m_isComplete = true;

    super.throwParseError(e);
  }
}
