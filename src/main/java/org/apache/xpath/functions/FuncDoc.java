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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.xml.sax.ErrorHandler;

/**
 * Implementation of XPath 3.1 function fn:doc.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncDoc extends FunctionOneArg {

    private static final long serialVersionUID = -7132184569695971726L;
    
    private static ErrorHandler m_errorHandler = null;
    
    /**
     * Default constructor.
     */
    public FuncDoc() {
    	m_defined_arity = new Short[] { 1 };	
    }
    
    /**
     * Class constructor.
     */
    public FuncDoc(ErrorHandler errorHandler) {
    	m_errorHandler = errorHandler;
    }

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

        XObject xObject0 = m_arg0.execute(xctxt);
            
        String hrefStrVal = XslTransformEvaluationHelper.getStrVal(xObject0);
            
        result = getDocumentNode(xctxt, hrefStrVal);
        
        return result;
    }

    /**
     * Given a URL string as an argument to this method, fetch the document 
     * contents from URL and return an XDM document node corresponding to URL's 
     * document contents.
     * 
     * @param xctxt				XPath context object
     * @param hrefStrVal		a URL's string value
     * @return					an XDM document node corresponding to URL's contents
     * @throws TransformerException
     */
	public XObject getDocumentNode(XPathContext xctxt, String hrefStrVal) throws TransformerException {
		
		XObject result = null;
		
		SourceLocator srcLocator = xctxt.getSAXLocator(); 
		
		// If the first argument is a relative uri reference, then 
        // resolve that relative uri with base uri of the stylesheet.
        
        URL resolvedArg0Url = null;

        try {
        	URI arg0Uri = new URI(hrefStrVal);        	

        	if (arg0Uri.isAbsolute()) {
        	    resolvedArg0Url = new URL(hrefStrVal); 
        	}
        	else {
        		String stylesheetSystemId = null;
            	
        		if (srcLocator != null) {
            		stylesheetSystemId = srcLocator.getSystemId();
            	}
            	else {
            		ExpressionNode expressionNode = getExpressionOwner();
            		stylesheetSystemId = expressionNode.getSystemId(); 
            	}
            	
        		if (stylesheetSystemId != null) {
        			URI resolvedUriArg = (new URI(stylesheetSystemId)).resolve(hrefStrVal);
        			resolvedArg0Url = resolvedUriArg.toURL();
        		}
        		else {
        			resolvedArg0Url = new URL(hrefStrVal);
        		}
        	}

        	String urlResourceStrContents = XslTransformEvaluationHelper.getStringContentFromUrl(resolvedArg0Url);

        	result = FuncParseXml.getNodeSetFromStr(urlResourceStrContents, xctxt, m_errorHandler);
        }
        catch (URISyntaxException ex) {
           throw new javax.xml.transform.TransformerException("FODC0005 : The uri '" + hrefStrVal + "' is not a valid absolute uri, "
                                                                                                + "or cannot be resolved to an absolute uri.", srcLocator); 
        }
        catch (MalformedURLException ex) {
           throw new javax.xml.transform.TransformerException("FODC0005 : The uri '" + hrefStrVal + "' is not a valid absolute uri, "
                                                                                                + "or cannot be resolved to an absolute uri.", srcLocator); 
        }
        catch (IOException ex) {
           throw new javax.xml.transform.TransformerException("FODC0002 : The data from uri '" + hrefStrVal + "' cannot be "
                                                                                                                      + "retrieved.", srcLocator);
        }
        catch (javax.xml.transform.TransformerException ex) {
           throw new javax.xml.transform.TransformerException("FODC0002 : The data from uri '" + hrefStrVal + "' cannot be "
                                                                                                                      + "parsed as an XML document.", srcLocator); 
        }
        catch (Exception ex) {
           throw new javax.xml.transform.TransformerException("FODC0002 : The data from uri '" + hrefStrVal + "' cannot be "
                                                                                                                      + "parsed as an XML document.", srcLocator); 
        }
        
		return result;
		
	}

}
