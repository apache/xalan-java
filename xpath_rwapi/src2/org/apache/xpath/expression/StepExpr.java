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

import org.apache.xpath.XPathException;


/**
 * Represents step.
 * <pre>
 * [20]   StepExpr   ::=   (ForwardStep |  ReverseStep |  Literal) Predicates
 * [39]   ForwardStep   ::=   (ForwardAxis NodeTest) |  AbbreviatedForwardStep
 * [40]   ReverseStep   ::=   (ReverseAxis NodeTest) |  AbbreviatedReverseStep
 * </pre>
 *
 * @see <a href="http://www.w3.org/TR/xpath20/#id-axis-steps">XPath 2.0
 *      specification</a>
 */
public interface StepExpr extends Expr
{
    /**
     * Full name of axis. This array is kept in synchronization with axis
     * constants.
     */
    static final String[] FULL_AXIS_NAME = 
                                           {
                                               "", "child", "descendant",
                                               "parent", "attribute", "self",
                                               "descendant-or-self", "ancestor",
                                               "following-sibling",
                                               "preceding-sibling", "following",
                                               "preceding", "namespace",
                                               "ancestor-or-self"
                                           };

    /**
     * The step axis is child
     */
    static final short AXIS_CHILD = 1;

    /**
     * The step axis is descendant
     */
    static final short AXIS_DESCENDANT = 2;

    /**
     * The step axis is parent
     */
    static final short AXIS_PARENT = 3;

    /**
     * The step axis is attribute
     */
    static final short AXIS_ATTRIBUTE = 4;

    /**
     * The step axis is self
     */
    static final short AXIS_SELF = 5;

    /**
     * The step axis is descendant or self
     */
    static final short AXIS_DESCENDANT_OR_SELF = 6;

    /**
     * The step axis is ancestor
     */
    static final short AXIS_ANCESTOR = 7;

    /**
     * The step axis is following sibling
     */
    static final short AXIS_FOLLOWING_SIBLING = 8;

    /**
     * The step axis is preceding sibling
     */
    static final short AXIS_PRECEDING_SIBLING = 9;

    /**
     * The step axis is following
     */
    static final short AXIS_FOLLOWING = 10;

    /**
     * The step axis is preceding
     */
    static final short AXIS_PRECEDING = 11;

    /**
     * The step axis is namespace
     */
    static final short AXIS_NAMESPACE = 12;

    /**
     * The step axis is ancestor or self
     */
    static final short AXIS_ANCESTOR_OR_SELF = 13;

    /**
     * Returns true when the step is a forward test
     *
     * @return boolean
     */
    boolean isForwardStep();

    /**
     * Returns true when the step is a reverse test
     *
     * @return boolean
     */
    boolean isReversedStep();

    /**
     * Tell whether this step is a primary expression.
     *
     * @return boolean
     */
    boolean isPrimaryExpr();

    /**
     * Gets the type of step axis
     *
     * @return short The axis type corresponding to one of the constants defined above.
     * @throws XPathException whenever the step is neither a forward step nor a reverse
     *         step.
     */
    short getAxisType() throws XPathException;

    /**
     * Sets the type of the step axis
     * @param newType The new axis type
     * @throws XPathException whenever the step is not a forward or reverse
     *         step.
     */
    void setAxisType(short newType) throws XPathException;

    /**
     * Gets the name of the step axis
     *
     * @return String Full name of the step axis
     * @throws XPathException whenever the step is not a forward or reverse
     *         step.
     */
    String getAxisName() throws XPathException;

    /**
     * Gets the node test 
     *
     * @return NodeTest
     * @throws XPathException whenever the step is not a forward or reverse
     *          step.
     */
    NodeTest getNodeTest() throws XPathException;

    /**
     * Gets the step as a primary expression. 
     *
     * @return Expr The primary expression 
     */
    Expr getPrimaryExpr() throws XPathException;

    /**
     * Gets the predicate expression at the specified position
     * @param i index of the predicate to return
     * @return The predicate at the ith position
     * @throws java.lang.ArrayIndexOutOfBoundsException
     */
    Expr getPredicateAt(int i);

    /**
     * Gets the number of predicate
     *
     * @return The number of predicates
     */
    int getPredicateCount();

    /**
     * Append the specified predicate
     * @param predicate The predicate to append
     */
    void appendPredicate(Expr predicate);

    /**
     * Remove the specified predicate
     */
    void removePredicate(Expr predicate);

    /**
     * Clone this step
     * @return A clone of this step
     */
    StepExpr cloneStep();
}
