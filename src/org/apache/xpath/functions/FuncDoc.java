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

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

/**
 * Implementation of the doc() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncDoc extends FunctionOneArg {

    private static final long serialVersionUID = -7132184569695971726L;

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
        
        SourceLocator srcLocator = xctxt.getSAXLocator();

        XObject xObject0 = m_arg0.execute(xctxt);
            
        String hrefStrVal = XslTransformEvaluationHelper.getStrVal(xObject0);
            
        // If the first argument is a relative uri reference, then 
        // resolve that relative uri with base uri of the stylesheet
        URL resolvedArg0Url = null;

        try {
           URI arg0Uri = new URI(hrefStrVal);
           String stylesheetSystemId = null;
           if (srcLocator != null) {
              stylesheetSystemId = srcLocator.getSystemId();
           }
           else {
              ExpressionNode expressionNode = getExpressionOwner();
              stylesheetSystemId = expressionNode.getSystemId(); 
           }
                        
           if (!arg0Uri.isAbsolute() && (stylesheetSystemId != null)) {
              URI resolvedUriArg = (new URI(stylesheetSystemId)).resolve(hrefStrVal);
              resolvedArg0Url = resolvedUriArg.toURL(); 
           }
                
            if (resolvedArg0Url == null) {
               resolvedArg0Url = new URL(hrefStrVal);   
            }
                
            String urlStrContents = FuncUnparsedText.readStrDataFromUrl(resolvedArg0Url);
                
            result = FuncParseXml.getNodeSetFromStr(urlStrContents, xctxt);
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
           throw new javax.xml.transform.TransformerException("FODC0002 : The data from uri '" + hrefStrVal + "' cannot be successfully "
                                                                                                                      + "parsed as an XML document.", srcLocator); 
        }
        catch (Exception ex) {
           throw new javax.xml.transform.TransformerException("FODC0002 : The data from uri '" + hrefStrVal + "' cannot be successfully "
                                                                                                                      + "parsed as an XML document.", srcLocator); 
        }
        
        return result;
    }

}
