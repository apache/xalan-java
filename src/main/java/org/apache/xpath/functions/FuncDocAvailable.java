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

import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import xml.xpath31.processor.types.XSBoolean;

/**
 * Implementation of the doc-available() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncDocAvailable extends FunctionOneArg {

	private static final long serialVersionUID = -737728533828222724L;

	/**
     * Execute the function. The function must return a valid object.
     * 
     * @param xctxt The current execution context.
     * @return A valid XObject.
     *
     * @throws javax.xml.transform.TransformerException
     */
    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {        
        
        XObject result = null;
        
        if ((m_arg0 == null) || ((m_arg0 instanceof ResultSequence) && (((ResultSequence)m_arg0).size() == 0))) {
        	// if function argument is an empty sequence, this function returns false        	
        	result = new XSBoolean(false);
        }
        else { 
        	FuncDocErrorHandler funcDocErrorHandler = new FuncDocErrorHandler();
            
        	FuncDoc funcDoc = new FuncDoc(funcDocErrorHandler);            
            funcDoc.m_arg0 = this.m_arg0;
            
            try {
               XObject funcDocResult = funcDoc.execute(xctxt);
               if (funcDocResult instanceof XNodeSet) {
            	   result = new XSBoolean(true); 
               }
               else {
            	   result = new XSBoolean(false);
               }
            }
            catch (javax.xml.transform.TransformerException ex) {
               // if FuncDocErrorHandler object has isErrorOccured == true,
               // we don't need to do anything specific about that. during that 
               // situation, code evaluation control comes here.
               result = new XSBoolean(false);
            }
            
            if (result == null) {
                result = new XSBoolean(false);
            }
        }
        
        
        return result;
    }
    
    /**
     * An org.xml.sax.ErrorHandler interface implementation. We bind an 
     * object of this class with javax.xml.parsers.DocumentBuilder later 
     * during processing of function fn:doc-available.
     */
    class FuncDocErrorHandler implements ErrorHandler {
    	
    	private boolean isErrorOccured = false;

		@Override
		public void error(SAXParseException spe) throws SAXException {
		   isErrorOccured = true;			
		}

		@Override
		public void fatalError(SAXParseException spe) throws SAXException {
		   isErrorOccured = true;			
		}

		@Override
		public void warning(SAXParseException spe) throws SAXException {
		   // NO OP			
		}
		
		public boolean getErrorStatus() {
			return isErrorOccured;
		}
    	
    }

}
