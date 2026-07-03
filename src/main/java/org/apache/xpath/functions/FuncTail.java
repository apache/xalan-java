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

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;

/**
 * Implementation of an XPath 3.1 function fn:tail.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncTail extends FunctionOneArg {

    private static final long serialVersionUID = -7505231181568257145L;
    
    /**
     * Class constructor.
     */
    public FuncTail() {
  	   m_arity = new Short[] { 1 };  
    }

    /**
	 * Evaluate the function. The function must return a valid object.
	 * 
	 * @param xctxt                        An XPath context object
	 * @return                             A valid XObject
	 *
	 * @throws javax.xml.transform.TransformerException
	 */
    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
    {
        
        ResultSequence result = new ResultSequence();
        
        XObject xObject0 = getFunctionArgEffectiveValue(m_arg0, xctxt);
        
        if (xObject0 instanceof XMLNodeCursorImpl) {
           XMLNodeCursorImpl nodeSet = (XMLNodeCursorImpl)xObject0;
           
           DTMCursorIterator dtmIter = nodeSet.iterRaw();
           int nextNode = dtmIter.nextNode();
           while ((nextNode = dtmIter.nextNode()) != DTM.NULL) {
              XMLNodeCursorImpl xdmNode = new XMLNodeCursorImpl(nextNode, xctxt);
              result.add(xdmNode);
           }
        }
        else if (xObject0 instanceof ResultSequence) {
           ResultSequence resultSeq = (ResultSequence)xObject0;
           int size1 = resultSeq.size();
           for (int idx = 1; idx < size1; idx++) {
              result.add(resultSeq.item(idx)); 
           }
        }
        
        return result;
    }

}
