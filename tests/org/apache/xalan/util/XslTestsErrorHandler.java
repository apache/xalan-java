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
package org.apache.xalan.util;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * XSLT transformation error handler class, that may be bound to
 * an object of type javax.xml.transform.Transformer.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XslTestsErrorHandler implements ErrorHandler, ErrorListener {
    
    private List<String> trfErrorList = new ArrayList<String>();
    
    private List<String> trfFatalErrorList = new ArrayList<String>();

    @Override
    public void error(TransformerException ex) throws TransformerException {        
        String errMesg = ex.getMessage();
        trfErrorList.add(errMesg);
    }

    @Override
    public void fatalError(TransformerException ex) throws TransformerException {        
        String errMesg = ex.getMessage();
        trfFatalErrorList.add(errMesg);
    }

    @Override
    public void warning(TransformerException ex) throws TransformerException {
        // no op        
    }

    @Override
    public void error(SAXParseException ex) throws SAXException {        
        
    }

    @Override
    public void fatalError(SAXParseException ex) throws SAXException {        
        
    }

    @Override
    public void warning(SAXParseException ex) throws SAXException {
        // no op        
    }
    
    public List<String> getTrfErrorList() {
        return trfErrorList;  
    }
    
    public List<String> getTrfFatalErrorList() {
        return trfFatalErrorList;  
    }

}
