/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id$
 */
package org.apache.xpath.axes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.compiler.OpCodes;
import org.apache.xpath.compiler.OpMap;

/**
 * This class provides an iterator view of evaluation 
 * result of XPath 'intersect' operator.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class NodeSequenceIntersectIterator extends LocPathIterator implements Cloneable, 
                                                               DTMIterator, java.io.Serializable, PathComponent 
{
  
  private static final long serialVersionUID = -764531646697520864L;
  
  private PredicatedNodeTest[] m_nodeTests = null;
  
  private Iterator<Integer> m_NodeRefIter = null;

  /**
   * Constructor to create an instance which you can add 
   * location paths to.
   */
  public NodeSequenceIntersectIterator()
  {
	  super();
	  m_iterators = null;
	  m_exprs = null;
  }

  /**
   * Initialize the context values for this expression 
   * after it is cloned.
   *
   * @param context The XPath runtime context for this 
   * transformation.
   */
  public void setRoot(int context, Object environment)
  {
    super.setRoot(context, environment);

    try
    {
      if (m_exprs != null)
      {
        int n = m_exprs.length;
        DTMIterator newIters[] = new DTMIterator[n];
  
        for (int i = 0; i < n; i++)
        {
          DTMIterator iter = m_exprs[i].asIterator(m_execContext, context);
          newIters[i] = iter;
        }
        m_iterators = newIters;
      }
    }
    catch(Exception e)
    {
      throw new org.apache.xml.utils.WrappedRuntimeException(e);
    }
  }
  
  /**
   * Add DTMIterator object instance to an array of DTMIterator objects, 
   * representing node sequences whose intersection needs to be found.
   */
  public void addIterator(DTMIterator expr)
  {

    // Increase array size by one
    if (m_iterators == null) {
      m_iterators = new DTMIterator[1];
      m_iterators[0] = expr;
    }
    else {
      DTMIterator[] exprs = m_iterators;
      int len = m_iterators.length;

      m_iterators = new DTMIterator[len + 1];

      System.arraycopy(exprs, 0, m_iterators, 0, len);

      m_iterators[len] = expr;
    }
    expr.nextNode();
    if(expr instanceof Expression) {
       ((Expression)expr).exprSetParent(this);
    }
  }
  
  /**
   * Detaches the iterator from the set which it iterated over, releasing
   * any computational resources and placing the iterator in the INVALID
   * state. After<code>detach</code> has been invoked, calls to
   * <code>nextNode</code> or<code>previousNode</code> will raise the
   * exception INVALID_STATE_ERR.
   */
  public void detach()
  {
	  if(m_allowDetach && null != m_iterators){         
		  int n = m_iterators.length;
		  for(int i = 0; i < n; i++)
		  {
			  m_iterators[i].detach();
		  }
		  m_iterators = null;                                
	  }
  }


  /**
   * Create a NodeSequenceIntersectIterator object, including creation 
   * of location path iterators from the opcode list, and call back 
   * into the Compiler to create predicate expressions.
   *
   * @param compiler The Compiler which is creating 
   * this expression.
   * @param opPos The position of this iterator in the 
   * opcode list from the compiler.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public NodeSequenceIntersectIterator(Compiler compiler, int opPos)
          throws javax.xml.transform.TransformerException
  {

    super();

    opPos = OpMap.getFirstChildPos(opPos);

    loadLocationPaths(compiler, opPos, 0);
  }
  
  /**
   * This will return an iterator capable of handling the 
   * intersection of XPath paths given.
   * 
   * @param compiler The Compiler which is creating 
   * this expression.
   * @param opPos The position of this iterator in the 
   * opcode list from the compiler.
   * 
   * @return Object that is derived from LocPathIterator.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public static LocPathIterator createIntersectIterator(Compiler compiler, int opPos)
          throws javax.xml.transform.TransformerException
  {  	
  	NodeSequenceIntersectIterator nodeSeqInterSectIter = new NodeSequenceIntersectIterator(compiler, opPos);
  	int nPaths = nodeSeqInterSectIter.m_exprs.length;
  	
	  	for(int i = 0; i < nPaths; i++)
	  	{
	  		PredicatedNodeTest lpi = nodeSeqInterSectIter.m_exprs[i];
	  		nodeSeqInterSectIter.addNodeTest(lpi);
	  	}
	  	return nodeSeqInterSectIter; 
  }
  
  private void addNodeTest(PredicatedNodeTest test)
  {

    // Increase array size by 1
    if (m_nodeTests == null)
    {
      m_nodeTests = new PredicatedNodeTest[1];
      m_nodeTests[0] = test;
    }
    else
    {
      PredicatedNodeTest[] tests = m_nodeTests;
      int len = m_nodeTests.length;

      m_nodeTests = new PredicatedNodeTest[len + 1];

      System.arraycopy(tests, 0, m_nodeTests, 0, len);

      m_nodeTests[len] = test;
    }
    
    test.exprSetParent(this);
  }
  
  /** 
   * Get the analysis bits for this walker, as defined in the WalkerFactory.
   * @return One of WalkerFactory#BIT_DESCENDANT, etc.
   */
  public int getAnalysisBits()
  {
    int bits = 0;
    
    if (m_exprs != null)
    {
      int n = m_exprs.length;

      for (int i = 0; i < n; i++)
      {
      	int bit = m_exprs[i].getAnalysisBits();
        bits |= bit;
      }
    }

    return bits;
  }
  
  /**
   * Read the object from a serialization stream.
   *
   * @param stream Input stream to read from
   *
   * @throws java.io.IOException
   * @throws javax.xml.transform.TransformerException
   */
  private void readObject(java.io.ObjectInputStream stream)
          throws java.io.IOException, javax.xml.transform.TransformerException
  {
    try
    {
      stream.defaultReadObject();
      m_clones =  new IteratorPool(this);
    }
    catch (ClassNotFoundException cnfe)
    {
      throw new javax.xml.transform.TransformerException(cnfe);
    }
  }

  /**
   * Get a cloned LocPathIterator that holds the same 
   * position as this iterator.
   *
   * @return A clone of this iterator that holds the same node position.
   *
   * @throws CloneNotSupportedException
   */
  public Object clone() throws CloneNotSupportedException
  {

    NodeSequenceIntersectIterator clone = (NodeSequenceIntersectIterator) super.clone();
    if (m_iterators != null)
    {
      int n = m_iterators.length;
      
      clone.m_iterators = new DTMIterator[n];

      for (int i = 0; i < n; i++)
      {
        clone.m_iterators[i] = (DTMIterator)m_iterators[i].clone();
      }
    }

    return clone;
  }
  
  
  /**
   * Create a new location path iterator.
   *
   * @param compiler The Compiler which is creating 
   * this expression.
   * @param opPos The position of this iterator in the 
   *
   * @return New location path iterator.
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected LocPathIterator createDTMIterator(
          Compiler compiler, int opPos) throws javax.xml.transform.TransformerException
  {
    LocPathIterator lpi = (LocPathIterator)WalkerFactory.newDTMIterator(compiler, opPos, 
                                      (compiler.getLocationPathDepth() <= 0));
    return lpi;
  }

  /**
   * Initialize the location path iterators.  Recursive.
   *
   * @param compiler The Compiler which is creating 
   * this expression.
   * @param opPos The position of this iterator in the 
   * opcode list from the compiler.
   * @param count The insert position of the iterator.
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void loadLocationPaths(Compiler compiler, int opPos, int count)
          throws javax.xml.transform.TransformerException
  {

    // TODO: Handle unwrapped FilterExpr
    int steptype = compiler.getOp(opPos);

    if (steptype == OpCodes.OP_LOCATIONPATH)
    {
      loadLocationPaths(compiler, compiler.getNextOpPos(opPos), count + 1);

      m_exprs[count] = createDTMIterator(compiler, opPos);
      m_exprs[count].exprSetParent(this);
    }
    else
    {

      // Have to check for unwrapped functions, which the LocPathIterator
      // doesn't handle. 
      switch (steptype)
      {
      case OpCodes.OP_VARIABLE :
      case OpCodes.OP_CONSTRUCTOR_STYLESHEET_EXT_FUNCTION :
      case OpCodes.OP_FUNCTION :
      case OpCodes.OP_GROUP :
        loadLocationPaths(compiler, compiler.getNextOpPos(opPos), count + 1);

        WalkingIterator iter =
          new WalkingIterator(compiler.getNamespaceContext());
        iter.exprSetParent(this);
          
        if(compiler.getLocationPathDepth() <= 0)
          iter.setIsTopLevel(true);

        iter.m_firstWalker = new org.apache.xpath.axes.FilterExprWalker(iter);

        iter.m_firstWalker.init(compiler, opPos, steptype);

        m_exprs[count] = iter;
        break;
      default :
        m_exprs = new LocPathIterator[count];
      }
    }
  }

  /**
   * Returns the next node in the set and advances the 
   * position of the iterator in the set.
   */
  public int nextNode()
  {

    int result = DTM.NULL;

    if (m_iterators != null) {    	
    	if (m_lastFetched == DTM.NULL) {
    		int n = m_iterators.length;
    		List<List<Integer>> memberLists = new ArrayList<List<Integer>>();
    		for (int i = 0; i < n; i++) {
    			List<Integer> memberList = new ArrayList<Integer>();
    			DTMIterator dtmIter = m_iterators[i];
    			int node = DTM.NULL;
    			while ((node = dtmIter.nextNode()) != DTM.NULL) {
    				memberList.add(Integer.valueOf(node)); 
    			}
    			memberLists.add(memberList);
    		}
    		
    		Set<Integer> intersectResultSet = null;
    		
    		// XPath 'intersect' operator result in document order
    		List<Integer> intersectResultInDocOrder = null;
    		
    		for (int idx = 0; idx < memberLists.size(); idx++) {
    			List<Integer> nodeRefList1 = null;
    			List<Integer> nodeRefList2 = null;    			
    			if (intersectResultSet == null) {
    				nodeRefList1 = memberLists.get(idx);
    				if ((idx + 1) < memberLists.size()) {
    				   nodeRefList2 = memberLists.get(idx + 1);
    				   intersectResultSet = nodeRefList1.stream()
    						                            .distinct()
    						                            .filter(nodeRefList2::contains)
    						                            .collect(Collectors.toSet());
    				   idx++;
    				   continue;
    				}
    				else {
    				   intersectResultSet = new HashSet<Integer>();
    				   intersectResultSet.addAll(nodeRefList1);
    				}
    			}
    			else {    				
    				nodeRefList1 = Arrays.asList(intersectResultSet.toArray(new Integer[0]));
    				nodeRefList2 = memberLists.get(idx);
    				intersectResultSet = nodeRefList1.stream()
                             						 .distinct()
                             						 .filter(nodeRefList2::contains)
                             						 .collect(Collectors.toSet());
					idx++;
					continue;
    			}
    		}
    		
    		List<Integer> list = Arrays.asList(intersectResultSet.toArray(new Integer[0]));
    		
    		list.sort(null);
    		intersectResultInDocOrder = list; 
    		
    		m_NodeRefIter = intersectResultInDocOrder.iterator();
    		if (m_NodeRefIter.hasNext()) {
    		   result = m_NodeRefIter.next();    		    
    		}
    		else {
    		   result = DTM.NULL;
    		}
    		
    		m_lastFetched = result;
    	}
    	else {
    		if (m_NodeRefIter.hasNext()) {
     		   result = m_NodeRefIter.next();
     		}
    		else {
    		   result = DTM.NULL;
    		}
    		
    		m_lastFetched = result;
    	}
    }

    return result;
  }
            
  /**
   * This function is used to fixup variables from QNames to stack frame 
   * indexes at stylesheet build time.
   * @param vars List of QNames that correspond to variables.  This list 
   * should be searched backwards for the first qualified name that 
   * corresponds to the variable reference qname.  The position of the 
   * QName in the vector from the start of the vector will be its position 
   * in the stack frame (but variables above the globalsTop value will need 
   * to be offset to the current stack frame).
   */
  public void fixupVariables(java.util.Vector vars, int globalsSize)
  {
    for (int i = 0; i < m_exprs.length; i++) 
    {
      m_exprs[i].fixupVariables(vars, globalsSize);
    }
    
  }
  
  /**
   * An array of LocPathIterator object instances, one for 
   * each location path contained in the intersect expression.
   */
  protected LocPathIterator[] m_exprs;

    
  /**
   * An array of DTMIterator object instances, one for each 
   * location path contained in the intersect expression.
   */
  protected DTMIterator[] m_iterators;
      
  /**
   * Returns the axis being iterated, if it is known.
   * 
   * @return Axis.CHILD, etc., or -1 if the axis is not known or is of multiple 
   * types.
   */
  public int getAxis()
  {
    // Could be smarter.
    return -1;
  }
  
  class iterOwner implements ExpressionOwner
  {
  	int m_index;
  	
  	iterOwner(int index)
  	{
  		m_index = index;
  	}
  	
    /**
     * @see ExpressionOwner#getExpression()
     */
    public Expression getExpression()
    {
      return m_exprs[m_index];
    }

    /**
     * @see ExpressionOwner#setExpression(Expression)
     */
    public void setExpression(Expression exp)
    {
    	
    	if(!(exp instanceof LocPathIterator))
    	{
    		// Yuck.  Need FilterExprIter.  Or make it so m_exprs can be just 
    		// plain expressions?
    		WalkingIterator wi = new WalkingIterator(getPrefixResolver());
    		FilterExprWalker few = new FilterExprWalker(wi);
    		wi.setFirstWalker(few);
    		few.setInnerExpression(exp);
    		wi.exprSetParent(NodeSequenceIntersectIterator.this);
    		few.exprSetParent(wi);
    		exp.exprSetParent(few);
    		exp = wi;
    	}
    	else
    		exp.exprSetParent(NodeSequenceIntersectIterator.this);
    	m_exprs[m_index] = (LocPathIterator)exp;
    }

  }

  /**
   * @see org.apache.xpath.XPathVisitable#callVisitors(ExpressionOwner, XPathVisitor)
   */
  public void callVisitors(ExpressionOwner owner, XPathVisitor visitor)
  {
  	 	if(visitor.visitIntersectPath(owner, this))
  	 	{
  	 		if(m_exprs != null)
  	 		{
  	 			int n = m_exprs.length;
  	 			for(int i = 0; i < n; i++)
  	 			{
  	 				m_exprs[i].callVisitors(new iterOwner(i), visitor);
  	 			}
  	 		}
  	 	}
  }
  
  /**
   * @see Expression#deepEquals(Expression)
   */
  public boolean deepEquals(Expression expr)
  {
	  if (!super.deepEquals(expr))
		  return false;

	  NodeSequenceIntersectIterator upi = (NodeSequenceIntersectIterator) expr;

	  if (m_exprs != null)
	  {
		  int n = m_exprs.length;

		  if((null == upi.m_exprs) || (upi.m_exprs.length != n))
			  return false;

		  for (int i = 0; i < n; i++)
		  {
			  if(!m_exprs[i].deepEquals(upi.m_exprs[i]))
				  return false;
		  }
	  }
	  else if (null != upi.m_exprs)
	  {
		  return false;
	  }

	  return true;
  }

}
