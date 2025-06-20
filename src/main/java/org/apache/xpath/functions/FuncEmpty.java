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
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;

/**
 * Implementation of XPath 3.1 function fn:empty.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncEmpty extends FunctionOneArg {
    
    private static final long serialVersionUID = 8023120626582040786L;

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
        
        XObject result = null;
        
        XObject arg0Obj = m_arg0.execute(xctxt);
        
        if (arg0Obj instanceof XMLNodeCursorImpl) {
           XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)arg0Obj;
           DTMCursorIterator dtmCursorIter = xmlNodeCursorImpl.iterRaw();
    	   if (dtmCursorIter.nextNode() == DTM.NULL) {
    		   result = XBoolean.S_TRUE;  
    	   }
    	   else {
    		   result = XBoolean.S_FALSE; 
    	   }
        }
        else if (arg0Obj instanceof ResultSequence) {
           ResultSequence resultSeq = (ResultSequence)arg0Obj;
           if (resultSeq.size() == 0) {
              result = XBoolean.S_TRUE; 
           }
           else if ((resultSeq.size() == 1) && (resultSeq.item(0) instanceof XMLNodeCursorImpl)) {
        	   XObject xObj = resultSeq.item(0);
        	   XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj;
        	   DTMCursorIterator dtmCursorIter = xmlNodeCursorImpl.iterRaw();
        	   if (dtmCursorIter.nextNode() == DTM.NULL) {
        		   result = XBoolean.S_TRUE;  
        	   }
        	   else {
        		   result = XBoolean.S_FALSE; 
        	   }
           }
           else {
              result = XBoolean.S_FALSE;
           }
        }
        else {
           result = XBoolean.S_FALSE; 
        }
        
        return result;
    }

}
