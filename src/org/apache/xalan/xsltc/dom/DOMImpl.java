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
 *
 */

/* Issues to resolve:
   o) All stacks in the DOMBuilder class have hardcoded length
   o) There are no namespace nodes (but namespace are handled correctly).
*/

package org.apache.xalan.xsltc.dom;

import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Dictionary;
import java.util.Enumeration;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Document;
import org.xml.sax.*;
import org.apache.xalan.xsltc.*;
import org.apache.xalan.xsltc.util.IntegerArray;
import org.apache.xalan.xsltc.runtime.BasisLibrary;
import org.apache.xalan.xsltc.runtime.SAXAdapter;

public final class DOMImpl implements DOM, Externalizable {

    // empty String for null attribute values
    private final static String EMPTYSTRING = "";


    // empty iterator to be returned when there are no children
    private final static NodeIterator EMPTYITERATOR =
	new NodeIterator() {
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
	    };

    // empty attribute list to be returned for nodes with no attributes
    private static final AttributeList EmptyAttributes =
	new AttributeList() {
		public int getLength() { return 0; }
		public String getName(int i) { return null; }
		public String getType(int i) { return null; }
		public String getValue(int i) { return null; }
		public String getType(String name) { return null; }
		public String getValue(String name) { return null; }
	    };

    // document tree representation data structures
    private int       _treeNodeLimit;
    private int       _firstAttributeNode;
    private short[]   _type;
    private short[]   _namespace;
    private int[]     _parent;
    private int[]     _nextSibling;
    private int[]     _offsetOrChild;
    private int[]     _lengthOrAttr;
    private char[]    _text;
    private String[]  _namesArray;
    private String[]  _nsNamesArray;
    private BitArray  _whitespace;
    private short[]   _mapping = null;

    private Hashtable _types = null;
    private Hashtable _nsIndex = new Hashtable();

    private String    _documentURI;

    private int nsCounter = 0;

    private String generateNamespacePrefix() {
	return(new String("ns"+Integer.toString(nsCounter++)));
    }

    public void setDocumentURI(String uri) {
	_documentURI = uri;
    }

    public String getDocumentURI() {
	return(_documentURI);
    }

    public void setFilter(StripWhitespaceFilter filter) { }

    private boolean isElement(final int node) {
	return ((node < _treeNodeLimit) && (_type[node] >= NTYPES));
    }

    public int getSize() {
	return(_type.length);
    }

    public boolean lessThan(int node1, int node2) {
	if ((node2 < _treeNodeLimit) && (node1 < node2))
	    return(true);
	else
	    return(false);
    }

    // Support for access/navigation through org.w3c.dom API
    private Node[] _nodes;

    public Node makeNode(int index) {
	if (_nodes == null) {
	    _nodes = new Node[_type.length];
	}
	return _nodes[index] != null
	    ? _nodes[index]
	    : (_nodes[index] = new NodeImpl(index));
    }

    public Node makeNode(NodeIterator iter) {
	return makeNode(iter.next());	// iter must be started
    }

    private NodeList[] _nodeLists;

    public NodeList makeNodeList(int index) {
	if (_nodeLists == null) {
	    _nodeLists = new NodeList[_type.length];
	}
	return _nodeLists[index] != null
	    ? _nodeLists[index]
	    : (_nodeLists[index] = new NodeListImpl(index));
    }

    public NodeList makeNodeList(NodeIterator iter) {
	return new NodeListImpl(iter);	// iter must be started
    }

    private final class NotSupportedException extends DOMException {
	public NotSupportedException() {
	    super(NOT_SUPPORTED_ERR, "modification not supported");
	}
    } // end of NotSupportedException
	
    private static NodeList EmptyNodeList;
    private static NamedNodeMap EmptyNamedNodeMap;
	
    private NodeList getEmptyNodeList() {
	return EmptyNodeList != null
	    ? EmptyNodeList
	    : (EmptyNodeList = new NodeListImpl(new int[0]));
    }

    private NamedNodeMap getEmptyNamedNodeMap() {
	return EmptyNamedNodeMap != null
	    ? EmptyNamedNodeMap
	    : (EmptyNamedNodeMap = new NamedNodeMapImpl(new int[0]));
    }

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
    } // end of NodeListImpl
		
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
    } // end of NamedNodeMapImpl
	
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
		return _index < _treeNodeLimit
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
	    // it can be supported if need be
	    // with and additional field to signify
	    // whether it is a deep or shallow clone
	    throw new NotSupportedException();
	}
		
	public NamedNodeMap getAttributes() {
	    if (getNodeType() == Node.ELEMENT_NODE) {
		int attribute = _lengthOrAttr[_index];
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
		return "***PI Name NYI";
		
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
    } // end of NodeImpl

    public Filter getTypeFilter(int type) {
	return new TypeFilter(type);
    }

    // single copy of ElementFilter
    private Filter _elementFilter;

    private Filter getElementFilter() {
	return _elementFilter != null
	    ? _elementFilter
	    : (_elementFilter = new Filter() {
		    public boolean test(int node) {
			return isElement(node);
		    }
		});
    }

    private final class TypeFilter implements Filter {
	private final int _nodeType;
		
	public TypeFilter(int type) {
	    _nodeType = type;
	}
		
	public boolean test(int node) {
	    return _type[node] == _nodeType;
	}
    } // end of TypeFilter


    /**************************************************************
     * Iterator that returns all children of a given node
     */
    private final class ChildrenIterator extends NodeIteratorBase {
	// child to return next
	private int _currentChild;
	
	public NodeIterator setStartNode(final int node) {
	    if (_isRestartable) {
		_currentChild = hasChildren(node)
		    ? _offsetOrChild[_startNode = node] : END;
		return resetPosition();
	    }
	    return this;
	}

	public int next() {
	    final int node = _currentChild;
	    _currentChild = _nextSibling[node];
	    return returnNode(node);
	}

	public void setMark() {
	    _markedNode = _currentChild;
	}

	public void gotoMark() {
	    _currentChild = _markedNode;
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
	private final int _nodeType;
	// node to consider next
	private int _currentChild;
	
	public TypedChildrenIterator(int nodeType) {
	    _nodeType = nodeType;
	}

	public NodeIterator setStartNode(int node) {
	    if (_isRestartable) {
		_currentChild = hasChildren(node)
		    ? _offsetOrChild[_startNode = node] : END;
		return resetPosition();
	    }
	    return this;
	}

	public int next() {
	    for (int node = _currentChild; node != END;
		 node = _nextSibling[node]) {
		if (_type[node] == _nodeType) {
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
	    _attribute = _nextSibling[_attribute];
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
	    while ((node = super.next()) != NULL && _type[node] != _nodeType) {
	    }
	    return node;
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
		_attribute = isElement(node)
		    ? _lengthOrAttr[_startNode = node]
		    : NULL;
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
		
	// assumes caller will pass element nodes
	public NodeIterator setStartNode(int node) {
	    if (_isRestartable) {
		for (node = _lengthOrAttr[_startNode = node];
		     node != NULL && _type[node] != _nodeType;
		     node = _nextSibling[node]);
		_attribute = node;
		return resetPosition();
	    }
	    return this;
	}
		
	public int next() {
	    final int node = _attribute;
	    _attribute = NULL;	// singleton iterator
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
     * Iterator that returns preceding siblings of a given node
     */
    private class PrecedingSiblingIterator extends NodeIteratorBase {
	private int _start;
	private int _node;
	
	public boolean isReverse() {
	    return true;
	}
	
	public NodeIterator setStartNode(int node) {
	    if (_isRestartable) {
		_node = _offsetOrChild[_parent[_startNode = _start = node]];
		return resetPosition();
	    }
	    return this;
	}
		
	public int next() {
	    if (_node == _start) {
		return NULL;
	    }
	    else {
		final int node = _node;
		_node = _nextSibling[node];
		return returnNode(node);
	    }
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
	    while ((node = super.next()) != NULL && _type[node] != _nodeType) {
	    }
	    return node;
	}

    } // end of PrecedingSiblingIterator


    /**************************************************************
     * Iterator that returns preceding nodes of a given node.
     * This includes the node set {root+1, start-1}, but excludes
     * all ancestors.
     */
    private class PrecedingIterator extends NodeIteratorBase {
	// start node + ancestors up to ROOTNODE
	private int[] _stack = new int[8];
	private int _sp, _oldsp;
	
	//  _node precedes candidates
	private int _node;

	public boolean isReverse() {
	    return true;
	}
	
	public NodeIterator cloneIterator() {
	    _isRestartable = false;
	    final int[] stackCopy = new int[8];
	    try {
		final PrecedingIterator clone = 
		    (PrecedingIterator)super.clone();
		System.arraycopy(_stack, 0, stackCopy, 0, _stack.length);
		clone._stack = stackCopy; 
		return clone.reset();
	    }
	    catch (CloneNotSupportedException e) {
		BasisLibrary.runTimeError("Iterator clone not supported.");
		return null;
	    }
	}
	
	public NodeIterator setStartNode(int node) {
	    if (_isRestartable) {
		// iterator is not a clone
		int parent, index;
		_startNode = node;
		_node = ROOTNODE;
		_stack[index = 0] = node;
		if (node > ROOTNODE) {
		    while ((parent = _parent[node]) != ROOTNODE) {
			if (++index == _stack.length) {
			    final int[] stack = new int[index + 4];
			    System.arraycopy(_stack, 0, stack, 0, index);
			    _stack = stack;
			}
			_stack[index] = node = parent;
		    }
		}
		_oldsp = _sp = index;
		return resetPosition();
	    }
	    return this;
	}
		
	public int next() {
	    final int node = _node + 1;
	    if ((_sp >= 0) && (node < _stack[_sp])) {
		return returnNode(_node = node);
	    }
	    else {
		_node = node;	// skip ancestor
		return --_sp >= 0 ? next() : NULL;
	    }
	}

	// redefine NodeIteratorBase's reset
	public NodeIterator reset() {
	    _sp = _oldsp;
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
	    while ((node = super.next()) != NULL && _type[node] != _nodeType) {
	    }
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
	    if (_isRestartable) {
		_startNode = node;
		// find rightmost descendant (or self)
		int current;
		while ((node = lastChild(current = node)) != NULL) {
		}
		_node = current;
		// _node precedes possible following(node) nodes
		return resetPosition();
	    }
	    return this;
	}
		    
	public int next() {
	    final int node = _node + 1;
	    return node < _treeNodeLimit ? returnNode(_node = node) : NULL;
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
	    final int limit = _treeNodeLimit;
	    int node = _node + 1;
	    final int type = _nodeType;
	    // skipping nodes not of desired type
	    while (node < limit && _type[node] != type) {
		++node;
	    }
	    return node == limit ? NULL : returnNode(_node = node);
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

	public final boolean isReverse() {
	    return true;
	}

	public int getLast() {
	    return(ROOT);
	}
	
	public NodeIterator cloneIterator() {
	    _isRestartable = false;	// must set to false for any clone
	    try {
		final AncestorIterator clone = (AncestorIterator)super.clone();
		clone._startNode = _startNode;
		return clone.reset();
	    } catch (CloneNotSupportedException e) {
		BasisLibrary.runTimeError("Iterator clone not supported.");
		return null;
	    }
	}
		
	public NodeIterator setStartNode(int node) {
	    if (_isRestartable) {
		if (_includeSelf)
		    _startNode = node;
		else
		    _startNode = _parent[node];
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
		int bob = _index;
		if (_index == 0)
		    _index = -1;
		else
		    _index = _parent[_index];
		return returnNode(bob);
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
		if (_type[node] == _nodeType)
		    return returnNode(node);
	    }
	    return(NULL);
	}

	public int getLast() {
	    int last = _index;
	    int curr = _index;

	    while (curr >= 0) {
		if (curr == 0)
		    curr = -1;
		else
		    curr = _parent[curr];
		if (_type[curr] == _nodeType)
		    last = curr;
	    }
	    return(last);
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
	    if (++_node >= (_limit))
		return(NULL);
	    else
		return(returnNode(_node));
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

	int _pos;

	public NthDescendantIterator(int pos) {
	    _pos = pos;
	    _limit = _treeNodeLimit;
	}

	// The start node of this iterator is always the root!!!
	public NodeIterator setStartNode(int node) {
	    NodeIterator iterator = super.setStartNode(1);
	    _limit = _treeNodeLimit;
	    return iterator;
	}

	public int next() {
	    int node;
	    while ((node = super.next()) != END) {
		int parent = _parent[node];
		int child = _offsetOrChild[parent];
		int pos = 0;

		do {
		    if (isElement(child)) pos++;
		} while ((pos < _pos) && (child = _nextSibling[child]) != 0);
		if (node == child) return node;
	    }
	    return(END);
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

	private StripWhitespaceFilter _filter = null;
	private short[] _mapping = null;
	private final NodeIterator _source;
	private boolean _children = false;
	private int _action = USE_PREDICATE;
	private int _last = -1;

	public StrippingIterator(NodeIterator source,
				 short[] mapping,
				 StripWhitespaceFilter filter) {

	    _filter = filter;
	    _mapping = mapping;
	    _source = source;

	    if (_source instanceof ChildrenIterator ||
		_source instanceof TypedChildrenIterator) {
		_children = true;
	    }
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
	    return resetPosition();
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
		    if (_whitespace.getBit(node) &&
			_filter.stripSpace((DOM)DOMImpl.this, node,
					   _mapping[_type[_parent[node]]]))
			continue;
		    return returnNode(node);
		}
	    }
	    return END;
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

    public NodeIterator strippingIterator(NodeIterator iterator, short[] mapping,
			     StripWhitespaceFilter filter) {
	return(new StrippingIterator(iterator, mapping, filter));
    }
			     

    /**************************************************************
     * Iterator that assured that a single node is only returned once
     * and that the nodes are returned in document order.
     */	    
    private final class OrderedIterator extends NodeIteratorBase {

	private BitArray  _nodes = null;
	private int       _save = 0;
	private int       _mark = 0;
	private int       _start = 0;
	private int       _node = -1;
	private int       _last = 0;

	public OrderedIterator(NodeIterator source, int node) {
	    _nodes = new BitArray(_treeNodeLimit);
	    source.setStartNode(node);
	    while ((_node = source.next()) != END) {
		if (_start == -1) _start = _node;
		_last = _node;
		_nodes.setBit(_node);
	    }
	    _node = -1;
	}
 
	public int next() {
	    while ((_node < _treeNodeLimit) && (!_nodes.getBit(++_node))) ;
	    if (_node >= _treeNodeLimit) return(END);
	    return returnNode(_node);
	}

	public NodeIterator reset() {
	    _node = _start - 1;
	    return(this);
	}

	public int getLast() {
	    return(_last);
	}
	
	public void setMark() {
	    _save = _node;
	}

	public void gotoMark() {
	    _node = _save;
	}

	public NodeIterator setStartNode(int start) {
	    _start = start;
	    return((NodeIterator)this);
	}

	public boolean isReverse() {
	    return(false);
	}

	public NodeIterator cloneIterator() {
	    return((NodeIterator)this);
	}

    } // end of OrderedIterator

    /**
     *
     */
    public NodeIterator orderNodes(NodeIterator source, int node) {
	return new OrderedIterator(source, node);
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

    /**
     * Returns a node's position amongst other nodes of the same type
     */
    public int getTypedPosition(NodeIterator iterator, int type, int node) {

	// Just return the basic position if no type is specified
	if (type == -1) type = _type[node];
	//if (type == -1) return(iterator.getPosition()); DEAD WRONG!!!!

	int match = 1;
	int curr  = 0;
	iterator.reset();

	while ( ((curr = iterator.next()) != NULL) && (curr != node)) {
	    if (_type[curr] == type) match++;
	}
	return match;	
    }

    /**
     * Returns an iterator's last node of a given type
     */
    public int getTypedLast(NodeIterator iterator, int type, int node) {
	// Just return the basic position if no type is specified
	if (type == -1) return(iterator.getLast());

	int match = 0;
	int curr  = 0;
	iterator.setMark();
	iterator.reset();

	while ((curr = iterator.next()) != NULL) {
	    if (_type[curr] == type) match++;
	}
	iterator.gotoMark();
	return match;	
    }

	
    /**
     * Returns singleton iterator containg the document root
     * Works for them main document (mark == 0)
     */
    public NodeIterator getIterator() {
	return new SingletonIterator(ROOTNODE);
    }

    public int getType(final int node) {
	if (node >= _type.length)
	    return(0);
	else
	    return _type[node];
    }
    
    public int getNamespaceType(final int node) {
	final int type = _type[node];
	if (type >= NTYPES)
	    return(_namespace[type-NTYPES]);
	else
	    return(0); // default namespace
    }

    public short[] getTypeArray() {
	return _type;
    }

    public String getNodeValue(final int node) {
	if (node == NULL) return EMPTYSTRING;
	switch(_type[node]) {
	case ROOT:
	    return getNodeValue(_offsetOrChild[node]);
	case TEXT:
	    return makeStringValue(node);
	default:
	    return node < _treeNodeLimit
		? getElementValue(node) // element string value
		: makeStringValue(node); // attribute value
	}
    }

    private Hashtable setupMapping(String[] namesArray) {
	final int nNames = namesArray.length;
	final Hashtable types = new Hashtable(nNames);
	for (int i = 0; i < nNames; i++) {
	    types.put(namesArray[i], new Integer(i + NTYPES));
	}
	return types;
    }

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
	final int namesLength = names.length;
	final int mappingLength = _namesArray.length + NTYPES;
	final short[] result = new short[mappingLength];

	// primitive types map to themselves
	for (int i = 0; i < NTYPES; i++)
	    result[i] = (short)i;

	// extended types initialized to "beyond caller's types"
	// unknown element or Attr
	for (int i = NTYPES; i < mappingLength; i++) {
	    final String name = _namesArray[i - NTYPES];
	    final int atPos = name.lastIndexOf(':')+1;
	    result[i] = (short)(name.charAt(atPos) == '@'
				? ATTRIBUTE : ELEMENT);
	}

	// actual mapping of caller requested names
	for (int i = 0; i < namesLength; i++) {
	    result[getGeneralizedType(names[i])] = (short)(i + NTYPES);
	}
	    
	return(result);

    }

    /**
     * Get mapping from external element/attribute types to DOM types
     */
    public short[] getReverseMapping(String[] names) {
	final short[] result = new short[names.length + NTYPES];
	// primitive types map to themselves
	for (int i = 0; i < NTYPES; i++) {
	    result[i] = (short)i;
	}
	// caller's types map into appropriate dom types
	for (int i = 0; i < names.length; i++) {
	    result[i + NTYPES] = (short)getGeneralizedType(names[i]);
	    if (result[i + NTYPES] == ELEMENT)
		result[i + NTYPES] = NO_TYPE;
	}
	return(result);
    }

    /**
     * Get mapping from DOM namespace types to external namespace types
     */
    public short[] getNamespaceMapping(String[] namespaces) {
	final int nsLength = namespaces.length;
	final int mappingLength = _nsNamesArray.length;
	final short[] result = new short[mappingLength];

	// Initialize all entries to -1
	for (int i=0; i<mappingLength; i++)
	    result[i] = (-1);

	for (int i=0; i<nsLength; i++) {
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
	final int length = namespaces.length;
	final short[] result = new short[length];

	for (int i=0; i<length; i++) {
	    Integer type = (Integer)_nsIndex.get(namespaces[i]);
	    if (type == null)
		result[i] = -1;
	    else
		result[i] = type.shortValue();
	}

	return(result);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
	out.writeInt(_treeNodeLimit);
	out.writeObject(_type);
	out.writeObject(_parent);
	out.writeObject(_nextSibling);
	out.writeObject(_offsetOrChild);
	out.writeObject(_lengthOrAttr);
	out.writeObject(_text);
	out.writeObject(_namesArray);
	out.writeObject(_whitespace);
	out.flush();
    }

    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	_treeNodeLimit = in.readInt();
	_type          = (short[])in.readObject();
	_parent        = (int[])in.readObject();
	_nextSibling   = (int[])in.readObject();
	_offsetOrChild = (int[])in.readObject();
	_lengthOrAttr  = (int[])in.readObject();
	_text          = (char[])in.readObject();
	_namesArray    = (String[])in.readObject();
	_whitespace    = (BitArray)in.readObject();

	_types         = setupMapping(_namesArray);
    }

    public DOMImpl() {
	this(32*1024);
    }
	
    public DOMImpl(final int size) {
	_type          = new short[size];
	_parent        = new int[size];
	_nextSibling   = new int[size];
	_offsetOrChild = new int[size];
	_lengthOrAttr  = new int[size];
	_text          = new char[size * 10];
	_whitespace    = new BitArray(size);
	// _namesArray will be allocated at endDocument
    }

    public void print(int node, int level) {
	switch(_type[node]) {
	case ROOT:
	    print(_offsetOrChild[node], level);
	    break;

	case TEXT:
	    System.out.print(makeStringValue(node));
	    break;

	case PROCESSING_INSTRUCTION:
	case COMMENT:
	    System.out.println("***PI/CMT***");
	    break;

	default:		// element
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
     * Returns the name of a node type (attribute or element).
     * Look at it again (better way to differenciate attributes).
     */
    /*
    private String namedNodeName(final int type) {
	final String rawName = _namesArray[type - NTYPES];

	return rawName.charAt(0) == '@' ? rawName.substring(1) : rawName;
    }
    */

    /**
     * Returns the name of a node (attribute or element).
     */
    public String getNodeName(final int node) {
	// Get the node type and make sure that it is within limits
	final short type = _type[node];
	if (type == DOM.PROCESSING_INSTRUCTION)
	    return("a-pi");
	else if (type < NTYPES)
	    return EMPTYSTRING;

	// Get node's name (attribute or element)
	final String rawName = _namesArray[type - NTYPES];
	// Make sure attributes are returned without the leading '@'
	if (node < _firstAttributeNode)
	    return(rawName);
	else {
	    final int col = rawName.lastIndexOf(':');
	    if (col < 0)
		return(rawName.substring(1));
	    else
		return(rawName.substring(0,col)+':'+rawName.substring(col+2));
	}
    }

    public String getNamespaceName(final int node) {
	final int type = getNamespaceType(node);
	return(_nsNamesArray[type]);
    }

    private String makeStringValue(final int node) {
	return new String(_text, _offsetOrChild[node], _lengthOrAttr[node]);
    }

    public String getAttributeValue(final int eType,
				    final int node,
				    final short[] reverseMapping) {
	return getAttributeValue(reverseMapping[eType], node);
    }
    
    public String getAttributeValue(final int gType, final int element) {
	for (int attr = _lengthOrAttr[element];
	     attr != NULL;
	     attr = _nextSibling[attr]) {
	    if (_type[attr] == gType) {
		return makeStringValue(attr);
	    }
	}
	return EMPTYSTRING;
    }

    public int getAttributeNode(final int gType, final int element) {
	for (int attr = _lengthOrAttr[element]; attr != NULL;
	     attr = _nextSibling[attr]) {
	    if (_type[attr] == gType) {
		return attr;
	    }
	}
	return NULL;
    }

    // for testing
    public String getAttributeValue(final String name, final int element) {
	return getAttributeValue(getGeneralizedType(name), element);
    }
    
    public boolean hasAttribute(final int gType, final int node) {
	for (int attr = _lengthOrAttr[node]; attr != NULL;
	     attr = _nextSibling[attr]) {
	    if (_type[attr] == gType) {
		return true;
	    }
	}
	return false;
    }

    private boolean hasChildren(final int node) {
	if (node < _treeNodeLimit) {
	    final int type = _type[node];
	    return(((type >= NTYPES) || (type == ROOT)) &&
		   (_offsetOrChild[node] != 0));
	}
	return(false);
    }

    public NodeIterator getChildren(final int node) {
	if (hasChildren(node)) {
	    return(new ChildrenIterator());
	}
	else {
	    return(EMPTYITERATOR);
	}
    }

    // shortcut for a popular one
    public NodeIterator getTypedChildren(final int type) {
	return(new TypedChildrenIterator(type));
    }

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
	    return(new TypedAttributeIterator(getGeneralizedType("xmlns")));
	default:
	    BasisLibrary.runTimeError("Error: iterator for axis '" + 
				      Axis.names[axis] + "' not implemented");
	    System.exit(1);
	}
	return(iterator);
    }

    public NodeIterator getTypedAxisIterator(int axis, int type) {
	NodeIterator iterator = null;

	/* This causes an error when using patterns for elements that
	   do not exist in the DOM (translet types which do not correspond
	   to a DOM type are mapped to the DOM.ELEMENT type).
	*/

	if (type == NO_TYPE) {
	    return(EMPTYITERATOR);
	}
        else if (type == ELEMENT) {
	    iterator = new FilterIterator(getAxisIterator(axis),
					  getElementFilter());
	}
	else
	{
	    switch (axis) {
	    case Axis.SELF:
		iterator = new TypedSingletonIterator(type);
		break;
	    case Axis.CHILD:
		iterator = new TypedChildrenIterator(type);
		break;
	    case Axis.PARENT:
		return(new ParentIterator().setNodeType(type));
	    case Axis.ANCESTOR:
		return(new TypedAncestorIterator(type));
	    case Axis.ANCESTORORSELF:
		return((new TypedAncestorIterator(type)).includeSelf());
	    case Axis.ATTRIBUTE:
		return(new TypedAttributeIterator(type));
	    case Axis.DESCENDANT:
		iterator = new TypedDescendantIterator(type);
		break;
	    case Axis.DESCENDANTORSELF:
		iterator = (new TypedDescendantIterator(type)).includeSelf();
		break;
	    case Axis.FOLLOWING:
		iterator = new TypedFollowingIterator(type);
		break;
	    case Axis.PRECEDING:
		iterator = new TypedPrecedingIterator(type);
		break;
	    case Axis.FOLLOWINGSIBLING:
		iterator = new TypedFollowingSiblingIterator(type);
		break;
	    case Axis.PRECEDINGSIBLING:
		iterator = new TypedPrecedingSiblingIterator(type);
		break;
	    default:
		BasisLibrary.runTimeError("Error: typed iterator for axis " + 
					  Axis.names[axis]+"not implemented");
	    }
	}
	return(iterator);
    }

    public NodeIterator getNamespaceAxisIterator(int axis, int ns) {

	NodeIterator iterator = null;

	if (ns == NO_TYPE) {
	    return(EMPTYITERATOR);
	}
	else {
	    switch (axis) {
	    case Axis.CHILD:
		iterator = new NamespaceChildrenIterator(ns);
		break;
	    case Axis.ATTRIBUTE:
		iterator = new NamespaceAttributeIterator(ns);
		break;
	    default:
		BasisLibrary.runTimeError("Error: typed iterator for axis " + 
					  Axis.names[axis]+"not implemented");
	    }
	}
	return(iterator);
    }

    public NodeIterator getTypedDescendantIterator(int type) {
	NodeIterator iterator = new TypedDescendantIterator(type);
	iterator.setStartNode(1);
	return(iterator);
    }    

    public NodeIterator getNthDescendant(int node, int n) {
	return (new NthDescendantIterator(n));
    }

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
     * Copy a single node to an output handler
     */
    public void copy(final int node, TransletOutputHandler handler)
	throws TransletException {

	if (node >= _treeNodeLimit) {
	    return;
	}

	switch(_type[node]) {
	case ROOT:
	    for (int child = _offsetOrChild[node];
		 child != NULL;
		 child = _nextSibling[child]) {
		copy(child, handler);
	    }
	    break;

	case PROCESSING_INSTRUCTION:
	case COMMENT:
	    //!!! TODO
	    break;

	case TEXT:
	    handler.characters(_text,
			       _offsetOrChild[node],
			       _lengthOrAttr[node]);
	    break;

	default:
	    if (isElement(node)) {
		final String name = getNodeName(node);
		// Copy element name - start tag
		int col = name.lastIndexOf(':');
		if (col > 0) {
		    final String prefix = generateNamespacePrefix();
		    handler.startElement(prefix+':'+name.substring(col+1));
		    handler.attribute("xmlns:"+prefix, name.substring(0,col));
		}
		else {
		    handler.startElement(name);
		}

		// Copy element attribute
		for (int attr = _lengthOrAttr[node];
		     attr != NULL;
		     attr = _nextSibling[attr]) {
		    final String aname = getNodeName(attr);
		    col = aname.lastIndexOf(':');
		    if (col < 0) {
			handler.attribute(aname,makeStringValue(attr));
		    }
		    else {
			final String prefix = generateNamespacePrefix();
			handler.attribute("xmlns:"+prefix,
					  aname.substring(0,col));
			handler.attribute(prefix+':'+aname.substring(col+1),
					  makeStringValue(attr));
		    }
		}
		// Copy element namespace declarations ???
		

		// Copy element children
		for (int child = _offsetOrChild[node];
		     child != NULL;
		     child = _nextSibling[child]) {
		    copy(child, handler);
		}
		handler.endElement(name);
	    }
	    else {
		System.err.println("NYI: non element in copy");
	    }
	    break;
	}
    }

    private void copyPI(final int node, TransletOutputHandler handler)
	throws TransletException {
	final char[] text = _text;
	final int start = _offsetOrChild[node];
	final int length = _lengthOrAttr[node];
	int i = start + 1;
	while (text[i] != ' ') {
	    ++i;
	}
	handler.processingInstruction(new String(text, start, i - start),
				      new String(text, i + 1, length - i));
    }

    public String shallowCopy(final int node, TransletOutputHandler handler)
	throws TransletException {
	switch(_type[node]) {
	case ROOT:
	    // do nothing
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
	    // !!! implement; not in the tree currently
	    return null;
	    
	default:		// element or attribute
	    final String name = getNodeName(node);
	    if (node < _treeNodeLimit) { // element
		handler.startElement(name);
		return name;
	    }
	    else {		// attribute
		handler.attribute(name, makeStringValue(node));
		return null;
	    }
	}
    }
	
    public String getStringValue() {
	return getElementValue(ROOTNODE);
    }

    public String getElementValue(final int element) {
	// optimization: only create StringBuffer if > 1 child
	final int child = _offsetOrChild[element];
	return child != NULL
	    ? (_type[child] == TEXT && _nextSibling[child] == NULL
	       ? makeStringValue(child)
	       : stringValueAux(new StringBuffer(), element).toString())
	    : EMPTYSTRING;
    }

    private StringBuffer stringValueAux(StringBuffer buffer, final int element) {
	for (int child = _offsetOrChild[element];
	     child != NULL;
	     child = _nextSibling[child]) {
	    switch (_type[child]) {
	    case TEXT:
		/* WHITESPACE
		if (_filter != null) {
		    if ((_whitespace.getBit(child)) &&
			(_filter.stripSpace((DOM)this, child, 
					    _type[_parent[child]]))) {
			break;
		    }
		}
		*/
		buffer.append(_text,
			      _offsetOrChild[child],
			      _lengthOrAttr[child]);
		break;
		
	    case PROCESSING_INSTRUCTION:
	    case COMMENT:
		break;
				
		// !!! at the moment default can only be an element???
	    default:
		stringValueAux(buffer, child);
	    }
	}
	return buffer;
    }

    /**
     * Returns the defined language for a node (if any)
     */
    public String getLanguage(int node) {
	final Integer langType = (Integer)_types.get("xml:@lang");
	if (langType != null) {
	    final int type = langType.intValue();
	    while (node != DOM.NULL) {
		int attr = _lengthOrAttr[node];
		while (attr != DOM.NULL) {
		    if (_type[attr] == type) {
			return(getNodeValue(attr));
		    }
		    attr = _nextSibling[attr];
		}
		node = getParent(node);
	    }
	}
	return(null);
    }

    public HandlerBase getBuilder() {
	return new DOMBuilder();
    }

    public TransletOutputHandler getOutputDomBuilder() {
	return new SAXAdapter(getBuilder());
    }

    /**
     * Returns true if a character is an XML whitespace character
     */
    private static final boolean isWhitespaceChar(char c) {
	return c == 0x20 || c == 0x0A || c == 0x0D || c == 0x09;
    }


    /**************************************************************
     * DOM builder class definition
     */
    private final class DOMBuilder extends HandlerBase {

	private final static int REUSABLE_TEXT_SIZE = 32;
	private Hashtable _shortTexts           = null;

	private Hashtable _names                = null;
	private int       _nextNameCode         = NTYPES;
	private int[]     _parentStack          = new int[64];
	private int[]     _previousSiblingStack = new int[64];
	private int       _sp;
	private int       _baseOffset           = 0;
	private int       _currentOffset        = 0;
	private int       _currentNode          = 0;

	// attribute node stuff
	private int       _currentAttributeNode = 0;
	private short[]  _type2        = new short[32];
	private int[]    _parent2      = new int[32];
	private int[]    _nextSibling2 = new int[32];
	private int[]    _offset       = new int[32];
	private int[]    _length       = new int[32];

	// namespace node stuff
	private int       _nsCount      = 0;
	private int       _nsLimit      = 32;
	private String[]  _nsNames      = new String[_nsLimit];
	private Hashtable _nsPrefixes   = new Hashtable();

	NSDeclStack _nsDeclarations = new NSDeclStack(64);
	
	// Stack used to keep track of what whitespace text nodes are protected
	// by xml:space="preserve" attributes and which nodes that are not.
	private int[]   _xmlSpaceStack = new int[64];
	private int     _idx = 1;
	private boolean _preserve = false;

	private static final String XML_STRING = "xml";
	private static final String XMLNS_STRING = "xmlns";
	private static final String XMLSPACE_STRING = "xmlns";
	private static final String PRESERVE_STRING = "preserve";

	/**************************************************************
	 * Class containing a stack that is used to keep track of the varying
	 * namespace URIs (types) behind a namespace prefix.
	 */
	private final class NSNodeStack {

	    private final static int DEFAULT_SIZE = 8;

	    private int[] _data;
	    private int _size;
	    private int _sp = 0;

	    // Constructor
	    NSNodeStack(int size) {
		_size = size;
		_data = new int[size];
	    }

	    // Default constructor
	    NSNodeStack() {
		this(DEFAULT_SIZE);
	    }

	    // Push a namespace type the prefix will refer to for now
	    void push(int nsIndex) {
		if (_sp == _size) {
		    _size = _size << 1;
		    final int[] newData = new int[_size];
		    System.arraycopy(_data,0,newData,0,_sp);
		    _data = newData;
		}
		_data[_sp++] = nsIndex;
	    }
	    
	    // Pop the current namespace type for this prefix
	    int pop() {
		if (_sp != 0)
		    return(_data[--_sp]);
		else
		    return(-1);
	    }

	    // Get the current namespace type for this prefix
	    int get() {
		if (_sp != 0)
		    return(_data[_sp-1]);
		else
		    return(-1);
	    }
	}


	/**************************************************************
	 * Class containg a stack that is used for keeping track of what nodes
	 * contained the latest namespace declarations.
	 */
	private final class NSDeclStack {

	    private final static int DEFAULT_SIZE = 64;

	    private int _sp = 0;
	    private int _size = 0;
	    private NSNodeStack[] _stacks = null;
	    private int[] _nodes = null;

	    // Constructor
	    NSDeclStack(int size) {
		_size = size;
		_stacks = new NSNodeStack[size];
		_nodes = new int[size];
	    }

	    NSDeclStack() {
		this(DEFAULT_SIZE);
	    }

	    // Push a NS node stack with the node where the decl. was found.
	    void push(int node, NSNodeStack stack) {
		// time to resize arrays?
		if (_sp == _size) {
		    _size = _size << 1;
		    final NSNodeStack[] newStacks = new NSNodeStack[_size];
		    final int[] newNodes = new int[_size];
		    System.arraycopy(_stacks,0,newStacks,0,_sp);
		    System.arraycopy(_nodes,0,newNodes,0,_sp);
		    _stacks = newStacks;
		    _nodes = newNodes;
		}

		// now push the new namespace node
		_nodes[_sp] = node;
		_stacks[_sp] = stack;
		_sp++;
	    }
	    
	    // Pop all namespace declarations for a given node (if any).
	    void pop(int node) {
		while ((_sp != 0) && (_nodes[_sp-1] == node)) {
		    _sp--;
		    _stacks[_sp].pop();
		    _stacks[_sp] = null;
		}
	    }
	}

	/**
	 * Add a new namespace URI to the _namespaceNames table
	 */
	private int addNamespaceURI(String uri) {
	    // Check if we already have this URI
	    Integer index = (Integer)_nsIndex.get(uri);
	    // Add new namespace type if we do not have this one.
	    if (index == null) {
		// Resize namespace array if necessary
		if (_nsCount == _nsLimit) {
		    _nsLimit = _nsLimit << 1;
		    final String[] newData = new String[_nsLimit];
		    System.arraycopy(_nsNames,0,newData,0,_nsCount);
		    _nsNames = newData;
		}
		index = new Integer(_nsCount);
		_nsIndex.put(uri,index);
		_nsNames[_nsCount++] = uri;
	    }
	    return(index.intValue());
	}

	/**
	 * Add a new prefix for a namespace type
	 */
	private void addNamespacePrefix(int node, String prefix, int type) {
	    NSNodeStack stack = (NSNodeStack)_nsPrefixes.get(prefix);
	    if (stack == null) { // add new namespace prefix in table
		stack = new NSNodeStack(8); // what size ? dynamic ?
		_nsPrefixes.put(prefix,stack);
	    }
	    stack.push(type);
	    _nsDeclarations.push(node,stack);
	}

	/**
	 * Add a new namespace - prefix and uri
	 */
	private int addNamespace(int node, String prefix, String uri) {
	    final int type = addNamespaceURI(uri);
	    addNamespacePrefix(node,prefix,type);
	    return(type);
	}

	/**
	 * Get the namespace type from a namespace prefix
	 */
	private int getNamespaceType(String prefix) {
	    // Get the stack for this prefix.
	    final NSNodeStack stack = (NSNodeStack)_nsPrefixes.get(prefix);
	    if (stack != null)
		return(stack.get()); // return namespace type id
	    else
		return(0xffffffff);  //undeclared prefix - should not occur
	}

	/**
	 * Get the namespace URI from a namespace type
	 */
	private String getNamespaceURI(int nsType) {
	    if (nsType < _nsCount)
		return(_nsNames[nsType]);
	    else
		return(null);
	}

	/**
	 * Get the namespace URI from a namespace prefix
	 */
	private String getNamespaceURI(String prefix) {
	    return(getNamespaceURI(getNamespaceType(prefix)));
	}

	/**
	 * Create internal name for an element node
	 */
	private short internElement(String name) throws SAXException {
	    int i;
	    
	    // Expand prefix:localname to full QName
	    if ((i = name.lastIndexOf(':')) != -1) {
		name = getNamespaceURI(name.substring(0,i))+name.substring(i);
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
	 * Create internal name for an attribute node
	 */
	private short internAttribute(String name, boolean xmlAttr)
	    throws SAXException {

	    // Leave XML attributes as the are
	    if (!xmlAttr) {
		int col;
		// Expand prefix:localname to full QName
		if ((col = name.lastIndexOf(':')) != -1) {
		    name = getNamespaceURI(name.substring(0,col))+
			':'+'@'+name.substring(col+1);
		}
		// Append default namespace with the name has no prefix
		else {
		    // Attributes with no prefix belong in the "" namespace,
		    // and not in the namespace that the "" prefix points to,
		    // so the attribute name remains the way it is.
		    name = '@'+name;
		}
	    }
	    else {
		name = name.substring(0,4)+'@'+name.substring(4);
	    }

	    // Stuff the QName into the names vector & hashtable
	    Integer obj = (Integer)_names.get(name);
	    if (obj == null) {
		_names.put(name, obj = new Integer(_nextNameCode++));
	    }

	    return (short)obj.intValue();
	}

	/**
	 * Default constructor for the DOMBuiler class
	 */
	public DOMBuilder() {
	    _xmlSpaceStack[0] = DOM.ROOTNODE;
	}
	
	/**
	 * Call this when an xml:space attribute is encountered
	 */
	public void xmlSpaceDefine(String val, final int node) {
	    final boolean setting = val.equals(PRESERVE_STRING);
	    if (setting != _preserve) {
		_xmlSpaceStack[_idx++] = node;
		_preserve = setting;
	    }
	}

	/**
	 * Call this from endElement() to revert strip/preserve setting
	 */
	public void xmlSpaceRevert(final int node) {
	    if (node == _xmlSpaceStack[_idx - 1]) {
		_idx--;
		_preserve = !_preserve;
	    }
	}
	
	/**
	 *
	 */
	private int nextNode() {
	    final int index = _currentNode++;
	    if (index == _type.length) {
		resizeArrays(_type.length * 2, index);
	    }
	    return index;
	}

	/**
	 *
	 */
	private int nextAttributeNode() {
	    final int index = _currentAttributeNode++;
	    if (index == _type2.length) {
		resizeArrays2(_type2.length * 2, index);
	    }
	    return index;
	}

	/**
	 *
	 */
	private void resizeTextArray(final int newSize) {
	    final char[] newText = new char[newSize];
	    System.arraycopy(_text, 0, newText, 0, _currentOffset);
	    _text = newText;
	}
	
	/**
	 *
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
	 * Creates a text-node and checks if it is a whitespace node.
	 */
	private void maybeCreateTextNode() {
	    if (_currentOffset > _baseOffset) {
		final int node = nextNode();
		final int limit = _currentOffset;
		// Check if whitespace unless protected by xml:space="preserve"
		if (!_preserve) {
		    // Check if this text node is purely whitespace
		    int i = _baseOffset;
		    while (isWhitespaceChar(_text[i++]) && i < limit) ;
		    if ((i == limit) && isWhitespaceChar(_text[i-1]))
			_whitespace.setBit(node);
		}
		_type[node] = TEXT;
		linkChildren(node);
		storeTextRef(node);
	    }
	}

	/*
	 * returns offset new or existing
	 * updates _baseOffset, _currentOffset state
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
	 *
	 */
	private void storeTextRef(final int node) {
	    final int length = _currentOffset - _baseOffset;
	    _offsetOrChild[node] = maybeReuseText(length);
	    _lengthOrAttr[node]  = length;
	}
	
	/**
	 *
	 */
	private void storeAttrValRef(final int attributeNode) {
	    final int length = _currentOffset - _baseOffset;
	    _offset[attributeNode] = maybeReuseText(length);
	    _length[attributeNode] = length;
	}
	
	/**
	 * Part of SAX handler interface - initializes the DOM builder
	 */
	public void startDocument() {
	    _shortTexts     = new Hashtable();
	    _names          = new Hashtable();
	    _sp             = 0;
	    _parentStack[0] = ROOTNODE;	// root
	    _currentNode    = ROOTNODE + 1;
	    _currentAttributeNode = 0;
	    addNamespace(0,EMPTYSTRING,EMPTYSTRING); // default namespace
	}

	/**
	 * Part of SAX handler interfaces - finalizes the DOM
	 */
	public void endDocument() {

	    maybeCreateTextNode();
	    _shortTexts = null;
	    final int namesSize = _nextNameCode - NTYPES;

	    // Fill the names array
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
	    _treeNodeLimit = _currentNode;

	    // Fill the _namespace[] array
	    _namespace = new short[namesSize];
	    for (int i=0; i<namesSize; i++) {
		int col = _namesArray[i].lastIndexOf(':');
		if (col == -1) { // default namespace
		    _namespace[i] = 0;
		}
		else {           // other namespaces
		    _namespace[i] =
			(short)addNamespaceURI(_namesArray[i].substring(0,col));
		}
	    }

	    // Fill the _nsNamesArray[] array
	    _nsNamesArray = new String[_nsCount];
	    System.arraycopy(_nsNames,0,_nsNamesArray,0,_nsCount);
	    _nsNames = null;
	}
	
	/**
	 * Part of SAX handler interface
	 */
	public void startElement(String elementName, AttributeList attList)
	    throws SAXException {

	    maybeCreateTextNode();
	    final int node = nextNode();
	    linkChildren(node);
	    _parentStack[++_sp] = node;

	    // process attribute list - including namespace declarations
	    final int length = attList.getLength();
	    if (length > 0) {
		int i = 1, attrNode = makeAttributeNode(node, attList, 0);
		while (attrNode == -1 && i < length) {
		    attrNode = makeAttributeNode(node, attList, i++);
		}
		if (attrNode != -1) {
		    _lengthOrAttr[node] = attrNode;	// first attr
		    while (i < length) {
			final int attrNode2 =
			    makeAttributeNode(node, attList, i++);
			if (attrNode2 != -1) {
			    _nextSibling2[attrNode] = attrNode2;
			    attrNode = attrNode2;
			}
		    }
		}
	    }
	    _type[node] = internElement(elementName);
	}

	/**
	 * Part of SAX handler interface
	 */
	private int makeAttributeNode(int parent, AttributeList attList, int i)
	    throws SAXException {
	    final String name = attList.getName(i);
	    boolean xmlAttr = false;

	    // process namespace declaration
	    if (name.startsWith(XML_STRING)) {
		if (name.startsWith(XMLNS_STRING)) {
		    // declaring the default namespace ?
		    if (name.length() == 5)
			addNamespace(parent, EMPTYSTRING, attList.getValue(i));
		    // declaring some other namespace
		    else
			addNamespace(parent, name.substring(6),
				     attList.getValue(i));
		    return -1;
		}
		else if (name.startsWith(XMLSPACE_STRING)) {
		    xmlSpaceDefine(attList.getValue(i), parent);
		}
		xmlAttr = true;
	    }

	    // fall through to handle a regular attribute
    	    final int node = nextAttributeNode();
	    _type2[node] = internAttribute(name,xmlAttr);
	    _parent2[node] = parent;
	    characters(attList.getValue(i));
	    storeAttrValRef(node);
	    return node;
	}
	
	/**
	 * Part of SAX handler interface
	 */
	public void endElement(String elementName) {
	    maybeCreateTextNode();
	    // Revert to strip/preserve-space setting from before this element
	    xmlSpaceRevert(_parentStack[_sp]);
	    // Pop all namespace declarations found in this element
	    _nsDeclarations.pop(_parentStack[_sp]);
	    _previousSiblingStack[_sp--] = 0;
	}

	/**
	 * Part of SAX handler interface
	 */
	public void processingInstruction(String target, String data) {
	    maybeCreateTextNode();
	    final int node = nextNode();
	    _type[node] = PROCESSING_INSTRUCTION;
	    linkChildren(node);
	    characters(target + ' ' + data);
	    storeTextRef(node);
	}
		
	/**
	 * Part of SAX handler interface - NO, THIS IS NOT SAX, THIS IS SHITE!!
	 */
	public void characters(final String string) {
	    final int length = string.length();
	    if (_currentOffset + length > _text.length) {
		resizeTextArray(_text.length * 2);
	    }
	    string.getChars(0, length, _text, _currentOffset);
	    _currentOffset += length;
	}

	/**
	 * can accumulate text from consecutive calls
	 */
	public void characters(char[] ch, int start, int length) {
	    if (_currentOffset + length > _text.length) {
		resizeTextArray(_text.length * 2);
	    }
	    System.arraycopy(ch, start, _text, _currentOffset, length);
	    _currentOffset += length;
	    maybeCreateTextNode();
	}
	
	private void resizeArrays(final int newSize, final int length) {
	    final short[] newType = new short[newSize];
	    System.arraycopy(_type, 0, newType, 0, length);
	    _type = newType;
	    final int[] newParent = new int[newSize];
	    System.arraycopy(_parent, 0, newParent, 0, length);
	    _parent = newParent;
	    final int[] newNextSibling = new int[newSize];
	    System.arraycopy(_nextSibling, 0, newNextSibling, 0, length);
	    _nextSibling = newNextSibling;
	    final int[] newOffsetOrChild = new int[newSize];
	    System.arraycopy(_offsetOrChild, 0, newOffsetOrChild, 0, length);
	    _offsetOrChild = newOffsetOrChild;
	    final int[] newLengthOrAttr = new int[newSize];
	    System.arraycopy(_lengthOrAttr, 0, newLengthOrAttr, 0, length);
	    _lengthOrAttr = newLengthOrAttr;
	    _whitespace.resize(newSize);
	}
	
	private void resizeArrays2(final int newSize, final int length) {
	    final short[] newType = new short[newSize];
	    System.arraycopy(_type2, 0, newType, 0, length);
	    _type2 = newType;
	    final int[] newParent = new int[newSize];
	    System.arraycopy(_parent2, 0, newParent, 0, length);
	    _parent2 = newParent;
	    final int[] newNextSibling = new int[newSize];
	    System.arraycopy(_nextSibling2, 0, newNextSibling, 0, length);
	    _nextSibling2 = newNextSibling;
	    final int[] newOffset = new int[newSize];
	    System.arraycopy(_offset, 0, newOffset, 0, length);
	    _offset = newOffset;
	    final int[] newLength = new int[newSize];
	    System.arraycopy(_length, 0, newLength, 0, length);
	    _length = newLength;
	}
	
	private void shiftAttributes(final int shift) {
	    final int limit = _currentAttributeNode;
	    int lastParent = -1;
	    for (int i = 0; i < limit; i++) {
		if (_parent2[i] != lastParent) {
		    lastParent = _parent2[i];
		    _lengthOrAttr[lastParent] = i + shift;
		}
		final int next = _nextSibling2[i];
		_nextSibling2[i] = next != 0 ? next + shift : 0;
	    }
	}
	
	private void appendAttributes() {
	    final int len = _currentAttributeNode;
	    if (len > 0) {
		final int dst = _currentNode;
		System.arraycopy(_type2,         0, _type,          dst, len);
		System.arraycopy(_parent2,       0, _parent,        dst, len);
		System.arraycopy(_nextSibling2,  0, _nextSibling,   dst, len);
		System.arraycopy(_offset,        0, _offsetOrChild, dst, len);
		System.arraycopy(_length,        0, _lengthOrAttr,  dst, len);
	    }
	}
    } // end of DOMBuilder
}
