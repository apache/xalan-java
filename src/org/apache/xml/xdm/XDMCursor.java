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
package org.apache.xml.xdm;

import org.apache.xml.utils.XMLString;
import org.apache.xml.utils.NodeVector;
import org.apache.xpath.objects.*;
import javax.xml.transform.SourceLocator;

/**
 * <code>XDMCursor</code> (XSLT Data Model Cursor)
 * is intended to replace direct access to DTM Nodes, Traversers 
 * and Iterators. The goal is to generalize the concepts
 * so they can be wrapped around any back-end model with
 * reasonable efficiency, and it may actually improve access
 * to DTM nodes as well by avoiding the shift-mask-and-indirect
 * cycle which DTM Node Handles required.
 * 
 * A cursor is a "flyweight" accessor for an XSLT data model. It
 * hides the details of the model, and exposes only the APIs needed to
 * retrieve data from the "current node" within that model. Methods are
 * also provided to select another node as current, via an 
 * iterator-style "next" operation
 * (and, possibly, "previous" and numerically-indexed access.)
 * 
 * The simplest "next" might be a single-node cursor -- navigation but
 * no next/previous sequence traversal. Next simplest would be doc-order
 * sequence, forward or backward. Then we get into filtering and
 * fancier XPaths.
 * 
 * Note that navigation changes the current node of this
 * cursor, since in many cases we don't need to retain a reference to the
 * old node. If you *do* need to hold onto that reference, clone 
 * the cursor first. (NOTE: If it has a fancy traversal attached,
 * cloning all of that could be expensive. Use copy ctor to get a
 * basic single-node cursor instead.)
 * 
 * A cursor operates only within a single Document. Higher-level
 * APIs are used to express query results that span multiple
 * documents. (This decision may still be subject to %REVIEW%.)
 * 
 * <blockquote>"Cursors! Foiled again!"</blockquote>
 * */
public interface XDMCursor
{
  // ===== Manefest Constants: Node Type Numbers =====
  // These nodeType mnemonics and values are deliberately the same as those
  // used by the DOM and DTM, for convenient mapping. They should
  // probably be declared in one place and shared, as was done for
  // the Axis mnemonics.

  /**   * The node is an <code>Element</code>.   */
  public static final short ELEMENT_NODE = org.w3c.dom.Document.ELEMENT_NODE;

  /**   * The node is an <code>Attr</code>.   */
  public static final short ATTRIBUTE_NODE = org.w3c.dom.Document.ATTRIBUTE_NODE;

  /**   * The node is a <code>Text</code> node.   */
  public static final short TEXT_NODE = org.w3c.dom.Document.TEXT_NODE;

  /**   * The node is a <code>CDATASection</code>.   */
  public static final short CDATA_SECTION_NODE = org.w3c.dom.Document.CDATA_SECTION_NODE;

  /**   * The node is an <code>EntityReference</code>.   */
  public static final short ENTITY_REFERENCE_NODE = org.w3c.dom.Document.ENTITY_REFERENCE_NODE;

  /**   * The node is an <code>Entity</code>.   */
  public static final short ENTITY_NODE = org.w3c.dom.Document.ENTITY_NODE;

  /**   * The node is a <code>ProcessingInstruction</code>.   */
  public static final short PROCESSING_INSTRUCTION_NODE = org.w3c.dom.Document.PROCESSING_INSTRUCTION_NODE;

  /**   * The node is a <code>Comment</code>.   */
  public static final short COMMENT_NODE = org.w3c.dom.Document.COMMENT_NODE;

  /**   * The node is a <code>Document</code>.   */
  public static final short DOCUMENT_NODE = org.w3c.dom.Document.DOCUMENT_NODE;

  /**   * The node is a <code>DocumentType</code>.   */
  public static final short DOCUMENT_TYPE_NODE = org.w3c.dom.Document.DOCUMENT_TYPE_NODE;

  /**   * The node is a <code>DocumentFragment</code>.   */
  public static final short DOCUMENT_FRAGMENT_NODE = org.w3c.dom.Document.DOCUMENT_FRAGMENT_NODE;

  /**   * The node is a <code>Notation</code>.   */
  public static final short NOTATION_NODE = org.w3c.dom.Document.NOTATION_NODE;

  /**
   * The node is a <code>namespace node</code>. Note that this is not
   * currently a node type defined by the DOM API.
   */
  public static final short NAMESPACE_NODE = 13;
  
  /**
   * The number of valid nodetypes. REMEMBER TO UPDATE THIS if you add more
   * node types.
   */
  public static final short  NTYPES = 14;
  
  /**  Map node types to their names; useful in debugging printouts.
   * 
   * There's no particular need for this to be public, and exposing
   * it would open the risk of someone overwriting it. If we do need
   * public access, we can add a query method.
   */
	static final String[] TYPENAME=
	  { "NULL",
    	"ELEMENT",
	    "ATTRIBUTE",
    	"TEXT",
	    "CDATA_SECTION",
    	"ENTITY_REFERENCE",
	    "ENTITY",
    	"PROCESSING_INSTRUCTION",
	    "COMMENT",
    	"DOCUMENT",
	    "DOCUMENT_TYPE",
    	"DOCUMENT_FRAGMENT",
	    "NOTATION",
    	"NAMESPACE"
	  };

  // ======== Constructors =====
  //
  // Constructors can't actually be declared by Interfaces.
  // There will need to be constructors to serve the initial
  // get-root-of-a-document action, and possibly others... but
  // those will be low-level model-specific support, handled by
  // specific XDMCursor implementations rather than by this API.
  
  // ======== Cursor Factories ========
  // What we *can* declare is factory methods. Note that
  // we're going to have to go to whatever
  // XDM glue is being used for this particular back-end model,
  // so having the cursor be able to manufacture other cursors
  // over the same document is probably the right answer.
  //
  // See ISSUE re cursors spannning multiple documents.

  /** Obtain a new XDMCursor starting at a specified node, using
   * the XDM cursor factory/pool serving that document.
   * 
   * @param node XDMCursor whose current node is the starting
   * node for this axis search.
   * 
   * @param axis Which axis to scan, as manefest constant
   * obtained from the Axes class. (Currently ints. Singleton
   * objects might or might not be more elegant.)
   * 
   * Scan starts from <strong>this</strong> cursor's current node!
   * 
   * @return a new XDMCursor object, ready to iterate over the
   * specified axis.
   * */
  public XDMCursor getAxisCursor(int axis);
  
  /** Obtain a new XDMCursor starting at a specified node, using
   * the XDM cursor factory/pool serving that document.
   * 
   * Scan starts from this cursor's current node!
   * 
   * @param node XDMCursor whose current node is the
   * starting/context node.
   * 
   * @param axis Which axis to scan, as manefest constant
   * obtained from the Axes class.
   * 
   * @param Extended Type of nodes to be accepted.
   * %REVIEW% Note that this means the concept of Extended Types
   * is shared by DTM and XDM. Define it here and use it there,
   * for architectural-hierarchy reasons...?
   * 
   * @return a new XDMCursor object, ready to iterate over the
   * specified axis.
   * */
  public XDMCursor getTypedAxisCursor(final int axis,final int type);

  /** Clone this cursor... with its iteration state preserved.
   * Typed equivalent of Object.clone(), for convenience -- but
   * note that we should override that too so it's an untyped
   * equivalent of this, cloning iteration state deeply and
   * model reference shallowly.
   * 
   * @return a new XDMCursor object, ready to resume iteration
   * over the specified axis from the same point this one has
   * reached.
   * */
  public XDMCursor cloneXDMCursor();
  
  /** PROBABLY SUPERFLUOUS: Convenience for 
   * getAxisCursor(this,SINGLE_NODE). Documented here mostly for
   * ease of discussion of "SELF cursor" as a concept.
   * 
   * @return a new "SELF" Cursor object, initialized from
   * this cursor's current node.
   * */
  public XDMCursor singleNode();
  
  /** A cursor to be "empty", becuase it does not match
   * any nodes (eg, applying the Attributes axis to
   * something other than an Element node). Attempting to
   * access an empty cursor will probably result in
   * null-reference exceptions. This test allows checking
   * whether a cursor is empty.
   * 
   * In some sense, this call acts as an initializing 
   * equivalent of nextNode(). One of the things that does
   * for is us reserve a hook which we can use to defer
   * resolution of the first matching node until required,
   * which may give us an opportunity for optimization.
   * 
   * Typical use:
   * <code>
   * <pre>
   * for(boolean hasMore=!cursor.isEmpty();
   *     hasMore;
   *     hasMore=cursor.nextNode())
   *     // ... operate on cursor's current node ...
   * </pre>
   * </code>
   * */
  public boolean isEmpty();

  // ========= Document Tree Navigation =========
  // If you need to take a single step, obtain a Cursor for
  // that axis and use it to take that step.
  //
  // If you need to do an entry/exit-aware visit of a subtree,
  // see the XDMTreeWalker/XDMNodeVisitor interfaces.
  //
  // HOWEVER: I've kept just a few local-navigation routines for
  // efficiency reasons.
   
  /**
   * @return boolean true iff the given node has child nodes.
   */
  public boolean hasChildNodes();

  /**
   * Find an attribute of the current node by local name and namespace URI.
   * 
   * We *could* do this by getting the extended 
   * type and a typed-axis cursor over the attributes axis... but
   * this version will perform better in many (not all) models,
   * since attribute access by name is often optimized.
   *
   * @param namespaceURI The namespace URI of the attribute to
   *   retrieve, or null.
   * @param name The local name of the attribute to
   *   retrieve.
   * @return a "SELF" Cursor whose current node is
   * the attribute node with the specified name, or
   * <code>NULL</code> if there is no such
   *  attribute.
   */
  public XDMCursor getAttributeNode(String namespaceURI,String name);

  /**
   *  find the owning document node.
   *
   * @return a "SELF" Cursor whose current node is the
   * owning document, or <code>NULL</code> if the current node was
   * a Document. (Note difference from DOM, where getOwnerDocument returns
   * null for the Document node. The DOM Proxy layer will have to
   * test node type in combination with this operation to yield
   * the right result.)
   * @see getOwnerDocument
   */
  public XDMCursor getDocumentRoot();

  // ========= Node Sequence (iterating) Navigation =========
  // Concepts swiped from DOM TreeWalker
  // (each request changes the current node),
  // DTM Iterators, and DTM's node-property accessors.
  //
  // If you want a non-iterating cursor (for tree-walking
  // access only), instantiate a "SELF" Cursor and
  // call its nextNode() exactly once.
  //
  // We've concluded that the cursor starts with its current node
  // AT the first node to be yielded. Attempting to impose
  // an "empty when before first node" state would have
  // computational costs; individually small but multiplied
  // by all XDM accesses, and would interfere with the concept
  // of a SELF cursor being extremely lightweight. Some specific
  // implementations may wish to transparently defer resolving
  // the first node until it is referenced; the code to achieve
  // this is straightforward but -- because replicated everywhere
  // -- ugly, and has costs similar to those of the before-first
  // state which make it a questionable optimization.
  // 
  // (Note that this means creating a cursor you don't intend
  // to use may be costly, if it's one that may search far across
  // the document to find its first node. I *think* the solution
  // is to avoid creating cursors you won't use... perhaps
  // to create only a SELF cursor until you know that the axis
  // will actually be explored... or to leverage isEmpty() as
  // an implied initialization. This design is still subject to
  // %REVIEW% as we gain experience with XDM.)
  //
  // We have also decided that there *is* such a thing
  // as an empty cursor, because it may be created empty 
  // (no matching nodes). Accessing the current-node 
  // properties of an empty cursor is considered a coding error
  // and will yield some flavor of null-reference runtime exception.
  // Users are advised to watch the return value of nextNode()
  // and the isEmpty() property,  and avoid fencepost errors in
  // their code!
  
 /**
   * The root node of the iteration, as specified when it
   * was created.  Note the root node is not the root node of the 
   * document tree, but the context node from where the iteration 
   * begins and ends.
   *
   * @return XDMCursor reference to the context node.
   */
  public XDMCursor getIterationRoot();

  /**
   * Reset the root node of the <code>DTMIterator</code>, overriding
   * the value specified when it was created.  Note the root node is
   * not the root node of the document tree, but the context node from
   * where the iteration begins.
   *
   * @param XDMCursor reference to the context node.
   * @param environment The environment object.  
   * The environment in which this iterator operates, which should provide:
   * <ul>
   * <li>a node (the context node... same value as "root" defined below) </li>
   * <li>a pair of non-zero positive integers (the context position and the context size) </li>
   * <li>a set of variable bindings </li>
   * <li>a function library </li>
   * <li>the set of namespace declarations in scope for the expression.</li>
   * <ul>
   * 
   * <p>At this time the exact implementation of this environment is application 
   * dependent.  "Probably a proper interface will be created fairly soon."
   * (we said that *how* long ago?)</p>
   * 
   * @return this, updated -- or null if the proposed root is not
   * in a compatable cursor implementation (wrong document, usually).
   * %REVIEW% Should that throw exception? Should it instantiate
   * a new cursor of corresponding type in the root cursor's space?
   * (It can do so, by calling the root's factories...)
   * Should it return an empty cursor?
   */
  public XDMCursor setIterationRoot(XDMCursor root, Object environment);
  
  /**
   * Reset the iterator to the start. After resetting, the current node
   * will be the the first node matching this Cursor's iteration.
   */
  public void resetIteration();

  /**
   * DTMIterator.getWhatToShow is invoked recursively by
   * folks who implement that query, but nobody every calls
   * the query itself. Seeing no need for it, let's drop it.
   */

  /** getExpandEntityReferences() dropped; meaningless in Xalan */

  /**
   * Returns the next node in the set and advances the position of the
   * iterator in the set. After a <code>DTMIterator</code> has setRoot called,
   * the first call to <code>nextNode()</code> returns that root or (if it
   * is rejected by the filters) the first node within its subtree which is
   * not filtered out.
   * 
   * When no next-node is available, this returns false, but does
   * <strong>not</strong> advance the cursor position. It's the
   * caller's responsibility to check the returned value.
   * 
   * @return true if a next-node was found, false if we couldn't
   * advance.
   */
  public boolean nextNode();

  /**
   * Returns the previous node in the set and moves the position of the
   * <code>DTMIterator</code> backwards in the set.
   * 
   * As far as I can tell, the only place
   * DTMIterator.previousNode() was ever used was in implementing
   * itself, or in implementing NodeSequence.runTo()'s roll-back
   * mode. I submit that this means it's probably an internal that
   * need not be exposed on XDMCursor. Note too that equivalent
   * behavior can be obtained via setCurrentPos(getCurrentPos()-1).
   * 
   * @return this, with current node advanced to
   * the previous node in the set being iterated over, or
   * null if the cursor has been advanced off the 
   * beginning of the iteration and is now empty (no current node).
   */
  //public boolean previousNode();


  /**
   * Detaches the <code>XDMCursor</code> from the set which it iterated
   * over, releasing any computational resources and placing the iterator
   * in the INVALID state. After <code>detach</code> has been invoked,
   * calls to <code>nextNode</code> or <code>previousNode</code> will
   * raise a runtime exception.
   * 
   * We used this to return LocPathIterators to a reuse pool.
   * And XRTreeFrag, which implemented DTMIterator, used this
   * as a hint that it was now time to discard the 
   * Temporary/ResultFragment DTM. It appears to have been a no-op 
   * for other iterators.
   * 
   * <p>%REVIEW%  Do we need to expose this on the Cursor?
   * (Probably, unless we're willing to deal with finalizers.)
   */
  public void detach();
  
  /**
   * Specify if it's OK for detach to release the cursor for reuse.
   * 
   * Implemented in LocPathIterator (gates detach() behavor),
   * XRTreeGrag (ditto) and
   * NodeSequence (setting it false turns on internal cache (???),
   * as well as being passed along to contained iterators/super)
   * 
   * <p>%REVIEW%  Do we need to expose this on the Cursor, or
   * is it a low-level iterator issue? Are we pooling cursors??
   * 
   * @param allowRelease true if it is OK for detach to release
   * this cursor for pooling.
   */
  public void allowDetachToRelease(boolean allowRelease);

  /** getCurrentNode goes away since we access the node
   * through the Cursor API.
   * 
   * isFresh is really an internal.
   */
  
  //========= Sequence-based (Random Access) navigation ==========

  /**
   * If setShouldCache() is called, then nodes will
   * be cached, enabling random access, and giving the ability to do 
   * sorts and the like.  They are not cached by default.
   *
   * %REVIEW% (old issue) Should random-access methods 
   * (specifically setCurrentPosition() and item())
   * throw an exception if they're called on a Cursor
   * with this flag set false?
   */
  public void setShouldCacheNodes();
  
  /**
   * Tells if this iterator can have nodes added to it or set via 
   * the <code>setItem(int node, int index)</code> method.
   * 
   * @return True if the nodelist can be mutated.
   */
  public boolean isMutable();

  /** Get the current position within the cached list, which is one
   * less than the next nextNode() call will retrieve.  i.e. if you
   * call getCurrentPos() and the return is 0, the next fetch will
   * take place at index 1.
   * 
   * %REVIEW% Should empty cursors have a currentPos other
   * than 0? Probably not... 
   *
   * @return The position of the iteration.
   */
  public int getCurrentPos();

  /**
   * If an index is requested, NodeSetDTM will call this method
   * to run the iterator to the index.  This sets
   * the current node to the index.  If the index argument is -1, this
   * signals that the iterator should be run to the end and
   * completely fill the cache.
   *
   * %REVIEW% In the DTM world, this method was much like
   * setCurrentPosition but had the added behavior of completely preloading an
   * iterator's cache when called with the index -1. I believe
   * we can consider that an _internal_ behavior, since it's only
   * used as preparation for sorting or for getLength() -- and indeed
   * one can argue that calling getLength() for the side-effect is
   * just about equally clean/ugly.
   * 
   * @param index The index to run to, or -1 if the iterator should be run
   *              to the end.
   * @return this, updated to point to the specified
   * node -- or, if index>=length, return null and make the
   * cursor empty (no current node).
   */
  // public XDMCursor runTo(int index);

  /**
   * Set the current position in the node set.
   * 
   * @param i Must be a valid index.
   * @return true if the specified position exists and
   * has been accepted, or false if not (ie, index>=length, or
   * index<position in a non-cached cursor).
   */
  public boolean setCurrentPos(int i);

  /**
   * Returns the <code>node handle</code> of an item in the collection. If
   * <code>index</code> is greater than or equal to the number of nodes in
   * the list, this returns <code>null</code>.
   *
   * @param index of the item.
   * @return This, with the current node set to the node
   *   at the <code>index</code>th position in the
   *   <code>DTMIterator</code>, or null f that is not a valid
   *   index.
   */
  public XDMCursor item(int index);
  
  /** 
   * Sets the node at the specified index of this vector to be the
   * specified node. The previous component at that position is discarded.
   *
   * <p>The index must be a value greater than or equal to 0 and less
   * than the current size of the vector.  
   * The iterator must be in cached mode.</p>
   * 
   * <p>Primarily meant to be used for sorted iterators.</p>
   * 
   * %REVIEW% DOES THIS BELONG IN CURSOR, OR SHOULD MUTABLE CURSOR
   * BE A SEPARATE CASE? (Or should sort be moved internal?)
   *
   * @param node Node to set
   * @param index Index of where to set the node
   */
  public void setItem(int node, int index);
  
  /**
   * The number of nodes in the list. The range of valid child node indices
   * is 0 to <code>length-1</code> inclusive. 
   * 
   * Note that computing this is EXPENSIVE in axis cursors;
   * it requires driving the axis to completion, then resetting
   * our position. In a cached cursor where we intend to retrive
   * all values, that isn't bad. In an uncached cursor, or
   * one where we don't expect to retrieve all the matching nodes,
   * it can cause a significant amount of additional tree
   * searching. You should seriously consider avoiding the 
   * getLength() call when possible.
   *
   * @return The number of nodes in the list.
   */
  public int getLength();
    
  //=========== Cloning operations. ============
  
  /**
   * Get a cloned Iterator that is reset to the start of the iteration.
   *
   * @return A clone of this iteration that has been reset.
   *
   * @throws CloneNotSupportedException
   */
  public XDMCursor cloneWithReset() throws CloneNotSupportedException;

  /**
   * Get a clone of this iterator, but don't reset the iteration in the 
   * process, so that it may be used from the current position.
   *
   * @return A clone of this object.
   *
   * @throws CloneNotSupportedException
   */
  public Object clone() throws CloneNotSupportedException;
  
  /**
   * Returns true if all the nodes in the iteration well be returned in document 
   * order.
   * 
   * @return true if all the nodes in the iteration well be returned in document 
   * order.
   */
  public boolean isDocOrdered();
  
  /**
   * Returns the axis being iterated, if it is known.
   * 
   * @return Axis.CHILD, etc., or -1 if the axis is not known or is of multiple 
   * types.
   */
  public int getAxis();
  
  // ======== NODE PROPERTY ACCESSORS ========
  // Based primarily on the DTM Node API, which in turn is based
  // on the DOM Node API -- but accesses the Cursor's current node,
  // rather than via a DOM Node object or DTM Node Handle.

  /**
   * Get the string-value of the current node as a String object
   * (see http://www.w3.org/TR/xpath#data-model
   * for the definition of the current node's string-value).
   *
   * @return A string object that represents the string-value of the given node.
   */
  public XMLString getStringValue();

  /**
   * Get number of character array chunks in
   * the string-value of the current node.
   * (see http://www.w3.org/TR/xpath#data-model
   * for the definition of the current node's string-value).
   * Note that a single text node may have multiple text chunks.
   *
   * @param nodeHandle The node ID.
   *
   * @return number of character array chunks in
   *         the string-value of the current node.
   */
  public int getStringValueChunkCount();

  /**
   * Get a character array chunk in the string-value of the current node.
   * (see http://www.w3.org/TR/xpath#data-model
   * for the definition of the current node's string-value).
   * Note that a single text node may have multiple text chunks.
   *
   * @param chunkIndex Which chunk to get.
   * @param startAndLen  A two-integer array which, upon return, WILL
   * BE FILLED with values representing the chunk's start position
   * within the returned character buffer and the length of the chunk.
   * @return The character array buffer within which the chunk occurs,
   * setting startAndLen's contents as a side-effect.
   */
  public char[] getStringValueChunk(int chunkIndex,
                                    int[] startAndLen);

  /**
   *  return an ID that represents the current node's expanded name.
   *
   * @param nodeHandle The handle to the node in question.
   *
   * @return the expanded-name id of the node.
   */
  public int getExpandedTypeID();

  /**
   * Given an expanded name, return an ID.  If the expanded-name does not
   * exist in the internal tables of the document being accessed, the entry
   * will be created, and the ID will be returned.  Any additional nodes 
   * that are created that have this expanded name will use this ID.
   * 
   * <p>%REVIEW%  Should this really be on the Cursor API? (Yes,
   * at whatever level creates/requests typed traversal. Darn it.)
   * 
   * <p>%REVIEW%  Should expanded names really be handled via 
   * numeric IDs, rather than pointers to rows of an object 
   * table, with rows predefined for the reserved types? The only
   * reason they're numbers is because it was convenient/efficient
   * to represent the DOM-style node types directly... but if we
   * create manefest-constant _objects_ for those types, it's
   * pretty much equivalent in efficiency and convenience.
   * 
   * <p>%REVIEW%  Shouldn't we really have a single centralized expanded-name
   * table that spans all documents in a transformation? That'd avoid
   * having to remap to compare, would reduce storage... Could make 
   * it cross-transform, but then you get into multitasking issue and 
   * questions of when/how to flush the cache.
   *
   * NEEDSDOC @param namespace
   * NEEDSDOC @param localName
   * NEEDSDOC @param type
   *
   * @return the expanded-name id of the node.
   */
  public int getExpandedTypeID(String namespace, String localName, int type);

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
   *  return the current node's DOM-style node name. This will
   * include names such as #text or #document.
   *
   * @return String Name of this node, which may be an empty string.
   * %REVIEW% (old issue) Document when empty string is possible...
   * I honestly can't think of such a case!!!
   */
  public String getNodeName();

  /**
   *  return the XPath node name.  This should be
   * the name as described by the XPath data model, NOT the DOM-style
   * name.
   *
   * @param nodeHandle the id of the node.
   * @return String Name of this node.
   */
  public String getNodeNameX();

  /**
   *  return the current node's DOM-style localname.
   * (As defined in Namespaces, this is the portion of the name after the
   * prefix, if present, or the whole node name if no prefix exists)
   *
   * @param nodeHandle the id of the node.
   * @return String Local name of this node.
   */
  public String getLocalName();

  /**
   * If the current node is a namespace node, 
   * return the prefix that it defines.
   * If it's an element or attribute node,
   * return the prefix it used to represent the namespace.
   * (As defined in Namespaces, this is the portion of the 
   * name before any colon character).
   * Otherwise, the node has no prefix; return "".
   * 
   * Note that this is different from DOM behavior, where
   * a namespace delcaration node is treated like an Attr.
   * 
   * @return String namespace prefix of this node, 
   * or "" if no explicit namespace prefix was given.
   */
  public String getPrefix();

  /**
   *  return the current node's namespace URI
   * (As defined in Namespaces, this is the declared URI which this node's
   * prefix -- or default in lieu thereof -- was mapped to.)
   *
   * @return String URI value of this node's namespace, or null if no
   * namespace was resolved.
   */
  public String getNamespaceURI();

  /**
   *  return the current node's node value. This is mostly
   * as defined by the DOM, but may ignore some conveniences.
   * <p>
   * @return String Value of this node, or null if not
   * meaningful for this node type.
   */
  public String getNodeValue();

  /**
   *  return the current node's DOM-style node type.
   *
   * Generally, returning short is false economy; it generally
   * costs performance rather than improving performance. I've gone
   * ahead and broken with tradition. I may be forced to change
   * it back after %REVIEW%.
   * 
   *  Is it finally time to change the API to int?
   *
   * @return int Node type, as per the DOM's Node._NODE constants.
   */
  public int getNodeType();
  
  // ============== Document Node methods ==============
  // Methods that would be on the Document node in a DOM,
  // and methods related to them.

  /**
   * Tests whether the current document implements a specific DOM feature and
   * that feature is supported by the current node.
   * @param feature The name of the feature to test.
   * @param version This is the version number of the feature to test.
   *   If the version is not
   *   specified, supporting any version of the feature will cause the
   *   method to return <code>true</code>.
   * @return Returns <code>true</code> if the specified feature is
   *   supported on this node, <code>false</code> otherwise.
   */
  public boolean isSupported(String feature, String version);

  /**
   * Return the base URI of the current document entity. If it is not known
   * (because the document was parsed from a socket connection or from
   * standard input, for example), the value of this property is unknown.
   *
   * @return the document base URI String object or null if unknown.
   */
  public String getDocumentBaseURI();

  /**
   * Set the base URI of the current document entity.
   * 
   * %REVIEW% Should this be exposed? It's in use, but theoretically
   * shouldn't be mucked with after being set at document creation.
   *
   * @param baseURI the document base URI String object or null if unknown.
   */
  public void setDocumentBaseURI(String baseURI);

  /**
   * Return the system identifier of the current document entity. If
   * it is not known, the value of this property is null.
   *
   * @param nodeHandle The node id, which can be any valid node handle.
   * @return the system identifier String object or null if unknown.
   */
  public String getDocumentSystemIdentifier();

  /**
   * Return the name of the character encoding scheme
   *        in which the document entity is expressed.
   *
   * @return the document encoding String object.
   */
  public String getDocumentEncoding();

  /**
   * Return an indication of the standalone status of the document,
   *        either "yes" or "no". This property is derived from the optional
   *        standalone document declaration in the XML declaration at the
   *        beginning of the document entity, and has no value if there is no
   *        standalone document declaration.
   * 
   * <p>%REVIEW% 
   * As far as I can tell, Xalan's only use of this property would
   * be in the default identity transform... which in the past
   * hasn't actually flowed through our data model. Do we really
   * need this on our Cursor API?
   *
   * @return the document standalone String object, either "yes", "no", or null.
   */
  public String getDocumentStandalone();

  /**
   * Return a string representing the XML version of the 
   * document containing the current node. This
   * property is derived from the XML declaration optionally present at the
   * beginning of the document entity, and has no value if there is no XML
   * declaration.
   *
   * NEEDSDOC @param documentHandle
   *
   * @return the document version String object
   */
  public String getDocumentVersion();

  /**
   * Return an indication of whether the processor has read 
   * the complete DTD for the current document.
   * If it is false, then certain properties (indicated in their
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
  public String getDocumentTypeDeclarationPublicIdentifier();

  /**
   * Returns the <code>Element</code> in the current document
   * whose <code>ID</code> is given by
   * <code>elementId</code>. If no such element exists, returns
   * <code>NULL</code>. Behavior is not defined if more than one element
   * has this <code>ID</code>. Attributes (including those
   * with the name "ID") are not of type ID unless so defined by DTD/Schema
   * information available to the DTM implementation.
   * Implementations that do not know whether attributes are of type ID or
   * not are expected to return <code>NULL</code>.
   *
   * IDs are scoped to a single document,
   * and this operation searches only the current document.
   * </p>
   *
   * @param elementId The unique <code>id</code> value for an element.
   * @return The handle of the matching element.
   */
  public XDMCursor getElementById(String elementId);
  
  /**
   * Returns the <code>Element</code> in the current document
   * whose <code>IDREF</code> is given by
   * <code>elementIdref</code>. If no such element exists, returns
   * <code>NULL</code>. Behavior is not defined if more than one element
   * has this <code>IDREF</code>. Attributes (including those
   * with the name "IDREF") are not of type IDREF unless so defined by DTD/Schema
   * information available to the DTM implementation.
   * Implementations that do not know whether attributes are of type ID or
   * not are expected to return <code>NULL</code>.
   *
   * IDREFs are scoped to a single document,
   * and this operation searches only the current document.</p>
   * 
   * <p>%REVIEW% 
   * This was added to support the IDREF Xpath function.
   * Does it need to be on the XDMCursor API, or should it
   * be a kind of axis iterator? The latter seems to make more
   * sense, especially since the NodeVector is going to have
   * to either return cursors or become an iterating cursor
   * itself.
   *
   * @param elementIdref The unique <code>idref</code> value for an element.
   * @return The handle of the matching element.
   */
  public NodeVector getElementByIdref(String elementIdref);

  /**
   * The getUnparsedEntityURI function returns the URI of the unparsed
   * entity with the specified name in the same document as the context
   * node (see [3.3 Unparsed Entities]). It returns the empty string if
   * there is no such entity.
   * <p>
   * XML processors may choose to use the System Identifier (if one
   * is provided) to resolve the entity, rather than the URI in the
   * Public Identifier. The details are dependent on the processor, and
   * we would have to support some form of plug-in resolver to handle
   * this properly. Currently, we simply return the System Identifier if
   * present, and hope that it a usable URI or that our caller can
   * map it to one.
   * %REVIEW% (old issue)
   * Resolve Public Identifiers... or consider changing function name.
   * <p>
   * If we find a relative URI
   * reference, XML expects it to be resolved in terms of the base URI
   * of the document. The DOM doesn't do that for us, and it isn't
   * entirely clear whether that should be done here; currently that's
   * pushed up to a higher level of our application. (Note that DOM Level
   * 1 didn't store the document's base URI.)
   * %REVIEW% (old issue) Consider resolving Relative URIs.
   * <p>
   * (The DOM's statement that "An XML processor may choose to
   * completely expand entities before the structure model is passed
   * to the DOM" refers only to parsed entities, not unparsed, and hence
   * doesn't affect this function.)
   *
   * @param name A string containing the Entity Name of the unparsed
   * entity.
   *
   * @return String containing the URI of the Unparsed Entity, or an
   * empty string if no such entity exists.
   */
  public String getUnparsedEntityURI(String name);

  // ============== XPATH/Xalan support methods ================

  /**
   * getLevel() is not needed. Theoretically it could be an
   * optimization, but the only use is in some of the 
   * unit tests, and they just print it out as
   * a (not very interesting) datum. 
   */

  /**
   * @return true if xsl:strip-space or xsl:preserve-space was
   * processed during construction of the current document.
   */
  public boolean supportsPreStripping();

  
  /**
   * @param otherCursor Cursor to compare with.
   * @return boolean true iff the current nodes of this cursor
   * and the one specified as a parameter are the same.
   */
  public boolean isSameNode(XDMCursor other);

  /**
   * Figure out whether this cursor's current node is after
   * another cursor's current node, in Document Order as defined
   * by the XPath model. This may not agree with the ordering defined
   * by other XML applications.
   * <p>
   * Note that there are subtleties regarding attribute and
   * namespace nodes. Be careful!
   * <p>
   * There are some cases where ordering isn't defined, and neither are
   * the results of this function -- though we'll generally return true.
   * <p>
   *
   * @param otherCursor Cursor to compare with.
   * @return false if our current node comes before otherCursor's
   * current node, otherwise return true.
   */
  public boolean isAfter(XDMCursor otherCursor);

  /**
   * 2. [element content whitespace] A boolean indicating whether a
   * text node represents white space appearing within element content
   * (see [XML], 2.10 "White Space Handling").  Note that validating
   * XML processors are required by XML 1.0 to provide this
   * information... but that DOM Level 2 did not support it, since it
   * depends on knowledge of the DTD which DOM2 could not guarantee
   * would be available.
   * <p>
   * If there is no declaration for the containing element, an XML
   * processor must assume that the whitespace could be meaningful and
   * return false. If no declaration has been read, but the [all
   * declarations processed] property of the document information item
   * is false (so there may be an unread declaration), then the value
   * of this property is indeterminate for white space characters and
   * should probably be reported as false. It is always false for text
   * nodes that contain anything other than (or in addition to) white
   * space.
   * <p>
   * Note too that it always returns false for non-Text nodes.
   * <p>
   * (The old DTM API for this was called isCharacterElementContentWhitespace;
   * I'm taking advantage of the opportunity to rename it!)
   *
   * @return <code>true</code> if the current node
   * definitely represents whitespace in
   * element content; <code>false</code> otherwise.
   */
  public boolean isWhitespaceInElementContent();

  /**
   *    10. [all declarations processed] This property is not strictly speaking
   *        part of the infoset of the document. Rather it is an indication of
   *        whether the processor has read the complete DTD. the current node's value is a
   *        boolean. If it is false, then certain properties (indicated in their
   *        descriptions below) may be unknown. If it is true, those properties
   *        are never unknown.
   *
   *
   * @param the document handle
   *
   * @return <code>true</code> if all declarations in the
   * current document were processed; <code>false</code> otherwise.
   */
  public boolean isDocumentAllDeclarationsProcessed();

  /**
   *     5. [specified] A flag indicating whether this attribute was actually
   *        specified in the start-tag of the current node's element, or was defaulted from the
   *        DTD (or schema).
   *
   * @param the attribute handle
   *
   * @return <code>true</code> if the current node is a 
   *            specified attribute;
   *         <code>false</code> if it was defaulted or
   *            is not an attribute node.
   */
  public boolean isAttributeSpecified();

  // ========== Direct SAX Dispatch, for optimization purposes ========
  // Some models implement special support for these calls,
  // eg to allow issuing multiple calls to characters() rather
  // than copying all the data into a concatenated string
  // and issuing that as a single call. These methods act
  // as a wrapper to permit either approach.

  /**
   * Directly call the
   * characters method on the passed ContentHandler for the
   * string-value of the current node (see http://www.w3.org/TR/xpath#data-model
   * for the definition of the current node's string-value). Multiple calls to the
   * ContentHandler's characters methods may well occur for a single call to
   * this method.
   *
   * @param ch A non-null reference to a ContentHandler.
   * @param normalize true if the content should be normalized according to
   * the rules for the XPath
   * <a href="http://www.w3.org/TR/xpath#function-normalize-space">normalize-space</a>
   * function.
   *
   * @throws org.xml.sax.SAXException
   */
  public void dispatchCharactersEvents(
    org.xml.sax.ContentHandler ch, boolean normalize)
      throws org.xml.sax.SAXException;

  /**
   * Directly create SAX parser events representing the XML content of
   * the current subtree. This is a "serialize" operation.
   *
   * @param ch A non-null reference to a ContentHandler.
   *
   * @throws org.xml.sax.SAXException
   */
  public void dispatchToEvents(org.xml.sax.ContentHandler ch)
    throws org.xml.sax.SAXException;

  // ******** DOM COMPATABILITY SUPPORT ******** 

  /**
   * @return a DOM Node view of the current node.
   */
  public org.w3c.dom.Node getNode();

  /**
   * Given a W3C DOM node, ask whether the
   * current document can yield a XDMCursor for it.
   * Generally, returns a valid cursor only if the
   * Node is actually mapped by this 
   * Document (eg, because it's a DOM2DTM which 
   * contains that Node).
   * 
   * <p>%REVIEW%  I don't *think* we actually need this
   * on the Cursor interface, unless we consider it a
   * convenience factory method for "SELF" Cursors...?
   *
   * @param node Non-null reference to a DOM node.
   *
   * @return a XDMCursor, or NULL if the current
   * document model doesn't recognize the provided DOM node.
   */
  public XDMCursor getXDMCursorFromNode(org.w3c.dom.Node node);

  // ******** TOOLING SUPPORT ********
  
  /**
   * Get the location of the current node in the source document.
   *
   * @return a SAX <code>SourceLocator</code> value or null if no location
   * is available
   */
  public SourceLocator getSourceLocator();


   /**
    * EXPERIMENTAL XPath2 Support:
    * 
    * Query schema type name of the current node.
    * 
    * %REVIEW% (old issue) Is this actually needed?
    * 
    * @return null if no type known, else returns the expanded-QName (namespace URI
    *	rather than prefix) of the type actually
    *    resolved in the instance document. Note that this may be derived from,
    *	rather than identical to, the type declared in the schema.
    */
   public String getSchemaTypeName();
  	
  /** 
    * EXPERIMENTAL XPath2 Support:
    * 
	* Query schema type namespace of the current node.
    * 
    * %REVIEW% (old issue) Is this actually needed?
    * 
    * @return null if no type known, else returns the namespace URI
    *	of the type actually resolved in the instance document. This may
    * 	be null if the default/unspecified namespace was used.
    *    Note that this may be derived from,
    *	rather than identical to, the type declared in the schema.
    */
   public String getSchemaTypeNamespace();

  /** EXPERIMENTAL XPath2 Support: Query schema type localname of
   * the current node.
   * 
   * %REVIEW% (old issue) Is this actually needed?
   * 
   * @return null if no type known, else returns the localname of the type
   *    resolved in the instance document. Note that this may be derived from,
   *	rather than identical to, the type declared in the schema.
   */
  public String getSchemaTypeLocalName();

  /** EXPERIMENTAL XPath2 Support: Query whether current node's type is
   *  derived from a specific type
   * 
   * @param namespace String containing URI of namespace for the type we're intersted in
   * @param localname String containing local name for the type we're intersted in
   * @return true if node has a Schema Type which equals or is derived from 
   *	the specified type. False if the node has no type or that type is not
   * 	derived from the specified type.
   */
  public boolean isNodeSchemaType(String namespace, String localname);
  
  /** EXPERIMENTAL XPath2 Support: Retrieve the typed value(s)
   * of the current node, based on the schema  type.
   * 
   * @return XSequence object containing one or more values and their type
   * information. If no typed value is available, returns an empty sequence.
   * */
  public XDMSequence getTypedValue();
  
  // ======== Tree Walkers =========
  
  /** @return an XDMTreeWalker whose root node is this
   * XDMCursor's current node.
   * 
   * %REVIEW% We could bind the visitor at this time. Should we
   * do so, rather than waiting for the walk request? Or should
   * a walker be reusable with a series of different visitors?
   * I *think* the latter is preferable, but it depends on our
   * actual usecases. Consider supporting both options???
   * 
   * @see XDMTreeWalker
   * @see XDMNodeVisitor
   * */
  public XDMTreeWalker getTreeWalker();
}
