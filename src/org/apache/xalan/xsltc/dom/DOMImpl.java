/*
 * @(#)$Id$
 *
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
 * originally based on software copyright (c) 2001, Sun
 * Microsystems., http://www.sun.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 * @author Morten Jorgensen
 * @author Douglas Sellers <douglasjsellers@hotmail.com>
 *
 */

package org.apache.xalan.xsltc.dom;

import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Stack;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Document;

import org.xml.sax.*;
import org.xml.sax.ext.*;
import org.xml.sax.helpers.AttributesImpl;
import org.apache.xalan.xsltc.*;
import org.apache.xalan.xsltc.util.IntegerArray;
import org.apache.xalan.xsltc.runtime.BasisLibrary;
import org.apache.xalan.xsltc.runtime.SAXAdapter;
import org.apache.xalan.xsltc.runtime.Hashtable;

public final class DOMImpl implements DOM, Externalizable {

    // empty String for null attribute values
    private final static String EMPTYSTRING = "";

    // empty iterator to be returned when there are no children
    private final static NodeIterator EMPTYITERATOR = new NodeIterator() {
	    public NodeIterator reset() { return this; }
	    public NodeIterator setStartNode(int node) { return this; }
	    public int next() { return NULL; }
	    public void setMark() {}
	    public void gotoMark() {}
	    public int getLast() { return 0; }
	    public int getPosition() { return 0; }
	    public NodeIterator cloneIterator() { return this; }
	    public boolean isReverse() { return false; }
	    public NodeIterator resetOnce() { return this; }
	    public NodeIterator includeSelf() { return this; }
	    public void setRestartable(boolean isRestartable) { }
	};

    // Contains the number of nodes and attribute nodes in the tree
    private int       _treeNodeLimit;
    private int       _firstAttributeNode;

    // Node-to-type, type-to-name, and name-to-type mappings
    private short[]   _type;
    private Hashtable _types = null;
    private String[]  _namesArray;

    // Tree navigation arrays
    private int[]     _parent;
    private int[]     _nextSibling;
    private int[]     _offsetOrChild; // Serves two purposes !!!
    private int[]     _lengthOrAttr;  // Serves two purposes !!!

    // Holds contents of text/comment nodes and attribute values
    private char[]    _text;

    // Namespace related stuff
    private String[]  _uriArray;
    private String[]  _prefixArray;
    private short[]   _namespace;
    private short[]   _prefix;
    private Hashtable _nsIndex = new Hashtable();

    // Tracks which textnodes are whitespaces and which are not
    private BitArray  _whitespace; // takes xml:space into acc.

    // Tracks which textnodes are not escaped
    private BitArray  _dontEscape = null; 

    // The URI to this document
    private String    _documentURI = null;
    static private int _documentURIIndex = 0;

    // Support for access/navigation through org.w3c.dom API
    private Node[] _nodes;
    private NodeList[] _nodeLists;
    private static NodeList EmptyNodeList;
    private static NamedNodeMap EmptyNamedNodeMap;

    private final static String XML_LANG_ATTRIBUTE =
	"http://www.w3.org/XML/1998/namespace:@lang";

    /**
     * Define the origin of the document from which the tree was built
     */
    public void setDocumentURI(String uri) {
	_documentURI = uri;
    }

    /**
     * Returns the origin of the document from which the tree was built
     */
    public String getDocumentURI() {
	return (_documentURI != null) ? _documentURI : "rtf" + _documentURIIndex++;
    }

    public String getDocumentURI(int node) {
	return getDocumentURI();
    }

    public void setupMapping(String[] names, String[] namespaces) {
	// This method only has a function in DOM adapters
    }

    /**
     * Lookup a namespace URI from a prefix starting at node. This method 
     * is used in the execution of xsl:element when the prefix is not known 
     * at compile time.
     */
    public String lookupNamespace(int node, String prefix) 
	throws TransletException 
    {
	int anode, nsnode;
	final AncestorIterator ancestors = new AncestorIterator();
	
	if (isElement(node)) {
	    ancestors.includeSelf();
	}

	ancestors.setStartNode(node);
	while ((anode = ancestors.next()) != NULL) {
	    final NodeIterator namespaces = 
		new NamespaceIterator().setStartNode(anode);

	    while ((nsnode = namespaces.next()) != NULL) {
		if (_prefixArray[_prefix[nsnode]].equals(prefix)) {
		    return getNodeValue(nsnode);
		}
	    }
	}

	// TODO: Internationalization?
	throw new TransletException("Namespace prefix '" + prefix + "' is undeclared.");
    }

    /**
     * Returns 'true' if a specific node is an element (of any type)
     */
    public boolean isElement(final int node) {
	final int type = _type[node];
	return ((node < _firstAttributeNode) && (type >= NTYPES));
    }

    /**
     * Returns 'true' if a specific node is an element (of any type)
     */
    public boolean isAttribute(final int node) {
	final int type = _type[node];
	return ((node >= _firstAttributeNode) && (type >= NTYPES));
    }

    /**
     * Returns the number of nodes in the tree (used for indexing)
     */
    public int getSize() {
	return(_type.length);
    }

    /**
     * Part of the DOM interface - no function here.
     */
    public void setFilter(StripFilter filter) { }


    /**
     * Returns true if node1 comes before node2 in document order
     */
    public boolean lessThan(int node1, int node2) {
	// Hack for ordering attribute nodes
	if (node1 >= _firstAttributeNode) node1 = _parent[node1];
	if (node2 >= _firstAttributeNode) node2 = _parent[node2];
	return (node2 < _treeNodeLimit && node1 < node2);
    }

    /**
     * Create an org.w3c.dom.Node from a node in the tree
     */
    public Node makeNode(int index) {
	if (_nodes == null) {
	    _nodes = new Node[_type.length];
	}
	return _nodes[index] != null ? _nodes[index]
				     : (_nodes[index] = new NodeImpl(index));
    }

    /**
     * Create an org.w3c.dom.Node from a node in an iterator
     * The iterator most be started before this method is called
     */
    public Node makeNode(NodeIterator iter) {
	return makeNode(iter.next());
    }

    /**
     * Create an org.w3c.dom.NodeList from a node in the tree
     */
    public NodeList makeNodeList(int index) {
	if (_nodeLists == null) {
	    _nodeLists = new NodeList[_type.length];
	}
	return _nodeLists[index] != null ? _nodeLists[index]
		     : (_nodeLists[index] = new NodeListImpl(index));
    }

    /**
     * Create an org.w3c.dom.NodeList from a node iterator
     * The iterator most be started before this method is called
     */
    public NodeList makeNodeList(NodeIterator iter) {
	return new NodeListImpl(iter);
    }

    /**
     * Create an empty org.w3c.dom.NodeList
     */
    private NodeList getEmptyNodeList() {
	return EmptyNodeList != null ? EmptyNodeList
	    : (EmptyNodeList = new NodeListImpl(new int[0]));
    }

    /**
     * Create an empty org.w3c.dom.NamedNodeMap
     */
    private NamedNodeMap getEmptyNamedNodeMap() {
	return EmptyNamedNodeMap != null ? EmptyNamedNodeMap
	    : (EmptyNamedNodeMap = new NamedNodeMapImpl(new int[0]));
    }

    /**
     * Exception thrown by methods in inner classes implementing
     * various org.w3c.dom interfaces (below)
     */
    private final class NotSupportedException extends DOMException {
	public NotSupportedException() {
	    super(NOT_SUPPORTED_ERR, "modification not supported");
	}
    }

    /**************************************************************
     * Implementation of org.w3c.dom.NodeList
     */
    private final class NodeListImpl implements NodeList {
	private final int[] _nodes;

	public NodeListImpl(int node) {
	    _nodes = new int[1];
	    _nodes[0] = node;
	}

	public NodeListImpl(int[] nodes) {
	    _nodes = nodes;
	}
                  
	public NodeListImpl(NodeIterator iter) {
	    final IntegerArray list = new IntegerArray();
	    int node;
	    while ((node = iter.next()) != NodeIterator.END) {
		list.add(node);
	    }         
	    _nodes = list.toIntArray();         
	}

	public int getLength() {
	    return _nodes.length;
	}
                  
	public Node item(int index) {
	    return makeNode(_nodes[index]);
	}
    }

                  
    /**************************************************************
     * Implementation of org.w3c.dom.NamedNodeMap
     */
    private final class NamedNodeMapImpl implements NamedNodeMap {

	private final int[] _nodes;
		
	public NamedNodeMapImpl(int[] nodes) {
	    _nodes = nodes;
	}
		
	public int getLength() {
	    return _nodes.length;
	}
		
	public Node getNamedItem(String name) {
	    for (int i = 0; i < _nodes.length; i++) {
		if (name.equals(getNodeName(_nodes[i]))) {
		    return makeNode(_nodes[i]);
		}
	    }
	    return null;
	}
		
	public Node item(int index) {
	    return makeNode(_nodes[index]);
	}
		
	public Node removeNamedItem(String name) {
	    throw new NotSupportedException();
	}
		
	public Node setNamedItem(Node node) {
	    throw new NotSupportedException();
	}

	public Node getNamedItemNS(String uri, String local) {
	    return(getNamedItem(uri+':'+local));
	}

	public Node setNamedItemNS(Node node) {
	    throw new NotSupportedException();
	}

	public Node removeNamedItemNS(String uri, String local) {
	    throw new NotSupportedException();
	}

    }


    /**************************************************************
     * Implementation of org.w3c.dom.Node
     */
    private final class NodeImpl implements Node {

	private final int _index;

	public NodeImpl(int index) {
	    _index = index;
	}

	public short getNodeType() {
	    switch (_type[_index]) {
	    case ROOT:
		return Node.DOCUMENT_NODE;
		
	    case TEXT:
		return Node.TEXT_NODE;
		
	    case PROCESSING_INSTRUCTION:
		return Node.PROCESSING_INSTRUCTION_NODE;
		
	    case COMMENT:
		return Node.COMMENT_NODE;
		
	    default:
		return _index < _firstAttributeNode
		    ? Node.ELEMENT_NODE : Node.ATTRIBUTE_NODE;
	    }
	}
		
	public Node getParentNode() {
	    final int parent = getParent(_index);
	    return parent > NULL ? makeNode(parent) : null;
	}
		
	public Node appendChild(Node node) throws DOMException {
	    throw new NotSupportedException();
	}
		
	public Node cloneNode(boolean deep) {
	    throw new NotSupportedException();
	}
		
	public NamedNodeMap getAttributes() {
	    if (getNodeType() == Node.ELEMENT_NODE) {
		int attribute = _lengthOrAttr[_index];
		// Skip attribute nodes
		while (_type[attribute] == NAMESPACE) {
		    attribute = _nextSibling[attribute];
		}
		if (attribute != NULL) {
		    final IntegerArray attributes = new IntegerArray(4);
		    do {
			attributes.add(attribute);
		    }
		    while ((attribute = _nextSibling[attribute]) != 0);
		    return new NamedNodeMapImpl(attributes.toIntArray());
		}
		else {
		    return getEmptyNamedNodeMap();
		}
	    }
	    else {
		return null;
	    }
	}

	public NodeList getChildNodes() {
	    if (hasChildNodes()) {
		final IntegerArray children = new IntegerArray(8);
		int child = _offsetOrChild[_index];
		do {
		    children.add(child);
		}
		while ((child = _nextSibling[child]) != 0);
		return new NodeListImpl(children.toIntArray());
	    }
	    else {
		return getEmptyNodeList();
	    }
	}
		
	public Node getFirstChild() {
	    return hasChildNodes()
		? makeNode(_offsetOrChild[_index])
		: null;
	}
		
	public Node getLastChild() {
	    return hasChildNodes()
		? makeNode(lastChild(_index))
		: null;
	}
		
	public Node getNextSibling() {
	    final int next = _nextSibling[_index];
	    return next != 0 ? makeNode(next) : null;
	}
		
	public String getNodeName() {
	    switch (_type[_index]) {
	    case ROOT:
		return "#document";
	    case TEXT:
		return "#text";
	    case PROCESSING_INSTRUCTION:
		return "#pi";
	    case COMMENT:
		return "#comment";
	    default:
		return DOMImpl.this.getNodeName(_index);
	    }
	}
		
	public String getNodeValue() throws DOMException {
	    return DOMImpl.this.getNodeValue(_index);
	}
		
	public Document getOwnerDocument() {
	    return null;
	}
		
	//??? how does it work with attributes
	public Node getPreviousSibling() {
	    int node = _parent[_index];
	    if (node > NULL) {
		int prev = -1;
		node = _offsetOrChild[node];
		while (node != _index) {
		    node = _nextSibling[prev = node];
		}
		if (prev != -1) {
		    return makeNode(prev);
		}
	    }
	    return null;
	}
		
	public boolean hasChildNodes() {
	    switch (getNodeType()) {
	    case Node.ELEMENT_NODE:
	    case Node.DOCUMENT_NODE:
		return _offsetOrChild[_index] != 0;

	    default:
		return false;
	    }
	}
		
	public Node insertBefore(Node n1, Node n2) throws DOMException {
	    throw new NotSupportedException();
	}
		
	public Node removeChild(Node n) throws DOMException {
	    throw new NotSupportedException();
	}
		
	public Node replaceChild(Node n1, Node n2) throws DOMException {
	    throw new NotSupportedException();
	}
		
	public void setNodeValue(String s) throws DOMException {
	    throw new NotSupportedException();
	}

	public void normalize() {
	    throw new NotSupportedException();
	}

	public boolean isSupported(String feature, String version) {
	    return false;
	}

	public String getNamespaceURI() {
	    return _uriArray[_namespace[_type[_index] - NTYPES]];
	}

	public String getPrefix() {
	    return _prefixArray[_prefix[_index]];
	}

	public void setPrefix(String prefix) {
	    throw new NotSupportedException();
	}

	public String getLocalName() {
	    return DOMImpl.this.getLocalName(_index);
	}

	public boolean hasAttributes() {
	    int attribute = _lengthOrAttr[_index];
	    while (_type[attribute] == NAMESPACE) {
		attribute = _nextSibling[attribute];
	    }
	    return (attribute != NULL);
	}

    }

    // A single copy (cache) of ElementFilter
    private Filter _elementFilter;

    /**
     * Returns a filter that lets only element nodes through
     */
    private Filter getElementFilter() {
	if (_elementFilter == null) {
	    _elementFilter = new Filter() {
		    public boolean test(int node) {
			return isElement(node);
		    }
		};
	}
	return _elementFilter;
    }

    /**
     * Implementation of a filter that only returns nodes of a
     * certain type (instanciate through getTypeFilter()).
     */
    private final class TypeFilter implements Filter {
	private final int _nodeType;
                  
	public TypeFilter(int type) {
	    _nodeType = type;
	}
                  
	public boolean test(int node) {
	    return _type[node] == _nodeType;
	}
    }

    /**
     * Returns a node type filter (implementation of Filter)
     */
    public Filter getTypeFilter(int type) {
	return new TypeFilter(type);
    }


    /**************************************************************
     * Iterator that returns all children of a given node
     */
    private final class ChildrenIterator extends NodeIteratorBase {
	// child to return next
	private int _currentChild;
	private int _last = -1;

	public NodeIterator setStartNode(int node) {
	    if (_isRestartable) {
		if (node >= _firstAttributeNode) node = NULL;
		if (node != _startNode) _last = -1;
		_startNode = node;
		if (_includeSelf) {
		    _currentChild = -1;
		}
		else {
		    if (hasChildren(node))
			_currentChild = _offsetOrChild[node];
		    else
			_currentChild = END;
		}
		return resetPosition();
	    }
	    return this;
	}

	public int next() {
	    int node = _currentChild;
	    if (_includeSelf) {
		if (node == -1) {
		    node = _startNode;
		    if (hasChildren(node))
			_currentChild = _offsetOrChild[node];
		    else
			_currentChild = END;
		    // IMPORTANT: The start node (parent of all children) is
		    // returned, but the node position counter (_position)
		    // should not be increased, so returnNode() is not called
		    return node;
		}
	    }
	    _currentChild = _nextSibling[node];
	    return returnNode(node);
	}

	public void setMark() {
	    _markedNode = _currentChild;
	}

	public void gotoMark() {
	    _currentChild = _markedNode;
	}

	public int getLast() {
	    if (_last == -1) {
		_last = 1;
		int node = _offsetOrChild[_startNode];
		while ((node = _nextSibling[node]) != END) _last++;
	    }
	    return(_last);
	}

    } // end of ChildrenIterator


    /**************************************************************
     * Iterator that returns the parent of a given node
     */
    private final class ParentIterator extends NodeIteratorBase {
	// candidate parent node
	private int _node;
	private int _nodeType = -1;
         
	public NodeIterator setStartNode(int node) {
	    if (_isRestartable) {
		_node = _parent[_startNode = node];
		return resetPosition();
	    }
	    return this;
	}
             
	public NodeIterator setNodeType(final int type) {
	    _nodeType = type;
	    return this;
	}

	public int next() {
	    int result = _node;
	    if ((_nodeType != -1) && (_type[_node] != _nodeType))
		result = END;
	    else
		result = _node;
	    _node = END;
	    return returnNode(result);
	}

	public void setMark() {
	    _markedNode = _node;
	}

	public void gotoMark() {
	    _node = _markedNode;
	}
    } // end of ParentIterator


    /**************************************************************
     * Iterator that returns children of a given type for a given node.
     * The functionality chould be achieved by putting a filter on top
     * of a basic child iterator, but a specialised iterator is used
     * for efficiency (both speed and size of translet).
     */
    private final class TypedChildrenIterator extends NodeIteratorBase {
	private int _nodeType;
	// node to consider next
	private int _currentChild;
         
	public TypedChildrenIterator(int nodeType) {
	    _nodeType = nodeType;
	}

	public NodeIterator setStartNode(int node) {
	    if (_isRestartable) {
		if (node >= _firstAttributeNode) node = NULL;
		_currentChild = hasChildren(node)
		    ? _offsetOrChild[_startNode = node] : END;
		return resetPosition();
	    }
	    return this;
	}

	public NodeIterator cloneIterator() {
	    try {
		final TypedChildrenIterator clone =
		    (TypedChildrenIterator)super.clone();
		clone._nodeType = _nodeType;
		clone.setRestartable(false);
		return clone.reset();
	    }
	    catch (CloneNotSupportedException e) {
		BasisLibrary.runTimeError(BasisLibrary.ITERATOR_CLONE_ERR,
					  e.toString());
		return null;
	    }
	}

	public NodeIterator reset() {
	    _currentChild = hasChildren(_startNode) ? 
			    _offsetOrChild[_startNode] : END;
	    return resetPosition();
	}

	public int next() {
	    final short[] type = _type;
	    final int nodeType = _nodeType;
	    final int[] nextSibling = _nextSibling;

	    for (int node = _currentChild; node != END; node = nextSibling[node]) {
		if (type[node] == nodeType) {
		    _currentChild = nextSibling[node];
		    return returnNode(node);
		}
	    }
	    return END;
	}

	public void setMark() {
	    _markedNode = _currentChild;
	}

	public void gotoMark() {
	    _currentChild = _markedNode;
	}
    } // end of TypedChildrenIterator


    /**************************************************************
     * Iterator that returns children within a given namespace for a
     * given node. The functionality chould be achieved by putting a
     * filter on top of a basic child iterator, but a specialised
     * iterator is used for efficiency (both speed and size of translet).
     */
    private final class NamespaceChildrenIterator extends NodeIteratorBase {
	private final int _nsType;
	private int _currentChild;
         
	public NamespaceChildrenIterator(final int type) {
	    _nsType = type;
	}

	public NodeIterator setStartNode(int node) {
	    if (_isRestartable) {
		if (node >= _firstAttributeNode) node = NULL;
		_currentChild = hasChildren(node)
		    ? _offsetOrChild[_startNode = node] : END;
		return resetPosition();
	    }
	    return this;
	}

	public int next() {
	    for (int node = _currentChild; node != END; 
		 node = _nextSibling[node]) {
		if (getNamespaceType(node) == _nsType) {
		    _currentChild = _nextSibling[node];
		    return returnNode(node);
		}
	    }
	    return END;
	}

	public void setMark() {
	    _markedNode = _currentChild;
	}

	public void gotoMark() {
	    _currentChild = _markedNode;
	}

    } // end of TypedChildrenIterator


    /**************************************************************
     * Iterator that returns attributes within a given namespace for a node.
     */
    private final class NamespaceAttributeIterator extends NodeIteratorBase {

	private final int _nsType;
	private int _attribute;
         
	public NamespaceAttributeIterator(int nsType) {
	    super();
	    _nsType = nsType;
	}
                  
	public NodeIterator setStartNode(int node) {
	    if (_isRestartable) {
		for (node = _lengthOrAttr[_startNode = node];
		     node != NULL && getNamespaceType(node) != _nsType;
		     node = _nextSibling[node]);
		_attribute = node;
		return resetPosition();
	    }
	    return this;
	}
                  
	public int next() {
	    final int save = _attribute;
	    int node = save;
	    do {
		_attribute = _nextSibling[_attribute];
	    } while(_type[_attribute] == NAMESPACE);
	    
	    for (node = _lengthOrAttr[_startNode = node];
		 node != NULL && getNamespaceType(node) != _nsType;
		 node = _nextSibling[node]);
	    _attribute = node;

	    return returnNode(save);
	}

	public void setMark() {
	    _markedNode = _attribute;
	}

	public void gotoMark() {
	    _attribute = _markedNode;
	}
         
    } // end of TypedChildrenIterator


    /**************************************************************
     * Iterator that returns all siblings of a given node.
     */
    private class FollowingSiblingIterator extends NodeIteratorBase {
	private int _node;
         
	public NodeIterator setStartNode(int node) {
	    if (_isRestartable) {
		if (node >= _firstAttributeNode) node = NULL;
		_node = _startNode = node;
		return resetPosition();
	    }
	    return this;
	}
                  
	public int next() {
	    return returnNode(_node = _nextSibling[_node]);
	}

	public void setMark() {
	    _markedNode = _node;
	}

	public void gotoMark() {
	    _node = _markedNode;
	}
    } // end of FollowingSiblingIterator


    /**************************************************************
     * Iterator that returns all following siblings of a given node.
     */
    private final class TypedFollowingSiblingIterator
	extends FollowingSiblingIterator {
	private final int _nodeType;

	public TypedFollowingSiblingIterator(int type) {
	    _nodeType = type;
	}
         
	public int next() {
	    int node;
	    while ((node = super.next()) != NULL) {
		if (_type[node] == _nodeType) return(node);
		_position--;
	    }
	    return END;
	}

    } // end of TypedFollowingSiblingIterator


    /**************************************************************
     * Iterator that returns attribute nodes (of what nodes?)
     */
    private final class AttributeIterator extends NodeIteratorBase {
	private int _attribute;
         
	// assumes caller will pass element nodes
	public NodeIterator setStartNode(int node) {
	    if (_isRestartable) {
		if (isElement(node)) {
		    _attribute = _lengthOrAttr[_startNode = node];
		    // Skip namespace nodes
		    while (_type[_attribute] == NAMESPACE) {
			_attribute = _nextSibling[_attribute];
		    }
		}
		else {
		    _attribute = NULL;
		}
		return resetPosition();
	    }
	    return this;
	}
                  
	public int next() {
	    final int node = _attribute;
	    _attribute = _nextSibling[_attribute];
	    return returnNode(node);
	}

	public void setMark() {
	    _markedNode = _attribute;
	}

	public void gotoMark() {
	    _attribute = _markedNode;
	}
    } // end of AttributeIterator


    /**************************************************************
     * Iterator that returns attribute nodes of a given type
     */
    private final class TypedAttributeIterator extends NodeIteratorBase {
	private final int _nodeType;
	private int _attribute;
         
	public TypedAttributeIterator(int nodeType) {
	    _nodeType = nodeType;
	}
                  
	public NodeIterator setStartNode(int node) {
	    if (_isRestartable) {
		// If not an element node, then set iterator at END
		if (!isElement(node)) {
		    _attribute = END;
		    return resetPosition();
		}

		for (node = _lengthOrAttr[_startNode = node];
		     node != NULL && _type[node] != _nodeType;
		     node = _nextSibling[node]);
		_attribute = node;
		return resetPosition();
	    }
	    return this;
	}

	public NodeIterator reset() {
	    int node = _startNode;
	    for (node = _lengthOrAttr[node];
		 node != NULL && _type[node] != _nodeType;
		 node = _nextSibling[node]);
	    _attribute = node;
	    return resetPosition();
	}
                  
	public int next() {
	    final int node = _attribute;
	    _attribute = NULL;         // singleton iterator
	    return returnNode(node);
	}

	public void setMark() {
	    _markedNode = _attribute;
	}

	public void gotoMark() {
	    _attribute = _markedNode;
	}
    } // end of TypedAttributeIterator


    /**************************************************************
     * Iterator that returns namespace nodes
     */
    private class NamespaceIterator extends NodeIteratorBase {
	
	protected int _node;
	protected int _ns;
         
	// assumes caller will pass element nodes
	public NodeIterator setStartNode(int node) {
	    if (_isRestartable) {
		if (isElement(node)) {
		    _startNode = _node = node;
		    _ns = _lengthOrAttr[_node];
		    while ((_ns != DOM.NULL) && (_type[_ns] != NAMESPACE)) {
			_ns = _nextSibling[_ns];
		    }
		}
		else {
		    _ns = DOM.NULL;
		}
		return resetPosition();
	    }
	    return this;
	}
                  
	public int next() {
	    while (_node != NULL) {
		final int node = _ns;
		_ns = _nextSibling[_ns];

		while ((_ns == DOM.NULL) && (_node != DOM.NULL)) {
		    _node = _parent[_node];
		    _ns = _lengthOrAttr[_node];

		    while ((_ns != DOM.NULL) && (_type[_ns] != NAMESPACE)) {
			_ns = _nextSibling[_ns];
		    }
		}
		if (_type[node] == NAMESPACE)
		    return returnNode(node);
	    }
	    return NULL;
	}

	public void setMark() {
	    _markedNode = _ns;
	}

	public void gotoMark() {
	    _ns = _markedNode;
	}
	
    } // end of NamespaceIterator


    /**************************************************************
     * Iterator that returns namespace nodes
     */
    private final class TypedNamespaceIterator extends NamespaceIterator {

	final int _uriType;

	public TypedNamespaceIterator(int type) {
	    _uriType = type;
	}

	public int next() {
	    int node;

	    while ((node = _ns) != DOM.NULL) {
		_ns = _nextSibling[_ns];
		while ((_ns == DOM.NULL) && (_node != DOM.NULL)) {
		    _node = _parent[_node];
		    _ns = _lengthOrAttr[_node];
		    while ((_ns != DOM.NULL) && (_type[_ns] != NAMESPACE)) {
			_ns = _nextSibling[_ns];
		    }
		}
		if (_prefix[node] == _uriType) return returnNode(node);
	    }
	    return DOM.NULL;
	}
         
    } // end of AttributeIterator


    /**************************************************************
     * Iterator that returns preceding siblings of a given node
     */
    private class PrecedingSiblingIterator extends NodeIteratorBase {

	private int _node;
	private int _mom;
         
	public boolean isReverse() {
	    return true;
	}
         
	public NodeIterator setStartNode(int node) {
	    if (_isRestartable) {
		if (node >= _firstAttributeNode) node = NULL;
		int tmp = NULL;
		_startNode = node;
		_mom = _parent[node];
		_node = _offsetOrChild[_mom];
		while ((_node != node) && (_node != NULL)) {
		    tmp = _node;
		    _node = _nextSibling[_node];
		}
		_node = tmp;
		return resetPosition();
	    }
	    return this;
	}

	public int next() {
	    // Return NULL if end already reached
	    if (_node == NULL) return NULL;

	    int current = _offsetOrChild[_mom];

	    // Otherwise find the next preceeding sibling
	    int last = NULL;
	    while ((current != _node) && (current != NULL)) {
		last = current;
		current = _nextSibling[current];
	    }
	    current = _node;
	    _node = last;
	    return returnNode(current);
	}

	public void setMark() {
	    _markedNode = _node;
	}

	public void gotoMark() {
	    _node = _markedNode;
	}

    } // end of PrecedingSiblingIterator


    /**************************************************************
     * Iterator that returns preceding siblings of a given type for
     * a given node
     */
    private final class TypedPrecedingSiblingIterator
	extends PrecedingSiblingIterator {
	private final int _nodeType;

	public TypedPrecedingSiblingIterator(int type) {
	    _nodeType = type;
	}
         
	public int next() {
	    int node;
	    while ((node = super.next()) != NULL && _type[node] != _nodeType)
		_position--;
	    return(node);
	}

    } // end of PrecedingSiblingIterator


    /**************************************************************
     * Iterator that returns preceding nodes of a given node.
     * This includes the node set {root+1, start-1}, but excludes
     * all ancestors.
     */
    private class PrecedingIterator extends NodeIteratorBase {

	private int _node = 0;
	private int _mom = 0;

	public boolean isReverse() {
	    return true;
	}
         
	public NodeIterator cloneIterator() {
	    try {
		final PrecedingIterator clone = 
		    (PrecedingIterator)super.clone();
		clone.setRestartable(false);
		return clone.reset();
	    }
	    catch (CloneNotSupportedException e) {
		BasisLibrary.runTimeError(BasisLibrary.ITERATOR_CLONE_ERR,
					  e.toString());
		return null;
	    }
	}
         
	public NodeIterator setStartNode(int node) {
	    if (_isRestartable) {
		if (node >= _firstAttributeNode) node = _parent[node];
		_node = _startNode = node;
		_mom  = _parent[_startNode];
		return resetPosition();
	    }
	    return this;
	}
                  
	public int next() {
	    while (--_node > ROOTNODE) {
		if (_node < _mom) _mom = _parent[_mom];
		if (_node != _mom) return returnNode(_node);
	    }
	    return(NULL);
	}

	// redefine NodeIteratorBase's reset
	public NodeIterator reset() {
	    _node = _startNode;
	    _mom  = _parent[_startNode];
	    return resetPosition();
	}

	public void setMark() {
	    _markedNode = _node;
	}

	public void gotoMark() {
	    _node = _markedNode;
	}

    } // end of PrecedingIterator


    /**************************************************************
     * Iterator that returns preceding nodes of agiven type for a
     * given node. This includes the node set {root+1, start-1}, but
     * excludes all ancestors.
     */
    private final class TypedPrecedingIterator extends PrecedingIterator {
	private final int _nodeType;

	public TypedPrecedingIterator(int type) {
	    _nodeType = type;
	}
         
	public int next() {
	    int node;
	    while ((node = super.next()) != NULL && _type[node] != _nodeType)
		_position--; 
	    return node;
	}

    } // end of TypedPrecedingIterator


    /**************************************************************
     * Iterator that returns following nodes of for a given node.
     */
    private class FollowingIterator extends NodeIteratorBase {
	//  _node precedes search for next
	protected int _node;
                  
	public NodeIterator setStartNode(int node) {
	    int skip = 0;
	    if (_isRestartable) {
		if (node >= _firstAttributeNode) {
		    skip = 1;
		    node = _parent[node];
		    int child = _offsetOrChild[node];
		    if (child != NULL) node = child;
		}
		_startNode = node;

		// find rightmost descendant (or self)
		int current;
		while ((node = lastChild(current = node)) != NULL) { }

		_node = current - skip;
		// _node precedes possible following(node) nodes
		return resetPosition();
	    }
	    return this;
	}
                      
	public int next() {
	    final int node = _node + 1;
	    return node < _firstAttributeNode ? returnNode(_node = node) : NULL;
	}

	public void setMark() {
	    _markedNode = _node;
	}

	public void gotoMark() {
	    _node = _markedNode;
	}
    } // end of FollowingIterator


    /**************************************************************
     * Iterator that returns following nodes of a given type for a given node.
     */
    private final class TypedFollowingIterator extends FollowingIterator {
	private final int _nodeType;

	public TypedFollowingIterator(int type) {
	    _nodeType = type;
	}
                  
	public int next() {
	    int node;
	    while ((node = super.next()) != NULL) {
		if (_type[node] == _nodeType) return(node);
		_position--;
	    }
	    return END;
	}
    } // end of TypedFollowingIterator


    /**************************************************************
     * Iterator that returns the ancestors of a given node.
     * The nodes are returned in reverse document order, so you
     * get the context node (or its parent node) first, and the
     * root node in the very, very end.
     */         
    private class AncestorIterator extends NodeIteratorBase {

	protected int _index;
	protected int _last = -1;

	public final boolean isReverse() {
	    return true;
	}

	public int getLast() {
	    if (_last > -1) return _last;
	    int count = 1;
	    int node = _startNode;
	    while ((node = _parent[node]) != ROOT) count++;
	    _last = count;
	    return(count);
	}
         
	public NodeIterator cloneIterator() {
	    try {
		final AncestorIterator clone = (AncestorIterator)super.clone();
		clone.setRestartable(false); // must set to false for any clone
		clone._startNode = _startNode;
		return clone.reset();
	    }
	    catch (CloneNotSupportedException e) {
		BasisLibrary.runTimeError(BasisLibrary.ITERATOR_CLONE_ERR,
					  e.toString());
		return null;
	    }
	}
                  
	public NodeIterator setStartNode(int node) {
	    if (_isRestartable) {
		_last = -1;
		if (_includeSelf) {
		    _startNode = node;
		}
		else if (node >= _firstAttributeNode) {
		    _startNode = node = _parent[node];
		}
		else {
		    _startNode = _parent[node];
		}
		_index = _startNode;
		return resetPosition();
	    }
	    return this;
	}

	public NodeIterator reset() {
	    _index = _startNode;
	    return resetPosition();
	}
                  
	public int next() {
	    if (_index >= 0) {
		final int node = _index;
		_index = (_index == 0) ? -1 : _parent[_index];
		return returnNode(node);
	    }
	    return(NULL);
	}

	public void setMark() {
	    _markedNode = _index;
	}

	public void gotoMark() {
	    _index = _markedNode;
	}
    } // end of AncestorIterator


    /**************************************************************
     * Typed iterator that returns the ancestors of a given node.
     */             
    private final class TypedAncestorIterator extends AncestorIterator {

	private final int _nodeType;
                  
	public TypedAncestorIterator(int type) {
	    _nodeType = type;
	}

	public int next() {
	    int node;
	    while ((node = super.next()) != NULL) {
		if (_type[node] == _nodeType) return(node);
		_position--;
	    }
	    return(NULL);
	}

	public int getLast() {
	    if (_last > -1) return _last;
	    int count = 1;
	    int node = _startNode;
	    do {
		if (_type[node] == _nodeType) count++;
	    } while ((node = _parent[node]) != ROOT);
	    _last = count;
	    return(count);
	}

    } // end of TypedAncestorIterator


    /**************************************************************
     * Iterator that returns the descendants of a given node.
     */             
    private class DescendantIterator extends NodeIteratorBase {
	//  _node precedes search for next
	protected int _node;
	// first node outside descendant range
	protected int _limit;

	public NodeIterator setStartNode(int node) {
	    _startNode = node;
	    if (_isRestartable) {
		_node = _startNode = _includeSelf ? node - 1 : node;
		// no descendents if no children
		if (hasChildren(node) == false) {
		    // set _limit to match next()'s criteria for end
		    _limit = node + 1;
		}
		// find leftmost descendant of next sibling
		else if ((node = _nextSibling[node]) == 0) {
		    // no next sibling, array end is the limit
		    _limit = _treeNodeLimit;
		}
		else {
		    _limit = leftmostDescendant(node);
		}
		return resetPosition();
	    }
	    return this;
	}

	public int next() {
	    while (++_node < _limit) {
		if (_type[_node] > TEXT) {
		    return(returnNode(_node));
		}
	    } 
	    return(NULL);
	}

	public void setMark() {
	    _markedNode = _node;
	}

	public void gotoMark() {
	    _node = _markedNode;
	}

    } // end of DescendantIterator


    /**************************************************************
     * Typed iterator that returns the descendants of a given node.
     */             
    private final class TypedDescendantIterator extends DescendantIterator {
	private final int _nodeType;
                  
	public TypedDescendantIterator(int nodeType) {
	    _nodeType = nodeType;
	}
                  
	public int next() {
	    final int limit = _limit;
	    final int type = _nodeType;
	    int node = _node + 1; // start search w/ next
	    // while condition == which nodes to skip
	    // iteration stops when at end or node w/ desired type
	    while (node < limit && _type[node] != type) {
		++node;
	    }
	    return node < limit ? returnNode(_node = node) : NULL;
	}

    } // end of TypedDescendantIterator


    /**************************************************************
     * Iterator that returns the descendants of a given node.
     */             
    private class NthDescendantIterator extends DescendantIterator {

	final NodeIterator _source;
	final int _pos;
	final int _ourtype;

	public NthDescendantIterator(NodeIterator source, int pos, int type) {
	    _source = source;
	    _ourtype = type;
	    _pos = pos;
	}

	public void setRestartable(boolean isRestartable) {
	    _isRestartable = isRestartable;
	    _source.setRestartable(isRestartable);
	}

	// The start node of this iterator is always the root!!!
	public NodeIterator setStartNode(int node) {
	    _source.setStartNode(node);
	    return this;
	}

	public int next() {
	    int node;
	    while ((node = _source.next()) != END) {
		int parent = _parent[node];
		int child = _offsetOrChild[parent];
		int pos = 0;

		if (_ourtype != -1) {
		    do {
			if (isElement(child) && _type[child] == _ourtype) pos++;
		    } while ((pos<_pos) && (child = _nextSibling[child]) != 0);
		}
		else {
		    do {
			if (isElement(child)) pos++;
		    } while ((pos<_pos) && (child = _nextSibling[child]) != 0);
		}

		if (node == child) return node; 
	    }
	    return(END);
	}

	public NodeIterator reset() {
	    _source.reset();
	    return this;
	}


    } // end of NthDescendantIterator


    /**************************************************************
     * Iterator that returns a given node only if it is of a given type.
     */             
    private final class TypedSingletonIterator extends SingletonIterator {
	private final int _nodeType;

	public TypedSingletonIterator(int nodeType) {
	    _nodeType = nodeType;
	}
         
	public int next() {
	    final int result = super.next();
	    return _type[result] == _nodeType ? result : NULL;
	}
    } // end of TypedSingletonIterator


    /**************************************************************
     * Iterator to put on top of other iterators. It will take the
     * nodes from the underlaying iterator and return all but
     * whitespace text nodes. The iterator needs to be a supplied
     * with a filter that tells it what nodes are WS text.
     */             
    private final class StrippingIterator extends NodeIteratorBase {

	private static final int USE_PREDICATE  = 0;
	private static final int STRIP_SPACE    = 1;
	private static final int PRESERVE_SPACE = 2;

	private StripFilter _filter = null;
	private short[] _mapping = null;
	private final NodeIterator _source;
	private boolean _children = false;
	private int _action = USE_PREDICATE;
	private int _last = -1;

	public StrippingIterator(NodeIterator source,
				 short[] mapping,
				 StripFilter filter) {

	    _filter = filter;
	    _mapping = mapping;
	    _source = source;

	    if (_source instanceof ChildrenIterator ||
		_source instanceof TypedChildrenIterator) {
		_children = true;
	    }
	}

	public void setRestartable(boolean isRestartable) {
	    _isRestartable = isRestartable;
	    _source.setRestartable(isRestartable);
	}

	public NodeIterator setStartNode(int node) {
	    if (_children) {
		if (_filter.stripSpace((DOM)DOMImpl.this, node,
				       _mapping[_type[node]]))
		    _action = STRIP_SPACE;
		else
		    _action = PRESERVE_SPACE;
	    }

	    _source.setStartNode(node);
	    //return resetPosition();
	    return(this);
	}
    
	public int next() {
	    int node;
	    while ((node = _source.next()) != END) {
		switch(_action) {
		case STRIP_SPACE:
		    if (_whitespace.getBit(node)) continue;
		    // fall through...
		case PRESERVE_SPACE:
		    return returnNode(node);
		case USE_PREDICATE:
		default:
		    if (_whitespace.getBit(node) &&
			_filter.stripSpace((DOM)DOMImpl.this, node,
					   _mapping[_type[_parent[node]]]))
			continue;
		    return returnNode(node);
		}
	    }
	    return END;
	}

	public NodeIterator reset() {
	    _source.reset();
	    return this;
	}

	public void setMark() {
	    _source.setMark();
	}

	public void gotoMark() {
	    _source.gotoMark();
	}

	public int getLast() {
	    // Return chached value (if we have it)
	    if (_last != -1) return _last;

	    int count = getPosition();
	    int node;

	    _source.setMark();
	    while ((node = _source.next()) != END) {
		switch(_action) {
		case STRIP_SPACE:
		    if (_whitespace.getBit(node))
			continue;
		    // fall through...
		case PRESERVE_SPACE:
		    count++;
		    break;
		case USE_PREDICATE:
		default:
		    if (_whitespace.getBit(node) &&
			_filter.stripSpace((DOM)DOMImpl.this, node,
					   _mapping[_type[_parent[node]]]))
			continue;
		    else
			count++;
		}
	    }
	    _source.gotoMark();
	    _last = count;
	    return(count);
	}

    } // end of StrippingIterator

    public NodeIterator strippingIterator(NodeIterator iterator,
					  short[] mapping,
					  StripFilter filter) {
	return(new StrippingIterator(iterator, mapping, filter));
    }

    /**************************************************************
     * This is a specialised iterator for predicates comparing node or
     * attribute values to variable or parameter values.
     */
    private final class NodeValueIterator extends NodeIteratorBase {

	private NodeIterator _source;
	private String _value;
	private boolean _op;
	private final boolean _isReverse;
	private int _returnType = RETURN_PARENT;
	private int _pos;

	public NodeValueIterator(NodeIterator source, int returnType,
				 String value, boolean op) {
	    _source = source;
	    _returnType = returnType;
	    _value = value;
	    _op = op;
	    _isReverse = source.isReverse();
	}

	public boolean isReverse() {
	    return _isReverse;
	}
    
	public void setRestartable(boolean isRestartable) {
	    _isRestartable = isRestartable;
	    _source.setRestartable(isRestartable);
	}

	public NodeIterator cloneIterator() {
	    try {
		NodeValueIterator clone = (NodeValueIterator)super.clone();
		clone._source = _source.cloneIterator();
		clone.setRestartable(false);
		return clone.reset();
	    }
	    catch (CloneNotSupportedException e) {
		BasisLibrary.runTimeError(BasisLibrary.ITERATOR_CLONE_ERR,
					  e.toString());
		return null;
	    }
	}
    
	public NodeIterator reset() {
	    _source.reset();
	    return resetPosition();
	}

	public int next() {

	    int node;
	    while ((node = _source.next()) != END) {
		String val = getNodeValue(node);
		if (_value.equals(val) == _op) {
		    if (_returnType == RETURN_CURRENT)
			return returnNode(node);
		    else
			return returnNode(_parent[node]);
		}
	    }
	    return END;
	}

	public NodeIterator setStartNode(int node) {
	    if (_isRestartable) {
		_source.setStartNode(_startNode = node); 
		return resetPosition();
	    }
	    return this;
	}

	public void setMark() {
	    _source.setMark();
	    _pos = _position;
	}

	public void gotoMark() {
	    _source.gotoMark();
	    _position = _pos;
	}
    }                       

    public NodeIterator getNodeValueIterator(NodeIterator iterator, int type,
					     String value, boolean op) {
	return(new NodeValueIterator(iterator, type, value, op));
    }

    /**
     * Encapsulates an iterator in an OrderedIterator to ensure node order
     */
    public NodeIterator orderNodes(NodeIterator source, int node) {
	return new DupFilterIterator(source);
    }

    /**
     * Returns the leftmost descendant of a node (bottom left in sub-tree)
     */
    private int leftmostDescendant(int node) {
	int current;
	while (_type[current = node] >= NTYPES 
	       && (node = _offsetOrChild[node]) != NULL) {
	}
	return current;
    }

    /**
     * Returns index of last child or 0 if no children
     */
    private int lastChild(int node) {
	if (isElement(node) || node == ROOTNODE) {
	    int child;
	    if ((child = _offsetOrChild[node]) != NULL) {
		while ((child = _nextSibling[node = child]) != NULL) {
		}
		return node;
	    }
	}
	return 0;
    }

    /**
     * Returns the parent of a node
     */
    public int getParent(final int node) {
	return _parent[node];
    }

    public int getElementPosition(int node) {
	// Initialize with the first sbiling of the current node
	int match = 0;
	int curr  = _offsetOrChild[_parent[node]];
	if (isElement(curr)) match++;

	// Then traverse all other siblings up until the current node
	while (curr != node) {
	    curr = _nextSibling[curr];
	    if (isElement(curr)) match++;
	}

	// And finally return number of matches
	return match;         
    }

    public int getAttributePosition(int attr) {
	// Initialize with the first sbiling of the current node
	int match = 1;
	int curr  = _lengthOrAttr[_parent[attr]];

	// Then traverse all other siblings up until the current node
	while (curr != attr) {
	    curr = _nextSibling[curr];
	    match++;
	}

	// And finally return number of matches
	return match;         
    }

    /**
     * Returns a node's position amongst other nodes of the same type
     */
    public int getTypedPosition(int type, int node) {
	// Just return the basic position if no type is specified
	switch(type) {
	case ELEMENT:
	    return getElementPosition(node);
	case ATTRIBUTE:
	    return getAttributePosition(node);
	case -1:
	    type = _type[node];
	}

	// Initialize with the first sbiling of the current node
	int match = 0;
	int curr  = _offsetOrChild[_parent[node]];
	if (_type[curr] == type) match++;

	// Then traverse all other siblings up until the current node
	while (curr != node) {
	    curr = _nextSibling[curr];
	    if (_type[curr] == type) match++;
	}

	// And finally return number of matches
	return match;         
    }

    /**
     * Returns an iterator's last node of a given type
     */
    public int getTypedLast(int type, int node) {
	// Just return the basic position if no type is specified
	if (type == -1) type = _type[node];

	// Initialize with the first sbiling of the current node
	int match = 0;
	int curr  = _offsetOrChild[_parent[node]];
	if (_type[curr] == type) match++;

	// Then traverse all other siblings up until the very last one
	while (curr != NULL) {
	    curr = _nextSibling[curr];
	    if (_type[curr] == type) match++;
	}

	return match;         
    }

    /**
     * Returns singleton iterator containg the document root
     * Works for them main document (mark == 0)
     */
    public NodeIterator getIterator() {
	return new SingletonIterator(ROOTNODE);
    }

    /**
     * Returns the type of a specific node
     */
    public int getType(final int node) {
	return (node >= _type.length) ? 0 : _type[node];
    }
    
    /**
     * Returns the namespace type of a specific node
     */
    public int getNamespaceType(final int node) {
	final int type = _type[node];
	return (type >= NTYPES) ? _namespace[type-NTYPES] 
	    : 0; 	// default namespace
    }

    /**
     * Returns the node-to-type mapping array
     */
    public short[] getTypeArray() {
	return _type;
    }

    /**
     * Returns the (String) value of any node in the tree
     */
    public String getNodeValue(final int node) {
	// NS prefix = _prefixArray[_prefix[node]]
	if ((node == NULL) || (node > _treeNodeLimit)) return EMPTYSTRING;
	switch(_type[node]) {
	case ROOT:
	    return getNodeValue(_offsetOrChild[node]);
	case TEXT:
	    // GTM - add escapign code here too.
	case COMMENT:
	    return makeStringValue(node);
	case PROCESSING_INSTRUCTION:
	    final String pistr = makeStringValue(node);
	    final int col = pistr.indexOf(' ');
	    return (col > 0) ?  pistr.substring(col+1) : pistr;
	default:
	    return (node < _firstAttributeNode) ? getElementValue(node) :
						  makeStringValue(node);
	}
    }

    private String getLocalName(int node) {
	final int type = _type[node] - NTYPES;
	final String qname = _namesArray[type];
	final String uri = _uriArray[_namespace[type]];

	if (uri != null) {
	    final int len = uri.length();
	    if (len > 0) return qname.substring(len+1);
	}
	return qname;
    }

    /**
     * Sets up a translet-to-dom type mapping table
     */
    private Hashtable setupMapping(String[] namesArray) {
	final int nNames = namesArray.length;
	final Hashtable types = new Hashtable(nNames);
	for (int i = 0; i < nNames; i++) {
	    types.put(namesArray[i], new Integer(i + NTYPES));
	}
	return types;
    }

    /**
     * Returns the internal type associated with an expaneded QName
     */
    public int getGeneralizedType(final String name) {
	final Integer type = (Integer)_types.get(name);
	if (type == null) {
	    // memorize default type
	    final int code = name.charAt(0) == '@' ? ATTRIBUTE : ELEMENT;
	    _types.put(name, new Integer(code));
	    return code;
	}
	else {
	    return type.intValue();
	}
    }

    /**
     * Get mapping from DOM element/attribute types to external types
     */
    public short[] getMapping(String[] names) {
	int i;
	final int namesLength = names.length;
	final int mappingLength = _namesArray.length + NTYPES;
	final short[] result = new short[mappingLength];

	// primitive types map to themselves
	for (i = 0; i < NTYPES; i++)
	    result[i] = (short)i;

	// extended types initialized to "beyond caller's types"
	// unknown element or Attr
	for (i = NTYPES; i < mappingLength; i++) {
	    final int type = i - NTYPES;
	    final String name = _namesArray[type];
	    final String uri = _uriArray[_namespace[type]];
	    int len = 0;
	    if (uri != null) {
		len = uri.length();
		if (len > 0) len++;
	    }
	    result[i] = (short) ((name.length() > 0 && name.charAt(len) == '@') ?
		ATTRIBUTE : ELEMENT);
	}

	// actual mapping of caller requested names
	for (i = 0; i < namesLength; i++) {
	    result[getGeneralizedType(names[i])] = (short)(i + NTYPES);
	}

	return result;
    }

    /**
     * Get mapping from external element/attribute types to DOM types
     */
    public short[] getReverseMapping(String[] names) {
	int i;
	final short[] result = new short[names.length + NTYPES];
	// primitive types map to themselves
	for (i = 0; i < NTYPES; i++) {
	    result[i] = (short)i;
	}
	// caller's types map into appropriate dom types
	for (i = 0; i < names.length; i++) {
	    result[i + NTYPES] = (short)getGeneralizedType(names[i]);
	    if (result[i + NTYPES] == ELEMENT)
		result[i + NTYPES] = NO_TYPE;
	}
	return result;
    }

    /**
     * Get mapping from DOM namespace types to external namespace types
     */
    public short[] getNamespaceMapping(String[] namespaces) {
	int i;
	final int nsLength = namespaces.length;
	final int mappingLength = _uriArray.length;
	final short[] result = new short[mappingLength];

	// Initialize all entries to -1
	for (i=0; i<mappingLength; i++)
	    result[i] = (-1);

	for (i=0; i<nsLength; i++) {
	    Integer type = (Integer)_nsIndex.get(namespaces[i]);
	    if (type != null) {
		result[type.intValue()] = (short)i;
	    }
	}

	return(result);
    }

    /**
     * Get mapping from external namespace types to DOM namespace types
     */
    public short[] getReverseNamespaceMapping(String[] namespaces) {
	int i;
	final int length = namespaces.length;
	final short[] result = new short[length];

	for (i = 0; i < length; i++) {
	    Integer type = (Integer)_nsIndex.get(namespaces[i]);
	    result[i] = (type == null) ? -1 : type.shortValue();
	}
	return result;
    }

    /**
     * Dump the whole tree to a file (serialized)
     */
    public void writeExternal(ObjectOutput out) throws IOException {
	out.writeInt(_treeNodeLimit);      // number of nodes in DOM
	out.writeInt(_firstAttributeNode); // index of first attribute node
	out.writeObject(_documentURI);     // URI of original document

	out.writeObject(_type);            // type of every node in DOM
	out.writeObject(_namespace);       // namespace URI of each type
	out.writeObject(_prefix);          // prefix type of every node in DOM

	out.writeObject(_parent);          // parent of every node in DOM
	out.writeObject(_nextSibling);     // next sibling of every node in DOM
	out.writeObject(_offsetOrChild);   // first child of every node in DOM
	out.writeObject(_lengthOrAttr);    // first attr of every node in DOM

	out.writeObject(_text);            // all text in DOM (text, PIs, etc)
	out.writeObject(_namesArray);      // names of all element/attr types
	out.writeObject(_uriArray);        // name of all URIs
	out.writeObject(_prefixArray);     // name of all prefixes

	out.writeObject(_whitespace);

	if (_dontEscape != null) {
	    out.writeObject(_dontEscape);
	}
	else {
	    out.writeObject(new BitArray(0));
	}

	out.flush();
    }

    /**
     * Read the whole tree from a file (serialized)
     */
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	_treeNodeLimit = in.readInt();
	_firstAttributeNode = in.readInt();
	_documentURI = (String)in.readObject();

	_type          = (short[])in.readObject();
	_namespace     = (short[])in.readObject();
	_prefix        = (short[])in.readObject();

	_parent        = (int[])in.readObject();
	_nextSibling   = (int[])in.readObject();
	_offsetOrChild = (int[])in.readObject();
	_lengthOrAttr  = (int[])in.readObject();

	_text          = (char[])in.readObject();
	_namesArray    = (String[])in.readObject();
	_uriArray      = (String[])in.readObject();
	_prefixArray   = (String[])in.readObject();

	_whitespace    = (BitArray)in.readObject();

	_dontEscape    = (BitArray)in.readObject();
	if (_dontEscape.size() == 0) {
	    _dontEscape = null;
        }

	_types         = setupMapping(_namesArray);
    }

    /**
     * Constructor - defaults to 32K nodes
     */
    public DOMImpl() {
	//this(32*1024);
	this(8*1024);
    }
         
    /**
     * Constructor - defines initial size
     */
    public DOMImpl(int size) {
	_type          = new short[size];
	_parent        = new int[size];
	_nextSibling   = new int[size];
	_offsetOrChild = new int[size];
	_lengthOrAttr  = new int[size];
	_text          = new char[size * 10];
	_whitespace    = new BitArray(size);
	_prefix        = new short[size];
	// _namesArray[] and _uriArray[] are allocated in endDocument
    }

    /**
     * Prints the whole tree to standard output
     */
    public void print(int node, int level) {
	switch(_type[node]) {
	case ROOT:
	    print(_offsetOrChild[node], level);
	    break;
	case TEXT:
	case COMMENT:
	case PROCESSING_INSTRUCTION:
	    System.out.print(makeStringValue(node));
	    break;
	default:                  // element
	    final String name = getNodeName(node);
	    System.out.print("<" + name);
	    for (int a = _lengthOrAttr[node]; a != NULL; a = _nextSibling[a]) {
		System.out.print("\n" + getNodeName(a) +
				 "=\"" + makeStringValue(a) + "\"");
	    }
	    System.out.print('>');
	    for (int child = _offsetOrChild[node]; child != NULL;
		 child = _nextSibling[child]) {
		print(child, level + 1);
	    }
	    System.out.println("</" + name + '>');
	    break;
	}
    }

    /**
     * Returns the name of a node (attribute or element).
     */
    public String getNodeName(final int node) {
	// Get the node type and make sure that it is within limits
	final short type = _type[node];
	switch(type) {
	case DOM.ROOT:
	case DOM.TEXT:
	case DOM.ELEMENT:
	case DOM.ATTRIBUTE:
	case DOM.COMMENT:
	    return EMPTYSTRING;
	case DOM.NAMESPACE:
	    final int index = _prefix[node];
	    return (index < _prefixArray.length) ? _prefixArray[index]
						 : EMPTYSTRING;
	case DOM.PROCESSING_INSTRUCTION:
	    final String pistr = makeStringValue(node);
	    final int col = pistr.indexOf(' ');
	    return (col > -1) ? pistr.substring(0,col) : pistr;
	default:
	    // Construct the local part (omit '@' for attributes)
	    String name  = getLocalName(node);
	    if (node >= _firstAttributeNode)
		name = name.substring(1);

	    final int pi = _prefix[node];
	    if (pi > 0) {
		final String prefix = _prefixArray[pi];
		if (prefix != EMPTYSTRING) {
		    name = prefix + ':' + name;
		}
	    }
	    return name;
	}
    }

    /**
     * Returns the namespace URI to which a node belongs
     */
    public String getNamespaceName(final int node) {
	if (_type[node] == NAMESPACE) {
	    return(EMPTYSTRING); //return getNodeValue(node);
	}
	else {
	    final int type = getNamespaceType(node);
	    final String name = _uriArray[type];
	    return (name == null) ? EMPTYSTRING : name;
	}
    }

    /**
     * Returns the string value of a single text/comment node or
     * attribute value (they are all stored in the same array).
     */
    private String makeStringValue(final int node) {
	return new String(_text, _offsetOrChild[node], _lengthOrAttr[node]);
    }

    /**
     * Returns the attribute node of a given type (if any) for an element
     */
    public int getAttributeNode(final int type, final int element) {
	for (int attr = _lengthOrAttr[element];
	     attr != NULL;
	     attr = _nextSibling[attr]) {
	    if (_type[attr] == type) return attr;
	}
	return NULL;
    }

    /**
     * Returns the value of a given attribute type of a given element
     */
    public String getAttributeValue(final int type, final int element) {
	final int attr = getAttributeNode(type, element);
	return (attr != NULL) ? makeStringValue(attr) : EMPTYSTRING;
    }

    /**
     * Returns true if a given element has an attribute of a given type
     */
    public boolean hasAttribute(final int type, final int node) {
	return (getAttributeNode(type, node) != NULL);
    }

    /**
     * This method is for testing/debugging only
     */
    public String getAttributeValue(final String name, final int element) {
	return getAttributeValue(getGeneralizedType(name), element);
    }
    
    /**
     * Returns true if the given element has any children
     */
    private boolean hasChildren(final int node) {
	if (node < _firstAttributeNode) {
	    final int type = _type[node];
	    return(((type >= NTYPES) || (type == ROOT)) &&
		   (_offsetOrChild[node] != 0));
	}
	return(false);
    }

    /**
     * Returns an iterator with all the children of a given node
     */
    public NodeIterator getChildren(final int node) {
	return hasChildren(node) ? new ChildrenIterator()
				 : EMPTYITERATOR;
    }

    /**
     * Returns an iterator with all children of a specific type
     * for a given node (element)
     */
    public NodeIterator getTypedChildren(final int type) {
	return(new TypedChildrenIterator(type));
    }

    /**
     * This is a shortcut to the iterators that implement the
     * supported XPath axes (only namespace::) is not supported.
     * Returns a bare-bones iterator that must be initialized
     * with a start node (using iterator.setStartNode()).
     */
    public NodeIterator getAxisIterator(final int axis) {
	NodeIterator iterator = null;

	switch (axis) {
	case Axis.SELF:
	    iterator = new SingletonIterator();
	    break;
	case Axis.CHILD:
	    iterator = new ChildrenIterator();
	    break;
	case Axis.PARENT:
	    return(new ParentIterator());
	case Axis.ANCESTOR:
	    return(new AncestorIterator());
	case Axis.ANCESTORORSELF:
	    return((new AncestorIterator()).includeSelf());
	case Axis.ATTRIBUTE:
	    return(new AttributeIterator());
	case Axis.DESCENDANT:
	    iterator = new DescendantIterator();
	    break;
	case Axis.DESCENDANTORSELF:
	    iterator = (new DescendantIterator()).includeSelf();
	    break;
	case Axis.FOLLOWING:
	    iterator = new FollowingIterator();
	    break;
	case Axis.PRECEDING:
	    iterator = new PrecedingIterator();
	    break;
	case Axis.FOLLOWINGSIBLING:
	    iterator = new FollowingSiblingIterator();
	    break;
	case Axis.PRECEDINGSIBLING:
	    iterator = new PrecedingSiblingIterator();
	    break;
	case Axis.NAMESPACE:
	    iterator = new NamespaceIterator();
	    break;
	default:
	    BasisLibrary.runTimeError(BasisLibrary.AXIS_SUPPORT_ERR,
				      Axis.names[axis]);
	}
	return(iterator);
    }

    /**
     * Similar to getAxisIterator, but this one returns an iterator
     * containing nodes of a typed axis (ex.: child::foo)
     */
    public NodeIterator getTypedAxisIterator(int axis, int type) {
	/* This causes an error when using patterns for elements that
	   do not exist in the DOM (translet types which do not correspond
	   to a DOM type are mapped to the DOM.ELEMENT type).
	*/

	// Most common case handled first
	if (axis == Axis.CHILD && type != ELEMENT) {
	    return new TypedChildrenIterator(type);
	}

	if (type == NO_TYPE) {
	    return EMPTYITERATOR;
	}

        if (type == ELEMENT && axis != Axis.NAMESPACE) {
	    return new FilterIterator(getAxisIterator(axis),
				      getElementFilter());
	}
	else {
	    switch (axis) {
	    case Axis.SELF:
		return new TypedSingletonIterator(type);
	    case Axis.PARENT:
		return new ParentIterator().setNodeType(type);
	    case Axis.ANCESTOR:
		return new TypedAncestorIterator(type);
	    case Axis.ANCESTORORSELF:
		return (new TypedAncestorIterator(type)).includeSelf();
	    case Axis.ATTRIBUTE:
		return new TypedAttributeIterator(type);
	    case Axis.DESCENDANT:
		return new TypedDescendantIterator(type);
	    case Axis.DESCENDANTORSELF:
		return (new TypedDescendantIterator(type)).includeSelf();
	    case Axis.FOLLOWING:
		return new TypedFollowingIterator(type);
	    case Axis.PRECEDING:
		return new TypedPrecedingIterator(type);
	    case Axis.FOLLOWINGSIBLING:
		return new TypedFollowingSiblingIterator(type);
	    case Axis.PRECEDINGSIBLING:
		return new TypedPrecedingSiblingIterator(type);
	    case Axis.NAMESPACE:
		return (type == ELEMENT) ?  
		    (NodeIterator) new NamespaceIterator() :
		    (NodeIterator) new TypedNamespaceIterator(type);
	    default:
		BasisLibrary.runTimeError(BasisLibrary.TYPED_AXIS_SUPPORT_ERR,
					  Axis.names[axis]);
	    }
	}
	return null;
    }

    /**
     * Do not thing that this returns an iterator for the namespace axis.
     * It returns an iterator with nodes that belong in a certain namespace,
     * such as with <xsl:apply-templates select="blob/foo:*"/>
     * The 'axis' specifies the axis for the base iterator from which the
     * nodes are taken, while 'ns' specifies the namespace URI type.
     */
    public NodeIterator getNamespaceAxisIterator(int axis, int ns) {
	if (ns == NO_TYPE) {
	    return EMPTYITERATOR;
	}
	else {
	    switch (axis) {
	    case Axis.CHILD:
		return new NamespaceChildrenIterator(ns);
	    case Axis.ATTRIBUTE:
		return new NamespaceAttributeIterator(ns);
	    default:
		BasisLibrary.runTimeError(BasisLibrary.TYPED_AXIS_SUPPORT_ERR,
					  Axis.names[axis]);
	    }
	}
	return null;
    }

    /**
     * Returns an iterator with all descendants of a node that are of
     * a given type.
     */
    public NodeIterator getTypedDescendantIterator(int type) {
	return (type == ELEMENT) ? (NodeIterator)
	    new FilterIterator(new DescendantIterator(), getElementFilter())
	    : (NodeIterator) new TypedDescendantIterator(type);
    }

    /**
     * Returns the nth descendant of a node
     */
    public NodeIterator getNthDescendant(int type, int n, boolean includeself) {
	NodeIterator source = (type == ELEMENT) ? (NodeIterator)
	     new FilterIterator(new DescendantIterator(), getElementFilter())
	     : (NodeIterator) new TypedDescendantIterator(type);

	if (includeself) {
	    ((NodeIteratorBase)source).includeSelf();
	}
	return new NthDescendantIterator(source, n, type);
    }

    /**
     * Copy the contents of a text-node to an output handler
     */
    public void characters(final int textNode, TransletOutputHandler handler)
	throws TransletException {
	handler.characters(_text,
			   _offsetOrChild[textNode],
			   _lengthOrAttr[textNode]);
    }

    /**
     * Copy a node-set to an output handler
     */
    public void copy(NodeIterator nodes, TransletOutputHandler handler)
	throws TransletException {
	int node;
	while ((node = nodes.next()) != NULL) {
	    copy(node, handler);
	}
    }

    /**
     * Copy the whole tree to an output handler
     */
    public void copy(TransletOutputHandler handler) throws TransletException {
	copy(ROOTNODE, handler);
    }

    /** 
     * Performs a deep copy (ref. XSLs copy-of())
     *
     * TODO: Copy namespace declarations. Can't be done until we
     *       add namespace nodes and keep track of NS prefixes
     * TODO: Copy comment nodes
     */
    public void copy(final int node, TransletOutputHandler handler)
	throws TransletException {

	final int type = _type[node];

	switch(type) {
	case ROOT:
	    for (int c=_offsetOrChild[node]; c!=NULL; c=_nextSibling[c])
		copy(c, handler);
	    break;
	case PROCESSING_INSTRUCTION:
	    copyPI(node, handler);
	    break;
	case COMMENT:
	    handler.comment(new String(_text,
				       _offsetOrChild[node],
				       _lengthOrAttr[node]));
	    break;
	case TEXT:
	    boolean last = false;
	    boolean escapeBit = false;

	    if (_dontEscape != null) {	
		escapeBit = _dontEscape.getBit(node);
		if (escapeBit) {
		    last = handler.setEscaping(false);
		}	
	    }

	    handler.characters(_text,
			       _offsetOrChild[node],
			       _lengthOrAttr[node]);

            if (_dontEscape != null && escapeBit) {
		handler.setEscaping(last);
	    }	
	    break;
	case ATTRIBUTE:
	    shallowCopy(node, handler);
	    break;
	case NAMESPACE:
	    shallowCopy(node, handler);
	    break;
	default:
	    if (isElement(node)) {
		// Start element definition
		final String name = copyElement(node, type, handler);

		// Copy element attribute
		for (int a=_lengthOrAttr[node]; a!=NULL; a=_nextSibling[a]) {
		    if (_type[a] != NAMESPACE) {
			final String uri = getNamespaceName(a);
			if (uri != EMPTYSTRING) {
			    final String prefix = _prefixArray[_prefix[a]];
			    handler.namespace(prefix, uri);
			}
			handler.attribute(getNodeName(a), makeStringValue(a));
		    }
		    else {
			handler.namespace(_prefixArray[_prefix[a]],
					  makeStringValue(a));
		    }
		}
		// Copy element children
		for (int c=_offsetOrChild[node]; c!=NULL; c=_nextSibling[c])
		    copy(c, handler);
		// Close element definition
		handler.endElement(name);
	    }
	    // Shallow copy of attribute to output handler
	    else {
		final String uri = getNamespaceName(node);
		if (uri != EMPTYSTRING) {
		    final String prefix = _prefixArray[_prefix[node]];
		    handler.namespace(prefix, uri);
		}
		handler.attribute(getNodeName(node), makeStringValue(node));
	    }
	    break;
	}
    }

    /**
     * Copies a processing instruction node to an output handler
     */ 
    private void copyPI(final int node, TransletOutputHandler handler)
	throws TransletException {
	final char[] text = _text;
	final int start = _offsetOrChild[node];
	final int length = _lengthOrAttr[node];

	// Target and Value are separated by a whitespace - find it!
	int i = start;
	while (text[i] != ' ') i++;

	final int len = i - start;
	final String target = new String(text, start, len);
	final String value  = new String(text, i + 1, length - len - 1);

	handler.processingInstruction(target, value);
    }

    /**
     * Performs a shallow copy (ref. XSLs copy())
     */
    public String shallowCopy(final int node, TransletOutputHandler handler)
	throws TransletException 
    {
	final int type = _type[node];

	switch(type) {
	case ROOT: // do nothing
	    return EMPTYSTRING;
	case TEXT:

	    handler.characters(_text,
			       _offsetOrChild[node],
			       _lengthOrAttr[node]);

	    return null;
	case PROCESSING_INSTRUCTION:
	    copyPI(node, handler);
	    return null;
	case COMMENT:
	    final String comment = new String(_text,
					      _offsetOrChild[node],
					      _lengthOrAttr[node]);
	    handler.comment(comment);
	    return null;
	case NAMESPACE:
	    handler.namespace(_prefixArray[_prefix[node]],
			      makeStringValue(node));
	    return null;
	default:
	    if (isElement(node)) {
		return(copyElement(node, type, handler));
	    }
	    else {
		final String uri = getNamespaceName(node);
		if (uri != EMPTYSTRING) {
		    final String prefix = _prefixArray[_prefix[node]];
		    handler.namespace(prefix, uri);
		}
		handler.attribute(getNodeName(node), makeStringValue(node));
		return null;
	    }
	}
    }

    private String copyElement(int node, int type,
			       TransletOutputHandler handler)
	throws TransletException 
    {
	type = type - NTYPES;
	String name = _namesArray[type];
	final int pi = _prefix[node];
	final int ui = _namespace[type];

	if (pi > 0) {
	    final String prefix = _prefixArray[pi];
	    final String uri = _uriArray[ui];
	    final String local = getLocalName(node);

	    name = prefix.equals(EMPTYSTRING) ? local : (prefix + ':' + local);
	    handler.startElement(name);
	    handler.namespace(prefix, uri);
	}
	else {
	    if (ui > 0) {
		handler.startElement(name = getLocalName(node));
		handler.namespace(EMPTYSTRING, _uriArray[ui]);
	    }
	    else {
		handler.startElement(name);
	    }
	}

	// Copy element namespaces
	for (int a = _lengthOrAttr[node]; a != NULL; a = _nextSibling[a]) {
	    if (_type[a] == NAMESPACE) {
		handler.namespace(_prefixArray[_prefix[a]],
				  makeStringValue(a));
	    }
	}

	return name;
    }

    /**
     * Returns the string value of the entire tree
     */
    private String _cachedStringValue = null;

    public String getStringValue() {
	if (_cachedStringValue == null) {
	    _cachedStringValue = getElementValue(ROOTNODE);
	}
	return _cachedStringValue;
    }

    /**
     * Returns the string value of any element
     */
    public String getElementValue(final int element) {
	// optimization: only create StringBuffer if > 1 child
	final int child = _offsetOrChild[element];
	if (child == NULL)
	    return EMPTYSTRING;
	if ((_type[child] == TEXT) && (_nextSibling[child] == NULL))
	    return makeStringValue(child);
	else
	    return stringValueAux(new StringBuffer(), element).toString();
    }

    /**
     * Helper to getElementValue() above
     */
    private StringBuffer stringValueAux(StringBuffer buffer, final int element) {
	for (int child = _offsetOrChild[element];
	     child != NULL;
	     child = _nextSibling[child]) {
	    switch (_type[child]) {
	    case TEXT:
		buffer.append(_text,
			      _offsetOrChild[child],
			      _lengthOrAttr[child]);
		break;
	    case PROCESSING_INSTRUCTION:
	    case COMMENT:
		// This method should not return anything for PIs and comments
		break;
	    default:
		stringValueAux(buffer, child);
	    }
	}
	return buffer;
    }

    public String getTreeString() {
	StringBuffer buf = new StringBuffer();
	buf = getElementString(buf, ROOTNODE);
	return buf.toString();
    }

    /**
     * Helper to getTreeString() above
     */
    private StringBuffer getElementString(StringBuffer buffer, int element) {
	String name = null;

	if (isElement(element)) {
	    if ((name = getNodeName(element)) != null) {
		buffer.append('<');
		buffer.append(name);

		int attribute = _lengthOrAttr[element];
		while (attribute != NULL) {
		    // Skip namespace nodes
		    if (_type[attribute] != NAMESPACE) {
			buffer.append(' ').append(getNodeName(attribute))
			      .append("=\"").append(getNodeValue(attribute))
			      .append('"');
		    }
		    attribute = _nextSibling[attribute];
		}

		if (_offsetOrChild[element] == NULL) {
		    buffer.append("/>");
		    return buffer;
		}
		buffer.append('>');
	    }
	}

	for (int child = _offsetOrChild[element];
	     child != NULL;
	     child = _nextSibling[child]) {
	    switch (_type[child]) {
	    case COMMENT:
		buffer.append("<!--");
		buffer.append(_text,
			      _offsetOrChild[child],
			      _lengthOrAttr[child]);
		buffer.append("-->");
		break;
	    case TEXT:
		buffer.append(_text,
			      _offsetOrChild[child],
			      _lengthOrAttr[child]);
		break;
	    case PROCESSING_INSTRUCTION:
		buffer.append("<?");
		buffer.append(_text,
			      _offsetOrChild[child],
			      _lengthOrAttr[child]);
		buffer.append("?>");
		break;
	    default:
		getElementString(buffer, child);
	    }
	}

	if (isElement(element) && name != null) {
	    buffer.append("</");
	    buffer.append(name);
	    buffer.append(">");
	}

	return buffer;
    }

    /**
     * Returns a node' defined language for a node (if any)
     */
    public String getLanguage(int node) {
	final Integer langType = (Integer)_types.get(XML_LANG_ATTRIBUTE);
	if (langType != null) {
	    final int type = langType.intValue();
	    while (node != DOM.NULL) {
		int attr = _lengthOrAttr[node];
		while (attr != DOM.NULL) {
		    if (_type[attr] == type)
			return(getNodeValue(attr));
		    attr = _nextSibling[attr];
		}
		node = getParent(node);
	    }
	}
	return(null);
    }

    /**
     * Returns an instance of the DOMBuilder inner class
     * This class will consume the input document through a SAX2
     * interface and populate the tree.
     */
    public DOMBuilder getBuilder() {
	return new DOMBuilderImpl();
    }

    /**
     * Returns a DOMBuilder class wrapped in a SAX adapter.
     * I am not sure if we need this one anymore now that the
     * DOM builder's interface is pure SAX2 (must investigate)
     */
    public TransletOutputHandler getOutputDomBuilder() {
	return new SAXAdapter(new DOMBuilderImpl());
    }

    /**
     * Returns true if a character is an XML whitespace character.
     * Order of tests is important for performance ([space] first).
     */
    private static final boolean isWhitespaceChar(char c) {
	return c == 0x20 || c == 0x0A || c == 0x0D || c == 0x09;
    }


    /****************************************************************/
    /*               DOM builder class definition                   */
    /****************************************************************/
    private final class DOMBuilderImpl implements DOMBuilder {

	private final static int ATTR_ARRAY_SIZE = 32;
	private final static int REUSABLE_TEXT_SIZE = 32;
	private final static int INIT_STACK_LENGTH = 64;

	private Hashtable _shortTexts           = null;

	private Hashtable _names                = null;
	private int       _nextNameCode         = NTYPES;
	private int       _parentStackLength    = INIT_STACK_LENGTH;
	private int[]     _parentStack          = new int[INIT_STACK_LENGTH];
	private int[]     _previousSiblingStack = new int[INIT_STACK_LENGTH];
	private int       _sp;
	private int       _baseOffset           = 0;
	private int       _currentOffset        = 0;
	private int       _currentNode          = 0;

	// Temporary structures for attribute nodes
	private int       _currentAttributeNode = 1;
	private short[]   _type2        = new short[ATTR_ARRAY_SIZE];
	private short[]   _prefix2      = new short[ATTR_ARRAY_SIZE];
	private int[]     _parent2      = new int[ATTR_ARRAY_SIZE];
	private int[]     _nextSibling2 = new int[ATTR_ARRAY_SIZE];
	private int[]     _offset       = new int[ATTR_ARRAY_SIZE];
	private int[]     _length       = new int[ATTR_ARRAY_SIZE];

	// Namespace prefix-to-uri mapping stuff
	private Hashtable _nsPrefixes   = new Hashtable();
	private int       _uriCount     = 0;
	private int       _prefixCount  = 0;

	private int       _nextNamespace = DOM.NULL;
	private int       _lastNamespace = DOM.NULL;
	
	// Stack used to keep track of what whitespace text nodes are protected
	// by xml:space="preserve" attributes and which nodes that are not.
	private int[]   _xmlSpaceStack = new int[64];
	private int     _idx = 1;
	private boolean _preserve = false;

	private static final String XML_STRING = "xml:";
	private static final String XMLSPACE_STRING = "xml:space";
	private static final String PRESERVE_STRING = "preserve";
	private static final String XML_PREFIX   = "xml";
	private static final String XMLNS_PREFIX = "xmlns";

	private boolean _escaping = true;
	private boolean _disableEscaping = false;

	/**
	 * Default constructor for the DOMBuiler class
	 */
	public DOMBuilderImpl() {
	    _xmlSpaceStack[0] = DOM.ROOTNODE;
	}

	/**
	 * Returns the namespace URI that a prefix currently maps to
	 */
	private String getNamespaceURI(String prefix) {
	    // Get the stack associated with this namespace prefix
	    final Stack stack = (Stack)_nsPrefixes.get(prefix);
	    return (stack != null && !stack.empty()) ? (String) stack.peek()
		: EMPTYSTRING;
	}

	/**
	 * Call this when an xml:space attribute is encountered to
	 * define the whitespace strip/preserve settings.
	 */
	private void xmlSpaceDefine(String val, final int node) {
	    final boolean setting = val.equals(PRESERVE_STRING);
	    if (setting != _preserve) {
		_xmlSpaceStack[_idx++] = node;
		_preserve = setting;
	    }
	}

	/**
	 * Call this from endElement() to revert strip/preserve setting
	 * to whatever it was before the corresponding startElement()
	 */
	private void xmlSpaceRevert(final int node) {
	    if (node == _xmlSpaceStack[_idx - 1]) {
		_idx--;
		_preserve = !_preserve;
	    }
	}

	/**
	 * Returns the next available node. Increases the various arrays
	 * that constitute the node if necessary.
	 */
	private int nextNode() {
	    final int index = _currentNode++;
	    if (index == _type.length) {
		resizeArrays(_type.length * 2, index);
	    }
	    return index;
	}

	/**
	 * Returns the next available attribute node. Increases the
	 * various arrays that constitute the attribute if necessary
	 */
	private int nextAttributeNode() {
	    final int index = _currentAttributeNode++;
	    if (index == _type2.length) {
		resizeArrays2(_type2.length * 2, index);
	    }
	    return index;
	}

	/**
	 * Resize the character array that holds the contents of
	 * all text nodes, comments and attribute values
	 */
	private void resizeTextArray(final int newSize) {
	    final char[] newText = new char[newSize];
	    System.arraycopy(_text, 0, newText, 0, _currentOffset);
	    _text = newText;
	}
	
	/**
	 * Links together the children of a node. Child nodes are linked
	 * through the _nextSibling array
	 */
	private void linkChildren(final int node) {
	    _parent[node] = _parentStack[_sp];
	    if (_previousSiblingStack[_sp] != 0) { // current not first child
		_nextSibling[_previousSiblingStack[_sp]] = node;
	    }
	    else {
		_offsetOrChild[_parentStack[_sp]] = node;
	    }
	    _previousSiblingStack[_sp] = node;
	}

	/**
	 * Sets the current parent
	 */
	private void linkParent(final int node) {
	    if (++_sp >= _parentStackLength) {
		int length = _parentStackLength;
		_parentStackLength = length + INIT_STACK_LENGTH;

		final int newParent[] = new int[_parentStackLength];
		System.arraycopy(_parentStack,0,newParent,0,length);
		_parentStack = newParent;

		final int newSibling[] = new int[_parentStackLength];
		System.arraycopy(_previousSiblingStack,0,newSibling,0,length);
		_previousSiblingStack = newSibling;
	    }
	    _parentStack[_sp] = node;
	}

	/**
	 * Generate the internal type for an element's expanded QName
	 */
	private short makeElementNode(String uri, String localname)
	    throws SAXException {
	    final String name;
	    if (uri != EMPTYSTRING)
		name = uri + ':' + localname;
	    else
		name = localname;

	    // Stuff the QName into the names vector & hashtable
	    Integer obj = (Integer)_names.get(name);
	    if (obj == null) {
		_names.put(name, obj = new Integer(_nextNameCode++));
	    }
	    return (short)obj.intValue();
	}

	/**
	 * Generate the internal type for an element's expanded QName
	 */
	private short makeElementNode(String name, int col)
	    throws SAXException {
	    // Expand prefix:localname to full QName
	    if (col > -1) {
		final String uri = getNamespaceURI(name.substring(0, col));
		name = uri + name.substring(col);
	    }
	    // Append default namespace with the name has no prefix
	    else {
		final String uri = getNamespaceURI(EMPTYSTRING);
		if (!uri.equals(EMPTYSTRING)) name = uri + ':' + name;
	    }

	    // Stuff the QName into the names vector & hashtable
	    Integer obj = (Integer)_names.get(name);
	    if (obj == null) {
		_names.put(name, obj = new Integer(_nextNameCode++));
	    }
	    return (short)obj.intValue();
	}

	/**
	 *
	 */
	private short registerPrefix(String prefix) {
	    Stack stack = (Stack)_nsPrefixes.get(prefix);
	    if (stack != null) {
		Integer obj = (Integer)stack.elementAt(0);
		return (short)obj.intValue();
	    }
	    return 0;
	}

	/*
	 * This method will check if the current text node contains text that
	 * is already in the text array. If the text is found in the array
	 * then this method returns the offset of the previous instance of the
	 * string. Otherwise the text is inserted in the array, and the
	 * offset of the new instance is inserted.
	 * Updates the globals _baseOffset and _currentOffset
	 */
	private int maybeReuseText(final int length) {
	    final int base = _baseOffset;
	    if (length <= REUSABLE_TEXT_SIZE) {
		// Use a char array instead of string for performance benefit
		char[] chars = new char[length];
		System.arraycopy(_text, base, chars, 0, length);
		final Integer offsetObj = (Integer)_shortTexts.get(chars);

		if (offsetObj != null) {
		    _currentOffset = base;       // step back current
		    return offsetObj.intValue(); // reuse previous string
		}
		else {
		    _shortTexts.put(chars, new Integer(base));
		}
	    }
	    _baseOffset = _currentOffset; // advance base to current
	    return base;
	}

	/**
	 * Links a text reference (an occurance of a sequence of characters
	 * in the _text[] array) to a specific node index.
	 */
	private void storeTextRef(final int node) {
	    final int length = _currentOffset - _baseOffset;
	    _offsetOrChild[node] = maybeReuseText(length);
	    _lengthOrAttr[node]  = length;
	}
	
	/**
	 * Creates a text-node and checks if it is a whitespace node.
	 */
	private int makeTextNode(boolean isWhitespace) {
	    if (_currentOffset > _baseOffset) {

		final int node = nextNode();
		final int limit = _currentOffset;
		// Tag as whitespace node if the parser tells us that it is...
		if (isWhitespace) {
		    _whitespace.setBit(node);
		}
		// ...otherwise we check if this is a whitespace node, unless
		// the node is protected by an xml:space="preserve" attribute.
		else if (!_preserve) {
		    int i = _baseOffset;
		    while (isWhitespaceChar(_text[i++]) && i < limit) ;
		    if ((i == limit) && isWhitespaceChar(_text[i-1])) {
			_whitespace.setBit(node);
		    }
		}
		_type[node] = TEXT;
		linkChildren(node);
		storeTextRef(node);

		if (_disableEscaping) {
		    if (_dontEscape == null) {
			_dontEscape = new BitArray(_whitespace.size());
		    }
		    _dontEscape.setBit(node);
		    _disableEscaping = false;
		}
		return node;
	    }
	    return -1;
	}

	/**
	 * Links an attribute value (an occurance of a sequence of characters
	 * in the _text[] array) to a specific attribute node index.
	 */
	private void storeAttrValRef(final int attributeNode) {
	    final int length = _currentOffset - _baseOffset;
	    _offset[attributeNode] = maybeReuseText(length);
	    _length[attributeNode] = length;
	}

	private int makeNamespaceNode(String prefix, String uri)
	    throws SAXException {

    	    final int node = nextAttributeNode();
	    _type2[node] = NAMESPACE;
	    characters(uri);
	    storeAttrValRef(node);
	    return node;	    
	}

	/**
	 * Creates an attribute node
	 */
	private int makeAttributeNode(int parent, Attributes attList, int i)
	    throws SAXException 
	{
    	    final int node = nextAttributeNode();
	    final String qname = attList.getQName(i);
	    String localName = attList.getLocalName(i);
	    final String value = attList.getValue(i);
	    StringBuffer namebuf = new StringBuffer(EMPTYSTRING);
	    
	    if (qname.startsWith(XMLSPACE_STRING)) {
		xmlSpaceDefine(attList.getValue(i), parent);
	    }

	    // If local name is null set it to the empty string
	    if (localName == null) {
		localName = EMPTYSTRING;
	    }

	    // Create the internal attribute node name (uri+@+localname)
	    final String uri = attList.getURI(i);
	    if (uri != null && !uri.equals(EMPTYSTRING)) {
		namebuf.append(uri);
		namebuf.append(':');
	    }
	    namebuf.append('@');
	    namebuf.append(localName.length() > 0 ? localName : qname);

	    String name = namebuf.toString();

	    // Get the index of the attribute node name (create new if non-ex).
	    Integer obj = (Integer)_names.get(name);
	    if (obj == null) {
		_type2[node] = (short)_nextNameCode;
		_names.put(name, obj = new Integer(_nextNameCode++));
	    }
	    else {
		_type2[node] = (short)obj.intValue();
	    }

	    final int col = qname.lastIndexOf(':');
	    if (col > 0) {
		_prefix2[node] = registerPrefix(qname.substring(0, col));
	    }

	    characters(attList.getValue(i));
	    storeAttrValRef(node);
	    return node;
	}
	
	/****************************************************************/
	/*               SAX Interface Starts Here                      */
	/****************************************************************/

	/**
	 * SAX2: Receive notification of character data.
	 */
	public void characters(char[] ch, int start, int length) {
	    if (_currentOffset + length > _text.length) {
		resizeTextArray(
		    Math.max(_text.length * 2, _currentOffset + length));
	    }
	    System.arraycopy(ch, start, _text, _currentOffset, length);
	    _currentOffset += length;

	    _disableEscaping = !_escaping;	
	}

	/**
	 * SAX2: Receive notification of the beginning of a document.
	 */
	public void startDocument() throws SAXException {
	    _shortTexts     = new Hashtable();
	    _names          = new Hashtable();
	    _sp             = 0;
	    _parentStack[0] = ROOTNODE;	// root
	    _currentNode    = ROOTNODE + 1;
	    _currentAttributeNode = 1;
	    _type2[0] = NAMESPACE;

	    definePrefixAndUri(EMPTYSTRING, EMPTYSTRING);
	    startPrefixMapping(XML_PREFIX, "http://www.w3.org/XML/1998/namespace");

	    _lengthOrAttr[ROOTNODE] = _nextNamespace;
	    _parent2[_nextNamespace] = ROOTNODE;
	    _nextNamespace = DOM.NULL;
	}

	/**
	 * SAX2: Receive notification of the end of a document.
	 */
	public void endDocument() {
	    makeTextNode(false);

	    _shortTexts = null;
	    final int namesSize = _nextNameCode - NTYPES;

	    // Fill the _namesArray[] array
	    _namesArray = new String[namesSize];
	    Enumeration keys = _names.keys();
	    while (keys.hasMoreElements()) {
		final String name = (String)keys.nextElement();
		final Integer idx = (Integer)_names.get(name);
		_namesArray[idx.intValue() - NTYPES] = name;
	    }

	    _names = null;
	    _types = setupMapping(_namesArray);

	    // trim arrays' sizes
	    resizeTextArray(_currentOffset);
	    
	    _firstAttributeNode = _currentNode;
	    shiftAttributes(_currentNode);
	    resizeArrays(_currentNode + _currentAttributeNode, _currentNode);
	    appendAttributes();
	    _treeNodeLimit = _currentNode + _currentAttributeNode;

	    // Fill the _namespace[] and _uriArray[] array
	    _namespace = new short[namesSize];
	    _uriArray = new String[_uriCount];
	    for (int i = 0; i<namesSize; i++) {
		final String qname = _namesArray[i];
		final int col = _namesArray[i].lastIndexOf(':');
		// Elements/attributes with the xml prefix are not in a NS
		if ((!qname.startsWith(XML_STRING)) && (col > -1)) {
		    final String uri = _namesArray[i].substring(0, col);
		    final Integer idx = (Integer)_nsIndex.get(uri);
		    if (idx != null) {
			_namespace[i] = idx.shortValue();
			_uriArray[idx.intValue()] = uri;
		    }
		}
	    }

	    _prefixArray = new String[_prefixCount];
	    Enumeration p = _nsPrefixes.keys();
	    while (p.hasMoreElements()) {
		final String prefix = (String)p.nextElement();
		final Stack stack = (Stack)_nsPrefixes.get(prefix);
		final Integer I = (Integer)stack.elementAt(0);
		_prefixArray[I.shortValue()] = prefix;
	    }
	}
	
	/**
	 * SAX2: Receive notification of the beginning of an element.
	 */
	public void startElement(String uri, String localName,
				 String qname, Attributes attributes)
	    throws SAXException 
	{
// System.out.println("DOMImpl.startElement() qname = " + qname);
	    makeTextNode(false);

	    // Get node index and setup parent/child references
	    final int node = nextNode();
	    linkChildren(node);
	    linkParent(node);

	    _lengthOrAttr[node] = DOM.NULL;

	    int last = -1;
	    final int count = attributes.getLength();

	    // Append any namespace nodes
	    if (_nextNamespace != DOM.NULL) {
		_lengthOrAttr[node] = _nextNamespace;

		while (_nextNamespace != DOM.NULL) {
		    _parent2[_nextNamespace] = node;
		    _nextNamespace = _nextSibling2[last = _nextNamespace];
		    // Chain last namespace node to following attribute node(s)
		    if (_nextNamespace == DOM.NULL && count > 0) {
			_nextSibling2[last] = _currentAttributeNode;
		    }
		}
	    }

	    // If local name is null set it to the empty string
	    if (localName == null) {
		localName = EMPTYSTRING;
	    }

	    // Append any attribute nodes
	    boolean attrsAdded = false;
	    if (count > 0) {
		int attr = _currentAttributeNode;
		if (_lengthOrAttr[node] == DOM.NULL) {
		    _lengthOrAttr[node] = attr;
		}
		for (int i = 0; i < count; i++) {
		    if (!attributes.getQName(i).startsWith(XMLNS_PREFIX)) {
			attr = makeAttributeNode(node, attributes, i);
			_parent2[attr] = node;
			_nextSibling2[attr] = attr + 1;
			attrsAdded = true;
		    }
		}
		// Did we append namespace nodes only?
		_nextSibling2[(!attrsAdded && last != -1) ? last : attr] = DOM.NULL;
	    }

	    final int col = qname.lastIndexOf(':');

	    // Assign an internal type to this element (may exist)
	    _type[node] = (uri != null && localName.length() > 0) ?
		makeElementNode(uri, localName) : makeElementNode(qname, col);

	    // Assign an internal type to the element's prefix (may exist)
	    if (col > -1) {
		_prefix[node] = registerPrefix(qname.substring(0, col));
	    }
	}
	
	/**
	 * SAX2: Receive notification of the end of an element.
	 */
	public void endElement(String uri, String localName,
			       String qname) {
	    makeTextNode(false);

	    // Revert to strip/preserve-space setting from before this element
	    xmlSpaceRevert(_parentStack[_sp]);
	    _previousSiblingStack[_sp--] = 0;
	}

	/**
	 * SAX2: Receive notification of a processing instruction.
	 */
	public void processingInstruction(String target, String data)
	    throws SAXException {

	    makeTextNode(false);

	    final int node = nextNode();
	    _type[node] = PROCESSING_INSTRUCTION;
	    linkChildren(node);
	    characters(target);
	    characters(" ");
	    characters(data);
	    storeTextRef(node);
	}

	/**
	 * SAX2: Receive notification of ignorable whitespace in element
	 * content. Similar to characters(char[], int, int).
	 */
	public void ignorableWhitespace(char[] ch, int start, int length) {
	    if (_currentOffset + length > _text.length) {
		resizeTextArray(
		    Math.max(_text.length * 2, _currentOffset + length));
	    }
	    System.arraycopy(ch, start, _text, _currentOffset, length);
	    _currentOffset += length;
	    makeTextNode(true);
	}

	/**
	 * SAX2: Receive an object for locating the origin of SAX document
	 * events. 
	 */
	public void setDocumentLocator(Locator locator) {
	    // Not handled
	}

	/**
	 * SAX2: Receive notification of a skipped entity.
	 */
	public void skippedEntity(String name) {
	    // Not handled 
	}

	/**
	 * SAX2: Begin the scope of a prefix-URI Namespace mapping.
	 */
	public void startPrefixMapping(String prefix, String uri) 
	    throws SAXException 
	{
	    final Stack stack = definePrefixAndUri(prefix, uri);

	    makeTextNode(false);
	    int attr = makeNamespaceNode(prefix, uri);
	    if (_nextNamespace == DOM.NULL) {
		_nextNamespace = attr;
	    }
	    else {
		_nextSibling2[attr-1] = attr;
	    }
	    _nextSibling2[attr] = DOM.NULL;
	    _prefix2[attr] = ((Integer) stack.elementAt(0)).shortValue();
	}

	private Stack definePrefixAndUri(String prefix, String uri) 
	    throws SAXException 
	{
	    // Get the stack associated with this namespace prefix
	    Stack stack = (Stack)_nsPrefixes.get(prefix);
	    if (stack == null) {
		stack = new Stack();
		stack.push(new Integer(_prefixCount++));
		_nsPrefixes.put(prefix, stack);
	    }

	    // Check if the URI already exists before pushing on stack
	    Integer idx;
	    if ((idx = (Integer)_nsIndex.get(uri)) == null) {
		_nsIndex.put(uri, idx = new Integer(_uriCount++));
	    }
	    stack.push(uri);

	    return stack;
	}

	/**
	 * SAX2: End the scope of a prefix-URI Namespace mapping.
	 */
	public void endPrefixMapping(String prefix) {
	    // Get the stack associated with this namespace prefix
	    final Stack stack = (Stack)_nsPrefixes.get(prefix);
	    if ((stack != null) && (!stack.empty())) stack.pop();
	}

	/**
	 * SAX2: Report an XML comment anywhere in the document.
	 */
	public void comment(char[] ch, int start, int length) {
	    makeTextNode(false);
	    if (_currentOffset + length > _text.length) {
		resizeTextArray(
		    Math.max(_text.length * 2, _currentOffset + length));
	    }
	    System.arraycopy(ch, start, _text, _currentOffset, length);
	    _currentOffset += length;
	    final int node = makeTextNode(false);
	    _type[node] = COMMENT;
	}

	/**
	 * SAX2: Ignored events
	 */
	public void startCDATA() {}
	public void endCDATA() {}
	public void startDTD(String name, String publicId, String systemId) {}
	public void endDTD() {}
	public void startEntity(String name) {}
	public void endEntity(String name) {}

	/**
	 * Similar to the SAX2 method character(char[], int, int), but this
	 * method takes a string as its only parameter. The effect is the same.
	 */
	private void characters(final String string) {
	    final int length = string.length();
	    if (_currentOffset + length > _text.length) {
		resizeTextArray(
		    Math.max(_text.length * 2, _currentOffset + length));
	    }
	    string.getChars(0, length, _text, _currentOffset);
	    _currentOffset += length;
	}

	private void resizeArrays(final int newSize, int length) {
	    if ((length < newSize) && (newSize == _currentNode))
		length = _currentNode;

	    // Resize the '_type' array
	    final short[] newType = new short[newSize];
	    System.arraycopy(_type, 0, newType, 0, length);
	    _type = newType;

	    // Resize the '_parent' array
	    final int[] newParent = new int[newSize];
	    System.arraycopy(_parent, 0, newParent, 0, length);
	    _parent = newParent;

	    // Resize the '_nextSibling' array
	    final int[] newNextSibling = new int[newSize];
	    System.arraycopy(_nextSibling, 0, newNextSibling, 0, length);
	    _nextSibling = newNextSibling;

	    // Resize the '_offsetOrChild' array
	    final int[] newOffsetOrChild = new int[newSize];
	    System.arraycopy(_offsetOrChild, 0, newOffsetOrChild, 0,length);
	    _offsetOrChild = newOffsetOrChild;

	    // Resize the '_lengthOrAttr' array
	    final int[] newLengthOrAttr = new int[newSize];
	    System.arraycopy(_lengthOrAttr, 0, newLengthOrAttr, 0, length);
	    _lengthOrAttr = newLengthOrAttr;

	    // Resize the '_whitespace' array (a BitArray instance)
	    _whitespace.resize(newSize);

	    // Resize the '_dontEscape' array (a BitArray instance)
	    if (_dontEscape != null) {
		_dontEscape.resize(newSize);
	    }

	    // Resize the '_prefix' array
	    final short[] newPrefix = new short[newSize];
	    System.arraycopy(_prefix, 0, newPrefix, 0, length);
	    _prefix = newPrefix;
	}
	
	private void resizeArrays2(final int newSize, final int length) {
	    if (newSize > length) {
		// Resize the '_type2' array (attribute types)
		final short[] newType = new short[newSize];
		System.arraycopy(_type2, 0, newType, 0, length);
		_type2 = newType;

		// Resize the '_parent2' array (attribute parent elements)
		final int[] newParent = new int[newSize];
		System.arraycopy(_parent2, 0, newParent, 0, length);
		_parent2 = newParent;

		// Resize the '_nextSibling2' array (you get the idea...)
		final int[] newNextSibling = new int[newSize];
		System.arraycopy(_nextSibling2, 0, newNextSibling, 0, length);
		_nextSibling2 = newNextSibling;

		// Resize the '_offset' array (attribute value start)
		final int[] newOffset = new int[newSize];
		System.arraycopy(_offset, 0, newOffset, 0, length);
		_offset = newOffset;

		// Resize the 'length' array (attribute value length)
		final int[] newLength = new int[newSize];
		System.arraycopy(_length, 0, newLength, 0, length);
		_length = newLength;

		// Resize the '_prefix2' array
		final short[] newPrefix = new short[newSize];
		System.arraycopy(_prefix2, 0, newPrefix, 0, length);
		_prefix2 = newPrefix;
	    }
	}
	
	private void shiftAttributes(final int shift) {
	    int i = 0;
	    int next = 0;
	    final int limit = _currentAttributeNode;
	    int lastParent = -1;

	    for (i = 0; i < limit; i++) {
		if (_parent2[i] != lastParent) {
		    lastParent = _parent2[i];
		    _lengthOrAttr[lastParent] = i + shift;
		}
		next = _nextSibling2[i];
		_nextSibling2[i] = next != 0 ? next + shift : 0;
	    }
	}
	
	private void appendAttributes() {
	    final int len = _currentAttributeNode;
	    if (len > 0) {
		final int dst = _currentNode;
		System.arraycopy(_type2,         0, _type,          dst, len);
		System.arraycopy(_prefix2,       0, _prefix,        dst, len);
		System.arraycopy(_parent2,       0, _parent,        dst, len);
		System.arraycopy(_nextSibling2,  0, _nextSibling,   dst, len);
		System.arraycopy(_offset,        0, _offsetOrChild, dst, len);
		System.arraycopy(_length,        0, _lengthOrAttr,  dst, len);
	    }
	}

 	public boolean setEscaping(boolean value) {
	    final boolean temp = _escaping;
	    _escaping = value; 
	    return temp;
    	}

    } // end of DOMBuilder
}
