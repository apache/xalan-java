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

import org.apache.xpath.compiler.OpCodes;
import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.patterns.NodeTest;

import org.w3c.dom.traversal.NodeFilter;

/**
 * This class is both a factory for XPath location path expressions, 
 * which are built from the opcode map output, and an analysis engine 
 * for the location path expressions in order to provide optimization hints.
 */
public class WalkerFactory
{

  /**
   * <meta name="usage" content="advanced"/>
   * This method is for building an array of possible levels
   * where the target element(s) could be found for a match.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   *
   * @param lpi The owning location path iterator.
   * @param compiler non-null reference to compiler object that has processed 
   *                 the XPath operations into an opcode map.
   * @param stepOpCodePos The opcode position for the step.
   *
   * @return non-null AxesWalker derivative.
   *
   * @throws javax.xml.transform.TransformerException
   */
  static AxesWalker loadOneWalker(
          LocPathIterator lpi, Compiler compiler, int stepOpCodePos)
            throws javax.xml.transform.TransformerException
  {

    AxesWalker firstWalker = null;
    int stepType = compiler.getOpMap()[stepOpCodePos];

    if (stepType != OpCodes.ENDOP)
    {

      // m_axesWalkers = new AxesWalker[1];
      // As we unwind from the recursion, create the iterators.
      firstWalker = createDefaultWalker(compiler, stepType, lpi, 0);

      firstWalker.init(compiler, stepOpCodePos, stepType);
    }

    return firstWalker;
  }

  /**
   * <meta name="usage" content="advanced"/>
   * This method is for building an array of possible levels
   * where the target element(s) could be found for a match.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   *
   * @param lpi The owning location path iterator object.
   * @param compiler non-null reference to compiler object that has processed 
   *                 the XPath operations into an opcode map.
   * @param stepOpCodePos The opcode position for the step.
   * @param stepIndex The top-level step index withing the iterator.
   *
   * @return non-null AxesWalker derivative.
   *
   * @throws javax.xml.transform.TransformerException
   */
  static AxesWalker loadWalkers(
          LocPathIterator lpi, Compiler compiler, int stepOpCodePos, int stepIndex)
            throws javax.xml.transform.TransformerException
  {

    int stepType;
    AxesWalker firstWalker = null;
    AxesWalker walker, prevWalker = null;
    int ops[] = compiler.getOpMap();
    int analysis = analyze(compiler, stepOpCodePos, stepIndex);

    while (OpCodes.ENDOP != (stepType = ops[stepOpCodePos]))
    {
      walker = createDefaultWalker(compiler, stepOpCodePos, lpi, analysis);

      walker.init(compiler, stepOpCodePos, stepType);
      // walker.setAnalysis(analysis);

      if (null == firstWalker)
      {
        firstWalker = walker;
      }
      else
      {
        prevWalker.setNextWalker(walker);
        walker.setPrevWalker(prevWalker);
      }

      prevWalker = walker;
      stepOpCodePos = compiler.getNextStepPos(stepOpCodePos);

      if (stepOpCodePos < 0)
        break;
    }

    return firstWalker;
  }

  /**
   * Create a new LocPathIterator iterator.  The exact type of iterator 
   * returned is based on an analysis of the XPath operations.
   *
   * @param compiler non-null reference to compiler object that has processed 
   *                 the XPath operations into an opcode map.
   * @param opPos The position of the operation code for this itterator.
   *
   * @return non-null reference to a LocPathIterator or derivative.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public static LocPathIterator newLocPathIterator(
          Compiler compiler, int opPos)
            throws javax.xml.transform.TransformerException
  {
    int firstStepPos = compiler.getFirstChildPos(opPos);
    int analysis = analyze(compiler, firstStepPos, 0);
    
    
    // Is the iteration exactly one child step?
    if ((BIT_CHILD | 0x00000001) == (analysis & (BIT_CHILD | BITS_COUNT)))
    {
      //                 BIT_NODETEST_ANY: 1000000000000000000000000000000
      //                 BIT_PREDICATE:                      1000000000000
      // new iterator:  ChildIterator: 1000000000000010000000000000001, node()
      // Does the pattern specify *any* child with no predicate? (i.e. select="child::node()".
      if ((BIT_NODETEST_ANY == (analysis & BIT_NODETEST_ANY)) && 
         !(BIT_PREDICATE == (analysis & BIT_PREDICATE)))
      {
        if (DEBUG_ITERATOR_CREATION)
          System.out.println("new iterator:  ChildIterator: " 
                            + Integer.toBinaryString(analysis) + ", "
                             + compiler.toString());

        // Use simple child iteration without any test.
        return new ChildIterator(compiler, opPos, analysis);
      }
      else
      {
        if (DEBUG_ITERATOR_CREATION)
          System.out.println("new iterator:  ChildTestIterator: " 
                            + Integer.toBinaryString(analysis) + ", "
                             + compiler.toString());

        // Else use simple node test iteration with predicate test.
        return new ChildTestIterator(compiler, opPos, analysis);
      }
    }
    // Is the iteration a one-step attribute pattern (i.e. select="@foo")?
    else if ((BIT_ATTRIBUTE | 0x00000001)
             == (analysis & (BIT_ATTRIBUTE | BITS_COUNT)))
    {
      if (DEBUG_ITERATOR_CREATION)
        System.out.println("new iterator:  AttributeIterator: " 
                          + Integer.toBinaryString(analysis) + ", "
                           + compiler.toString());

      // Then use a simple iteration of the attributes, with node test 
      // and predicate testing.
      return new AttributeIterator(compiler, opPos, analysis);
    }
    // Analysis of "//center":
    // bits: 1001000000001010000000000000011
    // count: 3
    // root
    // child:node()
    // BIT_DESCENDANT_OR_SELF
    // It's highly possible that we should have a seperate bit set for 
    // "//foo" patterns.
    // For at least the time being, we can't optimize patterns like 
    // "//table[3]", because this has to be analyzed as 
    // "/descendant-or-self::node()/table[3]" in order for the indexes 
    // to work right.
    else if (0 == (BIT_PREDICATE & analysis) &&
            (((BIT_DESCENDANT | BIT_DESCENDANT_OR_SELF | 0x00000001)
             == (analysis
                 & (BIT_DESCENDANT | BIT_DESCENDANT_OR_SELF | BITS_COUNT))) ||
            ((BIT_DESCENDANT_OR_SELF | BIT_SELF | 0x00000002)
             == (analysis
                 & (BIT_DESCENDANT_OR_SELF | BIT_SELF | BITS_COUNT))) 
            
            /* ".//center" -- 1000010000001010000000000000011 */     
            || ((BIT_DESCENDANT_OR_SELF | BIT_SELF | BIT_CHILD | BIT_NODETEST_ANY | 0x00000003)
             == (analysis
              & (BIT_DESCENDANT_OR_SELF | BIT_SELF | BIT_CHILD | BIT_NODETEST_ANY | BITS_COUNT)))

            /* "//center" -- 1001000000001010000000000000011 */
            || ((BIT_DESCENDANT_OR_SELF | BIT_ROOT | BIT_CHILD | BIT_NODETEST_ANY | 
              BIT_ANY_DESCENDANT_FROM_ROOT | 0x00000003)
             == (analysis
                 & (BIT_DESCENDANT_OR_SELF | BIT_ROOT | BIT_CHILD | 
                    BIT_NODETEST_ANY | BIT_ANY_DESCENDANT_FROM_ROOT | BITS_COUNT))))
    )
    {
      if (DEBUG_ITERATOR_CREATION)
        System.out.println("new iterator:  DescendantIterator: " 
                          + Integer.toBinaryString(analysis) + ", "
                           + compiler.toString());

      return new DescendantIterator(compiler, opPos, analysis);
    }
    else
    {
      if (DEBUG_ITERATOR_CREATION)
        System.out.println("new iterator:  LocPathIterator: " 
                          + Integer.toBinaryString(analysis) + ", "
                           + compiler.toString());
      return new LocPathIterator(compiler, opPos, analysis, true);
    }
  }

  /**
   * Analyze the location path and return 32 bits that give information about 
   * the location path as a whole.  See the BIT_XXX constants for meaning about 
   * each of the bits.
   *
   * @param compiler non-null reference to compiler object that has processed 
   *                 the XPath operations into an opcode map.
   * @param stepOpCodePos The opcode position for the step.
   * @param stepIndex The top-level step index withing the iterator.
   *
   * @return 32 bits as an integer that give information about the location 
   * path as a whole.
   *
   * @throws javax.xml.transform.TransformerException
   */
  private static int analyze(
          Compiler compiler, int stepOpCodePos, int stepIndex)
            throws javax.xml.transform.TransformerException
  {

    int stepType;
    int ops[] = compiler.getOpMap();
    int stepCount = 0;
    int analysisResult = 0x00000000;  // 32 bits of analysis

    while (OpCodes.ENDOP != (stepType = ops[stepOpCodePos]))
    {
      stepCount++;

      // String namespace = compiler.getStepNS(stepOpCodePos);
      // boolean isNSWild = (null != namespace) 
      //                   ? namespace.equals(NodeTest.WILD) : false;
      // String localname = compiler.getStepLocalName(stepOpCodePos);
      // boolean isWild = (null != localname) ? localname.equals(NodeTest.WILD) : false;
      boolean predAnalysis = analyzePredicate(compiler, stepOpCodePos,
                                              stepType);

      if (predAnalysis)
        analysisResult |= BIT_PREDICATE;

      switch (stepType)
      {
      case OpCodes.OP_VARIABLE :
      case OpCodes.OP_EXTFUNCTION :
      case OpCodes.OP_FUNCTION :
      case OpCodes.OP_GROUP :
        analysisResult |= BIT_FILTER;
        break;
      case OpCodes.FROM_ROOT :
        analysisResult |= BIT_ROOT;
        break;
      case OpCodes.FROM_ANCESTORS :
        analysisResult |= BIT_ANCESTOR;
        break;
      case OpCodes.FROM_ANCESTORS_OR_SELF :
        analysisResult |= BIT_ANCESTOR_OR_SELF;
        break;
      case OpCodes.FROM_ATTRIBUTES :
        analysisResult |= BIT_ATTRIBUTE;
        break;
      case OpCodes.FROM_NAMESPACE :
        analysisResult |= BIT_NAMESPACE;
        break;
      case OpCodes.FROM_CHILDREN :
        analysisResult |= BIT_CHILD;
        break;
      case OpCodes.FROM_DESCENDANTS :
        analysisResult |= BIT_DESCENDANT;
        break;
      case OpCodes.FROM_DESCENDANTS_OR_SELF :
        // Use a special bit to to make sure we get the right analysis of "//foo".
        if(2 == stepCount && BIT_ROOT == analysisResult)
        {
          analysisResult |= BIT_ANY_DESCENDANT_FROM_ROOT;
        }
        analysisResult |= BIT_DESCENDANT_OR_SELF;
        break;
      case OpCodes.FROM_FOLLOWING :
        analysisResult |= BIT_DESCENDANT_OR_SELF;
        break;
      case OpCodes.FROM_FOLLOWING_SIBLINGS :
        analysisResult |= BIT_FOLLOWING_SIBLING;
        break;
      case OpCodes.FROM_PRECEDING :
        analysisResult |= BIT_PRECEDING;
        break;
      case OpCodes.FROM_PRECEDING_SIBLINGS :
        analysisResult |= BIT_PRECEDING_SIBLING;
        break;
      case OpCodes.FROM_PARENT :
        analysisResult |= BIT_PARENT;
        break;
      case OpCodes.FROM_SELF :
        analysisResult |= BIT_SELF;
        break;
      case OpCodes.MATCH_ATTRIBUTE :
        analysisResult |= (BIT_MATCH_PATTERN | BIT_ATTRIBUTE);
        break;
      case OpCodes.MATCH_ANY_ANCESTOR :
        analysisResult |= (BIT_MATCH_PATTERN | BIT_ANCESTOR);
        break;
      case OpCodes.MATCH_IMMEDIATE_ANCESTOR :
        analysisResult |= (BIT_MATCH_PATTERN | BIT_PARENT);
        break;
      default :
        throw new RuntimeException("Programmer's assertion: unknown opcode: "
                                   + stepType);
      }

      if (OpCodes.NODETYPE_NODE == ops[stepOpCodePos + 3])  // child::node()
      {
        analysisResult |= BIT_NODETEST_ANY;
      }

      stepOpCodePos = compiler.getNextStepPos(stepOpCodePos);

      if (stepOpCodePos < 0)
        break;
    }

    analysisResult |= (stepCount & BITS_COUNT);

    return analysisResult;
  }

  /**
   * Analyze a step and give information about it's predicates.  Right now this 
   * just returns true or false if the step has a predicate.
   *
   * @param compiler non-null reference to compiler object that has processed 
   *                 the XPath operations into an opcode map.
   * @param opPos The opcode position for the step.
   * @param stepType The type of step, one of OP_GROUP, etc.
   *
   * @return true if step has a predicate.
   *
   * @throws javax.xml.transform.TransformerException
   */
  static boolean analyzePredicate(Compiler compiler, int opPos, int stepType)
          throws javax.xml.transform.TransformerException
  {

    int argLen;

    switch (stepType)
    {
    case OpCodes.OP_VARIABLE :
    case OpCodes.OP_EXTFUNCTION :
    case OpCodes.OP_FUNCTION :
    case OpCodes.OP_GROUP :
      argLen = compiler.getArgLength(opPos);
      break;
    default :
      argLen = compiler.getArgLengthOfStep(opPos);
    }

    int pos = compiler.getFirstPredicateOpPos(opPos);
    int nPredicates = compiler.countPredicates(pos);

    return (nPredicates > 0) ? true : false;
  }

  /**
   * Create the proper Walker from the axes type.
   *
   * @param compiler non-null reference to compiler object that has processed 
   *                 the XPath operations into an opcode map.
   * @param opPos The opcode position for the step.
   * @param lpi The owning location path iterator.
   * @param analysis 32 bits of analysis, from which the type of AxesWalker 
   *                 may be influenced.
   *
   * @return non-null reference to AxesWalker derivative.
   * @throws RuntimeException if the input is bad.
   */
  private static AxesWalker createDefaultWalker(Compiler compiler, int opPos,
          LocPathIterator lpi, int analysis)
  {

    AxesWalker ai;
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
    int totalNumberWalkers = (analysis & BITS_COUNT);
    boolean prevIsOneStepDown = true;

    switch (stepType)
    {
    case OpCodes.OP_VARIABLE :
    case OpCodes.OP_EXTFUNCTION :
    case OpCodes.OP_FUNCTION :
    case OpCodes.OP_GROUP :
      prevIsOneStepDown = false;
      if (DEBUG_WALKER_CREATION)
        System.out.println("new walker:  FilterExprWalker: " + analysis
                           + ", " + compiler.toString());
      ai = new FilterExprWalker(lpi);
      simpleInit = true;
      break;
    case OpCodes.FROM_ROOT :
      if (0 == (analysis & ~(BIT_ROOT | BIT_CHILD | BIT_ATTRIBUTE | 
                             BIT_NAMESPACE | BIT_PREDICATE | BITS_COUNT)))
      {
        if (DEBUG_WALKER_CREATION)
          System.out.println("new walker:  RootWalkerMultiStep: " + analysis
                             + ", " + compiler.toString());
                             
        ai = new RootWalkerMultiStep(lpi);
      }
      else
      {
        if (DEBUG_WALKER_CREATION)
          System.out.println("new walker:  RootWalker: " + analysis
                             + ", " + compiler.toString());
        ai = new RootWalker(lpi);
      }
      break;
    case OpCodes.FROM_ANCESTORS :
      prevIsOneStepDown = false;
      if (DEBUG_WALKER_CREATION)
        System.out.println("new walker:  AncestorWalker: " + analysis
                           + ", " + compiler.toString());
      ai = new AncestorWalker(lpi);
      break;
    case OpCodes.FROM_ANCESTORS_OR_SELF :
      prevIsOneStepDown = false;
      if (DEBUG_WALKER_CREATION)
        System.out.println("new walker:  AncestorOrSelfWalker: " + analysis
                           + ", " + compiler.toString());
      ai = new AncestorOrSelfWalker(lpi);
      break;
    case OpCodes.FROM_ATTRIBUTES :
      if (1 == totalNumberWalkers)
      {
        if (DEBUG_WALKER_CREATION)
          System.out.println("new walker:  AttributeWalkerOneStep: " + analysis
                             + ", " + compiler.toString());

        // TODO: We should be able to do this as long as this is 
        // the last step.
        ai = new AttributeWalkerOneStep(lpi);
      }
      else
      {
        if (DEBUG_WALKER_CREATION)
          System.out.println("new walker:  AttributeWalker: " + analysis
                             + ", " + compiler.toString());
                             
        ai = new AttributeWalker(lpi);
      }
      break;
    case OpCodes.FROM_NAMESPACE :
      if (DEBUG_WALKER_CREATION)
        System.out.println("new walker:  NamespaceWalker: " + analysis
                           + ", " + compiler.toString());
      ai = new NamespaceWalker(lpi);
      break;
    case OpCodes.FROM_CHILDREN :
      if (1 == totalNumberWalkers)
      {
        // I don't think this will ever happen any more.  -sb
        if (DEBUG_WALKER_CREATION)
          System.out.println("new walker:  ChildWalkerOneStep: " + analysis + ", "
                             + compiler.toString());

        ai = new ChildWalkerOneStep(lpi);
      }
      else
      {
        if (0 == (analysis & ~(BIT_ROOT | BIT_CHILD | BIT_ATTRIBUTE | 
                               BIT_NAMESPACE | BIT_PREDICATE | BITS_COUNT)))
        {
          if (DEBUG_WALKER_CREATION)
            System.out.println("new walker:  ChildWalkerMultiStep: " + analysis
                               + ", " + compiler.toString());

          ai = new ChildWalkerMultiStep(lpi);
        }
        else
        {
          if (DEBUG_WALKER_CREATION)
            System.out.println("new walker:  ChildWalker: " + analysis
                               + ", " + compiler.toString());
          ai = new ChildWalker(lpi);
        }
      }
      break;
    case OpCodes.FROM_DESCENDANTS :
      prevIsOneStepDown = false;
      if (DEBUG_WALKER_CREATION)
        System.out.println("new walker:  DescendantWalker: " + analysis
                           + ", " + compiler.toString());
      ai = new DescendantWalker(lpi);
      break;
    case OpCodes.FROM_DESCENDANTS_OR_SELF :
      prevIsOneStepDown = false;
      if (DEBUG_WALKER_CREATION)
        System.out.println("new walker:  DescendantOrSelfWalker: " + analysis
                           + ", " + compiler.toString());
      ai = new DescendantOrSelfWalker(lpi);
      break;
    case OpCodes.FROM_FOLLOWING :
      prevIsOneStepDown = false;
      if (DEBUG_WALKER_CREATION)
        System.out.println("new walker:  FollowingWalker: " + analysis
                           + ", " + compiler.toString());
      ai = new FollowingWalker(lpi);
      break;
    case OpCodes.FROM_FOLLOWING_SIBLINGS :
      prevIsOneStepDown = false;
      if (DEBUG_WALKER_CREATION)
        System.out.println("new walker:  FollowingSiblingWalker: " + analysis
                           + ", " + compiler.toString());
      ai = new FollowingSiblingWalker(lpi);
      break;
    case OpCodes.FROM_PRECEDING :
      prevIsOneStepDown = false;
      if (DEBUG_WALKER_CREATION)
        System.out.println("new walker:  PrecedingWalker: " + analysis
                           + ", " + compiler.toString());
      ai = new PrecedingWalker(lpi);
      break;
    case OpCodes.FROM_PRECEDING_SIBLINGS :
      prevIsOneStepDown = false;
      if (DEBUG_WALKER_CREATION)
        System.out.println("new walker:  PrecedingSiblingWalker: " + analysis
                           + ", " + compiler.toString());
      ai = new PrecedingSiblingWalker(lpi);
      break;
    case OpCodes.FROM_PARENT :
      prevIsOneStepDown = false;
      if (DEBUG_WALKER_CREATION)
        System.out.println("new walker:  ParentWalker: " + analysis
                           + ", " + compiler.toString());
      ai = new ParentWalker(lpi);
      break;
    case OpCodes.FROM_SELF :
      if (1 == totalNumberWalkers)
      {
        if (DEBUG_WALKER_CREATION)
          System.out.println("new walker:  SelfWalkerOneStep: " + analysis
                             + ", " + compiler.toString());

        ai = new SelfWalkerOneStep(lpi);
      }
      else
      {
        if (DEBUG_WALKER_CREATION)
          System.out.println("new walker:  SelfWalker: " + analysis
                             + ", " + compiler.toString());
        ai = new SelfWalker(lpi);
      }
      break;
    case OpCodes.MATCH_ATTRIBUTE :
      if (DEBUG_WALKER_CREATION)
        System.out.println("new walker:  AttributeWalker(MATCH_ATTRIBUTE): " + analysis
                           + ", " + compiler.toString());
      ai = new AttributeWalker(lpi);
      break;
    case OpCodes.MATCH_ANY_ANCESTOR :
      if (DEBUG_WALKER_CREATION)
        System.out.println("new walker:  ChildWalker(MATCH_ANY_ANCESTOR): " + analysis
                           + ", " + compiler.toString());
      ai = new ChildWalker(lpi);
      break;
    case OpCodes.MATCH_IMMEDIATE_ANCESTOR :
      if (DEBUG_WALKER_CREATION)
        System.out.println("new walker:  ChildWalker(MATCH_IMMEDIATE_ANCESTOR): " + analysis
                           + ", " + compiler.toString());
      ai = new ChildWalker(lpi);
      break;
    default :
      throw new RuntimeException("Programmer's assertion: unknown opcode: "
                                 + stepType);
    }

    if (simpleInit)
    {
      ai.initNodeTest(NodeFilter.SHOW_ALL);
    }
    else
    {
      int whatToShow = compiler.getWhatToShow(opPos);

      /*
      System.out.print("construct: ");
      NodeTest.debugWhatToShow(whatToShow);
      System.out.println("or stuff: "+(whatToShow & (NodeFilter.SHOW_ATTRIBUTE
                             | NodeFilter.SHOW_ELEMENT
                             | NodeFilter.SHOW_PROCESSING_INSTRUCTION)));
      */
      if ((0 == (whatToShow
                 & (NodeFilter.SHOW_ATTRIBUTE | NodeFilter.SHOW_ELEMENT
                    | NodeFilter.SHOW_PROCESSING_INSTRUCTION))) || (whatToShow == NodeFilter.SHOW_ALL))
        ai.initNodeTest(whatToShow);
      else
      {
        ai.initNodeTest(whatToShow, compiler.getStepNS(opPos),
                        compiler.getStepLocalName(opPos));
      }
    }

    return ai;
  }

  /** Set to true for diagnostics about walker creation */
  static final boolean DEBUG_WALKER_CREATION = false;

  /** Set to true for diagnostics about iterator creation */
  static final boolean DEBUG_ITERATOR_CREATION = false;

  /**
   * First 8 bits are the number of top-level location steps.  Hopefully
   *  there will never be more that 255 location steps!!! 
   */
  public static final int BITS_COUNT = 0x000000FF;

  /** 4 bits are reserved for future use. */
  public static final int BITS_RESERVED = 0x00000F00;

  /** Bit is on if the expression contains a top-level predicate. */
  public static final int BIT_PREDICATE = (0x00001000);

  /** Bit is on if any of the walkers contain an ancestor step. */
  public static final int BIT_ANCESTOR = (0x00001000 << 1);

  /** Bit is on if any of the walkers contain an ancestor-or-self step. */
  public static final int BIT_ANCESTOR_OR_SELF = (0x00001000 << 2);

  /** Bit is on if any of the walkers contain an attribute step. */
  public static final int BIT_ATTRIBUTE = (0x00001000 << 3);

  /** Bit is on if any of the walkers contain a child step. */
  public static final int BIT_CHILD = (0x00001000 << 4);

  /** Bit is on if any of the walkers contain a descendant step. */
  public static final int BIT_DESCENDANT = (0x00001000 << 5);

  /** Bit is on if any of the walkers contain a descendant-or-self step. */
  public static final int BIT_DESCENDANT_OR_SELF = (0x00001000 << 6);

  /** Bit is on if any of the walkers contain a following step. */
  public static final int BIT_FOLLOWING = (0x00001000 << 7);

  /** Bit is on if any of the walkers contain a following-sibiling step. */
  public static final int BIT_FOLLOWING_SIBLING = (0x00001000 << 8);

  /** Bit is on if any of the walkers contain a namespace step. */
  public static final int BIT_NAMESPACE = (0x00001000 << 9);

  /** Bit is on if any of the walkers contain a parent step. */
  public static final int BIT_PARENT = (0x00001000 << 10);

  /** Bit is on if any of the walkers contain a preceding step. */
  public static final int BIT_PRECEDING = (0x00001000 << 11);

  /** Bit is on if any of the walkers contain a preceding-sibling step. */
  public static final int BIT_PRECEDING_SIBLING = (0x00001000 << 12);

  /** Bit is on if any of the walkers contain a self step. */
  public static final int BIT_SELF = (0x00001000 << 13);

  /**
   * Bit is on if any of the walkers contain a filter (i.e. id(), extension
   *  function, etc.) step. 
   */
  public static final int BIT_FILTER = (0x00001000 << 14);

  /** Bit is on if any of the walkers contain a root step. */
  public static final int BIT_ROOT = (0x00001000 << 15);
  
  /** If any of these bits are on, the expression may likely traverse outside 
   *  the given subtree. */
  public static final int BITMASK_TRAVERSES_OUTSIDE_SUBTREE 
                                                   = (BIT_NAMESPACE // ??
                                                    | BIT_PRECEDING_SIBLING
                                                    | BIT_PRECEDING
                                                    | BIT_FOLLOWING_SIBLING
                                                    | BIT_FOLLOWING
                                                    | BIT_PARENT // except parent of attrs.
                                                    | BIT_ANCESTOR_OR_SELF
                                                    | BIT_ANCESTOR
                                                    | BIT_FILTER
                                                    | BIT_ROOT);                                                    

  /**
   * Bit is on if any of the walkers can go backwards in document
   *  order from the context node. 
   */
  public static final int BIT_BACKWARDS_SELF = (0x00001000 << 16);

  /** Found "//foo" pattern */
  public static final int BIT_ANY_DESCENDANT_FROM_ROOT = (0x00001000 << 17);

  /**
   * Bit is on if any of the walkers contain an node() test.  This is
   *  really only useful if the count is 1. 
   */
  public static final int BIT_NODETEST_ANY = (0x00001000 << 18);
  
  // can't go higher than 18!

  /** Bit is on if the expression is a match pattern. */
  public static final int BIT_MATCH_PATTERN = (0x00001000 << 19);

}
