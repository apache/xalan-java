package org.apache.xpath.compiler;

import org.apache.xpath.operations.And;
import org.apache.xpath.operations.Bool;
import org.apache.xpath.operations.Div;
import org.apache.xpath.operations.Equals;
import org.apache.xpath.operations.Gt;
import org.apache.xpath.operations.Gte;
import org.apache.xpath.operations.Lt;
import org.apache.xpath.operations.Lte;
import org.apache.xpath.operations.Minus;
import org.apache.xpath.operations.Mod;
import org.apache.xpath.operations.Mult;
import org.apache.xpath.operations.Neg;
import org.apache.xpath.operations.NotEquals;
import org.apache.xpath.operations.Operation;
import org.apache.xpath.operations.Or;
import org.apache.xpath.operations.Plus;
import org.apache.xpath.operations.Quo;
import org.apache.xpath.operations.UnaryOperation;
import org.apache.xpath.operations.Variable;

import org.apache.xpath.objects.*;
import org.apache.xpath.axes.*;
import org.apache.xpath.patterns.*;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.FuncExtFunction;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.*;
import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xalan.res.XSLMessages;

import org.apache.xalan.utils.QName;
import org.apache.xalan.utils.PrefixResolver;

import trax.ProcessorException;

import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import org.w3c.dom.traversal.NodeFilter;

public class Compiler extends OpMap
{  
  public Compiler(ErrorHandler errorHandler,
                  Locator locator)
  {
    m_errorHandler = errorHandler;
    m_locator = locator;
  }

  public Compiler()
  {
    m_errorHandler = null;
    m_locator = null;
  }
  
  /**
   * <meta name="usage" content="advanced"/>
   * Execute the XPath object from a given opcode position.
   * @param xctxt The execution context.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param callback Interface that implements the processLocatedNode method.
   * @param callbackInfo Object that will be passed to the processLocatedNode method.
   * @return The result of the XPath.
   */
  public Expression compile(int opPos)
    throws org.xml.sax.SAXException
  {
    int op = m_opMap[opPos];
    // System.out.println(getPatternString()+"op: "+op);
    switch(op)
    {
    case OpCodes.OP_XPATH: return compile(opPos+2);
    case OpCodes.OP_OR: return or(opPos);
    case OpCodes.OP_AND: return and(opPos);
    case OpCodes.OP_NOTEQUALS: return notequals(opPos);
    case OpCodes.OP_EQUALS: return equals(opPos);
    case OpCodes.OP_LTE: return lte(opPos);
    case OpCodes.OP_LT: return lt(opPos);
    case OpCodes.OP_GTE: return gte(opPos);
    case OpCodes.OP_GT: return gt(opPos);
    case OpCodes.OP_PLUS: return plus(opPos);
    case OpCodes.OP_MINUS: return minus(opPos);
    case OpCodes.OP_MULT: return mult(opPos);
    case OpCodes.OP_DIV: return div(opPos);
    case OpCodes.OP_MOD: return mod(opPos);
    case OpCodes.OP_QUO: return quo(opPos);
    case OpCodes.OP_NEG: return neg(opPos);
    case OpCodes.OP_STRING: return string(opPos);
    case OpCodes.OP_BOOL: return bool(opPos);
    case OpCodes.OP_NUMBER: return number(opPos);
    case OpCodes.OP_UNION: return union(opPos);
    case OpCodes.OP_LITERAL: return literal(opPos);
    case OpCodes.OP_VARIABLE: return variable(opPos);
    case OpCodes.OP_GROUP: return group(opPos);
    case OpCodes.OP_NUMBERLIT: return numberlit(opPos);
    case OpCodes.OP_ARGUMENT: return arg(opPos);
    case OpCodes.OP_EXTFUNCTION: return compileExtension(opPos);
    case OpCodes.OP_FUNCTION: return compileFunction(opPos);
    case OpCodes.OP_LOCATIONPATH: return locationPath(opPos);
    case OpCodes.OP_PREDICATE: return null; // should never hit this here.
    case OpCodes.OP_MATCHPATTERN: return matchPattern(opPos+2);
    case OpCodes.OP_LOCATIONPATHPATTERN: return locationPathPattern(opPos);
    default:  error(XPATHErrorResources.ER_UNKNOWN_OPCODE, new Object[] {Integer.toString(m_opMap[opPos])}); //"ERROR! Unknown op code: "+m_opMap[opPos]);
    }
    return null;
  }
  
  /**
   * Bottle-neck compilation of an operation.
   */
  private Expression compileOperation(Operation operation, int opPos)
    throws SAXException
  {
    int leftPos = getFirstChildPos(opPos);
    int rightPos = getNextOpPos(leftPos);
    operation.setLeftRight(compile(leftPos), compile(rightPos));
    return operation;
  }
  
  /**
   * Bottle-neck compilation of an operation.
   */
  private Expression compileUnary(UnaryOperation unary, int opPos)
    throws SAXException
  {
    int rightPos = getFirstChildPos(opPos);
    unary.setRight(compile(rightPos));
    return unary;
  }
  
  /**
   * OR two expressions and return the boolean result.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns XBoolean set to true if the one of the two arguments are true.
   */
  protected Expression or(int opPos) 
    throws org.xml.sax.SAXException
  {
    return compileOperation(new Or(), opPos);
  }

  /**
   * AND two expressions and return the boolean result.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns XBoolean set to true if the two arguments are both true.
   */
  protected Expression and(int opPos) 
    throws org.xml.sax.SAXException
  {
    return compileOperation(new And(), opPos);
  }

  /**
   * Tell if two expressions are functionally not equal.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns XBoolean set to true if the two arguments are not equal.
   */
  protected Expression notequals(int opPos) 
    throws org.xml.sax.SAXException
  {
    return compileOperation(new NotEquals(), opPos);
  }


  /**
   * Tell if two expressions are functionally equal.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns XBoolean set to true if the two arguments are equal.
   */
  protected Expression equals(int opPos) 
    throws org.xml.sax.SAXException
  {
    return compileOperation(new Equals(), opPos);
  }
    
  /**
   * Tell if one argument is less than or equal to the other argument.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns XBoolean set to true if arg 1 is less than or equal to arg 2.
   */
  protected Expression lte(int opPos) 
    throws org.xml.sax.SAXException
  {
    return compileOperation(new Lte(), opPos);
  }

  /**
   * Tell if one argument is less than the other argument.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns XBoolean set to true if arg 1 is less than arg 2.
   */
  protected Expression lt(int opPos) 
    throws org.xml.sax.SAXException
  {
    return compileOperation(new Lt(), opPos);
  }

  /**
   * Tell if one argument is greater than or equal to the other argument.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns XBoolean set to true if arg 1 is greater than or equal to arg 2.
   */
  protected Expression gte(int opPos) 
    throws org.xml.sax.SAXException
  {
    return compileOperation(new Gte(), opPos);
  }


  /**
   * Tell if one argument is greater than the other argument.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns XBoolean set to true if arg 1 is greater than arg 2.
   */
  protected Expression gt(int opPos) 
    throws org.xml.sax.SAXException
  {
    return compileOperation(new Gt(), opPos);
  }

  /**
   * Give the sum of two arguments.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns sum of arg1 and arg2.
   */
  protected Expression plus(int opPos) 
    throws org.xml.sax.SAXException
  {
    return compileOperation(new Plus(), opPos);
  }

  /**
   * Give the difference of two arguments.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns difference of arg1 and arg2.
   */
  protected Expression minus(int opPos) 
    throws org.xml.sax.SAXException
  {
    return compileOperation(new Minus(), opPos);
  }

  /**
   * Multiply two arguments.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns arg1 * arg2.
   */
  protected Expression mult(int opPos) 
    throws org.xml.sax.SAXException
  {
    return compileOperation(new Mult(), opPos);
  }

  /**
   * Divide a number.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns arg1 / arg2.
   */
  protected Expression div(int opPos) 
    throws org.xml.sax.SAXException
  {
    return compileOperation(new Div(), opPos);
  }

  /**
   * Return the remainder from a truncating division.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns arg1 mod arg2.
   */
  protected Expression mod(int opPos) 
    throws org.xml.sax.SAXException
  {
    return compileOperation(new Mod(), opPos);
  }

  /**
   * Return the remainder from a truncating division.
   * (Quo is no longer supported by xpath).
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns arg1 mod arg2.
   */
  protected Expression quo(int opPos) 
    throws org.xml.sax.SAXException
  {
    return compileOperation(new Quo(), opPos);
  }

  /**
   * Return the negation of a number.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns -arg.
   */
  protected Expression neg(int opPos) 
    throws org.xml.sax.SAXException
  {
    return compileUnary(new Neg(), opPos);
  }

  /**
   * Cast an expression to a string.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns arg cast to a string.
   */
  protected Expression string(int opPos) 
    throws org.xml.sax.SAXException
  {
    return compileUnary(new org.apache.xpath.operations.String(), opPos);
  }

  /**
   * Cast an expression to a boolean.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns arg cast to a boolean.
   */
  protected Expression bool(int opPos) 
    throws org.xml.sax.SAXException
  {
    return compileUnary(new org.apache.xpath.operations.Bool(), opPos);
  }
 
  /**
   * Cast an expression to a number.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns arg cast to a number.
   */
  protected Expression number(int opPos) 
    throws org.xml.sax.SAXException
  {
    return compileUnary(new org.apache.xpath.operations.Number(), opPos);
  }
  
  /**
   * Get a literal value.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns an XString object.
   */
  protected Expression literal(int opPos) 
  {
    opPos = getFirstChildPos(opPos);
    
    return (XString)m_tokenQueue[m_opMap[opPos]];
  }
  
  /**
   * Get a literal value.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns an XString object.
   */
  protected Expression numberlit(int opPos) 
  {
    opPos = getFirstChildPos(opPos);
    return (XNumber)m_tokenQueue[m_opMap[opPos]];
  }
  
  /**
   * Get a literal value.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns an XObject object.
   */
  protected Expression variable(int opPos) 
    throws org.xml.sax.SAXException
  {
    Variable var = new Variable();
    opPos = getFirstChildPos(opPos);
    java.lang.String varName = (java.lang.String)m_tokenQueue[m_opMap[opPos]];
    QName qname = new QName(varName, getNamespaceContext());
    var.setQName(qname);
    return var;
  }


  /**
   * Execute an expression as a group.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns arg.
   */
  protected Expression group(int opPos) 
    throws org.xml.sax.SAXException
  {    
    // no-op
    return compile(opPos+2);
  }
  
  /**
   * Execute a function argument.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns the result of the argument expression.
   */
  protected Expression arg(int opPos) 
    throws org.xml.sax.SAXException
  {    
    // no-op
    return compile(opPos+2);
  }
  
  /**
   * Computes the union of its operands which must be node-sets.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @param callback Interface that implements the processLocatedNode method.
   * @param callbackInfo Object that will be passed to the processLocatedNode method.
   * @returns the union of node-set operands.
   */
  protected Expression union(int opPos) 
    throws org.xml.sax.SAXException
  {
    
    return new UnionPathIterator(this, opPos);
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Execute a location path.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @param callback Interface that implements the processLocatedNode method.
   * @param callbackInfo Object that will be passed to the processLocatedNode method.
   * @returns a node-set.
   */
  public Expression locationPath(int opPos) 
    throws org.xml.sax.SAXException
  {    
    return new LocPathIterator(this, opPos);
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Evaluate a predicate.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns either a boolean or a number.
   */
  public Expression predicate(int opPos) 
    throws org.xml.sax.SAXException
  {
    return compile(opPos+2);
  }
  
  /**
   * Computes the union of its operands which must be node-sets.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns the match score in the form of an XObject.
   */
  protected Expression matchPattern(int opPos) 
    throws org.xml.sax.SAXException
  {
    // First, count...
    int nextOpPos = opPos;
    int i;
    for( i = 0; m_opMap[nextOpPos] == OpCodes.OP_LOCATIONPATHPATTERN; i++)
      nextOpPos = getNextOpPos(nextOpPos);
    
    if( i == 1 )
      return compile(opPos);

    UnionPattern up = new UnionPattern();
    StepPattern[] patterns =  new StepPattern[i];

    for(i = 0; m_opMap[opPos] == OpCodes.OP_LOCATIONPATHPATTERN; i++)
    {
      nextOpPos = getNextOpPos(opPos);
      patterns[i] = (StepPattern)compile(opPos);
      opPos =  nextOpPos;
    }
    up.setPatterns(patterns);
    
    return up;
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
  public Expression locationPathPattern(int opPos) 
    throws org.xml.sax.SAXException
  {    
    opPos = getFirstChildPos(opPos);
    return stepPattern(opPos, 0, null);
  }
    
  public int getWhatToShow(int opPos)
  {
    int axesType = getOp(opPos);
    int testType = getOp(opPos+3);
    // System.out.println("testType: "+testType);
    switch(testType)
    {
    case OpCodes.NODETYPE_COMMENT:
      return NodeFilter.SHOW_COMMENT;

    case OpCodes.NODETYPE_TEXT:
      return NodeFilter.SHOW_TEXT | NodeFilter.SHOW_COMMENT;

    case OpCodes.NODETYPE_PI:
      return NodeFilter.SHOW_PROCESSING_INSTRUCTION;

    case OpCodes.NODETYPE_NODE:
      return NodeFilter.SHOW_ALL;
      
    case OpCodes.NODETYPE_ROOT:
      return NodeFilter.SHOW_DOCUMENT;
      
    case OpCodes.NODETYPE_FUNCTEST:
      return NodeTest.SHOW_BYFUNCTION;
      
    case OpCodes.NODENAME:
      switch(axesType)
      {
      case OpCodes.FROM_NAMESPACE: 
        return NodeFilter.SHOW_ATTRIBUTE | NodeTest.SHOW_NAMESPACE;
        
      case OpCodes.FROM_ATTRIBUTES: 
      case OpCodes.MATCH_ATTRIBUTE:
        return NodeFilter.SHOW_ATTRIBUTE;
        // break;

      case OpCodes.MATCH_ANY_ANCESTOR:
      case OpCodes.MATCH_IMMEDIATE_ANCESTOR:
        return NodeFilter.SHOW_ELEMENT;
        // break;
        
      default:
        return NodeFilter.SHOW_ELEMENT;
      }

    default:
      return NodeFilter.SHOW_ALL;
    }
  }

  /**
   * Execute a step in a location path.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @returns the last matched context node.
   */
  protected StepPattern stepPattern(int opPos, int stepCount, StepPattern ancestorPattern) 
    throws org.xml.sax.SAXException
  {    
    int startOpPos = opPos;
    int stepType = getOpMap()[opPos];
    if(OpCodes.ENDOP == stepType)
      return null;
    
    int endStep = getNextOpPos(opPos);
    // int nextStepType = getOpMap()[endStep];
    
    StepPattern pattern;

    // boolean isSimple = ((OpCodes.ENDOP == nextStepType) && (stepCount == 0));
    
    int argLen;

    switch(stepType)
    {
    case OpCodes.OP_FUNCTION:
      argLen = m_opMap[opPos+OpMap.MAPINDEX_LENGTH];
      pattern = new FunctionPattern(compileFunction(opPos));
      break;
      
    case OpCodes.FROM_ROOT:
      argLen = getArgLengthOfStep(opPos);
      opPos = getFirstChildPosOfStep(opPos);
      pattern = new StepPattern(NodeFilter.SHOW_DOCUMENT);
      break;
      
    case OpCodes.MATCH_ATTRIBUTE:
      argLen = getArgLengthOfStep(opPos);
      opPos = getFirstChildPosOfStep(opPos);
      pattern = new StepPattern(NodeFilter.SHOW_ATTRIBUTE, 
                                getStepNS(startOpPos), 
                                getStepLocalName(startOpPos));
      break;

    case OpCodes.MATCH_ANY_ANCESTOR:
      argLen = getArgLengthOfStep(opPos);
      opPos = getFirstChildPosOfStep(opPos);
      pattern = new AncestorStepPattern(getWhatToShow(startOpPos), 
                                getStepNS(startOpPos), 
                                getStepLocalName(startOpPos));
      break;
      
    case OpCodes.MATCH_IMMEDIATE_ANCESTOR:
      argLen = getArgLengthOfStep(opPos);
      opPos = getFirstChildPosOfStep(opPos);
      pattern = new StepPattern(getWhatToShow(startOpPos), 
                                getStepNS(startOpPos), 
                                getStepLocalName(startOpPos));
      break;
      
    default:
      error(XPATHErrorResources.ER_UNKNOWN_MATCH_OPERATION, null); //"unknown match operation!");
      return null;
    }
    
    pattern.setPredicates(getCompiledPredicates(opPos + argLen));
    pattern.setRelativePathPattern(ancestorPattern);
    
    StepPattern relativePathPattern = stepPattern(endStep, stepCount+1, pattern);
        
    return (null != relativePathPattern) ? relativePathPattern : pattern;
  }
  
  public Expression[] getCompiledPredicates(int opPos)
    throws org.xml.sax.SAXException
  {
    int count = countPredicates(opPos);
    if(count > 0)
    {
      Expression[] predicates = new Expression[count];
      compilePredicates(opPos, predicates);
      return predicates;
    }
    return null;
  }
  
  /**
   * Count the number of predicates in the step.
   */
  private int countPredicates(int opPos)
    throws org.xml.sax.SAXException
  {
    int count = 0;
    while(OpCodes.OP_PREDICATE == getOp(opPos))
    {
      count++;
      opPos = getNextOpPos(opPos);
    }
    return count;
  }
  
  /**
   * Allocate predicates in the step.
   */
  private void compilePredicates(int opPos, Expression[] predicates)
    throws org.xml.sax.SAXException
  {
    for(int i = 0; OpCodes.OP_PREDICATE == getOp(opPos); i++)
    {
      predicates[i] = predicate(opPos);
      opPos = getNextOpPos(opPos);
    }
  }

  /**
   * Execute a function from an op code.
   */
  Expression compileFunction(int opPos)
    throws org.xml.sax.SAXException
  {
    int endFunc = opPos+m_opMap[opPos+1]-1;
    opPos = getFirstChildPos(opPos);
    int funcID = m_opMap[opPos];
    opPos++;
    if(-1 != funcID)
    {
      Function func = FunctionTable.getFunction(funcID);

      try
      {
        int i = 0;
        for(int p = opPos; p < endFunc; p = getNextOpPos(p), i++)
        {
          // System.out.println("argPos: "+ p);
          // System.out.println("argCode: "+ m_opMap[p]);
          func.setArg(compile(p), i);
        }
        func.checkNumberArgs(i);
      }
      catch(WrongNumberArgsException wnae)
      {
        java.lang.String name = FunctionTable.m_functions[funcID].getName();
        throw new SAXException(name+" only allows "+wnae.m_argsExpected+" arguments");
      }
      
      return func;
    }
    else
    {
      warn(XPATHErrorResources.WG_FUNCTION_TOKEN_NOT_FOUND, null); //"function token not found.");
      return null;
    }
  }
  
  /**
   * Execute an extension function from an op code.
   */
  private Expression compileExtension(int opPos)
    throws org.xml.sax.SAXException
  {
    int endExtFunc = opPos+m_opMap[opPos+1]-1;
    opPos = getFirstChildPos(opPos);
    java.lang.String ns = (java.lang.String)m_tokenQueue[m_opMap[opPos]];
    opPos++;
    java.lang.String funcName = (java.lang.String)m_tokenQueue[m_opMap[opPos]];
    opPos++;
    Function extension = new FuncExtFunction(ns, funcName, 
                            // Create a method key, for faster lookup.
                            String.valueOf(opPos)+String.valueOf(hashCode()));
    
    try
    {
      int i = 0;
      while(opPos < endExtFunc)
      {
        int nextOpPos = getNextOpPos(opPos);
        extension.setArg(this.compile(opPos), i);
        opPos = nextOpPos;
        i++;
      }
    }
    catch(WrongNumberArgsException wnae)
    {
      ; // should never happen
    }
    return extension;
  }

  
  /**
   * Warn the user of an problem.
   */
  public void warn(int msg, Object[] args)
    throws org.xml.sax.SAXException
  {
    java.lang.String fmsg = XSLMessages.createXPATHWarning(msg, args); 
    
    if(null != m_errorHandler)
    {
      // TO DO: Need to get stylesheet Locator from here.
      m_errorHandler.warning(new ProcessorException(fmsg));
    }
  }

  /**
   * Tell the user of an assertion error, and probably throw an 
   * exception.
   */
  public void assert(boolean b, java.lang.String msg)
    throws org.xml.sax.SAXException
  {
    if(!b)
    {
      java.lang.String fMsg = XSLMessages.createXPATHMessage(XPATHErrorResources.ER_INCORRECT_PROGRAMMER_ASSERTION, new Object[] {msg}); 
      throw new RuntimeException(fMsg);
    }
  }

  /**
   * Tell the user of an error, and probably throw an 
   * exception.
   */
  public void error(int msg, Object[] args)
    throws org.xml.sax.SAXException
  {
    java.lang.String fmsg = XSLMessages.createXPATHMessage(msg, args); 
    
    ProcessorException te = new ProcessorException(fmsg, 
                                                   m_locator);
    
    if(null != m_errorHandler)
      m_errorHandler.fatalError(te);
    else
    {
      // System.out.println(te.getMessage()
      //                    +"; file "+te.getSystemId()
      //                    +"; line "+te.getLineNumber()
      //                    +"; column "+te.getColumnNumber());
      throw te;
    }
  }
  
  /**
   * The current prefixResolver for the execution context.
   */
  private PrefixResolver m_currentPrefixResolver = null;

  
  /**
   * Get the current namespace context for the xpath.
   */
  public PrefixResolver getNamespaceContext()
  {
    return m_currentPrefixResolver;
  }

  /**
   * Set the current namespace context for the xpath.
   */
  public void setNamespaceContext(PrefixResolver pr)
  {
    m_currentPrefixResolver = pr;
  } 

  ErrorHandler m_errorHandler;
  Locator m_locator;
  
  
}
