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
package org.apache.xpath.compiler; 

import java.lang.Class;
import org.apache.xpath.res.XPATHErrorResources;
import org.w3c.dom.Node;
import java.util.Vector;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPath;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.functions.Function;

/**
 * <meta name="usage" content="advanced"/>
 * Load functions in function table as needed
 */
public class FuncLoader
{
  private int m_funcID;
  private String m_funcName, test;
  
  public String getName()
  {
    return m_funcName;
  }
  
  public FuncLoader(String funcName, int funcID)
  {
    super();
    m_funcID = funcID;
    m_funcName = funcName;
  }
  
  public Function getFunction() 
    throws org.xml.sax.SAXException
  {
    try
    {
      Class function;
      // first get package name if necessary
      if ( m_funcName.indexOf(".") < 0 )
      {  
        // String thisName = this.getClass().getName();
        // int lastdot = thisName.lastIndexOf(".");
        // String classname = thisName.substring(0,lastdot+1) + m_funcName; 
        String classname = "org.apache.xpath.functions." + m_funcName; 
        function = Class.forName(classname);
      }
      else
        function = Class.forName(m_funcName);
      
      Function func = (Function)function.newInstance();
      return func;
    }
    catch(ClassNotFoundException e)
    {
      throw new org.xml.sax.SAXException(e);
    }
    catch(IllegalAccessException e)
    {
      throw new org.xml.sax.SAXException(e);
    } 
    catch(InstantiationException e)
    {
      throw new org.xml.sax.SAXException(e);
    } 
  }
}
