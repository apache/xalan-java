package org.apache.xpath.parser;

import java.util.Vector;

import javax.xml.transform.TransformerException;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.XObject;

/**
 * This is an expression node that only exists for construction 
 * purposes. 
 */
public class QName extends NonExecutableExpression
{
  protected PrefixResolver m_prefixResolver;

  QName(XPath parser)
  {
    super(parser);
    m_prefixResolver = parser.m_prefixResolver;
  }

  protected org.apache.xml.utils.QName m_qname;

  public org.apache.xml.utils.QName getQName()
  {
    return m_qname;
  }

  public String getNamespaceURI()
  {
    return m_qname.getNamespaceURI();
  }

  public String getLocalName()
  {
    return m_qname.getLocalName();
  }

  /**
   * @see org.apache.xpath.parser.SimpleNode#processToken(Token)
   */
  public void processToken(Token t)
  {
    if (null == m_qname)
      // We might have to call this earlier than natural, so avoid duplicate processing.
    {
      String qname = t.image;
      int parenIndex = qname.lastIndexOf("(");
      if (parenIndex > 0)
      {
        qname = qname.substring(0, qname.lastIndexOf("("));
      }
      try
      {
        qname = qname.trim();
        m_qname = new org.apache.xml.utils.QName(qname, m_prefixResolver);
      }
      catch (RuntimeException e)
      {
        throw e;
      }
    }
  }

}
