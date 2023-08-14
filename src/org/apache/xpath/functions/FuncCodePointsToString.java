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
package org.apache.xpath.functions;

import javax.xml.transform.SourceLocator;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.xs.types.XSNumericType;

/**
 * Implementation of the codepoints-to-string() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncCodePointsToString extends FunctionOneArg {

    private static final long serialVersionUID = -4531182517520672452L;

    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
    {
        XObject result = null;                                
        
        String resultStr = "";
        
        if (m_arg0 instanceof Variable) {
           XObject xObj = ((Variable)m_arg0).execute(xctxt);           
           resultStr = getStringFromXObject(xObj, xctxt);           
        }
        else {
           XObject xObj = m_arg0.execute(xctxt);
           resultStr = getStringFromXObject(xObj, xctxt);
        }
        
        result = new XString(resultStr);
        
        return result;
    }
    
    /*
     * Given an XObject object instance, convert that into a string, which is
     * the result of function invocation fn:codepoints-to-string.
     */
    private String getStringFromXObject(XObject xObj, XPathContext xctxt) 
                                                                 throws javax.xml.transform.TransformerException {
       
       String resultStr = "";
       
       SourceLocator srcLocator = xctxt.getSAXLocator();
       
       ResultSequence inpSeq = null;
       
       if (xObj instanceof ResultSequence) {
           inpSeq = (ResultSequence)xObj;
           for (int idx = 0; idx < inpSeq.size(); idx++) {
              XObject inpSeqObj = inpSeq.item(idx);
              if (inpSeqObj instanceof XNumber) {
                 XNumber xNum = (XNumber)inpSeqObj;
                 double dblVal = xNum.num();
                 if (dblVal == (int)dblVal) {
                    char[] charArr = Character.toChars((int)dblVal);
                    resultStr = resultStr + String.valueOf(charArr);
                 }
                 else {
                    throw new javax.xml.transform.TransformerException("FORG0006 : An input sequence item " + dblVal + " "
                                                                                                + "is not an xs:integer value, "
                                                                                                + "or cannot be cast to xs:integer.", srcLocator);    
                 }
              }
              else if (inpSeqObj instanceof XSNumericType) {
                 String itemStrVal = ((XSNumericType)inpSeqObj).stringValue();
                 double dblVal = (Double.valueOf(itemStrVal)).doubleValue();
                 if (dblVal == (int)dblVal) {
                    char[] charArr = Character.toChars((int)dblVal);
                    resultStr = resultStr + String.valueOf(charArr);  
                 }
                 else {
                    throw new javax.xml.transform.TransformerException("FORG0006 : An input sequence item " + dblVal + " "
                                                                                                  + "is not an xs:integer value, "
                                                                                                  + "or cannot be cast to xs:integer.", srcLocator);   
                 }
              }
              else if (inpSeqObj instanceof XNodeSet) {
                 XNodeSet inpSeqItem = (XNodeSet)inpSeqObj;
                 if (inpSeqItem.getLength() == 1) {
                    String itemStrVal = inpSeqItem.str();
                    double dblVal = (Double.valueOf(itemStrVal)).doubleValue();
                    if (dblVal == (int)dblVal) {
                       char[] charArr = Character.toChars((int)dblVal);
                       resultStr = resultStr + String.valueOf(charArr); 
                    }
                    else {
                       throw new javax.xml.transform.TransformerException("FORG0006 : An input sequence item " + dblVal + " "
                                                                                                       + "is not an xs:integer value, "
                                                                                                       + "or cannot be cast to xs:integer.", srcLocator);   
                    }
                 }
                 else {
                    throw new javax.xml.transform.TransformerException("FORG0006 : An input sequence item is a nodeset "
                                                                                                       + "with size not equal to one.", srcLocator); 
                 }
              }
              else {
                 String itemStrVal = inpSeqObj.str();
                 try {
                    double dblVal = (Double.valueOf(itemStrVal)).doubleValue();
                    if (dblVal == (int)dblVal) {
                       char[] charArr = Character.toChars((int)dblVal);
                       resultStr = resultStr + String.valueOf(charArr); 
                    }
                    else {
                       throw new javax.xml.transform.TransformerException("FORG0006 : An input sequence item " + dblVal + " "
                                                                                                           + "is not an xs:integer value, "
                                                                                                           + "or cannot be cast to xs:integer.", srcLocator);   
                    }
                 }
                 catch (NumberFormatException ex) {
                    throw new javax.xml.transform.TransformerException("FORG0006 : An input sequence item, cannot be "
                                                                                                + "cast to numeric value.", srcLocator);
                 }
              }
           }
        }
        else if (xObj instanceof XNodeSet) {
           DTMManager dtmMgr = (DTMManager)xctxt;
            
           XNodeSet xNodeSet = (XNodeSet)xObj;           
           DTMIterator sourceNodes = xNodeSet.iter();
            
           int nextNodeDtmHandle;
           
           while ((nextNodeDtmHandle = sourceNodes.nextNode()) != DTM.NULL) {
               XNodeSet xNodeSetItem = new XNodeSet(nextNodeDtmHandle, dtmMgr);
               String nodeStrValue = xNodeSetItem.str();
               
               double dblVal = (Double.valueOf(nodeStrValue)).doubleValue();
               if (dblVal == (int)dblVal) {
                  char[] charArr = Character.toChars((int)dblVal);
                  resultStr = resultStr + String.valueOf(charArr);  
               }
               else {
                  throw new javax.xml.transform.TransformerException("FORG0006 : An input sequence item " + dblVal + " "
                                                                                               + "is not an xs:integer value, "
                                                                                               + "or cannot be cast to xs:integer.", srcLocator);   
               }
           }
        }
        else {
           String xdmItemStrVal = xObj.str();
           
           double dblVal = (Double.valueOf(xdmItemStrVal)).doubleValue();
           if (dblVal == (int)dblVal) {
              char[] charArr = Character.toChars((int)dblVal);
              resultStr = String.valueOf(charArr);  
           }
           else {
              throw new javax.xml.transform.TransformerException("FORG0006 : An input sequence item " + dblVal + " "
                                                                                                  + "is not an xs:integer value, "
                                                                                                  + "or cannot be cast to xs:integer.", srcLocator);   
           }
        }
       
        return resultStr; 
    }

}
