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
 *     the documentation and/or other materials provided with the
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
package org.apache.xpath.compiler;

import java.util.Vector;
import org.apache.xalan.utils.QName;
import org.apache.xpath.patterns.NodeTest;

/**
 * This class represents the data structure basics of the XPath 
 * object.
 */
public class OpMap
{
  /**
   * The current pattern string, for diagnostics purposes
   */
  protected String m_currentPattern;
  
  /**
   * Get the pattern string.
   */
  public String getPatternString()
  {
    return m_currentPattern;
  }
  
  /**
   * The max size that the token queue can grow to.
   */
  static final int MAXTOKENQUEUESIZE = 500;

  /**
   *  TokenStack is the queue of used tokens. The current token is the token at the 
   * end of the m_tokenQueue. The idea is that the queue can be marked and a sequence
   * of tokens can be reused.
   */
  public Object[] m_tokenQueue = new Object[MAXTOKENQUEUESIZE];
  
  /**
   * <meta name="usage" content="advanced"/>
   * Get the XPath as a list of tokens.
   */
  public Object[] getTokenQueue()
  {
    return m_tokenQueue;
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Get the XPath as a list of tokens.
   */
  public Object getToken(int pos)
  {
    return m_tokenQueue[pos];
  }

  /**
   * The current size of the token queue.
   */
  public int m_tokenQueueSize = 0;

  /**
   * <meta name="usage" content="advanced"/>
   * Get size of the token queue.
   */
  public int getTokenQueueSize()
  {
    return m_tokenQueueSize;
  }

  /**
   * An operations map is used instead of a proper parse tree.  It contains 
   * operations codes and indexes into the m_tokenQueue.
   * I use an array instead of a full parse tree in order to cut down 
   * on the number of objects created.
   */
  public int m_opMap[] = null;
  
  /**
   * <meta name="usage" content="advanced"/>
   * Get the opcode list that describes the XPath operations.  It contains 
   * operations codes and indexes into the m_tokenQueue.
   * I use an array instead of a full parse tree in order to cut down 
   * on the number of objects created.
   */
  public int[] getOpMap()
  {
    return m_opMap;
  }
  
  // Position indexes
  
  /**
   * <meta name="usage" content="advanced"/>
   * The length is always the opcode position + 1.
   * Length is always expressed as the opcode+length bytes, 
   * so it is always 2 or greater.
   */
  public static final int MAPINDEX_LENGTH = 1;
  
  /**
   * Replace the large arrays 
   * with a small array.
   */
  void shrink()
  {
    int map[] = m_opMap;
    int n = m_opMap[MAPINDEX_LENGTH];;
    m_opMap = new int[n+4];
    int i;
    for(i = 0; i < n; i++)
    {
      m_opMap[i] = map[i];
    }
    m_opMap[i] = 0;
    m_opMap[i+1] = 0;
    m_opMap[i+2] = 0;
        
    Object[] tokens = m_tokenQueue;
    n = m_tokenQueueSize;
    m_tokenQueue = new Object[n+4];
    for(i = 0; i < n; i++)
    {
      m_tokenQueue[i] = tokens[i];
    }
    m_tokenQueue[i] = null;
    m_tokenQueue[i+1] = null;
    m_tokenQueue[i+2] = null;
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Given an operation position, return the current op.
   * @return position of next operation in m_opMap.
   */
  public int getOp(int opPos)
  {
    return m_opMap[opPos];
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Given an operation position, return the end position, i.e. the 
   * beginning of the next operation.
   * @return position of next operation in m_opMap.
   */
  public int getNextOpPos(int opPos)
  {
    return opPos+m_opMap[opPos+1];
  }
  
  /**
   * <meta name="usage" content="advanced"/>
   * Given an operation position, return the end position, i.e. the 
   * beginning of the next operation.
   * @return position of next operation in m_opMap.
   */
  public int getNextStepPos(int opPos)
  {
    int stepType = getOp(opPos);
    if((stepType >= OpCodes.AXES_START_TYPES) && (stepType <= OpCodes.AXES_END_TYPES))
    {
      return getNextOpPos(opPos);
    }
    else if((stepType >= OpCodes.FIRST_NODESET_OP) && (stepType <= OpCodes.LAST_NODESET_OP))
    {
      int newOpPos = getNextOpPos(opPos);
      
      while(OpCodes.OP_PREDICATE == getOp(newOpPos))
        newOpPos = getNextOpPos(newOpPos);

      stepType = getOp(newOpPos);
      if(!((stepType >= OpCodes.AXES_START_TYPES) && (stepType <= OpCodes.AXES_END_TYPES)))
      {
        return OpCodes.ENDOP;
      }
      return newOpPos;
    }
    else
    {
      throw new RuntimeException("Programmer's assertion in getNextStepPos: unknown stepType: "+stepType);
    }
  }

  
  /**
   * <meta name="usage" content="advanced"/>
   * Given an operation position, return the end position, i.e. the 
   * beginning of the next operation.
   * @return position of next operation in m_opMap.
   */
  public static int getNextOpPos(int[] opMap, int opPos)
  {
    return opPos+opMap[opPos+1];
  }
  
  /**
   * <meta name="usage" content="advanced"/>
   * Given an FROM_stepType position, return the position of the 
   * first predicate, if there is one, or else this will point 
   * to the end of the FROM_stepType.
   * Example:
   *  int posOfPredicate = xpath.getNextOpPos(stepPos);
   *  boolean hasPredicates = 
   *            OpCodes.OP_PREDICATE == xpath.getOp(posOfPredicate);
   * @return position of predicate in FROM_stepType structure.
   */
  public int getFirstPredicateOpPos(int opPos)
  {
    int stepType = m_opMap[opPos];
    if((stepType >= OpCodes.AXES_START_TYPES) && (stepType <= OpCodes.AXES_END_TYPES))
    {
      return opPos+m_opMap[opPos+2];
    }
    else if((stepType >= OpCodes.FIRST_NODESET_OP) && (stepType <= OpCodes.LAST_NODESET_OP))
    {
      return opPos+m_opMap[opPos+1];
    }
    else
    {
      throw new RuntimeException("Programmer's assertion in getNextStepPos: unknown stepType: "+stepType);
    }
  }

  
  /**
   * <meta name="usage" content="advanced"/>
   * Go to the first child of a given operation.
   */
  public static int getFirstChildPos(int opPos)
  {
    return opPos+2;
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Go to the first child of a given operation.
   */
  public int getArgLength(int opPos)
  {
    return m_opMap[opPos+MAPINDEX_LENGTH];
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Go to the first child of a given operation.
   */
  public int getArgLengthOfStep(int opPos)
  {
    return m_opMap[opPos+MAPINDEX_LENGTH+1]-3;
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Go to the first child of a given operation.
   */
  public static int getFirstChildPosOfStep(int opPos)
  {
    return opPos+3;
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Get the test type of the step, i.e. NODETYPE_XXX value.
   * @param opPosOfStep The position of the FROM_XXX step. 
   */
  public int getStepTestType(int opPosOfStep)
  {
    return m_opMap[opPosOfStep+3]; // skip past op, len, len without predicates
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Get the namespace of the step.
   * @param opPosOfStep The position of the FROM_XXX step. 
   */
  public String getStepNS(int opPosOfStep)
  {
    int argLenOfStep = getArgLengthOfStep(opPosOfStep);
    // System.out.println("getStepNS.argLenOfStep: "+argLenOfStep);
    if(argLenOfStep == 3)
    {
      int index = m_opMap[opPosOfStep+4];
      if(index >= 0)
        return (String)m_tokenQueue[index];
      else if(OpCodes.ELEMWILDCARD == index)
        return NodeTest.WILD;
      else
        return null;
    }
    else
      return null;
  }
  
  /**
   * <meta name="usage" content="advanced"/>
   * Get the local name of the step.
   * @param opPosOfStep The position of the FROM_XXX step. 
   */
  public String getStepLocalName(int opPosOfStep)
  {
    int argLenOfStep = getArgLengthOfStep(opPosOfStep);
    // System.out.println("getStepLocalName.argLenOfStep: "+argLenOfStep);
    int index = (argLenOfStep == 3) ? m_opMap[opPosOfStep+5] 
                                      : ((argLenOfStep == 1) ? -3 : -2);
    if(index >= 0)
      return (String)m_tokenQueue[index];
    else if(OpCodes.ELEMWILDCARD == index)
      return NodeTest.WILD;
    else
      return null;
  }

  /**
   * <meta name="usage" content="advanced"/>
   * This method is for building indexes of match patterns for fast lookup.
   * This allows a caller to get the QName, and quickly 
   * find the likely candidates that may match.  Note that this will 
   * produce QName objects that aren't strictly legal, like "*".
   */
  public Vector getTargetElementQNames()
  {
    Vector targetQNames = new Vector();

    int opPos = 2;

    while(m_opMap[opPos] == OpCodes.OP_LOCATIONPATHPATTERN)
    {
      int nextOpPos = getNextOpPos(opPos);
      opPos = getFirstChildPos(opPos);
      
      while( m_opMap[opPos] != OpCodes.ENDOP )
      {
        int nextStepPos = getNextOpPos(opPos);
        int nextOp = m_opMap[nextStepPos];
        if((nextOp == OpCodes.OP_PREDICATE) || (nextOp == OpCodes.ENDOP))
        {
          int stepType = m_opMap[opPos];
          opPos += 3;
          switch(stepType)
          {
          case OpCodes.OP_FUNCTION:
            targetQNames.addElement(new QName(PsuedoNames.PSEUDONAME_ANY));
            break;
          case OpCodes.FROM_ROOT:
            targetQNames.addElement(new QName(PsuedoNames.PSEUDONAME_ROOT));
            break;
          case OpCodes.MATCH_ATTRIBUTE:
          case OpCodes.MATCH_ANY_ANCESTOR:
          case OpCodes.MATCH_IMMEDIATE_ANCESTOR:
            int tok = m_opMap[opPos];
            opPos++;
            switch(tok)
            {
            case OpCodes.NODETYPE_COMMENT:
              targetQNames.addElement(new QName(PsuedoNames.PSEUDONAME_COMMENT));
              break;
            case OpCodes.NODETYPE_TEXT:
              targetQNames.addElement(new QName(PsuedoNames.PSEUDONAME_TEXT));
              break;
            case OpCodes.NODETYPE_NODE:
              targetQNames.addElement(new QName(PsuedoNames.PSEUDONAME_ANY));
              break;
            case OpCodes.NODETYPE_ROOT:
              targetQNames.addElement(new QName(PsuedoNames.PSEUDONAME_ROOT));
              break;
            case OpCodes.NODETYPE_ANYELEMENT:
              targetQNames.addElement(new QName(PsuedoNames.PSEUDONAME_ANY));
              break;
            case OpCodes.NODETYPE_PI:
              targetQNames.addElement(new QName(PsuedoNames.PSEUDONAME_ANY));
              break;
            case OpCodes.NODENAME:
              int tokenIndex = m_opMap[opPos+1];
              String namespace = (tokenIndex >= 0) ? 
                                 (String)m_tokenQueue[tokenIndex] : null;
              tokenIndex = m_opMap[opPos+1];
              if(tokenIndex >= 0)
              {
                String targetName = (String)m_tokenQueue[tokenIndex];
                if(targetName.equals("*"))
                {
                  targetQNames.addElement(new QName(namespace, PsuedoNames.PSEUDONAME_ANY));
                }
                else
                {
                  targetQNames.addElement(new QName(namespace, targetName));
                }
              }
              else
              {
                // ?? -sboag
                targetQNames.addElement(new QName(namespace, PsuedoNames.PSEUDONAME_ANY));
              }
              break;
            default:
              targetQNames.addElement(new QName(PsuedoNames.PSEUDONAME_ANY));
              break;
            }
            break;
          }
        }
        opPos = nextStepPos;
      }
      
      opPos = nextOpPos;
    }
    return targetQNames;
  }

}
