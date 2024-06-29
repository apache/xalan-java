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
/*
 * $Id$
 */
package org.apache.xalan.templates;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.RegexEvaluationSupport;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.regex.Matcher;

import xml.xpath31.processor.types.XSString;

/**
 * XSLT 3.0 analyze-string element.
 * 
 * <xsl:analyze-string
            select = expression
            regex = { string }
            flags? = { string } >
       <!-- Content: (xsl:matching-substring?, xsl:non-matching-substring?, xsl:fallback*) -->
   </xsl:analyze-string>
   
   <xsl:matching-substring>
      <!-- Content: sequence-constructor -->
   </xsl:matching-substring>
   
   <xsl:non-matching-substring>
      <!-- Content: sequence-constructor -->
   </xsl:non-matching-substring>
   
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
       
       XObject xpathEvalResult = m_selectExpression.execute(xctxt);
       
       String strToBeAnalyzed = null;
       
       if (xpathEvalResult instanceof XSString) {
          strToBeAnalyzed = ((XSString)xpathEvalResult).stringValue();
       }
       else {
    	  strToBeAnalyzed = xpathEvalResult.str();      	   
       }
       
       if (m_regex == null) {
           throw new javax.xml.transform.TransformerException("XTSE0010 : xsl:analyze-string element must "
                                                                                 + "have an 'regex' attribute.", xctxt.getSAXLocator());   
       }
       
       if (m_regex_flags != null && !RegexEvaluationSupport.isFlagStrValid(m_regex_flags)) {
           throw new javax.xml.transform.TransformerException("XTDE1145 : incorrect regex flag(s) are present, on an "
                   + "xsl:analyze-string element.", xctxt.getSAXLocator());    
       }

       String effectiveRegexStrValue = m_regex.evaluate(xctxt, xctxt.getContextNode(), this);
       
       Matcher regexMatcher = RegexEvaluationSupport.compileAndExecute(RegexEvaluationSupport.transformRegexStrForSubtractionOp(
                                                                                    effectiveRegexStrValue), m_regex_flags, strToBeAnalyzed);
       
       ElemTemplateElement templateElem1 = this.m_firstChild;
       ElemTemplateElement templateElem2 = null;
       ElemTemplateElement templateElem3 = null;
       
       if (templateElem1 == null) {
           throw new javax.xml.transform.TransformerException("XTSE1130 : At-least one of the elements xsl:matching-substring, or "
                                                                  + "xsl:non-matching-substring must be present as child of xsl:analyze-string "
                                                                  + "element.", xctxt.getSAXLocator());    
       }
       else {
           templateElem2 = templateElem1.m_nextSibling;
           if (templateElem2 != null) {
               if (!((templateElem1 instanceof ElemMatchingSubstring) && (templateElem2 instanceof ElemNonMatchingSubstring))) {
                   throw new javax.xml.transform.TransformerException("XTSE0010 : xsl:matching-substring element must come first within "
                                                                                          + "xsl:analyze-string element.", xctxt.getSAXLocator());   
               }
               templateElem3 = templateElem2.m_nextSibling;
               if (templateElem3 != null) {
                   throw new javax.xml.transform.TransformerException("XTSE0010 : Only xsl:matching-substring and xsl:non-matching-substring "
                                                                                          + "elements are allowed to be present within xsl:analyze-string "
                                                                                          + "element.", xctxt.getSAXLocator());    
               }
           }
       }
       
       regexMatcher.reset();
       
       List<String> strToBeAnalyzedMatchingSubsequences = new ArrayList<String>();
       
       while (regexMatcher.find()) {
           int idx1 = regexMatcher.start();
           int idx2 = regexMatcher.end();
           String str = strToBeAnalyzed.substring(idx1, idx2);
           strToBeAnalyzedMatchingSubsequences.add(str);
       }
       
       // We use the string value of a random UUID value, representing
       // a fairly random string, to help us pre-process the input string 
       // that is been analyzed by xsl:analyze-string instruction.       
       String randomStr1 = (UUID.randomUUID()).toString();
       
       String str1Replaced = regexMatcher.replaceAll(randomStr1);
       
       if (str1Replaced.equals(randomStr1)) {
          // Handle the case, where xsl:analyze-string's regex value matches 
          // the whole of an input string. 
          if ((templateElem1 != null) && (templateElem1 instanceof ElemMatchingSubstring)) {
              ((ElemMatchingSubstring)templateElem1).setStrValue(strToBeAnalyzed);
              ((ElemMatchingSubstring)templateElem1).setRegex(effectiveRegexStrValue);
              ((ElemMatchingSubstring)templateElem1).setFlags(m_regex_flags);
              xctxt.setSAXLocator(templateElem1);
              transformer.setCurrentElement(templateElem1);                   
              templateElem1.execute(transformer);   
          }
          
          return;
       }
       
       String[] strParts = str1Replaced.split(randomStr1);
       
       int i2 = 0;
       for (int idx = 0; idx < strParts.length; idx++) {           
           if (templateElem2 != null) {
               ((ElemNonMatchingSubstring)templateElem2).setStrValue(strParts[idx]);               
               xctxt.setSAXLocator(templateElem2);
               transformer.setCurrentElement(templateElem2);                   
               templateElem2.execute(transformer);
           }

           if (templateElem1 != null) {
               if (templateElem1 instanceof ElemNonMatchingSubstring) {
                   ((ElemNonMatchingSubstring)templateElem1).setStrValue(strParts[idx]);               
                   xctxt.setSAXLocator(templateElem1);
                   transformer.setCurrentElement(templateElem1);                   
                   templateElem1.execute(transformer);    
               }
               else {
                   if ((idx < strParts.length - 1) || ((idx == strParts.length - 1) && 
                                                                 str1Replaced.endsWith(randomStr1))) {
                      ((ElemMatchingSubstring)templateElem1).setStrValue(
                                                                 strToBeAnalyzedMatchingSubsequences.get(i2));
                      ((ElemMatchingSubstring)templateElem1).setRegex(effectiveRegexStrValue);
                      ((ElemMatchingSubstring)templateElem1).setFlags(m_regex_flags);
                      xctxt.setSAXLocator(templateElem1);
                      transformer.setCurrentElement(templateElem1);                   
                      templateElem1.execute(transformer);
                      i2++;
                   }
               }
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
  
}
