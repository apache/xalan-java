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
package org.apache.xalan.templates;

import java.util.Vector;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

import org.apache.xpath.NodeSet;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.DOMHelper;
import org.apache.xpath.SourceTreeManager;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xpath.XPathContext;
import org.apache.xalan.transformer.TransformerImpl;

import org.xml.sax.InputSource;
import org.xml.sax.Locator;

import javax.xml.transform.TransformerException;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.ErrorListener;
import org.apache.xml.utils.SAXSourceLocator;
import javax.xml.transform.Source;

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
public class FuncDocument extends Function2Args
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

    Node context = xctxt.getCurrentNode();
    Document docContext = (Node.DOCUMENT_NODE == context.getNodeType())
                          ? (Document) context : context.getOwnerDocument();
    XObject arg = (XObject) this.getArg0().execute(xctxt);
    String base = "";
    Expression arg1Expr = this.getArg1();

    if (null != arg1Expr)
    {

      // The URI reference may be relative. The base URI (see [3.2 Base URI]) 
      // of the node in the second argument node-set that is first in document 
      // order is used as the base URI for resolving the 
      // relative URI into an absolute URI. 
      XObject arg2 = arg1Expr.execute(xctxt);

      if (XObject.CLASS_NODESET == arg2.getType())
      {
        Node baseNode = arg2.nodeset().nextNode();

        if (baseNode == null)
          warn(xctxt, XSLTErrorResources.WG_EMPTY_SECOND_ARG, null);

        Document baseDoc = (baseNode == null
                            ? null
                            : (Node.DOCUMENT_NODE == baseNode.getNodeType())
                              ? (Document) baseNode
                              : baseNode.getOwnerDocument());

        if (baseDoc == null || baseDoc instanceof Stylesheet)
        {

          // base = ((Stylesheet)baseDoc).getBaseIdentifier();
          base = xctxt.getNamespaceContext().getBaseIdentifier();
        }
        else
          base = xctxt.getSourceTreeManager().findURIFromDoc(baseDoc);
      }
      else
      {
        base = arg2.str();
      }
    }
    else
    {

      // If the second argument is omitted, then it defaults to 
      // the node in the stylesheet that contains the expression that 
      // includes the call to the document function. Note that a 
      // zero-length URI reference is a reference to the document 
      // relative to which the URI reference is being resolved; thus 
      // document("") refers to the root node of the stylesheet; 
      // the tree representation of the stylesheet is exactly 
      // the same as if the XML document containing the stylesheet 
      // was the initial source document.
      base = xctxt.getNamespaceContext().getBaseIdentifier();
    }

    XNodeSet nodes = new XNodeSet();
    NodeSet mnl = nodes.mutableNodeset();
    NodeIterator iterator = (XObject.CLASS_NODESET == arg.getType())
                            ? arg.nodeset() : null;
    Node pos = null;

    while ((null == iterator) || (null != (pos = iterator.nextNode())))
    {
      String ref = (null != iterator)
                   ? DOMHelper.getNodeData(pos) : arg.str();

      if (null == ref)
        continue;

      if (null == docContext)
      {
        error(xctxt, XSLTErrorResources.ER_NO_CONTEXT_OWNERDOC, null);  //"context does not have an owner document!");
      }

      // From http://www.ics.uci.edu/pub/ietf/uri/rfc1630.txt
      // A partial form can be distinguished from an absolute form in that the
      // latter must have a colon and that colon must occur before any slash
      // characters. Systems not requiring partial forms should not use any
      // unencoded slashes in their naming schemes.  If they do, absolute URIs
      // will still work, but confusion may result.
      int indexOfColon = ref.indexOf(':');
      int indexOfSlash = ref.indexOf('/');

      if ((indexOfColon != -1) && (indexOfSlash != -1)
              && (indexOfColon < indexOfSlash))
      {

        // The url (or filename, for that matter) is absolute.
        base = null;
      }

      Node newDoc = getDoc(xctxt, context, ref, base);

      // nodes.mutableNodeset().addNode(newDoc);  
      if (null != newDoc)
      {

        // TODO: mnl.addNodeInDocOrder(newDoc, true, xctxt); ??
        if (!mnl.contains(newDoc))
          mnl.addElement(newDoc);
      }

      if (null == iterator || newDoc == null)
        break;
    }

    return nodes;
  }

  /**
   * Get the document from the given URI and base
   *
   * @param xctxt The XPath runtime state.
   * @param context The current context node
   * @param uri Relative(?) URI of the document
   * @param base Base to resolve relative URI from.
   *
   * @return The document Node pointing to the document at the given URI
   * or null
   *
   * @throws javax.xml.transform.TransformerException
   */
  Node getDoc(XPathContext xctxt, Node context, String uri, String base)
          throws javax.xml.transform.TransformerException
  {

    // System.out.println("base: "+base+", uri: "+uri);
    SourceTreeManager treeMgr = xctxt.getSourceTreeManager();
    
    Node newDoc;
    try
    {
      Source source = treeMgr.resolveURI(base, uri, xctxt.getSAXLocator());
      newDoc = treeMgr.getNode(source);
    }
    catch (IOException ioe)
    {
      throw new TransformerException(ioe.getMessage(), 
        (SourceLocator)xctxt.getSAXLocator(), ioe);
    }
    catch(TransformerException te)
    {
      throw new TransformerException(te);
    }

    if (null != newDoc)
      return newDoc;

    // If the uri length is zero, get the uri of the stylesheet.
    if (uri.length() == 0)
      uri = xctxt.getNamespaceContext().getBaseIdentifier();

    String diagnosticsString = null;

    try
    {
      if ((null != uri) && (uri.toString().length() > 0))
      {
        newDoc = treeMgr.getSourceTree(base, uri, xctxt.getSAXLocator());

        // System.out.println("newDoc: "+((Document)newDoc).getDocumentElement().getNodeName());
      }
      else
        warn(xctxt, XSLTErrorResources.WG_CANNOT_MAKE_URL_FROM,
             new Object[]{ ((base == null) ? "" : base) + uri });  //"Can not make URL from: "+((base == null) ? "" : base )+uri);
    }
    catch (Throwable throwable)
    {

      // throwable.printStackTrace();
      newDoc = null;

      // path.warn(XSLTErrorResources.WG_ENCODING_NOT_SUPPORTED_USING_JAVA, new Object[]{((base == null) ? "" : base )+uri}); //"Can not load requested doc: "+((base == null) ? "" : base )+uri);
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

    if (null == newDoc)
    {

      // System.out.println("what?: "+base+", uri: "+uri);
      if (null != diagnosticsString)
      {
        warn(xctxt, XSLTErrorResources.WG_CANNOT_LOAD_REQUESTED_DOC,
             new Object[]{ diagnosticsString });  //"Can not load requested doc: "+((base == null) ? "" : base )+uri);
      }
      else
        warn(xctxt, XSLTErrorResources.WG_CANNOT_LOAD_REQUESTED_DOC,
             new Object[]{
               uri == null
               ? ((base == null) ? "" : base) + uri : uri.toString() });  //"Can not load requested doc: "+((base == null) ? "" : base )+uri);
    }
    else
    {

      // TBD: What to do about XLocator?
      // xctxt.getSourceTreeManager().associateXLocatorToNode(newDoc, url, null);
    }

    return newDoc;
  }

  /**
   * Tell the user of an error, and probably throw an
   * exception.
   *
   * @param xctxt The XPath runtime state.
   * @param msg The error message code
   * @param args Arguments to be used in the error message
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public void error(XPathContext xctxt, int msg, Object args[])
          throws javax.xml.transform.TransformerException
  {

    String formattedMsg = XSLMessages.createMessage(msg, args);
    ErrorListener errHandler = xctxt.getErrorListener();
    TransformerException spe = new TransformerException(formattedMsg,
                              (SourceLocator)xctxt.getSAXLocator());

    if (null != errHandler)
      errHandler.error(spe);
    else
      System.out.println(formattedMsg);
  }

  /**
   * Warn the user of a problem.
   *
   * @param xctxt The XPath runtime state.
   * @param msg Warning message code
   * @param args Arguments to be used in the warning message
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public void warn(XPathContext xctxt, int msg, Object args[])
          throws javax.xml.transform.TransformerException
  {

    String formattedMsg = XSLMessages.createWarning(msg, args);
    ErrorListener errHandler = xctxt.getErrorListener();
    TransformerException spe = new TransformerException(formattedMsg,
                              (SourceLocator)xctxt.getSAXLocator());

    if (null != errHandler)
      errHandler.warning(spe);
    else
      System.out.println(formattedMsg);
  }

 /**
   * Overide the superclass method to allow one or two arguments.
   *
   *
   * @param argNum Number of arguments passed in to this function
   *
   * @throws WrongNumberArgsException
   */
  public void checkNumberArgs(int argNum) throws WrongNumberArgsException
  {
    if ((argNum < 1) || (argNum > 2))
      throw new WrongNumberArgsException("2");
  }
}
