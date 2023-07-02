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

import org.apache.xml.dtm.DTMIterator;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.operations.Range;
import org.apache.xpath.operations.Variable;

/**
 * Execute the count() function.
 * 
 * @xsl.usage advanced
 */
public class FuncCount extends FunctionOneArg
{
    static final long serialVersionUID = -7116225100474153751L;

  /**
   * Execute the function. The function must return a valid object.
   * 
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
        int count = 0;
        
        if (m_arg0 instanceof Function) {
            XObject evalResult = ((Function)m_arg0).execute(xctxt);
            if (evalResult instanceof XNodeSet) {
                count = ((XNodeSet)evalResult).getLength();   
            }
            else if (evalResult instanceof ResultSequence) {
               count = ((ResultSequence)evalResult).size();
            }
        }
        else if (m_arg0 instanceof Variable) {
           XObject evalResult = ((Variable)m_arg0).execute(xctxt);
           if (evalResult instanceof XNodeSet) {
               count = ((XNodeSet)evalResult).getLength();   
           }
           else if (evalResult instanceof ResultSequence) {
              count = ((ResultSequence)evalResult).size();
           }
        }
        else if (m_arg0 instanceof Expression) {
            if (m_arg0 instanceof Range) {
                ResultSequence resultSeq = (ResultSequence)(((Range)m_arg0).execute(xctxt));
                count = resultSeq.size();
            }
            else {
               DTMIterator nl = m_arg0.asIterator(xctxt, xctxt.getCurrentNode());
               count = nl.getLength();	
               nl.detach();
            }
        }
    
        return new XNumber((double)count);    
  }
  
}
