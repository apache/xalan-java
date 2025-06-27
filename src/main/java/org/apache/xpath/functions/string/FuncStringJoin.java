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
package org.apache.xpath.functions.string;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.operations.Operation;
import org.apache.xpath.operations.Range;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.res.XPATHErrorResources;

import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSUntyped;
import xml.xpath31.processor.types.XSUntypedAtomic;

/**
 * Implementation of an XPath 3.1 string-join function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncStringJoin extends Function2Args {

   private static final long serialVersionUID = 4171534319684252331L;
   
   /**
	* Class constructor.
	*/
   public FuncStringJoin() {
	   m_defined_arity = new Short[] { 1, 2 };
   }

   /**
   * Implementation of the function.
   * 
   * @param xctxt The current execution context.
   * 
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
    
		XObject result = null;		
	    
	    SourceLocator srcLocator = xctxt.getSAXLocator();	    	    
	    
	    ResultSequence arg0ResultSeq = null;
	    
	    if ((m_arg0 instanceof Function) || (m_arg0 instanceof Variable) || 
	    		                            (m_arg0 instanceof Range) || 
	    		                            (m_arg0 instanceof Operation)) {
	        XObject evalResult = m_arg0.execute(xctxt);
	        if (evalResult instanceof ResultSequence) {
	           arg0ResultSeq = (ResultSequence)evalResult;   
	        }                
	    }    
	    else if (m_arg0 instanceof LocPathIterator) {
	        arg0ResultSeq = new ResultSequence();
	        	        
	        final int contextNode = xctxt.getCurrentNode();
	        DTMCursorIterator arg0DtmIterator = m_arg0.asIterator(xctxt, contextNode);        
	        
	        int nodeDtmHandle;	        
	        DTMManager dtmMgr = (DTMManager)xctxt;
	        
	        while ((nodeDtmHandle = arg0DtmIterator.nextNode()) != DTM.NULL) {
	            XMLNodeCursorImpl xNodeSetItem = new XMLNodeCursorImpl(nodeDtmHandle, dtmMgr);            
	            String nodeStrValue = xNodeSetItem.str();
	            
	            DTM dtm = dtmMgr.getDTM(nodeDtmHandle);
	            
	            if (dtm.getNodeType(nodeDtmHandle) == DTM.ELEMENT_NODE) {
	               XSUntyped xsUntyped = new XSUntyped(nodeStrValue);
	               arg0ResultSeq.add(xsUntyped);
	            }
	            else if (dtm.getNodeType(nodeDtmHandle) == DTM.ATTRIBUTE_NODE) {
	               XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
	               arg0ResultSeq.add(xsUntypedAtomic);
	            }
	            else {
	               XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
	               arg0ResultSeq.add(xsUntypedAtomic);
	            }                        
	        }
	    }
	    else if (m_arg0 instanceof ResultSequence) {
	    	arg0ResultSeq = (ResultSequence)m_arg0;
	    }
	    else {
	    	XObject evalResult = m_arg0.execute(xctxt);
	    	if (evalResult instanceof ResultSequence) {
	    		arg0ResultSeq = new ResultSequence();
	    		ResultSequence resultSeq = (ResultSequence)evalResult;
	    		for (int idx = 0; idx < resultSeq.size(); idx++) {
	    			arg0ResultSeq.add(resultSeq.item(idx));  
	    		}
	    	}
	    }
	    
	    if (arg0ResultSeq == null) {
	        throw new javax.xml.transform.TransformerException("The 1st argument of function call fn:string-join, did "
	        		                                            + "not evaluate to a sequence.", srcLocator);    
	    }
	    
	    String strJoinSeparator = null;
	    
	    if (m_arg1 == null) {
	       strJoinSeparator = "";   
	    }    
	    else if (m_arg1 instanceof XString) {
	       strJoinSeparator = ((XString)m_arg1).str();
	    }
	    else {
	       throw new javax.xml.transform.TransformerException("The 2nd argument of function call fn:string-join must be "
	       		                                               + "absent, or it must be a string value.", srcLocator);
	    }
	    
	    StringBuffer strBuffer = new StringBuffer();
	    
	    for (int idx = 0; idx < arg0ResultSeq.size(); idx++) {       
	       XObject xObject = arg0ResultSeq.item(idx);       
	       String strValue = XslTransformEvaluationHelper.getStrVal(xObject);       
	       if (idx < (arg0ResultSeq.size() - 1)) {
	          strBuffer.append(strValue + strJoinSeparator);    
	       }
	       else {
	          strBuffer.append(strValue);    
	       }
	    }
	    
	    result = new XSString(strBuffer.toString());
	
	    return result;
  }

  /**
   * Check that the number of arguments passed to this function is correct.
   *
   * @param argNum The number of arguments that is being passed to the function.
   *
   * @throws WrongNumberArgsException
   */
  public void checkNumberArgs(int argNum) throws WrongNumberArgsException
  {
     if (argNum < 1 || argNum > 2) {
        reportWrongNumberArgs();
     }
  }

  /**
   * Constructs and throws a WrongNumberArgException with the appropriate
   * message for this function object.
   *
   * @throws WrongNumberArgsException
   */
  protected void reportWrongNumberArgs() throws WrongNumberArgsException {
      throw new WrongNumberArgsException(XSLMessages.createXPATHMessage(
                                                XPATHErrorResources.ER_ONE_OR_TWO, null)); //"1 or 2"
  }
  
}
