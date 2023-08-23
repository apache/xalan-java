/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xalan.xslt.util;

import java.util.List;

import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.xs.types.XSAnyType;
import org.apache.xpath.xs.types.XSUntyped;
import org.apache.xpath.xs.types.XSUntypedAtomic;

import com.sun.org.apache.xml.internal.dtm.DTM;

/**
 * This class, has few utility methods, to help with certain 
 * XalanJ XSLT transformation implementation tasks.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XslTransformEvaluationHelper {
    
    /**
     * Given an xdm input sequence, expand that sequence to produce a new sequence
     * none of whose items are sequence with cardinality greater than one.
     * 
     * The caller of this method, needs to pass an xdm sequence to be expanded
     * as an argument, and another argument reference to get the result from this 
     * method.
     */
    public static void expandResultSequence(ResultSequence seqToBeExpanded, 
                                                                  ResultSequence finalResultSeq) {               
        for (int idx = 0; idx < seqToBeExpanded.size(); idx++) {
          XObject xObject = seqToBeExpanded.item(idx);
          if (xObject instanceof ResultSequence) {
             expandResultSequence((ResultSequence)xObject, finalResultSeq); 
          }
          else {
             finalResultSeq.add(xObject);
          }
       }
    }
    
    /**
     * Given an XPath expression string, replace XML namespace uri references within it,
     * with the corresponding declared XML namespace prefixes, using information from
     * the list object 'nsPrefixTable' passed to this method.
     */
    public static String replaceNsUrisWithPrefixesOnXPathStr(String xpathExprStr, 
                                                                          List<XMLNSDecl> nsPrefixTable) {
       String replacedXPathExprStr = xpathExprStr;
       
       for (int idx = 0; idx < nsPrefixTable.size(); idx++) {
          XMLNSDecl xmlNSDecl = nsPrefixTable.get(idx);
          String prefix = xmlNSDecl.getPrefix();
          String uri = xmlNSDecl.getURI();
          replacedXPathExprStr = replacedXPathExprStr.replace(uri + ":", prefix + ":");
       }
       
       return replacedXPathExprStr; 
    }
    
    /**
     * Given an XObject object reference, return the string value 
     * of the object. 
     */
    public static String getStrVal(XObject xObj) {
       String strVal = null;
       
       if (xObj instanceof XSAnyType) {
          strVal = ((XSAnyType)xObj).stringValue();    
       }
       else {
          strVal = xObj.str();  
       }
       
       return strVal;
    }
    
    /**
     * Add an xdm input item to result sequence, if that already doesn't exist within
     * the result sequence. 
     */
    public static void addItemToResultSequence(ResultSequence resultSeq, XObject inpItem, 
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
     * Given an XObject object instance, get its contents as a ResultSequence object.  
     */
    public static ResultSequence getResultSequenceFromXObject(XObject xObject, XPathContext xctxt) {        
        
        ResultSequence resultSeq = new ResultSequence();
        
        if (xObject instanceof XNodeSet) {
           XNodeSet nodeSet = (XNodeSet)xObject;
            
           DTMIterator dtmIter = nodeSet.iterRaw();
           int nextNode;
           while ((nextNode = dtmIter.nextNode()) != DTM.NULL) {
              XNodeSet xdmNode = new XNodeSet(nextNode, xctxt);
              resultSeq.add(xdmNode);
           }
        }
        else if (xObject instanceof ResultSequence) {
           ResultSequence rSeq = (ResultSequence)xObject;
           
           for (int idx = 0; idx < rSeq.size(); idx++) {
              resultSeq.add(rSeq.item(idx)); 
           }
        }
        else {
           resultSeq.add(xObject);
        }
        
        return resultSeq;
    }
    
    /**
     * Check whether a 'ResultSequence' object, contains a specific xdm item.
     */
    private static boolean contains(ResultSequence resultSeq, XObject srch) {
       
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
