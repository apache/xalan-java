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
import org.apache.xpath.patterns.NodeTestFilter;
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

  /** NEEDSDOC Field DEBUG          */
  static final boolean DEBUG = false;

  /** NEEDSDOC Field DEBUG_WAITING          */
  static final boolean DEBUG_WAITING = false;

  /** NEEDSDOC Field DEBUG_TRAVERSAL          */
  static final boolean DEBUG_TRAVERSAL = false;

  /** NEEDSDOC Field DEBUG_LOCATED          */
  static final boolean DEBUG_LOCATED = false;

  /** NEEDSDOC Field DEBUG_PREDICATECOUNTING          */
  static final boolean DEBUG_PREDICATECOUNTING = false;

  /** NEEDSDOC Field FEATURE_NODETESTFILTER          */
  public static final String FEATURE_NODETESTFILTER = "NodeTestFilter";

  /** NEEDSDOC Field m_lpi          */
  protected LocPathIterator m_lpi;

  /**
   * NEEDSDOC Method getLocPathIterator 
   *
   *
   * NEEDSDOC (getLocPathIterator) @return
   */
  public LocPathIterator getLocPathIterator()
  {
    return m_lpi;
  }

  /**
   * NEEDSDOC Method setLocPathIterator 
   *
   *
   * NEEDSDOC @param li
   */
  public void setLocPathIterator(LocPathIterator li)
  {
    m_lpi = li;
  }

  /**
   * Construct an AxesWalker using a LocPathIterator.
   *
   * NEEDSDOC @param locPathIterator
   */
  public AxesWalker(LocPathIterator locPathIterator)
  {
    m_lpi = locPathIterator;
  }

  /**
   * Init an AxesWalker.
   *
   * NEEDSDOC @param compiler
   * NEEDSDOC @param opPos
   * NEEDSDOC @param stepType
   *
   * @throws javax.xml.transform.TransformerException
   */
  public void init(Compiler compiler, int opPos, int stepType)
          throws javax.xml.transform.TransformerException
  {

    // int nodeTestOpPos = compiler.getFirstChildPosOfStep(opPos);
    m_stepType = stepType;

    switch (stepType)
    {
    case OpCodes.OP_VARIABLE :
    case OpCodes.OP_EXTFUNCTION :
    case OpCodes.OP_FUNCTION :
    case OpCodes.OP_GROUP :
      m_argLen = compiler.getArgLength(opPos);
      break;
    default :
      m_argLen = compiler.getArgLengthOfStep(opPos);
    }

    initPredicateInfo(compiler, opPos);

    // int testType = compiler.getOp(nodeTestOpPos);
  }

  /**
   * Get a cloned AxesWalker.
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws CloneNotSupportedException
   */
  public Object clone() throws CloneNotSupportedException
  {

    AxesWalker clone = (AxesWalker) super.clone();

    if ((null != this.m_proximityPositions)
            && (this.m_proximityPositions == clone.m_proximityPositions))
    {
      clone.m_proximityPositions = new int[this.m_proximityPositions.length];

      System.arraycopy(this.m_proximityPositions, 0,
                       clone.m_proximityPositions, 0,
                       this.m_proximityPositions.length);
    }

    //clone.setCurrentNode(clone.m_root);

    // clone.m_isFresh = true;

    return clone;
  }

  /**
   * The step type of the XPath step. Does not change after the constructor.
   */
  private int m_stepType;

  /**
   * NEEDSDOC Method getStepType 
   *
   *
   * NEEDSDOC (getStepType) @return
   */
  protected int getStepType()
  {
    return m_stepType;
  }

  /**
   * The arg length of the XPath step. Does not change after the constructor.
   */
  private int m_argLen;

  /**
   * NEEDSDOC Method getArgLen 
   *
   *
   * NEEDSDOC (getArgLen) @return
   */
  protected int getArgLen()
  {
    return m_argLen;
  }

  /**
   * The type of this walker based on the pattern analysis.
   * @see org.apache.xpath.axes.WalkerFactory
   */
  protected int m_analysis = WalkerFactory.NO_OPTIMIZE;

  /**
   * NEEDSDOC Method getAnalysis 
   *
   *
   * NEEDSDOC (getAnalysis) @return
   */
  int getAnalysis()
  {
    return m_analysis;
  }

  /**
   * NEEDSDOC Method setAnalysis 
   *
   *
   * NEEDSDOC @param a
   */
  void setAnalysis(int a)
  {
    m_analysis = a;
  }

  /**
   * An array of counts that correspond to the number
   * of predicates the step contains.
   */
  protected int[] m_proximityPositions;

  /**
   * Get the current sub-context position.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public int getProximityPosition()
  {

    // System.out.println("getProximityPosition - m_predicateIndex: "+m_predicateIndex);
    return getProximityPosition(m_predicateIndex);
  }

  /**
   * Get the current sub-context position.
   *
   * NEEDSDOC @param xctxt
   *
   * NEEDSDOC ($objectName$) @return
   */
  public int getProximityPosition(XPathContext xctxt)
  {
    return getProximityPosition();
  }

  /**
   * Get the current sub-context position.
   *
   * NEEDSDOC @param predicateIndex
   *
   * NEEDSDOC ($objectName$) @return
   */
  protected int getProximityPosition(int predicateIndex)
  {
    return (predicateIndex >= 0) ? m_proximityPositions[predicateIndex] : 0;
  }

  /**
   * Reset the proximity positions counts.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public void resetProximityPositions() throws javax.xml.transform.TransformerException
  {

    if (m_predicateCount > 0)
    {
      if (null == m_proximityPositions)
        m_proximityPositions = new int[m_predicateCount];

      for (int i = 0; i < m_predicateCount; i++)
      {
        initProximityPosition(i);
      }
    }
  }

  /**
   * Init the proximity position to zero for a forward axes.
   *
   * NEEDSDOC @param i
   *
   * @throws javax.xml.transform.TransformerException
   */
  public void initProximityPosition(int i) throws javax.xml.transform.TransformerException
  {
    m_proximityPositions[i] = 0;
  }

  /**
   * Count forward one proximity position.
   *
   * NEEDSDOC @param i
   */
  protected void countProximityPosition(int i)
  {
    if (i < m_proximityPositions.length)
      m_proximityPositions[i]++;
  }

  /**
   * Tells if this is a reverse axes.
   *
   * NEEDSDOC ($objectName$) @return
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
   *
   * NEEDSDOC ($objectName$) @return
   */
  public int getPredicateIndex()
  {
    return m_predicateIndex;
  }

  /**
   * Process the predicates.
   *
   * NEEDSDOC @param context
   * NEEDSDOC @param xctxt
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws javax.xml.transform.TransformerException
   */
  boolean executePredicates(Node context, XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {

    m_predicateIndex = 0;

    int nPredicates = m_predicateCount;

    if (nPredicates == 0)
      return true;

    PrefixResolver savedResolver = xctxt.getNamespaceContext();

    try
    {
      xctxt.pushSubContextList(this);
      xctxt.setNamespaceContext(m_lpi.getPrefixResolver());
      xctxt.pushCurrentNode(context);

      for (int i = 0; i < nPredicates; i++)
      {
        XObject pred;

        pred = m_predicates[i].execute(xctxt);

        if (XObject.CLASS_NUMBER == pred.getType())
        {
          if (DEBUG_PREDICATECOUNTING)
          {
            System.out.println("=============");
            System.out.println("m_predicateIndex: " + m_predicateIndex);
            System.out.println("getProximityPosition(m_predicateIndex): "
                               + getProximityPosition(m_predicateIndex));
            System.out.println("pred.num(): " + pred.num());
          }

          if (this.getProximityPosition(m_predicateIndex) != (int) pred.num())
          {
            return false;
          }
        }
        else if (!pred.bool())
          return false;

        countProximityPosition(++m_predicateIndex);
      }
    }
    finally
    {
      xctxt.popCurrentNode();
      xctxt.setNamespaceContext(savedResolver);
      xctxt.popSubContextList();
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
   *
   * NEEDSDOC ($objectName$) @return
   */
  public int getPredicateCount()
  {
    return m_predicateCount;
  }

  /**
   * Set the number of predicates that this walker has.
   *
   * NEEDSDOC @param count
   */
  public void setPredicateCount(int count)
  {
    m_predicateCount = count;
  }

  /**
   * Init predicate info.
   *
   * NEEDSDOC @param compiler
   * NEEDSDOC @param opPos
   *
   * @throws javax.xml.transform.TransformerException
   */
  private void initPredicateInfo(Compiler compiler, int opPos)
          throws javax.xml.transform.TransformerException
  {

    int pos = compiler.getFirstPredicateOpPos(opPos);

    m_predicates = compiler.getCompiledPredicates(pos);
    m_predicateCount = (null == m_predicates) ? 0 : m_predicates.length;
  }

  /** NEEDSDOC Field m_predicates          */
  private Expression[] m_predicates;

  /**
   * NEEDSDOC Method getPredicate 
   *
   *
   * NEEDSDOC @param index
   *
   * NEEDSDOC (getPredicate) @return
   */
  Expression getPredicate(int index)
  {
    return m_predicates[index];
  }

  /**
   * Tell if the given node is a parent of the
   * step context, or the step context node itself.
   *
   * NEEDSDOC @param n
   *
   * NEEDSDOC ($objectName$) @return
   */
  boolean isAncestorOfRootContext(Node n)
  {

    Node parent = m_root;

    while (null != (parent = parent.getParentNode()))
    {
      if (parent.equals(n))
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
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Node getRoot()
  {
    return m_root;
  }

  /** NEEDSDOC Field m_isFresh          */
  boolean m_isFresh;

  /**
   * Set the root node of the TreeWalker.
   * (Not part of the DOM2 TreeWalker interface).
   *
   * NEEDSDOC @param root
   */
  public void setRoot(Node root)
  {

    m_isFresh = true;
    m_root = root;
    m_currentNode = root;
    m_prevReturned = null;

    if (null == root)
    {
      throw new RuntimeException(
        "\n !!!! Error! Setting the root of a walker to null!!!");
    }

    try
    {
      resetProximityPositions();
    }
    catch (javax.xml.transform.TransformerException se)
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
   *
   * NEEDSDOC ($objectName$) @return
   * @exception DOMException
   *    NOT_SUPPORTED_ERR: Raised if the specified <code>currentNode</code>
   *   is<code>null</code> .
   */
  public final Node getCurrentNode()
  {
    return m_currentNode;
  }

  /**
   * Set the current node.
   *
   * NEEDSDOC @param currentNode
   *
   * @throws DOMException
   */
  public void setCurrentNode(Node currentNode) throws DOMException
  {
    m_currentNode = currentNode;
  }

  /**
   * Set the current node if it's not null.
   *
   * NEEDSDOC @param currentNode
   * @return The node passed in.
   *
   * @throws DOMException
   */
  protected Node setCurrentIfNotNull(Node currentNode) throws DOMException
  {

    if (null != currentNode)
      m_currentNode = currentNode;

    return currentNode;
  }

  /**
   *  The filter used to screen nodes.
   *
   * NEEDSDOC ($objectName$) @return
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
   *
   * NEEDSDOC ($objectName$) @return
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

  /** NEEDSDOC Field m_nextWalker          */
  protected AxesWalker m_nextWalker;

  /**
   * NEEDSDOC Method setNextWalker 
   *
   *
   * NEEDSDOC @param walker
   */
  public void setNextWalker(AxesWalker walker)
  {
    m_nextWalker = walker;
  }

  /**
   * NEEDSDOC Method getNextWalker 
   *
   *
   * NEEDSDOC (getNextWalker) @return
   */
  public AxesWalker getNextWalker()
  {
    return m_nextWalker;
  }

  /** NEEDSDOC Field m_prevWalker          */
  AxesWalker m_prevWalker;

  /**
   * NEEDSDOC Method setPrevWalker 
   *
   *
   * NEEDSDOC @param walker
   */
  public void setPrevWalker(AxesWalker walker)
  {
    m_prevWalker = walker;
  }

  /**
   * NEEDSDOC Method getPrevWalker 
   *
   *
   * NEEDSDOC (getPrevWalker) @return
   */
  public AxesWalker getPrevWalker()
  {
    return m_prevWalker;
  }

  /**
   * Diagnostics.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String toString()
  {

    Class cl = this.getClass();
    String clName = cl.getName();
    java.util.StringTokenizer tokenizer =
      new java.util.StringTokenizer(clName, ".");

    while (tokenizer.hasMoreTokens())
    {
      clName = tokenizer.nextToken();
    }

    String rootName;
    String currentNodeName;

    try
    {
      rootName = (null == m_root)
                 ? "null"
                 : m_root.getNodeName() + "{"
                   + ((org.apache.xalan.stree.Child) m_root).getUid() + "}";
      currentNodeName =
        (null == m_root)
        ? "null"
        : m_currentNode.getNodeName() + "{"
          + ((org.apache.xalan.stree.Child) m_currentNode).getUid() + "}";
    }
    catch (ClassCastException cce)
    {
      rootName = (null == m_root) ? "null" : m_root.getNodeName();
      currentNodeName = (null == m_root)
                        ? "null" : m_currentNode.getNodeName();
    }

    return clName + "[" + rootName + "][" + currentNodeName + "]";
  }

  /**
   * Diagnostics.
   *
   * NEEDSDOC @param n
   *
   * NEEDSDOC ($objectName$) @return
   */
  protected String nodeToString(Node n)
  {

    try
    {
      return (null != n)
             ? n.getNodeName() + "{" + ((org.apache.xalan.stree.Child) n).getUid() + "}"
             : "null";
    }
    catch (ClassCastException cce)
    {
      return (null != n) ? n.getNodeName() : "null";
    }
  }

  /**
   * Diagnostics.
   *
   * NEEDSDOC @param n
   *
   * NEEDSDOC ($objectName$) @return
   */
  private Node returnNextNode(Node n)
  {

    if (DEBUG_LOCATED && (null != n))
    {
      printDebug("RETURN --->" + nodeToString(n));
    }
    else if (DEBUG_LOCATED)
    {
      printDebug("RETURN --->null");
    }

    return n;
  }

  /**
   * Diagnostics.
   *
   * NEEDSDOC @param s
   */
  private void printDebug(String s)
  {

    if (DEBUG)
    {
      System.out.print("\n");

      if (null != m_currentNode)
      {
        try
        {
          org.apache.xalan.stree.Child n =
            ((org.apache.xalan.stree.Child) m_currentNode);
          int depth = n.getLevel();

          for (int i = 0; i < depth; i++)
          {
            System.out.print(" ");
          }
        }
        catch (ClassCastException cce){}
      }

      System.out.print(s);
    }
  }

  /**
   * Do a diagnostics dump of the entire document.
   *
   * NEEDSDOC @param node
   * NEEDSDOC @param indent
   */
  private void dumpAll(Node node, int indent)
  {

    for (int i = 0; i < indent; i++)
    {
      System.out.print(" ");
    }

    System.out.print(nodeToString(node));

    if (Node.TEXT_NODE == node.getNodeType())
    {
      String value = node.getNodeValue();

      if (null != value)
      {
        System.out.print("+= -->" + value.trim());
      }
    }

    System.out.println("");

    NamedNodeMap map = node.getAttributes();

    if (null != map)
    {
      int n = map.getLength();

      for (int i = 0; i < n; i++)
      {
        for (int k = 0; k < indent; k++)
        {
          System.out.print(" ");
        }

        System.out.print("attr -->");
        System.out.print(nodeToString(map.item(i)));

        String value = map.item(i).getNodeValue();

        if (null != value)
        {
          System.out.print("+= -->" + value.trim());
        }

        System.out.println("");
      }
    }

    Node child = node.getFirstChild();

    while (null != child)
    {
      dumpAll(child, indent + 1);

      child = child.getNextSibling();
    }
  }

  /**
   * Diagnostics.
   *
   * NEEDSDOC @param s
   */
  private void printDebugAdd(String s)
  {

    if (DEBUG)
    {
      System.out.print("; " + s);
    }
  }

  /**
   * Diagnostics.
   */
  private void printEntryDebug()
  {

    if (true && DEBUG_TRAVERSAL)
    {
      System.out.print("\n============================\n");

      if (null != m_currentNode)
      {
        try
        {
          org.apache.xalan.stree.Child n =
            ((org.apache.xalan.stree.Child) m_currentNode);
          int depth = n.getLevel();

          for (int i = 0; i < depth; i++)
          {
            System.out.print("+");
          }
        }
        catch (ClassCastException cce){}
      }

      System.out.print(" " + this.toString() + ", "
                       + nodeToString(this.m_currentNode));
      printWaiters();
    }
  }

  /**
   * Diagnostics.
   */
  private void printWaiters()
  {

    if (DEBUG_WAITING)
    {
      int nWaiting = m_lpi.m_waiting.size();

      for (int i = 0; i < nWaiting; i++)
      {
        AxesWalker ws = (AxesWalker) m_lpi.m_waiting.elementAt(i);

        printDebug("[" + ws.toString() + " WAITING... ]");
      }

      printDebug("Waiting count: " + nWaiting);
    }
  }

  // short-lived flag.

  /** NEEDSDOC Field m_waitingForNext          */
  private boolean m_waitingForNext = false;

  /**
   * Tell what's the maximum level this axes can descend to.
   *
   * NEEDSDOC ($objectName$) @return
   */
  protected int getLevelMax()
  {
    return 0;
  }

  /** NEEDSDOC Field m_nextLevelAmount          */
  protected int m_nextLevelAmount;

  /**
   * Tell what's the next level this axes can descend to.
   *
   * NEEDSDOC ($objectName$) @return
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
   *
   * NEEDSDOC @param prevStepWalker
   * NEEDSDOC @param testWalker
   * NEEDSDOC @param currentTestNode
   * NEEDSDOC @param nextLevelAmount
   *
   * NEEDSDOC ($objectName$) @return
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
    if (DEBUG_WAITING)
      printDebug("[prevStepWalker.getLevelMax():"
                 + prevStepWalker.getLevelMax() + " > level:" + level + "?]");

    boolean ok;

    if (!prevStepWalker.m_isDone && prevStepWalker.getLevelMax() > level)
    {

      // Is (prevStepWalker.m_currentNode > the currentTestNode)?
      // (Sorry about the reverse logic).
      boolean isNodeAfter = !dh.isNodeAfter(prevNode, currentTestNode);

      if (DEBUG_WAITING)
        printDebug("[isNodeAfter:" + isNodeAfter + "?]");

      if (isNodeAfter)
      {
        int prevStepLevel = dh.getLevel(prevNode);

        // If the previous step walker is below us in the tree, 
        // then we have to wait until it pops back up to our level, 
        // (if it ever does).
        if (DEBUG_WAITING)
          printDebug("[prevStepLevel:" + prevStepLevel + " <= (level:"
                     + level + "+nextLevelAmount:" + nextLevelAmount + "):"
                     + (level + nextLevelAmount) + "?]");

        if (prevStepLevel > (level + nextLevelAmount))
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

    if (DEBUG_WAITING)
      printDebug("checkOKToTraverse = " + ok);

    return ok;
  }

  /** NEEDSDOC Field m_didSwitch          */
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
    if ((null != walker) && (null == walker.m_currentNode))
      return walker;

    int nWaiting = m_lpi.m_waiting.size();

    for (int i = 0; i < nWaiting; i++)
    {
      AxesWalker ws = (AxesWalker) m_lpi.m_waiting.elementAt(i);
      AxesWalker prevStepWalker = ws.m_prevWalker;

      if (null != prevStepWalker)
      {
        if (DEBUG_WAITING)
          printDebug("Calling checkOKToTraverse(" + prevStepWalker.toString()
                     + ", " + ws.toString() + ", .);");

        if (checkOKToTraverse(prevStepWalker, ws, ws.m_currentNode,
                              ws.m_nextLevelAmount))
        {
          if (null != walker)
          {
            AxesWalker deferedWalker = walker;

            if (DEBUG_WAITING)
              printDebug("[Moving " + deferedWalker.toString() + ", "
                         + nodeToString(deferedWalker.m_currentNode)
                         + " to WAITING list]");

            if (!isWaiting(deferedWalker))
              m_lpi.addToWaitList(deferedWalker);
          }

          walker = ws;

          m_lpi.removeFromWaitList(walker);

          if (DEBUG_WAITING)
            printDebug("[And using WAITING on " + ws.toString());

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
   *
   * NEEDSDOC ($objectName$) @return
   */
  private AxesWalker getEarliestWaiting()
  {

    DOMHelper dh = m_lpi.getDOMHelper();
    AxesWalker first = null;
    int nWaiting = m_lpi.m_waiting.size();

    for (int i = 0; i < nWaiting; i++)
    {
      AxesWalker ws = (AxesWalker) m_lpi.m_waiting.elementAt(i);

      if (first == null)
        first = ws;
      else
      {
        if (!dh.isNodeAfter(ws.m_currentNode, first.m_currentNode))
          first = ws;
      }
    }

    if (null != first)
    {
      m_lpi.removeFromWaitList(first);

      if (DEBUG_WAITING)
        printDebug("[(getEarliestWaiting)Using WAITING on "
                   + first.toString());

      first.printEntryDebug();
    }

    return first;
  }

  /**
   * Tell if the given walker is already on the waiting list.
   *
   * NEEDSDOC @param walker
   *
   * NEEDSDOC ($objectName$) @return
   */
  boolean isWaiting(AxesWalker walker)
  {

    int nWaiting = m_lpi.m_waiting.size();

    for (int i = 0; i < nWaiting; i++)
    {
      AxesWalker ws = (AxesWalker) m_lpi.m_waiting.elementAt(i);

      if (ws == walker)
        return true;
    }

    return false;
  }

  /**
   * Check if a given walker needs to wait for the previous walker to
   * catch up.
   *
   * NEEDSDOC @param walker
   *
   * NEEDSDOC ($objectName$) @return
   */
  AxesWalker checkNeedsToWait(AxesWalker walker)
  {

    AxesWalker prevWalker = walker.m_prevWalker;

    if (null != prevWalker)
    {
      if (DEBUG_WAITING)
        printDebug("Calling checkOKToTraverse(" + prevWalker.toString()
                   + ", " + walker.toString() + ", .);");

      if (!checkOKToTraverse(prevWalker, walker, walker.m_currentNode,
                             walker.m_nextLevelAmount))
      {
        if (DEBUG_WAITING)
          printDebug("[Adding " + walker.toString() + " to WAITING list");

        if (isWaiting(walker))
        {
          try
          {
            if (DEBUG_WAITING)
              printDebug("checkNeedsToWait.clone: " + walker.toString());

            m_lpi.addToWaitList((AxesWalker) walker.clone());
          }
          catch (CloneNotSupportedException cnse){}
        }
        else
          m_lpi.addToWaitList(walker);

        walker = walker.m_prevWalker;

        walker.printEntryDebug();
      }
    }

    return walker;
  }

  /** NEEDSDOC Field m_isDone          */
  boolean m_isDone = false;

  /**
   * Get the next node in document order on the axes.
   *
   * NEEDSDOC ($objectName$) @return
   */
  protected Node getNextNode()
  {

    if (m_isFresh)
      m_isFresh = false;

    Node current = this.getCurrentNode();

    if (current.isSupported(FEATURE_NODETESTFILTER, "1.0"))
      ((NodeTestFilter) current).setNodeTest(this);

    Node next = this.firstChild();

    while (null == next)
    {
      next = this.nextSibling();

      if (null == next)
      {
        Node p = this.parentNode();

        if (null == p)
          break;
      }
    }

    if (null == next)
      this.m_isDone = true;

    return next;
  }

  /** NEEDSDOC Field m_prevReturned          */
  Node m_prevReturned;

  /** NEEDSDOC Field m_didDumpAll          */
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

    if (DEBUG_TRAVERSAL &&!m_didDumpAll)
    {
      m_didDumpAll = true;

      // Node doc = (Node.DOCUMENT_NODE == m_root.getNodeType()) ? m_root : m_root.getOwnerDocument();
      // dumpAll(doc, 0);
    }

    Node nextNode = null;
    AxesWalker walker = m_lpi.getLastUsedWalker();

    // DOMHelper dh = m_lpi.getDOMHelper();
    // walker.printEntryDebug();
    m_didSwitch = false;

    boolean processWaiters = true;

    do
    {
      while (true)
      {

        // Check to see if there's any walkers that need to execute first.
        if (processWaiters)
        {
          AxesWalker waiting = checkWaiting(walker);

          if (m_didSwitch)
          {
            m_didSwitch = false;
            walker = waiting;
          }
          else if (null != walker)
          {
            waiting = checkNeedsToWait(walker);

            if (waiting != walker)
            {
              walker = waiting;

              continue;
            }
          }
        }
        else
          processWaiters = true;

        if (null == walker)
          break;

        nextNode = walker.getNextNode();

        if (DEBUG_TRAVERSAL)
          walker.printDebug(walker.toString() + "--NEXT->"
                            + nodeToString(nextNode) + ")");

        if (null == nextNode)
        {

          // AxesWalker prev = walker; ?? -sb
          walker = walker.m_prevWalker;

          if (null != walker)
            walker.printEntryDebug();
          else
          {
            walker = getEarliestWaiting();

            if (null != walker)
            {
              processWaiters = false;

              continue;
            }
          }
        }
        else
        {
          if (walker.acceptNode(nextNode) != NodeFilter.FILTER_ACCEPT)
          {
            if (DEBUG_TRAVERSAL)
              printDebugAdd("[FILTER_SKIP]");

            continue;
          }
          else
          {
            if (DEBUG_TRAVERSAL)
              printDebugAdd("[FILTER_ACCEPT]");
          }

          if (null == walker.m_nextWalker)
          {

            // walker.pushState();
            if (DEBUG_TRAVERSAL)
              printDebug("May be returning: " + nodeToString(nextNode));

            if (DEBUG_TRAVERSAL && (null != m_prevReturned))
              printDebugAdd(", m_prevReturned: "
                            + nodeToString(m_prevReturned));

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
            if (isWaiting(walker))
            {
              try
              {
                walker = (AxesWalker) walker.clone();

                // walker.pushState();
                // System.out.println("AxesWalker - Calling setRoot(1)");
                walker.setRoot(nextNode);

                if (DEBUG_WAITING)
                  printDebug("clone: " + walker.toString());
              }
              catch (CloneNotSupportedException cnse){}
            }
            else
            {

              // System.out.println("AxesWalker - Calling setRoot(2)");
              walker.setRoot(nextNode);
            }

            walker.m_prevWalker = prev;

            walker.printEntryDebug();

            continue;
          }
        }  // if(null != nextNode)
      }  // while(null != walker)
    }

    // Not sure what is going on here, but we were loosing
    // the next node in the nodeset because it's coming from a 
    // different document. 
    while (
      (null != nextNode) && (null != m_prevReturned)
      && nextNode.getOwnerDocument() == m_prevReturned.getOwnerDocument()
      && m_lpi.getDOMHelper().isNodeAfter(nextNode, m_prevReturned));

    m_prevReturned = nextNode;

    if (DEBUG_LOCATED)
      return returnNextNode(nextNode);
    else
      return nextNode;
  }

  //============= End TreeWalker Implementation =============

  /**
   * NEEDSDOC Method getLastPos 
   *
   *
   * NEEDSDOC @param xctxt
   *
   * NEEDSDOC (getLastPos) @return
   */
  public int getLastPos(XPathContext xctxt)
  {

    int pos = getProximityPosition();
    AxesWalker walker;

    try
    {
      walker = (AxesWalker) clone();
    }
    catch (CloneNotSupportedException cnse)
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
      {
        pos++;
      }

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
      if (score != NodeTest.SCORE_NONE)
      {
        if (m_predicateCount > 0)
        {
          countProximityPosition(0);

          if (!executePredicates(n, xctxt))
            return NodeFilter.FILTER_SKIP;
        }

        return NodeFilter.FILTER_ACCEPT;
      }
    }
    catch (javax.xml.transform.TransformerException se)
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
