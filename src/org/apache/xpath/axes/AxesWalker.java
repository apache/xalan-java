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
import java.util.Vector;

// Xalan imports
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.axes.SubContextList;
import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.compiler.OpCodes;
import org.apache.xpath.DOMHelper;
import org.apache.xpath.Expression;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.patterns.NodeTestFilter;
import org.apache.xpath.patterns.NodeTest;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPath;

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
public abstract class AxesWalker extends PredicatedNodeTest
        implements Cloneable, TreeWalker, NodeFilter
{
  
  /**
   * Construct an AxesWalker using a LocPathIterator.
   *
   * @param locPathIterator non-null reference to the parent iterator.
   */
  public AxesWalker(LocPathIterator locPathIterator)
  {
    super( locPathIterator );
  }

  /**
   * Initialize an AxesWalker during the parse of the XPath expression.
   *
   * @param compiler The Compiler object that has information about this 
   *                 walker in the op map.
   * @param opPos The op code position of this location step.
   * @param stepType  The type of location step.
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
   * @return A new AxesWalker that can be used without mutating this one.
   *
   * @throws CloneNotSupportedException
   */
  public Object clone() throws CloneNotSupportedException
  {
    // Do not access the location path itterator during this operation!
    
    AxesWalker clone = (AxesWalker) super.clone();

    //clone.setCurrentNode(clone.m_root);

    // clone.m_isFresh = true;

    return clone;
  }
  
  /**
   * Do a deep clone of this walker, including next and previous walkers.
   * If the this AxesWalker is on the clone list, don't clone but 
   * return the already cloned version.
   * 
   * @param cloneOwner non-null reference to the cloned location path 
   *                   iterator to which this clone will be added.
   * @param cloneList non-null vector of sources in odd elements, and the 
   *                  corresponding clones in even vectors.
   * 
   * @return non-null clone, which may be a new clone, or may be a clone 
   *         contained on the cloneList.
   */
  AxesWalker cloneDeep(LocPathIterator cloneOwner, Vector cloneList)
     throws CloneNotSupportedException
  {
    AxesWalker clone = findClone(this, cloneList);
    if(null != clone)
      return clone;
    clone = (AxesWalker)this.clone();
    clone.setLocPathIterator(cloneOwner);
    if(null != cloneList)
    {
      cloneList.addElement(this);
      cloneList.addElement(clone);
    }
    
    if(m_lpi.m_lastUsedWalker == this)
      cloneOwner.m_lastUsedWalker = clone;
      
    if(null != m_nextWalker)
      clone.m_nextWalker = m_nextWalker.cloneDeep(cloneOwner, cloneList);
      
    // If you don't check for the cloneList here, you'll go into an 
    // recursive infinate loop.  
    if(null != cloneList)
    {
      if(null != m_prevWalker)
        clone.m_prevWalker = m_prevWalker.cloneDeep(cloneOwner, cloneList);
    }
    else
    {
      if(null != m_nextWalker)
        clone.m_nextWalker.m_prevWalker = clone;
    }
    return clone;
  }
  
  /**
   * Find a clone that corresponds to the key argument.
   * 
   * @param key The original AxesWalker for which there may be a clone.
   * @param cloneList vector of sources in odd elements, and the 
   *                  corresponding clones in even vectors, may be null.
   * 
   * @return A clone that corresponds to the key, or null if key not found.
   */
  static AxesWalker findClone(AxesWalker key, Vector cloneList)
  {
    if(null != cloneList)
    {
      // First, look for clone on list.
      int n = cloneList.size();
      for (int i = 0; i < n; i+=2) 
      {
        if(key == cloneList.elementAt(i))
          return (AxesWalker)cloneList.elementAt(i+1);
      }
    }
    return null;    
  }
  
  /**
   * Tell if this expression or it's subexpressions can traverse outside 
   * the current subtree.
   * 
   * @return true if traversal outside the context node's subtree can occur.
   */
   public boolean canTraverseOutsideSubtree()
   {
    if(super.canTraverseOutsideSubtree())
      return true;
    if(null != m_nextWalker)
      return m_nextWalker.canTraverseOutsideSubtree();
    return false;
   }

  /**
   * The the step type op code.
   *
   *
   * @return An integer that represents an axes traversal opcode found in 
   * {@link org.apache.xpath.compiler.OpCodes}.
   */
  protected int getStepType()
  {
    return m_stepType;
  }

  /**
   * Get the argument length of the location step in the opcode map.
   * TODO: Can this be removed since it is only valuable at compile time?
   *
   * @return The argument length of the location step in the opcode map.
   */
  protected int getArgLen()
  {
    return m_argLen;
  }

  /**
   * Tell if the given node is a parent of the
   * step context, or the step context node itself.
   *
   * @param n The node being tested.
   *
   * @return true if n is a parent of the step context, or the step context 
   *              itself.
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
   * The root node of the TreeWalker, as specified in setRoot(Node root).
   * Note that this may actually be below the current node.
   *
   * @return The context node of the step.
   */
  public Node getRoot()
  {
    return m_root;
  }

  /**
   * Set the root node of the TreeWalker.
   * (Not part of the DOM2 TreeWalker interface).
   *
   * @param root The context node of this step.
   */
  public void setRoot(Node root)
  {

    m_isFresh = true;
    m_isDone = false;
    m_root = root;
    m_currentNode = root;
    m_prevReturned = null;

    if (null == root)
    {
      throw new RuntimeException(
        "\n !!!! Error! Setting the root of a walker to null!!!");
    }

    resetProximityPositions();
  }

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
   * @return The node at which the TreeWalker is currently positioned, only null 
   * if setRoot has not yet been called.
   * @throws DOMException
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
   * @param currentNode The current itteration node, should not be null.
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
   * @param currentNode The current node or null.
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
   * @return This AxesWalker.
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
   * @return true.
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

  /**
   * Set the next walker in the location step chain.
   *
   *
   * @param walker Reference to AxesWalker derivative, or may be null.
   */
  public void setNextWalker(AxesWalker walker)
  {
    m_nextWalker = walker;
  }

  /**
   * Get the next walker in the location step chain.
   *
   *
   * @return Reference to AxesWalker derivative, or null.
   */
  public AxesWalker getNextWalker()
  {
    return m_nextWalker;
  }

  /**
   * Set or clear the previous walker reference in the location step chain.
   *
   *
   * @param walker Reference to previous walker reference in the location 
   *               step chain, or null.
   */
  public void setPrevWalker(AxesWalker walker)
  {
    m_prevWalker = walker;
  }

  /**
   * Get the previous walker reference in the location step chain.
   *
   *
   * @return Reference to previous walker reference in the location 
   *               step chain, or null.
   */
  public AxesWalker getPrevWalker()
  {
    return m_prevWalker;
  }

  /**
   * Diagnostic string for this walker.
   *
   * @return Diagnostic string for this walker.
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
   * This is simply a way to bottle-neck the return of the next node, for 
   * diagnostic purposes.
   *
   * @param n Node to return, or null.
   *
   * @return The argument.
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
   * Print a diagnostics string, adding a line break before the print.
   *
   * @param s String to print.
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
   * Do a diagnostics dump of an entire subtree.
   *
   * @param node The top of the subtree.
   * @param indent The amount to begin the indenting at.
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
   * Print a diagnostic string without adding a line break.
   *
   * @param s The string to print.
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
      int nWaiting = m_lpi.getWaitingCount();

      for (int i = m_lpi.m_waitingBottom; i < nWaiting; i++)
      {
        AxesWalker ws = (AxesWalker) m_lpi.getWaiting(i);

        printDebug("[" + ws.toString() + " WAITING... ]");
      }

      printDebug("Waiting count: " + nWaiting);
    }
  }

  /**
   * Tell what's the maximum level this axes can descend to.  This method is 
   * meant to be overloaded by derived classes.
   *
   * @return An estimation of the maximum level this axes can descend to.
   */
  protected int getLevelMax()
  {
    return 0;
  }

  /**
   * Tell what's the next level this axes can descend to.
   *
   * @return An estimation of the next level that this walker will traverse to.
   */
  protected int getNextLevelAmount()
  {
    return m_nextLevelAmount;
  }

  /**
   * Tell if it's OK to traverse to the next node, following document
   * order, or if the walker should wait for a condition to occur.
   * 
   * @param prevStepWalker The previous walker in the location path.
   * @param testWalker The walker being tested, but the state may not be intact,
   * so only static information can be obtained from it.
   * @param currentTestNode The current node being testing.
   * @param nextLevelAmount An estimation of the next level to traverse to.
   *
   * @return True if it's OK for testWalker to traverse to nextLevelAmount.
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

    int nWaiting = m_lpi.getWaitingCount();

    for (int i = m_lpi.m_waitingBottom; i < nWaiting; i++)
    {
      AxesWalker ws = (AxesWalker) m_lpi.getWaiting(i);
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

            if (!isWaiting(deferedWalker))
            {
              addToWaitList(deferedWalker);
            }
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
   * @return the earliest (in doc order) waiting walker.
   */
  private AxesWalker getEarliestWaiting()
  {

    DOMHelper dh = m_lpi.getDOMHelper();
    AxesWalker first = null;
    int nWaiting = m_lpi.getWaitingCount();

    for (int i = m_lpi.m_waitingBottom; i < nWaiting; i++)
    {
      AxesWalker ws = (AxesWalker) m_lpi.getWaiting(i);

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
   * @param walker Reference to walker that is the subject of the test.
   *
   * @return  True if the walker argument is on the waiting list.
   */
  boolean isWaiting(AxesWalker walker)
  {

    int nWaiting = m_lpi.getWaitingCount();

    for (int i = m_lpi.m_waitingBottom; i < nWaiting; i++)
    {
      AxesWalker ws = (AxesWalker) m_lpi.getWaiting(i);

      if (ws == walker)
        return true;
    }

    return false;
  }
  
  private final void addToWaitList(AxesWalker walker)
  {
      if (DEBUG_WAITING)
        printDebug("[Moving " + walker.toString() + ", "
                   + nodeToString(walker.m_currentNode)
                   + " to WAITING list]");
                   
      m_lpi.addToWaitList(walker);
  }

  /**
   * Check if a given walker needs to wait for the previous walker to
   * catch up.
   *
   * @param walker The walker being checked.
   *
   * @return The walker or the previous walker.
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

            addToWaitList((AxesWalker) walker.clone());
          }
          catch (CloneNotSupportedException cnse){}
        }
        else
          addToWaitList(walker);

        walker = walker.m_prevWalker;
        
        if (DEBUG_WAITING)
          walker.printEntryDebug();
      }
    }

    return walker;
  }

  /**
   * Get the next node in document order on the axes.
   *
   * @return the next node in document order on the axes, or null.
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
   * Get the index of the last node that can be itterated to.
   *
   *
   * @param xctxt XPath runtime context.
   *
   * @return the index of the last node that can be itterated to.
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
  
  /**
   * Tell if this is a special type of walker compatible with ChildWalkerMultiStep.
   * 
   * @return true this is a special type of walker compatible with ChildWalkerMultiStep.
   */
  protected boolean isFastWalker()
  {
    return false;
  }

  //============= Static Data =============

  // These are useful to enable if you want to turn diagnostics messages 
  // on or off temporarily from another module.
//  public static boolean DEBUG = false;
//  public static boolean DEBUG_WAITING = false;
//  public static boolean DEBUG_TRAVERSAL = false;
//  public static boolean DEBUG_LOCATED = false;
//  public static boolean DEBUG_PREDICATECOUNTING = false;
  
  /** General static debug flag.  Setting this to false will suppress some 
   *  of the output messages caused by the other debug categories.  */
  static final boolean DEBUG = false;

  /** If true, diagnostic messages about the waiting queue will be posted.  */
  static final boolean DEBUG_WAITING = false;

  /** If true, diagnostic messages about the tree traversal will be posted.  */
  static final boolean DEBUG_TRAVERSAL = false;

  /** If true, diagnostic messages about the nodes that have 
   *  been 'located' will be posted.  */
  static final boolean DEBUG_LOCATED = false;

  /** For diagnostic purposes, tells if we already did a subtree dump.  */
  static boolean m_didDumpAll = false;

  /** String passed to {@link org.w3c.dom.Node#isSupported} to see if it implements 
   *  a {@link org.apache.xpath.patterns.NodeTestFilter} interface. */
  public static final String FEATURE_NODETESTFILTER = "NodeTestFilter";
  
  //============= State Data =============

  /**
   *  The root node of the TreeWalker, as specified when it was created.
   */
  transient Node m_root;

  /**
   *  The node at which the TreeWalker is currently positioned.
   */
  transient Node m_currentNode;
  
  /** The node last returned from nextNode(). */
  transient Node m_prevReturned;

  /**
   * The arg length of the XPath step. Does not change after the constructor.
   * TODO: Can this be removed since it is only valuable at compile time?
   * @serial
   */
  private int m_argLen;
  
  /**
   * The step type of the XPath step. Does not change after the constructor.
   * @serial
   */
  private int m_stepType;
    
  /** Fairly short lived flag to tell if we switched to a waiting walker.  */
  transient private boolean m_didSwitch = false;

  /** True if this walker has found it's last node.  */
  transient boolean m_isDone = false;

  /** True if an itteration has not begun.  */
  transient boolean m_isFresh;

  /** An estimation of the next level that this walker will traverse to.  Not 
   *  always accurate.  */
  transient protected int m_nextLevelAmount;

  /** The next walker in the location step chain.
   *  @serial  */
  protected AxesWalker m_nextWalker;
  
  /** The previous walker in the location step chain, or null.
   *  @serial   */
  AxesWalker m_prevWalker;
    
}
