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
package org.apache.xml.dtm.dom2dtm;

import org.apache.xml.dtm.*;
import org.apache.xml.utils.IntVector;
import org.apache.xml.utils.IntStack;
import org.apache.xml.utils.BoolStack;
import org.apache.xml.utils.StringBufferPool;
import org.apache.xml.utils.FastStringBuffer;
import org.apache.xml.utils.TreeWalker;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.XMLCharacterRecognizer;

import org.w3c.dom.*;

import java.util.Vector;

import javax.xml.transform.dom.DOMSource;
import org.xml.sax.ContentHandler;

import org.apache.xml.utils.NodeVector;

import org.apache.xml.utils.XMLString;
import org.apache.xml.utils.XMLStringFactory;

/**
 * The <code>DOM2DTM</code> class serves up a DOM via a DTM API.
 */
public class DOM2DTM extends DTMDefaultBase
{
  /**
   * This represents the number of integers per node in the
   * <code>m_info</code> member variable.
   */
  protected static final int NODEINFOBLOCKSIZE = DEFAULTNODEINFOBLOCKSIZE;

  /** The top of the subtree, may not be the same as m_context if "//foo" pattern. */
  transient private Node m_root;

  /** The current position in the tree. */
  transient private Node m_pos;

  /** true if all the nodes have been processed. */
  transient private boolean m_nodesAreProcessed;

  /**
   * %TBD% Needs doc... how to explain?
   * [0] index of parent.
   * [1] index of previous sibling.
   */
  transient private IntStack m_levelInfo = new IntStack();

  /**
   * %TBD% Doc
   */
  transient private NamedNodeMap m_attrs;

  /**
   * %TBD% Doc
   */
  transient private int m_attrsPos;

  /** NEEDSDOC Field LEVELINFO_PARENT */
  static final int LEVELINFO_PARENT = 1;

  /** NEEDSDOC Field LEVELINFO_PREVSIB */
  static final int LEVELINFO_PREVSIB = 0;

  /** NEEDSDOC Field LEVELINFO_NPERLEVEL */
  static final int LEVELINFO_NPERLEVEL = 2;
  
  /** Samed element for attribute iteration */
  private Node m_elementForAttrs;
  
  /** Samed element index for attribute iteration */
  private int m_elementForAttrsIndex;

  /**
   * The node objects.  The instance part of the handle indexes directly
   * into this vector.  Each DTM node may actually be composed of several
   * DOM nodes.
   */
  protected Vector m_nodes = new Vector();

  /**
   * Construct a DOM2DTM object from a DOM node.
   *
   * @param mgr The DTMManager who owns this DTM.
   * @param domSource the DOM source that this DTM will wrap.
   * @param dtmIdentity The DTM identity ID for this DTM.
   * @param whiteSpaceFilter The white space filter for this DTM, which may 
   *                         be null.
   */
  public DOM2DTM(DTMManager mgr, DOMSource domSource, 
                 int dtmIdentity, DTMWSFilter whiteSpaceFilter,
                 XMLStringFactory xstringfactory)
  {
    super(mgr, domSource, dtmIdentity, whiteSpaceFilter, xstringfactory);

    m_root = domSource.getNode();
    m_pos = null;
    m_nodesAreProcessed = false;
    addNode(m_root, 0, DTM.NULL, DTM.NULL);
  }

  /**
   * Construct the node map from the node.
   *
   * @param node The node that is to be added to the DTM.
   * @param level The current level in the tree.
   * @param parentIndex The current parent index.
   * @param previousSibling The previous sibling index.
   *
   * @return The index identity of the node that was added.
   */
  protected int addNode(Node node, int level, int parentIndex,
                        int previousSibling)
  {

    int nodeIndex = m_nodes.size();
    
    int type = node.getNodeType();
    
    m_nodes.addElement(node);

    int startInfo = nodeIndex * NODEINFOBLOCKSIZE;

    m_info.addElements(NODEINFOBLOCKSIZE);
    m_info.setElementAt(level, startInfo + OFFSET_LEVEL);

    if (Node.ATTRIBUTE_NODE == type)
    {
      String name = node.getNodeName();

      if (name.startsWith("xmlns:") || name.equals("xmlns"))
      {
        type = DTM.NAMESPACE_NODE;
      }
    }

    m_info.setElementAt(type, startInfo + OFFSET_TYPE);
    m_info.setElementAt(NOTPROCESSED, startInfo + OFFSET_FIRSTCHILD);
    m_info.setElementAt(NOTPROCESSED, startInfo + OFFSET_NEXTSIBLING);
    m_info.setElementAt(previousSibling, startInfo + OFFSET_PREVSIBLING);
    m_info.setElementAt(parentIndex, startInfo + OFFSET_PARENT);
    
    if(DTM.NULL != parentIndex && type != DTM.ATTRIBUTE_NODE && type != DTM.NAMESPACE_NODE)
    {
      int startParentInfo = parentIndex * NODEINFOBLOCKSIZE;
      if(NOTPROCESSED == m_info.elementAt(startParentInfo + OFFSET_FIRSTCHILD))
      {
        m_info.setElementAt(nodeIndex, startParentInfo + OFFSET_FIRSTCHILD);
      }
    }
    
    String nsURI = node.getNamespaceURI();
    String localName =  (type == Node.PROCESSING_INSTRUCTION_NODE) ? 
                         node.getNodeName() :
                         node.getLocalName();
    ExpandedNameTable exnt = m_mgr.getExpandedNameTable(this);

    int expandedNameID = (null != localName) 
       ? exnt.getExpandedNameID(nsURI, localName, type) :
         exnt.getExpandedNameID(type);

    m_info.setElementAt(expandedNameID, startInfo + OFFSET_EXPANDEDNAMEID);    

    if (DTM.NULL != previousSibling)
    {
      m_info.setElementAt(nodeIndex,
                          (previousSibling * NODEINFOBLOCKSIZE)
                          + OFFSET_NEXTSIBLING);
    }

    return nodeIndex;
  }
  
  /**
   * Return the number of integers in each node info block.
   */
  protected int getNodeInfoBlockSize()
  {
    return NODEINFOBLOCKSIZE;
  }
  
  /**
   * Get the number of nodes that have been added.
   */
  protected int getNumberOfNodes()
  {
    return m_nodes.size();
  }


  /**
   * This method iterates to the next node that will be added to the table.
   * Each call to this method adds a new node to the table, unless the end
   * is reached, in which case it returns null.
   *
   * @return The true if a next node is found or false if 
   *         there are no more nodes.
   */
  protected boolean nextNode()
  {
    // Non-recursive one-fetch-at-a-time depth-first traversal with 
    // attribute/namespace nodes and white-space stripping.
    // Yippee!  Not for the faint of heart.  I would be glad for 
    // constructive suggestions on how to make this cleaner.

    if (m_nodesAreProcessed)
    {
      return false;
    }
    
    if(m_nodes.size() == 47)
    {
      int x = 5;
      x++;
    }

    Node top = m_root;  // tells us when to stop.
    Node pos = (null == m_pos) ? m_root : m_pos;

    Node nextNode;
    int type = pos.getNodeType();

    int currentIndexHandle = m_nodes.size()-1;
    int posInfo = currentIndexHandle * NODEINFOBLOCKSIZE;
    
    boolean shouldPushLevel = true;
    if (Node.ELEMENT_NODE == type)
    {
      m_attrs = pos.getAttributes();
      m_attrsPos = 0;

      if (null != m_attrs)
      {
        if (m_attrsPos < m_attrs.getLength())
        {
          m_elementForAttrs = pos;
          m_elementForAttrsIndex = currentIndexHandle;
          nextNode = m_attrs.item(m_attrsPos);
        }
        else
          nextNode = pos.getFirstChild();
      }
      else
      {
        nextNode = pos.getFirstChild();
      }
    }
    else if (Node.ATTRIBUTE_NODE == type)
    {
      m_info.setElementAt(DTM.NULL, posInfo + OFFSET_FIRSTCHILD);
      m_attrsPos++;

      if (m_attrsPos < m_attrs.getLength())
      {
        nextNode = m_attrs.item(m_attrsPos);
        shouldPushLevel = false;
      }
      else
      {
        m_info.setElementAt(DTM.NULL, posInfo + OFFSET_NEXTSIBLING); 
        pos = m_elementForAttrs;
        currentIndexHandle = m_elementForAttrsIndex;
        posInfo = currentIndexHandle * NODEINFOBLOCKSIZE;
        nextNode = pos.getFirstChild();
        m_levelInfo.quickPop(LEVELINFO_NPERLEVEL);
      }
    }
    else
      nextNode = pos.getFirstChild();  
     
    // %TBD% Text node coalition.
    if((null != m_wsfilter) && (null != nextNode) && getShouldStripWhitespace())
    {
      int t = nextNode.getNodeType();
      
      if((Node.CDATA_SECTION_NODE == t) || (Node.TEXT_NODE == t))
      {
        String data = nextNode.getNodeValue();
        if(XMLCharacterRecognizer.isWhiteSpace(data))
        {
          nextNode = nextNode.getNextSibling();
        }
      }
    }
    if (shouldPushLevel && (null != nextNode))
    {
      m_levelInfo.push(currentIndexHandle); // parent
      m_levelInfo.push(DTM.NULL); // previous sibling
    }

    while (null == nextNode)
    {
      if(m_info.elementAt(posInfo + OFFSET_FIRSTCHILD) == NOTPROCESSED)
      {
        m_info.setElementAt(DTM.NULL, posInfo + OFFSET_FIRSTCHILD);
      }
      
      if (top.equals(pos))
      {
        m_info.setElementAt(DTM.NULL, posInfo + OFFSET_NEXTSIBLING);
        break;
      }
      
      nextNode = pos.getNextSibling();
      if(null != nextNode && Node.DOCUMENT_TYPE_NODE == nextNode.getNodeType())
      {
        // Xerces
        nextNode = nextNode.getNextSibling(); // just skip it.
      }
      
      if(Node.ELEMENT_NODE == pos.getNodeType())
      {
        // I think this only has to be popped here, and not at getParent,
        // oddly enough at first glance.
        popShouldStripWhitespace();
      }

      // %TBD% Text node coalition.
      if((null != nextNode) && (null != m_wsfilter) && getShouldStripWhitespace())
      {
        int t = nextNode.getNodeType();
        
        if((Node.CDATA_SECTION_NODE == t) || (Node.TEXT_NODE == t))
        {
          String data = nextNode.getNodeValue();
          if(XMLCharacterRecognizer.isWhiteSpace(data))
          {
            nextNode = nextNode.getNextSibling();
          }
        }
      }
                  
      if (null == nextNode)
      {
        m_info.setElementAt(DTM.NULL, posInfo + OFFSET_NEXTSIBLING);

        currentIndexHandle = m_info.elementAt(posInfo + OFFSET_PARENT);
        posInfo = currentIndexHandle * NODEINFOBLOCKSIZE;
        m_levelInfo.quickPop(LEVELINFO_NPERLEVEL);
        pos = pos.getParentNode();

        if ((null == pos) || (top.equals(pos)))
        {
          m_info.setElementAt(DTM.NULL, posInfo + OFFSET_NEXTSIBLING);
          nextNode = null;
          // break;
          m_nodesAreProcessed = true;
          return false;
        }
      }
        
      
    } // end while (null == nextNode) [for next sibling, parent]

    pos = nextNode;

    if (null != pos)
    {
      int level = m_levelInfo.size() / LEVELINFO_NPERLEVEL;
      int newIndexHandle = 
            addNode(pos, level, m_levelInfo.peek(LEVELINFO_PARENT),
                m_levelInfo.peek(LEVELINFO_PREVSIB));

      m_pos = pos;

      int sz = m_levelInfo.size();

      m_levelInfo.setElementAt(newIndexHandle,
                               sz - (1 + LEVELINFO_PREVSIB));
                               
      if((null != m_wsfilter) && (Node.ELEMENT_NODE == pos.getNodeType()))
      {
        short wsv = m_wsfilter.getShouldStripSpace(newIndexHandle);
        boolean shouldStrip = (DTMWSFilter.INHERIT == wsv) ? 
                  getShouldStripWhitespace() : (DTMWSFilter.STRIP == wsv);
        pushShouldStripWhitespace(shouldStrip);
      }
      return true;
    }


    m_nodesAreProcessed = true;
    m_pos = null;
    return false;
  }

  /**
   * Return an DOM node for the given node.
   *
   * @param nodeHandle The node ID.
   *
   * @return A node representation of the DTM node.
   */
  public Node getNode(int nodeHandle)
  {

    int identity = nodeHandle & m_mask;

    return (Node) m_nodes.elementAt(identity);
  }

  /**
   * Get a Node from an identity index.
   *
   * NEEDSDOC @param nodeIdentity
   *
   * NEEDSDOC ($objectName$) @return
   */
  protected Node lookupNode(int nodeIdentity)
  {
    return (Node) m_nodes.elementAt(nodeIdentity);
  }

  /**
   * Get the next node identity value in the list, and call the iterator
   * if it hasn't been added yet.
   *
   * @param identity The node identity (index).
   * @return identity+1, or DTM.NULL.
   */
  protected int getNextNodeIdentity(int identity)
  {

    identity += 1;

    if (identity >= m_nodes.size())
    {
      if (!nextNode())
        identity = DTM.NULL;
    }

    return identity;
  }

  /**
   * Get the handle from a Node.
   * <p>%OPT% This will be pretty slow.</p>
   * 
   * %REVIEW% This relies on being able to test node-identity via
   * object-identity. DTM2DOM proxying is a great example of a case where
   * that doesn't work. DOM Level 3 will provide the isSameNode() method
   * to fix that, but until then this is going to be flaky.
   *
   * @param node A node, which may be null.
   *
   * @return The node handle or <code>DTM.NULL</code>.
   */
  private int getHandleFromNode(Node node)
  {
    if (null != node)
    {
      int len = m_nodes.size();
      for (int i = 0; i < len; i++)
	{
	  if (m_nodes.elementAt(i) == node)
	    return i | m_dtmIdent;
	}
    }

    return DTM.NULL;
  }

  /** Get the handle from a Node. This is a more robust version of
   * getHandleFromNode, intended to be usable by the public.
   *
   * <p>%OPT% This will be pretty slow.</p>
   * 
   * %REVIEW% This relies on being able to test node-identity via
   * object-identity. DTM2DOM proxying is a great example of a case where
   * that doesn't work. DOM Level 3 will provide the isSameNode() method
   * to fix that, but until then this is going to be flaky.
   *
   * @param node A node, which may be null.
   *
   * @return The node handle or <code>DTM.NULL</code>.  */
  public int getHandleOfNode(Node node)
  {
    if (null != node)
    {
      // Is Node actually within the same document? If not, don't search!
      // This would be easier if m_root was always the Document node, but
      // we decided to allow wrapping a DTM around a subtree.
      if((m_root==node) ||
	 (m_root.getNodeType()==DOCUMENT_NODE &&
	  m_root==node.getOwnerDocument()) ||
	 (m_root.getNodeType()!=DOCUMENT_NODE &&
	  m_root.getOwnerDocument()==node.getOwnerDocument())
	 )
	{
	  // If node _is_ in m_root's tree, find its handle
	  //
	  // %OPT% This check may be improved significantly when DOM
	  // Level 3 nodeKey and relative-order tests become
	  // available!
	  for(Node cursor=node;
	      cursor!=null;
	      cursor=
		(cursor.getNodeType()!=ATTRIBUTE_NODE)
		? cursor.getParentNode()
		: ((org.w3c.dom.Attr)cursor).getOwnerElement())
	    {
	      if(cursor==m_root)
		// We know this node; find its handle.
		return getHandleFromNode(node); 
	    } // for ancestors of node
	} // if node and m_root in same Document
    } // if node!=null

    return DTM.NULL;
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
  public int getAttributeNode(int nodeHandle, String namespaceURI,
                              String name)
  {

    // %OPT% This is probably slower than it needs to be.
    if (null == namespaceURI)
      namespaceURI = "";

    int type = getNodeType(nodeHandle);

    if (DTM.ELEMENT_NODE == type)
    {

      // Assume that attributes immediately follow the element.
      int identity = nodeHandle & m_mask;

      while (DTM.NULL != (identity = getNextNodeIdentity(identity)))
      {
        // Assume this can not be null.
        type = getNodeType(identity);

        if (type == DTM.ATTRIBUTE_NODE)
        {
          Node node = lookupNode(identity);
          String nodeuri = node.getNamespaceURI();

          if (null == nodeuri)
            nodeuri = "";

          String nodelocalname = node.getLocalName();

          if (nodeuri.equals(namespaceURI) && name.equals(nodelocalname))
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
   * Get the string-value of a node as a String object
   * (see http://www.w3.org/TR/xpath#data-model
   * for the definition of a node's string-value).
   *
   * @param nodeHandle The node ID.
   *
   * @return A string object that represents the string-value of the given node.
   */
  public XMLString getStringValue(int nodeHandle)
  {

    int type = getNodeType(nodeHandle);
    Node node = getNode(nodeHandle);
    // %TBD% If an element only has one text node, we should just use it 
    // directly.
    if(DTM.ELEMENT_NODE == type || DTM.DOCUMENT_NODE == type 
    || DTM.DOCUMENT_FRAGMENT_NODE == type)
    {
      FastStringBuffer buf = StringBufferPool.get();
      String s;
  
      try
      {
        getNodeData(node, buf);
  
        s = (buf.length() > 0) ? buf.toString() : "";
      }
      finally
      {
        StringBufferPool.free(buf);
      }
  
      return m_xstrf.newstr( s );

    }
    return m_xstrf.newstr( node.getNodeValue() );
  }
  
  /**
   * Retrieve the text content of a DOM subtree, appending it into a
   * user-supplied FastStringBuffer object. Note that attributes are
   * not considered part of the content of an element.
   * <p>
   * There are open questions regarding whitespace stripping. 
   * Currently we make no special effort in that regard, since the standard
   * DOM doesn't yet provide DTD-based information to distinguish
   * whitespace-in-element-context from genuine #PCDATA. Note that we
   * should probably also consider xml:space if/when we address this.
   * DOM Level 3 may solve the problem for us.
   *
   * @param node Node whose subtree is to be walked, gathering the
   * contents of all Text or CDATASection nodes.
   * @param buf FastStringBuffer into which the contents of the text
   * nodes are to be concatenated.
   */
  protected static void getNodeData(Node node, FastStringBuffer buf)
  {

    switch (node.getNodeType())
    {
    case Node.DOCUMENT_FRAGMENT_NODE :
    case Node.DOCUMENT_NODE :
    case Node.ELEMENT_NODE :
    {
      for (Node child = node.getFirstChild(); null != child;
              child = child.getNextSibling())
      {
        getNodeData(child, buf);
      }
    }
    break;
    case Node.TEXT_NODE :
    case Node.CDATA_SECTION_NODE :
      buf.append(node.getNodeValue());
      break;
    case Node.ATTRIBUTE_NODE :
      buf.append(node.getNodeValue());
      break;
    case Node.PROCESSING_INSTRUCTION_NODE :
      // warning(XPATHErrorResources.WG_PARSING_AND_PREPARING);        
      break;
    default :
      // ignore
      break;
    }
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
  public String getNodeName(int nodeHandle)
  {

    Node node = getNode(nodeHandle);

    // Assume non-null.
    return node.getNodeName();
  }

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

    String name;
    short type = getNodeType(nodeHandle);

    switch (type)
    {
    case DTM.NAMESPACE_NODE :
    {
      Node node = getNode(nodeHandle);

      // assume not null.
      name = node.getNodeName();
      if(name.startsWith("xmlns:"))
      {
        name = QName.getLocalPart(name);
      }
      else if(name.equals("xmlns"))
      {
        name = "";
      }
    }
    break;
    case DTM.ATTRIBUTE_NODE :
    case DTM.ELEMENT_NODE :
    case DTM.ENTITY_REFERENCE_NODE :
    case DTM.PROCESSING_INSTRUCTION_NODE :
    {
      Node node = getNode(nodeHandle);

      // assume not null.
      name = node.getNodeName();
    }
    break;
    default :
      name = "";
    }

    return name;
  }

  /**
   * Given a node handle, return its XPath-style localname.
   * (As defined in Namespaces, this is the portion of the name after any
   * colon character).
   *
   * @param nodeHandle the id of the node.
   * @return String Local name of this node.
   */
  public String getLocalName(int nodeHandle)
  {

    String name;
    short type = getNodeType(nodeHandle);

    switch (type)
    {
    case DTM.ATTRIBUTE_NODE :
    case DTM.ELEMENT_NODE :
    case DTM.ENTITY_REFERENCE_NODE :
    case DTM.NAMESPACE_NODE :
    case DTM.PROCESSING_INSTRUCTION_NODE :
    {
      Node node = getNode(nodeHandle);

      // assume not null.
      name = node.getLocalName();

      if (null == name)
      {
        String qname = node.getNodeName();
        int index = qname.indexOf(':');

        name = (index < 0) ? qname : qname.substring(index + 1);
      }
    }
    break;
    default :
      name = "";
    }

    return name;
  }

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
  public String getPrefix(int nodeHandle)
  {

    String prefix;
    short type = getNodeType(nodeHandle);

    switch (type)
    {
    case DTM.NAMESPACE_NODE :
    {
      Node node = getNode(nodeHandle);

      // assume not null.
      String qname = node.getNodeName();
      int index = qname.indexOf(':');

      prefix = (index < 0) ? "" : qname.substring(index + 1);
    }
    break;
    case DTM.ATTRIBUTE_NODE :
    case DTM.ELEMENT_NODE :
    {
      Node node = getNode(nodeHandle);

      // assume not null.
      String qname = node.getNodeName();
      int index = qname.indexOf(':');

      prefix = (index < 0) ? "" : qname.substring(0, index);
    }
    break;
    default :
      prefix = "";
    }

    return prefix;
  }

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
  public String getNamespaceURI(int nodeHandle)
  {

    String nsuri;
    short type = getNodeType(nodeHandle);

    switch (type)
    {
    case DTM.ATTRIBUTE_NODE :
    case DTM.ELEMENT_NODE :
    case DTM.ENTITY_REFERENCE_NODE :
    case DTM.NAMESPACE_NODE :
    case DTM.PROCESSING_INSTRUCTION_NODE :
    {
      Node node = getNode(nodeHandle);

      // assume not null.
      nsuri = node.getNamespaceURI();

      // %TBD% Handle DOM1?
    }
    break;
    default :
      nsuri = null;
    }

    return nsuri;
  }

  /**
   * Given a node handle, return its node value. This is mostly
   * as defined by the DOM, but may ignore some conveniences.
   * <p>
   *
   * @param nodeHandle The node id.
   * @return String Value of this node, or null if not
   * meaningful for this node type.
   */
  public String getNodeValue(int nodeHandle)
  {

    Node node = getNode(nodeHandle);

    return node.getNodeValue();
  }


  /**
   *   A document type declaration information item has the following properties:
   *
   *     1. [system identifier] The system identifier of the external subset, if
   *        it exists. Otherwise this property has no value.
   *
   * @return the system identifier String object, or null if there is none.
   */
  public String getDocumentTypeDeclarationSystemIdentifier()
  {

    Document doc;

    if (m_root.getNodeType() == Node.DOCUMENT_NODE)
      doc = (Document) m_root;
    else
      doc = m_root.getOwnerDocument();

    if (null != doc)
    {
      DocumentType dtd = doc.getDoctype();

      if (null != dtd)
      {
        return dtd.getSystemId();
      }
    }

    return null;
  }

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
  public String getDocumentTypeDeclarationPublicIdentifier()
  {

    Document doc;

    if (m_root.getNodeType() == Node.DOCUMENT_NODE)
      doc = (Document) m_root;
    else
      doc = m_root.getOwnerDocument();

    if (null != doc)
    {
      DocumentType dtd = doc.getDoctype();

      if (null != dtd)
      {
        return dtd.getPublicId();
      }
    }

    return null;
  }

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
  public int getElementById(String elementId)
  {

    Document doc = (m_root.getNodeType() == Node.DOCUMENT_NODE) 
        ? (Document) m_root : m_root.getOwnerDocument();
        
    if(null != doc)
    {
      Node elem = doc.getElementById(elementId);
      if(null != elem)
      {
        int elemHandle = getHandleFromNode(elem);
        
        if(DTM.NULL == elemHandle)
        {
          int identity = m_nodes.size()-1;
          while (DTM.NULL != (identity = getNextNodeIdentity(identity)))
          {
            Node node = getNode(identity);
            if(node == elem)
            {
              elemHandle = getHandleFromNode(elem);
              break;
            }
           }
        }
        
        return elemHandle;
      }
    
    }
    return DTM.NULL;
  }

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
  public String getUnparsedEntityURI(String name)
  {

    String url = "";
    Document doc = (m_root.getNodeType() == Node.DOCUMENT_NODE) 
        ? (Document) m_root : m_root.getOwnerDocument();

    if (null != doc)
    {
      DocumentType doctype = doc.getDoctype();
  
      if (null != doctype)
      {
        NamedNodeMap entities = doctype.getEntities();
        if(null == entities)
          return url;
        Entity entity = (Entity) entities.getNamedItem(name);
        if(null == entity)
          return url;
        
        String notationName = entity.getNotationName();
  
        if (null != notationName)  // then it's unparsed
        {
          // The draft says: "The XSLT processor may use the public 
          // identifier to generate a URI for the entity instead of the URI 
          // specified in the system identifier. If the XSLT processor does 
          // not use the public identifier to generate the URI, it must use 
          // the system identifier; if the system identifier is a relative 
          // URI, it must be resolved into an absolute URI using the URI of 
          // the resource containing the entity declaration as the base 
          // URI [RFC2396]."
          // So I'm falling a bit short here.
          url = entity.getSystemId();
  
          if (null == url)
          {
            url = entity.getPublicId();
          }
          else
          {
            // This should be resolved to an absolute URL, but that's hard 
            // to do from here.
          }        
        }
      }
    }

    return url;
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
  public boolean isAttributeSpecified(int attributeHandle)
  {
    int type = getNodeType(attributeHandle);

    if (DTM.ATTRIBUTE_NODE == type)
    {
      Attr attr = (Attr)getNode(attributeHandle);
      return attr.getSpecified();
    }
    return false;
  }

  /** Bind a CoroutineParser to this DTM. NOT RELEVANT for DOM2DTM, since
   * we're wrapped around an existing DOM.
   *
   * @param coroutineParser The parser that we want to recieve events from
   * on demand.
   */
  public void setCoroutineParser(CoroutineParser coroutineParser)
  {
  }
  
  /** getContentHandler returns "our SAX builder" -- the thing that
   * someone else should send SAX events to in order to extend this
   * DTM model.
   *
   * @return null if this model doesn't respond to SAX events,
   * "this" if the DTM object has a built-in SAX ContentHandler,
   * the CoroutineParser if we're bound to one and should receive
   * the SAX stream via it for incremental build purposes...
   * */
  public org.xml.sax.ContentHandler getContentHandler()
  {
      return null;
  }
  
  /**
   * Return this DTM's lexical handler.
   *
   * %REVIEW% Should this return null if constrution already done/begun?
   *
   * @return null if this model doesn't respond to lexical SAX events,
   * "this" if the DTM object has a built-in SAX ContentHandler,
   * the CoroutineParser if we're bound to one and should receive
   * the SAX stream via it for incremental build purposes...
   */
  public org.xml.sax.ext.LexicalHandler getLexicalHandler()
  {

    return null;
  }

  
  /**
   * Return this DTM's EntityResolver.
   *
   * @return null if this model doesn't respond to SAX entity ref events.
   */
  public org.xml.sax.EntityResolver getEntityResolver()
  {

    return null;
  }
  
  /**
   * Return this DTM's DTDHandler.
   *
   * @return null if this model doesn't respond to SAX dtd events.
   */
  public org.xml.sax.DTDHandler getDTDHandler()
  {

    return null;
  }

  /**
   * Return this DTM's ErrorHandler.
   *
   * @return null if this model doesn't respond to SAX error events.
   */
  public org.xml.sax.ErrorHandler getErrorHandler()
  {

    return null;
  }
  
  /**
   * Return this DTM's DeclHandler.
   *
   * @return null if this model doesn't respond to SAX Decl events.
   */
  public org.xml.sax.ext.DeclHandler getDeclHandler()
  {

    return null;
  }  

  /** @return true iff we're building this model incrementally (eg
   * we're partnered with a CoroutineParser) and thus require that the
   * transformation and the parse run simultaneously. Guidance to the
   * DTMManager.
   * */
  public boolean needsTwoThreads()
  {
    return false;
  }

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
  public void dispatchCharactersEvents(
          int nodeHandle, org.xml.sax.ContentHandler ch)
            throws org.xml.sax.SAXException
  {
    int type = getNodeType(nodeHandle);
    Node node = getNode(nodeHandle);
    dispatchNodeData(node, ch, 0);
  }
  
  /**
   * Retrieve the text content of a DOM subtree, appending it into a
   * user-supplied FastStringBuffer object. Note that attributes are
   * not considered part of the content of an element.
   * <p>
   * There are open questions regarding whitespace stripping. 
   * Currently we make no special effort in that regard, since the standard
   * DOM doesn't yet provide DTD-based information to distinguish
   * whitespace-in-element-context from genuine #PCDATA. Note that we
   * should probably also consider xml:space if/when we address this.
   * DOM Level 3 may solve the problem for us.
   *
   * @param node Node whose subtree is to be walked, gathering the
   * contents of all Text or CDATASection nodes.
   * @param buf FastStringBuffer into which the contents of the text
   * nodes are to be concatenated.
   */
  protected static void dispatchNodeData(Node node, 
                                         org.xml.sax.ContentHandler ch, 
                                         int depth)
            throws org.xml.sax.SAXException
  {

    switch (node.getNodeType())
    {
    case Node.DOCUMENT_FRAGMENT_NODE :
    case Node.DOCUMENT_NODE :
    case Node.ELEMENT_NODE :
    {
      for (Node child = node.getFirstChild(); null != child;
              child = child.getNextSibling())
      {
        dispatchNodeData(child, ch, depth+1);
      }
    }
    break;
    case Node.PROCESSING_INSTRUCTION_NODE : // %REVIEW%
    case Node.COMMENT_NODE :
      if(0 != depth)
        break;
    case Node.TEXT_NODE :
    case Node.CDATA_SECTION_NODE :
    case Node.ATTRIBUTE_NODE :
      String str = node.getNodeValue();
      ch.characters(str.toCharArray(), 0, str.length());
      break;
//    /* case Node.PROCESSING_INSTRUCTION_NODE :
//      // warning(XPATHErrorResources.WG_PARSING_AND_PREPARING);        
//      break; */
    default :
      // ignore
      break;
    }
  }
  
  TreeWalker m_walker = new TreeWalker(null);
  
  /**
   * Directly create SAX parser events from a subtree.
   *
   * @param nodeHandle The node ID.
   * @param ch A non-null reference to a ContentHandler.
   *
   * @throws org.xml.sax.SAXException
   */
  public void dispatchToEvents(int nodeHandle, org.xml.sax.ContentHandler ch)
          throws org.xml.sax.SAXException
  {
    TreeWalker treeWalker = m_walker;
    ContentHandler prevCH = treeWalker.getContentHandler();
    
    if(null != prevCH)
    {
      treeWalker = new TreeWalker(null);
    }
    treeWalker.setContentHandler(ch);
    
    try
    {
      Node node = getNode(nodeHandle);
      treeWalker.traverse(node);
    }
    finally
    {
      treeWalker.setContentHandler(null);
    }
  }

}
