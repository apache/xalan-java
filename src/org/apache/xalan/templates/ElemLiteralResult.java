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
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.util.StringTokenizer;
import org.apache.xalan.utils.QName;
import org.apache.xalan.utils.NameSpace;
import org.apache.xpath.XPathContext;
import org.apache.xalan.utils.StringToStringTable;
import org.apache.xalan.utils.NameSpace;
import org.apache.xalan.utils.StringVector;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.ResultTreeHandler;

import java.io.*;
import java.util.*;


/**
 * <meta name="usage" content="advanced"/>
 * Implement a Literal Result Element.
 * @see <a href="http://www.w3.org/TR/xslt#literal-result-element">literal-result-element in XSLT Specification</a>
 */
public class ElemLiteralResult extends ElemUse
{
  /**
   * The created element node will have the attribute nodes 
   * that were present on the element node in the stylesheet tree, 
   * other than attributes with names in the XSLT namespace.
   */
  private Vector m_avts = null;
  
  private Vector m_xslAttr = null;
  
  /**
   * Set a literal result attribute (AVTs only).
   */
  public void addLiteralResultAttribute(AVT avt)
  {
    if(null == m_avts)
      m_avts = new Vector();
    m_avts.addElement(avt);     
  }
  
  /**
   * Set a literal result attribute (used for xsl attributes).
   */
  public void addLiteralResultAttribute(String att)
  {
    if(null == m_xslAttr)
      m_xslAttr = new Vector();
    m_xslAttr.addElement(att);      
  }
  
  /**
   * Get a literal result attribute by name.
   */
  public AVT getLiteralResultAttribute(String name)
  {
    if(null != m_avts)
    {
      int nAttrs = m_avts.size();
      for(int i = (nAttrs-1); i >= 0; i--)
      {
        AVT avt = (AVT)m_avts.elementAt(i);
        if(avt.getRawName().equals(name))
        {
          return avt;      
        }
      } // end for
    }
    return null;  
  } 
  
  /**
   * The namespace of the element to be created.
   */
  private String m_namespace;
  
  /**
   * Set the m_namespace of the LRE.
   */
  public void setNamespace(String ns)
  {
    m_namespace = ns;
  }

  /**
   * Get the m_namespace of the Literal Result Element.
   */
  public String getNamespace()
  {
    return m_namespace;
  }

  /**
   * The raw name of the element to be created.
   */
  private String m_localName;
  
  /**
   * Set the local name of the LRE.
   */
  public void setLocalName(String localName)
  {
    m_localName = localName;
  }

  /**
   * Get the local name of the Literal Result Element.
   */
  public String getLocalName()
  {
    return m_localName;
  }

  
  /**
   * The raw name of the element to be created.
   */
  private String m_rawName;
  
  /**
   * Set the raw name of the LRE.
   */
  public void setRawName(String rawName)
  {
    m_rawName = rawName;
  }

  /**
   * Get the raw name of the Literal Result Element.
   */
  public String getRawName()
  {
    return m_rawName;
  }

  /**
   * The "extension-element-prefixes" property, actually contains URIs. 
   */
  private StringVector m_ExtensionElementURIs;

  /**
   * Set the "extension-element-prefixes" property. 
   * @see <a href="http://www.w3.org/TR/xslt#extension-element">extension-element in XSLT Specification</a>
   */
  public void setExtensionElementPrefixes (StringVector v)
  {
    m_ExtensionElementURIs = v;
  }

  /**
   * Get and "extension-element-prefix" property. 
   * @see <a href="http://www.w3.org/TR/xslt#extension-element">extension-element in XSLT Specification</a>
   */
  public String getExtensionElementPrefix(int i)
    throws ArrayIndexOutOfBoundsException
  {
    if(null == m_ExtensionElementURIs)
      throw new ArrayIndexOutOfBoundsException();
    return m_ExtensionElementURIs.elementAt(i);
  }
  
  /**
   * Get the number of "extension-element-prefixes" Strings. 
   * @see <a href="http://www.w3.org/TR/xslt#extension-element">extension-element in XSLT Specification</a>
   */
  public int getExtensionElementPrefixCount()
  {
    return (null != m_ExtensionElementURIs) 
           ? m_ExtensionElementURIs.size() : 0;
  }
  
  /**
   * Get and "extension-element-prefix" property. 
   * @see <a href="http://www.w3.org/TR/xslt#extension-element">extension-element in XSLT Specification</a>
   */
  public boolean containsExtensionElementURI(String uri)
  {
    if(null == m_ExtensionElementURIs)
      return false;
    return m_ExtensionElementURIs.contains(uri);
  }  

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_LITERALRESULT;
  }
  
  /** 
   * Return the node name.
   */
  public String getNodeName()
  {
    // TODO: Need prefix.
    return m_rawName;
  }
  
  /**
   * The XSLT version as specified by this element.
   */
  private String m_version;
  
  /**
   * Set the "version" property. 
   * @see <a href="http://www.w3.org/TR/xslt#forwards">forwards in XSLT Specification</a>
   */
  public void setVersion (String v)
  {
    m_version = v;
  }
  
  /**
   * The "exclude-result-prefixes" property.
   */
  private StringVector m_excludeResultPrefixes;
  
  /**
   * Set the "exclude-result-prefixes" property. 
   * The designation of a namespace as an excluded namespace is 
   * effective within the subtree of the stylesheet rooted at 
   * the element bearing the exclude-result-prefixes or 
   * xsl:exclude-result-prefixes attribute; a subtree rooted 
   * at an xsl:stylesheet element does not include any stylesheets 
   * imported or included by children of that xsl:stylesheet element.
   * @see <a href="http://www.w3.org/TR/xslt#literal-result-element">literal-result-element in XSLT Specification</a>
   */
  public void setExcludeResultPrefixes (StringVector v)
  {
    m_excludeResultPrefixes = v;
  }
  
  /**
   * Tell if the result namespace decl should be excluded.  Should be called before 
   * namespace aliasing (I think).
   */
  private boolean excludeResultNSDecl(String prefix, String uri)
    throws SAXException
  {
    if(null != m_excludeResultPrefixes)
    {  
      if (m_excludeResultPrefixes.contains(prefix))
        return true;
    } 
    return false;    
  }
  
  /**
   * Combine the parent's namespaces with this namespace 
   * for fast processing, taking care to reference the 
   * parent's namespace if this namespace adds nothing new.
   * (Recursive method, walking the elements depth-first, 
   * processing parents before children).
   * Overide super method to handle exclude-result-prefix attribute.
   */
  public void resolvePrefixTables()
    throws SAXException
  {
    // Always start with a fresh prefix table!
    m_prefixTable = null;
    Vector m_declaredPrefixes = getDeclaredPrefixes();
    
    // If we have declared declarations, then we look for 
    // a parent that has namespace decls, and add them 
    // to this element's decls.  Otherwise we just point 
    // to the parent that has decls.
    if(null != m_declaredPrefixes)
    {
      // Add this element's declared prefixes to the 
      // prefix table.
      int n = m_declaredPrefixes.size();
      for(int i = 0; i < n; i++)
      {
        XMLNSDecl decl = (XMLNSDecl)m_declaredPrefixes.elementAt(i);
        String prefix = decl.getPrefix();
        String uri = decl.getURI();
        boolean shouldExclude = excludeResultNSDecl(prefix, uri);
        // Create a new prefix table if one has not already been created.
        if(null == m_prefixTable)
          m_prefixTable = new Vector();
        m_prefixTable.addElement(new XMLNSDecl(prefix, uri, shouldExclude));
      }
    }
    
    ElemTemplateElement parent = (ElemTemplateElement)this.getParentNode();
    if(null != parent)
    {
      // The prefix table of the parent should never be null!
      Vector prefixes = parent.m_prefixTable;
      if (null == m_excludeResultPrefixes &&  null == m_prefixTable)
      {
        // Nothing to combine, so just use parent's table!
          this.m_prefixTable = parent.m_prefixTable;
      }
      else 
      { 
        if (null == m_prefixTable)
          m_prefixTable = new Vector();
      
        // Add the prefixes from the parent's prefix table.
        int n = prefixes.size();
        for(int i = 0; i < n; i++)
        {
          XMLNSDecl decl = (XMLNSDecl)prefixes.elementAt(i);
          boolean isexcluded = decl.getIsExcluded();
          if (!isexcluded)
          {  
            boolean shouldExclude = excludeResultNSDecl(decl.getPrefix(), decl.getURI());
            if(shouldExclude != isexcluded)
            {
              decl = new XMLNSDecl(decl.getPrefix(), decl.getURI(), shouldExclude);
            }
          }
          m_prefixTable.addElement(decl);
        }

      }
    }
    else if(null == m_prefixTable)
    {
      // Must be stylesheet element without any result prefixes!
      m_prefixTable = new Vector();
    }
    
    // Resolve the children's prefix tables.
    for(ElemTemplateElement child = m_firstChild; 
        child != null; child = child.m_nextSibling)
    {
      child.resolvePrefixTables();
    }
  }
  

  /**
   * Copy a Literal Result Element into the Result tree, copy the 
   * non-excluded namespace attributes, copy the attributes not 
   * of the XSLT namespace, and execute the children of the LRE.
   * @see <a href="http://www.w3.org/TR/xslt#literal-result-element">literal-result-element in XSLT Specification</a>
   */
  public void execute(TransformerImpl transformer, 
                      Node sourceNode,
                      QName mode)
    throws SAXException
  {
    ResultTreeHandler rhandler = transformer.getResultTreeHandler();
    
    // Add namespace declarations.
    executeNSDecls(transformer);

    rhandler.startElement(getNamespace(), getLocalName(), getRawName());
    
    // Process any possible attributes from xsl:use-attribute-sets first
    super.execute(transformer, sourceNode, mode);
    
    //xsl:version, excludeResultPrefixes???
        
    // Process the list of avts next
    if(null != m_avts)
    {
      int nAttrs = m_avts.size();
      for(int i = (nAttrs-1); i >= 0; i--)
      {
        AVT avt = (AVT)m_avts.elementAt(i);
        XPathContext xctxt = transformer.getXPathContext();
        String stringedValue = avt.evaluate(xctxt, sourceNode, this,
                                            new StringBuffer());
        
        if(null != stringedValue)
        {
          // Important Note: I'm not going to check for excluded namespace 
          // prefixes here.  It seems like it's to expensive, and I'm not 
          // even sure this is right.  But I could be wrong, so this needs 
          // to be tested against other implementations.
          rhandler.addAttribute(avt.getURI(), avt.getName(),
                                avt.getRawName(), 
                                "CDATA", stringedValue);
        }
      } // end for
    }        
    
    // Now process all the elements in this subtree
    // TODO: Process m_extensionElementPrefixes && m_attributeSetsNames
    transformer.executeChildTemplates(this, sourceNode, mode);
    
    rhandler.endElement (getNamespace(), getLocalName(), getRawName());
    unexecuteNSDecls(transformer);
  }
  
  /** Compiling templates requires that we be able to list the AVTs
   * ADDED 9/5/2000 to support compilation experiment
   */
  public Enumeration enumerateLiteralResultAttributes()
  {
	  return (null==m_avts) ? null : m_avts.elements();
  }

}
