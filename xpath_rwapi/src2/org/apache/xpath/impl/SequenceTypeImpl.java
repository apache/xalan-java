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
import org.apache.xpath.impl.parser.Node;
import org.apache.xpath.impl.parser.QName;
import org.apache.xpath.impl.parser.SimpleNode;
import org.apache.xpath.impl.parser.XPath;
import org.apache.xpath.impl.parser.XPathTreeConstants;


/**
 * Default implementation of the SequenceType interface
 */
public class SequenceTypeImpl extends SimpleNode implements SequenceType
{
    short m_itemType;
    short m_occInd;
    String m_type;

    /**
     * DOCUMENT ME!
     *
     * @param i
     */
    public SequenceTypeImpl(int i)
    {
        super(i);
        
		m_occInd = ONE;
    }

    /**
     * DOCUMENT ME!
     *
     * @param p
     * @param i
     */
    public SequenceTypeImpl(XPath p, int i)
    {
        super(p, i);
        
		m_occInd = ONE;
    }

    /* (non-Javadoc)
     * @see org.apache.xpath.datamodel.SequenceType#getItemType()
     */
    public short getItemType()
    {
        return m_itemType;
    }

    /* (non-Javadoc)
     * @see org.apache.xpath.datamodel.SequenceType#getOccurrenceIndicator()
     */
    public short getOccurrenceIndicator()
    {
        return m_occInd;
    }

    /* (non-Javadoc)
     * @see org.apache.xpath.datamodel.SequenceType#getType()
     */
    public String getType()
    {
        return m_type;
    }

    /* (non-Javadoc)
     * @see org.apache.xpath.impl.parser.SimpleNode#getString(java.lang.StringBuffer, boolean)
     */
    public void getString(StringBuffer expr, boolean abbreviate)
    {
        switch (m_itemType)
        {
            case EMPTY_SEQ:
                expr.append("empty");

                break;

            case ELEMENT_ITEM_TYPE:
                expr.append("element");

                if (m_type != null)
                {
                    expr.append(" ").append(m_type);
                }

                break;

            case ATTRIBUTE_ITEM_TYPE:
                expr.append("attribute");

                if (m_type != null)
                {
                    expr.append(" ").append(m_type);
                }

                break;

            case NODE_ITEM_TYPE:
                expr.append("node");

                break;

            case PI_ITEM_TYPE:
                expr.append("processing-instruction");

                break;

            case COMMENT_ITEM_TYPE:
                expr.append("comment");

                break;

            case TEXT_ITEM_TYPE:
                expr.append("text");

                break;

            case DOCUMENT_ITEM_TYPE:
                expr.append("document");

                break;

            case ITEM_ITEM_TYPE:
                expr.append("item");

                break;

            case ATOMIC_ITEM_TYPE:
                expr.append(m_type);

                break;
        }

        if (m_itemType != EMPTY_SEQ)
        {
            switch (m_occInd)
            {
                case ZERO_OR_MORE:
                    expr.append("*");

                    break;

                case ONE_OR_MORE:
                    expr.append("+");

                    break;

                case ZERO_OR_ONE:
                    expr.append("?");

                    break;
                    
                    default: // ONE
                    // nothing
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.xpath.impl.parser.Node#jjtAddChild(org.apache.xpath.impl.parser.Node, int)
     */
    public void jjtAddChild(Node n, int i)
    {
        switch (n.getId())
        {
            case XPathTreeConstants.JJTEMPTY:
                m_itemType = EMPTY_SEQ;

                break;

            case XPathTreeConstants.JJTELEMENTTYPE:
                m_itemType = ELEMENT_ITEM_TYPE;

                break;

            case XPathTreeConstants.JJTATTRIBUTETYPE:
                m_itemType = ATTRIBUTE_ITEM_TYPE;

                break;

            case XPathTreeConstants.JJTATOMICTYPE:
                m_itemType = ATOMIC_ITEM_TYPE;

				// Parse the substree to get the type as a string
				// TODO:
				m_type = "xs:integer";
                break;

            case XPathTreeConstants.JJTNODE:
                m_itemType = NODE_ITEM_TYPE;

                break;

            case XPathTreeConstants.JJTPROCESSINGINSTRUCTION:
                m_itemType = PI_ITEM_TYPE;

                break;

            case XPathTreeConstants.JJTCOMMENT:
                m_itemType = COMMENT_ITEM_TYPE;

                break;

            case XPathTreeConstants.JJTTEXT:
                m_itemType = TEXT_ITEM_TYPE;

                break;

            case XPathTreeConstants.JJTDOCUMENT:
                m_itemType = DOCUMENT_ITEM_TYPE;

                break;

            case XPathTreeConstants.JJTITEM:
                m_itemType = ITEM_ITEM_TYPE;

                break;

            case XPathTreeConstants.JJTMULTIPLY:
                m_occInd = ZERO_OR_MORE;

                break;

            case XPathTreeConstants.JJTQMARK:
                m_occInd = ZERO_OR_ONE;

                break;

            case XPathTreeConstants.JJTPLUS:
                m_occInd = ONE_OR_MORE;

                break;
            
        }
    }
}
