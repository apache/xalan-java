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
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.res.XPATHErrorResources;

import xml.xpath31.processor.types.XSDate;
import xml.xpath31.processor.types.XSDayTimeDuration;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSUntyped;
import xml.xpath31.processor.types.XSUntypedAtomic;
import xml.xpath31.processor.types.XSYearMonthDuration;

/**
 * Implementation of an XPath 3.1 function fn:min.
 * 
 * Ref : https://www.w3.org/TR/xpath-functions-31/#func-min
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncMin extends FunctionMultiArgs
{
    
  private static final long serialVersionUID = 3366369283941946933L;
  
  /**
   * The number of arguments passed to the fn:min function 
   * call.
   */
  private int numOfArgs = 0;

  /**
   * Execute the function. The function must return
   * a valid object.
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {          
      XObject result = null;
      
      SourceLocator srcLocator = xctxt.getSAXLocator();
      
      XObject xObjArg0 = null;
      
      ResultSequence convetedInpSequence = new ResultSequence(); 
      
      if (m_arg0 instanceof Variable) {
         xObjArg0 = ((Variable)m_arg0).execute(xctxt);           
      }
      else {
         xObjArg0 = m_arg0.execute(xctxt); 
      }
      
      int doubleItemCount = 0;
      int strItemCount = 0;
      int dateItemCount = 0;
      int yearMonthDurationItemCount = 0;
      int dayTimeDurationItemCount = 0;
      
      if (xObjArg0 instanceof XNodeSet) {
         // If for all the nodes of an input sequence, the node's string
         // value can be cast to xs:double, we shall be able to process
         // such a nodeset successfully, to be able to find a numeric
         // minimum value from these nodes.
         XNodeSet xsObjNodeSet = (XNodeSet)xObjArg0;
         DTMIterator dtmIter = xsObjNodeSet.iterRaw();
         
         int nextNodeDtmHandle;
         
         while ((nextNodeDtmHandle = dtmIter.nextNode()) != DTM.NULL) {       
            XNodeSet xdmNode = new XNodeSet(nextNodeDtmHandle, xctxt);
            String nodeStrVal = xdmNode.str();
            try {
               convetedInpSequence.add(new XSDouble((Double.valueOf(nodeStrVal)).doubleValue()));
               doubleItemCount++;
            }
            catch (NumberFormatException ex) {
               throw new javax.xml.transform.TransformerException("FORG0006 : An xdm node's string value '" + 
                                                                        nodeStrVal + "' cannot be cast to xs:double.", srcLocator); 
            }
         }
      }
      else if (xObjArg0 instanceof ResultSequence) {
         ResultSequence resultSeq = (ResultSequence)xObjArg0;
         
         for (int idx = 0; idx < resultSeq.size(); idx++) {
            XObject seqObj = resultSeq.item(idx);
            if (seqObj instanceof XSUntypedAtomic) {
               convetedInpSequence.add(new XSDouble(((XSUntypedAtomic)seqObj).
                                                                        stringValue()));
               doubleItemCount++;
            }
            else if (seqObj instanceof XSUntyped) {
               convetedInpSequence.add(new XSDouble(((XSUntyped)seqObj).stringValue()));
               doubleItemCount++;
            }
            else if (seqObj instanceof XSString) {
               convetedInpSequence.add((XSString)seqObj);
               strItemCount++;
            }
            else if (seqObj instanceof XString) {
               String strValue = ((XString)seqObj).str();
               convetedInpSequence.add(new XSString(strValue));
               strItemCount++;
            }
            else if (seqObj instanceof XSNumericType) {
               String strVal = ((XSNumericType)seqObj).stringValue();
               convetedInpSequence.add(new XSDouble(strVal));
               doubleItemCount++;
            }
            else if (seqObj instanceof XNumber) {
               double doubleVal = ((XNumber)seqObj).num();
               convetedInpSequence.add(new XSDouble(doubleVal));
               doubleItemCount++;
            }
            else if (seqObj instanceof XSDate) {
               convetedInpSequence.add((XSDate)seqObj);
               dateItemCount++;
            }
            else if (seqObj instanceof XSYearMonthDuration) {
               convetedInpSequence.add((XSYearMonthDuration)seqObj);
               yearMonthDurationItemCount++;
            }
            else if (seqObj instanceof XSDayTimeDuration) {
               convetedInpSequence.add((XSDayTimeDuration)seqObj);
               dayTimeDurationItemCount++;
            }
         }
      }
      else {
         result = xObjArg0;  
      }
      
      if (result == null) {
         if ((doubleItemCount > 0) && (convetedInpSequence.size() == doubleItemCount)) {
            result = getMinValueFromXSDoubleSequence(convetedInpSequence);
         }
         else if ((strItemCount > 0) && (convetedInpSequence.size() == strItemCount)) {
            String collationUri = getCollationUri(xctxt);
            result = getMinValueFromXSStringSequence(convetedInpSequence, collationUri, 
                                                                                    xctxt);
         }
         else if ((dateItemCount > 0) && (convetedInpSequence.size() == dateItemCount)) {
            result = getMinValueFromXSDateSequence(convetedInpSequence);
         }
         else if ((yearMonthDurationItemCount > 0) && (convetedInpSequence.size() == 
                                                                             yearMonthDurationItemCount)) {
            result = getMinValueFromXSYearMonthDurationSequence(convetedInpSequence);
         }
         else if ((dayTimeDurationItemCount > 0) && (convetedInpSequence.size() == 
                                                                             dayTimeDurationItemCount)) {
            result = getMinValueFromXSDayTimeDurationSequence(convetedInpSequence);
         }
         else if ((xObjArg0 instanceof ResultSequence) && (((ResultSequence)xObjArg0).
                                                                                  size() == 0)) {
            // the result value is an empty sequence
            result = new ResultSequence(); 
         }
         else {
            throw new javax.xml.transform.TransformerException("FORG0006 : An input sequence processed by function fn:min "
                                                                                      + "should have data values of same type for all the items of "
                                                                                      + "sequence (for e.g, all xs:double, all xs:string, all xs:date, "
                                                                                      + "all xs:yearMonthDuration, all xs:dayTimeDuration etc).", 
                                                                                      srcLocator);
         }
      }
      
      if (result == null) {
         // the result value is an empty sequence
         result = new ResultSequence(); 
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
      if (!((argNum == 1) || (argNum == 2))) {
         reportWrongNumberArgs();
      }
      else {
         numOfArgs = argNum;   
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
   * Given a sequence of xdm XSDouble objects, find the XSDouble object
   * that has minimum numeric value amongst the items of the provided 
   * sequence.  
   */
  private XSDouble getMinValueFromXSDoubleSequence(ResultSequence inpSeq) {
     XSDouble result = new XSDouble(Double.NEGATIVE_INFINITY);
     
     double minValue = ((XSDouble)(inpSeq.item(0))).doubleValue();
         
     for (int idx = 1; idx < inpSeq.size(); idx++) {
        double nextVal = ((XSDouble)(inpSeq.item(idx))).doubleValue();
        if (nextVal < minValue) {
           minValue = nextVal;   
        }
     }
         
     result = new XSDouble(minValue);
     
     return result;
  }
  
  /**
   * Given a sequence of xdm XSString objects, find the XSString object
   * that is a minimum amongst the items of the provided sequence.   
   */
  private XSString getMinValueFromXSStringSequence(ResultSequence inpSeq, 
                                                            String collationUri, 
                                                            XPathContext xctxt) throws javax.xml.transform.TransformerException {
     XSString result = (XSString)(inpSeq.item(0));
     
     XPathCollationSupport xpathCollationSupport = xctxt.getXPathCollationSupport();
     
     for (int idx = 1; idx < inpSeq.size(); idx++) {
        XSString nextVal = (XSString)(inpSeq.item(idx));
        if (xpathCollationSupport.compareStringsUsingCollation(nextVal.stringValue(), result.stringValue(), 
                                                                                                      collationUri) == -1) {
           result = nextVal;  
        }
     }
     
     return result;
  }
  
  /**
   * Given a sequence of xdm XSDate objects, find the XSDate object
   * that represents the earliest date amongst the items of the 
   * provided sequence.  
   */
  private XSDate getMinValueFromXSDateSequence(ResultSequence inpSeq) {
     XSDate result = (XSDate)(inpSeq.item(0));
         
     for (int idx = 1; idx < inpSeq.size(); idx++) {
        XSDate nextVal = (XSDate)(inpSeq.item(idx));
        if (nextVal.lt(result)) {
           result = nextVal;   
        }
     }
     
     return result;
  }
  
  /**
   * Given a sequence of xdm XSYearMonthDuration objects, find the
   * XSYearMonthDuration object that represents the smallest value
   * amongst the items of the provided sequence.  
   */
  private XSYearMonthDuration getMinValueFromXSYearMonthDurationSequence(
                                                                     ResultSequence inpSeq) {
     XSYearMonthDuration result = (XSYearMonthDuration)(inpSeq.item(0));
         
     for (int idx = 1; idx < inpSeq.size(); idx++) {
        XSYearMonthDuration nextVal = (XSYearMonthDuration)(inpSeq.item(idx));
        if (nextVal.lt(result)) {
           result = nextVal;   
        }
     }
     
     return result;
  }
  
  /**
   * Given a sequence of xdm XSDayTimeDuration objects, find the
   * XSDayTimeDuration object that represents the smallest value
   * amongst the items of the provided sequence.  
   */
  private XSDayTimeDuration getMinValueFromXSDayTimeDurationSequence(
                                                                     ResultSequence inpSeq) {
     XSDayTimeDuration result = (XSDayTimeDuration)(inpSeq.item(0));
         
     for (int idx = 1; idx < inpSeq.size(); idx++) {
        XSDayTimeDuration nextVal = (XSDayTimeDuration)(inpSeq.item(idx));
        if (nextVal.lt(result)) {
           result = nextVal;   
        }
     }
     
     return result;
  }
  
  /**
   * Get the string comparison collation uri, for function call fn:min's evaluation.
   */
  private String getCollationUri(XPathContext xctxt) throws 
                                                        javax.xml.transform.TransformerException {
      
      String collationUri = xctxt.getDefaultCollation();
      
      if (m_arg1 != null) {
          XObject XObjArg1 = m_arg1.execute(xctxt);
               
          if ((XObjArg1 instanceof ResultSequence) && 
                                                    (((ResultSequence)XObjArg1).size() == 0)) {
             collationUri = xctxt.getDefaultCollation();   
          }
          else {
             // a collation uri was, explicitly provided during the function call fn:min                  
             collationUri = XslTransformEvaluationHelper.getStrVal(XObjArg1); 
          }
      }
      
      return collationUri;
  }
  
}
