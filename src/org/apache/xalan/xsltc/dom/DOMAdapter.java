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
 * @author Morten Jorgensen
 *
 */

package org.apache.xalan.xsltc.dom;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.StripFilter;
import org.apache.xalan.xsltc.NodeIterator;
import org.apache.xalan.xsltc.TransletOutputHandler;
import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.runtime.Hashtable;

import org.apache.xml.dtm.*;
import org.apache.xml.dtm.ref.*;

public final class DOMAdapter implements DOM {
    private final DOM _domImpl;
    private short[] _mapping;
    private int[] _reverse;
    private short[] _NSmapping;
    private short[] _NSreverse;

    private StripFilter _filter = null;

    private int _multiDOMMask;
    
    public DOMAdapter(DOM dom,
                      String[] namesArray,
                      String[] namespaceArray) 
    {
      _domImpl = dom;
      if (_domImpl instanceof DOMImpl )
      {
        _mapping = ((DOMImpl)dom).getMapping(namesArray);
        _reverse = ((DOMImpl)dom).getReverseMapping(namesArray);
        _NSmapping = ((DOMImpl)dom).getNamespaceMapping(namespaceArray);
        _NSreverse = ((DOMImpl)dom).getReverseNamespaceMapping(namespaceArray);
      }
      else
      {
      	 _mapping = ((SAXImpl)dom).getMapping(namesArray);
         _reverse = ((SAXImpl)dom).getReverseMapping(namesArray);
         _NSmapping = ((SAXImpl)dom).getNamespaceMapping(namespaceArray);
         _NSreverse = ((SAXImpl)dom).getReverseNamespaceMapping(namespaceArray);
      }
    }

    public void setupMapping(String[] names, String[] namespaces) 
    {
      if (_domImpl instanceof DOMImpl )
       {
          _mapping = ((DOMImpl)_domImpl).getMapping(names);
          _reverse = ((DOMImpl)_domImpl).getReverseMapping(names);
          _NSmapping = ((DOMImpl)_domImpl).getNamespaceMapping(namespaces);
          _NSreverse = ((DOMImpl)_domImpl).getReverseNamespaceMapping(namespaces);
       }
      else
       {
          _mapping = ((SAXImpl)_domImpl).getMapping(names);
          _reverse = ((SAXImpl)_domImpl).getReverseMapping(names);
          _NSmapping = ((SAXImpl)_domImpl).getNamespaceMapping(namespaces);
          _NSreverse = ((SAXImpl)_domImpl).getReverseNamespaceMapping(namespaces);
       }
    }
    
    
    public DOM getDOMImpl()
    {
    	return _domImpl;
    }

    /** 
      * Returns singleton iterator containg the document root 
      */
    public DTMAxisIterator getIterator() {
      return _domImpl.getIterator();
    }
    
    public String getStringValue() {
      return _domImpl.getStringValue();
    }
    
    public DTMAxisIterator getChildren(final int node) {
      DTMAxisIterator iterator = _domImpl.getChildren(node);
      if (_filter == null) {
        return(iterator.setStartNode(node));
      }
      else {
      	if (_domImpl instanceof DOMImpl )
          iterator = ((DOMImpl)_domImpl).strippingIterator(iterator,_mapping,_filter);
       else
          iterator = ((SAXImpl)_domImpl).strippingIterator(iterator,_mapping,_filter);
        return iterator.setStartNode(node);
      }
    }

    public void setFilter(StripFilter filter) {
      _filter = filter;
    }
    
    public DTMAxisIterator getTypedChildren(final int type) {
      DTMAxisIterator iterator = _domImpl.getTypedChildren(_reverse[type]);
      if (_reverse[type] == DTM.TEXT_NODE && _filter != null)
      {
      	if (_domImpl instanceof DOMImpl )
          iterator = ((DOMImpl)_domImpl).strippingIterator(iterator,_mapping,_filter);
       else
          iterator = ((SAXImpl)_domImpl).strippingIterator(iterator,_mapping,_filter);
      }
      return iterator;
    }

    public DTMAxisIterator getNamespaceAxisIterator(final int axis, final int ns) {
      return _domImpl.getNamespaceAxisIterator(axis,_NSreverse[ns]);
    }

    public DTMAxisIterator getAxisIterator(final int axis) {
      DTMAxisIterator iterator = _domImpl.getAxisIterator(axis);
      if (_filter != null)
      {
        return (_domImpl instanceof DOMImpl)
             ? ((DOMImpl)_domImpl).strippingIterator(iterator,_mapping,_filter)
             : ((SAXImpl)_domImpl).strippingIterator(iterator,_mapping,_filter);
      }
      return iterator;
    }
    
    public DTMAxisIterator getTypedAxisIterator(final int axis, final int type) {
      DTMAxisIterator iterator;

      if (axis == Axis.NAMESPACE) 
      {
        iterator = (type == NO_TYPE || type > _NSreverse.length)
             ? _domImpl.getAxisIterator(axis)
             : _domImpl.getTypedAxisIterator(axis,_NSreverse[type]);
      }
      else {
        iterator = _domImpl.getTypedAxisIterator(axis, _reverse[type]);
      }
      
      if (_reverse[type] == DTM.TEXT_NODE && _filter != null)
      {
      	if (_domImpl instanceof DOMImpl )
          iterator = ((DOMImpl)_domImpl).strippingIterator(iterator,_mapping,_filter);
       else
          iterator = ((SAXImpl)_domImpl).strippingIterator(iterator,_mapping,_filter);
        
      }
      return iterator;
    }
    

    public String getTreeString() {
	return _domImpl.getTreeString();
    }
    
    public int getMultiDOMMask() {
	return _multiDOMMask;
    }

    public void setMultiDOMMask(int mask) {
	_multiDOMMask = mask;
    }

    public DTMAxisIterator getNthDescendant(int type, int n, boolean includeself) 
    {
      return _domImpl.getNthDescendant(_reverse[type], n, includeself);
    }

    public DTMAxisIterator getNodeValueIterator(DTMAxisIterator iterator, int type,
                                                String value, boolean op) 
    {
      return _domImpl.getNodeValueIterator(iterator, type, value, op);
    }


    public DTMAxisIterator orderNodes(DTMAxisIterator source, int node) 
    {
      return _domImpl.orderNodes(source, node);
    }
    
    public int getType(final int node) 
    {
      return _mapping[_domImpl.getType(node)];
    }

    public int getNamespaceType(final int node) 
    {
    	
    	return _NSmapping[_domImpl.getNSType(node)];
    }
    
    public int getNSType(int node)
    {
	return _domImpl.getNSType(node);
    }
    
    public int getParent(final int node) 
    {
      return _domImpl.getParent(node);
    }

    public int getTypedPosition(int type, int node) 
    {
      return _domImpl.getTypedPosition(_reverse[type], node);
    }

    public int getTypedLast(int type, int node) 
    {
      return _domImpl.getTypedLast(_reverse[type], node);
    }

    public int getAttributeNode(final int type, final int element) 
    {
      return _domImpl.getAttributeNode(_reverse[type], element);
    }
    
    public String getNodeName(final int node) 
    {
    	if (node == DTM.NULL)
    	return "";
      return _domImpl.getNodeName(node);
    }
    
    public String getNodeNameX(final int node) 
    {
    	if (node == DTM.NULL)
    	return "";
      return _domImpl.getNodeNameX(node);
    }

    public String getNamespaceName(final int node) 
    {
    	if (node == DTM.NULL)
    	return "";
      return _domImpl.getNamespaceName(node);
    }
    
    public String getNodeValue(final int node) 
    {
    	if (node == DTM.NULL)
    	return "";
      return _domImpl.getNodeValue(node);
    }
    
    public void copy(final int node, TransletOutputHandler handler)
	throws TransletException 
    {
	    _domImpl.copy(node, handler);
    }
    
    public void copy(DTMAxisIterator nodes, TransletOutputHandler handler)
	throws TransletException 
    {
	    _domImpl.copy(nodes, handler);
    }

    public String shallowCopy(final int node, TransletOutputHandler handler)
	throws TransletException 
    {
	    return _domImpl.shallowCopy(node, handler);
    }
    
    public boolean lessThan(final int node1, final int node2) 
    {
      return _domImpl.lessThan(node1, node2);
    }
    
    public void characters(final int textNode, TransletOutputHandler handler)
      throws TransletException 
    {
      _domImpl.characters(textNode, handler);
    }

    public Node makeNode(int index) 
    {
      return _domImpl.makeNode(index);
    }

    public Node makeNode(DTMAxisIterator iter) 
    {
      return _domImpl.makeNode(iter);
    }

    public NodeList makeNodeList(int index) 
    {
      return _domImpl.makeNodeList(index);
    }

    public NodeList makeNodeList(DTMIterator iter) 
    {
      return _domImpl.makeNodeList(iter);
    }

    public String getLanguage(int node) 
    {
      return _domImpl.getLanguage(node);
    }

    public int getSize() 
    {
      return _domImpl.getSize();
    }

    public void setDocumentURI(String uri) 
    {
      if (_domImpl instanceof DOMImpl )
        ((DOMImpl)_domImpl).setDocumentURI(uri);
      else
        ((SAXImpl)_domImpl).setDocumentURI(uri);
    }

    public String getDocumentURI() 
    {
      if (_domImpl instanceof DOMImpl )
        return(((DOMImpl)_domImpl).getDocumentURI());
      else
        return(((SAXImpl)_domImpl).getDocumentURI());
    }

    public String getDocumentURI(int node) 
    {
      if (_domImpl instanceof DOMImpl)
        return(((DOMImpl)_domImpl).getDocumentURI());
      else
        return(((SAXImpl)_domImpl).getDocumentURI());
    }
    
    public int getDocument() 
    {
      return(((DTMDefaultBase)_domImpl).getDocument());
    }

    public boolean isElement(final int node) 
    {
      return(_domImpl.isElement(node));
    }

    public boolean isAttribute(final int node) 
    {
      return(_domImpl.isAttribute(node));
    }
    
    public int getNodeIdent(int nodeHandle)
    {
    	return _domImpl.getNodeIdent(nodeHandle);
    }
    
    public int getNodeHandle(int nodeId)
    {
    	return _domImpl.getNodeHandle(nodeId);
    }
    
    /**
     * Return a instance of a DOM class to be used as an RTF
     */ 
    public DOM getResultTreeFrag()
    {
    	return _domImpl.getResultTreeFrag();
    }
    
    /**
     * Returns a DOMBuilder class wrapped in a SAX adapter.
     */
    public TransletOutputHandler getOutputDomBuilder()
    {
    	return _domImpl.getOutputDomBuilder();
    }

    public String lookupNamespace(int node, String prefix) 
	throws TransletException 
    {
	return _domImpl.lookupNamespace(node, prefix);
    }

    public String getUnparsedEntityURI(String entity) {
        return _domImpl.getUnparsedEntityURI(entity);
    }

    public Hashtable getElementsWithIDs() {
        return _domImpl.getElementsWithIDs();
    }
}
