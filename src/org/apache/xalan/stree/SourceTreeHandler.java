package org.apache.xalan.stree;

import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.apache.xalan.utils.DOMBuilder;
import org.apache.xpath.XPathContext;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.WhiteSpaceInfo;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import trax.Transformer;

/**
 * This class handles SAX2 parse events to create a source 
 * tree for transformation.
 */
public class SourceTreeHandler implements ContentHandler, LexicalHandler
{
  /**
   * Create a SourceTreeHandler that will start a transformation as 
   * soon as a startDocument occurs.
   */
  public SourceTreeHandler(TransformerImpl transformer)
  {
    m_transformer = transformer;
    XPathContext xctxt = ((TransformerImpl)transformer).getXPathContext();
    xctxt.setDOMHelper(new StreeDOMHelper());
  }

  /**
   * Create a SourceTreeHandler.
   */
  public SourceTreeHandler()
  {
  }
  
  private TransformerImpl m_transformer;
  public TransformerImpl getTransformer()
  {
    return m_transformer;
  }

  private DOMBuilder m_sourceTreeHandler;
  
  private Document m_root; // Normally a Document
  
  /**
   * Get the root document of tree that is being or will be created.
   */
  public Node getRoot()
  {
    return m_root;
  }

  /**
   * Set the root document of tree will be created.
   */
  public void setRoot(Document root)
  {
    m_root = root;    
  }

  /**
   * Implement the setDocumentLocator event.
   */
  public void setDocumentLocator (Locator locator)
  {
  }
  
  private boolean m_useMultiThreading = false;
  
  private boolean indexedLookup = false;      // for now   
  
  /**
   * Implement the startDocument event.
   */
  public void startDocument ()
    throws SAXException
  {    
    if(null == m_root)
    {
      if (indexedLookup)
        m_root = new IndexedDocImpl();
      else
        m_root = new DocumentImpl();      
    }
    ((DocumentImpl)m_root).setSourceTreeHandler(this);
    ((DocumentImpl)m_root).setUid(1);
    ((DocumentImpl)m_root).setLevel(new Integer(1).shortValue());
    m_sourceTreeHandler = new DOMBuilder(m_root);
    setShouldStripWhitespace(false);
    
    if(m_useMultiThreading && (null != m_transformer))
    {
      m_transformer.transformNode(m_root);
    }

    m_sourceTreeHandler.startDocument();
  }
  
  

  /**
   * Implement the endDocument event.
   */
  public void endDocument ()
    throws SAXException
  {
    m_sourceTreeHandler.endDocument();
    
    if(!m_useMultiThreading && (null != m_transformer))
    {
      m_transformer.transformNode(m_root);
    }
  }

  /**
   * Implement the startElement event.
   */
  public void startElement (String ns, String localName,
                            String name, Attributes atts)
    throws SAXException
  {
    m_sourceTreeHandler.startElement(ns, localName, name, atts);
  }

  /**
   * Implement the endElement event.
   */
  public void endElement (String ns, String localName,
                          String name)
    throws SAXException
  {
    m_sourceTreeHandler.endElement(ns, localName, name);
  }

  private boolean m_isCData = false;
  
  /**
   * Report the start of a CDATA section.
   *
   * <p>The contents of the CDATA section will be reported through
   * the regular {@link org.xml.sax.ContentHandler#characters
   * characters} event.</p>
   *
   * @exception SAXException The application may raise an exception.
   * @see #endCDATA
   */
  public void startCDATA ()
    throws SAXException
  {
    m_isCData = true;
  }
  
  /**
   * Report the end of a CDATA section.
   *
   * @exception SAXException The application may raise an exception.
   * @see #startCDATA
   */
  public void endCDATA ()
    throws SAXException
  {
    m_isCData = false;
  }
  
  /**
   * Tell if the given character array is whitespace.
   */
  private boolean isWhitespaceArray(char ch[], int start, int length)
  {
    int n = start+length;
    for(int i = start; i < n; i++)
    {
      if(!Character.isWhitespace(ch[i]))
         return false;
    }
    return true;
  }

  /**
   * Implement the characters event.
   */
  public void characters (char ch[], int start, int length)
    throws SAXException
  {
    if(isWhitespaceArray(ch, start, length) && getShouldStripWhitespace())
      return;
    
    if(m_isCData)
      m_sourceTreeHandler.cdata(ch, start, length);
    else
      m_sourceTreeHandler.characters(ch, start, length);
  }

  /**
   * Implement the characters event.
   */
  public void charactersRaw (char ch[], int start, int length)
    throws SAXException
  {
    m_sourceTreeHandler.charactersRaw(ch, start, length);
  }

  /**
   * Implement the ignorableWhitespace event.
   */
  public void ignorableWhitespace (char ch[], int start, int length)
    throws SAXException
  {
    m_sourceTreeHandler.charactersRaw(ch, start, length);
  }

  /**
   * Implement the processingInstruction event.
   */
  public void processingInstruction (String target, String data)
    throws SAXException
  {
    m_sourceTreeHandler.processingInstruction(target, data);
  }

  /**
   * Report an XML comment anywhere in the document.
   *
   * <p>This callback will be used for comments inside or outside the
   * document element, including comments in the external DTD
   * subset (if read).</p>
   *
   * @param ch An array holding the characters in the comment.
   * @param start The starting position in the array.
   * @param length The number of characters to use from the array.
   * @exception SAXException The application may raise an exception.
   */
  public void comment (char ch[], int start, int length)
    throws SAXException
  {
    m_sourceTreeHandler.comment(ch, start, length);
  }
  
  /**
   * Report the beginning of an entity.
   *
   * <p>The start and end of the document entity are not reported.
   * The start and end of the external DTD subset are reported
   * using the pseudo-name "[dtd]".  All other events must be
   * properly nested within start/end entity events.</p>
   *
   * <p>Note that skipped entities will be reported through the
   * {@link org.xml.sax.ContentHandler#skippedEntity skippedEntity}
   * event, which is part of the ContentHandler interface.</p>
   *
   * @param name The name of the entity.  If it is a parameter
   *        entity, the name will begin with '%'.
   * @exception SAXException The application may raise an exception.
   * @see #endEntity
   * @see org.xml.sax.ext.DeclHandler#internalEntityDecl
   * @see org.xml.sax.ext.DeclHandler#externalEntityDecl
   */
  public void startEntity (String name)
    throws SAXException
  {
    m_sourceTreeHandler.startEntity(name);
  }

  /**
   * Report the end of an entity.
   *
   * @param name The name of the entity that is ending.
   * @exception SAXException The application may raise an exception.
   * @see #startEntity
   */
  public void endEntity (String name)
    throws SAXException
  {
    m_sourceTreeHandler.endEntity(name);
  }
  
  /**
   * Report the start of DTD declarations, if any.
   *
   * <p>Any declarations are assumed to be in the internal subset
   * unless otherwise indicated by a {@link #startEntity startEntity}
   * event.</p>
   *
   * @param name The document type name.
   * @param publicId The declared public identifier for the
   *        external DTD subset, or null if none was declared.
   * @param systemId The declared system identifier for the
   *        external DTD subset, or null if none was declared.
   * @exception SAXException The application may raise an
   *            exception.
   * @see #endDTD
   * @see #startEntity
   */
  public void startDTD (String name, String publicId,
                        String systemId)
    throws SAXException
  {
  }


  /**
   * Report the end of DTD declarations.
   *
   * @exception SAXException The application may raise an exception.
   * @see #startDTD
   */
  public void endDTD ()
    throws SAXException
  {
  }

  /**
   * Begin the scope of a prefix-URI Namespace mapping.
   *
   * <p>The information from this event is not necessary for
   * normal Namespace processing: the SAX XML reader will 
   * automatically replace prefixes for element and attribute
   * names when the http://xml.org/sax/features/namespaces
   * feature is true (the default).</p>
   *
   * <p>There are cases, however, when applications need to
   * use prefixes in character data or in attribute values,
   * where they cannot safely be expanded automatically; the
   * start/endPrefixMapping event supplies the information
   * to the application to expand prefixes in those contexts
   * itself, if necessary.</p>
   *
   * <p>Note that start/endPrefixMapping events are not
   * guaranteed to be properly nested relative to each-other:
   * all startPrefixMapping events will occur before the
   * corresponding startElement event, and all endPrefixMapping
   * events will occur after the corresponding endElement event,
   * but their order is not guaranteed.</p>
   *
   * @param prefix The Namespace prefix being declared.
   * @param uri The Namespace URI the prefix is mapped to.
   * @exception org.xml.sax.SAXException The client may throw
   *            an exception during processing.
   * @see #endPrefixMapping
   * @see #startElement
   */
  public void startPrefixMapping (String prefix, String uri)
    throws SAXException
  {
    // System.out.println("DOMBuilder.startPrefixMapping("+prefix+", "+uri+");");
  }

  /**
   * End the scope of a prefix-URI mapping.
   *
   * <p>See startPrefixMapping for details.  This event will
   * always occur after the corresponding endElement event,
   * but the order of endPrefixMapping events is not otherwise
   * guaranteed.</p>
   *
   * @param prefix The prefix that was being mapping.
   * @exception org.xml.sax.SAXException The client may throw
   *            an exception during processing.
   * @see #startPrefixMapping
   * @see #endElement
   */
  public void endPrefixMapping (String prefix)
    throws SAXException
  {
  }
  
  /**
   * Receive notification of a skipped entity.
   *
   * <p>The Parser will invoke this method once for each entity
   * skipped.  Non-validating processors may skip entities if they
   * have not seen the declarations (because, for example, the
   * entity was declared in an external DTD subset).  All processors
   * may skip external entities, depending on the values of the
   * http://xml.org/sax/features/external-general-entities and the
   * http://xml.org/sax/features/external-parameter-entities
   * properties.</p>
   *
   * @param name The name of the skipped entity.  If it is a 
   *        parameter entity, the name will begin with '%'.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  public void skippedEntity (String name)
    throws SAXException
  {
  }
  
  private boolean m_shouldStripWhitespace = false;
  
  boolean getShouldStripWhitespace()
  {
    return m_shouldStripWhitespace;
  }
  
  void setShouldStripWhitespace(boolean shouldStrip)
  {
    m_shouldStripWhitespace = shouldStrip;
  }

}
