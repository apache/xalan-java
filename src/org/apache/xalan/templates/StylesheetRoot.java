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

import org.w3c.dom.*;
import java.util.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import org.apache.xml.serialize.*;

import org.apache.xalan.utils.*;
import org.apache.xalan.xpath.*;
import org.apache.xalan.trace.*;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.transformer.TransformerImpl;
import trax.Transformer;
import trax.ProcessorException;
import trax.Templates;

/**
 * <meta name="usage" content="general"/>
 * This class represents the root object of the stylesheet tree.
 */
public class StylesheetRoot 
  extends StylesheetComposed 
  implements java.io.Serializable, Templates
{
  /**
   * Uses an XSL stylesheet document.
   * @param transformer  The XSLTProcessor implementation.
   * @param baseIdentifier The file name or URL for the XSL stylesheet.
   * @exception ProcessorException if the baseIdentifier can not be resolved to a URL.
   */
  public StylesheetRoot()
    throws ProcessorException
  {
    super(null);
    setStylesheetRoot(this);
    try
    {
      initDefaultRule();
    }
    catch(SAXException se)
    {
      throw new ProcessorException("Can't init default templates!", se);
    }
  }
  
  /**
   * Tell if this is the root of the stylesheet tree.
   */
  public boolean isRoot()
  {
    return true;
  }
  
  
  //============== Templates Interface ================
  
  /**
   * Create a new transformation context for this Templates object.
   */
  public Transformer newTransformer()
  {
    return new TransformerImpl(this);
  }
      
  /**
   * Get the properties for xsl:output.  The object returned will 
   * be a clone of the internal values, and thus it can be mutated 
   * without mutating the Templates object, and then handed in to 
   * the process method.
   * <p>A stylesheet may contain multiple xsl:output elements and may 
   * include or import stylesheets that also contain xsl:output elements. 
   * All the xsl:output elements occurring in a stylesheet are merged 
   * into a single effective xsl:output element. For the 
   * cdata-section-elements attribute, the effective value is the 
   * union of the specified values. For other attributes, the effective 
   * value is the specified value with the highest import precedence. 
   * It is an error if there is more than one such value for an attribute. 
   * An XSLT processor may signal the error; if it does not signal the 
   * error, if should recover by using the value that occurs last in 
   * the stylesheet. The values of attributes are defaulted after 
   * the xsl:output elements have been merged; different output 
   * methods may have different default values for an attribute.</p>
   * @see <a href="http://www.w3.org/TR/xslt#output">output in XSLT Specification</a>
   * @return A OutputProperties object that may be mutated.
   * 
   * @see org.xml.serialize.OutputFormat
   */
  public OutputFormat getOutputFormat()
  {
    OutputFormat cloned = new OutputFormat();
    cloned.setPreserveSpace(true);
    cloned.setCDataElements(m_outputFormatComposed.getCDataElements());
    cloned.setDoctype(m_outputFormatComposed.getDoctypePublic(), m_outputFormatComposed.getDoctypeSystem());
    cloned.setEncoding(m_outputFormatComposed.getEncoding());
    cloned.setIndent(m_outputFormatComposed.getIndent());
    cloned.setIndenting(m_outputFormatComposed.getIndenting());
    cloned.setLineSeparator(m_outputFormatComposed.getLineSeparator());
    cloned.setLineWidth(m_outputFormatComposed.getLineWidth());
    cloned.setMediaType(m_outputFormatComposed.getMediaType());
    cloned.setMethod(m_outputFormatComposed.getMethod());
    cloned.setNonEscapingElements(m_outputFormatComposed.getNonEscapingElements());
    cloned.setOmitXMLDeclaration(m_outputFormatComposed.getOmitXMLDeclaration());
    cloned.setPreserveSpace(m_outputFormatComposed.getPreserveSpace());
    cloned.setStandalone(m_outputFormatComposed.getStandalone());
    cloned.setVersion(m_outputFormatComposed.getVersion());
    return cloned;
  }

  //============== End Templates Interface ================
  
  /**
   * Recompose the values of all "composed" properties, meaning 
   * properties that need to be combined or calculated from 
   * the combination of imported and included stylesheets.
   */
  public void recompose()
    throws SAXException
  {
    recomposeImports();
    recomposeOutput();
    
    int n = getGlobalImportCount();
    for(int i = 0; i < n; i++)
    {
      StylesheetComposed sheet = getGlobalImport(i);
      if(sheet != this) // already done
        sheet.recomposeImports();
      sheet.recomposeIncludes(sheet);
      sheet.recomposeAttributeSets();
      sheet.recomposeDecimalFormats();
      sheet.recomposeKeys();
      sheet.recomposeNamespaceAliases();
      sheet.recomposeParams();
      sheet.recomposeTemplates();
      sheet.recomposeVariables();
      sheet.recomposeWhiteSpaceInfo();
    }
  }
  
  /**
   * This will be set up with the default values, and then the values 
   * will be set as stylesheets are encountered.
   */
  private OutputFormat m_outputFormatComposed;

  /**
   * Get the combined "xsl:output" property with the properties 
   * combined from the included stylesheets.  If a xsl:output 
   * is not declared in this stylesheet or an included stylesheet, 
   * look in the imports. 
   * Please note that this returns a reference to the OutputFormat
   * object, not a cloned object, like getOutputFormat does.
   * @see <a href="http://www.w3.org/TR/xslt#output">output in XSLT Specification</a>
   */
  public OutputFormat getOutputComposed()
  {
    return m_outputFormatComposed;
  }
  
  /**
   * Recompose the output format object from the included elements.
   */
  public void recomposeOutput()
  {
    m_outputFormatComposed = new OutputFormat();
    m_outputFormatComposed.setPreserveSpace(true);
    recomposeOutput(this);
  }
  
  
  /**
   * Recompose the output format object from the included elements.
   */
  private void recomposeOutput(Stylesheet stylesheet)
  {
    // Get the direct imports of this sheet.
    int n = stylesheet.getImportCount();
    if(n > 0)
    {
      for(int i = 0; i < n; i++)
      {
        Stylesheet imported = stylesheet.getImport(i);
        recomposeOutput(imported);
      }
    }
    
    n = stylesheet.getIncludeCount();
    if(n > 0)
    {
      for(int i = 0; i < n; i++)
      {
        Stylesheet included = stylesheet.getInclude(i);
        recomposeOutput(included);
      }
    }
    
    OutputFormatExtended of = getOutput();
    if(null != of)
    {
      if(of.cdataElementsHasBeenSet())
        m_outputFormatComposed.setCDataElements(of.getCDataElements());
      if(of.doctypePublicHasBeenSet())
        m_outputFormatComposed.setDoctype(of.getDoctypePublic(), 
                                          m_outputFormatComposed.getDoctypeSystem());
      if(of.doctypeSystemHasBeenSet())
        m_outputFormatComposed.setDoctype(m_outputFormatComposed.getDoctypePublic(), 
                                          of.getDoctypeSystem());
      if(of.encodingHasBeenSet())
        m_outputFormatComposed.setEncoding(of.getEncoding());
      if(of.indentHasBeenSet())
        m_outputFormatComposed.setIndent(of.getIndent());
      if(of.indentingHasBeenSet())
        m_outputFormatComposed.setIndenting(of.getIndenting());
      if(of.mediaTypeHasBeenSet())
        m_outputFormatComposed.setMediaType(of.getMediaType());
      if(of.methodHasBeenSet())
        m_outputFormatComposed.setMethod(of.getMethod());
      if(of.nonEscapingElementsHasBeenSet())
        m_outputFormatComposed.setNonEscapingElements(of.getNonEscapingElements());
      if(of.omitXmlDeclarationHasBeenSet())
        m_outputFormatComposed.setOmitXMLDeclaration(of.getOmitXMLDeclaration());
      if(of.standaloneHasBeenSet())
        m_outputFormatComposed.setStandalone(of.getStandalone());
      if(of.versionHasBeenSet())
        m_outputFormatComposed.setVersion(of.getVersion());
    }
  }

  
  private boolean m_outputMethodSet = false;

  /**
   * <meta name="usage" content="internal"/>
   * Find out if an output method has been set by the user.
   */
  public boolean isOutputMethodSet()
  {
    return m_outputMethodSet;
  }
  
  /**
   * The combined list of imports.
   */
  private transient Vector m_globalImportList;
  
  /**
   * Add the imports in the given sheet to the m_globalImportList
   * list.  The will be added from highest import precedence to 
   * least import precidence.
   */
  protected void addImports(Stylesheet stylesheet)
  {
    // Get the direct imports of this sheet.
    int n = stylesheet.getImportCount();
    if(n > 0)
    {
      for(int i = 0; i < n; i++)
      {
        Stylesheet imported = stylesheet.getImport(i);
        m_globalImportList.insertElementAt(imported, 0);
        addImports(imported);
      }
    }
    
    n = stylesheet.getIncludeCount();
    if(n > 0)
    {
      for(int i = 0; i < n; i++)
      {
        Stylesheet included = stylesheet.getInclude(i);
        addImports(included);
      }
    }
  }
  
  /**
   * Recompose the value of the composed import list. This 
   * means any stylesheets of lesser import precidence.
   * <p>For example, suppose</p>
   * <p>stylesheet A imports stylesheets B and C in that order;</p>
   * <p>stylesheet B imports stylesheet D;</p>
   * <p>stylesheet C imports stylesheet E.</p>
   * <p>Then the order of import precedence (highest first) is 
   * A, C, E, B, D.</p>
   */
  protected void recomposeImports()
  {
    if(null == m_globalImportList)
    {
      m_globalImportList = new Vector();
      int n = getImportCount();
      for(int i = 0; i < n; i++)
      {
        StylesheetComposed imported = getImport(i);
        addImports(imported);
      }
      m_globalImportList.insertElementAt(this, 0);
    }
    super.recomposeImports();
  }
  
  /**
   * Get a stylesheet from the global import list.
   */
  protected StylesheetComposed getGlobalImport(int i)
  {
    return (StylesheetComposed)m_globalImportList.elementAt(i);
  }
  
  /**
   * Get the total number of imports in the global import list.
   * @return The total number of imported stylesheets, including 
   * the root stylesheet, thus the number will always be 1 or 
   * greater.
   */
  protected int getGlobalImportCount()
  {
    return m_globalImportList.size();
  }
  
  /**
   * Given a stylesheet, return the number of the stylesheet 
   * in the global import list.
   * @param sheet The stylesheet which will be located in the 
   * global import list.
   * @return The index into the global import list of the given stylesheet,
   * or -1 if it is not found (which should never happen).
   */
  protected int getImportNumber(StylesheetComposed sheet)
  {
    if(this == sheet)
      return 0;
    
    int n = getImportCount();
    for(int i = 0; i < n; i++)
    {
      if(this == getImport(i))
        return i;
    }
    return -1;
  }
  
  /**
   * <meta name="usage" content="advanced"/>
   * The default template to use for text nodes if we don't find
   * anything else.  This is initialized in initDefaultRule().
   * @serial
   */
  private ElemTemplate m_defaultTextRule;
  
  /**
   * <meta name="usage" content="advanced"/>
   * Get the default template for text.
   */
  public ElemTemplate getDefaultTextRule()
  {
    return m_defaultTextRule;
  }
  

  /**
   * <meta name="usage" content="advanced"/>
   * The default template to use if we don't find anything
   * else.  This is initialized in initDefaultRule().
   * @serial
   */
  private ElemTemplate m_defaultRule;

  /**
   * <meta name="usage" content="advanced"/>
   * Get the default template for elements.
   */
  public ElemTemplate getDefaultRule()
  {
    return m_defaultRule;
  }
  
  /**
   * <meta name="usage" content="advanced"/>
   * The default template to use for the root if we don't find
   * anything else.  This is initialized in initDefaultRule().
   * We kind of need this because the defaultRule isn't good
   * enough because it doesn't supply a document context.
   * For now, I default the root document element to "HTML".
   * Don't know if this is really a good idea or not.
   * I suspect it is not.
   * @serial
   */
  private ElemTemplate m_defaultRootRule;

  /**
   * <meta name="usage" content="advanced"/>
   * Get the default template for a root node.
   */
  public ElemTemplate getDefaultRootRule()
  {
    return m_defaultRootRule;
  }

  /**
   * Create the default rule if needed.
   */
  private void initDefaultRule()
    throws SAXException
  {
    XPathParser processor = new XPathParser();

    // Then manufacture a default
    m_defaultRule = new ElemTemplate();
    m_defaultRule.setStylesheet(this);
    XPath defMatch = new XPath();
    processor.initMatchPattern(defMatch, "*", this);
    m_defaultRule.setMatch(defMatch);

    ElemApplyTemplates childrenElement
      = new ElemApplyTemplates();
    childrenElement.setIsDefaultTemplate(true);
    m_defaultRule.appendChild(childrenElement);

    // -----------------------------

    m_defaultTextRule = new ElemTemplate();
    m_defaultTextRule.setStylesheet(this);
    
    defMatch = new XPath();
    processor.initMatchPattern(defMatch, "text() | @*", this);
    m_defaultTextRule.setMatch(defMatch);

    ElemValueOf elemValueOf
      = new ElemValueOf();
    m_defaultTextRule.appendChild(elemValueOf);
    
    XPath selectPattern = new XPath();
    processor.initXPath(selectPattern, ".", this);
    elemValueOf.setSelect(selectPattern);


    //--------------------------------

    m_defaultRootRule = new ElemTemplate();
    m_defaultRootRule.setStylesheet(this);
    
    defMatch = new XPath();
    processor.initMatchPattern(defMatch, "/", this);
    m_defaultRootRule.setMatch(defMatch);

    childrenElement
      = new ElemApplyTemplates();
    childrenElement.setIsDefaultTemplate(true);
    m_defaultRootRule.appendChild(childrenElement);
  }
}
