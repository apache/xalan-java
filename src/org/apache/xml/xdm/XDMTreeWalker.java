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
 * <code>XDMTreeWalker</code> is intended to handle the cases where
 * we are processing a complete XDM subtree -- typically, serializing
 * or copying it -- and must be aware of both entering AND EXITING
 * some nodes (Elements and Documents), so a simple document-order
 * iteration will not suffice.
 * 
 * These tasks could be performed with XDMCursors, but
 * would require a number of them to handle the various axes
 * (namespace nodes, attributes, children), and would require
 * re-creating the tree-walk logic in each instance.
 * 
 * So instead, we've encapsulated the tree-walk in a single object,
 * and provided a "visitor" mechanism which lets the caller plug in
 * the behavior to be applied at each step of the walk. 
 * 
 * Implementation hint: An XDMTreeWalker object may want to
 * implement XDMCursor as well, so it can simply pass itself
 * to the XDMNodeVisitor. Or maintain a resettable single-node
 * cursor and pass that, if you want a slightly more modular
 * solution. (This is strictly an internal detail, of course;
 * the important point is that the visitor must not assume it
 * can store the cursor and have it continue to refer to the
 * same node. As always, if you want to refer back to it,
 * clone it.)
 * 
 * @see XDMCursor.getTreeWalker()
 * @see XDMNodeVisitor
 * */
public interface XDMTreeWalker
{
	// ======== FACTORIES ========
	// XDMTreeWalker implementations will probably want to be
	// specific to particular back-end data models, for efficiency
	// reasons. I'm currently proposing that we handle this via
	// XDMCursor.getTreeWalker(), qv.
	
	
	// ======== (RE)INITIALIZATION ========
	
	/** Reset the TreeWalkers's root node (starting point).
	 * This should only be invoked between walks! 
	 * 
	 * %REVIEW% Only needed if we want to reuse TreeWalkers.
	 * The fact that they're bound to specific documents may make
	 * this call less than useful.
	 * 
	 * @param newroot XDMCursor specifying the new root node.
	 * @return this (for convenience), appropriately reset
	 * @throws [something appropriate] if the new root is not
	 * within the same document as the previous root.
	 * */
	public XDMTreeWalker setRootNode(XDMCursor newroot);
	
	// ======== INVOCATION ========
	
	/** Walk the tree, reporting node transitions to the
	 * visitor.
	 * 
	 * %REVIEW% Should the visitor be a parameter to walk(),
	 * or bound more permanantly? (Or should we support both,
	 * for convenience?)
	 * 
	 * %REVIEW% Should the walker be immediately ready to re-walk
	 * the same tree after this has finished, or should users have
	 * to re-initialize it? I lean toward the former.
	 * 
	 * %REVIEW% Should we return anything?
	 * 
	 * %REVIEW% Should we support any filtering, versus
	 * just having the visitor ignore what it doesn't need?
	 * 
	 * @param behavior XDMNodeVisitor to be applied to each
	 * node as we enter and/or exit. 
	 * */
	public void walk(XDMNodeVisitor behavior);
}
