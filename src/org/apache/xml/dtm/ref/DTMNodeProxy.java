/*
 * Copyright 1999-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id$
 */
package org.apache.xml.dtm.ref;

import java.util.Vector;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMDOMException;
import org.apache.xpath.NodeSet;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/**
 * <code>DTMNodeProxy</code> presents a DOM Node API front-end to the DTM model.
 * <p>
 * It does _not_ attempt to address the "node identity" question; no effort
 * is made to prevent the creation of multiple proxies referring to a single
 * DTM node. Users can create a mechanism for managing this, or relinquish the
 * use of "==" and use the .sameNodeAs() mechanism, which is under
 * consideration for future versions of the DOM.
 * <p>
 * DTMNodeProxy may be subclassed further to present specific DOM node types.
 *
 * @see org.w3c.dom
 * @xsl.usage internal
 */
public class DTMNodeProxy
  implements Node, Document, Text, Element, Attr,
                   ProcessingInstruction, Comment, DocumentFragment
{

  /** The DTM for this node. */
  public DTM dtm;

  /** The DTM node handle. */
  int node;
  
  /** The return value as Empty String. */
  private static final String EMPTYSTRING = "";
          
  /** The DOMImplementation object */
  static final DOMImplementation implementation=new DTMNodeProxyImplementation();

  /**
   * Create a DTMNodeProxy Node representing a specific Node in a DTM
   *
   * @param dtm The DTM Reference, must be non-null.
   * @param node The DTM node handle.
   */
  public DTMNodeProxy(DTM dtm, int node)
  {
    this.dtm = dtm;
    this.node = node;
  }

  /**
   * NON-DOM: Return the DTM model
   *
   * @return The DTM that this proxy is a representative for.
   */
  public final DTM getDTM()
  {
    return dtm;
  }

  /**
   * NON-DOM: Return the DTM node number
   *
   * @return The DTM node handle.
   */
  public final int getDTMNodeNumber()
  {
    return node;
  }

  /**
   * Test for equality based on node number.
   *
   * @param node A DTM node proxy reference.
   *
   * @return true if the given node has the same handle as this node.
   */
  public final boolean equals(Node node)
  {

    try
    {
      DTMNodeProxy dtmp = (DTMNodeProxy) node;

      // return (dtmp.node == this.node);
      // Patch attributed to Gary L Peskin <garyp@firstech.com>
      return (dtmp.node == this.node) && (dtmp.dtm == this.dtm);
    }
    catch (ClassCastException cce)
    {
      return false;
    }
  }

  /**
   * Test for equality based on node number.
   *
   * @param node A DTM node proxy reference.
   *
   * @return true if the given node has the same handle as this node.
   */
  public final boolean equals(Object node)
  {

    try
    {

      // DTMNodeProxy dtmp = (DTMNodeProxy)node;
      // return (dtmp.node == this.node);
      // Patch attributed to Gary L Peskin <garyp@firstech.com>
      return equals((Node) node);
    }
    catch (ClassCastException cce)
    {
      return false;
    }
  }

  /**
   * FUTURE DOM: Test node identity, in lieu of Node==Node
   *
   * @param other
   *
   * @return true if the given node has the same handle as this node.
   */
  public final boolean sameNodeAs(Node other)
  {

    if (!(other instanceof DTMNodeProxy))
      return false;

    DTMNodeProxy that = (DTMNodeProxy) other;

    return this.dtm == that.dtm && this.node == that.node;
  }

  /**
   *
   *
   * @see org.w3c.dom.Node
   */
  public final String getNodeName()
  {
    return dtm.getNodeName(node);
  }

  /**
   * A PI's "target" states what processor channel the PI's data
   * should be directed to. It is defined differently in HTML and XML.
   * <p>
   * In XML, a PI's "target" is the first (whitespace-delimited) token
   * following the "<?" token that begins the PI.
   * <p>
   * In HTML, target is always null.
   * <p>
   * Note that getNodeName is aliased to getTarget.
   *
   *
   */
  public final String getTarget()
  {
    return dtm.getNodeName(node);
  }  // getTarget():String

  /**
   *
   *
   * @see org.w3c.dom.Node as of DOM Level 2
   */
  public final String getLocalName()
  {
    return dtm.getLocalName(node);
  }

  /**
   * @return The prefix for this node.
   * @see org.w3c.dom.Node as of DOM Level 2
   */
  public final String getPrefix()
  {
    return dtm.getPrefix(node);
  }

  /**
   *
   * @param prefix
   *
   * @throws DOMException
   * @see org.w3c.dom.Node as of DOM Level 2 -- DTMNodeProxy is read-only
   */
  public final void setPrefix(String prefix) throws DOMException
  {
    throw new DTMDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR);
  }

  /**
   *
   *
   * @see org.w3c.dom.Node as of DOM Level 2
   */
  public final String getNamespaceURI()
  {
    return dtm.getNamespaceURI(node);
  }

  /** Ask whether we support a given DOM feature.
   * In fact, we do not _fully_ support any DOM feature -- we're a
   * read-only subset -- so arguably we should always return false.
   * Or we could say that we support DOM Core Level 2 but all nodes
   * are read-only. Unclear which answer is least misleading.
   * 
   * NON-DOM method. This was present in early drafts of DOM Level 2,
   * but was renamed isSupported. It's present here only because it's
   * cheap, harmless, and might help some poor fool who is still trying
   * to use an early Working Draft of the DOM.
   *
   * @param feature
   * @param version
   *
   * @return false
   */
  public final boolean supports(String feature, String version)
  {
    return implementation.hasFeature(feature,version);
    //throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /** Ask whether we support a given DOM feature.
   * In fact, we do not _fully_ support any DOM feature -- we're a
   * read-only subset -- so arguably we should always return false.
   *
   * @param feature
   * @param version
   *
   * @return false
   * @see org.w3c.dom.Node as of DOM Level 2
   */
  public final boolean isSupported(String feature, String version)
  {
    return implementation.hasFeature(feature,version);
    // throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   *
   *
   *
   * @throws DOMException
   * @see org.w3c.dom.Node
   */
  public final String getNodeValue() throws DOMException
  {
    return dtm.getNodeValue(node);
  }
  
  /**
   * @return The string value of the node
   * 
   * @throws DOMException
   */
  public final String getStringValue() throws DOMException
  {
  	return dtm.getStringValue(node).toString();
  }

  /**
   *
   * @param nodeValue
   *
   * @throws DOMException
   * @see org.w3c.dom.Node -- DTMNodeProxy is read-only
   */
  public final void setNodeValue(String nodeValue) throws DOMException
  {
    throw new DTMDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR);
  }

  /**
   *
   *
   * @see org.w3c.dom.Node
   */
  public final short getNodeType()
  {
    return (short) dtm.getNodeType(node);
  }

  /**
   *
   *
   * @see org.w3c.dom.Node
   */
  public final Node getParentNode()
  {

    if (getNodeType() == Node.ATTRIBUTE_NODE)
      return null;

    int newnode = dtm.getParent(node);

    return (newnode == DTM.NULL) ? null : dtm.getNode(newnode);
  }

  /**
   *
   *
   * @see org.w3c.dom.Node
   */
  public final Node getOwnerNode()
  {

    int newnode = dtm.getParent(node);

    return (newnode == DTM.NULL) ? null : dtm.getNode(newnode);
  }

  /**
   *
   *
   * @see org.w3c.dom.Node
   */
  public final NodeList getChildNodes()
  {
                
    // Annoyingly, AxisIterators do not currently implement DTMIterator, so
    // we can't just wap DTMNodeList around an Axis.CHILD iterator.
    // Instead, we've created a special-case operating mode for that object.
    return new DTMChildIterNodeList(dtm,node);

    // throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   *
   *
   * @see org.w3c.dom.Node
   */
  public final Node getFirstChild()
  {

    int newnode = dtm.getFirstChild(node);

    return (newnode == DTM.NULL) ? null : dtm.getNode(newnode);
  }

  /**
   *
   *
   * @see org.w3c.dom.Node
   */
  public final Node getLastChild()
  {

    int newnode = dtm.getLastChild(node);

    return (newnode == DTM.NULL) ? null : dtm.getNode(newnode);
  }

  /**
   *
   *
   * @see org.w3c.dom.Node
   */
  public final Node getPreviousSibling()
  {

    int newnode = dtm.getPreviousSibling(node);

    return (newnode == DTM.NULL) ? null : dtm.getNode(newnode);
  }

  /**
   *
   *
   * @see org.w3c.dom.Node
   */
  public final Node getNextSibling()
  {

    // Attr's Next is defined at DTM level, but not at DOM level.
    if (dtm.getNodeType(node) == Node.ATTRIBUTE_NODE)
      return null;

    int newnode = dtm.getNextSibling(node);

    return (newnode == DTM.NULL) ? null : dtm.getNode(newnode);
  }

  // DTMNamedNodeMap m_attrs;

  /**
   *
   *
   * @see org.w3c.dom.Node
   */
  public final NamedNodeMap getAttributes()
  {

    return new DTMNamedNodeMap(dtm, node);
  }

  /**
   * Method hasAttribute
   *
   *
   * @param name
   *
   *
   */
  public boolean hasAttribute(String name)
  {
    return DTM.NULL != dtm.getAttributeNode(node,null,name);
  }

  /**
   * Method hasAttributeNS
   *
   *
   * @param name
   * @param x
   *
   *
   */
  public boolean hasAttributeNS(String namespaceURI, String localName)
  {
    return DTM.NULL != dtm.getAttributeNode(node,namespaceURI,localName);
  }

  /**
   *
   *
   * @see org.w3c.dom.Node
   */
  public final Document getOwnerDocument()
  {
  	// Note that this uses the DOM-compatable version of the call
	return (Document)(dtm.getNode(dtm.getOwnerDocument(node)));
  }

  /**
   *
   * @param newChild
   * @param refChild
   *
   *
   *
   * @throws DOMException
   * @see org.w3c.dom.Node -- DTMNodeProxy is read-only
   */
  public final Node insertBefore(Node newChild, Node refChild)
    throws DOMException
  {
    throw new DTMDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR);
  }

  /**
   *
   * @param newChild
   * @param oldChild
   *
   *
   *
   * @throws DOMException
   * @see org.w3c.dom.Node -- DTMNodeProxy is read-only
   */
  public final Node replaceChild(Node newChild, Node oldChild)
    throws DOMException
  {
    throw new DTMDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR);
  }

  /**
   *
   * @param oldChild
   *
   *
   *
   * @throws DOMException
   * @see org.w3c.dom.Node -- DTMNodeProxy is read-only
   */
  public final Node removeChild(Node oldChild) throws DOMException
  {
    throw new DTMDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR);
  }

  /**
   *
   * @param newChild
   *
   *
   *
   * @throws DOMException
   * @see org.w3c.dom.Node -- DTMNodeProxy is read-only
   */
  public final Node appendChild(Node newChild) throws DOMException
  {
    throw new DTMDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR);
  }

  /**
   *
   *
   * @see org.w3c.dom.Node
   */
  public final boolean hasChildNodes()
  {
    return (DTM.NULL != dtm.getFirstChild(node));
  }

  /**
   *
   * @param deep
   *
   *
   * @see org.w3c.dom.Node -- DTMNodeProxy is read-only
   */
  public final Node cloneNode(boolean deep)
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   *
   *
   * @see org.w3c.dom.Document
   */
  public final DocumentType getDoctype()
  {
    return null;
  }

  /**
   *
   *
   * @see org.w3c.dom.Document
   */
  public final DOMImplementation getImplementation()
  {
    return implementation;
  }

  /** This is a bit of a problem in DTM, since a DTM may be a Document
   * Fragment and hence not have a clear-cut Document Element. We can
   * make it work in the well-formed cases but would that be confusing for others?
   * 
   *
   * @see org.w3c.dom.Document
   */
  public final Element getDocumentElement()
  {
		int dochandle=dtm.getDocument();
		int elementhandle=DTM.NULL;
		for(int kidhandle=dtm.getFirstChild(dochandle);
				kidhandle!=DTM.NULL;
				kidhandle=dtm.getNextSibling(kidhandle))
		{
			switch(dtm.getNodeType(kidhandle))
			{
			case Node.ELEMENT_NODE:
				if(elementhandle!=DTM.NULL) 
				{
					elementhandle=DTM.NULL; // More than one; ill-formed.
					kidhandle=dtm.getLastChild(dochandle); // End loop
				}
				else
					elementhandle=kidhandle;
				break;
				
			// These are harmless; document is still wellformed
			case Node.COMMENT_NODE:
			case Node.PROCESSING_INSTRUCTION_NODE:
			case Node.DOCUMENT_TYPE_NODE:
				break;
					
			default:
				elementhandle=DTM.NULL; // ill-formed
				kidhandle=dtm.getLastChild(dochandle); // End loop
				break;
			}
		}
		if(elementhandle==DTM.NULL)
			throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
		else
			return (Element)(dtm.getNode(elementhandle));
  }

  /**
   *
   * @param tagName
   *
   *
   *
   * @throws DOMException
   * @see org.w3c.dom.Document
   */
  public final Element createElement(String tagName) throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   *
   *
   * @see org.w3c.dom.Document
   */
  public final DocumentFragment createDocumentFragment()
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   *
   * @param data
   *
   *
   * @see org.w3c.dom.Document
   */
  public final Text createTextNode(String data)
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   *
   * @param data
   *
   *
   * @see org.w3c.dom.Document
   */
  public final Comment createComment(String data)
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   *
   * @param data
   *
   *
   *
   * @throws DOMException
   * @see org.w3c.dom.Document
   */
  public final CDATASection createCDATASection(String data)
    throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   *
   * @param target
   * @param data
   *
   *
   *
   * @throws DOMException
   * @see org.w3c.dom.Document
   */
  public final ProcessingInstruction createProcessingInstruction(
                                                                 String target, String data) throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   *
   * @param name
   *
   *
   *
   * @throws DOMException
   * @see org.w3c.dom.Document
   */
  public final Attr createAttribute(String name) throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   *
   * @param name
   *
   *
   *
   * @throws DOMException
   * @see org.w3c.dom.Document
   */
  public final EntityReference createEntityReference(String name)
    throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   *
   * @param tagname
   *
   *
   * @see org.w3c.dom.Document
   */
  public final NodeList getElementsByTagName(String tagname) 
  {
       Vector listVector = new Vector();
       Node retNode = dtm.getNode(node);
       if (retNode != null) 
       {
         boolean isTagNameWildCard = "*".equals(tagname);
         if (DTM.ELEMENT_NODE == retNode.getNodeType()) 
         {
           NodeList nodeList = retNode.getChildNodes();
           for (int i = 0; i < nodeList.getLength(); i++) 
           {
             traverseChildren(listVector, nodeList.item(i), tagname,
                              isTagNameWildCard);
           }
         } else if (DTM.DOCUMENT_NODE == retNode.getNodeType()) {
           traverseChildren(listVector, dtm.getNode(node), tagname,
                            isTagNameWildCard);
         }
       }
       int size = listVector.size();
       NodeSet nodeSet = new NodeSet(size);
       for (int i = 0; i < size; i++) 
       {
         nodeSet.addNode((Node) listVector.elementAt(i));
       }
       return (NodeList) nodeSet;
  }
  /**
   * 
   * @param listVector
   * @param tempNode
   * @param tagname
   * @param isTagNameWildCard
   * 
   * 
   * Private method to be used for recursive iterations to obtain elements by tag name.
   */
  private final void traverseChildren
  (
    Vector listVector,
    Node tempNode,
    String tagname,
    boolean isTagNameWildCard) {
    if (tempNode == null) 
    {
      return;
    } 
    else
    { 
      if (tempNode.getNodeType() == DTM.ELEMENT_NODE
            && (isTagNameWildCard || tempNode.getNodeName().equals(tagname)))
      {
        listVector.add(tempNode);
      }
      if(tempNode.hasChildNodes())
      {
        NodeList nodeList = tempNode.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++)
        {
          traverseChildren(listVector, nodeList.item(i), tagname,
                           isTagNameWildCard);
        }
      }
    }
  }

  /**
   *
   * @param importedNode
   * @param deep
   *
   *
   *
   * @throws DOMException
   * @see org.w3c.dom.Document as of DOM Level 2 -- DTMNodeProxy is read-only
   */
  public final Node importNode(Node importedNode, boolean deep)
    throws DOMException
  {
    throw new DTMDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR);
  }

  /**
   *
   * @param namespaceURI
   * @param qualifiedName
   *
   *
   *
   * @throws DOMException
   * @see org.w3c.dom.Document as of DOM Level 2
   */
  public final Element createElementNS(
                                       String namespaceURI, String qualifiedName) throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   *
   * @param namespaceURI
   * @param qualifiedName
   *
   *
   *
   * @throws DOMException
   * @see org.w3c.dom.Document as of DOM Level 2
   */
  public final Attr createAttributeNS(
                                      String namespaceURI, String qualifiedName) throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   *
   * @param namespaceURI
   * @param localName
   *
   *
   * @see org.w3c.dom.Document as of DOM Level 2
   */
  public final NodeList getElementsByTagNameNS(String namespaceURI,
                                               String localName)
  {
    Vector listVector = new Vector();
    Node retNode = dtm.getNode(node);
    if (retNode != null)
    {               
      boolean isNamespaceURIWildCard = "*".equals(namespaceURI);
      boolean isLocalNameWildCard    = "*".equals(localName);
      if (DTM.ELEMENT_NODE == retNode.getNodeType())
      {
        NodeList nodeList = retNode.getChildNodes();                    
        for(int i = 0; i < nodeList.getLength(); i++)
        {
          traverseChildren(listVector, nodeList.item(i), namespaceURI, localName, isNamespaceURIWildCard, isLocalNameWildCard);
        }
      }
      else if(DTM.DOCUMENT_NODE == retNode.getNodeType())
      {
        traverseChildren(listVector, dtm.getNode(node), namespaceURI, localName, isNamespaceURIWildCard, isLocalNameWildCard);
      }
    }
    int size = listVector.size();
    NodeSet nodeSet = new NodeSet(size);
    for (int i = 0; i < size; i++)
    {
      nodeSet.addNode((Node)listVector.elementAt(i));
    }
    return (NodeList) nodeSet;
  }
  /**
   * 
   * @param listVector
   * @param tempNode
   * @param namespaceURI
   * @param localname
   * @param isNamespaceURIWildCard
   * @param isLocalNameWildCard
   * 
   * Private method to be used for recursive iterations to obtain elements by tag name 
   * and namespaceURI.
   */
  private final void traverseChildren
  (
   Vector listVector, 
   Node tempNode, 
   String namespaceURI, 
   String localname,
   boolean isNamespaceURIWildCard,
   boolean isLocalNameWildCard) 
   {
    if (tempNode == null)
    {
      return;
    }
    else 
    {
      if (tempNode.getNodeType() == DTM.ELEMENT_NODE
              && (isLocalNameWildCard
                      || tempNode.getLocalName().equals(localname)))
      {         
        String nsURI = tempNode.getNamespaceURI();
        if ((namespaceURI == null && nsURI == null)
               || isNamespaceURIWildCard
               || (namespaceURI != null && namespaceURI.equals(nsURI)))
        {     
          listVector.add(tempNode); 
        } 
      }
      if(tempNode.hasChildNodes())
      {
        NodeList nl = tempNode.getChildNodes();                 
        for(int i = 0; i < nl.getLength(); i++)
        {
          traverseChildren(listVector, nl.item(i), namespaceURI, localname,
                           isNamespaceURIWildCard, isLocalNameWildCard);
        }
      }
    }
  }
  /**
   *
   * @param elementId
   *
   *
   * @see org.w3c.dom.Document as of DOM Level 2
   */
  public final Element getElementById(String elementId)
  {
       return (Element) dtm.getNode(dtm.getElementById(elementId));
  }

  /**
   *
   * @param offset
   *
   *
   *
   * @throws DOMException
   * @see org.w3c.dom.Text
   */
  public final Text splitText(int offset) throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   *
   *
   *
   * @throws DOMException
   * @see org.w3c.dom.CharacterData
   */
  public final String getData() throws DOMException
  {
    return dtm.getNodeValue(node);
  }

  /**
   *
   * @param data
   *
   * @throws DOMException
   * @see org.w3c.dom.CharacterData
   */
  public final void setData(String data) throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   *
   *
   * @see org.w3c.dom.CharacterData
   */
  public final int getLength()
  {
    // %OPT% This should do something smarter?
    return dtm.getNodeValue(node).length();
  }

  /**
   *
   * @param offset
   * @param count
   *
   *
   *
   * @throws DOMException
   * @see org.w3c.dom.CharacterData
   */
  public final String substringData(int offset, int count) throws DOMException
  {
    return getData().substring(offset,offset+count);
  }

  /**
   *
   * @param arg
   *
   * @throws DOMException
   * @see org.w3c.dom.CharacterData
   */
  public final void appendData(String arg) throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   *
   * @param offset
   * @param arg
   *
   * @throws DOMException
   * @see org.w3c.dom.CharacterData
   */
  public final void insertData(int offset, String arg) throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   *
   * @param offset
   * @param count
   *
   * @throws DOMException
   * @see org.w3c.dom.CharacterData
   */
  public final void deleteData(int offset, int count) throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   *
   * @param offset
   * @param count
   * @param arg
   *
   * @throws DOMException
   * @see org.w3c.dom.CharacterData
   */
  public final void replaceData(int offset, int count, String arg)
    throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   *
   *
   * @see org.w3c.dom.Element
   */
  public final String getTagName()
  {
    return dtm.getNodeName(node);
  }

  /**
   *
   * @param name
   *
   *
   * @see org.w3c.dom.Element
   */
  public final String getAttribute(String name)
  {

    DTMNamedNodeMap  map = new DTMNamedNodeMap(dtm, node);
    Node node = map.getNamedItem(name);
    return (null == node) ? EMPTYSTRING : node.getNodeValue();
  }

  /**
   *
   * @param name
   * @param value
   *
   * @throws DOMException
   * @see org.w3c.dom.Element
   */
  public final void setAttribute(String name, String value)
    throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   *
   * @param name
   *
   * @throws DOMException
   * @see org.w3c.dom.Element
   */
  public final void removeAttribute(String name) throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   *
   * @param name
   *
   *
   * @see org.w3c.dom.Element
   */
  public final Attr getAttributeNode(String name)
  {

    DTMNamedNodeMap  map = new DTMNamedNodeMap(dtm, node);
    return (Attr)map.getNamedItem(name);
  }

  /**
   *
   * @param newAttr
   *
   *
   *
   * @throws DOMException
   * @see org.w3c.dom.Element
   */
  public final Attr setAttributeNode(Attr newAttr) throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   *
   * @param oldAttr
   *
   *
   *
   * @throws DOMException
   * @see org.w3c.dom.Element
   */
  public final Attr removeAttributeNode(Attr oldAttr) throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   * Introduced in DOM Level 2.
   *
   *
   */
  public boolean hasAttributes()
  {
    return DTM.NULL != dtm.getFirstAttribute(node);
  }

  /** @see org.w3c.dom.Element */
  public final void normalize()
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   *
   * @param namespaceURI
   * @param localName
   *
   *
   * @see org.w3c.dom.Element
   */
  public final String getAttributeNS(String namespaceURI, String localName)
  {
       Node retNode = null;
       int n = dtm.getAttributeNode(node,namespaceURI,localName);
       if(n != DTM.NULL)
               retNode = dtm.getNode(n);
       return (null == retNode) ? EMPTYSTRING : retNode.getNodeValue();
  }

  /**
   *
   * @param namespaceURI
   * @param qualifiedName
   * @param value
   *
   * @throws DOMException
   * @see org.w3c.dom.Element
   */
  public final void setAttributeNS(
                                   String namespaceURI, String qualifiedName, String value)
    throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   *
   * @param namespaceURI
   * @param localName
   *
   * @throws DOMException
   * @see org.w3c.dom.Element
   */
  public final void removeAttributeNS(String namespaceURI, String localName)
    throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   *
   * @param namespaceURI
   * @param localName
   *
   *
   * @see org.w3c.dom.Element
   */
  public final Attr getAttributeNodeNS(String namespaceURI, String localName)
  {
       Attr retAttr = null;
       int n = dtm.getAttributeNode(node,namespaceURI,localName);
       if(n != DTM.NULL)
               retAttr = (Attr) dtm.getNode(n);
       return retAttr;
  }

  /**
   *
   * @param newAttr
   *
   *
   *
   * @throws DOMException
   * @see org.w3c.dom.Element
   */
  public final Attr setAttributeNodeNS(Attr newAttr) throws DOMException
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   *
   *
   * @see org.w3c.dom.Attr
   */
  public final String getName()
  {
    return dtm.getNodeName(node);
  }

  /**
   *
   *
   * @see org.w3c.dom.Attr
   */
  public final boolean getSpecified()
  {
    // We really don't know which attributes might have come from the
    // source document versus from the DTD. Treat them all as having
    // been provided by the user.
    // %REVIEW% if/when we become aware of DTDs/schemae.
    return true;
  }

  /**
   *
   *
   * @see org.w3c.dom.Attr
   */
  public final String getValue()
  {
    return dtm.getNodeValue(node);
  }

  /**
   *
   * @param value
   * @see org.w3c.dom.Attr
   */
  public final void setValue(String value)
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   * Get the owner element of an attribute.
   *
   *
   * @see org.w3c.dom.Attr as of DOM Level 2
   */
  public final Element getOwnerElement()
  {
    if (getNodeType() != Node.ATTRIBUTE_NODE)
      return null;
    // In XPath and DTM data models, unlike DOM, an Attr's parent is its
    // owner element.
    int newnode = dtm.getParent(node);
    return (newnode == DTM.NULL) ? null : (Element)(dtm.getNode(newnode));
  }

  /**
   * NEEDSDOC Method adoptNode 
   *
   *
   * NEEDSDOC @param source
   *
   *
   *
   * @throws DOMException
   */
  public Node adoptNode(Node source) throws DOMException
  {

    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   * <p>EXPERIMENTAL! Based on the <a
   * href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010605'>Document
   * Object Model (DOM) Level 3 Core Working Draft of 5 June 2001.</a>.
   * <p>
   * An attribute specifying, as part of the XML declaration, the encoding
   * of this document. This is <code>null</code> when unspecified.
   * @since DOM Level 3
   *
   *
   */
  public String getEncoding()
  {

    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   * <p>EXPERIMENTAL! Based on the <a
   * href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010605'>Document
   * Object Model (DOM) Level 3 Core Working Draft of 5 June 2001.</a>.
   * <p>
   * An attribute specifying, as part of the XML declaration, the encoding
   * of this document. This is <code>null</code> when unspecified.
   * @since DOM Level 3
   *
   * NEEDSDOC @param encoding
   */
  public void setEncoding(String encoding)
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   * <p>EXPERIMENTAL! Based on the <a
   * href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010605'>Document
   * Object Model (DOM) Level 3 Core Working Draft of 5 June 2001.</a>.
   * <p>
   * An attribute specifying, as part of the XML declaration, whether this
   * document is standalone.
   * @since DOM Level 3
   *
   *
   */
  public boolean getStandalone()
  {

    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   * <p>EXPERIMENTAL! Based on the <a
   * href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010605'>Document
   * Object Model (DOM) Level 3 Core Working Draft of 5 June 2001.</a>.
   * <p>
   * An attribute specifying, as part of the XML declaration, whether this
   * document is standalone.
   * @since DOM Level 3
   *
   * NEEDSDOC @param standalone
   */
  public void setStandalone(boolean standalone)
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   * <p>EXPERIMENTAL! Based on the <a
   * href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010605'>Document
   * Object Model (DOM) Level 3 Core Working Draft of 5 June 2001.</a>.
   * <p>
   * An attribute specifying whether errors checking is enforced or not.
   * When set to <code>false</code>, the implementation is free to not
   * test every possible error case normally defined on DOM operations,
   * and not raise any <code>DOMException</code>. In case of error, the
   * behavior is undefined. This attribute is <code>true</code> by
   * defaults.
   * @since DOM Level 3
   *
   *
   */
  public boolean getStrictErrorChecking()
  {

    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   * <p>EXPERIMENTAL! Based on the <a
   * href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010605'>Document
   * Object Model (DOM) Level 3 Core Working Draft of 5 June 2001.</a>.
   * <p>
   * An attribute specifying whether errors checking is enforced or not.
   * When set to <code>false</code>, the implementation is free to not
   * test every possible error case normally defined on DOM operations,
   * and not raise any <code>DOMException</code>. In case of error, the
   * behavior is undefined. This attribute is <code>true</code> by
   * defaults.
   * @since DOM Level 3
   *
   * NEEDSDOC @param strictErrorChecking
   */
  public void setStrictErrorChecking(boolean strictErrorChecking)
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   * <p>EXPERIMENTAL! Based on the <a
   * href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010605'>Document
   * Object Model (DOM) Level 3 Core Working Draft of 5 June 2001.</a>.
   * <p>
   * An attribute specifying, as part of the XML declaration, the version
   * number of this document. This is <code>null</code> when unspecified.
   * @since DOM Level 3
   *
   *
   */
  public String getVersion()
  {

    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }

  /**
   * <p>EXPERIMENTAL! Based on the <a
   * href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010605'>Document
   * Object Model (DOM) Level 3 Core Working Draft of 5 June 2001.</a>.
   * <p>
   * An attribute specifying, as part of the XML declaration, the version
   * number of this document. This is <code>null</code> when unspecified.
   * @since DOM Level 3
   *
   * NEEDSDOC @param version
   */
  public void setVersion(String version)
  {
    throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
  }
        
        
  /** Inner class to support getDOMImplementation.
   */
  static class DTMNodeProxyImplementation implements DOMImplementation
  {
    public DocumentType createDocumentType(String qualifiedName,String publicId, String systemId)
    {
      throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);
    }
    public Document createDocument(String namespaceURI,String qualfiedName,DocumentType doctype)                        
    {
      // Could create a DTM... but why, when it'd have to be permanantly empty?
      throw new DTMDOMException(DOMException.NOT_SUPPORTED_ERR);        
    }
    /** Ask whether we support a given DOM feature.
     * 
     * In fact, we do not _fully_ support any DOM feature -- we're a
     * read-only subset -- so arguably we should always return false.
     * On the other hand, it may be more practically useful to return
     * true and simply treat the whole DOM as read-only, failing on the
     * methods we can't support. I'm not sure which would be more useful
     * to the caller.
     */
    public boolean hasFeature(String feature,String version)
    {
      if( ("CORE".equals(feature.toUpperCase()) || "XML".equals(feature.toUpperCase())) 
					&& 
          ("1.0".equals(version) || "2.0".equals(version))
          )
        return true;
      return false;
    }
  }
}
