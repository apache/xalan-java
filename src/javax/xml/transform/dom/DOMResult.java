/*
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
 * 4. The name "Apache Software Foundation" must not be used to endorse or
 *    promote products derived from this software without prior written
 *    permission. For written permission, please contact apache@apache.org.
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
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package javax.xml.transform.dom;

import javax.xml.transform.*;

import java.lang.String;

import java.io.OutputStream;
import java.io.Writer;

import org.w3c.dom.Node;


/**
 * Acts as a holder for a transformation result tree, in the
 * form of a Document Object Model (DOM) tree. If no output DOM source is set,
 * the transformation will create a Document node as the holder
 * for the result of the transformation, which may be retrieved
 * with getNode.
 */
public class DOMResult implements Result {

    /** If {@link javax.xml.transform.TransformerFactory#getFeature}
     * returns true when passed this value as an argument,
     * the Transformer supports Result output of this type.
     */
    public static final String FEATURE =
        "http://javax.xml.transform.dom.DOMResult/feature";

    /**
     * Zero-argument default constructor.
     */
    public DOMResult() {}

    /**
     * Use a DOM node to create a new output target. In practice,
     * the node should be a {@link org.w3c.dom.Document} node,
     * a {@link org.w3c.dom.DocumentFragment} node, or a
     * {@link org.w3c.dom.Element} node.  In other words, a node
     * that accepts children.
     *
     * @param n The DOM node that will contain the result tree.
     */
    public DOMResult(Node node) {
        setNode(node);
    }

    /**
     * Create a new output target with a DOM node. In practice,
     * the node should be a {@link org.w3c.dom.Document} node,
     * a {@link org.w3c.dom.DocumentFragment} node, or a
     * {@link org.w3c.dom.Element} node.  In other words, a node
     * that accepts children.
     *
     * @param node The DOM node that will contain the result tree.
     * @param systemID The system identifier which may be used in association
     * with this node.
     */
    public DOMResult(Node node, String systemID) {
        setNode(node);
        setSystemId(systemID);
    }

    /**
     * Set the node that will contain the result DOM tree.  In practice,
     * the node should be a {@link org.w3c.dom.Document} node,
     * a {@link org.w3c.dom.DocumentFragment} node, or a
     * {@link org.w3c.dom.Element} node.  In other words, a node
     * that accepts children.
     *
     * @param node The node to which the transformation
     * will be appended.
     */
    public void setNode(Node node) {
        this.node = node;
    }

    /**
     * Get the node that will contain the result DOM tree.
     * If no node was set via setNode, the node will be
     * set by the transformation, and may be obtained from
     * this method once the transformation is complete.
     *
     * @return The node to which the transformation
     * will be appended.
     */
    public Node getNode() {
        return node;
    }

    /**
     * Method setSystemId Set the systemID that may be used in association
     * with the node.
     *
     * @param systemId The system identifier as a URI string.
     */
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    /**
     * Get the system identifier that was set with setSystemId.
     *
     * @return The system identifier that was set with setSystemId, or null
     * if setSystemId was not called.
     */
    public String getSystemId() {
        return systemId;
    }

    //////////////////////////////////////////////////////////////////////
    // Internal state.
    //////////////////////////////////////////////////////////////////////

    /**
     * The node to which the transformation will be appended.
     */
    private Node node;

    /**
     * The systemID that may be used in association
     * with the node.
     */
    private String systemId;
}
