package org.apache.xalan.stree;

import org.w3c.dom.Node;

public class NotationImpl
{
  private String m_name;

  /** Public identifier. */
  private String m_publicId;

  /** System identifier. */
  private String m_systemId;

  /** 
   * A short integer indicating what type of node this is. The named
   * constants for this value are defined in the org.w3c.dom.Node interface.
   */
  public short getNodeType() 
  {
    return Node.NOTATION_NODE;
  }
  
  /**
   * The Public Identifier for this Notation. If no public identifier
   * was specified, this will be null.  
   */
  public String getPublicId() 
  {
    return m_publicId;

  } // getPublicId():String

  /**
   * The System Identifier for this Notation. If no system identifier
   * was specified, this will be null.  
   */
  public String getSystemId() 
  {
    return m_systemId;
  } // getSystemId():String

  /** Returns the node name. */
  public String getNodeName() 
  {
    return m_name;
  }

}
