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

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.XPathContext;
import org.apache.xpath.composite.ForExpr;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.operations.SimpleMapOperator;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.xs.types.XSAnyType;

/**
 * Execute the Sum() function.
 * @xsl.usage advanced
 */
public class FuncSum extends FunctionOneArg
{
    static final long serialVersionUID = -2719049259574677519L;

  /**
   * Execute the function.  The function must return
   * a valid object.
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
    
    double sum = 0.0;    
    
    if (m_arg0 instanceof Variable) {
       Variable xslVariable = (Variable)m_arg0;
       XObject resultObj = xslVariable.execute(xctxt);
       if (resultObj instanceof ResultSequence) {
          ResultSequence resultSeq = (ResultSequence)resultObj;
          sum = sumResultSequence(resultSeq);          
       }       
    }
    else if (m_arg0 instanceof Function) {
       XObject resultObj = ((Function)m_arg0).execute(xctxt);
       if (resultObj instanceof ResultSequence) {
          ResultSequence resultSeq = (ResultSequence)resultObj;
          sum = sumResultSequence(resultSeq);          
       }  
    }
    else if (m_arg0 instanceof ForExpr) {
       ForExpr forExpr = (ForExpr)m_arg0;
       ResultSequence forExprResult = (ResultSequence)(forExpr.execute(xctxt));
       sum = sumResultSequence(forExprResult);
    }
    else if (m_arg0 instanceof SimpleMapOperator) {
       SimpleMapOperator simpleMapOperator = (SimpleMapOperator)m_arg0;
       ResultSequence simpleMapOperatorResult = (ResultSequence)(simpleMapOperator.
                                                                              execute(xctxt));
       sum = sumResultSequence(simpleMapOperatorResult);
    }
    else {
       int pos;
        
       DTMIterator nodes = m_arg0.asIterator(xctxt, xctxt.getCurrentNode());

       while (DTM.NULL != (pos = nodes.nextNode())) {
          DTM dtm = nodes.getDTM(pos);
          XMLString xmlStr = dtm.getStringValue(pos);

          if (xmlStr != null) {
             sum += xmlStr.toDouble();
          }
       }
       nodes.detach();
    }

    return new XNumber(sum);
  }
  
  /**
   * Summation of the values of ResultSequence data items.
   *  
   * @param resultSeq  The ResultSequence object instance, whose items
   *                   need to be added to produce a summation value. 
   * @return           The summation value with data type double.
   */
  private double sumResultSequence(ResultSequence resultSeq) {
     double sum = 0.0;
     
     for (int idx = 0; idx < resultSeq.size(); idx++) {
        XObject xObj = resultSeq.item(idx);
        String str = null;
        if (xObj instanceof XSAnyType) {
           str = ((XSAnyType)xObj).stringValue();     
        }
        else {
           str = xObj.str();
        }
        if (str != null) {
           XString xStr = new XString(str);
           sum +=  xStr.toDouble();
        }
     }
     
     return sum;
  }
}
