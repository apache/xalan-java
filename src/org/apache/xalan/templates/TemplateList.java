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
import java.util.Vector;
import java.util.Enumeration;

import java.io.Serializable;

import org.w3c.dom.Node;

import org.xml.sax.SAXException;

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
   *
   * @param stylesheet -- The stylesheet owner, must be valid (non-null).
   */
  TemplateList(Stylesheet stylesheet)
  {
    m_stylesheet = stylesheet;
    m_stylesheetComposed = m_stylesheet.getStylesheetComposed();
  }

  /**
   * Add a template to the template list.
   *
   * @param template
   */
  public void setTemplate(ElemTemplate template)
  {

    int pos = 0;

    if (null == m_firstTemplate)
    {
      m_firstTemplate = template;
    }
    else
    {
      ElemTemplateElement next = m_firstTemplate;

      while (null != next)
      {
        if (null == next.m_nextSibling)
        {
          next.m_nextSibling = template;
          template.m_nextSibling = null;  // just to play it safe.

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

    if (null != template.getName())
    {
      if (m_namedTemplates.get(template.getName()) == null)
      {
        m_namedTemplates.put(template.getName(), template);
      }
      else
      {
        template.error(XSLTErrorResources.ER_DUPLICATE_NAMED_TEMPLATE,
                       new Object[]{ template.getName() });
      }
    }

    XPath matchXPath = template.getMatch();

    if (null != matchXPath)
    {
      Expression matchExpr = matchXPath.getExpression();

      if (matchExpr instanceof StepPattern)
      {
        insertPatternInTable((StepPattern) matchExpr, template, pos);
      }
      else if (matchExpr instanceof UnionPattern)
      {
        UnionPattern upat = (UnionPattern) matchExpr;
        StepPattern[] pats = upat.getPatterns();
        int n = pats.length;

        for (int i = 0; i < n; i++)
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

  /** NEEDSDOC Field DEBUG          */
  boolean DEBUG = false;

  /**
   * NEEDSDOC Method dumpAssociationTables 
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
  public void compose()
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
   * list.  Sort by priority first, then by document order.
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

    // Sort first by decreasing priority (highest priority is at front),
    // then by document order (later in document is at front).
    // GLP:  This routine sorts by the document order obtained by getDocOrderPos() on
    //       the item.  However, this is only the sequence within the individual
    //       sheet, not within a composed sheet.  This needs to be fixed.

    double priority = getPriorityOrScore(item);
    double workPriority;
    int docOrder = item.getDocOrderPos();
    TemplateSubPatternAssociation insertPoint = head;
    TemplateSubPatternAssociation next;
    boolean insertBefore;         // true means insert before insertPoint; otherwise after
                                  // This can only be true if insertPoint is pointing to
                                  // the first or last template.

    // Spin down so that insertPoint points to:
    // (a) the template immediately _before_ the first template on the chain with
    // a priority that is either (i) less than ours or (ii) the same as ours but
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
        if (priority > workPriority)
          break;
        else if (priority < workPriority)
          insertPoint = next;
        else if (docOrder >= next.getDocOrderPos())      // priorities are equal
          break;
        else
          insertPoint = next;
      }
    }

    if ( (null == next) || (insertPoint == head) )      // insert point is first or last
    {
      workPriority = getPriorityOrScore(insertPoint);
      if (priority > workPriority)
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
   * @param pos
   */
  private void insertPatternInTable(StepPattern pattern,
                                    ElemTemplate template, int pos)
  {

    String target = pattern.getTargetString();

    if (null != target)
    {
      String pstring = template.getMatch().getPatternString();
      TemplateSubPatternAssociation association =
        new TemplateSubPatternAssociation(template, pattern, pstring, pos);

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
   * <meta name="usage" content="internal"/>
   * Method getPriorityOrScore  <needs-description/>
   *
   *
   * @param matchPat
   *
   * @return
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
   * Locate a macro via the "name" attribute.
   *
   * @param qname
   *
   * @return
   * @exception XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   */
  public ElemTemplate getTemplate(QName qname)
  {

    ElemTemplate namedTemplate = (ElemTemplate) m_namedTemplates.get(qname);

    if (null == namedTemplate)
    {
      StylesheetComposed stylesheet = m_stylesheetComposed;
      int n = stylesheet.getImportCountComposed();

      for (int i = 0; i < n; i++)
      {
        StylesheetComposed imported = stylesheet.getImportComposed(i);

        namedTemplate = imported.getTemplateComposed(qname);

        if (null != namedTemplate)
        {
          break;
        }
      }
    }

    return namedTemplate;
  }

  /**
   * Get the head of the most likely list of associations to check.
   *
   * @param xctxt
   * @param targetNode
   *
   * @return
   */
  TemplateSubPatternAssociation getHead(XPathContext xctxt, Node targetNode)
  {

    short targetNodeType = targetNode.getNodeType();
    TemplateSubPatternAssociation head;

    switch (targetNodeType)
    {
    case Node.ELEMENT_NODE :
    case Node.ATTRIBUTE_NODE :
      head = (TemplateSubPatternAssociation) m_patternTable.get(
        xctxt.getDOMHelper().getLocalNameOfNode(targetNode));
      break;
    case Node.TEXT_NODE :
    case Node.CDATA_SECTION_NODE :
      head = m_textPatterns;
      break;
    case Node.ENTITY_REFERENCE_NODE :
    case Node.ENTITY_NODE :
      head = (TemplateSubPatternAssociation) m_patternTable.get(
        targetNode.getNodeName());
      break;
    case Node.PROCESSING_INSTRUCTION_NODE :
      head = (TemplateSubPatternAssociation) m_patternTable.get(
        xctxt.getDOMHelper().getLocalNameOfNode(targetNode));
      break;
    case Node.COMMENT_NODE :
      head = m_commentPatterns;
      break;
    case Node.DOCUMENT_NODE :
    case Node.DOCUMENT_FRAGMENT_NODE :
      head = m_docPatterns;
      break;
    case Node.NOTATION_NODE :
    default :
      head = (TemplateSubPatternAssociation) m_patternTable.get(
        targetNode.getNodeName());
    }

    return (null == head) ? m_wildCardPatterns : head;
  }

  /**
   * Given a target element, find the template that best
   * matches in the given XSL document, according
   * to the rules specified in the xsl draft.
   * @param stylesheetTree Where the XSL rules are to be found.
   * @param targetElem The element that needs a rule.
   *
   * @param xctxt
   * @param targetNode
   * @param mode A string indicating the display mode.
   * @param useImports means that this is an xsl:apply-imports commend.
   * @param quietConflictWarnings
   * @return Rule that best matches targetElem.
   * @exception XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws SAXException
   */
  public ElemTemplate getTemplate(
          XPathContext xctxt, Node targetNode, QName mode, boolean quietConflictWarnings)
            throws SAXException
  {

    TemplateSubPatternAssociation head = getHead(xctxt, targetNode);

    if (null != head)
    {
      try
      {
        xctxt.pushCurrentNodeAndExpression(targetNode, targetNode);

        do
        {
          if ((head.m_stepPattern.execute(xctxt) != NodeTest.SCORE_NONE)
                  && head.matchMode(mode))
          {
            if (quietConflictWarnings)
              checkConflicts(head, xctxt, targetNode, mode);

            return head.getTemplate();
          }
        }
        while (null != (head = head.getNext()));
      }
      finally
      {
        xctxt.popCurrentNodeAndExpression();
      }
    }

    int n = m_stylesheetComposed.getImportCountComposed();

    if (0 != n)
    {
      for (int i = 0; i < n; i++)
      {
        StylesheetComposed imported =
          m_stylesheetComposed.getImportComposed(i);
        ElemTemplate t = getTemplate(imported, xctxt, targetNode, mode,
                                     quietConflictWarnings);

        if (null != t)
          return t;
      }
    }

    return null;
  }  // end findTemplate

  /**
   * Check for match conflicts, and warn the stylesheet author.
   *
   * NEEDSDOC @param head
   * NEEDSDOC @param xctxt
   * NEEDSDOC @param targetNode
   * NEEDSDOC @param mode
   */
  private void checkConflicts(TemplateSubPatternAssociation head,
                              XPathContext xctxt, Node targetNode, QName mode)
  {

    // TODO: Check for conflicts.
  }

  /**
   * For derived classes to override which method gets accesed to
   * get the imported template.
   *
   * @param imported
   * @param support
   * @param targetNode
   * @param mode
   * @param quietConflictWarnings
   *
   * @return
   *
   * @throws SAXException
   */
  protected ElemTemplate getTemplate(
          StylesheetComposed imported, XPathContext support, Node targetNode, QName mode, boolean quietConflictWarnings)
            throws SAXException
  {
    return imported.getTemplateComposed(support, targetNode, mode,
                                        quietConflictWarnings);
  }

  /**
   * Add object to vector if not already there.
   *
   * @param obj
   * @param v
   */
  private void addObjectIfNotFound(Object obj, Vector v)
  {

    int n = v.size();
    boolean addIt = true;

    for (int i = 0; i < n; i++)
    {
      if (v.elementAt(i) == obj)
      {
        addIt = false;

        break;
      }
    }

    if (addIt)
    {
      v.addElement(obj);
    }
  }

  /**
   * The stylesheet owner of the list.
   */
  private Stylesheet m_stylesheet;

  /** NEEDSDOC Field m_stylesheetComposed          */
  private StylesheetComposed m_stylesheetComposed;

  /**
   * Get the stylesheet owner of the list.
   *
   * @return
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
   *
   * @return
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
  private Hashtable m_namedTemplates = new Hashtable(89);

  /**
   * This table is keyed on the target elements
   * of patterns, and contains linked lists of
   * the actual patterns that match the target element
   * to some degree of specifity.
   * @serial
   */
  private Hashtable m_patternTable = new Hashtable(89);

  /** NEEDSDOC Field m_wildCardPatterns          */
  private TemplateSubPatternAssociation m_wildCardPatterns = null;

  /** NEEDSDOC Field m_textPatterns          */
  private TemplateSubPatternAssociation m_textPatterns = null;

  /** NEEDSDOC Field m_docPatterns          */
  private TemplateSubPatternAssociation m_docPatterns = null;

  /** NEEDSDOC Field m_commentPatterns          */
  private TemplateSubPatternAssociation m_commentPatterns = null;

  /**
   * Get table of named Templates.
   * These are keyed on string macro names, and holding values
   * that are template elements in the XSL DOM tree.
   *
   * @return
   */
  private Hashtable getNamedTemplates()
  {
    return m_namedTemplates;
  }

  /**
   * Set table of named Templates.
   * These are keyed on string macro names, and holding values
   * that are template elements in the XSL DOM tree.
   *
   * @param v
   */
  private void setNamedTemplates(Hashtable v)
  {
    m_namedTemplates = v;
  }

  /**
   * Get the head of the assocation list that is keyed by target.
   *
   * @param key
   *
   * @return
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
}
