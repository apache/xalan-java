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
 * @author Morten Jorgensen
 *
 */

package org.apache.xalan.xsltc.dom;

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.StripFilter;
import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.runtime.Hashtable;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.serializer.SerializationHandler;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class DOMAdapter implements DOM {

    // Mutually exclusive casting of DOM interface to known implementations
    private SAXImpl _saxImpl;

    private DOM _dom;

    private String[] _namesArray;
    private String[] _namespaceArray;

    // Cached mappings
    private short[] _mapping = null;
    private int[]   _reverse = null;
    private short[] _NSmapping = null;
    private short[] _NSreverse = null;

    private StripFilter _filter = null;

    private int _multiDOMMask;
    
    public DOMAdapter(DOM dom,
                      String[] namesArray,
                      String[] namespaceArray) {
        if (dom instanceof SAXImpl){
            _saxImpl = (SAXImpl) dom;
        }

        _dom = dom;
        _namesArray = namesArray;
        _namespaceArray = namespaceArray;
    }

    public void setupMapping(String[] names, String[] namespaces) {
        _namesArray = names;
        _namespaceArray = namespaces;
    }
    
    public String[] getNamesArray() {
        return _namesArray;
    }
    
    public String[] getNamespaceArray() {
        return _namespaceArray;
    }
    
    public DOM getDOMImpl() {
    	return _dom;
    }

    private short[] getMapping() {
        if (_mapping == null) {
            if (_saxImpl != null) {
                _mapping = _saxImpl.getMapping(_namesArray);
            } 
        }
        return _mapping;
    }

    private int[] getReverse() {
	if (_reverse == null) {
            if (_saxImpl != null) {
	        _reverse = _saxImpl.getReverseMapping(_namesArray);
            }
	}
	return _reverse;
    }

    private short[] getNSMapping() {
	if (_NSmapping == null) {
            if (_saxImpl != null) {
	        _NSmapping = _saxImpl.getNamespaceMapping(_namespaceArray);
            }
	}
	return _NSmapping;
    }

    private short[] getNSReverse() {
	if (_NSreverse == null) {
            if (_saxImpl != null) {
	        _NSreverse = _saxImpl.getReverseNamespaceMapping(_namespaceArray);
            }
	}
	return _NSreverse;
    }

    /** 
      * Returns singleton iterator containg the document root 
      */
    public DTMAxisIterator getIterator() {
        return _dom.getIterator();
    }
    
    public String getStringValue() {
        return _dom.getStringValue();
    }
    
    public DTMAxisIterator getChildren(final int node) {
        if (_saxImpl != null) {
            return _saxImpl.getChildren(node);
        }
        else {
            DTMAxisIterator iterator = _dom.getChildren(node);
            return iterator.setStartNode(node);
        }
    }

    public void setFilter(StripFilter filter) {
	_filter = filter;
    }

    public DTMAxisIterator getTypedChildren(final int type) {
        final int[] reverse = getReverse();

        if (_saxImpl != null) {
            return _saxImpl.getTypedChildren(reverse[type]);
        }
        else {
            return _dom.getTypedChildren(type);
        }      
    }

    public DTMAxisIterator getNamespaceAxisIterator(final int axis,
                                                    final int ns) {
        return _dom.getNamespaceAxisIterator(axis, getNSReverse()[ns]);
    }

    public DTMAxisIterator getAxisIterator(final int axis) {
        if (_saxImpl != null) {
            return _saxImpl.getAxisIterator(axis);
        }
        else {
            return _dom.getAxisIterator(axis);
        }        
    }
    
    public DTMAxisIterator getTypedAxisIterator(final int axis,
                                                final int type) {
        final int[] reverse = getReverse();

        if (axis == Axis.NAMESPACE) {
            short[] NSReverse = getNSReverse();
            if (type == NO_TYPE || type > NSReverse.length) {
                return _dom.getAxisIterator(axis);
            } else {
                return _dom.getTypedAxisIterator(axis, NSReverse[type]);
            }
        } else if (_saxImpl != null) {
            return _saxImpl.getTypedAxisIterator(axis, reverse[type]);
        } else {
            return _dom.getTypedAxisIterator(axis, type);
        }      
    }
        
    public int getMultiDOMMask() {
	return _multiDOMMask;
    }

    public void setMultiDOMMask(int mask) {
	_multiDOMMask = mask;
    }

    public DTMAxisIterator getNthDescendant(int type, int n,
                                            boolean includeself) {
        return _dom.getNthDescendant(getReverse()[type], n, includeself);
    }

    public DTMAxisIterator getNodeValueIterator(DTMAxisIterator iterator,
                                                int type, String value,
                                                boolean op) {
        return _dom.getNodeValueIterator(iterator, type, value, op);
    }

    public DTMAxisIterator orderNodes(DTMAxisIterator source, int node) {
        return _dom.orderNodes(source, node);
    }
    
    public int getExpandedTypeID(final int node) {
        if (_saxImpl != null) {
            return getMapping()[_saxImpl.getExpandedTypeID2(node)];
        }
        else {
            return getMapping()[_dom.getExpandedTypeID(node)];
        }
    }

    public int getNamespaceType(final int node) {
    	return getNSMapping()[_dom.getNSType(node)];
    }

    public int getNSType(int node) {
	return _dom.getNSType(node);
    }
    
    public int getParent(final int node) {
        return _dom.getParent(node);
    }

    public int getAttributeNode(final int type, final int element) {
	return _dom.getAttributeNode(getReverse()[type], element);
    }
    
    public String getNodeName(final int node) {
    	if (node == DTM.NULL) {
    	    return "";
    	}
        return _dom.getNodeName(node);
    }
    
    public String getNodeNameX(final int node) 
    {
    	if (node == DTM.NULL) {
    	    return "";
    	}
        return _dom.getNodeNameX(node);
    }

    public String getNamespaceName(final int node) 
    {
    	if (node == DTM.NULL) {
    	    return "";
    	}
        return _dom.getNamespaceName(node);
    }
    
    public String getStringValueX(final int node) 
    {    	
    	if (_saxImpl != null) {
            return _saxImpl.getStringValueX(node);
        }
        else {
            if (node == DTM.NULL) {
    	        return "";
    	    }
            return _dom.getStringValueX(node);
        }
    }
    
    public void copy(final int node, SerializationHandler handler)
	throws TransletException 
    {
        _dom.copy(node, handler);
    }
    
    public void copy(DTMAxisIterator nodes,SerializationHandler handler)
	throws TransletException 
    {
	_dom.copy(nodes, handler);
    }

    public String shallowCopy(final int node, SerializationHandler handler)
	throws TransletException 
    {
        if (_saxImpl != null) {
            return _saxImpl.shallowCopy(node, handler);
        }
        else {
            return _dom.shallowCopy(node, handler);
        }
    }
    
    public boolean lessThan(final int node1, final int node2) 
    {
        return _dom.lessThan(node1, node2);
    }
    
    public void characters(final int textNode, SerializationHandler handler)
      throws TransletException 
    {
        if (_saxImpl != null) {
            _saxImpl.characters(textNode, handler);
        }
        else {
            _dom.characters(textNode, handler);
        }
    }

    public Node makeNode(int index) 
    {
        return _dom.makeNode(index);
    }

    public Node makeNode(DTMAxisIterator iter) 
    {
        return _dom.makeNode(iter);
    }

    public NodeList makeNodeList(int index) 
    {
        return _dom.makeNodeList(index);
    }

    public NodeList makeNodeList(DTMAxisIterator iter) 
    {
        return _dom.makeNodeList(iter);
    }

    public String getLanguage(int node) 
    {
        return _dom.getLanguage(node);
    }

    public int getSize() 
    {
        return _dom.getSize();
    }

    public void setDocumentURI(String uri) 
    {
        if (_saxImpl != null) {
            _saxImpl.setDocumentURI(uri);
        }
    }

    public String getDocumentURI()
    {
        if (_saxImpl != null) {
            return _saxImpl.getDocumentURI();
        }
        else {
            return "";
        }
    }

    public String getDocumentURI(int node) 
    {
        return _dom.getDocumentURI(node);
    }

    public int getDocument() 
    {
        return _dom.getDocument();
    }

    public boolean isElement(final int node) 
    {
        return(_dom.isElement(node));
    }

    public boolean isAttribute(final int node) 
    {
        return(_dom.isAttribute(node));
    }
    
    public int getNodeIdent(int nodeHandle)
    {
    	return _dom.getNodeIdent(nodeHandle);
    }
    
    public int getNodeHandle(int nodeId)
    {
    	return _dom.getNodeHandle(nodeId);
    }
    
    /**
     * Return a instance of a DOM class to be used as an RTF
     */ 
    public DOM getResultTreeFrag(int initSize, int rtfType)
    {
    	if (_saxImpl != null) {
    	    return _saxImpl.getResultTreeFrag(initSize, rtfType);
    	}
    	else {
    	    return _dom.getResultTreeFrag(initSize, rtfType);
    	}
    }
    
    /**
     * Return a instance of a DOM class to be used as an RTF
     */ 
    public DOM getResultTreeFrag(int initSize, int rtfType, boolean addToManager)
    {
    	if (_saxImpl != null) {
    	    return _saxImpl.getResultTreeFrag(initSize, rtfType, addToManager);
    	}
    	else {
	    return _dom.getResultTreeFrag(initSize, rtfType, addToManager);
	}
    }
  
    
    /**
     * Returns a SerializationHandler class wrapped in a SAX adapter.
     */
    public SerializationHandler getOutputDomBuilder()
    {
    	return _dom.getOutputDomBuilder();
    }

    public String lookupNamespace(int node, String prefix) 
	throws TransletException 
    {
	return _dom.lookupNamespace(node, prefix);
    }

    public String getUnparsedEntityURI(String entity) {
        return _dom.getUnparsedEntityURI(entity);
    }

    public Hashtable getElementsWithIDs() {
        return _dom.getElementsWithIDs();
    }
}
