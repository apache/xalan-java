/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xml.utils;

/**
 * <meta name="usage" content="internal"/>
 * <code>DTM</code> is an XML document model expressed as a table rather than
 * an object tree. It attempts to provide an interface to a parse tree that 
 * has very little object creation.
 * 
 * <p>Nodes in the DTM are identified by integer "handles".  A handle must 
 * be unique within a document.  A processing application must be careful, 
 * because a handle is not unique within a process... you can have two 
 * handles that belong to different documents.  It is up to the calling 
 * application to keep track of the association of a document with it's 
 * handle.</p>
 * 
 * <p>Namespace URLs, local-names, and expanded-names can all be represented 
 * by integer ID values.  An expanded name is made of a combination of the URL 
 * ID in the high two bytes, and the local-name ID is held in the low two 
 * bytes.  Thus a comparison of an expanded name can be quickly made in a 
 * single operation.  Also, the symbol space for URLs and local-names must 
 * be limited to 32K each (if you are to only use positive values for index 
 * lookup), which means that these should not be part of a general string 
 * pool mechanism.  Note that the namespace URL id can be 0, which should have 
 * the meaning that the namespace is null.  Zero should also not be used for 
 * a local-name index.</p>
 * 
 * <p>The model of the tree, as well as the general navigation model, is 
 * that of XPath 1.0, for the moment.  The model will be adapted to match
 * the XPath 2.0 data model, XML Schema, and InfoSet.</p>
 * 
 * <p>DTM does _not_ directly support the W3C's Document Object Model. However,
 * it attempts to come close enough that an implementation of DTM can be created 
 * that wraps a DOM.</p>
 * <p>State: In progress!!</p>
 */
public interface DTM
{

  // These are the same as the DOM on purpose.
  /**
   * The node is an <code>Element</code>.
   */
  public static final short ELEMENT_NODE              = 1;
  /**
   * The node is an <code>Attr</code>.
   */
  public static final short ATTRIBUTE_NODE            = 2;
  /**
   * The node is a <code>Text</code> node.
   */
  public static final short TEXT_NODE                 = 3;
  /**
   * The node is a <code>CDATASection</code>.
   */
  public static final short CDATA_SECTION_NODE        = 4;
  /**
   * The node is an <code>EntityReference</code>.
   */
  public static final short ENTITY_REFERENCE_NODE     = 5;
  /**
   * The node is an <code>Entity</code>.
   */
  public static final short ENTITY_NODE               = 6;
  /**
   * The node is a <code>ProcessingInstruction</code>.
   */
  public static final short PROCESSING_INSTRUCTION_NODE = 7;
  /**
   * The node is a <code>Comment</code>.
   */
  public static final short COMMENT_NODE              = 8;
  /**
   * The node is a <code>Document</code>.
   */
  public static final short DOCUMENT_NODE             = 9;
  /**
   * The node is a <code>DocumentType</code>.
   */
  public static final short DOCUMENT_TYPE_NODE        = 10;
  /**
   * The node is a <code>DocumentFragment</code>.
   */
  public static final short DOCUMENT_FRAGMENT_NODE    = 11;
  /**
   * The node is a <code>Notation</code>.
   */
  public static final short NOTATION_NODE             = 12;

  /**
   * The node is a <code>namespace node</code>.
   */
  public static final short NAMESPACE_NODE             = 113;
  
  // ========= DTM Implementation Control Functions. ==============

  /**
   * Set a suggested parse block size for the parser.
   *
   * @param blockSizeSuggestion Suggested size of the parse blocks, in bytes.
   */
  public void setParseBlockSize(int blockSizeSuggestion);

  /**
   * Set an implementation dependent feature.
   *
   * @param featureId A feature URL.
   * @param state true if this feature should be on, false otherwise.
   */
  public void setFeature(String featureId, boolean state);
  
  // ========= Document Navigation Functions =========

  /**
   * Given a node handle, get the handle of the node's first child.
   * If not yet resolved, waits for more nodes to be added to the document and
   * tries again.
   * 
   * @param nodeHandle int Handle of the node..
   * @return int DTM node-number of first child, or -1 to indicate none exists.
   */
  public int getFirstChild(int nodeHandle);

  /**
   * Given a node handle, advance to its last child.
   * If not yet resolved, waits for more nodes to be added to the document and
   * tries again.
   * 
   * @param nodeHandle int Handle of the node..
   * @return int Node-number of last child,
   * or -1 to indicate none exists.
   */
  public int getLastChild(int nodeHandle);

  /**
   * Given a node handle, get the index of the node's first attribute.
   * 
   * @param nodeHandle int Handle of the node..
   * @return Handle of first attribute, or -1 to indicate none exists.
   */
  public int getFirstAttribute(int nodeHandle);
  
  /**
   * Given a node handle, get the index of the node's first child.
   * If not yet resolved, waits for more nodes to be added to the document and
   * tries again
   * 
   * @param nodeHandle handle to node, which should probably be an element 
   *                   node, but need not be.
   *                   
   * @param inScope    true if all namespaces in scope should be returned, 
   *                   false if only the namespace declarations should be 
   *                   returned.
   * @return handle of first namespace, or -1 to indicate none exists.
   */
  public int getFirstNamespaceNode(int nodeHandle, boolean inScope);

  /**
   * Given a node handle, advance to its next sibling.
   * If not yet resolved, waits for more nodes to be added to the document and
   * tries again.
   * @param nodeHandle int Handle of the node..
   * @return int Node-number of next sibling,
   * or -1 to indicate none exists.
   */
  public int getNextSibling(int nodeHandle);
  
  /**
   * Given a node handle, find its preceeding sibling.
   * WARNING: DTM is asymmetric; this operation is resolved by search, and is
   * relatively expensive.
   * @param postition int Handle of the node..
   *
   * @param nodeHandle the id of the node.
   * @return int Node-number of the previous sib,
   * or -1 to indicate none exists.
   */
  public int getPreviousSibling(int nodeHandle);

  /**
   * Given a node handle, advance to the next attribute. If an
   * element, we advance to its first attribute; if an attr, we advance to
   * the next attr on the same node.
   * 
   * @param nodeHandle int Handle of the node..
   * @return int DTM node-number of the resolved attr,
   * or -1 to indicate none exists.
   */
  public int getNextAttribute(int nodeHandle);

  /**
   * Given a namespace handle, advance to the next namespace.
   * 
   * @param namespaceHandle handle to node which must be of type NAMESPACE_NODE.
   * @return handle of next namespace, or -1 to indicate none exists.
   */
  public int getNextNamespaceNode(int namespaceHandle, boolean inScope);

  /**
   * Given a node handle, advance to its next descendant.
   * If not yet resolved, waits for more nodes to be added to the document and
   * tries again.
   *
   * @param subtreeRootNodeHandle
   * @param nodeHandle int Handle of the node..
   * @return handle of next descendant,
   * or -1 to indicate none exists.
   */
  public int getNextDescendant(int subtreeRootHandle, int nodeHandle);

  /**
   * Given a node handle, advance to the next node on the following axis.
   *
   * @param axisContextHandle the start of the axis that is being traversed.
   * @param nodeHandle
   * @return handle of next sibling,
   * or -1 to indicate none exists.
   */
  public int getNextFollowing(int axisContextHandle, int nodeHandle);
  
  /**
   * Given a node handle, advance to the next node on the preceding axis.
   * 
   * @param axisContextHandle the start of the axis that is being traversed.
   * @param nodeHandle the id of the node.
   * @return int Node-number of preceding sibling,
   * or -1 to indicate none exists.
   */
  public int getNextPreceding(int axisContextHandle, int nodeHandle);

  /**
   * Given a node handle, find its parent node.
   * @param postition int Handle of the node..
   *
   * @param nodeHandle the id of the node.
   * @return int Node-number of parent,
   * or -1 to indicate none exists.
   */
  public int getParent(int nodeHandle);

  /**
   * Get the string-value of a node as a String object
   * (see http://www.w3.org/TR/xpath#data-model 
   * for the definition of a node's string-value).
   *
   * @param nodeHandle The node ID.
   *
   * @return A string object that represents the string-value of the given node.
   */
  public String getStringValue(int nodeHandle);
  
  /**
   * Get number of character array chunks in 
   * the string-value of a node.
   * (see http://www.w3.org/TR/xpath#data-model 
   * for the definition of a node's string-value).
   * Note that a single text node may have multiple text chunks.
   *
   * @param nodeHandle The node ID.
   *
   * @return number of character array chunks in 
   *         the string-value of a node.
   */
  public int getStringValueChunkCount(int nodeHandle);
  
  /**
   * Get a character array chunk in the string-value of a node.
   * (see http://www.w3.org/TR/xpath#data-model 
   * for the definition of a node's string-value).
   * Note that a single text node may have multiple text chunks.
   *
   * @param nodeHandle The node ID.
   * @param chunkIndex Which chunk to get.
   * @param startAndLen An array of 2 where the start position and length of 
   *                    the chunk will be returned.
   *
   * @return The character array reference where the chunk occurs.
   */
  public char[] getStringValueChunk(int nodeHandle, int chunkIndex, 
                                    int[] startAndLen);

  /**
   * Given a node handle, return an ID that represents the node's expanded name.
   * 
   * @param nodeHandle The handle to the node in question.
   *
   * @return the expanded-name id of the node.
   */
  public int getExpandedNameID(int nodeHandle);
  
  /**
   * Given an expanded name, return an ID.  If the expanded-name does not 
   * exist in the internal tables, the entry will be created, and the ID will 
   * be returned.  Any additional nodes that are created that have this 
   * expanded name will use this ID.
   * 
   * @param nodeHandle The handle to the node in question.
   *
   * @return the expanded-name id of the node.
   */
  public int getExpandedNameID(String namespace, String localName);
  
  /**
   * Given an expanded-name ID, return the local name part.
   *
   * @param ExpandedNameID an ID that represents an expanded-name.
   * @return String Local name of this node.
   */
  public String getLocalNameFromExpandedNameID(int ExpandedNameID);
  
  /**
   * Given an expanded-name ID, return the namespace URI part.
   *
   * @param ExpandedNameID an ID that represents an expanded-name.
   * @return String URI value of this node's namespace, or null if no
   * namespace was resolved.
   */
  public String getNamespaceFromExpandedNameID(int ExpandedNameID);

  /**
   * Given a node handle, return its DOM-style node name.
   *
   * @param nodeHandle the id of the node.
   * @return String Name of this node.
   */
  public String getNodeName(int nodeHandle);

  /**
   * Given a node handle, return its DOM-style localname.
   * (As defined in Namespaces, this is the portion of the name after any
   * colon character)
   *
   * @param nodeHandle the id of the node.
   * @return String Local name of this node.
   */
  public String getLocalName(int nodeHandle);

  /**
   * Given a node handle, return its DOM-style name prefix.
   * (As defined in Namespaces, this is the portion of the name before any
   * colon character)
   * @param postition int Handle of the node..
   *
   * @param nodeHandle the id of the node.
   * @return String prefix of this node's name, or null if no explicit
   * namespace prefix was given.
   */
  public String getPrefix(int nodeHandle);

  /**
   * Given a node handle, return its DOM-style namespace URI
   * (As defined in Namespaces, this is the declared URI which this node's
   * prefix -- or default in lieu thereof -- was mapped to.)
   * @param postition int Handle of the node..
   *
   * @param nodeHandle the id of the node.
   * @return String URI value of this node's namespace, or null if no
   * namespace was resolved.
   */
  public String getNamespaceURI(int nodeHandle);

  /**
   * Given a node handle, return its node value. This is mostly
   * as defined by the DOM, but may ignore some conveniences.
   * <p>
   * @param postition int Handle of the node..
   *
   * @param nodeHandle The node id.
   * @return String Value of this node, or null if not
   * meaningful for this node type.
   */
  public String getNodeValue(int nodeHandle);

  /**
   * Given a node handle, return its DOM-style node type.
   * @param postition int Handle of the node..
   *
   * @param nodeHandle The node id.
   * @return int Node type, as per the DOM's Node._NODE constants.
   */
  public int getNodeType(int nodeHandle);
  
  /**
   * <meta name="usage" content="internal"/>
   * Get the depth level of this node in the tree (equals 1 for
   * a parentless node).
   *
   * @param nodeHandle The node id.
   * @return the number of ancestors, plus one
   */
  public short getLevel(int nodeHandle);
  
  // ============== Document query functions ============== 
  
  /**
   * Return the base URI of the document entity. If it is not known
   * (because the document was parsed from a socket connection or from
   * standard input, for example), the value of this property is unknown.
   *
   * @param nodeHandle The node id, which can be any valid node handle.
   * @return the document base URI String object or null if unknown.
   */
  public String getDocumentBaseURI(int nodeHandle);

  /**
   * Return the system identifier of the document entity. If
   * it is not known, the value of this property is unknown.
   *
   * @param nodeHandle The node id, which can be any valid node handle.
   * @return the system identifier String object or null if unknown.
   */
  public int getDocumentSystemIdentifier(int nodeHandle);

  /**
   * Return the name of the character encoding scheme
   *        in which the document entity is expressed.
   *
   * @param nodeHandle The node id, which can be any valid node handle.
   * @return the document encoding String object.
   */
  public String getDocumentEncoding(int nodeHandle);

  /**
   * Return an indication of the standalone status of the document,
   *        either "yes" or "no". This property is derived from the optional
   *        standalone document declaration in the XML declaration at the
   *        beginning of the document entity, and has no value if there is no
   *        standalone document declaration.
   *
   * @param nodeHandle The node id, which can be any valid node handle.
   * @return the document standalone String object, either "yes", "no", or null.
   */
  public String getDocumentStandalone(int nodeHandle);

  /**
   * Return a string representing the XML version of the document. This
   * property is derived from the XML declaration optionally present at the
   * beginning of the document entity, and has no value if there is no XML
   * declaration.
   *
   * @param the document handle
   *
   * @return the document version String object
   */
  public String getDocumentVersion(int documentHandle);

  /**
   * Return an indication of
   * whether the processor has read the complete DTD. Its value is a
   * boolean. If it is false, then certain properties (indicated in their
   * descriptions below) may be unknown. If it is true, those properties
   * are never unknown.
   *
   * @return <code>true</code> if all declarations were processed;
   *         <code>false</code> otherwise.
   */
  public boolean getDocumentAllDeclarationsProcessed();

  /**
   *   A document type declaration information item has the following properties:
   *
   *     1. [system identifier] The system identifier of the external subset, if
   *        it exists. Otherwise this property has no value.
   *
   * @return the system identifier String object, or null if there is none.
   */
  public String getDocumentTypeDeclarationSystemIdentifier();

  /**
   * Return the public identifier of the external subset,
   * normalized as described in 4.2.2 External Entities [XML]. If there is
   * no external subset or if it has no public identifier, this property
   * has no value.
   *
   * @param the document type declaration handle
   *
   * @return the public identifier String object, or null if there is none.
   */
  public int getDocumentTypeDeclarationPublicIdentifier();

  // ============== Boolean methods ================
  
  /**
   * Figure out whether nodeHandle2 should be considered as being later
   * in the document than nodeHandle1, in Document Order as defined
   * by the XPath model. This may not agree with the ordering defined
   * by other XML applications.
   * <p>
   * There are some cases where ordering isn't defined, and neither are
   * the results of this function -- though we'll generally return true.
   * 
   * TODO: Make sure this does the right thing with attribute nodes!!!
   *
   * @param node1 DOM Node to perform position comparison on.
   * @param node2 DOM Node to perform position comparison on .
   * 
   * @return false if node2 comes before node1, otherwise return true.
   * You can think of this as 
   * <code>(node1.documentOrderPosition &lt;= node2.documentOrderPosition)</code>.
   */
  public boolean isNodeAfter(int nodeHandle1, int nodeHandle2);

  /**
   *     2. [element content whitespace] A boolean indicating whether the
   *        character is white space appearing within element content (see [XML],
   *        2.10 "White Space Handling"). Note that validating XML processors are
   *        required by XML 1.0 to provide this information. If there is no
   *        declaration for the containing element, this property has no value for
   *        white space characters. If no declaration has been read, but the [all
   *        declarations processed] property of the document information item is
   *        false (so there may be an unread declaration), then the value of this
   *        property is unknown for white space characters. It is always false for
   *        characters that are not white space.
   *
   * @param nodeHandle the node ID.
   * @return <code>true</code> if the character data is whitespace;
   *         <code>false</code> otherwise.
   */
  public boolean isCharacterElementContentWhitespace(int nodeHandle);

  /**
   *    10. [all declarations processed] This property is not strictly speaking
   *        part of the infoset of the document. Rather it is an indication of
   *        whether the processor has read the complete DTD. Its value is a
   *        boolean. If it is false, then certain properties (indicated in their
   *        descriptions below) may be unknown. If it is true, those properties
   *        are never unknown.
   *
   * @param the document handle
   *
   * @param documentHandle A node handle that must identify a document.
   * @return <code>true</code> if all declarations were processed;
   *         <code>false</code> otherwise.
   */
  public boolean isDocumentAllDeclarationsProcessed(int documentHandle);

  /**
   *     5. [specified] A flag indicating whether this attribute was actually
   *        specified in the start-tag of its element, or was defaulted from the
   *        DTD.
   *
   * @param the attribute handle
   *
   * NEEDSDOC @param attributeHandle
   * @return <code>true</code> if the attribute was specified;
   *         <code>false</code> if it was defaulted.
   */
  public boolean isAttributeSpecified(int attributeHandle);
  
  
  // ========== Direct SAX Dispatch, for optimization purposes ========
  
  /**
   * Directly call the
   * characters method on the passed ContentHandler for the 
   * string-value of the given node (see http://www.w3.org/TR/xpath#data-model 
   * for the definition of a node's string-value). Multiple calls to the 
   * ContentHandler's characters methods may well occur for a single call to 
   * this method.
   *
   * @param nodeHandle The node ID.
   * @param ch A non-null reference to a ContentHandler.
   *
   * @throws org.xml.sax.SAXException
   */
  public void dispatchCharactersEvents(
    int nodeHandle, org.xml.sax.ContentHandler ch)
      throws org.xml.sax.SAXException;

  /**
   * Directly create SAX parser events from a subtree.
   *
   * @param nodeHandle The node ID.
   * @param ch A non-null reference to a ContentHandler.
   *
   * @throws org.xml.sax.SAXException
   */
  public void dispatchToEvents(
    int nodeHandle, org.xml.sax.ContentHandler ch)
      throws org.xml.sax.SAXException;

}
