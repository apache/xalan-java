package org.apache.xpath.parser;
public class NCNameColonStar extends StarColonNCName
{
  NCNameColonStar(XPath parser)
  {
  	super(parser);
  }
  
  protected String m_namespace;
  
  public String getNamespaceURI()
  {
  	return m_namespace;
  }

  public String getLocalName()
  {
  	return org.apache.xpath.patterns.NodeTest.WILD;
  }  
  
  
  public void processToken(Token t) 
  { 
  	int posOfColon = t.image.indexOf(':');
  	String prefix = t.image.substring(0, posOfColon).trim();
  	m_namespace = m_prefixResolver.getNamespaceForPrefix(prefix);
  }

}

