/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
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
package org.apache.xml.xdm;

import org.apache.xml.utils.XMLString;

/**
 * <code>XDMNodeVisitor</code> defines a set of behaviors to be
 * applied as an <code>XDMTreeWalker</code> scans a subtree. You
 * can think of it as a varient on the SAX event handlers,
 * designed to accept <code>XDMCursor</code>s and read
 * information from their current node as required (rather
 * than expecting to have the information extracted and passed
 * as parameters). This should be more flexible, and more efficient,
 * given that we're traversing a model already in memory.
 * 
 * NOTE: XDMCursors may change which node they're currently pointing
 * at. If your visitor needs to store a reference to a specific
 * node, it's your responsibility to call XDMCursor.cloneCursor()
 * or XDMCursor.getSingleNode() or whatever.
 * 
 * %REVIEW% Namespaces are an issue. I don't like it. Can we
 * dodge the bullet, or must we Do Something?
 * 
 * @see XDMCursor.getTreeWalker()
 * @see XDMTreeWalker.walk()
 * */
public interface XDMNodeVisitor
{
	// ======== FACTORIES ========
	// Visitors will be specific to particular tasks to be
	// performed -- serializing, tree copying, etc --
	// so I don't think we're going to have any central
	// factories.
	
	// ======== BEHAVIOR =========
    
    /**
     * Receive notification of the beginning of a document.
     *
     * @param docNode XDMCursor whose current node (at the time
     * this method is invoked) is the Document -- or, possibly,
     * DocumentFragment. (Should that be a separate case?)
     */
    public void startDocument (XDMCursor docNode);
    
    
    /**
     * Receive notification of the end of a document.
     *
     * @param docNode XDMCursor whose current node (at the time
     * this method is invoked) is the Document -- or, possibly,
     * DocumentFragment. (Should that be a separate case?)
     */
    public void endDocument (XDMCursor docNode);
        
    
    /**
     * Receive notification of the beginning of an element.
     * 
     * %REVIEW% Should we always make folks explicitly retrieve
     * attributes/namespace axis cursors, or should the
     * walker yield them and let visitors ignore those events
     * if they'd rather explicitly retrieve (or not retrieve
     * at all)?
     *
     * @param element XDMCursor whose current node (at the time
     * this method is invoked) is the Element we have just entered.
     * @see #endElement
     */
    public void startElement (XDMCursor element);
    
    /**
     * Receive notification of the end of an element.
     *
     * @param element XDMCursor whose current node (at the time
     * this method is invoked) is the Element we are about to
     * leave.
     */
    public void endElement (XDMCursor element);    
    
    /**
     * Receive notification of a text node.
     *
     * @param text XDMCursor whose current node (at the time
     * this method is invoked) is the Text node.
     * @see org.xml.sax.Locator
     */
    public void text (XDMCursor text);    
    
    /**
     * Receive notification of a processing instruction.
     *
     * @param pi XDMCursor whose current node (at the time
     * this method is invoked) is the PI node.
     */
    public void processingInstruction (XDMCursor pi);

    /**
     * Receive notification of a comment.
     *
     * @param pi XDMCursor whose current node (at the time
     * this method is invoked) is the comment node.
     */
    public void comment (XDMCursor pi);

	/** Tell the XDMTreeWalker whether we want to recieve events
	 * for Attribute nodes. (If we don't, we can still request
	 * them by getting an attr-axis cursor from the Element...).
	 * This call may be issued only once, and should probably be
	 * returning a constant for any given walker.
	 * 
	 * (I wasn't sure whether attributes should be passed
	 * as events; this is my compromise until I decide. We
	 * could make this a more general whatToShow-style
	 * mask, or add calls for gating the other events -- or add
	 * that/other filtering to the XDMTreeWalker, which might
	 * be the simpler answer if that much control is really desired.)
	 * 
	 * @return true iff attribute() events should be passed to
	 * this visitor.
	 * */
	public boolean wantsAttributeEvents();

    /**
     * Receive notification of an Attribute node.
     *
     * @param attr XDMCursor whose current node (at the time
     * this method is invoked) is the Attr.
     */
    public void attribute (XDMCursor attr);
    
    /**
     * Receive notification of a new/local Namespace declaration.
	 *
	 * %REVIEW% Namespace nodes are Going Away in XPath 2.0, 
	 * so I don't really want to deliver these as cursors.
	 * I also don't really want to report all in scope, even though
	 * that's what the XPath 1.0 model expects.
	 * 
	 * Simplest answer may be to let the specific visitor
	 * implementation ask the Element for its namespaces
	 * if/when it wants them... or just deduce requirements
	 * from the usage on other nodes as they go by, though that
	 * would miss prefixes used only within strings or not
	 * used at all.
	 * 
	 * UGH.
     * */
    /* DEFERRED: public void namespace_declaration (XDMCursor nsdecl); */
    
}
