/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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
package org.apache.xml.dtm.ref.sax2dtm;

import org.apache.xml.dtm.*;
import org.apache.xml.dtm.ref.*;
import org.apache.xml.utils.SuballocatedIntVector;
import org.apache.xml.utils.XMLStringFactory;
import org.apache.xml.res.XMLMessages;
import org.apache.xml.res.XMLErrorResources;

import javax.xml.transform.Source;
import org.xml.sax.*;
import org.xml.sax.ext.*;

/**
 * SAX2DTM2 is an optimized version of SAX2DTM which is used in non-incremental case.
 * It is used as the super class of the XSLTC SAXImpl. Many of the interfaces in SAX2DTM
 * and DTMDefaultBase are overridden in SAX2DTM2 in order to allow fast, efficient 
 * access to the DTM model. Some nested iterators in DTMDefaultBaseIterators
 * are also overridden in SAX2DTM2 for performance reasons.
 *
 * %MK% The code in this class is critical to the XSLTC_DTM performance. Be very careful
 * when making changes here!
 */
public class SAX2DTM2 extends SAX2DTM
{

  /****************************************************************
   *       Optimized version of the nested iterators
   ****************************************************************/
   
  /**
   * Iterator that returns all immediate children of a given node
   */
  public final class ChildrenIterator extends InternalAxisIteratorBase
  {

    /**
     * Setting start to END should 'close' the iterator,
     * i.e. subsequent call to next() should return END.
     *
     * If the iterator is not restartable, this has no effect.
     * %REVIEW% Should it return/throw something in that case,
     * or set current node to END, to indicate request-not-honored?
     *
     * @param node Sets the root of the iteration.
     *
     * @return A DTMAxisIterator set to the start of the iteration.
     */
    public DTMAxisIterator setStartNode(int node)
    {
//%HZ%: Added reference to DTMDefaultBase.ROOTNODE back in, temporarily
      if (node == DTMDefaultBase.ROOTNODE)
        node = getDocument();
      if (_isRestartable)
      {
        _startNode = node;
        _currentNode = (node == DTM.NULL) ? DTM.NULL
                                          : _firstch2(makeNodeIdentity(node));

        return resetPosition();
      }

      return this;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END if no more
     * are available.
     */
    public int next()
    {
      if (_currentNode != NULL) {
        int node = _currentNode;
        _currentNode = _nextsib2(node);
        return returnNode(makeNodeHandle(node));
      }

      return END;
    }
  }  // end of ChildrenIterator

  /**
   * Iterator that returns the parent of a given node. Note that
   * this delivers only a single node; if you want all the ancestors,
   * see AncestorIterator.
   */
  public final class ParentIterator extends InternalAxisIteratorBase
  {

    /** The extended type ID that was requested. */
    private int _nodeType = -1;

    /**
     * Set start to END should 'close' the iterator,
     * i.e. subsequent call to next() should return END.
     *
     * @param node Sets the root of the iteration.
     *
     * @return A DTMAxisIterator set to the start of the iteration.
     */
    public DTMAxisIterator setStartNode(int node)
    {
//%HZ%: Added reference to DTMDefaultBase.ROOTNODE back in, temporarily
      if (node == DTMDefaultBase.ROOTNODE)
        node = getDocument();
      if (_isRestartable)
      {
        _startNode = node;
        _currentNode = getParent(node);

        return resetPosition();
      }

      return this;
    }

    /**
     * Set the node type of the parent that we're looking for.
     * Note that this does _not_ mean "find the nearest ancestor of
     * this type", but "yield the parent if it is of this type".
     *
     *
     * @param type extended type ID.
     *
     * @return ParentIterator configured with the type filter set.
     */
    public DTMAxisIterator setNodeType(final int type)
    {

      _nodeType = type;

      return this;
    }

    /**
     * Get the next node in the iteration. In this case, we return
     * only the immediate parent, _if_ it matches the requested nodeType.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {
      int result = _currentNode;

      if (_nodeType >= DTM.NTYPES) {
        if (_nodeType != getExpandedTypeID(_currentNode)) {
          result = END;
        }
      } else if (_nodeType != NULL) {
        if (_nodeType != getNodeType(_currentNode)) {
          result = END;
        }
      }

      _currentNode = END;

      return returnNode(result);
    }
  }  // end of ParentIterator

  /**
   * Iterator that returns children of a given type for a given node.
   * The functionality chould be achieved by putting a filter on top
   * of a basic child iterator, but a specialised iterator is used
   * for efficiency (both speed and size of translet).
   */
  public final class TypedChildrenIterator extends InternalAxisIteratorBase
  {

    /** The extended type ID that was requested. */
    private final int _nodeType;

    /**
     * Constructor TypedChildrenIterator
     *
     *
     * @param nodeType The extended type ID being requested.
     */
    public TypedChildrenIterator(int nodeType)
    {
      _nodeType = nodeType;
    }

    /**
     * Set start to END should 'close' the iterator,
     * i.e. subsequent call to next() should return END.
     *
     * @param node Sets the root of the iteration.
     *
     * @return A DTMAxisIterator set to the start of the iteration.
     */
    public DTMAxisIterator setStartNode(int node)
    {
//%HZ%: Added reference to DTMDefaultBase.ROOTNODE back in, temporarily
      if (node == DTMDefaultBase.ROOTNODE)
        node = getDocument();
      if (_isRestartable)
      {
        _startNode = node;
        _currentNode = (node == DTM.NULL)
                                   ? DTM.NULL
                                   : _firstch2(makeNodeIdentity(_startNode));

        return resetPosition();
      }

      return this;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {
      int eType;
      int node = _currentNode;

      int nodeType = _nodeType;

      if (nodeType >= DTM.NTYPES) {
        while (node != DTM.NULL && _exptype2(node) != nodeType) {
          node = _nextsib2(node);
        }
      } else {
        while (node != DTM.NULL) {
          eType = _exptype2(node);
          if (eType < DTM.NTYPES) {
            if (eType == nodeType) {
              break;
            }
          } else if (m_expandedNameTable.getType(eType) == nodeType) {
            break;
          }
          node = _nextsib2(node);
        }
      }

      if (node == DTM.NULL) {
        _currentNode = DTM.NULL;
        return DTM.NULL;
      } else {
        _currentNode = _nextsib2(node);
        return returnNode(makeNodeHandle(node));
      }

    }
  }  // end of TypedChildrenIterator

  /**
   * Iterator that returns the namespace nodes as defined by the XPath data model 
   * for a given node, filtered by extended type ID.
   */
  public class TypedRootIterator extends RootIterator
  {

    /** The extended type ID that was requested. */
    private final int _nodeType;

    /**
     * Constructor TypedRootIterator
     *
     * @param nodeType The extended type ID being requested.
     */
    public TypedRootIterator(int nodeType)
    {
      super();
      _nodeType = nodeType;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {
    	if(_startNode == _currentNode)
        return NULL;

      int nodeType = _nodeType;
      int node = _startNode;
      int expType = getExpandedTypeID(node);

      _currentNode = node;

      if (nodeType >= DTM.NTYPES) {
        if (nodeType == expType) {
          return returnNode(node);
        }
      } else {
        if (expType < DTM.NTYPES) {
          if (expType == nodeType) {
            return returnNode(node);
          }
        } else {
          if (m_expandedNameTable.getType(expType) == nodeType) {
            return returnNode(node);
          }
        }
      }

      return END;
    }
  }  // end of TypedRootIterator

  /**
   * Iterator that returns all siblings of a given node.
   */
  public class FollowingSiblingIterator extends InternalAxisIteratorBase
  {

    /**
     * Set start to END should 'close' the iterator,
     * i.e. subsequent call to next() should return END.
     *
     * @param node Sets the root of the iteration.
     *
     * @return A DTMAxisIterator set to the start of the iteration.
     */
    public DTMAxisIterator setStartNode(int node)
    {
//%HZ%: Added reference to DTMDefaultBase.ROOTNODE back in, temporarily
      if (node == DTMDefaultBase.ROOTNODE)
        node = getDocument();
      if (_isRestartable)
      {
        _startNode = node;
        _currentNode = makeNodeIdentity(node);

        return resetPosition();
      }

      return this;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {
      _currentNode = (_currentNode == DTM.NULL) ? DTM.NULL
                                                : _nextsib2(_currentNode);
      return returnNode(makeNodeHandle(_currentNode));
    }
  }  // end of FollowingSiblingIterator

  /**
   * Iterator that returns all following siblings of a given node.
   */
  public final class TypedFollowingSiblingIterator
          extends FollowingSiblingIterator
  {

    /** The extended type ID that was requested. */
    private final int _nodeType;

    /**
     * Constructor TypedFollowingSiblingIterator
     *
     *
     * @param type The extended type ID being requested.
     */
    public TypedFollowingSiblingIterator(int type)
    {
      _nodeType = type;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {
      if (_currentNode == DTM.NULL) {
        return DTM.NULL;
      }

      int node = _currentNode;
      int eType;
      int nodeType = _nodeType;

      if (nodeType >= DTM.NTYPES) {
        do {
          node = _nextsib2(node);
        } while (node != DTM.NULL && _exptype2(node) != nodeType);
      } else {
        while ((node = _nextsib2(node)) != DTM.NULL) {
          eType = _exptype2(node);
          if (eType < DTM.NTYPES) {
            if (eType == nodeType) {
              break;
            }
          } else if (m_expandedNameTable.getType(eType) == nodeType) {
            break;
          }
        }
      }

      _currentNode = node;

      return (_currentNode == DTM.NULL)
                      ? DTM.NULL
                      : returnNode(makeNodeHandle(_currentNode));
    }
  }  // end of TypedFollowingSiblingIterator

  /**
   * Iterator that returns attribute nodes (of what nodes?)
   */
  public final class AttributeIterator extends InternalAxisIteratorBase
  {

    // assumes caller will pass element nodes

    /**
     * Set start to END should 'close' the iterator,
     * i.e. subsequent call to next() should return END.
     *
     * @param node Sets the root of the iteration.
     *
     * @return A DTMAxisIterator set to the start of the iteration.
     */
    public DTMAxisIterator setStartNode(int node)
    {
//%HZ%: Added reference to DTMDefaultBase.ROOTNODE back in, temporarily
      if (node == DTMDefaultBase.ROOTNODE)
        node = getDocument();
      if (_isRestartable)
      {
        _startNode = node;
        _currentNode = getFirstAttributeIdentity(makeNodeIdentity(node));

        return resetPosition();
      }

      return this;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {

      final int node = _currentNode;

      if (node != NULL) {
        _currentNode = getNextAttributeIdentity(node);
        return returnNode(makeNodeHandle(node));
      }

      return NULL;
    }
  }  // end of AttributeIterator

  /**
   * Iterator that returns attribute nodes of a given type
   */
  public final class TypedAttributeIterator extends InternalAxisIteratorBase
  {

    /** The extended type ID that was requested. */
    private final int _nodeType;

    /**
     * Constructor TypedAttributeIterator
     *
     *
     * @param nodeType The extended type ID that is requested.
     */
    public TypedAttributeIterator(int nodeType)
    {
      _nodeType = nodeType;
    }

    // assumes caller will pass element nodes

    /**
     * Set start to END should 'close' the iterator,
     * i.e. subsequent call to next() should return END.
     *
     * @param node Sets the root of the iteration.
     *
     * @return A DTMAxisIterator set to the start of the iteration.
     */
    public DTMAxisIterator setStartNode(int node)
    {
      if (_isRestartable)
      {
        _startNode = node;

        _currentNode = getTypedAttribute(node, _nodeType);

        return resetPosition();
      }

      return this;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {

      final int node = _currentNode;

      // singleton iterator, since there can only be one attribute of 
      // a given type.
      _currentNode = NULL;

      return returnNode(node);
    }
  }  // end of TypedAttributeIterator

  /**
   * Iterator that returns preceding siblings of a given node
   */
  public class PrecedingSiblingIterator extends InternalAxisIteratorBase
  {

    /**
     * The node identity of _startNode for this iterator
     */
    protected int _startNodeID;

    /**
     * True if this iterator has a reversed axis.
     *
     * @return true.
     */
    public boolean isReverse()
    {
      return true;
    }

    /**
     * Set start to END should 'close' the iterator,
     * i.e. subsequent call to next() should return END.
     *
     * @param node Sets the root of the iteration.
     *
     * @return A DTMAxisIterator set to the start of the iteration.
     */
    public DTMAxisIterator setStartNode(int node)
    {
//%HZ%: Added reference to DTMDefaultBase.ROOTNODE back in, temporarily
      if (node == DTMDefaultBase.ROOTNODE)
        node = getDocument();
      if (_isRestartable)
      {
        _startNode = node;
        node = _startNodeID = makeNodeIdentity(node);

        if(node == NULL)
        {
          _currentNode = node;
          return resetPosition();
        }

        int type = m_expandedNameTable.getType(_exptype2(node));
        if(ExpandedNameTable.ATTRIBUTE == type 
           || ExpandedNameTable.NAMESPACE == type )
        {
          _currentNode = node;
        }
        else
        {
          // Be careful to handle the Document node properly
          _currentNode = _parent2(node);
          if(NULL!=_currentNode)	
            _currentNode = _firstch2(_currentNode);
          else
            _currentNode = node;
        }

        return resetPosition();
      }

      return this;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {

      if (_currentNode == _startNodeID || _currentNode == DTM.NULL)
      {
        return NULL;
      }
      else
      {
        final int node = _currentNode;
        _currentNode = _nextsib2(node);

        return returnNode(makeNodeHandle(node));
      }
    }
  }  // end of PrecedingSiblingIterator

  /**
   * Iterator that returns preceding siblings of a given type for
   * a given node
   */
  public final class TypedPrecedingSiblingIterator
          extends PrecedingSiblingIterator
  {

    /** The extended type ID that was requested. */
    private final int _nodeType;

    /**
     * Constructor TypedPrecedingSiblingIterator
     *
     *
     * @param type The extended type ID being requested.
     */
    public TypedPrecedingSiblingIterator(int type)
    {
      _nodeType = type;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {
      int node = _currentNode;
      int expType;

      int nodeType = _nodeType;
      int startID = _startNodeID;

      if (nodeType >= DTM.NTYPES) {
        while (node != NULL && node != startID && _exptype2(node) != nodeType) {
          node = _nextsib2(node);
        }
      } else {
        while (node != NULL && node != startID) {
          expType = _exptype2(node);
          if (expType < DTM.NTYPES) {
            if (expType == nodeType) {
              break;
            }
          } else {
            if (m_expandedNameTable.getType(expType) == nodeType) {
              break;
            }
          }
          node = _nextsib2(node);
        }
      }

      if (node == DTM.NULL || node == _startNodeID) {
        _currentNode = NULL;
        return NULL;
      } else {
        _currentNode = _nextsib2(node);
        return returnNode(makeNodeHandle(node));
      }
    }
  }  // end of TypedPrecedingSiblingIterator

  /**
   * Iterator that returns preceding nodes of a given node.
   * This includes the node set {root+1, start-1}, but excludes
   * all ancestors, attributes, and namespace nodes.
   */
  public class PrecedingIterator extends InternalAxisIteratorBase
  {

    /** The max ancestors, but it can grow... */
    private final int _maxAncestors = 8;

    /**
     * The stack of start node + ancestors up to the root of the tree,
     *  which we must avoid.
     */
    protected int[] _stack = new int[_maxAncestors];

    /** (not sure yet... -sb) */
    protected int _sp, _oldsp;

    protected int _markedsp, _markedNode, _markedDescendant;

    /* _currentNode precedes candidates.  This is the identity, not the handle! */

    /**
     * True if this iterator has a reversed axis.
     *
     * @return true since this iterator is a reversed axis.
     */
    public boolean isReverse()
    {
      return true;
    }

    /**
     * Returns a deep copy of this iterator.   The cloned iterator is not reset.
     *
     * @return a deep copy of this iterator.
     */
    public DTMAxisIterator cloneIterator()
    {
      _isRestartable = false;

      try
      {
        final PrecedingIterator clone = (PrecedingIterator) super.clone();
        final int[] stackCopy = new int[_stack.length];
        System.arraycopy(_stack, 0, stackCopy, 0, _stack.length);

        clone._stack = stackCopy;

        // return clone.reset();
        return clone;
      }
      catch (CloneNotSupportedException e)
      {
        throw new DTMException(XMLMessages.createXMLMessage(XMLErrorResources.ER_ITERATOR_CLONE_NOT_SUPPORTED, null)); //"Iterator clone not supported.");
      }
    }

    /**
     * Set start to END should 'close' the iterator,
     * i.e. subsequent call to next() should return END.
     *
     * @param node Sets the root of the iteration.
     *
     * @return A DTMAxisIterator set to the start of the iteration.
     */
    public DTMAxisIterator setStartNode(int node)
    {
//%HZ%: Added reference to DTMDefaultBase.ROOTNODE back in, temporarily
      if (node == DTMDefaultBase.ROOTNODE)
        node = getDocument();
      if (_isRestartable)
      {
        node = makeNodeIdentity(node);

        // iterator is not a clone
        int parent, index;

       if (_type2(node) == DTM.ATTRIBUTE_NODE)
        node = _parent2(node);

        _startNode = node;
        _stack[index = 0] = node;
        
       

		parent=node;
		while ((parent = _parent2(parent)) != NULL)
		{
			if (++index == _stack.length)
			{
				final int[] stack = new int[index + 4];
				System.arraycopy(_stack, 0, stack, 0, index);
				_stack = stack;
			}
			_stack[index] = parent;
        }
        if(index>0)
	        --index; // Pop actual root node (if not start) back off the stack

        _currentNode=_stack[index]; // Last parent before root node

        _oldsp = _sp = index;

        return resetPosition();
      }

      return this;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {
    	// Bugzilla 8324: We were forgetting to skip Attrs and NS nodes.
    	// Also recoded the loop controls for clarity and to flatten out
    	// the tail-recursion.
   		for(++_currentNode; 
   			_sp>=0; 
   			++_currentNode)
   		{
   			if(_currentNode < _stack[_sp])
   			{
   				if(_type2(_currentNode) != ATTRIBUTE_NODE &&
   					_type2(_currentNode) != NAMESPACE_NODE)
   					return returnNode(makeNodeHandle(_currentNode));
   			}
   			else
   				--_sp;
   		}
   		return NULL;
    }

    // redefine DTMAxisIteratorBase's reset

    /**
     * Resets the iterator to the last start node.
     *
     * @return A DTMAxisIterator, which may or may not be the same as this
     *         iterator.
     */
    public DTMAxisIterator reset()
    {

      _sp = _oldsp;

      return resetPosition();
    }

    public void setMark() {
        _markedsp = _sp;
        _markedNode = _currentNode;
        _markedDescendant = _stack[0];
    }

    public void gotoMark() {
        _sp = _markedsp;
        _currentNode = _markedNode;
    }
  }  // end of PrecedingIterator

  /**
   * Iterator that returns preceding nodes of agiven type for a
   * given node. This includes the node set {root+1, start-1}, but
   * excludes all ancestors.
   */
  public final class TypedPrecedingIterator extends PrecedingIterator
  {

    /** The extended type ID that was requested. */
    private final int _nodeType;

    /**
     * Constructor TypedPrecedingIterator
     *
     *
     * @param type The extended type ID being requested.
     */
    public TypedPrecedingIterator(int type)
    {
      _nodeType = type;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {
      int node = _currentNode;
      int nodeType = _nodeType;

      if (nodeType >= DTM.NTYPES) {
        while (true) {
          node = node + 1;

          if (_sp < 0) {
            node = NULL;
            break;
          } else if (node >= _stack[_sp]) {
            if (--_sp < 0) {
              node = NULL;
              break;
            }
          } else if (_exptype2(node) == nodeType) {
            break;
          }
        }
      } else {
        int expType;

        while (true) {
          node = node + 1;

          if (_sp < 0) {
            node = NULL;
            break;
          } else if (node >= _stack[_sp]) {
            if (--_sp < 0) {
              node = NULL;
              break;
            }
          } else {
            expType = _exptype2(node);
            if (expType < DTM.NTYPES) {
              if (expType == nodeType) {
                break;
              }
            } else {
              if (m_expandedNameTable.getType(expType) == nodeType) {
                break;
              }
            }
          }
        }
      }

      _currentNode = node;
             
      return (node == NULL) ? NULL : returnNode(makeNodeHandle(node));
    }
  }  // end of TypedPrecedingIterator

  /**
   * Iterator that returns following nodes of for a given node.
   */
  public class FollowingIterator extends InternalAxisIteratorBase
  {
    DTMAxisTraverser m_traverser; // easier for now
    
    public FollowingIterator()
    {
      m_traverser = getAxisTraverser(Axis.FOLLOWING);
    }

    /**
     * Set start to END should 'close' the iterator,
     * i.e. subsequent call to next() should return END.
     *
     * @param node Sets the root of the iteration.
     *
     * @return A DTMAxisIterator set to the start of the iteration.
     */
    public DTMAxisIterator setStartNode(int node)
    {
//%HZ%: Added reference to DTMDefaultBase.ROOTNODE back in, temporarily
      if (node == DTMDefaultBase.ROOTNODE)
        node = getDocument();
      if (_isRestartable)
      {
        _startNode = node;

        // ?? -sb
        // find rightmost descendant (or self)
        // int current;
        // while ((node = getLastChild(current = node)) != NULL){}
        // _currentNode = current;
        _currentNode = m_traverser.first(node);

        // _currentNode precedes possible following(node) nodes
        return resetPosition();
      }

      return this;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {

      int node = _currentNode;

      _currentNode = m_traverser.next(_startNode, _currentNode);

      return returnNode(node);
    }
  }  // end of FollowingIterator

  /**
   * Iterator that returns following nodes of a given type for a given node.
   */
  public final class TypedFollowingIterator extends FollowingIterator
  {

    /** The extended type ID that was requested. */
    private final int _nodeType;

    /**
     * Constructor TypedFollowingIterator
     *
     *
     * @param type The extended type ID being requested.
     */
    public TypedFollowingIterator(int type)
    {
      _nodeType = type;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {

      int node;

      do{
       node = _currentNode;

      _currentNode = m_traverser.next(_startNode, _currentNode);

      } 
      while (node != DTM.NULL
             && (getExpandedTypeID(node) != _nodeType && getNodeType(node) != _nodeType));

      return (node == DTM.NULL ? DTM.NULL :returnNode(node));
    }
  }  // end of TypedFollowingIterator

  /**
   * Iterator that returns the ancestors of a given node in document
   * order.  (NOTE!  This was changed from the XSLTC code!)
   */
  public class AncestorIterator extends InternalAxisIteratorBase
  {
    org.apache.xml.utils.NodeVector m_ancestors = 
         new org.apache.xml.utils.NodeVector();
         
    int m_ancestorsPos;

    int m_markedPos;
    
    /** The real start node for this axes, since _startNode will be adjusted. */
    int m_realStartNode;
    
    /**
     * Get start to END should 'close' the iterator,
     * i.e. subsequent call to next() should return END.
     *
     * @return The root node of the iteration.
     */
    public int getStartNode()
    {
      return m_realStartNode;
    }

    /**
     * True if this iterator has a reversed axis.
     *
     * @return true since this iterator is a reversed axis.
     */
    public final boolean isReverse()
    {
      return true;
    }

    /**
     * Returns a deep copy of this iterator.  The cloned iterator is not reset.
     *
     * @return a deep copy of this iterator.
     */
    public DTMAxisIterator cloneIterator()
    {
      _isRestartable = false;  // must set to false for any clone

      try
      {
        final AncestorIterator clone = (AncestorIterator) super.clone();

        clone._startNode = _startNode;

        // return clone.reset();
        return clone;
      }
      catch (CloneNotSupportedException e)
      {
        throw new DTMException(XMLMessages.createXMLMessage(XMLErrorResources.ER_ITERATOR_CLONE_NOT_SUPPORTED, null)); //"Iterator clone not supported.");
      }
    }

    /**
     * Set start to END should 'close' the iterator,
     * i.e. subsequent call to next() should return END.
     *
     * @param node Sets the root of the iteration.
     *
     * @return A DTMAxisIterator set to the start of the iteration.
     */
    public DTMAxisIterator setStartNode(int node)
    {
//%HZ%: Added reference to DTMDefaultBase.ROOTNODE back in, temporarily
      if (node == DTMDefaultBase.ROOTNODE)
        node = getDocument();
      m_realStartNode = node;

      if (_isRestartable)
      {
        int nodeID = makeNodeIdentity(node);

        if (!_includeSelf && node != DTM.NULL) {
          nodeID = _parent2(nodeID);
          node = makeNodeHandle(nodeID);
        }

        _startNode = node;

        while (nodeID != END) {
          m_ancestors.addElement(node);
          nodeID = _parent2(nodeID);
          node = makeNodeHandle(nodeID);
        }
        m_ancestorsPos = m_ancestors.size()-1;

        _currentNode = (m_ancestorsPos>=0)
                               ? m_ancestors.elementAt(m_ancestorsPos)
                               : DTM.NULL;

        return resetPosition();
      }

      return this;
    }

    /**
     * Resets the iterator to the last start node.
     *
     * @return A DTMAxisIterator, which may or may not be the same as this
     *         iterator.
     */
    public DTMAxisIterator reset()
    {

      m_ancestorsPos = m_ancestors.size()-1;

      _currentNode = (m_ancestorsPos>=0) ? m_ancestors.elementAt(m_ancestorsPos)
                                         : DTM.NULL;

      return resetPosition();
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {

      int next = _currentNode;
      
      int pos = --m_ancestorsPos;

      _currentNode = (pos >= 0) ? m_ancestors.elementAt(m_ancestorsPos)
                                : DTM.NULL;
      
      return returnNode(next);
    }

    public void setMark() {
        m_markedPos = m_ancestorsPos;
    }

    public void gotoMark() {
        m_ancestorsPos = m_markedPos;
        _currentNode = m_ancestorsPos>=0 ? m_ancestors.elementAt(m_ancestorsPos)
                                         : DTM.NULL;
    }
  }  // end of AncestorIterator

  /**
   * Typed iterator that returns the ancestors of a given node.
   */
  public final class TypedAncestorIterator extends AncestorIterator
  {

    /** The extended type ID that was requested. */
    private final int _nodeType;

    /**
     * Constructor TypedAncestorIterator
     *
     *
     * @param type The extended type ID being requested.
     */
    public TypedAncestorIterator(int type)
    {
      _nodeType = type;
    }

    /**
     * Set start to END should 'close' the iterator,
     * i.e. subsequent call to next() should return END.
     *
     * @param node Sets the root of the iteration.
     *
     * @return A DTMAxisIterator set to the start of the iteration.
     */
    public DTMAxisIterator setStartNode(int node)
    {
//%HZ%: Added reference to DTMDefaultBase.ROOTNODE back in, temporarily
      if (node == DTMDefaultBase.ROOTNODE)
        node = getDocument();
      m_realStartNode = node;

      if (_isRestartable)
      {
        int nodeID = makeNodeIdentity(node);
        int nodeType = _nodeType;

        if (!_includeSelf && node != DTM.NULL) {
          nodeID = _parent2(nodeID);
        }

        _startNode = node;

        if (nodeType >= DTM.NTYPES) {
          while (nodeID != END) {
            int eType = _exptype2(nodeID);

            if (eType == nodeType) {
              m_ancestors.addElement(makeNodeHandle(nodeID));
            }
            nodeID = _parent2(nodeID);
          }
        } else {
          while (nodeID != END) {
            int eType = _exptype2(nodeID);

            if ((eType >= DTM.NTYPES
                    && m_expandedNameTable.getType(eType) == nodeType)
                || (eType < DTM.NTYPES && eType == nodeType)) {
              m_ancestors.addElement(makeNodeHandle(nodeID));
            }
            nodeID = _parent2(nodeID);
          }
        }
        m_ancestorsPos = m_ancestors.size()-1;

        _currentNode = (m_ancestorsPos>=0)
                               ? m_ancestors.elementAt(m_ancestorsPos)
                               : DTM.NULL;

        return resetPosition();
      }

      return this;
    }
  }  // end of TypedAncestorIterator

  /**
   * Iterator that returns the descendants of a given node.
   */
  public class DescendantIterator extends InternalAxisIteratorBase
  {

    /**
     * Set start to END should 'close' the iterator,
     * i.e. subsequent call to next() should return END.
     *
     * @param node Sets the root of the iteration.
     *
     * @return A DTMAxisIterator set to the start of the iteration.
     */
    public DTMAxisIterator setStartNode(int node)
    {
//%HZ%: Added reference to DTMDefaultBase.ROOTNODE back in, temporarily
      if (node == DTMDefaultBase.ROOTNODE)
        node = getDocument();
      if (_isRestartable)
      {
        node = makeNodeIdentity(node);
        _startNode = node;

        if (_includeSelf)
          node--;

        _currentNode = node;

        return resetPosition();
      }

      return this;
    }

    /**
     * Tell if this node identity is a descendant.  Assumes that
     * the node info for the element has already been obtained.
     *
     * This one-sided test works only if the parent has been
     * previously tested and is known to be a descendent. It fails if
     * the parent is the _startNode's next sibling, or indeed any node
     * that follows _startNode in document order.  That may suffice
     * for this iterator, but it's not really an isDescendent() test.
     * %REVIEW% rename?
     *
     * @param identity The index number of the node in question.
     * @return true if the index is a descendant of _startNode.
     */
    protected boolean isDescendant(int identity)
    {
      return (_parent2(identity) >= _startNode) || (_startNode == identity);
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {
      if (_startNode == NULL) {
        return NULL;
      }

      if (_includeSelf && (_currentNode + 1) == _startNode)
          return returnNode(makeNodeHandle(++_currentNode)); // | m_dtmIdent);

      int node = _currentNode;
      int type;

      do {
        node++;
        type = _type(node);

        if (NULL == type ||!isDescendant(node)) {
          _currentNode = NULL;
          return END;
        }
      } while(ATTRIBUTE_NODE == type || TEXT_NODE == type
                 || NAMESPACE_NODE == type);

      _currentNode = node;
      return returnNode(makeNodeHandle(node));  // make handle.
    }
  
    /**
     * Reset.
     *
     */ 
  public DTMAxisIterator reset()
  {

    final boolean temp = _isRestartable;

    _isRestartable = true;

    setStartNode(makeNodeHandle(_startNode));

    _isRestartable = temp;

    return this;
  }
    
  }  // end of DescendantIterator

  /**
   * Typed iterator that returns the descendants of a given node.
   */
  public final class TypedDescendantIterator extends DescendantIterator
  {

    /** The extended type ID that was requested. */
    private final int _nodeType;

    /**
     * Constructor TypedDescendantIterator
     *
     *
     * @param nodeType Extended type ID being requested.
     */
    public TypedDescendantIterator(int nodeType)
    {
      _nodeType = nodeType;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {
      int node;
      int type;

      if (_startNode == NULL) {
        return NULL;
      }

      node = _currentNode;

      do
      {
        node++;
        type = _type2(node);

        if (NULL == type ||!isDescendant(node)) {
          _currentNode = NULL;
          return END;
        }
      }
      while (type != _nodeType && _exptype2(node) != _nodeType);

      _currentNode = node;
      return returnNode(makeNodeHandle(node));
    }
  }  // end of TypedDescendantIterator

  /**
   * Iterator that returns a given node only if it is of a given type.
   */
  public final class TypedSingletonIterator extends SingletonIterator
  {

    /** The extended type ID that was requested. */
    private final int _nodeType;

    /**
     * Constructor TypedSingletonIterator
     *
     *
     * @param nodeType The extended type ID being requested.
     */
    public TypedSingletonIterator(int nodeType)
    {
      _nodeType = nodeType;
    }

    /**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {

      //final int result = super.next();
      final int result = _currentNode;
      int nodeType = _nodeType;

      _currentNode = END;

      if (nodeType >= DTM.NTYPES) {
        if (getExpandedTypeID(result) == nodeType) {
          return returnNode(result);
        }
      } else {
        if (getNodeType(result) == nodeType) {
          return returnNode(result);
        }
      }

      return NULL;
    }
  }  // end of TypedSingletonIterator

  /*******************************************************************
   *                End of nested iterators          
   *******************************************************************/


  // %OPT% Array references which are used to cache the map0 arrays in 
  // SuballocatedIntVectors. Using the cached arrays reduces the level
  // of indirection and results in better performance than just calling
  // SuballocatedIntVector.elementAt().
  private final int[] m_exptype_map0;
  private final int[] m_nextsib_map0;
  private final int[] m_firstch_map0;
  private final int[] m_parent_map0;
  
  // Double array references to the map arrays in SuballocatedIntVectors.
  private int[][] m_exptype_map;
  private int[][] m_nextsib_map;
  private int[][] m_firstch_map;
  private int[][] m_parent_map;

  // %OPT% Cache the array of extended types in this class
  private ExtendedType[] m_extendedTypes;
  
  // Cache the shift and mask values for the SuballocatedIntVectors.
  protected final int m_SHIFT;
  protected final int m_MASK;
  
  // A constant for empty string
  private static final String EMPTY_STR = "";

  /**
   * Construct a SAX2DTM2 object using the default block size.
   */
  public SAX2DTM2(DTMManager mgr, Source source, int dtmIdentity,
                 DTMWSFilter whiteSpaceFilter,
                 XMLStringFactory xstringfactory,
                 boolean doIndexing)
  {

    this(mgr, source, dtmIdentity, whiteSpaceFilter,
          xstringfactory, doIndexing, m_initialblocksize);
  }
  
  /**
   * Construct a SAX2DTM2 object using the given block size.
   */
  public SAX2DTM2(DTMManager mgr, Source source, int dtmIdentity,
                 DTMWSFilter whiteSpaceFilter,
                 XMLStringFactory xstringfactory,
                 boolean doIndexing,
                 int blocksize)
  {

    super(mgr, source, dtmIdentity, whiteSpaceFilter,
          xstringfactory, doIndexing, blocksize);
    
    // Initialize the values of m_SHIFT and m_MASK.
    int shift;
    for(shift=0; (blocksize>>>=1) != 0; ++shift);
    
    m_SHIFT = shift;
    m_MASK = (1<<shift) - 1;
    
    // Set the map0 values in the constructor.
    m_exptype_map0 = m_exptype.getMap0();
    m_nextsib_map0 = m_nextsib.getMap0();
    m_firstch_map0 = m_firstch.getMap0();
    m_parent_map0  = m_parent.getMap0();
  }
  
  /**
   * The optimized version of DTMDefaultBase._exptype().
   */
  public final int _exptype2(int identity)
  {
    //return m_exptype.elementAt(identity);
    
    if (identity < m_blocksize)
      return m_exptype_map0[identity];
    else
      return m_exptype_map[identity>>>m_SHIFT][identity&m_MASK];    
  }
  
  /**
   * The optimized version of DTMDefaultBase._nextsib().
   */
  public final int _nextsib2(int identity)
  {
    //return m_nextsib.elementAt(identity);
    
    if (identity < m_blocksize)
      return m_nextsib_map0[identity];
    else
      return m_nextsib_map[identity>>>m_SHIFT][identity&m_MASK];     
  }
  
  /**
   * The optimized version of DTMDefaultBase._firstch().
   */
  public final int _firstch2(int identity)
  {
    //return m_firstch.elementAt(identity);
    
    if (identity < m_blocksize)
      return m_firstch_map0[identity];
    else
      return m_firstch_map[identity>>>m_SHIFT][identity&m_MASK];    
  }
  
  /**
   * The optimized version of DTMDefaultBase._parent().
   */
  public final int _parent2(int identity)
  {
    //return m_parent.elementAt(identity);
    
    if (identity < m_blocksize)
      return m_parent_map0[identity];
    else
      return m_parent_map[identity>>>m_SHIFT][identity&m_MASK];    
  }
  
  /**
   * The optimized version of DTMDefaultBase._type().
   */
  public final int _type2(int identity)
  {
    int eType = _exptype2(identity);
  	
    if (NULL != eType)
      return m_extendedTypes[eType].getNodeType();
    else
      return NULL;
  }
    
  /**
   * Receive notification of the end of the document.
   *
   * @throws SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#endDocument
   */
  public void endDocument() throws SAXException
  {
    super.endDocument();
    
    // Add a NULL entry to the end of the node arrays as
    // the end indication.
    m_exptype.addElement(NULL);
    m_parent.addElement(NULL);
    m_nextsib.addElement(NULL);
    m_firstch.addElement(NULL);
    
    // Set the cached references after the document is built.
    m_extendedTypes = m_expandedNameTable.getExtendedTypes();
    m_exptype_map = m_exptype.getMap();
    m_nextsib_map = m_nextsib.getMap();
    m_firstch_map = m_firstch.getMap();
    m_parent_map  = m_parent.getMap();
  }

  /**
   * The optimized version of DTMDefaultBase.getNodeType().
   * 
   * Given a node handle, return its DOM- style node type.
   * 
   * @param  nodeHandle The node id.
   * @return int Node type, as per the DOM's Node._NODE constants.
   */
  public final int getNodeType2(int nodeHandle)
  {
    if (nodeHandle == DTM.NULL)
      return DTM.NULL;
    else
      return m_extendedTypes[_exptype2(makeNodeIdentity(nodeHandle))].getNodeType();
  }

  /**
   * The optimized version of DTMDefaultBase.getFirstAttribute().
   * 
   * Given a node handle, get the index of the node's first attribute.
   *
   * @param nodeHandle int Handle of the node.
   * @return Handle of first attribute, or DTM.NULL to indicate none exists.
   */
  public final int getFirstAttribute(int nodeHandle)
  {
    int nodeID = makeNodeIdentity(nodeHandle);

    if (nodeID == DTM.NULL)
      return DTM.NULL;
    
    int type = _type2(nodeID);

    if (DTM.ELEMENT_NODE == type)
    {
      // Assume that attributes and namespaces immediately follow the element.
      while (true)
      {
        nodeID++;
	// Assume this can not be null.
	type = _type2(nodeID);

	if (type == DTM.ATTRIBUTE_NODE)
	{
	  return makeNodeHandle(nodeID);
	}
	else if (DTM.NAMESPACE_NODE != type)
	{
	  break;
	}
      }
    }

    return DTM.NULL;
  }

  /**
   * The optimized version of DTMDefaultBase.getTypedAttribute(int, int).
   * 
   * Given a node handle and an expanded type ID, get the index of the node's
   * attribute of that type, if any.
   *
   * @param nodeHandle int Handle of the node.
   * @param attType int expanded type ID of the required attribute.
   * @return Handle of attribute of the required type, or DTM.NULL to indicate
   * none exists.
   */
  protected final int getTypedAttribute(int nodeHandle, int attType) 
  {
          
    int nodeID = makeNodeIdentity(nodeHandle);
    
    if (nodeID == DTM.NULL)
      return DTM.NULL;
    
    int type = _type2(nodeID);
    
    if (DTM.ELEMENT_NODE == type)
    {
      int expType;
      while (true)
      {
	nodeID++;
	expType = _exptype2(nodeID);
	
	if (expType != DTM.NULL)
	  type = m_extendedTypes[expType].getNodeType();
	else
	  return DTM.NULL;

	if (type == DTM.ATTRIBUTE_NODE)
	{
	  if (expType == attType) return makeNodeHandle(nodeID);
	}
	else if (DTM.NAMESPACE_NODE != type)
	{
	  break;
	}
      }
    }

    return DTM.NULL;
  }

  /**
   * The optimized version of SAX2DTM.getNodeNameX().
   * 
   * Given  a node handle, return the XPath node name. This should be the name
   * as described by the XPath data model, NOT the DOM- style name.
   *
   * @param nodeHandle the id of the node.
   * @return String Name of this node, which may be an empty string.
   */
  public final String getNodeNameX(int nodeHandle)
  {

    int nodeID = makeNodeIdentity(nodeHandle);
    int eType = _exptype2(nodeID);
    final ExtendedType extType = m_extendedTypes[eType];
                         
    if (extType.getNamespace().length() == 0)
    {
      return extType.getLocalName();
    }
    else
    {
      int qnameIndex = m_dataOrQName.elementAt(nodeID);

      if (qnameIndex < 0)
      {
	qnameIndex = -qnameIndex;
	qnameIndex = m_data.elementAt(qnameIndex);
      }

      return m_valuesOrPrefixes.indexToString(qnameIndex);
    }
  }

  /**
   * The optimized version of SAX2DTM.getStringValue(int).
   * 
   * %OPT% This is one of the most often used interfaces. Performance is
   * critical here. This one is different from SAX2DTM.getStringValue(int) in
   * that it returns a String instead of a XMLString.
   * 
   * Get the string- value of a node as a String object (see http: //www. w3.
   * org/TR/xpath#data- model for the definition of a node's string- value).
   *
   * @param nodeHandle The node ID.
   *
   * @return A string object that represents the string-value of the given node.
   */
  public final String getStringValueX(final int nodeHandle)
  {
    int identity = makeNodeIdentity(nodeHandle);
    if (identity == DTM.NULL)
      return EMPTY_STR;
    
    int type= _type2(identity);

    if (type == DTM.ELEMENT_NODE || type == DTM.DOCUMENT_NODE)
    {
      int firstChild = _firstch2(identity);
      if (DTM.NULL != firstChild)
      {
	int offset = -1;
	int length = 0;
	int startNode = identity;

	identity = firstChild;

	do 
	{
	  type = _exptype2(identity);

	  if (type == DTM.TEXT_NODE || type == DTM.CDATA_SECTION_NODE)
	  {
	    int dataIndex = _dataOrQName(identity);

	    if (-1 == offset)
	    {
              offset = m_data.elementAt(dataIndex);
	    }

	    length += m_data.elementAt(dataIndex + 1);
	  }

	  identity++;
	} while (_parent2(identity) >= startNode);

	if (length > 0)
	{
	  return m_chars.getString(offset, length);
	}
	else
	  return EMPTY_STR;
      }
      else
        return EMPTY_STR;
    } 
    else if (DTM.TEXT_NODE == type || DTM.CDATA_SECTION_NODE == type)
    {
      int dataIndex = _dataOrQName(identity);
      int offset = m_data.elementAt(dataIndex);
      int length = m_data.elementAt(dataIndex + 1);

      return m_chars.getString(offset, length);
    }
    else
    {
      int dataIndex = _dataOrQName(identity);

      if (dataIndex < 0)
      {
        dataIndex = -dataIndex;
        dataIndex = m_data.elementAt(dataIndex + 1);
      }
      return m_valuesOrPrefixes.indexToString(dataIndex);
    }
  }
  
}