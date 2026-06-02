/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xalan.tests.w3c.xslt3;

import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xalan.tests.w3c.xpath3.W3CXPath3TestsUtil;
import org.apache.xml.utils.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class implementation to produce W3C XSLT 3.0 test suite's summarized 
 * XML result document for Xalan-J XSLT 3.0 development implementation.
 * 
 * Following are sequence of steps to use this class:
 * 
 * 1) Run W3C XSLT 3.0 test suite for Xalan-J implementation using class 
 *    org.apache.xalan.tests.w3c.xslt3.W3CXSLT3Tests.
 * 2) Run this class to produce final aggregated results for W3C XSLT 3.0 
 *    test suite for Xalan-J. 
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class W3CXSLT3TestSuiteXalanResultSummarize {
	
	/**
	 * The value of this class field, need to conform to the local host 
	 * where this class shall run.
	 */
	private static final String XALAN_W3C_XSLT3_TESTSUITE_RESULT_FOLDER_ROOT = "d:\\eclipseWorkspaces\\xalanj\\xalan-j_xslt3.0_mvn\\src\\test\\java\\org\\apache\\xalan\\tests\\w3c\\xslt3\\result";
	
	private static final String RESULT_FILE_NAME = "w3c_xslt3_testsuite_xalan-j_result.xml";
	
	private static final String XSL_SERIALIZATION_INDENT_YES = "yes";
	
	private static final String XSL_SERIALIZATION_INDENT_KEY = "{http://xml.apache.org/xslt}indent-amount";
	
	private static final String W3C_XSLT3_TEST_SUITE_RESULTS = "W3C XSLT 3.0 test suite results";
	
	private static final int XSL_SERIALIZATION_INDENT_VALUE = 2;

	/**
	 * Main method of this class.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		W3CXSLT3TestSuiteXalanResultSummarize applnObj = new W3CXSLT3TestSuiteXalanResultSummarize();
		
		File folderRoot = new File(XALAN_W3C_XSLT3_TESTSUITE_RESULT_FOLDER_ROOT);		
		applnObj.summarizeTestSuiteResult(folderRoot);
	}
	
	/**
	 * Method definition, to implement W3C XSLT 3.0 test suite result 
	 * aggregation for Xalan-J implementation's conformance.
	 */
	private void summarizeTestSuiteResult(File folderRoot) {
		System.setProperty(Constants.XML_DOCUMENT_BUILDER_FACTORY_KEY, Constants.XML_DOCUMENT_BUILDER_FACTORY_VALUE);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		
		DecimalFormat decimalFormat = new DecimalFormat("#." + getStrForZeros(2));

		URI uri1 = null;
		String pathStr = null;
		try {
			DocumentBuilder docBuilder = dbf.newDocumentBuilder();
			Document document = docBuilder.newDocument();
			Element testResultElem = document.createElement(W3CXPath3TestsUtil.TESTRESULT);
			testResultElem.setAttribute(W3CXPath3TestsUtil.DESC, W3C_XSLT3_TEST_SUITE_RESULTS);
			testResultElem.setAttribute(W3CXPath3TestsUtil.XSLT_PROCESSOR, W3CXPath3TestsUtil.XSLT_PROC_NAME);
			String testRunDateStrValue = getDateISOString(new Date());
			testResultElem.setAttribute(W3CXPath3TestsUtil.DATETIME, testRunDateStrValue);

			String[] strArray = folderRoot.list();
			int totalCount = 0;
			int totalPass = 0;
			int totalFail = 0;
			int totalSkipped = 0;
			for (int idx = 0; idx < strArray.length; idx++) {
				String testSetKindName = strArray[idx];	                       // e.g, decl, expr etc				
				pathStr = folderRoot + File.separator + testSetKindName;
				
				if (pathStr.contains("xslt3" + File.separator + "result" + File.separator + "xalan")) {
				   continue;	
				}
				
				File file = new File(pathStr);
				if (file.isDirectory()) {
					String[] fileNames = file.list();
					Element testSetKindElem = document.createElement(testSetKindName);
					for (int idx2 = 0; idx2 < fileNames.length; idx2++) {
						String fileName = fileNames[idx2];
						String localFilePath = (pathStr + File.separator + fileName);						
						File file1 = new File(localFilePath);
						uri1 = file1.toURI();						
						Document testSetResultDoc = docBuilder.parse(uri1.toString());
						Element docElem = testSetResultDoc.getDocumentElement();
						String testSetName = docElem.getAttribute(W3CXPath3TestsUtil.NAME);
						int run = Integer.valueOf(docElem.getAttribute(W3CXPath3TestsUtil.RUN));
						totalCount += run; 
						int pass = Integer.valueOf(docElem.getAttribute(W3CXPath3TestsUtil.PASS));
						totalPass += pass;
						int fail = Integer.valueOf(docElem.getAttribute(W3CXPath3TestsUtil.FAIL));
						totalFail += fail;
						int skipped = Integer.valueOf(docElem.getAttribute(W3CXPath3TestsUtil.SKIPPED));
						totalSkipped += skipped;
						double successPer = ((pass / (double)run)) * 100;
						double successPerDbl = (Double.valueOf(decimalFormat.format(Double.valueOf(String.valueOf(successPer))))).doubleValue();
						Element testSetElem = document.createElement(testSetName);
						testSetElem.setAttribute(W3CXPath3TestsUtil.RUN, String.valueOf(run));
						testSetElem.setAttribute(W3CXPath3TestsUtil.PASS, String.valueOf(pass));
						testSetElem.setAttribute(W3CXPath3TestsUtil.FAIL, String.valueOf(fail));
						testSetElem.setAttribute(W3CXPath3TestsUtil.SKIPPED, String.valueOf(skipped));
						testSetElem.setAttribute(W3CXPath3TestsUtil.SUCCESS, String.valueOf(successPerDbl) + "%");
						testSetKindElem.appendChild(testSetElem);
					}
					
					testResultElem.appendChild(testSetKindElem);
				}
			}
			
			double totalSuccessPer = ((totalPass / (double)totalCount)) * 100;
			double totalSuccessPerDbl = (Double.valueOf(decimalFormat.format(Double.valueOf(String.valueOf(totalSuccessPer))))).doubleValue();
			testResultElem.setAttribute(W3CXPath3TestsUtil.RUN, String.valueOf(totalCount));
			testResultElem.setAttribute(W3CXPath3TestsUtil.PASS, String.valueOf(totalPass));
			testResultElem.setAttribute(W3CXPath3TestsUtil.FAIL, String.valueOf(totalFail));
			testResultElem.setAttribute(W3CXPath3TestsUtil.SKIPPED, String.valueOf(totalSkipped));
			testResultElem.setAttribute(W3CXPath3TestsUtil.SUCCESS, String.valueOf(totalSuccessPerDbl) + "%");
			
			document.appendChild(testResultElem);
			
			// Serialize an XML document object to file
			
			TransformerFactory xslTransformFactory = TransformerFactory.newInstance();
			Transformer transformer = xslTransformFactory.newTransformer();
			
			transformer.setOutputProperty(OutputKeys.INDENT, XSL_SERIALIZATION_INDENT_YES);
			transformer.setOutputProperty(XSL_SERIALIZATION_INDENT_KEY, String.valueOf(XSL_SERIALIZATION_INDENT_VALUE));
			
			DOMSource domSource = new DOMSource(document);
			FileWriter fileWriter = new FileWriter(new File(XALAN_W3C_XSLT3_TESTSUITE_RESULT_FOLDER_ROOT + File.separator + RESULT_FILE_NAME));
			StreamResult streamResult = new StreamResult(fileWriter);
			
			transformer.transform(domSource, streamResult);
		}
		catch (Exception ex) {
		    ex.printStackTrace();
		}
	}
	
	/**
	 * Method definition, to get string value comprising as many 
	 * characters '0' as the supplied non-negative integer value.
	 * We use the string value returned by this method, to construct 
     * a java.text.DecimalFormat object instance.    
	 * 
	 * @param strSize               Non-negative integer value
	 * @return                      The computed string value
	 */
    private String getStrForZeros(int strSize) {
       
       String result = "";
       
       for (int idx = 0; idx < strSize; idx++) {
          result = result + "0";  
       }
       
       return result;
    }
    
    /**
     * Method definition, to get an ISO formatted date string for
     * the supplied java.util.Date value.
     *  
     * @param date				The supplied date object value
     * @return					The formatted date string
     */
    private String getDateISOString(Date dateValue) {
    	
    	String result = null;
    	
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        result = sdf.format(dateValue); 
        
        return result;
    }

}
