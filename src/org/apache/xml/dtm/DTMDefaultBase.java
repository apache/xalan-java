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
package org.apache.xml.dtm;

import org.apache.xml.dtm.*;
import org.apache.xml.utils.IntVector;
import org.apache.xml.utils.IntStack;
import org.apache.xml.utils.BoolStack;
import org.apache.xml.utils.StringBufferPool;
import org.apache.xml.utils.FastStringBuffer;
import org.apache.xml.utils.TreeWalker;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.XMLCharacterRecognizer;

import java.util.Vector;

import org.xml.sax.ContentHandler;

import org.apache.xml.utils.NodeVector;

import javax.xml.transform.Source;

import org.apache.xml.utils.XMLString;
import org.apache.xml.utils.XMLStringFactory;

/**
 * The <code>DTMDefaultBase</code> class serves as a helper base for DTMs.
 * It sets up structures for navigation and type, while leaving data
 * management and construction to the derived classes.
 */
public abstract class DTMDefaultBase implements DTM
{

  /**
   * This is extra information about the node objects.  Each information
   * block is composed of several array members.  The size of this will always
   * be (m_nodes.size() * NODEINFOBLOCKSIZE).
   */
  protected IntVector m_info = new IntVector();

  // Offsets into each set of integers in the <code>m_info</code> table.

  /** %TBD% Doc */
  protected static final int OFFSET_EXPANDEDNAMEID = 0;

  /** %TBD% Doc */
  protected static final int OFFSET_TYPE = 1;

  /** %TBD% Doc */
  protected static final int OFFSET_LEVEL = 2;

  /** %TBD% Doc */
  protected static final int OFFSET_FIRSTCHILD = 3;

  /** %TBD% Doc */
  protected static final int OFFSET_NEXTSIBLING = 4;

  /** %TBD% Doc */
  protected static final int OFFSET_PREVSIBLING = 5;

  /** %TBD% Doc */
  protected static final int OFFSET_PARENT = 6;

  /**
   * This represents the number of integers per node in the
   * <code>m_info</code> member variable, if the derived class
   * does not add information.
   */
  protected static final int DEFAULTNODEINFOBLOCKSIZE = 7;

  /**
   * The value to use when the information has not been built yet.
   */
  protected static final int NOTPROCESSED = DTM.NULL - 1;

  /** Not sure if this is used? */
  protected static final int NODEIDENTITYBITS = 0x000FFFFF;

  /**
   * The DTM manager who "owns" this DTM.
   */
  protected DTMManager m_mgr;

  /** %TBD% Doc */
  protected int m_dtmIdent;

  /** %TBD% Doc */
  protected int m_mask;

  /** %TBD% Doc */
  protected String m_documentBaseURI;

  /**
   * The whitespace filter that enables elements to strip whitespace or not.
   */
  protected DTMWSFilter m_wsfilter;

  /** Flag indicating whether to strip whitespace nodes */
  protected boolean m_shouldStripWS = false;

  /** Stack of flags indicating whether to strip whitespace nodes */
  protected BoolStack m_shouldStripWhitespaceStack;
  
  /** The XMLString factory for creating XMLStrings. */
  protected XMLStringFactory m_xstrf;

  /**
   * Construct a DTMDefaultBase object from a DOM node.
   *
   * @param mgr The DTMManager who owns this DTM.
   * @param domSource the DOM source that this DTM will wrap.
   * NEEDSDOC @param source
   * @param dtmIdentity The DTM identity ID for this DTM.
   * @param whiteSpaceFilter The white space filter for this DTM, which may
   *                         be null.
   */
  public DTMDefaultBase(DTMManager mgr, Source source, int dtmIdentity,
                        DTMWSFilter whiteSpaceFilter,
                        XMLStringFactory xstringfactory)
  {

    m_mgr = mgr;
    m_documentBaseURI = (null != source) ? source.getSystemId() : null;
    m_dtmIdent = dtmIdentity;
    m_mask = mgr.getNodeIdentityMask();
    m_wsfilter = whiteSpaceFilter;
    m_xstrf = xstringfactory;

    if (null != whiteSpaceFilter)
    {
      m_shouldStripWhitespaceStack = new BoolStack();

      pushShouldStripWhitespace(false);
    }
  }

  /**
   * Get the next node identity value in the list, and call the iterator
   * if it hasn't been added yet.
   *
   * @param identity The node identity (index).
   * @return identity+1, or DTM.NULL.
   */
  protected abstract int getNextNodeIdentity(int identity);

  /**
   * This method should try and build one or more nodes in the table.
   *
   * @return The true if a next node is found or false if
   *         there are no more nodes.
   */
  protected abstract boolean nextNode();

  /**
   * Return the number of integers in each node info block.
   *
   * NEEDSDOC ($objectName$) @return
   */
  protected int getNodeInfoBlockSize()
  {
    return DEFAULTNODEINFOBLOCKSIZE;
  }

  /**
   * Get the number of nodes that have been added.
   *
   * NEEDSDOC ($objectName$) @return
   */
  protected abstract int getNumberOfNodes();

  /**
   * Get a node handle that is relative to the given node.
   *
   * @param identity The node identity.
   * @param offsetValue One of OFFSET_XXX values.
   * @return The relative node handle.
   */
  protected int getNodeInfo(int identity, int offsetValue)
  {

    int base = (identity * getNodeInfoBlockSize());
    int info = (identity >= getNumberOfNodes())
               ? NOTPROCESSED : m_info.elementAt(base + offsetValue);

    // Check to see if the information requested has been processed, and, 
    // if not, advance the iterator until we the information has been 
    // processed.
    while (info == NOTPROCESSED)
    {
      boolean isMore = nextNode();

      if (identity >= getNumberOfNodes())
      {
        info = NOTPROCESSED;
      }
      else
        info = m_info.elementAt(base + offsetValue);

      if (!isMore && NOTPROCESSED == info)
        return DTM.NULL;
    }

    return info;
  }

  /**
   * Get a node handle that is relative to the given node, but don't check for
   * the NOTPROCESSED flag.  A class will need to use this call to get values
   * that may be negative.  Also, it's a tiny bit faster than getNodeInfo if
   * you know that NOTPROCESSED is not a possibility.
   *
   * @param identity The node identity.
   * @param offsetValue One of OFFSET_XXX values.
   * @return The relative node handle.
   */
  protected int getNodeInfoNoWait(int identity, int offsetValue)
  {
    return m_info.elementAt((identity * getNodeInfoBlockSize())
                            + offsetValue);
  }

  /**
   * Diagnostics function to dump the DTM.
   */
  public void dumpDTM()
  {

    while (nextNode()){}

    int sizePerRecord = getNodeInfoBlockSize();
    int nRecords = m_info.size() / sizePerRecord;

    System.out.println("Total nodes: " + nRecords);

    for (int i = 0; i < nRecords; i++)
    {
      int offset = i * sizePerRecord;

      System.out.println("=========== " + i + " ===========");
      System.out.println("NodeName: " + getNodeName(i));
      System.out.println("NodeNameX: " + getNodeNameX(i));
      System.out.println("LocalName: " + getLocalName(i));
      System.out.println("NamespaceURI: " + getNamespaceURI(i));
      System.out.println("Prefix: " + getPrefix(i));

      int exTypeID = getExpandedNameID(i);

      System.out.println("Expanded Type ID: "
                         + Integer.toHexString(exTypeID));

      int type = getNodeType(i);
      String typestring;

      switch (type)
      {
      case DTM.ATTRIBUTE_NODE :
        typestring = "ATTRIBUTE_NODE";
        break;
      case DTM.CDATA_SECTION_NODE :
        typestring = "CDATA_SECTION_NODE";
        break;
      case DTM.COMMENT_NODE :
        typestring = "COMMENT_NODE";
        break;
      case DTM.DOCUMENT_FRAGMENT_NODE :
        typestring = "DOCUMENT_FRAGMENT_NODE";
        break;
      case DTM.DOCUMENT_NODE :
        typestring = "DOCUMENT_NODE";
        break;
      case DTM.DOCUMENT_TYPE_NODE :
        typestring = "DOCUMENT_NODE";
        break;
      case DTM.ELEMENT_NODE :
        typestring = "ELEMENT_NODE";
        break;
      case DTM.ENTITY_NODE :
        typestring = "ENTITY_NODE";
        break;
      case DTM.ENTITY_REFERENCE_NODE :
        typestring = "ENTITY_REFERENCE_NODE";
        break;
      case DTM.NAMESPACE_NODE :
        typestring = "NAMESPACE_NODE";
        break;
      case DTM.NOTATION_NODE :
        typestring = "NOTATION_NODE";
        break;
      case DTM.NULL :
        typestring = "NULL";
        break;
      case DTM.PROCESSING_INSTRUCTION_NODE :
        typestring = "PROCESSING_INSTRUCTION_NODE";
        break;
      case DTM.TEXT_NODE :
        typestring = "TEXT_NODE";
        break;
      default :
        typestring = "Unknown!";
        break;
      }

      System.out.println("Type: " + typestring);

      int firstChild = m_info.elementAt(offset + OFFSET_FIRSTCHILD);

      if (DTM.NULL == firstChild)
        System.out.println("First child: DTM.NULL");
      else if (NOTPROCESSED == firstChild)
        System.out.println("First child: NOTPROCESSED");
      else
        System.out.println("First child: " + firstChild);

      int prevSibling = m_info.elementAt(offset + OFFSET_PREVSIBLING);

      if (DTM.NULL == prevSibling)
        System.out.println("Prev sibling: DTM.NULL");
      else if (NOTPROCESSED == prevSibling)
        System.out.println("Prev sibling: NOTPROCESSED");
      else
        System.out.println("Prev sibling: " + prevSibling);

      int nextSibling = m_info.elementAt(offset + OFFSET_NEXTSIBLING);

      if (DTM.NULL == nextSibling)
        System.out.println("Next sibling: DTM.NULL");
      else if (NOTPROCESSED == nextSibling)
        System.out.println("Next sibling: NOTPROCESSED");
      else
        System.out.println("Next sibling: " + nextSibling);

      int parent = m_info.elementAt(offset + OFFSET_PARENT);

      if (DTM.NULL == parent)
        System.out.println("Parent: DTM.NULL");
      else if (NOTPROCESSED == parent)
        System.out.println("Parent: NOTPROCESSED");
      else
        System.out.println("Parent: " + parent);

      int level = m_info.elementAt(offset + OFFSET_LEVEL);

      System.out.println("Level: " + level);
      System.out.println("Node Value: " + getNodeValue(i));
      System.out.println("String Value: " + getStringValue(i));
    }
  }

  // ========= DTM Implementation Control Functions. ==============

  /**
   * Set an implementation dependent feature.
   * <p>
   * %REVIEW% Do we really expect to set features on DTMs?
   *
   * @param featureId A feature URL.
   * @param state true if this feature should be on, false otherwise.
   */
  public void setFeature(String featureId, boolean state){}

  // ========= Document Navigation Functions =========

  /**
   * Given a node handle, test if it has child nodes.
   * <p> %REVIEW% This is obviously useful at the DOM layer, where it
   * would permit testing this without having to create a proxy
   * node. It's less useful in the DTM API, where
   * (dtm.getFirstChild(nodeHandle)!=DTM.NULL) is just as fast and
   * almost as self-evident. But it's a convenience, and eases porting
   * of DOM code to DTM.  </p>
   *
   * @param nodeHandle int Handle of the node.
   * @return int true if the given node has child nodes.
   */
  public boolean hasChildNodes(int nodeHandle)
  {

    int identity = nodeHandle & m_mask;
    int firstChild = getNodeInfo(identity, OFFSET_FIRSTCHILD);

    return firstChild != DTM.NULL;
  }

  /**
   * Given a node handle, get the handle of the node's first child.
   * If not yet resolved, waits for more nodes to be added to the document and
   * tries again.
   *
   * @param nodeHandle int Handle of the node.
   * @return int DTM node-number of first child, or DTM.NULL to indicate none exists.
   */
  public int getFirstChild(int nodeHandle)
  {

    int identity = nodeHandle & m_mask;
    int firstChild = getNodeInfo(identity, OFFSET_FIRSTCHILD);

    return firstChild | m_dtmIdent;
  }

  /**
   * Given a node handle, advance to its last child.
   * If not yet resolved, waits for more nodes to be added to the document and
   * tries again.
   *
   * @param nodeHandle int Handle of the node.
   * @return int Node-number of last child,
   * or DTM.NULL to indicate none exists.
   */
  public int getLastChild(int nodeHandle)
  {

    int identity = nodeHandle & m_mask;
    int child = getNodeInfo(identity, OFFSET_FIRSTCHILD);
    int lastChild = DTM.NULL;

    while (child != DTM.NULL)
    {
      lastChild = child;
      child = getNodeInfo(child, OFFSET_NEXTSIBLING);
    }

    return lastChild | m_dtmIdent;
  }

  /**
   * Retrieves an attribute node by by qualified name and namespace URI.
   *
   * @param nodeHandle int Handle of the node upon which to look up this attribute..
   * @param namespaceURI The namespace URI of the attribute to
   *   retrieve, or null.
   * @param name The local name of the attribute to
   *   retrieve.
   * @return The attribute node handle with the specified name (
   *   <code>nodeName</code>) or <code>DTM.NULL</code> if there is no such
   *   attribute.
   */
  public abstract int getAttributeNode(int nodeHandle, String namespaceURI,
                                       String name);

  /**
   * Given a node handle, get the index of the node's first attribute.
   *
   * @param nodeHandle int Handle of the node.
   * @return Handle of first attribute, or DTM.NULL to indicate none exists.
   */
  public int getFirstAttribute(int nodeHandle)
  {

    int type = getNodeType(nodeHandle);

    if (DTM.ELEMENT_NODE == type)
    {

      // Assume that attributes and namespaces immediately follow the element.
      int identity = nodeHandle & m_mask;

      while (DTM.NULL != (identity = getNextNodeIdentity(identity)))
      {

        // Assume this can not be null.
        type = getNodeType(identity);

        if (type == DTM.ATTRIBUTE_NODE)
        {
          return identity | m_dtmIdent;
        }
        else if (DTM.NAMESPACE_NODE != type)
        {
          break;
        }
      }
    }

    return DTM.NULL;
  }

  /**
   * Given a node handle, advance to its next sibling.
   * If not yet resolved, waits for more nodes to be added to the document and
   * tries again.
   * @param nodeHandle int Handle of the node.
   * @return int Node-number of next sibling,
   * or DTM.NULL to indicate none exists.
   */
  public int getNextSibling(int nodeHandle)
  {

    int identity = nodeHandle & m_mask;
    int nextSibling = getNodeInfo(identity, OFFSET_NEXTSIBLING);

    return nextSibling | m_dtmIdent;
  }

  /**
   * Given a node handle, find its preceeding sibling.
   * WARNING: DTM is asymmetric; this operation is resolved by search, and is
   * relatively expensive.
   *
   * @param nodeHandle the id of the node.
   * @return int Node-number of the previous sib,
   * or DTM.NULL to indicate none exists.
   */
  public int getPreviousSibling(int nodeHandle)
  {

    int firstChild = getNodeInfo(nodeHandle & m_mask, OFFSET_PREVSIBLING);

    return firstChild | m_dtmIdent;
  }

  /**
   * Given a node handle, advance to the next attribute. If an
   * element, we advance to its first attribute; if an attr, we advance to
   * the next attr on the same node.
   *
   * @param nodeHandle int Handle of the node.
   * @return int DTM node-number of the resolved attr,
   * or DTM.NULL to indicate none exists.
   */
  public int getNextAttribute(int nodeHandle)
  {

    int type = getNodeType(nodeHandle);

    if (DTM.ATTRIBUTE_NODE == type)
    {

      // Assume that attributes and namespace nodes immediately follow the element.
      int identity = nodeHandle & m_mask;

      while (DTM.NULL != (identity = getNextNodeIdentity(identity)))
      {
        type = getNodeType(identity);

        if (type == DTM.ATTRIBUTE_NODE)
        {
          return identity | m_dtmIdent;
        }
        else if (type != DTM.NAMESPACE_NODE)
        {
          break;
        }
      }
    }

    return DTM.NULL;
  }

  /** NEEDSDOC Field m_namespaceLists */
  private Vector m_namespaceLists = null;  // on demand

  /**
   * NEEDSDOC Method getNamespaceList
   *
   *
   * NEEDSDOC @param baseHandle
   *
   * NEEDSDOC (getNamespaceList) @return
   */
  protected NodeVector getNamespaceList(int baseHandle)
  {

    if (null == m_namespaceLists)
      m_namespaceLists = new Vector();
    else
    {
      int n = m_namespaceLists.size();

      for (int i = (n - 1); i >= 0; i--)
      {
        NodeVector ivec = (NodeVector) m_namespaceLists.elementAt(i);

        if (ivec.elementAt(0) == baseHandle)
          return ivec;
      }
    }

    NodeVector ivec = buildNamespaceList(baseHandle);

    m_namespaceLists.addElement(ivec);

    return ivec;
  }

  /**
   * NEEDSDOC Method buildNamespaceList
   *
   *
   * NEEDSDOC @param baseHandle
   *
   * NEEDSDOC (buildNamespaceList) @return
   */
  private NodeVector buildNamespaceList(int baseHandle)
  {

    NodeVector ivec = new NodeVector(7);

    ivec.addElement(-1);  // for base handle.

    int nodeHandle = baseHandle;
    int type = getNodeType(baseHandle);
    int namespaceHandle = DTM.NULL;

    if (DTM.ELEMENT_NODE == type)
    {

      // We have to return in document order, so we actually want to find the 
      // first namespace decl of the last element that has a namespace decl.
      // Assume that attributes and namespaces immediately follow the element.
      int identity = nodeHandle & m_mask;

      while (DTM.NULL != identity)
      {
        identity = getNextNodeIdentity(identity);
        type = (DTM.NULL == identity) ? -1 : getNodeType(identity);

        if (type == DTM.NAMESPACE_NODE)
        {
          namespaceHandle = identity | m_dtmIdent;

          ivec.insertInOrder(namespaceHandle);
        }
        else if (DTM.ATTRIBUTE_NODE != type)
        {
          if (identity > 0)
          {
            nodeHandle = getParent(nodeHandle);

            if (nodeHandle == DTM.NULL)
              break;

            identity = nodeHandle & m_mask;

            if (identity == 0)
              break;
          }
          else
            break;
        }
      }
    }

    ivec.setElementAt(baseHandle, 0);

    return ivec;
  }

  /**
   * Given a node handle, get the index of the node's first child.
   * If not yet resolved, waits for more nodes to be added to the document and
   * tries again
   *
   * @param nodeHandle handle to node, which should probably be an element
   *                   node, but need not be.
   *
   * @param inScope    true if all namespaces in scope should be returned,
   *                   false if only the namespace declarations should be
   *                   returned.
   * @return handle of first namespace, or DTM.NULL to indicate none exists.
   */
  public int getFirstNamespaceNode(int nodeHandle, boolean inScope)
  {

    int type = getNodeType(nodeHandle);

    if (DTM.ELEMENT_NODE == type)
    {
      if (inScope)
      {
        NodeVector namespaces = getNamespaceList(nodeHandle);
        int n = namespaces.size();

        if (n > 1)
          return namespaces.elementAt(1);
      }
      else
      {

        // Assume that attributes and namespaces immediately follow the element.
        int identity = nodeHandle & m_mask;

        while (DTM.NULL != (identity = getNextNodeIdentity(identity)))
        {

          // Assume this can not be null.
          type = getNodeType(identity);

          if (type == DTM.NAMESPACE_NODE)
          {
            return identity | m_dtmIdent;
          }
          else if (DTM.ATTRIBUTE_NODE != type)
          {
            break;
          }
        }
      }
    }

    return DTM.NULL;
  }

  /**
   * Given a namespace handle, advance to the next namespace.
   *
   * @param baseHandle handle to original node from where the first namespace
   * was relative to (needed to return nodes in document order).
   * @param namespaceHandle handle to node which must be of type
   * NAMESPACE_NODE.
   * NEEDSDOC @param nodeHandle
   * NEEDSDOC @param inScope
   * @return handle of next namespace, or DTM.NULL to indicate none exists.
   */
  public int getNextNamespaceNode(int baseHandle, int nodeHandle,
                                  boolean inScope)
  {

    int type = getNodeType(nodeHandle);

    if (DTM.NAMESPACE_NODE == type)
    {
      if (inScope)
      {
        NodeVector namespaces = getNamespaceList(baseHandle);
        int n = namespaces.size();

        for (int i = 1; i < n; i++)  // start from 1 on purpose 
        {
          if (nodeHandle == namespaces.elementAt(i))
          {
            if (i + 1 < n)
              return namespaces.elementAt(i + 1);
          }
        }
      }
      else
      {

        // Assume that attributes and namespace nodes immediately follow the element.
        int identity = nodeHandle & m_mask;

        while (DTM.NULL != (identity = getNextNodeIdentity(identity)))
        {
          type = getNodeType(identity);

          if (type == DTM.NAMESPACE_NODE)
          {
            return identity | m_dtmIdent;
          }
          else if (type != DTM.ATTRIBUTE_NODE)
          {
            break;
          }
        }
      }
    }

    return DTM.NULL;
  }

  /**
   * Given a node handle, find its parent node.
   *
   * @param nodeHandle the id of the node.
   * @return int Node-number of parent,
   * or DTM.NULL to indicate none exists.
   */
  public int getParent(int nodeHandle)
  {

    int identity = nodeHandle & m_mask;

    if (identity > 0)
    {
      int parent = getNodeInfo(identity, OFFSET_PARENT);

      return parent | m_dtmIdent;
    }
    else
      return DTM.NULL;
  }

  /**
   *  Given a node handle, find the owning document node.
   *
   *  @param nodeHandle the id of the node.
   *  @return int Node handle of document, which should always be valid.
   */
  public int getDocument()
  {
    return m_dtmIdent;
  }

  /**
   * Given a node handle, find the owning document node.  This has the exact
   * same semantics as the DOM Document method of the same name, in that if
   * the nodeHandle is a document node, it will return NULL.
   *
   * <p>%REVIEW% Since this is DOM-specific, it may belong at the DOM
   * binding layer. Included here as a convenience function and to
   * aid porting of DOM code to DTM.</p>
   *
   * @param nodeHandle the id of the node.
   * @return int Node handle of owning document, or -1 if the nodeHandle is
   *             a document.
   */
  public int getOwnerDocument(int nodeHandle)
  {

    int type = getNodeType(nodeHandle);

    if (DTM.DOCUMENT_NODE == type)
    {
      return DTM.NULL;
    }

    return getDocument();
  }

  /**
   * Get the string-value of a node as a String object
   * (see http://www.w3.org/TR/xpath#data-model
   * for the definition of a node's string-value).
   *
   * @param nodeHandle The node ID.
   *
   * @return A string object that represents the string-value of the given node.
   */
  public abstract XMLString getStringValue(int nodeHandle);

  /**
   * Get number of character array chunks in
   * the string-value of a node.
   * (see http://www.w3.org/TR/xpath#data-model
   * for the definition of a node's string-value).
   * Note that a single text node may have multiple text chunks.
   *
   * @param nodeHandle The node ID.
   *
   * @return number of character array chunks in
   *         the string-value of a node.
   */
  public int getStringValueChunkCount(int nodeHandle)
  {

    // %TBD%
    error("getStringValueChunkCount not yet supported!");

    return 0;
  }

  /**
   * Get a character array chunk in the string-value of a node.
   * (see http://www.w3.org/TR/xpath#data-model
   * for the definition of a node's string-value).
   * Note that a single text node may have multiple text chunks.
   *
   * @param nodeHandle The node ID.
   * @param chunkIndex Which chunk to get.
   * @param startAndLen An array of 2 where the start position and length of
   *                    the chunk will be returned.
   *
   * @return The character array reference where the chunk occurs.
   */
  public char[] getStringValueChunk(int nodeHandle, int chunkIndex,
                                    int[] startAndLen)
  {

    // %TBD%
    error("getStringValueChunk not yet supported!");

    return null;
  }

  /**
   * Given a node handle, return an ID that represents the node's expanded name.
   *
   * @param nodeHandle The handle to the node in question.
   *
   * @return the expanded-name id of the node.
   */
  public int getExpandedNameID(int nodeHandle)
  {

    int identity = nodeHandle & m_mask;
    int expandedNameID = getNodeInfo(identity, OFFSET_EXPANDEDNAMEID);

    return expandedNameID;
  }

  /**
   * Given an expanded name, return an ID.  If the expanded-name does not
   * exist in the internal tables, the entry will be created, and the ID will
   * be returned.  Any additional nodes that are created that have this
   * expanded name will use this ID.
   *
   * @param nodeHandle The handle to the node in question.
   * NEEDSDOC @param type
   *
   * NEEDSDOC @param namespace
   * NEEDSDOC @param localName
   *
   * @return the expanded-name id of the node.
   */
  public int getExpandedNameID(String namespace, String localName, int type)
  {

    ExpandedNameTable ent = m_mgr.getExpandedNameTable(this);

    return ent.getExpandedNameID(namespace, localName, type);
  }

  /**
   * Given an expanded-name ID, return the local name part.
   *
   * @param ExpandedNameID an ID that represents an expanded-name.
   * @return String Local name of this node.
   */
  public String getLocalNameFromExpandedNameID(int ExpandedNameID)
  {

    ExpandedNameTable ent = m_mgr.getExpandedNameTable(this);

    return ent.getLocalName(ExpandedNameID);
  }

  /**
   * Given an expanded-name ID, return the namespace URI part.
   *
   * @param ExpandedNameID an ID that represents an expanded-name.
   * @return String URI value of this node's namespace, or null if no
   * namespace was resolved.
   */
  public String getNamespaceFromExpandedNameID(int ExpandedNameID)
  {

    ExpandedNameTable ent = m_mgr.getExpandedNameTable(this);

    return ent.getNamespace(ExpandedNameID);
  }

  /**
   * Given a node handle, return its DOM-style node name. This will
   * include names such as #text or #document.
   *
   * @param nodeHandle the id of the node.
   * @return String Name of this node, which may be an empty string.
   * %REVIEW% Document when empty string is possible...
   * %REVIEW-COMMENT% It should never be empty, should it?
   */
  public abstract String getNodeName(int nodeHandle);

  /**
   * Given a node handle, return the XPath node name.  This should be
   * the name as described by the XPath data model, NOT the DOM-style
   * name.
   *
   * @param nodeHandle the id of the node.
   * @return String Name of this node, which may be an empty string.
   */
  public String getNodeNameX(int nodeHandle)
  {

    /** @todo: implement this org.apache.xml.dtm.DTMDefaultBase abstract method */
    error("Not yet supported!");

    return null;
  }

  /**
   * Given a node handle, return its XPath-style localname.
   * (As defined in Namespaces, this is the portion of the name after any
   * colon character).
   *
   * @param nodeHandle the id of the node.
   * @return String Local name of this node.
   */
  public abstract String getLocalName(int nodeHandle);

  /**
   * Given a namespace handle, return the prefix that the namespace decl is
   * mapping.
   * Given a node handle, return the prefix used to map to the namespace.
   *
   * <p> %REVIEW% Are you sure you want "" for no prefix?  </p>
   * <p> %REVIEW-COMMENT% I think so... not totally sure. -sb  </p>
   *
   * @param nodeHandle the id of the node.
   * @return String prefix of this node's name, or "" if no explicit
   * namespace prefix was given.
   */
  public abstract String getPrefix(int nodeHandle);

  /**
   * Given a node handle, return its DOM-style namespace URI
   * (As defined in Namespaces, this is the declared URI which this node's
   * prefix -- or default in lieu thereof -- was mapped to.)
   *
   * <p>%REVIEW% Null or ""? -sb</p>
   *
   * @param nodeHandle the id of the node.
   * @return String URI value of this node's namespace, or null if no
   * namespace was resolved.
   */
  public abstract String getNamespaceURI(int nodeHandle);

  /**
   * Given a node handle, return its node value. This is mostly
   * as defined by the DOM, but may ignore some conveniences.
   * <p>
   *
   * @param nodeHandle The node id.
   * @return String Value of this node, or null if not
   * meaningful for this node type.
   */
  public abstract String getNodeValue(int nodeHandle);

  /**
   * Given a node handle, return its DOM-style node type.
   * <p>
   * %REVIEW% Generally, returning short is false economy. Return int?
   *
   * @param nodeHandle The node id.
   * @return int Node type, as per the DOM's Node._NODE constants.
   */
  public short getNodeType(int nodeHandle)
  {

    int identity = nodeHandle & m_mask;
    short type = (short) getNodeInfo(identity, OFFSET_TYPE);

    return type;
  }

  /**
   * <meta name="usage" content="internal"/>
   * Get the depth level of this node in the tree (equals 1 for
   * a parentless node).
   *
   * @param nodeHandle The node id.
   * @return the number of ancestors, plus one
   */
  public short getLevel(int nodeHandle)
  {

    int identity = nodeHandle & m_mask;

    // Apparently, the axis walker stuff requires levels to count from 1.
    return (short) (getNodeInfo(identity, OFFSET_LEVEL) + 1);
  }

  // ============== Document query functions ============== 

  /**
   * Tests whether DTM DOM implementation implements a specific feature and
   * that feature is supported by this node.
   *
   * @param feature The name of the feature to test.
   * @param versionThis is the version number of the feature to test.
   *   If the version is not
   *   specified, supporting any version of the feature will cause the
   *   method to return <code>true</code>.
   * NEEDSDOC @param version
   * @return Returns <code>true</code> if the specified feature is
   *   supported on this node, <code>false</code> otherwise.
   */
  public boolean isSupported(String feature, String version)
  {

    // %TBD%
    return false;
  }

  /**
   * Return the base URI of the document entity. If it is not known
   * (because the document was parsed from a socket connection or from
   * standard input, for example), the value of this property is unknown.
   *
   * @return the document base URI String object or null if unknown.
   */
  public String getDocumentBaseURI()
  {
    return m_documentBaseURI;
  }

  /**
   * Set the base URI of the document entity.
   *
   * @param baseURI the document base URI String object or null if unknown.
   */
  public void setDocumentBaseURI(String baseURI)
  {
    m_documentBaseURI = baseURI;
  }

  /**
   * Return the system identifier of the document entity. If
   * it is not known, the value of this property is unknown.
   *
   * @param nodeHandle The node id, which can be any valid node handle.
   * @return the system identifier String object or null if unknown.
   */
  public String getDocumentSystemIdentifier(int nodeHandle)
  {

    // %REVIEW%  OK? -sb
    return m_documentBaseURI;
  }

  /**
   * Return the name of the character encoding scheme
   *        in which the document entity is expressed.
   *
   * @param nodeHandle The node id, which can be any valid node handle.
   * @return the document encoding String object.
   */
  public String getDocumentEncoding(int nodeHandle)
  {

    // %REVIEW%  OK??  -sb
    return "UTF-8";
  }

  /**
   * Return an indication of the standalone status of the document,
   *        either "yes" or "no". This property is derived from the optional
   *        standalone document declaration in the XML declaration at the
   *        beginning of the document entity, and has no value if there is no
   *        standalone document declaration.
   *
   * @param nodeHandle The node id, which can be any valid node handle.
   * @return the document standalone String object, either "yes", "no", or null.
   */
  public String getDocumentStandalone(int nodeHandle)
  {
    return null;
  }

  /**
   * Return a string representing the XML version of the document. This
   * property is derived from the XML declaration optionally present at the
   * beginning of the document entity, and has no value if there is no XML
   * declaration.
   *
   * @param the document handle
   *
   * NEEDSDOC @param documentHandle
   *
   * @return the document version String object.
   */
  public String getDocumentVersion(int documentHandle)
  {
    return null;
  }

  /**
   * Return an indication of
   * whether the processor has read the complete DTD. Its value is a
   * boolean. If it is false, then certain properties (indicated in their
   * descriptions below) may be unknown. If it is true, those properties
   * are never unknown.
   *
   * @return <code>true</code> if all declarations were processed;
   *         <code>false</code> otherwise.
   */
  public boolean getDocumentAllDeclarationsProcessed()
  {

    // %REVIEW% OK?
    return true;
  }

  /**
   *   A document type declaration information item has the following properties:
   *
   *     1. [system identifier] The system identifier of the external subset, if
   *        it exists. Otherwise this property has no value.
   *
   * @return the system identifier String object, or null if there is none.
   */
  public abstract String getDocumentTypeDeclarationSystemIdentifier();

  /**
   * Return the public identifier of the external subset,
   * normalized as described in 4.2.2 External Entities [XML]. If there is
   * no external subset or if it has no public identifier, this property
   * has no value.
   *
   * @param the document type declaration handle
   *
   * @return the public identifier String object, or null if there is none.
   */
  public abstract String getDocumentTypeDeclarationPublicIdentifier();

  /**
   * Returns the <code>Element</code> whose <code>ID</code> is given by
   * <code>elementId</code>. If no such element exists, returns
   * <code>DTM.NULL</code>. Behavior is not defined if more than one element
   * has this <code>ID</code>. Attributes (including those
   * with the name "ID") are not of type ID unless so defined by DTD/Schema
   * information available to the DTM implementation.
   * Implementations that do not know whether attributes are of type ID or
   * not are expected to return <code>DTM.NULL</code>.
   *
   * <p>%REVIEW% Presumably IDs are still scoped to a single document,
   * and this operation searches only within a single document, right?
   * Wouldn't want collisions between DTMs in the same process.</p>
   *
   * @param elementId The unique <code>id</code> value for an element.
   * @return The handle of the matching element.
   */
  public abstract int getElementById(String elementId);

  /**
   * The getUnparsedEntityURI function returns the URI of the unparsed
   * entity with the specified name in the same document as the context
   * node (see [3.3 Unparsed Entities]). It returns the empty string if
   * there is no such entity.
   * <p>
   * XML processors may choose to use the System Identifier (if one
   * is provided) to resolve the entity, rather than the URI in the
   * Public Identifier. The details are dependent on the processor, and
   * we would have to support some form of plug-in resolver to handle
   * this properly. Currently, we simply return the System Identifier if
   * present, and hope that it a usable URI or that our caller can
   * map it to one.
   * TODO: Resolve Public Identifiers... or consider changing function name.
   * <p>
   * If we find a relative URI
   * reference, XML expects it to be resolved in terms of the base URI
   * of the document. The DOM doesn't do that for us, and it isn't
   * entirely clear whether that should be done here; currently that's
   * pushed up to a higher level of our application. (Note that DOM Level
   * 1 didn't store the document's base URI.)
   * TODO: Consider resolving Relative URIs.
   * <p>
   * (The DOM's statement that "An XML processor may choose to
   * completely expand entities before the structure model is passed
   * to the DOM" refers only to parsed entities, not unparsed, and hence
   * doesn't affect this function.)
   *
   * @param name A string containing the Entity Name of the unparsed
   * entity.
   *
   * @return String containing the URI of the Unparsed Entity, or an
   * empty string if no such entity exists.
   */
  public abstract String getUnparsedEntityURI(String name);

  // ============== Boolean methods ================

  /**
   * Return true if the xsl:strip-space or xsl:preserve-space was processed
   * during construction of the DTM document.
   *
   * @return true if this DTM supports prestripping.
   */
  public boolean supportsPreStripping()
  {
    return true;
  }

  /**
   * Figure out whether nodeHandle2 should be considered as being later
   * in the document than nodeHandle1, in Document Order as defined
   * by the XPath model. This may not agree with the ordering defined
   * by other XML applications.
   * <p>
   * There are some cases where ordering isn't defined, and neither are
   * the results of this function -- though we'll generally return true.
   *
   * TODO: Make sure this does the right thing with attribute nodes!!!
   *
   * @param node1 DOM Node to perform position comparison on.
   * @param node2 DOM Node to perform position comparison on .
   *
   * NEEDSDOC @param nodeHandle1
   * NEEDSDOC @param nodeHandle2
   *
   * @return false if node2 comes before node1, otherwise return true.
   * You can think of this as
   * <code>(node1.documentOrderPosition &lt;= node2.documentOrderPosition)</code>.
   */
  public boolean isNodeAfter(int nodeHandle1, int nodeHandle2)
  {

    int index1 = nodeHandle1 & m_mask;
    int index2 = nodeHandle2 & m_mask;

    return index1 <= index2;
  }

  /**
   *     2. [element content whitespace] A boolean indicating whether the
   *        character is white space appearing within element content (see [XML],
   *        2.10 "White Space Handling"). Note that validating XML processors are
   *        required by XML 1.0 to provide this information. If there is no
   *        declaration for the containing element, this property has no value for
   *        white space characters. If no declaration has been read, but the [all
   *        declarations processed] property of the document information item is
   *        false (so there may be an unread declaration), then the value of this
   *        property is unknown for white space characters. It is always false for
   *        characters that are not white space.
   *
   * @param nodeHandle the node ID.
   * @return <code>true</code> if the character data is whitespace;
   *         <code>false</code> otherwise.
   */
  public boolean isCharacterElementContentWhitespace(int nodeHandle)
  {

    // %TBD%
    return false;
  }

  /**
   *    10. [all declarations processed] This property is not strictly speaking
   *        part of the infoset of the document. Rather it is an indication of
   *        whether the processor has read the complete DTD. Its value is a
   *        boolean. If it is false, then certain properties (indicated in their
   *        descriptions below) may be unknown. If it is true, those properties
   *        are never unknown.
   *
   * @param the document handle
   *
   * @param documentHandle A node handle that must identify a document.
   * @return <code>true</code> if all declarations were processed;
   *         <code>false</code> otherwise.
   */
  public boolean isDocumentAllDeclarationsProcessed(int documentHandle)
  {
    return true;
  }

  /**
   *     5. [specified] A flag indicating whether this attribute was actually
   *        specified in the start-tag of its element, or was defaulted from the
   *        DTD.
   *
   * @param the attribute handle
   *
   * NEEDSDOC @param attributeHandle
   * @return <code>true</code> if the attribute was specified;
   *         <code>false</code> if it was defaulted.
   */
  public abstract boolean isAttributeSpecified(int attributeHandle);

  // ========== Direct SAX Dispatch, for optimization purposes ========

  /**
   * Directly call the
   * characters method on the passed ContentHandler for the
   * string-value of the given node (see http://www.w3.org/TR/xpath#data-model
   * for the definition of a node's string-value). Multiple calls to the
   * ContentHandler's characters methods may well occur for a single call to
   * this method.
   *
   * @param nodeHandle The node ID.
   * @param ch A non-null reference to a ContentHandler.
   *
   * @throws org.xml.sax.SAXException
   */
  public abstract void dispatchCharactersEvents(
    int nodeHandle, org.xml.sax.ContentHandler ch)
      throws org.xml.sax.SAXException;

  /**
   * Directly create SAX parser events from a subtree.
   *
   * @param nodeHandle The node ID.
   * @param ch A non-null reference to a ContentHandler.
   *
   * @throws org.xml.sax.SAXException
   */
  public abstract void dispatchToEvents(
    int nodeHandle, org.xml.sax.ContentHandler ch)
      throws org.xml.sax.SAXException;

  /**
   * Return an DOM node for the given node.
   *
   * @param nodeHandle The node ID.
   *
   * @return A node representation of the DTM node.
   */
  public org.w3c.dom.Node getNode(int nodeHandle)
  {
    return new DTMNodeProxy(this, nodeHandle);
  }

  // ==== Construction methods (may not be supported by some implementations!) =====

  /**
   * Append a child to the end of the document. Please note that the node
   * is always cloned if it is owned by another document.
   *
   * <p>%REVIEW% "End of the document" needs to be defined more clearly.
   * Does it become the last child of the Document? Of the root element?</p>
   *
   * @param newChild Must be a valid new node handle.
   * @param clone true if the child should be cloned into the document.
   * @param cloneDepth if the clone argument is true, specifies that the
   *                   clone should include all it's children.
   */
  public void appendChild(int newChild, boolean clone, boolean cloneDepth)
  {
    error("appendChild not yet supported!");
  }

  /**
   * Append a text node child that will be constructed from a string,
   * to the end of the document.
   *
   * <p>%REVIEW% "End of the document" needs to be defined more clearly.
   * Does it become the last child of the Document? Of the root element?</p>
   *
   * @param str Non-null reverence to a string.
   */
  public void appendTextChild(String str)
  {
    error("appendTextChild not yet supported!");
  }

  /**
   * Simple error for asserts and the like.
   *
   * NEEDSDOC @param msg
   */
  protected void error(String msg)
  {
    throw new DTMException(msg);
  }

  /**
   * Find out whether or not to strip whispace nodes.
   *
   *
   * @return whether or not to strip whispace nodes.
   */
  protected boolean getShouldStripWhitespace()
  {
    return m_shouldStripWS;
  }

  /**
   * Set whether to strip whitespaces and push in current value of
   * m_shouldStripWS in m_shouldStripWhitespaceStack.
   *
   * @param shouldStrip Flag indicating whether to strip whitespace nodes
   */
  protected void pushShouldStripWhitespace(boolean shouldStrip)
  {

    m_shouldStripWS = shouldStrip;

    if (null != m_shouldStripWhitespaceStack)
      m_shouldStripWhitespaceStack.push(shouldStrip);
  }

  /**
   * Set whether to strip whitespaces at this point by popping out
   * m_shouldStripWhitespaceStack.
   *
   */
  protected void popShouldStripWhitespace()
  {
    if (null != m_shouldStripWhitespaceStack)
      m_shouldStripWS = m_shouldStripWhitespaceStack.popAndTop();
  }

  /**
   * Set whether to strip whitespaces and set the top of the stack to
   * the current value of m_shouldStripWS.
   *
   *
   * @param shouldStrip Flag indicating whether to strip whitespace nodes
   */
  protected void setShouldStripWhitespace(boolean shouldStrip)
  {

    m_shouldStripWS = shouldStrip;

    if (null != m_shouldStripWhitespaceStack)
      m_shouldStripWhitespaceStack.setTop(shouldStrip);
  }
}
