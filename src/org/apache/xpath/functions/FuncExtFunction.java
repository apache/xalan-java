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
package org.apache.xpath.functions;

import java.util.Vector;

import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.*;
import org.apache.xalan.extensions.ExtensionsTable;

import org.w3c.dom.Node;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.traversal.NodeIterator;

/**
 * <meta name="usage" content="advanced"/>
 * An object of this class represents an extension call expression.  When 
 * the expression executes, it calls ExtensionsTable#extFunction, and then 
 * converts the result to the appropriate XObject.
 */
public class FuncExtFunction extends Function
{

  /** The namespace for the extension function, which should not normally 
   *  be null or empty.
   *  @serial    */
  String m_namespace;

  /** The local name of the extension.
   *  @serial   */
  String m_extensionName;

  /** Unique method key, which is passed to ExtensionsTable#extFunction in 
   *  order to allow caching of the method. 
   *  @serial */
  Object m_methodKey;

  /** Array of static expressions which represent the parameters to the 
   *  function.
   *  @serial   */
  Vector m_argVec = new Vector();

  /**
   * Create a new FuncExtFunction based on the qualified name of the extension, 
   * and a unique method key.
   *
   * @param namespace The namespace for the extension function, which should 
   *                  not normally be null or empty. 
   * @param extensionName The local name of the extension.
   * @param methodKey Unique method key, which is passed to 
   *                  ExtensionsTable#extFunction in order to allow caching 
   *                  of the method.
   */
  public FuncExtFunction(java.lang.String namespace,
                         java.lang.String extensionName, Object methodKey)
  {

    m_namespace = namespace;
    m_extensionName = extensionName;
    m_methodKey = methodKey;
  }

  /**
   * Execute the function.  The function must return
   * a valid object.
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {

    XObject result;
    Vector argVec = new Vector();
    int nArgs = m_argVec.size();

    for (int i = 0; i < nArgs; i++)
    {
      Expression arg = (Expression) m_argVec.elementAt(i);

      argVec.addElement(arg.execute(xctxt));
    }

    ExtensionsTable etable = xctxt.getExtensionsTable();
    Object val = etable.extFunction(m_namespace, m_extensionName, argVec,
                                    m_methodKey, xctxt);

    if (null != val)
    {
      if (val instanceof XObject)
      {
        result = (XObject) val;
      }

      // else if(val instanceof XLocator)
      // {
      // XLocator locator = (XLocator)val;
      // opPos = getNextOpPos(opPos+1);
      // result = locator.connectToNodes(this, opPos, argVec);  
      // System.out.println("nodeset len: "+result.nodeset().getLength());
      // }
      else if (val instanceof String)
      {
        result = new XString((String) val);
      }
      else if (val instanceof Boolean)
      {
        result = ((Boolean) val).booleanValue()
                 ? XBoolean.S_TRUE : XBoolean.S_FALSE;
      }
      else if (val instanceof Double)
      {
        result = new XNumber(((Double) val).doubleValue());
      }
      else if (val instanceof DocumentFragment)
      {
        result = new XRTreeFrag((DocumentFragment) val);
      }
      else if (val instanceof NodeIterator)
      {
        result = new XNodeSet((NodeIterator) val);
      }
      else if (val instanceof Node)
      {
        result = new XNodeSet((Node) val);
      }
      else
      {
        result = new XObject(val);
      }
    }
    else
    {
      result = new XNull();
    }

    return result;
  }

  /**
   * Set an argument expression for a function.  This method is called by the 
   * XPath compiler.
   *
   * @param arg non-null expression that represents the argument.
   * @param argNum The argument number index.
   *
   * @throws WrongNumberArgsException If the argNum parameter is beyond what 
   * is specified for this function.
   */
  public void setArg(Expression arg, int argNum)
          throws WrongNumberArgsException
  {
    m_argVec.addElement(arg);
  }

  /**
   * Check that the number of arguments passed to this function is correct. 
   *
   *
   * @param argNum The number of arguments that is being passed to the function.
   *
   * @throws WrongNumberArgsException
   */
  public void checkNumberArgs(int argNum) throws WrongNumberArgsException{}
}
