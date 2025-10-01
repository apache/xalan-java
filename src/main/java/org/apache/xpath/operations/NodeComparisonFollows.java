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
 * A class definition, to implement XPath 3.1 node comparison 
 * operator '>>'.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class NodeComparisonFollows extends Operation
{

    private static final long serialVersionUID = -4609240318743840419L;

    /**
    * Apply the operation to two operands, and return the result.
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
	   }
	   else if ((left instanceof ResultSequence) && (((ResultSequence)left).size() == 1) && 
			                                                             (((ResultSequence)left).item(0) instanceof XMLNodeCursorImpl)) {
		   lNodeSet = (XMLNodeCursorImpl)(((ResultSequence)left).item(0));  
	   }

	   if (lNodeSet != null) {
		   lNodeSet = (XMLNodeCursorImpl)(lNodeSet.getFresh());
	   }

	   if (right instanceof XMLNodeCursorImpl) {
		   rNodeSet = (XMLNodeCursorImpl)right; 
	   }
	   else if ((right instanceof ResultSequence) && (((ResultSequence)right).size() == 1) && 
			                                                               (((ResultSequence)right).item(0) instanceof XMLNodeCursorImpl)) {
		   rNodeSet = (XMLNodeCursorImpl)(((ResultSequence)right).item(0));  
	   }

	   if (rNodeSet != null) {
		   rNodeSet = (XMLNodeCursorImpl)(rNodeSet.getFresh());
	   }

	   if ((lNodeSet != null) && (rNodeSet != null)) {
		   int lNodeHandle = lNodeSet.asNode(m_xctxt);
		   int rNodeHandle = rNodeSet.asNode(m_xctxt);                               
		   if ((lNodeHandle == DTM.NULL) || (rNodeHandle == DTM.NULL)) {
			   result = new ResultSequence();  
		   }
		   else if (lNodeHandle > rNodeHandle) {
			   result = XBoolean.S_TRUE;
		   }
		   else {
			   result = XBoolean.S_FALSE;  
		   }
	   }
	   else if (lNodeSet == null) {
		   throw new javax.xml.transform.TransformerException("XPTY0004 : The supplied item type of LHS operand of XPath "
				                                                                                               + "operator '>>' is not a singleton node."); 
	   }
	   else if (rNodeSet == null) {
		   throw new javax.xml.transform.TransformerException("XPTY0004 : The supplied item type of RHS operand of XPath "
				                                                                                               + "operator '>>' is not a singleton node.");
	   }

	   return result; 
   }

}
