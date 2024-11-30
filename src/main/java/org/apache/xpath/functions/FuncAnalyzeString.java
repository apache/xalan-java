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

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLMessages;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.XPathContext;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.regex.Matcher;
import org.apache.xpath.regex.Pattern;
import org.apache.xpath.res.XPATHErrorResources;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import xml.xpath31.processor.types.XSString;

/**
 * Implementation of the fn:analyze-string function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncAnalyzeString extends FunctionMultiArgs {

	private static final long serialVersionUID = -1559008263985308212L;
	
	/**
     * The number of arguments passed to the fn:analyze-string function 
     * call.
     */
    private int fNumOfArgs = 0;
    
    /**
     * Implementation of the function. The function must return a valid object.
     * 
     * @param xctxt The current execution context.
     * @return A valid XObject.
     *
     * @throws javax.xml.transform.TransformerException
     */
    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {
        
        XObject result = null;
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        XObject arg0XObj = m_arg0.execute(xctxt);        
        XObject arg1XObj = m_arg1.execute(xctxt);                                
        
        // Get 'string value' of string to be analyzed by fn:analyze-string 
        // function call.
        String strToBeAnalyzed = null;
        if (arg0XObj instanceof XSString) {
           strToBeAnalyzed = ((XSString)arg0XObj).stringValue();
        }
        else {
     	   strToBeAnalyzed = arg0XObj.str();      	   
        }
        
        // Get 'string value' of regex argument of fn:analyze-string 
        // function call.
        String regexStr = null;
        if (arg1XObj instanceof XSString) {
           regexStr = ((XSString)arg1XObj).stringValue();
        }
        else {
           regexStr = arg1XObj.str();      	   
        }
        
        String flagsStr = null;
        if (m_arg2 != null) {
           // Get 'string value' of flags argument of fn:analyze-string 
           // function call.
           XObject arg2XObj = m_arg2.execute(xctxt);
           if (arg2XObj instanceof XSString) {
        	  flagsStr = ((XSString)arg2XObj).stringValue();
           }
           else {
              flagsStr = arg2XObj.str();      	   
           }
           
           if (!RegexEvaluationSupport.isFlagStrValid(flagsStr)) {
              throw new javax.xml.transform.TransformerException("XTDE1145 : Invalid regex flag value(s) is specified, "
              		                                           + "as an argument to function call fn:analyze-string. XPath "
              		                                           + "regex valid flag charcaters are : s, m, i, x, q.", srcLocator);    
           }
        }
        
        Document document = createEmptyXmlDom(srcLocator);
        
        Element analyzeStrResultElem = document.createElementNS(FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI, 
        		                                                                                        "analyze-string-result");
        document.appendChild(analyzeStrResultElem);
        
        if (strToBeAnalyzed.length() > 0) {
        	Matcher regexMatcher = RegexEvaluationSupport.compileAndExecute(
        			                                    RegexEvaluationSupport.transformRegexStrForSubtractionOp(regexStr), 
        			                                    flagsStr, strToBeAnalyzed);

        	List<RegexMatchInfo> regexMatchInfoList = new ArrayList<RegexMatchInfo>();

        	while (regexMatcher.find()) {
        		int idx1 = regexMatcher.start();
        		int idx2 = regexMatcher.end();
        		RegexMatchInfo regexMatchInfo = new RegexMatchInfo();
        		regexMatchInfo.setStartIdx(idx1);
        		regexMatchInfo.setEndIdx(idx2);
        		regexMatchInfoList.add(regexMatchInfo);
        	}
        	
        	regexMatcher.reset();

        	RegexMatchInfo firstRegexMatchInfo = regexMatchInfoList.get(0);
        	int startIdx1 = firstRegexMatchInfo.getStartIdx();
        	if (startIdx1 == 0) {
        		// Regex has matched a substring, which is prefix of an input string        		
        		for (int idx = 0; idx < regexMatchInfoList.size(); idx++) {
        			RegexMatchInfo matchInfo = regexMatchInfoList.get(idx);
        			int idx1 = matchInfo.getStartIdx();
        			int idx2 = matchInfo.getEndIdx();
        			String matchStr = strToBeAnalyzed.substring(idx1, idx2);
        			createMatchNodeToResult(document, analyzeStrResultElem, 
        					                matchStr, regexStr); 
        			
        			if (isNonMatchingStringAvailable(strToBeAnalyzed, idx2)) {
        				String nonMatchStr = null;
        				if ((idx + 1) == regexMatchInfoList.size()) {
        					nonMatchStr = strToBeAnalyzed.substring(idx2);
        				}
        				else {
        					RegexMatchInfo matchInfoNext = regexMatchInfoList.get(idx+1);
        					nonMatchStr = strToBeAnalyzed.substring(idx2, matchInfoNext.getStartIdx());   
        				}                    	                    	
        				createNonMatchNodeToResult(document, analyzeStrResultElem, nonMatchStr);
        			}        		
        		}	
        	}
        	else if (startIdx1 > 0) {
        		// Any prefix of an input string, hasn't been matched by regex        		
        		RegexMatchInfo pof1 = regexMatchInfoList.get(0);
        		String nonMatchStr = strToBeAnalyzed.substring(0, pof1.getStartIdx());
 			    createNonMatchNodeToResult(document, analyzeStrResultElem, nonMatchStr);
 			    
        		for (int idx = 0; idx < regexMatchInfoList.size(); idx++) {
        			RegexMatchInfo matchInfo = regexMatchInfoList.get(idx);
        			int idx1 = matchInfo.getStartIdx();
        			int idx2 = matchInfo.getEndIdx();
        			String matchStr = strToBeAnalyzed.substring(idx1, idx2);
        			createMatchNodeToResult(document, analyzeStrResultElem, 
        					                matchStr, regexStr);

        			if (isNonMatchingStringAvailable(strToBeAnalyzed, idx2)) {
        				if ((idx + 1) == regexMatchInfoList.size()) {
        					nonMatchStr = strToBeAnalyzed.substring(idx2);
        				}
        				else {
        					RegexMatchInfo matchInfoNext = regexMatchInfoList.get(idx+1);
        					nonMatchStr = strToBeAnalyzed.substring(idx2, matchInfoNext.getStartIdx());   
        				}                    	                    	        				
        				createNonMatchNodeToResult(document, analyzeStrResultElem, nonMatchStr);
        			}        			
        		}
        	}
        }
        
        DTMManager dtmMgr = xctxt.getDTMManager();
        int dtmHandleOfResultNode = dtmMgr.getDTMHandleFromNode(document.getFirstChild()); 
        result = new XNodeSet(dtmHandleOfResultNode, dtmMgr); 
            
        return result;
    }
    
    /**
     * Check that the number of arguments passed to this function is correct.
     *
     * @param argNum The number of arguments that is being passed to the function.
     *
     * @throws WrongNumberArgsException
     */
    public void checkNumberArgs(int argNum) throws WrongNumberArgsException
    {
       if (!((argNum == 2) || (argNum == 3))) {
          reportWrongNumberArgs();
       }
       else {
          fNumOfArgs = argNum;   
       }
    }
    
    /**
     * Constructs and throws a WrongNumberArgException with the appropriate
     * message for this function object.
     *
     * @throws WrongNumberArgsException
     */
    protected void reportWrongNumberArgs() throws WrongNumberArgsException {
        throw new WrongNumberArgsException(XSLMessages.createXPATHMessage(
                                                                     XPATHErrorResources.ER_TWO_OR_THREE, null));
    }
    
    /**
     * A class representing, a pair of string index values,
     * for a substring that matched with the fn:analyze-string 
     * function's regex argument.
     */
    class RegexMatchInfo {    	
    	private int startIdx;
    	
    	private int endIdx;
    	
    	/**
    	 * Class constructor.
    	 */
    	public RegexMatchInfo() {
    	    // NO OP
    	}

		public int getStartIdx() {
			return startIdx;
		}

		public void setStartIdx(int startIdx) {
			this.startIdx = startIdx;
		}

		public int getEndIdx() {
			return endIdx;
		}

		public void setEndIdx(int endIdx) {
			this.endIdx = endIdx;
		}
    }
    
    /**
     * This method, checks whether an XML "non-match" element can be appended at 
     * certain places within the result of function call fn:analyze-string. 
     * 
     * @param strToBeAnalyzed    this is an original string that is analyzed by 
     *                           the function call fn:analyze-string. 
     * @param idx                an end index of a particular regex match
     * @return                   true, or false result, indicating whether an
     *                           XML "non-match" element can be constructed.
     */
    private boolean isNonMatchingStringAvailable(String strToBeAnalyzed, int idx) {
		boolean isNonMatchAvailable;
		try {
			isNonMatchAvailable = (strToBeAnalyzed.charAt(idx) != -1);
		}
		catch (IndexOutOfBoundsException ex) {
			isNonMatchAvailable = false;
		}
		return isNonMatchAvailable;
	}

	/**
	 * Method to create an XML "non-match" element, and append to the result. 
	 * 
	 * @param document                    XML document node
	 * @param analyzeStrResultElem        XML result element, that is appended with more information
	 * @param nonMatchStr                 text value that is appended as child of XML "non-match" element 
	 */
	private void createNonMatchNodeToResult(Document document, Element analyzeStrResultElem, 
			                                String nonMatchStr) {
		Element nonMatchElem = document.createElementNS(FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI, "non-match");
		Text txtNode2 = document.createTextNode(nonMatchStr);
		nonMatchElem.appendChild(txtNode2);
		analyzeStrResultElem.appendChild(nonMatchElem);
	}

	/**
	 * Method to create an XML "match" element, and append to the result. 
	 * 
	 * @param document                    XML document node
	 * @param analyzeStrResultElem        XML result element, that is appended with more information
	 * @param subsequenceStr              text value that is appended as child of XML "match" element
	 * @param regexStr					  regex string, provided as an argument to function 
	 *                                    call fn:analyze-string. 
	 */
	private void createMatchNodeToResult(Document document, Element analyzeStrResultElem, 
			                             String subsequenceStr, String regexStr) {
		Element matchElem = document.createElementNS(FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI, "match");		
		Pattern regexSubsequencePattern = Pattern.compile(regexStr);
		Matcher regexSubsequenceMatcher = regexSubsequencePattern.matcher(subsequenceStr);
		int grpCount = regexSubsequenceMatcher.groupCount();
		if (grpCount > 0) {
		   if (regexSubsequenceMatcher.matches()) {
			   for (int idx = 0; idx < grpCount; idx++) {			  
				  Element grpElem = document.createElementNS(FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI, "group");
				  grpElem.setAttribute("nr", String.valueOf(idx+1));
				  String grpStrValue = regexSubsequenceMatcher.group(idx+1);
				  Text grpTxtNode = document.createTextNode(grpStrValue);
				  grpElem.appendChild(grpTxtNode);
				  matchElem.appendChild(grpElem);
				  if (idx < (grpCount - 1)) {
					 Text hyphenTxtNode = document.createTextNode("-");
					 matchElem.appendChild(hyphenTxtNode);
				  }
			   }			   
			   analyzeStrResultElem.appendChild(matchElem);
		   }		   
		   regexSubsequenceMatcher.reset();
		}				
		else {
		   Text txtNode1 = document.createTextNode(subsequenceStr);
		   matchElem.appendChild(txtNode1);
		   analyzeStrResultElem.appendChild(matchElem);
		}				
	}

    /**
     * Method to create an empty XML DOM document node.
     * 
     * @param srcLocator   XSL transformation sourceLocator object
     * @return an empty DOM document node
     */
	private Document createEmptyXmlDom(SourceLocator srcLocator) throws TransformerException {
		Document document = null;
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        
        DocumentBuilder dBuilder = null;		
        try {
		   dBuilder = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException ex) {
		   throw new javax.xml.transform.TransformerException("FOJS0001 : An error occured, within an XML parser "
		   		                                                      + "library.", srcLocator);
		}
		
        document = dBuilder.newDocument();
        
		return document;
	}

}
