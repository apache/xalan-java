package org.apache.xpath.parser;

/**
 * Represents a "/" or "//" node that does not appear at the head of a path expression.  This node 
 * is not executable and exists for construction purposes only.
 * 
 * Created Jul 11, 2002
 * @author sboag
 */
public class SlashOrSlashSlash extends NonExecutableExpression
{
  /** true if this is a "//" step. */
  boolean m_isSlashSlash;

  /**
   * Construct a SlashOrSlashSlash node.
   * @param isSlashSlash true if this is a "//" step.
   * @see org.apache.xpath.parser.NonExecutableExpression#NonExecutableExpression(XPath)
   */
  public SlashOrSlashSlash(XPath parser)
  {
    super(parser);
  }

  /**
   * Construct a SlashOrSlashSlash node.
   * @param isSlashSlash true if this is a "//" step.
   * @param parser The XPath parser that is creating this node.
   */
  SlashOrSlashSlash(boolean isSlashSlash, XPath parser)
  {
    super(parser);
    m_isSlashSlash = isSlashSlash;
  }
  
  /**
   * Returns the isSlashSlash property.
   * @return boolean true if this is a "//" step.
   */
  public boolean getisSlashSlash()
  {
    return m_isSlashSlash;
  }

  
  /**
   * Returns the isSlashSlash property.
   * @return boolean true if this is a "//" step.
   */
  public boolean isSlashSlash()
  {
    return m_isSlashSlash;
  }

  /**
   * Sets the isSlashSlash.
   * @param isSlashSlash The isSlashSlash to set
   */
  public void setIsSlashSlash(boolean isSlashSlash)
  {
    m_isSlashSlash = isSlashSlash;
  }

}

