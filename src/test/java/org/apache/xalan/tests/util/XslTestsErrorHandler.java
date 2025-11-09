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
package org.apache.xalan.tests.util;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * XSLT transformation error handler class, that may be bound to
 * javax.xml.transform.Transformer object instance.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XslTestsErrorHandler implements ErrorHandler, ErrorListener {
    
    private List<String> m_trfErrorList = null;
    
    private List<String> m_trfFatalErrorList = null;
    
    private List<String> m_trfWarningList = null;
    
    private String m_xmlDocumentStr = null;
    
    private String m_xslTestCaseName = null;
    
    /**
     * Class constructor.
     */
    public XslTestsErrorHandler() {
    	m_trfErrorList = new ArrayList<String>();
    	m_trfFatalErrorList = new ArrayList<String>();
    	m_trfWarningList = new ArrayList<String>();
    }

    @Override
    public void error(TransformerException ex) throws TransformerException {        
        String errMesg = ex.getMessage();
        m_trfErrorList.add(errMesg);
    }

    @Override
    public void fatalError(TransformerException ex) throws TransformerException {        
        String errMesg = ex.getMessage();
        m_trfFatalErrorList.add(errMesg);
    }

    @Override
    public void warning(TransformerException ex) throws TransformerException {
    	String errMesg = ex.getMessage();
    	m_trfWarningList.add(errMesg);
    }

    @Override
    public void error(SAXParseException ex) throws SAXException {        
    	String errMesg = ex.getMessage();
    	m_trfErrorList.add(errMesg);
    }

    @Override
    public void fatalError(SAXParseException ex) throws SAXException {        
    	String errMesg = ex.getMessage();
    	m_trfFatalErrorList.add(errMesg);
    }

    @Override
    public void warning(SAXParseException ex) throws SAXException {
    	String errMesg = ex.getMessage();
    	m_trfWarningList.add(errMesg);
    }
    
    public List<String> getTrfErrorList() {
        return m_trfErrorList;  
    }
    
    public List<String> getTrfFatalErrorList() {
        return m_trfFatalErrorList;  
    }
    
    public List<String> getTrfWarningList() {
        return m_trfWarningList;  
    }

	public void setXMLDocumentStr(String xmlDocumentStr) {
		this.m_xmlDocumentStr = xmlDocumentStr;		
	}

	public void setTestCaseName(String xslTestCaseName) {
		this.m_xslTestCaseName = xslTestCaseName;		
	}

}
