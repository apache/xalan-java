package org.apache.xalan.stree;

import org.xml.sax.Attributes;

public class ElementImplWithNS extends ElementImpl
{
  private String m_localName;
  private String m_uri;
  
  ElementImplWithNS (DocumentImpl doc, String ns, String name)
  {
    super(doc, name);
    int index = name.indexOf(':');
    if (index >0)
      m_localName = name.substring(index+1);
    else
      m_localName = name;
    m_uri = ns;
  }
  
  ElementImplWithNS (DocumentImpl doc, String ns, String localName,
                     String name, Attributes atts)
  {
    super(doc, name, atts);
    m_localName = localName;
    m_uri = ns;
  }
  
  /**
   * The namespace URI of this node, or <code>null</code> if it is 
   * unspecified.
    */
  public String       getNamespaceURI()
  {
    return m_uri;
  }
    
  /**
   * The namespace prefix of this node, or <code>null</code> if it is 
   * unspecified.
   * @since DOM Level 2
   */
  public String       getPrefix()
  {
    String rawName = getNodeName();
    int indexOfNSSep = rawName.indexOf(':');
    return (indexOfNSSep >= 0) 
                    ? rawName.substring(0, indexOfNSSep) : null;
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
