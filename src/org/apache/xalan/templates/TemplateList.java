/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xalan.templates;

import java.util.Hashtable;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import java.util.Vector;
import java.io.Serializable;

import org.apache.xalan.utils.QName;
import org.apache.xpath.XPath;
import org.apache.xpath.compiler.PsuedoNames;
import org.apache.xpath.patterns.NodeTest;
import org.apache.xpath.Expression;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xpath.XPathContext;
import org.apache.xpath.patterns.StepPattern;
import org.apache.xpath.patterns.UnionPattern;

/**
 * <meta name="usage" content="advanced"/>
 * Encapsulates a template list, and helps locate individual templates.
 */
public class TemplateList implements java.io.Serializable
{
  /**
   * Construct a TemplateList object.
   */
  TemplateList(Stylesheet stylesheet)
  {
    m_stylesheet = stylesheet;
  }
  
  /**
   * Add a template to the template list.
   */
  public void setTemplate(ElemTemplate template)
  {
    int pos = 0;
    if(null == m_firstTemplate)
    {
      m_firstTemplate = template;
    }
    else
    {
      ElemTemplateElement next = m_firstTemplate;
      while(null != next)
      {
        if(null == next.m_nextSibling)
        {
          next.m_nextSibling = template;
          template.m_nextSibling = null; // just to play it safe.
          break;
        }
        else if (template.equals(next.m_nextSibling))
        {
          pos++;
          break;
        }  
                 
        pos++;
        next = next.m_nextSibling;
      }
    }
    if(null != template.getName())
    {
      if (m_namedTemplates.get(template.getName()) == null)
        m_namedTemplates.put(template.getName(), template);
      else
        template.error(XSLTErrorResources.ER_DUPLICATE_NAMED_TEMPLATE, new Object[]{template.getName()});
    }

    XPath matchXPath = template.getMatch();
    if(null != matchXPath)
    {
      Expression matchExpr = matchXPath.getExpression();
      if(matchExpr instanceof StepPattern)
      {
        insertPatternInTable((StepPattern)matchExpr, template, pos);
      }
      if(matchExpr instanceof UnionPattern)
      {
        UnionPattern upat = (UnionPattern)matchExpr;
        StepPattern[] pats = upat.getPatterns();
        int n = pats.length;
        for(int i = 0; i < n; i++)
        {
          insertPatternInTable(pats[i], template, pos);
        }
      }
      else
      {
        // TODO: assert error
      }                                  
    }
  }
  
  /**
   * Add a template to the template list.
   */
  public void insertPatternInTable(StepPattern pattern, ElemTemplate template, int pos)
  {
    String target = pattern.getTargetString();
    if(null != target)
    {       
      Object newMatchPat = new MatchPattern2(template.getMatch().getPatternString(),
                                             template.getMatch(),
                                             template, pos,
                                             target, m_stylesheet, pattern);

      // See if there's already one there
      Object val = m_patternTable.get(target);
      if(null == val)
      {
        // System.out.println("putting: "+target);
        m_patternTable.put(target, newMatchPat);
      }
      else
      {            
        MatchPattern2 matchPat = (MatchPattern2)val;
        //((MatchPattern2)newMatchPat).setNext(matchPat);
        //m_patternTable.put(target, newMatchPat);
        //*
        // Sort by priority first, then by document order.
        double priority = getPriorityOrScore((MatchPattern2)newMatchPat);
        MatchPattern2 next;
        while( ((next = matchPat.getNext()) != null) &&
               (getPriorityOrScore(next) > priority) )
          
        {
          matchPat = next;
        }
        // System.out.println("appending: "+target+" to "+matchPat.getPattern());
        
        // This check is just to catch the first template in the list
        // It's priority was not checked against the new template  
        if ( (getPriorityOrScore(matchPat) <= priority))
        { 
          ((MatchPattern2)newMatchPat).setNext(matchPat); 
          m_patternTable.put(target, newMatchPat);
        }  
        else
        {  
          ((MatchPattern2)newMatchPat).setNext(next);
          matchPat.setNext((MatchPattern2)newMatchPat);            
        }            
      }
    }
  }  
  
  private double getPriorityOrScore(MatchPattern2 matchPat)
  {
    double priority = matchPat.getTemplate().getPriority();
    if(priority == XPath.MATCH_SCORE_NONE)         
    {  
      Expression ex = matchPat.getStepPattern();
      if (ex instanceof NodeTest)
        return ((NodeTest)ex).getDefaultScore();
    }
    return priority;
  }  
  
  /**
   * Locate a macro via the "name" attribute.
   * @exception XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   */
  public ElemTemplate getTemplate(QName qname)
  {
    ElemTemplate namedTemplate = (ElemTemplate)m_namedTemplates.get(qname);
    if(null == namedTemplate)
    {
      StylesheetComposed stylesheet = getStylesheet().getStylesheetComposed();
      int n = stylesheet.getImportCountComposed();
      for(int i = 0; i < n; i++)
      {
        StylesheetComposed imported = stylesheet.getImportComposed(i);
        namedTemplate = imported.getTemplateComposed(qname);
        if(null != namedTemplate)
          break;
      }
    }
    return namedTemplate;
  }
  
  /**
   * Given a target element, find the template that best
   * matches in the given XSL document, according
   * to the rules specified in the xsl draft.
   * @param stylesheetTree Where the XSL rules are to be found.
   * @param targetElem The element that needs a rule.
   * @param mode A string indicating the display mode.
   * @param useImports means that this is an xsl:apply-imports commend.
   * @return Rule that best matches targetElem.
   * @exception XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   */
  public ElemTemplate getTemplate(XPathContext support,
                                  Node targetNode,
                                  QName mode,
                                  boolean quietConflictWarnings)
    throws SAXException
  {
    ElemTemplate bestMatchedRule = null;
    MatchPattern2 bestMatchedPattern =null; // Syncs with bestMatchedRule
    double bestMatchPathPriority = XPath.MATCH_SCORE_NONE;
    if(m_isWrapperless)
    {
      return m_wrapperlessTemplate;
    }
    Vector conflicts = null;

    double highScore = XPath.MATCH_SCORE_NONE;

    MatchPattern2 matchPat = null;
    int targetNodeType = targetNode.getNodeType();

    switch(targetNodeType)
    {
    case Node.ELEMENT_NODE:
      {
        // String targetName = m_parserLiaison.getExpandedElementName((Element)targetNode);
        String targetName = support.getDOMHelper().getLocalNameOfNode(targetNode);
        matchPat = locateMatchPatternList2(targetName, true);
      }
      break;

    case Node.PROCESSING_INSTRUCTION_NODE:
    case Node.ATTRIBUTE_NODE:
      {
        String targetName = support.getDOMHelper().getLocalNameOfNode(targetNode);
        matchPat = locateMatchPatternList2(targetName, true);
      }
      break;

    case Node.CDATA_SECTION_NODE:
    case Node.TEXT_NODE:
      matchPat = locateMatchPatternList2(PsuedoNames.PSEUDONAME_TEXT, true);
      break;

    case Node.COMMENT_NODE:
      matchPat = locateMatchPatternList2(PsuedoNames.PSEUDONAME_COMMENT, true);
      break;

    case Node.DOCUMENT_NODE:
      matchPat = locateMatchPatternList2(PsuedoNames.PSEUDONAME_ROOT, false);
      break;

    case Node.DOCUMENT_FRAGMENT_NODE:
      matchPat = locateMatchPatternList2(PsuedoNames.PSEUDONAME_ANY, false);
      break;

    default:
      {
        matchPat = locateMatchPatternList2(targetNode.getNodeName(), true);
      }
    }

    String prevPat = null;
    MatchPattern2 prevMatchPat = null;
    double prevMatchPatPriority = XPath.MATCH_SCORE_NONE;

    while(null != matchPat)
    {
      ElemTemplate rule = matchPat.getTemplate();
      // We'll be needing to match rules according to what
      // mode we're in.
      QName ruleMode = rule.getMode();
      double matchPatPriority = XPath.MATCH_SCORE_NONE;

      // The logic here should be that if we are not in a mode AND
      // the rule does not have a node, then go ahead.
      // OR if we are in a mode, AND the rule has a node,
      // AND the rules match, then go ahead.
      if(((null == mode) && (null == ruleMode)) ||
         ((null != ruleMode) && (null != mode) && ruleMode.equals(mode)))
      {
        String patterns = matchPat.getPattern();

        if(null != patterns) 
        {
          if((prevPat != null) && prevPat.equals(patterns) &&
             (prevMatchPat.getTemplate().getPriority()
              == matchPat.getTemplate().getPriority()))
          {
            // Then we know that this pattern matches also.  Since we're 
            // operating in document order, take this one instead of the 
            // old one.
            if(bestMatchedPattern == prevMatchPat)
            {
              bestMatchedRule = rule;
              bestMatchedPattern = matchPat;
              bestMatchPathPriority
                = (XPath.MATCH_SCORE_NONE != rule.getPriority())
                  ? rule.getPriority() : matchPat.getExpression().getMatchScore(support, targetNode);
            }            
            prevMatchPat = matchPat; // make sure this is done after above check.
            // TODO, need to add to the conflicts list, I think...
          }
          else
          {
            prevMatchPat = matchPat;
            prevPat = patterns;
            prevMatchPatPriority = matchPatPriority;
            matchPatPriority = XPath.MATCH_SCORE_NONE;

            // Date date1 = new Date();
            XPath xpath = matchPat.getExpression();
            // System.out.println("Testing score for: "+targetNode.getNodeName()+
            //                   " against '"+xpath.m_currentPattern);
            double score = xpath.getMatchScore(support, targetNode);
            // System.out.println("Score for: "+targetNode.getNodeName()+
            //                   " against '"+xpath.m_currentPattern+
            //                   "' returned "+score);

            if(XPath.MATCH_SCORE_NONE != score)
            {
              double priorityOfRule
                = (XPath.MATCH_SCORE_NONE != rule.getPriority())
                  ? rule.getPriority() : score;
              matchPatPriority = priorityOfRule;
              double priorityOfBestMatched = (null != bestMatchedPattern) ?
                                             bestMatchPathPriority :
                                             XPath.MATCH_SCORE_NONE;
              // System.out.println("priorityOfRule: "+priorityOfRule+", priorityOfBestMatched: "+priorityOfBestMatched);
              if(priorityOfRule > priorityOfBestMatched)
              {
                if(null != conflicts)
                  conflicts.removeAllElements();
                highScore = score;
                bestMatchedRule = rule;
                bestMatchedPattern = matchPat;
                bestMatchPathPriority = matchPatPriority;
              }
              else if(priorityOfRule == priorityOfBestMatched)
              {
                if(null == conflicts)
                  conflicts = new Vector(10);
                addObjectIfNotFound(bestMatchedPattern, conflicts);
                conflicts.addElement(matchPat);
                highScore = score;
                bestMatchedRule = rule;
                bestMatchedPattern = matchPat;
              }
              // We have found a match. If not issueing conflict warnings,
              // stop right here. This should be the best match because
              // the template list was set up with the highest priority
              // (including document order priority) template at the top.              
              if (quietConflictWarnings)
                break;
            }
            // Date date2 = new Date();
            // m_totalTimePatternMatching+=(date2.getTime() - date1.getTime());
          } 
        } // end if(null != patterns) 
      } // end if if(targetModeString.equals(mode))

      MatchPattern2 nextMatchPat = matchPat.getNext();

      // We also have to consider wildcard matches.
      if((null == nextMatchPat) && 
         !matchPat.m_targetString.equals("*")
         && (Node.DOCUMENT_NODE != targetNodeType)
         && (Node.ATTRIBUTE_NODE != targetNodeType)
        /* ((Node.ELEMENT_NODE == targetNodeType) ||
          (Node.TEXT_NODE == targetNodeType) ||
          (Node.ATTRIBUTE_NODE == targetNodeType) ||
          (Node.PROCESSING_INSTRUCTION_NODE == targetNodeType))*/
         )
      {
        nextMatchPat = (MatchPattern2)m_patternTable.get("*");
        // TODO: something like this to avoid going thgough executing 
        // the match pattern if we can know that it will have lower score 
        //double d = ((Nodetest)(nextMatchPat.getExpression().getExpression())).getDefaultScore();
        // if (d < bestMatchScore)
        //   nextMatchPat = null;  // don't even bother to execute 
      }
      matchPat = nextMatchPat;
    }

    if(null == bestMatchedRule)
    {
      StylesheetComposed stylesheet = getStylesheet().getStylesheetComposed();
      int n = stylesheet.getImportCountComposed();
      for(int i = 0; i < n; i++)
      {
        StylesheetComposed imported = stylesheet.getImportComposed(i);
        bestMatchedRule 
          = getTemplate(imported, support, targetNode, mode, 
                        quietConflictWarnings);
        if(null != bestMatchedRule)
          break;
      }
    }

    if(null != conflicts)
    {
      int nConflicts = conflicts.size();
      // System.out.println("nConflicts: "+nConflicts);
      String conflictsString = (!quietConflictWarnings)
                               ? "" : null;
      for(int i = 0; i < nConflicts; i++)
      {
        MatchPattern2 conflictPat = (MatchPattern2)conflicts.elementAt(i);
        if(0 != i)
        {
          if(!quietConflictWarnings)
            conflictsString += ", ";

          // Find the furthest one towards the bottom of the document.
          if(conflictPat.m_posInStylesheet > bestMatchedPattern.m_posInStylesheet)
          {
            bestMatchedPattern = conflictPat;
          }
        }
        else
        {
          bestMatchedPattern = conflictPat;
        }
        if(!quietConflictWarnings)
          conflictsString += "\""+conflictPat.getPattern()+"\"";
      }
      bestMatchedRule = bestMatchedPattern.getTemplate();
      if(!quietConflictWarnings)
      {
        //conflictsString += " ";
        //conflictsString += "Last found in stylesheet will be used.";
        
        // transformContext.warn(XSLTErrorResources.WG_SPECIFICITY_CONFLICTS, new Object[] {conflictsString});
      }
    }

    return bestMatchedRule;
  } // end findTemplate
  
  /**
   * For derived classes to override which method gets accesed to 
   * get the imported template.
   */
  protected ElemTemplate getTemplate(StylesheetComposed imported,
                                     XPathContext support,
                                     Node targetNode,
                                     QName mode,
                                     boolean quietConflictWarnings)
    throws SAXException
  {
    return imported.getTemplateComposed(support, 
                                         targetNode, mode, 
                                         quietConflictWarnings);
  }

  
  /**
   * Set the manufactured template if there is no wrapper.
   * and xsl:template wrapper.
   */
  public void setWrapperlessTemplate(ElemTemplate t)
  {
    setIsWrapperless(true);
    m_wrapperlessTemplate = t;
  }

  /**
   * Get the manufactured template if there is no wrapper.
   * and xsl:template wrapper.
   */
  public ElemTemplate getWrapperlessTemplate()
  {
    return m_wrapperlessTemplate;
  }

  /**
   * Add object to vector if not already there.
   */
  private void addObjectIfNotFound(Object obj, Vector v)
  {
    int n = v.size();
    boolean addIt = true;
    for(int i = 0; i < n; i++)
    {
      if(v.elementAt(i) == obj)
      {
        addIt = false;
        break;
      }
    }
    if(addIt)
    {
      v.addElement(obj);
    }
  }

  /**
   * The stylesheet owner of the list.
   */
  private Stylesheet m_stylesheet;
  
  /**
   * Get the stylesheet owner of the list.
   */
  private Stylesheet getStylesheet()
  {
    return m_stylesheet;
  }

  /**
   * The first template of the template children.
   * @serial
   */
  private ElemTemplateElement m_firstTemplate = null;

  /**
   * Get the first template of the template children.
   */
  private ElemTemplateElement getFirstTemplate()
  {
    return m_firstTemplate;
  }

  /**
   * Keyed on string macro names, and holding values
   * that are macro elements in the XSL DOM tree.
   * Initialized in initMacroLookupTable, and used in
   * findNamedTemplate.
   * @serial
   */
  private Hashtable m_namedTemplates = new Hashtable();

  /**
   * Tells if the stylesheet is without an xsl:stylesheet
   * and xsl:template wrapper.
   * @serial
   */
  private boolean m_isWrapperless = false;

  /**
   * The manufactured template if there is no wrapper.
   * @serial
   */
  private ElemTemplate m_wrapperlessTemplate = null;

  /**
   * This table is keyed on the target elements
   * of patterns, and contains linked lists of
   * the actual patterns that match the target element
   * to some degree of specifity.
   * @serial
   */
  private Hashtable m_patternTable = new Hashtable();

  /**
   * Set if the stylesheet is without an xsl:stylesheet
   * and xsl:template wrapper.
   */
  private void setIsWrapperless(boolean b)
  {
    m_isWrapperless = b;
  }

  /**
   * Get if the stylesheet is without an xsl:stylesheet
   * and xsl:template wrapper.
   */
  boolean getIsWrapperless()
  {
    return m_isWrapperless;
  }

  /**
   * Get table of named Templates.
   * These are keyed on string macro names, and holding values
   * that are template elements in the XSL DOM tree.
   */
  private Hashtable getNamedTemplates()
  {
    return m_namedTemplates;
  }

  /**
   * Set table of named Templates.
   * These are keyed on string macro names, and holding values
   * that are template elements in the XSL DOM tree.
   */
  private void setNamedTemplates(Hashtable v)
  {
    m_namedTemplates = v;
  }

  /**
   * Given an element type, locate the start of a linked list of
   * possible template matches.
   */
  private MatchPattern2 locateMatchPatternList2(String sourceElementType, boolean tryWildCard)
  {
    MatchPattern2 startMatchList = null;
    Object val = m_patternTable.get(sourceElementType);
    if(null != val)
    {
      startMatchList = (MatchPattern2)val;
    }
    else if(tryWildCard)
    {
      val = m_patternTable.get("*");
      if(null != val)
      {
        startMatchList = (MatchPattern2)val;
      }
    }
    return startMatchList;
  }

  /**
   * A class to contain a match pattern and it's corresponding template.
   * This class also defines a node in a match pattern linked list.
   */
  class MatchPattern2 implements Serializable
  {
    /**
     * Construct a match pattern from a pattern and template.
     * @param pat For now a Nodelist that contains old-style element patterns.
     * @param template The node that contains the template for this pattern.
     * @param isMatchPatternsOnly tells if pat param contains only match
     * patterns (for compatibility with old syntax).
     */
    MatchPattern2(String pat, XPath exp, ElemTemplate template, int posInStylesheet,
                  String targetString, Stylesheet stylesheet, StepPattern pattern )
    {
      m_pattern = pat;
      m_template = template;
      m_posInStylesheet = posInStylesheet;
      m_targetString = targetString;
      m_stylesheet = stylesheet;
      m_expression = exp;
      m_stepPattern = pattern;
    }

    Stylesheet m_stylesheet;

    String m_targetString;

    XPath m_expression;
    public XPath getExpression() { return m_expression; }
    
    StepPattern m_stepPattern;
    public StepPattern getStepPattern() { return m_stepPattern; }

    int m_posInStylesheet;

    /**
     * Transient... only used to track priority while
     * processing.
     */
    double m_priority = XPath.MATCH_SCORE_NONE;

    private String m_pattern;
    public String getPattern() { return m_pattern; }

    private ElemTemplate m_template; // ref to the corrisponding template
    public ElemTemplate getTemplate() { return m_template; }

    private MatchPattern2 m_next = null; // null when at end of list.
    public MatchPattern2 getNext() { return m_next; }
    public void setNext(MatchPattern2 mp) { m_next = mp; }
  }

}
