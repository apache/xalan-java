package org.apache.xpath.axes;

import javax.xml.transform.TransformerException;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMFilter;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.VariableStack;
import org.apache.xpath.parser.StepExpr;

/**
 * Base for iterators that handle predicates.  Does the basic next 
 * node logic, so all the derived iterator has to do is get the 
 * next node.
 */
public abstract class BasicTestIterator extends LocPathIterator
{
  /**
   * Create a LocPathIterator object.
   *
   * @param nscontext The namespace context for this iterator,
   * should be OK if null.
   */
  protected BasicTestIterator()
  {
  }


  /**
   * Create a LocPathIterator object.
   *
   * @param nscontext The namespace context for this iterator,
   * should be OK if null.
   */
  protected BasicTestIterator(PrefixResolver nscontext)
  {

    super(nscontext);
  }

  /**
   * Create a LocPathIterator object, including creation
   * of step walkers from the opcode list, and call back
   * into the Compiler to create predicate expressions.
   *
   * @param compiler The Compiler which is creating
   * this expression.
   * @param opPos The position of this iterator in the
   * opcode list from the compiler.
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected BasicTestIterator(StepExpr stepExpr, int analysis)
          throws javax.xml.transform.TransformerException
  {
    super(stepExpr.getParser().getPrefixResolver());
    
    int whatToShow = stepExpr.getWhatToShow();

    if ((0 == (whatToShow
               & (DTMFilter.SHOW_ATTRIBUTE 
               | DTMFilter.SHOW_NAMESPACE 
               | DTMFilter.SHOW_ELEMENT
               | DTMFilter.SHOW_PROCESSING_INSTRUCTION))) 
               || (whatToShow == DTMFilter.SHOW_ALL))
      initNodeTest(whatToShow);
    else
    {
      initNodeTest(whatToShow, stepExpr.getNamespaceURI(),
                              stepExpr.getLocalName());
    }
    initPredicateInfo(stepExpr);
  }
	
  /**
   * Get the next node via getNextXXX.  Bottlenecked for derived class override.
   * @return The next node on the axis, or DTM.NULL.
   */
  protected abstract int getNextNode();

  /**
   *  Returns the next node in the set and advances the position of the
   * iterator in the set. After a NodeIterator is created, the first call
   * to nextNode() returns the first node in the set.
   *
   * @return  The next <code>Node</code> in the set being iterated over, or
   *   <code>null</code> if there are no more members in that set.
   */
  public int nextNode()
  {      
  	if(m_foundLast)
  		return DTM.NULL;
  		
    if(DTM.NULL == m_lastFetched)
    {
      resetProximityPositions();
    }

    int next;
    
    org.apache.xpath.VariableStack vars;
    int savedStart;
    if (-1 != m_stackFrame)
    {
      vars = m_execContext.getVarStack();

      // These three statements need to be combined into one operation.
      savedStart = vars.getStackFrame();

      vars.setStackFrame(m_stackFrame);
    }
    else
    {
      // Yuck.  Just to shut up the compiler!
      vars = null;
      savedStart = 0;
    }
    
    try
    {
      do
      {
        next = getNextNode();
  
        if (DTM.NULL != next)
        {
          if(DTMIterator.FILTER_ACCEPT == acceptNode(next))
            break;
          else
            continue;
        }
        else
          break;
      }
      while (next != DTM.NULL);
  
      if (DTM.NULL != next)
      {
      	m_pos++;
        return next;
      }
      else
      {
        m_foundLast = true;
  
        return DTM.NULL;
      }
    }
    finally
    {
      if (-1 != m_stackFrame)
      {
        // These two statements need to be combined into one operation.
        vars.setStackFrame(savedStart);
      }
    }
  }
  
  /**
   *  Get a cloned Iterator that is reset to the beginning
   *  of the query.
   * 
   *  @return A cloned NodeIterator set of the start of the query.
   * 
   *  @throws CloneNotSupportedException
   */
  public DTMIterator cloneWithReset() throws CloneNotSupportedException
  {

    ChildTestIterator clone = (ChildTestIterator) super.cloneWithReset();

    clone.resetProximityPositions();

    return clone;
  }
  


}

