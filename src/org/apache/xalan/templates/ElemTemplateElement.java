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
import org.apache.xalan.utils.UnImplNode;
import org.apache.xalan.utils.NameSpace;
import org.apache.xalan.utils.PrefixResolver;
import org.apache.xalan.utils.QName;
import org.apache.xalan.utils.StringToStringTable;

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.res.XSLMessages;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.ResultNameSpace;
import org.apache.xalan.transformer.ResultTreeHandler;

import org.apache.xpath.VariableStack;

// TRaX imports
import trax.Templates;

// Serializer imports
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Serializer;

// DOM Imports
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;

// SAX Imports
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.NamespaceSupport;

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
public class ElemTemplateElement extends UnImplNode 
  implements PrefixResolver, Serializable, Locator
{  
  /** Construct a template element instance.
   * 
   * @param transformer The XSLT Processor.
   * @param stylesheetTree The owning stylesheet.
   * @param name The name of the element.
   * @param atts The element attributes.
   * @param lineNumber The line in the XSLT file that the element occurs on.
   * @param columnNumber The column index in the XSLT file that the element occurs on.
   * @exception SAXException Never.
   */
  public ElemTemplateElement()
  {
  }
  
  /** 
   * Get an integer representation of the element type.
   * 
   * @return An integer representation of the element, defined in the 
   *     Constants class.
   * @see org.apache.xalan.templates.Constants
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_UNDEFINED;
  }
  
  /** 
   * Return the node name.
   */
  public String getNodeName()
  {
    return "Unknown XSLT Element";
  }
  
  /** Execute the element's primary function.  Subclasses of this
   * function may recursivly execute down the element tree.
   * 
   * @exception XSLProcessorException 
   * @exception java.net.MalformedURLException 
   * @exception java.io.FileNotFoundException 
   * @exception java.io.IOException 
   * @exception SAXException 
   * @param transformer The XSLT Processor.
   * @param sourceNode The current context node.
   * @param mode The current mode.
   */
  public void execute(TransformerImpl transformer, 
                      Node sourceNode,
                      QName mode)
    throws SAXException
  {
  }

  
  /**
   * Get the owning "composed" stylesheet.  This looks up the 
   * inheritance chain until it calls getStylesheetComposed
   * on a Stylesheet object, which will Get the owning 
   * aggregated stylesheet, or that stylesheet if it is aggregated.
   */
  public StylesheetComposed getStylesheetComposed()
  {
    return m_parentNode.getStylesheetComposed();
  }

  /**
   * Get the owning stylesheet.  This looks up the 
   * inheritance chain until it calls getStylesheet
   * on a Stylesheet object, which will return itself.
   */
  public Stylesheet getStylesheet()
  {
    return m_parentNode.getStylesheet();
  }

  /**
   * Get the owning root stylesheet.  This looks up the 
   * inheritance chain until it calls StylesheetRoot
   * on a Stylesheet object, which will return a reference 
   * to the root stylesheet.
   */
  public StylesheetRoot getStylesheetRoot()
  {
    return m_parentNode.getStylesheetRoot();
  } 
  
  /** 
   * Validate that the string is an NCName.
   * 
   * @param s The name in question.
   * @return True if the string is a valid NCName according to XML rules.
   * @see <a href="http://www.w3.org/TR/REC-xml-names#NT-NCName">XXX in XSLT Specification</a>
   */
  protected boolean isValidNCName(String s)
  {
    int len = s.length();
    char c = s.charAt(0);
    if(!(Character.isLetter(c) || (c == '_')))
      return false;
    if(len > 0)
    {
      for(int i = 1; i < len; i++)
      {
        c = s.charAt(i);
        if(!(Character.isLetterOrDigit(c) || (c == '_') || (c == '-') || (c == '.')))
          return false;
      }
    }
    return true;
  }
    
  /** 
   * Throw a template element runtime error.  (Note: should we throw a SAXException instead?)
   * 
   * @param msg Description of the error that occured.
   */
  public void error(int msg, Object[] args)
  {
    String themsg = XSLMessages.createMessage(msg, args);  
    throw new RuntimeException(XSLMessages.createMessage(XSLTErrorResources.ER_ELEMTEMPLATEELEM_ERR, new Object[] {themsg})); //"ElemTemplateElement error: "+msg);
  }
  
  // Implemented DOM Element methods.
  
  /** 
   * Add a child to the child list.
   * 
   * @exception DOMException 
   * @param newChild 
   */
  public Node               appendChild(Node newChild)
    throws DOMException
  {
    if(null == newChild)
    {
      error(XSLTErrorResources.ER_NULL_CHILD, null); //"Trying to add a null child!");
    }
    ElemTemplateElement elem = (ElemTemplateElement)newChild;
    if(null == m_firstChild)
    {
      m_firstChild = elem;
    }
    else
    {
      ElemTemplateElement last = (ElemTemplateElement)getLastChild();
      last.m_nextSibling = elem;
    }
    elem.m_parentNode = this;
    
    return newChild;
  }
  
  /** 
   * Tell if there are child nodes.
   */
  public boolean            hasChildNodes()
  {
    return (null != m_firstChild);
  }
  
  /** 
   * Get the type of the node.
   */
  public short              getNodeType()
  {
    return Node.ELEMENT_NODE;
  }
    
  /** Return the nodelist (same reference).
   */
  public NodeList           getChildNodes()
  {
    return this;
  }
  
  
  /** Replace the old child with a new child. */
  public Node               replaceChild(Node newChild,
                                         Node oldChild)
    throws DOMException
  {
    ElemTemplateElement node = (ElemTemplateElement)getFirstChild();
    while(null != node)
    {
      if(node == oldChild)
      {
        ElemTemplateElement newChildElem 
          = ((ElemTemplateElement)newChild);
        ElemTemplateElement oldChildElem 
          = ((ElemTemplateElement)oldChild);

        // Fix up previous sibling.
        ElemTemplateElement prev 
          = (ElemTemplateElement)oldChildElem.getPreviousSibling();
        if(null != prev)
          prev.m_nextSibling = newChildElem;

        // Fix up parent.
        if(newChildElem.m_parentNode.m_firstChild == oldChildElem)
          newChildElem.m_parentNode.m_firstChild = newChildElem;

        newChildElem.m_parentNode = oldChildElem.m_parentNode;
        oldChildElem.m_parentNode = null;
        
        newChildElem.m_nextSibling = oldChildElem.m_nextSibling;
        oldChildElem.m_nextSibling = null;

        // newChildElem.m_stylesheet = oldChildElem.m_stylesheet;
        // oldChildElem.m_stylesheet = null;
        
        return newChildElem;
      }
      node = (ElemTemplateElement)node.getNextSibling();
    }
    return null;
  }
  
  /** 
   * NodeList method: Count the immediate children of this node
   * 
   * @return int
   */
  public int getLength() 
  {

    // It is assumed that the getChildNodes call synchronized
    // the children. Therefore, we can access the first child
    // reference directly.
    int count = 0;
    for (ElemTemplateElement node = m_firstChild; node != null; node = node.m_nextSibling) 
    {
      count++;
    }
    return count;

  } // getLength():int
  
  /** 
   * NodeList method: Return the Nth immediate child of this node, or
   * null if the index is out of bounds.
   * 
   * @param index 
   * @return org.w3c.dom.Node
   */
  public Node item(int index) 
  {
    // It is assumed that the getChildNodes call synchronized
    // the children. Therefore, we can access the first child
    // reference directly.
    ElemTemplateElement node = m_firstChild;
    for (int i = 0; i < index && node != null; i++) 
    {
      node = node.m_nextSibling;
    }
    return node;

  } // item(int):Node
  
  /** Get the stylesheet owner.
   */
  public Document           getOwnerDocument()
  {
    return getStylesheet();
  }
  
  /** Return the element name.
   */
  public String getTagName()
  {
    return getNodeName();
  }
    
  /** Return the base identifier.
   */
  public String getBaseIdentifier()
  {
    // Should this always be absolute?
    return this.getSystemId();
  }
  
  private int m_lineNumber;

  /**
   * Return the line number where the current document event ends.
   * Note that this is the line position of the first character
   * after the text associated with the document event.
   * @return The line number, or -1 if none is available.
   * @see #getColumnNumber
   */
  public int getLineNumber ()
  {
    return m_lineNumber;
  }
  
  
  private int m_columnNumber;

  /**
   * Return the column number where the current document event ends.
   * Note that this is the column number of the first
   * character after the text associated with the document
   * event.  The first column in a line is position 1.
   * @return The column number, or -1 if none is available.
   * @see #getLineNumber
   */
  public int getColumnNumber ()
  {
    return m_columnNumber;
  }
  
  /**
   * Return the public identifier for the current document event.
   * <p>This will be the public identifier
   * @return A string containing the public identifier, or
   *         null if none is available.
   * @see #getSystemId
   */
  public String getPublicId ()
  {
    return (null != m_parentNode) ? m_parentNode.getPublicId() : null;
  }
  
  /**
   * Return the system identifier for the current document event.
   *
   * <p>If the system identifier is a URL, the parser must resolve it
   * fully before passing it to the application.</p>
   *
   * @return A string containing the system identifier, or null
   *         if none is available.
   * @see #getPublicId
   */
  public String getSystemId ()
  {
    return (null != m_parentNode) ? m_parentNode.getSystemId() : null;
    // return m_parentNode.getSystemId();
  }

  
  /**
   * Set the location information for this element.
   */
  public void setLocaterInfo(Locator locator)
  {
    m_lineNumber = locator.getLineNumber();
    m_columnNumber = locator.getColumnNumber();
  }

  
  /** 
   * Tell if this element has the default space handling
   * turned off or on according to the xml:space attribute.
   * @serial
   */
  private boolean m_defaultSpace = true;
  
  /**
   * Set the "xml:space" attribute. 
   * A text node is preserved if an ancestor element of the text node 
   * has an xml:space attribute with a value of preserve, and 
   * no closer ancestor element has xml:space with a value of default.
   * @see <a href="http://www.w3.org/TR/xslt#strip">strip in XSLT Specification</a>
   * @see <a href="http://www.w3.org/TR/xslt#section-Creating-Text">section-Creating-Text in XSLT Specification</a>
   */
  public void setXmlSpace(boolean v)
  {
    m_defaultSpace = v;
  }

  /**
   * Get the "xml:space" attribute. 
   * A text node is preserved if an ancestor element of the text node 
   * has an xml:space attribute with a value of preserve, and 
   * no closer ancestor element has xml:space with a value of default.
   * @see <a href="http://www.w3.org/TR/xslt#strip">strip in XSLT Specification</a>
   * @see <a href="http://www.w3.org/TR/xslt#section-Creating-Text">section-Creating-Text in XSLT Specification</a>
   */
  public boolean getXmlSpace()
  {
    return m_defaultSpace;
  }
  
  /** 
   * The list of namespace declarations for this element only.
   * @serial
   */
  private Vector m_declaredPrefixes;
  
  /**
   * Return a table that contains all prefixes available 
   * within this element context.
   */
  public Vector getDeclaredPrefixes()
  {
    return m_declaredPrefixes;
  }
  
  /**
   * From the SAX2 helper class, set the namespace table for 
   * this element.  Take care to call resolveInheritedNamespaceDecls.
   * after all namespace declarations have been added.
   */
  public void setPrefixes(NamespaceSupport nsSupport)
    throws SAXException
  {
    Enumeration decls = nsSupport.getDeclaredPrefixes ();
    while(decls.hasMoreElements())
    {
      String prefix = (String)decls.nextElement();
      if(null == m_declaredPrefixes)
        m_declaredPrefixes = new Vector();
      String uri = nsSupport.getURI(prefix);
      // System.out.println("setPrefixes - "+prefix+", "+uri);
      XMLNSDecl decl = new XMLNSDecl(prefix, uri, false);
      m_declaredPrefixes.addElement(decl);
    }
  }
  
  /** 
   * Fullfill the PrefixResolver interface.  Calling this will throw an error.
   */
  public String getNamespaceForPrefix(String prefix, org.w3c.dom.Node context)
  {
    this.error(XSLTErrorResources.ER_CANT_RESOLVE_NSPREFIX, null);
    return null;
  }
  
  /** 
   * Given a namespace, get the corrisponding prefix.
   */
  public String getNamespaceForPrefix(String prefix)
  {
    ElemTemplateElement elem = this;
    while(null != elem)
    {
      Vector nsDecls = elem.m_declaredPrefixes;
      if(null != nsDecls)
      {
        int n = nsDecls.size();
        for(int i = 0; i < n; i++)
        {
          XMLNSDecl decl = (XMLNSDecl)nsDecls.elementAt(i);
          if(prefix.equals(decl.getPrefix()))
            return decl.getURI();
        }
      }
      elem = elem.m_parentNode;
    }

    return null;
  }

  /** 
   * The table of namespace declarations for this element 
   * and all parent elements, screened for excluded prefixes.
   * @serial
   */
  Vector m_prefixTable;

  /**
   * Return a table that contains all prefixes available 
   * within this element context.
   */
  public Vector getPrefixes()
  {
    return m_prefixTable;
  }
  
  /**
   * Tell if the result namespace decl should be excluded.  Should be called before 
   * namespace aliasing (I think).
   */
  private boolean excludeResultNSDecl(String prefix, String uri)
    throws SAXException
  {
    if(uri.equals(Constants.S_XSLNAMESPACEURL)
       || getStylesheet().containsExtensionElementURI(uri)
       || uri.equals("http://xml.apache.org/xslt")
       || uri.equals("http://xsl.lotus.com/")
       || uri.equals("http://xsl.lotus.com"))
      return true; 
    
    if(getStylesheet().containsExcludeResultPrefix(prefix))
      return true;
    
    return false;
  }

    
  /**
   * Combine the parent's namespaces with this namespace 
   * for fast processing, taking care to reference the 
   * parent's namespace if this namespace adds nothing new.
   * (Recursive method, walking the elements depth-first, 
   * processing parents before children).
   */
  public void resolvePrefixTables()
    throws SAXException
  {
    // Always start with a fresh prefix table!
    m_prefixTable = null;
    
    // If we have declared declarations, then we look for 
    // a parent that has namespace decls, and add them 
    // to this element's decls.  Otherwise we just point 
    // to the parent that has decls.
    if(null != this.m_declaredPrefixes)
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
      if(null == m_prefixTable)
      {
        // Nothing to combine, so just use parent's table!
        this.m_prefixTable = parent.m_prefixTable;
      }
      else
      {
        // Add the prefixes from the parent's prefix table.
        int n = prefixes.size();
        for(int i = 0; i < n; i++)
        {
          XMLNSDecl decl = (XMLNSDecl)prefixes.elementAt(i);
          boolean shouldExclude 
            = excludeResultNSDecl(decl.getPrefix(), decl.getURI());
          if(shouldExclude != decl.getIsExcluded())
          {
            decl = new XMLNSDecl(decl.getPrefix(), decl.getURI(), shouldExclude);
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
   * Send startPrefixMapping events to the result tree handler 
   * for all declared prefix mappings in the stylesheet.
   */
  void executeNSDecls(TransformerImpl transformer)
    throws SAXException
  {
    ResultTreeHandler rhandler = transformer.getResultTreeHandler();
    int n = m_prefixTable.size();
    for(int i = 0; i < n; i++)
    {
      XMLNSDecl decl = (XMLNSDecl)m_prefixTable.elementAt(i);
      if(!decl.getIsExcluded())
      {
        rhandler.startPrefixMapping(decl.getPrefix(), decl.getURI());
      }
    }    
  }
    
  /** 
   * Parent node.
   * @serial
   */
  protected ElemTemplateElement m_parentNode;

  /** 
   * Get the parent as a Node.
   */
  public Node getParentNode()
  {
    return m_parentNode;
  }

  /** 
   * Get the parent as a Node.
   */
  public ElemTemplateElement getParentElem()
  {
    return m_parentNode;
  }

  /** 
   * Next sibling.
   * @serial
   */
  protected ElemTemplateElement m_nextSibling;
  
  /** 
   * Get the next sibling (as a Node) or return null.
   */
  public Node getNextSibling()
  {
    return m_nextSibling;
  }

  /** 
   * Get the next sibling (as a ElemTemplateElement) or return null.
   */
  public ElemTemplateElement getNextSiblingElem()
  {
    return m_nextSibling;
  }
  
  /** 
   * First child.
   * @serial
   */
  protected ElemTemplateElement m_firstChild;
  
  /** 
   * Get the first child as a Node.
   */
  public Node getFirstChild()
  {
    return m_firstChild;
  }
  
  /** 
   * Get the first child as a ElemTemplateElement.
   */
  public ElemTemplateElement getFirstChildElem()
  {
    return m_firstChild;
  }
  
  /** Get the last child.
   */
  public Node               getLastChild()
  {
    ElemTemplateElement lastChild = null;
    for (ElemTemplateElement node = m_firstChild; 
         node != null; node = node.m_nextSibling) 
    {
      lastChild = node;
    }
    return lastChild;
  }
  
  private Node m_DOMBackPointer;
  
  /**
   * If this stylesheet was created from a DOM, get the 
   * DOM backpointer that this element originated from.
   * For tooling use.
   */
  public Node getDOMBackPointer()
  {
    return m_DOMBackPointer;
  }

  /**
   * If this stylesheet was created from a DOM, set the 
   * DOM backpointer that this element originated from.
   * For tooling use.
   */
  public void setDOMBackPointer(Node n)
  {
    m_DOMBackPointer = n;
  }
  
}
