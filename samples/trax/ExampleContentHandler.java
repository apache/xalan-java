import org.xml.sax.ContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;

public class ExampleContentHandler implements ContentHandler
{
  public void setDocumentLocator (Locator locator)
  {
    System.out.println("setDocumentLocator");
  }


  public void startDocument ()
    throws SAXException
  {
    System.out.println("startDocument");
  }


  public void endDocument()
    throws SAXException
  {
    System.out.println("endDocument");
  }


  public void startPrefixMapping (String prefix, String uri)
    throws SAXException
  {
    System.out.println("startPrefixMapping: "+prefix+", "+uri);
  }


  public void endPrefixMapping (String prefix)
    throws SAXException
  {
    System.out.println("endPrefixMapping: "+prefix);
  }


  public void startElement (String namespaceURI, String localName,
                            String qName, Attributes atts)
    throws SAXException
  {
    System.out.print("startElement: "+namespaceURI+", "+namespaceURI+
                       ", "+qName);
    int n = atts.getLength();
    for(int i = 0; i < n; i++)
    {
      System.out.print(", "+atts.getQName(i));
    }
    System.out.println("");
  }


  public void endElement (String namespaceURI, String localName,
                          String qName)
    throws SAXException
  {
    System.out.println("endElement: "+namespaceURI+", "+namespaceURI+
                       ", "+qName);
  }


  public void characters (char ch[], int start, int length)
    throws SAXException
  {
    String s = new String(ch, start, (length > 30) ? 30 : length);
    if(length > 30)
      System.out.println("characters: \""+s+"\"...");
    else
      System.out.println("characters: \""+s+"\"");
  }


  public void ignorableWhitespace (char ch[], int start, int length)
    throws SAXException
  {
    System.out.println("ignorableWhitespace");
  }


  public void processingInstruction (String target, String data)
    throws SAXException
  {
    System.out.println("processingInstruction: "+target+", "+target);
  }


  public void skippedEntity (String name)
    throws SAXException
  {
    System.out.println("skippedEntity: "+name);
  }

}
