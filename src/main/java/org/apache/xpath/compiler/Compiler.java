/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id$
 */
package org.apache.xpath.compiler;

import java.util.List;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLMessages;
import org.apache.xml.dtm.Axis;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.DTMFilter;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.SAXSourceLocator;
import org.apache.xpath.Expression;
import org.apache.xpath.axes.UnionPathIterator;
import org.apache.xpath.axes.WalkerFactory;
import org.apache.xpath.compiler.XPathParser.XPathArrayConsFuncArgs;
import org.apache.xpath.compiler.XPathParser.XPathSequenceConsFuncArgs;
import org.apache.xpath.composite.XPathArrayConstructor;
import org.apache.xpath.composite.XPathForExpr;
import org.apache.xpath.composite.XPathSequenceConstructor;
import org.apache.xpath.functions.FuncExtFunctionAvailable;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.functions.XPathDynamicFunctionCall;
import org.apache.xpath.functions.XSL3ConstructorOrExtensionFunction;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XString;
import org.apache.xpath.operations.And;
import org.apache.xpath.operations.ArrowOp;
import org.apache.xpath.operations.CastAs;
import org.apache.xpath.operations.CastableAs;
import org.apache.xpath.operations.Div;
import org.apache.xpath.operations.Equals;
import org.apache.xpath.operations.Except;
import org.apache.xpath.operations.Gt;
import org.apache.xpath.operations.Gte;
import org.apache.xpath.operations.IDiv;
import org.apache.xpath.operations.InstanceOf;
import org.apache.xpath.operations.Intersect;
import org.apache.xpath.operations.Lt;
import org.apache.xpath.operations.Lte;
import org.apache.xpath.operations.Minus;
import org.apache.xpath.operations.Mod;
import org.apache.xpath.operations.Mult;
import org.apache.xpath.operations.Neg;
import org.apache.xpath.operations.NodeComparisonFollows;
import org.apache.xpath.operations.NodeComparisonIs;
import org.apache.xpath.operations.NodeComparisonPrecede;
import org.apache.xpath.operations.NotEquals;
import org.apache.xpath.operations.Operation;
import org.apache.xpath.operations.Or;
import org.apache.xpath.operations.Plus;
import org.apache.xpath.operations.Range;
import org.apache.xpath.operations.SimpleMapOperator;
import org.apache.xpath.operations.StrConcat;
import org.apache.xpath.operations.TreatAs;
import org.apache.xpath.operations.UnaryOperation;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.operations.VcEquals;
import org.apache.xpath.operations.VcGe;
import org.apache.xpath.operations.VcGt;
import org.apache.xpath.operations.VcLe;
import org.apache.xpath.operations.VcLt;
import org.apache.xpath.operations.VcNotEquals;
import org.apache.xpath.patterns.FunctionPattern;
import org.apache.xpath.patterns.NodeTest;
import org.apache.xpath.patterns.StepPattern;
import org.apache.xpath.patterns.UnionPattern;
import org.apache.xpath.res.XPATHErrorResources;

/**
 * An instance of this class compiles an XPath string expression into 
 * a Expression object.  This class compiles the string into a sequence 
 * of operation codes (op map) and then builds from that into an Expression 
 * tree.
 * 
 * @author Scott Boag <scott_boag@us.ibm.com>
 * @author Myriam Midy <mmidy@apache.org>
 * @author Gary L Peskin <garyp@apache.org>, Ilene Seelemann <ilene@apache.org>,
 *         Henry Zongaro <zongaro@ca.ibm.com>, Morris Kwan <mkwan@apache.org>,
 *         Brian James Minchau <minchau@apache.org>, Santiago Pericas-Geertsen <santiagopg@apache.org>,
 *         Christine Li <jycli@apache.org>
 *         
 * @author Mukul Gandhi <mukulg@apache.org>
 *         (XPath 3 specific changes, to this class)         
 * 
 * @xsl.usage advanced
 */
public class Compiler extends OpMap
{

  /**
   * Construct a Compiler object with a specific ErrorListener and 
   * SourceLocator where the expression is located.
   *
   * @param errorHandler Error listener where messages will be sent, or null 
   *                     if messages should be sent to System err.
   * @param locator      The location object where the expression lives, which 
   *                     may be null, but which, if not null, must be valid over 
   *                     the long haul, in other words, it will not be cloned.
   * @param funcTable    The FunctionTable object where the xpath build-in 
   *                     functions are stored.
   */
  public Compiler(ErrorListener errorHandler, SourceLocator locator, FunctionTable funcTable)
  {
    m_errorHandler = errorHandler;
    m_locator = locator;
    m_functionTable = funcTable;
  }

  /**
   * Construct a Compiler instance that has a null error listener and a 
   * null source locator.
   */
  public Compiler()
  {
    m_errorHandler = null;
    m_locator = null;
  }

  /**
   * Execute the XPath object from a given opcode position.
   * @param opPos The current position in the xpath.m_opMap array.
   * @return The result of the XPath.
   *
   * @throws TransformerException if there is a syntax or other error.
   * @xsl.usage advanced
   */
  public Expression compile(int opPos) throws TransformerException
  {

    int op = getOp(opPos);

    Expression expr = null;

    switch (op)
    {
    case OpCodes.OP_XPATH :
      expr = compile(opPos + 2); break;
    case OpCodes.OP_FOR_EXPR :
      expr = forExpr(opPos); break;
    case OpCodes.OP_LET_EXPR :
      expr = letExpr(opPos); break;
    case OpCodes.OP_QUANTIFIED_EXPR :
      expr = quantifiedExpr(opPos); break;
    case OpCodes.OP_IF_EXPR :
      expr = ifExpr(opPos); break;
    case OpCodes.OP_SEQUENCE_CONSTRUCTOR_EXPR :
      expr = sequenceConstructorExpr(opPos); break;
    case OpCodes.OP_ARRAY_CONSTRUCTOR_EXPR :
      expr = arrayConstructorExpr(opPos); break;
    case OpCodes.OP_MAP_CONSTRUCTOR_EXPR :
      expr = mapConstructorExpr(opPos); break;
    case OpCodes.OP_OR :
      expr = or(opPos); break;
    case OpCodes.OP_AND :
      expr = and(opPos); break;
    case OpCodes.OP_NOTEQUALS :
      expr = notequals(opPos); break;
    case OpCodes.OP_EQUALS :
      expr = equals(opPos); break;
    case OpCodes.OP_VC_EQUALS :
      expr = vcEquals(opPos); break;
    case OpCodes.OP_VC_NOT_EQUALS :
      expr = vcNotEquals(opPos); break;
    case OpCodes.OP_VC_LT :
      expr = vcLt(opPos); break;
    case OpCodes.OP_VC_GT :
      expr = vcGt(opPos); break;
    case OpCodes.OP_VC_LE :
      expr = vcLe(opPos); break;
    case OpCodes.OP_VC_GE :
      expr = vcGe(opPos); break;
    case OpCodes.OP_IS :
      expr = nodeComparisonIs(opPos); break;
    case OpCodes.OP_NC_PRECEDE :
      expr = nodeComparisonPrecede(opPos); break;
    case OpCodes.OP_NC_FOLLOWS :
      expr = nodeComparisonFollows(opPos); break;
    case OpCodes.OP_SIMPLE_MAP_OPERATOR :
      expr = simpleMapOperator(opPos); break;
    case OpCodes.OP_SEQUENCE_TYPE_EXPR :
      expr = sequenceTypeExpr(opPos); break;
    case OpCodes.OP_INSTANCE_OF :
      expr = instanceOfExpr(opPos); break;
    case OpCodes.OP_CAST_AS :
      expr = castAsExpr(opPos); break;
    case OpCodes.OP_CASTABLE_AS :
      expr = castableAsExpr(opPos); break;
    case OpCodes.OP_TREAT_AS :
      expr = treatAsExpr(opPos); break;
    case OpCodes.OP_LTE :
      expr = lte(opPos); break;
    case OpCodes.OP_LT :
      expr = lt(opPos); break;
    case OpCodes.OP_GTE :
      expr = gte(opPos); break;
    case OpCodes.OP_GT :
      expr = gt(opPos); break;
    case OpCodes.OP_PLUS :
      expr = plus(opPos); break;
    case OpCodes.OP_TO :
      expr = range(opPos); break;
    case OpCodes.OP_STR_CONCAT :
      expr = strConcat(opPos); break;
    case OpCodes.OP_ARROW :
      m_isCompileFuncPrecededByCompileArrow = true;
      expr = arrowOp(opPos);
      break;
    case OpCodes.OP_MINUS :
      expr = minus(opPos); break;
    case OpCodes.OP_MULT :
      expr = mult(opPos); break;
    case OpCodes.OP_DIV :
      expr = div(opPos); break;
    case OpCodes.OP_IDIV :
      expr = idiv(opPos); break;      
    case OpCodes.OP_MOD :
      expr = mod(opPos); break;
    case OpCodes.OP_NEG :
      expr = neg(opPos); break;
    case OpCodes.OP_STRING :
      expr = string(opPos); break;
    case OpCodes.OP_BOOL :
      expr = bool(opPos); break;
    case OpCodes.OP_NUMBER :
      expr = number(opPos); break;
    case OpCodes.OP_UNION :
      expr = union(opPos); break;
    case OpCodes.OP_INTERSECT :
        expr = intersect(opPos); break;
    case OpCodes.OP_EXCEPT :
        expr = except(opPos); break;
    case OpCodes.OP_LITERAL :
      expr = literal(opPos); break;
    case OpCodes.OP_VARIABLE :
      expr = variable(opPos); break;
    case OpCodes.OP_GROUP :
      expr = group(opPos); break;
    case OpCodes.OP_NUMBERLIT :
      expr = numberlit(opPos); break;
    case OpCodes.OP_ARGUMENT :
      expr = arg(opPos); break;
    case OpCodes.OP_CONSTRUCTOR_STYLESHEET_EXT_FUNCTION :
      expr = compileConstructorStylesheetOrExtensionFunction(opPos); break;
    case OpCodes.OP_FUNCTION :
      expr = compileFunction(opPos); break;
    case OpCodes.OP_INLINE_FUNCTION :
      expr = compileInlineFunctionDefinition(opPos); break;
    case OpCodes.OP_DYNAMIC_FUNCTION_CALL :
      expr = compileDynamicFunctionCall(opPos); break;
    case OpCodes.OP_LOCATIONPATH :
      expr = locationPath(opPos); break;
    case OpCodes.OP_PREDICATE :
      expr = null; break;  // should never hit this here.
    case OpCodes.OP_MATCHPATTERN :
      expr = matchPattern(opPos + 2); break;
    case OpCodes.OP_LOCATIONPATHPATTERN :
      expr = locationPathPattern(opPos); break;
    case OpCodes.OP_NAMED_FUNCTION_REFERENCE :
      expr = namedFunctionReference(opPos); break;
    case OpCodes.OP_XPATH_EXPR_WITH_FUNC_CALL_SUFFIX :
      expr = xpathExprWithFuncCallSuffix(opPos); break;
    case OpCodes.OP_CONTEXT_ITEM_WITH_PREDICATE :
      expr = xpathContextItemWithPredicate(opPos); break;
    case OpCodes.OP_QUO:
      error(XPATHErrorResources.ER_UNKNOWN_OPCODE, new Object[]{ "quo" });
      break;
    default :
      error(XPATHErrorResources.ER_UNKNOWN_OPCODE,
            new Object[]{ Integer.toString(getOp(opPos)) });
    }
    
    return expr;
  }

  /**
   * Bottle-neck compilation of an operation with left and right operands.
   *
   * @param operation non-null reference to parent operation.
   * @param opPos The op map position of the parent operation.
   *
   * @return reference to {@link org.apache.xpath.operations.Operation} instance.
   *
   * @throws TransformerException if there is a syntax or other error.
   */
  private Expression compileOperation(Operation operation, int opPos)
          throws TransformerException
  {

    int leftPos = getFirstChildPos(opPos);
    int rightPos = getNextOpPos(leftPos);

    operation.setLeftRight(compile(leftPos), compile(rightPos));

    return operation;
  }

  /**
   * Bottle-neck compilation of a unary operation.
   *
   * @param unary The parent unary operation.
   * @param opPos The position in the op map of the parent operation.
   *
   * @return The unary argument.
   *
   * @throws TransformerException if syntax or other error occurs.
   */
  private Expression compileUnary(UnaryOperation unary, int opPos)
          throws TransformerException
  {

    int rightPos = getFirstChildPos(opPos);

    unary.setRight(compile(rightPos));

    return unary;
  }

  /**
   * Compile an 'or' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Or} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression or(int opPos) throws TransformerException
  {
    return compileOperation(new Or(), opPos);
  }

  /**
   * Compile an 'and' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.And} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression and(int opPos) throws TransformerException
  {
    return compileOperation(new And(), opPos);
  }

  /**
   * Compile a '!=' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.NotEquals} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression notequals(int opPos) throws TransformerException
  {
    return compileOperation(new NotEquals(), opPos);
  }

  /**
   * Compile a '=' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Equals} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression equals(int opPos) throws TransformerException
  {
    return compileOperation(new Equals(), opPos);
  }
  
  /**
   * Compile an XPath 3.1 value comparison "eq" operation.
   * 
   * @param opPos The current position in the m_opMap array.
   * 
   * @return reference to {@link org.apache.xpath.operations.VcEquals} instance.
   * 
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression vcEquals(int opPos) throws TransformerException
  {
    return compileOperation(new VcEquals(), opPos);
  }
  
  /**
   * Compile an XPath 3.1 value comparison "ne" operation.
   * 
   * @param opPos The current position in the m_opMap array.
   * 
   * @return reference to {@link org.apache.xpath.operations.VcNotEquals} instance.
   * 
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression vcNotEquals(int opPos) throws TransformerException
  {
    return compileOperation(new VcNotEquals(), opPos);
  }

  /**
   * Compile a '<=' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Lte} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression lte(int opPos) throws TransformerException
  {
    return compileOperation(new Lte(), opPos);
  }

  /**
   * Compile a '<' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Lt} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression lt(int opPos) throws TransformerException
  {
    return compileOperation(new Lt(), opPos);
  }
  
  /**
   * Compile an XPath 3.1 value comparison "lt" operation.
   * 
   * @param opPos The current position in the m_opMap array.
   * 
   * @return reference to {@link org.apache.xpath.operations.VcLt} instance.
   * 
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression vcLt(int opPos) throws TransformerException
  {
    return compileOperation(new VcLt(), opPos);
  }
  
  /**
   * Compile an XPath 3.1 value comparison "gt" operation.
   * 
   * @param opPos The current position in the m_opMap array.
   * 
   * @return reference to {@link org.apache.xpath.operations.VcGt} instance.
   * 
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression vcGt(int opPos) throws TransformerException
  {
    return compileOperation(new VcGt(), opPos);
  }
  
  /**
   * Compile an XPath 3.1 value comparison "le" operation.
   * 
   * @param opPos The current position in the m_opMap array.
   * 
   * @return reference to {@link org.apache.xpath.operations.VcLe} instance.
   * 
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression vcLe(int opPos) throws TransformerException
  {
    return compileOperation(new VcLe(), opPos);
  }
  
  /**
   * Compile an XPath 3.1 value comparison "ge" operation.
   * 
   * @param opPos The current position in the m_opMap array.
   * 
   * @return reference to {@link org.apache.xpath.operations.VcGe} instance.
   * 
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression vcGe(int opPos) throws TransformerException
  {
    return compileOperation(new VcGe(), opPos);
  }
  
  /**
   * Compile an XPath 3.1 node comparison "is" operation.
   * 
   * @param opPos The current position in the m_opMap array.
   * 
   * @return reference to {@link org.apache.xpath.operations.NodeComparisonIs} instance.
   * 
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression nodeComparisonIs(int opPos) throws TransformerException
  {
    return compileOperation(new NodeComparisonIs(), opPos);
  }
  
  /**
   * Compile an XPath 3.1 node comparison "<<" operation.
   * 
   * @param opPos The current position in the m_opMap array.
   * 
   * @return reference to {@link org.apache.xpath.operations.NodeComparisonPrecede} instance.
   * 
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression nodeComparisonPrecede(int opPos) throws TransformerException
  {
    return compileOperation(new NodeComparisonPrecede(), opPos);
  }
  
  /**
   * Compile an XPath 3.1 node comparison ">>" operation.
   * 
   * @param opPos The current position in the m_opMap array.
   * 
   * @return reference to {@link org.apache.xpath.operations.NodeComparisonFollows} instance.
   * 
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression nodeComparisonFollows(int opPos) throws TransformerException
  {
    return compileOperation(new NodeComparisonFollows(), opPos);
  }
  
  /**
   * Compile an XPath 3.1 simple map '!' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   * 
   * @return reference to {@link org.apache.xpath.operations.SimpleMapOperator} instance.
   * 
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression simpleMapOperator(int opPos) throws TransformerException
  {
    return compileOperation(new SimpleMapOperator(), opPos);
  }
  
  /**
   * Compile an XPath 'SequenceType', expression.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return the compiled 'SequenceType' expression returned as an object of class
   *         XPathSequenceTypeExpr.       
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  Expression sequenceTypeExpr(int opPos) throws TransformerException
  {
      return XPathParser.m_xpathSequenceTypeExpr;
  }
  
  /**
   * Compile an XPath 3.1 "instance of" expression.
   * 
   * @param opPos The current position in the m_opMap array.
   * 
   * @return reference to {@link org.apache.xpath.operations.InstanceOf} instance.
   * 
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression instanceOfExpr(int opPos) throws TransformerException
  {
    return compileOperation(new InstanceOf(), opPos);
  }
  
  /**
   * Compile a 'cast as' operation.
   * 
   * @param    opPos The current position in the m_opMap array.
   * @return   an XPath compiled representation of 'cast as' expression 
   * @throws TransformerException
   */
  protected Expression castAsExpr(int opPos) throws TransformerException
  {
	return compileOperation(new CastAs(), opPos); 
  }
  
  /**
   * Compile a 'castable as' operation.
   * 
   * @param    opPos The current position in the m_opMap array.
   * @return   an XPath compiled representation of 'castable as' expression 
   * @throws TransformerException
   */
  protected Expression castableAsExpr(int opPos) throws TransformerException
  {
	return compileOperation(new CastableAs(), opPos); 
  }
  
  /**
   * Compile a 'treat as' operation.
   * 
   * @param    opPos The current position in the m_opMap array.
   * @return   an XPath compiled representation of 'treat as' expression 
   * @throws TransformerException
   */
  protected Expression treatAsExpr(int opPos) throws TransformerException
  {
	return compileOperation(new TreatAs(), opPos); 
  }

  /**
   * Compile a '>=' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Gte} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression gte(int opPos) throws TransformerException
  {
    return compileOperation(new Gte(), opPos);
  }

  /**
   * Compile a '>' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Gt} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression gt(int opPos) throws TransformerException
  {
    return compileOperation(new Gt(), opPos);
  }

  /**
   * Compile a '+' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Plus} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression plus(int opPos) throws TransformerException
  {
    return compileOperation(new Plus(), opPos);
  }
  
  /**
   * Compile an XPath 3.1 range "to" operation.
   * 
   * @param opPos The current position in the m_opMap array.
   * 
   * @return reference to {@link org.apache.xpath.operations.Range} instance.
   * 
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression range(int opPos) throws TransformerException
  {
    return compileOperation(new Range(), opPos);   
  }
  
  /**
   * Compile an XPath 3.1 string concatenation "||" operation.
   * 
   * @param opPos The current position in the m_opMap array.
   * 
   * @return reference to {@link org.apache.xpath.operations.StrConcat} instance.
   * 
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression strConcat(int opPos) throws TransformerException
  {
    return compileOperation(new StrConcat(), opPos);   
  }
  
  /**
   * Compile an XPath 3.1 arrow "=>" operation.
   * 
   * @param opPos The current position in the m_opMap array.
   * 
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression arrowOp(int opPos) throws TransformerException
  {
	return compileOperation(new ArrowOp(), opPos);
  }

  /**
   * Compile a '-' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Minus} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression minus(int opPos) throws TransformerException
  {
    return compileOperation(new Minus(), opPos);
  }

  /**
   * Compile a '*' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Mult} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression mult(int opPos) throws TransformerException
  {
    return compileOperation(new Mult(), opPos);
  }

  /**
   * Compile a 'div' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Div} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression div(int opPos) throws TransformerException
  {
    return compileOperation(new Div(), opPos);
  }
  
  /**
   * Compile a 'idiv' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.IDiv} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression idiv(int opPos) throws TransformerException
  {
    return compileOperation(new IDiv(), opPos);
  }

  /**
   * Compile a 'mod' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Mod} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression mod(int opPos) throws TransformerException
  {
    return compileOperation(new Mod(), opPos);
  }

  /*
   * Compile a 'quo' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Quo} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
//  protected Expression quo(int opPos) throws TransformerException
//  {
//    return compileOperation(new Quo(), opPos);
//  }

  /**
   * Compile a unary '-' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Neg} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression neg(int opPos) throws TransformerException
  {
    return compileUnary(new Neg(), opPos);
  }

  /**
   * Compile a 'string(...)' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.String} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression string(int opPos) throws TransformerException
  {
    return compileUnary(new org.apache.xpath.operations.String(), opPos);
  }

  /**
   * Compile a 'boolean(...)' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Bool} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression bool(int opPos) throws TransformerException
  {
    return compileUnary(new org.apache.xpath.operations.Bool(), opPos);
  }

  /**
   * Compile a 'number(...)' operation.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Number} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression number(int opPos) throws TransformerException
  {
    return compileUnary(new org.apache.xpath.operations.Number(), opPos);
  }

  /**
   * Compile a literal string value.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.objects.XString} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression literal(int opPos)
  {

    opPos = getFirstChildPos(opPos);

    return (XString) getTokenQueue().elementAt(getOp(opPos));
  }

  /**
   * Compile a literal number value.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.objects.XNumber} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression numberlit(int opPos)
  {

    opPos = getFirstChildPos(opPos);

    return (XNumber) getTokenQueue().elementAt(getOp(opPos));
  }

  /**
   * Compile a variable reference.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.operations.Variable} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression variable(int opPos) throws TransformerException
  {

    Variable var = new Variable();

    opPos = getFirstChildPos(opPos);

    int nsPos = getOp(opPos);
    java.lang.String namespace 
      = (OpCodes.EMPTY == nsPos) ? null 
                                   : (java.lang.String) getTokenQueue().elementAt(nsPos);
    java.lang.String localname 
      = (java.lang.String) getTokenQueue().elementAt(getOp(opPos+1));
    QName qname = new QName(namespace, localname);

    var.setQName(qname);

    return var;
  }

  /**
   * Compile an expression group.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to the contained expression.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression group(int opPos) throws TransformerException
  {

    // no-op
    return compile(opPos + 2);
  }

  /**
   * Compile a function argument.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to the argument expression.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression arg(int opPos) throws TransformerException
  {

    // no-op
    return compile(opPos + 2);
  }

  /**
   * Compile a location path union. The UnionPathIterator itself may create
   * {@link org.apache.xpath.axes.LocPathIterator} children.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.axes.LocPathIterator} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression union(int opPos) throws TransformerException
  {
    locPathDepth++;
    try
    {
      return UnionPathIterator.createUnionIterator(this, opPos);
    }
    finally
    {
      locPathDepth--;
    }
  }
  
  /**
   * Compile an XPath node sequence 'intersect' operator.
   */
  protected Expression intersect(int opPos) throws TransformerException
  {	  
	  locPathDepth++;
	  
	  Expression result = null;
	  
	  try {
	     result = compileOperation(new Intersect(), opPos);
	  }
	  finally {
		 locPathDepth--;
	  }	
	  
	  return result;
  }
  
  /**
   * Compile an XPath node sequence 'except' operator.
   */
  protected Expression except(int opPos) throws TransformerException
  {	  
      locPathDepth++;
	  
	  Expression result = null;
	  
	  try {
	     result = compileOperation(new Except(), opPos);
	  }
	  finally {
		 locPathDepth--;
	  }	
	  
	  return result;
  }
  
  private int locPathDepth = -1;
  
  /**
   * Get the level of the location path or union being constructed.  
   * @return 0 if it is a top-level path.
   */
  public int getLocationPathDepth()
  {
    return locPathDepth;
  }

  /**
   * Get the function table  
   */
  FunctionTable getFunctionTable()
  {
    return m_functionTable;
  }

  /**
   * Compile a location path.  The LocPathIterator itself may create
   * {@link org.apache.xpath.axes.AxesWalker} children.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.axes.LocPathIterator} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  public Expression locationPath(int opPos) throws TransformerException
  {
    locPathDepth++;
    try
    {
      DTMCursorIterator iter = WalkerFactory.newDTMIterator(this, opPos, (locPathDepth == 0));
      return (Expression)iter; // cast OK, I guess.
    }
    finally
    {
      locPathDepth--;
    }
  }

  /**
   * Compile a location step predicate expression.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return the contained predicate expression.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  public Expression predicate(int opPos) throws TransformerException
  {
    return compile(opPos + 2);
  }

  /**
   * Compile an entire match pattern expression.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.patterns.UnionPattern} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected Expression matchPattern(int opPos) throws TransformerException
  {
    locPathDepth++;
    try
    {
      // First, count...
      int nextOpPos = opPos;
      int i;

      for (i = 0; getOp(nextOpPos) == OpCodes.OP_LOCATIONPATHPATTERN; i++)
      {
        nextOpPos = getNextOpPos(nextOpPos);
      }

      if (i == 1)
        return compile(opPos);

      UnionPattern up = new UnionPattern();
      StepPattern[] patterns = new StepPattern[i];

      for (i = 0; getOp(opPos) == OpCodes.OP_LOCATIONPATHPATTERN; i++)
      {
        nextOpPos = getNextOpPos(opPos);
        patterns[i] = (StepPattern) compile(opPos);
        opPos = nextOpPos;
      }

      up.setPatterns(patterns);

      return up;
    }
    finally
    {
      locPathDepth--;
    }
  }

  /**
   * Compile a location match pattern unit expression.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.patterns.StepPattern} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  public Expression locationPathPattern(int opPos)
          throws TransformerException
  {

    opPos = getFirstChildPos(opPos);

    return stepPattern(opPos, 0, null);
  }
  
  /**
   * Compile an XPath 'named function reference' expression.
   */
  public Expression namedFunctionReference(int opPos) {
	return XPathParser.m_xpathNamedFunctionReference;  
  }

  /**
   * Get a {@link org.w3c.dom.traversal.NodeFilter} bit set that tells what 
   * to show for a given node test.
   *
   * @param opPos the op map position for the location step.
   *
   * @return {@link org.w3c.dom.traversal.NodeFilter} bit set that tells what 
   *         to show for a given node test.
   */
  public int getWhatToShow(int opPos)
  {

    int axesType = getOp(opPos);
    int testType = getOp(opPos + 3);

    // System.out.println("testType: "+testType);
    switch (testType)
    {
    case OpCodes.NODETYPE_COMMENT :
      return DTMFilter.SHOW_COMMENT;
    case OpCodes.NODETYPE_TEXT :
//      return DTMFilter.SHOW_TEXT | DTMFilter.SHOW_COMMENT;
      return DTMFilter.SHOW_TEXT | DTMFilter.SHOW_CDATA_SECTION ;
    case OpCodes.NODETYPE_PI :
      return DTMFilter.SHOW_PROCESSING_INSTRUCTION;
    case OpCodes.NODETYPE_NODE :
//      return DTMFilter.SHOW_ALL;
      switch (axesType)
      {
      case OpCodes.FROM_NAMESPACE:
        return DTMFilter.SHOW_NAMESPACE;
      case OpCodes.FROM_ATTRIBUTES :
      case OpCodes.MATCH_ATTRIBUTE :
        return DTMFilter.SHOW_ATTRIBUTE;
      case OpCodes.FROM_SELF:
      case OpCodes.FROM_ANCESTORS_OR_SELF:
      case OpCodes.FROM_DESCENDANTS_OR_SELF:
        return DTMFilter.SHOW_ALL;
      default:
        if (getOp(0) == OpCodes.OP_MATCHPATTERN)
          return ~DTMFilter.SHOW_ATTRIBUTE
                  & ~DTMFilter.SHOW_DOCUMENT
                  & ~DTMFilter.SHOW_DOCUMENT_FRAGMENT;
        else
          return ~DTMFilter.SHOW_ATTRIBUTE;
      }
    case OpCodes.NODETYPE_ROOT :
      return DTMFilter.SHOW_DOCUMENT | DTMFilter.SHOW_DOCUMENT_FRAGMENT;
    case OpCodes.NODETYPE_FUNCTEST :
      return NodeTest.SHOW_BYFUNCTION;
    case OpCodes.NODENAME :
      switch (axesType)
      {
      case OpCodes.FROM_NAMESPACE :
        return DTMFilter.SHOW_NAMESPACE;
      case OpCodes.FROM_ATTRIBUTES :
      case OpCodes.MATCH_ATTRIBUTE :
        return DTMFilter.SHOW_ATTRIBUTE;

      // break;
      case OpCodes.MATCH_ANY_ANCESTOR :
      case OpCodes.MATCH_IMMEDIATE_ANCESTOR :
        return DTMFilter.SHOW_ELEMENT;

      // break;
      default :
        return DTMFilter.SHOW_ELEMENT;
      }
    default :
      // System.err.println("We should never reach here.");
      return DTMFilter.SHOW_ALL;
    }
  }
  
private static final boolean DEBUG = false;

  /**
   * Compile a step pattern unit expression, used for both location paths 
   * and match patterns.
   * 
   * @param opPos The current position in the m_opMap array.
   * @param stepCount The number of steps to expect.
   * @param ancestorPattern The owning StepPattern, which may be null.
   *
   * @return reference to {@link org.apache.xpath.patterns.StepPattern} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  protected StepPattern stepPattern(
          int opPos, int stepCount, StepPattern ancestorPattern)
            throws TransformerException
  {

    int startOpPos = opPos;
    int stepType = getOp(opPos);

    if (OpCodes.ENDOP == stepType)
    {
      return null;
    }
    
    boolean addMagicSelf = true;

    int endStep = getNextOpPos(opPos);

    // int nextStepType = getOpMap()[endStep];
    StepPattern pattern;
    
    // boolean isSimple = ((OpCodes.ENDOP == nextStepType) && (stepCount == 0));
    int argLen;

    switch (stepType)
    {
    case OpCodes.OP_FUNCTION :
      if(DEBUG)
        System.out.println("MATCH_FUNCTION: "+m_currentPattern); 
      addMagicSelf = false;
      argLen = getOp(opPos + OpMap.MAPINDEX_LENGTH);
      pattern = new FunctionPattern(compileFunction(opPos), Axis.PARENT, Axis.CHILD);
      break;
    case OpCodes.FROM_ROOT :
      if(DEBUG)
        System.out.println("FROM_ROOT, "+m_currentPattern);
      addMagicSelf = false;
      argLen = getArgLengthOfStep(opPos);
      opPos = getFirstChildPosOfStep(opPos);
      pattern = new StepPattern(DTMFilter.SHOW_DOCUMENT | 
                                DTMFilter.SHOW_DOCUMENT_FRAGMENT,
                                Axis.PARENT, Axis.CHILD);
      break;
    case OpCodes.MATCH_ATTRIBUTE :
     if(DEBUG)
        System.out.println("MATCH_ATTRIBUTE: "+getStepLocalName(startOpPos)+", "+m_currentPattern);
      argLen = getArgLengthOfStep(opPos);
      opPos = getFirstChildPosOfStep(opPos);
      pattern = new StepPattern(DTMFilter.SHOW_ATTRIBUTE,
                                getStepNS(startOpPos),
                                getStepLocalName(startOpPos),
                                Axis.PARENT, Axis.ATTRIBUTE);
      break;
    case OpCodes.MATCH_ANY_ANCESTOR :
      if(DEBUG)
        System.out.println("MATCH_ANY_ANCESTOR: "+getStepLocalName(startOpPos)+", "+m_currentPattern);
      argLen = getArgLengthOfStep(opPos);
      opPos = getFirstChildPosOfStep(opPos);
      int what = getWhatToShow(startOpPos);
      // bit-o-hackery, but this code is due for the morgue anyway...
      if(0x00000500 == what)
        addMagicSelf = false;
      pattern = new StepPattern(getWhatToShow(startOpPos),
                                        getStepNS(startOpPos),
                                        getStepLocalName(startOpPos),
                                        Axis.ANCESTOR, Axis.CHILD);
      break;
    case OpCodes.MATCH_IMMEDIATE_ANCESTOR :
      if(DEBUG)
        System.out.println("MATCH_IMMEDIATE_ANCESTOR: "+getStepLocalName(startOpPos)+", "+m_currentPattern);
      argLen = getArgLengthOfStep(opPos);
      opPos = getFirstChildPosOfStep(opPos);
      pattern = new StepPattern(getWhatToShow(startOpPos),
                                getStepNS(startOpPos),
                                getStepLocalName(startOpPos),
                                Axis.PARENT, Axis.CHILD);
      break;
    default :
      error(XPATHErrorResources.ER_UNKNOWN_MATCH_OPERATION, null);  //"unknown match operation!");

      return null;
    }

    pattern.setPredicates(getCompiledPredicates(opPos + argLen));
    if(null == ancestorPattern)
    {
      // This is the magic and invisible "." at the head of every 
      // match pattern, and corresponds to the current node in the context 
      // list, from where predicates are counted.
      // So, in order to calculate "foo[3]", it has to count from the 
      // current node in the context list, so, from that current node, 
      // the full pattern is really "self::node()/child::foo[3]".  If you 
      // translate this to a select pattern from the node being tested, 
      // which is really how we're treating match patterns, it works out to 
      // self::foo/parent::node[child::foo[3]]", or close enough.
	/*      if(addMagicSelf && pattern.getPredicateCount() > 0)
      {
        StepPattern selfPattern = new StepPattern(DTMFilter.SHOW_ALL, 
                                                  Axis.PARENT, Axis.CHILD);
        // We need to keep the new nodetest from affecting the score...
        XNumber score = pattern.getStaticScore();
        pattern.setRelativePathPattern(selfPattern);
        pattern.setStaticScore(score);
        selfPattern.setStaticScore(score);
	}*/
    }
    else
    {
      // System.out.println("Setting "+ancestorPattern+" as relative to "+pattern);
      pattern.setRelativePathPattern(ancestorPattern);
    }

    StepPattern relativePathPattern = stepPattern(endStep, stepCount + 1,
                                        pattern);

    return (null != relativePathPattern) ? relativePathPattern : pattern;
  }

  /**
   * Compile a zero or more predicates for a given match pattern.
   * 
   * @param opPos The position of the first predicate the m_opMap array.
   *
   * @return reference to array of {@link org.apache.xpath.Expression} instances.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  public Expression[] getCompiledPredicates(int opPos)
          throws TransformerException
  {

    int count = countPredicates(opPos);

    if (count > 0)
    {
      Expression[] predicates = new Expression[count];

      compilePredicates(opPos, predicates);

      return predicates;
    }

    return null;
  }

  /**
   * Count the number of predicates in the step.
   *
   * @param opPos The position of the first predicate the m_opMap array.
   *
   * @return The number of predicates for this step.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  public int countPredicates(int opPos) throws TransformerException
  {

    int count = 0;

    while (OpCodes.OP_PREDICATE == getOp(opPos))
    {
      count++;

      opPos = getNextOpPos(opPos);
    }

    return count;
  }

  /**
   * Compiles predicates in the step.
   *
   * @param opPos The position of the first predicate the m_opMap array.
   * @param predicates An empty pre-determined array of 
   *            {@link org.apache.xpath.Expression}s, that will be filled in.
   *
   * @throws TransformerException
   */
  private void compilePredicates(int opPos, Expression[] predicates)
          throws TransformerException
  {

    for (int i = 0; OpCodes.OP_PREDICATE == getOp(opPos); i++)
    {
      predicates[i] = predicate(opPos);
      opPos = getNextOpPos(opPos);
    }
  }

  /**
   * Compile a built-in XPath function.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.functions.Function} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  Expression compileFunction(int opPos) throws TransformerException
  {

    int endFunc = opPos + getOp(opPos + 1) - 1;

    opPos = getFirstChildPos(opPos);

    int funcID = getOp(opPos);

    opPos++;

    if (-1 != funcID)
    {
      Function func = m_functionTable.getFunction(funcID);
      
      /**
       * It is a trick for function-available. Since the function table is an
       * instance field, insert this table at compilation time for later usage
       */
      
      if (func instanceof FuncExtFunctionAvailable)
          ((FuncExtFunctionAvailable) func).setFunctionTable(m_functionTable);

      func.postCompileStep(this);
      
      try
      {
        int i = 0;

        for (int p = opPos; p < endFunc; p = getNextOpPos(p), i++)
        {          
           func.setArg(compile(p), i);
        }
        
        if (m_isCompileFuncPrecededByCompileArrow) 
        {
           // This allows us to, permit the absence of XPath function's 1st 
           // argument when evaluating with operator "=>".
           i++;
           m_isCompileFuncPrecededByCompileArrow = false;
        }
        
        func.checkNumberArgs(i);
      }
      catch (WrongNumberArgsException wnae)
      {
        java.lang.String name = m_functionTable.getFunctionName(funcID);

        m_errorHandler.fatalError( new TransformerException(
                                             XSLMessages.createXPATHMessage(XPATHErrorResources.ER_ONLY_ALLOWS, 
                                             new Object[]{name, wnae.getMessage()}), m_locator));
      }

      return func;
    }
    else
    {
      error(XPATHErrorResources.ER_FUNCTION_TOKEN_NOT_FOUND, null);  //"function token not found.");

      return null;
    }
  }
  
  /**
   * Compile an XPath function item inline function definition, expression.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return the compiled inline function definition expression returned
   *         as an object of class InlineFunction.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  Expression compileInlineFunctionDefinition(int opPos) throws TransformerException
  {
      return XPathParser.m_xpath_inlineFunction;
  }
  
  /**
   * Compile an XPath dynamic function call, expression.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return the compiled dynamic function call expression returned
   *         as an object of class DynamicFunctionCall.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  Expression compileDynamicFunctionCall(int opPos) throws TransformerException
  {
	  XPathDynamicFunctionCall result = null;

	  XPathParser.m_xpathDynFuncCallProcessedCount++;
	  result = (XPathParser.m_xpathDynamicFunctionCallList).get(XPathParser.m_xpathDynFuncCallProcessedCount - 1);  

	  return result;	  	  
  }
  
  /**
   * Compile an XPath "for", expression.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return the compiled "for" expression returned as an object of class
   *         XPathForExpr.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  Expression forExpr(int opPos) throws TransformerException
  {
	  XPathForExpr forExpr = (XPathParser.m_forExprList).get(0);
	  (XPathParser.m_forExprList).remove(0);
      return forExpr;	  
  }
  
  /**
   * Compile an XPath "let", expression.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return the compiled "let" expression returned as an object of class
   *         XPathLetExpr.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  Expression letExpr(int opPos) throws TransformerException
  {
      return XPathParser.m_letExpr;
  }
  
  /**
   * Compile an XPath quantified, expression (either 'some' or 'every').
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return the compiled quantified expression returned as an object of class
   *         XPathQuantifiedExpr.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  Expression quantifiedExpr(int opPos) throws TransformerException
  {
      return XPathParser.m_quantifiedExpr;
  }
  
  /**
   * Compile an XPath "if", expression.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return the compiled "if" expression returned as an object of class
   *         IfExpr.     
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  Expression ifExpr(int opPos) throws TransformerException
  {
      return XPathParser.m_ifExpr;
  }
  
  /**
   * Compile an XPath sequence constructor, expression.
   */
  Expression sequenceConstructorExpr(int opPos) throws TransformerException
  {	  
      Expression xpathSequenceCons = null;
	  
	  if (XPathParser.m_xpathSequenceConstructor != null) {
		 xpathSequenceCons = XPathParser.m_xpathSequenceConstructor;
		 XPathParser.m_xpathSequenceConstructor = null;
	  }
	  else {
		 // We use an implementation here, when XPath built-in function call 
		 // arguments are literal sequence expressions.
		 XPathSequenceConsFuncArgs xpathSeqConsFuncArgs = XPathParser.m_xpathSequenceConsFuncArgs;
		 
		 List<XPathSequenceConstructor> seqConsList = xpathSeqConsFuncArgs.getSeqFuncArgList();		 
		 List<Boolean> funcArgUsedList = xpathSeqConsFuncArgs.getIsFuncArgUsedList();		 
		 for (int idx = 0; idx < funcArgUsedList.size(); idx++) {
			Boolean boolVal = funcArgUsedList.get(idx);
			if (!boolVal.booleanValue()) {
			   xpathSequenceCons = seqConsList.get(idx);
			   funcArgUsedList.set(idx, Boolean.valueOf(true));
			   break;
			}
		 }
	  }
	  
	  return xpathSequenceCons;
  }
  
  /**
   * Compile an XPath array constructor, expression.
   */
  Expression arrayConstructorExpr(int opPos) throws TransformerException
  {
	  Expression xpathArrayCons = null;
	  
	  if (XPathParser.m_xpathArrayConstructor != null) {
		 xpathArrayCons = XPathParser.m_xpathArrayConstructor;
		 XPathParser.m_xpathArrayConstructor = null;
	  }
	  else {
		 // We use an implementation within this 'else' branch, when XPath
		 // built-in function call arguments are literal array expressions.
		 XPathArrayConsFuncArgs xpathArrayConsFuncArgs = XPathParser.m_xpathArrayConsFuncArgs;
		 
		 List<XPathArrayConstructor> arrayConsList = xpathArrayConsFuncArgs.getArrayFuncArgList();		 
		 List<Boolean> funcArgUsedArr = xpathArrayConsFuncArgs.getIsFuncArgUsedArr();		 
		 for (int idx = 0; idx < funcArgUsedArr.size(); idx++) {
			Boolean boolVal = funcArgUsedArr.get(idx);
			if (!boolVal.booleanValue()) {
			   xpathArrayCons = arrayConsList.get(idx);
			   funcArgUsedArr.set(idx, Boolean.valueOf(true));
			   break;
			}
		 }
	  }
	  
	  return xpathArrayCons; 
  }
  
  /**
   * Compile an XPath path expression, whose string value has a 
   * function call suffix (for e.g, an XPath expression of the 
   * form /temp/abc/func()).
   */
  Expression xpathExprWithFuncCallSuffix(int opPos) throws TransformerException
  {
      return XPathParser.m_xpathExprWithFuncCallSuffix;
  }
  
  /**
   * Compile an XPath expression with string value like .[predicate]
   */
  Expression xpathContextItemWithPredicate(int opPos) throws TransformerException
  {
      return XPathParser.xpathContextItemWithPredicate;
  }
  
  /**
   * Compile an XPath map constructor, expression.
   */
  Expression mapConstructorExpr(int opPos) throws TransformerException
  {
	  return XPathParser.m_xpathMapConstructor; 
  }

  // The current id for extension functions.
  private static long s_nextMethodId = 0;

  /**
   * Get the next available method id
   */
  synchronized private long getNextMethodId()
  {
    if (s_nextMethodId == Long.MAX_VALUE)
      s_nextMethodId = 0;
    
    return s_nextMethodId++;
  }
  
  /**
   * Compile an XPath constructor, XSL stylesheet or an extension function.
   * 
   * @param opPos The current position in the m_opMap array.
   *
   * @return reference to {@link org.apache.xpath.functions.XSL3ConstructorOrExtensionFunction} instance.
   *
   * @throws TransformerException if a error occurs creating the Expression.
   */
  private Expression compileConstructorStylesheetOrExtensionFunction(int opPos)
          throws TransformerException
  {

    int endExtFunc = opPos + getOp(opPos + 1) - 1;

    opPos = getFirstChildPos(opPos);

    java.lang.String ns = (java.lang.String) getTokenQueue().elementAt(getOp(opPos));

    opPos++;

    java.lang.String funcName =
      (java.lang.String) getTokenQueue().elementAt(getOp(opPos));

    opPos++;

    // We create a method key to uniquely identify this function so that we
    // can cache the object needed to invoke it. This way, we only pay the
    // reflection overhead on the first call.

    Function funcObj = new XSL3ConstructorOrExtensionFunction(ns, funcName, String.valueOf(getNextMethodId()));

    try
    {
      int i = 0;

      while (opPos < endExtFunc)
      {
        int nextOpPos = getNextOpPos(opPos);

        funcObj.setArg(this.compile(opPos), i);

        opPos = nextOpPos;

        i++;
      }
    }
    catch (WrongNumberArgsException wnae)
    {
      ;  // should never happen
    }

    return funcObj;
  }

  /**
   * Warn the user of an problem.
   *
   * @param msg An error msgkey that corresponds to one of the constants found 
   *            in {@link org.apache.xpath.res.XPATHErrorResources}, which is 
   *            a key for a format string.
   * @param args An array of arguments represented in the format string, which 
   *             may be null.
   *
   * @throws TransformerException if the current ErrorListoner determines to 
   *                              throw an exception.
   */
  public void warn(String msg, Object[] args) throws TransformerException
  {

    java.lang.String fmsg = XSLMessages.createXPATHWarning(msg, args);

    if (null != m_errorHandler)
    {
      m_errorHandler.warning(new TransformerException(fmsg, m_locator));
    }
    else
    {
      System.out.println(fmsg
                          +"; file "+m_locator.getSystemId()
                          +"; line "+m_locator.getLineNumber()
                          +"; column "+m_locator.getColumnNumber());
    }
  }

  /**
   * Tell the user of an assertion error, and probably throw an
   * exception.
   *
   * @param b  If false, a runtime exception will be thrown.
   * @param msg The assertion message, which should be informative.
   * 
   * @throws RuntimeException if the b argument is false.
   */
  public void assertion(boolean b, java.lang.String msg)
  {

    if (!b)
    {
      java.lang.String fMsg = XSLMessages.createXPATHMessage(
        XPATHErrorResources.ER_INCORRECT_PROGRAMMER_ASSERTION,
        new Object[]{ msg });

      throw new RuntimeException(fMsg);
    }
  }

  /**
   * Tell the user of an error, and probably throw an
   * exception.
   *
   * @param msg An error msgkey that corresponds to one of the constants found 
   *            in {@link org.apache.xpath.res.XPATHErrorResources}, which is 
   *            a key for a format string.
   * @param args An array of arguments represented in the format string, which 
   *             may be null.
   *
   * @throws TransformerException if the current ErrorListoner determines to 
   *                              throw an exception.
   */
  public void error(String msg, Object[] args) throws TransformerException
  {

    java.lang.String fmsg = XSLMessages.createXPATHMessage(msg, args);
    

    if (null != m_errorHandler)
    {
      m_errorHandler.fatalError(new TransformerException(fmsg, m_locator));
    }
    else
    {

      // System.out.println(te.getMessage()
      //                    +"; file "+te.getSystemId()
      //                    +"; line "+te.getLineNumber()
      //                    +"; column "+te.getColumnNumber());
      throw new TransformerException(fmsg, (SAXSourceLocator)m_locator);
    }
  }

  /**
   * The current prefixResolver for the execution context.
   */
  private PrefixResolver m_currentPrefixResolver = null;

  /**
   * Get the current namespace context for the xpath.
   *
   * @return The current prefix resolver, *may* be null, though hopefully not.
   */
  public PrefixResolver getNamespaceContext()
  {
    return m_currentPrefixResolver;
  }

  /**
   * Set the current namespace context for the xpath.
   *
   * @param pr The resolver for prefixes in the XPath expression.
   */
  public void setNamespaceContext(PrefixResolver pr)
  {
    m_currentPrefixResolver = pr;
  }

  /** The error listener where errors will be sent.  If this is null, errors 
   *  and warnings will be sent to System.err.  May be null.    */
  ErrorListener m_errorHandler;

  /** The source locator for the expression being compiled.  May be null. */
  SourceLocator m_locator;
  
  /**
   * The FunctionTable for all xpath build-in functions
   */
  private FunctionTable m_functionTable;
  
  /**
   * We store within this class field, the fact that, there is
   * an XPath expression of kind "... => functionCall()".
   */
  private boolean m_isCompileFuncPrecededByCompileArrow;
}
