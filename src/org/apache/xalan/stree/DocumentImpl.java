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

import org.apache.xml.utils.FastStringBuffer;

/**
 * <meta name="usage" content="internal"/>
 * This is the implementation of the DOM2 Document 
 * interface.  It rules over the tree, and may contain 
 * common information for the tree.
 */
public class DocumentImpl extends DocImpl
{
  /**
   * Constructor DocumentImpl. This constructor is 
   * not normally used by the transformation.
   */
  DocumentImpl()
  {

    super();
    setDoc(this);
    // m_bUpIndexer = new LevelIndexer();
  }
  
  /**
   * Constructor DocumentImpl
   */
  public DocumentImpl(int charBufSize)
  {
    super(charBufSize);
    setDoc(this);
  }


  /**
   * Constructor DocumentImpl
   *
   * @param sth A reference back to the source tree 
   * handler that is creating this tree. 
   */
  DocumentImpl(SourceTreeHandler sth)
  {

    super();
    setDoc(this);
    // m_bUpIndexer = new LevelIndexer();
    m_sourceTreeHandler = sth;
  }

  /**
   * Constructor DocumentImpl.  This constructor is 
   * not normally used by the transformation.
   *
   * @param doctype The DocumentType reference that 
   * this tree conforms to.
   */
  DocumentImpl(DocumentType doctype)
  {

    super();
    setDoc(this);
    if (null != doctype)
      m_docType = (DocumentTypeImpl) doctype;

    // m_bUpIndexer = new LevelIndexer();
  }

  /**
   * The document type that this tree conforms to.  Is 
   * not normally used, and may well be null.
   */
  DocumentTypeImpl m_docType;

  /**
   * For XML, this provides access to the Document Type Definition.
   * For HTML documents, and XML documents which don't specify a DTD,
   * it will be null.
   *
   * @return The DocumentType reference, which may well be null.
   */
  public DocumentType getDoctype()
  {
    return m_docType;
  }
  
  public void setDoctype(DocumentType docType)
  {
    m_docType = (DocumentTypeImpl)docType;
  }

  /**
   * The document element.
   */
  ElementImpl m_docElement;

  /**
   * Convenience method, allowing direct access to the child node
   * which is considered the root of the actual document content.
   *
   * @return the document element, which may be null if it hasn't 
   * been constructed.
   */
  public Element getDocumentElement()
  {
    return m_docElement;
  }

  /** This table holds the ID string to node associations, for 
   * XML IDs.  */
  Hashtable m_idAttributes = new Hashtable();

  /**
   * Get the table that holds the ID string to node associations, 
   * for looking up XML IDs.
   *
   * @return the ID table, never null.
   */
  public Hashtable getIDAttributes()
  {
    return m_idAttributes;
  }

  /**
   * Set an ID string to node association in the ID table.
   *
   * @param id The ID string.
   * @param elem The associated ID.
   */
  public void setIDAttribute(String id, Element elem)
  {
    m_idAttributes.put(id, elem);
  }

  /**
   * Append a child to the child list.
   * 
   * @param newChild Must be a org.apache.xalan.stree.Child.
   *
   * @return The child that was added.
   * 
   * @throws ClassCastException if the newChild isn't a org.apache.xalan.stree.Child.
   * @throws DOMException
   */
  public Node appendChild(Node newChild) throws DOMException
  {

    if(getNodeType() != Node.DOCUMENT_FRAGMENT_NODE)
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
    }

    return super.appendChild(newChild);
  }

  /**
   * Returns the node type. 
   *
   * @return Node.DOCUMENT_NODE. 
   */
  public short getNodeType()
  {
    return Node.DOCUMENT_NODE;
  }

  /**
   * Returns the node name. 
   *
   * @return "#document" string.
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
   * @return The local name of the node, or null if namespaces are not processed.
   */
  public String getLocalName()
  {
    return "#document";
  }

  /**
   * Create a new Element.
   *
   * @param tagName The name of the element.
   *
   * @return A new element.
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
   * @return a new DocumentFragment reference.
   */
  public DocumentFragment createDocumentFragment()
  {
    return new DocumentFragmentImpl();
  }

  /**
   * Create a Text node. 
   *
   * @param data The character that this node holds.
   *
   * @return A new Text node.
   */
  public Text createTextNode(String data)
  {
    return new TextImpl(this, data);
  }

  /**
   * Create a Comment node. 
   *
   * @param data The comment data that this node holds.
   *
   * @return A new Comment node.
   */
  public Comment createComment(String data)
  {
    return new CommentImpl(this, data);
  }

  /**
   * Create a CDATASection node. 
   *
   * @param data The character data that this node holds.
   *
   * @return A new CDATASection node.
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
   * @param target The name of the PI.
   * @param data The data for this PI.
   *
   * @return A new ProcessingInstruction node.
   *
   * @throws DOMException
   */
  public ProcessingInstruction createProcessingInstruction(
          String target, String data) throws DOMException
  {
    return new ProcessingInstructionImpl(this, target, data);
  }

  /**
   * Unimplemented, since this a read-only DOM. 
   *
   * @param importedNode The node being imported.
   * @param deep Tells if we should also import the subtree.
   *
   * @return A new node of the same type as importedNode.
   *
   * @throws DOMException
   */
  public Node importNode(Node importedNode, boolean deep) throws DOMException
  {
    return super.importNode(importedNode, deep);
  }

  /**
   * Create a new namespaced element. 
   *
   * @param namespaceURI The namespace URI (NEEDTOREVIEW: Handling of "" vs. null. -sb)
   * @param qualifiedName  The  qualified name of the element type to 
   *   instantiate.
   * 
   * @return  A new <code>Element</code> object with the following 
   *   attributes: Attribute Value<code>Node.nodeName</code>
   *   <code>qualifiedName</code><code>Node.namespaceURI</code>
   *   <code>namespaceURI</code><code>Node.prefix</code> prefix, extracted 
   *   from <code>qualifiedName</code> , or <code>null</code> if there is no
   *    prefix<code>Node.localName</code> local name , extracted from 
   *   <code>qualifiedName</code><code>Element.tagName</code>
   *   <code>qualifiedName</code>
   *
   * @throws DOMException
   */
  public Element createElementNS(String namespaceURI, String qualifiedName)
          throws DOMException
  {
    return new ElementImplWithNS(this, namespaceURI, qualifiedName);
  }

  /**
   * Unimplemented right now. 
   *
   * @param namespaceURI  The  namespace URI of the attribute to create.
   * @param qualifiedName  The  qualified name of the attribute to 
   *   instantiate.
   * @return  A new <code>Attr</code> object with the following attributes: 
   *   Attribute Value<code>Node.nodeName</code> qualifiedName
   *   <code>Node.namespaceURI</code><code>namespaceURI</code>
   *   <code>Node.prefix</code> prefix, extracted from 
   *   <code>qualifiedName</code> , or <code>null</code> if there is no 
   *   prefix<code>Node.localName</code> local name , extracted from 
   *   <code>qualifiedName</code><code>Attr.name</code>
   *   <code>qualifiedName</code>
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
   * @param elementId A non-null string that may be a key in the ID table.
   *
   * @return The element associated with the given elementId, or 
   * null if not found.
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
            m_doc.wait(100);
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
   * @return Always null, since there is never a node following a Document node.
   */
  public Node getNextSibling()
  {
    return null;
  }
}
