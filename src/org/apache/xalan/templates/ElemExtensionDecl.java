package org.apache.xalan.templates;

import java.util.Vector;
import org.apache.xalan.utils.QName;
import org.apache.xalan.utils.NameSpace;
import org.apache.xalan.utils.StringToStringTable;
import org.apache.xalan.utils.StringVector;
import org.apache.xalan.extensions.ExtensionHandler;
import org.apache.xalan.extensions.ExtensionHandlerGeneral;
import org.apache.xalan.extensions.ExtensionHandlerJavaClass;
import org.apache.xalan.extensions.ExtensionHandlerJavaPackage;
import org.apache.xalan.extensions.ExtensionsTable;
import org.apache.xalan.transformer.TransformerImpl;
import org.xml.sax.SAXException;
import org.apache.xpath.XPathContext;
import org.apache.xalan.res.XSLTErrorResources;

public class ElemExtensionDecl extends ElemTemplateElement
{
  public ElemExtensionDecl()
  {
    // System.out.println("ElemExtensionDecl ctor");
  }
  
  private String m_prefix = null;
  
  public void setPrefix(String v)
  {
    m_prefix = v;
  }

  public String getPrefix()
  {
    return m_prefix;
  }

  private StringVector m_functions = new StringVector();
  
  public void setFunctions(StringVector v)
  {
    m_functions = v;
  }

  public StringVector getFunctions()
  {
    return m_functions;
  }
  
  public String getFunction(int i)
    throws ArrayIndexOutOfBoundsException
  {
    if(null == m_functions)
      throw new ArrayIndexOutOfBoundsException();
    return (String)m_functions.elementAt(i);
  }

  public int getFunctionCount()
  {
    return (null != m_functions) 
           ? m_functions.size() : 0;
  }

  
  private StringVector m_elements = null;
  
  public void setElements(StringVector v)
  {
    m_elements = v;
  }

  public StringVector getElements()
  {
    return m_elements;
  }
  
  public String getElement(int i)
    throws ArrayIndexOutOfBoundsException
  {
    if(null == m_elements)
      throw new ArrayIndexOutOfBoundsException();
    return (String)m_elements.elementAt(i);
  }

  public int getElementCount()
  {
    return (null != m_elements) 
           ? m_elements.size() : 0;
  }

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_EXTENSIONDECL;
  }
  
  /** 
   * This function will be called on top-level elements 
   * only, just before the transform begins.
   * 
   * @param transformer The XSLT Processor.
   */
  public void runtimeInit(TransformerImpl transformer)
    throws SAXException
  {
    String lang = null;
    String srcURL = null;
    String scriptSrc = null;
    String prefix = getPrefix();
    String declNamespace = getNamespaceForPrefix(prefix);
    for(ElemTemplateElement child = getFirstChildElem();
        child != null; child = child.getNextSiblingElem())
    {
      if(Constants.ELEMNAME_EXTENSIONSCRIPT == child.getXSLToken())
      {
        ElemExtensionScript sdecl = (ElemExtensionScript)child;
        lang = sdecl.getLang();
        srcURL = sdecl.getSrc();
        ElemTemplateElement childOfSDecl = sdecl.getFirstChildElem();
        if(null != childOfSDecl)
        {
          if(Constants.ELEMNAME_TEXTLITERALRESULT == childOfSDecl.getXSLToken())
          {
            ElemTextLiteral tl = (ElemTextLiteral)childOfSDecl;
            char[] chars = tl.getChars();
            scriptSrc = new String(chars);
            if(scriptSrc.trim().length() == 0)
              scriptSrc = null;
          }
        }
      }
    }
    if(null == lang)
      lang = "javaclass";

    if ( ("javaclass" == lang) && (scriptSrc != null) )
      throw new SAXException("Element content not allowed for lang=javaclass " + scriptSrc);

    XPathContext liaison = ((XPathContext)transformer.getXPathContext());
    ExtensionsTable etable = liaison.getExtensionsTable();
    ExtensionHandler nsh = etable.get(declNamespace);

    // If we have no prior ExtensionHandler for this namespace, we need to
    // create one.
    // If the script element is for javaclass, this is our special compiled java.
    // Element content is not supported for this so we throw an exception if
    // it is provided.  Otherwise, we look up the srcURL to see if we already have
    // an ExtensionHandler.

    if(null == nsh)
    {
      if (lang.equals("javaclass")) {
        nsh = etable.get(srcURL);
        if (null == nsh) {
          nsh = etable.makeJavaNamespace(srcURL);
        }
      }
      else     // not java
      {
        nsh = new ExtensionHandlerGeneral(declNamespace,
                                          this.m_elements,
                                          this.m_functions,
                                          lang,
                                          srcURL,
                                          scriptSrc);
        // System.out.println("Adding NS Handler: declNamespace = "+
        //                   declNamespace+", lang = "+lang+", srcURL = "+
        //                   srcURL+", scriptSrc="+scriptSrc);
      }

      etable.addExtensionNamespace (declNamespace, nsh);

    }
  }
}
