package org.apache.xml.utils;

/**
 * Simple filter for doing node tests.  Note the semantics of this are somewhat 
 * different that the DOM's NodeFilter.
 */
public interface DTMFilter
{

  // Constants for whatToShow.  These are used to set the node type that will 
  // be traversed.

  /**
   * Show all <code>Nodes</code>.
   */
  public static final int SHOW_ALL = 0xFFFFFFFF;

  /**
   * Show <code>Element</code> nodes.
   */
  public static final int SHOW_ELEMENT = 0x00000001;

  /**
   * Show <code>Attr</code> nodes. This is meaningful only when creating an
   * iterator or tree-walker with an attribute node as its
   * <code>root</code>; in this case, it means that the attribute node
   * will appear in the first position of the iteration or traversal.
   * Since attributes are never children of other nodes, they do not
   * appear when traversing over the document tree.
   */
  public static final int SHOW_ATTRIBUTE = 0x00000002;

  /**
   * Show <code>Text</code> nodes.
   */
  public static final int SHOW_TEXT = 0x00000004;

  /**
   * Show <code>CDATASection</code> nodes.
   */
  public static final int SHOW_CDATA_SECTION = 0x00000008;

  /**
   * Show <code>EntityReference</code> nodes.
   */
  public static final int SHOW_ENTITY_REFERENCE = 0x00000010;

  /**
   * Show <code>Entity</code> nodes. This is meaningful only when creating
   * an iterator or tree-walker with an<code> Entity</code> node as its
   * <code>root</code>; in this case, it means that the <code>Entity</code>
   *  node will appear in the first position of the traversal. Since
   * entities are not part of the document tree, they do not appear when
   * traversing over the document tree.
   */
  public static final int SHOW_ENTITY = 0x00000020;

  /**
   * Show <code>ProcessingInstruction</code> nodes.
   */
  public static final int SHOW_PROCESSING_INSTRUCTION = 0x00000040;

  /**
   * Show <code>Comment</code> nodes.
   */
  public static final int SHOW_COMMENT = 0x00000080;

  /**
   * Show <code>Document</code> nodes.
   */
  public static final int SHOW_DOCUMENT = 0x00000100;

  /**
   * Show <code>DocumentType</code> nodes.
   */
  public static final int SHOW_DOCUMENT_TYPE = 0x00000200;

  /**
   * Show <code>DocumentFragment</code> nodes.
   */
  public static final int SHOW_DOCUMENT_FRAGMENT = 0x00000400;

  /**
   * Show <code>Notation</code> nodes. This is meaningful only when creating
   * an iterator or tree-walker with a <code>Notation</code> node as its
   * <code>root</code>; in this case, it means that the
   * <code>Notation</code> node will appear in the first position of the
   * traversal. Since notations are not part of the document tree, they do
   * not appear when traversing over the document tree.
   */
  public static final int SHOW_NOTATION = 0x00000800;
  
  /** This bit specifies a namespace, and extends the SHOW_XXX stuff 
   *  in {@link org.w3c.dom.traversal.NodeFilter}. */
  public static final int SHOW_NAMESPACE = 0x00001000;

  /**
   * Special bitmap for match patterns starting with a function.
   * Make sure this does not conflict with {@link org.w3c.dom.traversal.NodeFilter}.
   */
  public static final int SHOW_BYFUNCTION = 0x00010000;

  /**
   * Test whether a specified node is visible in the logical view of a
   * <code>DTMIterator</code>. Normally, this function
   * will be called by the implementation of <code>DTMIterator</code>; 
   * it is not normally called directly from
   * user code.
   * 
   * @param nodeHandle int Handle of the node.
   * @param whatToShow one of SHOW_XXX values.
   * @return one of FILTER_ACCEPT, FILTER_REJECT, or FILTER_SKIP.
   */
  public short acceptNode(int nodeHandle, int whatToShow);
  
  /**
   * Test whether a specified node is visible in the logical view of a
   * <code>DTMIterator</code>. Normally, this function
   * will be called by the implementation of <code>DTMIterator</code>; 
   * it is not normally called directly from
   * user code.
   * 
   * @param nodeHandle int Handle of the node.
   * @param whatToShow one of SHOW_XXX values.
   * @param expandedName a value defining the exanded name as defined in 
   *                     the DTM interface.  Wild cards will be defined 
   *                     by 0xFFFF in the high word and/or in the low word.
   * @return one of FILTER_ACCEPT, FILTER_REJECT, or FILTER_SKIP.
   */
  public short acceptNode(int nodeHandle, int whatToShow, int expandedName);
 
}