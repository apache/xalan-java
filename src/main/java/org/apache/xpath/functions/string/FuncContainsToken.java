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

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionMultiArgs;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.res.XPATHErrorResources;

import xml.xpath31.processor.types.XSBoolean;

/**
 * Implementation of an XPath 3.1 function contains-token().
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncContainsToken extends FunctionMultiArgs {

   private static final long serialVersionUID = -7113398825278491809L;
   
   /**
    * Class constructor.
    */
   public FuncContainsToken() {
 	  m_defined_arity = new Short[] { 2, 3 };
   }

   /**
   * Execute the function. The function must return a valid object.
   * 
   * @param xctxt The current execution context.
   * 
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
   public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
   {      
        ResultSequence result = new ResultSequence();
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        Expression arg0 = getArg0();
        Expression arg1 = getArg1();
        Expression arg2 = getArg2();
        
        if ((arg0 == null) || (arg1 == null)) {
           // If first two mandatory arguments are not present within function 
           // call fn:contains-token, return an empty sequence. 
           return result; 
        }
        
        XObject arg0EvalResult = arg0.execute(xctxt);
        
        List<String> arg0StrList = new ArrayList<String>();
        
        if (arg0EvalResult instanceof XMLNodeCursorImpl) {
           XMLNodeCursorImpl nodeSet = (XMLNodeCursorImpl)arg0EvalResult;
           if (nodeSet.getLength() > 0) {
               DTMManager dtmMgr = (DTMManager)xctxt;                        
               DTMCursorIterator sourceNodes = nodeSet.iter();
               
               int nextNodeDtmHandle;
               while ((nextNodeDtmHandle = sourceNodes.nextNode()) != DTM.NULL) {
                  XMLNodeCursorImpl xNodeSetItem = new XMLNodeCursorImpl(nextNodeDtmHandle, dtmMgr);
                  String strVal = xNodeSetItem.str();
                  arg0StrList.add(strVal);
               }
           }
           else {
              result.add(new XSBoolean(false));
           }
        }
        else if (arg0EvalResult instanceof ResultSequence) {
           ResultSequence resultSeq = (ResultSequence)arg0EvalResult;
           if (resultSeq.size() > 0) {
              for (int idx = 0; idx < resultSeq.size(); idx++) {
                 XObject xObject = resultSeq.item(idx); 
                 String strVal = XslTransformEvaluationHelper.getStrVal(xObject);
                 arg0StrList.add(strVal);
              }
           }
           else {
              result.add(new XSBoolean(false)); 
           }
        }
        else {
           arg0StrList.add(arg0EvalResult.str()); 
        }        
        
        XObject arg1EvalResult = arg1.execute(xctxt);
        
        String tokenStrVal = XslTransformEvaluationHelper.getStrVal(arg1EvalResult);
        tokenStrVal = tokenStrVal.trim();
        
        if (tokenStrVal.length() > 0) {
           String collationUri = null;
           if (arg2 == null) {
              collationUri = xctxt.getDefaultCollation();  
           }
           else {
              XObject arg2EvalResult = arg2.execute(xctxt);
              collationUri = XslTransformEvaluationHelper.getStrVal(arg2EvalResult); 
           }
           
           XPathCollationSupport xpathCollationSupport = xctxt.getXPathCollationSupport();
           
           boolean isTokenExists = false;
           
           for (int idx1 = 0; idx1 < arg0StrList.size(); idx1++) {
              String strVal = arg0StrList.get(idx1);
              // Split this string at whitespace boundaries
              String[] strPartsArr = strVal.split("\\s+");
              for (int idx2 = 0; idx2 < strPartsArr.length; idx2++) {
                 String strPart = strPartsArr[idx2];
                 if (xpathCollationSupport.compareStringsUsingCollation(strPart, tokenStrVal, 
                                                                                       collationUri) == 0) {
                    result.add(new XSBoolean(true));
                    isTokenExists = true;
                    break;
                 }
              }
              
              if (isTokenExists) {
                 break; 
              }
           }
           
           if (!isTokenExists) {
              result.add(new XSBoolean(false)); 
           }
        }
        else {
           result.add(new XSBoolean(false)); 
        }
                    
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
     if (!(argNum == 2 || argNum == 3)) {
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
                                              XPATHErrorResources.ER_TWO_OR_THREE, null)); //"2 or 3"
  }
  

}
