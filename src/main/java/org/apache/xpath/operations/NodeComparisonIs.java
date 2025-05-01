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
package org.apache.xpath.operations;

import org.apache.xml.dtm.DTM;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;

/**
 * The XPath 3.1 node comparison "is" operation.
 * 
 * Ref : https://www.w3.org/TR/xpath-31/#id-node-comparisons
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class NodeComparisonIs extends Operation
{
    
    private static final long serialVersionUID = 7254558212157001252L;
   
    /**
    * Apply the operation to two operands, and return the result.
    *
    *
    * @param left non-null reference to the evaluated left operand.
    * @param right non-null reference to the evaluated right operand.
    *
    * @return non-null reference to the XObject that represents the result of the operation.
    *
    * @throws javax.xml.transform.TransformerException
    */
   public XObject operate(XObject left, XObject right) throws javax.xml.transform.TransformerException
   {
       XObject result = null;
       
       XMLNodeCursorImpl lNodeSet = null;
       XMLNodeCursorImpl rNodeSet = null;
       
       if (left instanceof XMLNodeCursorImpl) {
          lNodeSet = (XMLNodeCursorImpl)left;
          if (lNodeSet.getLength() > 1) {
             throw new javax.xml.transform.TransformerException("XPTY0004 : A sequence of more than one item is not "
                                                                                  + "allowed as the first operand of an operator 'is'.");   
          }
       }
       else {
          throw new javax.xml.transform.TransformerException("XPTY0004 : The supplied item type of first "
                                                                                  + "operand of an operator 'is' is not node().");    
       }
       
       if (right instanceof XMLNodeCursorImpl) {
          rNodeSet = (XMLNodeCursorImpl)right;
          if (rNodeSet.getLength() > 1) {
             throw new javax.xml.transform.TransformerException("XPTY0004 : A sequence of more than one item is not "
                                                                                  + "allowed as the second operand of an operator 'is'.");   
          }  
       }
       else {
          throw new javax.xml.transform.TransformerException("XPTY0004 : The supplied item type of second "
                                                                                   + "operand of an operator 'is' is not node()."); 
       }
       
       int lNodeHandle = lNodeSet.nextNode();
       int rNodeHandle = rNodeSet.nextNode();
       
       if ((lNodeHandle != DTM.NULL) && (rNodeHandle != DTM.NULL)) {
          result = (lNodeHandle == rNodeHandle) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
       }
       else {
          // As per XPath 3.1 spec for the node comparison "is" operator, if either operand 
          // is an empty sequence the result of the comparison is an empty sequence.
          result = new ResultSequence();  
       }
       
       return result; 
   }

}
