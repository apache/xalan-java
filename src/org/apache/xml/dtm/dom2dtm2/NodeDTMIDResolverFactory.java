/*
 * The Apache Software License, Version 1.1
 *
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
package org.apache.xml.dtm.dom2dtm2;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/** Returns an object which can map a Node in a specific DOM into a 
 * unique-within-document identifying integer. Different
 * DOMs may require different solutions, so most of the
 * actual behavior will occur in subclasses; the factory
 * method <code>createResolverForDOM</code> can be used to
 * (attempt to?) automatically select an appropriate
 * implementation.
 * 
 * @author keshlam
 * @since Sep 6, 2002
 */
public abstract class NodeDTMIDResolverFactory
{
	/** FACTORY METHOD: Obtain a resolver appropriate for a
	 * specific DOM implementation. This may look at anything
	 * from class/interface ancestry to isSupported() calls to
	 * behavioral probes.
	 * 
	 * @param domimpl The DOMImplementation object for this DOM.
	 * If null, we will attempt to retrieve it from the document.
	 * 
	 * @param doc The root node for a specific DOM in this
	 * implementation. If null, we may have to create (and then
	 * discard) a temporary document as part of our analysis...
	 * but this is problematic, since an implementation may deliver
	 * different classes, with different behaviors, depending on the
	 * doctype and root element instantiated.
	 * 
	 * @return An instance of NodeDTMIDResolverFactory which we hope
	 * will be suitable for use with this DOM, or <code>null</code>
	 * if we haven't a clue.
	 * */
	public static NodeDTMIDResolver createResolverForDOM
		(DOMImplementation domimpl, Node node)
	{
		if (domimpl==null && node==null)
			return null; // Can't do anything useful!
			
		// This is problematic, as discussed above
		Document doc;
		if(node==null)
			doc=domimpl.createDocument(null,"dummy",null);
		else
		{
			// Go from general node to its Document node.
			// Unfortunately Document.getOwnerDocument() is null...
			doc=node.getOwnerDocument();
			if(doc==null) doc=(Document)node;
		}
	
		if (domimpl==null)
			domimpl=doc.getImplementation();
			
		// Start seeking mapper, in order of preference
		NodeDTMIDResolver resolver=null;
	
		// Try Xerces-specific (preview DOM3)
		resolver=NodeDTMIDResolver_xerces.getInstance(domimpl,doc);
		
		if(resolver==null)
		{	
			// DOM3-specific? Requires DOM3 in classpath, or some
			// reflection magic.
			
			// Consider a last-ditch isSameNode search.
			
			// Consider a last-last-ditch "In this DOM, we happen to
			// know that object identity equals node identity" search.
		
			// We've run out of good ideas
		}
		return resolver;
	}
}

