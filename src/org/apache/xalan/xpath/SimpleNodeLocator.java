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
package org.apache.xalan.xpath;

import java.util.*;
import java.io.*;
import org.w3c.dom.*;
import org.w3c.dom.traversal.NodeIterator;
import org.apache.xalan.xpath.res.XPATHErrorResources;

/**
 * <meta name="usage" content="advanced"/>
 * SimpleNodeLocator implements a search of one or more DOM trees.
 * By using the connect function as an extension, the user may 
 * specify a directory and a filter specification for XML files 
 * that will be searched.
 * This is a singleton class.
 */
public class SimpleNodeLocator implements XLocator, Serializable
{  
  /**
   * Create a SimpleNodeLocator object.
   */
  public SimpleNodeLocator()
  {
  }
  
  /**
   * The singleton instance of this class.
   */
  private static SimpleNodeLocator m_locater = null;

  /**
   * The the default locator.
   */
  public static XLocator getDefaultLocator()
  {
    m_locater = (null == m_locater) ? new SimpleNodeLocator() : m_locater;
    return m_locater;
  }

  /**
   * Execute the proprietary connect() function, which returns an 
   * instance of XLocator.  When the XPath object sees a return type 
   * of XLocator, it will call the locationPath function that passes 
   * in the connectArgs.  The opPos and args params are not used 
   * by this function.  This really is just a factory function 
   * for the XLocator instance, but this fact is hidden from the 
   * user.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param args The function args.
   * @returns A node set of Document nodes.
   */
  public static XLocator query(String path, String fileSpec) 
  {    
    m_locater = (null == m_locater) ? new SimpleNodeLocator() : m_locater;
    return m_locater;
  }
  
  /**
   * (Same as query for the moment).
   * @param opPos The current position in the xpath.m_opMap array.
   * @param args The function args.
   * @returns A node set of Document nodes.
   */
  public static XLocator connect(String path, String fileSpec) 
  {    
    m_locater = (null == m_locater) ? new SimpleNodeLocator() : m_locater;
    return m_locater;
  }
  
  /**
   * Execute a connection (if it was not executed by the static 
   * connect method) and process the following LocationPath, 
   * if it is present.  Normally, the connection functionality 
   * should be executed by this function and not the static connect 
   * function, which is really a factory method for the XLocator 
   * instance.  The arguments to the static connect function
   * are re-passed to this function.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param connectArgs The same arguments that were passed to the 
   * static connect function.
   * @returns the result of the query in an XNodeSet object.
   */
  public XNodeSet connectToNodes(XPath xpath, XPathContext xctxt, Node context, 
                                 int opPos, Vector connectArgs) 
    throws org.xml.sax.SAXException
  {    
    String fileSpec = ((XObject)connectArgs.elementAt(0)).str();
    FileFilter filter = null;
    String filterSpec = null;
    if(connectArgs.size() > 1)
    {
      filterSpec = ((XObject)connectArgs.elementAt(1)).str();
      filter = new FileFilter(filterSpec);
    }
    
    File dir = new File(fileSpec);
    
    XNodeSet results = new XNodeSet();
    NodeSet mnl = results.mutableNodeset();
    
    if(null != dir)
    {
      String filenames[] = (filter != null) ? dir.list(filter) : dir.list();
      if(null != filenames)
      {
        int nFiles = filenames.length;
        for(int i = 0; i < nFiles; i++)
        {
          try
          {
            String urlString = "file:"+dir.getAbsolutePath()+File.separatorChar+filenames[i];
            // java.net.URL url = xctxt.getAbsoluteURI(filenames[i], null);
            // java.net.URL url = new java.net.URL(urlString);
            // Document doc = xctxt.parseXML(url, null, null);
            Document doc = null; // TBD:
            if(null != doc)
            {
              int op = xpath.m_opMap[opPos];
              if(OpCodes.OP_LOCATIONPATH == (op & OpCodes.LOCATIONPATHEX_MASK))
              {
                XNodeSet xnl = xpath.locationPath(xctxt, doc, opPos);
                if(null != xnl)
                {
                  mnl.addNodes(xnl.nodeset());
                  xctxt.getSourceTreeManager().associateXLocatorToNode(doc, urlString, this);
                }
              }
              else
              {
                mnl.addNode(doc);
                xctxt.getSourceTreeManager().associateXLocatorToNode(doc, urlString, this);
              }
            }
          }
          catch(Exception e)
          {
            System.out.println("Couldn't parse file: "+e.getMessage());
          }
        }
      }
      else
      {
        System.out.println("Couldn't get a file list from filespec");
      }
    }
    else
    {
      System.out.println("Filespec was bad in connect");
    }
    
    return results;
  }
  
  /**
   * Computes the union of its operands which must be node-sets.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns the union of node-set operands, or an empty set if 
   * callback methods are used.
   */
  public XNodeSet union(XPath xpath, XPathContext xctxt, 
                        Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    
    return new XNodeSet(new UnionPathIterator(xpath, xctxt, context, opPos, this));
  }

  /**
   * Execute a location path.  Normally, this method simply 
   * moves past the OP_LOCATIONPATH and it's length member, 
   * and calls the Step function, which will recursivly process 
   * the rest of the LocationPath, and then wraps the NodeList result
   * in an XNodeSet object.
   * @param xpath The xpath that is executing.
   * @param xctxt The execution context.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param callback Interface that implements the processLocatedNode method.
   * @param callbackInfo Object that will be passed to the processLocatedNode method.
   * @returns the result of the query in an XNodeSet object.
   */
  public XNodeSet locationPath(XPath xpath, XPathContext xctxt, 
                               Node context, int opPos) 
    throws org.xml.sax.SAXException
  {    
    return new XNodeSet(new LocPathIterator(xpath, xctxt, context, opPos, this));
  }
    
  /**
   * Execute a single predicate for a single node.
   * @returns True if the node should not be filtered.
   */
  protected boolean predicate(XPath xpath, XPathContext xctxt, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    boolean shouldNotFilter = true;

    int nextStepType = xpath.m_opMap[opPos];
    if(OpCodes.OP_PREDICATE == nextStepType)
    {
      XObject pred = xpath.predicate(xctxt, context, opPos);
      if(XObject.CLASS_NUMBER == pred.getType())
      {
        throw new FoundIndex(); // Ugly, but... see comment in the Step function.
      }
      else if(!pred.bool())
      {
        shouldNotFilter = false;
      }
    }
    return shouldNotFilter;
  }
  
  /**
   * Execute a a location path pattern.  This will return a score
   * of MATCH_SCORE_NONE, MATCH_SCORE_NODETEST, 
   * MATCH_SCORE_OTHER, MATCH_SCORE_QNAME.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @returns score, one of MATCH_SCORE_NODETEST, 
   * MATCH_SCORE_NONE, MATCH_SCORE_OTHER, MATCH_SCORE_QNAME.
   */
  public double locationPathPattern(XPath xpath, XPathContext xctxt, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {    
    opPos = xpath.getFirstChildPos(opPos);
    double[] scoreHolder = 
    {
      XPath.MATCH_SCORE_NONE};
    stepPattern(xpath, xctxt, context, opPos, scoreHolder, 0);
    return scoreHolder[0];
  }

  /**
   * Execute a step in a location path.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @returns the last matched context node.
   */
  protected Node stepPattern(XPath xpath, XPathContext xctxt, 
                             Node context, int opPos, double scoreHolder[],
                             int stepCount) 
    throws org.xml.sax.SAXException
  {    
    int startOpPos = opPos;
    int stepType = xpath.m_opMap[opPos];
    
    int endStep = xpath.getNextOpPos(opPos);
    int nextStepType = xpath.m_opMap[endStep];
    double score;
    
    if(OpCodes.ENDOP != nextStepType)
    {
      // Continue step via recursion...
      context = stepPattern(xpath, xctxt, context, endStep, scoreHolder, stepCount+1);
      if(null == context)
        scoreHolder[0] = XPath.MATCH_SCORE_NONE;
      if(scoreHolder[0] == XPath.MATCH_SCORE_NONE)
        return null;
      
      scoreHolder[0] = XPath.MATCH_SCORE_OTHER;
      context = xctxt.getDOMHelper().getParentOfNode(context);
      if(null == context)
        return null;
    }
    // System.out.println("stepCount: "+stepCount);
    boolean isSimple = ((OpCodes.ENDOP == nextStepType) && (stepCount == 0));
    
    int argLen;

    switch(stepType)
    {
    case OpCodes.OP_FUNCTION:
      {
        argLen = xpath.m_opMap[opPos+XPath.MAPINDEX_LENGTH];
        XObject obj = xpath.execute(xctxt, context, opPos);
        NodeIterator nl = obj.nodeset();
        score = XPath.MATCH_SCORE_NONE;
        Node n;
        while(null != (n = nl.nextNode()))
        {
          score = (n.equals(context)) ? XPath.MATCH_SCORE_OTHER : XPath.MATCH_SCORE_NONE;
          if(score == XPath.MATCH_SCORE_OTHER)
          {
            context = n;
            break;
          }
        }
      }
      break;
    case OpCodes.FROM_ROOT:
      {
        argLen = xpath.getArgLengthOfStep(opPos);
        opPos = xpath.getFirstChildPosOfStep(opPos);
        Document docContext = (Node.DOCUMENT_NODE == context.getNodeType()) 
                              ? (Document)context : context.getOwnerDocument();
        score = (docContext.equals( context )) ? XPath.MATCH_SCORE_OTHER : XPath.MATCH_SCORE_NONE;
        if(score == XPath.MATCH_SCORE_OTHER)
        {
          context = docContext;
        }
      }
      break;
    case OpCodes.MATCH_ATTRIBUTE:
      {
        argLen = xpath.getArgLengthOfStep(opPos);
        opPos = xpath.getFirstChildPosOfStep(opPos);
        score = nodeTest(xpath, xctxt, context, opPos, argLen, OpCodes.FROM_ATTRIBUTES);
        break;
      }
    case OpCodes.MATCH_ANY_ANCESTOR:
      argLen = xpath.getArgLengthOfStep(opPos);
      if(context.getNodeType() != Node.ATTRIBUTE_NODE)
      {
        opPos = xpath.getFirstChildPosOfStep(opPos);
        score = XPath.MATCH_SCORE_NONE;
        while(null != context)
        {
          score = nodeTest(xpath, xctxt, context, opPos, argLen, stepType);
          if(XPath.MATCH_SCORE_NONE != score)
            break;
          // context = xctxt.getParentOfNode(context);
          context = context.getParentNode();
        }
      }
      else
      {
        score = XPath.MATCH_SCORE_NONE;
      }
      break;
    case OpCodes.MATCH_IMMEDIATE_ANCESTOR:
      argLen = xpath.getArgLengthOfStep(opPos);
      if(context.getNodeType() != Node.ATTRIBUTE_NODE)
      {
        opPos = xpath.getFirstChildPosOfStep(opPos);
        score = nodeTest(xpath, xctxt, context, opPos, argLen, stepType);
      }
      else
      {
        score = XPath.MATCH_SCORE_NONE;
      }
      break;
    default:
      argLen = xpath.getArgLengthOfStep(opPos);
      opPos = xpath.getFirstChildPosOfStep(opPos);
      score = XPath.MATCH_SCORE_NONE;
      xpath.error(xctxt, context, XPATHErrorResources.ER_UNKNOWN_MATCH_OPERATION, null); //"unknown match operation!");
      break;
    }
    opPos += argLen;
    nextStepType = xpath.m_opMap[opPos];
    
    if(((score != XPath.MATCH_SCORE_NONE)) && (OpCodes.OP_PREDICATE == nextStepType))
    {
      score = XPath.MATCH_SCORE_OTHER;
      // Execute the xpath.predicates, but if we have an index, then we have 
      // to start over and do a search from the parent.  It would be nice 
      // if I could sense this condition earlier...
      try
      {
        // BUG: m_throwFoundIndex is not threadsafe
        
        int startPredicates = opPos;
        opPos = startPredicates;
        nextStepType = xpath.m_opMap[opPos];
        xctxt.setThrowFoundIndex(true);
        for(int i = 0; OpCodes.OP_PREDICATE == nextStepType; i++)
        {
          XObject pred = xpath.predicate(xctxt, context, opPos);
          if(XObject.CLASS_NUMBER == pred.getType())
          {
            if((i == 0) && isSimple)
            {
              NodeSet cnl = xctxt.getContextNodeList();
              // System.out.println("cnl.getCurrentPos(): "+cnl.getCurrentPos());
              if(cnl.getCurrentPos() != pred.num())
              {
                score = XPath.MATCH_SCORE_NONE;
                break; // from while(OpCodes.OP_PREDICATE == nextStepType)
              }
            }
            else
            {
              throw new FoundIndex();
            }
          }
          else if(!pred.bool())
          {
            score = XPath.MATCH_SCORE_NONE;
            break; // from while(OpCodes.OP_PREDICATE == nextStepType)
          }
          opPos = xpath.getNextOpPos(opPos);
          nextStepType = xpath.m_opMap[opPos];
        }
        xctxt.setThrowFoundIndex(false);
      }
      catch(FoundIndex fi)
      {
        // We have an index somewhere in our pattern.  So, we have 
        // to do a full search for our step, using the parent as 
        // context, then see if the current context is found in the 
        // node set.  Seems crazy, but, so far, it seems like the 
        // easiest way.
        xctxt.setThrowFoundIndex(false);
        Node parentContext = xctxt.getDOMHelper().getParentOfNode(context);
        LocPathIterator lpi 
          = new LocPathIterator(xpath, xctxt, parentContext, startOpPos, this, true);
        
        score = XPath.MATCH_SCORE_NONE;
        Node child;
        while(null != (child=lpi.nextNode()))
        {
          if(child.equals( context ))
          {
            score = XPath.MATCH_SCORE_OTHER;
            break;
          }
        }
      }
    }
    // If we haven't found a score yet, or the test was 
    // negative, assign the score.
    if((scoreHolder[0] == XPath.MATCH_SCORE_NONE) || 
       (score == XPath.MATCH_SCORE_NONE))
      scoreHolder[0] = score;
    
    return (score == XPath.MATCH_SCORE_NONE) ? null : context;
  }

  
  /**
   * Test a node to see if it matches the given node test.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param len The length of the argument.
   * @param len The type of the step.
   * @returns score in an XNumber, one of MATCH_SCORE_NODETEST, 
   * MATCH_SCORE_NONE, MATCH_SCORE_OTHER, MATCH_SCORE_QNAME.
   */
  public double nodeTest(XPath xpath, XPathContext xctxt, Node context, 
                         int opPos, int argLen, int stepType)
    throws org.xml.sax.SAXException
  {
    double score;
    int testType = xpath.m_opMap[opPos];
    int nodeType = context.getNodeType();
    opPos++;
    switch(testType)
    {
    case OpCodes.NODETYPE_COMMENT:
      score = (Node.COMMENT_NODE == nodeType)
              ? XPath.MATCH_SCORE_NODETEST : XPath.MATCH_SCORE_NONE;
      break;
    case OpCodes.NODETYPE_TEXT:
      score = (((Node.CDATA_SECTION_NODE == nodeType) 
                || (Node.TEXT_NODE == nodeType)) &&
               (!xctxt.getDOMHelper().shouldStripSourceNode(context)))
              ? XPath.MATCH_SCORE_NODETEST : XPath.MATCH_SCORE_NONE;
      break;
    case OpCodes.NODETYPE_PI:
      if( (Node.PROCESSING_INSTRUCTION_NODE == nodeType) )
      {
        if(argLen == 2)
        {
          XString name = (XString)xpath.m_tokenQueue[xpath.m_opMap[opPos]];
          score = ((ProcessingInstruction)context).getNodeName().equals(name.str())
                  ? XPath.MATCH_SCORE_QNAME : XPath.MATCH_SCORE_NONE;
        }
        else if(argLen == 1)
        {
          score = XPath.MATCH_SCORE_NODETEST;
        }
        else
        {
          score = XPath.MATCH_SCORE_NONE;
          xpath.error(xctxt, context, XPATHErrorResources.ER_INCORRECT_ARG_LENGTH, null); //"Arg length of processing-instruction() node test is incorrect!");
        }
      }
      else
      {
        score = XPath.MATCH_SCORE_NONE;
      }
      break;
    case OpCodes.NODETYPE_NODE:
      if((Node.CDATA_SECTION_NODE == nodeType) 
         || (Node.TEXT_NODE == nodeType))
      {
        score = (!xctxt.getDOMHelper().shouldStripSourceNode(context))
                ? XPath.MATCH_SCORE_NODETEST : XPath.MATCH_SCORE_NONE;
      }
      else
      {
        score = XPath.MATCH_SCORE_NODETEST;
      }
      break;
    case OpCodes.NODETYPE_ROOT:
      score = ( (Node.DOCUMENT_FRAGMENT_NODE == nodeType) 
                || (Node.DOCUMENT_NODE == nodeType))
              ? XPath.MATCH_SCORE_OTHER : XPath.MATCH_SCORE_NONE;
      break;
      
    case OpCodes.NODENAME:
      {
        if(!((Node.ATTRIBUTE_NODE == nodeType) || (Node.ELEMENT_NODE == nodeType)))
          return XPath.MATCH_SCORE_NONE;
                                        
        boolean test;
        int queueIndex = xpath.m_opMap[opPos];
        String targetNS = (queueIndex >= 0) ? (String)xpath.m_tokenQueue[xpath.m_opMap[opPos]]
                                              : null;
        opPos++;
        
        // From the draft: "Two expanded names are equal if they 
        // have the same local part, and either both have no URI or 
        // both have the same URI."
        // "A node test * is true for any node of the principal node type. 
        // For example, child::* will select all element children of the 
        // context node, and attribute::* will select all attributes of 
        // the context node."
        // "A node test can have the form NCName:*. In this case, the prefix 
        // is expanded in the same way as with a QName using the context 
        // namespace declarations. The node test will be true for any node 
        // of the principal type whose expanded name has the URI to which 
        // the prefix expands, regardless of the local part of the name."
        boolean isTotallyWild = (null == targetNS) 
                                && (xpath.m_opMap[opPos] == OpCodes.ELEMWILDCARD);
        boolean didMatchNS = false;
        if(!isTotallyWild)
        {
          String contextNS = xctxt.getDOMHelper().getNamespaceOfNode(context);
          if((null != targetNS) && (null != contextNS))
          {
            test = contextNS.equals(targetNS);
            didMatchNS = true;
          }
          else
          {
            test = (OpCodes.ELEMWILDCARD == queueIndex) || 
                   (((null == contextNS) || (contextNS.length() == 0)) &&
                    ((null == targetNS) || (targetNS.length() == 0)));
          }
        }
        else 
          test = true;
        
        queueIndex = xpath.m_opMap[opPos];
        String targetLocalName = (queueIndex >= 0) ? (String)xpath.m_tokenQueue[xpath.m_opMap[opPos]]
                                                     : null;
        
        if(!test)
        {
          score = XPath.MATCH_SCORE_NONE;
        }
        else
        {
          switch(nodeType)
          {
          case Node.ATTRIBUTE_NODE:
            if((stepType == OpCodes.FROM_ATTRIBUTES) || (stepType == OpCodes.FROM_NAMESPACE))
            {            
              boolean isNamespace = xctxt.getDOMHelper().isNamespaceNode(context);
              if(OpCodes.ELEMWILDCARD == queueIndex)
              {
                if(stepType == OpCodes.FROM_ATTRIBUTES)
                {
                  score = !isNamespace ? XPath.MATCH_SCORE_NODETEST : XPath.MATCH_SCORE_NONE;
                }
                else
                {
                  score = isNamespace ? XPath.MATCH_SCORE_NODETEST : XPath.MATCH_SCORE_NONE;
                }
              }
              else
              {
                if(stepType == OpCodes.FROM_ATTRIBUTES)
                {
                  if(!isNamespace)
                  {
                    String localAttrName 
                      = xctxt.getDOMHelper().getLocalNameOfNode(context);
                    score = localAttrName.equals(targetLocalName)
                            ? XPath.MATCH_SCORE_QNAME : XPath.MATCH_SCORE_NONE;
                  }
                  else
                  {
                    score = XPath.MATCH_SCORE_NONE;
                  }
                }
                else
                {
                  if(isNamespace)
                  {
                    String namespace = ((Attr)context).getValue();
                    
                    score = namespace.equals(targetLocalName)
                            ? XPath.MATCH_SCORE_QNAME : XPath.MATCH_SCORE_NONE;
                  }
                  else
                  {
                    score = XPath.MATCH_SCORE_NONE;
                  }
                }
              }
            }
            else
            {
              score  = XPath.MATCH_SCORE_NONE;
            }
            break;

          case Node.ELEMENT_NODE:
            if(stepType != OpCodes.FROM_ATTRIBUTES)
            {
              if(OpCodes.ELEMWILDCARD == queueIndex)
              {
                score = (didMatchNS ? 
                         XPath.MATCH_SCORE_NSWILD : XPath.MATCH_SCORE_NODETEST);
              }
              else
              {
                
                score = (xctxt.getDOMHelper().getLocalNameOfNode(context).equals(targetLocalName))
                        ? XPath.MATCH_SCORE_QNAME : XPath.MATCH_SCORE_NONE;
              }
            }
            else
            {
              score  = XPath.MATCH_SCORE_NONE;
            }
            break;
            
          default:
            // Trying to match on anything else causes nasty bugs.
            score  = XPath.MATCH_SCORE_NONE;
            break;

          } // end switch(nodeType)
        } // end if(test)
      } // end case OpCodes.NODENAME
      break;
    default:
      score  = XPath.MATCH_SCORE_NONE;
    } // end switch(testType)
    
    return score;    
  }
  
  /**
   * Create an XPathFactory for this XLocator.
   */
  public static XPathFactory factory() 
  {
    return new SimpleNodeLocatorFactory();
  }

  /**
   * Very crude file filter.
   */
  class FileFilter implements FilenameFilter
  {
    private String m_filterSpec;
    
    public FileFilter(String filter)
    {
      m_filterSpec = filter;
    }
    
    /**
     * Tests if a specified file should be included in a file list.
     *
     * @param   dir    the directory in which the file was found.
     * @param   name   the name of the file.
     * @return  <code>true</code> if the name should be included in the file
     *          list; <code>false</code> otherwise.
     * @since   JDK1.0
     */
    public boolean accept(File dir, String name)
    {
      return name.endsWith(m_filterSpec);
    }
  }
  
}

/**
 * Override the createXLocatorHandler method.
 */
class DOMXPath extends XPath
{
  public DOMXPath()
  {
    super();
  }
  
  /**
   * getXLocatorHandler.
   */
  public XLocator createXLocatorHandler(XPath xpath)
  {
    return new SimpleNodeLocator();
  }
}

/**
 * Implement an XPath factory.
 */
class SimpleNodeLocatorFactory implements XPathFactory
{
  public XPath create()
  {
    return new DOMXPath();
  }
}
