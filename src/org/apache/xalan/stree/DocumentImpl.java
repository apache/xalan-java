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

import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Text;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Comment;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.DocumentType;
import org.w3c.dom.DOMException;

import java.util.Hashtable;

import org.apache.xalan.utils.FastStringBuffer;

/**
 * <meta name="usage" content="internal"/>
 * NEEDSDOC Class DocumentImpl <needs-comment/>
 */
public class DocumentImpl extends Parent
{

  /** NEEDSDOC Field m_exceptionThrown          */
  public Exception m_exceptionThrown = null;

  /** NEEDSDOC Field m_chars          */
  FastStringBuffer m_chars = new FastStringBuffer(1024 * 64);

  /**
   * Constructor DocumentImpl
   *
   */
  DocumentImpl()
  {

    super(null);

    setDoc(this);

    // m_bUpIndexer = new LevelIndexer();
  }

  /**
   * Constructor DocumentImpl
   *
   *
   * NEEDSDOC @param sth
   */
  DocumentImpl(SourceTreeHandler sth)
  {

    super(null);

    setDoc(this);

    // m_bUpIndexer = new LevelIndexer();
    m_sourceTreeHandler = sth;
  }

  /**
   * Constructor DocumentImpl
   *
   *
   * NEEDSDOC @param doctype
   */
  DocumentImpl(DocumentType doctype)
  {

    super(null);

    setDoc(this);

    if (null != doctype)
      m_docType = (DocumentTypeImpl) doctype;

    // m_bUpIndexer = new LevelIndexer();
  }

  /** NEEDSDOC Field m_sourceTreeHandler          */
  SourceTreeHandler m_sourceTreeHandler;

  /**
   * NEEDSDOC Method getSourceTreeHandler 
   *
   *
   * NEEDSDOC (getSourceTreeHandler) @return
   */
  SourceTreeHandler getSourceTreeHandler()
  {
    return m_sourceTreeHandler;
  }

  /**
   * NEEDSDOC Method setSourceTreeHandler 
   *
   *
   * NEEDSDOC @param h
   */
  void setSourceTreeHandler(SourceTreeHandler h)
  {
    m_sourceTreeHandler = h;
  }

  /** NEEDSDOC Field indexedLookup          */
  private boolean indexedLookup = false;  // for now

  /**
   *
   */

  // private LevelIndexer m_bUpIndexer ;

  /**
   *
   */
  // public LevelIndexer getLevelIndexer()
  // {
  //  return m_bUpIndexer;
  // }
  DocumentTypeImpl m_docType;

  /** NEEDSDOC Field m_docOrderCount          */
  int m_docOrderCount = 1;

  /**
   * Increment the document order count.  Needs to be called
   * when a child is added.
   */
  protected void incrementDocOrderCount()
  {
    m_docOrderCount++;
  }

  /**
   * Get the number of nodes in the tree.  Needs to be called
   * when a child is added.
   *
   * NEEDSDOC ($objectName$) @return
   */
  protected int getDocOrderCount()
  {
    return m_docOrderCount;
  }

  /**
   * For XML, this provides access to the Document Type Definition.
   * For HTML documents, and XML documents which don't specify a DTD,
   * it will be null.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public DocumentType getDoctype()
  {
    return m_docType;
  }

  /** NEEDSDOC Field m_useMultiThreading          */
  boolean m_useMultiThreading = false;

  /**
   * Set whether or not the tree being built should handle
   * transformation while the parse is still going on.
   *
   * NEEDSDOC @param b
   */
  public void setUseMultiThreading(boolean b)
  {
    m_useMultiThreading = b;
  }

  /**
   * Tell whether or not the tree being built should handle
   * transformation while the parse is still going on.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public boolean getUseMultiThreading()
  {
    return m_useMultiThreading;
  }

  /**
   * The document element.
   */
  ElementImpl m_docElement;

  /**
   * Convenience method, allowing direct access to the child node
   * which is considered the root of the actual document content.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Element getDocumentElement()
  {
    return m_docElement;
  }

  /** NEEDSDOC Field m_idAttributes          */
  Hashtable m_idAttributes = new Hashtable();

  /**
   * NEEDSDOC Method getIDAttributes 
   *
   *
   * NEEDSDOC (getIDAttributes) @return
   */
  public Hashtable getIDAttributes()
  {
    return m_idAttributes;
  }

  /**
   * NEEDSDOC Method setIDAttribute 
   *
   *
   * NEEDSDOC @param namespaceURI
   * NEEDSDOC @param qualifiedName
   * NEEDSDOC @param value
   * NEEDSDOC @param elem
   */
  public void setIDAttribute(String namespaceURI, String qualifiedName,
                             String value, Element elem)
  {
    m_idAttributes.put(value, elem);
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

    short type = newChild.getNodeType();

    if (type == Node.ELEMENT_NODE)
    {
      if (null != m_docElement)
        throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR,
                               "DOM006 Hierarchy request error");

      m_docElement = (ElementImpl) newChild;
    }
    else if (type == Node.DOCUMENT_TYPE_NODE)
    {
      m_docType = (DocumentTypeImpl) newChild;
    }

    return super.appendChild(newChild);
  }

  /**
   * Returns the node type. 
   *
   * NEEDSDOC ($objectName$) @return
   */
  public short getNodeType()
  {
    return Node.DOCUMENT_NODE;
  }

  /**
   * Returns the node name. 
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getNodeName()
  {
    return "#document";
  }

  /**
   * Returns the local part of the qualified name of this node.
   * <br>For nodes created with a DOM Level 1 method, such as
   * <code>createElement</code> from the <code>Document</code> interface,
   * it is <code>null</code>.
   * @since DOM Level 2
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getLocalName()
  {
    return "#document";
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param tagName
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws DOMException
   */
  public Element createElement(String tagName) throws DOMException
  {
    return new ElementImpl(this, tagName);
  }

  /**
   * Create a DocumentFragment. 
   *
   * NEEDSDOC ($objectName$) @return
   */
  public DocumentFragment createDocumentFragment()
  {
    return new DocumentFragmentImpl();
  }

  /**
   * Create a Text node. 
   *
   * NEEDSDOC @param data
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Text createTextNode(String data)
  {
    return new TextImpl(this, data);
  }

  /**
   * Create a Comment node. 
   *
   * NEEDSDOC @param data
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Comment createComment(String data)
  {
    return new CommentImpl(this, data);
  }

  /**
   * Create a CDATASection node. 
   *
   * NEEDSDOC @param data
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws DOMException
   */
  public CDATASection createCDATASection(String data) throws DOMException
  {
    return new CDATASectionImpl(this, data);
  }

  /**
   * Create a ProcessingInstruction node. 
   *
   * NEEDSDOC @param target
   * NEEDSDOC @param data
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws DOMException
   */
  public ProcessingInstruction createProcessingInstruction(
          String target, String data) throws DOMException
  {
    return new ProcessingInstructionImpl(this, target, data);
  }

  /**
   * Unimplemented right now, but I should probably implement. 
   *
   * NEEDSDOC @param importedNode
   * NEEDSDOC @param deep
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws DOMException
   */
  public Node importNode(Node importedNode, boolean deep) throws DOMException
  {
    return super.importNode(importedNode, deep);
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param namespaceURI
   * NEEDSDOC @param qualifiedName
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws DOMException
   */
  public Element createElementNS(String namespaceURI, String qualifiedName)
          throws DOMException
  {
    return new ElementImplWithNS(this, namespaceURI, qualifiedName);
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param namespaceURI
   * NEEDSDOC @param qualifiedName
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws DOMException
   */
  public Attr createAttributeNS(String namespaceURI, String qualifiedName)
          throws DOMException
  {
    return super.createAttributeNS(namespaceURI, qualifiedName);
  }

  /**
   * Given an ID, return the element.
   *
   * NEEDSDOC @param elementId
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Element getElementById(String elementId)
  {

    Element elem = (Element) m_idAttributes.get(elementId);

    // Make sure we're done parsing.
    if (elem == null &&!isComplete())
    {
      synchronized (m_doc)
      {
        try
        {

          // Don't really know why we should need the while loop,
          // but we seem to come out of wait() too soon! 
          while (!isComplete())
          {
            m_doc.wait();
            throwIfParseError();

            elem = (Element) m_idAttributes.get(elementId);

            if (null != elem)
              return elem;
          }
        }
        catch (InterruptedException e)
        {
          throwIfParseError();
        }
      }

      elem = (Element) m_idAttributes.get(elementId);
    }

    return elem;
  }

  /**
   * The node immediately following this node. If there is no such node,
   * this returns <code>null</code>.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Node getNextSibling()
  {
    return null;
  }
}
