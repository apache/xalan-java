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
package org.apache.xpath.functions;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.res.XPATHErrorResources;

import xml.xpath31.processor.types.XSString;

/**
 * Base class for functions that accept one argument that can be defaulted if
 * not specified.
 * @xsl.usage advanced
 */
public class FunctionDef1Arg extends FunctionOneArg
{
    static final long serialVersionUID = 2325189412814149264L;

  /**
   * Execute the first argument expression that is expected to return a
   * nodeset.  If the argument is null, then return the current context node.
   *
   * @param xctxt Runtime XPath context.
   *
   * @return The first node of the executed nodeset, or the current context
   *         node if the first argument is null.
   *
   * @throws javax.xml.transform.TransformerException if an error occurs while
   *                                   executing the argument expression.
   */
  protected int getArg0AsNode(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {

    return (null == m_arg0)
           ? xctxt.getCurrentNode() : m_arg0.asNode(xctxt);
  }
  
  /**
   * Tell if the expression is a nodeset expression.
   * @return true if the expression can be represented as a nodeset.
   */
  public boolean Arg0IsNodesetExpr()
  {
    return (null == m_arg0) ? true : m_arg0.isNodesetExpr();
  }

  /**
   * Execute the first argument expression that is expected to return a
   * string.  If the argument is null, then get the string value from the
   * current context node.
   *
   * @param xctxt Runtime XPath context.
   *
   * @return The string value of the first argument, or the string value of the
   *         current context node if the first argument is null.
   *
   * @throws javax.xml.transform.TransformerException if an error occurs while
   *                                   executing the argument expression.
   */
  protected XMLString getArg0AsString(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {
    XMLString resultVal = null;
      
    if (m_arg0 == null)
    {
      int currentNode = xctxt.getCurrentNode();
      if(DTM.NULL == currentNode) {
          resultVal = XString.EMPTYSTRING;
      }
      else
      {
          DTM dtm = xctxt.getDTM(currentNode);
          resultVal = dtm.getStringValue(currentNode);
      }      
    }
    else if (m_arg0 instanceof SelfIteratorNoPredicate) {
       XObject xpath3ContextItem = xctxt.getXPath3ContextItem();
       if (xpath3ContextItem != null) {
          resultVal = new XString(XslTransformEvaluationHelper.getStrVal(xpath3ContextItem));
       }
       else {
          XObject arg0XObject = m_arg0.execute(xctxt);
           
          resultVal = new XString(XslTransformEvaluationHelper.getStrVal(arg0XObject));
       }
    }
    else if (m_arg0 instanceof XSString) {
       String strVal = ((XSString)m_arg0).stringValue();
       resultVal = new XString(strVal);
    }
    else {
       XObject arg0XObject = m_arg0.execute(xctxt);
        
       resultVal = new XString(XslTransformEvaluationHelper.getStrVal(arg0XObject));  
    }
    
    return resultVal;
  }

  /**
   * Execute the first argument expression that is expected to return a
   * number.  If the argument is null, then get the number value from the
   * current context node.
   *
   * @param xctxt Runtime XPath context.
   *
   * @return The number value of the first argument, or the number value of the
   *         current context node if the first argument is null.
   *
   * @throws javax.xml.transform.TransformerException if an error occurs while
   *                                   executing the argument expression.
   */
  protected double getArg0AsNumber(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {

    if(null == m_arg0)
    {
      int currentNode = xctxt.getCurrentNode();
      if(DTM.NULL == currentNode)
        return 0;
      else
      {
        DTM dtm = xctxt.getDTM(currentNode);
        XMLString str = dtm.getStringValue(currentNode);
        return str.toDouble();
      }      
    }
    else if (m_arg0 instanceof SelfIteratorNoPredicate) {
    	XObject contextItem = xctxt.getXPath3ContextItem();
    	if (contextItem != null) {
    		String argStrVal = XslTransformEvaluationHelper.getStrVal(contextItem);
    		return (Double.valueOf(argStrVal)).doubleValue();
    	}
    	else {
    	   return m_arg0.execute(xctxt).num();
    	}
    }    	
    else {
       XObject xObj = m_arg0.execute(xctxt);
       if (xObj instanceof XSString) {
    	   XString xStr = new XString(((XSString)xObj).stringValue());
    	   return xStr.num();
       }
       else {
    	   return m_arg0.execute(xctxt).num();   
       }
    }
  }

  /**
   * Check that the number of arguments passed to this function is correct.
   *
   * @param argNum The number of arguments that is being passed to the function.
   *
   * @throws WrongNumberArgsException if the number of arguments is not 0 or 1.
   */
  public void checkNumberArgs(int argNum) throws WrongNumberArgsException
  {
    if (argNum > 1)
      reportWrongNumberArgs();
  }

  /**
   * Constructs and throws a WrongNumberArgException with the appropriate
   * message for this function object.
   *
   * @throws WrongNumberArgsException
   */
  protected void reportWrongNumberArgs() throws WrongNumberArgsException {
      throw new WrongNumberArgsException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_ZERO_OR_ONE, null)); //"0 or 1");
  }

  /**
   * Tell if this expression or it's subexpressions can traverse outside
   * the current subtree.
   *
   * @return true if traversal outside the context node's subtree can occur.
   */
  public boolean canTraverseOutsideSubtree()
  {
    return (null == m_arg0) ? false : super.canTraverseOutsideSubtree();
  }
}
