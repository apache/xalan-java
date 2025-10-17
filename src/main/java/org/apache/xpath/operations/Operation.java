/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
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
package org.apache.xpath.operations;

import javax.xml.XMLConstants;

import org.apache.xalan.templates.ElemFunction;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.TemplateList;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.functions.XSL3ConstructorOrExtensionFunction;
import org.apache.xpath.functions.XSL3FunctionService;
import org.apache.xpath.functions.XSLFunctionBuilder;
import org.apache.xpath.objects.ElemFunctionItem;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.patterns.NodeTest;

/**
 * A class definition, implementing XPath binary operators. 
 */
public class Operation extends Expression implements ExpressionOwner
{
  static final long serialVersionUID = -3037139537171050430L;

  /** 
   * The left operand expression.
   */
  protected Expression m_left;

  /** 
   * The right operand expression.
   */
  protected Expression m_right;
  
  /**
   * An XPath context object.
   */
  protected XPathContext m_xctxt;
  
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
    m_left.fixupVariables(vars, globalsSize);
    m_right.fixupVariables(vars, globalsSize);
  }


  /**
   * Tell if this expression or it's subexpressions can traverse outside
   * the current subtree.
   *
   * @return true if traversal outside the context node's subtree can occur.
   */
  public boolean canTraverseOutsideSubtree()
  {

    if (null != m_left && m_left.canTraverseOutsideSubtree())
      return true;

    if (null != m_right && m_right.canTraverseOutsideSubtree())
      return true;

    return false;
  }

  /**
   * Set the left and right operand expressions for this operation.
   *
   * @param l The left expression operand.
   * @param r The right expression operand.
   */
  public void setLeftRight(Expression l, Expression r)
  {
    m_left = l;
    m_right = r;
    l.exprSetParent(this);
    r.exprSetParent(this);
  }

  /**
   * Execute a binary operation by calling execute on each of the operands,
   * and then calling the operate method on the derived class.
   *
   * @param xctxt The runtime execution context.
   *
   * @return The XObject result of the operation.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {
      
    XObject left = null;
    
    XObject right = null;
    
    m_xctxt = xctxt; 
    
    XSL3FunctionService xslFunctionService = xctxt.getXSLFunctionService();
    
    if (m_left instanceof XSL3ConstructorOrExtensionFunction) {
    	XSL3ConstructorOrExtensionFunction xpathFunc = (XSL3ConstructorOrExtensionFunction)m_left;
    	if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(xpathFunc.getNamespace())) {
    		left = xslFunctionService.callFunction(xpathFunc, null, xctxt); 
    	}
    	else {
    		left = m_left.execute(xctxt, true);  
    	}
    }
    else if (m_left instanceof SelfIteratorNoPredicate) {
    	XObject xpath3ContextItem = xctxt.getXPath3ContextItem();
    	if (xpath3ContextItem != null) {
    		left = xpath3ContextItem;     
    	}
    	else {
    		left = m_left.execute(xctxt, true);   
    	}
    }
    else if (m_left instanceof NodeTest) {       	        	    	
    	try {            	
			StylesheetRoot stylesheetRoot = XslTransformEvaluationHelper.getXslStylesheetRootFromXslElementRef(
                                                                                                           (ElemTemplateElement)m_left.getExpressionOwner());
			NodeTest nodeTest = (NodeTest)m_left; 
			java.lang.String funcLocalNameRef = nodeTest.getLocalName();
			java.lang.String funcNamespace = nodeTest.getNamespace();			    			  
			TemplateList templateList = stylesheetRoot.getTemplateListComposed();
			XSL3FunctionService m_xslFunctionService = XSLFunctionBuilder.getXSLFunctionService();

			if (!"".equals(funcLocalNameRef) && m_xslFunctionService.isFuncArityWellFormed(funcLocalNameRef)) {        	   
				int hashCharIdx = funcLocalNameRef.indexOf('#');
				java.lang.String funcNameRef2 = funcLocalNameRef.substring(0, hashCharIdx);
				int funcArity = Integer.valueOf(funcLocalNameRef.substring(hashCharIdx + 1));        		   
				ElemTemplate elemTemplate = templateList.getXslFunction(new QName(funcNamespace, funcNameRef2), funcArity);
				ElemFunction elemFunction = null;
				if (elemTemplate != null) {
					elemFunction = (ElemFunction)elemTemplate;
					int xslFuncDefnParamCount = elemFunction.getArity();                      
					java.lang.String str = funcLocalNameRef.substring(hashCharIdx + 1);
					int funcRefParamCount = (Integer.valueOf(str)).intValue();
					if (funcRefParamCount != xslFuncDefnParamCount) {
						throw new javax.xml.transform.TransformerException("FORG0006 : An XPath named function reference " + funcLocalNameRef + 
																																		" cannot resolve to a function "
																																		+ "definition.", this); 
					}

					if (elemFunction != null) {
						ElemFunctionItem elemFunctionObject = new ElemFunctionItem(elemFunction);
						
						left = elemFunctionObject; 
					}
				}
			}
			
			if (left == null) {
			   left = m_left.execute(xctxt, true);
			}
		}
		catch (Exception ex) {
			left = m_left.execute(xctxt, true);
		}
    }
    else {
    	left = m_left.execute(xctxt, true); 
    }

    if (m_right instanceof XSL3ConstructorOrExtensionFunction) {
    	XSL3ConstructorOrExtensionFunction xpathFunc = (XSL3ConstructorOrExtensionFunction)m_right;
    	if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(xpathFunc.getNamespace())) {
    		right = xslFunctionService.callFunction(xpathFunc, null, xctxt); 
    	}
    	else {
    		right = m_right.execute(xctxt, true);  
    	}
    }
    else if (m_right instanceof SelfIteratorNoPredicate) {
    	XObject xpath3ContextItem = xctxt.getXPath3ContextItem();
    	if (xpath3ContextItem != null) {
    		right = xpath3ContextItem;     
    	}
    	else {
    		right = m_right.execute(xctxt, true);   
    	}
    }
    else {
    	right = m_right.execute(xctxt, true); 
    }

    XObject result = operate(left, right);
    
    left.detach();
    right.detach();
    
    return result;
  }

  /**
   * Apply the operation to two operands, and return the result.
   *
   * @param left non-null reference to the evaluated left operand.
   * @param right non-null reference to the evaluated right operand.
   *
   * @return non-null reference to the XObject that represents the result of the operation.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject operate(XObject left, XObject right)
          throws javax.xml.transform.TransformerException
  {
    return null;  // no-op
  }

  /** 
   * @return the left operand of binary operation, as an Expression.
   */
  public Expression getLeftOperand(){
    return m_left;
  }

  /** 
   * @return the right operand of binary operation, as an Expression.
   */
  public Expression getRightOperand(){
    return m_right;
  }
  
  class LeftExprOwner implements ExpressionOwner
  {
    /**
     * @see ExpressionOwner#getExpression()
     */
    public Expression getExpression()
    {
      return m_left;
    }

    /**
     * @see ExpressionOwner#setExpression(Expression)
     */
    public void setExpression(Expression exp)
    {
    	exp.exprSetParent(Operation.this);
    	m_left = exp;
    }
  }

  /**
   * @see org.apache.xpath.XPathVisitable#callVisitors(ExpressionOwner, XPathVisitor)
   */
  public void callVisitors(ExpressionOwner owner, XPathVisitor visitor)
  {
  	if(visitor.visitBinaryOperation(owner, this))
  	{
  		m_left.callVisitors(new LeftExprOwner(), visitor);
  		m_right.callVisitors(this, visitor);
  	}
  }

  /**
   * @see ExpressionOwner#getExpression()
   */
  public Expression getExpression()
  {
    return m_right;
  }

  /**
   * @see ExpressionOwner#setExpression(Expression)
   */
  public void setExpression(Expression exp)
  {
  	exp.exprSetParent(this);
  	m_right = exp;
  }

  /**
   * @see Expression#deepEquals(Expression)
   */
  public boolean deepEquals(Expression expr)
  {
  	if(!isSameClass(expr))
  		return false;
  		
  	if(!m_left.deepEquals(((Operation)expr).m_left))
  		return false;
  		
  	if(!m_right.deepEquals(((Operation)expr).m_right))
  		return false;
  		
  	return true;
  }
  
  /**
   * Method definition, to get a modified XPath expression operand value,
   * if an XPath operand expression is a SelfIteratorNoPredicate iterator. 
   * 
   * @param opValue						        The supplied XPath expression operand value.
   *                                            This could be value of either LHS or RHS 
   *                                            operand of an XPath binary operator.
   * @param selfIteratorNoPredicate				SelfIteratorNoPredicate iterator
   * @return									The modified or an existing XPath expression 
   *                                            operand value.
   */
  protected XObject getModifiedOperandValue(XObject opValue, SelfIteratorNoPredicate selfIteratorNoPredicate) {
	  
	  XObject result = opValue;

	  ExpressionNode exprNode = selfIteratorNoPredicate.getExpressionOwner();
	  XObject contextItem = getXPath3ContextItem(exprNode);
	  if (contextItem != null) {
		  result = contextItem;  
	  }

	  return result;
  }
  
  /**
   * Method definition, to get an XPath context item, given 
   * a supplied non-expression XSL stylesheet node.
   * 
   * Usually, XPath context has reference to context node's
   * integer handle. For XPath expression ".", its possible that
   * a compiled XPath context item is available within XPath context,
   * which can be retrieved via this method definition.
   * 
   * @param exprNode			Non-expression stylesheet node
   * @return					A context item if available within XPath context
   */
  private XObject getXPath3ContextItem(ExpressionNode exprNode) {
	
	  XObject result = null;

	  ExpressionNode stylesheetRootNode = null;
	  while (exprNode != null) {
		  stylesheetRootNode = exprNode;
		  exprNode = exprNode.exprGetParent();                     
	  }

	  StylesheetRoot stylesheetRoot = (StylesheetRoot)stylesheetRootNode;
	  TransformerImpl transformerImpl = stylesheetRoot.getTransformerImpl();
	  XPathContext xpathContext = transformerImpl.getXPathContext();
	  result = xpathContext.getXPath3ContextItem();

	  return result;
  }
  
}
