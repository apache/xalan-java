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
package org.apache.xalan.dtm;
import org.w3c.dom.*;
import java.util.Vector;

/** 
 * <meta name="usage" content="internal"/>
 * DTMProxyMap is a quickie (as opposed to quick) implementation of the DOM's
 * NamedNodeMap interface, intended to support DTMProxy's getAttributes()
 * call. 
 * <p>
 * ***** Note: this does _not_ current attempt to cache any of the data;
 * if you ask for attribute 27 and then 28, you'll have to rescan the first
 * 27. It should probably at least keep track of the last one retrieved,
 * and possibly buffer the whole array.
 * <p>
 * ***** Also note that there's no fastpath for the by-name query; we search
 * linearly until we find it or fail to find it. Again, that could be
 * optimized at some cost in object creation/storage.
 */
public class DTMProxyMap
implements NamedNodeMap
{
    DTM dtm;
    int element;
    
    /** Create a getAttributes NamedNodeMap for a given DTM element node */
    DTMProxyMap(DTM dtm,int element)
    {
        this.dtm=dtm;
        this.element=element;
    }
    
    /** Return the number of Attributes on this Element */
    public int getLength()
    {
        int count=0;
        for(int n=dtm.getNextAttribute(element);
            n!=-1;
            n=dtm.getNextAttribute(n))
        {
            ++count;
        }
        return count;           
    }
    
    /** Return the Attr node having a specific name */
    public Node getNamedItem(String name)
    {
        for(int n=dtm.getNextAttribute(element);
            n!=-1;
            n=dtm.getNextAttribute(n))
        {
            if(dtm.getNodeName(n).equals(name))
                return dtm.getNode(n);
        }
        return null;
    }
    
    /** Return the i'th Attr node bound to this element */ 
    public Node item(int i)
    {
        int count=0;
        for(int n=dtm.getNextAttribute(element);
            n!=-1;
            n=dtm.getNextAttribute(n))
        {
            if(count == i)
                return dtm.getNode(n);
            else
                ++count;
        }
        return null;
    }
    
    /** DOM API requires this, but DTM is a read-only model */
    public Node setNamedItem(Node newNode)
    {
        throw new DTMException(DTMException.NO_MODIFICATION_ALLOWED_ERR);
    }
    
    /** DOM API requires this, but DTM is a read-only model */ 
    public Node removeNamedItem(String name)
    {
        throw new DTMException(DTMException.NO_MODIFICATION_ALLOWED_ERR);
    }
    
    /** Retrieve a node specified by local name and namespace URI -- DOMLevel 2 */
    public Node getNamedItemNS(String namespaceURI, 
                               String localName)
    {
        throw new DTMException(DTMException.NOT_SUPPORTED_ERR);
    }
 
    /** DOM 2 API requires this, but DTM is a read-only model */
    public Node setNamedItemNS(Node arg)
      throws DOMException
    {
        throw new DTMException(DTMException.NO_MODIFICATION_ALLOWED_ERR);
    }

    /** DOM 2 API requires this, but DTM is a read-only model */
    public Node removeNamedItemNS(String namespaceURI, 
                                  String localName)
      throws DOMException
    {
        throw new DTMException(DTMException.NO_MODIFICATION_ALLOWED_ERR);
    }
    

}