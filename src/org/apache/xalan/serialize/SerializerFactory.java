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
package org.apache.xalan.serialize;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

import java.io.IOException;
import java.io.InputStream;

/**
 * Factory for creating default serializers. An implementation need
 * only support the default output methods (XML, HTML and Text).
 * Additional serializers may be constructed directly by the application.
 * <p>
 * The factory is used with the default serializers provided by the
 * implementation and named in the <tt>serializer.properties</tt>
 * file of the implementation.
 * <p>
 * Usage example:
 * <pre>
 * Serializer ser;
 *
 * ser = SerializerFactory.getSerializer( Method.XML );
 * </pre>
 * or,
 * <pre>
 * Serializer   ser;
 * OutputFormat format;
 *
 * format = SerializerFactory.getOutputFormat( Method.HTML );
 * ser = SerializerFactory.getSerializer( format );
 * </pre>
 * <p>
 *
 *
 *
 * @version Alpha
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 */
public abstract class SerializerFactory
{

  /**
   * The name of the properties file listing all the supported
   * serializers. (<tt>/org/xml/serilize/serializer.properties</tt>).
   */
  public static final String PropertiesResource = "serializer.properties";

  /**
   * The name of the property listing all the supported output
   * methods. Contains a comma delimited list of method names.
   * (<tt>serialize.methods</tt>).
   */
  public static final String PropertyMethods = "serialize.methods";

  /**
   * The prefix of a property supplying the class name for a
   * serializer implementing a specific method.
   * (<tt>serialize.</tt>).
   */
  public static final String PropertySerializerPrefix = "serialize.";

  /**
   * The prefix of a property supplying the class name for an
   * output format implementing a specific method.
   * (<tt>serialize.format.</tt>).
   */
  public static final String PropertyFormatPrefix = "serialize.format.";

  /**
   * Associates output methods to serializer classes.
   */
  private static Hashtable _serializers = new Hashtable();

  /**
   * Associates output methods to default output formats.
   */
  private static Hashtable _formats = new Hashtable();

  /**
   * Returns a serializer for the specified output method. Returns
   * null if no implementation exists that supports the specified
   * output method. For a list of the default output methods see
   * {@link Method}.
   *
   * @param method The output method
   * @return A suitable serializer, or null
   */
  public static Serializer getSerializer(String method)
  {

    Serializer ser;
    Class cls;

    cls = (Class) _serializers.get(method);

    if (cls == null)
      return null;

    try
    {
      ser = (Serializer) cls.newInstance();
    }
    catch (Exception except)
    {
      return null;
    }

    return ser;
  }

  /**
   * Returns a serializer for the specified output method. Returns
   * null if no implementation exists that supports the specified
   * output method. For a list of the default output methods see
   * {@link Method}.
   *
   * @param format The output format
   * @return A suitable serializer, or null
   */
  public static Serializer getSerializer(OutputFormat format)
  {

    Serializer ser;
    Class cls;

    if (format.getMethod() == null)
      throw new IllegalArgumentException(
        "The output format has not method name");

    cls = (Class) _serializers.get(format.getMethod());

    if (cls == null)
      return null;

    try
    {
      ser = (Serializer) cls.newInstance();
    }
    catch (Exception except)
    {
      return null;
    }

    ser.setOutputFormat(format);

    return ser;
  }

  /**
   * Returns an output format for the specified output method.
   * An implementation may extend {@link OutputFormat} to provide
   * additional properties.
   *
   * @param method The output method
   * @return A suitable output format
   */
  public static OutputFormat getOutputFormat(String method)
  {

    OutputFormat format;
    Class cls;

    cls = (Class) _formats.get(method);

    if (cls != null)
    {
      try
      {
        format = (OutputFormat) cls.newInstance();

        return format;
      }
      catch (Exception except){}
    }

    format = new OutputFormat();

    format.setMethod(method);

    return format;
  }

  /**
   * Returns an enumeration of all the output methods supported by this
   * implementation. The enumeration contains the names of all the output
   * methods for which this implementation provides a serializer.
   *
   * @return An enumeration of all output methods
   */
  public Enumeration listMethods()
  {
    return _serializers.keys();
  }

  /**
   * Static constructor loads serializers and output formats
   * from properties file.
   */
  static
  {
    Properties props;
    StringTokenizer token;

    try
    {
      props = new Properties();

      InputStream is =
        SerializerFactory.class.getResourceAsStream(PropertiesResource);

      if (null == is)
        System.err.println("Can't get serializer property file: "
                           + PropertiesResource);

      props.load(is);

      if (props.getProperty(PropertyMethods) == null)
        System.err.println("Serializer property file has no "
                           + PropertyMethods + " property");
      else
      {
        token = new StringTokenizer(props.getProperty(PropertyMethods), ", ");

        while (token.hasMoreElements())
        {
          String method;
          String clsName;
          Class cls;

          method = token.nextToken();

          // Get the serializer class that matches this output method
          clsName = props.getProperty(PropertySerializerPrefix + method);

          if (clsName == null)
          {
            System.err.println(
              "Could not find property for serializer implementing output method "
              + method);
          }
          else
          {
            try
            {
              cls = Class.forName(clsName);

              // Breaks in jview -sb
              // cls = SerializerFactory.class.getClassLoader().loadClass( clsName );
              _serializers.put(method, cls);
            }
            catch (ClassNotFoundException except)
            {
              System.err.println("Could not locate serializer class "
                                 + clsName);
            }
          }

          // Get the output format class that matches this output method
          clsName = props.getProperty(PropertyFormatPrefix + method);

          if (clsName == null)
          {

            // Don't complain in this case.  -sb
            // System.err.println( "Could not find property for output format implementing output method " + method );
          }
          else
          {
            try
            {
              cls = Class.forName(clsName);

              // Breaks in jview -sb
              // cls = SerializerFactory.class.getClassLoader().loadClass( clsName );
              _formats.put(method, cls);
            }
            catch (ClassNotFoundException except)
            {
              System.err.println("Could not locate output format class "
                                 + clsName);
            }
          }
        }
      }
    }
    catch (IOException except)
    {
      System.err.println("Error loading " + PropertiesResource + ": "
                         + except.toString());
    }
  }
}
