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

import org.apache.xml.QName;

import org.apache.xpath.XPathException;
import org.apache.xpath.expression.NodeTest;
import org.apache.xpath.impl.parser.Node;
import org.apache.xpath.impl.parser.SimpleNode;
import org.apache.xpath.impl.parser.XPathTreeConstants;


/**
 *
 */
public class NameTestImpl extends SimpleNode implements NodeTest
{
    /**
     * Name test
     */
    QName m_qname;

    /**
     * Constructor for NameTestImpl.
     *
     * @param i
     */
    public NameTestImpl(int i)
    {
        super(i);
    }

    /**
     * Constructor for NodeTestImpl.
     *
     * @param namespace DOCUMENT ME!
     * @param localpart DOCUMENT ME!
     */
    public NameTestImpl(String namespace, String localpart)
    {
        super(XPathTreeConstants.JJTNAMETEST);

        m_qname = new QName(namespace, localpart);
    }

    /**
     * @see org.apache.xpath.expression.NodeTest#isNameTest()
     */
    public boolean isNameTest()
    {
        return true;
    }

    /**
     * @see org.apache.xpath.expression.NodeTest#isKindTest()
     */
    public boolean isKindTest()
    {
        return false;
    }

    /**
     * @see org.apache.xpath.expression.NodeTest#getKindTest()
     */
    public short getKindTest() throws XPathException
    {
        throw new XPathException("Invalid call of this method on NameTest node"); //I8
    }

    /**
     * @see org.apache.xpath.expression.NodeTest#getLocalNameTest()
     */
    public String getLocalNameTest() throws XPathException
    {
        return m_qname.getLocalPart();
    }

    /**
     * @see org.apache.xpath.expression.NodeTest#getPrefix()
     */
    public String getPrefix() throws XPathException
    {
        return m_qname.getPrefix();
    }

    /**
     * @see org.apache.xpath.expression.Expr#getString(boolean)
     */
    public String getString(boolean abbreviate)
    {
        return m_qname.toString();
    }

    /**
     * @see org.apache.xpath.impl.parser.SimpleNode#canBeFiltered()
     */
    protected boolean canBeFiltered()
    {
        return false;
    }

    /**
     * @see org.apache.xpath.impl.parser.Node#jjtAddChild(Node, int)
     */
    public void jjtAddChild(Node n, int i)
    {
        // don't add n in the tree
        m_qname = ((org.apache.xpath.impl.parser.QName) n).getQName();
    }

    /**
     * @see org.apache.xpath.impl.ExprImpl#getString(StringBuffer,
     *      boolean)
     */
    public void getString(StringBuffer expr, boolean abbreviate)
    {
        expr.append(m_qname.toString());
    }

    /**
     * Override to print out useful instance data.
     *
     * @see org.apache.xpath.impl.parser.SimpleNode#toString()
     */
    public String toString()
    {
        return XPathTreeConstants.jjtNodeName[id] + " " + getClass() + " "
        + getString(false);
    }
}
