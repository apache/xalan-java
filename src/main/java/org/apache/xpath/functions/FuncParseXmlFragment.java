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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.dom.DOMSource;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;

/**
 * Implementation of the parse-xml-fragment() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncParseXmlFragment extends FunctionOneArg {

    private static final long serialVersionUID = -3212697250471567672L;

    /**
     * Execute the function. The function must return a valid object.
     * 
     * @param xctxt The current execution context.
     * @return A valid XObject.
     *
     * @throws javax.xml.transform.TransformerException
     */
    public XObject execute(XPathContext xctxt) throws 
                                                   javax.xml.transform.TransformerException {        
        XObject result = null;
        
        SourceLocator srcLocator = xctxt.getSAXLocator();

        try {
            XObject xObject0 = m_arg0.execute(xctxt);            
            
            String argStrVal = XslTransformEvaluationHelper.getStrVal(xObject0);
            
            result = getNodeSetFromStr(argStrVal, xctxt);
        }
        catch (javax.xml.transform.TransformerException ex) {
            throw new javax.xml.transform.TransformerException(ex.getMessage(), srcLocator);   
        }
        
        return result;
    }
    
    /**
     * Get an xdm nodeset corresponding to an XML fragment string value.
     */
    private XMLNodeCursorImpl getNodeSetFromStr(String strVal, XPathContext xctxt) throws 
                                                          javax.xml.transform.TransformerException {
        XMLNodeCursorImpl nodeSet = null;
        
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            
            // Enable the XML namespace processing, on an XML parse 
            // of the string value.
            dbf.setNamespaceAware(true);         
            
            DocumentBuilder dBuilder = dbf.newDocumentBuilder();
            
            // Wrap the originally provided string value (that is supposed to be an XML fragment 
            // string value), with a temporary XML outer element.
            String currentTimeMillisec = String.valueOf(System.currentTimeMillis());
            String tempOuterWrapperElementName = "t0_" + currentTimeMillisec;            
            String wrappedXmlStrVal = "<" + tempOuterWrapperElementName + ">" + strVal + 
                                                                                     "</" + tempOuterWrapperElementName+">"; 
            
            InputStream inpStream = new ByteArrayInputStream(wrappedXmlStrVal.getBytes(
                                                                                    StandardCharsets.UTF_8));
            Document xmlDocument = dBuilder.parse(inpStream);
            
            DocumentFragment xmlDocFragment = xmlDocument.createDocumentFragment();            
            NodeList xmlDomChildNodes = (xmlDocument.getDocumentElement()).getChildNodes();
            
            while (xmlDomChildNodes.getLength() > 0) {
               xmlDocFragment.appendChild(xmlDomChildNodes.item(0));
            }
            
            DTM dtm = xctxt.getDTM(new DOMSource(xmlDocFragment), true, null, false, false);            
            int documentNodeHandleVal = dtm.getDocument();
            
            nodeSet = new XMLNodeCursorImpl(documentNodeHandleVal, xctxt.getDTMManager());
        }
        catch (Exception ex) {
           throw new javax.xml.transform.TransformerException("FODC0002 : The string value supplied as an argument to function "
                                                                                         + "fn:parse-xml-fragment(), cannot be successfully "
                                                                                         + "parsed as an XML document fragment."); 
        }
        
        return nodeSet;
    }

}
