package org.apache.xpath.axes;

import javax.xml.transform.TransformerException;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMFilter;
import org.apache.xpath.Expression;
import org.apache.xpath.parser.StepExpr;

/**
 * <meta name="usage" content="advanced"/>
 * This class implements a general iterator for
 * those LocationSteps with only one step, and perhaps a predicate, 
 * that only go forward (i.e. it can not be used with ancestors, 
 * preceding, etc.)
 * @see org.apache.xpath.axes.WalkerFactory#newLocPathIterator
 */
public class OneStepIteratorForward extends ChildTestIterator
{
  /** The traversal axis from where the nodes will be filtered. */
  protected int m_axis = -1;

  /**
   * Create a OneStepIteratorForward object.
   *
   * @param stepExpr The step expression from the parser.
   * @param analysis The analysis bits for the total path expression.
   *
   * @throws javax.xml.transform.TransformerException
   */
  OneStepIteratorForward(StepExpr stepExpr, int analysis)
          throws javax.xml.transform.TransformerException
  {
    super(stepExpr, analysis);
    
    m_axis = stepExpr.getAxis();
    
  }
    
  /**
   * Create a OneStepIterator object that will just traverse the self axes.
   * 
   * @param axis One of the org.apache.xml.dtm.Axis integers.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public OneStepIteratorForward(int axis)
  {
    super(null);
    
    m_axis = axis;
    int whatToShow = DTMFilter.SHOW_ALL;
    initNodeTest(whatToShow);
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
    m_traverser = m_cdtm.getAxisTraverser(m_axis);
    
  }
  
//  /**
//   * Return the first node out of the nodeset, if this expression is 
//   * a nodeset expression.  This is the default implementation for 
//   * nodesets.
//   * <p>WARNING: Do not mutate this class from this function!</p>
//   * @param xctxt The XPath runtime context.
//   * @return the first node out of the nodeset, or DTM.NULL.
//   */
//  public int asNode(XPathContext xctxt)
//    throws javax.xml.transform.TransformerException
//  {
//    if(getPredicateCount() > 0)
//      return super.asNode(xctxt);
//      
//    int current = xctxt.getCurrentNode();
//    
//    DTM dtm = xctxt.getDTM(current);
//    DTMAxisTraverser traverser = dtm.getAxisTraverser(m_axis);
//    
//    String localName = getLocalName();
//    String namespace = getNamespace();
//    int what = m_whatToShow;
//    
//    // System.out.println("what: ");
//    // NodeTest.debugWhatToShow(what);
//    if(DTMFilter.SHOW_ALL == what
//       || ((DTMFilter.SHOW_ELEMENT & what) == 0)
//       || localName == NodeTest.WILD
//       || namespace == NodeTest.WILD)
//    {
//      return traverser.first(current);
//    }
//    else
//    {
//      int type = getNodeTypeTest(what);
//      int extendedType = dtm.getExpandedTypeID(namespace, localName, type);
//      return traverser.first(current, extendedType);
//    }
//  }
  
  /**
   * Get the next node via getFirstAttribute && getNextAttribute.
   */
  protected int getNextNode()
  {
    m_lastFetched = (DTM.NULL == m_lastFetched)
                     ? m_traverser.first(m_context)
                     : m_traverser.next(m_context, m_lastFetched);
    return m_lastFetched;
  }
  
  /**
   * Returns the axis being iterated, if it is known.
   * 
   * @return Axis.CHILD, etc., or -1 if the axis is not known or is of multiple 
   * types.
   */
  public int getAxis()
  {
    return m_axis;
  }

  /**
   * @see Expression#deepEquals(Expression)
   */
  public boolean deepEquals(Expression expr)
  {
  	if(!super.deepEquals(expr))
  		return false;
  		
  	if(m_axis != ((OneStepIteratorForward)expr).m_axis)
  		return false;
  		
  	return true;
  }

  
}