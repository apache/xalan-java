package org.apache.xpath.seqctor;

import javax.xml.transform.TransformerException;

import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.VariableComposeState;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.parser.Node;

public class FLWRExpr extends Expression implements ExpressionOwner
{
  Binding[] m_bindings;
  Expression m_return;

  /**
   * Constructor for FLWRExpr
   */
  public FLWRExpr()
  {
    super();
  }

  /**
   * @see SimpleNode#shouldReduceIfOneChild()
   */
  public boolean shouldReduceIfOneChild()
  {
    return (jjtGetNumChildren() == 1) ? true : false;
  }

  /**
   * @see org.apache.xpath.XPathVisitable#callVisitors(ExpressionOwner, XPathVisitor)
   */
  public void callVisitors(ExpressionOwner owner, XPathVisitor visitor)
  {
  }

  /**
   * @see org.apache.xpath.Expression#deepEquals(Expression)
   */
  public boolean deepEquals(Expression expr)
  {
    return false;
  }

  /**
   * @see org.apache.xpath.Expression#execute(XPathContext)
   */
  public XObject execute(XPathContext xctxt) throws TransformerException
  {
    return new FLWRIter(m_bindings, m_return, xctxt);
  }

  /**
   * @see org.apache.xpath.Expression#fixupVariables(Vector, int)
   */
  public void fixupVariables(VariableComposeState vcs)
  {
    vcs.pushStackMark();
    Binding[] bindings = m_bindings;
    int globalsSize = vcs.getGlobalsSize();
    for (int i = 0; i < bindings.length; i++)
    {
      Binding binding = bindings[i];
      binding.getExpr().fixupVariables(vcs);
      Variable var = binding.getVar();
      QName varName = var.getQName();
      int index = vcs.addVariableName(varName) - globalsSize;
      var.setIndex(index);
      var.setFixUpWasCalled(true);
      // var.fixupVariables(vcs);
    }    
    m_return.fixupVariables(vcs);
    vcs.popStackMark();
  }

  /**
   * @see org.apache.xpath.ExpressionOwner#getExpression()
   */
  public Expression getExpression()
  {
    return null;
  }

  /**
   * @see org.apache.xpath.ExpressionOwner#setExpression(Expression)
   */
  public void setExpression(Expression exp)
  {
    m_return = exp;
  }

  /**
   * Returns the r.
   * @return Expression
   */
  public Expression getReturn()
  {
    return m_return;
  }

  /**
   * Sets the inExprs.
   * @param inExprs The inExprs to set
   */
  public void setBindings(Binding[] bindings)
  {
    m_bindings = bindings;
  }

  /**
   * Sets the r.
   * @param r The r to set
   */
  public void setReturn(Expression r)
  {
    m_return = r;
  }

  /**
   * @see org.apache.xpath.parser.Node#jjtAddChild(Node, int)
   */
  public void jjtAddChild(Node n, int i)
  {
    if(null == m_bindings && 0 == i)
      m_return = (Expression)fixupPrimarys(n); // Act like a construction node!
    else if(i == 2)
      m_return = (Expression)fixupPrimarys(n);
  }
  
  

  /**
   * @see org.apache.xpath.parser.Node#jjtGetChild(int)
   */
  public Node jjtGetChild(int i)
  {
    if (i == jjtGetNumChildren() - 1)
      return m_return;
    else
      if (null != m_bindings)
      {
        if ((i % 2) == 1)
          return m_bindings[i / 2].getVar();
        else
          return m_bindings[i / 2].getExpr();
      }
      else
        return null;
  }

  /**
   * @see org.apache.xpath.parser.Node#jjtGetNumChildren()
   */
  public int jjtGetNumChildren()
  {
    return (((null != m_bindings) ? m_bindings.length : 0)*2)+
      ((null != m_return) ? 1 : 0);
  }

}

