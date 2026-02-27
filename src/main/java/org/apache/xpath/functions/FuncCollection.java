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
package org.apache.xpath.functions;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.dom.DOMSource;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.utils.Constants;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.json.JsonFunction;
import org.apache.xpath.functions.json.XSLJsonConstants;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.regex.Pattern;
import org.apache.xpath.types.XSBase64Binary;
import org.w3c.dom.Document;

import xml.xpath31.processor.types.XSString;

/**
 * Implementation of XPath 3.1 function fn:collection.
 * 
 * This implementation supports XPath function fn:collection
 * argument that are local file system uri strings beginning 
 * with file:///, or file:/.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncCollection extends JsonFunction
{

	private static final long serialVersionUID = -5613347556907647341L;
	
	private static final String XML = "xml";
	
	private static final String JSON = "json";
	
	private static final String TEXT = "txt";
	
	private static final String CSV = "csv";
	
	private static final char PERIOD_CHAR = '.';
	
	private static final String SLASH_CHAR_STR = "/";
	
	/**
	 * A string array, specifying the allowed file name extensions 
	 * for, Xalan's XPath function fn:collection's implementation.
	 */
	private static final String[] ALLOWED_FILE_EXTS = { XML, JSON, TEXT, CSV };
	
	/**
     * Default constructor.
     */
    public FuncCollection() {
    	m_defined_arity = new Short[] { 0, 1 };	
    }

	/**
	 * Evaluate the function. The function must return a valid object.
	 * 
	 * @param xctxt The current execution context.
	 * @return A valid XObject.
	 *
	 * @throws javax.xml.transform.TransformerException
	 */
	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
	{          
		XObject result = null;
		
		SourceLocator srcLocator = xctxt.getSAXLocator();
		
		ResultSequence xpathDefCollectionSeq = xctxt.getDefaultCollection();
		
		if (m_arg1 != null) {
		   throw new javax.xml.transform.TransformerException("XPST0017 : An XPath function 'collection' may have "
		   		                                                                                      + "either zero or one argument.", srcLocator);
		}
		
		XObject arg0XObj = null;
		
        if (m_arg0 != null) {
           arg0XObj = m_arg0.execute(xctxt);
		}
        else if (xpathDefCollectionSeq.size() == 0) {
        	throw new javax.xml.transform.TransformerException("FODC0002 : An XPath default collection has not been defined.", srcLocator);
        }
        else {
        	// We need to use XPath context's default collection, 
        	// if it's available, which is currently empty.
        }
        
        if ((arg0XObj instanceof ResultSequence) && (((ResultSequence)arg0XObj).size() == 0) && (xpathDefCollectionSeq.size() == 0)) {
            throw new javax.xml.transform.TransformerException("FODC0002 : An XPath default collection has not been defined.", srcLocator);
        }
        else {
        	// We need to use XPath context's default collection, 
        	// if it's available, which is currently empty.
        }
		
		String arg0StrValue = XslTransformEvaluationHelper.getStrVal(arg0XObj);
		
		String fileSystemPathStr = null;
		
		if (arg0StrValue.startsWith("file:///")) {
			fileSystemPathStr = arg0StrValue.substring(8);
		}
		else if (arg0StrValue.startsWith("file:/")) {
			fileSystemPathStr = arg0StrValue.substring(6);			
		}
		
		result = getResultSequenceFromFileSystemPathStr(fileSystemPathStr, xctxt);

		return result;
	}
	
	/**
	 * Method definition, to get XPath function fn:collection's result as ResultSequence 
	 * object instance, using the supplied file system path string. 
	 * 
	 * The supplied file system path string, has two components, as follows:
	 * 
	 * 1) A directory path prefix interpreted as string literal, that occurs
	 *    before the supplied string value's last character '/'.
	 * 2) A trailing file name suffix string, which is interpreted as regex, 
	 *    with XPath 3.1 regex syntax. This can also be a string literal.
	 * 
	 * This function, returns an xdm sequence containing a mix of XML document nodes 
	 * (corresponding to XML document files), JSON maps and arrays (corresponding to 
	 * JSON document files), and string values correspond to other types of text files.
	 * 
	 * @param fileSystemPathStr                              The file system path string, from which 
	 *                                                       to evaluate XPath function fn:collection's 
	 *                                                       result. 
	 * @param xctxt                                          An XPath context object.
	 * @return                                               A ResultSequence object instance
	 * @throws javax.xml.transform.TransformerException
	 */
	private ResultSequence getResultSequenceFromFileSystemPathStr(String fileSystemPathStr, 
			                                                      XPathContext xctxt) throws javax.xml.transform.TransformerException {
		
		ResultSequence resultSeq = new ResultSequence();
		
		SourceLocator srcLocator = xctxt.getSAXLocator();
		
		String[] filePathPartStringArr = fileSystemPathStr.split(SLASH_CHAR_STR);
		
		String fileSystemDirPathStr = "";
		int arrLength = filePathPartStringArr.length;
		for (int idx = 0; idx < (arrLength - 1); idx++) {
			fileSystemDirPathStr += filePathPartStringArr[idx];
			if (idx != (filePathPartStringArr.length - 2)) {
				fileSystemDirPathStr += SLASH_CHAR_STR;	
			}
		}
		
		File dirFileObj = new File(fileSystemDirPathStr);
		String[] fileNameArr = dirFileObj.list();
		
		String fileNameRegex = filePathPartStringArr[arrLength - 1];						
		
		int arrLength2 = fileNameArr.length;
		
		for (int idx = 0; idx < arrLength2; idx++) {
			String fileNameStr = fileNameArr[idx];
			int lastPeriodIdx = fileNameStr.lastIndexOf(PERIOD_CHAR);
			if (Pattern.matches(fileNameRegex, fileNameStr) && 
					                                isFileExtValid(fileNameStr.substring(lastPeriodIdx + 1))) {
				if (fileNameStr.endsWith(PERIOD_CHAR + XML)) {
					File fileObj = new File(fileSystemDirPathStr, fileNameStr);  
					try {
						Document document = getXmlDocumentFromFile(fileObj);						
						DTMManager dtmMgr = xctxt.getDTMManager();
						DTM dtm = dtmMgr.getDTM(new DOMSource(document), true, null, false, false);
						int docNodeDtmHandle = dtm.getDocument();							
						XMLNodeCursorImpl xNodeSet = new XMLNodeCursorImpl(docNodeDtmHandle, dtmMgr);
						resultSeq.add(xNodeSet);
					}
					catch (Exception ex) {
						throw new javax.xml.transform.TransformerException("FODC0004 : An XPath dynamic error occured while "
														                                           		+ "constructing an XML document node for file " + 
														                                           		(fileSystemDirPathStr + SLASH_CHAR_STR + fileNameStr) + ", while processing "
														                                           		+ "with XPath function 'collection'.", srcLocator); 
					}
				}
				else if (fileNameStr.endsWith(PERIOD_CHAR + JSON)) {
					try {
						File fileObj = new File(fileSystemDirPathStr + SLASH_CHAR_STR + fileNameStr);
						byte[] byteArr = Files.readAllBytes(Paths.get(fileObj.toURI()));
						String strValue = new String(byteArr);							
						XObject xObj = getJsonXdmValueFromStr(strValue, false, XSLJsonConstants.DUPLICATES_USE_FIRST);							
						resultSeq.add(xObj);
					}
					catch (Exception ex) {
						throw new javax.xml.transform.TransformerException("FODC0004 : An XPath dynamic error occured while "
						                                                                           		+ "constructing an xdm map/array for JSON file " + (fileSystemDirPathStr + 
						                                                                           		SLASH_CHAR_STR + fileNameStr) + ", while processing "
						                                                                           		+ "with XPath function 'collection'." , srcLocator);
					}
				}
				else if (fileNameStr.endsWith(PERIOD_CHAR + TEXT) || fileNameStr.endsWith(PERIOD_CHAR + CSV)) {
					try {
						File fileObj = new File(fileSystemDirPathStr + SLASH_CHAR_STR + fileNameStr);
						byte[] byteArr = Files.readAllBytes(Paths.get(fileObj.toURI()));
						String strValue = new String(byteArr);
						resultSeq.add(new XSString(strValue));
					}
					catch (Exception ex) {
						throw new javax.xml.transform.TransformerException("FODC0004 : An XPath dynamic error occured while "
						                                                                           		+ "constructing string value from contents of text/csv file " + 
						                                                                           		(fileSystemDirPathStr + SLASH_CHAR_STR + fileNameStr) + ", while processing "
						                                                                           		+ "with XPath function 'collection'.", srcLocator);
					}
				}
				else {
					try {
						File fileObj = new File(fileSystemDirPathStr + SLASH_CHAR_STR + fileNameStr);
						byte[] byteArr = Files.readAllBytes(Paths.get(fileObj.toURI()));
						String strValue = new String(byteArr);
						XSBase64Binary xsBase64Binary = new XSBase64Binary(strValue);
						resultSeq.add(xsBase64Binary);
					}
					catch (Exception ex) {
						throw new javax.xml.transform.TransformerException("FODC0004 : The XPath function 'collection''s evaluation, only supports file types XML (ext .xml), "
                                                                                                       + "JSON (ext .json), text (ext .txt), CSV (ext .csv) or binary file "
                                                                                                       + "data encoded as with XML Schema type base64Binary. The supplied "
                                                                                                       + "file with name " + (fileSystemDirPathStr + SLASH_CHAR_STR + fileNameStr) 
                                                                                                       + " is invalid.", srcLocator); 
					}															
				}
			}
		}
		
		return resultSeq;
	}
	
	/**
	 * Method definition, to check whether the file name extension 
     * string supplied as an argument to this method, is allowed 
     * for XPath function fn:collection's implementation.
	 * 
	 * @param fileExtStr				The supplied file name extension 
	 *                                  string.
	 * @return                          Boolean value true, or false
	 */
	private boolean isFileExtValid(String fileExtStr) {
		boolean result = false;
		
		List<String> allowedFileExtList = Arrays.asList(ALLOWED_FILE_EXTS);
		if (allowedFileExtList.contains(fileExtStr)) {
			result = true;
		}
		
		return result;
	}
	
	/**
	 * Method definition, to get an XML document object instance, using
	 * the supplied java.io.File object.
	 * 
	 * @param file						 A java.io.File object instance
	 * @return                           An XML document object instance
	 * @throws Exception
	 */
	private Document getXmlDocumentFromFile(File file) throws Exception {		
		Document document = null;
		
		System.setProperty(Constants.XML_DOCUMENT_BUILDER_FACTORY_KEY, Constants.XML_DOCUMENT_BUILDER_FACTORY_VALUE);
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder dBuilder = dbf.newDocumentBuilder();
		
		document = dBuilder.parse(file);
		
		return document;
	}
  
}
