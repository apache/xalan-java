package org.apache.xalan.stree;

import org.w3c.dom.Node;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.w3c.dom.DOMException;

public class AttrImpl extends Child implements Attr
{
  private String m_name;
  private String m_value;
  private boolean m_specified = true;
  
  AttrImpl(String name, String value)
  {
    m_name = name;
    m_value = value;
  }
  
  /**
   * A short integer indicating what type of node this is. The named
   * constants for this value are defined in the org.w3c.dom.Node interface.
   */
  public short getNodeType() 
  {
    return Node.ATTRIBUTE_NODE;
  }

  /** Returns the node name. */
  public String getNodeName() 
  {
    return m_name;
  }
  
  /**
   * The namespace URI of this node, or <code>null</code> if it is 
   * unspecified.
   * <br>This is not a computed value that is the result of a namespace 
   * lookup based on an examination of the namespace declarations in scope. 
   * It is merely the namespace URI given at creation time.
   * <br>For nodes of any type other than <code>ELEMENT_NODE</code> and 
   * <code>ATTRIBUTE_NODE</code> and nodes created with a DOM Level 1 
   * method, such as <code>createElement</code> from the 
   * <code>Document</code> interface, this is always <code>null</code>.Per 
   * the Namespaces in XML Specification  an attribute does not inherit its 
   * namespace from the element it is attached to. If an attribute is not 
   * explicitly given a namespace, it simply has no namespace.
   */
  public String       getNamespaceURI()
  {
    return null;
  }
    
  /**
   * The namespace prefix of this node, or <code>null</code> if it is 
   * unspecified.
   * @since DOM Level 2
   */
  public String       getPrefix()
  {
    return null;
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
   * Returns the value of this attribute node.
   * <br>For nodes created with a DOM Level 1 method, such as 
   * <code>createElement</code> from the <code>Document</code> interface, 
   * it is <code>null</code>.
   * @since DOM Level 2
   */
  public String getValue ()
  {      
    return m_value;
  } 
  
  /** Same as getValue(). */
  public String getNodeValue()
    throws DOMException
  {
    return m_value;
  }
  
  /**
   * Sets the value of this attribute node.
   * <br>For nodes created with a DOM Level 1 method, such as 
   * <code>createElement</code> from the <code>Document</code> interface, 
   * it is <code>null</code>.
   * @since DOM Level 2
   */
  public void setValue (String value) throws DOMException
  {      
    m_value = value;
  }
  
  /**
     *  If this attribute was explicitly given a value in the original 
     * document, this is <code>true</code> ; otherwise, it is 
     * <code>false</code> . Note that the implementation is in charge of this 
     * attribute, not the user. If the user changes the value of the 
     * attribute (even if it ends up having the same value as the default 
     * value) then the <code>specified</code> flag is automatically flipped 
     * to <code>true</code> .  To re-specify the attribute as the default 
     * value from the DTD, the user must delete the attribute. The 
     * implementation will then make a new attribute available with 
     * <code>specified</code> set to <code>false</code> and the default value 
     * (if one exists).
     * <br> In summary: If the attribute has an assigned value in the document 
     * then  <code>specified</code> is <code>true</code> , and the value is 
     * the  assigned value. If the attribute has no assigned value in the 
     * document and has  a default value in the DTD, then 
     * <code>specified</code> is <code>false</code> ,  and the value is the 
     * default value in the DTD. If the attribute has no assigned value in 
     * the document and has  a value of #IMPLIED in the DTD, then the  
     * attribute does not appear  in the structure model of the document.
     */
    public boolean getSpecified()
    {
      return m_specified;
    }
    
   /**
     *  The <code>Element</code> node this attribute is attached to or 
     * <code>null</code> if this attribute is not in use.
     * @since DOM Level 2
     */
    public Element getOwnerElement()
    {
      return (Element)getParentNode();
    }
    
    public String getName()
    {
      return m_name;
    }  
    
    void setName(String name)
    {
      m_name = name;
    }  
    
}
