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
package org.apache.xalan.xslt;

import java.util.TooManyListenersException;
import java.util.Vector;

import org.apache.xalan.xpath.XObject;
import org.apache.xalan.xpath.XNodeSet;
import org.apache.xalan.xpath.XBoolean;
import org.apache.xalan.xpath.XNumber;
import org.apache.xalan.xpath.XNull;
import org.apache.xalan.xpath.XString;
import org.apache.xalan.xpath.xml.XMLParserLiaison;
import org.apache.xalan.xpath.xml.ProblemListener;

import org.apache.xalan.templates.Stylesheet;
//import org.apache.xalan.templates.StylesheetRoot;
//import org.apache.xalan.xslt.StylesheetSpec;
import org.apache.xalan.trace.*;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import org.apache.xml.serialize.OutputFormat;
import org.xml.sax.ext.LexicalHandler;

/**
 * <meta name="usage" content="general"/>
 * The transformation processor -- use {@link org.apache.xalan.xslt.XSLTProcessorFactory} to instantiate an implementation of this interface.
 * It's the responsibility of the implementation (XSLTEngineImpl),
 * collaborating with a XMLParserLiaison, the DOM,
 * and the XPath engine, to transform a source tree
 * of nodes into a result tree according to instructions
 * and templates specified by a stylesheet tree.
 * Use the <code>process(...)</code> are the primary
 * public entry points.
 *
 * Look at the Process class main() method for
 * for an advanced usage example.
 *
 * <p>If you reuse the processor instance, you should call reset() between transformations.</p>
 * @deprecated This compatibility layer will be removed in later releases. 
 */
public interface XSLTProcessor extends DocumentHandler, LexicalHandler
{
  /**
   * Use the XSL stylesheet to transform the XML input, placing the result in the result tree.
   * @param xmlSource  The XML input to be transformed.
   * @param xslStylesheet  The XSL stylesheet to be used for the transformation.  May be null if XML input
   * has an XSL stylesheet PI.
   * @param resultTree The tree where the result of the transformation is placed.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public void process( XSLTInputSource xmlSource,
                       XSLTInputSource xslStylesheet,
                       XSLTResultTarget resultTree)
    throws SAXException;

  /**
   * Compile the XSL stylesheet represented by an XSLTInputSource object into an internal representation,
   * and use it to set the XSLTProcessor Stylesheet property.
   * This operation is required if the XSLTProcessor is to function as a
   * SAX DocumentHandler.
   * If the Stylesheet property has already been set to non-null, this operation
   * calls reset() before a transformation is performed.
   *
   * @param stylesheetSource  The XSL stylesheet.
   * @return The compiled stylesheet object.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public StylesheetRoot processStylesheet(XSLTInputSource stylesheetSource)
    throws SAXException;

  /**
   * Given a URL to (or file name of) an XSL stylesheet,
   * Compile the stylesheet into an internal representation, and use it to
   * set the XSLTProcessor Stylesheet property.
   * This operation is required if the XSLTProcessor is to function as a
   * SAX DocumentHandler.
   * If the Stylesheet property has already been set to non-null, this operation
   * calls reset() before a transformation is performed.
   *
   * @param xsldocURLString  The URL to the XSL stylesheet.
   * @return The compiled stylesheet object.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide
   * the error condition is severe enough to halt processing.
   */
  public StylesheetRoot processStylesheet(String xsldocURLString)
    throws SAXException;

  /**
   * Set the output stream. Required when the XSLTProcessor is being used
   * as a SAX DocumentHandler.
   */
  public void setOutputStream(java.io.OutputStream os);

  /**
   * Reset the XSLTProcessor state.  Must be used after a process() call
   * if the XSLTProcessor instance is to be used again.
   */
  public void reset();

  /**
   * Get the DOM Node from the XSLTInputSource object. Returns null if the XSLTInputSource
   * object does not contain a Node (it may, for example, contain an input stream).
   */
  public Node getSourceTreeFromInput(XSLTInputSource inputSource)
    throws org.xml.sax.SAXException;

  /**
   * Use a compiled stylesheet to set the Stylesheet property for this processor.
   * When this property is set, the process method uses this stylesheet rather than
   * looking for a stylesheet PI if the stylesheet parameter is null.  Also required
   * if you are going to use the XSLTProcessor as a SAX DocumentHandler.
   */
  public void setStylesheet(StylesheetRoot stylesheetRoot);

  /**
   * Get the current Stylesheet setting for this XSLTProcessor.
   */
  public StylesheetRoot getStylesheet();

  /**
   * Get the XMLParserLiaison that this processor uses.
   */
  public XMLParserLiaison getXMLProcessorLiaison();

  /**
   * Get the preferred stylesheet for the XSLTInputSource XML document,
   * as identified by the xml-stylesheet PI, and matching the media and
   * charset criteria. See <a href="http://www.w3.org/TR/xml-stylesheet/">
   * Associating Style Sheets with XML documents</a>.
   * Does not yet handle the LINK REL="stylesheet" syntax.
   *
   * @param media The media attribute to be matched.  May be null, in which
   *              case the prefered stylesheet will be used (i.e., alternate = no).
   * @param title The value of the title attribute to match.  May be null.
   * @param charset The value of the charset attribute to match.  May be null.
   * @returns StylesheetSpec extends XSLTInputSource extends SAX InputSource; the return value
   * can be passed to the processStylesheet method.
   */
  public StylesheetSpec getAssociatedStylesheet(XSLTInputSource source,
                                                      String media,
                                                      String charset)
    throws SAXException;

  /**
   * Get a list of stylesheet specifications for the XSLTInputSource XML document,
   * as identified by the xml-stylesheet PI, and matching the media and
   * charset criteria. See <a href="http://www.w3.org/TR/xml-stylesheet/">
   * Associating Style Sheets with XML documents</a>.
   * Does not yet handle the LINK REL="stylesheet" syntax.
   *
   * @param media The media attribute to be matched.  May be null, in which
   *              case the prefered stylesheet will be used (i.e., alternate = no).
   * @param title The value of the title attribute to match.  May be null.
   * @param charset The value of the charset attribute to match.  May be null.
   * @returns list of StylesheetSpecs (extend XSLTInputSources extend SAX InputSources; a
   * list member may be passsed to the processStylesheet method.
   */
  public Vector getAssociatedStylesheets(XSLTInputSource source,
                                                      String media,
                                                      String charset)
    throws SAXException;


  /**
   * Convenience function to create an XString.
   * @param s A valid string.
   * @return An XString object.
   */
  public XString createXString(String s);

  /**
   * Convenience function to create an XObject.
   * @param o Any java object.
   * @return An XObject object.
   */
  public XObject createXObject(Object o);

  /**
   * Convenience function to create an XNumber.
   * @param d Any double number.
   * @return An XNumber object.
   */
  public XNumber createXNumber(double d);

  /**
   * Convenience function to create an XBoolean.
   * @param b boolean value.
   * @return An XBoolean object.
   */
  public XBoolean createXBoolean(boolean b);

  /**
   * Convenience function to create an XNodeSet.
   * @param nl A NodeList object.
   * @return An XNodeSet object.
   */
  public XNodeSet createXNodeSet(NodeList nl);

  /**
   * Convenience function to create an XNodeSet from a node.
   * @param n A DOM node.
   * @return An XNodeSet object.
   */
  public XNodeSet createXNodeSet(Node n);

  /**
   * Convenience function to create an XNull.
   * @return An XNull object.
   */
  public XNull createXNull();

  /**
   * Submit a top-level stylesheet parameter.  This value can
   * be evaluated in the stylesheet via xsl:param-variable.
   * @param key The name of the param.
   * @param value An XObject that will be used.
   */
  public void setStylesheetParam(String key, XObject value);

  /**
   * Set a top-level stylesheet parameter.  This value can
   * be evaluated via xsl:param-variable.  Note that the value
   * passed is an expression, and not a string.  This means that
   * setStylesheetParam("foo", "hello"); will look for the
   * element "hello".  If you want to pass a string, you'll
   * need to put quotes around it:
   * setStylesheetParam("foo", "'hello'"); will look for the
   * @param key The name of the param.
   * @param expression An expression that will be evaluated.
   */
  public void setStylesheetParam(String key, String expression);

  /**
   * Get the current FormatterListener (SAX DocumentHandler), or null if none has been set.
   */
  public DocumentHandler getFormatterListener();

  /**
   * Set the FormatterListener (the SAX DocumentHandler).
   */
  public void setFormatterListener(DocumentHandler flistener);

  /**
   * Get the current SAX DocumentHandler (the same object as the FormatterListener), or null if none has been set.
   */
  public DocumentHandler getDocumentHandler();

  /**
   * Set the current SAX DocumentHandler (the same
   * object as the FormatterListener).
   */
  public void setDocumentHandler(DocumentHandler listener);

  /**
   * Add a trace listener for the purposes of debugging and diagnosis.
   * @param tl Trace listener to be added.
   */
  public void addTraceListener(TraceListener tl)
    throws TooManyListenersException;

  /**
   * If set to true, template calls are traced.
   */
  public void setTraceTemplates(boolean b);

  /**
   * If set to true, selection events are traced.
   */
  public void setTraceSelect(boolean b);

  /**
   * If set to true (the default is false), as template children are being constructed, debug diagnostics
   * are written to the m_diagnosticsPrintWriter
   * stream.
   */
  public void setTraceTemplateChildren(boolean b);

  /**
   * If set to true (the default), pattern conflict warnings are not printed to the diagnostics stream.
   * @param b true if conflict warnings should be suppressed.
   */
  public void setQuietConflictWarnings(boolean b);

  /**
   * Remove a trace listener.
   * @param tl Trace listener to be removed.
   */
  public void removeTraceListener(TraceListener tl);

  /**
   * If set, diagnostics will be
   * written to the m_diagnosticsPrintWriter stream. If
   * null, diagnostics are turned off. This convenience method calls
   * {@link #setDiagnosticsOutput(java.io.PrintWriter)}.
   */
  public void setDiagnosticsOutput(java.io.OutputStream out);

  /**
   * If set, diagnostics will be
   * written to the m_diagnosticsPrintWriter stream. If
   * null, diagnostics are turned off.
   */
  public void setDiagnosticsOutput(java.io.PrintWriter pw);

  /**
   * Set the problem listener property.
   * The XSL class can have a single listener to be informed
   * of errors and warnings. The problem listener normally controls whether an exception
   * is thrown or not (or the problem listeners can throw its own RuntimeException).
   * @param l A ProblemListener interface.
   */
  public void setProblemListener(ProblemListener l);

  /**
   * Get the problem listener property.
   * The XSL class can have a single listener to be informed
   * of errors and warnings. The problem listener normally controls whether an exception
   * is thrown or not (or the problem listener can throw its own RuntimeException).
   * @return A ProblemListener interface.
   */
  public ProblemListener getProblemListener();

}
