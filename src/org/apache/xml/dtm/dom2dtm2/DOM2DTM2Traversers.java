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
package org.apache.xml.dtm.dom2dtm2;

import org.apache.xml.dtm.*;
//import org.apache.xml.dtm.ref.ExpandedNameTable;
import org.w3c.dom.Node;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import javax.xml.transform.Source;

import org.apache.xml.utils.XMLStringFactory;

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.res.XSLMessages;


/**
 * This class implements the traversers for DOM2DTM2.
 *
 * PLEASE NOTE that the public interface for all traversers should be
 * in terms of DTM Node Handles... but they may use the internal node
 * identity indices within their logic, for efficiency's sake. Be very
 * careful to avoid confusing these when maintaining this code.
 * 
 * IN PARTICULAR: The ...dtm.ref version of these assumed that Node
 * Identifiers were sequentially assigned in document order.
 * Good optimization in that specific version... but this is
 * *NOT* true in the general case, and explicitly not true
 * for DOM2DTM2. This code needs to be checked very carefully to
 * remove that set of assumptions... and in some cases to switch to
 * processing directly against the DOM (another microoptimization).
 * 
 * I'm actually somewhat less than delighted with having the traversers
 * implementation derived from the DTM. That's another optimization
 * trick... but it would be nice to have at least one completely generic
 * implementation, using only public (or "friend"/package) APIs, to
 * make bootstrapping new DTM implementations eaiser. Something to think
 * about in our copious spare time.
 * */
public abstract class DOM2DTM2Traversers extends DOM2DTM2Base
{

  /**
   * Construct a DTMDefaultBaseTraversers object from a DOM node.
   *
   * @param mgr The DTMManager who owns this DTM.
   * @param domSource the DOM source that this DTM will wrap.
   * @param source The object that is used to specify the construction source.
   * @param dtmIdentity The DTM identity ID for this DTM.
   * @param whiteSpaceFilter The white space filter for this DTM, which may
   *                         be null.
   * @param xstringfactory The factory to use for creating XMLStrings.
   * @param doIndexing true if the caller considers it worth it to use
   *                   indexing schemes.
   */
  public DOM2DTM2Traversers(DTMManager mgr, Source source,
                                  int dtmIdentity,
                                  DTMWSFilter whiteSpaceFilter,
                                  XMLStringFactory xstringfactory,
                                  boolean doIndexing)
  {
    super(mgr, source, dtmIdentity, whiteSpaceFilter, xstringfactory,
          doIndexing);
  }

  /**
   * This returns a stateless "traverser", that can navigate
   * over an XPath axis, though perhaps not in document order.
   *
   * @param axis One of Axes.ANCESTORORSELF, etc.
   *
   * @return A DTMAxisTraverser, or null if the given axis isn't supported.
   */
  public DTMAxisTraverser getAxisTraverser(final int axis)
  {

    DTMAxisTraverser traverser;

    if (null == m_traversers)  // Cache of stateless traversers for this DTM
    {
      m_traversers = new DTMAxisTraverser[Axis.names.length];
      traverser = null;
    }
    else
    {
      traverser = m_traversers[axis];  // Share/reuse existing traverser

      if (traverser != null)
        return traverser;
    }

    switch (axis)  // Generate new traverser
    {
    case Axis.ANCESTOR :
      traverser = new AncestorTraverser();
      break;
    case Axis.ANCESTORORSELF :
      traverser = new AncestorOrSelfTraverser();
      break;
    case Axis.ATTRIBUTE :
      traverser = new AttributeTraverser();
      break;
    case Axis.CHILD :
      traverser = new ChildTraverser();
      break;
    case Axis.DESCENDANT :
      traverser = new DescendantTraverser();
      break;
    case Axis.DESCENDANTORSELF :
      traverser = new DescendantOrSelfTraverser();
      break;
    case Axis.FOLLOWING :
      traverser = new FollowingTraverser();
      break;
    case Axis.FOLLOWINGSIBLING :
      traverser = new FollowingSiblingTraverser();
      break;
    case Axis.NAMESPACE :
      traverser = new NamespaceTraverser();
      break;
    case Axis.NAMESPACEDECLS :
      traverser = new NamespaceDeclsTraverser();
      break;
    case Axis.PARENT :
      traverser = new ParentTraverser();
      break;
    case Axis.PRECEDING :
      traverser = new PrecedingTraverser();
      break;
    case Axis.PRECEDINGSIBLING :
      traverser = new PrecedingSiblingTraverser();
      break;
    case Axis.SELF :
      traverser = new SelfTraverser();
      break;
    case Axis.ALL :
      traverser = new AllFromRootTraverser();
      break;
    case Axis.ALLFROMNODE :
      traverser = new AllFromNodeTraverser();
      break;
    case Axis.PRECEDINGANDANCESTOR :
      traverser = new PrecedingAndAncestorTraverser();
      break;
    case Axis.DESCENDANTSFROMROOT :
      traverser = new DescendantFromRootTraverser();
      break;
    case Axis.DESCENDANTSORSELFFROMROOT :
      traverser = new DescendantOrSelfFromRootTraverser();
      break;
    case Axis.ROOT :
      traverser = new RootTraverser();
      break;
    case Axis.FILTEREDLIST :
      return null; // Don't want to throw an exception for this one.
    default :
      throw new DTMException(XSLMessages.createMessage(XSLTErrorResources.ER_UNKNOWN_AXIS_TYPE, new Object[]{Integer.toString(axis)})); //"Unknown axis traversal type: "+axis);
    }

    if (null == traverser)
      throw new DTMException("Axis traverser not supported: "
                             + Axis.names[axis]);

    m_traversers[axis] = traverser;

    return traverser;
  }

  /**
   * Implements traversal of the Ancestor access, in reverse document order.
   */
  private class AncestorTraverser extends DTMAxisTraverser
  {

    /**
     * Traverse to the next node after the current node.
     *
     * @param context The context node if this iteration.
     * @param current The current node of the iteration.
     *
     * @return the next node in the iteration, or DTM.NULL.
     */
    public int next(int context, int current)
    {
			return getParent(current);
    }

    /**
     * Traverse to the next node after the current node that is matched
     * by the expanded type ID.
     *
     * @param context The context node of this iteration.
     * @param current The current node of the iteration.
     * @param expandedTypeID The expanded type ID that must match.
     *
     * @return the next node in the iteration, or DTM.NULL.
     */
    public int next(int context, int current, int expandedTypeID)
    {
      // %REVIEW% Switch to DOM view for efficiency?
      // Requires awareness of DOM/XPath mapping...
      // and dealing efficiently with the ExpandedType stuff.
      while (DTM.NULL != (current = getParent(current)))
      {
        if (getExpandedTypeID(current) == expandedTypeID)
          return makeNodeHandle(current);
      }

      return DTM.NULL;
    }
  }

  /**
   * Implements traversal of the Ancestor access, in reverse document order.
   */
  private class AncestorOrSelfTraverser extends AncestorTraverser
  {

    /**
     * By the nature of the stateless traversal, the context node can not be
     * returned or the iteration will go into an infinate loop.  To see if
     * the self node should be processed, use this function.
     *
     * @param context The context node of this traversal.
     *
     * @return the first node in the traversal.
     */
    public int first(int context)
    {
      return context;
    }

    /**
     * By the nature of the stateless traversal, the context node can not be
     * returned or the iteration will go into an infinate loop.  To see if
     * the self node should be processed, use this function.  If the context
     * node does not match the expanded type ID, this function will return
     * false.
     *
     * @param context The context node of this traversal.
     * @param expandedTypeID The expanded type ID that must match.
     *
     * @return the first node in the traversal.
     */
    public int first(int context, int expandedTypeID)
    {
			return (getExpandedTypeID(context) == expandedTypeID)
             ? context : next(context, context, expandedTypeID);
    }
  }

  /**
   * Implements traversal of the Attribute access
   */
  private class AttributeTraverser extends DTMAxisTraverser
  {

    /**
     * Traverse to the next node after the current node.
     *
     * @param context The context node of this iteration.
     * @param current The current node of the iteration.
     *
     * @return the next node in the iteration, or DTM.NULL.
     */
    public int next(int context, int current)
    {
      return (context == current)
             ? getFirstAttribute(context) : getNextAttribute(current);
    }

    /**
     * Traverse to the next node after the current node that is matched
     * by the expanded type ID.
     *
     * @param context The context node of this iteration.
     * @param current The current node of the iteration.
     * @param expandedTypeID The expanded type ID that must match.
     *
     * @return the next node in the iteration, or DTM.NULL.
     */
    public int next(int context, int current, int expandedTypeID)
    {

      current = (context == current)
                ? getFirstAttribute(context) : getNextAttribute(current);

      do
      {
        if (getExpandedTypeID(current) == expandedTypeID)
          return current;
      }
      while (DTM.NULL != (current = getNextAttribute(current)));

      return DTM.NULL;
    }
  }

  /**
   * Implements traversal of the Ancestor access, in reverse document order.
   */
  private class ChildTraverser extends DTMAxisTraverser
  {
    /**
     * By the nature of the stateless traversal, the context node can not be
     * returned or the iteration will go into an infinate loop.  So to traverse 
     * an axis, the first function must be used to get the first node.
     *
     * <p>This method needs to be overloaded only by those axis that process
     * the self node. <\p>
     *
     * @param context The context node of this traversal. This is the point
     * that the traversal starts from.
     * @return the first node in the traversal.
     */
    public int first(int context)
    {
      return getFirstChild(context);
    }
  
    /**
     * By the nature of the stateless traversal, the context node can not be
     * returned or the iteration will go into an infinate loop.  So to traverse 
     * an axis, the first function must be used to get the first node.
     *
     * <p>This method needs to be overloaded only by those axis that process
     * the self node. <\p>
     *
     * @param context The context node of this traversal. This is the point
     * of origin for the traversal -- its "root node" or starting point.
     * @param expandedTypeID The expanded type ID that must match.
     *
     * @return the first node in the traversal.
     */
    public int first(int context, int expandedTypeID)
    {
      if(false)
      {
      	error("INDEXING NOT YET SUPPORTED IN DOM2DTM2");
      	return DTM.NULL;
      	/*
        int identity = makeNodeIdentity(context);
        int firstMatch = getNextIndexed(identity, _firstch(identity),
                                 expandedTypeID);
        return makeNodeHandle(firstMatch);
        */
      }
      else
      {
				// %REVIEW% Dead code. Eliminate?
        for (int current = _firstch(makeNodeIdentity(context)); 
             DTM.NULL != current; 
             current = _nextsib(current)) 
        {
          if (_exptype(current) == expandedTypeID)
              return makeNodeHandle(current);
        }
        return DTM.NULL;
      }
    }

    /**
     * Traverse to the next node after the current node.
     *
     * @param context The context node of this iteration.
     * @param current The current node of the iteration.
     *
     * @return the next node in the iteration, or DTM.NULL.
     */
    public int next(int context, int current)
    {
      return getNextSibling(current);
    }

    /**
     * Traverse to the next node after the current node that is matched
     * by the expanded type ID.
     *
     * @param context The context node of this iteration.
     * @param current The current node of the iteration.
     * @param expandedTypeID The expanded type ID that must match.
     *
     * @return the next node in the iteration, or DTM.NULL.
     */
    public int next(int context, int current, int expandedTypeID)
    {
		// %OPT% Process in DOM space?
      for (;
           DTM.NULL != current; 
           current = getNextSibling(current)) 
      {
        if (getExpandedTypeID(current) == expandedTypeID)
            return makeNodeHandle(current);
      }
      
      return DTM.NULL;
    }
  }

  /**
   * Super class for derived classes that want a convenient way to access 
   * the indexing mechanism.
   */
  private abstract class IndexedDTMAxisTraverser extends DTMAxisTraverser
  {

    /**
     * Tell if the indexing is on and the given expanded type ID matches 
     * what is in the indexes.  Derived classes should call this before 
     * calling {@link #getNextIndexed(int, int, int) getNextIndexed} method.
     *
     * @param expandedTypeID The expanded type ID being requested.
     *
     * @return true if it is OK to call the 
     *         {@link #getNextIndexed(int, int, int) getNextIndexed} method.
     */
    protected final boolean isIndexed(int expandedTypeID)
    {
      return (m_indexing
              && m_expandedNameTable.ELEMENT
                 == m_expandedNameTable.getType(expandedTypeID)); 
    }

    /**
     * Get the next indexed node that matches the expanded type ID.  Before 
     * calling this function, one should first call 
     * {@link #isIndexed(int) isIndexed} to make sure that the index can 
     * contain nodes that match the given expanded type ID.
     *
     * @param axisRoot The root identity of the axis.
     * @param nextPotential The node found must match or occur after this node.
     * @param expandedTypeID The expanded type ID for the request.
     *
     * @return The node ID or NULL if not found.
     */
    protected int getNextIndexed(int axisRoot, int nextPotential,
                                 int expandedTypeID)
    {
      	error("INDEXING NOT YET SUPPORTED IN DOM2DTM2");
      	return DTM.NULL;
    	/*
      int nsIndex = m_expandedNameTable.getNamespaceID(expandedTypeID);
      int lnIndex = m_expandedNameTable.getLocalNameID(expandedTypeID);

      while(true)
      {
        int next = findElementFromIndex(nsIndex, lnIndex, nextPotential);

        if (NOTPROCESSED != next)
        {
          if (isAfterAxis(axisRoot, next))
            return NULL;

          // System.out.println("Found node via index: "+first);
          return next;
        }
        else if(axisHasBeenProcessed(axisRoot))
          break;

        nextNode();
      }

      return DTM.NULL;
      */
    }
  }

  /**
   * Implements traversal of the Descendent access, in document order.
   */
  private class DescendantTraverser extends IndexedDTMAxisTraverser
  {
    /**
     * Get the first potential identity that can be returned.  This should 
     * be overridded by classes that need to return the self node.
     *
     * @param identity The node identity of the root context of the traversal.
     *
     * @return The first potential node that can be in the traversal.
     */
    protected int getFirstPotential(int identity)
    {
      return _firstch(identity);
    }
    
    /**
     * Get the subtree root identity from the handle that was passed in by 
     * the caller.  Derived classes may override this to change the root 
     * context of the traversal.
     * 
     * @param handle handle to the root context.
     * @return identity of the root of the subtree.
     */
    protected int getSubtreeRoot(int handle)
    {
      return makeNodeIdentity(handle);
    }

    /**
     * By the nature of the stateless traversal, the context node can not be
     * returned or the iteration will go into an infinate loop.  So to traverse
     * an axis, the first function must be used to get the first node.
     *
     * <p>This method needs to be overloaded only by those axis that process
     * the self node. <\p>
     *
     * @param context The context node of this traversal. This is the point
     * of origin for the traversal -- its "root node" or starting point.
     * @param expandedTypeID The expanded type ID that must match.
     *
     * @return the first node in the traversal.
     */
    public int first(int context, int expandedTypeID)
    {
      if (isIndexed(expandedTypeID))
      {
        int identity = getSubtreeRoot(context);
        int firstPotential = getFirstPotential(identity);

        return makeNodeHandle(getNextIndexed(identity, firstPotential, expandedTypeID));
      }

      return next(context, context, expandedTypeID);
    }

    /**
     * Traverse to the next node after the current node.
     *
     * @param context The context node of this iteration.
     * @param current The current node of the iteration.
     *
     * @return the next node in the iteration, or DTM.NULL.
     */
    public int next(int context, int current)
    {
      int subtreeRootIdent = getSubtreeRoot(context);
      Node subtreeRootNode=m_resolver.findNode(subtreeRootIdent);
      int currentNodeIdent=makeNodeIdentity(current);
      Node n=m_resolver.findNode(currentNodeIdent);
      n=walkNextSkipEntRef(n,subtreeRootNode,true);
      return makeNodeHandle(m_resolver.findID(n));
    }

    /**
     * Traverse to the next node after the current node that is matched
     * by the expanded type ID.
     *
     * @param context The context node of this iteration.
     * @param current The current node of the iteration.
     * @param expandedTypeID The expanded type ID that must match.
     *
     * @return the next node in the iteration, or DTM.NULL.
     */
    public int next(int context, int current, int expandedTypeID)
    {

      int subtreeRootIdent = getSubtreeRoot(context);

      if (isIndexed(expandedTypeID))
      {
      	error("Indexed doesn't work yet in DOM2DTM2");
	    current = makeNodeIdentity(current) + 1; /* gonk! */
        return makeNodeHandle(getNextIndexed(subtreeRootIdent, current, expandedTypeID));
      }

      Node subtreeRootNode=m_resolver.findNode(subtreeRootIdent);
      int currentNodeIdent=makeNodeIdentity(current);
      Node n=m_resolver.findNode(currentNodeIdent);

	  // %OPT% Note presumption that this lookup is faster than
	  // extracting and testing the fields...
	  int newid;
      do
      {
	      n=walkNextSkipEntRef(n,subtreeRootNode,true);
	      newid=m_resolver.findID(n);
      } while(n!=null && expandedTypeID != _exptype(newid));      			
      			
      return makeNodeHandle(newid);
    }
  }

  /**
   * Implements traversal of the Ancestor access, in reverse document order.
   */
  private class DescendantOrSelfTraverser extends DescendantTraverser
  {

    /**
     * Get the first potential identity that can be returned, which is the 
     * axis context, in this case.
     *
     * @param identity The node identity of the root context of the traversal.
     *
     * @return The axis context.
     */
    protected int getFirstPotential(int identity)
    {
      return identity;
    }

    /**
     * By the nature of the stateless traversal, the context node can not be
     * returned or the iteration will go into an infinate loop.  To see if
     * the self node should be processed, use this function.
     *
     * @param context The context node of this traversal.
     *
     * @return the first node in the traversal.
     */
    public int first(int context)
    {
      return context;
    }
  }

  /**
   * Implements traversal of the entire subtree, including the root node
   * AND INCLUDING NAMESPACES/ATTRS. 
   */
  private class AllFromNodeTraverser extends DescendantOrSelfTraverser
  {
    /**
     * Traverse to the next node after the current node.
     *
     * @param context Handle of the context node of this iteration.
     * @param current Handle of the current node of the iteration.
     *
     * @return Handle of the next node in the iteration, or DTM.NULL.
     */
    public int next(int context, int current)
    {
      int subtreeRootIdent = makeNodeIdentity(context);

		// %OPT% Rewrite in terms of IDs? Nodes?

	  int nexthandle=DTM.NULL;
	  
	  // Special case for namespace nodes
	  // %REVIEW% Should we add hook there for Element to first NS?
	  int type=getNodeType(current);
	  if(type==DTM.ELEMENT_NODE)            
      {
			nexthandle=getFirstNamespaceNode(
				current,false);
      }
	  else if(type==DTM.NAMESPACE_NODE)            
      {
			nexthandle=getNextNamespaceNode(
				getParent(current),current,false);
      }

	  // This already handles the case of moving from Element
	  // to first attr      
	  if(nexthandle==DTM.NULL)
		nexthandle=getNextAttribute(current);

	  if(nexthandle==DTM.NULL)
		nexthandle=getFirstChild(current);

	  while(nexthandle==DTM.NULL)
	  { 		  
		nexthandle=getNextSibling(current);
		if(nexthandle==DTM.NULL)
		{
			current=getParent(current);
			if(current==DTM.NULL || current==context)
				break;
		}
	  }
	  return nexthandle;
    }

    /**
     * Traverse to the next node after the current node that matches
     * the given type.
     * 
     * %REVIEW% This wasn't implemented in the reference implementation.
     * Why the heck not?
     *
     * @param context Handle of the context node of this iteration.
     * @param current Handle of the current node of the iteration.
     *
     * @return Handle of the next node in the iteration, or DTM.NULL.
     */
    public int next(int context, int current, int expandedTypeID)
    {
      int subtreeRootIdent = makeNodeIdentity(context);

		// %OPT% Rewrite in terms of IDs? Nodes?

	  int nexthandle=DTM.NULL;

	  do
	  {	  
		  // Special case for namespace nodes
		  // %REVIEW% Should we add hook there for Element to first NS?
		  int type=getNodeType(current);
		  if(type==DTM.ELEMENT_NODE)            
	      {
				nexthandle=getFirstNamespaceNode(
					current,false);
	      }
		  else if(type==DTM.NAMESPACE_NODE)            
	      {
				nexthandle=getNextNamespaceNode(
					getParent(current),current,false);
	      }
	
		  // This already handles the case of moving from Element
		  // to first attr      
		  if(nexthandle==DTM.NULL)
			nexthandle=getNextAttribute(current);
	
		  if(nexthandle==DTM.NULL)
			nexthandle=getFirstChild(current);
	
		  while(nexthandle==DTM.NULL)
		  { 		  
			nexthandle=getNextSibling(current);
			if(nexthandle==DTM.NULL)
			{
				current=getParent(current);
				if(current==DTM.NULL || current==context)
					break;
			}
		  }
		  
		  current=nexthandle;
	  } while(current!=DTM.NULL && 
	  	!_isExpandedTypeID(makeNodeIdentity(current),expandedTypeID));
	  
	  return current;
    }
  }


  /**
   * Implements traversal of the following access, in document order.
   * Despite the inheritance, this is apparently NOT limited to 
   * descendents.
   */
  private class FollowingTraverser extends DescendantTraverser
  {
    /**
     * Get the first of the following.
     *
     * @param context The context node of this traversal. This is the point
     * that the traversal starts from.
     * @return the first node in the traversal.
     */
    public int first(int context)
    {
			// Compute in ID space
			context=makeNodeIdentity(context);

      int first;
      int type = _type(context);

      if ((DTM.ATTRIBUTE_NODE == type) || (DTM.NAMESPACE_NODE == type))
      {
        context = _parent(context);
        first = _firstch(context);

        if (DTM.NULL != first)
          return makeNodeHandle(first);
      }

      do
      {
        first = _nextsib(context);

        if (DTM.NULL == first)
          context = _parent(context);
      }
      while (DTM.NULL == first && DTM.NULL != context);

      return makeNodeHandle(first);
    }

    /**
     * Get the first of the following.
     *
     * @param context The context node of this traversal. This is the point
     * of origin for the traversal -- its "root node" or starting point.
     * @param expandedTypeID The expanded type ID that must match.
     *
     * @return the first node in the traversal.
     */
    public int first(int context, int expandedTypeID)
    {
			// %REVIEW% This looks like it might want shift into identity space
			// to avoid repeated conversion in the individual functions
      int first;
      int type = getNodeType(context);

      if ((DTM.ATTRIBUTE_NODE == type) || (DTM.NAMESPACE_NODE == type))
      {
        context = getParent(context);
        first = getFirstChild(context);

        if (DTM.NULL != first)
        {
          if (getExpandedTypeID(first) == expandedTypeID)
            return first;
          else
            return next(context, first, expandedTypeID);
        }
      }

      do
      {
        first = getNextSibling(context);

        if (DTM.NULL == first)
          context = getParent(context);
        else
        {
          if (getExpandedTypeID(first) == expandedTypeID)
            return first;
          else
            return next(context, first, expandedTypeID);
        }
      }
      while (DTM.NULL == first && DTM.NULL != context);

      return first;
    }

    /**
     * Traverse to the next node after the current node.
     *
     * @param context The context node of this iteration.
     * @param current The current node of the iteration.
     *
     * @return the next node in the iteration, or DTM.NULL.
     */
    public int next(int context, int current)
    {
    	// We know it isn't an attr or a namespace,
    	// since first() dealt with that.
    	Node n=getNode(current);
    	n=walkNextSkipEntRef(n,null,true);
    	return makeNodeHandle(m_resolver.findID(n));
    }

    /**
     * Traverse to the next node after the current node that is matched
     * by the expanded type ID.
     *
     * @param context The context node of this iteration.
     * @param current The current node of the iteration.
     * @param expandedTypeID The expanded type ID that must match.
     *
     * @return the next node in the iteration, or DTM.NULL.
     */
    public int next(int context, int current, int expandedTypeID)
    {
    	// We know it isn't an attr or a namespace,
    	// since first() dealt with that.
    	Node n=getNode(current);
    	do
    	{
	    	n=walkNextSkipEntRef(n,null,true);
    	}
    	while(n!=null && expandedTypeID!=_exptype(m_resolver.findID(n)));
    	return makeNodeHandle(m_resolver.findID(n));
    }
  }

  /**
   * Implements traversal of the following sibling axis
   */
  private class FollowingSiblingTraverser extends DTMAxisTraverser
  {
    /**
     * Traverse to the next node after the current node.
     *
     * @param context The context node of this iteration.
     * @param current The current node of the iteration.
     *
     * @return the next node in the iteration, or DTM.NULL.
     */
    public int next(int context, int current)
    {
      return getNextSibling(current);
    }

    /**
     * Traverse to the next node after the current node that is matched
     * by the expanded type ID.
     *
     * @param context The context node of this iteration.
     * @param current The current node of the iteration.
     * @param expandedTypeID The expanded type ID that must match.
     *
     * @return the next node in the iteration, or DTM.NULL.
     */
    public int next(int context, int current, int expandedTypeID)
    {

      while (DTM.NULL != (current = getNextSibling(current)))
      {
        if (getExpandedTypeID(current) == expandedTypeID)
          return current;
      }

      return DTM.NULL;
    }
  }

  /**
   * Implements traversal of the Ancestor access, in reverse document order.
   */
  private class NamespaceDeclsTraverser extends DTMAxisTraverser
  {

    /**
     * Traverse to the next node after the current node.
     *
     * @param context The context node of this iteration.
     * @param current The current node of the iteration.
     *
     * @return the next node in the iteration, or DTM.NULL.
     */
    public int next(int context, int current)
    {

      return (context == current)
             ? getFirstNamespaceNode(context, false)
             : getNextNamespaceNode(context, current, false);
    }

    /**
     * Traverse to the next node after the current node that is matched
     * by the expanded type ID.
     *
     * @param context The context node of this iteration.
     * @param current The current node of the iteration.
     * @param expandedTypeID The expanded type ID that must match.
     *
     * @return the next node in the iteration, or DTM.NULL.
     */
    public int next(int context, int current, int expandedTypeID)
    {

      current = (context == current)
                ? getFirstNamespaceNode(context, false)
                : getNextNamespaceNode(context, current, false);

      do
      {
        if (getExpandedTypeID(current) == expandedTypeID)
          return current;
      }
      while (DTM.NULL
             != (current = getNextNamespaceNode(context, current, false)));

      return DTM.NULL;
    }
  }

  /**
   * Implements traversal of the Ancestor access, in reverse document order.
   */
  private class NamespaceTraverser extends DTMAxisTraverser
  {
    /**
     * Traverse to the next node after the current node.
     *
     * @param context The context node of this iteration.
     * @param current The current node of the iteration.
     *
     * @return the next node in the iteration, or DTM.NULL.
     */
    public int next(int context, int current)
    {

      return (context == current)
             ? getFirstNamespaceNode(context, true)
             : getNextNamespaceNode(context, current, true);
    }

    /**
     * Traverse to the next node after the current node that is matched
     * by the expanded type ID.
     *
     * @param context The context node of this iteration.
     * @param current The current node of the iteration.
     * @param expandedTypeID The expanded type ID that must match.
     *
     * @return the next node in the iteration, or DTM.NULL.
     */
    public int next(int context, int current, int expandedTypeID)
    {

      current = (context == current)
                ? getFirstNamespaceNode(context, true)
                : getNextNamespaceNode(context, current, true);

      do
      {
        if (getExpandedTypeID(current) == expandedTypeID)
          return current;
      }
      while (DTM.NULL
             != (current = getNextNamespaceNode(context, current, true)));

      return DTM.NULL;
    }
  }

  /**
   * Implements traversal of the Ancestor access, in reverse document order.
   */
  private class ParentTraverser extends DTMAxisTraverser
  {
    /**
     * By the nature of the stateless traversal, the context node can not be
     * returned or the iteration will go into an infinate loop.  So to traverse 
     * an axis, the first function must be used to get the first node.
     *
     * <p>This method needs to be overloaded only by those axis that process
     * the self node. <\p>
     *
     * @param context The context node of this traversal. This is the point
     * that the traversal starts from.
     * @return the first node in the traversal.
     */
    public int first(int context)
    {
      return getParent(context);
    }
  
    /** This is actually going to the nearest ancestor of the
     * specified type, not to the parent-if-it-has-this-type.
     * I suspect that's incorrect!
     * %REVIEW% 
     * 
     * By the nature of the stateless traversal, the context node can not be
     * returned or the iteration will go into an infinate loop.  So to traverse 
     * an axis, the first function must be used to get the first node.
     *
     * <p>This method needs to be overloaded only by those axis that process
     * the self node. <\p>
     *
     * @param context The context node of this traversal. This is the point
     * of origin for the traversal -- its "root node" or starting point.
     * @param expandedTypeID The expanded type ID that must match.
     *
     * @return the first node in the traversal.
     */
    public int first(int current, int expandedTypeID)
    {
      current=makeNodeIdentity(current);
      while (DTM.NULL != (current = getParent(current)))
      {
        if (_isExpandedTypeID(current,expandedTypeID))
          return makeNodeHandle(current);
      }

      return DTM.NULL;
    }


    /**
     * Traverse to the next node after the current node.
     *
     * @param context The context node of this iteration.
     * @param current The current node of the iteration.
     *
     * @return the next node in the iteration, or DTM.NULL.
     */
    public int next(int context, int current)
    {

      return DTM.NULL;
    }
    


    /**
     * Traverse to the next node after the current node that is matched
     * by the expanded type ID.
     *
     * @param context The context node of this iteration.
     * @param current The current node of the iteration.
     * @param expandedTypeID The expanded type ID that must match.
     *
     * @return the next node in the iteration, or DTM.NULL.
     */
    public int next(int context, int current, int expandedTypeID)
    {

      return DTM.NULL;
    }
  }

  /**
   * Implements traversal of the Preceeding access, in reverse document order.
   * Note that this axis excludes the direct ancestors of
   * this node. Apparetly this is for symmetry with FollowingTraverser,
   * which also doesn't stop at the parents. 
   * @see PreceedingAndAncestorTraverser
   */
  private class PrecedingTraverser extends DTMAxisTraverser
  {
    /**
     * Tell if the current identity is an ancestor of the context identity.
     * This is an expensive operation, made worse by the stateless traversal.
     * But the preceding axis is used fairly infrequently.
     *
     * @param contextIdent The context node of the axis traversal.
     * @param currentIdent The node in question.
     * @return true if the currentIdent node is an ancestor of contextIdent.
     */
    protected boolean isAncestor(Node contextNode, Node currentNode)
    {
      for (contextNode = contextNode.getParentNode();
	       null != contextNode;
           contextNode = contextNode.getParentNode())
      {
        if (m_resolver.isSameNode(contextNode,currentNode))
          return true;
      }

      return false;
    }


    /**
     * Traverse to the next node after the current node.
     *
     * @param context The context node of this iteration.
     * @param current The current node of the iteration.
     *
     * @return the next node in the iteration, or DTM.NULL.
     */
    public int next(int context, int current)
    {
      int subtreeRootIdent = makeNodeIdentity(context);
	  Node subtreeRootNode=m_resolver.findNode(subtreeRootIdent);

      int doc=_documentRoot(subtreeRootIdent);
      
	  // Just main tree, *NOT* namespace or attribute notes! 
      Node within=m_resolver.findNode(doc);     
      Node n=m_resolver.findNode(makeNodeIdentity(current));
      do
      {
	      n=walkPreviousSkipEntRef(n,within);
      } while(n!=null && isAncestor(subtreeRootNode,n));
      return makeNodeHandle(m_resolver.findID(n));
    }

    /**
     * Traverse to the next node after the current node that is matched
     * by the expanded type ID.
     *
     * @param context The context node of this iteration.
     * @param current The current node of the iteration.
     * @param expandedTypeID The expanded type ID that must match.
     *
     * @return the next node in the iteration, or DTM.NULL.
     */
    public int next(int context, int current, int expandedTypeID)
    {
      int subtreeRootIdent = makeNodeIdentity(context);
	  Node subtreeRootNode=m_resolver.findNode(subtreeRootIdent);

      int doc=_documentRoot(subtreeRootIdent);
      Node within=m_resolver.findNode(doc);     

      Node n=m_resolver.findNode(makeNodeIdentity(current));
      do
      {
	      n=walkPreviousSkipEntRef(n,within);
      }
      while(n!=null && isAncestor(subtreeRootNode,n) && !walkIsExpandedTypeID(n,expandedTypeID));
      return makeNodeHandle(m_resolver.findID(n));
    }
  }

  /**
   * Implements traversal of the Ancestor and the Preceding axis,
   * in reverse document order.
   */
  private class PrecedingAndAncestorTraverser extends DTMAxisTraverser
  {
    /**
     * Traverse to the next node after the current node.
     *
     * @param context The context node of this iteration.
     * @param current The current node of the iteration.
     *
     * @return the next node in the iteration, or DTM.NULL.
     */
    public int next(int context, int current)
    {
      int subtreeRootIdent = makeNodeIdentity(context);
      int doc=_documentRoot(subtreeRootIdent);
      
	  // Just main tree, *NOT* namespace or attribute notes! 
      Node within=m_resolver.findNode(doc);     
      Node n=m_resolver.findNode(makeNodeIdentity(current));
      n=walkPreviousSkipEntRef(n,within);
      return makeNodeHandle(m_resolver.findID(n));
    }

    /**
     * Traverse to the next node after the current node that is matched
     * by the expanded type ID.
     *
     * @param context The context node of this iteration.
     * @param current The current node of the iteration.
     * @param expandedTypeID The expanded type ID that must match.
     *
     * @return the next node in the iteration, or DTM.NULL.
     */
    public int next(int context, int current, int expandedTypeID)
    {
      int subtreeRootIdent = makeNodeIdentity(context);
      int doc=_documentRoot(subtreeRootIdent);
      
	  // Just main tree, *NOT* namespace or attribute notes! 
      Node within=m_resolver.findNode(doc);     
      Node n=m_resolver.findNode(makeNodeIdentity(current));
      do
      {
	      n=walkPreviousSkipEntRef(n,within);
      }
      while(n!=null && walkIsExpandedTypeID(n,expandedTypeID));
      return makeNodeHandle(m_resolver.findID(n));
    }
  }

  /**
   * Implements traversal of the Ancestor access, in reverse document order.
   */
  private class PrecedingSiblingTraverser extends DTMAxisTraverser
  {

    /**
     * Traverse to the next node after the current node.
     *
     * @param context The context node of this iteration.
     * @param current The current node of the iteration.
     *
     * @return the next node in the iteration, or DTM.NULL.
     */
    public int next(int context, int current)
    {
      return getPreviousSibling(current);
    }

    /**
     * Traverse to the next node after the current node that is matched
     * by the expanded type ID.
     *
     * @param context The context node of this iteration.
     * @param current The current node of the iteration.
     * @param expandedTypeID The expanded type ID that must match.
     *
     * @return the next node in the iteration, or DTM.NULL.
     */
    public int next(int context, int current, int expandedTypeID)
    {

      while (DTM.NULL != (current = getPreviousSibling(current)))
      {
      	// Slightly more efficient to ask the node rather than
      	// computing the int and testing that. I think.
        if (_isExpandedTypeID(makeNodeIdentity(current),expandedTypeID))
          return current;
      }

      return DTM.NULL;
    }
  }

  /**
   * Implements traversal of the Self axis.
   */
  private class SelfTraverser extends DTMAxisTraverser
  {

    /**
     * By the nature of the stateless traversal, the context node can not be
     * returned or the iteration will go into an infinate loop.  To see if
     * the self node should be processed, use this function.
     *
     * @param context The context node of this traversal.
     *
     * @return the first node in the traversal.
     */
    public int first(int context)
    {
      return context;
    }

    /**
     * By the nature of the stateless traversal, the context node can not be
     * returned or the iteration will go into an infinate loop.  To see if
     * the self node should be processed, use this function.  If the context
     * node does not match the expanded type ID, this function will return
     * false.
     *
     * @param context The context node of this traversal.
     * @param expandedTypeID The expanded type ID that must match.
     *
     * @return the first node in the traversal.
     */
    public int first(int context, int expandedTypeID)
    {
      return (getExpandedTypeID(context) == expandedTypeID) ? context : DTM.NULL;
    }

    /**
     * Traverse to the next node after the current node.
     *
     * @param context The context node of this iteration.
     * @param current The current node of the iteration.
     *
     * @return Always return DTM.NULL for this axis.
     */
    public int next(int context, int current)
    {
      return DTM.NULL;
    }

    /**
     * Traverse to the next node after the current node that is matched
     * by the expanded type ID.
     *
     * @param context The context node of this iteration.
     * @param current The current node of the iteration.
     * @param expandedTypeID The expanded type ID that must match.
     *
     * @return the next node in the iteration, or DTM.NULL.
     */
    public int next(int context, int current, int expandedTypeID)
    {
      return DTM.NULL;
    }
  }

  /**
   * Implements traversal of the Ancestor access, in reverse document order.
   */
  private class AllFromRootTraverser extends AllFromNodeTraverser
  {

    /**
     * Return the root.
     *
     * @param context The context node of this traversal.
     *
     * @return the first node in the traversal.
     */
    public int first(int context)
    {
      return getDocumentRoot(context);
    }

    /**
     * Return the root if it matches the expanded type ID.
     *
     * @param context The context node of this traversal.
     * @param expandedTypeID The expanded type ID that must match.
     *
     * @return the first node in the traversal.
     */
    public int first(int context, int expandedTypeID)
    {
      return (getExpandedTypeID(getDocumentRoot(context)) == expandedTypeID)
             ? context : next(context, context, expandedTypeID);
    }

    /**
     * Traverse to the next node after the current node.
     *
     * @param context The context node of this iteration.
     * @param current The current node of the iteration.
     *
     * @return the next node in the iteration, or DTM.NULL.
     */
    public int next(int context, int current)
    {
    	// I believe the AllFromRootTraverser should do the Right Thing
    	// with this. Easier than recopying that code and
    	// taking out the context test!
    	return super.next(DTM.NULL,current);
    }

    /**
     * Traverse to the next node after the current node that is matched
     * by the expanded type ID.
     *
     * @param context The context node of this iteration.
     * @param current The current node of the iteration.
     * @param expandedTypeID The expanded type ID that must match.
     *
     * @return the next node in the iteration, or DTM.NULL.
     */
    public int next(int context, int current, int expandedTypeID)
    {
    	return super.next(DTM.NULL,current,expandedTypeID);
    }
  }

  /**
   * Implements traversal of the Self axis.
   */
  private class RootTraverser extends AllFromRootTraverser
  {

    /**
     * Traverse to the next node after the current node.
     *
     * @param context The context node of this iteration.
     * @param current The current node of the iteration.
     *
     * @return Always return DTM.NULL for this axis.
     */
    public int next(int context, int current)
    {
      return DTM.NULL;
    }

    /**
     * Traverse to the next node after the current node that is matched
     * by the expanded type ID.
     *
     * @param context The context node of this iteration.
     * @param current The current node of the iteration.
     * @param expandedTypeID The expanded type ID that must match.
     *
     * @return the next node in the iteration, or DTM.NULL.
     */
    public int next(int context, int current, int expandedTypeID)
    {
      return DTM.NULL;
    }
  }

  /**
   * A non-xpath axis, returns all nodes that aren't namespaces or attributes,
   * from and including the root.
   */
  private class DescendantOrSelfFromRootTraverser extends DescendantTraverser
  {

    /**
     * Get the first potential identity that can be returned, which is the axis 
     * root context in this case.
     *
     * @param identity The node identity of the root context of the traversal.
     *
     * @return The identity argument.
     */
    protected int getFirstPotential(int identity)
    {
      return identity;
    }

    /**
     * Get the first potential identity that can be returned.
     * @param handle handle to the root context.
     * @return identity of the root of the subtree.
     */
    protected int getSubtreeRoot(int handle)
    {
      return _documentRoot(makeNodeIdentity(handle));
    }

    /**
     * Return the root.
     *
     * @param context The context node of this traversal.
     *
     * @return the first node in the traversal.
     */
    public int first(int context)
    {
      return getDocumentRoot(context);
    }
    
    /**
     * By the nature of the stateless traversal, the context node can not be
     * returned or the iteration will go into an infinate loop.  So to traverse
     * an axis, the first function must be used to get the first node.
     *
     * <p>This method needs to be overloaded only by those axis that process
     * the self node. <\p>
     *
     * @param context The context node of this traversal. This is the point
     * of origin for the traversal -- its "root node" or starting point.
     * @param expandedTypeID The expanded type ID that must match.
     *
     * @return the first node in the traversal.
     */
    public int first(int context, int expandedTypeID)
    {
      if (isIndexed(expandedTypeID))
      {
        int identity = _documentRoot(makeNodeIdentity(context));
        int firstPotential = getFirstPotential(identity);

        return makeNodeHandle(getNextIndexed(identity, firstPotential, expandedTypeID));
      }

      int root = first(context); 
      return next(root, root, expandedTypeID);
    }
  }
  
  /**
   * A non-xpath axis, returns all nodes that aren't 
   * namespaces or attributes, from but not including the root.
   */
  private class DescendantFromRootTraverser extends DescendantTraverser
  {

    /**
     * Get the first potential identity that can be returned, which is the axis 
     * root context in this case.
     *
     * @param identity The node identity of the root context of the traversal.
     *
     * @return The identity argument.
     */
    protected int getFirstPotential(int identity)
    {
      return _firstch(_documentRoot(identity));
    }

    /**
     * Get the first potential identity that can be returned.
     * @param handle handle to the root context.
     * @return identity of the root of the subtree.
     */
    protected int getSubtreeRoot(int handle)
    {
      return _documentRoot(makeNodeIdentity(handle));
    }

    /**
     * Return the root.
     *
     * @param context The context node of this traversal.
     *
     * @return the first node in the traversal.
     */
    public int first(int context)
    {
      return makeNodeHandle(_firstch(_documentRoot(makeNodeIdentity(context))));
    }
    
    /**
     * By the nature of the stateless traversal, the context node can not be
     * returned or the iteration will go into an infinate loop.  So to traverse
     * an axis, the first function must be used to get the first node.
     *
     * <p>This method needs to be overloaded only by those axis that process
     * the self node. <\p>
     *
     * @param context The context node of this traversal. This is the point
     * of origin for the traversal -- its "root node" or starting point.
     * @param expandedTypeID The expanded type ID that must match.
     *
     * @return the first node in the traversal.
     */
    public int first(int context, int expandedTypeID)
    {
      if (isIndexed(expandedTypeID))
      {
        int identity = _documentRoot(makeNodeIdentity(context)); 
        int firstPotential = getFirstPotential(identity);

        return makeNodeHandle(getNextIndexed(identity, firstPotential, expandedTypeID));
      }

      int root = getDocumentRoot(context); 
      return next(root, root, expandedTypeID);
    }
    
  }

}
