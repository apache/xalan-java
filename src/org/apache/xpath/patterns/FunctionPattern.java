package org.apache.xpath.patterns;

import org.apache.xpath.XPath;
import org.apache.xpath.DOMHelper;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

public class FunctionPattern extends StepPattern
{
  public FunctionPattern(Expression expr)
  {
    super(0, null, null);
    m_functionExpr = expr;
  }
  
  /**
   * Static calc of match score.
   */
  protected final void calcScore()
  {
    m_score = SCORE_OTHER;
    if(null == m_targetString)
      calcTargetString();
  }
  
  Expression m_functionExpr;
  
  /**
   * Test a node to see if it matches the given node test.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param len The length of the argument.
   * @param len The type of the step.
   * @returns score in an XNumber, one of MATCH_SCORE_NODETEST, 
   * MATCH_SCORE_NONE, MATCH_SCORE_OTHER, MATCH_SCORE_QNAME.
   */
  public XObject execute(XPathContext xctxt)
    throws org.xml.sax.SAXException
  {
    Node context = xctxt.getCurrentNode();
    XObject obj = m_functionExpr.execute(xctxt);
    NodeIterator nl = obj.nodeset();
    XNumber score = SCORE_NONE;
    if(null != nl)
    {
      Node n;
      while(null != (n = nl.nextNode()))
      {
        score = (n.equals(context)) ? SCORE_OTHER : SCORE_NONE;
        if(score == SCORE_OTHER)
        {
          context = n;
          break;
        }
      }
    }
    return score;
  }
}
