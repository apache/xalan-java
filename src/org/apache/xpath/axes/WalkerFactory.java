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
 * <meta name="usage" content="internal"/>
 * NEEDSDOC Class WalkerFactory <needs-comment/>
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
   * NEEDSDOC @param lpi
   * NEEDSDOC @param compiler
   * NEEDSDOC @param stepOpCodePos
   *
   * NEEDSDOC ($objectName$) @return
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
   * NEEDSDOC @param lpi
   * NEEDSDOC @param compiler
   * NEEDSDOC @param stepOpCodePos
   * NEEDSDOC @param stepIndex
   *
   * NEEDSDOC ($objectName$) @return
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
      walker.setAnalysis(analysis);

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
   * NEEDSDOC Method newLocPathIterator 
   *
   *
   * NEEDSDOC @param compiler
   * NEEDSDOC @param opPos
   *
   * NEEDSDOC (newLocPathIterator) @return
   *
   * @throws javax.xml.transform.TransformerException
   */
  public static LocPathIterator newLocPathIterator(
          Compiler compiler, int opPos) throws javax.xml.transform.TransformerException
  {

    int firstStepPos = compiler.getFirstChildPos(opPos);
    int analysis = analyze(compiler, firstStepPos, 0);

    if (ONESTEP_CHILDREN_ANY == analysis)
    {
      return new ChildIterator(compiler, opPos);
    }
    else if(ONESTEP_CHILDREN_NO_PREDICATE == analysis)
    {
      return new ChildTestIterator(compiler, opPos);
    }
    else if(ONESTEP_ATTR_NO_PREDICATES == analysis)
    {
      return new AttributeIterator(compiler, opPos);
    }
    else
    {
      return new LocPathIterator(compiler, opPos, true);
    }
  }

  // There is no optimized walker that can handle 
  // this pattern, so use the default.

  /** Pattern that we do not optimize for.  */
  static final int NO_OPTIMIZE = 1;

  /** "."  */
  static final int ONESTEP_SELF = 2;

  /** "*" */
  static final int ONESTEP_CHILDREN = 3;

  /** "node()"  */
  static final int ONESTEP_CHILDREN_ANY = 7;

  /** "@foo[../baz]"  */
  static final int ONESTEP_ATTR = 4;

  /** "@foo"  */
  static final int ONESTEP_ATTR_NO_PREDICATES = 9;

  /** NEEDSDOC Field ONESTEP_DESCENDANTS          */
  static final int ONESTEP_DESCENDANTS = 5;

  /** NEEDSDOC Field MULTISTEP_CHILDREN          */
  static final int MULTISTEP_CHILDREN = 6;

  /** NEEDSDOC Field ONESTEP_CHILDREN          */
  static final int ONESTEP_CHILDREN_NO_PREDICATE = 8;

  /**
   * <meta name="usage" content="advanced"/>
   *
   * NEEDSDOC @param compiler
   * NEEDSDOC @param stepOpCodePos
   * NEEDSDOC @param stepIndex
   *
   * NEEDSDOC ($objectName$) @return
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
    int analysisResult = NO_OPTIMIZE;

    while (OpCodes.ENDOP != (stepType = ops[stepOpCodePos]))
    {
      stepCount++;

      // String namespace = compiler.getStepNS(stepOpCodePos);
      // boolean isNSWild = (null != namespace) 
      //                   ? namespace.equals(NodeTest.WILD) : false;
      // String localname = compiler.getStepLocalName(stepOpCodePos);
      // boolean isWild = (null != localname) ? localname.equals(NodeTest.WILD) : false;
      int predAnalysis = analyzePredicate(compiler, stepOpCodePos, stepType);
      switch (stepType)
      {
      case OpCodes.OP_VARIABLE :
      case OpCodes.OP_EXTFUNCTION :
      case OpCodes.OP_FUNCTION :
      case OpCodes.OP_GROUP :
        return NO_OPTIMIZE;  // at least for now
      case OpCodes.FROM_ROOT :
        return NO_OPTIMIZE;  // at least for now
      case OpCodes.FROM_ANCESTORS :
        return NO_OPTIMIZE;  // at least for now
      case OpCodes.FROM_ANCESTORS_OR_SELF :
        return NO_OPTIMIZE;  // at least for now
      case OpCodes.FROM_ATTRIBUTES :
        if (1 == stepCount)
        {
          if(predAnalysis == HAS_NOPREDICATE)
            analysisResult = ONESTEP_ATTR_NO_PREDICATES;
          else
            analysisResult = ONESTEP_ATTR;
        }
        else
        {
          return NO_OPTIMIZE;  // at least for now
        }
        break;
      case OpCodes.FROM_NAMESPACE :
        return NO_OPTIMIZE;  // at least for now
      case OpCodes.FROM_CHILDREN :
        if (1 == stepCount)
        {
          if (OpCodes.NODETYPE_NODE == ops[stepOpCodePos+3])    // child::node()
          {
            // System.out.println("ONESTEP_CHILDREN_ANY");
            if(predAnalysis == HAS_NOPREDICATE)
              analysisResult = ONESTEP_CHILDREN_ANY;
            else
              analysisResult = NO_OPTIMIZE;
          }
          else
          {
            if(predAnalysis == HAS_NOPREDICATE)
              analysisResult = ONESTEP_CHILDREN_NO_PREDICATE;
            else
              analysisResult = ONESTEP_CHILDREN;
          }
        }
        else
        {
          if ((analysisResult == ONESTEP_CHILDREN)
                  || (analysisResult == MULTISTEP_CHILDREN))
            analysisResult = MULTISTEP_CHILDREN;
          else
            return NO_OPTIMIZE;
        }
        break;
      case OpCodes.FROM_DESCENDANTS :
        return NO_OPTIMIZE;  // TODO
      case OpCodes.FROM_DESCENDANTS_OR_SELF :
        return NO_OPTIMIZE;  // TODO
      case OpCodes.FROM_FOLLOWING :
        return NO_OPTIMIZE;  // at least for now
      case OpCodes.FROM_FOLLOWING_SIBLINGS :
        return NO_OPTIMIZE;  // at least for now
      case OpCodes.FROM_PRECEDING :
        return NO_OPTIMIZE;  // at least for now
      case OpCodes.FROM_PRECEDING_SIBLINGS :
        return NO_OPTIMIZE;  // at least for now
      case OpCodes.FROM_PARENT :
        return NO_OPTIMIZE;  // at least for now
      case OpCodes.FROM_SELF :
        if (1 == stepCount)
          analysisResult = ONESTEP_SELF;
        else
          return NO_OPTIMIZE;  // at least for now
        break;
      case OpCodes.MATCH_ATTRIBUTE :
        return NO_OPTIMIZE;  // for now we shouldn't be dealing with match patterns here.
      case OpCodes.MATCH_ANY_ANCESTOR :
        return NO_OPTIMIZE;  // for now we shouldn't be dealing with match patterns here.
      case OpCodes.MATCH_IMMEDIATE_ANCESTOR :
        return NO_OPTIMIZE;  // for now we shouldn't be dealing with match patterns here.
      default :
        throw new RuntimeException("Programmer's assertion: unknown opcode: "
                                   + stepType);
      }

      stepOpCodePos = compiler.getNextStepPos(stepOpCodePos);

      if (stepOpCodePos < 0)
        break;
    }

    return analysisResult;
  }

  /** NEEDSDOC Field HAS_PREDICATE          */
  static final int HAS_PREDICATE = 1;

  /** NEEDSDOC Field HAS_NOPREDICATE          */
  static final int HAS_NOPREDICATE = 2;

  /** NEEDSDOC Field HAS_BOOLEANPREDICATE          */
  static final int HAS_BOOLEANPREDICATE = 3;

  /** NEEDSDOC Field MAYHAVE_INDEXPREDICATE          */
  static final int MAYHAVE_INDEXPREDICATE = 4;

  /**
   * NEEDSDOC Method analyzePredicate 
   *
   *
   * NEEDSDOC @param compiler
   * NEEDSDOC @param opPos
   * NEEDSDOC @param stepType
   *
   * NEEDSDOC (analyzePredicate) @return
   *
   * @throws javax.xml.transform.TransformerException
   */
  static int analyzePredicate(Compiler compiler, int opPos, int stepType)
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

    return (nPredicates > 0) ? HAS_PREDICATE : HAS_NOPREDICATE;
  }

  /**
   * Create the proper Walker from the axes type.
   *
   * NEEDSDOC @param compiler
   * NEEDSDOC @param opPos
   * NEEDSDOC @param lpi
   * NEEDSDOC @param analysis
   *
   * NEEDSDOC ($objectName$) @return
   */
  private static AxesWalker createDefaultWalker(Compiler compiler, int opPos,
          LocPathIterator lpi, int analysis)
  {

    AxesWalker ai;
    int stepType = compiler.getOp(opPos);
    boolean debug = false;

    /*
    System.out.println("0: "+compiler.getOp(opPos));
    System.out.println("1: "+compiler.getOp(opPos+1));
    System.out.println("2: "+compiler.getOp(opPos+2));
    System.out.println("3: "+compiler.getOp(opPos+3));
    System.out.println("4: "+compiler.getOp(opPos+4));
    System.out.println("5: "+compiler.getOp(opPos+5));
    */
    boolean simpleInit = false;

    switch (stepType)
    {
    case OpCodes.OP_VARIABLE :
    case OpCodes.OP_EXTFUNCTION :
    case OpCodes.OP_FUNCTION :
    case OpCodes.OP_GROUP :
      ai = new FilterExprWalker(lpi);
      simpleInit = true;
      break;
    case OpCodes.FROM_ROOT :
      ai = new RootWalker(lpi);
      break;
    case OpCodes.FROM_ANCESTORS :
      ai = new AncestorWalker(lpi);
      break;
    case OpCodes.FROM_ANCESTORS_OR_SELF :
      ai = new AncestorOrSelfWalker(lpi);
      break;
    case OpCodes.FROM_ATTRIBUTES :
      switch (analysis)
      {
      case ONESTEP_ATTR_NO_PREDICATES:
      case ONESTEP_ATTR :
        ai = new AttributeWalkerOneStep(lpi);
        break;
      default :
        ai = new AttributeWalker(lpi);
      }
      break;
    case OpCodes.FROM_NAMESPACE :
      ai = new NamespaceWalker(lpi);
      break;
    case OpCodes.FROM_CHILDREN :
      switch (analysis)
      {
      case ONESTEP_CHILDREN :
      {
        if (debug)
          System.out.println("analysis -- onestep child: " + analysis + ", "
                             + compiler.toString());

        ai = new ChildWalkerOneStep(lpi);
      }
      break;
      case MULTISTEP_CHILDREN :
        if (debug)
          System.out.println("analysis -- multi-step child: " + analysis
                             + ", " + compiler.toString());

        ai = new ChildWalkerMultiStep(lpi);
        break;
      default :
        ai = new ChildWalker(lpi);
      }
      break;
    case OpCodes.FROM_DESCENDANTS :
      ai = new DescendantWalker(lpi);
      break;
    case OpCodes.FROM_DESCENDANTS_OR_SELF :
      ai = new DescendantOrSelfWalker(lpi);
      break;
    case OpCodes.FROM_FOLLOWING :
      ai = new FollowingWalker(lpi);
      break;
    case OpCodes.FROM_FOLLOWING_SIBLINGS :
      ai = new FollowingSiblingWalker(lpi);
      break;
    case OpCodes.FROM_PRECEDING :
      ai = new PrecedingWalker(lpi);
      break;
    case OpCodes.FROM_PRECEDING_SIBLINGS :
      ai = new PrecedingSiblingWalker(lpi);
      break;
    case OpCodes.FROM_PARENT :
      ai = new ParentWalker(lpi);
      break;
    case OpCodes.FROM_SELF :
      switch (analysis)
      {
      case ONESTEP_SELF :
        ai = new SelfWalkerOneStep(lpi);
        break;
      default :
        ai = new SelfWalker(lpi);
      }
      break;
    case OpCodes.MATCH_ATTRIBUTE :
      ai = new AttributeWalker(lpi);
      break;
    case OpCodes.MATCH_ANY_ANCESTOR :
      ai = new ChildWalker(lpi);
      break;
    case OpCodes.MATCH_IMMEDIATE_ANCESTOR :
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
}
