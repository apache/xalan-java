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
 *     the documentation and/or other materials provided with the
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

import java.util.Hashtable;
import java.util.Vector;
import org.apache.xpath.objects.XNull;
import org.apache.xpath.XPathProcessorException;
import org.w3c.xslt.ExpressionContext;

public class ExtensionsTable
{
  /**
   * <meta name="usage" content="internal"/>
   * Table of extensions that may be called from the expression language 
   * via the call(name, ...) function.  Objects are keyed on the call 
   * name.
   * @see extensions.html.
   */
  public Hashtable m_extensionFunctionNamespaces = new Hashtable();
  
  /**
   * Get an ExtensionNSHandler object that represents the 
   * given namespace.
   * @param extns A valid extension namespace.
   */
  public ExtensionNSHandler get(String extns)
  {
    return (ExtensionNSHandler)m_extensionFunctionNamespaces.get(extns);
  }
  
  /**
   * <meta name="usage" content="advanced"/>
   * Register an extension namespace handler. This handler provides
   * functions for testing whether a function is known within the 
   * namespace and also for invoking the functions.
   *
   * @param uri the URI for the extension.
   * @param extNS the extension handler.
   */
  public void addExtensionNamespace (String uri,
                                     ExtensionFunctionHandler extNS) {
    m_extensionFunctionNamespaces.put (uri, extNS);
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Register an element extension namespace handler. This handler provides
   * functions for testing whether a function is known within the 
   * namespace and also for invoking the functions.
   *
   * @param uri the URI for the extension.
   * @param extNS the extension handler.
   */
  public void addExtensionElementNamespace (String uri,
                                            ExtensionFunctionHandler extNS) {
    m_extensionFunctionNamespaces.put (uri, extNS);
  }

  /**
   * Execute the function-available() function.
   * @param ns       the URI of namespace in which the function is needed
   * @param funcName the function name being tested
   *
   * @return whether the given function is available or not.
   */
  public boolean functionAvailable (String ns, String funcName) 
  {
    boolean isAvailable = false;
    if (null != ns) 
    {
      ExtensionFunctionHandler extNS = 
                                      (ExtensionFunctionHandler) m_extensionFunctionNamespaces.get (ns);
      if (extNS != null) 
      {
        isAvailable = extNS.isFunctionAvailable (funcName);
      }
    }
    // System.err.println (">>> functionAvailable (ns=" + ns + 
    //                    ", func=" + funcName + ") = " + isAvailable);
    return isAvailable;
  }
  
  /**
   * Execute the element-available() function.
   * @param ns       the URI of namespace in which the function is needed
   * @param funcName the function name being tested
   *
   * @return whether the given function is available or not.
   */
  public boolean elementAvailable (String ns, String funcName) 
  {
    boolean isAvailable = false;
    if (null != ns) 
    {
      ExtensionFunctionHandler extNS = 
                                      (ExtensionFunctionHandler) m_extensionFunctionNamespaces.get (ns);
      if (extNS != null) 
      {
        isAvailable = extNS.isElementAvailable (funcName);
      }
    }
    // System.err.println (">>> elementAvailable (ns=" + ns + 
    //                    ", func=" + funcName + ") = " + isAvailable);
    return isAvailable;
  }


  /**
   * Handle an extension function.
   * @param ns       the URI of namespace in which the function is needed
   * @param funcName the function name being called
   * @param argVec   arguments to the function in a vector
   *
   * @return result of executing the function
   */
  public Object extFunction (String ns, String funcName, Vector argVec, 
                             Object methodKey, ExpressionContext exprContext)
    throws org.xml.sax.SAXException
  {
    if(null == m_extensionFunctionNamespaces.get ("http://xml.apache.org/xslt/java"))
    {
      // register the java namespace as being implemented by the 
      // xslt-javaclass engine. Note that there's no real code
      // per se for this extension as the functions carry the 
      // object on which to call etc. and all the logic of breaking
      // that up is in the xslt-javaclass engine.
      String uri = "http://xml.apache.org/xslt/java";
      ExtensionFunctionHandler fh = new ExtensionFunctionHandler (uri, null, "xslt-javaclass", null, null);
      
      addExtensionNamespace (uri, fh);   
    }
    if(null == m_extensionFunctionNamespaces.get ("http://xsl.lotus.com/java"))
    {
      // register the java namespace as being implemented by the 
      // xslt-javaclass engine. Note that there's no real code
      // per se for this extension as the functions carry the 
      // object on which to call etc. and all the logic of breaking
      // that up is in the xslt-javaclass engine.
      String uri = "http://xsl.lotus.com/java";
      ExtensionFunctionHandler fh = new ExtensionFunctionHandler (uri, null, "xslt-javaclass", null, null);
      
      addExtensionNamespace (uri, fh);   
    }

    Object result = null;
    if (null != ns)
    {
      ExtensionFunctionHandler extNS = (ExtensionFunctionHandler)
                                        m_extensionFunctionNamespaces.get (ns);

      // if not found try to auto declare this extension namespace:
      // try treaing the URI of the extension as a fully qualified
      // class name; if it works then go with treating this an extension
      // implemented in "javaclass" for with that class being the srcURL.
      // forget about setting functions in that case - so if u do
      // extension-function-available then u get false, but that's ok.
      if (extNS == null) 
      {
        try 
        {
          // Scott: I don't think this is doing anything for us.
          // String cname = ns.startsWith ("class:") ? ns.substring (6) : ns;
          // Class.forName (cname); // does it load?
          extNS = new ExtensionFunctionHandler (ns, null, "javaclass",
                                                ns, null);
          addExtensionNamespace (ns, extNS);
        }
        catch (Exception e) 
        {
          // oops, it failed .. ok, so this path ain't gonna pan out. shucks.
        }
      }

      if (extNS != null)
      {
        try
        {
          result = extNS.callFunction (funcName, argVec, methodKey, null, exprContext);
        }
        catch (Exception e)
        {
          // e.printStackTrace();
          // throw new XPathProcessorException ("Extension function '" + ns +
          //  ":" + funcName +
          //  "', threw exception: " + e, e);
          String msg = e.getMessage();
          if(null != msg)
          {
            if(msg.startsWith("Stopping after fatal error:"))
            {
              msg = msg.substring("Stopping after fatal error:".length());
            }
            System.out.println("Call to extension function failed: "+msg);
            result = new XNull();
            throw new org.xml.sax.SAXException(e);
          }
        }
      }
      else 
      {
        throw new XPathProcessorException ("Extension function '" + ns +
          ":" + funcName + "' is unknown");
      }
    }
    return result;
  }
  
  /**
  * The table of extension namespaces.
  * @serial
  */
  // public Hashtable m_extensionNamespaces = new Hashtable();
}
