package org.apache.xml.dtm.dom2dtm2;
import java.util.Vector;

import org.apache.xml.dtm.DTM;
import org.apache.xml.utils.NodeVector;
import org.apache.xml.dtm.ref.dom2dtm.DOM2DTMdefaultNamespaceDeclarationNode;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.traversal.TreeWalker;

import org.apache.xerces.dom.NodeImpl;


/** Implement the <code>NodeDTMIDResolver</code> for a DOM which
 * supports the DOM Level 3 userData feature, or at least supports
 * those methods. Specifically hardcoded for Xerces; to adapt to
 * others, replace the (NodeImpl) cast appropriately.
 * 
 * Strategy: Use userData to bind an Integer object to the node the
 * first time someone asks for its ID. 
 * 
 * This wrapper also binds the 
 * Namespace Node List, since computing that (especially given DTM's
 * minimal-state getNextNamespaceNode call) is something of a pain,
 * and since it really wants to be computed in the DTM's space to
 * better prepare us to leverage DOM implementation features.
 * %REVIEW% Currently that's stored as another userData. May be better
 * (more reusable, maybe more compact) to use a SparseVector indexed on the
 * Node ID. Preformance probably comparable.
 * 
 * @author Joe Kesselman
 * @since Sep 6, 2002
 */
public class NodeDTMIDResolver_xerces implements NodeDTMIDResolver
{
	static final boolean JJK_DEBUG=true;
	
	
	// Manefest constant; see isNodeOrder comments
	private static final boolean ORDER_UNDEFINED=false;

	// Manefest constant: userData keys
	static final String KEY_BOUND_RESOLVER="xalan:bound_xerces_NodeDTMIDResolver";
	static final String KEY_ID="xalan:id_NodeDTMIDResolver";
	static final String KEY_NS="xalan:ns_NodeDTMIDResolver";
	
	// Manefest constant: Namespace for xml: prefix
	static final String NAMESPACE_URI_XML="http://www.w3.org/XML/1998/namespace";
	// Manefest constant: Namespace for xmlns: prefix
	static final String NAMESPACE_URI_XMLNS="http://www.w3.org/2000/xmlns/";

	// ID to Node map
	// We'd _like_ to use the map's length as our counter. Problem is,
	// that breaks down if the same DOM is ever fed to another resolver;
	// duplication of IDs would be produced. Best solution I can
	// think of is to store the resolver into the DOM and make
	// getInstance check for that possibility.
	java.util.Vector m_map=new Vector();
	
	/* Check whether we can support this DOM, and if so manufacture
	 * an instance. If we can't, return null.
	 * */	
	public static NodeDTMIDResolver getInstance(DOMImplementation domimpl,Document doc)
	{
		if(doc instanceof NodeImpl)
		try
		{
			// Sanity check -- make sure the methods are really there
			java.lang.reflect.Method m=doc.getClass().getMethod("getUserData",new Class[]{String.class});			
			
			// First question: Is a resolver already bound?			
			NodeDTMIDResolver resolver=(NodeDTMIDResolver)((NodeImpl)doc).getUserData(KEY_BOUND_RESOLVER);
			if(resolver!=null)
				return resolver;
			
			resolver=new NodeDTMIDResolver_xerces();
			// Make sure the Document node will be node 0, just for
			// clarity/consistancy/simplicity of retrofit.
			resolver.findID(doc,false); // never a text node
			
			((NodeImpl)doc).setUserData(KEY_BOUND_RESOLVER,resolver,null);
			return resolver;
		}
		catch(java.lang.NoSuchMethodException e)
		{
			// No recovery
		}
		
		return null; // Not for us.
	}

	/** Given a node, return its unique-within-Document ID number.
	 * This is implemented uniquely in each concrete resolver class.
	 * 
	 * Note that some mapping from DOM to XPath should be performed
	 * -- specifically, findID should "skip" EntityReference nodes,
	 * and seek to the first of a logically-adjacent set of Text nodes.
	 * 
	 * Here, we use the pre-release DOM Level 3 APIs by casting to
	 * Xerces-specific classes, and do the node-to-ID binding via
	 * userData. Reverse binding is done via a vector.
	 * 
	 * %REVIEW%
	 * NOTE: Xerces nodes are claimed to be hashable, since that's what they
	 * use for their internal implementation of userData. We could bypass
	 * their system and use that. The code here generalizes to be
	 * the DOM3-native solution; we may also want to retain a 
	 * custom Xerces version which leverages that.
	 * */
	public int findID(Node n)
	{
		return findID(n,true);
	}
	
	/** PACKAGE-INTERNAL node-to-ID lookup, used within DOM2DTM2. If we're scanning
	 * forward (and thus know that any text nodes encountered will be the
	 * first in their logically-adjacent text block) we don't need to spend
	 * cycles calling the (expensive) findContainingXPathNode.
	 * */
	public int findID(Node n,boolean fixupTextNodes)	
	{
	  	// %REVIEW% Should we have stored which doc we're applying to,
  		// and enforce membership? Probably good idea since we're
	  	// relying on implementation characteristics.
	  	
		if(n==null) return DTM.NULL;

		// If it's our built-in, we've got a custom method
		if(n instanceof DOM2DTMdefaultNamespaceDeclarationNode)
		{
		  return
			((DOM2DTMdefaultNamespaceDeclarationNode)n).getIDOfNode();
		}

		int ntype=n.getNodeType();
		Integer id;

		NodeImpl n3=(NodeImpl)n; 
		id=(Integer)n3.getUserData(KEY_ID);
		
		if(id==null)
		{
		  switch(ntype)
		  {
		  case Node.TEXT_NODE:
		  case Node.CDATA_SECTION_NODE:
		  case Node.ENTITY_REFERENCE_NODE:
		    // Special handling for text nodes: Map them
		    // as the first node in their Logically
		    // Consecutive Text block.
		    //
		    // %REVIEW% Should this lookup be recursive, so IDs are
		    // set on all the intermediates as a side effect?
		    //
		    // Get the "containing" node; it may be same as n.
		    NodeImpl root;
		    root=(fixupTextNodes)
		    	? (NodeImpl)findContainingXPathNode(n)
				: (NodeImpl)n;
				
		    id=(Integer)root.getUserData(KEY_ID); 
		    if(id==null)
		    {
		      // Assign ID. Note sequence; first is added at 0.
		      id=new Integer(m_map.size());
		      root.setUserData(KEY_ID,id,null);
		      m_map.addElement(root);
		    }
		    if(root!=n3)
			    n3.setUserData(KEY_ID,id,null);
		    break;
		    
		  default:
		      // Assign ID. Note sequence; first is added at 0.
		      id=new Integer(m_map.size());
		      n3.setUserData(KEY_ID,id,null);
		      m_map.addElement(n);
		    break;

		  }// end switch
		}// end if ID was null
		
		// If this throws NPE, something above is Broken
		return id.intValue();		
		
	}

	/** Given unique-within-Document ID number, return its Node
	 * This is implemented uniquely in each concrete resolver class.
	 * 
	 * Out of range values will throw exceptions.
	 * */
	public Node findNode(int id)
	{
		if(id==DTM.NULL) return null;
		
		return (Node)m_map.elementAt(id);
	}
	
	/** If it's a Text node, we should find the first Text node
	 * in sequence (backward depth-first walk through EntRefs).
	 * If it's an EntRef, we should find the first non-EntRef
	 * preceeding it.
	 * If it's in the Doctype set... return null?
	 * */
	Node findContainingXPathNode(Node n)
	{
		int ntype=n.getNodeType();
		switch(ntype)
		{
			case Node.DOCUMENT_TYPE_NODE:
			case Node.ENTITY_NODE:
			case Node.NOTATION_NODE:
				return null;
				
			case Node.ENTITY_REFERENCE_NODE:
			case Node.TEXT_NODE:
			case Node.CDATA_SECTION_NODE:
				Node preceeding;
				// Note that the walker filters out EntRefs.
				while ((preceeding=previousSkipEntRefNode(n))!=null)
				{
					int ptype=preceeding.getNodeType();
					
					// Found acceptable nontext XPath-model node?
					if(ptype!=Node.TEXT_NODE
						&& ptype!=Node.CDATA_SECTION_NODE)
					{
						// If we found a text before it, take
						// that instead.
						if(ntype!=Node.TEXT_NODE
							&& ntype!=Node.CDATA_SECTION_NODE)
							return preceeding;
						else
							return n;
					}
					n=preceeding;
					ntype=ptype;
				} 
	
				// Shouldn't happen ... Assume orphan Text node?
				// %REVIEW%
				return n;
					
				
			default:
				return n;
		}
	}

    /** Leftward iterative stateless DOM walker.
	 * Logic adapted from Xerces traverser code, which swiped it from
	 * Xerces Deep Nodelist code, both of which I wrote the first
	 * drafts of... Could have just used a TreeWalker object here, but
	 * because the ID Resolver is stateless this seems simpler.
	 * 
	 * It's as reentrant as the DOM(s) it's operating on -- no less
	 * and no more. Caveat hackitor.
	 * 
	 * @param startNode DOM node to start scanning from
	 * @return the node which preceeds this one in document order,
	 * skipping EntityReference nodes. If none found, returns null.
     */
    public Node previousSkipEntRefNode(Node startNode) {
    	Node result;
        
        if (startNode == null) return null;
            
        do // Loop to skip EntRef nodes
        {
	        // get previous sibling
    	    result = startNode.getPreviousSibling();
    	    
            // if none, get parent instead
	        if (result == null) {
	            startNode = startNode.getParentNode();
                continue; // We have candidate; test for EntRef
	        }
	        
	        // get the rightmost descendent of previous sib
	        Node lastChild  = result.getLastChild();
	        Node prevLastChild = lastChild ;
	        while (lastChild != null) {
	          prevLastChild = lastChild ;
	          lastChild = prevLastChild.getLastChild();
	        }
	        lastChild = prevLastChild; // Take last good...
	        
	        // If there is a lastChild return it rather than prev-sib
 	        if (lastChild != null)
	            startNode=lastChild;
	        else
		        startNode=result;	        
 			// We have candidate; fall through to test for EntRef
        } while (startNode!=null && startNode.getNodeType()!=Node.ENTITY_REFERENCE_NODE);
        
        return startNode; // May be null if we ran out of piggies
    }

    /** Rightward iterative stateless DOM walker.
	 * Logic adapted from Xerces traverser code, which swiped it from
	 * Xerces Deep Nodelist code, both of which I wrote the first
	 * drafts of... Could have just used a TreeWalker object here, but
	 * because the ID Resolver is stateless this seems simpler.
	 * 
	 * It's as reentrant as the DOM(s) it's operating on -- no less
	 * and no more. Caveat hackitor.
	 * 
	 * @param startNode DOM node to start scanning from
	 * @return the node which follows this one in document order,
	 * skipping EntityReference nodes. If none found, returns null.
     */
    public Node nextSkipEntRefNode(Node startNode) {
    	Node result;
        
        if (startNode == null) return null;
            
        do // Loop to skip EntRef nodes
        {
	        // get next sibling
    	    result = startNode.getNextSibling();
    	    
            // if none, get parent instead
	        if (result == null) {
	            startNode = startNode.getParentNode();
                continue; // We have candidate; test for EntRef
	        }
	        
	        // get the rightmost descendent of next sib
	        Node firstChild  = result.getFirstChild();
	        Node prevFirstChild = firstChild ;
	        while (firstChild != null) {
	          prevFirstChild = firstChild ;
	          firstChild = prevFirstChild.getFirstChild();
	        }
	        firstChild = prevFirstChild; // Take last good...
	        
	        // If there is a firstChild return it rather than prev-sib
 	        if (firstChild != null)
	            startNode=firstChild;
	        else
		        startNode=result;	        
 			// We have candidate; fall through to test for EntRef
        } while (startNode.getNodeType()!=Node.ENTITY_REFERENCE_NODE);
        
        return startNode; // May be null if we ran out of piggies
    }
    
    public boolean isSameNode(Node n1, Node n2)
    {
    	// In this DOM, we can use object identity *OR*
    	// the DOM Level 3 isSameNode test... The latter currently
    	// requires casting to implementation-specific API, so I'd
    	// rather take the sloppy solution for now.
    	return n1==n2;
    }
    
    public Vector getNamespacesInScope(Node n, boolean flush)
    {
	  	// Find an element to resolve this at (minimize storage)
  		for(int ntype=n.getNodeType();
	  		ntype!=Node.ELEMENT_NODE;
  			ntype=n.getNodeType())
	    {
   	   		Node parent=n.getParentNode();
			if(parent==null && ntype==Node.ATTRIBUTE_NODE)
				parent=((Attr)n).getOwnerElement();
       		if(parent==null)
       			return null;
       		n=parent;
	    }
	    
		NodeImpl n3=(NodeImpl)n; 

	    // Are we willing to accept cached value?
	    // %OPT% We need to find a smarter way to handle this...
	    // but mutation events can be expensive, esp. if we need to
	    // walk the whole subtree of a document. 
	    if(!flush)
	    {
		    // Does this element already have a namespace annotation?
			Vector v=(Vector)n3.getUserData(KEY_NS);
			if(v!=null)
			return v;
	    }
			
		// Begin computation by recursing for inheritance
		Node parent=n3.getParentNode();
		Vector v=null;
		Vector inherited=(parent==null)
			?null
			:getNamespacesInScope(parent,flush);
		
		// If nothing inherited, seed with pseudodeclaration for
		// XML: prefix. Note that due to recursion, this will be
		// be done only on the root and then be inherited
		// down the chain.
		//
		// We _COULD_ do similar for the implicit declarations
		// implied by element and attribute prefix/URI bindings.
		// I don't think I want to go there right now.
		// %REVIEW%
		if(inherited==null)
		{
			Attr implicit_declaration;
			
			// PROBLEM: We don't want to remanufacture this node each
			// time we flush. (We could, but that would require special-casing
			// in isSameNode). So even if we're in a flush cycle, we want to
			// check whether it pre-exists and retain it if so.
			Vector prev=(Vector)n3.getUserData(KEY_NS);
			if(prev!=null)
			{
				// if it exists, it will be first in list
				implicit_declaration=(Attr)prev.firstElement();
			}
			else
			{			
				// PROBLEM: This is set up to store/return handle.
				// I want ID!
				// %REVIEW% %BUG%
				implicit_declaration=
					new DOM2DTMdefaultNamespaceDeclarationNode(
						(Element)n3,	// pseudoparent
						"xml",			// prefix
						NAMESPACE_URI_XML, //declared namespace uri
						-99999, 		// handle -- WRONG!!!!!!! GONK!!!
						m_map.size());	// id 
				m_map.addElement(implicit_declaration);
			}
			v=new Vector(1,5);
			v.addElement(implicit_declaration);
		}
		
		// Now add any local declarations.
		NamedNodeMap attrs=n3.getAttributes();
		Node attr=null;
		int which=0;
		while( null!=(attr=attrs.item(which++)) )
		{
			if(NAMESPACE_URI_XMLNS.equals(attr.getNamespaceURI()))
			{
				if(v==null)
					v=(Vector)inherited.clone();
				// DOM stores prefix being defined as localname
				String prefix=attr.getLocalName();
				// Check for overwrites
				// (Hash would be faster but less space-efficient...)
				for(int pos=v.size()-1;attr!=null && pos>=0;--pos)
					if(prefix.equals(
						 ((Node)v.elementAt(pos)).getLocalName() 
						 ))
					{
						v.setElementAt(attr,pos); // overwrite existing
						attr=null;
					}
				// If not overriding inherited, add as new
				if(attr!=null)
					v.addElement(attr);
			}
		}
		
		if(v==null) v=inherited;	// Same as parent
		n3.setUserData(KEY_NS,v,null);	// Bind for reuse

		return v;    	
    }

  /**
   * Figure out whether nodeHandle2 should be considered as being later
   * in the document than nodeHandle1, in Document Order as defined
   * by the DOM model. This may not agree with the ordering defined
   * by other XML applications; XPath in particular needs to do a
   * separate test for ordering of Namespace Nodes versus Attributes
   * within a single element.
   * <p>
   * This method is long and baroque in an effort to use the cheapest test
   * possible. Performance is still likely to be lousy. DOM Level 3 
   * is introducing a method which may be better optimized (in some DOMs).
   * %REVIEW% %OPT%
   * <p>
   * There are some cases where ordering isn't defined.
   * Our convention is that since we can't definitively say it
   * <strong>is</strong> ordered, we return <code>false</code>.
   *
   * @param nodeHandle1 Node handle to perform position comparison on.
   * @param nodeHandle2 Second Node handle to perform position comparison on .
   *
   * @return true if node1 comes before node2 (or is same node), 
   * otherwise return false.
   * You can think of this as
   * <code>(node1.documentOrderPosition &lt;= node2.documentOrderPosition)</code>.
   * 
   * Warning: Note the polarity of the DOM3 call!
   */
  public boolean isNodeOrder(Node n1, Node n2)
  {
	// Shortcuts
	if(n1==null || n2==null) 
		return ORDER_UNDEFINED;
	if(n1==n2) 
		return true;
		
	// Problem: Our synthetic namespace nodes for xml: can't
	// (of course) be tested by Xerces and require a few
	// special-case tests
	if(n1 instanceof DOM2DTMdefaultNamespaceDeclarationNode)
	{
		Node e1=((Attr)n1).getOwnerElement();
		if(n2 instanceof Attr)
		{
			Node e2=((Attr)n2).getOwnerElement();
			if(e1!=e2)
				return isNodeOrder(e1,e2); // order as per elements
			else if(NAMESPACE_URI_XML.equals(n2.getNamespaceURI()))
				return ORDER_UNDEFINED; // both namespaces, unordered
			else
				return true; // NS node comes before non-NS Attr
		}
		else
			return isNodeOrder(e1,n2); // order as per element			
	}
	else if(n2 instanceof DOM2DTMdefaultNamespaceDeclarationNode)
	{
		Node e2=((Attr)n1).getOwnerElement();
		if(n1 instanceof Attr) // We know it isn't a NS node
		{
			Node e1=((Attr)n2).getOwnerElement();
			if(e1!=e2)
				return isNodeOrder(e1,e2); // order as per elements
			else
				return false; // NS node comes before non-NS Attr
		}
		else
			return isNodeOrder(n1,e2); // order as per element			
	}

  	// As long as I'm using one DOM3 call, I might as well use another and
  	// avoid reinventing this wheel... among other things, the built-in
  	// version is likely to be more efficient.
	NodeImpl n1a=(NodeImpl)n1; 
	
	// Returns a bitmask. NOTE that this call reports the position of
	// the argument node relative to the node it's called on, 
	// NOT vice versa.
	short n2_to_n1=(true)
	  ? n1a.compareTreePosition(n2)
	  : compareTreePosition(n1,n2);	// EXPERIMENTAL: Local for tuning
	
	if(0!=(n2_to_n1 & n1a.TREE_POSITION_DISCONNECTED))
		return ORDER_UNDEFINED;
	else if(0!=(n2_to_n1 & n1a.TREE_POSITION_PRECEDING))
	{
		return false; // n1 follows n2
	}
	else if (0!=(n2_to_n1 & n1a.TREE_POSITION_EQUIVALENT))
	{
		// Attrs of same element. XSLT requires that we
		// refine this further: namespace nodes order 
		// before non-namespace nodes.  Otherwise they're unordered
		String ns1=n1.getNamespaceURI();
		String ns2=n2.getNamespaceURI();
		if(NAMESPACE_URI_XMLNS.equals(ns1)) // Theoretically should check prefix too
			if(NAMESPACE_URI_XMLNS.equals(ns2)) // Theoretically should check prefix too
				return ORDER_UNDEFINED;
			else
				return true; //n2 follows n1
		else
			if(NAMESPACE_URI_XMLNS.equals(ns2)) // Theoretically should check prefix too
				return false; // n1 follows n2
			else
				return ORDER_UNDEFINED;
	}
	else
		return true; // n2 follows n1
  }
  
  private static Vector _ancestry(Node n)
  {
  	Vector v=new Vector();
   	for(;
  		n!=null;
  		n=n.getParentNode())
  	{
  		v.addElement(n);
  	}
	return v;
  }
  
  
  
  // Swiped from Xerces for debugging
  /**
   * Compares a node with this node with regard to their position in the 
   * tree and according to the document order. This order can be extended 
   * by module that define additional types of nodes.
   * @param other The node to compare against this node.
   * @return Returns how the given node is positioned relatively to this 
   *   node.
   * @since DOM Level 3
   */
  public short compareTreePosition(Node reference,Node other) {
    // Questions of clarification for this method - to be answered by the
    // DOM WG.   Current assumptions listed - LM
    // 
    // 1. How do ENTITY nodes compare?  
    //    Current assumption: TREE_POSITION_DISCONNECTED, as ENTITY nodes 
    //    aren't really 'in the tree'
    //
    // 2. How do NOTATION nodes compare?
    //    Current assumption: TREE_POSITION_DISCONNECTED, as NOTATION nodes
    //    aren't really 'in the tree'
    //
    // 3. Are TREE_POSITION_ANCESTOR and TREE_POSITION_DESCENDANT     
    //    only relevant for nodes that are "part of the document tree"?   
    //     <outer>
    //         <inner  myattr="true"/>
    //     </outer>
    //    Is the element node "outer" considered an ancestor of "myattr"?
    //    Current assumption: No.                                     
    //
    // 4. How do children of ATTRIBUTE nodes compare (with eachother, or  
    //    with children of other attribute nodes with the same element)    
    //    Current assumption: Children of ATTRIBUTE nodes are treated as if 
    //    they they are the attribute node itself, unless the 2 nodes 
    //    are both children of the same attribute. 
    //
    // 5. How does an ENTITY_REFERENCE node compare with it's children? 
    //    Given the DOM, it should precede its children as an ancestor. 
    //    Given "document order",  does it represent the same position?     
    //    Current assumption: An ENTITY_REFERENCE node is an ancestor of its
    //    children.
    //
    // 6. How do children of a DocumentFragment compare?   
    //    Current assumption: If both nodes are part of the same document 
    //    fragment, there are compared as if they were part of a document. 

        
    // If the nodes are the same...
    if (reference==other) 
      return (NodeImpl.TREE_POSITION_SAME_NODE | NodeImpl.TREE_POSITION_EQUIVALENT);
        
    // If either node is of type ENTITY or NOTATION, compare as disconnected
    short thisType = reference.getNodeType();
    short otherType = other.getNodeType();

    // If either node is of type ENTITY or NOTATION, compare as disconnected
    if (thisType == Node.ENTITY_NODE || 
	thisType == Node.NOTATION_NODE ||
	otherType == Node.ENTITY_NODE ||
	otherType == Node.NOTATION_NODE ) {
      return NodeImpl.TREE_POSITION_DISCONNECTED; 
    }

    // Find the ancestor of each node, and the distance each node is from 
    // its ancestor.
    // During this traversal, look for ancestor/descendent relationships 
    // between the 2 nodes in question. 
    // We do this now, so that we get this info correct for attribute nodes 
    // and their children. 

    Node node; 
    Node thisAncestor = reference;
    Node otherAncestor = other;
    int thisDepth=0;
    int otherDepth=0;
    for (node=reference; node != null; node = node.getParentNode()) {
      thisDepth +=1;
      if (node == other) 
	// The other node is an ancestor of this one.
	return (NodeImpl.TREE_POSITION_ANCESTOR | NodeImpl.TREE_POSITION_PRECEDING);
      thisAncestor = node;
    }

    for (node=other; node!=null; node=node.getParentNode()) {
      otherDepth +=1;
      if (node == reference) 
	// The other node is a descendent of the reference node.
	return (NodeImpl.TREE_POSITION_DESCENDANT | NodeImpl.TREE_POSITION_FOLLOWING);
      otherAncestor = node;
    }
        
       
    Node thisNode = reference;
    Node otherNode = other;

    int thisAncestorType = thisAncestor.getNodeType();
    int otherAncestorType = otherAncestor.getNodeType();

    // if the ancestor is an attribute, get owning element. 
    // we are now interested in the owner to determine position.

    if (thisAncestorType == Node.ATTRIBUTE_NODE)  {
      thisNode = ((Attr)thisAncestor).getOwnerElement();
    }
    if (otherAncestorType == Node.ATTRIBUTE_NODE) {
      otherNode = ((Attr)otherAncestor).getOwnerElement();
    }

    // Before proceeding, we should check if both ancestor nodes turned
    // out to be attributes for the same element
    if (thisAncestorType == Node.ATTRIBUTE_NODE &&  
	otherAncestorType == Node.ATTRIBUTE_NODE &&  
	thisNode==otherNode)              
      return NodeImpl.TREE_POSITION_EQUIVALENT;

    // Now, find the ancestor of the owning element, if the original
    // ancestor was an attribute
 
    // Note:  the following 2 loops are quite close to the ones above.
    // May want to common them up.  LM.
    if (thisAncestorType == Node.ATTRIBUTE_NODE) {
      thisDepth=0;
      for (node=thisNode; node != null; node=node.getParentNode()) {
	thisDepth +=1;
	if (node == otherNode) 
	  // The other node is an ancestor of the owning element
	  return NodeImpl.TREE_POSITION_PRECEDING;
	thisAncestor = node;
      }
    }

    // Now, find the ancestor of the owning element, if the original
    // ancestor was an attribute
    if (otherAncestorType == Node.ATTRIBUTE_NODE) {
      otherDepth=0;
      for (node=otherNode; node != null; node=node.getParentNode()) {
	otherDepth +=1;
	if (node == thisNode) 
	  // The other node is a descendent of the reference 
	  // node's element
	  return NodeImpl.TREE_POSITION_FOLLOWING;
	otherAncestor = node;
      }
    }

    // thisAncestor and otherAncestor must be the same at this point,  
    // otherwise, we are not in the same tree or document fragment
    if (thisAncestor != otherAncestor) 
      return NodeImpl.TREE_POSITION_DISCONNECTED; 

    // Go up the parent chain of the deeper node, until we find a node
    // with the same depth as the shallower node
    if (thisDepth > otherDepth) {
      for (int i=0; i<thisDepth - otherDepth; i++)
	thisNode = thisNode.getParentNode();
      // Check if the node we have reached is in fact "otherNode". 
      // This can happen in the case of attributes.  In this case, 
      // otherNode "precedes" this.
      if (thisNode == otherNode)
	return NodeImpl.TREE_POSITION_PRECEDING;
    }
    else {
      for (int i=0; i<otherDepth - thisDepth; i++)
	otherNode = otherNode.getParentNode();
      // Check if the node we have reached is in fact "thisNode".  This can
      // happen in the case of attributes.  In this case, otherNode
      // "follows" this.
      if (otherNode == thisNode)
	return NodeImpl.TREE_POSITION_FOLLOWING;
    }
		
    // We now have nodes at the same depth in the tree.  Find a common 
    // ancestor.                                   
    Node thisNodeP, otherNodeP;
    for (thisNodeP=thisNode.getParentNode(),
	   otherNodeP=otherNode.getParentNode();
	 thisNodeP!=otherNodeP;) {
      thisNode = thisNodeP;
      otherNode = otherNodeP;
      thisNodeP = thisNodeP.getParentNode();
      otherNodeP = otherNodeP.getParentNode();
    }

    // See whether thisNode or otherNode is the leftmost
    for (Node current=thisNodeP.getFirstChild(); 
	 current!=null;
	 current=current.getNextSibling()) {
      if (current==otherNode) {
	return NodeImpl.TREE_POSITION_PRECEDING;
      }
      else if (current==thisNode) {
	return NodeImpl.TREE_POSITION_FOLLOWING;
      }
    }
    // REVISIT:  shouldn't get here.   Should probably throw an 
    // exception
    return 0;

  }
  
}
