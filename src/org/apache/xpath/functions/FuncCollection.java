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

import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.IOException;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.XType;

import org.apache.xpath.NodeSetDTM;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XSequence;
import org.apache.xpath.objects.XNodeSequenceSingleton;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.SourceTreeManager;
import org.apache.xpath.Expression;
import org.apache.xalan.res.XSLMessages;
import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xalan.transformer.TransformerImpl;

import org.xml.sax.InputSource;
import org.xml.sax.Locator;

import javax.xml.transform.TransformerException;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;

import org.apache.xml.utils.SAXSourceLocator;
import org.apache.xml.utils.XMLString;

/**
 * <meta name="usage" content="advanced"/>
 * Execute the Doc() function.
 *
 * When the document function has exactly one argument and the argument
 * is a node-set, then the result is the union, for each node in the
 * argument node-set, of the result of calling the document function with
 * the first argument being the string-value of the node, and the second
 * argument being a node-set with the node as its only member. When the
 * document function has two arguments and the first argument is a node-set,
 * then the result is the union, for each node in the argument node-set,
 * of the result of calling the document function with the first argument
 * being the string-value of the node, and with the second argument being
 * the second argument passed to the document function.
 */
public class FuncCollection extends FunctionOneArg
{

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
    int context = xctxt.getCurrentNode();
    DTM dtm = xctxt.getDTM(context);
    
    int docContext = dtm.getDocumentRoot(context);

     // The URI reference must be an absolute URI.       
     String base = this.getArg0().execute(xctxt).str();    

      if (DTM.NULL == docContext)
      {
        error(xctxt, XPATHErrorResources.ER_CONTEXT_HAS_NO_OWNERDOC, null);  //"context does not have an owner document!");
      }
      
      XNodeSet nodes = new XNodeSet(xctxt.getDTMManager());
      NodeSetDTM mnl = nodes.mutableNodeset();

      int newDoc = getDoc(xctxt, context, base);

      if (DTM.NULL != newDoc)
      {
        // TODO: mnl.addNodeInDocOrder(newDoc, true, xctxt); ??
        if (!mnl.contains(newDoc))
        {
          mnl.addElement(newDoc);
        }
      }

    if (mnl.getLength() == 0)
      return XSequence.EMPTY;
    else  
      return nodes;
  }

  /**
   * Get the document from the given URI and base
   *
   * @param xctxt The XPath runtime state.
   * @param context The current context node
   * @param uri URI of the document
   *
   * @return The document Node pointing to the document at the given URI
   * or null
   *
   * @throws javax.xml.transform.TransformerException
   */
  int getDoc(XPathContext xctxt, int context, String uri)
          throws javax.xml.transform.TransformerException
  {

    SourceTreeManager treeMgr = xctxt.getSourceTreeManager();
    Source source;
   
    int newDoc;
    try
    {
      source = treeMgr.resolveURI(null, uri, xctxt.getSAXLocator());
      newDoc = treeMgr.getNode(source);
    }
    catch (IOException ioe)
    {
      throw new TransformerException(ioe.getMessage(), 
        (SourceLocator)xctxt.getSAXLocator(), ioe);
    }

    if (DTM.NULL != newDoc)
      return newDoc;

    String diagnosticsString = null;

    try
    {
      if ((null != uri) && (uri.toString().length() > 0))
      {
        newDoc = treeMgr.getSourceTree(source, xctxt.getSAXLocator(), xctxt);
        
      }
      else
        warn(xctxt, XPATHErrorResources.WG_CANNOT_MAKE_URL_FROM,
             new Object[]{ uri });  //"Can not make URL from: "+((base == null) ? "" : base )+uri);
    }
    catch (Throwable throwable)
    {

      newDoc = DTM.NULL;

       while (throwable
             instanceof org.apache.xml.utils.WrappedRuntimeException)
      {
        throwable =
          ((org.apache.xml.utils.WrappedRuntimeException) throwable).getException();
      }

      if ((throwable instanceof NullPointerException)
              || (throwable instanceof ClassCastException))
      {
        throw new org.apache.xml.utils.WrappedRuntimeException(
          (Exception) throwable);
      }

      StringWriter sw = new StringWriter();
      PrintWriter diagnosticsWriter = new PrintWriter(sw);

      if (throwable instanceof TransformerException)
      {
        TransformerException spe = (TransformerException) throwable;

        {
          Throwable e = spe;

          while (null != e)
          {
            if (null != e.getMessage())
            {
              diagnosticsWriter.println(" (" + e.getClass().getName() + "): "
                                        + e.getMessage());
            }

            if (e instanceof TransformerException)
            {
              TransformerException spe2 = (TransformerException) e;

              SourceLocator locator = spe2.getLocator();
              if ((null != locator) && (null != locator.getSystemId()))
                diagnosticsWriter.println("   ID: " + locator.getSystemId()
                                          + " Line #" + locator.getLineNumber()
                                          + " Column #"
                                          + locator.getColumnNumber());

              e = spe2.getException();

              if (e instanceof org.apache.xml.utils.WrappedRuntimeException)
                e = ((org.apache.xml.utils.WrappedRuntimeException) e).getException();
            }
            else
              e = null;
          }
        }
      }
      else
      {
        diagnosticsWriter.println(" (" + throwable.getClass().getName()
                                  + "): " + throwable.getMessage());
      }

      diagnosticsString = throwable.getMessage(); //sw.toString();
    }

    if (DTM.NULL == newDoc)
    {

      if (null != diagnosticsString)
      {
        error(xctxt, XPATHErrorResources.ER_INVALID_ARG_TO_COLLECTION,
             new Object[]{ diagnosticsString });  //"Can not load requested doc: "+((base == null) ? "" : base )+uri);
      }
      else
        error(xctxt, XPATHErrorResources.ER_INVALID_ARG_TO_COLLECTION,
             new Object[]{
               uri == null
               ? uri : uri.toString() });  //"Can not load requested doc: "+((base == null) ? "" : base )+uri);
    }

    return newDoc;
  }
  
  /**
   * Tell if the expression is a nodeset expression.  In other words, tell 
   * if you can execute {@link asNode() asNode} without an exception.
   * @return true if the expression can be represented as a nodeset.
   */
  public boolean isNodesetExpr()
  {
    return true;
  }

}
