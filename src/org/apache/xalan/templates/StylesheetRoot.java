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

import java.net.MalformedURLException;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.io.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import org.apache.xalan.serialize.*;
import org.apache.xml.utils.*;
import org.apache.xpath.*;
import org.apache.xpath.compiler.XPathParser;
import org.apache.xalan.trace.*;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.processor.XSLTSchema;
import org.apache.xalan.transformer.TransformerImpl;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.Templates;
import javax.xml.transform.OutputKeys;

/**
 * <meta name="usage" content="general"/>
 * This class represents the root object of the stylesheet tree.
 */
public class StylesheetRoot extends StylesheetComposed
        implements java.io.Serializable, Templates
{

  /**
   * Uses an XSL stylesheet document.
   * @exception TransformerConfigurationException if the baseIdentifier can not be resolved to a URL.
   */
  public StylesheetRoot() throws TransformerConfigurationException
  {

    super(null);

    setStylesheetRoot(this);

    try
    {
      m_selectDefault = new XPath("node()", this, this, XPath.SELECT);

      initDefaultRule();
    }
    catch (TransformerException se)
    {
      throw new TransformerConfigurationException("Can't init default templates!", se);
    }
  }

  /**
   * The schema used when creating this StylesheetRoot
   */
  private XSLTSchema m_schema;

  /**
   * Creates a StylesheetRoot and retains a pointer to the schema used to create this
   * StylesheetRoot.  The schema may be needed later for an element-available() function call.
   * @exception TransformerConfigurationException if the baseIdentifier can not be resolved to a URL.
   */
  public StylesheetRoot(XSLTSchema schema) throws TransformerConfigurationException
  {

    this();
    m_schema = schema;

  }

  /**
   * Tell if this is the root of the stylesheet tree.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public boolean isRoot()
  {
    return true;
  }

  /**
   * Get the schema associated with this StylesheetRoot
   *
   * @return the schema in effect when this StylesheetRoot was built
   */
  public XSLTSchema getSchema()
  {
    return m_schema;
  }

  //============== Templates Interface ================

  /**
   * Create a new transformation context for this Templates object.
   *
   * @return A Transformer instance, never null.
   */
  public Transformer newTransformer()
  {
    return new TransformerImpl(this);
  }
  

  public Properties getDefaultOutputProps()
  {
    OutputFormat outputProps = m_outputFormatComposed;
    Properties defaultProps = new Properties();
    defaultProps.put(OutputKeys.METHOD, outputProps.getMethod());
    defaultProps.put(OutputKeys.INDENT, outputProps.getIndent() ? "yes" : "no");
    if(null != outputProps.getDoctypePublicId())
      defaultProps.put(OutputKeys.DOCTYPE_PUBLIC, outputProps.getDoctypePublicId());
    if(null != outputProps.getDoctypeSystemId())
      defaultProps.put(OutputKeys.DOCTYPE_SYSTEM, outputProps.getDoctypeSystemId());
    if(null != outputProps.getMediaType())
      defaultProps.put(OutputKeys.MEDIA_TYPE, outputProps.getMediaType());
    defaultProps.put(OutputKeys.OMIT_XML_DECLARATION, outputProps.getOmitXMLDeclaration() ? "yes" : "no");
    defaultProps.put(OutputKeys.STANDALONE, outputProps.getStandalone() ? "yes" : "no");
    if(null != outputProps.getEncoding())
      defaultProps.put(OutputKeys.ENCODING, outputProps.getEncoding());
    if(null != outputProps.getVersion())
      defaultProps.put(OutputKeys.VERSION, outputProps.getVersion());
    return defaultProps;
  }
  
  /**
   * Get the static properties for xsl:output.  The object returned will
   * be a clone of the internal values, and thus it can be mutated
   * without mutating the Templates object, and then handed in to
   * the process method.
   *
   * <p>For XSLT, Attribute Value Templates attribute values will
   * be returned unexpanded (since there is no context at this point).</p>
   *
   * @return A Properties object, not null.
   */
  public Properties getOutputProperties()
  {    
    return getDefaultOutputProps();
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
   * @return A Properties object that may be mutated.
   *
   * @see org.xml.org.apache.xalan.serialize.OutputFormat
   */
  public OutputFormat getOutputFormat()
  {

    OutputFormatExtended cloned = new OutputFormatExtended(0);

    if (m_outputFormatComposed instanceof OutputFormatExtended)
    {
      cloned.copyFrom((OutputFormatExtended) m_outputFormatComposed);
    }
    else
    {
      cloned.copyFrom(m_outputFormatComposed);
    }

    return cloned;
  }

  //============== End Templates Interface ================

  /**
   * Recompose the values of all "composed" properties, meaning
   * properties that need to be combined or calculated from
   * the combination of imported and included stylesheets.  This
   * method determines the proper import precedence of all imported
   * stylesheets.  It then iterates through all of the elements and 
   * properties in the proper order and triggers the individual recompose
   * methods.
   *
   * @throws TransformerException
   */
  public void recompose() throws TransformerException
  {

    // First, we build the global import tree.

    if (null == m_globalImportList)
    {

      Vector importList = new Vector();

      addImports(this, true, importList);

      // Now we create an array and reverse the order of the importList vector.
      // We built the importList vector backwards so that we could use addElement
      // to append to the end of the vector instead of constantly pushing new
      // stylesheets onto the front of the vector and having to shift the rest
      // of the vector each time.

      m_globalImportList = new StylesheetComposed[importList.size()];

      for (int i = importList.size() - 1, j= 0; i >= 0; i--)
        m_globalImportList[j++] = (StylesheetComposed) importList.elementAt(i);
    }

    // Now we make a Vector that is going to hold all of the recomposable elements

    Vector recomposableElements = new Vector();

    // Next, we walk the import tree and add all of the recomposable elements to the vector.

    int n = getGlobalImportCount();

    for (int i = 0; i < n; i++)
    {
      StylesheetComposed imported = getGlobalImport(i);
      imported.recompose(recomposableElements);
    }

    // We sort the elements into ascending order.

    QuickSort2(recomposableElements, 0, recomposableElements.size() - 1);

    // We set up the global variables that will hold the recomposed information.

    m_outputFormatComposed = new OutputFormatExtended(0);
    m_attrSets = new Hashtable();
    m_decimalFormatSymbols = new Hashtable();
    m_keyDecls = new Vector();
    m_namespaceAliasComposed = new Hashtable();
    m_templateList = new TemplateList();
    m_variables = new Vector();

    // Now we sequence through the sorted elements, 
    // calling the recompose() function on each one.  This will call back into the
    // appropriate routine here to actually do the recomposition.
    // Note that we're going backwards, encountering the highest precedence items first.

    for (int i = recomposableElements.size() - 1; i >= 0; i--)
      ((Recomposable) recomposableElements.elementAt(i)).recompose(this);

    // Need final composition of TemplateList.  This adds the wild cards onto the chains.

    m_templateList.compose();

    // Now call the compose() method on every element to give it a chance to adjust
    // based on composed values.

    n = getGlobalImportCount();

    for (int i = 0; i < n; i++)
    {
      StylesheetComposed imported = this.getGlobalImport(i);
      int includedCount = imported.getIncludeCountComposed();
      for (int j = -1; j < includedCount; j++)
      {
        Stylesheet included = imported.getIncludeComposed(j);
        composeTemplates(included);
      }
    }
  }

  /**
   * Call the compose function for each ElemTemplateElement.
   *
   * NEEDSDOC @param templ
   */
  static void composeTemplates(ElemTemplateElement templ)
  {

    templ.compose();

    for (ElemTemplateElement child = templ.getFirstChildElem();
            child != null; child = child.getNextSiblingElem())
    {
      composeTemplates(child);
    }
  }

  /**
   * The combined list of imports.  The stylesheet with the highest
   * import precedence will be at element 0.  The one with the lowest
   * import precedence will be at element length - 1.
   */
  private transient StylesheetComposed[] m_globalImportList;

  /**
   * Add the imports in the given sheet to the working importList vector.
   * The will be added from highest import precedence to
   * least import precedence.  This is a post-order traversal of the
   * import tree as described in <a href="http://www.w3.org/TR/xslt.html#import">the
   * XSLT Recommendation</a>.
   * <p>For example, suppose</p>
   * <p>stylesheet A imports stylesheets B and C in that order;</p>
   * <p>stylesheet B imports stylesheet D;</p>
   * <p>stylesheet C imports stylesheet E.</p>
   * <p>Then the order of import precedence (highest first) is
   * A, C, E, B, D.</p>
   *
   * @param stylesheet Stylesheet to examine for imports.
   * @param addToList  <code>true</code> if this template should be added to the import list
   * @param importList The working import list.  Templates are added here in the reverse
   *        order of priority.  When we're all done, we'll reverse this to the correct
   *        priority in an array.
   */
  protected void addImports(Stylesheet stylesheet, boolean addToList, Vector importList)
  {

    // Get the direct imports of this sheet.

    int n = stylesheet.getImportCount();

    if (n > 0)
    {
      for (int i = 0; i < n; i++)
      {
        Stylesheet imported = stylesheet.getImport(i);

        addImports(imported, true, importList);
      }
    }

    n = stylesheet.getIncludeCount();

    if (n > 0)
    {
      for (int i = 0; i < n; i++)
      {
        Stylesheet included = stylesheet.getInclude(i);

        addImports(included, false, importList);
      }
    }

    if (addToList)
      importList.addElement(stylesheet);

  }

  /**
   * Get a stylesheet from the global import list. 
   * TODO: JKESS PROPOSES SPECIAL-CASE FOR NO IMPORT LIST, TO MATCH COUNT.
   * 
   * NEEDSDOC @param i 
   *
   * NEEDSDOC ($objectName$) @return
   */
  public StylesheetComposed getGlobalImport(int i)
  {
    return m_globalImportList[i];
  }

  /**
   * Get the total number of imports in the global import list.
   * @return The total number of imported stylesheets, including
   * the root stylesheet, thus the number will always be 1 or
   * greater.
   * TODO: JKESS PROPOSES SPECIAL-CASE FOR NO IMPORT LIST, TO MATCH DESCRIPTION.
   */
  public int getGlobalImportCount()
  {
          return (m_globalImportList!=null)
                        ? m_globalImportList.length 
                          : 1;
  }

  /**
   * Given a stylesheet, return the number of the stylesheet
   * in the global import list.
   * @param sheet The stylesheet which will be located in the
   * global import list.
   * @return The index into the global import list of the given stylesheet,
   * or -1 if it is not found (which should never happen).
   */
  public int getImportNumber(StylesheetComposed sheet)
  {

    if (this == sheet)
      return 0;

    int n = getGlobalImportCount();

    for (int i = 0; i < n; i++)
    {
      if (sheet == getGlobalImport(i))
        return i;
    }

    return -1;
  }

  /**
   * This will be set up with the default values, and then the values
   * will be set as stylesheets are encountered.
   */
  private OutputFormatExtended m_outputFormatComposed;

  /**
   * Recompose the output format object from the included elements.
   *
   * NEEDSDOC @param stylesheet
   */
  void recomposeOutput(OutputFormatExtended of)
  {
    m_outputFormatComposed.copyFrom(of);
  }

  /**
   * Get the combined "xsl:output" property with the properties
   * combined from the included stylesheets.  If a xsl:output
   * is not declared in this stylesheet or an included stylesheet,
   * look in the imports.
   * Please note that this returns a reference to the OutputFormat
   * object, not a cloned object, like getOutputFormat does.
   * @see <a href="http://www.w3.org/TR/xslt#output">output in XSLT Specification</a>
   *
   * NEEDSDOC ($objectName$) @return
   */
  public OutputFormat getOutputComposed()
  {

    // System.out.println("getOutputComposed.getIndent: "+m_outputFormatComposed.getIndent());
    // System.out.println("getOutputComposed.getIndenting: "+m_outputFormatComposed.getIndenting());
    return m_outputFormatComposed;
  }

  /** NEEDSDOC Field m_outputMethodSet          */
  private boolean m_outputMethodSet = false;

  /**
   * <meta name="usage" content="internal"/>
   * Find out if an output method has been set by the user.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public boolean isOutputMethodSet()
  {
    return m_outputMethodSet;
  }

  /**
   * Composed set of all included and imported attribute set properties.
   * Each entry is a vector of ElemAttributeSet objects.
   */
  private transient Hashtable m_attrSets;

  /**
   * Recompose the attribute-set declarations.
   *
   * @param attrSet An attribute-set to add to the hashtable of attribute sets.
   */
  void recomposeAttributeSets(ElemAttributeSet attrSet)
  {
    Vector attrSetList = (Vector) m_attrSets.get(attrSet.getName());

    if (null == attrSetList)
    {
      attrSetList = new Vector();

      m_attrSets.put(attrSet.getName(), attrSetList);
    }

    attrSetList.addElement(attrSet);
  }

  /**
   * Get a list "xsl:attribute-set" properties that match the qname.
   * @see <a href="http://www.w3.org/TR/xslt#attribute-sets">attribute-sets in XSLT Specification</a>
   *
   * NEEDSDOC @param name
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws ArrayIndexOutOfBoundsException
   */
  public Vector getAttributeSetComposed(QName name)
          throws ArrayIndexOutOfBoundsException
  {
    return (Vector) m_attrSets.get(name);
  }

  /**
   * Table of DecimalFormatSymbols, keyed by QName.
   */
  private transient Hashtable m_decimalFormatSymbols;

  /**
   * Recompose the decimal-format declarations.
   *
   * @param dfp A DecimalFormatProperties to add to the hashtable of decimal formats.
   */
  void recomposeDecimalFormats(DecimalFormatProperties dfp)
  {
    DecimalFormatSymbols oldDfs =
                  (DecimalFormatSymbols) m_decimalFormatSymbols.get(dfp.getName());
    if (null == oldDfs)
    {
      m_decimalFormatSymbols.put(dfp.getName(), dfp.getDecimalFormatSymbols());
    }
    else if (!dfp.getDecimalFormatSymbols().equals(oldDfs))
    {
      String themsg;
      if (dfp.getName().equals(new QName("")))
      {
        // "Only one default xsl:decimal-format declaration is allowed."
        themsg = XSLMessages.createWarning(
                          XSLTErrorResources.WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED,
                          new Object[0]);
      }
      else
      {
        // "xsl:decimal-format names must be unique. Name {0} has been duplicated."
        themsg = XSLMessages.createWarning(
                          XSLTErrorResources.WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE,
                          new Object[] {dfp.getName()});
      }

      throw new RuntimeException(themsg);   // Should we throw TransformerException instead?
    }

  }

  /**
   * Given a valid element decimal-format name, return the
   * decimalFormatSymbols with that name.
   * <p>It is an error to declare either the default decimal-format or
   * a decimal-format with a given name more than once (even with
   * different import precedence), unless it is declared every
   * time with the same value for all attributes (taking into
   * account any default values).</p>
   * <p>Which means, as far as I can tell, the decimal-format
   * properties are not additive.</p>
   *
   * NEEDSDOC @param name
   * @return null if name is not found.
   */
  public DecimalFormatSymbols getDecimalFormatComposed(QName name)
  {
    return (DecimalFormatSymbols) m_decimalFormatSymbols.get(name);
  }

  /**
   * A list of all key declarations visible from this stylesheet and all
   * lesser stylesheets.
   */
  private transient Vector m_keyDecls;

  /**
   * Recompose the key declarations.
   *
   * @param keyDecl A KeyDeclaration to be added to the vector of key declarations.
   */
  void recomposeKeys(KeyDeclaration keyDecl)
  {
    m_keyDecls.addElement(keyDecl);
  }

  /**
   * Get the composed "xsl:key" properties.
   * @see <a href="http://www.w3.org/TR/xslt#key">key in XSLT Specification</a>
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Vector getKeysComposed()
  {
    return m_keyDecls;
  }

  /**
   * Composed set of all namespace aliases.
   */
  private transient Hashtable m_namespaceAliasComposed;

  /**
   * Recompose the namespace-alias declarations.
   *
   * @param nsAlias A NamespaceAlias object to add to the hashtable of namespace aliases.
   */
  void recomposeNamespaceAliases(NamespaceAlias nsAlias)
  {
    m_namespaceAliasComposed.put(nsAlias.getStylesheetNamespace(),
                                 nsAlias);
  }

  /**
   * Get the "xsl:namespace-alias" property.
   * Return the NamespaceAlias for a given namespace uri.
   * @see <a href="http://www.w3.org/TR/xslt#literal-result-element">literal-result-element in XSLT Specification</a>
   *
   * @param uri non-null reference to namespace that is to be aliased.
   *
   * @return NamespaceAlias that matches uri, or null if no match.
   */
  public NamespaceAlias getNamespaceAliasComposed(String uri)
  {
    return (NamespaceAlias) ((null == m_namespaceAliasComposed) 
                    ? null : m_namespaceAliasComposed.get(uri));
  }

  /**
   * The "xsl:template" properties.
   */
  private transient TemplateList m_templateList;

  /**
   * Recompose the template declarations.
   *
   * @param template An ElemTemplate object to add to the template list.
   */
  void recomposeTemplates(ElemTemplate template)
  {
    m_templateList.setTemplate(template);
  }

  /**
   * NEEDSDOC Method getTemplateListComposed 
   *
   *
   * NEEDSDOC (getTemplateListComposed) @return
   */
  public final TemplateList getTemplateListComposed()
  {
    return m_templateList;
  }

  /**
   * Get an "xsl:template" property by node match. This looks in the imports as
   * well as this stylesheet.
   * @see <a href="http://www.w3.org/TR/xslt#section-Defining-Template-Rules">section-Defining-Template-Rules in XSLT Specification</a>
   *
   * NEEDSDOC @param support
   * NEEDSDOC @param targetNode
   * NEEDSDOC @param mode
   * NEEDSDOC @param quietConflictWarnings
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws TransformerException
   */
  public ElemTemplate getTemplateComposed(XPathContext support,
                                          Node targetNode,
                                          QName mode,
                                          int maxImportLevel,
                                          boolean quietConflictWarnings)
            throws TransformerException
  {
    return m_templateList.getTemplate(support, targetNode, mode, maxImportLevel,
                                                                quietConflictWarnings);
  }

  /**
   * Get an "xsl:template" property. This looks in the imports as
   * well as this stylesheet.
   * @see <a href="http://www.w3.org/TR/xslt#section-Defining-Template-Rules">section-Defining-Template-Rules in XSLT Specification</a>
   *
   * NEEDSDOC @param qname
   *
   * NEEDSDOC ($objectName$) @return
   */
  public ElemTemplate getTemplateComposed(QName qname)
  {
    return m_templateList.getTemplate(qname);
  }
  
  /**
   * Composed set of all variables and params.
   */
  private transient Vector m_variables;

  /**
   * Recompose the top level variable and parameter declarations.
   *
   * @param elemVar A top level variable or parameter to be added to the Vector.
   */
  void recomposeVariables(ElemVariable elemVar)
  {
    // Don't overide higher priority variable        
    if (getVariableOrParamComposed(elemVar.getName()) == null)
      m_variables.addElement(elemVar);
  }

  /**
   * Get an "xsl:variable" property.
   * @see <a href="http://www.w3.org/TR/xslt#top-level-variables">top-level-variables in XSLT Specification</a>
   *
   * NEEDSDOC @param qname
   *
   * NEEDSDOC ($objectName$) @return
   */
  public ElemVariable getVariableOrParamComposed(QName qname)
  {
    if (null != m_variables)
    {
      int n = m_variables.size();

      for (int i = 0; i < n; i++)
      {
        ElemVariable var = (ElemVariable)m_variables.elementAt(i);
        if(var.getName().equals(qname))
          return var;
      }
    }

    return null;
  }

  /**
   * Get all global "xsl:variable" properties in scope for this stylesheet.
   * @see <a href="http://www.w3.org/TR/xslt#top-level-variables">top-level-variables in XSLT Specification</a>
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Vector getVariablesAndParamsComposed()
  {
    return m_variables;
  }

  /**
   * A list of properties that specify how to do space
   * stripping. This uses the same exact mechanism as Templates.
   */
  private TemplateList m_whiteSpaceInfoList;

  /**
   * Recompose the strip-space and preserve-space declarations.
   *
   * @param wsi A WhiteSpaceInfo element to add to the list of WhiteSpaceInfo elements.
   */
  void recomposeWhiteSpaceInfo(WhiteSpaceInfo wsi)
  {
    if (null == m_whiteSpaceInfoList)
      m_whiteSpaceInfoList = new TemplateList();

    m_whiteSpaceInfoList.setTemplate(wsi);
  }

  /**
   * Check to see if the caller should bother with check for
   * whitespace nodes.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public boolean shouldCheckWhitespace()
  {
    return null != m_whiteSpaceInfoList;
  }

  /**
   * Get information about whether or not an element should strip whitespace.
   * @see <a href="http://www.w3.org/TR/xslt#strip">strip in XSLT Specification</a>
   *
   * NEEDSDOC @param support
   * NEEDSDOC @param targetElement
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws TransformerException
   */
  public WhiteSpaceInfo getWhiteSpaceInfo(
          XPathContext support, Element targetElement) throws TransformerException
  {

    if (null != m_whiteSpaceInfoList)
      return (WhiteSpaceInfo) m_whiteSpaceInfoList.getTemplate(support,
              targetElement, null, -1, false);
    else
      return null;
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
   *
   * NEEDSDOC ($objectName$) @return
   */
  public final ElemTemplate getDefaultTextRule()
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
   *
   * NEEDSDOC ($objectName$) @return
   */
  public final ElemTemplate getDefaultRule()
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
   *
   * NEEDSDOC ($objectName$) @return
   */
  public final ElemTemplate getDefaultRootRule()
  {
    return m_defaultRootRule;
  }

  /**
   * Used for default selection.
   */
  XPath m_selectDefault;

  /**
   * Create the default rule if needed.
   *
   * @throws TransformerException
   */
  private void initDefaultRule() throws TransformerException
  {

    // Then manufacture a default
    m_defaultRule = new ElemTemplate();

    m_defaultRule.setStylesheet(this);

    XPath defMatch = new XPath("*", this, this, XPath.MATCH);

    m_defaultRule.setMatch(defMatch);

    ElemApplyTemplates childrenElement = new ElemApplyTemplates();

    childrenElement.setIsDefaultTemplate(true);
    m_defaultRule.appendChild(childrenElement);

    // -----------------------------
    m_defaultTextRule = new ElemTemplate();

    m_defaultTextRule.setStylesheet(this);

    defMatch = new XPath("text() | @*", this, this, XPath.MATCH);

    m_defaultTextRule.setMatch(defMatch);

    ElemValueOf elemValueOf = new ElemValueOf();

    m_defaultTextRule.appendChild(elemValueOf);

    XPath selectPattern = new XPath(".", this, this, XPath.SELECT);

    elemValueOf.setSelect(selectPattern);

    //--------------------------------
    m_defaultRootRule = new ElemTemplate();

    m_defaultRootRule.setStylesheet(this);

    defMatch = new XPath("/", this, this, XPath.MATCH);

    m_defaultRootRule.setMatch(defMatch);

    childrenElement = new ElemApplyTemplates();

    childrenElement.setIsDefaultTemplate(true);
    m_defaultRootRule.appendChild(childrenElement);
  }

  /**
   * This is a generic version of C.A.R Hoare's Quick Sort
   * algorithm.  This will handle arrays that are already
   * sorted, and arrays with duplicate keys.  It was lifted from
   * the NodeSorter class but should probably be eliminated and replaced
   * with a call to Collections.sort when we migrate to Java2.<BR>
   *
   * If you think of a one dimensional array as going from
   * the lowest index on the left to the highest index on the right
   * then the parameters to this function are lowest index or
   * left and highest index or right.  The first time you call
   * this function it will be with the parameters 0, a.length - 1.
   *
   * @param a       an integer array
   * @param lo0     left boundary of array partition
   * @param hi0     right boundary of array partition
   *
   * NEEDSDOC @param v
   * NEEDSDOC @param i
   * NEEDSDOC @param j
   */

  private void QuickSort2(Vector v, int lo0, int hi0)
    {
      int lo = lo0;
      int hi = hi0;

      if ( hi0 > lo0)
      {
        // Arbitrarily establishing partition element as the midpoint of
        // the array.
        Recomposable midNode = (Recomposable) v.elementAt( ( lo0 + hi0 ) / 2 );

        // loop through the array until indices cross
        while( lo <= hi )
        {
          // find the first element that is greater than or equal to
          // the partition element starting from the left Index.
          while( (lo < hi0) && (((Recomposable) v.elementAt(lo)).compareTo(midNode) < 0) )
          {
            ++lo;
          } // end while

          // find an element that is smaller than or equal to
          // the partition element starting from the right Index.
          while( (hi > lo0) && (((Recomposable) v.elementAt(hi)).compareTo(midNode) > 0) )          {
            --hi;
          }

          // if the indexes have not crossed, swap
          if( lo <= hi )
          {
            Recomposable node = (Recomposable) v.elementAt(lo);
            v.setElementAt(v.elementAt(hi), lo);
            v.setElementAt(node, hi);

            ++lo;
            --hi;
          }
        }

        // If the right index has not reached the left side of array
        // must now sort the left partition.
        if( lo0 < hi )
        {
          QuickSort2( v, lo0, hi );
        }

        // If the left index has not reached the right side of array
        // must now sort the right partition.
        if( lo < hi0 )
        {
          QuickSort2( v, lo, hi0 );
        }
      }
    } // end QuickSort2  */
}
