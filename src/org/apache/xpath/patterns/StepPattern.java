package org.apache.xpath.patterns;

import org.apache.xpath.Expression;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.XPathContext;
import org.apache.xalan.utils.PrefixResolver;
import org.apache.xpath.axes.SubContextList;

import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

public class StepPattern extends NodeTest implements SubContextList
{  
  public StepPattern(int whatToShow, String namespace, String name)
  {
    super(whatToShow, namespace, name);
  }
  
  public StepPattern(int whatToShow)
  {
    super(whatToShow);
  }
  
  /**
   * Reference to nodetest and predicate for 
   * parent or ancestor.
   */
  StepPattern m_relativePathPattern;
  
  public void setRelativePathPattern(StepPattern expr)
  {
    m_relativePathPattern = expr;
  }
  
  Expression[] m_predicates;
  
  public Expression getPredicate(int i)
  {
    return m_predicates[i];
  }
  
  public final int getPredicateCount()
  {
    return (null == m_predicates) ? 0 : m_predicates.length;
  }
  
  public void setPredicates(Expression[] predicates)
  {
    m_predicates = predicates;
  }
  
  public XObject executeStep(XPathContext xctxt)
    throws org.xml.sax.SAXException
  {
    XObject score = super.execute(xctxt);
    if(score == NodeTest.SCORE_NONE)
    {
      // System.out.println("executeStep: "+this.m_name+" = "+score);
      return score;
    }
    
    int n = getPredicateCount();
    if(n == 0)
    {
      // System.out.println("executeStep: "+this.m_name+" = "+score);
      return score;
    }

    // xctxt.getVarStack().setCurrentStackFrameIndex(m_lpi.getStackFrameIndex());
    // xctxt.setNamespaceContext(m_lpi.getPrefixResolver());
    // xctxt.pushCurrentNode(context);
    try
    {
      xctxt.pushSubContextList(this);
      for(int i = 0; i < n; i++)
      {
        XObject pred;
        pred = m_predicates[i].execute(xctxt);
        if(XObject.CLASS_NUMBER == pred.getType())
        {
          if(this.getProximityPosition(xctxt) != (int)pred.num())
          {
            score = NodeTest.SCORE_NONE;
            break;
          }
        }
        else if(!pred.bool())
        {
          score = NodeTest.SCORE_NONE;
          break;
        }
        
        // countProximityPosition(++m_predicateIndex);
      }
    }
    finally
    {
      xctxt.popSubContextList();
    }
    // System.out.println("executeStep: "+this.m_name+" = "+score);
    return score;
  }
  
  public int getProximityPosition(XPathContext xctxt)
  {
    Node context = xctxt.getCurrentNode();
    // System.out.println("context: "+context.getNodeName());
    Node parentContext = xctxt.getDOMHelper().getParentOfNode(context);
    // System.out.println("parentContext: "+parentContext.getNodeName());
    try
    {
      xctxt.pushCurrentNode(parentContext);
      
      int pos = 0;
      for(Node child = parentContext.getFirstChild(); 
          child != null; 
          child = child.getNextSibling())
      {
        try
        {
          xctxt.pushCurrentNode(child);
          if(NodeTest.SCORE_NONE != super.execute(xctxt))
          {
            pos++;
            if(child.equals( context ))
            {
              return pos;
            }
          }
        }
        finally
        {
          xctxt.popCurrentNode();
        }
      }
    }
    catch(org.xml.sax.SAXException se)
    {
      // TODO: should keep throw sax exception...
      throw new java.lang.RuntimeException(se.getMessage());
    }
    finally
    {
      xctxt.popCurrentNode();
      // xctxt.popContextNodeList();
    }
    return 0;
  }
  
  public int getLastPos(XPathContext xctxt)
  {
    Node context = xctxt.getCurrentNode();
    Node parentContext = xctxt.getDOMHelper().getParentOfNode(context);
    try
    {
      xctxt.pushCurrentNode(parentContext);
      
      int count = 0;
      for(Node child = parentContext.getFirstChild(); 
          child != null; 
          child = child.getNextSibling())
      {
        try
        {
          xctxt.pushCurrentNode(child);
          if(NodeTest.SCORE_NONE != super.execute(xctxt))
            count++;
        }
        finally
        {
          xctxt.popCurrentNode();
        }
        return count;
      }
    }
    catch(org.xml.sax.SAXException se)
    {
      // TODO: should keep throw sax exception...
      throw new java.lang.RuntimeException(se.getMessage());
    }
    finally
    {
      xctxt.popCurrentNode();
      // xctxt.popContextNodeList();
    }
    return 0;
  }
  
  public XObject executeRelativePathPattern(XPathContext xctxt)
    throws org.xml.sax.SAXException
  {
    XObject score;    
    Node parent = xctxt.getDOMHelper().getParentOfNode(xctxt.getCurrentNode());
    if (null != parent)
    {
      try
      {
        xctxt.pushCurrentNode(parent);
        score = execute(xctxt);
        if(score != NodeTest.SCORE_NONE)
          score = SCORE_OTHER;
      }
      finally
      {
        xctxt.popCurrentNode();
      }
    }  
    else
      score = NodeTest.SCORE_NONE;    
    
    return score;
  }
  
  public XObject execute(XPathContext xctxt)
    throws org.xml.sax.SAXException
  {
    XObject score;
    // int n = getPredicateCount();
    
    score = executeStep(xctxt);
    
    if((score != NodeTest.SCORE_NONE) && (null != m_relativePathPattern))
    {
      score = m_relativePathPattern.executeRelativePathPattern(xctxt);
    }
    return score;
  }
  

}
