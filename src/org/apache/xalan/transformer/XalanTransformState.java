/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights reserved.
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
 * 4. The names "Xerces" and "Apache Software Foundation" must
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
 * originally based on software copyright (c) 2003, International Business
 * Machines, Inc., http://www.ibm.com.  For more information on the Apache
 * Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.xalan.transformer;

import javax.xml.transform.Transformer;

import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

/**
 * Before the serializer merge, the TransformState interface was
 * implemented by ResultTreeHandler.
 */
public class XalanTransformState
    implements TransformState {
        
    Node m_node = null;
    ElemTemplateElement m_currentElement = null;
    ElemTemplate m_currentTemplate = null;
    ElemTemplate m_matchedTemplate = null;
    int m_currentNodeHandle = DTM.NULL;
    Node m_currentNode = null;
    int m_matchedNode = DTM.NULL;
    DTMIterator m_contextNodeList = null;
    boolean m_elemPending = false;    
    TransformerImpl m_transformer = null;

    /**
     * @see org.apache.xml.serializer.SerializerTransformState#setCurrentNode(Node)
     */
    public void setCurrentNode(Node n) {
        m_node = n;
    }

    /**
     * @see org.apache.xml.serializer.SerializerTransformState#resetState(Transformer)
     */
    public void resetState(Transformer transformer) {
        if ((transformer != null) && (transformer instanceof TransformerImpl)) {
           m_transformer = (TransformerImpl)transformer;
           m_currentElement = m_transformer.getCurrentElement();
           m_currentTemplate = m_transformer.getCurrentTemplate();
           m_matchedTemplate = m_transformer.getMatchedTemplate();
           int currentNodeHandle = m_transformer.getCurrentNode();
           DTM dtm = m_transformer.getXPathContext().getDTM(currentNodeHandle);
           m_currentNode = dtm.getNode(currentNodeHandle);
           m_matchedNode = m_transformer.getMatchedNode();
           m_contextNodeList = m_transformer.getContextNodeList();    
        }       
    }

    /**
     * @see org.apache.xalan.transformer.TransformState#getCurrentElement()
     */
    public ElemTemplateElement getCurrentElement() {
      if (m_elemPending)
         return m_currentElement;
      else
         return m_transformer.getCurrentElement();
    }

    /**
     * @see org.apache.xalan.transformer.TransformState#getCurrentNode()
     */
    public Node getCurrentNode() {
      if (m_currentNode != null) {
         return m_currentNode;
      } else {
         DTM dtm = m_transformer.getXPathContext().getDTM(m_transformer.getCurrentNode());
         return dtm.getNode(m_transformer.getCurrentNode());
      }
    }
    
    /**
     * @see org.apache.xalan.transformer.TransformState#getCurrentTemplate()
     */
    public ElemTemplate getCurrentTemplate() {
       if (m_elemPending)
         return m_currentTemplate;
       else
         return m_transformer.getCurrentTemplate();
    }

    /**
     * @see org.apache.xalan.transformer.TransformState#getMatchedTemplate()
     */
    public ElemTemplate getMatchedTemplate() {
      if (m_elemPending)
         return m_matchedTemplate;
      else
         return m_transformer.getMatchedTemplate();
    }

    /**
     * @see org.apache.xalan.transformer.TransformState#getMatchedNode()
     */
    public Node getMatchedNode() {
 
       if (m_elemPending) {
         DTM dtm = m_transformer.getXPathContext().getDTM(m_matchedNode);
         return dtm.getNode(m_matchedNode);
       } else {
         DTM dtm = m_transformer.getXPathContext().getDTM(m_transformer.getMatchedNode());
         return dtm.getNode(m_transformer.getMatchedNode());
       }
    }

    /**
     * @see org.apache.xalan.transformer.TransformState#getContextNodeList()
     */
    public NodeIterator getContextNodeList() {
      if (m_elemPending) {
          return new org.apache.xml.dtm.ref.DTMNodeIterator(m_contextNodeList);
      } else {
          return new org.apache.xml.dtm.ref.DTMNodeIterator(m_transformer.getContextNodeList());
      }
    }
    /**
     * @see org.apache.xalan.transformer.TransformState#getTransformer()
     */
    public Transformer getTransformer() {
        return m_transformer;
    }

}
