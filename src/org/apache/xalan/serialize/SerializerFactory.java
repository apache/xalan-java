/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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
package org.apache.xalan.serialize;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Properties;

import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

/**
 * Factory for creating serializers.
 * @deprecated The new class to use is 
 * org.apache.xml.serializer.SerializerFactory
 */
public abstract class SerializerFactory
{

    private SerializerFactory()
    {
    }
    /**
     * Returns a serializer for the specified output method. Returns
     * null if no implementation exists that supports the specified
     * output method. For a list of the default output methods see
     * {@link Method}.
     *
     * @param format The output format
     * @return A suitable serializer, or null
     * @throws IllegalArgumentException (apparently -sc) if method is
     * null or an appropriate serializer can't be found
     * @throws WrappedRuntimeException (apparently -sc) if an
     * exception is thrown while trying to find serializer
     * @deprecated Use org.apache.xml.serializer.SerializerFactory
     */
    public static Serializer getSerializer(Properties format)
    {
        org.apache.xml.serializer.Serializer ser;
        ser = org.apache.xml.serializer.SerializerFactory.getSerializer(format);
        SerializerFactory.SerializerWrapper si = new SerializerWrapper(ser);
        return si;

    }
    
    /**
     * This class just exists to wrap a new Serializer in the new package by
     * an old one.
     * @deprecated
     */

    private static class SerializerWrapper implements Serializer
    {
        private final org.apache.xml.serializer.Serializer m_serializer;
        private DOMSerializer m_old_DOMSerializer;

        SerializerWrapper(org.apache.xml.serializer.Serializer ser)
        {
            m_serializer = ser;

        }

        public void setOutputStream(OutputStream output)
        {
            m_serializer.setOutputStream(output);
        }

        public OutputStream getOutputStream()
        {
            return m_serializer.getOutputStream();
        }

        public void setWriter(Writer writer)
        {
            m_serializer.setWriter(writer);
        }

        public Writer getWriter()
        {
            return m_serializer.getWriter();
        }

        public void setOutputFormat(Properties format)
        {
            m_serializer.setOutputFormat(format);
        }

        public Properties getOutputFormat()
        {
            return m_serializer.getOutputFormat();
        }

        public ContentHandler asContentHandler() throws IOException
        {
            return m_serializer.asContentHandler();
        }

        /**
         * @return an old style DOMSerializer that wraps a new one.
         * @see org.apache.xalan.serialize.Serializer#asDOMSerializer()
         */
        public DOMSerializer asDOMSerializer() throws IOException
        {
            if (m_old_DOMSerializer == null)
            {
                m_old_DOMSerializer =
                    new DOMSerializerWrapper(m_serializer.asDOMSerializer());
            }
            return m_old_DOMSerializer;
        }
        /**
         * @see org.apache.xalan.serialize.Serializer#reset()
         */
        public boolean reset()
        {
            return m_serializer.reset();
        }

    }

    /**
     * This class just wraps a new DOMSerializer with an old style one for
     * migration purposes. 
  *
     */
    private static class DOMSerializerWrapper implements DOMSerializer
    {
        private final org.apache.xml.serializer.DOMSerializer m_dom;
        DOMSerializerWrapper(org.apache.xml.serializer.DOMSerializer domser)
        {
            m_dom = domser;
        }

        public void serialize(Node node) throws IOException
        {
            m_dom.serialize(node);
        }
    }

}
