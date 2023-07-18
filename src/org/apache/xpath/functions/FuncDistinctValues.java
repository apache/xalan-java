/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
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

import javax.xml.transform.SourceLocator;

import org.apache.xalan.res.XSLMessages;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xpath.xs.types.XSAnyType;
import org.apache.xpath.xs.types.XSUntyped;
import org.apache.xpath.xs.types.XSUntypedAtomic;

/**
 * Execute the distinct-values() function.
 * 
 * XPath 3.1 F&O spec, defines the function fn:distinct-values as follows,
 *  
 * This function returns the values that appear in a sequence, with 
 * duplicates eliminated.
 * 
 * Function signatures :
 *      1) fn:distinct-values($arg as xs:anyAtomicType*) as xs:anyAtomicType*

        2) fn:distinct-values($arg as xs:anyAtomicType*, $collation as xs:string) 
                                                                            as xs:anyAtomicType*
                                                                                                                                                        
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncDistinctValues extends Function2Args {
    
   private static final long serialVersionUID = -1637800188441824456L;
    
   private static final String FUNCTION_NAME = "distinct-values()"; 

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
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        ResultSequence resultSeq = new ResultSequence();
        
        Expression arg0 = getArg0();        
        Expression arg1 = getArg1();   // the second argument of this function, is unused for now
        
        XObject arg0Obj = arg0.execute(xctxt);
        
        if (arg0Obj instanceof XNodeSet) {
           DTMManager dtmMgr = (DTMManager)xctxt;
           
           int currentNode = xctxt.getCurrentNode();
           DTMIterator sourceNodes = arg0.asIterator(xctxt, currentNode);
           
           int nextNodeDtmHandle;
           
           while ((nextNodeDtmHandle = sourceNodes.nextNode()) != DTM.NULL) {
              XNodeSet xNodeSet = new XNodeSet(nextNodeDtmHandle, xctxt);
              String nodeStrValue = xNodeSet.str();
              
              DTM dtm = dtmMgr.getDTM(nextNodeDtmHandle);
              
              if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ELEMENT_NODE) {
                 XSUntyped xsUntyped = new XSUntyped(nodeStrValue);                 
                 addItemToResultSequence(resultSeq, xsUntyped, true);
              }
              else if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ATTRIBUTE_NODE) {
                 XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
                 addItemToResultSequence(resultSeq, xsUntypedAtomic, true);
              }
              else {
                 XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
                 addItemToResultSequence(resultSeq, xsUntypedAtomic, true);
              }
           }
        }
        else if (arg0Obj instanceof ResultSequence) {
           ResultSequence inpResultSeq = (ResultSequence)arg0Obj; 
           for (int idx = 0; idx < inpResultSeq.size(); idx++) {
              XObject xObj = inpResultSeq.item(idx);
              if (xObj instanceof XSAnyType) {
                 XSAnyType xsAnyType = (XSAnyType)xObj;
                 addItemToResultSequence(resultSeq, xsAnyType, true);
              }
              else {
                 addItemToResultSequence(resultSeq, xObj, true);
              }
           }
        }
        else {
           // we're assuming here that, an input value is an 
           // singleton xdm item.            
           if (arg0Obj instanceof XSAnyType) {
              XSAnyType xsAnyType = (XSAnyType)arg0Obj;
              addItemToResultSequence(resultSeq, xsAnyType, false);
           }
           else {
              String seqItemStrValue = arg0Obj.str();
              addItemToResultSequence(resultSeq, new XString(seqItemStrValue),
                                                                         false);
           }
        }
            
        return resultSeq;
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
     if (!(argNum == 1 || argNum == 2)) {
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
  
  /**
   * Add an xdm input item to result sequence, if that already doesn't exist within
   * the result sequence. 
   */
  private void addItemToResultSequence(ResultSequence resultSeq, XObject inpItem, 
                                                                     boolean cardinalityCheck) {
      if (cardinalityCheck) {
          if (resultSeq.size() == 0) {                     
              resultSeq.add(inpItem);    
          }
          else if (!contains(resultSeq, inpItem)) {
              resultSeq.add(inpItem);
          }   
      }
      else {
          resultSeq.add(inpItem);   
      }
  }
  
  /**
   * Check whether a 'ResultSequence' object, contains a specific xdm item.
   */
  private boolean contains(ResultSequence resultSeq, XObject srch) {
     
     boolean isSeqContains = false;
     
     for (int idx = 0; idx < resultSeq.size(); idx++) {
        XObject existingItemWithinResultSeq = resultSeq.item(idx);
        if ((existingItemWithinResultSeq instanceof XSUntyped) && 
                                                          (srch instanceof XSUntyped)) {
           if (((XSUntyped)existingItemWithinResultSeq).equals((XSUntyped)srch)) {
               isSeqContains = true;
               break;    
           }
        }
        else if ((existingItemWithinResultSeq instanceof XSUntypedAtomic) && 
                                                                (srch instanceof XSUntypedAtomic)) {
            if (((XSUntypedAtomic)existingItemWithinResultSeq).equals((XSUntypedAtomic)srch)) {
               isSeqContains = true;
               break;    
            } 
        }
        else if ((existingItemWithinResultSeq instanceof XSUntyped) && 
                                                              (srch instanceof XSUntypedAtomic)) {
            if (((XSUntyped)existingItemWithinResultSeq).equals((XSUntypedAtomic)srch)) {
               isSeqContains = true;
               break;    
            } 
        }
        else if ((existingItemWithinResultSeq instanceof XSUntypedAtomic) && 
                                                              (srch instanceof XSUntyped)) {
            if (((XSUntypedAtomic)existingItemWithinResultSeq).equals((XSUntyped)srch)) {
               isSeqContains = true;
               break;    
            }
        }
        else if ((existingItemWithinResultSeq instanceof XSAnyType) && 
                                                               (srch instanceof XSAnyType)) {
            if (((XSAnyType)existingItemWithinResultSeq).equals((XSAnyType)srch)) {
                isSeqContains = true;
                break;    
            }   
        }
        else if (existingItemWithinResultSeq.equals(srch)) {
            isSeqContains = true;
            break;    
        }
     }
     
     return isSeqContains;
     
  }

}
