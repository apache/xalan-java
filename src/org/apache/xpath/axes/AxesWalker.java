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
package org.apache.xpath.axes;

import java.util.Stack;

// Xalan imports
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.SubContextList;
import org.apache.xpath.compiler.OpCodes;
import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.DOMHelper;
import org.apache.xpath.Expression;
import org.apache.xpath.patterns.NodeTest;

import org.apache.xalan.utils.PrefixResolver;

// DOM2 imports
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;

import org.w3c.dom.traversal.TreeWalker;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.DOMException;

/**
 * Serves as common interface for axes Walkers, and stores common 
 * state variables.
 */
public abstract class AxesWalker extends NodeTest
  implements Cloneable, TreeWalker, NodeFilter, SubContextList
{
  boolean DEBUG = false;
  boolean DEBUG_WAITING = false;
  boolean DEBUG_TRAVERSAL = false;
  boolean DEBUG_LOCATED = false;
  boolean DEBUG_PREDICATECOUNTING = false;

  protected LocPathIterator m_lpi;
  public LocPathIterator getLocPathIterator() { return m_lpi; }
  public void setLocPathIterator(LocPathIterator li) { m_lpi = li; }
  
  /**
   * Construct an AxesWalker using a LocPathIterator.
   */
  public AxesWalker(LocPathIterator locPathIterator)
  {
    m_lpi = locPathIterator;
  }
      
  /**
   * Init an AxesWalker.
   */
  public void init(Compiler compiler, int opPos, int stepType)
    throws org.xml.sax.SAXException
  {
    int nodeTestOpPos = compiler.getFirstChildPosOfStep(opPos);
    m_stepType = stepType;
    m_argLen = compiler.getArgLengthOfStep(opPos);
    initPredicateInfo(compiler, opPos);
    
    // int testType = compiler.getOp(nodeTestOpPos);
  }
  
  /**
   * Get a cloned AxesWalker.
   */
  public Object clone()
    throws CloneNotSupportedException
  {
    AxesWalker clone = (AxesWalker)super.clone();

    if((null != this.m_proximityPositions) && (this.m_proximityPositions == clone.m_proximityPositions))
    {
      clone.m_proximityPositions = new int[this.m_proximityPositions.length];
      System.arraycopy(this.m_proximityPositions, 0, clone.m_proximityPositions, 0, this.m_proximityPositions.length);
    }
    return clone;
  }
  
  /**
   * Create the proper Walker from the axes type.
   */
  public static AxesWalker createDefaultWalker(Compiler compiler,
                                               int opPos, 
                                               LocPathIterator lpi)
  {
    AxesWalker ai;
    int whatToShow = compiler.getWhatToShow(opPos);
    int stepType = compiler.getOp(opPos);
    /*
    System.out.println("0: "+compiler.getOp(opPos));
    System.out.println("1: "+compiler.getOp(opPos+1));
    System.out.println("2: "+compiler.getOp(opPos+2));
    System.out.println("3: "+compiler.getOp(opPos+3));
    System.out.println("4: "+compiler.getOp(opPos+4));
    System.out.println("5: "+compiler.getOp(opPos+5));
    */
    boolean simpleInit = false;
    switch(stepType)
    {
    case OpCodes.OP_VARIABLE:
    case OpCodes.OP_EXTFUNCTION:
    case OpCodes.OP_FUNCTION:
    case OpCodes.OP_GROUP:
      ai = new FilterExprWalker(lpi);
      simpleInit = true;
      break;
    case OpCodes.FROM_ROOT: 
      ai = new RootWalker(lpi); 
      simpleInit = true;
      break;
    case OpCodes.FROM_ANCESTORS: 
      ai = new AncestorWalker(lpi); 
      break;
    case OpCodes.FROM_ANCESTORS_OR_SELF: 
      ai = new AncestorOrSelfWalker(lpi); 
      break;
    case OpCodes.FROM_ATTRIBUTES: 
      ai = new AttributeWalker(lpi); 
      break;
    case OpCodes.FROM_NAMESPACE: 
      ai = new NamespaceWalker(lpi); 
      break;
    case OpCodes.FROM_CHILDREN: 
      ai = new ChildWalker(lpi);  
      {
        if(false && compiler.getPatternString().startsWith("(preceding-sibling::*|following-sibling::*)"))
        {
          ai.DEBUG = true;
          ai.DEBUG_LOCATED = true;
          ai.DEBUG_TRAVERSAL = true;
          ai.DEBUG_WAITING = true;
        }
      }
      break;
    case OpCodes.FROM_DESCENDANTS: 
      ai = new DescendantWalker(lpi); 
      break;
    case OpCodes.FROM_DESCENDANTS_OR_SELF: 
      ai = new DescendantOrSelfWalker(lpi); 
      break;
    case OpCodes.FROM_FOLLOWING: 
      ai = new FollowingWalker(lpi); 
      break;
    case OpCodes.FROM_FOLLOWING_SIBLINGS: 
      ai = new FollowingSiblingWalker(lpi); 
      break;
    case OpCodes.FROM_PRECEDING: 
      ai = new PrecedingWalker(lpi); 
      break;
    case OpCodes.FROM_PRECEDING_SIBLINGS: 
      ai = new PrecedingSiblingWalker(lpi); 
      break;
    case OpCodes.FROM_PARENT: 
      ai = new ParentWalker(lpi); 
      break;
    case OpCodes.FROM_SELF: 
      ai = new SelfWalker(lpi); 
      break;

    case OpCodes.MATCH_ATTRIBUTE: 
      ai = new AttributeWalker(lpi); 
      break;
    case OpCodes.MATCH_ANY_ANCESTOR: 
      ai = new ChildWalker(lpi); 
      break;
    case OpCodes.MATCH_IMMEDIATE_ANCESTOR: 
      ai = new ChildWalker(lpi); 
      break;
    default: 
      throw new RuntimeException("Programmer's assertion: unknown opcode: "+stepType);
    }
    /*
    System.out.print("construct: ");
    NodeTest.debugWhatToShow(whatToShow);
    System.out.println("or stuff: "+(whatToShow & (NodeFilter.SHOW_ATTRIBUTE 
                           | NodeFilter.SHOW_ELEMENT
                           | NodeFilter.SHOW_PROCESSING_INSTRUCTION)));
    */
    if((0 == (whatToShow & (NodeFilter.SHOW_ATTRIBUTE 
                            | NodeFilter.SHOW_ELEMENT
                            | NodeFilter.SHOW_PROCESSING_INSTRUCTION))) ||
       (whatToShow == NodeFilter.SHOW_ALL))
      ai.initNodeTest(whatToShow);
    else
    {
      ai.initNodeTest(whatToShow, 
                      compiler.getStepNS(opPos), 
                      compiler.getStepLocalName(opPos));
    }
    return ai;
  }
  
  /**
   * Reset the Walker.
   */
  public void reset()
  {
    setCurrentNode(m_root);
  }

  /**
   * The step type of the XPath step. Does not change after the constructor.
   */
  private int m_stepType;
  protected int getStepType() { return m_stepType; }
  
  /**
   * The arg length of the XPath step. Does not change after the constructor.
   */
  private int m_argLen;
  protected int getArgLen() { return m_argLen; }
          
  /**
   * An array of counts that correspond to the number 
   * of predicates the step contains.
   */
  protected int[] m_proximityPositions;
  
  /**
   * Get the current sub-context position.
   */
  public int getProximityPosition()
  {
    // System.out.println("getProximityPosition - m_predicateIndex: "+m_predicateIndex);
    return getProximityPosition(m_predicateIndex);
  }
  
  /**
   * Get the current sub-context position.
   */
  public int getProximityPosition(XPathContext xctxt)
  {
    return getProximityPosition();
  }
  
  /**
   * Get the current sub-context position.
   */
  protected int getProximityPosition(int predicateIndex)
  {
    return (predicateIndex >= 0) ?
           m_proximityPositions[predicateIndex] : 0;
  }
  
  /**
   * Reset the proximity positions counts.
   */
  public void resetProximityPositions()
    throws org.xml.sax.SAXException
  {
    if(m_predicateCount > 0)
    {
      if(null == m_proximityPositions)
        m_proximityPositions = new int[m_predicateCount];
      for(int i = 0; i < m_predicateCount; i++)
      {
        initProximityPosition(i);
      }
    }
  }
  
  /**
   * Init the proximity position to zero for a forward axes.
   */
  public void initProximityPosition(int i)
    throws org.xml.sax.SAXException
  {
    m_proximityPositions[i] = 0;
  }

  /**
   * Count forward one proximity position.
   */
  protected void countProximityPosition(int i)
  {
    if(i < m_proximityPositions.length)
      m_proximityPositions[i]++;
  }
  
  /**
   * Tells if this is a reverse axes.
   */
  public boolean isReverseAxes()
  {
    return false;
  }
  
  /**
   * Which predicate we are executing.
   */
  int m_predicateIndex = -1;
  
  /**
   * Get which predicate is executing.  Returns 
   * -1 if no predicate is executing.
   */
  public int getPredicateIndex()
  {
    return m_predicateIndex;
  }
    
  /**
   * Process the predicates.
   */
  boolean executePredicates(Node context, XPathContext xctxt)
    throws org.xml.sax.SAXException
  {    
    m_predicateIndex = 0;
    int nPredicates = m_predicateCount;
    if(nPredicates == 0)
      return true;
    
    PrefixResolver savedResolver = xctxt.getNamespaceContext();
    int savedStackframeIndex = xctxt.getVarStack().getCurrentStackFrameIndex();
    try
    {
      xctxt.pushSubContextList(this);
      xctxt.getVarStack().setCurrentStackFrameIndex(m_lpi.getStackFrameIndex());
      xctxt.setNamespaceContext(m_lpi.getPrefixResolver());
      xctxt.pushCurrentNode(context);
      for(int i = 0; i < nPredicates; i++)
      {
        XObject pred;
        pred = m_predicates[i].execute(xctxt);
        if(XObject.CLASS_NUMBER == pred.getType())
        {
          if(DEBUG_PREDICATECOUNTING)
          {
            System.out.println("=============");
            System.out.println("m_predicateIndex: "+m_predicateIndex);
            System.out.println("getProximityPosition(m_predicateIndex): "+getProximityPosition(m_predicateIndex));
            System.out.println("pred.num(): "+pred.num());
          }
          if(this.getProximityPosition(m_predicateIndex) != (int)pred.num())
          {
            return false;
          }
        }
        else if(!pred.bool())
          return false;
        
        countProximityPosition(++m_predicateIndex);
      }
    }
    finally
    {
      xctxt.popCurrentNode();
      xctxt.setNamespaceContext(savedResolver);
      xctxt.popSubContextList();
      xctxt.getVarStack().setCurrentStackFrameIndex(savedStackframeIndex);
    }

    m_predicateIndex = -1;
    return true;
  }
  
  /**
   * Number of predicates (in effect).
   */
  int m_predicateCount;
    
  /**
   * Get the number of predicates that this walker has.
   */
  public int getPredicateCount()
  {
    return m_predicateCount;
  }
  
  /**
   * Set the number of predicates that this walker has.
   */
  public void setPredicateCount(int count)
  {
    m_predicateCount = count;
  }
    
  /**
   * Init predicate info.
   */
  private void initPredicateInfo(Compiler compiler, int opPos)
    throws org.xml.sax.SAXException
  {
    int pos = compiler.getFirstPredicateOpPos(opPos);
    m_predicates = compiler.getCompiledPredicates(pos);
    m_predicateCount = (null == m_predicates) ? 0 : m_predicates.length;
  }
  
  private Expression[] m_predicates;
  
  Expression getPredicate(int index)
  {
    return m_predicates[index];
  }
      
  /**
   * Tell if the given node is a parent of the 
   * step context, or the step context node itself.
   */
  boolean isAncestorOfRootContext(Node n)
  {
    Node parent = m_root;
    while(null != (parent = parent.getParentNode()))
    {
      if(parent.equals( n ))
        return true;
    }
    return false;
  }
  
  //=============== TreeWalker Implementation ===============
  
  /**
   *  The root node of the TreeWalker, as specified when it was created.
   */
  Node m_root;
    
  /**
   * The root node of the TreeWalker, as specified in setRoot(Node root).
   * Note that this may actually be below the current node.
   */
  public Node getRoot()
  {
    return m_root;
  }
  
  boolean m_isFresh;

  /**
   * Set the root node of the TreeWalker.
   * (Not part of the DOM2 TreeWalker interface).
   */
  public void setRoot(Node root)
  {
    m_isFresh = true;
    m_root = root;
    m_currentNode = root;
    m_prevReturned = null;
    if(null == root)
    {
      throw new RuntimeException("\n !!!! Error! Setting the root of a walker to null!!!");
    }
    try
    {
      resetProximityPositions();
    }
    catch(org.xml.sax.SAXException se)
    {
      // TODO: Fix this...
      throw new RuntimeException(se.getMessage());
    }
  }
  
  /**
   *  The node at which the TreeWalker is currently positioned.
   */
  Node m_currentNode;

  /**
   * The node at which the TreeWalker is currently positioned.
   * <br> The value must not be null. Alterations to the DOM tree may cause 
   * the current node to no longer be accepted by the TreeWalker's 
   * associated filter. currentNode may also be explicitly set to any node, 
   * whether or not it is within the subtree specified by the root node or 
   * would be accepted by the filter and whatToShow flags. Further 
   * traversal occurs relative to currentNode even if it is not part of the 
   * current view by applying the filters in the requested direction (not 
   * changing currentNode where no traversal is possible). 
   * @exception DOMException
   *    NOT_SUPPORTED_ERR: Raised if the specified <code>currentNode</code> 
   *   is<code>null</code> .
   */
  public Node getCurrentNode()
  { 
    return m_currentNode; 
  }
  
  /**
   * Set the current node.
   */
  public void setCurrentNode(Node currentNode)
    throws DOMException
  { 
    m_currentNode = currentNode; 
  }
  
  /**
   * Set the current node if it's not null.
   * @return The node passed in.
   */
  protected Node setCurrentIfNotNull(Node currentNode)
    throws DOMException
  { 
    if(null != currentNode)
      m_currentNode = currentNode; 
    return currentNode;
  }
  
  /**
   *  The filter used to screen nodes.
   */
  public NodeFilter getFilter()
  {
    return this;
  }

  /**
   *  The value of this flag determines whether the children of entity 
   * reference nodes are visible to the TreeWalker. If false, they will be 
   * skipped over.
   * <br> To produce a view of the document that has entity references 
   * expanded and does not expose the entity reference node itself, use the 
   * whatToShow flags to hide the entity reference node and set 
   * expandEntityReferences to true when creating the TreeWalker. To 
   * produce a view of the document that has entity reference nodes but no 
   * entity expansion, use the whatToShow flags to show the entity 
   * reference node and set expandEntityReferences to false.
   */
  public boolean getExpandEntityReferences()
  {
    return true;
  }
      
  /**
   *  Moves to and returns the closest visible ancestor node of the current 
   * node. If the search for parentNode attempts to step upward from the 
   * TreeWalker's root node, or if it fails to find a visible ancestor 
   * node, this method retains the current position and returns null.
   * @return  The new parent node, or null if the current node has no parent 
   *   in the TreeWalker's logical view.
   */
  public Node parentNode()
  {
    return null;
  }

  /**
   *  Moves the <code>TreeWalker</code> to the first visible child of the 
   * current node, and returns the new node. If the current node has no 
   * visible children, returns <code>null</code> , and retains the current 
   * node.
   * @return  The new node, or <code>null</code> if the current node has no 
   *   visible children in the TreeWalker's logical view.
   */
  public Node firstChild()
  {
    return null;
  }
  
  /**
   *  Moves the <code>TreeWalker</code> to the next sibling of the current 
   * node, and returns the new node. If the current node has no visible 
   * next sibling, returns <code>null</code> , and retains the current node.
   * @return  The new node, or <code>null</code> if the current node has no 
   *   next sibling in the TreeWalker's logical view.
   */
  public Node nextSibling()
  {
    return null;
  }

  /**
   *  Moves the <code>TreeWalker</code> to the last visible child of the 
   * current node, and returns the new node. If the current node has no 
   * visible children, returns <code>null</code> , and retains the current 
   * node.
   * @return  The new node, or <code>null</code> if the current node has no 
   *   children  in the TreeWalker's logical view.
   */
  public Node lastChild()
  {
    // We may need to support this...
    throw new RuntimeException("lastChild not supported!");
  }

  /**
   *  Moves the <code>TreeWalker</code> to the previous sibling of the 
   * current node, and returns the new node. If the current node has no 
   * visible previous sibling, returns <code>null</code> , and retains the 
   * current node.
   * @return  The new node, or <code>null</code> if the current node has no 
   *   previous sibling in the TreeWalker's logical view.
   */
  public Node previousSibling()
  {
    throw new RuntimeException("previousSibling not supported!");
  }

  /**
   *  Moves the <code>TreeWalker</code> to the previous visible node in 
   * document order relative to the current node, and returns the new node. 
   * If the current node has no previous node,  or if the search for 
   * previousNode attempts to step upward from the TreeWalker's root node, 
   * returns <code>null</code> , and retains the current node. 
   * @return  The new node, or <code>null</code> if the current node has no 
   *   previous node in the TreeWalker's logical view.
   */
  public Node previousNode()
  {
    throw new RuntimeException("previousNode not supported!");
  }
    
  private AxesWalker m_nextWalker;
  
  public void setNextWalker(AxesWalker walker)
  {
    m_nextWalker = walker;
  }

  public AxesWalker getNextWalker()
  {
    return m_nextWalker;
  }

  AxesWalker m_prevWalker;
  
  public void setPrevWalker(AxesWalker walker)
  {
    m_prevWalker = walker;
  }

  public AxesWalker getPrevWalker()
  {
    return m_prevWalker;
  }
  
  /**
   * Diagnostics.
   */
  public String toString()
  {
    Class cl = this.getClass();
    String clName = cl.getName();
    java.util.StringTokenizer tokenizer 
      = new java.util.StringTokenizer(clName, ".");
    while(tokenizer.hasMoreTokens())
      clName = tokenizer.nextToken();
    String rootName = (null == m_root) 
                      ? "null" 
                        : m_root.getNodeName() 
                          + "{"+((org.apache.xalan.stree.Child)m_root).getUid()+"}";
    String currentNodeName = (null == m_root) 
                             ? "null" 
                               : m_currentNode.getNodeName()
                                + "{"+((org.apache.xalan.stree.Child)m_currentNode).getUid()+"}";

    return clName+"["+rootName+"]["+currentNodeName+"]";
  }
  
  /**
   * Diagnostics.
   */
  protected String nodeToString(Node n)
  {
    return (null != n) 
           ? n.getNodeName()+"{"+ ((org.apache.xalan.stree.Child)n).getUid() + "}"
             : "null";
  }
    
  /**
   * Diagnostics.
   */
  private Node returnNextNode(Node n)
  {
    if(DEBUG_LOCATED && (null != n))
    {
      printDebug("RETURN --->"+nodeToString(n));
    }
    else if(DEBUG_LOCATED)
    {
      printDebug("RETURN --->null");
    }
    return n;
  }
  
  /**
   * Diagnostics.
   */
  private void printDebug(String s)
  {
    if(DEBUG)
    {
      System.out.print("\n");
      if(null != m_currentNode)
      {
        org.apache.xalan.stree.Child n = ((org.apache.xalan.stree.Child)m_currentNode);
        int depth = n.getLevel();
        for(int i = 0; i < depth; i++)
          System.out.print(" ");
      }
      System.out.print(s);
    }
  }
  
  /**
   * Do a diagnostics dump of the entire document.
   */
  private void dumpAll(Node node, int indent)
  {
    for(int i = 0; i < indent; i++)
      System.out.print(" ");

    System.out.print(nodeToString(node));
    if(Node.TEXT_NODE == node.getNodeType())
    {
      String value = node.getNodeValue();
      if(null != value)
      {
        System.out.print("+= -->"+value.trim());
      }
    }
    System.out.println("");
    NamedNodeMap map = node.getAttributes();
    if(null != map)
    {
      int n = map.getLength();
      for(int i = 0; i < n; i++)
      {
        for(int k = 0; k < indent; k++)
          System.out.print(" ");
        System.out.print("attr -->");
        System.out.print(nodeToString(map.item(i)));
        String value = map.item(i).getNodeValue();
        if(null != value)
        {
          System.out.print("+= -->"+value.trim());
        }
        System.out.println("");
      }
    }
    Node child = node.getFirstChild();
    while(null != child)
    {
      dumpAll(child, indent+1);
      child = child.getNextSibling();
    }
  }

  /**
   * Diagnostics.
   */
  private void printDebugAdd(String s)
  {
    if(DEBUG)
    {
      System.out.print("; "+s);
    }
  }

  /**
   * Diagnostics.
   */
  private void printEntryDebug()
  {
    if(true && DEBUG_TRAVERSAL)
    {
      System.out.print("\n============================\n");
      if(null != m_currentNode)
      {
        org.apache.xalan.stree.Child n = ((org.apache.xalan.stree.Child)m_currentNode);
        int depth = n.getLevel();
        for(int i = 0; i < depth; i++)
          System.out.print("+");
      }
      System.out.print(" "+this.toString()+", "+nodeToString(this.m_currentNode));
      printWaiters();
    }
  }
  
  /**
   * Diagnostics.
   */
  private void printWaiters()
  {
    if(DEBUG_WAITING)
    {
      int nWaiting = m_lpi.m_waiting.size();
      for(int i = 0; i < nWaiting; i++)
      { 
        AxesWalker ws = (AxesWalker)m_lpi.m_waiting.elementAt(i);
        printDebug("["+ws.toString()
                   +" WAITING... ]");
      }
      printDebug("Waiting count: "+nWaiting);
    }
  }
  
  // short-lived flag.
  private boolean m_waitingForNext = false;
  
  /**
   * Tell what's the maximum level this axes can descend to.
   */
  protected int getLevelMax()
  {
    return 0;
  }
  
  protected int m_nextLevelAmount;
  
  /**
   * Tell what's the next level this axes can descend to.
   */
  protected int getNextLevelAmount()
  {
    return m_nextLevelAmount;
  }
  
  /**
   * Tell if it's OK to traverse to the next node, following document 
   * order, or if the walker should wait for a condition to occur.
   * @prevStepWalker The previous walker in the location path.
   * @testWalker The walker being tested, but the state may not be intact, 
   * so only static information can be obtained from it.
   */
  protected boolean checkOKToTraverse(AxesWalker prevStepWalker, 
                                      AxesWalker testWalker, 
                                      Node currentTestNode,
                                      int nextLevelAmount)
  {
    DOMHelper dh = m_lpi.getDOMHelper();
    
    int level = dh.getLevel(currentTestNode);
    
    // Is this always the context node of the test walker?
    Node prevNode = prevStepWalker.m_currentNode;

    // Can the previous walker go past the one being tested?
    if(DEBUG_WAITING)
      printDebug("[prevStepWalker.getLevelMax():"+prevStepWalker.getLevelMax()
                 +" > level:"+level+"?]");
    boolean ok;
    if(!prevStepWalker.m_isDone && prevStepWalker.getLevelMax() > level) 
    {
      // Is (prevStepWalker.m_currentNode > the currentTestNode)?
      // (Sorry about the reverse logic).
      boolean isNodeAfter 
        = !dh.isNodeAfter(prevNode, currentTestNode);
      if(DEBUG_WAITING)
        printDebug("[isNodeAfter:"+isNodeAfter+"?]");
      if(isNodeAfter)
      {
        int prevStepLevel = dh.getLevel(prevNode);
        // If the previous step walker is below us in the tree, 
        // then we have to wait until it pops back up to our level, 
        // (if it ever does).
        if(DEBUG_WAITING)
          printDebug("[prevStepLevel:"+prevStepLevel
                     +" <= (level:"+level+"+nextLevelAmount:"+nextLevelAmount+"):"+(level+nextLevelAmount)+"?]");
        if(prevStepLevel > (level+nextLevelAmount))
        {
          // if next step is down, then ok = true, else
          // if next step is horizontal, then we have to wait.
          ok = false;
        }
        else
          ok = true;
      }
      else
        ok = false;
    }
    else
      ok = true;
    if(DEBUG_WAITING)
      printDebug("checkOKToTraverse = "+ok);
    
    return ok;
  }
    
  private boolean m_didSwitch = false;
  
  /**
   * Check if any walkers need to fire before the given walker.  If they 
   * do, then the given walker will be put on the waiting list, and the 
   * waiting walker will be returned.
   * @param walker The walker that is about to call nextNode(), or null.
   * @return walker argument or new walker.
   */
  AxesWalker checkWaiting(AxesWalker walker)
  {
    // printDebug("checkWaiting: "+walker.toString()+", "+nodeToString(walker.m_currentNode));
    if((null != walker) && (null == walker.m_currentNode))
      return walker;
    
    int nWaiting = m_lpi.m_waiting.size();
    for(int i = 0; i < nWaiting; i++)
    { 
      AxesWalker ws = (AxesWalker)m_lpi.m_waiting.elementAt(i);
      AxesWalker prevStepWalker = ws.m_prevWalker;
      if(null != prevStepWalker)
      {
        if(DEBUG_WAITING)
          printDebug("Calling checkOKToTraverse("+prevStepWalker.toString()+", "+
                     ws.toString()+", .);");

        if(checkOKToTraverse(prevStepWalker, ws, 
                                      ws.m_currentNode, ws.m_nextLevelAmount))
        {
          if(null != walker)
          {
            AxesWalker deferedWalker = walker;
            
            if(DEBUG_WAITING)
              printDebug("[Moving "+deferedWalker.toString()
                         +", "+nodeToString(deferedWalker.m_currentNode)+" to WAITING list]");
            if(!isWaiting(deferedWalker))
              m_lpi.addToWaitList(deferedWalker);
          }

          walker = ws;
          m_lpi.removeFromWaitList(walker);
          if(DEBUG_WAITING)
            printDebug("[And using WAITING on "+ws.toString());
          
          walker.printEntryDebug();
          m_didSwitch = true;
          break;
        }
      }
    }
            
    return walker;
  }
  
  /**
   * We have to do something to get things moving along, 
   * so get the earliest (in doc order) waiter.
   */
  private AxesWalker getEarliestWaiting()
  {
    DOMHelper dh = m_lpi.getDOMHelper();
    AxesWalker first = null;
    int nWaiting = m_lpi.m_waiting.size();
    for(int i = 0; i < nWaiting; i++)
    {
      AxesWalker ws = (AxesWalker)m_lpi.m_waiting.elementAt(i);
      if(first == null)
        first = ws;
      else
      {
        if(!dh.isNodeAfter(ws.m_currentNode, first.m_currentNode))
          first = ws;
      }
    }
    if(null != first)
    {
      m_lpi.removeFromWaitList(first);
      if(DEBUG_WAITING)
        printDebug("[(getEarliestWaiting)Using WAITING on "+first.toString());
      
      first.printEntryDebug();
    }
    return first;
  }
  
  /**
   * Tell if the given walker is already on the waiting list.
   */
  boolean isWaiting(AxesWalker walker)
  {
    int nWaiting = m_lpi.m_waiting.size();
    for(int i = 0; i < nWaiting; i++)
    { 
      AxesWalker ws = (AxesWalker)m_lpi.m_waiting.elementAt(i);
      if(ws == walker)
        return true;
    }
    return false;
  }
  
  /**
   * Check if a given walker needs to wait for the previous walker to 
   * catch up.
   */
  AxesWalker checkNeedsToWait(AxesWalker walker)
  {
    AxesWalker prevWalker = walker.m_prevWalker;
    if(null != prevWalker)
    {
      if(DEBUG_WAITING)
        printDebug("Calling checkOKToTraverse("+prevWalker.toString()+", "+
                   walker.toString()+", .);");
      if(!checkOKToTraverse(prevWalker, walker, 
                           walker.m_currentNode, walker.m_nextLevelAmount))
      {
        if(DEBUG_WAITING)
          printDebug("[Adding "+walker.toString()+" to WAITING list");
        if(isWaiting(walker))
        {
          try
          {
            if(DEBUG_WAITING)
              printDebug("checkNeedsToWait.clone: "+walker.toString());
            m_lpi.addToWaitList((AxesWalker)walker.clone());
          }
          catch(CloneNotSupportedException cnse)
          {
          }
        }
        else
          m_lpi.addToWaitList(walker);
        walker = walker.m_prevWalker;
        walker.printEntryDebug();
      }
    }
    return walker;
  }
  
  boolean m_isDone = false;
  
  /**
   * Get the next node in document order on the axes.
   */
  protected Node getNextNode()
  {
    if(m_isFresh)
      m_isFresh = false;
    Node next = this.firstChild();

    while(null == next)
    {     
      next = this.nextSibling();
      
      if(null == next)
      {
        Node p = this.parentNode();
        if(null == p)
          break;
      }
    }
    if(null == next)
      this.m_isDone = true;
    return next;
  }
  
  Node m_prevReturned;
  
  static boolean m_didDumpAll = false;

  /**
   *  Moves the <code>TreeWalker</code> to the next visible node in document 
   * order relative to the current node, and returns the new node. If the 
   * current node has no next node,  or if the search for nextNode attempts 
   * to step upward from the TreeWalker's root node, returns 
   * <code>null</code> , and retains the current node.
   * @return  The new node, or <code>null</code> if the current node has no 
   *   next node  in the TreeWalker's logical view.
   */
  public Node nextNode()
  {
    if(DEBUG_TRAVERSAL && !m_didDumpAll)
    {
      m_didDumpAll = true;
      Node doc = (Node.DOCUMENT_NODE == m_root.getNodeType()) ? m_root : m_root.getOwnerDocument();
      // dumpAll(doc, 0);
    }
    
    Node nextNode = null;
    AxesWalker walker = m_lpi.getLastUsedWalker();
    // DOMHelper dh = m_lpi.getDOMHelper();
    walker.printEntryDebug();
    m_didSwitch = false;
    boolean processWaiters = true;

    do
    {
      while(true)
      {
        // Check to see if there's any walkers that need to execute first.
        if(processWaiters)
        {
          AxesWalker waiting = checkWaiting(walker);
          if(m_didSwitch)
          {
            m_didSwitch = false;
            walker = waiting;
          }
          else if(null != walker)
          {
            waiting = checkNeedsToWait(walker);
            if(waiting != walker)
            {
              walker = waiting;
              continue;
            }
          }
        }
        else
          processWaiters = true;
        
        if(null == walker)
          break;
        
        nextNode = walker.getNextNode();
        
        if(DEBUG_TRAVERSAL)
          walker.printDebug(walker.toString()+"--NEXT->"+nodeToString(nextNode)+")");
        
        if(null == nextNode)
        {
          AxesWalker prev = walker;
          walker = walker.m_prevWalker;
          if(null != walker)
            walker.printEntryDebug();
          else
          {
            walker = getEarliestWaiting();
            if(null != walker)
            {
              processWaiters = false;
              continue;
            }
          }
        }
        else
        {
          if(walker.acceptNode(nextNode) != NodeFilter.FILTER_ACCEPT)
          {
            if(DEBUG_TRAVERSAL)
              printDebugAdd("[FILTER_SKIP]");
            continue;
          }
          else
          {
            if(DEBUG_TRAVERSAL)
              printDebugAdd("[FILTER_ACCEPT]");
          }
          
          if(null == walker.m_nextWalker)
          {
            // walker.pushState();
            if(DEBUG_TRAVERSAL)
              printDebug("May be returning: "+nodeToString(nextNode));
            if(DEBUG_TRAVERSAL && (null != m_prevReturned))
              printDebugAdd(", m_prevReturned: "+nodeToString(m_prevReturned));
            
            m_lpi.setLastUsedWalker(walker);
            // return walker.returnNextNode(nextNode);
            break;
          }
          else
          {
            AxesWalker prev = walker;
            walker = walker.m_nextWalker;
            /*
            if((walker.getRoot() != null) &&
               prev.getLevelMax() >= walker.getLevelMax()) // bogus, but might be ok
            */
            if(isWaiting(walker))
            {
              try
              {
                walker = (AxesWalker)walker.clone();
                // walker.pushState();
                walker.setRoot(nextNode);
                if(DEBUG_WAITING)
                  printDebug("clone: "+walker.toString());
              }
              catch(CloneNotSupportedException cnse)
              {
              }
            }
            else
            {
              walker.setRoot(nextNode);
            }
            walker.m_prevWalker = prev;
            walker.printEntryDebug();

            continue;
          }
        } // if(null != nextNode)
        
      } // while(null != walker)
      
    }
      while((null != nextNode) && (null != m_prevReturned)
            && m_lpi.getDOMHelper().isNodeAfter(nextNode, m_prevReturned));
    
    m_prevReturned = nextNode;
    if(DEBUG_LOCATED)
      return returnNextNode(nextNode);
    else
      return nextNode;
  }
  
  //============= End TreeWalker Implementation =============
  
  public int getLastPos(XPathContext xctxt)
  {
    int pos = getProximityPosition();
    
    AxesWalker walker;
    try
    {
      walker = (AxesWalker)clone();
    }
    catch(CloneNotSupportedException cnse)
    {
      return -1;
    }
    walker.setPredicateCount(walker.getPredicateCount() - 1);
    walker.setNextWalker(null);
    walker.setPrevWalker(null);
    LocPathIterator lpi = walker.getLocPathIterator();
    AxesWalker savedWalker = lpi.getLastUsedWalker();
    try
    {
      lpi.setLastUsedWalker(walker);
      Node next;
      while (null != (next = walker.nextNode()))
        pos++;
      // TODO: Should probably save this in the iterator.
    }
    finally
    {
      lpi.setLastUsedWalker(savedWalker);
    }
    // System.out.println("pos: "+pos);
    return pos;
  }

  //=============== NodeFilter Implementation ===============
  
  /**
   *  Test whether a specified node is visible in the logical view of a 
   * TreeWalker or NodeIterator. This function will be called by the 
   * implementation of TreeWalker and NodeIterator; it is not intended to 
   * be called directly from user code.
   * @param n  The node to check to see if it passes the filter or not.
   * @return  a constant to determine whether the node is accepted, 
   *   rejected, or skipped, as defined  above .
   */
  public short acceptNode(Node n)
  { 
    XPathContext xctxt = m_lpi.getXPathContext();
    try
    {
      xctxt.pushCurrentNode(n);
      XObject score = execute(xctxt);
      // System.out.println("::acceptNode - score: "+score.num()+"::");
      
      if(score != NodeTest.SCORE_NONE)
      {                    
        if(m_predicateCount > 0)
        {
          countProximityPosition(0);
          
          if(!executePredicates(n, xctxt))
            return NodeFilter.FILTER_SKIP;
        }
        return NodeFilter.FILTER_ACCEPT;
      }
    }
    catch(org.xml.sax.SAXException se)
    {
      // TODO: Fix this.
      throw new RuntimeException(se.getMessage());
    }
    finally
    {
      xctxt.popCurrentNode();
    }
    return NodeFilter.FILTER_SKIP;
  }

  
  //============= End NodeFilter Implementation =============
  
}
