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

//import org.w3c.dom.*;
//import org.w3c.dom.traversal.NodeIterator;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMManager;

import org.apache.xml.utils.XMLString;

// Experemental
import org.apache.xml.dtm.ref.ExpandedNameTable;

import org.xml.sax.*;

import org.apache.xpath.*;
import org.apache.xpath.Expression;
import org.apache.xpath.axes.ContextNodeList;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XSequence;
import org.apache.xpath.objects.XSequenceImpl;
import org.apache.xpath.parser.regexp.*;

import java.util.Vector;

import org.apache.xml.utils.QName;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.ResultTreeHandler;
import org.apache.xalan.transformer.StackGuard;
import org.apache.xalan.transformer.ClonerToResultTree;
import org.apache.xalan.res.XSLMessages;

import org.apache.xpath.objects.XSequenceImpl;
import org.apache.xpath.objects.XNodeSequenceSingleton;
import org.apache.xpath.objects.XInteger;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import org.apache.xpath.ExpressionOwner;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:matching-substring.
 * <pre>
 * <!ELEMENT xsl:matching-substring
 *  (#PCDATA)
 * >
 *
 * 
 * </pre>
 * @see <a href="http://www.w3.org/TR/xslt#matching-substring">matching-substring in XSLT Specification</a>
 */
public class ElemMatchingSubstring extends ElemTemplateElement
{
  /** Set true to request some basic status reports */
  static final boolean DEBUG = false;
  
    
  /**
   * Construct a element representing xsl:analyze-string.
   */
  public ElemMatchingSubstring()
  {
  	super();
  }
  protected XSequence m_substringSequence = null;
  
  
  
  
  /**
   * Set the substring sequence.
   *
   * @param substringSeq The sequence of substrings to set.
   */
  public void setRegexGroup(XSequence substringSeq)
  {
    m_substringSequence = substringSeq;
  }

  /**
   * Get the substring sequence.
   *
   * @return The sequence of substrings.
   */
  public XSequence getRegexGroup()
  {
    return m_substringSequence;
  }

  
  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for this element
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_MATCHINGSUBSTRING;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_MATCHINGSUBSTRING_STRING;
  }

  /**
   * Execute the xsl:matching-substring transformation
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {

    transformer.pushCurrentTemplateRuleIsNull(true);    
    if (TransformerImpl.S_DEBUG)
      transformer.getTraceManager().fireTraceEvent(this);
      
   final XPathContext xctxt = transformer.getXPathContext();

    try
    {
    	
    final int sourceNode = xctxt.getCurrentNode();
    DTMManager dtmManager = xctxt.getDTMManager();
    
    final ResultTreeHandler rth = transformer.getResultTreeHandler();
      ContentHandler chandler = rth.getContentHandler();

      xctxt.pushCurrentItem(XSequence.EMPTY);

      int currentNodePos = xctxt.getCurrentNodeFirstFree() - 1;

      xctxt.pushCurrentExpressionNode(DTM.NULL);

      int[] currentExpressionNodes = xctxt.getCurrentExpressionNodeStack();
      int currentExpressionNodePos =
                                    xctxt.getCurrentExpressionNodesFirstFree() - 1;

      xctxt.pushSAXLocatorNull();
      //xctxt.pushContextNodeList(population);
      xctxt.pushContextSequence(m_substringSequence);
      transformer.pushElemTemplateElement(null);

      XObject item;
      
       //for each item in the population, evaluate the expression
       // while (null != (item = m_substringSequence.next()))
         if (null != (item = m_substringSequence.next()))
          {
          	xctxt.setCurrentItem(item);
          	// And execute the child templates.
            // Loop through the children of the template, calling execute on 
            // each of them.
            for (ElemTemplateElement t = this.m_firstChild; t != null;
                 t = t.m_nextSibling)
            {
              xctxt.setSAXLocator(t);
              transformer.setCurrentElement(t);
              t.execute(transformer);
            }
          }
          
    }
    finally
    {
      if (TransformerImpl.S_DEBUG)
	    transformer.getTraceManager().fireTraceEndEvent(this); 
      transformer.popCurrentTemplateRuleIsNull();
      
      xctxt.popSAXLocator();
      xctxt.popContextSequence();
      transformer.popElemTemplateElement();
      xctxt.popCurrentExpressionNode();
      xctxt.popCurrentItem();
      m_substringSequence.detach();
    }
  }

  /**
   * Get template element associated with this
   *
   *
   * @return template element associated with this (itself)
   */
  protected ElemTemplateElement getTemplateMatch()
  {
    return this;
  }

  

  /**
   * <meta name="usage" content="advanced"/>
   * Perform a query if needed, and call transformNode for each child.
   *
   * @param transformer non-null reference to the the current transform-time state.
   * @param template The owning template context.
   *
   * @throws TransformerException Thrown in a variety of circumstances.
   *
  public void transformSelectedNodes(TransformerImpl transformer)
    throws TransformerException
  {

    final XPathContext xctxt = transformer.getXPathContext();
    final int sourceNode = xctxt.getCurrentNode();
    DTMManager dtmManager = xctxt.getDTMManager();
    //DTMIterator savedCurrentGroup = xctxt.getCurrentGroup();
    //DTMIterator population = m_selectExpression.asIterator(xctxt, sourceNode);
    XSequence savedCurrentGroup = xctxt.getCurrentGroup();
    XObject selectResult = m_selectExpression.execute(xctxt);
    XSequence xseq = selectResult.xseq();                                                           
    
    try
    {

      final Vector keys = (m_sortElems == null)
                          ? null
                            : transformer.processSortKeys(this, sourceNode);

      // Sort if we need to.
       //if (null != keys)
      //   population = sortNodes(xctxt, keys, population);

      if (TransformerImpl.S_DEBUG)
      {
        transformer.getTraceManager().fireSelectedEvent(sourceNode, this,
                                                        "select", new XPath(m_selectExpression),
                                                        (org.apache.xpath.objects.XNodeSet)xseq);
                                                        //new org.apache.xpath.objects.XNodeSet(population));
      }

      final ResultTreeHandler rth = transformer.getResultTreeHandler();
      ContentHandler chandler = rth.getContentHandler();

      xctxt.pushCurrentItem(XSequence.EMPTY);

      int currentNodePos = xctxt.getCurrentNodeFirstFree() - 1;

      xctxt.pushCurrentExpressionNode(DTM.NULL);

      int[] currentExpressionNodes = xctxt.getCurrentExpressionNodeStack();
      int currentExpressionNodePos =
                                    xctxt.getCurrentExpressionNodesFirstFree() - 1;

      xctxt.pushSAXLocatorNull();
      //xctxt.pushContextNodeList(population);
      xctxt.pushContextSequence(xseq);
      transformer.pushElemTemplateElement(null);

      // pushParams(transformer, xctxt);
      // Should be able to get this from the iterator but there must be a bug.
      DTM dtm = xctxt.getDTM(sourceNode);
      
      int docID = sourceNode & DTMManager.IDENT_DTM_DEFAULT;
      XObject item;
      int child;
      Vector groups = new Vector();
      XSequence groupedNodes; 
      
      // First, evaluate the group expression      
      switch(m_groupType)
      {
      case TYPE_GROUP_BY:      	       
        {
          //for each item in the population, evaluate the expression
          while (null != (item = xseq.next()))
          {
          	xctxt.setCurrentItem(item);
           if(item instanceof XNodeSequenceSingleton)
            {
              XNodeSequenceSingleton xnss = (XNodeSequenceSingleton)item;
              child = xnss.getNodeHandle();
              currentExpressionNodes[currentExpressionNodePos] = child;
  
             if ((child & DTMManager.IDENT_DTM_DEFAULT) != docID)
              {
                dtm = xnss.getDTM();
                docID = sourceNode & DTMManager.IDENT_DTM_DEFAULT;
              }        
         // }

            try
            {
              //xctxt.setCurrentNode(child);
             // xctxt.pushCurrentNodeAndExpression(child, child);

              XMLString groupKey = m_groupByExpression.xstr(xctxt);        
              if (groups.size()>0)
              {
                boolean foundGroup = false;
                for (int i = 0; i<groups.size() ; i++)
                {
                  Group group = (Group)groups.elementAt(i);
                  if (group.getKey().equals(groupKey))
                  {
                    group.add(child, dtm);
                    foundGroup = true;
                    break;
                  }            		
                }
                if (!foundGroup)
                {
                  Group newGroup = new Group(groupKey, child, dtm);
                  groups.addElement(newGroup);
                }   
              }
              else
             {            
                Group newGroup = new Group(groupKey, child, dtm);
                groups.addElement(newGroup);
              }
            } 
            
            finally
            {
              //xctxt.popCurrentNodeAndExpression();
            }
          }
          }
          break;
        }
      case TYPE_GROUP_ADJACENT:          
        {
          //for each item in the population, evaluate the expression
          while (null != (item = xseq.next()))
          {
          	xctxt.setCurrentItem(item);
           if(item instanceof XNodeSequenceSingleton)
            {
              XNodeSequenceSingleton xnss = (XNodeSequenceSingleton)item;
              child = xnss.getNodeHandle();
              currentExpressionNodes[currentExpressionNodePos] = child;
  
             if ((child & DTMManager.IDENT_DTM_DEFAULT) != docID)
              {
                dtm = xnss.getDTM();
                docID = sourceNode & DTMManager.IDENT_DTM_DEFAULT;
              }  
            try
            {
              //xctxt.pushCurrentNodeAndExpression(child, child);

              XMLString groupKey = m_groupAdjExpression.xstr(xctxt);        
              if (groups.size()>0)
              {
                Group group = (Group)groups.lastElement();
                if (group.getKey().equals(groupKey))
                {
                  group.add(child, dtm);
                }            		
                else
                {
                  Group newGroup = new Group(groupKey, child, dtm);
                  groups.addElement(newGroup);
                }   
              }
              else
              {            
                Group newGroup = new Group(groupKey, child, dtm);
                groups.addElement(newGroup);
              }
            } 
            finally
           {
            //  xctxt.popCurrentNodeAndExpression();
            } 
           }           
          }
          break;
        }
      case TYPE_GROUP_STARTING_WITH:          
        {
        	DTMIterator matches = m_groupStartWithExpression.asIterator(xctxt, sourceNode);
        	int match = matches.nextNode();
        	
          while (null != (item = xseq.next()))
           {
          	xctxt.setCurrentItem(item);
           if(item instanceof XNodeSequenceSingleton)
            {
              XNodeSequenceSingleton xnss = (XNodeSequenceSingleton)item;
              child = xnss.getNodeHandle();
              currentExpressionNodes[currentExpressionNodePos] = child;
  
             if ((child & DTMManager.IDENT_DTM_DEFAULT) != docID)
              {
                dtm = xnss.getDTM();
                docID = sourceNode & DTMManager.IDENT_DTM_DEFAULT;
              } 
            if (groups.size()>0)
            {
              if (match == child)
              {
                Group newGroup = new Group(child, dtm);
                groups.addElement(newGroup);
                match = matches.nextNode(); 
              }
              else
              {
                Group group = (Group)groups.lastElement();
                group.add(child, dtm);
              }
            }
            else
            { 
             if (match == child)
              {                                                
                match = matches.nextNode();
              }
              Group newGroup = new Group(child, dtm);
              groups.addElement(newGroup);
            }
          }
          }
          break;
        }
        
      case TYPE_GROUP_ENDING_WITH:
        {
          DTMIterator matches = this.m_groupEndWithExpression.asIterator(xctxt, sourceNode);
         int match = matches.nextNode();
          
         while (null != (item = xseq.next()))
          {
          	xctxt.setCurrentItem(item);
           if(item instanceof XNodeSequenceSingleton)
            {
              XNodeSequenceSingleton xnss = (XNodeSequenceSingleton)item;
              child = xnss.getNodeHandle();
              currentExpressionNodes[currentExpressionNodePos] = child;
  
            if ((child & DTMManager.IDENT_DTM_DEFAULT) != docID)
              {
                dtm = xnss.getDTM();
                docID = sourceNode & DTMManager.IDENT_DTM_DEFAULT;
              } 
            if (groups.size()>0)
             {
              Group group = (Group)groups.lastElement();
              if (group.lastElementAdded() == match)
              {
                Group newGroup = new Group(child, dtm);
                groups.addElement(newGroup);
                match = matches.nextNode();
              }
              else
              {                
                group.add(child, dtm);
              }
            }
            else
            {
              Group newGroup = new Group(child, dtm);
              groups.addElement(newGroup);
            }
          }
          }
          break;
        }        
      }
      
      GroupingIterator groupIterator = new GroupingIterator(groups);
      
      //GroupingSequence groupSeq = new GroupingSequence(groups);
      // first sort if need to 
     // if (null != keys)
     // {
    //    xseq = sortSequence(xctxt, keys, xseq);
    //  } 
      if (null != keys)
        groupIterator = sortNodes(xctxt, keys, groupIterator);
      
      // Need to set XContext...
      groupIterator.setRoot(DTM.NULL, xctxt);
      
      // Now process each group 
      while (DTM.NULL != (child = groupIterator.nextNode()))
      {
        try{
          groupedNodes = groupIterator.getSequence(child);
          try{ 
          	XSequence clone =  (XSequence)groupedNodes.clone();
          	//clone.reset();           
          xctxt.setCurrentGroup(clone);
          }
          catch (CloneNotSupportedException e)
          {}
          // first sort if need to                     
          //if (null != keys)
          //  sortedIterator = sortNodes(xctxt, keys, groupedNodes);
          //groupIterator.setRoot(child, xctxt);
          xctxt.pushSAXLocatorNull();
          xctxt.pushContextSequence(groupIterator);

          //while (DTM.NULL != (child = groupedNodes.nextNode()))
          {
            // currentNodes[currentNodePos] = child;
            xctxt.setCurrentNode(child);
            currentExpressionNodes[currentExpressionNodePos] = child;

            if ((child & DTMManager.IDENT_DTM_DEFAULT) != docID)
            {
              dtm = xctxt.getDTM(child);
              docID = sourceNode & DTMManager.IDENT_DTM_DEFAULT;
            }

            //final int exNodeType = dtm.getExpandedTypeID(child);
            final int nodeType = dtm.getNodeType(child); 

            // Fire a trace event for the template.
            if (TransformerImpl.S_DEBUG)
            {
              transformer.getTraceManager().fireTraceEvent(this);
            }

            // And execute the child templates.
            // Loop through the children of the template, calling execute on 
            // each of them.
            for (ElemTemplateElement t = this.m_firstChild; t != null;
                 t = t.m_nextSibling)
            {
              xctxt.setSAXLocator(t);
              transformer.setCurrentElement(t);
              t.execute(transformer);
            }
            
            if (TransformerImpl.S_DEBUG)
            {
              // We need to make sure an old current element is not 
              // on the stack.  See TransformerImpl#getElementCallstack.
              transformer.setCurrentElement(null);
              transformer.getTraceManager().fireTraceEndEvent(this);
            }


            // KLUGE: Implement <?xalan:doc_cache_off?> 
            // ASSUMPTION: This will be set only when the XPath was indeed
            // a call to the Document() function. Calling it in other
            // situations is likely to fry Xalan.
            //
            // %REVIEW% We need a MUCH cleaner solution -- one that will
            // handle cleaning up after document() and getDTM() in other
            // contexts. The whole SourceTreeManager mechanism should probably
            // be moved into DTMManager rather than being explicitly invoked in
            // FuncDocument and here.
            
            //******NOTE: (FMM) Do we need to do this of each node in the 
            // group?? 
            /*
            if(m_doc_cache_off)
            {
            if(DEBUG)
            System.out.println("JJK***** CACHE RELEASE *****\n"+
            "\tdtm="+dtm.getDocumentBaseURI());
            // NOTE: This will work because this is _NOT_ a shared DTM, and thus has
            // only a single Document node. If it could ever be an RTF or other
            // shared DTM, this would require substantial rework.
            xctxt.getSourceTreeManager().removeDocumentFromCache(dtm.getDocument());
            xctxt.release(dtm,false);
            }
            *
          }
        }
        finally
        {
          xctxt.popSAXLocator();
          xctxt.popContextNodeList();
        }
      }
    }
    finally
    {
      if (TransformerImpl.S_DEBUG)
        transformer.getTraceManager().fireSelectedEndEvent(sourceNode, this,
                                                           "select", new XPath(m_selectExpression),
                                                           (org.apache.xpath.objects.XNodeSet)xseq);
                                                           //new org.apache.xpath.objects.XNodeSet(population));

      xctxt.popSAXLocator();
      xctxt.popContextSequence();
      transformer.popElemTemplateElement();
      xctxt.popCurrentExpressionNode();
      xctxt.popCurrentItem();
      xseq.detach();
      xctxt.setCurrentGroup(savedCurrentGroup);
    }
  }*/  

}
