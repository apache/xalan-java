package org.apache.xalan.templates;

public class ElemExtensionScript extends ElemTemplateElement
{
  public ElemExtensionScript()
  {
    // System.out.println("ElemExtensionScript ctor");
  }

  private String m_lang = null;
  
  public void setLang(String v)
  {
    m_lang = v;
  }

  public String getLang()
  {
    return m_lang;
  }
  
  private String m_src = null;
  
  public void setSrc(String v)
  {
    m_src = v;
  }

  public String getSrc()
  {
    return m_src;
  }

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_EXTENSIONSCRIPT;
  }

}
