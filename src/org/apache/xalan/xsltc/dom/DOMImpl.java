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

import org.apache.xml.dtm.ref.*;
import org.apache.xml.dtm.*;
import org.apache.xml.dtm.ref.dom2dtm.DOM2DTM;
import org.apache.xml.utils.XMLStringFactory;

import javax.xml.transform.dom.DOMSource;

public final class DOMImpl extends DOM2DTM implements DOM, Externalizable
{

    // empty String for null attribute values
    private final static String EMPTYSTRING = "";

    // empty iterator to be returned when there are no children
    private final static DTMAxisIterator EMPTYITERATOR =
                                               new DTMAxisIteratorBase() {
	    public DTMAxisIterator reset() { return this; }
	    public DTMAxisIterator setStartNode(int node) { return this; }
	    public int next() { return DTM.NULL; }
	    public void setMark() {}
	    public void gotoMark() {}
	    public int getLast() { return 0; }
	    public int getPosition() { return 0; }

	    public DTMAxisIterator cloneIterator() { return this; }
	    public void setRestartable(boolean isRestartable) { }

	};

    // Contains the number of nodes and attribute nodes in the tree
    private int       _treeNodeLimit;
    private int       _firstAttributeNode;

    // Node-to-type, type-to-name, and name-to-type mappings
    private int[] _types;
    private String[]  _namesArray;
    private Hashtable _names;
    private int       _namesCount = 0;

    // Tree navigation arrays
    private int[]     _offsetOrChild; // Serves two purposes !!!
    private int[]     _lengthOrAttr;  // Serves two purposes !!!

    // Holds contents of text/comment nodes and attribute values
    private char[]    _text;

    // Namespace related stuff
    private String[]  _uriArray;
    private Hashtable _prefixArray;
    private short[]   _namespace;
    private Hashtable _namespaceHash;
    private Hashtable _nsIndex = new Hashtable();
    private int       _URICount = 0;

    // Tracks which textnodes are whitespaces and which are not
    private BitArray  _whitespace; // takes xml:space into acc.
    // Tracks which bits in _whitespace are valid
    private BitArray _checkedForWhitespace;

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

    private static final String XML_PREFIX   = "xml";

    /**
     * %HZ% %REVISIT% Need javadoc
     */
    public void createMappings() {
        DTMAxisIterator rootIterator = getIterator();
        DTMAxisIterator docIter =
                 new org.apache.xml.dtm.ref.DTMDefaultBaseIterators
                                            .DescendantIterator();
        docIter.setStartNode(rootIterator.next());

        _names = new Hashtable();
        _namespaceHash = new Hashtable();
        _prefixArray = new Hashtable();

        Integer eType = new Integer(getExpandedTypeID(EMPTYSTRING, EMPTYSTRING,
                                                      DTM.NAMESPACE_NODE));
        _nsIndex.put(eType, new Integer(_URICount++));
  
        eType = new Integer(getExpandedTypeID(XML_PREFIX,
                                     "http://www.w3.org/XML/1998/namespace",
                                     DTM.NAMESPACE_NODE));
        _prefixArray.put(XML_PREFIX, "http://www.w3.org/XML/1998/namespace");
        _nsIndex.put(eType, new Integer(_URICount++));

        for (int node = docIter.next();
             node != DTM.NULL;
             node = docIter.next()) {
            if (getNodeType(node) == DTM.ELEMENT_NODE) {
                addNodeToMappings(node, false);

                for (int anode = getFirstAttribute(node);
                     anode != DTM.NULL;
                     anode = getNextAttribute(anode)) {
                    addNodeToMappings(anode, true);
                }
            }
        }

        String [] URIArray = new String[_URICount];
        String [] namesArray = new String[_namesCount];
        short [] namespace = new short[_namesCount];

        Enumeration names = _names.keys();
        while (names.hasMoreElements()) {
            final String name = (String)names.nextElement();
            final Integer Idx = (Integer)_names.get(name);
            final String uri = (String)_namespaceHash.get(Idx);
            final int idx = Idx.intValue();

            namesArray[idx] = name;
            if (uri != null) {
                final String prefix = (String)_prefixArray.get(uri);
                eType = new Integer(getExpandedTypeID(uri, prefix,
                                                      DTM.NAMESPACE_NODE));
                final short URIIdx = ((Integer)_nsIndex.get(eType))
                                                       .shortValue();
                namespace[idx] = URIIdx;
                URIArray[URIIdx] = uri;
            }
        }
        _namespace = namespace;
        _namesArray = namesArray;
        _uriArray = URIArray;

        _names = null;
        _namespaceHash = null;

        _types = setupMapping(namesArray);
    }


    /**
     * %HZ% %REVISIT% Need javadoc
     */
    private void addNodeToMappings(int node, boolean isAttrNode) {
        String name = getLocalName(node);
        final String prefix = getPrefix(node);
        final String uri = getNamespaceName(node);
        name = (uri.length() == 0) ? (isAttrNode ? ('@' + name) : name)
                                   : (isAttrNode ? (uri + ":@" + name)
                                                 : (uri + ':' + name));

        final boolean hasNamespace = (uri.length() != 0 &&
                                      !prefix.equals(XML_PREFIX));
        if (hasNamespace) {
            Integer eType = new Integer(getExpandedTypeID(uri, prefix,
                                                          DTM.NAMESPACE_NODE));
            if ((Integer)_nsIndex.get(eType) == null) {
                _prefixArray.put(uri, prefix);
                _nsIndex.put(eType, new Integer(_URICount++));
            }
        }

        if (_names.get(name) == null) {
            final Integer nameIdx = new Integer(_namesCount++);
            _names.put(name, nameIdx);

            if (hasNamespace) {
                _namespaceHash.put(nameIdx, uri);
            }
        }
    }

    /**
     * Define the origin of the document from which the tree was built
     */
    public void setDocumentURI(String uri) 
    {
        setDocumentBaseURI(uri);
        _documentURI = uri;
    }

    /**
     * Returns the origin of the document from which the tree was built
     */
    public String getDocumentURI() {
      String baseURI = getDocumentBaseURI();
      return (baseURI != null) ? baseURI : "rtf" + _documentURIIndex++;
    }

    public String getDocumentURI(int node) 
    {
      return getDocumentURI();
    }

    public void setupMapping(String[] names, String[] namespaces) 
    {
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
	final org.apache.xml.dtm.ref.DTMDefaultBaseIterators.AncestorIterator
              ancestors =
                 new org.apache.xml.dtm.ref.DTMDefaultBaseIterators
                                                .AncestorIterator();
	
	if (isElement(node)) {
	    ancestors.includeSelf();
	}

	ancestors.setStartNode(node);
	while ((anode = ancestors.next()) != DTM.NULL) {
            final org.apache.xml.dtm.ref.DTMDefaultBaseIterators
                     .NamespaceIterator namespaces =
                             new org.apache.xml.dtm.ref.DTMDefaultBaseIterators
                                                       .NamespaceIterator();

            namespaces.setStartNode(anode);
	    while ((nsnode = namespaces.next()) != DTM.NULL) {
		if (getLocalName(nsnode).equals(prefix)) {
		    return getNodeValue(nsnode);
		}
	    }
	}

	// TODO: Internationalization?
	throw new TransletException("Namespace prefix '" + prefix +
                                 "' is undeclared.");
    }

    /**
     * Returns 'true' if a specific node is an element (of any type)
     */
    public boolean isElement(final int node) 
    {
      return (((node < _firstAttributeNode) && (getType(node) >= DTM.NTYPES))
              || getNodeType(node) == DTM.ELEMENT_NODE);
    }

    /**
     * Returns 'true' if a specific node is an element (of any type)
     */
    public boolean isAttribute(final int node) 
    {
      return ((node >= _firstAttributeNode) && (getType(node) >= DTM.NTYPES));
    }

    /**
     * Returns the number of nodes in the tree (used for indexing)
     */
    public int getSize() 
    {
      return getNumberOfNodes();
    }

    /**
     * Part of the DOM interface - no function here.
     */
    public void setFilter(StripFilter filter) { }


    /**
     * Returns true if node1 comes before node2 in document order
     */
    public boolean lessThan(int node1, int node2) {
      if (node1 == DTM.NULL)
          return false;
      if (node2 == DTM.NULL) 
          return true;
    	
      // Hack for ordering attribute nodes
      if (getNodeType(node1) == DTM.ATTRIBUTE_NODE) {
          node1 = getParent(node1);
      }
      if (getNodeType(node2) == DTM.ATTRIBUTE_NODE) {
          node2 = getParent(node2);
      }

      return (node1 < node2);
    }

    /**
     * Create an org.w3c.dom.Node from a node in the tree
     */
    public Node makeNode(int index) {
	if (_nodes == null) {
            _nodes = new Node[_namesArray.length + DTM.NTYPES];
	}
	return _nodes[index] != null ? _nodes[index]
                                     : (_nodes[index]
                                            = new DTMNodeProxy((DTM)this,
                                                               index));
    }

    /**
     * Create an org.w3c.dom.Node from a node in an iterator
     * The iterator most be started before this method is called
     */
    public Node makeNode(DTMAxisIterator iter) 
    {
	    return makeNode(iter.next());
    }

    /**
     * Create an org.w3c.dom.NodeList from a node in the tree
     */
    public NodeList makeNodeList(int index) {
	if (_nodeLists == null) {
            _nodeLists = new NodeList[_namesArray.length + DTM.NTYPES];
	}
      try
      {        
// %HZ%:  Should we create an org.apache.xpath.axes.SingletonIterator class?
      return _nodeLists[index] != null
               ? _nodeLists[index]
               : (_nodeLists[index] = new DTMNodeList(new org.apache.xpath.axes.SingletonIterator(index)));
      }
      catch (javax.xml.transform.TransformerException te)
      {return null;}
    }

    /**
     * Create an org.w3c.dom.NodeList from a node iterator
     * The iterator most be started before this method is called
     */
    public NodeList makeNodeList(DTMIterator iter) {
        return new DTMNodeList(iter);
    }

    /**
     * Create an empty org.w3c.dom.NodeList
     */
    private NodeList getEmptyNodeList() {
      return EmptyNodeList != null
             ? EmptyNodeList
               : (EmptyNodeList = new DTMNodeList(null));
    }

    /**
     * Create an empty org.w3c.dom.NamedNodeMap
     */
    private NamedNodeMap getEmptyNamedNodeMap() {
      return EmptyNamedNodeMap != null
             ? EmptyNamedNodeMap
               : (EmptyNamedNodeMap = new DTMNamedNodeMap(this, DTM.NULL));
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

    // A single copy (cache) of ElementFilter
    private DTMFilter _elementFilter;

    /**
     * Returns a filter that lets only element nodes through
     */
    private DTMFilter getElementFilter() 
    {
      if (_elementFilter == null) 
      {
        _elementFilter = new DTMFilter() {
          public short acceptNode(int node, int whatToShow) 
          {
            short nodeType = getNodeType(node);
            int nodebit = DTMFilter.SHOW_ELEMENT & (0x00000001 << (nodeType -1)) ;
            return nodebit ==0 ? DTMIterator.FILTER_REJECT : DTMIterator.FILTER_ACCEPT;//isElement(node);
          }
          public short acceptNode(int node, int whatToShow, int expandedName) 
          {
            short nodeType = getNodeType(node);
            int nodebit = DTMFilter.SHOW_ELEMENT & (0x00000001 << (nodeType -1)) ;
            return nodebit ==0 ? DTMIterator.FILTER_REJECT : DTMIterator.FILTER_ACCEPT;//return isElement(node);
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
	    return getType(node) == _nodeType;
	}
    }

    /**
     * Returns a node type filter (implementation of Filter)
     */
    public Filter getTypeFilter(int type) {
	return new TypeFilter(type);
    }


    /**************************************************************
     * Iterator that returns namespace nodes
     */
    
    private final class TypedNamespaceIterator extends NamespaceIterator {

	/** The extended type ID that was requested. */
    private final int _nodeType;

    /**
     * Constructor TypedChildrenIterator
     *
     *
     * @param nodeType The extended type ID being requested.
     */
	public TypedNamespaceIterator(int nodeType)
    {
      super();
      _nodeType = nodeType;
    }

	/**
     * Get the next node in the iteration.
     *
     * @return The next node handle in the iteration, or END.
     */
    public int next()
    {
    	int node; 

      for (node = super.next(); node != END; node = super.next())
      {
      	if (getExpandedTypeID(node) == _nodeType
             || getNodeType(node) == _nodeType
             || getNamespaceType(node) == _nodeType)
        {
          return returnNode(node);
        }
      }

      return (END);
    }
  }  // end of TypedNamespaceIterator


    /**************************************************************
     * Iterator to put on top of other iterators. It will take the
     * nodes from the underlaying iterator and return all but
     * whitespace text nodes. The iterator needs to be a supplied
     * with a filter that tells it what nodes are WS text.
     */             
    private final class StrippingIterator extends InternalAxisIteratorBase {

        private static final int USE_PREDICATE  = 0;
        private static final int STRIP_SPACE    = 1;
        private static final int PRESERVE_SPACE = 2;

        private StripFilter _filter = null;
        private short[] _mapping = null;
        private final DTMAxisIterator _source;
        private boolean _children = false;
        private int _action = USE_PREDICATE;
        private int _last = -1;

        public StrippingIterator(DTMAxisIterator source, short[] mapping,
                                 StripFilter filter) {
            _filter = filter;
            _mapping = mapping;
            _source = source;

            if (_source instanceof ChildrenIterator
                  || _source instanceof TypedChildrenIterator) {
                _children = true;
            }
        }

        public DTMAxisIterator setStartNode(int node) {
            if (_children){
                if (_filter.stripSpace((DOM)DOMImpl.this, node,
                                       _mapping[getType(node)])) {
                    _action = STRIP_SPACE;
                } else {
                    _action = PRESERVE_SPACE;
                }
            }

            _source.setStartNode(node);
            return this;
        }
  
        public int next() {
            int node;
            while ((node = _source.next()) != END) {
                switch(_action) {
                case STRIP_SPACE:
                    if (isWhitespace(node)) {
                        continue;
                    }
                // fall through...
                case PRESERVE_SPACE:
                    return returnNode(node);
                case USE_PREDICATE:
                default:
                    if (isWhitespace(node)
                         && _filter.stripSpace((DOM)DOMImpl.this, node,
                                         _mapping[getType(getParent(node))])) {
                        continue;
                    }
                    return returnNode(node);
                }
            }
            return END;
        }


        public void setRestartable(boolean isRestartable) {
            _isRestartable = isRestartable;
            _source.setRestartable(isRestartable);
        }
	

        public DTMAxisIterator reset() {
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
            if (_last != -1) {
                return _last;
            }

            int count = getPosition();
            int node;

            _source.setMark();
            while ((node = _source.next()) != END) {
                switch(_action) {
                case STRIP_SPACE:
                    if (isWhitespace(node)) {
                        continue;
                    }
                    // fall through...
                case PRESERVE_SPACE:
                    count++;
                    break;
                case USE_PREDICATE:
                default:
                    if (isWhitespace(node)
                         && _filter.stripSpace((DOM)DOMImpl.this, node,
                                          _mapping[getType(getParent(node))])) {
                        continue;
                    } else {
                         count++;
                    }
                }
            }
            _source.gotoMark();
            _last = count;
            return count;
        }
  
        private boolean isWhitespace(int node) {
            final int nodeIdent = getNodeIdent(node);
    
            // Is this the first time we've visited this node?  If so, check
            // whether it's whitespace.
            if (!_checkedForWhitespace.getBit(nodeIdent)) {
                _checkedForWhitespace.setBit(nodeIdent);
                final int nodeType = getNodeType(node);
                if ((nodeType == DTM.TEXT_NODE
                       || nodeType == DTM.CDATA_SECTION_NODE)
                     && DOMImpl.this.isWhitespace(node)) {
                    _whitespace.setBit(nodeIdent);
                    return true;
                }
                return false;
            }

            return _whitespace.getBit(nodeIdent);
        }

    } // end of StrippingIterator

    public DTMAxisIterator strippingIterator(DTMAxisIterator iterator,
                                          short[] mapping,
                                          StripFilter filter) 
    {
      return(new StrippingIterator(iterator, mapping, filter));
    }

    /**************************************************************
     * This is a specialised iterator for predicates comparing node or
     * attribute values to variable or parameter values.
     */
    private final class NodeValueIterator extends InternalAxisIteratorBase //NodeIteratorBase 
    {

	private DTMAxisIterator _source;
	private String _value;
	private boolean _op;
	private final boolean _isReverse;
	private int _returnType = RETURN_PARENT;	

	public NodeValueIterator(DTMAxisIterator source, int returnType,
				 String value, boolean op) 
  {
	    _source = source;
	    _returnType = returnType;
	    _value = value;
	    _op = op;
	    _isReverse = source.isReverse();
	}

	public boolean isReverse() 
  {
	    return _isReverse;
	}
    

  public DTMAxisIterator cloneIterator() 
  {
    try 
    {
      NodeValueIterator clone = (NodeValueIterator)super.clone();
      clone._isRestartable = false;
      clone._source = _source.cloneIterator();
      clone._value = _value;
      clone._op = _op;
      return clone.reset();
    }
    catch (CloneNotSupportedException e) 
    {
      BasisLibrary.runTimeError(BasisLibrary.ITERATOR_CLONE_ERR,
                                e.toString());
      return null;
    }
  }

	public void setRestartable(boolean isRestartable) {
	    _isRestartable = isRestartable;
	    _source.setRestartable(isRestartable);
	}
    
	public DTMAxisIterator reset() 
  {
	    _source.reset();
	    return resetPosition();
	}

	public int next() 
  {

    int node;
    while ((node = _source.next()) != END) 
    {
      String val = getNodeValue(node);
      if (_value.equals(val) == _op) 
      {
        if (_returnType == RETURN_CURRENT)
          return returnNode(node);
        else
          return returnNode(getParent(node));
      }
    }
    return END;
  }

	public DTMAxisIterator setStartNode(int node) 
  {
    if (_isRestartable) 
    {
      _source.setStartNode(_startNode = node); 
      return resetPosition();
    }
    return this;
  }

	public void setMark() 
  {
	    _source.setMark();
	}

	public void gotoMark() 
  {
	    _source.gotoMark();
	}
    } 
    
    // end NodeValueIterator

    public DTMAxisIterator getNodeValueIterator(DTMAxisIterator iterator, int type,
					     String value, boolean op) 
    {
	    return(DTMAxisIterator)(new NodeValueIterator(iterator, type, value, op));
    }

    /**
     * Encapsulates an iterator in an OrderedIterator to ensure node order
     */
    public DTMAxisIterator orderNodes(DTMAxisIterator source, int node) 
    {
	    return new DupFilterIterator(source);
    }

    /**
     * Returns the leftmost descendant of a node (bottom left in sub-tree)
     */
    private int leftmostDescendant(int node) 
    {
      int current;
      while (getType(current = node) >= DTM.NTYPES
             && (node = getFirstChild(node)) != DTM.NULL)
      {
      }
      return current;
    }

    /**
     * Returns index of last child or 0 if no children
     * (returns DTM.NULL in fact)
     */
    private int lastChild(int node) 
    {
      return getLastChild(node);
    }

    /**
     * Returns the parent of a node
     *
    public int getParent(final int node) {
	return _parent[node];
    }  use DTM's */

    public int getElementPosition(int node) 
    {
      // Initialize with the first sbiling of the current node
      int match = 0;
      int curr  = getFirstChild(getParent(node));
      if (isElement(curr)) match++;

      // Then traverse all other siblings up until the current node
      while (curr != node) 
      {
        curr = getNextSibling(curr);
        if (isElement(curr)) match++;
      }

      // And finally return number of matches
      return match;         
    }

    public int getAttributePosition(int attr) 
    {
      // Initialize with the first sbiling of the current node
      int match = 1;
      int curr  = getFirstChild(getParent(attr));

      // Then traverse all other siblings up until the current node
      while (curr != attr) 
      {
        curr = getNextSibling(curr);
        match++;
      }

      // And finally return number of matches
      return match;         
    }

    /**
     * Returns a node's position amongst other nodes of the same type
     */
    public int getTypedPosition(int type, int node) 
    {
      // Just return the basic position if no type is specified
      switch(type)
      {
      case DTM.ELEMENT_NODE:
        return getElementPosition(node);
      case DTM.ATTRIBUTE_NODE:
        return getAttributePosition(node);
      case -1:
        type = getNodeType(node);
      }

      // Initialize with the first sbiling of the current node
      int match = 0;
      int curr  = getFirstChild(getParent(node));
      if (getExpandedTypeID(curr) == type) match++;

      // Then traverse all other siblings up until the current node
      while (curr != node)
      {
        curr = getNextSibling(curr);
        if (getExpandedTypeID(curr) == type) match++;
      }

      // And finally return number of matches
      return match;
    }
    
    /**
     * Returns an iterator's last node of a given type
     */
    public int getTypedLast(int type, int node) 
    {
      // Just return the basic position if no type is specified
      if (type == -1) type = getNodeType(node);

      // Initialize with the first sbiling of the current node
      int match = 0;
      int curr  = getFirstChild(getParent(node));
      if (getExpandedTypeID(curr) == type) match++;

      // Then traverse all other siblings up until the very last one
      while (curr != DTM.NULL)
      {
        curr = getNextSibling(curr);
        if (getExpandedTypeID(curr) == type) match++;
      }

      return match;        
    }

    /**
     * Returns singleton iterator containg the document root
     * Works for them main document (mark == 0)
     */
    public DTMAxisIterator getIterator() 
    {
	    return new SingletonIterator(getDocument()); //ROOTNODE);
    }

    /**
     * Returns the type of a specific node
     */
    public int getType(final int node) {
        return getExpandedTypeID(node);
    }
    
    /**
     * Get mapping from DOM namespace types to external namespace types
     */
    public int getNSType(int node)
    {
        final String uri = getNamespaceURI(node);
        if (uri == null || uri.length() == 0) {
            return 0;
        }
        int eType = getExpandedTypeID(uri, getPrefix(node), DTM.NAMESPACE_NODE);
    	return ((Integer)_nsIndex.get(new Integer(eType))).intValue();        
    }
    
    
    /**
     * Returns the namespace type of a specific node
     */
    public int getNamespaceType(final int node) {
      return super.getNamespaceType(node);
    }

    /**
     * Returns the (String) value of any node in the tree
     */
    public String getNodeValue(final int node) {
	if (node == DTM.NULL) return EMPTYSTRING;
	switch(getNodeType(node)) {
	case DTM.ROOT_NODE:
	case DTM.DOCUMENT_NODE:
	    return getStringValue(getFirstChild(node)).toString();
	case DTM.TEXT_NODE:
	// GTM - add escapign code here too.
	case DTM.COMMENT_NODE:
	    return getStringValue(node).toString();
	case DTM.PROCESSING_INSTRUCTION_NODE:
	    final String pistr = getStringValue(node).toString();
	    return pistr;
	default:
	    return getStringValue(node).toString();
	}
    }

    /**
     * Sets up a translet-to-dom type mapping table
     */
    private int[] setupMapping(String[] namesArray) 
    {
      final int nNames = namesArray.length;
      // Padding with number of names, because they
      // may need to be added, i.e for RTFs. See copy03  
      final int[] types = new int[m_expandedNameTable.getSize()];
      for (int i = 0; i < nNames; i++) {
          int type = getGeneralizedType(namesArray[i]);
          types[type] = type;
      }
      return types;
    }

    /**
     * Returns the internal type associated with an expanded QName
     */
    public int getGeneralizedType(final String name) {
        String lName, ns = null;
        int index;
        int code;

        // Is there a prefix?
        if ((index = name.lastIndexOf(":"))> -1) {
            ns = name.substring(0, index);
        }

        // Local part of name is after colon.  lastIndexOf returns -1 if
        // there is no colon, so lNameStartIdx will be zero in that case.
        int lNameStartIdx = index+1;

        // Distinguish attribute and element names.  Attribute has @ before
        // local part of name.
        if (name.charAt(lNameStartIdx) == '@') {
            code = DTM.ATTRIBUTE_NODE;
            lNameStartIdx++;
        }
        else {
            code = DTM.ELEMENT_NODE;
        }

        // Extract local name
        lName = (lNameStartIdx == 0) ? name : name.substring(lNameStartIdx);

        return this.getExpandedTypeID(ns, lName, code);
    }

    /**
     * Get mapping from DOM element/attribute types to external types
     */
    public short[] getMapping(String[] names) {
        int i;
        final int namesLength = names.length;
        final int exLength = m_expandedNameTable.getSize();
        final short[] result = new short[exLength];

        // primitive types map to themselves
        for (i = 0; i < DTM.NTYPES; i++)
            result[i] = (short)i;
        
        for (i = NTYPES; i < exLength; i++) 
            result[i] = m_expandedNameTable.getType(i); 
      	
        // actual mapping of caller requested names
        for (i = 0; i < namesLength; i++) {
            int genType = getGeneralizedType(names[i]);
            if (genType < _types.length && genType == _types[genType]) {
                result[genType] = (short)(i + DTM.NTYPES);
            }
        }

        return result;
    }

    /**
     * Get mapping from external element/attribute types to DOM types
     */
    public int[] getReverseMapping(String[] names) {
      int i;
      final int[] result = new int[names.length + DTM.NTYPES];
      // primitive types map to themselves
      for (i = 0; i < DTM.NTYPES; i++) 
      {
        result[i] = i;
      }
      // caller's types map into appropriate dom types
      for (i = 0; i < names.length; i++)
      {
          int type = getGeneralizedType(names[i]);
          result[i+DTM.NTYPES] = type;
      }
      return result;
    }

    /**
     * Get mapping from DOM namespace types to external namespace types
     */
    public short[] getNamespaceMapping(String[] namespaces) 
    {
      int i;
      final int nsLength = namespaces.length;
      final int mappingLength = _uriArray.length;
      final short[] result = new short[mappingLength];

      // Initialize all entries to -1
      for (i=0; i<mappingLength; i++)
        result[i] = (short)(-1);

      for (i=0; i<nsLength; i++)
      {
        int eType = getExpandedTypeID(namespaces[i],
                                      (String)_prefixArray.get(namespaces[i]),
                                      DTM.NAMESPACE_NODE);
        Integer type = (Integer)_nsIndex.get(new Integer(eType));
        if (type != null)
        {
          result[type.intValue()] = (short)i;
        }
      }

      return(result);
    }

    /**
     * Get mapping from external namespace types to DOM namespace types
     */
    public short[] getReverseNamespaceMapping(String[] namespaces) 
    {
      int i;
      final int length = namespaces.length;
      final short[] result = new short[length];

      for (i = 0; i < length; i++) 
      {
        int eType = getExpandedTypeID(namespaces[i],
                                      (String)_prefixArray.get(namespaces[i]),
                                      DTM.NAMESPACE_NODE);
        Integer type = (Integer)_nsIndex.get(new Integer (eType));
	result[i] = (type == null) ? -1 : type.shortValue();
      }

      return result;
    }

    /**
     * Dump the whole tree to a file (serialized)
     */
    public void writeExternal(ObjectOutput out) throws IOException 
    {
      out.writeInt(_treeNodeLimit);      // number of nodes in DOM
      out.writeInt(_firstAttributeNode); // index of first attribute node
      out.writeObject(_documentURI);     // URI of original document

      //out.writeObject(_type);            // type of every node in DOM
      out.writeObject(_namespace);       // namespace URI of each type
     // out.writeObject(_prefix);          // prefix type of every node in DOM

      //out.writeObject(_parent);          // parent of every node in DOM
      //out.writeObject(_nextSibling);     // next sibling of every node in DOM
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
      throws IOException, ClassNotFoundException 
    {
      _treeNodeLimit = in.readInt();
      _firstAttributeNode = in.readInt();
      _documentURI = (String)in.readObject();

      //_type          = (short[])in.readObject();
      _namespace     = (short[])in.readObject();
    //  _prefix        = (short[])in.readObject();

      //_parent        = (int[])in.readObject();
      //_nextSibling   = (int[])in.readObject();
      _offsetOrChild = (int[])in.readObject();
      _lengthOrAttr  = (int[])in.readObject();

      _text          = (char[])in.readObject();
      _namesArray    = (String[])in.readObject();
      _uriArray      = (String[])in.readObject();
      _prefixArray   = (Hashtable)in.readObject();

      _whitespace    = (BitArray)in.readObject();


	_dontEscape    = (BitArray)in.readObject();
	if (_dontEscape.size() == 0) {
	    _dontEscape = null;
        }

	_types         = setupMapping(_namesArray);

    }

    /*
     * These init sizes have been tuned for the average case. Do not
     * change these values unless you know exactly what you're doing.
     */
    static private final int SMALL_TEXT_SIZE   = 1024; 
    static private final int DEFAULT_INIT_SIZE = 1024;
    static private final int DEFAULT_TEXT_FACTOR = 10;

    /**
     * Construct a DOMImpl object from a DOM node.
     *
     * @param mgr The DTMManager who owns this DTM.
     * @param domSource the DOM source that this DTM will wrap.
     * @param dtmIdentity The DTM identity ID for this DTM.
     * @param whiteSpaceFilter The white space filter for this DTM, which may
     *                         be null.
     * @param xstringFactory XMLString factory for creating character content.
     * @param doIndexing true if the caller considers it worth it to use
     *                   indexing schemes.
     */
    public DOMImpl(DTMManager mgr, DOMSource domSource, 
                   int dtmIdentity, DTMWSFilter whiteSpaceFilter,
                   XMLStringFactory xstringfactory,
                   boolean doIndexing)
    {
        this(mgr, domSource, dtmIdentity, whiteSpaceFilter, xstringfactory,
             doIndexing, DEFAULT_INIT_SIZE);
    }
    
    /**
     * Construct a DOMImpl object from a DOM node.
     *
     * @param mgr The DTMManager who owns this DTM.
     * @param domSource the DOM source that this DTM will wrap.
     * @param dtmIdentity The DTM identity ID for this DTM.
     * @param whiteSpaceFilter The white space filter for this DTM, which may
     *                         be null.
     * @param xstringFactory XMLString factory for creating character content.
     * @param doIndexing true if the caller considers it worth it to use
     *                   indexing schemes.
     * @param size The number of nodes required for the tree.
     */
    public DOMImpl(DTMManager mgr, DOMSource domSource, 
                 int dtmIdentity, DTMWSFilter whiteSpaceFilter,
                 XMLStringFactory xstringfactory,
                 boolean doIndexing, int size)
    {
        super(mgr, domSource, dtmIdentity, whiteSpaceFilter, xstringfactory,
              doIndexing);
	initialize(size, size < 128 ? SMALL_TEXT_SIZE
	                            : size * DEFAULT_TEXT_FACTOR);
    }
    
    /**
     *  defines initial size
     */
    public void initialize(int size, int textsize)
    {
      _offsetOrChild        = new int[size];
      _lengthOrAttr         = new int[size];
      _text                 = new char[textsize];
      _whitespace           = new BitArray(size);
      _checkedForWhitespace = new BitArray(size);
    }

    /**
     * Prints the whole tree to standard output
     */
    public void print(int node, int level) {
	switch(getNodeType(node)) 
        {
	case DTM.ROOT_NODE:
	case DTM.DOCUMENT_NODE:
	    print(getFirstChild(node), level);
	    break;
	case DTM.TEXT_NODE:
	case DTM.COMMENT_NODE:
	case DTM.PROCESSING_INSTRUCTION_NODE:
	    System.out.print(getStringValue(node).toString());
	    break;
	default:                  // element
	    final String name = getNodeName(node);
	    System.out.print("<" + name);
	    for (int a = getFirstAttribute(node); a != DTM.NULL; a = getNextAttribute(a))
      {
		    System.out.print("\n" + getNodeName(a) +
				 "=\"" + getStringValue(a).toString() + "\"");
	    }
	    System.out.print('>');
	    for (int child = getFirstChild(node); child != DTM.NULL;
		                                  child = getNextSibling(child))
      {
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
	int nodeh = node; //makeNodeHandle(node);
	final short type = getNodeType(nodeh);
	switch(type) {
	case DTM.ROOT_NODE:
	case DTM.DOCUMENT_NODE:
	case DTM.TEXT_NODE:
	case DTM.COMMENT_NODE:
	    return EMPTYSTRING;
        case DTM.NAMESPACE_NODE:
            final String name = this.getLocalName(nodeh);
            // %HZ% %REVISIT% DTM bug?  Should DTM.getLocalName for a default
            // %HZ% %REVISIT% namespace declaration return the empty string or
            // %HZ% %REVISIT% "xmlns"?  DOM2DTM returns the latter, but SAX2DTM
            // %HZ% %REVISIT% the former.
            return name.equals("xmlns") ? EMPTYSTRING : name;
	default:
	    return super.getNodeName(nodeh);
	}
    }
 

    /**
     * Returns the namespace URI to which a node belongs
     */
    public String getNamespaceName(final int node) 
    {
        if (node == DTM.NULL || getNodeType(node) == DTM.NAMESPACE_NODE)
            return EMPTYSTRING;
        String s;
        return (s = getNamespaceURI(node)) == null ? EMPTYSTRING : s;
    }

    /**
     * Returns the string value of a single text/comment node or
     * attribute value (they are all stored in the same array).
     */
    private String makeStringValue(final int node) 
    {
      return getStringValue(node).toString();
    }

    /**
     * Returns the attribute node of a given type (if any) for an element
     */
    public int getAttributeNode(final int type, final int element) 
    {      
      for (int attr = getFirstAttribute(element);
           attr != DTM.NULL;
           attr = getNextAttribute(attr)) 
      {
        if (getExpandedTypeID(attr) == type) return attr;
      }
      return DTM.NULL;
    }

    /**
     * Returns the value of a given attribute type of a given element
     */
    public String getAttributeValue(final int type, final int element) 
    {
      final int attr = getAttributeNode(type, element);
      return (attr != DTM.NULL) ? getStringValue(attr).toString() : EMPTYSTRING;
    }

    /**
     * Returns true if a given element has an attribute of a given type
     */
    public boolean hasAttribute(final int type, final int node) 
    {
      return (getAttributeNode(type, node) != DTM.NULL);
    }

    /**
     * This method is for testing/debugging only
     */
    public String getAttributeValue(final String name, final int element) 
    {
      return getAttributeValue(getGeneralizedType(name), element);
    }
    
    /**
     * Returns true if the given element has any children
     */
    private boolean hasChildren(final int node) 
    {
      return(hasChildNodes(node));
    }

    /**
     * Returns an iterator with all the children of a given node
     */
    public DTMAxisIterator getChildren(final int node) 
    {
      return hasChildren(node)
                 ? new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.ChildrenIterator()
                 : EMPTYITERATOR;
    }

    /**
     * Returns an iterator with all children of a specific type
     * for a given node (element)
     */
    public DTMAxisIterator getTypedChildren(final int type)
    {      
      return(new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.TypedChildrenIterator(type));
    }

    /**
     * This is a shortcut to the iterators that implement the
     * supported XPath axes (only namespace::) is not supported.
     * Returns a bare-bones iterator that must be initialized
     * with a start node (using iterator.setStartNode()).
     */
    public DTMAxisIterator getAxisIterator(final int axis) 
    {
      switch (axis) 
      {
      case Axis.SELF:
        return new SingletonIterator();
      case Axis.CHILD:
        return new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.ChildrenIterator();
      case Axis.PARENT:
        return new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.ParentIterator();
      case Axis.ANCESTOR:
        return new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.AncestorIterator();
      case Axis.ANCESTORORSELF:
        return (new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.AncestorIterator()).includeSelf();
      case Axis.ATTRIBUTE:
        return new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.AttributeIterator();
      case Axis.DESCENDANT:
        return new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.DescendantIterator();
      case Axis.DESCENDANTORSELF:
        return (new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.DescendantIterator()).includeSelf();
      case Axis.FOLLOWING:
        return new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.FollowingIterator();
      case Axis.PRECEDING:
        return new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.PrecedingIterator();
      case Axis.FOLLOWINGSIBLING:
        return new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.FollowingSiblingIterator();
      case Axis.PRECEDINGSIBLING:
        return new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.PrecedingSiblingIterator();
      case Axis.NAMESPACE:
        return new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.NamespaceIterator();
      default:
        BasisLibrary.runTimeError(BasisLibrary.AXIS_SUPPORT_ERR,
                                  Axis.names[axis]);
      }
      return null;
    }

    /**
     * Similar to getAxisIterator, but this one returns an iterator
     * containing nodes of a typed axis (ex.: child::foo)
     */
    public DTMAxisIterator getTypedAxisIterator(int axis, int type) 
    {      
      /* This causes an error when using patterns for elements that
      do not exist in the DOM (translet types which do not correspond
      to a DOM type are mapped to the DOM.ELEMENT type).
      */

      // Most common case handled first
      if (axis == Axis.CHILD && type != DTM.ELEMENT_NODE) {
          return new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.TypedChildrenIterator(type);
      }

      if (type == NO_TYPE) 
      {
        return EMPTYITERATOR;
      }
      if ((type == DTM.ELEMENT_NODE) && (axis != Axis.NAMESPACE)) 
      {
        return new FilterIterator(getAxisIterator(axis),
                                  getElementFilter());
      }
      else 
      {
        switch (axis) 
        {
        case Axis.SELF:
          return new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.TypedSingletonIterator(type);
        case Axis.PARENT:
          return new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.ParentIterator().setNodeType(type);
        case Axis.ANCESTOR:
          return new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.TypedAncestorIterator(type);
        case Axis.ANCESTORORSELF:
          return (new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.TypedAncestorIterator(type)).includeSelf();
        case Axis.ATTRIBUTE:
          return new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.TypedAttributeIterator(type);
        case Axis.DESCENDANT:
          return new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.TypedDescendantIterator(type);
        case Axis.DESCENDANTORSELF:
          return (new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.TypedDescendantIterator(type)).includeSelf();
        case Axis.FOLLOWING:
          return new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.TypedFollowingIterator(type);
        case Axis.PRECEDING:
          return new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.TypedPrecedingIterator(type);
        case Axis.FOLLOWINGSIBLING:
          return new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.TypedFollowingSiblingIterator(type);
        case Axis.PRECEDINGSIBLING:
          return new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.TypedPrecedingSiblingIterator(type);
        case Axis.NAMESPACE:
          return (type == DTM.ELEMENT_NODE)
           ?        new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.NamespaceIterator() :
                    new TypedNamespaceIterator(type);
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
    public DTMAxisIterator getNamespaceAxisIterator(int axis, int ns) 
    {

      DTMAxisIterator iterator = null;

      if (ns == NO_TYPE) {
        return EMPTYITERATOR;
      }
      else {
        switch (axis) {
        case Axis.CHILD:
          return new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.NamespaceChildrenIterator(ns);
        case Axis.ATTRIBUTE:
          return new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.NamespaceAttributeIterator(ns);
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
    public DTMAxisIterator getTypedDescendantIterator(int type) 
    {
      return (type == DTM.ELEMENT_NODE)
              ? (DTMAxisIterator) new FilterIterator(new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.DescendantIterator(),
                                   getElementFilter())
              : (DTMAxisIterator) new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.TypedDescendantIterator(type);
    }

    /**
     * Returns the nth descendant of a node
     */
    public DTMAxisIterator getNthDescendant(int type, int n, boolean includeself) 
    {
      DTMAxisIterator source =
             (type == DTM.ELEMENT_NODE)
                   ? (DTMAxisIterator) new FilterIterator(new DescendantIterator(),
                                        getElementFilter())
                   : (DTMAxisIterator) new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.TypedDescendantIterator(type);
      // %HZ% Need to do something here???
      //TODO?? if (includeself) 
      //  ((NodeIteratorBase)source).includeSelf();
// %HZ%:  What are we doing with source????
      return new org.apache.xml.dtm.ref.DTMDefaultBaseIterators.NthDescendantIterator(n);
    }

    /**
     * Copy the contents of a text-node to an output handler
     */
    public void characters(final int textNode, TransletOutputHandler handler)
      throws TransletException 
    {
      //TODO: child of a text node== null!!
      /* handler.characters(_text,
                         _offsetOrChild[textNode],
                         _lengthOrAttr[textNode]); */
       char[] buffer;
        buffer = getNodeValue(textNode).toCharArray();
        handler.characters(buffer, 0, buffer.length);
    }

    /**
     * Copy a node-set to an output handler
     */
    public void copy(DTMAxisIterator nodes, TransletOutputHandler handler)
      throws TransletException 
    {
      int node;
      while ((node = nodes.next()) != DTM.NULL) 
      {
        copy(node, handler);
      }
    }

    /**
     * Copy the whole tree to an output handler
     */
    public void copy(TransletOutputHandler handler) throws TransletException 
    {
      copy(getDocument()/*(DTMDefaultBase.ROOTNODE*/, handler);
    }

    /** 
     * Performs a deep copy (ref. XSLs copy-of())
     *
     * TODO: Copy namespace declarations. Can't be done until we
     *       add namespace nodes and keep track of NS prefixes
     * TODO: Copy comment nodes
     */
    public void copy(final int node, TransletOutputHandler handler)
	throws TransletException 
    {

      final int type = getNodeType(node); //_type[node];


      switch(type) 
      {
      case DTM.ROOT_NODE:
       case DTM.DOCUMENT_NODE:
        for(int c=getFirstChild(node); c!=DTM.NULL; c=getNextSibling(c))
          copy(c, handler);
        break;
      case DTM.PROCESSING_INSTRUCTION_NODE:
        copyPI(node, handler);
        break;
      case DTM.COMMENT_NODE:
        handler.comment(getNodeValue(node)/*_text,
                                   _offsetOrChild[node],
                                   _lengthOrAttr[node])*/);
        break;
      case DTM.TEXT_NODE:
        char[] buffer = getNodeValue(node).toCharArray();
        handler.characters(buffer, 0, buffer.length);/*_text,
                           _offsetOrChild[node],
                           _lengthOrAttr[node]);*/
        break;
      case DTM.ATTRIBUTE_NODE:
        shallowCopy(node, handler);
        break;
      case DTM.NAMESPACE_NODE:
        shallowCopy(node, handler);
        break;
      default:
        if (isElement(node)) {
          // Start element definition
          final String name = copyElement(node, type, handler);
          // Copy element attribute
          for(int a=getFirstAttribute(node); a!=DTM.NULL; a=getNextAttribute(a)){
              final String uri = getNamespaceName(a);
              if (uri != EMPTYSTRING) {
                final String prefix = getPrefix(a);
                handler.namespace(prefix, uri);
              }
              handler.attribute(getNodeName(a), getNodeValue(a));
            }
            for(int a = getFirstNamespaceNode(node, true);
                    a != DTM.NULL;
                    a = getNextNamespaceNode(node, a, true)) 
           {
              handler.namespace(getNodeNameX(a),
                                getNodeValue(a));
            }
          
          // Copy element children
          for(int c=getFirstChild(node); c!=DTM.NULL; c=getNextSibling(c))
            copy(c, handler);
          // Close element definition
          handler.endElement(name);
        }
        // Shallow copy of attribute to output handler
        else {
          final String uri = getNamespaceName(node);
          if (uri != EMPTYSTRING) {
            final String prefix = getPrefix(node);
            handler.namespace(prefix, uri);
          }
          handler.attribute(getNodeName(node), getNodeValue(node));
        }
        break;
      }
      

    }

    /**
     * Copies a processing instruction node to an output handler
     */ 
    private void copyPI(final int node, TransletOutputHandler handler)
	throws TransletException 
    {
      //TODO
     /* final char[] text = _text;
      final int start = _offsetOrChild[node];
      final int length = _lengthOrAttr[node];

      // Target and Value are separated by a whitespace - find it!
      int i = start;
      while (text[i] != ' ') i++;

      final int len = i - start;
      final String target = new String(text, start, len);
      final String value  = new String(text, i + 1, length - len - 1);
      */
      final String target = getNodeName(node);
      final String value = getStringValue(node).toString();
      handler.processingInstruction(target, value);
    }

    /**
     * Performs a shallow copy (ref. XSLs copy())
     */
    public String shallowCopy(final int node, TransletOutputHandler handler)
      throws TransletException 
    {

      final int type = getNodeType(node);
      switch(type)
      {
      case DTM.ROOT_NODE: // do nothing
     case DTM.DOCUMENT_NODE:
        return EMPTYSTRING;
      case DTM.TEXT_NODE:
              char[] buffer = this.getNodeValue(node).toCharArray();
        handler.characters(buffer, 0, buffer.length); /*_text,
                           _offsetOrChild[node],
                           _lengthOrAttr[node]); */
        return null;
      case DTM.PROCESSING_INSTRUCTION_NODE:
        copyPI(node, handler);
        return null;
      case DTM.COMMENT_NODE:
        final String comment = getNodeValue(node); /*)new String(_text,
                                          _offsetOrChild[node],
                                          _lengthOrAttr[node]);*/
        handler.comment(comment);
        return null;
      case DTM.NAMESPACE_NODE:
        handler.namespace(getNodeNameX(node), //_prefixArray[_prefix[node]],
                          getNodeValue(node)); //makeStringValue(node));
        return null;
      case DTM.ATTRIBUTE_NODE:
      final String uri = getNamespaceName(node);
          if (uri != EMPTYSTRING) {
            final String prefix = getPrefix(node); // _prefixArray[_prefix[node]];
            handler.namespace(prefix, uri);
          }
      handler.attribute(getNodeName(node), getNodeValue(node)); //makeStringValue(node));
          return null;  
      default:
        if (type == DTM.ELEMENT_NODE) //isElement(node))
        {
          return(copyElement(node, type, handler));
        }
        else
        {
          final String uri1 = getNamespaceName(node);
          if (uri1 != EMPTYSTRING) {
            final String prefix = getPrefix(node); // _prefixArray[_prefix[node]];
            handler.namespace(prefix, uri1);
          }
          handler.attribute(getNodeName(node), getNodeValue(node)); //makeStringValue(node));
          return null;
        }
      }
    }

    private String copyElement(int node, int type,
                               TransletOutputHandler handler)
      throws TransletException 
    {

      String name = null;
      final String prefix = getPrefix(node);
      final String uri = getNamespaceName(node);
      final String local = getLocalName(node);
      name = (prefix.equals(EMPTYSTRING)) ? local : (prefix + ':' + local);

      if (uri != null)
      {
        handler.startElement(name);
        handler.namespace(prefix, uri);
      }
      else
      {
/*
 * %HZ%:  Should prefix be copied when URI is null?  The MAIN branch avoids
 * %HZ%:  it today.
 */
        handler.startElement(name);
      }

/* %HZ%:  How do namespaces normally get copied?
      // Copy element's namespaces
      for (int a = getFirstNamespaceNode(node, false);
               a != DTM.NULL;
               a = getNextNamespaceNode(node, a, false)) {
          handler.namespace(getPrefix(a), getStringValue(a).toString());
      }
*/

      return name;
    }


    /**
     * Returns the string value of the entire tree
     */
// %HZ%: Do we need to cache with DTM?
    private String _cachedStringValue = null;

    public String getStringValue() {
	if (_cachedStringValue == null) {
            _cachedStringValue = getElementValue(getDocument());
	}
	return _cachedStringValue;
    }

    /**
     * Returns the string value of any element
     */
    public String getElementValue(final int element) 
    {
      // optimization: only create StringBuffer if > 1 child
      final int child = getFirstChild(element);
      if (child == DTM.NULL)
        return EMPTYSTRING;
      if ((getNodeType(child) == DTM.TEXT_NODE) && (getNextSibling(child) == DTM.NULL))
        return getStringValue(child).toString();
      else
        return stringValueAux(new StringBuffer(), element).toString();
    }

    /**
     * Helper to getElementValue() above
     */
    private StringBuffer stringValueAux(StringBuffer buffer, final int element) 
    {
      for (int child = getFirstChild(element);
           child != DTM.NULL;
           child = getNextSibling(child)) {
        switch (getNodeType(child)) 
        {
        case DTM.COMMENT_NODE:
          break;
        case DTM.TEXT_NODE:
          buffer.append(getStringValue(child).toString());/*_text,
                        _offsetOrChild[child],
                        _lengthOrAttr[child]);*/
          break;
        case DTM.PROCESSING_INSTRUCTION_NODE:
          /* This method should not return anything for PIs
          buffer.append(_text,
          _offsetOrChild[child],
          _lengthOrAttr[child]);
          */
          break;
        default:
          stringValueAux(buffer, child);
        }
      }
      return buffer;

    }

    public String getTreeString() {
	StringBuffer buf = new StringBuffer();
	buf = getElementString(buf, getDocument());
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

		int attribute = getFirstAttribute(element);
		while (attribute != DTM.NULL) {
		    buffer.append(' ').append(getNodeName(attribute))
		          .append("=\"").append(getNodeValue(attribute))
		          .append('"');
		    attribute = getNextAttribute(attribute);
		}

		if (getFirstChild(element) == DTM.NULL) {
		    buffer.append("/>");
		    return buffer;
		}
		buffer.append('>');
	    }
	}

	for (int child = getFirstChild(element);
	     child != DTM.NULL;
	     child = getNextSibling(child)) {
	    switch (getNodeType(child)) {
	    case DTM.COMMENT_NODE:
		buffer.append("<!--");
		buffer.append(buffer.append(getStringValue(child).toString()));
		buffer.append("-->");
		break;
	    case DTM.TEXT_NODE:
		buffer.append(buffer.append(getStringValue(child).toString()));
		break;
	    case DTM.PROCESSING_INSTRUCTION_NODE:
		buffer.append("<?");
		buffer.append(buffer.append(getStringValue(child).toString()));
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
    public String getLanguage(int node) 
    {
    	int parent = node;
    	while (DTM.NULL != parent)
    {
      if (DTM.ELEMENT_NODE == getNodeType(parent))
      {
        int langAttr = getAttributeNode(parent, "http://www.w3.org/XML/1998/namespace", "lang");

        if (DTM.NULL != langAttr)
        {
          return getNodeValue(langAttr);     
        }
      }

      parent = getParent(parent);
    }      
      return(null);
    }


    /**
     * Returns an instance of the DOMBuilder inner class
     * This class will consume the input document through a SAX2
     * interface and populate the tree.
     */
    public DOMBuilder getBuilder() 
    {
       //return new DOMBuilderImpl();
       return null;
    }

    /**
     * Returns a DOMBuilder class wrapped in a SAX adapter.
     * I am not sure if we need this one anymore now that the
     * DOM builder's interface is pure SAX2 (must investigate)
     */
    public TransletOutputHandler getOutputDomBuilder() 
    {
      //DOMBuilder builder = getBuilder();
     // return new SAXAdapter(builder, builder);
     // %HZ% %REVISIT%:  return new SAXAdapter(new DOMBuilderImpl());
      return null;
    }
    
    /**
     * Return a instance of a DOM class to be used as an RTF
     */ 
    public DOM getResultTreeFrag(int initSize)
    {
        return (SAXImpl) ((XSLTCDTMManager)m_mgr).getDTM(null, true, m_wsfilter,
                                                        false, false, initSize);
    }

    /**
     * %HZ% Need Javadoc
     */
    public Hashtable getElementsWithIDs() {
        return null;
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
    private final class DOMBuilderImpl implements DOMBuilder
            // I think this may not even be needed for the DOM case???
    {

	private final static int ATTR_ARRAY_SIZE = 32;
	private final static int REUSABLE_TEXT_SIZE = 32;
	private final static int INIT_STACK_LENGTH = 64;

	private Hashtable _shortTexts           = null;

	private Hashtable _names                = null;
	private int       _nextNameCode         = NTYPES;
	private int       _parentStackLength    = INIT_STACK_LENGTH;
	private int[]     _parentStack          = new int[INIT_STACK_LENGTH];
	//private int[]     _previousSiblingStack = new int[INIT_STACK_LENGTH];
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
	    _xmlSpaceStack[0] = DTMDefaultBase.ROOTNODE;
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
	private void xmlSpaceDefine(String val, final int node) 
        {
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
	 * Sets the current parent
	 */
	private void linkParent(final int node) {
            if (++_sp >= _parentStackLength) {
                int length = _parentStackLength;
                _parentStackLength = length + INIT_STACK_LENGTH;

                final int newParent[] = new int[_parentStackLength];
                System.arraycopy(_parentStack,0,newParent,0,length);
                _parentStack = newParent;

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
            if (stack != null) 
            {
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
            if (length <= REUSABLE_TEXT_SIZE) 
            {
                // Use a char array instead of string for performance benefit
                char[] chars = new char[length];
                System.arraycopy(_text, base, chars, 0, length);
                final Integer offsetObj = (Integer)_shortTexts.get(chars);

                if (offsetObj != null) 
                {
                    _currentOffset = base;       // step back current
                    return offsetObj.intValue(); // reuse previous string
                }
                else 
                {
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
	    //final int length = _currentOffset - _baseOffset;
	    //_offsetOrChild[node] = maybeReuseText(length);
	    //_lengthOrAttr[node]  = length;
	}
	
	/**
	 * Creates a text-node and checks if it is a whitespace node.
         */
        private int makeTextNode(boolean isWhitespace) {
            final int node = getNumberOfNodes()-1;
            // Tag as whitespace node if the parser tells us that it is...
            if (isWhitespace)
            {
                _whitespace.setBit(node);
            }
            // ...otherwise we check if this is a whitespace node, unless
            // the node is protected by an xml:space="preserve" attribute.
            else if (!_preserve)
            {
      	        while (_currentNode < node)
      	        {
                    int nodeh = makeNodeHandle(++_currentNode);
                    if (isWhitespace(nodeh)) {
                        //System.out.println("<<<set bit2 " +
                        //    SAXImpl.this.getNodeIdent(node)+ " " + node); 
                        _whitespace.setBit(_currentNode);
                    }
                }
            }
            storeTextRef(node);
            return node;
        }


	/**
	 * Links an attribute value (an occurance of a sequence of characters
	 * in the _text[] array) to a specific attribute node index.
	 */
	private void storeAttrValRef(final int attributeNode) {
	    final int length = _currentOffset - _baseOffset;
	}

	private int makeNamespaceNode(String prefix, String uri)
	    throws SAXException {

    	    final int node = nextAttributeNode();
	    _type2[node] = DTM.NAMESPACE_NODE;
	    characters(uri);
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

            characters(attList.getValue(i));
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
	    _parentStack[0] = DTMDefaultBase.ROOTNODE;	// root
	    _currentNode    = DTMDefaultBase.ROOTNODE + 1;
	    _currentAttributeNode = 1;
	    _type2[0] = DTM.NAMESPACE_NODE;

	    definePrefixAndUri(EMPTYSTRING, EMPTYSTRING);
	    startPrefixMapping(XML_PREFIX, "http://www.w3.org/XML/1998/namespace");
	    _lengthOrAttr[DTMDefaultBase.ROOTNODE] = _nextNamespace;
	    _parent2[_nextNamespace] = DTMDefaultBase.ROOTNODE;
	    _nextNamespace = DTM.NULL;
	}

	/**
	 * SAX2: Receive notification of the end of a document.
	 */
	public void endDocument() {
            makeTextNode(false);

            _shortTexts = null;
            final int namesSize = _nextNameCode - DTM.NTYPES;

            // Fill the _namesArray[] array
            _namesArray = new String[namesSize];
            Enumeration keys = _names.keys();
            while (keys.hasMoreElements()) {
                final String name = (String)keys.nextElement();
                final Integer idx = (Integer)_names.get(name);
                _namesArray[idx.intValue() - DTM.NTYPES] = name;
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
                    Integer eType =
                        new Integer(getExpandedTypeID(uri,
                                                (String)_prefixArray.get(uri),
                                                DTM.NAMESPACE_NODE));
                    final Integer idx = (Integer)_nsIndex.get(eType);
                    if (idx != null) {
                        _namespace[i] = idx.shortValue();
                        _uriArray[idx.intValue()] = uri;
                    }
                }
            }

            _prefixArray = new Hashtable(); //String[_prefixCount];
            Enumeration p = _nsPrefixes.keys();
            while (p.hasMoreElements()) 
            {
                final String prefix = (String)p.nextElement();
                final Stack stack = (Stack)_nsPrefixes.get(prefix);
                final Integer I = (Integer)stack.elementAt(0);
                //_prefixArray[I.shortValue()] = prefix;
            }
        }
	
	/**
	 * SAX2: Receive notification of the beginning of an element.
	 */
        public void startElement(String uri, String localName,
				 String qname, Attributes attributes)
	    throws SAXException 
	{
	    makeTextNode(false);

            // Get node index and setup parent/child references
            final int node = nextNode();
            linkParent(node);

            _lengthOrAttr[node] = DTM.NULL;

            final int count = attributes.getLength();

            // Append any namespace nodes
            if (_nextNamespace != DTM.NULL) {
                _lengthOrAttr[node] = _nextNamespace;
                while (_nextNamespace != DTM.NULL) {
                    _parent2[_nextNamespace] = node;
                    int tail = _nextNamespace;
                    _nextNamespace = _nextSibling2[_nextNamespace];
                    // Chain last namespace node to following attribute node(s)
                    if ((_nextNamespace == DTM.NULL) && (count > 0))
                        _nextSibling2[tail] = _currentAttributeNode;
                }
            }

	    // If local name is null set it to the empty string
	    if (localName == null) {
		localName = EMPTYSTRING;
	    }

            // Append any attribute nodes
            if (count > 0) {
                int attr = _currentAttributeNode;
                if (_lengthOrAttr[node] == DTM.NULL)
                    _lengthOrAttr[node] = attr;
                for (int i = 0; i<count; i++) {
                    attr = makeAttributeNode(node, attributes, i);
                    _parent2[attr] = node;
                    _nextSibling2[attr] = attr + 1;
                }
                _nextSibling2[attr] = DTM.NULL;
            }

            final int col = qname.lastIndexOf(':');

// %HZ% Fix comments - no assignment is happening anymore
            // Assign an internal type to this element (may exist)
            if ((uri != null) && (localName.length() > 0))
                makeElementNode(uri, localName);
            else
                makeElementNode(qname, col);
        }

	/**
	 * SAX2: Receive notification of the end of an element.
	 */

	public void endElement(String namespaceURI, String localName,
			       String qname) {

	    makeTextNode(false);

	    // Revert to strip/preserve-space setting from before this element
	    xmlSpaceRevert(_parentStack[_sp]);
	    //_previousSiblingStack[_sp--] = 0;
	}

	/**
	 * SAX2: Receive notification of a processing instruction.
	 */
	public void processingInstruction(String target, String data)
	    throws SAXException {

	    makeTextNode(false);

	    final int node = nextNode();
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
	    if (_nextNamespace == DTM.NULL) {
		_nextNamespace = attr;
	    }
	    else {
		_nextSibling2[attr-1] = attr;
	    }
	    _nextSibling2[attr] = DTM.NULL;
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
            Integer eType = new Integer (getExpandedTypeID(uri, prefix,
                                                           DTM.NAMESPACE_NODE));
            if ((idx = (Integer)_nsIndex.get(eType)) == null) {
                _prefixArray.put(uri, prefix);
                _nsIndex.put(eType, idx = new Integer(_uriCount++));
            }
            stack.push(uri);

            return stack;
        }

        /**
         * SAX2: End the scope of a prefix-URI Namespace mapping.
         */
        public void endPrefixMapping(String prefix) 
        {
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
        public void notationDecl(String name, String publicId,
                                 String systemId) {}


        // %HZ%:  Need Javadoc
        public void unparsedEntityDecl(String name, String publicId,
                                       String systemId, String notationName) {}

        // %HZ%:  Need Javadoc
        public void elementDecl(String name, String model) {}

        // %HZ%:  Need Javadoc
        public void attributeDecl(String eName, String aName, String type,
                                  String valueDefault, String value) {}

        // %HZ%:  Need Javadoc
        public void externalEntityDecl(String name, String publicId,
                                       String systemId) {}

        // %HZ%:  Need Javadoc
        public void internalEntityDecl(String name, String value) {}


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
            if ((length < newSize) && (newSize == _currentNode)) {
                length = _currentNode;
            }

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
