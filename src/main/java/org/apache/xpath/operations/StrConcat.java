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

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

/**
 * The XPath 3.1 string concatenation "||" operation.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class StrConcat extends Operation
{

   private static final long serialVersionUID = -820873651965512508L;

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
    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {
        
    	XObject result = null;

    	XObject left = m_left.execute(xctxt);

    	XObject right = m_right.execute(xctxt);

    	result = new XString(XslTransformEvaluationHelper.getStrVal(left) + 
    			                                       XslTransformEvaluationHelper.getStrVal(right));

    	return result;
    }
    
    /**
     * Apply the operation to two operands, and return the result.
     */
    public XObject operate(XObject left, XObject right) {
    	XObject result =  new XString(XslTransformEvaluationHelper.getStrVal(left) + 
    			                                                XslTransformEvaluationHelper.getStrVal(right));        
    	return result;
    }

}
