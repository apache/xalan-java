package org.apache.xpath.parser;
public class StarColonNCName extends QName
{
  StarColonNCName(XPath parser)
  {
  	super(parser);
  }
	
  protected String m_localName;
  
  public String getNamespaceURI()
  {
  	return org.apache.xpath.patterns.NodeTest.WILD;
  }

  public String getLocalName()
  {
  	return m_localName;
  }  
  
  
  public void processToken(Token t) 
  { 
  	int posOfColon = t.image.indexOf(':');
  	m_localName = t.image.substring(posOfColon+1).trim();
  }

}

