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
package org.apache.xpath.impl;

import org.apache.xpath.datamodel.SequenceType;
import org.apache.xpath.expression.Expr;
import org.apache.xpath.expression.InstanceOfExpr;
import org.apache.xpath.expression.Visitor;
import org.apache.xpath.impl.parser.Node;
import org.apache.xpath.impl.parser.SimpleNode;
import org.apache.xpath.impl.parser.XPath;

/**
 * Default implementation of instanceof expression.
 */
public class InstanceOfExprImpl extends ExprImpl implements InstanceOfExpr {

	/**
	 * Constructor for InstanceOfExprImpl.
	 * @param i
	 */
	public InstanceOfExprImpl(int i) {
		super(i);
		
		children = new Node[2];
	}

	/**
	 * Constructor for InstanceOfExprImpl.
	 * @param p
	 * @param i
	 */
	public InstanceOfExprImpl(XPath p, int i) {
		super(p, i);
		
		children = new Node[2];
	}
	
	/**
	 * Constructor for cloning	 
	 */
	private InstanceOfExprImpl(InstanceOfExprImpl expr) {
		super(expr.id);
		
		children = new Node[2];
		children[0] = (Node) expr.getTestedExpr().cloneExpression();
		children[1] = (Node) expr.getSequenceType();
	}

	/**
	 * @see org.apache.xpath.expression.InstanceOfExpr#getSequenceType()
	 */
	public SequenceType getSequenceType() {
		return (SequenceType) children[1];
	}

	/**
	 * @see org.apache.xpath.expression.InstanceOfExpr#getTestedExpr()
	 */
	public Expr getTestedExpr() {
		return (Expr) children[0];
	}

	/**
	 * @see org.apache.xpath.expression.Expr#getExprType()
	 */
	public short getExprType() {
		return INSTANCE_OF_EXPR;
	}

	/**
	 * @see org.apache.xpath.expression.Expr#cloneExpression()
	 */
	public Expr cloneExpression() {
		return new InstanceOfExprImpl(this);
	}

	/**
	 * @see org.apache.xpath.expression.Visitable#visited(Visitor)
	 */
	public void visited(Visitor visitor) {
		visitor.visitInstanceOf(this);
	}
    
    /**
     * @see org.apache.xpath.impl.parser.Node#jjtAddChild(Node, int)
     */
    public void jjtAddChild(Node n, int i) {
        if (((SimpleNode) n).canBeReduced()) {
            super.jjtAddChild(n.jjtGetChild(0), i);
        } else {
             super.jjtAddChild(n, i);
        }
    }
    
    
	/* (non-Javadoc)
	 * @see org.apache.xpath.impl.parser.SimpleNode#getString(java.lang.StringBuffer, boolean)
	 */
	public void getString(StringBuffer expr, boolean abbreviate) {
		((ExprImpl) getTestedExpr()).getString(expr, abbreviate);
		expr.append(" instance of ");
		((SimpleNode) getSequenceType()).getString(expr, abbreviate);
	}

}
