package org.apache.xalan.stree;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DOMException;
import org.apache.xalan.utils.UnImplNode;

public class DOMImplementationImpl extends UnImplNode implements DOMImplementation
{
  //
  // Data
  //

  // static

  /** Dom implementation singleton. */
  static DOMImplementationImpl singleton = new DOMImplementationImpl();

  //
  // DOMImplementation methods
  //

  /** 
   * Test if the DOM implementation supports a specific "feature" --
   * currently meaning language and level thereof.
   * 
   * @param feature      The package name of the feature to test.
   * In Level 1, supported values are "HTML" and "XML" (case-insensitive).
   * At this writing, org.apache.xerces.dom supports only XML.
   *
   * @param version      The version number of the feature being tested.
   * This is interpreted as "Version of the DOM API supported for the
   * specified Feature", and in Level 1 should be "1.0"
   *
   * @returns    true iff this implementation is compatable with the
   * specified feature and version.
   */
  public boolean hasFeature(String feature, String version) {

    // Currently, we support only XML Level 1 version 1.0
    return 
      (feature.equalsIgnoreCase("XML") 
       && (version == null
           || version.equals("1.0")
           || version.equals("2.0")))
      || (feature.equalsIgnoreCase("Events") 
          && (version == null
              || version.equals("2.0")))
      || (feature.equalsIgnoreCase("MutationEvents") 
          && (version == null
              || version.equals("2.0")))
      || (feature.equalsIgnoreCase("Traversal") 
          && (version == null
              || version.equals("2.0")))
      ;

  } // hasFeature(String,String):boolean

  //
  // Public methods
  //

  /** NON-DOM: Obtain and return the single shared object */
  public static DOMImplementation getDOMImplementation() {
    return singleton;
  }  
  
  /**
   * Introduced in DOM Level 2. <p>
   * 
   * Creates an empty DocumentType node.
   *
   * @param qualifiedName The qualified name of the document type to be created. 
   * @param publicID The document type public identifier.
   * @param systemID The document type system identifier.
   * @since WD-DOM-Level-2-19990923
   */
  public DocumentType       createDocumentType(String qualifiedName, 
                                               String publicID, 
                                               String systemID)
  {
    // return new DocumentTypeImpl(null, qualifiedName, publicID, systemID);
    return null;
  }
  
  /**
   * Introduced in DOM Level 2. <p>
   * 
   * Creates an XML Document object of the specified type with its document
   * element.
   *
   * @param namespaceURI     The namespace URI of the document
   *                         element to create, or null. 
   * @param qualifiedName    The qualified name of the document
   *                         element to create, or null. 
   * @param doctype          The type of document to be created or null.<p>
   *
   *                         When doctype is not null, its
   *                         Node.ownerDocument attribute is set to
   *                         the document being created.
   * @return Document        A new Document object.
   * @throws DOMException    WRONG_DOCUMENT_ERR: Raised if doctype has
   *                         already been used with a different document.
   * @since WD-DOM-Level-2-19990923
   */
  public Document           createDocument(String namespaceURI, 
                                           String qualifiedName, 
                                           DocumentType doctype)
    throws DOMException
  {
    if (doctype != null && doctype.getOwnerDocument() != null) {
      throw new DOMException(DOMException.WRONG_DOCUMENT_ERR, 
        "DOM005 Wrong document");
    }
    DocumentImpl doc = new DocumentImpl(doctype);
    return doc;
  }
}
