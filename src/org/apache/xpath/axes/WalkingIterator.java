package org.apache.xpath.axes;

import javax.xml.transform.TransformerException;

import org.apache.xml.dtm.DTM;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.VariableComposeState;
import org.apache.xpath.VariableStack;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.parser.Node;
import org.apache.xpath.parser.NonExecutableExpression;

/**
 * Location path iterator that uses Walkers.
 */

public class WalkingIterator extends LocPathIterator implements ExpressionOwner
{  
  /**
   * Create a WalkingIterator object.
   *
   * @param nscontext The namespace context for this iterator,
   * should be OK if null.
   */
  public WalkingIterator(PrefixResolver nscontext, org.apache.xpath.parser.PathExpr path)
  {

    super(nscontext);
    
    try
    {
    	m_firstWalker = WalkerFactory.loadWalkers(path, this);
    	m_lastUsedWalker = m_firstWalker;
    }
    catch(TransformerException te)
    {
    	throw new org.apache.xml.utils.WrappedRuntimeException(te);
    }

  }
  
  /**
   * Create a WalkingIterator object.
   *
   * @param nscontext The namespace context for this iterator,
   * should be OK if null.
   */
  public WalkingIterator(PrefixResolver nscontext)
  {
    super(nscontext);
  }

  
  
  /** 
   * Get the analysis bits for this walker, as defined in the WalkerFactory.
   * @return One of WalkerFactory#BIT_DESCENDANT, etc.
   */
  public int getAnalysisBits()
  {
    int bits = 0;
    if (null != m_firstWalker)
    {    	
      AxesWalker walker = m_firstWalker;

      while (null != walker)
      {
        int bit = walker.getAnalysisBits();
        bits |= bit;
        walker = walker.getNextWalker();
      }       
    }
    return bits;
  }
  
  /**
   * Get a cloned WalkingIterator that holds the same
   * position as this iterator.
   *
   * @return A clone of this iterator that holds the same node position.
   *
   * @throws CloneNotSupportedException
   */
  public Object clone() throws CloneNotSupportedException
  {

    WalkingIterator clone = (WalkingIterator) super.clone();

    //    clone.m_varStackPos = this.m_varStackPos;
    //    clone.m_varStackContext = this.m_varStackContext;
    if (null != m_firstWalker)
    {
      clone.m_firstWalker = m_firstWalker.cloneDeep(clone, null);
    }

    return clone;
  }
  
  /**
   * Reset the iterator.
   */
  public void reset()
  {

    super.reset();

    if (null != m_firstWalker)
    {
      m_lastUsedWalker = m_firstWalker;

      m_firstWalker.setRoot(m_context);
    }

  }
  
  /**
   * Initialize the context values for this expression
   * after it is cloned.
   *
   * @param execContext The XPath runtime context for this
   * transformation.
   */
  public void setRoot(int context, Object environment)
  {

    super.setRoot(context, environment);
    
    if(null != m_firstWalker)
    {
      m_firstWalker.setRoot(context);
      m_lastUsedWalker = m_firstWalker;
    }
  }
  
  /**
   *  Returns the next node in the set and advances the position of the
   * iterator in the set. After a NodeIterator is created, the first call
   * to nextNode() returns the first node in the set.
   * @return  The next <code>Node</code> in the set being iterated over, or
   *   <code>null</code> if there are no more members in that set.
   */
  public int nextNode()
  {
  	if(m_foundLast)
  		return DTM.NULL;

    // If the variable stack position is not -1, we'll have to 
    // set our position in the variable stack, so our variable access 
    // will be correct.  Iterators that are at the top level of the 
    // expression need to reset the variable stack, while iterators 
    // in predicates do not need to, and should not, since their execution
    // may be much later than top-level iterators.  
    // m_varStackPos is set in setRoot, which is called 
    // from the execute method.
    if (-1 == m_stackFrame)
    {
      return returnNextNode(m_firstWalker.nextNode());
    }
    else
    {
      VariableStack vars = m_execContext.getVarStack();

      // These three statements need to be combined into one operation.
      int savedStart = vars.getStackFrame();

      vars.setStackFrame(m_stackFrame);

      int n = returnNextNode(m_firstWalker.nextNode());

      // These two statements need to be combined into one operation.
      vars.setStackFrame(savedStart);

      return n;
    }
  }

  
  /**
   * <meta name="usage" content="advanced"/>
   * Get the head of the walker list.
   *
   * @return The head of the walker list, or null
   * if this iterator does not implement walkers.
   */
  public final AxesWalker getFirstWalker()
  {
    return m_firstWalker;
  }
  
  /**
   * <meta name="usage" content="advanced"/>
   * Set the head of the walker list.
   * 
   * @param walker Should be a valid AxesWalker.
   */
  public final void setFirstWalker(AxesWalker walker)
  {
    m_firstWalker = walker;
  }


  /**
   * <meta name="usage" content="advanced"/>
   * Set the last used walker.
   *
   * @param walker The last used walker, or null.
   */
  public final void setLastUsedWalker(AxesWalker walker)
  {
    m_lastUsedWalker = walker;
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Get the last used walker.
   *
   * @return The last used walker, or null.
   */
  public final AxesWalker getLastUsedWalker()
  {
    return m_lastUsedWalker;
  }
  
  /**
   *  Detaches the iterator from the set which it iterated over, releasing
   * any computational resources and placing the iterator in the INVALID
   * state. After<code>detach</code> has been invoked, calls to
   * <code>nextNode</code> or<code>previousNode</code> will raise the
   * exception INVALID_STATE_ERR.
   */
  public void detach()
  {   
    if(m_allowDetach)
    {
	  	AxesWalker walker = m_firstWalker; 
	    while (null != walker)
	    {
	      walker.detach();
	      walker = walker.getNextWalker();
	    }
	
	    m_lastUsedWalker = null;
	    
	    // Always call the superclass detach last!
	    super.detach();
    }
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
  public void fixupVariables(VariableComposeState vcs)
  {
    m_predicateIndex = -1;

    AxesWalker walker = m_firstWalker;

    while (null != walker)
    {
      walker.fixupVariables(vcs);
      walker = walker.getNextWalker();
    }
  }
  
  /**
   * @see XPathVisitable#callVisitors(ExpressionOwner, XPathVisitor)
   */
  public void callVisitors(ExpressionOwner owner, XPathVisitor visitor)
  {
  	 	if(visitor.visitLocationPath(owner, this))
  	 	{
  	 		if(null != m_firstWalker)
  	 		{
  	 			m_firstWalker.callVisitors(this, visitor);
  	 		}
  	 	}
  }

  
  /** The last used step walker in the walker list.
   *  @serial */
  protected AxesWalker m_lastUsedWalker;

  /** The head of the step walker list.
   *  @serial */
  protected AxesWalker m_firstWalker;

  /**
   * @see ExpressionOwner#getExpression()
   */
  public Expression getExpression()
  {
    return m_firstWalker;
  }

  /**
   * @see ExpressionOwner#setExpression(Expression)
   */
  public void setExpression(Expression exp)
  {
    // assertion(null != exp, "Expression owner can not be set to null!");
  	exp.exprSetParent(this);
  	m_firstWalker = (AxesWalker)exp;
  }
  
    /**
     * @see Expression#deepEquals(Expression)
     */
    public boolean deepEquals(Expression expr)
    {
      if (!super.deepEquals(expr))
                return false;

      AxesWalker walker1 = m_firstWalker;
      AxesWalker walker2 = ((WalkingIterator)expr).m_firstWalker;
      while ((null != walker1) && (null != walker2))
      {
        if(!walker1.deepEquals(walker2))
        	return false;
        walker1 = walker1.getNextWalker();
        walker2 = walker2.getNextWalker();
      }
      
      if((null != walker1) || (null != walker2))
      	return false;

      return true;
    }

  public Node jjtGetChild(int i) 
  {
    int superclassChildCount = super.jjtGetNumChildren();
    if((null != m_firstWalker) && i == 0)
    	return m_firstWalker;
    else
    	return super.jjtGetChild(i-((null == m_firstWalker) ? 0 : 1));
  }

  public int jjtGetNumChildren() 
  {
  	int superChildCount = super.jjtGetNumChildren();
    return superChildCount+((null == m_firstWalker) ? 0 : 1);
  }
  
  

  /**
   * @see org.apache.xpath.parser.SimpleNode#checkTreeIntegrity(int, int, boolean)
   */
  public boolean checkTreeIntegrity(
    int levelCount,
    int childNumber,
    boolean parentOK)
  {
    ExpressionNode expOwner = getExpressionOwner();
    if(null != expOwner)
    {
      if(!(expOwner instanceof Node))
        parentOK = flagProblem(" Expression owner is not a Node! It's a "+expOwner.getClass().getName());
      else if(expOwner instanceof NonExecutableExpression)
        parentOK = flagProblem(" Expression owner is a NonExecutableExpression!");
    }
    return super.checkTreeIntegrity(levelCount, childNumber, parentOK);
  
  }

  /**
   * @see org.apache.xpath.parser.Node#jjtClose()
   */
  public void jjtClose()
  {
    if(null == getExpressionOwner())
      flagProblem("The expression owner can not be null on jjtClose!");
    super.jjtClose();
  }

}