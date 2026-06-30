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
package org.apache.xpath.functions.string;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FuncString;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.res.XPATHErrorResources;

import xml.xpath31.processor.types.XSString;

/**
 * Implementation of an XPath 3.1 function fn:string-join.
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
   * Evaluate the function. The function must return a valid object.
   * 
   * @param xctxt                           An XPath context object
   * @return                                A valid XObject
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
    
		XObject result = null;		
	    
	    SourceLocator srcLocator = xctxt.getSAXLocator();	    	    
	    
	    XObject xObjArg0 = getFunctionArgEffectiveValue(m_arg0, xctxt);
	    
	    ResultSequence arg0Seq = new ResultSequence();
	    
	    if (xObjArg0 instanceof ResultSequence) {
	    	ResultSequence rSeq = (ResultSequence)xObjArg0;
	    	int size1 = rSeq.size();
	    	for (int idx = 0; idx < size1; idx++) {
	    		arg0Seq.add(rSeq.item(idx));  
	    	}
	    }
	    else if (xObjArg0 instanceof XMLNodeCursorImpl) {
	    	XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObjArg0;
	    	int nextNode = DTM.NULL;
	    	DTMCursorIterator dtmCursorIter = xmlNodeCursorImpl.iter();
	    	while ((nextNode = dtmCursorIter.nextNode()) != DTM.NULL) {
	    	   XMLNodeCursorImpl xNodeSetItem = new XMLNodeCursorImpl(nextNode, xctxt);
	    	   arg0Seq.add(xNodeSetItem);
	    	}
	    }
	    else {
	    	arg0Seq.add(xObjArg0);
	    }
	    
	    String strJoinSeparator = null;
	    
	    if (m_arg1 == null) {
	       strJoinSeparator = "";   
	    }    
	    else {
	       XObject xObjArg1 = getFunctionArgEffectiveValue(m_arg1, xctxt);
	       
	       if ((xObjArg1 instanceof ResultSequence) && (((ResultSequence)xObjArg1).size() == 0)) {
	    	  throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function 'string-join' "
					    	  		                                                               + "second argument cannot be an empty "
					    	  		                                                               + "sequence.", srcLocator); 
	       }
	    	
	       FuncString funcStr = new FuncString();
	       funcStr.setArg0(m_arg1);
	       
	       XObject xObj1 = null;
	       try {
	          xObj1 = funcStr.execute(xctxt);	          
	          strJoinSeparator = XslTransformEvaluationHelper.getStrVal(xObj1);
	       }
	       catch (TransformerException ex) {
	    	  throw ex; 
	       }
	    }
	    
	    StringBuffer strBuff = new StringBuffer();
	    
	    int size1 = arg0Seq.size();
	    for (int idx = 0; idx < size1; idx++) {       
	       XObject xObj = arg0Seq.item(idx);       
	       String strValue = XslTransformEvaluationHelper.getStrVal(xObj);       
	       if (idx < (size1 - 1)) {
	          strBuff.append(strValue + strJoinSeparator);    
	       }
	       else {
	          strBuff.append(strValue);    
	       }
	    }
	    
	    result = new XSString(strBuff.toString());
	
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
