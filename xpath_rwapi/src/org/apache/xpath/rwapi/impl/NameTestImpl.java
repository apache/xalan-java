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
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xpath.rwapi.impl;

import org.apache.xpath.rwapi.XPathException;
import org.apache.xpath.rwapi.expression.Expr;
import org.apache.xpath.rwapi.expression.NodeTest;
import org.apache.xpath.rwapi.impl.parser.Node;
import org.apache.xpath.rwapi.impl.parser.QName;
import org.apache.xpath.rwapi.impl.parser.Token;
import org.apache.xpath.rwapi.impl.parser.XPathTreeConstants;

/**
 *
 */
public class NameTestImpl extends ExprImpl implements NodeTest {

    String m_localPart;
    String m_prefix;
    
 
	/**
	 * Constructor for NameTestImpl.
	 * @param i
	 */
	public NameTestImpl(int i) {
		super(i);
	}

    /**
     * Constructor for NodeTestImpl.
     * @param p
     * @param i
     */
    public NameTestImpl(String localPart, String prefix) {
        super(XPathTreeConstants.JJTNAMETEST);
        m_localPart = localPart;
        m_prefix = prefix;
    }

	/**
	 * @see org.apache.xpath.rwapi.expression.NodeTest#isNameTest()
	 */
	public boolean isNameTest() {
		return true;
	}

	/**
	 * @see org.apache.xpath.rwapi.expression.NodeTest#isKindTest()
	 */
	public boolean isKindTest() {
		return false;
	}

	/**
	 * @see org.apache.xpath.rwapi.expression.NodeTest#getKindTest()
	 */
	public short getKindTest() throws XPathException {
        throw new XPathException("Invalid call of this method on NameTest node"); //I8
	}

	/**
	 * @see org.apache.xpath.rwapi.expression.NodeTest#getLocalNameTest()
	 */
	public String getLocalNameTest() throws XPathException {
		return m_localPart;
	}

	/**
	 * @see org.apache.xpath.rwapi.expression.NodeTest#getPrefix()
	 */
	public String getPrefix() throws XPathException {
		return m_prefix;
	}

	/**
	 * @see org.apache.xpath.rwapi.expression.Expr#getExprType()
	 */
	public short getExprType() {
		return Expr.NAMETEST_EXPR;
	}

	/**
	 * @see org.apache.xpath.rwapi.expression.Expr#cloneExpression()
	 */
	public Expr cloneExpression() {
		return new NameTestImpl(m_localPart, m_prefix);
	}

	/**
	 * @see org.apache.xpath.rwapi.expression.Expr#getString(boolean)
	 */
	public String getString(boolean abbreviate) {
		return (m_prefix != null) ? m_prefix + ":" + m_localPart : m_localPart;
	}
	
	/**
	 * @see org.apache.xpath.rwapi.impl.parser.SimpleNode#processToken(Token)
	 */
	public void processToken(Token token) {
            		
	}

	/**
	 * @see org.apache.xpath.rwapi.impl.parser.SimpleNode#canBeFiltered()
	 */
	protected boolean canBeFiltered() {
		return false;
	}

	/**
	 * @see org.apache.xpath.rwapi.impl.parser.Node#jjtAddChild(Node, int)
	 */
	public void jjtAddChild(Node n, int i) {
        // don't add n in the tree
       m_localPart = ((QName) n).getLocalPart();        
       m_prefix = ((QName) n).getPrefix();        
	}

	/**
	 * @see org.apache.xpath.rwapi.impl.ExprImpl#getString(StringBuffer, boolean)
	 */
	protected void getString(StringBuffer expr, boolean abbreviate) {
        if ( m_prefix != null ) {
    		expr.append(m_prefix).append(":").append(m_localPart);
        } else {
            expr.append(m_localPart);
        }
	}

}
