package org.apache.xalan.stree;

import org.apache.xalan.utils.QName;

import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;


public class ElementImpl extends Parent implements Attributes, NamedNodeMap
  
{
  private String m_name;
  private short attrsEnd;
  
  ElementImpl (String name)
  {
    m_name = name;    
  }

  ElementImpl (String name, Attributes atts)
  {
    m_name = name;
    setAttributes(atts);
  }

  /** 
   * A short integer indicating what type of node this is. The named
   * constants for this value are defined in the org.w3c.dom.Node interface.
   */
  public short getNodeType() {
    return Node.ELEMENT_NODE;
  }

  /** Returns the node name. */
  public String getNodeName() 
  {
    return m_name;
  }
  
  /**
   * Returns the local part of the qualified name of this node.
   * <br>For nodes created with a DOM Level 1 method, such as 
   * <code>createElement</code> from the <code>Document</code> interface, 
   * it is <code>null</code>.
   * @since DOM Level 2
   */
  public String       getLocalName()
  {
    return m_name;
  }
  
  /**
   * Returns the tag name of this node.
   * <br>For nodes created with a DOM Level 1 method, such as 
   * <code>createElement</code> from the <code>Document</code> interface, 
   * it is <code>null</code>.
   * @since DOM Level 2
   */
  public String getTagName()
  {
    return m_name;
  }
  
  /**
   * The first child of this node. If there is no such node, this returns
   * Factor in any existing attribute nodes. 
   * <code>null</code>.
   */
  public Node         getFirstChild()
  {
    // The call to getAttrCount gets the number of attributes in the list.
    // The attributes are put in the list before the actual children.
    // Force attributes to be added first!
    int attrs = getAttrCount();
    return (getChildCount() == 0) ? null : getChild(attrs);
  }
      
  /**
   * Get the nth attribute child.
   * @param i the index of the child.
   * @exception ArrayIndexOutOfBoundsException if the index is out of bounds.
   * @exception NullPointerException if there are no children.
   */
  public AttrImpl getChildAttribute(int i)
    throws ArrayIndexOutOfBoundsException, NullPointerException
  {
    // wait?
    if (i < getAttrCount() && i >= 0) 
      return (AttrImpl)m_children[i];
    else
      return null;
  }
  
  /**
   * Get the number of children this node currently contains.
   * Factor in the number of attributes at beginning of list.
   * Note that this will only return the number of children 
   * added so far.  If the isComplete property is false, 
   * it is likely that more children will be added.
   */
  public int getChildCount()
  {
    if (null == m_children || !isComplete())
    {
      synchronized (this)
      {
        try
        {
          //System.out.println("Waiting... getelCount " );
          wait();
        }
        catch (InterruptedException e)
        {
          // That's OK, it's as good a time as any to check again
        }
        //System.out.println("/// gotelcount " );
        
      }
    }
    return (null == m_children) ? 0 : m_children.length - getAttrCount();
  }
  
  
  /**
   * Get attributes of this node.
   * <br>For nodes created with a DOM Level 1 method, such as 
   * <code>createElement</code> from the <code>Document</code> interface, 
   * it is <code>null</code>.
   * @since DOM Level 2
   */
  public NamedNodeMap       getAttributes()
  {
    return this;
  }
  
  /**
   * Set attributes of this node.
   * <br>For nodes created with a DOM Level 1 method, such as 
   * <code>createElement</code> from the <code>Document</code> interface, 
   * it is <code>null</code>.
   * @since DOM Level 2
   */
  public void               setAttribute(String name,
                                         String value)
    throws DOMException
  {
    AttrImpl attr = (AttrImpl)createAttribute(name); 
    attr.setValue(value); 
    
  }
  
  /**
   * Set attributes of this node.
   * <br>For nodes created with a DOM Level 1 method, such as 
   * <code>createElement</code> from the <code>Document</code> interface, 
   * it is <code>null</code>.
   * @since DOM Level 2
   */  
  public void               setAttributeNS(String namespaceURI,
                                           String qualifiedName,
                                           String value)
    throws DOMException
  {
    AttrImplNS attr = (AttrImplNS)createAttributeNS(namespaceURI, qualifiedName);
    attr.setValue(value);
    
  }
  
  /**
   * Set a list of attributes of this node.
   * <br>For nodes created with a DOM Level 1 method, such as 
   * <code>createElement</code> from the <code>Document</code> interface, 
   * it is <code>null</code>.
   * @since DOM Level 2
   */ 
  public void               setAttributes(Attributes atts)
    throws DOMException
  {
    for(int i=0; i< atts.getLength(); i++)
    {
      String uri = atts.getURI(i);
      String name = atts.getQName(i);
      AttrImpl attr;
      if (null != uri || name.indexOf(':') >0)
        attr = (AttrImplNS)createAttributeNS(uri, name);
      else        
        attr = (AttrImpl)createAttribute(name); 
      
      attr.setValue(atts.getValue(i));             
    } 
    
  }
  
  public void setIDAttribute(String namespaceURI,
                                           String qualifiedName,
                                           String value)
  {
    getDocumentImpl().setIDAttribute(namespaceURI, qualifiedName, value, this);
  }
  
  /**
   *  Create an attribute node. 
   */
  public Attr               createAttribute(String name)
    throws DOMException
  {
    // System.out.println("name: "+name);
    AttrImpl attrImpl;
    if(QName.isXMLNSDecl(name))
    {
      attrImpl = new NameSpaceDecl("http://www.w3.org/2000/xmlns/", 
                                   name, "");
    }
    else
      attrImpl = new AttrImpl(name, "");
    boolean found = false;
    for (int i = 0; i < attrsEnd; i++)
    {
      AttrImpl attr = (AttrImpl)m_children[i];
      if (attr.getNodeName().equals(name))
      {  
        m_children[i] = attrImpl;
        found = true;
        break;
      }
    } 
    if (!found)
    {          
      appendChild(attrImpl);
      attrsEnd++;
      
    }       
    return (Attr)attrImpl;    
  }
  
  /** 
   * Create an attribute node with a namespace . 
   */
  public Attr               createAttributeNS(String namespaceURI,
                                              String qualifiedName)
    throws DOMException
  {
    // System.out.println("qualifiedName: "+qualifiedName);
    AttrImplNS attrImpl = new AttrImplNS(namespaceURI, qualifiedName, "");
    boolean found = false;
    for (int i = 0; i < attrsEnd; i++)
    {
      AttrImpl attr = (AttrImpl)m_children[i];
      if (attr.getLocalName().equals(attrImpl.getLocalName()) &&
            attr.getNamespaceURI().equals(attrImpl.getNamespaceURI()))
      {  
        m_children[i] = attrImpl;
        found = true;
        break;
      }
    } 
    if (!found)
    {          
      appendChild(attrImpl);
      attrsEnd++;      
    } 
    return (Attr)attrImpl;    
  }
  
  //
  //implement Attributes Interface
  //
  
    /**
     * Return the number of attributes in the list.
     *
     * @return The number of attributes in the list.
     */
    public int getAttrCount ()
    {
      if (null == m_children && !isComplete())
      {
        // Force it to wait until children have been added
        int count = getChildCount(); 
      }
      return attrsEnd;
    }
      


    /**
     * Look up an attribute's Namespace URI by index.
     *
     * @param index The attribute index (zero-based).
     * @return The Namespace URI, or the empty string if none
     *         is available, or null if the index is out of
     *         range.
     */
    public String getURI (int index)
    {
      AttrImpl attr = getChildAttribute(index);
      if (null != attr)
        return attr.getNamespaceURI();
      else
        return null;
    }


    /**
     * Look up an attribute's local name by index.
     *
     * @param index The attribute index (zero-based).
     * @return The local name, or the empty string if Namespace
     *         processing is not being performed, or null
     *         if the index is out of range.
     */
    public  String getLocalName (int index)
    {
      AttrImpl attr = getChildAttribute(index);
      if (null != attr)
        return attr.getLocalName();
      else
        return null;
    }  


    /**
     * Look up an attribute's raw XML 1.0 name by index.
     *
     * @param index The attribute index (zero-based).
     * @return The raw XML 1.0 name, or the empty string
     *         if none is available, or null if the index
     *         is out of range.
     */
    public String getQName (int index)
    {
      AttrImpl attr = getChildAttribute(index);
      if (null != attr)
        return attr.getNodeName();
      else
        return null;
    }  


    /**
     * Look up an attribute's type by index.
     *
     * @param index The attribute index (zero-based).
     * @return The attribute's type as a string, or null if the
     *         index is out of range.
     */
    public String getType (int index)
    {
      AttrImpl attr = getChildAttribute(index);
      if (null != attr)
        return Integer.toString(attr.getNodeType());
      else
        return null;
    } 

    /**
     * Look up an attribute's value by index.
     *
     * @param index The attribute index (zero-based).
     * @return The attribute's value as a string, or null if the
     *         index is out of range.
     */
    public String getValue (int index)
    {
      AttrImpl attr = getChildAttribute(index);
      if (null != attr)
        return attr.getValue();
      else 
        return null;
    }
    
    /**
     * Look up an attribute's value by name.
     *
     * @param index The attribute index (zero-based).
     * @return The attribute's value as a string, or null if the
     *         index is out of range.
     */
  public String             getAttribute(String name)
  {
    return getValue(name);
  }


    ////////////////////////////////////////////////////////////////////
    // Name-based query.
    ////////////////////////////////////////////////////////////////////


    /**
     * Look up the index of an attribute by Namespace name.
     *
     * @param uri The Namespace URI, or the empty string if
     *        the name has no Namespace URI.
     * @param localName The attribute's local name.
     * @return The index of the attribute, or -1 if it does not
     *         appear in the list.
     */
    public int getIndex (String uri, String localPart)
    {
      for (int i = 0; i < getAttrCount(); i++)
      {
        AttrImpl attr = (AttrImpl)getChildAttribute(i);
        if (attr.getLocalName().equals(localPart) &&
            attr.getNamespaceURI().equals(uri))
          return i;
      }
      return -1;
    } 

    /**
     * Look up the index of an attribute by raw XML 1.0 name.
     *
     * @param rawName The raw (prefixed) name.
     * @return The index of the attribute, or -1 if it does not
     *         appear in the list.
     */
    public int getIndex (String rawName)
    {
      for (int i = 0; i < getAttrCount(); i++)
      {
        AttrImpl attr = getChildAttribute(i);
        if (attr.getNodeName().equals(rawName))
          return i;
      }
      return -1;
    }  

    /**
     * Look up an attribute's type by Namespace name.
     *
     * @param uri The Namespace URI, or the empty String if the
     *        name has no Namespace URI.
     * @param localName The local name of the attribute.
     * @return The attribute type as a string, or null if the
     *         attribute is not in the list or if Namespace
     *         processing is not being performed.
     */
    public String getType (String uri, String localName)
    {
      for (int i = 0; i < getAttrCount(); i++)
      {
        AttrImpl attr = (AttrImpl)getChildAttribute(i);
        if (attr.getLocalName().equals(localName) &&
            attr.getNamespaceURI().equals(uri))
          return Integer.toString(attr.getNodeType());
      }
      return null;
    } 

    /**
     * Look up an attribute's type by raw XML 1.0 name.
     *
     * @param rawName The raw XML 1.0 name.
     * @return The attribute type as a string, or null if the
     *         attribute is not in the list or if raw names
     *         are not available.
     */
    public String getType (String rawName)
    {
      for (int i = 0; i < getAttrCount(); i++)
      {
        AttrImpl attr = getChildAttribute(i);
        if (attr.getNodeName().equals(rawName))
          return Integer.toString(attr.getNodeType());
      }
      return null;
    } 

    /**
     * Look up an attribute's value by Namespace name.
     *
     * @param uri The Namespace URI, or the empty String if the
     *        name has no Namespace URI.
     * @param localName The local name of the attribute.
     * @return The attribute value as a string, or null if the
     *         attribute is not in the list.
     */
    public String getValue (String uri, String localName)
    {
      for (int i = 0; i < getAttrCount(); i++)
      {
        AttrImpl attr = (AttrImpl)getChildAttribute(i);
        if (attr.getLocalName().equals(localName) &&
            attr.getNamespaceURI().equals(uri))
          return attr.getValue();
      }
      return null;
    } 

    /**
     * Look up an attribute's value by raw XML 1.0 name.
     *
     * @param rawName The raw XML 1.0 name.
     * @return The attribute value as a string, or null if the
     *         attribute is not in the list or if raw names
     *         are not available.
     */
    public String getValue (String rawName)
    {
      for (int i = 0; i < getAttrCount(); i++)
      {
        AttrImpl attr = getChildAttribute(i);
        if (attr.getNodeName().equals(rawName))
          return attr.getValue();
      }
      return null;
    }
    
    ////////////////////////////  
    // Implement NamedNodeMap //
    ////////////////////////////
    
    public Node getNamedItem(String name)
    {
      return getChildAttribute(getIndex(name));
    }

    /**
     *  Adds a node using its <code>nodeName</code> attribute. If a node with 
     * that name is already present in this map, it is replaced by the new 
     * one.
     * <br> As the <code>nodeName</code> attribute is used to derive the name 
     * which the node must be stored under, multiple nodes of certain types 
     * (those that have a "special" string value) cannot be stored as the 
     * names would clash. This is seen as preferable to allowing nodes to be 
     * aliased.
     * @param arg  A node to store in this map. The node will later be 
     *   accessible using the value of its <code>nodeName</code> attribute.
     * @return  If the new <code>Node</code> replaces an existing node the 
     *   replaced <code>Node</code> is returned, otherwise <code>null</code> 
     *   is returned.
     * @exception DOMException
     *    WRONG_DOCUMENT_ERR: Raised if <code>arg</code> was created from a 
     *   different document than the one that created this map.
     *   <br> NO_MODIFICATION_ALLOWED_ERR: Raised if this map is readonly.
     *   <br> INUSE_ATTRIBUTE_ERR: Raised if <code>arg</code> is an 
     *   <code>Attr</code> that is already an attribute of another 
     *   <code>Element</code> object. The DOM user must explicitly clone 
     *   <code>Attr</code> nodes to re-use them in other elements.
     */
    public Node setNamedItem(Node arg)
                             throws DOMException
    {
      setAttribute(((Attr)arg).getName(), ((Attr)arg).getValue());
      return getChildAttribute(getIndex(((Attr)arg).getName()));
    }                                    

    /**
     *  Removes a node specified by name. A removed attribute may be known to 
     * have a default value when this map contains the attributes attached to 
     * an element, as returned by the attributes attribute of the 
     * <code>Node</code> interface. If so, an attribute immediately appears 
     * containing the default value as well as the corresponding namespace 
     * URI, local name, and prefix when applicable.
     * @param name  The <code>nodeName</code> of the node to remove.
     * @return  The node removed from this map if a node with such a name 
     *   exists.
     * @exception DOMException
     *    NOT_FOUND_ERR: Raised if there is no node named <code>name</code> 
     *   in this map.
     *   <br> NO_MODIFICATION_ALLOWED_ERR: Raised if this map is readonly.
     */
    public Node removeNamedItem(String name)
                                throws DOMException
    {
      int index = getIndex(name);
      return removeItem(index);
    } 
    
    public Node removeItem(int index)
                                throws DOMException
    {
      if (index >0)
      {  
        AttrImpl attr = getChildAttribute(index);
        int childCount = m_children.length;
        Child[] newChildren = new Child[childCount-1];
        System.arraycopy(m_children, 0, newChildren, 0, index);
        System.arraycopy(m_children, index+1, newChildren, index, childCount-1);
        m_children = newChildren;
        attrsEnd--;  
      
        return attr;  
      }
      else
        return null;
      
    }
    
    /**
     * The number of nodes (attributes) in this map. 
     * The range of valid child node indices 
     * is <code>0</code> to <code>length-1</code> inclusive. 
     */
    public int getLength()
    {
      return getAttrCount();
    } // getLength():int

    /**
     *  Returns the <code>index</code> th item in the map. If 
     * <code>index</code> is greater than or equal to the number of nodes in 
     * this map, this returns <code>null</code> .
     * @param index  Index into this map.
     * @return  The node at the <code>index</code> th position in the map, or 
     *   <code>null</code> if that is not a valid index.
     */
    public Node item(int index)
    {      
      return getChildAttribute(index);             
    } 
    

    /**
     *  Retrieves a node specified by local name and namespace URI. HTML-only 
     * DOM implementations do not need to implement this method.
     * @param namespaceURI  The  namespace URI of the node to retrieve.
     * @param localName  The  local name of the node to retrieve.
     * @return  A <code>Node</code> (of any type) with the specified local 
     *   name and namespace URI, or <code>null</code> if they do not identify 
     *   any node in this map.
     * @since DOM Level 2
     */
    public Node getNamedItemNS(String namespaceURI, 
                               String localName)
    {
      return getChildAttribute(getIndex(namespaceURI, localName));
    }  

    /**
     *  Adds a node using its <code>namespaceURI</code> and 
     * <code>localName</code> . If a node with that namespace URI and that 
     * local name is already present in this map, it is replaced by the new 
     * one.
     * <br> HTML-only DOM implementations do not need to implement this method.
     * @param arg  A node to store in this map. The node will later be 
     *   accessible using the value of its <code>namespaceURI</code> and 
     *   <code>localName</code> attributes.
     * @return  If the new <code>Node</code> replaces an existing node the 
     *   replaced <code>Node</code> is returned, otherwise <code>null</code> 
     *   is returned.
     * @exception DOMException
     *    WRONG_DOCUMENT_ERR: Raised if <code>arg</code> was created from a 
     *   different document than the one that created this map.
     *   <br> NO_MODIFICATION_ALLOWED_ERR: Raised if this map is readonly.
     *   <br> INUSE_ATTRIBUTE_ERR: Raised if <code>arg</code> is an 
     *   <code>Attr</code> that is already an attribute of another 
     *   <code>Element</code> object. The DOM user must explicitly clone 
     *   <code>Attr</code> nodes to re-use them in other elements.
     * @since DOM Level 2
     */
    public Node setNamedItemNS(Node arg)
                               throws DOMException
    {
      setAttributeNS(((Attr)arg).getNamespaceURI(), ((Attr)arg).getName(), ((Attr)arg).getValue());
      return getChildAttribute(getIndex(((Attr)arg).getNamespaceURI(), ((Attr)arg).getName()));
    }                                      

    /**
     *  Removes a node specified by local name and namespace URI. A removed 
     * attribute may be known to have a default value when this map contains 
     * the attributes attached to an element, as returned by the attributes 
     * attribute of the <code>Node</code> interface. If so, an attribute 
     * immediately appears containing the default value as well as the 
     * corresponding namespace URI, local name, and prefix when applicable.
     * <br> HTML-only DOM implementations do not need to implement this method.
     * @param namespaceURI  The  namespace URI of the node to remove.
     * @param localName  The  local name of the node to remove.
     * @return  The node removed from this map if a node with such a local 
     *   name and namespace URI exists.
     * @exception DOMException
     *    NOT_FOUND_ERR: Raised if there is no node with the specified 
     *   <code>namespaceURI</code> and <code>localName</code> in this map.
     *   <br> NO_MODIFICATION_ALLOWED_ERR: Raised if this map is readonly.
     * @since DOM Level 2
     */
    public Node removeNamedItemNS(String namespaceURI, 
                                  String localName)
                                  throws DOMException
    {
      int index = getIndex(namespaceURI, localName);
      return removeItem(index);
    }          

}
