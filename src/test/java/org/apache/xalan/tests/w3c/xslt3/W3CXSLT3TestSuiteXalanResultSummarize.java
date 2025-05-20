package org.apache.xalan.tests.w3c.xslt3;

import java.io.File;
import java.io.FileWriter;
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
 * XML result document for Xalan-J XSLT 3.0 development implementation.
 * 
 * Steps to use this class:
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
	private static final String W3C_XSLT3_XALAN_TESTSUITE_RESULT_FOLDER_ROOT = "d:\\eclipseWorkspaces\\xalanj\\xalan-j_xslt3.0_mvn\\src\\test\\java\\org\\apache\\xalan\\tests\\w3c\\xslt3\\result";
	
	private static final String RESULT_FILE_NAME = "w3c_xslt3_testsuite_xalan-j_result.xml";
	
	private static final String XSL_SERIALIZATION_INDENT_YES = "yes";
	
	private static final String XSL_SERIALIZATION_INDENT_KEY = "{http://xml.apache.org/xslt}indent-amount";
	
	private static final int XSL_SERIALIZATION_INDENT_VALUE = 2;

	/**
	 * Main method of this class.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		W3CXSLT3TestSuiteXalanResultSummarize applnObj = new W3CXSLT3TestSuiteXalanResultSummarize();
		
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
			testResultElem.setAttribute("xslt_processor", "Xalan-J XSLT 3.0 development code");
			String testRunDateStrValue = getDateISOString(new Date());
			testResultElem.setAttribute("dateTime", testRunDateStrValue);

			String[] strArray = folderRoot.list();
			int totalCount = 0;
			int totalPass = 0;
			int totalFail = 0;
			int totalSkipped = 0;
			for (int idx = 0; idx < strArray.length; idx++) {
				String testSetKindName = strArray[idx];	       // e.g, decl, expr etc				
				String pathStr = folderRoot + "\\"+ testSetKindName;
				File file = new File(pathStr);
				if (file.isDirectory()) {
					String[] fileNames = file.list();
					Element testSetKindElem = document.createElement(testSetKindName);
					for (int idx2 = 0; idx2 < fileNames.length; idx2++) {
					   String fileName = fileNames[idx2];
					   Document testSetResultDoc = docBuilder.parse(pathStr + "\\" + fileName);
					   Element docElem = testSetResultDoc.getDocumentElement();
					   String testSetName = docElem.getAttribute("name");
					   int totalRun = Integer.valueOf(docElem.getAttribute("totalRun"));
					   totalCount += totalRun; 
					   int pass = Integer.valueOf(docElem.getAttribute("pass"));
					   totalPass += pass;
					   int fail = Integer.valueOf(docElem.getAttribute("fail"));
					   totalFail += fail;
					   int skipped = Integer.valueOf(docElem.getAttribute("skipped"));
					   totalSkipped += skipped;
					   double successPer = (((pass + skipped) / (double)totalRun)) * 100;
					   double roundedDbl = (Double.valueOf(decimalFormat.format(Double.valueOf(String.valueOf(successPer))))).doubleValue();
					   Element testSetElem = document.createElement(testSetName);
					   testSetElem.setAttribute("totalRun", String.valueOf(totalRun));
					   testSetElem.setAttribute("pass", String.valueOf(pass));
					   testSetElem.setAttribute("fail", String.valueOf(fail));
					   testSetElem.setAttribute("skipped", String.valueOf(skipped));
					   testSetElem.setAttribute("success", String.valueOf(roundedDbl) + "%");
					   testSetKindElem.appendChild(testSetElem);
					}
					
					testResultElem.appendChild(testSetKindElem);
				}
			}
			
			double totalSuccessPer = (((totalPass + totalSkipped) / (double)totalCount)) * 100;
			double totalSuccessPerDbl = (Double.valueOf(decimalFormat.format(Double.valueOf(String.valueOf(totalSuccessPer))))).doubleValue();
			testResultElem.setAttribute("totalRun", String.valueOf(totalCount));
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
			FileWriter fileWriter = new FileWriter(new File(W3C_XSLT3_XALAN_TESTSUITE_RESULT_FOLDER_ROOT + "\\" + RESULT_FILE_NAME));
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
