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

import java.util.Collections;
import java.util.List;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

/**
 * Implementation of the unordered() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncUnordered extends FunctionOneArg {

    private static final long serialVersionUID = 4192924071383539197L;
    
    /**
     * Class constructor.
     */
    public FuncUnordered() {
  	   m_defined_arity = new Short[] { 1 };  
    }

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
        
        ResultSequence result = new ResultSequence();
        
        SourceLocator srcLocator = xctxt.getSAXLocator();

        XObject xObject0 = m_arg0.execute(xctxt);
            
        ResultSequence rsArg0 = XslTransformEvaluationHelper.getResultSequenceFromXObject(
                                                                                      xObject0, xctxt);
        List<XObject> sequenceAsList = rsArg0.getResultSequenceItems();
        
        // randomly permute the list of input sequence items
        Collections.shuffle(sequenceAsList);
        
        for (int idx = 0; idx < sequenceAsList.size(); idx++) {
           result.add(sequenceAsList.get(idx));  
        }
        
        return result;
    }

}
