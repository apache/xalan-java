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
 * originally based on software copyright (c) 2003, International Business
 * Machines, Inc., http://www.ibm.com.  For more information on the Apache
 * Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xml.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Properties;
import java.util.Vector;

import javax.xml.transform.Transformer;

import org.apache.xml.serializer.Serializer;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;

/**
 * This interface is the one that a serializer implements. It is a group of
 * other interfaces, such as ExtendedContentHandler, ExtendedLexicalHandler etc.
 * In addition there are other methods, such as reset().
 */
public interface SerializationHandler
    extends
        ExtendedContentHandler,
        ExtendedLexicalHandler,
        XSLOutputAttributes,
        DeclHandler,
        ErrorHandler,
        DOMSerializer,
        Serializer
{
    /**
     * Set the SAX Content handler that the serializer sends its output to. This
     * method only applies to a ToSAXHandler, not to a ToStream serializer.
     * 
     * @see org.apache.xml.serializer.Serializer#asContentHandler()
     * @see org.apache.xml.serializer.ToSAXHandler
     */
    public void setContentHandler(ContentHandler ch);
    
    public void close();

    /**
     * Notify that the serializer should take this DOM node as input to be
     * serialized.
     * 
     * @param node the DOM node to be serialized.
     * @throws IOException
     */
    public void serialize(Node node) throws IOException;
    /**
     * Turns special character escaping on/off. 
     * 
     * Note that characters will
     * never, even if this option is set to 'true', be escaped within
     * CDATA sections in output XML documents.
     * 
     * @param true if escaping is to be set on.
     */
    public boolean setEscaping(boolean escape) throws SAXException;

    /**
     * Set the number of spaces to indent for each indentation level.
     * @param spaces the number of spaces to indent for each indentation level.
     */
    public void setIndentAmount(int spaces);

    /**
     * Set the transformer associated with the serializer.
     * @param transformer the transformer associated with the serializer.
     */
    public void setTransformer(Transformer transformer);
    
    /**
     * Get the transformer associated with the serializer.
     * @return Transformer the transformer associated with the serializer.
     */
    public Transformer getTransformer();

    /** 
     * Used only by TransformerSnapshotImpl to restore the serialization 
     * to a previous state. 
     * 
     * @param NamespaceMappings
     */
    public void setNamespaceMappings(NamespaceMappings mappings);

    /**
     * Flush any pending events currently queued up in the serializer. This will
     * flush any input that the serializer has which it has not yet sent as
     * output.
     */
    public void flushPending();


}
