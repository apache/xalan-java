package org.apache.xalan.stree;

import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Text;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Comment;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.DocumentType;
import org.w3c.dom.DOMException;

import java.util.Hashtable;


public class DocumentImpl extends Parent
{
  DocumentImpl()
  {
	  m_bUpIndexer = new LevelIndexer();
  }

  DocumentImpl(DocumentType doctype)
  {
    if(null != doctype)
      m_docType = (DocumentTypeImpl)doctype;
	  m_bUpIndexer = new LevelIndexer();
  }
  
  private SourceTreeHandler m_sourceTreeHandler;
  SourceTreeHandler getSourceTreeHandler()
  {
    return m_sourceTreeHandler;
  }
  
  void setSourceTreeHandler(SourceTreeHandler h)
  {
    m_sourceTreeHandler = h;
  }
  
  private boolean indexedLookup = false;   // for now
    
  /**
   * 
   */
  private LevelIndexer m_bUpIndexer ;
 
  /**
   * 
   */
  public LevelIndexer getLevelIndexer()
  {
    return m_bUpIndexer;
  }
  
  DocumentTypeImpl m_docType;
  
  private int m_docOrderCount = 1;

  /**
   * Increment the document order count.  Needs to be called 
   * when a child is added.
   */
  protected void incrementDocOrderCount()
  {
    m_docOrderCount++;
  }
  
  /**
   * Increment the document order count.  Needs to be called 
   * when a child is added.
   */
  protected int getDocOrderCount()
  {
    return m_docOrderCount;
  }  

  /**
   * For XML, this provides access to the Document Type Definition.
   * For HTML documents, and XML documents which don't specify a DTD,
   * it will be null.
   */
  public DocumentType getDoctype() 
  {
    return m_docType;
  }
  
  private boolean m_useMultiThreading = false;
  
  /**
   * Set whether or not the tree being built should handle 
   * transformation while the parse is still going on.
   */
  public void setUseMultiThreading(boolean b)
  {
    m_useMultiThreading = b;
  }
  
  /**
   * Tell whether or not the tree being built should handle 
   * transformation while the parse is still going on.
   */
  public boolean getUseMultiThreading()
  {
    return m_useMultiThreading;
  }
  
  /**
   * The document element.
   */
  ElementImpl m_docElement;
  
  /**
   * Convenience method, allowing direct access to the child node
   * which is considered the root of the actual document content.
   */
  public Element getDocumentElement() 
  {
    return m_docElement;
  }
  
  Hashtable m_idAttributes = new Hashtable();
  
  public Hashtable getIDAttributes()
  {
    return m_idAttributes;
  } 
  
  public void setIDAttribute(String namespaceURI,
                             String qualifiedName,
                             String value,
                             Element elem)
  {
    m_idAttributes.put(value, elem);
  }
  
  /**
   * Append a child to the child list.
   * @param newChild Must be a org.apache.xalan.stree.Child.
   * @exception ClassCastException if the newChild isn't a org.apache.xalan.stree.Child.
   */
  public Node appendChild(Node newChild)
    throws DOMException
  {
    int type = newChild.getNodeType();
    if (type == Node.ELEMENT_NODE) 
    {
      if(null != m_docElement)
        throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR,
          "DOM006 Hierarchy request error");
      
      m_docElement = (ElementImpl)newChild;
    }
    else if (type == Node.DOCUMENT_TYPE_NODE) 
    {
      m_docType = (DocumentTypeImpl)newChild;
    }
    return super.appendChild(newChild);
  }


  /** Returns the node type. */
  public short getNodeType() 
  {
    return Node.DOCUMENT_NODE;
  }

  /** Returns the node name. */
  public String getNodeName() 
  {
    return "#document";
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
    return "#document";
  }
  
  /** Unimplemented. */
  public Element            createElement(String tagName)
    throws DOMException
  {
    if (indexedLookup)
      return new IndexedElemImpl(tagName);
    else
      return new ElementImpl(tagName);
  }

  /** Create a DocumentFragment. */
  public DocumentFragment   createDocumentFragment()
  {
    return new DocumentFragmentImpl();
  }

  /** Create a Text node. */
  public Text               createTextNode(String data)
  {
    return new TextImpl(data);
  }

  /** Create a Comment node. */
  public Comment            createComment(String data)
  {
    return new CommentImpl(data);
  }

  /** Create a CDATASection node. */
  public CDATASection       createCDATASection(String data)
    throws DOMException

  {
    return new CDATASectionImpl(data);
  }

  /** Create a ProcessingInstruction node. */
  public ProcessingInstruction createProcessingInstruction(String target,
                                                           String data)
    throws DOMException

  {
    return new ProcessingInstructionImpl(target, data);
  }

  /** Unimplemented right now, but I should probably implement. */
  public Node               importNode(Node importedNode,
                                       boolean deep)
    throws DOMException
  {
    return super.importNode(importedNode, deep);
  }

  /** Unimplemented. */
  public Element            createElementNS(String namespaceURI,
                                            String qualifiedName)
    throws DOMException
  {
    if (indexedLookup)
      return new IndexedElemWithNS(namespaceURI, qualifiedName);
    else
      return new ElementImplWithNS(namespaceURI, qualifiedName);
    //return super.createElementNS(namespaceURI, qualifiedName);
  }

  /** Unimplemented. */
  public Attr               createAttributeNS(String namespaceURI,
                                              String qualifiedName)
    throws DOMException
  {
    return super.createAttributeNS(namespaceURI, qualifiedName);
  }
  
  /**
   * Given an ID, return the element.
   */
  public Element getElementById(String elementId)
  {
    Element elem = (Element)m_idAttributes.get(elementId); 
    // Make sure we're done parsing.
    if (elem == null && !isComplete())
    {    
      synchronized (this)
      {
        try
        {
          // Don't really know why we should need the while loop,
          // but we seem to come out of wait() too soon! 
          while (!isComplete())
            wait();
        }
        catch (InterruptedException e)
        {          
          // That's OK, it's as good a time as any to check again
        }        
      }
      elem = (Element)m_idAttributes.get(elementId); 
    }    
    return elem;
  }
  
  

}
