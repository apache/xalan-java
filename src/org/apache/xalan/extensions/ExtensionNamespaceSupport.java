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
package org.apache.xalan.extensions;

import java.lang.reflect.Constructor;

import javax.xml.transform.TransformerException;

/**
 * During styleseet composition, an ExtensionNamespaceSupport object is created for each extension 
 * namespace the stylesheet uses. At the beginning of a transformation, TransformerImpl generates
 * an ExtensionHandler for each of these objects and adds an entry to the ExtensionsTable hashtable.
 */
public class ExtensionNamespaceSupport
{
  // Namespace, ExtensionHandler class name, constructor signature 
  // and arguments.
  String m_namespace = null;
  String m_handlerClass = null;
  Class [] m_sig = null;  
  Object [] m_args = null;
 
  public ExtensionNamespaceSupport(String namespace, 
                                   String handlerClass, 
                                   Object[] constructorArgs)
  {
    m_namespace = namespace;
    m_handlerClass = handlerClass;
    m_args = constructorArgs;
    // Create the constructor signature.
    m_sig = new Class[m_args.length];
    for (int i = 0; i < m_args.length; i++)
    {
      if (m_args[i] != null)
        m_sig[i] = m_args[i].getClass();//System.out.println("arg class " + i + " " +m_sig[i]);
      else // If an arguments is null, pick the constructor later.
      {
        m_sig = null;
        break;
      }
    }
  }
  
  public String getNamespace()
  {
    return m_namespace;
  }
  
  /**
   * Launch the ExtensionHandler that this ExtensionNamespaceSupport object defines.
   */
  public ExtensionHandler launch()
    throws TransformerException
  {
    ExtensionHandler handler = null;
    try
    {
      Class cl = ExtensionHandler.getClassForName(m_handlerClass);
      Constructor con = null;
      //System.out.println("class " + cl + " " + m_args + " " + m_args.length + " " + m_sig);
      if (m_sig != null)
        con = cl.getConstructor(m_sig);
      else // Pick the constructor based on number of args.
      {
        Constructor[] cons = cl.getConstructors();
        for (int i = 0; i < cons.length; i ++)
        {
          if (cons[i].getParameterTypes().length == m_args.length)
          {
            con = cons[i];
            break;
          }
        }
      }
      // System.out.println("constructor " + con);
      if (con != null)
        handler = (ExtensionHandler)con.newInstance(m_args);
      else
        throw new TransformerException("ExtensionHandler constructor not found");
    }
    catch (Exception e)
    {
      throw new TransformerException(e);
    }
    return handler;
  }

}