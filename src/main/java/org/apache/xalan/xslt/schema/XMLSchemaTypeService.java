/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xalan.xslt.schema;

import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.Constants;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.xslt.util.XslTransformData;
import org.apache.xerces.dom.DOMInputImpl;
import org.apache.xerces.impl.dv.SchemaDVFactory;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.impl.xs.XSAttributeDecl;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.impl.xs.XSLoaderImpl;
import org.apache.xerces.util.SymbolHash;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xml.utils.QName;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.XSL3FunctionService;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

/**
 * Class definition, providing support for XSL 3, few 
 * XML Schema awareness utility methods.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XMLSchemaTypeService {
			
	/**
	 * Method definition, to populate XSL stylesheet's all the XML Schema
     * (i.e, from one or more XML Schema sources referred by an XSL stylesheet) 
     * in-scope schema types, in-scope element declarations, and in-scope attribute 
     * declarations, and store those within an XPath 3.1 static context.
	 * 
	 * @param stylesheetRoot						 An StylesheetRoot object instance
	 * @param xctxt                                  An XPathContext object instance
	 * @throws TransformerException
	 */
    public static void populateXSLAllInScopeSchemaInformation(StylesheetRoot stylesheetRoot, XPathContext xctxt) 
    																									       throws TransformerException {    	      	                	  
        
    	// An XSL stylesheet document's absolute uri
    	String xslStylesheetAbsUri = XslTransformData.m_xslSystemId;                                                         

    	try {
    		Map<QName, XSTypeDefinition> xslStylesheetInscopeTypeMap = new HashMap<QName, XSTypeDefinition>();

    		Map<QName, XSElementDeclaration> xslStylesheetInscopeElemDeclMap = new HashMap<QName, XSElementDeclaration>();

    		Map<QName, XSAttributeDeclaration> xslStylesheetInscopeAttrDeclMap = new HashMap<QName, XSAttributeDeclaration>();

    		ElemTemplateElement elemTemplateElement = stylesheetRoot.getFirstChildElem();        	          	  

    		XSLoaderImpl xsLoaderImpl = new XSLoaderImpl();

    		List<String> schemaLocationStrList = new ArrayList<String>();
    		List<String> namespaceAttrStrList = new ArrayList<String>();

    		int xslImportSchemaCount = -1;

    		while (elemTemplateElement != null) {	   
    			if ((Constants.ELEMNAME_IMPORT_SCHEMA_STRING).equals(elemTemplateElement.getLocalName())) {        			  
    				xslImportSchemaCount++;

    				NamedNodeMap xsImportSchemaNodeAttrList = ((Element)elemTemplateElement).getAttributes();    		      		          			  

    				if (xsImportSchemaNodeAttrList != null) {
    					Node attrNode1 = xsImportSchemaNodeAttrList.item(0);
    					Node attrNode2 = xsImportSchemaNodeAttrList.item(1);

    					String attrName1 = null;
    					String attrName2 = null;

    					if (attrNode1 != null) {
    						attrName1 = attrNode1.getNodeName();
    					}

    					if (attrNode2 != null) {
    						attrName2 = attrNode2.getNodeName();
    					}

    					if ((Constants.ATTRNAME_SCHEMA_LOCATION).equals(attrName1)) {
    						schemaLocationStrList.add(attrNode1.getNodeValue());
    					}
    					else if ((Constants.ATTRNAME_SCHEMA_LOCATION).equals(attrName2)) {
    						schemaLocationStrList.add(attrNode2.getNodeValue());
    					}

    					if ((Constants.ATTRNAME_NAMESPACE).equals(attrName2)) {
    						namespaceAttrStrList.add(attrNode2.getNodeValue());
    					}
    					else if ((Constants.ATTRNAME_NAMESPACE).equals(attrName1)) {
    						namespaceAttrStrList.add(attrNode1.getNodeValue());
    					}
    				}

    				NodeList nodeList = elemTemplateElement.getChildNodes();

    				int size1 = nodeList.getLength();

    				if (size1 == 0) {
    					if (schemaLocationStrList.size() > xslImportSchemaCount) {
    						URI uri1 = new URI(schemaLocationStrList.get(xslImportSchemaCount));

    						if (!uri1.isAbsolute() && (xslStylesheetAbsUri != null)) {
    							URI resolvedUri = (new URI(xslStylesheetAbsUri)).resolve(schemaLocationStrList.get(xslImportSchemaCount));
    							URL url = resolvedUri.toURL();

    							schemaLocationStrList.set(xslImportSchemaCount, url.toString());
    						}
    					}
    					else {
    						throw new TransformerException("XTSE0215 : An XSL instruction 'import-schema' without child content, must "
    								                                                                                                  + "have 'schema-location' attribute."); 
    					}
    				}
    				else if (size1 == 1) {
    					Node xsSchemaNode = nodeList.item(0);
    					String nodeNameStr = xsSchemaNode.getNodeName();

    					QName qName = new QName(nodeNameStr, stylesheetRoot);

    					String localName = qName.getLocalName();
    					String nsUri = qName.getNamespaceURI();

    					if ((Constants.ATTRNAME_SCHEMA).equals(localName) && (XMLConstants.W3C_XML_SCHEMA_NS_URI).equals(nsUri)) {
    						if (schemaLocationStrList.size() <= xslImportSchemaCount) {
    							DOMImplementationLS domImplLS = (DOMImplementationLS)((DOMImplementationRegistry.newInstance()).getDOMImplementation("LS"));

    							LSSerializer lsSerializer = domImplLS.createLSSerializer();

    							DOMConfiguration domConfig = lsSerializer.getDomConfig();
    							domConfig.setParameter(XSL3FunctionService.XML_DOM_FORMAT_PRETTY_PRINT, Boolean.TRUE);

    							String xmlSchemaDocumentStr = lsSerializer.writeToString((Document)xsSchemaNode);
    							xmlSchemaDocumentStr = xmlSchemaDocumentStr.replaceFirst(XSL3FunctionService.UTF_16, XSL3FunctionService.UTF_8);
    							xmlSchemaDocumentStr = xmlSchemaDocumentStr.replaceFirst(Constants.ATTRNAME_SCHEMA, "schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"");

    							DOMInputImpl lsInput = new DOMInputImpl();
    							lsInput.setCharacterStream(new StringReader(xmlSchemaDocumentStr));

    							XSModel xsModel = xsLoaderImpl.load(lsInput);

    							if (namespaceAttrStrList.size() > xslImportSchemaCount) {
    								StringList strList = xsModel.getNamespaces();

    								if (strList != null) {
    									String namespaceAttrStr = namespaceAttrStrList.get(xslImportSchemaCount);

    									if (!strList.contains(namespaceAttrStrList.get(xslImportSchemaCount))) {
    										throw new TransformerException("XTSE0215 : An XSL instruction 'import-schema' specifies an attribute 'namespace' with "
																								    												              + "non-null value '" + namespaceAttrStr + "', but an "
																								    												              + "XML schema attribute 'targetNamespace' string value is not equal "
																								    												              + "to this value.");
    									}
    								}
    								else {
    									throw new TransformerException("XTSE0215 : An XSL instruction 'import-schema' specifies an attribute 'namespace' with "
																						    											                      + "non-null value, but an "
																						    											                      + "XML schema is in no namespace."); 
    								}
    							}

    							populateXSLInScopeSchemaInformation(xsModel, xslStylesheetInscopeTypeMap, xslStylesheetInscopeElemDeclMap,
    																								      xslStylesheetInscopeAttrDeclMap);
    						}
    						else {
    							throw new TransformerException("XTSE0215 : An XSL instruction 'import-schema' cannot have both a child "
																								    								   + "XML Schema 'schema' node, "
																								    								   + "and a 'schema-location' attribute."); 
    						}
    					}
    					else {
    						throw new TransformerException("XTSE0215 : An XSL instruction 'import-schema' has a child element that is not an XML Schema node 'element'."); 
    					}
    				}
    				else {
    					throw new TransformerException("XTSE0215 : An XSL instruction 'import-schema' cannot have more than one child elements. "
																							    							                    + "An XSL instruction 'import-schema' "
																							    							                    + "first child element should be an XML Schema "
																							    							                    + "'schema' element."); 
    				}
    			}

    			elemTemplateElement = elemTemplateElement.getNextSiblingElem();
    		}

    		if (schemaLocationStrList.size() > 0) {
    			int size2 = schemaLocationStrList.size();

    			for (int idx = 0; idx < size2; idx++) {    		      		      		     
    				String xsSchemaSystemId = schemaLocationStrList.get(idx);

    				XSModel xsModel = xsLoaderImpl.loadURI(xsSchemaSystemId);

    				if (namespaceAttrStrList.size() > 0) {
    					StringList strList = xsModel.getNamespaces();

    					if (strList != null) {
    						String namespaceAttrStr = namespaceAttrStrList.get(idx);

    						if (!strList.contains(namespaceAttrStr)) {
    							throw new TransformerException("XTSE0215 : An XSL instruction 'import-schema' specifies an attribute 'namespace' with "
																					    									                          + "non-null value '" + namespaceAttrStr + "', but an "
																					    									                          + "XML schema attribute 'targetNamespace' string value is not equal "
																					    									                          + "to this value.");
    						}
    					}
    					else {
    						throw new TransformerException("XTSE0215 : An XSL instruction 'import-schema' specifies an attribute 'namespace' with "
																											    								  + "non-null value, but an "
																											    								  + "XML schema is in no namespace."); 
    					}
    				}

    				populateXSLInScopeSchemaInformation(xsModel, xslStylesheetInscopeTypeMap, xslStylesheetInscopeElemDeclMap,
    						                                                                  xslStylesheetInscopeAttrDeclMap);
    			}    		  
    		}

    		if (xslStylesheetInscopeTypeMap.size() > 0) {
    			xctxt.setInScopeSchemaTypes(xslStylesheetInscopeTypeMap);
    			xctxt.setInScopeElementDeclarations(xslStylesheetInscopeElemDeclMap);
    			xctxt.setInScopeAttributeDeclarations(xslStylesheetInscopeAttrDeclMap);
    		}
    		else {
    			/**
    			 * An XSL stylesheet doesn't have xsl:import-schema instruction.
    			 * Populate XPath 3.1 static context with XML Schema built-in schema
    			 * simple type information, and an XML Schema complex type 'anyType'.
    			 */

    			SchemaDVFactory schemaDvFactory = SchemaDVFactory.getInstance();
    			SymbolHash xsBuiltInTypeHashTable = schemaDvFactory.getBuiltInTypes();

    			Object[] objArray1 = xsBuiltInTypeHashTable.getEntries();

    			for (int idx = 1; idx <= objArray1.length; idx += 2) {
    				XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)(objArray1[idx]);

    				String xsTypeName = xsSimpleTypeDecl.getTypeName();
    				String xsTypeNs = xsSimpleTypeDecl.getTypeNamespace();                	
    				QName qName = new QName(xsTypeNs, xsTypeName);

    				xslStylesheetInscopeTypeMap.put(qName, xsSimpleTypeDecl);
    			}
    			
    			xslStylesheetInscopeTypeMap.put(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "untypedAtomic"), new XSSimpleTypeDecl());
    			xslStylesheetInscopeTypeMap.put(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "anyAtomicType"), new XSSimpleTypeDecl());
    			xslStylesheetInscopeTypeMap.put(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "untyped"), new XSComplexTypeDecl());
    			
    			xslStylesheetInscopeTypeMap.put(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "dayTimeDuration"), new XSSimpleTypeDecl());
    			xslStylesheetInscopeTypeMap.put(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "yearMonthDuration"), new XSSimpleTypeDecl());
    			
    			xslStylesheetInscopeTypeMap.put(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "anyType"), new XSComplexTypeDecl());

    			xctxt.setInScopeSchemaTypes(xslStylesheetInscopeTypeMap);
    		}
    	}
    	catch(Exception ex) {
    		if (ex instanceof TransformerException) {
    			throw (TransformerException)ex;  
    		}
    		else {
    			throw new TransformerException("XPST0003 : An XSL transformation error occured, while populating "
																					    					     + "XPath 3.1 static context "
																					    					     + "with in-scope schema information. " + ex.getMessage());
    		}
    	}
    }
    
    /**
     * Method definition, to populate XSL in-scope schema information
     * to be made available within XPath 3.1 static context,using XML Schema 
     * type, element and attribute information, from an XML Schema document.
     * 
     * @param xsModel                                Compiled XSModel object instance,
     *                                               corresponding to an XML Schema document.
     * @param xslStylesheetInscopeTypeMap            java.util.Map object instance, that shall
     *                                               contain XSL in-scope schema types.
     * @param xslStylesheetInscopeElemDeclMap        java.util.Map object instance, that shall
     *                                               contain XSL in-scope element declarations.
     * @param xslStylesheetInscopeAttrDeclMap        java.util.Map object instance, that shall
     *                                               contain XSL in-scope attribute declarations.
     */
    private static void populateXSLInScopeSchemaInformation(XSModel xsModel, Map<QName, XSTypeDefinition> xslStylesheetInscopeTypeMap,
                                                                             Map<QName, XSElementDeclaration> xslStylesheetInscopeElemDeclMap,
                                                                             Map<QName, XSAttributeDeclaration> xslStylesheetInscopeAttrDeclMap) {

    	// An object instance xsNamedMap1 includes, both XML Schema built-in and 
    	// user defined types.
    	XSNamedMap xsNamedMap1 = xsModel.getComponents(XSTypeDefinition.SIMPLE_TYPE);

    	int size1 = xsNamedMap1.getLength();

    	for (int idx2 = 0; idx2 < size1; idx2++) {
    		XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)(xsNamedMap1.item(idx2));

    		String xsTypeName = xsSimpleTypeDecl.getTypeName();
    		String xsTypeNs = xsSimpleTypeDecl.getTypeNamespace();                	
    		QName qName = new QName(xsTypeNs, xsTypeName);

    		xslStylesheetInscopeTypeMap.put(qName, xsSimpleTypeDecl);
    	}
    	
    	xslStylesheetInscopeTypeMap.put(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "untypedAtomic"), new XSSimpleTypeDecl());
		xslStylesheetInscopeTypeMap.put(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "anyAtomicType"), new XSSimpleTypeDecl());
		xslStylesheetInscopeTypeMap.put(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "untyped"), new XSComplexTypeDecl());
		
		xslStylesheetInscopeTypeMap.put(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "dayTimeDuration"), new XSSimpleTypeDecl());
		xslStylesheetInscopeTypeMap.put(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "yearMonthDuration"), new XSSimpleTypeDecl());

    	XSNamedMap xsNamedMap2 = xsModel.getComponents(XSTypeDefinition.COMPLEX_TYPE);

    	int size2 = xsNamedMap2.getLength();

    	for (int idx2 = 0; idx2 < size2; idx2++) {
    		XSComplexTypeDecl xsComplexTypeDecl = (XSComplexTypeDecl)(xsNamedMap2.item(idx2));

    		String xsTypeName = xsComplexTypeDecl.getTypeName();
    		String xsTypeNs = xsComplexTypeDecl.getTypeNamespace();                	
    		QName qName = new QName(xsTypeNs, xsTypeName);

    		xslStylesheetInscopeTypeMap.put(qName, xsComplexTypeDecl);
    	}

    	XSNamedMap xsNamedMap3 = xsModel.getComponents(XSConstants.ELEMENT_DECLARATION);

    	int size3 = xsNamedMap3.getLength();

    	for (int idx2 = 0; idx2 < size3; idx2++) {
    		XSElementDecl xsElementDecl = (XSElementDecl)(xsNamedMap3.item(idx2));

    		String elemName1 = xsElementDecl.getName();
    		String elemTargetnamespace1 = xsElementDecl.getNamespace();
    		QName qName = new QName(elemTargetnamespace1, elemName1);

    		xslStylesheetInscopeElemDeclMap.put(qName, xsElementDecl);
    	}

    	XSNamedMap xsNamedMap4 = xsModel.getComponents(XSConstants.ATTRIBUTE_DECLARATION);

    	int size4 = xsNamedMap4.getLength();

    	for (int idx2 = 0; idx2 < size4; idx2++) {
    		XSAttributeDecl xsAttrDecl = (XSAttributeDecl)(xsNamedMap4.item(idx2));

    		String attrName1 = xsAttrDecl.getName();
    		String attrTargetnamespace1 = xsAttrDecl.getNamespace();
    		QName qName = new QName(attrTargetnamespace1, attrName1);

    		xslStylesheetInscopeAttrDeclMap.put(qName, xsAttrDecl);
    	}    	
    }

}
