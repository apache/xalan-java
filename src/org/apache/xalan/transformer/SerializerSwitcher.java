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
package org.apache.xalan.transformer;

import java.io.Writer;
import java.io.OutputStream;

import org.apache.xalan.templates.StylesheetRoot;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import org.apache.serialize.Serializer;
import org.apache.serialize.SerializerFactory;
import org.apache.serialize.Method;
import org.apache.serialize.OutputFormat;
import org.apache.xalan.templates.OutputFormatExtended;

/**
 * This is a helper class that decides if Xalan needs to switch
 * serializers, based on the first output element.
 */
public class SerializerSwitcher
{

  /**
   * NEEDSDOC Method switchSerializerIfHTML 
   *
   *
   * NEEDSDOC @param transformer
   * NEEDSDOC @param ns
   * NEEDSDOC @param localName
   *
   * @throws SAXException
   */
  public static void switchSerializerIfHTML(
          TransformerImpl transformer, String ns, String localName)
            throws SAXException
  {

    if (null == transformer)
      return;

    StylesheetRoot stylesheet = transformer.getStylesheet();

    if (null != stylesheet &&!stylesheet.isOutputMethodSet())
    {
      if (((null == ns) || (ns.length() == 0))
              && localName.equalsIgnoreCase("html"))
      {
        OutputFormat oformat = stylesheet.getOutputFormat();

        if (oformat instanceof OutputFormatExtended)
        {
          boolean methodHasBeeenSet =
            ((OutputFormatExtended) oformat).methodHasBeenSet();

          if (methodHasBeeenSet)
            return;
        }

        oformat.setMethod(Method.HTML);

        try
        {
          Serializer oldSerializer = transformer.getSerializer();

          if (null != oldSerializer)
          {
            Serializer serializer = SerializerFactory.getSerializer(oformat);
            Writer writer = oldSerializer.getWriter();

            if (null != writer)
              serializer.setWriter(writer);
            else
            {
              OutputStream os = serializer.getOutputStream();

              if (null != os)
                serializer.setOutputStream(os);
            }

            transformer.setSerializer(serializer);

            ContentHandler ch = serializer.asContentHandler();

            transformer.setContentHandler(ch);
          }
        }
        catch (java.io.IOException e)
        {
          throw new SAXException(e);
        }
      }
    }
  }
}
