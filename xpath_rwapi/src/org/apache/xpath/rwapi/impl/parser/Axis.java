/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights 
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
package org.apache.xpath.rwapi.impl.parser;

import org.apache.xpath.rwapi.expression.StepExpr;

/**
 *
 */
public class Axis extends SimpleNode {

    static final protected Axis AXIS_CHILD = new Axis(XPathTreeConstants.JJTAXISCHILD, StepExpr.AXIS_CHILD);
    static final protected Axis AXIS_ATTRIBUTE = new Axis(XPathTreeConstants.JJTAXISATTRIBUTE , StepExpr.AXIS_ATTRIBUTE);
    static final protected Axis AXIS_DESCENDANT = new Axis(XPathTreeConstants.JJTAXISDESCENDANT, StepExpr.AXIS_DESCENDANT);
    static final protected Axis AXIS_SELF = new Axis(XPathTreeConstants.JJTAXISSELF, StepExpr.AXIS_SELF);
    static final protected Axis AXIS_DESCENDANTORSELF = new Axis(XPathTreeConstants.JJTAXISDESCENDANTORSELF, StepExpr.AXIS_DESCENDANT_OR_SELF);
    static final protected Axis AXIS_FOLLOWINGSIBLING = new Axis(XPathTreeConstants.JJTAXISFOLLOWINGSIBLING, StepExpr.AXIS_FOLLOWING_SIBLING);
    static final protected Axis AXIS_FOLLOWING = new Axis(XPathTreeConstants.JJTAXISFOLLOWING, StepExpr.AXIS_FOLLOWING);
    static final protected Axis AXIS_NAMESPACE = new Axis(XPathTreeConstants.JJTAXISNAMESPACE, StepExpr.AXIS_NAMESPACE);
    static final protected Axis AXIS_PARENT = new Axis(XPathTreeConstants.JJTAXISPARENT, StepExpr.AXIS_PARENT);
    static final protected Axis AXIS_ANCESTOR = new Axis(XPathTreeConstants.JJTAXISANCESTOR, StepExpr.AXIS_ANCESTOR);
    static final protected Axis AXIS_PRECEDINGSIBLING = new Axis(XPathTreeConstants.JJTAXISPRECEDINGSIBLING, StepExpr.AXIS_PRECEDING_SIBLING);
    static final protected Axis AXIS_PRECEDING = new Axis(XPathTreeConstants.JJTAXISPRECEDING, StepExpr.AXIS_PRECEDING);
    static final protected Axis AXIS_ANCESTORORSELF = new Axis(XPathTreeConstants.JJTAXISANCESTORORSELF, StepExpr.AXIS_ANCESTOR_OR_SELF);

    short m_axis;

	/**
	 * Constructor for Axis.
	 * @param i
	 */
	private Axis(int i) {
		super(i);
	}

	/**
	 * Constructor for Axis.
	 * @param p
	 * @param i
	 */
	private Axis(XPath p, int i) {
		super(p, i);
	}
    
    /**
     * Constructor for Axis.
     * @param p
     * @param i
     */
    private Axis(int id, short axis) {
        super(id);
        m_axis = axis;
    }
    
    /**
     * 
     */
    public short getAxis() {
      return m_axis;
    }
    
    /**
     * 
     */
    public int getId() {
        return id;
    }

	

}
