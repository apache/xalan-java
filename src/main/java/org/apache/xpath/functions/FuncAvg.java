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
/*
 * $Id$
 */
package org.apache.xpath.functions;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

/**
 * Implementation of an XPath 3.1 function fn:avg.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncAvg extends FunctionOneArg
{

  private static final long serialVersionUID = 6282866669363344636L;

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
      
      XNumber sumOfValues = XslTransformEvaluationHelper.getSumOfValues(
                                                                     m_arg0, xctxt);
      
      XNumber countOfSeqItems = XslTransformEvaluationHelper.getCountOfSequenceItems(
                                                                     m_arg0, xctxt);
      if (countOfSeqItems.num() > 0) {
         result = new XNumber(sumOfValues.num() / countOfSeqItems.num()); 
      }
      else {
         // if this function's argument has evaluated to an empty sequence,
         // the result of this function call is empty sequence.
         result = new ResultSequence();
      }
      
      // TO DO : average calculation involving for example, xs:duration and 
      // its subtypes.
      
      return result;
  }
  
}
