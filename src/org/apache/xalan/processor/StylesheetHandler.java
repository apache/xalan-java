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
package org.apache.xalan.processor;

import java.net.URL;

import java.util.Stack;

import org.apache.xalan.templates.Constants;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.templates.ElemUnknown;
import org.apache.xalan.utils.NodeConsumer;
import org.apache.xalan.utils.XMLCharacterRecognizer;
import org.apache.trax.ProcessorException;
import org.apache.trax.TemplatesBuilder;
import org.apache.trax.Templates;
import org.apache.trax.TransformException;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathFactory;
import org.apache.xpath.compiler.XPathParser;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.functions.Function;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.utils.PrefixResolver;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.helpers.NamespaceSupport;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.w3c.dom.Node;

/**
 * <meta name="usage" content="advanced"/>
 * Initializes and processes a stylesheet via SAX events.
 * This class acts as essentially a state machine, maintaining
 * a ContentHandler stack, and pushing appropriate content
 * handlers as parse events occur.
 */
public class StylesheetHandler
        implements EntityResolver, DTDHandler, ContentHandler, ErrorHandler,
                   TemplatesBuilder, PrefixResolver, NodeConsumer
{

  /**
   * Create a StylesheetHandler object, creating a root stylesheet
   * as the target.
   *
   * NEEDSDOC @param processor
   * @exception May throw ProcessorException if a StylesheetRoot
   * can not be constructed for some reason.
   *
   * @throws ProcessorException
   */
  public StylesheetHandler(StylesheetProcessor processor)
          throws ProcessorException
  {

    // m_schema = new XSLTSchema();
    init(processor);
  }

  /**
   * Static flag to let us know if the XPath functions table
   * has been initialized.
   */
  private static boolean m_xpathFunctionsInited = false;

  /**
   * Do common initialization.
   *
   * NEEDSDOC @param processor
   */
  void init(StylesheetProcessor processor)
  {

    // Not sure about double-check of this flag, but 
    // it seems safe...
    if (false == m_xpathFunctionsInited)
    {
      synchronized (this)
      {
        if (false == m_xpathFunctionsInited)
        {
          m_xpathFunctionsInited = true;

          Function func = new org.apache.xalan.templates.FuncDocument();

          FunctionTable.installFunction("document", func);

          // func = new org.apache.xalan.templates.FuncKey();
          // FunctionTable.installFunction("key", func);
          func = new org.apache.xalan.templates.FuncFormatNumb();

          FunctionTable.installFunction("format-number", func);
        }
      }
    }

    m_stylesheetProcessor = processor;

    // Set the initial content handler.
    m_processors.push(m_schema.getElementProcessor());
    this.pushNewNamespaceSupport();

    // m_includeStack.push(SystemIDResolver.getAbsoluteURI(this.getBaseIdentifier(), null));
    // initXPath(processor, null);
  }

  /**
   * Process an expression string into an XPath.
   * Must be public for access by the AVT class.
   *
   * NEEDSDOC @param str
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws org.xml.sax.SAXException
   */
  public XPath createXPath(String str) throws org.xml.sax.SAXException
  {
    return new XPath(str, getLocator(), this, XPath.SELECT);
  }

  /**
   * Process an expression string into an XPath.
   *
   * NEEDSDOC @param str
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws org.xml.sax.SAXException
   */
  XPath createMatchPatternXPath(String str) throws org.xml.sax.SAXException
  {
    return new XPath(str, getLocator(), this, XPath.MATCH);
  }

  /**
   * Given a namespace, get the corrisponding prefix.  This assumes that
   * the PrevixResolver hold's it's own namespace context, or is a namespace
   * context itself.
   *
   * NEEDSDOC @param prefix
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getNamespaceForPrefix(String prefix)
  {
    return this.getNamespaceSupport().getURI(prefix);
  }

  /**
   * Given a namespace, get the corrisponding prefix.
   *
   * NEEDSDOC @param prefix
   * NEEDSDOC @param context
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getNamespaceForPrefix(String prefix, org.w3c.dom.Node context)
  {

    // Don't need to support this here.  Return the current URI for the prefix, 
    // ignoring the context.
    assert(true, "can't process a context node in StylesheetHandler!");

    return null;
  }

  /**
   * Return the base identifier.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getBaseIdentifier()
  {

    org.xml.sax.Locator locator = getLocator();

    return (null == locator) ? "" : locator.getSystemId();
  }

  /**
   * Test to see if the stack contains the given URL.
   *
   * NEEDSDOC @param stack
   * NEEDSDOC @param url
   *
   * NEEDSDOC ($objectName$) @return
   */
  private boolean stackContains(Stack stack, String url)
  {

    int n = stack.size();
    boolean contains = false;

    for (int i = 0; i < n; i++)
    {
      String url2 = (String) stack.elementAt(i);

      if (url2.equals(url))
      {
        contains = true;

        break;
      }
    }

    return contains;
  }

  ////////////////////////////////////////////////////////////////////
  // Implementation of the TRAX TemplatesBuilder interface.
  ////////////////////////////////////////////////////////////////////

  /**
   * When this object is used as a ContentHandler or ContentHandler, it will
   * create a Templates object, which the caller can get once
   * the SAX events have been completed.
   * @return The stylesheet object that was created during
   * the SAX event process, or null if no stylesheet has
   * been created.
   *
   * @version Alpha
   * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
   *
   * @throws TransformException
   */
  public Templates getTemplates() throws TransformException
  {
    return getStylesheetRoot();
  }

  /**
   * Set the base ID (URL or system ID) for the stylesheet
   * created by this builder.  This must be set in order to
   * resolve relative URLs in the stylesheet.
   * @param baseID Base URL for this stylesheet.
   */
  public void setBaseID(String baseID)
  {
    pushBaseIndentifier(baseID);
  }

  ////////////////////////////////////////////////////////////////////
  // Implementation of the EntityResolver interface.
  ////////////////////////////////////////////////////////////////////

  /**
   * Resolve an external entity.
   *
   * <p>Always return null, so that the parser will use the system
   * identifier provided in the XML document.  This method implements
   * the SAX default behaviour: application writers can override it
   * in a subclass to do special translations such as catalog lookups
   * or URI redirection.</p>
   *
   * @param publicId The public identifer, or null if none is
   *                 available.
   * @param systemId The system identifier provided in the XML
   *                 document.
   * @return The new input source, or null to require the
   *         default behaviour.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.EntityResolver#resolveEntity
   *
   * @throws SAXException
   */
  public InputSource resolveEntity(String publicId, String systemId)
          throws SAXException
  {
    return getCurrentProcessor().resolveEntity(this, publicId, systemId);
  }

  ////////////////////////////////////////////////////////////////////
  // Implementation of DTDHandler interface.
  ////////////////////////////////////////////////////////////////////

  /**
   * Receive notification of a notation declaration.
   *
   * <p>By default, do nothing.  Application writers may override this
   * method in a subclass if they wish to keep track of the notations
   * declared in a document.</p>
   *
   * @param name The notation name.
   * @param publicId The notation public identifier, or null if not
   *                 available.
   * @param systemId The notation system identifier.
   * @see org.xml.sax.DTDHandler#notationDecl
   */
  public void notationDecl(String name, String publicId, String systemId)
  {
    getCurrentProcessor().notationDecl(this, name, publicId, systemId);
  }

  /**
   * Receive notification of an unparsed entity declaration.
   *
   * @param name The entity name.
   * @param publicId The entity public identifier, or null if not
   *                 available.
   * @param systemId The entity system identifier.
   * @param notationName The name of the associated notation.
   * @see org.xml.sax.DTDHandler#unparsedEntityDecl
   */
  public void unparsedEntityDecl(String name, String publicId,
                                 String systemId, String notationName)
  {
    getCurrentProcessor().unparsedEntityDecl(this, name, publicId, systemId,
                                             notationName);
  }

  /**
   * Given a namespace URI, and a local name or a node type, get the processor
   * for the element, or return null if not allowed.
   *
   * NEEDSDOC @param uri
   * NEEDSDOC @param localName
   * NEEDSDOC @param rawName
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws SAXException
   */
  XSLTElementProcessor getProcessorFor(
          String uri, String localName, String rawName) throws SAXException
  {

    XSLTElementProcessor currentProcessor = getCurrentProcessor();
    XSLTElementDef def = currentProcessor.getElemDef();
    XSLTElementProcessor elemProcessor = def.getProcessorFor(uri, localName);

    if (null == elemProcessor
            && Double.valueOf(getStylesheet().getVersion()).doubleValue()
               > Constants.XSLTVERSUPPORTED)
    {
      elemProcessor = def.getProcessorForUnknown(uri, localName);
    }

    if (null == elemProcessor)
      error(rawName + " is not allowed in this position in the stylesheet!",
            null);

    return elemProcessor;
  }

  ////////////////////////////////////////////////////////////////////
  // Implementation of ContentHandler interface.
  ////////////////////////////////////////////////////////////////////

  /**
   * Receive a Locator object for document events.
   * This is called by the parser to push a locator for the
   * stylesheet being parsed. The stack needs to be popped
   * after the stylesheed has been parsed. We pop in in
   * popStylesheet.
   *
   * @param locator A locator for all SAX document events.
   * @see org.xml.sax.ContentHandler#setDocumentLocator
   * @see org.xml.sax.Locator
   */
  public void setDocumentLocator(Locator locator)
  {

    // System.out.println("pushing locator for: "+locator.getSystemId());
    m_stylesheetLocatorStack.push(locator);
  }

  /**
   * The level of the stylesheet we are at.
   */
  private int m_stylesheetLevel = -1;

  /**
   * Receive notification of the beginning of the document.
   *
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#startDocument
   *
   * @throws SAXException
   */
  public void startDocument() throws SAXException
  {
    m_stylesheetLevel++;
  }

  /**
   * Receive notification of the end of the document.
   *
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#endDocument
   *
   * @throws SAXException
   */
  public void endDocument() throws SAXException
  {

    if (null != getStylesheetRoot())
    {
      if (0 == m_stylesheetLevel)
        getStylesheetRoot().recompose();

      // Resolve the result prefix tables in the elements.
      getLastPoppedStylesheet().resolvePrefixTables();
    }
    else
      throw new SAXException("Did not find the stylesheet root!");

    XSLTElementProcessor elemProcessor = getCurrentProcessor();

    if (null != elemProcessor)
      elemProcessor.startNonText(this);

    m_stylesheetLevel--;
  }

  /**
   * Receive notification of the start of a Namespace mapping.
   *
   * <p>By default, do nothing.  Application writers may override this
   * method in a subclass to take specific actions at the start of
   * each element (such as allocating a new tree node or writing
   * output to a file).</p>
   *
   * @param prefix The Namespace prefix being declared.
   * @param uri The Namespace URI mapped to the prefix.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#startPrefixMapping
   *
   * @throws SAXException
   */
  public void startPrefixMapping(String prefix, String uri)
          throws SAXException
  {

    // m_nsSupport.pushContext();
    this.getNamespaceSupport().declarePrefix(prefix, uri);
  }

  /**
   * Receive notification of the end of a Namespace mapping.
   *
   * <p>By default, do nothing.  Application writers may override this
   * method in a subclass to take specific actions at the start of
   * each element (such as allocating a new tree node or writing
   * output to a file).</p>
   *
   * @param prefix The Namespace prefix being declared.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#endPrefixMapping
   *
   * @throws SAXException
   */
  public void endPrefixMapping(String prefix) throws SAXException
  {

    // m_nsSupport.popContext();
  }

  /**
   * Flush the characters buffer.
   *
   * @throws SAXException
   */
  private void flushCharacters() throws SAXException
  {

    XSLTElementProcessor elemProcessor = getCurrentProcessor();

    if (null != elemProcessor)
      elemProcessor.startNonText(this);
  }

  /**
   * Receive notification of the start of an element.
   *
   * @param name The element type name.
   *
   * NEEDSDOC @param uri
   * NEEDSDOC @param localName
   * NEEDSDOC @param rawName
   * @param attributes The specified or defaulted attributes.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#startElement
   *
   * @throws SAXException
   */
  public void startElement(
          String uri, String localName, String rawName, Attributes attributes)
            throws SAXException
  {

    m_elementID++;

    checkForFragmentID(attributes);

    if (!m_shouldProcess)
      return;

    flushCharacters();

    XSLTElementProcessor elemProcessor = getProcessorFor(uri, localName,
                                           rawName);

    this.pushProcessor(elemProcessor);
    elemProcessor.startElement(this, uri, localName, rawName, attributes);
    this.getNamespaceSupport().pushContext();
  }

  /**
   * Receive notification of the end of an element.
   *
   * @param name The element type name.
   * @param attributes The specified or defaulted attributes.
   *
   * NEEDSDOC @param uri
   * NEEDSDOC @param localName
   * NEEDSDOC @param rawName
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#endElement
   *
   * @throws SAXException
   */
  public void endElement(String uri, String localName, String rawName)
          throws SAXException
  {

    m_elementID--;

    if (!m_shouldProcess)
      return;

    if ((m_elementID + 1) == m_fragmentID)
      m_shouldProcess = false;

    flushCharacters();

    XSLTElementProcessor p = getCurrentProcessor();

    p.endElement(this, uri, localName, rawName);
    this.popProcessor();
    this.getNamespaceSupport().popContext();
  }

  /**
   * Receive notification of character data inside an element.
   *
   * @param ch The characters.
   * @param start The start position in the character array.
   * @param length The number of characters to use from the
   *               character array.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#characters
   *
   * @throws SAXException
   */
  public void characters(char ch[], int start, int length) throws SAXException
  {

    if (!m_shouldProcess)
      return;

    XSLTElementProcessor elemProcessor = getCurrentProcessor();
    XSLTElementDef def = elemProcessor.getElemDef();

    if (def.getType() != XSLTElementDef.T_PCDATA)
      elemProcessor = def.getProcessorFor(null, "text()");

    if (null == elemProcessor)
    {

      // If it's whitespace, just ignore it, otherwise flag an error.
      if (!XMLCharacterRecognizer.isWhiteSpace(ch, start, length))
        error(
          "Non-whitespace text is not allowed in this position in the stylesheet!",
          null);
    }
    else
      elemProcessor.characters(this, ch, start, length);
  }

  /**
   * Receive notification of ignorable whitespace in element content.
   *
   * @param ch The whitespace characters.
   * @param start The start position in the character array.
   * @param length The number of characters to use from the
   *               character array.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#ignorableWhitespace
   *
   * @throws SAXException
   */
  public void ignorableWhitespace(char ch[], int start, int length)
          throws SAXException
  {

    if (!m_shouldProcess)
      return;

    getCurrentProcessor().ignorableWhitespace(this, ch, start, length);
  }

  /**
   * Receive notification of a processing instruction.
   *
   * <p>By default, do nothing.  Application writers may override this
   * method in a subclass to take specific actions for each
   * processing instruction, such as setting status variables or
   * invoking other methods.</p>
   *
   * @param target The processing instruction target.
   * @param data The processing instruction data, or null if
   *             none is supplied.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#processingInstruction
   *
   * @throws SAXException
   */
  public void processingInstruction(String target, String data)
          throws SAXException
  {

    if (!m_shouldProcess)
      return;

    flushCharacters();
    getCurrentProcessor().processingInstruction(this, target, data);
  }

  /**
   * Receive notification of a skipped entity.
   *
   * <p>By default, do nothing.  Application writers may override this
   * method in a subclass to take specific actions for each
   * processing instruction, such as setting status variables or
   * invoking other methods.</p>
   *
   * @param name The name of the skipped entity.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#processingInstruction
   *
   * @throws SAXException
   */
  public void skippedEntity(String name) throws SAXException
  {

    if (!m_shouldProcess)
      return;

    getCurrentProcessor().skippedEntity(this, name);
  }

  ////////////////////////////////////////////////////////////////////
  // Implementation of the ErrorHandler interface.
  ////////////////////////////////////////////////////////////////////

  /**
   * <meta name="usage" content="internal"/>
   * Warn the user of an problem.
   *
   * NEEDSDOC @param msg
   * NEEDSDOC @param args
   * @exception XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws SAXException
   */
  public void warn(int msg, Object args[]) throws SAXException
  {

    String formattedMsg = m_XSLMessages.createWarning(msg, args);
    Locator locator = getLocator();
    ErrorHandler handler = m_stylesheetProcessor.getErrorHandler();

    if (null != handler)
      handler.warning(new ProcessorException(formattedMsg, locator));
  }

  /**
   * <meta name="usage" content="internal"/>
   * Assert that a condition is true.  If it is not true, throw an error.
   *
   * NEEDSDOC @param condition
   * NEEDSDOC @param msg
   * @exception throws Runtime Exception.
   */
  private void assert(boolean condition, String msg)
  {
    if (!condition)
      throw new RuntimeException(msg);
  }

  /**
   * <meta name="usage" content="internal"/>
   * Tell the user of an error, and probably throw an
   * exception.
   *
   * NEEDSDOC @param msg
   * NEEDSDOC @param e
   * @exception XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws SAXException
   */
  protected void error(String msg, Exception e) throws SAXException
  {

    Locator locator = getLocator();
    ErrorHandler handler = m_stylesheetProcessor.getErrorHandler();
    ProcessorException pe = (null == e)
                            ? new ProcessorException(msg, locator)
                            : new ProcessorException(msg, locator, e);

    if (null != handler)
      handler.fatalError(pe);
    else
      throw pe;
  }

  /**
   * <meta name="usage" content="internal"/>
   * Tell the user of an error, and probably throw an
   * exception.
   *
   * NEEDSDOC @param msg
   * NEEDSDOC @param args
   * NEEDSDOC @param e
   * @exception XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws SAXException
   */
  protected void error(int msg, Object args[], Exception e)
          throws SAXException
  {

    String formattedMsg = m_XSLMessages.createMessage(msg, args);

    error(formattedMsg, e);
  }

  /**
   * Receive notification of a parser warning.
   *
   * @param e The warning information encoded as an exception.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ErrorHandler#warning
   * @see org.xml.sax.SAXParseException
   *
   * @throws SAXException
   */
  public void warning(SAXParseException e) throws SAXException
  {

    // Need to set up a diagnosticsWriter here?
    System.out.println("WARNING: " + e.getMessage());
    System.out.println(" ID: " + e.getSystemId() + " Line #"
                       + e.getLineNumber() + " Column #"
                       + e.getColumnNumber());
  }

  /**
   * Receive notification of a recoverable parser error.
   *
   * @param e The warning information encoded as an exception.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ErrorHandler#warning
   * @see org.xml.sax.SAXParseException
   *
   * @throws SAXException
   */
  public void error(SAXParseException e) throws SAXException
  {

    // Need to set up a diagnosticsWriter here?
    System.out.println("RECOVERABLE ERROR: " + e.getMessage());
    System.out.println(" ID: " + e.getSystemId() + " Line #"
                       + e.getLineNumber() + " Column #"
                       + e.getColumnNumber());
  }

  /**
   * Report a fatal XML parsing error.
   *
   * <p>The default implementation throws a SAXParseException.
   * Application writers may override this method in a subclass if
   * they need to take specific actions for each fatal error (such as
   * collecting all of the errors into a single report): in any case,
   * the application must stop all regular processing when this
   * method is invoked, since the document is no longer reliable, and
   * the parser may no longer report parsing events.</p>
   *
   * @param e The error information encoded as an exception.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ErrorHandler#fatalError
   * @see org.xml.sax.SAXParseException
   *
   * @throws SAXException
   */
  public void fatalError(SAXParseException e) throws SAXException
  {
    throw e;
  }

  /**
   * If we have a URL to a XML fragment, this is set
   * to false until the ID is found.
   * (warning: I worry that this should be in a stack).
   */
  private boolean m_shouldProcess = true;

  /**
   * If we have a URL to a XML fragment, the value is stored
   * in this string, and the m_shouldProcess flag is set to
   * false until we match an ID with this string.
   * (warning: I worry that this should be in a stack).
   */
  private String m_fragmentIDString;

  /**
   * Keep track of the elementID, so we can tell when
   * is has completed.  This isn't a real ID, but rather
   * a nesting level.  However, it's good enough for
   * our purposes.
   * (warning: I worry that this should be in a stack).
   */
  private int m_elementID = 0;

  /**
   * The ID of the fragment that has been found
   * (warning: I worry that this should be in a stack).
   */
  private int m_fragmentID = 0;

  /**
   * Check to see if an ID attribute matched the #id, called
   * from startElement.
   *
   * NEEDSDOC @param attributes
   */
  private void checkForFragmentID(Attributes attributes)
  {

    if (!m_shouldProcess)
    {
      if ((null != attributes) && (null != m_fragmentIDString))
      {
        int n = attributes.getLength();

        for (int i = 0; i < n; i++)
        {
          String type = attributes.getType(i);

          if (type.equalsIgnoreCase("ID"))
          {
            String val = attributes.getValue(i);

            if (val.equalsIgnoreCase(m_fragmentIDString))
            {
              m_shouldProcess = true;
              m_fragmentID = m_elementID;
            }
          }
        }
      }
    }
  }

  /**
   *  The XSLT Processor for needed services.
   */
  private StylesheetProcessor m_stylesheetProcessor;

  /**
   * Get the XSLT Processor for needed services.
   *
   * NEEDSDOC ($objectName$) @return
   */
  StylesheetProcessor getStylesheetProcessor()
  {
    return m_stylesheetProcessor;
  }

  /** NEEDSDOC Field STYPE_ROOT          */
  static final int STYPE_ROOT = 1;

  /** NEEDSDOC Field STYPE_INCLUDE          */
  static final int STYPE_INCLUDE = 2;

  /** NEEDSDOC Field STYPE_IMPORT          */
  static final int STYPE_IMPORT = 3;

  /** NEEDSDOC Field m_stylesheetType          */
  private int m_stylesheetType = STYPE_ROOT;

  /**
   * Get the type of stylesheet that should be built
   * or is being processed.
   *
   * NEEDSDOC ($objectName$) @return
   */
  int getStylesheetType()
  {
    return m_stylesheetType;
  }

  /**
   * Set the type of stylesheet that should be built
   * or is being processed.
   *
   * NEEDSDOC @param type
   */
  void setStylesheetType(int type)
  {
    m_stylesheetType = type;
  }

  /**
   * The stack of stylesheets being processed.
   */
  private Stack m_stylesheets = new Stack();

  /**
   * Return the stylesheet that this handler is constructing.
   *
   * NEEDSDOC ($objectName$) @return
   */
  Stylesheet getStylesheet()
  {
    return (m_stylesheets.size() == 0)
           ? null : (Stylesheet) m_stylesheets.peek();
  }

  /**
   * Return the stylesheet that this handler is constructing.
   *
   * NEEDSDOC ($objectName$) @return
   */
  Stylesheet getLastPoppedStylesheet()
  {
    return m_lastPoppedStylesheet;
  }

  /**
   * Return the stylesheet that this handler is constructing.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public StylesheetRoot getStylesheetRoot()
  {
    return m_stylesheetRoot;
  }

  /** NEEDSDOC Field m_stylesheetRoot          */
  StylesheetRoot m_stylesheetRoot;

  /** NEEDSDOC Field m_lastPoppedStylesheet          */
  Stylesheet m_lastPoppedStylesheet;

  /**
   * Return the stylesheet that this handler is constructing.
   *
   * NEEDSDOC @param s
   */
  void pushStylesheet(Stylesheet s)
  {

    if (m_stylesheets.size() == 0)
      m_stylesheetRoot = (StylesheetRoot) s;

    m_stylesheets.push(s);
  }

  /**
   * Return the stylesheet that this handler is constructing.
   *
   * NEEDSDOC ($objectName$) @return
   */
  Stylesheet popStylesheet()
  {

    // The stylesheetLocatorStack needs to be popped because
    // a locator was pushed in for this stylesheet by the SAXparser by calling
    // setDocumentLocator().
    m_stylesheetLocatorStack.pop();

    m_lastPoppedStylesheet = (Stylesheet) m_stylesheets.pop();

    return m_lastPoppedStylesheet;
  }

  /**
   * The stack of current processors.
   */
  private Stack m_processors = new Stack();

  /**
   * Get the current XSLTElementProcessor at the top of the stack.
   * @return Valid XSLTElementProcessor, which should never be null.
   */
  XSLTElementProcessor getCurrentProcessor()
  {
    return (XSLTElementProcessor) m_processors.peek();
  }

  /**
   * Push the current XSLTElementProcessor onto the top of the stack.
   *
   * NEEDSDOC @param processor
   */
  void pushProcessor(XSLTElementProcessor processor)
  {
    m_processors.push(processor);
  }

  /**
   * Pop the current XSLTElementProcessor from the top of the stack.
   * @return the XSLTElementProcessor which was popped.
   */
  XSLTElementProcessor popProcessor()
  {
    return (XSLTElementProcessor) m_processors.pop();
  }

  /**
   * The root of the XSLT Schema, which tells us how to
   * transition content handlers, create elements, etc.
   * For the moment at least, this can't be static, since
   * the processors store state.
   */
  private XSLTSchema m_schema = new XSLTSchema();

  /**
   * Get the root of the XSLT Schema, which tells us how to
   * transition content handlers, create elements, etc.
   *
   * NEEDSDOC ($objectName$) @return
   */
  XSLTSchema getSchema()
  {
    return m_schema;
  }

  /**
   * The stack of elements, pushed and popped as events occur.
   */
  private Stack m_elems = new Stack();

  /**
   * Get the current ElemTemplateElement at the top of the stack.
   * @return Valid ElemTemplateElement, which may be null.
   */
  ElemTemplateElement getElemTemplateElement()
  {

    try
    {
      return (ElemTemplateElement) m_elems.peek();
    }
    catch (java.util.EmptyStackException ese)
    {
      return null;
    }
  }

  /**
   * Push the current XSLTElementProcessor to the top of the stack.
   *
   * NEEDSDOC @param elem
   */
  void pushElemTemplateElement(ElemTemplateElement elem)
  {
    m_elems.push(elem);
  }

  /**
   * Get the current XSLTElementProcessor from the top of the stack.
   * @return the ElemTemplateElement which was popped.
   */
  ElemTemplateElement popElemTemplateElement()
  {
    return (ElemTemplateElement) m_elems.pop();
  }

  /**
   * Flag to let us know when we've found an element inside the
   * stylesheet that is not an xsl:import, so we can restrict imports
   * to being the first elements.
   */
  private boolean m_foundNotImport = false;

  /**
   * A XSLMessages instance capable of producing user messages.
   */
  private static XSLMessages m_XSLMessages = new XSLMessages();

  /**
   * Get an XSLMessages instance capable of producing user messages.
   *
   * NEEDSDOC ($objectName$) @return
   */
  XSLMessages getXSLMessages()
  {
    return m_XSLMessages;
  }

  /**
   * This will act as a stack to keep track of the
   * current include base.
   */
  Stack m_baseIdentifiers = new Stack();

  /**
   * Push a base identifier onto the base URI stack.
   *
   * NEEDSDOC @param baseID
   */
  void pushBaseIndentifier(String baseID)
  {

    if (null != baseID)
    {
      int posOfHash = baseID.indexOf('#');

      if (posOfHash > -1)
      {
        m_fragmentIDString = baseID.substring(posOfHash + 1);
        m_shouldProcess = false;
      }
      else
        m_shouldProcess = true;
    }
    else
      m_shouldProcess = true;

    m_baseIdentifiers.push(baseID);
  }

  /**
   * Pop a base URI from the stack.
   * @return baseIdentifier.
   */
  String popBaseIndentifier()
  {
    return (String) m_baseIdentifiers.pop();
  }

  /**
   * The top of this stack should contain the currently processed
   * stylesheet SAX locator object.
   */
  private Stack m_stylesheetLocatorStack = new Stack();

  /**
   * Get the current stylesheet Locator object.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Locator getLocator()
  {

    if (m_stylesheetLocatorStack.isEmpty())
    {
      org.xml.sax.helpers.LocatorImpl locator =
        new org.xml.sax.helpers.LocatorImpl();

      locator.setSystemId(this.getStylesheetProcessor().getDOMsystemID());
      m_stylesheetLocatorStack.push(locator);
    }

    return ((Locator) m_stylesheetLocatorStack.peek());
  }

  /**
   * A stack of URL hrefs for imported stylesheets.  This is
   * used to diagnose circular imports.
   */
  private Stack m_importStack = new Stack();

  /**
   * Push an import href onto the stylesheet stack.
   *
   * NEEDSDOC @param hrefUrl
   */
  void pushImportURL(String hrefUrl)
  {
    m_importStack.push(hrefUrl);
  }

  /**
   * See if the imported stylesheet stack already contains
   * the given URL.
   *
   * NEEDSDOC @param hrefUrl
   *
   * NEEDSDOC ($objectName$) @return
   */
  boolean importStackContains(String hrefUrl)
  {
    return stackContains(m_importStack, hrefUrl);
  }

  /**
   * Pop an import href from the stylesheet stack.
   *
   * NEEDSDOC ($objectName$) @return
   */
  String popImportURL()
  {
    return (String) m_importStack.pop();
  }

  /**
   * If this is set to true, we've already warned about using the
   * older XSLT namespace URL.
   */
  private boolean warnedAboutOldXSLTNamespace = false;

  /**
   * The query/pattern-matcher object.
   */
  private XPathParser m_xpathProcessor = new XPathParser();

  /** NEEDSDOC Field m_nsSupportStack          */
  Stack m_nsSupportStack = new Stack();

  /**
   * NEEDSDOC Method pushNewNamespaceSupport 
   *
   */
  void pushNewNamespaceSupport()
  {
    m_nsSupportStack.push(new NamespaceSupport());
  }

  /**
   * NEEDSDOC Method popNamespaceSupport 
   *
   */
  void popNamespaceSupport()
  {
    m_nsSupportStack.pop();
  }

  /**
   * Get the NamespaceSupport object.
   *
   * NEEDSDOC ($objectName$) @return
   */
  NamespaceSupport getNamespaceSupport()
  {
    return (NamespaceSupport) m_nsSupportStack.peek();
  }

  /** NEEDSDOC Field m_originatingNode          */
  private Node m_originatingNode;

  /**
   * Set the node that is originating the SAX event.
   *
   * NEEDSDOC @param n
   */
  public void setOriginatingNode(Node n)
  {
    m_originatingNode = n;
  }

  /**
   * Set the node that is originating the SAX event.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Node getOriginatingNode()
  {
    return m_originatingNode;
  }
}
