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
package org.apache.xalan.dtm;
import org.w3c.dom.*;

/**
 * <meta name="usage" content="internal"/>
 * <code>DTMProxy</code> presents a DOM Node API front-end to the DTM model.
 * <p>
 * It does _not_ attempt to address the "node identity" question; no effort
 * is made to prevent the creation of multiple proxies referring to a single
 * DTM node. Users can create a mechanism for managing this, or relinquish the
 * use of "==" and use the .sameNodeAs() mechanism, which is under
 * consideration for future versions of the DOM.
 * <p>
 * DTMProxy may be subclassed further to present specific DOM node types.
 *
 * @see org.w3c.dom
 */
public class DTMProxy
  implements Node, Document, Text, Element, Attr, ProcessingInstruction, Comment
{
  public DTM dtm;
  int node;

  /** Create a DTMProxy Node representing a specific Node in a DTM */
  DTMProxy(DTM dtm,int node,int type)
  {
    this.dtm=dtm;
    this.node=node;
  }

  /** Create a DTMProxy Node representing a specific Node in a DTM */
  DTMProxy(DTM dtm,int node)
  {
    this.dtm=dtm;
    this.node=node;
  }

  /** Create a DTMProxy Node representing the Document Node in a DTM */
  DTMProxy(DTM dtm)
  {
    this(dtm,0, Node.DOCUMENT_NODE);
  }
  
  /** NON-DOM: Return the DTM model */
  public final DTM getDTM()
  {
    return dtm;
  }
  
  /** NON-DOM: Return the DTM node number */
  public final int getDTMNodeNumber()
  {
    return node;
  }
  
  /**
   * Test for equality based on node number.
   */
  public final boolean equals(Node node)
  {
    try
    {
      DTMProxy dtmp = (DTMProxy)node;
      return (dtmp.node == this.node);
    }
    catch(ClassCastException cce)
    {
      return false;
    }
  }

  /**
   * Test for equality based on node number.
   */
  public final boolean equals(Object node)
  {
    try
    {
      DTMProxy dtmp = (DTMProxy)node;
      return (dtmp.node == this.node);
    }
    catch(ClassCastException cce)
    {
      return false;
    }
  }

  /** FUTURE DOM: Test node identity, in lieu of Node==Node */
  public final boolean sameNodeAs(Node other)
  {
    if(! (other instanceof DTMProxy) )
      return false;
    DTMProxy that=(DTMProxy)other;
    return this.dtm==that.dtm && this.node==that.node;
  }
  
  /** @see org.w3c.dom.Node */
  public final String             getNodeName()
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
   */
  public final String getTarget() 
  {
    return dtm.getNodeName(node);
  } // getTarget():String
  
  /** @see org.w3c.dom.Node as of DOM Level 2*/
  public final String             getLocalName()
  {
    return dtm.getLocalName(node);
  }
  
  /** @see org.w3c.dom.Node as of DOM Level 2*/
  public final String             getPrefix()
  {
    return dtm.getPrefix(node);
  }
  
  /** @see org.w3c.dom.Node as of DOM Level 2 -- DTMProxy is read-only */
  public final void               setPrefix(String prefix)
    throws DOMException
  {
    throw new DTMException(DTMException.NO_MODIFICATION_ALLOWED_ERR);
  }
  
  /** @see org.w3c.dom.Node as of DOM Level 2*/
  public final String             getNamespaceURI()
  {
    return dtm.getNamespaceURI(node);
  }
  
  /** @see org.w3c.dom.Node as of DOM Level 2 */
  public final boolean            supports(String feature, 
                                           String version)
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }

  /** @see org.w3c.dom.Node */
  public final String  getNodeValue()                                                 
    throws DOMException
  {
    // ***** ASSUMPTION: ATTRIBUTES HAVE SINGLE TEXT-NODE CHILD.
    // (SIMILAR ASSUMPTION CURRENTLY MADE IN DTM; BE SURE TO
    // REVISIT THIS IF THAT CHANGES!)
    if (getNodeType()==Node.ATTRIBUTE_NODE)
      return dtm.getNodeValue(node+1);
    return dtm.getNodeValue(node);
  }

  /** @see org.w3c.dom.Node -- DTMProxy is read-only */
  public final void               setNodeValue(String nodeValue)
    throws DOMException
  {
    throw new DTMException(DTMException.NO_MODIFICATION_ALLOWED_ERR);
  }

  /** @see org.w3c.dom.Node */
  public final short              getNodeType()
  {
    return (short) dtm.getNodeType(node);
  }
  
  /** @see org.w3c.dom.Node */
  public final Node               getParentNode()
  {
    if(getNodeType() == Node.ATTRIBUTE_NODE)
      return null;
    int newnode=dtm.getParent(node);
    return (newnode==-1) ? null : dtm.getNode(newnode);
  }

  /** @see org.w3c.dom.Node */
  public final Node               getOwnerNode()
  {
    int newnode=dtm.getParent(node);
    return (newnode==-1) ? null : dtm.getNode(newnode);
  }

  /** @see org.w3c.dom.Node */
  public final NodeList           getChildNodes()
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }
  
  /** @see org.w3c.dom.Node */
  public final Node               getFirstChild()
  {
    int newnode=dtm.getFirstChild(node);
    return (newnode==-1) ? null : dtm.getNode(newnode);
  }
  
  /** @see org.w3c.dom.Node */
  public final Node               getLastChild()
  {
    int newnode=dtm.getLastChild(node);
    return (newnode==-1) ? null : dtm.getNode(newnode);
  }
  
  /** @see org.w3c.dom.Node */
  public final Node               getPreviousSibling()
  {
    int newnode=dtm.getPreviousSibling(node);
    return (newnode==-1) ? null : dtm.getNode(newnode);
  }
  
  /** @see org.w3c.dom.Node */
  public final Node               getNextSibling()
  {
    // Attr's Next is defined at DTM level, but not at DOM level.
    if(dtm.getNodeType(node)==Node.ATTRIBUTE_NODE)
      return null;
    int newnode=dtm.getNextSibling(node);
    return (newnode==-1) ? null : dtm.getNode(newnode);
  }

  /** @see org.w3c.dom.Node */
  public final NamedNodeMap       getAttributes()
  {
    return new DTMProxyMap(dtm,node);
  }

  /** @see org.w3c.dom.Node */
  public final Document           getOwnerDocument()
  {
    return dtm.getDocument();
  }
  
  /** @see org.w3c.dom.Node -- DTMProxy is read-only */
  public final Node               insertBefore(Node newChild, 
                                         Node refChild)
    throws DOMException
  {
    throw new DTMException(DTMException.NO_MODIFICATION_ALLOWED_ERR);
  }

  /** @see org.w3c.dom.Node -- DTMProxy is read-only */
  public final Node               replaceChild(Node newChild, 
                                         Node oldChild)
    throws DOMException
  {
    throw new DTMException(DTMException.NO_MODIFICATION_ALLOWED_ERR);
  }

  /** @see org.w3c.dom.Node -- DTMProxy is read-only */
  public final Node               removeChild(Node oldChild)
    throws DOMException
  {
    throw new DTMException(DTMException.NO_MODIFICATION_ALLOWED_ERR);
  }

  /** @see org.w3c.dom.Node -- DTMProxy is read-only */
  public final Node               appendChild(Node newChild)
    throws DOMException
  {
    throw new DTMException(DTMException.NO_MODIFICATION_ALLOWED_ERR);
  }

  /** @see org.w3c.dom.Node */
  public final boolean            hasChildNodes()
  {
    return (-1 != dtm.getFirstChild(node));
  }
  
  /** @see org.w3c.dom.Node -- DTMProxy is read-only */
  public final Node               cloneNode(boolean deep)
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }
  
  /** @see org.w3c.dom.Document */
  public final DocumentType       getDoctype()
  {
    return null;
  }

  /** @see org.w3c.dom.Document */
  public final DOMImplementation  getImplementation()
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }

  /** @see org.w3c.dom.Document */
  public final Element            getDocumentElement()
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }

  /** @see org.w3c.dom.Document */
  public final Element            createElement(String tagName)
    throws DOMException
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }

  /** @see org.w3c.dom.Document */
  public final DocumentFragment   createDocumentFragment()
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }

  /** @see org.w3c.dom.Document */
  public final Text               createTextNode(String data)
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }

  /** @see org.w3c.dom.Document */
  public final Comment            createComment(String data)
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }

  /** @see org.w3c.dom.Document */
  public final CDATASection       createCDATASection(String data)
    throws DOMException
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }

  /** @see org.w3c.dom.Document */
  public final ProcessingInstruction createProcessingInstruction(String target, 
                                                           String data)
    throws DOMException
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }

  /** @see org.w3c.dom.Document */
  public final Attr               createAttribute(String name)
    throws DOMException
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }

  /** @see org.w3c.dom.Document */
  public final EntityReference    createEntityReference(String name)
    throws DOMException
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }

  /** @see org.w3c.dom.Document */
  public final NodeList           getElementsByTagName(String tagname)
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }
  
  /** @see org.w3c.dom.Document as of DOM Level 2 -- DTMProxy is read-only*/
  public final Node               importNode(Node importedNode, 
                                             boolean deep)
    throws DOMException
  {
    throw new DTMException(DTMException.NO_MODIFICATION_ALLOWED_ERR);
  }

  /** @see org.w3c.dom.Document as of DOM Level 2 */
  public final Element            createElementNS(String namespaceURI, 
                                                  String qualifiedName)
    throws DOMException
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }
  
  /** @see org.w3c.dom.Document as of DOM Level 2 */
  public final Attr               createAttributeNS(String namespaceURI, 
                                                    String qualifiedName)
    throws DOMException
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }
  
  /** @see org.w3c.dom.Document as of DOM Level 2 */
  public final NodeList           getElementsByTagNameNS(String namespaceURI, 
                                                         String localName)
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }
  
  /** @see org.w3c.dom.Document as of DOM Level 2 */
  public final Element            getElementById(String elementId)
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }
  
  /** @see org.w3c.dom.Text */
  public final Text               splitText(int offset)
    throws DOMException
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }

  /** @see org.w3c.dom.CharacterData */
  public final String             getData()
    throws DOMException
  {
    return dtm.getNodeValue(node);
  }

  /** @see org.w3c.dom.CharacterData */
  public final void               setData(String data)
    throws DOMException
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }

  /** @see org.w3c.dom.CharacterData */
  public final int                getLength()
  {
    // %%FIX: This should do something smarter?
    return dtm.getNodeValue(node).length();
  }

  /** @see org.w3c.dom.CharacterData */
  public final String             substringData(int offset, 
                                          int count)
    throws DOMException
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }

  /** @see org.w3c.dom.CharacterData */
  public final void               appendData(String arg)
    throws DOMException
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }

  /** @see org.w3c.dom.CharacterData */
  public final void               insertData(int offset, 
                                       String arg)
    throws DOMException
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }

  /** @see org.w3c.dom.CharacterData */
  public final void               deleteData(int offset, 
                                       int count)
    throws DOMException
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }

  /** @see org.w3c.dom.CharacterData */
  public final void               replaceData(int offset, 
                                        int count, 
                                        String arg)
    throws DOMException
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }
  
  /** @see org.w3c.dom.Element */
  public final String             getTagName()
  {
    return dtm.getNodeName(node);
  }

  /** @see org.w3c.dom.Element */
  public final String             getAttribute(String name)
  { 
    DTMProxyMap  map = new DTMProxyMap(dtm, node);
    Node node = map.getNamedItem(name);
    return (null == node) ? null : node.getNodeValue();
  }

  /** @see org.w3c.dom.Element */
  public final void               setAttribute(String name, 
                                         String value)
    throws DOMException
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }

  /** @see org.w3c.dom.Element */
  public final void               removeAttribute(String name)
    throws DOMException
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }
  
  /** @see org.w3c.dom.Element */
  public final Attr               getAttributeNode(String name)
  {
    DTMProxyMap  map = new DTMProxyMap(dtm, node);
    return (Attr)map.getNamedItem(name);
  }

  /** @see org.w3c.dom.Element */
  public final Attr               setAttributeNode(Attr newAttr)
    throws DOMException
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }

  /** @see org.w3c.dom.Element */
  public final Attr               removeAttributeNode(Attr oldAttr)
    throws DOMException
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }

  /** @see org.w3c.dom.Element */
  public final void               normalize()
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }

  public boolean hasAttribute(String name)
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
    // return false;
  }

  public boolean hasAttributeNS(String name, String x)
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
    // return false;
  }
  
  /** @see org.w3c.dom.Element */
  public final String             getAttributeNS(String namespaceURI, 
                                                 String localName)
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }

  /** @see org.w3c.dom.Element */
  public final void               setAttributeNS(String namespaceURI, 
                                                 String qualifiedName, 
                                                 String value)
    throws DOMException
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }

  /** @see org.w3c.dom.Element */
  public final void               removeAttributeNS(String namespaceURI, 
                                                    String localName)
    throws DOMException
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }

  /** @see org.w3c.dom.Element */
  public final Attr               getAttributeNodeNS(String namespaceURI, 
                                                     String localName)
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }
  
  /** @see org.w3c.dom.Element */
  public final Attr               setAttributeNodeNS(Attr newAttr)
    throws DOMException
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }
  
  /** @see org.w3c.dom.Attr */
  public final String             getName()
  {
    return dtm.getNodeName(node);
  }

  /** @see org.w3c.dom.Attr */
  public final boolean            getSpecified()
  {
    // %%FIX
    return true;
  }

  /** @see org.w3c.dom.Attr */
  public final String             getValue()
  {
    return dtm.getNodeValue(node+1);
  }
  
  /** @see org.w3c.dom.Attr */
  public final void               setValue(String value)
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }

  /** @see org.w3c.dom.Attr as of DOM Level 2 */ 
  public final Element            getOwnerElement()
  {
    throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
  }

}