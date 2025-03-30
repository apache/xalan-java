/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
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
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.regex.Pattern;
import org.w3c.dom.Document;

import xml.xpath31.processor.types.XSString;

/**
 * Implementation of an XPath 3.1 function fn:collection.
 * 
 * This function implementation, only supports local URI's
 * with file: scheme. If there's a need to use online web URI's
 * by this function implementation, one approach can be to code 
 * a java application writing data information within local 
 * files from online URI's, and as a second step use an XSL 
 * stylesheet using fn:collection function on those local file(s).
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
	 * A string array, specifying allowed file name extensions for Xalan-J
	 * fn:collection function's implementation.
	 */
	private static final String[] ALLOWED_FILE_EXTS = {XML, JSON, TEXT, CSV};

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
		
		ResultSequence resultSeq = new ResultSequence();
		
		XObject arg0XObj = m_arg0.execute(xctxt);
		String arg0StrValue = XslTransformEvaluationHelper.getStrVal(arg0XObj);
		
		/**
		 * Xalan-J fn:collection function's implementation as coded within this class,
		 * supports local file system URI's beginning with prefixes that can only be 
		 * 'file:///' or 'file:/' (both of which are equivalently correct). 
		 */
		
		if (arg0StrValue.startsWith("file:///")) {
			String fileSystemPathStr = arg0StrValue.substring(8);
			resultSeq = getResultSequenceFromFileSystemPathStr(fileSystemPathStr, xctxt);
		}
		else if (arg0StrValue.startsWith("file:/")) {
			String fileSystemPathStr = arg0StrValue.substring(6);			
			resultSeq = getResultSequenceFromFileSystemPathStr(fileSystemPathStr, xctxt);
		}
		
		result = resultSeq; 

		return result;
	}

	/**
	 * Given a file system local directory path string, interpreting the file name suffix as 
	 * regex and prefix as directory (currently no regex are assumed within directory name 
	 * prefix), return an XDM sequence containing ordered list of XML document nodes (corresponding 
	 * to XML document files), JSON maps or/and arrays (corresponding to JSON document files), or/and 
	 * strings correspond to other kinds of text files (for e.g, CSV or/and TSV). 
	 */
	private ResultSequence getResultSequenceFromFileSystemPathStr(String fileSystemPathStr, 
			                                                      XPathContext xctxt) throws javax.xml.transform.TransformerException {
		
		ResultSequence resultSeq = new ResultSequence();
		
		SourceLocator srcLocator = xctxt.getSAXLocator();
		
		String[] filePathPartStringArr = fileSystemPathStr.split(SLASH_CHAR_STR);
		
		String dirPathStr = "";			
		for (int idx = 0; idx < (filePathPartStringArr.length - 1); idx++) {
			dirPathStr += filePathPartStringArr[idx];
			if (idx != (filePathPartStringArr.length - 2)) {
				dirPathStr += SLASH_CHAR_STR;	
			}
		}
		
		File dirFileObj = new File(dirPathStr);
		String[] fileNameArr = dirFileObj.list();
		
		String fileNameRegex = filePathPartStringArr[filePathPartStringArr.length - 1];						
		
		for (int idx = 0; idx < fileNameArr.length; idx++) {
			String fileNameStr = fileNameArr[idx];
			int lastPeriodIdx = fileNameStr.lastIndexOf(PERIOD_CHAR);
			if (Pattern.matches(fileNameRegex, fileNameStr) && 
					                                isFileExtValid(fileNameStr.substring(lastPeriodIdx + 1))) {
				if (fileNameStr.endsWith(PERIOD_CHAR + XML)) {
					File fileObj = new File(dirPathStr, fileNameStr);  
					try {
						Document document = getXmlDomFromFileObj(fileObj);						
						DTMManager dtmMgr = xctxt.getDTMManager();
						DTM dtm = dtmMgr.getDTM(new DOMSource(document), true, null, false, false);
						int docNodeDtmHandle = dtm.getDocument();							
						XMLNodeCursorImpl xNodeSet = new XMLNodeCursorImpl(docNodeDtmHandle, dtmMgr);
						resultSeq.add(xNodeSet);
					}
					catch (Exception ex) {
						throw new javax.xml.transform.TransformerException("FODC0004 : An XPath dynamic error occured while "
								                                           		+ "constructing an XML document node for file " + 
								                                           		(dirPathStr + SLASH_CHAR_STR + fileNameStr) + " during processing "
								                                           		+ "with XPath function fn:collection.", srcLocator); 
					}
				}
				else if (fileNameStr.endsWith(PERIOD_CHAR + JSON)) {
					try {
						File fileObj = new File(dirPathStr + SLASH_CHAR_STR + fileNameStr);
						byte[] byteArr = Files.readAllBytes(Paths.get(fileObj.toURI()));
						String strValue = new String(byteArr);							
						XObject xObj = getJsonXdmValueFromStr(strValue);							
						resultSeq.add(xObj);
					}
					catch (Exception ex) {
						throw new javax.xml.transform.TransformerException("FODC0004 : An XPath dynamic error occured while "
                                                                           		+ "constructing an XDM map/array for JSON file " + (dirPathStr + 
                                                                           		SLASH_CHAR_STR + fileNameStr) + " during processing "
                                                                           		+ "with XPath function fn:collection." , srcLocator);
					}
				}
				else if (fileNameStr.endsWith(PERIOD_CHAR + TEXT) || fileNameStr.endsWith(PERIOD_CHAR + CSV)) {
					try {
						File fileObj = new File(dirPathStr + SLASH_CHAR_STR + fileNameStr);
						byte[] byteArr = Files.readAllBytes(Paths.get(fileObj.toURI()));
						String strValue = new String(byteArr);
						resultSeq.add(new XSString(strValue));
					}
					catch (Exception ex) {
						throw new javax.xml.transform.TransformerException("FODC0004 : An XPath dynamic error occured while "
                                                                           		+ "constructing string value from contents of text/csv file " + 
                                                                           		(dirPathStr + SLASH_CHAR_STR + fileNameStr) + " during processing "
                                                                           		+ "with XPath function fn:collection.", srcLocator);
					}
				}
				else {
					throw new javax.xml.transform.TransformerException("FODC0004 : Only XML (ext .xml), JSON (ext .json), text (ext .txt) and CSV (ext .csv)"
							                                           		+ "file documents are supported for processing by XPath function fn:collection. "
							                                           		+ "Invalid file "+ (dirPathStr + SLASH_CHAR_STR + fileNameStr) + " is provided as "
							                                           		+ "input for processing with function fn:collection.", srcLocator);
				}
			}
		}
		
		return resultSeq;
	}
	
	/**
     * Method definition to check whether the file extension string passed as 
     * an argument to this method, is allowed for the function implementation 
     * fn:collection.
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
	 * Method definition to get an XML DOM object instance, represented by 
	 * an XML document java.io.File object instance that is passed as an 
	 * argument to this method call.
	 */
	private Document getXmlDomFromFileObj(File fileObj) throws Exception {
		Document document = null;
		
		System.setProperty(Constants.XML_DOCUMENT_BUILDER_FACTORY_KEY, Constants.XML_DOCUMENT_BUILDER_FACTORY_VALUE);
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder dBuilder = dbf.newDocumentBuilder();
		
		document = dBuilder.parse(fileObj);
		
		return document;
	}
  
}
