/*
 * @(#)$Id$
 *
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Enumeration;

import javax.xml.transform.Source;

import org.apache.xalan.xsltc.runtime.BasisLibrary;
import org.apache.xalan.xsltc.runtime.Hashtable;
import org.apache.xalan.xsltc.runtime.SAXAdapter;
import org.apache.xml.utils.XMLStringFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.apache.xml.dtm.ref.sax2dtm.SAX2DTM2;

import org.apache.xalan.xsltc.*;
import org.apache.xml.dtm.ref.*;
import org.apache.xml.dtm.*;


public final class SAXImpl extends SAX2DTM2 implements DOM, DOMBuilder
{
    
    /* ------------------------------------------------------------------- */
    /* DOMBuilder fields BEGIN                                             */
    /* ------------------------------------------------------------------- */
    private final static int INIT_STACK_LENGTH = 64;

    private int       _parentStackLength    = INIT_STACK_LENGTH;
    private int[]     _parentStack          = new int[INIT_STACK_LENGTH];
    private int       _sp;

    // Temporary structures for attribute nodes
    private int       _currentAttributeNode = 1;

    // Namespace prefix-to-uri mapping stuff
    private int       _uriCount     = 0;
    private int       _prefixCount  = 0;

    // Stack used to keep track of what whitespace text nodes are protected
    // by xml:space="preserve" attributes and which nodes that are not.
    private int[]   _xmlSpaceStack = new int[64];
    private int     _idx = 1;
    private boolean _preserve = false;

    private static final String XML_STRING = "xml:";
    private static final String XML_PREFIX   = "xml";   
    private static final String XMLSPACE_STRING = "xml:space";
    private static final String PRESERVE_STRING = "preserve";
    private static final String XMLNS_PREFIX = "xmlns";

    private boolean _escaping = true;
    private boolean _disableEscaping = false;
    private int _textNodeToProcess = DTM.NULL;

    /* ------------------------------------------------------------------- */
    /* DOMBuilder fields END                                               */
    /* ------------------------------------------------------------------- */

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

    // Namespace related stuff
    private Hashtable _nsIndex = new Hashtable();

    // Tracks which textnodes are whitespaces and which are not
    //private BitArray  _whitespace; // takes xml:space into acc.
   
    // The initial size of the text buffer
    private int _size = 0;
    
    // Tracks which textnodes are not escaped
    private BitArray  _dontEscape = null;

    // The URI to this document
    private String    _documentURI = null;
    static private int _documentURIIndex = 0;

    // Object used to map TransletOutputHandler events to SAX events
    private CH2TOH _ch2toh = new CH2TOH();

    // Support for access/navigation through org.w3c.dom API
    private Node[] _nodes;
    private NodeList[] _nodeLists;
    private final static String XML_LANG_ATTRIBUTE =
        "http://www.w3.org/XML/1998/namespace:@lang";

    /**
     * Define the origin of the document from which the tree was built
     */
    public void setDocumentURI(String uri) {
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
        while ((anode = ancestors.next()) != DTM.NULL) {
            final NamespaceIterator namespaces = new NamespaceIterator();

            namespaces.setStartNode(anode);
            while ((nsnode = namespaces.next()) != DTM.NULL) {
                if (getLocalName(nsnode).equals(prefix)) {
                    return getNodeValue(nsnode);
                }
            }
        }

        BasisLibrary.runTimeError(BasisLibrary.NAMESPACE_PREFIX_ERR, prefix);
        return null;
    }

    /**
     * Returns 'true' if a specific node is an element (of any type)
     */
    public boolean isElement(final int node) {
        return (((node < _firstAttributeNode)
                     && (getExpandedTypeID(node) >= DTM.NTYPES))
                || getNodeType(node) == DTM.ELEMENT_NODE);
    }

    /**
     * Returns 'true' if a specific node is an attribute (of any type)
     */
    public boolean isAttribute(final int node) {
        return (((node >= _firstAttributeNode)
                     && (getExpandedTypeID(node) >= DTM.NTYPES))
                || getNodeType(node) == DTM.ATTRIBUTE_NODE);
    }

    /**
     * Returns the number of nodes in the tree (used for indexing)
     */
    public int getSize() {
        return getNumberOfNodes();
    }

    /**
     * Part of the DOM interface - no function here.
     */
    public void setFilter(StripFilter filter) {
    }


    /**
     * Returns true if node1 comes before node2 in document order
     */
    public boolean lessThan(int node1, int node2) {
        if (node1 == DTM.NULL) {
            return false;
        }

        if (node2 == DTM.NULL) {
            return true;
        }

        /*
        // Hack for ordering attribute nodes
        if (getNodeType(node1) == DTM.ATTRIBUTE_NODE) {
            node1 = getParent(node1);
        }

        if (getNodeType(node2) == DTM.ATTRIBUTE_NODE) {
            node2 = getParent(node2);
        }
        */

        return (node1 < node2);
    }

    /**
     * Create an org.w3c.dom.Node from a node in the tree
     */
    public Node makeNode(int index) {
        if (_nodes == null) {
            _nodes = new Node[_namesArray.length + DTM.NTYPES];
        }

        return (_nodes[index] != null)
                    ? _nodes[index]
                    : (_nodes[index] = new DTMNodeProxy((DTM)this, index));
    }

    /**
     * Create an org.w3c.dom.Node from a node in an iterator
     * The iterator most be started before this method is called
     */
    public Node makeNode(DTMAxisIterator iter) {
        return makeNode(iter.next());
    }

    /**
     * Create an org.w3c.dom.NodeList from a node in the tree
     */
    public NodeList makeNodeList(int index) {
        if (_nodeLists == null) {
            _nodeLists = new NodeList[_namesArray.length + DTM.NTYPES];
        }
        return (_nodeLists[index] != null)
                 ? _nodeLists[index]
                 : (_nodeLists[index] =
                         new DTMAxisIterNodeList(this,
                                                 new SingletonIterator(index)));
    }

    /**
     * Create an org.w3c.dom.NodeList from a node iterator
     * The iterator most be started before this method is called
     */
    public NodeList makeNodeList(DTMAxisIterator iter) {
        return new DTMAxisIterNodeList(this, iter);
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
    private DTMFilter getElementFilter() {
        if (_elementFilter == null) {
            _elementFilter = new DTMFilter() {
                  public short acceptNode(int node, int whatToShow) {
                      return (getNodeType(node) == DTM.ELEMENT_NODE)
                                      ? DTMIterator.FILTER_ACCEPT
                                      : DTMIterator.FILTER_REJECT;
                  }
                  public short acceptNode(int node, int whatToShow,
                                          int expandedName)
                  {
                      return (getNodeType(node) == DTM.ELEMENT_NODE)
                                      ? DTMIterator.FILTER_ACCEPT
                                      : DTMIterator.FILTER_REJECT;
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
            return getExpandedTypeID(node) == _nodeType;
        }
    }

    /**
     * Returns a node type filter (implementation of Filter)
     */
    public Filter getTypeFilter(int type) {
        return new TypeFilter(type);
    }

    /**
     * Iterator that returns the namespace nodes as defined by the XPath data
     * model for a given node, filtered by extended type ID.
     */
    public class TypedNamespaceIterator extends NamespaceIterator {
        /** The extended type ID that was requested. */
        private final int _nodeType;

        /**
         * Constructor TypedChildrenIterator
         *
         *
         * @param nodeType The extended type ID being requested.
         */
        public TypedNamespaceIterator(int nodeType) {
            super();
            _nodeType = nodeType;
        }

       /**
        * Get the next node in the iteration.
        *
        * @return The next node handle in the iteration, or END.
        */
        public int next() {
            int node;

            for (node = super.next(); node != END; node = super.next()) {
                if (getExpandedTypeID(node) == _nodeType
                      || getNodeType(node) == _nodeType
                      || getIdForNamespace(getStringValue(node).toString())
                             == _nodeType) {
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
     *
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

	public StrippingIterator(DTMAxisIterator source,
                           short[] mapping,
                           StripFilter filter)
  {

    _filter = filter;
    _mapping = mapping;
    _source = source;

    if (_source instanceof ChildrenIterator ||
        _source instanceof TypedChildrenIterator)
    {
      _children = true;
    }
  }

	public DTMAxisIterator setStartNode(int node)
  {
    if (_children)
    {
      if (_filter.stripSpace((DOM)SAXImpl.this, node,
                             _mapping[getExpandedTypeID(node)]))
        _action = STRIP_SPACE;
      else
        _action = PRESERVE_SPACE;
    }

    _source.setStartNode(node);
    //return resetPosition();
    return(this);
  }

  public int next()
  {
    int node;
    while ((node = _source.next()) != END)
    {
      switch(_action)
      {
      case STRIP_SPACE:
        if (_whitespace.getBit(getNodeIdent(node))) continue;
        // fall through...
      case PRESERVE_SPACE:
        return returnNode(node);
      case USE_PREDICATE:
      default:
        if (_whitespace.getBit(getNodeIdent(node)) &&
            _filter.stripSpace((DOM)SAXImpl.this, node,
                               _mapping[getExpandedTypeID(getParent(node))]))
          continue;
        return returnNode(node);
      }
    }
    return END;
  }
  
  public void setRestartable(boolean isRestartable) {
	    _isRestartable = isRestartable;
	    _source.setRestartable(isRestartable);
	}

	public DTMAxisIterator reset()
  {
	    _source.reset();
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

	public int getLast()
  {
    // Return chached value (if we have it)
    if (_last != -1) return _last;

    int count = getPosition();
    int node;

    _source.setMark();
    while ((node = _source.next()) != END)
    {
      switch(_action)
      {
      case STRIP_SPACE:
        if (_whitespace.getBit(getNodeIdent(node)))
          continue;
        // fall through...
      case PRESERVE_SPACE:
        count++;
        break;
      case USE_PREDICATE:
      default:
        if (_whitespace.getBit(getNodeIdent(node)) &&
            _filter.stripSpace((DOM)SAXImpl.this, node,
                               _mapping[getExpandedTypeID(getParent(node))]))
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
*/
    /*
    public DTMAxisIterator strippingIterator(DTMAxisIterator iterator,
                                          short[] mapping,
                                          StripFilter filter)
    {
      return(new StrippingIterator(iterator, mapping, filter));
    }
    */

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
      String val = getStringValueX(node);
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
     * Returns the parent of a node
     *
    public int getParent(final int node) {
	return _parent[node];
    }  use DTM's */

    public int getElementPosition(int node) {
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
	    return new SingletonIterator(getDocument());
    }

     /**
     * Get mapping from DOM namespace types to external namespace types
     */
    public int getNSType(int node)
    {
    	String s = getNamespaceURI(node);
    	if (s == null)
    	return 0;
    	int eType = getIdForNamespace(s);
    	return ((Integer)_nsIndex.get(new Integer(eType))).intValue();        
    }
    
    

    /**
     * Returns the namespace type of a specific node
     */
    public int getNamespaceType(final int node)
    {
    	return super.getNamespaceType(node);
    }

    /**
     * Returns the (String) value of any node in the tree
     */
    public String getStringValueX(final int node)
    {
	if (node == DTM.NULL) return EMPTYSTRING;
/*
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
*/
        return getStringValue(node).toString();
    }   

    /**
     * Sets up a translet-to-dom type mapping table
     */
    private int[] setupMapping(String[] namesArray, int nNames) {
      // Padding with number of names, because they
      // may need to be added, i.e for RTFs. See copy03  
      final int[] types = new int[m_expandedNameTable.getSize()];
      for (int i = 0; i < nNames; i++)      {
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
        int index = -1;
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
    public short[] getMapping(String[] names)
    {
      int i;
      final int namesLength = names.length;
      final int exLength = m_expandedNameTable.getSize();
      int[] generalizedTypes = null;
      if (namesLength > 0)
          generalizedTypes = new int[namesLength];
      
      int resultLength = exLength;
      
      for (i = 0; i < namesLength; i++) {
          generalizedTypes[i] = getGeneralizedType(names[i]);
          if (_types == null && generalizedTypes[i] >= resultLength)
              resultLength = generalizedTypes[i] + 1;
      }
      
      final short[] result = new short[resultLength];

      // primitive types map to themselves
      for (i = 0; i < DTM.NTYPES; i++)
        result[i] = (short)i;
        
      for (i = NTYPES; i < exLength; i++) 
      	result[i] = m_expandedNameTable.getType(i); 
      	
      // actual mapping of caller requested names
      for (i = 0; i < namesLength; i++) {
          int genType = generalizedTypes[i];         
          if (_types != null) {
              if (genType < _types.length && genType == _types[genType]) {
                  result[genType] = (short)(i + DTM.NTYPES);
              }
          }
          else
              result[genType] = (short)(i + DTM.NTYPES);         
      }

      return(result);

    }

    /**
     * Get mapping from external element/attribute types to DOM types
     */
    public int[] getReverseMapping(String[] names)
    {
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
         /* Integer iType = ((Integer)_types.get(new Integer(type)));
        //  if (iType != null)
        //  {
        result[iType.intValue()] = type;
        if (result[iType.intValue()] == DTM.ELEMENT_NODE)
          result[iType.intValue()] = NO_TYPE;
          }
          else  */
          result[i+DTM.NTYPES] = type;
      }
      return(result);
    }
    
    /**
     * Get mapping from DOM namespace types to external namespace types
     */
    public short[] getNamespaceMapping(String[] namespaces)
    {
      int i;
      final int nsLength = namespaces.length;
 //     final int mappingLength = _uriArray.length;
      final int mappingLength = _uriCount;

      final short[] result = new short[mappingLength];

      // Initialize all entries to -1
      for (i=0; i<mappingLength; i++)
        result[i] = (short)(-1);

      for (i=0; i<nsLength; i++)
      {
        int eType = getIdForNamespace(namespaces[i]); 
                       //getExpandedTypeID(null, getPrefix(null, namespaces[i]) , DTM.NAMESPACE_NODE); // need to make it public in SAX2DTM...
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
          int eType = getIdForNamespace(namespaces[i]);
                        //getExpandedTypeID(null, getPrefix(null, namespaces[i]) , DTM.NAMESPACE_NODE); // need to make it public in SAX2DTM...
        Integer type = (Integer)_nsIndex.get(new Integer(eType));
        result[i] = (type == null) ? -1 : type.shortValue();
      }

      return result;
    }

    /*
     * These init sizes have been tuned for the average case. Do not
     * change these values unless you know exactly what you're doing.
     */
    static private final int SMALL_TEXT_SIZE   = 1024;
    static private final int DEFAULT_INIT_SIZE = 1024;
    static private final int DEFAULT_TEXT_FACTOR = 10;

    /**
     * Construct a SAXImpl object using the default block size.
     */
    public SAXImpl(DTMManager mgr, Source saxSource,
                 int dtmIdentity, DTMWSFilter whiteSpaceFilter,
                 XMLStringFactory xstringfactory,
                 boolean doIndexing)
    {
      this(mgr, saxSource, dtmIdentity, whiteSpaceFilter, xstringfactory,
           doIndexing, m_initialblocksize);
    }

    /**
     * Construct a SAXImpl object using the given block size.
     */
    public SAXImpl(DTMManager mgr, Source saxSource,
                 int dtmIdentity, DTMWSFilter whiteSpaceFilter,
                 XMLStringFactory xstringfactory,
                 boolean doIndexing, int blocksize)
    {
      super(mgr, saxSource, dtmIdentity, whiteSpaceFilter, xstringfactory,
            doIndexing, blocksize);
      _size = blocksize;
      //initialize(size, size < 128 ? SMALL_TEXT_SIZE
      //                            : size * DEFAULT_TEXT_FACTOR);
                                  
       /* From DOMBuilder */ 
      _xmlSpaceStack[0] = DTMDefaultBase.ROOTNODE;                            
    }

    /**
     *  defines initial size
     
    private void initialize(int size, int textsize)
    {
      _whitespace           = new BitArray(size);
    }
    */

   /*---------------------------------------------------------------------------*/
   /* DOMBuilder methods begin                                                  */
   /*---------------------------------------------------------------------------*/
    /**
     * Call this when an xml:space attribute is encountered to
     * define the whitespace strip/preserve settings.
     */
    private void xmlSpaceDefine(String val, final int node)
  {
    final boolean setting = val.equals(PRESERVE_STRING);
    if (setting != _preserve)
    {
      _xmlSpaceStack[_idx++] = node;
      _preserve = setting;
    }
  }

    /**
     * Call this from endElement() to revert strip/preserve setting
     * to whatever it was before the corresponding startElement()
     */
    private void xmlSpaceRevert(final int node)
  {
    if (node == _xmlSpaceStack[_idx - 1])
    {
      _idx--;
      _preserve = !_preserve;
    }
  }

  /**
   * Find out whether or not to strip whitespace nodes.
   *
   *
   * @return whether or not to strip whitespace nodes.
   */
  protected boolean getShouldStripWhitespace()
  {
      if (_preserve)
          return false;
      else
          return super.getShouldStripWhitespace(); 
  }

    /**
     * Returns the next available node. Increases the various arrays
     * that constitute the node if necessary.
     */
//    private int nextNode()
//  {
//    final int index = _currentNode++;
//   /* if (index == _names.length + DTM.NTYPES) //_type.length)
//    {
//      resizeArrays((_names.length + DTM.NTYPES) * 2, index);
//    }*/
//    return index;
//  }

    /**
     * Returns the next available attribute node. Increases the
     * various arrays that constitute the attribute if necessary
     */
  private int nextAttributeNode()
  {
    final int index = _currentAttributeNode++;
    return index;
  }

    /**
     * Sets the current parent
     */
    private void linkParent(final int node)
  {
    if (++_sp >= _parentStackLength)
    {
      int length = _parentStackLength;
      _parentStackLength = length + INIT_STACK_LENGTH;

      final int newParent[] = new int[_parentStackLength];
      System.arraycopy(_parentStack,0,newParent,0,length);
      _parentStack = newParent;

    }
    _parentStack[_sp] = node;
  }

    /**
     * Creates a text-node and checks if it is a whitespace node.
   */
  private void handleTextEscaping() {
      if (_disableEscaping && _textNodeToProcess != DTM.NULL
            && getNodeType(makeNodeHandle(_textNodeToProcess))==DTM.TEXT_NODE) {
          if (_dontEscape == null) {
              //_dontEscape = new BitArray(_whitespace.size());
              _dontEscape = new BitArray(_size);
          }
          _dontEscape.setBit(_textNodeToProcess);
          _disableEscaping = false;
      }
      _textNodeToProcess = DTM.NULL;
  }

    private int makeNamespaceNode(String prefix, String uri)
        throws SAXException
  {

            final int node = nextAttributeNode();
        //characters(uri);
       // storeAttrValRef(node);
        return node;
    }


    /****************************************************************/
    /*               SAX Interface Starts Here                      */
    /****************************************************************/

    /**
     * SAX2: Receive notification of character data.
     */
  public void characters(char[] ch, int start, int length) throws SAXException
  {
       super.characters(ch, start, length);
        
       _disableEscaping = !_escaping;  
       _textNodeToProcess = getNumberOfNodes();
  }

    /**
     * SAX2: Receive notification of the beginning of a document.
     */
    public void startDocument() throws SAXException
  {

        super.startDocument();
        _sp             = 0;
        _parentStack[0] = DTMDefaultBase.ROOTNODE;  // root
        _currentAttributeNode = 1;

        definePrefixAndUri(EMPTYSTRING, EMPTYSTRING);
        startPrefixMapping(XML_PREFIX, "http://www.w3.org/XML/1998/namespace");
    }

    /**
     * SAX2: Receive notification of the end of a document.
     */
    public void endDocument() throws SAXException
  {
    final int namesSize = m_expandedNameTable.getSize() - DTM.NTYPES;
    _types = new int[m_expandedNameTable.getSize()];
 
    // Fill the _namesArray[] array
    String[] namesArray = new String[namesSize];
    int nameCount = 0;

    for (int i = 0; i < namesSize; i++) {
        final short nodeType =
                        m_expandedNameTable.getType(i+DTM.NTYPES);
        final boolean isAttr = (nodeType == DTM.ATTRIBUTE_NODE);
        if (isAttr || nodeType == DTM.ELEMENT_NODE) {
            final String uri = m_expandedNameTable
                                        .getNamespace(i+DTM.NTYPES);
            final String lName = m_expandedNameTable
                                        .getLocalName(i+DTM.NTYPES);

            if (uri != null && uri.length() != 0) {
                namesArray[nameCount++] = isAttr ? (uri + ":@" + lName)
                                               : uri + ":" + lName;
            } else {
                namesArray[nameCount++] = isAttr ? ("@"+lName) : lName;
            }
            int type = getExpandedTypeID(uri, lName, nodeType);
           _types[type] = type;
            
        }
        
    }
    _namesArray = namesArray;
//    _types = setupMapping(_namesArray, nameCount);
    // trim arrays' sizes
    //resizeTextArray(_currentOffset);

    _firstAttributeNode = getNumberOfNodes()-1;
   // shiftAttributes(_currentNode);
    resizeArrays(_firstAttributeNode + _currentAttributeNode,
                 _firstAttributeNode);
   // appendAttributes();
    //_treeNodeLimit = _currentNode + _currentAttributeNode;


        super.endDocument();
     handleTextEscaping();
  }

    /**
     * SAX2: Receive notification of the beginning of an element.
     */
    public void startElement(String uri, String localName,
                 String qname, Attributes attributes)
        throws SAXException
    {
        super.startElement(uri, localName, qname, attributes);
        handleTextEscaping();

        // Get node index and setup parent/child references
        int currentNode = getNumberOfNodes()-1;
        linkParent(currentNode);

        if (m_wsfilter != null) {
            // Look for any xml:space attributes
            // Depending on the implementation of attributes, this
            // might be faster than looping through all attributes. ILENE
            final int index = attributes.getIndex(XMLSPACE_STRING);
            if (index >= 0) {
                xmlSpaceDefine(attributes.getValue(index), currentNode);
            }
        }
    }

    /**
     * SAX2: Receive notification of the end of an element.
     */
    public void endElement(String namespaceURI, String localName,
                   String qname) throws SAXException
    {
        super.endElement(namespaceURI, localName, qname);
        handleTextEscaping();

        // Revert to strip/preserve-space setting from before this element
            // use m_parent??
        xmlSpaceRevert(_parentStack[_sp--]);
    }

    /**
     * SAX2: Receive notification of a processing instruction.
     */
    public void processingInstruction(String target, String data)
        throws SAXException
    {
        super.processingInstruction(target, data);
        handleTextEscaping();
    }

    /**
     * SAX2: Receive notification of ignorable whitespace in element
     * content. Similar to characters(char[], int, int).
     */
    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException
    {
        super.ignorableWhitespace(ch, start, length);
        _textNodeToProcess = getNumberOfNodes();
    }

    /**
     * SAX2: Begin the scope of a prefix-URI Namespace mapping.
     */
    public void startPrefixMapping(String prefix, String uri)
        throws SAXException
    {
        super.startPrefixMapping(prefix, uri);
        handleTextEscaping();

        definePrefixAndUri(prefix, uri);
        makeNamespaceNode(prefix, uri);
    }

    private void definePrefixAndUri(String prefix, String uri) 
        throws SAXException 
    {
            // Check if the URI already exists before pushing on stack
            Integer eType = new Integer(getIdForNamespace(uri));
            if ((Integer)_nsIndex.get(eType) == null) {
                _nsIndex.put(eType, new Integer(_uriCount++));
            }
    }
 

    /**
     * SAX2: Report an XML comment anywhere in the document.
     */
    public void comment(char[] ch, int start, int length)
            throws SAXException
        {
            super.comment(ch, start, length);
            handleTextEscaping();
        }

    /**
     * Similar to the SAX2 method character(char[], int, int), but this
     * method takes a string as its only parameter. The effect is the same.
     */
    private void characters(final String string)
        {
            final int length = string.length();
 //           _currentOffset += length;
    
        }

    private void resizeArrays(final int newSize, int length) {
    if ((length < newSize) && (newSize == getNumberOfNodes()-1))
      length = getNumberOfNodes()-1;

    // Resize the '_whitespace' array (a BitArray instance)
    // _whitespace.resize(newSize);
    // Resize the '_dontEscape' array (a BitArray instance)
    if (_dontEscape != null) {
        _dontEscape.resize(newSize);
    }
  }

        public boolean setEscaping(boolean value) {
            final boolean temp = _escaping;
            _escaping = value; 
            return temp;
        }
   
   /*---------------------------------------------------------------------------*/
   /* DOMBuilder methods end                                                    */
   /*---------------------------------------------------------------------------*/

    /**
     * Prints the whole tree to standard output
     */
 public void print(int node, int level)
 {
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
  public String getNodeName(final int node)
  {
	// Get the node type and make sure that it is within limits
	int nodeh = node; //makeNodeHandle(node);
	final short type = getNodeType(nodeh);
	switch(type)
  {
	case DTM.ROOT_NODE:
	case DTM.DOCUMENT_NODE:
	case DTM.TEXT_NODE:
	case DTM.COMMENT_NODE:
	    return EMPTYSTRING;
	case DTM.NAMESPACE_NODE:
		return this.getLocalName(nodeh);
	default:
	    return super.getNodeName(nodeh);
	}
    }    

    /**
     * Returns the namespace URI to which a node belongs
     */
    public String getNamespaceName(final int node)
    {
    	if (node == DTM.NULL)
    	return "";
        String s;
        return (s = getNamespaceURI(node)) == null ? EMPTYSTRING : s;
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
/*
      return hasChildren(node)
                ? new ChildrenIterator()
                : EMPTYITERATOR;
*/
      return new ChildrenIterator();
    }

    /**
     * Returns an iterator with all children of a specific type
     * for a given node (element)
     */
    public DTMAxisIterator getTypedChildren(final int type)
    {
      return(new TypedChildrenIterator(type));
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
        return new ChildrenIterator();
      case Axis.PARENT:
        return new ParentIterator();
      case Axis.ANCESTOR:
        return new AncestorIterator();
      case Axis.ANCESTORORSELF:
        return (new AncestorIterator()).includeSelf();
      case Axis.ATTRIBUTE:
        return new AttributeIterator();
      case Axis.DESCENDANT:
        return new DescendantIterator();
      case Axis.DESCENDANTORSELF:
        return (new DescendantIterator()).includeSelf();
      case Axis.FOLLOWING:
        return new FollowingIterator();
      case Axis.PRECEDING:
        return new PrecedingIterator();
      case Axis.FOLLOWINGSIBLING:
        return new FollowingSiblingIterator();
      case Axis.PRECEDINGSIBLING:
        return new PrecedingSiblingIterator();
      case Axis.NAMESPACE:
        return new NamespaceIterator();
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
          return new TypedChildrenIterator(type);
      }

      if (type == NO_TYPE)
      {
        return(EMPTYITERATOR);
      }
      else
      {
        switch (axis)
        {
        case Axis.SELF:
          return new TypedSingletonIterator(type);
        case Axis.CHILD:
          return new TypedChildrenIterator(type);
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
          return (type == DTM.ELEMENT_NODE)
                       ? new NamespaceIterator()
                       : new TypedNamespaceIterator(type);
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
    public DTMAxisIterator getTypedDescendantIterator(int type)
    {
/*
      return (type == DTM.ELEMENT_NODE)
              ? (DTMAxisIterator) new FilterIterator(new DescendantIterator(),
                                   getElementFilter())
              : 
*/
      return new TypedDescendantIterator(type);
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
                   : (DTMAxisIterator) new TypedDescendantIterator(type);
      // %HZ% Need to do something here???
      //TODO?? if (includeself)
      //  ((NodeIteratorBase)source).includeSelf();
      return(new NthDescendantIterator(n));
    }

    /**
     * Copy the string value of a node directly to an output handler
     */
    public void characters(final int node, TransletOutputHandler handler)
      throws TransletException
    {
        if (node != DTM.NULL) {
            _ch2toh.setTOH(handler);
            try {
                dispatchCharactersEvents(node, _ch2toh, false);
            } catch (SAXException e) {
                throw new TransletException(e);
            }
        }
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
      copy(getDocument()/*DTMDefaultBase.ROOTNODE*/, handler);
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
        handler.comment(getStringValueX(node));
        break;
      case DTM.TEXT_NODE:
        boolean oldEscapeSetting = false;
        boolean escapeBit = false;

        if (_dontEscape != null) {
            escapeBit = _dontEscape.getBit(getNodeIdent(node));
            if (escapeBit) {
                oldEscapeSetting = handler.setEscaping(false);
            }
        }
        characters(node, handler);
        
        if (escapeBit) {
            handler.setEscaping(oldEscapeSetting);
        }
        break;
      case DTM.ATTRIBUTE_NODE:
        shallowCopy(node, handler);
        break;
      case DTM.NAMESPACE_NODE:
        shallowCopy(node, handler);
        break;
      default:
        if (isElement(node)) 
        {
          // Start element definition
          final String name = copyElement(node, type, handler);
          // Copy element attribute
          for(int a=getFirstAttribute(node); a!=DTM.NULL; a=getNextAttribute(a))
            {
              final String uri = getNamespaceName(a);
              if (uri.length() != 0) {
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
          if (uri.length() != 0) {
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
        characters(node, handler);      
        return null;
      case DTM.PROCESSING_INSTRUCTION_NODE:
        copyPI(node, handler);
        return null;
      case DTM.COMMENT_NODE:
        handler.comment(getStringValueX(node));
        return null;
      case DTM.NAMESPACE_NODE:
        handler.namespace(getNodeNameX(node), //_prefixArray[_prefix[node]],
                          getNodeValue(node)); //makeStringValue(node));
        return null;
      case DTM.ATTRIBUTE_NODE:
      final String uri = getNamespaceName(node);
          if (uri.length() != 0) {
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
          if (uri1.length() != 0) {
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
      final String name = getNodeName(node);
      final String localName = getLocalName(node);
      final String uri = getNamespaceName(node);

      handler.startElement(name);

      if (name.length() != localName.length()) {
        handler.namespace(getPrefix(node), uri);
      } else if (uri.length() != 0) {
        handler.namespace(EMPTYSTRING, uri);
      }

      return name;
    }

    /**
     * Returns the string value of the entire tree
     */
    public String getStringValue()
    {
      final int doc = getDocument();
      final int child = getFirstChild(doc);
      if (child == DTM.NULL) return EMPTYSTRING;
      
      // optimization: only create StringBuffer if > 1 child      
      if ((getNodeType(child) == DTM.TEXT_NODE) && (getNextSibling(child) == DTM.NULL))
        return getStringValue(child).toString();
      else
        return stringValueAux(new StringBuffer(), doc).toString();
    }

    /**
     * Returns the string value of any element
     */
//    public String getElementValue(final int element)
//    {
//      // optimization: only create StringBuffer if > 1 child
//      final int child = getFirstChild(element);
//      if (child == DTM.NULL)
//        return EMPTYSTRING;
//      
//    }

    /**
     * Helper to getStringValue() above
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
          buffer.append(getStringValue(child).toString());
          break;
        case DTM.PROCESSING_INSTRUCTION_NODE:
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
	return this;
    }
    
    /**
     * Return the names array
     */
    public String[] getNamesArray()
    {
        return _namesArray;
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
      return new SAXAdapter(this);
    }
    
    /**
     * Return a instance of a DOM class to be used as an RTF
     */ 
    public DOM getResultTreeFrag(int initSize)
    {
    	return (SAXImpl) ((XSLTCDTMManager)m_mgr).getDTM(null, true, m_wsfilter,
                                                         false, false, false,
                                                         initSize);
    }

    /**
     * %HZ% Need Javadoc
     */
    public Hashtable getElementsWithIDs() {
        if (m_idAttributes == null) {
            return null;
        }

        // Convert a java.util.Hashtable to an xsltc.runtime.Hashtable
        Enumeration idValues = m_idAttributes.keys();
        if (!idValues.hasMoreElements()) {
            return null;
        }

        Hashtable idAttrsTable = new Hashtable();

        while (idValues.hasMoreElements()) {
            Object idValue = idValues.nextElement();

            idAttrsTable.put(idValue, m_idAttributes.get(idValue));
        }

        return idAttrsTable;
    }

    public boolean compareNodeToString(int node, String value) {
        return getStringValue(node).equals(value);
    }
}
