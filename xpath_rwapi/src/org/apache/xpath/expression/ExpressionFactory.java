/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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
 * originally based on software copyright (c) 2002, International
 * Business Machines Corporation., http://www.ibm.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xpath.expression;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.xml.QName;
import org.apache.xpath.XPath20Exception;
import org.apache.xpath.datamodel.SequenceType;
import org.apache.xpath.datamodel.SingleType;

/**
 * Constructors for XPath expressions. 
 * 
 * @author <a href="mailto:villard@us.ibm.com">Lionel Villard</a>
 * @version $Id$
 */
public interface ExpressionFactory
{
	/**
	 * Creates a new XPath expression from the specified string representation.
	 * <p>The following namespace prefixes are defined in the default static context:
	 * <ul>
	 * <li>xs is bound to http://www.w3.org/2001/XMLSchema</li>
	 * <li>xsi is bound to http://www.w3.org/2001/XMLSchema-instance</li>
	 * <li>fn is bound to http://www.w3.org/2003/05/xpath-functions</li>
	 * </ul>
	 * The others attributes of the default static context are defined as following:
	 * <ul>
	 * <li>XPath 1.0 compatibility mode: false</li>
	 * <li>In-scope namespaces: see above</li>
	 * <li>Default element/type namespace: set to null</li>
	 * <li>Default function namespace: set to null</li>
	 * <li>In-scope schema definitions: empty set</li>
	 * <li>In-scope variables: empty set</li>
	 * <li>In-scope functions: set of XPath functions</li>
	 * <li>In-scope collations: empty set</li>
	 * <li>Default collation: set to null</li>
	 * <li>Base URI: set to null</li>
	 * <li>Statically-known documents: empty set</li>
	 * <li>Statically-known collections: empty set</li>
	 * </ul>
	 * </p> 
	 * <p>Various static checkings are not performed during the creation,  
	 * like the existence test of variable declaration.</p>
	 * <p>The returned expression is always a {@link OperatorExpr sequence expression}.
	 * This statement fits the assertion written 
	 * in <a target="_top" href="http://www.w3.org/TR/xpath20#dt-sequence">XPath 2.0 specification</a>
	 * </p>
	 * @return A XPath expression
	 * @throws XPath20Exception whenever the specified expression is not statically 
	 * valid
	 */
	public Expr createExpr(String expr) throws XPath20Exception;

	/**
	 * Creates a new XPath expression from the specified string representation.
	 * Uses the specified static context to resolve namespaces and perform
	 * various static checkings.
	 * <p>The returned expression is always a {@link OperatorExpr sequence expression}.
	 * This statement fits the assertion written 
	 * in <a target="_parent" href="http://www.w3.org/TR/xpath20#dt-sequence">XPath 2.0 specification</a>
	 * </p>
	 * @return A XPath expression
	 * @throws XPath20Exception whenever the specified expression is not 
	 * statically valid.
	 */
	public Expr createExpr(StaticContext ctx, String expr)
		throws XPath20Exception;

	/**
	 * Creates a new pattern from the specified string representation.
	 * Uses the {@link org.apache.xpath.impl.DefaultStaticContext default static context}.
	 * @return A XPath expression which respects the pattern syntax defined in 
	 * XSLT 2.0
	 * @see <a target="_top" href=" http://www.w3.org/TR/xslt20/#patterns">XSLT 2.0 specification</a>
	 */
	public Expr createPattern(String pattern) throws XPath20Exception;
	
	/**
	 * Creates a new pattern from the specified string representation.
	 * Uses the specified static context to resolve namespaces and perform
	 * various static checkings.
	 * @return A XPath expression which respects the pattern syntax defined in 
	 * XSLT 2.0
	 * @see <a target="_top" href=" http://www.w3.org/TR/xslt20/#patterns">XSLT 2.0 specification</a>
	 */
	public Expr createPattern(StaticContext ctx, String pattern) throws XPath20Exception;

	/**
	 * Creates a new empty {@link OperatorExpr expression sequence}
	 * @return A expression sequence
	 */
	public OperatorExpr createSequence();

	/**
	 * Creates a new relative or absolute {@link PathExpr}
	 * @return An XPath expression of type 'path'
	 */
	public PathExpr createPathExpr(boolean isAbsolute);

	/**
	 * Creates a new {@link StepExpr axis step} from the specified axis type and node test.
	 * It contains no predicate.     
	 * @return A {@link StepExpr}
	 */
	public StepExpr createStepExpr(short axisType, NodeTest nodeTest);

	/**
	 * Creates a new {@link StepExpr filter step} from the specified primary expression.
	 * It contains no predicate.     
	 * @return A {@link StepExpr}
	 */
	public StepExpr createStepExpr(Expr primaryExpr);

	/**
	 * Creates a {@link NameTest}.
	 * @return A name test
	 */
	public NameTest createNameTest(QName qname);

	/**
	 * Creates a {@link KindTest} of the specified type.
	 *   
	 * @return A {@link KindTest} which match <em>any</em> nodes according to
	 * the specified kind test type (element(), node(), etc..).
	 */
	public KindTest createKindTest(short type);

	/**
	 * Creates an {@link OperatorExpr operator expression} from the
	 * specified type. The new operator contains no operand.
	 * @param exprType type of operator to create. See {@link Expr} for existing
	 * operator type.
	 * @param operatorType One of the constants defined in {@link OperatorExpr}
	 * @return A new operator
	 */
	public OperatorExpr createOperatorExpr(short exprType, short operatorType);

	/**
	 * Creates a new {@link OperatorExpr combining expression} of the specified type
	 * @param type The type of the combining expression to create.
	 * @return An XPath expression of type combine     
	 * @see OperatorExpr#UNION_COMBINE
	 * @see OperatorExpr#EXCEPT_COMBINE
	 * @see OperatorExpr#INTERSECT_COMBINE
	 */
	public OperatorExpr createCombineExpr(short type);

	/**
	 * Creates a new {@link ForAndQuantifiedExpr quantifier expression} of type 'some' with one clause     
	 * @param clause First clause of the new quantifier expression
	 * @return An XPath expression of type 'some'
	 */
	public ForAndQuantifiedExpr createSomeExpr(Expr clause);

	/**
	 * Creates a {@link ForAndQuantifiedExpr quantifier expression} of type 'every'  with one clause
	 * @return An XPath expression of type 'every'
	 */
	public ForAndQuantifiedExpr createEveryExpr(Expr clause);

	/**
	 * Creates a {@link OperatorExpr logical expression} of type 'and' with two operands
	 * @param operand1 XPath expression to compose
	 * @param operand2 XPath expression to compose
	 * @return An XPath expression of type 'and'
	 */
	public OperatorExpr createAndExpr(Expr operand1, Expr operand2);

	/**
	 * Creates a {@link OperatorExpr logical expression} of type 'or' with at least two operands
	 * @param operand1 XPath expression to compose
	 * @param operand2 XPath expression to compose
	 * @return An XPath expression of type 'or'
	 */
	public OperatorExpr createOrExpr(Expr operand1, Expr operand2);

	/**
	 * Creates a {@link ConditionalExpr conditional expression}
	 * @param test The XPath expression uses for the test 
	 * @param thenExpr The XPath expression uses in the then clause
	 * @param elseExpr The XPath expression uses in the else clause
	 * @return An XPath expression of type 'if'
	 */
	public ConditionalExpr createIfExpr(
		Expr test,
		Expr thenExpr,
		Expr elseExpr);

	/**
	 * Creates a {@link ForAndQuantifiedExpr 'for' expression}   
	 * @param varName The name of the binding variable
	 * @param clauseExpr The for clause expression
	 * @param quantifiedExpr The content of the for expression
	 * @return An XPath expression of type 'for'
	 */
	public ForAndQuantifiedExpr createForExpr(
		String varName,
		Expr clauseExpr,
		Expr quantifiedExpr);

	/**
	 * Creates a {@link CastableOrCastExpr 'cast as' expression}     
	 * @param type A Single type 
	 * @param expr The XPath expression to cast as     
	 * @return An XPath expression of type cast as
	 */
	public CastableOrCastExpr createCastAsExpr(Expr expr, SingleType type);

	/**
	 * Creates a {@link CastableOrCastExpr 'castable as' expression}     
	 * @param expr The XPath expression to test   
	 * @param type A Single type 
	 * @return An XPath expression of type castable as
	 */
	public CastableOrCastExpr createCastableAsExpr(Expr expr, SingleType type);

	/**
	 * Creates a  {@link TreatExpr} expression.
	 * @param expr The XPath expression to treat as       
	 * @param seqType The treat as type   
	 * @return An XPath expression of type treat as
	 */
	public TreatExpr createTreatAsExpr(Expr expr, SequenceType seqType);

	/**
	 * Creates a {@link Literal literal} of type integer
	 * @return A literal of type integer
	 */
	public Literal createIntegerLiteralExpr(int value);

	/**
	 * Creates a  {@link Literal literal} of type big integer
	 * @param value The big integer     
	 * @return A literal of type integer
	 */
	public Literal createIntegerLiteralExpr(BigInteger value);

	/**
	 * Creates a {@link Literal literal} of type decimal
	 * @return A literal of type decimal
	 */
	public Literal createDecimalLiteralExpr(double value);

	/**
	 * Creates a {@link Literal literal} of type big decimal     
	 * @return A literal of type decimal
	 */
	public Literal createDecimalLiteralExpr(BigDecimal value);

	/**
	 * Creates a {@link Literal literal} of type string
	 * @return A literal of type string
	 */
	public Literal createStringLiteralExpr(String value);

	/**
	 * Creates a {@link Literal literal} of type double
	 * @return A literal of type double
	 */
	public Literal createDoubleLiteralExpr(double value);

	/**
	 * Creates a {@link FunctionCall function call} with the specified qname
	 * and no parameter. 
	 * @param name Qualified name of the function to create 
	 * @return A new function call
	 */
	public FunctionCall createFunctionCall(QName name);

	/**
	 * Creates a {@link Expr context item expression}.
	 * @return A new context item expression
	 */
	public Expr createContextItemExpr();

	/**
	 * Creates a {@link org.apache.xpath.expression.Variable} 
	 * with the specified {@link QName}.
	 * @param name
	 * @return A new variable
	 */
	public Variable createVariable(QName name);

	/**
	 * Creates a axis {@link StepExpr pattern step}
	 * @param axisType
	 * @param nodeTest
	 */
	public StepExpr createPatternStepExpr(short axisType, NodeTest nodeTest);

	/**
	 * Makes a {@link QName} with the specified prefix, namespace 
	 * and local part.
	 * @param ns or null
	 * @param localPart
	 * @param prefix or null
	 * @return A new QName or an existing one with the same
	 * namespace and localpart
	 */
	public QName createQName(String ns, String localPart, String prefix);

	/**
	 * Makes a {@link SingleType} with the specified type name.
	 * @param typeName
	 * @return A new single type
	 */
	public SingleType createSingleType(QName typeName);

	/**
	 * Makes a {@link SequenceType} of the specified kind.
	 * The occurence indicator is set to {@link SequenceType#ONE}.
	 * @param kind
	 * @return A the most generic new sequence type for the specified
	 * kind of type (element(), node(), empty(), etc..).
	 * @throws XPath20Exception whenever the kind value doesn't belong to
	 * one of the constants defined in {@link SequenceType} and whenever
	 * kind hold {@link SequenceType#ATOMIC_TYPE}.
	 */
	public SequenceType createSequenceType(short kind) throws XPath20Exception;

	/**
	 * Makes a {@link SequenceType atomic type} with the specified
	 * type name.
	 * The occurence indicator is set to {@link SequenceType#ONE}.
	 * @param typeName
	 * @return An atomic type
	 */
	public SequenceType createAtomicType(QName typeName);

	
	
}
