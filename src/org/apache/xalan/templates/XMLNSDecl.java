package org.apache.xalan.templates;

/**
 * Represents an xmlns declaration
 */
public class XMLNSDecl
{
  
  public XMLNSDecl(String prefix, String uri, boolean isExcluded)
  {
    m_prefix = prefix;
    m_uri = uri;
    m_isExcluded = isExcluded;
  }
  
  private String m_prefix;

  /**
   * Return the prefix.
   * @return The prefix that is associated with this URI, or null 
   * if the XMLNSDecl is declaring the default namespace.
   */
  public String getPrefix ()
  {
    return m_prefix;
  }
  
  private String m_uri;

  /**
   * Return the URI.
   * @return The URI that is associated with this declaration.
   */
  public String getURI ()
  {
    return m_uri;
  }
  
  private boolean m_isExcluded;
  
  /**
   * Tell if this declaration should be excluded from the 
   * result namespace.
   */
  public boolean getIsExcluded()
  {
    return m_isExcluded;
  }

}
