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
 * <code>Iterators</code> are used to step through a set of nodes.  
 * It is modeled largely after the DOM NodeIterator.
 * 
 * <p>A DTMIterator is a somewhat unusual type of iterator, in that it 
 * can serve both single node iteration and random access.</p>
 * 
 * <p>The DTMIterator's traversal semantics, i.e. how it walks the tree,
 * are specified when it is created, possibly and probably by an XPath
 * <a href="http://www.w3.org/TR/xpath#NT-LocationPath>LocationPath</a> or 
 * a <a href="http://www.w3.org/TR/xpath#NT-UnionExpr">UnionExpr</a>.</p>
 * 
 * <p>A DTMIterator is meant to be created once as a master static object, and 
 * then cloned many times for runtime use.  Or the master object itself may 
 * be used for simpler use cases.</p>
 * <p>State: In progress!!</p>
 */
public interface DTMIterator
{

  // Constants returned by acceptNode

  /**
   * Accept the node.
   */
  public static final short FILTER_ACCEPT = 1;

  /**
   * Reject the node. (same as FILTER_SKIP).
   */
  public static final short FILTER_REJECT = 2;

  /**
   * Skip this single node. 
   */
  public static final short FILTER_SKIP = 3;
  
  /**
   * Set the environment in which this iterator operates, which should provide:
   * a node (the context node... same value as "root" defined below) 
   * a pair of non-zero positive integers (the context position and the context size) 
   * a set of variable bindings 
   * a function library 
   * the set of namespace declarations in scope for the expression.
   * 
   * <p>At this time the exact implementation of this environment is application 
   * dependent.  Probably a proper interface will be created fairly soon.</p>
   * 
   * @param environment The environment object.
   */
  public void setEnvironment(Object environment);

  /**
   * The root node of the <code>DTMIterator</code>, as specified when it
   * was created.  Note the root node is not the root node of the 
   * document tree, but the context node from where the itteration 
   * begins.
   *
   * @return nodeHandle int Handle of the context node.
   */
  public int getRoot();

  /**
   * The root node of the <code>DTMIterator</code>, as specified when it
   * was created.  Note the root node is not the root node of the 
   * document tree, but the context node from where the itteration 
   * begins.
   *
   * @param nodeHandle int Handle of the context node.
   */
  public void setRoot(int nodeHandle);
  
  /**
   * Reset the iterator to the start.
   */
  public void reset();

  /**
   * This attribute determines which node types are presented via the
   * iterator. The available set of constants is defined above.  
   * Nodes not accepted by
   * <code>whatToShow</code> will be skipped, but their children may still
   * be considered.
   *
   * @return one of the SHOW_XXX constants.
   */
  public int getWhatToShow();

  /**
   *  The value of this flag determines whether the children of entity
   * reference nodes are visible to the iterator. If false, they  and
   * their descendants will be rejected. Note that this rejection takes
   * precedence over <code>whatToShow</code> and the filter. 
   * <br>
   * <br> To produce a view of the document that has entity references
   * expanded and does not expose the entity reference node itself, use
   * the <code>whatToShow</code> flags to hide the entity reference node
   * and set <code>expandEntityReferences</code> to true when creating the
   * iterator. To produce a view of the document that has entity reference
   * nodes but no entity expansion, use the <code>whatToShow</code> flags
   * to show the entity reference node and set
   * <code>expandEntityReferences</code> to false.
   *
   * @return true if entity references will be expanded.
   */
  public boolean getExpandEntityReferences();

  /**
   * Returns the next node in the set and advances the position of the
   * iterator in the set. After a <code>DTMIterator</code> has setRoot called,
   * the first call to <code>nextNode()</code> returns the first node in
   * the set.
   * @return The next node handle in the set being iterated over, or
   *   -1 if there are no more members in that set.
   */
  public int nextNode();

  /**
   * Returns the previous node in the set and moves the position of the
   * <code>DTMIterator</code> backwards in the set.
   * @return The previous node handle in the set being iterated over,
   *   or <code>null</code> if there are no more members in that set.
   */
  public int previousNode();

  /**
   * Detaches the <code>DTMIterator</code> from the set which it iterated
   * over, releasing any computational resources and placing the iterator
   * in the INVALID state. After <code>detach</code> has been invoked,
   * calls to <code>nextNode</code> or <code>previousNode</code> will
   * raise a runtime exception.
   */
  public void detach();

  /**
   * Get the current node in the iterator.</a>.
   *
   * @return The current node handle, or -1.
   */
  public int getCurrentNode();

  /**
   * Tells if this NodeSet is "fresh", in other words, if
   * the first nextNode() that is called will return the
   * first node in the set.
   *
   * @return true if the iteration of this list has not yet begun.
   */
  public boolean isFresh();

  //========= Random Access ==========

  /**
   * If setShouldCacheNodes(true) is called, then nodes will
   * be cached, enabling random access, and giving the ability to do 
   * sorts and the like.  They are not cached by default.
   *
   * @param b true if the nodes should be cached.
   */
  public void setShouldCacheNodes(boolean b);

  /**
   * Get the current position, which is one less than
   * the next nextNode() call will retrieve.  i.e. if
   * you call getCurrentPos() and the return is 0, the next
   * fetch will take place at index 1.
   *
   * @return The position of the iteration.</a>.
   */
  public int getCurrentPos();

  /**
   * If an index is requested, NodeSet will call this method
   * to run the iterator to the index.  By default this sets
   * m_next to the index.  If the index argument is -1, this
   * signals that the iterator should be run to the end.
   *
   * @param index The index to run to, or -1 if the iterator should be run
   *              to the end.
   */
  public void runTo(int index);

  /**
   * Set the current position in the node set.
   * 
   * @param i Must be a valid index.
   */
  public void setCurrentPos(int i);

  /**
   * Returns the <code>node handle</code> of an item in the collection. If
   * <code>index</code> is greater than or equal to the number of nodes in
   * the list, this returns <code>null</code>.
   *
   * @param index of the item.
   * @return The node handle at the <code>index</code>th position in the
   *   <code>DTMIterator</code>, or <code>-1</code> if that is not a valid
   *   index.
   */
  public int item(int index);
  
  /**
   * The number of nodes in the list. The range of valid child node indices
   * is 0 to <code>length-1</code> inclusive.
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
  public DTMIterator cloneWithReset() throws CloneNotSupportedException;

  /**
   * Get a clone of this iterator, but don't reset the iteration in the 
   * process, so that it may be used from the current position.
   *
   * @return A clone of this object.
   *
   * @throws CloneNotSupportedException
   */
  public Object clone() throws CloneNotSupportedException;

}
