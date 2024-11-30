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

import java.math.BigInteger;

import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSInteger;

/**
 * Implementation of the string-to-codepoints() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncStringToCodepoints extends FunctionDef1Arg
{

   private static final long serialVersionUID = -2509583274718320399L;

  /**
   * Execute the function. The function must return
   * a valid object.
   * 
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
     
     XObject result = null;
     
     String inpStr = (getArg0AsString(xctxt)).toString();
     
     XPathCollationSupport xPathCollationSupport = xctxt.getXPathCollationSupport();
     
     int[] codePointsArr = xPathCollationSupport.getCodepointsFromString(inpStr);
     
     ResultSequence resultSeq = new ResultSequence();
     
     for (int idx=0; idx < codePointsArr.length; idx++) {
        long codepointVal = codePointsArr[idx]; 
        resultSeq.add(new XSInteger(BigInteger.valueOf(codepointVal))); 
     }
     
     result = resultSeq; 
     
     return result;
  }
}
