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
package org.apache.xpath.functions.string;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.Constants;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathStaticContext;
import org.apache.xpath.functions.FunctionMultiArgs;
import org.apache.xpath.functions.RegexEvaluationSupport;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.regex.Matcher;
import org.apache.xpath.regex.Pattern;
import org.apache.xpath.res.XPATHErrorResources;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * Implementation of XPath 3.1 function fn:analyze-string.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncAnalyzeString extends FunctionMultiArgs {

	private static final long serialVersionUID = -1559008263985308212L;
	
	private static final String FUNCTION_NAME = "analyze-string()";
	
	/**
	 * Class constructor.
	 */
	public FuncAnalyzeString() {
	   m_defined_arity = new Short[] { 2, 3 };
	}
	
	/**
     * The number of arguments passed to the fn:analyze-string function 
     * call.
     */
    private int fNumOfArgs = 0;
    
    /**
     * Evaluate the function. The function must return a valid object.
     * 
     * @param xctxt                           An XPath context object
     * @return                                A valid XObject
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
        
        if ((arg0XObj instanceof ResultSequence) && (((ResultSequence)arg0XObj).size() == 0)) {
           strToBeAnalyzed  = "";
        }
        else if ((arg0XObj instanceof XMLNodeCursorImpl) && (((XMLNodeCursorImpl)arg0XObj).getLength() == 0)) {
           strToBeAnalyzed  = "";
        }
        else {        
           strToBeAnalyzed = XslTransformEvaluationHelper.getStrVal(arg0XObj);
        }
        
        // Get 'string value' for regex argument of fn:analyze-string 
        // function call.
        String regexStr = XslTransformEvaluationHelper.getStrVal(arg1XObj);
        
        String flagsStr = null;
        
        if (m_arg2 != null) {
           // Get 'string value' for flags argument of fn:analyze-string 
           // function call.
           XObject arg2XObj = m_arg2.execute(xctxt);

           flagsStr = XslTransformEvaluationHelper.getStrVal(arg2XObj);           
           
           if (!RegexEvaluationSupport.isRegexFlagStrValid(flagsStr)) {              
              throw new javax.xml.transform.TransformerException("XTDE1145 : An XPath 3.1 function 'analyze-string' has been "
              		                                                                                            + "called with incorrect regex flags "
              		                                                                                            + "argument. XPath regex valid flag charcaters "
              		                                                                                            + "are : s, m, i, x, q.", srcLocator);
           }
        }
        
        Document document = createEmptyXmlDom(srcLocator);
        
        Element analyzeStrResultElem = document.createElementNS(XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI, 
        		                                                                                            Constants.ELEMNAME_ANALYZESTRING_RESULT_STRING);
        document.appendChild(analyzeStrResultElem);
        
        if (strToBeAnalyzed.length() > 0) {
        	Matcher regexMatcher = null;
        	
        	try {
        		regexMatcher = RegexEvaluationSupport.compileAndExecute(RegexEvaluationSupport.transformRegexStrForSubtrOp(regexStr), flagsStr, strToBeAnalyzed);
        	}
        	catch (Exception ex) {        		        		
                String errMesg = XSLMessages.createXPATHMessage(XPATHErrorResources.ER_INVALID_REGEX, new Object[]{ FUNCTION_NAME });        		
        		
        		String mesg1 = ex.getMessage();
        		errMesg = (mesg1 != null) ? (errMesg + " " + mesg1) : errMesg;  
        		
        		throw new javax.xml.transform.TransformerException(errMesg, srcLocator);
        	}

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
        	
        	int size1 = regexMatchInfoList.size();

        	if (size1 > 0) {
        		RegexMatchInfo firstRegexMatchInfo = regexMatchInfoList.get(0);
        		int startIdx1 = firstRegexMatchInfo.getStartIdx();
        		if (startIdx1 == 0) {
        			// Regex has matched a substring, which is prefix of an input string 
        			
        			for (int idx = 0; idx < size1; idx++) {
        				RegexMatchInfo matchInfo = regexMatchInfoList.get(idx);
        				int idx1 = matchInfo.getStartIdx();
        				int idx2 = matchInfo.getEndIdx();
        				String matchStr = strToBeAnalyzed.substring(idx1, idx2);
        				
        				createXslMatchNodeToResult(document, analyzeStrResultElem, matchStr, regexStr); 

        				if (isXslNonMatchStringAvailable(strToBeAnalyzed, idx2)) {
        					String nonMatchStr = null;
        					if ((idx + 1) == size1) {
        						nonMatchStr = strToBeAnalyzed.substring(idx2);
        					}
        					else {
        						RegexMatchInfo matchInfoNext = regexMatchInfoList.get(idx + 1);
        						nonMatchStr = strToBeAnalyzed.substring(idx2, matchInfoNext.getStartIdx());   
        					}
        					
        					createXslNonMatchNodeToResult(document, analyzeStrResultElem, nonMatchStr);
        				}        		
        			}	
        		}
        		else if (startIdx1 > 0) {
        			// An input string's prefix has not been matched by regex
        			
        			RegexMatchInfo pof1 = regexMatchInfoList.get(0);
        			String nonMatchStr = strToBeAnalyzed.substring(0, pof1.getStartIdx());
        			
        			createXslNonMatchNodeToResult(document, analyzeStrResultElem, nonMatchStr);

        			for (int idx = 0; idx < size1; idx++) {
        				RegexMatchInfo matchInfo = regexMatchInfoList.get(idx);
        				int idx1 = matchInfo.getStartIdx();
        				int idx2 = matchInfo.getEndIdx();
        				String matchStr = strToBeAnalyzed.substring(idx1, idx2);
        				
        				createXslMatchNodeToResult(document, analyzeStrResultElem, matchStr, regexStr);

        				if (isXslNonMatchStringAvailable(strToBeAnalyzed, idx2)) {
        					if ((idx + 1) == size1) {
        						nonMatchStr = strToBeAnalyzed.substring(idx2);
        					}
        					else {
        						RegexMatchInfo matchInfoNext = regexMatchInfoList.get(idx + 1);
        						nonMatchStr = strToBeAnalyzed.substring(idx2, matchInfoNext.getStartIdx());   
        					}
        					
        					createXslNonMatchNodeToResult(document, analyzeStrResultElem, nonMatchStr);
        				}        			
        			}
        		}
            }
        	else {
        		createXslNonMatchNodeToResult(document, analyzeStrResultElem, strToBeAnalyzed);
        	}
        }
        
        DTMManager dtmMgr = xctxt.getDTMManager();
        int dtmHandleOfResultNode = dtmMgr.getDTMHandleFromNode(document.getFirstChild()); 
        result = new XMLNodeCursorImpl(dtmHandleOfResultNode, dtmMgr); 
            
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
    	    // no op
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
     * Method definition, to check whether an XML element named {http://www.w3.org/2005/xpath-functions}non-match 
     * can be appended within the result of function call fn:analyze-string. 
     * 
     * @param strToBeAnalyzed    An XPath 3.1 function call fn:analyze-string's 
     *                           first argument. 
     * @param idx                An end index for a particular regex match
     * @return                   Boolean value true or false
     */
    private boolean isXslNonMatchStringAvailable(String strToBeAnalyzed, int idx) {
		
    	boolean result = false;
		
    	try {
			result = (strToBeAnalyzed.charAt(idx) != -1);
		}
		catch (IndexOutOfBoundsException ex) {
			// no op
		}
    	
		return result;
	}

	/**
	 * Method definition, to create an XML element named {http://www.w3.org/2005/xpath-functions}non-match, 
	 * and append to XPath 3.1 function 'analyze-string' call result.  
	 * 
	 * @param document                    XML document node
	 * @param analyzeStrResultElem        An XML element, that is appended with result information 
	 *                                    XPath 3.1 function 'analyze-string' result.
	 * @param nonMatchStr                 An XML DOM text node's string value that is appended as 
	 *                                    child of XML element named {http://www.w3.org/2005/xpath-functions}non-match.
	 * @param regexStr					  Regex string value  
	 */
	private void createXslNonMatchNodeToResult(Document document, Element analyzeStrResultElem, String nonMatchStr) {
		
		Element nonMatchElem = document.createElementNS(XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI, Constants.ELEMNAME_ANALYZESTRING_NON_MATCH_STRING);
		Text txtNode2 = document.createTextNode(nonMatchStr);
		nonMatchElem.appendChild(txtNode2);
		analyzeStrResultElem.appendChild(nonMatchElem);
	}

	/**
	 * Method definition, to create an XML element named {http://www.w3.org/2005/xpath-functions}match, 
	 * and append to XPath 3.1 function 'analyze-string' call result.  
	 * 
	 * @param document                    XML document node
	 * @param analyzeStrResultElem        An XML element, that is appended with result information
	 * @param subsequenceStr              An XML DOM text node's string value that is appended as 
	 *                                    child of XML element named {http://www.w3.org/2005/xpath-functions}match.
	 * @param regexStr					  Regex string value 
	 */
	private void createXslMatchNodeToResult(Document document, Element analyzeStrResultElem, 
			                                                                            String subsequenceStr, String regexStr) {
		
		Element xslMatchElem = document.createElementNS(XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI, Constants.ELEMNAME_ANALYZESTRING_MATCH_STRING);
		
		Pattern pattern = Pattern.compile(regexStr);
		Matcher matcher = pattern.matcher(subsequenceStr);
		
		int grpCount = matcher.groupCount();
		
		if (grpCount > 0) {
		   if (matcher.matches()) {
			   for (int idx = 0; idx < grpCount; idx++) {			  
				  Element grpElem = document.createElementNS(XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI, Constants.ELEMNAME_ANALYZESTRING_GROUP_STRING);
				  grpElem.setAttribute(Constants.ELEMNAME_ANALYZESTRING_NR_STRING, String.valueOf(idx + 1));
				  String grpStrValue = matcher.group(idx + 1);
				  Text grpTxtNode = document.createTextNode(grpStrValue);
				  grpElem.appendChild(grpTxtNode);
				  xslMatchElem.appendChild(grpElem);
				  if (idx < (grpCount - 1)) {
					 Text hyphenTxtNode = document.createTextNode("-");
					 xslMatchElem.appendChild(hyphenTxtNode);
				  }
			   }
			   
			   analyzeStrResultElem.appendChild(xslMatchElem);
		   }
		   
		   matcher.reset();
		}				
		else {
		   Text txtNode1 = document.createTextNode(subsequenceStr);
		   xslMatchElem.appendChild(txtNode1);
		   analyzeStrResultElem.appendChild(xslMatchElem);
		}				
	}

    /**
     * Method definition, to create an empty XML DOM document node.
     * 
     * @param srcLocator                           XSL transformation SourceLocator object
     * @return                                     An empty DOM document node
     */
	private Document createEmptyXmlDom(SourceLocator srcLocator) throws TransformerException {
		
		Document result = null;
		
		System.setProperty(Constants.XML_DOCUMENT_BUILDER_FACTORY_KEY, Constants.XML_DOCUMENT_BUILDER_FACTORY_VALUE);
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        
        DocumentBuilder dBuilder = null;		
        try {
		   dBuilder = dbf.newDocumentBuilder();
		} 
        catch (ParserConfigurationException ex) {
		   throw new javax.xml.transform.TransformerException("FOJS0001 : An XPath 3.1 function call 'analyze-string', has encountered "
		   		                                                                                                + "an internal error within an XML "
		   		                                                                                                + "parser library invocation.", srcLocator);
		}
		
        result = dBuilder.newDocument();
        
		return result;
	}

}
