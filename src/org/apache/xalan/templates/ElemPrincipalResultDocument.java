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

import java.lang.InstantiationException;

import java.io.Serializable;

import java.util.Enumeration;
import java.util.Vector;

// Xalan imports
import org.apache.xml.utils.UnImplNode;
import org.apache.xml.utils.NameSpace;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.StringToStringTable;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.ResultNameSpace;
import org.apache.xalan.transformer.ResultTreeHandler;
import org.apache.xpath.VariableStack;
import org.apache.xpath.WhitespaceStrippingElementMatcher;
import org.apache.xpath.ExpressionNode;

// TRaX imports
import javax.xml.transform.Templates;
import javax.xml.transform.SourceLocator;

// DOM Imports
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.apache.xml.dtm.DTM;

// SAX Imports
import org.xml.sax.Locator;
import javax.xml.transform.TransformerException;

import org.xml.sax.helpers.NamespaceSupport;
import org.apache.xml.utils.NamespaceSupport2;
import org.xml.sax.ContentHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import java.util.Properties;

/**
 * <meta name="usage" content="advanced"/>
 * An instance of this class represents an element inside
 * an xsl:template class.  It has a single "execute" method
 * which is expected to perform the given action on the
 * result tree.
 * This class acts like a Element node, and implements the
 * Element interface, but is not a full implementation
 * of that interface... it only implements enough for
 * basic traversal of the tree.
 *
 * @see Stylesheet
 */
public class ElemPrincipalResultDocument extends ElemTemplateElement
{
  
  /**
   * The value of the "format" attribute.
   * @serial
   */
  protected QName m_qname;
  
  protected String m_href;

  /**
   * Set the "name" attribute.
   * Both xsl:variable and xsl:param have a required name
   * attribute, which specifies the name of the variable. The
   * value of the name attribute is a QName, which is expanded
   * as described in [2.4 Qualified Names].
   * @see <a href="http://www.w3.org/TR/xslt#qname">qname in XSLT Specification</a>
   *
   * @param v Value to set for the "format" attribute.
   */
  public void setFormat(QName v)
  {
    m_qname = v;
  }

  /**
   * Get the "name" attribute.
   * Both xsl:variable and xsl:param have a required name
   * attribute, which specifies the name of the variable. The
   * value of the name attribute is a QName, which is expanded
   * as described in [2.4 Qualified Names].
   * @see <a href="http://www.w3.org/TR/xslt#qname">qname in XSLT Specification</a>
   *
   * @return Value of the "format" attribute.
   */
  public QName getFormat()
  {
    return m_qname;
  }
  
  public void setHref(String v)
  {
    m_href = v;
  }
  
  public String getHref()
  {
    return m_href;
  }
  
  private OutputProperties m_outputProperties;
  
  public void compose(StylesheetRoot sroot) throws TransformerException
  {
    //System.out.println("ElemPrincipalResultDocument.compose()-- format " + getFormat());  
    super.compose(sroot);
    m_outputProperties = sroot.getOutputComposed(getFormat());
    // set primary output properties for the stylesheet.
    sroot.setOutputProperties(m_outputProperties);
  }
 
  // runtimeInit() is called; execute() is not. Seems like a little overkill setting output props 
  // and content handler, but anything less doesn't seem to work.
  public void runtimeInit(TransformerImpl transformer) throws TransformerException
  {
    //System.out.println("ElemPrincipalResultDocument.runtimeInit()");
    transformer.setOutputFormat(m_outputProperties);
    String fileName = "primary.out";
    try
    {
      if (null != getHref())
      {
        fileName = urlToFileName(getHref());
      }
      File file = new File(fileName);
      if (!(file.isAbsolute()))
      {
        fileName = makeAbsolute(fileName, transformer);
        file = new File(fileName);
      }
      if (transformer.OutputFileAlreadyUsed(fileName)) // should never be true.
          throw new TransformerException("Output file already used -- " + fileName);
      
      String dirStr = file.getParent();
      if((null != dirStr) && (dirStr.length() > 0))
      {
        File dir = new File(dirStr);
        dir.mkdirs();
      }
      
      Result primaryResult = new StreamResult(new FileOutputStream(file));
      // Be sure to set system ID; used for setting base for secondary 
      // output documents.
      primaryResult.setSystemId(getHref());
      transformer.setOutputTarget(primaryResult);
      ContentHandler primaryHandler = transformer.createResultContentHandler
                                           (primaryResult, m_outputProperties);
      transformer.setContentHandler(primaryHandler);
    }
    catch (FileNotFoundException fe)
    {
      throw new TransformerException(fe);
    }    
  }
  
  /**
   * Based on Redirect, which questions its use!
   * In addition to purging "file" scheme (as in
   * Redirect), also adjusts the file separator.
   * Static so can be called from ElemResultDocument for
   * managing secondary output documents.
   */
  protected static String urlToFileName(String url)
  {
    String fileName = url;
      if(null != url)
    {        
      if(url.startsWith("file:////"))
        fileName = url.substring(7);
      else if(url.startsWith("file:///"))
        fileName = url.substring(6);
      else if(url.startsWith("file://"))
        fileName = url.substring(5);
      else if(url.startsWith("file:/"))
        fileName = url.substring(5);
      else if(url.startsWith("file:"))
        fileName = url.substring(4);
      
      if (File.separatorChar != '/')
        fileName = fileName.replace(File.separatorChar, '/');
    }
    return fileName;
  }
  
  protected static String makeAbsolute(String fileName, TransformerImpl transformer)
  {
    String base = "";
    Result outputTarget = transformer.getOutputTarget();
    if ( (null != outputTarget) && ((base = outputTarget.getSystemId()) != null) ) 
    {
      base = ElemPrincipalResultDocument.urlToFileName(base);
    }
    else
    {
      base = ElemPrincipalResultDocument.urlToFileName(transformer.getBaseURLOfSource());
    }
    File file = new File(base).getAbsoluteFile();
    String baseDir = file.getParent();
    return baseDir + File.separator + fileName;
  }  

}
