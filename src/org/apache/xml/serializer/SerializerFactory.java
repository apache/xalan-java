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
package org.apache.xml.serializer;

import java.util.Hashtable;
import java.util.Properties;

import javax.xml.transform.OutputKeys;

import org.apache.xml.res.XMLErrorResources;
import org.apache.xml.res.XMLMessages;

import org.xml.sax.ContentHandler;

/**
 * Factory for creating serializers.
 */
public abstract class SerializerFactory
{
  /**
   * Associates output methods to default output formats.
   */
  private static Hashtable m_formats = new Hashtable();

  /**
   * Returns a serializer for the specified output method. 
   * If no implementation exists that supports the specified output method
   * an exception of some type will be thrown.
   * For a list of the default output methods see {@link Method}.
   *
   * @param format The output format, minimally the "method" property must be set.
   * @return A suitable serializer.
   * @throws IllegalArgumentException if method is
   * null or an appropriate serializer can't be found
   * @throws Exception if the class for the serializer is found but does not
   * implement ContentHandler.
   * @throws WrappedRuntimeException if an exception is thrown while trying to find serializer
   */
  public static Serializer getSerializer(Properties format)
  {
      Serializer ser;

      try
      {
        String method = format.getProperty(OutputKeys.METHOD);

        if (method == null)
          throw new IllegalArgumentException(
            "The output format has a null method name");

        String className =
            format.getProperty(OutputPropertiesFactory.S_KEY_CONTENT_HANDLER);


        if (null == className)
        {
            // Missing Content Handler property, load default using OutputPropertiesFactory
            Properties methodDefaults =
                OutputPropertiesFactory.getDefaultMethodProperties(method);
            className = 
            methodDefaults.getProperty(OutputPropertiesFactory.S_KEY_CONTENT_HANDLER);
                if (null == className)
                throw new IllegalArgumentException(
                    "The output format must have a '"
                    + OutputPropertiesFactory.S_KEY_CONTENT_HANDLER + "' property!");
        }



        ClassLoader loader = ObjectFactory.findClassLoader();

        Class cls = ObjectFactory.findProviderClass(className, loader, true);

        // _serializers.put(method, cls);

        Object obj = cls.newInstance();

        if (obj instanceof SerializationHandler)
        {
              // this is one of the supplied serializers
            ser = (Serializer) cls.newInstance();
            ser.setOutputFormat(format);
        }
        else
        {
              /*
               *  This  must be a user defined Serializer.
               *  It had better implement ContentHandler.
               */
               if (obj instanceof ContentHandler)
               {

                  /*
                   * The user defined serializer defines ContentHandler,
                   * but we need to wrap it with ToXMLSAXHandler which
                   * will collect SAX-like events and emit true
                   * SAX ContentHandler events to the users handler.
                   */
                  className = SerializerConstants.DEFAULT_SAX_SERIALIZER;
                  cls = ObjectFactory.findProviderClass(className, loader, true);
                  SerializationHandler sh =
                      (SerializationHandler) cls.newInstance();
                  sh.setContentHandler( (ContentHandler) obj);
                  sh.setOutputFormat(format);

                  ser = sh;
               }
               else
               {
                  // user defined serializer does not implement
                  // ContentHandler, ... very bad
                   throw new Exception(
                       XMLMessages.createXMLMessage(
                           XMLErrorResources.ER_SERIALIZER_NOT_CONTENTHANDLER,
                               new Object[] { className}));
               }

        }
      }
      catch (Exception e)
      {
        throw new org.apache.xml.utils.WrappedRuntimeException(e);
      }

      // If we make it to here ser is not null.
      return ser;
  }
}
