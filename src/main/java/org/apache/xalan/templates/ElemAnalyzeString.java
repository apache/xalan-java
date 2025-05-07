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
package org.apache.xalan.templates;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.RegexEvaluationSupport;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.regex.Matcher;

import xml.xpath31.processor.types.XSNumericType;

/**
 * Implementation of the XSLT 3.0 xsl:analyze-string instruction.
 *   
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ElemAnalyzeString extends ElemTemplateElement implements ExpressionOwner {
  
  private static final long serialVersionUID = 5892915527446791252L;

  /**
   * Constructor of this class.
   */
  public ElemAnalyzeString() {}

  /**
   * The "select" expression.
   */
  protected Expression m_selectExpression = null;
  
  /**
   * The "regex" avt expression.
  */
  protected AVT m_regex = null;
  
  /**
   * The regex "flags" string value.
  */
  protected String m_regex_flags = null;

  /**
   * Set the "select" attribute.
   *
   * @param xpath The XPath expression for the "select" attribute.
   */
  public void setSelect(XPath xpath) {
      m_selectExpression = xpath.getExpression();   
  }

  /**
   * Get the "select" attribute.
   *
   * @return The XPath expression for the "select" attribute.
   */
  public Expression getSelect() {
      return m_selectExpression;
  }
  
  /**
   * Set the "regex" attribute.
   *
   * @param regex The avt value for the "regex" attribute.
   */
  public void setRegex(AVT regex) {
      m_regex = regex;   
  }

  /**
   * Get the "regex" attribute.
   *
   * @return The avt value for the "regex" attribute.
   */
  public AVT getRegex() {
      return m_regex;
  }
  
  /**
   * Set the regex "flags" attribute.
   *
   * @param regex The string value for the "flags" attribute.
   */
  public void setFlags(String flags) {
      m_regex_flags = flags;   
  }

  /**
   * Get the regex "flags" attribute.
   *
   * @return The string value for the "flags" attribute.
   */
  public String getFlags() {
      return m_regex_flags;
  }

  /**
   * This function is called after everything else has been
   * recomposed, and allows the template to set remaining
   * values that may be based on some other property that
   * depends on recomposition.
   *
   * @throws TransformerException
   */
  public void compose(StylesheetRoot sroot) throws TransformerException {
        
        super.compose(sroot);
    
        java.util.Vector vnames = sroot.getComposeState().getVariableNames();
    
        if (null != m_selectExpression) {
            m_selectExpression.fixupVariables(vnames, sroot.getComposeState().getGlobalsSize());
        }
        else {
            m_selectExpression = getStylesheetRoot().m_selectDefault.getExpression();
        }
  }
  
  /**
   * This after the template's children have been composed.
   */
  public void endCompose(StylesheetRoot sroot) throws TransformerException {   
      super.endCompose(sroot);
  }

  /**
   * Get an int constant identifying the type of element.
   *
   * @return The token ID for this element
   */
  public int getXSLToken() {
      return Constants.ELEMNAME_ANALYZESTRING;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName() {
      return Constants.ELEMNAME_ANALYZESTRING_STRING;
  }

  /**
   * Execute the XSLT element xsl:analyze-string's transformation.
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException {
      transformSelectedNodes(transformer);
  }

  /**
   * Get template element associated with this.
   * 
   * @return template element associated with this (itself)
   */
  protected ElemTemplateElement getTemplateMatch() {
      return this;
  }

  /**
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException Thrown in a variety of circumstances.
   * 
   * @xsl.usage advanced
   */
  public void transformSelectedNodes(TransformerImpl transformer) throws TransformerException {
      
       final XPathContext xctxt = transformer.getXPathContext();
       
       SourceLocator srcLocator = xctxt.getSAXLocator();
       
       XObject xpathSelectExpr = m_selectExpression.execute(xctxt);
       
       if (xpathSelectExpr instanceof ResultSequence) {
    	   ResultSequence rSeq = (ResultSequence)xpathSelectExpr;
    	   if (rSeq.size() == 0) {
    		   throw new javax.xml.transform.TransformerException("XPTY0004 : The xsl:analyze-string/@select expression evaluated "
    		   		                                                                                                  + "to an empty sequence.", srcLocator);  
    	   }
    	   else if (rSeq.size() > 1) {
    		   throw new javax.xml.transform.TransformerException("XPTY0004 : The xsl:analyze-string/@select expression evaluated to a "
    		   		                                                                                                  + "sequence with size greater than one.", srcLocator);
    	   }
       }
       
       if ((xpathSelectExpr instanceof XNumber) || (xpathSelectExpr instanceof XSNumericType)) {
    	   throw new javax.xml.transform.TransformerException("XPTY0004 : The xsl:analyze-string/@select expression has error. The supplied "
							    	   		                                                                          + "'select' expression evaluated to a numeric value, whereas the "
							    	   		                                                                          + "required type is xs:string.", srcLocator); 
       }
       
       String strToBeAnalyzed = XslTransformEvaluationHelper.getStrVal(xpathSelectExpr);
       
       if (m_regex == null) {
           throw new javax.xml.transform.TransformerException("XTSE0010 : xsl:analyze-string element must have an 'regex' attribute.", srcLocator);   
       }
       
       if (m_regex_flags != null && !RegexEvaluationSupport.isFlagStrValid(m_regex_flags)) {
           throw new javax.xml.transform.TransformerException("XTDE1145 : Incorrect regex flag value(s) are present as value of 'flags' "
           		                                                                  									 + "attribute of an xsl:analyze-string element.", srcLocator);    
       }

       ElemTemplateElement templateElem1 = this.m_firstChild;
       ElemTemplateElement templateElem2 = null;
       
       if (templateElem1 == null) {
           throw new javax.xml.transform.TransformerException("XTSE1130 : At-least one of the elements xsl:matching-substring, or "
												                                                                     + "xsl:non-matching-substring must be present as child of xsl:analyze-string "
												                                                                     + "element.", srcLocator);    
       }
       else {
           templateElem2 = templateElem1.m_nextSibling;
           if (templateElem2 != null) {
               if (!((templateElem1 instanceof ElemMatchingSubstring) && (templateElem2 instanceof ElemNonMatchingSubstring))) {
                   throw new javax.xml.transform.TransformerException("XTSE0010 : xsl:matching-substring element must come first within "
                                                                                          							 + "xsl:analyze-string element.", srcLocator);   
               }
               if (templateElem2.m_nextSibling != null) {
                   throw new javax.xml.transform.TransformerException("XTSE0010 : Only xsl:matching-substring, and xsl:non-matching-substring "
						                                                                                             + "elements are allowed to be present within an xsl:analyze-string "
						                                                                                             + "element.", srcLocator);    
               }
           }
       }
       
       if (strToBeAnalyzed.length() > 0) {
    	   String regexStr = m_regex.evaluate(xctxt, xctxt.getContextNode(), this);
    	   Matcher regexMatcher = RegexEvaluationSupport.compileAndExecute(RegexEvaluationSupport.transformRegexStrForSubtractionOp(regexStr), 
    			   										                                                                                  m_regex_flags, strToBeAnalyzed);

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
    	   
    	   ElemMatchingSubstring matchingXslElem = null;
		   if ((templateElem1 != null) && (templateElem1 instanceof ElemMatchingSubstring)) {
		      matchingXslElem = (ElemMatchingSubstring)templateElem1;  
		   }
		   else if ((templateElem2 != null) && (templateElem2 instanceof ElemMatchingSubstring)) {
			  matchingXslElem = (ElemMatchingSubstring)templateElem2; 
		   }
		   
		   ElemNonMatchingSubstring nonMatchingXslElem = null;
		   if ((templateElem1 != null) && (templateElem1 instanceof ElemNonMatchingSubstring)) {
		      nonMatchingXslElem = (ElemNonMatchingSubstring)templateElem1;  
		   }
		   else if ((templateElem2 != null) && (templateElem2 instanceof ElemNonMatchingSubstring)) {
			  nonMatchingXslElem = (ElemNonMatchingSubstring)templateElem2; 
		   }
		   
		   // Variable used to track evaluation result of XPath function 
		   // fn:last, within xsl:analyze-string instruction.
		   int xslAnalyzeStrContextSize = 0;
		   
		   if (regexMatchInfoList.size() > 0) {
			   RegexMatchInfo firstRegexMatchInfo = regexMatchInfoList.get(0);
			   int startIdx1 = firstRegexMatchInfo.getStartIdx();
			   if (startIdx1 == 0) {
				   // xsl:analyze-string instruction's regex has matched a substring, 
				   // which is prefix of an input string.
				   for (int idx = 0; idx < regexMatchInfoList.size(); idx++) {
					   RegexMatchInfo matchInfo = regexMatchInfoList.get(idx);
					   int idx1 = matchInfo.getStartIdx();
					   int idx2 = matchInfo.getEndIdx();					   
					   if (matchingXslElem != null) {
						   xslAnalyzeStrContextSize++;
					   }
					   
					   if (isNonMatchingStringAvailable(strToBeAnalyzed, idx2)) {
						   String nonMatchStr = null;
						   if ((idx + 1) == regexMatchInfoList.size()) {
							   nonMatchStr = strToBeAnalyzed.substring(idx2);
						   }
						   else {
							   RegexMatchInfo matchInfoNext = regexMatchInfoList.get(idx+1);
							   nonMatchStr = strToBeAnalyzed.substring(idx2, matchInfoNext.getStartIdx());   
						   }
						   
						   if ((nonMatchingXslElem != null) && (nonMatchStr.length() > 0)) {
							   xslAnalyzeStrContextSize++; 
						   }
					   }
				   }
			   }
			   else if (startIdx1 > 0) {
				   // None of the prefix of an input string, matched xsl:analyze-string 
				   // instruction's regex.     		
				   RegexMatchInfo pof1 = regexMatchInfoList.get(0);
				   String nonMatchStr = strToBeAnalyzed.substring(0, pof1.getStartIdx());
				   
				   if ((nonMatchingXslElem != null) && (nonMatchStr.length() > 0)) {
					   xslAnalyzeStrContextSize++;
				   }
				   
				   for (int idx = 0; idx < regexMatchInfoList.size(); idx++) {
					   RegexMatchInfo matchInfo = regexMatchInfoList.get(idx);
					   int idx1 = matchInfo.getStartIdx();
					   int idx2 = matchInfo.getEndIdx();
					   if (matchingXslElem != null) {
						   xslAnalyzeStrContextSize++; 
					   }
					   
					   if (isNonMatchingStringAvailable(strToBeAnalyzed, idx2)) {
						   if ((idx + 1) == regexMatchInfoList.size()) {
							   nonMatchStr = strToBeAnalyzed.substring(idx2);
						   }
						   else {
							   RegexMatchInfo matchInfoNext = regexMatchInfoList.get(idx+1);
							   nonMatchStr = strToBeAnalyzed.substring(idx2, matchInfoNext.getStartIdx());   
						   }
						   
						   if ((nonMatchingXslElem != null) && (nonMatchStr.length() > 0)) {
							   xslAnalyzeStrContextSize++; 
						   }
					   }
				   }
			   }
		   }
		   else if (nonMatchingXslElem != null) {
			   xslAnalyzeStrContextSize++;
		   }
		
		   // Variable used to track evaluation result of XPath function 
		   // fn:position, within xsl:analyze-string instruction.
		   int posValue = 0;
		   
		   // Traverse xsl:analyze-string instruction's regex match positions,
		   // and evaluate xsl:matching-substring and xsl:non-matching-substring
		   // instructions in sequence.
		   try {
			   if (regexMatchInfoList.size() > 0) {
				   RegexMatchInfo firstRegexMatchInfo = regexMatchInfoList.get(0);
				   int startIdx1 = firstRegexMatchInfo.getStartIdx();
				   if (startIdx1 == 0) {
					   // xsl:analyze-string instruction's regex has matched a substring, 
					   // which is prefix of an input string.
					   for (int idx = 0; idx < regexMatchInfoList.size(); idx++) {
						   RegexMatchInfo matchInfo = regexMatchInfoList.get(idx);
						   int idx1 = matchInfo.getStartIdx();
						   int idx2 = matchInfo.getEndIdx();
						   String matchStr = strToBeAnalyzed.substring(idx1, idx2);       			   
						   if (matchingXslElem != null) { 
							   matchingXslElem.setStrValue(matchStr);
							   matchingXslElem.setRegex(regexStr);
							   matchingXslElem.setFlags(m_regex_flags);
							   xctxt.setPos(++posValue);
							   xctxt.setLast(xslAnalyzeStrContextSize);
							   xctxt.setSAXLocator(matchingXslElem);
							   transformer.setCurrentElement(matchingXslElem);                   
							   matchingXslElem.execute(transformer);   
						   }

						   if (isNonMatchingStringAvailable(strToBeAnalyzed, idx2)) {
							   String nonMatchStr = null;
							   if ((idx + 1) == regexMatchInfoList.size()) {
								   nonMatchStr = strToBeAnalyzed.substring(idx2);
							   }
							   else {
								   RegexMatchInfo matchInfoNext = regexMatchInfoList.get(idx+1);
								   nonMatchStr = strToBeAnalyzed.substring(idx2, matchInfoNext.getStartIdx());   
							   }       				          				   

							   if ((nonMatchingXslElem != null) && (nonMatchStr.length() > 0)) {
								   nonMatchingXslElem.setStrValue(nonMatchStr);
								   xctxt.setPos(++posValue);
								   xctxt.setLast(xslAnalyzeStrContextSize);
								   xctxt.setSAXLocator(nonMatchingXslElem);
								   transformer.setCurrentElement(nonMatchingXslElem);                   
								   nonMatchingXslElem.execute(transformer);   
							   }
						   }        		
					   }	
				   }
				   else if (startIdx1 > 0) {
					   // None of the prefix of an input string, matched xsl:analyze-string 
					   // instruction's regex.     		
					   RegexMatchInfo pof1 = regexMatchInfoList.get(0);
					   String nonMatchStr = strToBeAnalyzed.substring(0, pof1.getStartIdx());

					   if ((nonMatchingXslElem != null) && (nonMatchStr.length() > 0)) {
						   nonMatchingXslElem.setStrValue(nonMatchStr);
						   xctxt.setPos(++posValue);
						   xctxt.setLast(xslAnalyzeStrContextSize);
						   xctxt.setSAXLocator(nonMatchingXslElem);
						   transformer.setCurrentElement(nonMatchingXslElem);                   
						   nonMatchingXslElem.execute(transformer);   
					   }

					   for (int idx = 0; idx < regexMatchInfoList.size(); idx++) {
						   RegexMatchInfo matchInfo = regexMatchInfoList.get(idx);
						   int idx1 = matchInfo.getStartIdx();
						   int idx2 = matchInfo.getEndIdx();
						   String matchStr = strToBeAnalyzed.substring(idx1, idx2);
						   if (matchingXslElem != null) {
							   matchingXslElem.setStrValue(matchStr);
							   matchingXslElem.setRegex(regexStr);
							   matchingXslElem.setFlags(m_regex_flags);
							   xctxt.setPos(++posValue);
							   xctxt.setLast(xslAnalyzeStrContextSize);
							   xctxt.setSAXLocator(matchingXslElem);
							   transformer.setCurrentElement(matchingXslElem);                   
							   matchingXslElem.execute(transformer);   
						   }

						   if (isNonMatchingStringAvailable(strToBeAnalyzed, idx2)) {
							   if ((idx + 1) == regexMatchInfoList.size()) {
								   nonMatchStr = strToBeAnalyzed.substring(idx2);
							   }
							   else {
								   RegexMatchInfo matchInfoNext = regexMatchInfoList.get(idx+1);
								   nonMatchStr = strToBeAnalyzed.substring(idx2, matchInfoNext.getStartIdx());   
							   }

							   if ((nonMatchingXslElem != null) && (nonMatchStr.length() > 0)) {
								   nonMatchingXslElem.setStrValue(nonMatchStr);
								   xctxt.setPos(++posValue);
								   xctxt.setLast(xslAnalyzeStrContextSize);
								   xctxt.setSAXLocator(nonMatchingXslElem);
								   transformer.setCurrentElement(nonMatchingXslElem);                   
								   nonMatchingXslElem.execute(transformer);   
							   }
						   }        			
					   }
				   }
			   }
			   else if (nonMatchingXslElem != null) {
				   // xsl:analyze-string instruction's regex didn't match anything within an 
				   // input string to be analyzed. If there's a xsl:non-matching-substring element 
				   // available, process an input string with it.
				   nonMatchingXslElem.setStrValue(strToBeAnalyzed);
				   xctxt.setPos(1);
				   xctxt.setLast(1);
				   xctxt.setSAXLocator(nonMatchingXslElem);
				   transformer.setCurrentElement(nonMatchingXslElem);                   
				   nonMatchingXslElem.execute(transformer);   
			   }
           }
           finally {
        	   xctxt.setPos(0);
			   xctxt.setLast(0);
           }
       }       
  }

  /**
   *
   * @param newChild Child to add to child list
   *
   * @return Child just added to child list
   */
  public ElemTemplateElement appendChild(ElemTemplateElement newChild)
  {
      return super.appendChild(newChild);
  }
  
  /**
   * Call the children visitors.
   * 
   * @param visitor The visitor whose appropriate method will be called.
   */
  public void callChildVisitors(XSLTVisitor visitor, boolean callAttributes)
  {
  	  if (callAttributes && (null != m_selectExpression)) {
  		 m_selectExpression.callVisitors(this, visitor);
  	  }

      super.callChildVisitors(visitor, callAttributes);
  }

  /**
   * @see ExpressionOwner#getExpression()
   */
  public Expression getExpression() {
      return m_selectExpression;
  }

  /**
   * @see ExpressionOwner#setExpression(Expression)
   */
  public void setExpression(Expression exp) {
      exp.exprSetParent(this);
      m_selectExpression = exp;
  }
  
  /**
   * A class representing, a pair of string index values,
   * for a substring that matched with xsl:analyze-string element's 
   * regex attribute.
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
   * This method, checks whether string 'non match' information can be appended at 
   * certain places within the result of xsl:analyze-string instruction. 
   * 
   * @param strToBeAnalyzed    this is an original string that is analyzed by 
   *                           xsl:analyze-string instruction. 
   * @param idx                an end index of a particular regex match
   * @return                   true, or false result, indicating whether
   *                           string 'non match' information can be appended
   *                           to the result of instruction xsl:analyze-string. 
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
  
}
