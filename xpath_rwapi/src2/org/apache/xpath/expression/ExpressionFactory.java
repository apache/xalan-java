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


/**
 * Constructors for XPath expressions. 
 * <p>Most of the constructors creates expression fragments (the top level expression is <em>not</em>
 * a sequence). In order to obtain a valid expression, create a {@link #createSequence() sequence}
 * and {@link OperatorExpr#append(OperatorExpr) append} the expression fragment to it.</p> 
 * @author <a href="mailto:villard@us.ibm.com">Lionel Villard</a>
 * @version $Id$
 */
public interface ExpressionFactory
{
    /**
     * Creates a new XPath/XQuery expression from a string representation.
     * <p>For XPath expression, default element and function namespaces are
     * used to resolve prefixes.
     * Various checking are not performed when the expression is built, 
     * like the existence test of variable declaration.</p>
     * @return A XPath expression
     * @throws XPath20Exception whenever the specified expression is not valid 
     * syntaxically or semantically.
     */
    public Expr createExpr(String expr) throws XPath20Exception;
    
	/**
     * Creates a new XPath expression from a string representation.
	 * Use the specified static context to resolve namespaces and perform
	 * various static type checking.
	 * @return A XPath expression
	 * @throws XPath20Exception whenever the specified expression is not valid 
	 * syntaxically or semantically.
	 */
	public Expr createExpr(StaticContext ctx, String expr) throws XPath20Exception;

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
     * Creates a new axis {@link StepExpr} step from the specified axis type and node test.
     * It contains no predicate.     
     * @return A {@link StepExpr}
     */
    public StepExpr createStepExpr(short axisType, NodeTest nodeTest);

    /**
     * Creates a name test.
     * @return A name test
     */
    public NodeTest createNameTest(QName qname);

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
    public ConditionalExpr createIfExpr(Expr test, Expr thenExpr, Expr elseExpr);

    /**
     * Creates a {@link ForAndQuantifiedExpr 'for' expression}   
     * @param varName The name of the binding variable
     * @param clauseExpr The for clause expression
     * @param quantifiedExpr The content of the for expression
     * @return An XPath expression of type 'for'
     */
    public ForAndQuantifiedExpr createForExpr(String varName, Expr clauseExpr,
        Expr quantifiedExpr);

    /**
     * Creates a {@link CastableAsExpr 'cast as' expression}     
     * @param seqType The cast as type
     * @param parExpr The XPath expression to cast as     
     * @return An XPath expression of type cast as
     */
    public CastOrTreatAsExpr createCastAsExpr(SequenceType seqType,
        OperatorExpr parExpr);

    /**
     * Creates a  {@link CastOrTreatAsExpr 'treat as' expression}     
     * @param seqType The treat as type
     * @param parExpr The XPath expression to treat as     
     * @return An XPath expression of type treat as
     */
    public CastOrTreatAsExpr createTreatAsExpr(SequenceType seqType,
        OperatorExpr parExpr);

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
     * @return
     */
    public FunctionCall createFunctionCall(QName name);
  
  	/**
  	 * Creates a {@link QName qname} with the specified prefix, namespace 
  	 * and local part.
  	 * @param ns or null
  	 * @param localPart
  	 * @param prefix or null
  	 * @return A new QName or an existing one with the same
  	 * namespace and localpart
  	 */
  	public QName createQName(String ns, String localPart, String prefix);
    
	/**
	 * Creates a {@link LetExpr} with one initial clause
	 * @param varname of the initial clause
	 * @param type of the variable (can be null)
	 * @param expr of the initial clause
	 * @return
	 */
	public LetExpr createLetExpr(QName varname, SequenceType type, Expr expr);
	
}
