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
package org.apache.xalan.stree;

import java.util.*;
import java.io.*;
import org.w3c.dom.*;

// XPath imports
import org.apache.xalan.xpath.res.XPATHErrorResources;
import org.apache.xalan.xpath.SimpleNodeLocator;
import org.apache.xalan.xpath.XPath;
import org.apache.xalan.xpath.XLocator;
import org.apache.xalan.xpath.NodeSet;
import org.apache.xalan.xpath.XNodeSet;
import org.apache.xalan.xpath.XPathFactory;
import org.apache.xalan.xpath.XPathContext;
import org.apache.xalan.xpath.OpCodes;

/**
 * <meta name="usage" content="advanced"/>
 * SimpleNodeLocator implements a search of one or more DOM trees.
 * By using the connect function as an extension, the user may 
 * specify a directory and a filter specification for XML files 
 * that will be searched.
 * This is a singleton class.
 */
public class StreeLocator extends SimpleNodeLocator
{  
  /**
   * Create a StreeLocator object.
   */
  public StreeLocator()
  {
    super();
  }
  
  /**
   * The singleton instance of this class.
   */
  private static StreeLocator m_locater = null;
   
  /**
   * The the default locator.
   */
  public static XLocator getDefaultLocator()
  {
    m_locater = (null == m_locater) ? new StreeLocator() : m_locater;
    return m_locater;
  }
  
  /**
   * Execute the proprietary connect() function, which returns an 
   * instance of XLocator.  When the XPath object sees a return type 
   * of XLocator, it will call the locationPath function that passes 
   * in the connectArgs.  The opPos and args params are not used 
   * by this function.  This really is just a factory function 
   * for the XLocator instance, but this fact is hidden from the 
   * user.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param args The function args.
   * @returns A node set of Document nodes.
   */
  public static XLocator query(String path, String fileSpec) 
  {    
    m_locater = (null == m_locater) ? new StreeLocator() : m_locater;
    return m_locater;
  }
  
  /**
   * (Same as query for the moment).
   * @param opPos The current position in the xpath.m_opMap array.
   * @param args The function args.
   * @returns A node set of Document nodes.
   */
  public static XLocator connect(String path, String fileSpec) 
  {    
    m_locater = (null == m_locater) ? new StreeLocator() : m_locater;
    return m_locater;
  }
    

  /**
   * Computes the union of its operands which must be node-sets.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns the union of node-set operands, or an empty set if 
   * callback methods are used.
   */
  public XNodeSet union(XPath xpath, XPathContext execContext, 
                        Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    return new XNodeSet(new IndexedUnionPathIterator(xpath, execContext, 
                                 context, opPos, this));
  } 
  
  /**
   * Execute a location path.  Normally, this method simply 
   * moves past the OP_LOCATIONPATH and it's length member, 
   * and calls the Step function, which will recursivly process 
   * the rest of the LocationPath, and then wraps the NodeList result
   * in an XNodeSet object.
   * @param xpath The xpath that is executing.
   * @param xctxt The execution context.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param callback Interface that implements the processLocatedNode method.
   * @param callbackInfo Object that will be passed to the processLocatedNode method.
   * @returns the result of the query in an XNodeSet object.
   */
  public XNodeSet locationPath(XPath xpath, XPathContext xctxt, 
                               Node context, int opPos) 
    throws org.xml.sax.SAXException
  {    
    return new XNodeSet(new IndexedLocPathIterator(xpath, xctxt, 
                                 context, opPos, this));
  }
  
  
  /**
   * Create an XPathFactory for this XLocator.
   */
  public static XPathFactory factory() 
  {
    return new StreeLocatorFactory();
  }

}

/**
 * Override the createXLocatorHandler method.
 */
class FNLDOMXPath extends XPath
{
  public FNLDOMXPath()
  {
    super();
  }
  
  /**
   * getXLocatorHandler.
   */
  public XLocator createXLocatorHandler(XPath xpath)
  {
    return new StreeLocator();
  }
}

/**
 * Implement an XPath factory.
 */
class StreeLocatorFactory implements XPathFactory
{
  public XPath create()
  {
    return new FNLDOMXPath();
  }
}
