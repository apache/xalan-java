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

import org.apache.xpath.XPath20Exception;

/**
 * Represents <em>step</em> expressions. 
 * A step is either a <em>axis step</em> or an <em>filter step</em>.
 * @see <a href="http://www.w3.org/TR/xpath20/#id-axis-steps">XPath 2.0
 *      specification</a>
 * @author <a href="mailto:villard@us.ibm.com">Lionel Villard</a>
 * @version $Id$
 */
public interface StepExpr extends Expr
{
	/**
	 * Full name of axis. This array is kept in synchronization with axis
	 * constants.
	 */
	static final String[] FULL_AXIS_NAME =
		{
			"",
			"child",
			"descendant",
			"parent",
			"attribute",
			"self",
			"descendant-or-self",
			"ancestor",
			"following-sibling",
			"preceding-sibling",
			"following",
			"preceding",
			"namespace",
			"ancestor-or-self" };

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
	 * Tells whether this step is a forward axis step.
	 * Includes the follwing axis: 
	 * <ul>
	 * <li>{@link #AXIS_CHILD}</li>
	 * <li>{@link #AXIS_DESCENDANT}</li>
	 * <li>{@link #AXIS_DESCENDANT_OR_SELF}</li>
	 * <li>{@link #AXIS_ATTRIBUTE}</li>
	 * <li>{@link #AXIS_SELF}</li>
	 * <li>{@link #AXIS_FOLLOWING}</li>
	 * <li>{@link #AXIS_FOLLOWING_SIBLING}</li>
	 * <li>{@link #AXIS_NAMESPACE}</li> 
	 * </ul>
	 * @return true whenever {@link #getAxisType()} returns one the 
	 * constants right above.
	 */
	boolean isForwardStep();

	/**
	 * Tells whether this step is a reversed axis step.
	 * Includes the following axis: 
	 * <ul>
	 * <li>{@link #AXIS_PARENT}</li>
	 * <li>{@link #AXIS_ANCESTOR}</li> 
	 * <li>{@link #AXIS_PRECEDING}</li>
	 * <li>{@link #AXIS_PRECEDING_SIBLING}</li>
	 * <li>{@link #AXIS_ANCESTOR_OR_SELF}</li>
	 * </ul> 
	 * @return true whenever {@link #getAxisType()} returns one the 
	 * constants right above.
	 */
	boolean isReversedStep();

	/**
	 * Tells whether this step is a filter step
	 * @return boolean
	 */
	boolean isFilterStep();

	/**
	 * Gets the type of step axis
	 * @return short The axis type corresponding to one of the constants defined above.
	 * @throws XPath20Exception whenever the step isn't an axis step
	 */
	short getAxisType() throws XPath20Exception;

	/**
	 * Sets the type of the step axis
	 * @param newType The new axis type
	 * @throws XPath20Exception whenever the step isn't an axis step
	 */
	void setAxisType(short newType) throws XPath20Exception;

	/**
	 * Gets the name of the step axis
	 * @return String Full name of the step axis
	 * @throws XPath20Exception whenever the step isn't an axis step
	 */
	String getAxisName() throws XPath20Exception;

	/**
	 * Gets the node test 
	 * @return NodeTest
	 * @throws XPath20Exception whenever the step isn't an axis step
	 */
	NodeTest getNodeTest() throws XPath20Exception;

	/**
	 * Sets the node test 
	 * @param NodeTest
	 * @throws XPath20Exception whenever the step isn't an axis step
	 */
	void setNodeTest(NodeTest test) throws XPath20Exception;

	/**
	 * Gets the primary expression of the filter step. 
	 * @return Expr The primary expression 
	 * @throws XPath20Exception whenever the step isn't a filter step
	 */
	Expr getPrimaryExpr() throws XPath20Exception;

	/**
	 * Gets the predicate expression at the specified position
	 * @param i index of the predicate to return
	 * @return The predicate at the ith position
	 * @throws java.lang.ArrayIndexOutOfBoundsException
	 */
	Expr getPredicateAt(int i);

	/**
	 * Gets the number of predicate
	 * @return The number of predicates
	 */
	int getPredicateCount();

	/**
	 * Append the specified predicate at the end of the list of
	 * predicates
	 * @param predicate The predicate to append
	 */
	void appendPredicate(Expr predicate);

	/**
	 * Remove the specified predicate
	 */
	void removePredicate(Expr predicate);
}
