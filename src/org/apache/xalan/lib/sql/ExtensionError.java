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

package org.apache.xalan.lib.sql;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.CDATASection;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.DOMException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xalan.stree.DocumentImpl;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

import java.io.StringWriter;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.apache.xpath.axes.ContextNodeList;

/**
 * <meta name="usage" content="experimental"/>
 * <p>
 * A base class that will convert an exception into an XML stream
 * that can be returned in place of the standard result. The XML
 * format returned is a follows.
 * </p>
 * <pre>
 * <p>
 * &lt;ext-error&gt;
 *  &lt;exception-info&gt;
 *    &lt;message&gt; Message from the Exception thrown &lt;/message&gt;
 *  &lt;exception-info&gt;
 *
 *  If another class subclasses this class, there will be an
 *  opportunity to add specific information here. Each Extension
 *  class should implement their own specific Extension Error
 *  class.
 * </p>
 * </pre>
 *
 */

public class ExtensionError
  implements NodeIterator, ContextNodeList, Cloneable
{
  private static final boolean DEBUG = false;

  private boolean   m_FirstTime = true;
  private Document  m_doc = null;

  public ExtensionError()
  {
  }

  /**
   *
   * <meta name="usage" content="experimental"/>
   *
   * Initialize an error with the base exception and
   * extrace the relavent information.
   *
   */
  public ExtensionError(Exception err)
  {
    processBaseError(err);
    // dump();
  }

  /**
   *  <meta name="usage" content="experimental"/>
   *
   * Process the standard error information and build the
   * base document.
   *
   * Note: this implementation should probably extend UnImplNode and
   * create a light weight representation of the Document instead of
   * using a full Document implementation.
   *
   * Classes that extend this class to extend the error information
   * need to override the populateSpecificData method so that only
   * one control creates the return Document.
   *
   */
  protected void processBaseError(Exception err)
  {
    try
    {
      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = dfactory.newDocumentBuilder();

      m_doc = docBuilder.newDocument();

      Element etmp = null;
      Text text = null;
      CDATASection cdata = null;

      Element root = m_doc.createElement("ext-error");
      m_doc.appendChild(root);


      Element info = m_doc.createElement("exception-info");
      root.appendChild(info);

      etmp = m_doc.createElement("message");
      info.appendChild(etmp);
      text = m_doc.createTextNode(err.getLocalizedMessage());
      etmp.appendChild(text);

      // If we have been extended then give the extension a chance
      // to add their information.
      populateSpecificData(m_doc, root);

    }
    catch(Exception e)
    {
      // e.printStackTrace();

      m_doc = null;
    }
  }

  /**
   * Other classes that extend this class will override this mehood to
   * add any specific information.
   *
   * @param Document - The document that will be returned to the processor
   * @param Node - the <ext-error> Element that can be added to.
   */

  protected void populateSpecificData(Document doc, Node node)
  {
  }

  public Node getRoot()
  {
    return m_doc;
  }

  /**
   *  This attribute determines which node types are presented via the
   * iterator. The available set of constants is defined in the
   * <code>NodeFilter</code> interface.
   *
   * @return which node types are to be presented
   */
  public int getWhatToShow()
  {

    if (DEBUG)
      System.out.println("In ExtensionError.getWhatToShow");

    // TODO: ??
    return NodeFilter.SHOW_ALL & ~NodeFilter.SHOW_ENTITY_REFERENCE;
  }

  /**
   *  The filter used to screen nodes.
   * @return null.
   */
  public NodeFilter getFilter()
  {

    if (DEBUG)
      System.out.println("In ExtensionError.getFilter");

    return null;
  }

  /**
   *  The value of this flag determines whether the children of entity
   * reference nodes are visible to the iterator. If false, they will be
   * skipped over.
   * <br> To produce a view of the document that has entity references
   * expanded and does not expose the entity reference node itself, use the
   * whatToShow flags to hide the entity reference node and set
   * expandEntityReferences to true when creating the iterator. To produce
   * a view of the document that has entity reference nodes but no entity
   * expansion, use the whatToShow flags to show the entity reference node
   * and set expandEntityReferences to false.
   * @return true.
   */
  public boolean getExpandEntityReferences()
  {

    if (DEBUG)
      System.out.println("In ExtensionError.getExpandEntityReferences");

    return true;
  }

  /**
   * Return the #Document node (one role the XStatement plays) the first time called;
   * return null thereafter.
   * @return this or null.
   *
   * @throws DOMException
   */

  public Node nextNode() throws DOMException
  {

    if (DEBUG)
      System.out.println("In Extension Error: next node");

    if (! m_FirstTime) return null;

    m_FirstTime = false;
    return m_doc;
  }

  public Node previousNode()
    throws DOMException
  {
    return null;
  }

  public void detach()
  {
  }


  public Node getCurrentNode()
  {
    return m_doc;
  }

  public int getCurrentPos()
  {
    return 0;
  }

  public void reset()
  {
    m_FirstTime = true;
  }

  public void setShouldCacheNodes(boolean b)
  {
    //TODO: Implement this org.apache.xpath.axes.ContextNodeList method
  }

  public void runTo(int index)
  {
    //TODO: Implement this org.apache.xpath.axes.ContextNodeList method
  }

  public void setCurrentPos(int i)
  {
    //TODO: Implement this org.apache.xpath.axes.ContextNodeList method
  }

  public int size()
  {
    return 1;
  }

  public boolean isFresh()
  {
    return m_FirstTime;
  }

  public NodeIterator cloneWithReset() throws CloneNotSupportedException
  {
    ExtensionError clone = (ExtensionError) super.clone();
    clone.reset();
    return clone;
  }

  public Object clone() throws CloneNotSupportedException
  {
    ExtensionError clone = (ExtensionError) super.clone();
    return clone;
  }

  public int getLast()
  {
    return 0;
  }

  public void setLast(int last)
  {
  }
}