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
package org.apache.xalan.extensions;

import java.util.Hashtable;
import java.util.Vector;
import java.util.StringTokenizer;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Attr;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.utils.QName;
import org.apache.xpath.XPathProcessorException;

/**
 * <meta name="usage" content="advanced"/>
 * Represents an extension namespace. Provides functions
 * to call into the extension via both element syntax and function
 * syntax. Extends XPath's extension function capability to a full
 * namespace extension model.
 *
 * @author Sanjiva Weerawarana (sanjiva@watson.ibm.com)
 */
public class ExtensionNSHandler extends ExtensionFunctionHandler
{
  TransformerImpl xslp;        // xsl transformer for whom I'm working
  boolean componentDescLoaded; // true when info from the component desc
  // has been loaded. This gets set as soon
  // as any of the info has been specified.
  // If this is false, when processElement or
  // processFunction is called it will use the
  // namespace URI as a URL and try to load
  // that location as the component desc.

  /////////////////////////////////////////////////////////////////////////
  // Constructors
  /////////////////////////////////////////////////////////////////////////

  /**
   * Construct a new extension namespace handler for a given extension NS.
   * This doesn't do anything - just hang on to the namespace URI.
   *
   * @param xslp         handle to the XSL transformer that I'm working for
   * @param namespaceUri the extension namespace URI that I'm implementing
   */
  public ExtensionNSHandler (String namespaceUri)
  {
    super (namespaceUri);
    this.xslp = xslp;
  }

  /////////////////////////////////////////////////////////////////////////

  /**
   * Construct a new extension namespace handler given all the information
   * needed.
   *
   * @param xslp         handle to the XSL transformer that I'm working for
   * @param namespaceUri the extension namespace URI that I'm implementing
   * @param elemNames    string containing list of elements of extension NS
   * @param funcNames    string containing list of functions of extension NS
   * @param lang         language of code implementing the extension
   * @param srcURL       value of src attribute (if any) - treated as a URL
   *                     or a classname depending on the value of lang. If
   *                     srcURL is not null, then scriptSrc is ignored.
   * @param scriptSrc    the actual script code (if any)
   */
  public ExtensionNSHandler (TransformerImpl xslp, String namespaceUri,
                             String elemNames, String funcNames,
                             String lang, String srcURL, String src)
  {
    super (namespaceUri, funcNames, lang, srcURL, src);
    this.xslp = xslp;
    setElements (elemNames);
    componentDescLoaded = true;
  }

  /////////////////////////////////////////////////////////////////////////
  // Main API
  /////////////////////////////////////////////////////////////////////////

  /*
  * Set function local parts of extension NS. Super does the work; I
  * just record that a component desc has been loaded.
  *
  * @param functions whitespace separated list of function names defined
  *        by this extension namespace.
  */
  public void setFunctions (String funcNames)
  {
    super.setFunctions (funcNames);
    componentDescLoaded = true;
  }

  /////////////////////////////////////////////////////////////////////////

  /**
   * Set the script data for this extension NS. Deferred to super for
   * actual work - I only record that a component desc has been loaded.
   *
   * @param lang      language of the script.
   * @param srcURL    value of src attribute (if any) - treated as a URL
   *                  or a classname depending on the value of lang. If
   *                  srcURL is not null, then scriptSrc is ignored.
   * @param scriptSrc the actual script code (if any)
   */
  public void setScript (String lang, String srcURL, String scriptSrc)
  {
    super.setScript (lang, srcURL, scriptSrc);
    componentDescLoaded = true;
  }

  /////////////////////////////////////////////////////////////////////////

  /**
   * Set element local parts of extension NS.
   *
   * @param elemNames whitespace separated list of element names defined
   *        by this extension namespace.
   */
  public void setElements (String elemNames)
  {
    if (elemNames == null) 
      return;
    super.setElements(elemNames);
    componentDescLoaded = true;
  }

  /////////////////////////////////////////////////////////////////////////

  /**
   * Tests whether a certain element name is known within this namespace.
   *
   * @param element name of the element being tested
   *
   * @return true if its known, false if not.
   */
  public boolean isElementAvailable (String element)
  {
    return (elements.get (element) != null);
  }

  /////////////////////////////////////////////////////////////////////////

  /**
   * Process a call to this extension namespace via an element. As a side
   * effect, the results are sent to the TransformerImpl's result tree.
   *
   * @param localPart      Element name's local part.
   * @param element        The extension element being processed.
   * @param transformer      Handle to TransformerImpl.
   * @param stylesheetTree The compiled stylesheet tree.
   * @param mode           The current mode.
   * @param sourceTree     The root of the source tree (but don't assume
   *                       it's a Document).
   * @param sourceNode     The current context node.
   *
   * @exception XSLProcessorException thrown if something goes wrong
   *            while running the extension handler.
   * @exception MalformedURLException if loading trouble
   * @exception FileNotFoundException if loading trouble
   * @exception IOException           if loading trouble
   * @exception SAXException          if parsing trouble
   */
  public void processElement (String localPart, Element element,
                              TransformerImpl transformer,
                              Stylesheet stylesheetTree,
                              Node sourceTree, Node sourceNode, QName mode,
                              Class classObj, Object methodKey)
    throws SAXException, IOException
  {
    if (!componentStarted)
    {
      try
      {
        startupComponent (classObj);
      }
      catch (XPathProcessorException e)
      {
        // e.printStackTrace ();
        throw new SAXException (e.getMessage (), e);
      }
    }

    Object result = null;
    XSLProcessorContext xpc = new XSLProcessorContext (transformer, 
                                                       stylesheetTree,
                                                       sourceTree, 
                                                       sourceNode, 
                                                       mode);
    try
    {
      Vector argv = new Vector (2);
      argv.addElement (xpc);
      argv.addElement (element);
      result = super.callFunction (localPart, argv, methodKey, classObj,
                                   transformer.getXPathContext());
    }
    catch (XPathProcessorException e)
    {
      // e.printStackTrace ();
      throw new SAXException (e.getMessage (), e);
    }

    if (result != null)
    {
      xpc.outputToResultTree (stylesheetTree, result);
    }
  }

  /////////////////////////////////////////////////////////////////////////
  // Private/Protected Functions
  /////////////////////////////////////////////////////////////////////////

  /**
   * Start the component up by executing any script that needs to run
   * at startup time. This needs to happen before any functions can be
   * called on the component.
   *
   * @exception XPathProcessorException if something bad happens.
   */
  protected void startupComponent (Class classObj) 
    throws  SAXException
  {
    synchronized(bsfInitSynch)
    {
      if(!bsfInitialized)
      {
        bsfInitialized = true;
        com.ibm.bsf.BSFManager.registerScriptingEngine ("xslt-javaclass",
                                                        "org.apache.xalan.extensions.XSLTJavaClassEngine",
                                                        new String[0]);
      }
    }

    if (!componentDescLoaded)
    {
      try
      {
        loadComponentDescription ();
      }
      catch (Exception e)
      {
        throw new XPathProcessorException (e.getMessage (), e);
      }
    }
    super.startupComponent (classObj);
  }

  /////////////////////////////////////////////////////////////////////////

  /**
   * Load the component spec for this extension namespace taking the URI
   * of this namespace as the URL to read from.
   *
   * @exception XSLProcessorException if something bad happens.
   * @exception MalformedURLException if loading trouble
   * @exception FileNotFoundException if loading trouble
   * @exception IOException           if loading trouble
   * @exception SAXException          if parsing trouble
   */
  private void loadComponentDescription ()
    throws  SAXException
  {
    // first try treaing the URI of the extension as a fully qualified
    // class name; if it works then go with treating this an extension
    // implemented in "javaclass" for with that class being the srcURL.
    // forget about setting elements and functions in that case - so if
    // u do extension-{element,function}-available then u get false,
    // but that's ok.
    try {
      String cname = namespaceUri.startsWith ("class:") ?
                       namespaceUri.substring (6) : namespaceUri;
      Class.forName (cname); // does it load?
      setScript ("javaclass", namespaceUri, null);
      componentDescLoaded = true;
      return;
    } catch (Exception e) {
      // oops, it failed .. ok, so this path ain't gonna pan out. shucks.
    }

    // parse the document at the URI of the extension, if any
    String url = null; // xslp.getAbsoluteURI(namespaceUri,
                     //               xslp.m_stylesheetRoot.getBaseIdentifier());
    // System.out.println("Extension URI: "+url.toString());
    org.apache.xpath.XPathContext liaison = xslp.getXPathContext();

    Element componentElement = null;
    /*
    try
    {
    */
    // liaison.parse(new InputSource(url.toString()));
    // Document compSpec = liaison.getDocument();
    Document compSpec = null;
    componentElement = compSpec.getDocumentElement ();

    // determine the functions and elements of this component
    setElements (componentElement.getAttribute ("elements"));
    setFunctions (componentElement.getAttribute ("functions"));

    // is there an lxslt:script element child? [NOTE THAT THIS IS NOT
    // PROPER NAMESPACE-WISE .. I'll FIX IT LATER. .. Sanjiva 8/20/99.]
    NodeList nl = componentElement.getElementsByTagName ("lxslt:script");
    switch (nl.getLength ())
    {
    case 0:
      break;
    case 1:
      Element scriptElem = (Element) nl.item (0);
      String lang = scriptElem.getAttribute ("lang");
      Attr srcURLAttr = scriptElem.getAttributeNode ("src");
      String srcURL = (srcURLAttr == null) ? null : srcURLAttr.getValue ();
      String src = getScriptString (scriptElem);
      setScript (lang, srcURL, src);
      break;
    default:
      throw new SAXException ("too many <script>s in component");
    }
    componentDescLoaded = true;
    /*
    }
    catch(org.xml.sax.SAXException se)
    {

    }
    catch(java.net.UnknownHostException uhe)
    {

    }
    */
  }

  /////////////////////////////////////////////////////////////////////////

  /**
   * extract the text nodes and CDATA content children of the given
   * elem and return as a string. Any other types of node children
   * are ignored
   *
   * @param elem element whose text and cdata children are to be
   *        concatenated together.
   *
   * @return string resulting from concatanating the text/cdata child
   *         nodes' values.
   */
  private String getScriptString (Element elem)
  {
    StringBuffer strBuf = new StringBuffer();
    for (Node n = elem.getFirstChild (); n != null; n = n.getNextSibling ())
    {
      switch (n.getNodeType())
      {
      case Node.TEXT_NODE:
      case Node.CDATA_SECTION_NODE:
        strBuf.append(((CharacterData) n).getData());
        break;
      default:
        break;
      }
    }
    return strBuf.toString();
  }


  /**
   * Implement SAX error handler for default reporting.
   */
  class ExtErrorHandler implements ErrorHandler
  {
    String m_xmlID = null;

    public ExtErrorHandler(String identifier)
    {
      m_xmlID = identifier;
    }

    /**
     * Receive notification of a warning.
     *
     * <p>SAX parsers will use this method to report conditions that
     * are not errors or fatal errors as defined by the XML 1.0
     * recommendation.  The default behaviour is to take no action.</p>
     *
     * <p>The SAX parser must continue to provide normal parsing events
     * after invoking this method: it should still be possible for the
     * application to process the document through to the end.</p>
     *
     * @param exception The warning information encapsulated in a
     *                  SAX parse exception.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see org.xml.sax.SAXParseException
     */
    public void warning (SAXParseException exception)
      throws SAXException
    {
      System.out.println("Parser warning: "+exception.getMessage());
    }


    /**
     * Receive notification of a recoverable error.
     *
     * <p>This corresponds to the definition of "error" in section 1.2
     * of the W3C XML 1.0 Recommendation.  For example, a validating
     * parser would use this callback to report the violation of a
     * validity constraint.  The default behaviour is to take no
     * action.</p>
     *
     * <p>The SAX parser must continue to provide normal parsing events
     * after invoking this method: it should still be possible for the
     * application to process the document through to the end.  If the
     * application cannot do so, then the parser should report a fatal
     * error even if the XML 1.0 recommendation does not require it to
     * do so.</p>
     *
     * @param exception The error information encapsulated in a
     *                  SAX parse exception.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see org.xml.sax.SAXParseException
     */
    public void error (SAXParseException exception)
      throws SAXException
    {
      throw exception;
      // System.out.println("Parser error: "+exception.getMessage());
    }

    /**
     * Receive notification of a non-recoverable error.
     *
     * <p>This corresponds to the definition of "fatal error" in
     * section 1.2 of the W3C XML 1.0 Recommendation.  For example, a
     * parser would use this callback to report the violation of a
     * well-formedness constraint.</p>
     *
     * <p>The application must assume that the document is unusable
     * after the parser has invoked this method, and should continue
     * (if at all) only for the sake of collecting addition error
     * messages: in fact, SAX parsers are free to stop reporting any
     * other events once this method has been invoked.</p>
     *
     * @param exception The error information encapsulated in a
     *                  SAX parse exception.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see org.xml.sax.SAXParseException
     */
    public void fatalError (SAXParseException exception)
      throws SAXException
    {
    }

  }

}
