package org.apache.xalan.stree;

import org.w3c.dom.Node;
import org.xml.sax.Attributes;

public class AttrImplNS extends AttrImpl
{
  private String m_localName;
  private String m_namespaceURI;                  // attribute index
  
  AttrImplNS(String uri, String name, String value)
  {
    super(name, value);
    // System.out.println("AttrImplNS - name: "+name);
    // System.out.println("uri: "+uri+", "+name);
    m_namespaceURI = uri;
    int index = name.indexOf(':');
    if (index >0)
      m_localName = name.substring(index+1);
    else
      m_localName = name;
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
    return m_namespaceURI;    
  }
    
  /**
   * The namespace prefix of this node, or <code>null</code> if it is 
   * unspecified.
   * @since DOM Level 2
   */
  public String       getPrefix()
  {
    String m_name = getNodeName();
    int indexOfNSSep = m_name.indexOf(':');
    return (indexOfNSSep >= 0) 
                    ? m_name.substring(0, indexOfNSSep) : null;
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
    return m_localName;
  }
}
