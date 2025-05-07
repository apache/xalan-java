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
package org.apache.xpath.functions;

import javax.xml.transform.TransformerException;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.SubContextList;
import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

/**
 * Implementation of the XPath 3.1 function fn:position().
 * 
 * @xsl.usage advanced
 */
public class FuncPosition extends Function
{
  static final long serialVersionUID = -9092846348197271582L;
  
  private boolean m_isTopLevel;
  
  public static int m_forEachGroupGroupByPos = 0;
  
  /**
   * Figure out if we're executing a toplevel expression.
   * If so, we can't be inside of a predicate. 
   */
  public void postCompileStep(Compiler compiler)
  {
    m_isTopLevel = compiler.getLocationPathDepth() == -1;
  }

  /**
   * Get the position in the current context node list.
   *
   * @param xctxt Runtime XPath context.
   *
   * @return The current position of the iteration in the context node list, 
   *         or -1 if there is no active context node list.
 * @throws TransformerException 
   */
  public int getPositionInContextNodeList(XPathContext xctxt) throws TransformerException
  {
    
    if (xctxt.getXPath3ContextPosition() != -1) {
       return xctxt.getXPath3ContextPosition();
    }
    
    if (xctxt.getContextNode() == DTM.NULL) {
        throw new javax.xml.transform.TransformerException("XPDY0002 : The context item is absent "
                                                                  + "at this point, and therefore position() function "
                                                                  + "cannot be called.", xctxt.getSAXLocator());       
    }
    
    // If we're in a predicate, then this will return non-null.
    SubContextList iter = m_isTopLevel ? null : xctxt.getSubContextList();

    if (null != iter)
    {
      int prox = iter.getProximityPosition(xctxt);
 
      return prox;
    }

    DTMCursorIterator cnl = xctxt.getContextNodeList();

    if (null != cnl)
    {
      int n = cnl.getCurrentNode();
      if(n == DTM.NULL)
      {
        if(cnl.getCurrentPos() == 0)
          return 0;
          
        // Then I think we're in a sort.  See sort21.xsl. So the iterator has 
        // already been spent, and is not on the node we're processing. 
        // It's highly possible that this is an issue for other context-list 
        // functions.  Shouldn't be a problem for last(), and it shouldn't be 
        // a problem for current().
        try 
        { 
          cnl = cnl.cloneWithReset(); 
        }
        catch(CloneNotSupportedException cnse)
        {
          throw new org.apache.xml.utils.WrappedRuntimeException(cnse);
        }
        
        int currentNode = xctxt.getContextNode();
        while(DTM.NULL != (n = cnl.nextNode()))
        {
          if(n == currentNode)
            break;
        }
      }
      
      return cnl.getCurrentPos();
    }

    return -1;
  }

  /**
   * Execute the function. The function must return a valid object.
   * 
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {    	  
	  double pos = (xctxt.getPos() > 0) ? xctxt.getPos() : ((double) getPositionInContextNodeList(xctxt));
	  pos = (pos > 0) ? pos : m_forEachGroupGroupByPos;
    
      return new XNumber(pos);
  }
  
  /**
   * No arguments to process, so this does nothing.
   */
  public void fixupVariables(java.util.Vector vars, int globalsSize)
  {
    // no-op
  }
}
