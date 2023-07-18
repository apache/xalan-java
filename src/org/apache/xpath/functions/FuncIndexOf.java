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

import java.math.BigInteger;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.res.XSLMessages;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xpath.xs.types.XSAnyType;
import org.apache.xpath.xs.types.XSInteger;
import org.apache.xpath.xs.types.XSUntyped;
import org.apache.xpath.xs.types.XSUntypedAtomic;

/**
 * Execute the index-of() function.
 * 
 * This function, returns a sequence of positive integers giving the positions 
 * within the sequence $seq of items that are equal to $search.
 * 
 * Function signatures :
 *      1) fn:index-of($seq         as xs:anyAtomicType*,
                       $search      as xs:anyAtomicType) as xs:integer*

        2) fn:index-of($seq         as xs:anyAtomicType*,
                       $search      as xs:anyAtomicType,
                       $collation   as xs:string) as xs:integer*
 *
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncIndexOf extends Function3Args {

   private static final long serialVersionUID = 2912594883291006421L;
   
   private static final String FUNCTION_NAME = "index-of()"; 

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
        
        ResultSequence arg0ResultSeq = null;
        
        final int contextNode = xctxt.getCurrentNode();
        
        Expression arg0 = getArg0();
        Expression arg1 = getArg1();
        
        DTMManager dtmMgr = (DTMManager)xctxt;
        
        if (arg0 instanceof LocPathIterator) {
            arg0ResultSeq = new ResultSequence();
                                
            DTMIterator arg0DtmIterator = m_arg0.asIterator(xctxt, contextNode);        
            
            int nextNodeDtmHandle;
            
            while ((nextNodeDtmHandle = arg0DtmIterator.nextNode()) != DTM.NULL) {
                XNodeSet xNodeSet = new XNodeSet(nextNodeDtmHandle, xctxt.getDTMManager());
                
                String nodeStrValue = xNodeSet.str();
                
                DTM dtm = dtmMgr.getDTM(nextNodeDtmHandle);
                
                if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ELEMENT_NODE) {
                   XSUntyped xsUntyped = new XSUntyped(nodeStrValue);
                   arg0ResultSeq.add(xsUntyped);
                }
                else if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ATTRIBUTE_NODE) {
                   XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
                   arg0ResultSeq.add(xsUntypedAtomic);
                }
                else {
                   XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
                   arg0ResultSeq.add(xsUntypedAtomic);
                }                        
            } 
        }
        else {
            XObject arg0Obj = arg0.execute(xctxt);
            if (arg0Obj instanceof ResultSequence) {
               arg0ResultSeq = (ResultSequence)arg0Obj;     
            }
        }

        if (arg0ResultSeq == null) {
           throw new javax.xml.transform.TransformerException("XPTY0004 : The first argument of fn:index-of didn't "
                                                                                        + "evaluate to a sequence.", srcLocator);     
        }
        
        XObject arg1Obj = arg1.execute(xctxt);
        
        if (arg1Obj instanceof XNodeSet) {
           XNodeSet xNodeSet = (XNodeSet)arg1Obj;           
           
           if (xNodeSet.getLength() == 1) {
              String nodeStrValue = xNodeSet.str();
               
              DTMIterator sourceNodes = arg0.asIterator(xctxt, contextNode);
              int dtmNodeHandle = sourceNodes.nextNode();
              
              DTM dtm = dtmMgr.getDTM(dtmNodeHandle);
               
              if (dtm.getNodeType(dtmNodeHandle) == DTM.ELEMENT_NODE) {
                 arg1Obj = new XSUntyped(nodeStrValue);
              }
              else if (dtm.getNodeType(dtmNodeHandle) == DTM.ATTRIBUTE_NODE) {
                 arg1Obj = new XSUntypedAtomic(nodeStrValue);
              }
              else {
                 arg1Obj = new XSUntypedAtomic(nodeStrValue);
              }    
           }
           else {                            
              throw new javax.xml.transform.TransformerException("XPTY0004 : the second argument of "
                                                                                   + "fn:index-of needs to be a sequence of size one.", 
                                                                                            srcLocator); 
           }
        }
        
        for (int idx = 0; idx < arg0ResultSeq.size(); idx++) {
           if (equals(arg0ResultSeq.item(idx), arg1Obj)) {
              resultSeq.add(new XSInteger(BigInteger.valueOf(idx + 1)));    
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
  
  /**
   * Check equality of two xdm items.
   */
  private boolean equals(XObject obj1, XObject obj2) {
      
      boolean isEquals = false;
      
      if ((obj1 instanceof XSUntyped) && (obj2 instanceof XSUntyped)) {
         if (((XSUntyped)obj1).equals((XSUntyped)obj2)) {
            isEquals = true;   
         }
      }
      else if ((obj1 instanceof XSUntypedAtomic) && (obj2 instanceof XSUntypedAtomic)) {
         if (((XSUntypedAtomic)obj1).equals((XSUntypedAtomic)obj2)) {
            isEquals = true;   
         }
      }
      else if ((obj1 instanceof XSUntyped) && (obj2 instanceof XSUntypedAtomic)) {
         if (((XSUntyped)obj1).equals((XSUntypedAtomic)obj2)) {
            isEquals = true;   
         } 
      }
      else if ((obj1 instanceof XSUntypedAtomic) && (obj2 instanceof XSUntyped)) {
         if (((XSUntypedAtomic)obj1).equals((XSUntyped)obj2)) {
            isEquals = true; 
         }
      }
      else if ((obj1 instanceof XSAnyType) && (obj2 instanceof XSAnyType)) {
         if (((XSAnyType)obj1).equals((XSAnyType)obj2)) {
            isEquals = true;  
         }   
      }
      else if (obj1.equals(obj2)) {
          isEquals = true;   
      }
      
      return isEquals; 
  }

}
