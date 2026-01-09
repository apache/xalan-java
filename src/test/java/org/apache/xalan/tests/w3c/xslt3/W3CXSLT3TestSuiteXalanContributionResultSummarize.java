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

import org.apache.xml.utils.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class implementation to produce W3C XSLT 3.0 test suite's summarized 
 * XML result document, for Xalan contribution, for Xalan-J XSLT 3.0 
 * development implementation.
 * 
 * Please note: This class needs to be run, after running the class
 * W3CXSLT3XalanContributionTests, that produces the overall Xalan
 * XSLT 3.0 test suite result status document w3c_xslt3_testsuite_xalan_contribution_result.xml. 
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class W3CXSLT3TestSuiteXalanContributionResultSummarize {
	
	/**
	 * The value of this class field, need to conform to the local host 
	 * where this class shall run.
	 */
	private static final String W3C_XSLT3_XALAN_TESTSUITE_RESULT_FOLDER_ROOT = "d:\\eclipseWorkspaces\\xalanj\\xalan-j_xslt3.0_mvn\\src\\test\\java\\org\\apache\\xalan\\tests\\w3c\\xslt3\\result\\xalan";
	
	private static final String RESULT_FILE_NAME = "w3c_xslt3_testsuite_xalan_contribution_result.xml";
	
	private static final String XSL_SERIALIZATION_INDENT_YES = "yes";
	
	private static final String XSL_SERIALIZATION_INDENT_KEY = "{http://xml.apache.org/xslt}indent-amount";
	
	private static final int XSL_SERIALIZATION_INDENT_VALUE = 2;

	/**
	 * Main method of this class.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		W3CXSLT3TestSuiteXalanContributionResultSummarize applnObj = new W3CXSLT3TestSuiteXalanContributionResultSummarize();
		
		File folderRoot = new File(W3C_XSLT3_XALAN_TESTSUITE_RESULT_FOLDER_ROOT);		
		applnObj.summarizeTestSuiteResult(folderRoot);
	}
	
	/**
	 * Method definition implementing W3C XSLT 3.0 test suite's result 
	 * aggregation for Xalan-J implementation's conformance.
	 */
	private void summarizeTestSuiteResult(File folderRoot) {
		System.setProperty(Constants.XML_DOCUMENT_BUILDER_FACTORY_KEY, Constants.XML_DOCUMENT_BUILDER_FACTORY_VALUE);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		
		DecimalFormat decimalFormat = new DecimalFormat("#." + getStrForZeros(2));

		try {
			DocumentBuilder docBuilder = dbf.newDocumentBuilder();
			Document document = docBuilder.newDocument();
			Element testResultElem = document.createElement("testResult");
			testResultElem.setAttribute("desc", "W3C XSLT 3.0 test suite results");
			testResultElem.setAttribute("xslt_processor", "Apache Xalan XSLT 3.0 development code");
			String testRunDateStrValue = getDateISOString(new Date());
			testResultElem.setAttribute("dateTime", testRunDateStrValue);

			String[] strArray = folderRoot.list();
			int totalCount = 0;
			int totalPass = 0;
			int totalFail = 0;
			int totalSkipped = 0;
			for (int idx = 0; idx < strArray.length; idx++) {
				String testSetKindName = strArray[idx];	       // e.g, decl, expr etc				
				String pathStr = folderRoot + File.separator + testSetKindName;
				File file = new File(pathStr);
				if (file.isDirectory()) {
					String[] fileNames = file.list();
					Element testSetKindElem = document.createElement(testSetKindName);
					for (int idx2 = 0; idx2 < fileNames.length; idx2++) {
					   String fileName = fileNames[idx2];
					   String localFilePath = (pathStr + File.separator + fileName);						
					   File file1 = new File(localFilePath);
					   URI uri = file1.toURI();
					   Document testSetResultDoc = docBuilder.parse(uri.toString());
					   Element docElem = testSetResultDoc.getDocumentElement();
					   String testSetName = docElem.getAttribute("name");
					   int run = Integer.valueOf(docElem.getAttribute("run"));
					   totalCount += run; 
					   int pass = Integer.valueOf(docElem.getAttribute("pass"));
					   totalPass += pass;
					   int fail = Integer.valueOf(docElem.getAttribute("fail"));
					   totalFail += fail;
					   int skipped = Integer.valueOf(docElem.getAttribute("skipped"));
					   totalSkipped += skipped;
					   double successPer = ((pass / (double)run)) * 100;
					   double successPerDbl = (Double.valueOf(decimalFormat.format(Double.valueOf(String.valueOf(successPer))))).doubleValue();
					   Element testSetElem = document.createElement(testSetName);
					   testSetElem.setAttribute("run", String.valueOf(run));
					   testSetElem.setAttribute("pass", String.valueOf(pass));
					   testSetElem.setAttribute("fail", String.valueOf(fail));
					   testSetElem.setAttribute("skipped", String.valueOf(skipped));
					   testSetElem.setAttribute("success", String.valueOf(successPerDbl) + "%");
					   testSetKindElem.appendChild(testSetElem);
					}
					
					testResultElem.appendChild(testSetKindElem);
				}
			}
			
			double totalSuccessPer = ((totalPass / (double)totalCount)) * 100;
			double totalSuccessPerDbl = (Double.valueOf(decimalFormat.format(Double.valueOf(String.valueOf(totalSuccessPer))))).doubleValue();
			testResultElem.setAttribute("run", String.valueOf(totalCount));
			testResultElem.setAttribute("pass", String.valueOf(totalPass));
			testResultElem.setAttribute("fail", String.valueOf(totalFail));
			testResultElem.setAttribute("skipped", String.valueOf(totalSkipped));
			testResultElem.setAttribute("success", String.valueOf(totalSuccessPerDbl) + "%");
			
			document.appendChild(testResultElem);
			
			// Serialize an XML document object to file
			
			TransformerFactory xslTransformFactory = TransformerFactory.newInstance();
			Transformer transformer = xslTransformFactory.newTransformer();
			
			transformer.setOutputProperty(OutputKeys.INDENT, XSL_SERIALIZATION_INDENT_YES);
			transformer.setOutputProperty(XSL_SERIALIZATION_INDENT_KEY, String.valueOf(XSL_SERIALIZATION_INDENT_VALUE));
			
			DOMSource domSource = new DOMSource(document);
			FileWriter fileWriter = new FileWriter(new File(W3C_XSLT3_XALAN_TESTSUITE_RESULT_FOLDER_ROOT + File.separator + RESULT_FILE_NAME));
			StreamResult streamResult = new StreamResult(fileWriter);
			
			transformer.transform(domSource, streamResult);
		}
		catch (Exception ex) {
		    ex.printStackTrace();
		}
	}
	
	/*
     * Given a non-negative integer value, return a string comprising those many 
     * characters '0'. We use the string value returned by this method, to construct 
     * a java.text.DecimalFormat object instance.
     */
    private String getStrForZeros(int strSize) {
       String strVal = "";
       
       for (int idx = 0; idx < strSize; idx++) {
          strVal = strVal + "0";  
       }
       
       return strVal;
    }
    
    /**
     * Method definition to get an ISO formatted date string for the supplied 
     * java.util.Date value.
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
