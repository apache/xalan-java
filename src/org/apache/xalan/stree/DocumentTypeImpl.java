package org.apache.xalan.stree;

import org.w3c.dom.Node;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;

public class DocumentTypeImpl extends Child implements DocumentType 
{
  DocumentTypeImpl(String name)
  {
    m_name = name;
  }
  
  private String m_name;
  private String m_publicID;
  private String m_systemID;
  private String m_internalSubset;
  
  /** 
   * A short integer indicating what type of node this is. The named
   * constants for this value are defined in the org.w3c.dom.Node interface.
   */
  public short getNodeType() 
  {
    return Node.DOCUMENT_TYPE_NODE;
  }
  
  /** Returns the node name. */
  public String getNodeName() 
  {
    return m_name; // I guess I need the name of the document type
  }
  
  /**
   * The name of DTD; i.e., the name immediately following the 
   * <code>DOCTYPE</code> keyword.
   */
  public String       getName()
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
     * A <code>NamedNodeMap</code> containing the general entities, both 
     * external and internal, declared in the DTD. Parameter entities are not 
     *  contained. Duplicates are discarded. For example in:
     * <pre>
     * &lt;!DOCTYPE ex SYSTEM "ex.dtd" [
     *   &lt;!ENTITY foo "foo"&gt;
     *   &lt;!ENTITY bar "bar"&gt;
     *   &lt;!ENTITY bar "bar2"&gt;
     *   &lt;!ENTITY % baz "baz"&gt;
     * ]&gt;
     * &lt;ex/&gt;</pre>
     *   the interface 
     * provides access to <code>foo</code> and the first declaration of 
     * <code>bar</code> but not the second declaration of  <code>bar</code> 
     * or <code>baz</code>. Every node in this map also implements the 
     * <code>Entity</code> interface.
     * <br>The DOM Level 2 does not support editing entities, therefore 
     * <code>entities</code> cannot be altered in any way.
     */
    public NamedNodeMap getEntities()
    {
      return null;
    }
    
    /**
     * A <code>NamedNodeMap</code> containing  the notations declared in the 
     * DTD. Duplicates are discarded. Every node in this map also implements 
     * the <code>Notation</code> interface.
     * <br>The DOM Level 2 does not support editing notations, therefore 
     * <code>notations</code> cannot be altered in any way.
     */
    public NamedNodeMap getNotations()
    {
      return null;
    }
    
    /**
     * The public identifier of the external subset.
     * @since DOM Level 2
     */
    public String       getPublicId()
    {
      return m_publicID;
    }
    
    /**
     * The system identifier of the external subset.
     * @since DOM Level 2
     */
    public String       getSystemId()
    {
      return m_systemID;
    }
    
    /**
     * The internal subset as a string.
     * @since DOM Level 2
     */
    public String       getInternalSubset()
    {
      return m_internalSubset;
    }

}
