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
package org.apache.xalan.xpath;

import org.w3c.dom.*;
import java.util.*;
import java.io.Serializable;

/**
 * <meta name="usage" content="advanced"/>
 * This interface provides services for processing a 
 * XPath LocationPath.  Either the default implementation 
 * (SimpleNodeLocator) will be used to implement this interface, 
 * or an extension function that serves as a factory method can be 
 * used that returns an XLocator.
 */
public interface XLocator extends Serializable
{
  /**
   * Execute a connection (if it was not executed by the static 
   * connect method) and process the following LocationPath, 
   * if it is present.  Normally, the connection functionality 
   * should be executed by this function and not the static connect 
   * function, which is really a factory method for the XLocator 
   * instance.  The arguments to the static connect function
   * are re-passed to this function.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param connectArgs The same arguments that were passed to the 
   * static connect function.
   * @returns the result of the query in an XNodeSet object.
   */
  XNodeSet connectToNodes(XPath xpath, XPathContext xctxt, Node context, 
                               int opPos, Vector connectArgs)
    throws org.xml.sax.SAXException;
  
  /**
   * Execute a union. The union of its operands, which are locationPaths,
   * must be node-sets.
   * @param xpath The xpath that is executing.
   * @param xctxt The execution context.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param callback Interface that implements the processLocatedNode method.
   * @param callbackInfo Object that will be passed to the processLocatedNode method.
   * @returns the result of the query in an XNodeSet object.
   */
  XNodeSet union(XPath xpath, XPathContext xctxt, Node context, 
                 int opPos) 
    throws org.xml.sax.SAXException;

  /**
   * Execute a location path.  Normally, this method simply 
   * moves past the OP_LOCATIONPATH and it's length member, 
   * and calls the Step function, which will recursivly process 
   * the rest of the LocationPath, and then wraps the NodeList result
   * in an XNodeSet object.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param callback Interface that implements the processLocatedNode method.
   * @param callbackInfo Object that will be passed to the processLocatedNode method.
   * @param stopAtFirst True if only the first found node in doc order is needed.
   * @returns the result of the query in an XNodeSet object.
   */
  XNodeSet locationPath(XPath xpath, XPathContext xctxt, 
                        Node context, int opPos)
    throws org.xml.sax.SAXException;
  
  /**
   * Execute a location path pattern.  This will return a score
   * of MATCH_SCORE_NONE, MATCH_SCORE_NODETEST, 
   * MATCH_SCORE_OTHER, MATCH_SCORE_QNAME.
   * @param xpath The xpath that is executing.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @returns score, one of MATCH_SCORE_NODETEST, 
   * MATCH_SCORE_NONE, MATCH_SCORE_OTHER, MATCH_SCORE_QNAME.
   */
  double locationPathPattern(XPath xpath, XPathContext xctxt, Node context, int opPos)
    throws org.xml.sax.SAXException;
}
