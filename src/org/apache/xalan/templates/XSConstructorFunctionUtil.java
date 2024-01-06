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
package org.apache.xalan.templates;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.XMLConstants;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.apache.xerces.xs.XSModel;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.XPathContext;
import org.apache.xpath.compiler.Keywords;
import org.apache.xpath.functions.FuncExtFunction;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSDate;
import xml.xpath31.processor.types.XSDateTime;
import xml.xpath31.processor.types.XSDayTimeDuration;
import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSDuration;
import xml.xpath31.processor.types.XSFloat;
import xml.xpath31.processor.types.XSInt;
import xml.xpath31.processor.types.XSInteger;
import xml.xpath31.processor.types.XSLong;
import xml.xpath31.processor.types.XSNormalizedString;
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSTime;
import xml.xpath31.processor.types.XSToken;
import xml.xpath31.processor.types.XSYearMonthDuration;

/**
 * A utility class that primarily supports, evaluations of XSLT stylesheet
 * function calls (for the stylesheet functions, defined with syntax 
 * xsl:function), and XPath 3.1 constructor function calls.  
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XSConstructorFunctionUtil {
	
	// The below mentioned class fields define constants used by implementation
	// within this class, and also within few other parts of XalanJ's XSLT 3.0 
	// implementation.
	
	public static final String XS_VALID_TRUE = "XS_VALID_TRUE";
	
	private static final String UTF_16 = "UTF-16";
	
	private static final String UTF_8 = "UTF-8";
	
	private static final String DOM_FORMAT_PRETTY_PRINT = "format-pretty-print";
    
    /**
     * We use this method, primarily to evaluate XSLT stylesheet function calls (for the
     * stylesheet functions, defined with syntax xsl:function), and XPath 3.1 constructor
     * function calls (having syntax with form xs:type_name(..)).
     * 
     *  XalanJ's extension function handling mechanism, treats at a syntactic level,
     *  function calls belonging to non-null XML namespaces as calls to extension functions.
     *  
     *  Currently, XPath function calls having XML namespaces
     *  http://www.w3.org/2005/xpath-functions, http://www.w3.org/2005/xpath-functions/math
     *  within function names, are treated as XPath built-in functions by XalanJ. All other
     *  XPath function calls having other non-null XML namespaces are handling by XalanJ's
     *  extension function handling mechanism.
     */
    public static XObject processFuncExtFunctionOrXPathOpn(XPathContext xctxt, Expression expr, 
                                                           TransformerImpl transformer) throws TransformerException {        
        XObject evalResult = null;
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        StylesheetRoot stylesheetRoot = null;
        
        FuncExtFunction funcExtFunction = null;
        String funcName = null;
        String funcNamespace = null;
        
        try {        
	        if (expr instanceof FuncExtFunction) {
	            funcExtFunction = (FuncExtFunction)expr;	            
	            funcName = funcExtFunction.getFunctionName();
	            funcNamespace = funcExtFunction.getNamespace();
	            
	            ExpressionNode expressionNode = expr.getExpressionOwner();
	            ExpressionNode stylesheetRootNode = null;
	            while (expressionNode != null) {
	                stylesheetRootNode = expressionNode;
	                expressionNode = expressionNode.exprGetParent();                     
	            }
	            
	            stylesheetRoot = (StylesheetRoot)stylesheetRootNode;
	            
	            if (transformer == null) {
	               transformer = stylesheetRoot.getTransformerImpl();  
	            }
	            
	            TemplateList templateList = stylesheetRoot.getTemplateListComposed();
	            
	            ElemTemplate elemTemplate = templateList.getTemplate(new QName(funcNamespace, funcName));
	                        
	            if ((elemTemplate != null) && (elemTemplate instanceof ElemFunction)) {
	                // Handle call to XSLT stylesheet function definition, specified with syntax 
	                // xsl:function.                
	                ResultSequence argSequence = new ResultSequence();
	                for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
	                    XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
	                    argSequence.add(argVal);
	                }
	                
	                evalResult = ((ElemFunction)elemTemplate).executeXslFunction(transformer, argSequence);
	            }            
	            else if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(funcExtFunction.getNamespace())) {                
	                // Handle call to XPath 3.1 constructor function, having syntax with form
	                // xs:type_name(..). 
	                ResultSequence argSequence = new ResultSequence();
	                ResultSequence evalResultSequence = null;
	                
	                switch (funcExtFunction.getFunctionName()) {
	                    case Keywords.XS_STRING :                        
	                        for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
	                            XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
	                            argSequence.add(new XSString(XslTransformEvaluationHelper.getStrVal(argVal)));
	                        }
	    
	                        evalResultSequence = (new XSString()).constructor(argSequence);
	                        evalResult = evalResultSequence.item(0);
	                        
	                        break;
	                    case Keywords.XS_NORMALIZED_STRING :                        
	                        for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
	                            XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
	                            argSequence.add(new XSNormalizedString(XslTransformEvaluationHelper.getStrVal(argVal)));
	                        }
	    
	                        evalResultSequence = (new XSNormalizedString()).constructor(argSequence);
	                        evalResult = evalResultSequence.item(0);
	                        
	                        break;
	                    case Keywords.XS_TOKEN :                        
	                        for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
	                            XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
	                            argSequence.add(new XSToken(XslTransformEvaluationHelper.getStrVal(argVal)));
	                        }
	    
	                        evalResultSequence = (new XSToken()).constructor(argSequence);
	                        evalResult = evalResultSequence.item(0);
	                        
	                        break;
	                    case Keywords.XS_DECIMAL :
	                        for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
	                            XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
	                            argSequence.add(new XSDecimal(XslTransformEvaluationHelper.getStrVal(argVal)));
	                        }
	    
	                        evalResultSequence = (new XSDecimal()).constructor(argSequence);
	                        evalResult = evalResultSequence.item(0);
	                        
	                        break;
	                    case Keywords.XS_FLOAT :
	                        for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
	                            XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
	                            argSequence.add(new XSFloat(XslTransformEvaluationHelper.getStrVal(argVal)));
	                        }
	    
	                        evalResultSequence = (new XSFloat()).constructor(argSequence);
	                        evalResult = evalResultSequence.item(0);
	                        
	                        break;                
	                    case Keywords.XS_DOUBLE :
	                        for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
	                            XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
	                            argSequence.add(new XSDouble(XslTransformEvaluationHelper.getStrVal(argVal)));
	                        }
	    
	                        evalResultSequence = (new XSDouble()).constructor(argSequence);
	                        evalResult = evalResultSequence.item(0);
	                        
	                        break;                
	                    case Keywords.XS_INTEGER :
	                        for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
	                            XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
	                            argSequence.add(new XSInteger(XslTransformEvaluationHelper.getStrVal(argVal)));
	                        }
	    
	                        evalResultSequence = (new XSInteger()).constructor(argSequence);
	                        evalResult = evalResultSequence.item(0); 
	                        
	                        break;                
	                    case Keywords.XS_LONG :
	                        for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
	                            XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
	                            argSequence.add(new XSLong(XslTransformEvaluationHelper.getStrVal(argVal)));
	                        }
	    
	                        evalResultSequence = (new XSLong()).constructor(argSequence);
	                        evalResult = evalResultSequence.item(0);
	                        
	                        break;
	                    case Keywords.XS_INT :
	                        for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
	                            XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
	                            argSequence.add(new XSInt(XslTransformEvaluationHelper.getStrVal(argVal)));
	                        }
	    
	                        evalResultSequence = (new XSInt()).constructor(argSequence);
	                        evalResult = evalResultSequence.item(0);
	                        
	                        break;
	                    case Keywords.FUNC_BOOLEAN_STRING :
	                        for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
	                            XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
	                            String argStrVal = XslTransformEvaluationHelper.getStrVal(argVal);
	                            Boolean boolVal = Boolean.valueOf(("0".equals(argStrVal) || "false".equals(argStrVal)) ? 
	                                                                                                     "false" : "true");
	                            argSequence.add(new XSBoolean(boolVal));
	                        }
	    
	                        evalResultSequence = (new XSBoolean()).constructor(argSequence);
	                        evalResult = evalResultSequence.item(0);
	                        
	                        break;
	                    case Keywords.XS_DATE :
	                        for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
	                            XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
	                            argSequence.add(XSDate.parseDate(XslTransformEvaluationHelper.getStrVal(argVal)));
	                        }
	    
	                        evalResultSequence = (new XSDate()).constructor(argSequence); 
	                        evalResult = evalResultSequence.item(0);
	                        
	                        break;
	                    case Keywords.XS_DATETIME :
	                        for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
	                            XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
	                            argSequence.add(XSDateTime.parseDateTime(XslTransformEvaluationHelper.getStrVal(argVal)));
	                        }
	    
	                        evalResultSequence = (new XSDateTime()).constructor(argSequence); 
	                        evalResult = evalResultSequence.item(0);
	                        
	                        break;
	                    case Keywords.XS_DURATION :
	                        for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
	                            XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
	                            String strVal = XslTransformEvaluationHelper.getStrVal(argVal);
	                            XSDuration xsDuration = XSDuration.parseDuration(strVal);
	                            if (xsDuration != null) {
	                               argSequence.add(xsDuration);
	                               evalResultSequence = (new XSDuration()).constructor(argSequence); 
	                               evalResult = evalResultSequence.item(0);
	                            }
	                            else {
	                               throw new TransformerException("FORG0001 : An incorrectly formatted xs:duration value '" + 
	                                                                                               strVal + "' is present in the input.", srcLocator); 
	                            }
	                        }
	                        
	                        break;
	                    case Keywords.XS_YEAR_MONTH_DURATION :                   
	                        for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
	                            XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
	                            String strVal = XslTransformEvaluationHelper.getStrVal(argVal);
	                            XSDuration xsDuration = XSYearMonthDuration.parseYearMonthDuration(strVal);
	                            if (xsDuration != null) {
	                               argSequence.add(xsDuration);
	                               evalResultSequence = (new XSYearMonthDuration()).constructor(argSequence); 
	                               evalResult = evalResultSequence.item(0);
	                            }
	                            else {
	                               throw new TransformerException("FORG0001 : An incorrectly formatted xs:yearMonthDuration value '" + 
	                                                                                                strVal + "' is present in the input.", srcLocator); 
	                            }
	                        }
	                        
	                        break;
	                    case Keywords.XS_DAY_TIME_DURATION :                 
	                        for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
	                            XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
	                            String strVal = XslTransformEvaluationHelper.getStrVal(argVal);
	                            XSDuration xsDuration = XSDayTimeDuration.parseDayTimeDuration(strVal);
	                            if (xsDuration != null) {
	                               argSequence.add(xsDuration);
	                               evalResultSequence = (new XSDayTimeDuration()).constructor(argSequence); 
	                               evalResult = evalResultSequence.item(0);
	                            }
	                            else {
	                               throw new TransformerException("FORG0001 : An incorrectly formatted xs:dayTimeDuration value '" + 
	                                                                                                strVal + "' is present in the input.", srcLocator); 
	                            }                            
	                        }
	                        
	                        break;
	                    case Keywords.XS_TIME :                 
	                        for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
	                            XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
	                            String strVal = XslTransformEvaluationHelper.getStrVal(argVal);
	                            XSTime xsTime = XSTime.parseTime(strVal);
	                            if (xsTime != null) {
	                               argSequence.add(xsTime);
	                               evalResultSequence = (new XSTime()).constructor(argSequence); 
	                               evalResult = evalResultSequence.item(0);
	                            }
	                            else {
	                               throw new TransformerException("FORG0001 : An incorrectly formatted xs:time value '" + 
	                                                                                                         strVal + "' is present in the input.", srcLocator); 
	                            }                            
	                        }
	                        
	                        break;
	                        
	                    default:
	                       // no op
	                  }
	             }	             
	        }
	        else {
	           evalResult = expr.execute(xctxt);   
	        }
        }
        catch (TransformerException ex) {
            throw new TransformerException(ex.getMessage(), srcLocator); 
        }
        
        if ((evalResult == null) && (funcNamespace != null) && !(XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(funcNamespace) || 
        		                                                (Constants.S_EXTENSIONS_JAVA_URL).equals(funcNamespace))) {
        	HashMap stylesheetAvailableElems = stylesheetRoot.getAvailableElements();
        	if (stylesheetAvailableElems.containsKey(new QName(Constants.S_XSLNAMESPACEURL, Constants.ELEMNAME_IMPORT_SCHEMA_STRING))) {
        	   Node elemTemplateElem = stylesheetRoot.getFirstChildElem();
        	   
        	   while (elemTemplateElem != null && !(Constants.ELEMNAME_IMPORT_SCHEMA_STRING).equals(elemTemplateElem.getLocalName())) {   
        		   elemTemplateElem = elemTemplateElem.getNextSibling();
        	   }
        	   
        	   if ((Constants.ELEMNAME_IMPORT_SCHEMA_STRING).equals(elemTemplateElem.getLocalName())) {
        		   NodeList nodeList = elemTemplateElem.getChildNodes();
        		   Node xsSchemaTopMostNode = nodeList.item(0);
        		   
        		   // We shall attempt to use here, XercesJ's XMLSchemaLoader object
        		   // instance to try constructing an XSModel object instance which
        		   // is a compiled representation of an XML Schema document.
        		   XMLSchemaLoader xsLoader = new XMLSchemaLoader();
        		   
        		   XSModel xsModel = null;
        		   
        		   if (xsSchemaTopMostNode == null) {
        			   // We shall attempt here to construct, an XML Schema XSModel instance via schema document 
        			   // uri referred by an attribute value 'schema-location'. 
        			   NamedNodeMap importSchemaNodeAttributes = ((Element)elemTemplateElem).getAttributes();        			   
        			   
        			   if (importSchemaNodeAttributes != null) {
	        			   Node attrNode1 = importSchemaNodeAttributes.item(0);
	        			   Node attrNode2 = importSchemaNodeAttributes.item(1);	        			   	        			   	        			   
	        			   
	        			   try {
		        			   if (attrNode1 != null) {
		        				   URI inpUri = new URI(attrNode1.getNodeValue());
		        				   String stylesheetSystemId = srcLocator.getSystemId();
		        				   
		        				   if (!inpUri.isAbsolute() && (stylesheetSystemId != null)) {
		        			          URI resolvedUri = (new URI(stylesheetSystemId)).resolve(inpUri);
		        			          URL url = resolvedUri.toURL();
		        			          if (!"namespace".equals(attrNode1.getNodeName())) {
		        			             xsModel = xsLoader.loadURI(url.toString());
		        			          }
		        			       }
		        			   }
	        			   
	        			       if (attrNode2 != null && xsModel == null) {
	        			    	   URI inpUri = new URI(attrNode2.getNodeValue());
		        				   String stylesheetSystemId = srcLocator.getSystemId();
		        				   
		        				   if (!inpUri.isAbsolute() && (stylesheetSystemId != null)) {
		        			          URI resolvedUri = (new URI(stylesheetSystemId)).resolve(inpUri);
		        			          URL url = resolvedUri.toURL();
		        			          if ("schema-location".equals(attrNode2.getNodeName())) {
		        			             xsModel = xsLoader.loadURI(url.toString());
		        			          }
		        			       }	        				  
	        			       }
	        			       
	        			       if (xsModel != null) {
		        			       XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)(xsModel.getTypeDefinition(funcName, funcNamespace));
			        	           
			        	           XObject xsSimpleTypeInpObj = (funcExtFunction.getArg(0)).execute(xctxt);
			        	           xsSimpleTypeDecl.validate(xsSimpleTypeInpObj.str(), null, null);
			        	           
			        	           evalResult = new XSString(XS_VALID_TRUE);
	        			       }
	        			       else {
	        			    	   throw new javax.xml.transform.TransformerException("FODC0005 : While analyzing xsl:import-schema instruction, a compiled "
	        			    	   		                                                      + "representation of a an XML Schema document cannot be built. "
	        			    	   		                                                      + "Please check the stylesheet context to resolve.", srcLocator);
	        			       }
	        			   }
	        			   catch (URISyntaxException ex) {
	        				   throw new javax.xml.transform.TransformerException("FODC0005 : The uri analyzed, while processing xsl:import-schema "
	        				   		                                                          + "instruction is not a valid absolute uri, "
                                                                                              + "or cannot be resolved to an absolute uri.", srcLocator);   
	        			   }
	        			   catch (MalformedURLException ex) {
	        				   throw new javax.xml.transform.TransformerException("FODC0005 : The uri analyzed, while processing xsl:import-schema "
	        				   		                                                          + "instruction is not a valid absolute uri, "
                                                                                              + "or cannot be resolved to an absolute uri.", srcLocator); 
	        			   }
	        			   catch (InvalidDatatypeValueException ex) {
	        				   throw new TransformerException(ex.getMessage(), srcLocator);
	        			   }
        			   }
        		   }
        		   else {
        			   // We shall attempt here to construct, an XML Schema XSModel instance via lexical 
        			   // schema document information available as child contents of an element node 
        			   // xsl:import-schema.
        			   String xsSchemaStr = null;
        			   
	        		   try {
	        		       DOMImplementationLS domImplLS = (DOMImplementationLS)((DOMImplementationRegistry.
	        		    		                                                                        newInstance()).getDOMImplementation("LS"));
	        		       LSSerializer lsSerializer = domImplLS.createLSSerializer();
	        	           DOMConfiguration domConfig = lsSerializer.getDomConfig();
	        	           domConfig.setParameter(DOM_FORMAT_PRETTY_PRINT, Boolean.TRUE);
	        	           xsSchemaStr = lsSerializer.writeToString((Document)xsSchemaTopMostNode);
	        	           xsSchemaStr = xsSchemaStr.replaceFirst(UTF_16, UTF_8);
	        	           xsSchemaStr = xsSchemaStr.replaceFirst("schema", "schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"");
	        	           
	        	           String xsFileName = "xalan_1_" + System.currentTimeMillis() + ".xsd";
	        	           BufferedWriter buffWriter = new BufferedWriter(new FileWriter(xsFileName));
	        	           buffWriter.write(xsSchemaStr);	            	           
	        	           buffWriter.close();	            	           	            	           

	        	           xsModel = xsLoader.loadURI(xsFileName);
	        	           
	        	           // The purpose of temporary file created here has been achieved. We could delete this file now.
	        	           (new File(xsFileName)).delete();
	        	           
	        	           XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)(xsModel.getTypeDefinition(funcName, funcNamespace));
	        	           
	        	           XObject xsSimpleTypeInpObj = (funcExtFunction.getArg(0)).execute(xctxt);
	        	           xsSimpleTypeDecl.validate(xsSimpleTypeInpObj.str(), null, null);
	        	           
	        	           evalResult = new XSString(XS_VALID_TRUE); 
	        		   }
	        		   catch (InvalidDatatypeValueException ex) {
	        			   throw new TransformerException(ex.getMessage(), srcLocator); 
	        		   }
	        		   catch (Exception ex) {
	        			   throw new TransformerException(ex.getMessage(), srcLocator);
	        		   }
        		   }
        	   }
        	}
        }

        return evalResult;
        
    }
    
}
