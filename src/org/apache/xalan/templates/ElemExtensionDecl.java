package org.apache.xalan.templates;

import java.util.Vector;
import org.apache.xalan.utils.QName;
import org.apache.xalan.utils.NameSpace;
import org.apache.xalan.utils.StringToStringTable;
import org.apache.xalan.extensions.ExtensionNSHandler;
import org.apache.xalan.extensions.ExtensionsTable;
import org.apache.xalan.transformer.TransformerImpl;
import org.xml.sax.SAXException;
import org.apache.xpath.XPathContext;
import org.apache.xalan.res.XSLTErrorResources;

public class ElemExtensionDecl extends ElemTemplateElement
{
  public ElemExtensionDecl()
  {
    System.out.println("ElemExtensionDecl ctor");
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

  private Vector m_functions = new Vector();
  
  public void setFunctions(Vector v)
  {
    m_functions = v;
  }

  public Vector getFunctions()
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

  
  private Vector m_elements = null;
  
  public void setElements(Vector v)
  {
    m_elements = v;
  }

  public Vector getElements()
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
          }
        }
        break;
      }
      XPathContext liaison = ((XPathContext)transformer.getXPathContext());
      ExtensionsTable etable = liaison.getExtensionsTable();
      ExtensionNSHandler nsh = etable.get(declNamespace);

      if(null == nsh)
      {
        nsh = new ExtensionNSHandler (declNamespace);
        nsh.setScript (lang, srcURL, scriptSrc);
        etable.addExtensionElementNamespace(declNamespace, nsh);
      }
    }
  }


}
