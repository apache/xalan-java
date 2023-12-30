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

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSBoolean;

/**
 * Execute the deep-equal() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncDeepEqual extends FunctionMultiArgs {

  private static final long serialVersionUID = -7233896041672168880L;

  /**
   * Execute the function. The function must return a valid object.
   * 
   * @param xctxt The current execution context
   * @return A valid XObject
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {
	    
	  XObject result = new XSBoolean(true);
	  
	  SourceLocator srcLocator = xctxt.getSAXLocator();
	  
	  Expression arg0 = getArg0();
	  Expression arg1 = getArg1();
	  
	  if ((arg0 == null) || (arg1 == null)) {
		 throw new javax.xml.transform.TransformerException("FOAP0001 : The number of arguments specified while "
		 		                                                   + "calling deep-equal() function is wrong. Expected "
		 		                                                   + "number of arguments for deep-equal() function is two "
		 		                                                   + "or three.", srcLocator);  
	  }
	  
	  XObject arg0Val = arg0.execute(xctxt);
	  XObject arg1Val = arg1.execute(xctxt);
	  
      Expression arg2 = getArg2();  // an optional collation argument. REVISIT
	  
	  XObject collationVal = null;
	  
	  if (arg2 != null) {
		 collationVal = arg2.execute(xctxt);   
	  }
	  
	  ResultSequence resultSeq0 = XslTransformEvaluationHelper.getResultSequenceFromXObject(arg0Val, xctxt);
	  
	  ResultSequence resultSeq1 = XslTransformEvaluationHelper.getResultSequenceFromXObject(arg1Val, xctxt);
	  
	  boolean isNodesUnequal = false;
	  
	  if (resultSeq0.size() == resultSeq1.size()) {		 
		 for (int idx1 = 0; idx1 < resultSeq0.size(); idx1++) {
			for (int idx2 = 0; idx2 < resultSeq1.size(); idx2++) {
			  if (idx1 == idx2) {
				 XObject item1 = resultSeq0.item(idx1);
				 XObject item2 = resultSeq1.item(idx2);
				 if (item1.getType() != item2.getType()) {
					result = new XSBoolean(false);
					isNodesUnequal = true;
					break;
				 }
			     else if ((item1.getType() == XObject.CLASS_NODESET) && 
						  (item2.getType() == XObject.CLASS_NODESET)) {
					XNodeSet node1 = (XNodeSet)item1;
					XNodeSet node2 = (XNodeSet)item2;
					if (!node1.equalsWithNodeName(node2, xctxt)) {
					   result = new XSBoolean(false);
					   isNodesUnequal = true;
					   break;  
					}
				 }
				 else if (!item1.vcEquals(item2, null, true)) {
					result = new XSBoolean(false);
					isNodesUnequal = true;
					break;
				 }										
			  }			 			 
		  }
			 
		  if (isNodesUnequal) {
			 break; 
		  }
	   }
	 }
	 else {
	    result = new XSBoolean(false);  
	 }
	
	 return result;
  }
  
}
