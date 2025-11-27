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
package org.apache.xpath.operations;

import java.util.Map;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.Constants;
import org.apache.xalan.templates.ElemCatch;
import org.apache.xalan.templates.ElemIterate;
import org.apache.xalan.templates.ElemIterateOnCompletion;
import org.apache.xalan.templates.ElemParam;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.ElemVariable;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformData;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.axes.PathComponent;
import org.apache.xpath.axes.WalkerFactory;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.res.XPATHErrorResources;

/**
 * The variable reference expression, evaluator.
 */
public class Variable extends Expression implements PathComponent
{
  
  static final long serialVersionUID = -4334975375609297049L;
  
  /**
   * Tell if fixupVariables was called.
   */
  private boolean m_fixUpWasCalled = false;

  /**
   * The qualified name of the variable.
   */
  protected QName m_qname;
  
  /**
   * The index of the variable, which is either an absolute index to a 
   * global, or, if higher than the globals area, must be adjusted by adding 
   * the offset to the current stack frame.
   */
  protected int m_index;
  
  /**
   * Set the index for the variable into the stack.  For advanced use only. You 
   * must know what you are doing to use this.
   * 
   * @param index a global or local index.
   */
  public void setIndex(int index)
  {
  	m_index = index;
  }
  
  /**
   * Set the index for the variable into the stack.  For advanced use only.
   * 
   * @return index a global or local index.
   */
  public int getIndex()
  {
  	return m_index;
  }
  
  /**
   * Set whether or not this is a global reference.  For advanced use only.
   * 
   * @param isGlobal true if this should be a global variable reference.
   */
  public void setIsGlobal(boolean isGlobal)
  {
  	m_isGlobal = isGlobal;
  }
  
  /**
   * Set the index for the variable into the stack.  For advanced use only.
   * 
   * @return true if this should be a global variable reference.
   */
  public boolean getGlobal()
  {
  	return m_isGlobal;
  }
  
  protected boolean m_isGlobal = false;
  
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
	  m_fixUpWasCalled = true;

	  for (int i = vars.size()-1; i >= 0; i--) 
	  {
		  QName qn = (QName)vars.elementAt(i);
		  if (qn.equals(m_qname))
		  {

			  if(i < globalsSize)
			  {
				  m_isGlobal = true;
				  m_index = i;
			  }
			  else
			  {
				  m_index = i-globalsSize;
			  }

			  return;
		  }
	  }
	  
	  ExpressionNode exprOwnerNode = getExpressionOwner();
	  boolean isVariableRefInXslCatch = false;
	  if (exprOwnerNode instanceof ElemCatch) {
		  isVariableRefInXslCatch = true; 
	  }
	  else {
		  while (exprOwnerNode != null) {
			  exprOwnerNode = exprOwnerNode.exprGetParent();
			  if (exprOwnerNode instanceof ElemCatch) {
				  isVariableRefInXslCatch = true;
				  break;
			  }
		  }
	  }
	  
	  if (isVariableRefInXslCatch) {
		  java.lang.String varLocalName = m_qname.getLocalName();
		  java.lang.String nsUri = m_qname.getNamespaceURI();
		  if ((Constants.XSL_ERROR_NAMESACE).equals(nsUri)) {
			 if (Constants.XSL_ERROR_CODE.equals(varLocalName) || Constants.XSL_ERROR_DESCRIPTION.equals(varLocalName) || 
					                                              Constants.XSL_ERROR_LINE_NUMBER.equals(varLocalName) || 
					                                              Constants.XSL_ERROR_COLUMN_NUMBER.equals(varLocalName)) {
				 return; 
			 }
		  }
	  }

	  if (m_xpathVarList.contains(m_qname)) {
		  // This takes care of variable references within, XPath 3.1 expressions
		  // like function item, "for", "let", "quantified" expressions.
		  return;    
	  }
    
  }


  /**
   * Set the qualified name of the variable.
   *
   * @param qname Must be a non-null reference to a qualified name.
   */
  public void setQName(QName qname)
  {
    m_qname = qname;
  }
  
  /**
   * Get the qualified name of the variable.
   *
   * @return A non-null reference to a qualified name.
   */
  public QName getQName()
  {
    return m_qname;
  }
  
  /**
   * Execute an expression in the XPath runtime context, and return the
   * result of the expression.
   *
   *
   * @param xctxt The XPath runtime context.
   *
   * @return The result of the expression in the form of a <code>XObject</code>.
   *
   * @throws javax.xml.transform.TransformerException if a runtime exception
   *         occurs.
   */
  public XObject execute(XPathContext xctxt)
    throws javax.xml.transform.TransformerException
  {
  	return execute(xctxt, false);
  }


  /**
   * Method definition, to dereference a variable and return variable's value. 
   * Note that lazy evaluation will occur. If a variable within scope is 
   * not found, a warning shall be sent to Xalan-J's error listener.
   *
   * @param xctxt 								An XPath context object
   *
   * @return 									The evaluated variable
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt, boolean destructiveOK) throws javax.xml.transform.TransformerException
  {
      
        XObject result = null;
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        int sourceNode = xctxt.getCurrentNode();
        
        ExpressionNode expressionNode = this.getExpressionOwner();
		ExpressionNode stylesheetRootNode = null;
		while (expressionNode != null) {
			stylesheetRootNode = expressionNode;
			expressionNode = expressionNode.exprGetParent();                     
		}

		StylesheetRoot stylesheetRoot = null;

		if (stylesheetRootNode != null) {
			if (stylesheetRootNode instanceof Stylesheet) {
				Stylesheet stylesheet = (Stylesheet)stylesheetRootNode;

				stylesheetRoot = stylesheet.getStylesheetRoot();    				
			}    			
			else {
				stylesheetRoot = (StylesheetRoot)stylesheetRootNode;
			}
		}

		TransformerImpl transformerImpl = null; 
		if (stylesheetRoot != null) {
		    transformerImpl = stylesheetRoot.getTransformerImpl();  
		}
		
		if ((transformerImpl != null) && isVariableRefDescendantOfXslIterate()) {
			ElemTemplateElement elemTemplateElem = (ElemTemplateElement)(this.getExpressionOwner());
			ElemVariable elemVariable = getNearestPrecedingVarIter(elemTemplateElem, m_qname);
			if (elemVariable != null) {
			    result = elemVariable.getValue(transformerImpl, sourceNode);
			    
			    return result;
			}
		}
        
        Map<QName, XObject> xpathVarMap = xctxt.getXPathVarMap();
        XObject varValue = xpathVarMap.get(m_qname);
        
        if (varValue != null) {
        	if (varValue instanceof XMLNodeCursorImpl) {
        		result = ((XMLNodeCursorImpl)varValue).getFresh();    
        	}
        	else {
        		result = varValue;
        	}

        	return result;
        }
        
        try {
           if (m_fixUpWasCalled) {        	  
              if (m_isGlobal) {
                 result = xctxt.getVarStack().getGlobalVariable(xctxt, m_index, destructiveOK);
              }
              else {
                 result = xctxt.getVarStack().getLocalVariable(xctxt, m_index, destructiveOK);
              }
           } 
           else {  
              result = xctxt.getVarStack().getVariableOrParam(xctxt, m_qname);
           }
           
           ElemVariable elemVariable = this.getElemVariable();           
           ElemTemplateElement elemTemplateElement = (ElemTemplateElement)(getExpressionOwner());                      
           if ((elemVariable == null) && (elemTemplateElement instanceof ElemIterateOnCompletion)) {
        	   throw new javax.xml.transform.TransformerException("XPST0008 : Variable $" + m_qname.toString() + " "
             			                                                                  + "accessed before it is bound!", srcLocator); 
           }
           else {
               /**
                * If this XPath variable reference, is within xsl:catch instruction,
                * check whether there's an appropriate xsl:variable declaration 
                * available.           	   
                */
        	   ElemCatch elemCatch = null;
        	   while (elemTemplateElement != null) {
        		   if (elemTemplateElement instanceof ElemCatch) {
        			   elemCatch = (ElemCatch)elemTemplateElement;
        			   
        			   break;
        		   }
        		   else {
        			   elemTemplateElement = elemTemplateElement.getParentElem(); 
        		   }
        	   }
        	   
        	   if (elemCatch != null) {
        		  if ((elemVariable != null) && !isXslVariableDeclAvailableXslCatch((ElemTemplateElement)elemCatch)) {        			  
        			  throw new javax.xml.transform.TransformerException("XPST0008 : Variable $" + m_qname.toString() + "accessed before "
        			  		                                                                                                   + "it is bound!", srcLocator); 
        		  }
        	   }
           }
        }
        catch (javax.xml.transform.TransformerException ex) {
           java.lang.String exceptionMesg = ex.getMessage();
           QName errValueQname = new QName(Constants.XSL_ERROR_NAMESACE, Constants.XSL_ERROR_VALUE);
           if (m_qname.equals(errValueQname)) {
        	  result = new ResultSequence();  
           }
           else if ((m_qname == null) || ((exceptionMesg != null) && (exceptionMesg.startsWith("XTDE0050") || exceptionMesg.startsWith("XPTY") 
        		                                                                                      || exceptionMesg.startsWith("FOUT")
        		                                                                                      || exceptionMesg.startsWith("XPST0008")))) {
              throw ex;   
           }
           else if (XslTransformData.m_xpathNodeCombiningExprRhsStrBuff != null) {
        	   java.lang.String xpathExprStr = XslTransformData.m_xpathNodeCombiningExprRhsStrBuff.toString();        	   
        	   XPath xpath = new XPath(xpathExprStr, xctxt.getSAXLocator(), xctxt.getNamespaceContext(), 
        			                                                                                   XPath.SELECT, null);
        	   result = xpath.execute(xctxt, xctxt.getCurrentNode(), xctxt.getNamespaceContext());
        	   
        	   XslTransformData.m_xpathNodeCombiningExprRhsStrBuff = null;
           }           
           else {
        	  try {
        		  if (m_isGlobal) {
        			  result = xctxt.getVarStack().getGlobalVariable(xctxt, m_index, destructiveOK);
        		  }
        		  else {
        			  result = xctxt.getVarStack().getLocalVariable(xctxt, m_index, destructiveOK);
        		  }
        	  }
        	  catch (TransformerException ex1) {
                  throw new javax.xml.transform.TransformerException("Variable $" + m_qname.toString() + " "
                                                                                                  			+ "accessed before it is bound!", srcLocator);
        	  }
           }
        }
      
        if (result == null) {
        	warn(xctxt, XPATHErrorResources.WG_ILLEGAL_VARIABLE_REFERENCE, new Object[]{ m_qname.getLocalPart() });            
        	result = new XMLNodeCursorImpl(xctxt.getDTMManager());
        }
      
        return result;
  }

  /**
   * Get the XSLT ElemVariable that this sub-expression references.  In order for 
   * this to work, the SourceLocator must be the owning ElemTemplateElement.
   * @return The dereference to the ElemVariable, or null if not found.
   */
  public org.apache.xalan.templates.ElemVariable getElemVariable()
  {
  	
    // Get the current ElemTemplateElement, and then walk backwards in 
    // document order, searching 
    // for an xsl:param element or xsl:variable element that matches our 
    // qname.  If we reach the top level, use the StylesheetRoot's composed
    // list of top level variables and parameters.
    
    org.apache.xalan.templates.ElemVariable vvar = null;	
    org.apache.xpath.ExpressionNode owner = getExpressionOwner();

    if (null != owner && owner instanceof org.apache.xalan.templates.ElemTemplateElement)
    {

      org.apache.xalan.templates.ElemTemplateElement prev = 
        (org.apache.xalan.templates.ElemTemplateElement) owner;

      if (!(prev instanceof org.apache.xalan.templates.Stylesheet))
      {            
        while ( prev != null && !(prev.getParentNode() instanceof org.apache.xalan.templates.Stylesheet) )
        {
          org.apache.xalan.templates.ElemTemplateElement savedprev = prev;

          while (null != (prev = prev.getPreviousSiblingElem()))
          {
            if(prev instanceof org.apache.xalan.templates.ElemVariable)
            {
              vvar = (org.apache.xalan.templates.ElemVariable) prev;
            
              if (vvar.getName().equals(m_qname))
              {
                return vvar;
              }
              vvar = null; 	 	
            }
          }
          prev = savedprev.getParentElem();
        }
      }
      if (prev != null)
        vvar = prev.getStylesheetRoot().getVariableOrParamComposed(m_qname);
    }
    return vvar;

  }
  
  /**
   * Tell if this expression returns a stable number that will not change during 
   * iterations within the expression.  This is used to determine if a proximity 
   * position predicate can indicate that no more searching has to occur.
   * 
   *
   * @return true if the expression represents a stable number.
   */
  public boolean isStableNumber()
  {
    return true;
  }
  
  /** 
   * Get the analysis bits for this walker, as defined in the WalkerFactory.
   * @return One of WalkerFactory#BIT_DESCENDANT, etc.
   */
  public int getAnalysisBits()
  {
  	org.apache.xalan.templates.ElemVariable vvar = getElemVariable();
  	if(null != vvar)
  	{
  		XPath xpath = vvar.getSelect();
  		if(null != xpath)
  		{
	  		Expression expr = xpath.getExpression();
	  		if(null != expr && expr instanceof PathComponent)
	  		{
	  			return ((PathComponent)expr).getAnalysisBits();
	  		}
  		}
  	}
    return WalkerFactory.BIT_FILTER;
  }


  /**
   * @see org.apache.xpath.XPathVisitable#callVisitors(ExpressionOwner, XPathVisitor)
   */
  public void callVisitors(ExpressionOwner owner, XPathVisitor visitor)
  {
  	visitor.visitVariableRef(owner, this);
  }
  /**
   * @see Expression#deepEquals(Expression)
   */
  public boolean deepEquals(Expression expr)
  {
  	if(!isSameClass(expr))
  		return false;
  		
  	if(!m_qname.equals(((Variable)expr).m_qname))
  		return false;
  		
  	// We have to make sure that the qname really references 
  	// the same variable element.
    if(getElemVariable() != ((Variable)expr).getElemVariable())
    	return false;
  		
  	return true;
  }
  
  static final java.lang.String PSUEDOVARNAMESPACE = "http://xml.apache.org/xalan/psuedovar";
  
  /**
   * Tell if this is a psuedo variable reference, declared by Xalan instead 
   * of by the user.
   */
  public boolean isPsuedoVarRef()
  {
  	java.lang.String ns = m_qname.getNamespaceURI();
  	if((null != ns) && ns.equals(PSUEDOVARNAMESPACE))
  	{
  		if(m_qname.getLocalName().startsWith("#"))
  			return true;
  	}
  	return false;
  }
  
  /**
   * Method definition, to get nearest preceding (along XSL preceding sibling 
   * and ancestor instruction directions) xsl:variable instruction reference, 
   * with respect to the supplied XSL variable reference expression.
   * 
   * This method definition is used, when the XSL variable reference is 
   * descendant of xsl:iterate instruction.
   * 
   * @param elemTemplateElem						An XSL instruction reference within the 
   *                                                stylesheet, which is the starting point to 
   *                                                make this XSL stylesheet search.
   * @param qName									An XML qualified name of XSL stylesheet variable,
   *                                                that needs to be searched within stylesheet.
   * @return
   */
  private ElemVariable getNearestPrecedingVarIter(ElemTemplateElement elemTemplateElem, QName qName) {
	  
	  ElemVariable result = null;

	  if (!(elemTemplateElem instanceof ElemIterate)) {
		  ElemTemplateElement elemTemplateElem2 = elemTemplateElem.getPreviousSiblingElem();
		  if (elemTemplateElem2 == null) {
			  elemTemplateElem2 = elemTemplateElem.getParentElem(); 
		  }

		  while ((elemTemplateElem2 != null) && !(elemTemplateElem2 instanceof ElemIterate)) {
			  if ((elemTemplateElem2 instanceof ElemVariable) && !(elemTemplateElem2 instanceof ElemParam)) {
				  ElemVariable elemVariable = (ElemVariable)elemTemplateElem2;
				  if ((elemVariable.getName()).equals(qName)) {
					  result = elemVariable;

					  break;
				  }
			  }

			  if (elemTemplateElem2.getPreviousSiblingElem() != null) {
				  elemTemplateElem2 = elemTemplateElem2.getPreviousSiblingElem();
			  }
			  else {
				  elemTemplateElem2 = elemTemplateElem2.getParentElem();
			  }
		  }
	  }

	  return result; 
  }

  /**
   * Method definition, to check whether XSL variable reference expression 
   * is descendant of xsl:iterate instruction.
   * 
   * @return                            Boolean value true, if XSL variable reference 
   *                                    expression is descendant of xsl:iterate 
   *                                    instruction, otherwise false. 
   */
  private boolean isVariableRefDescendantOfXslIterate() {		
	
	  boolean result = false;

	  ElemTemplateElement elemTemplateElem = (ElemTemplateElement)(this.getExpressionOwner());
	  elemTemplateElem = elemTemplateElem.getParentElem();
	  while (elemTemplateElem != null) {
		  if (elemTemplateElem instanceof ElemIterate) {
			  result = true;
			  
			  break;
		  }
		  else {
			  elemTemplateElem = elemTemplateElem.getParentElem();
		  }
	  }

	  return result;
  }
  
 /**
  * Method definition to check whether, this XSL variable reference object
  * has an appropriate xsl:variable declaration available.
  * 
  * @param elemTemplateElement           An xsl:catch instruction reference provided 
  *                                      as an initial argument to this method. This
  *                                      ElemTemplateElement object reference changes
  *                                      with recursive calls to this method.
  * @return								 Boolean value true or false
  */
 private boolean isXslVariableDeclAvailableXslCatch(ElemTemplateElement elemTemplateElement) {	  			
	  
	  boolean result = false;
	  
	  ElemTemplateElement xslCatchElem = elemTemplateElement;
	  ElemTemplateElement xslExprOwnerElem = (ElemTemplateElement)(getExpressionOwner());
	  
	  if (!(xslExprOwnerElem instanceof ElemCatch)) {		  
		  elemTemplateElement = xslExprOwnerElem.getPreviousSiblingElem();
		  while (elemTemplateElement != null) {
			  if (elemTemplateElement instanceof ElemVariable) {
				  ElemVariable elemVariable = (ElemVariable)elemTemplateElement;
				  QName varName1 = elemVariable.getName();
				  if (varName1.equals(this.m_qname)) {
					  return true; 
				  }
			  }

			  elemTemplateElement = elemTemplateElement.getPreviousSiblingElem();
		  }
     }

	  ElemTemplateElement elemTry = xslCatchElem.getParentElem();

	  result = isXslVariableDeclAvailableXslTry(elemTry);      	    	  

	  return result;
 }
 
 /**
  * Method definition to check whether, this XSL variable reference object
  * has an appropriate xsl:variable declaration available.
  * 
  * @param elemTemplateElement           An xsl:try instruction reference provided 
  *                                      as an initial argument to this method. This
  *                                      ElemTemplateElement object reference changes
  *                                      with recursive calls to this method.
  * @return								 Boolean value true or false
  */
 private boolean isXslVariableDeclAvailableXslTry(ElemTemplateElement elemTemplateElement) {
	  
	  boolean result = false;

	  ElemTemplateElement xslTryElem = elemTemplateElement;
	  elemTemplateElement = elemTemplateElement.getPreviousSiblingElem();
	  while (elemTemplateElement != null) {
		  if (elemTemplateElement instanceof ElemVariable) {
			  ElemVariable elemVariable = (ElemVariable)elemTemplateElement;
			  QName varName1 = elemVariable.getName();
			  if (varName1.equals(this.m_qname)) {
				  return true; 
			  }
		  }
		  
		  elemTemplateElement = elemTemplateElement.getPreviousSiblingElem();
	  }
	  
	  ElemTemplateElement xslParentElem = xslTryElem.getParentElem();
	  if (xslParentElem != null) {
	     result = isXslVariableDeclAvailableXslTry(xslParentElem);
	  }

	  return result;
 }

}
