package org.apache.xalan.processor;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.Attributes;
import java.util.Vector;
import java.util.StringTokenizer;
import org.apache.xalan.utils.SystemIDResolver;

/**
 * Handle the xml-stylesheet processing instruction.
 * @see <a href="http://www.w3.org/TR/xml-stylesheet/">Associating Style Sheets with XML documents, Version 1.0</a>
 */
public class StylesheetPIHandler extends DefaultHandler
{
  static final String STARTELEM_FOUND_MSG = "##startElement found";
  
  InputSource m_source;
  String m_media; 
  String m_title;
  String m_charset;
  Vector m_stylesheets = new Vector();
  
  /**
   * Construct a StylesheetPIHandler instance.
   */
  public StylesheetPIHandler(InputSource source,
                             String media, 
                             String title,
                             String charset)
  {
    m_source = source;
    m_media = media;
    m_title = title;
    m_charset = charset;
  }
  
  /**
   * Return all stylesheets found that match the constraints.
   */
  public InputSource[] getAssociatedStylesheets()
  {
    int sz = m_stylesheets.size();
    if(sz > 0)
    {
      InputSource[] inputs = new InputSource[sz];
      for(int i = 0; i < sz; i++)
      {
        inputs[i] = (InputSource)m_stylesheets.elementAt(i);
      }
      return inputs;
    }
    else
      return null;
  }
  
  /**
   * Handle the xml-stylesheet processing instruction.
   *
   * @param target The processing instruction target.
   * @param data The processing instruction data, or null if
   *             none is supplied.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#processingInstruction
   * @see <a href="http://www.w3.org/TR/xml-stylesheet/">Associating Style Sheets with XML documents, Version 1.0</a>
   */
  public void processingInstruction (String target, String data)
    throws SAXException
  {
    if(target.equals("xml-stylesheet"))
    {
      String href = null; // CDATA #REQUIRED
      String type = null; // CDATA #REQUIRED
      String title = null; // CDATA #IMPLIED
      String media = null; // CDATA #IMPLIED
      String charset = null; // CDATA #IMPLIED
      boolean alternate = false; // (yes|no) "no"

      StringTokenizer tokenizer = new StringTokenizer(data, " \t=");
      while(tokenizer.hasMoreTokens())
      {
        String name = tokenizer.nextToken();
        if(name.equals("type"))
        {
          String typeVal = tokenizer.nextToken();
          type = typeVal.substring(1, typeVal.length()-1);
        }
        else if(name.equals("href"))
        {
          href = tokenizer.nextToken();
          href = href.substring(1, href.length()-1);
          href = SystemIDResolver.getAbsoluteURI(href, m_source.getSystemId());
        }
        else if(name.equals("title"))
        {
          title = tokenizer.nextToken();
          title = title.substring(1, title.length()-1);
        }
        else if(name.equals("media"))
        {
          media = tokenizer.nextToken();
          media = media.substring(1, media.length()-1);
        }
        else if(name.equals("charset"))
        {
          charset = tokenizer.nextToken();
          charset = charset.substring(1, charset.length()-1);
        }
        else if(name.equals("alternate"))
        {
          String alternateStr = tokenizer.nextToken();
          alternate = alternateStr.substring(1, alternateStr.length()-1).equals("yes");
        }
      }

      if((null != type) && type.equals("text/xsl") && (null != href))
      {
        if(null != m_media)
        {
          if(null != media)
          {
            if(!media.equals(m_media))
              return;
          }
          else
            return;
        }
        if(null != m_charset)
        {
          if(null != charset)
          {
            if(!charset.equals(m_charset))
              return;
          }
          else
            return;
        }
        if(null != m_title)
        {
          if(null != title)
          {
            if(!title.equals(m_title))
              return;
          }
          else
            return;
        }
        m_stylesheets.addElement(new InputSource(href));
      }
    }
  }
  
  /**
   * The spec notes that "The xml-stylesheet processing instruction is allowed only in the prolog of an XML document.",
   * so, at least for right now, I'm going to go ahead an throw a SAXException
   * in order to stop the parse.
   */
  public void startElement (String namespaceURI, String localName,
                            String qName, Attributes atts)
    throws SAXException
  {
    throw new SAXException(STARTELEM_FOUND_MSG);
  }

}
