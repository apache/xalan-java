package org.apache.xpath.patterns;

import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

public class UnionPattern extends Expression
{
  private StepPattern[] m_patterns;
  
  public void setPatterns(StepPattern[] patterns)
  {
    m_patterns = patterns;
  }
  
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
    XObject bestScore = null;
    
    int n = m_patterns.length;
    for(int i = 0; i < n; i++)
    {
      XObject score = m_patterns[i].execute(xctxt);
      if(score != NodeTest.SCORE_NONE)
      {
        if(null == bestScore)
          bestScore = score;
        else if(score.num() > bestScore.num())
          bestScore = score;
      }
    }
    if(null == bestScore)
    {
      bestScore = NodeTest.SCORE_NONE;
    }
    
    return bestScore;
  }
}
