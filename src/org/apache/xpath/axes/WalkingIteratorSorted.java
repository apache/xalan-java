package org.apache.xpath.axes;

import org.apache.xpath.XPathContext;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.compiler.Compiler;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;

public class WalkingIteratorSorted extends WalkingIterator
{
  /**
   * Create a WalkingIteratorSorted object.
   *
   * @param nscontext The namespace context for this iterator,
   * should be OK if null.
   */
  public WalkingIteratorSorted(PrefixResolver nscontext)
  {

    super(nscontext);
  }

  /**
   * Create a WalkingIteratorSorted iteratorWalkingIteratorSortedWalkingIteratorSorted.
   *
   * @param compiler The Compiler which is creating
   * this expression.
   * @param opPos The position of this iterator in the
   * opcode list from the compiler.
   * @param shouldLoadWalkers True if walkers should be
   * loaded, or false if this is a derived iterator and
   * it doesn't wish to load child walkers.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public WalkingIteratorSorted(
          Compiler compiler, int opPos, int analysis, boolean shouldLoadWalkers)
            throws javax.xml.transform.TransformerException
  {
    super(compiler, opPos, analysis, shouldLoadWalkers);
    this.setShouldCacheNodes(true);
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
    
    this.setShouldCacheNodes(true);

    setNextPosition(0);
    m_firstWalker.setRoot(context);

    m_lastUsedWalker = m_firstWalker;

    int nextNode = DTM.NULL;
    AxesWalker walker = getLastUsedWalker();
    XPathContext execContext = (XPathContext)environment;
    execContext.pushCurrentNodeAndExpression(context, context);
    
    try
    {

    do
    {
      while (true)
      {
        if (null == walker)
          break;

        nextNode = walker.getNextNode();

        if (DTM.NULL == nextNode)
        {
          walker = walker.m_prevWalker;
        }
        else
        {
          if (walker.acceptNode(nextNode) != DTMIterator.FILTER_ACCEPT)
          {
            continue;
          }

          if (null == walker.m_nextWalker)
          {
            setLastUsedWalker(walker);

            // return walker.returnNextNode(nextNode);
            break;
          }
          else
          {
            AxesWalker prev = walker;

            walker = walker.m_nextWalker;

            walker.setRoot(nextNode);

            walker.m_prevWalker = prev;

            continue;
          }
        }  // if(null != nextNode)
      }  // while(null != walker)
      
      if(DTM.NULL != nextNode)
      {
        incrementNextPosition();
        // m_currentContextNode = nextNode;
        m_cachedNodes.addNodeInDocOrder(nextNode, execContext);
        walker = getLastUsedWalker();
      }
    }
      while (DTM.NULL != nextNode);
      
    }
    finally
    {
      execContext.popCurrentNodeAndExpression();
    }

    // m_prevReturned = nextNode;
    setNextPosition(0);
    m_last = m_cachedNodes.size();
    m_lastFetched = DTM.NULL;
    m_currentContextNode = DTM.NULL;
    m_foundLast = true;
  }
  
  public int nextNode()
  {
    return super.nextNode();
  }
  
  /**
   * Reset the iterator.
   */
  public void reset()
  {

    // super.reset();
    // m_foundLast = false;
    m_lastFetched = DTM.NULL;
    m_next = 0;
    // m_last = 0;
    
    if (null != m_firstWalker)
    {
      m_lastUsedWalker = m_firstWalker;

      m_firstWalker.setRoot(m_context);
    }

  }
  
}