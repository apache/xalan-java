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
package org.apache.xml.dtm.dom2dtm2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Vector;

import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.dom.DOMSource;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMAxisTraverser;
import org.apache.xml.dtm.DTMException;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.DTMSequence;
import org.apache.xml.dtm.DTMWSFilter;
import org.apache.xml.dtm.ref.DTMManagerDefault;
import org.apache.xml.dtm.ref.ExpandedNameTable;
import org.apache.xml.dtm.ref.DTMNodeProxy;
import org.apache.xml.utils.BoolStack;
import org.apache.xml.utils.FastStringBuffer;
import org.apache.xml.utils.StringBufferPool;
import org.apache.xml.utils.SparseVector;
import org.apache.xml.utils.SuballocatedIntVector;
import org.apache.xml.utils.XMLString;
import org.apache.xml.utils.XMLStringFactory;
import org.apache.xml.utils.NodeVector;
import org.apache.xpath.objects.XSequence;
import org.w3c.dom.*;
import org.xml.sax.ContentHandler;
import org.apache.xml.dtm.ref.dom2dtm.DOM2DTM;
import org.apache.xml.dtm.ref.dom2dtm.DOM2DTMdefaultNamespaceDeclarationNode;
import org.apache.xml.utils.TreeWalker;
import org.apache.xml.utils.XMLCharacterRecognizer;

/**
 * The <code>DTMDefaultBase</code> class serves as a helper base for DTMs.
 * It sets up structures for navigation and type, while leaving data
 * management and construction to the derived classes.
 */
public abstract class DOM2DTM2Base implements DTM
{
	static boolean JJK_DEBUG=false;
	
	// For xml:
	static final String NAMESPACE_URI_XML="http://www.w3.org/XML/1998/namespace";

	// For xmlns:
	static final String NAMESPACE_URI_XMLNS="http://www.w3.org/2000/xmlns/";
	
	
	/** DOM Root of this document */
	protected Node m_root;
	
	/** Tool for mapping DOM to node identities */
	protected org.apache.xml.dtm.dom2dtm2.NodeDTMIDResolver m_resolver;
	
	/** Base URI for document */
	protected String m_documentBaseURI;
	
	/** Is indexing in use -- Currently unsupported */
	protected boolean m_indexing=false;

  /**
   * The DTM manager who "owns" this DTM.
   */
  protected DTMManager m_mgr;
  /**
   * m_mgr cast to DTMManagerDefault, or null if it isn't an instance
   * (Efficiency hook)
   */
  protected DTMManagerDefault m_mgrDefault=null;

  /** The document identity number(s). If we have overflowed the addressing
   * range of the first that was assigned to us, we may add others. */
  protected SuballocatedIntVector m_dtmIdent=new SuballocatedIntVector();

  /**
   * The whitespace filter that enables elements to strip whitespace or not.
   */
  protected DTMWSFilter m_wsfilter;

  /** Flag indicating whether to strip whitespace nodes */
  protected boolean m_shouldStripWS = false;

  /** Stack of flags indicating whether to strip whitespace nodes */
  protected BoolStack m_shouldStripWhitespaceStack;

  /** The XMLString factory for creating XMLStrings. */
  protected XMLStringFactory m_xstrf;

  /**
   * The table for exandedNameID lookups.  This may or may not be the same
   * table as is contained in the DTMManagerDefault.
   */
  protected ExpandedNameTable m_expandedNameTable;

  /**
   * Construct a DTMDefaultBase object from a DOM node.
   *
   * @param mgr The DTMManager who owns this DTM.
   * @param domSource the DOM source that this DTM will wrap.
   * @param source The object that is used to specify the construction source.
   * @param dtmIdentity The DTM identity ID for this DTM.
   * @param whiteSpaceFilter The white space filter for this DTM, which may
   *                         be null.
   * @param xstringfactory The factory to use for creating XMLStrings.
   * @param doIndexing true if the caller considers it worth it to use
   *                   indexing schemes.
   */
  public DOM2DTM2Base(DTMManager mgr, Source source, int dtmIdentity,
                        DTMWSFilter whiteSpaceFilter,
                        XMLStringFactory xstringfactory, boolean doIndexing)
  {
  	m_documentBaseURI=source.getSystemId();
  	m_root=((DOMSource)source).getNode();
  	m_resolver=NodeDTMIDResolverFactory.createResolverForDOM(null,m_root);
  	if(m_resolver==null)
  		throw new java.lang.ClassCastException("Unsupported DOM Implementation");
  	
    m_mgr = mgr;
    if(mgr instanceof DTMManagerDefault)
      m_mgrDefault=(DTMManagerDefault)mgr;

    m_dtmIdent.setElementAt(dtmIdentity,0);
    m_wsfilter = whiteSpaceFilter;
    m_xstrf = xstringfactory;

	// %REVIEW% Since we aren't using indexing, does this distinction matter?
    if (doIndexing)
    {
      m_expandedNameTable = new ExpandedNameTable();
    }
    else
    {
      // Note that this fails if we aren't talking to an instance of
      // DTMManagerDefault
      m_expandedNameTable = m_mgrDefault.getExpandedNameTable(this);
    }

  }

  /** Stateless axis traversers, lazely built. */
  protected DTMAxisTraverser[] m_traversers;

//    /**
//     * Ensure that the size of the information arrays can hold another entry
//     * at the given index.
//     *
//     * @param index On exit from this function, the information arrays sizes must be
//     * at least index+1.
//     */
//    protected void ensureSize(int index)
//    {
//        // We've cut over to Suballocated*Vector, which are self-sizing.
//    }

  /**
   * Get the simple type ID for the given node identity.
   *
   * @param identity The node identity.
   *
   * @return The simple type ID, or DTM.NULL.
   */
  protected short _type(int identity)
  {
  	// Small but may be inefficient?
  	if(true)
	  	return m_expandedNameTable.getType(_exptype(identity));
  	
  	// Alternative is to do it explicitly
  	Node n=m_resolver.findNode(identity);
  	int type=n.getNodeType();
  	switch(type)
  	{
  		case Node.ATTRIBUTE_NODE:
  			if(NAMESPACE_URI_XMLNS.equals(n.getNamespaceURI()))
	  			return DTM.NAMESPACE_NODE;
	  		else
	  			return DTM.ATTRIBUTE_NODE;
	  		//break;
	  		
	  	case Node.CDATA_SECTION_NODE:
	  	case Node.TEXT_NODE:
	  		return DTM.TEXT_NODE;
	  		//break;
	  		  		
	  	case Node.DOCUMENT_NODE:
	  	case Node.DOCUMENT_FRAGMENT_NODE:
	  		return DTM.DOCUMENT_NODE;
	  		//break;
	  		
	  	case Node.COMMENT_NODE:
	  	case Node.ELEMENT_NODE:
	  	case Node.PROCESSING_INSTRUCTION_NODE:
	  		return (short)type; // Same number as in DOM.
	  		//break; 			
	  		
	  	case Node.ENTITY_NODE:
	  	case Node.ENTITY_REFERENCE_NODE:
	  	case Node.NOTATION_NODE:
		default:
	  		return (short)DTM.NULL; // Should never be given IDs!
	  		//break;
  	}
  }

  /**
   * Get the expanded type ID for the given node identity.
   *
   * @param identity The node identity.
   *
   * @return The expanded type ID, or DTM.NULL.
   */
  protected int _exptype(int identity)
  {
  	if(identity==NULL) return NULL;
  	
  	// %OPT% This should probably be stashed to avoid recomputing!!!
  	Node n=m_resolver.findNode(identity);
  	// XPath type distinguishes between Attribute and Namespace Node
  	String namespace=n.getNamespaceURI();
  	int type=n.getNodeType();
  	if(type==Node.ATTRIBUTE_NODE && NAMESPACE_URI_XMLNS.equals(namespace))
  		type=DTM.NAMESPACE_NODE;
  	return m_expandedNameTable.getExpandedTypeID(namespace,n.getLocalName(),type);
  }

  /**
   * Get the level in the tree for the given node identity.
   *
   * @param identity The node identity.
   *
   * @return The tree level, or DTM.NULL.
   */
  protected int _level(int identity)
  {
  	Node n=m_resolver.findNode(identity);
    int i=0;
    
    // Deal with the DOM/XPath disagreement over parentage of
    // Attribute nodes. Children of Attrs should never have been
    // given IDs anyway, so this should be all we need.
    if(n.getNodeType()==Node.ATTRIBUTE_NODE)
    {
    	i=1;
    	n=((Attr)n).getOwnerElement();
    }
    
    while(null != (n=n.getParentNode()))
    {
    	// Unclear we actually need the test, since I think this is
    	// only used for quick deeper/shallower sort on same ancestry
    	// ... but until I'm sure, paranoia is a Good Thing.
    	// %REVIEW%
    	if(n.getNodeType()!=Node.ENTITY_REFERENCE_NODE)
	      ++i;
    }
    return i;
  }
  
  /** Utility: Parent, skipping Entity References and handling
   * XPath-style parentage of attributes/namespaces
   * %REVIEW% Should these be factored out into a DOM-to-XPath library?
   * */
  protected static Node walkParentSkipEntRef(Node n)
  {
  	if(n==null)return null;
  	do 
	{	
		Node p=n.getParentNode();
		if(p==null && n.getNodeType()==Node.ATTRIBUTE_NODE)
			p=((Attr)n).getOwnerElement();
		n=p;
	}
  	while(n!=null && n.getNodeType()==Node.ENTITY_REFERENCE_NODE);
  	
  	return n; // May be null
  }
  
  /** Utility: First child, skipping Entity References.
   * Can't be static due to m_wsfilter reference.
   * %REVIEW% Should these be factored out into a DOM-to-XPath library?
   *  */
  protected  Node walkFirstChildSkipEntRef(Node n)
  {
  	if(n==null)return null;
	n=n.getFirstChild();
  	if(n!=null && n.getNodeType()==Node.ENTITY_REFERENCE_NODE)
  	{
  		Node c=walkFirstChildSkipEntRef(n);
  		if(c!=null)
  			n=c;
  		else
	  		n=walkNextSiblingSkipEntRef(n);
  	}
  	
  	n=walkSkipSuppressedNodesForward(n);
  	  	
  	return n; // May be null
  }
  
  /** Utility subroutine: Given a node, determine whether it's
   * one which our whitespace filter or the XPath Data Model
   * says we should suppress.
   * If not, return it. If it is, return its logical next
   * sibling instead.
   * */
  protected Node walkSkipSuppressedNodesForward(Node n)
  {
  	if(n==null) return null;  	
  	int ntype=n.getNodeType();

	// Start by suppressing the non-XPath nodes  	
  	boolean suppressNode=(
  		ntype==Node.DOCUMENT_TYPE_NODE ||  // may arise as child of Document
  		ntype==Node.ENTITY_NODE || // shouldn't arise but I'm being paranoid
  		ntype==Node.NOTATION_NODE); // shouldn't arise but I'm being paranoid
  	if(suppressNode)
  	{
  		return walkSkipSuppressedNodesForward(n.getNextSibling());
  	}
  	
  	if(ntype==Node.TEXT_NODE || ntype==Node.CDATA_SECTION_NODE)
  	{
        // If filtering, initially assume we're going to suppress the node
        // %OPT% ****MASSIVELY*** inefficient since the default is inherit.
        // Could cache this info on each element, but that means starting 
        // to grow the shadow. Again. Arggh.        
        int shouldstrip=m_wsfilter.INHERIT;
        if(m_wsfilter!=null)
        {
	        for(Node p=n.getParentNode();
    	    	p!=null && shouldstrip==m_wsfilter.INHERIT;
        		p=p.getParentNode())
	        {
	        	if(p.getNodeType()==Node.ELEMENT_NODE)
	    	    	shouldstrip=m_wsfilter.getShouldStripSpace(
	    	    		makeNodeHandle(m_resolver.findID(p,false)), //never a text node
	    	    		this);
        	}
        }
        suppressNode=(shouldstrip==m_wsfilter.STRIP);
        if(suppressNode)
        {
	        // Scan logically contiguous text (siblings, plus "flattening"
	        // of entity reference boundaries). If nonblank found, cancel
	        // suppression.
	        Node lasttext=n;
	        for(Node nx=n;
	        	nx!=null & suppressNode;
	            lasttext=nx,nx=walkLogicalNextDOMTextNode(nx))
	          {
	            suppressNode &=
	              XMLCharacterRecognizer.isWhiteSpace(nx.getNodeValue());
	          }
	        // If nothing but whitespace found, skip this node,
	        // advancing to next sib.
	        if(suppressNode)
	        	n=walkNextSiblingSkipEntRef(lasttext);
        }
  	}

  	return n;
  }
  
  /** Utility subroutine: Given a node, determine whether it's
   * one which our whitespace filter and/or the XPath Model
   * says we should suppress.
   * If not, return the first node in this logically-
   * contiguous block (eg, the start of this XPath Text Node
   * -- useful effect in its own right).
   * If it is suppressed whitespace, return the logical previous
   * sibling instead.
   * */
  protected Node walkSkipSuppressedNodesBackward(Node n)
  {
  	if(n==null) return null;  	
  	int ntype=n.getNodeType();

	// Start by suppressing the non-XPath nodes  	
  	boolean suppressNode=(
  		ntype==Node.DOCUMENT_TYPE_NODE ||  // may arise as child of Document
  		ntype==Node.ENTITY_NODE || // shouldn't arise but I'm being paranoid
  		ntype==Node.NOTATION_NODE); // shouldn't arise but I'm being paranoid
  	if(suppressNode)
  	{
  		return walkSkipSuppressedNodesBackward(n.getPreviousSibling());
  	}

  	if(ntype==Node.TEXT_NODE || ntype==Node.CDATA_SECTION_NODE)
  	{
        // If filtering, initially assume we're going to suppress the node
        // %OPT% ****MASSIVELY*** inefficient since the default is inherit.
        // Could cache this info on each element, but that means starting 
        // to grow the shadow. Again. Arggh.        
        int shouldstrip=m_wsfilter.INHERIT;
        if(m_wsfilter!=null)
        {
	        for(Node p=n.getParentNode();
    	    	p!=null && shouldstrip==m_wsfilter.INHERIT;
        		p=p.getParentNode())
	        {
	        	if(p.getNodeType()==Node.ELEMENT_NODE)
	    	    	shouldstrip=m_wsfilter.getShouldStripSpace(
	    	    		makeNodeHandle(m_resolver.findID(p,false)),  // never a text node
	    	    		this);
        	}
        }
        suppressNode=(shouldstrip==m_wsfilter.STRIP);
        if(suppressNode)
        {
	        // Scan logically contiguous text (siblings, plus "flattening"
	        // of entity reference boundaries). If nonblank found, cancel
	        // suppression.
	        Node firsttext=n;
	        for(Node nx=n;
	        	nx!=null & suppressNode;
	            firsttext=nx,nx=walkLogicalPreviousDOMTextNode(nx))
	          {
	            suppressNode &=
	              XMLCharacterRecognizer.isWhiteSpace(nx.getNodeValue());
	          }
	        // If nothing but whitespace found, skip this node,
	        // advancing to next sib.
	        if(suppressNode)
	        	n=walkNextSiblingSkipEntRef(firsttext);
	        else
	        	n=firsttext;
        }
  	}

  	return n;
  }

  /** Utility: Last child, skipping Entity References.
   * %REVIEW% Should these be factored out into a DOM-to-XPath library?
   *  */
  protected Node walkLastChildSkipEntRef(Node n)
  {
  	if(n==null)return null;
	n=n.getLastChild();
  	if(n!=null && n.getNodeType()==Node.ENTITY_REFERENCE_NODE)
  	{
  		Node c=walkLastChildSkipEntRef(n);
  		if(c!=null)
  			n=c;
  		else
	  		n=walkPreviousSiblingSkipEntRef(n);
  	}
  	
  	n=walkSkipSuppressedNodesBackward(n);
  	
  	return n; // May be null
  }

  /** Utility: Next sibling, skipping Entity References
   * (This gets a bit weird since it's skip rather than reject;
   * we have to be prepared to walk out as well as in.)
   * 
   * Does *not* suppress successive text nodes; for now that should be handled
   * by the caller.
   * %OPT% Might be cheaper to do it here. Might not.
   * 
   * %REVIEW% Should these be factored out into a DOM-to-XPath library?
   * */
  protected Node walkNextSiblingSkipEntRef(Node n)
  {
  	if(n==null)return null;
	Node ns=n.getNextSibling();
	if(ns==null)
	{
		// Might have walked into an EntRef
		Node p=n.getParentNode();
		if(p!=null && p.getNodeType()==Node.ENTITY_REFERENCE_NODE)
			ns=walkNextSiblingSkipEntRef(p);
		// else there really isn't one.
	}
  	else if(ns.getNodeType()==Node.ENTITY_REFERENCE_NODE)
  	{
  		Node c=walkFirstChildSkipEntRef(ns);
  		if(c!=null)
  			ns=c;
  		else
	  		ns=walkNextSiblingSkipEntRef(n);
  	}
  	
  	ns=walkSkipSuppressedNodesForward(ns);
  	
  	return ns; // May be null
  }
  
  /** Utility: Previous sibling, skipping Entity References
   * (This gets a bit weird since it's skip rather than reject;
   * we have to be prepared to walk out as well as in).
   * 
   * %REVIEW% Should these be factored out into a DOM-to-XPath library?
   *  */
  protected Node walkPreviousSiblingSkipEntRef(Node n)
  {
  	if(n==null)return null;
	Node ns=n.getPreviousSibling();
	if(ns==null)
	{
		// Might have walked into an EntRef
		Node p=n.getParentNode();
		if(p!=null && p.getNodeType()==Node.ENTITY_REFERENCE_NODE)
			ns=walkPreviousSiblingSkipEntRef(p);
	}
  	else if(ns.getNodeType()==Node.ENTITY_REFERENCE_NODE)
  	{
  		Node c=walkLastChildSkipEntRef(ns);
  		if(c!=null)
  			ns=c;
  		else
	  		ns=walkPreviousSiblingSkipEntRef(n);
  	}
  	
  	n=walkSkipSuppressedNodesBackward(ns);

   	return n; // May be null
  }

  /** Utility: Advance to next DOM Namespace Attr.
   * 
   * %REVIEW% I'm not sure this is still being used except by
   * getFirstNamespaceNode. Fold it into that? Simplify to just
   * do get-first?
   * 
   * %REVIEW% Should these be factored out into a DOM-to-XPath library?
   * 
   * @param startPoint Node to start scan from. If attr, we scan
   * following attrs of same parent; otherwise we scan attrs of this
   * node (empty for all but elements)
   * */
  protected Node nextNSAttr(Node startPoint)
  {
  	// %REVIEW% This one can't be static since it uses 
  	// the resolver for isSameNode
  	if(startPoint==null)
  		return null;
  		
  	Node container;
  	Attr startAttr;
  	if(startPoint.getNodeType()==Node.ATTRIBUTE_NODE)
  	{
  		startAttr=(Attr)startPoint;
  		container=startAttr.getOwnerElement();
	  	if(container==null) // Orphaned attr?
  			return null;
  	}
  	else
  	{
  		startAttr=null;
  		container=startPoint;
  	}
  		
	org.w3c.dom.NamedNodeMap nnm=container.getAttributes();
  	if(nnm==null)
  		return null;
	
	int i=0;
	Node n;
	if(startAttr!=null)
	{
		// Find the previously-located attr
		for(n=nnm.item(0);
			!m_resolver.isSameNode(startAttr,n);
			n=nnm.item(++i))
			;
		// Start with attr after that
		++i;
	}
	for(n=nnm.item(i);
		n!=null;
		n=nnm.item(++i))
		if(NAMESPACE_URI_XMLNS.equals(
			nnm.item(i).getNamespaceURI()) )
			return n;

	return null; 	// No non-NS attr found  
  }
  
  /** Utility: Next node in document order, skipping Entity References
   * and NOT including attributes or namespace declarations.
   * Like TreeWalker.getNextNode() with whatToShow rejecting EntRef.
   * Used by traversers.
   * 
   * %REVIEW% Does that need to be conditional, eg for text dump?
   * 
   * %REVIEW% Should these be factored out into a DOM-to-XPath library?
   * (Should it just use TreeWalker?)
   *  */
  protected Node walkNextSkipEntRef(Node n, Node within, boolean mergetext)
  {
    if (n == null) return null;
    
    int ntype=n.getNodeType();
    boolean wastext=mergetext && (ntype==Node.TEXT_NODE || ntype==Node.CDATA_SECTION_NODE);
    
    Node result=n;
    int rtype;
    do // May have to skip logically-adjacent text nodes
    {        
    	n=result;
    	
    	// Try children -- but ***ONLY** if not navigating from an attr,
    	// since XPath Attr nodes have no children. (Note that this
    	// will only occur if we _start_ at an attr, since attrs won't be
    	// navigated to. 
    	// %REVIEW% %OPT% Can this be factored out, to avoid
    	// testing later iterations? Not hugely expensive, but...
    	if(n.getNodeType()!=Node.ATTRIBUTE_NODE)
    	{
		    result = walkFirstChildSkipEntRef(n);
		    if (result != null)
				break;			
    	}

		// If there weren't any kids, and we're sitting at the stop point,
		// stop now and return null.
		if(m_resolver.isSameNode(n,within))
		{
			result=null;
			break; 
		}
	    
	    result = walkNextSiblingSkipEntRef(n);
	    if (result != null)
			break;
		            
	    // return ancestor's 1st sibling.
	    // Text following a "real" ancestor is *NOT* part of same text block!
	    wastext=false; 
	    Node parent = walkParentSkipEntRef(n);
	    while (parent != null) 
	    {
			if(within!=null && m_resolver.isSameNode(parent,within))
				break; 
				
		    result = walkNextSiblingSkipEntRef(parent);
			if(result!=null) 
				break;
	        else 
	            parent = walkParentSkipEntRef(parent);
	    }
    } while(wastext && result!=null &&
	    		 ((rtype=result.getNodeType())==Node.TEXT_NODE ||
    				rtype==Node.CDATA_SECTION_NODE ) 
    		);
        
    return result;
  }

  /** Utility: Previous node in document order, skipping Entity References
   * and NOT including attributes or namespace declarations.
   * 
   * Also handles logically-united text nodes. 
   * %REVIEW% Does that need to be conditional, eg for text dump?
   * 
   * Like TreeWalker.getNextNode() with whatToShow rejecting EntRef.
   * Used by traversers.
   * 
   * %REVIEW% Should these be factored out into a DOM-to-XPath library?
   * (Should it just use TreeWalker?)
   * 
   * @param n Node to start walking from
   * @param within Node not to walk out of (ie, top of subtree)
   *  */
  protected Node walkPreviousSkipEntRef(Node n, Node within)
  {
	Node result;
        
    if (n == null) return null;
        
    // get sibling
    result = walkPreviousSiblingSkipEntRef(n);

    if (result == null) // No sib, so go up.
    {
	  	do 
		{	
	        if(within!=null && m_resolver.isSameNode(n,within))
				return null;
			Node p=n.getParentNode();
			if(p==null && n.getNodeType()!=Node.ATTRIBUTE_NODE)
				p=((Attr)n).getOwnerElement();
			n=p;
		}
	  	while(n!=null && n.getNodeType()==Node.ENTITY_REFERENCE_NODE);
     	return n;   
    }
        
    // get the last descendent of previous sib. ("last before null" loop)
    Node lastChild  = walkLastChildSkipEntRef(result);
    Node lastDescendent = lastChild ;
    while (lastChild != null) {
      lastDescendent = lastChild ;
      lastChild = walkLastChildSkipEntRef(lastDescendent) ;
    }
        
    if (lastDescendent != null)
        result=lastDescendent;

	// This is really ugly, but there isn't a good
	// portable alternative...
	// %OPT% Might be cleaner to iterate over the above.
	int rtype;
	if(result!=null &&
		( (rtype=result.getNodeType())==Node.TEXT_NODE ||
			rtype==Node.CDATA_SECTION_NODE) )
	{
		Node recurseprev=walkPreviousSkipEntRef(result,within);
		if(recurseprev!=null &&
			( (rtype=recurseprev.getNodeType())==Node.TEXT_NODE ||
				rtype==Node.CDATA_SECTION_NODE) )
			result=recurseprev;
	}

	return result;			
  }
  

  /**
   * Get the first child for the given node identity.
   *
   * @param identity The node identity.
   *
   * @return The first child identity, or DTM.NULL.
   */
  protected int _firstch(int identity)
  {
  	Node n=m_resolver.findNode(identity);
  	if(n.getNodeType()==Node.ATTRIBUTE_NODE)
	 	return NULL;
	  
  	return m_resolver.findID(walkFirstChildSkipEntRef(n),false);  // should never be non-first text 
  }

  /**
   * Get the next sibling for the given node identity. Note
   * that attributes and namespaces DO have sibs in the DTM
   * model (other attrs and namespaces respectively but not
   * interchangably).
   *
   * @param identity The node identity.
   *
   * @return The next sibling identity, or DTM.NULL.
   */
  protected int _nextsib(int identity)
  {
  	Node n=m_resolver.findNode(identity);
  	
  	int ntype=n.getNodeType();
  	if(ntype==Node.ATTRIBUTE_NODE)
  	{
  		Element e=((Attr)n).getOwnerElement();
  		if(e==null) return NULL;
  		
  		boolean isns=
  			NAMESPACE_URI_XMLNS.equals(n.getNamespaceURI()) &&
  			("xmlns".equals(n.getPrefix()) || "xmlns".equals(n.getNodeName()))
  			;
  			
		NamedNodeMap attrs=e.getAttributes();
		int len=attrs.getLength();
		int i;
		for(i=0;
			i<len && !m_resolver.isSameNode(n,attrs.item(i));
			++i)
			; // Loop does it all, scanning for last-seen attr
		for(++i;
			i<len;
			++i)
		{
			n=attrs.item(i);
			// If first was NS this must be too, else neither.
			if(isns == (
	  			NAMESPACE_URI_XMLNS.equals(n.getNamespaceURI()) &&
  				("xmlns".equals(n.getPrefix()) || "xmlns".equals(n.getNodeName()))
  				))
		  	return m_resolver.findID(n,false);
		}
	 	return NULL;
  	}
	else
	{
		// Multiple DOM text-nodes constitute a single XPath text node;
		// if start at text. scan until nontext.
		boolean istext=(ntype==Node.TEXT_NODE ||ntype==Node.CDATA_SECTION_NODE);
		do
		{
			n=walkNextSiblingSkipEntRef(n);
		} while(n!=null && 
			istext && 
			((ntype=n.getNodeType())==Node.TEXT_NODE 
				||ntype==Node.CDATA_SECTION_NODE));
	}
	  
  	return m_resolver.findID(n,false); // should never be non-first text 
  }

  /**
   * Get the previous sibling for the given node identity.
   * In DTM, unlike DOM, namespace nodes and attrs have sibs
   * (within each of those two groups).
   *
   * @param identity The node identity.
   *
   * @return The previous sibling identity, or DTM.NULL.
   */
  protected int _prevsib(int identity)
  {
  	Node n=m_resolver.findNode(identity);
  	
  	int ntype=n.getNodeType();
  	if(ntype==Node.ATTRIBUTE_NODE)
  	{
  		Element e=((Attr)n).getOwnerElement();
  		if(e==null) return NULL;
  		boolean isns=
  			NAMESPACE_URI_XMLNS.equals(n.getNamespaceURI()) &&
  			("xmlns".equals(n.getPrefix()) || "xmlns".equals(n.getNodeName()))
  			;
		NamedNodeMap attrs=e.getAttributes();
		int len=attrs.getLength();
		int i;
		for(i=len-1;
			i>=0 && !m_resolver.isSameNode(n,attrs.item(i));
			--i)
			; // Loop does it all, scanning for last-seen attr
		for(--i;
			i>=0;
			--i)
		{
			n=attrs.item(i);
			// If first was NS this must be too, else neither.
			if(isns == (
	  			NAMESPACE_URI_XMLNS.equals(n.getNamespaceURI()) &&
  				("xmlns".equals(n.getPrefix()) || "xmlns".equals(n.getNodeName()))
  				))
		  	return m_resolver.findID(n,true); // might be text but not first-text
		}
	 	return NULL;
  	}
	else
	{
		n=walkPreviousSiblingSkipEntRef(n);
		
		// Multiple DOM text-nodes constitute a single XPath text node.
		//
		// I don't actually have to check that here; the resolver's
		// findID() already contains that safety net.
		if(n!=null)
		{
			/*****
			boolean wastext=(ntype==Node.TEXT_NODE ||ntype==Node.CDATA_SECTION_NODE);
			if(wastext)
			{
				// Scan sibs left until non-text
				Node p=n;
				while(p!=null && 
					((ntype=p.getNodeType())==Node.TEXT_NODE 
						||ntype==Node.CDATA_SECTION_NODE));
				{
					n=p;
					p=previousSiblingSkipEntRef(p);
				};
			}
			*****/
		}
	}
	  
  	return m_resolver.findID(n,true);  // may be non-first text 
  }

  /**
   * Get the parent for the given node identity.
   *
   * @param identity The node identity.
   *
   * @return The parent identity, or DTM.NULL.
   */
  protected int _parent(int identity)
  {
  	Node n=m_resolver.findNode(identity);
  	n=walkParentSkipEntRef(n);
  	return m_resolver.findID(n,false); // Parent will never be text node!
  }

  /**
   * Diagnostics function to dump the DTM.
   * 
   * CAUTION: This will cause complete mapping of the DOM tree!
   */
  public void dumpDTM(OutputStream os)
  {
    try
    {
      if(os==null)
      {
	      File f = new File("DTMDump"+((Object)this).hashCode()+".txt");
 	      System.err.println("Dumping... "+f.getAbsolutePath());
 	      os=new FileOutputStream(f);
      }
      PrintStream ps = new PrintStream(os);

      for (int index = 0; true/*index < nRecords*/; ++index)
      {
      	// The loop body will keep adding nodes until we run out
      	// of axes to explore. Presumably they're being added
      	// sequentially, so this exception won't be hit until
      	// we really have run out of nodes to convert. 
      	try
      	{
      		m_resolver.findNode(index);      		
      	}
      	catch(ArrayIndexOutOfBoundsException e)
      	{
      		return;
      	}
      	
      	int i=makeNodeHandle(index);
        ps.println("=========== index=" + index + " handle=" + i + " ===========");
        ps.println("NodeName: " + getNodeName(i));
        ps.println("NodeNameX: " + getNodeNameX(i));
        ps.println("LocalName: " + getLocalName(i));
        ps.println("NamespaceURI: " + getNamespaceURI(i));
        ps.println("Prefix: " + getPrefix(i));

        int exTypeID = _exptype(index);

        ps.println("Expanded Type ID: "
                           + Integer.toHexString(exTypeID));

        int type = _type(index);
        String typestring;

        switch (type)
        {
        case DTM.ATTRIBUTE_NODE :
          typestring = "ATTRIBUTE_NODE";
          break;
        case DTM.CDATA_SECTION_NODE :
          typestring = "CDATA_SECTION_NODE";
          break;
        case DTM.COMMENT_NODE :
          typestring = "COMMENT_NODE";
          break;
        case DTM.DOCUMENT_FRAGMENT_NODE :
          typestring = "DOCUMENT_FRAGMENT_NODE";
          break;
        case DTM.DOCUMENT_NODE :
          typestring = "DOCUMENT_NODE";
          break;
        case DTM.DOCUMENT_TYPE_NODE :
          typestring = "DOCUMENT_NODE";
          break;
        case DTM.ELEMENT_NODE :
          typestring = "ELEMENT_NODE";
          break;
        case DTM.ENTITY_NODE :
          typestring = "ENTITY_NODE";
          break;
        case DTM.ENTITY_REFERENCE_NODE :
          typestring = "ENTITY_REFERENCE_NODE";
          break;
        case DTM.NAMESPACE_NODE :
          typestring = "NAMESPACE_NODE";
          break;
        case DTM.NOTATION_NODE :
          typestring = "NOTATION_NODE";
          break;
        case DTM.NULL :
          typestring = "NULL";
          break;
        case DTM.PROCESSING_INSTRUCTION_NODE :
          typestring = "PROCESSING_INSTRUCTION_NODE";
          break;
        case DTM.TEXT_NODE :
          typestring = "TEXT_NODE";
          break;
        default :
          typestring = "Unknown!";
          break;
        }

        ps.println("Type: " + typestring);

        int firstChild = _firstch(index);

        if (DTM.NULL == firstChild)
          ps.println("First child: DTM.NULL");
        else
          ps.println("First child: " + firstChild);

        int prevSibling = _prevsib(index);

        if (DTM.NULL == prevSibling)
          ps.println("Prev sibling: DTM.NULL");
        else
          ps.println("Prev sibling: " + prevSibling);

        int nextSibling = _nextsib(index);

        if (DTM.NULL == nextSibling)
          ps.println("Next sibling: DTM.NULL");
        else
          ps.println("Next sibling: " + nextSibling);

        int parent = _parent(index);

        if (DTM.NULL == parent)
          ps.println("Parent: DTM.NULL");
        else
          ps.println("Parent: " + parent);

        int level = _level(index);

        ps.println("Level: " + level);
        ps.println("Node Value: " + getNodeValue(i));
        ps.println("String Value: " + getStringValue(i));
      }
    }
    catch(IOException ioe)
    {
      ioe.printStackTrace(System.err);
      System.exit(-1);
    }
  }
  
  /**
   * Diagnostics function to dump a single node.
   * 
   * Warning: This will cause the nodes around this node to be
   * loaded into the DTM, and may thus interfere with testing
   * incrementality.
   * 
   * %REVIEW% KNOWN GLITCH: If you pass it a node index rather than a 
   * node handle, it works just fine... but the displayed identity 
   * number before the colon is different, which complicates comparing
   * it with nodes printed the other way. We could always OR the DTM ID
   * into the value, to suppress that distinction...
   * 
   * %REVIEW% This might want to be moved up to DTMDefaultBase, or possibly
   * DTM itself, since it's a useful diagnostic and uses only DTM's public
   * APIs.
   */
  public String dumpNode(int nodeHandle)
  {	  
	  if(nodeHandle==DTM.NULL)
		  return "[null]";
		  
        String typestring;
        switch (getNodeType(nodeHandle))
        {
        case DTM.ATTRIBUTE_NODE :
          typestring = "ATTR";
          break;
        case DTM.CDATA_SECTION_NODE :
          typestring = "CDATA";
          break;
        case DTM.COMMENT_NODE :
          typestring = "COMMENT";
          break;
        case DTM.DOCUMENT_FRAGMENT_NODE :
          typestring = "DOC_FRAG";
          break;
        case DTM.DOCUMENT_NODE :
          typestring = "DOC";
          break;
        case DTM.DOCUMENT_TYPE_NODE :
          typestring = "DOC_TYPE";
          break;
        case DTM.ELEMENT_NODE :
          typestring = "ELEMENT";
          break;
        case DTM.ENTITY_NODE :
          typestring = "ENTITY";
          break;
        case DTM.ENTITY_REFERENCE_NODE :
          typestring = "ENT_REF";
          break;
        case DTM.NAMESPACE_NODE :
          typestring = "NAMESPACE";
          break;
        case DTM.NOTATION_NODE :
          typestring = "NOTATION";
          break;
        case (short)DTM.NULL:
          typestring = "null";
          break;
        case DTM.PROCESSING_INSTRUCTION_NODE :
          typestring = "PI";
          break;
        case DTM.TEXT_NODE :
          typestring = "TEXT";
          break;
        default :
          typestring = "Unknown!";
          break;
        }

      StringBuffer sb=new StringBuffer();
	  sb.append("["+nodeHandle+": "+typestring+
				"(0x"+Integer.toHexString(getExpandedTypeID(nodeHandle))+") "+
				getNodeNameX(nodeHandle)+" {"+getNamespaceURI(nodeHandle)+"}"+
				"=\""+ getNodeValue(nodeHandle)+"\"]");
	  return sb.toString();
  }

  // ========= DTM Implementation Control Functions. ==============

  /**
   * Set an implementation dependent feature.
   * <p>
   * %REVIEW% Do we really expect to set features on DTMs?
   *
   * @param featureId A feature URL.
   * @param state true if this feature should be on, false otherwise.
   */
  public void setFeature(String featureId, boolean state){}

  // ========= Document Navigation Functions =========

  /**
   * Given a node handle, test if it has child nodes.
   * <p> %REVIEW% This is obviously useful at the DOM layer, where it
   * would permit testing this without having to create a proxy
   * node. It's less useful in the DTM API, where
   * (dtm.getFirstChild(nodeHandle)!=DTM.NULL) is just as fast and
   * almost as self-evident. But it's a convenience, and eases porting
   * of DOM code to DTM.  </p>
   *
   * @param nodeHandle int Handle of the node.
   * @return int true if the given node has child nodes.
   */
  public boolean hasChildNodes(int nodeHandle)
  {

    int identity = makeNodeIdentity(nodeHandle);
    int firstChild = _firstch(identity);

    return firstChild != DTM.NULL;
  }
	
  /** Given a node identity, return a node handle. If extended addressing
   * has been used (multiple DTM IDs), we need to map the high bits of the
   * identity into the proper DTM ID.
   * 
   * This has been made FINAL to facilitate inlining, since we do not expect
   * any subclass of DTMDefaultBase to ever change the algorithm. (I don't
   * really like doing so, and would love to have an excuse not to...)
   * 
   * %REVIEW% Is it worth trying to specialcase small documents?
   * %REVIEW% Should this be exposed at the package/public layers?
   * 
   * @param nodeIdentity Internal offset to this node's records.
   * @return NodeHandle (external representation of node)
   * */
  final protected int makeNodeHandle(int nodeIdentity)
  {
    if(NULL==nodeIdentity) return NULL;
		
    if(JJK_DEBUG && nodeIdentity>DTMManager.IDENT_NODE_DEFAULT)
      System.err.println("GONK! (only useful in limited situations)");

    return m_dtmIdent.elementAt(nodeIdentity >>> DTMManager.IDENT_DTM_NODE_BITS)
      + (nodeIdentity & DTMManager.IDENT_NODE_DEFAULT) ;											
  }
	
  /** Given a node handle, return a node identity. If extended addressing
   * has been used (multiple DTM IDs), we need to map the high bits of the
   * identity into the proper DTM ID and thence find the proper offset
   * to add to the low bits of the identity
   * 
   * This has been made FINAL to facilitate inlining, since we do not expect
   * any subclass of DTMDefaultBase to ever change the algorithm. (I don't
   * really like doing so, and would love to have an excuse not to...)
   * 
   * %OPT% Performance is critical for this operation.
   *
   * %REVIEW% Should this be exposed at the package/public layers?
   * 
   * @param NodeHandle (external representation of node)
   * @return nodeIdentity Internal offset to this node's records.
   * */
  final protected int makeNodeIdentity(int nodeHandle)
  {
    if(NULL==nodeHandle) return NULL;

    if(m_mgrDefault!=null)
    {
      // Optimization: use the DTMManagerDefault's fast DTMID-to-offsets
      // table.  I'm not wild about this solution but this operation
      // needs need extreme speed.

      int whichDTMindex=nodeHandle>>>DTMManager.IDENT_DTM_NODE_BITS;

      
      // %REVIEW% These methods are currently synchronized, and hence
      // less performant than the DTMDefaultBase implementation which
      // reaches directly into the arrays... but safer...

      // %REVIEW% Wish I didn't have to perform the pre-test, but
      // someone is apparently asking DTMs whether they contain nodes
      // which really don't belong to them. That's probably a bug
      // which should be fixed, but until it is:     
      if(m_mgrDefault.getDTM(nodeHandle)!=this)
		return NULL;
      else
		return
		  m_mgrDefault.getDTMoffset(whichDTMindex)
		  | (nodeHandle & DTMManager.IDENT_NODE_DEFAULT);
    }
	  
    int whichDTMid=m_dtmIdent.indexOf(nodeHandle & DTMManager.IDENT_DTM_DEFAULT);
    return (whichDTMid==NULL) 
      ? NULL
      : (whichDTMid << DTMManager.IDENT_DTM_NODE_BITS)
      + (nodeHandle & DTMManager.IDENT_NODE_DEFAULT);
  }


  /**
   * Given a node handle, get the handle of the node's first child.
   * If not yet resolved, waits for more nodes to be added to the document and
   * tries again.
   *
   * @param nodeHandle int Handle of the node.
   * @return int DTM node-number of first child, or DTM.NULL to indicate none exists.
   */
  public int getFirstChild(int nodeHandle)
  {
    int identity = makeNodeIdentity(nodeHandle);
    int firstChild = _firstch(identity);

    return makeNodeHandle(firstChild);
  }

  /**
   * Given a node handle, advance to its last child.
   * If not yet resolved, waits for more nodes to be added to the document and
   * tries again.
   *
   * @param nodeHandle int Handle of the node.
   * @return int Node-number of last child,
   * or DTM.NULL to indicate none exists.
   */
  public int getLastChild(int nodeHandle)
  {
    int identity = makeNodeIdentity(nodeHandle);

  	Node n=m_resolver.findNode(identity);
  	if(n.getNodeType()==Node.ATTRIBUTE_NODE)
	 	return NULL;
	  
	// may be text but not first-text
  	return makeNodeHandle(m_resolver.findID(walkLastChildSkipEntRef(n),true));
  }

  /**
   * Retrieves an attribute node by by qualified name and namespace URI.
   *
   * @param nodeHandle int Handle of the node upon which to look up this attribute..
   * @param namespaceURI The namespace URI of the attribute to
   *   retrieve, or null.
   * @param name The local name of the attribute to
   *   retrieve.
   * @return The attribute node handle with the specified name (
   *   <code>nodeName</code>) or <code>DTM.NULL</code> if there is no such
   *   attribute.
   */
  public int getAttributeNode(int nodeHandle, String namespaceURI,
                                       String name)
  {
    int identity = makeNodeIdentity(nodeHandle);

  	Node n=m_resolver.findNode(identity);
  	if(n.getNodeType()!=Node.ELEMENT_NODE)
  		return NULL;
	  
  	return makeNodeHandle(m_resolver.findID(
  		((Element)n).getAttributeNodeNS(namespaceURI,name),
  		false // never a text node
  		));
  }

  /**
   * Given a node handle, get the index of the node's first attribute.
   *
   * @param nodeHandle int Handle of the node.
   * @return Handle of first attribute, or DTM.NULL to indicate none exists.
   */
  public int getFirstAttribute(int nodeHandle)
  {
    int identity = makeNodeIdentity(nodeHandle);

  	Node n=m_resolver.findNode(identity);
	org.w3c.dom.NamedNodeMap nnm=n.getAttributes();
	if(nnm==null)
		return NULL;
	int i=0;
	for(n=nnm.item(0);
		n!=null;
		n=nnm.item(++i))
		if(! NAMESPACE_URI_XMLNS.equals(
			nnm.item(i).getNamespaceURI()) )
			return makeNodeHandle(m_resolver.findID(n,false)); // never a text node

	return NULL; 	// No non-NS attr found
  }

  /**
   * Given a node handle, advance to its next sibling.
   * If not yet resolved, waits for more nodes to be added to the document and
   * tries again.
   * @param nodeHandle int Handle of the node.
   * @return int Node-number of next sibling,
   * or DTM.NULL to indicate none exists.
   */
  public int getNextSibling(int nodeHandle)
  {
    return makeNodeHandle(_nextsib(makeNodeIdentity(nodeHandle)));
  }

  /**
   * Given a node handle, find its preceeding sibling.
   * WARNING: DTM is asymmetric; this operation is resolved by search, and is
   * relatively expensive.
   *
   * @param nodeHandle the id of the node.
   * @return int Node-number of the previous sib,
   * or DTM.NULL to indicate none exists.
   */
  public int getPreviousSibling(int nodeHandle)
  {
    return makeNodeHandle(_prevsib(makeNodeIdentity(nodeHandle)));
  }
  
  /**
   * Given a node handle, advance to the next attribute.
   * If an attr, we advance to
   * the next attr on the same node.  If not an attribute, we return NULL.
   *
   * @param nodeHandle int Handle of the node.
   * @return int DTM node-number of the resolved attr,
   * or DTM.NULL to indicate none exists.
   */
  public int getNextAttribute(int nodeHandle)
  {
  	// %REVIEW% Not an efficient operation.
  	// Then again, how often do we really perform it?
  	//
  	// We could optimize by caching the Named Node Map, relying
  	// on the fact that only one thread is accessing the DTM...
  	// Not sure how much we'd gain.

    int identity = makeNodeIdentity(nodeHandle);
    if (_type(identity)!=DTM.ATTRIBUTE_NODE)
  		return NULL;
  		
  	return makeNodeHandle(_nextsib(identity));
  }

  /** Lazily created namespace lists. */
  private SparseVector m_namespaceLists = null;  // on demand

  /**
   * Given a node handle, get the index of the node's first namespace.
   * If not yet resolved, waits for more nodes to be added to the document and
   * tries again
   * 
   * %REVIEW% Should we try to recognize implicit NS's? Pain, but...
   *
   * @param nodeHandle handle to node, which should probably be an element
   *                   node, but need not be.
   *
   * @param inScope    true if all namespaces in scope should be returned,
   *                   false if only the namespace declarations should be
   *                   returned.
   * @return handle of first namespace, or DTM.NULL to indicate none exists.
   */
  public int getFirstNamespaceNode(int nodeHandle, boolean inScope)
  {
  	if(nodeHandle==NULL)
  		return NULL;
  	
  	int identity=makeNodeIdentity(nodeHandle);
  	Node n=m_resolver.findNode(identity);
  	       		  	
    if(inScope)
	{
		// %REVIEW% %OPT% Currently flushing on get-first.
		Vector nsnodes=m_resolver.getNamespacesInScope(n,true);
		// should never be empty; xml: is always defined...
		// unless this is an orphan node
		if(nsnodes==null)
			return NULL;
		n=(Node)nsnodes.elementAt(0);
		return makeNodeHandle(m_resolver.findID( n,false )); // never a text node
    }
	else
	{
		// Just do local scan?
		// %REVIEW%
		n=nextNSAttr(n);
		return makeNodeHandle(m_resolver.findID( n,false )); // never a text node
	}
  }

  /**
   * Given a namespace handle, advance to the next namespace.
   * 
   * %REVIEW% Should we try to recognize implicit NS's? Pain, but...
   *
   * @param baseHandle handle to original node from where the first namespace
   * was relative to (needed to return nodes in document order).
   * @param namespaceHandle handle to node which must be of type
   * NAMESPACE_NODE.
   * @param nodeHandle A namespace handle for which we will find the next node.
   * @param inScope true if all namespaces that are in scope should be processed,
   * otherwise just process the nodes in the given element handle.
   * @return handle of next namespace, or DTM.NULL to indicate none exists.
   */
  public int getNextNamespaceNode(int baseHandle, int nodeHandle,
                                  boolean inScope)
  {
    int identity = makeNodeIdentity(nodeHandle);
	  	
	if(inScope)
	{
	    int elementidentity = makeNodeIdentity(baseHandle);
	  	Node base_element=m_resolver.findNode(elementidentity);
		Vector nsnodes=m_resolver.getNamespacesInScope(base_element,false);
		// should never be empty, but cheap test...
		if(nsnodes==null)
			return NULL;
		

		// Note presumption that previous namespace *IS* somewhere in this set,
		// and that list is short enough to make scan acceptable.
	  	Node start=m_resolver.findNode(identity);
		int i=0,s=nsnodes.size()-1;
		for(i=0;i<s;++i)
		{
			if(m_resolver.isSameNode(start,(Node)nsnodes.elementAt(i)))
				return makeNodeHandle(m_resolver.findID((Node)nsnodes.elementAt(i+1),false)); // never a text node
		}
		return NULL; // Off end of list
	  }
	else
	  {
	  	// %REVIEW% Not an efficient operation.
  		// Then again, how often do we really perform it?
	  	//
  		// We could optimize by caching the Named Node Map, relying
	  	// on the fact that only one thread is accessing the DTM...
  		// Not sure how much we'd gain.
    	if (_type(identity)!=DTM.NAMESPACE_NODE)
	  		return NULL;		
	  	return makeNodeHandle(_nextsib(identity));	  	
	  }
  }

  /**
   * Given a node handle, find its parent node.
   *
   * @param nodeHandle the id of the node.
   * @return int Node-number of parent,
   * or DTM.NULL to indicate none exists.
   */
  public int getParent(int nodeHandle)
  {
    int identity = makeNodeIdentity(nodeHandle);

	// %OPT%
    if (identity > 0)
      return makeNodeHandle(_parent(identity));
    else
      return DTM.NULL;
  }

  /**
   * Find the Document node handle for the document currently under construction.
   * PLEASE NOTE that most people should use getOwnerDocument(nodeHandle) instead;
   * this version of the operation is primarily intended for use during negotiation
   * with the DTM Manager.
   * 
   *  @param nodeHandle the id of the node.
   *  @return int Node handle of document, which should always be valid.
   */
  public int getDocument()
  {
    return m_dtmIdent.elementAt(0); // 0 should be hardwired to Doc in resolver.
  }

  /**
   * Given a node handle, find the owning document node.  This has the exact
   * same semantics as the DOM Document method of the same name, in that if
   * the nodeHandle is a document node, it will return NULL.
   *
   * <p>%REVIEW% Since this is DOM-specific, it may belong at the DOM
   * binding layer. Included here as a convenience function and to
   * aid porting of DOM code to DTM.</p>
   *
   * @param nodeHandle the id of the node.
   * @return int Node handle of owning document, or -1 if the node was a Docment
   */
  public int getOwnerDocument(int nodeHandle)
  {
    if (DTM.DOCUMENT_NODE == getNodeType(nodeHandle))
  	    return DTM.NULL;

    return getDocumentRoot(nodeHandle);
  }

  /**
   * Given a node handle, find the owning document node.  Unlike the DOM,
   * this considers the owningDocument of a Document to be itself.
   *
   * @param nodeHandle the id of the node.
   * @return int Node handle of owning document, or the nodeHandle if it is
   *             a Document.
   */
  public int getDocumentRoot(int nodeHandle)
  {
    return getDocument();
  }

  /**
   * Given a node identifier, find the owning document node.  Unlike the DOM,
   * this considers the owningDocument of a Document to be itself. Note that
   * in shared DTMs this may not be zero.
   *
   * @param nodeId the id of the node.
   * @return int Node identifier of owning document, or the nodeId if it is
   *             a Document.
   */
  protected int _documentRoot(int nodeIdentifier)
  {
    return 0;
  }

  /**
   * Get the string-value of a node as a String object
   * (see http://www.w3.org/TR/xpath#data-model
   * for the definition of a node's string-value).
   *
   * @param nodeHandle The node ID.
   *
   * @return A string object that represents the string-value of the given node.
   */
  public XMLString getStringValue(int nodeHandle)
  {

    int type = getNodeType(nodeHandle);
    Node node = getNode(nodeHandle);
    // %TBD% If an element only has one text node, we should just use it 
    // directly.
    if(DTM.ELEMENT_NODE == type || DTM.DOCUMENT_NODE == type 
    || DTM.DOCUMENT_FRAGMENT_NODE == type)
    {
      FastStringBuffer buf = StringBufferPool.get();
      String s;
  
      try
      {
        getNodeData(node, buf);
  
        s = (buf.length() > 0) ? buf.toString() : "";
      }
      finally
      {
        StringBufferPool.free(buf);
      }
  
      return m_xstrf.newstr( s );
    }
    else if(TEXT_NODE == type || CDATA_SECTION_NODE == type)
    {
      // If this is a DTM text node, it may be made of multiple DOM text
      // nodes -- including navigating into Entity References. DOM2DTM
      // records the first node in the sequence and requires that we
      // pick up the others when we retrieve the DTM node's value.
      //
      // %REVIEW% DOM Level 3 is expected to add a "whole text"
      // retrieval method which performs this function for us.
      FastStringBuffer buf = StringBufferPool.get();
      while(node!=null)
      {
        buf.append(node.getNodeValue());
        node=walkLogicalNextDOMTextNode(node);
      }
      String s=(buf.length() > 0) ? buf.toString() : "";
      StringBufferPool.free(buf);
      return m_xstrf.newstr( s );
    }
    else
      return m_xstrf.newstr( node.getNodeValue() );
  }

  /** Utility function: Given a DOM Text node, determine whether it is
   * logically followed by another Text or CDATASection node, and
   * return that if so. If not, return null.
   * 
   * This may involve traversing into and out of Entity References.
   * 
   * %REVIEW% DOM Level 3 is expected to add functionality which may 
   * allow us to retire this.
   */
  protected Node walkLogicalNextDOMTextNode(Node n)
  {  	
  	n=walkNextSiblingSkipEntRef(n);
    if(n!=null)
    {
    	// Found a logical next sibling. Is it text?
        int ntype=n.getNodeType();
        if(TEXT_NODE != ntype && CDATA_SECTION_NODE != ntype)
        	n=null;
    }
    return n;
  }

  /** Utility function: Given a DOM Text node, determine whether it is
   * logically preceeded by another Text or CDATASection node, and
   * return that if so. If not, return null.
   * 
   * This may involve traversing into and out of Entity References.
   * 
   * %REVIEW% DOM Level 3 is expected to add functionality which may 
   * allow us to retire this.
   */
  protected Node walkLogicalPreviousDOMTextNode(Node n)
  {    		
  	Node scan=walkPreviousSkipEntRef(n,null);
    if(scan!=null)
    {
    	// Found a logical next sibling. Is it text?
        int ntype=scan.getNodeType();
        if(TEXT_NODE != ntype && CDATA_SECTION_NODE != ntype)
        	n=null;
    }
    return n;
  }
  
  /**
   * Retrieve the text content of a DOM subtree, appending it into a
   * user-supplied FastStringBuffer object. Note that attributes are
   * not considered part of the content of an element.
   * <p>
   * There are open questions regarding whitespace stripping. 
   * Currently we make no special effort in that regard, since the standard
   * DOM doesn't yet provide DTD-based information to distinguish
   * whitespace-in-element-context from genuine #PCDATA. Note that we
   * should probably also consider xml:space if/when we address this.
   * DOM Level 3 may solve the problem for us.
   * <p>
   * %REVIEW% Actually, since this method operates on the DOM side of the
   * fence rather than the DTM side, it SHOULDN'T do
   * any special handling. The DOM does what the DOM does; if you want
   * DTM-level abstractions, use DTM-level methods.
   *
   * @param node Node whose subtree is to be walked, gathering the
   * contents of all Text or CDATASection nodes.
   * @param buf FastStringBuffer into which the contents of the text
   * nodes are to be concatenated.
   */
  protected static void getNodeData(Node node, FastStringBuffer buf)
  {

    switch (node.getNodeType())
    {
    case Node.DOCUMENT_FRAGMENT_NODE :
    case Node.DOCUMENT_NODE :
    case Node.ELEMENT_NODE :
    {
      for (Node child = node.getFirstChild(); null != child;
              child = child.getNextSibling())
      {
        getNodeData(child, buf);
      }
    }
    break;
    case Node.TEXT_NODE :
    case Node.CDATA_SECTION_NODE :
    case Node.ATTRIBUTE_NODE :	// Never a child but might be our starting node
      buf.append(node.getNodeValue());
      break;
    case Node.PROCESSING_INSTRUCTION_NODE :
      // warning(XPATHErrorResources.WG_PARSING_AND_PREPARING);        
      break;
    default :
      // ignore
      break;
    }
  }

  /**
   * Get number of character array chunks in
   * the string-value of a node.
   * (see http://www.w3.org/TR/xpath#data-model
   * for the definition of a node's string-value).
   * Note that a single text node may have multiple text chunks.
   *
   * @param nodeHandle The node ID.
   *
   * @return number of character array chunks in
   *         the string-value of a node.
   */
  public int getStringValueChunkCount(int nodeHandle)
  {

    // %TBD%
    error(XSLMessages.createMessage(XSLTErrorResources.ER_METHOD_NOT_SUPPORTED, null));//("getStringValueChunkCount not yet supported!");

    return 0;
  }

  /**
   * Get a character array chunk in the string-value of a node.
   * (see http://www.w3.org/TR/xpath#data-model
   * for the definition of a node's string-value).
   * Note that a single text node may have multiple text chunks.
   *
   * @param nodeHandle The node ID.
   * @param chunkIndex Which chunk to get.
   * @param startAndLen An array of 2 where the start position and length of
   *                    the chunk will be returned.
   *
   * @return The character array reference where the chunk occurs.
   */
  public char[] getStringValueChunk(int nodeHandle, int chunkIndex,
                                    int[] startAndLen)
  {

    // %TBD%
    error(XSLMessages.createMessage(XSLTErrorResources.ER_METHOD_NOT_SUPPORTED, null));//"getStringValueChunk not yet supported!");

    return null;
  }

  protected boolean walkIsExpandedTypeID(Node n, int exptype)
  {
    // %REVIEW% This _should_ only be null if someone asked the wrong DTM about the node...
    // which one would hope would never happen...
    if(n==null)
      return exptype==NULL;
      
     String ns=n.getNamespaceURI();
     if(ns==null) ns="";
     String local=n.getLocalName();
     if(local==null) local="";
     int type=n.getNodeType();
     if(type==Node.ATTRIBUTE_NODE && NAMESPACE_URI_XMLNS.equals(ns))
     	type=DTM.NAMESPACE_NODE;
     	
     return m_expandedNameTable.getType(exptype)==type
     	&& m_expandedNameTable.getLocalName(exptype).equals(local)
	    && m_expandedNameTable.getNamespace(exptype).equals(ns);
  }
  
  protected boolean _isExpandedTypeID(int identity,int exptype)
  {
  	return walkIsExpandedTypeID(m_resolver.findNode(identity),exptype);
  }

  /**
   * Given a node handle, return an ID that represents the node's expanded name.
   *
   * @param nodeHandle The handle to the node in question.
   *
   * @return the expanded-name id of the node.
   */
  public int getExpandedTypeID(int nodeHandle)
  {
    // %REVIEW% This _should_ only be null if someone asked the wrong DTM about the node...
    // which one would hope would never happen...
    int id=makeNodeIdentity(nodeHandle);
    if(id==NULL)
      return NULL;
    return _exptype(id);
  }

  /**
   * Given an expanded name, return an ID.  If the expanded-name does not
   * exist in the internal tables, the entry will be created, and the ID will
   * be returned.  Any additional nodes that are created that have this
   * expanded name will use this ID.
   *
   * @param nodeHandle The handle to the node in question.
   * @param type The simple type, i.e. one of ELEMENT, ATTRIBUTE, etc.
   *
   * @param namespace The namespace URI, which may be null, may be an empty
   *                  string (which will be the same as null), or may be a
   *                  namespace URI.
   * @param localName The local name string, which must be a valid
   *                  <a href="http://www.w3.org/TR/REC-xml-names/">NCName</a>.
   *
   * @return the expanded-name id of the node.
   */
  public int getExpandedTypeID(String namespace, String localName, int type)
  {
    return m_expandedNameTable.getExpandedTypeID(namespace, localName, type);
  }

  /**
   * Given an expanded-name ID, return the local name part.
   *
   * @param ExpandedNameID an ID that represents an expanded-name.
   * @return String Local name of this node.
   */
  public String getLocalNameFromExpandedNameID(int expandedNameID)
  {
    return m_expandedNameTable.getLocalName(expandedNameID);
  }

  /**
   * Given an expanded-name ID, return the namespace URI part.
   *
   * @param ExpandedNameID an ID that represents an expanded-name.
   * @return String URI value of this node's namespace, or null if no
   * namespace was resolved.
   */
  public String getNamespaceFromExpandedNameID(int expandedNameID)
  {
    return m_expandedNameTable.getNamespace(expandedNameID);
  }

  /**
   * Returns the namespace type of a specific node
   * @param nodeHandle the id of the node.
   * @return the ID of the namespace.
   */
  public int getNamespaceType(final int nodeHandle)
  {

    int identity = makeNodeIdentity(nodeHandle);
    int expandedNameID = _exptype(identity);

    return m_expandedNameTable.getNamespaceID(expandedNameID);
  }

  /**
   * Given a node handle, return its DOM-style node name. This will
   * include names such as #text or #document.
   *
   * @param nodeHandle the id of the node.
   * @return String Name of this node, which may be an empty string.
   * %REVIEW% Document when empty string is possible...
   * %REVIEW-COMMENT% It should never be empty, should it?
   */
  public String getNodeName(int nodeHandle)
  {
  	Node node=m_resolver.findNode(makeNodeIdentity(nodeHandle));
    // Assume non-null.
    return node.getNodeName(); 
  }

  /**
   * Given a node handle, return the XPath node name.  This should be
   * the name as described by the XPath data model, NOT the DOM-style
   * name.
   *
   * @param nodeHandle the id of the node.
   * @return String Name of this node, which may be an empty string.
   */
  public String getNodeNameX(int nodeHandle)
  {
  	// %OPT% INEFFICIENT to go through expanded-type...?
  	
    String name;
	Node node;
    short type = getNodeType(nodeHandle);

    switch (type)
    {
    case DTM.NAMESPACE_NODE :
    	node = getNode(nodeHandle);
		if(node.getNodeName().equals("xmlns"))
			name = "";
		else
			name = node.getLocalName();
	    break;
    case DTM.ATTRIBUTE_NODE :
    case DTM.ELEMENT_NODE :
    case DTM.ENTITY_REFERENCE_NODE :
    case DTM.PROCESSING_INSTRUCTION_NODE :
    	node = getNode(nodeHandle);
		name = node.getNodeName();
		break;
    default :
		name = "";
    }

    return name;
  }

  /**
   * Given a node handle, return its XPath-style localname.
   * (As defined in Namespaces, this is the portion of the name after any
   * colon character).
   *
   * @param nodeHandle the id of the node.
   * @return String Local name of this node.
   */
  public String getLocalName(int nodeHandle)
  {
    int id=makeNodeIdentity(nodeHandle);
    if(NULL==id) return null;
    Node node=m_resolver.findNode(id);

    String name=node.getLocalName();
    if (null == name)
    {
		// XSLT treats PIs, and possibly other things, as having QNames.
		String qname = node.getNodeName();
		if('#'==node.getNodeName().charAt(0))
		{
		  //  Match old default for this function
		  // This conversion may or may not be necessary
		  name="";
		}
		else
		{
		  int index = qname.indexOf(':');
		  name = (index < 0) ? qname : qname.substring(index + 1);
		}
	}
	return name;
  }  

  /**
   * Given a namespace handle, return the prefix that the namespace decl is
   * mapping.
   * Given a node handle, return the prefix used to map to the namespace.
   *
   * @param nodeHandle the id of the node.
   * @return String prefix of this node's name, or "" if no explicit
   * namespace prefix was given.
   */
  public String getPrefix(int nodeHandle)
  {
  	// %OPT% Inefficient to go through the Expanded Type.
    String qname,prefix;
    Node node;
    int index;
    short type = getNodeType(nodeHandle);

    switch (type)
    {
    case DTM.NAMESPACE_NODE :
    	// XPath says the "prefix" of a Namespace Node is the
    	// prefix being defined (DOM localname), 
    	// not the prefix used to define it.
    	// Annoying, and IMO not useful, difference from DOM.
    	// ... But in fact, our other DTM implementations don't seem to
    	// present that view; they're returning DOM-style prefix.
    case DTM.ATTRIBUTE_NODE :
    case DTM.ELEMENT_NODE :
		node = getNode(nodeHandle);
		prefix=node.getPrefix();
		if(prefix==null) prefix="";
	    break;
    default :
      prefix = "";
    }

    return prefix;
  }

  /**
   * Given a node handle, return its XPath-style namespace URI
   * (As defined in Namespaces, this is the declared URI which this node's
   * prefix -- or default in lieu thereof -- was mapped to.)
   * Note that this is XPath, not DOM -- the namespace of a namespace
   * node is the namespace being declared, not the namespace 
   * for namespaces!
   *
   * <p>%REVIEW% Null or ""? -sb</p>
   *
   * @param nodeHandle the id of the node.
   * @return String URI value of this node's namespace, or null if no
   * namespace was resolved.
   */
  public String getNamespaceURI(int nodeHandle)
  {
      int id=makeNodeIdentity(nodeHandle);
      if(id==NULL) return null;
      Node node=m_resolver.findNode(id);
      
      if(node.getNodeType()==Node.ATTRIBUTE_NODE &&
      	NAMESPACE_URI_XMLNS.equals(node.getNamespaceURI()))
      	return node.getNodeValue(); // XPath namespace nodes "have no namespace"
      	
      return node.getNamespaceURI();
  }
  

  /**
   * Given a node handle, return its node value. This is mostly
   * as defined by the DOM, but may ignore some conveniences.
   * <p>
   *
   * @param nodeHandle The node id.
   * @return String Value of this node, or null if not
   * meaningful for this node type.
   */
  public String getNodeValue(int nodeHandle)
  {
    Node node = getNode(nodeHandle);
    int type = node.getNodeType();
    
    if(TEXT_NODE!=type && CDATA_SECTION_NODE!=type)
      return node.getNodeValue();
    
    // If this is a DTM text node, it may be made of multiple DOM text
    // nodes -- including navigating into Entity References. DOM2DTM
    // records the first node in the sequence and requires that we
    // pick up the others when we retrieve the DTM node's value.
    //
    // %REVIEW% DOM Level 3 is expected to add a "whole text"
    // retrieval method which performs this function for us.
    Node n=walkLogicalNextDOMTextNode(node);
    if(n==null)
      return node.getNodeValue();
    
    FastStringBuffer buf = StringBufferPool.get();
        buf.append(node.getNodeValue());
    while(n!=null)
    {
      buf.append(n.getNodeValue());
      n=walkLogicalNextDOMTextNode(n);
    }
    String s = (buf.length() > 0) ? buf.toString() : "";
    StringBufferPool.free(buf);
    return s;
  }
  

  /**
   * Given a node handle, return its DOM-style node type.
   * <p>
   * %REVIEW% Generally, returning short is false economy. Return int?
   *
   * @param nodeHandle The node id.
   * @return int Node type, as per the DOM's Node._NODE constants.
   */
  public short getNodeType(int nodeHandle)
  {
  	// %OPT% Should this operate off the DOM's nodeType and namespaceURI?
    return m_expandedNameTable.getType(_exptype(makeNodeIdentity(nodeHandle))); 
  }

  /**
   * <meta name="usage" content="internal"/>
   * Get the depth level of this node in the tree (equals 1 for
   * a parentless node).
   *
   * @param nodeHandle The node id.
   * @return the number of ancestors, plus one
   */
  public short getLevel(int nodeHandle)
  {
    // Apparently, the axis walker stuff requires levels to count from 1.
    int identity = makeNodeIdentity(nodeHandle);
    return (short) (_level(identity) + 1);
  }

  // ============== Document query functions ==============

  /**
   * Tests whether DTM DOM implementation implements a specific feature and
   * that feature is supported by this node.
   *
   * @param feature The name of the feature to test.
   * @param versionThis is the version number of the feature to test.
   *   If the version is not
   *   specified, supporting any version of the feature will cause the
   *   method to return <code>true</code>.
   * @param version The version string of the feature requested, may be null.
   * @return Returns <code>true</code> if the specified feature is
   *   supported on this node, <code>false</code> otherwise.
   */
  public boolean isSupported(String feature, String version)
  {

    // %TBD%
    return false;
  }

  /**
   * Return the base URI of the document entity. If it is not known
   * (because the document was parsed from a socket connection or from
   * standard input, for example), the value of this property is unknown.
   *
   * @return the document base URI String object or null if unknown.
   */
  public String getDocumentBaseURI()
  {
    return m_documentBaseURI;
  }

  /**
   * Set the base URI of the document entity.
   *
   * @param baseURI the document base URI String object or null if unknown.
   */
  public void setDocumentBaseURI(String baseURI)
  {
    m_documentBaseURI = baseURI;
  }

  /**
   * Return the system identifier of the document entity. If
   * it is not known, the value of this property is unknown.
   *
   * @param nodeHandle The node id, which can be any valid node handle.
   * @return the system identifier String object or null if unknown.
   */
  public String getDocumentSystemIdentifier(int nodeHandle)
  {
    // %REVIEW%  OK? -sb
    return m_documentBaseURI;
  }

  /**
   * Return the name of the character encoding scheme
   *        in which the document entity is expressed.
   *
   * @param nodeHandle The node id, which can be any valid node handle.
   * @return the document encoding String object.
   */
  public String getDocumentEncoding(int nodeHandle)
  {

    // %REVIEW%  OK??  -sb
    return "UTF-8";
  }

  /**
   * Return an indication of the standalone status of the document,
   *        either "yes" or "no". This property is derived from the optional
   *        standalone document declaration in the XML declaration at the
   *        beginning of the document entity, and has no value if there is no
   *        standalone document declaration.
   *
   * @param nodeHandle The node id, which can be any valid node handle.
   * @return the document standalone String object, either "yes", "no", or null.
   */
  public String getDocumentStandalone(int nodeHandle)
  {
    return null;
  }

  /**
   * Return a string representing the XML version of the document. This
   * property is derived from the XML declaration optionally present at the
   * beginning of the document entity, and has no value if there is no XML
   * declaration.
   *
   * @param documentHandle The document handle
   *
   * @return the document version String object.
   */
  public String getDocumentVersion(int documentHandle)
  {
    return null;
  }

  /**
   * Return an indication of
   * whether the processor has read the complete DTD. Its value is a
   * boolean. If it is false, then certain properties (indicated in their
   * descriptions below) may be unknown. If it is true, those properties
   * are never unknown.
   *
   * @return <code>true</code> if all declarations were processed;
   *         <code>false</code> otherwise.
   */
  public boolean getDocumentAllDeclarationsProcessed()
  {

    // %REVIEW% OK?
    return true;
  }

  /**
   *   A document type declaration information item has the following properties:
   *
   *     1. [system identifier] The system identifier of the external subset, if
   *        it exists. Otherwise this property has no value.
   *
   * @return the system identifier String object, or null if there is none.
   */
  public String getDocumentTypeDeclarationSystemIdentifier()
  {
    Document doc;

    if (m_root.getNodeType() == Node.DOCUMENT_NODE)
      doc = (Document) m_root;
    else
      doc = m_root.getOwnerDocument();

    if (null != doc)
    {
      DocumentType dtd = doc.getDoctype();

      if (null != dtd)
      {
        return dtd.getSystemId();
      }
    }

    return null;
  }
  

  /**
   * Return the public identifier of the external subset,
   * normalized as described in 4.2.2 External Entities [XML]. If there is
   * no external subset or if it has no public identifier, this property
   * has no value.
   *
   * @param the document type declaration handle
   *
   * @return the public identifier String object, or null if there is none.
   */
  public String getDocumentTypeDeclarationPublicIdentifier()
  {

    Document doc;

    if (m_root.getNodeType() == Node.DOCUMENT_NODE)
      doc = (Document) m_root;
    else
      doc = m_root.getOwnerDocument();

    if (null != doc)
    {
      DocumentType dtd = doc.getDoctype();

      if (null != dtd)
      {
        return dtd.getPublicId();
      }
    }

    return null;
  }

  /**
   * Returns the <code>Element</code> whose <code>ID</code> is given by
   * <code>elementId</code>. If no such element exists, returns
   * <code>DTM.NULL</code>. Behavior is not defined if more than one element
   * has this <code>ID</code>. Attributes (including those
   * with the name "ID") are not of type ID unless so defined by DTD/Schema
   * information available to the DTM implementation.
   * Implementations that do not know whether attributes are of type ID or
   * not are expected to return <code>DTM.NULL</code>.
   *
   * <p>%REVIEW% Presumably IDs are still scoped to a single document,
   * and this operation searches only within a single document, right?
   * Wouldn't want collisions between DTMs in the same process.</p>
   *
   * @param elementId The unique <code>id</code> value for an element.
   * @return The handle of the matching element.
   */
  public int getElementById(String elementId)
  {

    Document doc;

    if (m_root.getNodeType() == Node.DOCUMENT_NODE)
      doc = (Document) m_root;
    else
      doc = m_root.getOwnerDocument();

	return (doc==null) 
		? NULL
		: makeNodeHandle(m_resolver.findID(
			doc.getElementById( elementId),
			false // never a text node
			));
  }
  
  public NodeVector getElementByIdref(String elementIdref)
  {
    error(XSLMessages.createMessage(XSLTErrorResources.ER_METHOD_NOT_SUPPORTED, null));   
    return null;
  }

  /**
   * The getUnparsedEntityURI function returns the URI of the unparsed
   * entity with the specified name in the same document as the context
   * node (see [3.3 Unparsed Entities]). It returns the empty string if
   * there is no such entity.
   * <p>
   * XML processors may choose to use the System Identifier (if one
   * is provided) to resolve the entity, rather than the URI in the
   * Public Identifier. The details are dependent on the processor, and
   * we would have to support some form of plug-in resolver to handle
   * this properly. Currently, we simply return the System Identifier if
   * present, and hope that it a usable URI or that our caller can
   * map it to one.
   * TODO: Resolve Public Identifiers... or consider changing function name.
   * <p>
   * If we find a relative URI
   * reference, XML expects it to be resolved in terms of the base URI
   * of the document. The DOM doesn't do that for us, and it isn't
   * entirely clear whether that should be done here; currently that's
   * pushed up to a higher level of our application. (Note that DOM Level
   * 1 didn't store the document's base URI.)
   * TODO: Consider resolving Relative URIs.
   * <p>
   * (The DOM's statement that "An XML processor may choose to
   * completely expand entities before the structure model is passed
   * to the DOM" refers only to parsed entities, not unparsed, and hence
   * doesn't affect this function.)
   *
   * @param name A string containing the Entity Name of the unparsed
   * entity.
   *
   * @return String containing the URI of the Unparsed Entity, or an
   * empty string if no such entity exists.
   */
  public String getUnparsedEntityURI(String name)
  {
  	// %OPT%
    String url = "";
    Document doc = (m_root.getNodeType() == Node.DOCUMENT_NODE) 
        ? (Document) m_root : m_root.getOwnerDocument();

    if (null != doc)
    {
      DocumentType doctype = doc.getDoctype();
  
      if (null != doctype)
      {
        NamedNodeMap entities = doctype.getEntities();
        if(null == entities)
          return url;
        Entity entity = (Entity) entities.getNamedItem(name);
        if(null == entity)
          return url;
        
        String notationName = entity.getNotationName();
  
        if (null != notationName)  // then it's unparsed
        {
          // The draft says: "The XSLT processor may use the public 
          // identifier to generate a URI for the entity instead of the URI 
          // specified in the system identifier. If the XSLT processor does 
          // not use the public identifier to generate the URI, it must use 
          // the system identifier; if the system identifier is a relative 
          // URI, it must be resolved into an absolute URI using the URI of 
          // the resource containing the entity declaration as the base 
          // URI [RFC2396]."
          // So I'm falling a bit short here.
          url = entity.getSystemId();
  
          if (null == url)
          {
            url = entity.getPublicId();
          }
          else
          {
            // This should be resolved to an absolute URL, but that's hard 
            // to do from here.
          }        
        }
      }
    }

    return url;
  }
  

  // ============== Boolean methods ================

  /**
   * Return true if the xsl:strip-space or xsl:preserve-space was processed
   * during construction of the DTM document.
   *
   * @return true if this DTM supports prestripping.
   */
  public boolean supportsPreStripping()
  {
    return true; // %REVIEW% We don't know, actually...
  }

  /**
   * Figure out whether nodeHandle2 should be considered as being later
   * in the document than nodeHandle1, in Document Order as defined
   * by the XPath model. This may not agree with the ordering defined
   * by other XML applications.
   * <p>
   * There are some cases where ordering isn't defined, and neither are
   * the results of this function.
   * <p>
   * Performance is likely to be lousy.
   * <p>
   * %REVIEW% You know, that's a poorly named function...
   *
   * @param nodeHandle1 Node handle to perform position comparison on.
   * @param nodeHandle2 Second Node handle to perform position comparison on .
   *
   * @return true if node1 comes before node2, otherwise return false.
   * You can think of this as
   * <code>(node1.documentOrderPosition &lt;= node2.documentOrderPosition)</code>.
   */
  public boolean isNodeAfter(int nodeHandle1, int nodeHandle2)
  {
  	Node n1=m_resolver.findNode(makeNodeIdentity(nodeHandle1));
  	if(n1==null)
 		return false;
  	Node n2=m_resolver.findNode(makeNodeIdentity(nodeHandle2));
  	if(n2==null)
 		return true;

	// Special case:
  	// NS before attr within single owning element
  	if(n1.getNodeType()==Node.ATTRIBUTE_NODE &&
  		n1.getNodeType()==Node.ATTRIBUTE_NODE &&
  		NAMESPACE_URI_XMLNS.equals(n1.getNamespaceURI()) &&
  		! NAMESPACE_URI_XMLNS.equals(n1.getNamespaceURI()) &&
  		m_resolver.isSameNode(
  				((Attr)n1).getOwnerElement(),
  				((Attr)n2).getOwnerElement())
  		)
  		return false;
  	
  	// Else as per DOM's concept of doc order 	
  	// May be optimized in some DOMs, so push it off to them
	return m_resolver.isNodeOrder(n1,n2);
  }

  /**
   *     2. [element content whitespace] A boolean indicating whether the
   *        character is white space appearing within element content (see [XML],
   *        2.10 "White Space Handling"). Note that validating XML processors are
   *        required by XML 1.0 to provide this information. If there is no
   *        declaration for the containing element, this property has no value for
   *        white space characters. If no declaration has been read, but the [all
   *        declarations processed] property of the document information item is
   *        false (so there may be an unread declaration), then the value of this
   *        property is unknown for white space characters. It is always false for
   *        characters that are not white space.
   *
   * @param nodeHandle the node ID.
   * @return <code>true</code> if the character data is whitespace;
   *         <code>false</code> otherwise.
   */
  public boolean isCharacterElementContentWhitespace(int nodeHandle)
  {

    // %TBD%
    return false;
  }

  /**
   *    10. [all declarations processed] This property is not strictly speaking
   *        part of the infoset of the document. Rather it is an indication of
   *        whether the processor has read the complete DTD. Its value is a
   *        boolean. If it is false, then certain properties (indicated in their
   *        descriptions below) may be unknown. If it is true, those properties
   *        are never unknown.
   *
   * @param the document handle
   *
   * @param documentHandle A node handle that must identify a document.
   * @return <code>true</code> if all declarations were processed;
   *         <code>false</code> otherwise.
   */
  public boolean isDocumentAllDeclarationsProcessed(int documentHandle)
  {
    return true;
  }

  /**
   *     5. [specified] A flag indicating whether this attribute was actually
   *        specified in the start-tag of its element, or was defaulted from the
   *        DTD.
   *
   * @param attributeHandle The attribute handle in question.
   *
   * @return <code>true</code> if the attribute was specified;
   *         <code>false</code> if it was defaulted.
   */
  public boolean isAttributeSpecified(int attributeHandle)
  {
    int type = getNodeType(attributeHandle);

    if (DTM.ATTRIBUTE_NODE == type)
    {
      Attr attr = (Attr)getNode(attributeHandle);
      return attr.getSpecified();
    }
    return false;  
  }

  // ========== Direct SAX Dispatch, for optimization purposes ========

  /**
   * Directly call the
   * characters method on the passed ContentHandler for the
   * string-value of the given node (see http://www.w3.org/TR/xpath#data-model
   * for the definition of a node's string-value). Multiple calls to the
   * ContentHandler's characters methods may well occur for a single call to
   * this method.
   *
   * @param nodeHandle The node ID.
   * @param ch A non-null reference to a ContentHandler.
   * @param normalize true if the content should be normalized according to
   * the rules for the XPath
   * <a href="http://www.w3.org/TR/xpath#function-normalize-space">normalize-space</a>
   * function.
   *
   * @throws org.xml.sax.SAXException
   */
  public void dispatchCharactersEvents(
    int nodeHandle, org.xml.sax.ContentHandler ch, boolean normalize)
      throws org.xml.sax.SAXException
  {
    if(normalize)
    {
      XMLString str = getStringValue(nodeHandle);
      str = str.fixWhiteSpace(true, true, false);
      str.dispatchCharactersEvents(ch);
    }
    else
    {
      int type = getNodeType(nodeHandle);
      Node node = getNode(nodeHandle);
      dispatchNodeData(node, ch, 0);
          // Text coalition -- a DTM text node may represent multiple
          // DOM nodes.
          if(TEXT_NODE == type || CDATA_SECTION_NODE == type)
          {
                  while( null != (node=walkLogicalNextDOMTextNode(node)) )
                  {
                      dispatchNodeData(node, ch, 0);
                  }
          }
    }
  }

  /**
   * Retrieve the text content of a DOM subtree, appending it into a
   * user-supplied FastStringBuffer object. Note that attributes are
   * not considered part of the content of an element.
   * <p>
   * There are open questions regarding whitespace stripping. 
   * Currently we make no special effort in that regard, since the standard
   * DOM doesn't yet provide DTD-based information to distinguish
   * whitespace-in-element-context from genuine #PCDATA. Note that we
   * should probably also consider xml:space if/when we address this.
   * DOM Level 3 may solve the problem for us.
   * <p>
   * %REVIEW% Note that as a DOM-level operation, it can be argued that this
   * routine _shouldn't_ perform any processing beyond what the DOM already
   * does, and that whitespace stripping and so on belong at the DTM level.
   * If you want a stripped DOM view, wrap DTM2DOM around DOM2DTM.
   *
   * @param node Node whose subtree is to be walked, gathering the
   * contents of all Text or CDATASection nodes.
   * @param buf FastStringBuffer into which the contents of the text
   * nodes are to be concatenated.
   */
  protected static void dispatchNodeData(Node node, 
                                         org.xml.sax.ContentHandler ch, 
                                         int depth)
            throws org.xml.sax.SAXException
  {

    switch (node.getNodeType())
    {
    case Node.DOCUMENT_FRAGMENT_NODE :
    case Node.DOCUMENT_NODE :
    case Node.ELEMENT_NODE :
    {
      for (Node child = node.getFirstChild(); null != child;
              child = child.getNextSibling())
      {
        dispatchNodeData(child, ch, depth+1);
      }
    }
    break;
    case Node.PROCESSING_INSTRUCTION_NODE : // %REVIEW%
    case Node.COMMENT_NODE :
      if(0 != depth)
        break;
        // NOTE: Because this operation works in the DOM space, it does _not_ attempt
        // to perform Text Coalition. That should only be done in DTM space. 
    case Node.TEXT_NODE :
    case Node.CDATA_SECTION_NODE :
    case Node.ATTRIBUTE_NODE :
      String str = node.getNodeValue();
      if(ch instanceof DOM2DTM.CharacterNodeHandler)
      {
        ((DOM2DTM.CharacterNodeHandler)ch).characters(node);
      }
      else
      {
        ch.characters(str.toCharArray(), 0, str.length());
      }
      break;
//    /* case Node.PROCESSING_INSTRUCTION_NODE :
//      // warning(XPATHErrorResources.WG_PARSING_AND_PREPARING);        
//      break; */
    default :
      // ignore
      break;
    }
  }
  
  TreeWalker m_walker = new TreeWalker(null);

  /**
   * Directly create SAX parser events from a subtree.
   *
   * @param nodeHandle The node ID.
   * @param ch A non-null reference to a ContentHandler.
   *
   * @throws org.xml.sax.SAXException
   */
  public void dispatchToEvents(
    int nodeHandle, org.xml.sax.ContentHandler ch)
      throws org.xml.sax.SAXException
  {
    TreeWalker treeWalker = m_walker;
    ContentHandler prevCH = treeWalker.getContentHandler();
    
    if(null != prevCH)
    {
      treeWalker = new TreeWalker(null);
    }
    treeWalker.setContentHandler(ch);
    
    try
    {
      Node node = getNode(nodeHandle);
      treeWalker.traverse(node);
    }
    finally
    {
      treeWalker.setContentHandler(null);
    }

  }

  /**
   * Return an DOM node for the given node.
   *
   * @param nodeHandle The node ID.
   *
   * @return A node representation of the DTM node.
   */
  public org.w3c.dom.Node getNode(int nodeHandle)
  {
    int identity = makeNodeIdentity(nodeHandle);
    return m_resolver.findNode(identity);
  }
  
  /**
   * Given a W3C DOM node, ask whether this DTM knows of a Node Handle
   * associated with it. Generally, returns a valid handle only if the
   * Node is actually mapped by this DTM (eg, because it's a DOM2DTM
   * which contains that Node).
   *
   * @param node Non-null reference to a DOM node.
   *
   * @return a DTM node handle, or DTM.NULL if this DTM doesn't
   * recognize the provided DOM node.
   */
  public int getDTMHandleFromNode(org.w3c.dom.Node node)
  {
  	// If it's a DTM simulated node, it may know its handle.
  	// %OPT% Make these share a common interface so we can test-and-call just once!
  	if(node instanceof DTMNodeProxy)
  		return ((DTMNodeProxy)node).getDTMNodeNumber();
  	else if(node instanceof DOM2DTMdefaultNamespaceDeclarationNode)
  		return ((DOM2DTMdefaultNamespaceDeclarationNode)node).getHandleOfNode();
  	
  	// Otherwise, see if it's one this DTM covers.
	// This would be easier if m_root was always the Document node, but
	// we decided to allow wrapping a DTM around a subtree.
  	// %REVIEW% Is this test adequate? What about disjoint subtrees?
    if((m_root==node) ||
       (m_root.getNodeType()==DOCUMENT_NODE &&
        m_root==node.getOwnerDocument()) ||
       (m_root.getNodeType()!=DOCUMENT_NODE &&
        m_root.getOwnerDocument()==node.getOwnerDocument())
       )
    {
		int identity=m_resolver.findID(node);
		return makeNodeHandle(identity);
    }
    
    return DTM.NULL; // Not ours.        
  }
  
  /**
   * Return a DTM handle for the given DOM node, if that can be determined
   * by this DTM. If we don't know, we will return NULL.
   * 
   * Note that in some cases this will be a very slow operation. Also note
   * that it may require testing Node identity -- which wasn't standardized
   * before DOM Level 3 introduced isSameNode().
   *
   * @param node The Node reference.
   *
   * @return the DTM node handle covering this DOM node, or DTM.NULL if
   * this DTM instance can not determine that value for this node.
   */
  public int getDTMHandleFromNode_lightweight(org.w3c.dom.Node node)
  {
  	if(node instanceof DTMNodeProxy)
  		return ((DTMNodeProxy)node).getDTMNodeNumber();
  	else if(node instanceof DOM2DTMdefaultNamespaceDeclarationNode)
  		return ((DOM2DTMdefaultNamespaceDeclarationNode)node).getHandleOfNode();
  	else
  	  return makeNodeHandle(m_resolver.findID(node));
  }
  

  // ==== Construction methods (may not be supported by some implementations!) =====

  /**
   * Append a child to the end of the document. Please note that the node
   * is always cloned if it is owned by another document.
   *
   * <p>%REVIEW% "End of the document" needs to be defined more clearly.
   * Does it become the last child of the Document? Of the root element?</p>
   *
   * @param newChild Must be a valid new node handle.
   * @param clone true if the child should be cloned into the document.
   * @param cloneDepth if the clone argument is true, specifies that the
   *                   clone should include all it's children.
   */
  public void appendChild(int newChild, boolean clone, boolean cloneDepth)
  {
    error(XSLMessages.createMessage(XSLTErrorResources.ER_METHOD_NOT_SUPPORTED, null));//"appendChild not yet supported!");
  }

  /**
   * Append a text node child that will be constructed from a string,
   * to the end of the document.
   *
   * <p>%REVIEW% "End of the document" needs to be defined more clearly.
   * Does it become the last child of the Document? Of the root element?</p>
   *
   * @param str Non-null reverence to a string.
   */
  public void appendTextChild(String str)
  {
    error(XSLMessages.createMessage(XSLTErrorResources.ER_METHOD_NOT_SUPPORTED, null));//"appendTextChild not yet supported!");
  }

  /**
   * Simple error for asserts and the like.
   *
   * @param msg Error message to report.
   */
  protected void error(String msg)
  {
    throw new DTMException(msg);
  }

  /**
   * A dummy routine to satisify the abstract interface. If the DTM
   * implementation that extends the default base requires notification
   * of registration, they can override this method.
   */
   public void documentRegistration()
   {
   }

  /**
   * A dummy routine to satisify the abstract interface. If the DTM
   * implememtation that extends the default base requires notification
   * when the document is being released, they can override this method
   */
   public void documentRelease()
   {
   }

	 /** Query which DTMManager this DTM is currently being handled by.
	  * 
	  * @return a DTMManager, or null if this is a "stand-alone" DTM.
	  */
	 public DTMManager getManager()
	 {
		 return m_mgr;
	 }

	 /** Query which DTMIDs this DTM is currently using within the DTMManager.
	  * 
	  * %REVEW% Should this become part of the base DTM API?
	  * 
	  * @return an IntVector, or null if this is a "stand-alone" DTM.
	  */
	 public SuballocatedIntVector getDTMIDs()
	 {
		 if(m_mgr==null) return null;
		 return m_dtmIdent;
	 }

	//---------------------------------------------------------------	 
	// Fill in inherited abstract methods
	
	/**
	  * Return this DTM's content handler, if it has one.
	  *
	  * @return null if this model doesn't respond to SAX events.
	  */
	public org.xml.sax.ContentHandler getContentHandler()
	{
		return null;
	}
   /**
    * Return this DTM's lexical handler, if it has one.
    *
    * %REVIEW% Should this return null if constrution already done/begun?
    *
    * @return null if this model doesn't respond to lexical SAX events.
    */
   public org.xml.sax.ext.LexicalHandler getLexicalHandler()
	{
		return null;
	}

  /**
   * Return this DTM's EntityResolver, if it has one.
   *
   * @return null if this model doesn't respond to SAX entity ref events.
   */
  public org.xml.sax.EntityResolver getEntityResolver()
	{
		return null;
	}

  /**
   * Return this DTM's DTDHandler, if it has one.
   *
   * @return null if this model doesn't respond to SAX dtd events.
   */
  public org.xml.sax.DTDHandler getDTDHandler()
	{
		return null;
	}

  /**
   * Return this DTM's ErrorHandler, if it has one.
   *
   * @return null if this model doesn't respond to SAX error events.
   */
  public org.xml.sax.ErrorHandler getErrorHandler()
	{
		return null;
	}

  /**
   * Return this DTM's DeclHandler, if it has one.
   *
   * @return null if this model doesn't respond to SAX Decl events.
   */
  public org.xml.sax.ext.DeclHandler getDeclHandler()
	{
		return null;
	}
  
  /**
   * Get the location of a node in the source document.
   *
   * @param node an <code>int</code> value
   * @return a <code>SourceLocator</code> value or null if no location
   * is available
   */
  public SourceLocator getSourceLocatorFor(int node)
	{
		return null;
	}

  /**
   * @return false, telling the DTMManager that we do not require
   * multithreading of parsing and transformation.
   */
  public boolean needsTwoThreads()
  {
  	return false;
  }
    
  /**
   * Set a run time property for this DTM instance.
   *
   * @param property a <code>String</code> value
   * @param value an <code>Object</code> value
   */
  public void setProperty(String property, Object value)
  {
	return; // None currently implemented.
  }  
  
    //---------------------------------------------------------------	 

  /**
    * EXPERIMENTAL XPath2 Support:
    * 
    * Query schema type name of a given node.
    * 
    * %REVIEW% Is this actually needed?
    * 
    * @param nodeHandle DTM Node Handle of Node to be queried
    * @return null if no type known, else returns the expanded-QName (namespace URI
    *	rather than prefix) of the type actually
    *    resolved in the instance document. Note that this may be derived from,
    *	rather than identical to, the type declared in the schema.
    */
   public String getSchemaTypeName(int nodeHandle)
   { return null; }
  	
  /** 
    * EXPERIMENTAL XPath2 Support:
    * 
	* Query schema type namespace of a given node.
    * 
    * %REVIEW% Is this actually needed?
    * 
    * @param nodeHandle DTM Node Handle of Node to be queried
    * @return null if no type known, else returns the namespace URI
    *	of the type actually resolved in the instance document. This may
    * 	be null if the default/unspecified namespace was used.
    *    Note that this may be derived from,
    *	rather than identical to, the type declared in the schema.
    */
   public String getSchemaTypeNamespace(int nodeHandle)
   { return null; }

  /** EXPERIMENTAL XPath2 Support: Query schema type localname of a given node.
   * 
   * %REVIEW% Is this actually needed?
   * 
   * @param nodeHandle DTM Node Handle of Node to be queried
   * @return null if no type known, else returns the localname of the type
   *    resolved in the instance document. Note that this may be derived from,
   *	rather than identical to, the type declared in the schema.
   */
  public String getSchemaTypeLocalName(int nodeHandle)
   { return null; }

  /** EXPERIMENTAL XPath2 Support: Query whether node's type is derived from a specific type
   * 
   * @param nodeHandle DTM Node Handle of Node to be queried
   * @param namespace String containing URI of namespace for the type we're intersted in
   * @param localname String containing local name for the type we're intersted in
   * @return true if node has a Schema Type which equals or is derived from 
   *	the specified type. False if the node has no type or that type is not
   * 	derived from the specified type.
   */
  public boolean isNodeSchemaType(int nodeHandle, String namespace, String localname)
   { return false; }
  
  /** EXPERIMENTAL XPath2 Support: Retrieve the typed value(s), based on the schema
   *  type.
   * 
   * @param nodeHandle DTM Node Handle of Node to be queried
   * @return XSequence object containing one or more values and their type
   * information. If no typed value is available, returns an empty sequence.
   * */
  public DTMSequence getTypedValue(int nodeHandle)
   {return DTMSequence.EMPTY;}
	 
}
