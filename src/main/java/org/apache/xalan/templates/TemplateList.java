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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xml.dtm.DTM;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.compiler.PsuedoNames;
import org.apache.xpath.patterns.NodeTest;
import org.apache.xpath.patterns.StepPattern;
import org.apache.xpath.patterns.UnionPattern;

/**
 * Encapsulates a template list, and helps locate individual 
 * templates and functions.
 * 
 * @xsl.usage advanced
 */
public class TemplateList implements java.io.Serializable
{
  static final long serialVersionUID = 5803675288911728791L;

  /**
   * Construct a TemplateList object. Needs to be public so it can
   * be invoked from the CompilingStylesheetHandler.
   */
  public TemplateList()
  {
    super();
  }

  /**
   * Add a template to the table of named templates, and/or the table of templates
   * with match patterns. This method is also used to add xsl:function definitions
   * to a table of function definitions. 
   * 
   * This routine should be called in decreasing order of precedence but it 
   * checks nonetheless.
   *
   * @param template				This can be for an xsl:template definition or
   *                                xsl:function definition.
   * @throws TransformerException 
   */
  public void setTemplate(ElemTemplate template) throws TransformerException
  {    
	  
    if (!(template instanceof ElemFunction)) {    	
    	XPath matchXPath = template.getMatch();
    	
    	if ((template.getName() == null) && (matchXPath == null))
        {
            template.error(XSLTErrorResources.ER_NEED_NAME_OR_MATCH_ATTRIB,
                                                                      new Object[]{ "xsl:template" });
        }
    	
    	if (template.getName() != null)
        {
    		ElemTemplate existingTemplate = (ElemTemplate)(m_namedTemplates.get(template.getName()));
    		if (existingTemplate == null)
    		{
    			m_namedTemplates.put(template.getName(), template);
    		}
    		else {
    			int existingPrecedence = existingTemplate.getStylesheetComposed().getImportCountComposed();
    			int newPrecedence = template.getStylesheetComposed().getImportCountComposed();
    			if (newPrecedence == existingPrecedence) {
    				template.error(XSLTErrorResources.ER_DUPLICATE_NAMED_TEMPLATE,
    																		  new Object[]{ template.getName() });
    			}
    		}    		
        }
    	
    	if (matchXPath != null)
        {
    		Expression matchExpr = matchXPath.getExpression();

    		String xpathPatternStr = matchXPath.getPatternString();
    		if ((xpathPatternStr.startsWith("child::") || xpathPatternStr.startsWith("attribute::")) && 
    				                                                              xpathPatternStr.endsWith("document-node()")) {
    		   /**
    		    * XSL template definitions with match patterns like <xsl:template match="child::document-node()"> 
    		    * & <xsl:template match="attribute::document-node()"> are allowed by XSLT 3.0 spec, but these
    		    * XSL template definitions cannot match any XML document input nodes. Therefore, we make these 
    		    * XSL template definitions as unavailable within XSL compiled stylesheet.
    		    */
    			
    		   return;
    		}
    		
    		if (".".equals(xpathPatternStr) || xpathPatternStr.startsWith(".[")) {		 		 
    			insertPatternInTable(String.valueOf(xpathPatternStr), template);
    		}
    		else if (matchExpr instanceof StepPattern)
    		{
    			insertPatternInTable((StepPattern)matchExpr, template);
    		}
    		else if (matchExpr instanceof UnionPattern)
    		{
    			UnionPattern upat = (UnionPattern)matchExpr;
    			StepPattern[] pats = upat.getPatterns();
    			int n = pats.length;

    			for (int i = 0; i < n; i++)
    			{
    				insertPatternInTable(pats[i], template);
    			}
    		}
        }
    }
    else {
    	if (template.getName() == null)
        {
            template.error(XSLTErrorResources.ER_XSL_FUNCTION_NEED_NAME_ATTRIB,
                                                                      new Object[]{});            
        }
    	else {
    		ElemFunction newFunc = (ElemFunction)template;
    		int funcArity = newFunc.getArity();
    		boolean isOverrideDecl = newFunc.isOverrideAttrDeclared();
    		boolean isOverrideExtDecl = newFunc.isOverrideExtensionFunctionAttrDeclared();
    		if (isOverrideDecl && isOverrideExtDecl && (newFunc.getOverride() != newFunc.getOverrideExtensionFunction())) {
    			  QName funcName = newFunc.getName();
    			  throw new TransformerException("XTSE0020 : An XSL function '" + funcName.toString() + "' has both "
															                            + "the attributes 'override' and 'override-extension-function', "
															                            + "but they don't have the same value.", newFunc);
    	    }
    		
    		if (!isOverrideDecl) {
    			// An attribute 'override' is not declared. We set its value to default "yes".
    			newFunc.setOverride(true);
    		}
    		
    		if (!isOverrideExtDecl) {
    			// An attribute 'override-extension-function' is not declared. We set its value to default "yes".
    			newFunc.setOverrideExtensionFunction(true);
    		}
    		
    		QName funcQName = newFunc.getName();
    		if ((Constants.XSL_ERROR_NAMESACE).equals(funcQName.getNamespace())) {
    		   throw new TransformerException("XTSE0740 : An XSL function declaration that has local name '" + funcQName.getLocalName() 
    		                                                                                            + "', doesn't have function name's namespace.", newFunc);
    		}
    		
    		XslFunctionDefinitionKey funcDefnKey = new XslFunctionDefinitionKey(newFunc.getName(), funcArity, newFunc.getOverride());
    		ElemFunction xslFunctionObj = m_functionDeclarations.get(funcDefnKey);
    		if (xslFunctionObj == null) {
    			m_functionDeclarations.put(funcDefnKey, newFunc);
    		}
    		else {
    			int existingPrecedence = xslFunctionObj.getStylesheetComposed().getImportCountComposed();
    			int newPrecedence = newFunc.getStylesheetComposed().getImportCountComposed();
    			if (newPrecedence == existingPrecedence) {    				
    				if ((isOverrideDecl && newFunc.getOverride()) || (isOverrideExtDecl && newFunc.getOverrideExtensionFunction())) {
    					m_functionDeclarations.remove(funcDefnKey);
    					m_functionDeclarations.put(funcDefnKey, newFunc);
    				}
    				else {
    				    template.error(XSLTErrorResources.ER_DUPLICATE_XSL_FUNCTION,
    																		  new Object[]{ xslFunctionObj.getName(), funcDefnKey.getArity() });
    				}
    			}
    		}
    	 }
      }
    
  }

  /** Boolean value to indicate whether in DEBUG mode */
  final static boolean DEBUG = false;

  /**
   * Dump all patterns and elements that match those patterns
   *
   */
  void dumpAssociationTables()
  {

    Enumeration associations = m_patternTable.elements();

    while (associations.hasMoreElements())
    {
      TemplateSubPatternAssociation head =
        (TemplateSubPatternAssociation) associations.nextElement();

      while (null != head)
      {
        System.out.print("(" + head.getTargetString() + ", "
                         + head.getPattern() + ")");

        head = head.getNext();
      }

      System.out.println("\n.....");
    }

    TemplateSubPatternAssociation head = m_wildCardPatterns;

    System.out.print("wild card list: ");

    while (null != head)
    {
      System.out.print("(" + head.getTargetString() + ", "
                       + head.getPattern() + ")");

      head = head.getNext();
    }

    System.out.println("\n.....");
  }

  /**
   * After all templates have been added, this function
   * should be called.
   */
  public void compose(StylesheetRoot sroot)
  {

    if (DEBUG)
    {
      System.out.println("Before wildcard insert...");
      dumpAssociationTables();
    }

    if (null != m_wildCardPatterns)
    {
      Enumeration associations = m_patternTable.elements();

      while (associations.hasMoreElements())
      {    	
        TemplateSubPatternAssociation head =
          (TemplateSubPatternAssociation) associations.nextElement();
        TemplateSubPatternAssociation wild = m_wildCardPatterns;

        while (null != wild)
        {
          try
          {
            head = insertAssociationIntoList(
              head, (TemplateSubPatternAssociation) wild.clone(), true);
          }
          catch (CloneNotSupportedException cnse){}

          wild = wild.getNext();
        }
      }
    }

    if (DEBUG)
    {
      System.out.println("After wildcard insert...");
      dumpAssociationTables();
    }
  }

  /**
   * Insert the given TemplateSubPatternAssociation into the the linked
   * list.  Sort by import precedence, then priority, then by document order.
   *
   * @param head The first TemplateSubPatternAssociation in the linked list.
   * @param item The item that we want to insert into the proper place.
   * @param isWildCardInsert <code>true</code> if we are inserting a wild card 
   *             template onto this list.
   * @return the new head of the list.
   */
  private TemplateSubPatternAssociation
              insertAssociationIntoList(TemplateSubPatternAssociation head,
                                         TemplateSubPatternAssociation item,
                                         boolean isWildCardInsert)
  {

    // Sort first by import level (higher level is at front),
    // then by priority (highest priority is at front),
    // then by document order (later in document is at front).

    double priority = getPriorityOrScore(item);
    double workPriority;
    int importLevel = item.getImportLevel();
    int docOrder = item.getDocOrderPos();
    TemplateSubPatternAssociation insertPoint = head;
    TemplateSubPatternAssociation next;
    boolean insertBefore;         // true means insert before insertPoint; otherwise after
                                  // This can only be true if insertPoint is pointing to
                                  // the first or last template.

    // Spin down so that insertPoint points to:
    // (a) the template immediately _before_ the first template on the chain with
    // a precedence that is either (i) less than ours or (ii) the same as ours but
    // the template document position is less than ours
    // -or-
    // (b) the last template on the chain if no such template described in (a) exists.
    // If we are pointing to the first template or the last template (that is, case b),
    // we need to determine whether to insert before or after the template.  Otherwise,
    // we always insert after the insertPoint.

    while (true)
    {
      next = insertPoint.getNext();
      if (null == next)
        break;
      else
      {
        workPriority = getPriorityOrScore(next);
        if (importLevel > next.getImportLevel())
          break;
        else if (importLevel < next.getImportLevel())
          insertPoint = next;
        else if (priority > workPriority)               // import precedence is equal
          break;
        else if (priority < workPriority)
          insertPoint = next;
        else if (docOrder >= next.getDocOrderPos())     // priorities, import are equal
          break;
        else
          insertPoint = next;
      }
    }

    if ( (null == next) || (insertPoint == head) )      // insert point is first or last
    {
      workPriority = getPriorityOrScore(insertPoint);
      if (importLevel > insertPoint.getImportLevel())
        insertBefore = true;
      else if (importLevel < insertPoint.getImportLevel())
        insertBefore = false;
      else if (priority > workPriority)
        insertBefore = true;
      else if (priority < workPriority)
        insertBefore = false;
      else if (docOrder >= insertPoint.getDocOrderPos())
        insertBefore = true;
      else
        insertBefore = false;
    }
    else
      insertBefore = false;

    // System.out.println("appending: "+target+" to "+matchPat.getPattern());
    
    if (isWildCardInsert)
    {
      if (insertBefore)
      {
        item.setNext(insertPoint);

        String key = insertPoint.getTargetString();

        item.setTargetString(key);
        putHead(key, item);
        return item;
      }
      else
      {
        item.setNext(next);
        insertPoint.setNext(item);
        return head;
      }
    }
    else
    {
      if (insertBefore)
      {
        item.setNext(insertPoint);

        if (insertPoint.isWild() || item.isWild())
          m_wildCardPatterns = item;
        else
          putHead(item.getTargetString(), item);
        return item;
      }
      else
      {
        item.setNext(next);
        insertPoint.setNext(item);
        return head;
      }
    }
  }

  /**
   * Add a template to the template list.
   *
   * @param pattern
   * @param template
   */
  private void insertPatternInTable(StepPattern pattern, ElemTemplate template)
  {

    String target = pattern.getTargetString();

    if (null != target)
    {
      String pstring = template.getMatch().getPatternString();
      TemplateSubPatternAssociation association =
        new TemplateSubPatternAssociation(template, pattern, pstring);

      // See if there's already one there
      boolean isWildCard = association.isWild();
      TemplateSubPatternAssociation head = isWildCard
                                           ? m_wildCardPatterns
                                           : getHead(target);

      if (null == head)
      {
        if (isWildCard)
          m_wildCardPatterns = association;
        else
          putHead(target, association);
      }
      else
      {
        insertAssociationIntoList(head, association, false);
      }
    }
  }
  
  /**
   * Add a template to the template list.
   * 
   * This method definition supports XSLT 3.0 xsl:template pattern strings 
   * like ".", and .[...] (this is XPath pattern '.' followed by predicate 
   * that imposes a boolean filter condition on context value '.'. Only 
   * one predicate is supported here).
   *
   * @param pattern
   * @param template
   */
  private void insertPatternInTable(String pattern, ElemTemplate template) {		
	  TemplateSubPatternAssociation association =
			  								new TemplateSubPatternAssociation(template, null, pattern);
	  
	  String patternTableKeyStr = pattern; 

	  QName templateModeQname = template.getMode();
	  
	  String templateModeStr = null;
	  if (templateModeQname != null) {
		  templateModeStr = templateModeQname.toString();   
	  }
	  
	  if (templateModeStr != null) {
		  patternTableKeyStr = (patternTableKeyStr + Constants.XSL_PATTERN_TABLE_DELIM + templateModeStr);  
	  }
	  else {
		  patternTableKeyStr = (patternTableKeyStr + Constants.XSL_PATTERN_TABLE_DELIM + "mode_unspecified");		  
	  }
	  
	  double templatePriority = template.getPriority();
	  String templatePriorityStr = String.valueOf(templatePriority);
	  
	  patternTableKeyStr = (patternTableKeyStr + Constants.XSL_PATTERN_TABLE_DELIM + templatePriorityStr);
	  
	  m_patternTable.put(patternTableKeyStr, association);
  }

  /**
   * Given a match pattern and template association, return the 
   * score of that match.  This score or priority can always be 
   * statically calculated.
   *
   * @param matchPat The match pattern to template association.
   *
   * @return {@link org.apache.xpath.patterns.NodeTest#SCORE_NODETEST}, 
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_NONE}, 
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_NSWILD}, 
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_QNAME}, or
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_OTHER}, or 
   *         the value defined by the priority attribute of the template.
   *
   */
  private double getPriorityOrScore(TemplateSubPatternAssociation matchPat)
  {

    double priority = matchPat.getTemplate().getPriority();

    if (priority == XPath.MATCH_SCORE_NONE)
    {
      Expression ex = matchPat.getStepPattern();

      if (ex instanceof NodeTest)
      {
        return ((NodeTest) ex).getDefaultScore();
      }
    }

    return priority;
  }

  /**
   * Locate a named template.
   *
   * @param qname  Qualified name of the template.
   *
   * @return 	   An xsl:template definition object instance, or null 
   *               if not found.
   */
  public ElemTemplate getTemplate(QName qname)
  {
	  return (ElemTemplate) m_namedTemplates.get(qname);
  }
  
  /**
   * Locate an XSL stylesheet function.
   * 
   * @param qname			     An XSL stylesheet function's name
   * @param arity		       	 An XSL stylesheet function's arity
   * 
   * @return                     An XSL stylesheet function's compiled
   *                             object.
   */
  public ElemTemplate getXslFunction(QName qname, int arity)
  {
	  XslFunctionDefinitionKey xslFunctionDefinitionKey = new XslFunctionDefinitionKey(qname, arity, true);
	  ElemFunction elemFunc = m_functionDeclarations.get(xslFunctionDefinitionKey);
	  if (elemFunc == null) {
		  elemFunc = m_functionDeclarations.get(new XslFunctionDefinitionKey(qname, arity, false));  
	  }

	  return elemFunc;
  }

  /**
   * Get the head as an TemplateSubPatternAssociation object of the most 
   * likely list of associations to check, based on the name and type of 
   * the targetNode argument.
   *
   * @param xctxt The XPath runtime context.
   * @param targetNode The target node that will be checked for a match.
   * @param dtm The dtm owner for the target node.
   *
   * @return An TemplateSubPatternAssociation object, which is the head of a 
   *         linked list that contains all possible match pattern to template 
   *         associations.
   */
  public TemplateSubPatternAssociation getHead(XPathContext xctxt, 
                                               int targetNode, DTM dtm)
  {
    TemplateSubPatternAssociation head;
    
    short nodeType = dtm.getNodeType(targetNode);    
    switch (nodeType)
    {
    case DTM.ELEMENT_NODE :
    case DTM.ATTRIBUTE_NODE :
      head = (TemplateSubPatternAssociation)(m_patternTable.get(
        dtm.getLocalName(targetNode)));
      break;
    case DTM.TEXT_NODE :
    case DTM.CDATA_SECTION_NODE :
      head = m_textPatterns;
      break;
    case DTM.ENTITY_REFERENCE_NODE :
    case DTM.ENTITY_NODE :
      head = (TemplateSubPatternAssociation)(m_patternTable.get(
        dtm.getNodeName(targetNode))); // %REVIEW% I think this is right
      break;
    case DTM.PROCESSING_INSTRUCTION_NODE :
      head = (TemplateSubPatternAssociation)(m_patternTable.get(
        dtm.getLocalName(targetNode)));
      break;
    case DTM.COMMENT_NODE :
      head = m_commentPatterns;
      break;
    case DTM.DOCUMENT_NODE :
    case DTM.DOCUMENT_FRAGMENT_NODE :
      head = m_docPatterns;
      break;
    case DTM.NOTATION_NODE :
    default :
      head = (TemplateSubPatternAssociation)(m_patternTable.get(
        dtm.getNodeName(targetNode))); // %REVIEW% I think this is right
    }

    return (null == head) ? m_wildCardPatterns : head;
  }
  
  /**
   * Method definition, to get an XSL template rule object reference, 
   * that best matches the supplied XML node.
   *
   * @param xctxt                          An XPath context object reference
   * @param targetNode				       An xdm node's, integer valued node handle
   * @param expTypeID                      An ID value, that represents an xdm node's 
   *                                       expanded name.                                 
   * @param mode                           A QName value indicating an XSL template mode
   * @param maxImportLevel                 The maximum importCountComposed that we should consider or -1
   *                                       if we should consider all import levels. This is used by 
   *                                       apply-imports to access templates that have been overridden.
   * @param quietConflictWarnings          If this method argument is true, XSL template conflicts
   *                                       shall be evaluated and appropriate XSL transform error or 
   *                                       warning shall be emitted.
   * @param dtm                            An DTM object instance, for the supplied node reference
   * @param xslOnMultipleMatchStr          xsl:mode instruction "on-multiple-match" attribute's value
   * @param xslWarningOnMultipleMatch      xsl:mode instruction "warning-on-multiple-match" attribute's value 
   * @param isXdmParentlessSiblingNodes    Boolean value, telling whether 'targetNode' for which XSL template 
   *                                       rule needs to be found, doesn't have an XDM parent node.
   *                                        
   * @return                               An XSL template rule object reference  
   * @throws TransformerException 
   */
  public ElemTemplate getTemplateFast(XPathContext xctxt, int targetNode, int expTypeID,
                                      QName mode, int maxImportLevel, boolean quietConflictWarnings,
                                      DTM dtm, String xslOnMultipleMatchStr, boolean xslWarningOnMultipleMatch, 
                                                                                                     boolean isXdmParentlessSiblingNodes) throws TransformerException
  {
    
    TemplateSubPatternAssociation head;

    short nodeType = dtm.getNodeType(targetNode);
    
    switch (nodeType)
    {
    case DTM.ELEMENT_NODE :
    case DTM.ATTRIBUTE_NODE :
    	head = (TemplateSubPatternAssociation)(m_patternTable.get(
    			dtm.getLocalNameFromExpandedNameID(expTypeID)));
    	break;
    case DTM.TEXT_NODE :
    case DTM.CDATA_SECTION_NODE :
    	head = m_textPatterns;
    	break;
    case DTM.ENTITY_REFERENCE_NODE :
    case DTM.ENTITY_NODE :
    	head = (TemplateSubPatternAssociation)(m_patternTable.get(
    			dtm.getNodeName(targetNode))); // %REVIEW% I think this is right
    	break;
    case DTM.PROCESSING_INSTRUCTION_NODE :
    	head = (TemplateSubPatternAssociation)(m_patternTable.get(
    			dtm.getLocalName(targetNode)));
    	break;
    case DTM.COMMENT_NODE :
    	head = m_commentPatterns;
    	break;
    case DTM.DOCUMENT_NODE :
    case DTM.DOCUMENT_FRAGMENT_NODE :
    	head = m_docPatterns;
    	break;
    case DTM.NOTATION_NODE :
    default :
    	head = (TemplateSubPatternAssociation)(m_patternTable.get(
    			dtm.getNodeName(targetNode))); // %REVIEW% I think this is right
    }

    if (head == null)
    {
    	head = m_wildCardPatterns;
    	if (head == null)
    		return null;
    }                                              

    xctxt.pushNamespaceContextNull();
    
    try
    {
      do
      {
    	  if ((maxImportLevel > -1) && (head.getImportLevel() > maxImportLevel))
    	  {
    		  continue;
    	  }
    	  
    	  ElemTemplate template = head.getTemplate();        
    	  xctxt.setNamespaceContext(template);
    	  
    	  if (isXdmParentlessSiblingNodes) {    		      		      		  
    		  int prevSibling = dtm.getPreviousSibling(targetNode);
    		  boolean isXslDocumentChild = false;
    		  while (prevSibling != DTM.NULL) {
    			  String str1 = dtm.getNodeValue(prevSibling);
    			  if ((Constants.XSL_DOCUMENT_INSTRUCTION_MARKER).equals(str1)) {
    				  isXslDocumentChild = true;

    				  break;
    			  }

    			  prevSibling = dtm.getPreviousSibling(prevSibling); 
    		  }
    		  
    		  String nodeName = dtm.getNodeName(targetNode);

    		  String headPatternStr = head.getPattern();

    		  if (headPatternStr.startsWith("/") && !(headPatternStr.startsWith("//"))) {
    			  if (head.getNext() != null) {
    				  continue;  
    			  }
    		  }

    		  if (headPatternStr.startsWith("//")) {
    			  if ((head.m_stepPattern.execute(xctxt, targetNode, dtm, expTypeID) != NodeTest.SCORE_NONE)
    					                                                                                    && head.matchMode(mode)) {    		  
    				  TemplateSubPatternAssociation head1 = head;    		      		      		  

    				  while ((head1 = head1.getNext()) != null) {    		  
    					  String head1PatternStr = head1.getPattern();

    					  ElemTemplate template1 = head1.getTemplate();    					  

    					  if (head1PatternStr.equals(headPatternStr.substring(2))) {    					      					  
    						  if (quietConflictWarnings) {
    							  checkConflicts(head1, xctxt, targetNode, mode, dtm, expTypeID, 
    									                                                    xslOnMultipleMatchStr, xslWarningOnMultipleMatch);
    						  }

    						  if (!isXslDocumentChild) {
    							  return template1;
    						  }
    					  }
    					  
    					  Expression matchExpr2 = (template1.getMatch()).getExpression();
    					  if (matchExpr2 instanceof UnionPattern) {
    						  UnionPattern unionPattern = (UnionPattern)matchExpr2;
    						  StepPattern[] stepPatternArr = unionPattern.getPatterns();
    						  int stepPatternCount = stepPatternArr.length;
    						  for (int idx = 0; idx < stepPatternCount; idx++) {
    							  StepPattern stepPattern = stepPatternArr[idx];
    							  if ((stepPattern.execute(xctxt, targetNode, dtm, expTypeID) != NodeTest.SCORE_NONE)
    									                                                                            && head1.matchMode(mode)) {
    								  if (quietConflictWarnings) {
    									  checkConflicts(head1, xctxt, targetNode, mode, dtm, expTypeID, 
    											                                                    xslOnMultipleMatchStr, xslWarningOnMultipleMatch);
    								  }

    								  if (!isXslDocumentChild) {
    								     return template1;
    								  }
    							  }
    						  }
    					  }
    				  }
    			  }
    		  }
          }
    	  
    	  if ((head.m_stepPattern.execute(xctxt, targetNode, dtm, expTypeID) != NodeTest.SCORE_NONE)
    			  																					&& head.matchMode(mode)) {
    		  if (quietConflictWarnings) {
    			  checkConflicts(head, xctxt, targetNode, mode, dtm, expTypeID, 
    					                                                   xslOnMultipleMatchStr, xslWarningOnMultipleMatch);
    		  }

    		  return template;
    	  }
      }
      while (null != (head = head.getNext()));
    }
    finally
    {
    	xctxt.popNamespaceContext();
    }

    return null;
    
  }  // end getTemplateFast

  /**
   * Given a target element, find the template that best
   * matches in the given XSL document, according
   * to the rules specified in the xsl draft.
   *
   * @param xctxt
   * @param targetNode
   * @param mode A string indicating the display mode.
   * @param quietConflictWarnings
   * @return Rule that best matches targetElem.
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws TransformerException
   */
  public ElemTemplate getTemplate(XPathContext xctxt,
                                int targetNode,
                                QName mode,
                                boolean quietConflictWarnings,
                                DTM dtm)
            throws TransformerException
  {

    TemplateSubPatternAssociation head = getHead(xctxt, targetNode, dtm);

    if (null != head)
    {
      xctxt.pushNamespaceContextNull();
      xctxt.pushCurrentNodeAndExpression(targetNode, targetNode);
      try
      {    	
        do
        {
          ElemTemplate template = head.getTemplate();        
          xctxt.setNamespaceContext(template);
          
          if ((head.m_stepPattern.execute(xctxt, targetNode) != NodeTest.SCORE_NONE)
                  																	&& head.matchMode(mode))
          {
            if (quietConflictWarnings) {
                int expTypeID = dtm.getExpandedTypeID(targetNode);
                checkConflicts(head, xctxt, targetNode, mode, dtm, expTypeID, null, false);
            }

            return template;
          }
        }
        while (null != (head = head.getNext()));
      }
      finally
      {
        xctxt.popCurrentNodeAndExpression();
        xctxt.popNamespaceContext();
      }
    }

    return null;
  }  // end getTemplate
  
  /**
   * Given a target element, find the template that best
   * matches in the given XSL document, according
   * to the rules specified in the xsl draft.
   *
   * @param xctxt
   * @param targetNode
   * @param mode A string indicating the display mode.
   * @param maxImportLevel The maximum importCountComposed that we should consider or -1
   *        if we should consider all import levels.  This is used by apply-imports to
   *        access templates that have been overridden.
   * @param endImportLevel The count of composed imports
   * @param quietConflictWarnings
   * @return Rule that best matches targetElem.
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws TransformerException
   */
  public ElemTemplate getTemplate(XPathContext xctxt,
                                int targetNode,
                                QName mode,
                                int maxImportLevel, int endImportLevel,
                                boolean quietConflictWarnings,
                                DTM dtm)
            throws TransformerException
  {

    TemplateSubPatternAssociation head = getHead(xctxt, targetNode, dtm);

    if (null != head)
    {
      xctxt.pushNamespaceContextNull();
      xctxt.pushCurrentNodeAndExpression(targetNode, targetNode);
      try
      {
        do
        {
          if ( (maxImportLevel > -1) && (head.getImportLevel() > maxImportLevel))
          {
            continue;
          }
          if (head.getImportLevel()<= maxImportLevel - endImportLevel)
            return null;
          ElemTemplate template = head.getTemplate();        
          xctxt.setNamespaceContext(template);
          
          if ((head.m_stepPattern.execute(xctxt, targetNode) != NodeTest.SCORE_NONE)
                  																	&& head.matchMode(mode))
          {
            if (quietConflictWarnings) {
               int exNodeType = dtm.getExpandedTypeID(targetNode);
               checkConflicts(head, xctxt, targetNode, mode, dtm, exNodeType, null, false);
            }

            return template;
          }
        }
        while (null != (head = head.getNext()));
      }
      finally
      {
        xctxt.popCurrentNodeAndExpression();
        xctxt.popNamespaceContext();
      }
    }

    return null;
  }  // end findTemplate

  /**
   * Get a TemplateWalker for use by a compiler.  See the documentation for
   * the TreeWalker inner class for further details.
   */
  public TemplateWalker getWalker()
  {
    return new TemplateWalker();
  }
  
  public ElemTemplate getTemplate(String patternStr)
  {
	  return (ElemTemplate)m_patternTable.get(patternStr);
  }

  /**
   * Method definition, to check for XSL template rule match conflicts.
   *
   * @param head                                    The first, TemplateSubPatternAssociation object
   *                                                instance within template pattern association
   *                                                linked list.
   * @param xctxt                                   The current, XPath context object instance
   * @param targetNode                              Node matching XSL template pattern
   * @param mode                                    QName object reference, to the current mode
   * @param dtm										An XML document model instance
   * @param expTypeID  
   * @param xslOnMultipleMatchStr                   xsl:mode instruction "on-multiple-match"
   *                                                attribute's value.
   * @param xslWarningOnMultipleMatch               xsl:mode instruction "warning-on-multiple-match"
   *                                                attribute's value.
   * @throws TransformerException 
   */
  private void checkConflicts(TemplateSubPatternAssociation head, XPathContext xctxt, int targetNode, 
		                                                          QName mode, DTM dtm, int expTypeID, String xslOnMultipleMatchStr, 
		                                                          boolean xslWarningOnMultipleMatch) throws TransformerException
  {	  
	   
	  if ((Constants.ATTRVAL_FAIL).equals(xslOnMultipleMatchStr)) {
		  TemplateSubPatternAssociation next = head;
		  double headNodePriority = (head.getTemplate()).getPriority();
		  while ((next = next.getNext()) != null) {
			  double nextNodePriority = (next.getTemplate()).getPriority();
			  if ((next.m_stepPattern.execute(xctxt, targetNode) != NodeTest.SCORE_NONE) 
																					  && next.matchMode(mode) 
																					  && (nextNodePriority == headNodePriority)) {
				  /**
				   * Emit an XSL stylesheet dynamic error, when xsl:mode instruction 
				   * specifies an attribute "on-multiple-match" with value 'fail'.   
				   */

				  int nodeType = dtm.getNodeType(targetNode);
				  String nodeTypeStr = getNodeTypeString(nodeType);

				  String nodeNameStr = dtm.getNodeName(targetNode);

				  SourceLocator srcLocator1 = (SourceLocator)(head.getTemplate());
				  int lineNo1 = srcLocator1.getLineNumber(); 
				  SourceLocator srcLocator2 = (SourceLocator)(next.getTemplate());
				  int lineNo2 = srcLocator2.getLineNumber();

				  String errMesg = "XTDE0540 : More than one XSL template rule matched an XML " + nodeTypeStr + " node";
				  if (nodeNameStr != null) {
					  errMesg = (errMesg + " '" + nodeNameStr + "'."); 
				  }
				  else {
					  errMesg = (errMesg + "."); 
				  }

				  throw new TransformerException(errMesg + " Conflicting template rule locations are line " + lineNo1 
						  																				+ " and line " + lineNo2 +".", srcLocator1); 
			  }
		  }
	  }
	   
	  if (xslWarningOnMultipleMatch) {
		  TemplateSubPatternAssociation next = head;
		  double headNodePriority = (head.getTemplate()).getPriority();
		  while ((next = next.getNext()) != null) {
			  double nextNodePriority = (next.getTemplate()).getPriority();
			  if ((next.m_stepPattern.execute(xctxt, targetNode) != NodeTest.SCORE_NONE) 
																					  && next.matchMode(mode) 
																					  && (nextNodePriority == headNodePriority)) {
				  /**
				   * Emit an XSL stylesheet processing warning, when xsl:mode instruction 
				   * specifies an attribute "warning-on-multiple-match" with value true.
				   */

				  int nodeType = dtm.getNodeType(targetNode);
				  String nodeTypeStr = getNodeTypeString(nodeType);

				  String nodeNameStr = dtm.getNodeName(targetNode);

				  SourceLocator srcLocator1 = (SourceLocator)(head.getTemplate());
				  int lineNo1 = srcLocator1.getLineNumber(); 
				  SourceLocator srcLocator2 = (SourceLocator)(next.getTemplate());
				  int lineNo2 = srcLocator2.getLineNumber();

				  String errMesg = "Warning : More than one XSL template rule matched an XML " + nodeTypeStr + " node";
				  if (nodeNameStr != null) {
					  errMesg = (errMesg + " '" + nodeNameStr + "'."); 
				  }
				  else {
					  errMesg = (errMesg + "."); 
				  }

				  ErrorListener errorListener = xctxt.getErrorListener();				   
				  errorListener.warning(new TransformerException(errMesg + " Conflicting template rule locations are line " + lineNo1 
						  																								+ " and line " + lineNo2 +".", srcLocator1)); 
			  }
		  }
	  }
  }

  /**
   * Method definition, to get xdm node type string 
   * value, for the supplied node type integer value.
   * 
   * @param nodeType				Node type integer value
   * @return						Node type string value
   */
  private String getNodeTypeString(int nodeType) {
	  
	  String result = null;

	  if (nodeType == DTM.ELEMENT_NODE) {
		  result = Constants.ELEMNAME_ELEMENT_STRING;  
	  }
	  else if (nodeType == DTM.ATTRIBUTE_NODE) {
		  result = Constants.ELEMNAME_ATTRIBUTE_STRING;  
	  }
	  else if (nodeType == DTM.TEXT_NODE) {
		  result = Constants.ELEMNAME_TEXT_STRING; 
	  }
	  else if (nodeType == DTM.COMMENT_NODE) {
		  result = Constants.ELEMNAME_COMMENT_STRING;	
	  }
	  else if (nodeType == DTM.PROCESSING_INSTRUCTION_NODE) {
		  result = Constants.ELEMNAME_PI_STRING; 
	  }
	  
	  return result;
  }

  /**
   * Keyed on string macro names, and holding values
   * that are macro elements in the XSL DOM tree.
   * Initialized in initMacroLookupTable, and used in
   * findNamedTemplate.
   */
  private Hashtable m_namedTemplates = new Hashtable(89);
  
  /**
   * An java.util.Map object to store xsl:function declarations 
   * within an XSL stylesheet. 
   */
  private Map<XslFunctionDefinitionKey, ElemFunction> m_functionDeclarations = new HashMap<XslFunctionDefinitionKey, ElemFunction>();

  /**
   * This table is keyed on the target elements
   * of patterns, and contains linked lists of
   * the actual patterns that match the target element
   * to some degree of specifity.
   */
  private Hashtable m_patternTable = new Hashtable(89);

  /** 
   * Wildcard patterns.
   */
  private TemplateSubPatternAssociation m_wildCardPatterns = null;

  /** 
   * Text Patterns.
   */
  private TemplateSubPatternAssociation m_textPatterns = null;

  /** 
   * Root document Patterns.
   */
  private TemplateSubPatternAssociation m_docPatterns = null;

  /** 
   * Comment Patterns.
   */
  private TemplateSubPatternAssociation m_commentPatterns = null;

  /**
   * Get the head of the assocation list that is keyed by target.
   *
   * @param key The name of a node. 
   *
   * @return The head of a linked list that contains all possible match pattern to 
   * template associations for the given key.
   */
  private TemplateSubPatternAssociation getHead(String key)
  {
    return (TemplateSubPatternAssociation) m_patternTable.get(key);
  }

  /**
   * Get the head of the assocation list that is keyed by target.
   *
   * @param key
   * @param assoc
   */
  private void putHead(String key, TemplateSubPatternAssociation assoc)
  {

    if (key.equals(PsuedoNames.PSEUDONAME_TEXT))
      m_textPatterns = assoc;
    else if (key.equals(PsuedoNames.PSEUDONAME_ROOT))
      m_docPatterns = assoc;
    else if (key.equals(PsuedoNames.PSEUDONAME_COMMENT))
      m_commentPatterns = assoc;

    m_patternTable.put(key, assoc);
  }

  /**
   * An inner class used by a compiler to iterate over all of the ElemTemplates
   * stored in this TemplateList.  The compiler can replace returned templates
   * with their compiled equivalent.
   */
  public class TemplateWalker
  {
    private Enumeration hashIterator;
    private boolean inPatterns;
    private TemplateSubPatternAssociation curPattern;

    private Hashtable m_compilerCache = new Hashtable();

    private TemplateWalker()
    {
      hashIterator = m_patternTable.elements();
      inPatterns = true;
      curPattern = null;
    }

    public ElemTemplate next()
    {

      ElemTemplate retValue = null;
      ElemTemplate ct;

      while (true)
      {
        if (inPatterns)
        {
          if (null != curPattern)
            curPattern = curPattern.getNext();

          if (null != curPattern)
            retValue = curPattern.getTemplate();
          else
          {
            if (hashIterator.hasMoreElements())
            {
              curPattern = (TemplateSubPatternAssociation) hashIterator.nextElement();
              retValue =  curPattern.getTemplate();
            }
            else
            {
              inPatterns = false;
              hashIterator = m_namedTemplates.elements();
            }
          }
        }

        if (!inPatterns)
        {
          if (hashIterator.hasMoreElements())
            retValue = (ElemTemplate) hashIterator.nextElement();
          else
            return null;
        }

        ct = (ElemTemplate) m_compilerCache.get(new Integer(retValue.getUid()));
        if (null == ct)
        {
          m_compilerCache.put(new Integer(retValue.getUid()), retValue);
          return retValue;
        }
      }
    }
  }

  public Hashtable getPatternTable() {
	  return m_patternTable;
  }

  public TemplateSubPatternAssociation getWildCardPatterns() {
	  return m_wildCardPatterns;
  }

  public void setWildCardPatterns(TemplateSubPatternAssociation wildCardPatterns) {
	  this.m_wildCardPatterns = wildCardPatterns;
  }

}
