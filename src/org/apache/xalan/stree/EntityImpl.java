package org.apache.xalan.stree;

import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.w3c.dom.Entity;


public class EntityImpl extends org.apache.xml.utils.UnImplNode implements Entity
{
  String m_publicId;
  String m_systemId;
  String m_notationName;
  String m_name;
  
  EntityImpl(String name, String notationName, String publicId, String systemId)
  {
    m_publicId = publicId;
    m_systemId = systemId;
    m_notationName = notationName;
    m_name = name;
  }
  
  public String getNodeName()
  {
    return m_name;
  }

  
  /**
   * The public identifier associated with the entity, if specified. If the 
   * public identifier was not specified, this is <code>null</code>.
   */
  public String getPublicId()
  {
    return m_publicId;
  }

  /**
   * The system identifier associated with the entity, if specified. If the 
   * system identifier was not specified, this is <code>null</code>.
   */
  public String getSystemId()
  {
    return m_systemId;
  }

  /**
   * For unparsed entities, the name of the notation for the entity. For 
   * parsed entities, this is <code>null</code>. 
   */
  public String getNotationName()
  {
    return m_notationName;
  }

}
